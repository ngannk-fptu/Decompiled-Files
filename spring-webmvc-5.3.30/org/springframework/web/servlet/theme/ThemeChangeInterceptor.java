/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package org.springframework.web.servlet.theme;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ThemeResolver;
import org.springframework.web.servlet.support.RequestContextUtils;

public class ThemeChangeInterceptor
implements HandlerInterceptor {
    public static final String DEFAULT_PARAM_NAME = "theme";
    private String paramName = "theme";

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public String getParamName() {
        return this.paramName;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws ServletException {
        String newTheme = request.getParameter(this.paramName);
        if (newTheme != null) {
            ThemeResolver themeResolver = RequestContextUtils.getThemeResolver(request);
            if (themeResolver == null) {
                throw new IllegalStateException("No ThemeResolver found: not in a DispatcherServlet request?");
            }
            themeResolver.setThemeName(request, response, newTheme);
        }
        return true;
    }
}

