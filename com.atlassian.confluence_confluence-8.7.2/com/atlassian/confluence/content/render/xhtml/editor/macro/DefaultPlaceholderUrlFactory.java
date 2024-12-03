/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.content.render.xhtml.editor.macro;

import com.atlassian.confluence.content.render.xhtml.editor.macro.PlaceholderUrlFactory;
import com.atlassian.confluence.core.ContextPathHolder;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.macro.MacroDefinitionSerializer;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.xhtml.api.MacroDefinition;
import org.apache.commons.lang3.StringUtils;

public class DefaultPlaceholderUrlFactory
implements PlaceholderUrlFactory {
    private final int placeholderVersion;
    private final SettingsManager settingsManager;
    private final ContextPathHolder contextPathHolder;
    private final LocaleManager localeManager;
    private final MacroDefinitionSerializer macroDefinitionSerializer;

    public DefaultPlaceholderUrlFactory(int placeholderVersion, SettingsManager settingsManager, ContextPathHolder contextPathHolder, LocaleManager localeManager, MacroDefinitionSerializer macroDefinitionSerializer) {
        this.settingsManager = settingsManager;
        this.contextPathHolder = contextPathHolder;
        this.placeholderVersion = placeholderVersion;
        this.localeManager = localeManager;
        this.macroDefinitionSerializer = macroDefinitionSerializer;
    }

    @Override
    public String getUrlForMacro(MacroDefinition macroDefinition) {
        if (macroDefinition == null) {
            throw new IllegalArgumentException("macro definition is required");
        }
        String url = this.contextPathHolder.getContextPath() + "/plugins/servlet/confluence/placeholder/macro?definition=" + this.macroDefinitionSerializer.serialize(macroDefinition);
        url = url + "&" + this.getCachingParameters();
        return url;
    }

    @Override
    public String getUrlForMacroHeading(MacroDefinition macroDefinition) {
        if (macroDefinition == null) {
            throw new IllegalArgumentException("macro definition is required");
        }
        StringBuilder url = new StringBuilder(this.settingsManager.getGlobalSettings().getBaseUrl());
        url.append("/plugins/servlet/confluence/placeholder/macro-heading?definition=");
        url.append(this.macroDefinitionSerializer.serialize(macroDefinition));
        url.append("&").append(this.getCachingParameters());
        return url.toString();
    }

    @Override
    public String getUrlForUnknownAttachment() {
        return this.contextPathHolder.getContextPath() + "/plugins/servlet/confluence/placeholder/unknown-attachment?" + this.getCachingParameters();
    }

    @Override
    public String getUrlForUnknownMacro(String macroName) {
        if (StringUtils.isBlank((CharSequence)macroName)) {
            throw new IllegalArgumentException("macro name is required");
        }
        return this.contextPathHolder.getContextPath() + "/plugins/servlet/confluence/placeholder/unknown-macro?name=" + macroName + "&" + this.getCachingParameters();
    }

    @Override
    public String getUrlForErrorPlaceholder(String errorI18nKey) {
        return this.contextPathHolder.getContextPath() + "/plugins/servlet/confluence/placeholder/error?i18nKey=" + StringUtils.defaultString((String)errorI18nKey) + "&" + this.getCachingParameters();
    }

    private String getCachingParameters() {
        return "locale=" + this.localeManager.getLocale(AuthenticatedUserThreadLocal.get()).toString() + "&version=" + this.placeholderVersion;
    }
}

