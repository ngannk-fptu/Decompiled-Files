/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.bytebuddy.dynamic.loading.ClassInjector$UsingLookup
 *  net.bytebuddy.dynamic.loading.ClassLoadingStrategy
 *  net.bytebuddy.dynamic.loading.ClassLoadingStrategy$ForUnsafeInjection
 *  net.bytebuddy.dynamic.loading.ClassLoadingStrategy$UsingLookup
 */
package org.hibernate.bytecode.spi;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import net.bytebuddy.dynamic.loading.ClassInjector;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import org.hibernate.HibernateException;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;

public class ClassLoadingStrategyHelper {
    private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();
    private static final CoreMessageLogger LOG = CoreLogging.messageLogger(ClassLoadingStrategyHelper.class);

    public static ClassLoadingStrategy<ClassLoader> resolveClassLoadingStrategy(Class<?> originalClass) {
        Method privateLookupIn;
        if (!ClassInjector.UsingLookup.isAvailable()) {
            return new ClassLoadingStrategy.ForUnsafeInjection(originalClass.getProtectionDomain());
        }
        try {
            privateLookupIn = MethodHandles.class.getMethod("privateLookupIn", Class.class, MethodHandles.Lookup.class);
        }
        catch (Exception e) {
            throw new HibernateException(LOG.bytecodeEnhancementFailed(originalClass.getName()), e);
        }
        try {
            Object privateLookup;
            try {
                privateLookup = privateLookupIn.invoke(null, originalClass, LOOKUP);
            }
            catch (InvocationTargetException exception) {
                if (exception.getCause() instanceof IllegalAccessException) {
                    return new ClassLoadingStrategy.ForUnsafeInjection(originalClass.getProtectionDomain());
                }
                throw new HibernateException(LOG.bytecodeEnhancementFailed(originalClass.getName()), exception.getCause());
            }
            return ClassLoadingStrategy.UsingLookup.of((Object)privateLookup);
        }
        catch (Throwable e) {
            throw new HibernateException(LOG.bytecodeEnhancementFailedUnableToGetPrivateLookupFor(originalClass.getName()), e);
        }
    }
}

