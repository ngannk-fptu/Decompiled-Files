/*
 * Decompiled with CFR 0.152.
 */
package org.tuckey.web.filters.urlrewrite.utils;

public interface StringMatchingMatcher {
    public boolean find();

    public boolean isMultipleMatchingSupported();

    public int start();

    public int end();

    public void reset();

    public boolean isFound();

    public int groupCount();

    public String group(int var1);
}

