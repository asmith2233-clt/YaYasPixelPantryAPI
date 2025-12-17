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

    // GET http://localhost:8080/categories
    @GetMapping
    @PreAuthorize("permitAll()")
    public List<Category> getAll()
    {
        return categoryDao.getAllCategories();
    }

    // ✅ GET http://localhost:8080/categories/{id}
    @GetMapping("/{id}")
    @PreAuthorize("permitAll()")
    public Category getById(@PathVariable int id)
    {
        Category category = categoryDao.getById(id);

        if (category == null)
        {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        return category;
    }

    // GET http://localhost:8080/categories/{categoryId}/products
    @GetMapping("/{categoryId}/products")
    @PreAuthorize("permitAll()")
    public List<Product> getProductsByCategory(@PathVariable int categoryId)
    {
        return productDao.listByCategoryId(categoryId);
    }

    // POST http://localhost:8080/categories  (ADMIN ONLY)
    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public Category addCategory(@RequestBody Category category)
    {
        return categoryDao.create(category);
    }

    // PUT http://localhost:8080/categories/{id}  (ADMIN ONLY)
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateCategory(@PathVariable int id, @RequestBody Category category)
    {
        Category existing = categoryDao.getById(id);
        if (existing == null)
        {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        categoryDao.update(id, category);
    }

    // DELETE http://localhost:8080/categories/{id}  (ADMIN ONLY)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable int id)
    {
        Category existing = categoryDao.getById(id);
        if (existing == null)
        {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        categoryDao.delete(id);
    }
}
