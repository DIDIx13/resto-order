package ch.hearc.ig.orderresto.presentation;

import ch.hearc.ig.orderresto.business.Order;
// import ch.hearc.ig.orderresto.persistence.FakeDb;
import ch.hearc.ig.orderresto.persistence.*;
import ch.hearc.ig.orderresto.services.OrderService;

public class MainCLI extends AbstractCLI {

    private final OrderService orderService;

    public MainCLI(OrderService orderService) {
        this.orderService = orderService;
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

        OrderCLI orderCLI = new OrderCLI(orderService);
        if (userChoice == 1) {
            Order newOrder = orderCLI.createNewOrder();
            if (newOrder != null) {
                RestaurantMapper rs = orderService.getRestaurantMapper();
                rs.addOrder(newOrder);
                this.ln("Commande créée avec succès!");
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