package org.yearup.data.mysql;

import org.springframework.stereotype.Repository;
import org.yearup.data.ShoppingCartDao;
import org.yearup.models.Product;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static java.sql.DriverManager.getConnection;

@Repository
public class MySqlShoppingCartDao extends MySqlDaoBase implements ShoppingCartDao
{
    public MySqlShoppingCartDao(DataSource dataSource)
    {
        super(dataSource);
    }

    @Override
    public void updateProduct(int userId, int productId, int quantity) {

    }

    // ✅ GET /cart
    @Override
    public ShoppingCart getByUserId(int userId)
    {
        String sql =
                "SELECT sc.product_id, sc.quantity, p.* " +
                        "FROM shopping_cart sc " +
                        "JOIN products p ON sc.product_id = p.product_id " +
                        "WHERE sc.user_id = ?";

        Map<Integer, ShoppingCartItem> items = new HashMap<>();

        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(sql))
        {
            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery())
            {
                while (rs.next())
                {
                    Product product = mapProduct(rs);
                    int quantity = rs.getInt("quantity");

                    items.put(
                            product.getProductId(),
                            new ShoppingCartItem(product, quantity)
                    );
                }
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Error loading shopping cart", e);
        }

        return new ShoppingCart(userId, items);
    }

    // ✅ POST /cart/products/{id}
    @Override
    public void addProduct(int userId, int productId)
    {
        String sql =
                "INSERT INTO shopping_cart (user_id, product_id, quantity) " +
                        "VALUES (?, ?, 1) " +
                        "ON DUPLICATE KEY UPDATE quantity = quantity + 1";

        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(sql))
        {
            ps.setInt(1, userId);
            ps.setInt(2, productId);
            ps.executeUpdate();
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Error adding product to cart", e);
        }
    }

    // ✅ PUT /cart/products/{id}
    @Override
    public void updateQuantity(int userId, int productId, int quantity)
    {
        if (quantity <= 0)
        {
            removeProduct(userId, productId);
            return;
        }

        String sql =
                "UPDATE shopping_cart " +
                        "SET quantity = ? " +
                        "WHERE user_id = ? AND product_id = ?";

        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(sql))
        {
            ps.setInt(1, quantity);
            ps.setInt(2, userId);
            ps.setInt(3, productId);
            ps.executeUpdate();
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Error updating cart quantity", e);
        }
    }

    public Connection getConnection() {
        return null;
    }

    // 🔥 INTERNAL HELPER (not part of interface)
    public void removeProduct(int userId, int productId)
    {
        String sql =
                "DELETE FROM shopping_cart WHERE user_id = ? AND product_id = ?";

        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(sql))
        {
            ps.setInt(1, userId);
            ps.setInt(2, productId);
            ps.executeUpdate();
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Error removing product from cart", e);
        }
    }

    // ✅ DELETE /cart
    @Override
    public void clearCart(int userId)
    {
        String sql =
                "DELETE FROM shopping_cart WHERE user_id = ?";

        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(sql))
        {
            ps.setInt(1, userId);
            ps.executeUpdate();
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Error clearing shopping cart", e);
        }
    }

    private Product mapProduct(ResultSet row) throws SQLException
    {
        return new Product(
                row.getInt("product_id"),
                row.getString("name"),
                row.getBigDecimal("price"),
                row.getInt("category_id"),
                row.getString("description"),
                row.getString("subcategory"),
                row.getInt("stock"),
                row.getBoolean("featured"),
                row.getString("image_url")
        );
    }
}
