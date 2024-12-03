/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.pages;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;
import org.apache.commons.lang3.StringUtils;

public class NaturalStringComparator
implements Comparator<String> {
    private Collator collator;

    public NaturalStringComparator() {
        this.collator = Collator.getInstance();
    }

    public NaturalStringComparator(Locale locale) {
        this.collator = Collator.getInstance(locale);
    }

    @Override
    public int compare(String arg0, String arg1) {
        return this.compareNatural(arg0, arg1);
    }

    private int compareNatural(String s, String t) {
        return this.compareNatural(s, t, false, this.collator);
    }

    private int compareNatural(String s, String t, boolean caseSensitive, Collator collator) {
        int sIndex = 0;
        int tIndex = 0;
        int sLength = StringUtils.length((CharSequence)s);
        int tLength = StringUtils.length((CharSequence)t);
        block0: while (sIndex != sLength || tIndex != tLength) {
            if (sIndex == sLength) {
                return -1;
            }
            if (tIndex == tLength) {
                return 1;
            }
            char sChar = s.charAt(sIndex);
            char tChar = t.charAt(tIndex);
            boolean sCharIsDigit = Character.isDigit(sChar);
            boolean tCharIsDigit = Character.isDigit(tChar);
            if (sCharIsDigit && tCharIsDigit) {
                boolean tAllZero;
                int sLeadingZeroCount = 0;
                while (sChar == '0') {
                    ++sLeadingZeroCount;
                    if (++sIndex == sLength) break;
                    sChar = s.charAt(sIndex);
                }
                int tLeadingZeroCount = 0;
                while (tChar == '0') {
                    ++tLeadingZeroCount;
                    if (++tIndex == tLength) break;
                    tChar = t.charAt(tIndex);
                }
                boolean sAllZero = sIndex == sLength || !Character.isDigit(sChar);
                boolean bl = tAllZero = tIndex == tLength || !Character.isDigit(tChar);
                if (sAllZero && tAllZero) continue;
                if (sAllZero && !tAllZero) {
                    return -1;
                }
                if (tAllZero) {
                    return 1;
                }
                int diff = 0;
                do {
                    if (diff == 0) {
                        diff = sChar - tChar;
                    }
                    if (++sIndex == sLength && ++tIndex == tLength) {
                        return diff != 0 ? diff : sLeadingZeroCount - tLeadingZeroCount;
                    }
                    if (sIndex == sLength) {
                        if (diff == 0) {
                            return -1;
                        }
                        return Character.isDigit(t.charAt(tIndex)) ? -1 : diff;
                    }
                    if (tIndex == tLength) {
                        if (diff == 0) {
                            return 1;
                        }
                        return Character.isDigit(s.charAt(sIndex)) ? 1 : diff;
                    }
                    sChar = s.charAt(sIndex);
                    tChar = t.charAt(tIndex);
                    sCharIsDigit = Character.isDigit(sChar);
                    tCharIsDigit = Character.isDigit(tChar);
                    if (!sCharIsDigit && !tCharIsDigit) {
                        if (diff == 0) continue block0;
                        return diff;
                    }
                    if (sCharIsDigit) continue;
                    return -1;
                } while (tCharIsDigit);
                return 1;
            }
            if (collator != null) {
                String bs;
                int aw = sIndex;
                int bw = tIndex;
                while (++sIndex < sLength && !Character.isDigit(s.charAt(sIndex))) {
                }
                while (++tIndex < tLength && !Character.isDigit(t.charAt(tIndex))) {
                }
                String as = s.substring(aw, sIndex);
                int subwordResult = collator.compare(as, bs = t.substring(bw, tIndex));
                if (subwordResult == 0) continue;
                return subwordResult;
            }
            do {
                if (sChar != tChar) {
                    if (caseSensitive) {
                        return sChar - tChar;
                    }
                    if ((sChar = Character.toUpperCase(sChar)) != (tChar = Character.toUpperCase(tChar)) && (sChar = Character.toLowerCase(sChar)) != (tChar = Character.toLowerCase(tChar))) {
                        return sChar - tChar;
                    }
                }
                if (++sIndex == sLength && ++tIndex == tLength) {
                    return 0;
                }
                if (sIndex == sLength) {
                    return -1;
                }
                if (tIndex == tLength) {
                    return 1;
                }
                sChar = s.charAt(sIndex);
                tChar = t.charAt(tIndex);
                sCharIsDigit = Character.isDigit(sChar);
                tCharIsDigit = Character.isDigit(tChar);
            } while (!sCharIsDigit && !tCharIsDigit);
        }
        return 0;
    }
}

