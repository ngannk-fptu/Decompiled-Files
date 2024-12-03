/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.metadata.location;

import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.hibernate.validator.internal.metadata.location.BeanConstraintLocation;
import org.hibernate.validator.internal.metadata.location.CrossParameterConstraintLocation;
import org.hibernate.validator.internal.metadata.location.FieldConstraintLocation;
import org.hibernate.validator.internal.metadata.location.GetterConstraintLocation;
import org.hibernate.validator.internal.metadata.location.ParameterConstraintLocation;
import org.hibernate.validator.internal.metadata.location.ReturnValueConstraintLocation;
import org.hibernate.validator.internal.metadata.location.TypeArgumentConstraintLocation;
import org.hibernate.validator.internal.util.ExecutableParameterNameProvider;

public interface ConstraintLocation {
    public static ConstraintLocation forClass(Class<?> declaringClass) {
        return new BeanConstraintLocation(declaringClass);
    }

    public static ConstraintLocation forField(Field field) {
        return new FieldConstraintLocation(field);
    }

    public static ConstraintLocation forGetter(Method getter) {
        return new GetterConstraintLocation(getter.getDeclaringClass(), getter);
    }

    public static ConstraintLocation forGetter(Class<?> declaringClass, Method getter) {
        return new GetterConstraintLocation(declaringClass, getter);
    }

    public static ConstraintLocation forTypeArgument(ConstraintLocation delegate, TypeVariable<?> typeParameter, Type typeOfAnnotatedElement) {
        return new TypeArgumentConstraintLocation(delegate, typeParameter, typeOfAnnotatedElement);
    }

    public static ConstraintLocation forReturnValue(Executable executable) {
        return new ReturnValueConstraintLocation(executable);
    }

    public static ConstraintLocation forCrossParameter(Executable executable) {
        return new CrossParameterConstraintLocation(executable);
    }

    public static ConstraintLocation forParameter(Executable executable, int index) {
        return new ParameterConstraintLocation(executable, index);
    }

    public Class<?> getDeclaringClass();

    public Member getMember();

    public Type getTypeForValidatorResolution();

    public void appendTo(ExecutableParameterNameProvider var1, PathImpl var2);

    public Object getValue(Object var1);
}

