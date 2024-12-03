/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.server.io;

import java.io.IOException;
import org.apache.jackrabbit.server.io.ExportContext;
import org.apache.jackrabbit.server.io.IOManager;
import org.apache.jackrabbit.server.io.ImportContext;
import org.apache.jackrabbit.webdav.DavResource;

public interface IOHandler {
    public IOManager getIOManager();

    public void setIOManager(IOManager var1);

    public String getName();

    public boolean canImport(ImportContext var1, boolean var2);

    public boolean canImport(ImportContext var1, DavResource var2);

    public boolean importContent(ImportContext var1, boolean var2) throws IOException;

    public boolean importContent(ImportContext var1, DavResource var2) throws IOException;

    public boolean canExport(ExportContext var1, boolean var2);

    public boolean canExport(ExportContext var1, DavResource var2);

    public boolean exportContent(ExportContext var1, boolean var2) throws IOException;

    public boolean exportContent(ExportContext var1, DavResource var2) throws IOException;
}

