/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hslf.usermodel;

import java.awt.Color;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ddf.AbstractEscherOptRecord;
import org.apache.poi.ddf.EscherChildAnchorRecord;
import org.apache.poi.ddf.EscherClientAnchorRecord;
import org.apache.poi.ddf.EscherContainerRecord;
import org.apache.poi.ddf.EscherOptRecord;
import org.apache.poi.ddf.EscherPropertyTypes;
import org.apache.poi.ddf.EscherRecord;
import org.apache.poi.ddf.EscherSimpleProperty;
import org.apache.poi.ddf.EscherSpRecord;
import org.apache.poi.hslf.exceptions.HSLFException;
import org.apache.poi.hslf.usermodel.HSLFHyperlink;
import org.apache.poi.hslf.usermodel.HSLFShape;
import org.apache.poi.hslf.usermodel.HSLFShapePlaceholderDetails;
import org.apache.poi.hslf.usermodel.HSLFTextParagraph;
import org.apache.poi.sl.draw.DrawPaint;
import org.apache.poi.sl.draw.geom.CustomGeometry;
import org.apache.poi.sl.draw.geom.Guide;
import org.apache.poi.sl.draw.geom.PresetGeometries;
import org.apache.poi.sl.usermodel.LineDecoration;
import org.apache.poi.sl.usermodel.PaintStyle;
import org.apache.poi.sl.usermodel.Placeholder;
import org.apache.poi.sl.usermodel.Shadow;
import org.apache.poi.sl.usermodel.ShapeContainer;
import org.apache.poi.sl.usermodel.ShapeType;
import org.apache.poi.sl.usermodel.SimpleShape;
import org.apache.poi.sl.usermodel.StrokeStyle;
import org.apache.poi.util.LittleEndian;
import org.apache.poi.util.Units;

