package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.models.Product;
import org.yearup.data.ProductDao;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class MySqlProductDao extends MySqlDaoBase implements ProductDao
{
    public MySqlProductDao(DataSource dataSource)
    {
        super(dataSource);
    }

    // 🔎 FIXED SEARCH METHOD (BUG RESOLVED)
    @Override
    public List<Product> search(Integer categoryId, BigDecimal minPrice, BigDecimal maxPrice, String subCategory)
    {
        List<Product> products = new ArrayList<>();

        String sql = "SELECT * FROM products " +
                "WHERE (category_id = ? OR ? = -1) " +
                "AND (price >= ? OR ? = -1) " +
                "AND (price <= ? OR ? = -1) " +
                "AND (subcategory = ? OR ? = '')";

        categoryId = categoryId == null ? -1 : categoryId;
        minPrice = minPrice == null ? new BigDecimal("-1") : minPrice;
        maxPrice = maxPrice == null ? new BigDecimal("-1") : maxPrice;
        subCategory = subCategory == null ? "" : subCategory;

        try (Connection connection = getConnection())
        {
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setInt(1, categoryId);
            statement.setInt(2, categoryId);

            statement.setBigDecimal(3, minPrice);
            statement.setBigDecimal(4, minPrice);

            statement.setBigDecimal(5, maxPrice);
            statement.setBigDecimal(6, maxPrice);

            statement.setString(7, subCategory);
            statement.setString(8, subCategory);

            ResultSet row = statement.executeQuery();

            while (row.next())
            {
                products.add(mapRow(row));
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }

        return products;
    }

    @Override
    public List<Product> listByCategoryId(int categoryId)
    {
        List<Product> products = new ArrayList<>();

        String sql = "SELECT * FROM products WHERE category_id = ?";

        try (Connection connection = getConnection())
        {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, categoryId);

            ResultSet row = statement.executeQuery();

            while (row.next())
            {
                products.add(mapRow(row));
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }

        return products;
    }

    @Override
    public Product getById(int productId)
    {
        String sql = "SELECT * FROM products WHERE product_id = ?";

        try (Connection connection = getConnection())
        {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, productId);

            ResultSet row = statement.executeQuery();

            if (row.next())
            {
                return mapRow(row);
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }

        return null;
    }

    @Override
    public Product create(Product product)
    {
        String sql = "INSERT INTO products " +
                "(name, price, category_id, description, subcategory, image_url, stock, featured) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = getConnection())
        {
            PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            statement.setString(1, product.getName());
            statement.setBigDecimal(2, product.getPrice());
            statement.setInt(3, product.getCategoryId());
            statement.setString(4, product.getDescription());
            statement.setString(5, product.getSubCategory());
            statement.setString(6, product.getImageUrl());
            statement.setInt(7, product.getStock());
            statement.setBoolean(8, product.isFeatured());

            statement.executeUpdate();

            ResultSet keys = statement.getGeneratedKeys();
            if (keys.next())
            {
                int productId = keys.getInt(1);
                return getById(productId);
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }

        return null;
    }

    @Override
    public void update(int productId, Product product)
    {
        String sql = "UPDATE products SET " +
                "name = ?, " +
                "price = ?, " +
                "category_id = ?, " +
                "description = ?, " +
                "subcategory = ?, " +
                "image_url = ?, " +
                "stock = ?, " +
                "featured = ? " +
                "WHERE product_id = ?";

        try (Connection connection = getConnection())
        {
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setString(1, product.getName());
            statement.setBigDecimal(2, product.getPrice());
            statement.setInt(3, product.getCategoryId());
            statement.setString(4, product.getDescription());
            statement.setString(5, product.getSubCategory());
            statement.setString(6, product.getImageUrl());
            statement.setInt(7, product.getStock());
            statement.setBoolean(8, product.isFeatured());
            statement.setInt(9, productId);

            statement.executeUpdate();
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(int productId)
    {
        String sql = "DELETE FROM products WHERE product_id = ?";

        try (Connection connection = getConnection())
        {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, productId);
            statement.executeUpdate();
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Product> getByCategoryId(int categoryId) {
        return List.of();
    }

    protected static Product mapRow(ResultSet row) throws SQLException
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
