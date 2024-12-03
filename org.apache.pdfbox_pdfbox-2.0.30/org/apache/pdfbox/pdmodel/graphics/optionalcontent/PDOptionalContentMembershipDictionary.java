/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.graphics.optionalcontent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.documentinterchange.markedcontent.PDPropertyList;

public class PDOptionalContentMembershipDictionary
extends PDPropertyList {
    public PDOptionalContentMembershipDictionary() {
        this.dict.setItem(COSName.TYPE, (COSBase)COSName.OCMD);
    }

    public PDOptionalContentMembershipDictionary(COSDictionary dict) {
        super(dict);
        if (!dict.getItem(COSName.TYPE).equals(COSName.OCMD)) {
            throw new IllegalArgumentException("Provided dictionary is not of type '" + COSName.OCMD + "'");
        }
    }

    public List<PDPropertyList> getOCGs() {
        COSBase base = this.dict.getDictionaryObject(COSName.OCGS);
        if (base instanceof COSDictionary) {
            return Collections.singletonList(PDPropertyList.create((COSDictionary)base));
        }
        if (base instanceof COSArray) {
            COSArray ar = (COSArray)base;
            ArrayList<PDPropertyList> list = new ArrayList<PDPropertyList>();
            for (int i = 0; i < ar.size(); ++i) {
                COSBase elem = ar.getObject(i);
                if (!(elem instanceof COSDictionary)) continue;
                list.add(PDPropertyList.create((COSDictionary)elem));
            }
            return list;
        }
        return Collections.emptyList();
    }

    public void setOCGs(List<PDPropertyList> ocgs) {
        COSArray ar = new COSArray();
        for (PDPropertyList prop : ocgs) {
            ar.add(prop);
        }
        this.dict.setItem(COSName.OCGS, (COSBase)ar);
    }

    public COSName getVisibilityPolicy() {
        return this.dict.getCOSName(COSName.P, COSName.ANY_ON);
    }

    public void setVisibilityPolicy(COSName visibilityPolicy) {
        this.dict.setItem(COSName.P, (COSBase)visibilityPolicy);
    }
}

