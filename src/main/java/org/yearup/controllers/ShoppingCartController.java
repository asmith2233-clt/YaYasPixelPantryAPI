package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
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

@RestController
@RequestMapping("/cart")
@PreAuthorize("isAuthenticated()")
public class ShoppingCartController
{
    private final ShoppingCartDao shoppingCartDao;
    private final UserDao userDao;
    private final ProfileDao profileDao;
    private ProductDao productDao;

    @Autowired
    public ShoppingCartController(
            ShoppingCartDao shoppingCartDao,
            UserDao userDao, ProfileDao profileDao)
    {
        this.shoppingCartDao = shoppingCartDao;
        this.userDao = userDao;
        this.profileDao = profileDao;
    }

    // GET /cart
    @GetMapping
    public ShoppingCart getCart(Principal principal) {
        User user = userDao.getByUsername(principal.getName());
        ShoppingCart cart = shoppingCartDao.getByUserId(user.getId());

        for (ShoppingCartItem item : cart.getItems().values())
        {

            Product product = productDao.getById(item.getProductId());
            item.setProduct(product);
        }

        return cart;
    }
    // POST /cart/products/{productId}
    @PostMapping("/products/{productId}")
    public void addProductToCart(
            @PathVariable int productId,
            Principal principal)
    {
        User user = userDao.getByUsername(principal.getName());
        shoppingCartDao.addProduct(user.getId(), productId);
    }

    // PUT /cart/products/{productId}
    @PutMapping("/products/{productId}")
    public void updateProductQuantity(
            @PathVariable int productId,
            @RequestBody ShoppingCartItem item,
            Principal principal)
    {
        User user = userDao.getByUsername(principal.getName());
        shoppingCartDao.updateQuantity(
                user.getId(),
                productId,
                item.getQuantity());
    }

    // DELETE /cart
    @DeleteMapping
    public void clearCart(Principal principal)
    {
        User user = userDao.getByUsername(principal.getName());
        shoppingCartDao.clearCart(user.getId());
    }
}
