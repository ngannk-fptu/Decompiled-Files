/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ObjectUtils
 */
package org.springframework.data.util;

import java.lang.reflect.TypeVariable;
import org.springframework.data.util.ParentTypeAwareTypeInformation;
import org.springframework.data.util.TypeDiscoverer;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

class TypeVariableTypeInformation<T>
extends ParentTypeAwareTypeInformation<T> {
    private final TypeVariable<?> variable;

    public TypeVariableTypeInformation(TypeVariable<?> variable, TypeDiscoverer<?> parent) {
        super(variable, parent);
        Assert.notNull(variable, (String)"TypeVariable must not be null!");
        this.variable = variable;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof TypeVariableTypeInformation)) {
            return false;
        }
        TypeVariableTypeInformation that = (TypeVariableTypeInformation)obj;
        return this.getType().equals(that.getType());
    }

    @Override
    public int hashCode() {
        int result = 17;
        return result += 31 * ObjectUtils.nullSafeHashCode((Object)this.getType());
    }

    public String toString() {
        return this.variable.getName();
    }
}

