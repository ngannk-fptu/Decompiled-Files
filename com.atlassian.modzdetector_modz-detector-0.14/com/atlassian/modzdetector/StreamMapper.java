/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.modzdetector;

import java.io.File;
import java.io.InputStream;

public interface StreamMapper {
    public InputStream mapStream(String var1, String var2);

    public String getResourcePath(String var1);

    public String getResourceKey(File var1);
}

