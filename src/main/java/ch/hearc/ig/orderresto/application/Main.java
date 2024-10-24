package ch.hearc.ig.orderresto.application;

import ch.hearc.ig.orderresto.persistence.RestaurantMapper;
import ch.hearc.ig.orderresto.persistence.RestaurantMapperImpl;
import ch.hearc.ig.orderresto.presentation.MainCLI;

public class Main {

  public static void main(String[] args) {
    RestaurantMapper restaurantMapper = new RestaurantMapperImpl();
    (new MainCLI(restaurantMapper)).run();
  }
}