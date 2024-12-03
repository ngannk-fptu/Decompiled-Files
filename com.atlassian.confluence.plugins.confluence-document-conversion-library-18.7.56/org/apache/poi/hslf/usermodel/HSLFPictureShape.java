/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hslf.usermodel;

import java.awt.Insets;
import java.awt.geom.Rectangle2D;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Unbox;
import org.apache.poi.ddf.AbstractEscherOptRecord;
import org.apache.poi.ddf.EscherBSERecord;
import org.apache.poi.ddf.EscherComplexProperty;
import org.apache.poi.ddf.EscherContainerRecord;
import org.apache.poi.ddf.EscherPropertyTypes;
import org.apache.poi.ddf.EscherSimpleProperty;
import org.apache.poi.ddf.EscherSpRecord;
import org.apache.poi.hslf.record.Document;
import org.apache.poi.hslf.usermodel.HSLFGroupShape;
import org.apache.poi.hslf.usermodel.HSLFPictureData;
import org.apache.poi.hslf.usermodel.HSLFShape;
import org.apache.poi.hslf.usermodel.HSLFSheet;
import org.apache.poi.hslf.usermodel.HSLFSimpleShape;
import org.apache.poi.hslf.usermodel.HSLFSlideShow;
import org.apache.poi.hslf.usermodel.HSLFTextParagraph;
import org.apache.poi.sl.draw.DrawPictureShape;
import org.apache.poi.sl.usermodel.PictureShape;
import org.apache.poi.sl.usermodel.ShapeContainer;
import org.apache.poi.sl.usermodel.ShapeType;
import org.apache.poi.util.StringUtil;
import org.apache.poi.util.Units;

public class HSLFPictureShape
extends HSLFSimpleShape
implements PictureShape<HSLFShape, HSLFTextParagraph> {
    private static final Logger LOG = LogManager.getLogger(HSLFPictureShape.class);

    public HSLFPictureShape(HSLFPictureData data) {
        this(data, null);
    }

    public HSLFPictureShape(HSLFPictureData data, ShapeContainer<HSLFShape, HSLFTextParagraph> parent) {
        super(null, parent);
        this.createSpContainer(data.getIndex(), parent instanceof HSLFGroupShape);
    }

    protected HSLFPictureShape(EscherContainerRecord escherRecord, ShapeContainer<HSLFShape, HSLFTextParagraph> parent) {
        super(escherRecord, parent);
    }

    public int getPictureIndex() {
        AbstractEscherOptRecord opt = this.getEscherOptRecord();
        EscherSimpleProperty prop = (EscherSimpleProperty)HSLFPictureShape.getEscherProperty(opt, EscherPropertyTypes.BLIP__BLIPTODISPLAY);
        return prop == null ? 0 : prop.getPropertyValue();
    }

    protected EscherContainerRecord createSpContainer(int idx, boolean isChild) {
        EscherContainerRecord ecr = super.createSpContainer(isChild);
        EscherSpRecord spRecord = (EscherSpRecord)ecr.getChildById(EscherSpRecord.RECORD_ID);
        spRecord.setOptions((short)(ShapeType.FRAME.nativeId << 4 | 2));
        AbstractEscherOptRecord opt = this.getEscherOptRecord();
        HSLFPictureShape.setEscherProperty(opt, EscherPropertyTypes.PROTECTION__LOCKAGAINSTGROUPING, 0x800080);
        HSLFPictureShape.setEscherProperty(opt, EscherPropertyTypes.BLIP__BLIPTODISPLAY, true, idx);
        return ecr;
    }

    @Override
    public HSLFPictureData getPictureData() {
        HSLFSlideShow ppt = this.getSheet().getSlideShow();
        List<HSLFPictureData> pict = ppt.getPictureData();
        EscherBSERecord bse = this.getEscherBSERecord();
        if (bse == null) {
            LOG.atError().log("no reference to picture data found ");
        } else {
            for (HSLFPictureData pd : pict) {
                if (pd.bse != bse) continue;
                return pd;
            }
            LOG.atError().log("no picture found for our BSE offset {}", (Object)Unbox.box(bse.getOffset()));
        }
        return null;
    }

    protected EscherBSERecord getEscherBSERecord() {
        HSLFSlideShow ppt = this.getSheet().getSlideShow();
        Document doc = ppt.getDocumentRecord();
        EscherContainerRecord dggContainer = doc.getPPDrawingGroup().getDggContainer();
        EscherContainerRecord bstore = (EscherContainerRecord)HSLFShape.getEscherChild(dggContainer, EscherContainerRecord.BSTORE_CONTAINER);
        if (bstore == null) {
            LOG.atDebug().log("EscherContainerRecord.BSTORE_CONTAINER was not found ");
            return null;
        }
        int idx = this.getPictureIndex();
        if (idx == 0) {
            LOG.atDebug().log("picture index was not found, returning ");
            return null;
        }
        return (EscherBSERecord)bstore.getChild(idx - 1);
    }

    public String getPictureName() {
        AbstractEscherOptRecord opt = this.getEscherOptRecord();
        EscherComplexProperty prop = (EscherComplexProperty)HSLFPictureShape.getEscherProperty(opt, EscherPropertyTypes.BLIP__BLIPFILENAME);
        if (prop == null) {
            return null;
        }
        String name = StringUtil.getFromUnicodeLE(prop.getComplexData());
        return name.trim();
    }

    public void setPictureName(String name) {
        AbstractEscherOptRecord opt = this.getEscherOptRecord();
        byte[] data = StringUtil.getToUnicodeLE(name + '\u0000');
        EscherComplexProperty prop = new EscherComplexProperty(EscherPropertyTypes.BLIP__BLIPFILENAME, false, data.length);
        prop.setComplexData(data);
        opt.addEscherProperty(prop);
    }

    @Override
    protected void afterInsert(HSLFSheet sh) {
        super.afterInsert(sh);
        EscherBSERecord bse = this.getEscherBSERecord();
        bse.setRef(bse.getRef() + 1);
        Rectangle2D anchor = this.getAnchor();
        if (anchor.isEmpty()) {
            new DrawPictureShape(this).resize();
        }
    }

    @Override
    public Insets getClipping() {
        AbstractEscherOptRecord opt = this.getEscherOptRecord();
        double top = HSLFPictureShape.getFractProp(opt, EscherPropertyTypes.BLIP__CROPFROMTOP);
        double bottom = HSLFPictureShape.getFractProp(opt, EscherPropertyTypes.BLIP__CROPFROMBOTTOM);
        double left = HSLFPictureShape.getFractProp(opt, EscherPropertyTypes.BLIP__CROPFROMLEFT);
        double right = HSLFPictureShape.getFractProp(opt, EscherPropertyTypes.BLIP__CROPFROMRIGHT);
        return top == 0.0 && bottom == 0.0 && left == 0.0 && right == 0.0 ? null : new Insets((int)(top * 100000.0), (int)(left * 100000.0), (int)(bottom * 100000.0), (int)(right * 100000.0));
    }

    @Override
    public ShapeType getShapeType() {
        return ShapeType.RECT;
    }

    private static double getFractProp(AbstractEscherOptRecord opt, EscherPropertyTypes type) {
        EscherSimpleProperty prop = (EscherSimpleProperty)HSLFPictureShape.getEscherProperty(opt, type);
        if (prop == null) {
            return 0.0;
        }
        int fixedPoint = prop.getPropertyValue();
        return Units.fixedPointToDouble(fixedPoint);
    }
}

