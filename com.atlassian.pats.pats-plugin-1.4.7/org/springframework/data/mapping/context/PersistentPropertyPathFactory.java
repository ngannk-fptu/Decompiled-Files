/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ConcurrentReferenceHashMap
 *  org.springframework.util.ObjectUtils
 *  org.springframework.util.StringUtils
 */
package org.springframework.data.mapping.context;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.data.mapping.AssociationHandler;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.data.mapping.PersistentPropertyPath;
import org.springframework.data.mapping.PersistentPropertyPaths;
import org.springframework.data.mapping.PropertyHandler;
import org.springframework.data.mapping.PropertyPath;
import org.springframework.data.mapping.context.DefaultPersistentPropertyPath;
import org.springframework.data.mapping.context.InvalidPersistentPropertyPath;
import org.springframework.data.mapping.context.MappingContext;
import org.springframework.data.util.ClassTypeInformation;
import org.springframework.data.util.Pair;
import org.springframework.data.util.StreamUtils;
import org.springframework.data.util.TypeInformation;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ConcurrentReferenceHashMap;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

class PersistentPropertyPathFactory<E extends PersistentEntity<?, P>, P extends PersistentProperty<P>> {
    private static final Predicate<PersistentProperty<? extends PersistentProperty<?>>> IS_ENTITY = PersistentProperty::isEntity;
    private final Map<TypeAndPath, PersistentPropertyPath<P>> propertyPaths = new ConcurrentReferenceHashMap();
    private final MappingContext<E, P> context;

    public PersistentPropertyPathFactory(MappingContext<E, P> context) {
        this.context = context;
    }

    public PersistentPropertyPath<P> from(Class<?> type, String propertyPath) {
        Assert.notNull(type, (String)"Type must not be null!");
        Assert.notNull((Object)propertyPath, (String)"Property path must not be null!");
        return this.getPersistentPropertyPath(ClassTypeInformation.from(type), propertyPath);
    }

    public PersistentPropertyPath<P> from(TypeInformation<?> type, String propertyPath) {
        Assert.notNull(type, (String)"Type must not be null!");
        Assert.notNull((Object)propertyPath, (String)"Property path must not be null!");
        return this.getPersistentPropertyPath(type, propertyPath);
    }

    public PersistentPropertyPath<P> from(PropertyPath path) {
        Assert.notNull((Object)path, (String)"Property path must not be null!");
        return this.from(path.getOwningType(), path.toDotPath());
    }

    public <T> PersistentPropertyPaths<T, P> from(Class<T> type, Predicate<? super P> propertyFilter) {
        Assert.notNull(type, (String)"Type must not be null!");
        Assert.notNull(propertyFilter, (String)"Property filter must not be null!");
        return this.from(ClassTypeInformation.from(type), propertyFilter);
    }

    public <T> PersistentPropertyPaths<T, P> from(Class<T> type, Predicate<? super P> propertyFilter, Predicate<P> traversalGuard) {
        Assert.notNull(type, (String)"Type must not be null!");
        Assert.notNull(propertyFilter, (String)"Property filter must not be null!");
        Assert.notNull(traversalGuard, (String)"Traversal guard must not be null!");
        return this.from(ClassTypeInformation.from(type), propertyFilter, traversalGuard);
    }

    public <T> PersistentPropertyPaths<T, P> from(TypeInformation<T> type, Predicate<? super P> propertyFilter) {
        return this.from(type, propertyFilter, (P it) -> !it.isAssociation());
    }

    public <T> PersistentPropertyPaths<T, P> from(TypeInformation<T> type, Predicate<? super P> propertyFilter, Predicate<P> traversalGuard) {
        Assert.notNull(type, (String)"Type must not be null!");
        Assert.notNull(propertyFilter, (String)"Property filter must not be null!");
        Assert.notNull(traversalGuard, (String)"Traversal guard must not be null!");
        return DefaultPersistentPropertyPaths.of(type, this.from((E)type, propertyFilter, traversalGuard, (DefaultPersistentPropertyPath<P>)DefaultPersistentPropertyPath.empty()));
    }

