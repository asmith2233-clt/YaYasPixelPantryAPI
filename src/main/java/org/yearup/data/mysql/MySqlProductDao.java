package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.data.ProductDao;
import org.yearup.models.Product;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static java.sql.DriverManager.getConnection;

@Component
public abstract class MySqlProductDao extends MySqlDaoBase implements ProductDao
{
    public MySqlProductDao(DataSource dataSource)
    {
        super(dataSource);
    }

    @Override
    public void removeProduct(int userId, int productId) {

    }

    //  Search / filter (category, minPrice, maxPrice, subCategory)
    @Override
    public List<Product> search(Integer categoryId, BigDecimal minPrice, BigDecimal maxPrice, String subCategory)
    {
        List<Product> products = new ArrayList<>();

        String sql =
                "SELECT * FROM products " +
                        "WHERE (category_id = ? OR ? = -1) " +
                        "AND (price >= ? OR ? = -1) " +
                        "AND (price <= ? OR ? = -1) " +
                        "AND (subcategory = ? OR ? = '')";

        int cat = (categoryId == null) ? -1 : categoryId;
        BigDecimal min = (minPrice == null) ? BigDecimal.valueOf(-1) : minPrice;
        BigDecimal max = (maxPrice == null) ? BigDecimal.valueOf(-1) : maxPrice;
        String sub = (subCategory == null) ? "" : subCategory;

        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(sql))
        {
            ps.setInt(1, cat);
            ps.setInt(2, cat);

            ps.setBigDecimal(3, min);
            ps.setBigDecimal(4, min);

            ps.setBigDecimal(5, max);
            ps.setBigDecimal(6, max);

            ps.setString(7, sub);
            ps.setString(8, sub);

            try (ResultSet rs = ps.executeQuery())
            {
                while (rs.next())
                {
                    products.add(mapRow(rs));
                }
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Error searching products", e);
        }

        return products;
    }

    //  Used by CategoriesController
    @Override
    public List<Product> listByCategoryId(int categoryId)
    {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM products WHERE category_id = ?";

        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(sql))
        {
            ps.setInt(1, categoryId);

            try (ResultSet rs = ps.executeQuery())
            {
                while (rs.next())
                {
                    products.add(mapRow(rs));
                }
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Error retrieving products by category", e);
        }

        return products;
    }

    // ✅ Interface-required method (delegate safely)
    @Override
    public List<Product> getByCategoryId(int categoryId)
    {
        return listByCategoryId(categoryId);
    }

    @Override
    public Product getById(int productId)
    {
        String sql = "SELECT * FROM products WHERE product_id = ?";

        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(sql))
        {
            ps.setInt(1, productId);

            try (ResultSet rs = ps.executeQuery())
            {
                if (rs.next())
                {
                    return mapRow(rs);
                }
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Error retrieving product by id", e);
        }

        return null;
    }

    @Override
    public Product create(Product product)
    {
        String sql =
                "INSERT INTO products " +
                        "(name, price, category_id, description, subcategory, image_url, stock, featured) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS))
        {
            ps.setString(1, product.getName());
            ps.setBigDecimal(2, product.getPrice());
            ps.setInt(3, product.getCategoryId());
            ps.setString(4, product.getDescription());
            ps.setString(5, product.getSubCategory());
            ps.setString(6, product.getImageUrl());
            ps.setInt(7, product.getStock());
            ps.setBoolean(8, product.isFeatured());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys())
            {
                if (keys.next())
                {
                    return getById(keys.getInt(1));
                }
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Error creating product", e);
        }

        return null;
    }

    @Override
    public void update(int productId, Product product)
    {
        String sql =
                "UPDATE products SET " +
                        "name = ?, price = ?, category_id = ?, description = ?, " +
                        "subcategory = ?, image_url = ?, stock = ?, featured = ? " +
                        "WHERE product_id = ?";

        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(sql))
        {
            ps.setString(1, product.getName());
            ps.setBigDecimal(2, product.getPrice());
            ps.setInt(3, product.getCategoryId());
            ps.setString(4, product.getDescription());
            ps.setString(5, product.getSubCategory());
            ps.setString(6, product.getImageUrl());
            ps.setInt(7, product.getStock());
            ps.setBoolean(8, product.isFeatured());
            ps.setInt(9, productId);

            ps.executeUpdate();
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Error updating product", e);
        }
    }

    @Override
    public void delete(int productId)
    {
        String sql = "DELETE FROM products WHERE product_id = ?";

        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(sql))
        {
            ps.setInt(1, productId);
            ps.executeUpdate();
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Error deleting product", e);
        }
    }

    private Connection getConnection() {
        return null;
    }

    // ✅ Row mapper
    private static Product mapRow(ResultSet row) throws SQLException
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
