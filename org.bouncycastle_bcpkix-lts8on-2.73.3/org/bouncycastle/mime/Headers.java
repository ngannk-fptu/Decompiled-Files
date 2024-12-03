/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.util.Iterable
 *  org.bouncycastle.util.Strings
 */
package org.bouncycastle.mime;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.bouncycastle.mime.LineReader;
import org.bouncycastle.util.Iterable;
import org.bouncycastle.util.Strings;

public class Headers
implements Iterable<String> {
    private final Map<String, List> headers = new TreeMap<String, List>(String.CASE_INSENSITIVE_ORDER);
    private final List<String> headersAsPresented;
    private final String contentTransferEncoding;
    private String boundary;
    private boolean multipart;
    private String contentType;
    private Map<String, String> contentTypeParameters;

    private static List<String> parseHeaders(InputStream src) throws IOException {
        String s;
        ArrayList<String> headerLines = new ArrayList<String>();
        LineReader rd = new LineReader(src);
        while ((s = rd.readLine()) != null && s.length() != 0) {
            headerLines.add(s);
        }
        return headerLines;
    }

    public Headers(String contentType, String defaultContentTransferEncoding) {
        String header = "Content-Type: " + contentType;
        this.headersAsPresented = new ArrayList<String>();
        this.headersAsPresented.add(header);
        this.put("Content-Type", contentType);
        String contentTypeHeader = this.getValues("Content-Type") == null ? "text/plain" : this.getValues("Content-Type")[0];
        int parameterIndex = contentTypeHeader.indexOf(59);
        if (parameterIndex < 0) {
            contentType = contentTypeHeader;
            this.contentTypeParameters = Collections.EMPTY_MAP;
        } else {
            contentType = contentTypeHeader.substring(0, parameterIndex);
            this.contentTypeParameters = this.createContentTypeParameters(contentTypeHeader.substring(parameterIndex + 1).trim());
        }
        String string = this.contentTransferEncoding = this.getValues("Content-Transfer-Encoding") == null ? defaultContentTransferEncoding : this.getValues("Content-Transfer-Encoding")[0];
        if (contentType.indexOf("multipart") >= 0) {
            this.multipart = true;
            String bound = this.contentTypeParameters.get("boundary");
            this.boundary = bound.startsWith("\"") && bound.endsWith("\"") ? bound.substring(1, bound.length() - 1) : bound;
        } else {
            this.boundary = null;
            this.multipart = false;
        }
    }

    public Headers(InputStream source, String defaultContentTransferEncoding) throws IOException {
        this(Headers.parseHeaders(source), defaultContentTransferEncoding);
    }

    public Headers(List<String> headerLines, String defaultContentTransferEncoding) {
        String contentTypeHeader;
        int parameterIndex;
        this.headersAsPresented = headerLines;
        String header = "";
        for (String line : headerLines) {
            if (line.startsWith(" ") || line.startsWith("\t")) {
                header = header + line.trim();
                continue;
            }
            if (header.length() != 0) {
                this.put(header.substring(0, header.indexOf(58)).trim(), header.substring(header.indexOf(58) + 1).trim());
            }
            header = line;
        }
        if (header.trim().length() != 0) {
            this.put(header.substring(0, header.indexOf(58)).trim(), header.substring(header.indexOf(58) + 1).trim());
        }
        if ((parameterIndex = (contentTypeHeader = this.getValues("Content-Type") == null ? "text/plain" : this.getValues("Content-Type")[0]).indexOf(59)) < 0) {
            this.contentType = contentTypeHeader;
            this.contentTypeParameters = Collections.EMPTY_MAP;
        } else {
            this.contentType = contentTypeHeader.substring(0, parameterIndex);
            this.contentTypeParameters = this.createContentTypeParameters(contentTypeHeader.substring(parameterIndex + 1).trim());
        }
        String string = this.contentTransferEncoding = this.getValues("Content-Transfer-Encoding") == null ? defaultContentTransferEncoding : this.getValues("Content-Transfer-Encoding")[0];
        if (this.contentType.indexOf("multipart") >= 0) {
            this.multipart = true;
            String bound = this.contentTypeParameters.get("boundary");
            this.boundary = bound.substring(1, bound.length() - 1);
        } else {
            this.boundary = null;
            this.multipart = false;
        }
    }

    public Map<String, String> getContentTypeAttributes() {
        return this.contentTypeParameters;
    }

    private Map<String, String> createContentTypeParameters(String contentTypeParameters) {
        String[] parameterSplit = contentTypeParameters.split(";");
        LinkedHashMap<String, String> rv = new LinkedHashMap<String, String>();
        for (int i = 0; i != parameterSplit.length; ++i) {
            String parameter = parameterSplit[i];
            int eqIndex = parameter.indexOf(61);
            if (eqIndex < 0) {
                throw new IllegalArgumentException("malformed Content-Type header");
            }
            rv.put(parameter.substring(0, eqIndex).trim(), parameter.substring(eqIndex + 1).trim());
        }
        return Collections.unmodifiableMap(rv);
    }

    public boolean isMultipart() {
        return this.multipart;
    }

    public String getBoundary() {
        return this.boundary;
    }

    public String getContentType() {
        return this.contentType;
    }

    public String getContentTransferEncoding() {
        return this.contentTransferEncoding;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void put(String field, String value) {
        Headers headers = this;
        synchronized (headers) {
            KV kv = new KV(field, value);
            ArrayList<KV> list = this.headers.get(field);
            if (list == null) {
                list = new ArrayList<KV>();
                this.headers.put(field, list);
            }
            list.add(kv);
        }
    }

    public Iterator<String> getNames() {
        return this.headers.keySet().iterator();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public String[] getValues(String header) {
        Headers headers = this;
        synchronized (headers) {
            List kvList = this.headers.get(header);
            if (kvList == null) {
                return null;
            }
            String[] out = new String[kvList.size()];
            for (int t = 0; t < kvList.size(); ++t) {
                out[t] = ((KV)kvList.get((int)t)).value;
            }
            return out;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean isEmpty() {
        Headers headers = this;
        synchronized (headers) {
            return this.headers.isEmpty();
        }
    }

    public boolean containsKey(String s) {
        return this.headers.containsKey(s);
    }

    public Iterator<String> iterator() {
        return this.headers.keySet().iterator();
    }

    public void dumpHeaders(OutputStream outputStream) throws IOException {
        Iterator<String> it = this.headersAsPresented.iterator();
        while (it.hasNext()) {
            outputStream.write(Strings.toUTF8ByteArray((String)it.next().toString()));
            outputStream.write(13);
            outputStream.write(10);
        }
    }

    private static class KV {
        public final String key;
        public final String value;

        public KV(String key, String value) {
            this.key = key;
            this.value = value;
        }

        public KV(KV kv) {
            this.key = kv.key;
            this.value = kv.value;
        }
    }
}

