package ch.hearc.ig.orderresto.persistence;

import ch.hearc.ig.orderresto.business.Customer;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Set;

public interface CustomerMapper {
    void addCustomer(Connection conn, Customer customer) throws SQLException;
    void removeCustomer(Connection conn, Customer customer) throws SQLException;
    void updateCustomer(Connection conn, Customer customer) throws SQLException;
    Customer findCustomerById(Connection conn, Long id) throws SQLException;
    Customer findCustomerByEmail(Connection conn, String email) throws SQLException;
    Set<Customer> findAllCustomers(Connection conn) throws SQLException;
}