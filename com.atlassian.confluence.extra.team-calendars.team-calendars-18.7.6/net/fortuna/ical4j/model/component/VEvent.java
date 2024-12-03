/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.builder.HashCodeBuilder
 */
package net.fortuna.ical4j.model.component;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.time.temporal.TemporalAmount;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.ComponentFactory;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.Content;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.Period;
import net.fortuna.ical4j.model.PeriodList;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.component.VAlarm;
import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.model.property.Clazz;
import net.fortuna.ical4j.model.property.Created;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.DtEnd;
import net.fortuna.ical4j.model.property.DtStamp;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.Duration;
import net.fortuna.ical4j.model.property.Geo;
import net.fortuna.ical4j.model.property.LastModified;
import net.fortuna.ical4j.model.property.Location;
import net.fortuna.ical4j.model.property.Method;
import net.fortuna.ical4j.model.property.Organizer;
import net.fortuna.ical4j.model.property.Priority;
import net.fortuna.ical4j.model.property.RecurrenceId;
import net.fortuna.ical4j.model.property.Sequence;
import net.fortuna.ical4j.model.property.Status;
import net.fortuna.ical4j.model.property.Summary;
import net.fortuna.ical4j.model.property.Transp;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.model.property.Url;
import net.fortuna.ical4j.util.CompatibilityHints;
import net.fortuna.ical4j.util.Dates;
import net.fortuna.ical4j.validate.PropertyValidator;
import net.fortuna.ical4j.validate.ValidationException;
import net.fortuna.ical4j.validate.ValidationRule;
import net.fortuna.ical4j.validate.Validator;
import net.fortuna.ical4j.validate.component.VEventValidator;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class VEvent
extends CalendarComponent {
    private static final long serialVersionUID = 2547948989200697335L;
    private final Map<Method, Validator> methodValidators = new HashMap<Method, Validator>();
    private ComponentList<VAlarm> alarms;

    public VEvent() {
        this(true);
    }

    public VEvent(boolean initialise) {
        super("VEVENT");
        this.methodValidators.put(Method.ADD, new VEventValidator(new ValidationRule(ValidationRule.ValidationType.One, "DTSTAMP", "DTSTART", "ORGANIZER", "SEQUENCE", "SUMMARY", "UID"), new ValidationRule(ValidationRule.ValidationType.OneOrLess, "CATEGORIES", "CLASS", "CREATED", "DESCRIPTION", "DTEND", "DURATION", "GEO", "LAST-MODIFIED", "LOCATION", "PRIORITY", "RESOURCES", "STATUS", "TRANSP", "URL"), new ValidationRule(ValidationRule.ValidationType.None, "RECURRENCE-ID", "REQUEST-STATUS")));
        this.methodValidators.put(Method.CANCEL, new VEventValidator(false, new ValidationRule(ValidationRule.ValidationType.One, "DTSTAMP", "DTSTART", "ORGANIZER", "SEQUENCE", "UID"), new ValidationRule(ValidationRule.ValidationType.OneOrLess, "CATEGORIES", "CLASS", "CREATED", "DESCRIPTION", "DTEND", "DTSTART", "DURATION", "GEO", "LAST-MODIFIED", "LOCATION", "PRIORITY", "RECURRENCE-ID", "RESOURCES", "STATUS", "SUMMARY", "TRANSP", "URL"), new ValidationRule(ValidationRule.ValidationType.None, "REQUEST-STATUS")));
        this.methodValidators.put(Method.COUNTER, new VEventValidator(new ValidationRule(ValidationRule.ValidationType.One, "DTSTAMP", "DTSTART", "SEQUENCE", "SUMMARY", "UID"), new ValidationRule(ValidationRule.ValidationType.One, true, "ORGANIZER"), new ValidationRule(ValidationRule.ValidationType.OneOrLess, "CATEGORIES", "CLASS", "CREATED", "DESCRIPTION", "DTEND", "DURATION", "GEO", "LAST-MODIFIED", "LOCATION", "PRIORITY", "RECURRENCE-ID", "RESOURCES", "STATUS", "TRANSP", "URL")));
        this.methodValidators.put(Method.DECLINE_COUNTER, new VEventValidator(false, new ValidationRule(ValidationRule.ValidationType.One, "DTSTAMP", "ORGANIZER", "UID"), new ValidationRule(ValidationRule.ValidationType.OneOrLess, "RECURRENCE-ID", "SEQUENCE"), new ValidationRule(ValidationRule.ValidationType.None, "ATTACH", "ATTENDEE", "CATEGORIES", "CLASS", "CONTACT", "CREATED", "DESCRIPTION", "DTEND", "DTSTART", "DURATION", "EXDATE", "EXRULE", "GEO", "LAST-MODIFIED", "LOCATION", "PRIORITY", "RDATE", "RELATED-TO", "RESOURCES", "RRULE", "STATUS", "SUMMARY", "TRANSP", "URL")));
        this.methodValidators.put(Method.PUBLISH, new VEventValidator(new ValidationRule(ValidationRule.ValidationType.One, "DTSTART", "UID"), new ValidationRule(ValidationRule.ValidationType.One, true, "DTSTAMP", "ORGANIZER", "SUMMARY"), new ValidationRule(ValidationRule.ValidationType.OneOrLess, "RECURRENCE-ID", "SEQUENCE", "CATEGORIES", "CLASS", "CREATED", "DESCRIPTION", "DTEND", "DURATION", "GEO", "LAST-MODIFIED", "LOCATION", "PRIORITY", "RESOURCES", "STATUS", "TRANSP", "URL"), new ValidationRule(ValidationRule.ValidationType.None, true, "ATTENDEE"), new ValidationRule(ValidationRule.ValidationType.None, "REQUEST-STATUS")));
        this.methodValidators.put(Method.REFRESH, new VEventValidator(false, new ValidationRule(ValidationRule.ValidationType.One, "ATTENDEE", "DTSTAMP", "ORGANIZER", "UID"), new ValidationRule(ValidationRule.ValidationType.OneOrLess, "RECURRENCE-ID"), new ValidationRule(ValidationRule.ValidationType.None, "ATTACH", "CATEGORIES", "CLASS", "CONTACT", "CREATED", "DESCRIPTION", "DTEND", "DTSTART", "DURATION", "EXDATE", "EXRULE", "GEO", "LAST-MODIFIED", "LOCATION", "PRIORITY", "RDATE", "RELATED-TO", "REQUEST-STATUS", "RESOURCES", "RRULE", "SEQUENCE", "STATUS", "SUMMARY", "TRANSP", "URL")));
        this.methodValidators.put(Method.REPLY, new VEventValidator(CompatibilityHints.isHintEnabled("ical4j.validation.relaxed"), new ValidationRule(ValidationRule.ValidationType.One, "ATTENDEE", "DTSTAMP", "ORGANIZER", "UID"), new ValidationRule(ValidationRule.ValidationType.OneOrLess, "RECURRENCE-ID", "SEQUENCE", "CATEGORIES", "CLASS", "CREATED", "DESCRIPTION", "DTEND", "DTSTART", "DURATION", "GEO", "LAST-MODIFIED", "LOCATION", "PRIORITY", "RESOURCES", "STATUS", "SUMMARY", "TRANSP", "URL")));
        this.methodValidators.put(Method.REQUEST, new VEventValidator(new ValidationRule(ValidationRule.ValidationType.OneOrMore, true, "ATTENDEE"), new ValidationRule(ValidationRule.ValidationType.One, "DTSTAMP", "DTSTART", "ORGANIZER", "SUMMARY", "UID"), new ValidationRule(ValidationRule.ValidationType.OneOrLess, "SEQUENCE", "CATEGORIES", "CLASS", "CREATED", "DESCRIPTION", "DTEND", "DURATION", "GEO", "LAST-MODIFIED", "LOCATION", "PRIORITY", "RECURRENCE-ID", "RESOURCES", "STATUS", "TRANSP", "URL")));
        this.alarms = new ComponentList();
        if (initialise) {
            this.getProperties().add(new DtStamp());
        }
    }

    public VEvent(PropertyList properties) {
        super("VEVENT", properties);
        this.methodValidators.put(Method.ADD, new VEventValidator(new ValidationRule(ValidationRule.ValidationType.One, "DTSTAMP", "DTSTART", "ORGANIZER", "SEQUENCE", "SUMMARY", "UID"), new ValidationRule(ValidationRule.ValidationType.OneOrLess, "CATEGORIES", "CLASS", "CREATED", "DESCRIPTION", "DTEND", "DURATION", "GEO", "LAST-MODIFIED", "LOCATION", "PRIORITY", "RESOURCES", "STATUS", "TRANSP", "URL"), new ValidationRule(ValidationRule.ValidationType.None, "RECURRENCE-ID", "REQUEST-STATUS")));
        this.methodValidators.put(Method.CANCEL, new VEventValidator(false, new ValidationRule(ValidationRule.ValidationType.One, "DTSTAMP", "DTSTART", "ORGANIZER", "SEQUENCE", "UID"), new ValidationRule(ValidationRule.ValidationType.OneOrLess, "CATEGORIES", "CLASS", "CREATED", "DESCRIPTION", "DTEND", "DTSTART", "DURATION", "GEO", "LAST-MODIFIED", "LOCATION", "PRIORITY", "RECURRENCE-ID", "RESOURCES", "STATUS", "SUMMARY", "TRANSP", "URL"), new ValidationRule(ValidationRule.ValidationType.None, "REQUEST-STATUS")));
        this.methodValidators.put(Method.COUNTER, new VEventValidator(new ValidationRule(ValidationRule.ValidationType.One, "DTSTAMP", "DTSTART", "SEQUENCE", "SUMMARY", "UID"), new ValidationRule(ValidationRule.ValidationType.One, true, "ORGANIZER"), new ValidationRule(ValidationRule.ValidationType.OneOrLess, "CATEGORIES", "CLASS", "CREATED", "DESCRIPTION", "DTEND", "DURATION", "GEO", "LAST-MODIFIED", "LOCATION", "PRIORITY", "RECURRENCE-ID", "RESOURCES", "STATUS", "TRANSP", "URL")));
        this.methodValidators.put(Method.DECLINE_COUNTER, new VEventValidator(false, new ValidationRule(ValidationRule.ValidationType.One, "DTSTAMP", "ORGANIZER", "UID"), new ValidationRule(ValidationRule.ValidationType.OneOrLess, "RECURRENCE-ID", "SEQUENCE"), new ValidationRule(ValidationRule.ValidationType.None, "ATTACH", "ATTENDEE", "CATEGORIES", "CLASS", "CONTACT", "CREATED", "DESCRIPTION", "DTEND", "DTSTART", "DURATION", "EXDATE", "EXRULE", "GEO", "LAST-MODIFIED", "LOCATION", "PRIORITY", "RDATE", "RELATED-TO", "RESOURCES", "RRULE", "STATUS", "SUMMARY", "TRANSP", "URL")));
        this.methodValidators.put(Method.PUBLISH, new VEventValidator(new ValidationRule(ValidationRule.ValidationType.One, "DTSTART", "UID"), new ValidationRule(ValidationRule.ValidationType.One, true, "DTSTAMP", "ORGANIZER", "SUMMARY"), new ValidationRule(ValidationRule.ValidationType.OneOrLess, "RECURRENCE-ID", "SEQUENCE", "CATEGORIES", "CLASS", "CREATED", "DESCRIPTION", "DTEND", "DURATION", "GEO", "LAST-MODIFIED", "LOCATION", "PRIORITY", "RESOURCES", "STATUS", "TRANSP", "URL"), new ValidationRule(ValidationRule.ValidationType.None, true, "ATTENDEE"), new ValidationRule(ValidationRule.ValidationType.None, "REQUEST-STATUS")));
        this.methodValidators.put(Method.REFRESH, new VEventValidator(false, new ValidationRule(ValidationRule.ValidationType.One, "ATTENDEE", "DTSTAMP", "ORGANIZER", "UID"), new ValidationRule(ValidationRule.ValidationType.OneOrLess, "RECURRENCE-ID"), new ValidationRule(ValidationRule.ValidationType.None, "ATTACH", "CATEGORIES", "CLASS", "CONTACT", "CREATED", "DESCRIPTION", "DTEND", "DTSTART", "DURATION", "EXDATE", "EXRULE", "GEO", "LAST-MODIFIED", "LOCATION", "PRIORITY", "RDATE", "RELATED-TO", "REQUEST-STATUS", "RESOURCES", "RRULE", "SEQUENCE", "STATUS", "SUMMARY", "TRANSP", "URL")));
        this.methodValidators.put(Method.REPLY, new VEventValidator(CompatibilityHints.isHintEnabled("ical4j.validation.relaxed"), new ValidationRule(ValidationRule.ValidationType.One, "ATTENDEE", "DTSTAMP", "ORGANIZER", "UID"), new ValidationRule(ValidationRule.ValidationType.OneOrLess, "RECURRENCE-ID", "SEQUENCE", "CATEGORIES", "CLASS", "CREATED", "DESCRIPTION", "DTEND", "DTSTART", "DURATION", "GEO", "LAST-MODIFIED", "LOCATION", "PRIORITY", "RESOURCES", "STATUS", "SUMMARY", "TRANSP", "URL")));
        this.methodValidators.put(Method.REQUEST, new VEventValidator(new ValidationRule(ValidationRule.ValidationType.OneOrMore, true, "ATTENDEE"), new ValidationRule(ValidationRule.ValidationType.One, "DTSTAMP", "DTSTART", "ORGANIZER", "SUMMARY", "UID"), new ValidationRule(ValidationRule.ValidationType.OneOrLess, "SEQUENCE", "CATEGORIES", "CLASS", "CREATED", "DESCRIPTION", "DTEND", "DURATION", "GEO", "LAST-MODIFIED", "LOCATION", "PRIORITY", "RECURRENCE-ID", "RESOURCES", "STATUS", "TRANSP", "URL")));
        this.alarms = new ComponentList();
    }

    public VEvent(PropertyList properties, ComponentList<VAlarm> alarms) {
        super("VEVENT", properties);
        this.methodValidators.put(Method.ADD, new VEventValidator(new ValidationRule(ValidationRule.ValidationType.One, "DTSTAMP", "DTSTART", "ORGANIZER", "SEQUENCE", "SUMMARY", "UID"), new ValidationRule(ValidationRule.ValidationType.OneOrLess, "CATEGORIES", "CLASS", "CREATED", "DESCRIPTION", "DTEND", "DURATION", "GEO", "LAST-MODIFIED", "LOCATION", "PRIORITY", "RESOURCES", "STATUS", "TRANSP", "URL"), new ValidationRule(ValidationRule.ValidationType.None, "RECURRENCE-ID", "REQUEST-STATUS")));
        this.methodValidators.put(Method.CANCEL, new VEventValidator(false, new ValidationRule(ValidationRule.ValidationType.One, "DTSTAMP", "DTSTART", "ORGANIZER", "SEQUENCE", "UID"), new ValidationRule(ValidationRule.ValidationType.OneOrLess, "CATEGORIES", "CLASS", "CREATED", "DESCRIPTION", "DTEND", "DTSTART", "DURATION", "GEO", "LAST-MODIFIED", "LOCATION", "PRIORITY", "RECURRENCE-ID", "RESOURCES", "STATUS", "SUMMARY", "TRANSP", "URL"), new ValidationRule(ValidationRule.ValidationType.None, "REQUEST-STATUS")));
        this.methodValidators.put(Method.COUNTER, new VEventValidator(new ValidationRule(ValidationRule.ValidationType.One, "DTSTAMP", "DTSTART", "SEQUENCE", "SUMMARY", "UID"), new ValidationRule(ValidationRule.ValidationType.One, true, "ORGANIZER"), new ValidationRule(ValidationRule.ValidationType.OneOrLess, "CATEGORIES", "CLASS", "CREATED", "DESCRIPTION", "DTEND", "DURATION", "GEO", "LAST-MODIFIED", "LOCATION", "PRIORITY", "RECURRENCE-ID", "RESOURCES", "STATUS", "TRANSP", "URL")));
        this.methodValidators.put(Method.DECLINE_COUNTER, new VEventValidator(false, new ValidationRule(ValidationRule.ValidationType.One, "DTSTAMP", "ORGANIZER", "UID"), new ValidationRule(ValidationRule.ValidationType.OneOrLess, "RECURRENCE-ID", "SEQUENCE"), new ValidationRule(ValidationRule.ValidationType.None, "ATTACH", "ATTENDEE", "CATEGORIES", "CLASS", "CONTACT", "CREATED", "DESCRIPTION", "DTEND", "DTSTART", "DURATION", "EXDATE", "EXRULE", "GEO", "LAST-MODIFIED", "LOCATION", "PRIORITY", "RDATE", "RELATED-TO", "RESOURCES", "RRULE", "STATUS", "SUMMARY", "TRANSP", "URL")));
        this.methodValidators.put(Method.PUBLISH, new VEventValidator(new ValidationRule(ValidationRule.ValidationType.One, "DTSTART", "UID"), new ValidationRule(ValidationRule.ValidationType.One, true, "DTSTAMP", "ORGANIZER", "SUMMARY"), new ValidationRule(ValidationRule.ValidationType.OneOrLess, "RECURRENCE-ID", "SEQUENCE", "CATEGORIES", "CLASS", "CREATED", "DESCRIPTION", "DTEND", "DURATION", "GEO", "LAST-MODIFIED", "LOCATION", "PRIORITY", "RESOURCES", "STATUS", "TRANSP", "URL"), new ValidationRule(ValidationRule.ValidationType.None, true, "ATTENDEE"), new ValidationRule(ValidationRule.ValidationType.None, "REQUEST-STATUS")));
        this.methodValidators.put(Method.REFRESH, new VEventValidator(false, new ValidationRule(ValidationRule.ValidationType.One, "ATTENDEE", "DTSTAMP", "ORGANIZER", "UID"), new ValidationRule(ValidationRule.ValidationType.OneOrLess, "RECURRENCE-ID"), new ValidationRule(ValidationRule.ValidationType.None, "ATTACH", "CATEGORIES", "CLASS", "CONTACT", "CREATED", "DESCRIPTION", "DTEND", "DTSTART", "DURATION", "EXDATE", "EXRULE", "GEO", "LAST-MODIFIED", "LOCATION", "PRIORITY", "RDATE", "RELATED-TO", "REQUEST-STATUS", "RESOURCES", "RRULE", "SEQUENCE", "STATUS", "SUMMARY", "TRANSP", "URL")));
        this.methodValidators.put(Method.REPLY, new VEventValidator(CompatibilityHints.isHintEnabled("ical4j.validation.relaxed"), new ValidationRule(ValidationRule.ValidationType.One, "ATTENDEE", "DTSTAMP", "ORGANIZER", "UID"), new ValidationRule(ValidationRule.ValidationType.OneOrLess, "RECURRENCE-ID", "SEQUENCE", "CATEGORIES", "CLASS", "CREATED", "DESCRIPTION", "DTEND", "DTSTART", "DURATION", "GEO", "LAST-MODIFIED", "LOCATION", "PRIORITY", "RESOURCES", "STATUS", "SUMMARY", "TRANSP", "URL")));
        this.methodValidators.put(Method.REQUEST, new VEventValidator(new ValidationRule(ValidationRule.ValidationType.OneOrMore, true, "ATTENDEE"), new ValidationRule(ValidationRule.ValidationType.One, "DTSTAMP", "DTSTART", "ORGANIZER", "SUMMARY", "UID"), new ValidationRule(ValidationRule.ValidationType.OneOrLess, "SEQUENCE", "CATEGORIES", "CLASS", "CREATED", "DESCRIPTION", "DTEND", "DURATION", "GEO", "LAST-MODIFIED", "LOCATION", "PRIORITY", "RECURRENCE-ID", "RESOURCES", "STATUS", "TRANSP", "URL")));
        this.alarms = alarms;
    }

    public VEvent(Date start, String summary) {
        this();
        this.getProperties().add(new DtStart(start));
        this.getProperties().add(new Summary(summary));
    }

    public VEvent(Date start, Date end, String summary) {
        this();
        this.getProperties().add(new DtStart(start));
        this.getProperties().add(new DtEnd(end));
        this.getProperties().add(new Summary(summary));
    }

    public VEvent(Date start, TemporalAmount duration, String summary) {
        this();
        this.getProperties().add(new DtStart(start));
        this.getProperties().add(new Duration(duration));
        this.getProperties().add(new Summary(summary));
    }

    public final ComponentList<VAlarm> getAlarms() {
        return this.alarms;
    }

    @Override
    public final String toString() {
        return "BEGIN:" + this.getName() + "\r\n" + this.getProperties() + this.getAlarms() + "END" + ':' + this.getName() + "\r\n";
    }

    @Override
    public final void validate(boolean recurse) throws ValidationException {
        if (!CompatibilityHints.isHintEnabled("ical4j.validation.relaxed")) {
            PropertyValidator.assertOne("UID", this.getProperties());
            PropertyValidator.assertOne("DTSTAMP", this.getProperties());
        }
        Arrays.asList("CLASS", "CREATED", "DESCRIPTION", "DTSTART", "GEO", "LAST-MODIFIED", "LOCATION", "ORGANIZER", "PRIORITY", "DTSTAMP", "SEQUENCE", "STATUS", "SUMMARY", "TRANSP", "UID", "URL", "RECURRENCE-ID").forEach(property -> PropertyValidator.assertOneOrLess(property, this.getProperties()));
        Status status = (Status)this.getProperty("STATUS");
        if (!(status == null || Status.VEVENT_TENTATIVE.getValue().equals(status.getValue()) || Status.VEVENT_CONFIRMED.getValue().equals(status.getValue()) || Status.VEVENT_CANCELLED.getValue().equals(status.getValue()))) {
            throw new ValidationException("Status property [" + status.toString() + "] is not applicable for VEVENT");
        }
        try {
            PropertyValidator.assertNone("DTEND", this.getProperties());
        }
        catch (ValidationException ve) {
            PropertyValidator.assertNone("DURATION", this.getProperties());
        }
        if (this.getProperty("DTEND") != null) {
            DtStart start = (DtStart)this.getProperty("DTSTART");
            DtEnd end = (DtEnd)this.getProperty("DTEND");
            if (start != null) {
                Object startValue = start.getParameter("VALUE");
                Object endValue = end.getParameter("VALUE");
                boolean startEndValueMismatch = false;
                if (endValue != null) {
                    if (startValue != null && !((Parameter)endValue).equals(startValue)) {
                        startEndValueMismatch = true;
                    } else if (startValue == null && !Value.DATE_TIME.equals(endValue)) {
                        startEndValueMismatch = true;
                    }
                } else if (startValue != null && !Value.DATE_TIME.equals(startValue)) {
                    startEndValueMismatch = true;
                }
                if (startEndValueMismatch) {
                    throw new ValidationException("Property [DTEND] must have the same [VALUE] as [DTSTART]");
                }
            }
        }
        if (recurse) {
            this.validateProperties();
        }
    }

    protected Validator getValidator(Method method) {
        return this.methodValidators.get(method);
    }

    public final PeriodList getConsumedTime(Date rangeStart, Date rangeEnd) {
        return this.getConsumedTime(rangeStart, rangeEnd, true);
    }

    public final PeriodList getConsumedTime(Date rangeStart, Date rangeEnd, boolean normalise) {
        PeriodList periods = new PeriodList();
        if (!Transp.TRANSPARENT.equals(this.getProperty("TRANSP")) && !(periods = this.calculateRecurrenceSet(new Period(new DateTime(rangeStart), new DateTime(rangeEnd)))).isEmpty() && normalise) {
            periods = periods.normalise();
        }
        return periods;
    }

    public final VEvent getOccurrence(Date date) throws IOException, URISyntaxException, ParseException {
        PeriodList consumedTime = this.getConsumedTime(date, date);
        for (Period p : consumedTime) {
            if (!p.getStart().equals(date)) continue;
            VEvent occurrence = (VEvent)this.copy();
            occurrence.getProperties().add(new RecurrenceId(date));
            return occurrence;
        }
        return null;
    }

    public final Clazz getClassification() {
        return (Clazz)this.getProperty("CLASS");
    }

    public final Created getCreated() {
        return (Created)this.getProperty("CREATED");
    }

    public final Description getDescription() {
        return (Description)this.getProperty("DESCRIPTION");
    }

    public final DtStart getStartDate() {
        return (DtStart)this.getProperty("DTSTART");
    }

    public final Geo getGeographicPos() {
        return (Geo)this.getProperty("GEO");
    }

    public final LastModified getLastModified() {
        return (LastModified)this.getProperty("LAST-MODIFIED");
    }

    public final Location getLocation() {
        return (Location)this.getProperty("LOCATION");
    }

    public final Organizer getOrganizer() {
        return (Organizer)this.getProperty("ORGANIZER");
    }

    public final Priority getPriority() {
        return (Priority)this.getProperty("PRIORITY");
    }

    public final DtStamp getDateStamp() {
        return (DtStamp)this.getProperty("DTSTAMP");
    }

    public final Sequence getSequence() {
        return (Sequence)this.getProperty("SEQUENCE");
    }

    public final Status getStatus() {
        return (Status)this.getProperty("STATUS");
    }

    public final Summary getSummary() {
        return (Summary)this.getProperty("SUMMARY");
    }

    public final Transp getTransparency() {
        return (Transp)this.getProperty("TRANSP");
    }

    public final Url getUrl() {
        return (Url)this.getProperty("URL");
    }

    public final RecurrenceId getRecurrenceId() {
        return (RecurrenceId)this.getProperty("RECURRENCE-ID");
    }

    public final DtEnd getEndDate() {
        return this.getEndDate(true);
    }

    public final DtEnd getEndDate(boolean deriveFromDuration) {
        DtEnd dtEnd = (DtEnd)this.getProperty("DTEND");
        if (dtEnd == null && deriveFromDuration && this.getStartDate() != null) {
            DtStart dtStart = this.getStartDate();
            Duration vEventDuration = this.getDuration() != null ? this.getDuration() : (dtStart.getDate() instanceof DateTime ? new Duration(java.time.Duration.ZERO) : new Duration(java.time.Duration.ofDays(1L)));
            dtEnd = new DtEnd(Dates.getInstance(Date.from(dtStart.getDate().toInstant().plus(vEventDuration.getDuration())), (Value)dtStart.getParameter("VALUE")));
            if (dtStart.isUtc()) {
                dtEnd.setUtc(true);
            } else {
                dtEnd.setTimeZone(dtStart.getTimeZone());
            }
        }
        return dtEnd;
    }

    public final Duration getDuration() {
        return (Duration)this.getProperty("DURATION");
    }

    public final Uid getUid() {
        return (Uid)this.getProperty("UID");
    }

    @Override
    public boolean equals(Object arg0) {
        if (arg0 instanceof VEvent) {
            return super.equals(arg0) && Objects.equals(this.alarms, ((VEvent)arg0).getAlarms());
        }
        return super.equals(arg0);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append((Object)this.getName()).append(this.getProperties()).append(this.getAlarms()).toHashCode();
    }

    @Override
    public Component copy() throws ParseException, IOException, URISyntaxException {
        VEvent copy = (VEvent)super.copy();
        copy.alarms = new ComponentList<VAlarm>(this.alarms);
        return copy;
    }

    public static class Factory
    extends Content.Factory
    implements ComponentFactory<VEvent> {
        public Factory() {
            super("VEVENT");
        }

        @Override
        public VEvent createComponent() {
            return new VEvent(false);
        }

        @Override
        public VEvent createComponent(PropertyList properties) {
            return new VEvent(properties);
        }

        @Override
        public VEvent createComponent(PropertyList properties, ComponentList subComponents) {
            return new VEvent(properties, subComponents);
        }
    }
}

