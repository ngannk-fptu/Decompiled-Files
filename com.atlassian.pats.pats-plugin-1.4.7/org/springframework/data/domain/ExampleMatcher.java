/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ObjectUtils
 */
package org.springframework.data.domain;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import org.springframework.data.domain.TypedExampleMatcher;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

public interface ExampleMatcher {
    public static ExampleMatcher matching() {
        return ExampleMatcher.matchingAll();
    }

    public static ExampleMatcher matchingAny() {
        return new TypedExampleMatcher().withMode(MatchMode.ANY);
    }

    public static ExampleMatcher matchingAll() {
        return new TypedExampleMatcher().withMode(MatchMode.ALL);
    }

    public ExampleMatcher withIgnorePaths(String ... var1);

    public ExampleMatcher withStringMatcher(StringMatcher var1);

    default public ExampleMatcher withIgnoreCase() {
        return this.withIgnoreCase(true);
    }

    public ExampleMatcher withIgnoreCase(boolean var1);

    default public ExampleMatcher withMatcher(String propertyPath, MatcherConfigurer<GenericPropertyMatcher> matcherConfigurer) {
        Assert.hasText((String)propertyPath, (String)"PropertyPath must not be empty!");
        Assert.notNull(matcherConfigurer, (String)"MatcherConfigurer must not be empty!");
        GenericPropertyMatcher genericPropertyMatcher = new GenericPropertyMatcher();
        matcherConfigurer.configureMatcher(genericPropertyMatcher);
        return this.withMatcher(propertyPath, genericPropertyMatcher);
    }

    public ExampleMatcher withMatcher(String var1, GenericPropertyMatcher var2);

    public ExampleMatcher withTransformer(String var1, PropertyValueTransformer var2);

    public ExampleMatcher withIgnoreCase(String ... var1);

    default public ExampleMatcher withIncludeNullValues() {
        return this.withNullHandler(NullHandler.INCLUDE);
    }

    default public ExampleMatcher withIgnoreNullValues() {
        return this.withNullHandler(NullHandler.IGNORE);
    }

    public ExampleMatcher withNullHandler(NullHandler var1);

    public NullHandler getNullHandler();

    public StringMatcher getDefaultStringMatcher();

    public boolean isIgnoreCaseEnabled();

    default public boolean isIgnoredPath(String path) {
        return this.getIgnoredPaths().contains(path);
    }

    public Set<String> getIgnoredPaths();

    public PropertySpecifiers getPropertySpecifiers();

    default public boolean isAllMatching() {
        return this.getMatchMode().equals((Object)MatchMode.ALL);
    }

    default public boolean isAnyMatching() {
        return this.getMatchMode().equals((Object)MatchMode.ANY);
    }

    public MatchMode getMatchMode();

    public static enum MatchMode {
        ALL,
        ANY;

    }

    public static class PropertySpecifiers {
        private final Map<String, PropertySpecifier> propertySpecifiers = new LinkedHashMap<String, PropertySpecifier>();

        PropertySpecifiers() {
        }

        PropertySpecifiers(PropertySpecifiers propertySpecifiers) {
            this.propertySpecifiers.putAll(propertySpecifiers.propertySpecifiers);
        }

        public void add(PropertySpecifier specifier) {
            Assert.notNull((Object)specifier, (String)"PropertySpecifier must not be null!");
            this.propertySpecifiers.put(specifier.getPath(), specifier);
        }

        public boolean hasSpecifierForPath(String path) {
            return this.propertySpecifiers.containsKey(path);
        }

        public PropertySpecifier getForPath(String path) {
            return this.propertySpecifiers.get(path);
        }

        public boolean hasValues() {
            return !this.propertySpecifiers.isEmpty();
        }

