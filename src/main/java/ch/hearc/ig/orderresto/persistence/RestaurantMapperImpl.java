package ch.hearc.ig.orderresto.persistence;

import ch.hearc.ig.orderresto.business.*;
import ch.hearc.ig.orderresto.persistence.config.DatabaseManager;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;

public class RestaurantMapperImpl implements RestaurantMapper {
    private Set<Restaurant> restaurants = new HashSet<>();
    private Set<Order> orders = new HashSet<>();
    private Set<Customer> customers = new HashSet<>();

    @Override
    public void addRestaurant(Restaurant restaurant) {
        String sql = "INSERT INTO RESTAURANT (nom, code_postal, localite, rue, num_rue, pays) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, restaurant.getName());
            pstmt.setString(2, restaurant.getAddress().getPostalCode());
            pstmt.setString(3, restaurant.getAddress().getLocality());
            pstmt.setString(4, restaurant.getAddress().getStreet());
            pstmt.setString(5, restaurant.getAddress().getStreetNumber());
            pstmt.setString(6, restaurant.getAddress().getCountryCode());

            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    restaurant.setId(generatedKeys.getLong(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void removeRestaurant(Restaurant restaurant) {
        String sql = "DELETE FROM RESTAURANT WHERE numero = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, restaurant.getId());
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateRestaurant(Restaurant restaurant) {
        String sql = "UPDATE RESTAURANT SET nom = ?, code_postal = ?, localite = ?, rue = ?, num_rue = ?, pays = ? WHERE numero = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, restaurant.getName());
            pstmt.setString(2, restaurant.getAddress().getPostalCode());
            pstmt.setString(3, restaurant.getAddress().getLocality());
            pstmt.setString(4, restaurant.getAddress().getStreet());
            pstmt.setString(5, restaurant.getAddress().getStreetNumber());
            pstmt.setString(6, restaurant.getAddress().getCountryCode());
            pstmt.setLong(7, restaurant.getId());

            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Restaurant findRestaurantById(Long id) {
        String sql = "SELECT * FROM RESTAURANT WHERE numero = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Address address = new Address(
                            rs.getString("pays"),
                            rs.getString("code_postal"),
                            rs.getString("localite"),
                            rs.getString("rue"),
                            rs.getString("num_rue")
                    );
                    return new Restaurant(
                            rs.getLong("numero"),
                            rs.getString("nom"),
                            address
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Set<Restaurant> findAllRestaurants() {
        Set<Restaurant> restaurants = new HashSet<>();
        String sql = "SELECT * FROM RESTAURANT";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Address address = new Address(
                        rs.getString("pays"),
                        rs.getString("code_postal"),
                        rs.getString("localite"),
                        rs.getString("rue"),
                        rs.getString("num_rue")
                );
                Restaurant restaurant = new Restaurant(
                        rs.getLong("numero"),
                        rs.getString("nom"),
                        address
                );
                restaurants.add(restaurant);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return restaurants;
    }

    @Override
    public void addOrder(Order order) {
        orders.add(order);
    }

    @Override
    public void addCustomer(Customer customer) {
        customers.add(customer);
    }

    @Override
    public Customer findCustomerByEmail(String email) {
        return customers.stream()
                .filter(customer -> customer.getEmail().equals(email))
                .findFirst()
                .orElse(null);
    }

    public Set<Product> findProductsByRestaurantId(Long restaurantId) {
        Set<Product> products = new HashSet<>();
        String sql = "SELECT * FROM PRODUIT WHERE fk_resto = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, restaurantId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Product product = new Product(
                            rs.getLong("numero"),
                            rs.getString("nom"),
                            rs.getBigDecimal("prix_unitaire"),
                            rs.getString("description"),
                            null // the restaurant will be added later
                    );
                    products.add(product);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }
}