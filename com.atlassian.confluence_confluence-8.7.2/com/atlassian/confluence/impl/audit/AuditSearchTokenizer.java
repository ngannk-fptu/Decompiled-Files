/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Joiner
 *  com.google.common.base.Splitter
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.ImmutableSet$Builder
 */
package com.atlassian.confluence.impl.audit;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableSet;
import java.util.Locale;

@Deprecated
class AuditSearchTokenizer {
    final ImmutableSet.Builder<String> tokens = ImmutableSet.builder();

    AuditSearchTokenizer() {
    }

    AuditSearchTokenizer put(String stringToTokenize) {
        if (stringToTokenize != null) {
            Splitter.on((String)" ").omitEmptyStrings().split((CharSequence)stringToTokenize).forEach(input -> this.tokens.add((Object)input.toLowerCase(Locale.ENGLISH)));
        }
        return this;
    }

    String getTokenizedString() {
        return Joiner.on((String)" ").join(this.getTokens());
    }

    ImmutableSet<String> getTokens() {
        return this.tokens.build();
    }
}

