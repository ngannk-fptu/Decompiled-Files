/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.websudo.WebSudoRequired
 */
package com.atlassian.confluence.admin.actions.trust;

import com.atlassian.confluence.admin.actions.trust.AbstractTrustedApplicationAction;
import com.atlassian.confluence.impl.security.AdminOnly;
import com.atlassian.confluence.security.trust.ConfluenceTrustedApplication;
import com.atlassian.sal.api.websudo.WebSudoRequired;

@WebSudoRequired
@AdminOnly
public class RemoveTrustedApplicationAction
extends AbstractTrustedApplicationAction {
    private long id;

    public String execute() throws Exception {
        ConfluenceTrustedApplication application = this.trustedApplicationsManager.getTrustedApplication(this.id);
        if (application != null) {
            this.trustedApplicationsManager.deleteTrustedApplication(application);
        }
        return super.execute();
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }
}

