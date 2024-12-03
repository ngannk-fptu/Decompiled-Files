/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.confluence.util.message;

import com.atlassian.confluence.util.message.AbstractMessageManager;
import com.atlassian.confluence.util.message.Message;
import com.atlassian.confluence.web.context.HttpContext;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

public class RequestMessageManager
extends AbstractMessageManager {
    private final HttpContext httpContext;

    public RequestMessageManager(HttpContext httpContext) {
        this.httpContext = httpContext;
    }

    @Override
    protected Map<String, Message> retrieveEntries() {
        return this.httpContext.getRequest() == null ? null : (Map)this.httpContext.getRequest().getAttribute("confluence.messages");
    }

    @Override
    protected void saveEntries(Map<String, Message> messages) {
        HttpServletRequest req = this.httpContext.getRequest();
        if (req != null) {
            req.setAttribute("confluence.messages", messages);
        }
    }
}

