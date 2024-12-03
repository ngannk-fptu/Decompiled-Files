/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.util;

import com.amazonaws.annotation.Immutable;
import com.amazonaws.util.NameValuePair;
import java.io.Serializable;

@Immutable
class BasicNameValuePair
implements NameValuePair,
Cloneable,
Serializable {
    private static final long serialVersionUID = 1L;
    public static final int HASH_SEED = 17;
    public static final int HASH_OFFSET = 37;
    private final String name;
    private final String value;

    BasicNameValuePair(String name, String value) {
        if (name == null) {
            throw new IllegalArgumentException("Name must not be null");
        }
        this.name = name;
        this.value = value;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getValue() {
        return this.value;
    }

    public String toString() {
        if (this.value == null) {
            return this.name;
        }
        int len = this.name.length() + 1 + this.value.length();
        StringBuilder buffer = new StringBuilder(len);
        buffer.append(this.name);
        buffer.append("=");
        buffer.append(this.value);
        return buffer.toString();
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object instanceof NameValuePair) {
            BasicNameValuePair that = (BasicNameValuePair)object;
            return this.name.equals(that.name) && BasicNameValuePair.equals(this.value, that.value);
        }
        return false;
    }

    private static boolean equals(Object obj1, Object obj2) {
        return obj1 == null ? obj2 == null : obj1.equals(obj2);
    }

    public int hashCode() {
        int hash = 17;
        hash = BasicNameValuePair.hashCode(hash, this.name);
        hash = BasicNameValuePair.hashCode(hash, this.value);
        return hash;
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    private static int hashCode(int seed, Object obj) {
        return BasicNameValuePair.hashCode(seed, obj != null ? obj.hashCode() : 0);
    }

    private static int hashCode(int seed, int hashcode) {
        return seed * 37 + hashcode;
    }
}

