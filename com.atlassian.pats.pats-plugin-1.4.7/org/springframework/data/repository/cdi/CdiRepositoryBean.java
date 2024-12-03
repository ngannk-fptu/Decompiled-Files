/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.enterprise.context.ApplicationScoped
 *  javax.enterprise.context.spi.Contextual
 *  javax.enterprise.context.spi.CreationalContext
 *  javax.enterprise.inject.Alternative
 *  javax.enterprise.inject.Stereotype
 *  javax.enterprise.inject.spi.Bean
 *  javax.enterprise.inject.spi.BeanManager
 *  javax.enterprise.inject.spi.InjectionPoint
 *  javax.enterprise.inject.spi.PassivationCapable
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.core.log.LogMessage
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.StringUtils
 */
package org.springframework.data.repository.cdi;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.spi.Contextual;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Stereotype;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.PassivationCapable;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.log.LogMessage;
import org.springframework.data.repository.cdi.CdiRepositoryConfiguration;
import org.springframework.data.repository.cdi.CdiRepositoryContext;
import org.springframework.data.repository.config.CustomRepositoryImplementationDetector;
import org.springframework.data.repository.config.RepositoryFragmentConfiguration;
import org.springframework.data.repository.core.support.RepositoryComposition;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.data.repository.core.support.RepositoryFragment;
import org.springframework.data.util.Optionals;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

