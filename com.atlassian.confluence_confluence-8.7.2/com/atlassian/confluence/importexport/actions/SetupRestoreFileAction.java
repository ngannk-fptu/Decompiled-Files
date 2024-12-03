/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.websudo.WebSudoRequired
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  com.atlassian.xwork.RequireSecurityToken
 */
package com.atlassian.confluence.importexport.actions;

import com.atlassian.confluence.impl.security.SystemAdminOnly;
import com.atlassian.confluence.importexport.actions.RestoreLocalFileAction;
import com.atlassian.confluence.importexport.actions.SetupRestoreHelper;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.sal.api.websudo.WebSudoRequired;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import com.atlassian.xwork.RequireSecurityToken;

@Deprecated
@WebSudoRequired
@SystemAdminOnly
public class SetupRestoreFileAction
extends RestoreLocalFileAction {
    @Override
    public boolean isPermitted() {
        return !GeneralUtil.isSetupComplete();
    }

    @Override
    @PermittedMethods(value={HttpMethod.POST})
    @RequireSecurityToken(value=true)
    public String execute() throws Exception {
        SetupRestoreHelper.prepareForRestore();
        String result = super.execute();
        if ("success".equals(result)) {
            SetupRestoreHelper.postRestoreSteps();
        }
        return result;
    }
}

