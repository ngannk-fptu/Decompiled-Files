/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tuple.entity;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;
import org.hibernate.EntityMode;
import org.hibernate.EntityNameResolver;
import org.hibernate.HibernateException;
import org.hibernate.bytecode.enhance.spi.interceptor.BytecodeLazyAttributeInterceptor;
import org.hibernate.bytecode.enhance.spi.interceptor.EnhancementAsProxyLazinessInterceptor;
import org.hibernate.bytecode.spi.BytecodeProvider;
import org.hibernate.bytecode.spi.ProxyFactoryFactory;
import org.hibernate.bytecode.spi.ReflectionOptimizer;
import org.hibernate.cfg.Environment;
import org.hibernate.classic.Lifecycle;
import org.hibernate.engine.internal.ManagedTypeHelper;
import org.hibernate.engine.spi.SelfDirtinessTracker;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.property.access.spi.Getter;
import org.hibernate.property.access.spi.Setter;
import org.hibernate.proxy.ProxyFactory;
import org.hibernate.proxy.pojo.ProxyFactoryHelper;
import org.hibernate.tuple.Instantiator;
import org.hibernate.tuple.entity.AbstractEntityTuplizer;
import org.hibernate.tuple.entity.EntityMetamodel;
import org.hibernate.tuple.entity.PojoEntityInstantiator;
import org.hibernate.type.CompositeType;

