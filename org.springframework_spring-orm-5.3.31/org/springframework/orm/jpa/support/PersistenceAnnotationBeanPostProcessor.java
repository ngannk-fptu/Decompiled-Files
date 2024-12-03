/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.EntityManager
 *  javax.persistence.EntityManagerFactory
 *  javax.persistence.PersistenceContext
 *  javax.persistence.PersistenceContextType
 *  javax.persistence.PersistenceUnit
 *  javax.persistence.SynchronizationType
 *  org.springframework.beans.BeanUtils
 *  org.springframework.beans.PropertyValues
 *  org.springframework.beans.factory.BeanCreationException
 *  org.springframework.beans.factory.BeanFactory
 *  org.springframework.beans.factory.BeanFactoryAware
 *  org.springframework.beans.factory.ListableBeanFactory
 *  org.springframework.beans.factory.NoSuchBeanDefinitionException
 *  org.springframework.beans.factory.annotation.InjectionMetadata
 *  org.springframework.beans.factory.annotation.InjectionMetadata$InjectedElement
 *  org.springframework.beans.factory.config.ConfigurableBeanFactory
 *  org.springframework.beans.factory.config.ConfigurableListableBeanFactory
 *  org.springframework.beans.factory.config.DestructionAwareBeanPostProcessor
 *  org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor
 *  org.springframework.beans.factory.config.NamedBeanHolder
 *  org.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor
 *  org.springframework.beans.factory.support.RootBeanDefinition
 *  org.springframework.core.BridgeMethodResolver
 *  org.springframework.core.PriorityOrdered
 *  org.springframework.core.annotation.AnnotationUtils
 *  org.springframework.jndi.JndiLocatorDelegate
 *  org.springframework.jndi.JndiTemplate
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ClassUtils
 *  org.springframework.util.ObjectUtils
 *  org.springframework.util.ReflectionUtils
 *  org.springframework.util.StringUtils
 */
package org.springframework.orm.jpa.support;

import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.PersistenceUnit;
import javax.persistence.SynchronizationType;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.InjectionMetadata;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.DestructionAwareBeanPostProcessor;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.beans.factory.config.NamedBeanHolder;
import org.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.jndi.JndiLocatorDelegate;
import org.springframework.jndi.JndiTemplate;
import org.springframework.lang.Nullable;
import org.springframework.orm.jpa.EntityManagerFactoryInfo;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;
import org.springframework.orm.jpa.EntityManagerProxy;
import org.springframework.orm.jpa.ExtendedEntityManagerCreator;
import org.springframework.orm.jpa.SharedEntityManagerCreator;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

