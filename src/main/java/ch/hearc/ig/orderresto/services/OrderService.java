package ch.hearc.ig.orderresto.services;

import ch.hearc.ig.orderresto.business.Order;
import ch.hearc.ig.orderresto.persistence.OrderMapperImpl;
import ch.hearc.ig.orderresto.persistence.config.DatabaseManager;
import ch.hearc.ig.orderresto.persistence.OrderMapper;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Set;

public class OrderService {
    private final OrderMapper orderMapper; // votre Data Access Object
    private final DatabaseManager databaseManager; // gestionnaire de connexions JDBC

    public OrderService() {
        this.orderMapper = new OrderMapperImpl();
        this.databaseManager = new DatabaseManager();
    }

    public void addOrder(Order order) {
        Connection connection = null;
        try {
            // Ouvrir une connexion ou en obtenir une depuis le pool
            connection = databaseManager.getConnection();
            connection.setAutoCommit(false); // Commencer la transaction


            orderMapper.addOrder(connection, order);

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

    public void removeOrder(Order order) {
        Connection connection = null;
        try {
            // Ouvrir une connexion ou en obtenir une depuis le pool
            connection = databaseManager.getConnection();
            connection.setAutoCommit(false); // Commencer la transaction


            orderMapper.removeOrder(connection, order);

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

    public void updateOrder(Order order) {
        Connection connection = null;
        try {
            // Ouvrir une connexion ou en obtenir une depuis le pool
            connection = databaseManager.getConnection();
            connection.setAutoCommit(false); // Commencer la transaction


            orderMapper.updateOrder(connection, order);

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

    public Order findOrderById(Long id) {
        Connection connection = null;
        Order order = null;
        try {
            // Ouvrir une connexion ou en obtenir une depuis le pool
            connection = databaseManager.getConnection();
            connection.setAutoCommit(false); // Commencer la transaction


            order = orderMapper.findOrderById(connection, id);

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
            return order;
        }
    }

    public Set<Order> findOrdersByCustomerId(Long customerId) {
        Connection connection = null;
        Set<Order> orders = null;
        try {
            // Ouvrir une connexion ou en obtenir une depuis le pool
            connection = databaseManager.getConnection();
            connection.setAutoCommit(false); // Commencer la transaction


            orders = orderMapper.findOrdersByCustomerId(connection, customerId);

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
            return orders;
        }
    }

    public Set<Order> findAllOrders() {
        Connection connection = null;
        Set<Order> orders = null;
        try {
            // Ouvrir une connexion ou en obtenir une depuis le pool
            connection = databaseManager.getConnection();
            connection.setAutoCommit(false); // Commencer la transaction


            orders = orderMapper.findAllOrders(connection);

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
            return orders;
        }
    }
}
