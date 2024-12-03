/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.buf;

import java.io.IOException;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.Locale;
import org.apache.tomcat.util.buf.Ascii;
import org.apache.tomcat.util.buf.ByteChunk;
import org.apache.tomcat.util.buf.CharChunk;
import org.apache.tomcat.util.buf.HexUtils;
import org.apache.tomcat.util.res.StringManager;

public final class MessageBytes
implements Cloneable,
Serializable {
    private static final long serialVersionUID = 1L;
    private static final StringManager sm = StringManager.getManager(MessageBytes.class);
    private int type = 0;
    public static final int T_NULL = 0;
    public static final int T_STR = 1;
    public static final int T_BYTES = 2;
    public static final int T_CHARS = 3;
    public static final char[] EMPTY_CHAR_ARRAY = new char[0];
    private int hashCode = 0;
    private boolean hasHashCode = false;
    private final ByteChunk byteC = new ByteChunk();
    private final CharChunk charC = new CharChunk();
    private String strValue;
    private long longValue;
    private boolean hasLongValue = false;
    private static final MessageBytesFactory factory = new MessageBytesFactory();

    private MessageBytes() {
    }

    public static MessageBytes newInstance() {
        return factory.newInstance();
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public boolean isNull() {
        return this.type == 0;
    }

    public void recycle() {
        this.type = 0;
        this.byteC.recycle();
        this.charC.recycle();
        this.strValue = null;
        this.hasHashCode = false;
        this.hasLongValue = false;
    }

    public void setBytes(byte[] b, int off, int len) {
        this.byteC.setBytes(b, off, len);
        this.type = 2;
        this.hasHashCode = false;
        this.hasLongValue = false;
    }

    public void setChars(char[] c, int off, int len) {
        this.charC.setChars(c, off, len);
        this.type = 3;
        this.hasHashCode = false;
        this.hasLongValue = false;
    }

    public void setString(String s) {
        this.strValue = s;
        this.hasHashCode = false;
        this.hasLongValue = false;
        this.type = s == null ? 0 : 1;
    }

    public String toString() {
        switch (this.type) {
            case 0: 
            case 1: {
                break;
            }
            case 2: {
                this.strValue = this.byteC.toString();
                break;
            }
            case 3: {
                this.strValue = this.charC.toString();
            }
        }
        return this.strValue;
    }

    public String toStringType() {
        switch (this.type) {
            case 0: 
            case 1: {
                break;
            }
            case 2: {
                this.setString(this.byteC.toString());
                break;
            }
            case 3: {
                this.setString(this.charC.toString());
            }
        }
        return this.strValue;
    }

    public int getType() {
        return this.type;
    }

    public ByteChunk getByteChunk() {
        return this.byteC;
    }

    public CharChunk getCharChunk() {
        return this.charC;
    }

    public String getString() {
        return this.strValue;
    }

    public Charset getCharset() {
        return this.byteC.getCharset();
    }

    public void setCharset(Charset charset) {
        this.byteC.setCharset(charset);
    }

    public void toBytes() {
        if (this.type == 0) {
            this.byteC.recycle();
            return;
        }
        if (this.type == 2) {
            return;
        }
        if (this.getCharset() == ByteChunk.DEFAULT_CHARSET) {
            if (this.type == 3) {
                this.toBytesSimple(this.charC.getChars(), this.charC.getStart(), this.charC.getLength());
            } else {
                char[] chars = this.strValue.toCharArray();
                this.toBytesSimple(chars, 0, chars.length);
            }
            return;
        }
        ByteBuffer bb = this.type == 3 ? this.getCharset().encode(CharBuffer.wrap(this.charC)) : this.getCharset().encode(this.strValue);
        this.byteC.setBytes(bb.array(), bb.arrayOffset(), bb.limit());
    }

    private void toBytesSimple(char[] chars, int start, int len) {
        byte[] bytes = new byte[len];
        for (int i = 0; i < len; ++i) {
            if (chars[i + start] > '\u00ff') {
                throw new IllegalArgumentException(sm.getString("messageBytes.illegalCharacter", Character.toString(chars[i + start]), chars[i + start]));
            }
            bytes[i] = (byte)chars[i + start];
        }
        this.byteC.setBytes(bytes, 0, len);
        this.type = 2;
    }

    public void toChars() {
        switch (this.type) {
            case 0: {
                this.charC.recycle();
            }
            case 3: {
                return;
            }
            case 2: {
                this.toString();
            }
            case 1: {
                char[] cc = this.strValue.toCharArray();
                this.charC.setChars(cc, 0, cc.length);
            }
        }
    }

    public int getLength() {
        if (this.type == 2) {
            return this.byteC.getLength();
        }
        if (this.type == 3) {
            return this.charC.getLength();
        }
        if (this.type == 1) {
            return this.strValue.length();
        }
        this.toString();
        if (this.strValue == null) {
            return 0;
        }
        return this.strValue.length();
    }

    public boolean equals(String s) {
        switch (this.type) {
            case 1: {
                if (this.strValue == null) {
                    return s == null;
                }
                return this.strValue.equals(s);
            }
            case 3: {
                return this.charC.equals(s);
            }
            case 2: {
                return this.byteC.equals(s);
            }
        }
        return false;
    }

    public boolean equalsIgnoreCase(String s) {
        switch (this.type) {
            case 1: {
                if (this.strValue == null) {
                    return s == null;
                }
                return this.strValue.equalsIgnoreCase(s);
            }
            case 3: {
                return this.charC.equalsIgnoreCase(s);
            }
            case 2: {
                return this.byteC.equalsIgnoreCase(s);
            }
        }
        return false;
    }

    public boolean equals(Object obj) {
        if (obj instanceof MessageBytes) {
            return this.equals((MessageBytes)obj);
        }
        return false;
    }

    public boolean equals(MessageBytes mb) {
        switch (this.type) {
            case 1: {
                return mb.equals(this.strValue);
            }
        }
        if (mb.type != 3 && mb.type != 2) {
            return this.equals(mb.toString());
        }
        if (mb.type == 3 && this.type == 3) {
            return this.charC.equals(mb.charC);
        }
        if (mb.type == 2 && this.type == 2) {
            return this.byteC.equals(mb.byteC);
        }
        if (mb.type == 3 && this.type == 2) {
            return this.byteC.equals(mb.charC);
        }
        if (mb.type == 2 && this.type == 3) {
            return mb.byteC.equals(this.charC);
        }
        return true;
    }

    public boolean startsWithIgnoreCase(String s, int pos) {
        switch (this.type) {
            case 1: {
                if (this.strValue == null) {
                    return false;
                }
                if (this.strValue.length() < pos + s.length()) {
                    return false;
                }
                for (int i = 0; i < s.length(); ++i) {
                    if (Ascii.toLower(s.charAt(i)) == Ascii.toLower(this.strValue.charAt(pos + i))) continue;
                    return false;
                }
                return true;
            }
            case 3: {
                return this.charC.startsWithIgnoreCase(s, pos);
            }
            case 2: {
                return this.byteC.startsWithIgnoreCase(s, pos);
            }
        }
        return false;
    }

    public int hashCode() {
        if (this.hasHashCode) {
            return this.hashCode;
        }
        int code = 0;
        this.hashCode = code = this.hash();
        this.hasHashCode = true;
        return code;
    }

    private int hash() {
        int code = 0;
        switch (this.type) {
            case 1: {
                for (int i = 0; i < this.strValue.length(); ++i) {
                    code = code * 37 + this.strValue.charAt(i);
                }
                return code;
            }
            case 3: {
                return this.charC.hash();
            }
            case 2: {
                return this.byteC.hash();
            }
        }
        return 0;
    }

    public int indexOf(String s, int starting) {
        this.toString();
        return this.strValue.indexOf(s, starting);
    }

    public int indexOf(String s) {
        return this.indexOf(s, 0);
    }

    public int indexOfIgnoreCase(String s, int starting) {
        this.toString();
        String upper = this.strValue.toUpperCase(Locale.ENGLISH);
        String sU = s.toUpperCase(Locale.ENGLISH);
        return upper.indexOf(sU, starting);
    }

    public void duplicate(MessageBytes src) throws IOException {
        switch (src.getType()) {
            case 2: {
                this.type = 2;
                ByteChunk bc = src.getByteChunk();
                this.byteC.allocate(2 * bc.getLength(), -1);
                this.byteC.append(bc);
                break;
            }
            case 3: {
                this.type = 3;
                CharChunk cc = src.getCharChunk();
                this.charC.allocate(2 * cc.getLength(), -1);
                this.charC.append(cc);
                break;
            }
            case 1: {
                this.type = 1;
                String sc = src.getString();
                this.setString(sc);
            }
        }
        this.setCharset(src.getCharset());
    }

    public void setLong(long l) {
        this.byteC.allocate(32, 64);
        long current = l;
        byte[] buf = this.byteC.getBuffer();
        int start = 0;
        int end = 0;
        if (l == 0L) {
            buf[end++] = 48;
        }
        if (l < 0L) {
            current = -l;
            buf[end++] = 45;
        }
        while (current > 0L) {
            int digit = (int)(current % 10L);
            current /= 10L;
            buf[end++] = HexUtils.getHex(digit);
        }
        this.byteC.setOffset(0);
        this.byteC.setEnd(end);
        --end;
        if (l < 0L) {
            ++start;
        }
        while (end > start) {
            byte temp = buf[start];
            buf[start] = buf[end];
            buf[end] = temp;
            ++start;
            --end;
        }
        this.longValue = l;
        this.hasHashCode = false;
        this.hasLongValue = true;
        this.type = 2;
    }

    public long getLong() {
        if (this.hasLongValue) {
            return this.longValue;
        }
        switch (this.type) {
            case 2: {
                this.longValue = this.byteC.getLong();
                break;
            }
            default: {
                this.longValue = Long.parseLong(this.toString());
            }
        }
        this.hasLongValue = true;
        return this.longValue;
    }

    private static class MessageBytesFactory {
        protected MessageBytesFactory() {
        }

        public MessageBytes newInstance() {
            return new MessageBytes();
        }
    }
}

