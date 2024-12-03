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
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.jfree.chart.axis.AxisState;
import org.jfree.chart.axis.MarkerAxisBand;
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
import org.jfree.data.Range;
import org.jfree.data.RangeType;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.TextAnchor;
import org.jfree.util.ObjectUtilities;

public class NumberAxis
extends ValueAxis
implements Cloneable,
Serializable {
    private static final long serialVersionUID = 2805933088476185789L;
    public static final boolean DEFAULT_AUTO_RANGE_INCLUDES_ZERO = true;
    public static final boolean DEFAULT_AUTO_RANGE_STICKY_ZERO = true;
    public static final NumberTickUnit DEFAULT_TICK_UNIT = new NumberTickUnit(1.0, new DecimalFormat("0"));
    public static final boolean DEFAULT_VERTICAL_TICK_LABELS = false;
    private RangeType rangeType = RangeType.FULL;
    private boolean autoRangeIncludesZero = true;
    private boolean autoRangeStickyZero = true;
    private NumberTickUnit tickUnit = DEFAULT_TICK_UNIT;
    private NumberFormat numberFormatOverride = null;
    private MarkerAxisBand markerBand = null;

    public NumberAxis() {
        this(null);
    }

    public NumberAxis(String label) {
        super(label, NumberAxis.createStandardTickUnits());
    }

    public RangeType getRangeType() {
        return this.rangeType;
    }

    public void setRangeType(RangeType rangeType) {
        if (rangeType == null) {
            throw new IllegalArgumentException("Null 'rangeType' argument.");
        }
        this.rangeType = rangeType;
        this.notifyListeners(new AxisChangeEvent(this));
    }

    public boolean getAutoRangeIncludesZero() {
        return this.autoRangeIncludesZero;
    }

    public void setAutoRangeIncludesZero(boolean flag) {
        if (this.autoRangeIncludesZero != flag) {
            this.autoRangeIncludesZero = flag;
            if (this.isAutoRange()) {
                this.autoAdjustRange();
            }
            this.notifyListeners(new AxisChangeEvent(this));
        }
    }

    public boolean getAutoRangeStickyZero() {
        return this.autoRangeStickyZero;
    }

    public void setAutoRangeStickyZero(boolean flag) {
        if (this.autoRangeStickyZero != flag) {
            this.autoRangeStickyZero = flag;
            if (this.isAutoRange()) {
                this.autoAdjustRange();
            }
            this.notifyListeners(new AxisChangeEvent(this));
        }
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

    public MarkerAxisBand getMarkerBand() {
        return this.markerBand;
    }

    public void setMarkerBand(MarkerAxisBand band) {
        this.markerBand = band;
        this.notifyListeners(new AxisChangeEvent(this));
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
            double lower = r.getLowerBound();
            if (this.rangeType == RangeType.POSITIVE) {
                lower = Math.max(0.0, lower);
                upper = Math.max(0.0, upper);
            } else if (this.rangeType == RangeType.NEGATIVE) {
                lower = Math.min(0.0, lower);
                upper = Math.min(0.0, upper);
            }
            if (this.getAutoRangeIncludesZero()) {
                lower = Math.min(lower, 0.0);
                upper = Math.max(upper, 0.0);
            }
            double range = upper - lower;
            double fixedAutoRange = this.getFixedAutoRange();
            if (fixedAutoRange > 0.0) {
                lower = upper - fixedAutoRange;
            } else {
                double minRange = this.getAutoRangeMinimumSize();
                if (range < minRange) {
                    double expand = (minRange - range) / 2.0;
                    if ((lower -= expand) == (upper += expand)) {
                        double adjust = Math.abs(lower) / 10.0;
                        lower -= adjust;
                        upper += adjust;
                    }
                    if (this.rangeType == RangeType.POSITIVE) {
                        if (lower < 0.0) {
                            upper -= lower;
                            lower = 0.0;
                        }
                    } else if (this.rangeType == RangeType.NEGATIVE && upper > 0.0) {
                        lower -= upper;
                        upper = 0.0;
                    }
                }
                if (this.getAutoRangeStickyZero()) {
                    upper = upper <= 0.0 ? Math.min(0.0, upper + this.getUpperMargin() * range) : (upper += this.getUpperMargin() * range);
                    lower = lower >= 0.0 ? Math.max(0.0, lower - this.getLowerMargin() * range) : (lower -= this.getLowerMargin() * range);
                } else {
                    upper += this.getUpperMargin() * range;
                    lower -= this.getLowerMargin() * range;
                }
            }
            this.setRange(new Range(lower, upper), false, false);
        }
    }

    public double valueToJava2D(double value, Rectangle2D area, RectangleEdge edge) {
        Range range = this.getRange();
        double axisMin = range.getLowerBound();
        double axisMax = range.getUpperBound();
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

    public double java2DToValue(double java2DValue, Rectangle2D area, RectangleEdge edge) {
        Range range = this.getRange();
        double axisMin = range.getLowerBound();
        double axisMax = range.getUpperBound();
        double min = 0.0;
        double max = 0.0;
        if (RectangleEdge.isTopOrBottom(edge)) {
            min = area.getX();
            max = area.getMaxX();
        } else if (RectangleEdge.isLeftOrRight(edge)) {
            min = area.getMaxY();
            max = area.getY();
        }
        if (this.isInverted()) {
            return axisMax - (java2DValue - min) / (max - min) * (axisMax - axisMin);
        }
        return axisMin + (java2DValue - min) / (max - min) * (axisMax - axisMin);
    }

    protected double calculateLowestVisibleTickValue() {
        double unit = this.getTickUnit().getSize();
        double index = Math.ceil(this.getRange().getLowerBound() / unit);
        return index * unit;
    }

    protected double calculateHighestVisibleTickValue() {
        double unit = this.getTickUnit().getSize();
        double index = Math.floor(this.getRange().getUpperBound() / unit);
        return index * unit;
    }

    protected int calculateVisibleTickCount() {
        double unit = this.getTickUnit().getSize();
        Range range = this.getRange();
        return (int)(Math.floor(range.getUpperBound() / unit) - Math.ceil(range.getLowerBound() / unit) + 1.0);
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

    public static TickUnitSource createStandardTickUnits() {
        TickUnits units = new TickUnits();
        DecimalFormat df0 = new DecimalFormat("0.00000000");
        DecimalFormat df1 = new DecimalFormat("0.0000000");
        DecimalFormat df2 = new DecimalFormat("0.000000");
        DecimalFormat df3 = new DecimalFormat("0.00000");
        DecimalFormat df4 = new DecimalFormat("0.0000");
        DecimalFormat df5 = new DecimalFormat("0.000");
        DecimalFormat df6 = new DecimalFormat("0.00");
        DecimalFormat df7 = new DecimalFormat("0.0");
        DecimalFormat df8 = new DecimalFormat("#,##0");
        DecimalFormat df9 = new DecimalFormat("#,###,##0");
        DecimalFormat df10 = new DecimalFormat("#,###,###,##0");
        units.add(new NumberTickUnit(1.0E-7, df1, 2));
        units.add(new NumberTickUnit(1.0E-6, df2, 2));
        units.add(new NumberTickUnit(1.0E-5, df3, 2));
        units.add(new NumberTickUnit(1.0E-4, df4, 2));
        units.add(new NumberTickUnit(0.001, df5, 2));
        units.add(new NumberTickUnit(0.01, df6, 2));
        units.add(new NumberTickUnit(0.1, df7, 2));
        units.add(new NumberTickUnit(1.0, df8, 2));
        units.add(new NumberTickUnit(10.0, df8, 2));
        units.add(new NumberTickUnit(100.0, df8, 2));
        units.add(new NumberTickUnit(1000.0, df8, 2));
        units.add(new NumberTickUnit(10000.0, df8, 2));
        units.add(new NumberTickUnit(100000.0, df8, 2));
        units.add(new NumberTickUnit(1000000.0, df9, 2));
        units.add(new NumberTickUnit(1.0E7, df9, 2));
        units.add(new NumberTickUnit(1.0E8, df9, 2));
        units.add(new NumberTickUnit(1.0E9, df10, 2));
        units.add(new NumberTickUnit(1.0E10, df10, 2));
        units.add(new NumberTickUnit(1.0E11, df10, 2));
        units.add(new NumberTickUnit(2.5E-7, df0, 5));
        units.add(new NumberTickUnit(2.5E-6, df1, 5));
        units.add(new NumberTickUnit(2.5E-5, df2, 5));
        units.add(new NumberTickUnit(2.5E-4, df3, 5));
        units.add(new NumberTickUnit(0.0025, df4, 5));
        units.add(new NumberTickUnit(0.025, df5, 5));
        units.add(new NumberTickUnit(0.25, df6, 5));
        units.add(new NumberTickUnit(2.5, df7, 5));
        units.add(new NumberTickUnit(25.0, df8, 5));
        units.add(new NumberTickUnit(250.0, df8, 5));
        units.add(new NumberTickUnit(2500.0, df8, 5));
        units.add(new NumberTickUnit(25000.0, df8, 5));
        units.add(new NumberTickUnit(250000.0, df8, 5));
        units.add(new NumberTickUnit(2500000.0, df9, 5));
        units.add(new NumberTickUnit(2.5E7, df9, 5));
        units.add(new NumberTickUnit(2.5E8, df9, 5));
        units.add(new NumberTickUnit(2.5E9, df10, 5));
        units.add(new NumberTickUnit(2.5E10, df10, 5));
        units.add(new NumberTickUnit(2.5E11, df10, 5));
        units.add(new NumberTickUnit(5.0E-7, df1, 5));
        units.add(new NumberTickUnit(5.0E-6, df2, 5));
        units.add(new NumberTickUnit(5.0E-5, df3, 5));
        units.add(new NumberTickUnit(5.0E-4, df4, 5));
        units.add(new NumberTickUnit(0.005, df5, 5));
        units.add(new NumberTickUnit(0.05, df6, 5));
        units.add(new NumberTickUnit(0.5, df7, 5));
        units.add(new NumberTickUnit(5.0, df8, 5));
        units.add(new NumberTickUnit(50.0, df8, 5));
        units.add(new NumberTickUnit(500.0, df8, 5));
        units.add(new NumberTickUnit(5000.0, df8, 5));
        units.add(new NumberTickUnit(50000.0, df8, 5));
        units.add(new NumberTickUnit(500000.0, df8, 5));
        units.add(new NumberTickUnit(5000000.0, df9, 5));
        units.add(new NumberTickUnit(5.0E7, df9, 5));
        units.add(new NumberTickUnit(5.0E8, df9, 5));
        units.add(new NumberTickUnit(5.0E9, df10, 5));
        units.add(new NumberTickUnit(5.0E10, df10, 5));
        units.add(new NumberTickUnit(5.0E11, df10, 5));
        return units;
    }

    public static TickUnitSource createIntegerTickUnits() {
        TickUnits units = new TickUnits();
        DecimalFormat df0 = new DecimalFormat("0");
        DecimalFormat df1 = new DecimalFormat("#,##0");
        units.add(new NumberTickUnit(1.0, df0, 2));
        units.add(new NumberTickUnit(2.0, df0, 2));
        units.add(new NumberTickUnit(5.0, df0, 5));
        units.add(new NumberTickUnit(10.0, df0, 2));
        units.add(new NumberTickUnit(20.0, df0, 2));
        units.add(new NumberTickUnit(50.0, df0, 5));
        units.add(new NumberTickUnit(100.0, df0, 2));
        units.add(new NumberTickUnit(200.0, df0, 2));
        units.add(new NumberTickUnit(500.0, df0, 5));
        units.add(new NumberTickUnit(1000.0, df1, 2));
        units.add(new NumberTickUnit(2000.0, df1, 2));
        units.add(new NumberTickUnit(5000.0, df1, 5));
        units.add(new NumberTickUnit(10000.0, df1, 2));
        units.add(new NumberTickUnit(20000.0, df1, 2));
        units.add(new NumberTickUnit(50000.0, df1, 5));
        units.add(new NumberTickUnit(100000.0, df1, 2));
        units.add(new NumberTickUnit(200000.0, df1, 2));
        units.add(new NumberTickUnit(500000.0, df1, 5));
        units.add(new NumberTickUnit(1000000.0, df1, 2));
        units.add(new NumberTickUnit(2000000.0, df1, 2));
        units.add(new NumberTickUnit(5000000.0, df1, 5));
        units.add(new NumberTickUnit(1.0E7, df1, 2));
        units.add(new NumberTickUnit(2.0E7, df1, 2));
        units.add(new NumberTickUnit(5.0E7, df1, 5));
        units.add(new NumberTickUnit(1.0E8, df1, 2));
        units.add(new NumberTickUnit(2.0E8, df1, 2));
        units.add(new NumberTickUnit(5.0E8, df1, 5));
        units.add(new NumberTickUnit(1.0E9, df1, 2));
        units.add(new NumberTickUnit(2.0E9, df1, 2));
        units.add(new NumberTickUnit(5.0E9, df1, 5));
        units.add(new NumberTickUnit(1.0E10, df1, 2));
        return units;
    }

    public static TickUnitSource createStandardTickUnits(Locale locale) {
        TickUnits units = new TickUnits();
        NumberFormat numberFormat = NumberFormat.getNumberInstance(locale);
        units.add(new NumberTickUnit(1.0E-7, numberFormat, 2));
        units.add(new NumberTickUnit(1.0E-6, numberFormat, 2));
        units.add(new NumberTickUnit(1.0E-5, numberFormat, 2));
        units.add(new NumberTickUnit(1.0E-4, numberFormat, 2));
        units.add(new NumberTickUnit(0.001, numberFormat, 2));
        units.add(new NumberTickUnit(0.01, numberFormat, 2));
        units.add(new NumberTickUnit(0.1, numberFormat, 2));
        units.add(new NumberTickUnit(1.0, numberFormat, 2));
        units.add(new NumberTickUnit(10.0, numberFormat, 2));
        units.add(new NumberTickUnit(100.0, numberFormat, 2));
        units.add(new NumberTickUnit(1000.0, numberFormat, 2));
        units.add(new NumberTickUnit(10000.0, numberFormat, 2));
        units.add(new NumberTickUnit(100000.0, numberFormat, 2));
        units.add(new NumberTickUnit(1000000.0, numberFormat, 2));
        units.add(new NumberTickUnit(1.0E7, numberFormat, 2));
        units.add(new NumberTickUnit(1.0E8, numberFormat, 2));
        units.add(new NumberTickUnit(1.0E9, numberFormat, 2));
        units.add(new NumberTickUnit(1.0E10, numberFormat, 2));
        units.add(new NumberTickUnit(2.5E-7, numberFormat, 5));
        units.add(new NumberTickUnit(2.5E-6, numberFormat, 5));
        units.add(new NumberTickUnit(2.5E-5, numberFormat, 5));
        units.add(new NumberTickUnit(2.5E-4, numberFormat, 5));
        units.add(new NumberTickUnit(0.0025, numberFormat, 5));
        units.add(new NumberTickUnit(0.025, numberFormat, 5));
        units.add(new NumberTickUnit(0.25, numberFormat, 5));
        units.add(new NumberTickUnit(2.5, numberFormat, 5));
        units.add(new NumberTickUnit(25.0, numberFormat, 5));
        units.add(new NumberTickUnit(250.0, numberFormat, 5));
        units.add(new NumberTickUnit(2500.0, numberFormat, 5));
        units.add(new NumberTickUnit(25000.0, numberFormat, 5));
        units.add(new NumberTickUnit(250000.0, numberFormat, 5));
        units.add(new NumberTickUnit(2500000.0, numberFormat, 5));
        units.add(new NumberTickUnit(2.5E7, numberFormat, 5));
        units.add(new NumberTickUnit(2.5E8, numberFormat, 5));
        units.add(new NumberTickUnit(2.5E9, numberFormat, 5));
        units.add(new NumberTickUnit(2.5E10, numberFormat, 5));
        units.add(new NumberTickUnit(5.0E-7, numberFormat, 5));
        units.add(new NumberTickUnit(5.0E-6, numberFormat, 5));
        units.add(new NumberTickUnit(5.0E-5, numberFormat, 5));
        units.add(new NumberTickUnit(5.0E-4, numberFormat, 5));
        units.add(new NumberTickUnit(0.005, numberFormat, 5));
        units.add(new NumberTickUnit(0.05, numberFormat, 5));
        units.add(new NumberTickUnit(0.5, numberFormat, 5));
        units.add(new NumberTickUnit(5.0, numberFormat, 5));
        units.add(new NumberTickUnit(50.0, numberFormat, 5));
        units.add(new NumberTickUnit(500.0, numberFormat, 5));
        units.add(new NumberTickUnit(5000.0, numberFormat, 5));
        units.add(new NumberTickUnit(50000.0, numberFormat, 5));
        units.add(new NumberTickUnit(500000.0, numberFormat, 5));
        units.add(new NumberTickUnit(5000000.0, numberFormat, 5));
        units.add(new NumberTickUnit(5.0E7, numberFormat, 5));
        units.add(new NumberTickUnit(5.0E8, numberFormat, 5));
        units.add(new NumberTickUnit(5.0E9, numberFormat, 5));
        units.add(new NumberTickUnit(5.0E10, numberFormat, 5));
        return units;
    }

    public static TickUnitSource createIntegerTickUnits(Locale locale) {
        TickUnits units = new TickUnits();
        NumberFormat numberFormat = NumberFormat.getNumberInstance(locale);
        units.add(new NumberTickUnit(1.0, numberFormat, 2));
        units.add(new NumberTickUnit(2.0, numberFormat, 2));
        units.add(new NumberTickUnit(5.0, numberFormat, 5));
        units.add(new NumberTickUnit(10.0, numberFormat, 2));
        units.add(new NumberTickUnit(20.0, numberFormat, 2));
        units.add(new NumberTickUnit(50.0, numberFormat, 5));
        units.add(new NumberTickUnit(100.0, numberFormat, 2));
        units.add(new NumberTickUnit(200.0, numberFormat, 2));
        units.add(new NumberTickUnit(500.0, numberFormat, 5));
        units.add(new NumberTickUnit(1000.0, numberFormat, 2));
        units.add(new NumberTickUnit(2000.0, numberFormat, 2));
        units.add(new NumberTickUnit(5000.0, numberFormat, 5));
        units.add(new NumberTickUnit(10000.0, numberFormat, 2));
        units.add(new NumberTickUnit(20000.0, numberFormat, 2));
        units.add(new NumberTickUnit(50000.0, numberFormat, 5));
        units.add(new NumberTickUnit(100000.0, numberFormat, 2));
        units.add(new NumberTickUnit(200000.0, numberFormat, 2));
        units.add(new NumberTickUnit(500000.0, numberFormat, 5));
        units.add(new NumberTickUnit(1000000.0, numberFormat, 2));
        units.add(new NumberTickUnit(2000000.0, numberFormat, 2));
        units.add(new NumberTickUnit(5000000.0, numberFormat, 5));
        units.add(new NumberTickUnit(1.0E7, numberFormat, 2));
        units.add(new NumberTickUnit(2.0E7, numberFormat, 2));
        units.add(new NumberTickUnit(5.0E7, numberFormat, 5));
        units.add(new NumberTickUnit(1.0E8, numberFormat, 2));
        units.add(new NumberTickUnit(2.0E8, numberFormat, 2));
        units.add(new NumberTickUnit(5.0E8, numberFormat, 5));
        units.add(new NumberTickUnit(1.0E9, numberFormat, 2));
        units.add(new NumberTickUnit(2.0E9, numberFormat, 2));
        units.add(new NumberTickUnit(5.0E9, numberFormat, 5));
        units.add(new NumberTickUnit(1.0E10, numberFormat, 2));
        return units;
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
        double unit1Width = this.lengthToJava2D(unit1.getSize(), dataArea, edge);
        double guess = tickLabelWidth / unit1Width * unit1.getSize();
        NumberTickUnit unit2 = (NumberTickUnit)tickUnits.getCeilingTickUnit(guess);
        double unit2Width = this.lengthToJava2D(unit2.getSize(), dataArea, edge);
        tickLabelWidth = this.estimateMaximumTickLabelWidth(g2, unit2);
        if (tickLabelWidth > unit2Width) {
            unit2 = (NumberTickUnit)tickUnits.getLargerTickUnit(unit2);
        }
        this.setTickUnit(unit2, false, false);
    }

    protected void selectVerticalAutoTickUnit(Graphics2D g2, Rectangle2D dataArea, RectangleEdge edge) {
        double tickLabelHeight = this.estimateMaximumTickLabelHeight(g2);
        TickUnitSource tickUnits = this.getStandardTickUnits();
        TickUnit unit1 = tickUnits.getCeilingTickUnit(this.getTickUnit());
        double unitHeight = this.lengthToJava2D(unit1.getSize(), dataArea, edge);
        double guess = tickLabelHeight / unitHeight * unit1.getSize();
        NumberTickUnit unit2 = (NumberTickUnit)tickUnits.getCeilingTickUnit(guess);
        double unit2Height = this.lengthToJava2D(unit2.getSize(), dataArea, edge);
        tickLabelHeight = this.estimateMaximumTickLabelHeight(g2);
        if (tickLabelHeight > unit2Height) {
            unit2 = (NumberTickUnit)tickUnits.getLargerTickUnit(unit2);
        }
        this.setTickUnit(unit2, false, false);
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
        ArrayList<NumberTick> result = new ArrayList<NumberTick>();
        Font tickLabelFont = this.getTickLabelFont();
        g2.setFont(tickLabelFont);
        if (this.isAutoTickUnitSelection()) {
            this.selectAutoTickUnit(g2, dataArea, edge);
        }
        NumberTickUnit tu = this.getTickUnit();
        double size = tu.getSize();
        int count = this.calculateVisibleTickCount();
        double lowestTickValue = this.calculateLowestVisibleTickValue();
        if (count <= 500) {
            int minorTickSpaces = this.getMinorTickCount();
            if (minorTickSpaces <= 0) {
                minorTickSpaces = tu.getMinorTickCount();
            }
            for (int minorTick = 1; minorTick < minorTickSpaces; ++minorTick) {
                double minorTickValue = lowestTickValue - size * (double)minorTick / (double)minorTickSpaces;
                if (!this.getRange().contains(minorTickValue)) continue;
                result.add(new NumberTick(TickType.MINOR, minorTickValue, "", TextAnchor.TOP_CENTER, TextAnchor.CENTER, 0.0));
            }
            for (int i = 0; i < count; ++i) {
                double currentTickValue = lowestTickValue + (double)i * size;
                NumberFormat formatter = this.getNumberFormatOverride();
                String tickLabel = formatter != null ? formatter.format(currentTickValue) : this.getTickUnit().valueToString(currentTickValue);
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
                NumberTick tick = new NumberTick(new Double(currentTickValue), tickLabel, anchor, rotationAnchor, angle);
                result.add(tick);
                double nextTickValue = lowestTickValue + (double)(i + 1) * size;
                for (int minorTick = 1; minorTick < minorTickSpaces; ++minorTick) {
                    double minorTickValue = currentTickValue + (nextTickValue - currentTickValue) * (double)minorTick / (double)minorTickSpaces;
                    if (!this.getRange().contains(minorTickValue)) continue;
                    result.add(new NumberTick(TickType.MINOR, minorTickValue, "", TextAnchor.TOP_CENTER, TextAnchor.CENTER, 0.0));
                }
            }
        }
        return result;
    }

    protected List refreshTicksVertical(Graphics2D g2, Rectangle2D dataArea, RectangleEdge edge) {
        ArrayList<NumberTick> result = new ArrayList<NumberTick>();
        result.clear();
        Font tickLabelFont = this.getTickLabelFont();
        g2.setFont(tickLabelFont);
        if (this.isAutoTickUnitSelection()) {
            this.selectAutoTickUnit(g2, dataArea, edge);
        }
        NumberTickUnit tu = this.getTickUnit();
        double size = tu.getSize();
        int count = this.calculateVisibleTickCount();
        double lowestTickValue = this.calculateLowestVisibleTickValue();
        if (count <= 500) {
            int minorTickSpaces = this.getMinorTickCount();
            if (minorTickSpaces <= 0) {
                minorTickSpaces = tu.getMinorTickCount();
            }
            for (int minorTick = 1; minorTick < minorTickSpaces; ++minorTick) {
                double minorTickValue = lowestTickValue - size * (double)minorTick / (double)minorTickSpaces;
                if (!this.getRange().contains(minorTickValue)) continue;
                result.add(new NumberTick(TickType.MINOR, minorTickValue, "", TextAnchor.TOP_CENTER, TextAnchor.CENTER, 0.0));
            }
            for (int i = 0; i < count; ++i) {
                double currentTickValue = lowestTickValue + (double)i * size;
                NumberFormat formatter = this.getNumberFormatOverride();
                String tickLabel = formatter != null ? formatter.format(currentTickValue) : this.getTickUnit().valueToString(currentTickValue);
                TextAnchor anchor = null;
                TextAnchor rotationAnchor = null;
                double angle = 0.0;
                if (this.isVerticalTickLabels()) {
                    if (edge == RectangleEdge.LEFT) {
                        anchor = TextAnchor.BOTTOM_CENTER;
                        rotationAnchor = TextAnchor.BOTTOM_CENTER;
                        angle = -1.5707963267948966;
                    } else {
                        anchor = TextAnchor.BOTTOM_CENTER;
                        rotationAnchor = TextAnchor.BOTTOM_CENTER;
                        angle = 1.5707963267948966;
                    }
                } else if (edge == RectangleEdge.LEFT) {
                    anchor = TextAnchor.CENTER_RIGHT;
                    rotationAnchor = TextAnchor.CENTER_RIGHT;
                } else {
                    anchor = TextAnchor.CENTER_LEFT;
                    rotationAnchor = TextAnchor.CENTER_LEFT;
                }
                NumberTick tick = new NumberTick(new Double(currentTickValue), tickLabel, anchor, rotationAnchor, angle);
                result.add(tick);
                double nextTickValue = lowestTickValue + (double)(i + 1) * size;
                for (int minorTick = 1; minorTick < minorTickSpaces; ++minorTick) {
                    double minorTickValue = currentTickValue + (nextTickValue - currentTickValue) * (double)minorTick / (double)minorTickSpaces;
                    if (!this.getRange().contains(minorTickValue)) continue;
                    result.add(new NumberTick(TickType.MINOR, minorTickValue, "", TextAnchor.TOP_CENTER, TextAnchor.CENTER, 0.0));
                }
            }
        }
        return result;
    }

    public Object clone() throws CloneNotSupportedException {
        NumberAxis clone = (NumberAxis)super.clone();
        if (this.numberFormatOverride != null) {
            clone.numberFormatOverride = (NumberFormat)this.numberFormatOverride.clone();
        }
        return clone;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof NumberAxis)) {
            return false;
        }
        NumberAxis that = (NumberAxis)obj;
        if (this.autoRangeIncludesZero != that.autoRangeIncludesZero) {
            return false;
        }
        if (this.autoRangeStickyZero != that.autoRangeStickyZero) {
            return false;
        }
        if (!ObjectUtilities.equal(this.tickUnit, that.tickUnit)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.numberFormatOverride, that.numberFormatOverride)) {
            return false;
        }
        if (!this.rangeType.equals(that.rangeType)) {
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
}

