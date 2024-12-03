/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.data.support;

import java.util.Collection;
import org.springframework.data.domain.ExampleMatcher;

public class ExampleMatcherAccessor {
    private final ExampleMatcher matcher;

    public ExampleMatcherAccessor(ExampleMatcher matcher) {
        this.matcher = matcher;
    }

    public Collection<ExampleMatcher.PropertySpecifier> getPropertySpecifiers() {
        return this.matcher.getPropertySpecifiers().getSpecifiers();
    }

    public boolean hasPropertySpecifier(String path) {
        return this.matcher.getPropertySpecifiers().hasSpecifierForPath(path);
    }

    public ExampleMatcher.PropertySpecifier getPropertySpecifier(String path) {
        return this.matcher.getPropertySpecifiers().getForPath(path);
    }

    public boolean hasPropertySpecifiers() {
        return this.matcher.getPropertySpecifiers().hasValues();
    }

    public ExampleMatcher.StringMatcher getStringMatcherForPath(String path) {
        if (!this.hasPropertySpecifier(path)) {
            return this.matcher.getDefaultStringMatcher();
        }
        ExampleMatcher.PropertySpecifier specifier = this.getPropertySpecifier(path);
        ExampleMatcher.StringMatcher stringMatcher = specifier.getStringMatcher();
        return stringMatcher != null ? stringMatcher : this.matcher.getDefaultStringMatcher();
    }

    public ExampleMatcher.NullHandler getNullHandler() {
        return this.matcher.getNullHandler();
    }

    public ExampleMatcher.StringMatcher getDefaultStringMatcher() {
        return this.matcher.getDefaultStringMatcher();
    }

    public boolean isIgnoreCaseEnabled() {
        return this.matcher.isIgnoreCaseEnabled();
    }

    public boolean isIgnoredPath(String path) {
        return this.matcher.isIgnoredPath(path);
    }

    public boolean isIgnoreCaseForPath(String path) {
        if (!this.hasPropertySpecifier(path)) {
            return this.matcher.isIgnoreCaseEnabled();
        }
        ExampleMatcher.PropertySpecifier specifier = this.getPropertySpecifier(path);
        Boolean ignoreCase = specifier.getIgnoreCase();
        return ignoreCase != null ? ignoreCase.booleanValue() : this.matcher.isIgnoreCaseEnabled();
    }

    public ExampleMatcher.PropertyValueTransformer getValueTransformerForPath(String path) {
        if (!this.hasPropertySpecifier(path)) {
            return ExampleMatcher.NoOpPropertyValueTransformer.INSTANCE;
        }
        return this.getPropertySpecifier(path).getPropertyValueTransformer();
    }
}

