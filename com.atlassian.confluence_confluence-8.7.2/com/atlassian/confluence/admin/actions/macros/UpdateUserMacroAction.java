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
public class UpdateUserMacroAction
extends UserMacroAction {
    private String originalName;
    private List<HTMLPairType> legacyOutputTypes;

    public String getOriginalName() {
        return this.originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    @Override
    public void validate() {
        super.validate();
        String name = this.userMacro.getName();
        if (!StringUtils.equals((CharSequence)name, (CharSequence)this.originalName)) {
            this.validateNewMacroName(name);
        } else if (StringUtils.isNotEmpty((CharSequence)this.originalName) && this.hasNameChanged()) {
            if (this.userMacroLibrary.hasMacro(name)) {
                this.addFieldError("userMacro.name", "user.macro.already.exists", new Object[]{name});
            } else if (this.macroManager.getEnabledMacro(name) != null) {
                this.addFieldError("userMacro.name", "system.macro.already.exists", new Object[]{name});
            }
        }
        if (!"wiki".equals(this.userMacro.getOutputType()) && StringUtils.isNotBlank((CharSequence)this.userMacro.getTemplate())) {
            this.userMacro.setTemplate(this.userMacro.getTemplate());
        }
        this.validateMacroForm();
    }

    @Override
    public String doDefault() throws Exception {
        if (StringUtils.isNotEmpty((CharSequence)this.macro)) {
            this.originalName = this.macro;
        }
        return super.doDefault();
    }

    public String doUpdate() {
        if (StringUtils.isNotEmpty((CharSequence)this.originalName)) {
            this.userMacroLibrary.removeMacro(this.originalName);
        }
        this.addUpdateMacro(this.userMacro);
        return "success";
    }

    public List<HTMLPairType> getOutputTypes() {
        if (this.userMacro != null && "wiki".equals(this.userMacro.getOutputType())) {
            if (this.legacyOutputTypes == null) {
                this.legacyOutputTypes = List.of(new HTMLPairType("html", this.getText("user.macro.output.type.html")), new HTMLPairType("wiki", this.getText("user.macro.output.type.wiki")));
            }
            return this.legacyOutputTypes;
        }
        return Collections.emptyList();
    }

    public String getMode() {
        return "update";
    }

    private boolean hasNameChanged() {
        return !this.originalName.equals(this.userMacro.getName());
    }

    public String getSubmitKey() {
        return "update.name";
    }
}