    private PersistentPropertyPath<P> getPersistentPropertyPath(TypeInformation<?> type, String propertyPath) {
        return this.propertyPaths.computeIfAbsent(TypeAndPath.of(type, propertyPath), it -> this.createPersistentPropertyPath(it.getPath(), it.getType()));
    }

    private PersistentPropertyPath<P> createPersistentPropertyPath(String propertyPath, TypeInformation<?> type) {
        String trimmedPath = propertyPath.trim();
        List parts = trimmedPath.isEmpty() ? Collections.emptyList() : Arrays.asList(trimmedPath.split("\\."));
        DefaultPersistentPropertyPath path = DefaultPersistentPropertyPath.empty();
        Iterator<String> iterator = parts.iterator();
        Object current = this.context.getRequiredPersistentEntity(type);
        while (iterator.hasNext()) {
            String segment = (String)iterator.next();
            DefaultPersistentPropertyPath currentPath = path;
            Pair pair = this.getPair(path, iterator, segment, current);
            if (pair == null) {
                String source = StringUtils.collectionToDelimitedString(parts, (String)".");
                throw new InvalidPersistentPropertyPath(source, type, segment, currentPath);
            }
            path = pair.getFirst();
            current = (PersistentEntity)pair.getSecond();
        }
        return path;
    }

    @Nullable
    private Pair<DefaultPersistentPropertyPath<P>, E> getPair(DefaultPersistentPropertyPath<P> path, Iterator<String> iterator, String segment, E entity) {
        Object property = entity.getPersistentProperty(segment);
        if (property == null) {
            return null;
        }
        return Pair.of(path.append(property), iterator.hasNext() ? this.context.getRequiredPersistentEntity(property) : entity);
    }

    private <T> Collection<PersistentPropertyPath<P>> from(TypeInformation<T> type, Predicate<? super P> filter, Predicate<P> traversalGuard, DefaultPersistentPropertyPath<P> basePath) {
        TypeInformation<?> actualType = type.getActualType();
        if (actualType == null) {
            return Collections.emptyList();
        }
        E entity = this.context.getRequiredPersistentEntity(actualType);
        return this.from(entity, filter, traversalGuard, basePath);
    }

    private Collection<PersistentPropertyPath<P>> from(E entity, Predicate<? super P> filter, Predicate<P> traversalGuard, DefaultPersistentPropertyPath<P> basePath) {
        HashSet<PersistentPropertyPath<P>> properties = new HashSet<PersistentPropertyPath<P>>();
        PropertyHandler<PersistentProperty> propertyTester = persistentProperty -> {
            TypeInformation<?> typeInformation = persistentProperty.getTypeInformation();
            TypeInformation<?> actualPropertyType = typeInformation.getActualType();
            if (basePath.containsPropertyOfType(actualPropertyType)) {
                return;
            }
            DefaultPersistentPropertyPath<PersistentProperty> currentPath = basePath.append(persistentProperty);
            if (filter.test((P)persistentProperty)) {
                properties.add(currentPath);
            }
            if (traversalGuard.and(IS_ENTITY).test(persistentProperty)) {
                properties.addAll(this.from(this.context.getPersistentEntity(persistentProperty), filter, traversalGuard, currentPath));
            }
        };
        entity.doWithProperties(propertyTester);
        AssociationHandler handler = association -> propertyTester.doWithPersistentProperty((PersistentProperty)association.getInverse());
        entity.doWithAssociations(handler);
        return properties;
    }

