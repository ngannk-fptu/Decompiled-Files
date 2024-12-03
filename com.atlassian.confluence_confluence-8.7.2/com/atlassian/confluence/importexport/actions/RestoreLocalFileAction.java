/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.websudo.WebSudoRequired
 *  com.atlassian.user.User
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  com.atlassian.xwork.RequireSecurityToken
 */
package com.atlassian.confluence.importexport.actions;

import com.atlassian.confluence.impl.security.SystemAdminOnly;
import com.atlassian.confluence.importexport.ImportExportException;
import com.atlassian.confluence.importexport.actions.AbstractFileRestoreAction;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.sal.api.websudo.WebSudoRequired;
import com.atlassian.user.User;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import com.atlassian.xwork.RequireSecurityToken;
import java.io.File;

@Deprecated
@WebSudoRequired
@SystemAdminOnly
public class RestoreLocalFileAction
extends AbstractFileRestoreAction {
    @Override
    public boolean isPermitted() {
        return this.permissionManager.hasPermission((User)this.getAuthenticatedUser(), Permission.ADMINISTER, PermissionManager.TARGET_SYSTEM);
    }

    @Override
    @PermittedMethods(value={HttpMethod.POST})
    @RequireSecurityToken(value=true)
    public String execute() throws Exception {
        return super.execute();
    }

    @Override
    protected File getRestoreFile() throws ImportExportException {
        return this.getRestoreFileFromFileSystem();
    }

    @Override
    protected boolean isDeleteWorkingFile() {
        return false;
    }

    public void setBuildIndexLocalFileRestore(boolean buildIndex) {
        super.setBuildIndex(buildIndex);
    }
}

