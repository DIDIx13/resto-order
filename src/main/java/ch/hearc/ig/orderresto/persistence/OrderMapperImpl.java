package ch.hearc.ig.orderresto.persistence;

import ch.hearc.ig.orderresto.business.Order;
import ch.hearc.ig.orderresto.business.Product;
import ch.hearc.ig.orderresto.business.Customer;
import ch.hearc.ig.orderresto.business.Restaurant;
import ch.hearc.ig.orderresto.persistence.config.DatabaseManager;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class OrderMapperImpl implements OrderMapper {

    @Override
    public void addOrder(Order order) {
        String sqlGetOrderId = "SELECT SEQ_COMMANDE.NEXTVAL FROM DUAL"; // Ottiene un nuovo ID dall'oggetto sequenza
        String sqlOrder = "INSERT INTO COMMANDE (numero, fk_client, fk_resto, a_emporter, quand) VALUES (?, ?, ?, ?, ?)";
        String sqlProductOrder = "INSERT INTO PRODUIT_COMMANDE (fk_commande, fk_produit) VALUES (?, ?)";

        try (Connection conn = DatabaseManager.getConnection()) {
            conn.setAutoCommit(false); // Avvia una transazione

            // Ottieni un nuovo ID per l'ordine dalla sequenza
            try (PreparedStatement pstmtGetOrderId = conn.prepareStatement(sqlGetOrderId);
                 ResultSet rs = pstmtGetOrderId.executeQuery()) {
                if (rs.next()) {
                    order.setId(rs.getLong(1)); // Imposta l'ID dell'ordine con il valore della sequenza
                } else {
                    throw new SQLException("Failed to retrieve order ID from sequence.");
                }
            }

            // Inserimento dell'ordine nella tabella COMMANDE
            try (PreparedStatement pstmtOrder = conn.prepareStatement(sqlOrder)) {
                pstmtOrder.setLong(1, order.getId()); // Usa l'ID ottenuto dalla sequenza
                pstmtOrder.setLong(2, order.getCustomer().getId());
                pstmtOrder.setLong(3, order.getRestaurant().getId());
                pstmtOrder.setString(4, order.getTakeAway() ? "O" : "N");
                pstmtOrder.setTimestamp(5, Timestamp.valueOf(order.getWhen()));

                int affectedRows = pstmtOrder.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Failed to add order, no rows affected.");
                }
            }

            // Inserimento dei prodotti associati a questo ordine in PRODUIT_COMMANDE
            try (PreparedStatement pstmtProductOrder = conn.prepareStatement(sqlProductOrder)) {
                for (Product product : order.getProducts()) {
                    pstmtProductOrder.setLong(1, order.getId()); // fk_commande
                    pstmtProductOrder.setLong(2, product.getId()); // fk_produit
                    pstmtProductOrder.addBatch(); // Usa il batch per ottimizzare l'inserimento
                }
                pstmtProductOrder.executeBatch(); // Esegue il batch per inserire tutte le associazioni
            }

            conn.commit(); // Conferma la transazione

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void removeOrder(Order order) {
        String sql = "DELETE FROM COMMANDE WHERE numero = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, order.getId());
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateOrder(Order order) {
        String sql = "UPDATE COMMANDE SET fk_client = ?, fk_resto = ?, a_emporter = ?, quand = ? WHERE numero = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, order.getCustomer().getId());
            pstmt.setLong(2, order.getRestaurant().getId());
            pstmt.setString(3, order.getTakeAway() ? "O" : "N");
            pstmt.setTimestamp(4, Timestamp.valueOf(order.getWhen()));
            pstmt.setLong(5, order.getId());

            pstmt.executeUpdate();

            // Rimuovi i vecchi prodotti associati all'ordine e inserisci i nuovi
            String deleteOrderProductsSql = "DELETE FROM PRODUIT_COMMANDE WHERE fk_commande = ?";
            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteOrderProductsSql)) {
                deleteStmt.setLong(1, order.getId());
                deleteStmt.executeUpdate();
            }

            String orderProductSql = "INSERT INTO PRODUIT_COMMANDE (fk_commande, fk_produit) VALUES (?, ?)";
            try (PreparedStatement orderProductStmt = conn.prepareStatement(orderProductSql)) {
                for (Product product : order.getProducts()) {
                    orderProductStmt.setLong(1, order.getId());
                    orderProductStmt.setLong(2, product.getId());
                    orderProductStmt.addBatch();
                }
                orderProductStmt.executeBatch();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Order findOrderById(Long id) {
        String sql = "SELECT * FROM COMMANDE WHERE numero = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapToOrder(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Set<Order> findOrdersByCustomerId(Long customerId) {
        Set<Order> orders = new HashSet<>();
        String sql = "SELECT * FROM COMMANDE WHERE fk_client = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, customerId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    orders.add(mapToOrder(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;  // Restituisce un insieme vuoto se non ci sono ordini, evitando NullPointerException
    }

    @Override
    public Set<Order> findAllOrders() {
        Set<Order> orders = new HashSet<>();
        String sql = "SELECT * FROM COMMANDE";
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                orders.add(mapToOrder(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }

    private Order mapToOrder(ResultSet rs) throws SQLException {
        CustomerMapper customerMapper = new CustomerMapperImpl();
        RestaurantMapper restaurantMapper = new RestaurantMapperImpl();
        ProductMapper productMapper = new ProductMapperImpl();

        Long customerId = rs.getLong("fk_client");
        Long restaurantId = rs.getLong("fk_resto");

        Customer customer = customerMapper.findCustomerById(customerId);
        Restaurant restaurant = restaurantMapper.findRestaurantById(restaurantId);
        Boolean takeAway = "O".equals(rs.getString("a_emporter"));
        LocalDateTime when = rs.getTimestamp("quand").toLocalDateTime();

        Order order = new Order(rs.getLong("numero"), customer, restaurant, takeAway, when);

        // Recupera i prodotti associati all'ordine
        Set<Product> products = productMapper.findProductsByOrderId(order.getId());
        BigDecimal totalAmount = BigDecimal.ZERO;  // Inizializza il totale a zero
        for (Product product : products) {
            order.addProduct(product);  // Aggiunge il prodotto all'ordine
            totalAmount = totalAmount.add(product.getUnitPrice());  // Somma il prezzo unitario al totale
        }

        order.setTotalAmount(totalAmount);  // Imposta il totale calcolato nell'ordine
        return order;
    }
}







