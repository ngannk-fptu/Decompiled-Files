/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.service.accessmode.ReadOnlyAccessAllowed
 *  com.atlassian.core.exception.InfrastructureException
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.user.actions;

import com.atlassian.confluence.api.service.accessmode.ReadOnlyAccessAllowed;
import com.atlassian.confluence.security.ExternalUserManagementAware;
import com.atlassian.confluence.user.actions.AbstractUsersAction;
import com.atlassian.core.exception.InfrastructureException;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ReadOnlyAccessAllowed
public class DeactivateUserAction
extends AbstractUsersAction
implements ExternalUserManagementAware {
    private static final Logger log = LoggerFactory.getLogger(DeactivateUserAction.class);

    public String doDeactivate() {
        try {
            this.userAccessor.deactivateUser(this.getUser());
        }
        catch (InfrastructureException e) {
            log.error("Failed to deactivate user " + this.getUser(), (Throwable)e);
        }
        return this.userAccessor.isDeactivated(this.getUsername()) ? "success" : "error";
    }

    public String doReactivate() {
        try {
            this.userAccessor.reactivateUser(this.getUser());
        }
        catch (InfrastructureException e) {
            log.error("Failed to reactivate user " + this.getUser(), (Throwable)e);
        }
        return this.userAccessor.isDeactivated(this.getUsername()) ? "error" : "success";
    }

    public List<String> getUserDeactivationInformation() {
        return Arrays.asList(this.getText("user.deactivate.action.consequence.disable.ability.to.login"), this.getText("user.deactivate.action.consequence.user.nocontrib.to.license.count"));
    }

    public List<String> getUserReactivationInformation() {
        return Arrays.asList(this.getText("user.reactivate.action.consequence.enable.ability.to.login"), this.getText("user.reactivate.action.consequence.user.contrib.to.license.count"));
    }
}

