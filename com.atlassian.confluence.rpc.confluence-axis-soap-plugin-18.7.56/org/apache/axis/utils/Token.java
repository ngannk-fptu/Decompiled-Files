/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.utils;

class Token {
    public static final int TOKEN_SEPARATOR = 0;
    public static final int TOKEN_STRING = 1;
    private final int m_type;
    private final String m_value;

    public Token(int type, String value) {
        this.m_type = type;
        this.m_value = value;
    }

    public final String getValue() {
        return this.m_value;
    }

    public final int getType() {
        return this.m_type;
    }

    public final String toString() {
        return this.m_type + ":" + this.m_value;
    }
}

