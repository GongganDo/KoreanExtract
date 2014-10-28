import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kr.co.shineware.nlp.komoran.core.analyzer.Komoran;
import kr.co.shineware.util.common.model.Pair;

import com.google.gson.Gson;

public class KoreanExtract {
	
	private static final String WORD_DATAPATH = "models_light/";
	//private static final String WORD_USERDICNAME = "word.txt";
	
	private Komoran analyzer = null;
	
	private Reader reader;
	
	{
		analyzer = new Komoran(WORD_DATAPATH);
		analyzer.setUserDic("word-ilbe.txt");
		analyzer.setUserDic("word-new22.txt");
		//analyzer.setUserDic(WORD_DATAPATH + WORD_USERDICNAME);
	}
	
	public KoreanExtract(Reader reader) {
		this.reader = reader;
	}
	
	public KoreanExtract(InputStream is) {
		this.reader = new InputStreamReader(is);
	}
	
	public KoreanExtract(String filename) throws FileNotFoundException {
		this.reader = new FileReader(filename);
	}
	
	/**
	 * 데이터를 읽어들여 형태소 분석 후 단어 세기
	 * @return [단어, 개수] 형태의 Map
	 * @throws IOException
	 */
	public Map<String, Integer> analyze() throws IOException {
		return analyze(new String[0]);
	}
	
	
	/**
	 * 데이터를 읽어들여 형태소 분석 후 단어 세기
	 * @param skipList 분석에서 제외할 단어의 배열
	 * @return [단어, 개수] 형태의 Map
	 * @throws IOException
	 */
	public Map<String, Integer> analyze(String[] skipList) throws IOException {
		Map<String, Integer> keyMaps = new HashMap<String, Integer>();
		String sLine = null;
		BufferedReader br = new BufferedReader(reader);
		
		while ( (sLine = br.readLine()) != null) {
			
			@SuppressWarnings("unchecked")
			List<List<Pair<String, String>>> result = analyzer.analyze(sLine);
			String first, second;
			Pair<String, String> exc = new Pair<String, String>("@", "SW");
			
			int idx;
			for (List<Pair<String, String>> eojeolResult : result) {
				
				if (eojeolResult.isEmpty() || "http".equals(eojeolResult.get(0).getFirst()))
					continue;
				
				idx = eojeolResult.indexOf(exc);
				if (idx != -1 && idx < eojeolResult.size() - 1) {
					if ("-".equals(eojeolResult.get(idx + 1).getFirst())) {
						if (idx < eojeolResult.size() - 2) {
							if ("SN".equals(eojeolResult.get(idx + 2).getSecond())) {
								eojeolResult.remove(idx);
								eojeolResult.remove(idx);
								eojeolResult.remove(idx);
							}
						}
					} else {
						if ("SN".equals(eojeolResult.get(idx + 1).getSecond())) {
							eojeolResult.remove(idx);
							eojeolResult.remove(idx);
						}
					}
				}
				
				//final String[] equalsString = new String[] { "NNG", "NNP", "SH", "SL", "SN" };
				final String[] equalsString = new String[] { "NNG", "NNP" };
				
				for (Pair<String, String> wordMorph : eojeolResult) {
					first = wordMorph.getFirst();
					second = wordMorph.getSecond();
					if (Arrays.binarySearch(equalsString, second) >= 0) {
						boolean skip = false;
						
						for (String check : skipList) {
							if (check.equalsIgnoreCase(first)) {
								skip = true;
								break;
							}
						}
						
						if (skip)
							continue;
						
						if (keyMaps.containsKey(first))
							keyMaps.put(first, keyMaps.get(first) + 1);
						else
							keyMaps.put(first, 1);
					}
				}
			}
		}
		return keyMaps;
	}
	
	public void close() throws IOException {
		reader.close();
	}
	

	public static void main(String[] args) {
		long time = System.currentTimeMillis();
		Gson gson = new Gson();
		DateFormat dfIn = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
		DateFormat dfOut = new SimpleDateFormat("MMdd");
		HashMap<String, HashMap<String, Integer>> data = new HashMap<String, HashMap<String, Integer>>();
		
		try {
			BufferedReader br = new BufferedReader(new FileReader("ilbe-text-all-0827-1026"));
			//PrintWriter pww = new PrintWriter("ver4.0_Filtered");
			String st;
			//int cnt = 0;
			while ((st = br.readLine()) != null) {
			
				Article article = (Article)gson.fromJson(st, Article.class);
				Date date = dfIn.parse(article.getDate());
				
				StringReader sr = new StringReader(article.getTitle() + '\n' + article.getContent());
				KoreanExtract ke = new KoreanExtract(sr);
				Map<String, Integer> map = ke.analyze();
				ke.close();
				sr.close();
				
				String dtStr = dfOut.format(date);
				HashMap<String, Integer> hm;
				if (data.containsKey(dtStr)) {
					hm = data.get(dtStr);
				} else {
					hm = new HashMap<String, Integer>();
					data.put(dtStr, hm);
				}
				
				//StringBuilder sbb = new StringBuilder();
				
				for (String key : map.keySet()) {
					if (hm.containsKey(key)) {
						hm.put(key, hm.get(key)+1);
					} else {
						hm.put(key, 1);
					}
					
					/*if (key.length() >= 2) {
						sbb.append(key);
						sbb.append(" ");
					}*/
				}
				//article.setContent(sbb.toString().trim());
				//System.err.println(cnt++);
				//pww.println(gson.toJson(article));
			}
			br.close(); // end: 18342
			//pww.close();
			
			String[] dtKeys = data.keySet().toArray(new String[0]);
			Arrays.sort(dtKeys);
			
			PrintWriter pw = new PrintWriter("ver4.0_Filtered");
			
			for (String dtKey : dtKeys) {
				
				String[] wdKeys = data.get(dtKey).keySet().toArray(new String[0]);
				Arrays.sort(wdKeys);
				
				for (String key : wdKeys) {
					if (key.length() >= 2) {
						StringBuilder sb = new StringBuilder();
						sb.append(dtKey)
						  .append('\t')
						  .append(key)
						  .append('\t')
						  .append(data.get(dtKey).get(key));
						pw.println(sb.toString());
					}
				}
			}
			pw.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			time = System.currentTimeMillis() - time;
			System.err.println(time/3600000 + ":" + time%3600000/60000 + ":" + time%60000/1000 + "s");
		}
	}
}
