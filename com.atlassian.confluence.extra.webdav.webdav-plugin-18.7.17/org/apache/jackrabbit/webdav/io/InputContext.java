/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.webdav.io;

import java.io.InputStream;

public interface InputContext {
    public boolean hasStream();

    public InputStream getInputStream();

    public long getModificationTime();

    public String getContentLanguage();

    public long getContentLength();

    public String getContentType();

    public String getProperty(String var1);
}

