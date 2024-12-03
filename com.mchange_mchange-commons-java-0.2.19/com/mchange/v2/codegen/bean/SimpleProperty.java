/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.codegen.bean;

import com.mchange.v2.codegen.bean.Property;

public class SimpleProperty
implements Property {
    int variable_modifiers;
    String name;
    String simpleTypeName;
    String defensiveCopyExpression;
    String defaultValueExpression;
    int getter_modifiers;
    int setter_modifiers;
    boolean is_read_only;
    boolean is_bound;
    boolean is_constrained;

    @Override
    public int getVariableModifiers() {
        return this.variable_modifiers;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getSimpleTypeName() {
        return this.simpleTypeName;
    }

    @Override
    public String getDefensiveCopyExpression() {
        return this.defensiveCopyExpression;
    }

    @Override
    public String getDefaultValueExpression() {
        return this.defaultValueExpression;
    }

    @Override
    public int getGetterModifiers() {
        return this.getter_modifiers;
    }

    @Override
    public int getSetterModifiers() {
        return this.setter_modifiers;
    }

    @Override
    public boolean isReadOnly() {
        return this.is_read_only;
    }

    @Override
    public boolean isBound() {
        return this.is_bound;
    }

    @Override
    public boolean isConstrained() {
        return this.is_constrained;
    }

    public SimpleProperty(int n, String string, String string2, String string3, String string4, int n2, int n3, boolean bl, boolean bl2, boolean bl3) {
        this.variable_modifiers = n;
        this.name = string;
        this.simpleTypeName = string2;
        this.defensiveCopyExpression = string3;
        this.defaultValueExpression = string4;
        this.getter_modifiers = n2;
        this.setter_modifiers = n3;
        this.is_read_only = bl;
        this.is_bound = bl2;
        this.is_constrained = bl3;
    }

    public SimpleProperty(String string, String string2, String string3, String string4, boolean bl, boolean bl2, boolean bl3) {
        this(2, string, string2, string3, string4, 1, 1, bl, bl2, bl3);
    }
}

