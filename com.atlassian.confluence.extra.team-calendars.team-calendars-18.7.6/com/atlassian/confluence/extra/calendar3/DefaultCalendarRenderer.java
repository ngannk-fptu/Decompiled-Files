/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.plugin.services.VelocityHelperService
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.setup.settings.SettingsManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.confluence.util.i18n.I18NBean
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.user.User
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.beans.factory.annotation.Qualifier
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.calendar3;

import com.atlassian.confluence.compat.api.service.accessmode.AccessModeCompatService;
import com.atlassian.confluence.extra.calendar3.CalendarManager;
import com.atlassian.confluence.extra.calendar3.CalendarPermissionManager;
import com.atlassian.confluence.extra.calendar3.CalendarRenderer;
import com.atlassian.confluence.extra.calendar3.CalendarSettingsManager;
import com.atlassian.confluence.extra.calendar3.CalendarViewEventFacade;
import com.atlassian.confluence.extra.calendar3.GenericMessage;
import com.atlassian.confluence.extra.calendar3.JodaIcal4jTimeZoneMapper;
import com.atlassian.confluence.extra.calendar3.SubCalendarSubscriptionStatisticsAccessor;
import com.atlassian.confluence.extra.calendar3.calendarstore.JiraAccessor;
import com.atlassian.confluence.extra.calendar3.license.LicenseAccessor;
import com.atlassian.confluence.extra.calendar3.upgrade.task.aomigration.BandanaToActiveObjectMigrationManager;
import com.atlassian.confluence.extra.calendar3.upgrade.task.aomigration.CalendarContentTypeMigrationManager;
import com.atlassian.confluence.extra.calendar3.upgrade.task.aomigration.StatusProvider;
import com.atlassian.confluence.extra.calendar3.util.BuildInformationManager;
import com.atlassian.confluence.extra.calendar3.util.PdlUtil;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.plugin.services.VelocityHelperService;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.user.User;
import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class DefaultCalendarRenderer
implements CalendarRenderer {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultCalendarRenderer.class);
    private static final String AUTOCONVERT_PLUGIN_KEY = "com.atlassian.confluence.plugins.confluence-paste";
    private static int MAX_EVENTS_TO_DISPLAY_PER_CALENDAR;
    private static int MAX_MONTH_TO_DISPLAY_TIMELINE_CALENDAR;
    private final SettingsManager settingsManager;
    private final LocaleManager localeManager;
    private final I18NBeanFactory i18NBeanFactory;
    private final BuildInformationManager buildInformationManager;
    private final LicenseAccessor licenseAccessor;
    private final JodaIcal4jTimeZoneMapper jodaIcal4jTimeZoneMapper;
    private final CalendarManager calendarManager;
    private final CalendarPermissionManager calendarPermissionManager;
    private final SubCalendarSubscriptionStatisticsAccessor subCalendarSubscriptionStatisticsAccessor;
    private final VelocityHelperService velocityHelperService;
    private final JiraAccessor jiraAccessor;
    private final PermissionManager permissionManager;
    private final UserAccessor userAccessor;
    private final PluginAccessor pluginAccessor;
    private final CalendarViewEventFacade calendarViewEventFacade;
    private final CalendarSettingsManager teamCalendarSettingsManager;
    private final BandanaToActiveObjectMigrationManager migrationManager;
    private final CalendarContentTypeMigrationManager calendarContentTypeMigrationManager;
    private final AccessModeCompatService accessModeCompatService;

    @Autowired
    public DefaultCalendarRenderer(@ComponentImport SettingsManager settingsManager, @ComponentImport LocaleManager localeManager, @ComponentImport I18NBeanFactory i18NBeanFactory, BuildInformationManager buildInformationManager, LicenseAccessor licenseAccessor, JodaIcal4jTimeZoneMapper jodaIcal4jTimeZoneMapper, CalendarManager calendarManager, CalendarPermissionManager calendarPermissionManager, SubCalendarSubscriptionStatisticsAccessor subCalendarSubscriptionStatisticsAccessor, @ComponentImport VelocityHelperService velocityHelperService, JiraAccessor jiraAccessor, @ComponentImport PermissionManager permissionManager, @ComponentImport UserAccessor userAccessor, @ComponentImport PluginAccessor pluginAccessor, CalendarViewEventFacade calendarViewEventFacade, CalendarSettingsManager teamCalendarSettingsManager, BandanaToActiveObjectMigrationManager migrationManager, CalendarContentTypeMigrationManager calendarContentTypeMigrationManager, @Qualifier(value="accessModeCompatService") AccessModeCompatService accessModeCompatService) {
        this.settingsManager = settingsManager;
        this.localeManager = localeManager;
        this.i18NBeanFactory = i18NBeanFactory;
        this.buildInformationManager = buildInformationManager;
        this.licenseAccessor = licenseAccessor;
        this.jodaIcal4jTimeZoneMapper = jodaIcal4jTimeZoneMapper;
        this.calendarManager = calendarManager;
        this.calendarPermissionManager = calendarPermissionManager;
        this.subCalendarSubscriptionStatisticsAccessor = subCalendarSubscriptionStatisticsAccessor;
        this.velocityHelperService = velocityHelperService;
        this.jiraAccessor = jiraAccessor;
        this.permissionManager = permissionManager;
        this.userAccessor = userAccessor;
        this.pluginAccessor = pluginAccessor;
        this.calendarViewEventFacade = calendarViewEventFacade;
        this.teamCalendarSettingsManager = teamCalendarSettingsManager;
        this.migrationManager = migrationManager;
        this.calendarContentTypeMigrationManager = calendarContentTypeMigrationManager;
        this.accessModeCompatService = accessModeCompatService;
        MAX_EVENTS_TO_DISPLAY_PER_CALENDAR = teamCalendarSettingsManager.getMaxEventsToDisplayPerCalendar();
    }

    @Override
    public CalendarRenderer.RenderParamsBuilder newRenderParamsBuilder() {
        return new CalendarRenderer.RenderParamsBuilder(this.settingsManager, this.localeManager, this.i18NBeanFactory, this.permissionManager, this.buildInformationManager, this.licenseAccessor, this.jodaIcal4jTimeZoneMapper, this.calendarManager, this.calendarPermissionManager, this.subCalendarSubscriptionStatisticsAccessor, this.teamCalendarSettingsManager, this.accessModeCompatService);
    }

    @Override
    public CalendarRenderer.CalendarRendererStatus canRenderCalender() {
        if (this.isMigrationRunningFor(this.migrationManager) || this.isMigrationRunningFor(this.calendarContentTypeMigrationManager)) {
            I18NBean i18NBean = this.i18NBeanFactory.getI18NBean();
            String message = this.migrationManager.getInProgressMessage(i18NBean);
            return new CalendarRenderer.CalendarRendererStatus(false, message);
        }
        return new CalendarRenderer.CalendarRendererStatus(true, "");
    }

    @Override
    public String render(Map<CalendarRenderer.RenderParamsBuilder.ParamName, Object> params) {
        Map macroVelocityContext = this.velocityHelperService.createDefaultVelocityContext();
        macroVelocityContext.putAll(this.toMacroVelocityContext(params));
        LinkedHashSet<GenericMessage> errorMessages = new LinkedHashSet<GenericMessage>();
        macroVelocityContext.put("errorMessages", errorMessages);
        Set subCalendarIncludes = (Set)params.get((Object)CalendarRenderer.RenderParamsBuilder.ParamName.subCalendarIncludes);
        if (null != subCalendarIncludes && !subCalendarIncludes.isEmpty()) {
            LinkedHashSet<String> invalidSubCalendarIncludes = new LinkedHashSet<String>();
            for (String subCalendarId : subCalendarIncludes) {
                if (this.calendarManager.hasSubCalendar(subCalendarId)) continue;
                invalidSubCalendarIncludes.add(subCalendarId);
            }
            if (!invalidSubCalendarIncludes.isEmpty()) {
                boolean ignoreInvalidSubCalendarIncludes = (Boolean)params.get((Object)CalendarRenderer.RenderParamsBuilder.ParamName.ignoreInvalidSubCalendarIds);
                if (ignoreInvalidSubCalendarIncludes) {
                    LOG.info(String.format("The sub-calendars of IDs %s will not be rendered. They no longer exist.", StringUtils.join(invalidSubCalendarIncludes, ", ")));
                } else {
                    errorMessages.add(new GenericMessage("calendar3.error.invalidsubcalendar", new Serializable[]{StringEscapeUtils.escapeHtml(StringUtils.join(invalidSubCalendarIncludes, ", "))}));
                }
            }
        }
        macroVelocityContext.put("hasJiraLink", !this.jiraAccessor.getLinkedJiraApplications().isEmpty());
        macroVelocityContext.put("privateUrlsEnabled", this.teamCalendarSettingsManager.arePrivateUrlsEnabled());
        Set<String> disabledMessageKeys = this.calendarManager.getUserPreference(AuthenticatedUserThreadLocal.get()).getDisabledMessageKeys();
        if (this.buildInformationManager.isShowingWhatsNew()) {
            String whatsNewMessageKey = "MESSAGE_KEY_PREFIX_WHATSNEW_" + this.buildInformationManager.getVersion();
            if (!macroVelocityContext.containsKey("showWhatsNew")) {
                macroVelocityContext.put("showWhatsNew", !disabledMessageKeys.contains(whatsNewMessageKey));
            }
            macroVelocityContext.put("pluginVersion", this.buildInformationManager.getVersion());
        }
        macroVelocityContext.put("autowatch", this.userAccessor.getConfluenceUserPreferences((User)AuthenticatedUserThreadLocal.get()).isWatchingOwnContent());
        macroVelocityContext.put("autoConvertSupported", this.isAutoConvertSupported());
        macroVelocityContext.put("maxEventsToDisplayPerCalendar", MAX_EVENTS_TO_DISPLAY_PER_CALENDAR);
        macroVelocityContext.put("pdlEnabled", PdlUtil.isPdlEnabled());
        macroVelocityContext.put("maxMonthToDisplayTimelineCalendar", MAX_MONTH_TO_DISPLAY_TIMELINE_CALENDAR);
        this.publishViewEvent(params);
        return this.velocityHelperService.getRenderedTemplate("com/atlassian/confluence/extra/calendar3/templates/velocity/calendar-macro.vm", macroVelocityContext);
    }

    private boolean isAutoConvertSupported() {
        return null != this.pluginAccessor.getPlugin(AUTOCONVERT_PLUGIN_KEY) && this.pluginAccessor.isPluginEnabled(AUTOCONVERT_PLUGIN_KEY) && null != this.pluginAccessor.getPluginModule("com.atlassian.confluence.plugins.confluence-paste:autoconvert-core");
    }

    private Map<String, Object> toMacroVelocityContext(Map<CalendarRenderer.RenderParamsBuilder.ParamName, Object> params) {
        HashMap<String, Object> velocityContext = new HashMap<String, Object>();
        for (Map.Entry<CalendarRenderer.RenderParamsBuilder.ParamName, Object> entry : params.entrySet()) {
            velocityContext.put(entry.getKey().toString(), entry.getValue());
        }
        return velocityContext;
    }

    @Override
    public String renderStatic(Map<CalendarRenderer.RenderParamsBuilder.ParamName, Object> params) {
        Map macroVelocityContext = this.velocityHelperService.createDefaultVelocityContext();
        macroVelocityContext.putAll(this.toMacroVelocityContext(params));
        this.publishViewEvent(params);
        return this.velocityHelperService.getRenderedTemplate("com/atlassian/confluence/extra/calendar3/exports/export.vm", macroVelocityContext);
    }

    private boolean isMigrationRunningFor(StatusProvider statusProvider) {
        return statusProvider != null && statusProvider.getStatus() == StatusProvider.RunningStatus.RUNNING;
    }

    private void publishViewEvent(Map<CalendarRenderer.RenderParamsBuilder.ParamName, Object> params) {
        String view = (String)params.get((Object)CalendarRenderer.RenderParamsBuilder.ParamName.defaultPublicView);
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        if (view == null) {
            view = this.calendarManager.getUserPreference(user).getCalendarView();
        }
        CalendarRenderer.CalendarContext context = (CalendarRenderer.CalendarContext)((Object)params.get((Object)CalendarRenderer.RenderParamsBuilder.ParamName.calendarContext));
        this.calendarViewEventFacade.publishEvent(this, user, view, context.getValue());
    }

    @Override
    public boolean isInvalidateLicense() {
        return this.licenseAccessor.isLicenseInvalidated() || this.licenseAccessor.isLicenseExpired();
    }

    static {
        MAX_MONTH_TO_DISPLAY_TIMELINE_CALENDAR = Integer.getInteger("com.atlassian.confluence.extra.calendar3.display.timeline.calendar.maxmonth", 6);
    }
}

