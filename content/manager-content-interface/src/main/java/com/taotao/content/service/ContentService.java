package com.taotao.content.service;

import java.util.List;

import com.taotao.common.pojo.EasyUIDataGridResult;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.pojo.TbContent;

public interface ContentService {

	List<TbContent> getContentList(long categoryId);
	TaotaoResult addContent(TbContent tbContent);
	TaotaoResult updateContent(TbContent tbContent);
	TaotaoResult deleteContent(String ids);
}
