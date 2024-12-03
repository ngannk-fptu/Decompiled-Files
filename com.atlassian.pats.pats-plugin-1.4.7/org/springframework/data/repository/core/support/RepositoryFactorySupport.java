/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.aopalliance.aop.Advice
 *  org.aopalliance.intercept.MethodInterceptor
 *  org.aopalliance.intercept.MethodInvocation
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.aop.framework.ProxyFactory
 *  org.springframework.aop.interceptor.ExposeInvocationInterceptor
 *  org.springframework.beans.BeanUtils
 *  org.springframework.beans.BeansException
 *  org.springframework.beans.factory.BeanClassLoaderAware
 *  org.springframework.beans.factory.BeanFactory
 *  org.springframework.beans.factory.BeanFactoryAware
 *  org.springframework.beans.factory.NoSuchBeanDefinitionException
 *  org.springframework.core.convert.support.ConfigurableConversionService
 *  org.springframework.core.convert.support.DefaultConversionService
 *  org.springframework.core.convert.support.GenericConversionService
 *  org.springframework.core.log.LogMessage
 *  org.springframework.core.metrics.ApplicationStartup
 *  org.springframework.core.metrics.StartupStep
 *  org.springframework.lang.Nullable
 *  org.springframework.transaction.interceptor.TransactionalProxy
 *  org.springframework.util.Assert
 *  org.springframework.util.ClassUtils
 *  org.springframework.util.ConcurrentReferenceHashMap
 *  org.springframework.util.ConcurrentReferenceHashMap$ReferenceType
 *  org.springframework.util.ObjectUtils
 */
package org.springframework.data.repository.core.support;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.interceptor.ExposeInvocationInterceptor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.core.convert.support.ConfigurableConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.core.log.LogMessage;
import org.springframework.core.metrics.ApplicationStartup;
import org.springframework.core.metrics.StartupStep;
import org.springframework.data.projection.DefaultMethodInvokingMethodInterceptor;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.projection.SpelAwareProxyProjectionFactory;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.core.EntityInformation;
import org.springframework.data.repository.core.NamedQueries;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.AbstractRepositoryMetadata;
import org.springframework.data.repository.core.support.DefaultRepositoryInformation;
import org.springframework.data.repository.core.support.IncompleteRepositoryCompositionException;
import org.springframework.data.repository.core.support.MethodInvocationValidator;
import org.springframework.data.repository.core.support.PropertiesBasedNamedQueries;
import org.springframework.data.repository.core.support.QueryCreationListener;
import org.springframework.data.repository.core.support.QueryExecutorMethodInterceptor;
import org.springframework.data.repository.core.support.RepositoryComposition;
import org.springframework.data.repository.core.support.RepositoryFragment;
import org.springframework.data.repository.core.support.RepositoryInvocationMulticaster;
import org.springframework.data.repository.core.support.RepositoryMethodInvocationListener;
import org.springframework.data.repository.core.support.RepositoryProxyPostProcessor;
import org.springframework.data.repository.core.support.UnsupportedFragmentException;
import org.springframework.data.repository.query.QueryLookupStrategy;
import org.springframework.data.repository.query.QueryMethod;
import org.springframework.data.repository.query.QueryMethodEvaluationContextProvider;
import org.springframework.data.repository.query.RepositoryQuery;
import org.springframework.data.repository.util.ClassUtils;
import org.springframework.data.repository.util.QueryExecutionConverters;
import org.springframework.data.util.ReflectionUtils;
import org.springframework.lang.Nullable;
import org.springframework.transaction.interceptor.TransactionalProxy;
import org.springframework.util.Assert;
import org.springframework.util.ConcurrentReferenceHashMap;
import org.springframework.util.ObjectUtils;

