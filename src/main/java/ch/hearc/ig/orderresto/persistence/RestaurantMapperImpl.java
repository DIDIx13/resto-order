package ch.hearc.ig.orderresto.persistence;

import ch.hearc.ig.orderresto.business.Customer;
import ch.hearc.ig.orderresto.business.Order;
import ch.hearc.ig.orderresto.business.Restaurant;
import java.util.HashSet;
import java.util.Set;

public class RestaurantMapperImpl implements RestaurantMapper {
    private Set<Restaurant> restaurants = new HashSet<>();
    private Set<Order> orders = new HashSet<>();
    private Set<Customer> customers = new HashSet<>();

    @Override
    public void addRestaurant(Restaurant restaurant) {
        restaurants.add(restaurant);
    }

    @Override
    public void removeRestaurant(Restaurant restaurant) {
        restaurants.remove(restaurant);
    }

    @Override
    public void updateRestaurant(Restaurant restaurant) {
        restaurants.remove(restaurant);
        restaurants.add(restaurant);
    }

    @Override
    public Restaurant findRestaurantById(Long id) {
        return restaurants.stream()
                .filter(restaurant -> restaurant.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public Set<Restaurant> findAllRestaurants() {
        return new HashSet<>(restaurants);
    }

    @Override
    public void addOrder(Order order) {
        orders.add(order);
    }

    @Override
    public void addCustomer(Customer customer) {
        customers.add(customer);
    }
}