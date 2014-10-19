import java.io.PrintStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import kr.co.shineware.nlp.komoran.core.analyzer.Komoran;
import kr.co.shineware.util.common.model.Pair;

import com.google.gson.Gson;


public class Test {
	
	public static Komoran komoran;

	public static void main(String[] args) {
		long time = System.currentTimeMillis();
		komoran = new Komoran("models-light");
		komoran.addUserDic("word-ilbe.txt");
		komoran.addUserDic("word-new22.txt");
		Gson gson = new Gson();
		
		if (args.length < 2) {
			System.err.println("[Usage] java article_filename output_filename");
			System.exit(1);
		}
		
		try {
			ArticleReader ar = new ArticleReader(args[0]);
			PrintStream ps = new PrintStream(args[1]);
			Article article;
			
			SimpleDateFormat df1 = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
			SimpleDateFormat df2 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			
			while ( (article = ar.ReadArticle()) != null) {
				String str = article.getTitle() + "\n" + article.getContent();
				
				Date date = null;
				
				try {
					date = df1.parse(article.getDate());
				} catch (ParseException e) {
					date = df2.parse(article.getDate());
				}
				
				NounCounts nouns = analyze(str, date);
				ps.println(gson.toJson(nouns));
			}
			ps.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			time = System.currentTimeMillis() - time;
			System.err.println(time/3600000 + "h " + time/60000 + "m " + time/1000 + "s");
		}
	}
	
	public static NounCounts analyze(String str, Date timestamp) {
		@SuppressWarnings("unchecked")
		List<List<Pair<String, String>>> result = komoran.analyze(str);
		Nouns nouns = new Nouns();
		NounCounts nc = new NounCounts();
		nc.setTimestamp(timestamp);
		
		for (List<Pair<String, String>> eojeolResult : result) {
			for (Pair<String, String> wordMorph : eojeolResult) {
				String word = wordMorph.getFirst().trim();
				String morph = wordMorph.getSecond();
				if (word.length() > 1) {
					if ("NNP".equals(morph)) {
						nouns.putNNP(word);
						nc.putNoun(word);
					} else if ("NNG".equals(morph)) {
						nouns.putNNG(word);
						nc.putNoun(word);
					}
				}
			}
		}
		
		return nc;
	}

}

class Nouns {
	private HashSet<String> nnp, nng;
	
	public Nouns() {
		nnp = new HashSet<String>();
		nng = new HashSet<String>();
	}
	
	public boolean putNNP(String word) {
		return nnp.add(word);
	}
	
	public boolean putNNG(String word) {
		return nng.add(word);
	}
	
	public Set<String> nnpSet() {
		return nnp;
	}
	
	public Set<String> nngSet() {
		return nng;
	}
}

class NounCounts {
	private HashMap<String, Integer> noun;
	private Date timestamp;
	
	public NounCounts() {
		noun = new HashMap<String, Integer>();
		timestamp = null;
	}
	
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
	
	public Date getTimestamp() {
		return timestamp;
	}
	
	public void putNoun(String word) {
		if (noun.containsKey(word)) {
			noun.put(word, noun.get(word)+1);
		} else {
			noun.put(word, 1);
		}
	}
	
	public int getNoun(String word) {
		return noun.get(word);
	}
	
	public Set<String> keySet() {
		return noun.keySet();
	}
}