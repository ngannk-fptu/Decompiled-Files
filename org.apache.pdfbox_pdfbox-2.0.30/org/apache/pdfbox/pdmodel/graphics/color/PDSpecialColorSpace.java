/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.graphics.color;

import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.pdmodel.graphics.color.PDColorSpace;

public abstract class PDSpecialColorSpace
extends PDColorSpace {
    @Override
    public COSBase getCOSObject() {
        return this.array;
    }
}

