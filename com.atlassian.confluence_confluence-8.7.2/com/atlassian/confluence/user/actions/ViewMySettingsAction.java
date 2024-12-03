/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.security.XsrfProtectionExcluded
 *  com.atlassian.core.util.PairType
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.user.actions;

import com.atlassian.annotations.security.XsrfProtectionExcluded;
import com.atlassian.confluence.core.FormAware;
import com.atlassian.confluence.core.ListBuilder;
import com.atlassian.confluence.core.TimeZone;
import com.atlassian.confluence.languages.BrowserLanguageUtils;
import com.atlassian.confluence.languages.Language;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceType;
import com.atlassian.confluence.spaces.SpacesQuery;
import com.atlassian.confluence.user.actions.AbstractUserProfileAction;
import com.atlassian.confluence.util.HTMLPairType;
import com.atlassian.core.util.PairType;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class ViewMySettingsAction
extends AbstractUserProfileAction
implements FormAware {
    private boolean editMode;
    protected String preferredUserLocale;
    protected TimeZone userTimeZone;
    protected List<PairType> installedLocalePairs;
    protected String siteHomePage;
    protected static final String DEFAULT_KEY = "None";
    protected Boolean keyboardShortcutsEnabled;
    protected Boolean highlightOptionPanelEnabled;
    private static final String NO_LOCALE = "none";
    private static final String DASHBOARD_HOMEPAGE = "dashboard";
    private static final String CONFLUENCE_HIGHLIGHT_ACTION_KEY = "com.atlassian.confluence.plugins.confluence-highlight-actions";

    @PermittedMethods(value={HttpMethod.GET})
    public String doView() {
        this.editMode = false;
        return "success";
    }

    @PermittedMethods(value={HttpMethod.GET, HttpMethod.POST})
    @XsrfProtectionExcluded
    public String doEdit() throws Exception {
        this.editMode = true;
        return "input";
    }

    @Override
    public boolean isPermitted() {
        return this.getUsername() != null && super.isPermitted();
    }

    public String getSiteHomePage() {
        if (this.siteHomePage == null) {
            this.siteHomePage = this.userAccessor.getPropertySet(this.getAuthenticatedUser()).getString("confluence.user.site.homepage");
        }
        if (StringUtils.isBlank((CharSequence)this.siteHomePage)) {
            this.siteHomePage = "siteHomepage";
        }
        return this.siteHomePage;
    }

    public List<HTMLPairType> getSiteHomePages() {
        ArrayList<HTMLPairType> siteHomePages = new ArrayList<HTMLPairType>();
        siteHomePages.add(new HTMLPairType(DASHBOARD_HOMEPAGE, this.getText("dashboard.name")));
        siteHomePages.add(new HTMLPairType("siteHomepage", this.getText("homepage.default", new String[]{this.getGlobalHomepageSetting()})));
        siteHomePages.add(new HTMLPairType("profile", this.getText("selection.your.profile")));
        ArrayList<Space> visibleSpaces = new ArrayList<Space>();
        Space personalSpace = this.spaceManager.getPersonalSpace(this.getAuthenticatedUser());
        if (personalSpace != null) {
            visibleSpaces.add(personalSpace);
        }
        ListBuilder<Space> listBuilder = this.spaceManager.getSpaces(SpacesQuery.newQuery().forUser(this.getAuthenticatedUser()).withSpaceType(SpaceType.GLOBAL).build());
        for (List list : listBuilder) {
            visibleSpaces.addAll(list);
        }
        for (Space space : visibleSpaces) {
            siteHomePages.add(new HTMLPairType(space.getKey(), space.getName() + " (" + space.getKey() + ")"));
        }
        return siteHomePages;
    }

    public List<HTMLPairType> getAvailableTimeZones() {
        ArrayList<HTMLPairType> result = new ArrayList<HTMLPairType>();
        TimeZone defaultTimeZone = this.getDefaultTimeZone();
        result.add(new HTMLPairType(defaultTimeZone.getID(), this.defaultTimeZoneCaption(defaultTimeZone)));
        List<TimeZone> timeZones = TimeZone.getSortedTimeZones();
        for (TimeZone timeZone : timeZones) {
            TimeZone tz = timeZone;
            if (tz.equals(defaultTimeZone)) continue;
            result.add(new HTMLPairType(tz.getID(), this.timeZoneCaption(tz)));
        }
        return result;
    }

    private String defaultTimeZoneCaption(TimeZone defaultTimeZone) {
        return this.getText("time.zone.server.default", new Object[]{this.timeZoneCaption(defaultTimeZone)});
    }

    private String timeZoneCaption(TimeZone timeZone) {
        String key = timeZone.getMessageKey();
        if (key.equals(this.getText(key))) {
            return timeZone.getID();
        }
        return this.getText("time.zone.caption", new Object[]{timeZone.getDisplayOffset(), this.getText(key)});
    }

    public String getUserLocaleName() {
        if (NO_LOCALE.equals(this.getLocaleString())) {
            return this.getDefaultLanguageText();
        }
        for (PairType pairType : this.getInstalledLocalePairs()) {
            if (!pairType.getKey().equals(this.getLocaleString())) continue;
            return (String)((Object)pairType.getValue());
        }
        return null;
    }

    public List<PairType> getInstalledLocalePairs() {
        if (this.installedLocalePairs == null) {
            List<Language> installedLanguages = this.languageManager.getLanguages();
            ArrayList<PairType> localePairs = new ArrayList<PairType>();
            localePairs.add(0, new PairType((Serializable)((Object)DEFAULT_KEY), (Serializable)((Object)this.getDefaultLanguageText())));
            for (Language language : installedLanguages) {
                PairType pair = new PairType((Serializable)((Object)language.getName()), (Serializable)((Object)language.getDisplayName()));
                localePairs.add(pair);
            }
            this.installedLocalePairs = localePairs;
        }
        return this.installedLocalePairs;
    }

    public String getPreferredUserLocale() {
        if (this.preferredUserLocale == null) {
            this.preferredUserLocale = this.getUserPreferences().getString("confluence.user.locale");
        }
        return this.preferredUserLocale;
    }

    public boolean isHighlightOptionPanelEnabled() {
        if (this.highlightOptionPanelEnabled == null) {
            this.highlightOptionPanelEnabled = this.getUserPreferences().getBoolean("confluence.user.highlight.option.panel.enabled");
        }
        return this.highlightOptionPanelEnabled;
    }

    public boolean isHighlightPluginEnabled() {
        return this.pluginAccessor.isPluginEnabled(CONFLUENCE_HIGHLIGHT_ACTION_KEY);
    }

    public boolean isKeyboardShortcutsEnabled() {
        if (this.keyboardShortcutsEnabled == null) {
            this.keyboardShortcutsEnabled = !this.getUserPreferences().getBoolean("confluence.user.keyboard.shortcuts.disabled");
        }
        return this.keyboardShortcutsEnabled;
    }

    protected String getGlobalHomepageSetting() {
        String siteHomePage = this.getGlobalSettings().getSiteHomePage();
        siteHomePage = DASHBOARD_HOMEPAGE.equals(siteHomePage) || siteHomePage == null ? this.getText("dashboard.name") : this.getFriendlySpaceName(siteHomePage);
        return siteHomePage;
    }

    private String getFriendlySpaceName(String siteHomePage) {
        Space homePageSpace = this.spaceManager.getSpace((String)siteHomePage);
        if (homePageSpace != null) {
            siteHomePage = homePageSpace.getName() + " (" + homePageSpace.getKey() + ")";
        }
        return siteHomePage;
    }

    public String getUserTimeZone() {
        if (this.userTimeZone == null) {
            this.userTimeZone = this.userAccessor.getConfluenceUserPreferences(this.getUser()).getTimeZone();
        }
        return this.userTimeZone.getID();
    }

    @Override
    public boolean isEditMode() {
        return this.editMode;
    }

    private String getDefaultLanguageText() {
        if (BrowserLanguageUtils.isBrowserLanguageEnabled()) {
            return this.getText("language.detect");
        }
        return this.getText("language.default", new String[]{this.languageManager.getGlobalDefaultLanguage().getDisplayLanguage()});
    }
}

