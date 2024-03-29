package com.taotao.solrj;


import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Test;

public class TestSolrJ {
	/*	
	@Test
	public void testAddDocument() throws Exception{
		//创建一个solrServer对象。创建一个HttpSolrServer对象
		//需要指定solr服务的url
		SolrServer solrServer = new HttpSolrServer("http://192.168.25.130:8080/solr/collection1");
		//创建一个文档对象SolrInputDocument
		SolrInputDocument document = new SolrInputDocument();
		//向文档中添加域，必须有id域，域的名称必须在schema.xml中定义
		document.addField("id", "test001");
		document.addField("item_category_name", "test001");
		document.addField("item_title", "测试商品1");
		document.addField("item_image", "test001");
		document.addField("item_price", 1000);
		document.addField("item_desc", "test001");
		document.addField("item_sell_point", "test001");
		//把文档对象写入索引库
		solrServer.add(document);
		//提交
		solrServer.commit();
	}

	@Test
	public void deleteDocumentById() throws Exception{
		SolrServer solrServer = new HttpSolrServer("http://192.168.25.130:8080/solr/collection1");
		solrServer.deleteById("test001");
		//提交
		solrServer.commit(); 
	}
	
	@Test
	public void deleteDocumentByQery() throws Exception{
		SolrServer solrServer = new HttpSolrServer("http://192.168.25.130:8080/solr/collection1");
		solrServer.deleteByQuery("id:123");
		//提交
		solrServer.commit();
	}	*/

	@Test
	public void searchDocument() throws Exception{
		//创建一个solrServer的对象
		SolrServer solrServer = new HttpSolrServer("http://192.168.25.128:8080/solr/collection1");
		//创建一个solrQuery查询对象
		SolrQuery query = new SolrQuery();
		//设置查询条件，过滤条件 分页条件 排序条件 设置高亮
		//query.set("q", "*:*");
		query.setQuery("手机");
		//分页条件
		query.setStart(30);
		query.setRows(20);
		//设置默认搜索域
		query.set("df","item_keywords");
		//设置高亮
		query.setHighlight(true);
		//设置高亮显示的域
		query.addHighlightField("item_title");
		query.setHighlightSimplePre("<em>");
		query.setHighlightSimplePost("</em>");
		//执行查询 得到一个response对象
		QueryResponse response = solrServer.query(query);
		//取查询结果
		SolrDocumentList solrDocumentList = response.getResults();
		//取查询结果总记录数
		System.out.println("查询结果总记录数"+solrDocumentList.getNumFound());
		for(SolrDocument solrDocument : solrDocumentList) {
			System.out.println("id= "+solrDocument.get("id"));
			//取高亮显示
			Map< String, Map<String, List<String>>> highlighting = response.getHighlighting();
			List<String> list = highlighting.get(solrDocument.get("id")).get("item_title");
			String itemTitle = "";
			if(list != null && list.size() > 0) {
				itemTitle = list.get(0);
			}else {
				itemTitle = (String)solrDocument.get("item_title");
			}
			System.out.println("标题 = "+itemTitle);
			System.out.println("卖点= "+solrDocument.get("item_sell_point"));
			System.out.println("价格= "+solrDocument.get("item_price"));
			System.out.println("图片= "+solrDocument.get("item_image"));
			System.out.println("分类名称= "+solrDocument.get("item_category_name"));
			System.out.println("===================================");
		}
	}
	
}
