/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.model.component;

import java.time.temporal.TemporalAmount;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.ComponentFactory;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.Content;
import net.fortuna.ical4j.model.DateRange;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Period;
import net.fortuna.ical4j.model.PeriodList;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.TemporalAmountComparator;
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.parameter.FbType;
import net.fortuna.ical4j.model.property.Contact;
import net.fortuna.ical4j.model.property.DtEnd;
import net.fortuna.ical4j.model.property.DtStamp;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.Duration;
import net.fortuna.ical4j.model.property.FreeBusy;
import net.fortuna.ical4j.model.property.Method;
import net.fortuna.ical4j.model.property.Organizer;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.model.property.Url;
import net.fortuna.ical4j.util.CompatibilityHints;
import net.fortuna.ical4j.validate.ComponentValidator;
import net.fortuna.ical4j.validate.PropertyValidator;
import net.fortuna.ical4j.validate.ValidationException;
import net.fortuna.ical4j.validate.ValidationRule;
import net.fortuna.ical4j.validate.Validator;

public class VFreeBusy
extends CalendarComponent {
    private static final long serialVersionUID = 1046534053331139832L;
    private final Map<Method, Validator> methodValidators = new HashMap<Method, Validator>();

    public VFreeBusy() {
        this(true);
    }

    public VFreeBusy(boolean initialise) {
        super("VFREEBUSY");
        this.methodValidators.put(Method.PUBLISH, new ComponentValidator(new ValidationRule(ValidationRule.ValidationType.OneOrMore, "FREEBUSY"), new ValidationRule(ValidationRule.ValidationType.One, "DTSTAMP", "DTSTART", "DTEND", "ORGANIZER", "UID"), new ValidationRule(ValidationRule.ValidationType.OneOrLess, "URL"), new ValidationRule(ValidationRule.ValidationType.None, "ATTENDEE", "DURATION", "REQUEST-STATUS")));
        this.methodValidators.put(Method.REPLY, new ComponentValidator(new ValidationRule(ValidationRule.ValidationType.One, "ATTENDEE", "DTSTAMP", "DTEND", "DTSTART", "ORGANIZER", "UID"), new ValidationRule(ValidationRule.ValidationType.OneOrLess, "URL"), new ValidationRule(ValidationRule.ValidationType.None, "DURATION", "SEQUENCE")));
        this.methodValidators.put(Method.REQUEST, new ComponentValidator(new ValidationRule(ValidationRule.ValidationType.OneOrMore, "ATTENDEE"), new ValidationRule(ValidationRule.ValidationType.One, "DTEND", "DTSTAMP", "DTSTART", "ORGANIZER", "UID"), new ValidationRule(ValidationRule.ValidationType.None, "FREEBUSY", "DURATION", "REQUEST-STATUS", "URL")));
        if (initialise) {
            this.getProperties().add(new DtStamp());
        }
    }

    public VFreeBusy(PropertyList properties) {
        super("VFREEBUSY", properties);
        this.methodValidators.put(Method.PUBLISH, new ComponentValidator(new ValidationRule(ValidationRule.ValidationType.OneOrMore, "FREEBUSY"), new ValidationRule(ValidationRule.ValidationType.One, "DTSTAMP", "DTSTART", "DTEND", "ORGANIZER", "UID"), new ValidationRule(ValidationRule.ValidationType.OneOrLess, "URL"), new ValidationRule(ValidationRule.ValidationType.None, "ATTENDEE", "DURATION", "REQUEST-STATUS")));
        this.methodValidators.put(Method.REPLY, new ComponentValidator(new ValidationRule(ValidationRule.ValidationType.One, "ATTENDEE", "DTSTAMP", "DTEND", "DTSTART", "ORGANIZER", "UID"), new ValidationRule(ValidationRule.ValidationType.OneOrLess, "URL"), new ValidationRule(ValidationRule.ValidationType.None, "DURATION", "SEQUENCE")));
        this.methodValidators.put(Method.REQUEST, new ComponentValidator(new ValidationRule(ValidationRule.ValidationType.OneOrMore, "ATTENDEE"), new ValidationRule(ValidationRule.ValidationType.One, "DTEND", "DTSTAMP", "DTSTART", "ORGANIZER", "UID"), new ValidationRule(ValidationRule.ValidationType.None, "FREEBUSY", "DURATION", "REQUEST-STATUS", "URL")));
    }

    public VFreeBusy(DateTime start, DateTime end) {
        this();
        this.getProperties().add(new DtStart(start, true));
        this.getProperties().add(new DtEnd(end, true));
    }

    public VFreeBusy(DateTime start, DateTime end, TemporalAmount duration) {
        this();
        this.getProperties().add(new DtStart(start, true));
        this.getProperties().add(new DtEnd(end, true));
        this.getProperties().add(new Duration(duration));
    }

    public VFreeBusy(VFreeBusy request, ComponentList<CalendarComponent> components) {
        this();
        DtStart start = (DtStart)request.getProperty("DTSTART");
        DtEnd end = (DtEnd)request.getProperty("DTEND");
        Duration duration = (Duration)request.getProperty("DURATION");
        this.getProperties().add(new DtStart(start.getDate(), true));
        this.getProperties().add(new DtEnd(end.getDate(), true));
        if (duration != null) {
            this.getProperties().add(new Duration(duration.getDuration()));
            DateTime freeStart = new DateTime(start.getDate());
            DateTime freeEnd = new DateTime(end.getDate());
            FreeBusy fb = new FreeTimeBuilder().start(freeStart).end(freeEnd).duration(duration.getDuration()).components(components).build();
            if (fb != null && !fb.getPeriods().isEmpty()) {
                this.getProperties().add(fb);
            }
        } else {
            DateTime busyStart = new DateTime(start.getDate());
            DateTime busyEnd = new DateTime(end.getDate());
            FreeBusy fb = new BusyTimeBuilder().start(busyStart).end(busyEnd).components(components).build();
            if (fb != null && !fb.getPeriods().isEmpty()) {
                this.getProperties().add(fb);
            }
        }
    }

    private PeriodList getConsumedTime(ComponentList<CalendarComponent> components, DateTime rangeStart, DateTime rangeEnd) {
        PeriodList periods = new PeriodList();
        for (Component event : components.getComponents("VEVENT")) {
            periods.addAll(((VEvent)event).getConsumedTime(rangeStart, rangeEnd, false));
        }
        return periods.normalise();
    }

    @Override
    public final void validate(boolean recurse) throws ValidationException {
        if (!CompatibilityHints.isHintEnabled("ical4j.validation.relaxed")) {
            PropertyValidator.assertOne("UID", this.getProperties());
            PropertyValidator.assertOne("DTSTAMP", this.getProperties());
        }
        Arrays.asList("CONTACT", "DTSTART", "DTEND", "DURATION", "DTSTAMP", "ORGANIZER", "UID", "URL").forEach(parameter -> PropertyValidator.assertOneOrLess(parameter, this.getProperties()));
        Arrays.asList("RRULE", "EXRULE", "RDATE", "EXDATE").forEach(property -> PropertyValidator.assertNone(property, this.getProperties()));
        DtStart dtStart = (DtStart)this.getProperty("DTSTART");
        if (dtStart != null && !dtStart.isUtc()) {
            throw new ValidationException("DTSTART must be specified in UTC time");
        }
        DtEnd dtEnd = (DtEnd)this.getProperty("DTEND");
        if (dtEnd != null && !dtEnd.isUtc()) {
            throw new ValidationException("DTEND must be specified in UTC time");
        }
        if (dtStart != null && dtEnd != null && !dtStart.getDate().before(dtEnd.getDate())) {
            throw new ValidationException("Property [DTEND] must be later in time than [DTSTART]");
        }
        if (recurse) {
            this.validateProperties();
        }
    }

    protected Validator getValidator(Method method) {
        return this.methodValidators.get(method);
    }

    public final Contact getContact() {
        return (Contact)this.getProperty("CONTACT");
    }

    public final DtStart getStartDate() {
        return (DtStart)this.getProperty("DTSTART");
    }

    public final DtEnd getEndDate() {
        return (DtEnd)this.getProperty("DTEND");
    }

    public final Duration getDuration() {
        return (Duration)this.getProperty("DURATION");
    }

    public final DtStamp getDateStamp() {
        return (DtStamp)this.getProperty("DTSTAMP");
    }

    public final Organizer getOrganizer() {
        return (Organizer)this.getProperty("ORGANIZER");
    }

    public final Url getUrl() {
        return (Url)this.getProperty("URL");
    }

    public final Uid getUid() {
        return (Uid)this.getProperty("UID");
    }

    public static class Factory
    extends Content.Factory
    implements ComponentFactory<VFreeBusy> {
        public Factory() {
            super("VFREEBUSY");
        }

        @Override
        public VFreeBusy createComponent() {
            return new VFreeBusy(false);
        }

        @Override
        public VFreeBusy createComponent(PropertyList properties) {
            return new VFreeBusy(properties);
        }

        @Override
        public VFreeBusy createComponent(PropertyList properties, ComponentList subComponents) {
            throw new UnsupportedOperationException(String.format("%s does not support sub-components", "VFREEBUSY"));
        }
    }

    private class FreeTimeBuilder {
        private DateTime start;
        private DateTime end;
        private TemporalAmount duration;
        private ComponentList<CalendarComponent> components;

        private FreeTimeBuilder() {
        }

        public FreeTimeBuilder start(DateTime start) {
            this.start = start;
            return this;
        }

        public FreeTimeBuilder end(DateTime end) {
            this.end = end;
            return this;
        }

        private FreeTimeBuilder duration(TemporalAmount duration) {
            this.duration = duration;
            return this;
        }

        public FreeTimeBuilder components(ComponentList<CalendarComponent> components) {
            this.components = components;
            return this;
        }

        public FreeBusy build() {
            FreeBusy fb = new FreeBusy();
            fb.getParameters().add(FbType.FREE);
            PeriodList periods = VFreeBusy.this.getConsumedTime(this.components, this.start, this.end);
            DateRange range = new DateRange(this.start, this.end);
            periods.add(new Period(this.end, this.end));
            DateTime lastPeriodEnd = new DateTime(this.start);
            for (Period period : periods) {
                Duration freeDuration;
                if ((range.contains(period) || range.intersects(period) && period.getStart().after(range.getRangeStart())) && new TemporalAmountComparator().compare((freeDuration = new Duration(lastPeriodEnd, period.getStart())).getDuration(), this.duration) >= 0) {
                    fb.getPeriods().add(new Period(lastPeriodEnd, freeDuration.getDuration()));
                }
                if (!period.getEnd().after(lastPeriodEnd)) continue;
                lastPeriodEnd = period.getEnd();
            }
            return fb;
        }
    }

    private class BusyTimeBuilder {
        private DateTime start;
        private DateTime end;
        private ComponentList<CalendarComponent> components;

        private BusyTimeBuilder() {
        }

        public BusyTimeBuilder start(DateTime start) {
            this.start = start;
            return this;
        }

        public BusyTimeBuilder end(DateTime end) {
            this.end = end;
            return this;
        }

        public BusyTimeBuilder components(ComponentList<CalendarComponent> components) {
            this.components = components;
            return this;
        }

        public FreeBusy build() {
            PeriodList periods = VFreeBusy.this.getConsumedTime(this.components, this.start, this.end);
            DateRange range = new DateRange(this.start, this.end);
            periods.setUtc(true);
            periods.removeIf(period -> !range.intersects((DateRange)period));
            return new FreeBusy(periods);
        }
    }
}

