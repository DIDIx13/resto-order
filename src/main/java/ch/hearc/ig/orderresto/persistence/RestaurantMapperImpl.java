package ch.hearc.ig.orderresto.persistence;

import ch.hearc.ig.orderresto.business.*;
import ch.hearc.ig.orderresto.persistence.config.DatabaseManager;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;

public class RestaurantMapperImpl implements RestaurantMapper {
    private IdentityMap<Restaurant> identityMap = new IdentityMap<>();
    private OrderMapper orderMapper = new OrderMapperImpl();
    private CustomerMapper customerMapper = new CustomerMapperImpl();
    private ProductMapper productMapper = new ProductMapperImpl();

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
                    identityMap.put(restaurant.getId(), restaurant);
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
            identityMap.put(restaurant.getId(), null);

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
            identityMap.put(restaurant.getId(), restaurant);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Restaurant findRestaurantById(Long id) {
        if (identityMap.contains(id)) {
            return identityMap.get(id);
        }

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
                    Restaurant restaurant = new Restaurant(
                            rs.getLong("numero"),
                            rs.getString("nom"),
                            address
                    );
                    identityMap.put(id, restaurant);
                    return restaurant;
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
                identityMap.put(restaurant.getId(), restaurant);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return restaurants;
    }

    @Override
    public void addOrder(Order order) {
        orderMapper.addOrder(order);
    }

    @Override
    public void addCustomer(Customer customer) {
        customerMapper.addCustomer(customer);
    }

    @Override
    public Customer findCustomerByEmail(String email) {
        return customerMapper.findCustomerByEmail(email);
    }

    public Set<Product> findProductsByRestaurantId(Long restaurantId) {
        return productMapper.findProductsByRestaurantId(restaurantId);
    }
}