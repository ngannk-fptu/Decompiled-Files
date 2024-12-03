/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.setup.settings.SettingsManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.util.i18n.I18NBean
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.user.User
 *  org.apache.commons.lang3.RandomStringUtils
 *  org.joda.time.DateTime
 *  org.joda.time.DateTimeZone
 *  org.joda.time.ReadableInstant
 *  org.joda.time.format.DateTimeFormat
 *  org.joda.time.format.DateTimeFormatter
 */
package com.atlassian.confluence.extra.calendar3;

import com.atlassian.confluence.compat.api.service.accessmode.AccessModeCompatService;
import com.atlassian.confluence.extra.calendar3.CalendarManager;
import com.atlassian.confluence.extra.calendar3.CalendarPermissionManager;
import com.atlassian.confluence.extra.calendar3.CalendarSettingsManager;
import com.atlassian.confluence.extra.calendar3.JodaIcal4jTimeZoneMapper;
import com.atlassian.confluence.extra.calendar3.SubCalendarSubscriptionStatisticsAccessor;
import com.atlassian.confluence.extra.calendar3.license.DCAwareLicenseAccessor;
import com.atlassian.confluence.extra.calendar3.license.LicenseAccessor;
import com.atlassian.confluence.extra.calendar3.util.BuildInformationManager;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.user.User;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.RandomStringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.ReadableInstant;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public interface CalendarRenderer {
    public CalendarRendererStatus canRenderCalender();

    public String render(Map<RenderParamsBuilder.ParamName, Object> var1);

    public String renderStatic(Map<RenderParamsBuilder.ParamName, Object> var1);

    public RenderParamsBuilder newRenderParamsBuilder();

    public boolean isInvalidateLicense();

    public static class CalendarRendererStatus {
        private boolean canRender;
        private String reason;

        public CalendarRendererStatus(boolean canRender, String reason) {
            this.canRender = canRender;
            this.reason = reason;
        }

        public boolean isCanRender() {
            return this.canRender;
        }

        public void setCanRender(boolean canRender) {
            this.canRender = canRender;
        }

        public String getReason() {
            return this.reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }
    }

    public static class RenderParamsBuilder {
        private static final int MAX_EVENT_PER_UPCOMING_DAY = Integer.getInteger("com.atlassian.confluence.extra.calendar3.display.events.dashboard.maxperday", 20);
        private final SettingsManager settingsManager;
        private final LocaleManager localeManager;
        private final I18NBeanFactory i18NBeanFactory;
        private final PermissionManager permissionManager;
        private final BuildInformationManager buildInformationManager;
        private final LicenseAccessor licenseAccessor;
        private final JodaIcal4jTimeZoneMapper jodaIcal4jTimeZoneMapper;
        private final CalendarManager calendarManager;
        private final CalendarPermissionManager calendarPermissionManager;
        private final SubCalendarSubscriptionStatisticsAccessor subCalendarSubscriptionStatisticsAccessor;
        private final CalendarSettingsManager teamCalendarSettingsManager;
        private final String viewTimeZoneId;
        private boolean readOnly;
        private boolean hideSubCalendarsPanel;
        private boolean hidePaging;
        private CalendarView defaultView;
        private CalendarView defaultPublicView;
        private DateTime initialDate;
        private int height;
        private int width;
        private int timelineHeight;
        private Set<String> subCalendarIds;
        private boolean showFeedbackButton;
        private boolean showInlineAddEventButton;
        private boolean hideViewButtons;
        private boolean suppressPopularSubCalendarsDialogPopup;
        private boolean suppressCreateSubCalendarPopup;
        private boolean showHiddenSubCalendars;
        private boolean showSubCalendarNameInEventPopup;
        private boolean redirectEditInEventPopup;
        private boolean hideDeleteInEventPopup;
        private int maxUpcomingDays = Integer.getInteger("com.atlassian.confluence.extra.calendar3.display.events.calendar.maxdayslistview", 1);
        private int maxEventsPerUpcomingDay = MAX_EVENT_PER_UPCOMING_DAY;
        private boolean autoAdjustUpcomingEventsHeight;
        private boolean hideMoreEventsButtonInUpcomingEventsOnClick;
        private boolean ignoreInvalidSubCalendarIds;
        private CalendarContext calendarContext;
        private boolean hideCalendarTypes;
        private boolean hideWatchMenuItem;
        private boolean hideRemoveMenuItem;
        private boolean hideEditMenuItem;
        private boolean hideColorGrid;
        private boolean hideCategories;
        private boolean showLegendBottom;
        private boolean hideWeekends;
        private boolean isMacroRendering;
        private AccessModeCompatService accessModeCompatService;

        protected RenderParamsBuilder(SettingsManager settingsManager, LocaleManager localeManager, I18NBeanFactory i18NBeanFactory, PermissionManager permissionManager, BuildInformationManager buildInformationManager, LicenseAccessor licenseAccessor, JodaIcal4jTimeZoneMapper jodaIcal4jTimeZoneMapper, CalendarManager calendarManager, CalendarPermissionManager calendarPermissionManager, SubCalendarSubscriptionStatisticsAccessor subCalendarSubscriptionStatisticsAccessor, CalendarSettingsManager teamCalendarSettingsManager, AccessModeCompatService accessModeCompatService) {
            this.settingsManager = settingsManager;
            this.localeManager = localeManager;
            this.i18NBeanFactory = i18NBeanFactory;
            this.permissionManager = permissionManager;
            this.buildInformationManager = buildInformationManager;
            this.licenseAccessor = licenseAccessor;
            this.jodaIcal4jTimeZoneMapper = jodaIcal4jTimeZoneMapper;
            this.calendarManager = calendarManager;
            this.calendarPermissionManager = calendarPermissionManager;
            this.subCalendarSubscriptionStatisticsAccessor = subCalendarSubscriptionStatisticsAccessor;
            this.viewTimeZoneId = jodaIcal4jTimeZoneMapper.getUserTimeZoneIdJoda(AuthenticatedUserThreadLocal.get());
            this.teamCalendarSettingsManager = teamCalendarSettingsManager;
            this.accessModeCompatService = accessModeCompatService;
        }

        public RenderParamsBuilder setMacroRendering(boolean macroRendering) {
            this.isMacroRendering = macroRendering;
            return this;
        }

        public RenderParamsBuilder readOnly(boolean readOnly) {
            this.readOnly = readOnly;
            return this;
        }

        public RenderParamsBuilder hideSubCalendarsPanel(boolean hidden) {
            this.hideSubCalendarsPanel = hidden;
            return this;
        }

        public RenderParamsBuilder hidePaging(boolean hidden) {
            this.hidePaging = hidden;
            return this;
        }

        public RenderParamsBuilder initialView(CalendarView initialView) {
            this.defaultView = initialView;
            return this;
        }

        public RenderParamsBuilder defaultFirePublicView(CalendarView defaultPublicView) {
            this.defaultPublicView = defaultPublicView;
            return this;
        }

        public RenderParamsBuilder initialDate(DateTime initialDate) {
            this.initialDate = null == initialDate ? null : initialDate.withZone(DateTimeZone.forID((String)this.viewTimeZoneId));
            return this;
        }

        public RenderParamsBuilder height(int pixels) {
            this.height = pixels;
            return this;
        }

        public RenderParamsBuilder width(int pixels) {
            this.width = pixels;
            return this;
        }

        public RenderParamsBuilder timelineHeight(int timelineHeight) {
            this.timelineHeight = timelineHeight;
            return this;
        }

        public RenderParamsBuilder subCalendars(Set<String> subCalendarIds) {
            this.subCalendarIds = null == subCalendarIds ? Collections.emptySet() : new HashSet<String>(subCalendarIds);
            return this;
        }

        public RenderParamsBuilder feedbackButton(boolean visible) {
            this.showFeedbackButton = visible;
            return this;
        }

        public RenderParamsBuilder inlineAddEventButton(boolean visible) {
            this.showInlineAddEventButton = visible;
            return this;
        }

        public RenderParamsBuilder viewButtons(boolean visible) {
            this.hideViewButtons = !visible;
            return this;
        }

        public RenderParamsBuilder popularSubCalendarsDialogOnShow(boolean visible) {
            this.suppressPopularSubCalendarsDialogPopup = !visible;
            return this;
        }

        public RenderParamsBuilder createSubCalendarDialogOnShow(boolean visible) {
            this.suppressCreateSubCalendarPopup = !visible;
            return this;
        }

        public RenderParamsBuilder showHiddenSubCalendars(boolean showHiddenSubCalendars) {
            this.showHiddenSubCalendars = showHiddenSubCalendars;
            return this;
        }

        public RenderParamsBuilder showSubCalendarNameInEventPopup(boolean showSubCalendarNameInEventPopup) {
            this.showSubCalendarNameInEventPopup = showSubCalendarNameInEventPopup;
            return this;
        }

        public RenderParamsBuilder redirectEditInEventPopup(boolean redirectEditInEventPopup) {
            this.redirectEditInEventPopup = redirectEditInEventPopup;
            return this;
        }

        public RenderParamsBuilder hideDeleteInEventPopup(boolean hideDeleteInEventPopup) {
            this.hideDeleteInEventPopup = hideDeleteInEventPopup;
            return this;
        }

        public RenderParamsBuilder maxUpcomingDays(int maxUpcomingDays) {
            this.maxUpcomingDays = maxUpcomingDays;
            return this;
        }

        public RenderParamsBuilder maxEventsPerUpcomingDay(int maxEventsPerUpcomingDay) {
            this.maxEventsPerUpcomingDay = maxEventsPerUpcomingDay;
            return this;
        }

        public RenderParamsBuilder autoAdjustUpcomingEventsHeight(boolean autoAdjustUpcomingEventsHeight) {
            this.autoAdjustUpcomingEventsHeight = autoAdjustUpcomingEventsHeight;
            return this;
        }

        public RenderParamsBuilder hideMoreEventsButtonInUpcomingEventsOnClick(boolean hideMoreEventsButtonInUpcomingEventsOnClick) {
            this.hideMoreEventsButtonInUpcomingEventsOnClick = hideMoreEventsButtonInUpcomingEventsOnClick;
            return this;
        }

        public RenderParamsBuilder ignoreInvalidSubCalendarIds(boolean ignoreInvalidSubCalendarIds) {
            this.ignoreInvalidSubCalendarIds = ignoreInvalidSubCalendarIds;
            return this;
        }

        public RenderParamsBuilder calendarContext(CalendarContext calendarContext) {
            this.calendarContext = calendarContext;
            return this;
        }

        public RenderParamsBuilder hideCalendarTypes(boolean hideCalendarTypes) {
            this.hideCalendarTypes = hideCalendarTypes;
            return this;
        }

        public RenderParamsBuilder hideWatchMenuItem(boolean hideWatchMenuItem) {
            this.hideWatchMenuItem = hideWatchMenuItem;
            return this;
        }

        public RenderParamsBuilder hideRemoveMenuItem(boolean hideRemoveMenuItem) {
            this.hideRemoveMenuItem = hideRemoveMenuItem;
            return this;
        }

        public RenderParamsBuilder hideColorGrid(boolean hideColorGrid) {
            this.hideColorGrid = hideColorGrid;
            return this;
        }

        public RenderParamsBuilder hideEditMenuItem(boolean hideEditMenuItem) {
            this.hideEditMenuItem = hideEditMenuItem;
            return this;
        }

        public RenderParamsBuilder hideCategories(boolean hideCategories) {
            this.hideCategories = hideCategories;
            return this;
        }

        public RenderParamsBuilder showLegendBottom(boolean showLegendBottom) {
            this.showLegendBottom = showLegendBottom;
            return this;
        }

        public RenderParamsBuilder hideWeekends(boolean hideWeekends) {
            this.hideWeekends = hideWeekends;
            return this;
        }

        public RenderParamsBuilder subCalendarIds(Set<String> subCalendarIds) {
            this.subCalendarIds = subCalendarIds;
            return this;
        }

        public Map<ParamName, Object> build() {
            HashMap<ParamName, Object> params = new HashMap<ParamName, Object>();
            params.put(ParamName.elementIdSuffix, RandomStringUtils.randomAlphabetic((int)8));
            boolean readOnly = this.readOnly || this.accessModeCompatService.isReadOnlyAccessModeEnabled() || !this.calendarPermissionManager.hasEditSubCalendarPrivilege(AuthenticatedUserThreadLocal.get()) || this.licenseAccessor.isLicenseInvalidated();
            params.put(ParamName.readOnly, readOnly);
            params.put(ParamName.subCalendarClasses, this.calendarManager.getAvailableSubCalendarColorCssClasses());
            params.put(ParamName.userTimeZoneId, this.viewTimeZoneId);
            params.put(ParamName.hideSubCalendarsPanel, this.hideSubCalendarsPanel);
            params.put(ParamName.availableTimeZones, this.getAvailableTimeZones());
            params.put(ParamName.timeZoneOffsetFormatter, DateTimeFormat.forPattern((String)"Z").withLocale(this.getUserLocale()));
            params.put(ParamName.currentDate, new DateTime());
            params.put(ParamName.timeSuggestions, this.getTimeSuggestions());
            if (this.calendarContext == null) {
                this.calendarContext = CalendarContext.unknown;
            }
            params.put(ParamName.calendarContext, (Object)this.calendarContext);
            if (null != this.defaultView) {
                params.put(ParamName.defaultView, this.defaultView.toString());
            }
            if (null != this.defaultPublicView) {
                params.put(ParamName.defaultPublicView, this.defaultPublicView.toString());
            }
            params.put(ParamName.firstDayOfWeek, this.getFirstDayOfWeek());
            params.put(ParamName.disablePaging, this.hidePaging);
            DateTime initialDate = this.initialDate;
            if (null == initialDate) {
                initialDate = new DateTime(DateTimeZone.forID((String)this.viewTimeZoneId));
            }
            params.put(ParamName.startingYear, initialDate.getYear());
            params.put(ParamName.startingMonth, initialDate.getMonthOfYear() - 1);
            params.put(ParamName.startingDay, initialDate.getDayOfMonth());
            if (this.height > 0) {
                params.put(ParamName.height, this.height);
            }
            if (this.width > 0) {
                params.put(ParamName.width, this.width);
            }
            if (this.timelineHeight > 0) {
                params.put(ParamName.timelineHeight, this.timelineHeight);
            }
            if (null != this.subCalendarIds) {
                params.put(ParamName.subCalendarIncludes, this.subCalendarIds);
            }
            params.put(ParamName.ignoreInvalidSubCalendarIds, this.ignoreInvalidSubCalendarIds);
            params.put(ParamName.showFeedbackButton, this.showFeedbackButton);
            params.put(ParamName.showInlineAddEventButton, this.showInlineAddEventButton);
            params.put(ParamName.hideViewButtons, this.hideViewButtons);
            params.put(ParamName.hideCalendarTypes, this.hideCalendarTypes);
            params.put(ParamName.hideWatchMenuItem, this.hideWatchMenuItem);
            params.put(ParamName.hideRemoveMenuItem, this.hideRemoveMenuItem);
            params.put(ParamName.hideEditMenuItem, this.hideEditMenuItem);
            params.put(ParamName.hideColorGrid, this.hideColorGrid);
            params.put(ParamName.hideCategories, this.hideCategories);
            params.put(ParamName.showLegendBottom, this.showLegendBottom);
            params.put(ParamName.hideWeekends, this.hideWeekends);
            ConfluenceUser currentUser = AuthenticatedUserThreadLocal.get();
            boolean isCurrentUserSiteAdmin = this.permissionManager.hasPermission((User)currentUser, Permission.ADMINISTER, PermissionManager.TARGET_APPLICATION);
            if (!readOnly) {
                params.put(ParamName.currentUserSiteAdmin, isCurrentUserSiteAdmin);
                Set<String> subCalendarIdsInView = this.calendarManager.getSubCalendarsInView(AuthenticatedUserThreadLocal.get());
                Set<String> subCalendarsInUsersView = this.calendarManager.filterExistSubCalendarIds(subCalendarIdsInView.toArray(new String[0]));
                params.put(ParamName.isShowCalendarWizard, CalendarContext.myCalendars == this.calendarContext && !this.suppressCreateSubCalendarPopup && !this.suppressPopularSubCalendarsDialogPopup && subCalendarsInUsersView.isEmpty());
            }
            params.put(ParamName.isNotificationsEnabled, this.buildInformationManager.isNotificationsEnabled());
            if (this.licenseAccessor.isLicenseInvalidated()) {
                if (this.licenseAccessor.isLicenseExpired() || !isCurrentUserSiteAdmin) {
                    boolean shouldShowTCLicenseError = true;
                    if (this.licenseAccessor instanceof DCAwareLicenseAccessor) {
                        DCAwareLicenseAccessor dcAwareLicenseAccessor = (DCAwareLicenseAccessor)this.licenseAccessor;
                        boolean bl = shouldShowTCLicenseError = !dcAwareLicenseAccessor.isConfluenceLicenseExpired();
                    }
                    if (shouldShowTCLicenseError) {
                        params.put(ParamName.licenseMessages, this.generateExpirationMessage(isCurrentUserSiteAdmin));
                    }
                } else {
                    params.put(ParamName.licenseMessages, this.licenseAccessor.getInvalidLicenseReasons());
                }
            }
            params.put(ParamName.showHiddenSubCalendars, this.showHiddenSubCalendars);
            params.put(ParamName.showSubCalendarNameInEventPopup, this.showSubCalendarNameInEventPopup);
            params.put(ParamName.redirectEditInEventPopup, this.redirectEditInEventPopup);
            params.put(ParamName.hideDeleteInEventPopup, this.hideDeleteInEventPopup);
            params.put(ParamName.maxUpcomingDays, this.maxUpcomingDays);
            params.put(ParamName.maxEventsPerUpcomingDay, this.maxEventsPerUpcomingDay);
            params.put(ParamName.autoAdjustUpcomingEventsHeight, this.autoAdjustUpcomingEventsHeight);
            params.put(ParamName.hideMoreEventsButtonInUpcomingEventsOnClick, this.hideMoreEventsButtonInUpcomingEventsOnClick);
            if (this.isMacroRendering) {
                params.put(ParamName.showWhatsNew, false);
            }
            return params;
        }

        private List<String> generateExpirationMessage(boolean isCurrentUserSiteAdmin) {
            return Collections.singletonList(this.getText(isCurrentUserSiteAdmin ? "calendar3.licensing.readonlyadmin" : "calendar3.licensing.readonlynonadmin", Collections.singletonList(this.settingsManager.getGlobalSettings().getBaseUrl() + this.licenseAccessor.getLicenseManagerUrl())));
        }

        private int getFirstDayOfWeek() {
            Integer startDayOfWeek = this.teamCalendarSettingsManager.getStartDayOfWeek();
            if (startDayOfWeek == null) {
                return Calendar.getInstance(this.getUserLocale()).getFirstDayOfWeek() - 1;
            }
            return startDayOfWeek % 7;
        }

        private boolean hasPopularSubscriptionForUser() {
            return this.subCalendarSubscriptionStatisticsAccessor.hasPopularSubscriptions(AuthenticatedUserThreadLocal.get());
        }

        private Locale getUserLocale() {
            return this.localeManager.getLocale((User)AuthenticatedUserThreadLocal.get());
        }

        private I18NBean getI18nBean() {
            return this.i18NBeanFactory.getI18NBean(this.getUserLocale());
        }

        private String getText(String i18nKey, List substitutions) {
            return this.getI18nBean().getText(i18nKey, substitutions);
        }

        private Map<String, DateTimeZone> getAvailableTimeZones() {
            LinkedHashMap<String, DateTimeZone> availableTimeZones = new LinkedHashMap<String, DateTimeZone>();
            for (String supportedTimeZoneId : this.jodaIcal4jTimeZoneMapper.getSupportedTimeZoneIds()) {
                availableTimeZones.put(supportedTimeZoneId, DateTimeZone.forID((String)supportedTimeZoneId));
            }
            return availableTimeZones;
        }

        private Collection<String> getTimeSuggestions() {
            DateTime aDate = new DateTime(0, 1, 1, 0, 0, 0, 0);
            LinkedHashSet<String> timeSuggestions = new LinkedHashSet<String>();
            DateTimeFormatter dateFormatter = DateTimeFormat.forPattern((String)(this.teamCalendarSettingsManager.isTimeFormat24Hour() ? "H:mm" : "h:mm a")).withLocale(this.getUserLocale());
            while (1 == aDate.getDayOfMonth()) {
                timeSuggestions.add(dateFormatter.print((ReadableInstant)aDate));
                aDate = aDate.plusMinutes(30);
            }
            return timeSuggestions;
        }

        public static enum ParamName {
            elementIdSuffix,
            readOnly,
            subCalendarClasses,
            userTimeZoneId,
            hideSubCalendarsPanel,
            availableTimeZones,
            timeZoneOffsetFormatter,
            currentDate,
            timeSuggestions,
            defaultView,
            defaultPublicView,
            firstDayOfWeek,
            disablePaging,
            startingYear,
            startingMonth,
            startingDay,
            height,
            width,
            timelineHeight,
            subCalendarIncludes,
            subCalendarId,
            contentId,
            ignoreInvalidSubCalendarIds,
            licenseMessages,
            showFeedbackButton,
            showInlineAddEventButton,
            currentUserSiteAdmin,
            isShowCalendarWizard,
            hideViewButtons,
            isNotificationsEnabled,
            showHiddenSubCalendars,
            showSubCalendarNameInEventPopup,
            redirectEditInEventPopup,
            hideDeleteInEventPopup,
            maxUpcomingDays,
            maxEventsPerUpcomingDay,
            autoAdjustUpcomingEventsHeight,
            hideMoreEventsButtonInUpcomingEventsOnClick,
            calendarContext,
            hideCalendarTypes,
            hideWatchMenuItem,
            hideRemoveMenuItem,
            hideEditMenuItem,
            hideColorGrid,
            hideCategories,
            showLegendBottom,
            hideWeekends,
            macroRendering,
            showWhatsNew,
            enableShareCalendar;

        }
    }

    public static enum CalendarContext {
        page("embedded"),
        dashboard("dashboard"),
        myCalendars("myCalendars"),
        spaceCalendars("spaceCalendars"),
        singleCalendar("singleCalendar"),
        preview("preview"),
        unknown("unknown");

        private String value;

        private CalendarContext(String value) {
            this.value = value;
        }

        public String getValue() {
            return this.value;
        }
    }

    public static enum CalendarView {
        month,
        agendaWeek,
        agendaDay,
        basicDay,
        timeline;

    }
}

