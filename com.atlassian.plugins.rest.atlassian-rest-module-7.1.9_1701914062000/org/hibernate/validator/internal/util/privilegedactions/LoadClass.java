/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.util.privilegedactions;

import java.lang.invoke.MethodHandles;
import java.security.PrivilegedAction;
import org.hibernate.validator.HibernateValidator;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;

public final class LoadClass
implements PrivilegedAction<Class<?>> {
    private static final Log LOG = LoggerFactory.make(MethodHandles.lookup());
    private static final String HIBERNATE_VALIDATOR_CLASS_NAME = "org.hibernate.validator";
    private final String className;
    private final ClassLoader classLoader;
    private final ClassLoader initialThreadContextClassLoader;
    private final boolean fallbackOnTCCL;

    public static LoadClass action(String className, ClassLoader classLoader) {
        return LoadClass.action(className, classLoader, true);
    }

    public static LoadClass action(String className, ClassLoader classLoader, boolean fallbackOnTCCL) {
        return new LoadClass(className, classLoader, null, fallbackOnTCCL);
    }

    public static LoadClass action(String className, ClassLoader classLoader, ClassLoader initialThreadContextClassLoader) {
        return new LoadClass(className, classLoader, initialThreadContextClassLoader, true);
    }

    private LoadClass(String className, ClassLoader classLoader, ClassLoader initialThreadContextClassLoader, boolean fallbackOnTCCL) {
        this.className = className;
        this.classLoader = classLoader;
        this.initialThreadContextClassLoader = initialThreadContextClassLoader;
        this.fallbackOnTCCL = fallbackOnTCCL;
    }

    @Override
    public Class<?> run() {
        if (this.className.startsWith(HIBERNATE_VALIDATOR_CLASS_NAME)) {
            return this.loadClassInValidatorNameSpace();
        }
        return this.loadNonValidatorClass();
    }

    private Class<?> loadClassInValidatorNameSpace() {
        Exception exception;
        ClassLoader loader = HibernateValidator.class.getClassLoader();
        try {
            return Class.forName(this.className, true, HibernateValidator.class.getClassLoader());
        }
        catch (ClassNotFoundException e) {
            exception = e;
        }
        catch (RuntimeException e) {
            exception = e;
        }
        if (this.fallbackOnTCCL) {
            ClassLoader contextClassLoader;
            ClassLoader classLoader = contextClassLoader = this.initialThreadContextClassLoader != null ? this.initialThreadContextClassLoader : Thread.currentThread().getContextClassLoader();
            if (contextClassLoader != null) {
                try {
                    return Class.forName(this.className, false, contextClassLoader);
                }
                catch (ClassNotFoundException e) {
                    throw LOG.getUnableToLoadClassException(this.className, contextClassLoader, e);
                }
            }
            throw LOG.getUnableToLoadClassException(this.className, loader, exception);
        }
        throw LOG.getUnableToLoadClassException(this.className, loader, exception);
    }

    private Class<?> loadNonValidatorClass() {
        Exception exception = null;
        if (this.classLoader != null) {
            try {
                return Class.forName(this.className, false, this.classLoader);
            }
            catch (ClassNotFoundException e) {
                exception = e;
            }
            catch (RuntimeException e) {
                exception = e;
            }
        }
        if (this.fallbackOnTCCL) {
            try {
                ClassLoader contextClassLoader;
                ClassLoader classLoader = contextClassLoader = this.initialThreadContextClassLoader != null ? this.initialThreadContextClassLoader : Thread.currentThread().getContextClassLoader();
                if (contextClassLoader != null) {
                    return Class.forName(this.className, false, contextClassLoader);
                }
            }
            catch (ClassNotFoundException contextClassLoader) {
            }
            catch (RuntimeException contextClassLoader) {
                // empty catch block
            }
            ClassLoader loader = LoadClass.class.getClassLoader();
            try {
                return Class.forName(this.className, true, loader);
            }
            catch (ClassNotFoundException e) {
                throw LOG.getUnableToLoadClassException(this.className, loader, e);
            }
        }
        throw LOG.getUnableToLoadClassException(this.className, this.classLoader, exception);
    }
}

