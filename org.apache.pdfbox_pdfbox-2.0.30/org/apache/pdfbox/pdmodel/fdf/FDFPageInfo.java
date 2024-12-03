/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.fdf;

import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.pdmodel.common.COSObjectable;

public class FDFPageInfo
implements COSObjectable {
    private final COSDictionary pageInfo;

    public FDFPageInfo() {
        this.pageInfo = new COSDictionary();
    }

    public FDFPageInfo(COSDictionary p) {
        this.pageInfo = p;
    }

    @Override
    public COSDictionary getCOSObject() {
        return this.pageInfo;
    }
}

