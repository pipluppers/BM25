import java.io.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;


public class keyvalue {

	public static String loadFile(String fileName) throws FileNotFoundException, IOException, java.lang.IllegalStateException{
		File f = new File(fileName);
		BufferedReader br = new BufferedReader(new FileReader(f));	
		return br.readLine();
	}

	public static void main(String arg[]) throws FileNotFoundException, IOException {
		String str = loadFile("test2.txt");
		System.out.println(str);

		String aa = "5";
		int x = Integer.parseInt(aa);
		System.out.println(x + 1);

		
		String d = "(.*)   \"total count\" : (\\d+)(.*)";
		Pattern p1 = Pattern.compile(d);
		Matcher m1 = p1.matcher(str);
		Matcher m2;
		String tweetJson = "";
		String f = ", \\[\"name\" : ([^\\]]*), \"screen name\" : ([^\\]]*), \"location\" : ([^\\]]*), \"content\" : \"([^\\]]*)\", \"profile image url\" : ([^\\]]*), \"frequency in tweet\" : (\\d+)\\]";
		Pattern p2 = Pattern.compile(f);
		while(m1.find()) {
			System.out.println("TERM: " + m1.group(1));
			System.out.println("TOTAL COUNT: " + m1.group(2));
			tweetJson = m1.group(3);
			System.out.println(tweetJson + "\n");
			m2 = p2.matcher(tweetJson);
			while(m2.find()) {
				System.out.println("NAME: " + m2.group(1));
				System.out.println("SCREEN NAME: " + m2.group(2));
				System.out.println("LOCATION: " + m2.group(3));
				System.out.println("CONTENT: " + m2.group(4));
				System.out.println("PROFILE IMAGE URL: " + m2.group(5));
				System.out.println("FREQUENCY IN TWEET: " + m2.group(6) + "\n");
			}
		}
	}
}
