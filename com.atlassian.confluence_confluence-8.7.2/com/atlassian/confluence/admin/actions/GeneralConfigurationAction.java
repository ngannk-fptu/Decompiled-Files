/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.velocity.htmlsafe.HtmlSafe
 *  com.atlassian.core.util.FileSize
 *  com.atlassian.core.util.PairType
 *  com.atlassian.event.Event
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.renderer.WikiStyleRenderer
 *  com.atlassian.sal.api.websudo.WebSudoRequired
 *  com.atlassian.user.User
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.admin.actions;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.core.FormAware;
import com.atlassian.confluence.core.FormatSettingsManager;
import com.atlassian.confluence.event.events.admin.GlobalSettingsChangedEvent;
import com.atlassian.confluence.event.events.admin.SiteTitleChangeEvent;
import com.atlassian.confluence.event.events.admin.ViewGeneralConfigEvent;
import com.atlassian.confluence.event.events.plugin.XWorkStateChangeEvent;
import com.atlassian.confluence.impl.security.AdminOnly;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.search.SearchLanguage;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.setup.settings.Settings;
import com.atlassian.confluence.setup.settings.init.AdminUiProperties;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.util.UrlUtils;
import com.atlassian.confluence.util.http.ConfluenceHttpParameters;
import com.atlassian.confluence.velocity.htmlsafe.HtmlSafe;
import com.atlassian.core.util.FileSize;
import com.atlassian.core.util.PairType;
import com.atlassian.event.Event;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.WikiStyleRenderer;
import com.atlassian.sal.api.websudo.WebSudoRequired;
import com.atlassian.user.User;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebSudoRequired
@AdminOnly
public class GeneralConfigurationAction
extends ConfluenceActionSupport
implements FormAware {
    private static final Logger log = LoggerFactory.getLogger(GeneralConfigurationAction.class);
    private AdminUiProperties adminUiProperties;
    private SpaceManager spaceManager;
    private WikiStyleRenderer wikiStyleRenderer;
    private FormatSettingsManager formatSettingsManager;
    private boolean allowCamelCase;
    private boolean gzipResponseEncoding;
    private int maxAttachmentsInUI;
    private String attachmentMaxSizeMb;
    private String siteTitle;
    private String indexingLanguage;
    private String domainName;
    private String customContactAdminMessage;
    private boolean showContactAdministratorsForm;
    private String defaultTimeFormatterPattern;
    private String defaultDateTimeFormatterPattern;
    private String defaultDateFormatterPattern;
    private String defaultLongNumberFormatterPattern;
    private String defaultDecimalNumberFormatterPattern;
    private String siteSupportAddress;
    private String[] validURISchemes = new String[]{"http://", "https://"};
    private int socketTimeout;
    private int connectionTimeout;
    private boolean connectionsEnabled;
    private int maxThumbHeight;
    private int maxThumbWidth;
    public static final int MINIMUM_MAX_UPLOAD_ATTACHMENTS = 1;
    public static final int MAXIMUM_MAX_UPLOAD_ATTACHMENTS = 25;
    public static final String VALID_NUMBER_INPUT_PATTERN = "^\\d*\\.?\\d*$";
    private boolean editMode = true;
    private boolean editBaseUrl = false;

    @Override
    public boolean isPermitted() {
        return this.permissionManager.hasPermission((User)this.getAuthenticatedUser(), Permission.ADMINISTER, PermissionManager.TARGET_APPLICATION);
    }

    @PermittedMethods(value={HttpMethod.GET})
    public String doView() throws Exception {
        this.editMode = false;
        return this.doDefault();
    }

    @Override
    public String doDefault() throws Exception {
        Settings globalSettings = this.getGlobalSettings();
        this.allowCamelCase = globalSettings.isAllowCamelCase();
        this.siteTitle = globalSettings.getSiteTitle();
        this.customContactAdminMessage = globalSettings.getCustomContactMessage();
        this.showContactAdministratorsForm = globalSettings.isShowContactAdministratorsForm();
        this.maxAttachmentsInUI = globalSettings.getMaxAttachmentsInUI();
        DecimalFormat numberFormatter = new DecimalFormat();
        ((NumberFormat)numberFormatter).setGroupingUsed(false);
        ((NumberFormat)numberFormatter).setMaximumFractionDigits(4);
        this.attachmentMaxSizeMb = numberFormatter.format(FileSize.convertBytesToMB((long)globalSettings.getAttachmentMaxSize()));
        this.siteSupportAddress = globalSettings.getSupportRequestEmail();
        this.maxThumbHeight = globalSettings.getMaxThumbHeight();
        this.maxThumbWidth = globalSettings.getMaxThumbWidth();
        this.indexingLanguage = globalSettings.getIndexingLanguage();
        this.domainName = globalSettings.getBaseUrl();
        this.gzipResponseEncoding = globalSettings.isGzippingResponse();
        this.defaultTimeFormatterPattern = this.formatSettingsManager.getTimeFormat();
        this.defaultDateTimeFormatterPattern = this.formatSettingsManager.getDateTimeFormat();
        this.defaultDateFormatterPattern = this.formatSettingsManager.getDateFormat();
        this.defaultLongNumberFormatterPattern = this.formatSettingsManager.getLongNumberFormat();
        this.defaultDecimalNumberFormatterPattern = this.formatSettingsManager.getDecimalNumberFormat();
        ConfluenceHttpParameters httpParameters = globalSettings.getConfluenceHttpParameters();
        this.socketTimeout = httpParameters.getSocketTimeout();
        this.connectionTimeout = httpParameters.getConnectionTimeout();
        this.connectionsEnabled = httpParameters.isEnabled();
        this.eventManager.publishEvent((Event)new ViewGeneralConfigEvent(this));
        String autofocusParameter = this.getCurrentRequest().getParameter("autofocus");
        if (autofocusParameter != null && autofocusParameter.equalsIgnoreCase("editbaseurl")) {
            this.editBaseUrl = true;
        }
        return super.doDefault();
    }

    private void validation() {
        if (this.isSystemAdmin()) {
            if (StringUtils.isEmpty((CharSequence)this.domainName)) {
                this.addFieldError("domainName", this.getText("error.enter.domain.name"));
            } else if (!this.validBaseURLProtocol(this.domainName)) {
                String protocols = Arrays.toString(this.validURISchemes);
                protocols = protocols.substring(1, protocols.length() - 1);
                this.addFieldError("domainName", this.getText("error.domain.name.needs.protocol", Arrays.asList(protocols)));
            } else if (!UrlUtils.verifyUrl(this.domainName)) {
                this.addFieldError("domainName", this.getText("error.domain.name.invalid"));
            }
        }
        if (this.getMaxAttachmentsInUI() < 1) {
            this.addFieldError("maxAttachmentsInUI", this.getText("error.minimum.num.attachments", Arrays.asList(1)));
        } else if (this.getMaxAttachmentsInUI() > 25) {
            this.addFieldError("maxAttachmentsInUI", this.getText("error.maximum.num.attachments", Arrays.asList(25)));
        }
        if (!this.isShowContactAdministratorsForm() && StringUtils.equals((CharSequence)this.getCustomContactAdminMessage(), (CharSequence)this.getText("administrators.contact.default.prompt"))) {
            this.addFieldError("customContactAdminMessage", this.getText("site.custom.contact.admin.form.error"));
        }
        if (!this.isValidNumericalInput()) {
            this.addFieldError("attachmentMaxSizeMb", this.getText("error.number.input.invalid"));
        }
    }

    private boolean validBaseURLProtocol(String domainName) {
        if (domainName == null) {
            return false;
        }
        for (String p : this.validURISchemes) {
            if (!domainName.startsWith(p)) continue;
            return true;
        }
        return false;
    }

    private boolean isValidNumericalInput() {
        return !StringUtils.isEmpty((CharSequence)this.attachmentMaxSizeMb) && this.attachmentMaxSizeMb.matches(VALID_NUMBER_INPUT_PATTERN);
    }

    public String execute() throws Exception {
        this.validation();
        if (this.hasErrors()) {
            return "error";
        }
        Settings originalSettings = this.settingsManager.getGlobalSettings();
        String oldDomainName = this.settingsManager.getGlobalSettings().getBaseUrl();
        long oldMaxAttachmentSize = this.settingsManager.getGlobalSettings().getAttachmentMaxSize();
        String oldDefaultEncoding = this.settingsManager.getGlobalSettings().getDefaultEncoding();
        this.saveSetupOptions();
        this.saveFormattingSettings();
        GlobalSettingsChangedEvent event = new GlobalSettingsChangedEvent(this, originalSettings, this.settingsManager.getGlobalSettings(), oldDomainName, this.settingsManager.getGlobalSettings().getBaseUrl());
        this.eventManager.publishEvent((Event)event);
        if (!this.settingsManager.getGlobalSettings().getDefaultEncoding().equals(oldDefaultEncoding) || this.settingsManager.getGlobalSettings().getAttachmentMaxSize() != oldMaxAttachmentSize) {
            this.eventManager.publishEvent((Event)new XWorkStateChangeEvent(this));
        }
        return "success";
    }

    private void saveSetupOptions() {
        Settings settings = new Settings(this.getGlobalSettings());
        settings.setAllowCamelCase(this.allowCamelCase);
        settings.setMaxAttachmentsInUI(this.maxAttachmentsInUI);
        settings.setAttachmentMaxSize(FileSize.convertMBToBytes((double)Double.parseDouble(this.attachmentMaxSizeMb)));
        settings.setSiteTitle(this.siteTitle);
        settings.setIndexingLanguage(this.getIndexingLanguage());
        settings.setCustomContactMessage(this.getCustomContactAdminMessage());
        settings.setShowContactAdministratorsForm(this.isShowContactAdministratorsForm());
        if (this.isSiteSupportEmailAllowed()) {
            settings.setSupportRequestEmail(this.siteSupportAddress);
        }
        if (this.isSystemAdmin()) {
            settings.setGzippingResponse(this.gzipResponseEncoding);
            settings.setBaseUrl(this.getDomainName());
            ConfluenceHttpParameters httpParameters = settings.getConfluenceHttpParameters();
            httpParameters.setConnectionTimeout(this.connectionTimeout);
            httpParameters.setSocketTimeout(this.socketTimeout);
            httpParameters.setEnabled(this.connectionsEnabled);
        }
        if (!this.settingsManager.getGlobalSettings().getSiteTitle().equals(this.siteTitle)) {
            this.eventManager.publishEvent((Event)new SiteTitleChangeEvent(this));
        }
        this.settingsManager.updateGlobalSettings(settings);
    }

    public boolean isSystemAdmin() {
        return this.permissionManager.hasPermission((User)this.getAuthenticatedUser(), Permission.ADMINISTER, PermissionManager.TARGET_SYSTEM);
    }

    public boolean isFileSystemAttachmentStorage() {
        String setting = this.settingsManager.getGlobalSettings().getAttachmentDataStore();
        return setting == null || "file.system.based.attachments.storage".equals(setting);
    }

    private void saveFormattingSettings() {
        try {
            new DecimalFormat(this.defaultLongNumberFormatterPattern);
        }
        catch (IllegalArgumentException e) {
            this.addFieldError("defaultLongNumberFormatterPattern", this.getText("invalid.pattern"));
        }
        try {
            new DecimalFormat(this.defaultDecimalNumberFormatterPattern);
        }
        catch (IllegalArgumentException e) {
            this.addFieldError("defaultDecimalNumberFormatterPattern", this.getText("invalid.pattern"));
        }
        this.formatSettingsManager.setTimeFormat(this.defaultTimeFormatterPattern);
        this.formatSettingsManager.setDateTimeFormat(this.defaultDateTimeFormatterPattern);
        this.formatSettingsManager.setDateFormat(this.defaultDateFormatterPattern);
        this.formatSettingsManager.setLongNumberFormat(this.defaultLongNumberFormatterPattern);
        this.formatSettingsManager.setDecimalNumberFormat(this.defaultDecimalNumberFormatterPattern);
    }

    public SpaceManager getSpaceManager() {
        return this.spaceManager;
    }

    public void setSpaceManager(SpaceManager spaceManager) {
        this.spaceManager = spaceManager;
    }

    public void setWikiStyleRenderer(WikiStyleRenderer wikiStyleRenderer) {
        this.wikiStyleRenderer = wikiStyleRenderer;
    }

    public List<PairType> getIndexingLanguages() {
        return Arrays.stream(SearchLanguage.values()).map(lang -> new PairType((Serializable)((Object)lang.value), (Serializable)((Object)this.getText("indexing.language." + lang.value)))).collect(Collectors.toList());
    }

    @HtmlSafe
    public String getRenderedCustomErrorMessage() {
        return this.wikiStyleRenderer.convertWikiToXHtml((RenderContext)new PageContext(), this.getCustomContactAdminMessage());
    }

    public int getMaxAttachmentsInUI() {
        return this.maxAttachmentsInUI;
    }

    public void setMaxAttachmentsInUI(int maxAttachmentsInUI) {
        this.maxAttachmentsInUI = maxAttachmentsInUI;
    }

    public String getAttachmentMaxSizeMbNice() {
        return FileSize.format((long)this.settingsManager.getGlobalSettings().getAttachmentMaxSize());
    }

    public String getAttachmentMaxSizeMbEdit() {
        return this.attachmentMaxSizeMb;
    }

    public void setAttachmentMaxSizeMb(String attachmentMaxSizeMb) {
        this.attachmentMaxSizeMb = StringUtils.stripToEmpty((String)attachmentMaxSizeMb);
    }

    public String getCustomContactAdminMessage() {
        if (StringUtils.isBlank((CharSequence)this.customContactAdminMessage)) {
            return this.getText("administrators.contact.default.prompt");
        }
        return this.customContactAdminMessage;
    }

    public void setCustomContactAdminMessage(String customContactAdminMessage) {
        this.customContactAdminMessage = customContactAdminMessage;
    }

    public boolean isShowContactAdministratorsForm() {
        return this.showContactAdministratorsForm;
    }

    public void setShowContactAdministratorsForm(boolean showContactAdministratorsForm) {
        this.showContactAdministratorsForm = showContactAdministratorsForm;
    }

    public String getCurrentTime(String dateFormatPattern) {
        return new SimpleDateFormat(dateFormatPattern).format(new Date());
    }

    public boolean getAllowCamelCase() {
        return this.allowCamelCase;
    }

    public boolean isAllowCamelCase() {
        return this.allowCamelCase;
    }

    public void setAllowCamelCase(boolean allowCamelCase) {
        this.allowCamelCase = allowCamelCase;
    }

    public String getDefaultTimeFormatterPattern() {
        return this.defaultTimeFormatterPattern;
    }

    public void setDefaultTimeFormatterPattern(String defaultTimeFormatterPattern) {
        this.defaultTimeFormatterPattern = defaultTimeFormatterPattern;
    }

    public String getDefaultDateTimeFormatterPattern() {
        return this.defaultDateTimeFormatterPattern;
    }

    public void setDefaultDateTimeFormatterPattern(String defaultDateTimeFormatterPattern) {
        this.defaultDateTimeFormatterPattern = defaultDateTimeFormatterPattern;
    }

    public String getDefaultDateFormatterPattern() {
        return this.defaultDateFormatterPattern;
    }

    public void setDefaultDateFormatterPattern(String defaultDateFormatterPattern) {
        this.defaultDateFormatterPattern = defaultDateFormatterPattern;
    }

    public String getDefaultLongNumberFormatterPattern() {
        return this.defaultLongNumberFormatterPattern;
    }

    public void setDefaultLongNumberFormatterPattern(String defaultLongNumberFormatterPattern) {
        this.defaultLongNumberFormatterPattern = defaultLongNumberFormatterPattern;
    }

    public String getDefaultDecimalNumberFormatterPattern() {
        return this.defaultDecimalNumberFormatterPattern;
    }

    public void setDefaultDecimalNumberFormatterPattern(String defaultDecimalNumberFormatterPattern) {
        this.defaultDecimalNumberFormatterPattern = defaultDecimalNumberFormatterPattern;
    }

    public int getMaxThumbHeight() {
        return this.maxThumbHeight;
    }

    public void setMaxThumbHeight(int maxThumbHeight) {
        this.maxThumbHeight = maxThumbHeight;
    }

    public int getMaxThumbWidth() {
        return this.maxThumbWidth;
    }

    public void setMaxThumbWidth(int maxThumbWidth) {
        this.maxThumbWidth = maxThumbWidth;
    }

    public String getIndexingLanguage() {
        return this.indexingLanguage;
    }

    public void setIndexingLanguage(String indexingLanguage) {
        this.indexingLanguage = indexingLanguage;
    }

    public String getDomainName() {
        return this.domainName;
    }

    public void setDomainName(String domainName) {
        if (domainName != null) {
            domainName = domainName.trim();
        }
        if (domainName != null && domainName.endsWith("/")) {
            domainName = domainName.substring(0, domainName.lastIndexOf("/"));
        }
        this.domainName = domainName;
    }

    public String getSiteTitle() {
        return this.siteTitle;
    }

    public void setSiteTitle(String siteTitle) {
        this.siteTitle = siteTitle;
    }

    public boolean isGzipResponseEncoding() {
        return this.gzipResponseEncoding;
    }

    public void setGzipResponseEncoding(boolean gzipResponseEncoding) {
        this.gzipResponseEncoding = gzipResponseEncoding;
    }

    @Override
    public void setFormatSettingsManager(FormatSettingsManager formatSettingsManager) {
        this.formatSettingsManager = formatSettingsManager;
    }

    public void setSocketTimeout(int socketTimeout) {
        this.socketTimeout = socketTimeout;
    }

    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public int getSocketTimeout() {
        return this.socketTimeout;
    }

    public int getConnectionTimeout() {
        return this.connectionTimeout;
    }

    public boolean isConnectionsEnabled() {
        return this.connectionsEnabled;
    }

    public void setConnectionsEnabled(boolean connectionsEnabled) {
        this.connectionsEnabled = connectionsEnabled;
    }

    public String getSiteSupportAddress() {
        return this.siteSupportAddress;
    }

    public void setSiteSupportAddress(String siteSupportAddress) {
        this.siteSupportAddress = siteSupportAddress;
    }

    public boolean isSiteSupportEmailAllowed() {
        return this.adminUiProperties.isAllowed("admin.ui.allow.site.support.email");
    }

    public void setAdminUiProperties(AdminUiProperties adminUiProperties) {
        this.adminUiProperties = adminUiProperties;
    }

    @Override
    public boolean isEditMode() {
        return this.editMode;
    }

    public boolean isEditBaseUrl() {
        return this.editBaseUrl;
    }
}

