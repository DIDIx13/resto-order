package ch.hearc.ig.orderresto.persistence;

import ch.hearc.ig.orderresto.business.Product;
import ch.hearc.ig.orderresto.business.Restaurant;
import ch.hearc.ig.orderresto.persistence.config.DatabaseManager;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;

public class ProductMapperImpl implements ProductMapper {
    private IdentityMap<Product> identityMap = new IdentityMap<>();

    @Override
    public void addProduct(Connection conn, Product product, Long restaurantId) throws SQLException {
        String sql = "INSERT INTO PRODUIT (fk_resto, prix_unitaire, nom, description) VALUES (?, ?, ?, ?)";
        String selectIdSql = "SELECT SEQ_PRODUIT.CURRVAL FROM dual";

        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setLong(1, restaurantId);
            pstmt.setBigDecimal(2, product.getUnitPrice());
            pstmt.setString(3, product.getName());
            pstmt.setString(4, product.getDescription());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("L'ajout d'un produit a échoué, aucune ligne n'est affectée.");
            }

            // Recupero dell'ID generato per il prodotto
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(selectIdSql)) {
                if (rs.next()) {
                    product.setId(rs.getLong(1));
                    identityMap.put(product.getId(), product);
                } else {
                    throw new SQLException("Échec de la récupération de l'identifiant du produit.");
                }
            }
        }
    }

    @Override
    public void removeProduct(Connection conn, Product product) throws SQLException {
        String sql = "DELETE FROM PRODUIT WHERE numero = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, product.getId());
            pstmt.executeUpdate();
            identityMap.put(product.getId(), null);

        }
    }

    @Override
    public void updateProduct(Connection conn, Product product) throws SQLException {
        String sql = "UPDATE PRODUIT SET prix_unitaire = ?, nom = ?, description = ? WHERE numero = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setBigDecimal(1, product.getUnitPrice());
            pstmt.setString(2, product.getName());
            pstmt.setString(3, product.getDescription());
            pstmt.setLong(4, product.getId());

            pstmt.executeUpdate();
            identityMap.put(product.getId(), product);

        }
    }

    @Override
    public Product findProductById(Connection conn, Long id) throws SQLException {
        if (identityMap.contains(id)) {
            return identityMap.get(id);
        }

        String sql = "SELECT * FROM PRODUIT WHERE numero = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Product product = new Product(
                            rs.getLong("numero"),
                            rs.getString("nom"),
                            rs.getBigDecimal("prix_unitaire"),
                            rs.getString("description"),
                            null // Relationship with the restaurant (optional)
                    );
                    identityMap.put(id, product);
                    return product;
                }
            }
        }
        return null;
    }

    @Override
    public Set<Product> findProductsByRestaurantId(Connection conn, Long restaurantId) throws SQLException {
        Set<Product> products = new HashSet<>();
        String sql = "SELECT p.*, r.nom AS restaurant_name, r.numero AS restaurant_id " +
                "FROM PRODUIT p " +
                "JOIN RESTAURANT r ON p.fk_resto = r.numero " +
                "WHERE r.numero = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, restaurantId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Restaurant restaurant = new Restaurant(
                            rs.getLong("restaurant_id"),
                            rs.getString("restaurant_name"),
                            null // You can add the address if necessary
                    );

                    Product product = new Product(
                            rs.getLong("numero"),
                            rs.getString("nom"),
                            rs.getBigDecimal("prix_unitaire"),
                            rs.getString("description"),
                            restaurant // Associate the restaurant with the product
                    );

                    products.add(product);
                    identityMap.put(product.getId(), product);
                }
            }
        }
        return products;
    }

    public Set<Product> findProductsByOrderId(Connection conn, Long orderId) throws SQLException {
        Set<Product> products = new HashSet<>();
        String sql = "SELECT p.*, r.nom AS restaurant_name, r.numero AS restaurant_id " +
                "FROM PRODUIT p " +
                "JOIN PRODUIT_COMMANDE pc ON p.numero = pc.fk_produit " +
                "JOIN RESTAURANT r ON p.fk_resto = r.numero " +
                "WHERE pc.fk_commande = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, orderId); // Set the order ID
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    // Create the Restaurant object for each product
                    Restaurant restaurant = new Restaurant(
                            rs.getLong("restaurant_id"),
                            rs.getString("restaurant_name"),
                            null // Add more address details if needed
                    );

                    // Creates the Product object
                    Product product = new Product(
                            rs.getLong("numero"),
                            rs.getString("nom"),
                            rs.getBigDecimal("prix_unitaire"),
                            rs.getString("description"),
                            restaurant
                    );
                    products.add(product); // Add the product to the set
                    identityMap.put(product.getId(), product);
                }
            }
        }
        return products;
    }
}
