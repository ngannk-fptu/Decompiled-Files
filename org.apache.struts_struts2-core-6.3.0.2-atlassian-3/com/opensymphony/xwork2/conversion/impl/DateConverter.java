/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package com.opensymphony.xwork2.conversion.impl;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.TextProvider;
import com.opensymphony.xwork2.conversion.impl.DefaultTypeConverter;
import com.opensymphony.xwork2.util.ValueStack;
import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.conversion.TypeConversionException;

public class DateConverter
extends DefaultTypeConverter {
    private static final Logger LOG = LogManager.getLogger(DateConverter.class);

    @Override
    public Object convertValue(Map<String, Object> context, Object target, Member member, String propertyName, Object value, Class toType) {
        Date result;
        block30: {
            result = null;
            if (value instanceof String && ((String)value).length() > 0) {
                Date check;
                String sa = (String)value;
                Locale locale = this.getLocale(context);
                DateFormat df = null;
                if (Time.class == toType) {
                    df = DateFormat.getTimeInstance(2, locale);
                } else if (Timestamp.class == toType) {
                    SimpleDateFormat[] fmts;
                    check = null;
                    SimpleDateFormat dtfmt = (SimpleDateFormat)DateFormat.getDateTimeInstance(3, 2, locale);
                    SimpleDateFormat fullfmt = new SimpleDateFormat(dtfmt.toPattern() + ".SSS", locale);
                    SimpleDateFormat dfmt = (SimpleDateFormat)DateFormat.getDateInstance(3, locale);
                    for (SimpleDateFormat fmt : fmts = new SimpleDateFormat[]{fullfmt, dtfmt, dfmt}) {
                        try {
                            check = fmt.parse(sa);
                            df = fmt;
                            if (check == null) continue;
                            break;
                        }
                        catch (ParseException parseException) {
                            // empty catch block
                        }
                    }
                } else if (Date.class == toType) {
                    DateFormat[] dfs;
                    for (DateFormat df1 : dfs = this.getDateFormats(ActionContext.of(context), locale)) {
                        try {
                            check = df1.parse(sa);
                            df = df1;
                            if (check == null) continue;
                            break;
                        }
                        catch (ParseException parseException) {
                            // empty catch block
                        }
                    }
                } else if (LocalDateTime.class == toType || LocalDate.class == toType || LocalTime.class == toType) {
                    DateTimeFormatter[] dfs;
                    DateTimeFormatter dtf = null;
                    TemporalAccessor check2 = null;
                    for (DateTimeFormatter df1 : dfs = this.getDateTimeFormats(ActionContext.of(context), locale)) {
                        try {
                            check2 = df1.parseBest(sa, LocalDateTime::from, LocalDate::from, LocalTime::from);
                            dtf = df1;
                            if (check2 == null) continue;
                            break;
                        }
                        catch (DateTimeParseException dateTimeParseException) {
                            // empty catch block
                        }
                    }
                    try {
                        if (dtf != null && check2 instanceof LocalDateTime) {
                            return LocalDateTime.parse(sa, dtf);
                        }
                        if (dtf != null && check2 instanceof LocalDate) {
                            return LocalDate.parse(sa, dtf);
                        }
                        if (dtf != null && check2 instanceof LocalTime) {
                            return LocalTime.parse(sa, dtf);
                        }
                        throw new TypeConversionException("Could not parse date");
                    }
                    catch (DateTimeParseException e) {
                        throw new TypeConversionException("Could not parse date", e);
                    }
                }
                if (df == null) {
                    df = DateFormat.getDateInstance(3, locale);
                }
                try {
                    df.setLenient(false);
                    result = df.parse(sa);
                    if (Date.class != toType) {
                        try {
                            Constructor constructor = toType.getConstructor(Long.TYPE);
                            return constructor.newInstance(result.getTime());
                        }
                        catch (Exception e) {
                            throw new TypeConversionException("Couldn't create class " + toType + " using default (long) constructor", e);
                        }
                    }
                    break block30;
                }
                catch (ParseException e) {
                    throw new TypeConversionException("Could not parse date", e);
                }
            }
            if (Date.class.isAssignableFrom(value.getClass())) {
                result = (Date)value;
            }
        }
        return result;
    }

    protected String getGlobalDateString(ActionContext context) {
        String dateTagProperty = "struts.date.format";
        String globalDateString = null;
        TextProvider tp = this.findProviderInStack(context.getValueStack());
        if (tp != null) {
            String globalFormat = tp.getText("struts.date.format");
            if (globalFormat != null && !"struts.date.format".equals(globalFormat)) {
                LOG.debug("Found \"{}\" as \"{}\"", (Object)"struts.date.format", (Object)globalFormat);
                globalDateString = globalFormat;
            } else {
                LOG.debug("\"{}\" has not been defined, ignoring it", (Object)"struts.date.format");
            }
        }
        return globalDateString;
    }

    private DateFormat[] getDateFormats(ActionContext context, Locale locale) {
        SimpleDateFormat globalDateFormat = null;
        String globalFormat = this.getGlobalDateString(context);
        if (globalFormat != null) {
            globalDateFormat = new SimpleDateFormat(globalFormat, locale);
        }
        DateFormat dt1 = DateFormat.getDateTimeInstance(3, 1, locale);
        DateFormat dt2 = DateFormat.getDateTimeInstance(3, 2, locale);
        DateFormat dt3 = DateFormat.getDateTimeInstance(3, 3, locale);
        DateFormat d1 = DateFormat.getDateInstance(3, locale);
        DateFormat d2 = DateFormat.getDateInstance(2, locale);
        DateFormat d3 = DateFormat.getDateInstance(1, locale);
        SimpleDateFormat rfc3339 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        SimpleDateFormat rfc3339dateOnly = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat[] dateFormats = globalDateFormat == null ? new DateFormat[]{dt1, dt2, dt3, rfc3339, d1, d2, d3, rfc3339dateOnly} : new DateFormat[]{globalDateFormat, dt1, dt2, dt3, rfc3339, d1, d2, d3, rfc3339dateOnly};
        return dateFormats;
    }

    protected DateTimeFormatter[] getDateTimeFormats(ActionContext context, Locale locale) {
        DateTimeFormatter globalDateFormat = null;
        String globalFormat = this.getGlobalDateString(context);
        if (globalFormat != null) {
            globalDateFormat = DateTimeFormatter.ofPattern(globalFormat, locale);
        }
        DateTimeFormatter df1 = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        DateTimeFormatter df2 = DateTimeFormatter.ISO_LOCAL_DATE;
        DateTimeFormatter df3 = DateTimeFormatter.ISO_LOCAL_TIME;
        DateTimeFormatter[] dateFormats = globalDateFormat == null ? new DateTimeFormatter[]{df1, df2, df3} : new DateTimeFormatter[]{globalDateFormat, df1, df2, df3};
        return dateFormats;
    }

    private TextProvider findProviderInStack(ValueStack stack) {
        if (stack == null) {
            LOG.warn("ValueStack is null, won't be able to find TextProvider!");
            return null;
        }
        for (Object o : stack.getRoot()) {
            if (!(o instanceof TextProvider)) continue;
            return (TextProvider)o;
        }
        return null;
    }
}

