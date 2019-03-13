import java.io.*;
import java.lang.Math;

public class ranking {

	public static double avgdl(String[] allTweets) {
		double total_words = 0.0;
		for (String s:allTweets) total_words += s.length();
		return total_words / allTweets.length;
	}

	public static double tweet_num(String[] allTweets, String query_term) {
		double ni = 0;
		for (String s:allTweets) {
			if (s.equals(query_term)) ni++;
		}
		return ni; 
	}

	public static double BM25(String Query, String Tweet, String[] allTweets, double avg_doc_length) {
		double k1 = 1.2;
		double k2 = 100;
		double b = 0.75;

		double ni;	// Number of tweets this query term appears in
		double N = allTweets.length;	// Number of tweets we have
		double fi;	// Number of times the query term appears in tweet
		double qfi;	// Number of times the query term appears in query
		double K;
		double doc_length;

		String[] query_words = Query.split("\\s");
		String[] tweet_words = Tweet.split("\\s");
		// Calculate K
		doc_length = tweet_words.length;
		K = k1 * ((1.0-b) + b * (doc_length / avg_doc_length));

		double score,x,y,z;
		x = y = z = score = 0.0;
		for (String s:query_words) {
			qfi = fi = 0;
			// Calculate qfi and fi
			for (String s2:query_words) if (s.equals(s2)) qfi++;
			for (String s3:tweet_words) if (s.equals(s3)) fi++;
			//System.out.println("Word is " + s + " and qfi = " + qfi + " and fi = " + fi);

			ni = tweet_num(allTweets, s);

			x = Math.log10( (N-ni+0.5)/(ni+0.5) );
			y = ((k1 + 1)*fi) / (K + fi);
			z = ((k2 + 1)*qfi) / (k2 + qfi);
			score += (x*y*z);
		}
		return score;
	}

	public static void main(String args[]) {
		double N = 1000;
		String query = "Hello, my name is Alex";
		String b = "Job Pop my is Hello,";
		String c = "my Alex Alex name";
		String[] tweets = new String[2];
		tweets[0] = b; tweets[1] = c;
		double avg_tweets_l = avgdl(tweets);
		System.out.println("Average tweet length: " + avg_tweets_l);

		for (String tweet:tweets) {
			System.out.println("Tweet Text: " + tweet + "\n\tScore: "+ BM25(query, tweet, tweets, avg_tweets_l));
		}
	}
}
