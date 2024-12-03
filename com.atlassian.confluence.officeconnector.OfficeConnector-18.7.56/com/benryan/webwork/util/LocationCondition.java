/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.pages.Page
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugin.web.Condition
 *  com.benryan.components.OcSettingsManager
 */
package com.benryan.webwork.util;

import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugin.web.Condition;
import com.benryan.components.OcSettingsManager;
import com.benryan.webwork.util.EditInWordPermission;
import java.util.Map;

public class LocationCondition
extends EditInWordPermission
implements Condition {
    private static final String PAGETABS = "navbar";
    private static final String PAGEACTIONS = "actions";
    private static final String DOCIMPORT = "docimport";
    private static final String LOCATION = "location";
    private String _location;

    public LocationCondition(@ComponentImport PermissionManager permissionManager, OcSettingsManager ocSettingsManager) {
        super(permissionManager, ocSettingsManager);
    }

    public void init(Map map) {
        this._location = (String)map.get(LOCATION);
    }

    @Override
    protected boolean shouldDisplay(AbstractPage page) {
        boolean editIsPageAction;
        boolean bl = editIsPageAction = this.ocSettingsManager.getEditInWordLocation() == 2;
        if (DOCIMPORT.equals(this._location)) {
            return page instanceof Page;
        }
        if (page instanceof Page) {
            if (PAGETABS.equals(this._location)) {
                return !editIsPageAction;
            }
            return editIsPageAction;
        }
        return false;
    }
}

