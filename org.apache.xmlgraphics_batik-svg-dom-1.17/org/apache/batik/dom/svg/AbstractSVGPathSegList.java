/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.parser.DefaultPathHandler
 *  org.apache.batik.parser.ParseException
 *  org.apache.batik.parser.PathHandler
 *  org.apache.batik.parser.PathParser
 *  org.w3c.dom.svg.SVGException
 *  org.w3c.dom.svg.SVGPathSeg
 *  org.w3c.dom.svg.SVGPathSegArcAbs
 *  org.w3c.dom.svg.SVGPathSegArcRel
 *  org.w3c.dom.svg.SVGPathSegCurvetoCubicAbs
 *  org.w3c.dom.svg.SVGPathSegCurvetoCubicRel
 *  org.w3c.dom.svg.SVGPathSegCurvetoCubicSmoothAbs
 *  org.w3c.dom.svg.SVGPathSegCurvetoCubicSmoothRel
 *  org.w3c.dom.svg.SVGPathSegCurvetoQuadraticAbs
 *  org.w3c.dom.svg.SVGPathSegCurvetoQuadraticRel
 *  org.w3c.dom.svg.SVGPathSegCurvetoQuadraticSmoothAbs
 *  org.w3c.dom.svg.SVGPathSegCurvetoQuadraticSmoothRel
 *  org.w3c.dom.svg.SVGPathSegLinetoAbs
 *  org.w3c.dom.svg.SVGPathSegLinetoHorizontalAbs
 *  org.w3c.dom.svg.SVGPathSegLinetoHorizontalRel
 *  org.w3c.dom.svg.SVGPathSegLinetoRel
 *  org.w3c.dom.svg.SVGPathSegLinetoVerticalAbs
 *  org.w3c.dom.svg.SVGPathSegLinetoVerticalRel
 *  org.w3c.dom.svg.SVGPathSegList
 *  org.w3c.dom.svg.SVGPathSegMovetoAbs
 *  org.w3c.dom.svg.SVGPathSegMovetoRel
 */
package org.apache.batik.dom.svg;

import org.apache.batik.dom.svg.AbstractSVGList;
import org.apache.batik.dom.svg.ListHandler;
import org.apache.batik.dom.svg.SVGItem;
import org.apache.batik.dom.svg.SVGPathSegConstants;
import org.apache.batik.dom.svg.SVGPathSegItem;
import org.apache.batik.parser.DefaultPathHandler;
import org.apache.batik.parser.ParseException;
import org.apache.batik.parser.PathHandler;
import org.apache.batik.parser.PathParser;
import org.w3c.dom.DOMException;
import org.w3c.dom.svg.SVGException;
import org.w3c.dom.svg.SVGPathSeg;
import org.w3c.dom.svg.SVGPathSegArcAbs;
import org.w3c.dom.svg.SVGPathSegArcRel;
import org.w3c.dom.svg.SVGPathSegCurvetoCubicAbs;
import org.w3c.dom.svg.SVGPathSegCurvetoCubicRel;
import org.w3c.dom.svg.SVGPathSegCurvetoCubicSmoothAbs;
import org.w3c.dom.svg.SVGPathSegCurvetoCubicSmoothRel;
import org.w3c.dom.svg.SVGPathSegCurvetoQuadraticAbs;
import org.w3c.dom.svg.SVGPathSegCurvetoQuadraticRel;
import org.w3c.dom.svg.SVGPathSegCurvetoQuadraticSmoothAbs;
import org.w3c.dom.svg.SVGPathSegCurvetoQuadraticSmoothRel;
import org.w3c.dom.svg.SVGPathSegLinetoAbs;
import org.w3c.dom.svg.SVGPathSegLinetoHorizontalAbs;
import org.w3c.dom.svg.SVGPathSegLinetoHorizontalRel;
import org.w3c.dom.svg.SVGPathSegLinetoRel;
import org.w3c.dom.svg.SVGPathSegLinetoVerticalAbs;
import org.w3c.dom.svg.SVGPathSegLinetoVerticalRel;
import org.w3c.dom.svg.SVGPathSegList;
import org.w3c.dom.svg.SVGPathSegMovetoAbs;
import org.w3c.dom.svg.SVGPathSegMovetoRel;

