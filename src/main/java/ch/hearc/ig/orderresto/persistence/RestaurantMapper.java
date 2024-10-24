package ch.hearc.ig.orderresto.persistence;

import ch.hearc.ig.orderresto.business.Restaurant;
import java.util.Set;

public interface RestaurantMapper {
    void addRestaurant(Restaurant restaurant);
    void removeRestaurant(Restaurant restaurant);
    void updateRestaurant(Restaurant restaurant);
    Restaurant findRestaurantById(Long id);
    Set<Restaurant> findAllRestaurants();
}