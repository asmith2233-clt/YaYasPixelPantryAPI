package org.yearup.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigDecimal;

public class ShoppingCartItem
{
    private Product product;
    private int quantity;
    private BigDecimal discountPercent;

    // ✅ Required constructor (used by DAO)
    public ShoppingCartItem(Product product, int quantity)
    {
        this.product = product;
        this.quantity = quantity;
        this.discountPercent = BigDecimal.ZERO;
    }

    // ✅ Default constructor (safe for Jackson if ever needed)
    public ShoppingCartItem()
    {
        this.discountPercent = BigDecimal.ZERO;
        this.quantity = 1;
    }

    public Product getProduct()
    {
        return product;
    }

    public void setProduct(Product product)
    {
        this.product = product;
    }

    public int getQuantity()
    {
        return quantity;
    }

    public void setQuantity(int quantity)
    {
        this.quantity = quantity;
    }

    public BigDecimal getDiscountPercent()
    {
        return discountPercent;
    }

    public void setDiscountPercent(BigDecimal discountPercent)
    {
        this.discountPercent = discountPercent;
    }

    // ❌ Do NOT expose productId in JSON
    @JsonIgnore
    public int getProductId()
    {
        return product != null ? product.getProductId() : 0;
    }

    // ✅ Required by rubric: quantity * price - discount
    public BigDecimal getLineTotal()
    {
        if (product == null || product.getPrice() == null)
        {
            return BigDecimal.ZERO;
        }

        BigDecimal basePrice = product.getPrice();
        BigDecimal qty = BigDecimal.valueOf(quantity);

        BigDecimal subTotal = basePrice.multiply(qty);
        BigDecimal discountAmount = subTotal.multiply(discountPercent);

        return subTotal.subtract(discountAmount);
    }
}
