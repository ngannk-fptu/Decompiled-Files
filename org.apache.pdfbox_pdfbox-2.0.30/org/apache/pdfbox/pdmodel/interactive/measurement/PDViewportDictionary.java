/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.interactive.measurement;

import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.interactive.measurement.PDMeasureDictionary;

public class PDViewportDictionary
implements COSObjectable {
    public static final String TYPE = "Viewport";
    private final COSDictionary viewportDictionary;

    public PDViewportDictionary() {
        this.viewportDictionary = new COSDictionary();
    }

    public PDViewportDictionary(COSDictionary dictionary) {
        this.viewportDictionary = dictionary;
    }

    @Override
    public COSDictionary getCOSObject() {
        return this.viewportDictionary;
    }

    public String getType() {
        return TYPE;
    }

    public PDRectangle getBBox() {
        COSBase bbox = this.getCOSObject().getDictionaryObject(COSName.BBOX);
        if (bbox instanceof COSArray) {
            return new PDRectangle((COSArray)bbox);
        }
        return null;
    }

    public void setBBox(PDRectangle rectangle) {
        this.getCOSObject().setItem(COSName.BBOX, (COSObjectable)rectangle);
    }

    public String getName() {
        return this.getCOSObject().getNameAsString(COSName.NAME);
    }

    public void setName(String name) {
        this.getCOSObject().setName(COSName.NAME, name);
    }

    public PDMeasureDictionary getMeasure() {
        COSBase base = this.getCOSObject().getDictionaryObject(COSName.MEASURE);
        if (base instanceof COSDictionary) {
            return new PDMeasureDictionary((COSDictionary)base);
        }
        return null;
    }

    public void setMeasure(PDMeasureDictionary measure) {
        this.getCOSObject().setItem(COSName.MEASURE, (COSObjectable)measure);
    }
}

