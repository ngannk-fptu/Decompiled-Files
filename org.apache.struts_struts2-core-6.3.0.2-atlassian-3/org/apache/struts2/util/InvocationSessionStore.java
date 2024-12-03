/*
 * Decompiled with CFR 0.152.
 */
package org.apache.struts2.util;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class InvocationSessionStore {
    private static final String INVOCATION_MAP_KEY = "org.apache.struts2.util.InvocationSessionStore.invocationMap";

    private InvocationSessionStore() {
    }

    public static ActionInvocation loadInvocation(String key, String token) {
        InvocationContext invocationContext = (InvocationContext)InvocationSessionStore.getInvocationMap().get(key);
        if (invocationContext == null || !invocationContext.token.equals(token)) {
            return null;
        }
        ActionInvocation savedInvocation = invocationContext.invocation;
        if (savedInvocation != null) {
            ActionContext previousActionContext = ActionContext.getContext();
            savedInvocation.getInvocationContext().withPageContext(previousActionContext.getPageContext()).withValueStack(savedInvocation.getStack()).bind();
        }
        return savedInvocation;
    }

    public static void storeInvocation(String key, String token, ActionInvocation invocation) {
        InvocationContext invocationContext = new InvocationContext(invocation, token);
        Map<String, Object> invocationMap = InvocationSessionStore.getInvocationMap();
        invocationMap.put(key, invocationContext);
        InvocationSessionStore.setInvocationMap(invocationMap);
    }

    static void setInvocationMap(Map<String, Object> invocationMap) {
        Map<String, Object> session = ActionContext.getContext().getSession();
        if (session == null) {
            throw new IllegalStateException("Unable to access the session.");
        }
        session.put(INVOCATION_MAP_KEY, invocationMap);
    }

    static Map<String, Object> getInvocationMap() {
        Map<String, Object> session = ActionContext.getContext().getSession();
        if (session == null) {
            throw new IllegalStateException("Unable to access the session.");
        }
        HashMap<String, Object> invocationMap = (HashMap<String, Object>)session.get(INVOCATION_MAP_KEY);
        if (invocationMap == null) {
            invocationMap = new HashMap<String, Object>();
            InvocationSessionStore.setInvocationMap(invocationMap);
        }
        return invocationMap;
    }

    private static class InvocationContext
    implements Serializable {
        private static final long serialVersionUID = -286697666275777888L;
        transient ActionInvocation invocation;
        String token;

        public InvocationContext(ActionInvocation invocation, String token) {
            this.invocation = invocation;
            this.token = token;
        }
    }
}

