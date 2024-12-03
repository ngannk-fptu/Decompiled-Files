/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.util.PairType
 *  com.atlassian.event.Event
 *  com.atlassian.sal.api.websudo.WebSudoRequired
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.admin.actions.languages;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.event.events.admin.GlobalSettingsChangedEvent;
import com.atlassian.confluence.impl.security.AdminOnly;
import com.atlassian.confluence.languages.Language;
import com.atlassian.confluence.setup.settings.Settings;
import com.atlassian.confluence.util.HtmlUtil;
import com.atlassian.core.util.PairType;
import com.atlassian.event.Event;
import com.atlassian.sal.api.websudo.WebSudoRequired;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

@WebSudoRequired
@AdminOnly
public class ConfigureLanguageAction
extends ConfluenceActionSupport {
    private String globalDefaultLocale;
    private boolean editMode = true;

    @Override
    public void validate() {
        String globalDefaultLocale = this.getGlobalDefaultLocale();
        if (StringUtils.isNotEmpty((CharSequence)globalDefaultLocale)) {
            this.languageManager.getLanguage(globalDefaultLocale);
        }
    }

    @Override
    @PermittedMethods(value={HttpMethod.GET, HttpMethod.POST})
    public String doDefault() throws Exception {
        this.getGlobalDefaultLocaleFromSettings(this.settingsManager.getGlobalSettings());
        return super.doDefault();
    }

    @PermittedMethods(value={HttpMethod.GET})
    public String doView() throws Exception {
        this.editMode = false;
        return this.doDefault();
    }

    public String execute() throws Exception {
        Settings settings = this.settingsManager.getGlobalSettings();
        if (this.globalDefaultLocale == null) {
            this.getGlobalDefaultLocaleFromSettings(settings);
        } else if (!this.globalDefaultLocale.equals(settings.getGlobalDefaultLocale())) {
            this.updateLocale(settings, this.globalDefaultLocale);
        }
        return super.execute();
    }

    private void getGlobalDefaultLocaleFromSettings(Settings settings) {
        this.globalDefaultLocale = settings.getGlobalDefaultLocale();
        boolean localeExists = false;
        for (Language language : this.getInstalledLanguages()) {
            if (!this.globalDefaultLocale.equals(language.getName())) continue;
            localeExists = true;
            break;
        }
        if (!localeExists) {
            this.globalDefaultLocale = "en_GB";
            this.updateLocale(settings, this.globalDefaultLocale);
        }
    }

    public List<PairType> getInstalledLanguagesList() {
        List<Language> languages = this.getInstalledLanguages();
        ArrayList<PairType> languagesDetails = new ArrayList<PairType>();
        for (Language language : languages) {
            String displayName = HtmlUtil.htmlEncode(language.getDisplayName());
            String name = HtmlUtil.htmlEncode(language.getName());
            languagesDetails.add(new PairType((Serializable)((Object)displayName), (Serializable)((Object)name)));
        }
        return languagesDetails;
    }

    private void updateLocale(Settings settings, String newLocale) {
        Settings originalSettings = new Settings(this.settingsManager.getGlobalSettings());
        settings.setGlobalDefaultLocale(newLocale);
        this.settingsManager.updateGlobalSettings(settings);
        GlobalSettingsChangedEvent event = new GlobalSettingsChangedEvent(this, originalSettings, this.settingsManager.getGlobalSettings(), this.settingsManager.getGlobalSettings().getBaseUrl(), this.settingsManager.getGlobalSettings().getBaseUrl());
        this.eventManager.publishEvent((Event)event);
    }

    public String getGlobalDefaultLocale() {
        if (this.globalDefaultLocale == null) {
            this.getGlobalDefaultLocaleFromSettings(this.settingsManager.getGlobalSettings());
        }
        return this.globalDefaultLocale;
    }

    public void setGlobalDefaultLocale(String globalDefaultLocale) {
        this.globalDefaultLocale = globalDefaultLocale;
    }

    public String getGlobalDefaultLocaleUserFriendly() {
        return this.getLanguageUserFriendly(this.globalDefaultLocale);
    }

    public boolean isCurrentGlobalLocale(String locale) {
        if (this.globalDefaultLocale == null) {
            return false;
        }
        return this.globalDefaultLocale.equals(locale);
    }

    public boolean isEditMode() {
        return this.editMode;
    }
}

