/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.text;

import java.util.Formattable;
import java.util.Formatter;

public class FormattableUtils {
    private static final String SIMPLEST_FORMAT = "%s";

    public static Formatter append(CharSequence seq, Formatter formatter, int flags, int width, int precision) {
        return FormattableUtils.append(seq, formatter, flags, width, precision, ' ', null);
    }

    public static Formatter append(CharSequence seq, Formatter formatter, int flags, int width, int precision, char padChar) {
        return FormattableUtils.append(seq, formatter, flags, width, precision, padChar, null);
    }

    public static Formatter append(CharSequence seq, Formatter formatter, int flags, int width, int precision, char padChar, CharSequence truncateEllipsis) {
        if (truncateEllipsis != null && precision >= 0 && truncateEllipsis.length() > precision) {
            throw new IllegalArgumentException(String.format("Specified ellipsis '%s' exceeds precision of %s", truncateEllipsis, precision));
        }
        StringBuilder buf = new StringBuilder(seq);
        if (precision >= 0 && precision < seq.length()) {
            CharSequence ellipsis = truncateEllipsis == null ? "" : truncateEllipsis;
            buf.replace(precision - ellipsis.length(), seq.length(), ellipsis.toString());
        }
        boolean leftJustify = (flags & 1) == 1;
        for (int i = buf.length(); i < width; ++i) {
            buf.insert(leftJustify ? i : 0, padChar);
        }
        formatter.format(buf.toString(), new Object[0]);
        return formatter;
    }

    public static Formatter append(CharSequence seq, Formatter formatter, int flags, int width, int precision, CharSequence ellipsis) {
        return FormattableUtils.append(seq, formatter, flags, width, precision, ' ', ellipsis);
    }

    public static String toString(Formattable formattable) {
        return String.format(SIMPLEST_FORMAT, formattable);
    }
}

