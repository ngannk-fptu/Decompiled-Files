/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.fortuna.ical4j.model.component;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.ConstraintViolationException;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.DateList;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.model.property.DateListProperty;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.RRule;
import net.fortuna.ical4j.model.property.TzOffsetFrom;
import net.fortuna.ical4j.model.property.TzOffsetTo;
import net.fortuna.ical4j.util.Dates;
import net.fortuna.ical4j.util.TimeZones;
import net.fortuna.ical4j.validate.PropertyValidator;
import net.fortuna.ical4j.validate.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Observance
extends Component {
    private static final long serialVersionUID = 2523330383042085994L;
    public static final String STANDARD = "STANDARD";
    public static final String DAYLIGHT = "DAYLIGHT";
    private long[] onsetsMillisec;
    private DateTime[] onsetsDates;
    private Date initialOnset = null;
    private static final String UTC_PATTERN = "yyyyMMdd'T'HHmmss";
    private static final DateFormat UTC_FORMAT = new SimpleDateFormat("yyyyMMdd'T'HHmmss");
    private Date onsetLimit;

    protected Observance(String name) {
        super(name);
    }

    protected Observance(String name, PropertyList<Property> properties) {
        super(name, properties);
    }

    @Override
    public final void validate(boolean recurse) throws ValidationException {
        PropertyValidator.assertOne("TZOFFSETFROM", this.getProperties());
        PropertyValidator.assertOne("TZOFFSETTO", this.getProperties());
        PropertyValidator.assertOne("DTSTART", this.getProperties());
        if (recurse) {
            this.validateProperties();
        }
    }

    public final Date getLatestOnset(Date date) {
        DateTime initialOnsetUTC;
        if (this.initialOnset == null) {
            try {
                DtStart dtStart = (DtStart)this.getRequiredProperty("DTSTART");
                this.initialOnset = this.applyOffsetFrom(this.calculateOnset(dtStart.getDate()));
            }
            catch (ParseException | ConstraintViolationException e) {
                Logger log = LoggerFactory.getLogger(Observance.class);
                log.error("Unexpected error calculating initial onset", (Throwable)e);
                return null;
            }
        }
        if (date.before(this.initialOnset)) {
            return null;
        }
        if (this.onsetsMillisec != null && (this.onsetLimit == null || date.before(this.onsetLimit))) {
            return this.getCachedOnset(date);
        }
        Date onset = this.initialOnset;
        try {
            initialOnsetUTC = this.calculateOnset(((DtStart)this.getProperty("DTSTART")).getDate());
        }
        catch (ParseException e) {
            Logger log = LoggerFactory.getLogger(Observance.class);
            log.error("Unexpected error calculating initial onset", (Throwable)e);
            return null;
        }
        DateList cacheableOnsets = new DateList();
        cacheableOnsets.setUtc(true);
        cacheableOnsets.add(this.initialOnset);
        PropertyList rdates = this.getProperties("RDATE");
        for (Object rdate : rdates) {
            for (Date rdateDate : ((DateListProperty)rdate).getDates()) {
                try {
                    DateTime rdateOnset = this.applyOffsetFrom(this.calculateOnset(rdateDate));
                    if (!rdateOnset.after(date) && rdateOnset.after(onset)) {
                        onset = rdateOnset;
                    }
                    cacheableOnsets.add(rdateOnset);
                }
                catch (ParseException e) {
                    Logger log = LoggerFactory.getLogger(Observance.class);
                    log.error("Unexpected error calculating onset", (Throwable)e);
                }
            }
        }
        PropertyList rrules = this.getProperties("RRULE");
        for (RRule rrule : rrules) {
            Calendar cal = Dates.getCalendarInstance(date);
            cal.setTime(date);
            cal.add(1, 10);
            this.onsetLimit = Dates.getInstance(cal.getTime(), Value.DATE_TIME);
            DateList recurrenceDates = rrule.getRecur().getDates((Date)initialOnsetUTC, this.onsetLimit, Value.DATE_TIME);
            for (Date recurDate : recurrenceDates) {
                DateTime rruleOnset = this.applyOffsetFrom((DateTime)recurDate);
                if (!rruleOnset.after(date) && rruleOnset.after(onset)) {
                    onset = rruleOnset;
                }
                cacheableOnsets.add(rruleOnset);
            }
        }
        Collections.sort(cacheableOnsets);
        this.onsetsMillisec = new long[cacheableOnsets.size()];
        this.onsetsDates = new DateTime[this.onsetsMillisec.length];
        for (int i = 0; i < this.onsetsMillisec.length; ++i) {
            DateTime cacheableOnset = (DateTime)cacheableOnsets.get(i);
            this.onsetsMillisec[i] = cacheableOnset.getTime();
            this.onsetsDates[i] = cacheableOnset;
        }
        return onset;
    }

    private DateTime getCachedOnset(Date date) {
        int index = Arrays.binarySearch(this.onsetsMillisec, date.getTime());
        if (index >= 0) {
            return this.onsetsDates[index];
        }
        int insertionIndex = -index - 1;
        return this.onsetsDates[insertionIndex - 1];
    }

    public final DtStart getStartDate() {
        return (DtStart)this.getProperty("DTSTART");
    }

    public final TzOffsetFrom getOffsetFrom() {
        return (TzOffsetFrom)this.getProperty("TZOFFSETFROM");
    }

    public final TzOffsetTo getOffsetTo() {
        return (TzOffsetTo)this.getProperty("TZOFFSETTO");
    }

    private DateTime calculateOnset(Date date) throws ParseException {
        return this.calculateOnset(date.toString());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private DateTime calculateOnset(String dateStr) throws ParseException {
        long utcOnset;
        DateFormat dateFormat = UTC_FORMAT;
        synchronized (dateFormat) {
            utcOnset = UTC_FORMAT.parse(dateStr).getTime();
        }
        DateTime onset = new DateTime(true);
        onset.setTime(utcOnset);
        return onset;
    }

    private DateTime applyOffsetFrom(DateTime orig) {
        DateTime withOffset = new DateTime(true);
        withOffset.setTime(orig.getTime() - (long)this.getOffsetFrom().getOffset().getTotalSeconds() * 1000L);
        return withOffset;
    }

    static {
        UTC_FORMAT.setTimeZone(TimeZones.getUtcTimeZone());
        UTC_FORMAT.setLenient(false);
    }
}

