package ch.hearc.ig.orderresto.presentation;

import ch.hearc.ig.orderresto.business.Customer;
import ch.hearc.ig.orderresto.business.Order;
import ch.hearc.ig.orderresto.business.Product;
import ch.hearc.ig.orderresto.business.Restaurant;
// import ch.hearc.ig.orderresto.persistence.FakeDb;
import ch.hearc.ig.orderresto.persistence.RestaurantMapper;
import ch.hearc.ig.orderresto.persistence.ProductMapper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;

public class OrderCLI extends AbstractCLI {
    private RestaurantMapper restaurantMapper;
    private ProductMapper productMapper;

    public OrderCLI(RestaurantMapper restaurantMapper, ProductMapper productMapper) {
        this.restaurantMapper = restaurantMapper;
        this.productMapper = productMapper;
    }

    public Order createNewOrder() {
        this.ln("======================================================");
        Restaurant restaurant = (new RestaurantCLI(restaurantMapper)).getExistingRestaurant();

        // Recupera i prodotti del ristorante selezionato
        Set<Product> products = productMapper.findProductsByRestaurantId(restaurant.getId());

        if (products.isEmpty()) {
            this.ln("Il ristorante selezionato non ha prodotti disponibili.");
            return null;
        }

        this.ln("Prodotti disponibili presso " + restaurant.getName() + ":");
        Object[] productsArray = products.toArray();  // Converte Set<Product> in un array per l'accesso tramite indice
        for (int i = 0; i < productsArray.length; i++) {
            this.ln((i + 1) + ". " + productsArray[i].toString());
        }

        this.ln("Bienvenue chez " + restaurant.getName() + ". Choisissez un de nos produits:");
        int productChoice = this.readIntFromUser(1, productsArray.length);  // Consente di scegliere tra i prodotti elencati

        // Recupera il prodotto scelto dall'array
        Product selectedProduct = (Product) productsArray[productChoice - 1];

        this.ln("======================================================");
        this.ln("0. Annuler");
        this.ln("1. Je suis un client existant");
        this.ln("2. Je suis un nouveau client");
        int userChoice = this.readIntFromUser(2);
        if (userChoice == 0) {
            (new MainCLI(restaurantMapper)).run();
            return null;
        }

        CustomerCLI customerCLI = new CustomerCLI(restaurantMapper);
        Customer customer = null;
        if (userChoice == 1) {
            customer = customerCLI.getExistingCustomer();
        } else {
            customer = customerCLI.createNewCustomer();
            restaurantMapper.addCustomer(customer);
        }

        Order order = new Order(null, customer, restaurant, false, LocalDateTime.now());
        order.addProduct(selectedProduct); // Assicurati di aggiungere il prodotto all'ordine
        selectedProduct.addOrder(order); // Associa il prodotto all'ordine
        restaurant.addOrder(order); // Associa l'ordine al ristorante
        customer.addOrder(order); // Associa l'ordine al cliente
        this.ln("Merci pour votre commande!");
        return order;
    }

    public Order selectOrder() {
        Customer customer = (new CustomerCLI(restaurantMapper)).getExistingCustomer();

        if (customer == null) {
            this.ln(String.format("Désolé, il n'y a aucun client avec cet email"));
            return null;
        }
        Object[] orders = customer.getOrders().toArray();
        if (orders.length == 0) {
            this.ln(String.format("Désolé, il n'y a aucune commande pour %s", customer.getEmail()));
            return null;
        }
        this.ln("Choisissez une commande:");
        for (int i = 0 ; i < orders.length ; i++) {
            Order order = (Order) orders[i];
            LocalDateTime when = order.getWhen();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy à hh:mm");
            this.ln(String.format("%d. %.2f, le %s chez %s.", i, order.getTotalAmount(), when.format(formatter), order.getRestaurant().getName()));
        }
        int index = this.readIntFromUser(orders.length - 1);
        return (Order) orders[index];
    }

    public void displayOrder(Order order) {
        LocalDateTime when = order.getWhen();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy à hh:mm");
        this.ln(String.format("Commande %.2f, le %s chez %s.:", order.getTotalAmount(), when.format(formatter), order.getRestaurant().getName()));
        int index = 1;
        for (Product product: order.getProducts()) {
            this.ln(String.format("%d. %s", index, product));
            index++;
        }
    }
}