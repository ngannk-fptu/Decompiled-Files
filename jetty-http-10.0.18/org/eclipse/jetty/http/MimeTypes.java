/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.jetty.util.BufferUtil
 *  org.eclipse.jetty.util.Index
 *  org.eclipse.jetty.util.Index$Builder
 *  org.eclipse.jetty.util.StringUtil
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.eclipse.jetty.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import org.eclipse.jetty.http.HttpField;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.PreEncodedHttpField;
import org.eclipse.jetty.util.BufferUtil;
import org.eclipse.jetty.util.Index;
import org.eclipse.jetty.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MimeTypes {
    private static final Logger LOG;
    private static final Map<String, String> __dftMimeMap;
    private static final Map<String, String> __inferredEncodings;
    private static final Map<String, String> __assumedEncodings;
    public static final Index<Type> CACHE;
    private final Map<String, String> _mimeMap = new HashMap<String, String>();

    public Map<String, String> getMimeMap() {
        return this._mimeMap;
    }

    public void setMimeMap(Map<String, String> mimeMap) {
        this._mimeMap.clear();
        if (mimeMap != null) {
            for (Map.Entry<String, String> ext : mimeMap.entrySet()) {
                this._mimeMap.put(StringUtil.asciiToLowerCase((String)ext.getKey()), MimeTypes.normalizeMimeType(ext.getValue()));
            }
        }
    }

    public static String getDefaultMimeByExtension(String filename) {
        String type = null;
        if (filename != null) {
            int i = -1;
            while (type == null && (i = filename.indexOf(".", i + 1)) >= 0 && i < filename.length()) {
                String ext = StringUtil.asciiToLowerCase((String)filename.substring(i + 1));
                if (type != null) continue;
                type = __dftMimeMap.get(ext);
            }
        }
        if (type == null) {
            type = __dftMimeMap.get("*");
        }
        return type;
    }

    public String getMimeByExtension(String filename) {
        String type = null;
        if (filename != null) {
            int i = -1;
            while (type == null && (i = filename.indexOf(".", i + 1)) >= 0 && i < filename.length()) {
                String ext = StringUtil.asciiToLowerCase((String)filename.substring(i + 1));
                if (this._mimeMap != null) {
                    type = this._mimeMap.get(ext);
                }
                if (type != null) continue;
                type = __dftMimeMap.get(ext);
            }
        }
        if (type == null) {
            if (this._mimeMap != null) {
                type = this._mimeMap.get("*");
            }
            if (type == null) {
                type = __dftMimeMap.get("*");
            }
        }
        return type;
    }

    public void addMimeMapping(String extension, String type) {
        this._mimeMap.put(StringUtil.asciiToLowerCase((String)extension), MimeTypes.normalizeMimeType(type));
    }

    public static Set<String> getKnownMimeTypes() {
        return new HashSet<String>(__dftMimeMap.values());
    }

    private static String normalizeMimeType(String type) {
        Type t = (Type)((Object)CACHE.get(type));
        if (t != null) {
            return t.asString();
        }
        return StringUtil.asciiToLowerCase((String)type);
    }

    public static String getCharsetFromContentType(String value) {
        int i;
        if (value == null) {
            return null;
        }
        int end = value.length();
        int state = 0;
        int start = 0;
        boolean quote = false;
        block13: for (i = 0; i < end; ++i) {
            char b = value.charAt(i);
            if (quote && state != 10) {
                if ('\"' != b) continue;
                quote = false;
                continue;
            }
            if (';' == b && state <= 8) {
                state = 1;
                continue;
            }
            switch (state) {
                case 0: {
                    if ('\"' != b) continue block13;
                    quote = true;
                    continue block13;
                }
                case 1: {
                    if ('c' == b) {
                        state = 2;
                        continue block13;
                    }
                    if (' ' == b) continue block13;
                    state = 0;
                    continue block13;
                }
                case 2: {
                    if ('h' == b) {
                        state = 3;
                        continue block13;
                    }
                    state = 0;
                    continue block13;
                }
                case 3: {
                    if ('a' == b) {
                        state = 4;
                        continue block13;
                    }
                    state = 0;
                    continue block13;
                }
                case 4: {
                    if ('r' == b) {
                        state = 5;
                        continue block13;
                    }
                    state = 0;
                    continue block13;
                }
                case 5: {
                    if ('s' == b) {
                        state = 6;
                        continue block13;
                    }
                    state = 0;
                    continue block13;
                }
                case 6: {
                    if ('e' == b) {
                        state = 7;
                        continue block13;
                    }
                    state = 0;
                    continue block13;
                }
                case 7: {
                    if ('t' == b) {
                        state = 8;
                        continue block13;
                    }
                    state = 0;
                    continue block13;
                }
                case 8: {
                    if ('=' == b) {
                        state = 9;
                        continue block13;
                    }
                    if (' ' == b) continue block13;
                    state = 0;
                    continue block13;
                }
                case 9: {
                    if (' ' == b) continue block13;
                    if ('\"' == b) {
                        quote = true;
                        start = i + 1;
                        state = 10;
                        continue block13;
                    }
                    start = i;
                    state = 10;
                    continue block13;
                }
                case 10: {
                    if ((quote || ';' != b && ' ' != b) && (!quote || '\"' != b)) continue block13;
                    return StringUtil.normalizeCharset((String)value, (int)start, (int)(i - start));
                }
                default: {
                    throw new IllegalStateException();
                }
            }
        }
        if (state == 10) {
            return StringUtil.normalizeCharset((String)value, (int)start, (int)(i - start));
        }
        return null;
    }

    public static Map<String, String> getInferredEncodings() {
        return __inferredEncodings;
    }

    public static Map<String, String> getAssumedEncodings() {
        return __assumedEncodings;
    }

    public static String getCharsetInferredFromContentType(String contentType) {
        return __inferredEncodings.get(contentType);
    }

    public static String getCharsetAssumedFromContentType(String contentType) {
        return __assumedEncodings.get(contentType);
    }

    public static String getContentTypeWithoutCharset(String value) {
        int end = value.length();
        int state = 0;
        int start = 0;
        boolean quote = false;
        StringBuilder builder = null;
        block19: for (int i = 0; i < end; ++i) {
            char b = value.charAt(i);
            if ('\"' == b) {
                quote = !quote;
                switch (state) {
                    case 11: {
                        builder.append(b);
                        break;
                    }
                    case 10: {
                        break;
                    }
                    case 9: {
                        builder = new StringBuilder();
                        builder.append(value, 0, start + 1);
                        state = 10;
                        break;
                    }
                    default: {
                        start = i;
                        state = 0;
                        break;
                    }
                }
                continue;
            }
            if (quote) {
                if (builder == null || state == 10) continue;
                builder.append(b);
                continue;
            }
            switch (state) {
                case 0: {
                    if (';' == b) {
                        state = 1;
                        continue block19;
                    }
                    if (' ' == b) continue block19;
                    start = i;
                    continue block19;
                }
                case 1: {
                    if ('c' == b) {
                        state = 2;
                        continue block19;
                    }
                    if (' ' == b) continue block19;
                    state = 0;
                    continue block19;
                }
                case 2: {
                    if ('h' == b) {
                        state = 3;
                        continue block19;
                    }
                    state = 0;
                    continue block19;
                }
                case 3: {
                    if ('a' == b) {
                        state = 4;
                        continue block19;
                    }
                    state = 0;
                    continue block19;
                }
                case 4: {
                    if ('r' == b) {
                        state = 5;
                        continue block19;
                    }
                    state = 0;
                    continue block19;
                }
                case 5: {
                    if ('s' == b) {
                        state = 6;
                        continue block19;
                    }
                    state = 0;
                    continue block19;
                }
                case 6: {
                    if ('e' == b) {
                        state = 7;
                        continue block19;
                    }
                    state = 0;
                    continue block19;
                }
                case 7: {
                    if ('t' == b) {
                        state = 8;
                        continue block19;
                    }
                    state = 0;
                    continue block19;
                }
                case 8: {
                    if ('=' == b) {
                        state = 9;
                        continue block19;
                    }
                    if (' ' == b) continue block19;
                    state = 0;
                    continue block19;
                }
                case 9: {
                    if (' ' == b) continue block19;
                    builder = new StringBuilder();
                    builder.append(value, 0, start + 1);
                    state = 10;
                    continue block19;
                }
                case 10: {
                    if (';' != b) continue block19;
                    builder.append(b);
                    state = 11;
                    continue block19;
                }
                case 11: {
                    if (' ' == b) continue block19;
                    builder.append(b);
                    continue block19;
                }
                default: {
                    throw new IllegalStateException();
                }
            }
        }
        if (builder == null) {
            return value;
        }
        return builder.toString();
    }

    static {
        block44: {
            Properties props;
            InputStream stream;
            String resourceName;
            block43: {
                LOG = LoggerFactory.getLogger(MimeTypes.class);
                __dftMimeMap = new HashMap<String, String>();
                __inferredEncodings = new HashMap<String, String>();
                __assumedEncodings = new HashMap<String, String>();
                CACHE = new Index.Builder().caseSensitive(false).withAll(() -> {
                    HashMap<String, Type> result = new HashMap<String, Type>();
                    for (Type type : Type.values()) {
                        String key1 = type.toString();
                        result.put(key1, type);
                        if (key1.indexOf(";charset=") <= 0) continue;
                        String key2 = StringUtil.replace((String)key1, (String)";charset=", (String)"; charset=");
                        result.put(key2, type);
                    }
                    return result;
                }).build();
                for (Type type : Type.values()) {
                    if (!type.isCharsetAssumed()) continue;
                    __assumedEncodings.put(type.asString(), type.getCharsetString());
                }
                resourceName = "mime.properties";
                try {
                    stream = MimeTypes.class.getResourceAsStream(resourceName);
                    try {
                        if (stream == null) {
                            LOG.warn("Missing mime-type resource: {}", (Object)resourceName);
                            break block43;
                        }
                        try (InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8);){
                            props = new Properties();
                            props.load(reader);
                            props.stringPropertyNames().stream().filter(x -> x != null).forEach(x -> __dftMimeMap.put(StringUtil.asciiToLowerCase((String)x), MimeTypes.normalizeMimeType(props.getProperty((String)x))));
                            if (__dftMimeMap.isEmpty()) {
                                LOG.warn("Empty mime types at {}", (Object)resourceName);
                            } else if (__dftMimeMap.size() < props.keySet().size()) {
                                LOG.warn("Duplicate or null mime-type extension in resource: {}", (Object)resourceName);
                            }
                        }
                        catch (IOException e) {
                            if (LOG.isDebugEnabled()) {
                                LOG.warn("Unable to read mime-type resource: {}", (Object)resourceName, (Object)e);
                                break block43;
                            }
                            LOG.warn("Unable to read mime-type resource: {} - {}", (Object)resourceName, (Object)e.toString());
                        }
                    }
                    finally {
                        if (stream != null) {
                            stream.close();
                        }
                    }
                }
                catch (IOException e) {
                    if (LOG.isDebugEnabled()) {
                        LOG.warn("Unable to load mime-type resource: {}", (Object)resourceName, (Object)e);
                    }
                    LOG.warn("Unable to load mime-type resource: {} - {}", (Object)resourceName, (Object)e.toString());
                }
            }
            resourceName = "encoding.properties";
            try {
                stream = MimeTypes.class.getResourceAsStream(resourceName);
                try {
                    if (stream == null) {
                        LOG.warn("Missing encoding resource: {}", (Object)resourceName);
                        break block44;
                    }
                    try (InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8);){
                        props = new Properties();
                        props.load(reader);
                        props.stringPropertyNames().stream().filter(t -> t != null).forEach(t -> {
                            String charset = props.getProperty((String)t);
                            if (charset.startsWith("-")) {
                                __assumedEncodings.put((String)t, charset.substring(1));
                            } else {
                                __inferredEncodings.put((String)t, props.getProperty((String)t));
                            }
                        });
                        if (__inferredEncodings.isEmpty()) {
                            LOG.warn("Empty encodings at {}", (Object)resourceName);
                        } else if (__inferredEncodings.size() + __assumedEncodings.size() < props.keySet().size()) {
                            LOG.warn("Null or duplicate encodings in resource: {}", (Object)resourceName);
                        }
                    }
                    catch (IOException e) {
                        if (LOG.isDebugEnabled()) {
                            LOG.warn("Unable to read encoding resource: {}", (Object)resourceName, (Object)e);
                            break block44;
                        }
                        LOG.warn("Unable to read encoding resource: {} - {}", (Object)resourceName, (Object)e.toString());
                    }
                }
                finally {
                    if (stream != null) {
                        stream.close();
                    }
                }
            }
            catch (IOException e) {
                if (LOG.isDebugEnabled()) {
                    LOG.warn("Unable to load encoding resource: {}", (Object)resourceName, (Object)e);
                }
                LOG.warn("Unable to load encoding resource: {} - {}", (Object)resourceName, (Object)e.toString());
            }
        }
    }

    public static enum Type {
        FORM_ENCODED("application/x-www-form-urlencoded"),
        MESSAGE_HTTP("message/http"),
        MULTIPART_BYTERANGES("multipart/byteranges"),
        MULTIPART_FORM_DATA("multipart/form-data"),
        TEXT_HTML("text/html"),
        TEXT_PLAIN("text/plain"),
        TEXT_XML("text/xml"),
        TEXT_JSON("text/json", StandardCharsets.UTF_8),
        APPLICATION_JSON("application/json", StandardCharsets.UTF_8),
        TEXT_HTML_8859_1("text/html;charset=iso-8859-1", TEXT_HTML),
        TEXT_HTML_UTF_8("text/html;charset=utf-8", TEXT_HTML),
        TEXT_PLAIN_8859_1("text/plain;charset=iso-8859-1", TEXT_PLAIN),
        TEXT_PLAIN_UTF_8("text/plain;charset=utf-8", TEXT_PLAIN),
        TEXT_XML_8859_1("text/xml;charset=iso-8859-1", TEXT_XML),
        TEXT_XML_UTF_8("text/xml;charset=utf-8", TEXT_XML),
        TEXT_JSON_8859_1("text/json;charset=iso-8859-1", TEXT_JSON),
        TEXT_JSON_UTF_8("text/json;charset=utf-8", TEXT_JSON),
        APPLICATION_JSON_8859_1("application/json;charset=iso-8859-1", APPLICATION_JSON),
        APPLICATION_JSON_UTF_8("application/json;charset=utf-8", APPLICATION_JSON);

        private final String _string;
        private final Type _base;
        private final ByteBuffer _buffer;
        private final Charset _charset;
        private final String _charsetString;
        private final boolean _assumedCharset;
        private final HttpField _field;

        private Type(String s) {
            this._string = s;
            this._buffer = BufferUtil.toBuffer((String)s);
            this._base = this;
            this._charset = null;
            this._charsetString = null;
            this._assumedCharset = false;
            this._field = new PreEncodedHttpField(HttpHeader.CONTENT_TYPE, this._string);
        }

        private Type(String s, Type base) {
            this._string = s;
            this._buffer = BufferUtil.toBuffer((String)s);
            this._base = base;
            int i = s.indexOf(";charset=");
            this._charset = Charset.forName(s.substring(i + 9));
            this._charsetString = this._charset.toString().toLowerCase(Locale.ENGLISH);
            this._assumedCharset = false;
            this._field = new PreEncodedHttpField(HttpHeader.CONTENT_TYPE, this._string);
        }

        private Type(String s, Charset cs) {
            this._string = s;
            this._base = this;
            this._buffer = BufferUtil.toBuffer((String)s);
            this._charset = cs;
            this._charsetString = this._charset == null ? null : this._charset.toString().toLowerCase(Locale.ENGLISH);
            this._assumedCharset = true;
            this._field = new PreEncodedHttpField(HttpHeader.CONTENT_TYPE, this._string);
        }

        public ByteBuffer asBuffer() {
            return this._buffer.asReadOnlyBuffer();
        }

        public Charset getCharset() {
            return this._charset;
        }

        public String getCharsetString() {
            return this._charsetString;
        }

        public boolean is(String s) {
            return this._string.equalsIgnoreCase(s);
        }

        public String asString() {
            return this._string;
        }

        public String toString() {
            return this._string;
        }

        public boolean isCharsetAssumed() {
            return this._assumedCharset;
        }

        public HttpField getContentTypeField() {
            return this._field;
        }

        public Type getBaseType() {
            return this._base;
        }
    }
}

