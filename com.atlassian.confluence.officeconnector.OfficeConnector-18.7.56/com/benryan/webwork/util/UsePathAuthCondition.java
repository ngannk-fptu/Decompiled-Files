/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.benryan.components.OcSettingsManager
 */
package com.benryan.webwork.util;

import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.benryan.components.OcSettingsManager;
import com.benryan.webwork.util.EditInWordPermission;
import java.util.Map;

public class UsePathAuthCondition
extends EditInWordPermission {
    private static final String PATHAUTH = "pathauth";
    boolean hasPathAuth;

    public UsePathAuthCondition(@ComponentImport PermissionManager permissionManager, OcSettingsManager ocSettingsManager) {
        super(permissionManager, ocSettingsManager);
    }

    public void init(Map map) {
        this.hasPathAuth = Boolean.parseBoolean((String)map.get(PATHAUTH));
    }

    @Override
    protected boolean shouldDisplay(AbstractPage page) {
        boolean pathAuthSetting = this.ocSettingsManager.getPathAuth();
        return this.hasPathAuth == pathAuthSetting;
    }
}

