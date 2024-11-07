package ch.hearc.ig.orderresto.presentation;

import ch.hearc.ig.orderresto.business.Restaurant;
import ch.hearc.ig.orderresto.persistence.RestaurantMapper;
import ch.hearc.ig.orderresto.services.OrderService;
// import ch.hearc.ig.orderresto.persistence.FakeDb;

public class RestaurantCLI extends AbstractCLI {
    private RestaurantMapper restaurantMapper;

    public RestaurantCLI(OrderService orderService) {
        this.restaurantMapper = orderService.getRestaurantMapper();
    }

    public Restaurant getExistingRestaurant() {
        this.ln("Choisissez un restaurant:");
        Object[] allRestaurants = restaurantMapper.findAllRestaurants().toArray();
        for (int i = 0 ; i < allRestaurants.length ; i++) {
            Restaurant restaurant = (Restaurant) allRestaurants[i];
            this.ln(String.format("%d. %s.", i, restaurant.getName()));
        }
        int index = this.readIntFromUser(allRestaurants.length - 1);
        return (Restaurant) allRestaurants[index];
    }
}
