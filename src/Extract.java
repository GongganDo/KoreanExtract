import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import kr.co.shineware.nlp.komoran.core.analyzer.Komoran;
import kr.co.shineware.util.common.model.Pair;

public class Extract {
	static String path = null;
	static Komoran analyzer = null;
	static String[] filter = { "http", "rt", "co", "com" };
	static ArrayList<String> checkList = null;
	static HashMap<String, Integer> KeyMaps = new HashMap<String, Integer>();

	public static void main(String[] args) {
		
		analyzer = new Komoran("models-light/");
		//analyzer.setUserDic("komoran_data/word.txt");
		analyzer.setUserDic("word-ilbe.txt");
		analyzer.setUserDic("word-new21.txt");
		checkList = new ArrayList<String>();
		Collections.addAll(checkList, filter);
		
		try {
			
			BufferedReader br = new BufferedReader(new FileReader("text.txt"));
			PrintWriter pw = new PrintWriter(new FileOutputStream("result.txt"));
			
			String sLine;
			
			while ((sLine = br.readLine()) != null) {
				@SuppressWarnings("unchecked")
				List<List<Pair<String, String>>> result = analyzer.analyze(sLine);
				getAll(result, pw);
			}
			br.close();
			pw.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	public static void getAll(List<List<Pair<String, String>>> result, PrintWriter pw) {
		for (List<Pair<String, String>> eojeolResult : result) {
			for (Pair<String, String> wordMorph : eojeolResult) {
				String first = wordMorph.getFirst().trim();
				String second = wordMorph.getSecond();
				pw.print(first + "/" + second + "/");
			}
			pw.println("\b ");
		}
	}
	
	public static void getNoun(List<List<Pair<String, String>>> result, PrintWriter pw) {
		String first, second;
		int cnt = 0;
		Pair<String, String> exc = new Pair<String, String>("@", "SW");
		int idx;
		for (List<Pair<String, String>> eojeolResult : result) {
			if (eojeolResult.size() == 0
					|| eojeolResult.get(0).getFirst().equals("http"))
				continue;
			idx = eojeolResult.indexOf(exc);
			if (idx != -1 && idx < eojeolResult.size() - 1) {
				if (eojeolResult.get(idx + 1).getFirst().equals("-")) {
					if (idx < eojeolResult.size() - 2) {
						if (eojeolResult.get(idx + 2).getSecond()
								.equals("SN")) {
							eojeolResult.remove(idx);
							eojeolResult.remove(idx);
							eojeolResult.remove(idx);
						}
					}
				} else {
					if (eojeolResult.get(idx + 1).getSecond().equals("SN")) {
						eojeolResult.remove(idx);
						eojeolResult.remove(idx);
					}
				}
			}
			for (Pair<String, String> wordMorph : eojeolResult) {
				first = wordMorph.getFirst().trim();
				second = wordMorph.getSecond();
				if ((second.equals("NNG") || second.equals("NNP")
						|| second.equals("SL") || second.equals("SH") || second
							.equals("SN"))) {
					if (second.equals("SL")) {
						first = first.toLowerCase();
						if (checkList.contains(first))
							continue;
					}
					if (KeyMaps.get(first) != null)
						KeyMaps.put(first, KeyMaps.get(first) + 1);
					else
						KeyMaps.put(first, 1);
				}
			}
		}
		Set<String> keySet = KeyMaps.keySet();
		Iterator<String> iterator = keySet.iterator();

		while (iterator.hasNext()) {
			String key = iterator.next();
			int value = KeyMaps.get(key);
			if (value >= 1 && key.trim().length() > 1){
				pw.print(key);
				pw.print(',');
				pw.println(value);
			}
		}
		
		if (++cnt % 30 == 0) {
			System.out.println(cnt);
		}
		
		KeyMaps.clear();
	}

}
