/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver;

import java.util.Map;
import org.aspectj.weaver.ConcreteTypeMunger;
import org.aspectj.weaver.ResolvedType;
import org.aspectj.weaver.ResolvedTypeMunger;
import org.aspectj.weaver.World;

public class TemporaryTypeMunger
extends ConcreteTypeMunger {
    public TemporaryTypeMunger(ResolvedTypeMunger munger, ResolvedType aspectType) {
        super(munger, aspectType);
    }

    public ConcreteTypeMunger parameterizeWith(Map parameterizationMap, World world) {
        throw new UnsupportedOperationException("Cannot be called on a TemporaryTypeMunger");
    }

    @Override
    public ConcreteTypeMunger parameterizedFor(ResolvedType targetType) {
        throw new UnsupportedOperationException("Cannot be called on a TemporaryTypeMunger");
    }
}

