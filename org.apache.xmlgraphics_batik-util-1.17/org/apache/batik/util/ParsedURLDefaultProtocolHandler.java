/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.util;

import java.net.MalformedURLException;
import java.net.URL;
import org.apache.batik.util.AbstractParsedURLProtocolHandler;
import org.apache.batik.util.ParsedURL;
import org.apache.batik.util.ParsedURLData;

public class ParsedURLDefaultProtocolHandler
extends AbstractParsedURLProtocolHandler {
    public ParsedURLDefaultProtocolHandler() {
        super(null);
    }

    protected ParsedURLDefaultProtocolHandler(String protocol) {
        super(protocol);
    }

    protected ParsedURLData constructParsedURLData() {
        return new ParsedURLData();
    }

    protected ParsedURLData constructParsedURLData(URL url) {
        return new ParsedURLData(url);
    }

    @Override
    public ParsedURLData parseURL(String urlStr) {
        try {
            URL url = new URL(urlStr);
            return this.constructParsedURLData(url);
        }
        catch (MalformedURLException url) {
            ParsedURLData ret = this.constructParsedURLData();
            if (urlStr == null) {
                return ret;
            }
            int pidx = 0;
            int len = urlStr.length();
            int idx = urlStr.indexOf(35);
            ret.ref = null;
            if (idx != -1) {
                if (idx + 1 < len) {
                    ret.ref = urlStr.substring(idx + 1);
                }
                urlStr = urlStr.substring(0, idx);
                len = urlStr.length();
            }
            if (len == 0) {
                return ret;
            }
            idx = 0;
            char ch = urlStr.charAt(idx);
            while (ch == '-' || ch == '+' || ch == '.' || ch >= 'a' && ch <= 'z' || ch >= 'A' && ch <= 'Z') {
                if (++idx == len) {
                    ch = '\u0000';
                    break;
                }
                ch = urlStr.charAt(idx);
            }
            if (ch == ':') {
                ret.protocol = urlStr.substring(pidx, idx).toLowerCase();
                pidx = idx + 1;
            }
            if ((idx = urlStr.indexOf(47)) == -1 || pidx + 2 < len && urlStr.charAt(pidx) == '/' && urlStr.charAt(pidx + 1) == '/') {
                if (idx != -1) {
                    pidx += 2;
                }
                String hostPort = (idx = urlStr.indexOf(47, pidx)) == -1 ? urlStr.substring(pidx) : urlStr.substring(pidx, idx);
                int hidx = idx;
                idx = hostPort.indexOf(58);
                ret.port = -1;
                if (idx == -1) {
                    ret.host = hostPort.length() == 0 ? null : hostPort;
                } else {
                    ret.host = idx == 0 ? null : hostPort.substring(0, idx);
                    if (idx + 1 < hostPort.length()) {
                        String portStr = hostPort.substring(idx + 1);
                        try {
                            ret.port = Integer.parseInt(portStr);
                        }
                        catch (NumberFormatException numberFormatException) {
                            // empty catch block
                        }
                    }
                }
                if ((ret.host == null || ret.host.indexOf(46) == -1) && ret.port == -1) {
                    ret.host = null;
                } else {
                    pidx = hidx;
                }
            }
            if (pidx == -1 || pidx >= len) {
                return ret;
            }
            ret.path = urlStr.substring(pidx);
            return ret;
        }
    }

    public static String unescapeStr(String str) {
        int idx = str.indexOf(37);
        if (idx == -1) {
            return str;
        }
        int prev = 0;
        StringBuffer ret = new StringBuffer();
        while (idx != -1) {
            if (idx != prev) {
                ret.append(str.substring(prev, idx));
            }
            if (idx + 2 >= str.length()) break;
            prev = idx + 3;
            idx = str.indexOf(37, prev);
            int ch1 = ParsedURLDefaultProtocolHandler.charToHex(str.charAt(idx + 1));
            int ch2 = ParsedURLDefaultProtocolHandler.charToHex(str.charAt(idx + 1));
            if (ch1 == -1 || ch2 == -1) continue;
            ret.append((char)(ch1 << 4 | ch2));
        }
        return ret.toString();
    }

    public static int charToHex(int ch) {
        switch (ch) {
            case 48: 
            case 49: 
            case 50: 
            case 51: 
            case 52: 
            case 53: 
            case 54: 
            case 55: 
            case 56: 
            case 57: {
                return ch - 48;
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
        return -1;
    }

    @Override
    public ParsedURLData parseURL(ParsedURL baseURL, String urlStr) {
        if (urlStr.length() == 0) {
            return baseURL.data;
        }
        int idx = 0;
        int len = urlStr.length();
        if (len == 0) {
            return baseURL.data;
        }
        char ch = urlStr.charAt(idx);
        while (ch == '-' || ch == '+' || ch == '.' || ch >= 'a' && ch <= 'z' || ch >= 'A' && ch <= 'Z') {
            if (++idx == len) {
                ch = '\u0000';
                break;
            }
            ch = urlStr.charAt(idx);
        }
        String protocol = null;
        if (ch == ':') {
            protocol = urlStr.substring(0, idx).toLowerCase();
        }
        if (protocol != null) {
            if (!protocol.equals(baseURL.getProtocol())) {
                return this.parseURL(urlStr);
            }
            if (++idx == urlStr.length()) {
                return this.parseURL(urlStr);
            }
            if (urlStr.charAt(idx) == '/') {
                return this.parseURL(urlStr);
            }
            urlStr = urlStr.substring(idx);
        }
        if (urlStr.startsWith("/")) {
            if (urlStr.length() > 1 && urlStr.charAt(1) == '/') {
                return this.parseURL(baseURL.getProtocol() + ":" + urlStr);
            }
            return this.parseURL(baseURL.getPortStr() + urlStr);
        }
        if (urlStr.startsWith("#")) {
            String base = baseURL.getPortStr();
            if (baseURL.getPath() != null) {
                base = base + baseURL.getPath();
            }
            return this.parseURL(base + urlStr);
        }
        String path = baseURL.getPath();
        if (path == null) {
            path = "";
        }
        if ((idx = path.lastIndexOf(47)) == -1) {
            path = "";
        } else if (urlStr.startsWith(path = path.substring(0, idx + 1))) {
            urlStr = urlStr.substring(path.length());
        }
        return this.parseURL(baseURL.getPortStr() + path + urlStr);
    }
}

