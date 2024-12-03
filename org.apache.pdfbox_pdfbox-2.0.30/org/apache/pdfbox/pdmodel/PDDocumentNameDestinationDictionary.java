/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel;

import java.io.IOException;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDDestination;

public class PDDocumentNameDestinationDictionary
implements COSObjectable {
    private final COSDictionary nameDictionary;

    public PDDocumentNameDestinationDictionary(COSDictionary dict) {
        this.nameDictionary = dict;
    }

    @Override
    public COSDictionary getCOSObject() {
        return this.nameDictionary;
    }

    public PDDestination getDestination(String name) throws IOException {
        COSDictionary dict;
        COSBase item = this.nameDictionary.getDictionaryObject(name);
        if (item instanceof COSArray) {
            return PDDestination.create(item);
        }
        if (item instanceof COSDictionary && (dict = (COSDictionary)item).containsKey(COSName.D)) {
            return PDDestination.create(dict.getDictionaryObject(COSName.D));
        }
        return null;
    }
}

