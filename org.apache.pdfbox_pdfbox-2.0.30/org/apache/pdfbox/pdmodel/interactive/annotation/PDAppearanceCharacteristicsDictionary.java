/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.interactive.annotation;

import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.color.PDColorSpace;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceCMYK;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceColorSpace;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceGray;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceRGB;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;

public class PDAppearanceCharacteristicsDictionary
implements COSObjectable {
    private final COSDictionary dictionary;

    public PDAppearanceCharacteristicsDictionary(COSDictionary dict) {
        this.dictionary = dict;
    }

    @Override
    public COSDictionary getCOSObject() {
        return this.dictionary;
    }

    public int getRotation() {
        return this.getCOSObject().getInt(COSName.R, 0);
    }

    public void setRotation(int rotation) {
        this.getCOSObject().setInt(COSName.R, rotation);
    }

    public PDColor getBorderColour() {
        return this.getColor(COSName.BC);
    }

    public void setBorderColour(PDColor c) {
        this.getCOSObject().setItem(COSName.BC, (COSBase)c.toCOSArray());
    }

    public PDColor getBackground() {
        return this.getColor(COSName.BG);
    }

    public void setBackground(PDColor c) {
        this.getCOSObject().setItem(COSName.BG, (COSBase)c.toCOSArray());
    }

    public String getNormalCaption() {
        return this.getCOSObject().getString(COSName.CA);
    }

    public void setNormalCaption(String caption) {
        this.getCOSObject().setString(COSName.CA, caption);
    }

    public String getRolloverCaption() {
        return this.getCOSObject().getString(COSName.RC);
    }

    public void setRolloverCaption(String caption) {
        this.getCOSObject().setString(COSName.RC, caption);
    }

    public String getAlternateCaption() {
        return this.getCOSObject().getString(COSName.AC);
    }

    public void setAlternateCaption(String caption) {
        this.getCOSObject().setString(COSName.AC, caption);
    }

    public PDFormXObject getNormalIcon() {
        COSBase i = this.getCOSObject().getDictionaryObject(COSName.I);
        if (i instanceof COSStream) {
            return new PDFormXObject((COSStream)i);
        }
        return null;
    }

    public PDFormXObject getRolloverIcon() {
        COSBase i = this.getCOSObject().getDictionaryObject(COSName.RI);
        if (i instanceof COSStream) {
            return new PDFormXObject((COSStream)i);
        }
        return null;
    }

    public PDFormXObject getAlternateIcon() {
        COSBase i = this.getCOSObject().getDictionaryObject(COSName.IX);
        if (i instanceof COSStream) {
            return new PDFormXObject((COSStream)i);
        }
        return null;
    }

    private PDColor getColor(COSName itemName) {
        COSBase c = this.getCOSObject().getItem(itemName);
        if (c instanceof COSArray) {
            PDDeviceColorSpace colorSpace;
            switch (((COSArray)c).size()) {
                case 1: {
                    colorSpace = PDDeviceGray.INSTANCE;
                    break;
                }
                case 3: {
                    colorSpace = PDDeviceRGB.INSTANCE;
                    break;
                }
                case 4: {
                    colorSpace = PDDeviceCMYK.INSTANCE;
                    break;
                }
                default: {
                    return null;
                }
            }
            return new PDColor((COSArray)c, (PDColorSpace)colorSpace);
        }
        return null;
    }
}

