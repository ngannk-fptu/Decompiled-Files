/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.font;

import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.common.COSObjectable;

public final class PDCIDSystemInfo
implements COSObjectable {
    private final COSDictionary dictionary;

    PDCIDSystemInfo(String registry, String ordering, int supplement) {
        this.dictionary = new COSDictionary();
        this.dictionary.setString(COSName.REGISTRY, registry);
        this.dictionary.setString(COSName.ORDERING, ordering);
        this.dictionary.setInt(COSName.SUPPLEMENT, supplement);
    }

    PDCIDSystemInfo(COSDictionary dictionary) {
        this.dictionary = dictionary;
    }

    public String getRegistry() {
        return this.dictionary.getNameAsString(COSName.REGISTRY);
    }

    public String getOrdering() {
        return this.dictionary.getNameAsString(COSName.ORDERING);
    }

    public int getSupplement() {
        return this.dictionary.getInt(COSName.SUPPLEMENT);
    }

    @Override
    public COSBase getCOSObject() {
        return this.dictionary;
    }

    public String toString() {
        return this.getRegistry() + "-" + this.getOrdering() + "-" + this.getSupplement();
    }
}

