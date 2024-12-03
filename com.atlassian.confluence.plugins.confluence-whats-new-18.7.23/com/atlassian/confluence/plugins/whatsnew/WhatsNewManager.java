/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.security.login.LoginInfo
 *  com.atlassian.confluence.security.login.LoginManager
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.core.AtlassianCoreException
 *  com.atlassian.core.user.preferences.UserPreferences
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.user.User
 *  com.google.common.base.Objects
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.whatsnew;

import com.atlassian.confluence.security.login.LoginInfo;
import com.atlassian.confluence.security.login.LoginManager;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.core.AtlassianCoreException;
import com.atlassian.core.user.preferences.UserPreferences;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.user.User;
import com.google.common.base.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WhatsNewManager {
    private static final Logger log = LoggerFactory.getLogger(WhatsNewManager.class);
    private static final Pattern MAJOR_VERSION_PATTERN = Pattern.compile("^(\\d+\\.\\d+).*");
    private static final String PROPERTY_KEY = "confluence.user.whats.new.dont.show.version";
    private static final String VERSION_UNSET = "UNSET";
    private final UserAccessor userAccessor;
    private final ApplicationProperties applicationProperties;
    private final LoginManager loginManager;

    public WhatsNewManager(UserAccessor userAccessor, ApplicationProperties applicationProperties, LoginManager loginManager) {
        this.userAccessor = userAccessor;
        this.applicationProperties = applicationProperties;
        this.loginManager = loginManager;
    }

    public boolean isShownForUser(User user, boolean fromCheck) {
        String currentVersion;
        if (user == null) {
            return false;
        }
        if (this.hasNeverLoggedInBefore(user)) {
            try {
                this.setShownForUser(user, false);
            }
            catch (AtlassianCoreException e) {
                log.warn("Couldn't save the preference for the What's New dialog for user " + user.getName(), (Throwable)e);
            }
            return false;
        }
        UserPreferences pref = this.getUserPreferences(user);
        if (pref == null) {
            return false;
        }
        String seenVersion = pref.getString(PROPERTY_KEY);
        boolean shown = this.isShownForVersions(seenVersion, currentVersion = this.getCurrentVersion());
        if (shown && fromCheck) {
            log.debug("Shown for user '{}' : don't-show version is {} but current version is {}", new Object[]{user.getName(), seenVersion, currentVersion});
        }
        return shown;
    }

    public void setShownForUser(User user, boolean shown) throws AtlassianCoreException {
        UserPreferences pref = this.getUserPreferences(user);
        if (pref == null) {
            return;
        }
        String dontShowVersion = shown ? VERSION_UNSET : this.getCurrentVersion();
        pref.setString(PROPERTY_KEY, dontShowVersion);
        log.debug("Preference changed for user '{}' : don't-show version is {}", new Object[]{user.getName(), dontShowVersion});
    }

    private boolean hasNeverLoggedInBefore(User user) {
        LoginInfo loginInfo = this.loginManager.getLoginInfo(user);
        return loginInfo == null || loginInfo.getPreviousSuccessfulLoginDate() == null;
    }

    private boolean isShownForVersions(String seenVersion, String currentVersion) {
        if (seenVersion == null || VERSION_UNSET.equals(seenVersion)) {
            return true;
        }
        return !Objects.equal((Object)this.extractMajorVersion(currentVersion), (Object)this.extractMajorVersion(seenVersion));
    }

    @Nullable
    private String extractMajorVersion(String version) {
        Matcher matcher = MAJOR_VERSION_PATTERN.matcher(version);
        matcher.matches();
        return matcher.group(1);
    }

    private String getCurrentVersion() {
        return this.applicationProperties.getVersion();
    }

    private UserPreferences getUserPreferences(User user) {
        if (user == null) {
            return null;
        }
        UserPreferences userPreferences = this.userAccessor.getUserPreferences(user);
        if (userPreferences == null) {
            log.warn("Unable to set shownForUser preference for user: {}", (Object)user);
        }
        return userPreferences;
    }
}

