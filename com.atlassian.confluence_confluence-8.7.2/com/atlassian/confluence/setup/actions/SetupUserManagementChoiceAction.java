/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.websudo.WebSudoRequired
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  com.atlassian.xwork.RequireSecurityToken
 */
package com.atlassian.confluence.setup.actions;

import com.atlassian.confluence.impl.security.SystemAdminOnly;
import com.atlassian.confluence.setup.actions.AbstractSetupAction;
import com.atlassian.sal.api.websudo.WebSudoRequired;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import com.atlassian.xwork.RequireSecurityToken;

@WebSudoRequired
@SystemAdminOnly
public class SetupUserManagementChoiceAction
extends AbstractSetupAction {
    private String userManagementChoice;

    @PermittedMethods(value={HttpMethod.POST})
    @RequireSecurityToken(value=true)
    public String execute() throws Exception {
        if ("jaacs".equals(this.userManagementChoice)) {
            return "jaacs-user";
        }
        return "internal-user";
    }

    public String getUserManagementChoice() {
        return this.userManagementChoice;
    }

    public void setUserManagementChoice(String userManagementChoice) {
        this.userManagementChoice = userManagementChoice;
    }
}

