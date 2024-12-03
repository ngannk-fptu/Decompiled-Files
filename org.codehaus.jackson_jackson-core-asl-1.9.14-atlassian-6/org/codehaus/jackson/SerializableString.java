/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jackson;

public interface SerializableString {
    public String getValue();

    public int charLength();

    public char[] asQuotedChars();

    public byte[] asUnquotedUTF8();

    public byte[] asQuotedUTF8();
}

