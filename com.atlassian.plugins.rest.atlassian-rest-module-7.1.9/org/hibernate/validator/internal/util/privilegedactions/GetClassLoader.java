/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.util.privilegedactions;

import java.security.PrivilegedAction;
import org.hibernate.validator.internal.util.Contracts;
import org.hibernate.validator.internal.util.logging.Messages;

public final class GetClassLoader
implements PrivilegedAction<ClassLoader> {
    private static final GetClassLoader CONTEXT = new GetClassLoader(null);
    private final Class<?> clazz;

    public static GetClassLoader fromContext() {
        return CONTEXT;
    }

    public static GetClassLoader fromClass(Class<?> clazz) {
        Contracts.assertNotNull(clazz, Messages.MESSAGES.classIsNull());
        return new GetClassLoader(clazz);
    }

    private GetClassLoader(Class<?> clazz) {
        this.clazz = clazz;
    }

    @Override
    public ClassLoader run() {
        if (this.clazz != null) {
            return this.clazz.getClassLoader();
        }
        return Thread.currentThread().getContextClassLoader();
    }
}

