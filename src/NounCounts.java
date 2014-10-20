import java.util.HashMap;
import java.util.List;
import java.util.Set;

import kr.co.shineware.nlp.komoran.core.analyzer.Komoran;
import kr.co.shineware.util.common.model.Pair;

import com.google.gson.annotations.Expose;



public class NounCounts {
	@Expose
	private HashMap<String, Integer> noun;
	
	@Expose
	private int timestamp;
	
	private Komoran komoran;
	
	public NounCounts() {
		noun = new HashMap<String, Integer>();
		timestamp = 0;
		
		komoran = new Komoran("models-light");
		komoran.addUserDic("word-ilbe.txt");
		komoran.addUserDic("word-new22.txt");
	}
	
	public void setTimestamp(int timestamp) {
		this.timestamp = timestamp;
	}
	
	public int getTimestamp() {
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
	
	public int analyze(String str) {
		@SuppressWarnings("unchecked")
		List<List<Pair<String, String>>> result = komoran.analyze(str);
		
		for (List<Pair<String, String>> eojeolResult : result) {
			for (Pair<String, String> wordMorph : eojeolResult) {
				String word = wordMorph.getFirst().trim();
				String morph = wordMorph.getSecond();
				if (word.length() > 1) {
					if ("NNP".equals(morph)) {
						//nouns.putNNP(word);
						this.putNoun(word);
					} else if ("NNG".equals(morph)) {
						//nouns.putNNG(word);
					//	nc.putNoun(word);
					}
				}
			}
		}
		
		return noun.size();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("NounCounts [noun=").append(noun)
				.append(", timestamp=").append(timestamp).append("]");
		return builder.toString();
	}
	
}