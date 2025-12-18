package org.yearup.data.mysql;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public abstract class MySqlDaoBase
{
    private final DataSource dataSource;

    public MySqlDaoBase(DataSource dataSource)
    {
        this.dataSource = dataSource;
    }

    public abstract void updateProduct(int userId, int productId, int quantity);

    public abstract void removeProduct(int userId, int productId);

    protected Connection getConnection() throws SQLException
    {
        return dataSource.getConnection();
    }
}
