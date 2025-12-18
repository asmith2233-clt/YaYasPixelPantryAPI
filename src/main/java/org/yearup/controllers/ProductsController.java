package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.data.ProductDao;
import org.yearup.models.Product;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/products")
@CrossOrigin
public class ProductsController
{
    private final ProductDao productDao;

    @Autowired
    public ProductsController(ProductDao productDao)
    {
        this.productDao = productDao;
    }

    // 🔎 SEARCH PRODUCTS (PUBLIC)
    @GetMapping
    @PreAuthorize("permitAll()")
    public List<Product> search(
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(name = "subcategory", required = false) String subCategory
    )
    {
        return productDao.search(categoryId, minPrice, maxPrice, subCategory);
    }

    // 🔎 GET PRODUCT BY ID (PUBLIC)
    @GetMapping("/{id}")
    @PreAuthorize("permitAll()")
    public Product getById(@PathVariable int id)
    {
        Product product = productDao.getById(id);

        if (product == null)
        {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        return product;
    }

    // 🔒 ADD PRODUCT (ADMIN ONLY)
    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public Product addProduct(@RequestBody Product product)
    {
        return productDao.create(product);
    }

    // 🔒 UPDATE PRODUCT (ADMIN ONLY)
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateProduct(@PathVariable int id, @RequestBody Product product)
    {
        Product existing = productDao.getById(id);

        if (existing == null)
        {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        productDao.update(id, product);
    }

    // 🔒 DELETE PRODUCT (ADMIN ONLY)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct(@PathVariable int id)
    {
        Product existing = productDao.getById(id);

        if (existing == null)
        {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        productDao.delete(id);
    }
}
