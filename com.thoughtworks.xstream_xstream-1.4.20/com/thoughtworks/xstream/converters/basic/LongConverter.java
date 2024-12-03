/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.converters.basic;

import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;

public class LongConverter
extends AbstractSingleValueConverter {
    public boolean canConvert(Class type) {
        return type == Long.TYPE || type == Long.class;
    }

    public Object fromString(String str) {
        long low;
        long high;
        int len = str.length();
        if (len == 0) {
            throw new NumberFormatException("For input string: \"\"");
        }
        if (len < 17) {
            return Long.decode(str);
        }
        char c0 = str.charAt(0);
        if (c0 != '0' && c0 != '#') {
            return Long.decode(str);
        }
        char c1 = str.charAt(1);
        if (c0 == '#' && len == 17) {
            high = Long.parseLong(str.substring(1, 9), 16) << 32;
            low = Long.parseLong(str.substring(9, 17), 16);
        } else if ((c1 == 'x' || c1 == 'X') && len == 18) {
            high = Long.parseLong(str.substring(2, 10), 16) << 32;
            low = Long.parseLong(str.substring(10, 18), 16);
        } else if (len == 23 && c1 == '1') {
            high = Long.parseLong(str.substring(1, 12), 8) << 33;
            low = Long.parseLong(str.substring(12, 23), 8);
        } else {
            return Long.decode(str);
        }
        long num = high | low;
        return new Long(num);
    }
}

