/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.core.util;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.MonthDay;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.time.temporal.IsoFields;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.WeekFields;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

public class ISO8601JavaTimeConverter
extends AbstractSingleValueConverter {
    private static final DateTimeFormatter STD_DATE_TIME = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd'T'HH:mm:ss").appendFraction(ChronoField.NANO_OF_SECOND, 3, 9, true).appendOffsetId().toFormatter();
    private static final DateTimeFormatter STD_ORDINAL_DATE_TIME = new DateTimeFormatterBuilder().appendPattern("yyyy-DDD'T'HH:mm:ss").appendFraction(ChronoField.MILLI_OF_SECOND, 0, 3, true).appendOffsetId().toFormatter();
    private static final DateTimeFormatter BASIC_DATE_TIME = new DateTimeFormatterBuilder().appendPattern("yyyyMMdd'T'HHmmss").appendFraction(ChronoField.MILLI_OF_SECOND, 0, 3, true).appendOffsetId().toFormatter();
    private static final DateTimeFormatter BASIC_ORDINAL_DATE_TIME = new DateTimeFormatterBuilder().appendPattern("yyyyDDD'T'HHmmss").appendFraction(ChronoField.MILLI_OF_SECOND, 0, 3, true).appendOffsetId().toFormatter();
    private static final DateTimeFormatter BASIC_TIME = new DateTimeFormatterBuilder().appendPattern("HHmmss").appendFraction(ChronoField.MILLI_OF_SECOND, 0, 3, true).appendOffsetId().toFormatter();
    private static final DateTimeFormatter ISO_TTIME = new DateTimeFormatterBuilder().appendPattern("'T'HH:mm:ss").appendFraction(ChronoField.MILLI_OF_SECOND, 0, 3, true).appendOffsetId().toFormatter();
    private static final DateTimeFormatter BASIC_TTIME = new DateTimeFormatterBuilder().appendPattern("'T'HHmmss").appendFraction(ChronoField.MILLI_OF_SECOND, 0, 3, true).appendOffsetId().toFormatter();
    private static final DateTimeFormatter ISO_WEEK_DATE_TIME = new DateTimeFormatterBuilder().appendPattern("YYYY-'W'ww-e'T'HH:mm:ss").appendFraction(ChronoField.MILLI_OF_SECOND, 0, 3, true).appendOffsetId().toFormatter();
    private static final DateTimeFormatter BASIC_WEEK_DATE_TIME = new DateTimeFormatterBuilder().appendPattern("YYYY'W'wwe'T'HHmmss").appendFraction(ChronoField.MILLI_OF_SECOND, 0, 3, true).appendOffsetId().toFormatter();
    private static final DateTimeFormatter BASIC_ORDINAL_DATE = new DateTimeFormatterBuilder().appendPattern("yyyyDDD").toFormatter();
    private static final DateTimeFormatter BASIC_WEEK_DATE = new DateTimeFormatterBuilder().appendPattern("YYYY'W'wwe").toFormatter();
    private static final DateTimeFormatter STD_DATE_HOUR = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd'T'HH").toFormatter();
    private static final DateTimeFormatter STD_HOUR = new DateTimeFormatterBuilder().appendPattern("HH").toFormatter();
    private static final DateTimeFormatter STD_YEAR_WEEK = new DateTimeFormatterBuilder().appendPattern("YYYY-'W'ww").parseDefaulting(ChronoField.ALIGNED_DAY_OF_WEEK_IN_YEAR, 1L).toFormatter();

    @Override
    public boolean canConvert(Class type) {
        return false;
    }

    @Override
    public Object fromString(String str) {
        try {
            OffsetDateTime odt = OffsetDateTime.parse(str);
            return GregorianCalendar.from(odt.atZoneSameInstant(ZoneId.systemDefault()));
        }
        catch (DateTimeParseException odt) {
            try {
                LocalDateTime ldt = LocalDateTime.parse(str);
                return GregorianCalendar.from(ldt.atZone(ZoneId.systemDefault()));
            }
            catch (DateTimeParseException ldt) {
                try {
                    Instant instant = Instant.parse(str);
                    return GregorianCalendar.from(instant.atZone(ZoneId.systemDefault()));
                }
                catch (DateTimeParseException instant) {
                    try {
                        OffsetDateTime odt2 = BASIC_DATE_TIME.parse((CharSequence)str, OffsetDateTime::from);
                        return GregorianCalendar.from(odt2.atZoneSameInstant(ZoneId.systemDefault()));
                    }
                    catch (DateTimeParseException odt2) {
                        try {
                            OffsetDateTime odt3 = STD_ORDINAL_DATE_TIME.parse((CharSequence)str, OffsetDateTime::from);
                            return GregorianCalendar.from(odt3.atZoneSameInstant(ZoneId.systemDefault()));
                        }
                        catch (DateTimeParseException odt3) {
                            try {
                                OffsetDateTime odt4 = BASIC_ORDINAL_DATE_TIME.parse((CharSequence)str, OffsetDateTime::from);
                                return GregorianCalendar.from(odt4.atZoneSameInstant(ZoneId.systemDefault()));
                            }
                            catch (DateTimeParseException odt4) {
                                try {
                                    OffsetTime ot = OffsetTime.parse(str);
                                    return GregorianCalendar.from(ot.atDate(LocalDate.ofEpochDay(0L)).atZoneSameInstant(ZoneId.systemDefault()));
                                }
                                catch (DateTimeParseException ot) {
                                    try {
                                        OffsetTime ot2 = BASIC_TIME.parse((CharSequence)str, OffsetTime::from);
                                        return GregorianCalendar.from(ot2.atDate(LocalDate.ofEpochDay(0L)).atZoneSameInstant(ZoneId.systemDefault()));
                                    }
                                    catch (DateTimeParseException ot2) {
                                        try {
                                            OffsetTime ot3 = ISO_TTIME.parse((CharSequence)str, OffsetTime::from);
                                            return GregorianCalendar.from(ot3.atDate(LocalDate.ofEpochDay(0L)).atZoneSameInstant(ZoneId.systemDefault()));
                                        }
                                        catch (DateTimeParseException ot3) {
                                            try {
                                                OffsetTime ot4 = BASIC_TTIME.parse((CharSequence)str, OffsetTime::from);
                                                return GregorianCalendar.from(ot4.atDate(LocalDate.ofEpochDay(0L)).atZoneSameInstant(ZoneId.systemDefault()));
                                            }
                                            catch (DateTimeParseException ot4) {
                                                try {
                                                    TemporalAccessor ta = ISO_WEEK_DATE_TIME.withLocale(Locale.getDefault()).parse(str);
                                                    Year y = Year.from(ta);
                                                    MonthDay md = MonthDay.from(ta);
                                                    OffsetTime ot5 = OffsetTime.from(ta);
                                                    return GregorianCalendar.from(ot5.atDate(y.atMonthDay(md)).atZoneSameInstant(ZoneId.systemDefault()));
                                                }
                                                catch (DateTimeParseException ta) {
                                                    try {
                                                        TemporalAccessor ta2 = BASIC_WEEK_DATE_TIME.withLocale(Locale.getDefault()).parse(str);
                                                        Year y = Year.from(ta2);
                                                        MonthDay md = MonthDay.from(ta2);
                                                        OffsetTime ot6 = OffsetTime.from(ta2);
                                                        return GregorianCalendar.from(ot6.atDate(y.atMonthDay(md)).atZoneSameInstant(ZoneId.systemDefault()));
                                                    }
                                                    catch (DateTimeParseException ta2) {
                                                        try {
                                                            LocalDate ld = LocalDate.parse(str);
                                                            return GregorianCalendar.from(ld.atStartOfDay(ZoneId.systemDefault()));
                                                        }
                                                        catch (DateTimeParseException ld) {
                                                            try {
                                                                LocalDate ld2 = LocalDate.parse(str, DateTimeFormatter.BASIC_ISO_DATE);
                                                                return GregorianCalendar.from(ld2.atStartOfDay(ZoneId.systemDefault()));
                                                            }
                                                            catch (DateTimeParseException ld2) {
                                                                try {
                                                                    LocalDate ld3 = LocalDate.parse(str, DateTimeFormatter.ISO_ORDINAL_DATE);
                                                                    return GregorianCalendar.from(ld3.atStartOfDay(ZoneId.systemDefault()));
                                                                }
                                                                catch (DateTimeParseException ld3) {
                                                                    try {
                                                                        LocalDate ld4 = BASIC_ORDINAL_DATE.parse((CharSequence)str, LocalDate::from);
                                                                        return GregorianCalendar.from(ld4.atStartOfDay(ZoneId.systemDefault()));
                                                                    }
                                                                    catch (DateTimeParseException ld4) {
                                                                        try {
                                                                            LocalDate ld5 = LocalDate.parse(str, DateTimeFormatter.ISO_WEEK_DATE.withLocale(Locale.getDefault()));
                                                                            return GregorianCalendar.from(ld5.atStartOfDay(ZoneId.systemDefault()));
                                                                        }
                                                                        catch (DateTimeParseException ld5) {
                                                                            try {
                                                                                TemporalAccessor ta3 = BASIC_WEEK_DATE.withLocale(Locale.getDefault()).parse(str);
                                                                                Year y = Year.from(ta3);
                                                                                MonthDay md = MonthDay.from(ta3);
                                                                                return GregorianCalendar.from(y.atMonthDay(md).atStartOfDay(ZoneId.systemDefault()));
                                                                            }
                                                                            catch (DateTimeParseException ta3) {
                                                                                try {
                                                                                    LocalDateTime ldt2 = STD_DATE_HOUR.parse((CharSequence)str, LocalDateTime::from);
                                                                                    return GregorianCalendar.from(ldt2.atZone(ZoneId.systemDefault()));
                                                                                }
                                                                                catch (DateTimeParseException ldt2) {
                                                                                    try {
                                                                                        LocalTime lt = STD_HOUR.parse((CharSequence)str, LocalTime::from);
                                                                                        return GregorianCalendar.from(lt.atDate(LocalDate.ofEpochDay(0L)).atZone(ZoneId.systemDefault()));
                                                                                    }
                                                                                    catch (DateTimeParseException lt) {
                                                                                        try {
                                                                                            LocalTime lt2 = LocalTime.parse(str);
                                                                                            return GregorianCalendar.from(lt2.atDate(LocalDate.ofEpochDay(0L)).atZone(ZoneId.systemDefault()));
                                                                                        }
                                                                                        catch (DateTimeParseException lt2) {
                                                                                            try {
                                                                                                YearMonth ym = YearMonth.parse(str);
                                                                                                return GregorianCalendar.from(ym.atDay(1).atStartOfDay(ZoneId.systemDefault()));
                                                                                            }
                                                                                            catch (DateTimeParseException ym) {
                                                                                                try {
                                                                                                    Year y = Year.parse(str);
                                                                                                    return GregorianCalendar.from(y.atDay(1).atStartOfDay(ZoneId.systemDefault()));
                                                                                                }
                                                                                                catch (DateTimeParseException y) {
                                                                                                    try {
                                                                                                        TemporalAccessor ta4 = STD_YEAR_WEEK.withLocale(Locale.getDefault()).parse(str);
                                                                                                        int y2 = ta4.get(WeekFields.ISO.weekBasedYear());
                                                                                                        int w = ta4.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
                                                                                                        return GregorianCalendar.from(LocalDateTime.from(ta4).with(WeekFields.ISO.weekOfYear(), y2).with(WeekFields.ISO.weekOfWeekBasedYear(), w).atZone(ZoneId.systemDefault()));
                                                                                                    }
                                                                                                    catch (DateTimeParseException ta4) {
                                                                                                        ConversionException exception = new ConversionException("Cannot parse date");
                                                                                                        exception.add("date", str);
                                                                                                        throw exception;
                                                                                                    }
                                                                                                }
                                                                                            }
                                                                                        }
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public String toString(Object obj) {
        Calendar calendar = (Calendar)obj;
        Instant instant = Instant.ofEpochMilli(calendar.getTimeInMillis());
        int offsetInMillis = calendar.getTimeZone().getOffset(calendar.getTimeInMillis());
        OffsetDateTime offsetDateTime = OffsetDateTime.ofInstant(instant, ZoneOffset.ofTotalSeconds(offsetInMillis / 1000));
        return STD_DATE_TIME.format(offsetDateTime);
    }
}

