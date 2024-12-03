/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.importexport.resource;

import com.atlassian.confluence.importexport.resource.DownloadResourceManager;
import com.atlassian.confluence.importexport.resource.DownloadResourceNotFoundException;
import com.atlassian.confluence.importexport.resource.PartialDownloadResourceReader;
import com.atlassian.confluence.importexport.resource.UnauthorizedDownloadResourceException;
import com.atlassian.confluence.web.rangerequest.RangeNotSatisfiableException;
import java.util.Map;

public interface PartialDownloadResourceManager
extends DownloadResourceManager {
    public PartialDownloadResourceReader getPartialResourceReader(String var1, String var2, Map var3, String var4) throws UnauthorizedDownloadResourceException, DownloadResourceNotFoundException, RangeNotSatisfiableException;
}

