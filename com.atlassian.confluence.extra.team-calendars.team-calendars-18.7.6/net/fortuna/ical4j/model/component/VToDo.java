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
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.component.VAlarm;
import net.fortuna.ical4j.model.property.Clazz;
import net.fortuna.ical4j.model.property.Completed;
import net.fortuna.ical4j.model.property.Created;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.DtStamp;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.Due;
import net.fortuna.ical4j.model.property.Duration;
import net.fortuna.ical4j.model.property.Geo;
import net.fortuna.ical4j.model.property.LastModified;
import net.fortuna.ical4j.model.property.Location;
import net.fortuna.ical4j.model.property.Method;
import net.fortuna.ical4j.model.property.Organizer;
import net.fortuna.ical4j.model.property.PercentComplete;
import net.fortuna.ical4j.model.property.Priority;
import net.fortuna.ical4j.model.property.RecurrenceId;
import net.fortuna.ical4j.model.property.Sequence;
import net.fortuna.ical4j.model.property.Status;
import net.fortuna.ical4j.model.property.Summary;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.model.property.Url;
import net.fortuna.ical4j.util.CompatibilityHints;
import net.fortuna.ical4j.validate.PropertyValidator;
import net.fortuna.ical4j.validate.ValidationException;
import net.fortuna.ical4j.validate.ValidationRule;
import net.fortuna.ical4j.validate.Validator;
import net.fortuna.ical4j.validate.component.VToDoValidator;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class VToDo
extends CalendarComponent {
    private static final long serialVersionUID = -269658210065896668L;
    private final Map<Method, Validator> methodValidators = new HashMap<Method, Validator>();
    private ComponentList<VAlarm> alarms;

    public VToDo() {
        this(true);
    }

    public VToDo(boolean initialise) {
        super("VTODO");
        this.methodValidators.put(Method.ADD, new VToDoValidator(new ValidationRule(ValidationRule.ValidationType.One, "DTSTAMP", "ORGANIZER", "PRIORITY", "SEQUENCE", "SUMMARY", "UID"), new ValidationRule(ValidationRule.ValidationType.OneOrLess, "CATEGORIES", "CLASS", "CREATED", "DESCRIPTION", "DTSTART", "DUE", "DURATION", "GEO", "LAST-MODIFIED", "LOCATION", "PERCENT-COMPLETE", "RESOURCES", "STATUS", "URL"), new ValidationRule(ValidationRule.ValidationType.None, "RECURRENCE-ID", "REQUEST-STATUS")));
        this.methodValidators.put(Method.CANCEL, new VToDoValidator(false, new ValidationRule(ValidationRule.ValidationType.One, "UID", "DTSTAMP", "ORGANIZER", "SEQUENCE"), new ValidationRule(ValidationRule.ValidationType.OneOrLess, "CATEGORIES", "CLASS", "CREATED", "DESCRIPTION", "DTSTART", "DUE", "DURATION", "GEO", "LAST-MODIFIED", "LOCATION", "PERCENT-COMPLETE", "RECURRENCE-ID", "RESOURCES", "PRIORITY", "STATUS", "URL"), new ValidationRule(ValidationRule.ValidationType.None, "REQUEST-STATUS")));
        this.methodValidators.put(Method.COUNTER, new VToDoValidator(new ValidationRule(ValidationRule.ValidationType.OneOrMore, "ATTENDEE"), new ValidationRule(ValidationRule.ValidationType.One, "DTSTAMP", "ORGANIZER", "PRIORITY", "SUMMARY", "UID"), new ValidationRule(ValidationRule.ValidationType.OneOrLess, "CATEGORIES", "CLASS", "CREATED", "DESCRIPTION", "DTSTART", "DUE", "DURATION", "GEO", "LAST-MODIFIED", "LOCATION", "PERCENT-COMPLETE", "RECURRENCE-ID", "RESOURCES", "RRULE", "SEQUENCE", "STATUS", "URL")));
        this.methodValidators.put(Method.DECLINE_COUNTER, new VToDoValidator(false, new ValidationRule(ValidationRule.ValidationType.OneOrMore, "ATTENDEE"), new ValidationRule(ValidationRule.ValidationType.One, "DTSTAMP", "ORGANIZER", "SEQUENCE", "UID"), new ValidationRule(ValidationRule.ValidationType.OneOrLess, "CATEGORIES", "CLASS", "CREATED", "DESCRIPTION", "DTSTART", "DUE", "DURATION", "GEO", "LAST-MODIFIED", "LOCATION", "LOCATION", "PERCENT-COMPLETE", "PRIORITY", "RECURRENCE-ID", "RESOURCES", "STATUS", "URL")));
        this.methodValidators.put(Method.PUBLISH, new VToDoValidator(new ValidationRule(ValidationRule.ValidationType.One, "DTSTAMP", "SUMMARY", "UID"), new ValidationRule(ValidationRule.ValidationType.One, true, "ORGANIZER", "PRIORITY"), new ValidationRule(ValidationRule.ValidationType.OneOrLess, "DTSTART", "SEQUENCE", "CATEGORIES", "CLASS", "CREATED", "DESCRIPTION", "DUE", "DURATION", "GEO", "LAST-MODIFIED", "LOCATION", "PERCENT-COMPLETE", "RECURRENCE-ID", "RESOURCES", "STATUS", "URL"), new ValidationRule(ValidationRule.ValidationType.None, "ATTENDEE", "REQUEST-STATUS")));
        this.methodValidators.put(Method.REFRESH, new VToDoValidator(false, new ValidationRule(ValidationRule.ValidationType.One, "ATTENDEE", "DTSTAMP", "UID"), new ValidationRule(ValidationRule.ValidationType.OneOrLess, "RECURRENCE-ID"), new ValidationRule(ValidationRule.ValidationType.None, "ATTACH", "CATEGORIES", "CLASS", "CONTACT", "CREATED", "DESCRIPTION", "DTSTART", "DUE", "DURATION", "EXDATE", "EXRULE", "GEO", "LAST-MODIFIED", "LOCATION", "ORGANIZER", "PERCENT-COMPLETE", "PRIORITY", "RDATE", "RELATED-TO", "REQUEST-STATUS", "RESOURCES", "RRULE", "SEQUENCE", "STATUS", "URL")));
        this.methodValidators.put(Method.REPLY, new VToDoValidator(false, new ValidationRule(ValidationRule.ValidationType.OneOrMore, "ATTENDEE"), new ValidationRule(ValidationRule.ValidationType.One, "DTSTAMP", "ORGANIZER", "UID"), new ValidationRule(ValidationRule.ValidationType.OneOrLess, "CATEGORIES", "CLASS", "CREATED", "DESCRIPTION", "DTSTART", "DUE", "DURATION", "GEO", "LAST-MODIFIED", "LOCATION", "PERCENT-COMPLETE", "PRIORITY", "RESOURCES", "RECURRENCE-ID", "SEQUENCE", "STATUS", "SUMMARY", "URL")));
        this.methodValidators.put(Method.REQUEST, new VToDoValidator(new ValidationRule(ValidationRule.ValidationType.OneOrMore, "ATTENDEE"), new ValidationRule(ValidationRule.ValidationType.One, "DTSTAMP", "DTSTART", "ORGANIZER", "PRIORITY", "SUMMARY", "UID"), new ValidationRule(ValidationRule.ValidationType.OneOrLess, "SEQUENCE", "CATEGORIES", "CLASS", "CREATED", "DESCRIPTION", "DUE", "DURATION", "GEO", "LAST-MODIFIED", "LOCATION", "PERCENT-COMPLETE", "RECURRENCE-ID", "RESOURCES", "STATUS", "URL"), new ValidationRule(ValidationRule.ValidationType.None, "REQUEST-STATUS")));
        this.alarms = new ComponentList();
        if (initialise) {
            this.getProperties().add(new DtStamp());
        }
    }

    public VToDo(PropertyList properties) {
        super("VTODO", properties);
        this.methodValidators.put(Method.ADD, new VToDoValidator(new ValidationRule(ValidationRule.ValidationType.One, "DTSTAMP", "ORGANIZER", "PRIORITY", "SEQUENCE", "SUMMARY", "UID"), new ValidationRule(ValidationRule.ValidationType.OneOrLess, "CATEGORIES", "CLASS", "CREATED", "DESCRIPTION", "DTSTART", "DUE", "DURATION", "GEO", "LAST-MODIFIED", "LOCATION", "PERCENT-COMPLETE", "RESOURCES", "STATUS", "URL"), new ValidationRule(ValidationRule.ValidationType.None, "RECURRENCE-ID", "REQUEST-STATUS")));
        this.methodValidators.put(Method.CANCEL, new VToDoValidator(false, new ValidationRule(ValidationRule.ValidationType.One, "UID", "DTSTAMP", "ORGANIZER", "SEQUENCE"), new ValidationRule(ValidationRule.ValidationType.OneOrLess, "CATEGORIES", "CLASS", "CREATED", "DESCRIPTION", "DTSTART", "DUE", "DURATION", "GEO", "LAST-MODIFIED", "LOCATION", "PERCENT-COMPLETE", "RECURRENCE-ID", "RESOURCES", "PRIORITY", "STATUS", "URL"), new ValidationRule(ValidationRule.ValidationType.None, "REQUEST-STATUS")));
        this.methodValidators.put(Method.COUNTER, new VToDoValidator(new ValidationRule(ValidationRule.ValidationType.OneOrMore, "ATTENDEE"), new ValidationRule(ValidationRule.ValidationType.One, "DTSTAMP", "ORGANIZER", "PRIORITY", "SUMMARY", "UID"), new ValidationRule(ValidationRule.ValidationType.OneOrLess, "CATEGORIES", "CLASS", "CREATED", "DESCRIPTION", "DTSTART", "DUE", "DURATION", "GEO", "LAST-MODIFIED", "LOCATION", "PERCENT-COMPLETE", "RECURRENCE-ID", "RESOURCES", "RRULE", "SEQUENCE", "STATUS", "URL")));
        this.methodValidators.put(Method.DECLINE_COUNTER, new VToDoValidator(false, new ValidationRule(ValidationRule.ValidationType.OneOrMore, "ATTENDEE"), new ValidationRule(ValidationRule.ValidationType.One, "DTSTAMP", "ORGANIZER", "SEQUENCE", "UID"), new ValidationRule(ValidationRule.ValidationType.OneOrLess, "CATEGORIES", "CLASS", "CREATED", "DESCRIPTION", "DTSTART", "DUE", "DURATION", "GEO", "LAST-MODIFIED", "LOCATION", "LOCATION", "PERCENT-COMPLETE", "PRIORITY", "RECURRENCE-ID", "RESOURCES", "STATUS", "URL")));
        this.methodValidators.put(Method.PUBLISH, new VToDoValidator(new ValidationRule(ValidationRule.ValidationType.One, "DTSTAMP", "SUMMARY", "UID"), new ValidationRule(ValidationRule.ValidationType.One, true, "ORGANIZER", "PRIORITY"), new ValidationRule(ValidationRule.ValidationType.OneOrLess, "DTSTART", "SEQUENCE", "CATEGORIES", "CLASS", "CREATED", "DESCRIPTION", "DUE", "DURATION", "GEO", "LAST-MODIFIED", "LOCATION", "PERCENT-COMPLETE", "RECURRENCE-ID", "RESOURCES", "STATUS", "URL"), new ValidationRule(ValidationRule.ValidationType.None, "ATTENDEE", "REQUEST-STATUS")));
        this.methodValidators.put(Method.REFRESH, new VToDoValidator(false, new ValidationRule(ValidationRule.ValidationType.One, "ATTENDEE", "DTSTAMP", "UID"), new ValidationRule(ValidationRule.ValidationType.OneOrLess, "RECURRENCE-ID"), new ValidationRule(ValidationRule.ValidationType.None, "ATTACH", "CATEGORIES", "CLASS", "CONTACT", "CREATED", "DESCRIPTION", "DTSTART", "DUE", "DURATION", "EXDATE", "EXRULE", "GEO", "LAST-MODIFIED", "LOCATION", "ORGANIZER", "PERCENT-COMPLETE", "PRIORITY", "RDATE", "RELATED-TO", "REQUEST-STATUS", "RESOURCES", "RRULE", "SEQUENCE", "STATUS", "URL")));
        this.methodValidators.put(Method.REPLY, new VToDoValidator(false, new ValidationRule(ValidationRule.ValidationType.OneOrMore, "ATTENDEE"), new ValidationRule(ValidationRule.ValidationType.One, "DTSTAMP", "ORGANIZER", "UID"), new ValidationRule(ValidationRule.ValidationType.OneOrLess, "CATEGORIES", "CLASS", "CREATED", "DESCRIPTION", "DTSTART", "DUE", "DURATION", "GEO", "LAST-MODIFIED", "LOCATION", "PERCENT-COMPLETE", "PRIORITY", "RESOURCES", "RECURRENCE-ID", "SEQUENCE", "STATUS", "SUMMARY", "URL")));
        this.methodValidators.put(Method.REQUEST, new VToDoValidator(new ValidationRule(ValidationRule.ValidationType.OneOrMore, "ATTENDEE"), new ValidationRule(ValidationRule.ValidationType.One, "DTSTAMP", "DTSTART", "ORGANIZER", "PRIORITY", "SUMMARY", "UID"), new ValidationRule(ValidationRule.ValidationType.OneOrLess, "SEQUENCE", "CATEGORIES", "CLASS", "CREATED", "DESCRIPTION", "DUE", "DURATION", "GEO", "LAST-MODIFIED", "LOCATION", "PERCENT-COMPLETE", "RECURRENCE-ID", "RESOURCES", "STATUS", "URL"), new ValidationRule(ValidationRule.ValidationType.None, "REQUEST-STATUS")));
        this.alarms = new ComponentList();
    }

    public VToDo(PropertyList properties, ComponentList<VAlarm> alarms) {
        super("VTODO", properties);
        this.methodValidators.put(Method.ADD, new VToDoValidator(new ValidationRule(ValidationRule.ValidationType.One, "DTSTAMP", "ORGANIZER", "PRIORITY", "SEQUENCE", "SUMMARY", "UID"), new ValidationRule(ValidationRule.ValidationType.OneOrLess, "CATEGORIES", "CLASS", "CREATED", "DESCRIPTION", "DTSTART", "DUE", "DURATION", "GEO", "LAST-MODIFIED", "LOCATION", "PERCENT-COMPLETE", "RESOURCES", "STATUS", "URL"), new ValidationRule(ValidationRule.ValidationType.None, "RECURRENCE-ID", "REQUEST-STATUS")));
        this.methodValidators.put(Method.CANCEL, new VToDoValidator(false, new ValidationRule(ValidationRule.ValidationType.One, "UID", "DTSTAMP", "ORGANIZER", "SEQUENCE"), new ValidationRule(ValidationRule.ValidationType.OneOrLess, "CATEGORIES", "CLASS", "CREATED", "DESCRIPTION", "DTSTART", "DUE", "DURATION", "GEO", "LAST-MODIFIED", "LOCATION", "PERCENT-COMPLETE", "RECURRENCE-ID", "RESOURCES", "PRIORITY", "STATUS", "URL"), new ValidationRule(ValidationRule.ValidationType.None, "REQUEST-STATUS")));
        this.methodValidators.put(Method.COUNTER, new VToDoValidator(new ValidationRule(ValidationRule.ValidationType.OneOrMore, "ATTENDEE"), new ValidationRule(ValidationRule.ValidationType.One, "DTSTAMP", "ORGANIZER", "PRIORITY", "SUMMARY", "UID"), new ValidationRule(ValidationRule.ValidationType.OneOrLess, "CATEGORIES", "CLASS", "CREATED", "DESCRIPTION", "DTSTART", "DUE", "DURATION", "GEO", "LAST-MODIFIED", "LOCATION", "PERCENT-COMPLETE", "RECURRENCE-ID", "RESOURCES", "RRULE", "SEQUENCE", "STATUS", "URL")));
        this.methodValidators.put(Method.DECLINE_COUNTER, new VToDoValidator(false, new ValidationRule(ValidationRule.ValidationType.OneOrMore, "ATTENDEE"), new ValidationRule(ValidationRule.ValidationType.One, "DTSTAMP", "ORGANIZER", "SEQUENCE", "UID"), new ValidationRule(ValidationRule.ValidationType.OneOrLess, "CATEGORIES", "CLASS", "CREATED", "DESCRIPTION", "DTSTART", "DUE", "DURATION", "GEO", "LAST-MODIFIED", "LOCATION", "LOCATION", "PERCENT-COMPLETE", "PRIORITY", "RECURRENCE-ID", "RESOURCES", "STATUS", "URL")));
        this.methodValidators.put(Method.PUBLISH, new VToDoValidator(new ValidationRule(ValidationRule.ValidationType.One, "DTSTAMP", "SUMMARY", "UID"), new ValidationRule(ValidationRule.ValidationType.One, true, "ORGANIZER", "PRIORITY"), new ValidationRule(ValidationRule.ValidationType.OneOrLess, "DTSTART", "SEQUENCE", "CATEGORIES", "CLASS", "CREATED", "DESCRIPTION", "DUE", "DURATION", "GEO", "LAST-MODIFIED", "LOCATION", "PERCENT-COMPLETE", "RECURRENCE-ID", "RESOURCES", "STATUS", "URL"), new ValidationRule(ValidationRule.ValidationType.None, "ATTENDEE", "REQUEST-STATUS")));
        this.methodValidators.put(Method.REFRESH, new VToDoValidator(false, new ValidationRule(ValidationRule.ValidationType.One, "ATTENDEE", "DTSTAMP", "UID"), new ValidationRule(ValidationRule.ValidationType.OneOrLess, "RECURRENCE-ID"), new ValidationRule(ValidationRule.ValidationType.None, "ATTACH", "CATEGORIES", "CLASS", "CONTACT", "CREATED", "DESCRIPTION", "DTSTART", "DUE", "DURATION", "EXDATE", "EXRULE", "GEO", "LAST-MODIFIED", "LOCATION", "ORGANIZER", "PERCENT-COMPLETE", "PRIORITY", "RDATE", "RELATED-TO", "REQUEST-STATUS", "RESOURCES", "RRULE", "SEQUENCE", "STATUS", "URL")));
        this.methodValidators.put(Method.REPLY, new VToDoValidator(false, new ValidationRule(ValidationRule.ValidationType.OneOrMore, "ATTENDEE"), new ValidationRule(ValidationRule.ValidationType.One, "DTSTAMP", "ORGANIZER", "UID"), new ValidationRule(ValidationRule.ValidationType.OneOrLess, "CATEGORIES", "CLASS", "CREATED", "DESCRIPTION", "DTSTART", "DUE", "DURATION", "GEO", "LAST-MODIFIED", "LOCATION", "PERCENT-COMPLETE", "PRIORITY", "RESOURCES", "RECURRENCE-ID", "SEQUENCE", "STATUS", "SUMMARY", "URL")));
        this.methodValidators.put(Method.REQUEST, new VToDoValidator(new ValidationRule(ValidationRule.ValidationType.OneOrMore, "ATTENDEE"), new ValidationRule(ValidationRule.ValidationType.One, "DTSTAMP", "DTSTART", "ORGANIZER", "PRIORITY", "SUMMARY", "UID"), new ValidationRule(ValidationRule.ValidationType.OneOrLess, "SEQUENCE", "CATEGORIES", "CLASS", "CREATED", "DESCRIPTION", "DUE", "DURATION", "GEO", "LAST-MODIFIED", "LOCATION", "PERCENT-COMPLETE", "RECURRENCE-ID", "RESOURCES", "STATUS", "URL"), new ValidationRule(ValidationRule.ValidationType.None, "REQUEST-STATUS")));
        this.alarms = new ComponentList();
        this.alarms = alarms;
    }

    public VToDo(Date start, String summary) {
        this();
        this.getProperties().add(new DtStart(start));
        this.getProperties().add(new Summary(summary));
    }

    public VToDo(Date start, Date due, String summary) {
        this();
        this.getProperties().add(new DtStart(start));
        this.getProperties().add(new Due(due));
        this.getProperties().add(new Summary(summary));
    }

    public VToDo(Date start, TemporalAmount duration, String summary) {
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
        for (VAlarm component : this.getAlarms()) {
            component.validate(recurse);
        }
        if (!CompatibilityHints.isHintEnabled("ical4j.validation.relaxed")) {
            PropertyValidator.assertOne("UID", this.getProperties());
            PropertyValidator.assertOne("DTSTAMP", this.getProperties());
        }
        Arrays.asList("CLASS", "COMPLETED", "CREATED", "DESCRIPTION", "DTSTAMP", "DTSTART", "GEO", "LAST-MODIFIED", "LOCATION", "ORGANIZER", "PERCENT-COMPLETE", "PRIORITY", "RECURRENCE-ID", "SEQUENCE", "STATUS", "SUMMARY", "UID", "URL").forEach(property -> PropertyValidator.assertOneOrLess(property, this.getProperties()));
        Status status = (Status)this.getProperty("STATUS");
        if (!(status == null || Status.VTODO_NEEDS_ACTION.getValue().equals(status.getValue()) || Status.VTODO_COMPLETED.getValue().equals(status.getValue()) || Status.VTODO_IN_PROCESS.getValue().equals(status.getValue()) || Status.VTODO_CANCELLED.getValue().equals(status.getValue()))) {
            throw new ValidationException("Status property [" + status.toString() + "] may not occur in VTODO");
        }
        try {
            PropertyValidator.assertNone("DUE", this.getProperties());
        }
        catch (ValidationException ve) {
            PropertyValidator.assertNone("DURATION", this.getProperties());
        }
        if (recurse) {
            this.validateProperties();
        }
    }

    protected Validator getValidator(Method method) {
        return this.methodValidators.get(method);
    }

    public final Clazz getClassification() {
        return (Clazz)this.getProperty("CLASS");
    }

    public final Completed getDateCompleted() {
        return (Completed)this.getProperty("COMPLETED");
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

    public final PercentComplete getPercentComplete() {
        return (PercentComplete)this.getProperty("PERCENT-COMPLETE");
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

    public final Url getUrl() {
        return (Url)this.getProperty("URL");
    }

    public final RecurrenceId getRecurrenceId() {
        return (RecurrenceId)this.getProperty("RECURRENCE-ID");
    }

    public final Duration getDuration() {
        return (Duration)this.getProperty("DURATION");
    }

    public final Due getDue() {
        return (Due)this.getProperty("DUE");
    }

    public final Uid getUid() {
        return (Uid)this.getProperty("UID");
    }

    @Override
    public boolean equals(Object arg0) {
        if (arg0 instanceof VToDo) {
            return super.equals(arg0) && Objects.equals(this.alarms, ((VToDo)arg0).getAlarms());
        }
        return super.equals(arg0);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append((Object)this.getName()).append(this.getProperties()).append(this.getAlarms()).toHashCode();
    }

    @Override
    public Component copy() throws ParseException, IOException, URISyntaxException {
        VToDo copy = (VToDo)super.copy();
        copy.alarms = new ComponentList<VAlarm>(this.alarms);
        return copy;
    }

    public static class Factory
    extends Content.Factory
    implements ComponentFactory<VToDo> {
        public Factory() {
            super("VTODO");
        }

        @Override
        public VToDo createComponent() {
            return new VToDo(false);
        }

        @Override
        public VToDo createComponent(PropertyList properties) {
            return new VToDo(properties);
        }

        @Override
        public VToDo createComponent(PropertyList properties, ComponentList subComponents) {
            return new VToDo(properties, subComponents);
        }
    }
}

