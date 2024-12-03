/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bandana.BandanaContext
 *  com.atlassian.confluence.core.ConfluenceActionSupport
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext
 */
package com.atlassian.confluence.extra.flyingpdf.config;

import com.atlassian.bandana.BandanaContext;
import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.extra.flyingpdf.config.PdfExportSettingsManager;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext;

public abstract class AbstractConfigurePdfExportAction
extends ConfluenceActionSupport {
    protected BandanaContext context = new ConfluenceBandanaContext();
    protected PermissionManager permissionManager;
    protected PdfExportSettingsManager pdfSettings;

    public void setPdfExportSettingsManager(PdfExportSettingsManager pdfSettings) {
        this.pdfSettings = pdfSettings;
    }

    public void setPermissionManager(PermissionManager permissionManager) {
        this.permissionManager = permissionManager;
    }

    public boolean isPermitted() {
        return this.permissionManager.isConfluenceAdministrator(this.getRemoteUser());
    }

    protected BandanaContext getBandanaContext() {
        return this.context;
    }
}

