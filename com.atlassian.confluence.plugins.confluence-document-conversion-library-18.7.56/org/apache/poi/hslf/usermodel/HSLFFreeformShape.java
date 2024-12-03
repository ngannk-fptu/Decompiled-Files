/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hslf.usermodel;

import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Unbox;
import org.apache.poi.ddf.AbstractEscherOptRecord;
import org.apache.poi.ddf.EscherArrayProperty;
import org.apache.poi.ddf.EscherContainerRecord;
import org.apache.poi.ddf.EscherPropertyTypes;
import org.apache.poi.ddf.EscherSimpleProperty;
import org.apache.poi.hslf.usermodel.HSLFAutoShape;
import org.apache.poi.hslf.usermodel.HSLFGroupShape;
import org.apache.poi.hslf.usermodel.HSLFShape;
import org.apache.poi.hslf.usermodel.HSLFTextParagraph;
import org.apache.poi.sl.usermodel.FreeformShape;
import org.apache.poi.sl.usermodel.ShapeContainer;
import org.apache.poi.sl.usermodel.ShapeType;
import org.apache.poi.util.LittleEndian;
import org.apache.poi.util.Units;

public final class HSLFFreeformShape
extends HSLFAutoShape
implements FreeformShape<HSLFShape, HSLFTextParagraph> {
    private static final Logger LOG = LogManager.getLogger(HSLFFreeformShape.class);

    protected HSLFFreeformShape(EscherContainerRecord escherRecord, ShapeContainer<HSLFShape, HSLFTextParagraph> parent) {
        super(escherRecord, parent);
    }

    public HSLFFreeformShape(ShapeContainer<HSLFShape, HSLFTextParagraph> parent) {
        super((EscherContainerRecord)null, parent);
        this.createSpContainer(ShapeType.NOT_PRIMITIVE, parent instanceof HSLFGroupShape);
    }

    public HSLFFreeformShape() {
        this((ShapeContainer<HSLFShape, HSLFTextParagraph>)null);
    }

    @Override
    public int setPath(Path2D path) {
        Rectangle2D bounds = path.getBounds2D();
        PathIterator it = path.getPathIterator(null);
        ArrayList<byte[]> segInfo = new ArrayList<byte[]>();
        ArrayList<Point2D.Double> pntInfo = new ArrayList<Point2D.Double>();
        boolean isClosed = false;
        int numPoints = 0;
        while (!it.isDone()) {
            double[] vals = new double[6];
            int type = it.currentSegment(vals);
            switch (type) {
                case 0: {
                    pntInfo.add(new Point2D.Double(vals[0], vals[1]));
                    segInfo.add(SEGMENTINFO_MOVETO);
                    ++numPoints;
                    break;
                }
                case 1: {
                    pntInfo.add(new Point2D.Double(vals[0], vals[1]));
                    segInfo.add(SEGMENTINFO_LINETO);
                    segInfo.add(SEGMENTINFO_ESCAPE);
                    ++numPoints;
                    break;
                }
                case 3: {
                    pntInfo.add(new Point2D.Double(vals[0], vals[1]));
                    pntInfo.add(new Point2D.Double(vals[2], vals[3]));
                    pntInfo.add(new Point2D.Double(vals[4], vals[5]));
                    segInfo.add(SEGMENTINFO_CUBICTO);
                    segInfo.add(SEGMENTINFO_ESCAPE2);
                    ++numPoints;
                    break;
                }
                case 2: {
                    LOG.atWarn().log("SEG_QUADTO is not supported");
                    break;
                }
                case 4: {
                    pntInfo.add((Point2D.Double)pntInfo.get(0));
                    segInfo.add(SEGMENTINFO_LINETO);
                    segInfo.add(SEGMENTINFO_ESCAPE);
                    segInfo.add(SEGMENTINFO_LINETO);
                    segInfo.add(SEGMENTINFO_CLOSE);
                    isClosed = true;
                    ++numPoints;
                    break;
                }
                default: {
                    LOG.atWarn().log("Ignoring invalid segment type {}", (Object)Unbox.box(type));
                }
            }
            it.next();
        }
        if (!isClosed) {
            segInfo.add(SEGMENTINFO_LINETO);
        }
        segInfo.add(SEGMENTINFO_END);
        AbstractEscherOptRecord opt = this.getEscherOptRecord();
        opt.addEscherProperty(new EscherSimpleProperty(EscherPropertyTypes.GEOMETRY__SHAPEPATH, 4));
        EscherArrayProperty verticesProp = new EscherArrayProperty(EscherPropertyTypes.GEOMETRY__VERTICES, true, 0);
        verticesProp.setNumberOfElementsInArray(pntInfo.size());
        verticesProp.setNumberOfElementsInMemory(pntInfo.size());
        verticesProp.setSizeOfElements(8);
        for (int i = 0; i < pntInfo.size(); ++i) {
            Point2D.Double pnt = (Point2D.Double)pntInfo.get(i);
            byte[] data = new byte[8];
            LittleEndian.putInt(data, 0, Units.pointsToMaster(pnt.getX() - bounds.getX()));
            LittleEndian.putInt(data, 4, Units.pointsToMaster(pnt.getY() - bounds.getY()));
            verticesProp.setElement(i, data);
        }
        opt.addEscherProperty(verticesProp);
        EscherArrayProperty segmentsProp = new EscherArrayProperty(EscherPropertyTypes.GEOMETRY__SEGMENTINFO, true, 0);
        segmentsProp.setNumberOfElementsInArray(segInfo.size());
        segmentsProp.setNumberOfElementsInMemory(segInfo.size());
        segmentsProp.setSizeOfElements(2);
        for (int i = 0; i < segInfo.size(); ++i) {
            byte[] seg = (byte[])segInfo.get(i);
            segmentsProp.setElement(i, seg);
        }
        opt.addEscherProperty(segmentsProp);
        opt.addEscherProperty(new EscherSimpleProperty(EscherPropertyTypes.GEOMETRY__RIGHT, Units.pointsToMaster(bounds.getWidth())));
        opt.addEscherProperty(new EscherSimpleProperty(EscherPropertyTypes.GEOMETRY__BOTTOM, Units.pointsToMaster(bounds.getHeight())));
        opt.sortProperties();
        this.setAnchor(bounds);
        return numPoints;
    }

    @Override
    public Path2D getPath() {
        Path2D.Double path2D = new Path2D.Double();
        this.getGeometry(path2D);
        Rectangle2D bounds = path2D.getBounds2D();
        Rectangle2D anchor = this.getAnchor();
        AffineTransform at = new AffineTransform();
        at.translate(anchor.getX(), anchor.getY());
        at.scale(anchor.getWidth() / bounds.getWidth(), anchor.getHeight() / bounds.getHeight());
        ((Path2D)path2D).transform(at);
        return path2D;
    }

    static enum ShapePath {
        LINES(0),
        LINES_CLOSED(1),
        CURVES(2),
        CURVES_CLOSED(3),
        COMPLEX(4);

        private final int flag;

        private ShapePath(int flag) {
            this.flag = flag;
        }

        public int getFlag() {
            return this.flag;
        }

        static ShapePath valueOf(int flag) {
            for (ShapePath v : ShapePath.values()) {
                if (v.flag != flag) continue;
                return v;
            }
            return null;
        }
    }
}

