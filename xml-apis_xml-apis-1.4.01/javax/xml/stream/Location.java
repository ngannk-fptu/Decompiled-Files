/*
 * Decompiled with CFR 0.152.
 */
package javax.xml.stream;

public interface Location {
    public int getCharacterOffset();

    public int getColumnNumber();

    public int getLineNumber();

    public String getPublicId();

    public String getSystemId();
}

