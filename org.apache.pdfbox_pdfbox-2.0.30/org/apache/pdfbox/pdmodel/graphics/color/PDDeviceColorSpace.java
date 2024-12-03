/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.graphics.color;

import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.graphics.color.PDColorSpace;

public abstract class PDDeviceColorSpace
extends PDColorSpace {
    public String toString() {
        return this.getName();
    }

    @Override
    public COSBase getCOSObject() {
        return COSName.getPDFName(this.getName());
    }
}

