package ch.hearc.ig.orderresto.persistence;

import ch.hearc.ig.orderresto.business.Customer;
import ch.hearc.ig.orderresto.business.OrganizationCustomer;
import ch.hearc.ig.orderresto.business.PrivateCustomer;
import ch.hearc.ig.orderresto.persistence.config.DatabaseManager;
import ch.hearc.ig.orderresto.business.Address;

import java.math.BigDecimal;
import java.sql.*;
import java.util.HashSet;
import java.util.Set;

public class CustomerMapperImpl implements CustomerMapper {
    private IdentityMap<Customer> identityMap = new IdentityMap<>();

    @Override
    public void addCustomer(Connection conn, Customer customer) throws SQLException {
        String sql = "INSERT INTO CLIENT (email, telephone, nom, code_postal, localite, rue, num_rue, pays, est_une_femme, prenom, forme_sociale, type) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, customer.getEmail());
            pstmt.setString(2, customer.getPhone());
            pstmt.setString(3, customer.getAddress().getLocality());
            pstmt.setString(4, customer.getAddress().getPostalCode());
            pstmt.setString(5, customer.getAddress().getLocality());
            pstmt.setString(6, customer.getAddress().getStreet());
            pstmt.setString(7, customer.getAddress().getStreetNumber());
            pstmt.setString(8, customer.getAddress().getCountryCode());
            if (customer instanceof PrivateCustomer) {
                PrivateCustomer privateCustomer = (PrivateCustomer) customer;
                pstmt.setString(9, privateCustomer.getGender().equals("H") ? "N" : "O");
                pstmt.setString(10, privateCustomer.getFirstName());
                pstmt.setNull(11, Types.VARCHAR);
                pstmt.setString(12, "P");
            } else if (customer instanceof OrganizationCustomer) {
                OrganizationCustomer organizationCustomer = (OrganizationCustomer) customer;
                pstmt.setNull(9, Types.CHAR);
                pstmt.setNull(10, Types.VARCHAR);
                pstmt.setString(11, organizationCustomer.getLegalForm());
                pstmt.setString(12, "O");
            }

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("L'ajout d'un client a échoué, aucune ligne n'a été affectée.");
            }

            String selectIdSql = "SELECT SEQ_CLIENT.CURRVAL FROM dual";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(selectIdSql)) {
                if (rs.next()) {
                    customer.setId(rs.getLong(1));
                    identityMap.put(customer.getId(), customer);
                } else {
                    throw new SQLException("Échec de la récupération de l'identifiant du client.");
                }
            }
        }
    }

    @Override
    public void removeCustomer(Connection conn, Customer customer) throws SQLException {
        String sql = "DELETE FROM CLIENT WHERE numero = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, customer.getId());
            pstmt.executeUpdate();
            identityMap.put(customer.getId(), null);

        }
    }

    @Override
    public void updateCustomer(Connection conn, Customer customer) throws SQLException {
        String sql = "UPDATE CLIENT SET email = ?, telephone = ?, nom = ?, code_postal = ?, localite = ?, rue = ?, num_rue = ?, pays = ?, est_une_femme = ?, prenom = ?, forme_sociale = ?, type = ? WHERE numero = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, customer.getEmail());
            pstmt.setString(2, customer.getPhone());
            pstmt.setString(3, customer.getAddress().getLocality());
            pstmt.setString(4, customer.getAddress().getPostalCode());
            pstmt.setString(5, customer.getAddress().getLocality());
            pstmt.setString(6, customer.getAddress().getStreet());
            pstmt.setString(7, customer.getAddress().getStreetNumber());
            pstmt.setString(8, customer.getAddress().getCountryCode());
            if (customer instanceof PrivateCustomer) {
                PrivateCustomer privateCustomer = (PrivateCustomer) customer;
                pstmt.setString(9, privateCustomer.getGender().equals("H") ? "N" : "O");
                pstmt.setString(10, privateCustomer.getFirstName());
                pstmt.setNull(11, Types.VARCHAR);
                pstmt.setString(12, "P");
            } else if (customer instanceof OrganizationCustomer) {
                OrganizationCustomer organizationCustomer = (OrganizationCustomer) customer;
                pstmt.setNull(9, Types.CHAR);
                pstmt.setNull(10, Types.VARCHAR);
                pstmt.setString(11, organizationCustomer.getLegalForm());
                pstmt.setString(12, "O");
            }
            pstmt.setLong(13, customer.getId());

            pstmt.executeUpdate();
            identityMap.put(customer.getId(), customer);

        }
    }

    @Override
    public Customer findCustomerById(Connection conn, Long id) throws SQLException {
        if (identityMap.contains(id)) {
            return identityMap.get(id);
        }

        String sql = "SELECT * FROM CLIENT WHERE numero = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Customer customer = mapToCustomer(rs);
                    identityMap.put(id, customer);
                    return mapToCustomer(rs);
                }
            }
        }
        return null;
    }

    @Override
    public Customer findCustomerByEmail(Connection conn, String email) throws SQLException {
        String sql = "SELECT * FROM CLIENT WHERE UPPER(email) = UPPER(?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email.toUpperCase());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Customer customer = mapToCustomer(rs);
                    if (customer.getId() == null) {
                        throw new SQLException("L'identifiant du client est nul après l'extraction.");
                    }
                    identityMap.put(customer.getId(), customer);
                    return customer;
                }
            }
        }
        return null;
    }

    @Override
    public Set<Customer> findAllCustomers(Connection conn) throws SQLException {
        Set<Customer> customers = new HashSet<>();
        String sql = "SELECT * FROM CLIENT";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Customer customer = mapToCustomer(rs);
                customers.add(customer);
                identityMap.put(customer.getId(), customer);
            }
        }
        return customers;
    }

    private Customer mapToCustomer(ResultSet rs) throws SQLException {
        Long id = rs.getLong("numero"); // Correctly retrieves the client's ID
        Address address = new Address(
                rs.getString("pays"),
                rs.getString("code_postal"),
                rs.getString("localite"),
                rs.getString("rue"),
                rs.getString("num_rue")
        );

        if ("P".equals(rs.getString("type"))) {
            return new PrivateCustomer(
                    id,
                    rs.getString("telephone"),
                    rs.getString("email"),
                    address,
                    rs.getString("est_une_femme"),
                    rs.getString("prenom"),
                    rs.getString("nom")
            );
        } else {
            return new OrganizationCustomer(
                    id,
                    rs.getString("telephone"),
                    rs.getString("email"),
                    address,
                    rs.getString("nom"),
                    rs.getString("forme_sociale")
            );
        }
    }
}
