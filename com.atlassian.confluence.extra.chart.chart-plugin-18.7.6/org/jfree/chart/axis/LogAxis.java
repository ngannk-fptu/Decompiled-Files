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
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.jfree.chart.axis.AxisState;
import org.jfree.chart.axis.NumberTick;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.TickType;
import org.jfree.chart.axis.TickUnit;
import org.jfree.chart.axis.TickUnitSource;
import org.jfree.chart.axis.TickUnits;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.event.AxisChangeEvent;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.ValueAxisPlot;
import org.jfree.chart.util.LogFormat;
import org.jfree.data.Range;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.TextAnchor;

public class LogAxis
extends ValueAxis {
    private double base = 10.0;
    private double baseLog = Math.log(10.0);
    private double smallestValue = 1.0E-100;
    private NumberTickUnit tickUnit;
    private NumberFormat numberFormatOverride;

    public LogAxis() {
        this(null);
    }

    public LogAxis(String label) {
        super(label, LogAxis.createLogTickUnits(Locale.getDefault()));
        this.setDefaultAutoRange(new Range(0.01, 1.0));
        this.tickUnit = new NumberTickUnit(1.0, new DecimalFormat("0.#"), 9);
    }

    public double getBase() {
        return this.base;
    }

    public void setBase(double base) {
        if (base <= 1.0) {
            throw new IllegalArgumentException("Requires 'base' > 1.0.");
        }
        this.base = base;
        this.baseLog = Math.log(base);
        this.notifyListeners(new AxisChangeEvent(this));
    }

    public double getSmallestValue() {
        return this.smallestValue;
    }

    public void setSmallestValue(double value) {
        if (value <= 0.0) {
            throw new IllegalArgumentException("Requires 'value' > 0.0.");
        }
        this.smallestValue = value;
        this.notifyListeners(new AxisChangeEvent(this));
    }

    public NumberTickUnit getTickUnit() {
        return this.tickUnit;
    }

    public void setTickUnit(NumberTickUnit unit) {
        this.setTickUnit(unit, true, true);
    }

    public void setTickUnit(NumberTickUnit unit, boolean notify, boolean turnOffAutoSelect) {
        if (unit == null) {
            throw new IllegalArgumentException("Null 'unit' argument.");
        }
        this.tickUnit = unit;
        if (turnOffAutoSelect) {
            this.setAutoTickUnitSelection(false, false);
        }
        if (notify) {
            this.notifyListeners(new AxisChangeEvent(this));
        }
    }

    public NumberFormat getNumberFormatOverride() {
        return this.numberFormatOverride;
    }

    public void setNumberFormatOverride(NumberFormat formatter) {
        this.numberFormatOverride = formatter;
        this.notifyListeners(new AxisChangeEvent(this));
    }

    public double calculateLog(double value) {
        return Math.log(value) / this.baseLog;
    }

    public double calculateValue(double log) {
        return Math.pow(this.base, log);
    }

    public double java2DToValue(double java2DValue, Rectangle2D area, RectangleEdge edge) {
        Range range = this.getRange();
        double axisMin = this.calculateLog(range.getLowerBound());
        double axisMax = this.calculateLog(range.getUpperBound());
        double min = 0.0;
        double max = 0.0;
        if (RectangleEdge.isTopOrBottom(edge)) {
            min = area.getX();
            max = area.getMaxX();
        } else if (RectangleEdge.isLeftOrRight(edge)) {
            min = area.getMaxY();
            max = area.getY();
        }
        double log = 0.0;
        log = this.isInverted() ? axisMax - (java2DValue - min) / (max - min) * (axisMax - axisMin) : axisMin + (java2DValue - min) / (max - min) * (axisMax - axisMin);
        return this.calculateValue(log);
    }

    public double valueToJava2D(double value, Rectangle2D area, RectangleEdge edge) {
        Range range = this.getRange();
        double axisMin = this.calculateLog(range.getLowerBound());
        double axisMax = this.calculateLog(range.getUpperBound());
        value = this.calculateLog(value);
        double min = 0.0;
        double max = 0.0;
        if (RectangleEdge.isTopOrBottom(edge)) {
            min = area.getX();
            max = area.getMaxX();
        } else if (RectangleEdge.isLeftOrRight(edge)) {
            max = area.getMinY();
            min = area.getMaxY();
        }
        if (this.isInverted()) {
            return max - (value - axisMin) / (axisMax - axisMin) * (max - min);
        }
        return min + (value - axisMin) / (axisMax - axisMin) * (max - min);
    }

    public void configure() {
        if (this.isAutoRange()) {
            this.autoAdjustRange();
        }
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
            double upper = r.getUpperBound();
            double lower = Math.max(r.getLowerBound(), this.smallestValue);
            double range = upper - lower;
            double fixedAutoRange = this.getFixedAutoRange();
            if (fixedAutoRange > 0.0) {
                lower = Math.max(upper - fixedAutoRange, this.smallestValue);
            } else {
                double minRange = this.getAutoRangeMinimumSize();
                if (range < minRange) {
                    double expand = (minRange - range) / 2.0;
                    upper += expand;
                    lower -= expand;
                }
                double logUpper = this.calculateLog(upper);
                double logLower = this.calculateLog(lower);
                double logRange = logUpper - logLower;
                upper = this.calculateValue(logUpper += this.getUpperMargin() * logRange);
                lower = this.calculateValue(logLower -= this.getLowerMargin() * logRange);
            }
            this.setRange(new Range(lower, upper), false, false);
        }
    }

    public AxisState draw(Graphics2D g2, double cursor, Rectangle2D plotArea, Rectangle2D dataArea, RectangleEdge edge, PlotRenderingInfo plotState) {
        AxisState state = null;
        if (!this.isVisible()) {
            state = new AxisState(cursor);
            List ticks = this.refreshTicks(g2, state, dataArea, edge);
            state.setTicks(ticks);
            return state;
        }
        state = this.drawTickMarksAndLabels(g2, cursor, plotArea, dataArea, edge);
        state = this.drawLabel(this.getLabel(), g2, plotArea, dataArea, edge, state);
        this.createAndAddEntity(cursor, state, dataArea, edge, plotState);
        return state;
    }

    public List refreshTicks(Graphics2D g2, AxisState state, Rectangle2D dataArea, RectangleEdge edge) {
        List result = new ArrayList();
        if (RectangleEdge.isTopOrBottom(edge)) {
            result = this.refreshTicksHorizontal(g2, dataArea, edge);
        } else if (RectangleEdge.isLeftOrRight(edge)) {
            result = this.refreshTicksVertical(g2, dataArea, edge);
        }
        return result;
    }

    protected List refreshTicksHorizontal(Graphics2D g2, Rectangle2D dataArea, RectangleEdge edge) {
        Range range = this.getRange();
        ArrayList<NumberTick> ticks = new ArrayList<NumberTick>();
        Font tickLabelFont = this.getTickLabelFont();
        g2.setFont(tickLabelFont);
        TextAnchor textAnchor = edge == RectangleEdge.TOP ? TextAnchor.BOTTOM_CENTER : TextAnchor.TOP_CENTER;
        if (this.isAutoTickUnitSelection()) {
            this.selectAutoTickUnit(g2, dataArea, edge);
        }
        int minorTickCount = this.tickUnit.getMinorTickCount();
        double start = Math.floor(this.calculateLog(this.getLowerBound()));
        double end = Math.ceil(this.calculateLog(this.getUpperBound()));
        for (double current = start; current <= end; current += this.tickUnit.getSize()) {
            double v = this.calculateValue(current);
            if (range.contains(v)) {
                ticks.add(new NumberTick(TickType.MAJOR, v, this.createTickLabel(v), textAnchor, TextAnchor.CENTER, 0.0));
            }
            double next = Math.pow(this.base, current + this.tickUnit.getSize());
            for (int i = 1; i < minorTickCount; ++i) {
                double minorV = v + (double)i * ((next - v) / (double)minorTickCount);
                if (!range.contains(minorV)) continue;
                ticks.add(new NumberTick(TickType.MINOR, minorV, "", textAnchor, TextAnchor.CENTER, 0.0));
            }
        }
        return ticks;
    }

    protected List refreshTicksVertical(Graphics2D g2, Rectangle2D dataArea, RectangleEdge edge) {
        Range range = this.getRange();
        ArrayList<NumberTick> ticks = new ArrayList<NumberTick>();
        Font tickLabelFont = this.getTickLabelFont();
        g2.setFont(tickLabelFont);
        TextAnchor textAnchor = edge == RectangleEdge.RIGHT ? TextAnchor.CENTER_LEFT : TextAnchor.CENTER_RIGHT;
        if (this.isAutoTickUnitSelection()) {
            this.selectAutoTickUnit(g2, dataArea, edge);
        }
        int minorTickCount = this.tickUnit.getMinorTickCount();
        double start = Math.floor(this.calculateLog(this.getLowerBound()));
        double end = Math.ceil(this.calculateLog(this.getUpperBound()));
        for (double current = start; current <= end; current += this.tickUnit.getSize()) {
            double v = this.calculateValue(current);
            if (range.contains(v)) {
                ticks.add(new NumberTick(TickType.MAJOR, v, this.createTickLabel(v), textAnchor, TextAnchor.CENTER, 0.0));
            }
            double next = Math.pow(this.base, current + this.tickUnit.getSize());
            for (int i = 1; i < minorTickCount; ++i) {
                double minorV = v + (double)i * ((next - v) / (double)minorTickCount);
                if (!range.contains(minorV)) continue;
                ticks.add(new NumberTick(TickType.MINOR, minorV, "", textAnchor, TextAnchor.CENTER, 0.0));
            }
        }
        return ticks;
    }

    protected void selectAutoTickUnit(Graphics2D g2, Rectangle2D dataArea, RectangleEdge edge) {
        if (RectangleEdge.isTopOrBottom(edge)) {
            this.selectHorizontalAutoTickUnit(g2, dataArea, edge);
        } else if (RectangleEdge.isLeftOrRight(edge)) {
            this.selectVerticalAutoTickUnit(g2, dataArea, edge);
        }
    }

    protected void selectHorizontalAutoTickUnit(Graphics2D g2, Rectangle2D dataArea, RectangleEdge edge) {
        double tickLabelWidth = this.estimateMaximumTickLabelWidth(g2, this.getTickUnit());
        TickUnitSource tickUnits = this.getStandardTickUnits();
        TickUnit unit1 = tickUnits.getCeilingTickUnit(this.getTickUnit());
        double unit1Width = this.exponentLengthToJava2D(unit1.getSize(), dataArea, edge);
        double guess = tickLabelWidth / unit1Width * unit1.getSize();
        NumberTickUnit unit2 = (NumberTickUnit)tickUnits.getCeilingTickUnit(guess);
        double unit2Width = this.exponentLengthToJava2D(unit2.getSize(), dataArea, edge);
        tickLabelWidth = this.estimateMaximumTickLabelWidth(g2, unit2);
        if (tickLabelWidth > unit2Width) {
            unit2 = (NumberTickUnit)tickUnits.getLargerTickUnit(unit2);
        }
        this.setTickUnit(unit2, false, false);
    }

    public double exponentLengthToJava2D(double length, Rectangle2D area, RectangleEdge edge) {
        double one = this.valueToJava2D(this.calculateValue(1.0), area, edge);
        double l = this.valueToJava2D(this.calculateValue(length + 1.0), area, edge);
        return Math.abs(l - one);
    }

    protected void selectVerticalAutoTickUnit(Graphics2D g2, Rectangle2D dataArea, RectangleEdge edge) {
        double tickLabelHeight = this.estimateMaximumTickLabelHeight(g2);
        TickUnitSource tickUnits = this.getStandardTickUnits();
        TickUnit unit1 = tickUnits.getCeilingTickUnit(this.getTickUnit());
        double unitHeight = this.exponentLengthToJava2D(unit1.getSize(), dataArea, edge);
        double guess = tickLabelHeight / unitHeight * unit1.getSize();
        NumberTickUnit unit2 = (NumberTickUnit)tickUnits.getCeilingTickUnit(guess);
        double unit2Height = this.exponentLengthToJava2D(unit2.getSize(), dataArea, edge);
        tickLabelHeight = this.estimateMaximumTickLabelHeight(g2);
        if (tickLabelHeight > unit2Height) {
            unit2 = (NumberTickUnit)tickUnits.getLargerTickUnit(unit2);
        }
        this.setTickUnit(unit2, false, false);
    }

    protected double estimateMaximumTickLabelHeight(Graphics2D g2) {
        RectangleInsets tickLabelInsets = this.getTickLabelInsets();
        double result = tickLabelInsets.getTop() + tickLabelInsets.getBottom();
        Font tickLabelFont = this.getTickLabelFont();
        FontRenderContext frc = g2.getFontRenderContext();
        return result += (double)tickLabelFont.getLineMetrics("123", frc).getHeight();
    }

    protected double estimateMaximumTickLabelWidth(Graphics2D g2, TickUnit unit) {
        RectangleInsets tickLabelInsets = this.getTickLabelInsets();
        double result = tickLabelInsets.getLeft() + tickLabelInsets.getRight();
        if (this.isVerticalTickLabels()) {
            FontRenderContext frc = g2.getFontRenderContext();
            LineMetrics lm = this.getTickLabelFont().getLineMetrics("0", frc);
            result += (double)lm.getHeight();
        } else {
            FontMetrics fm = g2.getFontMetrics(this.getTickLabelFont());
            Range range = this.getRange();
            double lower = range.getLowerBound();
            double upper = range.getUpperBound();
            String lowerStr = "";
            String upperStr = "";
            NumberFormat formatter = this.getNumberFormatOverride();
            if (formatter != null) {
                lowerStr = formatter.format(lower);
                upperStr = formatter.format(upper);
            } else {
                lowerStr = unit.valueToString(lower);
                upperStr = unit.valueToString(upper);
            }
            double w1 = fm.stringWidth(lowerStr);
            double w2 = fm.stringWidth(upperStr);
            result += Math.max(w1, w2);
        }
        return result;
    }

    public void zoomRange(double lowerPercent, double upperPercent) {
        Range range = this.getRange();
        double start = range.getLowerBound();
        double end = range.getUpperBound();
        double log1 = this.calculateLog(start);
        double log2 = this.calculateLog(end);
        double length = log2 - log1;
        Range adjusted = null;
        if (this.isInverted()) {
            double logA = log1 + length * (1.0 - upperPercent);
            double logB = log1 + length * (1.0 - lowerPercent);
            adjusted = new Range(this.calculateValue(logA), this.calculateValue(logB));
        } else {
            double logA = log1 + length * lowerPercent;
            double logB = log1 + length * upperPercent;
            adjusted = new Range(this.calculateValue(logA), this.calculateValue(logB));
        }
        this.setRange(adjusted);
    }

    public void pan(double percent) {
        Range range = this.getRange();
        double lower = range.getLowerBound();
        double upper = range.getUpperBound();
        double log1 = this.calculateLog(lower);
        double log2 = this.calculateLog(upper);
        double length = log2 - log1;
        double adj = length * percent;
        this.setRange(this.calculateValue(log1 += adj), this.calculateValue(log2 += adj));
    }

    protected String createTickLabel(double value) {
        if (this.numberFormatOverride != null) {
            return this.numberFormatOverride.format(value);
        }
        return this.tickUnit.valueToString(value);
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof LogAxis)) {
            return false;
        }
        LogAxis that = (LogAxis)obj;
        if (this.base != that.base) {
            return false;
        }
        if (this.smallestValue != that.smallestValue) {
            return false;
        }
        return super.equals(obj);
    }

    public int hashCode() {
        int result = 193;
        long temp = Double.doubleToLongBits(this.base);
        result = 37 * result + (int)(temp ^ temp >>> 32);
        temp = Double.doubleToLongBits(this.smallestValue);
        result = 37 * result + (int)(temp ^ temp >>> 32);
        if (this.numberFormatOverride != null) {
            result = 37 * result + this.numberFormatOverride.hashCode();
        }
        result = 37 * result + this.tickUnit.hashCode();
        return result;
    }

    public static TickUnitSource createLogTickUnits(Locale locale) {
        TickUnits units = new TickUnits();
        LogFormat numberFormat = new LogFormat();
        units.add(new NumberTickUnit(0.05, numberFormat, 2));
        units.add(new NumberTickUnit(0.1, numberFormat, 10));
        units.add(new NumberTickUnit(0.2, numberFormat, 2));
        units.add(new NumberTickUnit(0.5, numberFormat, 5));
        units.add(new NumberTickUnit(1.0, numberFormat, 10));
        units.add(new NumberTickUnit(2.0, numberFormat, 10));
        units.add(new NumberTickUnit(3.0, numberFormat, 15));
        units.add(new NumberTickUnit(4.0, numberFormat, 20));
        units.add(new NumberTickUnit(5.0, numberFormat, 25));
        units.add(new NumberTickUnit(6.0, numberFormat));
        units.add(new NumberTickUnit(7.0, numberFormat));
        units.add(new NumberTickUnit(8.0, numberFormat));
        units.add(new NumberTickUnit(9.0, numberFormat));
        units.add(new NumberTickUnit(10.0, numberFormat));
        return units;
    }
}

