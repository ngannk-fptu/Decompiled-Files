/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.documentinterchange.logicalstructure;

import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.pdmodel.common.COSObjectable;

public class PDParentTreeValue
implements COSObjectable {
    COSObjectable obj;

    public PDParentTreeValue(COSArray obj) {
        this.obj = obj;
    }

    public PDParentTreeValue(COSDictionary obj) {
        this.obj = obj;
    }

    @Override
    public COSBase getCOSObject() {
        return this.obj.getCOSObject();
    }

    public String toString() {
        return this.obj.toString();
    }
}

