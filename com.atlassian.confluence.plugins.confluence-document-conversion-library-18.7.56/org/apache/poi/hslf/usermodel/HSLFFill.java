/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hslf.usermodel;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Unbox;
import org.apache.poi.ddf.AbstractEscherOptRecord;
import org.apache.poi.ddf.EscherArrayProperty;
import org.apache.poi.ddf.EscherBSERecord;
import org.apache.poi.ddf.EscherColorRef;
import org.apache.poi.ddf.EscherContainerRecord;
import org.apache.poi.ddf.EscherPropertyTypes;
import org.apache.poi.ddf.EscherRecordTypes;
import org.apache.poi.ddf.EscherSimpleProperty;
import org.apache.poi.hslf.record.Document;
import org.apache.poi.hslf.usermodel.HSLFPictureData;
import org.apache.poi.hslf.usermodel.HSLFShape;
import org.apache.poi.hslf.usermodel.HSLFSheet;
import org.apache.poi.hslf.usermodel.HSLFSlideShow;
import org.apache.poi.sl.draw.DrawPaint;
import org.apache.poi.sl.usermodel.ColorStyle;
import org.apache.poi.sl.usermodel.FillStyle;
import org.apache.poi.sl.usermodel.PaintStyle;
import org.apache.poi.sl.usermodel.Shape;
import org.apache.poi.util.BitField;
import org.apache.poi.util.BitFieldFactory;
import org.apache.poi.util.LittleEndian;
import org.apache.poi.util.Units;

public final class HSLFFill {
    private static final Logger LOG = LogManager.getLogger(HSLFFill.class);
    static final int FILL_SOLID = 0;
    static final int FILL_PATTERN = 1;
    static final int FILL_TEXTURE = 2;
    static final int FILL_PICTURE = 3;
    static final int FILL_SHADE = 4;
    static final int FILL_SHADE_CENTER = 5;
    static final int FILL_SHADE_SHAPE = 6;
    static final int FILL_SHADE_SCALE = 7;
    static final int FILL_SHADE_TITLE = 8;
    static final int FILL_BACKGROUND = 9;
    private static final BitField FILL_USE_RECOLOR_FILL_AS_PICTURE = BitFieldFactory.getInstance(0x400000);
    private static final BitField FILL_USE_USE_SHAPE_ANCHOR = BitFieldFactory.getInstance(0x200000);
    private static final BitField FILL_USE_FILLED = BitFieldFactory.getInstance(0x100000);
    private static final BitField FILL_USE_HIT_TEST_FILL = BitFieldFactory.getInstance(524288);
    private static final BitField FILL_USE_FILL_SHAPE = BitFieldFactory.getInstance(262144);
    private static final BitField FILL_USE_FILL_USE_RECT = BitFieldFactory.getInstance(131072);
    private static final BitField FILL_USE_NO_FILL_HIT_TEST = BitFieldFactory.getInstance(65536);
    private static final BitField FILL_RECOLOR_FILL_AS_PICTURE = BitFieldFactory.getInstance(64);
    private static final BitField FILL_USE_SHAPE_ANCHOR = BitFieldFactory.getInstance(32);
    private static final BitField FILL_FILLED = BitFieldFactory.getInstance(16);
    private static final BitField FILL_HIT_TEST_FILL = BitFieldFactory.getInstance(8);
    private static final BitField FILL_FILL_SHAPE = BitFieldFactory.getInstance(4);
    private static final BitField FILL_FILL_USE_RECT = BitFieldFactory.getInstance(2);
    private static final BitField FILL_NO_FILL_HIT_TEST = BitFieldFactory.getInstance(1);
    private HSLFShape shape;

    public HSLFFill(HSLFShape shape) {
        this.shape = shape;
    }

    public FillStyle getFillStyle() {
        return this::getPaintStyle;
    }

