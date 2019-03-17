package com.taotao.cart.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.taotao.common.pojo.TaotaoResult;
import com.taotao.common.utils.CookieUtils;
import com.taotao.common.utils.JsonUtils;
import com.taotao.pojo.TbItem;
import com.taotao.service.ItemService;

/*
 * 
 * 购物车管理Controller
 */
@Controller
class CartController {

	@Value("${CART_KEY}")
	private String CART_KEY;
	@Autowired
	private ItemService itemService;
	@Value("${CART_EXPIER}")
	private Integer CART_EXPIER;
	
	@RequestMapping("/cart/add/{itemId}")
	public String addItemCart(@PathVariable Long itemId,
			@RequestParam(defaultValue = "1")Integer num,
			HttpServletRequest request,HttpServletResponse response) {
		
		//取购物车商品列表
		List <TbItem> list = getCartItemList(request);
		//判断商品在购物车中是否存在
		boolean flag = false;
		for(TbItem tbItem:list) {
			//Long id;Long是对象，对象不能直接比较，直接比较比较的是内存地址
			if(tbItem.getId() == itemId.longValue()) {
				//如果存在，数量相加
				tbItem.setNum(tbItem.getNum()+num);
				flag = true;
				break;
			}
		}
		//如果不存在，添加一个新的商品
		if(!flag) {
			//需要调用服务取商品信息
			TbItem tbItem = itemService.getItemById(itemId);
			//设置购买的商品数量,num作为购买数量储存
			tbItem.setNum(num);
			//取一张图片
			String image = tbItem.getImage();
			if(StringUtils.isNotBlank(image)) {
				tbItem.setImage(image.split(",")[0]);
			}
			//把商品添加到购物车
			list.add(tbItem);
		}
		//把购物车列表写入cookie
		CookieUtils.setCookie(request, response, CART_KEY, JsonUtils.objectToJson(list), CART_EXPIER, true);
		//返回添加成功页面
		return "cartSuccess";
	}
	
	private List<TbItem> getCartItemList(HttpServletRequest request){
		
		//从cookie中取购物车商品列表,需要转码，明文存储不安全
		String json = CookieUtils.getCookieValue(request, CART_KEY, true); 
		if(StringUtils.isBlank(json)) {
			//如果没有内容返回一个空的list
			return new ArrayList<>();
		}
		//不为空的话返回list
		List<TbItem> list = JsonUtils.jsonToList(json, TbItem.class);
		return list;
	}
	
	@RequestMapping("/cart/cart")
	public String showCartList(HttpServletRequest request) {
		String json = CookieUtils.getCookieValue(request, CART_KEY, true); 
		List<TbItem> cartList = JsonUtils.jsonToList(json, TbItem.class);
		request.setAttribute("cartList",cartList);
		return "cart";
	}
	
	@RequestMapping("/cart/update/num/{itemId}/{num}")
	@ResponseBody
	public TaotaoResult updateItemNum(HttpServletRequest request,HttpServletResponse response,
			@PathVariable Long itemId,@PathVariable Integer num) {
		//从cookie中取购物车商品列表
		String json = CookieUtils.getCookieValue(request, CART_KEY, true); 
		List<TbItem> cartList = JsonUtils.jsonToList(json, TbItem.class);
		//遍历商品列表，找到当前商品
		for(TbItem tbItem:cartList) {
			if(tbItem.getId() == itemId.longValue()) {
				tbItem.setNum(num);
				break;
			}
		}
		//把购物车列表写入cookie
		CookieUtils.setCookie(request, response, CART_KEY, JsonUtils.objectToJson(cartList), CART_EXPIER, true);
		return TaotaoResult.ok();
	}
	
	@RequestMapping("/cart/delete/{itemId}")
	public String deleteCartItem(@PathVariable Long itemId,
			HttpServletRequest request,HttpServletResponse response) {
		//从cookie中取购物车商品列表
		String json = CookieUtils.getCookieValue(request, CART_KEY, true); 
		List<TbItem> cartList = JsonUtils.jsonToList(json, TbItem.class);
		//遍历商品列表，找到当前商品
		for(TbItem tbItem:cartList) {
			if(tbItem.getId() == itemId.longValue()) {
				cartList.remove(tbItem);
				break;
			}
		}
		//把购物车列表写入cookie
		CookieUtils.setCookie(request, response, CART_KEY, JsonUtils.objectToJson(cartList), CART_EXPIER, true);
		//重定向到购物车列表页面
		return "redirect:/cart/cart.html";
	}
	
	
}