public abstract class CdiRepositoryBean<T>
implements Bean<T>,
PassivationCapable {
    private static final Log logger = LogFactory.getLog(CdiRepositoryBean.class);
    private static final CdiRepositoryConfiguration DEFAULT_CONFIGURATION = DefaultCdiRepositoryConfiguration.INSTANCE;
    private final Set<Annotation> qualifiers;
    private final Class<T> repositoryType;
    private final CdiRepositoryContext context;
    private final BeanManager beanManager;
    private final String passivationId;
    @Nullable
    private transient T repoInstance;

    public CdiRepositoryBean(Set<Annotation> qualifiers, Class<T> repositoryType, BeanManager beanManager) {
        this(qualifiers, repositoryType, beanManager, new CdiRepositoryContext(CdiRepositoryBean.class.getClassLoader()));
    }

    public CdiRepositoryBean(Set<Annotation> qualifiers, Class<T> repositoryType, BeanManager beanManager, Optional<CustomRepositoryImplementationDetector> detector) {
        Assert.notNull(qualifiers, (String)"Qualifiers must not be null!");
        Assert.notNull((Object)beanManager, (String)"BeanManager must not be null!");
        Assert.notNull(repositoryType, (String)"Repoitory type must not be null!");
        Assert.isTrue((boolean)repositoryType.isInterface(), (String)"RepositoryType must be an interface!");
        this.qualifiers = qualifiers;
        this.repositoryType = repositoryType;
        this.beanManager = beanManager;
        this.context = new CdiRepositoryContext(this.getClass().getClassLoader(), detector.orElseThrow(() -> new IllegalArgumentException("CustomRepositoryImplementationDetector must be present!")));
        this.passivationId = this.createPassivationId(qualifiers, repositoryType);
    }

    public CdiRepositoryBean(Set<Annotation> qualifiers, Class<T> repositoryType, BeanManager beanManager, CdiRepositoryContext context) {
        Assert.notNull(qualifiers, (String)"Qualifiers must not be null!");
        Assert.notNull((Object)beanManager, (String)"BeanManager must not be null!");
        Assert.notNull(repositoryType, (String)"Repoitory type must not be null!");
        Assert.isTrue((boolean)repositoryType.isInterface(), (String)"RepositoryType must be an interface!");
        this.qualifiers = qualifiers;
        this.repositoryType = repositoryType;
        this.beanManager = beanManager;
        this.context = context;
        this.passivationId = this.createPassivationId(qualifiers, repositoryType);
    }

    private String createPassivationId(Set<Annotation> qualifiers, Class<?> repositoryType) {
        ArrayList<String> qualifierNames = new ArrayList<String>(qualifiers.size());
        for (Annotation qualifier : qualifiers) {
            qualifierNames.add(qualifier.annotationType().getName());
        }
        Collections.sort(qualifierNames);
        return StringUtils.collectionToDelimitedString(qualifierNames, (String)":") + ":" + repositoryType.getName();
    }

    public Set<Type> getTypes() {
        HashSet interfaces = new HashSet();
        interfaces.add(this.repositoryType);
        interfaces.addAll(Arrays.asList(this.repositoryType.getInterfaces()));
        return new HashSet<Type>(interfaces);
    }

    protected <S> S getDependencyInstance(Bean<S> bean) {
        return this.getDependencyInstance(bean, bean.getBeanClass());
    }

    protected <S> S getDependencyInstance(Bean<S> bean, Class<?> type) {
        CreationalContext creationalContext = this.beanManager.createCreationalContext(bean);
        return (S)this.beanManager.getReference(bean, type, creationalContext);
    }

    public final void initialize() {
        this.create(this.beanManager.createCreationalContext((Contextual)this));
    }

    public final T create(CreationalContext<T> creationalContext) {
        T repoInstance = this.repoInstance;
        if (repoInstance != null) {
            logger.debug((Object)LogMessage.format((String)"Returning eagerly created CDI repository instance for %s.", (Object)this.repositoryType.getName()));
            return repoInstance;
        }
        logger.debug((Object)LogMessage.format((String)"Creating CDI repository bean instance for %s.", (Object)this.repositoryType.getName()));
        this.repoInstance = repoInstance = this.create(creationalContext, this.repositoryType);
        return repoInstance;
    }

    public void destroy(T instance, CreationalContext<T> creationalContext) {
        if (logger.isDebugEnabled()) {
            logger.debug((Object)String.format("Destroying bean instance %s for repository type '%s'.", instance.toString(), this.repositoryType.getName()));
        }
        creationalContext.release();
    }

    public Set<Annotation> getQualifiers() {
        return this.qualifiers;
    }

    public String getName() {
        return this.repositoryType.getName();
    }

    public Set<Class<? extends Annotation>> getStereotypes() {
        return Arrays.stream(this.repositoryType.getAnnotations()).map(Annotation::annotationType).filter(it -> it.isAnnotationPresent(Stereotype.class)).collect(Collectors.toSet());
    }

    public Class<?> getBeanClass() {
        return this.repositoryType;
    }

    public boolean isAlternative() {
        return this.repositoryType.isAnnotationPresent(Alternative.class);
    }

    public boolean isNullable() {
        return false;
    }

    public Set<InjectionPoint> getInjectionPoints() {
        return Collections.emptySet();
    }

    public Class<? extends Annotation> getScope() {
        return ApplicationScoped.class;
    }

    public String getId() {
        return this.passivationId;
    }

    protected T create(CreationalContext<T> creationalContext, Class<T> repositoryType) {
        CdiRepositoryConfiguration cdiRepositoryConfiguration = this.lookupConfiguration(this.beanManager, this.qualifiers);
        Optional<Bean<Bean>> customImplementationBean = this.getCustomImplementationBean(repositoryType, cdiRepositoryConfiguration);
        Optional<Object> customImplementation = customImplementationBean.map(this::getDependencyInstance);
        return this.create(creationalContext, repositoryType, customImplementation);
    }

    protected T create(Supplier<? extends RepositoryFactorySupport> factorySupplier, Class<T> repositoryType) {
        CdiRepositoryConfiguration configuration = this.lookupConfiguration(this.beanManager, this.qualifiers);
        RepositoryComposition.RepositoryFragments repositoryFragments = this.getRepositoryFragments(repositoryType, configuration);
        RepositoryFactorySupport factory = factorySupplier.get();
        CdiRepositoryBean.applyConfiguration(factory, configuration);
        return CdiRepositoryBean.create(factory, repositoryType, repositoryFragments);
    }

    protected RepositoryComposition.RepositoryFragments getRepositoryFragments(Class<T> repositoryType) {
        Assert.notNull(repositoryType, (String)"Repository type must not be null!");
        CdiRepositoryConfiguration cdiRepositoryConfiguration = this.lookupConfiguration(this.beanManager, this.qualifiers);
        return this.getRepositoryFragments(repositoryType, cdiRepositoryConfiguration);
    }

    private RepositoryComposition.RepositoryFragments getRepositoryFragments(Class<T> repositoryType, CdiRepositoryConfiguration cdiRepositoryConfiguration) {
        Optional<Bean<Bean>> customImplementationBean = this.getCustomImplementationBean(repositoryType, cdiRepositoryConfiguration);
        Optional<Object> customImplementation = customImplementationBean.map(this::getDependencyInstance);
        List<RepositoryFragment<?>> repositoryFragments = this.findRepositoryFragments(repositoryType, cdiRepositoryConfiguration);
        RepositoryComposition.RepositoryFragments customImplementationFragment = customImplementation.map(xva$0 -> RepositoryComposition.RepositoryFragments.just(xva$0)).orElseGet(RepositoryComposition.RepositoryFragments::empty);
        return RepositoryComposition.RepositoryFragments.from(repositoryFragments).append(customImplementationFragment);
    }

    private List<RepositoryFragment<?>> findRepositoryFragments(Class<T> repositoryType, CdiRepositoryConfiguration cdiRepositoryConfiguration) {
        Stream<RepositoryFragmentConfiguration> fragmentConfigurations = this.context.getRepositoryFragments(cdiRepositoryConfiguration, repositoryType);
        return fragmentConfigurations.flatMap(it -> {
            Class<?> interfaceClass = CdiRepositoryBean.lookupFragmentInterface(repositoryType, it.getInterfaceName());
            Class<?> implementationClass = this.context.loadClass(it.getClassName());
            Optional<Bean<?>> bean = CdiRepositoryBean.getBean(implementationClass, this.beanManager, this.qualifiers);
            return Optionals.toStream(bean.map(this::getDependencyInstance).map(implementation -> RepositoryFragment.implemented(interfaceClass, implementation)));
        }).collect(Collectors.toList());
    }

    private static Class<?> lookupFragmentInterface(Class<?> repositoryType, String interfaceName) {
        return Arrays.stream(repositoryType.getInterfaces()).filter(it -> it.getName().equals(interfaceName)).findFirst().orElseThrow(() -> new IllegalArgumentException(String.format("Did not find type %s in %s!", interfaceName, Arrays.asList(repositoryType.getInterfaces()))));
    }

    @Deprecated
    protected T create(CreationalContext<T> creationalContext, Class<T> repositoryType, Optional<Object> customImplementation) {
        throw new UnsupportedOperationException("You have to implement create(CreationalContext<T>, Class<T>, Optional<Object>) in order to use custom repository implementations");
    }

    protected CdiRepositoryConfiguration lookupConfiguration(BeanManager beanManager, Set<Annotation> qualifiers) {
        return beanManager.getBeans(CdiRepositoryConfiguration.class, CdiRepositoryBean.getQualifiersArray(qualifiers)).stream().findFirst().map(it -> (CdiRepositoryConfiguration)this.getDependencyInstance((Bean)it)).orElse(DEFAULT_CONFIGURATION);
    }

    private Optional<Bean<?>> getCustomImplementationBean(Class<?> repositoryType, CdiRepositoryConfiguration cdiRepositoryConfiguration) {
        return this.context.getCustomImplementationClass(repositoryType, cdiRepositoryConfiguration).flatMap(type -> CdiRepositoryBean.getBean(type, this.beanManager, this.qualifiers));
    }

    protected void applyConfiguration(RepositoryFactorySupport repositoryFactory) {
        CdiRepositoryBean.applyConfiguration(repositoryFactory, this.lookupConfiguration(this.beanManager, this.qualifiers));
    }

    protected static void applyConfiguration(RepositoryFactorySupport repositoryFactory, CdiRepositoryConfiguration configuration) {
        configuration.getEvaluationContextProvider().ifPresent(repositoryFactory::setEvaluationContextProvider);
        configuration.getNamedQueries().ifPresent(repositoryFactory::setNamedQueries);
        configuration.getQueryLookupStrategy().ifPresent(repositoryFactory::setQueryLookupStrategyKey);
        configuration.getRepositoryBeanClass().ifPresent(repositoryFactory::setRepositoryBaseClass);
        configuration.getRepositoryProxyPostProcessors().forEach(repositoryFactory::addRepositoryProxyPostProcessor);
        configuration.getQueryCreationListeners().forEach(repositoryFactory::addQueryCreationListener);
    }

    protected static <T> T create(RepositoryFactorySupport repositoryFactory, Class<T> repositoryType, RepositoryComposition.RepositoryFragments repositoryFragments) {
        return repositoryFactory.getRepository(repositoryType, repositoryFragments);
    }

    private static Optional<Bean<?>> getBean(Class<?> beanType, BeanManager beanManager, Set<Annotation> qualifiers) {
        return beanManager.getBeans(beanType, CdiRepositoryBean.getQualifiersArray(qualifiers)).stream().findFirst();
    }

    private static Annotation[] getQualifiersArray(Set<Annotation> qualifiers) {
        return qualifiers.toArray(new Annotation[qualifiers.size()]);
    }

    public String toString() {
        return String.format("CdiRepositoryBean: type='%s', qualifiers=%s", this.repositoryType.getName(), this.qualifiers.toString());
    }

    static enum DefaultCdiRepositoryConfiguration implements CdiRepositoryConfiguration
    {
        INSTANCE;

    }
}

