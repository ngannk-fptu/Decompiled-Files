/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.codec.net.URLCodec
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.commons.httpclient.util;

import java.io.UnsupportedEncodingException;
import org.apache.commons.codec.net.URLCodec;
import org.apache.commons.httpclient.HttpClientError;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class EncodingUtil {
    private static final String DEFAULT_CHARSET = "ISO-8859-1";
    private static final Log LOG = LogFactory.getLog(EncodingUtil.class);

    public static String formUrlEncode(NameValuePair[] pairs, String charset) {
        try {
            return EncodingUtil.doFormUrlEncode(pairs, charset);
        }
        catch (UnsupportedEncodingException e) {
            LOG.error((Object)("Encoding not supported: " + charset));
            try {
                return EncodingUtil.doFormUrlEncode(pairs, DEFAULT_CHARSET);
            }
            catch (UnsupportedEncodingException fatal) {
                throw new HttpClientError("Encoding not supported: ISO-8859-1");
            }
        }
    }

    private static String doFormUrlEncode(NameValuePair[] pairs, String charset) throws UnsupportedEncodingException {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < pairs.length; ++i) {
            URLCodec codec = new URLCodec();
            NameValuePair pair = pairs[i];
            if (pair.getName() == null) continue;
            if (i > 0) {
                buf.append("&");
            }
            buf.append(codec.encode(pair.getName(), charset));
            buf.append("=");
            if (pair.getValue() == null) continue;
            buf.append(codec.encode(pair.getValue(), charset));
        }
        return buf.toString();
    }

    public static String getString(byte[] data, int offset, int length, String charset) {
        if (data == null) {
            throw new IllegalArgumentException("Parameter may not be null");
        }
        if (charset == null || charset.length() == 0) {
            throw new IllegalArgumentException("charset may not be null or empty");
        }
        try {
            return new String(data, offset, length, charset);
        }
        catch (UnsupportedEncodingException e) {
            if (LOG.isWarnEnabled()) {
                LOG.warn((Object)("Unsupported encoding: " + charset + ". System encoding used"));
            }
            return new String(data, offset, length);
        }
    }

    public static String getString(byte[] data, String charset) {
        return EncodingUtil.getString(data, 0, data.length, charset);
    }

    public static byte[] getBytes(String data, String charset) {
        if (data == null) {
            throw new IllegalArgumentException("data may not be null");
        }
        if (charset == null || charset.length() == 0) {
            throw new IllegalArgumentException("charset may not be null or empty");
        }
        try {
            return data.getBytes(charset);
        }
        catch (UnsupportedEncodingException e) {
            if (LOG.isWarnEnabled()) {
                LOG.warn((Object)("Unsupported encoding: " + charset + ". System encoding used."));
            }
            return data.getBytes();
        }
    }

    public static byte[] getAsciiBytes(String data) {
        if (data == null) {
            throw new IllegalArgumentException("Parameter may not be null");
        }
        try {
            return data.getBytes("US-ASCII");
        }
        catch (UnsupportedEncodingException e) {
            throw new HttpClientError("HttpClient requires ASCII support");
        }
    }

    public static String getAsciiString(byte[] data, int offset, int length) {
        if (data == null) {
            throw new IllegalArgumentException("Parameter may not be null");
        }
        try {
            return new String(data, offset, length, "US-ASCII");
        }
        catch (UnsupportedEncodingException e) {
            throw new HttpClientError("HttpClient requires ASCII support");
        }
    }

    public static String getAsciiString(byte[] data) {
        return EncodingUtil.getAsciiString(data, 0, data.length);
    }

    private EncodingUtil() {
    }
}

