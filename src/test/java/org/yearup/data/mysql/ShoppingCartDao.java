package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.models.Product;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;

import javax.sql.DataSource;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

import org.yearup.models.ShoppingCart;

public interface ShoppingCartDao
{
    ShoppingCart getByUserId(int userId);

    void addProduct(int userId, int productId);

    void updateProduct(int userId, int productId, int quantity);

    void removeProduct(int userId, int productId);

    void clearCart(int userId);
}


@Component
class MySqlShoppingCartDao extends MySqlDaoBase implements ShoppingCartDao
{
    public MySqlShoppingCartDao(DataSource dataSource)
    {
        super(dataSource);
    }

    // 🛒 GET CART BY USER ID
    @Override
    public ShoppingCart getByUserId(int userId)
    {
        String sql =
                "SELECT sc.product_id, sc.quantity, p.* " +
                        "FROM shopping_cart sc " +
                        "JOIN products p ON sc.product_id = p.product_id " +
                        "WHERE sc.user_id = ?";

        Map<Integer, ShoppingCartItem> items = new HashMap<>();

        try (Connection connection = getConnection())
        {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, userId);

            ResultSet row = ps.executeQuery();

            while (row.next())
            {
                Product product = mapProduct(row);
                int quantity = row.getInt("quantity");

                ShoppingCartItem item = new ShoppingCartItem(product, quantity);
                items.put(product.getProductId(), item);
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }

        return new ShoppingCart(userId, items);
    }

    // ➕ ADD PRODUCT (OR INCREMENT QUANTITY)
    @Override
    public void addProduct(int userId, int productId)
    {
        String checkSql =
                "SELECT quantity FROM shopping_cart WHERE user_id = ? AND product_id = ?";
        String insertSql =
                "INSERT INTO shopping_cart (user_id, product_id, quantity) VALUES (?, ?, 1)";
        String updateSql =
                "UPDATE shopping_cart SET quantity = quantity + 1 WHERE user_id = ? AND product_id = ?";

        try (Connection connection = getConnection())
        {
            PreparedStatement check = connection.prepareStatement(checkSql);
            check.setInt(1, userId);
            check.setInt(2, productId);

            ResultSet rs = check.executeQuery();

            if (rs.next())
            {
                PreparedStatement update = connection.prepareStatement(updateSql);
                update.setInt(1, userId);
                update.setInt(2, productId);
                update.executeUpdate();
            }
            else
            {
                PreparedStatement insert = connection.prepareStatement(insertSql);
                insert.setInt(1, userId);
                insert.setInt(2, productId);
                insert.executeUpdate();
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    // 🔁 UPDATE PRODUCT QUANTITY
    @Override
    public void updateProduct(int userId, int productId, int quantity)
    {
        if (quantity <= 0)
        {
            removeProduct(userId, productId);
            return;
        }

        String sql =
                "UPDATE shopping_cart SET quantity = ? WHERE user_id = ? AND product_id = ?";

        try (Connection connection = getConnection())
        {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, quantity);
            ps.setInt(2, userId);
            ps.setInt(3, productId);
            ps.executeUpdate();
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    // ❌ REMOVE SINGLE PRODUCT
    @Override
    public void removeProduct(int userId, int productId)
    {
        String sql =
                "DELETE FROM shopping_cart WHERE user_id = ? AND product_id = ?";

        try (Connection connection = getConnection())
        {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, userId);
            ps.setInt(2, productId);
            ps.executeUpdate();
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    // 🧹 CLEAR CART
    @Override
    public void clearCart(int userId)
    {
        String sql =
                "DELETE FROM shopping_cart WHERE user_id = ?";

        try (Connection connection = getConnection())
        {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, userId);
            ps.executeUpdate();
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    // 🔧 MAP PRODUCT FROM RESULT SET
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
