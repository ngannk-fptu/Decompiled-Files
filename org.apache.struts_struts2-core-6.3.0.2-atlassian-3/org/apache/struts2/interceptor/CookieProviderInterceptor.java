/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.Cookie
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package org.apache.struts2.interceptor;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import com.opensymphony.xwork2.interceptor.PreResultListener;
import java.util.Set;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.interceptor.CookieProvider;

public class CookieProviderInterceptor
extends AbstractInterceptor
implements PreResultListener {
    private static final Logger LOG = LogManager.getLogger(CookieProviderInterceptor.class);

    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        invocation.addPreResultListener(this);
        return invocation.invoke();
    }

    protected void addCookiesToResponse(CookieProvider action, HttpServletResponse response) {
        Set<Cookie> cookies = action.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Sending cookie [{}] with value [{}] for domain [{}]", (Object)cookie.getName(), (Object)cookie.getValue(), (Object)(cookie.getDomain() != null ? cookie.getDomain() : "no domain"));
                }
                response.addCookie(cookie);
            }
        }
    }

    @Override
    public void beforeResult(ActionInvocation invocation, String resultCode) {
        try {
            LOG.trace("beforeResult start");
            ActionContext ac = invocation.getInvocationContext();
            if (invocation.getAction() instanceof CookieProvider) {
                HttpServletResponse response = ac.getServletResponse();
                this.addCookiesToResponse((CookieProvider)invocation.getAction(), response);
            }
            LOG.trace("beforeResult end");
        }
        catch (Exception ex) {
            LOG.error("Unable to setup cookies", (Throwable)ex);
        }
    }
}

