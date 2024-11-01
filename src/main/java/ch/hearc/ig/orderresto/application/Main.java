package ch.hearc.ig.orderresto.application;

import ch.hearc.ig.orderresto.persistence.RestaurantMapper;
import ch.hearc.ig.orderresto.persistence.RestaurantMapperImpl;
import ch.hearc.ig.orderresto.persistence.config.DatabaseConfig;
import ch.hearc.ig.orderresto.presentation.MainCLI;

import java.sql.Connection;
import java.sql.SQLException;

public class Main {

  public static void main(String[] args) {
    try (Connection conn = DatabaseConfig.getConnection()) {
      if (conn != null) {
        System.out.println("Connessione al database avvenuta con successo!");
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    RestaurantMapper restaurantMapper = new RestaurantMapperImpl();
    (new MainCLI(restaurantMapper)).run();
  }
}