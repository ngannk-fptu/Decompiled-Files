/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.BeanUtils
 *  org.springframework.beans.BeansException
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.beans.factory.ListableBeanFactory
 *  org.springframework.context.ApplicationContext
 *  org.springframework.context.ApplicationContextAware
 *  org.springframework.context.ApplicationEventPublisher
 *  org.springframework.context.ApplicationEventPublisherAware
 *  org.springframework.core.KotlinDetector
 *  org.springframework.core.NativeDetector
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ReflectionUtils
 *  org.springframework.util.ReflectionUtils$FieldCallback
 *  org.springframework.util.ReflectionUtils$FieldFilter
 */
package org.springframework.data.mapping.context;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.core.KotlinDetector;
import org.springframework.core.NativeDetector;
import org.springframework.data.mapping.MappingException;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.data.mapping.PersistentPropertyPath;
import org.springframework.data.mapping.PersistentPropertyPaths;
import org.springframework.data.mapping.PropertyPath;
import org.springframework.data.mapping.context.MappingContext;
import org.springframework.data.mapping.context.MappingContextEvent;
import org.springframework.data.mapping.context.PersistentPropertyPathFactory;
import org.springframework.data.mapping.model.BeanWrapperPropertyAccessorFactory;
import org.springframework.data.mapping.model.ClassGeneratingPropertyAccessorFactory;
import org.springframework.data.mapping.model.EntityInstantiators;
import org.springframework.data.mapping.model.InstantiationAwarePropertyAccessorFactory;
import org.springframework.data.mapping.model.MutablePersistentEntity;
import org.springframework.data.mapping.model.PersistentPropertyAccessorFactory;
import org.springframework.data.mapping.model.Property;
import org.springframework.data.mapping.model.SimpleTypeHolder;
import org.springframework.data.spel.EvaluationContextProvider;
import org.springframework.data.spel.ExtensionAwareEvaluationContextProvider;
import org.springframework.data.util.ClassTypeInformation;
import org.springframework.data.util.KotlinReflectionUtils;
import org.springframework.data.util.NullableWrapperConverters;
import org.springframework.data.util.Optionals;
import org.springframework.data.util.Streamable;
import org.springframework.data.util.TypeInformation;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

