/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.fdf;

import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.cos.COSString;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.pdmodel.interactive.action.PDAction;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionFactory;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionJavaScript;

public class FDFJavaScript
implements COSObjectable {
    private final COSDictionary dictionary;

    public FDFJavaScript() {
        this.dictionary = new COSDictionary();
    }

    public FDFJavaScript(COSDictionary javaScript) {
        this.dictionary = javaScript;
    }

    @Override
    public COSDictionary getCOSObject() {
        return this.dictionary;
    }

    public String getBefore() {
        COSBase base = this.dictionary.getDictionaryObject(COSName.BEFORE);
        if (base instanceof COSString) {
            return ((COSString)base).getString();
        }
        if (base instanceof COSStream) {
            return ((COSStream)base).toTextString();
        }
        return null;
    }

    public void setBefore(String before) {
        this.dictionary.setItem(COSName.BEFORE, (COSBase)new COSString(before));
    }

    public String getAfter() {
        COSBase base = this.dictionary.getDictionaryObject(COSName.AFTER);
        if (base instanceof COSString) {
            return ((COSString)base).getString();
        }
        if (base instanceof COSStream) {
            return ((COSStream)base).toTextString();
        }
        return null;
    }

    public void setAfter(String after) {
        this.dictionary.setItem(COSName.AFTER, (COSBase)new COSString(after));
    }

    public Map<String, PDActionJavaScript> getDoc() {
        COSArray array = this.dictionary.getCOSArray(COSName.DOC);
        if (array == null) {
            return null;
        }
        LinkedHashMap<String, PDActionJavaScript> map = new LinkedHashMap<String, PDActionJavaScript>();
        int i = 0;
        while (i + 1 < array.size()) {
            PDAction action;
            COSBase base;
            String name = array.getName(i);
            if (name != null && (base = array.getObject(i + 1)) instanceof COSDictionary && (action = PDActionFactory.createAction((COSDictionary)base)) instanceof PDActionJavaScript) {
                map.put(name, (PDActionJavaScript)action);
            }
            i += 2;
        }
        return map;
    }

    public void setDoc(Map<String, PDActionJavaScript> map) {
        COSArray array = new COSArray();
        for (Map.Entry<String, PDActionJavaScript> entry : map.entrySet()) {
            array.add(new COSString(entry.getKey()));
            array.add(entry.getValue());
        }
        this.dictionary.setItem(COSName.DOC, (COSBase)array);
    }
}

