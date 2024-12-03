/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.i18n.text;

import java.io.IOException;
import org.apache.abdera.i18n.text.Codepoint;
import org.apache.abdera.i18n.text.CodepointIterator;
import org.apache.abdera.i18n.text.Filter;
import org.apache.abdera.i18n.text.InvalidCharacterException;

public final class CharUtils {
    public static final char LRE = '\u202a';
    public static final char RLE = '\u202b';
    public static final char LRO = '\u202d';
    public static final char RLO = '\u202e';
    public static final char LRM = '\u200e';
    public static final char RLM = '\u200f';
    public static final char PDF = '\u202c';

    private CharUtils() {
    }

    public static boolean isValid(int c) {
        return c >= 0 && c <= 0x10FFFF;
    }

    public static boolean isValid(Codepoint c) {
        return CharUtils.isValid(c.getValue());
    }

    public static boolean inRange(char[] chars, char low, char high) {
        for (int i = 0; i < chars.length; ++i) {
            if (chars[i] >= low && chars[i] <= high) continue;
            return false;
        }
        return true;
    }

    public static boolean inRange(char[] chars, int low, int high) {
        for (int i = 0; i < chars.length; ++i) {
            char n = chars[i];
            Codepoint cp = CharUtils.isHighSurrogate(n) && i + 1 < chars.length && CharUtils.isLowSurrogate(chars[i + 1]) ? CharUtils.toSupplementary(n, chars[i++]) : new Codepoint(n);
            int c = cp.getValue();
            if (c >= low && c <= high) continue;
            return false;
        }
        return true;
    }

    public static boolean inRange(int codepoint, int low, int high) {
        return codepoint >= low && codepoint <= high;
    }

    public static void append(Appendable buf, Codepoint c) {
        CharUtils.append(buf, c.getValue());
    }

