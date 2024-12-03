/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  org.apache.struts2.ServletActionContext
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.setup.actions;

import com.atlassian.confluence.internal.health.JohnsonEventPredicates;
import com.atlassian.confluence.internal.health.JohnsonEventType;
import com.atlassian.confluence.setup.actions.AbstractSetupAction;
import com.atlassian.confluence.setup.johnson.JohnsonUtils;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SelectSetupStepAction
extends AbstractSetupAction {
    private static final Logger log = LoggerFactory.getLogger(SelectSetupStepAction.class);
    public static final String RESULT_PRECONDITIONS_FAILED = "checklist";
    private String currentSetupActionName;

    @PermittedMethods(value={HttpMethod.GET})
    public String execute() {
        if (JohnsonUtils.eventExists(JohnsonEventPredicates.hasType(JohnsonEventType.SETUP))) {
            return RESULT_PRECONDITIONS_FAILED;
        }
        String currentStep = this.getSetupPersister().getCurrentStep();
        this.currentSetupActionName = "complete".equals(currentStep) ? "/setup/finishsetup.action" : "/setup/" + currentStep + ".action";
        log.debug("current setup step is {}", (Object)this.currentSetupActionName);
        return "success";
    }

    public String getCurrentStep() {
        return this.currentSetupActionName;
    }

    public String getServerName() {
        return ServletActionContext.getServletContext().getServerInfo();
    }

    @Override
    public boolean isPermitted() {
        return true;
    }

    @Deprecated
    public boolean isJdk14() {
        return true;
    }

    @Deprecated
    public boolean isServlet23() {
        return true;
    }

    @Deprecated
    public boolean isConfluenceHomeOk() {
        return this.checkConfluenceHomeOk();
    }

    @Deprecated
    public boolean checkJdk14() {
        return true;
    }

    @Deprecated
    public boolean checkServlet23() {
        return true;
    }

    @Deprecated
    public boolean checkConfluenceHomeOk() {
        return this.bootstrapConfigurer().isApplicationHomeValid();
    }

    @Deprecated
    public String getJdkName() {
        return System.getProperty("java.vendor") + " - " + System.getProperty("java.version");
    }

    @Deprecated
    public boolean isEverythingOk() {
        return this.isJdk14() && this.isServlet23() && this.isConfluenceHomeOk();
    }
}