    private PaintStyle getPaintStyle() {
        AbstractEscherOptRecord opt = this.shape.getEscherOptRecord();
        EscherSimpleProperty hitProp = (EscherSimpleProperty)HSLFShape.getEscherProperty(opt, EscherPropertyTypes.FILL__NOFILLHITTEST);
        int propVal = hitProp == null ? 0 : hitProp.getPropertyValue();
        EscherSimpleProperty masterProp = (EscherSimpleProperty)HSLFShape.getEscherProperty(opt, EscherPropertyTypes.SHAPE__MASTER);
        if (!FILL_USE_FILLED.isSet(propVal) && masterProp != null) {
            int masterId = masterProp.getPropertyValue();
            HSLFShape o = this.shape.getSheet().getMasterSheet().getShapes().stream().filter(s -> s.getShapeId() == masterId).findFirst().orElse(null);
            return o != null ? o.getFillStyle().getPaint() : null;
        }
        int fillType = this.getFillType();
        switch (fillType) {
            case 9: {
                return DrawPaint.createSolidPaint(this.getBackgroundColor());
            }
            case 0: {
                return DrawPaint.createSolidPaint(this.getForegroundColor());
            }
            case 6: {
                return this.getGradientPaint(PaintStyle.GradientPaint.GradientType.shape);
            }
            case 5: 
            case 8: {
                return this.getGradientPaint(PaintStyle.GradientPaint.GradientType.circular);
            }
            case 4: 
            case 7: {
                return this.getGradientPaint(PaintStyle.GradientPaint.GradientType.linear);
            }
            case 2: 
            case 3: {
                return this.getTexturePaint();
            }
        }
        LOG.atWarn().log("unsupported fill type: {}", (Object)Unbox.box(fillType));
        return null;
    }

    private boolean isRotatedWithShape() {
        AbstractEscherOptRecord opt = (AbstractEscherOptRecord)this.shape.getEscherChild(EscherRecordTypes.USER_DEFINED);
        EscherSimpleProperty p = (EscherSimpleProperty)HSLFShape.getEscherProperty(opt, EscherPropertyTypes.FILL__NOFILLHITTEST);
        int propVal = p == null ? 0 : p.getPropertyValue();
        return FILL_USE_USE_SHAPE_ANCHOR.isSet(propVal) && FILL_USE_SHAPE_ANCHOR.isSet(propVal);
    }

    private PaintStyle.GradientPaint getGradientPaint(final PaintStyle.GradientPaint.GradientType gradientType) {
        int propVal;
        AbstractEscherOptRecord opt = this.shape.getEscherOptRecord();
        EscherSimpleProperty p = (EscherSimpleProperty)HSLFShape.getEscherProperty(opt, EscherPropertyTypes.FILL__NOFILLHITTEST);
        int n = propVal = p == null ? 0 : p.getPropertyValue();
        if (FILL_USE_FILLED.isSet(propVal) && !FILL_FILLED.isSet(propVal)) {
            return null;
        }
        EscherArrayProperty ep = (EscherArrayProperty)HSLFShape.getEscherProperty(opt, EscherPropertyTypes.FILL__SHADECOLORS);
        int colorCnt = ep == null ? 0 : ep.getNumberOfElementsInArray();
        final ArrayList<Color> colors = new ArrayList<Color>();
        final ArrayList<Float> fractions = new ArrayList<Float>();
        if (colorCnt == 0) {
            colors.add(this.getBackgroundColor());
            colors.add(this.getForegroundColor());
            fractions.add(Float.valueOf(0.0f));
            fractions.add(Float.valueOf(1.0f));
        } else {
            ep.forEach(data -> {
                EscherColorRef ecr = new EscherColorRef((byte[])data, 0, 4);
                colors.add(this.shape.getColor(ecr));
                double pos = Units.fixedPointToDouble(LittleEndian.getInt(data, 4));
                fractions.add(Float.valueOf((float)pos));
            });
        }
        int focus = this.getFillFocus();
        if (focus == 100 || focus == -100) {
            Collections.reverse(colors);
        } else if (focus != 0) {
            float val;
            int i;
            if (focus < 0) {
                focus = 100 + focus;
            }
            ArrayList reflectedColors = new ArrayList(colors.subList(1, colors.size()));
            Collections.reverse(reflectedColors);
            colors.addAll(0, reflectedColors);
            ArrayList<Float> fractRev = new ArrayList<Float>();
            for (i = fractions.size() - 2; i >= 0; --i) {
                val = (float)(1.0 - (double)(((Float)fractions.get(i)).floatValue() * (float)focus) / 100.0);
                fractRev.add(Float.valueOf(val));
            }
            for (i = 0; i < fractions.size(); ++i) {
                val = (float)((double)(((Float)fractions.get(i)).floatValue() * (float)focus) / 100.0);
                fractions.set(i, Float.valueOf(val));
            }
            fractions.addAll(fractRev);
        }
        return new PaintStyle.GradientPaint(){

            @Override
            public double getGradientAngle() {
                int rot = HSLFFill.this.shape.getEscherProperty(EscherPropertyTypes.FILL__ANGLE);
                return 90.0 - Units.fixedPointToDouble(rot);
            }

            @Override
            public ColorStyle[] getGradientColors() {
                return (ColorStyle[])colors.stream().map(this::wrapColor).toArray(ColorStyle[]::new);
            }

            private ColorStyle wrapColor(Color col) {
                return col == null ? null : DrawPaint.createSolidPaint(col).getSolidColor();
            }

            @Override
            public float[] getGradientFractions() {
                float[] frc = new float[fractions.size()];
                for (int i = 0; i < fractions.size(); ++i) {
                    frc[i] = ((Float)fractions.get(i)).floatValue();
                }
                return frc;
            }

            @Override
            public boolean isRotatedWithShape() {
                return HSLFFill.this.isRotatedWithShape();
            }

            @Override
            public PaintStyle.GradientPaint.GradientType getGradientType() {
                return gradientType;
            }
        };
    }

