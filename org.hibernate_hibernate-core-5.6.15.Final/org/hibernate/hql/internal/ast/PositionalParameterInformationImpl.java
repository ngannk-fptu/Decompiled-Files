/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.hql.internal.ast;

import java.util.ArrayList;
import java.util.List;
import org.hibernate.hql.spi.PositionalParameterInformation;
import org.hibernate.internal.util.collections.ArrayHelper;
import org.hibernate.type.Type;

public class PositionalParameterInformationImpl
implements PositionalParameterInformation {
    private final int label;
    private final List<Integer> sourceLocations = new ArrayList<Integer>();
    private Type expectedType;

    public PositionalParameterInformationImpl(int label, Type initialType) {
        this.label = label;
        this.expectedType = initialType;
    }

    @Override
    public int getLabel() {
        return this.label;
    }

    @Override
    public int[] getSourceLocations() {
        return ArrayHelper.toIntArray(this.sourceLocations);
    }

    @Override
    public Type getExpectedType() {
        return this.expectedType;
    }

    @Override
    public void setExpectedType(Type expectedType) {
        this.expectedType = expectedType;
    }

    @Override
    public void addSourceLocation(int location) {
        this.sourceLocations.add(location);
    }
}

