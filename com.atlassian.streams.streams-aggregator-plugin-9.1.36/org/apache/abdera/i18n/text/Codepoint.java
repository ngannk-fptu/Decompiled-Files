/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.i18n.text;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import org.apache.abdera.i18n.text.CharUtils;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class Codepoint
implements Serializable,
Cloneable,
Comparable<Codepoint> {
    private static final long serialVersionUID = 140337939131905483L;
    private static final String DEFAULT_ENCODING = "UTF-8";
    private final int value;

    public Codepoint(byte[] bytes) {
        try {
            this.value = Codepoint.valueFromCharSequence(new String(bytes, DEFAULT_ENCODING));
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public Codepoint(byte[] bytes, String encoding) throws UnsupportedEncodingException {
        this.value = Codepoint.valueFromCharSequence(new String(bytes, encoding));
    }

    public Codepoint(CharSequence value) {
        this(Codepoint.valueFromCharSequence(value));
    }

    private static int valueFromCharSequence(CharSequence s) {
        if (s.length() == 1) {
            return s.charAt(0);
        }
        if (s.length() > 2) {
            throw new IllegalArgumentException("Too many chars");
        }
        char high = s.charAt(0);
        char low = s.charAt(1);
        return CharUtils.toSupplementary(high, low).getValue();
    }

    public Codepoint(char value) {
        this((int)value);
    }

    public Codepoint(char high, char low) {
        this(CharUtils.toSupplementary(high, low).getValue());
    }

    public Codepoint(Codepoint codepoint) {
        this(codepoint.value);
    }

    public Codepoint(int value) {
        if (value < 0) {
            throw new IllegalArgumentException("Invalid Codepoint");
        }
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public boolean isSupplementary() {
        return CharUtils.isSupplementary(this.value);
    }

    public boolean isLowSurrogate() {
        return CharUtils.isLowSurrogate((char)this.value);
    }

    public boolean isHighSurrogate() {
        return CharUtils.isHighSurrogate((char)this.value);
    }

    public char getHighSurrogate() {
        return CharUtils.getHighSurrogate(this.value);
    }

    public char getLowSurrogate() {
        return CharUtils.getLowSurrogate(this.value);
    }

    public boolean isBidi() {
        return CharUtils.isBidi(this.value);
    }

    public boolean isDigit() {
        return CharUtils.isDigit(this.value);
    }

    public boolean isAlpha() {
        return CharUtils.isAlpha(this.value);
    }

    public boolean isAlphaDigit() {
        return CharUtils.isAlpha(this.value);
    }

    @Override
    public int compareTo(Codepoint o) {
        return this.value < o.value ? -1 : (this.value == o.value ? 0 : 1);
    }

    public String toString() {
        return CharUtils.toString(this.value);
    }

    public char[] toChars() {
        return this.toString().toCharArray();
    }

    public int getCharCount() {
        return this.toChars().length;
    }

    public byte[] toBytes() {
        try {
            return this.toBytes(DEFAULT_ENCODING);
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public byte[] toBytes(String encoding) throws UnsupportedEncodingException {
        return this.toString().getBytes(encoding);
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + this.value;
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        Codepoint other = (Codepoint)obj;
        return this.value == other.value;
    }

    public int getPlane() {
        return this.value / 65536;
    }

    public Codepoint clone() {
        try {
            return (Codepoint)super.clone();
        }
        catch (CloneNotSupportedException e) {
            return new Codepoint(this.value);
        }
    }

    public Codepoint next() {
        if (this.value == 0x10FFFF) {
            throw new IndexOutOfBoundsException();
        }
        return new Codepoint(this.value + 1);
    }

    public Codepoint previous() {
        if (this.value == 0) {
            throw new IndexOutOfBoundsException();
        }
        return new Codepoint(this.value - 1);
    }
}

