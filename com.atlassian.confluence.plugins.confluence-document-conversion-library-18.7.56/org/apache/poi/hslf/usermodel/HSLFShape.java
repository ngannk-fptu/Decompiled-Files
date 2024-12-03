/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hslf.usermodel;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ddf.AbstractEscherOptRecord;
import org.apache.poi.ddf.EscherChildAnchorRecord;
import org.apache.poi.ddf.EscherClientAnchorRecord;
import org.apache.poi.ddf.EscherClientDataRecord;
import org.apache.poi.ddf.EscherColorRef;
import org.apache.poi.ddf.EscherComplexProperty;
import org.apache.poi.ddf.EscherContainerRecord;
import org.apache.poi.ddf.EscherProperty;
import org.apache.poi.ddf.EscherPropertyTypes;
import org.apache.poi.ddf.EscherRecord;
import org.apache.poi.ddf.EscherRecordTypes;
import org.apache.poi.ddf.EscherSimpleProperty;
import org.apache.poi.ddf.EscherSpRecord;
import org.apache.poi.ddf.EscherTextboxRecord;
import org.apache.poi.hslf.record.ColorSchemeAtom;
import org.apache.poi.hslf.record.HSLFEscherClientDataRecord;
import org.apache.poi.hslf.record.Record;
import org.apache.poi.hslf.usermodel.HSLFFill;
import org.apache.poi.hslf.usermodel.HSLFSheet;
import org.apache.poi.hslf.usermodel.HSLFSimpleShape;
import org.apache.poi.hslf.usermodel.HSLFTextParagraph;
import org.apache.poi.sl.draw.DrawFactory;
import org.apache.poi.sl.usermodel.FillStyle;
import org.apache.poi.sl.usermodel.PresetColor;
import org.apache.poi.sl.usermodel.Shape;
import org.apache.poi.sl.usermodel.ShapeContainer;
import org.apache.poi.sl.usermodel.ShapeType;
import org.apache.poi.util.RecordFormatException;
import org.apache.poi.util.Removal;
import org.apache.poi.util.StringUtil;
import org.apache.poi.util.Units;

