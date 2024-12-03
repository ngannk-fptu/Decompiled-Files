/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.spring.container.ContainerManager
 *  com.atlassian.user.User
 *  com.atlassian.util.profiling.Ticker
 *  com.atlassian.util.profiling.Timers
 *  com.opensymphony.xwork2.Action
 *  com.opensymphony.xwork2.ActionInvocation
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.spaces.actions;

import com.atlassian.confluence.plugin.webresource.ConfluenceWebResourceManager;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.setup.struts.AbstractAwareInterceptor;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.spaces.actions.SpaceAware;
import com.atlassian.spring.container.ContainerManager;
import com.atlassian.user.User;
import com.atlassian.util.profiling.Ticker;
import com.atlassian.util.profiling.Timers;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpaceAwareInterceptor
extends AbstractAwareInterceptor {
    private static final Logger log = LoggerFactory.getLogger(SpaceAwareInterceptor.class);
    private PermissionManager permissionManager;
    private SpaceManager spaceManager;
    private ConfluenceWebResourceManager webResourceManager;

    @Override
    public String intercept(ActionInvocation actionInvocation) throws Exception {
        try (Ticker ignored = Timers.start((String)"SpaceAwareInterceptor.intercept()");){
            Action action = (Action)actionInvocation.getAction();
            if (action instanceof SpaceAware) {
                SpaceAware spaceAware = (SpaceAware)action;
                Space space = this.getSpace();
                if (log.isDebugEnabled()) {
                    log.debug("Set space on SpaceAware " + action.getClass().getName() + ": " + space);
                }
                if (space != null) {
                    spaceAware.setSpace(space);
                }
                if (spaceAware.isViewPermissionRequired() && (spaceAware.isSpaceRequired() || this.hasParameter("spaceKey") || this.hasParameter("key")) && !this.getPermissionManager().hasPermission((User)this.getUser(), Permission.VIEW, space)) {
                    String string = this.getUser() == null ? "notpermitted" : "notfound";
                    return string;
                }
                if (space != null) {
                    this.getWebResourceManager().putMetadata("space-key", space.getKey());
                    this.getWebResourceManager().putMetadata("space-name", space.getName());
                } else if (spaceAware.isSpaceRequired()) {
                    String string = "pagenotfound";
                    return string;
                }
            }
            String string = actionInvocation.invoke();
            return string;
        }
    }

    private Space getSpace() {
        if (this.hasParameter("spaceKey")) {
            return this.getSpaceFromKey(this.getParameter("spaceKey"));
        }
        if (this.hasParameter("key")) {
            return this.getSpaceFromKey(this.getParameter("key"));
        }
        return null;
    }

    private Space getSpaceFromKey(String key) {
        if (Space.isValidSpaceKey(key)) {
            return this.getSpaceManager().getSpace(key);
        }
        return null;
    }

    private SpaceManager getSpaceManager() {
        if (this.spaceManager == null) {
            this.spaceManager = (SpaceManager)ContainerManager.getComponent((String)"spaceManager");
        }
        return this.spaceManager;
    }

    private PermissionManager getPermissionManager() {
        if (this.permissionManager == null) {
            this.permissionManager = (PermissionManager)ContainerManager.getComponent((String)"permissionManager");
        }
        return this.permissionManager;
    }

    private ConfluenceWebResourceManager getWebResourceManager() {
        if (this.webResourceManager == null) {
            this.webResourceManager = (ConfluenceWebResourceManager)ContainerManager.getComponent((String)"webResourceManager");
        }
        return this.webResourceManager;
    }
}

