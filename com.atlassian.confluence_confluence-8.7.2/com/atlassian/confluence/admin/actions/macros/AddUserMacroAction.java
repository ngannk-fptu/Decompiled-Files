/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.websudo.WebSudoRequired
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.admin.actions.macros;

import com.atlassian.confluence.admin.actions.macros.UserMacroAction;
import com.atlassian.confluence.impl.security.SystemAdminOnly;
import com.atlassian.confluence.util.HTMLPairType;
import com.atlassian.sal.api.websudo.WebSudoRequired;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

@WebSudoRequired
@SystemAdminOnly
public class AddUserMacroAction
extends UserMacroAction {
    private boolean start = true;

    @Override
    public void validate() {
        this.start = false;
        super.validate();
        this.validateNewMacroName(this.userMacro.getName());
        this.validateMacroForm();
        if (StringUtils.isNotBlank((CharSequence)this.userMacro.getTemplate())) {
            this.userMacro.setTemplate(this.userMacro.getTemplate());
        }
    }

    public String doAdd() {
        this.addUpdateMacro(this.userMacro);
        return "success";
    }

    public String getMode() {
        return "add";
    }

    public List<HTMLPairType> getOutputTypes() {
        return Collections.emptyList();
    }

    public String getTitleKey() {
        return "title.user.macros.add";
    }

    public String getSubmitKey() {
        return "add.name";
    }

    public boolean getStart() {
        return this.start;
    }
}

