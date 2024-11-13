package ch.hearc.ig.orderresto.presentation;

import ch.hearc.ig.orderresto.business.Address;
import ch.hearc.ig.orderresto.business.Customer;
import ch.hearc.ig.orderresto.business.OrganizationCustomer;
import ch.hearc.ig.orderresto.business.PrivateCustomer;
import ch.hearc.ig.orderresto.persistence.CustomerMapper;
import ch.hearc.ig.orderresto.services.CustomerService;

public class CustomerCLI extends AbstractCLI {
    private CustomerService customerService;

    public CustomerCLI() {
        this.customerService = new CustomerService();
    }

    public Customer getExistingCustomer() {
        this.ln("Quelle est votre adresse email?");
        String email = this.readEmailFromUser();
        Customer customer = customerService.findCustomerByEmail(email);
        if (customer == null) {
            this.ln("Aucun client trouvé avec cet email.");
        }
        return customer;
    }

    public Customer createNewCustomer() {
        this.ln("Êtes-vous un client privé ou une organisation?");
        this.ln("0. Annuler");
        this.ln("1. Un client privé");
        this.ln("2. Une organisation");
        int customerTypeChoice = this.readIntFromUser(2);

        if (customerTypeChoice == 0) {
            return null;
        }

        this.ln("Quelle est votre adresse email?");
        String email = this.readEmailFromUser();
        this.ln("Quelle est votre numéro de téléphone?");
        String phone = this.readStringFromUser();
        Address address = (new AddressCLI()).getNewAddress();

        Customer customer;
        if (customerTypeChoice == 1) {
            this.ln("Êtes-vous un homme ou une femme (H/F)?");
            String gender = this.readChoicesFromUser(new String[]{"H", "F"});
            this.ln("Quel est votre prénom?");
            String firstName = this.readStringFromUser();
            this.ln("Quel est votre nom?");
            String lastName = this.readStringFromUser();
            customer = new PrivateCustomer(null, phone, email, address, gender, firstName, lastName);
        } else {
            this.ln("Quel est le nom de votre organisation?");
            String name = this.readStringFromUser();
            this.ln(String.format("%s est une société anonyme (SA)?, une association (A) ou une fondation (F)?", name));
            String legalForm = this.readChoicesFromUser(new String[]{"SA", "A", "F"});
            customer = new OrganizationCustomer(null, phone, email, address, name, legalForm);
        }

        customerService.addCustomer(customer);
        this.ln("Client ajouté avec succès.");
        return customer;
    }
}
