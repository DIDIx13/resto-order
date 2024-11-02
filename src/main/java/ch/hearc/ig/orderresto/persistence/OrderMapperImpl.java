package ch.hearc.ig.orderresto.persistence;

import ch.hearc.ig.orderresto.business.Order;
import ch.hearc.ig.orderresto.business.Product;
import ch.hearc.ig.orderresto.business.Customer;
import ch.hearc.ig.orderresto.business.Restaurant;
import ch.hearc.ig.orderresto.persistence.config.DatabaseManager;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;

public class OrderMapperImpl implements OrderMapper {

    @Override
    public void addOrder(Order order) {
        String sql = "INSERT INTO COMMANDE (fk_client, fk_resto, a_emporter, quand) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setLong(1, order.getCustomer().getId());
            pstmt.setLong(2, order.getRestaurant().getId());
            pstmt.setString(3, order.getTakeAway() ? "O" : "N");
            pstmt.setTimestamp(4, Timestamp.valueOf(order.getWhen()));

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Failed to add order, no rows affected.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    order.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Failed to add order, no ID obtained.");
                }
            }

            // Aggiungi ogni prodotto associato all'ordine nella tabella PRODUIT_COMMANDE
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
        return orders;
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

        Long orderId = rs.getLong("numero");
        Customer customer = customerMapper.findCustomerById(rs.getLong("fk_client"));
        Restaurant restaurant = restaurantMapper.findRestaurantById(rs.getLong("fk_resto"));
        Boolean takeAway = "O".equals(rs.getString("a_emporter"));
        Timestamp whenTimestamp = rs.getTimestamp("quand");

        Order order = new Order(orderId, customer, restaurant, takeAway, whenTimestamp.toLocalDateTime());

        // Aggiungi i prodotti associati all'ordine
        Set<Product> products = productMapper.findProductsByOrderId(orderId);
        for (Product product : products) {
            order.addProduct(product);
        }
        return order;
    }
}







