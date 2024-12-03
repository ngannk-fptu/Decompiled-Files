/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package com.amazonaws.services.s3.internal;

import com.amazonaws.util.StringUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.StringTokenizer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Mimetypes {
    private static final Log log = LogFactory.getLog(Mimetypes.class);
    public static final String MIMETYPE_XML = "application/xml";
    public static final String MIMETYPE_HTML = "text/html";
    public static final String MIMETYPE_OCTET_STREAM = "application/octet-stream";
    public static final String MIMETYPE_GZIP = "application/x-gzip";
    private static Mimetypes mimetypes = null;
    private HashMap<String, String> extensionToMimetypeMap = new HashMap();

    private Mimetypes() {
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static synchronized Mimetypes getInstance() {
        if (mimetypes != null) {
            return mimetypes;
        }
        mimetypes = new Mimetypes();
        InputStream is = mimetypes.getClass().getResourceAsStream("/mime.types");
        if (is != null) {
            if (log.isDebugEnabled()) {
                log.debug((Object)"Loading mime types from file in the classpath: mime.types");
            }
            try {
                mimetypes.loadAndReplaceMimetypes(is);
                return mimetypes;
            }
            catch (IOException e) {
                if (!log.isErrorEnabled()) return mimetypes;
                log.error((Object)"Failed to load mime types from file in the classpath: mime.types", (Throwable)e);
                return mimetypes;
            }
            finally {
                try {
                    is.close();
                }
                catch (IOException ex) {
                    log.debug((Object)"", (Throwable)ex);
                }
            }
        }
        if (!log.isWarnEnabled()) return mimetypes;
        log.warn((Object)"Unable to find 'mime.types' file in classpath");
        return mimetypes;
    }

    public void loadAndReplaceMimetypes(InputStream is) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(is, StringUtils.UTF8));
        String line = null;
        while ((line = br.readLine()) != null) {
            if ((line = line.trim()).startsWith("#") || line.length() == 0) continue;
            StringTokenizer st = new StringTokenizer(line, " \t");
            if (st.countTokens() > 1) {
                String mimetype = st.nextToken();
                while (st.hasMoreTokens()) {
                    String extension = st.nextToken();
                    this.extensionToMimetypeMap.put(StringUtils.lowerCase(extension), mimetype);
                    if (!log.isDebugEnabled()) continue;
                    log.debug((Object)("Setting mime type for extension '" + StringUtils.lowerCase(extension) + "' to '" + mimetype + "'"));
                }
                continue;
            }
            if (!log.isDebugEnabled()) continue;
            log.debug((Object)("Ignoring mimetype with no associated file extensions: '" + line + "'"));
        }
    }

    public String getMimetype(String fileName) {
        int lastPeriodIndex = fileName.lastIndexOf(".");
        if (lastPeriodIndex > 0 && lastPeriodIndex + 1 < fileName.length()) {
            String ext = StringUtils.lowerCase(fileName.substring(lastPeriodIndex + 1));
            if (this.extensionToMimetypeMap.keySet().contains(ext)) {
                String mimetype = this.extensionToMimetypeMap.get(ext);
                if (log.isDebugEnabled()) {
                    log.debug((Object)("Recognised extension '" + ext + "', mimetype is: '" + mimetype + "'"));
                }
                return mimetype;
            }
            if (log.isDebugEnabled()) {
                log.debug((Object)("Extension '" + ext + "' is unrecognized in mime type listing, using default mime type: '" + MIMETYPE_OCTET_STREAM + "'"));
            }
        } else if (log.isDebugEnabled()) {
            log.debug((Object)("File name has no extension, mime type cannot be recognised for: " + fileName));
        }
        return MIMETYPE_OCTET_STREAM;
    }

    public String getMimetype(File file) {
        return this.getMimetype(file.getName());
    }
}

