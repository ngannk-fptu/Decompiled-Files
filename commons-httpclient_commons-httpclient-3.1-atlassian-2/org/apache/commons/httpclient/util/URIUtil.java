/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.codec.DecoderException
 *  org.apache.commons.codec.net.URLCodec
 */
package org.apache.commons.httpclient.util;

import java.util.BitSet;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.net.URLCodec;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.util.EncodingUtil;

public class URIUtil {
    protected static final BitSet empty = new BitSet(1);

    public static String getName(String uri) {
        if (uri == null || uri.length() == 0) {
            return uri;
        }
        String path = URIUtil.getPath(uri);
        int at = path.lastIndexOf("/");
        int to = path.length();
        return at >= 0 ? path.substring(at + 1, to) : path;
    }

    public static String getQuery(String uri) {
        if (uri == null || uri.length() == 0) {
            return null;
        }
        int at = uri.indexOf("//");
        int from = uri.indexOf("/", at >= 0 ? (uri.lastIndexOf("/", at - 1) >= 0 ? 0 : at + 2) : 0);
        int to = uri.length();
        at = uri.indexOf("?", from);
        if (at < 0) {
            return null;
        }
        from = at + 1;
        if (uri.lastIndexOf("#") > from) {
            to = uri.lastIndexOf("#");
        }
        return from < 0 || from == to ? null : uri.substring(from, to);
    }

    public static String getPath(String uri) {
        if (uri == null) {
            return null;
        }
        int at = uri.indexOf("//");
        int from = uri.indexOf("/", at >= 0 ? (uri.lastIndexOf("/", at - 1) >= 0 ? 0 : at + 2) : 0);
        int to = uri.length();
        if (uri.indexOf(63, from) != -1) {
            to = uri.indexOf(63, from);
        }
        if (uri.lastIndexOf("#") > from && uri.lastIndexOf("#") < to) {
            to = uri.lastIndexOf("#");
        }
        return from < 0 ? (at >= 0 ? "/" : uri) : uri.substring(from, to);
    }

    public static String getPathQuery(String uri) {
        if (uri == null) {
            return null;
        }
        int at = uri.indexOf("//");
        int from = uri.indexOf("/", at >= 0 ? (uri.lastIndexOf("/", at - 1) >= 0 ? 0 : at + 2) : 0);
        int to = uri.length();
        if (uri.lastIndexOf("#") > from) {
            to = uri.lastIndexOf("#");
        }
        return from < 0 ? (at >= 0 ? "/" : uri) : uri.substring(from, to);
    }

    public static String getFromPath(String uri) {
        if (uri == null) {
            return null;
        }
        int at = uri.indexOf("//");
        int from = uri.indexOf("/", at >= 0 ? (uri.lastIndexOf("/", at - 1) >= 0 ? 0 : at + 2) : 0);
        return from < 0 ? (at >= 0 ? "/" : uri) : uri.substring(from);
    }

    public static String encodeAll(String unescaped) throws URIException {
        return URIUtil.encodeAll(unescaped, URI.getDefaultProtocolCharset());
    }

    public static String encodeAll(String unescaped, String charset) throws URIException {
        return URIUtil.encode(unescaped, empty, charset);
    }

    public static String encodeWithinAuthority(String unescaped) throws URIException {
        return URIUtil.encodeWithinAuthority(unescaped, URI.getDefaultProtocolCharset());
    }

    public static String encodeWithinAuthority(String unescaped, String charset) throws URIException {
        return URIUtil.encode(unescaped, URI.allowed_within_authority, charset);
    }

    public static String encodePathQuery(String unescaped) throws URIException {
        return URIUtil.encodePathQuery(unescaped, URI.getDefaultProtocolCharset());
    }

    public static String encodePathQuery(String unescaped, String charset) throws URIException {
        int at = unescaped.indexOf(63);
        if (at < 0) {
            return URIUtil.encode(unescaped, URI.allowed_abs_path, charset);
        }
        return URIUtil.encode(unescaped.substring(0, at), URI.allowed_abs_path, charset) + '?' + URIUtil.encode(unescaped.substring(at + 1), URI.allowed_query, charset);
    }

    public static String encodeWithinPath(String unescaped) throws URIException {
        return URIUtil.encodeWithinPath(unescaped, URI.getDefaultProtocolCharset());
    }

    public static String encodeWithinPath(String unescaped, String charset) throws URIException {
        return URIUtil.encode(unescaped, URI.allowed_within_path, charset);
    }

    public static String encodePath(String unescaped) throws URIException {
        return URIUtil.encodePath(unescaped, URI.getDefaultProtocolCharset());
    }

    public static String encodePath(String unescaped, String charset) throws URIException {
        return URIUtil.encode(unescaped, URI.allowed_abs_path, charset);
    }

    public static String encodeWithinQuery(String unescaped) throws URIException {
        return URIUtil.encodeWithinQuery(unescaped, URI.getDefaultProtocolCharset());
    }

    public static String encodeWithinQuery(String unescaped, String charset) throws URIException {
        return URIUtil.encode(unescaped, URI.allowed_within_query, charset);
    }

    public static String encodeQuery(String unescaped) throws URIException {
        return URIUtil.encodeQuery(unescaped, URI.getDefaultProtocolCharset());
    }

    public static String encodeQuery(String unescaped, String charset) throws URIException {
        return URIUtil.encode(unescaped, URI.allowed_query, charset);
    }

    public static String encode(String unescaped, BitSet allowed) throws URIException {
        return URIUtil.encode(unescaped, allowed, URI.getDefaultProtocolCharset());
    }

    public static String encode(String unescaped, BitSet allowed, String charset) throws URIException {
        byte[] rawdata = URLCodec.encodeUrl((BitSet)allowed, (byte[])EncodingUtil.getBytes(unescaped, charset));
        return EncodingUtil.getAsciiString(rawdata);
    }

    public static String decode(String escaped) throws URIException {
        try {
            byte[] rawdata = URLCodec.decodeUrl((byte[])EncodingUtil.getAsciiBytes(escaped));
            return EncodingUtil.getString(rawdata, URI.getDefaultProtocolCharset());
        }
        catch (DecoderException e) {
            throw new URIException(e.getMessage());
        }
    }

    public static String decode(String escaped, String charset) throws URIException {
        return Coder.decode(escaped.toCharArray(), charset);
    }

    protected static class Coder
    extends URI {
        protected Coder() {
        }

        public static char[] encode(String unescapedComponent, BitSet allowed, String charset) throws URIException {
            return URI.encode(unescapedComponent, allowed, charset);
        }

        public static String decode(char[] escapedComponent, String charset) throws URIException {
            return URI.decode(escapedComponent, charset);
        }

        public static boolean verifyEscaped(char[] original) {
            for (int i = 0; i < original.length; ++i) {
                char c = original[i];
                if (c > '\u0080') {
                    return false;
                }
                if (c != '%' || Character.digit(original[++i], 16) != -1 && Character.digit(original[++i], 16) != -1) continue;
                return false;
            }
            return true;
        }

        public static String replace(String original, char[] from, char[] to) {
            for (int i = from.length; i > 0; --i) {
                original = Coder.replace(original, from[i], to[i]);
            }
            return original;
        }

        public static String replace(String original, char from, char to) {
            int at;
            StringBuffer result = new StringBuffer(original.length());
            int saved = 0;
            do {
                if ((at = original.indexOf(from)) >= 0) {
                    result.append(original.substring(0, at));
                    result.append(to);
                } else {
                    result.append(original.substring(saved));
                }
                saved = at;
            } while (at >= 0);
            return result.toString();
        }
    }
}

