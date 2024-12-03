/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.plan.build.internal.spaces;

import org.hibernate.internal.util.StringHelper;
import org.hibernate.loader.plan.spi.JoinDefinedByMetadata;
import org.hibernate.loader.plan.spi.QuerySpace;
import org.hibernate.type.Type;

public class JoinImpl
implements JoinDefinedByMetadata {
    private final QuerySpace leftHandSide;
    private final QuerySpace rightHandSide;
    private final String lhsPropertyName;
    private final String[] rhsColumnNames;
    private final boolean rightHandSideRequired;
    private final Type joinedPropertyType;

    public JoinImpl(QuerySpace leftHandSide, String lhsPropertyName, QuerySpace rightHandSide, String[] rhsColumnNames, Type joinedPropertyType, boolean rightHandSideRequired) {
        this.leftHandSide = leftHandSide;
        this.lhsPropertyName = lhsPropertyName;
        this.rightHandSide = rightHandSide;
        this.rhsColumnNames = rhsColumnNames;
        this.rightHandSideRequired = rightHandSideRequired;
        this.joinedPropertyType = joinedPropertyType;
        if (StringHelper.isEmpty(lhsPropertyName)) {
            throw new IllegalArgumentException("Incoming 'lhsPropertyName' parameter was empty");
        }
    }

    @Override
    public QuerySpace getLeftHandSide() {
        return this.leftHandSide;
    }

    @Override
    public QuerySpace getRightHandSide() {
        return this.rightHandSide;
    }

    @Override
    public boolean isRightHandSideRequired() {
        return this.rightHandSideRequired;
    }

    @Override
    public String[] resolveAliasedLeftHandSideJoinConditionColumns(String leftHandSideTableAlias) {
        return this.getLeftHandSide().toAliasedColumns(leftHandSideTableAlias, this.getJoinedPropertyName());
    }

    @Override
    public String[] resolveNonAliasedRightHandSideJoinConditionColumns() {
        if (this.rhsColumnNames == null) {
            throw new IllegalStateException("rhsColumnNames were null.  Generally that indicates a composite join, in which case calls to resolveAliasedLeftHandSideJoinConditionColumns are not allowed");
        }
        return this.rhsColumnNames;
    }

    @Override
    public String getAnyAdditionalJoinConditions(String rhsTableAlias) {
        return null;
    }

    @Override
    public String getJoinedPropertyName() {
        return this.lhsPropertyName;
    }

    @Override
    public Type getJoinedPropertyType() {
        return this.joinedPropertyType;
    }
}

