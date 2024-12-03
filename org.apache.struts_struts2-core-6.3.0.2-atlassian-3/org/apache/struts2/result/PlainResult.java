/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.Cookie
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.apache.logging.log4j.message.Message
 *  org.apache.logging.log4j.message.ParameterizedMessage
 */
package org.apache.struts2.result;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.Result;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.ParameterizedMessage;
import org.apache.struts2.StrutsException;
import org.apache.struts2.result.plain.HttpHeader;
import org.apache.struts2.result.plain.ResponseBuilder;

public interface PlainResult
extends Result {
    public static final Logger LOG = LogManager.getLogger(PlainResult.class);

    @Override
    default public void execute(ActionInvocation invocation) throws Exception {
        if (invocation == null) {
            throw new IllegalArgumentException("Invocation cannot be null!");
        }
        LOG.debug("Executing plain result");
        ResponseBuilder builder = new ResponseBuilder();
        this.write(builder);
        HttpServletResponse response = invocation.getInvocationContext().getServletResponse();
        if (response.isCommitted()) {
            if (this.ignoreCommitted()) {
                LOG.warn("Http response already committed, ignoring & skipping!");
                return;
            }
            throw new StrutsException("Http response already committed, cannot modify it!");
        }
        for (HttpHeader<String> httpHeader : builder.getStringHeaders()) {
            LOG.debug((Message)new ParameterizedMessage("A string header: {} = {}", (Object)httpHeader.getName(), (Object)httpHeader.getValue()));
            response.addHeader(httpHeader.getName(), httpHeader.getValue());
        }
        for (HttpHeader<Object> httpHeader : builder.getDateHeaders()) {
            LOG.debug((Message)new ParameterizedMessage("A date header: {} = {}", (Object)httpHeader.getName(), httpHeader.getValue()));
            response.addDateHeader(httpHeader.getName(), ((Long)httpHeader.getValue()).longValue());
        }
        for (HttpHeader<Object> httpHeader : builder.getIntHeaders()) {
            LOG.debug((Message)new ParameterizedMessage("An int header: {} = {}", (Object)httpHeader.getName(), httpHeader.getValue()));
            response.addIntHeader(httpHeader.getName(), ((Integer)httpHeader.getValue()).intValue());
        }
        for (Cookie cookie : builder.getCookies()) {
            LOG.debug((Message)new ParameterizedMessage("A cookie: {} = {}", (Object)cookie.getName(), (Object)cookie.getValue()));
            response.addCookie(cookie);
        }
        response.getWriter().write(builder.getBody());
        response.flushBuffer();
    }

    public void write(ResponseBuilder var1);

    default public boolean ignoreCommitted() {
        return false;
    }
}

