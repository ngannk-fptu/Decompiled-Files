/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.external.ActiveObjects
 *  com.atlassian.annotations.security.XsrfProtectionExcluded
 *  com.atlassian.plugins.rest.common.feature.RequiresDarkFeature
 *  javax.ws.rs.GET
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$ResponseBuilder
 *  net.java.ao.DBParam
 *  org.joda.time.DateTime
 *  org.joda.time.DateTimeZone
 *  org.joda.time.LocalDateTime
 *  org.joda.time.ReadableInstant
 *  org.springframework.beans.factory.annotation.Qualifier
 */
package com.atlassian.confluence.extra.calendar3.rest;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.annotations.security.XsrfProtectionExcluded;
import com.atlassian.confluence.extra.calendar3.ActiveObjectsServiceWrapper;
import com.atlassian.confluence.extra.calendar3.CalendarManager;
import com.atlassian.confluence.extra.calendar3.calendarstore.generic.DefaultChildSubCalendarDataStore;
import com.atlassian.confluence.extra.calendar3.ical4j.VEventMapper;
import com.atlassian.confluence.extra.calendar3.model.ReminderEvent;
import com.atlassian.confluence.extra.calendar3.model.SubCalendarEvent;
import com.atlassian.confluence.extra.calendar3.model.persistence.EventEntity;
import com.atlassian.confluence.extra.calendar3.model.persistence.InviteeEntity;
import com.atlassian.confluence.extra.calendar3.model.rest.CalendarRestResult;
import com.atlassian.confluence.extra.calendar3.model.rest.RestStatusCode;
import com.atlassian.confluence.extra.calendar3.reminder.job.CalendarReminderJob;
import com.atlassian.confluence.extra.calendar3.upgrade.task.aomigration.BandanaToActiveObjectMigrationManager;
import com.atlassian.confluence.extra.calendar3.util.Ical4jIoUtil;
import com.atlassian.plugins.rest.common.feature.RequiresDarkFeature;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.component.VEvent;
import net.java.ao.DBParam;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;
import org.joda.time.ReadableInstant;
import org.springframework.beans.factory.annotation.Qualifier;

@Path(value="/calendar-test")
@RequiresDarkFeature(value={"tc.test.mode"})
@Produces(value={"application/json;charset=UTF-8"})
public class CalendarTestResource {
    private static final String CHARSET_DEFAULT = "UTF-8";
    private final BandanaToActiveObjectMigrationManager migrator;
    private final ActiveObjectsServiceWrapper activeObjectsServiceWrapper;
    private final CalendarManager calendarManager;
    private final CalendarReminderJob calendarReminderJob;
    private final VEventMapper vEventMapper;

    public CalendarTestResource(ActiveObjectsServiceWrapper activeObjectsServiceWrapper, BandanaToActiveObjectMigrationManager migrator, CalendarManager calendarManager, @Qualifier(value="tcReminderJob") CalendarReminderJob calendarReminderJob, VEventMapper vEventMapper) {
        this.migrator = migrator;
        this.activeObjectsServiceWrapper = activeObjectsServiceWrapper;
        this.calendarManager = calendarManager;
        this.calendarReminderJob = calendarReminderJob;
        this.vEventMapper = vEventMapper;
    }

    @GET
    @Path(value="/denseCalendar")
    @Produces(value={"text/calendar; charset=UTF-8"})
    public Response createDenseCalendar(@QueryParam(value="numberEventPerDay") int numberEventPerDay) {
        DateTime firstDateOfCurrentMonth = new DateTime().withDayOfMonth(1);
        DateTime lastDateOfCurrentMonth = firstDateOfCurrentMonth.plusMonths(1);
        ArrayList<SubCalendarEvent> eventList = new ArrayList<SubCalendarEvent>(1000);
        DefaultChildSubCalendarDataStore.DefaultChildSubCalendar subCalendar = new DefaultChildSubCalendarDataStore.DefaultChildSubCalendar();
        while (firstDateOfCurrentMonth.isBefore((ReadableInstant)lastDateOfCurrentMonth)) {
            DateTime startDate = firstDateOfCurrentMonth;
            DateTime endDate = startDate.plusHours(2);
            for (int index = 0; index < numberEventPerDay; ++index) {
                SubCalendarEvent event = new SubCalendarEvent();
                event.setUid(UUID.randomUUID().toString());
                event.setSubCalendar(subCalendar);
                event.setEventType("other");
                event.setEventTypeName("other");
                event.setAllDay(false);
                event.setName("Event " + (index + 1));
                event.setDescription(String.format("Event %d with period %s - %s", index + 1, startDate.toString(), endDate.toString()));
                event.setStartTime(startDate);
                event.setEndTime(endDate);
                eventList.add(event);
            }
            firstDateOfCurrentMonth = firstDateOfCurrentMonth.plusDays(1);
        }
        PropertyList<Property> calendarProperties = new PropertyList<Property>();
        ComponentList<CalendarComponent> calendarComponents = new ComponentList<CalendarComponent>();
        eventList.stream().forEach(subCalendarEvent -> {
            VEvent event = this.vEventMapper.toVEvent(subCalendarEvent.getSubCalendar(), (SubCalendarEvent)subCalendarEvent);
            calendarComponents.add(event);
        });
        Calendar calendar = new Calendar(calendarProperties, calendarComponents);
        Response.ResponseBuilder response = Response.ok(outputStream -> Ical4jIoUtil.newCalendarOutputter().output(calendar, outputStream));
        response.header("Content-Disposition", (Object)"attachment; filename=\"DenseCalendar.ics\"");
        return response.build();
    }

