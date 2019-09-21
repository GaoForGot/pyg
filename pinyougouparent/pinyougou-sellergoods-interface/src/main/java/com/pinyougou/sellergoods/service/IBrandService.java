package com.pinyougou.sellergoods.service;

import com.pinyougou.pojo.TbBrand;
import entities.PageResult;

import java.util.List;
import java.util.Map;

public interface IBrandService {

    /**
     * 查询所有品牌
     * @return
     */
    List<TbBrand> findAll();

    /**
     * 分页查询品牌
     * @param pageNum
     * @param pageSize
     * @return
     */
    PageResult findPage(Integer pageNum, Integer pageSize);

    /**
     * 添加品牌
     * @param brand
     */
    void add(TbBrand brand);

    /**
     * 根据id查询品牌, for修改的回显
     * @param id
     * @return
     */
    TbBrand findById(Long id);

    /**
     * 根据id修改品牌
     * @param brand
     */
    void update(TbBrand brand);

    /**
     * 根据id刪除品牌
     * @param id
     */
    void delete(Long id);

    /**
     * 根据条件查询
     * @param brand
     * @param pageNum
     * @param pageSize
     * @return
     */
    PageResult findByConditions(TbBrand brand, Integer pageNum, Integer pageSize);

    List<Map> findOptionsList();
}
