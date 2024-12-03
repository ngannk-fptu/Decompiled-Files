/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hslf.usermodel;

import org.apache.poi.ddf.EscherContainerRecord;
import org.apache.poi.ddf.EscherPropertyTypes;
import org.apache.poi.hslf.usermodel.HSLFShape;
import org.apache.poi.hslf.usermodel.HSLFTextParagraph;
import org.apache.poi.hslf.usermodel.HSLFTextShape;
import org.apache.poi.sl.usermodel.ShapeContainer;
import org.apache.poi.sl.usermodel.ShapeType;
import org.apache.poi.sl.usermodel.TextBox;
import org.apache.poi.sl.usermodel.VerticalAlignment;

public class HSLFTextBox
extends HSLFTextShape
implements TextBox<HSLFShape, HSLFTextParagraph> {
    protected HSLFTextBox(EscherContainerRecord escherRecord, ShapeContainer<HSLFShape, HSLFTextParagraph> parent) {
        super(escherRecord, parent);
    }

    public HSLFTextBox(ShapeContainer<HSLFShape, HSLFTextParagraph> parent) {
        super(parent);
    }

    public HSLFTextBox() {
        this(null);
    }

    @Override
    protected EscherContainerRecord createSpContainer(boolean isChild) {
        EscherContainerRecord ecr = super.createSpContainer(isChild);
        this.setShapeType(ShapeType.TEXT_BOX);
        this.setEscherProperty(EscherPropertyTypes.FILL__FILLCOLOR, 0x8000004);
        this.setEscherProperty(EscherPropertyTypes.FILL__FILLBACKCOLOR, 0x8000000);
        this.setEscherProperty(EscherPropertyTypes.FILL__NOFILLHITTEST, 0x100000);
        this.setEscherProperty(EscherPropertyTypes.LINESTYLE__COLOR, 0x8000001);
        this.setEscherProperty(EscherPropertyTypes.LINESTYLE__NOLINEDRAWDASH, 524288);
        this.setEscherProperty(EscherPropertyTypes.SHADOWSTYLE__COLOR, 0x8000002);
        this.getTextParagraphs();
        return ecr;
    }

    @Override
    protected void setDefaultTextProperties(HSLFTextParagraph _txtrun) {
        this.setVerticalAlignment(VerticalAlignment.TOP);
        this.setEscherProperty(EscherPropertyTypes.TEXT__SIZE_TEXT_TO_FIT_SHAPE, 131074);
    }
}

