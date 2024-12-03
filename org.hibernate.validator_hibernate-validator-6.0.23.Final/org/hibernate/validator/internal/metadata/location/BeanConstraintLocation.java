/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.metadata.location;

import java.lang.reflect.Member;
import java.lang.reflect.Type;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.hibernate.validator.internal.metadata.location.ConstraintLocation;
import org.hibernate.validator.internal.util.ExecutableParameterNameProvider;
import org.hibernate.validator.internal.util.TypeHelper;

class BeanConstraintLocation
implements ConstraintLocation {
    private final Class<?> declaringClass;
    private final Type typeForValidatorResolution;

    BeanConstraintLocation(Class<?> declaringClass) {
        this.declaringClass = declaringClass;
        this.typeForValidatorResolution = declaringClass.getTypeParameters().length == 0 ? declaringClass : TypeHelper.parameterizedType(declaringClass, declaringClass.getTypeParameters());
    }

    @Override
    public Class<?> getDeclaringClass() {
        return this.declaringClass;
    }

    @Override
    public Member getMember() {
        return null;
    }

    @Override
    public Type getTypeForValidatorResolution() {
        return this.typeForValidatorResolution;
    }

    @Override
    public void appendTo(ExecutableParameterNameProvider parameterNameProvider, PathImpl path) {
        path.addBeanNode();
    }

    @Override
    public Object getValue(Object parent) {
        return parent;
    }

    public String toString() {
        return "BeanConstraintLocation [declaringClass=" + this.declaringClass + ", typeForValidatorResolution=" + this.typeForValidatorResolution + "]";
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + (this.declaringClass == null ? 0 : this.declaringClass.hashCode());
        result = 31 * result + (this.typeForValidatorResolution == null ? 0 : this.typeForValidatorResolution.hashCode());
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
        BeanConstraintLocation other = (BeanConstraintLocation)obj;
        if (this.declaringClass == null ? other.declaringClass != null : !this.declaringClass.equals(other.declaringClass)) {
            return false;
        }
        return !(this.typeForValidatorResolution == null ? other.typeForValidatorResolution != null : !this.typeForValidatorResolution.equals(other.typeForValidatorResolution));
    }
}

