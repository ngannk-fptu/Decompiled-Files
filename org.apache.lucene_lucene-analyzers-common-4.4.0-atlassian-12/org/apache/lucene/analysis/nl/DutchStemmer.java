/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.analysis.nl;

import java.util.Locale;
import java.util.Map;

@Deprecated
public class DutchStemmer {
    private static final Locale locale = new Locale("nl", "NL");
    private StringBuilder sb = new StringBuilder();
    private boolean _removedE;
    private Map _stemDict;
    private int _R1;
    private int _R2;

    public String stem(String term) {
        if (!this.isStemmable(term = term.toLowerCase(locale))) {
            return term;
        }
        if (this._stemDict != null && this._stemDict.containsKey(term)) {
            if (this._stemDict.get(term) instanceof String) {
                return (String)this._stemDict.get(term);
            }
            return null;
        }
        this.sb.delete(0, this.sb.length());
        this.sb.insert(0, term);
        this.substitute(this.sb);
        this.storeYandI(this.sb);
        this._R1 = this.getRIndex(this.sb, 0);
        this._R1 = Math.max(3, this._R1);
        this.step1(this.sb);
        this.step2(this.sb);
        this._R2 = this.getRIndex(this.sb, this._R1);
        this.step3a(this.sb);
        this.step3b(this.sb);
        this.step4(this.sb);
        this.reStoreYandI(this.sb);
        return this.sb.toString();
    }

    private boolean enEnding(StringBuilder sb) {
        String[] enend = new String[]{"ene", "en"};
        for (int i = 0; i < enend.length; ++i) {
            String end = enend[i];
            String s = sb.toString();
            int index = s.length() - end.length();
            if (!s.endsWith(end) || index < this._R1 || !this.isValidEnEnding(sb, index - 1)) continue;
            sb.delete(index, index + end.length());
            this.unDouble(sb, index);
            return true;
        }
        return false;
    }

    private void step1(StringBuilder sb) {
        int index;
        if (this._R1 >= sb.length()) {
            return;
        }
        String s = sb.toString();
        int lengthR1 = sb.length() - this._R1;
        if (s.endsWith("heden")) {
            sb.replace(this._R1, lengthR1 + this._R1, sb.substring(this._R1, lengthR1 + this._R1).replaceAll("heden", "heid"));
            return;
        }
        if (this.enEnding(sb)) {
            return;
        }
        if (s.endsWith("se") && (index = s.length() - 2) >= this._R1 && this.isValidSEnding(sb, index - 1)) {
            sb.delete(index, index + 2);
            return;
        }
        if (s.endsWith("s") && (index = s.length() - 1) >= this._R1 && this.isValidSEnding(sb, index - 1)) {
            sb.delete(index, index + 1);
        }
    }

    private void step2(StringBuilder sb) {
        this._removedE = false;
        if (this._R1 >= sb.length()) {
            return;
        }
        String s = sb.toString();
        int index = s.length() - 1;
        if (index >= this._R1 && s.endsWith("e") && !this.isVowel(sb.charAt(index - 1))) {
            sb.delete(index, index + 1);
            this.unDouble(sb);
            this._removedE = true;
        }
    }

    private void step3a(StringBuilder sb) {
        if (this._R2 >= sb.length()) {
            return;
        }
        String s = sb.toString();
        int index = s.length() - 4;
        if (s.endsWith("heid") && index >= this._R2 && sb.charAt(index - 1) != 'c') {
            sb.delete(index, index + 4);
            this.enEnding(sb);
        }
    }

    private void step3b(StringBuilder sb) {
        if (this._R2 >= sb.length()) {
            return;
        }
        String s = sb.toString();
        int index = 0;
        if ((s.endsWith("end") || s.endsWith("ing")) && (index = s.length() - 3) >= this._R2) {
            sb.delete(index, index + 3);
            if (sb.charAt(index - 2) == 'i' && sb.charAt(index - 1) == 'g') {
                if (sb.charAt(index - 3) != 'e' & index - 2 >= this._R2) {
                    sb.delete(index -= 2, index + 2);
                }
            } else {
                this.unDouble(sb, index);
            }
            return;
        }
        if (s.endsWith("ig") && (index = s.length() - 2) >= this._R2) {
            if (sb.charAt(index - 1) != 'e') {
                sb.delete(index, index + 2);
            }
            return;
        }
        if (s.endsWith("lijk") && (index = s.length() - 4) >= this._R2) {
            sb.delete(index, index + 4);
            this.step2(sb);
            return;
        }
        if (s.endsWith("baar") && (index = s.length() - 4) >= this._R2) {
            sb.delete(index, index + 4);
            return;
        }
        if (s.endsWith("bar") && (index = s.length() - 3) >= this._R2) {
            if (this._removedE) {
                sb.delete(index, index + 3);
            }
            return;
        }
    }

