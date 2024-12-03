/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansions
 *  com.atlassian.confluence.api.service.finder.ManyFetcher
 *  javax.annotation.Nonnull
 */
package com.atlassian.confluence.plugins.mobile.service;

import com.atlassian.confluence.api.model.Expansions;
import com.atlassian.confluence.api.service.finder.ManyFetcher;
import com.atlassian.confluence.plugins.mobile.model.card.Card;
import javax.annotation.Nonnull;

public interface CardService {
    @Nonnull
    public CardFinder find();

    public static interface CardFinder
    extends ManyFetcher<Card> {
        public CardFinder spaceKey(String var1);

        public CardFinder expand(Expansions var1);
    }
}

