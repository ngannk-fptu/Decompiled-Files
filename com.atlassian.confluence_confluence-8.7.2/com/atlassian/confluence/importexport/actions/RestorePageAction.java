/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.renderer.WikiStyleRenderer
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.importexport.actions;

import com.atlassian.confluence.importexport.DefaultImportContext;
import com.atlassian.confluence.importexport.ImportExportException;
import com.atlassian.confluence.importexport.actions.RestoreAction;
import com.atlassian.confluence.importexport.impl.ExportDescriptor;
import com.atlassian.confluence.importexport.impl.ExportScope;
import com.atlassian.confluence.importexport.impl.UnexpectedImportZipFileContents;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.spaces.Spaced;
import com.atlassian.confluence.spaces.actions.SpaceAdministrative;
import com.atlassian.confluence.themes.ThemeHelper;
import com.atlassian.confluence.themes.ThemeManager;
import com.atlassian.renderer.WikiStyleRenderer;
import com.atlassian.user.User;

@Deprecated
public class RestorePageAction
extends RestoreAction
implements SpaceAdministrative,
Spaced {
    private SpaceManager spaceManager;
    private ThemeManager themeManager;
    private Space space;
    private String key;
    private WikiStyleRenderer wikiStyleRenderer;

    @Override
    protected boolean isImportAllowed(ExportDescriptor exportDescriptor) throws ImportExportException {
        ExportScope exportScope;
        try {
            exportScope = exportDescriptor.getScope();
        }
        catch (ExportScope.IllegalExportScopeException e) {
            this.addActionError(this.getText("error.export.type.could.not.be.determined"));
            return false;
        }
        if (exportScope != ExportScope.PAGE) {
            this.addActionError(this.getText("error.can.only.import.backups.of.pages"));
            return false;
        }
        return true;
    }

    @Override
    public boolean isPermitted() {
        return this.permissionManager.hasCreatePermission((User)this.getAuthenticatedUser(), (Object)this.space, Page.class);
    }

    @Override
    public Space getSpace() {
        if (this.space == null && Space.isValidSpaceKey(this.getKey())) {
            this.space = this.spaceManager.getSpace(this.getKey());
        }
        return this.space;
    }

    @Override
    public void setSpaceManager(SpaceManager spaceManager) {
        this.spaceManager = spaceManager;
    }

    @Override
    protected DefaultImportContext createImportContext(ExportDescriptor exportDescriptor) throws ImportExportException, UnexpectedImportZipFileContents {
        DefaultImportContext importContext = super.createImportContext(exportDescriptor);
        importContext.setDefaultSpaceKey(this.getSpace().getKey());
        importContext.setIncrementalImport(true);
        importContext.setDeleteWorkingFile(false);
        return importContext;
    }

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public WikiStyleRenderer getWikiStyleRenderer() {
        return this.wikiStyleRenderer;
    }

    public void setWikiStyleRenderer(WikiStyleRenderer wikiStyleRenderer) {
        this.wikiStyleRenderer = wikiStyleRenderer;
    }

    public ThemeHelper getThemeHelper() {
        return this.getHelper();
    }

    public void setThemeManager(ThemeManager themeManager) {
        this.themeManager = themeManager;
    }

    public ThemeManager getThemeManager() {
        return this.themeManager;
    }
}

