package net.caucse.koreanextract;
import java.io.PrintStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import kr.co.shineware.nlp.komoran.core.analyzer.Komoran;
import kr.co.shineware.util.common.model.Pair;
import net.caucse.paperlibrary.WordDocument;
import net.caucse.paperlibrary.WordList;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


public class RealExtract {
	
	private static Komoran komoran;

	public static void main(String[] args) {
		long time = System.currentTimeMillis();
		GsonBuilder builder = new GsonBuilder();
		builder.excludeFieldsWithoutExposeAnnotation();
		Gson gson = builder.create();
		
		komoran = new Komoran("models-light");
		komoran.addUserDic("word-ilbe.txt");
		komoran.addUserDic("word-new22.txt");
		
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
				
				//WordDocument nouns = analyzeDocument(str, date);
				WordList nouns = analyzeList(str, date);
				ps.println(gson.toJson(nouns));
				//ps.println(nouns);
			}
			ps.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			time = System.currentTimeMillis() - time;
			System.err.println(time/3600000 + "h " + time%3600000/60000 + "m " + time%60000/1000 + "s");
		}
	}
	
	public static WordDocument analyzeDocument(String str, Date timestamp) {
		WordDocument wd = new WordDocument(timestamp);
		
		@SuppressWarnings("unchecked")
		List<List<Pair<String, String>>> result = komoran.analyze(str);
		
		for (List<Pair<String, String>> eojeolResult : result) {
			for (Pair<String, String> wordMorph : eojeolResult) {
				String word = wordMorph.getFirst().trim();
				String morph = wordMorph.getSecond();
				if (word.length() > 1) {
					if ("NNP".equals(morph)) {
						wd.put(word);
					} else if ("NNG".equals(morph)) {
						wd.put(word);
					}
				}
			}
		}
		return wd;
	}
	
	private static String[] morphList = new String[] {"MAG", "NNG", "NNP", "VA", "VV"};
	
	public static WordList analyzeList(String str, Date timestamp) {
		
		String[] lines = str.split("\n");
		
		WordList wl = new WordList(timestamp);
		
		for (String line : lines) {
			if (line.trim().length() == 0) continue;
			
			@SuppressWarnings("unchecked")
			List<List<Pair<String, String>>> result = komoran.analyze(line);
			
			wl.newLine();
			for (List<Pair<String, String>> eojeolResult : result) {
				for (Pair<String, String> wordMorph : eojeolResult) {
					String word = wordMorph.getFirst().trim();
					String morph = wordMorph.getSecond();
					if (word.length() > 1) {
						if (Arrays.binarySearch(morphList, morph) > 0) {
							wl.put(word);
						}
					}
				}
			}
		}
		return wl;
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