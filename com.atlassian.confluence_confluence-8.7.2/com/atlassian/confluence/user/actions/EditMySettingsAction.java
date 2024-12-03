/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.AtlassianCoreException
 *  com.atlassian.core.user.preferences.UserPreferences
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.user.actions;

import com.atlassian.confluence.core.FormAware;
import com.atlassian.confluence.core.TimeZone;
import com.atlassian.confluence.languages.Language;
import com.atlassian.confluence.user.actions.ViewMySettingsAction;
import com.atlassian.core.AtlassianCoreException;
import com.atlassian.core.user.preferences.UserPreferences;
import org.apache.commons.lang3.StringUtils;

public class EditMySettingsAction
extends ViewMySettingsAction
implements FormAware {
    private boolean isUserLocaleUpdated;

    @Override
    public String doEdit() throws AtlassianCoreException {
        this.isUserLocaleUpdated = false;
        this.updateUserPreferences();
        boolean isSuccessful = this.getFieldErrors().isEmpty();
        if (this.isUserLocaleUpdated) {
            this.getLocaleManager().invalidateLocaleInfoCache(this.getUser());
        }
        return isSuccessful ? "success" : "input";
    }

    @Override
    public void validate() {
        Language language;
        super.validate();
        if (StringUtils.isNotBlank((CharSequence)this.preferredUserLocale) && !this.preferredUserLocale.equals("None") && (language = this.languageManager.getLanguage(this.preferredUserLocale)) == null) {
            this.addActionError(this.getText("language.not.valid"));
        }
    }

    protected void updateUserPreferences() throws AtlassianCoreException {
        UserPreferences userPreferences = this.getUserPreferences();
        if ("".equals(this.siteHomePage)) {
            try {
                userPreferences.remove("confluence.user.site.homepage");
            }
            catch (AtlassianCoreException atlassianCoreException) {}
        } else {
            userPreferences.setString("confluence.user.site.homepage", this.siteHomePage);
        }
        String userLocale = userPreferences.getString("confluence.user.locale");
        if (this.preferredUserLocale.equals("None")) {
            if (userLocale != null) {
                userPreferences.remove("confluence.user.locale");
                this.isUserLocaleUpdated = true;
            }
        } else {
            userPreferences.setString("confluence.user.locale", this.preferredUserLocale.trim());
            this.isUserLocaleUpdated = true;
        }
        if (this.keyboardShortcutsEnabled == null) {
            this.keyboardShortcutsEnabled = false;
        }
        userPreferences.setBoolean("confluence.user.keyboard.shortcuts.disabled", this.keyboardShortcutsEnabled == false);
        if (this.highlightOptionPanelEnabled == null) {
            this.highlightOptionPanelEnabled = false;
        }
        userPreferences.setBoolean("confluence.user.highlight.option.panel.enabled", this.highlightOptionPanelEnabled.booleanValue());
        this.userAccessor.getConfluenceUserPreferences(this.getUser()).setTimeZone(this.getUserTimeZone());
    }

    public void setPreferredUserLocale(String locale) {
        this.preferredUserLocale = locale;
    }

    public void setKeyboardShortcutsEnabled(boolean keyboardShortcutsEnabled) {
        this.keyboardShortcutsEnabled = keyboardShortcutsEnabled;
    }

    public void setHighlightOptionPanelEnabled(boolean highlightOptionPanelEnabled) {
        this.highlightOptionPanelEnabled = highlightOptionPanelEnabled;
    }

    public void setSiteHomePage(String siteHomePage) {
        this.siteHomePage = siteHomePage;
    }

    public void setUserTimeZone(String timeZoneID) {
        this.userTimeZone = TimeZone.getInstance(timeZoneID);
    }

    @Override
    public boolean isPermitted() {
        return this.getUsername() != null && super.isPermitted();
    }

    @Override
    public boolean isEditMode() {
        return true;
    }
}

