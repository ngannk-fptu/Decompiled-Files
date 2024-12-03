/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.setup.settings;

import com.atlassian.confluence.content.render.image.ImageDimensions;
import com.atlassian.confluence.core.TimeZone;
import com.atlassian.confluence.pages.thumbnail.Dimensions;
import com.atlassian.confluence.search.SearchLanguage;
import com.atlassian.confluence.servlet.download.AttachmentSecurityLevel;
import com.atlassian.confluence.setup.settings.CustomHtmlSettings;
import com.atlassian.confluence.setup.settings.beans.CaptchaSettings;
import com.atlassian.confluence.setup.settings.beans.ColourSchemesSettings;
import com.atlassian.confluence.setup.settings.beans.LoginManagerSettings;
import com.atlassian.confluence.setup.settings.beans.ReferrerSettings;
import com.atlassian.confluence.user.AuthenticatorOverwrite;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.confluence.util.http.ConfluenceHttpParameters;
import com.atlassian.confluence.web.context.StaticHttpContext;
import java.io.Serializable;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Settings
implements Serializable {
    private static final long serialVersionUID = 1771974922537355930L;
    volatile boolean doNotSave = false;
    private static final Logger log = LoggerFactory.getLogger(Settings.class);
    public static final String DEFAULT_SITE_TITLE = "Confluence";
    public static final String FALLBACK_DEFAULT_USERS_GROUP = "confluence-users";
    public static final String DEFAULT_USERS_GROUP_ENV_VAR_KEY = "confluence.ondemand.default.user.group";
    private volatile boolean allowCamelCase = false;
    private volatile boolean allowTrackbacks = false;
    private volatile boolean allowThreadedComments = true;
    private volatile boolean externalUserManagement = false;
    private volatile boolean denyPublicSignup = true;
    private volatile boolean emailAdminMessageOff = false;
    private volatile boolean almostSupportPeriodEndMessageOff = false;
    private volatile boolean senMissingInLicenseMessageOff = true;
    private volatile boolean baseUrlAdminMessageOff = false;
    private volatile boolean allowRemoteApi = false;
    private volatile boolean allowRemoteApiAnonymous = false;
    private volatile boolean antiXssMode = true;
    private volatile boolean gzippingResponse = true;
    private volatile boolean disableLogo = false;
    private volatile boolean sharedMode = false;
    private volatile boolean enableDidYouMean = false;
    private volatile boolean enableQuickNav = true;
    private volatile boolean enableSpaceStyles = false;
    private volatile boolean enableOpenSearch = true;
    private volatile boolean showSystemInfoIn500Page = false;
    private volatile boolean showApplicationTitle = false;
    private volatile ReferrerSettings referrerSettings;
    private volatile CaptchaSettings captchaSettings = new CaptchaSettings();
    private volatile CustomHtmlSettings customHtmlSettings = new CustomHtmlSettings();
    private volatile ColourSchemesSettings colourSchemesSettings = new ColourSchemesSettings("custom");
    private volatile LoginManagerSettings loginManagerSettings = new LoginManagerSettings();
    private volatile ConfluenceHttpParameters confluenceHttpParameters = new ConfluenceHttpParameters();
    private volatile long attachmentMaxSize = 0x6400000L;
    private volatile int auditLogRetentionNumber = 3;
    private volatile String auditLogRetentionUnit = ChronoUnit.YEARS.toString();
    private volatile int draftSaveInterval = 30000;
    private volatile int maxAttachmentsInUI = 5;
    private volatile String siteHomePage;
    private volatile String siteTitle = "Confluence";
    private volatile String siteWelcomeMessage;
    private volatile String documentationUrlPattern = "http://docs.atlassian.com/confluence/docs-{0}/{1}";
    private volatile String customContactMessage;
    private volatile boolean showContactAdministratorsForm = true;
    private volatile String emailAddressVisibility = "email.address.public";
    private volatile String defaultEncoding = "UTF-8";
    private volatile int maxThumbHeight = 300;
    private volatile int maxThumbWidth = 300;
    private volatile boolean backupAttachmentsDaily = true;
    private volatile boolean backupDaily = true;
    private volatile String backupPath;
    private volatile boolean nofollowExternalLinks = true;
    private volatile String indexingLanguage;
    private volatile String globalDefaultLocale;
    private volatile String dailyBackupFilePrefix;
    private volatile String dailyBackupDateFormatPattern;
    private volatile String supportRequestEmail;
    private volatile String defaultSpaceHomepageTitle;
    private volatile String defaultSpaceHomepageContent;
    private volatile String defaultPersonalSpaceHomepageContent;
    private volatile String baseUrl;
    private volatile String attachmentDataStore;
    private volatile boolean displayLinkIcons;
    private volatile boolean addWildcardsToUserAndGroupSearches;
    private volatile boolean xsrfAddComments;
    private volatile long webSudoTimeout;
    private volatile boolean webSudoEnabled;
    private volatile String ignoredAdminTasks;
    private volatile String defaultUsersGroup;
    private volatile String attachmentSecurityLevel;
    private volatile String defaultTimezoneId;
    private boolean enableJavascriptTop;
    private boolean supportPeriodEndMessageOff;
    private boolean enableWysiwyg;
    private boolean useWysiwygByDefault;
    private int numberOfBreadcrumbAncestors;
    private boolean viewSpaceGoesToSpaceSummary;
    private volatile String webdavServerUrl;
    private volatile String webdavUsername;
    private volatile String webdavPassword;
    private boolean enableLikes;
    private String contentLookAndFeelSettings;
    private int currentIndexVersion;
    private volatile boolean maintenanceBannerMessageOn;
    private volatile String maintenanceBannerMessage;
    private volatile int maxSimultaneousQuickNavRequests;
    private volatile int maxRssItems;
    private volatile int rssTimeout;
    private volatile int pageTimeout;
    public static final String DEFAULT_DEFAULT_ENCODING = "UTF-8";
    public static final String EMAIL_ADDRESS_PUBLIC = "email.address.public";
    public static final String EMAIL_ADDRESS_MASKED = "email.address.masked";
    public static final String EMAIL_ADDRESS_PRIVATE = "email.address.private";
    public static final String EMAIL_FROMNAME_DEFAULT = "${fullname} (Confluence)";
    public static final String DAILY_BACKUP_DIRECTORY = "backups";
    @Deprecated
    public static final String ENGLISH = "english";
    @Deprecated
    public static final String GERMAN = "german";
    @Deprecated
    public static final String RUSSIAN = "russian";
    @Deprecated
    public static final String CJK = "CJK";
    @Deprecated
    public static final String CUSTOM_JAPANESE = "custom-japanese";
    @Deprecated
    public static final String CHINESE = "chinese";
    @Deprecated
    public static final String FRENCH = "french";
    @Deprecated
    public static final String GREEK = "greek";
    @Deprecated
    public static final String BRAZILIAN = "brazilian";
    @Deprecated
    public static final String CZECH = "czech";
    public static final String LOCALE_ENGLISH = "en_GB";
    public static final String LOCALE_GERMAN = "de_DE";
    public static final String LOCALE_RUSSIAN = "ru_RU";
    public static final String LOCALE_JAPANESE = "ja_JP";

    public static Settings unsavableSettings() {
        Settings settings = new Settings();
        settings.doNotSave = true;
        return settings;
    }

    public Settings() {
        this.indexingLanguage = SearchLanguage.ENGLISH.value;
        this.globalDefaultLocale = LOCALE_ENGLISH;
        this.dailyBackupFilePrefix = "backup-";
        this.dailyBackupDateFormatPattern = "yyyy_MM_dd";
        this.supportRequestEmail = "confluence-autosupportrequests@atlassian.com";
        this.defaultSpaceHomepageTitle = "Home";
        this.defaultSpaceHomepageContent = null;
        this.defaultPersonalSpaceHomepageContent = null;
        this.attachmentDataStore = "file.system.based.attachments.storage";
        this.displayLinkIcons = false;
        this.addWildcardsToUserAndGroupSearches = true;
        this.xsrfAddComments = true;
        this.webSudoTimeout = 10L;
        this.webSudoEnabled = true;
        this.attachmentSecurityLevel = AttachmentSecurityLevel.SMART.getLevel();
        this.enableJavascriptTop = true;
        this.supportPeriodEndMessageOff = false;
        this.enableWysiwyg = true;
        this.useWysiwygByDefault = true;
        this.numberOfBreadcrumbAncestors = 1;
        this.viewSpaceGoesToSpaceSummary = false;
        this.maintenanceBannerMessageOn = false;
        this.maxSimultaneousQuickNavRequests = 40;
        this.maxRssItems = 200;
        this.rssTimeout = 60;
        this.pageTimeout = 120;
    }

    public Settings(Settings settings) {
        this.indexingLanguage = SearchLanguage.ENGLISH.value;
        this.globalDefaultLocale = LOCALE_ENGLISH;
        this.dailyBackupFilePrefix = "backup-";
        this.dailyBackupDateFormatPattern = "yyyy_MM_dd";
        this.supportRequestEmail = "confluence-autosupportrequests@atlassian.com";
        this.defaultSpaceHomepageTitle = "Home";
        this.defaultSpaceHomepageContent = null;
        this.defaultPersonalSpaceHomepageContent = null;
        this.attachmentDataStore = "file.system.based.attachments.storage";
        this.displayLinkIcons = false;
        this.addWildcardsToUserAndGroupSearches = true;
        this.xsrfAddComments = true;
        this.webSudoTimeout = 10L;
        this.webSudoEnabled = true;
        this.attachmentSecurityLevel = AttachmentSecurityLevel.SMART.getLevel();
        this.enableJavascriptTop = true;
        this.supportPeriodEndMessageOff = false;
        this.enableWysiwyg = true;
        this.useWysiwygByDefault = true;
        this.numberOfBreadcrumbAncestors = 1;
        this.viewSpaceGoesToSpaceSummary = false;
        this.maintenanceBannerMessageOn = false;
        this.maxSimultaneousQuickNavRequests = 40;
        this.maxRssItems = 200;
        this.rssTimeout = 60;
        this.pageTimeout = 120;
        this.allowCamelCase = settings.allowCamelCase;
        this.allowThreadedComments = settings.allowThreadedComments;
        this.externalUserManagement = settings.externalUserManagement;
        this.denyPublicSignup = settings.denyPublicSignup;
        this.emailAdminMessageOff = settings.emailAdminMessageOff;
        this.almostSupportPeriodEndMessageOff = settings.almostSupportPeriodEndMessageOff;
        this.baseUrlAdminMessageOff = settings.baseUrlAdminMessageOff;
        this.maintenanceBannerMessageOn = settings.maintenanceBannerMessageOn;
        this.maintenanceBannerMessage = settings.maintenanceBannerMessage;
        this.allowRemoteApi = settings.allowRemoteApi;
        this.allowRemoteApiAnonymous = settings.allowRemoteApiAnonymous;
        this.antiXssMode = settings.antiXssMode;
        this.gzippingResponse = settings.gzippingResponse;
        this.disableLogo = settings.disableLogo;
        this.showApplicationTitle = settings.showApplicationTitle;
        this.customHtmlSettings = new CustomHtmlSettings(settings.getCustomHtmlSettings());
        this.attachmentMaxSize = settings.attachmentMaxSize;
        this.siteHomePage = settings.siteHomePage;
        this.siteTitle = settings.siteTitle;
        this.siteWelcomeMessage = settings.siteWelcomeMessage;
        this.customContactMessage = settings.customContactMessage;
        this.showContactAdministratorsForm = settings.showContactAdministratorsForm;
        this.emailAddressVisibility = settings.emailAddressVisibility;
        this.defaultEncoding = settings.defaultEncoding;
        this.backupAttachmentsDaily = settings.backupAttachmentsDaily;
        this.nofollowExternalLinks = settings.nofollowExternalLinks;
        this.indexingLanguage = settings.indexingLanguage;
        this.dailyBackupFilePrefix = settings.dailyBackupFilePrefix;
        this.dailyBackupDateFormatPattern = settings.dailyBackupDateFormatPattern;
        this.supportRequestEmail = settings.supportRequestEmail;
        this.defaultSpaceHomepageTitle = settings.defaultSpaceHomepageTitle;
        this.defaultSpaceHomepageContent = settings.defaultSpaceHomepageContent;
        this.defaultPersonalSpaceHomepageContent = settings.defaultPersonalSpaceHomepageContent;
        this.captchaSettings = new CaptchaSettings(settings.getCaptchaSettings());
        this.colourSchemesSettings = new ColourSchemesSettings(settings.getColourSchemesSettings());
        this.baseUrl = settings.getBaseUrl();
        this.attachmentDataStore = settings.getAttachmentDataStore();
        this.globalDefaultLocale = settings.getGlobalDefaultLocale();
        this.sharedMode = settings.sharedMode;
        this.displayLinkIcons = settings.displayLinkIcons;
        this.enableSpaceStyles = settings.enableSpaceStyles;
        this.maxSimultaneousQuickNavRequests = settings.maxSimultaneousQuickNavRequests;
        this.enableQuickNav = settings.enableQuickNav;
        this.enableOpenSearch = settings.enableOpenSearch;
        this.enableJavascriptTop = settings.enableJavascriptTop;
        this.maxRssItems = settings.maxRssItems;
        this.rssTimeout = settings.rssTimeout;
        this.pageTimeout = settings.pageTimeout;
        this.backupDaily = settings.backupDaily;
        this.backupPath = settings.backupPath;
        this.addWildcardsToUserAndGroupSearches = settings.addWildcardsToUserAndGroupSearches;
        this.senMissingInLicenseMessageOff = settings.senMissingInLicenseMessageOff;
        this.showSystemInfoIn500Page = settings.showSystemInfoIn500Page;
        this.loginManagerSettings = new LoginManagerSettings(settings.getLoginManagerSettings());
        this.xsrfAddComments = settings.xsrfAddComments;
        this.ignoredAdminTasks = settings.ignoredAdminTasks;
        this.defaultUsersGroup = settings.defaultUsersGroup;
        this.defaultTimezoneId = settings.defaultTimezoneId;
        this.auditLogRetentionNumber = settings.auditLogRetentionNumber;
        this.auditLogRetentionUnit = settings.auditLogRetentionUnit;
        this.draftSaveInterval = settings.draftSaveInterval;
        this.maxAttachmentsInUI = settings.maxAttachmentsInUI;
        this.webSudoEnabled = settings.webSudoEnabled;
        this.webSudoTimeout = settings.webSudoTimeout;
        this.attachmentSecurityLevel = settings.attachmentSecurityLevel;
        this.confluenceHttpParameters = new ConfluenceHttpParameters(settings.getConfluenceHttpParameters());
    }

    public ConfluenceHttpParameters getConfluenceHttpParameters() {
        return this.confluenceHttpParameters;
    }

    public void setConfluenceHttpParameters(ConfluenceHttpParameters confluenceHttpParameters) {
        this.confluenceHttpParameters = confluenceHttpParameters;
    }

    public boolean isAllowCamelCase() {
        return this.allowCamelCase;
    }

    public void setAllowCamelCase(boolean allowCamelCase) {
        this.allowCamelCase = allowCamelCase;
    }

    @Deprecated
    public boolean isAllowTrackbacks() {
        return false;
    }

    @Deprecated
    public void setAllowTrackbacks(boolean allowTrackbacks) {
    }

    public boolean isAllowThreadedComments() {
        return this.allowThreadedComments;
    }

    public void setAllowThreadedComments(boolean allowThreadedComments) {
        this.allowThreadedComments = allowThreadedComments;
    }

    @Deprecated
    public void setViewSpaceGoesToSpaceSummary(boolean viewSpaceGoesToSpaceSummary) {
    }

    public boolean isExternalUserManagement() {
        return this.externalUserManagement;
    }

    public void setExternalUserManagement(boolean externalUserManagement) {
        this.externalUserManagement = externalUserManagement;
    }

    public boolean isDenyPublicSignup() {
        return this.denyPublicSignup;
    }

    public void setDenyPublicSignup(boolean denyPublicSignup) {
        this.denyPublicSignup = denyPublicSignup;
    }

    @Deprecated
    public ReferrerSettings getReferrerSettings() {
        return new ReferrerSettings();
    }

    @Deprecated
    public void setReferrerSettings(ReferrerSettings referrerSettings) {
    }

    public CaptchaSettings getCaptchaSettings() {
        if (this.captchaSettings == null) {
            this.captchaSettings = new CaptchaSettings();
        }
        return this.captchaSettings;
    }

    public void setCaptchaSettings(CaptchaSettings captchaSettings) {
        this.captchaSettings = captchaSettings;
    }

    public String getSiteHomePage() {
        return this.siteHomePage;
    }

    public void setSiteHomePage(String siteHomePage) {
        this.siteHomePage = siteHomePage;
    }

    @Deprecated
    public String getSiteWelcomeMessage() {
        return this.siteWelcomeMessage;
    }

    @Deprecated
    public void setSiteWelcomeMessage(String siteWelcomeMessage) {
        this.siteWelcomeMessage = siteWelcomeMessage;
    }

    public String getCustomContactMessage() {
        return this.customContactMessage;
    }

    public void setCustomContactMessage(String customErrorMessage) {
        this.customContactMessage = customErrorMessage;
    }

    public boolean isShowContactAdministratorsForm() {
        return this.showContactAdministratorsForm;
    }

    public void setShowContactAdministratorsForm(boolean showContactAdministratorsForm) {
        this.showContactAdministratorsForm = showContactAdministratorsForm;
    }

    public String getEmailAddressVisibility() {
        return this.emailAddressVisibility;
    }

    public void setEmailAddressVisibility(String emailAddressVisibility) {
        this.emailAddressVisibility = emailAddressVisibility;
    }

    public boolean areEmailAddressesPrivate() {
        return EMAIL_ADDRESS_PRIVATE.equals(this.getEmailAddressVisibility());
    }

    public long getAttachmentMaxSize() {
        return this.attachmentMaxSize;
    }

    public void setAttachmentMaxSize(long attachmentMaxSize) {
        this.attachmentMaxSize = attachmentMaxSize;
    }

    public String getDefaultEncoding() {
        return this.defaultEncoding;
    }

    public void setDefaultEncoding(String defaultEncoding) {
        this.defaultEncoding = defaultEncoding;
    }

    public int getMaxThumbHeight() {
        return this.maxThumbHeight;
    }

    public int getMaxThumbWidth() {
        return this.maxThumbWidth;
    }

    @Deprecated
    public Dimensions getMaxThumbDimensions() {
        return new Dimensions(this.maxThumbWidth, this.maxThumbHeight);
    }

    public ImageDimensions getMaxThumbnailDimensions() {
        return new ImageDimensions(this.maxThumbWidth, this.maxThumbHeight);
    }

    public boolean isBackupAttachmentsDaily() {
        return this.backupAttachmentsDaily;
    }

    public void setBackupAttachmentsDaily(boolean backupAttachmentsDaily) {
        this.backupAttachmentsDaily = backupAttachmentsDaily;
    }

    public String getIndexingLanguage() {
        return this.indexingLanguage;
    }

    public void setIndexingLanguage(String indexingLanguage) {
        this.indexingLanguage = indexingLanguage;
    }

    public String getGlobalDefaultLocale() {
        return this.globalDefaultLocale;
    }

    public void setGlobalDefaultLocale(String localeString) {
        this.globalDefaultLocale = localeString;
    }

    public boolean isEmailAdminMessageOff() {
        return this.emailAdminMessageOff;
    }

    public void setEmailAdminMessageOff(boolean emailAdminMessageOff) {
        this.emailAdminMessageOff = emailAdminMessageOff;
    }

    public boolean isBaseUrlAdminMessageOff() {
        return this.baseUrlAdminMessageOff;
    }

    public void setBaseUrlAdminMessageOff(boolean baseUrlAdminMessageOff) {
        this.baseUrlAdminMessageOff = baseUrlAdminMessageOff;
    }

    public boolean isMaintenanceBannerMessageOn() {
        return this.maintenanceBannerMessageOn;
    }

    public void setMaintenanceBannerMessageOn(boolean maintenanceBannerMessageOn) {
        this.maintenanceBannerMessageOn = maintenanceBannerMessageOn;
    }

    public boolean isAlmostSupportPeriodEndMessageOff() {
        return this.almostSupportPeriodEndMessageOff;
    }

    public void setAlmostSupportPeriodEndMessageOff(boolean almostSupportPeriodEndMessageOff) {
        this.almostSupportPeriodEndMessageOff = almostSupportPeriodEndMessageOff;
    }

    public boolean isAllowRemoteApi() {
        return this.allowRemoteApi;
    }

    public void setAllowRemoteApi(boolean allowRemoteApi) {
        this.allowRemoteApi = allowRemoteApi;
    }

    public boolean isAllowRemoteApiAnonymous() {
        return this.allowRemoteApiAnonymous;
    }

    public void setAllowRemoteApiAnonymous(boolean allowRemoteApiAnonymous) {
        this.allowRemoteApiAnonymous = allowRemoteApiAnonymous;
    }

    public int getMaxAttachmentsInUI() {
        return this.maxAttachmentsInUI;
    }

    public void setMaxAttachmentsInUI(int maxAttachmentsInUI) {
        this.maxAttachmentsInUI = maxAttachmentsInUI;
    }

    public boolean isGzippingResponse() {
        return this.gzippingResponse;
    }

    public void setGzippingResponse(boolean gzippingResponse) {
        this.gzippingResponse = gzippingResponse;
    }

    public void setNofollowExternalLinks(boolean nofollowExternalLinks) {
        this.nofollowExternalLinks = nofollowExternalLinks;
    }

    public boolean isNofollowExternalLinks() {
        return this.nofollowExternalLinks;
    }

    public String getDailyBackupFilePrefix() {
        return this.dailyBackupFilePrefix;
    }

    public void setDailyBackupFilePrefix(String dailyBackupFilePrefix) {
        this.dailyBackupFilePrefix = dailyBackupFilePrefix;
    }

    public String getDailyBackupDateFormatPattern() {
        return this.dailyBackupDateFormatPattern;
    }

    public void setDailyBackupDateFormatPattern(String dailyBackupDateFormatPattern) {
        this.dailyBackupDateFormatPattern = dailyBackupDateFormatPattern;
    }

    public String getSiteTitle() {
        return this.siteTitle;
    }

    public void setSiteTitle(String siteTitle) {
        this.siteTitle = siteTitle;
    }

    @Deprecated
    public void setMaxThumbHeight(int maxThumbHeight) {
    }

    @Deprecated
    public void setMaxThumbWidth(int maxThumbWidth) {
    }

    @Deprecated
    public void setEnableWysiwyg(boolean enableWysiwyg) {
    }

    @Deprecated
    public void setUseWysiwygByDefault(boolean useWysiwygByDefault) {
    }

    @Deprecated
    public void setNumberOfBreadcrumbAncestors(int numberOfBreadcrumbAncestors) {
    }

    public boolean isDisableLogo() {
        return this.disableLogo;
    }

    public void setDisableLogo(boolean disableLogo) {
        this.disableLogo = disableLogo;
    }

    public boolean showApplicationTitle() {
        return this.showApplicationTitle;
    }

    public void setShowApplicationTitle(boolean showApplicationTitle) {
        this.showApplicationTitle = showApplicationTitle;
    }

    public int getDraftSaveInterval() {
        return this.draftSaveInterval;
    }

    public void setDraftSaveInterval(int draftSaveInterval) {
        this.draftSaveInterval = draftSaveInterval;
    }

    public CustomHtmlSettings getCustomHtmlSettings() {
        return this.customHtmlSettings;
    }

    public void setCustomHtmlSettings(CustomHtmlSettings customHtmlSettings) {
        this.customHtmlSettings = customHtmlSettings;
    }

    public ColourSchemesSettings getColourSchemesSettings() {
        if (this.colourSchemesSettings == null) {
            this.colourSchemesSettings = new ColourSchemesSettings("custom");
        }
        return this.colourSchemesSettings;
    }

    public void setColourSchemesSettings(ColourSchemesSettings colourSchemesSettings) {
        this.colourSchemesSettings = colourSchemesSettings;
    }

    @Deprecated
    public TimeZone getTimeZone() {
        return TimeZone.getDefault();
    }

    public String getDefaultSpaceHomepageTitle() {
        return this.defaultSpaceHomepageTitle;
    }

    public void setDefaultSpaceHomepageTitle(String defaultSpaceHomepageTitle) {
        this.defaultSpaceHomepageTitle = defaultSpaceHomepageTitle;
    }

    @Deprecated
    public String getDefaultSpaceHomepageContent() {
        return this.defaultSpaceHomepageContent;
    }

    @Deprecated
    public void setDefaultSpaceHomepageContent(String defaultSpaceHomepageContent) {
        this.defaultSpaceHomepageContent = defaultSpaceHomepageContent;
    }

    public String getBaseUrl() {
        if (this.baseUrl == null) {
            String transientBaseUrl = GeneralUtil.lookupDomainName(new StaticHttpContext().getRequest());
            log.debug("baseUrl has not been set. Using transient value generated from current request [{}]", (Object)transientBaseUrl);
            return transientBaseUrl;
        }
        return this.baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        if (!Objects.equals(this.baseUrl, baseUrl)) {
            log.info("baseUrl changing from [{}] to [{}]", (Object)this.baseUrl, (Object)baseUrl);
            this.baseUrl = baseUrl;
        }
    }

    public String getAttachmentDataStore() {
        return this.attachmentDataStore;
    }

    public void setAttachmentDataStore(String attachmentDataStore) {
        this.attachmentDataStore = attachmentDataStore;
    }

    @Deprecated
    public void setWebdavServerUrl(String webdavServerUrl) {
    }

    @Deprecated
    public void setWebdavUsername(String webdavUsername) {
    }

    @Deprecated
    public void setWebdavPassword(String webdavPassword) {
    }

    public boolean isAntiXssMode() {
        return true;
    }

    @Deprecated
    public void setAntiXssMode(boolean antiXssMode) {
    }

    @Deprecated
    public void setSharedMode(boolean sharedMode) {
    }

    @Deprecated
    public String getDefaultPersonalSpaceHomepageContent() {
        return this.defaultPersonalSpaceHomepageContent;
    }

    @Deprecated
    public void setDisplayLinkIcons(boolean displayLinkIcons) {
    }

    @Deprecated
    public void setDefaultPersonalSpaceHomepageContent(String defaultPersonalSpaceHomepageContent) {
        this.defaultPersonalSpaceHomepageContent = defaultPersonalSpaceHomepageContent;
    }

    public String getSupportRequestEmail() {
        return this.supportRequestEmail;
    }

    public void setSupportRequestEmail(String supportRequestEmail) {
        this.supportRequestEmail = supportRequestEmail;
    }

    @Deprecated
    public void setEnableDidYouMean(boolean enableDidYouMean) {
    }

    public boolean isEnableSpaceStyles() {
        return this.enableSpaceStyles;
    }

    public void setEnableSpaceStyles(boolean enableSpaceStyles) {
        this.enableSpaceStyles = enableSpaceStyles;
    }

    public boolean isEnableQuickNav() {
        return this.enableQuickNav;
    }

    public void setEnableQuickNav(boolean enableQuickNav) {
        this.enableQuickNav = enableQuickNav;
    }

    public int getMaxSimultaneousQuickNavRequests() {
        return this.maxSimultaneousQuickNavRequests;
    }

    public void setMaxSimultaneousQuickNavRequests(int maxSimultaneousQuickNavRequests) {
        this.maxSimultaneousQuickNavRequests = maxSimultaneousQuickNavRequests;
    }

    public boolean isEnableOpenSearch() {
        return this.enableOpenSearch;
    }

    public void setEnableOpenSearch(boolean enableOpenSearch) {
        this.enableOpenSearch = enableOpenSearch;
    }

    @Deprecated
    public void setEnableJavascriptTop(boolean enableJavascriptTop) {
    }

    public int getMaxRssItems() {
        return this.maxRssItems;
    }

    public void setMaxRssItems(int maxRssItems) {
        this.maxRssItems = maxRssItems;
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

    public boolean isBackupDaily() {
        return this.backupDaily;
    }

    @Deprecated
    public void setBackupDaily(boolean backupDaily) {
        this.backupDaily = backupDaily;
    }

    public String getBackupPath() {
        return this.backupPath;
    }

    public void setBackupPath(String backupPath) {
        this.backupPath = backupPath;
    }

    public boolean isAddWildcardsToUserAndGroupSearches() {
        return this.addWildcardsToUserAndGroupSearches;
    }

    public void setAddWildcardsToUserAndGroupSearches(boolean addWildcardsToUserAndGroupSearches) {
        this.addWildcardsToUserAndGroupSearches = addWildcardsToUserAndGroupSearches;
    }

    public boolean isSaveable() {
        return !this.doNotSave;
    }

    public boolean isSenMissingInLicenseMessageOff() {
        return this.senMissingInLicenseMessageOff;
    }

    public void setSenMissingInLicenseMessageOff(boolean senMissingInLicenseMessageOff) {
        this.senMissingInLicenseMessageOff = senMissingInLicenseMessageOff;
    }

    public boolean isShowSystemInfoIn500Page() {
        return this.showSystemInfoIn500Page;
    }

    public void setShowSystemInfoIn500Page(boolean showSystemInfoIn500Page) {
        this.showSystemInfoIn500Page = showSystemInfoIn500Page;
    }

    public LoginManagerSettings getLoginManagerSettings() {
        if (null == this.loginManagerSettings) {
            this.loginManagerSettings = new LoginManagerSettings();
        }
        return this.loginManagerSettings;
    }

    public void setLoginManagerSettings(LoginManagerSettings loginManagerSettings) {
        this.loginManagerSettings = loginManagerSettings;
    }

    public boolean isXsrfAddComments() {
        return this.xsrfAddComments;
    }

    public void setXsrfAddComments(boolean xsrfAddComments) {
        this.xsrfAddComments = xsrfAddComments;
    }

    public long getWebSudoTimeout() {
        return this.webSudoTimeout;
    }

    public void setWebSudoTimeout(long webSudoTimeout) {
        this.webSudoTimeout = webSudoTimeout;
    }

    public boolean getWebSudoEnabled() {
        return this.webSudoEnabled && !AuthenticatorOverwrite.isPasswordConfirmationDisabled();
    }

    public void setWebSudoEnabled(boolean webSudoEnabled) {
        this.webSudoEnabled = webSudoEnabled;
    }

    public String getIgnoredAdminTasks() {
        return this.ignoredAdminTasks;
    }

    public void setIgnoredAdminTasks(String ignoredAdminTasks) {
        this.ignoredAdminTasks = ignoredAdminTasks;
    }

    public String getDefaultTimezoneId() {
        return this.defaultTimezoneId;
    }

    public void setDefaultTimezoneId(String defaultTimezoneId) {
        this.defaultTimezoneId = defaultTimezoneId;
    }

    public String getDefaultUsersGroup() {
        if (this.defaultUsersGroup == null) {
            this.defaultUsersGroup = System.getProperty(DEFAULT_USERS_GROUP_ENV_VAR_KEY, FALLBACK_DEFAULT_USERS_GROUP);
            if (!FALLBACK_DEFAULT_USERS_GROUP.equals(this.defaultUsersGroup)) {
                log.debug("Retrieved default users group from system property: " + this.defaultUsersGroup);
            }
        }
        return this.defaultUsersGroup;
    }

    public void setDefaultUsersGroup(String defaultUsersGroup) {
        this.defaultUsersGroup = defaultUsersGroup;
    }

    public AttachmentSecurityLevel getAttachmentSecurityLevel() {
        return AttachmentSecurityLevel.fromLevel(this.attachmentSecurityLevel);
    }

    public void setAttachmentSecurityLevel(AttachmentSecurityLevel attachmentSecurityLevel) {
        this.attachmentSecurityLevel = attachmentSecurityLevel.getLevel();
    }

    public int getAuditLogRetentionNumber() {
        return this.auditLogRetentionNumber;
    }

    public void setAuditLogRetentionNumber(int auditLogRetentionNumber) {
        this.auditLogRetentionNumber = auditLogRetentionNumber;
    }

    public String getAuditLogRetentionUnit() {
        return this.auditLogRetentionUnit;
    }

    public void setAuditLogRetentionUnit(String auditLogRetentionUnit) {
        this.auditLogRetentionUnit = auditLogRetentionUnit;
    }

    public String getMaintenanceBannerMessage() {
        return this.maintenanceBannerMessage;
    }

    public void setMaintenanceBannerMessage(String maintenanceBannerMessage) {
        this.maintenanceBannerMessage = maintenanceBannerMessage;
    }
}

