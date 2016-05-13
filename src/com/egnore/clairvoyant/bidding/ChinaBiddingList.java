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

import com.egnore.clairvoyant.util.Logger;
import com.egnore.clairvoyant.util.TableColumnIterator;

public class ChinaBiddingList {
	public List<ChinaBidding> oldBiddings = new ArrayList<ChinaBidding>(); // oldest first
	public List<ChinaBidding> newBiddings = new ArrayList<ChinaBidding>(); // newest first
	public Date lastUpdateDate;
	static protected String HISTORY_FILE = "oldbiddings";

	public void saveBiddings() {
		
	}

	public boolean isKnownBidding(ChinaBidding b) {
		return false;
	}

	public List<ChinaBidding> getNewBiddingList() {
		return newBiddings;
	}
	
	public List<ChinaBidding> filterNewBiddingList(String word) {
		List<String> words = new ArrayList<String>();
		words.add(word);
		return filterNewBiddingList(words);
	}

	public List<ChinaBidding> filterNewBiddingList(List<String> words) {
		List<ChinaBidding> result = new ArrayList<ChinaBidding>();
		for (int i = 0; i < newBiddings.size(); i++) {
			ChinaBidding b = newBiddings.get(i);
			for (String s : words) {
				if (b.getName().toLowerCase().contains(s)) {
					result.add(b);
				}
			}
		}
		return result;
	}

	public void getNewBiddings() throws IOException, URISyntaxException, ParserException {
		if (oldBiddings.size() > 100000) {
			oldBiddings.clear();
		}
		
		for (int i = newBiddings.size() - 1; i >=0; i--) {
			oldBiddings.add(newBiddings.get(i));
		}

		newBiddings.clear();
		lastUpdateDate = new Date();
		ChinaBidding markedBidding = (oldBiddings.size() == 0) ? null : oldBiddings.get(oldBiddings.size() - 1);

		BasicCookieStore cookieStore = new BasicCookieStore();
        CloseableHttpClient httpclient = HttpClients.custom()
                .setDefaultCookieStore(cookieStore)
                .build();
        int page = 0;

		TableColumnIterator it = new TableColumnIterator();
		TableColumn n;
		while (true) {
			page++;
			URI uri = new URI("http://www.chinabidding.com.cn/search/searchzbw/search2?rp=22&categoryid=&keywords=&page=" + Integer.toString(page) + "&areaid=&table_type=1000&b_date=week");
			HttpGet httpget = new HttpGet(uri);
			CloseableHttpResponse response = httpclient.execute(httpget);
			try {
				HttpEntity entity = response.getEntity();
				String result = EntityUtils.toString(entity);
				//Logger.Debug(result);

				Parser parser = Parser.createParser(result, "utf-8");
				AndFilter filter = new AndFilter(new TagNameFilter("tr"), new HasAttributeFilter("height", "23"));  
				Node node = null;  
				NodeList nodeList = parser.extractAllNodesThatMatch(filter);
				if (nodeList.size() == 0) {
					return;
				}
				for (int i = 0; i < nodeList.size(); ++i) {  
					node = nodeList.elementAt(i);
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
						Logger.Debug("meet " + markedBidding.getName());
						Logger.Info(newBiddings.size() + " items loaded");
						return;
					}
					it.getNextTableColumn();	// Ignore
					b.types = it.getNextTableColumn().toPlainTextString().split(",");
					b.area = it.getNextTableColumn().toPlainTextString();
					b.industries = it.getNextTableColumn().toPlainTextString().split(",");
					System.out.println("P]" + b.name);
					newBiddings.add(b);
					}
 	        } finally {
	            response.close();
	        }

			if (markedBidding == null) {	//first run;
				Logger.Info(newBiddings.size() + " items loaded");
				return;
			}
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
