package ch.hearc.ig.orderresto.application;

import ch.hearc.ig.orderresto.persistence.RestaurantMapper;
import ch.hearc.ig.orderresto.persistence.RestaurantMapperImpl;
import ch.hearc.ig.orderresto.persistence.config.DatabaseConfig;
import ch.hearc.ig.orderresto.presentation.MainCLI;
import ch.hearc.ig.orderresto.services.OrderService;

import java.sql.Connection;
import java.sql.SQLException;

public class Main {

  public static void main(String[] args) {
    try (Connection conn = DatabaseConfig.getConnection()) {
      if (conn != null) {
        System.out.println("Connexion à la base de données réussie !");
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    OrderService orderService = new OrderService();
    (new MainCLI(orderService)).run();
  }
}