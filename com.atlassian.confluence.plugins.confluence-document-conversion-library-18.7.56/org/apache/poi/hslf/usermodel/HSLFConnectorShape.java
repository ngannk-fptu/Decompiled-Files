/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hslf.usermodel;

import org.apache.poi.ddf.EscherContainerRecord;
import org.apache.poi.hslf.usermodel.HSLFGroupShape;
import org.apache.poi.hslf.usermodel.HSLFShape;
import org.apache.poi.hslf.usermodel.HSLFSimpleShape;
import org.apache.poi.hslf.usermodel.HSLFTextParagraph;
import org.apache.poi.sl.usermodel.ConnectorShape;
import org.apache.poi.sl.usermodel.ShapeContainer;

public class HSLFConnectorShape
extends HSLFSimpleShape
implements ConnectorShape<HSLFShape, HSLFTextParagraph> {
    protected HSLFConnectorShape(EscherContainerRecord escherRecord, ShapeContainer<HSLFShape, HSLFTextParagraph> parent) {
        super(escherRecord, parent);
    }

    public HSLFConnectorShape(ShapeContainer<HSLFShape, HSLFTextParagraph> parent) {
        super(null, parent);
        this.createSpContainer(parent instanceof HSLFGroupShape);
    }

    public HSLFConnectorShape() {
        this(null);
    }
}

