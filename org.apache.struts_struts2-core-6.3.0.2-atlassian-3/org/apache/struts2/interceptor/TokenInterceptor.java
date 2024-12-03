/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpSession
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package org.apache.struts2.interceptor;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.TextProvider;
import com.opensymphony.xwork2.TextProviderFactory;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.interceptor.MethodFilterInterceptor;
import com.opensymphony.xwork2.interceptor.ValidationAware;
import javax.servlet.http.HttpSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.util.TokenHelper;

public class TokenInterceptor
extends MethodFilterInterceptor {
    private static final Logger LOG = LogManager.getLogger(TokenInterceptor.class);
    public static final String INVALID_TOKEN_CODE = "invalid.token";
    private static final String INVALID_TOKEN_MESSAGE_KEY = "struts.messages.invalid.token";
    private static final String DEFAULT_ERROR_MESSAGE = "The form has already been processed or no token was supplied, please try again.";
    private TextProvider textProvider;

    @Inject
    public void setTextProviderFactory(TextProviderFactory textProviderFactory) {
        this.textProvider = textProviderFactory.createInstance(this.getClass());
    }

    @Override
    protected String doIntercept(ActionInvocation invocation) throws Exception {
        LOG.debug("Intercepting invocation to check for valid transaction token.");
        return this.handleToken(invocation);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected String handleToken(ActionInvocation invocation) throws Exception {
        HttpSession session = ServletActionContext.getRequest().getSession(true);
        String string = session.getId().intern();
        synchronized (string) {
            if (!TokenHelper.validToken()) {
                return this.handleInvalidToken(invocation);
            }
        }
        return this.handleValidToken(invocation);
    }

    protected String handleInvalidToken(ActionInvocation invocation) throws Exception {
        Object action = invocation.getAction();
        String errorMessage = this.getErrorMessage(invocation);
        if (action instanceof ValidationAware) {
            ((ValidationAware)action).addActionError(errorMessage);
        } else {
            LOG.warn(errorMessage);
        }
        return INVALID_TOKEN_CODE;
    }

    protected String getErrorMessage(ActionInvocation invocation) {
        Object action = invocation.getAction();
        if (action instanceof TextProvider) {
            return ((TextProvider)action).getText(INVALID_TOKEN_MESSAGE_KEY, DEFAULT_ERROR_MESSAGE);
        }
        return this.textProvider.getText(INVALID_TOKEN_MESSAGE_KEY, DEFAULT_ERROR_MESSAGE);
    }

    protected String handleValidToken(ActionInvocation invocation) throws Exception {
        return invocation.invoke();
    }
}

