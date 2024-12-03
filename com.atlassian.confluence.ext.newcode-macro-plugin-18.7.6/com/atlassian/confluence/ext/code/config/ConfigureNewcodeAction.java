/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ConfluenceActionSupport
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.user.User
 *  com.atlassian.xwork.FileUploadUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.ext.code.config;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.ext.code.config.NewcodeSettingsManager;
import com.atlassian.confluence.ext.code.languages.Language;
import com.atlassian.confluence.ext.code.languages.LanguageRegistry;
import com.atlassian.confluence.ext.code.languages.RegisteredLanguageInstaller;
import com.atlassian.confluence.ext.code.languages.UnknownLanguageException;
import com.atlassian.confluence.ext.code.themes.ThemeRegistry;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.user.User;
import com.atlassian.xwork.FileUploadUtils;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ConfigureNewcodeAction
extends ConfluenceActionSupport {
    private static final Logger LOG = LoggerFactory.getLogger(ConfigureNewcodeAction.class);
    private static final int MAX_LANGUAGE_NAME_LENGTH = 30;
    private String defaultThemeName;
    private String defaultLanguageName;
    private String newLanguageName;
    private NewcodeSettingsManager newcodeSettingsManager;
    private ThemeRegistry themeRegistry;
    private LanguageRegistry languageRegistry;
    private RegisteredLanguageInstaller languageInstaller;

    public void setNewcodeSettingsManager(NewcodeSettingsManager newcodeSettingsManager) {
        this.newcodeSettingsManager = newcodeSettingsManager;
    }

    public void setThemeRegistry(ThemeRegistry themeRegistry) {
        this.themeRegistry = themeRegistry;
    }

    public void setLanguageInstaller(RegisteredLanguageInstaller languageInstaller) {
        this.languageInstaller = languageInstaller;
    }

    public void setLanguageRegistry(LanguageRegistry languageRegistry) {
        this.languageRegistry = languageRegistry;
    }

    public boolean getDisplayUpload() {
        return this.permissionManager.hasPermission((User)this.getAuthenticatedUser(), Permission.ADMINISTER, PermissionManager.TARGET_SYSTEM);
    }

    public Language getDefaultLanguage() throws UnknownLanguageException {
        String defaultLanguage = this.newcodeSettingsManager.getCurrentSettings().getDefaultLanguage();
        if (!StringUtils.isBlank((CharSequence)defaultLanguage)) {
            try {
                return this.languageRegistry.getLanguage(defaultLanguage);
            }
            catch (UnknownLanguageException e) {
                LOG.warn("Unable to retrieve default language {}; has it been removed?", (Object)defaultLanguage);
            }
        }
        return this.languageRegistry.getLanguage("Java");
    }

    public String getDefaultLanguageName() throws Exception {
        return this.getDefaultLanguage().getName();
    }

    public String getDefaultLanguageAlias() throws Exception {
        Language defaultLanguage = this.getDefaultLanguage();
        return defaultLanguage.getAliases().iterator().next();
    }

    public String getDefaultThemeResource() throws Exception {
        String defaultThemeName = this.newcodeSettingsManager.getCurrentSettings().getDefaultTheme();
        if (StringUtils.isBlank((CharSequence)defaultThemeName)) {
            defaultThemeName = "Confluence";
        }
        return this.themeRegistry.getWebResourceForTheme(defaultThemeName);
    }

    public String getDefaultLanguageResource() throws Exception {
        Language language = this.languageRegistry.getLanguage(this.getDefaultLanguageAlias());
        return this.languageRegistry.getWebResourceForLanguage(language.getAliases().iterator().next());
    }

    public String input() {
        return "input";
    }

    public String save() throws Exception {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Handling save event for the Newcode configuration UI");
        }
        if ("".equals(this.defaultThemeName)) {
            this.defaultThemeName = null;
        }
        if ("".equals(this.defaultLanguageName)) {
            this.defaultLanguageName = null;
        }
        this.newcodeSettingsManager.updateSettings(this.defaultThemeName, this.defaultLanguageName);
        this.addActionMessage(this.getText("newcode.config.successfully.saved"));
        if (LOG.isDebugEnabled()) {
            LOG.debug("Save event for the Newcode configuration UI handled");
        }
        return "success";
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public String addLanguage() throws Exception {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Handling addLanguage event for the Newcode configuration UI");
        }
        if (!this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.ADMINISTER, PermissionManager.TARGET_SYSTEM)) {
            LOG.error("Only system administrators may add new code macro languages.");
            this.addActionError(this.getText("newcode.config.language.add.sysadmin.required"));
            return "error";
        }
        if (StringUtils.isBlank((CharSequence)this.newLanguageName)) {
            this.addActionError(this.getText("newcode.config.language.add.friendlyname.required"));
            return "input";
        }
        if (this.newLanguageName.length() > 30) {
            this.addActionError(this.getText("newcode.config.language.add.friendlyname.length"));
            return "input";
        }
        File uploadedLanguage = FileUploadUtils.getSingleFile();
        if (uploadedLanguage == null) {
            this.addActionError(this.getText("newcode.config.language.add.filename.required"));
            return "input";
        }
        try (FileReader reader = new FileReader(uploadedLanguage);){
            this.languageInstaller.installLanguage(reader, this.newLanguageName);
        }
        this.addActionMessage(this.getText("newcode.config.successfully.added"));
        return "success";
    }

    public String getActionName(String fullClassName) {
        return "Configure New Code Plugin";
    }

    public List<String> getThemes() throws Exception {
        ArrayList<String> result = new ArrayList<String>();
        this.themeRegistry.listThemes().forEach(theme -> result.add(theme.getName()));
        Collections.sort(result);
        return result;
    }

    public List<Language> getLanguages() {
        List<Language> languages = this.languageRegistry.listLanguages();
        languages.sort((first, second) -> {
            String firstName = StringUtils.isBlank((CharSequence)first.getFriendlyName()) ? first.getName() : first.getFriendlyName();
            String secondName = StringUtils.isBlank((CharSequence)second.getFriendlyName()) ? second.getName() : second.getFriendlyName();
            return firstName.compareTo(secondName);
        });
        return languages;
    }

    public String getCurrentDefaultThemeName() {
        String defaultTheme = this.newcodeSettingsManager.getCurrentSettings().getDefaultTheme();
        if (StringUtils.isBlank((CharSequence)defaultTheme)) {
            return "Confluence";
        }
        return defaultTheme;
    }

    public void setDefaultThemeName(String defaultThemeName) {
        this.defaultThemeName = defaultThemeName;
    }

    public void setDefaultLanguageName(String defaultLanguageName) {
        this.defaultLanguageName = defaultLanguageName;
    }

    public void setNewLanguageName(String newLanguageName) {
        this.newLanguageName = newLanguageName;
    }
}

