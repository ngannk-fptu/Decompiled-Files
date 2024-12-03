/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.codegen.bean;

import com.mchange.v2.codegen.bean.Property;

class SimplePropertyMask
implements Property {
    Property p;

    SimplePropertyMask(Property property) {
        this.p = property;
    }

    @Override
    public int getVariableModifiers() {
        return 2;
    }

    @Override
    public String getName() {
        return this.p.getName();
    }

    @Override
    public String getSimpleTypeName() {
        return this.p.getSimpleTypeName();
    }

    @Override
    public String getDefensiveCopyExpression() {
        return null;
    }

    @Override
    public String getDefaultValueExpression() {
        return this.p.getDefaultValueExpression();
    }

    @Override
    public int getGetterModifiers() {
        return 1;
    }

    @Override
    public int getSetterModifiers() {
        return 1;
    }

    @Override
    public boolean isReadOnly() {
        return false;
    }

    @Override
    public boolean isBound() {
        return false;
    }

    @Override
    public boolean isConstrained() {
        return false;
    }
}

