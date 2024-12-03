/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.util;

import java.io.BufferedInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;
import java.util.zip.ZipException;

public class ParsedURLData {
    protected static final String HTTP_USER_AGENT_HEADER = "User-Agent";
    protected static final String HTTP_ACCEPT_HEADER = "Accept";
    protected static final String HTTP_ACCEPT_LANGUAGE_HEADER = "Accept-Language";
    protected static final String HTTP_ACCEPT_ENCODING_HEADER = "Accept-Encoding";
    protected static List acceptedEncodings = new LinkedList();
    public static final byte[] GZIP_MAGIC;
    public String protocol = null;
    public String host = null;
    public int port = -1;
    public String path = null;
    public String ref = null;
    public String contentType = null;
    public String contentEncoding = null;
    public InputStream stream = null;
    public boolean hasBeenOpened = false;
    protected String contentTypeMediaType;
    protected String contentTypeCharset;
    protected URL postConnectionURL;

    public static InputStream checkGZIP(InputStream is) throws IOException {
        int chk;
        if (!is.markSupported()) {
            is = new BufferedInputStream(is);
        }
        byte[] data = new byte[2];
        try {
            is.mark(2);
            is.read(data);
            is.reset();
        }
        catch (Exception ex) {
            is.reset();
            return is;
        }
        if (data[0] == GZIP_MAGIC[0] && data[1] == GZIP_MAGIC[1]) {
            return new GZIPInputStream(is);
        }
        if ((data[0] & 0xF) == 8 && data[0] >>> 4 <= 7 && (chk = (data[0] & 0xFF) * 256 + (data[1] & 0xFF)) % 31 == 0) {
            try {
                is.mark(100);
                FilterInputStream ret = new InflaterInputStream(is);
                if (!((InputStream)ret).markSupported()) {
                    ret = new BufferedInputStream(ret);
                }
                ((InputStream)ret).mark(2);
                ((InputStream)ret).read(data);
                is.reset();
                ret = new InflaterInputStream(is);
                return ret;
            }
            catch (ZipException ze) {
                is.reset();
                return is;
            }
        }
        return is;
    }

    public ParsedURLData() {
    }

    public ParsedURLData(URL url) {
        this.protocol = url.getProtocol();
        if (this.protocol != null && this.protocol.length() == 0) {
            this.protocol = null;
        }
        this.host = url.getHost();
        if (this.host != null && this.host.length() == 0) {
            this.host = null;
        }
        this.port = url.getPort();
        this.path = url.getFile();
        if (this.path != null && this.path.length() == 0) {
            this.path = null;
        }
        this.ref = url.getRef();
        if (this.ref != null && this.ref.length() == 0) {
            this.ref = null;
        }
    }

    protected URL buildURL() throws MalformedURLException {
        if (this.protocol != null && this.host != null) {
            String file = "";
            if (this.path != null) {
                file = this.path;
            }
            if (this.port == -1) {
                return new URL(this.protocol, this.host, file);
            }
            return new URL(this.protocol, this.host, this.port, file);
        }
        return new URL(this.toString());
    }

