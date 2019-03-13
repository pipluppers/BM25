import java.io.*;

public class keyvalue {
	private String term;
	private String name;
	private String screen_name;
	private String location;
	private String hashtag;
	private String content;
	private String profile_img_url;
	private int wordcount;

	public keyvalue(String t,String n,String sn,String l,String h,String c,String p,int w) {
		this.term = t; this.name = n; this.screen_name = sn; this.location = l; this.hashtag = h; this.content = c;
		this.profile_img_url = p; this.wordcount = w;
	}
}
