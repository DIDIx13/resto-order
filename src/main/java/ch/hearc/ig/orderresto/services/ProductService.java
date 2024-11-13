package ch.hearc.ig.orderresto.services;

import ch.hearc.ig.orderresto.business.Product;
import ch.hearc.ig.orderresto.persistence.OrderMapper;
import ch.hearc.ig.orderresto.persistence.ProductMapper;
import ch.hearc.ig.orderresto.persistence.ProductMapperImpl;
import ch.hearc.ig.orderresto.persistence.config.DatabaseManager;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Set;

public class ProductService {
    private final ProductMapper productMapper; // votre Data Access Object
    private final DatabaseManager databaseManager; // gestionnaire de connexions JDBC

    public ProductService() {
        this.productMapper = new ProductMapperImpl();
        this.databaseManager = new DatabaseManager();
    }

    public void addProduct(Product product, Long restaurantId) {
        Connection connection = null;
        try {
            // Ouvrir une connexion ou en obtenir une depuis le pool
            connection = databaseManager.getConnection();
            connection.setAutoCommit(false); // Commencer la transaction

            productMapper.addProduct(connection, product, restaurantId);

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

    public void removeProduct(Product product) {
        Connection connection = null;
        try {
            // Ouvrir une connexion ou en obtenir une depuis le pool
            connection = databaseManager.getConnection();
            connection.setAutoCommit(false); // Commencer la transaction

            productMapper.removeProduct(connection, product);

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

    public void updateProduct(Product product) {
        Connection connection = null;
        try {
            // Ouvrir une connexion ou en obtenir une depuis le pool
            connection = databaseManager.getConnection();
            connection.setAutoCommit(false); // Commencer la transaction


            productMapper.updateProduct(connection, product);

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

    public Product findProductById(Long id) {
        Connection connection = null;
        Product product = null;
        try {
            // Ouvrir une connexion ou en obtenir une depuis le pool
            connection = databaseManager.getConnection();
            connection.setAutoCommit(false); // Commencer la transaction


            product = productMapper.findProductById(connection, id);

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
            return product;
        }
    }

    public Set<Product> findProductsByRestaurantId(Long restaurantId) {
        Connection connection = null;
        Set<Product> products = null;
        try {
            // Ouvrir une connexion ou en obtenir une depuis le pool
            connection = databaseManager.getConnection();
            connection.setAutoCommit(false); // Commencer la transaction


            products = productMapper.findProductsByRestaurantId(connection, restaurantId);

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
            return products;
        }
    }

    public Set<Product> findProductsByOrderId(Long orderId) {
        Connection connection = null;
        Set<Product> products = null;
        try {
            // Ouvrir une connexion ou en obtenir une depuis le pool
            connection = databaseManager.getConnection();
            connection.setAutoCommit(false); // Commencer la transaction


            products = productMapper.findProductsByOrderId(connection, orderId);

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
            return products;
        }
    }
}
