package com.egnore.clairvoyant.bidding;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.htmlparser.util.ParserException;

import com.egnore.clairvoyant.util.Logger;

public class BiddingList {
	protected List<BiddingInfo> oldBiddings = new ArrayList<BiddingInfo>(); // oldest first
	protected List<BiddingInfo> newBiddings = new ArrayList<BiddingInfo>(); // newest first
	protected Date lastUpdateDate;

	protected BasicCookieStore cookieStore = new BasicCookieStore();
	protected CloseableHttpClient httpclient = HttpClients.custom()
            .setDefaultCookieStore(cookieStore)
            .build();

	public void saveBiddings() {
		
	}

	public boolean isKnownBidding(ChinaBidding b) {
		return false;
	}

	public List<BiddingInfo> getNewBiddingList() {
		return newBiddings;
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

	public List<BiddingInfo> filterNewBiddingList(String word) {
		List<String> words = new ArrayList<String>();
		words.add(word);
		return filterNewBiddingList(words, null);
	}

	public List<BiddingInfo> filterNewBiddingList(List<String> words) {
		return filterNewBiddingList(words, null);
	}

	public List<BiddingInfo> filterNewBiddingList(List<String> words, List<String> excludes) {
		List<BiddingInfo> result = new ArrayList<BiddingInfo>();

		for (int i = 0; i < newBiddings.size(); i++) {
			BiddingInfo b = newBiddings.get(i);
			boolean skip = false;
			
			if (excludes != null) {
				for (String s : excludes) {
					if (b.getProjectName().toLowerCase().contains(s)) {
						skip = true;
						break;
					}
				}
			}

			if (skip) {
				continue;
			}

			if (words == null ) {
				result.add(b);
			} else {
				for (String s : words) {
					if (b.getProjectName().toLowerCase().contains(s)) {
						result.add(b);
					}
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
		refreshNewBiddingFromWeb();

		Logger.Debug(newBiddings.size() + " loaded.");
		DateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (int i = 0; i < newBiddings.size(); i++) {
        	BiddingInfo bb = newBiddings.get(i);
        	Logger.Debug(bb.id + "=" + bb.projectName + ":" + bb.uri + ":" + ((bb.getDeadlineDate() == null) ? "" : format.format(bb.deadlineDate)));
        }
	}

	public void refreshNewBiddingFromWeb() throws IOException, URISyntaxException, ParserException {
    }

	public void dumpBiddings() throws Exception {
        for (int i = 0; i < newBiddings.size(); i++) {
        	newBiddings.get(i).dump(System.out);
        }
        System.out.println("=====");
        for (int i = 0; i < oldBiddings.size(); i++) {
        	oldBiddings.get(i).dump(System.out);
        }
	}

}
