/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.tiles.locale.impl.DefaultLocaleResolver
 *  org.apache.tiles.request.Request
 *  org.apache.tiles.request.servlet.NotAServletEnvironmentException
 *  org.apache.tiles.request.servlet.ServletUtil
 */
package org.springframework.web.servlet.view.tiles3;

import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import org.apache.tiles.locale.impl.DefaultLocaleResolver;
import org.apache.tiles.request.Request;
import org.apache.tiles.request.servlet.NotAServletEnvironmentException;
import org.apache.tiles.request.servlet.ServletUtil;
import org.springframework.web.servlet.support.RequestContextUtils;

public class SpringLocaleResolver
extends DefaultLocaleResolver {
    public Locale resolveLocale(Request request) {
        try {
            HttpServletRequest servletRequest = ServletUtil.getServletRequest((Request)request).getRequest();
            if (servletRequest != null) {
                return RequestContextUtils.getLocale(servletRequest);
            }
        }
        catch (NotAServletEnvironmentException notAServletEnvironmentException) {
            // empty catch block
        }
        return super.resolveLocale(request);
    }
}

