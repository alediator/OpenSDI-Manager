package it.geosolutions.opensdi.utils;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.ModelMap;
import org.springframework.web.multipart.MultipartFile;

public class ControllerUtils {

/**
 * DOT character "."
 */
public final static String DOT = ".";

private final static Logger LOGGER = Logger
	.getLogger(ControllerUtils.class);

public static void setCommonModel(ModelMap model) {
    Authentication auth = SecurityContextHolder.getContext()
            .getAuthentication();
    String name = auth.getName(); // get logged in username

    model.addAttribute("username", name);
}

/**
 * remove /../ and /./ from a path
 * 
 * @param dirString
 * @return
 */
public static String preventDirectoryTrasversing(String dirString) {
    // prevent directory traversing
    if (dirString == null)
        return null;
    dirString = dirString.replace("..", "");
    // clean path
    dirString = dirString.replace("/./", "/");
    dirString = dirString.replaceAll("/{2,}", "/");
    return dirString;
}

/**
 * Remove extension if exists in fileName
 * 
 * @param fileName to obtain the name
 * @return name without extension
 */
public static String removeExtension(String fileName) {
    if (fileName != null && fileName.lastIndexOf(DOT) >= 0)
        fileName = fileName.substring(0, fileName.lastIndexOf(DOT));
    return fileName;
}

public static String normalizePath(String folderPath) {
	folderPath = normalizeSeparator(folderPath);
    if (!folderPath.endsWith(File.separator)) {
			LOGGER.debug("[WARN] path not ending with file name separator ("
					+ File.separator + "), appending one");
			folderPath = folderPath.concat(File.separator);
    }
    return folderPath;
}


public static String normalizeSeparator(String folderPath) {
	return folderPath.replace("/", File.separator).replace("\\", File.separator);
}

/**
 * Concurrent upload handler
 * 
 * @author adiaz
 */
public static class CONCURRENT_UPLOAD {

/**
 * Map to handle file uploading chunked
 */
private static Map<String, List<byte[]>> UPLOADED_CHUNKS = new ConcurrentHashMap<String, List<byte[]>>();

/**
 * Pending chunks on last review and his size
 */
private static Map<String, Integer> PENDING_CHUNKS = new ConcurrentHashMap<String, Integer>();

/**
 * Private time for the last check
 */
private static Date LAST_CHECK;

/**
 * Minimum interval to check incomplete uploads
 */
private static int MIN_INTERVAL = 100000;

/**
 * Max of upload files with the same name
 */
public static final int MAX_SIM_UPLOAD = 100;

// TODO: Improve it. Now if you try to save at the same time the same file from
// two different clients, it fails.

/**
 * Add a chunk of a file upload
 * 
 * @param name of the file
 * @param chunks total for the file
 * @param chunk number on this upload
 * @param file with the content uploaded
 * @return current list of byte arrays for the file
 * @throws IOException if no more uploads are available
 */
public static Entry<String, List<byte[]>> addChunk(String name, int chunks,
        int chunk, MultipartFile file) throws IOException {
    Entry<String, List<byte[]>> entry = null;
    try {
        entry = getChunk(name, chunks, chunk);
        if (LOGGER.isTraceEnabled())
            LOGGER.trace("entry [" + entry.getKey() + "] found ");
        List<byte[]> uploadedChunks = entry.getValue();
        // add chunk on its position
        uploadedChunks.add(chunk, file.getBytes());
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("uploadedChunks size[" + entry.getKey() + "] --> "
                    + uploadedChunks.size());
        }
    } catch (IOException e) {
        LOGGER.error("Error on file upload", e);
    }

    return entry;
}

/**
 * Get a chunk of a file upload
 * 
 * @param name of the file
 * @param chunks total for the file
 * @param chunk number on this upload
 * @param file with the content uploaded
 * @return current entry for the file
 * @throws IOException if no more uploads are available
 */
public static Entry<String, List<byte[]>> getChunk(String name, int chunks,
        int chunk) throws IOException {
    Integer key = null;

    // init bytes for the chunk upload
    List<byte[]> uploadedChunks = UPLOADED_CHUNKS.get(name);
    if (chunk == 0) {
        if (uploadedChunks != null) {
            key = -1;
            while (uploadedChunks != null) {
                key++;
                uploadedChunks = UPLOADED_CHUNKS.get(name + "_" + key);
            }
        }
        uploadedChunks = new LinkedList<byte[]>();
    } else if (uploadedChunks == null || uploadedChunks.size() != chunk) {
        key = -1;
        while ((uploadedChunks == null || uploadedChunks.size() != chunk)
                && key < MAX_SIM_UPLOAD) {
            key++;
            uploadedChunks = UPLOADED_CHUNKS.get(name + "_" + key);
        }
        if (uploadedChunks == null || uploadedChunks.size() != chunk) {
            LOGGER.error("Incorrent chunk. Can't found previous chunks");
            throw new IOException(
                    "Incorrent chunk. Can't found previous chunks");
        }
    }

    // save and return entry
    String mapKey = key != null ? name + "_" + key : name;
    UPLOADED_CHUNKS.put(mapKey, uploadedChunks);
    Entry<String, List<byte[]>> entry = null;
    for (Entry<String, List<byte[]>> mapEntry : UPLOADED_CHUNKS.entrySet()) {
        if (mapEntry.getKey().equals(mapKey)) {
            entry = mapEntry;
            break;
        }
    }

    return entry;
}

/**
 * @return pending upload files size
 */
public static int size() {
    return UPLOADED_CHUNKS.size();
}

/**
 * Remove a file upload
 * 
 * @param key
 */
public static void remove(String key) {
    UPLOADED_CHUNKS.remove(key);
}

/**
 * This method cleans concurrent uploading files in two executions. It's ready
 * to be called on a cronable method to check if there are pending incomplete
 * files without changes in the interval.
 */
public static void cleanup() {
    Date date = new Date();
    if (LAST_CHECK == null) {
        LAST_CHECK = date;
    }
    // remove incomplete
    if (date.getTime() - LAST_CHECK.getTime() > MIN_INTERVAL) {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Cleaning pending incomplete uploads");
        LAST_CHECK = date;
        for (String key : PENDING_CHUNKS.keySet()) {
            if (UPLOADED_CHUNKS.get(key) != null) {
                Integer size = UPLOADED_CHUNKS.get(key).size();
                if (PENDING_CHUNKS.get(key).equals(size)) {
                    if (LOGGER.isInfoEnabled())
                        LOGGER.info("Removing incomplete upload [" + key + "]");
                    UPLOADED_CHUNKS.remove(key);
                    PENDING_CHUNKS.remove(key);
                } else {
                    PENDING_CHUNKS.put(key, size);
                }
            } else {
                PENDING_CHUNKS.remove(key);
            }
        }
    }
    // save size
    for (String key : UPLOADED_CHUNKS.keySet()) {
        PENDING_CHUNKS.put(key, UPLOADED_CHUNKS.get(key).size());
    }
}

}

}
