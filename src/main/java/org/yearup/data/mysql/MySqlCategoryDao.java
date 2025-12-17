package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.data.CategoryDao;
import org.yearup.models.Category;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class MySqlCategoryDao extends MySqlDaoBase implements CategoryDao
{
    public MySqlCategoryDao(DataSource dataSource)
    {
        super(dataSource);
    }

    @Override
    public void updateProduct(int userId, int productId, int quantity) {

    }

    @Override
    public void removeProduct(int userId, int productId) {

    }

    @Override
    public List<Category> getAllCategories()
    {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT category_id, name, description FROM categories";

        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet row = ps.executeQuery())
        {
            while (row.next())
            {
                categories.add(mapRow(row));
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Error retrieving categories", e);
        }

        return categories;
    }

    private Connection getConnection() {
        return null;
    }

    @Override
    public Category getById(int categoryId)
    {
        String sql = "SELECT category_id, name, description FROM categories WHERE category_id = ?";

        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(sql))
        {
            ps.setInt(1, categoryId);

            try (ResultSet row = ps.executeQuery())
            {
                if (row.next())
                {
                    return mapRow(row);
                }
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Error retrieving category id=" + categoryId, e);
        }

        return null;
    }

    @Override
    public Category create(Category category)
    {
        String sql = "INSERT INTO categories (name, description) VALUES (?, ?)";

        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS))
        {
            ps.setString(1, category.getName());
            ps.setString(2, category.getDescription());

            int rows = ps.executeUpdate();
            if (rows == 0)
            {
                throw new RuntimeException("Creating category failed, no rows affected.");
            }

            try (ResultSet keys = ps.getGeneratedKeys())
            {
                if (keys.next())
                {
                    int newId = keys.getInt(1);
                    return getById(newId); // return the actual saved row
                }
            }

            throw new RuntimeException("Creating category failed, no ID returned.");
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Error creating category", e);
        }
    }

    @Override
    public void update(int categoryId, Category category)
    {
        String sql = "UPDATE categories SET name = ?, description = ? WHERE category_id = ?";

        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(sql))
        {
            ps.setString(1, category.getName());
            ps.setString(2, category.getDescription());
            ps.setInt(3, categoryId);

            ps.executeUpdate();
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Error updating category id=" + categoryId, e);
        }
    }

    @Override
    public void delete(int categoryId)
    {
        String sql = "DELETE FROM categories WHERE category_id = ?";

        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(sql))
        {
            ps.setInt(1, categoryId);
            ps.executeUpdate();
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Error deleting category id=" + categoryId, e);
        }
    }

    private Category mapRow(ResultSet row) throws SQLException
    {
        Category category = new Category();
        category.setCategoryId(row.getInt("category_id"));
        category.setName(row.getString("name"));
        category.setDescription(row.getString("description"));
        return category;
    }
}
