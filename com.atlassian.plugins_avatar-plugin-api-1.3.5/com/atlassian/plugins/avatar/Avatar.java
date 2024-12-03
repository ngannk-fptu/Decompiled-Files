/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.avatar;

import java.io.IOException;
import java.io.InputStream;

public interface Avatar {
    public String getOwnerId();

    public String getUrl();

    public int getSize();

    public String getContentType();

    public boolean isExternal();

    public InputStream getBytes() throws IOException;

    public Avatar atSize(int var1);
}

