/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.builder.HashCodeBuilder
 */
package net.fortuna.ical4j.model;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Objects;
import net.fortuna.ical4j.util.Numbers;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class WeekDay
implements Serializable {
    private static final long serialVersionUID = -4412000990022011469L;
    public static final WeekDay SU = new WeekDay(Day.SU, 0);
    public static final WeekDay MO = new WeekDay(Day.MO, 0);
    public static final WeekDay TU = new WeekDay(Day.TU, 0);
    public static final WeekDay WE = new WeekDay(Day.WE, 0);
    public static final WeekDay TH = new WeekDay(Day.TH, 0);
    public static final WeekDay FR = new WeekDay(Day.FR, 0);
    public static final WeekDay SA = new WeekDay(Day.SA, 0);
    private Day day;
    private int offset;

    public WeekDay(String value) {
        this.offset = value.length() > 2 ? Numbers.parseInt(value.substring(0, value.length() - 2)) : 0;
        this.day = Day.valueOf(value.substring(value.length() - 2));
        this.validateDay();
    }

    private WeekDay(Day day, int offset) {
        this.day = day;
        this.offset = offset;
    }

    public WeekDay(WeekDay weekDay, int offset) {
        this.day = weekDay.getDay();
        this.offset = offset;
    }

    private void validateDay() {
        if (!(WeekDay.SU.day.equals((Object)this.day) || WeekDay.MO.day.equals((Object)this.day) || WeekDay.TU.day.equals((Object)this.day) || WeekDay.WE.day.equals((Object)this.day) || WeekDay.TH.day.equals((Object)this.day) || WeekDay.FR.day.equals((Object)this.day) || WeekDay.SA.day.equals((Object)this.day))) {
            throw new IllegalArgumentException("Invalid day: " + (Object)((Object)this.day));
        }
    }

    public final Day getDay() {
        return this.day;
    }

    public final int getOffset() {
        return this.offset;
    }

    public final String toString() {
        StringBuilder b = new StringBuilder();
        if (this.getOffset() != 0) {
            b.append(this.getOffset());
        }
        b.append((Object)this.getDay());
        return b.toString();
    }

    public static WeekDay getWeekDay(Day day) {
        switch (day) {
            case SU: {
                return SU;
            }
            case MO: {
                return MO;
            }
            case TU: {
                return TU;
            }
            case WE: {
                return WE;
            }
            case TH: {
                return TH;
            }
            case FR: {
                return FR;
            }
            case SA: {
                return SA;
            }
        }
        return null;
    }

    public static WeekDay getWeekDay(Calendar cal) {
        return new WeekDay(WeekDay.getDay(cal.get(7)), 0);
    }

    public static WeekDay getMonthlyOffset(Calendar cal) {
        return new WeekDay(WeekDay.getDay(cal.get(7)), cal.get(8));
    }

    public static WeekDay getNegativeMonthlyOffset(Calendar cal) {
        Calendar calClone = (Calendar)cal.clone();
        int delta = -1;
        do {
            calClone.add(7, 6);
        } while (calClone.get(2) == cal.get(2) && --delta > -5);
        return new WeekDay(WeekDay.getDay(cal.get(7)), delta);
    }

    public static WeekDay getDay(int calDay) {
        WeekDay day = null;
        switch (calDay) {
            case 1: {
                day = SU;
                break;
            }
            case 2: {
                day = MO;
                break;
            }
            case 3: {
                day = TU;
                break;
            }
            case 4: {
                day = WE;
                break;
            }
            case 5: {
                day = TH;
                break;
            }
            case 6: {
                day = FR;
                break;
            }
            case 7: {
                day = SA;
                break;
            }
        }
        return day;
    }

    public static int getCalendarDay(WeekDay weekday) {
        int calendarDay = -1;
        if (SU.getDay().equals((Object)weekday.getDay())) {
            calendarDay = 1;
        } else if (MO.getDay().equals((Object)weekday.getDay())) {
            calendarDay = 2;
        } else if (TU.getDay().equals((Object)weekday.getDay())) {
            calendarDay = 3;
        } else if (WE.getDay().equals((Object)weekday.getDay())) {
            calendarDay = 4;
        } else if (TH.getDay().equals((Object)weekday.getDay())) {
            calendarDay = 5;
        } else if (FR.getDay().equals((Object)weekday.getDay())) {
            calendarDay = 6;
        } else if (SA.getDay().equals((Object)weekday.getDay())) {
            calendarDay = 7;
        }
        return calendarDay;
    }

    public final boolean equals(Object arg0) {
        if (arg0 == null) {
            return false;
        }
        if (!(arg0 instanceof WeekDay)) {
            return false;
        }
        WeekDay wd = (WeekDay)arg0;
        return Objects.equals((Object)wd.getDay(), (Object)this.getDay()) && wd.getOffset() == this.getOffset();
    }

    public final int hashCode() {
        return new HashCodeBuilder().append((Object)this.getDay()).append(this.getOffset()).toHashCode();
    }

    public static enum Day {
        SU,
        MO,
        TU,
        WE,
        TH,
        FR,
        SA;

    }
}

