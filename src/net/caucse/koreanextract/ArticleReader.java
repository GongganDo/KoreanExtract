package net.caucse.koreanextract;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.google.gson.Gson;


public class ArticleReader {
	private BufferedReader reader;
	private Gson gson;
	
	{
		gson = new Gson();
	}
	
	public ArticleReader(String path) throws FileNotFoundException {
		reader = new BufferedReader(new FileReader(path));
	}
	public ArticleReader(InputStream is) {
		reader = new BufferedReader(new InputStreamReader(is));
	}
	
	public Article ReadArticle() throws IOException {
		String data = reader.readLine();
		if (data == null) return null;
		return gson.fromJson(data, Article.class);
	}
	
	public void close() throws IOException {
		reader.close();
	}
}
