/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.data.domain;

import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.TypedExample;
import org.springframework.data.util.ProxyUtils;

public interface Example<T> {
    public static <T> Example<T> of(T probe) {
        return new TypedExample<T>(probe, ExampleMatcher.matching());
    }

    public static <T> Example<T> of(T probe, ExampleMatcher matcher) {
        return new TypedExample<T>(probe, matcher);
    }

    public T getProbe();

    public ExampleMatcher getMatcher();

    default public Class<T> getProbeType() {
        return ProxyUtils.getUserClass(this.getProbe().getClass());
    }
}

