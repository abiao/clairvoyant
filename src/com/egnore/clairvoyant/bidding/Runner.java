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
		ChinaBiddingList cb  = new ChinaBiddingList();
		CMCCBiddingList cmcc = new CMCCBiddingList();
		MailSender sender = new MailSender();
		//String REG = "中国移动|大数据|hadoop|HADOOP|Hadoop";
		keyWords.clear();
		keyWords.add("大数据");
		keyWords.add("hadoop");
		keyWords.add("cloudera");
		keyWords.add("cdh");
//		keyWords.add("机房");

		List<String> cbExcludes = new ArrayList<String>();
		//cbExcludes.add("中国移动");

		List<BiddingInfo> r;
		Logger.Info("Begin with watcher: " + keyWords.toString());
		while (true) {
/*			cmcc.getNewBiddings();
			r = cmcc.filterNewBiddingList(keyWords);
			if (!r.isEmpty()) {
				for (int i = 0; i < r.size(); i++) {
					BiddingInfo b = r.get(i);
					Logger.Info("Find: " + b.getProjectName());
					//sender.sendDefaultMail("cb@cloudera.com", b.getProjectName(), b.getContentAsString());
				}
			}
*/
			cb.getNewBiddings();
			r = cb.filterNewBiddingList(keyWords, cbExcludes);
			if (!r.isEmpty()) {
				for (int i = 0; i < r.size(); i++) {
					BiddingInfo b = r.get(i);
					Logger.Info("Find: " + b.getProjectName());
					sender.sendDefaultMail("cb@cloudera.com", b.getProjectName(), b.getContentAsString());
				}
			}
			Thread.sleep(checkInterval);
		}
	}
}
