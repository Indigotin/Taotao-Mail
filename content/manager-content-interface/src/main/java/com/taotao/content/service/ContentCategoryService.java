package com.taotao.content.service;

import java.util.List;

import com.taotao.common.pojo.EasyUITreeNode;
import com.taotao.common.pojo.TaotaoResult;

public interface ContentCategoryService {
	
	List<EasyUITreeNode> getContentCategoryList(long parentId);
	TaotaoResult addContentCategory(long parentId, String name);
	void updateContentCategory(long id, String name);
	TaotaoResult deleteContentCategory(long id);
}
