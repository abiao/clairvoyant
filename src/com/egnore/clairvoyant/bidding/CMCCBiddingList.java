package com.egnore.clairvoyant.bidding;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.tags.TableColumn;
import org.htmlparser.tags.TableRow;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

public class CMCCBiddingList {
	public ArrayList<BiddingInfo> biddingList = new ArrayList<BiddingInfo>();
	public String getDetail(String id) throws IOException, URISyntaxException {
		URI uri = new URI("http://b2b.10086.cn/b2b/main/viewNoticeContent.html?noticeBean.id="+id);
		return "";
	}
	public void writeStringToFile(String content, String path) throws IOException {
		File file = new File(path); // 找到File类的实例
    // 创建文件
        file.createNewFile(); 
    // 声明字符输出流
        Writer out = null; 
    // 通过子类实例化，表示可以追加
        out = new FileWriter(file,true); 
    // 写入数据
        out.write(content); 
        out.close();
	}
	public void getBiddingList() throws URISyntaxException, IOException, ParserException {
		BasicCookieStore cookieStore = new BasicCookieStore();
        CloseableHttpClient httpclient = HttpClients.custom()
                .setDefaultCookieStore(cookieStore)
                .build();
		URI uri = new URI("http://b2b.10086.cn/b2b/main/showBiao!showZhaobiaoResult.html");

		List<NameValuePair> formparams = new ArrayList<NameValuePair>();
		formparams.add(new BasicNameValuePair("page.currentPage", "1"));
		formparams.add(new BasicNameValuePair("page.perPageSize", "20000"));
		UrlEncodedFormEntity entity1 = new UrlEncodedFormEntity(formparams, Consts.UTF_8);
		HttpPost httppost = new HttpPost(uri);
		httppost.setEntity(entity1);
		CloseableHttpResponse response3 = httpclient.execute(httppost);
		try {
            HttpEntity entity = response3.getEntity();
            String result = EntityUtils.toString(entity);
            
            
            Parser parser = Parser.createParser(result, "utf-8");
            //NodeFilter filter = new TagNameFilter("tr");
            AndFilter filter = new AndFilter(new TagNameFilter("tr"), new HasAttributeFilter("onmousemove", "cursorOver(this)"));  

            Node node = null;  
            NodeList nodeList = parser.extractAllNodesThatMatch(filter);
            biddingList.clear();
            Date now = new Date();
		    Calendar c = Calendar.getInstance();  
		    //for (int i = 0; i < 10; ++i) {  
	        for (int i = 0; i < nodeList.size(); ++i) {  
              node = nodeList.elementAt(i);
              TableRow r = (TableRow) node;
              String onclick = r.getAttribute("onclick");
              //System.out.println(onclick);
              String id = onclick.substring(onclick.indexOf("(") + 2, onclick.indexOf(")") - 1);
              
              TableColumn n = null;
              String projectName = null;
              boolean isFirst = true;

              BiddingInfo b = new BiddingInfo();
              
              for (int j = 0; j < node.getChildren().size(); ++j) {
            	  if (node.getChildren().elementAt(j) instanceof org.htmlparser.tags.TableColumn) {
            		  n = (TableColumn)node.getChildren().elementAt(j);
            		  if (isFirst) {
            			  for (int k = 0; k < n.getChildren().size(); ++k) {
            				  if (n.getChildren().elementAt(k) instanceof org.htmlparser.tags.LinkTag) {
            					  org.htmlparser.tags.LinkTag t = (org.htmlparser.tags.LinkTag) n.getChildren().elementAt(k);
            					  b.projectName = t.getAttribute("title");
            					  break;
            				  }
            			  }
            			  if ((b.projectName == null) || (b.projectName == "null") || b.projectName.isEmpty()) b.projectName = n.toPlainTextString().trim();
              			  isFirst = false;
            		  } else {
            			  String[] ss = n.toPlainTextString().trim().split("还有|天|时|分");
            			  c.setTime(now);   //设置当前日期  
            		      c.add(Calendar.DATE, Integer.parseInt(ss[1]));  
            		      c.add(Calendar.HOUR, Integer.parseInt(ss[2]));  
            		      c.add(Calendar.MINUTE, Integer.parseInt(ss[3]));  
            		      b.deadlineDate = c.getTime();  
            		  }
            	  }
            	  //System.out.println(node.getChildren().elementAt(j).getClass().getName());
              }
              b.id = id;
              biddingList.add(b);
              
              //System.out.println(node.getChildren().elementAt(0));
//              System.out.println(node.getClass().getName() + " " + id);
            		  
            }
	        DateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	        for (int i = 0; i < biddingList.size(); i++) {
            	BiddingInfo bb = biddingList.get(i);
            	System.out.println(bb.id + "=" + bb.projectName + ":" + format.format(bb.deadlineDate));
            }
        } finally {
            response3.close();
        }
	}

