app.controller('payController', function ($scope,$location, payService) {

    //创建支付, 并查询支付状态
    $scope.createNative = function () {
        payService.createNative().success(
            function (response) {
                $scope.totalFee = response.total_fee / 100;//订单金额
                $scope.outTradeNo = response.out_trade_no;//订单号
                //生成二维码
                var qr = new QRious({
                    element: document.getElementById("qrious"),
                    size: 250,
                    level: "M",
                    value: response.code_url
                });
                queryPayStatus(response.out_trade_no);//开始查询订单状态, 后台每三秒查询一次

            }
        );
    };

    //查询支付状态
    queryPayStatus = function (out_trade_no) {
        payService.queryPayStatus(out_trade_no).success(
            function (response) {
                if (response.success) {//支付成功
                    //将支付金额通过地址栏传参给支付成功页面
                    location.href = "paysuccess.html#?money=" + $scope.totalFee;
                } else {
                    if (response.message == "支付超时") {//支付超时
                        //刷新二维码
                        $scope.createNative();
                    } else {
                        location.href = "payfail.html";
                    }

                }
            }
        )

    };

    //支付成功后, 跳转到支付成功页面展示支付金额
    $scope.queryPayTotalFee = function () {
        return $location.search()['money'];
    }


});
