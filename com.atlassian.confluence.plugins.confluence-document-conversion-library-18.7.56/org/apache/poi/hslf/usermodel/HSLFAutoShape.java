/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hslf.usermodel;

import java.awt.geom.Arc2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ddf.AbstractEscherOptRecord;
import org.apache.poi.ddf.EscherArrayProperty;
import org.apache.poi.ddf.EscherContainerRecord;
import org.apache.poi.ddf.EscherPropertyTypes;
import org.apache.poi.ddf.EscherSimpleProperty;
import org.apache.poi.hslf.usermodel.HSLFFreeformShape;
import org.apache.poi.hslf.usermodel.HSLFGroupShape;
import org.apache.poi.hslf.usermodel.HSLFShape;
import org.apache.poi.hslf.usermodel.HSLFTextParagraph;
import org.apache.poi.hslf.usermodel.HSLFTextShape;
import org.apache.poi.sl.draw.geom.AdjustPoint;
import org.apache.poi.sl.draw.geom.ArcToCommand;
import org.apache.poi.sl.draw.geom.ClosePathCommand;
import org.apache.poi.sl.draw.geom.CurveToCommand;
import org.apache.poi.sl.draw.geom.CustomGeometry;
import org.apache.poi.sl.draw.geom.LineToCommand;
import org.apache.poi.sl.draw.geom.MoveToCommand;
import org.apache.poi.sl.draw.geom.Path;
import org.apache.poi.sl.usermodel.AutoShape;
import org.apache.poi.sl.usermodel.ShapeContainer;
import org.apache.poi.sl.usermodel.ShapeType;
import org.apache.poi.sl.usermodel.VerticalAlignment;
import org.apache.poi.util.BitField;
import org.apache.poi.util.BitFieldFactory;
import org.apache.poi.util.LittleEndian;

