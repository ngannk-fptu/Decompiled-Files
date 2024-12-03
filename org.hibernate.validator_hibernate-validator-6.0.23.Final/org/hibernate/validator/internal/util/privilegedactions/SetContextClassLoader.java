/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.util.privilegedactions;

import java.security.PrivilegedAction;
import org.hibernate.validator.internal.util.Contracts;

public final class SetContextClassLoader
implements PrivilegedAction<Void> {
    private final ClassLoader classLoader;

    public static SetContextClassLoader action(ClassLoader classLoader) {
        Contracts.assertNotNull(classLoader, "class loader must not be null");
        return new SetContextClassLoader(classLoader);
    }

    private SetContextClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public Void run() {
        Thread.currentThread().setContextClassLoader(this.classLoader);
        return null;
    }
}

