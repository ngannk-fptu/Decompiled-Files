/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.elements.ResourceDescriptor
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.languages;

import com.atlassian.confluence.plugin.descriptor.LanguageModuleDescriptor;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.plugin.elements.ResourceDescriptor;
import java.io.Serializable;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;

public class Language
implements Serializable {
    private String encoding;
    private Locale locale;
    private String flagUrl;

    public Language(Locale locale) {
        this.locale = locale;
    }

    public Language(LanguageModuleDescriptor moduleDescriptor) {
        this.setEncoding(moduleDescriptor.getEncoding());
        String language = moduleDescriptor.getLanguage();
        String country = moduleDescriptor.getCountry();
        String variant = moduleDescriptor.getVariant();
        if (language == null) {
            throw new NullPointerException("The language attribute of Language cannot be null");
        }
        if (country == null) {
            country = "";
        }
        if (variant == null) {
            variant = "";
        }
        this.locale = new Locale(language, country, variant);
        Optional<ResourceDescriptor> resource = moduleDescriptor.getResourceDescriptors().stream().filter(r -> "download".equalsIgnoreCase(r.getType())).findFirst();
        if (resource.isPresent()) {
            ResourceDescriptor descriptor = resource.get();
            this.setFlagUrl("/download/resources/" + moduleDescriptor.getCompleteKey() + "/" + descriptor.getName());
        }
    }

    public String getName() {
        return this.getLocale().toString();
    }

    public String getDisplayLanguage() {
        return this.getLocale().getDisplayLanguage(this.getLocale());
    }

    public String getDisplayName() {
        return this.getDisplayName(true);
    }

    public String getDisplayName(boolean shortCountry) {
        String displayName = this.getLocale().getDisplayName(this.getLocale());
        return !shortCountry ? displayName : displayName.replace("United Kingdom", "UK").replace("United States", "US");
    }

    public String getEncoding() {
        return this.encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public void setLanguage(String language) {
        Locale oldLocale = this.getLocale();
        Locale newLocale = new Locale(language, oldLocale.getCountry(), oldLocale.getVariant());
        this.setLocale(newLocale);
    }

    public String getLanguage() {
        return this.locale.getLanguage();
    }

    public void setCountry(String country) {
        Locale oldLocale = this.getLocale();
        Locale newLocale = new Locale(oldLocale.getLanguage(), country, oldLocale.getVariant());
        this.setLocale(newLocale);
    }

    public String getCountry() {
        return this.locale.getCountry();
    }

    public void setVariant(String variant) {
        Locale oldLocale = this.getLocale();
        Locale newLocale = new Locale(oldLocale.getLanguage(), oldLocale.getCountry(), variant);
        this.setLocale(newLocale);
    }

    public String setVariant() {
        return this.locale.getVariant();
    }

    public Locale getLocale() {
        return this.locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public String getFlagUrl() {
        return this.flagUrl;
    }

    public void setFlagUrl(String flagUrl) {
        this.flagUrl = flagUrl;
    }

    public String getResourceBundlePath() {
        String localeString = null;
        if (this.locale != null) {
            localeString = "_" + this.locale.toString();
        }
        return I18NBean.DEFAULT_RESOURCE_BUNDLE.replaceAll("\\.", "/") + localeString + ".properties";
    }

    public String getCapitalDisplayLanguage() {
        return StringUtils.capitalize((String)this.getDisplayLanguage());
    }

    public String getCapitalDisplayName() {
        return StringUtils.capitalize((String)this.getDisplayName());
    }

    public int hashCode() {
        return Objects.hash(this.encoding, this.locale, this.flagUrl);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Language)) {
            return false;
        }
        Language that = (Language)obj;
        return Objects.equals(this.encoding, that.encoding) && Objects.equals(this.locale, that.locale) && Objects.equals(this.flagUrl, that.flagUrl);
    }

    public String getJsLang() {
        return Language.getJsLang(this.getName());
    }

    public static String getJsLang(String langStr) {
        String jsStr = langStr.replace('_', '-');
        if (jsStr.equals("no-NO")) {
            return "nn";
        }
        return jsStr;
    }
}

