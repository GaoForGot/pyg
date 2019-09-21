package com.pinyougou.manager.controller;
import java.util.List;

import com.pinyougou.sellergoods.service.ItemCatService;
import entities.Message;
import entities.PageResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbItemCat;
/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/itemCat")
public class ItemCatController {

	@Reference
	private ItemCatService itemCatService;
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbItemCat> findAll(){			
		return itemCatService.findAll();
	}
	
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findPage")
	public PageResult findPage(int page, int rows){			
		return itemCatService.findPage(page, rows);
	}
	
	/**
	 * 增加
	 * @param itemCat
	 * @return
	 */
	@RequestMapping("/add")
	public Message add(@RequestBody TbItemCat itemCat){
		try {
			itemCatService.add(itemCat);
			return new Message(true, "增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Message(false, "增加失败");
		}
	}
	
	/**
	 * 修改
	 * @param itemCat
	 * @return
	 */
	@RequestMapping("/update")
	public Message update(@RequestBody TbItemCat itemCat){
		try {
			itemCatService.update(itemCat);
			return new Message(true, "修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Message(false, "修改失败");
		}
	}	
	
	/**
	 * 获取实体
	 * @param id
	 * @return
	 */
	@RequestMapping("/findOne")
	public TbItemCat findOne(Long id){
		return itemCatService.findOne(id);		
	}
	
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public Message delete(Long [] ids){
		try {
			itemCatService.delete(ids);
			return new Message(true, "删除成功"); 
		} catch (Exception e) {
			e.printStackTrace();
			return new Message(false, "删除失败");
		}
	}
	
	/**
	 * 查询+分页
	 * @param
	 * @param page
	 * @param rows
	 * @return
	 */
	@RequestMapping("/search")
	public PageResult search(@RequestBody TbItemCat itemCat, int page, int rows  ){
		return itemCatService.findPage(itemCat, page, rows);		
	}

	/**
	 * 根据父ID获取
	 * @param parentId 前端传来的父ID
	 * @return 拥有同一个父ID的分类的集合
	 */
	@RequestMapping("/findByParentId")
	public List<TbItemCat> findByParentId(Long parentId) {
		return itemCatService.findByParentId(parentId);
	}
	
}
