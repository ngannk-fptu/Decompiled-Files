/*
 * Decompiled with CFR 0.152.
 */
package javax.xml.transform;

public interface SourceLocator {
    public String getPublicId();

    public String getSystemId();

    public int getLineNumber();

    public int getColumnNumber();
}

