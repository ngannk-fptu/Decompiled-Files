/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Multimap
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.integration.jira;

import com.google.common.collect.Multimap;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface JiraKeyScanner {
    @Nonnull
    public Iterable<String> findAll(@Nonnull CharSequence var1);

    @Nonnull
    public Multimap<String, MatchResult> findMatches(@Nonnull CharSequence var1);

    @Nonnull
    public Multimap<String, MatchResult> findMatches(@Nonnull CharSequence var1, @Nullable Pattern var2);
}

