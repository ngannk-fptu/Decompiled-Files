/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.utils.res;

import org.apache.xml.utils.res.CharArrayWrapper;
import org.apache.xml.utils.res.IntArrayWrapper;
import org.apache.xml.utils.res.LongArrayWrapper;
import org.apache.xml.utils.res.StringArrayWrapper;
import org.apache.xml.utils.res.XResourceBundle;

public class XResources_el
extends XResourceBundle {
    @Override
    public Object[][] getContents() {
        return new Object[][]{{"ui_language", "el"}, {"help_language", "el"}, {"language", "el"}, {"alphabet", new CharArrayWrapper(new char[]{'\u03b1', '\u03b2', '\u03b3', '\u03b4', '\u03b5', '\u03b6', '\u03b7', '\u03b8', '\u03b9', '\u03ba', '\u03bb', '\u03bc', '\u03bd', '\u03be', '\u03bf', '\u03c0', '\u03c1', '\u03c2', '\u03c3', '\u03c4', '\u03c5', '\u03c6', '\u03c7', '\u03c8', '\u03c9'})}, {"tradAlphabet", new CharArrayWrapper(new char[]{'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'})}, {"orientation", "LeftToRight"}, {"numbering", "multiplicative-additive"}, {"multiplierOrder", "precedes"}, {"numberGroups", new IntArrayWrapper(new int[]{100, 10, 1})}, {"multiplier", new LongArrayWrapper(new long[]{1000L})}, {"multiplierChar", new CharArrayWrapper(new char[]{'\u03d9'})}, {"zero", new CharArrayWrapper(new char[0])}, {"digits", new CharArrayWrapper(new char[]{'\u03b1', '\u03b2', '\u03b3', '\u03b4', '\u03b5', '\u03db', '\u03b6', '\u03b7', '\u03b8'})}, {"tens", new CharArrayWrapper(new char[]{'\u03b9', '\u03ba', '\u03bb', '\u03bc', '\u03bd', '\u03be', '\u03bf', '\u03c0', '\u03df'})}, {"hundreds", new CharArrayWrapper(new char[]{'\u03c1', '\u03c2', '\u03c4', '\u03c5', '\u03c6', '\u03c7', '\u03c8', '\u03c9', '\u03e1'})}, {"tables", new StringArrayWrapper(new String[]{"hundreds", "tens", "digits"})}};
    }
}

