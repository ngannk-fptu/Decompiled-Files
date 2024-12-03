/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletRequest
 */
package com.atlassian.confluence.themes;

import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.themes.Theme;
import javax.servlet.ServletRequest;

public class ThemeContext {
    public static final String ATTRIBUTE_KEY = "confluence.themecontext";
    private final Space currentSpace;
    private final Theme spaceTheme;
    private final Theme globalTheme;

    public static void set(ServletRequest request, Space currentSpace, Theme currentTheme, Theme globalTheme) {
        request.setAttribute(ATTRIBUTE_KEY, (Object)new ThemeContext(currentSpace, currentTheme, globalTheme));
    }

    public static ThemeContext get(ServletRequest request) {
        ThemeContext context = (ThemeContext)request.getAttribute(ATTRIBUTE_KEY);
        return context == null ? new ThemeContext(null, null, null) : context;
    }

    public static boolean hasThemeContext(ServletRequest request) {
        return request.getAttribute(ATTRIBUTE_KEY) != null;
    }

    public ThemeContext(Space currentSpace, Theme spaceTheme, Theme globalTheme) {
        this.currentSpace = currentSpace;
        this.spaceTheme = spaceTheme;
        this.globalTheme = globalTheme;
    }

    public Space getSpace() {
        return this.currentSpace;
    }

    public boolean hasSpaceTheme() {
        return this.spaceTheme != null;
    }

    public Theme getSpaceTheme() {
        return this.spaceTheme;
    }

    public boolean hasGlobalTheme() {
        return this.globalTheme != null;
    }

    public Theme getGlobalTheme() {
        return this.globalTheme;
    }

    public String getSpaceKey() {
        return this.currentSpace == null ? null : this.currentSpace.getKey();
    }

    public Theme getAppliedTheme() {
        return this.spaceTheme == null ? this.globalTheme : this.spaceTheme;
    }
}

