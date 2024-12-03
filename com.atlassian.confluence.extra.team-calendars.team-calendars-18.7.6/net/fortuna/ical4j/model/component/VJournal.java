/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.model.component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import net.fortuna.ical4j.model.ComponentFactory;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.Content;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.property.Clazz;
import net.fortuna.ical4j.model.property.Created;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.DtStamp;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.LastModified;
import net.fortuna.ical4j.model.property.Method;
import net.fortuna.ical4j.model.property.Organizer;
import net.fortuna.ical4j.model.property.RecurrenceId;
import net.fortuna.ical4j.model.property.Sequence;
import net.fortuna.ical4j.model.property.Status;
import net.fortuna.ical4j.model.property.Summary;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.model.property.Url;
import net.fortuna.ical4j.util.CompatibilityHints;
import net.fortuna.ical4j.validate.ComponentValidator;
import net.fortuna.ical4j.validate.PropertyValidator;
import net.fortuna.ical4j.validate.ValidationException;
import net.fortuna.ical4j.validate.ValidationRule;
import net.fortuna.ical4j.validate.Validator;

public class VJournal
extends CalendarComponent {
    private static final long serialVersionUID = -7635140949183238830L;
    private final Map<Method, Validator> methodValidators = new HashMap<Method, Validator>();

    public VJournal() {
        this(true);
    }

    public VJournal(boolean initialise) {
        super("VJOURNAL");
        this.methodValidators.put(Method.ADD, new ComponentValidator(new ValidationRule(ValidationRule.ValidationType.One, "DESCRIPTION", "DTSTAMP", "DTSTART", "ORGANIZER", "SEQUENCE", "UID"), new ValidationRule(ValidationRule.ValidationType.OneOrLess, "CATEGORIES", "CLASS", "CREATED", "LAST-MODIFIED", "STATUS", "SUMMARY", "URL"), new ValidationRule(ValidationRule.ValidationType.None, "ATTENDEE", "RECURRENCE-ID")));
        this.methodValidators.put(Method.CANCEL, new ComponentValidator(new ValidationRule(ValidationRule.ValidationType.One, "DTSTAMP", "ORGANIZER", "SEQUENCE", "UID"), new ValidationRule(ValidationRule.ValidationType.OneOrLess, "CATEGORIES", "CLASS", "CREATED", "DESCRIPTION", "DTSTART", "LAST-MODIFIED", "RECURRENCE-ID", "STATUS", "SUMMARY", "URL"), new ValidationRule(ValidationRule.ValidationType.None, "REQUEST-STATUS")));
        this.methodValidators.put(Method.PUBLISH, new ComponentValidator(new ValidationRule(ValidationRule.ValidationType.One, "DESCRIPTION", "DTSTAMP", "DTSTART", "ORGANIZER", "UID"), new ValidationRule(ValidationRule.ValidationType.OneOrLess, "CATEGORIES", "CLASS", "CREATED", "LAST-MODIFIED", "RECURRENCE-ID", "SEQUENCE", "STATUS", "SUMMARY", "URL"), new ValidationRule(ValidationRule.ValidationType.None, "ATTENDEE")));
        if (initialise) {
            this.getProperties().add(new DtStamp());
        }
    }

    public VJournal(PropertyList properties) {
        super("VJOURNAL", properties);
        this.methodValidators.put(Method.ADD, new ComponentValidator(new ValidationRule(ValidationRule.ValidationType.One, "DESCRIPTION", "DTSTAMP", "DTSTART", "ORGANIZER", "SEQUENCE", "UID"), new ValidationRule(ValidationRule.ValidationType.OneOrLess, "CATEGORIES", "CLASS", "CREATED", "LAST-MODIFIED", "STATUS", "SUMMARY", "URL"), new ValidationRule(ValidationRule.ValidationType.None, "ATTENDEE", "RECURRENCE-ID")));
        this.methodValidators.put(Method.CANCEL, new ComponentValidator(new ValidationRule(ValidationRule.ValidationType.One, "DTSTAMP", "ORGANIZER", "SEQUENCE", "UID"), new ValidationRule(ValidationRule.ValidationType.OneOrLess, "CATEGORIES", "CLASS", "CREATED", "DESCRIPTION", "DTSTART", "LAST-MODIFIED", "RECURRENCE-ID", "STATUS", "SUMMARY", "URL"), new ValidationRule(ValidationRule.ValidationType.None, "REQUEST-STATUS")));
        this.methodValidators.put(Method.PUBLISH, new ComponentValidator(new ValidationRule(ValidationRule.ValidationType.One, "DESCRIPTION", "DTSTAMP", "DTSTART", "ORGANIZER", "UID"), new ValidationRule(ValidationRule.ValidationType.OneOrLess, "CATEGORIES", "CLASS", "CREATED", "LAST-MODIFIED", "RECURRENCE-ID", "SEQUENCE", "STATUS", "SUMMARY", "URL"), new ValidationRule(ValidationRule.ValidationType.None, "ATTENDEE")));
    }

    public VJournal(Date start, String summary) {
        this();
        this.getProperties().add(new DtStart(start));
        this.getProperties().add(new Summary(summary));
    }

    @Override
    public final void validate(boolean recurse) throws ValidationException {
        if (!CompatibilityHints.isHintEnabled("ical4j.validation.relaxed")) {
            PropertyValidator.assertOne("UID", this.getProperties());
            PropertyValidator.assertOne("DTSTAMP", this.getProperties());
        }
        Arrays.asList("CLASS", "CREATED", "DESCRIPTION", "DTSTART", "DTSTAMP", "LAST-MODIFIED", "ORGANIZER", "RECURRENCE-ID", "SEQUENCE", "STATUS", "SUMMARY", "UID", "URL").forEach(property -> PropertyValidator.assertOneOrLess(property, this.getProperties()));
        Status status = (Status)this.getProperty("STATUS");
        if (!(status == null || Status.VJOURNAL_DRAFT.getValue().equals(status.getValue()) || Status.VJOURNAL_FINAL.getValue().equals(status.getValue()) || Status.VJOURNAL_CANCELLED.getValue().equals(status.getValue()))) {
            throw new ValidationException("Status property [" + status.toString() + "] may not occur in VJOURNAL");
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

    public final Created getCreated() {
        return (Created)this.getProperty("CREATED");
    }

    public final Description getDescription() {
        return (Description)this.getProperty("DESCRIPTION");
    }

    public final DtStart getStartDate() {
        return (DtStart)this.getProperty("DTSTART");
    }

    public final LastModified getLastModified() {
        return (LastModified)this.getProperty("LAST-MODIFIED");
    }

    public final Organizer getOrganizer() {
        return (Organizer)this.getProperty("ORGANIZER");
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

    public final Uid getUid() {
        return (Uid)this.getProperty("UID");
    }

    public static class Factory
    extends Content.Factory
    implements ComponentFactory<VJournal> {
        public Factory() {
            super("VJOURNAL");
        }

        @Override
        public VJournal createComponent() {
            return new VJournal(false);
        }

        @Override
        public VJournal createComponent(PropertyList properties) {
            return new VJournal(properties);
        }

        @Override
        public VJournal createComponent(PropertyList properties, ComponentList subComponents) {
            throw new UnsupportedOperationException(String.format("%s does not support sub-components", "VJOURNAL"));
        }
    }
}

