package org.egret.java.egretruntimelauncher;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * 渠道接入Runtime.请勿修改此类.
 * @author imathliu
 *
 */
public class EgretRuntimeVersion {
    private static final String JSON_RUNTIME = "runtime";
    private static final String JSON_URL = "url";
    private static final String JSON_LIBRARY = "library";

    private ArrayList<Library> libraryList;
    private static EgretRuntimeVersion versionLab = null;
    private String url;

    public static EgretRuntimeVersion get() {
        if (versionLab == null) {
            versionLab = new EgretRuntimeVersion();
        }
        return versionLab;
    }

    private EgretRuntimeVersion() {
        libraryList = new ArrayList<Library>();
        url = null;
    }

    public void setLibraryLabBy(String content) {
        ArrayList<Library> result = new ArrayList<Library>();
        JSONObject json;
        try {
            json = new JSONObject(new JSONTokener(content));
            JSONObject runtime = json.getJSONObject(JSON_RUNTIME);
            this.url = runtime.getString(JSON_URL);
            JSONArray libs = runtime.getJSONArray(JSON_LIBRARY);
            for (int i = 0; i < libs.length(); i++) {
                result.add(new Library((JSONObject) libs.get(i)));
            }
            libraryList = result;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getUrlBy(String name) {
        if (name == null) {
            return null;
        }
        if (url.endsWith("/")) {
            return url + name;
        } else {
            return url + "/" + name;
        }
    }

    public ArrayList<Library> getLibraryList() {
        return libraryList;
    }

}
