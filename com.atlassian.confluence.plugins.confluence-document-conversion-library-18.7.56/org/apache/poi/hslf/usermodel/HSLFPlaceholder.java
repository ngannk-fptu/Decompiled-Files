/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hslf.usermodel;

import org.apache.poi.ddf.EscherContainerRecord;
import org.apache.poi.hslf.usermodel.HSLFShape;
import org.apache.poi.hslf.usermodel.HSLFTextBox;
import org.apache.poi.hslf.usermodel.HSLFTextParagraph;
import org.apache.poi.sl.usermodel.Placeholder;
import org.apache.poi.sl.usermodel.ShapeContainer;

public final class HSLFPlaceholder
extends HSLFTextBox {
    protected HSLFPlaceholder(EscherContainerRecord escherRecord, ShapeContainer<HSLFShape, HSLFTextParagraph> parent) {
        super(escherRecord, parent);
    }

    public HSLFPlaceholder(ShapeContainer<HSLFShape, HSLFTextParagraph> parent) {
        super(parent);
    }

    public HSLFPlaceholder() {
    }

    @Override
    protected EscherContainerRecord createSpContainer(boolean isChild) {
        EscherContainerRecord ecr = super.createSpContainer(isChild);
        this.setPlaceholder(Placeholder.BODY);
        return ecr;
    }
}

