/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.importexport.resource;

import com.atlassian.confluence.importexport.resource.DownloadResourceNotFoundException;
import com.atlassian.confluence.importexport.resource.DownloadResourceReader;
import com.atlassian.confluence.importexport.resource.UnauthorizedDownloadResourceException;
import java.util.Map;

public interface DownloadResourceManager {
    public boolean matches(String var1);

    public DownloadResourceReader getResourceReader(String var1, String var2, Map var3) throws UnauthorizedDownloadResourceException, DownloadResourceNotFoundException;
}

