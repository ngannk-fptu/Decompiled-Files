/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.util.BootstrapUtils
 *  com.opensymphony.oscache.web.ServletCacheAdministrator
 *  javax.servlet.ServletContext
 *  org.apache.struts2.ServletActionContext
 */
package com.atlassian.confluence.util;

import com.atlassian.config.util.BootstrapUtils;
import com.atlassian.confluence.plugin.descriptor.ThemeModuleDescriptor;
import com.atlassian.confluence.setup.BootstrapManager;
import com.atlassian.confluence.themes.ThemeManager;
import com.opensymphony.oscache.web.ServletCacheAdministrator;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.servlet.ServletContext;
import org.apache.struts2.ServletActionContext;

public class LayoutHelper {
    public static final String TEMPLATE_PATH = "velocity";
    protected static final String FS = System.getProperty("file.separator");
    public static final String THEME_ICON = "themeicon.gif";
    public static final String GLOBAL_CONFIGPATH = "global-config-path";
    public static final String SPACE_CONFIGPATH = "space-config-path";
    private ThemeManager themeManager;
    private boolean isGlobal = true;
    private static final String[] BUTTON_VALUES = new String[]{"global", "custom", "theme"};
    public static final List BUTTON_VALUE_LIST = Collections.unmodifiableList(Arrays.asList(BUTTON_VALUES));

    public static void flushThemeComponents(String spaceKey) {
        ServletCacheAdministrator admin = ServletCacheAdministrator.getInstance((ServletContext)ServletActionContext.getServletContext());
        admin.getAppScopeCache(ServletActionContext.getServletContext()).flushPattern(".css");
    }

    public ThemeModuleDescriptor findThemeDescriptor(String moduleCompleteKey) {
        return this.themeManager.getAvailableThemeDescriptors().stream().filter(d -> d.getCompleteKey().equals(moduleCompleteKey)).findFirst().orElse(null);
    }

    public boolean hasIcon(ThemeModuleDescriptor descriptor) {
        return descriptor.getResourceDescriptor("download", THEME_ICON) != null;
    }

    public String getConfigPath(ThemeModuleDescriptor descriptor, String configPath) {
        if (descriptor.getParams().containsKey(configPath)) {
            return (String)descriptor.getParams().get(configPath);
        }
        return null;
    }

    public List getColourSchemeTypes() {
        return BUTTON_VALUE_LIST;
    }

    public boolean isGlobal() {
        return this.isGlobal;
    }

    public void setGlobal(boolean global) {
        this.isGlobal = global;
    }

    public void setThemeManager(ThemeManager themeManager) {
        this.themeManager = themeManager;
    }

    public static String getFullTemplatePath() {
        BootstrapManager bootstrapManager = (BootstrapManager)BootstrapUtils.getBootstrapManager();
        return bootstrapManager.getConfluenceHome() + System.getProperty("file.separator") + TEMPLATE_PATH;
    }
}