    static class DefaultPersistentPropertyPaths<T, P extends PersistentProperty<P>>
    implements PersistentPropertyPaths<T, P> {
        private static final Comparator<PersistentPropertyPath<? extends PersistentProperty<?>>> SHORTEST_PATH = Comparator.comparingInt(PersistentPropertyPath::getLength);
        private final TypeInformation<T> type;
        private final Iterable<PersistentPropertyPath<P>> paths;

        private DefaultPersistentPropertyPaths(TypeInformation<T> type, Iterable<PersistentPropertyPath<P>> paths) {
            this.type = type;
            this.paths = paths;
        }

        static <T, P extends PersistentProperty<P>> PersistentPropertyPaths<T, P> of(TypeInformation<T> type, Collection<PersistentPropertyPath<P>> paths) {
            ArrayList<PersistentPropertyPath<P>> sorted = new ArrayList<PersistentPropertyPath<P>>(paths);
            Collections.sort(sorted, SHORTEST_PATH.thenComparing(ShortestSegmentFirst.INSTANCE));
            return new DefaultPersistentPropertyPaths<T, P>(type, sorted);
        }

        @Override
        public Optional<PersistentPropertyPath<P>> getFirst() {
            return this.isEmpty() ? Optional.empty() : Optional.of(this.iterator().next());
        }

        @Override
        public boolean contains(String path) {
            return this.contains(PropertyPath.from(path, this.type));
        }

        @Override
        public boolean contains(PropertyPath path) {
            Assert.notNull((Object)path, (String)"PropertyPath must not be null!");
            if (!path.getOwningType().equals(this.type)) {
                return false;
            }
            String dotPath = path.toDotPath();
            return this.stream().anyMatch(it -> dotPath.equals(it.toDotPath()));
        }

        @Override
        public Iterator<PersistentPropertyPath<P>> iterator() {
            return this.paths.iterator();
        }

        @Override
        public PersistentPropertyPaths<T, P> dropPathIfSegmentMatches(Predicate<? super P> predicate) {
            Assert.notNull(predicate, (String)"Predicate must not be null!");
            List<PersistentPropertyPath<P>> paths = this.stream().filter(it -> !it.stream().anyMatch(predicate)).collect(Collectors.toList());
            return paths.equals(this.paths) ? this : new DefaultPersistentPropertyPaths<T, P>(this.type, paths);
        }

        public String toString() {
            return "PersistentPropertyPathFactory.DefaultPersistentPropertyPaths(type=" + this.type + ", paths=" + this.paths + ")";
        }

        private static enum ShortestSegmentFirst implements Comparator<PersistentPropertyPath<? extends PersistentProperty<?>>>
        {
            INSTANCE;


            @Override
            public int compare(PersistentPropertyPath<?> left, PersistentPropertyPath<?> right) {
                Function<PersistentProperty, Integer> mapper = it -> it.getName().length();
                Stream<Integer> leftNames = left.stream().map(mapper);
                Stream<Integer> rightNames = right.stream().map(mapper);
                return StreamUtils.zip(leftNames, rightNames, (l, r) -> l - r).filter(it -> it != 0).findFirst().orElse(0);
            }
        }
    }

    static final class TypeAndPath {
        private final TypeInformation<?> type;
        private final String path;

        private TypeAndPath(TypeInformation<?> type, String path) {
            this.type = type;
            this.path = path;
        }

        public static TypeAndPath of(TypeInformation<?> type, String path) {
            return new TypeAndPath(type, path);
        }

        public TypeInformation<?> getType() {
            return this.type;
        }

        public String getPath() {
            return this.path;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof TypeAndPath)) {
                return false;
            }
            TypeAndPath that = (TypeAndPath)o;
            if (!ObjectUtils.nullSafeEquals(this.type, that.type)) {
                return false;
            }
            return ObjectUtils.nullSafeEquals((Object)this.path, (Object)that.path);
        }

        public int hashCode() {
            int result = ObjectUtils.nullSafeHashCode(this.type);
            result = 31 * result + ObjectUtils.nullSafeHashCode((Object)this.path);
            return result;
        }

        public String toString() {
            return "PersistentPropertyPathFactory.TypeAndPath(type=" + this.getType() + ", path=" + this.getPath() + ")";
        }
    }
}

