/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.i18n.templates;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.apache.abdera.i18n.templates.CachingContext;
import org.apache.abdera.i18n.templates.VarName;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class ObjectContext
extends CachingContext {
    private static final long serialVersionUID = -1387599933658718221L;
    private final Object target;
    private final Map<String, AccessibleObject> accessors = new HashMap<String, AccessibleObject>();

    public ObjectContext(Object object) {
        this(object, false);
    }

    public ObjectContext(Object object, boolean isiri) {
        if (object == null) {
            throw new IllegalArgumentException();
        }
        this.target = object;
        this.setIri(isiri);
        this.initMethods();
    }

    private void initMethods() {
        Class<?> _class = this.target.getClass();
        if (_class.isAnnotation() || _class.isArray() || _class.isEnum() || _class.isPrimitive()) {
            throw new IllegalArgumentException();
        }
        if (!_class.isInterface()) {
            Field[] fields = _class.getFields();
            for (AccessibleObject accessibleObject : fields) {
                if (Modifier.isPrivate(((Field)accessibleObject).getModifiers())) continue;
                this.accessors.put(this.getName(accessibleObject), accessibleObject);
            }
        }
        Method[] methods = _class.getMethods();
        for (AccessibleObject accessibleObject : methods) {
            String name = ((Method)accessibleObject).getName();
            if (Modifier.isPrivate(((Method)accessibleObject).getModifiers()) || ((Method)accessibleObject).getParameterTypes().length != 0 || ((Method)accessibleObject).getReturnType().equals(Void.class) || this.isReserved(name)) continue;
            this.accessors.put(this.getName(accessibleObject), accessibleObject);
        }
    }

    private String getName(AccessibleObject object) {
        String name = null;
        VarName varName = object.getAnnotation(VarName.class);
        if (varName != null) {
            return varName.value();
        }
        if (object instanceof Field) {
            name = ((Field)object).getName().toLowerCase();
        } else if (object instanceof Method) {
            name = ((Method)object).getName().toLowerCase();
            if (name.startsWith("get")) {
                name = name.substring(3);
            } else if (name.startsWith("is")) {
                name = name.substring(2);
            }
        }
        return name;
    }

    private boolean isReserved(String name) {
        return name.equals("toString") || name.equals("hashCode") || name.equals("notify") || name.equals("notifyAll") || name.equals("getClass") || name.equals("wait");
    }

    @Override
    protected <T> T resolveActual(String var) {
        try {
            var = var.toLowerCase();
            AccessibleObject accessor = this.accessors.get(var);
            if (accessor == null) {
                return null;
            }
            if (accessor instanceof Method) {
                Method method = (Method)accessor;
                return (T)method.invoke(this.target, new Object[0]);
            }
            if (accessor instanceof Field) {
                Field field = (Field)accessor;
                return (T)field.get(this.target);
            }
            return null;
        }
        catch (InvocationTargetException e) {
            throw new IllegalArgumentException("Accessor: " + var, e);
        }
        catch (IllegalAccessException e) {
            throw new IllegalArgumentException("Accessor: " + var, e);
        }
    }

    @Override
    public Iterator<String> iterator() {
        return this.accessors.keySet().iterator();
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + (this.target == null ? 0 : this.target.hashCode());
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
        ObjectContext other = (ObjectContext)obj;
        return !(this.target == null ? other.target != null : !this.target.equals(other.target));
    }
}

