/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ConfluenceActionSupport
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.pages.Attachment
 *  com.atlassian.confluence.pages.AttachmentManager
 *  com.atlassian.confluence.setup.settings.Settings
 *  com.atlassian.confluence.setup.settings.SettingsManager
 *  com.atlassian.confluence.velocity.htmlsafe.HtmlSafe
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.webresource.UrlMode
 *  com.atlassian.plugin.webresource.WebResourceUrlProvider
 *  org.springframework.beans.factory.annotation.Qualifier
 */
package com.atlassian.confluence.extra.calendar3.xwork;

import com.atlassian.confluence.compat.api.service.accessmode.AccessModeCompatService;
import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.extra.calendar3.CalendarManager;
import com.atlassian.confluence.extra.calendar3.CalendarPermissionManager;
import com.atlassian.confluence.extra.calendar3.CalendarRenderer;
import com.atlassian.confluence.extra.calendar3.contenttype.CalendarContentTypeManager;
import com.atlassian.confluence.extra.calendar3.events.CalendarDashboardViewEvent;
import com.atlassian.confluence.extra.calendar3.util.BuildInformationManager;
import com.atlassian.confluence.extra.calendar3.util.PdlUtil;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.setup.settings.Settings;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.velocity.htmlsafe.HtmlSafe;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.webresource.UrlMode;
import com.atlassian.plugin.webresource.WebResourceUrlProvider;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Qualifier;

public class CalendarAction
extends ConfluenceActionSupport {
    private CalendarPermissionManager calendarPermissionManager;
    private CalendarRenderer calendarRenderer;
    private WebResourceUrlProvider webResourceUrlProvider;
    private BuildInformationManager buildInformationManager;
    private AttachmentManager attachmentManager;
    protected SettingsManager settingsManager;
    protected CalendarManager calendarManager;
    protected EventPublisher eventPublisher;
    protected CalendarContentTypeManager calendarContentTypeManager;
    protected AccessModeCompatService accessModeCompatService;
    private Map<CalendarRenderer.RenderParamsBuilder.ParamName, Object> params;

    public void setCalendarPermissionManager(CalendarPermissionManager calendarPermissionManager) {
        this.calendarPermissionManager = calendarPermissionManager;
    }

    public void setCalendarRenderer(CalendarRenderer calendarRenderer) {
        this.calendarRenderer = calendarRenderer;
    }

    public void setWebResourceUrlProvider(WebResourceUrlProvider webResourceUrlProvider) {
        this.webResourceUrlProvider = webResourceUrlProvider;
    }

    public void setBuildInformationManager(BuildInformationManager buildInformationManager) {
        this.buildInformationManager = buildInformationManager;
    }

    public void setAttachmentManager(AttachmentManager attachmentManager) {
        this.attachmentManager = attachmentManager;
    }

    public void setSettingsManager(SettingsManager settingsManager) {
        this.settingsManager = settingsManager;
    }

    public void setCalendarManager(CalendarManager calendarManager) {
        this.calendarManager = calendarManager;
    }

    public void setCalendarContentTypeManager(CalendarContentTypeManager calendarContentTypeManager) {
        this.calendarContentTypeManager = calendarContentTypeManager;
    }

    @Qualifier(value="accessModeCompatService")
    public void setAccessModeCompatService(AccessModeCompatService accessModeCompatService) {
        this.accessModeCompatService = accessModeCompatService;
    }

    public boolean isPermitted() {
        return super.isPermitted() && this.calendarPermissionManager.hasEditSubCalendarPrivilege(this.getAuthenticatedUser());
    }

    public void setEventPublisher(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public String execute() throws Exception {
        this.triggerDashboardViewEvent(false);
        return "success";
    }

    @HtmlSafe
    public String getCalendarHtml() throws Exception {
        CalendarRenderer.CalendarRendererStatus status = this.calendarRenderer.canRenderCalender();
        if (!status.isCanRender()) {
            return status.getReason();
        }
        return this.calendarRenderer.render(this.updateRenderParams(this.getParams()));
    }

    public Map<CalendarRenderer.RenderParamsBuilder.ParamName, Object> getParams() {
        if (this.params == null) {
            this.params = this.calendarRenderer.newRenderParamsBuilder().calendarContext(this.getCalendarContext()).subCalendarIds(this.getSubCalendarIds()).build();
        }
        return this.params;
    }

    protected Map<CalendarRenderer.RenderParamsBuilder.ParamName, Object> updateRenderParams(Map<CalendarRenderer.RenderParamsBuilder.ParamName, Object> commonParams) {
        commonParams.put(CalendarRenderer.RenderParamsBuilder.ParamName.enableShareCalendar, this.pluginAccessor.getEnabledPlugin("com.atlassian.confluence.plugins.share-page") != null);
        return commonParams;
    }

    protected CalendarRenderer.CalendarContext getCalendarContext() {
        return CalendarRenderer.CalendarContext.myCalendars;
    }

    public String getCalendarContextStr() {
        return this.getCalendarContext().getValue();
    }

    protected Set<String> getSubCalendarIds() {
        return null;
    }

    protected String getCalendarId() {
        return null;
    }

    public boolean isPDLEnabled() {
        return PdlUtil.isPdlEnabled();
    }

    public boolean isInvalidateLicense() {
        return this.calendarRenderer.isInvalidateLicense();
    }

    public boolean isReadOnly() {
        return this.accessModeCompatService.isReadOnlyAccessModeEnabled();
    }

    public String getLogoIconUrl() {
        Attachment globalLogo = this.getGlobalLogoAttachment();
        if (null == globalLogo) {
            return this.webResourceUrlProvider.getStaticPluginResourceUrl(this.buildInformationManager.getPluginKey() + ":calendar-resources", "img/logo_48.png", UrlMode.ABSOLUTE);
        }
        return String.format("%s%s", this.settingsManager.getGlobalSettings().getBaseUrl(), globalLogo.getDownloadPath());
    }

    private Attachment getGlobalLogoAttachment() {
        Settings globalSettings = this.settingsManager.getGlobalSettings();
        if (globalSettings.isDisableLogo()) {
            return null;
        }
        return this.attachmentManager.getAttachment((ContentEntityObject)this.settingsManager.getGlobalDescription(), "global.logo");
    }

    protected void triggerDashboardViewEvent(boolean isOnSpace) {
        this.eventPublisher.publish((Object)new CalendarDashboardViewEvent(isOnSpace, (Object)this, this.getAuthenticatedUser()));
    }
}