    public static void append(Appendable buf, int c) {
        try {
            if (CharUtils.isSupplementary(c)) {
                buf.append(CharUtils.getHighSurrogate(c));
                buf.append(CharUtils.getLowSurrogate(c));
            } else {
                buf.append((char)c);
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static char getHighSurrogate(int c) {
        return c >= 65536 ? (char)(55232 + (c >> 10)) : (char)'\u0000';
    }

    public static char getLowSurrogate(int c) {
        return c >= 65536 ? (char)(56320 + (c & 0x3FF)) : (char)c;
    }

    public static boolean isHighSurrogate(char c) {
        return c <= '\udbff' && c >= '\ud800';
    }

    public static boolean isLowSurrogate(char c) {
        return c <= '\udfff' && c >= '\udc00';
    }

    public static boolean isSupplementary(int c) {
        return c <= 0x10FFFF && c >= 65536;
    }

    public static boolean isSurrogatePair(char high, char low) {
        return CharUtils.isHighSurrogate(high) && CharUtils.isLowSurrogate(low);
    }

    public static Codepoint toSupplementary(char high, char low) {
        if (!CharUtils.isHighSurrogate(high)) {
            throw new IllegalArgumentException("Invalid High Surrogate");
        }
        if (!CharUtils.isLowSurrogate(low)) {
            throw new IllegalArgumentException("Invalid Low Surrogate");
        }
        return new Codepoint((high - 55296 << 10) + (low - 56320) + 65536);
    }

    public static Codepoint codepointAt(String s, int i) {
        char high;
        char c = s.charAt(i);
        if (c < '\ud800' || c > '\udfff') {
            return new Codepoint(c);
        }
        if (CharUtils.isHighSurrogate(c)) {
            char low;
            if (s.length() != i && CharUtils.isLowSurrogate(low = s.charAt(i + 1))) {
                return CharUtils.toSupplementary(c, low);
            }
        } else if (CharUtils.isLowSurrogate(c) && i >= 1 && CharUtils.isHighSurrogate(high = s.charAt(i - 1))) {
            return CharUtils.toSupplementary(high, c);
        }
        return new Codepoint(c);
    }

    public static Codepoint codepointAt(CharSequence s, int i) {
        char high;
        char c = s.charAt(i);
        if (c < '\ud800' || c > '\udfff') {
            return new Codepoint(c);
        }
        if (CharUtils.isHighSurrogate(c)) {
            char low;
            if (s.length() != i && CharUtils.isLowSurrogate(low = s.charAt(i + 1))) {
                return CharUtils.toSupplementary(c, low);
            }
        } else if (CharUtils.isLowSurrogate(c) && i >= 1 && CharUtils.isHighSurrogate(high = s.charAt(i - 1))) {
            return CharUtils.toSupplementary(high, c);
        }
        return new Codepoint(c);
    }

    public static void insert(CharSequence s, int i, Codepoint c) {
        CharUtils.insert(s, i, c.getValue());
    }

    public static void insert(CharSequence s, int i, int c) {
        if (!(s instanceof StringBuilder) && !(s instanceof StringBuffer)) {
            CharUtils.insert((CharSequence)new StringBuilder(s), i, c);
        } else {
            char ch;
            boolean low;
            if (i > 0 && i < s.length() && (low = CharUtils.isLowSurrogate(ch = s.charAt(i))) && low && CharUtils.isHighSurrogate(s.charAt(i - 1))) {
                --i;
            }
            if (s instanceof StringBuffer) {
                ((StringBuffer)s).insert(i, CharUtils.toString(c));
            } else if (s instanceof StringBuilder) {
                ((StringBuilder)s).insert(i, CharUtils.toString(c));
            }
        }
    }

    public static void setChar(CharSequence s, int i, Codepoint c) {
        CharUtils.setChar(s, i, c.getValue());
    }

    public static void setChar(CharSequence s, int i, int c) {
        if (!(s instanceof StringBuilder) && !(s instanceof StringBuffer)) {
            CharUtils.setChar((CharSequence)new StringBuilder(s), i, c);
        } else {
            int l = 1;
            char ch = s.charAt(i);
            boolean high = CharUtils.isHighSurrogate(ch);
            boolean low = CharUtils.isLowSurrogate(ch);
            if (high || low) {
                if (high && i + 1 < s.length() && CharUtils.isLowSurrogate(s.charAt(i + 1))) {
                    ++l;
                } else if (low && i > 0 && CharUtils.isHighSurrogate(s.charAt(i - 1))) {
                    --i;
                    ++l;
                }
            }
            if (s instanceof StringBuffer) {
                ((StringBuffer)s).replace(i, i + l, CharUtils.toString(c));
            } else if (s instanceof StringBuilder) {
                ((StringBuilder)s).replace(i, i + l, CharUtils.toString(c));
            }
        }
    }

    public static int length(Codepoint c) {
        return c.getCharCount();
    }

    public static int length(int c) {
        return new Codepoint(c).getCharCount();
    }

    public static int length(CharSequence c) {
        return CharUtils.length(CodepointIterator.forCharSequence(c));
    }

    public static int length(char[] c) {
        return CharUtils.length(CodepointIterator.forCharArray(c));
    }

    private static int length(CodepointIterator ci) {
        int n = 0;
        while (ci.hasNext()) {
            ci.next();
            ++n;
        }
        return n;
    }

    private static String supplementaryToString(int c) {
        StringBuilder buf = new StringBuilder();
        buf.append(CharUtils.getHighSurrogate(c));
        buf.append(CharUtils.getLowSurrogate(c));
        return buf.toString();
    }

    public static String toString(int c) {
        return CharUtils.isSupplementary(c) ? CharUtils.supplementaryToString(c) : String.valueOf((char)c);
    }

    public static String stripBidi(String s) {
        if (s == null || s.length() <= 1) {
            return s;
        }
        if (CharUtils.isBidi(s.charAt(0))) {
            s = s.substring(1);
        }
        if (CharUtils.isBidi(s.charAt(s.length() - 1))) {
            s = s.substring(0, s.length() - 1);
        }
        return s;
    }

    public static String stripBidiInternal(String s) {
        return s.replaceAll("[\u202a\u202b\u202d\u202e\u200e\u200f\u202c]", "");
    }

    private static String wrap(String s, char c1, char c2) {
        StringBuilder buf = new StringBuilder(s);
        if (buf.length() > 1) {
            if (buf.charAt(0) != c1) {
                buf.insert(0, c1);
            }
            if (buf.charAt(buf.length() - 1) != c2) {
                buf.append(c2);
            }
        }
        return buf.toString();
    }

    public static String wrapBidi(String s, char c) {
        switch (c) {
            case '\u202b': {
                return CharUtils.wrap(s, '\u202b', '\u202c');
            }
            case '\u202e': {
                return CharUtils.wrap(s, '\u202e', '\u202c');
            }
            case '\u202a': {
                return CharUtils.wrap(s, '\u202a', '\u202c');
            }
            case '\u202d': {
                return CharUtils.wrap(s, '\u202d', '\u202c');
            }
            case '\u200f': {
                return CharUtils.wrap(s, '\u200f', '\u200f');
            }
            case '\u200e': {
                return CharUtils.wrap(s, '\u200e', '\u200e');
            }
        }
        return s;
    }

    public static boolean isDigit(Codepoint codepoint) {
        return CharUtils.isDigit(codepoint.getValue());
    }

    public static boolean isDigit(int codepoint) {
        return CharUtils.inRange(codepoint, 48, 57);
    }

    public static boolean isAlpha(Codepoint codepoint) {
        return CharUtils.isAlpha(codepoint.getValue());
    }

    public static boolean isAlpha(int codepoint) {
        return CharUtils.inRange(codepoint, 65, 90) || CharUtils.inRange(codepoint, 97, 122);
    }

    public static boolean isAlphaDigit(Codepoint codepoint) {
        return CharUtils.isAlphaDigit(codepoint.getValue());
    }

    public static boolean isAlphaDigit(int codepoint) {
        return CharUtils.isDigit(codepoint) || CharUtils.isAlpha(codepoint);
    }

    public static boolean isHex(int codepoint) {
        return CharUtils.isDigit(codepoint) || CharUtils.inRange(codepoint, 97, 102) || CharUtils.inRange(codepoint, 65, 70);
    }

    public static boolean isBidi(Codepoint codepoint) {
        return CharUtils.isBidi(codepoint.getValue());
    }

    public static boolean isBidi(int codepoint) {
        return codepoint == 8206 || codepoint == 8207 || codepoint == 8234 || codepoint == 8235 || codepoint == 8237 || codepoint == 8238 || codepoint == 8236;
    }

    public static int get_index(int[] set, int value) {
        int s = 0;
        int e = set.length;
        while (e - s > 8) {
            int i = e + s >> 1;
            s = set[i] <= value ? i : s;
            e = set[i] > value ? i : e;
        }
        while (s < e && value >= set[s]) {
            ++s;
        }
        return s == e ? -1 : s - 1;
    }

    public static boolean invset_contains(int[] set, int value) {
        int s = 0;
        int e = set.length;
        while (e - s > 8) {
            int i = e + s >> 1;
            s = set[i] <= value ? i : s;
            e = set[i] > value ? i : e;
        }
        while (s < e && value >= set[s]) {
            ++s;
        }
        return (s - 1 & 1) == 0;
    }

    public static boolean isPctEnc(int codepoint) {
        return codepoint == 37 || CharUtils.isDigit(codepoint) || CharUtils.inRange(codepoint, 65, 70) || CharUtils.inRange(codepoint, 97, 102);
    }

    public static boolean isMark(int codepoint) {
        return codepoint == 45 || codepoint == 95 || codepoint == 46 || codepoint == 33 || codepoint == 126 || codepoint == 42 || codepoint == 92 || codepoint == 39 || codepoint == 40 || codepoint == 41;
    }

    public static boolean isUnreserved(int codepoint) {
        return CharUtils.isAlphaDigit(codepoint) || codepoint == 45 || codepoint == 46 || codepoint == 95 || codepoint == 126;
    }

    public static boolean isReserved(int codepoint) {
        return codepoint == 36 || codepoint == 38 || codepoint == 43 || codepoint == 44 || codepoint == 47 || codepoint == 58 || codepoint == 59 || codepoint == 61 || codepoint == 63 || codepoint == 64 || codepoint == 91 || codepoint == 93;
    }

    public static boolean isGenDelim(int codepoint) {
        return codepoint == 35 || codepoint == 47 || codepoint == 58 || codepoint == 63 || codepoint == 64 || codepoint == 91 || codepoint == 93;
    }

    public static boolean isSubDelim(int codepoint) {
        return codepoint == 33 || codepoint == 36 || codepoint == 38 || codepoint == 39 || codepoint == 40 || codepoint == 41 || codepoint == 42 || codepoint == 43 || codepoint == 44 || codepoint == 59 || codepoint == 61 || codepoint == 92;
    }

    public static boolean isPchar(int codepoint) {
        return CharUtils.isUnreserved(codepoint) || codepoint == 58 || codepoint == 64 || codepoint == 38 || codepoint == 61 || codepoint == 43 || codepoint == 36 || codepoint == 44;
    }

    public static boolean isPath(int codepoint) {
        return CharUtils.isPchar(codepoint) || codepoint == 59 || codepoint == 47 || codepoint == 37 || codepoint == 44;
    }

    public static boolean isPathNoDelims(int codepoint) {
        return CharUtils.isPath(codepoint) && !CharUtils.isGenDelim(codepoint);
    }

    public static boolean isScheme(int codepoint) {
        return CharUtils.isAlphaDigit(codepoint) || codepoint == 43 || codepoint == 45 || codepoint == 46;
    }

    public static boolean isUserInfo(int codepoint) {
        return CharUtils.isUnreserved(codepoint) || CharUtils.isSubDelim(codepoint) || CharUtils.isPctEnc(codepoint);
    }

    public static boolean isQuery(int codepoint) {
        return CharUtils.isPchar(codepoint) || codepoint == 59 || codepoint == 47 || codepoint == 63 || codepoint == 37;
    }

    public static boolean isFragment(int codepoint) {
        return CharUtils.isPchar(codepoint) || codepoint == 47 || codepoint == 63 || codepoint == 37;
    }

    public static boolean is_ucschar(int codepoint) {
        return CharUtils.inRange(codepoint, 160, 55295) || CharUtils.inRange(codepoint, 63744, 64975) || CharUtils.inRange(codepoint, 65008, 65519) || CharUtils.inRange(codepoint, 65536, 131069) || CharUtils.inRange(codepoint, 131072, 196605) || CharUtils.inRange(codepoint, 196608, 262141) || CharUtils.inRange(codepoint, 262144, 327677) || CharUtils.inRange(codepoint, 327680, 393213) || CharUtils.inRange(codepoint, 393216, 458749) || CharUtils.inRange(codepoint, 458752, 524285) || CharUtils.inRange(codepoint, 524288, 589821) || CharUtils.inRange(codepoint, 589824, 655357) || CharUtils.inRange(codepoint, 655360, 720893) || CharUtils.inRange(codepoint, 720896, 786429) || CharUtils.inRange(codepoint, 786432, 851965) || CharUtils.inRange(codepoint, 851968, 917501) || CharUtils.inRange(codepoint, 921600, 983037);
    }

    public static boolean is_iprivate(int codepoint) {
        return CharUtils.inRange(codepoint, 57344, 63743) || CharUtils.inRange(codepoint, 983040, 1048573) || CharUtils.inRange(codepoint, 0x100000, 1114109);
    }

    public static boolean is_iunreserved(int codepoint) {
        return CharUtils.isAlphaDigit(codepoint) || CharUtils.isMark(codepoint) || CharUtils.is_ucschar(codepoint);
    }

    public static boolean is_ipchar(int codepoint) {
        return CharUtils.is_iunreserved(codepoint) || CharUtils.isSubDelim(codepoint) || codepoint == 58 || codepoint == 64 || codepoint == 38 || codepoint == 61 || codepoint == 43 || codepoint == 36;
    }

    public static boolean is_ipath(int codepoint) {
        return CharUtils.is_ipchar(codepoint) || codepoint == 59 || codepoint == 47 || codepoint == 37 || codepoint == 44;
    }

    public static boolean is_ipathnodelims(int codepoint) {
        return CharUtils.is_ipath(codepoint) && !CharUtils.isGenDelim(codepoint);
    }

    public static boolean is_iquery(int codepoint) {
        return CharUtils.is_ipchar(codepoint) || CharUtils.is_iprivate(codepoint) || codepoint == 59 || codepoint == 47 || codepoint == 63 || codepoint == 37;
    }

    public static boolean is_ifragment(int codepoint) {
        return CharUtils.is_ipchar(codepoint) || CharUtils.is_iprivate(codepoint) || codepoint == 47 || codepoint == 63 || codepoint == 37;
    }

    public static boolean is_iregname(int codepoint) {
        return CharUtils.is_iunreserved(codepoint) || codepoint == 33 || codepoint == 36 || codepoint == 38 || codepoint == 39 || codepoint == 40 || codepoint == 41 || codepoint == 42 || codepoint == 43 || codepoint == 44 || codepoint == 59 || codepoint == 61 || codepoint == 34;
    }

    public static boolean is_ipliteral(int codepoint) {
        return CharUtils.isHex(codepoint) || codepoint == 58 || codepoint == 91 || codepoint == 93;
    }

    public static boolean is_ihost(int codepoint) {
        return CharUtils.is_iregname(codepoint) || CharUtils.is_ipliteral(codepoint);
    }

    public static boolean is_regname(int codepoint) {
        return CharUtils.isUnreserved(codepoint) || codepoint == 33 || codepoint == 36 || codepoint == 38 || codepoint == 39 || codepoint == 40 || codepoint == 41 || codepoint == 42 || codepoint == 43 || codepoint == 44 || codepoint == 59 || codepoint == 61 || codepoint == 34;
    }

    public static boolean is_iuserinfo(int codepoint) {
        return CharUtils.is_iunreserved(codepoint) || codepoint == 59 || codepoint == 58 || codepoint == 38 || codepoint == 61 || codepoint == 43 || codepoint == 36 || codepoint == 44;
    }

    public static boolean is_iserver(int codepoint) {
        return CharUtils.is_iuserinfo(codepoint) || CharUtils.is_iregname(codepoint) || CharUtils.isAlphaDigit(codepoint) || codepoint == 46 || codepoint == 58 || codepoint == 64 || codepoint == 91 || codepoint == 93 || codepoint == 37 || codepoint == 45;
    }

    public static void verify(CodepointIterator ci, Filter filter) throws InvalidCharacterException {
        CodepointIterator rci = CodepointIterator.restrict(ci, filter);
        while (rci.hasNext()) {
            rci.next();
        }
    }

    public static void verify(CodepointIterator ci, Profile profile) throws InvalidCharacterException {
        CodepointIterator rci = CodepointIterator.restrict(ci, profile.filter());
        while (rci.hasNext()) {
            rci.next();
        }
    }

    public static void verify(char[] s, Profile profile) throws InvalidCharacterException {
        if (s == null) {
            return;
        }
        CharUtils.verify(CodepointIterator.forCharArray(s), profile);
    }

    public static void verify(String s, Profile profile) throws InvalidCharacterException {
        if (s == null) {
            return;
        }
        CharUtils.verify(CodepointIterator.forCharSequence(s), profile);
    }

    public static void verifyNot(CodepointIterator ci, Filter filter) throws InvalidCharacterException {
        CodepointIterator rci = ci.restrict(filter, false, true);
        while (rci.hasNext()) {
            rci.next();
        }
    }

    public static void verifyNot(CodepointIterator ci, Profile profile) throws InvalidCharacterException {
        CodepointIterator rci = ci.restrict(profile.filter(), false, true);
        while (rci.hasNext()) {
            rci.next();
        }
    }

    public static void verifyNot(char[] array, Profile profile) throws InvalidCharacterException {
        CodepointIterator rci = CodepointIterator.forCharArray(array).restrict(profile.filter(), false, true);
        while (rci.hasNext()) {
            rci.next();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum Profile {
        NONE(new Filter(){

            public boolean accept(int codepoint) {
                return true;
            }
        }),
        ALPHA(new Filter(){

            public boolean accept(int codepoint) {
                return !CharUtils.isAlpha(codepoint);
            }
        }),
        ALPHANUM(new Filter(){

            public boolean accept(int codepoint) {
                return !CharUtils.isAlphaDigit(codepoint);
            }
        }),
        FRAGMENT(new Filter(){

            public boolean accept(int codepoint) {
                return !CharUtils.isFragment(codepoint);
            }
        }),
        IFRAGMENT(new Filter(){

            public boolean accept(int codepoint) {
                return !CharUtils.is_ifragment(codepoint);
            }
        }),
        PATH(new Filter(){

            public boolean accept(int codepoint) {
                return !CharUtils.isPath(codepoint);
            }
        }),
        IPATH(new Filter(){

            public boolean accept(int codepoint) {
                return !CharUtils.is_ipath(codepoint);
            }
        }),
        IUSERINFO(new Filter(){

            public boolean accept(int codepoint) {
                return !CharUtils.is_iuserinfo(codepoint);
            }
        }),
        USERINFO(new Filter(){

            public boolean accept(int codepoint) {
                return !CharUtils.isUserInfo(codepoint);
            }
        }),
        QUERY(new Filter(){

            public boolean accept(int codepoint) {
                return !CharUtils.isQuery(codepoint);
            }
        }),
        IQUERY(new Filter(){

            public boolean accept(int codepoint) {
                return !CharUtils.is_iquery(codepoint);
            }
        }),
        SCHEME(new Filter(){

            public boolean accept(int codepoint) {
                return !CharUtils.isScheme(codepoint);
            }
        }),
        PATHNODELIMS(new Filter(){

            public boolean accept(int codepoint) {
                return !CharUtils.isPathNoDelims(codepoint);
            }
        }),
        IPATHNODELIMS(new Filter(){

            public boolean accept(int codepoint) {
                return !CharUtils.is_ipathnodelims(codepoint);
            }
        }),
        IPATHNODELIMS_SEG(new Filter(){

            public boolean accept(int codepoint) {
                return !CharUtils.is_ipathnodelims(codepoint) && codepoint != 64 && codepoint != 58;
            }
        }),
        IREGNAME(new Filter(){

            public boolean accept(int codepoint) {
                return !CharUtils.is_iregname(codepoint);
            }
        }),
        IHOST(new Filter(){

            public boolean accept(int codepoint) {
                return !CharUtils.is_ihost(codepoint);
            }
        }),
        IPRIVATE(new Filter(){

            public boolean accept(int codepoint) {
                return !CharUtils.is_iprivate(codepoint);
            }
        }),
        RESERVED(new Filter(){

            public boolean accept(int codepoint) {
                return !CharUtils.isReserved(codepoint);
            }
        }),
        IUNRESERVED(new Filter(){

            public boolean accept(int codepoint) {
                return !CharUtils.is_iunreserved(codepoint);
            }
        }),
        UNRESERVED(new Filter(){

            public boolean accept(int codepoint) {
                return !CharUtils.isUnreserved(codepoint);
            }
        }),
        SCHEMESPECIFICPART(new Filter(){

            public boolean accept(int codepoint) {
                return !CharUtils.is_iunreserved(codepoint) && !CharUtils.isReserved(codepoint) && !CharUtils.is_iprivate(codepoint) && !CharUtils.isPctEnc(codepoint) && codepoint != 35;
            }
        }),
        AUTHORITY(new Filter(){

            public boolean accept(int codepoint) {
                return !CharUtils.is_regname(codepoint) && !CharUtils.isUserInfo(codepoint) && !CharUtils.isGenDelim(codepoint);
            }
        }),
        ASCIISANSCRLF(new Filter(){

            public boolean accept(int codepoint) {
                return !CharUtils.inRange(codepoint, 1, 9) && !CharUtils.inRange(codepoint, 14, 127);
            }
        }),
        PCT(new Filter(){

            public boolean accept(int codepoint) {
                return !CharUtils.isPctEnc(codepoint);
            }
        }),
        STD3ASCIIRULES(new Filter(){

            public boolean accept(int codepoint) {
                return !CharUtils.inRange(codepoint, 0, 44) && !CharUtils.inRange(codepoint, 46, 47) && !CharUtils.inRange(codepoint, 58, 64) && !CharUtils.inRange(codepoint, 91, 94) && !CharUtils.inRange(codepoint, 96, 96) && !CharUtils.inRange(codepoint, 123, 127);
            }
        });

        private final Filter filter;

        private Profile(Filter filter) {
            this.filter = filter;
        }

        public Filter filter() {
            return this.filter;
        }

        public boolean check(int codepoint) {
            return this.filter.accept(codepoint);
        }
    }
}

