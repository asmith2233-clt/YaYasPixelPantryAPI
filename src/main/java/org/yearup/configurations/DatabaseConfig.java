package org.yearup.configurations;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.yearup.data.ProductDao;
import org.yearup.data.ProfileDao;
import org.yearup.data.UserDao;
import org.yearup.data.mysql.MySqlProductDao;
import org.yearup.data.mysql.MySqlProfileDao;
import org.yearup.data.mysql.MySqlUserDao;

@Configuration
public class DatabaseConfig
{
    private BasicDataSource basicDataSource;

    @Bean
    public BasicDataSource dataSource()
    {
        return basicDataSource;
    }

    @Autowired
    public DatabaseConfig(
            @Value("${spring.datasource.url}") String url,
            @Value("${spring.datasource.username}") String username,
            @Value("${spring.datasource.password}") String password)
    {
        basicDataSource = new BasicDataSource();
        basicDataSource.setUrl(url);
        basicDataSource.setUsername(username);
        basicDataSource.setPassword(password);
    }

    @Bean
    public ProductDao productDao()
    {
        return new MySqlProductDao(basicDataSource) {
            @Override
            public void updateProduct(int userId, int productId, int quantity) {

            }
        };
    }

    @Bean
    public UserDao userDao()
    {
        return new MySqlUserDao(basicDataSource);
    }

    @Bean
    public ProfileDao profileDao()
    {
        return new MySqlProfileDao(basicDataSource);
    }
}
