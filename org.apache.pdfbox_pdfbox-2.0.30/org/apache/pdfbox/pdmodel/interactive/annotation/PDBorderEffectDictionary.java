/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.interactive.annotation;

import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.pdmodel.common.COSObjectable;

public class PDBorderEffectDictionary
implements COSObjectable {
    public static final String STYLE_SOLID = "S";
    public static final String STYLE_CLOUDY = "C";
    private final COSDictionary dictionary;

    public PDBorderEffectDictionary() {
        this.dictionary = new COSDictionary();
    }

    public PDBorderEffectDictionary(COSDictionary dict) {
        this.dictionary = dict;
    }

    @Override
    public COSDictionary getCOSObject() {
        return this.dictionary;
    }

    public void setIntensity(float i) {
        this.getCOSObject().setFloat("I", i);
    }

    public float getIntensity() {
        return this.getCOSObject().getFloat("I", 0.0f);
    }

    public void setStyle(String s) {
        this.getCOSObject().setName(STYLE_SOLID, s);
    }

    public String getStyle() {
        return this.getCOSObject().getNameAsString(STYLE_SOLID, STYLE_SOLID);
    }
}

