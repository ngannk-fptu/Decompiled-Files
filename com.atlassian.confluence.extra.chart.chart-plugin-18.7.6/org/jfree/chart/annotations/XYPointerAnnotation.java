/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.chart.annotations;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import org.jfree.chart.HashUtilities;
import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.io.SerialUtilities;
import org.jfree.text.TextUtilities;
import org.jfree.ui.RectangleEdge;
import org.jfree.util.ObjectUtilities;
import org.jfree.util.PublicCloneable;

public class XYPointerAnnotation
extends XYTextAnnotation
implements Cloneable,
PublicCloneable,
Serializable {
    private static final long serialVersionUID = -4031161445009858551L;
    public static final double DEFAULT_TIP_RADIUS = 10.0;
    public static final double DEFAULT_BASE_RADIUS = 30.0;
    public static final double DEFAULT_LABEL_OFFSET = 3.0;
    public static final double DEFAULT_ARROW_LENGTH = 5.0;
    public static final double DEFAULT_ARROW_WIDTH = 3.0;
    private double angle;
    private double tipRadius;
    private double baseRadius;
    private double arrowLength;
    private double arrowWidth;
    private transient Stroke arrowStroke;
    private transient Paint arrowPaint;
    private double labelOffset;

    public XYPointerAnnotation(String label, double x, double y, double angle) {
        super(label, x, y);
        this.angle = angle;
        this.tipRadius = 10.0;
        this.baseRadius = 30.0;
        this.arrowLength = 5.0;
        this.arrowWidth = 3.0;
        this.labelOffset = 3.0;
        this.arrowStroke = new BasicStroke(1.0f);
        this.arrowPaint = Color.black;
    }

    public double getAngle() {
        return this.angle;
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }

    public double getTipRadius() {
        return this.tipRadius;
    }

    public void setTipRadius(double radius) {
        this.tipRadius = radius;
    }

    public double getBaseRadius() {
        return this.baseRadius;
    }

    public void setBaseRadius(double radius) {
        this.baseRadius = radius;
    }

    public double getLabelOffset() {
        return this.labelOffset;
    }

    public void setLabelOffset(double offset) {
        this.labelOffset = offset;
    }

    public double getArrowLength() {
        return this.arrowLength;
    }

    public void setArrowLength(double length) {
        this.arrowLength = length;
    }

    public double getArrowWidth() {
        return this.arrowWidth;
    }

    public void setArrowWidth(double width) {
        this.arrowWidth = width;
    }

    public Stroke getArrowStroke() {
        return this.arrowStroke;
    }

    public void setArrowStroke(Stroke stroke) {
        if (stroke == null) {
            throw new IllegalArgumentException("Null 'stroke' not permitted.");
        }
        this.arrowStroke = stroke;
    }

    public Paint getArrowPaint() {
        return this.arrowPaint;
    }

    public void setArrowPaint(Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.arrowPaint = paint;
    }

    public void draw(Graphics2D g2, XYPlot plot, Rectangle2D dataArea, ValueAxis domainAxis, ValueAxis rangeAxis, int rendererIndex, PlotRenderingInfo info) {
        PlotOrientation orientation = plot.getOrientation();
        RectangleEdge domainEdge = Plot.resolveDomainAxisLocation(plot.getDomainAxisLocation(), orientation);
        RectangleEdge rangeEdge = Plot.resolveRangeAxisLocation(plot.getRangeAxisLocation(), orientation);
        double j2DX = domainAxis.valueToJava2D(this.getX(), dataArea, domainEdge);
        double j2DY = rangeAxis.valueToJava2D(this.getY(), dataArea, rangeEdge);
        if (orientation == PlotOrientation.HORIZONTAL) {
            double temp = j2DX;
            j2DX = j2DY;
            j2DY = temp;
        }
        double startX = j2DX + Math.cos(this.angle) * this.baseRadius;
        double startY = j2DY + Math.sin(this.angle) * this.baseRadius;
        double endX = j2DX + Math.cos(this.angle) * this.tipRadius;
        double endY = j2DY + Math.sin(this.angle) * this.tipRadius;
        double arrowBaseX = endX + Math.cos(this.angle) * this.arrowLength;
        double arrowBaseY = endY + Math.sin(this.angle) * this.arrowLength;
        double arrowLeftX = arrowBaseX + Math.cos(this.angle + 1.5707963267948966) * this.arrowWidth;
        double arrowLeftY = arrowBaseY + Math.sin(this.angle + 1.5707963267948966) * this.arrowWidth;
        double arrowRightX = arrowBaseX - Math.cos(this.angle + 1.5707963267948966) * this.arrowWidth;
        double arrowRightY = arrowBaseY - Math.sin(this.angle + 1.5707963267948966) * this.arrowWidth;
        GeneralPath arrow = new GeneralPath();
        arrow.moveTo((float)endX, (float)endY);
        arrow.lineTo((float)arrowLeftX, (float)arrowLeftY);
        arrow.lineTo((float)arrowRightX, (float)arrowRightY);
        arrow.closePath();
        g2.setStroke(this.arrowStroke);
        g2.setPaint(this.arrowPaint);
        Line2D.Double line = new Line2D.Double(startX, startY, endX, endY);
        g2.draw(line);
        g2.fill(arrow);
        double labelX = j2DX + Math.cos(this.angle) * (this.baseRadius + this.labelOffset);
        double labelY = j2DY + Math.sin(this.angle) * (this.baseRadius + this.labelOffset);
        g2.setFont(this.getFont());
        Shape hotspot = TextUtilities.calculateRotatedStringBounds(this.getText(), g2, (float)labelX, (float)labelY, this.getTextAnchor(), this.getRotationAngle(), this.getRotationAnchor());
        if (this.getBackgroundPaint() != null) {
            g2.setPaint(this.getBackgroundPaint());
            g2.fill(hotspot);
        }
        g2.setPaint(this.getPaint());
        TextUtilities.drawRotatedString(this.getText(), g2, (float)labelX, (float)labelY, this.getTextAnchor(), this.getRotationAngle(), this.getRotationAnchor());
        if (this.isOutlineVisible()) {
            g2.setStroke(this.getOutlineStroke());
            g2.setPaint(this.getOutlinePaint());
            g2.draw(hotspot);
        }
        String toolTip = this.getToolTipText();
        String url = this.getURL();
        if (toolTip != null || url != null) {
            this.addEntity(info, hotspot, rendererIndex, toolTip, url);
        }
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof XYPointerAnnotation)) {
            return false;
        }
        XYPointerAnnotation that = (XYPointerAnnotation)obj;
        if (this.angle != that.angle) {
            return false;
        }
        if (this.tipRadius != that.tipRadius) {
            return false;
        }
        if (this.baseRadius != that.baseRadius) {
            return false;
        }
        if (this.arrowLength != that.arrowLength) {
            return false;
        }
        if (this.arrowWidth != that.arrowWidth) {
            return false;
        }
        if (!this.arrowPaint.equals(that.arrowPaint)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.arrowStroke, that.arrowStroke)) {
            return false;
        }
        if (this.labelOffset != that.labelOffset) {
            return false;
        }
        return super.equals(obj);
    }

    public int hashCode() {
        int result = super.hashCode();
        long temp = Double.doubleToLongBits(this.angle);
        result = 37 * result + (int)(temp ^ temp >>> 32);
        temp = Double.doubleToLongBits(this.tipRadius);
        result = 37 * result + (int)(temp ^ temp >>> 32);
        temp = Double.doubleToLongBits(this.baseRadius);
        result = 37 * result + (int)(temp ^ temp >>> 32);
        temp = Double.doubleToLongBits(this.arrowLength);
        result = 37 * result + (int)(temp ^ temp >>> 32);
        temp = Double.doubleToLongBits(this.arrowWidth);
        result = 37 * result + (int)(temp ^ temp >>> 32);
        result = result * 37 + HashUtilities.hashCodeForPaint(this.arrowPaint);
        result = result * 37 + this.arrowStroke.hashCode();
        temp = Double.doubleToLongBits(this.labelOffset);
        result = 37 * result + (int)(temp ^ temp >>> 32);
        return super.hashCode();
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writePaint(this.arrowPaint, stream);
        SerialUtilities.writeStroke(this.arrowStroke, stream);
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.arrowPaint = SerialUtilities.readPaint(stream);
        this.arrowStroke = SerialUtilities.readStroke(stream);
    }
}

