package ch.hearc.ig.orderresto.presentation;

import ch.hearc.ig.orderresto.business.Customer;
import ch.hearc.ig.orderresto.business.Order;
import ch.hearc.ig.orderresto.business.Product;
import ch.hearc.ig.orderresto.business.Restaurant;
import ch.hearc.ig.orderresto.persistence.OrderMapper;
import ch.hearc.ig.orderresto.persistence.CustomerMapper;
import ch.hearc.ig.orderresto.persistence.ProductMapper;
import ch.hearc.ig.orderresto.persistence.RestaurantMapper;
import ch.hearc.ig.orderresto.services.CustomerService;
import ch.hearc.ig.orderresto.services.OrderService;
import ch.hearc.ig.orderresto.services.ProductService;
import ch.hearc.ig.orderresto.services.RestaurantService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;

public class OrderCLI extends AbstractCLI {
    private RestaurantService restaurantService;
    private CustomerService customerService;
    private ProductService productService;
    private OrderService orderService;

    public OrderCLI() {
        this.restaurantService = new RestaurantService();
        this.productService = new ProductService();
        this.customerService = new CustomerService();
        this.orderService = new OrderService();
    }

    // Method to create a new command and save it to the database
    public Order createNewOrder() {
        this.ln("======================================================");
        Restaurant restaurant = (new RestaurantCLI()).getExistingRestaurant();

        // Retrieves products from the selected restaurant
        Set<Product> products = productService.findProductsByRestaurantId(restaurant.getId());

        if (products.isEmpty()) {
            this.ln("Le restaurant sélectionné n'a pas de produits disponibles.");
            return null;
        }

        this.ln("Produits disponibles auprès de " + restaurant.getName() + ":");
        Object[] productsArray = products.toArray();
        for (int i = 0; i < productsArray.length; i++) {
            this.ln((i + 1) + ". " + productsArray[i].toString());
        }

        this.ln("Choisissez un produit par son numéro:");
        int productChoice = this.readIntFromUser(1, productsArray.length);
        Product selectedProduct = (Product) productsArray[productChoice - 1];

        this.ln("======================================================");
        this.ln("0. Annuler");
        this.ln("1. Je suis un client existant");
        this.ln("2. Je suis un nouveau client");
        int userChoice = this.readIntFromUser(2);

        if (userChoice == 0) {
            this.ln("Commande annulée.");
            return null;
        }

        CustomerCLI customerCLI = new CustomerCLI();
        Customer customer;
        if (userChoice == 1) {
            customer = customerCLI.getExistingCustomer();
        } else {
            customer = customerCLI.createNewCustomer();
        }

        if (customer == null) {
            this.ln("Client introuvable ou création annulée.");
            return null;
        }

        this.ln("Commande à emporter ? (O/N)");
        String takeAwayChoice = this.readChoicesFromUser(new String[]{"O", "N"});
        boolean takeAway = "O".equalsIgnoreCase(takeAwayChoice);

        // Creating the order and saving it to the database
        Order order = new Order(null, customer, restaurant, takeAway, LocalDateTime.now());
        order.addProduct(selectedProduct);
        orderService.addOrder(order);

        this.ln("Commande ajoutée avec succès!");
        return order;
    }

    // Method for selecting an existing order associated with a client
    public Order selectOrder() {
        Customer customer = (new CustomerCLI()).getExistingCustomer();
        if (customer == null || customer.getId() == null) {
            this.ln("Désolé, le client n'existe pas ou l'ID du client est invalide.");
            return null;
        }

        Set<Order> orders = orderService.findOrdersByCustomerId(customer.getId());
        if (orders.isEmpty()) {
            this.ln(String.format("Désolé, il n'y a aucune commande pour %s", customer.getEmail()));
            return null;
        }

        Object[] ordersArray = orders.toArray();
        this.ln("Choisissez une commande:");
        for (int i = 0; i < ordersArray.length; i++) {
            Order order = (Order) ordersArray[i];
            LocalDateTime when = order.getWhen();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy à HH:mm");
            this.ln(String.format("%d. %.2f, le %s chez %s.", i + 1, order.getTotalAmount(), when.format(formatter), order.getRestaurant().getName()));
        }

        int index = this.readIntFromUser(1, ordersArray.length);
        return (Order) ordersArray[index - 1];
    }

    // Method for displaying the details of a selected command
    public void displayOrder(Order order) {
        if (order == null) {
            this.ln("Commande non trouvée.");
            return;
        }

        LocalDateTime when = order.getWhen();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy à HH:mm");
        this.ln(String.format("Commande de %.2f, le %s chez %s:", order.getTotalAmount(), when.format(formatter), order.getRestaurant().getName()));

        int index = 1;
        for (Product product : order.getProducts()) {
            this.ln(String.format("%d. %s", index++, product));
        }
    }
}