/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ConfluenceActionSupport
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.PluginController
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.ext.code.config;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.ext.code.languages.Language;
import com.atlassian.confluence.ext.code.languages.LanguageRegistry;
import com.atlassian.confluence.ext.code.languages.UnknownLanguageException;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.PluginController;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigureRpcAction
extends ConfluenceActionSupport {
    private static final Logger log = LoggerFactory.getLogger(ConfigureRpcAction.class);
    private PluginAccessor pluginAccessor;
    private PluginController pluginController;
    private LanguageRegistry languageRegistry;
    private String languageName;

    public void setPluginAccessor(PluginAccessor pluginAccessor) {
        this.pluginAccessor = pluginAccessor;
    }

    public void setPluginController(PluginController pluginController) {
        this.pluginController = pluginController;
    }

    public void setLanguageRegistry(LanguageRegistry languageRegistry) {
        this.languageRegistry = languageRegistry;
    }

    public boolean isPermitted() {
        return this.permissionManager.hasPermission(AuthenticatedUserThreadLocal.getUser(), Permission.ADMINISTER, PermissionManager.TARGET_SYSTEM);
    }

    public void setLanguageName(String languageName) {
        this.languageName = languageName;
    }

    public String removeLanguage() {
        if (StringUtils.isBlank((CharSequence)this.languageName)) {
            return "input";
        }
        try {
            Language language = this.languageRegistry.getLanguage(this.languageName);
            if (language.isBuiltIn()) {
                log.error(String.format("Built-in language %s cannot be un-installed.", this.languageName));
                return "error";
            }
            String pluginKey = this.getPluginKeyForLanguage(language);
            Plugin plugin = this.pluginAccessor.getPlugin(pluginKey);
            this.pluginController.uninstall(plugin);
        }
        catch (UnknownLanguageException e) {
            log.warn(String.format("Language %s does not exist in the Code macro configuration", this.languageName));
        }
        return "success";
    }

    private String getPluginKeyForLanguage(Language language) {
        return language.getWebResource().substring(0, language.getWebResource().indexOf(":"));
    }
}

