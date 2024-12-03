/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.ext.stax;

public interface DTDReader {
    public static final String PROPERTY = DTDReader.class.getName();

    public String getRootName();

    public String getPublicId();

    public String getSystemId();
}

