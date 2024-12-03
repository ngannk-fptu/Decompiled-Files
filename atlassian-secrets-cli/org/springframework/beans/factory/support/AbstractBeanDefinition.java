/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.beans.factory.support;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import org.springframework.beans.BeanMetadataAttributeAccessor;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.AutowireCandidateQualifier;
import org.springframework.beans.factory.support.BeanDefinitionDefaults;
import org.springframework.beans.factory.support.BeanDefinitionResource;
import org.springframework.beans.factory.support.BeanDefinitionValidationException;
import org.springframework.beans.factory.support.MethodOverride;
import org.springframework.beans.factory.support.MethodOverrides;
import org.springframework.core.ResolvableType;
import org.springframework.core.io.DescriptiveResource;
import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

public abstract class AbstractBeanDefinition
extends BeanMetadataAttributeAccessor
implements BeanDefinition,
Cloneable {
    public static final String SCOPE_DEFAULT = "";
    public static final int AUTOWIRE_NO = 0;
    public static final int AUTOWIRE_BY_NAME = 1;
    public static final int AUTOWIRE_BY_TYPE = 2;
    public static final int AUTOWIRE_CONSTRUCTOR = 3;
    @Deprecated
    public static final int AUTOWIRE_AUTODETECT = 4;
    public static final int DEPENDENCY_CHECK_NONE = 0;
    public static final int DEPENDENCY_CHECK_OBJECTS = 1;
    public static final int DEPENDENCY_CHECK_SIMPLE = 2;
    public static final int DEPENDENCY_CHECK_ALL = 3;
    public static final String INFER_METHOD = "(inferred)";
    @Nullable
    private volatile Object beanClass;
    @Nullable
    private String scope = "";
    private boolean abstractFlag = false;
    @Nullable
    private Boolean lazyInit;
    private int autowireMode = 0;
    private int dependencyCheck = 0;
    @Nullable
    private String[] dependsOn;
    private boolean autowireCandidate = true;
    private boolean primary = false;
    private final Map<String, AutowireCandidateQualifier> qualifiers = new LinkedHashMap<String, AutowireCandidateQualifier>();
    @Nullable
    private Supplier<?> instanceSupplier;
    private boolean nonPublicAccessAllowed = true;
    private boolean lenientConstructorResolution = true;
    @Nullable
    private String factoryBeanName;
    @Nullable
    private String factoryMethodName;
    @Nullable
    private ConstructorArgumentValues constructorArgumentValues;
    @Nullable
    private MutablePropertyValues propertyValues;
    private MethodOverrides methodOverrides = new MethodOverrides();
    @Nullable
    private String initMethodName;
    @Nullable
    private String destroyMethodName;
    private boolean enforceInitMethod = true;
    private boolean enforceDestroyMethod = true;
    private boolean synthetic = false;
    private int role = 0;
    @Nullable
    private String description;
    @Nullable
    private Resource resource;

    protected AbstractBeanDefinition() {
        this(null, null);
    }

    protected AbstractBeanDefinition(@Nullable ConstructorArgumentValues cargs, @Nullable MutablePropertyValues pvs) {
        this.constructorArgumentValues = cargs;
        this.propertyValues = pvs;
    }

    protected AbstractBeanDefinition(BeanDefinition original) {
        this.setParentName(original.getParentName());
        this.setBeanClassName(original.getBeanClassName());
        this.setScope(original.getScope());
        this.setAbstract(original.isAbstract());
        this.setFactoryBeanName(original.getFactoryBeanName());
        this.setFactoryMethodName(original.getFactoryMethodName());
        this.setRole(original.getRole());
        this.setSource(original.getSource());
        this.copyAttributesFrom(original);
        if (original instanceof AbstractBeanDefinition) {
            Boolean lazyInit;
            AbstractBeanDefinition originalAbd = (AbstractBeanDefinition)original;
            if (originalAbd.hasBeanClass()) {
                this.setBeanClass(originalAbd.getBeanClass());
            }
            if (originalAbd.hasConstructorArgumentValues()) {
                this.setConstructorArgumentValues(new ConstructorArgumentValues(original.getConstructorArgumentValues()));
            }
            if (originalAbd.hasPropertyValues()) {
                this.setPropertyValues(new MutablePropertyValues(original.getPropertyValues()));
            }
            if (originalAbd.hasMethodOverrides()) {
                this.setMethodOverrides(new MethodOverrides(originalAbd.getMethodOverrides()));
            }
            if ((lazyInit = originalAbd.getLazyInit()) != null) {
                this.setLazyInit(lazyInit);
            }
            this.setAutowireMode(originalAbd.getAutowireMode());
            this.setDependencyCheck(originalAbd.getDependencyCheck());
            this.setDependsOn(originalAbd.getDependsOn());
            this.setAutowireCandidate(originalAbd.isAutowireCandidate());
            this.setPrimary(originalAbd.isPrimary());
            this.copyQualifiersFrom(originalAbd);
            this.setInstanceSupplier(originalAbd.getInstanceSupplier());
            this.setNonPublicAccessAllowed(originalAbd.isNonPublicAccessAllowed());
            this.setLenientConstructorResolution(originalAbd.isLenientConstructorResolution());
            this.setInitMethodName(originalAbd.getInitMethodName());
            this.setEnforceInitMethod(originalAbd.isEnforceInitMethod());
            this.setDestroyMethodName(originalAbd.getDestroyMethodName());
            this.setEnforceDestroyMethod(originalAbd.isEnforceDestroyMethod());
            this.setSynthetic(originalAbd.isSynthetic());
            this.setResource(originalAbd.getResource());
        } else {
            this.setConstructorArgumentValues(new ConstructorArgumentValues(original.getConstructorArgumentValues()));
            this.setPropertyValues(new MutablePropertyValues(original.getPropertyValues()));
            this.setLazyInit(original.isLazyInit());
            this.setResourceDescription(original.getResourceDescription());
        }
    }

    public void overrideFrom(BeanDefinition other) {
        if (StringUtils.hasLength(other.getBeanClassName())) {
            this.setBeanClassName(other.getBeanClassName());
        }
        if (StringUtils.hasLength(other.getScope())) {
            this.setScope(other.getScope());
        }
        this.setAbstract(other.isAbstract());
        if (StringUtils.hasLength(other.getFactoryBeanName())) {
            this.setFactoryBeanName(other.getFactoryBeanName());
        }
        if (StringUtils.hasLength(other.getFactoryMethodName())) {
            this.setFactoryMethodName(other.getFactoryMethodName());
        }
        this.setRole(other.getRole());
        this.setSource(other.getSource());
        this.copyAttributesFrom(other);
        if (other instanceof AbstractBeanDefinition) {
            Boolean lazyInit;
            AbstractBeanDefinition otherAbd = (AbstractBeanDefinition)other;
            if (otherAbd.hasBeanClass()) {
                this.setBeanClass(otherAbd.getBeanClass());
            }
            if (otherAbd.hasConstructorArgumentValues()) {
                this.getConstructorArgumentValues().addArgumentValues(other.getConstructorArgumentValues());
            }
            if (otherAbd.hasPropertyValues()) {
                this.getPropertyValues().addPropertyValues(other.getPropertyValues());
            }
            if (otherAbd.hasMethodOverrides()) {
                this.getMethodOverrides().addOverrides(otherAbd.getMethodOverrides());
            }
            if ((lazyInit = otherAbd.getLazyInit()) != null) {
                this.setLazyInit(lazyInit);
            }
            this.setAutowireMode(otherAbd.getAutowireMode());
            this.setDependencyCheck(otherAbd.getDependencyCheck());
            this.setDependsOn(otherAbd.getDependsOn());
            this.setAutowireCandidate(otherAbd.isAutowireCandidate());
            this.setPrimary(otherAbd.isPrimary());
            this.copyQualifiersFrom(otherAbd);
            this.setInstanceSupplier(otherAbd.getInstanceSupplier());
            this.setNonPublicAccessAllowed(otherAbd.isNonPublicAccessAllowed());
            this.setLenientConstructorResolution(otherAbd.isLenientConstructorResolution());
            if (otherAbd.getInitMethodName() != null) {
                this.setInitMethodName(otherAbd.getInitMethodName());
                this.setEnforceInitMethod(otherAbd.isEnforceInitMethod());
            }
            if (otherAbd.getDestroyMethodName() != null) {
                this.setDestroyMethodName(otherAbd.getDestroyMethodName());
                this.setEnforceDestroyMethod(otherAbd.isEnforceDestroyMethod());
            }
            this.setSynthetic(otherAbd.isSynthetic());
            this.setResource(otherAbd.getResource());
        } else {
            this.getConstructorArgumentValues().addArgumentValues(other.getConstructorArgumentValues());
            this.getPropertyValues().addPropertyValues(other.getPropertyValues());
            this.setLazyInit(other.isLazyInit());
            this.setResourceDescription(other.getResourceDescription());
        }
    }

    public void applyDefaults(BeanDefinitionDefaults defaults) {
        Boolean lazyInit = defaults.getLazyInit();
        if (lazyInit != null) {
            this.setLazyInit(lazyInit);
        }
        this.setAutowireMode(defaults.getAutowireMode());
        this.setDependencyCheck(defaults.getDependencyCheck());
        this.setInitMethodName(defaults.getInitMethodName());
        this.setEnforceInitMethod(false);
        this.setDestroyMethodName(defaults.getDestroyMethodName());
        this.setEnforceDestroyMethod(false);
    }

    @Override
    public void setBeanClassName(@Nullable String beanClassName) {
        this.beanClass = beanClassName;
    }

    @Override
    @Nullable
    public String getBeanClassName() {
        Object beanClassObject = this.beanClass;
        if (beanClassObject instanceof Class) {
            return ((Class)beanClassObject).getName();
        }
        return (String)beanClassObject;
    }

    public void setBeanClass(@Nullable Class<?> beanClass) {
        this.beanClass = beanClass;
    }

    public Class<?> getBeanClass() throws IllegalStateException {
        Object beanClassObject = this.beanClass;
        if (beanClassObject == null) {
            throw new IllegalStateException("No bean class specified on bean definition");
        }
        if (!(beanClassObject instanceof Class)) {
            throw new IllegalStateException("Bean class name [" + beanClassObject + "] has not been resolved into an actual Class");
        }
        return (Class)beanClassObject;
    }

    public boolean hasBeanClass() {
        return this.beanClass instanceof Class;
    }

    @Nullable
    public Class<?> resolveBeanClass(@Nullable ClassLoader classLoader) throws ClassNotFoundException {
        Class<?> resolvedClass;
        String className = this.getBeanClassName();
        if (className == null) {
            return null;
        }
        this.beanClass = resolvedClass = ClassUtils.forName(className, classLoader);
        return resolvedClass;
    }

    @Override
    public ResolvableType getResolvableType() {
        return this.hasBeanClass() ? ResolvableType.forClass(this.getBeanClass()) : ResolvableType.NONE;
    }

    @Override
    public void setScope(@Nullable String scope) {
        this.scope = scope;
    }

    @Override
    @Nullable
    public String getScope() {
        return this.scope;
    }

    @Override
    public boolean isSingleton() {
        return "singleton".equals(this.scope) || SCOPE_DEFAULT.equals(this.scope);
    }

    @Override
    public boolean isPrototype() {
        return "prototype".equals(this.scope);
    }

    public void setAbstract(boolean abstractFlag) {
        this.abstractFlag = abstractFlag;
    }

    @Override
    public boolean isAbstract() {
        return this.abstractFlag;
    }

    @Override
    public void setLazyInit(boolean lazyInit) {
        this.lazyInit = lazyInit;
    }

    @Override
    public boolean isLazyInit() {
        return this.lazyInit != null && this.lazyInit != false;
    }

    @Nullable
    public Boolean getLazyInit() {
        return this.lazyInit;
    }

    public void setAutowireMode(int autowireMode) {
        this.autowireMode = autowireMode;
    }

    public int getAutowireMode() {
        return this.autowireMode;
    }

    public int getResolvedAutowireMode() {
        if (this.autowireMode == 4) {
            Constructor<?>[] constructors;
            for (Constructor<?> constructor : constructors = this.getBeanClass().getConstructors()) {
                if (constructor.getParameterCount() != 0) continue;
                return 2;
            }
            return 3;
        }
        return this.autowireMode;
    }

    public void setDependencyCheck(int dependencyCheck) {
        this.dependencyCheck = dependencyCheck;
    }

    public int getDependencyCheck() {
        return this.dependencyCheck;
    }

    @Override
    public void setDependsOn(String ... dependsOn) {
        this.dependsOn = dependsOn;
    }

    @Override
    @Nullable
    public String[] getDependsOn() {
        return this.dependsOn;
    }

    @Override
    public void setAutowireCandidate(boolean autowireCandidate) {
        this.autowireCandidate = autowireCandidate;
    }

    @Override
    public boolean isAutowireCandidate() {
        return this.autowireCandidate;
    }

    @Override
    public void setPrimary(boolean primary) {
        this.primary = primary;
    }

    @Override
    public boolean isPrimary() {
        return this.primary;
    }

    public void addQualifier(AutowireCandidateQualifier qualifier) {
        this.qualifiers.put(qualifier.getTypeName(), qualifier);
    }

    public boolean hasQualifier(String typeName) {
        return this.qualifiers.containsKey(typeName);
    }

    @Nullable
    public AutowireCandidateQualifier getQualifier(String typeName) {
        return this.qualifiers.get(typeName);
    }

    public Set<AutowireCandidateQualifier> getQualifiers() {
        return new LinkedHashSet<AutowireCandidateQualifier>(this.qualifiers.values());
    }

    public void copyQualifiersFrom(AbstractBeanDefinition source) {
        Assert.notNull((Object)source, "Source must not be null");
        this.qualifiers.putAll(source.qualifiers);
    }

    public void setInstanceSupplier(@Nullable Supplier<?> instanceSupplier) {
        this.instanceSupplier = instanceSupplier;
    }

    @Nullable
    public Supplier<?> getInstanceSupplier() {
        return this.instanceSupplier;
    }

    public void setNonPublicAccessAllowed(boolean nonPublicAccessAllowed) {
        this.nonPublicAccessAllowed = nonPublicAccessAllowed;
    }

    public boolean isNonPublicAccessAllowed() {
        return this.nonPublicAccessAllowed;
    }

    public void setLenientConstructorResolution(boolean lenientConstructorResolution) {
        this.lenientConstructorResolution = lenientConstructorResolution;
    }

    public boolean isLenientConstructorResolution() {
        return this.lenientConstructorResolution;
    }

    @Override
    public void setFactoryBeanName(@Nullable String factoryBeanName) {
        this.factoryBeanName = factoryBeanName;
    }

    @Override
    @Nullable
    public String getFactoryBeanName() {
        return this.factoryBeanName;
    }

    @Override
    public void setFactoryMethodName(@Nullable String factoryMethodName) {
        this.factoryMethodName = factoryMethodName;
    }

    @Override
    @Nullable
    public String getFactoryMethodName() {
        return this.factoryMethodName;
    }

    public void setConstructorArgumentValues(ConstructorArgumentValues constructorArgumentValues) {
        this.constructorArgumentValues = constructorArgumentValues;
    }

    @Override
    public ConstructorArgumentValues getConstructorArgumentValues() {
        if (this.constructorArgumentValues == null) {
            this.constructorArgumentValues = new ConstructorArgumentValues();
        }
        return this.constructorArgumentValues;
    }

    @Override
    public boolean hasConstructorArgumentValues() {
        return this.constructorArgumentValues != null && !this.constructorArgumentValues.isEmpty();
    }

    public void setPropertyValues(MutablePropertyValues propertyValues) {
        this.propertyValues = propertyValues;
    }

    @Override
    public MutablePropertyValues getPropertyValues() {
        if (this.propertyValues == null) {
            this.propertyValues = new MutablePropertyValues();
        }
        return this.propertyValues;
    }

    @Override
    public boolean hasPropertyValues() {
        return this.propertyValues != null && !this.propertyValues.isEmpty();
    }

    public void setMethodOverrides(MethodOverrides methodOverrides) {
        this.methodOverrides = methodOverrides;
    }

    public MethodOverrides getMethodOverrides() {
        return this.methodOverrides;
    }

    public boolean hasMethodOverrides() {
        return !this.methodOverrides.isEmpty();
    }

    @Override
    public void setInitMethodName(@Nullable String initMethodName) {
        this.initMethodName = initMethodName;
    }

    @Override
    @Nullable
    public String getInitMethodName() {
        return this.initMethodName;
    }

    public void setEnforceInitMethod(boolean enforceInitMethod) {
        this.enforceInitMethod = enforceInitMethod;
    }

    public boolean isEnforceInitMethod() {
        return this.enforceInitMethod;
    }

    @Override
    public void setDestroyMethodName(@Nullable String destroyMethodName) {
        this.destroyMethodName = destroyMethodName;
    }

    @Override
    @Nullable
    public String getDestroyMethodName() {
        return this.destroyMethodName;
    }

    public void setEnforceDestroyMethod(boolean enforceDestroyMethod) {
        this.enforceDestroyMethod = enforceDestroyMethod;
    }

    public boolean isEnforceDestroyMethod() {
        return this.enforceDestroyMethod;
    }

    public void setSynthetic(boolean synthetic) {
        this.synthetic = synthetic;
    }

    public boolean isSynthetic() {
        return this.synthetic;
    }

    @Override
    public void setRole(int role) {
        this.role = role;
    }

    @Override
    public int getRole() {
        return this.role;
    }

    @Override
    public void setDescription(@Nullable String description) {
        this.description = description;
    }

    @Override
    @Nullable
    public String getDescription() {
        return this.description;
    }

    public void setResource(@Nullable Resource resource) {
        this.resource = resource;
    }

    @Nullable
    public Resource getResource() {
        return this.resource;
    }

    public void setResourceDescription(@Nullable String resourceDescription) {
        this.resource = resourceDescription != null ? new DescriptiveResource(resourceDescription) : null;
    }

    @Override
    @Nullable
    public String getResourceDescription() {
        return this.resource != null ? this.resource.getDescription() : null;
    }

    public void setOriginatingBeanDefinition(BeanDefinition originatingBd) {
        this.resource = new BeanDefinitionResource(originatingBd);
    }

    @Override
    @Nullable
    public BeanDefinition getOriginatingBeanDefinition() {
        return this.resource instanceof BeanDefinitionResource ? ((BeanDefinitionResource)this.resource).getBeanDefinition() : null;
    }

    public void validate() throws BeanDefinitionValidationException {
        if (this.hasMethodOverrides() && this.getFactoryMethodName() != null) {
            throw new BeanDefinitionValidationException("Cannot combine factory method with container-generated method overrides: the factory method must create the concrete bean instance.");
        }
        if (this.hasBeanClass()) {
            this.prepareMethodOverrides();
        }
    }

    public void prepareMethodOverrides() throws BeanDefinitionValidationException {
        if (this.hasMethodOverrides()) {
            this.getMethodOverrides().getOverrides().forEach(this::prepareMethodOverride);
        }
    }

    protected void prepareMethodOverride(MethodOverride mo) throws BeanDefinitionValidationException {
        int count = ClassUtils.getMethodCountForName(this.getBeanClass(), mo.getMethodName());
        if (count == 0) {
            throw new BeanDefinitionValidationException("Invalid method override: no method with name '" + mo.getMethodName() + "' on class [" + this.getBeanClassName() + "]");
        }
        if (count == 1) {
            mo.setOverloaded(false);
        }
    }

    public Object clone() {
        return this.cloneBeanDefinition();
    }

    public abstract AbstractBeanDefinition cloneBeanDefinition();

    @Override
    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof AbstractBeanDefinition)) {
            return false;
        }
        AbstractBeanDefinition that = (AbstractBeanDefinition)other;
        return ObjectUtils.nullSafeEquals(this.getBeanClassName(), that.getBeanClassName()) && ObjectUtils.nullSafeEquals(this.scope, that.scope) && this.abstractFlag == that.abstractFlag && this.lazyInit == that.lazyInit && this.autowireMode == that.autowireMode && this.dependencyCheck == that.dependencyCheck && Arrays.equals(this.dependsOn, that.dependsOn) && this.autowireCandidate == that.autowireCandidate && ObjectUtils.nullSafeEquals(this.qualifiers, that.qualifiers) && this.primary == that.primary && this.nonPublicAccessAllowed == that.nonPublicAccessAllowed && this.lenientConstructorResolution == that.lenientConstructorResolution && this.equalsConstructorArgumentValues(that) && this.equalsPropertyValues(that) && ObjectUtils.nullSafeEquals(this.methodOverrides, that.methodOverrides) && ObjectUtils.nullSafeEquals(this.factoryBeanName, that.factoryBeanName) && ObjectUtils.nullSafeEquals(this.factoryMethodName, that.factoryMethodName) && ObjectUtils.nullSafeEquals(this.initMethodName, that.initMethodName) && this.enforceInitMethod == that.enforceInitMethod && ObjectUtils.nullSafeEquals(this.destroyMethodName, that.destroyMethodName) && this.enforceDestroyMethod == that.enforceDestroyMethod && this.synthetic == that.synthetic && this.role == that.role && super.equals(other);
    }

    private boolean equalsConstructorArgumentValues(AbstractBeanDefinition other) {
        if (!this.hasConstructorArgumentValues()) {
            return !other.hasConstructorArgumentValues();
        }
        return ObjectUtils.nullSafeEquals(this.constructorArgumentValues, other.constructorArgumentValues);
    }

    private boolean equalsPropertyValues(AbstractBeanDefinition other) {
        if (!this.hasPropertyValues()) {
            return !other.hasPropertyValues();
        }
        return ObjectUtils.nullSafeEquals(this.propertyValues, other.propertyValues);
    }

    @Override
    public int hashCode() {
        int hashCode = ObjectUtils.nullSafeHashCode(this.getBeanClassName());
        hashCode = 29 * hashCode + ObjectUtils.nullSafeHashCode(this.scope);
        if (this.hasConstructorArgumentValues()) {
            hashCode = 29 * hashCode + ObjectUtils.nullSafeHashCode(this.constructorArgumentValues);
        }
        if (this.hasPropertyValues()) {
            hashCode = 29 * hashCode + ObjectUtils.nullSafeHashCode(this.propertyValues);
        }
        hashCode = 29 * hashCode + ObjectUtils.nullSafeHashCode(this.factoryBeanName);
        hashCode = 29 * hashCode + ObjectUtils.nullSafeHashCode(this.factoryMethodName);
        hashCode = 29 * hashCode + super.hashCode();
        return hashCode;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("class [");
        sb.append(this.getBeanClassName()).append("]");
        sb.append("; scope=").append(this.scope);
        sb.append("; abstract=").append(this.abstractFlag);
        sb.append("; lazyInit=").append(this.lazyInit);
        sb.append("; autowireMode=").append(this.autowireMode);
        sb.append("; dependencyCheck=").append(this.dependencyCheck);
        sb.append("; autowireCandidate=").append(this.autowireCandidate);
        sb.append("; primary=").append(this.primary);
        sb.append("; factoryBeanName=").append(this.factoryBeanName);
        sb.append("; factoryMethodName=").append(this.factoryMethodName);
        sb.append("; initMethodName=").append(this.initMethodName);
        sb.append("; destroyMethodName=").append(this.destroyMethodName);
        if (this.resource != null) {
            sb.append("; defined in ").append(this.resource.getDescription());
        }
        return sb.toString();
    }
}

