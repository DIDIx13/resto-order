package ch.hearc.ig.orderresto.services;

import ch.hearc.ig.orderresto.business.Order;
import ch.hearc.ig.orderresto.business.Restaurant;
import ch.hearc.ig.orderresto.persistence.OrderMapper;
import ch.hearc.ig.orderresto.persistence.OrderMapperImpl;
import ch.hearc.ig.orderresto.persistence.RestaurantMapper;
import ch.hearc.ig.orderresto.persistence.RestaurantMapperImpl;
import ch.hearc.ig.orderresto.persistence.config.DatabaseManager;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Set;

public class RestaurantService {
    private final RestaurantMapper restaurantMapper; // votre Data Access Object
    private final DatabaseManager databaseManager; // gestionnaire de connexions JDBC

    public RestaurantService() {
        this.restaurantMapper = new RestaurantMapperImpl();
        this.databaseManager = new DatabaseManager();
    }

    public void addRestaurant(Restaurant restaurant) {
        Connection connection = null;
        try {
            // Ouvrir une connexion ou en obtenir une depuis le pool
            connection = databaseManager.getConnection();
            connection.setAutoCommit(false); // Commencer la transaction

              
            restaurantMapper.addRestaurant(connection, restaurant);

            // Commit de la transaction
            connection.commit();
        } catch (SQLException e) {
            if (connection != null) {
                try {
                    // Rollback en cas d'erreur
                    connection.rollback();
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
            }
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    // Fermer ou libérer la connexion pour la rendre au pool
                    connection.close();
                } catch (SQLException closeEx) {
                    closeEx.printStackTrace();
                }
            }
        }
    }

    public void removeRestaurant(Restaurant restaurant) {
        Connection connection = null;
        try {
            // Ouvrir une connexion ou en obtenir une depuis le pool
            connection = databaseManager.getConnection();
            connection.setAutoCommit(false); // Commencer la transaction

              
            restaurantMapper.removeRestaurant(connection, restaurant);

            // Commit de la transaction
            connection.commit();
        } catch (SQLException e) {
            if (connection != null) {
                try {
                    // Rollback en cas d'erreur
                    connection.rollback();
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
            }
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    // Fermer ou libérer la connexion pour la rendre au pool
                    connection.close();
                } catch (SQLException closeEx) {
                    closeEx.printStackTrace();
                }
            }
        }
    }

    public void updateRestaurant(Restaurant restaurant) {
        Connection connection = null;
        try {
            // Ouvrir une connexion ou en obtenir une depuis le pool
            connection = databaseManager.getConnection();
            connection.setAutoCommit(false); // Commencer la transaction

              
            restaurantMapper.updateRestaurant(connection, restaurant);

            // Commit de la transaction
            connection.commit();
        } catch (SQLException e) {
            if (connection != null) {
                try {
                    // Rollback en cas d'erreur
                    connection.rollback();
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
            }
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    // Fermer ou libérer la connexion pour la rendre au pool
                    connection.close();
                } catch (SQLException closeEx) {
                    closeEx.printStackTrace();
                }
            }
        }
    }

    public Restaurant findRestaurantById(Long id) {
        Connection connection = null;
        Restaurant restaurant = null;
        try {
            // Ouvrir une connexion ou en obtenir une depuis le pool
            connection = databaseManager.getConnection();
            connection.setAutoCommit(false); // Commencer la transaction

              
            restaurant = restaurantMapper.findRestaurantById(connection, id);

            // Commit de la transaction
            connection.commit();
        } catch (SQLException e) {
            if (connection != null) {
                try {
                    // Rollback en cas d'erreur
                    connection.rollback();
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
            }
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    // Fermer ou libérer la connexion pour la rendre au pool
                    connection.close();
                } catch (SQLException closeEx) {
                    closeEx.printStackTrace();
                }
            }
            return restaurant;
        }
    }

    public Set<Restaurant> findAllRestaurants() {
        Connection connection = null;
        Set<Restaurant> restaurants = null;
        try {
            // Ouvrir une connexion ou en obtenir une depuis le pool
            connection = databaseManager.getConnection();
            connection.setAutoCommit(false); // Commencer la transaction

              
            restaurants = restaurantMapper.findAllRestaurants(connection);

            // Commit de la transaction
            connection.commit();
        } catch (SQLException e) {
            if (connection != null) {
                try {
                    // Rollback en cas d'erreur
                    connection.rollback();
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
            }
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    // Fermer ou libérer la connexion pour la rendre au pool
                    connection.close();
                } catch (SQLException closeEx) {
                    closeEx.printStackTrace();
                }
            }
            return restaurants;
        }
    }
}
