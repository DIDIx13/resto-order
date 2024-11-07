package ch.hearc.ig.orderresto.services;

import ch.hearc.ig.orderresto.business.*;
import ch.hearc.ig.orderresto.persistence.*;
import ch.hearc.ig.orderresto.persistence.config.DatabaseManager;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Set;

public class OrderService {
    private RestaurantMapper restaurantMapper;
    private ProductMapper productMapper;
    private CustomerMapper customerMapper;
    private OrderMapper orderMapper;
    private Connection connection;

    public OrderService() {
        try {
            this.connection = DatabaseManager.getConnection();

            // Creazione dei mapper senza passare le dipendenze circolari
            this.customerMapper = new CustomerMapperImpl(connection);
            this.orderMapper = new OrderMapperImpl(connection);
            this.productMapper = new ProductMapperImpl(connection);
            this.restaurantMapper = new RestaurantMapperImpl(connection);

            // Impostazione delle dipendenze circolari usando i setter
            ((OrderMapperImpl) this.orderMapper).setCustomerMapper(customerMapper);
            ((OrderMapperImpl) this.orderMapper).setRestaurantMapper(restaurantMapper);
            ((OrderMapperImpl) this.orderMapper).setProductMapper(productMapper);

            ((RestaurantMapperImpl) this.restaurantMapper).setOrderMapper(orderMapper);
            ((RestaurantMapperImpl) this.restaurantMapper).setCustomerMapper(customerMapper);
            ((RestaurantMapperImpl) this.restaurantMapper).setProductMapper(productMapper);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Order createNewOrder(Customer customer, Restaurant restaurant, Product selectedProduct, boolean takeAway) {
        Order order = new Order(null, customer, restaurant, takeAway, LocalDateTime.now());
        order.addProduct(selectedProduct);
        try {
            orderMapper.addOrder(order);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return order;
    }

    public void addOrder(Order order) {
        orderMapper.addOrder(order);
    }

    public Set<Order> getOrdersByCustomerId(Long customerId) {
        try {
            return orderMapper.findOrdersByCustomerId(customerId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Set<Product> getProductsByRestaurant(Long restaurantId) {
        try {
            return productMapper.findProductsByRestaurantId(restaurantId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Set<Restaurant> getAllRestaurants() {
        try {
            return restaurantMapper.findAllRestaurants();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Customer findOrAddCustomer(String email, String phone, Address address, String customerType, String additionalInfo) {
        try {
            Customer existingCustomer = customerMapper.findCustomerByEmail(email);
            if (existingCustomer != null) {
                return existingCustomer;
            }
            Customer customer;
            if (customerType.equals("Private")) {
                String[] info = additionalInfo.split(",");
                customer = new PrivateCustomer(null, phone, email, address, info[0], info[1], info[2]);
            } else {
                customer = new OrganizationCustomer(null, phone, email, address, additionalInfo, "SA"); // esempio per "SA"
            }
            customerMapper.addCustomer(customer);
            return customer;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void close() {
        DatabaseManager.closeConnection(connection);
    }

    public CustomerMapper getCustomerMapper() {
        return customerMapper;
    }

    public RestaurantMapper getRestaurantMapper() {
        return restaurantMapper;
    }

    public ProductMapper getProductMapper() {
        return productMapper;
    }

    public OrderMapper getOrderMapper() {
        return orderMapper;
    }
}
