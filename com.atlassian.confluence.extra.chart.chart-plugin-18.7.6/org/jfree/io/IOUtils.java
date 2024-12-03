/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

public class IOUtils {
    private static IOUtils instance;

    private IOUtils() {
    }

    public static IOUtils getInstance() {
        if (instance == null) {
            instance = new IOUtils();
        }
        return instance;
    }

    private boolean isFileStyleProtocol(URL url) {
        if (url.getProtocol().equals("http")) {
            return true;
        }
        if (url.getProtocol().equals("https")) {
            return true;
        }
        if (url.getProtocol().equals("ftp")) {
            return true;
        }
        if (url.getProtocol().equals("file")) {
            return true;
        }
        return url.getProtocol().equals("jar");
    }

    private List parseName(String name) {
        ArrayList<String> list = new ArrayList<String>();
        StringTokenizer strTok = new StringTokenizer(name, "/");
        while (strTok.hasMoreElements()) {
            String s = (String)strTok.nextElement();
            if (s.length() == 0) continue;
            list.add(s);
        }
        return list;
    }

    private String formatName(List name, String query) {
        StringBuffer b = new StringBuffer();
        Iterator it = name.iterator();
        while (it.hasNext()) {
            b.append(it.next());
            if (!it.hasNext()) continue;
            b.append("/");
        }
        if (query != null) {
            b.append('?');
            b.append(query);
        }
        return b.toString();
    }

    private int startsWithUntil(List baseName, List urlName) {
        int minIdx = Math.min(urlName.size(), baseName.size());
        for (int i = 0; i < minIdx; ++i) {
            String urlToken;
            String baseToken = (String)baseName.get(i);
            if (baseToken.equals(urlToken = (String)urlName.get(i))) continue;
            return i;
        }
        return minIdx;
    }

    private boolean isSameService(URL url, URL baseUrl) {
        if (!url.getProtocol().equals(baseUrl.getProtocol())) {
            return false;
        }
        if (!url.getHost().equals(baseUrl.getHost())) {
            return false;
        }
        return url.getPort() == baseUrl.getPort();
    }

    public String createRelativeURL(URL url, URL baseURL) {
        if (url == null) {
            throw new NullPointerException("content url must not be null.");
        }
        if (baseURL == null) {
            throw new NullPointerException("baseURL must not be null.");
        }
        if (this.isFileStyleProtocol(url) && this.isSameService(url, baseURL)) {
            List urlName = this.parseName(this.getPath(url));
            List baseName = this.parseName(this.getPath(baseURL));
            String query = this.getQuery(url);
            if (!this.isPath(baseURL)) {
                baseName.remove(baseName.size() - 1);
            }
            if (url.equals(baseURL)) {
                return (String)urlName.get(urlName.size() - 1);
            }
            int commonIndex = this.startsWithUntil(urlName, baseName);
            if (commonIndex == 0) {
                return url.toExternalForm();
            }
            if (commonIndex == urlName.size()) {
                --commonIndex;
            }
            ArrayList<String> retval = new ArrayList<String>();
            if (baseName.size() >= urlName.size()) {
                int levels = baseName.size() - commonIndex;
                for (int i = 0; i < levels; ++i) {
                    retval.add("..");
                }
            }
            retval.addAll(urlName.subList(commonIndex, urlName.size()));
            return this.formatName(retval, query);
        }
        return url.toExternalForm();
    }

    private boolean isPath(URL baseURL) {
        if (this.getPath(baseURL).endsWith("/")) {
            return true;
        }
        if (baseURL.getProtocol().equals("file")) {
            File f = new File(this.getPath(baseURL));
            try {
                if (f.isDirectory()) {
                    return true;
                }
            }
            catch (SecurityException securityException) {
                // empty catch block
            }
        }
        return false;
    }

    private String getQuery(URL url) {
        String file = url.getFile();
        int queryIndex = file.indexOf(63);
        if (queryIndex == -1) {
            return null;
        }
        return file.substring(queryIndex + 1);
    }

    private String getPath(URL url) {
        String file = url.getFile();
        int queryIndex = file.indexOf(63);
        if (queryIndex == -1) {
            return file;
        }
        return file.substring(0, queryIndex);
    }

    public void copyStreams(InputStream in, OutputStream out) throws IOException {
        this.copyStreams(in, out, 4096);
    }

    public void copyStreams(InputStream in, OutputStream out, int buffersize) throws IOException {
        byte[] bytes = new byte[buffersize];
        int bytesRead = in.read(bytes);
        while (bytesRead > -1) {
            out.write(bytes, 0, bytesRead);
            bytesRead = in.read(bytes);
        }
    }

    public void copyWriter(Reader in, Writer out) throws IOException {
        this.copyWriter(in, out, 4096);
    }

    public void copyWriter(Reader in, Writer out, int buffersize) throws IOException {
        char[] bytes = new char[buffersize];
        int bytesRead = in.read(bytes);
        while (bytesRead > -1) {
            out.write(bytes, 0, bytesRead);
            bytesRead = in.read(bytes);
        }
    }

    public String getFileName(URL url) {
        String file = this.getPath(url);
        int last = file.lastIndexOf("/");
        if (last < 0) {
            return file;
        }
        return file.substring(last + 1);
    }

    public String stripFileExtension(String file) {
        int idx = file.lastIndexOf(".");
        if (idx < 1) {
            return file;
        }
        return file.substring(0, idx);
    }

    public String getFileExtension(String file) {
        int idx = file.lastIndexOf(".");
        if (idx < 1) {
            return "";
        }
        return file.substring(idx);
    }

    public boolean isSubDirectory(File base, File child) throws IOException {
        base = base.getCanonicalFile();
        for (File parentFile = child = child.getCanonicalFile(); parentFile != null; parentFile = parentFile.getParentFile()) {
            if (!base.equals(parentFile)) continue;
            return true;
        }
        return false;
    }
}

