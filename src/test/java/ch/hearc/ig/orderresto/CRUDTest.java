package ch.hearc.ig.orderresto;

import ch.hearc.ig.orderresto.business.*;
import ch.hearc.ig.orderresto.business.Order;
import ch.hearc.ig.orderresto.persistence.*;
import ch.hearc.ig.orderresto.services.*;
import org.junit.jupiter.api.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

public class CRUDTest {

    private CustomerService customerService;
    private ProductService productService;
    private OrderService orderService;
    private RestaurantService restaurantService;

    @BeforeEach
    public void setup() {
        customerService = new CustomerService();
        productService = new ProductService();
        orderService = new OrderService();
        restaurantService = new RestaurantService();
    }

    // Customer CRUD tests
    @Test
    public void testCreateCustomer() {
        Address address = new Address("CH", "2000", "Neuchâtel", "Rue de la Gare", "1");
        Customer customer = new PrivateCustomer(null, "123456789", "test@example.com", address, "H", "John", "Doe");
        customerService.addCustomer(customer);
        assertNotNull(customer.getId(), "Customer ID should not be null after creation");
    }

    @Test
    public void testReadCustomer() {
        // Create a customer first
        Address address = new Address("CH", "2000", "Neuchâtel", "Rue de la Gare", "1");
        Customer customer = new PrivateCustomer(null, "123456789", "test@example.com", address, "H", "John", "Doe");
        customerService.addCustomer(customer);

        // Read the customer
        Customer retrievedCustomer = customerService.findCustomerById(customer.getId());
        assertNotNull(retrievedCustomer, "Customer should be found");
        assertEquals("test@example.com", retrievedCustomer.getEmail(), "Customer email should match");
    }

    @Test
    public void testUpdateCustomer() {
        // Create a customer first
        Address address = new Address("CH", "2000", "Neuchâtel", "Rue de la Gare", "1");
        Customer customer = new PrivateCustomer(null, "123456789", "test@example.com", address, "H", "John", "Doe");
        customerService.addCustomer(customer);

        // Update the customer
        customer.setPhone("987654321");
        customerService.updateCustomer(customer);
        Customer updatedCustomer = customerService.findCustomerById(customer.getId());
        assertEquals("987654321", updatedCustomer.getPhone(), "Updated customer phone should match");
    }

    @Test
    public void testDeleteCustomer() {
        // Create a customer first
        Address address = new Address("CH", "2000", "Neuchâtel", "Rue de la Gare", "1");
        Customer customer = new PrivateCustomer(null, "123456789", "test@example.com", address, "H", "John", "Doe");
        customerService.addCustomer(customer);

        // Delete the customer
        customerService.removeCustomer(customer);
        assertNull(customerService.findCustomerById(customer.getId()), "Customer should be deleted");
    }

    // Product CRUD tests
    @Test
    public void testCreateProduct() {
        Restaurant restaurant = new Restaurant(null, "Le Délice", new Address("CH", "1000", "Lausanne", "Rue Centrale", "10"));
        restaurantService.addRestaurant(restaurant);
        Product product = new Product(null, "Pizza", BigDecimal.valueOf(15.00), "Delicious pizza", restaurant);
        productService.addProduct(product, restaurant.getId());
        assertNotNull(product.getId(), "Product ID should not be null after creation");
    }

    @Test
    public void testReadProduct() {
        // Create a product first
        Restaurant restaurant = new Restaurant(null, "Le Délice", new Address("CH", "1000", "Lausanne", "Rue Centrale", "10"));
        restaurantService.addRestaurant(restaurant);
        Product product = new Product(null, "Pizza", BigDecimal.valueOf(15.00), "Delicious pizza", restaurant);
        productService.addProduct(product, restaurant.getId());

        // Read the product
        Product retrievedProduct = productService.findProductById(product.getId());
        assertNotNull(retrievedProduct, "Product should be found");
        assertEquals("Pizza", retrievedProduct.getName(), "Product name should match");
    }

    @Test
    public void testUpdateProduct() {
        // Create a product first
        Restaurant restaurant = new Restaurant(null, "Le Délice", new Address("CH", "1000", "Lausanne", "Rue Centrale", "10"));
        restaurantService.addRestaurant(restaurant);
        Product product = new Product(null, "Pizza", BigDecimal.valueOf(15.00), "Delicious pizza", restaurant);
        productService.addProduct(product, restaurant.getId());

        // Update the product
        product.setUnitPrice(BigDecimal.valueOf(12.50));
        productService.updateProduct(product);
        Product updatedProduct = productService.findProductById(product.getId());
        assertEquals(BigDecimal.valueOf(12.50), updatedProduct.getUnitPrice(), "Updated product price should match");
    }

    @Test
    public void testDeleteProduct() {
        // Create a product first
        Restaurant restaurant = new Restaurant(null, "Le Délice", new Address("CH", "1000", "Lausanne", "Rue Centrale", "10"));
        restaurantService.addRestaurant(restaurant);
        Product product = new Product(null, "Pizza", BigDecimal.valueOf(15.00), "Delicious pizza", restaurant);
        productService.addProduct(product, restaurant.getId());

        // Delete the product
        productService.removeProduct(product);
        assertNull(productService.findProductById(product.getId()), "Product should be deleted");
    }

