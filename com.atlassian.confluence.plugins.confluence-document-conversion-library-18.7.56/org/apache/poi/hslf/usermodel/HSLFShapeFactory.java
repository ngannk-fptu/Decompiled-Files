/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hslf.usermodel;

import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ddf.AbstractEscherOptRecord;
import org.apache.poi.ddf.EscherClientDataRecord;
import org.apache.poi.ddf.EscherContainerRecord;
import org.apache.poi.ddf.EscherOptRecord;
import org.apache.poi.ddf.EscherProperty;
import org.apache.poi.ddf.EscherPropertyFactory;
import org.apache.poi.ddf.EscherPropertyTypes;
import org.apache.poi.ddf.EscherRecord;
import org.apache.poi.ddf.EscherRecordTypes;
import org.apache.poi.ddf.EscherSimpleProperty;
import org.apache.poi.ddf.EscherSpRecord;
import org.apache.poi.ddf.EscherTextboxRecord;
import org.apache.poi.hslf.model.MovieShape;
import org.apache.poi.hslf.record.ExObjRefAtom;
import org.apache.poi.hslf.record.HSLFEscherClientDataRecord;
import org.apache.poi.hslf.record.InteractiveInfo;
import org.apache.poi.hslf.record.Record;
import org.apache.poi.hslf.record.RecordTypes;
import org.apache.poi.hslf.usermodel.HSLFAutoShape;
import org.apache.poi.hslf.usermodel.HSLFFreeformShape;
import org.apache.poi.hslf.usermodel.HSLFGroupShape;
import org.apache.poi.hslf.usermodel.HSLFLine;
import org.apache.poi.hslf.usermodel.HSLFObjectShape;
import org.apache.poi.hslf.usermodel.HSLFPictureShape;
import org.apache.poi.hslf.usermodel.HSLFShape;
import org.apache.poi.hslf.usermodel.HSLFTable;
import org.apache.poi.hslf.usermodel.HSLFTableCell;
import org.apache.poi.hslf.usermodel.HSLFTextBox;
import org.apache.poi.hslf.usermodel.HSLFTextParagraph;
import org.apache.poi.sl.usermodel.ShapeContainer;
import org.apache.poi.sl.usermodel.ShapeType;
import org.apache.poi.util.RecordFormatException;

public final class HSLFShapeFactory {
    private static final Logger LOG = LogManager.getLogger(HSLFShapeFactory.class);

    public static HSLFShape createShape(EscherContainerRecord spContainer, ShapeContainer<HSLFShape, HSLFTextParagraph> parent) {
        if (spContainer.getRecordId() == EscherContainerRecord.SPGR_CONTAINER) {
            return HSLFShapeFactory.createShapeGroup(spContainer, parent);
        }
        return HSLFShapeFactory.createSimpleShape(spContainer, parent);
    }

    public static HSLFGroupShape createShapeGroup(EscherContainerRecord spContainer, ShapeContainer<HSLFShape, HSLFTextParagraph> parent) {
        boolean isTable = false;
        EscherContainerRecord ecr = (EscherContainerRecord)spContainer.getChild(0);
        Object opt = HSLFShape.getEscherChild(ecr, EscherRecordTypes.USER_DEFINED);
        if (opt != null) {
            EscherPropertyFactory f = new EscherPropertyFactory();
            List<EscherProperty> props = f.createProperties(((EscherRecord)opt).serialize(), 8, ((EscherRecord)opt).getInstance());
            for (EscherProperty ep : props) {
                if (ep.getPropertyNumber() != EscherPropertyTypes.GROUPSHAPE__TABLEPROPERTIES.propNumber || !(ep instanceof EscherSimpleProperty) || (((EscherSimpleProperty)ep).getPropertyValue() & 1) != 1) continue;
                isTable = true;
                break;
            }
        }
        HSLFGroupShape group = isTable ? new HSLFTable(spContainer, parent) : new HSLFGroupShape(spContainer, parent);
        return group;
    }

