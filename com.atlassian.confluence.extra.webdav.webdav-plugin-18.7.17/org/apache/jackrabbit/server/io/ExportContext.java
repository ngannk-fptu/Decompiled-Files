/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.server.io;

import java.io.OutputStream;
import javax.jcr.Item;
import org.apache.jackrabbit.server.io.IOContext;

public interface ExportContext
extends IOContext {
    public Item getExportRoot();

    public OutputStream getOutputStream();

    public void setContentType(String var1, String var2);

    public void setContentLanguage(String var1);

    public void setContentLength(long var1);

    public void setCreationTime(long var1);

    public void setModificationTime(long var1);

    public void setETag(String var1);

    public void setProperty(Object var1, Object var2);
}

