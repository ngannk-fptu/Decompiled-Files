/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.webresource.api.assembler.PageBuilderService
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.ext.code.render;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.ext.code.config.NewcodeSettings;
import com.atlassian.confluence.ext.code.config.NewcodeSettingsManager;
import com.atlassian.confluence.ext.code.languages.Language;
import com.atlassian.confluence.ext.code.languages.LanguageRegistry;
import com.atlassian.confluence.ext.code.languages.UnknownLanguageException;
import com.atlassian.confluence.ext.code.render.InvalidValueException;
import com.atlassian.confluence.ext.code.render.ParameterMapper;
import com.atlassian.confluence.ext.code.themes.ThemeRegistry;
import com.atlassian.confluence.ext.code.themes.UnknownThemeException;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.webresource.api.assembler.PageBuilderService;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public final class ContentFormatter {
    private static final Logger LOG = LoggerFactory.getLogger(ContentFormatter.class);
    private final PageBuilderService pageBuilderService;
    private final LanguageRegistry languageRegistry;
    private final ThemeRegistry themeRegistry;
    private final NewcodeSettingsManager newcodeSettingsManager;
    private ParameterMapper parameterMapper = new ParameterMapper();

    @Autowired
    public ContentFormatter(ThemeRegistry themeRegistry, LanguageRegistry languageRegistry, @ComponentImport PageBuilderService pageBuilderService, NewcodeSettingsManager newcodeSettingsManager) {
        this.themeRegistry = themeRegistry;
        this.languageRegistry = languageRegistry;
        this.pageBuilderService = pageBuilderService;
        this.newcodeSettingsManager = newcodeSettingsManager;
    }

    public String formatContent(ConversionContext conversionContext, Map<String, String> parameters, String body) throws InvalidValueException {
        Language lang;
        boolean isExport = "pdf".equals(conversionContext.getOutputType()) || "word".equals(conversionContext.getOutputType()) || "html_export".equals(conversionContext.getOutputType());
        boolean isMobile = "mobile".equals(conversionContext.getOutputDeviceType());
        LOG.debug("Starting rendering of content");
        Map<String, String> parametersWithDefaults = this.updateParametersWithDefaults(parameters);
        Map<String, String> mappedParameters = this.mapParameters(parametersWithDefaults, isExport);
        String theme = this.parameterMapper.getTheme(parametersWithDefaults).toLowerCase();
        String langStr = this.parameterMapper.getLanguage(parametersWithDefaults);
        this.verifyWithRegistry(langStr, theme);
        try {
            lang = this.languageRegistry.getLanguage(langStr);
        }
        catch (UnknownLanguageException e) {
            throw new InvalidValueException("lang");
        }
        Optional<String> customWebResource = lang.isBuiltIn() ? Optional.empty() : Optional.of(lang.getWebResource());
        String renderedContent = this.createRenderContent(body, mappedParameters, customWebResource);
        LOG.debug("Add web resources needed for rendering");
        if (isMobile) {
            this.requireMobileResources();
        }
        LOG.debug("Rendering of content finished");
        return renderedContent;
    }

    private void requireMobileResources() {
        this.pageBuilderService.assembler().resources().requireWebResource("confluence.macros.newcode.macro.mobile");
    }

    private Map<String, String> updateParametersWithDefaults(Map<String, String> parameters) {
        HashMap<String, String> merged = new HashMap<String, String>(parameters);
        if (merged.containsKey("language")) {
            merged.put("lang", (String)merged.get("language"));
        }
        NewcodeSettings currentSettings = this.newcodeSettingsManager.getCurrentSettings();
        if (!merged.containsKey("lang")) {
            merged.put("lang", currentSettings.getDefaultLanguage());
        } else {
            String language = ((String)merged.get("lang")).toLowerCase();
            if (!this.languageRegistry.isLanguageRegistered(language) && !((String)merged.get("lang")).equals("none")) {
                merged.put("lang", currentSettings.getDefaultLanguage());
            }
        }
        if (!merged.containsKey("theme")) {
            merged.put("theme", currentSettings.getDefaultTheme());
        }
        return merged;
    }

    public Map<String, String> getPanelParametersWithThemeLayout(Map<String, String> parameters) throws Exception {
        Map<String, String> layout;
        HashMap<String, String> merged = new HashMap<String, String>(parameters);
        Map<String, String> parametersWithDefaults = this.updateParametersWithDefaults(parameters);
        String theme = this.parameterMapper.getTheme(parametersWithDefaults).toLowerCase();
        try {
            layout = this.themeRegistry.getThemeLookAndFeel(theme);
        }
        catch (UnknownThemeException e) {
            throw new IllegalStateException("Invalid theme", e);
        }
        for (Map.Entry<String, String> entry : layout.entrySet()) {
            if (merged.containsKey(entry.getKey()) || entry.getValue() == null) continue;
            merged.put(entry.getKey(), entry.getValue());
        }
        return merged;
    }

    private Map<String, String> mapParameters(Map<String, String> parameters, boolean isExport) throws InvalidValueException {
        HashMap<String, String> tmpParameters = new HashMap<String, String>(parameters);
        tmpParameters.put("_isExport", Boolean.toString(isExport));
        LOG.debug("Perform mapping of parameters");
        return this.parameterMapper.mapParameters(tmpParameters);
    }

    private void verifyWithRegistry(String lang, String theme) throws InvalidValueException {
        LOG.debug("Check availability of language and theme");
        if (!this.languageRegistry.isLanguageRegistered(lang)) {
            throw new InvalidValueException("lang");
        }
        if (!this.themeRegistry.isThemeRegistered(theme)) {
            throw new InvalidValueException("theme");
        }
    }

    private String createRenderContent(String body, Map<String, String> parameters, Optional<String> customWebResource) {
        StringBuilder buffer = new StringBuilder();
        buffer.append("<pre class=\"syntaxhighlighter-pre\" data-syntaxhighlighter-params=\"");
        boolean first = true;
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            if (!first) {
                buffer.append("; ");
            }
            buffer.append(entry.getKey()).append(": ").append(entry.getValue());
            first = false;
        }
        customWebResource.ifPresent(resourceString -> {
            buffer.append("\" data-custom-language-resource=\"");
            buffer.append((String)resourceString);
        });
        buffer.append("\" data-theme=\"");
        buffer.append(parameters.get("theme"));
        buffer.append("\">");
        buffer.append(body);
        buffer.append("</pre>");
        return buffer.toString();
    }
}

