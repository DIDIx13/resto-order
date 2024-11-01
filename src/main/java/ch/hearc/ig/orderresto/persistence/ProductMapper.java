package ch.hearc.ig.orderresto.persistence;

import ch.hearc.ig.orderresto.business.Product;
import java.util.Set;

public interface ProductMapper {
    void addProduct(Product product, Long restaurantId);
    void removeProduct(Product product);
    void updateProduct(Product product);
    Product findProductById(Long id);
    Set<Product> findProductsByRestaurantId(Long restaurantId);
}