public abstract class HSLFSimpleShape
extends HSLFShape
implements SimpleShape<HSLFShape, HSLFTextParagraph> {
    private static final Logger LOG = LogManager.getLogger(HSLFSimpleShape.class);
    public static final double DEFAULT_LINE_WIDTH = 0.75;
    protected static final EscherPropertyTypes[] ADJUST_VALUES = new EscherPropertyTypes[]{EscherPropertyTypes.GEOMETRY__ADJUSTVALUE, EscherPropertyTypes.GEOMETRY__ADJUST2VALUE, EscherPropertyTypes.GEOMETRY__ADJUST3VALUE, EscherPropertyTypes.GEOMETRY__ADJUST4VALUE, EscherPropertyTypes.GEOMETRY__ADJUST5VALUE, EscherPropertyTypes.GEOMETRY__ADJUST6VALUE, EscherPropertyTypes.GEOMETRY__ADJUST7VALUE, EscherPropertyTypes.GEOMETRY__ADJUST8VALUE, EscherPropertyTypes.GEOMETRY__ADJUST9VALUE, EscherPropertyTypes.GEOMETRY__ADJUST10VALUE};
    protected HSLFHyperlink _hyperlink;

    protected HSLFSimpleShape(EscherContainerRecord escherRecord, ShapeContainer<HSLFShape, HSLFTextParagraph> parent) {
        super(escherRecord, parent);
    }

    @Override
    protected EscherContainerRecord createSpContainer(boolean isChild) {
        EscherRecord anchor;
        EscherContainerRecord ecr = super.createSpContainer(isChild);
        ecr.setRecordId(EscherContainerRecord.SP_CONTAINER);
        EscherSpRecord sp = new EscherSpRecord();
        int flags = 2560;
        if (isChild) {
            flags |= 2;
        }
        sp.setFlags(flags);
        ecr.addChildRecord(sp);
        EscherOptRecord opt = new EscherOptRecord();
        opt.setRecordId(EscherOptRecord.RECORD_ID);
        ecr.addChildRecord(opt);
        if (isChild) {
            anchor = new EscherChildAnchorRecord();
        } else {
            anchor = new EscherClientAnchorRecord();
            byte[] header = new byte[16];
            LittleEndian.putUShort(header, 0, 0);
            LittleEndian.putUShort(header, 2, 0);
            LittleEndian.putInt(header, 4, 8);
            anchor.fillFields(header, 0, null);
        }
        ecr.addChildRecord(anchor);
        return ecr;
    }

    public double getLineWidth() {
        AbstractEscherOptRecord opt = this.getEscherOptRecord();
        EscherSimpleProperty prop = (EscherSimpleProperty)HSLFSimpleShape.getEscherProperty(opt, EscherPropertyTypes.LINESTYLE__LINEWIDTH);
        return prop == null ? 0.75 : Units.toPoints(prop.getPropertyValue());
    }

    public void setLineWidth(double width) {
        AbstractEscherOptRecord opt = this.getEscherOptRecord();
        HSLFSimpleShape.setEscherProperty(opt, EscherPropertyTypes.LINESTYLE__LINEWIDTH, Units.toEMU(width));
    }

    public void setLineColor(Color color) {
        AbstractEscherOptRecord opt = this.getEscherOptRecord();
        if (color == null) {
            HSLFSimpleShape.setEscherProperty(opt, EscherPropertyTypes.LINESTYLE__NOLINEDRAWDASH, 524288);
        } else {
            int rgb = new Color(color.getBlue(), color.getGreen(), color.getRed(), 0).getRGB();
            HSLFSimpleShape.setEscherProperty(opt, EscherPropertyTypes.LINESTYLE__COLOR, rgb);
            HSLFSimpleShape.setEscherProperty(opt, EscherPropertyTypes.LINESTYLE__NOLINEDRAWDASH, 0x180018);
        }
    }

    public Color getLineColor() {
        AbstractEscherOptRecord opt = this.getEscherOptRecord();
        EscherSimpleProperty p = (EscherSimpleProperty)HSLFSimpleShape.getEscherProperty(opt, EscherPropertyTypes.LINESTYLE__NOLINEDRAWDASH);
        if (p != null && (p.getPropertyValue() & 8) == 0) {
            return null;
        }
        return this.getColor(EscherPropertyTypes.LINESTYLE__COLOR, EscherPropertyTypes.LINESTYLE__OPACITY);
    }

    public Color getLineBackgroundColor() {
        AbstractEscherOptRecord opt = this.getEscherOptRecord();
        EscherSimpleProperty p = (EscherSimpleProperty)HSLFSimpleShape.getEscherProperty(opt, EscherPropertyTypes.LINESTYLE__NOLINEDRAWDASH);
        if (p != null && (p.getPropertyValue() & 8) == 0) {
            return null;
        }
        return this.getColor(EscherPropertyTypes.LINESTYLE__BACKCOLOR, EscherPropertyTypes.LINESTYLE__OPACITY);
    }

    public void setLineBackgroundColor(Color color) {
        AbstractEscherOptRecord opt = this.getEscherOptRecord();
        if (color == null) {
            HSLFSimpleShape.setEscherProperty(opt, EscherPropertyTypes.LINESTYLE__NOLINEDRAWDASH, 524288);
            opt.removeEscherProperty(EscherPropertyTypes.LINESTYLE__BACKCOLOR);
        } else {
            int rgb = new Color(color.getBlue(), color.getGreen(), color.getRed(), 0).getRGB();
            HSLFSimpleShape.setEscherProperty(opt, EscherPropertyTypes.LINESTYLE__BACKCOLOR, rgb);
            HSLFSimpleShape.setEscherProperty(opt, EscherPropertyTypes.LINESTYLE__NOLINEDRAWDASH, 0x180018);
        }
    }

    public StrokeStyle.LineCap getLineCap() {
        AbstractEscherOptRecord opt = this.getEscherOptRecord();
        EscherSimpleProperty prop = (EscherSimpleProperty)HSLFSimpleShape.getEscherProperty(opt, EscherPropertyTypes.LINESTYLE__LINEENDCAPSTYLE);
        return prop == null ? StrokeStyle.LineCap.FLAT : StrokeStyle.LineCap.fromNativeId(prop.getPropertyValue());
    }

    public void setLineCap(StrokeStyle.LineCap pen) {
        AbstractEscherOptRecord opt = this.getEscherOptRecord();
        HSLFSimpleShape.setEscherProperty(opt, EscherPropertyTypes.LINESTYLE__LINEENDCAPSTYLE, pen == StrokeStyle.LineCap.FLAT ? -1 : pen.nativeId);
    }

    public StrokeStyle.LineDash getLineDash() {
        AbstractEscherOptRecord opt = this.getEscherOptRecord();
        EscherSimpleProperty prop = (EscherSimpleProperty)HSLFSimpleShape.getEscherProperty(opt, EscherPropertyTypes.LINESTYLE__LINEDASHING);
        return prop == null ? StrokeStyle.LineDash.SOLID : StrokeStyle.LineDash.fromNativeId(prop.getPropertyValue());
    }

    public void setLineDash(StrokeStyle.LineDash pen) {
        AbstractEscherOptRecord opt = this.getEscherOptRecord();
        HSLFSimpleShape.setEscherProperty(opt, EscherPropertyTypes.LINESTYLE__LINEDASHING, pen == StrokeStyle.LineDash.SOLID ? -1 : pen.nativeId);
    }

    public StrokeStyle.LineCompound getLineCompound() {
        AbstractEscherOptRecord opt = this.getEscherOptRecord();
        EscherSimpleProperty prop = (EscherSimpleProperty)HSLFSimpleShape.getEscherProperty(opt, EscherPropertyTypes.LINESTYLE__LINESTYLE);
        return prop == null ? StrokeStyle.LineCompound.SINGLE : StrokeStyle.LineCompound.fromNativeId(prop.getPropertyValue());
    }

    public void setLineCompound(StrokeStyle.LineCompound style) {
        AbstractEscherOptRecord opt = this.getEscherOptRecord();
        HSLFSimpleShape.setEscherProperty(opt, EscherPropertyTypes.LINESTYLE__LINESTYLE, style == StrokeStyle.LineCompound.SINGLE ? -1 : style.nativeId);
    }

    @Override
    public StrokeStyle getStrokeStyle() {
        return new StrokeStyle(){

            @Override
            public PaintStyle getPaint() {
                return DrawPaint.createSolidPaint(HSLFSimpleShape.this.getLineColor());
            }

            @Override
            public StrokeStyle.LineCap getLineCap() {
                return null;
            }

            @Override
            public StrokeStyle.LineDash getLineDash() {
                return HSLFSimpleShape.this.getLineDash();
            }

            @Override
            public StrokeStyle.LineCompound getLineCompound() {
                return HSLFSimpleShape.this.getLineCompound();
            }

            @Override
            public double getLineWidth() {
                return HSLFSimpleShape.this.getLineWidth();
            }
        };
    }

    @Override
    public Color getFillColor() {
        return this.getFill().getForegroundColor();
    }

    @Override
    public void setFillColor(Color color) {
        this.getFill().setForegroundColor(color);
    }

    @Override
    public Guide getAdjustValue(String name) {
        int adjInt;
        if (name == null || !name.matches("adj([1-9]|10)?")) {
            LOG.atInfo().log("Adjust value '{}' not supported. Using default value.", (Object)name);
            return null;
        }
        if ((name = name.replace("adj", "")).isEmpty()) {
            name = "1";
        }
        if ((adjInt = Integer.parseInt(name)) < 1 || adjInt > 10) {
            throw new HSLFException("invalid adjust value: " + adjInt);
        }
        EscherPropertyTypes escherProp = ADJUST_VALUES[adjInt - 1];
        int adjval = this.getEscherProperty(escherProp, -1);
        if (adjval == -1) {
            return null;
        }
        boolean isDegreeUnit = false;
        switch (this.getShapeType()) {
            case ARC: 
            case BLOCK_ARC: 
            case CHORD: 
            case PIE: {
                isDegreeUnit = adjInt == 1 || adjInt == 2;
                break;
            }
            case CIRCULAR_ARROW: 
            case LEFT_CIRCULAR_ARROW: 
            case LEFT_RIGHT_CIRCULAR_ARROW: {
                isDegreeUnit = adjInt == 2 || adjInt == 3 || adjInt == 4;
                break;
            }
            case MATH_NOT_EQUAL: {
                isDegreeUnit = adjInt == 2;
            }
        }
        Guide gd = new Guide();
        gd.setName(name);
        gd.setFmla("val " + Math.rint((double)adjval * (isDegreeUnit ? 65536.0 : 4.761904761904762)));
        return gd;
    }

    @Override
    public CustomGeometry getGeometry() {
        ShapeType st;
        String name;
        PresetGeometries dict = PresetGeometries.getInstance();
        CustomGeometry geom = dict.get(name = (st = this.getShapeType()) != null ? st.getOoxmlName() : null);
        if (geom == null) {
            if (name == null) {
                name = st != null ? st.toString() : "<unknown>";
            }
            LOG.atWarn().log("No preset shape definition for shapeType: {}", (Object)name);
        }
        return geom;
    }

    public double getShadowAngle() {
        AbstractEscherOptRecord opt = this.getEscherOptRecord();
        EscherSimpleProperty prop = (EscherSimpleProperty)HSLFSimpleShape.getEscherProperty(opt, EscherPropertyTypes.SHADOWSTYLE__OFFSETX);
        int offX = prop == null ? 0 : prop.getPropertyValue();
        prop = (EscherSimpleProperty)HSLFSimpleShape.getEscherProperty(opt, EscherPropertyTypes.SHADOWSTYLE__OFFSETY);
        int offY = prop == null ? 0 : prop.getPropertyValue();
        return Math.toDegrees(Math.atan2(offY, offX));
    }

    public double getShadowDistance() {
        AbstractEscherOptRecord opt = this.getEscherOptRecord();
        EscherSimpleProperty prop = (EscherSimpleProperty)HSLFSimpleShape.getEscherProperty(opt, EscherPropertyTypes.SHADOWSTYLE__OFFSETX);
        int offX = prop == null ? 0 : prop.getPropertyValue();
        prop = (EscherSimpleProperty)HSLFSimpleShape.getEscherProperty(opt, EscherPropertyTypes.SHADOWSTYLE__OFFSETY);
        int offY = prop == null ? 0 : prop.getPropertyValue();
        return Units.toPoints((long)Math.hypot(offX, offY));
    }

    public Color getShadowColor() {
        Color clr = this.getColor(EscherPropertyTypes.SHADOWSTYLE__COLOR, EscherPropertyTypes.SHADOWSTYLE__OPACITY);
        return clr == null ? Color.black : clr;
    }

    @Override
    public Shadow<HSLFShape, HSLFTextParagraph> getShadow() {
        AbstractEscherOptRecord opt = this.getEscherOptRecord();
        if (opt == null) {
            return null;
        }
        Object shadowType = opt.lookup(EscherPropertyTypes.SHADOWSTYLE__TYPE);
        if (shadowType == null) {
            return null;
        }
        return new Shadow<HSLFShape, HSLFTextParagraph>(){

            @Override
            public SimpleShape<HSLFShape, HSLFTextParagraph> getShadowParent() {
                return HSLFSimpleShape.this;
            }

            @Override
            public double getDistance() {
                return HSLFSimpleShape.this.getShadowDistance();
            }

            @Override
            public double getAngle() {
                return HSLFSimpleShape.this.getShadowAngle();
            }

            @Override
            public double getBlur() {
                return 0.0;
            }

            @Override
            public PaintStyle.SolidPaint getFillStyle() {
                return DrawPaint.createSolidPaint(HSLFSimpleShape.this.getShadowColor());
            }
        };
    }

    public LineDecoration.DecorationShape getLineHeadDecoration() {
        AbstractEscherOptRecord opt = this.getEscherOptRecord();
        EscherSimpleProperty prop = (EscherSimpleProperty)HSLFSimpleShape.getEscherProperty(opt, EscherPropertyTypes.LINESTYLE__LINESTARTARROWHEAD);
        return prop == null ? null : LineDecoration.DecorationShape.fromNativeId(prop.getPropertyValue());
    }

    public void setLineHeadDecoration(LineDecoration.DecorationShape decoShape) {
        AbstractEscherOptRecord opt = this.getEscherOptRecord();
        HSLFSimpleShape.setEscherProperty(opt, EscherPropertyTypes.LINESTYLE__LINESTARTARROWHEAD, decoShape == null ? -1 : decoShape.nativeId);
    }

    public LineDecoration.DecorationSize getLineHeadWidth() {
        AbstractEscherOptRecord opt = this.getEscherOptRecord();
        EscherSimpleProperty prop = (EscherSimpleProperty)HSLFSimpleShape.getEscherProperty(opt, EscherPropertyTypes.LINESTYLE__LINESTARTARROWWIDTH);
        return prop == null ? null : LineDecoration.DecorationSize.fromNativeId(prop.getPropertyValue());
    }

    public void setLineHeadWidth(LineDecoration.DecorationSize decoSize) {
        AbstractEscherOptRecord opt = this.getEscherOptRecord();
        HSLFSimpleShape.setEscherProperty(opt, EscherPropertyTypes.LINESTYLE__LINESTARTARROWWIDTH, decoSize == null ? -1 : decoSize.nativeId);
    }

    public LineDecoration.DecorationSize getLineHeadLength() {
        AbstractEscherOptRecord opt = this.getEscherOptRecord();
        EscherSimpleProperty prop = (EscherSimpleProperty)HSLFSimpleShape.getEscherProperty(opt, EscherPropertyTypes.LINESTYLE__LINESTARTARROWLENGTH);
        return prop == null ? null : LineDecoration.DecorationSize.fromNativeId(prop.getPropertyValue());
    }

    public void setLineHeadLength(LineDecoration.DecorationSize decoSize) {
        AbstractEscherOptRecord opt = this.getEscherOptRecord();
        HSLFSimpleShape.setEscherProperty(opt, EscherPropertyTypes.LINESTYLE__LINESTARTARROWLENGTH, decoSize == null ? -1 : decoSize.nativeId);
    }

    public LineDecoration.DecorationShape getLineTailDecoration() {
        AbstractEscherOptRecord opt = this.getEscherOptRecord();
        EscherSimpleProperty prop = (EscherSimpleProperty)HSLFSimpleShape.getEscherProperty(opt, EscherPropertyTypes.LINESTYLE__LINEENDARROWHEAD);
        return prop == null ? null : LineDecoration.DecorationShape.fromNativeId(prop.getPropertyValue());
    }

    public void setLineTailDecoration(LineDecoration.DecorationShape decoShape) {
        AbstractEscherOptRecord opt = this.getEscherOptRecord();
        HSLFSimpleShape.setEscherProperty(opt, EscherPropertyTypes.LINESTYLE__LINEENDARROWHEAD, decoShape == null ? -1 : decoShape.nativeId);
    }

    public LineDecoration.DecorationSize getLineTailWidth() {
        AbstractEscherOptRecord opt = this.getEscherOptRecord();
        EscherSimpleProperty prop = (EscherSimpleProperty)HSLFSimpleShape.getEscherProperty(opt, EscherPropertyTypes.LINESTYLE__LINEENDARROWWIDTH);
        return prop == null ? null : LineDecoration.DecorationSize.fromNativeId(prop.getPropertyValue());
    }

    public void setLineTailWidth(LineDecoration.DecorationSize decoSize) {
        AbstractEscherOptRecord opt = this.getEscherOptRecord();
        HSLFSimpleShape.setEscherProperty(opt, EscherPropertyTypes.LINESTYLE__LINEENDARROWWIDTH, decoSize == null ? -1 : decoSize.nativeId);
    }

    public LineDecoration.DecorationSize getLineTailLength() {
        AbstractEscherOptRecord opt = this.getEscherOptRecord();
        EscherSimpleProperty prop = (EscherSimpleProperty)HSLFSimpleShape.getEscherProperty(opt, EscherPropertyTypes.LINESTYLE__LINEENDARROWLENGTH);
        return prop == null ? null : LineDecoration.DecorationSize.fromNativeId(prop.getPropertyValue());
    }

    public void setLineTailLength(LineDecoration.DecorationSize decoSize) {
        AbstractEscherOptRecord opt = this.getEscherOptRecord();
        HSLFSimpleShape.setEscherProperty(opt, EscherPropertyTypes.LINESTYLE__LINEENDARROWLENGTH, decoSize == null ? -1 : decoSize.nativeId);
    }

    @Override
    public LineDecoration getLineDecoration() {
        return new LineDecoration(){

            @Override
            public LineDecoration.DecorationShape getHeadShape() {
                return HSLFSimpleShape.this.getLineHeadDecoration();
            }

            @Override
            public LineDecoration.DecorationSize getHeadWidth() {
                return HSLFSimpleShape.this.getLineHeadWidth();
            }

            @Override
            public LineDecoration.DecorationSize getHeadLength() {
                return HSLFSimpleShape.this.getLineHeadLength();
            }

            @Override
            public LineDecoration.DecorationShape getTailShape() {
                return HSLFSimpleShape.this.getLineTailDecoration();
            }

            @Override
            public LineDecoration.DecorationSize getTailWidth() {
                return HSLFSimpleShape.this.getLineTailWidth();
            }

            @Override
            public LineDecoration.DecorationSize getTailLength() {
                return HSLFSimpleShape.this.getLineTailLength();
            }
        };
    }

    @Override
    public HSLFShapePlaceholderDetails getPlaceholderDetails() {
        return new HSLFShapePlaceholderDetails(this);
    }

    @Override
    public Placeholder getPlaceholder() {
        return this.getPlaceholderDetails().getPlaceholder();
    }

    @Override
    public void setPlaceholder(Placeholder placeholder) {
        this.getPlaceholderDetails().setPlaceholder(placeholder);
    }

    @Override
    public void setStrokeStyle(Object ... styles) {
        if (styles.length == 0) {
            this.setLineColor(null);
            return;
        }
        for (Object st : styles) {
            if (st instanceof Number) {
                this.setLineWidth(((Number)st).doubleValue());
                continue;
            }
            if (st instanceof StrokeStyle.LineCap) {
                this.setLineCap((StrokeStyle.LineCap)((Object)st));
                continue;
            }
            if (st instanceof StrokeStyle.LineDash) {
                this.setLineDash((StrokeStyle.LineDash)((Object)st));
                continue;
            }
            if (st instanceof StrokeStyle.LineCompound) {
                this.setLineCompound((StrokeStyle.LineCompound)((Object)st));
                continue;
            }
            if (!(st instanceof Color)) continue;
            this.setLineColor((Color)st);
        }
    }

    public HSLFHyperlink getHyperlink() {
        return this._hyperlink;
    }

    public HSLFHyperlink createHyperlink() {
        if (this._hyperlink == null) {
            this._hyperlink = HSLFHyperlink.createHyperlink(this);
        }
        return this._hyperlink;
    }

    protected void setHyperlink(HSLFHyperlink link) {
        this._hyperlink = link;
    }

    @Override
    public boolean isPlaceholder() {
        return false;
    }
}

