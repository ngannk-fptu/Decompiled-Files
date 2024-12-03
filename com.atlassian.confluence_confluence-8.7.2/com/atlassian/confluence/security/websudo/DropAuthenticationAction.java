/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.util.ajax.AjaxResponse
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  com.google.common.base.Preconditions
 *  org.apache.struts2.ServletActionContext
 */
package com.atlassian.confluence.security.websudo;

import com.atlassian.confluence.core.Beanable;
import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.event.events.internal.auth.SudoLogoutEvent;
import com.atlassian.confluence.security.websudo.WebSudoManager;
import com.atlassian.confluence.util.ajax.AjaxResponse;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import com.google.common.base.Preconditions;
import org.apache.struts2.ServletActionContext;

public class DropAuthenticationAction
extends ConfluenceActionSupport
implements Beanable {
    private AjaxResponse response;
    private WebSudoManager webSudoManager;
    private EventPublisher eventPublisher;

    @PermittedMethods(value={HttpMethod.GET})
    public String execute() throws Exception {
        this.webSudoManager.invalidateSession(ServletActionContext.getRequest(), ServletActionContext.getResponse());
        this.response = AjaxResponse.success((String)"Success");
        SudoLogoutEvent sudoLogoutEvent = new SudoLogoutEvent(this);
        this.eventPublisher.publish((Object)sudoLogoutEvent);
        return "success";
    }

    @Override
    public Object getBean() {
        return this.response;
    }

    public void setWebSudoManager(WebSudoManager webSudoManager) {
        this.webSudoManager = (WebSudoManager)Preconditions.checkNotNull((Object)webSudoManager);
    }

    public void setEventPublisher(EventPublisher eventPublisher) {
        this.eventPublisher = (EventPublisher)Preconditions.checkNotNull((Object)eventPublisher);
    }
}

