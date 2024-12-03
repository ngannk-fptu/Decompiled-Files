/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.importexport.ImportExportException
 */
package com.atlassian.confluence.efi.services;

import com.atlassian.confluence.efi.services.SpaceImportConfig;
import com.atlassian.confluence.importexport.ImportExportException;
import java.net.URL;

public interface SpaceService {
    public String createUniqueSpaceKey(String var1);

    public URL getOnboardingSpaceZipURL();

    public void importAndReindex(URL var1, SpaceImportConfig var2) throws ImportExportException;
}

