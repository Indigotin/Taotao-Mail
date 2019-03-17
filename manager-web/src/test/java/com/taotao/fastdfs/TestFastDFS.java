package com.taotao.fastdfs;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.csource.common.MyException;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.StorageServer;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.junit.Test;

import com.taotao.utils.FastDFSClient;

public class TestFastDFS {

	@Test
	public void uploadFile() throws FileNotFoundException, IOException, MyException {
		
		//1、向工程中添加jar包
		//2、创建一个配置文件，配置tracker服务器的地址
		//3、加载配置文件
		ClientGlobal.init("D:\\eclipse-workspace\\workspace\\manager-web\\src\\main\\resources\\resource\\client.conf");
		//4、创建一个trackerClient对象
		TrackerClient trackerClient = new TrackerClient();
		//5、使用TrackerClient获得trackerServer对象
		TrackerServer trackerServer = trackerClient.getConnection();
		//6、创建一个StorageServer的引用null就可以
		StorageServer storageServer = null;
		//7、创建一个storageClient对象。trackerserver，storageserver两个对象
		StorageClient storageClient = new StorageClient(trackerServer,storageServer);
		//8、使用storageClient对象上传文件
		String[] strings = storageClient.upload_file("D:/1.png","png",null);
		for (String string : strings) {
			System.out.println(string);
		}
	}
	
	//测试已经写好的工具类
	@Test
	public void testFastDfsClient() throws Exception {
		FastDFSClient fastDFSClient = new FastDFSClient("D:\\\\eclipse-workspace\\\\workspace\\\\manager-web\\\\src\\\\main\\\\resources\\\\resource\\\\client.conf");
		String string = fastDFSClient.uploadFile("D:/1.png");
		System.out.println(string);
	}
}
