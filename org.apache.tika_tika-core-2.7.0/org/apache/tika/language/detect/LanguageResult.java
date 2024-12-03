/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.language.detect;

import java.util.Locale;
import org.apache.tika.language.detect.LanguageConfidence;

public class LanguageResult {
    public static final LanguageResult NULL = new LanguageResult("", LanguageConfidence.NONE, 0.0f);
    private final String language;
    private final LanguageConfidence confidence;
    private final float rawScore;

    public LanguageResult(String language, LanguageConfidence confidence, float rawScore) {
        this.language = language;
        this.confidence = confidence;
        this.rawScore = rawScore;
    }

    public String getLanguage() {
        return this.language;
    }

    public float getRawScore() {
        return this.rawScore;
    }

    public LanguageConfidence getConfidence() {
        return this.confidence;
    }

    public boolean isReasonablyCertain() {
        return this.confidence == LanguageConfidence.HIGH;
    }

    public boolean isUnknown() {
        return this.confidence == LanguageConfidence.NONE;
    }

    public boolean isLanguage(String language) {
        String[] targetLanguage = language.split("\\-");
        String[] resultLanguage = this.language.split("\\-");
        int minLength = Math.min(targetLanguage.length, resultLanguage.length);
        for (int i = 0; i < minLength; ++i) {
            if (targetLanguage[i].equalsIgnoreCase(resultLanguage[i])) continue;
            return false;
        }
        return true;
    }

    public String toString() {
        return String.format(Locale.US, "%s: %s (%f)", new Object[]{this.language, this.confidence, Float.valueOf(this.rawScore)});
    }
}