        public Collection<PropertySpecifier> getSpecifiers() {
            return this.propertySpecifiers.values();
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof PropertySpecifiers)) {
                return false;
            }
            PropertySpecifiers that = (PropertySpecifiers)o;
            return ObjectUtils.nullSafeEquals(this.propertySpecifiers, that.propertySpecifiers);
        }

        public int hashCode() {
            return ObjectUtils.nullSafeHashCode(this.propertySpecifiers);
        }
    }

    public static class PropertySpecifier {
        private final String path;
        @Nullable
        private final StringMatcher stringMatcher;
        @Nullable
        private final Boolean ignoreCase;
        private final PropertyValueTransformer valueTransformer;

        PropertySpecifier(String path) {
            Assert.hasText((String)path, (String)"Path must not be null/empty!");
            this.path = path;
            this.stringMatcher = null;
            this.ignoreCase = null;
            this.valueTransformer = NoOpPropertyValueTransformer.INSTANCE;
        }

        private PropertySpecifier(String path, @Nullable StringMatcher stringMatcher, @Nullable Boolean ignoreCase, PropertyValueTransformer valueTransformer) {
            this.path = path;
            this.stringMatcher = stringMatcher;
            this.ignoreCase = ignoreCase;
            this.valueTransformer = valueTransformer;
        }

        public PropertySpecifier withStringMatcher(StringMatcher stringMatcher) {
            Assert.notNull((Object)((Object)stringMatcher), (String)"StringMatcher must not be null!");
            return new PropertySpecifier(this.path, stringMatcher, this.ignoreCase, this.valueTransformer);
        }

        public PropertySpecifier withIgnoreCase(boolean ignoreCase) {
            return new PropertySpecifier(this.path, this.stringMatcher, ignoreCase, this.valueTransformer);
        }

        public PropertySpecifier withValueTransformer(PropertyValueTransformer valueTransformer) {
            Assert.notNull((Object)valueTransformer, (String)"PropertyValueTransformer must not be null!");
            return new PropertySpecifier(this.path, this.stringMatcher, this.ignoreCase, valueTransformer);
        }

        public String getPath() {
            return this.path;
        }

        @Nullable
        public StringMatcher getStringMatcher() {
            return this.stringMatcher;
        }

        @Nullable
        public Boolean getIgnoreCase() {
            return this.ignoreCase;
        }

        public PropertyValueTransformer getPropertyValueTransformer() {
            return this.valueTransformer == null ? NoOpPropertyValueTransformer.INSTANCE : this.valueTransformer;
        }

        public Optional<Object> transformValue(Optional<Object> source) {
            return (Optional)this.getPropertyValueTransformer().apply(source);
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof PropertySpecifier)) {
                return false;
            }
            PropertySpecifier that = (PropertySpecifier)o;
            if (!ObjectUtils.nullSafeEquals((Object)this.path, (Object)that.path)) {
                return false;
            }
            if (this.stringMatcher != that.stringMatcher) {
                return false;
            }
            if (!ObjectUtils.nullSafeEquals((Object)this.ignoreCase, (Object)that.ignoreCase)) {
                return false;
            }
            return ObjectUtils.nullSafeEquals((Object)this.valueTransformer, (Object)that.valueTransformer);
        }

        public int hashCode() {
            int result = ObjectUtils.nullSafeHashCode((Object)this.path);
            result = 31 * result + ObjectUtils.nullSafeHashCode((Object)((Object)this.stringMatcher));
            result = 31 * result + ObjectUtils.nullSafeHashCode((Object)this.ignoreCase);
            result = 31 * result + ObjectUtils.nullSafeHashCode((Object)this.valueTransformer);
            return result;
        }

        protected boolean canEqual(Object other) {
            return other instanceof PropertySpecifier;
        }
    }

    public static enum NoOpPropertyValueTransformer implements PropertyValueTransformer
    {
        INSTANCE;


        @Override
        public Optional<Object> apply(Optional<Object> source) {
            return source;
        }
    }

    public static interface PropertyValueTransformer
    extends Function<Optional<Object>, Optional<Object>> {
    }

    public static enum StringMatcher {
        DEFAULT,
        EXACT,
        STARTING,
        ENDING,
        CONTAINING,
        REGEX;

    }

    public static class GenericPropertyMatchers {
        public static GenericPropertyMatcher ignoreCase() {
            return new GenericPropertyMatcher().ignoreCase();
        }

        public static GenericPropertyMatcher caseSensitive() {
            return new GenericPropertyMatcher().caseSensitive();
        }

        public static GenericPropertyMatcher contains() {
            return new GenericPropertyMatcher().contains();
        }

        public static GenericPropertyMatcher endsWith() {
            return new GenericPropertyMatcher().endsWith();
        }

        public static GenericPropertyMatcher startsWith() {
            return new GenericPropertyMatcher().startsWith();
        }

        public static GenericPropertyMatcher exact() {
            return new GenericPropertyMatcher().exact();
        }

        public static GenericPropertyMatcher storeDefaultMatching() {
            return new GenericPropertyMatcher().storeDefaultMatching();
        }

        public static GenericPropertyMatcher regex() {
            return new GenericPropertyMatcher().regex();
        }
    }

    public static class GenericPropertyMatcher {
        @Nullable
        StringMatcher stringMatcher = null;
        @Nullable
        Boolean ignoreCase = null;
        PropertyValueTransformer valueTransformer = NoOpPropertyValueTransformer.INSTANCE;

        public static GenericPropertyMatcher of(StringMatcher stringMatcher, boolean ignoreCase) {
            return new GenericPropertyMatcher().stringMatcher(stringMatcher).ignoreCase(ignoreCase);
        }

        public static GenericPropertyMatcher of(StringMatcher stringMatcher) {
            return new GenericPropertyMatcher().stringMatcher(stringMatcher);
        }

        public GenericPropertyMatcher ignoreCase() {
            this.ignoreCase = true;
            return this;
        }

        public GenericPropertyMatcher ignoreCase(boolean ignoreCase) {
            this.ignoreCase = ignoreCase;
            return this;
        }

        public GenericPropertyMatcher caseSensitive() {
            this.ignoreCase = false;
            return this;
        }

        public GenericPropertyMatcher contains() {
            this.stringMatcher = StringMatcher.CONTAINING;
            return this;
        }

        public GenericPropertyMatcher endsWith() {
            this.stringMatcher = StringMatcher.ENDING;
            return this;
        }

        public GenericPropertyMatcher startsWith() {
            this.stringMatcher = StringMatcher.STARTING;
            return this;
        }

        public GenericPropertyMatcher exact() {
            this.stringMatcher = StringMatcher.EXACT;
            return this;
        }

        public GenericPropertyMatcher storeDefaultMatching() {
            this.stringMatcher = StringMatcher.DEFAULT;
            return this;
        }

        public GenericPropertyMatcher regex() {
            this.stringMatcher = StringMatcher.REGEX;
            return this;
        }

        public GenericPropertyMatcher stringMatcher(StringMatcher stringMatcher) {
            Assert.notNull((Object)((Object)stringMatcher), (String)"StringMatcher must not be null!");
            this.stringMatcher = stringMatcher;
            return this;
        }

        public GenericPropertyMatcher transform(PropertyValueTransformer propertyValueTransformer) {
            Assert.notNull((Object)propertyValueTransformer, (String)"PropertyValueTransformer must not be null!");
            this.valueTransformer = propertyValueTransformer;
            return this;
        }

        protected boolean canEqual(Object other) {
            return other instanceof GenericPropertyMatcher;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof GenericPropertyMatcher)) {
                return false;
            }
            GenericPropertyMatcher that = (GenericPropertyMatcher)o;
            if (this.stringMatcher != that.stringMatcher) {
                return false;
            }
            if (!ObjectUtils.nullSafeEquals((Object)this.ignoreCase, (Object)that.ignoreCase)) {
                return false;
            }
            return ObjectUtils.nullSafeEquals((Object)this.valueTransformer, (Object)that.valueTransformer);
        }

        public int hashCode() {
            int result = ObjectUtils.nullSafeHashCode((Object)((Object)this.stringMatcher));
            result = 31 * result + ObjectUtils.nullSafeHashCode((Object)this.ignoreCase);
            result = 31 * result + ObjectUtils.nullSafeHashCode((Object)this.valueTransformer);
            return result;
        }
    }

    public static interface MatcherConfigurer<T> {
        public void configureMatcher(T var1);
    }

    public static enum NullHandler {
        INCLUDE,
        IGNORE;

    }
}

