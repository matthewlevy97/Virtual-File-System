package fs;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class FileSystem {

	// CONSTANTS
	public final static int FILE_BLOCK_SIZE                = 512;

	public final static String FILE_SYSTEM_METADATA_FILE   = ".fsmetadata";
	public final static String FILE_SYSTEM_CONTAINER       = ".fscontainer";

	private HashMap<String, Metadata> fileMetadata;
	private Container container;

	public FileSystem() {
		fileMetadata = new HashMap<String, Metadata>();
		container    = new Container();
		
		loadMetadata();
	}
	
	@SuppressWarnings("unchecked")
	private void saveMetadata() {
		// Add meta-data for each file to jsonStr
		JSONArray array = new JSONArray();
		for(String key : fileMetadata.keySet()) {
			array.add(fileMetadata.get(key).toJSON());
		}
		
		try(FileWriter file = new FileWriter(FILE_SYSTEM_METADATA_FILE)) {
			file.write(array.toString());
			file.flush();
		} catch (IOException e) {
			System.err.println(String.format("Error saving metadata file. File: %s", FILE_SYSTEM_METADATA_FILE));
		}
	}
	
	@SuppressWarnings("unchecked")
	private void loadMetadata() {
		// Clear fileMetadata
		fileMetadata.clear();
		
		JSONParser parser = new JSONParser();
		try {
			JSONArray json = (JSONArray) parser.parse(new FileReader(FILE_SYSTEM_METADATA_FILE));
			
			// If json is null, no meta-data as even been saved
			if(json == null) {
				return;
			}
			
			Iterator<JSONObject> iter = json.iterator();
			
			while(iter.hasNext()) {
				JSONObject metadataJson = iter.next();
				System.out.println(metadataJson);
				String fileName = (String) metadataJson.get("f");
				long offset     = (long) metadataJson.get("o");
				long lastAccess = (long) metadataJson.get("a");
				
				fileMetadata.put(fileName, new Metadata(fileName, offset, lastAccess));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Error loading metadata file for file system");
		}
	}

	public FSFile open(String fileName) {
		Metadata metadata = fileMetadata.get(fileName);

		FSFile file;
		if(metadata == null) {
			// File does not exist already
			file = new FSFile(fileName);
		} else {
			// File has meta-data
			file = new FSFile(fileName, container.getFile(metadata.getOffset()));
		}

		return file;
	}
	
	/**
	 * Saves a file to the container
	 * 
	 * @param file
	 */
	public void save(FSFile file) {
		String fileName = file.getFileName();

		Metadata metadata = fileMetadata.get(fileName);
		if(metadata == null) {
			// File has no meta-data, so create meta-data and add file to container
			long offset = container.addFile(file);
			metadata = new Metadata(fileName, offset);
			fileMetadata.put(fileName, metadata);
		} else {
			// Overwrite the current data in the container for this file with the new data
			container.saveFile(metadata, file);
		}
	}

	/**
	 * Goes through the container removing files with no meta-data references
	 */
	public void defragSystem() {

	}
	
	/**
	 * Completely deletes the file system
	 * This is unrecoverable
	 */
	public void clean() {
		container.clean();
		fileMetadata.clear();
		saveMetadata();
	}
	
	/**
	 * 
	 * Saves the meta-data table in format:
	 * 	[
	 * 		{
	 * 			'f': FILE NAME,
	 * 			'o': FILE OFFSET,
	 * 			'a': LAST ACCESS DATE (Seconds from epoch)
	 * 		}, ...
	 * 	]
	 * 
	 */
	public void close() {
		container.close();
		saveMetadata();
	}
}