    @XsrfProtectionExcluded
    @GET
    @Produces(value={"application/json"})
    @Path(value="/reminder")
    public Response getReminderEvents() {
        List<ReminderEvent> reminderEventList = this.calendarManager.getEventUpComingReminder();
        return Response.ok(reminderEventList).build();
    }

    @XsrfProtectionExcluded
    @GET
    @Produces(value={"application/json"})
    @Path(value="/reminder/email/{millisecond}")
    public Response sendReminderEvents(@PathParam(value="millisecond") Long milliseconds) {
        this.calendarReminderJob.setReminderSupplier(() -> this.calendarManager.getEventUpComingReminder(milliseconds));
        this.calendarReminderJob.execute();
        return Response.ok((Object)"email sent").build();
    }

    @XsrfProtectionExcluded
    @GET
    @Produces(value={"application/json"})
    @Path(value="/reminder/email/reset")
    public Response resetReminder() {
        this.calendarReminderJob.resetReminderSupplier();
        return Response.ok((Object)"email sent").build();
    }

    @XsrfProtectionExcluded
    @GET
    @Produces(value={"application/json"})
    @Path(value="/reminder/{millisecond}")
    public Response getReminderEvents(@PathParam(value="millisecond") Long milliseconds) {
        List<ReminderEvent> reminderEventList = this.calendarManager.getEventUpComingReminder(milliseconds);
        return Response.ok(reminderEventList).build();
    }

    @XsrfProtectionExcluded
    @GET
    @Produces(value={"application/json"})
    @Path(value="/reminder/startOfDay")
    public Response getReminderEventsAtStartOfDay() {
        DateTime systemUTCTime = new LocalDateTime(System.currentTimeMillis()).toDateTime(DateTimeZone.UTC).withTimeAtStartOfDay().minusMinutes(5);
        List<ReminderEvent> reminderEventList = this.calendarManager.getEventUpComingReminder(systemUTCTime.getMillis());
        return Response.ok(reminderEventList).build();
    }

    @XsrfProtectionExcluded
    @POST
    @Path(value="/reset")
    public Response reset() {
        this.migrator.forceDeleteAllData(this.activeObjectsServiceWrapper);
        CalendarRestResult calendarRestResult = new CalendarRestResult.Builder().withStatusCode(RestStatusCode.OK).withErrorInfo(null).build();
        return Response.ok((Object)calendarRestResult).build();
    }

    @XsrfProtectionExcluded
    @POST
    @Path(value="/event/{eventId}/invitee/{numberOfInvitee}")
    public Response createInviteeForEvent(@PathParam(value="eventId") String eventId, @PathParam(value="numberOfInvitee") int numberOfInvitee) {
        ActiveObjects activeObjects = this.activeObjectsServiceWrapper.getActiveObjects();
        EventEntity eventEntity = ((EventEntity[])activeObjects.find(EventEntity.class, "VEVENT_UID = ?", new Object[]{eventId}))[0];
        for (int i = 1; i <= numberOfInvitee; ++i) {
            activeObjects.create(InviteeEntity.class, new DBParam[]{new DBParam("EVENT_ID", (Object)eventEntity.getID()), new DBParam("INVITEE_ID", (Object)("TestInvitee-" + i))});
        }
        return Response.ok((Object)eventId).build();
    }

    @XsrfProtectionExcluded
    @POST
    @Path(value="/subcalendar/{subCalId}/event/{numberOfEvent}")
    public Response createEventForSubCalendar(@PathParam(value="subCalId") String subCalId, @PathParam(value="numberOfEvent") int numberOfEvent) {
        for (int i = 1; i <= numberOfEvent; ++i) {
            VEvent testEvent = new VEvent(new Date(), new Date(), "test event " + i);
            this.activeObjectsServiceWrapper.createEventEntity(subCalId, testEvent);
        }
        return Response.ok((Object)subCalId).build();
    }
}

