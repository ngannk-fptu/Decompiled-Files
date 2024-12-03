/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jackson;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public enum JsonEncoding {
    UTF8("UTF-8", false),
    UTF16_BE("UTF-16BE", true),
    UTF16_LE("UTF-16LE", false),
    UTF32_BE("UTF-32BE", true),
    UTF32_LE("UTF-32LE", false);

    protected final String _javaName;
    protected final boolean _bigEndian;

    private JsonEncoding(String javaName, boolean bigEndian) {
        this._javaName = javaName;
        this._bigEndian = bigEndian;
    }

    public String getJavaName() {
        return this._javaName;
    }

    public boolean isBigEndian() {
        return this._bigEndian;
    }
}

