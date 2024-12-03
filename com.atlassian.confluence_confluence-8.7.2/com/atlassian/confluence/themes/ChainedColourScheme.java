/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Predicate
 *  com.google.common.base.Predicates
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Iterables
 */
package com.atlassian.confluence.themes;

import com.atlassian.confluence.themes.AbstractColourScheme;
import com.atlassian.confluence.themes.ColourScheme;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import java.util.List;

public class ChainedColourScheme
extends AbstractColourScheme {
    ColourScheme[] chainedSchemes;

    public ChainedColourScheme(List<ColourScheme> colourSchemes) {
        ImmutableList notNullSchemes = ImmutableList.copyOf((Iterable)Iterables.filter(colourSchemes, (Predicate)Predicates.notNull()));
        this.chainedSchemes = new ColourScheme[notNullSchemes.size()];
        notNullSchemes.toArray(this.chainedSchemes);
    }

    @Override
    public String get(String colourName) {
        for (ColourScheme chainedScheme : this.chainedSchemes) {
            String colour = chainedScheme.get(colourName);
            if (colour == null) continue;
            return colour;
        }
        return null;
    }
}

