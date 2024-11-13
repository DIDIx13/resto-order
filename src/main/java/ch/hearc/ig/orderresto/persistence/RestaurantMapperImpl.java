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
    public void addRestaurant(Connection conn, Restaurant restaurant) throws SQLException {
        String sql = "INSERT INTO RESTAURANT (nom, code_postal, localite, rue, num_rue, pays) VALUES (?, ?, ?, ?, ?, ?)";
        String selectIdSql = "SELECT SEQ_RESTAURANT.CURRVAL FROM dual";

        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, restaurant.getName());
            pstmt.setString(2, restaurant.getAddress().getPostalCode());
            pstmt.setString(3, restaurant.getAddress().getLocality());
            pstmt.setString(4, restaurant.getAddress().getStreet());
            pstmt.setString(5, restaurant.getAddress().getStreetNumber());
            pstmt.setString(6, restaurant.getAddress().getCountryCode());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("L'ajout d'un restaurant a échoué, aucune ligne affectée.");
            }

            // Recupero dell'ID generato per il ristorante
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(selectIdSql)) {
                if (rs.next()) {
                    restaurant.setId(rs.getLong(1));
                    identityMap.put(restaurant.getId(), restaurant);
                } else {
                    throw new SQLException("Échec de la récupération de l'identifiant du restaurant.");
                }
            }
        }
    }

    @Override
    public void removeRestaurant(Connection conn, Restaurant restaurant) throws SQLException {
        String sql = "DELETE FROM RESTAURANT WHERE numero = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, restaurant.getId());
            pstmt.executeUpdate();
            identityMap.put(restaurant.getId(), null);

        }
    }

    @Override
    public void updateRestaurant(Connection conn, Restaurant restaurant) throws SQLException {
        String sql = "UPDATE RESTAURANT SET nom = ?, code_postal = ?, localite = ?, rue = ?, num_rue = ?, pays = ? WHERE numero = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, restaurant.getName());
            pstmt.setString(2, restaurant.getAddress().getPostalCode());
            pstmt.setString(3, restaurant.getAddress().getLocality());
            pstmt.setString(4, restaurant.getAddress().getStreet());
            pstmt.setString(5, restaurant.getAddress().getStreetNumber());
            pstmt.setString(6, restaurant.getAddress().getCountryCode());
            pstmt.setLong(7, restaurant.getId());

            pstmt.executeUpdate();
            identityMap.put(restaurant.getId(), restaurant);

        }
    }

    @Override
    public Restaurant findRestaurantById(Connection conn, Long id) throws SQLException {
        if (identityMap.contains(id)) {
            return identityMap.get(id);
        }

        String sql = "SELECT * FROM RESTAURANT WHERE numero = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

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
        }
        return null;
    }

    @Override
    public Set<Restaurant> findAllRestaurants(Connection conn) throws SQLException {
        Set<Restaurant> restaurants = new HashSet<>();
        String sql = "SELECT * FROM RESTAURANT";
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
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
        }
        return restaurants;
    }
}