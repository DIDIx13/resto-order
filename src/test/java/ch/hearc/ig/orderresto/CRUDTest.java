package ch.hearc.ig.orderresto;

import ch.hearc.ig.orderresto.business.*;
import ch.hearc.ig.orderresto.business.Order;
import ch.hearc.ig.orderresto.persistence.*;
import org.junit.jupiter.api.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

public class CRUDTest {

    private CustomerMapper customerMapper;
    private ProductMapper productMapper;
    private OrderMapper orderMapper;
    private RestaurantMapper restaurantMapper;

    @BeforeEach
    public void setup() {
        customerMapper = new CustomerMapperImpl();
        productMapper = new ProductMapperImpl();
        orderMapper = new OrderMapperImpl();
        restaurantMapper = new RestaurantMapperImpl();
    }

    // Customer CRUD tests
    @Test
    public void testCreateCustomer() {
        Address address = new Address("CH", "2000", "Neuchâtel", "Rue de la Gare", "1");
        Customer customer = new PrivateCustomer(null, "123456789", "test@example.com", address, "H", "John", "Doe");
        customerMapper.addCustomer(customer);
        assertNotNull(customer.getId(), "Customer ID should not be null after creation");
    }

    @Test
    public void testReadCustomer() {
        // Create a customer first
        Address address = new Address("CH", "2000", "Neuchâtel", "Rue de la Gare", "1");
        Customer customer = new PrivateCustomer(null, "123456789", "test@example.com", address, "H", "John", "Doe");
        customerMapper.addCustomer(customer);

        // Read the customer
        Customer retrievedCustomer = customerMapper.findCustomerById(customer.getId());
        assertNotNull(retrievedCustomer, "Customer should be found");
        assertEquals("test@example.com", retrievedCustomer.getEmail(), "Customer email should match");
    }

    @Test
    public void testUpdateCustomer() {
        // Create a customer first
        Address address = new Address("CH", "2000", "Neuchâtel", "Rue de la Gare", "1");
        Customer customer = new PrivateCustomer(null, "123456789", "test@example.com", address, "H", "John", "Doe");
        customerMapper.addCustomer(customer);

        // Update the customer
        customer.setPhone("987654321");
        customerMapper.updateCustomer(customer);
        Customer updatedCustomer = customerMapper.findCustomerById(customer.getId());
        assertEquals("987654321", updatedCustomer.getPhone(), "Updated customer phone should match");
    }

    @Test
    public void testDeleteCustomer() {
        // Create a customer first
        Address address = new Address("CH", "2000", "Neuchâtel", "Rue de la Gare", "1");
        Customer customer = new PrivateCustomer(null, "123456789", "test@example.com", address, "H", "John", "Doe");
        customerMapper.addCustomer(customer);

        // Delete the customer
        customerMapper.removeCustomer(customer);
        assertNull(customerMapper.findCustomerById(customer.getId()), "Customer should be deleted");
    }

    // Product CRUD tests
    @Test
    public void testCreateProduct() {
        Restaurant restaurant = new Restaurant(null, "Le Délice", new Address("CH", "1000", "Lausanne", "Rue Centrale", "10"));
        restaurantMapper.addRestaurant(restaurant);
        Product product = new Product(null, "Pizza", BigDecimal.valueOf(15.00), "Delicious pizza", restaurant);
        productMapper.addProduct(product, restaurant.getId());
        assertNotNull(product.getId(), "Product ID should not be null after creation");
    }

    @Test
    public void testReadProduct() {
        // Create a product first
        Restaurant restaurant = new Restaurant(null, "Le Délice", new Address("CH", "1000", "Lausanne", "Rue Centrale", "10"));
        restaurantMapper.addRestaurant(restaurant);
        Product product = new Product(null, "Pizza", BigDecimal.valueOf(15.00), "Delicious pizza", restaurant);
        productMapper.addProduct(product, restaurant.getId());

        // Read the product
        Product retrievedProduct = productMapper.findProductById(product.getId());
        assertNotNull(retrievedProduct, "Product should be found");
        assertEquals("Pizza", retrievedProduct.getName(), "Product name should match");
    }

    @Test
    public void testUpdateProduct() {
        // Create a product first
        Restaurant restaurant = new Restaurant(null, "Le Délice", new Address("CH", "1000", "Lausanne", "Rue Centrale", "10"));
        restaurantMapper.addRestaurant(restaurant);
        Product product = new Product(null, "Pizza", BigDecimal.valueOf(15.00), "Delicious pizza", restaurant);
        productMapper.addProduct(product, restaurant.getId());

        // Update the product
        product.setUnitPrice(BigDecimal.valueOf(12.50));
        productMapper.updateProduct(product);
        Product updatedProduct = productMapper.findProductById(product.getId());
        assertEquals(BigDecimal.valueOf(12.50), updatedProduct.getUnitPrice(), "Updated product price should match");
    }

    @Test
    public void testDeleteProduct() {
        // Create a product first
        Restaurant restaurant = new Restaurant(null, "Le Délice", new Address("CH", "1000", "Lausanne", "Rue Centrale", "10"));
        restaurantMapper.addRestaurant(restaurant);
        Product product = new Product(null, "Pizza", BigDecimal.valueOf(15.00), "Delicious pizza", restaurant);
        productMapper.addProduct(product, restaurant.getId());

        // Delete the product
        productMapper.removeProduct(product);
        assertNull(productMapper.findProductById(product.getId()), "Product should be deleted");
    }

