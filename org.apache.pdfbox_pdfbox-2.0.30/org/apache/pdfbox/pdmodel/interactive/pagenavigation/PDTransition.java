/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.interactive.pagenavigation;

import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSBoolean;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSFloat;
import org.apache.pdfbox.cos.COSInteger;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.common.PDDictionaryWrapper;
import org.apache.pdfbox.pdmodel.interactive.pagenavigation.PDTransitionDimension;
import org.apache.pdfbox.pdmodel.interactive.pagenavigation.PDTransitionDirection;
import org.apache.pdfbox.pdmodel.interactive.pagenavigation.PDTransitionMotion;
import org.apache.pdfbox.pdmodel.interactive.pagenavigation.PDTransitionStyle;

public final class PDTransition
extends PDDictionaryWrapper {
    public PDTransition() {
        this(PDTransitionStyle.R);
    }

    public PDTransition(PDTransitionStyle style) {
        this.getCOSObject().setName(COSName.TYPE, COSName.TRANS.getName());
        this.getCOSObject().setName(COSName.S, style.name());
    }

    public PDTransition(COSDictionary dictionary) {
        super(dictionary);
    }

    public String getStyle() {
        return this.getCOSObject().getNameAsString(COSName.S, PDTransitionStyle.R.name());
    }

    public String getDimension() {
        return this.getCOSObject().getNameAsString(COSName.DM, PDTransitionDimension.H.name());
    }

    public void setDimension(PDTransitionDimension dimension) {
        this.getCOSObject().setName(COSName.DM, dimension.name());
    }

    public String getMotion() {
        return this.getCOSObject().getNameAsString(COSName.M, PDTransitionMotion.I.name());
    }

    public void setMotion(PDTransitionMotion motion) {
        this.getCOSObject().setName(COSName.M, motion.name());
    }

    public COSBase getDirection() {
        COSBase item = this.getCOSObject().getItem(COSName.DI);
        if (item == null) {
            return COSInteger.ZERO;
        }
        return item;
    }

    public void setDirection(PDTransitionDirection direction) {
        this.getCOSObject().setItem(COSName.DI, direction.getCOSBase());
    }

    public float getDuration() {
        return this.getCOSObject().getFloat(COSName.D, 1.0f);
    }

    public void setDuration(float duration) {
        this.getCOSObject().setItem(COSName.D, (COSBase)new COSFloat(duration));
    }

    public float getFlyScale() {
        return this.getCOSObject().getFloat(COSName.SS, 1.0f);
    }

    public void setFlyScale(float scale) {
        this.getCOSObject().setItem(COSName.SS, (COSBase)new COSFloat(scale));
    }

    public boolean isFlyAreaOpaque() {
        return this.getCOSObject().getBoolean(COSName.B, false);
    }

    public void setFlyAreaOpaque(boolean opaque) {
        this.getCOSObject().setItem(COSName.B, (COSBase)COSBoolean.getBoolean(opaque));
    }
}

