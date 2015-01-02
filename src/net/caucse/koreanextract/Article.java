package net.caucse.koreanextract;

public class Article {
	private String name;
	private String title;
	private String mid;
	private String content;
	private String date;
	private String id;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getMid() {
		return mid;
	}
	public void setMid(String mid) {
		this.mid = mid;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Article [name=").append(name).append(", title=")
				.append(title).append(", mid=").append(mid)
				.append(", content=").append(content).append(", date=")
				.append(date).append(", id=").append(id).append("]");
		return builder.toString();
	}
}
