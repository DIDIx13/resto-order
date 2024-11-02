package ch.hearc.ig.orderresto.persistence;

import ch.hearc.ig.orderresto.business.Order;
import java.util.Set;

public interface OrderMapper {
    void addOrder(Order order);
    void removeOrder(Order order);
    void updateOrder(Order order);
    Order findOrderById(Long id);
    Set<Order> findOrdersByCustomerId(Long customerId);
    Set<Order> findAllOrders();
}