public abstract class RepositoryFactorySupport
implements BeanClassLoaderAware,
BeanFactoryAware {
    static final GenericConversionService CONVERSION_SERVICE = new DefaultConversionService();
    private static final Log logger = LogFactory.getLog(RepositoryFactorySupport.class);
    private final Map<RepositoryInformationCacheKey, RepositoryInformation> repositoryInformationCache;
    private final List<RepositoryProxyPostProcessor> postProcessors;
    private Optional<Class<?>> repositoryBaseClass;
    @Nullable
    private QueryLookupStrategy.Key queryLookupStrategyKey;
    private List<QueryCreationListener<?>> queryPostProcessors;
    private List<RepositoryMethodInvocationListener> methodInvocationListeners;
    private NamedQueries namedQueries;
    private ClassLoader classLoader;
    private QueryMethodEvaluationContextProvider evaluationContextProvider;
    private BeanFactory beanFactory;
    private final QueryCollectingQueryCreationListener collectingListener = new QueryCollectingQueryCreationListener();

    public RepositoryFactorySupport() {
        this.repositoryInformationCache = new ConcurrentReferenceHashMap(16, ConcurrentReferenceHashMap.ReferenceType.WEAK);
        this.postProcessors = new ArrayList<RepositoryProxyPostProcessor>();
        this.repositoryBaseClass = Optional.empty();
        this.namedQueries = PropertiesBasedNamedQueries.EMPTY;
        this.classLoader = org.springframework.util.ClassUtils.getDefaultClassLoader();
        this.evaluationContextProvider = QueryMethodEvaluationContextProvider.DEFAULT;
        this.queryPostProcessors = new ArrayList();
        this.queryPostProcessors.add(this.collectingListener);
        this.methodInvocationListeners = new ArrayList<RepositoryMethodInvocationListener>();
    }

    public void setQueryLookupStrategyKey(QueryLookupStrategy.Key key) {
        this.queryLookupStrategyKey = key;
    }

    public void setNamedQueries(NamedQueries namedQueries) {
        this.namedQueries = namedQueries == null ? PropertiesBasedNamedQueries.EMPTY : namedQueries;
    }

    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader == null ? org.springframework.util.ClassUtils.getDefaultClassLoader() : classLoader;
    }

    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    public void setEvaluationContextProvider(QueryMethodEvaluationContextProvider evaluationContextProvider) {
        this.evaluationContextProvider = evaluationContextProvider == null ? QueryMethodEvaluationContextProvider.DEFAULT : evaluationContextProvider;
    }

    public void setRepositoryBaseClass(Class<?> repositoryBaseClass) {
        this.repositoryBaseClass = Optional.ofNullable(repositoryBaseClass);
    }

    public void addQueryCreationListener(QueryCreationListener<?> listener) {
        Assert.notNull(listener, (String)"Listener must not be null!");
        this.queryPostProcessors.add(listener);
    }

    public void addInvocationListener(RepositoryMethodInvocationListener listener) {
        Assert.notNull((Object)listener, (String)"Listener must not be null!");
        this.methodInvocationListeners.add(listener);
    }

    public void addRepositoryProxyPostProcessor(RepositoryProxyPostProcessor processor) {
        Assert.notNull((Object)processor, (String)"RepositoryProxyPostProcessor must not be null!");
        this.postProcessors.add(processor);
    }

    protected RepositoryComposition.RepositoryFragments getRepositoryFragments(RepositoryMetadata metadata) {
        return RepositoryComposition.RepositoryFragments.empty();
    }

    private RepositoryComposition getRepositoryComposition(RepositoryMetadata metadata) {
        return RepositoryComposition.fromMetadata(metadata);
    }

    public <T> T getRepository(Class<T> repositoryInterface) {
        return this.getRepository(repositoryInterface, RepositoryComposition.RepositoryFragments.empty());
    }

    public <T> T getRepository(Class<T> repositoryInterface, Object customImplementation) {
        return this.getRepository(repositoryInterface, RepositoryComposition.RepositoryFragments.just(customImplementation));
    }

    public <T> T getRepository(Class<T> repositoryInterface, RepositoryComposition.RepositoryFragments fragments) {
        if (logger.isDebugEnabled()) {
            logger.debug((Object)LogMessage.format((String)"Initializing repository instance for %s\u2026", (Object)repositoryInterface.getName()));
        }
        Assert.notNull(repositoryInterface, (String)"Repository interface must not be null!");
        Assert.notNull((Object)fragments, (String)"RepositoryFragments must not be null!");
        ApplicationStartup applicationStartup = this.getStartup();
        StartupStep repositoryInit = this.onEvent(applicationStartup, "spring.data.repository.init", repositoryInterface);
        this.repositoryBaseClass.ifPresent(it -> repositoryInit.tag("baseClass", it.getName()));
        StartupStep repositoryMetadataStep = this.onEvent(applicationStartup, "spring.data.repository.metadata", repositoryInterface);
        RepositoryMetadata metadata = this.getRepositoryMetadata(repositoryInterface);
        repositoryMetadataStep.end();
        StartupStep repositoryCompositionStep = this.onEvent(applicationStartup, "spring.data.repository.composition", repositoryInterface);
        repositoryCompositionStep.tag("fragment.count", String.valueOf(fragments.size()));
        RepositoryComposition composition = this.getRepositoryComposition(metadata, fragments);
        RepositoryInformation information = this.getRepositoryInformation(metadata, composition);
        repositoryCompositionStep.tag("fragments", () -> {
            StringBuilder fragmentsTag = new StringBuilder();
            for (RepositoryFragment<?> fragment : composition.getFragments()) {
                if (fragmentsTag.length() > 0) {
                    fragmentsTag.append(";");
                }
                fragmentsTag.append(fragment.getSignatureContributor().getName());
                fragmentsTag.append(fragment.getImplementation().map(it -> ":" + it.getClass().getName()).orElse(""));
            }
            return fragmentsTag.toString();
        });
        repositoryCompositionStep.end();
        StartupStep repositoryTargetStep = this.onEvent(applicationStartup, "spring.data.repository.target", repositoryInterface);
        Object target = this.getTargetRepository(information);
        repositoryTargetStep.tag("target", target.getClass().getName());
        repositoryTargetStep.end();
        RepositoryComposition compositionToUse = composition.append(RepositoryFragment.implemented(target));
        this.validate(information, compositionToUse);
        StartupStep repositoryProxyStep = this.onEvent(applicationStartup, "spring.data.repository.proxy", repositoryInterface);
        ProxyFactory result = new ProxyFactory();
        result.setTarget(target);
        result.setInterfaces(new Class[]{repositoryInterface, Repository.class, TransactionalProxy.class});
        if (MethodInvocationValidator.supports(repositoryInterface)) {
            result.addAdvice((Advice)new MethodInvocationValidator());
        }
        result.addAdvisor(ExposeInvocationInterceptor.ADVISOR);
        if (!this.postProcessors.isEmpty()) {
            StartupStep repositoryPostprocessorsStep = this.onEvent(applicationStartup, "spring.data.repository.postprocessors", repositoryInterface);
            this.postProcessors.forEach(processor -> {
                StartupStep singlePostProcessor = this.onEvent(applicationStartup, "spring.data.repository.postprocessor", repositoryInterface);
                singlePostProcessor.tag("type", processor.getClass().getName());
                processor.postProcess(result, information);
                singlePostProcessor.end();
            });
            repositoryPostprocessorsStep.end();
        }
        if (DefaultMethodInvokingMethodInterceptor.hasDefaultMethods(repositoryInterface)) {
            result.addAdvice((Advice)new DefaultMethodInvokingMethodInterceptor());
        }
        ProjectionFactory projectionFactory = this.getProjectionFactory(this.classLoader, this.beanFactory);
        Optional<QueryLookupStrategy> queryLookupStrategy = this.getQueryLookupStrategy(this.queryLookupStrategyKey, this.evaluationContextProvider);
        result.addAdvice((Advice)new QueryExecutorMethodInterceptor(information, projectionFactory, queryLookupStrategy, this.namedQueries, this.queryPostProcessors, this.methodInvocationListeners));
        result.addAdvice((Advice)new ImplementationMethodExecutionInterceptor(information, compositionToUse, this.methodInvocationListeners));
        Object repository = result.getProxy(this.classLoader);
        repositoryProxyStep.end();
        repositoryInit.end();
        if (logger.isDebugEnabled()) {
            logger.debug((Object)LogMessage.format((String)"Finished creation of repository instance for %s.", (Object)repositoryInterface.getName()));
        }
        return (T)repository;
    }

    protected ProjectionFactory getProjectionFactory(ClassLoader classLoader, BeanFactory beanFactory) {
        SpelAwareProxyProjectionFactory factory = new SpelAwareProxyProjectionFactory();
        factory.setBeanClassLoader(classLoader);
        factory.setBeanFactory(beanFactory);
        return factory;
    }

    protected RepositoryMetadata getRepositoryMetadata(Class<?> repositoryInterface) {
        return AbstractRepositoryMetadata.getMetadata(repositoryInterface);
    }

    protected RepositoryInformation getRepositoryInformation(RepositoryMetadata metadata, RepositoryComposition.RepositoryFragments fragments) {
        return this.getRepositoryInformation(metadata, this.getRepositoryComposition(metadata, fragments));
    }

    private RepositoryComposition getRepositoryComposition(RepositoryMetadata metadata, RepositoryComposition.RepositoryFragments fragments) {
        Assert.notNull((Object)metadata, (String)"RepositoryMetadata must not be null!");
        Assert.notNull((Object)fragments, (String)"RepositoryFragments must not be null!");
        RepositoryComposition composition = this.getRepositoryComposition(metadata);
        RepositoryComposition.RepositoryFragments repositoryAspects = this.getRepositoryFragments(metadata);
        return composition.append(fragments).append(repositoryAspects);
    }

    private RepositoryInformation getRepositoryInformation(RepositoryMetadata metadata, RepositoryComposition composition) {
        RepositoryInformationCacheKey cacheKey = new RepositoryInformationCacheKey(metadata, composition);
        return this.repositoryInformationCache.computeIfAbsent(cacheKey, key -> {
            Class<?> baseClass = this.repositoryBaseClass.orElse(this.getRepositoryBaseClass(metadata));
            return new DefaultRepositoryInformation(metadata, baseClass, composition);
        });
    }

    protected List<QueryMethod> getQueryMethods() {
        return this.collectingListener.getQueryMethods();
    }

    public abstract <T, ID> EntityInformation<T, ID> getEntityInformation(Class<T> var1);

    protected abstract Object getTargetRepository(RepositoryInformation var1);

    protected abstract Class<?> getRepositoryBaseClass(RepositoryMetadata var1);

    protected Optional<QueryLookupStrategy> getQueryLookupStrategy(@Nullable QueryLookupStrategy.Key key, QueryMethodEvaluationContextProvider evaluationContextProvider) {
        return Optional.empty();
    }

    private void validate(RepositoryInformation repositoryInformation, RepositoryComposition composition) {
        RepositoryValidator.validate(composition, this.getClass(), repositoryInformation);
        this.validate(repositoryInformation);
    }

    protected void validate(RepositoryMetadata repositoryMetadata) {
    }

    protected final <R> R getTargetRepositoryViaReflection(RepositoryInformation information, Object ... constructorArguments) {
        Class<?> baseClass = information.getRepositoryBaseClass();
        return this.getTargetRepositoryViaReflection(baseClass, constructorArguments);
    }

    protected final <R> R getTargetRepositoryViaReflection(Class<?> baseClass, Object ... constructorArguments) {
        Optional<Constructor<?>> constructor = ReflectionUtils.findConstructor(baseClass, constructorArguments);
        return (R)constructor.map(it -> BeanUtils.instantiateClass((Constructor)it, (Object[])constructorArguments)).orElseThrow(() -> new IllegalStateException(String.format("No suitable constructor found on %s to match the given arguments: %s. Make sure you implement a constructor taking these", baseClass, Arrays.stream(constructorArguments).map(Object::getClass).collect(Collectors.toList()))));
    }

    private ApplicationStartup getStartup() {
        try {
            ApplicationStartup applicationStartup = this.beanFactory != null ? (ApplicationStartup)this.beanFactory.getBean(ApplicationStartup.class) : ApplicationStartup.DEFAULT;
            return applicationStartup != null ? applicationStartup : ApplicationStartup.DEFAULT;
        }
        catch (NoSuchBeanDefinitionException e) {
            return ApplicationStartup.DEFAULT;
        }
    }

    private StartupStep onEvent(ApplicationStartup applicationStartup, String name, Class<?> repositoryInterface) {
        StartupStep step = applicationStartup.start(name);
        return step.tag("repository", repositoryInterface.getName());
    }

    static {
        QueryExecutionConverters.registerConvertersIn((ConfigurableConversionService)CONVERSION_SERVICE);
        CONVERSION_SERVICE.removeConvertible(Object.class, Object.class);
    }

    static class RepositoryValidator {
        static Map<Class<?>, String> WELL_KNOWN_EXECUTORS = new HashMap();

        RepositoryValidator() {
        }

        public static void validate(RepositoryComposition composition, Class<?> source, RepositoryInformation repositoryInformation) {
            Class<?> repositoryInterface = repositoryInformation.getRepositoryInterface();
            if (repositoryInformation.hasCustomMethod()) {
                if (composition.isEmpty()) {
                    throw new IncompleteRepositoryCompositionException(String.format("You have custom methods in %s but have not provided a custom implementation!", org.springframework.util.ClassUtils.getQualifiedName(repositoryInterface)), repositoryInterface);
                }
                composition.validateImplementation();
            }
            for (Map.Entry<Class<?>, String> entry : WELL_KNOWN_EXECUTORS.entrySet()) {
                Class<?> executorInterface = entry.getKey();
                if (!executorInterface.isAssignableFrom(repositoryInterface) || RepositoryValidator.containsFragmentImplementation(composition, executorInterface)) continue;
                throw new UnsupportedFragmentException(String.format("Repository %s implements %s but %s does not support %s!", org.springframework.util.ClassUtils.getQualifiedName(repositoryInterface), org.springframework.util.ClassUtils.getQualifiedName(executorInterface), org.springframework.util.ClassUtils.getShortName(source), entry.getValue()), repositoryInterface, executorInterface);
            }
        }

        private static boolean containsFragmentImplementation(RepositoryComposition composition, Class<?> executorInterface) {
            for (RepositoryFragment<?> fragment : composition.getFragments()) {
                if (!fragment.getImplementation().filter(executorInterface::isInstance).isPresent()) continue;
                return true;
            }
            return false;
        }

        static {
            ClassUtils.ifPresent("org.springframework.data.querydsl.QuerydslPredicateExecutor", RepositoryValidator.class.getClassLoader(), it -> WELL_KNOWN_EXECUTORS.put((Class<?>)it, "Querydsl"));
            ClassUtils.ifPresent("org.springframework.data.querydsl.ReactiveQuerydslPredicateExecutor", RepositoryValidator.class.getClassLoader(), it -> WELL_KNOWN_EXECUTORS.put((Class<?>)it, "Reactive Querydsl"));
            ClassUtils.ifPresent("org.springframework.data.repository.query.QueryByExampleExecutor", RepositoryValidator.class.getClassLoader(), it -> WELL_KNOWN_EXECUTORS.put((Class<?>)it, "Query by Example"));
            ClassUtils.ifPresent("org.springframework.data.repository.query.ReactiveQueryByExampleExecutor", RepositoryValidator.class.getClassLoader(), it -> WELL_KNOWN_EXECUTORS.put((Class<?>)it, "Reactive Query by Example"));
        }
    }

    private static final class RepositoryInformationCacheKey {
        private final String repositoryInterfaceName;
        private final long compositionHash;

        public RepositoryInformationCacheKey(RepositoryMetadata metadata, RepositoryComposition composition) {
            this.repositoryInterfaceName = metadata.getRepositoryInterface().getName();
            this.compositionHash = composition.hashCode();
        }

        public RepositoryInformationCacheKey(String repositoryInterfaceName, long compositionHash) {
            this.repositoryInterfaceName = repositoryInterfaceName;
            this.compositionHash = compositionHash;
        }

        public String getRepositoryInterfaceName() {
            return this.repositoryInterfaceName;
        }

        public long getCompositionHash() {
            return this.compositionHash;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof RepositoryInformationCacheKey)) {
                return false;
            }
            RepositoryInformationCacheKey that = (RepositoryInformationCacheKey)o;
            if (this.compositionHash != that.compositionHash) {
                return false;
            }
            return ObjectUtils.nullSafeEquals((Object)this.repositoryInterfaceName, (Object)that.repositoryInterfaceName);
        }

        public int hashCode() {
            int result = ObjectUtils.nullSafeHashCode((Object)this.repositoryInterfaceName);
            result = 31 * result + (int)(this.compositionHash ^ this.compositionHash >>> 32);
            return result;
        }

        public String toString() {
            return "RepositoryFactorySupport.RepositoryInformationCacheKey(repositoryInterfaceName=" + this.getRepositoryInterfaceName() + ", compositionHash=" + this.getCompositionHash() + ")";
        }
    }

    private static class QueryCollectingQueryCreationListener
    implements QueryCreationListener<RepositoryQuery> {
        private final List<QueryMethod> queryMethods = new ArrayList<QueryMethod>();

        private QueryCollectingQueryCreationListener() {
        }

        @Override
        public void onCreation(RepositoryQuery query) {
            this.queryMethods.add(query.getQueryMethod());
        }

        public List<QueryMethod> getQueryMethods() {
            return this.queryMethods;
        }
    }

    static class ImplementationMethodExecutionInterceptor
    implements MethodInterceptor {
        private final RepositoryInformation information;
        private final RepositoryComposition composition;
        private final RepositoryInvocationMulticaster invocationMulticaster;

        public ImplementationMethodExecutionInterceptor(RepositoryInformation information, RepositoryComposition composition, List<RepositoryMethodInvocationListener> methodInvocationListeners) {
            this.information = information;
            this.composition = composition;
            this.invocationMulticaster = methodInvocationListeners.isEmpty() ? RepositoryInvocationMulticaster.NoOpRepositoryInvocationMulticaster.INSTANCE : new RepositoryInvocationMulticaster.DefaultRepositoryInvocationMulticaster(methodInvocationListeners);
        }

        @Nullable
        public Object invoke(MethodInvocation invocation) throws Throwable {
            Method method = invocation.getMethod();
            Object[] arguments = invocation.getArguments();
            try {
                return this.composition.invoke(this.invocationMulticaster, method, arguments);
            }
            catch (Exception e) {
                ClassUtils.unwrapReflectionException(e);
                throw new IllegalStateException("Should not occur!");
            }
        }
    }
}

