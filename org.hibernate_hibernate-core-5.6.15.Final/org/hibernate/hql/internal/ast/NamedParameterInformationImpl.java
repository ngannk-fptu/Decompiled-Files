/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.hql.internal.ast;

import java.util.ArrayList;
import java.util.List;
import org.hibernate.hql.spi.NamedParameterInformation;
import org.hibernate.internal.util.collections.ArrayHelper;
import org.hibernate.type.Type;

public class NamedParameterInformationImpl
implements NamedParameterInformation {
    private final String name;
    private final List<Integer> sqlPositions = new ArrayList<Integer>();
    private Type expectedType;

    NamedParameterInformationImpl(String name, Type initialType) {
        this.name = name;
        this.expectedType = initialType;
    }

    @Override
    public String getSourceName() {
        return this.name;
    }

    @Override
    public int[] getSourceLocations() {
        return ArrayHelper.toIntArray(this.sqlPositions);
    }

    @Override
    public Type getExpectedType() {
        return this.expectedType;
    }

    @Override
    public void addSourceLocation(int position) {
        this.sqlPositions.add(position);
    }

    @Override
    public void setExpectedType(Type expectedType) {
        this.expectedType = expectedType;
    }
}

