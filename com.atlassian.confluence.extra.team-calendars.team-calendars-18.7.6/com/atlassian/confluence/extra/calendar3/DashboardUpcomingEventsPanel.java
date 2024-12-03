/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugin.services.VelocityHelperService
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.plugin.web.model.WebPanel
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.extra.calendar3;

import com.atlassian.confluence.extra.calendar3.CalendarManager;
import com.atlassian.confluence.extra.calendar3.CalendarPermissionManager;
import com.atlassian.confluence.extra.calendar3.CalendarRenderer;
import com.atlassian.confluence.extra.calendar3.util.CalendarUtil;
import com.atlassian.confluence.plugin.services.VelocityHelperService;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.plugin.web.model.WebPanel;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DashboardUpcomingEventsPanel
implements WebPanel {
    private static final Logger LOG = LoggerFactory.getLogger(DashboardUpcomingEventsPanel.class);
    private final CalendarManager calendarManager;
    private final CalendarPermissionManager calendarPermissionManager;
    private final CalendarRenderer calendarRenderer;
    private final VelocityHelperService velocityHelperService;

    public DashboardUpcomingEventsPanel(CalendarManager calendarManager, CalendarPermissionManager calendarPermissionManager, CalendarRenderer calendarRenderer, VelocityHelperService velocityHelperService) {
        this.calendarManager = calendarManager;
        this.calendarPermissionManager = calendarPermissionManager;
        this.calendarRenderer = calendarRenderer;
        this.velocityHelperService = velocityHelperService;
    }

    public void writeHtml(Writer writer, Map<String, Object> contextMap) throws IOException {
        CalendarRenderer.CalendarRendererStatus status = this.calendarRenderer.canRenderCalender();
        if (status.isCanRender()) {
            writer.write(status.getReason());
            return;
        }
        writer.write(this.getHtml(contextMap));
    }

    public String getHtml(Map<String, Object> contextMap) {
        ConfluenceUser loggedInUser = AuthenticatedUserThreadLocal.get();
        int subCalendarCount = this.calendarManager.getSubCalendarsCount();
        Map velocityContext = this.velocityHelperService.createDefaultVelocityContext();
        CalendarRenderer.CalendarRendererStatus status = this.calendarRenderer.canRenderCalender();
        if (!status.isCanRender()) {
            velocityContext.put("errorMessage", status.getReason());
            return this.velocityHelperService.getRenderedTemplate("com/atlassian/confluence/extra/calendar3/templates/velocity/upcoming-events-error.vm", velocityContext);
        }
        velocityContext.put("showAfterWelcomeMessage", true);
        velocityContext.put("showLinkToMyCalendars", this.calendarPermissionManager.hasEditSubCalendarPrivilege(loggedInUser));
        velocityContext.put("hasSubCalendars", subCalendarCount > 0);
        velocityContext.put("showSmallSadCalendar", true);
        velocityContext.put("emptyPersonalCalendar", subCalendarCount <= 0 || this.calendarManager.isPersonalCalendarEmpty(loggedInUser));
        velocityContext.put("isNewDashboard", CalendarUtil.isNewDashBoard());
        if (LOG.isDebugEnabled()) {
            LOG.debug(String.format("HTTP request for processing for user %s", loggedInUser.getKey()));
        }
        try {
            if (LOG.isDebugEnabled()) {
                LOG.debug(String.format("Rendering upcoming events %s", loggedInUser.getKey()));
            }
            velocityContext.put("upcomingEventsHtml", this.calendarRenderer.render(this.calendarRenderer.newRenderParamsBuilder().hideSubCalendarsPanel(true).initialView(CalendarRenderer.CalendarView.basicDay).defaultFirePublicView(CalendarRenderer.CalendarView.agendaDay).popularSubCalendarsDialogOnShow(false).createSubCalendarDialogOnShow(false).subCalendars(this.calendarManager.getSubCalendarsInView(loggedInUser)).showSubCalendarNameInEventPopup(true).redirectEditInEventPopup(true).hideDeleteInEventPopup(true).maxUpcomingDays(5).autoAdjustUpcomingEventsHeight(true).hideMoreEventsButtonInUpcomingEventsOnClick(true).calendarContext(CalendarRenderer.CalendarContext.dashboard).build()));
            return this.velocityHelperService.getRenderedTemplate("com/atlassian/confluence/extra/calendar3/templates/velocity/upcoming-events.vm", velocityContext);
        }
        catch (RuntimeException errorRenderingUpcomingEvents) {
            LOG.error(String.format("Unable to render upcoming events for %s", loggedInUser.getKey()), (Throwable)errorRenderingUpcomingEvents);
            throw errorRenderingUpcomingEvents;
        }
    }
}

