/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.Assert
 *  org.springframework.util.ObjectUtils
 */
package org.springframework.data.domain;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

class TypedExample<T>
implements Example<T> {
    private final T probe;
    private final ExampleMatcher matcher;

    TypedExample(T probe, ExampleMatcher matcher) {
        Assert.notNull(probe, (String)"Probe must not be null");
        Assert.notNull((Object)matcher, (String)"ExampleMatcher must not be null");
        this.probe = probe;
        this.matcher = matcher;
    }

    @Override
    public T getProbe() {
        return this.probe;
    }

    @Override
    public ExampleMatcher getMatcher() {
        return this.matcher;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TypedExample)) {
            return false;
        }
        TypedExample that = (TypedExample)o;
        if (!ObjectUtils.nullSafeEquals(this.probe, that.probe)) {
            return false;
        }
        return ObjectUtils.nullSafeEquals((Object)this.matcher, (Object)that.matcher);
    }

    public int hashCode() {
        int result = ObjectUtils.nullSafeHashCode(this.probe);
        result = 31 * result + ObjectUtils.nullSafeHashCode((Object)this.matcher);
        return result;
    }

    public String toString() {
        return "TypedExample{probe=" + this.probe + ", matcher=" + this.matcher + '}';
    }
}