    // Order CRUD tests
    @Test
    public void testCreateOrder() {
        // Setup
        Restaurant restaurant = new Restaurant(null, "Le Délice", new Address("CH", "1000", "Lausanne", "Rue Centrale", "10"));
        restaurantMapper.addRestaurant(restaurant);
        Customer customer = new PrivateCustomer(null, "123456789", "customer@example.com", restaurant.getAddress(), "H", "Jane", "Doe");
        customerMapper.addCustomer(customer);
        Product product = new Product(null, "Salad", BigDecimal.valueOf(8.50), "Fresh salad", restaurant);
        productMapper.addProduct(product, restaurant.getId());

        // Create the order
        Order order = new Order(null, customer, restaurant, true, LocalDateTime.now());
        order.addProduct(product);
        orderMapper.addOrder(order);
        assertNotNull(order.getId(), "Order ID should not be null after creation");
    }

    @Test
    public void testReadOrder() {
        // Setup and create an order
        Restaurant restaurant = new Restaurant(null, "Le Délice", new Address("CH", "1000", "Lausanne", "Rue Centrale", "10"));
        restaurantMapper.addRestaurant(restaurant);
        Customer customer = new PrivateCustomer(null, "123456789", "customer@example.com", restaurant.getAddress(), "H", "Jane", "Doe");
        customerMapper.addCustomer(customer);
        Product product = new Product(null, "Salad", BigDecimal.valueOf(8.50), "Fresh salad", restaurant);
        productMapper.addProduct(product, restaurant.getId());

        Order order = new Order(null, customer, restaurant, true, LocalDateTime.now());
        order.addProduct(product);
        orderMapper.addOrder(order);

        // Read the order
        Order retrievedOrder = orderMapper.findOrderById(order.getId());
        assertNotNull(retrievedOrder, "Order should be found");
        assertEquals(customer.getId(), retrievedOrder.getCustomer().getId(), "Order customer ID should match");
    }

    @Test
    public void testUpdateOrder() {
        // Setup and create an order
        Restaurant restaurant = new Restaurant(null, "Le Délice", new Address("CH", "1000", "Lausanne", "Rue Centrale", "10"));
        restaurantMapper.addRestaurant(restaurant);
        Customer customer = new PrivateCustomer(null, "123456789", "customer@example.com", restaurant.getAddress(), "H", "Jane", "Doe");
        customerMapper.addCustomer(customer);
        Product product = new Product(null, "Salad", BigDecimal.valueOf(8.50), "Fresh salad", restaurant);
        productMapper.addProduct(product, restaurant.getId());

        Order order = new Order(null, customer, restaurant, true, LocalDateTime.now());
        order.addProduct(product);
        orderMapper.addOrder(order);

        // Update the order
        order.setTakeAway(false);
        orderMapper.updateOrder(order);
        Order updatedOrder = orderMapper.findOrderById(order.getId());
        assertFalse(updatedOrder.getTakeAway(), "Updated order takeAway status should match");
    }

    @Test
    public void testDeleteOrder() {
        // Setup and create an order
        Restaurant restaurant = new Restaurant(null, "Le Délice", new Address("CH", "1000", "Lausanne", "Rue Centrale", "10"));
        restaurantMapper.addRestaurant(restaurant);
        Customer customer = new PrivateCustomer(null, "123456789", "customer@example.com", restaurant.getAddress(), "H", "Jane", "Doe");
        customerMapper.addCustomer(customer);
        Product product = new Product(null, "Salad", BigDecimal.valueOf(8.50), "Fresh salad", restaurant);
        productMapper.addProduct(product, restaurant.getId());

        Order order = new Order(null, customer, restaurant, true, LocalDateTime.now());
        order.addProduct(product);
        orderMapper.addOrder(order);

        // Delete the order
        orderMapper.removeOrder(order);
        assertNull(orderMapper.findOrderById(order.getId()), "Order should be deleted");
    }

    // Restaurant CRUD tests
    @Test
    public void testCreateRestaurant() {
        Restaurant restaurant = new Restaurant(null, "Le Délice", new Address("CH", "1000", "Lausanne", "Rue Centrale", "10"));
        restaurantMapper.addRestaurant(restaurant);
        assertNotNull(restaurant.getId(), "Restaurant ID should not be null after creation");
    }

    @Test
    public void testReadRestaurant() {
        // Create a restaurant first
        Restaurant restaurant = new Restaurant(null, "Le Délice", new Address("CH", "1000", "Lausanne", "Rue Centrale", "10"));
        restaurantMapper.addRestaurant(restaurant);

        // Read the restaurant
        Restaurant retrievedRestaurant = restaurantMapper.findRestaurantById(restaurant.getId());
        assertNotNull(retrievedRestaurant, "Restaurant should be found");
        assertEquals("Le Délice", retrievedRestaurant.getName(), "Restaurant name should match");
    }

    @Test
    public void testUpdateRestaurant() {
        // Create a restaurant first
        Restaurant restaurant = new Restaurant(null, "Le Délice", new Address("CH", "1000", "Lausanne", "Rue Centrale", "10"));
        restaurantMapper.addRestaurant(restaurant);

        // Update the restaurant
        restaurant.setName("Le Gourmet");
        restaurantMapper.updateRestaurant(restaurant);
        Restaurant updatedRestaurant = restaurantMapper.findRestaurantById(restaurant.getId());
        assertEquals("Le Gourmet", updatedRestaurant.getName(), "Updated restaurant name should match");
    }

    @Test
    public void testDeleteRestaurant() {
        // Create a restaurant first
        Restaurant restaurant = new Restaurant(null, "Le Délice", new Address("CH", "1000", "Lausanne", "Rue Centrale", "10"));
        restaurantMapper.addRestaurant(restaurant);

        // Delete the restaurant
        restaurantMapper.removeRestaurant(restaurant);
        assertNull(restaurantMapper.findRestaurantById(restaurant.getId()), "Restaurant should be deleted");
    }
}