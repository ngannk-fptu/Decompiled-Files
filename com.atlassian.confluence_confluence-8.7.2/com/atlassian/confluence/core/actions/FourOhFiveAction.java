/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.security.XsrfProtectionExcluded
 *  com.atlassian.spring.container.ContainerManager
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  org.apache.struts2.ServletActionContext
 */
package com.atlassian.confluence.core.actions;

import com.atlassian.annotations.security.XsrfProtectionExcluded;
import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.spring.container.ContainerManager;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import org.apache.struts2.ServletActionContext;

public class FourOhFiveAction
extends ConfluenceActionSupport {
    @PermittedMethods(value={HttpMethod.ANY_METHOD})
    @XsrfProtectionExcluded
    public String execute() throws Exception {
        ServletActionContext.getResponse().setStatus(405);
        if (!this.isSetupComplete()) {
            return "setup-success";
        }
        return "error";
    }

    @Override
    public boolean isPermitted() {
        return true;
    }

    private boolean isSetupComplete() {
        return ContainerManager.isContainerSetup() && this.getBootstrapStatusProvider().isSetupComplete();
    }
}

