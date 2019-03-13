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
		
		// Test Query and Tweets
		String query = "Hello, my name is Alex";
		String b = "Job Pop my is Hello,";
		String c = "my Alex Alex name";
		String d = "Hell This should have nothing in common";
		String[] tweets = new String[3];
		tweets[0] = b; tweets[1] = c; tweets[2] = d;
		double avg_tweets_l = avgdl(tweets);
		System.out.println("Average tweet length: " + avg_tweets_l);
		

		// Score all tweets
		double[] scoresList = new double[tweets.length];
		int i = 0;
		for (i = 0; i < tweets.length; ++i) {
			// BM25(Query, Tweet, List of all tweets, avg tweet length)
			scoresList[i] = BM25(query, tweets[i], tweets, avg_tweets_l);
			//System.out.println("Tweet Text: " + tweets[i] + "\n\tScore: " + scoresList[i]);
		}

		
		// Rank tweets in order
		int n = 3;			// TODO Update n to 100 later to find top 100
		double maxScore = 0.0;
		double prevMax = 10000;	// so we don't find the same max score
		double[] topnScores = new double[n];
		String[] topnTweets = new String[n];
		int j = 0;
		int ind = 0;		// Store the index of where we found the max
		for (i = 0; i < n; ++i) {	// Finding top 2
			maxScore = -1.0;
			for (j = 0; j < tweets.length; ++j) {
				if (maxScore < scoresList[j] && scoresList[j] < prevMax) {
					maxScore = scoresList[j];
					ind = j;
				}
			}
			prevMax = maxScore;	// Update previous max so we don't register this as max again
			topnScores[i] = scoresList[ind];
			topnTweets[i] = tweets[ind];
		}

		// Print top n Tweets		
		System.out.println("Top " + n + " tweets:");
		for (i = 0; i < topnScores.length; ++i) {
			System.out.println(i+1 + ": " + topnTweets[i] + "\n\tScore: " + topnScores[i]);
		}
	}
}
