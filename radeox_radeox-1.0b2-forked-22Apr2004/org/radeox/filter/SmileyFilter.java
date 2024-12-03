/*
 * Decompiled with CFR 0.152.
 */
package org.radeox.filter;

import org.radeox.filter.CacheFilter;
import org.radeox.filter.regex.RegexReplaceFilter;

public class SmileyFilter
extends RegexReplaceFilter
implements CacheFilter {
    public SmileyFilter() {
        super(":-\\(", ":-)");
    }
}

