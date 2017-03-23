package fs;

import org.json.simple.JSONObject;

public class Metadata {
	
	private String fileName;
	private long offset;
	private long lastAccess;
	
	public Metadata(String fileName, long offset) {
		this(fileName, offset, 0);
	}
	public Metadata(String fileName, long offset, long lastAccess) {
		this.fileName   = fileName;
		this.offset     = offset;
		this.lastAccess = lastAccess;
	}
	
	public String getFileName() {
		return fileName;
	}
	public long getOffset() {
		return offset;
	}
	public long getLastAccess() {
		return lastAccess;
	}
	
	@SuppressWarnings("unchecked")
	public JSONObject toJSON() {
		JSONObject obj = new JSONObject();
		
		obj.put('f', fileName);
		obj.put('o', offset);
		obj.put('a', lastAccess);
		
		return obj;
	}
	
}
