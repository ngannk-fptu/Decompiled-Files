/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.ext.awt.geom.ExtendedGeneralPath
 *  org.apache.batik.parser.DefaultPathHandler
 *  org.apache.batik.parser.ParseException
 *  org.apache.batik.parser.PathHandler
 *  org.apache.batik.parser.PathParser
 */
package org.apache.batik.dom.svg;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.PathIterator;
import org.apache.batik.dom.svg.AbstractSVGPathSegList;
import org.apache.batik.dom.svg.ListHandler;
import org.apache.batik.dom.svg.SVGPathSegItem;
import org.apache.batik.ext.awt.geom.ExtendedGeneralPath;
import org.apache.batik.parser.DefaultPathHandler;
import org.apache.batik.parser.ParseException;
import org.apache.batik.parser.PathHandler;
import org.apache.batik.parser.PathParser;

public abstract class AbstractSVGNormPathSegList
extends AbstractSVGPathSegList {
    protected AbstractSVGNormPathSegList() {
    }

    @Override
    protected void doParse(String value, ListHandler handler) throws ParseException {
        PathParser pathParser = new PathParser();
        NormalizedPathSegListBuilder builder = new NormalizedPathSegListBuilder(handler);
        pathParser.setPathHandler((PathHandler)builder);
        pathParser.parse(value);
    }

    protected static class SVGPathSegGenericItem
    extends SVGPathSegItem {
        public SVGPathSegGenericItem(short type, String letter, float x1, float y1, float x2, float y2, float x, float y) {
            super(type, letter);
            this.setX1(x2);
            this.setY1(y2);
            this.setX2(x2);
            this.setY2(y2);
            this.setX(x);
            this.setY(y);
        }

        public void setValue(float x1, float y1, float x2, float y2, float x, float y) {
            this.setX1(x2);
            this.setY1(y2);
            this.setX2(x2);
            this.setY2(y2);
            this.setX(x);
            this.setY(y);
        }

        public void setValue(float x, float y) {
            this.setX(x);
            this.setY(y);
        }

        public void setPathSegType(short type) {
            this.type = type;
        }
    }

    protected static class NormalizedPathSegListBuilder
    extends DefaultPathHandler {
        protected ListHandler listHandler;
        protected SVGPathSegGenericItem lastAbs;

        public NormalizedPathSegListBuilder(ListHandler listHandler) {
            this.listHandler = listHandler;
        }

        public void startPath() throws ParseException {
            this.listHandler.startList();
            this.lastAbs = new SVGPathSegGenericItem(2, "M", 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f);
        }

        public void endPath() throws ParseException {
            this.listHandler.endList();
        }

        public void movetoRel(float x, float y) throws ParseException {
            this.movetoAbs(this.lastAbs.getX() + x, this.lastAbs.getY() + y);
        }

        public void movetoAbs(float x, float y) throws ParseException {
            this.listHandler.item(new AbstractSVGPathSegList.SVGPathSegMovetoLinetoItem(2, "M", x, y));
            this.lastAbs.setX(x);
            this.lastAbs.setY(y);
            this.lastAbs.setPathSegType((short)2);
        }

        public void closePath() throws ParseException {
            this.listHandler.item(new SVGPathSegItem(1, "z"));
        }

        public void linetoRel(float x, float y) throws ParseException {
            this.linetoAbs(this.lastAbs.getX() + x, this.lastAbs.getY() + y);
        }

        public void linetoAbs(float x, float y) throws ParseException {
            this.listHandler.item(new AbstractSVGPathSegList.SVGPathSegMovetoLinetoItem(4, "L", x, y));
            this.lastAbs.setX(x);
            this.lastAbs.setY(y);
            this.lastAbs.setPathSegType((short)4);
        }

        public void linetoHorizontalRel(float x) throws ParseException {
            this.linetoAbs(this.lastAbs.getX() + x, this.lastAbs.getY());
        }

        public void linetoHorizontalAbs(float x) throws ParseException {
            this.linetoAbs(x, this.lastAbs.getY());
        }

        public void linetoVerticalRel(float y) throws ParseException {
            this.linetoAbs(this.lastAbs.getX(), this.lastAbs.getY() + y);
        }

        public void linetoVerticalAbs(float y) throws ParseException {
            this.linetoAbs(this.lastAbs.getX(), y);
        }

        public void curvetoCubicRel(float x1, float y1, float x2, float y2, float x, float y) throws ParseException {
            this.curvetoCubicAbs(this.lastAbs.getX() + x1, this.lastAbs.getY() + y1, this.lastAbs.getX() + x2, this.lastAbs.getY() + y2, this.lastAbs.getX() + x, this.lastAbs.getY() + y);
        }

        public void curvetoCubicAbs(float x1, float y1, float x2, float y2, float x, float y) throws ParseException {
            this.listHandler.item(new AbstractSVGPathSegList.SVGPathSegCurvetoCubicItem(6, "C", x1, y1, x2, y2, x, y));
            this.lastAbs.setValue(x1, y1, x2, y2, x, y);
            this.lastAbs.setPathSegType((short)6);
        }

        public void curvetoCubicSmoothRel(float x2, float y2, float x, float y) throws ParseException {
            this.curvetoCubicSmoothAbs(this.lastAbs.getX() + x2, this.lastAbs.getY() + y2, this.lastAbs.getX() + x, this.lastAbs.getY() + y);
        }

        public void curvetoCubicSmoothAbs(float x2, float y2, float x, float y) throws ParseException {
            if (this.lastAbs.getPathSegType() == 6) {
                this.curvetoCubicAbs(this.lastAbs.getX() + (this.lastAbs.getX() - this.lastAbs.getX2()), this.lastAbs.getY() + (this.lastAbs.getY() - this.lastAbs.getY2()), x2, y2, x, y);
            } else {
                this.curvetoCubicAbs(this.lastAbs.getX(), this.lastAbs.getY(), x2, y2, x, y);
            }
        }

        public void curvetoQuadraticRel(float x1, float y1, float x, float y) throws ParseException {
            this.curvetoQuadraticAbs(this.lastAbs.getX() + x1, this.lastAbs.getY() + y1, this.lastAbs.getX() + x, this.lastAbs.getY() + y);
        }

        public void curvetoQuadraticAbs(float x1, float y1, float x, float y) throws ParseException {
            this.curvetoCubicAbs(this.lastAbs.getX() + 2.0f * (x1 - this.lastAbs.getX()) / 3.0f, this.lastAbs.getY() + 2.0f * (y1 - this.lastAbs.getY()) / 3.0f, x + 2.0f * (x1 - x) / 3.0f, y + 2.0f * (y1 - y) / 3.0f, x, y);
            this.lastAbs.setX1(x1);
            this.lastAbs.setY1(y1);
            this.lastAbs.setPathSegType((short)8);
        }

        public void curvetoQuadraticSmoothRel(float x, float y) throws ParseException {
            this.curvetoQuadraticSmoothAbs(this.lastAbs.getX() + x, this.lastAbs.getY() + y);
        }

        public void curvetoQuadraticSmoothAbs(float x, float y) throws ParseException {
            if (this.lastAbs.getPathSegType() == 8) {
                this.curvetoQuadraticAbs(this.lastAbs.getX() + (this.lastAbs.getX() - this.lastAbs.getX1()), this.lastAbs.getY() + (this.lastAbs.getY() - this.lastAbs.getY1()), x, y);
            } else {
                this.curvetoQuadraticAbs(this.lastAbs.getX(), this.lastAbs.getY(), x, y);
            }
        }

        public void arcRel(float rx, float ry, float xAxisRotation, boolean largeArcFlag, boolean sweepFlag, float x, float y) throws ParseException {
            this.arcAbs(rx, ry, xAxisRotation, largeArcFlag, sweepFlag, this.lastAbs.getX() + x, this.lastAbs.getY() + y);
        }

        public void arcAbs(float rx, float ry, float xAxisRotation, boolean largeArcFlag, boolean sweepFlag, float x, float y) throws ParseException {
            if (rx == 0.0f || ry == 0.0f) {
                this.linetoAbs(x, y);
                return;
            }
            double x0 = this.lastAbs.getX();
            double y0 = this.lastAbs.getY();
            if (x0 == (double)x && y0 == (double)y) {
                return;
            }
            Arc2D arc = ExtendedGeneralPath.computeArc((double)x0, (double)y0, (double)rx, (double)ry, (double)xAxisRotation, (boolean)largeArcFlag, (boolean)sweepFlag, (double)x, (double)y);
            if (arc == null) {
                return;
            }
            AffineTransform t = AffineTransform.getRotateInstance(Math.toRadians(xAxisRotation), arc.getCenterX(), arc.getCenterY());
            Shape s = t.createTransformedShape(arc);
            PathIterator pi = s.getPathIterator(new AffineTransform());
            float[] d = new float[]{0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f};
            int i = -1;
            while (!pi.isDone()) {
                i = pi.currentSegment(d);
                switch (i) {
                    case 3: {
                        this.curvetoCubicAbs(d[0], d[1], d[2], d[3], d[4], d[5]);
                    }
                }
                pi.next();
            }
            this.lastAbs.setPathSegType((short)10);
        }
    }
}

