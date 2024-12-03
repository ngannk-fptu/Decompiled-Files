/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.ext.awt.geom.ExtendedGeneralPath
 */
package org.apache.batik.parser;

import java.awt.Shape;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.Reader;
import org.apache.batik.ext.awt.geom.ExtendedGeneralPath;
import org.apache.batik.parser.ParseException;
import org.apache.batik.parser.PathHandler;
import org.apache.batik.parser.PathParser;
import org.apache.batik.parser.ShapeProducer;

public class AWTPathProducer
implements PathHandler,
ShapeProducer {
    protected ExtendedGeneralPath path;
    protected float currentX;
    protected float currentY;
    protected float xCenter;
    protected float yCenter;
    protected int windingRule;

    public static Shape createShape(Reader r, int wr) throws IOException, ParseException {
        PathParser p = new PathParser();
        AWTPathProducer ph = new AWTPathProducer();
        ph.setWindingRule(wr);
        p.setPathHandler(ph);
        p.parse(r);
        return ph.getShape();
    }

    @Override
    public void setWindingRule(int i) {
        this.windingRule = i;
    }

    @Override
    public int getWindingRule() {
        return this.windingRule;
    }

    @Override
    public Shape getShape() {
        return this.path;
    }

    @Override
    public void startPath() throws ParseException {
        this.currentX = 0.0f;
        this.currentY = 0.0f;
        this.xCenter = 0.0f;
        this.yCenter = 0.0f;
        this.path = new ExtendedGeneralPath(this.windingRule);
    }

    @Override
    public void endPath() throws ParseException {
    }

    @Override
    public void movetoRel(float x, float y) throws ParseException {
        this.xCenter = this.currentX += x;
        this.yCenter = this.currentY += y;
        this.path.moveTo(this.currentX, this.currentY);
    }

    @Override
    public void movetoAbs(float x, float y) throws ParseException {
        this.xCenter = this.currentX = x;
        this.yCenter = this.currentY = y;
        this.path.moveTo(this.currentX, this.currentY);
    }

    @Override
    public void closePath() throws ParseException {
        this.path.closePath();
        Point2D pt = this.path.getCurrentPoint();
        this.currentX = (float)pt.getX();
        this.currentY = (float)pt.getY();
    }

    @Override
    public void linetoRel(float x, float y) throws ParseException {
        this.xCenter = this.currentX += x;
        this.yCenter = this.currentY += y;
        this.path.lineTo(this.currentX, this.currentY);
    }

    @Override
    public void linetoAbs(float x, float y) throws ParseException {
        this.xCenter = this.currentX = x;
        this.yCenter = this.currentY = y;
        this.path.lineTo(this.currentX, this.currentY);
    }

    @Override
    public void linetoHorizontalRel(float x) throws ParseException {
        this.xCenter = this.currentX += x;
        this.yCenter = this.currentY;
        this.path.lineTo(this.currentX, this.yCenter);
    }

    @Override
    public void linetoHorizontalAbs(float x) throws ParseException {
        this.xCenter = this.currentX = x;
        this.yCenter = this.currentY;
        this.path.lineTo(this.currentX, this.yCenter);
    }

    @Override
    public void linetoVerticalRel(float y) throws ParseException {
        this.xCenter = this.currentX;
        this.yCenter = this.currentY += y;
        this.path.lineTo(this.xCenter, this.currentY);
    }

    @Override
    public void linetoVerticalAbs(float y) throws ParseException {
        this.xCenter = this.currentX;
        this.yCenter = this.currentY = y;
        this.path.lineTo(this.xCenter, this.currentY);
    }

    @Override
    public void curvetoCubicRel(float x1, float y1, float x2, float y2, float x, float y) throws ParseException {
        this.xCenter = this.currentX + x2;
        this.yCenter = this.currentY + y2;
        this.path.curveTo(this.currentX + x1, this.currentY + y1, this.xCenter, this.yCenter, this.currentX += x, this.currentY += y);
    }

    @Override
    public void curvetoCubicAbs(float x1, float y1, float x2, float y2, float x, float y) throws ParseException {
        this.xCenter = x2;
        this.yCenter = y2;
        this.currentX = x;
        this.currentY = y;
        this.path.curveTo(x1, y1, this.xCenter, this.yCenter, this.currentX, this.currentY);
    }

    @Override
    public void curvetoCubicSmoothRel(float x2, float y2, float x, float y) throws ParseException {
        this.xCenter = this.currentX + x2;
        this.yCenter = this.currentY + y2;
        this.path.curveTo(this.currentX * 2.0f - this.xCenter, this.currentY * 2.0f - this.yCenter, this.xCenter, this.yCenter, this.currentX += x, this.currentY += y);
    }

    @Override
    public void curvetoCubicSmoothAbs(float x2, float y2, float x, float y) throws ParseException {
        this.xCenter = x2;
        this.yCenter = y2;
        this.currentX = x;
        this.currentY = y;
        this.path.curveTo(this.currentX * 2.0f - this.xCenter, this.currentY * 2.0f - this.yCenter, this.xCenter, this.yCenter, this.currentX, this.currentY);
    }

    @Override
    public void curvetoQuadraticRel(float x1, float y1, float x, float y) throws ParseException {
        this.xCenter = this.currentX + x1;
        this.yCenter = this.currentY + y1;
        this.path.quadTo(this.xCenter, this.yCenter, this.currentX += x, this.currentY += y);
    }

    @Override
    public void curvetoQuadraticAbs(float x1, float y1, float x, float y) throws ParseException {
        this.xCenter = x1;
        this.yCenter = y1;
        this.currentX = x;
        this.currentY = y;
        this.path.quadTo(this.xCenter, this.yCenter, this.currentX, this.currentY);
    }

    @Override
    public void curvetoQuadraticSmoothRel(float x, float y) throws ParseException {
        this.xCenter = this.currentX * 2.0f - this.xCenter;
        this.yCenter = this.currentY * 2.0f - this.yCenter;
        this.path.quadTo(this.xCenter, this.yCenter, this.currentX += x, this.currentY += y);
    }

    @Override
    public void curvetoQuadraticSmoothAbs(float x, float y) throws ParseException {
        this.xCenter = this.currentX * 2.0f - this.xCenter;
        this.yCenter = this.currentY * 2.0f - this.yCenter;
        this.currentX = x;
        this.currentY = y;
        this.path.quadTo(this.xCenter, this.yCenter, this.currentX, this.currentY);
    }

    @Override
    public void arcRel(float rx, float ry, float xAxisRotation, boolean largeArcFlag, boolean sweepFlag, float x, float y) throws ParseException {
        this.xCenter = this.currentX += x;
        this.yCenter = this.currentY += y;
        this.path.arcTo(rx, ry, xAxisRotation, largeArcFlag, sweepFlag, this.currentX, this.currentY);
    }

    @Override
    public void arcAbs(float rx, float ry, float xAxisRotation, boolean largeArcFlag, boolean sweepFlag, float x, float y) throws ParseException {
        this.xCenter = this.currentX = x;
        this.yCenter = this.currentY = y;
        this.path.arcTo(rx, ry, xAxisRotation, largeArcFlag, sweepFlag, this.currentX, this.currentY);
    }
}

