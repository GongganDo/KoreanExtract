package net.caucse.koreanextract;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import kr.co.shineware.nlp.komoran.core.analyzer.Komoran;
import kr.co.shineware.util.common.model.Pair;

/**
 * 한국어 텍스트를 분석하는 형태소 분석기 클래스
 */
public class StringExtract {
	
	private Komoran komoran;
	private String text;
	
	private int minWordLength = 2;
	
	private static String[] morphList = new String[] {"MAG", "NNG", "NNP", "VA", "VV"};

	/**
	 * 한국어 텍스트를 단어 단위로 분할하기 위한 형태소 분석기 객체를 생성
	 * @param text 분석할 한국어 텍스트
	 */
	public StringExtract(String text) {
		this.komoran = new Komoran("models-light");
		this.text = text;
	}
	
	/**
	 * 신조어 처리를 위해 위키백과와 엔하위키의 단어 데이터를 적용하도록 설정
	 */
	public void setWikiDic() {
		komoran.addUserDic("word-new22.txt");
	}
	
	/**
	 * '일간베스트' 사이트에서 사용되는 단어 및 표현을 처리할 수 있도록 설정
	 */
	public void setIlbeDic() {
		komoran.addUserDic("word-ilbe.txt");
	}
	
	/**
	 * 저장할 단어의 최소 글자 수를 지정 (기본값은 2)
	 * @param minWordLength 단어의 최소 글자 수
	 */
	public void setminWordLength(int minWordLength) {
		this.minWordLength = minWordLength;
	}
	
	/**
	 * 텍스트를 분석하여 단어 단위로 분할
	 * @return 텍스트의 단어를 담은 이중 리스트 (각각의 리스트는 각각의 줄을 의미)
	 */
	public List<List<String>> analyze() {
		
		Scanner scanner = new Scanner(text);
		List<List<String>> allList = new ArrayList<List<String>>();
		
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			if (line.trim().length() == 0) continue;
			
			List<String> lineList = new ArrayList<String>();
			
			@SuppressWarnings("unchecked")
			List<List<Pair<String, String>>> result = komoran.analyze(line);
			
			for (List<Pair<String, String>> eojeolResult : result) {
				for (Pair<String, String> wordMorph : eojeolResult) {
					String word = wordMorph.getFirst().trim();
					String morph = wordMorph.getSecond();
					if (word.length() >= minWordLength) {
						if (Arrays.binarySearch(morphList, morph) >= 0) {
							lineList.add(word);
						}
					}
				}
			}
			
			allList.add(lineList);
		}
		
		scanner.close();
		return allList;
	}
	
	/**
	 * 분석기에 저장된 원본 텍스트를 반환
	 * @return 원본 텍스트
	 */
	public String getText() {
		return text;
	}
}
