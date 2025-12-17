package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.yearup.data.ProductDao;
import org.yearup.data.ProfileDao;
import org.yearup.data.ShoppingCartDao;
import org.yearup.data.UserDao;
import org.yearup.models.Product;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;
import org.yearup.models.User;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/cart")
@CrossOrigin
@PreAuthorize("isAuthenticated()")
public class ShoppingCartController
{
    private final ShoppingCartDao shoppingCartDao;
    private final UserDao userDao;

    @Autowired
    public ShoppingCartController(ShoppingCartDao shoppingCartDao, UserDao userDao)
    {
        this.shoppingCartDao = shoppingCartDao;
        this.userDao = userDao;
    }

    // ✅ GET /cart
    @GetMapping
    public ShoppingCart getCart(Principal principal)
    {
        String username = principal.getName();
        int userId = userDao.getIdByUsername(username);

        return shoppingCartDao.getByUserId(userId);
    }

    // ✅ POST /cart/products/{id}
    @PostMapping("/products/{productId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void addProduct(
            @PathVariable int productId,
            Principal principal)
    {
        int userId = userDao.getIdByUsername(principal.getName());
        shoppingCartDao.addProduct(userId, productId);
    }

    // ✅ PUT /cart/products/{id}
    @PutMapping("/products/{productId}")
    public void updateQuantity(
            @PathVariable int productId,
            @RequestBody Map<String, Integer> body,
            Principal principal)
    {
        int quantity = body.get("quantity");
        int userId = userDao.getIdByUsername(principal.getName());

        shoppingCartDao.updateQuantity(userId, productId, quantity);
    }

    // ✅ DELETE /cart
    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void clearCart(Principal principal)
    {
        int userId = userDao.getIdByUsername(principal.getName());
        shoppingCartDao.clearCart(userId);
    }
}