	public void getData() throws URISyntaxException, IOException, ParserException {
		BasicCookieStore cookieStore = new BasicCookieStore();
        CloseableHttpClient httpclient = HttpClients.custom()
                .setDefaultCookieStore(cookieStore)
                .build();
		URI uri = new URI("http://b2b.10086.cn/b2b/main/showBiao!showZhaobiaoResult.html");
		 
		List<NameValuePair> formparams = new ArrayList<NameValuePair>();
		formparams.add(new BasicNameValuePair("page.currentPage", "1"));
		formparams.add(new BasicNameValuePair("page.perPageSize", "20000"));
		UrlEncodedFormEntity entity1 = new UrlEncodedFormEntity(formparams, Consts.UTF_8);
		HttpPost httppost = new HttpPost(uri);
		httppost.setEntity(entity1);
		CloseableHttpResponse response3 = httpclient.execute(httppost);

		try {
            HttpEntity entity = response3.getEntity();
            String result = EntityUtils.toString(entity);
            
            
            Parser parser = Parser.createParser(result, "utf-8");
            //NodeFilter filter = new TagNameFilter("tr");
            AndFilter filter = new AndFilter(new TagNameFilter("tr"),   
              new HasAttributeFilter("onmousemove", "cursorOver(this)"));  
            
            Node node = null;  
            NodeList nodeList = parser.extractAllNodesThatMatch(filter);  
            for (int i = 0; i < nodeList.size(); ++i) {  
              node = nodeList.elementAt(i);
              TableRow r = (TableRow) node;
              String onclick = r.getAttribute("onclick");
              System.out.println(onclick);
              String id = onclick.substring(onclick.indexOf("(") + 2, onclick.indexOf(")") - 1);
              System.out.println(node.getClass().getName() + " " + id);
              //for( Node n : node.getChildren()） ｛
            		  
              System.out.println(node.toPlainTextString());
              //sb.append(node.toPlainTextString());  
            }  
            
            
       //     writeStringToFile(result, "/Users/biaochen/b.txt");
            
            if (entity != null) {
                long len = entity.getContentLength();
                if (len != -1 && len < 2048) {
                    System.out.println(EntityUtils.toString(entity));
                } else {
                    // Stream content out
                }
            }
        } finally {
            response3.close();
        }
 /*       try {
            HttpEntity entity = response2.getEntity();

            System.out.println("Login form get: " + response2.getStatusLine());
            EntityUtils.consume(entity);

            System.out.println("Post logon cookies:");
            List<Cookie> cookies = cookieStore.getCookies();
            if (cookies.isEmpty()) {
                System.out.println("None");
            } else {
                for (int i = 0; i < cookies.size(); i++) {
                    System.out.println("- " + cookies.get(i).toString());
                }
            }
        } finally {
            response2.close();
        }		//post.setURI(uri);
	*/	/*post.set
		$.ajax({
			type : "POST",
			url : url,
			cache : false,
			processData : true,
			data : formData,
			success : function(responseData) {
				$("#searchResult").html(responseData);
				if ($("#totalRecordNum").val() == 0) {
					var msg="<font color='red' size='3em'>查无结果！</font>";
					$("#searchResult").html(msg);
				} else {
					$("#searchResult").html(responseData);
				}
			}
		});
		*/

	}
}
