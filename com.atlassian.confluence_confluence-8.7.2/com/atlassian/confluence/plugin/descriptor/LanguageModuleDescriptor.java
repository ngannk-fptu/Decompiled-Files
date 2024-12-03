/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.descriptors.AbstractModuleDescriptor
 *  com.atlassian.plugin.module.ModuleFactory
 *  org.apache.commons.lang3.StringUtils
 *  org.dom4j.Element
 */
package com.atlassian.confluence.plugin.descriptor;

import com.atlassian.confluence.languages.Language;
import com.atlassian.confluence.plugin.module.PluginModuleHolder;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.descriptors.AbstractModuleDescriptor;
import com.atlassian.plugin.module.ModuleFactory;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Element;

public class LanguageModuleDescriptor
extends AbstractModuleDescriptor<Language> {
    private String encoding;
    private String language;
    private String country;
    private String variant;
    private PluginModuleHolder<Language> languageModule;

    public LanguageModuleDescriptor(ModuleFactory moduleFactory) {
        super(moduleFactory);
    }

    public void init(Plugin plugin, Element element) throws PluginParseException {
        super.init(plugin, element);
        if (element.attribute("language") != null) {
            this.setLanguage(element.attribute("language").getText());
        }
        if (element.attribute("country") != null) {
            this.setCountry(element.attribute("country").getText());
        }
        if (element.attribute("variant") != null) {
            this.setVariant(element.attribute("variant").getText());
        }
        if (element.attribute("encoding") != null) {
            this.setEncoding(element.attribute("encoding").getText());
        }
        if (element.attribute("language") == null || !StringUtils.isNotEmpty((CharSequence)element.attribute("language").getValue())) {
            throw new PluginParseException("Module " + this.getCompleteKey() + " must define an \"language\" attribute");
        }
        this.languageModule = PluginModuleHolder.getInstance(() -> new Language(this));
    }

    public Language getModule() {
        return this.languageModule.getModule();
    }

    public void enabled() {
        super.enabled();
        this.languageModule.enabled(this.getModuleClass());
    }

    public void disabled() {
        this.languageModule.disabled();
        super.disabled();
    }

    public String getEncoding() {
        return this.encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public String getLanguage() {
        return this.language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getCountry() {
        return this.country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getVariant() {
        return this.variant;
    }

    public void setVariant(String variant) {
        this.variant = variant;
    }
}

