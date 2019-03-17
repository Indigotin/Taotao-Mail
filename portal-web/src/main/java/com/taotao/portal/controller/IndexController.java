package com.taotao.portal.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.taotao.common.utils.JsonUtils;
import com.taotao.content.service.ContentService;
import com.taotao.pojo.TbContent;
import com.taotao.portal.pojo.AD1Node;

/**
 * 首页展示
 * @author Len
 *
 */

@Controller
public class IndexController {
	 
	@Value("${AD1_CATEGORY_ID}")
	private long AD1_CATEGORY_ID;
	@Value("${AD1_WIDTH}")
	private Integer AD1_WIDTH;
	@Value("${AD1_WIDTH_B}")
	private Integer AD1_WIDTH_B;
	@Value("${AD1_HEIGH}")
	private Integer AD1_HEIGH;
	@Value("${AD1_HEIGH_B}")
	private Integer AD1_HEIGH_B;
	
	@Autowired
	private ContentService contentService;
	
	@RequestMapping("/index")
	public String showIndex(Model model) {
		//获取查询列表轮播图
		List<TbContent> contentList = contentService.getContentList(AD1_CATEGORY_ID);
		//将查询列表转化成AD1Node
		List<AD1Node> AD1List = new ArrayList<>();
		for(TbContent temp:contentList) {
			AD1Node node = new AD1Node();
			node.setSrc(temp.getPic());
			node.setSrcB(temp.getPic2());
			node.setHref(temp.getUrl());
			node.setAlt(temp.getTitle());
			node.setHeight(AD1_HEIGH);
			node.setHeightB(AD1_HEIGH_B);
			node.setWidth(AD1_WIDTH);
			node.setWidthB(AD1_WIDTH_B);
			//添加到节点列表
			AD1List.add(node);
		}
		//封装成json数据
		String ad1Json = JsonUtils.objectToJson(AD1List);
		//将Json数据传递给页面
		model.addAttribute("ad1",ad1Json);
		return "index";
	}
}

