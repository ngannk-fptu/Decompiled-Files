/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.hql.internal.classic;

import java.util.ArrayList;
import java.util.List;
import org.hibernate.hql.spi.ParameterInformation;
import org.hibernate.internal.util.collections.ArrayHelper;
import org.hibernate.param.ParameterBinder;
import org.hibernate.type.Type;

public abstract class AbstractParameterInformation
implements ParameterInformation,
ParameterBinder {
    private List<Integer> sqlPositions = new ArrayList<Integer>();

    @Override
    public int[] getSourceLocations() {
        return ArrayHelper.toIntArray(this.sqlPositions);
    }

    @Override
    public void addSourceLocation(int position) {
        this.sqlPositions.add(position);
    }

    @Override
    public Type getExpectedType() {
        return null;
    }

    @Override
    public void setExpectedType(Type expectedType) {
    }
}

