/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.analysis.fr;

import java.util.Locale;

@Deprecated
public class FrenchStemmer {
    private static final Locale locale = new Locale("fr", "FR");
    private StringBuilder sb = new StringBuilder();
    private StringBuilder tb = new StringBuilder();
    private String R0;
    private String RV;
    private String R1;
    private String R2;
    private boolean suite;
    private boolean modified;

    protected String stem(String term) {
        if (!this.isStemmable(term)) {
            return term;
        }
        term = term.toLowerCase(locale);
        this.sb.delete(0, this.sb.length());
        this.sb.insert(0, term);
        this.modified = false;
        this.suite = false;
        this.sb = this.treatVowels(this.sb);
        this.setStrings();
        this.step1();
        if ((!this.modified || this.suite) && this.RV != null) {
            this.suite = this.step2a();
            if (!this.suite) {
                this.step2b();
            }
        }
        if (this.modified || this.suite) {
            this.step3();
        } else {
            this.step4();
        }
        this.step5();
        this.step6();
        return this.sb.toString();
    }

    private void setStrings() {
        this.R0 = this.sb.toString();
        this.RV = this.retrieveRV(this.sb);
        this.R1 = this.retrieveR(this.sb);
        if (this.R1 != null) {
            this.tb.delete(0, this.tb.length());
            this.tb.insert(0, this.R1);
            this.R2 = this.retrieveR(this.tb);
        } else {
            this.R2 = null;
        }
    }

    private void step1() {
        String[] suffix = new String[]{"ances", "iqUes", "ismes", "ables", "istes", "ance", "iqUe", "isme", "able", "iste"};
        this.deleteFrom(this.R2, suffix);
        this.replaceFrom(this.R2, new String[]{"logies", "logie"}, "log");
        this.replaceFrom(this.R2, new String[]{"usions", "utions", "usion", "ution"}, "u");
        this.replaceFrom(this.R2, new String[]{"ences", "ence"}, "ent");
        String[] search = new String[]{"atrices", "ateurs", "ations", "atrice", "ateur", "ation"};
        this.deleteButSuffixFromElseReplace(this.R2, search, "ic", true, this.R0, "iqU");
        this.deleteButSuffixFromElseReplace(this.R2, new String[]{"ements", "ement"}, "eus", false, this.R0, "eux");
        this.deleteButSuffixFrom(this.R2, new String[]{"ements", "ement"}, "ativ", false);
        this.deleteButSuffixFrom(this.R2, new String[]{"ements", "ement"}, "iv", false);
        this.deleteButSuffixFrom(this.R2, new String[]{"ements", "ement"}, "abl", false);
        this.deleteButSuffixFrom(this.R2, new String[]{"ements", "ement"}, "iqU", false);
        this.deleteFromIfTestVowelBeforeIn(this.R1, new String[]{"issements", "issement"}, false, this.R0);
        this.deleteFrom(this.RV, new String[]{"ements", "ement"});
        this.deleteButSuffixFromElseReplace(this.R2, new String[]{"it\u00e9s", "it\u00e9"}, "abil", false, this.R0, "abl");
        this.deleteButSuffixFromElseReplace(this.R2, new String[]{"it\u00e9s", "it\u00e9"}, "ic", false, this.R0, "iqU");
        this.deleteButSuffixFrom(this.R2, new String[]{"it\u00e9s", "it\u00e9"}, "iv", true);
        String[] autre = new String[]{"ifs", "ives", "if", "ive"};
        this.deleteButSuffixFromElseReplace(this.R2, autre, "icat", false, this.R0, "iqU");
        this.deleteButSuffixFromElseReplace(this.R2, autre, "at", true, this.R2, "iqU");
        this.replaceFrom(this.R0, new String[]{"eaux"}, "eau");
        this.replaceFrom(this.R1, new String[]{"aux"}, "al");
        this.deleteButSuffixFromElseReplace(this.R2, new String[]{"euses", "euse"}, "", true, this.R1, "eux");
        this.deleteFrom(this.R2, new String[]{"eux"});
        boolean temp = false;
        temp = this.replaceFrom(this.RV, new String[]{"amment"}, "ant");
        if (temp) {
            this.suite = true;
        }
        if (temp = this.replaceFrom(this.RV, new String[]{"emment"}, "ent")) {
            this.suite = true;
        }
        if (temp = this.deleteFromIfTestVowelBeforeIn(this.RV, new String[]{"ments", "ment"}, true, this.RV)) {
            this.suite = true;
        }
    }

    private boolean step2a() {
        String[] search = new String[]{"\u00eemes", "\u00eetes", "iraIent", "irait", "irais", "irai", "iras", "ira", "irent", "iriez", "irez", "irions", "irons", "iront", "issaIent", "issais", "issantes", "issante", "issants", "issant", "issait", "issais", "issions", "issons", "issiez", "issez", "issent", "isses", "isse", "ir", "is", "\u00eet", "it", "ies", "ie", "i"};
        return this.deleteFromIfTestVowelBeforeIn(this.RV, search, false, this.RV);
    }

