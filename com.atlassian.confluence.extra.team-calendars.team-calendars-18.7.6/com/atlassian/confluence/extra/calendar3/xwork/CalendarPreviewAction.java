/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ConfluenceActionSupport
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.confluence.util.breadcrumbs.Breadcrumb
 *  com.atlassian.confluence.util.breadcrumbs.BreadcrumbAware
 *  com.atlassian.confluence.util.breadcrumbs.DashboardBreadcrumb
 *  com.atlassian.confluence.velocity.htmlsafe.HtmlSafe
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.webresource.UrlMode
 *  com.atlassian.plugin.webresource.WebResourceUrlProvider
 *  com.atlassian.xwork.RequireSecurityToken
 *  com.google.common.base.Predicate
 *  com.google.common.base.Predicates
 *  com.google.common.collect.Collections2
 */
package com.atlassian.confluence.extra.calendar3.xwork;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.extra.calendar3.CalendarManager;
import com.atlassian.confluence.extra.calendar3.CalendarPermissionManager;
import com.atlassian.confluence.extra.calendar3.CalendarRenderer;
import com.atlassian.confluence.extra.calendar3.events.SubCalendarIndexOutOfSynch;
import com.atlassian.confluence.extra.calendar3.model.PersistedSubCalendar;
import com.atlassian.confluence.extra.calendar3.model.SubCalendar;
import com.atlassian.confluence.extra.calendar3.model.SubscribingSubCalendar;
import com.atlassian.confluence.extra.calendar3.util.BuildInformationManager;
import com.atlassian.confluence.extra.calendar3.util.PdlUtil;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.util.breadcrumbs.Breadcrumb;
import com.atlassian.confluence.util.breadcrumbs.BreadcrumbAware;
import com.atlassian.confluence.util.breadcrumbs.DashboardBreadcrumb;
import com.atlassian.confluence.velocity.htmlsafe.HtmlSafe;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.webresource.UrlMode;
import com.atlassian.plugin.webresource.WebResourceUrlProvider;
import com.atlassian.xwork.RequireSecurityToken;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import org.apache.commons.lang.StringUtils;

