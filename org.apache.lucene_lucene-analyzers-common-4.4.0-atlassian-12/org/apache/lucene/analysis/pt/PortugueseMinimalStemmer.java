/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.analysis.pt;

import org.apache.lucene.analysis.pt.RSLPStemmerBase;

public class PortugueseMinimalStemmer
extends RSLPStemmerBase {
    private static final RSLPStemmerBase.Step pluralStep = PortugueseMinimalStemmer.parse(PortugueseMinimalStemmer.class, "portuguese.rslp").get("Plural");

    public int stem(char[] s, int len) {
        return pluralStep.apply(s, len);
    }
}

