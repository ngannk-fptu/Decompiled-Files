/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ConfluenceActionSupport
 *  com.atlassian.confluence.event.events.admin.GlobalSettingsChangedEvent
 *  com.atlassian.confluence.setup.settings.Settings
 *  com.atlassian.confluence.themes.events.FaviconChangedEvent
 *  com.atlassian.confluence.themes.events.FaviconChangedEvent$Action
 *  com.atlassian.core.util.thumbnail.ThumbnailDimension
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.favicon.core.Constants
 *  com.atlassian.favicon.core.Favicon
 *  com.atlassian.favicon.core.FaviconManager
 *  com.atlassian.favicon.core.ImageType
 *  com.atlassian.favicon.core.UploadedFaviconFile
 *  com.atlassian.favicon.core.exceptions.MessageKeyedException
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.soy.renderer.SoyException
 *  com.atlassian.soy.renderer.SoyTemplateRenderer
 *  com.atlassian.xwork.FileUploadUtils
 *  com.atlassian.xwork.FileUploadUtils$FileUploadException
 *  com.atlassian.xwork.FileUploadUtils$UploadedFile
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  com.atlassian.xwork.XsrfTokenGenerator
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.struts2.ServletActionContext
 *  org.apache.struts2.dispatcher.multipart.MultiPartRequestWrapper
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.lookandfeel;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.event.events.admin.GlobalSettingsChangedEvent;
import com.atlassian.confluence.plugins.lookandfeel.AutoLookAndFeelManager;
import com.atlassian.confluence.plugins.lookandfeel.SiteLogoManager;
import com.atlassian.confluence.setup.settings.Settings;
import com.atlassian.confluence.themes.events.FaviconChangedEvent;
import com.atlassian.core.util.thumbnail.ThumbnailDimension;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.favicon.core.Constants;
import com.atlassian.favicon.core.Favicon;
import com.atlassian.favicon.core.FaviconManager;
import com.atlassian.favicon.core.ImageType;
import com.atlassian.favicon.core.UploadedFaviconFile;
import com.atlassian.favicon.core.exceptions.MessageKeyedException;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.soy.renderer.SoyException;
import com.atlassian.soy.renderer.SoyTemplateRenderer;
import com.atlassian.xwork.FileUploadUtils;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import com.atlassian.xwork.XsrfTokenGenerator;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.dispatcher.multipart.MultiPartRequestWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EditSiteLogoAction
extends ConfluenceActionSupport {
    private static final Logger log = LoggerFactory.getLogger(EditSiteLogoAction.class);
    private SoyTemplateRenderer soyTemplateRenderer;
    private SiteLogoManager siteLogoManager;
    private XsrfTokenGenerator tokenGenerator;
    private AutoLookAndFeelManager autoLookAndFeelManager;
    private I18nResolver i18nResolver;
    private FaviconManager faviconManager;
    private EventPublisher eventPublisher;
    private String siteTitle;
    private boolean showBothLogoAndTitle;
    private String showOptions;
    private boolean colorSchemeUpdated;
    private boolean faviconUpdated;
    private boolean faviconReset;

    @PermittedMethods(value={HttpMethod.GET})
    public String doView() {
        if (this.isFaviconUpdated()) {
            this.addActionMessage(this.getText("custom.favicon.admin.upload.complete"));
        }
        if (this.isFaviconReset()) {
            this.addActionMessage(this.getText("custom.favicon.admin.reset.complete"));
        }
        Settings globalSettings = this.settingsManager.getGlobalSettings();
        this.showBothLogoAndTitle = globalSettings.showApplicationTitle();
        this.siteTitle = globalSettings.getSiteTitle();
        return "success";
    }

    public String doUpload() {
        try {
            FileUploadUtils.UploadedFile uploadedFile = FileUploadUtils.getSingleUploadedFile();
            if (uploadedFile != null) {
                this.updateSiteLogo(uploadedFile);
                if (this.hasErrors()) {
                    log.error("Site logo upload failed: {}", (Object)this.getActionErrors());
                    return "error";
                }
                log.info("Succeeded in uploading new site logo [{}]", (Object)uploadedFile.getFileName());
                this.autoLookAndFeelManager.backupColorScheme();
                this.autoLookAndFeelManager.generateFromSiteLogo();
                this.colorSchemeUpdated = true;
            }
        }
        catch (Exception e) {
            String errorMsg = this.getText("lookandfeel.sitelogo.admin.logo.upload.error");
            log.error(errorMsg, (Throwable)e);
            this.addFieldError("logoFile", errorMsg);
            return "error";
        }
        Settings globalSettings = this.settingsManager.getGlobalSettings();
        Settings newSettings = new Settings(globalSettings);
        newSettings.setShowApplicationTitle(this.showBothLogoAndTitle);
        newSettings.setSiteTitle(this.siteTitle);
        this.settingsManager.updateGlobalSettings(newSettings);
        this.eventPublisher.publish((Object)new GlobalSettingsChangedEvent((Object)this, globalSettings, newSettings));
        log.info("Updated site title to [{}]", (Object)this.siteTitle);
        return "success";
    }

    private void updateSiteLogo(FileUploadUtils.UploadedFile uploadedFile) throws IOException {
        try {
            this.siteLogoManager.uploadLogo(uploadedFile.getFile(), uploadedFile.getContentType());
        }
        catch (UnsupportedOperationException e) {
            log.info("Failed to upload new site logo", (Throwable)e);
            this.addActionError(this.getText("lookandfeel.sitelogo.admin.logo.upload.mimetype.unsupported.error", new Object[]{uploadedFile.getContentType()}));
        }
        catch (IOException e) {
            log.warn("Failed to upload new site logo", (Throwable)e);
            this.addActionError(this.getText("lookandfeel.sitelogo.admin.logo.upload.error "));
        }
    }

    @PermittedMethods(value={HttpMethod.GET, HttpMethod.POST})
    public String doReset() {
        this.siteLogoManager.resetToDefault();
        this.autoLookAndFeelManager.restoreDefaultColorScheme();
        return "success";
    }

    public String doRestoreColorScheme() {
        this.autoLookAndFeelManager.restoreBackupColorScheme();
        return "success";
    }

    public String getLogoFormAsHtml() {
        try {
            String xsrfToken = this.tokenGenerator.generateToken(ServletActionContext.getRequest());
            HashMap<String, Object> soyRenderData = new HashMap<String, Object>();
            soyRenderData.put("atlToken", xsrfToken);
            soyRenderData.put("uploadAction", "upload.action");
            soyRenderData.put("resetAction", "reset.action?atl_token=" + xsrfToken);
            soyRenderData.put("undoColorSchemeAction", "restoreColorScheme.action?atl_token=" + xsrfToken);
            soyRenderData.put("isNotDefault", this.siteLogoManager.useCustomLogo());
            soyRenderData.put("siteTitle", this.settingsManager.getGlobalSettings().getSiteTitle());
            soyRenderData.put("showBothLogoAndTitle", this.showBothLogoAndTitle);
            soyRenderData.put("fieldErrors", this.getFieldErrors());
            soyRenderData.put("colorSchemeUpdated", this.colorSchemeUpdated);
            soyRenderData.put("maxHeight", 48);
            return this.soyTemplateRenderer.render("com.atlassian.confluence.plugins.confluence-lookandfeel:sitelogo-resources", "Confluence.Templates.LookandFeelLogo.logoForm", soyRenderData);
        }
        catch (SoyException e) {
            log.debug("Could not render soy template for ");
            log.debug("Exception: ", (Throwable)e);
            return null;
        }
    }

    public String getFaviconFormAsHtml() {
        try {
            String xsrfToken = this.tokenGenerator.generateToken(ServletActionContext.getRequest());
            String faviconURL = String.format("%s/%s", this.settingsManager.getGlobalSettings().getBaseUrl(), this.generateFaviconFilename());
            HashMap<String, Object> soyRenderData = new HashMap<String, Object>();
            soyRenderData.put("atlToken", xsrfToken);
            soyRenderData.put("imageWithContext", faviconURL);
            soyRenderData.put("isNotDefault", this.faviconManager.isFaviconConfigured());
            soyRenderData.put("uploadAction", "uploadFavicon.action");
            soyRenderData.put("resetAction", "resetFavicon.action?atl_token=" + xsrfToken);
            soyRenderData.put("actionErrors", new ArrayList());
            soyRenderData.put("actionMessages", new ArrayList());
            return this.soyTemplateRenderer.render("com.atlassian.favicon.confluence-custom-favicon-plugin:favicon-soy", "Custom.Favicon.Templates.configureForm", soyRenderData);
        }
        catch (SoyException e) {
            log.debug("Could not render soy template for favicon configuration", (Throwable)e);
            return null;
        }
    }

    @PermittedMethods(value={HttpMethod.GET, HttpMethod.POST})
    public String doFaviconReset() {
        this.faviconManager.resetFavicon();
        this.eventPublisher.publish((Object)new FaviconChangedEvent((Object)this, FaviconChangedEvent.Action.RESET));
        return "success";
    }

    public String doFaviconUpload() {
        try {
            MultiPartRequestWrapper multiPartRequest = FileUploadUtils.unwrapMultiPartRequest((HttpServletRequest)ServletActionContext.getRequest());
            if (multiPartRequest == null) {
                return "success";
            }
            FileUploadUtils.UploadedFile file = null;
            try {
                file = FileUploadUtils.getSingleUploadedFile();
            }
            catch (FileUploadUtils.FileUploadException e) {
                log.debug("Could not read uploaded file", (Throwable)e);
            }
            if (file == null) {
                this.addActionError(this.getText("custom.favicon.admin.upload.error"));
                return "error";
            }
            Optional imageType = ImageType.parseFromContentType((String)file.getContentType());
            if (!imageType.isPresent()) {
                this.addActionError(this.getText("custom.favicon.unsupported.image.type", file.getContentType()));
                return "error";
            }
            this.faviconManager.setFavicon(new UploadedFaviconFile(file.getFile(), (ImageType)imageType.get()));
            this.eventPublisher.publish((Object)new FaviconChangedEvent((Object)this, FaviconChangedEvent.Action.UPLOADED));
        }
        catch (MessageKeyedException e) {
            this.addActionError(this.getText(e.getMessageKey(), e.getArguments()));
            return "error";
        }
        catch (Exception e) {
            log.warn("Unexpected error uploading the favicon image", (Throwable)e);
            return "error";
        }
        return "success";
    }

    private String generateFaviconFilename() {
        if (this.faviconManager.isFaviconConfigured()) {
            return Favicon.generateFilename((ImageType)ImageType.PNG, (ThumbnailDimension)Constants.MAX_DIMENSION);
        }
        return "favicon.ico";
    }

    public SiteLogoManager getSiteLogoManager() {
        return this.siteLogoManager;
    }

    public void setSiteLogoManager(SiteLogoManager siteLogoManager) {
        this.siteLogoManager = siteLogoManager;
    }

    public SoyTemplateRenderer getSoyTemplateRenderer() {
        return this.soyTemplateRenderer;
    }

    public void setSoyTemplateRenderer(@ComponentImport SoyTemplateRenderer soyTemplateRenderer) {
        this.soyTemplateRenderer = soyTemplateRenderer;
    }

    public XsrfTokenGenerator getTokenGenerator() {
        return this.tokenGenerator;
    }

    public void setTokenGenerator(@ComponentImport XsrfTokenGenerator tokenGenerator) {
        this.tokenGenerator = tokenGenerator;
    }

    public AutoLookAndFeelManager getAutoLookAndFeelManager() {
        return this.autoLookAndFeelManager;
    }

    public void setAutoLookAndFeelManager(AutoLookAndFeelManager autoLookAndFeelManager) {
        this.autoLookAndFeelManager = autoLookAndFeelManager;
    }

    public I18nResolver getI18nResolver() {
        return this.i18nResolver;
    }

    public void setI18nResolver(@ComponentImport I18nResolver i18nResolver) {
        this.i18nResolver = i18nResolver;
    }

    public String getSiteTitle() {
        return this.siteTitle;
    }

    public void setSiteTitle(String siteTitle) {
        this.siteTitle = siteTitle;
    }

    public String getShowOptions() {
        return this.showOptions;
    }

    public void setShowOptions(String showOptions) {
        this.showOptions = showOptions;
        this.showBothLogoAndTitle = "both".equals(showOptions);
    }

    public boolean isColorSchemeUpdated() {
        return this.colorSchemeUpdated;
    }

    public void setColorSchemeUpdated(boolean colorSchemeUpdated) {
        this.colorSchemeUpdated = colorSchemeUpdated;
    }

    public void setFaviconManager(@ComponentImport FaviconManager faviconManager) {
        this.faviconManager = faviconManager;
    }

    public boolean isFaviconUpdated() {
        return this.faviconUpdated;
    }

    public void setFaviconUpdated(boolean faviconUpdated) {
        this.faviconUpdated = faviconUpdated;
    }

    public boolean isFaviconReset() {
        return this.faviconReset;
    }

    public void setFaviconReset(boolean faviconReset) {
        this.faviconReset = faviconReset;
    }

    public EventPublisher getEventPublisher() {
        return this.eventPublisher;
    }

    public void setEventPublisher(@ComponentImport EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }
}