    private void step4(StringBuilder sb) {
        if (sb.length() < 4) {
            return;
        }
        String end = sb.substring(sb.length() - 4, sb.length());
        char c = end.charAt(0);
        char v1 = end.charAt(1);
        char v2 = end.charAt(2);
        char d = end.charAt(3);
        if (v1 == v2 && d != 'I' && v1 != 'i' && this.isVowel(v1) && !this.isVowel(d) && !this.isVowel(c)) {
            sb.delete(sb.length() - 2, sb.length() - 1);
        }
    }

    private boolean isStemmable(String term) {
        for (int c = 0; c < term.length(); ++c) {
            if (Character.isLetter(term.charAt(c))) continue;
            return false;
        }
        return true;
    }

    private void substitute(StringBuilder buffer) {
        block7: for (int i = 0; i < buffer.length(); ++i) {
            switch (buffer.charAt(i)) {
                case '\u00e1': 
                case '\u00e4': {
                    buffer.setCharAt(i, 'a');
                    continue block7;
                }
                case '\u00e9': 
                case '\u00eb': {
                    buffer.setCharAt(i, 'e');
                    continue block7;
                }
                case '\u00fa': 
                case '\u00fc': {
                    buffer.setCharAt(i, 'u');
                    continue block7;
                }
                case 'i': 
                case '\u00ef': {
                    buffer.setCharAt(i, 'i');
                    continue block7;
                }
                case '\u00f3': 
                case '\u00f6': {
                    buffer.setCharAt(i, 'o');
                }
            }
        }
    }

    private boolean isValidSEnding(StringBuilder sb, int index) {
        char c = sb.charAt(index);
        return !this.isVowel(c) && c != 'j';
    }

    private boolean isValidEnEnding(StringBuilder sb, int index) {
        char c = sb.charAt(index);
        if (this.isVowel(c)) {
            return false;
        }
        if (c < '\u0003') {
            return false;
        }
        return c != 'm' || sb.charAt(index - 2) != 'g' || sb.charAt(index - 1) != 'e';
    }

    private void unDouble(StringBuilder sb) {
        this.unDouble(sb, sb.length());
    }

    private void unDouble(StringBuilder sb, int endIndex) {
        String s = sb.substring(0, endIndex);
        if (s.endsWith("kk") || s.endsWith("tt") || s.endsWith("dd") || s.endsWith("nn") || s.endsWith("mm") || s.endsWith("ff")) {
            sb.delete(endIndex - 1, endIndex);
        }
    }

    private int getRIndex(StringBuilder sb, int start) {
        int i;
        if (start == 0) {
            start = 1;
        }
        for (i = start; i < sb.length(); ++i) {
            if (this.isVowel(sb.charAt(i)) || !this.isVowel(sb.charAt(i - 1))) continue;
            return i + 1;
        }
        return i + 1;
    }

    private void storeYandI(StringBuilder sb) {
        if (sb.charAt(0) == 'y') {
            sb.setCharAt(0, 'Y');
        }
        int last = sb.length() - 1;
        block4: for (int i = 1; i < last; ++i) {
            switch (sb.charAt(i)) {
                case 'i': {
                    if (!this.isVowel(sb.charAt(i - 1)) || !this.isVowel(sb.charAt(i + 1))) continue block4;
                    sb.setCharAt(i, 'I');
                    continue block4;
                }
                case 'y': {
                    if (!this.isVowel(sb.charAt(i - 1))) continue block4;
                    sb.setCharAt(i, 'Y');
                }
            }
        }
        if (last > 0 && sb.charAt(last) == 'y' && this.isVowel(sb.charAt(last - 1))) {
            sb.setCharAt(last, 'Y');
        }
    }

    private void reStoreYandI(StringBuilder sb) {
        String tmp = sb.toString();
        sb.delete(0, sb.length());
        sb.insert(0, tmp.replaceAll("I", "i").replaceAll("Y", "y"));
    }

    private boolean isVowel(char c) {
        switch (c) {
            case 'a': 
            case 'e': 
            case 'i': 
            case 'o': 
            case 'u': 
            case 'y': 
            case '\u00e8': {
                return true;
            }
        }
        return false;
    }

    void setStemDictionary(Map dict) {
        this._stemDict = dict;
    }
}

