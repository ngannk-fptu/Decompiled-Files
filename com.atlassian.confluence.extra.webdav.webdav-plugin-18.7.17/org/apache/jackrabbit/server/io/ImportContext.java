/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.server.io;

import java.io.InputStream;
import javax.jcr.Item;
import org.apache.jackrabbit.server.io.IOContext;

public interface ImportContext
extends IOContext {
    public Item getImportRoot();

    public String getSystemId();

    public InputStream getInputStream();

    public long getModificationTime();

    public String getContentLanguage();

    public long getContentLength();

    public String getMimeType();

    public String getEncoding();

    public Object getProperty(Object var1);
}

