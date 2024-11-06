package ch.hearc.ig.orderresto.persistence;

import ch.hearc.ig.orderresto.business.Customer;
import java.util.Set;

public interface CustomerMapper {
    void addCustomer(Customer customer);
    void removeCustomer(Customer customer);
    void updateCustomer(Customer customer);
    Customer findCustomerById(Long id);
    Customer findCustomerByEmail(String email);
    Set<Customer> findAllCustomers();
}