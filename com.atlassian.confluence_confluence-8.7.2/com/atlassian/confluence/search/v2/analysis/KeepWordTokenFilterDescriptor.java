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

public final class KeepWordTokenFilterDescriptor
implements TokenFilterDescriptor {
    private final Collection<String> dictionary;

    public KeepWordTokenFilterDescriptor(Collection<String> dictionary) {
        this.dictionary = ImmutableList.copyOf(dictionary);
    }

    public Collection<String> getDictionary() {
        return this.dictionary;
    }
}

