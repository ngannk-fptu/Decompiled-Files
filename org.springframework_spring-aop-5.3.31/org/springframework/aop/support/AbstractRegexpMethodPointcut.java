/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ClassUtils
 *  org.springframework.util.ObjectUtils
 *  org.springframework.util.StringUtils
 */
package org.springframework.aop.support;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Arrays;
import org.springframework.aop.support.StaticMethodMatcherPointcut;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

public abstract class AbstractRegexpMethodPointcut
extends StaticMethodMatcherPointcut
implements Serializable {
    private String[] patterns = new String[0];
    private String[] excludedPatterns = new String[0];

    public void setPattern(String pattern) {
        this.setPatterns(pattern);
    }

    public void setPatterns(String ... patterns) {
        Assert.notEmpty((Object[])patterns, (String)"'patterns' must not be empty");
        this.patterns = new String[patterns.length];
        for (int i = 0; i < patterns.length; ++i) {
            this.patterns[i] = StringUtils.trimWhitespace((String)patterns[i]);
        }
        this.initPatternRepresentation(this.patterns);
    }

    public String[] getPatterns() {
        return this.patterns;
    }

    public void setExcludedPattern(String excludedPattern) {
        this.setExcludedPatterns(excludedPattern);
    }

    public void setExcludedPatterns(String ... excludedPatterns) {
        Assert.notEmpty((Object[])excludedPatterns, (String)"'excludedPatterns' must not be empty");
        this.excludedPatterns = new String[excludedPatterns.length];
        for (int i = 0; i < excludedPatterns.length; ++i) {
            this.excludedPatterns[i] = StringUtils.trimWhitespace((String)excludedPatterns[i]);
        }
        this.initExcludedPatternRepresentation(this.excludedPatterns);
    }

    public String[] getExcludedPatterns() {
        return this.excludedPatterns;
    }

    @Override
    public boolean matches(Method method, Class<?> targetClass) {
        return this.matchesPattern(ClassUtils.getQualifiedMethodName((Method)method, targetClass)) || targetClass != method.getDeclaringClass() && this.matchesPattern(ClassUtils.getQualifiedMethodName((Method)method, method.getDeclaringClass()));
    }

    protected boolean matchesPattern(String signatureString) {
        for (int i = 0; i < this.patterns.length; ++i) {
            boolean matched = this.matches(signatureString, i);
            if (!matched) continue;
            for (int j = 0; j < this.excludedPatterns.length; ++j) {
                boolean excluded = this.matchesExclusion(signatureString, j);
                if (!excluded) continue;
                return false;
            }
            return true;
        }
        return false;
    }

    protected abstract void initPatternRepresentation(String[] var1) throws IllegalArgumentException;

    protected abstract void initExcludedPatternRepresentation(String[] var1) throws IllegalArgumentException;

    protected abstract boolean matches(String var1, int var2);

    protected abstract boolean matchesExclusion(String var1, int var2);

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof AbstractRegexpMethodPointcut)) {
            return false;
        }
        AbstractRegexpMethodPointcut otherPointcut = (AbstractRegexpMethodPointcut)other;
        return Arrays.equals(this.patterns, otherPointcut.patterns) && Arrays.equals(this.excludedPatterns, otherPointcut.excludedPatterns);
    }

    public int hashCode() {
        int result = 27;
        for (String pattern : this.patterns) {
            result = 13 * result + pattern.hashCode();
        }
        for (String excludedPattern : this.excludedPatterns) {
            result = 13 * result + excludedPattern.hashCode();
        }
        return result;
    }

    public String toString() {
        return this.getClass().getName() + ": patterns " + ObjectUtils.nullSafeToString((Object[])this.patterns) + ", excluded patterns " + ObjectUtils.nullSafeToString((Object[])this.excludedPatterns);
    }
}

