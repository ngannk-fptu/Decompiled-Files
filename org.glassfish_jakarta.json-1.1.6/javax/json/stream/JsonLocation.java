/*
 * Decompiled with CFR 0.152.
 */
package javax.json.stream;

public interface JsonLocation {
    public long getLineNumber();

    public long getColumnNumber();

    public long getStreamOffset();
}

