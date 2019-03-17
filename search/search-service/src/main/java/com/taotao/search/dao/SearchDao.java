package com.taotao.search.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.taotao.common.pojo.SearchItem;
import com.taotao.common.pojo.SearchResult;

/*
 * 
 * 查询索引库商品dao
 */

@Repository   //dao层用@Repository  service层用@Service 表现层用@Controller
public class SearchDao {
	@Autowired
	private SolrServer solrServer;

	public SearchResult search(SolrQuery query) throws Exception{
		//根据query对象进行查询
		QueryResponse response = solrServer.query(query);
		//取查询结果
		SolrDocumentList solrDocumentList = response.getResults();
		//取查询结果总记录数
		long numFonud = solrDocumentList.getNumFound();
		SearchResult result = new SearchResult();
		result.setRecordCount(numFonud);
		//将查询结果封装到searchItem对象中
		List<SearchItem> list = new ArrayList<>();
		for(SolrDocument solrDocument:solrDocumentList) {
			SearchItem item = new SearchItem();
			item.setId((String)solrDocument.get("id"));
			item.setCategory_name((String)solrDocument.get("item_category_name"));
			//取一张图片
			String image = (String)solrDocument.get("item_image");
			if(StringUtils.isNotBlank(image)) {
				image = image.split(",")[0];
			}
			item.setImage(image);
			item.setItem_desc((String)solrDocument.get("item_desc"));
			item.setPrice((long)solrDocument.get("item_price"));
			item.setSell_point((String)solrDocument.get("item_sell_point"));
			//取高亮显示
			Map< String, Map<String, List<String>>> highlighting = response.getHighlighting();
			List<String> highLightList = highlighting.get(solrDocument.get("id")).get("item_title");
			String itemTitle = "";
			if(highLightList != null && highLightList.size() > 0) {
				itemTitle = highLightList.get(0);
			}else {
				itemTitle = (String)solrDocument.get("item_title");
			}
			item.setTitle(itemTitle);
			list.add(item);
		}
		//把结果添加到SearchResult中
		result.setItemList(list);
		//返回
		return result;
	}
	
}
