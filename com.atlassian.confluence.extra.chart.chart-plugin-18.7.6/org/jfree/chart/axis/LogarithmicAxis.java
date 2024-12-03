/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.chart.axis;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTick;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.ValueAxisPlot;
import org.jfree.data.Range;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.TextAnchor;

public class LogarithmicAxis
extends NumberAxis {
    private static final long serialVersionUID = 2502918599004103054L;
    public static final double LOG10_VALUE = Math.log(10.0);
    public static final double SMALL_LOG_VALUE = 1.0E-100;
    protected boolean allowNegativesFlag = false;
    protected boolean strictValuesFlag = true;
    protected final NumberFormat numberFormatterObj = NumberFormat.getInstance();
    protected boolean expTickLabelsFlag = false;
    protected boolean log10TickLabelsFlag = false;
    protected boolean autoRangeNextLogFlag = false;
    protected boolean smallLogFlag = false;

    public LogarithmicAxis(String label) {
        super(label);
        this.setupNumberFmtObj();
    }

    public void setAllowNegativesFlag(boolean flgVal) {
        this.allowNegativesFlag = flgVal;
    }

    public boolean getAllowNegativesFlag() {
        return this.allowNegativesFlag;
    }

    public void setStrictValuesFlag(boolean flgVal) {
        this.strictValuesFlag = flgVal;
    }

    public boolean getStrictValuesFlag() {
        return this.strictValuesFlag;
    }

    public void setExpTickLabelsFlag(boolean flgVal) {
        this.expTickLabelsFlag = flgVal;
        this.setupNumberFmtObj();
    }

    public boolean getExpTickLabelsFlag() {
        return this.expTickLabelsFlag;
    }

    public void setLog10TickLabelsFlag(boolean flag) {
        this.log10TickLabelsFlag = flag;
    }

    public boolean getLog10TickLabelsFlag() {
        return this.log10TickLabelsFlag;
    }

    public void setAutoRangeNextLogFlag(boolean flag) {
        this.autoRangeNextLogFlag = flag;
    }

    public boolean getAutoRangeNextLogFlag() {
        return this.autoRangeNextLogFlag;
    }

    public void setRange(Range range) {
        super.setRange(range);
        this.setupSmallLogFlag();
    }

    protected void setupSmallLogFlag() {
        double lowerVal = this.getRange().getLowerBound();
        this.smallLogFlag = !this.allowNegativesFlag && lowerVal < 10.0 && lowerVal > 0.0;
    }

    protected void setupNumberFmtObj() {
        if (this.numberFormatterObj instanceof DecimalFormat) {
            ((DecimalFormat)this.numberFormatterObj).applyPattern(this.expTickLabelsFlag ? "0E0" : "0.###");
        }
    }

    protected double switchedLog10(double val) {
        return this.smallLogFlag ? Math.log(val) / LOG10_VALUE : this.adjustedLog10(val);
    }

    public double switchedPow10(double val) {
        return this.smallLogFlag ? Math.pow(10.0, val) : this.adjustedPow10(val);
    }

    public double adjustedLog10(double val) {
        boolean negFlag;
        boolean bl = negFlag = val < 0.0;
        if (negFlag) {
            val = -val;
        }
        if (val < 10.0) {
            val += (10.0 - val) / 10.0;
        }
        double res = Math.log(val) / LOG10_VALUE;
        return negFlag ? -res : res;
    }

    public double adjustedPow10(double val) {
        boolean negFlag;
        boolean bl = negFlag = val < 0.0;
        if (negFlag) {
            val = -val;
        }
        double res = val < 1.0 ? (Math.pow(10.0, val + 1.0) - 10.0) / 9.0 : Math.pow(10.0, val);
        return negFlag ? -res : res;
    }

    protected double computeLogFloor(double lower) {
        double logFloor;
        if (this.allowNegativesFlag) {
            if (lower > 10.0) {
                logFloor = Math.log(lower) / LOG10_VALUE;
                logFloor = Math.floor(logFloor);
                logFloor = Math.pow(10.0, logFloor);
            } else if (lower < -10.0) {
                logFloor = Math.log(-lower) / LOG10_VALUE;
                logFloor = Math.floor(-logFloor);
                logFloor = -Math.pow(10.0, -logFloor);
            } else {
                logFloor = Math.floor(lower);
            }
        } else if (lower > 0.0) {
            logFloor = Math.log(lower) / LOG10_VALUE;
            logFloor = Math.floor(logFloor);
            logFloor = Math.pow(10.0, logFloor);
        } else {
            logFloor = Math.floor(lower);
        }
        return logFloor;
    }

    protected double computeLogCeil(double upper) {
        double logCeil;
        if (this.allowNegativesFlag) {
            if (upper > 10.0) {
                logCeil = Math.log(upper) / LOG10_VALUE;
                logCeil = Math.ceil(logCeil);
                logCeil = Math.pow(10.0, logCeil);
            } else if (upper < -10.0) {
                logCeil = Math.log(-upper) / LOG10_VALUE;
                logCeil = Math.ceil(-logCeil);
                logCeil = -Math.pow(10.0, -logCeil);
            } else {
                logCeil = Math.ceil(upper);
            }
        } else if (upper > 0.0) {
            logCeil = Math.log(upper) / LOG10_VALUE;
            logCeil = Math.ceil(logCeil);
            logCeil = Math.pow(10.0, logCeil);
        } else {
            logCeil = Math.ceil(upper);
        }
        return logCeil;
    }

    public void autoAdjustRange() {
        Plot plot = this.getPlot();
        if (plot == null) {
            return;
        }
        if (plot instanceof ValueAxisPlot) {
            double upper;
            double lower;
            ValueAxisPlot vap = (ValueAxisPlot)((Object)plot);
            Range r = vap.getDataRange(this);
            if (r == null) {
                r = this.getDefaultAutoRange();
                lower = r.getLowerBound();
            } else {
                lower = r.getLowerBound();
                if (this.strictValuesFlag && !this.allowNegativesFlag && lower <= 0.0) {
                    throw new RuntimeException("Values less than or equal to zero not allowed with logarithmic axis");
                }
            }
            if (lower > 0.0) {
                double d;
                double lowerMargin = this.getLowerMargin();
                if (d > 0.0) {
                    double d2;
                    double logLower = Math.log(lower) / LOG10_VALUE;
                    double logAbs = Math.abs(logLower);
                    if (d2 < 1.0) {
                        logAbs = 1.0;
                    }
                    lower = Math.pow(10.0, logLower - logAbs * lowerMargin);
                }
            }
            if (this.autoRangeNextLogFlag) {
                lower = this.computeLogFloor(lower);
            }
            if (!this.allowNegativesFlag && lower >= 0.0 && lower < 1.0E-100) {
                lower = r.getLowerBound();
            }
            if ((upper = r.getUpperBound()) > 0.0) {
                double d;
                double upperMargin = this.getUpperMargin();
                if (d > 0.0) {
                    double d3;
                    double logUpper = Math.log(upper) / LOG10_VALUE;
                    double logAbs = Math.abs(logUpper);
                    if (d3 < 1.0) {
                        logAbs = 1.0;
                    }
                    upper = Math.pow(10.0, logUpper + logAbs * upperMargin);
                }
            }
            if (!this.allowNegativesFlag && upper < 1.0 && upper > 0.0 && lower > 0.0) {
                double expVal = Math.log(upper) / LOG10_VALUE;
                expVal = Math.ceil(-expVal + 0.001);
                upper = (expVal = Math.pow(10.0, expVal)) > 0.0 ? Math.ceil(upper * expVal) / expVal : Math.ceil(upper);
            } else {
                upper = this.autoRangeNextLogFlag ? this.computeLogCeil(upper) : Math.ceil(upper);
            }
            double minRange = this.getAutoRangeMinimumSize();
            if (upper - lower < minRange && (upper = (upper + lower + minRange) / 2.0) - (lower = (upper + lower - minRange) / 2.0) < minRange) {
                double absUpper = Math.abs(upper);
                double adjVal = absUpper > 1.0E-100 ? absUpper / 100.0 : 0.01;
                upper = (upper + lower + adjVal) / 2.0;
                lower = (upper + lower - adjVal) / 2.0;
            }
            this.setRange(new Range(lower, upper), false, false);
            this.setupSmallLogFlag();
        }
    }

    public double valueToJava2D(double value, Rectangle2D plotArea, RectangleEdge edge) {
        Range range = this.getRange();
        double axisMin = this.switchedLog10(range.getLowerBound());
        double axisMax = this.switchedLog10(range.getUpperBound());
        double min = 0.0;
        double max = 0.0;
        if (RectangleEdge.isTopOrBottom(edge)) {
            min = plotArea.getMinX();
            max = plotArea.getMaxX();
        } else if (RectangleEdge.isLeftOrRight(edge)) {
            min = plotArea.getMaxY();
            max = plotArea.getMinY();
        }
        value = this.switchedLog10(value);
        if (this.isInverted()) {
            return max - (value - axisMin) / (axisMax - axisMin) * (max - min);
        }
        return min + (value - axisMin) / (axisMax - axisMin) * (max - min);
    }

    public double java2DToValue(double java2DValue, Rectangle2D plotArea, RectangleEdge edge) {
        Range range = this.getRange();
        double axisMin = this.switchedLog10(range.getLowerBound());
        double axisMax = this.switchedLog10(range.getUpperBound());
        double plotMin = 0.0;
        double plotMax = 0.0;
        if (RectangleEdge.isTopOrBottom(edge)) {
            plotMin = plotArea.getX();
            plotMax = plotArea.getMaxX();
        } else if (RectangleEdge.isLeftOrRight(edge)) {
            plotMin = plotArea.getMaxY();
            plotMax = plotArea.getMinY();
        }
        if (this.isInverted()) {
            return this.switchedPow10(axisMax - (java2DValue - plotMin) / (plotMax - plotMin) * (axisMax - axisMin));
        }
        return this.switchedPow10(axisMin + (java2DValue - plotMin) / (plotMax - plotMin) * (axisMax - axisMin));
    }

    public void zoomRange(double lowerPercent, double upperPercent) {
        double startLog = this.switchedLog10(this.getRange().getLowerBound());
        double lengthLog = this.switchedLog10(this.getRange().getUpperBound()) - startLog;
        Range adjusted = this.isInverted() ? new Range(this.switchedPow10(startLog + lengthLog * (1.0 - upperPercent)), this.switchedPow10(startLog + lengthLog * (1.0 - lowerPercent))) : new Range(this.switchedPow10(startLog + lengthLog * lowerPercent), this.switchedPow10(startLog + lengthLog * upperPercent));
        this.setRange(adjusted);
    }

    protected List refreshTicksHorizontal(Graphics2D g2, Rectangle2D dataArea, RectangleEdge edge) {
        int iEndCount;
        ArrayList<NumberTick> ticks = new ArrayList<NumberTick>();
        Range range = this.getRange();
        double lowerBoundVal = range.getLowerBound();
        if (this.smallLogFlag && lowerBoundVal < 1.0E-100) {
            lowerBoundVal = 1.0E-100;
        }
        double upperBoundVal = range.getUpperBound();
        int iBegCount = (int)Math.rint(this.switchedLog10(lowerBoundVal));
        if (iBegCount == (iEndCount = (int)Math.rint(this.switchedLog10(upperBoundVal))) && iBegCount > 0 && Math.pow(10.0, iBegCount) > lowerBoundVal) {
            --iBegCount;
        }
        boolean zeroTickFlag = false;
        for (int i = iBegCount; i <= iEndCount; ++i) {
            for (int j = 0; j < 10; ++j) {
                String tickLabel;
                double currentTickValue;
                if (this.smallLogFlag) {
                    currentTickValue = Math.pow(10.0, i) + Math.pow(10.0, i) * (double)j;
                    if (this.expTickLabelsFlag || i < 0 && currentTickValue > 0.0 && currentTickValue < 1.0) {
                        if (j == 0 || i > -4 && j < 2 || currentTickValue >= upperBoundVal) {
                            this.numberFormatterObj.setMaximumFractionDigits(-i);
                            tickLabel = this.makeTickLabel(currentTickValue, true);
                        } else {
                            tickLabel = "";
                        }
                    } else {
                        tickLabel = j < 1 || i < 1 && j < 5 || j < 4 - i || currentTickValue >= upperBoundVal ? this.makeTickLabel(currentTickValue) : "";
                    }
                } else {
                    if (zeroTickFlag) {
                        --j;
                    }
                    double d = currentTickValue = i >= 0 ? Math.pow(10.0, i) + Math.pow(10.0, i) * (double)j : -(Math.pow(10.0, -i) - Math.pow(10.0, -i - 1) * (double)j);
                    if (!zeroTickFlag) {
                        if (Math.abs(currentTickValue - 1.0) < 1.0E-4 && lowerBoundVal <= 0.0 && upperBoundVal >= 0.0) {
                            currentTickValue = 0.0;
                            zeroTickFlag = true;
                        }
                    } else {
                        zeroTickFlag = false;
                    }
                    String string = tickLabel = this.expTickLabelsFlag && j < 2 || j < 1 || i < 1 && j < 5 || j < 4 - i || currentTickValue >= upperBoundVal ? this.makeTickLabel(currentTickValue) : "";
                }
                if (currentTickValue > upperBoundVal) {
                    return ticks;
                }
                if (!(currentTickValue >= lowerBoundVal - 1.0E-100)) continue;
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
                ticks.add(tick);
            }
        }
        return ticks;
    }

    protected List refreshTicksVertical(Graphics2D g2, Rectangle2D dataArea, RectangleEdge edge) {
        int iEndCount;
        ArrayList<NumberTick> ticks = new ArrayList<NumberTick>();
        double lowerBoundVal = this.getRange().getLowerBound();
        if (this.smallLogFlag && lowerBoundVal < 1.0E-100) {
            lowerBoundVal = 1.0E-100;
        }
        double upperBoundVal = this.getRange().getUpperBound();
        int iBegCount = (int)Math.rint(this.switchedLog10(lowerBoundVal));
        if (iBegCount == (iEndCount = (int)Math.rint(this.switchedLog10(upperBoundVal))) && iBegCount > 0 && Math.pow(10.0, iBegCount) > lowerBoundVal) {
            --iBegCount;
        }
        boolean zeroTickFlag = false;
        for (int i = iBegCount; i <= iEndCount; ++i) {
            int jEndCount = 10;
            if (i == iEndCount) {
                jEndCount = 1;
            }
            for (int j = 0; j < jEndCount; ++j) {
                NumberFormat format;
                String tickLabel;
                double tickVal;
                if (this.smallLogFlag) {
                    tickVal = Math.pow(10.0, i) + Math.pow(10.0, i) * (double)j;
                    if (j == 0) {
                        if (this.log10TickLabelsFlag) {
                            tickLabel = "10^" + i;
                        } else if (this.expTickLabelsFlag) {
                            tickLabel = "1e" + i;
                        } else if (i >= 0) {
                            format = this.getNumberFormatOverride();
                            tickLabel = format != null ? format.format(tickVal) : Long.toString((long)Math.rint(tickVal));
                        } else {
                            this.numberFormatterObj.setMaximumFractionDigits(-i);
                            tickLabel = this.numberFormatterObj.format(tickVal);
                        }
                    } else {
                        tickLabel = "";
                    }
                } else {
                    if (zeroTickFlag) {
                        --j;
                    }
                    double d = tickVal = i >= 0 ? Math.pow(10.0, i) + Math.pow(10.0, i) * (double)j : -(Math.pow(10.0, -i) - Math.pow(10.0, -i - 1) * (double)j);
                    if (j == 0) {
                        if (!zeroTickFlag) {
                            if (i > iBegCount && i < iEndCount && Math.abs(tickVal - 1.0) < 1.0E-4) {
                                tickVal = 0.0;
                                zeroTickFlag = true;
                                tickLabel = "0";
                            } else {
                                tickLabel = this.log10TickLabelsFlag ? (i < 0 ? "-" : "") + "10^" + Math.abs(i) : (this.expTickLabelsFlag ? (i < 0 ? "-" : "") + "1e" + Math.abs(i) : ((format = this.getNumberFormatOverride()) != null ? format.format(tickVal) : Long.toString((long)Math.rint(tickVal))));
                            }
                        } else {
                            tickLabel = "";
                            zeroTickFlag = false;
                        }
                    } else {
                        tickLabel = "";
                        zeroTickFlag = false;
                    }
                }
                if (tickVal > upperBoundVal) {
                    return ticks;
                }
                if (!(tickVal >= lowerBoundVal - 1.0E-100)) continue;
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
                ticks.add(new NumberTick(new Double(tickVal), tickLabel, anchor, rotationAnchor, angle));
            }
        }
        return ticks;
    }

    protected String makeTickLabel(double val, boolean forceFmtFlag) {
        if (this.expTickLabelsFlag || forceFmtFlag) {
            return this.numberFormatterObj.format(val).toLowerCase();
        }
        return this.getTickUnit().valueToString(val);
    }

    protected String makeTickLabel(double val) {
        return this.makeTickLabel(val, false);
    }
}

