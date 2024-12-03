/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ObjectUtils
 *  org.springframework.util.StringUtils
 */
package org.springframework.data.querydsl.binding;

import com.querydsl.core.types.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.data.querydsl.binding.MultiValueBinding;
import org.springframework.data.querydsl.binding.OptionalValueBinding;
import org.springframework.data.querydsl.binding.PathInformation;
import org.springframework.data.querydsl.binding.PropertyPathInformation;
import org.springframework.data.querydsl.binding.QuerydslPathInformation;
import org.springframework.data.querydsl.binding.SingleValueBinding;
import org.springframework.data.util.ClassTypeInformation;
import org.springframework.data.util.Optionals;
import org.springframework.data.util.TypeInformation;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

public class QuerydslBindings {
    private final Map<String, PathAndBinding<?, ?>> pathSpecs = new LinkedHashMap();
    private final Map<Class<?>, PathAndBinding<?, ?>> typeSpecs = new LinkedHashMap();
    private final Set<String> allowList = new HashSet<String>();
    private final Set<String> denyList = new HashSet<String>();
    private final Set<String> aliases = new HashSet<String>();
    private boolean excludeUnlistedProperties;

    public final <T extends Path<S>, S> AliasingPathBinder<T, S> bind(T path) {
        return new AliasingPathBinder(this, path);
    }

    @SafeVarargs
    public final <T extends Path<S>, S> PathBinder<T, S> bind(T ... paths) {
        return new PathBinder(this, paths);
    }

    public final <T> TypeBinder<T> bind(Class<T> type) {
        return new TypeBinder<T>(type);
    }

    public final void excluding(Path<?> ... paths) {
        Assert.notEmpty((Object[])paths, (String)"At least one path has to be provided!");
        for (Path<?> path : paths) {
            this.denyList.add(QuerydslBindings.toDotPath(Optional.of(path)));
        }
    }

    public final void including(Path<?> ... paths) {
        Assert.notEmpty((Object[])paths, (String)"At least one path has to be provided!");
        for (Path<?> path : paths) {
            this.allowList.add(QuerydslBindings.toDotPath(Optional.of(path)));
        }
    }

    public final QuerydslBindings excludeUnlistedProperties(boolean excludeUnlistedProperties) {
        this.excludeUnlistedProperties = excludeUnlistedProperties;
        return this;
    }

    boolean isPathAvailable(String path, Class<?> type) {
        Assert.notNull((Object)path, (String)"Path must not be null!");
        Assert.notNull(type, (String)"Type must not be null!");
        return this.isPathAvailable(path, ClassTypeInformation.from(type));
    }

    boolean isPathAvailable(String path, TypeInformation<?> type) {
        Assert.notNull((Object)path, (String)"Path must not be null!");
        Assert.notNull(type, (String)"Type must not be null!");
        return this.getPropertyPath(path, type) != null;
    }

    public <S extends Path<? extends T>, T> Optional<MultiValueBinding<S, T>> getBindingForPath(PathInformation path) {
        Optional<MultiValueBinding<S, T>> binding;
        Assert.notNull((Object)path, (String)"PropertyPath must not be null!");
        PathAndBinding<?, ?> pathAndBinding = this.pathSpecs.get(QuerydslBindings.createKey(path));
        if (pathAndBinding != null && (binding = pathAndBinding.getBinding()).isPresent()) {
            return binding;
        }
        pathAndBinding = this.typeSpecs.get(path.getLeafType());
        return pathAndBinding == null ? Optional.empty() : pathAndBinding.getBinding();
    }

    Optional<Path<?>> getExistingPath(PathInformation path) {
        Assert.notNull((Object)path, (String)"PropertyPath must not be null!");
        return Optional.ofNullable(this.pathSpecs.get(QuerydslBindings.createKey(path))).flatMap(PathAndBinding::getPath);
    }

