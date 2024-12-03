/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.interactive.measurement;

import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.common.COSObjectable;

public class PDMeasureDictionary
implements COSObjectable {
    public static final String TYPE = "Measure";
    private final COSDictionary measureDictionary;

    protected PDMeasureDictionary() {
        this.measureDictionary = new COSDictionary();
        this.getCOSObject().setName(COSName.TYPE, TYPE);
    }

    public PDMeasureDictionary(COSDictionary dictionary) {
        this.measureDictionary = dictionary;
    }

    @Override
    public COSDictionary getCOSObject() {
        return this.measureDictionary;
    }

    public String getType() {
        return TYPE;
    }

    public String getSubtype() {
        return this.getCOSObject().getNameAsString(COSName.SUBTYPE, "RL");
    }

    protected void setSubtype(String subtype) {
        this.getCOSObject().setName(COSName.SUBTYPE, subtype);
    }
}