public class PersistenceAnnotationBeanPostProcessor
implements InstantiationAwareBeanPostProcessor,
DestructionAwareBeanPostProcessor,
MergedBeanDefinitionPostProcessor,
PriorityOrdered,
BeanFactoryAware,
Serializable {
    @Nullable
    private Object jndiEnvironment;
    private boolean resourceRef = true;
    @Nullable
    private transient Map<String, String> persistenceUnits;
    @Nullable
    private transient Map<String, String> persistenceContexts;
    @Nullable
    private transient Map<String, String> extendedPersistenceContexts;
    private transient String defaultPersistenceUnitName = "";
    private int order = 0x7FFFFFFB;
    @Nullable
    private transient ListableBeanFactory beanFactory;
    private final transient Map<String, InjectionMetadata> injectionMetadataCache = new ConcurrentHashMap<String, InjectionMetadata>(256);
    private final Map<Object, EntityManager> extendedEntityManagersToClose = new ConcurrentHashMap<Object, EntityManager>(16);

    public void setJndiTemplate(Object jndiTemplate) {
        this.jndiEnvironment = jndiTemplate;
    }

    public void setJndiEnvironment(Properties jndiEnvironment) {
        this.jndiEnvironment = jndiEnvironment;
    }

    public void setResourceRef(boolean resourceRef) {
        this.resourceRef = resourceRef;
    }

    public void setPersistenceUnits(Map<String, String> persistenceUnits) {
        this.persistenceUnits = persistenceUnits;
    }

    public void setPersistenceContexts(Map<String, String> persistenceContexts) {
        this.persistenceContexts = persistenceContexts;
    }

    public void setExtendedPersistenceContexts(Map<String, String> extendedPersistenceContexts) {
        this.extendedPersistenceContexts = extendedPersistenceContexts;
    }

    public void setDefaultPersistenceUnitName(@Nullable String unitName) {
        this.defaultPersistenceUnitName = unitName != null ? unitName : "";
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public int getOrder() {
        return this.order;
    }

    public void setBeanFactory(BeanFactory beanFactory) {
        if (beanFactory instanceof ListableBeanFactory) {
            this.beanFactory = (ListableBeanFactory)beanFactory;
        }
    }

    public void postProcessMergedBeanDefinition(RootBeanDefinition beanDefinition, Class<?> beanType, String beanName) {
        InjectionMetadata metadata = this.findPersistenceMetadata(beanName, beanType, null);
        metadata.checkConfigMembers(beanDefinition);
    }

    public void resetBeanDefinition(String beanName) {
        this.injectionMetadataCache.remove(beanName);
    }

    public PropertyValues postProcessProperties(PropertyValues pvs, Object bean, String beanName) {
        InjectionMetadata metadata = this.findPersistenceMetadata(beanName, bean.getClass(), pvs);
        try {
            metadata.inject(bean, beanName, pvs);
        }
        catch (Throwable ex) {
            throw new BeanCreationException(beanName, "Injection of persistence dependencies failed", ex);
        }
        return pvs;
    }

    @Deprecated
    public PropertyValues postProcessPropertyValues(PropertyValues pvs, PropertyDescriptor[] pds, Object bean, String beanName) {
        return this.postProcessProperties(pvs, bean, beanName);
    }

    public void postProcessBeforeDestruction(Object bean, String beanName) {
        EntityManager emToClose = this.extendedEntityManagersToClose.remove(bean);
        EntityManagerFactoryUtils.closeEntityManager(emToClose);
    }

    public boolean requiresDestruction(Object bean) {
        return this.extendedEntityManagersToClose.containsKey(bean);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private InjectionMetadata findPersistenceMetadata(String beanName, Class<?> clazz, @Nullable PropertyValues pvs) {
        String cacheKey = StringUtils.hasLength((String)beanName) ? beanName : clazz.getName();
        InjectionMetadata metadata = this.injectionMetadataCache.get(cacheKey);
        if (InjectionMetadata.needsRefresh((InjectionMetadata)metadata, clazz)) {
            Map<String, InjectionMetadata> map = this.injectionMetadataCache;
            synchronized (map) {
                metadata = this.injectionMetadataCache.get(cacheKey);
                if (InjectionMetadata.needsRefresh((InjectionMetadata)metadata, clazz)) {
                    if (metadata != null) {
                        metadata.clear(pvs);
                    }
                    metadata = this.buildPersistenceMetadata(clazz);
                    this.injectionMetadataCache.put(cacheKey, metadata);
                }
            }
        }
        return metadata;
    }

    private InjectionMetadata buildPersistenceMetadata(Class<?> clazz) {
        if (!AnnotationUtils.isCandidateClass(clazz, Arrays.asList(PersistenceContext.class, PersistenceUnit.class))) {
            return InjectionMetadata.EMPTY;
        }
        ArrayList elements = new ArrayList();
        Class<?> targetClass = clazz;
        do {
            ArrayList currElements = new ArrayList();
            ReflectionUtils.doWithLocalFields(targetClass, field -> {
                if (field.isAnnotationPresent(PersistenceContext.class) || field.isAnnotationPresent(PersistenceUnit.class)) {
                    if (Modifier.isStatic(field.getModifiers())) {
                        throw new IllegalStateException("Persistence annotations are not supported on static fields");
                    }
                    currElements.add(new PersistenceElement(field, field, null));
                }
            });
            ReflectionUtils.doWithLocalMethods(targetClass, method -> {
                Method bridgedMethod = BridgeMethodResolver.findBridgedMethod((Method)method);
                if (!BridgeMethodResolver.isVisibilityBridgeMethodPair((Method)method, (Method)bridgedMethod)) {
                    return;
                }
                if ((bridgedMethod.isAnnotationPresent(PersistenceContext.class) || bridgedMethod.isAnnotationPresent(PersistenceUnit.class)) && method.equals(ClassUtils.getMostSpecificMethod((Method)method, (Class)clazz))) {
                    if (Modifier.isStatic(method.getModifiers())) {
                        throw new IllegalStateException("Persistence annotations are not supported on static methods");
                    }
                    if (method.getParameterCount() != 1) {
                        throw new IllegalStateException("Persistence annotation requires a single-arg method: " + method);
                    }
                    PropertyDescriptor pd = BeanUtils.findPropertyForMethod((Method)bridgedMethod, (Class)clazz);
                    currElements.add(new PersistenceElement(method, bridgedMethod, pd));
                }
            });
            elements.addAll(0, currElements);
        } while ((targetClass = targetClass.getSuperclass()) != null && targetClass != Object.class);
        return InjectionMetadata.forElements(elements, clazz);
    }

    @Nullable
    protected EntityManagerFactory getPersistenceUnit(@Nullable String unitName) {
        if (this.persistenceUnits != null) {
            String jndiName;
            String unitNameForLookup;
            String string = unitNameForLookup = unitName != null ? unitName : "";
            if (unitNameForLookup.isEmpty()) {
                unitNameForLookup = this.defaultPersistenceUnitName;
            }
            if ((jndiName = this.persistenceUnits.get(unitNameForLookup)) == null && unitNameForLookup.isEmpty() && this.persistenceUnits.size() == 1) {
                jndiName = this.persistenceUnits.values().iterator().next();
            }
            if (jndiName != null) {
                try {
                    return this.lookup(jndiName, EntityManagerFactory.class);
                }
                catch (Exception ex) {
                    throw new IllegalStateException("Could not obtain EntityManagerFactory [" + jndiName + "] from JNDI", ex);
                }
            }
        }
        return null;
    }

    @Nullable
    protected EntityManager getPersistenceContext(@Nullable String unitName, boolean extended) {
        Map<String, String> contexts;
        Map<String, String> map = contexts = extended ? this.extendedPersistenceContexts : this.persistenceContexts;
        if (contexts != null) {
            String jndiName;
            String unitNameForLookup;
            String string = unitNameForLookup = unitName != null ? unitName : "";
            if (unitNameForLookup.isEmpty()) {
                unitNameForLookup = this.defaultPersistenceUnitName;
            }
            if ((jndiName = contexts.get(unitNameForLookup)) == null && unitNameForLookup.isEmpty() && contexts.size() == 1) {
                jndiName = contexts.values().iterator().next();
            }
            if (jndiName != null) {
                try {
                    return this.lookup(jndiName, EntityManager.class);
                }
                catch (Exception ex) {
                    throw new IllegalStateException("Could not obtain EntityManager [" + jndiName + "] from JNDI", ex);
                }
            }
        }
        return null;
    }

    protected EntityManagerFactory findEntityManagerFactory(@Nullable String unitName, @Nullable String requestingBeanName) throws NoSuchBeanDefinitionException {
        String unitNameForLookup;
        String string = unitNameForLookup = unitName != null ? unitName : "";
        if (unitNameForLookup.isEmpty()) {
            unitNameForLookup = this.defaultPersistenceUnitName;
        }
        if (!unitNameForLookup.isEmpty()) {
            return this.findNamedEntityManagerFactory(unitNameForLookup, requestingBeanName);
        }
        return this.findDefaultEntityManagerFactory(requestingBeanName);
    }

    protected EntityManagerFactory findNamedEntityManagerFactory(String unitName, @Nullable String requestingBeanName) throws NoSuchBeanDefinitionException {
        Assert.state((this.beanFactory != null ? 1 : 0) != 0, (String)"ListableBeanFactory required for EntityManagerFactory bean lookup");
        EntityManagerFactory emf = EntityManagerFactoryUtils.findEntityManagerFactory(this.beanFactory, unitName);
        if (requestingBeanName != null && this.beanFactory instanceof ConfigurableBeanFactory) {
            ((ConfigurableBeanFactory)this.beanFactory).registerDependentBean(unitName, requestingBeanName);
        }
        return emf;
    }

    protected EntityManagerFactory findDefaultEntityManagerFactory(@Nullable String requestingBeanName) throws NoSuchBeanDefinitionException {
        Assert.state((this.beanFactory != null ? 1 : 0) != 0, (String)"ListableBeanFactory required for EntityManagerFactory bean lookup");
        if (this.beanFactory instanceof ConfigurableListableBeanFactory) {
            ConfigurableListableBeanFactory clbf = (ConfigurableListableBeanFactory)this.beanFactory;
            NamedBeanHolder emfHolder = clbf.resolveNamedBean(EntityManagerFactory.class);
            if (requestingBeanName != null) {
                clbf.registerDependentBean(emfHolder.getBeanName(), requestingBeanName);
            }
            return (EntityManagerFactory)emfHolder.getBeanInstance();
        }
        return (EntityManagerFactory)this.beanFactory.getBean(EntityManagerFactory.class);
    }

    protected <T> T lookup(String jndiName, Class<T> requiredType) throws Exception {
        return new LocatorDelegate().lookup(jndiName, requiredType);
    }

    private class PersistenceElement
    extends InjectionMetadata.InjectedElement {
        private final String unitName;
        @Nullable
        private PersistenceContextType type;
        private boolean synchronizedWithTransaction;
        @Nullable
        private Properties properties;

        public PersistenceElement(Member member, @Nullable AnnotatedElement ae, PropertyDescriptor pd) {
            super(member, pd);
            this.synchronizedWithTransaction = false;
            PersistenceContext pc = ae.getAnnotation(PersistenceContext.class);
            PersistenceUnit pu = ae.getAnnotation(PersistenceUnit.class);
            Class<EntityManager> resourceType = EntityManager.class;
            if (pc != null) {
                if (pu != null) {
                    throw new IllegalStateException("Member may only be annotated with either @PersistenceContext or @PersistenceUnit, not both: " + member);
                }
                Properties properties = null;
                Object[] pps = pc.properties();
                if (!ObjectUtils.isEmpty((Object[])pps)) {
                    properties = new Properties();
                    for (Object pp : pps) {
                        properties.setProperty(pp.name(), pp.value());
                    }
                }
                this.unitName = pc.unitName();
                this.type = pc.type();
                this.synchronizedWithTransaction = SynchronizationType.SYNCHRONIZED.equals((Object)pc.synchronization());
                this.properties = properties;
            } else {
                resourceType = EntityManagerFactory.class;
                this.unitName = pu.unitName();
            }
            this.checkResourceType(resourceType);
        }

        protected Object getResourceToInject(Object target, @Nullable String requestingBeanName) {
            if (this.type != null) {
                return this.type == PersistenceContextType.EXTENDED ? this.resolveExtendedEntityManager(target, requestingBeanName) : this.resolveEntityManager(requestingBeanName);
            }
            return this.resolveEntityManagerFactory(requestingBeanName);
        }

        private EntityManagerFactory resolveEntityManagerFactory(@Nullable String requestingBeanName) {
            EntityManagerFactory emf = PersistenceAnnotationBeanPostProcessor.this.getPersistenceUnit(this.unitName);
            if (emf == null) {
                emf = PersistenceAnnotationBeanPostProcessor.this.findEntityManagerFactory(this.unitName, requestingBeanName);
            }
            return emf;
        }

        private EntityManager resolveEntityManager(@Nullable String requestingBeanName) {
            EntityManager em = PersistenceAnnotationBeanPostProcessor.this.getPersistenceContext(this.unitName, false);
            if (em == null) {
                EntityManagerFactory emf = PersistenceAnnotationBeanPostProcessor.this.getPersistenceUnit(this.unitName);
                if (emf == null) {
                    emf = PersistenceAnnotationBeanPostProcessor.this.findEntityManagerFactory(this.unitName, requestingBeanName);
                }
                em = emf instanceof EntityManagerFactoryInfo && ((EntityManagerFactoryInfo)emf).getEntityManagerInterface() != null ? SharedEntityManagerCreator.createSharedEntityManager(emf, this.properties, this.synchronizedWithTransaction) : SharedEntityManagerCreator.createSharedEntityManager(emf, this.properties, this.synchronizedWithTransaction, this.getResourceType());
            }
            return em;
        }

        private EntityManager resolveExtendedEntityManager(Object target, @Nullable String requestingBeanName) {
            EntityManager em = PersistenceAnnotationBeanPostProcessor.this.getPersistenceContext(this.unitName, true);
            if (em == null) {
                EntityManagerFactory emf = PersistenceAnnotationBeanPostProcessor.this.getPersistenceUnit(this.unitName);
                if (emf == null) {
                    emf = PersistenceAnnotationBeanPostProcessor.this.findEntityManagerFactory(this.unitName, requestingBeanName);
                }
                em = ExtendedEntityManagerCreator.createContainerManagedEntityManager(emf, this.properties, this.synchronizedWithTransaction);
            }
            if (em instanceof EntityManagerProxy && PersistenceAnnotationBeanPostProcessor.this.beanFactory != null && requestingBeanName != null && PersistenceAnnotationBeanPostProcessor.this.beanFactory.containsBean(requestingBeanName) && !PersistenceAnnotationBeanPostProcessor.this.beanFactory.isPrototype(requestingBeanName)) {
                PersistenceAnnotationBeanPostProcessor.this.extendedEntityManagersToClose.put(target, ((EntityManagerProxy)em).getTargetEntityManager());
            }
            return em;
        }
    }

    private class LocatorDelegate {
        private LocatorDelegate() {
        }

        public <T> T lookup(String jndiName, Class<T> requiredType) throws Exception {
            JndiLocatorDelegate locator = new JndiLocatorDelegate();
            if (PersistenceAnnotationBeanPostProcessor.this.jndiEnvironment instanceof JndiTemplate) {
                locator.setJndiTemplate((JndiTemplate)PersistenceAnnotationBeanPostProcessor.this.jndiEnvironment);
            } else if (PersistenceAnnotationBeanPostProcessor.this.jndiEnvironment instanceof Properties) {
                locator.setJndiEnvironment((Properties)PersistenceAnnotationBeanPostProcessor.this.jndiEnvironment);
            } else if (PersistenceAnnotationBeanPostProcessor.this.jndiEnvironment != null) {
                throw new IllegalStateException("Illegal 'jndiEnvironment' type: " + PersistenceAnnotationBeanPostProcessor.this.jndiEnvironment.getClass());
            }
            locator.setResourceRef(PersistenceAnnotationBeanPostProcessor.this.resourceRef);
            return (T)locator.lookup(jndiName, requiredType);
        }
    }
}

