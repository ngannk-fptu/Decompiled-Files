/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.lang;

public interface Signature {
    public String toString();

    public String toShortString();

    public String toLongString();

    public String getName();

    public int getModifiers();

    public Class getDeclaringType();

    public String getDeclaringTypeName();
}

