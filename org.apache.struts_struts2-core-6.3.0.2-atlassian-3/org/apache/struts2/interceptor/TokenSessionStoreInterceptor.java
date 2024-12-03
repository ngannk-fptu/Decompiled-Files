/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  javax.servlet.http.HttpSession
 */
package org.apache.struts2.interceptor;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.Result;
import com.opensymphony.xwork2.util.ValueStack;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.dispatcher.HttpParameters;
import org.apache.struts2.interceptor.TokenInterceptor;
import org.apache.struts2.util.InvocationSessionStore;
import org.apache.struts2.util.TokenHelper;

public class TokenSessionStoreInterceptor
extends TokenInterceptor {
    private static final long serialVersionUID = -9032347965469098195L;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected String handleToken(ActionInvocation invocation) throws Exception {
        HttpSession session = ServletActionContext.getRequest().getSession(true);
        String string = session.getId().intern();
        synchronized (string) {
            if (!TokenHelper.validToken()) {
                return this.handleInvalidToken(invocation);
            }
            return this.handleValidToken(invocation);
        }
    }

    @Override
    protected String handleInvalidToken(ActionInvocation invocation) throws Exception {
        ActionContext ac = invocation.getInvocationContext();
        HttpServletRequest request = ac.getServletRequest();
        HttpServletResponse response = ac.getServletResponse();
        String tokenName = TokenHelper.getTokenName();
        String token = TokenHelper.getToken(tokenName);
        if (tokenName != null && token != null) {
            HttpParameters params = ac.getParameters();
            params.remove(tokenName);
            params.remove("struts.token.name");
            String sessionTokenName = TokenHelper.buildTokenSessionAttributeName(tokenName);
            ActionInvocation savedInvocation = InvocationSessionStore.loadInvocation(sessionTokenName, token);
            if (savedInvocation != null) {
                ValueStack stack = savedInvocation.getStack();
                request.setAttribute("struts.valueStack", (Object)stack);
                ActionContext savedContext = savedInvocation.getInvocationContext();
                savedContext.getContextMap().put("com.opensymphony.xwork2.dispatcher.HttpServletRequest", request);
                savedContext.getContextMap().put("com.opensymphony.xwork2.dispatcher.HttpServletResponse", response);
                Result result = savedInvocation.getResult();
                if (result != null && savedInvocation.getProxy().getExecuteResult()) {
                    result.execute(savedInvocation);
                }
                invocation.getProxy().setExecuteResult(false);
                return savedInvocation.getResultCode();
            }
        }
        return "invalid.token";
    }

    @Override
    protected String handleValidToken(ActionInvocation invocation) throws Exception {
        String key = TokenHelper.getTokenName();
        String token = TokenHelper.getToken(key);
        String sessionTokenName = TokenHelper.buildTokenSessionAttributeName(key);
        InvocationSessionStore.storeInvocation(sessionTokenName, token, invocation);
        return invocation.invoke();
    }
}

