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
    public void addOrder(Connection conn, Order order) throws SQLException {
        String sqlOrder = "INSERT INTO COMMANDE (fk_client, fk_resto, a_emporter, quand) VALUES (?, ?, ?, ?)";
        String selectIdSql = "SELECT SEQ_COMMANDE.CURRVAL FROM dual";
        String sqlProductOrder = "INSERT INTO PRODUIT_COMMANDE (fk_commande, fk_produit) VALUES (?, ?)";


            // Inserimento dell'ordine
            try (PreparedStatement pstmtOrder = conn.prepareStatement(sqlOrder, Statement.RETURN_GENERATED_KEYS)) {
                pstmtOrder.setLong(1, order.getCustomer().getId());
                pstmtOrder.setLong(2, order.getRestaurant().getId());
                pstmtOrder.setString(3, order.getTakeAway() ? "O" : "N");
                pstmtOrder.setTimestamp(4, Timestamp.valueOf(order.getWhen()));

                int affectedRows = pstmtOrder.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Échec de l'ajout d'une commande, aucune ligne affectée.");
                }

                // Recupero dell'ID generato per l'ordine
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery(selectIdSql)) {
                    if (rs.next()) {
                        order.setId(rs.getLong(1));
                        identityMap.put(order.getId(), order);
                    } else {
                        throw new SQLException("Échec de la récupération de l'identifiant de la commande.");
                    }
                }
            }

            // Inserimento dei prodotti associati all'ordine
            try (PreparedStatement pstmtProductOrder = conn.prepareStatement(sqlProductOrder)) {
                for (Product product : order.getProducts()) {
                    pstmtProductOrder.setLong(1, order.getId());
                    pstmtProductOrder.setLong(2, product.getId());
                    pstmtProductOrder.addBatch();
                }
                pstmtProductOrder.executeBatch();
            }
    }

    @Override
    public void removeOrder(Connection conn, Order order) throws SQLException {
        String sql = "DELETE FROM COMMANDE WHERE numero = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, order.getId());
            pstmt.executeUpdate();
            identityMap.put(order.getId(), null);

        }
    }

    @Override
    public void updateOrder(Connection conn, Order order) throws SQLException {
        String sql = "UPDATE COMMANDE SET fk_client = ?, fk_resto = ?, a_emporter = ?, quand = ? WHERE numero = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

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

        }
    }

    @Override
    public Order findOrderById(Connection conn, Long id) throws SQLException {
        if (identityMap.contains(id)) {
            return identityMap.get(id);
        }

        String sql = "SELECT * FROM COMMANDE WHERE numero = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Order order = mapToOrder(conn, rs);
                    identityMap.put(id, order);
                    return order;
                }
            }
        }
        return null;
    }

    @Override
    public Set<Order> findOrdersByCustomerId(Connection conn, Long customerId) throws SQLException {
        Set<Order> orders = new HashSet<>();
        String sql = "SELECT * FROM COMMANDE WHERE fk_client = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, customerId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Order order = mapToOrder(conn, rs);
                    orders.add(order);
                    identityMap.put(order.getId(), order);
                }
            }
        }
        return orders;  // Returns an empty set if there are no orders, avoiding NullPointerException
    }

    @Override
    public Set<Order> findAllOrders(Connection conn) throws SQLException {
        Set<Order> orders = new HashSet<>();
        String sql = "SELECT * FROM COMMANDE";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Order order = mapToOrder(conn, rs);
                orders.add(order);
                identityMap.put(order.getId(), order);
            }
        }
        return orders;
    }

    private Order mapToOrder(Connection conn, ResultSet rs) throws SQLException {
        CustomerMapper customerMapper = new CustomerMapperImpl();
        RestaurantMapper restaurantMapper = new RestaurantMapperImpl();
        ProductMapper productMapper = new ProductMapperImpl();

        Long customerId = rs.getLong("fk_client");
        Long restaurantId = rs.getLong("fk_resto");

        Customer customer = customerMapper.findCustomerById(conn, customerId);
        Restaurant restaurant = restaurantMapper.findRestaurantById(conn, restaurantId);
        Boolean takeAway = "O".equals(rs.getString("a_emporter"));
        LocalDateTime when = rs.getTimestamp("quand").toLocalDateTime();

        Order order = new Order(rs.getLong("numero"), customer, restaurant, takeAway, when);

        // Retrieve products associated with the order
        Set<Product> products = productMapper.findProductsByOrderId(conn, order.getId());
        BigDecimal totalAmount = BigDecimal.ZERO;  // Initialize the total to zero
        for (Product product : products) {
            order.addProduct(product);  // Adds the product to the order
            totalAmount = totalAmount.add(product.getUnitPrice());  // Add the unit price to the total
        }

        order.setTotalAmount(totalAmount);  // Set the calculated total in the order
        return order;
    }
}







