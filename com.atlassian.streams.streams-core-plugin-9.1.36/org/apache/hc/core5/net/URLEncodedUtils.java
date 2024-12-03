/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.net;

import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.apache.hc.core5.net.PercentCodec;
import org.apache.hc.core5.net.URIBuilder;
import org.apache.hc.core5.util.Args;
import org.apache.hc.core5.util.Tokenizer;

@Deprecated
public class URLEncodedUtils {
    private static final char QP_SEP_A = '&';
    private static final char QP_SEP_S = ';';
    private static final BitSet URLENCODER;

    public static List<NameValuePair> parse(URI uri, Charset charset) {
        Args.notNull(uri, "URI");
        String query = uri.getRawQuery();
        if (query != null && !query.isEmpty()) {
            return URLEncodedUtils.parse(query, charset);
        }
        return new ArrayList<NameValuePair>(0);
    }

    public static List<NameValuePair> parse(CharSequence s, Charset charset) {
        if (s == null) {
            return new ArrayList<NameValuePair>(0);
        }
        return URLEncodedUtils.parse(s, charset, '&', ';');
    }

    public static List<NameValuePair> parse(CharSequence s, Charset charset, char ... separators) {
        Args.notNull(s, "Char sequence");
        Tokenizer tokenParser = Tokenizer.INSTANCE;
        BitSet delimSet = new BitSet();
        for (char separator : separators) {
            delimSet.set(separator);
        }
        Tokenizer.Cursor cursor = new Tokenizer.Cursor(0, s.length());
        ArrayList<NameValuePair> list = new ArrayList<NameValuePair>();
        while (!cursor.atEnd()) {
            delimSet.set(61);
            String name = tokenParser.parseToken(s, cursor, delimSet);
            String value = null;
            if (!cursor.atEnd()) {
                char delim = s.charAt(cursor.getPos());
                cursor.updatePos(cursor.getPos() + 1);
                if (delim == '=') {
                    delimSet.clear(61);
                    value = tokenParser.parseToken(s, cursor, delimSet);
                    if (!cursor.atEnd()) {
                        cursor.updatePos(cursor.getPos() + 1);
                    }
                }
            }
            if (name.isEmpty()) continue;
            list.add(new BasicNameValuePair(PercentCodec.decode(name, charset, true), PercentCodec.decode(value, charset, true)));
        }
        return list;
    }

    public static List<String> parsePathSegments(CharSequence s, Charset charset) {
        return URIBuilder.parsePath(s, charset);
    }

    public static List<String> parsePathSegments(CharSequence s) {
        return URLEncodedUtils.parsePathSegments(s, StandardCharsets.UTF_8);
    }

    public static String formatSegments(Iterable<String> segments, Charset charset) {
        Args.notNull(segments, "Segments");
        StringBuilder buf = new StringBuilder();
        URIBuilder.formatPath(buf, segments, false, charset);
        return buf.toString();
    }

    public static String formatSegments(String ... segments) {
        return URLEncodedUtils.formatSegments(Arrays.asList(segments), StandardCharsets.UTF_8);
    }

    public static String format(Iterable<? extends NameValuePair> parameters, char parameterSeparator, Charset charset) {
        Args.notNull(parameters, "Parameters");
        StringBuilder buf = new StringBuilder();
        int i = 0;
        for (NameValuePair nameValuePair : parameters) {
            if (i > 0) {
                buf.append(parameterSeparator);
            }
            PercentCodec.encode(buf, nameValuePair.getName(), charset, URLENCODER, true);
            if (nameValuePair.getValue() != null) {
                buf.append('=');
                PercentCodec.encode(buf, nameValuePair.getValue(), charset, URLENCODER, true);
            }
            ++i;
        }
        return buf.toString();
    }

    public static String format(Iterable<? extends NameValuePair> parameters, Charset charset) {
        return URLEncodedUtils.format(parameters, '&', charset);
    }

    static {
        int i;
        URLENCODER = new BitSet(256);
        for (i = 97; i <= 122; ++i) {
            URLENCODER.set(i);
        }
        for (i = 65; i <= 90; ++i) {
            URLENCODER.set(i);
        }
        for (i = 48; i <= 57; ++i) {
            URLENCODER.set(i);
        }
        URLENCODER.set(95);
        URLENCODER.set(45);
        URLENCODER.set(46);
        URLENCODER.set(42);
    }
}

