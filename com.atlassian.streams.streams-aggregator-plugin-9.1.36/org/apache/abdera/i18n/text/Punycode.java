/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.i18n.text;

import java.io.IOException;
import org.apache.abdera.i18n.text.CharUtils;
import org.apache.abdera.i18n.text.CodepointIterator;

public final class Punycode {
    static final int base = 36;
    static final int tmin = 1;
    static final int tmax = 26;
    static final int skew = 38;
    static final int damp = 700;
    static final int initial_bias = 72;
    static final int initial_n = 128;
    static final int delimiter = 45;

    Punycode() {
    }

    private static boolean basic(int cp) {
        return cp < 128;
    }

    private static boolean delim(int cp) {
        return cp == 45;
    }

    private static boolean flagged(int bcp) {
        return bcp - 65 < 26;
    }

    private static int decode_digit(int cp) {
        return cp - 48 < 10 ? cp - 22 : (cp - 65 < 26 ? cp - 65 : (cp - 97 < 26 ? cp - 97 : 36));
    }

    private static int t(boolean c) {
        return c ? 1 : 0;
    }

    private static int encode_digit(int d, boolean upper) {
        return d + 22 + 75 * Punycode.t(d < 26) - (Punycode.t(upper) << 5);
    }

    private static int adapt(int delta, int numpoints, boolean firsttime) {
        delta = firsttime ? delta / 700 : delta >> 1;
        delta += delta / numpoints;
        int k = 0;
        while (delta > 455) {
            delta /= 35;
            k += 36;
        }
        return k + 36 * delta / (delta + 38);
    }

    public static String encode(char[] chars, boolean[] case_flags) throws IOException {
        int b;
        StringBuilder buf = new StringBuilder();
        CodepointIterator ci = CodepointIterator.forCharArray(chars);
        int n = 128;
        int delta = 0;
        int bias = 72;
        int i = -1;
        while (ci.hasNext()) {
            i = ci.next().getValue();
            if (!Punycode.basic(i) || case_flags != null) continue;
            buf.append((char)i);
        }
        int h = b = buf.length();
        if (b > 0) {
            buf.append('-');
        }
        while (h < chars.length) {
            ci.position(0);
            i = -1;
            int m = Integer.MAX_VALUE;
            while (ci.hasNext()) {
                i = ci.next().getValue();
                if (i < n || i >= m) continue;
                m = i;
            }
            if (m - n > (Integer.MAX_VALUE - delta) / (h + 1)) {
                throw new IOException("Overflow");
            }
            delta += (m - n) * (h + 1);
            n = m;
            ci.position(0);
            i = -1;
            while (ci.hasNext()) {
                i = ci.next().getValue();
                if (i < n && ++delta == 0) {
                    throw new IOException("Overflow");
                }
                if (i != n) continue;
                int q = delta;
                int k = 36;
                while (true) {
                    int t;
                    int n2 = k <= bias ? 1 : (t = k >= bias + 26 ? 26 : k - bias);
                    if (q < t) break;
                    buf.append((char)Punycode.encode_digit(t + (q - t) % (36 - t), false));
                    q = (q - t) / (36 - t);
                    k += 36;
                }
                buf.append((char)Punycode.encode_digit(q, case_flags != null ? case_flags[ci.position() - 1] : false));
                bias = Punycode.adapt(delta, h + 1, h == b);
                delta = 0;
                ++h;
            }
            ++delta;
            ++n;
        }
        return buf.toString();
    }

    public static String encode(String s) {
        try {
            if (s == null) {
                return null;
            }
            return Punycode.encode(s.toCharArray(), null).toString();
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String decode(String s) {
        try {
            if (s == null) {
                return null;
            }
            return Punycode.decode(s.toCharArray(), null).toString();
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String decode(char[] chars, boolean[] case_flags) throws IOException {
        int in;
        int j;
        StringBuilder buf = new StringBuilder();
        int n = 128;
        int i = 0;
        int out = 0;
        int bias = 72;
        int b = 0;
        for (j = 0; j < chars.length; ++j) {
            if (!Punycode.delim(chars[j])) continue;
            b = j;
        }
        for (j = 0; j < b; ++j) {
            if (case_flags != null) {
                case_flags[out] = Punycode.flagged(chars[j]);
            }
            if (!Punycode.basic(chars[j])) {
                throw new IOException("Bad Input");
            }
            buf.append(chars[j]);
        }
        out = buf.length();
        int n2 = in = b > 0 ? b + 1 : 0;
        while (in < chars.length) {
            int oldi = i;
            int w = 1;
            int k = 36;
            while (true) {
                int t;
                int digit;
                if (in > chars.length) {
                    throw new IOException("Bad input");
                }
                if ((digit = Punycode.decode_digit(chars[in++])) >= 36) {
                    throw new IOException("Bad input");
                }
                if (digit > (Integer.MAX_VALUE - i) / w) {
                    throw new IOException("Overflow");
                }
                i += digit * w;
                int n3 = k <= bias ? 1 : (t = k >= bias + 26 ? 26 : k - bias);
                if (digit < t) break;
                if (w > Integer.MAX_VALUE / (36 - t)) {
                    throw new IOException("Overflow");
                }
                w *= 36 - t;
                k += 36;
            }
            bias = Punycode.adapt(i - oldi, out + 1, oldi == 0);
            if (i / (out + 1) > Integer.MAX_VALUE - n) {
                throw new IOException("Overflow");
            }
            n += i / (out + 1);
            i %= out + 1;
            if (case_flags != null) {
                System.arraycopy(case_flags, i, case_flags, i + CharUtils.length(n), case_flags.length - i);
            }
            CharUtils.insert((CharSequence)buf, i++, n);
            ++out;
        }
        return buf.toString();
    }
}

