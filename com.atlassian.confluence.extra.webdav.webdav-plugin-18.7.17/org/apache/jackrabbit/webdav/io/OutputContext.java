/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.webdav.io;

import java.io.OutputStream;

public interface OutputContext {
    public boolean hasStream();

    public OutputStream getOutputStream();

    public void setContentLanguage(String var1);

    public void setContentLength(long var1);

    public void setContentType(String var1);

    public void setModificationTime(long var1);

    public void setETag(String var1);

    public void setProperty(String var1, String var2);
}

