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

class ReturnValueConstraintLocation
implements ConstraintLocation {
    private final Executable executable;
    private final Type typeForValidatorResolution;

    ReturnValueConstraintLocation(Executable executable) {
        this.executable = executable;
        this.typeForValidatorResolution = ReflectionHelper.boxedType(ReflectionHelper.typeOf(executable));
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

    @Override
    public void appendTo(ExecutableParameterNameProvider parameterNameProvider, PathImpl path) {
        path.addReturnValueNode();
    }

    @Override
    public Object getValue(Object parent) {
        return parent;
    }

    public String toString() {
        return "ReturnValueConstraintLocation [executable=" + this.executable + "]";
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + (this.executable == null ? 0 : this.executable.hashCode());
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
        ReturnValueConstraintLocation other = (ReturnValueConstraintLocation)obj;
        return !(this.executable == null ? other.executable != null : !this.executable.equals(other.executable));
    }
}

