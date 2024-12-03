/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.security.serialfilter;

import java.io.ObjectInputFilter;
import java.util.function.Predicate;

class DeserializationFilter
implements ObjectInputFilter {
    private final Predicate<Class<?>> deserializationTest;

    public DeserializationFilter(Predicate<Class<?>> deserializationTest) {
        this.deserializationTest = deserializationTest;
    }

    @Override
    public ObjectInputFilter.Status checkInput(ObjectInputFilter.FilterInfo filterInfo) {
        return this.deserializationTest.test(filterInfo.serialClass()) ? ObjectInputFilter.Status.ALLOWED : ObjectInputFilter.Status.REJECTED;
    }

    public void register() {
        ObjectInputFilter.Config.setSerialFilter(this);
    }
}

