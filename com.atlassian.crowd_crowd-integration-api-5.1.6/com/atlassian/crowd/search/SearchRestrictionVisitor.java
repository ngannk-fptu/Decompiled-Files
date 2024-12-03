/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.SearchRestriction
 */
package com.atlassian.crowd.search;

import com.atlassian.crowd.embedded.api.SearchRestriction;
import com.atlassian.crowd.search.query.entity.restriction.BooleanRestriction;
import com.atlassian.crowd.search.query.entity.restriction.BooleanRestrictionImpl;
import com.atlassian.crowd.search.query.entity.restriction.PropertyRestriction;
import java.util.List;
import java.util.stream.Collectors;

public abstract class SearchRestrictionVisitor {
    public SearchRestriction visit(SearchRestriction restriction) {
        if (restriction instanceof PropertyRestriction) {
            return this.visitPropertyRestriction((PropertyRestriction)restriction);
        }
        if (restriction instanceof BooleanRestriction) {
            BooleanRestriction boolRestriction = (BooleanRestriction)restriction;
            List restrictions = boolRestriction.getRestrictions().stream().map(this::visit).collect(Collectors.toList());
            return new BooleanRestrictionImpl(boolRestriction.getBooleanLogic(), restrictions);
        }
        return restriction;
    }

    protected abstract SearchRestriction visitPropertyRestriction(PropertyRestriction var1);
}

