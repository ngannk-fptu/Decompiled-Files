/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.security.XsrfProtectionExcluded
 *  com.atlassian.config.ApplicationConfiguration
 *  com.atlassian.config.util.BootstrapUtils
 *  com.atlassian.sal.api.websudo.WebSudoRequired
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 */
package com.atlassian.confluence.setup.actions;

import com.atlassian.annotations.security.XsrfProtectionExcluded;
import com.atlassian.config.ApplicationConfiguration;
import com.atlassian.config.util.BootstrapUtils;
import com.atlassian.confluence.impl.security.SystemAdminOnly;
import com.atlassian.confluence.setup.actions.AbstractSetupAction;
import com.atlassian.sal.api.websudo.WebSudoRequired;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;

@WebSudoRequired
@SystemAdminOnly
public class SelectGlobalLocaleAction
extends AbstractSetupAction {
    private String globalDefaultLocale;

    @Override
    public String doDefault() throws Exception {
        return super.doDefault();
    }

    @PermittedMethods(value={HttpMethod.GET, HttpMethod.POST})
    @XsrfProtectionExcluded
    public String execute() throws Exception {
        ApplicationConfiguration applicationConfig = (ApplicationConfiguration)BootstrapUtils.getBootstrapContext().getBean("applicationConfig");
        applicationConfig.setProperty((Object)"confluence.setup.locale", (Object)this.globalDefaultLocale);
        applicationConfig.save();
        return "success";
    }

    public void setGlobalDefaultLocale(String globalDefaultLocale) {
        this.globalDefaultLocale = globalDefaultLocale;
    }

    public String getGlobalDefaultLocale() {
        if (this.globalDefaultLocale == null) {
            this.globalDefaultLocale = this.getGlobalSettings().getGlobalDefaultLocale();
        }
        return this.globalDefaultLocale;
    }
}

