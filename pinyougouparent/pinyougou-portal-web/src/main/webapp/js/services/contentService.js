app.service("contentService", function ($http) {
    this.findByCatId=function (catId) {
        return $http.get("content/findByCatId.do?catId=" + catId);
    }
})