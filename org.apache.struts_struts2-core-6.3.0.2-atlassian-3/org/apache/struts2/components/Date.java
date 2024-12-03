/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package org.apache.struts2.components;

import com.opensymphony.xwork2.TextProvider;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.ValueStack;
import java.io.IOException;
import java.io.Writer;
import java.sql.Time;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.components.ContextBean;
import org.apache.struts2.components.date.DateFormatter;
import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.views.annotations.StrutsTagAttribute;

@StrutsTag(name="date", tldBodyContent="empty", tldTagClass="org.apache.struts2.views.jsp.DateTag", description="Render a formatted date.")
public class Date
extends ContextBean {
    private static final Logger LOG = LogManager.getLogger(Date.class);
    public static final String DATETAG_PROPERTY = "struts.date.format";
    public static final String DATETAG_PROPERTY_PAST = "struts.date.format.past";
    private static final String DATETAG_DEFAULT_PAST = "{0} ago";
    public static final String DATETAG_PROPERTY_FUTURE = "struts.date.format.future";
    private static final String DATETAG_DEFAULT_FUTURE = "in {0}";
    public static final String DATETAG_PROPERTY_SECONDS = "struts.date.format.seconds";
    private static final String DATETAG_DEFAULT_SECONDS = "an instant";
    public static final String DATETAG_PROPERTY_MINUTES = "struts.date.format.minutes";
    private static final String DATETAG_DEFAULT_MINUTES = "{0,choice,1#one minute|1<{0} minutes}";
    public static final String DATETAG_PROPERTY_HOURS = "struts.date.format.hours";
    private static final String DATETAG_DEFAULT_HOURS = "{0,choice,1#one hour|1<{0} hours}{1,choice,0#|1#, one minute|1<, {1} minutes}";
    public static final String DATETAG_PROPERTY_DAYS = "struts.date.format.days";
    private static final String DATETAG_DEFAULT_DAYS = "{0,choice,1#one day|1<{0} days}{1,choice,0#|1#, one hour|1<, {1} hours}";
    public static final String DATETAG_PROPERTY_YEARS = "struts.date.format.years";
    private static final String DATETAG_DEFAULT_YEARS = "{0,choice,1#one year|1<{0} years}{1,choice,0#|1#, one day|1<, {1} days}";
    private String name;
    private String format;
    private boolean nice;
    private String timezone;
    private DateFormatter dateFormatter;

    public Date(ValueStack stack) {
        super(stack);
    }

    @Inject
    public void setDateFormatter(DateFormatter dateFormatter) {
        this.dateFormatter = dateFormatter;
    }

    public String formatTime(TextProvider tp, ZonedDateTime date) {
        ZonedDateTime now = ZonedDateTime.now();
        StringBuilder sb = new StringBuilder();
        ArrayList<Object> args = new ArrayList<Object>();
        long secs = Math.abs(now.toEpochSecond() - date.toEpochSecond());
        long mins = secs / 60L;
        long sec = secs % 60L;
        int min = (int)mins % 60;
        long hours = mins / 60L;
        int hour = (int)hours % 24;
        int days = (int)hours / 24;
        int day = days % 365;
        int years = days / 365;
        if (years > 0) {
            args.add(years);
            args.add(day);
            args.add(sb);
            args.add(null);
            sb.append(tp.getText(DATETAG_PROPERTY_YEARS, DATETAG_DEFAULT_YEARS, args));
        } else if (day > 0) {
            args.add(day);
            args.add(hour);
            args.add(sb);
            args.add(null);
            sb.append(tp.getText(DATETAG_PROPERTY_DAYS, DATETAG_DEFAULT_DAYS, args));
        } else if (hour > 0) {
            args.add(hour);
            args.add(min);
            args.add(sb);
            args.add(null);
            sb.append(tp.getText(DATETAG_PROPERTY_HOURS, DATETAG_DEFAULT_HOURS, args));
        } else if (min > 0) {
            args.add(min);
            args.add(sec);
            args.add(sb);
            args.add(null);
            sb.append(tp.getText(DATETAG_PROPERTY_MINUTES, DATETAG_DEFAULT_MINUTES, args));
        } else {
            args.add(sec);
            args.add(sb);
            args.add(null);
            sb.append(tp.getText(DATETAG_PROPERTY_SECONDS, DATETAG_DEFAULT_SECONDS, args));
        }
        args.clear();
        args.add(sb.toString());
        if (date.isBefore(now)) {
            return tp.getText(DATETAG_PROPERTY_PAST, DATETAG_DEFAULT_PAST, args);
        }
        return tp.getText(DATETAG_PROPERTY_FUTURE, DATETAG_DEFAULT_FUTURE, args);
    }

    @Override
    public boolean end(Writer writer, String body) {
        String msg;
        TextProvider textProvider = this.findProviderInStack();
        ZonedDateTime date = null;
        ZoneId tz = this.getTimeZone();
        Object dateObject = this.findValue(this.name);
        if (dateObject instanceof java.sql.Date) {
            date = ((java.sql.Date)dateObject).toLocalDate().atTime(LocalTime.now(tz)).atZone(tz);
        } else if (dateObject instanceof Time) {
            date = ((Time)dateObject).toLocalTime().atDate(ZonedDateTime.now(tz).toLocalDate()).atZone(tz);
        } else if (dateObject instanceof java.util.Date) {
            date = ((java.util.Date)dateObject).toInstant().atZone(tz);
        } else if (dateObject instanceof Calendar) {
            date = ((Calendar)dateObject).toInstant().atZone(tz);
        } else if (dateObject instanceof Long) {
            date = Instant.ofEpochMilli((Long)dateObject).atZone(tz);
        } else if (dateObject instanceof LocalDateTime) {
            date = ((LocalDateTime)dateObject).atZone(tz);
        } else if (dateObject instanceof LocalDate) {
            date = ((LocalDate)dateObject).atStartOfDay(tz);
        } else if (dateObject instanceof LocalTime) {
            date = ((LocalTime)dateObject).atDate(ZonedDateTime.now(tz).toLocalDate()).atZone(tz);
        } else if (dateObject instanceof Instant) {
            date = ((Instant)dateObject).atZone(tz);
        } else if (this.devMode) {
            String developerNotification = "";
            if (textProvider != null) {
                developerNotification = textProvider.getText("devmode.notification", "Developer Notification:\n{0}", new String[]{"Expression [" + this.name + "] passed to <s:date/> tag which was evaluated to [" + dateObject + "](" + (dateObject != null ? dateObject.getClass() : "null") + ") isn't supported!"});
            }
            LOG.warn(developerNotification);
        } else {
            LOG.debug("Expression [{}] passed to <s:date/> tag which was evaluated to [{}]({}) isn't supported!", (Object)this.name, dateObject, dateObject != null ? dateObject.getClass() : "null");
        }
        if (this.format != null) {
            this.format = this.findString(this.format);
        }
        if (date != null && textProvider != null && (msg = this.nice ? this.formatTime(textProvider, date) : this.formatDate(textProvider, date)) != null) {
            try {
                if (this.getVar() == null) {
                    writer.write(msg);
                } else {
                    this.putInContext(msg);
                }
            }
            catch (IOException e) {
                LOG.error("Could not write out Date tag", (Throwable)e);
            }
        }
        return super.end(writer, "");
    }

    private String formatDate(TextProvider textProvider, ZonedDateTime date) {
        String useFormat = this.format;
        if (useFormat == null && DATETAG_PROPERTY.equals(useFormat = textProvider.getText(DATETAG_PROPERTY))) {
            useFormat = null;
        }
        return this.dateFormatter.format(date, useFormat);
    }

    private ZoneId getTimeZone() {
        ZoneId tz = ZoneId.systemDefault();
        if (this.timezone != null) {
            this.timezone = this.stripExpression(this.timezone);
            String actualTimezone = (String)this.getStack().findValue(this.timezone, String.class);
            if (actualTimezone != null) {
                this.timezone = actualTimezone;
            }
            tz = ZoneId.of(this.timezone);
        }
        return tz;
    }

    private TextProvider findProviderInStack() {
        for (Object o : this.getStack().getRoot()) {
            if (!(o instanceof TextProvider)) continue;
            return (TextProvider)o;
        }
        return null;
    }

    @StrutsTagAttribute(description="Date or DateTime format pattern")
    public void setFormat(String format) {
        this.format = format;
    }

    @StrutsTagAttribute(description="Whether to print out the date nicely", type="Boolean", defaultValue="false")
    public void setNice(boolean nice) {
        this.nice = nice;
    }

    @StrutsTagAttribute(description="The specific timezone in which to format the date")
    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String getName() {
        return this.name;
    }

    @StrutsTagAttribute(description="The date value to format", required=true)
    public void setName(String name) {
        this.name = name;
    }

    public String getFormat() {
        return this.format;
    }

    public boolean isNice() {
        return this.nice;
    }

    public String getTimezone() {
        return this.timezone;
    }
}

