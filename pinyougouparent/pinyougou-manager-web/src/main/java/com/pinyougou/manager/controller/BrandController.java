package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.sellergoods.service.IBrandService;
import entities.Message;
import entities.PageResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/brand")
public class BrandController {

    @Reference
    private IBrandService brandService;

    @RequestMapping("/findAll")
    public List<TbBrand> findAll() {
        return brandService.findAll();
    }

    /**
     * 品牌分页查询
     * @param pageNum 要查询的页数
     * @param pageSize 要查询的每页条数
     * @return 封装的分页数据, 包括数据总数和查询结果
     */
    @RequestMapping("/findPage")
    public PageResult findPage(@RequestParam(name = "page") Integer pageNum,
                               @RequestParam(name = "size") Integer pageSize) {
        return brandService.findPage(pageNum, pageSize);
    }

    /**
     * 添加品牌
     * @param brand
     * @return
     */
    @RequestMapping("/add")
    public Message add(@RequestBody TbBrand brand) {
        try {
            brandService.add(brand);
            return new Message(true,"添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Message(false,"添加失败");
        }
    }

    @RequestMapping("/findOne")
    public TbBrand findById(@RequestParam Long id) {
        return brandService.findById(id);
    }

    @RequestMapping("/update")
    public Message update(@RequestBody TbBrand brand) {
        try {
            brandService.update(brand);
            return new Message(true,"修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Message(false,"修改失败");
        }
    }

    @RequestMapping("/delete")
    public Message delete(Long[] ids) {
        try {
            for (Long id : ids) {
                brandService.delete(id);
            }
            return new Message(true,"刪除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Message(false,"刪除失败");
        }
    }

    @RequestMapping("/search")
    public PageResult search(@RequestBody TbBrand brand,
                             @RequestParam(name = "page") Integer pageNum,
                             @RequestParam(name = "size") Integer pageSize) {
        return brandService.findByConditions(brand,pageNum,pageSize);

    }

    @RequestMapping("/findOptionsList")
    public List<Map> findOptionsList() {
        return brandService.findOptionsList();
    }

}
