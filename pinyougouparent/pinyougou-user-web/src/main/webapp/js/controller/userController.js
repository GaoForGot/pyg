 //控制层
app.controller('userController' ,function($scope,$controller   ,userService){

    $scope.register=function () {
        if ($scope.entity.password!=$scope.repeatPassword) {

            alert("两次输入密码不一致, 请重新输入");

            $scope.entity.password = "";
            $scope.repeatPassword = "";

            return;
        }

        userService.add($scope.code,$scope.entity).success(function (response) {
            if (response.success) {
                alert(response.message);
            } else {
                alert(response.message)
            }
        })
    }

    $scope.createCode = function () {
        userService.createCode($scope.entity.phone).success(
            function (response) {
                alert(response.message);
            }
        )

    };

    
});	
