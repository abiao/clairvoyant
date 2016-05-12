package com.egnore.clairvoyant.bidding;

import java.io.IOException;
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
import org.apache.http.ParseException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
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
import org.htmlparser.tags.LinkTag;
import org.htmlparser.tags.TableColumn;
import org.htmlparser.tags.TableRow;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import com.egnore.clairvoyant.util.TableColumnIterator;

public class ChinaBiddingList {
	public List<ChinaBidding> oldBiddings = new ArrayList<ChinaBidding>();
	public List<ChinaBidding> newBiddings   = new ArrayList<ChinaBidding>();
	public Date lastUpdateDate;
	static protected String HISTORY_FILE = "oldbiddings";

	public void saveBiddings() {
		
	}

	public boolean isKnownBidding(ChinaBidding b) {
		return false;
	}

	public void getNewBiddings() throws IOException, URISyntaxException, ParserException {
		oldBiddings.addAll(newBiddings);
		newBiddings.clear();
		lastUpdateDate = new Date();
		ChinaBidding markedBidding = (oldBiddings.size() == 0) ? null : oldBiddings.get(oldBiddings.size() - 1);

		BasicCookieStore cookieStore = new BasicCookieStore();
        CloseableHttpClient httpclient = HttpClients.custom()
                .setDefaultCookieStore(cookieStore)
                .build();
        int page = 1;

		TableColumnIterator it = new TableColumnIterator();
		TableColumn n;
		while (true) {
			URI uri = new URI("http://www.chinabidding.com.cn/search/searchzbw/search2?rp=22&categoryid=&keywords=&page=" + Integer.toString(page) + "8&areaid=&table_type=1000&b_date=week");
			HttpGet httpget = new HttpGet(uri);
			CloseableHttpResponse response = httpclient.execute(httpget);
			try {
				HttpEntity entity = response.getEntity();
				String result = EntityUtils.toString(entity);
				//System.out.print(result);

				Parser parser = Parser.createParser(result, "utf-8");
				AndFilter filter = new AndFilter(new TagNameFilter("tr"), new HasAttributeFilter("height", "23"));  
				Node node = null;  
				NodeList nodeList = parser.extractAllNodesThatMatch(filter);
				if (nodeList.size() == 0) {
					return;
				}
				for (int i = 0; i < nodeList.size(); ++i) {  
					node = nodeList.elementAt(i);
					System.out.println(node.getClass().getName());
					it.setTableRow((TableRow) node); 
//			        <tr height="23"  class="listrow2"   >    
//		             <td>
//		                <div align="center"><img height="9" width="10" src="http://www.chinabidding.com.cn/zbw/images/cio_pic.jpg" /></div>
//		                <input type="hidden" value="VIPXM" name="table_name2"/>
//		            </td>
//		            <td><a target="_blank" href="/zbgg/CzblU.html">海棠公园二期建设规划设计项目招标公告</a></td>
//		            <td style="text-align:center;">
//		                <input type="hidden" class="info_id" id="sc23137069" value="23137069"/>
//		                <a class="x_sc" href="javascript:;">
//		                    <img src="/public/search/searchadvzbxx/test/images/souc_11.png" style="vertical-align:-3px;padding-right:5px;cursor:pointer;border:none;"/>
//		                    <span style="cursor:pointer;color:rgb(0,0,0);">收藏 </span>
//		                </a>
//		                <a class="x_qx" href="javascript:;" style="display:none;">
//		                    <img src="/public/search/searchadvzbxx/test/images/souc_hua_11.png" style="vertical-align:-3px;padding-right:5px;cursor:pointer;border:none;"/>
//		                    <span style="cursor:pointer;color:#ce5a0d;">已收藏 </span>
//		                </a>
//		            </td>
//		            <td>服务招标</td>
//		            <td>青海</td>
//		            <td>园林绿化</td>
//		            <td>
//		                
//		                2016-05-12
//		                <div style="display: none">2016-05-12 15:53:53</div>                                
//		            </td>
//		        </tr>
				
					it.getNextTableColumn();	// Ignore first
					n = it.getNextTableColumn();
					
					ChinaBidding b = new ChinaBidding();
					b.name = n.toPlainTextString().trim();
					b.uri = ((LinkTag)n.getChild(0)).getLink();
					if (markedBidding!= null && markedBidding.equals(b)) {
						System.out.print(newBiddings.size() + " items loaded");
						return;
					}
					it.getNextTableColumn();	// Ignore
					b.types = it.getNextTableColumn().toPlainTextString().split(",");
					b.area = it.getNextTableColumn().toPlainTextString();
					b.industries = it.getNextTableColumn().toPlainTextString().split(",");
					newBiddings.add(b);
 
            	  //System.out.println(node.getChildren().elementAt(j).getClass().getName());
              }
             // newbidding.add(b);
              
              //System.out.println(node.getChildren().elementAt(0));
//              System.out.println(node.getClass().getName() + " " + id);
            		  

	        } finally {
	            response.close();
	        }

			if (markedBidding == null) {	//first run;
				System.out.print(newBiddings.size() + " items loaded");
				return;
			}
			page++;
		}
	}

	public void dumpBiddings() throws Exception {
        for (int i = 0; i < newBiddings.size(); i++) {
        	newBiddings.get(i).dump(System.out);
        }
        System.out.println("=====");
        for (int i = 0; i < oldBiddings.size(); i++) {
        	oldBiddings.get(i).dump(System.out);
        }
        System.out.print(newBiddings.get(0).getContent());
	}

}
