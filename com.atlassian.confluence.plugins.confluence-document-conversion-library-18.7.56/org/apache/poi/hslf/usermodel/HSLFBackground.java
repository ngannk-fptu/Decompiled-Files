/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hslf.usermodel;

import org.apache.poi.ddf.EscherContainerRecord;
import org.apache.poi.hslf.usermodel.HSLFShape;
import org.apache.poi.hslf.usermodel.HSLFTextParagraph;
import org.apache.poi.sl.usermodel.Background;
import org.apache.poi.sl.usermodel.ShapeContainer;

public final class HSLFBackground
extends HSLFShape
implements Background<HSLFShape, HSLFTextParagraph> {
    protected HSLFBackground(EscherContainerRecord escherRecord, ShapeContainer<HSLFShape, HSLFTextParagraph> parent) {
        super(escherRecord, parent);
    }

    @Override
    protected EscherContainerRecord createSpContainer(boolean isChild) {
        return null;
    }
}

