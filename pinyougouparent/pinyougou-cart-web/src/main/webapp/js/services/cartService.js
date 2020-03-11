//服务层
app.service('cartService', function ($http) {

    //读取列表数据绑定到表单中
    this.findCartList = function () {
        return $http.get('../cart/findCartList.do');
    }

    //合计总价与数量方法, 将购物车列表中所有商品数量和价格累加
    this.sum = function (cartList) {

        var total = {totalNum: 0, totalFee: 0}
        for (i = 0; i < cartList.length; i++) {
            var cart = cartList[i];
            for (j = 0; j < cart.orderItemList.length; j++) {
                total.totalNum += cart.orderItemList[j].num;
                total.totalFee += cart.orderItemList[j].totalFee;
            }
        }
        return total;

    }

    this.addToCart = function (itemId, num) {
        return $http.get('../cart/addToCart.do?itemId='+itemId+'&num='+num);
    }

    this.findAddressList = function () {
        return $http.get('../address/findAddressByUserName.do');
    }

    this.submitOrder = function (order) {
        return $http.post('../order/add.do', order)
    };

});
