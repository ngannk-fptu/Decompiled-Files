/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.annotation.AnnotatedElementUtils
 *  org.springframework.expression.EvaluationContext
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.CollectionUtils
 *  org.springframework.util.ConcurrentReferenceHashMap
 *  org.springframework.util.ConcurrentReferenceHashMap$ReferenceType
 *  org.springframework.util.MultiValueMap
 *  org.springframework.util.StringUtils
 */
package org.springframework.data.mapping.model;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.data.annotation.Immutable;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.domain.Persistable;
import org.springframework.data.mapping.Alias;
import org.springframework.data.mapping.Association;
import org.springframework.data.mapping.AssociationHandler;
import org.springframework.data.mapping.IdentifierAccessor;
import org.springframework.data.mapping.MappingException;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.data.mapping.PersistentPropertyAccessor;
import org.springframework.data.mapping.PersistentPropertyPathAccessor;
import org.springframework.data.mapping.PreferredConstructor;
import org.springframework.data.mapping.PropertyHandler;
import org.springframework.data.mapping.SimpleAssociationHandler;
import org.springframework.data.mapping.SimplePropertyHandler;
import org.springframework.data.mapping.TargetAwareIdentifierAccessor;
import org.springframework.data.mapping.model.BeanWrapperPropertyAccessorFactory;
import org.springframework.data.mapping.model.IdPropertyIdentifierAccessor;
import org.springframework.data.mapping.model.MutablePersistentEntity;
import org.springframework.data.mapping.model.PersistableIdentifierAccessor;
import org.springframework.data.mapping.model.PersistentEntityIsNewStrategy;
import org.springframework.data.mapping.model.PersistentPropertyAccessorFactory;
import org.springframework.data.mapping.model.PreferredConstructorDiscoverer;
import org.springframework.data.mapping.model.SimplePersistentPropertyPathAccessor;
import org.springframework.data.spel.EvaluationContextProvider;
import org.springframework.data.spel.ExpressionDependencies;
import org.springframework.data.support.IsNewStrategy;
import org.springframework.data.support.PersistableIsNewStrategy;
import org.springframework.data.util.Lazy;
import org.springframework.data.util.TypeInformation;
import org.springframework.expression.EvaluationContext;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ConcurrentReferenceHashMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

