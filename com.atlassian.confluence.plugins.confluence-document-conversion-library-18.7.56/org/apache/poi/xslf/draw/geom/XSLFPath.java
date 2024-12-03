/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.xslf.draw.geom;

import java.awt.geom.Path2D;
import org.apache.poi.sl.draw.geom.ClosePathCommand;
import org.apache.poi.sl.draw.geom.Context;
import org.apache.poi.sl.draw.geom.PathCommand;
import org.apache.poi.sl.draw.geom.PathIf;
import org.apache.poi.sl.usermodel.PaintStyle;
import org.apache.poi.xslf.draw.geom.XSLFArcTo;
import org.apache.poi.xslf.draw.geom.XSLFCurveTo;
import org.apache.poi.xslf.draw.geom.XSLFLineTo;
import org.apache.poi.xslf.draw.geom.XSLFMoveTo;
import org.apache.poi.xslf.draw.geom.XSLFQuadTo;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPath2D;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPath2DArcTo;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPath2DClose;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPath2DCubicBezierTo;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPath2DLineTo;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPath2DMoveTo;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPath2DQuadBezierTo;
import org.openxmlformats.schemas.drawingml.x2006.main.STPathFillMode;

public class XSLFPath
implements PathIf {
    private final CTPath2D pathXml;

    public XSLFPath(CTPath2D pathXml) {
        this.pathXml = pathXml;
    }

    @Override
    public void addCommand(PathCommand cmd) {
    }

    @Override
    public Path2D.Double getPath(Context ctx) {
        Path2D.Double path2D = new Path2D.Double();
        try (XmlCursor cur = this.pathXml.newCursor();){
            boolean hasNext = cur.toFirstChild();
            while (hasNext) {
                block23: {
                    PathCommand pc;
                    block18: {
                        XmlObject xo;
                        block22: {
                            block21: {
                                block20: {
                                    block19: {
                                        block17: {
                                            xo = cur.getObject();
                                            if (!(xo instanceof CTPath2DArcTo)) break block17;
                                            pc = new XSLFArcTo((CTPath2DArcTo)xo);
                                            break block18;
                                        }
                                        if (!(xo instanceof CTPath2DCubicBezierTo)) break block19;
                                        pc = new XSLFCurveTo((CTPath2DCubicBezierTo)xo);
                                        break block18;
                                    }
                                    if (!(xo instanceof CTPath2DMoveTo)) break block20;
                                    pc = new XSLFMoveTo((CTPath2DMoveTo)xo);
                                    break block18;
                                }
                                if (!(xo instanceof CTPath2DLineTo)) break block21;
                                pc = new XSLFLineTo((CTPath2DLineTo)xo);
                                break block18;
                            }
                            if (!(xo instanceof CTPath2DQuadBezierTo)) break block22;
                            pc = new XSLFQuadTo((CTPath2DQuadBezierTo)xo);
                            break block18;
                        }
                        if (!(xo instanceof CTPath2DClose)) break block23;
                        pc = new ClosePathCommand();
                    }
                    pc.execute(path2D, ctx);
                }
                hasNext = cur.toNextSibling();
            }
        }
        return path2D;
    }

    @Override
    public boolean isStroked() {
        return this.pathXml.getStroke();
    }

    @Override
    public void setStroke(boolean stroke) {
        this.pathXml.setStroke(stroke);
    }

    @Override
    public boolean isFilled() {
        return this.pathXml.getFill() != STPathFillMode.NONE;
    }

    @Override
    public PaintStyle.PaintModifier getFill() {
        switch (this.pathXml.getFill().intValue()) {
            default: {
                return PaintStyle.PaintModifier.NONE;
            }
            case 2: {
                return PaintStyle.PaintModifier.NORM;
            }
            case 3: {
                return PaintStyle.PaintModifier.LIGHTEN;
            }
            case 4: {
                return PaintStyle.PaintModifier.LIGHTEN_LESS;
            }
            case 5: {
                return PaintStyle.PaintModifier.DARKEN;
            }
            case 6: 
        }
        return PaintStyle.PaintModifier.DARKEN_LESS;
    }

    @Override
    public void setFill(PaintStyle.PaintModifier fill) {
        STPathFillMode.Enum f;
        switch (fill) {
            default: {
                f = STPathFillMode.NONE;
                break;
            }
            case NORM: {
                f = STPathFillMode.NORM;
                break;
            }
            case LIGHTEN: {
                f = STPathFillMode.LIGHTEN;
                break;
            }
            case LIGHTEN_LESS: {
                f = STPathFillMode.LIGHTEN_LESS;
                break;
            }
            case DARKEN: {
                f = STPathFillMode.DARKEN;
                break;
            }
            case DARKEN_LESS: {
                f = STPathFillMode.DARKEN_LESS;
            }
        }
        this.pathXml.setFill(f);
    }

    @Override
    public long getW() {
        return this.pathXml.getW();
    }

    @Override
    public void setW(long w) {
        this.pathXml.setW(w);
    }

    @Override
    public long getH() {
        return this.pathXml.getH();
    }

    @Override
    public void setH(long h) {
        this.pathXml.setH(h);
    }

    @Override
    public boolean isExtrusionOk() {
        return this.pathXml.getExtrusionOk();
    }

    @Override
    public void setExtrusionOk(boolean extrusionOk) {
        this.pathXml.setExtrusionOk(extrusionOk);
    }
}

