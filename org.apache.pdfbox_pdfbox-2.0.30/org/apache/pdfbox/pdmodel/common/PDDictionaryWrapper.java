/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.common;

import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.pdmodel.common.COSObjectable;

public class PDDictionaryWrapper
implements COSObjectable {
    private final COSDictionary dictionary;

    public PDDictionaryWrapper() {
        this.dictionary = new COSDictionary();
    }

    public PDDictionaryWrapper(COSDictionary dictionary) {
        this.dictionary = dictionary;
    }

    @Override
    public COSDictionary getCOSObject() {
        return this.dictionary;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof PDDictionaryWrapper) {
            return this.dictionary.equals(((PDDictionaryWrapper)obj).dictionary);
        }
        return false;
    }

    public int hashCode() {
        return this.dictionary.hashCode();
    }
}

