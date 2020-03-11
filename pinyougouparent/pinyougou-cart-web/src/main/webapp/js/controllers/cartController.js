//控制层
app.controller('cartController' ,function($scope,cartService){

    //查询购物车列表
    $scope.findCartList = function () {
        cartService.findCartList().success(
            function (response) {
                $scope.cartList = response.cartList;
                $scope.total = cartService.sum($scope.cartList);
                if (!response.success) {
                    alert(response.message);
                }
            }
        )
    };

    //添加商品到购物车
    $scope.addToCart = function (itemId, num) {
        cartService.addToCart(itemId, num).success(
            function (response) {
                if (response.success) {
                    $scope.findCartList();
                } else {
                    alert(response.message);
                }
            }
        );

    };

    //查询地址列表
    $scope.findAddressList = function () {
        cartService.findAddressList().success(
            function (response) {
                $scope.addressList = response;
                for (var i = 0; i < $scope.addressList.length; i++) {
                    if ($scope.addressList[i].isDefault == '1') {
                        $scope.address = $scope.addressList[i];
                        break;
                    }
                }
            }
        );

    };

    //选择地址
    $scope.selectAddress=function (address) {
        $scope.address = address;
    }

    //选择地址样式更改
    $scope.isAddressSelected=function (address) {
        if (address == $scope.address) {
            return true;
        } else {
            return false;
        }
    };

    //订单对象初始化, 支付方式默认为微信支付(1)
    $scope.order={paymentType: '1'};

    //选择支付方式
    $scope.selectPaymentType = function (paymentType) {
        $scope.order.paymentType = paymentType;

    };

    //提交订单
    $scope.submitOrder = function () {
        //补全order对象
        $scope.order.receiverAreaName = $scope.address.address;//地址
        $scope.order.receiverMobile = $scope.address.mobile;//手机号
        $scope.order.receiver = $scope.address.contact;//收件人姓名
        cartService.submitOrder($scope.order).success(
            function (response) {
                if (response.success) {
                    if ($scope.order.paymentType == '1') {
                        location.href = "pay.html";
                    } else {
                        location.href = "paysuccess.html";
                    }
                } else {
                    alert(response.message);
                }
            }

        )

    };

});
