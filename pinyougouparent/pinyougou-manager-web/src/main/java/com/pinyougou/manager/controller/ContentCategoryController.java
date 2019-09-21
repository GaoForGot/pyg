package com.pinyougou.manager.controller;
import java.util.List;

import com.pinyougou.content.service.ContentCategoryService;
import entities.Message;
import entities.PageResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbContentCategory;

/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/contentCategory")
public class ContentCategoryController {

	@Reference
	private ContentCategoryService contentCategoryService;
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbContentCategory> findAll(){			
		return contentCategoryService.findAll();
	}
	
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findPage")
	public PageResult findPage(int page, int rows){
		return contentCategoryService.findPage(page, rows);
	}
	
	/**
	 * 增加
	 * @param contentCategory
	 * @return
	 */
	@RequestMapping("/add")
	public Message add(@RequestBody TbContentCategory contentCategory){
		try {
			contentCategoryService.add(contentCategory);
			return new Message(true, "增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Message(false, "增加失败");
		}
	}
	
	/**
	 * 修改
	 * @param contentCategory
	 * @return
	 */
	@RequestMapping("/update")
	public Message update(@RequestBody TbContentCategory contentCategory){
		try {
			contentCategoryService.update(contentCategory);
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
	public TbContentCategory findOne(Long id){
		return contentCategoryService.findOne(id);		
	}
	
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public Message delete(Long [] ids){
		try {
			contentCategoryService.delete(ids);
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
	public PageResult search(@RequestBody TbContentCategory contentCategory, int page, int rows  ){
		return contentCategoryService.findPage(contentCategory, page, rows);		
	}
	
}
