/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.service.url;

import aQute.bnd.http.HttpRequestException;
import aQute.bnd.service.url.State;
import aQute.lib.io.IO;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URLConnection;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TaggedData
implements Closeable {
    private final URLConnection con;
    private final int responseCode;
    private final String etag;
    private final InputStream in;
    private final URI url;
    private final File file;
    private final String message;
    static final Pattern HTML_TAGS_P = Pattern.compile("<!--.*-->|<[^>]+>");
    static final Pattern NEWLINES_P = Pattern.compile("(\\s*\n\r?\\s*)+");
    static final Pattern ENTITIES_P = Pattern.compile("&(#(?<nr>[0-9]+))|(?<name>[a-z]+);", 2);

    @Deprecated
    public TaggedData(String tag, InputStream inputStream, int responseCode, long modified, URI url) {
        throw new RuntimeException();
    }

    @Deprecated
    public TaggedData(String tag, InputStream inputStream, int responseCode) {
        throw new RuntimeException();
    }

    @Deprecated
    public TaggedData(String tag, InputStream inputStream) {
        throw new RuntimeException();
    }

    public TaggedData(URLConnection con, InputStream in) throws Exception {
        this(con, in, null);
    }

    public TaggedData(URLConnection con, InputStream in, File file) throws Exception {
        this.con = con;
        this.responseCode = con instanceof HttpURLConnection ? ((HttpURLConnection)con).getResponseCode() : (in != null ? 200 : -1);
        this.in = in == null && con != null && this.responseCode / 100 == 2 ? con.getInputStream() : in;
        this.file = file;
        this.etag = con.getHeaderField("ETag");
        this.url = con.getURL().toURI();
        this.message = this.getMessage(con);
    }

    private String getMessage(URLConnection con) {
        try {
            if (con == null || !(con instanceof HttpURLConnection)) {
                return null;
            }
            HttpURLConnection h = (HttpURLConnection)con;
            if (h.getResponseCode() / 100 < 4) {
                return null;
            }
            StringBuffer sb = new StringBuffer();
            try {
                InputStream in = con.getInputStream();
                if (in != null) {
                    sb.append(IO.collect(in));
                }
            }
            catch (Exception e) {
                // empty catch block
            }
            try {
                InputStream errorStream = h.getErrorStream();
                if (errorStream != null) {
                    sb.append(IO.collect(errorStream));
                }
            }
            catch (Exception e) {
                // empty catch block
            }
            return this.cleanHtml(sb);
        }
        catch (Exception e) {
            return null;
        }
    }

    private String cleanHtml(CharSequence sb) {
        sb = HTML_TAGS_P.matcher(sb).replaceAll("");
        sb = NEWLINES_P.matcher(sb).replaceAll("\n");
        StringBuffer x = new StringBuffer();
        Matcher m = ENTITIES_P.matcher(sb);
        while (m.find()) {
            if (m.group("nr") != null) {
                char c = (char)Integer.parseInt(m.group("nr"));
                m.appendReplacement(x, "");
                x.append(c);
                continue;
            }
            m.appendReplacement(x, this.entity(m.group("name")));
        }
        m.appendTail(x);
        return x.toString();
    }

    private String entity(String name) {
        switch (name) {
            case "nbsp": {
                return "\u00a0";
            }
            case "lt": {
                return "<";
            }
            case "gt": {
                return "<";
            }
            case "amp": {
                return "&";
            }
            case "cent": {
                return "\u00a2";
            }
            case "pound": {
                return "\u00a3";
            }
            case "euro": {
                return "\u20ac";
            }
            case "copy": {
                return "\u00a9";
            }
            case "reg": {
                return "\u00ae";
            }
            case "quot": {
                return "\"";
            }
            case "apos": {
                return "'";
            }
            case "yen": {
                return "\u00a5";
            }
            case "sect": {
                return "\u00a7";
            }
            case "not": {
                return "\u00ac";
            }
            case "para": {
                return "\u00b6";
            }
            case "curren": {
                return "\u00a4";
            }
        }
        return "&" + name + ";";
    }

    public TaggedData(URI url, int responseCode, File file) throws Exception {
        this.file = file;
        this.con = null;
        this.in = null;
        this.etag = "";
        this.responseCode = responseCode;
        this.url = url;
        this.message = null;
    }

    public String getTag() {
        return this.etag;
    }

    public InputStream getInputStream() throws IOException {
        return this.in;
    }

    public int getResponseCode() {
        return this.responseCode;
    }

    public long getModified() {
        if (this.con != null) {
            return this.con.getLastModified();
        }
        return -1L;
    }

    public boolean hasPayload() throws IOException {
        return this.in != null;
    }

    public URI getUrl() {
        return this.url;
    }

    public URLConnection getConnection() {
        return this.con;
    }

    public String toString() {
        return "TaggedData [tag=" + this.getTag() + ", code=" + this.getResponseCode() + ", modified=" + new Date(this.getModified()) + ", url=" + this.getUrl() + ", state=" + (Object)((Object)this.getState()) + (this.message == null ? "" : ", msg=" + this.message) + "]";
    }

    public boolean isOk() {
        return this.getResponseCode() / 100 == 2;
    }

    public boolean isNotModified() {
        return this.responseCode == 304;
    }

    public void throwIt() {
        throw new HttpRequestException(this);
    }

    public State getState() {
        if (this.isNotFound()) {
            return State.NOT_FOUND;
        }
        if (this.isNotModified()) {
            return State.UNMODIFIED;
        }
        if (this.isOk()) {
            return State.UPDATED;
        }
        return State.OTHER;
    }

    public boolean isNotFound() {
        return this.responseCode == 404;
    }

    public File getFile() {
        return this.file;
    }

    @Override
    public void close() throws IOException {
        IO.close(this.getInputStream());
    }
}

