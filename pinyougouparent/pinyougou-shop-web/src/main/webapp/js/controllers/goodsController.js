//控制层
app.controller('goodsController', function ($scope, $controller, $location, goodsService, uploadService, itemCatService, typeTemplateService) {

    $controller('baseController', {$scope: $scope});//继承

    //读取列表数据绑定到表单中  
    $scope.findAll = function () {
        goodsService.findAll().success(
            function (response) {
                $scope.list = response;
            }
        );
    };

    //分页
    $scope.findPage = function (page, rows) {
        goodsService.findPage(page, rows).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    };

    //查询实体
    //更新回显
    $scope.findOne = function () {
        var id = $location.search()['id'];
        if (id == null) {
            return;
        }
        goodsService.findOne(id).success(
            function (response) {
                $scope.entity = response;
                editor.html($scope.entity.tbGoodsDesc.introduction);
                $scope.entity.tbGoodsDesc.itemImages = JSON.parse($scope.entity.tbGoodsDesc.itemImages);
                $scope.entity.tbGoodsDesc.customAttributeItems = JSON.parse($scope.entity.tbGoodsDesc.customAttributeItems);
                $scope.entity.tbGoodsDesc.specificationItems = JSON.parse($scope.entity.tbGoodsDesc.specificationItems);
                for (var i = 0; i < $scope.entity.tbItemList.length; i++) {
                    $scope.entity.tbItemList[i].spec = JSON.parse($scope.entity.tbItemList[i].spec);
                }
            }
        );
    };

    //保存
    $scope.save = function () {
        //富文本编辑器中内容复制
        $scope.entity.tbGoodsDesc.introduction = editor.html();
        var serviceObject = null;
        //添加
        if ($scope.entity.tbGoods.id == null) {
            serviceObject = goodsService.add($scope.entity);
        } else {
            serviceObject = goodsService.update($scope.entity);
        }
        serviceObject.success(
            function (response) {
                if (response.success) {
                    //提示信息并清空entity
                    alert(response.message);
                    location.href = "goods.html";
                } else {
                    alert(response.message);
                }
            }
        )


    };


    //批量删除
    $scope.dele = function () {
        //获取选中的复选框
        goodsService.dele($scope.selectIds).success(
            function (response) {
                if (response.success) {
                    $scope.reloadList();//刷新列表
                    $scope.selectIds = [];
                }
            }
        );
    };

    $scope.searchEntity = {};//定义搜索对象
    //搜索
    $scope.search = function (page, rows) {
        goodsService.search(page, rows, $scope.searchEntity).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    };
    $scope.img_entity = {};
    //上传文件
    $scope.uploadFile = function () {
        uploadService.upload().success(
            function (response) {
                if (response.success) {
                    $scope.img_entity.url = response.message;
                } else {
                    alert(response.message);
                }
            }
        )
    };

    //添加图片
    $scope.entity = {};
    $scope.entity.tbGoodsDesc = {itemImages: [], specificationItems: []};
    $scope.addImgToList = function () {
        $scope.entity.tbGoodsDesc.itemImages.push($scope.img_entity)
    };

    //删除图片
    $scope.delImgFromList = function (index) {
        $scope.entity.tbGoodsDesc.itemImages.splice(index, 1);
    };

    $scope.entity.tbGoods = {};
    //展示一级分类
    $scope.findItemCatList1 = function () {
        itemCatService.findByParentId(0).success(
            function (response) {
                $scope.itemCatList1 = response;
                if ($location.search()["id"] == null) {//如果是新增
                    $scope.itemCatList2 = {};
                    $scope.itemCatList3 = {};
                    $scope.entity.tbGoods.typeTemplateId = "";
                    $scope.entity.tbGoods.brandId = "";
                    $scope.brandList = {};
                    $scope.entity.tbGoodsDesc.customAttributeItems = {};
                }

            }
        )
    };

    //展示二级分类
    $scope.$watch('entity.tbGoods.category1Id', function (newValue, oldValue) {
        if (!newValue) {
            return;
        }
        itemCatService.findByParentId(newValue).success(
            function (response) {
                //一级分类换了, 二级分类列表被更新
                //这时二级分类后的所有子属性都应被清空, 包括视图和模型
                $scope.itemCatList2 = response;
                if ($location.search()["id"] == null) {
                    $scope.itemCatList3 = {};
                    $scope.entity.tbGoods.typeTemplateId = "";
                    $scope.entity.tbGoods.brandId = "";
                    $scope.brandList = {};
                    $scope.entity.tbGoodsDesc.customAttributeItems = {};
                }
            }
        );
    });

    //展示三级分类
    $scope.$watch('entity.tbGoods.category2Id', function (newValue, oldValue) {
        if (!newValue) {
            return;
        }
        itemCatService.findByParentId(newValue).success(
            function (response) {
                $scope.itemCatList3 = response;
                // 二级分类如果变化, 三级分类列表也根据二级分类id更新
                // 在选择三级分类前, 三级分类后的子属性都应清空
                if ($location.search()["id"] == null) {
                    //清空模板id
                    $scope.entity.tbGoods.typeTemplateId = "";
                    //清空绑定的品牌id
                    $scope.entity.tbGoods.brandId = "";
                    //清空视图中展示的品牌列表
                    $scope.brandList = {};
                    //清空扩展列表, 视图和数据用的同一个对象
                    $scope.entity.tbGoodsDesc.customAttributeItems = {};
                }
            }
        )
    });

    //展示三级分类后所有子属性
    //模板ID, 品牌列表, 扩展属性, 规格列表
    $scope.$watch('entity.tbGoods.category3Id', function (newValue, oldValue) {
        if (!newValue) {
            return;
        }
        //根据三级分类id, 查询三级分类对象, 从该对象中获取对应的模板id
        itemCatService.findOne(newValue).success(
            function (response) {
                $scope.entity.tbGoods.typeTemplateId = response.typeId;
                //三级ID换了的话, 所有子属性直接更新即可
                /*
                //清空绑定的品牌id
                $scope.entity.tbGoods.brandId = "";
                //清空视图中展示的品牌列表
                $scope.brandList = {};
                //清空扩展列表, 视图和数据用的同一个对象
                $scope.entity.tbGoodsDesc.customAttributeItems = {};
                */
                // 因为模板id全是35, 所以换前面的会导致数据清空了但是因为模板id不变, 所以还是空的
                // 解决: 不监控模板id, 监控3级分类id,
                // 三级分类id变了, 品牌, 扩展属性列表 和 规格列表就变
                // 这样如果换了三级分类id,
                // 就不会出现品牌, 扩展属性都没更新的情况
                // 之前监听模板ID时, 换三级分类后模板id都不变,
                // 就会出现三级分类变了, 低级的属性都被清空,
                // 但是因为模板id没变, 不会触发监听, 新的属性只被清空, 但没被更新
                // 让他们监听3级分类id, 因为所有三级分类id都不一样, 就保证了监听一定被触发, 子属性一定会更新, 而不仅仅只是清空.

                //根据模板id, 查询品牌列表  和  扩展属性列表 , 展示品牌列表  和  扩展属性列表
                typeTemplateService.findOne(response.typeId).success(
                    function (response) {
                        if (response) {
                            //根据模板id查询模板

                            //1, 将模板中的品牌转换为js对象
                            $scope.brandList = JSON.parse(response.brandIds);

                            //2, 展示扩展属性列表
                            //[{"text":"内存大小"},{"text":"颜色"}]
                            //前端绑定entity.tbGoodsDesc.customAttributeItems.value
                            //获得完整的扩展属性键值对, 包括名字和描述, 然后存入goodDesc表
                            if ($location.search()["id"] == null) {//模板中查询回来的是没有value的, 修改时查询回来的是有value的
                                $scope.entity.tbGoodsDesc.customAttributeItems = JSON.parse(response.customAttributeItems);
                            }
                        }
                    }
                );
                //展示规格加规格列表
                //根据模板id, 获取完整spec规格列表
                //[{id:27, text:网络, options:[{optionName:'移动4G', id:98, specId:27},{...}]},{...}]
                typeTemplateService.findSpecList(response.typeId).success(
                    function (response) {
                        //这里不管是不是修改,  根据模板id返回这个选择列表
                        $scope.specOptions = response;
                    }
                )
            }
        )
    });

    //规格页面点击box
    //页面点了box, 就往$scope.entity.tbGoodsDesc.specificationItems中插入一个map
    $scope.addSpecificationItem = function ($event, attributeName, optionName) {
        //添加操作
        if ($event.target.checked) {
            //搜索list中是否已存在同名的规格map
            var map = $scope.searchForName($scope.entity.tbGoodsDesc.specificationItems, 'attributeName', attributeName);
            if (map) {
                //有同名的规格, 往这个规格map中添加optionName
                map.attributeValue.push(optionName);
            } else {
                //没有同名的规格, 往list中加这个规格map
                $scope.entity.tbGoodsDesc.specificationItems.push({
                    "attributeName": attributeName,
                    "attributeValue": [optionName]
                })
            }
        } else {
            //删除操作
            //获取map
            var map = $scope.searchForName($scope.entity.tbGoodsDesc.specificationItems, 'attributeName', attributeName);
            //删除指定元素
            map.attributeValue.splice(map.attributeValue.indexOf(optionName), 1);
            //如果map中的value数组被删空了, 就从list中直接删除整个map
            if (map.attributeValue.length == 0) {
                $scope.entity.tbGoodsDesc.specificationItems.splice(
                    $scope.entity.tbGoodsDesc.specificationItems.indexOf(map), 1);
            }
        }

    };

    //这其实是一个树状图
    //根据商品中的规格名和规格选项  创建sku表 entity.tbItemList  包括所有不同规格的组合
    //如: 所有内存和所有网络的组合 16g+2g. 16g+3g, 32g+2g, 32g+3g
    //specificationItems表结构:
    // [{"attributeName":"网络制式","attributeValue":["移动3G","移动4G"]},{"attributeName":"机身内存","attributeValue":["16G","32G"]}]
    //item表结构:
    // [{spec: {"网络":"移动3G", "机身内存":"16G"}, price: 0, num: 99999, status: '0', isDefault: '0'}, {............}]

    $scope.createItemList = function () {
        //每次更新规格选择, 都重新生成所有规格选项的组合,  并放进entity.tbItemList
        $scope.entity.tbItemList = [{spec: {}, price: 0, num: 99999, status: '0', isDefault: '0'}];
        //获取最新的规格名和选项名列表
        var specList = $scope.entity.tbGoodsDesc.specificationItems;

        //遍历规格列表
        //拿出一个规格, 对这个规格的所有规格选项, 生成所有item, 放进一个list
        //拿出第二个规格, 对这个规格的所有规格选项, 和每一个item生成所有规格选项的组合
        for (var j = 0; j < specList.length; j++) {
            //新规格表的接收变量
            var newItemList = [];
            //规格名
            var name = specList[j].attributeName;
            //规格选项集合
            var options = specList[j].attributeValue;

            //遍历sku
            //拿出一个sku, 追加所有新规格选项, 每一个新规格选项是一个新的sku
            for (var i = 0; i < $scope.entity.tbItemList.length; i++) {

                //遍历规格选项
                //拿出一个规格选项
                for (var x = 0; x < options.length; x++) {
                    //深克隆当前的sku
                    //针对这个sku, 追加当前规格的每一个规格选项
                    var newItem = JSON.parse(JSON.stringify($scope.entity.tbItemList[i]));
                    //追加规格选项
                    newItem.spec[name] = options[x];
                    //将追加规格选项后的新sku放入list
                    newItemList.push(newItem);

                }
            }
            //遍历完所有sku后, 这个规格的所有选项追加完成, 放入itemList, 进入下一个规格的遍历
            $scope.entity.tbItemList = newItemList;
        }
    };

    //封装方法的方式:
    $scope.createItemList2 = function () {
        //1, 遍历规格
        var specs = $scope.entity.tbGoodsDesc.specificationItems;
        //每次更改规格选项都清空item列表, 从头开始加, 因为删除操作太复杂, 需要删除键值对, 还要删除多余的item, 不如直接清空重来
        $scope.entity.tbItemList = [{spec: {}, price: 0, num: 99999, status: '0', isDefault: '0'}];
        for (var i = 0; i < specs.length; i++) {
            //2, 编写一个方法, 传入现在的sku表, 要增加的规格名, 要增加的规格列表
            //在方法中创建新的item, 追加所有的规格选项
            //返回新的itemList
            var specName = specs[i].attributeName;
            var specoptions = specs[i].attributeValue;
            $scope.entity.tbItemList = addColumn2($scope.entity.tbItemList, specName, specoptions);
        }

    };

    //增加sku方法
    var addColumn = function (list, spec, options) {
        var newList = [];
        //遍历list, 给每个sku追加option
        for (var i = 0; i < list.length; i++) {
            //遍历options, 每个option都要生成一个新的sku
            for (var j = 0; j < options.length; j++) {
                //每个option都要创建一个新的sku, 所以应深克隆原来的sku
                var newItem = JSON.parse(JSON.stringify(list[i]));
                //创建新的sku
                newItem.spec[spec] = options[j];
                //将新的sku放进list  最后return这个list
                newList.push(newItem);
            }
        }
        return newList;
    };

    var addColumn2 = function (list, spec, options) {
        //每一次添加一个规格  都基于之前的itemList  重新分裂成一个新itemList
        var newList = [];
        //1, 遍历options和list, 把一个option追加到每一个item里
        for (var i = 0; i < options.length; i++) {

            //2, 遍历list
            for (var j = 0; j < list.length; j++) {
                var newItem = JSON.parse(JSON.stringify(list[j]));
                newItem.spec[spec] = options[i];
                newList.push(newItem);
            }

        }
        //这个规格的每一个option都生成了一个新的sku
        return newList;
    };

    //状态信息
    $scope.auditStatus = ['未审核', '已审核', '审核未通过', '已关闭'];


    //分类信息
    $scope.catList = [];
    $scope.findCatList = function () {
        itemCatService.findAll().success(
            function (response) {
                for (var i = 0; i < response.length; i++) {
                    $scope.catList[response[i].id] = response[i].name;
                }
            }
        )
    };

    //根据规格名和选项名查询  entity中是否有这个规格选择
    $scope.checkSpecOptionSelected = function (specName, optionName) {
        var specList = $scope.entity.tbGoodsDesc.specificationItems;
        var map = $scope.searchForName(specList, 'attributeName', specName);
        if (map != null) {//如果有这个名字的规格, 则查询是否有规格选项
            if (map.attributeValue.indexOf(optionName) >= 0) {
                return true;
            }
        }
        //如果没有规格名或没有规格选项, 直接返回false
        return false;

    };

});
