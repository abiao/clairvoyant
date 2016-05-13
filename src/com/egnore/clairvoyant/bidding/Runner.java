package com.egnore.clairvoyant.bidding;

import java.util.ArrayList;
import java.util.List;

import com.egnore.clairvoyant.mail.MailSender;
import com.egnore.clairvoyant.util.Logger;

public class Runner {

	protected static int DEFAULT_CHECK_INTERVAL = 60000;
	
	protected List<String> keyWords = new ArrayList<String>();
	protected int checkInterval = DEFAULT_CHECK_INTERVAL;
	
	public void setKeyWords(List<String> words) {
		keyWords = words;
	}

	public void setCheckInterval(int interval) {
		if (interval >= 0) {
			checkInterval = interval;
		}
	}
	public void run() throws Exception {
		ChinaBiddingList cb = new ChinaBiddingList();
		MailSender sender = new MailSender();
		//String REG = "大数据|hadoop|HADOOP|Hadoop";
		//String REG = "中国移动|大数据|hadoop|HADOOP|Hadoop";
		keyWords.clear();
		keyWords.add("中国移动"); //test
		keyWords.add("大数据");
		keyWords.add("hadoop");
		keyWords.add("cloudera");
		keyWords.add("cdh");
		
		Logger.Info("Begin with watcher: " + keyWords.toString());
		while (true) {
			cb.getNewBiddings();
			List<ChinaBidding> r = cb.filterNewBiddingList(keyWords);
			if (!r.isEmpty()) {
				for (int i = 0; i < r.size(); i++) {
					ChinaBidding b = r.get(i);
					Logger.Info("Find: " + b.getName());
					sender.sendDefaultMail("cb@cloudera.com", b.getName(), b.getContent());
				}
			}
			Thread.sleep(checkInterval);
		}
	}
}
