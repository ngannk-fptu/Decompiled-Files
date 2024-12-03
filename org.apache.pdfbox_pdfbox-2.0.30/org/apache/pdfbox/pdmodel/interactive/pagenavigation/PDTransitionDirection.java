/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.interactive.pagenavigation;

import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSInteger;
import org.apache.pdfbox.cos.COSName;

public enum PDTransitionDirection {
    LEFT_TO_RIGHT(0),
    BOTTOM_TO_TOP(90),
    RIGHT_TO_LEFT(180),
    TOP_TO_BOTTOM(270),
    TOP_LEFT_TO_BOTTOM_RIGHT(315),
    NONE(0){

        @Override
        public COSBase getCOSBase() {
            return COSName.NONE;
        }
    };

    private final int degrees;

    private PDTransitionDirection(int degrees) {
        this.degrees = degrees;
    }

    public COSBase getCOSBase() {
        return COSInteger.get(this.degrees);
    }
}

