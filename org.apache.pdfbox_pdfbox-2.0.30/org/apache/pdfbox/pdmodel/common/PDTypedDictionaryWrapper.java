/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.common;

import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.common.PDDictionaryWrapper;

public class PDTypedDictionaryWrapper
extends PDDictionaryWrapper {
    public PDTypedDictionaryWrapper(String type) {
        this.getCOSObject().setName(COSName.TYPE, type);
    }

    public PDTypedDictionaryWrapper(COSDictionary dictionary) {
        super(dictionary);
    }

    public String getType() {
        return this.getCOSObject().getNameAsString(COSName.TYPE);
    }
}

