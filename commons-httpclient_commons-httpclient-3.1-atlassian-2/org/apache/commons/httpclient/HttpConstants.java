/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.commons.httpclient;

import java.io.UnsupportedEncodingException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class HttpConstants {
    public static final String HTTP_ELEMENT_CHARSET = "US-ASCII";
    public static final String DEFAULT_CONTENT_CHARSET = "ISO-8859-1";
    private static final Log LOG = LogFactory.getLog(HttpConstants.class);

    public static byte[] getBytes(String data) {
        if (data == null) {
            throw new IllegalArgumentException("Parameter may not be null");
        }
        try {
            return data.getBytes(HTTP_ELEMENT_CHARSET);
        }
        catch (UnsupportedEncodingException e) {
            if (LOG.isWarnEnabled()) {
                LOG.warn((Object)"Unsupported encoding: US-ASCII. System default encoding used");
            }
            return data.getBytes();
        }
    }

    public static String getString(byte[] data, int offset, int length) {
        if (data == null) {
            throw new IllegalArgumentException("Parameter may not be null");
        }
        try {
            return new String(data, offset, length, HTTP_ELEMENT_CHARSET);
        }
        catch (UnsupportedEncodingException e) {
            if (LOG.isWarnEnabled()) {
                LOG.warn((Object)"Unsupported encoding: US-ASCII. System default encoding used");
            }
            return new String(data, offset, length);
        }
    }

    public static String getString(byte[] data) {
        return HttpConstants.getString(data, 0, data.length);
    }

    public static byte[] getContentBytes(String data, String charset) {
        if (data == null) {
            throw new IllegalArgumentException("Parameter may not be null");
        }
        if (charset == null || charset.equals("")) {
            charset = DEFAULT_CONTENT_CHARSET;
        }
        try {
            return data.getBytes(charset);
        }
        catch (UnsupportedEncodingException e) {
            if (LOG.isWarnEnabled()) {
                LOG.warn((Object)("Unsupported encoding: " + charset + ". HTTP default encoding used"));
            }
            try {
                return data.getBytes(DEFAULT_CONTENT_CHARSET);
            }
            catch (UnsupportedEncodingException e2) {
                if (LOG.isWarnEnabled()) {
                    LOG.warn((Object)"Unsupported encoding: ISO-8859-1. System encoding used");
                }
                return data.getBytes();
            }
        }
    }

    public static String getContentString(byte[] data, int offset, int length, String charset) {
        if (data == null) {
            throw new IllegalArgumentException("Parameter may not be null");
        }
        if (charset == null || charset.equals("")) {
            charset = DEFAULT_CONTENT_CHARSET;
        }
        try {
            return new String(data, offset, length, charset);
        }
        catch (UnsupportedEncodingException e) {
            if (LOG.isWarnEnabled()) {
                LOG.warn((Object)("Unsupported encoding: " + charset + ". Default HTTP encoding used"));
            }
            try {
                return new String(data, offset, length, DEFAULT_CONTENT_CHARSET);
            }
            catch (UnsupportedEncodingException e2) {
                if (LOG.isWarnEnabled()) {
                    LOG.warn((Object)"Unsupported encoding: ISO-8859-1. System encoding used");
                }
                return new String(data, offset, length);
            }
        }
    }

    public static String getContentString(byte[] data, String charset) {
        return HttpConstants.getContentString(data, 0, data.length, charset);
    }

    public static byte[] getContentBytes(String data) {
        return HttpConstants.getContentBytes(data, null);
    }

    public static String getContentString(byte[] data, int offset, int length) {
        return HttpConstants.getContentString(data, offset, length, null);
    }

    public static String getContentString(byte[] data) {
        return HttpConstants.getContentString(data, null);
    }

    public static byte[] getAsciiBytes(String data) {
        if (data == null) {
            throw new IllegalArgumentException("Parameter may not be null");
        }
        try {
            return data.getBytes(HTTP_ELEMENT_CHARSET);
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException("HttpClient requires ASCII support");
        }
    }

    public static String getAsciiString(byte[] data, int offset, int length) {
        if (data == null) {
            throw new IllegalArgumentException("Parameter may not be null");
        }
        try {
            return new String(data, offset, length, HTTP_ELEMENT_CHARSET);
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException("HttpClient requires ASCII support");
        }
    }

    public static String getAsciiString(byte[] data) {
        return HttpConstants.getAsciiString(data, 0, data.length);
    }
}

