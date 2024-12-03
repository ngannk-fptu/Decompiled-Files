/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 *  javax.annotation.Resource
 *  javax.ejb.EJB
 *  javax.xml.ws.Service
 *  javax.xml.ws.WebServiceClient
 *  javax.xml.ws.WebServiceRef
 */
package org.springframework.context.annotation;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceRef;
import org.springframework.aop.TargetSource;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.InitDestroyAnnotationBeanPostProcessor;
import org.springframework.beans.factory.annotation.InjectionMetadata;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.config.EmbeddedValueResolver;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.MethodParameter;
import org.springframework.jndi.support.SimpleJndiBeanFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.util.StringValueResolver;

public class CommonAnnotationBeanPostProcessor
extends InitDestroyAnnotationBeanPostProcessor
implements InstantiationAwareBeanPostProcessor,
BeanFactoryAware,
Serializable {
    @Nullable
    private static Class<? extends Annotation> webServiceRefClass;
    @Nullable
    private static Class<? extends Annotation> ejbRefClass;
    private final Set<String> ignoredResourceTypes = new HashSet<String>(1);
    private boolean fallbackToDefaultTypeMatch = true;
    private boolean alwaysUseJndiLookup = false;
    private transient BeanFactory jndiFactory = new SimpleJndiBeanFactory();
    @Nullable
    private transient BeanFactory resourceFactory;
    @Nullable
    private transient BeanFactory beanFactory;
    @Nullable
    private transient StringValueResolver embeddedValueResolver;
    private final transient Map<String, InjectionMetadata> injectionMetadataCache = new ConcurrentHashMap<String, InjectionMetadata>(256);

    public CommonAnnotationBeanPostProcessor() {
        this.setOrder(0x7FFFFFFC);
        this.setInitAnnotationType(PostConstruct.class);
        this.setDestroyAnnotationType(PreDestroy.class);
        this.ignoreResourceType("javax.xml.ws.WebServiceContext");
    }

    public void ignoreResourceType(String resourceType) {
        Assert.notNull((Object)resourceType, "Ignored resource type must not be null");
        this.ignoredResourceTypes.add(resourceType);
    }

    public void setFallbackToDefaultTypeMatch(boolean fallbackToDefaultTypeMatch) {
        this.fallbackToDefaultTypeMatch = fallbackToDefaultTypeMatch;
    }

    public void setAlwaysUseJndiLookup(boolean alwaysUseJndiLookup) {
        this.alwaysUseJndiLookup = alwaysUseJndiLookup;
    }

    public void setJndiFactory(BeanFactory jndiFactory) {
        Assert.notNull((Object)jndiFactory, "BeanFactory must not be null");
        this.jndiFactory = jndiFactory;
    }

    public void setResourceFactory(BeanFactory resourceFactory) {
        Assert.notNull((Object)resourceFactory, "BeanFactory must not be null");
        this.resourceFactory = resourceFactory;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        Assert.notNull((Object)beanFactory, "BeanFactory must not be null");
        this.beanFactory = beanFactory;
        if (this.resourceFactory == null) {
            this.resourceFactory = beanFactory;
        }
        if (beanFactory instanceof ConfigurableBeanFactory) {
            this.embeddedValueResolver = new EmbeddedValueResolver((ConfigurableBeanFactory)beanFactory);
        }
    }

    @Override
    public void postProcessMergedBeanDefinition(RootBeanDefinition beanDefinition, Class<?> beanType, String beanName) {
        super.postProcessMergedBeanDefinition(beanDefinition, beanType, beanName);
        InjectionMetadata metadata = this.findResourceMetadata(beanName, beanType, null);
        metadata.checkConfigMembers(beanDefinition);
    }

    @Override
    public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) {
        return null;
    }

    @Override
    public boolean postProcessAfterInstantiation(Object bean2, String beanName) {
        return true;
    }

    @Override
    public PropertyValues postProcessPropertyValues(PropertyValues pvs, PropertyDescriptor[] pds, Object bean2, String beanName) {
        InjectionMetadata metadata = this.findResourceMetadata(beanName, bean2.getClass(), pvs);
        try {
            metadata.inject(bean2, beanName, pvs);
        }
        catch (Throwable ex) {
            throw new BeanCreationException(beanName, "Injection of resource dependencies failed", ex);
        }
        return pvs;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private InjectionMetadata findResourceMetadata(String beanName, Class<?> clazz, @Nullable PropertyValues pvs) {
        String cacheKey = StringUtils.hasLength(beanName) ? beanName : clazz.getName();
        InjectionMetadata metadata = this.injectionMetadataCache.get(cacheKey);
        if (InjectionMetadata.needsRefresh(metadata, clazz)) {
            Map<String, InjectionMetadata> map = this.injectionMetadataCache;
            synchronized (map) {
                metadata = this.injectionMetadataCache.get(cacheKey);
                if (InjectionMetadata.needsRefresh(metadata, clazz)) {
                    if (metadata != null) {
                        metadata.clear(pvs);
                    }
                    metadata = this.buildResourceMetadata(clazz);
                    this.injectionMetadataCache.put(cacheKey, metadata);
                }
            }
        }
        return metadata;
    }

    private InjectionMetadata buildResourceMetadata(Class<?> clazz) {
        ArrayList<InjectionMetadata.InjectedElement> elements = new ArrayList<InjectionMetadata.InjectedElement>();
        Class<?> targetClass = clazz;
        do {
            ArrayList currElements = new ArrayList();
            ReflectionUtils.doWithLocalFields(targetClass, field -> {
                if (webServiceRefClass != null && field.isAnnotationPresent(webServiceRefClass)) {
                    if (Modifier.isStatic(field.getModifiers())) {
                        throw new IllegalStateException("@WebServiceRef annotation is not supported on static fields");
                    }
                    currElements.add(new WebServiceRefElement(field, field, null));
                } else if (ejbRefClass != null && field.isAnnotationPresent(ejbRefClass)) {
                    if (Modifier.isStatic(field.getModifiers())) {
                        throw new IllegalStateException("@EJB annotation is not supported on static fields");
                    }
                    currElements.add(new EjbRefElement(field, field, null));
                } else if (field.isAnnotationPresent(Resource.class)) {
                    if (Modifier.isStatic(field.getModifiers())) {
                        throw new IllegalStateException("@Resource annotation is not supported on static fields");
                    }
                    if (!this.ignoredResourceTypes.contains(field.getType().getName())) {
                        currElements.add(new ResourceElement(field, field, null));
                    }
                }
            });
            ReflectionUtils.doWithLocalMethods(targetClass, method -> {
                Method bridgedMethod = BridgeMethodResolver.findBridgedMethod(method);
                if (!BridgeMethodResolver.isVisibilityBridgeMethodPair(method, bridgedMethod)) {
                    return;
                }
                if (method.equals(ClassUtils.getMostSpecificMethod(method, clazz))) {
                    if (webServiceRefClass != null && bridgedMethod.isAnnotationPresent(webServiceRefClass)) {
                        if (Modifier.isStatic(method.getModifiers())) {
                            throw new IllegalStateException("@WebServiceRef annotation is not supported on static methods");
                        }
                        if (method.getParameterCount() != 1) {
                            throw new IllegalStateException("@WebServiceRef annotation requires a single-arg method: " + method);
                        }
                        PropertyDescriptor pd = BeanUtils.findPropertyForMethod(bridgedMethod, clazz);
                        currElements.add(new WebServiceRefElement(method, bridgedMethod, pd));
                    } else if (ejbRefClass != null && bridgedMethod.isAnnotationPresent(ejbRefClass)) {
                        if (Modifier.isStatic(method.getModifiers())) {
                            throw new IllegalStateException("@EJB annotation is not supported on static methods");
                        }
                        if (method.getParameterCount() != 1) {
                            throw new IllegalStateException("@EJB annotation requires a single-arg method: " + method);
                        }
                        PropertyDescriptor pd = BeanUtils.findPropertyForMethod(bridgedMethod, clazz);
                        currElements.add(new EjbRefElement(method, bridgedMethod, pd));
                    } else if (bridgedMethod.isAnnotationPresent(Resource.class)) {
                        if (Modifier.isStatic(method.getModifiers())) {
                            throw new IllegalStateException("@Resource annotation is not supported on static methods");
                        }
                        Class<?>[] paramTypes = method.getParameterTypes();
                        if (paramTypes.length != 1) {
                            throw new IllegalStateException("@Resource annotation requires a single-arg method: " + method);
                        }
                        if (!this.ignoredResourceTypes.contains(paramTypes[0].getName())) {
                            PropertyDescriptor pd = BeanUtils.findPropertyForMethod(bridgedMethod, clazz);
                            currElements.add(new ResourceElement(method, bridgedMethod, pd));
                        }
                    }
                }
            });
            elements.addAll(0, currElements);
        } while ((targetClass = targetClass.getSuperclass()) != null && targetClass != Object.class);
        return new InjectionMetadata(clazz, elements);
    }

    protected Object buildLazyResourceProxy(final LookupElement element, final @Nullable String requestingBeanName) {
        TargetSource ts = new TargetSource(){

            @Override
            public Class<?> getTargetClass() {
                return element.lookupType;
            }

            @Override
            public boolean isStatic() {
                return false;
            }

            @Override
            public Object getTarget() {
                return CommonAnnotationBeanPostProcessor.this.getResource(element, requestingBeanName);
            }

            @Override
            public void releaseTarget(Object target) {
            }
        };
        ProxyFactory pf = new ProxyFactory();
        pf.setTargetSource(ts);
        if (element.lookupType.isInterface()) {
            pf.addInterface(element.lookupType);
        }
        ClassLoader classLoader = this.beanFactory instanceof ConfigurableBeanFactory ? ((ConfigurableBeanFactory)this.beanFactory).getBeanClassLoader() : null;
        return pf.getProxy(classLoader);
    }

    protected Object getResource(LookupElement element, @Nullable String requestingBeanName) throws NoSuchBeanDefinitionException {
        if (StringUtils.hasLength(element.mappedName)) {
            return this.jndiFactory.getBean(element.mappedName, element.lookupType);
        }
        if (this.alwaysUseJndiLookup) {
            return this.jndiFactory.getBean(element.name, element.lookupType);
        }
        if (this.resourceFactory == null) {
            throw new NoSuchBeanDefinitionException(element.lookupType, "No resource factory configured - specify the 'resourceFactory' property");
        }
        return this.autowireResource(this.resourceFactory, element, requestingBeanName);
    }

    protected Object autowireResource(BeanFactory factory, LookupElement element, @Nullable String requestingBeanName) throws NoSuchBeanDefinitionException {
        Object resource;
        Set<String> autowiredBeanNames;
        String name = element.name;
        if (this.fallbackToDefaultTypeMatch && element.isDefaultName && factory instanceof AutowireCapableBeanFactory && !factory.containsBean(name)) {
            autowiredBeanNames = new LinkedHashSet<String>();
            resource = ((AutowireCapableBeanFactory)factory).resolveDependency(element.getDependencyDescriptor(), requestingBeanName, autowiredBeanNames, null);
            if (resource == null) {
                throw new NoSuchBeanDefinitionException(element.getLookupType(), "No resolvable resource object");
            }
        } else {
            resource = factory.getBean(name, element.lookupType);
            autowiredBeanNames = Collections.singleton(name);
        }
        if (factory instanceof ConfigurableBeanFactory) {
            ConfigurableBeanFactory beanFactory = (ConfigurableBeanFactory)factory;
            for (String autowiredBeanName : autowiredBeanNames) {
                if (requestingBeanName == null || !beanFactory.containsBean(autowiredBeanName)) continue;
                beanFactory.registerDependentBean(autowiredBeanName, requestingBeanName);
            }
        }
        return resource;
    }

    static {
        Class<?> clazz;
        try {
            clazz = ClassUtils.forName("javax.xml.ws.WebServiceRef", CommonAnnotationBeanPostProcessor.class.getClassLoader());
            webServiceRefClass = clazz;
        }
        catch (ClassNotFoundException ex) {
            webServiceRefClass = null;
        }
        try {
            clazz = ClassUtils.forName("javax.ejb.EJB", CommonAnnotationBeanPostProcessor.class.getClassLoader());
            ejbRefClass = clazz;
        }
        catch (ClassNotFoundException ex) {
            ejbRefClass = null;
        }
    }

    private static class LookupDependencyDescriptor
    extends DependencyDescriptor {
        private final Class<?> lookupType;

        public LookupDependencyDescriptor(Field field, Class<?> lookupType) {
            super(field, true);
            this.lookupType = lookupType;
        }

        public LookupDependencyDescriptor(Method method, Class<?> lookupType) {
            super(new MethodParameter(method, 0), true);
            this.lookupType = lookupType;
        }

        @Override
        public Class<?> getDependencyType() {
            return this.lookupType;
        }
    }

    private class EjbRefElement
    extends LookupElement {
        private final String beanName;

        public EjbRefElement(Member member, @Nullable AnnotatedElement ae, PropertyDescriptor pd) {
            Class<?> resourceType;
            super(member, pd);
            EJB resource = ae.getAnnotation(EJB.class);
            String resourceBeanName = resource.beanName();
            String resourceName = resource.name();
            boolean bl = this.isDefaultName = !StringUtils.hasLength(resourceName);
            if (this.isDefaultName) {
                resourceName = this.member.getName();
                if (this.member instanceof Method && resourceName.startsWith("set") && resourceName.length() > 3) {
                    resourceName = Introspector.decapitalize(resourceName.substring(3));
                }
            }
            if (Object.class != (resourceType = resource.beanInterface())) {
                this.checkResourceType(resourceType);
            } else {
                resourceType = this.getResourceType();
            }
            this.beanName = resourceBeanName;
            this.name = resourceName;
            this.lookupType = resourceType;
            this.mappedName = resource.mappedName();
        }

        @Override
        protected Object getResourceToInject(Object target, @Nullable String requestingBeanName) {
            if (StringUtils.hasLength(this.beanName)) {
                if (CommonAnnotationBeanPostProcessor.this.beanFactory != null && CommonAnnotationBeanPostProcessor.this.beanFactory.containsBean(this.beanName)) {
                    Object bean2 = CommonAnnotationBeanPostProcessor.this.beanFactory.getBean(this.beanName, this.lookupType);
                    if (requestingBeanName != null && CommonAnnotationBeanPostProcessor.this.beanFactory instanceof ConfigurableBeanFactory) {
                        ((ConfigurableBeanFactory)CommonAnnotationBeanPostProcessor.this.beanFactory).registerDependentBean(this.beanName, requestingBeanName);
                    }
                    return bean2;
                }
                if (this.isDefaultName && !StringUtils.hasLength(this.mappedName)) {
                    throw new NoSuchBeanDefinitionException(this.beanName, "Cannot resolve 'beanName' in local BeanFactory. Consider specifying a general 'name' value instead.");
                }
            }
            return CommonAnnotationBeanPostProcessor.this.getResource(this, requestingBeanName);
        }
    }

    private class WebServiceRefElement
    extends LookupElement {
        private final Class<?> elementType;
        private final String wsdlLocation;

        public WebServiceRefElement(Member member, @Nullable AnnotatedElement ae, PropertyDescriptor pd) {
            super(member, pd);
            WebServiceRef resource = ae.getAnnotation(WebServiceRef.class);
            String resourceName = resource.name();
            Class<?> resourceType = resource.type();
            boolean bl = this.isDefaultName = !StringUtils.hasLength(resourceName);
            if (this.isDefaultName) {
                resourceName = this.member.getName();
                if (this.member instanceof Method && resourceName.startsWith("set") && resourceName.length() > 3) {
                    resourceName = Introspector.decapitalize(resourceName.substring(3));
                }
            }
            if (Object.class != resourceType) {
                this.checkResourceType(resourceType);
            } else {
                resourceType = this.getResourceType();
            }
            this.name = resourceName;
            this.elementType = resourceType;
            this.lookupType = Service.class.isAssignableFrom(resourceType) ? resourceType : resource.value();
            this.mappedName = resource.mappedName();
            this.wsdlLocation = resource.wsdlLocation();
        }

        @Override
        protected Object getResourceToInject(Object target, @Nullable String requestingBeanName) {
            Service service;
            try {
                service = (Service)CommonAnnotationBeanPostProcessor.this.getResource(this, requestingBeanName);
            }
            catch (NoSuchBeanDefinitionException notFound) {
                if (Service.class == this.lookupType) {
                    throw new IllegalStateException("No resource with name '" + this.name + "' found in context, and no specific JAX-WS Service subclass specified. The typical solution is to either specify a LocalJaxWsServiceFactoryBean with the given name or to specify the (generated) Service subclass as @WebServiceRef(...) value.");
                }
                if (StringUtils.hasLength(this.wsdlLocation)) {
                    try {
                        Constructor ctor = this.lookupType.getConstructor(URL.class, QName.class);
                        WebServiceClient clientAnn = this.lookupType.getAnnotation(WebServiceClient.class);
                        if (clientAnn == null) {
                            throw new IllegalStateException("JAX-WS Service class [" + this.lookupType.getName() + "] does not carry a WebServiceClient annotation");
                        }
                        service = (Service)BeanUtils.instantiateClass(ctor, new URL(this.wsdlLocation), new QName(clientAnn.targetNamespace(), clientAnn.name()));
                    }
                    catch (NoSuchMethodException ex) {
                        throw new IllegalStateException("JAX-WS Service class [" + this.lookupType.getName() + "] does not have a (URL, QName) constructor. Cannot apply specified WSDL location [" + this.wsdlLocation + "].");
                    }
                    catch (MalformedURLException ex) {
                        throw new IllegalArgumentException("Specified WSDL location [" + this.wsdlLocation + "] isn't a valid URL");
                    }
                }
                service = (Service)BeanUtils.instantiateClass(this.lookupType);
            }
            return service.getPort(this.elementType);
        }
    }

    private class ResourceElement
    extends LookupElement {
        private final boolean lazyLookup;

        public ResourceElement(Member member, @Nullable AnnotatedElement ae, PropertyDescriptor pd) {
            super(member, pd);
            Resource resource = ae.getAnnotation(Resource.class);
            String resourceName = resource.name();
            Class<?> resourceType = resource.type();
            boolean bl = this.isDefaultName = !StringUtils.hasLength(resourceName);
            if (this.isDefaultName) {
                resourceName = this.member.getName();
                if (this.member instanceof Method && resourceName.startsWith("set") && resourceName.length() > 3) {
                    resourceName = Introspector.decapitalize(resourceName.substring(3));
                }
            } else if (CommonAnnotationBeanPostProcessor.this.embeddedValueResolver != null) {
                resourceName = CommonAnnotationBeanPostProcessor.this.embeddedValueResolver.resolveStringValue(resourceName);
            }
            if (Object.class != resourceType) {
                this.checkResourceType(resourceType);
            } else {
                resourceType = this.getResourceType();
            }
            this.name = resourceName != null ? resourceName : "";
            this.lookupType = resourceType;
            String lookupValue = resource.lookup();
            this.mappedName = StringUtils.hasLength(lookupValue) ? lookupValue : resource.mappedName();
            Lazy lazy = ae.getAnnotation(Lazy.class);
            this.lazyLookup = lazy != null && lazy.value();
        }

        @Override
        protected Object getResourceToInject(Object target, @Nullable String requestingBeanName) {
            return this.lazyLookup ? CommonAnnotationBeanPostProcessor.this.buildLazyResourceProxy(this, requestingBeanName) : CommonAnnotationBeanPostProcessor.this.getResource(this, requestingBeanName);
        }
    }

    protected abstract class LookupElement
    extends InjectionMetadata.InjectedElement {
        protected String name;
        protected boolean isDefaultName;
        protected Class<?> lookupType;
        @Nullable
        protected String mappedName;

        public LookupElement(@Nullable Member member, PropertyDescriptor pd) {
            super(member, pd);
            this.name = "";
            this.isDefaultName = false;
            this.lookupType = Object.class;
        }

        public final String getName() {
            return this.name;
        }

        public final Class<?> getLookupType() {
            return this.lookupType;
        }

        public final DependencyDescriptor getDependencyDescriptor() {
            if (this.isField) {
                return new LookupDependencyDescriptor((Field)this.member, this.lookupType);
            }
            return new LookupDependencyDescriptor((Method)this.member, this.lookupType);
        }
    }
}

