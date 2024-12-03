/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.importexport.resource;

import com.atlassian.confluence.importexport.resource.DownloadResourceManager;
import com.atlassian.confluence.importexport.resource.DownloadResourceWriter;

public interface WritableDownloadResourceManager
extends DownloadResourceManager {
    public DownloadResourceWriter getResourceWriter(String var1, String var2, String var3);
}

