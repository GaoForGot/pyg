package com.pinyougou.shop.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.grouppojo.Goods;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbGoodsExample;
import com.pinyougou.sellergoods.service.GoodsService;
import entities.Message;
import entities.PageResult;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {

	@Reference
	private GoodsService goodsService;
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbGoods> findAll(){
		return goodsService.findAll();
	}
	
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findPage")
	public PageResult findPage(int page, int rows){
		return goodsService.findPage(page, rows);
	}
	
	/**
	 * 增加
	 * @param goods
	 * @return
	 */
	@RequestMapping("/add")
	public Message add(@RequestBody Goods goods){
		//设置sellerId为当前登录的商家id
		String name = SecurityContextHolder.getContext().getAuthentication().getName();
		goods.getTbGoods().setSellerId(name);
		try {
			goodsService.add(goods);
			return new Message(true, "增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Message(false, "增加失败");
		}
	}
	
	/**
	 * 修改
	 * @param goods
	 * @return
	 */
	@RequestMapping("/update")
	public Message update(@RequestBody Goods goods){
		//商品原本的商家id
		Long goodsId = goods.getTbGoods().getId();
		Goods goods2 = goodsService.findOne(goodsId);
		String sellerId1 = goods2.getTbGoods().getSellerId();
		//前端传来的商家id
		String sellerId2 = goods.getTbGoods().getSellerId();
		//当前登录的商家id
		String sellerId3 = SecurityContextHolder.getContext().getAuthentication().getName();
		//三个商家id都相等才能执行修改
		if (sellerId1.equals(sellerId2) && sellerId1.equals(sellerId3) && sellerId2.equals(sellerId3)) {
			try {
				goodsService.update(goods);
				return new Message(true, "修改成功");
			} catch (Exception e) {
				e.printStackTrace();
				return new Message(false, "修改失败");
			}
		} else {
			return new Message(false, "非法操作");
		}

	}	
	
	/**
	 * 获取实体
	 * @param id
	 * @return
	 */
	@RequestMapping("/findOne")
	public Goods findOne(Long id){
		return goodsService.findOne(id);		
	}
	
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public Message delete(Long [] ids){
		try {
			goodsService.delete(ids);
			return new Message(true, "删除成功"); 
		} catch (Exception e) {
			e.printStackTrace();
			return new Message(false, "删除失败");
		}
	}

	/**
	 * 查询+分页
	 *
	 * @param
	 * @param page
	 * @param rows
	 * @return
	 */
	@RequestMapping("/search")
	public PageResult search(@RequestBody TbGoods goods, int page, int rows) {
		String name = SecurityContextHolder.getContext().getAuthentication().getName();
		goods.setSellerId(name);
		return goodsService.findPage(goods, page, rows);
	}
	
}
