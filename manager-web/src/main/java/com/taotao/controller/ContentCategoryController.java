package com.taotao.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.taotao.common.pojo.EasyUITreeNode;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.content.service.ContentCategoryService;

@Controller
public class ContentCategoryController {

	@Autowired
	private ContentCategoryService contentCategoryService;
	
	@RequestMapping("/content/category/list")
	@ResponseBody
	public List<EasyUITreeNode> getContentCaterogyList(
			@RequestParam(value="id",defaultValue="0")long parentId){
		
		List<EasyUITreeNode> list = contentCategoryService.getContentCategoryList(parentId);
		return list;
	}
	
	@RequestMapping("/content/category/create")
	@ResponseBody
	public TaotaoResult addContentCategory(long parentId, String name) {
		TaotaoResult result = contentCategoryService.addContentCategory(parentId,name);
		return result;
	}
	
	@RequestMapping("/content/category/update")
	public TaotaoResult updateContentCategory(long id, String name) {
		contentCategoryService.updateContentCategory(id, name);
		return TaotaoResult.ok();
	}
	
	@RequestMapping("/content/category/delete")
	public TaotaoResult deleteContentCategory(long id) {
		System.out.println("delete id = "+id);
		TaotaoResult result = contentCategoryService.deleteContentCategory(id);
		System.out.println("deleteContentCategory执行完毕");
		return result;
	}
	
	
}
