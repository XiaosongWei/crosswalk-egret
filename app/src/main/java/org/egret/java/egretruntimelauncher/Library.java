package org.egret.java.egretruntimelauncher;

import org.json.JSONObject;

/**
 * 渠道接入Runtime.请勿修改此类.
 * @author imathliu
 *
 */
public class Library {
    private static final String JSON_ZIP_NAME = "name";
    private static final String JSON_LIBRARY_CHECKSUM = "md5";
    private static final String JSON_ZIP_CHECKSUM = "zip";

    private String zipName;
    private String libraryCheckSum;
    private String zipCheckSum;
    private String libraryName;

    public Library(JSONObject json) {
        try {
            zipName = json.getString(JSON_ZIP_NAME);
            libraryCheckSum = json.getString(JSON_LIBRARY_CHECKSUM);
            zipCheckSum = json.getString(JSON_ZIP_CHECKSUM);
            if (zipName == null) {
                libraryName = null;
                return;
            }
            int end = zipName.lastIndexOf(".zip");
            libraryName = end < 0 ? null : zipName.substring(0, end);
        } catch (Exception e) {
            e.printStackTrace();
            zipName = null;
            libraryName = null;
            libraryCheckSum = null;
            zipCheckSum = null;
        }
    }

    public String getZipName() {
        return zipName;
    }

    public String getLibraryCheckSum() {
        return libraryCheckSum;
    }

    public String getZipCheckSum() {
        return zipCheckSum;
    }

    public String getLibraryName() {
        return libraryName;
    }
}
