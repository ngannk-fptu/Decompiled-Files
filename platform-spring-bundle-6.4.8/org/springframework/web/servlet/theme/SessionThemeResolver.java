/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package org.springframework.web.servlet.theme;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.theme.AbstractThemeResolver;
import org.springframework.web.util.WebUtils;

public class SessionThemeResolver
extends AbstractThemeResolver {
    public static final String THEME_SESSION_ATTRIBUTE_NAME = SessionThemeResolver.class.getName() + ".THEME";

    @Override
    public String resolveThemeName(HttpServletRequest request) {
        String themeName = (String)WebUtils.getSessionAttribute(request, THEME_SESSION_ATTRIBUTE_NAME);
        return themeName != null ? themeName : this.getDefaultThemeName();
    }

    @Override
    public void setThemeName(HttpServletRequest request, @Nullable HttpServletResponse response, @Nullable String themeName) {
        WebUtils.setSessionAttribute(request, THEME_SESSION_ATTRIBUTE_NAME, StringUtils.hasText(themeName) ? themeName : null);
    }
}

