/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.SearchRestriction
 *  com.google.common.collect.ImmutableList
 *  org.apache.commons.lang3.Validate
 *  org.apache.commons.lang3.builder.ToStringBuilder
 */
package com.atlassian.crowd.search.query.entity.restriction;

import com.atlassian.crowd.embedded.api.SearchRestriction;
import com.atlassian.crowd.search.query.entity.restriction.BooleanRestriction;
import com.google.common.collect.ImmutableList;
import java.util.Arrays;
import java.util.Collection;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class BooleanRestrictionImpl
implements BooleanRestriction {
    private final ImmutableList<SearchRestriction> restrictions;
    private final BooleanRestriction.BooleanLogic booleanLogic;

    public BooleanRestrictionImpl(BooleanRestriction.BooleanLogic booleanLogic, SearchRestriction ... restrictions) {
        this(booleanLogic, Arrays.asList(restrictions));
    }

    public BooleanRestrictionImpl(BooleanRestriction.BooleanLogic booleanLogic, Collection<? extends SearchRestriction> restrictions) {
        Validate.notNull((Object)((Object)booleanLogic), (String)"booleanLogic cannot be null", (Object[])new Object[0]);
        Validate.notEmpty(restrictions, (String)"restrictions cannot be empty", (Object[])new Object[0]);
        this.restrictions = ImmutableList.copyOf(restrictions);
        this.booleanLogic = booleanLogic;
    }

    @Override
    public final Collection<SearchRestriction> getRestrictions() {
        return this.restrictions;
    }

    @Override
    public final BooleanRestriction.BooleanLogic getBooleanLogic() {
        return this.booleanLogic;
    }

    public String toString() {
        return new ToStringBuilder((Object)this).append("restrictions", this.restrictions).append("booleanLogic", (Object)this.booleanLogic).toString();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BooleanRestriction)) {
            return false;
        }
        BooleanRestriction that = (BooleanRestriction)o;
        if (this.booleanLogic != that.getBooleanLogic()) {
            return false;
        }
        if (this.restrictions == null) {
            if (that.getRestrictions() != null) {
                return false;
            }
        } else {
            boolean sizeEqual;
            boolean bl = sizeEqual = this.restrictions.size() == that.getRestrictions().size();
            if (!sizeEqual || !this.restrictions.containsAll(that.getRestrictions())) {
                return false;
            }
        }
        return true;
    }

    public int hashCode() {
        int result = this.restrictions != null ? this.restrictions.hashCode() : 0;
        result = 31 * result + (this.booleanLogic != null ? this.booleanLogic.hashCode() : 0);
        return result;
    }
}

