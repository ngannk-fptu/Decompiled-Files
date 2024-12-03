/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.analysis.gl;

import org.apache.lucene.analysis.pt.RSLPStemmerBase;

public class GalicianMinimalStemmer
extends RSLPStemmerBase {
    private static final RSLPStemmerBase.Step pluralStep = GalicianMinimalStemmer.parse(GalicianMinimalStemmer.class, "galician.rslp").get("Plural");

    public int stem(char[] s, int len) {
        return pluralStep.apply(s, len);
    }
}

