/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.server.io;

import java.io.IOException;
import org.apache.jackrabbit.server.io.ExportContext;
import org.apache.jackrabbit.server.io.IOHandler;
import org.apache.jackrabbit.server.io.ImportContext;
import org.apache.jackrabbit.webdav.DavResource;
import org.apache.tika.detect.Detector;

public interface IOManager {
    public void addIOHandler(IOHandler var1);

    public IOHandler[] getIOHandlers();

    public Detector getDetector();

    public void setDetector(Detector var1);

    public boolean importContent(ImportContext var1, boolean var2) throws IOException;

    public boolean importContent(ImportContext var1, DavResource var2) throws IOException;

    public boolean exportContent(ExportContext var1, boolean var2) throws IOException;

    public boolean exportContent(ExportContext var1, DavResource var2) throws IOException;
}

