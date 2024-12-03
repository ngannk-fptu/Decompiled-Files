/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.metadata.location;

import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.security.AccessController;
import java.security.PrivilegedAction;
import org.hibernate.validator.HibernateValidatorPermission;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.hibernate.validator.internal.metadata.location.ConstraintLocation;
import org.hibernate.validator.internal.util.ExecutableParameterNameProvider;
import org.hibernate.validator.internal.util.ReflectionHelper;
import org.hibernate.validator.internal.util.StringHelper;
import org.hibernate.validator.internal.util.privilegedactions.GetDeclaredMethod;

public class GetterConstraintLocation
implements ConstraintLocation {
    private final Method method;
    private final Method accessibleMethod;
    private final String propertyName;
    private final Type typeForValidatorResolution;
    private final Class<?> declaringClass;

    GetterConstraintLocation(Class<?> declaringClass, Method method) {
        this.method = method;
        this.accessibleMethod = GetterConstraintLocation.getAccessible(method);
        this.propertyName = ReflectionHelper.getPropertyName(method);
        this.typeForValidatorResolution = ReflectionHelper.boxedType(ReflectionHelper.typeOf(method));
        this.declaringClass = declaringClass;
    }

    @Override
    public Class<?> getDeclaringClass() {
        return this.declaringClass;
    }

    @Override
    public Method getMember() {
        return this.method;
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
        return ReflectionHelper.getValue(this.accessibleMethod, parent);
    }

    public String toString() {
        return "GetterConstraintLocation [method=" + StringHelper.toShortString((Member)this.method) + ", typeForValidatorResolution=" + StringHelper.toShortString(this.typeForValidatorResolution) + "]";
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        GetterConstraintLocation that = (GetterConstraintLocation)o;
        if (this.method != null ? !this.method.equals(that.method) : that.method != null) {
            return false;
        }
        return this.typeForValidatorResolution.equals(that.typeForValidatorResolution);
    }

    public int hashCode() {
        int result = this.method.hashCode();
        result = 31 * result + this.typeForValidatorResolution.hashCode();
        return result;
    }

    private static Method getAccessible(Method original) {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(HibernateValidatorPermission.ACCESS_PRIVATE_MEMBERS);
        }
        Class<?> clazz = original.getDeclaringClass();
        Method accessibleMethod = GetterConstraintLocation.run(GetDeclaredMethod.andMakeAccessible(clazz, original.getName(), new Class[0]));
        return accessibleMethod;
    }

    private static <T> T run(PrivilegedAction<T> action) {
        return System.getSecurityManager() != null ? AccessController.doPrivileged(action) : action.run();
    }
}

