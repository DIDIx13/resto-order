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
    private IdentityMap<Order> identityMap = new IdentityMap<>();

    @Override
    public void addOrder(Order order) {
        if (identityMap.contains(order.getId())) {
            return;
        }

        String sqlGetOrderId = "SELECT SEQ_COMMANDE.NEXTVAL FROM DUAL"; // Gets a new ID from the sequence object
        String sqlOrder = "INSERT INTO COMMANDE (numero, fk_client, fk_resto, a_emporter, quand) VALUES (?, ?, ?, ?, ?)";
        String sqlProductOrder = "INSERT INTO PRODUIT_COMMANDE (fk_commande, fk_produit) VALUES (?, ?)";

        try (Connection conn = DatabaseManager.getConnection()) {
            conn.setAutoCommit(false); // Start a transaction

            // Get a new order ID from the sequence
            try (PreparedStatement pstmtGetOrderId = conn.prepareStatement(sqlGetOrderId);
                 ResultSet rs = pstmtGetOrderId.executeQuery()) {
                if (rs.next()) {
                    order.setId(rs.getLong(1)); // Set the order ID with the value of the sequence
                } else {
                    throw new SQLException("Échec de l'extraction de l'identifiant de la commande à partir de la séquence.");
                }
            }

            // Order entry in the COMMANDE table.
            try (PreparedStatement pstmtOrder = conn.prepareStatement(sqlOrder)) {
                pstmtOrder.setLong(1, order.getId()); // Use the ID obtained from the sequence
                pstmtOrder.setLong(2, order.getCustomer().getId());
                pstmtOrder.setLong(3, order.getRestaurant().getId());
                pstmtOrder.setString(4, order.getTakeAway() ? "O" : "N");
                pstmtOrder.setTimestamp(5, Timestamp.valueOf(order.getWhen()));

                int affectedRows = pstmtOrder.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Échec de l'ajout d'une commande, aucune ligne affectée.");
                }
            }

            // Entering the products associated with this order in PRODUIT_COMMANDE
            try (PreparedStatement pstmtProductOrder = conn.prepareStatement(sqlProductOrder)) {
                for (Product product : order.getProducts()) {
                    pstmtProductOrder.setLong(1, order.getId()); // fk_commande
                    pstmtProductOrder.setLong(2, product.getId()); // fk_produit
                    pstmtProductOrder.addBatch(); // Use batch to optimize input
                }
                pstmtProductOrder.executeBatch(); // Runs batch to insert all associations
            }
            conn.commit();
            identityMap.put(order.getId(), order);

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
            identityMap.put(order.getId(), null);

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

            // Remove old products associated with the order and insert new ones
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

            identityMap.put(order.getId(), order);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Order findOrderById(Long id) {
        if (identityMap.contains(id)) {
            return identityMap.get(id);
        }

        String sql = "SELECT * FROM COMMANDE WHERE numero = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Order order = mapToOrder(rs);
                    identityMap.put(id, order);
                    return order;
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
                    Order order = mapToOrder(rs);
                    orders.add(order);
                    identityMap.put(order.getId(), order);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;  // Returns an empty set if there are no orders, avoiding NullPointerException
    }

    @Override
    public Set<Order> findAllOrders() {
        Set<Order> orders = new HashSet<>();
        String sql = "SELECT * FROM COMMANDE";
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Order order = mapToOrder(rs);
                orders.add(order);
                identityMap.put(order.getId(), order);
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

        // Retrieve products associated with the order
        Set<Product> products = productMapper.findProductsByOrderId(order.getId());
        BigDecimal totalAmount = BigDecimal.ZERO;  // Initialize the total to zero
        for (Product product : products) {
            order.addProduct(product);  // Adds the product to the order
            totalAmount = totalAmount.add(product.getUnitPrice());  // Add the unit price to the total
        }

        order.setTotalAmount(totalAmount);  // Set the calculated total in the order
        return order;
    }
}