    private void step2b() {
        String[] suffix = new String[]{"eraIent", "erais", "erait", "erai", "eras", "erions", "eriez", "erons", "eront", "erez", "\u00e8rent", "era", "\u00e9es", "iez", "\u00e9e", "\u00e9s", "er", "ez", "\u00e9"};
        this.deleteFrom(this.RV, suffix);
        String[] search = new String[]{"assions", "assiez", "assent", "asses", "asse", "aIent", "antes", "aIent", "Aient", "ante", "\u00e2mes", "\u00e2tes", "ants", "ant", "ait", "a\u00eet", "ais", "Ait", "A\u00eet", "Ais", "\u00e2t", "as", "ai", "Ai", "a"};
        this.deleteButSuffixFrom(this.RV, search, "e", true);
        this.deleteFrom(this.R2, new String[]{"ions"});
    }

    private void step3() {
        if (this.sb.length() > 0) {
            char ch = this.sb.charAt(this.sb.length() - 1);
            if (ch == 'Y') {
                this.sb.setCharAt(this.sb.length() - 1, 'i');
                this.setStrings();
            } else if (ch == '\u00e7') {
                this.sb.setCharAt(this.sb.length() - 1, 'c');
                this.setStrings();
            }
        }
    }

    private void step4() {
        boolean found;
        char b;
        char ch;
        if (this.sb.length() > 1 && (ch = this.sb.charAt(this.sb.length() - 1)) == 's' && (b = this.sb.charAt(this.sb.length() - 2)) != 'a' && b != 'i' && b != 'o' && b != 'u' && b != '\u00e8' && b != 's') {
            this.sb.delete(this.sb.length() - 1, this.sb.length());
            this.setStrings();
        }
        if (!(found = this.deleteFromIfPrecededIn(this.R2, new String[]{"ion"}, this.RV, "s"))) {
            found = this.deleteFromIfPrecededIn(this.R2, new String[]{"ion"}, this.RV, "t");
        }
        this.replaceFrom(this.RV, new String[]{"I\u00e8re", "i\u00e8re", "Ier", "ier"}, "i");
        this.deleteFrom(this.RV, new String[]{"e"});
        this.deleteFromIfPrecededIn(this.RV, new String[]{"\u00eb"}, this.R0, "gu");
    }

    private void step5() {
        if (this.R0 != null && (this.R0.endsWith("enn") || this.R0.endsWith("onn") || this.R0.endsWith("ett") || this.R0.endsWith("ell") || this.R0.endsWith("eill"))) {
            this.sb.delete(this.sb.length() - 1, this.sb.length());
            this.setStrings();
        }
    }

    private void step6() {
        if (this.R0 != null && this.R0.length() > 0) {
            boolean seenVowel = false;
            boolean seenConson = false;
            int pos = -1;
            for (int i = this.R0.length() - 1; i > -1; --i) {
                char ch = this.R0.charAt(i);
                if (this.isVowel(ch)) {
                    if (!(seenVowel || ch != '\u00e9' && ch != '\u00e8')) {
                        pos = i;
                        break;
                    }
                    seenVowel = true;
                    continue;
                }
                if (seenVowel) break;
                seenConson = true;
            }
            if (pos > -1 && seenConson && !seenVowel) {
                this.sb.setCharAt(pos, 'e');
            }
        }
    }

    private boolean deleteFromIfPrecededIn(String source, String[] search, String from, String prefix) {
        boolean found = false;
        if (source != null) {
            for (int i = 0; i < search.length; ++i) {
                if (!source.endsWith(search[i]) || from == null || !from.endsWith(prefix + search[i])) continue;
                this.sb.delete(this.sb.length() - search[i].length(), this.sb.length());
                found = true;
                this.setStrings();
                break;
            }
        }
        return found;
    }

    private boolean deleteFromIfTestVowelBeforeIn(String source, String[] search, boolean vowel, String from) {
        boolean found = false;
        if (source != null && from != null) {
            for (int i = 0; i < search.length; ++i) {
                boolean test;
                if (!source.endsWith(search[i]) || search[i].length() + 1 > from.length() || (test = this.isVowel(this.sb.charAt(this.sb.length() - (search[i].length() + 1)))) != vowel) continue;
                this.sb.delete(this.sb.length() - search[i].length(), this.sb.length());
                this.modified = true;
                found = true;
                this.setStrings();
                break;
            }
        }
        return found;
    }

    private void deleteButSuffixFrom(String source, String[] search, String prefix, boolean without) {
        if (source != null) {
            for (int i = 0; i < search.length; ++i) {
                if (source.endsWith(prefix + search[i])) {
                    this.sb.delete(this.sb.length() - (prefix.length() + search[i].length()), this.sb.length());
                    this.modified = true;
                    this.setStrings();
                    break;
                }
                if (!without || !source.endsWith(search[i])) continue;
                this.sb.delete(this.sb.length() - search[i].length(), this.sb.length());
                this.modified = true;
                this.setStrings();
                break;
            }
        }
    }

