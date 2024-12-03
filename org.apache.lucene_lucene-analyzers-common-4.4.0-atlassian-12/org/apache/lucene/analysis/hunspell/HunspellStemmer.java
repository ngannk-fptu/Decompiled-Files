/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.util.Version
 */
package org.apache.lucene.analysis.hunspell;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.lucene.analysis.hunspell.HunspellAffix;
import org.apache.lucene.analysis.hunspell.HunspellDictionary;
import org.apache.lucene.analysis.hunspell.HunspellWord;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.analysis.util.CharacterUtils;
import org.apache.lucene.util.Version;

public class HunspellStemmer {
    private final int recursionCap;
    private final HunspellDictionary dictionary;
    private final StringBuilder segment = new StringBuilder();
    private CharacterUtils charUtils = CharacterUtils.getInstance(Version.LUCENE_40);

    public HunspellStemmer(HunspellDictionary dictionary) {
        this(dictionary, 2);
    }

    public HunspellStemmer(HunspellDictionary dictionary, int recursionCap) {
        this.dictionary = dictionary;
        this.recursionCap = recursionCap;
    }

    public List<Stem> stem(String word) {
        return this.stem(word.toCharArray(), word.length());
    }

    public List<Stem> stem(char[] word, int length) {
        ArrayList<Stem> stems = new ArrayList<Stem>();
        if (this.dictionary.lookupWord(word, 0, length) != null) {
            stems.add(new Stem(word, length));
        }
        stems.addAll(this.stem(word, length, null, 0));
        return stems;
    }

    public List<Stem> uniqueStems(char[] word, int length) {
        ArrayList<Stem> stems = new ArrayList<Stem>();
        CharArraySet terms = new CharArraySet(this.dictionary.getVersion(), 8, this.dictionary.isIgnoreCase());
        if (this.dictionary.lookupWord(word, 0, length) != null) {
            stems.add(new Stem(word, length));
            terms.add(word);
        }
        List<Stem> otherStems = this.stem(word, length, null, 0);
        for (Stem s : otherStems) {
            if (terms.contains(s.stem)) continue;
            stems.add(s);
            terms.add(s.stem);
        }
        return stems;
    }

    private List<Stem> stem(char[] word, int length, char[] flags, int recursionDepth) {
        int i;
        ArrayList<Stem> stems = new ArrayList<Stem>();
        for (i = 0; i < length; ++i) {
            List<HunspellAffix> suffixes = this.dictionary.lookupSuffix(word, i, length - i);
            if (suffixes == null) continue;
            for (HunspellAffix suffix : suffixes) {
                if (!this.hasCrossCheckedFlag(suffix.getFlag(), flags)) continue;
                int deAffixedLength = length - suffix.getAppend().length();
                String strippedWord = new StringBuilder().append(word, 0, deAffixedLength).append(suffix.getStrip()).toString();
                List<Stem> stemList = this.applyAffix(strippedWord.toCharArray(), strippedWord.length(), suffix, recursionDepth);
                for (Stem stem : stemList) {
                    stem.addSuffix(suffix);
                }
                stems.addAll(stemList);
            }
        }
        for (i = length - 1; i >= 0; --i) {
            List<HunspellAffix> prefixes = this.dictionary.lookupPrefix(word, 0, i);
            if (prefixes == null) continue;
            for (HunspellAffix prefix : prefixes) {
                if (!this.hasCrossCheckedFlag(prefix.getFlag(), flags)) continue;
                int deAffixedStart = prefix.getAppend().length();
                int deAffixedLength = length - deAffixedStart;
                String strippedWord = new StringBuilder().append(prefix.getStrip()).append(word, deAffixedStart, deAffixedLength).toString();
                List<Stem> stemList = this.applyAffix(strippedWord.toCharArray(), strippedWord.length(), prefix, recursionDepth);
                for (Stem stem : stemList) {
                    stem.addPrefix(prefix);
                }
                stems.addAll(stemList);
            }
        }
        return stems;
    }

    public List<Stem> applyAffix(char[] strippedWord, int length, HunspellAffix affix, int recursionDepth) {
        if (this.dictionary.isIgnoreCase()) {
            this.charUtils.toLowerCase(strippedWord, 0, strippedWord.length);
        }
        this.segment.setLength(0);
        this.segment.append(strippedWord, 0, length);
        if (!affix.checkCondition(this.segment)) {
            return Collections.EMPTY_LIST;
        }
        ArrayList<Stem> stems = new ArrayList<Stem>();
        List<HunspellWord> words = this.dictionary.lookupWord(strippedWord, 0, length);
        if (words != null) {
            for (HunspellWord hunspellWord : words) {
                if (!hunspellWord.hasFlag(affix.getFlag())) continue;
                stems.add(new Stem(strippedWord, length));
            }
        }
        if (affix.isCrossProduct() && recursionDepth < this.recursionCap) {
            stems.addAll(this.stem(strippedWord, length, affix.getAppendFlags(), ++recursionDepth));
        }
        return stems;
    }

    private boolean hasCrossCheckedFlag(char flag, char[] flags) {
        return flags == null || Arrays.binarySearch(flags, flag) >= 0;
    }

    public static class Stem {
        private final List<HunspellAffix> prefixes = new ArrayList<HunspellAffix>();
        private final List<HunspellAffix> suffixes = new ArrayList<HunspellAffix>();
        private final char[] stem;
        private final int stemLength;

        public Stem(char[] stem, int stemLength) {
            this.stem = stem;
            this.stemLength = stemLength;
        }

        public void addPrefix(HunspellAffix prefix) {
            this.prefixes.add(0, prefix);
        }

        public void addSuffix(HunspellAffix suffix) {
            this.suffixes.add(suffix);
        }

        public List<HunspellAffix> getPrefixes() {
            return this.prefixes;
        }

        public List<HunspellAffix> getSuffixes() {
            return this.suffixes;
        }

        public char[] getStem() {
            return this.stem;
        }

        public int getStemLength() {
            return this.stemLength;
        }

        public String getStemString() {
            return new String(this.stem, 0, this.stemLength);
        }
    }
}

