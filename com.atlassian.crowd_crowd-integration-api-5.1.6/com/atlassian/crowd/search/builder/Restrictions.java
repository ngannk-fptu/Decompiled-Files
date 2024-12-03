/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.SearchRestriction
 */
package com.atlassian.crowd.search.builder;

import com.atlassian.crowd.embedded.api.SearchRestriction;
import com.atlassian.crowd.search.builder.Combine;
import com.atlassian.crowd.search.builder.Restriction;
import com.atlassian.crowd.search.query.entity.restriction.BooleanRestriction;
import com.atlassian.crowd.search.query.entity.restriction.NullRestriction;
import com.atlassian.crowd.search.query.entity.restriction.constants.UserTermKeys;
import java.util.function.Function;

public class Restrictions {
    public static BooleanRestriction userSearchRestriction(String searchText) {
        return Combine.anyOf(Restriction.on(UserTermKeys.USERNAME).containing(searchText), Restriction.on(UserTermKeys.DISPLAY_NAME).containing(searchText), Restriction.on(UserTermKeys.FIRST_NAME).containing(searchText), Restriction.on(UserTermKeys.LAST_NAME).containing(searchText), Restriction.on(UserTermKeys.EMAIL).containing(searchText));
    }

    public static <T> SearchRestriction optional(Function<T, SearchRestriction> provider, T value) {
        return value == null ? NullRestriction.INSTANCE : provider.apply(value);
    }
}