    private void deleteButSuffixFromElseReplace(String source, String[] search, String prefix, boolean without, String from, String replace) {
        if (source != null) {
            for (int i = 0; i < search.length; ++i) {
                if (source.endsWith(prefix + search[i])) {
                    this.sb.delete(this.sb.length() - (prefix.length() + search[i].length()), this.sb.length());
                    this.modified = true;
                    this.setStrings();
                    break;
                }
                if (from != null && from.endsWith(prefix + search[i])) {
                    this.sb.replace(this.sb.length() - (prefix.length() + search[i].length()), this.sb.length(), replace);
                    this.modified = true;
                    this.setStrings();
                    break;
                }
                if (!without || !source.endsWith(search[i])) continue;
                this.sb.delete(this.sb.length() - search[i].length(), this.sb.length());
                this.modified = true;
                this.setStrings();
                break;
            }
        }
    }

    private boolean replaceFrom(String source, String[] search, String replace) {
        boolean found = false;
        if (source != null) {
            for (int i = 0; i < search.length; ++i) {
                if (!source.endsWith(search[i])) continue;
                this.sb.replace(this.sb.length() - search[i].length(), this.sb.length(), replace);
                this.modified = true;
                found = true;
                this.setStrings();
                break;
            }
        }
        return found;
    }

    private void deleteFrom(String source, String[] suffix) {
        if (source != null) {
            for (int i = 0; i < suffix.length; ++i) {
                if (!source.endsWith(suffix[i])) continue;
                this.sb.delete(this.sb.length() - suffix[i].length(), this.sb.length());
                this.modified = true;
                this.setStrings();
                break;
            }
        }
    }

    private boolean isVowel(char ch) {
        switch (ch) {
            case 'a': 
            case 'e': 
            case 'i': 
            case 'o': 
            case 'u': 
            case 'y': 
            case '\u00e0': 
            case '\u00e2': 
            case '\u00e8': 
            case '\u00e9': 
            case '\u00ea': 
            case '\u00eb': 
            case '\u00ee': 
            case '\u00ef': 
            case '\u00f4': 
            case '\u00f9': 
            case '\u00fb': 
            case '\u00fc': {
                return true;
            }
        }
        return false;
    }

    private String retrieveR(StringBuilder buffer) {
        int len = buffer.length();
        int pos = -1;
        for (int c = 0; c < len; ++c) {
            if (!this.isVowel(buffer.charAt(c))) continue;
            pos = c;
            break;
        }
        if (pos > -1) {
            int consonne = -1;
            for (int c = pos; c < len; ++c) {
                if (this.isVowel(buffer.charAt(c))) continue;
                consonne = c;
                break;
            }
            if (consonne > -1 && consonne + 1 < len) {
                return buffer.substring(consonne + 1, len);
            }
            return null;
        }
        return null;
    }

    private String retrieveRV(StringBuilder buffer) {
        int len = buffer.length();
        if (buffer.length() > 3) {
            if (this.isVowel(buffer.charAt(0)) && this.isVowel(buffer.charAt(1))) {
                return buffer.substring(3, len);
            }
            int pos = 0;
            for (int c = 1; c < len; ++c) {
                if (!this.isVowel(buffer.charAt(c))) continue;
                pos = c;
                break;
            }
            if (pos + 1 < len) {
                return buffer.substring(pos + 1, len);
            }
            return null;
        }
        return null;
    }

    private StringBuilder treatVowels(StringBuilder buffer) {
        for (int c = 0; c < buffer.length(); ++c) {
            char ch = buffer.charAt(c);
            if (c == 0) {
                if (buffer.length() <= 1 || ch != 'y' || !this.isVowel(buffer.charAt(c + 1))) continue;
                buffer.setCharAt(c, 'Y');
                continue;
            }
            if (c == buffer.length() - 1) {
                if (ch == 'u' && buffer.charAt(c - 1) == 'q') {
                    buffer.setCharAt(c, 'U');
                }
                if (ch != 'y' || !this.isVowel(buffer.charAt(c - 1))) continue;
                buffer.setCharAt(c, 'Y');
                continue;
            }
            if (ch == 'u') {
                if (buffer.charAt(c - 1) == 'q') {
                    buffer.setCharAt(c, 'U');
                } else if (this.isVowel(buffer.charAt(c - 1)) && this.isVowel(buffer.charAt(c + 1))) {
                    buffer.setCharAt(c, 'U');
                }
            }
            if (ch == 'i' && this.isVowel(buffer.charAt(c - 1)) && this.isVowel(buffer.charAt(c + 1))) {
                buffer.setCharAt(c, 'I');
            }
            if (ch != 'y' || !this.isVowel(buffer.charAt(c - 1)) && !this.isVowel(buffer.charAt(c + 1))) continue;
            buffer.setCharAt(c, 'Y');
        }
        return buffer;
    }

    private boolean isStemmable(String term) {
        boolean upper = false;
        int first = -1;
        for (int c = 0; c < term.length(); ++c) {
            if (!Character.isLetter(term.charAt(c))) {
                return false;
            }
            if (!Character.isUpperCase(term.charAt(c))) continue;
            if (upper) {
                return false;
            }
            first = c;
            upper = true;
        }
        return first <= 0;
    }
}

