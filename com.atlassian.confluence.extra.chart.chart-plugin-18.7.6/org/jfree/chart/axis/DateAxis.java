/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.chart.axis;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import org.jfree.chart.axis.AxisState;
import org.jfree.chart.axis.DateTick;
import org.jfree.chart.axis.DateTickMarkPosition;
import org.jfree.chart.axis.DateTickUnit;
import org.jfree.chart.axis.DateTickUnitType;
import org.jfree.chart.axis.SegmentedTimeline;
import org.jfree.chart.axis.TickType;
import org.jfree.chart.axis.TickUnit;
import org.jfree.chart.axis.TickUnitSource;
import org.jfree.chart.axis.TickUnits;
import org.jfree.chart.axis.Timeline;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.event.AxisChangeEvent;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.ValueAxisPlot;
import org.jfree.data.Range;
import org.jfree.data.time.DateRange;
import org.jfree.data.time.Month;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.Year;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.TextAnchor;
import org.jfree.util.ObjectUtilities;

public class DateAxis
extends ValueAxis
implements Cloneable,
Serializable {
    private static final long serialVersionUID = -1013460999649007604L;
    public static final DateRange DEFAULT_DATE_RANGE = new DateRange();
    public static final double DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS = 2.0;
    public static final DateTickUnit DEFAULT_DATE_TICK_UNIT = new DateTickUnit(DateTickUnitType.DAY, 1, (DateFormat)new SimpleDateFormat());
    public static final Date DEFAULT_ANCHOR_DATE = new Date();
    private DateTickUnit tickUnit;
    private DateFormat dateFormatOverride;
    private DateTickMarkPosition tickMarkPosition = DateTickMarkPosition.START;
    private static final Timeline DEFAULT_TIMELINE = new DefaultTimeline();
    private TimeZone timeZone;
    private Locale locale;
    private Timeline timeline;

    public DateAxis() {
        this(null);
    }

    public DateAxis(String label) {
        this(label, TimeZone.getDefault());
    }

    public DateAxis(String label, TimeZone zone) {
        this(label, zone, Locale.getDefault());
    }

    public DateAxis(String label, TimeZone zone, Locale locale) {
        super(label, DateAxis.createStandardDateTickUnits(zone, locale));
        this.setTickUnit(DEFAULT_DATE_TICK_UNIT, false, false);
        this.setAutoRangeMinimumSize(2.0);
        this.setRange(DEFAULT_DATE_RANGE, false, false);
        this.dateFormatOverride = null;
        this.timeZone = zone;
        this.locale = locale;
        this.timeline = DEFAULT_TIMELINE;
    }

    public TimeZone getTimeZone() {
        return this.timeZone;
    }

    public void setTimeZone(TimeZone zone) {
        if (zone == null) {
            throw new IllegalArgumentException("Null 'zone' argument.");
        }
        if (!this.timeZone.equals(zone)) {
            this.timeZone = zone;
            this.setStandardTickUnits(DateAxis.createStandardDateTickUnits(zone, this.locale));
            this.notifyListeners(new AxisChangeEvent(this));
        }
    }

    public Timeline getTimeline() {
        return this.timeline;
    }

    public void setTimeline(Timeline timeline) {
        if (this.timeline != timeline) {
            this.timeline = timeline;
            this.notifyListeners(new AxisChangeEvent(this));
        }
    }

    public DateTickUnit getTickUnit() {
        return this.tickUnit;
    }

    public void setTickUnit(DateTickUnit unit) {
        this.setTickUnit(unit, true, true);
    }

    public void setTickUnit(DateTickUnit unit, boolean notify, boolean turnOffAutoSelection) {
        this.tickUnit = unit;
        if (turnOffAutoSelection) {
            this.setAutoTickUnitSelection(false, false);
        }
        if (notify) {
            this.notifyListeners(new AxisChangeEvent(this));
        }
    }

    public DateFormat getDateFormatOverride() {
        return this.dateFormatOverride;
    }

    public void setDateFormatOverride(DateFormat formatter) {
        this.dateFormatOverride = formatter;
        this.notifyListeners(new AxisChangeEvent(this));
    }

    public void setRange(Range range) {
        this.setRange(range, true, true);
    }

    public void setRange(Range range, boolean turnOffAutoRange, boolean notify) {
        if (range == null) {
            throw new IllegalArgumentException("Null 'range' argument.");
        }
        if (!(range instanceof DateRange)) {
            range = new DateRange(range);
        }
        super.setRange(range, turnOffAutoRange, notify);
    }

    public void setRange(Date lower, Date upper) {
        if (lower.getTime() >= upper.getTime()) {
            throw new IllegalArgumentException("Requires 'lower' < 'upper'.");
        }
        this.setRange(new DateRange(lower, upper));
    }

    public void setRange(double lower, double upper) {
        if (lower >= upper) {
            throw new IllegalArgumentException("Requires 'lower' < 'upper'.");
        }
        this.setRange(new DateRange(lower, upper));
    }

    public Date getMinimumDate() {
        Date result = null;
        Range range = this.getRange();
        if (range instanceof DateRange) {
            DateRange r = (DateRange)range;
            result = r.getLowerDate();
        } else {
            result = new Date((long)range.getLowerBound());
        }
        return result;
    }

    public void setMinimumDate(Date date) {
        long newMinMillis;
        if (date == null) {
            throw new IllegalArgumentException("Null 'date' argument.");
        }
        Date maxDate = this.getMaximumDate();
        long maxMillis = maxDate.getTime();
        if (maxMillis <= (newMinMillis = date.getTime())) {
            Date oldMin = this.getMinimumDate();
            long length = maxMillis - oldMin.getTime();
            maxDate = new Date(newMinMillis + length);
        }
        this.setRange(new DateRange(date, maxDate), true, false);
        this.notifyListeners(new AxisChangeEvent(this));
    }

    public Date getMaximumDate() {
        Date result = null;
        Range range = this.getRange();
        if (range instanceof DateRange) {
            DateRange r = (DateRange)range;
            result = r.getUpperDate();
        } else {
            result = new Date((long)range.getUpperBound());
        }
        return result;
    }

    public void setMaximumDate(Date maximumDate) {
        long newMaxMillis;
        if (maximumDate == null) {
            throw new IllegalArgumentException("Null 'maximumDate' argument.");
        }
        Date minDate = this.getMinimumDate();
        long minMillis = minDate.getTime();
        if (minMillis >= (newMaxMillis = maximumDate.getTime())) {
            Date oldMax = this.getMaximumDate();
            long length = oldMax.getTime() - minMillis;
            minDate = new Date(newMaxMillis - length);
        }
        this.setRange(new DateRange(minDate, maximumDate), true, false);
        this.notifyListeners(new AxisChangeEvent(this));
    }

    public DateTickMarkPosition getTickMarkPosition() {
        return this.tickMarkPosition;
    }

    public void setTickMarkPosition(DateTickMarkPosition position) {
        if (position == null) {
            throw new IllegalArgumentException("Null 'position' argument.");
        }
        this.tickMarkPosition = position;
        this.notifyListeners(new AxisChangeEvent(this));
    }

    public void configure() {
        if (this.isAutoRange()) {
            this.autoAdjustRange();
        }
    }

    public boolean isHiddenValue(long millis) {
        return !this.timeline.containsDomainValue(new Date(millis));
    }

    public double valueToJava2D(double value, Rectangle2D area, RectangleEdge edge) {
        value = this.timeline.toTimelineValue((long)value);
        DateRange range = (DateRange)this.getRange();
        double axisMin = this.timeline.toTimelineValue(range.getLowerMillis());
        double axisMax = this.timeline.toTimelineValue(range.getUpperMillis());
        double result = 0.0;
        if (RectangleEdge.isTopOrBottom(edge)) {
            double minX = area.getX();
            double maxX = area.getMaxX();
            result = this.isInverted() ? maxX + (value - axisMin) / (axisMax - axisMin) * (minX - maxX) : minX + (value - axisMin) / (axisMax - axisMin) * (maxX - minX);
        } else if (RectangleEdge.isLeftOrRight(edge)) {
            double minY = area.getMinY();
            double maxY = area.getMaxY();
            result = this.isInverted() ? minY + (value - axisMin) / (axisMax - axisMin) * (maxY - minY) : maxY - (value - axisMin) / (axisMax - axisMin) * (maxY - minY);
        }
        return result;
    }

    public double dateToJava2D(Date date, Rectangle2D area, RectangleEdge edge) {
        double value = date.getTime();
        return this.valueToJava2D(value, area, edge);
    }

    public double java2DToValue(double java2DValue, Rectangle2D area, RectangleEdge edge) {
        DateRange range = (DateRange)this.getRange();
        double axisMin = this.timeline.toTimelineValue(range.getLowerMillis());
        double axisMax = this.timeline.toTimelineValue(range.getUpperMillis());
        double min = 0.0;
        double max = 0.0;
        if (RectangleEdge.isTopOrBottom(edge)) {
            min = area.getX();
            max = area.getMaxX();
        } else if (RectangleEdge.isLeftOrRight(edge)) {
            min = area.getMaxY();
            max = area.getY();
        }
        double result = this.isInverted() ? axisMax - (java2DValue - min) / (max - min) * (axisMax - axisMin) : axisMin + (java2DValue - min) / (max - min) * (axisMax - axisMin);
        return this.timeline.toMillisecond((long)result);
    }

    public Date calculateLowestVisibleTickValue(DateTickUnit unit) {
        return this.nextStandardDate(this.getMinimumDate(), unit);
    }

    public Date calculateHighestVisibleTickValue(DateTickUnit unit) {
        return this.previousStandardDate(this.getMaximumDate(), unit);
    }

    protected Date previousStandardDate(Date date, DateTickUnit unit) {
        Calendar calendar = Calendar.getInstance(this.timeZone, this.locale);
        calendar.setTime(date);
        int count = unit.getCount();
        int current = calendar.get(unit.getCalendarField());
        int value = count * (current / count);
        switch (unit.getUnit()) {
            case 6: {
                int years = calendar.get(1);
                int months = calendar.get(2);
                int days = calendar.get(5);
                int hours = calendar.get(11);
                int minutes = calendar.get(12);
                int seconds = calendar.get(13);
                calendar.set(years, months, days, hours, minutes, seconds);
                calendar.set(14, value);
                Date mm = calendar.getTime();
                if (mm.getTime() >= date.getTime()) {
                    calendar.set(14, value - 1);
                    mm = calendar.getTime();
                }
                return mm;
            }
            case 5: {
                int years = calendar.get(1);
                int months = calendar.get(2);
                int days = calendar.get(5);
                int hours = calendar.get(11);
                int minutes = calendar.get(12);
                int milliseconds = this.tickMarkPosition == DateTickMarkPosition.START ? 0 : (this.tickMarkPosition == DateTickMarkPosition.MIDDLE ? 500 : 999);
                calendar.set(14, milliseconds);
                calendar.set(years, months, days, hours, minutes, value);
                Date dd = calendar.getTime();
                if (dd.getTime() >= date.getTime()) {
                    calendar.set(13, value - 1);
                    dd = calendar.getTime();
                }
                return dd;
            }
            case 4: {
                int years = calendar.get(1);
                int months = calendar.get(2);
                int days = calendar.get(5);
                int hours = calendar.get(11);
                int seconds = this.tickMarkPosition == DateTickMarkPosition.START ? 0 : (this.tickMarkPosition == DateTickMarkPosition.MIDDLE ? 30 : 59);
                calendar.clear(14);
                calendar.set(years, months, days, hours, value, seconds);
                Date d0 = calendar.getTime();
                if (d0.getTime() >= date.getTime()) {
                    calendar.set(12, value - 1);
                    d0 = calendar.getTime();
                }
                return d0;
            }
            case 3: {
                int seconds;
                int minutes;
                int years = calendar.get(1);
                int months = calendar.get(2);
                int days = calendar.get(5);
                if (this.tickMarkPosition == DateTickMarkPosition.START) {
                    minutes = 0;
                    seconds = 0;
                } else if (this.tickMarkPosition == DateTickMarkPosition.MIDDLE) {
                    minutes = 30;
                    seconds = 0;
                } else {
                    minutes = 59;
                    seconds = 59;
                }
                calendar.clear(14);
                calendar.set(years, months, days, value, minutes, seconds);
                Date d1 = calendar.getTime();
                if (d1.getTime() >= date.getTime()) {
                    calendar.set(11, value - 1);
                    d1 = calendar.getTime();
                }
                return d1;
            }
            case 2: {
                int hours;
                int years = calendar.get(1);
                int months = calendar.get(2);
                if (this.tickMarkPosition == DateTickMarkPosition.START) {
                    hours = 0;
                    boolean minutes = false;
                    boolean seconds = false;
                } else if (this.tickMarkPosition == DateTickMarkPosition.MIDDLE) {
                    hours = 12;
                    boolean minutes = false;
                    boolean seconds = false;
                } else {
                    hours = 23;
                    int minutes = 59;
                    int seconds = 59;
                }
                calendar.clear(14);
                calendar.set(years, months, value, hours, 0, 0);
                Date d2 = calendar.getTime();
                if (d2.getTime() >= date.getTime()) {
                    calendar.set(5, value - 1);
                    d2 = calendar.getTime();
                }
                return d2;
            }
            case 1: {
                int years = calendar.get(1);
                calendar.clear(14);
                calendar.set(years, value, 1, 0, 0, 0);
                Month month = new Month(calendar.getTime(), this.timeZone, this.locale);
                Date standardDate = this.calculateDateForPosition(month, this.tickMarkPosition);
                long millis = standardDate.getTime();
                if (millis >= date.getTime()) {
                    month = (Month)month.previous();
                    month.peg(Calendar.getInstance(this.timeZone));
                    standardDate = this.calculateDateForPosition(month, this.tickMarkPosition);
                }
                return standardDate;
            }
            case 0: {
                int days;
                int months;
                if (this.tickMarkPosition == DateTickMarkPosition.START) {
                    months = 0;
                    days = 1;
                } else if (this.tickMarkPosition == DateTickMarkPosition.MIDDLE) {
                    months = 6;
                    days = 1;
                } else {
                    months = 11;
                    days = 31;
                }
                calendar.clear(14);
                calendar.set(value, months, days, 0, 0, 0);
                Date d3 = calendar.getTime();
                if (d3.getTime() >= date.getTime()) {
                    calendar.set(1, value - 1);
                    d3 = calendar.getTime();
                }
                return d3;
            }
        }
        return null;
    }

    private Date calculateDateForPosition(RegularTimePeriod period, DateTickMarkPosition position) {
        if (position == null) {
            throw new IllegalArgumentException("Null 'position' argument.");
        }
        Date result = null;
        if (position == DateTickMarkPosition.START) {
            result = new Date(period.getFirstMillisecond());
        } else if (position == DateTickMarkPosition.MIDDLE) {
            result = new Date(period.getMiddleMillisecond());
        } else if (position == DateTickMarkPosition.END) {
            result = new Date(period.getLastMillisecond());
        }
        return result;
    }

    protected Date nextStandardDate(Date date, DateTickUnit unit) {
        Date previous = this.previousStandardDate(date, unit);
        Calendar calendar = Calendar.getInstance(this.timeZone, this.locale);
        calendar.setTime(previous);
        calendar.add(unit.getCalendarField(), unit.getMultiple());
        return calendar.getTime();
    }

    public static TickUnitSource createStandardDateTickUnits() {
        return DateAxis.createStandardDateTickUnits(TimeZone.getDefault(), Locale.getDefault());
    }

    public static TickUnitSource createStandardDateTickUnits(TimeZone zone) {
        return DateAxis.createStandardDateTickUnits(zone, Locale.getDefault());
    }

    public static TickUnitSource createStandardDateTickUnits(TimeZone zone, Locale locale) {
        if (zone == null) {
            throw new IllegalArgumentException("Null 'zone' argument.");
        }
        if (locale == null) {
            throw new IllegalArgumentException("Null 'locale' argument.");
        }
        TickUnits units = new TickUnits();
        SimpleDateFormat f1 = new SimpleDateFormat("HH:mm:ss.SSS", locale);
        SimpleDateFormat f2 = new SimpleDateFormat("HH:mm:ss", locale);
        SimpleDateFormat f3 = new SimpleDateFormat("HH:mm", locale);
        SimpleDateFormat f4 = new SimpleDateFormat("d-MMM, HH:mm", locale);
        SimpleDateFormat f5 = new SimpleDateFormat("d-MMM", locale);
        SimpleDateFormat f6 = new SimpleDateFormat("MMM-yyyy", locale);
        SimpleDateFormat f7 = new SimpleDateFormat("yyyy", locale);
        f1.setTimeZone(zone);
        f2.setTimeZone(zone);
        f3.setTimeZone(zone);
        f4.setTimeZone(zone);
        f5.setTimeZone(zone);
        f6.setTimeZone(zone);
        f7.setTimeZone(zone);
        units.add(new DateTickUnit(DateTickUnitType.MILLISECOND, 1, (DateFormat)f1));
        units.add(new DateTickUnit(DateTickUnitType.MILLISECOND, 5, DateTickUnitType.MILLISECOND, 1, (DateFormat)f1));
        units.add(new DateTickUnit(DateTickUnitType.MILLISECOND, 10, DateTickUnitType.MILLISECOND, 1, (DateFormat)f1));
        units.add(new DateTickUnit(DateTickUnitType.MILLISECOND, 25, DateTickUnitType.MILLISECOND, 5, (DateFormat)f1));
        units.add(new DateTickUnit(DateTickUnitType.MILLISECOND, 50, DateTickUnitType.MILLISECOND, 10, (DateFormat)f1));
        units.add(new DateTickUnit(DateTickUnitType.MILLISECOND, 100, DateTickUnitType.MILLISECOND, 10, (DateFormat)f1));
        units.add(new DateTickUnit(DateTickUnitType.MILLISECOND, 250, DateTickUnitType.MILLISECOND, 10, (DateFormat)f1));
        units.add(new DateTickUnit(DateTickUnitType.MILLISECOND, 500, DateTickUnitType.MILLISECOND, 50, (DateFormat)f1));
        units.add(new DateTickUnit(DateTickUnitType.SECOND, 1, DateTickUnitType.MILLISECOND, 50, (DateFormat)f2));
        units.add(new DateTickUnit(DateTickUnitType.SECOND, 5, DateTickUnitType.SECOND, 1, (DateFormat)f2));
        units.add(new DateTickUnit(DateTickUnitType.SECOND, 10, DateTickUnitType.SECOND, 1, (DateFormat)f2));
        units.add(new DateTickUnit(DateTickUnitType.SECOND, 30, DateTickUnitType.SECOND, 5, (DateFormat)f2));
        units.add(new DateTickUnit(DateTickUnitType.MINUTE, 1, DateTickUnitType.SECOND, 5, (DateFormat)f3));
        units.add(new DateTickUnit(DateTickUnitType.MINUTE, 2, DateTickUnitType.SECOND, 10, (DateFormat)f3));
        units.add(new DateTickUnit(DateTickUnitType.MINUTE, 5, DateTickUnitType.MINUTE, 1, (DateFormat)f3));
        units.add(new DateTickUnit(DateTickUnitType.MINUTE, 10, DateTickUnitType.MINUTE, 1, (DateFormat)f3));
        units.add(new DateTickUnit(DateTickUnitType.MINUTE, 15, DateTickUnitType.MINUTE, 5, (DateFormat)f3));
        units.add(new DateTickUnit(DateTickUnitType.MINUTE, 20, DateTickUnitType.MINUTE, 5, (DateFormat)f3));
        units.add(new DateTickUnit(DateTickUnitType.MINUTE, 30, DateTickUnitType.MINUTE, 5, (DateFormat)f3));
        units.add(new DateTickUnit(DateTickUnitType.HOUR, 1, DateTickUnitType.MINUTE, 5, (DateFormat)f3));
        units.add(new DateTickUnit(DateTickUnitType.HOUR, 2, DateTickUnitType.MINUTE, 10, (DateFormat)f3));
        units.add(new DateTickUnit(DateTickUnitType.HOUR, 4, DateTickUnitType.MINUTE, 30, (DateFormat)f3));
        units.add(new DateTickUnit(DateTickUnitType.HOUR, 6, DateTickUnitType.HOUR, 1, (DateFormat)f3));
        units.add(new DateTickUnit(DateTickUnitType.HOUR, 12, DateTickUnitType.HOUR, 1, (DateFormat)f4));
        units.add(new DateTickUnit(DateTickUnitType.DAY, 1, DateTickUnitType.HOUR, 1, (DateFormat)f5));
        units.add(new DateTickUnit(DateTickUnitType.DAY, 2, DateTickUnitType.HOUR, 1, (DateFormat)f5));
        units.add(new DateTickUnit(DateTickUnitType.DAY, 7, DateTickUnitType.DAY, 1, (DateFormat)f5));
        units.add(new DateTickUnit(DateTickUnitType.DAY, 15, DateTickUnitType.DAY, 1, (DateFormat)f5));
        units.add(new DateTickUnit(DateTickUnitType.MONTH, 1, DateTickUnitType.DAY, 1, (DateFormat)f6));
        units.add(new DateTickUnit(DateTickUnitType.MONTH, 2, DateTickUnitType.DAY, 1, (DateFormat)f6));
        units.add(new DateTickUnit(DateTickUnitType.MONTH, 3, DateTickUnitType.MONTH, 1, (DateFormat)f6));
        units.add(new DateTickUnit(DateTickUnitType.MONTH, 4, DateTickUnitType.MONTH, 1, (DateFormat)f6));
        units.add(new DateTickUnit(DateTickUnitType.MONTH, 6, DateTickUnitType.MONTH, 1, (DateFormat)f6));
        units.add(new DateTickUnit(DateTickUnitType.YEAR, 1, DateTickUnitType.MONTH, 1, (DateFormat)f7));
        units.add(new DateTickUnit(DateTickUnitType.YEAR, 2, DateTickUnitType.MONTH, 3, (DateFormat)f7));
        units.add(new DateTickUnit(DateTickUnitType.YEAR, 5, DateTickUnitType.YEAR, 1, (DateFormat)f7));
        units.add(new DateTickUnit(DateTickUnitType.YEAR, 10, DateTickUnitType.YEAR, 1, (DateFormat)f7));
        units.add(new DateTickUnit(DateTickUnitType.YEAR, 25, DateTickUnitType.YEAR, 5, (DateFormat)f7));
        units.add(new DateTickUnit(DateTickUnitType.YEAR, 50, DateTickUnitType.YEAR, 10, (DateFormat)f7));
        units.add(new DateTickUnit(DateTickUnitType.YEAR, 100, DateTickUnitType.YEAR, 20, (DateFormat)f7));
        return units;
    }

    protected void autoAdjustRange() {
        Plot plot = this.getPlot();
        if (plot == null) {
            return;
        }
        if (plot instanceof ValueAxisPlot) {
            long lower;
            ValueAxisPlot vap = (ValueAxisPlot)((Object)plot);
            Range r = vap.getDataRange(this);
            if (r == null) {
                r = this.timeline instanceof SegmentedTimeline ? new DateRange(((SegmentedTimeline)this.timeline).getStartTime(), ((SegmentedTimeline)this.timeline).getStartTime() + 1L) : new DateRange();
            }
            long upper = this.timeline.toTimelineValue((long)r.getUpperBound());
            long fixedAutoRange = (long)this.getFixedAutoRange();
            if ((double)fixedAutoRange > 0.0) {
                lower = upper - fixedAutoRange;
            } else {
                long minRange;
                lower = this.timeline.toTimelineValue((long)r.getLowerBound());
                double range = upper - lower;
                if (range < (double)(minRange = (long)this.getAutoRangeMinimumSize())) {
                    long expand = (long)((double)minRange - range) / 2L;
                    upper += expand;
                    lower -= expand;
                }
                upper += (long)(range * this.getUpperMargin());
                lower -= (long)(range * this.getLowerMargin());
            }
            upper = this.timeline.toMillisecond(upper);
            lower = this.timeline.toMillisecond(lower);
            DateRange dr = new DateRange(new Date(lower), new Date(upper));
            this.setRange(dr, false, false);
        }
    }

    protected void selectAutoTickUnit(Graphics2D g2, Rectangle2D dataArea, RectangleEdge edge) {
        if (RectangleEdge.isTopOrBottom(edge)) {
            this.selectHorizontalAutoTickUnit(g2, dataArea, edge);
        } else if (RectangleEdge.isLeftOrRight(edge)) {
            this.selectVerticalAutoTickUnit(g2, dataArea, edge);
        }
    }

    protected void selectHorizontalAutoTickUnit(Graphics2D g2, Rectangle2D dataArea, RectangleEdge edge) {
        long shift = 0L;
        if (this.timeline instanceof SegmentedTimeline) {
            shift = ((SegmentedTimeline)this.timeline).getStartTime();
        }
        double zero = this.valueToJava2D((double)shift + 0.0, dataArea, edge);
        double tickLabelWidth = this.estimateMaximumTickLabelWidth(g2, this.getTickUnit());
        TickUnitSource tickUnits = this.getStandardTickUnits();
        TickUnit unit1 = tickUnits.getCeilingTickUnit(this.getTickUnit());
        double x1 = this.valueToJava2D((double)shift + unit1.getSize(), dataArea, edge);
        double unit1Width = Math.abs(x1 - zero);
        double guess = tickLabelWidth / unit1Width * unit1.getSize();
        DateTickUnit unit2 = (DateTickUnit)tickUnits.getCeilingTickUnit(guess);
        double x2 = this.valueToJava2D((double)shift + unit2.getSize(), dataArea, edge);
        double unit2Width = Math.abs(x2 - zero);
        tickLabelWidth = this.estimateMaximumTickLabelWidth(g2, unit2);
        if (tickLabelWidth > unit2Width) {
            unit2 = (DateTickUnit)tickUnits.getLargerTickUnit(unit2);
        }
        this.setTickUnit(unit2, false, false);
    }

    protected void selectVerticalAutoTickUnit(Graphics2D g2, Rectangle2D dataArea, RectangleEdge edge) {
        double y2;
        double unit2Height;
        double y1;
        double candidate1UnitHeight;
        TickUnitSource tickUnits = this.getStandardTickUnits();
        double zero = this.valueToJava2D(0.0, dataArea, edge);
        double estimate1 = this.getRange().getLength() / 10.0;
        DateTickUnit candidate1 = (DateTickUnit)tickUnits.getCeilingTickUnit(estimate1);
        double labelHeight1 = this.estimateMaximumTickLabelHeight(g2, candidate1);
        double estimate2 = labelHeight1 / (candidate1UnitHeight = Math.abs((y1 = this.valueToJava2D(candidate1.getSize(), dataArea, edge)) - zero)) * candidate1.getSize();
        DateTickUnit candidate2 = (DateTickUnit)tickUnits.getCeilingTickUnit(estimate2);
        double labelHeight2 = this.estimateMaximumTickLabelHeight(g2, candidate2);
        DateTickUnit finalUnit = labelHeight2 < (unit2Height = Math.abs((y2 = this.valueToJava2D(candidate2.getSize(), dataArea, edge)) - zero)) ? candidate2 : (DateTickUnit)tickUnits.getLargerTickUnit(candidate2);
        this.setTickUnit(finalUnit, false, false);
    }

    private double estimateMaximumTickLabelWidth(Graphics2D g2, DateTickUnit unit) {
        RectangleInsets tickLabelInsets = this.getTickLabelInsets();
        double result = tickLabelInsets.getLeft() + tickLabelInsets.getRight();
        Font tickLabelFont = this.getTickLabelFont();
        FontRenderContext frc = g2.getFontRenderContext();
        LineMetrics lm = tickLabelFont.getLineMetrics("ABCxyz", frc);
        if (this.isVerticalTickLabels()) {
            result += (double)lm.getHeight();
        } else {
            DateRange range = (DateRange)this.getRange();
            Date lower = range.getLowerDate();
            Date upper = range.getUpperDate();
            String lowerStr = null;
            String upperStr = null;
            DateFormat formatter = this.getDateFormatOverride();
            if (formatter != null) {
                lowerStr = formatter.format(lower);
                upperStr = formatter.format(upper);
            } else {
                lowerStr = unit.dateToString(lower);
                upperStr = unit.dateToString(upper);
            }
            FontMetrics fm = g2.getFontMetrics(tickLabelFont);
            double w1 = fm.stringWidth(lowerStr);
            double w2 = fm.stringWidth(upperStr);
            result += Math.max(w1, w2);
        }
        return result;
    }

    private double estimateMaximumTickLabelHeight(Graphics2D g2, DateTickUnit unit) {
        RectangleInsets tickLabelInsets = this.getTickLabelInsets();
        double result = tickLabelInsets.getTop() + tickLabelInsets.getBottom();
        Font tickLabelFont = this.getTickLabelFont();
        FontRenderContext frc = g2.getFontRenderContext();
        LineMetrics lm = tickLabelFont.getLineMetrics("ABCxyz", frc);
        if (!this.isVerticalTickLabels()) {
            result += (double)lm.getHeight();
        } else {
            DateRange range = (DateRange)this.getRange();
            Date lower = range.getLowerDate();
            Date upper = range.getUpperDate();
            String lowerStr = null;
            String upperStr = null;
            DateFormat formatter = this.getDateFormatOverride();
            if (formatter != null) {
                lowerStr = formatter.format(lower);
                upperStr = formatter.format(upper);
            } else {
                lowerStr = unit.dateToString(lower);
                upperStr = unit.dateToString(upper);
            }
            FontMetrics fm = g2.getFontMetrics(tickLabelFont);
            double w1 = fm.stringWidth(lowerStr);
            double w2 = fm.stringWidth(upperStr);
            result += Math.max(w1, w2);
        }
        return result;
    }

    public List refreshTicks(Graphics2D g2, AxisState state, Rectangle2D dataArea, RectangleEdge edge) {
        List result = null;
        if (RectangleEdge.isTopOrBottom(edge)) {
            result = this.refreshTicksHorizontal(g2, dataArea, edge);
        } else if (RectangleEdge.isLeftOrRight(edge)) {
            result = this.refreshTicksVertical(g2, dataArea, edge);
        }
        return result;
    }

    private Date correctTickDateForPosition(Date time, DateTickUnit unit, DateTickMarkPosition position) {
        Date result = time;
        switch (unit.getUnit()) {
            case 2: 
            case 3: 
            case 4: 
            case 5: 
            case 6: {
                break;
            }
            case 1: {
                result = this.calculateDateForPosition(new Month(time, this.timeZone, this.locale), position);
                break;
            }
            case 0: {
                result = this.calculateDateForPosition(new Year(time, this.timeZone, this.locale), position);
                break;
            }
        }
        return result;
    }

    protected List refreshTicksHorizontal(Graphics2D g2, Rectangle2D dataArea, RectangleEdge edge) {
        ArrayList<DateTick> result = new ArrayList<DateTick>();
        Font tickLabelFont = this.getTickLabelFont();
        g2.setFont(tickLabelFont);
        if (this.isAutoTickUnitSelection()) {
            this.selectAutoTickUnit(g2, dataArea, edge);
        }
        DateTickUnit unit = this.getTickUnit();
        Date tickDate = this.calculateLowestVisibleTickValue(unit);
        Date upperDate = this.getMaximumDate();
        while (tickDate.before(upperDate)) {
            tickDate = this.correctTickDateForPosition(tickDate, unit, this.tickMarkPosition);
            long lowestTickTime = tickDate.getTime();
            long distance = unit.addToDate(tickDate, this.timeZone).getTime() - lowestTickTime;
            int minorTickSpaces = this.getMinorTickCount();
            if (minorTickSpaces <= 0) {
                minorTickSpaces = unit.getMinorTickCount();
            }
            for (int minorTick = 1; minorTick < minorTickSpaces; ++minorTick) {
                long minorTickTime = lowestTickTime - distance * (long)minorTick / (long)minorTickSpaces;
                if (minorTickTime <= 0L || !this.getRange().contains(minorTickTime) || this.isHiddenValue(minorTickTime)) continue;
                result.add(new DateTick(TickType.MINOR, new Date(minorTickTime), "", TextAnchor.TOP_CENTER, TextAnchor.CENTER, 0.0));
            }
            if (!this.isHiddenValue(tickDate.getTime())) {
                DateFormat formatter = this.getDateFormatOverride();
                String tickLabel = formatter != null ? formatter.format(tickDate) : this.tickUnit.dateToString(tickDate);
                TextAnchor anchor = null;
                TextAnchor rotationAnchor = null;
                double angle = 0.0;
                if (this.isVerticalTickLabels()) {
                    anchor = TextAnchor.CENTER_RIGHT;
                    rotationAnchor = TextAnchor.CENTER_RIGHT;
                    angle = edge == RectangleEdge.TOP ? 1.5707963267948966 : -1.5707963267948966;
                } else if (edge == RectangleEdge.TOP) {
                    anchor = TextAnchor.BOTTOM_CENTER;
                    rotationAnchor = TextAnchor.BOTTOM_CENTER;
                } else {
                    anchor = TextAnchor.TOP_CENTER;
                    rotationAnchor = TextAnchor.TOP_CENTER;
                }
                DateTick tick = new DateTick(tickDate, tickLabel, anchor, rotationAnchor, angle);
                result.add(tick);
                long currentTickTime = tickDate.getTime();
                tickDate = unit.addToDate(tickDate, this.timeZone);
                long nextTickTime = tickDate.getTime();
                for (int minorTick = 1; minorTick < minorTickSpaces; ++minorTick) {
                    long minorTickTime = currentTickTime + (nextTickTime - currentTickTime) * (long)minorTick / (long)minorTickSpaces;
                    if (!this.getRange().contains(minorTickTime) || this.isHiddenValue(minorTickTime)) continue;
                    result.add(new DateTick(TickType.MINOR, new Date(minorTickTime), "", TextAnchor.TOP_CENTER, TextAnchor.CENTER, 0.0));
                }
                continue;
            }
            tickDate = unit.rollDate(tickDate, this.timeZone);
        }
        return result;
    }

    protected List refreshTicksVertical(Graphics2D g2, Rectangle2D dataArea, RectangleEdge edge) {
        ArrayList<DateTick> result = new ArrayList<DateTick>();
        Font tickLabelFont = this.getTickLabelFont();
        g2.setFont(tickLabelFont);
        if (this.isAutoTickUnitSelection()) {
            this.selectAutoTickUnit(g2, dataArea, edge);
        }
        DateTickUnit unit = this.getTickUnit();
        Date tickDate = this.calculateLowestVisibleTickValue(unit);
        Date upperDate = this.getMaximumDate();
        while (tickDate.before(upperDate)) {
            tickDate = this.correctTickDateForPosition(tickDate, unit, this.tickMarkPosition);
            long lowestTickTime = tickDate.getTime();
            long distance = unit.addToDate(tickDate, this.timeZone).getTime() - lowestTickTime;
            int minorTickSpaces = this.getMinorTickCount();
            if (minorTickSpaces <= 0) {
                minorTickSpaces = unit.getMinorTickCount();
            }
            for (int minorTick = 1; minorTick < minorTickSpaces; ++minorTick) {
                long minorTickTime = lowestTickTime - distance * (long)minorTick / (long)minorTickSpaces;
                if (minorTickTime <= 0L || !this.getRange().contains(minorTickTime) || this.isHiddenValue(minorTickTime)) continue;
                result.add(new DateTick(TickType.MINOR, new Date(minorTickTime), "", TextAnchor.TOP_CENTER, TextAnchor.CENTER, 0.0));
            }
            if (!this.isHiddenValue(tickDate.getTime())) {
                DateFormat formatter = this.getDateFormatOverride();
                String tickLabel = formatter != null ? formatter.format(tickDate) : this.tickUnit.dateToString(tickDate);
                TextAnchor anchor = null;
                TextAnchor rotationAnchor = null;
                double angle = 0.0;
                if (this.isVerticalTickLabels()) {
                    anchor = TextAnchor.BOTTOM_CENTER;
                    rotationAnchor = TextAnchor.BOTTOM_CENTER;
                    angle = edge == RectangleEdge.LEFT ? -1.5707963267948966 : 1.5707963267948966;
                } else if (edge == RectangleEdge.LEFT) {
                    anchor = TextAnchor.CENTER_RIGHT;
                    rotationAnchor = TextAnchor.CENTER_RIGHT;
                } else {
                    anchor = TextAnchor.CENTER_LEFT;
                    rotationAnchor = TextAnchor.CENTER_LEFT;
                }
                DateTick tick = new DateTick(tickDate, tickLabel, anchor, rotationAnchor, angle);
                result.add(tick);
                long currentTickTime = tickDate.getTime();
                tickDate = unit.addToDate(tickDate, this.timeZone);
                long nextTickTime = tickDate.getTime();
                for (int minorTick = 1; minorTick < minorTickSpaces; ++minorTick) {
                    long minorTickTime = currentTickTime + (nextTickTime - currentTickTime) * (long)minorTick / (long)minorTickSpaces;
                    if (!this.getRange().contains(minorTickTime) || this.isHiddenValue(minorTickTime)) continue;
                    result.add(new DateTick(TickType.MINOR, new Date(minorTickTime), "", TextAnchor.TOP_CENTER, TextAnchor.CENTER, 0.0));
                }
                continue;
            }
            tickDate = unit.rollDate(tickDate, this.timeZone);
        }
        return result;
    }

    public AxisState draw(Graphics2D g2, double cursor, Rectangle2D plotArea, Rectangle2D dataArea, RectangleEdge edge, PlotRenderingInfo plotState) {
        if (!this.isVisible()) {
            AxisState state = new AxisState(cursor);
            List ticks = this.refreshTicks(g2, state, dataArea, edge);
            state.setTicks(ticks);
            return state;
        }
        AxisState state = this.drawTickMarksAndLabels(g2, cursor, plotArea, dataArea, edge);
        state = this.drawLabel(this.getLabel(), g2, plotArea, dataArea, edge, state);
        this.createAndAddEntity(cursor, state, dataArea, edge, plotState);
        return state;
    }

    public void zoomRange(double lowerPercent, double upperPercent) {
        double start = this.timeline.toTimelineValue((long)this.getRange().getLowerBound());
        double length = this.timeline.toTimelineValue((long)this.getRange().getUpperBound()) - this.timeline.toTimelineValue((long)this.getRange().getLowerBound());
        DateRange adjusted = null;
        adjusted = this.isInverted() ? new DateRange(this.timeline.toMillisecond((long)(start + length * (1.0 - upperPercent))), this.timeline.toMillisecond((long)(start + length * (1.0 - lowerPercent)))) : new DateRange(this.timeline.toMillisecond((long)(start + length * lowerPercent)), this.timeline.toMillisecond((long)(start + length * upperPercent)));
        this.setRange(adjusted);
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof DateAxis)) {
            return false;
        }
        DateAxis that = (DateAxis)obj;
        if (!ObjectUtilities.equal(this.tickUnit, that.tickUnit)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.dateFormatOverride, that.dateFormatOverride)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.tickMarkPosition, that.tickMarkPosition)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.timeline, that.timeline)) {
            return false;
        }
        return super.equals(obj);
    }

    public int hashCode() {
        if (this.getLabel() != null) {
            return this.getLabel().hashCode();
        }
        return 0;
    }

    public Object clone() throws CloneNotSupportedException {
        DateAxis clone = (DateAxis)super.clone();
        if (this.dateFormatOverride != null) {
            clone.dateFormatOverride = (DateFormat)this.dateFormatOverride.clone();
        }
        return clone;
    }

    private static class DefaultTimeline
    implements Timeline,
    Serializable {
        private DefaultTimeline() {
        }

        public long toTimelineValue(long millisecond) {
            return millisecond;
        }

        public long toTimelineValue(Date date) {
            return date.getTime();
        }

        public long toMillisecond(long value) {
            return value;
        }

        public boolean containsDomainValue(long millisecond) {
            return true;
        }

        public boolean containsDomainValue(Date date) {
            return true;
        }

        public boolean containsDomainRange(long from, long to) {
            return true;
        }

        public boolean containsDomainRange(Date from, Date to) {
            return true;
        }

        public boolean equals(Object object) {
            if (object == null) {
                return false;
            }
            if (object == this) {
                return true;
            }
            return object instanceof DefaultTimeline;
        }
    }
}

