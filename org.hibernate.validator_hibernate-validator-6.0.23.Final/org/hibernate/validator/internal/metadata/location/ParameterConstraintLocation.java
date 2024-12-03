/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.metadata.location;

import java.lang.reflect.Executable;
import java.lang.reflect.Member;
import java.lang.reflect.Type;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.hibernate.validator.internal.metadata.location.ConstraintLocation;
import org.hibernate.validator.internal.util.ExecutableParameterNameProvider;
import org.hibernate.validator.internal.util.ReflectionHelper;

public class ParameterConstraintLocation
implements ConstraintLocation {
    private final Executable executable;
    private final int index;
    private final Type typeForValidatorResolution;

    ParameterConstraintLocation(Executable executable, int index) {
        this.executable = executable;
        this.index = index;
        this.typeForValidatorResolution = ReflectionHelper.boxedType(ReflectionHelper.typeOf(executable, index));
    }

    @Override
    public Class<?> getDeclaringClass() {
        return this.executable.getDeclaringClass();
    }

    @Override
    public Member getMember() {
        return this.executable;
    }

    @Override
    public Type getTypeForValidatorResolution() {
        return this.typeForValidatorResolution;
    }

    public int getIndex() {
        return this.index;
    }

    @Override
    public void appendTo(ExecutableParameterNameProvider parameterNameProvider, PathImpl path) {
        String name = parameterNameProvider.getParameterNames(this.executable).get(this.index);
        path.addParameterNode(name, this.index);
    }

    @Override
    public Object getValue(Object parent) {
        return ((Object[])parent)[this.index];
    }

    public String toString() {
        return "ParameterConstraintLocation [executable=" + this.executable + ", index=" + this.index + ", typeForValidatorResolution=" + this.typeForValidatorResolution + "]";
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + (this.executable == null ? 0 : this.executable.hashCode());
        result = 31 * result + this.index;
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        ParameterConstraintLocation other = (ParameterConstraintLocation)obj;
        if (this.executable == null ? other.executable != null : !this.executable.equals(other.executable)) {
            return false;
        }
        return this.index == other.index;
    }
}