    // Order CRUD tests
    @Test
    public void testCreateOrder() {
        // Setup
        Restaurant restaurant = new Restaurant(null, "Le Délice", new Address("CH", "1000", "Lausanne", "Rue Centrale", "10"));
        restaurantService.addRestaurant(restaurant);
        Customer customer = new PrivateCustomer(null, "123456789", "customer@example.com", restaurant.getAddress(), "H", "Jane", "Doe");
        customerService.addCustomer(customer);
        Product product = new Product(null, "Salad", BigDecimal.valueOf(8.50), "Fresh salad", restaurant);
        productService.addProduct(product, restaurant.getId());

        // Create the order
        Order order = new Order(null, customer, restaurant, true, LocalDateTime.now());
        order.addProduct(product);
        orderService.addOrder(order);
        assertNotNull(order.getId(), "Order ID should not be null after creation");
    }

    @Test
    public void testReadOrder() {
        // Setup and create an order
        Restaurant restaurant = new Restaurant(null, "Le Délice", new Address("CH", "1000", "Lausanne", "Rue Centrale", "10"));
        restaurantService.addRestaurant(restaurant);
        Customer customer = new PrivateCustomer(null, "123456789", "customer@example.com", restaurant.getAddress(), "H", "Jane", "Doe");
        customerService.addCustomer(customer);
        Product product = new Product(null, "Salad", BigDecimal.valueOf(8.50), "Fresh salad", restaurant);
        productService.addProduct(product, restaurant.getId());

        Order order = new Order(null, customer, restaurant, true, LocalDateTime.now());
        order.addProduct(product);
        orderService.addOrder(order);

        // Read the order
        Order retrievedOrder = orderService.findOrderById(order.getId());
        assertNotNull(retrievedOrder, "Order should be found");
        assertEquals(customer.getId(), retrievedOrder.getCustomer().getId(), "Order customer ID should match");
    }

    @Test
    public void testUpdateOrder() {
        // Setup and create an order
        Restaurant restaurant = new Restaurant(null, "Le Délice", new Address("CH", "1000", "Lausanne", "Rue Centrale", "10"));
        restaurantService.addRestaurant(restaurant);
        Customer customer = new PrivateCustomer(null, "123456789", "customer@example.com", restaurant.getAddress(), "H", "Jane", "Doe");
        customerService.addCustomer(customer);
        Product product = new Product(null, "Salad", BigDecimal.valueOf(8.50), "Fresh salad", restaurant);
        productService.addProduct(product, restaurant.getId());

        Order order = new Order(null, customer, restaurant, true, LocalDateTime.now());
        order.addProduct(product);
        orderService.addOrder(order);

        // Update the order
        order.setTakeAway(false);
        orderService.updateOrder(order);
        Order updatedOrder = orderService.findOrderById(order.getId());
        assertFalse(updatedOrder.getTakeAway(), "Updated order takeAway status should match");
    }

    @Test
    public void testDeleteOrder() {
        // Setup and create an order
        Restaurant restaurant = new Restaurant(null, "Le Délice", new Address("CH", "1000", "Lausanne", "Rue Centrale", "10"));
        restaurantService.addRestaurant(restaurant);
        Customer customer = new PrivateCustomer(null, "123456789", "customer@example.com", restaurant.getAddress(), "H", "Jane", "Doe");
        customerService.addCustomer(customer);
        Product product = new Product(null, "Salad", BigDecimal.valueOf(8.50), "Fresh salad", restaurant);
        productService.addProduct(product, restaurant.getId());

        Order order = new Order(null, customer, restaurant, true, LocalDateTime.now());
        order.addProduct(product);
        orderService.addOrder(order);

        // Delete the order
        orderService.removeOrder(order);
        assertNull(orderService.findOrderById(order.getId()), "Order should be deleted");
    }

    // Restaurant CRUD tests
    @Test
    public void testCreateRestaurant() {
        Restaurant restaurant = new Restaurant(null, "Le Délice", new Address("CH", "1000", "Lausanne", "Rue Centrale", "10"));
        restaurantService.addRestaurant(restaurant);
        assertNotNull(restaurant.getId(), "Restaurant ID should not be null after creation");
    }

    @Test
    public void testReadRestaurant() {
        // Create a restaurant first
        Restaurant restaurant = new Restaurant(null, "Le Délice", new Address("CH", "1000", "Lausanne", "Rue Centrale", "10"));
        restaurantService.addRestaurant(restaurant);

        // Read the restaurant
        Restaurant retrievedRestaurant = restaurantService.findRestaurantById(restaurant.getId());
        assertNotNull(retrievedRestaurant, "Restaurant should be found");
        assertEquals("Le Délice", retrievedRestaurant.getName(), "Restaurant name should match");
    }

    @Test
    public void testUpdateRestaurant() {
        // Create a restaurant first
        Restaurant restaurant = new Restaurant(null, "Le Délice", new Address("CH", "1000", "Lausanne", "Rue Centrale", "10"));
        restaurantService.addRestaurant(restaurant);

        // Update the restaurant
        restaurant.setName("Le Gourmet");
        restaurantService.updateRestaurant(restaurant);
        Restaurant updatedRestaurant = restaurantService.findRestaurantById(restaurant.getId());
        assertEquals("Le Gourmet", updatedRestaurant.getName(), "Updated restaurant name should match");
    }

    @Test
    public void testDeleteRestaurant() {
        // Create a restaurant first
        Restaurant restaurant = new Restaurant(null, "Le Délice", new Address("CH", "1000", "Lausanne", "Rue Centrale", "10"));
        restaurantService.addRestaurant(restaurant);

        // Delete the restaurant
        restaurantService.removeRestaurant(restaurant);
        assertNull(restaurantService.findRestaurantById(restaurant.getId()), "Restaurant should be deleted");
    }
}
