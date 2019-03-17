package com.taotao.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.taotao.common.pojo.EasyUIDataGridResult;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.content.service.ContentService;
import com.taotao.pojo.TbContent;

@Controller
public class ContentController {
	
	@Autowired
	private ContentService contentService;
	
	@RequestMapping("/content/query/list")
	@ResponseBody
	public EasyUIDataGridResult getgetContentList(long categoryId,int page,int rows) {
		List<TbContent> list = contentService.getContentList(categoryId);
		//设置分页信息
		PageHelper.startPage(page, rows);
		//取查询结果
		PageInfo<TbContent> pageInfo = new PageInfo<>(list);
		EasyUIDataGridResult result = new EasyUIDataGridResult();
		result.setRows(list);
		result.setTotal(pageInfo.getTotal());
		return result;
	}
	
	@RequestMapping("/content/save")
	@ResponseBody
	public TaotaoResult addContent(TbContent tbContent) {
		TaotaoResult result = contentService.addContent(tbContent);
		return result;
	}
	
	@RequestMapping("/rest/content/edit")
	@ResponseBody
	public TaotaoResult updateContent(TbContent tbContent) {
		TaotaoResult result = contentService.updateContent(tbContent);
		return result;
	}
	
	@RequestMapping("/content/delete")
	@ResponseBody
	public TaotaoResult deleteContent(String ids) {
		System.out.println(ids);
		TaotaoResult result = contentService.deleteContent(ids);
		return result;
	}

}
