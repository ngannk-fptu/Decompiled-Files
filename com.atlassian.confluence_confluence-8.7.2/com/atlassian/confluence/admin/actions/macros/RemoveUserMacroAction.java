/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.websudo.WebSudoRequired
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.admin.actions.macros;

import com.atlassian.confluence.admin.actions.macros.UserMacroAction;
import com.atlassian.confluence.impl.security.SystemAdminOnly;
import com.atlassian.sal.api.websudo.WebSudoRequired;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import org.apache.commons.lang3.StringUtils;

@WebSudoRequired
@SystemAdminOnly
public class RemoveUserMacroAction
extends UserMacroAction {
    @Override
    public void validate() {
        super.validate();
        if (!StringUtils.isNotEmpty((CharSequence)this.macro)) {
            this.addActionError(this.getText("user.macro.name.empty"));
        } else if (!this.userMacroLibrary.hasMacro(this.macro)) {
            this.addActionError("user.macro.does.not.exist", this.macro);
        }
    }

    @PermittedMethods(value={HttpMethod.GET, HttpMethod.POST})
    public String doRemove() {
        this.userMacroLibrary.removeMacro(this.macro);
        return "success";
    }
}

