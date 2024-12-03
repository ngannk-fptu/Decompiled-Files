/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.documentinterchange.logicalstructure;

import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.pdmodel.common.COSObjectable;

public class PDMarkInfo
implements COSObjectable {
    private final COSDictionary dictionary;

    public PDMarkInfo() {
        this.dictionary = new COSDictionary();
    }

    public PDMarkInfo(COSDictionary dic) {
        this.dictionary = dic;
    }

    @Override
    public COSDictionary getCOSObject() {
        return this.dictionary;
    }

    public boolean isMarked() {
        return this.dictionary.getBoolean("Marked", false);
    }

    public void setMarked(boolean value) {
        this.dictionary.setBoolean("Marked", value);
    }

    public boolean usesUserProperties() {
        return this.dictionary.getBoolean("UserProperties", false);
    }

    public void setUserProperties(boolean userProps) {
        this.dictionary.setBoolean("UserProperties", userProps);
    }

    public boolean isSuspect() {
        return this.dictionary.getBoolean("Suspects", false);
    }

    public void setSuspect(boolean suspect) {
        this.dictionary.setBoolean("Suspects", false);
    }
}

