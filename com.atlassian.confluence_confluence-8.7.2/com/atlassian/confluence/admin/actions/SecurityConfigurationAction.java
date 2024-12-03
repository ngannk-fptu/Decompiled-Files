/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.util.PairType
 *  com.atlassian.event.Event
 *  com.atlassian.sal.api.websudo.WebSudoRequired
 *  com.atlassian.spring.container.ContainerManager
 *  com.atlassian.user.User
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.admin.actions;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.core.FormAware;
import com.atlassian.confluence.event.events.admin.GlobalSettingsChangedEvent;
import com.atlassian.confluence.impl.security.AdminOnly;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.servlet.download.AttachmentSecurityLevel;
import com.atlassian.confluence.setup.settings.Settings;
import com.atlassian.confluence.setup.settings.beans.LoginManagerSettings;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.user.AuthenticatorOverwrite;
import com.atlassian.confluence.util.HTMLPairType;
import com.atlassian.core.util.PairType;
import com.atlassian.event.Event;
import com.atlassian.sal.api.websudo.WebSudoRequired;
import com.atlassian.spring.container.ContainerManager;
import com.atlassian.user.User;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebSudoRequired
@AdminOnly
public class SecurityConfigurationAction
extends ConfluenceActionSupport
implements FormAware {
    private static final Logger log = LoggerFactory.getLogger(SecurityConfigurationAction.class);
    private SpaceManager spaceManager;
    private boolean externalUserManagement;
    private boolean addWildcardsToUserAndGroupSearches;
    private boolean nofollowExternalLinks;
    private String emailAddressVisibility;
    private boolean allowRemoteApiAnonymous;
    private boolean enableSpaceStyles;
    private int maxRssItems;
    private boolean showSystemInfoIn500Page;
    private boolean enableElevatedSecurityCheck;
    private int loginAttemptsThreshold;
    private boolean webSudoEnabled;
    private long webSudoTimeout;
    private boolean xsrfAddComments;
    private AttachmentSecurityLevel attachmentSecurityLevel;
    private List<HTMLPairType> emailAddressVisibilityTypes;
    private boolean editMode = true;
    private int rssTimeout;
    private int pageTimeout;

    @Override
    public boolean isPermitted() {
        boolean isPermitted = this.permissionManager.hasPermission((User)this.getAuthenticatedUser(), Permission.ADMINISTER, PermissionManager.TARGET_APPLICATION);
        log.debug("is permitted: {}", (Object)isPermitted);
        return isPermitted;
    }

    @PermittedMethods(value={HttpMethod.GET})
    public String doView() throws Exception {
        this.editMode = false;
        return this.doDefault();
    }

    @Override
    public String doDefault() throws Exception {
        this.allowRemoteApiAnonymous = this.getGlobalSettings().isAllowRemoteApiAnonymous();
        this.enableSpaceStyles = this.getGlobalSettings().isEnableSpaceStyles();
        this.externalUserManagement = this.getGlobalSettings().isExternalUserManagement();
        this.emailAddressVisibility = this.getGlobalSettings().getEmailAddressVisibility();
        this.maxRssItems = this.getGlobalSettings().getMaxRssItems();
        this.rssTimeout = this.getGlobalSettings().getRssTimeout();
        this.pageTimeout = this.getGlobalSettings().getPageTimeout();
        this.showSystemInfoIn500Page = this.getGlobalSettings().isShowSystemInfoIn500Page();
        this.nofollowExternalLinks = this.getGlobalSettings().isNofollowExternalLinks();
        this.addWildcardsToUserAndGroupSearches = this.getGlobalSettings().isAddWildcardsToUserAndGroupSearches();
        this.xsrfAddComments = this.getGlobalSettings().isXsrfAddComments();
        this.webSudoTimeout = this.getGlobalSettings().getWebSudoTimeout();
        this.webSudoEnabled = this.getGlobalSettings().getWebSudoEnabled();
        this.attachmentSecurityLevel = this.getGlobalSettings().getAttachmentSecurityLevel();
        LoginManagerSettings lms = this.getGlobalSettings().getLoginManagerSettings();
        this.enableElevatedSecurityCheck = lms.isEnableElevatedSecurityCheck();
        this.loginAttemptsThreshold = lms.getLoginAttemptsThreshold();
        return super.doDefault();
    }

    private void validation() {
        if (this.isSystemAdmin()) {
            if (this.getMaxRssItems() <= 0) {
                this.addFieldError("maxRssItems", this.getText("rss.max.items.outofrange"));
            }
            if (this.getRssTimeout() <= 0) {
                this.addFieldError("rssTimeout", this.getText("rss.max.time.invalid"));
            }
            if (this.getPageTimeout() <= 0) {
                this.addFieldError("pageTimeout", this.getText("page.render.max.time.invalid"));
            }
            if (this.isPassConfirmationConfigurable()) {
                if (this.getLoginAttemptsThreshold() < 1) {
                    this.addFieldError("loginAttemptsThreshold", this.getText("error.login.elevatedsecurity.loginAttemptsThreshold.outofrange"));
                }
                if (this.isWebSudoEnabled() && this.getWebSudoTimeout() < 1L) {
                    this.addFieldError("webSudoTimeout", this.getText("websudo.error.outofrange"));
                }
            }
        }
    }

    public String execute() throws Exception {
        log.debug("validating...");
        this.validation();
        if (this.hasErrors()) {
            log.debug("Security config not altered due to errors actionErrors: {}, fieldErrors: {}", (Object)this.getActionErrors(), (Object)this.getFieldErrors());
            return "error";
        }
        Settings originalSettings = this.settingsManager.getGlobalSettings();
        String oldDomainName = this.settingsManager.getGlobalSettings().getBaseUrl();
        this.saveSetupOptions();
        GlobalSettingsChangedEvent event = new GlobalSettingsChangedEvent(this, originalSettings, this.settingsManager.getGlobalSettings(), oldDomainName, this.settingsManager.getGlobalSettings().getBaseUrl(), GlobalSettingsChangedEvent.Type.SECURITY);
        this.eventManager.publishEvent((Event)event);
        return "success";
    }

    private void saveSetupOptions() {
        Settings settings = new Settings(this.getGlobalSettings());
        settings.setAllowRemoteApiAnonymous(this.allowRemoteApiAnonymous);
        settings.setNofollowExternalLinks(this.nofollowExternalLinks);
        settings.setEmailAddressVisibility(this.emailAddressVisibility);
        if (this.isSystemAdmin()) {
            settings.setExternalUserManagement(this.externalUserManagement);
            settings.setMaxRssItems(this.maxRssItems);
            settings.setRssTimeout(this.rssTimeout);
            settings.setPageTimeout(this.pageTimeout);
            settings.setAddWildcardsToUserAndGroupSearches(this.addWildcardsToUserAndGroupSearches);
            settings.setXsrfAddComments(this.xsrfAddComments);
            settings.setAttachmentSecurityLevel(this.attachmentSecurityLevel);
            settings.setEnableSpaceStyles(this.enableSpaceStyles);
            settings.setShowSystemInfoIn500Page(this.showSystemInfoIn500Page);
            LoginManagerSettings loginManagerSettings = settings.getLoginManagerSettings();
            if (this.isPassConfirmationConfigurable()) {
                settings.setWebSudoEnabled(this.webSudoEnabled);
                settings.setWebSudoTimeout(this.webSudoTimeout);
                loginManagerSettings.setEnableElevatedSecurityCheck(this.enableElevatedSecurityCheck);
                loginManagerSettings.setLoginAttemptsThreshold(this.loginAttemptsThreshold);
            }
        }
        log.debug("saving settings");
        this.settingsManager.updateGlobalSettings(settings);
        log.debug("settings saved");
    }

    public boolean isSystemAdmin() {
        if (this.permissionManager == null) {
            this.permissionManager = (PermissionManager)ContainerManager.getComponent((String)"permissionManager");
        }
        return this.permissionManager.hasPermission((User)this.getAuthenticatedUser(), Permission.ADMINISTER, PermissionManager.TARGET_SYSTEM);
    }

    public SpaceManager getSpaceManager() {
        return this.spaceManager;
    }

    public void setSpaceManager(SpaceManager spaceManager) {
        this.spaceManager = spaceManager;
    }

    public List<HTMLPairType> getEmailAddressVisibilityTypes() {
        if (this.emailAddressVisibilityTypes == null) {
            this.emailAddressVisibilityTypes = new ArrayList<HTMLPairType>();
            this.emailAddressVisibilityTypes.add(new HTMLPairType("email.address.public", this.getText("email.address.public")));
            this.emailAddressVisibilityTypes.add(new HTMLPairType("email.address.masked", this.getText("email.address.masked")));
            this.emailAddressVisibilityTypes.add(new HTMLPairType("email.address.private", this.getText("email.address.private")));
        }
        return this.emailAddressVisibilityTypes;
    }

    public boolean isPassConfirmationConfigurable() {
        return !AuthenticatorOverwrite.isPasswordConfirmationDisabled();
    }

    public boolean getExternalUserManagement() {
        return this.externalUserManagement;
    }

    public boolean isExternalUserManagement() {
        return this.externalUserManagement;
    }

    public void setExternalUserManagement(boolean externalUserManagement) {
        this.externalUserManagement = externalUserManagement;
    }

    public String getEmailAddressVisibility() {
        return this.emailAddressVisibility;
    }

    public void setEmailAddressVisibility(String emailAddressVisibility) {
        this.emailAddressVisibility = emailAddressVisibility;
    }

    public boolean isAllowRemoteApiAnonymous() {
        return this.allowRemoteApiAnonymous;
    }

    public void setAllowRemoteApiAnonymous(boolean allowRemoteApiAnonymous) {
        this.allowRemoteApiAnonymous = allowRemoteApiAnonymous;
    }

    public boolean isNofollowExternalLinks() {
        return this.nofollowExternalLinks;
    }

    public void setNofollowExternalLinks(boolean nofollowExternalLinks) {
        this.nofollowExternalLinks = nofollowExternalLinks;
    }

    public boolean isEnableSpaceStyles() {
        return this.enableSpaceStyles;
    }

    public void setEnableSpaceStyles(boolean enableSpaceStyles) {
        this.enableSpaceStyles = enableSpaceStyles;
    }

    public boolean isShowSystemInfoIn500Page() {
        return this.showSystemInfoIn500Page;
    }

    public void setShowSystemInfoIn500Page(boolean showSystemInfoIn500Page) {
        this.showSystemInfoIn500Page = showSystemInfoIn500Page;
    }

    public int getMaxRssItems() {
        return this.maxRssItems;
    }

    public void setMaxRssItems(int maxRssItems) {
        this.maxRssItems = maxRssItems;
    }

    public boolean isXsrfAddComments() {
        return this.xsrfAddComments;
    }

    public void setXsrfAddComments(boolean xsrfAddComments) {
        this.xsrfAddComments = xsrfAddComments;
    }

    public boolean isAddWildcardsToUserAndGroupSearches() {
        return this.addWildcardsToUserAndGroupSearches;
    }

    public void setAddWildcardsToUserAndGroupSearches(boolean addWildcardsToUserAndGroupSearches) {
        this.addWildcardsToUserAndGroupSearches = addWildcardsToUserAndGroupSearches;
    }

    public boolean isEnableElevatedSecurityCheck() {
        return this.enableElevatedSecurityCheck;
    }

    public void setEnableElevatedSecurityCheck(boolean enableElevatedSecurityCheck) {
        this.enableElevatedSecurityCheck = enableElevatedSecurityCheck;
    }

    public int getLoginAttemptsThreshold() {
        return this.loginAttemptsThreshold;
    }

    public void setLoginAttemptsThreshold(int loginAttemptsThreshold) {
        this.loginAttemptsThreshold = loginAttemptsThreshold;
    }

    public boolean isWebSudoEnabled() {
        return this.webSudoEnabled;
    }

    public void setWebSudoEnabled(boolean webSudoEnabled) {
        this.webSudoEnabled = webSudoEnabled;
    }

    public long getWebSudoTimeout() {
        return this.webSudoTimeout;
    }

    public void setWebSudoTimeout(long webSudoTimeout) {
        this.webSudoTimeout = webSudoTimeout;
    }

    @Override
    public boolean isEditMode() {
        return this.editMode;
    }

    public int getRssTimeout() {
        return this.rssTimeout;
    }

    public void setRssTimeout(int rssTimeout) {
        this.rssTimeout = rssTimeout;
    }

    public int getPageTimeout() {
        return this.pageTimeout;
    }

    public void setPageTimeout(int pageTimeout) {
        this.pageTimeout = pageTimeout;
    }

    public List<PairType> getAttachmentSecurityLevels() {
        ArrayList<PairType> levels = new ArrayList<PairType>();
        for (AttachmentSecurityLevel level : AttachmentSecurityLevel.values()) {
            String key = level.name().toLowerCase();
            levels.add(new PairType((Serializable)((Object)key), (Serializable)((Object)this.getAttachmentSecurityLevelDisplay(key))));
        }
        return levels;
    }

    public String getAttachmentSecurityLevel() {
        return this.attachmentSecurityLevel.getLevel();
    }

    public String getAttachmentSecurityLevelDisplay() {
        return this.getAttachmentSecurityLevelDisplay(this.attachmentSecurityLevel.getLevel());
    }

    private String getAttachmentSecurityLevelDisplay(String level) {
        return this.getText("attachment.security.level.option." + level);
    }

    public void setAttachmentSecurityLevel(String attachmentSecurityLevel) {
        this.attachmentSecurityLevel = AttachmentSecurityLevel.fromLevel(attachmentSecurityLevel);
    }
}

