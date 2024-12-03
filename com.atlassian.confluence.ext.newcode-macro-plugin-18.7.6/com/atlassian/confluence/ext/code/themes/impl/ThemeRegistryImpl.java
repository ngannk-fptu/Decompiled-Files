/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.PostConstruct
 *  org.apache.commons.collections.map.CaseInsensitiveMap
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.ext.code.themes.impl;

import com.atlassian.confluence.ext.code.descriptor.DescriptorFacade;
import com.atlassian.confluence.ext.code.descriptor.ThemeDefinition;
import com.atlassian.confluence.ext.code.themes.DuplicateThemeException;
import com.atlassian.confluence.ext.code.themes.Theme;
import com.atlassian.confluence.ext.code.themes.ThemeRegistry;
import com.atlassian.confluence.ext.code.themes.UnknownThemeException;
import com.atlassian.confluence.ext.code.themes.impl.BuiltinTheme;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import javax.annotation.PostConstruct;
import org.apache.commons.collections.map.CaseInsensitiveMap;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public final class ThemeRegistryImpl
implements ThemeRegistry,
InitializingBean {
    private CaseInsensitiveMap themes = new CaseInsensitiveMap();
    private DescriptorFacade descriptorFacade = null;

    @Autowired
    public ThemeRegistryImpl(DescriptorFacade descriptorFacade) {
        this.descriptorFacade = descriptorFacade;
    }

    @Override
    public Collection<Theme> listThemes() throws Exception {
        HashSet<Theme> result = new HashSet<Theme>();
        result.addAll(this.themes.values());
        return result;
    }

    void registerTheme(Theme theme) throws Exception {
        if (this.themes.get((Object)theme.getName()) != null) {
            throw new DuplicateThemeException("newcode.theme.register.duplicate.name", theme.getName());
        }
        this.themes.put((Object)theme.getName(), (Object)theme);
    }

    @Override
    public boolean isThemeRegistered(String theme) {
        return this.themes.containsKey((Object)theme);
    }

    @Override
    public String getWebResourceForTheme(String name) throws Exception {
        Theme theme = (Theme)this.themes.get((Object)name);
        if (theme == null) {
            throw new UnknownThemeException(name);
        }
        return theme.getWebResource();
    }

    @Override
    public Map<String, String> getThemeLookAndFeel(String name) throws Exception {
        Theme theme = (Theme)this.themes.get((Object)name);
        if (theme == null) {
            throw new UnknownThemeException(name);
        }
        return theme.getDefaultLayout();
    }

    @PostConstruct
    public void registerDefaultThemes() throws Exception {
        ThemeDefinition[] builtins;
        for (ThemeDefinition themeDef : builtins = this.descriptorFacade.listBuiltinThemes()) {
            String location = themeDef.getLocation();
            int start = "sh/styles/shTheme".length();
            int end = location.length() - ".css".length();
            String name = location.substring(start, end);
            BuiltinTheme theme = new BuiltinTheme(name, location, themeDef.getPanelLookAndFeel());
            theme.setWebResource(themeDef.getWebResourceId());
            this.registerTheme(theme);
        }
    }

    public void afterPropertiesSet() throws Exception {
    }
}

