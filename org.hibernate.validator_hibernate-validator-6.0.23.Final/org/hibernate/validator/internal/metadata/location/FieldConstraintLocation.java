/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.metadata.location;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Type;
import java.security.AccessController;
import java.security.PrivilegedAction;
import org.hibernate.validator.HibernateValidatorPermission;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.hibernate.validator.internal.metadata.location.ConstraintLocation;
import org.hibernate.validator.internal.util.ExecutableParameterNameProvider;
import org.hibernate.validator.internal.util.ReflectionHelper;
import org.hibernate.validator.internal.util.StringHelper;
import org.hibernate.validator.internal.util.privilegedactions.GetDeclaredField;

public class FieldConstraintLocation
implements ConstraintLocation {
    private final Field field;
    private final Field accessibleField;
    private final String propertyName;
    private final Type typeForValidatorResolution;

    FieldConstraintLocation(Field field) {
        this.field = field;
        this.accessibleField = FieldConstraintLocation.getAccessible(field);
        this.propertyName = ReflectionHelper.getPropertyName(field);
        this.typeForValidatorResolution = ReflectionHelper.boxedType(ReflectionHelper.typeOf(field));
    }

    @Override
    public Class<?> getDeclaringClass() {
        return this.field.getDeclaringClass();
    }

    @Override
    public Member getMember() {
        return this.field;
    }

    public String getPropertyName() {
        return this.propertyName;
    }

    @Override
    public Type getTypeForValidatorResolution() {
        return this.typeForValidatorResolution;
    }

    @Override
    public void appendTo(ExecutableParameterNameProvider parameterNameProvider, PathImpl path) {
        path.addPropertyNode(this.propertyName);
    }

    @Override
    public Object getValue(Object parent) {
        return ReflectionHelper.getValue(this.accessibleField, parent);
    }

    public String toString() {
        return "FieldConstraintLocation [member=" + StringHelper.toShortString((Member)this.field) + ", typeForValidatorResolution=" + StringHelper.toShortString(this.typeForValidatorResolution) + "]";
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        FieldConstraintLocation that = (FieldConstraintLocation)o;
        if (this.field != null ? !this.field.equals(that.field) : that.field != null) {
            return false;
        }
        return this.typeForValidatorResolution.equals(that.typeForValidatorResolution);
    }

    public int hashCode() {
        int result = this.field != null ? this.field.hashCode() : 0;
        result = 31 * result + this.typeForValidatorResolution.hashCode();
        return result;
    }

    private static Field getAccessible(Field original) {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(HibernateValidatorPermission.ACCESS_PRIVATE_MEMBERS);
        }
        Class<?> clazz = original.getDeclaringClass();
        return FieldConstraintLocation.run(GetDeclaredField.andMakeAccessible(clazz, original.getName()));
    }

    private static <T> T run(PrivilegedAction<T> action) {
        return System.getSecurityManager() != null ? AccessController.doPrivileged(action) : action.run();
    }
}

