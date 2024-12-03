/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hslf.model;

import org.apache.poi.ddf.AbstractEscherOptRecord;
import org.apache.poi.ddf.EscherComplexProperty;
import org.apache.poi.ddf.EscherContainerRecord;
import org.apache.poi.ddf.EscherPropertyTypes;
import org.apache.poi.ddf.EscherSpRecord;
import org.apache.poi.hslf.exceptions.HSLFException;
import org.apache.poi.hslf.record.Document;
import org.apache.poi.hslf.record.ExControl;
import org.apache.poi.hslf.record.ExObjList;
import org.apache.poi.hslf.record.ExObjRefAtom;
import org.apache.poi.hslf.record.HSLFEscherClientDataRecord;
import org.apache.poi.hslf.record.Record;
import org.apache.poi.hslf.record.RecordTypes;
import org.apache.poi.hslf.usermodel.HSLFPictureData;
import org.apache.poi.hslf.usermodel.HSLFPictureShape;
import org.apache.poi.hslf.usermodel.HSLFShape;
import org.apache.poi.hslf.usermodel.HSLFSheet;
import org.apache.poi.hslf.usermodel.HSLFTextParagraph;
import org.apache.poi.sl.usermodel.ShapeContainer;
import org.apache.poi.sl.usermodel.ShapeType;
import org.apache.poi.util.StringUtil;

public final class ActiveXShape
extends HSLFPictureShape {
    public static final int DEFAULT_ACTIVEX_THUMBNAIL = -1;

    public ActiveXShape(int movieIdx, HSLFPictureData pictureData) {
        super(pictureData, null);
        this.setActiveXIndex(movieIdx);
    }

    protected ActiveXShape(EscherContainerRecord escherRecord, ShapeContainer<HSLFShape, HSLFTextParagraph> parent) {
        super(escherRecord, parent);
    }

    @Override
    protected EscherContainerRecord createSpContainer(int idx, boolean isChild) {
        EscherContainerRecord ecr = super.createSpContainer(idx, isChild);
        EscherSpRecord spRecord = (EscherSpRecord)ecr.getChildById(EscherSpRecord.RECORD_ID);
        assert (spRecord != null);
        spRecord.setFlags(2576);
        this.setShapeType(ShapeType.HOST_CONTROL);
        this.setEscherProperty(EscherPropertyTypes.BLIP__PICTUREID, idx);
        this.setEscherProperty(EscherPropertyTypes.LINESTYLE__COLOR, 0x8000001);
        this.setEscherProperty(EscherPropertyTypes.LINESTYLE__NOLINEDRAWDASH, 524296);
        this.setEscherProperty(EscherPropertyTypes.SHADOWSTYLE__COLOR, 0x8000002);
        this.setEscherProperty(EscherPropertyTypes.PROTECTION__LOCKAGAINSTGROUPING, -1);
        HSLFEscherClientDataRecord cldata = this.getClientData(true);
        cldata.addChild(new ExObjRefAtom());
        return ecr;
    }

    public void setActiveXIndex(int idx) {
        ExObjRefAtom oe = (ExObjRefAtom)this.getClientDataRecord(RecordTypes.ExObjRefAtom.typeID);
        if (oe == null) {
            throw new HSLFException("OEShapeAtom for ActiveX doesn't exist");
        }
        oe.setExObjIdRef(idx);
    }

    public int getControlIndex() {
        int idx = -1;
        ExObjRefAtom oe = (ExObjRefAtom)this.getClientDataRecord(RecordTypes.ExObjRefAtom.typeID);
        if (oe != null) {
            idx = oe.getExObjIdRef();
        }
        return idx;
    }

    public void setProperty(String key, String value) {
    }

    public ExControl getExControl() {
        int idx = this.getControlIndex();
        Document doc = this.getSheet().getSlideShow().getDocumentRecord();
        ExObjList lst = (ExObjList)doc.findFirstOfType(RecordTypes.ExObjList.typeID);
        if (lst == null) {
            return null;
        }
        for (Record ch : lst.getChildRecords()) {
            ExControl c;
            if (!(ch instanceof ExControl) || (c = (ExControl)ch).getExOleObjAtom().getObjID() != idx) continue;
            return c;
        }
        return null;
    }

    @Override
    protected void afterInsert(HSLFSheet sheet) {
        ExControl ctrl = this.getExControl();
        if (ctrl == null) {
            throw new NullPointerException("ExControl is not defined");
        }
        ctrl.getExControlAtom().setSlideId(sheet._getSheetNumber());
        String name = ctrl.getProgId() + "-" + this.getControlIndex() + '\u0000';
        byte[] data = StringUtil.getToUnicodeLE(name);
        EscherComplexProperty prop = new EscherComplexProperty(EscherPropertyTypes.GROUPSHAPE__SHAPENAME, false, data.length);
        prop.setComplexData(data);
        AbstractEscherOptRecord opt = this.getEscherOptRecord();
        opt.addEscherProperty(prop);
    }
}

