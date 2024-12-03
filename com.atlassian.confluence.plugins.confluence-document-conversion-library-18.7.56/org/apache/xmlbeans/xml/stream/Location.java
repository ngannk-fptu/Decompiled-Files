/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.xml.stream;

public interface Location {
    public int getColumnNumber();

    public int getLineNumber();

    public String getPublicId();

    public String getSystemId();
}

