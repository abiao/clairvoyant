package com.egnore.clairvoyant.bidding;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Date;
import java.util.List;

import org.apache.http.ParseException;
import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import com.egnore.clairvoyant.HttpUtils;

public class ChinaBidding {
	public String name;
	public Date openDate;
	public String[] types;
	public String area;
	public String[] industries;
	public String uri;
	public boolean equals(ChinaBidding b) {
		return name.equals(b.name) && uri.equals(b.uri);
	}
	public void dump(PrintStream ps) {
		ps.println(uri + "=>" + name);
	}
	public String getContent() throws ParseException, IOException, ParserException {
		String s = HttpUtils.getURIContent("http://www.chinabidding.com.cn" + uri);
		StringBuilder sb = new StringBuilder();
		Parser parser = Parser.createParser(s, "utf-8");
		AndFilter filter = new AndFilter(new TagNameFilter("div"), new HasAttributeFilter("class", "xmgs"));  
		NodeList nodeList = parser.extractAllNodesThatMatch(filter);
		for (int i = 0; i < nodeList.size(); ++i) {  
			Node node = nodeList.elementAt(i);
			String ss = node.toPlainTextString();
			ss = ss.replace("中国采购与招标网", "我网");
			sb.append(ss);
		}
		return sb.toString();
	}
}
