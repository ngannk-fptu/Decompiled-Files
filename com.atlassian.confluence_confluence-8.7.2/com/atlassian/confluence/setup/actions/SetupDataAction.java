/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.websudo.WebSudoRequired
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  com.atlassian.xwork.RequireSecurityToken
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.setup.actions;

import com.atlassian.confluence.impl.security.SystemAdminOnly;
import com.atlassian.confluence.setup.SetupLocks;
import com.atlassian.confluence.setup.actions.AbstractSetupAction;
import com.atlassian.confluence.setup.actions.SetupDemoContentAction;
import com.atlassian.confluence.util.HTMLPairType;
import com.atlassian.sal.api.websudo.WebSudoRequired;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import com.atlassian.xwork.RequireSecurityToken;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebSudoRequired
@SystemAdminOnly
public class SetupDataAction
extends AbstractSetupAction {
    private static final Logger log = LoggerFactory.getLogger(SetupDataAction.class);
    public static final String DEMO_ERROR = "demoerror";
    private List<HTMLPairType> contentChoiceList;
    private String contentChoice = "demo";
    private SetupLocks setupLocks;

    @PermittedMethods(value={HttpMethod.POST})
    @RequireSecurityToken(value=true)
    public String execute() throws Exception {
        if (!this.setupLocks.compareAndSet(SetupLocks.Lock.CURRENTLY_POPULATING_DATA, false, true)) {
            return DEMO_ERROR;
        }
        if ("blank".equals(this.contentChoice)) {
            this.getSetupPersister().progessSetupStep();
            return "blank";
        }
        if ("demo".equals(this.contentChoice)) {
            log.info("Importing demo site");
            SetupDemoContentAction demoContentAction = new SetupDemoContentAction();
            demoContentAction.execute();
            if (!demoContentAction.getActionErrors().isEmpty()) {
                log.error("Demo site import failed: {}", (Object)demoContentAction.getActionErrors());
                this.getActionErrors().addAll(demoContentAction.getActionErrors());
                return DEMO_ERROR;
            }
            log.info("Demo site import succeeded");
            return "demo";
        }
        if ("import".equals(this.contentChoice)) {
            return "import";
        }
        this.setupLocks.set(SetupLocks.Lock.CURRENTLY_POPULATING_DATA, false);
        return "input";
    }

    public List<HTMLPairType> getContentChoiceList() {
        this.contentChoiceList = new ArrayList<HTMLPairType>();
        this.contentChoiceList.add(new HTMLPairType(this.getText("demo.description"), "demo"));
        this.contentChoiceList.add(new HTMLPairType(this.getText("blank.description"), "blank"));
        this.contentChoiceList.add(new HTMLPairType(this.getText("import.description"), "import"));
        return this.contentChoiceList;
    }

    public String getContentChoice() {
        return this.contentChoice;
    }

    public void setContentChoice(String contentChoice) {
        this.contentChoice = contentChoice;
    }

    public void setSetupLocks(SetupLocks setupLocks) {
        this.setupLocks = setupLocks;
    }

    public Boolean getShowSetupRestore() {
        return Boolean.getBoolean("confluence.show.setup.restore");
    }
}

