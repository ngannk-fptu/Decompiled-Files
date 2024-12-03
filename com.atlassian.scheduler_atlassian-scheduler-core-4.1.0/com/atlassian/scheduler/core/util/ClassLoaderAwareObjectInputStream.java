/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.scheduler.core.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.util.Objects;

public class ClassLoaderAwareObjectInputStream
extends ObjectInputStream {
    private final ClassLoader classLoader;

    public ClassLoaderAwareObjectInputStream(ClassLoader classLoader, byte[] parameters) throws IOException {
        super(new ByteArrayInputStream(Objects.requireNonNull(parameters, "parameters")));
        this.classLoader = Objects.requireNonNull(classLoader, "classLoader");
    }

    @Override
    protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
        try {
            return Class.forName(desc.getName(), false, this.classLoader);
        }
        catch (ClassNotFoundException originalEx) {
            try {
                return super.resolveClass(desc);
            }
            catch (ClassNotFoundException ignoredEx) {
                throw originalEx;
            }
        }
    }
}

