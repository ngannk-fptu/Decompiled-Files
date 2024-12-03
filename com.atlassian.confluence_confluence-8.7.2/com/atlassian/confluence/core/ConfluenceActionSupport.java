/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.service.accessmode.AccessModeService
 *  com.atlassian.confluence.api.service.datetime.DateFormatService
 *  com.atlassian.confluence.api.service.people.PersonService
 *  com.atlassian.confluence.velocity.htmlsafe.HtmlSafe
 *  com.atlassian.event.Event
 *  com.atlassian.event.EventManager
 *  com.atlassian.fugue.Option
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.web.WebInterfaceManager
 *  com.atlassian.sal.api.timezone.TimeZoneManager
 *  com.atlassian.spring.container.ContainerManager
 *  com.atlassian.user.User
 *  com.google.common.base.Supplier
 *  com.opensymphony.util.TextUtils
 *  com.opensymphony.xwork2.ActionContext
 *  com.opensymphony.xwork2.ActionSupport
 *  com.opensymphony.xwork2.LocaleProvider
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpSession
 *  org.apache.commons.lang3.ArrayUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.struts2.ServletActionContext
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.core;

import com.atlassian.confluence.api.service.accessmode.AccessModeService;
import com.atlassian.confluence.api.service.datetime.DateFormatService;
import com.atlassian.confluence.api.service.people.PersonService;
import com.atlassian.confluence.cache.ThreadLocalCacheAccessor;
import com.atlassian.confluence.content.ui.ContentUiSupport;
import com.atlassian.confluence.core.ConfluenceSystemProperties;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.DateFormatter;
import com.atlassian.confluence.core.FormatSettingsManager;
import com.atlassian.confluence.core.TimeZone;
import com.atlassian.confluence.core.datetime.FriendlyDateFormatter;
import com.atlassian.confluence.core.datetime.RequestTimeThreadLocal;
import com.atlassian.confluence.event.events.security.NoConfluencePermissionEvent;
import com.atlassian.confluence.impl.plugin.web.readonly.ReadOnlyWebInterfaceManager;
import com.atlassian.confluence.impl.setup.BootstrapStatusProviderImpl;
import com.atlassian.confluence.labels.LabelManager;
import com.atlassian.confluence.labels.Labelable;
import com.atlassian.confluence.languages.Language;
import com.atlassian.confluence.languages.LanguageManager;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.actions.PageAware;
import com.atlassian.confluence.plugin.descriptor.web.DefaultWebInterfaceContext;
import com.atlassian.confluence.plugin.descriptor.web.WebInterface;
import com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.security.SpacePermissionManager;
import com.atlassian.confluence.security.access.ConfluenceAccessManager;
import com.atlassian.confluence.security.login.LoginInfo;
import com.atlassian.confluence.security.login.LoginManager;
import com.atlassian.confluence.setup.BootstrapManager;
import com.atlassian.confluence.setup.BootstrapStatusProvider;
import com.atlassian.confluence.setup.BuildInformation;
import com.atlassian.confluence.setup.settings.Settings;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.Spaced;
import com.atlassian.confluence.status.service.SystemInformationService;
import com.atlassian.confluence.themes.GlobalHelper;
import com.atlassian.confluence.themes.ThemeHelper;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.user.UserInterfaceState;
import com.atlassian.confluence.user.UsernameCacheKey;
import com.atlassian.confluence.user.actions.ProfilePictureInfo;
import com.atlassian.confluence.user.actions.UserAware;
import com.atlassian.confluence.user.history.UserHistory;
import com.atlassian.confluence.user.persistence.dao.compatibility.FindUserHelper;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.confluence.util.PlainTextToHtmlConverter;
import com.atlassian.confluence.util.i18n.DefaultI18NBeanFactory;
import com.atlassian.confluence.util.i18n.DocumentationBean;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.confluence.util.i18n.Message;
import com.atlassian.confluence.util.i18n.VersionSpecificDocumentationBean;
import com.atlassian.confluence.validation.MessageHolder;
import com.atlassian.confluence.validation.MessageHolderAware;
import com.atlassian.confluence.velocity.htmlsafe.HtmlSafe;
import com.atlassian.event.Event;
import com.atlassian.event.EventManager;
import com.atlassian.fugue.Option;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.web.WebInterfaceManager;
import com.atlassian.sal.api.timezone.TimeZoneManager;
import com.atlassian.spring.container.ContainerManager;
import com.atlassian.user.User;
import com.opensymphony.util.TextUtils;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.LocaleProvider;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.function.Supplier;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfluenceActionSupport
extends ActionSupport
implements LocaleProvider,
WebInterface,
MessageHolderAware {
    private static final Logger log = LoggerFactory.getLogger(ConfluenceActionSupport.class);
    public static final String CANCEL = "cancel";
    public static final String PREVIEW = "preview";
    public static final String LICENSE_EXPIRED = "licenseexpired";
    public static final String LICENSE_USERS_EXCEEDED = "licenseusersexceeded";
    public static final String DEVMODE = "confluence.devmode";
    private static final String USER_DEFAULT_DATE_PATTERN = "d MMM, yyyy";
    private Locale userLocale = null;
    protected UserAccessor userAccessor;
    protected EventManager eventManager;
    protected SpacePermissionManager spacePermissionManager;
    protected PermissionManager permissionManager;
    private ConfluenceAccessManager confluenceAccessManager;
    protected SettingsManager settingsManager;
    protected LabelManager labelManager;
    protected LanguageManager languageManager;
    private ContentUiSupport contentUiSupport;
    private LocaleManager localeManager;
    private I18NBean i18NBean;
    protected I18NBeanFactory i18NBeanFactory;
    protected MessageHolder messageHolder;
    private DocumentationBean docBean;
    protected WebInterfaceManager webInterfaceManager;
    private FormatSettingsManager formatSettingsManager;
    private SystemInformationService systemInformationService;
    private LoginManager loginManager;
    private TimeZoneManager timeZoneManager;
    protected boolean permitted = false;
    private String cancel;
    private boolean skipAccessCheck = false;
    @Deprecated(since="8.5.3")
    private BootstrapManager bootstrapManager;
    private BootstrapStatusProvider bootstrapStatusProvider;
    protected AccessModeService accessModeService;
    protected PersonService personService;
    private Date previousLoginDate;
    private UserInterfaceState userInterfaceState;
    private GlobalHelper globalHelper;
    private DateFormatter dateFormatter;
    private FriendlyDateFormatter friendlyDateFormatter;
    protected PluginAccessor pluginAccessor;
    private DateFormatService dateFormatService;
    private static final ThreadLocalCacheAccessor<UsernameCacheKey, Option<ConfluenceUser>> userCacheAccessor = ThreadLocalCacheAccessor.newInstance();
    private Supplier<HttpServletRequest> servletRequestSupplier = new XWorkServletRequestSupplier();

    public void setCancel(String cancel) {
        this.cancel = cancel;
    }

    public boolean isCanceled() {
        return StringUtils.isNotBlank((CharSequence)this.cancel);
    }

    protected String getCancel() {
        return this.cancel;
    }

    public String doDefault() throws Exception {
        return "input";
    }

    @HtmlSafe
    public String getText(String key) {
        return this.getText(key, Collections.EMPTY_LIST);
    }

    @HtmlSafe
    public String getText(String key, String defaultValue) {
        if (key == null) {
            return defaultValue;
        }
        String i18nValue = this.getText(key);
        if (StringUtils.isEmpty((CharSequence)i18nValue) || key.equals(i18nValue)) {
            return defaultValue;
        }
        return i18nValue;
    }

    @HtmlSafe
    public String getText(String key, String defaultValue, String obj) {
        if (key == null) {
            return defaultValue;
        }
        String i18nValue = this.getText(key, Arrays.asList(obj));
        if (StringUtils.isEmpty((CharSequence)i18nValue) || key.equals(i18nValue)) {
            return defaultValue;
        }
        return i18nValue;
    }

    @HtmlSafe
    public String getText(String key, Object[] args) {
        try {
            return this.getI18n().getText(key, args);
        }
        catch (RuntimeException e) {
            log.error("Error retrieving i18n text with key: " + key + " and arguments: " + ArrayUtils.toString((Object)args), (Throwable)e);
            return "Error retrieving text key: " + key;
        }
    }

    @HtmlSafe
    public String getText(String key, List list) {
        try {
            return this.getI18n().getText(key, list);
        }
        catch (RuntimeException e) {
            log.error("Error retrieving i18n text with key: " + key + " and arguments: " + list, (Throwable)e);
            return "Error retrieving text key: " + key;
        }
    }

    public String getText(String key, String defaultValue, List list) {
        if (key == null) {
            return defaultValue;
        }
        try {
            String i18nValue = this.getI18n().getText(key, list);
            if (StringUtils.isEmpty((CharSequence)i18nValue) || key.equals(i18nValue)) {
                return defaultValue;
            }
            return i18nValue;
        }
        catch (RuntimeException e) {
            log.error("Error retrieving i18n text with key: " + key + " and arguments: " + list, (Throwable)e);
            return "Error retrieving text key: " + key;
        }
    }

    public String getText(String key, String[] args) {
        try {
            return this.getI18n().getText(key, args);
        }
        catch (RuntimeException e) {
            log.error("Error retrieving i18n text with key: " + key + " and arguments: " + ArrayUtils.toString((Object)args), (Throwable)e);
            return "Error retrieving text key: " + key;
        }
    }

    public String getText(String key, String defaultValue, String[] args) {
        if (key == null) {
            return defaultValue;
        }
        try {
            String i18nValue = this.getI18n().getText(key, args);
            if (StringUtils.isEmpty((CharSequence)i18nValue) || key.equals(i18nValue)) {
                return defaultValue;
            }
            return i18nValue;
        }
        catch (RuntimeException e) {
            log.error("Error retrieving i18n text with key: " + key + " and arguments: " + ArrayUtils.toString((Object)args), (Throwable)e);
            return "Error retrieving text key: " + key;
        }
    }

    public String getActionName(String fullClassName) {
        String key = fullClassName + ".action.name";
        return this.getText(key);
    }

    public String getActionName() {
        return this.getActionName(this.getClass().getName());
    }

    @HtmlSafe
    public String getTextStrict(String key) {
        return this.getI18n().getTextStrict(key);
    }

    public boolean isPrintableVersion() {
        HttpServletRequest servletRequest = this.getCurrentRequest();
        if (servletRequest == null) {
            return false;
        }
        return TextUtils.noNull((String)servletRequest.getParameter("decorator")).equalsIgnoreCase("printable");
    }

    public boolean isPermitted() {
        if (!this.getBootstrapStatusProvider().isSetupComplete()) {
            return true;
        }
        if (!this.skipAccessCheck && !this.spacePermissionManager.hasPermission("USECONFLUENCE", null, this.getAuthenticatedUser())) {
            this.eventManager.publishEvent((Event)new NoConfluencePermissionEvent(this));
        }
        return this.spacePermissionManager.hasAllPermissions(this.getPermissionTypes(), null, this.getAuthenticatedUser());
    }

    protected List<String> getPermissionTypes() {
        ArrayList<String> permissions = new ArrayList<String>();
        if (!this.skipAccessCheck) {
            this.addPermissionTypeTo("USECONFLUENCE", permissions);
        }
        return permissions;
    }

    public final void useSkipAccessCheck(boolean skipAccessCheck) {
        this.skipAccessCheck = skipAccessCheck;
    }

    public boolean isSkipAccessCheck() {
        return this.skipAccessCheck;
    }

    @Deprecated
    public User getRemoteUser() {
        return this.getAuthenticatedUser();
    }

    public ConfluenceUser getAuthenticatedUser() {
        return AuthenticatedUserThreadLocal.get();
    }

    public ConfluenceUser getUserByName(String username) {
        UsernameCacheKey usernameCacheKey = new UsernameCacheKey(username);
        Option<ConfluenceUser> cachedUserOption = userCacheAccessor.get(usernameCacheKey);
        if (cachedUserOption != null) {
            log.debug("user found in cache [ {} ]", (Object)usernameCacheKey);
            return (ConfluenceUser)cachedUserOption.getOrElse((Object)null);
        }
        log.debug("user not found in cache looking up [ {} ]", (Object)usernameCacheKey);
        ConfluenceUser user = this.getUserAccessor().getUserByName(username);
        userCacheAccessor.put(usernameCacheKey, (Option<ConfluenceUser>)Option.option((Object)user));
        return user;
    }

    public String getUserFullName(String username) {
        if (StringUtils.isEmpty((CharSequence)username)) {
            return null;
        }
        ConfluenceUser user = this.getUserByName(username);
        return user != null ? user.getFullName() : null;
    }

    @Deprecated(since="5.10", forRemoval=true)
    public String getUserProfilePicUrl() {
        ConfluenceUser user = this.getAuthenticatedUser();
        if (user == null) {
            return "";
        }
        ProfilePictureInfo userProfilePicture = this.userAccessor.getUserProfilePicture(user);
        if (userProfilePicture.isExternal()) {
            log.warn("getDownloadPath() method is deprecated and cannot be used with external avatars. Use getUriReference() instead. Falling back to default avatar. Real avatar url is [{}].", (Object)userProfilePicture.getUriReference());
            return "/images/icons/profilepics/default.svg";
        }
        return userProfilePicture.getDownloadPath();
    }

    protected void addPermissionTypeTo(String permissionType, List<String> permissionTypes) {
        if (permissionTypes.indexOf(permissionType) == -1) {
            permissionTypes.add(permissionType);
        }
    }

    protected <TYPE> List<TYPE> getPermittedEntitiesOf(List<TYPE> entities) {
        return this.permissionManager.getPermittedEntities(this.getAuthenticatedUser(), Permission.VIEW, entities);
    }

    protected <TYPE> List<TYPE> getPermittedEntitiesOf(Iterator<TYPE> entities, int maxCount, List<? extends PermissionManager.Criterion> criteria) {
        return this.permissionManager.getPermittedEntities(this.getAuthenticatedUser(), Permission.VIEW, entities, maxCount, criteria);
    }

    protected <TYPE> List<TYPE> getPermittedEntitiesOf(Iterator<TYPE> entities, int maxCount) {
        return this.permissionManager.getPermittedEntities(this.getAuthenticatedUser(), Permission.VIEW, entities, maxCount);
    }

    public void setPreviousLoginDate(Date previousLoginDate) {
        this.previousLoginDate = previousLoginDate;
    }

    public Date getPreviousLoginDate() {
        ConfluenceUser authenticatedUser;
        if (this.previousLoginDate == null && (authenticatedUser = this.getAuthenticatedUser()) != null) {
            try {
                LoginInfo loginInfo = this.getLoginManager().getLoginInfo(authenticatedUser);
                if (loginInfo == null) {
                    log.info("Problem retrieving loginInfo");
                    return null;
                }
                this.previousLoginDate = loginInfo.getPreviousSuccessfulLoginDate();
            }
            catch (Exception e) {
                log.info("Problem retrieving previousLoginDate", (Throwable)e);
                return null;
            }
        }
        return this.previousLoginDate;
    }

    public boolean isExternalUserManagementEnabled() {
        return this.settingsManager.getGlobalSettings().isExternalUserManagement();
    }

    @Deprecated
    public void setServletRequestSupplier(com.google.common.base.Supplier<HttpServletRequest> servletRequestSupplier) {
        this.servletRequestSupplier = servletRequestSupplier;
    }

    public void withServletRequestSupplier(Supplier<HttpServletRequest> servletRequestSupplier) {
        this.servletRequestSupplier = servletRequestSupplier;
    }

    protected HttpServletRequest getCurrentRequest() {
        return this.servletRequestSupplier.get();
    }

    protected HttpSession getCurrentSession() {
        return this.servletRequestSupplier.get().getSession();
    }

    protected void addToHistory(ContentEntityObject content) {
        UserHistory checkHistory;
        HttpSession session = this.getCurrentSession();
        UserHistory history = (UserHistory)session.getAttribute("confluence.user.history");
        if (history == null) {
            history = new UserHistory(20);
            session.setAttribute("confluence.user.history", (Object)history);
        }
        if ((checkHistory = (UserHistory)session.getAttribute("confluence.user.history")) != null && log.isDebugEnabled()) {
            log.debug("Number of user history entries prior to adding new entry: " + checkHistory.getContent().size());
            log.debug("User history stored in session: " + session.getId());
        }
        history.addContentEntity(content);
        if (checkHistory != null && log.isDebugEnabled()) {
            log.debug("Number of user history entries after to adding new entry: " + checkHistory.getContent().size());
        }
    }

    @Deprecated(forRemoval=true)
    public Map getSession() {
        return ActionContext.getContext().getSession();
    }

    protected UserInterfaceState getUserInterfaceState() {
        if (this.userInterfaceState == null) {
            this.userInterfaceState = new UserInterfaceState(this.getAuthenticatedUser(), this.getUserAccessor());
        }
        return this.userInterfaceState;
    }

    public boolean isAnonymousUser() {
        return this.getAuthenticatedUser() == null;
    }

    public boolean isDevMode() {
        return ConfluenceSystemProperties.isDevMode();
    }

    public String getFrontendServiceURL() {
        return ConfluenceSystemProperties.getConfluenceFrontendServiceURL();
    }

    public String getCancelResult() {
        return CANCEL;
    }

    @Deprecated
    public String getNiceContentType(ContentEntityObject entityObject) {
        return this.getText(this.contentUiSupport.getContentTypeI18NKey(entityObject));
    }

    public ResourceBundle getDefaultResourceBundle() {
        return this.getI18n().getResourceBundle();
    }

    public Settings getGlobalSettings() {
        return this.settingsManager != null ? this.settingsManager.getGlobalSettings() : null;
    }

    public boolean isEmailVisible() {
        return this.permissionManager.isConfluenceAdministrator(this.getAuthenticatedUser()) || !this.settingsManager.getGlobalSettings().areEmailAddressesPrivate();
    }

    public boolean isLabelable(Object object) {
        return object instanceof Labelable;
    }

    public final boolean getUserHasLicensedAccess() {
        ConfluenceUser user = this.getAuthenticatedUser();
        return user != null && this.getConfluenceAccessManager().getUserAccessStatus(user).hasLicensedAccess();
    }

    public final boolean getUserHasBrowseUsersPermission() {
        return this.permissionManager.hasPermission((User)this.getAuthenticatedUser(), Permission.VIEW, User.class);
    }

    public final String getAccessMode() {
        return this.accessModeService.getAccessMode().name();
    }

    public void setSettingsManager(SettingsManager settingsManager) {
        this.settingsManager = settingsManager;
    }

    protected boolean hasPermissionForSpace(List permissionTypes, Space space) {
        return this.spacePermissionManager.hasPermissionForSpace(this.getAuthenticatedUser(), permissionTypes, space);
    }

    public EventManager getEventManager() {
        return this.eventManager;
    }

    public void setEventManager(EventManager eventManager) {
        this.eventManager = eventManager;
    }

    @Deprecated
    public void setBootstrapManager(BootstrapManager bootstrapManager) {
        this.bootstrapManager = bootstrapManager;
    }

    public void setBootstrapStatusProvider(BootstrapStatusProvider bootstrapStatusProvider) {
        this.bootstrapStatusProvider = bootstrapStatusProvider;
    }

    @Deprecated
    protected BootstrapManager getBootstrapManager() {
        if (this.bootstrapManager == null) {
            return (BootstrapManager)((Object)this.getBootstrapStatusProvider());
        }
        return this.bootstrapManager;
    }

    public BootstrapStatusProvider getBootstrapStatusProvider() {
        if (this.bootstrapStatusProvider == null) {
            this.bootstrapStatusProvider = BootstrapStatusProviderImpl.getInstance();
        }
        return this.bootstrapStatusProvider;
    }

    public void setSpacePermissionManager(SpacePermissionManager spacePermissionManager) {
        this.spacePermissionManager = spacePermissionManager;
    }

    protected PermissionManager getPermissionManager() {
        return this.permissionManager;
    }

    public void setPermissionManager(PermissionManager permissionManager) {
        this.permissionManager = permissionManager;
    }

    @Deprecated(forRemoval=true)
    public WebInterfaceManager getWebInterfaceManager() {
        return GeneralUtil.applyIfNonNull(this.webInterfaceManager, ReadOnlyWebInterfaceManager::new);
    }

    public void setWebInterfaceManager(WebInterfaceManager webInterfaceManager) {
        this.webInterfaceManager = webInterfaceManager;
    }

    public void setLabelManager(LabelManager labelManager) {
        this.labelManager = labelManager;
    }

    public LabelManager getLabelManager() {
        return this.labelManager;
    }

    public void setLanguageManager(LanguageManager languageManager) {
        this.languageManager = languageManager;
    }

    public LanguageManager getLanguageManager() {
        return this.languageManager;
    }

    public ThemeHelper getHelper() {
        if (this.globalHelper == null) {
            this.globalHelper = new GlobalHelper(this);
        }
        return this.globalHelper;
    }

    @Deprecated(forRemoval=true)
    protected UserAccessor getUserAccessor() {
        if (this.userAccessor == null) {
            this.userAccessor = (UserAccessor)ContainerManager.getComponent((String)"userAccessor");
        }
        return this.userAccessor;
    }

    public void setUserAccessor(UserAccessor userAccessor) {
        this.userAccessor = userAccessor;
    }

    private LoginManager getLoginManager() {
        if (this.loginManager == null) {
            this.loginManager = (LoginManager)ContainerManager.getComponent((String)"loginManager");
        }
        return this.loginManager;
    }

    public void setLoginManager(LoginManager loginManager) {
        this.loginManager = loginManager;
    }

    @Deprecated(since="8.0")
    public static String getTextStatic(String key) {
        ConfluenceActionSupport dummy = GeneralUtil.newWiredConfluenceActionSupport();
        return dummy.getText(key);
    }

    @Deprecated(since="8.0")
    public static String getTextStatic(String key, Object[] list) {
        ConfluenceActionSupport dummy = GeneralUtil.newWiredConfluenceActionSupport();
        return dummy.getText(key, list);
    }

    public Locale getLocale() {
        if (this.userLocale == null) {
            this.userLocale = this.getLocaleManager().getLocale(this.getAuthenticatedUser());
        }
        return this.userLocale;
    }

    public String getLocaleString() {
        return this.getLocale().toString();
    }

    public String getLanguageUserFriendly(String locale) {
        try {
            String[] keys = locale.split("_");
            Locale temp = new Locale(keys[0], keys[1]);
            return temp.getDisplayLanguage(temp);
        }
        catch (Exception e) {
            return locale;
        }
    }

    public String getLanguageJs() {
        return Language.getJsLang(this.getLocaleString());
    }

    public String getUserLocaleDefaultDatePattern() {
        if (this.dateFormatService == null) {
            return USER_DEFAULT_DATE_PATTERN;
        }
        return this.dateFormatService.getDateFormatPatternForUser();
    }

    public void setApiDateFormatService(DateFormatService dateFormatService) {
        this.dateFormatService = dateFormatService;
    }

    public List<Language> getInstalledLanguages() {
        return this.getLanguageManager().getLanguages();
    }

    public void addFieldError(String fieldName, String textKey, Object[] args) {
        this.addFieldError(fieldName, this.getText(textKey, PlainTextToHtmlConverter.encodeHtmlEntities(args)));
    }

    public void addActionError(String textKey, Object ... args) {
        this.addActionError(this.getText(textKey, PlainTextToHtmlConverter.encodeHtmlEntities(args)));
    }

    public void addActionMessage(String textKey, Object ... args) {
        this.addActionMessage(this.getText(textKey, PlainTextToHtmlConverter.encodeHtmlEntities(args)));
    }

    public I18NBean getI18n() {
        if (this.i18NBean == null) {
            I18NBeanFactory i18NBeanFactory1 = this.getI18NBeanFactory();
            this.i18NBean = i18NBeanFactory1.getI18NBean(this.getLocale());
        }
        return this.i18NBean;
    }

    public String getDocLink(String page) {
        if (this.docBean == null) {
            this.docBean = new VersionSpecificDocumentationBean(BuildInformation.INSTANCE.getVersionNumber(), this.getI18n());
        }
        return this.docBean.getLink(page);
    }

    public SystemInformationService getSystemInformationService() {
        return this.systemInformationService;
    }

    public void setSystemInformationService(SystemInformationService systemInformationService) {
        this.systemInformationService = systemInformationService;
    }

    public void setLocaleManager(LocaleManager localeManager) {
        this.localeManager = localeManager;
    }

    public LocaleManager getLocaleManager() {
        if (this.localeManager == null) {
            this.localeManager = (LocaleManager)ContainerManager.getComponent((String)"localeManager");
        }
        return this.localeManager;
    }

    public void setI18NBeanFactory(I18NBeanFactory i18NBeanFactory) {
        this.i18NBeanFactory = i18NBeanFactory;
    }

    @Override
    public void setMessageHolder(MessageHolder messageHolder) {
        this.messageHolder = messageHolder;
    }

    @Override
    public MessageHolder getMessageHolder() {
        return this.messageHolder;
    }

    private I18NBeanFactory getI18NBeanFactory() {
        if (this.i18NBeanFactory == null) {
            log.info("ConfluenceActionSupport was asked to perform i18N without being properly initialised.");
            DefaultI18NBeanFactory backupBeanFactory = new DefaultI18NBeanFactory();
            return backupBeanFactory;
        }
        return this.i18NBeanFactory;
    }

    public TimeZone getTimeZone() {
        if (this.getBootstrapStatusProvider().isSetupComplete() && this.getAuthenticatedUser() != null) {
            return this.getUserAccessor().getConfluenceUserPreferences(this.getAuthenticatedUser()).getTimeZone();
        }
        return this.getDefaultTimeZone();
    }

    public TimeZone getDefaultTimeZone() {
        return TimeZone.getInstance(this.timeZoneManager.getDefaultTimeZone().getID());
    }

    public DateFormatter getDateFormatter() {
        if (this.dateFormatter == null) {
            this.dateFormatter = new DateFormatter(this.getTimeZone(), this.formatSettingsManager, this.localeManager);
        }
        return this.dateFormatter;
    }

    public FriendlyDateFormatter getFriendlyDateFormatter() {
        if (this.friendlyDateFormatter == null) {
            this.friendlyDateFormatter = new FriendlyDateFormatter(RequestTimeThreadLocal.getTimeOrNow(), this.getDateFormatter());
        }
        return this.friendlyDateFormatter;
    }

    public String getDateFormatSetting() {
        return this.formatSettingsManager.getDateFormat();
    }

    public String formatFriendlyDate(Date date) {
        Message message = this.getFriendlyDateFormatter().getFormatMessage(date);
        return this.getText(message.getKey(), message.getArguments());
    }

    public void setFormatSettingsManager(FormatSettingsManager formatSettingsManager) {
        this.formatSettingsManager = formatSettingsManager;
    }

    public boolean isUsingHSQL() {
        String dialect = this.getBootstrapStatusProvider().getHibernateDialect();
        return dialect.contains("HSQLDialect") || dialect.contains("HSQL2Dialect");
    }

    public boolean isUsingH2() {
        return this.getBootstrapStatusProvider().getHibernateDialect().contains("H2Dialect");
    }

    public void setI18NBean(I18NBean i18NBean) {
        this.i18NBean = i18NBean;
    }

    @Override
    public WebInterfaceContext getWebInterfaceContext() {
        DefaultWebInterfaceContext context = new DefaultWebInterfaceContext();
        context.setCurrentUser(this.getAuthenticatedUser());
        if (this instanceof PageAware) {
            context.setPage(((PageAware)((Object)this)).getPage());
        }
        if (this instanceof UserAware) {
            context.setTargetedUser(FindUserHelper.getUser(((UserAware)((Object)this)).getUser()));
        }
        Space space = null;
        if (this instanceof Spaced) {
            space = ((Spaced)((Object)this)).getSpace();
        } else {
            AbstractPage page = context.getPage();
            if (page != null) {
                space = page.getLatestVersion().getSpace();
            }
        }
        if (space != null) {
            context.setSpace(space);
            if (space.isPersonal()) {
                ConfluenceUser user = space.getCreator();
                context.setTargetedUser(user);
            }
        }
        return context;
    }

    public boolean isUserStatusPluginEnabled() {
        return this.pluginAccessor != null && this.pluginAccessor.isPluginEnabled("confluence.userstatus");
    }

    public void setPluginAccessor(PluginAccessor pluginAccessor) {
        this.pluginAccessor = pluginAccessor;
    }

    public Map<String, Object> getContext() {
        HashMap<String, Object> context = new HashMap<String, Object>();
        context.put("action", this);
        context.put("remoteuser", this.getAuthenticatedUser());
        context.put("user", this.getAuthenticatedUser());
        return context;
    }

    public void setContentUiSupport(ContentUiSupport contentUiSupport) {
        this.contentUiSupport = contentUiSupport;
    }

    public void setTimeZoneManager(TimeZoneManager timeZoneManager) {
        this.timeZoneManager = timeZoneManager;
    }

    public void setConfluenceAccessManager(ConfluenceAccessManager confluenceAccessManager) {
        this.confluenceAccessManager = confluenceAccessManager;
    }

    public ConfluenceAccessManager getConfluenceAccessManager() {
        return this.confluenceAccessManager;
    }

    public void setAccessModeService(AccessModeService accessModeService) {
        this.accessModeService = accessModeService;
    }

    @Deprecated(forRemoval=true)
    protected PersonService getPersonService() {
        return this.personService;
    }

    public void setApiPersonService(PersonService personService) {
        this.personService = personService;
    }

    public ContentUiSupport getContentUiSupport() {
        return this.contentUiSupport;
    }

    public DocumentationBean getDocBean() {
        return this.docBean;
    }

    private static class XWorkServletRequestSupplier
    implements Supplier<HttpServletRequest> {
        private XWorkServletRequestSupplier() {
        }

        @Override
        public HttpServletRequest get() {
            return ServletActionContext.getRequest();
        }
    }
}