public abstract class AbstractMappingContext<E extends MutablePersistentEntity<?, P>, P extends PersistentProperty<P>>
implements MappingContext<E, P>,
ApplicationEventPublisherAware,
ApplicationContextAware,
InitializingBean {
    private final Optional<E> NONE = Optional.empty();
    private final Map<TypeInformation<?>, Optional<E>> persistentEntities = new HashMap();
    private final PersistentPropertyAccessorFactory persistentPropertyAccessorFactory;
    private final PersistentPropertyPathFactory<E, P> persistentPropertyPathFactory;
    @Nullable
    private ApplicationEventPublisher applicationEventPublisher;
    private EvaluationContextProvider evaluationContextProvider = EvaluationContextProvider.DEFAULT;
    private Set<? extends Class<?>> initialEntitySet = new HashSet();
    private boolean strict = false;
    private SimpleTypeHolder simpleTypeHolder = SimpleTypeHolder.DEFAULT;
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock read = this.lock.readLock();
    private final Lock write = this.lock.writeLock();

    protected AbstractMappingContext() {
        this.persistentPropertyPathFactory = new PersistentPropertyPathFactory(this);
        EntityInstantiators instantiators = new EntityInstantiators();
        PersistentPropertyAccessorFactory accessorFactory = NativeDetector.inNativeImage() ? BeanWrapperPropertyAccessorFactory.INSTANCE : new ClassGeneratingPropertyAccessorFactory();
        this.persistentPropertyAccessorFactory = new InstantiationAwarePropertyAccessorFactory(accessorFactory, instantiators);
    }

    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.evaluationContextProvider = new ExtensionAwareEvaluationContextProvider((ListableBeanFactory)applicationContext);
        if (this.applicationEventPublisher == null) {
            this.applicationEventPublisher = applicationContext;
        }
    }

    public void setInitialEntitySet(Set<? extends Class<?>> initialEntitySet) {
        this.initialEntitySet = initialEntitySet;
    }

    public void setStrict(boolean strict) {
        this.strict = strict;
    }

    public void setSimpleTypeHolder(SimpleTypeHolder simpleTypes) {
        Assert.notNull((Object)simpleTypes, (String)"SimpleTypeHolder must not be null!");
        this.simpleTypeHolder = simpleTypes;
    }

    @Override
    public Collection<E> getPersistentEntities() {
        try {
            this.read.lock();
            Collection collection = this.persistentEntities.values().stream().flatMap(xva$0 -> Optionals.toStream(xva$0)).collect(Collectors.toSet());
            return collection;
        }
        finally {
            this.read.unlock();
        }
    }

    @Override
    @Nullable
    public E getPersistentEntity(Class<?> type) {
        return (E)this.getPersistentEntity(ClassTypeInformation.from(type));
    }

    @Override
    public boolean hasPersistentEntityFor(Class<?> type) {
        Assert.notNull(type, (String)"Type must not be null!");
        Optional<E> entity = this.persistentEntities.get(ClassTypeInformation.from(type));
        return entity == null ? false : entity.isPresent();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    @Nullable
    public E getPersistentEntity(TypeInformation<?> type) {
        Assert.notNull(type, (String)"Type must not be null!");
        try {
            this.read.lock();
            Optional<E> entity = this.persistentEntities.get(type);
            if (entity != null) {
                MutablePersistentEntity mutablePersistentEntity = entity.orElse(null);
                return (E)mutablePersistentEntity;
            }
        }
        finally {
            this.read.unlock();
        }
        if (!this.shouldCreatePersistentEntityFor(type)) {
            try {
                this.write.lock();
                this.persistentEntities.put(type, this.NONE);
            }
            finally {
                this.write.unlock();
            }
            return null;
        }
        if (this.strict) {
            throw new MappingException("Unknown persistent entity " + type);
        }
        return (E)((MutablePersistentEntity)this.addPersistentEntity(type).orElse(null));
    }

    @Override
    @Nullable
    public E getPersistentEntity(P persistentProperty) {
        Assert.notNull(persistentProperty, (String)"PersistentProperty must not be null!");
        if (!persistentProperty.isEntity()) {
            return null;
        }
        TypeInformation<?> typeInfo = persistentProperty.getTypeInformation();
        return (E)this.getPersistentEntity((TypeInformation)typeInfo.getRequiredActualType());
    }

    @Override
    public PersistentPropertyPath<P> getPersistentPropertyPath(PropertyPath propertyPath) {
        return this.persistentPropertyPathFactory.from(propertyPath);
    }

    @Override
    public PersistentPropertyPath<P> getPersistentPropertyPath(String propertyPath, Class<?> type) {
        return this.persistentPropertyPathFactory.from(type, propertyPath);
    }

    @Override
    public <T> PersistentPropertyPaths<T, P> findPersistentPropertyPaths(Class<T> type, Predicate<? super P> predicate) {
        Assert.notNull(type, (String)"Type must not be null!");
        Assert.notNull(predicate, (String)"Selection predicate must not be null!");
        return this.doFindPersistentPropertyPaths(type, predicate, it -> !it.isAssociation());
    }

    protected final <T> PersistentPropertyPaths<T, P> doFindPersistentPropertyPaths(Class<T> type, Predicate<? super P> predicate, Predicate<P> traversalGuard) {
        return this.persistentPropertyPathFactory.from(ClassTypeInformation.from(type), predicate, traversalGuard);
    }

    protected Optional<E> addPersistentEntity(Class<?> type) {
        return this.addPersistentEntity(ClassTypeInformation.from(type));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected Optional<E> addPersistentEntity(TypeInformation<?> typeInformation) {
        E entity;
        Assert.notNull(typeInformation, (String)"TypeInformation must not be null!");
        try {
            this.read.lock();
            Optional<E> persistentEntity = this.persistentEntities.get(typeInformation);
            if (persistentEntity != null) {
                Optional<E> optional = persistentEntity;
                return optional;
            }
        }
        finally {
            this.read.unlock();
        }
        try {
            this.write.lock();
            entity = this.doAddPersistentEntity(typeInformation);
        }
        catch (BeansException e) {
            throw new MappingException(e.getMessage(), e);
        }
        finally {
            this.write.unlock();
        }
        if (this.applicationEventPublisher != null) {
            this.applicationEventPublisher.publishEvent(new MappingContextEvent(this, entity));
        }
        return Optional.of(entity);
    }

    private E doAddPersistentEntity(TypeInformation<?> typeInformation) {
        try {
            Class<?> type = typeInformation.getType();
            E entity = this.createPersistentEntity(typeInformation);
            entity.setEvaluationContextProvider(this.evaluationContextProvider);
            this.persistentEntities.put(typeInformation, Optional.of(entity));
            PropertyDescriptor[] pds = BeanUtils.getPropertyDescriptors(type);
            HashMap<String, PropertyDescriptor> descriptors = new HashMap<String, PropertyDescriptor>();
            for (PropertyDescriptor descriptor : pds) {
                descriptors.put(descriptor.getName(), descriptor);
            }
            PersistentPropertyCreator persistentPropertyCreator = new PersistentPropertyCreator(this, entity, descriptors);
            ReflectionUtils.doWithFields(type, (ReflectionUtils.FieldCallback)persistentPropertyCreator, (ReflectionUtils.FieldFilter)PersistentPropertyFilter.INSTANCE);
            persistentPropertyCreator.addPropertiesForRemainingDescriptors();
            entity.verify();
            if (this.persistentPropertyAccessorFactory.isSupported((PersistentEntity<?, ?>)entity)) {
                entity.setPersistentPropertyAccessorFactory(this.persistentPropertyAccessorFactory);
            }
            return entity;
        }
        catch (RuntimeException e) {
            this.persistentEntities.remove(typeInformation);
            throw e;
        }
    }

    @Override
    public Collection<TypeInformation<?>> getManagedTypes() {
        try {
            this.read.lock();
            Set<TypeInformation<?>> set = Collections.unmodifiableSet(new HashSet(this.persistentEntities.keySet()));
            return set;
        }
        finally {
            this.read.unlock();
        }
    }

    protected abstract <T> E createPersistentEntity(TypeInformation<T> var1);

    protected abstract P createPersistentProperty(Property var1, E var2, SimpleTypeHolder var3);

    public void afterPropertiesSet() {
        this.initialize();
    }

    public void initialize() {
        this.initialEntitySet.forEach(this::addPersistentEntity);
    }

    protected boolean shouldCreatePersistentEntityFor(TypeInformation<?> type) {
        if (this.simpleTypeHolder.isSimpleType(type.getType())) {
            return false;
        }
        if (NullableWrapperConverters.supports(type.getType())) {
            return false;
        }
        return !KotlinDetector.isKotlinType(type.getType()) || KotlinReflectionUtils.isSupportedKotlinClass(type.getType());
    }

    static enum PersistentPropertyFilter implements ReflectionUtils.FieldFilter
    {
        INSTANCE;

        private static final Streamable<PropertyMatch> UNMAPPED_PROPERTIES;

        public boolean matches(Field field) {
            if (Modifier.isStatic(field.getModifiers())) {
                return false;
            }
            return !UNMAPPED_PROPERTIES.stream().anyMatch(it -> it.matches(field.getName(), field.getType()));
        }

        public boolean matches(Property property) {
            Assert.notNull((Object)property, (String)"Property must not be null!");
            if (!property.hasAccessor()) {
                return false;
            }
            return !UNMAPPED_PROPERTIES.stream().anyMatch(it -> it.matches(property.getName(), property.getType()));
        }

        static {
            HashSet<PropertyMatch> matches = new HashSet<PropertyMatch>();
            matches.add(new PropertyMatch("class", null));
            matches.add(new PropertyMatch("this\\$.*", null));
            matches.add(new PropertyMatch("metaClass", "groovy.lang.MetaClass"));
            UNMAPPED_PROPERTIES = Streamable.of(matches);
        }

        static class PropertyMatch {
            @Nullable
            private final String namePattern;
            @Nullable
            private final String typeName;

            public PropertyMatch(@Nullable String namePattern, @Nullable String typeName) {
                Assert.isTrue((namePattern != null || typeName != null ? 1 : 0) != 0, (String)"Either name pattern or type name must be given!");
                this.namePattern = namePattern;
                this.typeName = typeName;
            }

            public boolean matches(String name, Class<?> type) {
                Assert.notNull((Object)name, (String)"Name must not be null!");
                Assert.notNull(type, (String)"Type must not be null!");
                if (this.namePattern != null && !name.matches(this.namePattern)) {
                    return false;
                }
                return this.typeName == null || type.getName().equals(this.typeName);
            }
        }
    }

    private static final class PersistentPropertyCreator
    implements ReflectionUtils.FieldCallback {
        private final E entity;
        private final Map<String, PropertyDescriptor> descriptors;
        private final Map<String, PropertyDescriptor> remainingDescriptors;
        final /* synthetic */ AbstractMappingContext this$0;

        public PersistentPropertyCreator(E entity, Map<String, PropertyDescriptor> descriptors) {
            this(var1_1, (MutablePersistentEntity)entity, descriptors, descriptors);
        }

        private PersistentPropertyCreator(E entity, Map<String, PropertyDescriptor> descriptors, Map<String, PropertyDescriptor> remainingDescriptors) {
            this.this$0 = var1_1;
            this.entity = entity;
            this.descriptors = descriptors;
            this.remainingDescriptors = remainingDescriptors;
        }

        public void doWith(Field field) {
            String fieldName = field.getName();
            TypeInformation type = this.entity.getTypeInformation();
            ReflectionUtils.makeAccessible((Field)field);
            Property property = Optional.ofNullable(this.descriptors.get(fieldName)).map(it -> Property.of(type, field, it)).orElseGet(() -> Property.of(type, field));
            this.createAndRegisterProperty(property);
            this.remainingDescriptors.remove(fieldName);
        }

        public void addPropertiesForRemainingDescriptors() {
            this.remainingDescriptors.values().stream().filter(Property::supportsStandalone).map(it -> Property.of(this.entity.getTypeInformation(), it)).filter(PersistentPropertyFilter.INSTANCE::matches).forEach(this::createAndRegisterProperty);
        }

        private void createAndRegisterProperty(Property input) {
            Object property = this.this$0.createPersistentProperty(input, this.entity, this.this$0.simpleTypeHolder);
            if (property.isTransient()) {
                return;
            }
            if (!input.isFieldBacked() && !property.usePropertyAccess()) {
                return;
            }
            this.entity.addPersistentProperty(property);
            if (property.isAssociation()) {
                this.entity.addAssociation(property.getRequiredAssociation());
            }
            if (this.entity.getType().equals(property.getRawType())) {
                return;
            }
            StreamSupport.stream(property.getPersistentEntityTypes().spliterator(), false).map(it -> {
                if (it.isNullableWrapper()) {
                    return it.getActualType();
                }
                return it;
            }).filter(it -> {
                boolean shouldCreate = this.this$0.shouldCreatePersistentEntityFor((TypeInformation<?>)it);
                return shouldCreate;
            }).forEach(this.this$0::addPersistentEntity);
        }
    }
}

