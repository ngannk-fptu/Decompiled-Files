/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.BaseApiEnum
 *  com.google.common.collect.ImmutableList
 */
package com.atlassian.confluence.plugins.mobile.model.card;

import com.atlassian.confluence.api.model.BaseApiEnum;
import com.google.common.collect.ImmutableList;
import java.util.List;

public class CardType
extends BaseApiEnum {
    public static final CardType ALL_UPDATE_CARD = new CardType("AllUpdateCard");
    public static final CardType RECENTLY_VIEWED_CARD = new CardType("RecentlyViewedCard");
    public static final CardType IMPORTANT_CARD = new CardType("ImportantCard");
    public static final List<CardType> BUILT_IN = ImmutableList.of((Object)((Object)ALL_UPDATE_CARD), (Object)((Object)IMPORTANT_CARD), (Object)((Object)RECENTLY_VIEWED_CARD));

    public CardType(String value) {
        super(value);
    }
}

