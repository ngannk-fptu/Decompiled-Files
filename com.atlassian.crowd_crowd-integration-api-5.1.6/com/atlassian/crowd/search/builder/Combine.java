/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.SearchRestriction
 */
package com.atlassian.crowd.search.builder;

import com.atlassian.crowd.embedded.api.SearchRestriction;
import com.atlassian.crowd.search.query.entity.restriction.BooleanRestriction;
import com.atlassian.crowd.search.query.entity.restriction.BooleanRestrictionImpl;
import com.atlassian.crowd.search.query.entity.restriction.NullRestriction;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Combine {
    public static BooleanRestriction anyOf(SearchRestriction ... restrictions) {
        return new BooleanRestrictionImpl(BooleanRestriction.BooleanLogic.OR, restrictions);
    }

    public static BooleanRestriction allOf(SearchRestriction ... restrictions) {
        return new BooleanRestrictionImpl(BooleanRestriction.BooleanLogic.AND, restrictions);
    }

    public static BooleanRestriction anyOf(Collection<? extends SearchRestriction> restrictions) {
        return new BooleanRestrictionImpl(BooleanRestriction.BooleanLogic.OR, restrictions);
    }

    public static BooleanRestriction allOf(Collection<? extends SearchRestriction> restrictions) {
        return new BooleanRestrictionImpl(BooleanRestriction.BooleanLogic.AND, restrictions);
    }

    public static SearchRestriction optionalAllOf(SearchRestriction ... optionalRestrictions) {
        List restrictions = Stream.of(optionalRestrictions).filter(restriction -> restriction != NullRestriction.INSTANCE).collect(Collectors.toList());
        return Combine.allOfIfNeeded(restrictions);
    }

    public static SearchRestriction allOfIfNeeded(Collection<? extends SearchRestriction> restrictions) {
        return Combine.combineIfNeeded(restrictions, BooleanRestriction.BooleanLogic.AND);
    }

    public static SearchRestriction anyOfIfNeeded(Collection<? extends SearchRestriction> restrictions) {
        return Combine.combineIfNeeded(restrictions, BooleanRestriction.BooleanLogic.OR);
    }

    private static SearchRestriction combineIfNeeded(Collection<? extends SearchRestriction> restrictions, BooleanRestriction.BooleanLogic booleanLogic) {
        if (restrictions.isEmpty()) {
            return NullRestriction.INSTANCE;
        }
        if (restrictions.size() == 1) {
            return restrictions.iterator().next();
        }
        return new BooleanRestrictionImpl(booleanLogic, restrictions);
    }
}

