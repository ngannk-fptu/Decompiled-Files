/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.interactive.action;

import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.pdmodel.common.COSObjectable;

public class PDURIDictionary
implements COSObjectable {
    private final COSDictionary uriDictionary;

    public PDURIDictionary() {
        this.uriDictionary = new COSDictionary();
    }

    public PDURIDictionary(COSDictionary dictionary) {
        this.uriDictionary = dictionary;
    }

    @Override
    public COSDictionary getCOSObject() {
        return this.uriDictionary;
    }

    public String getBase() {
        return this.getCOSObject().getString("Base");
    }

    public void setBase(String base) {
        this.getCOSObject().setString("Base", base);
    }
}

