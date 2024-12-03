/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 */
package com.atlassian.confluence.search.v2.analysis;

import com.atlassian.confluence.plugins.index.api.TokenFilterDescriptor;
import com.google.common.collect.ImmutableList;
import java.util.Collection;

public final class CommonGramsTokenFilterDescriptor
implements TokenFilterDescriptor {
    private final Collection<String> commonWords;
    private final boolean queryMode;

    public CommonGramsTokenFilterDescriptor(Collection<String> commonWords, boolean queryMode) {
        this.commonWords = ImmutableList.copyOf(commonWords);
        this.queryMode = queryMode;
    }

    public Collection<String> getCommonWords() {
        return this.commonWords;
    }

    public boolean isQueryMode() {
        return this.queryMode;
    }
}

