/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.chart.axis;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.font.LineMetrics;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import org.jfree.chart.axis.Axis;
import org.jfree.chart.axis.AxisSpace;
import org.jfree.chart.axis.AxisState;
import org.jfree.chart.axis.Tick;
import org.jfree.chart.axis.TickType;
import org.jfree.chart.axis.TickUnitSource;
import org.jfree.chart.axis.ValueTick;
import org.jfree.chart.event.AxisChangeEvent;
import org.jfree.chart.plot.Plot;
import org.jfree.data.Range;
import org.jfree.io.SerialUtilities;
import org.jfree.text.TextUtilities;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.jfree.util.ObjectUtilities;
import org.jfree.util.PublicCloneable;

public abstract class ValueAxis
extends Axis
implements Cloneable,
PublicCloneable,
Serializable {
    private static final long serialVersionUID = 3698345477322391456L;
    public static final Range DEFAULT_RANGE = new Range(0.0, 1.0);
    public static final boolean DEFAULT_AUTO_RANGE = true;
    public static final boolean DEFAULT_INVERTED = false;
    public static final double DEFAULT_AUTO_RANGE_MINIMUM_SIZE = 1.0E-8;
    public static final double DEFAULT_LOWER_MARGIN = 0.05;
    public static final double DEFAULT_UPPER_MARGIN = 0.05;
    public static final double DEFAULT_LOWER_BOUND = 0.0;
    public static final double DEFAULT_UPPER_BOUND = 1.0;
    public static final boolean DEFAULT_AUTO_TICK_UNIT_SELECTION = true;
    public static final int MAXIMUM_TICK_COUNT = 500;
    private boolean positiveArrowVisible = false;
    private boolean negativeArrowVisible = false;
    private transient Shape upArrow;
    private transient Shape downArrow;
    private transient Shape leftArrow;
    private transient Shape rightArrow;
    private boolean inverted = false;
    private Range range = DEFAULT_RANGE;
    private boolean autoRange = true;
    private double autoRangeMinimumSize = 1.0E-8;
    private Range defaultAutoRange = DEFAULT_RANGE;
    private double upperMargin = 0.05;
    private double lowerMargin = 0.05;
    private double fixedAutoRange = 0.0;
    private boolean autoTickUnitSelection = true;
    private TickUnitSource standardTickUnits;
    private int autoTickIndex;
    private int minorTickCount;
    private boolean verticalTickLabels;

    protected ValueAxis(String label, TickUnitSource standardTickUnits) {
        super(label);
        this.standardTickUnits = standardTickUnits;
        Polygon p1 = new Polygon();
        p1.addPoint(0, 0);
        p1.addPoint(-2, 2);
        p1.addPoint(2, 2);
        this.upArrow = p1;
        Polygon p2 = new Polygon();
        p2.addPoint(0, 0);
        p2.addPoint(-2, -2);
        p2.addPoint(2, -2);
        this.downArrow = p2;
        Polygon p3 = new Polygon();
        p3.addPoint(0, 0);
        p3.addPoint(-2, -2);
        p3.addPoint(-2, 2);
        this.rightArrow = p3;
        Polygon p4 = new Polygon();
        p4.addPoint(0, 0);
        p4.addPoint(2, -2);
        p4.addPoint(2, 2);
        this.leftArrow = p4;
        this.verticalTickLabels = false;
        this.minorTickCount = 0;
    }

    public boolean isVerticalTickLabels() {
        return this.verticalTickLabels;
    }

    public void setVerticalTickLabels(boolean flag) {
        if (this.verticalTickLabels != flag) {
            this.verticalTickLabels = flag;
            this.notifyListeners(new AxisChangeEvent(this));
        }
    }

    public boolean isPositiveArrowVisible() {
        return this.positiveArrowVisible;
    }

    public void setPositiveArrowVisible(boolean visible) {
        this.positiveArrowVisible = visible;
        this.notifyListeners(new AxisChangeEvent(this));
    }

    public boolean isNegativeArrowVisible() {
        return this.negativeArrowVisible;
    }

    public void setNegativeArrowVisible(boolean visible) {
        this.negativeArrowVisible = visible;
        this.notifyListeners(new AxisChangeEvent(this));
    }

    public Shape getUpArrow() {
        return this.upArrow;
    }

    public void setUpArrow(Shape arrow) {
        if (arrow == null) {
            throw new IllegalArgumentException("Null 'arrow' argument.");
        }
        this.upArrow = arrow;
        this.notifyListeners(new AxisChangeEvent(this));
    }

    public Shape getDownArrow() {
        return this.downArrow;
    }

    public void setDownArrow(Shape arrow) {
        if (arrow == null) {
            throw new IllegalArgumentException("Null 'arrow' argument.");
        }
        this.downArrow = arrow;
        this.notifyListeners(new AxisChangeEvent(this));
    }

    public Shape getLeftArrow() {
        return this.leftArrow;
    }

    public void setLeftArrow(Shape arrow) {
        if (arrow == null) {
            throw new IllegalArgumentException("Null 'arrow' argument.");
        }
        this.leftArrow = arrow;
        this.notifyListeners(new AxisChangeEvent(this));
    }

    public Shape getRightArrow() {
        return this.rightArrow;
    }

    public void setRightArrow(Shape arrow) {
        if (arrow == null) {
            throw new IllegalArgumentException("Null 'arrow' argument.");
        }
        this.rightArrow = arrow;
        this.notifyListeners(new AxisChangeEvent(this));
    }

    protected void drawAxisLine(Graphics2D g2, double cursor, Rectangle2D dataArea, RectangleEdge edge) {
        Shape shape;
        AffineTransform transformer;
        Shape arrow;
        double y;
        double x;
        Line2D.Double axisLine = null;
        if (edge == RectangleEdge.TOP) {
            axisLine = new Line2D.Double(dataArea.getX(), cursor, dataArea.getMaxX(), cursor);
        } else if (edge == RectangleEdge.BOTTOM) {
            axisLine = new Line2D.Double(dataArea.getX(), cursor, dataArea.getMaxX(), cursor);
        } else if (edge == RectangleEdge.LEFT) {
            axisLine = new Line2D.Double(cursor, dataArea.getY(), cursor, dataArea.getMaxY());
        } else if (edge == RectangleEdge.RIGHT) {
            axisLine = new Line2D.Double(cursor, dataArea.getY(), cursor, dataArea.getMaxY());
        }
        g2.setPaint(this.getAxisLinePaint());
        g2.setStroke(this.getAxisLineStroke());
        g2.draw(axisLine);
        boolean drawUpOrRight = false;
        boolean drawDownOrLeft = false;
        if (this.positiveArrowVisible) {
            if (this.inverted) {
                drawDownOrLeft = true;
            } else {
                drawUpOrRight = true;
            }
        }
        if (this.negativeArrowVisible) {
            if (this.inverted) {
                drawUpOrRight = true;
            } else {
                drawDownOrLeft = true;
            }
        }
        if (drawUpOrRight) {
            x = 0.0;
            y = 0.0;
            arrow = null;
            if (edge == RectangleEdge.TOP || edge == RectangleEdge.BOTTOM) {
                x = dataArea.getMaxX();
                y = cursor;
                arrow = this.rightArrow;
            } else if (edge == RectangleEdge.LEFT || edge == RectangleEdge.RIGHT) {
                x = cursor;
                y = dataArea.getMinY();
                arrow = this.upArrow;
            }
            transformer = new AffineTransform();
            transformer.setToTranslation(x, y);
            shape = transformer.createTransformedShape(arrow);
            g2.fill(shape);
            g2.draw(shape);
        }
        if (drawDownOrLeft) {
            x = 0.0;
            y = 0.0;
            arrow = null;
            if (edge == RectangleEdge.TOP || edge == RectangleEdge.BOTTOM) {
                x = dataArea.getMinX();
                y = cursor;
                arrow = this.leftArrow;
            } else if (edge == RectangleEdge.LEFT || edge == RectangleEdge.RIGHT) {
                x = cursor;
                y = dataArea.getMaxY();
                arrow = this.downArrow;
            }
            transformer = new AffineTransform();
            transformer.setToTranslation(x, y);
            shape = transformer.createTransformedShape(arrow);
            g2.fill(shape);
            g2.draw(shape);
        }
    }

    protected float[] calculateAnchorPoint(ValueTick tick, double cursor, Rectangle2D dataArea, RectangleEdge edge) {
        RectangleInsets insets = this.getTickLabelInsets();
        float[] result = new float[2];
        if (edge == RectangleEdge.TOP) {
            result[0] = (float)this.valueToJava2D(tick.getValue(), dataArea, edge);
            result[1] = (float)(cursor - insets.getBottom() - 2.0);
        } else if (edge == RectangleEdge.BOTTOM) {
            result[0] = (float)this.valueToJava2D(tick.getValue(), dataArea, edge);
            result[1] = (float)(cursor + insets.getTop() + 2.0);
        } else if (edge == RectangleEdge.LEFT) {
            result[0] = (float)(cursor - insets.getLeft() - 2.0);
            result[1] = (float)this.valueToJava2D(tick.getValue(), dataArea, edge);
        } else if (edge == RectangleEdge.RIGHT) {
            result[0] = (float)(cursor + insets.getRight() + 2.0);
            result[1] = (float)this.valueToJava2D(tick.getValue(), dataArea, edge);
        }
        return result;
    }

    protected AxisState drawTickMarksAndLabels(Graphics2D g2, double cursor, Rectangle2D plotArea, Rectangle2D dataArea, RectangleEdge edge) {
        AxisState state = new AxisState(cursor);
        if (this.isAxisLineVisible()) {
            this.drawAxisLine(g2, cursor, dataArea, edge);
        }
        List ticks = this.refreshTicks(g2, state, dataArea, edge);
        state.setTicks(ticks);
        g2.setFont(this.getTickLabelFont());
        Iterator iterator = ticks.iterator();
        while (iterator.hasNext()) {
            ValueTick tick = (ValueTick)iterator.next();
            if (this.isTickLabelsVisible()) {
                g2.setPaint(this.getTickLabelPaint());
                float[] anchorPoint = this.calculateAnchorPoint(tick, cursor, dataArea, edge);
                TextUtilities.drawRotatedString(tick.getText(), g2, anchorPoint[0], anchorPoint[1], tick.getTextAnchor(), tick.getAngle(), tick.getRotationAnchor());
            }
            if ((!this.isTickMarksVisible() || !tick.getTickType().equals(TickType.MAJOR)) && (!this.isMinorTickMarksVisible() || !tick.getTickType().equals(TickType.MINOR))) continue;
            double ol = tick.getTickType().equals(TickType.MINOR) ? (double)this.getMinorTickMarkOutsideLength() : (double)this.getTickMarkOutsideLength();
            double il = tick.getTickType().equals(TickType.MINOR) ? (double)this.getMinorTickMarkInsideLength() : (double)this.getTickMarkInsideLength();
            float xx = (float)this.valueToJava2D(tick.getValue(), dataArea, edge);
            Line2D.Double mark = null;
            g2.setStroke(this.getTickMarkStroke());
            g2.setPaint(this.getTickMarkPaint());
            if (edge == RectangleEdge.LEFT) {
                mark = new Line2D.Double(cursor - ol, xx, cursor + il, xx);
            } else if (edge == RectangleEdge.RIGHT) {
                mark = new Line2D.Double(cursor + ol, xx, cursor - il, xx);
            } else if (edge == RectangleEdge.TOP) {
                mark = new Line2D.Double(xx, cursor - ol, xx, cursor + il);
            } else if (edge == RectangleEdge.BOTTOM) {
                mark = new Line2D.Double(xx, cursor + ol, xx, cursor - il);
            }
            g2.draw(mark);
        }
        double used = 0.0;
        if (this.isTickLabelsVisible()) {
            if (edge == RectangleEdge.LEFT) {
                state.cursorLeft(used += this.findMaximumTickLabelWidth(ticks, g2, plotArea, this.isVerticalTickLabels()));
            } else if (edge == RectangleEdge.RIGHT) {
                used = this.findMaximumTickLabelWidth(ticks, g2, plotArea, this.isVerticalTickLabels());
                state.cursorRight(used);
            } else if (edge == RectangleEdge.TOP) {
                used = this.findMaximumTickLabelHeight(ticks, g2, plotArea, this.isVerticalTickLabels());
                state.cursorUp(used);
            } else if (edge == RectangleEdge.BOTTOM) {
                used = this.findMaximumTickLabelHeight(ticks, g2, plotArea, this.isVerticalTickLabels());
                state.cursorDown(used);
            }
        }
        return state;
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
        double tickLabelHeight = 0.0;
        double tickLabelWidth = 0.0;
        if (this.isTickLabelsVisible()) {
            g2.setFont(this.getTickLabelFont());
            List ticks = this.refreshTicks(g2, new AxisState(), plotArea, edge);
            if (RectangleEdge.isTopOrBottom(edge)) {
                tickLabelHeight = this.findMaximumTickLabelHeight(ticks, g2, plotArea, this.isVerticalTickLabels());
            } else if (RectangleEdge.isLeftOrRight(edge)) {
                tickLabelWidth = this.findMaximumTickLabelWidth(ticks, g2, plotArea, this.isVerticalTickLabels());
            }
        }
        Rectangle2D labelEnclosure = this.getLabelEnclosure(g2, edge);
        double labelHeight = 0.0;
        double labelWidth = 0.0;
        if (RectangleEdge.isTopOrBottom(edge)) {
            labelHeight = labelEnclosure.getHeight();
            space.add(labelHeight + tickLabelHeight, edge);
        } else if (RectangleEdge.isLeftOrRight(edge)) {
            labelWidth = labelEnclosure.getWidth();
            space.add(labelWidth + tickLabelWidth, edge);
        }
        return space;
    }

    protected double findMaximumTickLabelHeight(List ticks, Graphics2D g2, Rectangle2D drawArea, boolean vertical) {
        RectangleInsets insets = this.getTickLabelInsets();
        Font font = this.getTickLabelFont();
        double maxHeight = 0.0;
        if (vertical) {
            FontMetrics fm = g2.getFontMetrics(font);
            Iterator iterator = ticks.iterator();
            while (iterator.hasNext()) {
                Tick tick = (Tick)iterator.next();
                Rectangle2D labelBounds = TextUtilities.getTextBounds(tick.getText(), g2, fm);
                if (!(labelBounds.getWidth() + insets.getTop() + insets.getBottom() > maxHeight)) continue;
                maxHeight = labelBounds.getWidth() + insets.getTop() + insets.getBottom();
            }
        } else {
            LineMetrics metrics = font.getLineMetrics("ABCxyz", g2.getFontRenderContext());
            maxHeight = (double)metrics.getHeight() + insets.getTop() + insets.getBottom();
        }
        return maxHeight;
    }

    protected double findMaximumTickLabelWidth(List ticks, Graphics2D g2, Rectangle2D drawArea, boolean vertical) {
        RectangleInsets insets = this.getTickLabelInsets();
        Font font = this.getTickLabelFont();
        double maxWidth = 0.0;
        if (!vertical) {
            FontMetrics fm = g2.getFontMetrics(font);
            Iterator iterator = ticks.iterator();
            while (iterator.hasNext()) {
                Tick tick = (Tick)iterator.next();
                Rectangle2D labelBounds = TextUtilities.getTextBounds(tick.getText(), g2, fm);
                if (!(labelBounds.getWidth() + insets.getLeft() + insets.getRight() > maxWidth)) continue;
                maxWidth = labelBounds.getWidth() + insets.getLeft() + insets.getRight();
            }
        } else {
            LineMetrics metrics = font.getLineMetrics("ABCxyz", g2.getFontRenderContext());
            maxWidth = (double)metrics.getHeight() + insets.getTop() + insets.getBottom();
        }
        return maxWidth;
    }

    public boolean isInverted() {
        return this.inverted;
    }

    public void setInverted(boolean flag) {
        if (this.inverted != flag) {
            this.inverted = flag;
            this.notifyListeners(new AxisChangeEvent(this));
        }
    }

    public boolean isAutoRange() {
        return this.autoRange;
    }

    public void setAutoRange(boolean auto) {
        this.setAutoRange(auto, true);
    }

    protected void setAutoRange(boolean auto, boolean notify) {
        if (this.autoRange != auto) {
            this.autoRange = auto;
            if (this.autoRange) {
                this.autoAdjustRange();
            }
            if (notify) {
                this.notifyListeners(new AxisChangeEvent(this));
            }
        }
    }

    public double getAutoRangeMinimumSize() {
        return this.autoRangeMinimumSize;
    }

    public void setAutoRangeMinimumSize(double size) {
        this.setAutoRangeMinimumSize(size, true);
    }

    public void setAutoRangeMinimumSize(double size, boolean notify) {
        if (size <= 0.0) {
            throw new IllegalArgumentException("NumberAxis.setAutoRangeMinimumSize(double): must be > 0.0.");
        }
        if (this.autoRangeMinimumSize != size) {
            this.autoRangeMinimumSize = size;
            if (this.autoRange) {
                this.autoAdjustRange();
            }
            if (notify) {
                this.notifyListeners(new AxisChangeEvent(this));
            }
        }
    }

    public Range getDefaultAutoRange() {
        return this.defaultAutoRange;
    }

    public void setDefaultAutoRange(Range range) {
        if (range == null) {
            throw new IllegalArgumentException("Null 'range' argument.");
        }
        this.defaultAutoRange = range;
        this.notifyListeners(new AxisChangeEvent(this));
    }

    public double getLowerMargin() {
        return this.lowerMargin;
    }

    public void setLowerMargin(double margin) {
        this.lowerMargin = margin;
        if (this.isAutoRange()) {
            this.autoAdjustRange();
        }
        this.notifyListeners(new AxisChangeEvent(this));
    }

    public double getUpperMargin() {
        return this.upperMargin;
    }

    public void setUpperMargin(double margin) {
        this.upperMargin = margin;
        if (this.isAutoRange()) {
            this.autoAdjustRange();
        }
        this.notifyListeners(new AxisChangeEvent(this));
    }

    public double getFixedAutoRange() {
        return this.fixedAutoRange;
    }

    public void setFixedAutoRange(double length) {
        this.fixedAutoRange = length;
        if (this.isAutoRange()) {
            this.autoAdjustRange();
        }
        this.notifyListeners(new AxisChangeEvent(this));
    }

    public double getLowerBound() {
        return this.range.getLowerBound();
    }

    public void setLowerBound(double min) {
        if (this.range.getUpperBound() > min) {
            this.setRange(new Range(min, this.range.getUpperBound()));
        } else {
            this.setRange(new Range(min, min + 1.0));
        }
    }

    public double getUpperBound() {
        return this.range.getUpperBound();
    }

    public void setUpperBound(double max) {
        if (this.range.getLowerBound() < max) {
            this.setRange(new Range(this.range.getLowerBound(), max));
        } else {
            this.setRange(max - 1.0, max);
        }
    }

    public Range getRange() {
        return this.range;
    }

    public void setRange(Range range) {
        this.setRange(range, true, true);
    }

    public void setRange(Range range, boolean turnOffAutoRange, boolean notify) {
        if (range == null) {
            throw new IllegalArgumentException("Null 'range' argument.");
        }
        if (turnOffAutoRange) {
            this.autoRange = false;
        }
        this.range = range;
        if (notify) {
            this.notifyListeners(new AxisChangeEvent(this));
        }
    }

    public void setRange(double lower, double upper) {
        this.setRange(new Range(lower, upper));
    }

    public void setRangeWithMargins(Range range) {
        this.setRangeWithMargins(range, true, true);
    }

    public void setRangeWithMargins(Range range, boolean turnOffAutoRange, boolean notify) {
        if (range == null) {
            throw new IllegalArgumentException("Null 'range' argument.");
        }
        this.setRange(Range.expand(range, this.getLowerMargin(), this.getUpperMargin()), turnOffAutoRange, notify);
    }

    public void setRangeWithMargins(double lower, double upper) {
        this.setRangeWithMargins(new Range(lower, upper));
    }

    public void setRangeAboutValue(double value, double length) {
        this.setRange(new Range(value - length / 2.0, value + length / 2.0));
    }

    public boolean isAutoTickUnitSelection() {
        return this.autoTickUnitSelection;
    }

    public void setAutoTickUnitSelection(boolean flag) {
        this.setAutoTickUnitSelection(flag, true);
    }

    public void setAutoTickUnitSelection(boolean flag, boolean notify) {
        if (this.autoTickUnitSelection != flag) {
            this.autoTickUnitSelection = flag;
            if (notify) {
                this.notifyListeners(new AxisChangeEvent(this));
            }
        }
    }

    public TickUnitSource getStandardTickUnits() {
        return this.standardTickUnits;
    }

    public void setStandardTickUnits(TickUnitSource source) {
        this.standardTickUnits = source;
        this.notifyListeners(new AxisChangeEvent(this));
    }

    public int getMinorTickCount() {
        return this.minorTickCount;
    }

    public void setMinorTickCount(int count) {
        this.minorTickCount = count;
        this.notifyListeners(new AxisChangeEvent(this));
    }

    public abstract double valueToJava2D(double var1, Rectangle2D var3, RectangleEdge var4);

    public double lengthToJava2D(double length, Rectangle2D area, RectangleEdge edge) {
        double zero = this.valueToJava2D(0.0, area, edge);
        double l = this.valueToJava2D(length, area, edge);
        return Math.abs(l - zero);
    }

    public abstract double java2DToValue(double var1, Rectangle2D var3, RectangleEdge var4);

    protected abstract void autoAdjustRange();

    public void centerRange(double value) {
        double central = this.range.getCentralValue();
        Range adjusted = new Range(this.range.getLowerBound() + value - central, this.range.getUpperBound() + value - central);
        this.setRange(adjusted);
    }

    public void resizeRange(double percent) {
        this.resizeRange(percent, this.range.getCentralValue());
    }

    public void resizeRange(double percent, double anchorValue) {
        if (percent > 0.0) {
            double halfLength = this.range.getLength() * percent / 2.0;
            Range adjusted = new Range(anchorValue - halfLength, anchorValue + halfLength);
            this.setRange(adjusted);
        } else {
            this.setAutoRange(true);
        }
    }

    public void resizeRange2(double percent, double anchorValue) {
        if (percent > 0.0) {
            double left = anchorValue - this.getLowerBound();
            double right = this.getUpperBound() - anchorValue;
            Range adjusted = new Range(anchorValue - left * percent, anchorValue + right * percent);
            this.setRange(adjusted);
        } else {
            this.setAutoRange(true);
        }
    }

    public void zoomRange(double lowerPercent, double upperPercent) {
        double start = this.range.getLowerBound();
        double length = this.range.getLength();
        Range adjusted = null;
        adjusted = this.isInverted() ? new Range(start + length * (1.0 - upperPercent), start + length * (1.0 - lowerPercent)) : new Range(start + length * lowerPercent, start + length * upperPercent);
        this.setRange(adjusted);
    }

    public void pan(double percent) {
        Range range = this.getRange();
        double length = range.getLength();
        double adj = length * percent;
        double lower = range.getLowerBound() + adj;
        double upper = range.getUpperBound() + adj;
        this.setRange(lower, upper);
    }

    protected int getAutoTickIndex() {
        return this.autoTickIndex;
    }

    protected void setAutoTickIndex(int index) {
        this.autoTickIndex = index;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof ValueAxis)) {
            return false;
        }
        ValueAxis that = (ValueAxis)obj;
        if (this.positiveArrowVisible != that.positiveArrowVisible) {
            return false;
        }
        if (this.negativeArrowVisible != that.negativeArrowVisible) {
            return false;
        }
        if (this.inverted != that.inverted) {
            return false;
        }
        if (!this.autoRange && !ObjectUtilities.equal(this.range, that.range)) {
            return false;
        }
        if (this.autoRange != that.autoRange) {
            return false;
        }
        if (this.autoRangeMinimumSize != that.autoRangeMinimumSize) {
            return false;
        }
        if (!this.defaultAutoRange.equals(that.defaultAutoRange)) {
            return false;
        }
        if (this.upperMargin != that.upperMargin) {
            return false;
        }
        if (this.lowerMargin != that.lowerMargin) {
            return false;
        }
        if (this.fixedAutoRange != that.fixedAutoRange) {
            return false;
        }
        if (this.autoTickUnitSelection != that.autoTickUnitSelection) {
            return false;
        }
        if (!ObjectUtilities.equal(this.standardTickUnits, that.standardTickUnits)) {
            return false;
        }
        if (this.verticalTickLabels != that.verticalTickLabels) {
            return false;
        }
        if (this.minorTickCount != that.minorTickCount) {
            return false;
        }
        return super.equals(obj);
    }

    public Object clone() throws CloneNotSupportedException {
        ValueAxis clone = (ValueAxis)super.clone();
        return clone;
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writeShape(this.upArrow, stream);
        SerialUtilities.writeShape(this.downArrow, stream);
        SerialUtilities.writeShape(this.leftArrow, stream);
        SerialUtilities.writeShape(this.rightArrow, stream);
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.upArrow = SerialUtilities.readShape(stream);
        this.downArrow = SerialUtilities.readShape(stream);
        this.leftArrow = SerialUtilities.readShape(stream);
        this.rightArrow = SerialUtilities.readShape(stream);
    }
}

