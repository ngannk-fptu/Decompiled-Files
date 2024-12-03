/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.internal;

import com.google.inject.internal.cglib.core.$DefaultNamingPolicy;
import com.google.inject.internal.cglib.core.$NamingPolicy;
import com.google.inject.internal.cglib.core.$Predicate;
import com.google.inject.internal.cglib.proxy.$Enhancer;
import com.google.inject.internal.cglib.reflect.$FastClass;
import com.google.inject.internal.util.$Function;
import com.google.inject.internal.util.$ImmutableMap;
import com.google.inject.internal.util.$MapMaker;
import com.google.inject.internal.util.$Nullable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Map;
import java.util.logging.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class BytecodeGen {
    static final Logger logger = Logger.getLogger(BytecodeGen.class.getName());
    static final ClassLoader GUICE_CLASS_LOADER = BytecodeGen.canonicalize(BytecodeGen.class.getClassLoader());
    static final String GUICE_INTERNAL_PACKAGE = BytecodeGen.class.getName().replaceFirst("\\.internal\\..*$", ".internal");
    static final String CGLIB_PACKAGE = $Enhancer.class.getName().replaceFirst("\\.cglib\\..*$", ".cglib");
    static final $NamingPolicy FASTCLASS_NAMING_POLICY = new $DefaultNamingPolicy(){

        protected String getTag() {
            return "ByGuice";
        }

        public String getClassName(String prefix, String source, Object key, $Predicate names) {
            return super.getClassName(prefix, "FastClass", key, names);
        }
    };
    static final $NamingPolicy ENHANCER_NAMING_POLICY = new $DefaultNamingPolicy(){

        protected String getTag() {
            return "ByGuice";
        }

        public String getClassName(String prefix, String source, Object key, $Predicate names) {
            return super.getClassName(prefix, "Enhancer", key, names);
        }
    };
    private static final boolean CUSTOM_LOADER_ENABLED = Boolean.parseBoolean(System.getProperty("guice.custom.loader", "true"));
    private static final Map<ClassLoader, ClassLoader> CLASS_LOADER_CACHE = CUSTOM_LOADER_ENABLED ? new $MapMaker().weakKeys().weakValues().makeComputingMap(new $Function<ClassLoader, ClassLoader>(){

        @Override
        public ClassLoader apply(final @$Nullable ClassLoader typeClassLoader) {
            logger.fine("Creating a bridge ClassLoader for " + typeClassLoader);
            return AccessController.doPrivileged(new PrivilegedAction<ClassLoader>(){

                @Override
                public ClassLoader run() {
                    return new BridgeClassLoader(typeClassLoader);
                }
            });
        }
    }) : $ImmutableMap.of();

    private static ClassLoader canonicalize(ClassLoader classLoader) {
        return classLoader != null ? classLoader : SystemBridgeHolder.SYSTEM_BRIDGE.getParent();
    }

    public static ClassLoader getClassLoader(Class<?> type) {
        return BytecodeGen.getClassLoader(type, type.getClassLoader());
    }

    private static ClassLoader getClassLoader(Class<?> type, ClassLoader delegate) {
        if (!CUSTOM_LOADER_ENABLED) {
            return delegate;
        }
        if (type.getName().startsWith("java.")) {
            return GUICE_CLASS_LOADER;
        }
        if ((delegate = BytecodeGen.canonicalize(delegate)) == GUICE_CLASS_LOADER || delegate instanceof BridgeClassLoader) {
            return delegate;
        }
        if (Visibility.forType(type) == Visibility.PUBLIC) {
            if (delegate != SystemBridgeHolder.SYSTEM_BRIDGE.getParent()) {
                return CLASS_LOADER_CACHE.get(delegate);
            }
            return SystemBridgeHolder.SYSTEM_BRIDGE;
        }
        return delegate;
    }

    public static $FastClass newFastClass(Class<?> type, Visibility visibility) {
        $FastClass.Generator generator = new $FastClass.Generator();
        generator.setType(type);
        if (visibility == Visibility.PUBLIC) {
            generator.setClassLoader(BytecodeGen.getClassLoader(type));
        }
        generator.setNamingPolicy(FASTCLASS_NAMING_POLICY);
        logger.fine("Loading " + type + " FastClass with " + generator.getClassLoader());
        return generator.create();
    }

    public static $Enhancer newEnhancer(Class<?> type, Visibility visibility) {
        $Enhancer enhancer = new $Enhancer();
        enhancer.setSuperclass(type);
        enhancer.setUseFactory(false);
        if (visibility == Visibility.PUBLIC) {
            enhancer.setClassLoader(BytecodeGen.getClassLoader(type));
        }
        enhancer.setNamingPolicy(ENHANCER_NAMING_POLICY);
        logger.fine("Loading " + type + " Enhancer with " + enhancer.getClassLoader());
        return enhancer;
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class BridgeClassLoader
    extends ClassLoader {
        BridgeClassLoader() {
        }

        BridgeClassLoader(ClassLoader usersClassLoader) {
            super(usersClassLoader);
        }

        @Override
        protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
            if (name.startsWith("sun.reflect")) {
                return SystemBridgeHolder.SYSTEM_BRIDGE.classicLoadClass(name, resolve);
            }
            if (name.startsWith(GUICE_INTERNAL_PACKAGE) || name.startsWith(CGLIB_PACKAGE)) {
                if (null == GUICE_CLASS_LOADER) {
                    return SystemBridgeHolder.SYSTEM_BRIDGE.classicLoadClass(name, resolve);
                }
                try {
                    Class<?> clazz = GUICE_CLASS_LOADER.loadClass(name);
                    if (resolve) {
                        this.resolveClass(clazz);
                    }
                    return clazz;
                }
                catch (Throwable throwable) {
                    // empty catch block
                }
            }
            return this.classicLoadClass(name, resolve);
        }

        Class<?> classicLoadClass(String name, boolean resolve) throws ClassNotFoundException {
            return super.loadClass(name, resolve);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum Visibility {
        PUBLIC{

            public Visibility and(Visibility that) {
                return that;
            }
        }
        ,
        SAME_PACKAGE{

            public Visibility and(Visibility that) {
                return this;
            }
        };


        public static Visibility forMember(Member member) {
            Class<?>[] parameterTypes;
            if ((member.getModifiers() & 5) == 0) {
                return SAME_PACKAGE;
            }
            if (member instanceof Constructor) {
                parameterTypes = ((Constructor)member).getParameterTypes();
            } else {
                Method method = (Method)member;
                if (Visibility.forType(method.getReturnType()) == SAME_PACKAGE) {
                    return SAME_PACKAGE;
                }
                parameterTypes = method.getParameterTypes();
            }
            for (Class<?> type : parameterTypes) {
                if (Visibility.forType(type) != SAME_PACKAGE) continue;
                return SAME_PACKAGE;
            }
            return PUBLIC;
        }

        public static Visibility forType(Class<?> type) {
            return (type.getModifiers() & 5) != 0 ? PUBLIC : SAME_PACKAGE;
        }

        public abstract Visibility and(Visibility var1);
    }

    private static class SystemBridgeHolder {
        static final BridgeClassLoader SYSTEM_BRIDGE = new BridgeClassLoader();

        private SystemBridgeHolder() {
        }
    }
}

