/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletRequestEvent
 *  javax.servlet.ServletRequestListener
 *  javax.servlet.http.HttpServletRequest
 *  org.springframework.context.i18n.LocaleContextHolder
 */
package org.springframework.web.context.request;

import java.util.Locale;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.http.HttpServletRequest;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class RequestContextListener
implements ServletRequestListener {
    private static final String REQUEST_ATTRIBUTES_ATTRIBUTE = RequestContextListener.class.getName() + ".REQUEST_ATTRIBUTES";

    public void requestInitialized(ServletRequestEvent requestEvent) {
        if (!(requestEvent.getServletRequest() instanceof HttpServletRequest)) {
            throw new IllegalArgumentException("Request is not an HttpServletRequest: " + requestEvent.getServletRequest());
        }
        HttpServletRequest request = (HttpServletRequest)requestEvent.getServletRequest();
        ServletRequestAttributes attributes = new ServletRequestAttributes(request);
        request.setAttribute(REQUEST_ATTRIBUTES_ATTRIBUTE, (Object)attributes);
        LocaleContextHolder.setLocale((Locale)request.getLocale());
        RequestContextHolder.setRequestAttributes(attributes);
    }

    public void requestDestroyed(ServletRequestEvent requestEvent) {
        RequestAttributes threadAttributes;
        ServletRequestAttributes attributes = null;
        Object reqAttr = requestEvent.getServletRequest().getAttribute(REQUEST_ATTRIBUTES_ATTRIBUTE);
        if (reqAttr instanceof ServletRequestAttributes) {
            attributes = (ServletRequestAttributes)reqAttr;
        }
        if ((threadAttributes = RequestContextHolder.getRequestAttributes()) != null) {
            LocaleContextHolder.resetLocaleContext();
            RequestContextHolder.resetRequestAttributes();
            if (attributes == null && threadAttributes instanceof ServletRequestAttributes) {
                attributes = (ServletRequestAttributes)threadAttributes;
            }
        }
        if (attributes != null) {
            attributes.requestCompleted();
        }
    }
}

