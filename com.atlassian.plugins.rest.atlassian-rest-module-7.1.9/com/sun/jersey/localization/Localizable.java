/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.localization;

public interface Localizable {
    public static final String NOT_LOCALIZABLE = new String("\u0000");

    public String getKey();

    public Object[] getArguments();

    public String getResourceBundleName();
}

