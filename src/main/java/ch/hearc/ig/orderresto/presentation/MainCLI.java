package ch.hearc.ig.orderresto.presentation;

import ch.hearc.ig.orderresto.business.Order;
// import ch.hearc.ig.orderresto.persistence.FakeDb;
import ch.hearc.ig.orderresto.persistence.*;
import ch.hearc.ig.orderresto.services.CustomerService;
import ch.hearc.ig.orderresto.services.OrderService;
import ch.hearc.ig.orderresto.services.ProductService;
import ch.hearc.ig.orderresto.services.RestaurantService;

public class MainCLI extends AbstractCLI {
    private RestaurantService restaurantService;
    private ProductService productService;
    private CustomerService customerService;
    private OrderService orderService;

    public MainCLI() {
        this.restaurantService = new RestaurantService();
        this.productService = new ProductService();
        this.customerService = new CustomerService();
        this.orderService = new OrderService();
    }

    public void run() {
        this.ln("======================================================");
        this.ln("Que voulez-vous faire ?");
        this.ln("0. Quitter l'application");
        this.ln("1. Faire une nouvelle commande");
        this.ln("2. Consulter une commande");
        int userChoice = this.readIntFromUser(2);
        this.handleUserChoice(userChoice);
    }

    private void handleUserChoice(int userChoice) {
        if (userChoice == 0) {
            this.ln("Good bye!");
            return;
        }
        OrderCLI orderCLI = new OrderCLI();
        if (userChoice == 1) {
            Order newOrder = orderCLI.createNewOrder();
            if (newOrder != null) {
                //orderService.addOrder(newOrder);
            }
        } else {
            Order existingOrder = orderCLI.selectOrder();
            if (existingOrder != null) {
                orderCLI.displayOrder(existingOrder);
            }
        }
        this.run();
    }
}