public class CalendarPreviewAction
extends ConfluenceActionSupport
implements BreadcrumbAware {
    private String subCalendarId;
    private CalendarManager calendarManager;
    private CalendarPermissionManager calendarPermissionManager;
    private CalendarRenderer calendarRenderer;
    private WebResourceUrlProvider webResourceUrlProvider;
    private BuildInformationManager buildInformationManager;
    private EventPublisher eventPublisher;
    private SpaceManager spaceManager;
    private Space space;

    public String getSubCalendarId() {
        return this.subCalendarId;
    }

    public void setSubCalendarId(String subCalendarId) {
        this.subCalendarId = subCalendarId;
    }

    public String getSpaceCalendarUrl() {
        return this.space != null ? String.format("%s/display/%s/calendars", this.settingsManager.getGlobalSettings().getBaseUrl(), this.space.getKey()) : "";
    }

    public String getSpaceName() {
        return this.space != null ? this.space.getName() : "";
    }

    public String getSpaceLink() {
        return this.space != null ? String.format("%s/display/%s", this.settingsManager.getGlobalSettings().getBaseUrl(), this.space.getKey()) : "";
    }

    private void setSpace(PersistedSubCalendar persistedSubCalendar) {
        String spaceKey;
        if (persistedSubCalendar != null && StringUtils.isNotBlank(spaceKey = persistedSubCalendar.getSpaceKey())) {
            this.space = this.spaceManager.getSpace(spaceKey);
        }
    }

    public void setEventPublisher(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public void setCalendarManager(CalendarManager calendarManager) {
        this.calendarManager = calendarManager;
    }

    public CalendarManager getCalendarManager() {
        return this.calendarManager;
    }

    public void setCalendarPermissionManager(CalendarPermissionManager calendarPermissionManager) {
        this.calendarPermissionManager = calendarPermissionManager;
    }

    public CalendarPermissionManager getCalendarPermissionManager() {
        return this.calendarPermissionManager;
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

    public void setSpaceManager(SpaceManager spaceManager) {
        this.spaceManager = spaceManager;
    }

    public String doDefault() throws Exception {
        if (this.isSubCalendarPreviewable()) {
            PersistedSubCalendar persistedSubCalendar = this.calendarManager.getSubCalendar(this.getSubCalendarId());
            this.setSpace(persistedSubCalendar);
            if (persistedSubCalendar instanceof SubscribingSubCalendar) {
                this.setSubCalendarId(((SubscribingSubCalendar)persistedSubCalendar).getSubscriptionId());
                return "requery";
            }
            return super.doDefault();
        }
        return "notfound";
    }

    public String getSubCalendarName() {
        return this.calendarManager.getSubCalendar(this.getSubCalendarId()).getName();
    }

    private boolean isSubCalendarPreviewable() {
        String subCalendarId = StringUtils.defaultString(this.getSubCalendarId());
        if (this.calendarManager.hasSubCalendar(subCalendarId)) {
            PersistedSubCalendar subCalendaToPreview = this.calendarManager.getSubCalendar(subCalendarId);
            return this.calendarPermissionManager.hasViewEventPrivilege(subCalendaToPreview, this.getAuthenticatedUser());
        }
        this.eventPublisher.publish((Object)new SubCalendarIndexOutOfSynch(subCalendarId, this.getAuthenticatedUser()));
        return false;
    }

    @RequireSecurityToken(value=true)
    public String execute() throws Exception {
        CalendarRenderer.CalendarRendererStatus status = this.calendarRenderer.canRenderCalender();
        if (!status.isCanRender()) {
            return status.getReason();
        }
        PersistedSubCalendar subCalendarToSubscribe = this.calendarManager.getSubCalendar(this.getSubCalendarId());
        SubCalendar subscription = new SubCalendar();
        subscription.setType("internal-subscription");
        subscription.setName(subCalendarToSubscribe.getName());
        subscription.setDescription(subCalendarToSubscribe.getDescription());
        subscription.setColor(this.calendarManager.getRandomCalendarColor(new String[0]));
        subscription.setSourceLocation("subscription://" + subCalendarToSubscribe.getId());
        this.calendarManager.save(subscription);
        return "success";
    }

    public void validate() {
        super.validate();
        if (!this.isSubCalendarPreviewable()) {
            this.addActionError("calendar3.error.notsubsribeable", new Object[]{StringUtils.defaultString(this.getSubCalendarId())});
        }
    }

    @HtmlSafe
    public String getCalendarHtml() throws Exception {
        return this.calendarRenderer.render(this.calendarRenderer.newRenderParamsBuilder().readOnly(true).subCalendars(new HashSet<String>(Arrays.asList(this.getSubCalendarId()))).hideSubCalendarsPanel(true).createSubCalendarDialogOnShow(false).popularSubCalendarsDialogOnShow(false).calendarContext(CalendarRenderer.CalendarContext.preview).build());
    }

    public boolean isSubscribingAllowed() {
        return this.calendarPermissionManager.hasEditSubCalendarPrivilege(this.getAuthenticatedUser()) && this.isNotSubscribingToSubCalendar();
    }

    private boolean isNotSubscribingToSubCalendar() {
        return Collections2.filter((Collection)Collections2.transform(this.calendarManager.getSubCalendarsInView(this.getAuthenticatedUser()), subCalendarId -> this.calendarManager.getSubCalendar((String)subCalendarId)), (Predicate)Predicates.and((Predicate)Predicates.notNull(), persistedSubCalendar -> StringUtils.equals(this.subCalendarId, persistedSubCalendar instanceof SubscribingSubCalendar ? ((SubscribingSubCalendar)persistedSubCalendar).getSubscriptionId() : persistedSubCalendar.getId()))).isEmpty();
    }

    public Breadcrumb getBreadcrumb() {
        return new CalendarPreviewBreadcrumb(this.getSubCalendarName());
    }

    public String getLogoIconUrl() {
        return this.webResourceUrlProvider.getStaticPluginResourceUrl(this.buildInformationManager.getPluginKey() + ":calendar-resources", "img/logo_48.png", UrlMode.ABSOLUTE);
    }

    public boolean isPDLEnabled() {
        return PdlUtil.isPdlEnabled();
    }

    private static class CalendarPreviewBreadcrumb
    implements Breadcrumb {
        private final String subCalendarName;

        private CalendarPreviewBreadcrumb(String subCalendarName) {
            this.subCalendarName = subCalendarName;
        }

        public String getTarget() {
            return null;
        }

        public String getTitle() {
            return this.subCalendarName;
        }

        public String getDisplayTitle() {
            return this.subCalendarName;
        }

        public String getTooltip() {
            return this.subCalendarName;
        }

        public boolean filterTrailingBreadcrumb() {
            return false;
        }

        public void setFilterTrailingBreadcrumb(boolean b) {
        }

        public String getCssClass() {
            return "";
        }

        public void setCssClass(String s) {
        }

        public List<Breadcrumb> getBreadcrumbsTrail() {
            return Arrays.asList(DashboardBreadcrumb.getInstance(), new Breadcrumb(){

                public String getTarget() {
                    return "/calendar/mycalendar.action";
                }

                public String getTitle() {
                    return "com.atlassian.confluence.extra.calendar3.xwork.CalendarAction.action.name";
                }

                public String getDisplayTitle() {
                    return null;
                }

                public String getTooltip() {
                    return null;
                }

                public List<Breadcrumb> getBreadcrumbsTrail() {
                    return Arrays.asList(DashboardBreadcrumb.getInstance(), this);
                }

                public String getCssClass() {
                    return null;
                }

                public void setCssClass(String s) {
                }

                public void setFilterTrailingBreadcrumb(boolean b) {
                }

                public boolean filterTrailingBreadcrumb() {
                    return false;
                }
            }, this);
        }
    }
}

