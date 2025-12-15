package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.data.CategoryDao;
import org.yearup.data.ProductDao;
import org.yearup.models.Category;
import org.yearup.models.Product;

import java.util.List;

@RestController
@RequestMapping("/categories")
@CrossOrigin
public class CategoriesController
{
    private final CategoryDao categoryDao;
    private final ProductDao productDao;

    @Autowired
    public CategoriesController(CategoryDao categoryDao, ProductDao productDao)
    {
        this.categoryDao = categoryDao;
        this.productDao = productDao;
    }

    // 🔹 GET all categories
    @GetMapping
    @PreAuthorize("permitAll()")
    public List<Category> getAll()
    {
        try
        {
            return categoryDao.getAll();
        }
        catch (Exception ex)
        {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Unable to retrieve categories"
            );
        }
    }

    // 🔹 GET category by ID
    @GetMapping("/{id}")
    @PreAuthorize("permitAll()")
    public Category getById(@PathVariable int id)
    {
        try
        {
            Category category = categoryDao.getById(id);

            if (category == null)
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found");

            return category;
        }
        catch (ResponseStatusException ex)
        {
            throw ex;
        }
        catch (Exception ex)
        {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Unable to retrieve category"
            );
        }
    }

    // 🔹 GET products by category ID
    @GetMapping("/{categoryId}/products")
    @PreAuthorize("permitAll()")
    public List<Product> getProductsByCategory(@PathVariable int categoryId)
    {
        try
        {
            return productDao.getByCategoryId(categoryId);
        }
        catch (Exception ex)
        {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Unable to retrieve products for category"
            );
        }
    }

    // 🔒 POST new category (ADMIN ONLY)
    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public Category addCategory(@RequestBody Category category)
    {
        try
        {
            return categoryDao.create(category);
        }
        catch (Exception ex)
        {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Unable to create category"
            );
        }
    }

    // 🔒 PUT update category (ADMIN ONLY)
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateCategory(@PathVariable int id, @RequestBody Category category)
    {
        try
        {
            Category existing = categoryDao.getById(id);

            if (existing == null)
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found");

            categoryDao.update(id, category);
        }
        catch (ResponseStatusException ex)
        {
            throw ex;
        }
        catch (Exception ex)
        {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Unable to update category"
            );
        }
    }

    // 🔒 DELETE category (ADMIN ONLY)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable int id)
    {
        try
        {
            Category existing = categoryDao.getById(id);

            if (existing == null)
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found");

            categoryDao.delete(id);
        }
        catch (ResponseStatusException ex)
        {
            throw ex;
        }
        catch (Exception ex)
        {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Unable to delete category"
            );
        }
    }
}