    private PaintStyle.TexturePaint getTexturePaint() {
        final HSLFPictureData pd = this.getPictureData();
        if (pd == null) {
            return null;
        }
        return new PaintStyle.TexturePaint(){

            @Override
            public InputStream getImageData() {
                return new ByteArrayInputStream(pd.getData());
            }

            @Override
            public String getContentType() {
                return pd.getContentType();
            }

            @Override
            public int getAlpha() {
                return (int)(HSLFFill.this.shape.getAlpha(EscherPropertyTypes.FILL__FILLOPACITY) * 100000.0);
            }

            @Override
            public boolean isRotatedWithShape() {
                return HSLFFill.this.isRotatedWithShape();
            }

            @Override
            public Shape getShape() {
                return HSLFFill.this.shape;
            }
        };
    }

    public int getFillType() {
        AbstractEscherOptRecord opt = this.shape.getEscherOptRecord();
        EscherSimpleProperty prop = (EscherSimpleProperty)HSLFShape.getEscherProperty(opt, EscherPropertyTypes.FILL__FILLTYPE);
        return prop == null ? 0 : prop.getPropertyValue();
    }

    public int getFillFocus() {
        AbstractEscherOptRecord opt = this.shape.getEscherOptRecord();
        EscherSimpleProperty prop = (EscherSimpleProperty)HSLFShape.getEscherProperty(opt, EscherPropertyTypes.FILL__FOCUS);
        return prop == null ? 0 : prop.getPropertyValue();
    }

    void afterInsert(HSLFSheet sh) {
        int idx;
        EscherBSERecord bse;
        AbstractEscherOptRecord opt = this.shape.getEscherOptRecord();
        EscherSimpleProperty p = (EscherSimpleProperty)HSLFShape.getEscherProperty(opt, EscherPropertyTypes.FILL__PATTERNTEXTURE);
        if (p != null && (bse = this.getEscherBSERecord(idx = p.getPropertyValue())) != null) {
            bse.setRef(bse.getRef() + 1);
        }
    }

    EscherBSERecord getEscherBSERecord(int idx) {
        HSLFSheet sheet = this.shape.getSheet();
        if (sheet == null) {
            LOG.atDebug().log("Fill has not yet been assigned to a sheet");
            return null;
        }
        HSLFSlideShow ppt = sheet.getSlideShow();
        Document doc = ppt.getDocumentRecord();
        EscherContainerRecord dggContainer = doc.getPPDrawingGroup().getDggContainer();
        EscherContainerRecord bstore = (EscherContainerRecord)HSLFShape.getEscherChild(dggContainer, EscherContainerRecord.BSTORE_CONTAINER);
        if (bstore == null) {
            LOG.atDebug().log("EscherContainerRecord.BSTORE_CONTAINER was not found ");
            return null;
        }
        return (EscherBSERecord)bstore.getChild(idx - 1);
    }

    public void setFillType(int type) {
        AbstractEscherOptRecord opt = this.shape.getEscherOptRecord();
        HSLFShape.setEscherProperty(opt, EscherPropertyTypes.FILL__FILLTYPE, type);
    }

    public Color getForegroundColor() {
        AbstractEscherOptRecord opt = this.shape.getEscherOptRecord();
        EscherSimpleProperty p = (EscherSimpleProperty)HSLFShape.getEscherProperty(opt, EscherPropertyTypes.FILL__NOFILLHITTEST);
        int propVal = p == null ? 0 : p.getPropertyValue();
        return !FILL_USE_FILLED.isSet(propVal) || FILL_USE_FILLED.isSet(propVal) && FILL_FILLED.isSet(propVal) ? this.shape.getColor(EscherPropertyTypes.FILL__FILLCOLOR, EscherPropertyTypes.FILL__FILLOPACITY) : null;
    }

