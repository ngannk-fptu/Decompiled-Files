/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.scheduler.core.util.ClassLoaderAwareObjectInputStream
 */
package com.atlassian.confluence.impl.schedule.caesium;

import com.atlassian.scheduler.core.util.ClassLoaderAwareObjectInputStream;
import java.io.IOException;
import java.io.ObjectStreamClass;
import java.util.function.Predicate;

class SecureClassLoaderAwareObjectInputStream
extends ClassLoaderAwareObjectInputStream {
    private final Predicate<String> validator;

    public SecureClassLoaderAwareObjectInputStream(ClassLoader classLoader, byte[] parameters, Predicate<String> validator) throws IOException {
        super(classLoader, parameters);
        this.validator = validator;
    }

    protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
        String className = desc.getName();
        if (!this.validator.test(className)) {
            throw new DisallowedClassException(className);
        }
        return super.resolveClass(desc);
    }

    static class DisallowedClassException
    extends RuntimeException {
        private final String disallowedClass;

        DisallowedClassException(String disallowedClass) {
            super("Unexpected class in serialized data: " + disallowedClass);
            this.disallowedClass = disallowedClass;
        }

        String getDisallowedClass() {
            return this.disallowedClass;
        }
    }
}

