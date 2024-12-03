/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package org.apache.commons.text.lookup;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.lookup.StringLookup;

abstract class AbstractStringLookup
implements StringLookup {
    protected static final char SPLIT_CH = ':';
    protected static final String SPLIT_STR = String.valueOf(':');

    AbstractStringLookup() {
    }

    static String toLookupKey(String left, String right) {
        return AbstractStringLookup.toLookupKey(left, SPLIT_STR, right);
    }

    static String toLookupKey(String left, String separator, String right) {
        return left + separator + right;
    }

    @Deprecated
    protected String substringAfter(String value, char ch) {
        return StringUtils.substringAfter((String)value, (int)ch);
    }

    @Deprecated
    protected String substringAfter(String value, String str) {
        return StringUtils.substringAfter((String)value, (String)str);
    }

    @Deprecated
    protected String substringAfterLast(String value, char ch) {
        return StringUtils.substringAfterLast((String)value, (int)ch);
    }
}

