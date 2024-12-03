/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hslf.usermodel;

import org.apache.poi.ddf.AbstractEscherOptRecord;
import org.apache.poi.ddf.EscherContainerRecord;
import org.apache.poi.ddf.EscherPropertyTypes;
import org.apache.poi.ddf.EscherSpRecord;
import org.apache.poi.hslf.usermodel.HSLFGroupShape;
import org.apache.poi.hslf.usermodel.HSLFShape;
import org.apache.poi.hslf.usermodel.HSLFTextParagraph;
import org.apache.poi.hslf.usermodel.HSLFTextShape;
import org.apache.poi.sl.usermodel.Line;
import org.apache.poi.sl.usermodel.ShapeContainer;
import org.apache.poi.sl.usermodel.ShapeType;

public final class HSLFLine
extends HSLFTextShape
implements Line<HSLFShape, HSLFTextParagraph> {
    public HSLFLine(EscherContainerRecord escherRecord, ShapeContainer<HSLFShape, HSLFTextParagraph> parent) {
        super(escherRecord, parent);
    }

    public HSLFLine(ShapeContainer<HSLFShape, HSLFTextParagraph> parent) {
        super(null, parent);
        this.createSpContainer(parent instanceof HSLFGroupShape);
    }

    public HSLFLine() {
        this(null);
    }

    @Override
    protected EscherContainerRecord createSpContainer(boolean isChild) {
        EscherContainerRecord ecr = super.createSpContainer(isChild);
        this.setShapeType(ShapeType.LINE);
        EscherSpRecord spRecord = (EscherSpRecord)ecr.getChildById(EscherSpRecord.RECORD_ID);
        short type = (short)(ShapeType.LINE.nativeId << 4 | 2);
        spRecord.setOptions(type);
        AbstractEscherOptRecord opt = this.getEscherOptRecord();
        HSLFLine.setEscherProperty(opt, EscherPropertyTypes.GEOMETRY__SHAPEPATH, 4);
        HSLFLine.setEscherProperty(opt, EscherPropertyTypes.GEOMETRY__FILLOK, 65536);
        HSLFLine.setEscherProperty(opt, EscherPropertyTypes.FILL__NOFILLHITTEST, 0x100000);
        HSLFLine.setEscherProperty(opt, EscherPropertyTypes.LINESTYLE__COLOR, 0x8000001);
        HSLFLine.setEscherProperty(opt, EscherPropertyTypes.LINESTYLE__NOLINEDRAWDASH, 655368);
        HSLFLine.setEscherProperty(opt, EscherPropertyTypes.SHADOWSTYLE__COLOR, 0x8000002);
        return ecr;
    }
}

