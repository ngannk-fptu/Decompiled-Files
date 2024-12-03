/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.documentinterchange.markedcontent;

import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.pdmodel.graphics.optionalcontent.PDOptionalContentGroup;
import org.apache.pdfbox.pdmodel.graphics.optionalcontent.PDOptionalContentMembershipDictionary;

public class PDPropertyList
implements COSObjectable {
    protected final COSDictionary dict;

    public static PDPropertyList create(COSDictionary dict) {
        COSBase item = dict.getItem(COSName.TYPE);
        if (COSName.OCG.equals(item)) {
            return new PDOptionalContentGroup(dict);
        }
        if (COSName.OCMD.equals(item)) {
            return new PDOptionalContentMembershipDictionary(dict);
        }
        return new PDPropertyList(dict);
    }

    protected PDPropertyList() {
        this.dict = new COSDictionary();
    }

    protected PDPropertyList(COSDictionary dict) {
        this.dict = dict;
    }

    @Override
    public COSDictionary getCOSObject() {
        return this.dict;
    }
}