public abstract class HSLFShape
implements Shape<HSLFShape, HSLFTextParagraph> {
    private static final Logger LOG = LogManager.getLogger(HSLFShape.class);
    private EscherContainerRecord _escherContainer;
    private final ShapeContainer<HSLFShape, HSLFTextParagraph> _parent;
    private HSLFSheet _sheet;
    private HSLFFill _fill;

    protected HSLFShape(EscherContainerRecord escherRecord, ShapeContainer<HSLFShape, HSLFTextParagraph> parent) {
        this._escherContainer = escherRecord;
        this._parent = parent;
    }

    protected EscherContainerRecord createSpContainer(boolean isChild) {
        if (this._escherContainer == null) {
            this._escherContainer = new EscherContainerRecord();
            this._escherContainer.setOptions((short)15);
        }
        return this._escherContainer;
    }

    @Override
    public ShapeContainer<HSLFShape, HSLFTextParagraph> getParent() {
        return this._parent;
    }

    @Override
    public String getShapeName() {
        EscherComplexProperty ep = (EscherComplexProperty)HSLFShape.getEscherProperty(this.getEscherOptRecord(), EscherPropertyTypes.GROUPSHAPE__SHAPENAME);
        if (ep != null) {
            byte[] cd = ep.getComplexData();
            return StringUtil.getFromUnicodeLE0Terminated(cd, 0, cd.length / 2);
        }
        return this.getShapeType().nativeName + " " + this.getShapeId();
    }

    public ShapeType getShapeType() {
        EscherSpRecord spRecord = (EscherSpRecord)this.getEscherChild(EscherSpRecord.RECORD_ID);
        return ShapeType.forId(spRecord.getShapeType(), false);
    }

    public void setShapeType(ShapeType type) {
        EscherSpRecord spRecord = (EscherSpRecord)this.getEscherChild(EscherSpRecord.RECORD_ID);
        spRecord.setShapeType((short)type.nativeId);
        spRecord.setVersion((short)2);
    }

    @Override
    public Rectangle2D getAnchor() {
        int y2;
        int x2;
        int y1;
        int x1;
        boolean useChildRec;
        EscherSpRecord spRecord = (EscherSpRecord)this.getEscherChild(EscherSpRecord.RECORD_ID);
        int flags = spRecord.getFlags();
        EscherChildAnchorRecord childRec = (EscherChildAnchorRecord)this.getEscherChild(EscherChildAnchorRecord.RECORD_ID);
        boolean bl = useChildRec = (flags & 2) != 0;
        if (useChildRec && childRec != null) {
            x1 = childRec.getDx1();
            y1 = childRec.getDy1();
            x2 = childRec.getDx2();
            y2 = childRec.getDy2();
        } else {
            EscherClientAnchorRecord clientRec;
            if (useChildRec) {
                LOG.atWarn().log("EscherSpRecord.FLAG_CHILD is set but EscherChildAnchorRecord was not found");
            }
            if ((clientRec = (EscherClientAnchorRecord)this.getEscherChild(EscherClientAnchorRecord.RECORD_ID)) == null) {
                throw new RecordFormatException("Could not read record 'CLIENT_ANCHOR' with record-id: " + EscherClientAnchorRecord.RECORD_ID);
            }
            x1 = clientRec.getCol1();
            y1 = clientRec.getFlag();
            x2 = clientRec.getDx1();
            y2 = clientRec.getRow1();
        }
        return new Rectangle2D.Double(x1 == -1 ? -1.0 : Units.masterToPoints(x1), y1 == -1 ? -1.0 : Units.masterToPoints(y1), x2 == -1 ? -1.0 : Units.masterToPoints(x2 - x1), y2 == -1 ? -1.0 : Units.masterToPoints(y2 - y1));
    }

    public void setAnchor(Rectangle2D anchor) {
        int x = Units.pointsToMaster(anchor.getX());
        int y = Units.pointsToMaster(anchor.getY());
        int w = Units.pointsToMaster(anchor.getWidth() + anchor.getX());
        int h = Units.pointsToMaster(anchor.getHeight() + anchor.getY());
        EscherSpRecord spRecord = (EscherSpRecord)this.getEscherChild(EscherSpRecord.RECORD_ID);
        int flags = spRecord.getFlags();
        if ((flags & 2) != 0) {
            EscherChildAnchorRecord rec = (EscherChildAnchorRecord)this.getEscherChild(EscherChildAnchorRecord.RECORD_ID);
            rec.setDx1(x);
            rec.setDy1(y);
            rec.setDx2(w);
            rec.setDy2(h);
        } else {
            EscherClientAnchorRecord rec = (EscherClientAnchorRecord)this.getEscherChild(EscherClientAnchorRecord.RECORD_ID);
            rec.setCol1((short)x);
            rec.setFlag((short)y);
            rec.setDx1((short)w);
            rec.setRow1((short)h);
        }
    }

    public final void moveTo(double x, double y) {
        Rectangle2D anchor = this.getAnchor();
        anchor.setRect(x, y, anchor.getWidth(), anchor.getHeight());
        this.setAnchor(anchor);
    }

    public static <T extends EscherRecord> T getEscherChild(EscherContainerRecord owner, int recordId) {
        return owner.getChildById((short)recordId);
    }

    public static <T extends EscherRecord> T getEscherChild(EscherContainerRecord owner, EscherRecordTypes recordId) {
        return HSLFShape.getEscherChild(owner, recordId.typeID);
    }

    public <T extends EscherRecord> T getEscherChild(int recordId) {
        return this._escherContainer.getChildById((short)recordId);
    }

    public <T extends EscherRecord> T getEscherChild(EscherRecordTypes recordId) {
        return this.getEscherChild(recordId.typeID);
    }

    @Deprecated
    @Removal(version="5.0.0")
    public static <T extends EscherProperty> T getEscherProperty(AbstractEscherOptRecord opt, int propId) {
        return opt == null ? null : (T)opt.lookup(propId);
    }

    public static <T extends EscherProperty> T getEscherProperty(AbstractEscherOptRecord opt, EscherPropertyTypes type) {
        return opt == null ? null : (T)opt.lookup(type);
    }

    @Deprecated
    @Removal(version="5.0.0")
    public static void setEscherProperty(AbstractEscherOptRecord opt, short propId, int value) {
        List<EscherProperty> props = opt.getEscherProperties();
        Iterator<EscherProperty> iterator = props.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().getPropertyNumber() != propId) continue;
            iterator.remove();
            break;
        }
        if (value != -1) {
            opt.addEscherProperty(new EscherSimpleProperty(propId, value));
            opt.sortProperties();
        }
    }

    public static void setEscherProperty(AbstractEscherOptRecord opt, EscherPropertyTypes propType, int value) {
        HSLFShape.setEscherProperty(opt, propType, false, value);
    }

    public static void setEscherProperty(AbstractEscherOptRecord opt, EscherPropertyTypes propType, boolean isBlipId, int value) {
        List<EscherProperty> props = opt.getEscherProperties();
        Iterator<EscherProperty> iterator = props.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().getPropertyNumber() != propType.propNumber) continue;
            iterator.remove();
            break;
        }
        if (value != -1) {
            opt.addEscherProperty(new EscherSimpleProperty(propType, false, isBlipId, value));
            opt.sortProperties();
        }
    }

    @Deprecated
    @Removal(version="5.0.0")
    public void setEscherProperty(short propId, int value) {
        AbstractEscherOptRecord opt = this.getEscherOptRecord();
        HSLFShape.setEscherProperty(opt, propId, value);
    }

    public void setEscherProperty(EscherPropertyTypes propType, int value) {
        AbstractEscherOptRecord opt = this.getEscherOptRecord();
        HSLFShape.setEscherProperty(opt, propType, value);
    }

    public int getEscherProperty(short propId) {
        AbstractEscherOptRecord opt = this.getEscherOptRecord();
        EscherSimpleProperty prop = (EscherSimpleProperty)HSLFShape.getEscherProperty(opt, propId);
        return prop == null ? 0 : prop.getPropertyValue();
    }

    public int getEscherProperty(EscherPropertyTypes propType) {
        AbstractEscherOptRecord opt = this.getEscherOptRecord();
        EscherSimpleProperty prop = (EscherSimpleProperty)HSLFShape.getEscherProperty(opt, propType);
        return prop == null ? 0 : prop.getPropertyValue();
    }

    @Deprecated
    @Removal(version="5.0.0")
    public int getEscherProperty(short propId, int defaultValue) {
        AbstractEscherOptRecord opt = this.getEscherOptRecord();
        EscherSimpleProperty prop = (EscherSimpleProperty)HSLFShape.getEscherProperty(opt, propId);
        return prop == null ? defaultValue : prop.getPropertyValue();
    }

    public int getEscherProperty(EscherPropertyTypes type, int defaultValue) {
        AbstractEscherOptRecord opt = this.getEscherOptRecord();
        EscherSimpleProperty prop = (EscherSimpleProperty)HSLFShape.getEscherProperty(opt, type);
        return prop == null ? defaultValue : prop.getPropertyValue();
    }

    public EscherContainerRecord getSpContainer() {
        return this._escherContainer;
    }

    protected void afterInsert(HSLFSheet sh) {
        if (this._fill != null) {
            this._fill.afterInsert(sh);
        }
    }

    public HSLFSheet getSheet() {
        return this._sheet;
    }

    public void setSheet(HSLFSheet sheet) {
        this._sheet = sheet;
    }

    Color getColor(EscherPropertyTypes colorProperty, EscherPropertyTypes opacityProperty) {
        Color col;
        AbstractEscherOptRecord opt = this.getEscherOptRecord();
        EscherSimpleProperty colProp = (EscherSimpleProperty)HSLFShape.getEscherProperty(opt, colorProperty);
        if (colProp == null) {
            col = Color.WHITE;
        } else {
            EscherColorRef ecr = new EscherColorRef(colProp.getPropertyValue());
            col = this.getColor(ecr);
            if (col == null) {
                return null;
            }
        }
        double alpha = this.getAlpha(opacityProperty);
        return new Color(col.getRed(), col.getGreen(), col.getBlue(), (int)(alpha * 255.0));
    }

    Color getColor(EscherColorRef ecr) {
        boolean fPaletteIndex = ecr.hasPaletteIndexFlag();
        boolean fPaletteRGB = ecr.hasPaletteRGBFlag();
        boolean fSystemRGB = ecr.hasSystemRGBFlag();
        boolean fSchemeIndex = ecr.hasSchemeIndexFlag();
        boolean fSysIndex = ecr.hasSysIndexFlag();
        int[] rgb = ecr.getRGB();
        HSLFSheet sheet = this.getSheet();
        if (fSchemeIndex && sheet != null) {
            ColorSchemeAtom ca = sheet.getColorScheme();
            int schemeColor = ca.getColor(ecr.getSchemeIndex());
            rgb[0] = schemeColor >> 0 & 0xFF;
            rgb[1] = schemeColor >> 8 & 0xFF;
            rgb[2] = schemeColor >> 16 & 0xFF;
        } else if (!fPaletteIndex && !fPaletteRGB && !fSystemRGB && fSysIndex) {
            Color col = this.getSysIndexColor(ecr);
            col = this.applySysIndexProcedure(ecr, col);
            return col;
        }
        return new Color(rgb[0], rgb[1], rgb[2]);
    }

    private Color getSysIndexColor(EscherColorRef ecr) {
        EscherColorRef.SysIndexSource sis = ecr.getSysIndexSource();
        if (sis == null) {
            int sysIdx = ecr.getSysIndex();
            PresetColor pc = PresetColor.valueOfNativeId(sysIdx);
            return pc != null ? pc.color : null;
        }
        switch (sis) {
            case FILL_COLOR: {
                return this.getColor(EscherPropertyTypes.FILL__FILLCOLOR, EscherPropertyTypes.FILL__FILLOPACITY);
            }
            case LINE_OR_FILL_COLOR: {
                Color col = null;
                if (this instanceof HSLFSimpleShape) {
                    col = this.getColor(EscherPropertyTypes.LINESTYLE__COLOR, EscherPropertyTypes.LINESTYLE__OPACITY);
                }
                if (col == null) {
                    col = this.getColor(EscherPropertyTypes.FILL__FILLCOLOR, EscherPropertyTypes.FILL__FILLOPACITY);
                }
                return col;
            }
            case LINE_COLOR: {
                if (!(this instanceof HSLFSimpleShape)) break;
                return this.getColor(EscherPropertyTypes.LINESTYLE__COLOR, EscherPropertyTypes.LINESTYLE__OPACITY);
            }
            case SHADOW_COLOR: {
                if (!(this instanceof HSLFSimpleShape)) break;
                return ((HSLFSimpleShape)this).getShadowColor();
            }
            case CURRENT_OR_LAST_COLOR: {
                break;
            }
            case FILL_BACKGROUND_COLOR: {
                return this.getColor(EscherPropertyTypes.FILL__FILLBACKCOLOR, EscherPropertyTypes.FILL__FILLOPACITY);
            }
            case LINE_BACKGROUND_COLOR: {
                if (!(this instanceof HSLFSimpleShape)) break;
                return ((HSLFSimpleShape)this).getLineBackgroundColor();
            }
            case FILL_OR_LINE_COLOR: {
                Color col = this.getColor(EscherPropertyTypes.FILL__FILLCOLOR, EscherPropertyTypes.FILL__FILLOPACITY);
                if (col == null && this instanceof HSLFSimpleShape) {
                    col = this.getColor(EscherPropertyTypes.LINESTYLE__COLOR, EscherPropertyTypes.LINESTYLE__OPACITY);
                }
                return col;
            }
        }
        return null;
    }

    private Color applySysIndexProcedure(EscherColorRef ecr, Color col) {
        EscherColorRef.SysIndexProcedure sip = ecr.getSysIndexProcedure();
        if (col == null || sip == null) {
            return col;
        }
        switch (sip) {
            case DARKEN_COLOR: {
                double FACTOR = (double)ecr.getRGB()[2] / 255.0;
                int r = Math.toIntExact(Math.round((double)col.getRed() * FACTOR));
                int g = Math.toIntExact(Math.round((double)col.getGreen() * FACTOR));
                int b = Math.toIntExact(Math.round((double)col.getBlue() * FACTOR));
                return new Color(r, g, b);
            }
            case LIGHTEN_COLOR: {
                double FACTOR = (double)(255 - ecr.getRGB()[2]) / 255.0;
                int r = col.getRed();
                int g = col.getGreen();
                int b = col.getBlue();
                r = Math.toIntExact(Math.round((double)r + (double)(255 - r) * FACTOR));
                g = Math.toIntExact(Math.round((double)g + (double)(255 - g) * FACTOR));
                b = Math.toIntExact(Math.round((double)b + (double)(255 - b) * FACTOR));
                return new Color(r, g, b);
            }
        }
        return col;
    }

    double getAlpha(EscherPropertyTypes opacityProperty) {
        AbstractEscherOptRecord opt = this.getEscherOptRecord();
        EscherSimpleProperty op = (EscherSimpleProperty)HSLFShape.getEscherProperty(opt, opacityProperty);
        int defaultOpacity = 65536;
        int opacity = op == null ? defaultOpacity : op.getPropertyValue();
        return Units.fixedPointToDouble(opacity);
    }

    Color toRGB(int val) {
        int a = val >> 24 & 0xFF;
        int b = val >> 16 & 0xFF;
        int g = val >> 8 & 0xFF;
        int r = val >> 0 & 0xFF;
        if (a != 254 && a != 255) {
            ColorSchemeAtom ca = this.getSheet().getColorScheme();
            int schemeColor = ca.getColor(a);
            r = schemeColor >> 0 & 0xFF;
            g = schemeColor >> 8 & 0xFF;
            b = schemeColor >> 16 & 0xFF;
        }
        return new Color(r, g, b);
    }

    @Override
    public int getShapeId() {
        EscherSpRecord spRecord = (EscherSpRecord)this.getEscherChild(EscherSpRecord.RECORD_ID);
        return spRecord == null ? 0 : spRecord.getShapeId();
    }

    public void setShapeId(int id) {
        EscherSpRecord spRecord = (EscherSpRecord)this.getEscherChild(EscherSpRecord.RECORD_ID);
        if (spRecord != null) {
            spRecord.setShapeId(id);
        }
    }

    public HSLFFill getFill() {
        if (this._fill == null) {
            this._fill = new HSLFFill(this);
        }
        return this._fill;
    }

    public FillStyle getFillStyle() {
        return this.getFill().getFillStyle();
    }

    @Override
    public void draw(Graphics2D graphics, Rectangle2D bounds) {
        DrawFactory.getInstance(graphics).drawShape(graphics, this, bounds);
    }

    public AbstractEscherOptRecord getEscherOptRecord() {
        AbstractEscherOptRecord opt = (AbstractEscherOptRecord)this.getEscherChild(EscherRecordTypes.OPT);
        if (opt == null) {
            opt = (AbstractEscherOptRecord)this.getEscherChild(EscherRecordTypes.USER_DEFINED);
        }
        return opt;
    }

    public boolean getFlipHorizontal() {
        EscherSpRecord spRecord = (EscherSpRecord)this.getEscherChild(EscherSpRecord.RECORD_ID);
        return (spRecord.getFlags() & 0x40) != 0;
    }

    public void setFlipHorizontal(boolean flip) {
        EscherSpRecord spRecord = (EscherSpRecord)this.getEscherChild(EscherSpRecord.RECORD_ID);
        int flag = spRecord.getFlags() | 0x40;
        spRecord.setFlags(flag);
    }

    public boolean getFlipVertical() {
        EscherSpRecord spRecord = (EscherSpRecord)this.getEscherChild(EscherSpRecord.RECORD_ID);
        return (spRecord.getFlags() & 0x80) != 0;
    }

    public void setFlipVertical(boolean flip) {
        EscherSpRecord spRecord = (EscherSpRecord)this.getEscherChild(EscherSpRecord.RECORD_ID);
        int flag = spRecord.getFlags() | 0x80;
        spRecord.setFlags(flag);
    }

    public double getRotation() {
        int rot = this.getEscherProperty(EscherPropertyTypes.TRANSFORM__ROTATION);
        return Units.fixedPointToDouble(rot);
    }

    public void setRotation(double theta) {
        int rot = Units.doubleToFixedPoint(theta % 360.0);
        this.setEscherProperty(EscherPropertyTypes.TRANSFORM__ROTATION, rot);
    }

    public boolean isPlaceholder() {
        return false;
    }

    public <T extends Record> T getClientDataRecord(int recordType) {
        List<? extends Record> records = this.getClientRecords();
        if (records != null) {
            for (Record record : records) {
                if (record.getRecordType() != (long)recordType) continue;
                return (T)record;
            }
        }
        return null;
    }

    protected List<? extends Record> getClientRecords() {
        HSLFEscherClientDataRecord clientData = this.getClientData(false);
        return clientData == null ? null : clientData.getHSLFChildRecords();
    }

    protected HSLFEscherClientDataRecord getClientData(boolean create) {
        HSLFEscherClientDataRecord clientData = (HSLFEscherClientDataRecord)this.getEscherChild(EscherClientDataRecord.RECORD_ID);
        if (clientData == null && create) {
            clientData = new HSLFEscherClientDataRecord();
            clientData.setOptions((short)15);
            clientData.setRecordId(EscherClientDataRecord.RECORD_ID);
            this.getSpContainer().addChildBefore(clientData, EscherTextboxRecord.RECORD_ID);
        }
        return clientData;
    }
}

