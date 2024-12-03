/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.atlassian.activeobjects.internal;

import com.atlassian.activeobjects.ao.ConverterUtils;
import com.atlassian.activeobjects.internal.Prefix;
import com.google.common.base.Preconditions;

public final class SimplePrefix
implements Prefix {
    private static final String DEFAULT_SEPARATOR = "_";
    private final String prefix;
    private final String separator;

    public SimplePrefix(String prefix) {
        this(prefix, DEFAULT_SEPARATOR);
    }

    public SimplePrefix(String prefix, String separator) {
        this.prefix = (String)Preconditions.checkNotNull((Object)prefix);
        this.separator = (String)Preconditions.checkNotNull((Object)separator);
    }

    @Override
    public String prepend(String string) {
        return this.prefix + this.separator + string;
    }

    @Override
    public boolean isStarting(String string, boolean caseSensitive) {
        return string != null && this.transform(string, caseSensitive).startsWith(this.transform(this.prefix + this.separator, caseSensitive));
    }

    private String transform(String s, boolean caseSensitive) {
        return caseSensitive ? s : ConverterUtils.toLowerCase(s);
    }
}

