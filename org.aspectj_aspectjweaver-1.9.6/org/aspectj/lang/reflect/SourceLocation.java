/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.lang.reflect;

public interface SourceLocation {
    public Class getWithinType();

    public String getFileName();

    public int getLine();

    public int getColumn();
}

