/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.beans;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import org.springframework.beans.AbstractNestablePropertyAccessor;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.CachedIntrospectionResults;
import org.springframework.beans.GenericTypeAwarePropertyDescriptor;
import org.springframework.beans.InvalidPropertyException;
import org.springframework.beans.NotWritablePropertyException;
import org.springframework.beans.PropertyMatches;
import org.springframework.beans.TypeConverterDelegate;
import org.springframework.beans.TypeMismatchException;
import org.springframework.core.ResolvableType;
import org.springframework.core.convert.Property;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.lang.Nullable;
import org.springframework.util.ReflectionUtils;

public class BeanWrapperImpl
extends AbstractNestablePropertyAccessor
implements BeanWrapper {
    @Nullable
    private CachedIntrospectionResults cachedIntrospectionResults;
    @Nullable
    private AccessControlContext acc;

    public BeanWrapperImpl() {
        this(true);
    }

    public BeanWrapperImpl(boolean registerDefaultEditors) {
        super(registerDefaultEditors);
    }

    public BeanWrapperImpl(Object object) {
        super(object);
    }

    public BeanWrapperImpl(Class<?> clazz) {
        super(clazz);
    }

    public BeanWrapperImpl(Object object, String nestedPath, Object rootObject) {
        super(object, nestedPath, rootObject);
    }

    private BeanWrapperImpl(Object object, String nestedPath, BeanWrapperImpl parent) {
        super(object, nestedPath, parent);
        this.setSecurityContext(parent.acc);
    }

    public void setBeanInstance(Object object) {
        this.wrappedObject = object;
        this.rootObject = object;
        this.typeConverterDelegate = new TypeConverterDelegate(this, this.wrappedObject);
        this.setIntrospectionClass(object.getClass());
    }

    @Override
    public void setWrappedInstance(Object object, @Nullable String nestedPath, @Nullable Object rootObject) {
        super.setWrappedInstance(object, nestedPath, rootObject);
        this.setIntrospectionClass(this.getWrappedClass());
    }

    protected void setIntrospectionClass(Class<?> clazz) {
        if (this.cachedIntrospectionResults != null && this.cachedIntrospectionResults.getBeanClass() != clazz) {
            this.cachedIntrospectionResults = null;
        }
    }

    private CachedIntrospectionResults getCachedIntrospectionResults() {
        if (this.cachedIntrospectionResults == null) {
            this.cachedIntrospectionResults = CachedIntrospectionResults.forClass(this.getWrappedClass());
        }
        return this.cachedIntrospectionResults;
    }

    public void setSecurityContext(@Nullable AccessControlContext acc) {
        this.acc = acc;
    }

    @Nullable
    public AccessControlContext getSecurityContext() {
        return this.acc;
    }

    @Nullable
    public Object convertForProperty(@Nullable Object value, String propertyName) throws TypeMismatchException {
        CachedIntrospectionResults cachedIntrospectionResults = this.getCachedIntrospectionResults();
        PropertyDescriptor pd = cachedIntrospectionResults.getPropertyDescriptor(propertyName);
        if (pd == null) {
            throw new InvalidPropertyException(this.getRootClass(), this.getNestedPath() + propertyName, "No property '" + propertyName + "' found");
        }
        TypeDescriptor td = cachedIntrospectionResults.getTypeDescriptor(pd);
        if (td == null) {
            td = cachedIntrospectionResults.addTypeDescriptor(pd, new TypeDescriptor(this.property(pd)));
        }
        return this.convertForProperty(propertyName, null, value, td);
    }

    private Property property(PropertyDescriptor pd) {
        GenericTypeAwarePropertyDescriptor gpd = (GenericTypeAwarePropertyDescriptor)pd;
        return new Property(gpd.getBeanClass(), gpd.getReadMethod(), gpd.getWriteMethod(), gpd.getName());
    }

    @Override
    @Nullable
    protected BeanPropertyHandler getLocalPropertyHandler(String propertyName) {
        PropertyDescriptor pd = this.getCachedIntrospectionResults().getPropertyDescriptor(propertyName);
        return pd != null ? new BeanPropertyHandler(pd) : null;
    }

    @Override
    protected BeanWrapperImpl newNestedPropertyAccessor(Object object, String nestedPath) {
        return new BeanWrapperImpl(object, nestedPath, this);
    }

    @Override
    protected NotWritablePropertyException createNotWritablePropertyException(String propertyName) {
        PropertyMatches matches = PropertyMatches.forProperty(propertyName, this.getRootClass());
        throw new NotWritablePropertyException(this.getRootClass(), this.getNestedPath() + propertyName, matches.buildErrorMessage(), matches.getPossibleMatches());
    }

    @Override
    public PropertyDescriptor[] getPropertyDescriptors() {
        return this.getCachedIntrospectionResults().getPropertyDescriptors();
    }

    @Override
    public PropertyDescriptor getPropertyDescriptor(String propertyName) throws InvalidPropertyException {
        BeanWrapperImpl nestedBw = (BeanWrapperImpl)this.getPropertyAccessorForPropertyPath(propertyName);
        String finalPath = this.getFinalPath(nestedBw, propertyName);
        PropertyDescriptor pd = nestedBw.getCachedIntrospectionResults().getPropertyDescriptor(finalPath);
        if (pd == null) {
            throw new InvalidPropertyException(this.getRootClass(), this.getNestedPath() + propertyName, "No property '" + propertyName + "' found");
        }
        return pd;
    }

    private class BeanPropertyHandler
    extends AbstractNestablePropertyAccessor.PropertyHandler {
        private final PropertyDescriptor pd;

        public BeanPropertyHandler(PropertyDescriptor pd) {
            super(pd.getPropertyType(), pd.getReadMethod() != null, pd.getWriteMethod() != null);
            this.pd = pd;
        }

        @Override
        public ResolvableType getResolvableType() {
            return ResolvableType.forMethodReturnType(this.pd.getReadMethod());
        }

        @Override
        public TypeDescriptor toTypeDescriptor() {
            return new TypeDescriptor(BeanWrapperImpl.this.property(this.pd));
        }

        @Override
        @Nullable
        public TypeDescriptor nested(int level) {
            return TypeDescriptor.nested(BeanWrapperImpl.this.property(this.pd), level);
        }

        @Override
        @Nullable
        public Object getValue() throws Exception {
            Method readMethod = this.pd.getReadMethod();
            if (System.getSecurityManager() != null) {
                AccessController.doPrivileged(() -> {
                    ReflectionUtils.makeAccessible(readMethod);
                    return null;
                });
                try {
                    return AccessController.doPrivileged(() -> readMethod.invoke(BeanWrapperImpl.this.getWrappedInstance(), (Object[])null), BeanWrapperImpl.this.acc);
                }
                catch (PrivilegedActionException pae) {
                    throw pae.getException();
                }
            }
            ReflectionUtils.makeAccessible(readMethod);
            return readMethod.invoke(BeanWrapperImpl.this.getWrappedInstance(), (Object[])null);
        }

        @Override
        public void setValue(@Nullable Object value) throws Exception {
            Method writeMethod;
            Method method = writeMethod = this.pd instanceof GenericTypeAwarePropertyDescriptor ? ((GenericTypeAwarePropertyDescriptor)this.pd).getWriteMethodForActualAccess() : this.pd.getWriteMethod();
            if (System.getSecurityManager() != null) {
                AccessController.doPrivileged(() -> {
                    ReflectionUtils.makeAccessible(writeMethod);
                    return null;
                });
                try {
                    AccessController.doPrivileged(() -> writeMethod.invoke(BeanWrapperImpl.this.getWrappedInstance(), value), BeanWrapperImpl.this.acc);
                }
                catch (PrivilegedActionException ex) {
                    throw ex.getException();
                }
            } else {
                ReflectionUtils.makeAccessible(writeMethod);
                writeMethod.invoke(BeanWrapperImpl.this.getWrappedInstance(), value);
            }
        }
    }
}

