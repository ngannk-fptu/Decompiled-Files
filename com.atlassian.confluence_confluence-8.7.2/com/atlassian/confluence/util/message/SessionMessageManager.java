/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpSession
 */
package com.atlassian.confluence.util.message;

import com.atlassian.confluence.util.message.AbstractMessageManager;
import com.atlassian.confluence.util.message.Message;
import com.atlassian.confluence.web.context.HttpContext;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class SessionMessageManager
extends AbstractMessageManager {
    private final HttpContext httpContext;

    public SessionMessageManager(HttpContext httpContext) {
        this.httpContext = httpContext;
    }

    @Override
    protected Map<String, Message> retrieveEntries() {
        HttpSession session = this.getSession();
        return session == null ? null : (Map)session.getAttribute("confluence.messages");
    }

    private HttpSession getSession() {
        HttpServletRequest req = this.httpContext.getRequest();
        return req == null ? null : req.getSession(false);
    }

    @Override
    protected void saveEntries(Map<String, Message> messages) {
        HttpSession session = this.getSession();
        if (session != null) {
            session.setAttribute("confluence.messages", messages);
        }
    }
}

