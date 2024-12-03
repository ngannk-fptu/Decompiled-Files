/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.upgrade.UpgradeManager
 *  com.atlassian.plugin.web.descriptors.WebItemModuleDescriptor
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.struts2.ServletActionContext
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.user.actions;

import com.atlassian.confluence.admin.criteria.WritableDirectoryExistsCriteria;
import com.atlassian.confluence.cluster.ClusterManager;
import com.atlassian.confluence.languages.BrowserLanguageUtils;
import com.atlassian.confluence.languages.Language;
import com.atlassian.confluence.languages.LocaleParser;
import com.atlassian.confluence.security.CaptchaManager;
import com.atlassian.confluence.setup.settings.DarkFeaturesManager;
import com.atlassian.confluence.upgrade.UpgradeManager;
import com.atlassian.confluence.user.SignupManager;
import com.atlassian.confluence.user.actions.AbstractUsersAction;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.confluence.util.MobileUtils;
import com.atlassian.confluence.util.UserAgentUtil;
import com.atlassian.plugin.web.descriptors.WebItemModuleDescriptor;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractLoginSignupAction
extends AbstractUsersAction {
    private static final Logger logger = LoggerFactory.getLogger(AbstractLoginSignupAction.class);
    private boolean fromNotPermitted = false;
    protected UpgradeManager upgradeManager = null;
    protected SignupManager signupManager;
    protected String token;
    private boolean logout;
    protected String os_destination;
    private ClusterManager clusterManager;
    private CaptchaManager captchaManager;
    private String language;
    private WritableDirectoryExistsCriteria writableDirectoryExistsCriteria;
    private DarkFeaturesManager darkFeaturesManager;

    public boolean isFromNotPermitted() {
        return this.fromNotPermitted;
    }

    public void setFromNotPermitted(boolean fromNotPermitted) {
        this.fromNotPermitted = fromNotPermitted;
    }

    public boolean isShowSignUp() {
        if (!this.userAccessor.isLicensedToAddMoreUsers()) {
            return false;
        }
        if (!this.upgradeManager.isUpgraded()) {
            return false;
        }
        if (!this.writableDirectoryExistsCriteria.isMet()) {
            return false;
        }
        if (this.isUnifiedUserManagementEnabled()) {
            return this.isUnifiedUserManagementSignUpEnabled();
        }
        return !this.settingsManager.getGlobalSettings().isDenyPublicSignup() || this.isValidToken();
    }

    private boolean isValidToken() {
        return !StringUtils.isBlank((CharSequence)this.token) && this.signupManager.canSignUpWith(this.token);
    }

    public String getSignupURL() {
        WebItemModuleDescriptor signupWebItem = AbstractLoginSignupAction.findSignupWebItem(this.webInterfaceManager.getDisplayableItems("system.user/anonymous", Collections.emptyMap()));
        if (signupWebItem != null) {
            return signupWebItem.getLink().getDisplayableUrl(ServletActionContext.getRequest(), signupWebItem.getContextProvider().getContextMap(Collections.emptyMap()));
        }
        return ServletActionContext.getRequest().getContextPath() + "/signup.action";
    }

    private static WebItemModuleDescriptor findSignupWebItem(Iterable<WebItemModuleDescriptor> webItems) {
        for (WebItemModuleDescriptor webItem : webItems) {
            if (!webItem.getKey().contains("signup")) continue;
            return webItem;
        }
        return null;
    }

    private boolean isUnifiedUserManagementEnabled() {
        return this.darkFeaturesManager.getSiteDarkFeatures().isFeatureEnabled("unified.usermanagement");
    }

    private boolean isUnifiedUserManagementSignUpEnabled() {
        for (WebItemModuleDescriptor webItem : this.webInterfaceManager.getDisplayableItems("system.user/anonymous", Collections.emptyMap())) {
            if (!webItem.getKey().equals("signup-um")) continue;
            return true;
        }
        return false;
    }

    @Override
    public String doDefault() throws Exception {
        if (this.language != null && this.languageManager.getLanguage(this.language) != null) {
            this.setI18NBean(this.i18NBeanFactory.getI18NBean(LocaleParser.toLocale(this.language)));
            this.getLocaleManager().setLanguage(this.language);
            GeneralUtil.setCookie("confluence-language", this.language);
        }
        return super.doDefault();
    }

    public UpgradeManager getUpgradeManager() {
        return this.upgradeManager;
    }

    public void setUpgradeManager(UpgradeManager upgradeManager) {
        this.upgradeManager = upgradeManager;
    }

    public SignupManager getSignupManager() {
        return this.signupManager;
    }

    public void setSignupManager(SignupManager signupManager) {
        this.signupManager = signupManager;
    }

    public String getToken() {
        return this.token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isLogout() {
        return this.logout;
    }

    public void setLogout(boolean logout) {
        this.logout = logout;
    }

    public String getOs_destination() {
        return this.os_destination;
    }

    public void setOs_destination(String os_destination) {
        this.os_destination = os_destination;
    }

    public void setClusterManager(ClusterManager clusterManager) {
        this.clusterManager = clusterManager;
    }

    public ClusterManager getClusterManager() {
        return this.clusterManager;
    }

    public void setCaptchaManager(CaptchaManager captchaManager) {
        this.captchaManager = captchaManager;
    }

    public CaptchaManager getCaptchaManager() {
        return this.captchaManager;
    }

    public String getCurrentYearAsString() {
        Calendar calendar = Calendar.getInstance();
        return String.valueOf(calendar.get(1));
    }

    public boolean isBrowserLanguageEnabled() {
        return BrowserLanguageUtils.isBrowserLanguageEnabled() && !this.isMobileAppWebView();
    }

    public String getLanguage() {
        return this.language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public void setWritableDirectoryExistsCriteria(WritableDirectoryExistsCriteria writableDirectoryExistsCriteria) {
        this.writableDirectoryExistsCriteria = writableDirectoryExistsCriteria;
    }

    public List<Language> getLanguages() {
        return this.languageManager.getLanguages();
    }

    public void setDarkFeaturesManager(DarkFeaturesManager darkFeaturesManager) {
        this.darkFeaturesManager = darkFeaturesManager;
    }

    public boolean shouldRememberMeCheckboxBeOmitted() {
        return this.clusterManager.isClustered() && !Boolean.getBoolean("cluster.login.rememberme.enabled") || this.isMobileAppWebView();
    }

    public boolean isMobileAppWebView() {
        return UserAgentUtil.isBrowserFamily(UserAgentUtil.BrowserFamily.ATLASSIAN_MOBILE);
    }

    public boolean shouldShowMobileBanner() {
        return MobileUtils.shouldShowBanner(ServletActionContext.getRequest());
    }
}

