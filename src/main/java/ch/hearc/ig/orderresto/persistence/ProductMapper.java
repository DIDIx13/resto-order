package ch.hearc.ig.orderresto.persistence;

import ch.hearc.ig.orderresto.business.Product;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Set;

public interface ProductMapper {
    void addProduct(Connection conn, Product product, Long restaurantId) throws SQLException;
    void removeProduct(Connection conn, Product product) throws SQLException;
    void updateProduct(Connection conn, Product product) throws SQLException;
    Product findProductById(Connection conn, Long id) throws SQLException;
    Set<Product> findProductsByRestaurantId(Connection conn, Long restaurantId) throws SQLException;
    Set<Product> findProductsByOrderId(Connection conn, Long orderId) throws SQLException;
}