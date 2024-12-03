/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.eclipse.jetty.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import org.eclipse.jetty.util.ByteArrayOutputStream2;
import org.eclipse.jetty.util.IO;
import org.eclipse.jetty.util.MultiMap;
import org.eclipse.jetty.util.TypeUtil;
import org.eclipse.jetty.util.Utf8Appendable;
import org.eclipse.jetty.util.Utf8StringBuffer;
import org.eclipse.jetty.util.Utf8StringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UrlEncoded {
    static final Logger LOG;
    public static final Charset ENCODING;

    private UrlEncoded() {
    }

    public static String encode(MultiMap<String> map, Charset charset, boolean equalsForNullValue) {
        if (charset == null) {
            charset = ENCODING;
        }
        StringBuilder result = new StringBuilder(128);
        boolean delim = false;
        for (Map.Entry entry : map.entrySet()) {
            String key = (String)entry.getKey();
            List list = (List)entry.getValue();
            int s = list.size();
            if (delim) {
                result.append('&');
            }
            if (s == 0) {
                result.append(UrlEncoded.encodeString(key, charset));
                if (equalsForNullValue) {
                    result.append('=');
                }
            } else {
                for (int i = 0; i < s; ++i) {
                    if (i > 0) {
                        result.append('&');
                    }
                    String val = (String)list.get(i);
                    result.append(UrlEncoded.encodeString(key, charset));
                    if (val != null) {
                        if (val.length() > 0) {
                            result.append('=');
                            result.append(UrlEncoded.encodeString(val, charset));
                            continue;
                        }
                        if (!equalsForNullValue) continue;
                        result.append('=');
                        continue;
                    }
                    if (!equalsForNullValue) continue;
                    result.append('=');
                }
            }
            delim = true;
        }
        return result.toString();
    }

    @Deprecated(since="10", forRemoval=true)
    public static void decodeTo(String content, MultiMap<String> map, String charset) {
        UrlEncoded.decodeTo(content, map, charset == null ? null : Charset.forName(charset));
    }

    public static void decodeTo(String content, MultiMap<String> map, Charset charset) {
        UrlEncoded.decodeTo(content, map, charset, -1);
    }

    public static void decodeTo(String content, MultiMap<String> map, Charset charset, int maxKeys) {
        String value;
        if (charset == null) {
            charset = ENCODING;
        }
        if (StandardCharsets.UTF_8.equals(charset)) {
            UrlEncoded.decodeUtf8To(content, 0, content.length(), map);
            return;
        }
        String key = null;
        int mark = -1;
        boolean encoded = false;
        block5: for (int i = 0; i < content.length(); ++i) {
            char c = content.charAt(i);
            switch (c) {
                case '&': {
                    int l = i - mark - 1;
                    value = l == 0 ? "" : (encoded ? UrlEncoded.decodeString(content, mark + 1, l, charset) : content.substring(mark + 1, i));
                    mark = i;
                    encoded = false;
                    if (key != null) {
                        map.add(key, value);
                    } else if (value != null && value.length() > 0) {
                        map.add(value, "");
                    }
                    UrlEncoded.checkMaxKeys(map, maxKeys);
                    key = null;
                    value = null;
                    continue block5;
                }
                case '=': {
                    if (key != null) continue block5;
                    key = encoded ? UrlEncoded.decodeString(content, mark + 1, i - mark - 1, charset) : content.substring(mark + 1, i);
                    mark = i;
                    encoded = false;
                    continue block5;
                }
                case '%': 
                case '+': {
                    encoded = true;
                }
            }
        }
        if (key != null) {
            int l = content.length() - mark - 1;
            value = l == 0 ? "" : (encoded ? UrlEncoded.decodeString(content, mark + 1, l, charset) : content.substring(mark + 1));
            map.add(key, value);
            UrlEncoded.checkMaxKeys(map, maxKeys);
        } else if (mark < content.length()) {
            String string = key = encoded ? UrlEncoded.decodeString(content, mark + 1, content.length() - mark - 1, charset) : content.substring(mark + 1);
            if (key != null && key.length() > 0) {
                map.add(key, "");
                UrlEncoded.checkMaxKeys(map, maxKeys);
            }
        }
    }

    public static void decodeUtf8To(String query, MultiMap<String> map) {
        UrlEncoded.decodeUtf8To(query, 0, query.length(), map);
    }

    public static void decodeUtf8To(String query, int offset, int length, MultiMap<String> map) {
        String value;
        Utf8StringBuilder buffer = new Utf8StringBuilder();
        String key = null;
        int end = offset + length;
        block6: for (int i = offset; i < end; ++i) {
            char c = query.charAt(i);
            switch (c) {
                case '&': {
                    value = buffer.toReplacedString();
                    buffer.reset();
                    if (key != null) {
                        map.add(key, value);
                    } else if (value != null && value.length() > 0) {
                        map.add(value, "");
                    }
                    key = null;
                    continue block6;
                }
                case '=': {
                    if (key != null) {
                        buffer.append(c);
                        continue block6;
                    }
                    key = buffer.toReplacedString();
                    buffer.reset();
                    continue block6;
                }
                case '+': {
                    buffer.append((byte)32);
                    continue block6;
                }
                case '%': {
                    if (i + 2 < end) {
                        char hi = query.charAt(++i);
                        char lo = query.charAt(++i);
                        buffer.append(UrlEncoded.decodeHexByte(hi, lo));
                        continue block6;
                    }
                    throw new Utf8Appendable.NotUtf8Exception("Incomplete % encoding");
                }
                default: {
                    buffer.append(c);
                }
            }
        }
        if (key != null) {
            value = buffer.toReplacedString();
            buffer.reset();
            map.add(key, value);
        } else if (buffer.length() > 0) {
            map.add(buffer.toReplacedString(), "");
        }
    }

    public static void decode88591To(InputStream in, MultiMap<String> map, int maxLength, int maxKeys) throws IOException {
        String value;
        int b;
        StringBuilder buffer = new StringBuilder();
        String key = null;
        int totalLength = 0;
        while ((b = in.read()) >= 0) {
            switch ((char)b) {
                case '&': {
                    value = buffer.length() == 0 ? "" : buffer.toString();
                    buffer.setLength(0);
                    if (key != null) {
                        map.add(key, value);
                    } else if (value.length() > 0) {
                        map.add(value, "");
                    }
                    key = null;
                    UrlEncoded.checkMaxKeys(map, maxKeys);
                    break;
                }
                case '=': {
                    if (key != null) {
                        buffer.append((char)b);
                        break;
                    }
                    key = buffer.toString();
                    buffer.setLength(0);
                    break;
                }
                case '+': {
                    buffer.append(' ');
                    break;
                }
                case '%': {
                    int code0 = in.read();
                    int code1 = in.read();
                    buffer.append(UrlEncoded.decodeHexChar(code0, code1));
                    break;
                }
                default: {
                    buffer.append((char)b);
                }
            }
            UrlEncoded.checkMaxLength(++totalLength, maxLength);
        }
        if (key != null) {
            value = buffer.length() == 0 ? "" : buffer.toString();
            buffer.setLength(0);
            map.add(key, value);
        } else if (buffer.length() > 0) {
            map.add(buffer.toString(), "");
        }
        UrlEncoded.checkMaxKeys(map, maxKeys);
    }

    public static void decodeUtf8To(InputStream in, MultiMap<String> map, int maxLength, int maxKeys) throws IOException {
        String value;
        int b;
        Utf8StringBuilder buffer = new Utf8StringBuilder();
        String key = null;
        int totalLength = 0;
        while ((b = in.read()) >= 0) {
            switch ((char)b) {
                case '&': {
                    value = buffer.toReplacedString();
                    buffer.reset();
                    if (key != null) {
                        map.add(key, value);
                    } else if (value != null && value.length() > 0) {
                        map.add(value, "");
                    }
                    key = null;
                    UrlEncoded.checkMaxKeys(map, maxKeys);
                    break;
                }
                case '=': {
                    if (key != null) {
                        buffer.append((byte)b);
                        break;
                    }
                    key = buffer.toReplacedString();
                    buffer.reset();
                    break;
                }
                case '+': {
                    buffer.append((byte)32);
                    break;
                }
                case '%': {
                    char code0 = (char)in.read();
                    char code1 = (char)in.read();
                    buffer.append(UrlEncoded.decodeHexByte(code0, code1));
                    break;
                }
                default: {
                    buffer.append((byte)b);
                }
            }
            UrlEncoded.checkMaxLength(++totalLength, maxLength);
        }
        if (key != null) {
            value = buffer.toReplacedString();
            buffer.reset();
            map.add(key, value);
        } else if (buffer.length() > 0) {
            map.add(buffer.toReplacedString(), "");
        }
        UrlEncoded.checkMaxKeys(map, maxKeys);
    }

    public static void decodeUtf16To(InputStream in, MultiMap<String> map, int maxLength, int maxKeys) throws IOException {
        InputStreamReader input = new InputStreamReader(in, StandardCharsets.UTF_16);
        StringWriter buf = new StringWriter(8192);
        IO.copy(input, buf, (long)maxLength);
        UrlEncoded.decodeTo(buf.getBuffer().toString(), map, StandardCharsets.UTF_16, maxKeys);
    }

    @Deprecated(since="10", forRemoval=true)
    public static void decodeTo(InputStream in, MultiMap<String> map, String charset, int maxLength, int maxKeys) throws IOException {
        if (charset == null) {
            if (ENCODING.equals(StandardCharsets.UTF_8)) {
                UrlEncoded.decodeUtf8To(in, map, maxLength, maxKeys);
            } else {
                UrlEncoded.decodeTo(in, map, ENCODING, maxLength, maxKeys);
            }
        } else if ("utf-8".equalsIgnoreCase(charset)) {
            UrlEncoded.decodeUtf8To(in, map, maxLength, maxKeys);
        } else if ("iso-8859-1".equalsIgnoreCase(charset)) {
            UrlEncoded.decode88591To(in, map, maxLength, maxKeys);
        } else if ("utf-16".equalsIgnoreCase(charset)) {
            UrlEncoded.decodeUtf16To(in, map, maxLength, maxKeys);
        } else {
            UrlEncoded.decodeTo(in, map, Charset.forName(charset), maxLength, maxKeys);
        }
    }

    public static void decodeTo(InputStream in, MultiMap<String> map, Charset charset, int maxLength, int maxKeys) throws IOException {
        if (charset == null) {
            charset = ENCODING;
        }
        if (StandardCharsets.UTF_8.equals(charset)) {
            UrlEncoded.decodeUtf8To(in, map, maxLength, maxKeys);
            return;
        }
        if (StandardCharsets.ISO_8859_1.equals(charset)) {
            UrlEncoded.decode88591To(in, map, maxLength, maxKeys);
            return;
        }
        if (StandardCharsets.UTF_16.equals(charset)) {
            UrlEncoded.decodeUtf16To(in, map, maxLength, maxKeys);
            return;
        }
        String key = null;
        int totalLength = 0;
        try (ByteArrayOutputStream2 output = new ByteArrayOutputStream2();){
            String value;
            int size;
            int c;
            while ((c = in.read()) > 0) {
                switch ((char)c) {
                    case '&': {
                        size = output.size();
                        value = size == 0 ? "" : output.toString(charset);
                        output.setCount(0);
                        if (key != null) {
                            map.add(key, value);
                        } else if (value != null && value.length() > 0) {
                            map.add(value, "");
                        }
                        key = null;
                        UrlEncoded.checkMaxKeys(map, maxKeys);
                        break;
                    }
                    case '=': {
                        if (key != null) {
                            output.write(c);
                            break;
                        }
                        size = output.size();
                        key = size == 0 ? "" : output.toString(charset);
                        output.setCount(0);
                        break;
                    }
                    case '+': {
                        output.write(32);
                        break;
                    }
                    case '%': {
                        int code0 = in.read();
                        int code1 = in.read();
                        output.write(UrlEncoded.decodeHexChar(code0, code1));
                        break;
                    }
                    default: {
                        output.write(c);
                    }
                }
                UrlEncoded.checkMaxLength(++totalLength, maxLength);
            }
            size = output.size();
            if (key != null) {
                value = size == 0 ? "" : output.toString(charset);
                output.setCount(0);
                map.add(key, value);
            } else if (size > 0) {
                map.add(output.toString(charset), "");
            }
            UrlEncoded.checkMaxKeys(map, maxKeys);
        }
    }

    private static void checkMaxKeys(MultiMap<String> map, int maxKeys) {
        int size = map.size();
        if (maxKeys >= 0 && size > maxKeys) {
            throw new IllegalStateException(String.format("Form with too many keys [%d > %d]", size, maxKeys));
        }
    }

    private static void checkMaxLength(int length, int maxLength) {
        if (maxLength >= 0 && length > maxLength) {
            throw new IllegalStateException("Form is larger than max length " + maxLength);
        }
    }

    public static String decodeString(String encoded) {
        return UrlEncoded.decodeString(encoded, 0, encoded.length(), ENCODING);
    }

    public static String decodeString(String encoded, int offset, int length, Charset charset) {
        if (charset == null || StandardCharsets.UTF_8.equals(charset)) {
            Utf8Appendable buffer = null;
            for (int i = 0; i < length; ++i) {
                char c = encoded.charAt(offset + i);
                if (c > '\u00ff') {
                    if (buffer == null) {
                        buffer = new Utf8StringBuffer(length);
                        ((Utf8StringBuffer)buffer).getStringBuffer().append(encoded, offset, offset + i + 1);
                        continue;
                    }
                    ((Utf8StringBuffer)buffer).getStringBuffer().append(c);
                    continue;
                }
                if (c == '+') {
                    if (buffer == null) {
                        buffer = new Utf8StringBuffer(length);
                        ((Utf8StringBuffer)buffer).getStringBuffer().append(encoded, offset, offset + i);
                    }
                    ((Utf8StringBuffer)buffer).getStringBuffer().append(' ');
                    continue;
                }
                if (c == '%') {
                    if (buffer == null) {
                        buffer = new Utf8StringBuffer(length);
                        ((Utf8StringBuffer)buffer).getStringBuffer().append(encoded, offset, offset + i);
                    }
                    if (i + 2 < length) {
                        int o = offset + i + 1;
                        i += 2;
                        byte b = (byte)TypeUtil.parseInt(encoded, o, 2, 16);
                        buffer.append(b);
                        continue;
                    }
                    ((Utf8StringBuffer)buffer).getStringBuffer().append('\ufffd');
                    i = length;
                    continue;
                }
                if (buffer == null) continue;
                ((Utf8StringBuffer)buffer).getStringBuffer().append(c);
            }
            if (buffer == null) {
                if (offset == 0 && encoded.length() == length) {
                    return encoded;
                }
                return encoded.substring(offset, offset + length);
            }
            return buffer.toReplacedString();
        }
        StringBuffer buffer = null;
        for (int i = 0; i < length; ++i) {
            char c = encoded.charAt(offset + i);
            if (c > '\u00ff') {
                if (buffer == null) {
                    buffer = new StringBuffer(length);
                    buffer.append(encoded, offset, offset + i + 1);
                    continue;
                }
                buffer.append(c);
                continue;
            }
            if (c == '+') {
                if (buffer == null) {
                    buffer = new StringBuffer(length);
                    buffer.append(encoded, offset, offset + i);
                }
                buffer.append(' ');
                continue;
            }
            if (c == '%') {
                if (buffer == null) {
                    buffer = new StringBuffer(length);
                    buffer.append(encoded, offset, offset + i);
                }
                byte[] ba = new byte[length];
                int n = 0;
                while (c <= '\u00ff') {
                    if (c == '%') {
                        if (i + 2 < length) {
                            int o = offset + i + 1;
                            i += 3;
                            ba[n] = (byte)TypeUtil.parseInt(encoded, o, 2, 16);
                            ++n;
                        } else {
                            ba[n++] = 63;
                            i = length;
                        }
                    } else if (c == '+') {
                        ba[n++] = 32;
                        ++i;
                    } else {
                        ba[n++] = (byte)c;
                        ++i;
                    }
                    if (i >= length) break;
                    c = encoded.charAt(offset + i);
                }
                --i;
                buffer.append(new String(ba, 0, n, charset));
                continue;
            }
            if (buffer == null) continue;
            buffer.append(c);
        }
        if (buffer == null) {
            if (offset == 0 && encoded.length() == length) {
                return encoded;
            }
            return encoded.substring(offset, offset + length);
        }
        return buffer.toString();
    }

    private static char decodeHexChar(int hi, int lo) {
        try {
            return (char)((TypeUtil.convertHexDigit(hi) << 4) + TypeUtil.convertHexDigit(lo));
        }
        catch (NumberFormatException e) {
            throw new IllegalArgumentException("Not valid encoding '%" + (char)hi + (char)lo + "'");
        }
    }

    private static byte decodeHexByte(char hi, char lo) {
        try {
            return (byte)((TypeUtil.convertHexDigit(hi) << 4) + TypeUtil.convertHexDigit(lo));
        }
        catch (NumberFormatException e) {
            throw new IllegalArgumentException("Not valid encoding '%" + hi + lo + "'");
        }
    }

    public static String encodeString(String string) {
        return UrlEncoded.encodeString(string, ENCODING);
    }

    public static String encodeString(String string, Charset charset) {
        if (charset == null) {
            charset = ENCODING;
        }
        byte[] bytes = string.getBytes(charset);
        byte[] encoded = new byte[bytes.length * 3];
        int n = 0;
        boolean noEncode = true;
        for (byte b : bytes) {
            if (b == 32) {
                noEncode = false;
                encoded[n++] = 43;
                continue;
            }
            if (b >= 97 && b <= 122 || b >= 65 && b <= 90 || b >= 48 && b <= 57 || b == 45 || b == 46 || b == 95 || b == 126) {
                encoded[n++] = b;
                continue;
            }
            noEncode = false;
            encoded[n++] = 37;
            byte nibble = (byte)((b & 0xF0) >> 4);
            encoded[n++] = nibble >= 10 ? (byte)(65 + nibble - 10) : (byte)(48 + nibble);
            nibble = (byte)(b & 0xF);
            encoded[n++] = nibble >= 10 ? (byte)(65 + nibble - 10) : (byte)(48 + nibble);
        }
        if (noEncode) {
            return string;
        }
        return new String(encoded, 0, n, charset);
    }

    static {
        Charset encoding;
        LOG = LoggerFactory.getLogger(UrlEncoded.class);
        String charset = null;
        try {
            charset = System.getProperty("org.eclipse.jetty.util.UrlEncoding.charset");
            if (charset == null) {
                charset = StandardCharsets.UTF_8.toString();
                encoding = StandardCharsets.UTF_8;
            } else {
                encoding = Charset.forName(charset);
            }
        }
        catch (Exception e) {
            LOG.warn("Unable to set default UrlEncoding charset: {}", (Object)charset, (Object)e);
            encoding = StandardCharsets.UTF_8;
        }
        ENCODING = encoding;
    }
}

