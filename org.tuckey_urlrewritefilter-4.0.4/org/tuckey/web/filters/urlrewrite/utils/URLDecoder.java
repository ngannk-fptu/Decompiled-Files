/*
 * Decompiled with CFR 0.152.
 */
package org.tuckey.web.filters.urlrewrite.utils;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;

public class URLDecoder {
    public static String decodeURL(String url, String charset) throws URISyntaxException {
        int queryPart = url.indexOf(63);
        String query = null;
        String path = url;
        if (queryPart != -1) {
            query = url.substring(queryPart + 1);
            path = url.substring(0, queryPart);
        }
        String decodedPath = URLDecoder.decodePath(path, charset);
        if (query != null) {
            return decodedPath + '?' + URLDecoder.decodeQuery(query, charset);
        }
        return decodedPath;
    }

    public static String decodePath(String path, String charset) throws URISyntaxException {
        return URLDecoder.decodeURLEncoded(path, false, charset);
    }

    public static String decodeQuery(String query, String charset) throws URISyntaxException {
        return URLDecoder.decodeURLEncoded(query, true, charset);
    }

    public static String decodeURLEncoded(String part, boolean query, String charset) throws URISyntaxException {
        try {
            byte[] ascii = part.getBytes("ASCII");
            byte[] decoded = new byte[ascii.length];
            int j = 0;
            int i = 0;
            while (i < ascii.length) {
                if (ascii[i] == 37) {
                    if (i + 2 >= ascii.length) {
                        throw new URISyntaxException(part, "Invalid URL-encoded string at char " + i);
                    }
                    byte first = ascii[++i];
                    byte second = ascii[++i];
                    decoded[j] = (byte)(URLDecoder.hexToByte(first) * 16 + URLDecoder.hexToByte(second));
                } else {
                    decoded[j] = query && ascii[i] == 43 ? 32 : ascii[i];
                }
                ++i;
                ++j;
            }
            return new String(decoded, 0, j, charset);
        }
        catch (UnsupportedEncodingException x) {
            throw new URISyntaxException(part, "Invalid encoding: " + charset);
        }
    }

    private static byte hexToByte(byte b) throws URISyntaxException {
        switch (b) {
            case 48: {
                return 0;
            }
            case 49: {
                return 1;
            }
            case 50: {
                return 2;
            }
            case 51: {
                return 3;
            }
            case 52: {
                return 4;
            }
            case 53: {
                return 5;
            }
            case 54: {
                return 6;
            }
            case 55: {
                return 7;
            }
            case 56: {
                return 8;
            }
            case 57: {
                return 9;
            }
            case 65: 
            case 97: {
                return 10;
            }
            case 66: 
            case 98: {
                return 11;
            }
            case 67: 
            case 99: {
                return 12;
            }
            case 68: 
            case 100: {
                return 13;
            }
            case 69: 
            case 101: {
                return 14;
            }
            case 70: 
            case 102: {
                return 15;
            }
        }
        throw new URISyntaxException(String.valueOf(b), "Invalid URL-encoded string");
    }
}