    public void setForegroundColor(Color color) {
        EscherSimpleProperty p;
        AbstractEscherOptRecord opt = this.shape.getEscherOptRecord();
        opt.removeEscherProperty(EscherPropertyTypes.FILL__FILLOPACITY);
        opt.removeEscherProperty(EscherPropertyTypes.FILL__FILLCOLOR);
        if (color != null) {
            int rgb = new Color(color.getBlue(), color.getGreen(), color.getRed(), 0).getRGB();
            HSLFShape.setEscherProperty(opt, EscherPropertyTypes.FILL__FILLCOLOR, rgb);
            int alpha = color.getAlpha();
            if (alpha < 255) {
                int alphaFP = Units.doubleToFixedPoint((double)alpha / 255.0);
                HSLFShape.setEscherProperty(opt, EscherPropertyTypes.FILL__FILLOPACITY, alphaFP);
            }
        }
        int propVal = (p = (EscherSimpleProperty)HSLFShape.getEscherProperty(opt, EscherPropertyTypes.FILL__NOFILLHITTEST)) == null ? 0 : p.getPropertyValue();
        propVal = FILL_FILLED.setBoolean(propVal, color != null);
        propVal = FILL_NO_FILL_HIT_TEST.setBoolean(propVal, color != null);
        propVal = FILL_USE_FILLED.set(propVal);
        propVal = FILL_USE_FILL_SHAPE.set(propVal);
        propVal = FILL_USE_NO_FILL_HIT_TEST.set(propVal);
        propVal = FILL_FILL_SHAPE.clear(propVal);
        HSLFShape.setEscherProperty(opt, EscherPropertyTypes.FILL__NOFILLHITTEST, propVal);
    }

    public Color getBackgroundColor() {
        AbstractEscherOptRecord opt = this.shape.getEscherOptRecord();
        EscherSimpleProperty p = (EscherSimpleProperty)HSLFShape.getEscherProperty(opt, EscherPropertyTypes.FILL__NOFILLHITTEST);
        int propVal = p == null ? 0 : p.getPropertyValue();
        return !FILL_USE_FILLED.isSet(propVal) || FILL_USE_FILLED.isSet(propVal) && FILL_FILLED.isSet(propVal) ? this.shape.getColor(EscherPropertyTypes.FILL__FILLBACKCOLOR, EscherPropertyTypes.FILL__FILLOPACITY) : null;
    }

    public void setBackgroundColor(Color color) {
        AbstractEscherOptRecord opt = this.shape.getEscherOptRecord();
        if (color == null) {
            HSLFShape.setEscherProperty(opt, EscherPropertyTypes.FILL__FILLBACKCOLOR, -1);
        } else {
            int rgb = new Color(color.getBlue(), color.getGreen(), color.getRed(), 0).getRGB();
            HSLFShape.setEscherProperty(opt, EscherPropertyTypes.FILL__FILLBACKCOLOR, rgb);
        }
    }

    public HSLFPictureData getPictureData() {
        AbstractEscherOptRecord opt = this.shape.getEscherOptRecord();
        EscherSimpleProperty p = (EscherSimpleProperty)HSLFShape.getEscherProperty(opt, EscherPropertyTypes.FILL__PATTERNTEXTURE);
        if (p == null) {
            return null;
        }
        HSLFSlideShow ppt = this.shape.getSheet().getSlideShow();
        List<HSLFPictureData> pict = ppt.getPictureData();
        Document doc = ppt.getDocumentRecord();
        EscherContainerRecord dggContainer = doc.getPPDrawingGroup().getDggContainer();
        EscherContainerRecord bstore = (EscherContainerRecord)HSLFShape.getEscherChild(dggContainer, EscherContainerRecord.BSTORE_CONTAINER);
        int idx = p.getPropertyValue();
        if (idx == 0) {
            LOG.atWarn().log("no reference to picture data found ");
        } else {
            EscherBSERecord bse = (EscherBSERecord)bstore.getChild(idx - 1);
            for (HSLFPictureData pd : pict) {
                if (pd.bse != bse) continue;
                return pd;
            }
        }
        return null;
    }

    public void setPictureData(HSLFPictureData data) {
        EscherBSERecord bse;
        AbstractEscherOptRecord opt = this.shape.getEscherOptRecord();
        HSLFShape.setEscherProperty(opt, EscherPropertyTypes.FILL__PATTERNTEXTURE, true, data == null ? 0 : data.getIndex());
        if (data != null && this.shape.getSheet() != null && (bse = this.getEscherBSERecord(data.getIndex())) != null) {
            bse.setRef(bse.getRef() + 1);
        }
    }
}

