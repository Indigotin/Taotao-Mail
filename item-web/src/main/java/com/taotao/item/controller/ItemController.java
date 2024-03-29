package com.taotao.item.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.taotao.item.pojo.Item;
import com.taotao.pojo.TbItem;
import com.taotao.pojo.TbItemDesc;
import com.taotao.service.ItemService;

/*
 * 
 * 商品详情页面展示
 */
@Controller
public class ItemController {

	
	@Autowired
	private ItemService itemService;
	
	@RequestMapping("/item/{itemId}")
	public String showItem(@PathVariable Long itemId,Model model) {
		//取商品基本信息
		TbItem tbItem = itemService.getItemById(itemId);
		TbItemDesc itemDesc = itemService.getItemDescById(itemId);
		//去商品详情
		Item item = new Item(tbItem);
		model.addAttribute("item",item);
		model.addAttribute("itemDesc",itemDesc);
		return "item";
	}
}