public class PojoEntityTuplizer
extends AbstractEntityTuplizer {
    private static final CoreMessageLogger LOG = CoreLogging.messageLogger(PojoEntityTuplizer.class);
    private final Class mappedClass;
    private final Class proxyInterface;
    private final boolean lifecycleImplementor;
    private final ReflectionOptimizer optimizer;

    public PojoEntityTuplizer(EntityMetamodel entityMetamodel, PersistentClass mappedEntity) {
        super(entityMetamodel, mappedEntity);
        this.mappedClass = mappedEntity.getMappedClass();
        this.proxyInterface = mappedEntity.getProxyInterface();
        this.lifecycleImplementor = Lifecycle.class.isAssignableFrom(this.mappedClass);
        String[] getterNames = new String[this.propertySpan];
        String[] setterNames = new String[this.propertySpan];
        Class[] propTypes = new Class[this.propertySpan];
        for (int i = 0; i < this.propertySpan; ++i) {
            getterNames[i] = this.getters[i].getMethodName();
            setterNames[i] = this.setters[i].getMethodName();
            propTypes[i] = this.getters[i].getReturnType();
        }
        if (this.hasCustomAccessors || !Environment.useReflectionOptimizer()) {
            this.optimizer = null;
        } else {
            BytecodeProvider bytecodeProvider = entityMetamodel.getSessionFactory().getServiceRegistry().getService(BytecodeProvider.class);
            this.optimizer = bytecodeProvider.getReflectionOptimizer(this.mappedClass, getterNames, setterNames, propTypes);
        }
    }

    @Override
    protected ProxyFactory buildProxyFactory(PersistentClass persistentClass, Getter idGetter, Setter idSetter) {
        String entityName = this.getEntityName();
        Class mappedClass = persistentClass.getMappedClass();
        Class proxyInterface = persistentClass.getProxyInterface();
        Set<Class> proxyInterfaces = ProxyFactoryHelper.extractProxyInterfaces(persistentClass, entityName);
        Method proxyGetIdentifierMethod = ProxyFactoryHelper.extractProxyGetIdentifierMethod(idGetter, proxyInterface);
        Method proxySetIdentifierMethod = ProxyFactoryHelper.extractProxySetIdentifierMethod(idSetter, proxyInterface);
        ProxyFactory pf = this.buildProxyFactoryInternal(persistentClass, idGetter, idSetter);
        try {
            ProxyFactoryHelper.validateGetterSetterMethodProxyability("Getter", proxyGetIdentifierMethod);
            ProxyFactoryHelper.validateGetterSetterMethodProxyability("Setter", proxySetIdentifierMethod);
            ProxyFactoryHelper.validateProxyability(persistentClass);
            pf.postInstantiate(entityName, mappedClass, proxyInterfaces, proxyGetIdentifierMethod, proxySetIdentifierMethod, persistentClass.hasEmbeddedIdentifier() ? (CompositeType)persistentClass.getIdentifier().getType() : null);
        }
        catch (HibernateException he) {
            LOG.unableToCreateProxyFactory(entityName, he);
            pf = null;
        }
        return pf;
    }

    protected ProxyFactory buildProxyFactoryInternal(PersistentClass persistentClass, Getter idGetter, Setter idSetter) {
        ProxyFactoryFactory proxyFactory = this.getFactory().getServiceRegistry().getService(ProxyFactoryFactory.class);
        return proxyFactory.buildProxyFactory(this.getFactory());
    }

    @Override
    protected Instantiator buildInstantiator(EntityMetamodel entityMetamodel, PersistentClass persistentClass) {
        if (this.optimizer == null) {
            return new PojoEntityInstantiator(entityMetamodel, persistentClass, null);
        }
        return new PojoEntityInstantiator(entityMetamodel, persistentClass, this.optimizer.getInstantiationOptimizer());
    }

    @Override
    public void setPropertyValues(Object entity, Object[] values) throws HibernateException {
        if (!this.getEntityMetamodel().hasLazyProperties() && this.optimizer != null && this.optimizer.getAccessOptimizer() != null) {
            this.setPropertyValuesWithOptimizer(entity, values);
        } else {
            super.setPropertyValues(entity, values);
        }
    }

    @Override
    public Object[] getPropertyValues(Object entity) throws HibernateException {
        if (this.shouldGetAllProperties(entity) && this.optimizer != null && this.optimizer.getAccessOptimizer() != null) {
            return this.getPropertyValuesWithOptimizer(entity);
        }
        return super.getPropertyValues(entity);
    }

    @Override
    public Object[] getPropertyValuesToInsert(Object entity, Map mergeMap, SharedSessionContractImplementor session) {
        if (this.shouldGetAllProperties(entity) && this.optimizer != null && this.optimizer.getAccessOptimizer() != null) {
            return this.getPropertyValuesWithOptimizer(entity);
        }
        return super.getPropertyValuesToInsert(entity, mergeMap, session);
    }

    protected void setPropertyValuesWithOptimizer(Object object, Object[] values) {
        this.optimizer.getAccessOptimizer().setPropertyValues(object, values);
    }

    protected Object[] getPropertyValuesWithOptimizer(Object object) {
        return this.optimizer.getAccessOptimizer().getPropertyValues(object);
    }

    @Override
    public EntityMode getEntityMode() {
        return EntityMode.POJO;
    }

    @Override
    public Class getMappedClass() {
        return this.mappedClass;
    }

    @Override
    public boolean isLifecycleImplementor() {
        return this.lifecycleImplementor;
    }

    @Override
    protected Getter buildPropertyGetter(Property mappedProperty, PersistentClass mappedEntity) {
        return mappedProperty.getGetter(mappedEntity.getMappedClass());
    }

    @Override
    protected Setter buildPropertySetter(Property mappedProperty, PersistentClass mappedEntity) {
        return mappedProperty.getSetter(mappedEntity.getMappedClass());
    }

    @Override
    public Class getConcreteProxyClass() {
        return this.proxyInterface;
    }

    @Override
    public void afterInitialize(Object entity, SharedSessionContractImplementor session) {
        if (ManagedTypeHelper.isPersistentAttributeInterceptable(entity)) {
            BytecodeLazyAttributeInterceptor interceptor = this.getEntityMetamodel().getBytecodeEnhancementMetadata().extractLazyInterceptor(entity);
            if (interceptor == null || interceptor instanceof EnhancementAsProxyLazinessInterceptor) {
                this.getEntityMetamodel().getBytecodeEnhancementMetadata().injectInterceptor(entity, this.getIdentifier(entity, session), session);
            } else if (interceptor.getLinkedSession() == null) {
                interceptor.setSession(session);
            }
        }
        ManagedTypeHelper.processIfSelfDirtinessTracker(entity, PojoEntityTuplizer::clearDirtyAttributes);
    }

    private static void clearDirtyAttributes(SelfDirtinessTracker entity) {
        entity.$$_hibernate_clearDirtyAttributes();
    }

    @Override
    public String determineConcreteSubclassEntityName(Object entityInstance, SessionFactoryImplementor factory) {
        if (entityInstance == null) {
            return this.getEntityName();
        }
        Class<?> concreteEntityClass = entityInstance.getClass();
        if (concreteEntityClass == this.getMappedClass()) {
            return this.getEntityName();
        }
        String entityName = this.getEntityMetamodel().findEntityNameByEntityClass(concreteEntityClass);
        if (entityName == null) {
            throw new HibernateException("Unable to resolve entity name from Class [" + concreteEntityClass.getName() + "] expected instance/subclass of [" + this.getEntityName() + "]");
        }
        return entityName;
    }

    @Override
    public EntityNameResolver[] getEntityNameResolvers() {
        return null;
    }
}

