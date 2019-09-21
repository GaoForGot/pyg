app.service("brandService", function ($http) {

    this.findAll = function () {
        return $http.get("../brand/findAll.do");
    }

    this.findPage = function (page,size) {
        return $http.get("../brand/findPage.do?page=" + page + "&size=" + size);
    }

    this.add = function (brand) {
        return $http.post("../brand/add.do", brand);
    }

    this.findOne = function (id) {
        return $http.get("../brand/findOne.do?id=" + id);
    }

    this.update = function (brand) {
        return $http.post("../brand/update.do", brand)

    };

    this.del = function (ids) {
        return $http.get("../brand/delete.do?ids=" + ids)
    }

    this.search = function (page,size,brand) {
        return $http.post("../brand/search.do?page=" + page + "&size=" + size, brand)
    }

    this.findOptionsList = function () {
        return $http.get("../brand/findOptionsList.do");
    };
})