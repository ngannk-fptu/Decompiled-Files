/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2.beanutils;

import java.util.Objects;
import org.apache.commons.configuration2.beanutils.BeanDeclaration;

public final class ConstructorArg {
    private final BeanDeclaration beanDeclaration;
    private final Object value;
    private final String typeName;

    private ConstructorArg(BeanDeclaration decl, Object val, String type) {
        this.beanDeclaration = decl;
        this.value = val;
        this.typeName = type;
    }

    public static ConstructorArg forBeanDeclaration(BeanDeclaration decl) {
        return ConstructorArg.forBeanDeclaration(decl, null);
    }

    public static ConstructorArg forBeanDeclaration(BeanDeclaration beanDeclaration, String typeName) {
        Objects.requireNonNull(beanDeclaration, "beanDeclaration");
        return new ConstructorArg(beanDeclaration, null, typeName);
    }

    public static ConstructorArg forValue(Object value) {
        return ConstructorArg.forValue(value, null);
    }

    public static ConstructorArg forValue(Object value, String typeName) {
        return new ConstructorArg(null, value, typeName);
    }

    public BeanDeclaration getBeanDeclaration() {
        return this.beanDeclaration;
    }

    public boolean isNestedBeanDeclaration() {
        return this.getBeanDeclaration() != null;
    }

    public Object getValue() {
        return this.value;
    }

    public String getTypeName() {
        return this.typeName;
    }

    public boolean matches(Class<?> argCls) {
        if (argCls == null) {
            return false;
        }
        return this.getTypeName() == null || this.getTypeName().equals(argCls.getName());
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append(this.getClass().getSimpleName());
        buf.append(" [ value = ");
        buf.append(this.isNestedBeanDeclaration() ? this.getBeanDeclaration() : this.getValue());
        if (this.getTypeName() != null) {
            buf.append(" (").append(this.getTypeName()).append(')');
        }
        buf.append(" ]");
        return buf.toString();
    }
}

