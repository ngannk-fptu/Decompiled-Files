/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm.osgi;

public interface Version
extends Comparable<Version> {
    public int getMajor();

    public int getMinor();

    public int getMicro();

    public String getQualifier();
}