public class BasicPersistentEntity<T, P extends PersistentProperty<P>>
implements MutablePersistentEntity<T, P> {
    private static final String TYPE_MISMATCH = "Target bean of type %s is not of type of the persistent entity (%s)!";
    @Nullable
    private final PreferredConstructor<T, P> constructor;
    private final TypeInformation<T> information;
    private final List<P> properties;
    private final List<P> persistentPropertiesCache;
    @Nullable
    private final Comparator<P> comparator;
    private final Set<Association<P>> associations;
    private final Map<String, P> propertyCache;
    private final Map<Class<? extends Annotation>, Optional<Annotation>> annotationCache;
    private final MultiValueMap<Class<? extends Annotation>, P> propertyAnnotationCache;
    @Nullable
    private P idProperty;
    @Nullable
    private P versionProperty;
    private PersistentPropertyAccessorFactory propertyAccessorFactory;
    private EvaluationContextProvider evaluationContextProvider = EvaluationContextProvider.DEFAULT;
    private final Lazy<Alias> typeAlias;
    private final Lazy<IsNewStrategy> isNewStrategy;
    private final Lazy<Boolean> isImmutable;
    private final Lazy<Boolean> requiresPropertyPopulation;

    public BasicPersistentEntity(TypeInformation<T> information) {
        this(information, null);
    }

    public BasicPersistentEntity(TypeInformation<T> information, @Nullable Comparator<P> comparator) {
        Assert.notNull(information, (String)"Information must not be null!");
        this.information = information;
        this.properties = new ArrayList<P>();
        this.persistentPropertiesCache = new ArrayList<P>();
        this.comparator = comparator;
        this.constructor = PreferredConstructorDiscoverer.discover(this);
        this.associations = comparator == null ? new HashSet() : new TreeSet<P>(new AssociationComparator<P>(comparator));
        this.propertyCache = new HashMap<String, P>(16, 1.0f);
        this.annotationCache = new ConcurrentReferenceHashMap(16, ConcurrentReferenceHashMap.ReferenceType.WEAK);
        this.propertyAnnotationCache = CollectionUtils.toMultiValueMap((Map)new ConcurrentReferenceHashMap(16, ConcurrentReferenceHashMap.ReferenceType.WEAK));
        this.propertyAccessorFactory = BeanWrapperPropertyAccessorFactory.INSTANCE;
        this.typeAlias = Lazy.of(() -> BasicPersistentEntity.getAliasFromAnnotation(this.getType()));
        this.isNewStrategy = Lazy.of(() -> Persistable.class.isAssignableFrom(information.getType()) ? PersistableIsNewStrategy.INSTANCE : this.getFallbackIsNewStrategy());
        this.isImmutable = Lazy.of(() -> this.isAnnotationPresent(Immutable.class));
        this.requiresPropertyPopulation = Lazy.of(() -> !this.isImmutable() && this.properties.stream().anyMatch(it -> !this.isConstructorArgument((PersistentProperty<?>)it) && !it.isTransient()));
    }

    @Override
    @Nullable
    public PreferredConstructor<T, P> getPersistenceConstructor() {
        return this.constructor;
    }

    @Override
    public boolean isConstructorArgument(PersistentProperty<?> property) {
        return this.constructor != null && this.constructor.isConstructorParameter(property);
    }

    @Override
    public boolean isIdProperty(PersistentProperty<?> property) {
        return this.idProperty != null && this.idProperty.equals(property);
    }

    @Override
    public boolean isVersionProperty(PersistentProperty<?> property) {
        return this.versionProperty != null && this.versionProperty.equals(property);
    }

    @Override
    public String getName() {
        return this.getType().getName();
    }

    @Override
    @Nullable
    public P getIdProperty() {
        return this.idProperty;
    }

    @Override
    @Nullable
    public P getVersionProperty() {
        return this.versionProperty;
    }

    @Override
    public boolean hasIdProperty() {
        return this.idProperty != null;
    }

    @Override
    public boolean hasVersionProperty() {
        return this.versionProperty != null;
    }

    @Override
    public void addPersistentProperty(P property) {
        Assert.notNull(property, (String)"Property must not be null!");
        if (this.properties.contains(property)) {
            return;
        }
        this.properties.add(property);
        if (!property.isTransient() && !property.isAssociation()) {
            this.persistentPropertiesCache.add(property);
        }
        this.propertyCache.computeIfAbsent(property.getName(), key -> property);
        P candidate = this.returnPropertyIfBetterIdPropertyCandidateOrNull(property);
        if (candidate != null) {
            this.idProperty = candidate;
        }
        if (property.isVersionProperty()) {
            P versionProperty = this.versionProperty;
            if (versionProperty != null) {
                throw new MappingException(String.format("Attempt to add version property %s but already have property %s registered as version. Check your mapping configuration!", property.getField(), versionProperty.getField()));
            }
            this.versionProperty = property;
        }
    }

    @Override
    public void setEvaluationContextProvider(EvaluationContextProvider provider) {
        this.evaluationContextProvider = provider;
    }

    @Nullable
    protected P returnPropertyIfBetterIdPropertyCandidateOrNull(P property) {
        if (!property.isIdProperty()) {
            return null;
        }
        P idProperty = this.idProperty;
        if (idProperty != null) {
            throw new MappingException(String.format("Attempt to add id property %s but already have property %s registered as id. Check your mapping configuration!", property.getField(), idProperty.getField()));
        }
        return property;
    }

    @Override
    public void addAssociation(Association<P> association) {
        Assert.notNull(association, (String)"Association must not be null!");
        this.associations.add(association);
    }

    @Override
    @Nullable
    public P getPersistentProperty(String name) {
        return (P)((PersistentProperty)this.propertyCache.get(name));
    }

    @Override
    public Iterable<P> getPersistentProperties(Class<? extends Annotation> annotationType) {
        Assert.notNull(annotationType, (String)"Annotation type must not be null!");
        return (Iterable)this.propertyAnnotationCache.computeIfAbsent(annotationType, this::doFindPersistentProperty);
    }

    private List<P> doFindPersistentProperty(Class<? extends Annotation> annotationType) {
        List annotatedProperties = this.properties.stream().filter(it -> it.isAnnotationPresent(annotationType)).collect(Collectors.toList());
        if (!annotatedProperties.isEmpty()) {
            return annotatedProperties;
        }
        return this.associations.stream().map(Association::getInverse).filter(it -> it.isAnnotationPresent(annotationType)).collect(Collectors.toList());
    }

    @Override
    public Class<T> getType() {
        return this.information.getType();
    }

    @Override
    public Alias getTypeAlias() {
        return this.typeAlias.get();
    }

    @Override
    public TypeInformation<T> getTypeInformation() {
        return this.information;
    }

    @Override
    public void doWithProperties(PropertyHandler<P> handler) {
        Assert.notNull(handler, (String)"PropertyHandler must not be null!");
        for (PersistentProperty property : this.persistentPropertiesCache) {
            handler.doWithPersistentProperty(property);
        }
    }

    @Override
    public void doWithProperties(SimplePropertyHandler handler) {
        Assert.notNull((Object)handler, (String)"Handler must not be null!");
        for (PersistentProperty property : this.persistentPropertiesCache) {
            handler.doWithPersistentProperty(property);
        }
    }

    @Override
    public void doWithAssociations(AssociationHandler<P> handler) {
        Assert.notNull(handler, (String)"Handler must not be null!");
        for (Association<P> association : this.associations) {
            handler.doWithAssociation(association);
        }
    }

    @Override
    public void doWithAssociations(SimpleAssociationHandler handler) {
        Assert.notNull((Object)handler, (String)"Handler must not be null!");
        for (Association<P> association : this.associations) {
            handler.doWithAssociation(association);
        }
    }

    @Override
    @Nullable
    public <A extends Annotation> A findAnnotation(Class<A> annotationType) {
        return (A)((Annotation)this.doFindAnnotation(annotationType).orElse(null));
    }

    @Override
    public <A extends Annotation> boolean isAnnotationPresent(Class<A> annotationType) {
        return this.doFindAnnotation(annotationType).isPresent();
    }

    private <A extends Annotation> Optional<A> doFindAnnotation(Class<A> annotationType) {
        return this.annotationCache.computeIfAbsent(annotationType, it -> Optional.ofNullable(AnnotatedElementUtils.findMergedAnnotation(this.getType(), (Class)it)));
    }

    @Override
    public void verify() {
        if (this.comparator != null) {
            this.properties.sort(this.comparator);
            this.persistentPropertiesCache.sort(this.comparator);
        }
    }

    @Override
    public void setPersistentPropertyAccessorFactory(PersistentPropertyAccessorFactory factory) {
        this.propertyAccessorFactory = factory;
    }

    @Override
    public <B> PersistentPropertyAccessor<B> getPropertyAccessor(B bean) {
        this.verifyBeanType(bean);
        return this.propertyAccessorFactory.getPropertyAccessor(this, bean);
    }

    @Override
    public <B> PersistentPropertyPathAccessor<B> getPropertyPathAccessor(B bean) {
        return new SimplePersistentPropertyPathAccessor<B>(this.getPropertyAccessor(bean));
    }

    @Override
    public IdentifierAccessor getIdentifierAccessor(Object bean) {
        this.verifyBeanType(bean);
        if (Persistable.class.isAssignableFrom(this.getType())) {
            return new PersistableIdentifierAccessor((Persistable)bean);
        }
        return this.hasIdProperty() ? new IdPropertyIdentifierAccessor(this, bean) : new AbsentIdentifierAccessor(bean);
    }

    @Override
    public boolean isNew(Object bean) {
        this.verifyBeanType(bean);
        return this.isNewStrategy.get().isNew(bean);
    }

    @Override
    public boolean isImmutable() {
        return this.isImmutable.get();
    }

    @Override
    public boolean requiresPropertyPopulation() {
        return this.requiresPropertyPopulation.get();
    }

    @Override
    public Iterator<P> iterator() {
        final Iterator<P> iterator = this.properties.iterator();
        return new Iterator<P>(){

            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public P next() {
                return (PersistentProperty)iterator.next();
            }
        };
    }

    protected EvaluationContext getEvaluationContext(Object rootObject) {
        return this.evaluationContextProvider.getEvaluationContext(rootObject);
    }

    protected EvaluationContext getEvaluationContext(Object rootObject, ExpressionDependencies dependencies) {
        return this.evaluationContextProvider.getEvaluationContext(rootObject, dependencies);
    }

    protected IsNewStrategy getFallbackIsNewStrategy() {
        return PersistentEntityIsNewStrategy.of(this);
    }

    private void verifyBeanType(Object bean) {
        Assert.notNull((Object)bean, (String)"Target bean must not be null!");
        Assert.isInstanceOf(this.getType(), (Object)bean, () -> String.format(TYPE_MISMATCH, bean.getClass().getName(), this.getType().getName()));
    }

    private static Alias getAliasFromAnnotation(Class<?> type) {
        Optional<String> typeAliasValue = Optional.ofNullable(AnnotatedElementUtils.findMergedAnnotation(type, TypeAlias.class)).map(TypeAlias::value).filter(StringUtils::hasText);
        return Alias.ofNullable(typeAliasValue.orElse(null));
    }

    private static final class AssociationComparator<P extends PersistentProperty<P>>
    implements Comparator<Association<P>>,
    Serializable {
        private static final long serialVersionUID = 4508054194886854513L;
        private final Comparator<P> delegate;

        AssociationComparator(Comparator<P> delegate) {
            this.delegate = delegate;
        }

        @Override
        public int compare(@Nullable Association<P> left, @Nullable Association<P> right) {
            if (left == null) {
                throw new IllegalArgumentException("Left argument must not be null!");
            }
            if (right == null) {
                throw new IllegalArgumentException("Right argument must not be null!");
            }
            return this.delegate.compare(left.getInverse(), right.getInverse());
        }
    }

    private static class AbsentIdentifierAccessor
    extends TargetAwareIdentifierAccessor {
        public AbsentIdentifierAccessor(Object target) {
            super(target);
        }

        @Override
        @Nullable
        public Object getIdentifier() {
            return null;
        }
    }
}

