package ch.hearc.ig.orderresto.services;

import ch.hearc.ig.orderresto.business.Customer;
import ch.hearc.ig.orderresto.persistence.CustomerMapper;
import ch.hearc.ig.orderresto.persistence.CustomerMapperImpl;
import ch.hearc.ig.orderresto.persistence.config.DatabaseManager;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Set;

public class CustomerService {
    private final CustomerMapper customerMapper; // votre Data Access Object
    private final DatabaseManager databaseManager; // gestionnaire de connexions JDBC

    public CustomerService() {
        this.customerMapper = new CustomerMapperImpl();
        this.databaseManager = new DatabaseManager();
    }

    public void addCustomer(Customer customer) {
        Connection connection = null;
        try {
            // Ouvrir une connexion ou en obtenir une depuis le pool
            connection = databaseManager.getConnection();
            connection.setAutoCommit(false); // Commencer la transaction


            customerMapper.addCustomer(connection, customer);

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

    public void removeCustomer(Customer customer) {
        Connection connection = null;
        try {
            // Ouvrir une connexion ou en obtenir une depuis le pool
            connection = databaseManager.getConnection();
            connection.setAutoCommit(false); // Commencer la transaction


            customerMapper.removeCustomer(connection, customer);

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

    public void updateCustomer(Customer customer) {
        Connection connection = null;
        try {
            // Ouvrir une connexion ou en obtenir une depuis le pool
            connection = databaseManager.getConnection();
            connection.setAutoCommit(false); // Commencer la transaction


            customerMapper.updateCustomer(connection, customer);

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

    public Customer findCustomerById(Long id) {
        Connection connection = null;
        Customer customer = null;
        try {
            // Ouvrir une connexion ou en obtenir une depuis le pool
            connection = databaseManager.getConnection();
            connection.setAutoCommit(false); // Commencer la transaction


            customer = customerMapper.findCustomerById(connection, id);

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
            return customer;
        }
    }

    public Customer findCustomerByEmail(String email) {
        Connection connection = null;
        Customer customer = null;
        try {
            // Ouvrir une connexion ou en obtenir une depuis le pool
            connection = databaseManager.getConnection();
            connection.setAutoCommit(false); // Commencer la transaction


            customer = customerMapper.findCustomerByEmail(connection, email);

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
            return customer;
        }
    }

    public Set<Customer> findAllCustomers() {
        Connection connection = null;
        Set<Customer> customers = null;
        try {
            // Ouvrir une connexion ou en obtenir une depuis le pool
            connection = databaseManager.getConnection();
            connection.setAutoCommit(false); // Commencer la transaction


            customers = customerMapper.findAllCustomers(connection);

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
            return customers;
        }
    }
}
