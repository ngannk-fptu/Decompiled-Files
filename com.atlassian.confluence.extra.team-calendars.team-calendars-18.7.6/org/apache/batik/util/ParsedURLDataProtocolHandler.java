/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import org.apache.batik.util.AbstractParsedURLProtocolHandler;
import org.apache.batik.util.Base64DecodeStream;
import org.apache.batik.util.ParsedURL;
import org.apache.batik.util.ParsedURLData;

public class ParsedURLDataProtocolHandler
extends AbstractParsedURLProtocolHandler {
    static final String DATA_PROTOCOL = "data";
    static final String BASE64 = "base64";
    static final String CHARSET = "charset";

    public ParsedURLDataProtocolHandler() {
        super(DATA_PROTOCOL);
    }

    @Override
    public ParsedURLData parseURL(ParsedURL baseURL, String urlStr) {
        return this.parseURL(urlStr);
    }

    @Override
    public ParsedURLData parseURL(String urlStr) {
        DataParsedURLData ret = new DataParsedURLData();
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
        if ((idx = urlStr.indexOf(58)) != -1) {
            ret.protocol = urlStr.substring(pidx, idx);
            if (ret.protocol.indexOf(47) == -1) {
                pidx = idx + 1;
            } else {
                ret.protocol = null;
                pidx = 0;
            }
        }
        if ((idx = urlStr.indexOf(44, pidx)) != -1 && idx != pidx) {
            ret.host = urlStr.substring(pidx, idx);
            pidx = idx + 1;
            int aidx = ret.host.lastIndexOf(59);
            if (aidx == -1 || aidx == ret.host.length()) {
                ret.contentType = ret.host;
            } else {
                String enc = ret.host.substring(aidx + 1);
                idx = enc.indexOf(61);
                if (idx == -1) {
                    ret.contentEncoding = enc;
                    ret.contentType = ret.host.substring(0, aidx);
                } else {
                    ret.contentType = ret.host;
                }
                aidx = 0;
                idx = ret.contentType.indexOf(59, aidx);
                if (idx != -1) {
                    aidx = idx + 1;
                    while (aidx < ret.contentType.length()) {
                        String param;
                        int eqIdx;
                        idx = ret.contentType.indexOf(59, aidx);
                        if (idx == -1) {
                            idx = ret.contentType.length();
                        }
                        if ((eqIdx = (param = ret.contentType.substring(aidx, idx)).indexOf(61)) != -1 && CHARSET.equals(param.substring(0, eqIdx))) {
                            ret.charset = param.substring(eqIdx + 1);
                        }
                        aidx = idx + 1;
                    }
                }
            }
        }
        if (pidx < urlStr.length()) {
            ret.path = urlStr.substring(pidx);
        }
        return ret;
    }

    static class DataParsedURLData
    extends ParsedURLData {
        String charset;

        DataParsedURLData() {
        }

        @Override
        public boolean complete() {
            return this.path != null;
        }

        @Override
        public String getPortStr() {
            String portStr = "data:";
            if (this.host != null) {
                portStr = portStr + this.host;
            }
            portStr = portStr + ",";
            return portStr;
        }

        @Override
        public String toString() {
            String ret = this.getPortStr();
            if (this.path != null) {
                ret = ret + this.path;
            }
            if (this.ref != null) {
                ret = ret + '#' + this.ref;
            }
            return ret;
        }

        @Override
        public String getContentType(String userAgent) {
            return this.contentType;
        }

        @Override
        public String getContentEncoding(String userAgent) {
            return this.contentEncoding;
        }

        @Override
        protected InputStream openStreamInternal(String userAgent, Iterator mimeTypes, Iterator encodingTypes) throws IOException {
            this.stream = DataParsedURLData.decode(this.path);
            if (ParsedURLDataProtocolHandler.BASE64.equals(this.contentEncoding)) {
                this.stream = new Base64DecodeStream(this.stream);
            }
            return this.stream;
        }

        public static InputStream decode(String s) {
            int len = s.length();
            byte[] data = new byte[len];
            int j = 0;
            block3: for (int i = 0; i < len; ++i) {
                char c = s.charAt(i);
                switch (c) {
                    default: {
                        data[j++] = (byte)c;
                        continue block3;
                    }
                    case '%': {
                        byte b;
                        if (i + 2 >= len) continue block3;
                        char c1 = s.charAt((i += 2) - 1);
                        if (c1 >= '0' && c1 <= '9') {
                            b = (byte)(c1 - 48);
                        } else if (c1 >= 'a' && c1 <= 'z') {
                            b = (byte)(c1 - 97 + 10);
                        } else {
                            if (c1 < 'A' || c1 > 'Z') continue block3;
                            b = (byte)(c1 - 65 + 10);
                        }
                        b = (byte)(b * 16);
                        char c2 = s.charAt(i);
                        if (c2 >= '0' && c2 <= '9') {
                            b = (byte)(b + (byte)(c2 - 48));
                        } else if (c2 >= 'a' && c2 <= 'z') {
                            b = (byte)(b + (byte)(c2 - 97 + 10));
                        } else {
                            if (c2 < 'A' || c2 > 'Z') continue block3;
                            b = (byte)(b + (byte)(c2 - 65 + 10));
                        }
                        data[j++] = b;
                    }
                }
            }
            return new ByteArrayInputStream(data, 0, j);
        }
    }
}

