package org.yearup.models;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class ShoppingCart
{
    private int userId;
    private Map<Integer, ShoppingCartItem> items = new HashMap<>();

    public ShoppingCart() {}

    public ShoppingCart(int userId, Map<Integer, ShoppingCartItem> items)
    {
        this.userId = userId;
        this.items = (items == null) ? new HashMap<>() : items;
    }

    public int getUserId()
    {
        return userId;
    }

    public void setUserId(int userId)
    {
        this.userId = userId;
    }

    public Map<Integer, ShoppingCartItem> getItems()
    {
        return items;
    }

    public void setItems(Map<Integer, ShoppingCartItem> items)
    {
        this.items = items;
    }

    public boolean contains(int productId)
    {
        return items.containsKey(productId);
    }

    public void add(ShoppingCartItem item)
    {
        items.put(item.getProductId(), item);
    }

    public ShoppingCartItem get(int productId)
    {
        return items.get(productId);
    }

    public BigDecimal getTotal()
    {
        return items.values()
                .stream()
                .map(ShoppingCartItem::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
