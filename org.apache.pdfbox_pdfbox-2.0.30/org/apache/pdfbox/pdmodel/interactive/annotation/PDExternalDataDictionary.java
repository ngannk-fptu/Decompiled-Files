/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.interactive.annotation;

import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.common.COSObjectable;

public class PDExternalDataDictionary
implements COSObjectable {
    private final COSDictionary dataDictionary;

    public PDExternalDataDictionary() {
        this.dataDictionary = new COSDictionary();
        this.dataDictionary.setName(COSName.TYPE, "ExData");
    }

    public PDExternalDataDictionary(COSDictionary dictionary) {
        this.dataDictionary = dictionary;
    }

    @Override
    public COSDictionary getCOSObject() {
        return this.dataDictionary;
    }

    public String getType() {
        return this.getCOSObject().getNameAsString(COSName.TYPE, "ExData");
    }

    public String getSubtype() {
        return this.getCOSObject().getNameAsString(COSName.SUBTYPE);
    }

    public void setSubtype(String subtype) {
        this.getCOSObject().setName(COSName.SUBTYPE, subtype);
    }
}

