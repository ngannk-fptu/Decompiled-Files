/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.layout;

import java.util.Iterator;
import java.util.List;
import org.xhtmlrenderer.css.constants.IdentValue;

public class CounterFunction {
    private IdentValue _listStyleType;
    private int _counterValue;
    private List _counterValues;
    private String _separator;

    public CounterFunction(int counterValue, IdentValue listStyleType) {
        this._counterValue = counterValue;
        this._listStyleType = listStyleType;
    }

    public CounterFunction(List counterValues, String separator, IdentValue listStyleType) {
        this._counterValues = counterValues;
        this._separator = separator;
        this._listStyleType = listStyleType;
    }

    public String evaluate() {
        if (this._counterValues == null) {
            return CounterFunction.createCounterText(this._listStyleType, this._counterValue);
        }
        StringBuffer sb = new StringBuffer();
        Iterator i = this._counterValues.iterator();
        while (i.hasNext()) {
            Integer value = (Integer)i.next();
            sb.append(CounterFunction.createCounterText(this._listStyleType, value));
            if (!i.hasNext()) continue;
            sb.append(this._separator);
        }
        return sb.toString();
    }

    public static String createCounterText(IdentValue listStyle, int listCounter) {
        String text = listStyle == IdentValue.LOWER_LATIN || listStyle == IdentValue.LOWER_ALPHA ? CounterFunction.toLatin(listCounter).toLowerCase() : (listStyle == IdentValue.UPPER_LATIN || listStyle == IdentValue.UPPER_ALPHA ? CounterFunction.toLatin(listCounter).toUpperCase() : (listStyle == IdentValue.LOWER_ROMAN ? CounterFunction.toRoman(listCounter).toLowerCase() : (listStyle == IdentValue.UPPER_ROMAN ? CounterFunction.toRoman(listCounter).toUpperCase() : (listStyle == IdentValue.DECIMAL_LEADING_ZERO ? (listCounter >= 10 ? "" : "0") + listCounter : Integer.toString(listCounter)))));
        return text;
    }

    private static String toLatin(int val) {
        String result = "";
        --val;
        while (val >= 0) {
            int letter = val % 26;
            val = val / 26 - 1;
            result = (char)(letter + 65) + result;
        }
        return result;
    }

    private static String toRoman(int val) {
        int[] ints = new int[]{1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};
        String[] nums = new String[]{"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < ints.length; ++i) {
            int count = val / ints[i];
            for (int j = 0; j < count; ++j) {
                sb.append(nums[i]);
            }
            val -= ints[i] * count;
        }
        return sb.toString();
    }
}

