/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.modzdetector;

import java.io.InputStream;

public interface ResourceAccessor {
    public InputStream getResourceByPath(String var1);

    public InputStream getResourceFromClasspath(String var1);
}

