/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v1.db.sql.schemarep;

import com.mchange.v1.db.sql.schemarep.UniquenessConstraintRep;
import com.mchange.v1.util.SetUtils;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class UniquenessConstraintRepImpl
implements UniquenessConstraintRep {
    Set uniqueColNames;

    public UniquenessConstraintRepImpl(Collection collection) {
        this.uniqueColNames = Collections.unmodifiableSet(new HashSet(collection));
    }

    @Override
    public Set getUniqueColumnNames() {
        return this.uniqueColNames;
    }

    public boolean equals(Object object) {
        return object != null && this.getClass() == object.getClass() && SetUtils.equivalentDisregardingSort(this.uniqueColNames, ((UniquenessConstraintRepImpl)object).uniqueColNames);
    }

    public int hashCode() {
        return this.getClass().hashCode() ^ SetUtils.hashContentsDisregardingSort(this.uniqueColNames);
    }
}

