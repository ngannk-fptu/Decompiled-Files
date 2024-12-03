/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.chart.axis;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.jfree.chart.axis.AxisState;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTick;
import org.jfree.chart.axis.ValueTick;
import org.jfree.chart.event.AxisChangeEvent;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.ValueAxisPlot;
import org.jfree.data.Range;
import org.jfree.io.SerialUtilities;
import org.jfree.text.TextUtilities;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.TextAnchor;
import org.jfree.util.PaintUtilities;

public class SymbolAxis
extends NumberAxis
implements Serializable {
    private static final long serialVersionUID = 7216330468770619716L;
    public static final Paint DEFAULT_GRID_BAND_PAINT = new Color(232, 234, 232, 128);
    public static final Paint DEFAULT_GRID_BAND_ALTERNATE_PAINT = new Color(0, 0, 0, 0);
    private List symbols;
    private boolean gridBandsVisible;
    private transient Paint gridBandPaint;
    private transient Paint gridBandAlternatePaint;

    public SymbolAxis(String label, String[] sv) {
        super(label);
        this.symbols = Arrays.asList(sv);
        this.gridBandsVisible = true;
        this.gridBandPaint = DEFAULT_GRID_BAND_PAINT;
        this.gridBandAlternatePaint = DEFAULT_GRID_BAND_ALTERNATE_PAINT;
        this.setAutoTickUnitSelection(false, false);
        this.setAutoRangeStickyZero(false);
    }

    public String[] getSymbols() {
        String[] result = new String[this.symbols.size()];
        result = this.symbols.toArray(result);
        return result;
    }

    public boolean isGridBandsVisible() {
        return this.gridBandsVisible;
    }

    public void setGridBandsVisible(boolean flag) {
        if (this.gridBandsVisible != flag) {
            this.gridBandsVisible = flag;
            this.notifyListeners(new AxisChangeEvent(this));
        }
    }

    public Paint getGridBandPaint() {
        return this.gridBandPaint;
    }

    public void setGridBandPaint(Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.gridBandPaint = paint;
        this.notifyListeners(new AxisChangeEvent(this));
    }

    public Paint getGridBandAlternatePaint() {
        return this.gridBandAlternatePaint;
    }

    public void setGridBandAlternatePaint(Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.gridBandAlternatePaint = paint;
        this.notifyListeners(new AxisChangeEvent(this));
    }

    protected void selectAutoTickUnit(Graphics2D g2, Rectangle2D dataArea, RectangleEdge edge) {
        throw new UnsupportedOperationException();
    }

    public AxisState draw(Graphics2D g2, double cursor, Rectangle2D plotArea, Rectangle2D dataArea, RectangleEdge edge, PlotRenderingInfo plotState) {
        AxisState info = new AxisState(cursor);
        if (this.isVisible()) {
            info = super.draw(g2, cursor, plotArea, dataArea, edge, plotState);
        }
        if (this.gridBandsVisible) {
            this.drawGridBands(g2, plotArea, dataArea, edge, info.getTicks());
        }
        return info;
    }

    protected void drawGridBands(Graphics2D g2, Rectangle2D plotArea, Rectangle2D dataArea, RectangleEdge edge, List ticks) {
        Shape savedClip = g2.getClip();
        g2.clip(dataArea);
        if (RectangleEdge.isTopOrBottom(edge)) {
            this.drawGridBandsHorizontal(g2, plotArea, dataArea, true, ticks);
        } else if (RectangleEdge.isLeftOrRight(edge)) {
            this.drawGridBandsVertical(g2, plotArea, dataArea, true, ticks);
        }
        g2.setClip(savedClip);
    }

    protected void drawGridBandsHorizontal(Graphics2D g2, Rectangle2D plotArea, Rectangle2D dataArea, boolean firstGridBandIsDark, List ticks) {
        boolean currentGridBandIsDark = firstGridBandIsDark;
        double yy = dataArea.getY();
        double outlineStrokeWidth = this.getPlot().getOutlineStroke() != null ? (double)((BasicStroke)this.getPlot().getOutlineStroke()).getLineWidth() : 1.0;
        Iterator iterator = ticks.iterator();
        while (iterator.hasNext()) {
            ValueTick tick = (ValueTick)iterator.next();
            double xx1 = this.valueToJava2D(tick.getValue() - 0.5, dataArea, RectangleEdge.BOTTOM);
            double xx2 = this.valueToJava2D(tick.getValue() + 0.5, dataArea, RectangleEdge.BOTTOM);
            if (currentGridBandIsDark) {
                g2.setPaint(this.gridBandPaint);
            } else {
                g2.setPaint(this.gridBandAlternatePaint);
            }
            Rectangle2D.Double band = new Rectangle2D.Double(xx1, yy + outlineStrokeWidth, xx2 - xx1, dataArea.getMaxY() - yy - outlineStrokeWidth);
            g2.fill(band);
            currentGridBandIsDark = !currentGridBandIsDark;
        }
        g2.setPaintMode();
    }

    protected void drawGridBandsVertical(Graphics2D g2, Rectangle2D drawArea, Rectangle2D plotArea, boolean firstGridBandIsDark, List ticks) {
        boolean currentGridBandIsDark = firstGridBandIsDark;
        double xx = plotArea.getX();
        Stroke outlineStroke = this.getPlot().getOutlineStroke();
        double outlineStrokeWidth = outlineStroke != null && outlineStroke instanceof BasicStroke ? (double)((BasicStroke)outlineStroke).getLineWidth() : 1.0;
        Iterator iterator = ticks.iterator();
        while (iterator.hasNext()) {
            ValueTick tick = (ValueTick)iterator.next();
            double yy1 = this.valueToJava2D(tick.getValue() + 0.5, plotArea, RectangleEdge.LEFT);
            double yy2 = this.valueToJava2D(tick.getValue() - 0.5, plotArea, RectangleEdge.LEFT);
            if (currentGridBandIsDark) {
                g2.setPaint(this.gridBandPaint);
            } else {
                g2.setPaint(this.gridBandAlternatePaint);
            }
            Rectangle2D.Double band = new Rectangle2D.Double(xx + outlineStrokeWidth, yy1, plotArea.getMaxX() - xx - outlineStrokeWidth, yy2 - yy1);
            g2.fill(band);
            currentGridBandIsDark = !currentGridBandIsDark;
        }
        g2.setPaintMode();
    }

    protected void autoAdjustRange() {
        Plot plot = this.getPlot();
        if (plot == null) {
            return;
        }
        if (plot instanceof ValueAxisPlot) {
            double minRange;
            double lower;
            double upper = this.symbols.size() - 1;
            double range = upper - (lower = 0.0);
            if (range < (minRange = this.getAutoRangeMinimumSize())) {
                upper = (upper + lower + minRange) / 2.0;
                lower = (upper + lower - minRange) / 2.0;
            }
            double upperMargin = 0.5;
            double lowerMargin = 0.5;
            if (this.getAutoRangeIncludesZero()) {
                if (this.getAutoRangeStickyZero()) {
                    upper = upper <= 0.0 ? 0.0 : (upper += upperMargin);
                    lower = lower >= 0.0 ? 0.0 : (lower -= lowerMargin);
                } else {
                    upper = Math.max(0.0, upper + upperMargin);
                    lower = Math.min(0.0, lower - lowerMargin);
                }
            } else if (this.getAutoRangeStickyZero()) {
                upper = upper <= 0.0 ? Math.min(0.0, upper + upperMargin) : (upper += upperMargin * range);
                lower = lower >= 0.0 ? Math.max(0.0, lower - lowerMargin) : (lower -= lowerMargin);
            } else {
                upper += upperMargin;
                lower -= lowerMargin;
            }
            this.setRange(new Range(lower, upper), false, false);
        }
    }

    public List refreshTicks(Graphics2D g2, AxisState state, Rectangle2D dataArea, RectangleEdge edge) {
        List ticks = null;
        if (RectangleEdge.isTopOrBottom(edge)) {
            ticks = this.refreshTicksHorizontal(g2, dataArea, edge);
        } else if (RectangleEdge.isLeftOrRight(edge)) {
            ticks = this.refreshTicksVertical(g2, dataArea, edge);
        }
        return ticks;
    }

    protected List refreshTicksHorizontal(Graphics2D g2, Rectangle2D dataArea, RectangleEdge edge) {
        ArrayList<NumberTick> ticks = new ArrayList<NumberTick>();
        Font tickLabelFont = this.getTickLabelFont();
        g2.setFont(tickLabelFont);
        double size = this.getTickUnit().getSize();
        int count = this.calculateVisibleTickCount();
        double lowestTickValue = this.calculateLowestVisibleTickValue();
        double previousDrawnTickLabelPos = 0.0;
        double previousDrawnTickLabelLength = 0.0;
        if (count <= 500) {
            for (int i = 0; i < count; ++i) {
                double currentTickValue = lowestTickValue + (double)i * size;
                double xx = this.valueToJava2D(currentTickValue, dataArea, edge);
                NumberFormat formatter = this.getNumberFormatOverride();
                String tickLabel = formatter != null ? formatter.format(currentTickValue) : this.valueToString(currentTickValue);
                Rectangle2D bounds = TextUtilities.getTextBounds(tickLabel, g2, g2.getFontMetrics());
                double tickLabelLength = this.isVerticalTickLabels() ? bounds.getHeight() : bounds.getWidth();
                boolean tickLabelsOverlapping = false;
                if (i > 0) {
                    double avgTickLabelLength = (previousDrawnTickLabelLength + tickLabelLength) / 2.0;
                    if (Math.abs(xx - previousDrawnTickLabelPos) < avgTickLabelLength) {
                        tickLabelsOverlapping = true;
                    }
                }
                if (tickLabelsOverlapping) {
                    tickLabel = "";
                } else {
                    previousDrawnTickLabelPos = xx;
                    previousDrawnTickLabelLength = tickLabelLength;
                }
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
        ArrayList<NumberTick> ticks = new ArrayList<NumberTick>();
        Font tickLabelFont = this.getTickLabelFont();
        g2.setFont(tickLabelFont);
        double size = this.getTickUnit().getSize();
        int count = this.calculateVisibleTickCount();
        double lowestTickValue = this.calculateLowestVisibleTickValue();
        double previousDrawnTickLabelPos = 0.0;
        double previousDrawnTickLabelLength = 0.0;
        if (count <= 500) {
            for (int i = 0; i < count; ++i) {
                double currentTickValue = lowestTickValue + (double)i * size;
                double yy = this.valueToJava2D(currentTickValue, dataArea, edge);
                NumberFormat formatter = this.getNumberFormatOverride();
                String tickLabel = formatter != null ? formatter.format(currentTickValue) : this.valueToString(currentTickValue);
                Rectangle2D bounds = TextUtilities.getTextBounds(tickLabel, g2, g2.getFontMetrics());
                double tickLabelLength = this.isVerticalTickLabels() ? bounds.getWidth() : bounds.getHeight();
                boolean tickLabelsOverlapping = false;
                if (i > 0) {
                    double avgTickLabelLength = (previousDrawnTickLabelLength + tickLabelLength) / 2.0;
                    if (Math.abs(yy - previousDrawnTickLabelPos) < avgTickLabelLength) {
                        tickLabelsOverlapping = true;
                    }
                }
                if (tickLabelsOverlapping) {
                    tickLabel = "";
                } else {
                    previousDrawnTickLabelPos = yy;
                    previousDrawnTickLabelLength = tickLabelLength;
                }
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
                NumberTick tick = new NumberTick(new Double(currentTickValue), tickLabel, anchor, rotationAnchor, angle);
                ticks.add(tick);
            }
        }
        return ticks;
    }

    public String valueToString(double value) {
        String strToReturn;
        try {
            strToReturn = (String)this.symbols.get((int)value);
        }
        catch (IndexOutOfBoundsException ex) {
            strToReturn = "";
        }
        return strToReturn;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof SymbolAxis)) {
            return false;
        }
        SymbolAxis that = (SymbolAxis)obj;
        if (!((Object)this.symbols).equals(that.symbols)) {
            return false;
        }
        if (this.gridBandsVisible != that.gridBandsVisible) {
            return false;
        }
        if (!PaintUtilities.equal(this.gridBandPaint, that.gridBandPaint)) {
            return false;
        }
        if (!PaintUtilities.equal(this.gridBandAlternatePaint, that.gridBandAlternatePaint)) {
            return false;
        }
        return super.equals(obj);
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writePaint(this.gridBandPaint, stream);
        SerialUtilities.writePaint(this.gridBandAlternatePaint, stream);
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.gridBandPaint = SerialUtilities.readPaint(stream);
        this.gridBandAlternatePaint = SerialUtilities.readPaint(stream);
    }
}

