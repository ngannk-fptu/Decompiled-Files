/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.language.detect;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.tika.config.ServiceLoader;
import org.apache.tika.language.detect.LanguageResult;

public abstract class LanguageDetector {
    private static final ServiceLoader DEFAULT_SERVICE_LOADER = new ServiceLoader();
    private static final int BUFFER_LENGTH = 4096;
    protected boolean mixedLanguages = false;
    protected boolean shortText = false;

    public static LanguageDetector getDefaultLanguageDetector() {
        List<LanguageDetector> detectors = LanguageDetector.getLanguageDetectors();
        if (detectors.isEmpty()) {
            throw new IllegalStateException("No language detectors available");
        }
        return detectors.get(0);
    }

    public static List<LanguageDetector> getLanguageDetectors() {
        return LanguageDetector.getLanguageDetectors(DEFAULT_SERVICE_LOADER);
    }

    public static List<LanguageDetector> getLanguageDetectors(ServiceLoader loader) {
        List<LanguageDetector> detectors = loader.loadStaticServiceProviders(LanguageDetector.class);
        Collections.sort(detectors, new Comparator<LanguageDetector>(){

            @Override
            public int compare(LanguageDetector d1, LanguageDetector d2) {
                boolean tika2;
                String n1 = d1.getClass().getName();
                String n2 = d2.getClass().getName();
                boolean tika1 = n1.startsWith("org.apache.tika.");
                if (tika1 == (tika2 = n2.startsWith("org.apache.tika."))) {
                    return n1.compareTo(n2);
                }
                if (tika1) {
                    return -1;
                }
                return 1;
            }
        });
        return detectors;
    }

    public boolean isMixedLanguages() {
        return this.mixedLanguages;
    }

    public LanguageDetector setMixedLanguages(boolean mixedLanguages) {
        this.mixedLanguages = mixedLanguages;
        return this;
    }

    public boolean isShortText() {
        return this.shortText;
    }

    public LanguageDetector setShortText(boolean shortText) {
        this.shortText = shortText;
        return this;
    }

    public abstract LanguageDetector loadModels() throws IOException;

    public abstract LanguageDetector loadModels(Set<String> var1) throws IOException;

    public abstract boolean hasModel(String var1);

    public abstract LanguageDetector setPriors(Map<String, Float> var1) throws IOException;

    public abstract void reset();

    public abstract void addText(char[] var1, int var2, int var3);

    public void addText(CharSequence text) {
        int len = text.length();
        if (len < 4096) {
            char[] chars = text.toString().toCharArray();
            this.addText(chars, 0, chars.length);
            return;
        }
        for (int start = 0; !this.hasEnoughText() && start < len; start += 4096) {
            int end = Math.min(start + 4096, len);
            char[] chars = text.subSequence(start, end).toString().toCharArray();
            this.addText(chars, 0, chars.length);
        }
    }

    public boolean hasEnoughText() {
        return false;
    }

    public abstract List<LanguageResult> detectAll();

    public LanguageResult detect() {
        List<LanguageResult> results = this.detectAll();
        return results.get(0);
    }

    public List<LanguageResult> detectAll(String text) {
        this.reset();
        this.addText(text);
        return this.detectAll();
    }

    public LanguageResult detect(CharSequence text) {
        this.reset();
        this.addText(text);
        return this.detect();
    }
}