public abstract class AbstractSVGPathSegList
extends AbstractSVGList
implements SVGPathSegList,
SVGPathSegConstants {
    public static final String SVG_PATHSEG_LIST_SEPARATOR = " ";

    protected AbstractSVGPathSegList() {
    }

    @Override
    protected String getItemSeparator() {
        return SVG_PATHSEG_LIST_SEPARATOR;
    }

    protected abstract SVGException createSVGException(short var1, String var2, Object[] var3);

    public SVGPathSeg initialize(SVGPathSeg newItem) throws DOMException, SVGException {
        return (SVGPathSeg)this.initializeImpl(newItem);
    }

    public SVGPathSeg getItem(int index) throws DOMException {
        return (SVGPathSeg)this.getItemImpl(index);
    }

    public SVGPathSeg insertItemBefore(SVGPathSeg newItem, int index) throws DOMException, SVGException {
        return (SVGPathSeg)this.insertItemBeforeImpl(newItem, index);
    }

    public SVGPathSeg replaceItem(SVGPathSeg newItem, int index) throws DOMException, SVGException {
        return (SVGPathSeg)this.replaceItemImpl(newItem, index);
    }

    public SVGPathSeg removeItem(int index) throws DOMException {
        return (SVGPathSeg)this.removeItemImpl(index);
    }

    public SVGPathSeg appendItem(SVGPathSeg newItem) throws DOMException, SVGException {
        return (SVGPathSeg)this.appendItemImpl(newItem);
    }

    @Override
    protected SVGItem createSVGItem(Object newItem) {
        SVGPathSeg pathSeg = (SVGPathSeg)newItem;
        return this.createPathSegItem(pathSeg);
    }

    @Override
    protected void doParse(String value, ListHandler handler) throws ParseException {
        PathParser pathParser = new PathParser();
        PathSegListBuilder builder = new PathSegListBuilder(handler);
        pathParser.setPathHandler((PathHandler)builder);
        pathParser.parse(value);
    }

    @Override
    protected void checkItemType(Object newItem) {
        if (!(newItem instanceof SVGPathSeg)) {
            this.createSVGException((short)0, "expected SVGPathSeg", null);
        }
    }

    protected SVGPathSegItem createPathSegItem(SVGPathSeg pathSeg) {
        SVGPathSegItem pathSegItem = null;
        short type = pathSeg.getPathSegType();
        switch (type) {
            case 10: 
            case 11: {
                pathSegItem = new SVGPathSegArcItem(pathSeg);
                break;
            }
            case 1: {
                pathSegItem = new SVGPathSegItem(pathSeg);
                break;
            }
            case 6: 
            case 7: {
                pathSegItem = new SVGPathSegCurvetoCubicItem(pathSeg);
                break;
            }
            case 16: 
            case 17: {
                pathSegItem = new SVGPathSegCurvetoCubicSmoothItem(pathSeg);
                break;
            }
            case 8: 
            case 9: {
                pathSegItem = new SVGPathSegCurvetoQuadraticItem(pathSeg);
                break;
            }
            case 18: 
            case 19: {
                pathSegItem = new SVGPathSegCurvetoQuadraticSmoothItem(pathSeg);
                break;
            }
            case 2: 
            case 3: 
            case 4: 
            case 5: {
                pathSegItem = new SVGPathSegMovetoLinetoItem(pathSeg);
                break;
            }
            case 12: 
            case 13: {
                pathSegItem = new SVGPathSegLinetoHorizontalItem(pathSeg);
                break;
            }
            case 14: 
            case 15: {
                pathSegItem = new SVGPathSegLinetoVerticalItem(pathSeg);
                break;
            }
        }
        return pathSegItem;
    }

    protected static class PathSegListBuilder
    extends DefaultPathHandler {
        protected ListHandler listHandler;

        public PathSegListBuilder(ListHandler listHandler) {
            this.listHandler = listHandler;
        }

        public void startPath() throws ParseException {
            this.listHandler.startList();
        }

        public void endPath() throws ParseException {
            this.listHandler.endList();
        }

        public void movetoRel(float x, float y) throws ParseException {
            this.listHandler.item(new SVGPathSegMovetoLinetoItem(3, "m", x, y));
        }

        public void movetoAbs(float x, float y) throws ParseException {
            this.listHandler.item(new SVGPathSegMovetoLinetoItem(2, "M", x, y));
        }

        public void closePath() throws ParseException {
            this.listHandler.item(new SVGPathSegItem(1, "z"));
        }

        public void linetoRel(float x, float y) throws ParseException {
            this.listHandler.item(new SVGPathSegMovetoLinetoItem(5, "l", x, y));
        }

        public void linetoAbs(float x, float y) throws ParseException {
            this.listHandler.item(new SVGPathSegMovetoLinetoItem(4, "L", x, y));
        }

        public void linetoHorizontalRel(float x) throws ParseException {
            this.listHandler.item(new SVGPathSegLinetoHorizontalItem(13, "h", x));
        }

        public void linetoHorizontalAbs(float x) throws ParseException {
            this.listHandler.item(new SVGPathSegLinetoHorizontalItem(12, "H", x));
        }

        public void linetoVerticalRel(float y) throws ParseException {
            this.listHandler.item(new SVGPathSegLinetoVerticalItem(15, "v", y));
        }

        public void linetoVerticalAbs(float y) throws ParseException {
            this.listHandler.item(new SVGPathSegLinetoVerticalItem(14, "V", y));
        }

        public void curvetoCubicRel(float x1, float y1, float x2, float y2, float x, float y) throws ParseException {
            this.listHandler.item(new SVGPathSegCurvetoCubicItem(7, "c", x1, y1, x2, y2, x, y));
        }

        public void curvetoCubicAbs(float x1, float y1, float x2, float y2, float x, float y) throws ParseException {
            this.listHandler.item(new SVGPathSegCurvetoCubicItem(6, "C", x1, y1, x2, y2, x, y));
        }

        public void curvetoCubicSmoothRel(float x2, float y2, float x, float y) throws ParseException {
            this.listHandler.item(new SVGPathSegCurvetoCubicSmoothItem(17, "s", x2, y2, x, y));
        }

        public void curvetoCubicSmoothAbs(float x2, float y2, float x, float y) throws ParseException {
            this.listHandler.item(new SVGPathSegCurvetoCubicSmoothItem(16, "S", x2, y2, x, y));
        }

        public void curvetoQuadraticRel(float x1, float y1, float x, float y) throws ParseException {
            this.listHandler.item(new SVGPathSegCurvetoQuadraticItem(9, "q", x1, y1, x, y));
        }

        public void curvetoQuadraticAbs(float x1, float y1, float x, float y) throws ParseException {
            this.listHandler.item(new SVGPathSegCurvetoQuadraticItem(8, "Q", x1, y1, x, y));
        }

        public void curvetoQuadraticSmoothRel(float x, float y) throws ParseException {
            this.listHandler.item(new SVGPathSegCurvetoQuadraticSmoothItem(19, "t", x, y));
        }

        public void curvetoQuadraticSmoothAbs(float x, float y) throws ParseException {
            this.listHandler.item(new SVGPathSegCurvetoQuadraticSmoothItem(18, "T", x, y));
        }

        public void arcRel(float rx, float ry, float xAxisRotation, boolean largeArcFlag, boolean sweepFlag, float x, float y) throws ParseException {
            this.listHandler.item(new SVGPathSegArcItem(11, "a", rx, ry, xAxisRotation, largeArcFlag, sweepFlag, x, y));
        }

        public void arcAbs(float rx, float ry, float xAxisRotation, boolean largeArcFlag, boolean sweepFlag, float x, float y) throws ParseException {
            this.listHandler.item(new SVGPathSegArcItem(10, "A", rx, ry, xAxisRotation, largeArcFlag, sweepFlag, x, y));
        }
    }

    public static class SVGPathSegCurvetoQuadraticSmoothItem
    extends SVGPathSegItem
    implements SVGPathSegCurvetoQuadraticSmoothAbs,
    SVGPathSegCurvetoQuadraticSmoothRel {
        public SVGPathSegCurvetoQuadraticSmoothItem(short type, String letter, float x, float y) {
            super(type, letter);
            this.setX(x);
            this.setY(y);
        }

        public SVGPathSegCurvetoQuadraticSmoothItem(SVGPathSeg pathSeg) {
            this.type = pathSeg.getPathSegType();
            switch (this.type) {
                case 18: {
                    this.letter = "T";
                    this.setX(((SVGPathSegCurvetoQuadraticSmoothAbs)pathSeg).getX());
                    this.setY(((SVGPathSegCurvetoQuadraticSmoothAbs)pathSeg).getY());
                    break;
                }
                case 19: {
                    this.letter = "t";
                    this.setX(((SVGPathSegCurvetoQuadraticSmoothRel)pathSeg).getX());
                    this.setY(((SVGPathSegCurvetoQuadraticSmoothRel)pathSeg).getY());
                    break;
                }
            }
        }

        @Override
        public void setX(float x) {
            super.setX(x);
            this.resetAttribute();
        }

        @Override
        public void setY(float y) {
            super.setY(y);
            this.resetAttribute();
        }

        @Override
        protected String getStringValue() {
            return this.letter + ' ' + Float.toString(this.getX()) + ' ' + Float.toString(this.getY());
        }
    }

    public static class SVGPathSegCurvetoCubicSmoothItem
    extends SVGPathSegItem
    implements SVGPathSegCurvetoCubicSmoothAbs,
    SVGPathSegCurvetoCubicSmoothRel {
        public SVGPathSegCurvetoCubicSmoothItem(short type, String letter, float x2, float y2, float x, float y) {
            super(type, letter);
            this.setX(x);
            this.setY(y);
            this.setX2(x2);
            this.setY2(y2);
        }

        public SVGPathSegCurvetoCubicSmoothItem(SVGPathSeg pathSeg) {
            this.type = pathSeg.getPathSegType();
            switch (this.type) {
                case 16: {
                    this.letter = "S";
                    this.setX(((SVGPathSegCurvetoCubicSmoothAbs)pathSeg).getX());
                    this.setY(((SVGPathSegCurvetoCubicSmoothAbs)pathSeg).getY());
                    this.setX2(((SVGPathSegCurvetoCubicSmoothAbs)pathSeg).getX2());
                    this.setY2(((SVGPathSegCurvetoCubicSmoothAbs)pathSeg).getY2());
                    break;
                }
                case 17: {
                    this.letter = "s";
                    this.setX(((SVGPathSegCurvetoCubicSmoothRel)pathSeg).getX());
                    this.setY(((SVGPathSegCurvetoCubicSmoothRel)pathSeg).getY());
                    this.setX2(((SVGPathSegCurvetoCubicSmoothRel)pathSeg).getX2());
                    this.setY2(((SVGPathSegCurvetoCubicSmoothRel)pathSeg).getY2());
                    break;
                }
            }
        }

        @Override
        public void setX(float x) {
            super.setX(x);
            this.resetAttribute();
        }

        @Override
        public void setY(float y) {
            super.setY(y);
            this.resetAttribute();
        }

        @Override
        public void setX2(float x2) {
            super.setX2(x2);
            this.resetAttribute();
        }

        @Override
        public void setY2(float y2) {
            super.setY2(y2);
            this.resetAttribute();
        }

        @Override
        protected String getStringValue() {
            return this.letter + ' ' + Float.toString(this.getX2()) + ' ' + Float.toString(this.getY2()) + ' ' + Float.toString(this.getX()) + ' ' + Float.toString(this.getY());
        }
    }

    public static class SVGPathSegLinetoVerticalItem
    extends SVGPathSegItem
    implements SVGPathSegLinetoVerticalAbs,
    SVGPathSegLinetoVerticalRel {
        public SVGPathSegLinetoVerticalItem(short type, String letter, float value) {
            super(type, letter);
            this.setY(value);
        }

        public SVGPathSegLinetoVerticalItem(SVGPathSeg pathSeg) {
            this.type = pathSeg.getPathSegType();
            switch (this.type) {
                case 14: {
                    this.letter = "V";
                    this.setY(((SVGPathSegLinetoVerticalAbs)pathSeg).getY());
                    break;
                }
                case 15: {
                    this.letter = "v";
                    this.setY(((SVGPathSegLinetoVerticalRel)pathSeg).getY());
                    break;
                }
            }
        }

        @Override
        public void setY(float y) {
            super.setY(y);
            this.resetAttribute();
        }

        @Override
        protected String getStringValue() {
            return this.letter + ' ' + Float.toString(this.getY());
        }
    }

    public static class SVGPathSegLinetoHorizontalItem
    extends SVGPathSegItem
    implements SVGPathSegLinetoHorizontalAbs,
    SVGPathSegLinetoHorizontalRel {
        public SVGPathSegLinetoHorizontalItem(short type, String letter, float value) {
            super(type, letter);
            this.setX(value);
        }

        public SVGPathSegLinetoHorizontalItem(SVGPathSeg pathSeg) {
            this.type = pathSeg.getPathSegType();
            switch (this.type) {
                case 12: {
                    this.letter = "H";
                    this.setX(((SVGPathSegLinetoHorizontalAbs)pathSeg).getX());
                    break;
                }
                case 13: {
                    this.letter = "h";
                    this.setX(((SVGPathSegLinetoHorizontalRel)pathSeg).getX());
                    break;
                }
            }
        }

        @Override
        public void setX(float x) {
            super.setX(x);
            this.resetAttribute();
        }

        @Override
        protected String getStringValue() {
            return this.letter + ' ' + Float.toString(this.getX());
        }
    }

    public static class SVGPathSegArcItem
    extends SVGPathSegItem
    implements SVGPathSegArcAbs,
    SVGPathSegArcRel {
        public SVGPathSegArcItem(short type, String letter, float r1, float r2, float angle, boolean largeArcFlag, boolean sweepFlag, float x, float y) {
            super(type, letter);
            this.setX(x);
            this.setY(y);
            this.setR1(r1);
            this.setR2(r2);
            this.setAngle(angle);
            this.setLargeArcFlag(largeArcFlag);
            this.setSweepFlag(sweepFlag);
        }

        public SVGPathSegArcItem(SVGPathSeg pathSeg) {
            this.type = pathSeg.getPathSegType();
            switch (this.type) {
                case 10: {
                    this.letter = "A";
                    this.setX(((SVGPathSegArcAbs)pathSeg).getX());
                    this.setY(((SVGPathSegArcAbs)pathSeg).getY());
                    this.setR1(((SVGPathSegArcAbs)pathSeg).getR1());
                    this.setR2(((SVGPathSegArcAbs)pathSeg).getR2());
                    this.setAngle(((SVGPathSegArcAbs)pathSeg).getAngle());
                    this.setLargeArcFlag(((SVGPathSegArcAbs)pathSeg).getLargeArcFlag());
                    this.setSweepFlag(((SVGPathSegArcAbs)pathSeg).getSweepFlag());
                    break;
                }
                case 11: {
                    this.letter = "a";
                    this.setX(((SVGPathSegArcRel)pathSeg).getX());
                    this.setY(((SVGPathSegArcRel)pathSeg).getY());
                    this.setR1(((SVGPathSegArcRel)pathSeg).getR1());
                    this.setR2(((SVGPathSegArcRel)pathSeg).getR2());
                    this.setAngle(((SVGPathSegArcRel)pathSeg).getAngle());
                    this.setLargeArcFlag(((SVGPathSegArcRel)pathSeg).getLargeArcFlag());
                    this.setSweepFlag(((SVGPathSegArcRel)pathSeg).getSweepFlag());
                    break;
                }
            }
        }

        @Override
        public void setX(float x) {
            super.setX(x);
            this.resetAttribute();
        }

        @Override
        public void setY(float y) {
            super.setY(y);
            this.resetAttribute();
        }

        @Override
        public void setR1(float r1) {
            super.setR1(r1);
            this.resetAttribute();
        }

        @Override
        public void setR2(float r2) {
            super.setR2(r2);
            this.resetAttribute();
        }

        @Override
        public void setAngle(float angle) {
            super.setAngle(angle);
            this.resetAttribute();
        }

        public boolean getSweepFlag() {
            return this.isSweepFlag();
        }

        @Override
        public void setSweepFlag(boolean sweepFlag) {
            super.setSweepFlag(sweepFlag);
            this.resetAttribute();
        }

        public boolean getLargeArcFlag() {
            return this.isLargeArcFlag();
        }

        @Override
        public void setLargeArcFlag(boolean largeArcFlag) {
            super.setLargeArcFlag(largeArcFlag);
            this.resetAttribute();
        }

        @Override
        protected String getStringValue() {
            return this.letter + ' ' + Float.toString(this.getR1()) + ' ' + Float.toString(this.getR2()) + ' ' + Float.toString(this.getAngle()) + ' ' + (this.isLargeArcFlag() ? "1" : "0") + ' ' + (this.isSweepFlag() ? "1" : "0") + ' ' + Float.toString(this.getX()) + ' ' + Float.toString(this.getY());
        }
    }

    public static class SVGPathSegCurvetoQuadraticItem
    extends SVGPathSegItem
    implements SVGPathSegCurvetoQuadraticAbs,
    SVGPathSegCurvetoQuadraticRel {
        public SVGPathSegCurvetoQuadraticItem(short type, String letter, float x1, float y1, float x, float y) {
            super(type, letter);
            this.setX(x);
            this.setY(y);
            this.setX1(x1);
            this.setY1(y1);
        }

        public SVGPathSegCurvetoQuadraticItem(SVGPathSeg pathSeg) {
            this.type = pathSeg.getPathSegType();
            switch (this.type) {
                case 8: {
                    this.letter = "Q";
                    this.setX(((SVGPathSegCurvetoQuadraticAbs)pathSeg).getX());
                    this.setY(((SVGPathSegCurvetoQuadraticAbs)pathSeg).getY());
                    this.setX1(((SVGPathSegCurvetoQuadraticAbs)pathSeg).getX1());
                    this.setY1(((SVGPathSegCurvetoQuadraticAbs)pathSeg).getY1());
                    break;
                }
                case 9: {
                    this.letter = "q";
                    this.setX(((SVGPathSegCurvetoQuadraticRel)pathSeg).getX());
                    this.setY(((SVGPathSegCurvetoQuadraticRel)pathSeg).getY());
                    this.setX1(((SVGPathSegCurvetoQuadraticRel)pathSeg).getX1());
                    this.setY1(((SVGPathSegCurvetoQuadraticRel)pathSeg).getY1());
                    break;
                }
            }
        }

        @Override
        public void setX(float x) {
            super.setX(x);
            this.resetAttribute();
        }

        @Override
        public void setY(float y) {
            super.setY(y);
            this.resetAttribute();
        }

        @Override
        public void setX1(float x1) {
            super.setX1(x1);
            this.resetAttribute();
        }

        @Override
        public void setY1(float y1) {
            super.setY1(y1);
            this.resetAttribute();
        }

        @Override
        protected String getStringValue() {
            return this.letter + ' ' + Float.toString(this.getX1()) + ' ' + Float.toString(this.getY1()) + ' ' + Float.toString(this.getX()) + ' ' + Float.toString(this.getY());
        }
    }

    public static class SVGPathSegCurvetoCubicItem
    extends SVGPathSegItem
    implements SVGPathSegCurvetoCubicAbs,
    SVGPathSegCurvetoCubicRel {
        public SVGPathSegCurvetoCubicItem(short type, String letter, float x1, float y1, float x2, float y2, float x, float y) {
            super(type, letter);
            this.setX(x);
            this.setY(y);
            this.setX1(x1);
            this.setY1(y1);
            this.setX2(x2);
            this.setY2(y2);
        }

        public SVGPathSegCurvetoCubicItem(SVGPathSeg pathSeg) {
            this.type = pathSeg.getPathSegType();
            switch (this.type) {
                case 6: {
                    this.letter = "C";
                    this.setX(((SVGPathSegCurvetoCubicAbs)pathSeg).getX());
                    this.setY(((SVGPathSegCurvetoCubicAbs)pathSeg).getY());
                    this.setX1(((SVGPathSegCurvetoCubicAbs)pathSeg).getX1());
                    this.setY1(((SVGPathSegCurvetoCubicAbs)pathSeg).getY1());
                    this.setX2(((SVGPathSegCurvetoCubicAbs)pathSeg).getX2());
                    this.setY2(((SVGPathSegCurvetoCubicAbs)pathSeg).getY2());
                    break;
                }
                case 7: {
                    this.letter = "c";
                    this.setX(((SVGPathSegCurvetoCubicRel)pathSeg).getX());
                    this.setY(((SVGPathSegCurvetoCubicRel)pathSeg).getY());
                    this.setX1(((SVGPathSegCurvetoCubicRel)pathSeg).getX1());
                    this.setY1(((SVGPathSegCurvetoCubicRel)pathSeg).getY1());
                    this.setX2(((SVGPathSegCurvetoCubicRel)pathSeg).getX2());
                    this.setY2(((SVGPathSegCurvetoCubicRel)pathSeg).getY2());
                    break;
                }
            }
        }

        @Override
        public void setX(float x) {
            super.setX(x);
            this.resetAttribute();
        }

        @Override
        public void setY(float y) {
            super.setY(y);
            this.resetAttribute();
        }

        @Override
        public void setX1(float x1) {
            super.setX1(x1);
            this.resetAttribute();
        }

        @Override
        public void setY1(float y1) {
            super.setY1(y1);
            this.resetAttribute();
        }

        @Override
        public void setX2(float x2) {
            super.setX2(x2);
            this.resetAttribute();
        }

        @Override
        public void setY2(float y2) {
            super.setY2(y2);
            this.resetAttribute();
        }

        @Override
        protected String getStringValue() {
            return this.letter + ' ' + Float.toString(this.getX1()) + ' ' + Float.toString(this.getY1()) + ' ' + Float.toString(this.getX2()) + ' ' + Float.toString(this.getY2()) + ' ' + Float.toString(this.getX()) + ' ' + Float.toString(this.getY());
        }
    }

    public static class SVGPathSegMovetoLinetoItem
    extends SVGPathSegItem
    implements SVGPathSegMovetoAbs,
    SVGPathSegMovetoRel,
    SVGPathSegLinetoAbs,
    SVGPathSegLinetoRel {
        public SVGPathSegMovetoLinetoItem(short type, String letter, float x, float y) {
            super(type, letter);
            this.setX(x);
            this.setY(y);
        }

        public SVGPathSegMovetoLinetoItem(SVGPathSeg pathSeg) {
            this.type = pathSeg.getPathSegType();
            switch (this.type) {
                case 5: {
                    this.letter = "l";
                    this.setX(((SVGPathSegLinetoRel)pathSeg).getX());
                    this.setY(((SVGPathSegLinetoRel)pathSeg).getY());
                    break;
                }
                case 4: {
                    this.letter = "L";
                    this.setX(((SVGPathSegLinetoAbs)pathSeg).getX());
                    this.setY(((SVGPathSegLinetoAbs)pathSeg).getY());
                    break;
                }
                case 3: {
                    this.letter = "m";
                    this.setX(((SVGPathSegMovetoRel)pathSeg).getX());
                    this.setY(((SVGPathSegMovetoRel)pathSeg).getY());
                    break;
                }
                case 2: {
                    this.letter = "M";
                    this.setX(((SVGPathSegMovetoAbs)pathSeg).getX());
                    this.setY(((SVGPathSegMovetoAbs)pathSeg).getY());
                    break;
                }
            }
        }

        @Override
        public void setX(float x) {
            super.setX(x);
            this.resetAttribute();
        }

        @Override
        public void setY(float y) {
            super.setY(y);
            this.resetAttribute();
        }

        @Override
        protected String getStringValue() {
            return this.letter + ' ' + Float.toString(this.getX()) + ' ' + Float.toString(this.getY());
        }
    }
}

