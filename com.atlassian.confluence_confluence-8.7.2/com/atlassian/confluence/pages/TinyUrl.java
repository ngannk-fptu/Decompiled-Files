/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.codec.binary.Base64
 */
package com.atlassian.confluence.pages;

import com.atlassian.confluence.pages.AbstractPage;
import org.apache.commons.codec.binary.Base64;

public class TinyUrl {
    private long id;
    private String identifier;

    public TinyUrl(long id) {
        this.id = id;
        this.identifier = this.makeSafeForUrl(Base64.encodeBase64((byte[])TinyUrl.longToByteArray(id)));
    }

    public TinyUrl(AbstractPage page) {
        this(page.getId());
    }

    public TinyUrl(String identifier) {
        this.identifier = identifier;
        this.id = this.byteArrayToLong(Base64.decodeBase64((byte[])this.decodeFromUrl(identifier).getBytes()));
    }

    public String getIdentifier() {
        return this.identifier;
    }

    public long getPageId() {
        return this.id;
    }

    private String makeSafeForUrl(byte[] bytes) {
        char lastChar;
        StringBuilder buf = new StringBuilder();
        boolean padding = true;
        for (int i = bytes.length - 1; i >= 0; --i) {
            byte b = bytes[i];
            if (b == 61 || b == 10 || padding && b == 65) continue;
            padding = false;
            if (b == 47) {
                buf.insert(0, '-');
                continue;
            }
            if (b == 43) {
                buf.insert(0, '_');
                continue;
            }
            buf.insert(0, (char)b);
        }
        if (buf.length() > 0 && ((lastChar = buf.charAt(buf.length() - 1)) == '-' || lastChar == '_')) {
            buf.append('/');
        }
        return buf.toString();
    }

    private String decodeFromUrl(String identifier) {
        if (identifier.endsWith("/")) {
            identifier = identifier.substring(0, identifier.length() - 1);
        }
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < 11; ++i) {
            if (i >= identifier.length()) {
                buf.append('A');
                continue;
            }
            char c = identifier.charAt(i);
            if (c == '-') {
                buf.append('/');
                continue;
            }
            if (c == '_') {
                buf.append('+');
                continue;
            }
            buf.append(c);
        }
        buf.append("=\n");
        return buf.toString();
    }

    private long byteArrayToLong(byte[] bytes) {
        long l = 0L;
        for (int i = bytes.length - 1; i >= 0; --i) {
            l <<= 8;
            l |= (long)bytes[i] & 0xFFL;
        }
        return l;
    }

    private static byte[] longToByteArray(long l) {
        byte[] retVal = new byte[8];
        for (int i = 0; i < 8; ++i) {
            retVal[i] = (byte)l;
            l >>= 8;
        }
        return retVal;
    }
}

