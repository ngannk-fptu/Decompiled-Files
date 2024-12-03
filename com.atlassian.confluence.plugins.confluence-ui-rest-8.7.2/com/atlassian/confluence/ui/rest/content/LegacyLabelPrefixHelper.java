/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.legacyapi.model.content.Label$Prefix
 *  com.google.common.collect.Iterables
 */
package com.atlassian.confluence.ui.rest.content;

import com.atlassian.confluence.legacyapi.model.content.Label;
import com.google.common.collect.Iterables;
import java.util.Arrays;
import java.util.List;

@Deprecated
public class LegacyLabelPrefixHelper {
    public static Iterable<Label.Prefix> convertLabelPrefixStrings(List<String> prefixes) {
        Iterable<Object> requestPrefixes = prefixes == null || prefixes.isEmpty() ? Arrays.asList(Label.Prefix.values()) : Iterables.transform(prefixes, Label.Prefix::valueOf);
        return requestPrefixes;
    }
}