    @Nullable
    PathInformation getPropertyPath(String path, TypeInformation<?> type) {
        Assert.notNull((Object)path, (String)"Path must not be null!");
        Assert.notNull(type, (String)"Type information must not be null!");
        if (!this.isPathVisible(path)) {
            return null;
        }
        String key = QuerydslBindings.createKey(type, path);
        if (this.pathSpecs.containsKey(key)) {
            return this.pathSpecs.get(key).getPath().map(QuerydslPathInformation::of).orElse(null);
        }
        if (this.pathSpecs.containsKey(path)) {
            return this.pathSpecs.get(path).getPath().map(QuerydslPathInformation::of).orElse(null);
        }
        try {
            PropertyPathInformation propertyPath = PropertyPathInformation.of(path, type);
            return this.isPathVisible(propertyPath) ? propertyPath : null;
        }
        catch (PropertyReferenceException o_O) {
            return null;
        }
    }

    private static String createKey(Optional<Path<?>> path) {
        return path.map(QuerydslPathInformation::of).map(QuerydslBindings::createKey).orElse("");
    }

    private static String createKey(PathInformation path) {
        return QuerydslBindings.createKey(path.getRootParentType(), path.toDotPath());
    }

    private static String createKey(TypeInformation<?> type, String path) {
        return QuerydslBindings.createKey(type.getType(), path);
    }

    private static String createKey(Class<?> type, String path) {
        return type.getSimpleName() + "." + path;
    }

    private boolean isPathVisible(PathInformation path) {
        List<String> segments = Arrays.asList(path.toDotPath().split("\\."));
        for (int i = 1; i <= segments.size(); ++i) {
            if (this.isPathVisible(StringUtils.collectionToDelimitedString(segments.subList(0, i), (String)"."))) continue;
            if (!this.allowList.isEmpty()) {
                return this.allowList.contains(path.toDotPath());
            }
            return false;
        }
        return true;
    }

    private boolean isPathVisible(String path) {
        if (this.aliases.contains(path) && !this.denyList.contains(path)) {
            return true;
        }
        if (this.allowList.isEmpty()) {
            return this.excludeUnlistedProperties ? false : !this.denyList.contains(path);
        }
        return this.allowList.contains(path);
    }

    private static String toDotPath(Optional<Path<?>> path) {
        return path.map(QuerydslBindings::fromRootPath).orElse("");
    }

    private static String fromRootPath(Path<?> path) {
        Path<?> rootPath = path.getMetadata().getRootPath();
        if (rootPath == null) {
            throw new IllegalStateException(String.format("Couldn't find root path on path %s!", path));
        }
        return path.toString().substring(rootPath.getMetadata().getName().length() + 1);
    }

    private static final class PathAndBinding<P extends Path<? extends T>, T> {
        private final Optional<Path<?>> path;
        private final Optional<MultiValueBinding<P, T>> binding;

        PathAndBinding(Optional<Path<?>> path, Optional<MultiValueBinding<P, T>> binding) {
            this.path = path;
            this.binding = binding;
        }

        public static <T, P extends Path<? extends T>> PathAndBinding<P, T> withPath(P path) {
            return new PathAndBinding<P, T>(Optional.of(path), Optional.empty());
        }

        public static <T, S extends Path<? extends T>> PathAndBinding<S, T> withoutPath() {
            return new PathAndBinding(Optional.empty(), Optional.empty());
        }

        public PathAndBinding<P, T> with(MultiValueBinding<P, T> binding) {
            return new PathAndBinding<P, T>(this.path, Optional.of(binding));
        }

        public Optional<Path<?>> getPath() {
            return this.path;
        }

