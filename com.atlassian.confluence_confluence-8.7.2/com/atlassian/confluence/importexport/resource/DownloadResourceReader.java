/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.importexport.resource;

import java.io.InputStream;
import java.util.Date;

public interface DownloadResourceReader {
    public String getName();

    public String getContentType();

    public long getContentLength();

    public Date getLastModificationDate();

    public InputStream getStreamForReading();
}

