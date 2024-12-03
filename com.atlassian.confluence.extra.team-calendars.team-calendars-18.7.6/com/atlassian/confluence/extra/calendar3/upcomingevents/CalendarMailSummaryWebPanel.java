/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.botocss.Botocss
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.plugin.web.model.WebPanel
 *  com.google.common.base.Predicate
 *  com.google.common.base.Predicates
 *  com.google.common.collect.Collections2
 *  org.apache.commons.io.IOUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.extra.calendar3.upcomingevents;

import com.atlassian.botocss.Botocss;
import com.atlassian.confluence.extra.calendar3.CalendarManager;
import com.atlassian.confluence.extra.calendar3.model.SubCalendarSummary;
import com.atlassian.confluence.extra.calendar3.upcomingevents.MailSummaryRenderer;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.plugin.web.model.WebPanel;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.Collection;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CalendarMailSummaryWebPanel
implements WebPanel {
    private static final Logger LOG = LoggerFactory.getLogger(CalendarMailSummaryWebPanel.class);
    private CalendarManager calendarManager;
    private MailSummaryRenderer mailSummaryRenderer;

    public CalendarMailSummaryWebPanel(CalendarManager calendarManager, MailSummaryRenderer mailSummaryRenderer) {
        this.calendarManager = calendarManager;
        this.mailSummaryRenderer = mailSummaryRenderer;
    }

    public void writeHtml(Writer writer, Map<String, Object> context) throws IOException {
        writer.append(this.getHtml(context));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public String getHtml(Map<String, Object> context) {
        ConfluenceUser originalUserInAuthenticatedUserThreadLocal = AuthenticatedUserThreadLocal.get();
        ConfluenceUser user = (ConfluenceUser)context.get("summary-recipient");
        AuthenticatedUserThreadLocal.set((ConfluenceUser)user);
        try {
            Collection<String> subCalendarIds = this.calculateCalendarIdsFor(user);
            String string = Botocss.inject((String)this.mailSummaryRenderer.renderUpcomingEventsForMail(subCalendarIds, true, user), (String[])new String[]{this.getCss("com/atlassian/confluence/extra/calendar3/css/mail-calendar.css")});
            return string;
        }
        catch (IOException ioError) {
            LOG.error("Error rendering upcoming events in summary email: Unable to inject styles to it.", (Throwable)ioError);
            String string = "";
            return string;
        }
        finally {
            AuthenticatedUserThreadLocal.set((ConfluenceUser)originalUserInAuthenticatedUserThreadLocal);
        }
    }

    private Collection<String> calculateCalendarIdsFor(ConfluenceUser user) {
        return Collections2.transform((Collection)Collections2.filter((Collection)Collections2.transform(this.calendarManager.getSubCalendarsInView(user), subCalendarId -> this.calendarManager.getSubCalendarSummary((String)subCalendarId)), (Predicate)Predicates.notNull()), SubCalendarSummary::getId);
    }

    private String getCss(String path) throws IOException {
        try (InputStream is = this.getClass().getClassLoader().getResourceAsStream(path);){
            String string = IOUtils.toString((InputStream)is, (String)"UTF-8");
            return string;
        }
    }
}

