package entities;

import com.pinyougou.grouppojo.Cart;

import java.util.List;

public class CartListMessage {
    private Boolean success;
    private String message;
    private List<Cart> cartList;

    public CartListMessage(Boolean success, String message, List<Cart> cartList) {
        this.success = success;
        this.message = message;
        this.cartList = cartList;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Cart> getCartList() {
        return cartList;
    }

    public void setCartList(List<Cart> cartList) {
        this.cartList = cartList;
    }
}
