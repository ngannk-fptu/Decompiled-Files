/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.event.Evented
 *  com.atlassian.confluence.spaces.actions.AbstractSpaceAdminAction
 *  com.atlassian.core.filters.ServletContextThreadLocal
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.applinks.ui.confluence;

import com.atlassian.applinks.analytics.EntityLinksAdminViewEvent;
import com.atlassian.applinks.ui.velocity.ListEntityLinksContext;
import com.atlassian.applinks.ui.velocity.VelocityContextFactory;
import com.atlassian.confluence.event.Evented;
import com.atlassian.confluence.spaces.actions.AbstractSpaceAdminAction;
import com.atlassian.core.filters.ServletContextThreadLocal;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import javax.servlet.http.HttpServletRequest;

public class ListEntityLinksAction
extends AbstractSpaceAdminAction
implements Evented<EntityLinksAdminViewEvent> {
    private final VelocityContextFactory velocityContextFactory;
    private ListEntityLinksContext context;
    private String typeId;

    public ListEntityLinksAction(VelocityContextFactory velocityContextFactory) {
        this.velocityContextFactory = velocityContextFactory;
    }

    @PermittedMethods(value={HttpMethod.GET})
    public String execute() {
        return "success";
    }

    public ListEntityLinksContext getApplinksContext() {
        if (this.context == null) {
            HttpServletRequest request = ServletContextThreadLocal.getRequest();
            this.context = this.velocityContextFactory.buildListEntityLinksContext(request, this.typeId, this.key);
        }
        return this.context;
    }

    public EntityLinksAdminViewEvent getEventToPublish(String status) {
        return new EntityLinksAdminViewEvent(this.typeId, this.key);
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }
}

