/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.chart.axis;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import org.jfree.chart.axis.AxisSpace;
import org.jfree.chart.axis.AxisState;
import org.jfree.chart.axis.NumberTick;
import org.jfree.chart.axis.PeriodAxisLabelInfo;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.event.AxisChangeEvent;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.ValueAxisPlot;
import org.jfree.data.Range;
import org.jfree.data.time.Day;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.io.SerialUtilities;
import org.jfree.text.TextUtilities;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.TextAnchor;
import org.jfree.util.PublicCloneable;

public class PeriodAxis
extends ValueAxis
implements Cloneable,
PublicCloneable,
Serializable {
    private static final long serialVersionUID = 8353295532075872069L;
    private RegularTimePeriod first;
    private RegularTimePeriod last;
    private TimeZone timeZone;
    private Locale locale;
    private Calendar calendar;
    private Class autoRangeTimePeriodClass;
    private Class majorTickTimePeriodClass;
    private boolean minorTickMarksVisible;
    private Class minorTickTimePeriodClass;
    private float minorTickMarkInsideLength = 0.0f;
    private float minorTickMarkOutsideLength = 2.0f;
    private transient Stroke minorTickMarkStroke = new BasicStroke(0.5f);
    private transient Paint minorTickMarkPaint = Color.black;
    private PeriodAxisLabelInfo[] labelInfo;
    static /* synthetic */ Class class$org$jfree$data$time$Month;
    static /* synthetic */ Class class$org$jfree$data$time$Year;
    static /* synthetic */ Class class$java$util$Date;
    static /* synthetic */ Class class$java$util$TimeZone;
    static /* synthetic */ Class class$java$util$Locale;

    public PeriodAxis(String label) {
        this(label, new Day(), new Day());
    }

    public PeriodAxis(String label, RegularTimePeriod first, RegularTimePeriod last) {
        this(label, first, last, TimeZone.getDefault(), Locale.getDefault());
    }

    public PeriodAxis(String label, RegularTimePeriod first, RegularTimePeriod last, TimeZone timeZone) {
        this(label, first, last, timeZone, Locale.getDefault());
    }

    public PeriodAxis(String label, RegularTimePeriod first, RegularTimePeriod last, TimeZone timeZone, Locale locale) {
        super(label, null);
        if (timeZone == null) {
            throw new IllegalArgumentException("Null 'timeZone' argument.");
        }
        if (locale == null) {
            throw new IllegalArgumentException("Null 'locale' argument.");
        }
        this.first = first;
        this.last = last;
        this.timeZone = timeZone;
        this.locale = locale;
        this.calendar = Calendar.getInstance(timeZone, locale);
        this.first.peg(this.calendar);
        this.last.peg(this.calendar);
        this.autoRangeTimePeriodClass = first.getClass();
        this.majorTickTimePeriodClass = first.getClass();
        this.minorTickMarksVisible = false;
        this.minorTickTimePeriodClass = RegularTimePeriod.downsize(this.majorTickTimePeriodClass);
        this.setAutoRange(true);
        this.labelInfo = new PeriodAxisLabelInfo[2];
        this.labelInfo[0] = new PeriodAxisLabelInfo(class$org$jfree$data$time$Month == null ? (class$org$jfree$data$time$Month = PeriodAxis.class$("org.jfree.data.time.Month")) : class$org$jfree$data$time$Month, new SimpleDateFormat("MMM", locale));
        this.labelInfo[1] = new PeriodAxisLabelInfo(class$org$jfree$data$time$Year == null ? (class$org$jfree$data$time$Year = PeriodAxis.class$("org.jfree.data.time.Year")) : class$org$jfree$data$time$Year, new SimpleDateFormat("yyyy", locale));
    }

    public RegularTimePeriod getFirst() {
        return this.first;
    }

    public void setFirst(RegularTimePeriod first) {
        if (first == null) {
            throw new IllegalArgumentException("Null 'first' argument.");
        }
        this.first = first;
        this.first.peg(this.calendar);
        this.notifyListeners(new AxisChangeEvent(this));
    }

    public RegularTimePeriod getLast() {
        return this.last;
    }

    public void setLast(RegularTimePeriod last) {
        if (last == null) {
            throw new IllegalArgumentException("Null 'last' argument.");
        }
        this.last = last;
        this.last.peg(this.calendar);
        this.notifyListeners(new AxisChangeEvent(this));
    }

    public TimeZone getTimeZone() {
        return this.timeZone;
    }

    public void setTimeZone(TimeZone zone) {
        if (zone == null) {
            throw new IllegalArgumentException("Null 'zone' argument.");
        }
        this.timeZone = zone;
        this.calendar = Calendar.getInstance(zone, this.locale);
        this.first.peg(this.calendar);
        this.last.peg(this.calendar);
        this.notifyListeners(new AxisChangeEvent(this));
    }

    public Locale getLocale() {
        return this.locale;
    }

    public Class getAutoRangeTimePeriodClass() {
        return this.autoRangeTimePeriodClass;
    }

    public void setAutoRangeTimePeriodClass(Class c) {
        if (c == null) {
            throw new IllegalArgumentException("Null 'c' argument.");
        }
        this.autoRangeTimePeriodClass = c;
        this.notifyListeners(new AxisChangeEvent(this));
    }

    public Class getMajorTickTimePeriodClass() {
        return this.majorTickTimePeriodClass;
    }

    public void setMajorTickTimePeriodClass(Class c) {
        if (c == null) {
            throw new IllegalArgumentException("Null 'c' argument.");
        }
        this.majorTickTimePeriodClass = c;
        this.notifyListeners(new AxisChangeEvent(this));
    }

    public boolean isMinorTickMarksVisible() {
        return this.minorTickMarksVisible;
    }

    public void setMinorTickMarksVisible(boolean visible) {
        this.minorTickMarksVisible = visible;
        this.notifyListeners(new AxisChangeEvent(this));
    }

    public Class getMinorTickTimePeriodClass() {
        return this.minorTickTimePeriodClass;
    }

    public void setMinorTickTimePeriodClass(Class c) {
        if (c == null) {
            throw new IllegalArgumentException("Null 'c' argument.");
        }
        this.minorTickTimePeriodClass = c;
        this.notifyListeners(new AxisChangeEvent(this));
    }

    public Stroke getMinorTickMarkStroke() {
        return this.minorTickMarkStroke;
    }

    public void setMinorTickMarkStroke(Stroke stroke) {
        if (stroke == null) {
            throw new IllegalArgumentException("Null 'stroke' argument.");
        }
        this.minorTickMarkStroke = stroke;
        this.notifyListeners(new AxisChangeEvent(this));
    }

    public Paint getMinorTickMarkPaint() {
        return this.minorTickMarkPaint;
    }

    public void setMinorTickMarkPaint(Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.minorTickMarkPaint = paint;
        this.notifyListeners(new AxisChangeEvent(this));
    }

    public float getMinorTickMarkInsideLength() {
        return this.minorTickMarkInsideLength;
    }

    public void setMinorTickMarkInsideLength(float length) {
        this.minorTickMarkInsideLength = length;
        this.notifyListeners(new AxisChangeEvent(this));
    }

    public float getMinorTickMarkOutsideLength() {
        return this.minorTickMarkOutsideLength;
    }

    public void setMinorTickMarkOutsideLength(float length) {
        this.minorTickMarkOutsideLength = length;
        this.notifyListeners(new AxisChangeEvent(this));
    }

    public PeriodAxisLabelInfo[] getLabelInfo() {
        return this.labelInfo;
    }

    public void setLabelInfo(PeriodAxisLabelInfo[] info) {
        this.labelInfo = info;
        this.notifyListeners(new AxisChangeEvent(this));
    }

    public void setRange(Range range, boolean turnOffAutoRange, boolean notify) {
        long upper = Math.round(range.getUpperBound());
        long lower = Math.round(range.getLowerBound());
        this.first = this.createInstance(this.autoRangeTimePeriodClass, new Date(lower), this.timeZone, this.locale);
        this.last = this.createInstance(this.autoRangeTimePeriodClass, new Date(upper), this.timeZone, this.locale);
        super.setRange(new Range(this.first.getFirstMillisecond(), (double)this.last.getLastMillisecond() + 1.0), turnOffAutoRange, notify);
    }

    public void configure() {
        if (this.isAutoRange()) {
            this.autoAdjustRange();
        }
    }

    public AxisSpace reserveSpace(Graphics2D g2, Plot plot, Rectangle2D plotArea, RectangleEdge edge, AxisSpace space) {
        if (space == null) {
            space = new AxisSpace();
        }
        if (!this.isVisible()) {
            return space;
        }
        double dimension = this.getFixedDimension();
        if (dimension > 0.0) {
            space.ensureAtLeast(dimension, edge);
        }
        Rectangle2D labelEnclosure = this.getLabelEnclosure(g2, edge);
        double labelHeight = 0.0;
        double labelWidth = 0.0;
        double tickLabelBandsDimension = 0.0;
        for (int i = 0; i < this.labelInfo.length; ++i) {
            PeriodAxisLabelInfo info = this.labelInfo[i];
            FontMetrics fm = g2.getFontMetrics(info.getLabelFont());
            tickLabelBandsDimension += info.getPadding().extendHeight(fm.getHeight());
        }
        if (RectangleEdge.isTopOrBottom(edge)) {
            labelHeight = labelEnclosure.getHeight();
            space.add(labelHeight + tickLabelBandsDimension, edge);
        } else if (RectangleEdge.isLeftOrRight(edge)) {
            labelWidth = labelEnclosure.getWidth();
            space.add(labelWidth + tickLabelBandsDimension, edge);
        }
        double tickMarkSpace = 0.0;
        if (this.isTickMarksVisible()) {
            tickMarkSpace = this.getTickMarkOutsideLength();
        }
        if (this.minorTickMarksVisible) {
            tickMarkSpace = Math.max(tickMarkSpace, (double)this.minorTickMarkOutsideLength);
        }
        space.add(tickMarkSpace, edge);
        return space;
    }

    public AxisState draw(Graphics2D g2, double cursor, Rectangle2D plotArea, Rectangle2D dataArea, RectangleEdge edge, PlotRenderingInfo plotState) {
        AxisState axisState = new AxisState(cursor);
        if (this.isAxisLineVisible()) {
            this.drawAxisLine(g2, cursor, dataArea, edge);
        }
        if (this.isTickMarksVisible()) {
            this.drawTickMarks(g2, axisState, dataArea, edge);
        }
        if (this.isTickLabelsVisible()) {
            for (int band = 0; band < this.labelInfo.length; ++band) {
                axisState = this.drawTickLabels(band, g2, axisState, dataArea, edge);
            }
        }
        axisState = this.drawLabel(this.getLabel(), g2, plotArea, dataArea, edge, axisState);
        return axisState;
    }

    protected void drawTickMarks(Graphics2D g2, AxisState state, Rectangle2D dataArea, RectangleEdge edge) {
        if (RectangleEdge.isTopOrBottom(edge)) {
            this.drawTickMarksHorizontal(g2, state, dataArea, edge);
        } else if (RectangleEdge.isLeftOrRight(edge)) {
            this.drawTickMarksVertical(g2, state, dataArea, edge);
        }
    }

    protected void drawTickMarksHorizontal(Graphics2D g2, AxisState state, Rectangle2D dataArea, RectangleEdge edge) {
        ArrayList<NumberTick> ticks = new ArrayList<NumberTick>();
        double x0 = dataArea.getX();
        double y0 = state.getCursor();
        double insideLength = this.getTickMarkInsideLength();
        double outsideLength = this.getTickMarkOutsideLength();
        RegularTimePeriod t = this.createInstance(this.majorTickTimePeriodClass, this.first.getStart(), this.getTimeZone(), this.locale);
        long t0 = t.getFirstMillisecond();
        Line2D.Double inside = null;
        Line2D.Double outside = null;
        long firstOnAxis = this.getFirst().getFirstMillisecond();
        long lastOnAxis = this.getLast().getLastMillisecond() + 1L;
        while (t0 <= lastOnAxis) {
            ticks.add(new NumberTick(new Double(t0), "", TextAnchor.CENTER, TextAnchor.CENTER, 0.0));
            x0 = this.valueToJava2D(t0, dataArea, edge);
            if (edge == RectangleEdge.TOP) {
                inside = new Line2D.Double(x0, y0, x0, y0 + insideLength);
                outside = new Line2D.Double(x0, y0, x0, y0 - outsideLength);
            } else if (edge == RectangleEdge.BOTTOM) {
                inside = new Line2D.Double(x0, y0, x0, y0 - insideLength);
                outside = new Line2D.Double(x0, y0, x0, y0 + outsideLength);
            }
            if (t0 >= firstOnAxis) {
                g2.setPaint(this.getTickMarkPaint());
                g2.setStroke(this.getTickMarkStroke());
                g2.draw(inside);
                g2.draw(outside);
            }
            if (this.minorTickMarksVisible) {
                RegularTimePeriod tminor = this.createInstance(this.minorTickTimePeriodClass, new Date(t0), this.getTimeZone(), this.locale);
                long tt0 = tminor.getFirstMillisecond();
                while (tt0 < t.getLastMillisecond() && tt0 < lastOnAxis) {
                    double xx0 = this.valueToJava2D(tt0, dataArea, edge);
                    if (edge == RectangleEdge.TOP) {
                        inside = new Line2D.Double(xx0, y0, xx0, y0 + (double)this.minorTickMarkInsideLength);
                        outside = new Line2D.Double(xx0, y0, xx0, y0 - (double)this.minorTickMarkOutsideLength);
                    } else if (edge == RectangleEdge.BOTTOM) {
                        inside = new Line2D.Double(xx0, y0, xx0, y0 - (double)this.minorTickMarkInsideLength);
                        outside = new Line2D.Double(xx0, y0, xx0, y0 + (double)this.minorTickMarkOutsideLength);
                    }
                    if (tt0 >= firstOnAxis) {
                        g2.setPaint(this.minorTickMarkPaint);
                        g2.setStroke(this.minorTickMarkStroke);
                        g2.draw(inside);
                        g2.draw(outside);
                    }
                    tminor = tminor.next();
                    tminor.peg(this.calendar);
                    tt0 = tminor.getFirstMillisecond();
                }
            }
            t = t.next();
            t.peg(this.calendar);
            t0 = t.getFirstMillisecond();
        }
        if (edge == RectangleEdge.TOP) {
            state.cursorUp(Math.max(outsideLength, (double)this.minorTickMarkOutsideLength));
        } else if (edge == RectangleEdge.BOTTOM) {
            state.cursorDown(Math.max(outsideLength, (double)this.minorTickMarkOutsideLength));
        }
        state.setTicks(ticks);
    }

    protected void drawTickMarksVertical(Graphics2D g2, AxisState state, Rectangle2D dataArea, RectangleEdge edge) {
    }

    protected AxisState drawTickLabels(int band, Graphics2D g2, AxisState state, Rectangle2D dataArea, RectangleEdge edge) {
        double delta1 = 0.0;
        FontMetrics fm = g2.getFontMetrics(this.labelInfo[band].getLabelFont());
        if (edge == RectangleEdge.BOTTOM) {
            delta1 = this.labelInfo[band].getPadding().calculateTopOutset(fm.getHeight());
        } else if (edge == RectangleEdge.TOP) {
            delta1 = this.labelInfo[band].getPadding().calculateBottomOutset(fm.getHeight());
        }
        state.moveCursor(delta1, edge);
        long axisMin = this.first.getFirstMillisecond();
        long axisMax = this.last.getLastMillisecond();
        g2.setFont(this.labelInfo[band].getLabelFont());
        g2.setPaint(this.labelInfo[band].getLabelPaint());
        RegularTimePeriod p1 = this.labelInfo[band].createInstance(new Date(axisMin), this.timeZone, this.locale);
        RegularTimePeriod p2 = this.labelInfo[band].createInstance(new Date(axisMax), this.timeZone, this.locale);
        String label1 = this.labelInfo[band].getDateFormat().format(new Date(p1.getMiddleMillisecond()));
        String label2 = this.labelInfo[band].getDateFormat().format(new Date(p2.getMiddleMillisecond()));
        Rectangle2D b1 = TextUtilities.getTextBounds(label1, g2, g2.getFontMetrics());
        Rectangle2D b2 = TextUtilities.getTextBounds(label2, g2, g2.getFontMetrics());
        double w = Math.max(b1.getWidth(), b2.getWidth());
        long ww = Math.round(this.java2DToValue(dataArea.getX() + w + 5.0, dataArea, edge));
        ww = this.isInverted() ? axisMax - ww : (ww -= axisMin);
        long length = p1.getLastMillisecond() - p1.getFirstMillisecond();
        int periods = (int)(ww / length) + 1;
        RegularTimePeriod p = this.labelInfo[band].createInstance(new Date(axisMin), this.timeZone, this.locale);
        RectangularShape b = null;
        long lastXX = 0L;
        float y = (float)state.getCursor();
        TextAnchor anchor = TextAnchor.TOP_CENTER;
        float yDelta = (float)b1.getHeight();
        if (edge == RectangleEdge.TOP) {
            anchor = TextAnchor.BOTTOM_CENTER;
            yDelta = -yDelta;
        }
        while (p.getFirstMillisecond() <= axisMax) {
            Rectangle2D bb;
            float x = (float)this.valueToJava2D(p.getMiddleMillisecond(), dataArea, edge);
            DateFormat df = this.labelInfo[band].getDateFormat();
            String label = df.format(new Date(p.getMiddleMillisecond()));
            long first = p.getFirstMillisecond();
            long last = p.getLastMillisecond();
            if (last > axisMax && (double)x + (bb = TextUtilities.getTextBounds(label, g2, g2.getFontMetrics())).getWidth() / 2.0 > dataArea.getMaxX()) {
                float xstart = (float)this.valueToJava2D(Math.max(first, axisMin), dataArea, edge);
                if (bb.getWidth() < dataArea.getMaxX() - (double)xstart) {
                    x = ((float)dataArea.getMaxX() + xstart) / 2.0f;
                } else {
                    label = null;
                }
            }
            if (first < axisMin && (double)x - (bb = TextUtilities.getTextBounds(label, g2, g2.getFontMetrics())).getWidth() / 2.0 < dataArea.getX()) {
                float xlast = (float)this.valueToJava2D(Math.min(last, axisMax), dataArea, edge);
                if (bb.getWidth() < (double)xlast - dataArea.getX()) {
                    x = (xlast + (float)dataArea.getX()) / 2.0f;
                } else {
                    label = null;
                }
            }
            if (label != null) {
                g2.setPaint(this.labelInfo[band].getLabelPaint());
                b = TextUtilities.drawAlignedString(label, g2, x, y, anchor);
            }
            if (lastXX > 0L && this.labelInfo[band].getDrawDividers()) {
                long nextXX = p.getFirstMillisecond();
                long mid = (lastXX + nextXX) / 2L;
                float mid2d = (float)this.valueToJava2D(mid, dataArea, edge);
                g2.setStroke(this.labelInfo[band].getDividerStroke());
                g2.setPaint(this.labelInfo[band].getDividerPaint());
                g2.draw(new Line2D.Float(mid2d, y, mid2d, y + yDelta));
            }
            lastXX = last;
            for (int i = 0; i < periods; ++i) {
                p = p.next();
            }
            p.peg(this.calendar);
        }
        double used = 0.0;
        if (b != null) {
            used = b.getHeight();
            if (edge == RectangleEdge.BOTTOM) {
                used += this.labelInfo[band].getPadding().calculateBottomOutset(fm.getHeight());
            } else if (edge == RectangleEdge.TOP) {
                used += this.labelInfo[band].getPadding().calculateTopOutset(fm.getHeight());
            }
        }
        state.moveCursor(used, edge);
        return state;
    }

    public List refreshTicks(Graphics2D g2, AxisState state, Rectangle2D dataArea, RectangleEdge edge) {
        return Collections.EMPTY_LIST;
    }

    public double valueToJava2D(double value, Rectangle2D area, RectangleEdge edge) {
        double result = Double.NaN;
        double axisMin = this.first.getFirstMillisecond();
        double axisMax = this.last.getLastMillisecond();
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

    public double java2DToValue(double java2DValue, Rectangle2D area, RectangleEdge edge) {
        double result = Double.NaN;
        double min = 0.0;
        double max = 0.0;
        double axisMin = this.first.getFirstMillisecond();
        double axisMax = this.last.getLastMillisecond();
        if (RectangleEdge.isTopOrBottom(edge)) {
            min = area.getX();
            max = area.getMaxX();
        } else if (RectangleEdge.isLeftOrRight(edge)) {
            min = area.getMaxY();
            max = area.getY();
        }
        result = this.isInverted() ? axisMax - (java2DValue - min) / (max - min) * (axisMax - axisMin) : axisMin + (java2DValue - min) / (max - min) * (axisMax - axisMin);
        return result;
    }

    protected void autoAdjustRange() {
        Plot plot = this.getPlot();
        if (plot == null) {
            return;
        }
        if (plot instanceof ValueAxisPlot) {
            ValueAxisPlot vap = (ValueAxisPlot)((Object)plot);
            Range r = vap.getDataRange(this);
            if (r == null) {
                r = this.getDefaultAutoRange();
            }
            long upper = Math.round(r.getUpperBound());
            long lower = Math.round(r.getLowerBound());
            this.first = this.createInstance(this.autoRangeTimePeriodClass, new Date(lower), this.timeZone, this.locale);
            this.last = this.createInstance(this.autoRangeTimePeriodClass, new Date(upper), this.timeZone, this.locale);
            this.setRange(r, false, false);
        }
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof PeriodAxis)) {
            return false;
        }
        PeriodAxis that = (PeriodAxis)obj;
        if (!this.first.equals(that.first)) {
            return false;
        }
        if (!this.last.equals(that.last)) {
            return false;
        }
        if (!this.timeZone.equals(that.timeZone)) {
            return false;
        }
        if (!this.locale.equals(that.locale)) {
            return false;
        }
        if (!this.autoRangeTimePeriodClass.equals(that.autoRangeTimePeriodClass)) {
            return false;
        }
        if (this.isMinorTickMarksVisible() != that.isMinorTickMarksVisible()) {
            return false;
        }
        if (!this.majorTickTimePeriodClass.equals(that.majorTickTimePeriodClass)) {
            return false;
        }
        if (!this.minorTickTimePeriodClass.equals(that.minorTickTimePeriodClass)) {
            return false;
        }
        if (!this.minorTickMarkPaint.equals(that.minorTickMarkPaint)) {
            return false;
        }
        if (!this.minorTickMarkStroke.equals(that.minorTickMarkStroke)) {
            return false;
        }
        if (!Arrays.equals(this.labelInfo, that.labelInfo)) {
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
        PeriodAxis clone = (PeriodAxis)super.clone();
        clone.timeZone = (TimeZone)this.timeZone.clone();
        clone.labelInfo = new PeriodAxisLabelInfo[this.labelInfo.length];
        for (int i = 0; i < this.labelInfo.length; ++i) {
            clone.labelInfo[i] = this.labelInfo[i];
        }
        return clone;
    }

    private RegularTimePeriod createInstance(Class periodClass, Date millisecond, TimeZone zone, Locale locale) {
        RegularTimePeriod result = null;
        try {
            Constructor c = periodClass.getDeclaredConstructor(class$java$util$Date == null ? (class$java$util$Date = PeriodAxis.class$("java.util.Date")) : class$java$util$Date, class$java$util$TimeZone == null ? (class$java$util$TimeZone = PeriodAxis.class$("java.util.TimeZone")) : class$java$util$TimeZone, class$java$util$Locale == null ? (class$java$util$Locale = PeriodAxis.class$("java.util.Locale")) : class$java$util$Locale);
            result = (RegularTimePeriod)c.newInstance(millisecond, zone, locale);
        }
        catch (Exception e) {
            try {
                Constructor c = periodClass.getDeclaredConstructor(class$java$util$Date == null ? (class$java$util$Date = PeriodAxis.class$("java.util.Date")) : class$java$util$Date);
                result = (RegularTimePeriod)c.newInstance(millisecond);
            }
            catch (Exception e2) {
                // empty catch block
            }
        }
        return result;
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writeStroke(this.minorTickMarkStroke, stream);
        SerialUtilities.writePaint(this.minorTickMarkPaint, stream);
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.minorTickMarkStroke = SerialUtilities.readStroke(stream);
        this.minorTickMarkPaint = SerialUtilities.readPaint(stream);
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

