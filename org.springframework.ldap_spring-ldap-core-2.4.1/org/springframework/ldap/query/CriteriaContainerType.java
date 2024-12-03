/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.ldap.query;

import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.BinaryLogicalFilter;
import org.springframework.ldap.filter.OrFilter;

enum CriteriaContainerType {
    AND{

        @Override
        public BinaryLogicalFilter constructFilter() {
            return new AndFilter();
        }
    }
    ,
    OR{

        @Override
        public BinaryLogicalFilter constructFilter() {
            return new OrFilter();
        }
    };


    public void validateSameType(CriteriaContainerType oldType) {
        if (oldType != null && oldType != this) {
            throw new IllegalStateException(String.format("Container type has already been specified as %s, cannot change it to %s", oldType.toString(), this.toString()));
        }
    }

    public abstract BinaryLogicalFilter constructFilter();
}

