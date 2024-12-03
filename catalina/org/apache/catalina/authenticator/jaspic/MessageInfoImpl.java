/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.security.auth.message.MessageInfo
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.catalina.authenticator.jaspic;

import java.util.HashMap;
import java.util.Map;
import javax.security.auth.message.MessageInfo;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.tomcat.util.res.StringManager;

public class MessageInfoImpl
implements MessageInfo {
    protected static final StringManager sm = StringManager.getManager(MessageInfoImpl.class);
    public static final String IS_MANDATORY = "javax.security.auth.message.MessagePolicy.isMandatory";
    private final Map<String, Object> map = new HashMap<String, Object>();
    private HttpServletRequest request;
    private HttpServletResponse response;

    public MessageInfoImpl() {
    }

    public MessageInfoImpl(HttpServletRequest request, HttpServletResponse response, boolean authMandatory) {
        this.request = request;
        this.response = response;
        this.map.put(IS_MANDATORY, Boolean.toString(authMandatory));
    }

    public Map getMap() {
        return this.map;
    }

    public Object getRequestMessage() {
        return this.request;
    }

    public Object getResponseMessage() {
        return this.response;
    }

    public void setRequestMessage(Object request) {
        if (!(request instanceof HttpServletRequest)) {
            throw new IllegalArgumentException(sm.getString("authenticator.jaspic.badRequestType", new Object[]{request.getClass().getName()}));
        }
        this.request = (HttpServletRequest)request;
    }

    public void setResponseMessage(Object response) {
        if (!(response instanceof HttpServletResponse)) {
            throw new IllegalArgumentException(sm.getString("authenticator.jaspic.badResponseType", new Object[]{response.getClass().getName()}));
        }
        this.response = (HttpServletResponse)response;
    }
}

