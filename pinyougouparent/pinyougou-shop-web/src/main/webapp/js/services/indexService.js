app.service("indexService", function ($http) {

    this.findUserName = function () {
        return $http.get("../login/findUserName.do");
    }

})