    public static HSLFShape createSimpleShape(EscherContainerRecord spContainer, ShapeContainer<HSLFShape, HSLFTextParagraph> parent) {
        HSLFShape shape;
        EscherSpRecord spRecord = (EscherSpRecord)spContainer.getChildById(EscherSpRecord.RECORD_ID);
        if (spRecord == null) {
            throw new RecordFormatException("Could not read EscherSpRecord as child of " + spContainer.getRecordName());
        }
        ShapeType type = ShapeType.forId(spRecord.getShapeType(), false);
        switch (type) {
            case TEXT_BOX: {
                shape = new HSLFTextBox(spContainer, parent);
                break;
            }
            case HOST_CONTROL: 
            case FRAME: {
                shape = HSLFShapeFactory.createFrame(spContainer, parent);
                break;
            }
            case LINE: {
                shape = new HSLFLine(spContainer, parent);
                break;
            }
            case NOT_PRIMITIVE: {
                shape = HSLFShapeFactory.createNonPrimitive(spContainer, parent);
                break;
            }
            default: {
                if (parent instanceof HSLFTable) {
                    EscherTextboxRecord etr = (EscherTextboxRecord)spContainer.getChildById(EscherTextboxRecord.RECORD_ID);
                    if (etr == null) {
                        LOG.atWarn().log("invalid ppt - add EscherTextboxRecord to cell");
                        etr = new EscherTextboxRecord();
                        etr.setRecordId(EscherTextboxRecord.RECORD_ID);
                        etr.setOptions((short)15);
                        spContainer.addChildRecord(etr);
                    }
                    shape = new HSLFTableCell(spContainer, (HSLFTable)parent);
                    break;
                }
                shape = new HSLFAutoShape(spContainer, parent);
            }
        }
        return shape;
    }

    private static HSLFShape createFrame(EscherContainerRecord spContainer, ShapeContainer<HSLFShape, HSLFTextParagraph> parent) {
        ExObjRefAtom oes;
        InteractiveInfo info = (InteractiveInfo)HSLFShapeFactory.getClientDataRecord(spContainer, RecordTypes.InteractiveInfo.typeID);
        if (info != null && info.getInteractiveInfoAtom() != null) {
            switch (info.getInteractiveInfoAtom().getAction()) {
                case 5: {
                    return new HSLFObjectShape(spContainer, parent);
                }
                case 6: {
                    return new MovieShape(spContainer, parent);
                }
            }
        }
        return (oes = (ExObjRefAtom)HSLFShapeFactory.getClientDataRecord(spContainer, RecordTypes.ExObjRefAtom.typeID)) != null ? new HSLFObjectShape(spContainer, parent) : new HSLFPictureShape(spContainer, parent);
    }

    private static HSLFShape createNonPrimitive(EscherContainerRecord spContainer, ShapeContainer<HSLFShape, HSLFTextParagraph> parent) {
        AbstractEscherOptRecord opt = (AbstractEscherOptRecord)HSLFShape.getEscherChild(spContainer, EscherOptRecord.RECORD_ID);
        Object prop = HSLFShape.getEscherProperty(opt, EscherPropertyTypes.GEOMETRY__VERTICES);
        if (prop != null) {
            return new HSLFFreeformShape(spContainer, parent);
        }
        LOG.atInfo().log("Creating AutoShape for a NotPrimitive shape");
        return new HSLFAutoShape(spContainer, parent);
    }

    protected static <T extends Record> T getClientDataRecord(EscherContainerRecord spContainer, int recordType) {
        HSLFEscherClientDataRecord cldata = (HSLFEscherClientDataRecord)spContainer.getChildById(EscherClientDataRecord.RECORD_ID);
        if (cldata != null) {
            for (Record record : cldata.getHSLFChildRecords()) {
                if (record.getRecordType() != (long)recordType) continue;
                return (T)record;
            }
        }
        return null;
    }
}

