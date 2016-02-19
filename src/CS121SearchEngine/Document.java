package CS121SearchEngine;

public class Document 
{
	private Integer docId;
	private String body;
	private String rawHTML;
	private String  url;
	
	/**
	 * @param docId
	 * @param body
	 * @param rawHTML
	 * @param Url
	 */
	public Document(Integer docId,String Url, String body, String rawHTML) 
	{
		this.docId = docId;
		this.body = body;
		this.rawHTML = rawHTML;
		this.url = Url;
	}

	/**
	 * @return the docId
	 */
	public Integer getDocId() {
		return docId;
	}

	/**
	 * @return the body
	 */
	public String getBody() {
		return body;
	}

	/**
	 * @return the rawHTML
	 */
	public String getRawHTML() {
		return rawHTML;
	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Document [docId=" + docId + ", url=" + url + "]";
	}
	
}
