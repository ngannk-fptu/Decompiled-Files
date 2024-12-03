/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.rmi;

import com.sun.media.jai.rmi.SerializableStateImpl;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.awt.geom.QuadCurve2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class ShapeState
extends SerializableStateImpl {
    private static final int SHAPE_UNKNOWN = 0;
    private static final int SHAPE_AREA = 1;
    private static final int SHAPE_ARC_DOUBLE = 2;
    private static final int SHAPE_ARC_FLOAT = 3;
    private static final int SHAPE_CUBICCURVE_DOUBLE = 4;
    private static final int SHAPE_CUBICCURVE_FLOAT = 5;
    private static final int SHAPE_ELLIPSE_DOUBLE = 6;
    private static final int SHAPE_ELLIPSE_FLOAT = 7;
    private static final int SHAPE_GENERALPATH = 8;
    private static final int SHAPE_LINE_DOUBLE = 9;
    private static final int SHAPE_LINE_FLOAT = 10;
    private static final int SHAPE_QUADCURVE_DOUBLE = 11;
    private static final int SHAPE_QUADCURVE_FLOAT = 12;
    private static final int SHAPE_ROUNDRECTANGLE_DOUBLE = 13;
    private static final int SHAPE_ROUNDRECTANGLE_FLOAT = 14;
    private static final int SHAPE_RECTANGLE_DOUBLE = 15;
    private static final int SHAPE_RECTANGLE_FLOAT = 16;
    static /* synthetic */ Class class$java$awt$Shape;

    public static Class[] getSupportedClasses() {
        return new Class[]{class$java$awt$Shape == null ? (class$java$awt$Shape = ShapeState.class$("java.awt.Shape")) : class$java$awt$Shape};
    }

    public ShapeState(Class c, Object o, RenderingHints h) {
        super(c, o, h);
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        boolean serializable = false;
        Object object = this.theObject;
        if (object instanceof Serializable) {
            serializable = true;
        }
        out.writeBoolean(serializable);
        if (serializable) {
            out.writeObject(object);
            return;
        }
        Object[] dataArray = null;
        Integer otherData = null;
        int type = 0;
        if (this.theObject instanceof Area) {
            type = 1;
        } else if (this.theObject instanceof Arc2D.Double) {
            Arc2D.Double ad = (Arc2D.Double)this.theObject;
            dataArray = new double[]{ad.x, ad.y, ad.width, ad.height, ad.start, ad.extent};
            type = 2;
            otherData = new Integer(ad.getArcType());
        } else if (this.theObject instanceof Arc2D.Float) {
            Arc2D.Float af = (Arc2D.Float)this.theObject;
            dataArray = new float[]{af.x, af.y, af.width, af.height, af.start, af.extent};
            type = 3;
            otherData = new Integer(af.getArcType());
        } else if (this.theObject instanceof CubicCurve2D.Double) {
            CubicCurve2D.Double cd = (CubicCurve2D.Double)this.theObject;
            dataArray = new double[]{cd.x1, cd.y1, cd.ctrlx1, cd.ctrly1, cd.ctrlx2, cd.ctrly2, cd.x2, cd.y2};
            type = 4;
        } else if (this.theObject instanceof CubicCurve2D.Float) {
            CubicCurve2D.Float cf = (CubicCurve2D.Float)this.theObject;
            dataArray = new float[]{cf.x1, cf.y1, cf.ctrlx1, cf.ctrly1, cf.ctrlx2, cf.ctrly2, cf.x2, cf.y2};
            type = 5;
        } else if (this.theObject instanceof Ellipse2D.Double) {
            Ellipse2D.Double ed = (Ellipse2D.Double)this.theObject;
            dataArray = new double[]{ed.x, ed.y, ed.width, ed.height};
            type = 6;
        } else if (this.theObject instanceof Ellipse2D.Float) {
            Ellipse2D.Float ef = (Ellipse2D.Float)this.theObject;
            dataArray = new float[]{ef.x, ef.y, ef.width, ef.height};
            type = 7;
        } else if (this.theObject instanceof GeneralPath) {
            type = 8;
        } else if (this.theObject instanceof Line2D.Double) {
            Line2D.Double ld = (Line2D.Double)this.theObject;
            dataArray = new double[]{ld.x1, ld.y1, ld.x2, ld.y2};
            type = 9;
        } else if (this.theObject instanceof Line2D.Float) {
            Line2D.Float lf = (Line2D.Float)this.theObject;
            dataArray = new float[]{lf.x1, lf.y1, lf.x2, lf.y2};
            type = 10;
        } else if (this.theObject instanceof QuadCurve2D.Double) {
            QuadCurve2D.Double qd = (QuadCurve2D.Double)this.theObject;
            dataArray = new double[]{qd.x1, qd.y1, qd.ctrlx, qd.ctrly, qd.x2, qd.y2};
            type = 11;
        } else if (this.theObject instanceof QuadCurve2D.Float) {
            QuadCurve2D.Float qf = (QuadCurve2D.Float)this.theObject;
            dataArray = new float[]{qf.x1, qf.y1, qf.ctrlx, qf.ctrly, qf.x2, qf.y2};
            type = 12;
        } else if (this.theObject instanceof RoundRectangle2D.Double) {
            RoundRectangle2D.Double rrd = (RoundRectangle2D.Double)this.theObject;
            dataArray = new double[]{rrd.x, rrd.y, rrd.width, rrd.height, rrd.arcwidth, rrd.archeight};
            type = 13;
        } else if (this.theObject instanceof RoundRectangle2D.Float) {
            RoundRectangle2D.Float rrf = (RoundRectangle2D.Float)this.theObject;
            dataArray = new float[]{rrf.x, rrf.y, rrf.width, rrf.height, rrf.arcwidth, rrf.archeight};
            type = 14;
        } else if (this.theObject instanceof Rectangle2D.Double) {
            Rectangle2D.Double rd = (Rectangle2D.Double)this.theObject;
            dataArray = new double[]{rd.x, rd.y, rd.width, rd.height};
            type = 15;
        } else if (this.theObject instanceof Rectangle2D.Float) {
            Rectangle2D.Float rf = (Rectangle2D.Float)this.theObject;
            dataArray = new float[]{rf.x, rf.y, rf.width, rf.height};
            type = 16;
        }
        out.writeInt(type);
        if (dataArray != null) {
            out.writeObject(dataArray);
            if (otherData != null) {
                out.writeObject(otherData);
            }
            return;
        }
        PathIterator pathIterator = ((Shape)this.theObject).getPathIterator(null);
        int rule = pathIterator.getWindingRule();
        out.writeInt(rule);
        float[] coordinates = new float[6];
        boolean isDone = pathIterator.isDone();
        while (!isDone) {
            int segmentType = pathIterator.currentSegment(coordinates);
            out.writeBoolean(isDone);
            out.writeInt(segmentType);
            for (int i = 0; i < 6; ++i) {
                out.writeFloat(coordinates[i]);
            }
            pathIterator.next();
            isDone = pathIterator.isDone();
        }
        out.writeBoolean(isDone);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        boolean serializable = in.readBoolean();
        if (serializable) {
            this.theObject = in.readObject();
            return;
        }
        int type = in.readInt();
        switch (type) {
            case 2: {
                double[] data = (double[])in.readObject();
                int arcType = (Integer)in.readObject();
                this.theObject = new Arc2D.Double(data[0], data[1], data[2], data[3], data[4], data[5], arcType);
                return;
            }
            case 3: {
                float[] data = (float[])in.readObject();
                int arcType = (Integer)in.readObject();
                this.theObject = new Arc2D.Float(data[0], data[1], data[2], data[3], data[4], data[5], arcType);
                return;
            }
            case 4: {
                double[] data = (double[])in.readObject();
                this.theObject = new CubicCurve2D.Double(data[0], data[1], data[2], data[3], data[4], data[5], data[6], data[7]);
                return;
            }
            case 5: {
                float[] data = (float[])in.readObject();
                this.theObject = new CubicCurve2D.Float(data[0], data[1], data[2], data[3], data[4], data[5], data[6], data[7]);
                return;
            }
            case 6: {
                double[] data = (double[])in.readObject();
                this.theObject = new Ellipse2D.Double(data[0], data[1], data[2], data[3]);
                return;
            }
            case 7: {
                float[] data = (float[])in.readObject();
                this.theObject = new Ellipse2D.Float(data[0], data[1], data[2], data[3]);
                return;
            }
            case 9: {
                double[] data = (double[])in.readObject();
                this.theObject = new Line2D.Double(data[0], data[1], data[2], data[3]);
                return;
            }
            case 10: {
                float[] data = (float[])in.readObject();
                this.theObject = new Line2D.Float(data[0], data[1], data[2], data[3]);
                return;
            }
            case 11: {
                double[] data = (double[])in.readObject();
                this.theObject = new QuadCurve2D.Double(data[0], data[1], data[2], data[3], data[4], data[5]);
                return;
            }
            case 12: {
                float[] data = (float[])in.readObject();
                this.theObject = new QuadCurve2D.Float(data[0], data[1], data[2], data[3], data[4], data[5]);
                return;
            }
            case 13: {
                double[] data = (double[])in.readObject();
                this.theObject = new RoundRectangle2D.Double(data[0], data[1], data[2], data[3], data[4], data[5]);
                return;
            }
            case 14: {
                float[] data = (float[])in.readObject();
                this.theObject = new RoundRectangle2D.Float(data[0], data[1], data[2], data[3], data[4], data[5]);
                return;
            }
            case 15: {
                double[] data = (double[])in.readObject();
                this.theObject = new Rectangle2D.Double(data[0], data[1], data[2], data[3]);
                return;
            }
            case 16: {
                float[] data = (float[])in.readObject();
                this.theObject = new Rectangle2D.Float(data[0], data[1], data[2], data[3]);
                return;
            }
        }
        int rule = in.readInt();
        GeneralPath path = new GeneralPath(rule);
        float[] coordinates = new float[6];
        while (!in.readBoolean()) {
            int segmentType = in.readInt();
            for (int i = 0; i < 6; ++i) {
                coordinates[i] = in.readFloat();
            }
            switch (segmentType) {
                case 0: {
                    path.moveTo(coordinates[0], coordinates[1]);
                    break;
                }
                case 1: {
                    path.lineTo(coordinates[0], coordinates[1]);
                    break;
                }
                case 2: {
                    path.quadTo(coordinates[0], coordinates[1], coordinates[2], coordinates[3]);
                    break;
                }
                case 3: {
                    path.curveTo(coordinates[0], coordinates[1], coordinates[2], coordinates[3], coordinates[4], coordinates[5]);
                    break;
                }
                case 4: {
                    path.closePath();
                    break;
                }
            }
        }
        switch (type) {
            case 1: {
                this.theObject = new Area(path);
                break;
            }
            default: {
                this.theObject = path;
            }
        }
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

