package com.taotao.search.mapper;

import java.util.List;

import com.taotao.common.pojo.SearchItem;

public interface SearchItemMapper {

	//要将所有的商品导入到索引库中 mapper的ID是方法名
	List<SearchItem> getItemList();
	SearchItem getItemById(long itemId);
}
