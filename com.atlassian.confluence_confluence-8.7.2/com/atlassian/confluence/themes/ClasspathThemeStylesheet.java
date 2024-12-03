/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.themes;

import com.atlassian.confluence.themes.ThemeResource;

public class ClasspathThemeStylesheet
implements ThemeResource {
    private final String moduleKey;
    private final String name;
    private final String location;

    public ClasspathThemeStylesheet(String moduleKey, String name, String location) {
        this.moduleKey = moduleKey;
        this.name = name;
        this.location = location;
    }

    @Override
    public ThemeResource.Type getType() {
        return ThemeResource.Type.CSS;
    }

    @Override
    public String getLocation() {
        return this.location;
    }

    @Override
    public String getCompleteModuleKey() {
        return this.moduleKey;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public String toString() {
        return "[ name='" + this.name + "', location='classpath:" + this.location + "' ]";
    }

    @Override
    public boolean isIeOnly() {
        return false;
    }
}

