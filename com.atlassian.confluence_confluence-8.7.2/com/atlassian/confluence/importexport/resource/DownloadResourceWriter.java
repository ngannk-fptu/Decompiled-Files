/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.importexport.resource;

import java.io.OutputStream;

public interface DownloadResourceWriter {
    public String getResourcePath();

    public OutputStream getStreamForWriting();
}

