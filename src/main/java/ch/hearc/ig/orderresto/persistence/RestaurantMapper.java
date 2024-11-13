package ch.hearc.ig.orderresto.persistence;

import ch.hearc.ig.orderresto.business.Customer;
import ch.hearc.ig.orderresto.business.Order;
import ch.hearc.ig.orderresto.business.Restaurant;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Set;

public interface RestaurantMapper {
    void addRestaurant(Connection conn, Restaurant restaurant) throws SQLException;
    void removeRestaurant(Connection conn, Restaurant restaurant) throws SQLException;
    void updateRestaurant(Connection conn, Restaurant restaurant) throws SQLException;
    Restaurant findRestaurantById(Connection conn, Long id) throws SQLException;
    Set<Restaurant> findAllRestaurants(Connection conn) throws SQLException;
}