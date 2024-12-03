/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package net.bytebuddy.dynamic.loading;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.utility.nullability.MaybeNull;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class InjectionClassLoader
extends ClassLoader {
    private final AtomicBoolean sealed;

    @SuppressFBWarnings(value={"DP_DO_INSIDE_DO_PRIVILEGED"}, justification="Must be invoked from targeting class loader type.")
    private static void doRegisterAsParallelCapable() {
        try {
            Method method = ClassLoader.class.getDeclaredMethod("registerAsParallelCapable", new Class[0]);
            method.setAccessible(true);
            method.invoke(null, new Object[0]);
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    protected InjectionClassLoader(@MaybeNull ClassLoader parent, boolean sealed) {
        super(parent);
        this.sealed = new AtomicBoolean(sealed);
    }

    public boolean isSealed() {
        return this.sealed.get();
    }

    public boolean seal() {
        return !this.sealed.getAndSet(true);
    }

    public Class<?> defineClass(String name, byte[] binaryRepresentation) throws ClassNotFoundException {
        return this.defineClasses(Collections.singletonMap(name, binaryRepresentation)).get(name);
    }

    public Map<String, Class<?>> defineClasses(Map<String, byte[]> typeDefinitions) throws ClassNotFoundException {
        if (this.sealed.get()) {
            throw new IllegalStateException("Cannot inject classes into a sealed class loader");
        }
        return this.doDefineClasses(typeDefinitions);
    }

    protected abstract Map<String, Class<?>> doDefineClasses(Map<String, byte[]> var1) throws ClassNotFoundException;

    static {
        InjectionClassLoader.doRegisterAsParallelCapable();
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum Strategy implements ClassLoadingStrategy<InjectionClassLoader>
    {
        INSTANCE;


        @Override
        public Map<TypeDescription, Class<?>> load(@MaybeNull InjectionClassLoader classLoader, Map<TypeDescription, byte[]> types) {
            if (classLoader == null) {
                throw new IllegalArgumentException("Cannot add types to bootstrap class loader: " + types);
            }
            LinkedHashMap<String, byte[]> typeDefinitions = new LinkedHashMap<String, byte[]>();
            HashMap<String, TypeDescription> typeDescriptions = new HashMap<String, TypeDescription>();
            for (Map.Entry<TypeDescription, byte[]> entry : types.entrySet()) {
                typeDefinitions.put(entry.getKey().getName(), entry.getValue());
                typeDescriptions.put(entry.getKey().getName(), entry.getKey());
            }
            HashMap loadedTypes = new HashMap();
            try {
                for (Map.Entry<String, Class<?>> entry : classLoader.defineClasses(typeDefinitions).entrySet()) {
                    loadedTypes.put((TypeDescription)typeDescriptions.get(entry.getKey()), entry.getValue());
                }
            }
            catch (ClassNotFoundException classNotFoundException) {
                throw new IllegalStateException("Cannot load classes: " + types, classNotFoundException);
            }
            return loadedTypes;
        }
    }
}

