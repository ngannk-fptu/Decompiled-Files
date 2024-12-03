/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.dbcp.dbcp2.datasources;

import java.io.Serializable;
import java.util.Arrays;
import org.apache.tomcat.dbcp.dbcp2.Utils;

final class CharArray
implements Serializable {
    private static final long serialVersionUID = 1L;
    static final CharArray NULL = new CharArray((char[])null);
    private final char[] chars;

    CharArray(char[] chars) {
        this.chars = Utils.clone(chars);
    }

    CharArray(String string) {
        this.chars = Utils.toCharArray(string);
    }

    String asString() {
        return Utils.toString(this.chars);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof CharArray)) {
            return false;
        }
        CharArray other = (CharArray)obj;
        return Arrays.equals(this.chars, other.chars);
    }

    char[] get() {
        return Utils.clone(this.chars);
    }

    public int hashCode() {
        return Arrays.hashCode(this.chars);
    }
}