public class HSLFAutoShape
extends HSLFTextShape
implements AutoShape<HSLFShape, HSLFTextParagraph> {
    private static final Logger LOG = LogManager.getLogger(HSLFAutoShape.class);
    static final byte[] SEGMENTINFO_MOVETO = new byte[]{0, 64};
    static final byte[] SEGMENTINFO_LINETO = new byte[]{0, -84};
    static final byte[] SEGMENTINFO_ESCAPE = new byte[]{1, 0};
    static final byte[] SEGMENTINFO_ESCAPE2 = new byte[]{1, 32};
    static final byte[] SEGMENTINFO_CUBICTO = new byte[]{0, -83};
    static final byte[] SEGMENTINFO_CLOSE = new byte[]{1, 96};
    static final byte[] SEGMENTINFO_END = new byte[]{0, -128};
    private static final BitField PATH_INFO = BitFieldFactory.getInstance(57344);
    private static final BitField ESCAPE_INFO = BitFieldFactory.getInstance(7936);

    protected HSLFAutoShape(EscherContainerRecord escherRecord, ShapeContainer<HSLFShape, HSLFTextParagraph> parent) {
        super(escherRecord, parent);
    }

    public HSLFAutoShape(ShapeType type, ShapeContainer<HSLFShape, HSLFTextParagraph> parent) {
        super(null, parent);
        this.createSpContainer(type, parent instanceof HSLFGroupShape);
    }

    public HSLFAutoShape(ShapeType type) {
        this(type, null);
    }

    protected EscherContainerRecord createSpContainer(ShapeType shapeType, boolean isChild) {
        EscherContainerRecord ecr = super.createSpContainer(isChild);
        this.setShapeType(shapeType);
        this.setEscherProperty(EscherPropertyTypes.PROTECTION__LOCKAGAINSTGROUPING, 262144);
        this.setEscherProperty(EscherPropertyTypes.FILL__FILLCOLOR, 0x8000004);
        this.setEscherProperty(EscherPropertyTypes.FILL__FILLCOLOR, 0x8000004);
        this.setEscherProperty(EscherPropertyTypes.FILL__FILLBACKCOLOR, 0x8000000);
        this.setEscherProperty(EscherPropertyTypes.FILL__NOFILLHITTEST, 0x100010);
        this.setEscherProperty(EscherPropertyTypes.LINESTYLE__COLOR, 0x8000001);
        this.setEscherProperty(EscherPropertyTypes.LINESTYLE__NOLINEDRAWDASH, 524296);
        this.setEscherProperty(EscherPropertyTypes.SHADOWSTYLE__COLOR, 0x8000002);
        return ecr;
    }

    @Override
    protected void setDefaultTextProperties(HSLFTextParagraph _txtrun) {
        this.setVerticalAlignment(VerticalAlignment.MIDDLE);
        this.setHorizontalCentered(true);
        this.setWordWrap(false);
    }

    public int getAdjustmentValue(int idx) {
        if (idx < 0 || idx > 9) {
            throw new IllegalArgumentException("The index of an adjustment value must be in the [0, 9] range");
        }
        return this.getEscherProperty(ADJUST_VALUES[idx]);
    }

    public void setAdjustmentValue(int idx, int val) {
        if (idx < 0 || idx > 9) {
            throw new IllegalArgumentException("The index of an adjustment value must be in the [0, 9] range");
        }
        this.setEscherProperty(ADJUST_VALUES[idx], val);
    }

    @Override
    public CustomGeometry getGeometry() {
        return this.getGeometry(new Path2D.Double());
    }

    CustomGeometry getGeometry(Path2D path2D) {
        CustomGeometry cusGeo = new CustomGeometry();
        AbstractEscherOptRecord opt = this.getEscherOptRecord();
        EscherArrayProperty verticesProp = (EscherArrayProperty)HSLFAutoShape.getEscherProperty(opt, EscherPropertyTypes.GEOMETRY__VERTICES);
        EscherArrayProperty segmentsProp = (EscherArrayProperty)HSLFAutoShape.getEscherProperty(opt, EscherPropertyTypes.GEOMETRY__SEGMENTINFO);
        if (verticesProp == null) {
            LOG.atWarn().log("Freeform is missing GEOMETRY__VERTICES ");
            return super.getGeometry();
        }
        if (segmentsProp == null) {
            LOG.atWarn().log("Freeform is missing GEOMETRY__SEGMENTINFO ");
            return super.getGeometry();
        }
        Iterator<byte[]> vertIter = verticesProp.iterator();
        Iterator<byte[]> segIter = segmentsProp.iterator();
        int[] xyPoints = new int[2];
        boolean isClosed = false;
        Path path = new Path();
        cusGeo.addPath(path);
        while (segIter.hasNext()) {
            byte[] segElem = segIter.next();
            PathInfo pi = HSLFAutoShape.getPathInfo(segElem);
            if (pi == null) continue;
            switch (pi) {
                case escape: {
                    HSLFAutoShape.handleEscapeInfo(path, path2D, segElem, vertIter);
                    break;
                }
                case moveTo: {
                    HSLFAutoShape.handleMoveTo(vertIter, xyPoints, path, path2D);
                    break;
                }
                case lineTo: {
                    HSLFAutoShape.handleLineTo(vertIter, xyPoints, path, path2D);
                    break;
                }
                case curveTo: {
                    HSLFAutoShape.handleCurveTo(vertIter, xyPoints, path, path2D);
                    break;
                }
                case close: {
                    if (path2D.getCurrentPoint() != null) {
                        path.addCommand(new ClosePathCommand());
                        path2D.closePath();
                    }
                    isClosed = true;
                    break;
                }
            }
        }
        if (!isClosed) {
            HSLFAutoShape.handleClosedShape(opt, path, path2D);
        }
        Rectangle2D bounds = HSLFAutoShape.getBounds(opt, path2D);
        path.setW((int)Math.rint(bounds.getWidth()));
        path.setH((int)Math.rint(bounds.getHeight()));
        return cusGeo;
    }

    private static Rectangle2D getBounds(AbstractEscherOptRecord opt, Path2D path2D) {
        EscherSimpleProperty geoLeft = (EscherSimpleProperty)HSLFAutoShape.getEscherProperty(opt, EscherPropertyTypes.GEOMETRY__LEFT);
        EscherSimpleProperty geoRight = (EscherSimpleProperty)HSLFAutoShape.getEscherProperty(opt, EscherPropertyTypes.GEOMETRY__RIGHT);
        EscherSimpleProperty geoTop = (EscherSimpleProperty)HSLFAutoShape.getEscherProperty(opt, EscherPropertyTypes.GEOMETRY__TOP);
        EscherSimpleProperty geoBottom = (EscherSimpleProperty)HSLFAutoShape.getEscherProperty(opt, EscherPropertyTypes.GEOMETRY__BOTTOM);
        if (geoLeft != null && geoRight != null && geoTop != null && geoBottom != null) {
            Rectangle2D.Double bounds = new Rectangle2D.Double();
            bounds.setFrameFromDiagonal(new Point2D.Double(geoLeft.getPropertyValue(), geoTop.getPropertyValue()), new Point2D.Double(geoRight.getPropertyValue(), geoBottom.getPropertyValue()));
            return bounds;
        }
        return path2D.getBounds2D();
    }

    private static void handleClosedShape(AbstractEscherOptRecord opt, Path path, Path2D path2D) {
        EscherSimpleProperty shapePath = (EscherSimpleProperty)HSLFAutoShape.getEscherProperty(opt, EscherPropertyTypes.GEOMETRY__SHAPEPATH);
        HSLFFreeformShape.ShapePath sp = HSLFFreeformShape.ShapePath.valueOf(shapePath == null ? 1 : shapePath.getPropertyValue());
        if (sp == HSLFFreeformShape.ShapePath.LINES_CLOSED || sp == HSLFFreeformShape.ShapePath.CURVES_CLOSED) {
            path.addCommand(new ClosePathCommand());
            path2D.closePath();
        }
    }

    private static void handleMoveTo(Iterator<byte[]> vertIter, int[] xyPoints, Path path, Path2D path2D) {
        if (!vertIter.hasNext()) {
            return;
        }
        MoveToCommand m = new MoveToCommand();
        m.setPt(HSLFAutoShape.fillPoint(vertIter.next(), xyPoints));
        path.addCommand(m);
        path2D.moveTo(xyPoints[0], xyPoints[1]);
    }

    private static void handleLineTo(Iterator<byte[]> vertIter, int[] xyPoints, Path path, Path2D path2D) {
        if (!vertIter.hasNext()) {
            return;
        }
        HSLFAutoShape.handleMoveTo0(path, path2D);
        LineToCommand m = new LineToCommand();
        m.setPt(HSLFAutoShape.fillPoint(vertIter.next(), xyPoints));
        path.addCommand(m);
        path2D.lineTo(xyPoints[0], xyPoints[1]);
    }

    private static void handleCurveTo(Iterator<byte[]> vertIter, int[] xyPoints, Path path, Path2D path2D) {
        if (!vertIter.hasNext()) {
            return;
        }
        HSLFAutoShape.handleMoveTo0(path, path2D);
        CurveToCommand m = new CurveToCommand();
        int[] pts = new int[6];
        AdjustPoint[] ap = new AdjustPoint[3];
        for (int i = 0; vertIter.hasNext() && i < 3; ++i) {
            ap[i] = HSLFAutoShape.fillPoint(vertIter.next(), xyPoints);
            pts[i * 2] = xyPoints[0];
            pts[i * 2 + 1] = xyPoints[1];
        }
        m.setPt1(ap[0]);
        m.setPt2(ap[1]);
        m.setPt3(ap[2]);
        path.addCommand(m);
        path2D.curveTo(pts[0], pts[1], pts[2], pts[3], pts[4], pts[5]);
    }

    private static void handleMoveTo0(Path moveLst, Path2D path2D) {
        if (path2D.getCurrentPoint() == null) {
            MoveToCommand m = new MoveToCommand();
            AdjustPoint pt = new AdjustPoint();
            pt.setX("0");
            pt.setY("0");
            m.setPt(pt);
            moveLst.addCommand(m);
            path2D.moveTo(0.0, 0.0);
        }
    }

    private static void handleEscapeInfo(Path pathCT, Path2D path2D, byte[] segElem, Iterator<byte[]> vertIter) {
        EscapeInfo ei = HSLFAutoShape.getEscapeInfo(segElem);
        if (ei == null) {
            return;
        }
        switch (ei) {
            case EXTENSION: {
                break;
            }
            case ANGLE_ELLIPSE_TO: {
                break;
            }
            case ANGLE_ELLIPSE: {
                break;
            }
            case ARC_TO: {
                int[] r1 = new int[2];
                int[] r2 = new int[2];
                int[] start = new int[2];
                int[] end = new int[2];
                HSLFAutoShape.fillPoint(vertIter.next(), r1);
                HSLFAutoShape.fillPoint(vertIter.next(), r2);
                HSLFAutoShape.fillPoint(vertIter.next(), start);
                HSLFAutoShape.fillPoint(vertIter.next(), end);
                Arc2D.Double arc2D = new Arc2D.Double();
                Rectangle2D.Double bounds = new Rectangle2D.Double();
                bounds.setFrameFromDiagonal(HSLFAutoShape.xy2p(r1), HSLFAutoShape.xy2p(r2));
                arc2D.setFrame(bounds);
                arc2D.setAngles(HSLFAutoShape.xy2p(start), HSLFAutoShape.xy2p(end));
                path2D.append(arc2D, true);
                ArcToCommand arcTo = new ArcToCommand();
                arcTo.setHR(HSLFAutoShape.d2s(bounds.getHeight() / 2.0));
                arcTo.setWR(HSLFAutoShape.d2s(bounds.getWidth() / 2.0));
                arcTo.setStAng(HSLFAutoShape.d2s(-((Arc2D)arc2D).getAngleStart() * 60000.0));
                arcTo.setSwAng(HSLFAutoShape.d2s(-((Arc2D)arc2D).getAngleExtent() * 60000.0));
                pathCT.addCommand(arcTo);
                break;
            }
            case ARC: {
                break;
            }
            case CLOCKWISE_ARC_TO: {
                break;
            }
            case CLOCKWISE_ARC: {
                break;
            }
            case ELLIPTICAL_QUADRANT_X: {
                break;
            }
            case ELLIPTICAL_QUADRANT_Y: {
                break;
            }
            case QUADRATIC_BEZIER: {
                break;
            }
            case NO_FILL: {
                break;
            }
            case NO_LINE: {
                break;
            }
            case AUTO_LINE: {
                break;
            }
            case AUTO_CURVE: {
                break;
            }
            case CORNER_LINE: {
                break;
            }
            case CORNER_CURVE: {
                break;
            }
            case SMOOTH_LINE: {
                break;
            }
            case SMOOTH_CURVE: {
                break;
            }
            case SYMMETRIC_LINE: {
                break;
            }
            case SYMMETRIC_CURVE: {
                break;
            }
            case FREEFORM: {
                break;
            }
            case FILL_COLOR: {
                break;
            }
            case LINE_COLOR: {
                break;
            }
        }
    }

    private static String d2s(double d) {
        return Integer.toString((int)Math.rint(d));
    }

    private static Point2D xy2p(int[] xyPoints) {
        return new Point2D.Double(xyPoints[0], xyPoints[1]);
    }

    private static PathInfo getPathInfo(byte[] elem) {
        int elemUS = LittleEndian.getUShort(elem, 0);
        int pathInfo = PATH_INFO.getValue(elemUS);
        return PathInfo.valueOf(pathInfo);
    }

    private static EscapeInfo getEscapeInfo(byte[] elem) {
        int elemUS = LittleEndian.getUShort(elem, 0);
        int escInfo = ESCAPE_INFO.getValue(elemUS);
        return EscapeInfo.valueOf(escInfo);
    }

    private static AdjustPoint fillPoint(byte[] xyMaster, int[] xyPoints) {
        int y;
        int x;
        if (xyMaster == null || xyPoints == null) {
            LOG.atWarn().log("Master bytes or points not set - ignore point");
            return null;
        }
        if (xyMaster.length != 4 && xyMaster.length != 8 || xyPoints.length != 2) {
            LOG.atWarn().log("Invalid number of master bytes for a single point - ignore point");
            return null;
        }
        if (xyMaster.length == 4) {
            x = LittleEndian.getShort(xyMaster, 0);
            y = LittleEndian.getShort(xyMaster, 2);
        } else {
            x = LittleEndian.getInt(xyMaster, 0);
            y = LittleEndian.getInt(xyMaster, 4);
        }
        xyPoints[0] = x;
        xyPoints[1] = y;
        return HSLFAutoShape.toPoint(xyPoints);
    }

    private static AdjustPoint toPoint(int[] xyPoints) {
        AdjustPoint pt = new AdjustPoint();
        pt.setX(Integer.toString(xyPoints[0]));
        pt.setY(Integer.toString(xyPoints[1]));
        return pt;
    }

    static enum EscapeInfo {
        EXTENSION(0),
        ANGLE_ELLIPSE_TO(1),
        ANGLE_ELLIPSE(2),
        ARC_TO(3),
        ARC(4),
        CLOCKWISE_ARC_TO(5),
        CLOCKWISE_ARC(6),
        ELLIPTICAL_QUADRANT_X(7),
        ELLIPTICAL_QUADRANT_Y(8),
        QUADRATIC_BEZIER(9),
        NO_FILL(10),
        NO_LINE(11),
        AUTO_LINE(12),
        AUTO_CURVE(13),
        CORNER_LINE(14),
        CORNER_CURVE(15),
        SMOOTH_LINE(16),
        SMOOTH_CURVE(17),
        SYMMETRIC_LINE(18),
        SYMMETRIC_CURVE(19),
        FREEFORM(20),
        FILL_COLOR(21),
        LINE_COLOR(22);

        private final int flag;

        private EscapeInfo(int flag) {
            this.flag = flag;
        }

        public int getFlag() {
            return this.flag;
        }

        static EscapeInfo valueOf(int flag) {
            for (EscapeInfo v : EscapeInfo.values()) {
                if (v.flag != flag) continue;
                return v;
            }
            return null;
        }
    }

    static enum PathInfo {
        lineTo(0),
        curveTo(1),
        moveTo(2),
        close(3),
        end(4),
        escape(5),
        clientEscape(6);

        private final int flag;

        private PathInfo(int flag) {
            this.flag = flag;
        }

        public int getFlag() {
            return this.flag;
        }

        static PathInfo valueOf(int flag) {
            for (PathInfo v : PathInfo.values()) {
                if (v.flag != flag) continue;
                return v;
            }
            return null;
        }
    }
}

