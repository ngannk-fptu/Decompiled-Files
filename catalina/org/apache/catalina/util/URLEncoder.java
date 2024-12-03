/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.BitSet;

public final class URLEncoder
implements Cloneable {
    private static final char[] hexadecimal = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
    public static final URLEncoder DEFAULT = new URLEncoder();
    public static final URLEncoder QUERY = new URLEncoder();
    private final BitSet safeCharacters;
    private boolean encodeSpaceAsPlus = false;

    public URLEncoder() {
        this(new BitSet(256));
        char i;
        for (i = 'a'; i <= 'z'; i = (char)((char)(i + '\u0001'))) {
            this.addSafeCharacter(i);
        }
        for (i = 'A'; i <= 'Z'; i = (char)(i + '\u0001')) {
            this.addSafeCharacter(i);
        }
        for (i = '0'; i <= '9'; i = (char)(i + '\u0001')) {
            this.addSafeCharacter(i);
        }
    }

    private URLEncoder(BitSet safeCharacters) {
        this.safeCharacters = safeCharacters;
    }

    public void addSafeCharacter(char c) {
        this.safeCharacters.set(c);
    }

    public void removeSafeCharacter(char c) {
        this.safeCharacters.clear(c);
    }

    public void setEncodeSpaceAsPlus(boolean encodeSpaceAsPlus) {
        this.encodeSpaceAsPlus = encodeSpaceAsPlus;
    }

    public String encode(String path, Charset charset) {
        int maxBytesPerChar = 10;
        StringBuilder rewrittenPath = new StringBuilder(path.length());
        ByteArrayOutputStream buf = new ByteArrayOutputStream(maxBytesPerChar);
        OutputStreamWriter writer = new OutputStreamWriter((OutputStream)buf, charset);
        for (int i = 0; i < path.length(); ++i) {
            byte[] ba;
            char c = path.charAt(i);
            if (this.safeCharacters.get(c)) {
                rewrittenPath.append(c);
                continue;
            }
            if (this.encodeSpaceAsPlus && c == ' ') {
                rewrittenPath.append('+');
                continue;
            }
            try {
                writer.write(c);
                writer.flush();
            }
            catch (IOException e) {
                buf.reset();
                continue;
            }
            for (byte toEncode : ba = buf.toByteArray()) {
                rewrittenPath.append('%');
                int low = toEncode & 0xF;
                int high = (toEncode & 0xF0) >> 4;
                rewrittenPath.append(hexadecimal[high]);
                rewrittenPath.append(hexadecimal[low]);
            }
            buf.reset();
        }
        return rewrittenPath.toString();
    }

    public Object clone() {
        URLEncoder result = new URLEncoder((BitSet)this.safeCharacters.clone());
        result.setEncodeSpaceAsPlus(this.encodeSpaceAsPlus);
        return result;
    }

    static {
        DEFAULT.addSafeCharacter('-');
        DEFAULT.addSafeCharacter('.');
        DEFAULT.addSafeCharacter('_');
        DEFAULT.addSafeCharacter('~');
        DEFAULT.addSafeCharacter('!');
        DEFAULT.addSafeCharacter('$');
        DEFAULT.addSafeCharacter('&');
        DEFAULT.addSafeCharacter('\'');
        DEFAULT.addSafeCharacter('(');
        DEFAULT.addSafeCharacter(')');
        DEFAULT.addSafeCharacter('*');
        DEFAULT.addSafeCharacter('+');
        DEFAULT.addSafeCharacter(',');
        DEFAULT.addSafeCharacter(';');
        DEFAULT.addSafeCharacter('=');
        DEFAULT.addSafeCharacter(':');
        DEFAULT.addSafeCharacter('@');
        DEFAULT.addSafeCharacter('/');
        QUERY.setEncodeSpaceAsPlus(true);
        QUERY.addSafeCharacter('*');
        QUERY.addSafeCharacter('-');
        QUERY.addSafeCharacter('.');
        QUERY.addSafeCharacter('_');
        QUERY.addSafeCharacter('=');
        QUERY.addSafeCharacter('&');
    }
}

