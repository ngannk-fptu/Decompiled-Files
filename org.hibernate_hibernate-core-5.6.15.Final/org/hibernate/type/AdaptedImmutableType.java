/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type;

import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.AbstractStandardBasicType;
import org.hibernate.type.descriptor.java.ImmutableMutabilityPlan;
import org.hibernate.type.descriptor.java.MutabilityPlan;

public class AdaptedImmutableType<T>
extends AbstractSingleColumnStandardBasicType<T> {
    private final AbstractStandardBasicType<T> baseMutableType;

    public AdaptedImmutableType(AbstractStandardBasicType<T> baseMutableType) {
        super(baseMutableType.getSqlTypeDescriptor(), baseMutableType.getJavaTypeDescriptor());
        this.baseMutableType = baseMutableType;
    }

    @Override
    protected MutabilityPlan<T> getMutabilityPlan() {
        return ImmutableMutabilityPlan.INSTANCE;
    }

    @Override
    public String getName() {
        return "imm_" + this.baseMutableType.getName();
    }
}