    public int hashCode() {
        int len;
        int hc = this.port;
        if (this.protocol != null) {
            hc ^= this.protocol.hashCode();
        }
        if (this.host != null) {
            hc ^= this.host.hashCode();
        }
        if (this.path != null) {
            len = this.path.length();
            hc = len > 20 ? (hc ^= this.path.substring(len - 20).hashCode()) : (hc ^= this.path.hashCode());
        }
        if (this.ref != null) {
            len = this.ref.length();
            hc = len > 20 ? (hc ^= this.ref.substring(len - 20).hashCode()) : (hc ^= this.ref.hashCode());
        }
        return hc;
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof ParsedURLData)) {
            return false;
        }
        ParsedURLData ud = (ParsedURLData)obj;
        if (ud.port != this.port) {
            return false;
        }
        if (ud.protocol == null) {
            if (this.protocol != null) {
                return false;
            }
        } else {
            if (this.protocol == null) {
                return false;
            }
            if (!ud.protocol.equals(this.protocol)) {
                return false;
            }
        }
        if (ud.host == null) {
            if (this.host != null) {
                return false;
            }
        } else {
            if (this.host == null) {
                return false;
            }
            if (!ud.host.equals(this.host)) {
                return false;
            }
        }
        if (ud.ref == null) {
            if (this.ref != null) {
                return false;
            }
        } else {
            if (this.ref == null) {
                return false;
            }
            if (!ud.ref.equals(this.ref)) {
                return false;
            }
        }
        if (ud.path == null) {
            if (this.path != null) {
                return false;
            }
        } else {
            if (this.path == null) {
                return false;
            }
            if (!ud.path.equals(this.path)) {
                return false;
            }
        }
        return true;
    }

    public String getContentType(String userAgent) {
        if (this.contentType != null) {
            return this.contentType;
        }
        if (!this.hasBeenOpened) {
            try {
                this.openStreamInternal(userAgent, null, null);
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }
        return this.contentType;
    }

    public String getContentTypeMediaType(String userAgent) {
        if (this.contentTypeMediaType != null) {
            return this.contentTypeMediaType;
        }
        this.extractContentTypeParts(userAgent);
        return this.contentTypeMediaType;
    }

    public String getContentTypeCharset(String userAgent) {
        if (this.contentTypeMediaType != null) {
            return this.contentTypeCharset;
        }
        this.extractContentTypeParts(userAgent);
        return this.contentTypeCharset;
    }

    public boolean hasContentTypeParameter(String userAgent, String param) {
        int i;
        this.getContentType(userAgent);
        if (this.contentType == null) {
            return false;
        }
        int len = this.contentType.length();
        int plen = param.length();
        block3: for (i = 0; i < len; ++i) {
            switch (this.contentType.charAt(i)) {
                case ' ': 
                case ';': {
                    break block3;
                }
                default: {
                    continue block3;
                }
            }
        }
        this.contentTypeMediaType = i == len ? this.contentType : this.contentType.substring(0, i);
        block4: while (true) {
            if (i < len && this.contentType.charAt(i) != ';') {
                ++i;
                continue;
            }
            if (i == len) {
                return false;
            }
            ++i;
            while (i < len && this.contentType.charAt(i) == ' ') {
                ++i;
            }
            if (i >= len - plen - 1) {
                return false;
            }
            for (int j = 0; j < plen; ++j) {
                if (this.contentType.charAt(i++) != param.charAt(j)) continue block4;
            }
            if (this.contentType.charAt(i) == '=') break;
        }
        return true;
    }

    protected void extractContentTypeParts(String userAgent) {
        int i;
        this.getContentType(userAgent);
        if (this.contentType == null) {
            return;
        }
        int len = this.contentType.length();
        block6: for (i = 0; i < len; ++i) {
            switch (this.contentType.charAt(i)) {
                case ' ': 
                case ';': {
                    break block6;
                }
                default: {
                    continue block6;
                }
            }
        }
        this.contentTypeMediaType = i == len ? this.contentType : this.contentType.substring(0, i);
        while (true) {
            if (i < len && this.contentType.charAt(i) != ';') {
                ++i;
                continue;
            }
            if (i == len) {
                return;
            }
            ++i;
            while (i < len && this.contentType.charAt(i) == ' ') {
                ++i;
            }
            if (i >= len - 8) {
                return;
            }
            if (this.contentType.charAt(i++) == 'c' && this.contentType.charAt(i++) == 'h' && this.contentType.charAt(i++) == 'a' && this.contentType.charAt(i++) == 'r' && this.contentType.charAt(i++) == 's' && this.contentType.charAt(i++) == 'e' && this.contentType.charAt(i++) == 't' && this.contentType.charAt(i++) == '=') break;
        }
        int j = i;
        block9: while (i < len) {
            switch (this.contentType.charAt(i)) {
                case ' ': 
                case ';': {
                    break block9;
                }
                default: {
                    ++i;
                    continue block9;
                }
            }
        }
        this.contentTypeCharset = this.contentType.substring(j, i);
    }

    public String getContentEncoding(String userAgent) {
        if (this.contentEncoding != null) {
            return this.contentEncoding;
        }
        if (!this.hasBeenOpened) {
            try {
                this.openStreamInternal(userAgent, null, null);
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }
        return this.contentEncoding;
    }

    public boolean complete() {
        try {
            this.buildURL();
        }
        catch (MalformedURLException mue) {
            return false;
        }
        return true;
    }

    public InputStream openStream(String userAgent, Iterator mimeTypes) throws IOException {
        InputStream raw = this.openStreamInternal(userAgent, mimeTypes, acceptedEncodings.iterator());
        if (raw == null) {
            return null;
        }
        this.stream = null;
        return ParsedURLData.checkGZIP(raw);
    }

    public InputStream openStreamRaw(String userAgent, Iterator mimeTypes) throws IOException {
        InputStream ret = this.openStreamInternal(userAgent, mimeTypes, null);
        this.stream = null;
        return ret;
    }

    protected InputStream openStreamInternal(String userAgent, Iterator mimeTypes, Iterator encodingTypes) throws IOException {
        if (this.stream != null) {
            return this.stream;
        }
        this.hasBeenOpened = true;
        URL url = null;
        try {
            url = this.buildURL();
        }
        catch (MalformedURLException mue) {
            throw new IOException("Unable to make sense of URL for connection");
        }
        if (url == null) {
            return null;
        }
        URLConnection urlC = url.openConnection();
        if (urlC instanceof HttpURLConnection) {
            if (userAgent != null) {
                urlC.setRequestProperty(HTTP_USER_AGENT_HEADER, userAgent);
            }
            if (mimeTypes != null) {
                String acceptHeader = "";
                while (mimeTypes.hasNext()) {
                    acceptHeader = acceptHeader + mimeTypes.next();
                    if (!mimeTypes.hasNext()) continue;
                    acceptHeader = acceptHeader + ",";
                }
                urlC.setRequestProperty(HTTP_ACCEPT_HEADER, acceptHeader);
            }
            if (encodingTypes != null) {
                String encodingHeader = "";
                while (encodingTypes.hasNext()) {
                    encodingHeader = encodingHeader + encodingTypes.next();
                    if (!encodingTypes.hasNext()) continue;
                    encodingHeader = encodingHeader + ",";
                }
                urlC.setRequestProperty(HTTP_ACCEPT_ENCODING_HEADER, encodingHeader);
            }
            this.contentType = urlC.getContentType();
            this.contentEncoding = urlC.getContentEncoding();
            this.postConnectionURL = urlC.getURL();
        }
        try {
            this.stream = urlC.getInputStream();
            return this.stream;
        }
        catch (IOException e) {
            if (urlC instanceof HttpURLConnection) {
                this.stream = ((HttpURLConnection)urlC).getErrorStream();
                if (this.stream == null) {
                    throw e;
                }
                return this.stream;
            }
            throw e;
        }
    }

    public String getPortStr() {
        String portStr = "";
        if (this.protocol != null) {
            portStr = portStr + this.protocol + ":";
        }
        if (this.host != null || this.port != -1) {
            portStr = portStr + "//";
            if (this.host != null) {
                portStr = portStr + this.host;
            }
            if (this.port != -1) {
                portStr = portStr + ":" + this.port;
            }
        }
        return portStr;
    }

    protected boolean sameFile(ParsedURLData other) {
        if (this == other) {
            return true;
        }
        return this.port == other.port && (this.path == other.path || this.path != null && this.path.equals(other.path)) && (this.host == other.host || this.host != null && this.host.equals(other.host)) && (this.protocol == other.protocol || this.protocol != null && this.protocol.equals(other.protocol));
    }

    public String toString() {
        String ret = this.getPortStr();
        if (this.path != null) {
            ret = ret + this.path;
        }
        if (this.ref != null) {
            ret = ret + "#" + this.ref;
        }
        return ret;
    }

    public String getPostConnectionURL() {
        if (this.postConnectionURL != null) {
            if (this.ref != null) {
                return this.postConnectionURL.toString() + '#' + this.ref;
            }
            return this.postConnectionURL.toString();
        }
        return this.toString();
    }

    static {
        acceptedEncodings.add("gzip");
        GZIP_MAGIC = new byte[]{31, -117};
    }
}

