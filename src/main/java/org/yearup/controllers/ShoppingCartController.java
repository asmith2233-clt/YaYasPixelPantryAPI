package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.data.ProductDao;
import org.yearup.data.ShoppingCartDao;
import org.yearup.data.UserDao;
import org.yearup.models.ShoppingCart;
import org.yearup.models.User;

import java.security.Principal;

@RestController
@RequestMapping("/cart")
@CrossOrigin
@PreAuthorize("isAuthenticated()")
public class ShoppingCartController
{
    private final ShoppingCartDao shoppingCartDao;
    private final UserDao userDao;
    private final ProductDao productDao;

    @Autowired
    public ShoppingCartController(
            ShoppingCartDao shoppingCartDao,
            UserDao userDao,
            ProductDao productDao)
    {
        this.shoppingCartDao = shoppingCartDao;
        this.userDao = userDao;
        this.productDao = productDao;
    }

    @GetMapping
    public ShoppingCart getCart(Principal principal)
    {
        User user = getAuthenticatedUser(principal);
        return shoppingCartDao.getByUserId(user.getId());
    }

    @PostMapping("/products/{productId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void addProductToCart(@PathVariable int productId, Principal principal)
    {
        User user = getAuthenticatedUser(principal);

        if (productDao.getById(productId) == null)
        {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
        }

        shoppingCartDao.addProduct(user.getId(), productId);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void clearCart(Principal principal)
    {
        User user = getAuthenticatedUser(principal);
        shoppingCartDao.clearCart(user.getId());
    }

    private User getAuthenticatedUser(Principal principal)
    {
        if (principal == null)
        {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
        }

        User user = userDao.getByUserName(principal.getName());

        if (user == null)
        {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found");
        }

        return user;
    }
}
