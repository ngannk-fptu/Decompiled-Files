/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.Assert
 *  org.springframework.util.ObjectUtils
 */
package org.springframework.data.domain;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

class TypedExampleMatcher
implements ExampleMatcher {
    private final ExampleMatcher.NullHandler nullHandler;
    private final ExampleMatcher.StringMatcher defaultStringMatcher;
    private final ExampleMatcher.PropertySpecifiers propertySpecifiers;
    private final Set<String> ignoredPaths;
    private final boolean defaultIgnoreCase;
    private final ExampleMatcher.MatchMode mode;

    TypedExampleMatcher() {
        this(ExampleMatcher.NullHandler.IGNORE, ExampleMatcher.StringMatcher.DEFAULT, new ExampleMatcher.PropertySpecifiers(), Collections.emptySet(), false, ExampleMatcher.MatchMode.ALL);
    }

    private TypedExampleMatcher(ExampleMatcher.NullHandler nullHandler, ExampleMatcher.StringMatcher defaultStringMatcher, ExampleMatcher.PropertySpecifiers propertySpecifiers, Set<String> ignoredPaths, boolean defaultIgnoreCase, ExampleMatcher.MatchMode mode) {
        this.nullHandler = nullHandler;
        this.defaultStringMatcher = defaultStringMatcher;
        this.propertySpecifiers = propertySpecifiers;
        this.ignoredPaths = ignoredPaths;
        this.defaultIgnoreCase = defaultIgnoreCase;
        this.mode = mode;
    }

    @Override
    public ExampleMatcher withIgnorePaths(String ... ignoredPaths) {
        Assert.notEmpty((Object[])ignoredPaths, (String)"IgnoredPaths must not be empty!");
        Assert.noNullElements((Object[])ignoredPaths, (String)"IgnoredPaths must not contain null elements!");
        LinkedHashSet<String> newIgnoredPaths = new LinkedHashSet<String>(this.ignoredPaths);
        newIgnoredPaths.addAll(Arrays.asList(ignoredPaths));
        return new TypedExampleMatcher(this.nullHandler, this.defaultStringMatcher, this.propertySpecifiers, newIgnoredPaths, this.defaultIgnoreCase, this.mode);
    }

    @Override
    public ExampleMatcher withStringMatcher(ExampleMatcher.StringMatcher defaultStringMatcher) {
        Assert.notNull(this.ignoredPaths, (String)"DefaultStringMatcher must not be empty!");
        return new TypedExampleMatcher(this.nullHandler, defaultStringMatcher, this.propertySpecifiers, this.ignoredPaths, this.defaultIgnoreCase, this.mode);
    }

    @Override
    public ExampleMatcher withIgnoreCase(boolean defaultIgnoreCase) {
        return new TypedExampleMatcher(this.nullHandler, this.defaultStringMatcher, this.propertySpecifiers, this.ignoredPaths, defaultIgnoreCase, this.mode);
    }

    @Override
    public ExampleMatcher withMatcher(String propertyPath, ExampleMatcher.GenericPropertyMatcher genericPropertyMatcher) {
        Assert.hasText((String)propertyPath, (String)"PropertyPath must not be empty!");
        Assert.notNull((Object)genericPropertyMatcher, (String)"GenericPropertyMatcher must not be empty!");
        ExampleMatcher.PropertySpecifiers propertySpecifiers = new ExampleMatcher.PropertySpecifiers(this.propertySpecifiers);
        ExampleMatcher.PropertySpecifier propertySpecifier = new ExampleMatcher.PropertySpecifier(propertyPath);
        if (genericPropertyMatcher.ignoreCase != null) {
            propertySpecifier = propertySpecifier.withIgnoreCase(genericPropertyMatcher.ignoreCase);
        }
        if (genericPropertyMatcher.stringMatcher != null) {
            propertySpecifier = propertySpecifier.withStringMatcher(genericPropertyMatcher.stringMatcher);
        }
        propertySpecifier = propertySpecifier.withValueTransformer(genericPropertyMatcher.valueTransformer);
        propertySpecifiers.add(propertySpecifier);
        return new TypedExampleMatcher(this.nullHandler, this.defaultStringMatcher, propertySpecifiers, this.ignoredPaths, this.defaultIgnoreCase, this.mode);
    }

    @Override
    public ExampleMatcher withTransformer(String propertyPath, ExampleMatcher.PropertyValueTransformer propertyValueTransformer) {
        Assert.hasText((String)propertyPath, (String)"PropertyPath must not be empty!");
        Assert.notNull((Object)propertyValueTransformer, (String)"PropertyValueTransformer must not be empty!");
        ExampleMatcher.PropertySpecifiers propertySpecifiers = new ExampleMatcher.PropertySpecifiers(this.propertySpecifiers);
        ExampleMatcher.PropertySpecifier propertySpecifier = this.getOrCreatePropertySpecifier(propertyPath, propertySpecifiers);
        propertySpecifiers.add(propertySpecifier.withValueTransformer(propertyValueTransformer));
        return new TypedExampleMatcher(this.nullHandler, this.defaultStringMatcher, propertySpecifiers, this.ignoredPaths, this.defaultIgnoreCase, this.mode);
    }

    @Override
    public ExampleMatcher withIgnoreCase(String ... propertyPaths) {
        Assert.notEmpty((Object[])propertyPaths, (String)"PropertyPaths must not be empty!");
        Assert.noNullElements((Object[])propertyPaths, (String)"PropertyPaths must not contain null elements!");
        ExampleMatcher.PropertySpecifiers propertySpecifiers = new ExampleMatcher.PropertySpecifiers(this.propertySpecifiers);
        for (String propertyPath : propertyPaths) {
            ExampleMatcher.PropertySpecifier propertySpecifier = this.getOrCreatePropertySpecifier(propertyPath, propertySpecifiers);
            propertySpecifiers.add(propertySpecifier.withIgnoreCase(true));
        }
        return new TypedExampleMatcher(this.nullHandler, this.defaultStringMatcher, propertySpecifiers, this.ignoredPaths, this.defaultIgnoreCase, this.mode);
    }

    @Override
    public ExampleMatcher withNullHandler(ExampleMatcher.NullHandler nullHandler) {
        Assert.notNull((Object)((Object)nullHandler), (String)"NullHandler must not be null!");
        return new TypedExampleMatcher(nullHandler, this.defaultStringMatcher, this.propertySpecifiers, this.ignoredPaths, this.defaultIgnoreCase, this.mode);
    }

    @Override
    public ExampleMatcher.NullHandler getNullHandler() {
        return this.nullHandler;
    }

    @Override
    public ExampleMatcher.StringMatcher getDefaultStringMatcher() {
        return this.defaultStringMatcher;
    }

    @Override
    public boolean isIgnoreCaseEnabled() {
        return this.defaultIgnoreCase;
    }

    @Override
    public Set<String> getIgnoredPaths() {
        return this.ignoredPaths;
    }

    @Override
    public ExampleMatcher.PropertySpecifiers getPropertySpecifiers() {
        return this.propertySpecifiers;
    }

    @Override
    public ExampleMatcher.MatchMode getMatchMode() {
        return this.mode;
    }

    TypedExampleMatcher withMode(ExampleMatcher.MatchMode mode) {
        return this.mode == mode ? this : new TypedExampleMatcher(this.nullHandler, this.defaultStringMatcher, this.propertySpecifiers, this.ignoredPaths, this.defaultIgnoreCase, mode);
    }

    private ExampleMatcher.PropertySpecifier getOrCreatePropertySpecifier(String propertyPath, ExampleMatcher.PropertySpecifiers propertySpecifiers) {
        if (propertySpecifiers.hasSpecifierForPath(propertyPath)) {
            return propertySpecifiers.getForPath(propertyPath);
        }
        return new ExampleMatcher.PropertySpecifier(propertyPath);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TypedExampleMatcher)) {
            return false;
        }
        TypedExampleMatcher that = (TypedExampleMatcher)o;
        if (this.defaultIgnoreCase != that.defaultIgnoreCase) {
            return false;
        }
        if (this.nullHandler != that.nullHandler) {
            return false;
        }
        if (this.defaultStringMatcher != that.defaultStringMatcher) {
            return false;
        }
        if (!ObjectUtils.nullSafeEquals((Object)this.propertySpecifiers, (Object)that.propertySpecifiers)) {
            return false;
        }
        if (!ObjectUtils.nullSafeEquals(this.ignoredPaths, that.ignoredPaths)) {
            return false;
        }
        return this.mode == that.mode;
    }

    public int hashCode() {
        int result = ObjectUtils.nullSafeHashCode((Object)((Object)this.nullHandler));
        result = 31 * result + ObjectUtils.nullSafeHashCode((Object)((Object)this.defaultStringMatcher));
        result = 31 * result + ObjectUtils.nullSafeHashCode((Object)this.propertySpecifiers);
        result = 31 * result + ObjectUtils.nullSafeHashCode(this.ignoredPaths);
        result = 31 * result + (this.defaultIgnoreCase ? 1 : 0);
        result = 31 * result + ObjectUtils.nullSafeHashCode((Object)((Object)this.mode));
        return result;
    }

    public String toString() {
        return "TypedExampleMatcher{nullHandler=" + (Object)((Object)this.nullHandler) + ", defaultStringMatcher=" + (Object)((Object)this.defaultStringMatcher) + ", propertySpecifiers=" + this.propertySpecifiers + ", ignoredPaths=" + this.ignoredPaths + ", defaultIgnoreCase=" + this.defaultIgnoreCase + ", mode=" + (Object)((Object)this.mode) + '}';
    }
}

