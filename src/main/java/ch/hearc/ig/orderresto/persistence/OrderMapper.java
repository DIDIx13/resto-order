package ch.hearc.ig.orderresto.persistence;

import ch.hearc.ig.orderresto.business.Order;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Set;

public interface OrderMapper {
    void addOrder(Connection conn, Order order) throws SQLException;
    void removeOrder(Connection conn, Order order) throws SQLException;
    void updateOrder(Connection conn, Order order) throws SQLException;
    Order findOrderById(Connection conn, Long id) throws SQLException;
    Set<Order> findOrdersByCustomerId(Connection conn, Long customerId) throws SQLException;
    Set<Order> findAllOrders(Connection conn) throws SQLException;
}