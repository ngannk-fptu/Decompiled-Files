/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.beans.factory.annotation;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.lang.Nullable;
import org.springframework.util.ReflectionUtils;

public class InjectionMetadata {
    public static final InjectionMetadata EMPTY = new InjectionMetadata((Class)Object.class, (Collection)Collections.emptyList()){

        @Override
        protected boolean needsRefresh(Class<?> clazz) {
            return false;
        }

        @Override
        public void checkConfigMembers(RootBeanDefinition beanDefinition) {
        }

        @Override
        public void inject(Object target, @Nullable String beanName, @Nullable PropertyValues pvs) {
        }

        @Override
        public void clear(@Nullable PropertyValues pvs) {
        }
    };
    private final Class<?> targetClass;
    private final Collection<InjectedElement> injectedElements;
    @Nullable
    private volatile Set<InjectedElement> checkedElements;

    public InjectionMetadata(Class<?> targetClass, Collection<InjectedElement> elements) {
        this.targetClass = targetClass;
        this.injectedElements = elements;
    }

    protected boolean needsRefresh(Class<?> clazz) {
        return this.targetClass != clazz;
    }

    public void checkConfigMembers(RootBeanDefinition beanDefinition) {
        LinkedHashSet<InjectedElement> checkedElements = new LinkedHashSet<InjectedElement>(this.injectedElements.size());
        for (InjectedElement element : this.injectedElements) {
            Member member = element.getMember();
            if (beanDefinition.isExternallyManagedConfigMember(member)) continue;
            beanDefinition.registerExternallyManagedConfigMember(member);
            checkedElements.add(element);
        }
        this.checkedElements = checkedElements;
    }

    public void inject(Object target, @Nullable String beanName, @Nullable PropertyValues pvs) throws Throwable {
        Collection<InjectedElement> elementsToIterate;
        Set<InjectedElement> checkedElements = this.checkedElements;
        Collection<InjectedElement> collection = elementsToIterate = checkedElements != null ? checkedElements : this.injectedElements;
        if (!elementsToIterate.isEmpty()) {
            for (InjectedElement element : elementsToIterate) {
                element.inject(target, beanName, pvs);
            }
        }
    }

    public void clear(@Nullable PropertyValues pvs) {
        Collection<InjectedElement> elementsToIterate;
        Set<InjectedElement> checkedElements = this.checkedElements;
        Collection<InjectedElement> collection = elementsToIterate = checkedElements != null ? checkedElements : this.injectedElements;
        if (!elementsToIterate.isEmpty()) {
            for (InjectedElement element : elementsToIterate) {
                element.clearPropertySkipping(pvs);
            }
        }
    }

    public static InjectionMetadata forElements(Collection<InjectedElement> elements, Class<?> clazz) {
        return elements.isEmpty() ? new InjectionMetadata(clazz, Collections.emptyList()) : new InjectionMetadata(clazz, elements);
    }

    public static boolean needsRefresh(@Nullable InjectionMetadata metadata, Class<?> clazz) {
        return metadata == null || metadata.needsRefresh(clazz);
    }

    public static abstract class InjectedElement {
        protected final Member member;
        protected final boolean isField;
        @Nullable
        protected final PropertyDescriptor pd;
        @Nullable
        protected volatile Boolean skip;

        protected InjectedElement(Member member, @Nullable PropertyDescriptor pd) {
            this.member = member;
            this.isField = member instanceof Field;
            this.pd = pd;
        }

        public final Member getMember() {
            return this.member;
        }

        protected final Class<?> getResourceType() {
            if (this.isField) {
                return ((Field)this.member).getType();
            }
            if (this.pd != null) {
                return this.pd.getPropertyType();
            }
            return ((Method)this.member).getParameterTypes()[0];
        }

        protected final void checkResourceType(Class<?> resourceType) {
            if (this.isField) {
                Class<?> fieldType = ((Field)this.member).getType();
                if (!resourceType.isAssignableFrom(fieldType) && !fieldType.isAssignableFrom(resourceType)) {
                    throw new IllegalStateException("Specified field type [" + fieldType + "] is incompatible with resource type [" + resourceType.getName() + "]");
                }
            } else {
                Class<?> paramType;
                Class<?> clazz = paramType = this.pd != null ? this.pd.getPropertyType() : ((Method)this.member).getParameterTypes()[0];
                if (!resourceType.isAssignableFrom(paramType) && !paramType.isAssignableFrom(resourceType)) {
                    throw new IllegalStateException("Specified parameter type [" + paramType + "] is incompatible with resource type [" + resourceType.getName() + "]");
                }
            }
        }

        protected void inject(Object target, @Nullable String requestingBeanName, @Nullable PropertyValues pvs) throws Throwable {
            if (this.isField) {
                Field field = (Field)this.member;
                ReflectionUtils.makeAccessible(field);
                field.set(target, this.getResourceToInject(target, requestingBeanName));
            } else {
                if (this.checkPropertySkipping(pvs)) {
                    return;
                }
                try {
                    Method method = (Method)this.member;
                    ReflectionUtils.makeAccessible(method);
                    method.invoke(target, this.getResourceToInject(target, requestingBeanName));
                }
                catch (InvocationTargetException ex) {
                    throw ex.getTargetException();
                }
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        protected boolean checkPropertySkipping(@Nullable PropertyValues pvs) {
            Boolean skip = this.skip;
            if (skip != null) {
                return skip;
            }
            if (pvs == null) {
                this.skip = false;
                return false;
            }
            PropertyValues propertyValues = pvs;
            synchronized (propertyValues) {
                skip = this.skip;
                if (skip != null) {
                    return skip;
                }
                if (this.pd != null) {
                    if (pvs.contains(this.pd.getName())) {
                        this.skip = true;
                        return true;
                    }
                    if (pvs instanceof MutablePropertyValues) {
                        ((MutablePropertyValues)pvs).registerProcessedProperty(this.pd.getName());
                    }
                }
                this.skip = false;
                return false;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        protected void clearPropertySkipping(@Nullable PropertyValues pvs) {
            if (pvs == null) {
                return;
            }
            PropertyValues propertyValues = pvs;
            synchronized (propertyValues) {
                if (Boolean.FALSE.equals(this.skip) && this.pd != null && pvs instanceof MutablePropertyValues) {
                    ((MutablePropertyValues)pvs).clearProcessedProperty(this.pd.getName());
                }
            }
        }

        @Nullable
        protected Object getResourceToInject(Object target, @Nullable String requestingBeanName) {
            return null;
        }

        public boolean equals(@Nullable Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof InjectedElement)) {
                return false;
            }
            InjectedElement otherElement = (InjectedElement)other;
            return this.member.equals(otherElement.member);
        }

        public int hashCode() {
            return this.member.getClass().hashCode() * 29 + this.member.getName().hashCode();
        }

        public String toString() {
            return this.getClass().getSimpleName() + " for " + this.member;
        }
    }
}

