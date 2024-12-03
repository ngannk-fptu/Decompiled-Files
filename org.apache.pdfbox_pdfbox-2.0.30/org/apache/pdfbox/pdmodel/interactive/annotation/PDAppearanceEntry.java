/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.interactive.annotation;

import java.util.HashMap;
import java.util.Map;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.pdmodel.common.COSDictionaryMap;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAppearanceStream;

public class PDAppearanceEntry
implements COSObjectable {
    private COSBase entry;

    private PDAppearanceEntry() {
    }

    public PDAppearanceEntry(COSBase entry) {
        this.entry = entry;
    }

    @Override
    public COSBase getCOSObject() {
        return this.entry;
    }

    public boolean isSubDictionary() {
        return !(this.entry instanceof COSStream);
    }

    public boolean isStream() {
        return this.entry instanceof COSStream;
    }

    public PDAppearanceStream getAppearanceStream() {
        if (!this.isStream()) {
            throw new IllegalStateException("This entry is not an appearance stream");
        }
        return new PDAppearanceStream((COSStream)this.entry);
    }

    public Map<COSName, PDAppearanceStream> getSubDictionary() {
        if (!this.isSubDictionary()) {
            throw new IllegalStateException("This entry is not an appearance subdictionary");
        }
        COSDictionary dict = (COSDictionary)this.entry;
        HashMap<COSName, PDAppearanceStream> map = new HashMap<COSName, PDAppearanceStream>();
        for (COSName name : dict.keySet()) {
            COSBase value = dict.getDictionaryObject(name);
            if (!(value instanceof COSStream)) continue;
            map.put(name, new PDAppearanceStream((COSStream)value));
        }
        return new COSDictionaryMap<COSName, PDAppearanceStream>(map, dict);
    }
}