        public Optional<MultiValueBinding<P, T>> getBinding() {
            return this.binding;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof PathAndBinding)) {
                return false;
            }
            PathAndBinding that = (PathAndBinding)o;
            if (!ObjectUtils.nullSafeEquals(this.path, that.path)) {
                return false;
            }
            return ObjectUtils.nullSafeEquals(this.binding, that.binding);
        }

        public int hashCode() {
            int result = ObjectUtils.nullSafeHashCode(this.path);
            result = 31 * result + ObjectUtils.nullSafeHashCode(this.binding);
            return result;
        }

        public String toString() {
            return "QuerydslBindings.PathAndBinding(path=" + this.getPath() + ", binding=" + this.getBinding() + ")";
        }
    }

    public final class TypeBinder<T> {
        private final Class<T> type;

        public TypeBinder(Class<T> type) {
            this.type = type;
        }

        public <P extends Path<T>> void firstOptional(OptionalValueBinding<P, T> binding) {
            Assert.notNull(binding, (String)"Binding must not be null!");
            this.all((path, value) -> binding.bind(path, Optionals.next(value.iterator())));
        }

        public <P extends Path<T>> void first(SingleValueBinding<P, T> binding) {
            Assert.notNull(binding, (String)"Binding must not be null!");
            this.all((path, value) -> Optionals.next(value.iterator()).map(t -> binding.bind(path, t)));
        }

        public <P extends Path<T>> void all(MultiValueBinding<P, T> binding) {
            Assert.notNull(binding, (String)"Binding must not be null!");
            QuerydslBindings.this.typeSpecs.put(this.type, PathAndBinding.withoutPath().with(binding));
        }
    }

    public static class AliasingPathBinder<P extends Path<? extends T>, T>
    extends PathBinder<P, T> {
        @Nullable
        private final String alias;
        private final P path;
        final /* synthetic */ QuerydslBindings this$0;

        AliasingPathBinder(P path) {
            this(this$0, null, (Path)path);
        }

        private AliasingPathBinder(String alias, P path) {
            this.this$0 = this$0;
            super((QuerydslBindings)this$0, new Path[]{path});
            Assert.notNull(path, (String)"Path must not be null!");
            this.alias = alias;
            this.path = path;
        }

        public AliasingPathBinder<P, T> as(String alias) {
            Assert.hasText((String)alias, (String)"Alias must not be null or empty!");
            return new AliasingPathBinder(this.this$0, alias, this.path);
        }

        public void withDefaultBinding() {
            this.registerBinding(PathAndBinding.withPath(this.path));
        }

        @Override
        protected void registerBinding(PathAndBinding<P, T> binding) {
            super.registerBinding(binding);
            String dotPath = QuerydslBindings.toDotPath(binding.getPath());
            if (this.alias != null) {
                this.this$0.pathSpecs.put(this.alias, binding);
                this.this$0.aliases.add(this.alias);
                this.this$0.denyList.add(dotPath);
            }
        }
    }

    public static class PathBinder<P extends Path<? extends T>, T> {
        private final List<P> paths;
        final /* synthetic */ QuerydslBindings this$0;

        @SafeVarargs
        PathBinder(P ... paths) {
            this.this$0 = this$0;
            Assert.notEmpty((Object[])paths, (String)"At least one path has to be provided!");
            this.paths = Arrays.asList(paths);
        }

        public void firstOptional(OptionalValueBinding<P, T> binding) {
            Assert.notNull(binding, (String)"Binding must not be null!");
            this.all((path, value) -> binding.bind(path, Optionals.next(value.iterator())));
        }

        public void first(SingleValueBinding<P, T> binding) {
            Assert.notNull(binding, (String)"Binding must not be null!");
            this.all((path, value) -> Optionals.next(value.iterator()).map(t -> binding.bind(path, t)));
        }

        public void all(MultiValueBinding<P, T> binding) {
            Assert.notNull(binding, (String)"Binding must not be null!");
            this.paths.forEach(path -> this.registerBinding(PathAndBinding.withPath(path).with(binding)));
        }

        protected void registerBinding(PathAndBinding<P, T> binding) {
            this.this$0.pathSpecs.put(QuerydslBindings.createKey(binding.getPath()), binding);
        }
    }
}

