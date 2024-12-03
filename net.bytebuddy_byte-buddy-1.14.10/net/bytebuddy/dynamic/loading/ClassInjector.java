/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jna.FunctionMapper
 *  com.sun.jna.JNIEnv
 *  com.sun.jna.LastErrorException
 *  com.sun.jna.Library
 *  com.sun.jna.Native
 *  com.sun.jna.NativeLibrary
 *  com.sun.jna.Platform
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package net.bytebuddy.dynamic.loading;

import com.sun.jna.FunctionMapper;
import com.sun.jna.JNIEnv;
import com.sun.jna.LastErrorException;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import com.sun.jna.Platform;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ReflectPermission;
import java.lang.reflect.Type;
import java.net.URL;
import java.security.AccessController;
import java.security.Permission;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.ClassFileVersion;
import net.bytebuddy.asm.MemberRemoval;
import net.bytebuddy.build.AccessControllerPlugin;
import net.bytebuddy.build.HashCodeAndEqualsPlugin;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.description.type.PackageDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.dynamic.loading.PackageDefinitionStrategy;
import net.bytebuddy.dynamic.scaffold.TypeValidation;
import net.bytebuddy.dynamic.scaffold.subclass.ConstructorStrategy;
import net.bytebuddy.implementation.FixedValue;
import net.bytebuddy.implementation.MethodCall;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.GraalImageCode;
import net.bytebuddy.utility.JavaModule;
import net.bytebuddy.utility.JavaType;
import net.bytebuddy.utility.RandomString;
import net.bytebuddy.utility.dispatcher.JavaDispatcher;
import net.bytebuddy.utility.nullability.AlwaysNull;
import net.bytebuddy.utility.nullability.MaybeNull;
import net.bytebuddy.utility.nullability.UnknownNull;
import net.bytebuddy.utility.privilege.GetMethodAction;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface ClassInjector {
    public static final Permission SUPPRESS_ACCESS_CHECKS = new ReflectPermission("suppressAccessChecks");
    public static final boolean ALLOW_EXISTING_TYPES = false;

    public boolean isAlive();

    public Map<TypeDescription, Class<?>> inject(Map<? extends TypeDescription, byte[]> var1);

    public Map<String, Class<?>> injectRaw(Map<? extends String, byte[]> var1);

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @HashCodeAndEqualsPlugin.Enhance
    public static class UsingJna
    extends AbstractBase {
        private static final Dispatcher DISPATCHER;
        private static final Object BOOTSTRAP_LOADER_LOCK;
        @MaybeNull
        @HashCodeAndEqualsPlugin.ValueHandling(value=HashCodeAndEqualsPlugin.ValueHandling.Sort.REVERSE_NULLABILITY)
        private final ClassLoader classLoader;
        @MaybeNull
        @HashCodeAndEqualsPlugin.ValueHandling(value=HashCodeAndEqualsPlugin.ValueHandling.Sort.REVERSE_NULLABILITY)
        private final ProtectionDomain protectionDomain;
        private static final boolean ACCESS_CONTROLLER;

        public UsingJna(@MaybeNull ClassLoader classLoader) {
            this(classLoader, ClassLoadingStrategy.NO_PROTECTION_DOMAIN);
        }

        public UsingJna(@MaybeNull ClassLoader classLoader, @MaybeNull ProtectionDomain protectionDomain) {
            this.classLoader = classLoader;
            this.protectionDomain = protectionDomain;
        }

        @AccessControllerPlugin.Enhance
        private static <T> T doPrivileged(PrivilegedAction<T> privilegedAction) {
            PrivilegedAction<T> action;
            if (ACCESS_CONTROLLER) {
                return AccessController.doPrivileged(privilegedAction);
            }
            return action.run();
        }

        public static boolean isAvailable() {
            return DISPATCHER.isAvailable();
        }

        public static ClassInjector ofSystemLoader() {
            return new UsingJna(ClassLoader.getSystemClassLoader());
        }

        public static ClassInjector ofPlatformLoader() {
            return new UsingJna(ClassLoader.getSystemClassLoader().getParent());
        }

        public static ClassInjector ofBootLoader() {
            return new UsingJna(ClassLoadingStrategy.BOOTSTRAP_LOADER);
        }

        @Override
        public boolean isAlive() {
            return DISPATCHER.isAvailable();
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public Map<String, Class<?>> injectRaw(Map<? extends String, byte[]> types) {
            HashMap result = new HashMap();
            Object object = this.classLoader == null ? BOOTSTRAP_LOADER_LOCK : this.classLoader;
            synchronized (object) {
                for (Map.Entry<? extends String, byte[]> entry : types.entrySet()) {
                    try {
                        result.put(entry.getKey(), Class.forName(entry.getKey(), false, this.classLoader));
                    }
                    catch (ClassNotFoundException ignored) {
                        result.put(entry.getKey(), DISPATCHER.defineClass(this.classLoader, entry.getKey(), entry.getValue(), this.protectionDomain));
                    }
                }
            }
            return result;
        }

        /*
         * Enabled aggressive block sorting
         * Enabled unnecessary exception pruning
         * Enabled aggressive exception aggregation
         */
        static {
            try {
                Class.forName("java.security.AccessController", false, null);
                ACCESS_CONTROLLER = Boolean.parseBoolean(System.getProperty("net.bytebuddy.securitymanager", "true"));
            }
            catch (ClassNotFoundException classNotFoundException) {
                ACCESS_CONTROLLER = false;
            }
            catch (SecurityException securityException) {
                ACCESS_CONTROLLER = true;
            }
            DISPATCHER = UsingJna.doPrivileged(Dispatcher.CreationAction.INSTANCE);
            BOOTSTRAP_LOADER_LOCK = new Object();
        }

        public boolean equals(@MaybeNull Object object) {
            block18: {
                block17: {
                    Object object2;
                    block16: {
                        Object object3;
                        Object object4;
                        block15: {
                            block14: {
                                ClassLoader classLoader;
                                block13: {
                                    if (this == object) {
                                        return true;
                                    }
                                    if (object == null) {
                                        return false;
                                    }
                                    if (this.getClass() != object.getClass()) {
                                        return false;
                                    }
                                    object4 = ((UsingJna)object).classLoader;
                                    object3 = this.classLoader;
                                    classLoader = object3;
                                    if (object4 == null) break block13;
                                    if (classLoader == null) break block14;
                                    if (!object3.equals(object4)) {
                                        return false;
                                    }
                                    break block15;
                                }
                                if (classLoader == null) break block15;
                            }
                            return false;
                        }
                        object4 = ((UsingJna)object).protectionDomain;
                        object2 = object3 = this.protectionDomain;
                        if (object4 == null) break block16;
                        if (object2 == null) break block17;
                        if (!object3.equals(object4)) {
                            return false;
                        }
                        break block18;
                    }
                    if (object2 == null) break block18;
                }
                return false;
            }
            return true;
        }

        public int hashCode() {
            int n = this.getClass().hashCode() * 31;
            Object object = this.classLoader;
            if (object != null) {
                n = n + object.hashCode();
            }
            int n2 = n * 31;
            object = this.protectionDomain;
            if (object != null) {
                n2 = n2 + object.hashCode();
            }
            return n2;
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        protected static interface Dispatcher {
            public boolean isAvailable();

            public Class<?> defineClass(@MaybeNull ClassLoader var1, String var2, byte[] var3, @MaybeNull ProtectionDomain var4);

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            public static interface Jvm
            extends Library {
                public Class<?> JVM_DefineClass(JNIEnv var1, String var2, @MaybeNull ClassLoader var3, byte[] var4, int var5, @MaybeNull ProtectionDomain var6) throws LastErrorException;
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            @HashCodeAndEqualsPlugin.Enhance
            public static class Unavailable
            implements Dispatcher {
                private final String error;

                protected Unavailable(String error) {
                    this.error = error;
                }

                @Override
                public boolean isAvailable() {
                    return false;
                }

                @Override
                public Class<?> defineClass(@MaybeNull ClassLoader classLoader, String name, byte[] binaryRepresentation, @MaybeNull ProtectionDomain protectionDomain) {
                    throw new UnsupportedOperationException("JNA is not available and JNA-based injection cannot be used: " + this.error);
                }

                public boolean equals(@MaybeNull Object object) {
                    if (this == object) {
                        return true;
                    }
                    if (object == null) {
                        return false;
                    }
                    if (this.getClass() != object.getClass()) {
                        return false;
                    }
                    return this.error.equals(((Unavailable)object).error);
                }

                public int hashCode() {
                    return this.getClass().hashCode() * 31 + this.error.hashCode();
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            @HashCodeAndEqualsPlugin.Enhance
            public static class Enabled
            implements Dispatcher {
                private final Jvm jvm;

                protected Enabled(Jvm jvm) {
                    this.jvm = jvm;
                }

                @Override
                public boolean isAvailable() {
                    return true;
                }

                @Override
                public Class<?> defineClass(@MaybeNull ClassLoader classLoader, String name, byte[] binaryRepresentation, @MaybeNull ProtectionDomain protectionDomain) {
                    return this.jvm.JVM_DefineClass(JNIEnv.CURRENT, name.replace('.', '/'), classLoader, binaryRepresentation, binaryRepresentation.length, protectionDomain);
                }

                public boolean equals(@MaybeNull Object object) {
                    if (this == object) {
                        return true;
                    }
                    if (object == null) {
                        return false;
                    }
                    if (this.getClass() != object.getClass()) {
                        return false;
                    }
                    return this.jvm.equals(((Enabled)object).jvm);
                }

                public int hashCode() {
                    return this.getClass().hashCode() * 31 + this.jvm.hashCode();
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            public static enum Windows32BitFunctionMapper implements FunctionMapper
            {
                INSTANCE;


                public String getFunctionName(NativeLibrary library, Method method) {
                    if (method.getName().equals("JVM_DefineClass")) {
                        return "_JVM_DefineClass@24";
                    }
                    return method.getName();
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            public static enum CreationAction implements PrivilegedAction<Dispatcher>
            {
                INSTANCE;


                @Override
                public Dispatcher run() {
                    if (System.getProperty("java.vm.name", "").toUpperCase(Locale.US).contains("J9")) {
                        return new Unavailable("J9 does not support JNA-based class definition");
                    }
                    try {
                        HashMap<String, Object> options = new HashMap<String, Object>();
                        options.put("allow-objects", Boolean.TRUE);
                        if (Platform.isWindows() && !Platform.is64Bit()) {
                            options.put("function-mapper", (Object)Windows32BitFunctionMapper.INSTANCE);
                        }
                        return new Enabled((Jvm)Native.loadLibrary((String)"jvm", Jvm.class, options));
                    }
                    catch (Throwable throwable) {
                        return new Unavailable(throwable.getMessage());
                    }
                }
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @HashCodeAndEqualsPlugin.Enhance
    public static class UsingInstrumentation
    extends AbstractBase {
        private static final String JAR = "jar";
        private static final String CLASS_FILE_EXTENSION = ".class";
        private static final Dispatcher DISPATCHER;
        private final Instrumentation instrumentation;
        private final Target target;
        private final File folder;
        private final RandomString randomString;
        private static final boolean ACCESS_CONTROLLER;

        protected UsingInstrumentation(File folder, Target target, Instrumentation instrumentation, RandomString randomString) {
            this.folder = folder;
            this.target = target;
            this.instrumentation = instrumentation;
            this.randomString = randomString;
        }

        @AccessControllerPlugin.Enhance
        private static <T> T doPrivileged(PrivilegedAction<T> privilegedAction) {
            PrivilegedAction<T> action;
            if (ACCESS_CONTROLLER) {
                return AccessController.doPrivileged(privilegedAction);
            }
            return action.run();
        }

        public static void redefineModule(Instrumentation instrumentation, JavaModule target, Set<JavaModule> reads, Map<String, Set<JavaModule>> exports, Map<String, Set<JavaModule>> opens, Set<Class<?>> uses, Map<Class<?>, List<Class<?>>> provides) {
            if (!DISPATCHER.isModifiableModule(instrumentation, target.unwrap())) {
                throw new IllegalArgumentException("Cannot modify module: " + target);
            }
            HashSet<Object> unwrappedReads = new HashSet<Object>();
            for (JavaModule javaModule : reads) {
                unwrappedReads.add(javaModule.unwrap());
            }
            HashMap unwrappedExports = new HashMap();
            for (Map.Entry<String, Set<JavaModule>> entry : exports.entrySet()) {
                HashSet<Object> modules = new HashSet<Object>();
                for (JavaModule module : entry.getValue()) {
                    modules.add(module.unwrap());
                }
                unwrappedExports.put(entry.getKey(), modules);
            }
            HashMap hashMap = new HashMap();
            for (Map.Entry<String, Set<JavaModule>> entry : opens.entrySet()) {
                HashSet<Object> modules = new HashSet<Object>();
                for (JavaModule module : entry.getValue()) {
                    modules.add(module.unwrap());
                }
                hashMap.put(entry.getKey(), modules);
            }
            DISPATCHER.redefineModule(instrumentation, target.unwrap(), unwrappedReads, unwrappedExports, hashMap, uses, provides);
        }

        public static ClassInjector of(File folder, Target target, Instrumentation instrumentation) {
            return new UsingInstrumentation(folder, target, instrumentation, new RandomString());
        }

        @Override
        public boolean isAlive() {
            return UsingInstrumentation.isAvailable();
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public Map<String, Class<?>> injectRaw(Map<? extends String, byte[]> types) {
            File file = new File(this.folder, JAR + this.randomString.nextString() + "." + JAR);
            if (!file.createNewFile()) {
                throw new IllegalStateException("Cannot create file " + file);
            }
            try {
                HashMap<String, Class<?>> hashMap;
                block14: {
                    JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(file));
                    try {
                        for (Map.Entry<? extends String, byte[]> entry : types.entrySet()) {
                            jarOutputStream.putNextEntry(new JarEntry(entry.getKey().replace('.', '/') + CLASS_FILE_EXTENSION));
                            jarOutputStream.write(entry.getValue());
                        }
                        Object var7_8 = null;
                    }
                    catch (Throwable throwable) {
                        Object var7_9 = null;
                        jarOutputStream.close();
                        throw throwable;
                    }
                    jarOutputStream.close();
                    JarFile jarFile = new JarFile(file, false);
                    try {
                        this.target.inject(this.instrumentation, jarFile);
                        Object var9_14 = null;
                    }
                    catch (Throwable throwable) {
                        Object var9_15 = null;
                        jarFile.close();
                        throw throwable;
                    }
                    jarFile.close();
                    HashMap result = new HashMap();
                    for (String string : types.keySet()) {
                        result.put(string, Class.forName(string, false, this.target.getClassLoader()));
                    }
                    hashMap = result;
                    {
                        Object var11_17 = null;
                        if (file.delete()) break block14;
                        file.deleteOnExit();
                    }
                }
                return hashMap;
            }
            catch (Throwable throwable) {
                try {
                    Object var11_18 = null;
                    if (!file.delete()) {
                        file.deleteOnExit();
                    }
                    throw throwable;
                }
                catch (IOException exception) {
                    throw new IllegalStateException("Cannot write jar file to disk", exception);
                }
                catch (ClassNotFoundException exception) {
                    throw new IllegalStateException("Cannot load injected class", exception);
                }
            }
        }

        public static boolean isAvailable() {
            return ClassFileVersion.ofThisVm(ClassFileVersion.JAVA_V5).isAtLeast(ClassFileVersion.JAVA_V6);
        }

        /*
         * Enabled aggressive block sorting
         * Enabled unnecessary exception pruning
         * Enabled aggressive exception aggregation
         */
        static {
            try {
                Class.forName("java.security.AccessController", false, null);
                ACCESS_CONTROLLER = Boolean.parseBoolean(System.getProperty("net.bytebuddy.securitymanager", "true"));
            }
            catch (ClassNotFoundException classNotFoundException) {
                ACCESS_CONTROLLER = false;
            }
            catch (SecurityException securityException) {
                ACCESS_CONTROLLER = true;
            }
            DISPATCHER = UsingInstrumentation.doPrivileged(JavaDispatcher.of(Dispatcher.class));
        }

        public boolean equals(@MaybeNull Object object) {
            if (this == object) {
                return true;
            }
            if (object == null) {
                return false;
            }
            if (this.getClass() != object.getClass()) {
                return false;
            }
            if (!this.target.equals((Object)((UsingInstrumentation)object).target)) {
                return false;
            }
            if (!this.instrumentation.equals(((UsingInstrumentation)object).instrumentation)) {
                return false;
            }
            if (!this.folder.equals(((UsingInstrumentation)object).folder)) {
                return false;
            }
            return this.randomString.equals(((UsingInstrumentation)object).randomString);
        }

        public int hashCode() {
            return (((this.getClass().hashCode() * 31 + this.instrumentation.hashCode()) * 31 + this.target.hashCode()) * 31 + this.folder.hashCode()) * 31 + this.randomString.hashCode();
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static enum Target {
            BOOTSTRAP(null){

                protected void inject(Instrumentation instrumentation, JarFile jarFile) {
                    DISPATCHER.appendToBootstrapClassLoaderSearch(instrumentation, jarFile);
                }
            }
            ,
            SYSTEM(ClassLoader.getSystemClassLoader()){

                protected void inject(Instrumentation instrumentation, JarFile jarFile) {
                    DISPATCHER.appendToSystemClassLoaderSearch(instrumentation, jarFile);
                }
            };

            @MaybeNull
            private final ClassLoader classLoader;

            private Target(ClassLoader classLoader) {
                this.classLoader = classLoader;
            }

            @MaybeNull
            protected ClassLoader getClassLoader() {
                return this.classLoader;
            }

            protected abstract void inject(Instrumentation var1, JarFile var2);
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        @JavaDispatcher.Proxied(value="java.lang.instrument.Instrumentation")
        protected static interface Dispatcher {
            @JavaDispatcher.Proxied(value="appendToBootstrapClassLoaderSearch")
            public void appendToBootstrapClassLoaderSearch(Instrumentation var1, JarFile var2);

            @JavaDispatcher.Proxied(value="appendToSystemClassLoaderSearch")
            public void appendToSystemClassLoaderSearch(Instrumentation var1, JarFile var2);

            @JavaDispatcher.Proxied(value="isModifiableModule")
            public boolean isModifiableModule(Instrumentation var1, @JavaDispatcher.Proxied(value="java.lang.Module") Object var2);

            @JavaDispatcher.Proxied(value="redefineModule")
            public void redefineModule(Instrumentation var1, @JavaDispatcher.Proxied(value="java.lang.Module") Object var2, Set<?> var3, Map<String, Set<?>> var4, Map<String, Set<?>> var5, Set<Class<?>> var6, Map<Class<?>, List<Class<?>>> var7);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @HashCodeAndEqualsPlugin.Enhance
    public static class UsingUnsafe
    extends AbstractBase {
        public static final String SAFE_PROPERTY = "net.bytebuddy.safe";
        private static final Dispatcher.Initializable DISPATCHER;
        private static final System SYSTEM;
        private static final Method CHECK_PERMISSION;
        private static final Object BOOTSTRAP_LOADER_LOCK;
        @MaybeNull
        @HashCodeAndEqualsPlugin.ValueHandling(value=HashCodeAndEqualsPlugin.ValueHandling.Sort.REVERSE_NULLABILITY)
        private final ClassLoader classLoader;
        @MaybeNull
        @HashCodeAndEqualsPlugin.ValueHandling(value=HashCodeAndEqualsPlugin.ValueHandling.Sort.REVERSE_NULLABILITY)
        private final ProtectionDomain protectionDomain;
        private final Dispatcher.Initializable dispatcher;
        private static final boolean ACCESS_CONTROLLER;

        public UsingUnsafe(@MaybeNull ClassLoader classLoader) {
            this(classLoader, ClassLoadingStrategy.NO_PROTECTION_DOMAIN);
        }

        public UsingUnsafe(@MaybeNull ClassLoader classLoader, @MaybeNull ProtectionDomain protectionDomain) {
            this(classLoader, protectionDomain, DISPATCHER);
        }

        protected UsingUnsafe(@MaybeNull ClassLoader classLoader, @MaybeNull ProtectionDomain protectionDomain, Dispatcher.Initializable dispatcher) {
            this.classLoader = classLoader;
            this.protectionDomain = protectionDomain;
            this.dispatcher = dispatcher;
        }

        @AccessControllerPlugin.Enhance
        private static <T> T doPrivileged(PrivilegedAction<T> privilegedAction) {
            PrivilegedAction<T> action;
            if (ACCESS_CONTROLLER) {
                return AccessController.doPrivileged(privilegedAction);
            }
            return action.run();
        }

        @Override
        public boolean isAlive() {
            return this.dispatcher.isAvailable();
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public Map<String, Class<?>> injectRaw(Map<? extends String, byte[]> types) {
            Dispatcher dispatcher = this.dispatcher.initialize();
            HashMap result = new HashMap();
            Object object = this.classLoader == null ? BOOTSTRAP_LOADER_LOCK : this.classLoader;
            synchronized (object) {
                for (Map.Entry<? extends String, byte[]> entry : types.entrySet()) {
                    try {
                        result.put(entry.getKey(), Class.forName(entry.getKey(), false, this.classLoader));
                    }
                    catch (ClassNotFoundException ignored) {
                        try {
                            result.put(entry.getKey(), dispatcher.defineClass(this.classLoader, entry.getKey(), entry.getValue(), this.protectionDomain));
                        }
                        catch (RuntimeException exception) {
                            try {
                                result.put(entry.getKey(), Class.forName(entry.getKey(), false, this.classLoader));
                            }
                            catch (ClassNotFoundException ignored2) {
                                throw exception;
                            }
                        }
                        catch (Error error) {
                            try {
                                result.put(entry.getKey(), Class.forName(entry.getKey(), false, this.classLoader));
                            }
                            catch (ClassNotFoundException ignored2) {
                                throw error;
                            }
                        }
                    }
                }
            }
            return result;
        }

        public static boolean isAvailable() {
            return DISPATCHER.isAvailable();
        }

        public static ClassInjector ofSystemLoader() {
            return new UsingUnsafe(ClassLoader.getSystemClassLoader());
        }

        public static ClassInjector ofPlatformLoader() {
            return new UsingUnsafe(ClassLoader.getSystemClassLoader().getParent());
        }

        public static ClassInjector ofBootLoader() {
            return new UsingUnsafe(ClassLoadingStrategy.BOOTSTRAP_LOADER);
        }

        /*
         * Enabled aggressive block sorting
         * Enabled unnecessary exception pruning
         * Enabled aggressive exception aggregation
         */
        static {
            try {
                Class.forName("java.security.AccessController", false, null);
                ACCESS_CONTROLLER = Boolean.parseBoolean(java.lang.System.getProperty("net.bytebuddy.securitymanager", "true"));
            }
            catch (ClassNotFoundException classNotFoundException) {
                ACCESS_CONTROLLER = false;
            }
            catch (SecurityException securityException) {
                ACCESS_CONTROLLER = true;
            }
            DISPATCHER = UsingUnsafe.doPrivileged(Dispatcher.CreationAction.INSTANCE);
            SYSTEM = UsingUnsafe.doPrivileged(JavaDispatcher.of(System.class));
            CHECK_PERMISSION = UsingUnsafe.doPrivileged(new GetMethodAction("java.lang.SecurityManager", "checkPermission", Permission.class));
            BOOTSTRAP_LOADER_LOCK = new Object();
        }

        public boolean equals(@MaybeNull Object object) {
            block18: {
                block17: {
                    Object object2;
                    block16: {
                        Object object3;
                        Object object4;
                        block15: {
                            block14: {
                                ClassLoader classLoader;
                                block13: {
                                    if (this == object) {
                                        return true;
                                    }
                                    if (object == null) {
                                        return false;
                                    }
                                    if (this.getClass() != object.getClass()) {
                                        return false;
                                    }
                                    object4 = ((UsingUnsafe)object).classLoader;
                                    object3 = this.classLoader;
                                    classLoader = object3;
                                    if (object4 == null) break block13;
                                    if (classLoader == null) break block14;
                                    if (!object3.equals(object4)) {
                                        return false;
                                    }
                                    break block15;
                                }
                                if (classLoader == null) break block15;
                            }
                            return false;
                        }
                        object4 = ((UsingUnsafe)object).protectionDomain;
                        object2 = object3 = this.protectionDomain;
                        if (object4 == null) break block16;
                        if (object2 == null) break block17;
                        if (!object3.equals(object4)) {
                            return false;
                        }
                        break block18;
                    }
                    if (object2 == null) break block18;
                }
                return false;
            }
            return this.dispatcher.equals(((UsingUnsafe)object).dispatcher);
        }

        public int hashCode() {
            int n = this.getClass().hashCode() * 31;
            Object object = this.classLoader;
            if (object != null) {
                n = n + object.hashCode();
            }
            int n2 = n * 31;
            object = this.protectionDomain;
            if (object != null) {
                n2 = n2 + object.hashCode();
            }
            return n2 * 31 + this.dispatcher.hashCode();
        }

        @JavaDispatcher.Proxied(value="java.lang.System")
        protected static interface System {
            @MaybeNull
            @JavaDispatcher.IsStatic
            @JavaDispatcher.Defaults
            @JavaDispatcher.Proxied(value="getSecurityManager")
            public Object getSecurityManager();
        }

        @HashCodeAndEqualsPlugin.Enhance
        public static class Factory {
            private final Dispatcher.Initializable dispatcher;

            public Factory() {
                this(AccessResolver.Default.INSTANCE);
            }

            @SuppressFBWarnings(value={"REC_CATCH_EXCEPTION"}, justification="Exception should not be rethrown but trigger a fallback.")
            public Factory(AccessResolver accessResolver) {
                Dispatcher.Initializable dispatcher;
                if (DISPATCHER.isAvailable()) {
                    dispatcher = DISPATCHER;
                } else {
                    try {
                        Class<?> unsafeType = Class.forName("jdk.internal.misc.Unsafe");
                        Field theUnsafe = unsafeType.getDeclaredField("theUnsafe");
                        accessResolver.apply(theUnsafe);
                        Object unsafe = theUnsafe.get(null);
                        Method defineClass = unsafeType.getMethod("defineClass", String.class, byte[].class, Integer.TYPE, Integer.TYPE, ClassLoader.class, ProtectionDomain.class);
                        accessResolver.apply(defineClass);
                        dispatcher = new Dispatcher.Enabled(unsafe, defineClass);
                    }
                    catch (Exception exception) {
                        dispatcher = new Dispatcher.Unavailable(exception.getMessage());
                    }
                }
                this.dispatcher = dispatcher;
            }

            protected Factory(Dispatcher.Initializable dispatcher) {
                this.dispatcher = dispatcher;
            }

            public static Factory resolve(Instrumentation instrumentation) {
                return Factory.resolve(instrumentation, false);
            }

            @SuppressFBWarnings(value={"REC_CATCH_EXCEPTION", "NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE"}, justification="Exception intends to trigger disabled injection strategy. Modules are assumed if module system is supported.")
            public static Factory resolve(Instrumentation instrumentation, boolean local) {
                if (UsingUnsafe.isAvailable() || !JavaModule.isSupported()) {
                    return new Factory();
                }
                try {
                    Class<?> type = Class.forName("jdk.internal.misc.Unsafe");
                    PackageDescription.ForLoadedPackage packageDescription = new PackageDescription.ForLoadedPackage(type.getPackage());
                    JavaModule source = JavaModule.ofType(type);
                    JavaModule target = JavaModule.ofType(UsingUnsafe.class);
                    if (source.isOpened(packageDescription, target)) {
                        return new Factory();
                    }
                    if (local) {
                        JavaModule module = JavaModule.ofType(AccessResolver.Default.class);
                        UsingInstrumentation.redefineModule(instrumentation, source, Collections.singleton(module), Collections.<String, Set<JavaModule>>emptyMap(), Collections.singletonMap(packageDescription.getName(), Collections.singleton(module)), Collections.<Class<?>>emptySet(), Collections.<Class<?>, List<Class<?>>>emptyMap());
                        return new Factory();
                    }
                    Class resolver = new ByteBuddy().subclass(AccessResolver.class).method(ElementMatchers.named("apply")).intercept(MethodCall.invoke(AccessibleObject.class.getMethod("setAccessible", Boolean.TYPE)).onArgument(0).with(true)).make().load(AccessResolver.class.getClassLoader(), ClassLoadingStrategy.Default.WRAPPER.with(AccessResolver.class.getProtectionDomain())).getLoaded();
                    JavaModule module = JavaModule.ofType(resolver);
                    UsingInstrumentation.redefineModule(instrumentation, source, Collections.singleton(module), Collections.<String, Set<JavaModule>>emptyMap(), Collections.singletonMap(packageDescription.getName(), Collections.singleton(module)), Collections.<Class<?>>emptySet(), Collections.<Class<?>, List<Class<?>>>emptyMap());
                    return new Factory((AccessResolver)resolver.getConstructor(new Class[0]).newInstance(new Object[0]));
                }
                catch (Exception exception) {
                    return new Factory(new Dispatcher.Unavailable(exception.getMessage()));
                }
            }

            public boolean isAvailable() {
                return this.dispatcher.isAvailable();
            }

            public ClassInjector make(@MaybeNull ClassLoader classLoader) {
                return this.make(classLoader, ClassLoadingStrategy.NO_PROTECTION_DOMAIN);
            }

            public ClassInjector make(@MaybeNull ClassLoader classLoader, @MaybeNull ProtectionDomain protectionDomain) {
                return new UsingUnsafe(classLoader, protectionDomain, this.dispatcher);
            }

            public boolean equals(@MaybeNull Object object) {
                if (this == object) {
                    return true;
                }
                if (object == null) {
                    return false;
                }
                if (this.getClass() != object.getClass()) {
                    return false;
                }
                return this.dispatcher.equals(((Factory)object).dispatcher);
            }

            public int hashCode() {
                return this.getClass().hashCode() * 31 + this.dispatcher.hashCode();
            }

            public static interface AccessResolver {
                public void apply(AccessibleObject var1);

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                public static enum Default implements AccessResolver
                {
                    INSTANCE;


                    @Override
                    public void apply(AccessibleObject accessibleObject) {
                        accessibleObject.setAccessible(true);
                    }
                }
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        protected static interface Dispatcher {
            public Class<?> defineClass(@MaybeNull ClassLoader var1, String var2, byte[] var3, @MaybeNull ProtectionDomain var4);

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            @HashCodeAndEqualsPlugin.Enhance
            public static class Unavailable
            implements Dispatcher,
            Initializable {
                private final String message;

                protected Unavailable(String message) {
                    this.message = message;
                }

                @Override
                public boolean isAvailable() {
                    return false;
                }

                @Override
                public Dispatcher initialize() {
                    throw new UnsupportedOperationException("Could not access Unsafe class: " + this.message);
                }

                @Override
                public Class<?> defineClass(@MaybeNull ClassLoader classLoader, String name, byte[] binaryRepresentation, @MaybeNull ProtectionDomain protectionDomain) {
                    throw new UnsupportedOperationException("Could not access Unsafe class: " + this.message);
                }

                public boolean equals(@MaybeNull Object object) {
                    if (this == object) {
                        return true;
                    }
                    if (object == null) {
                        return false;
                    }
                    if (this.getClass() != object.getClass()) {
                        return false;
                    }
                    return this.message.equals(((Unavailable)object).message);
                }

                public int hashCode() {
                    return this.getClass().hashCode() * 31 + this.message.hashCode();
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            @HashCodeAndEqualsPlugin.Enhance
            public static class Enabled
            implements Dispatcher,
            Initializable {
                private final Object unsafe;
                private final Method defineClass;

                protected Enabled(Object unsafe, Method defineClass) {
                    this.unsafe = unsafe;
                    this.defineClass = defineClass;
                }

                @Override
                public boolean isAvailable() {
                    return true;
                }

                @Override
                public Dispatcher initialize() {
                    Object securityManager = SYSTEM.getSecurityManager();
                    if (securityManager != null) {
                        try {
                            CHECK_PERMISSION.invoke(securityManager, SUPPRESS_ACCESS_CHECKS);
                        }
                        catch (InvocationTargetException exception) {
                            return new Unavailable(exception.getTargetException().getMessage());
                        }
                        catch (Exception exception) {
                            return new Unavailable(exception.getMessage());
                        }
                    }
                    return this;
                }

                @Override
                public Class<?> defineClass(@MaybeNull ClassLoader classLoader, String name, byte[] binaryRepresentation, @MaybeNull ProtectionDomain protectionDomain) {
                    try {
                        return (Class)this.defineClass.invoke(this.unsafe, name, binaryRepresentation, 0, binaryRepresentation.length, classLoader, protectionDomain);
                    }
                    catch (IllegalAccessException exception) {
                        throw new IllegalStateException(exception);
                    }
                    catch (InvocationTargetException exception) {
                        throw new IllegalStateException(exception.getTargetException());
                    }
                }

                public boolean equals(@MaybeNull Object object) {
                    if (this == object) {
                        return true;
                    }
                    if (object == null) {
                        return false;
                    }
                    if (this.getClass() != object.getClass()) {
                        return false;
                    }
                    if (!this.unsafe.equals(((Enabled)object).unsafe)) {
                        return false;
                    }
                    return this.defineClass.equals(((Enabled)object).defineClass);
                }

                public int hashCode() {
                    return (this.getClass().hashCode() * 31 + this.unsafe.hashCode()) * 31 + this.defineClass.hashCode();
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            public static enum CreationAction implements PrivilegedAction<Initializable>
            {
                INSTANCE;


                @Override
                @SuppressFBWarnings(value={"REC_CATCH_EXCEPTION"}, justification="Exception should not be rethrown but trigger a fallback.")
                public Initializable run() {
                    if (Boolean.parseBoolean(java.lang.System.getProperty(UsingUnsafe.SAFE_PROPERTY, Boolean.toString(GraalImageCode.getCurrent().isDefined())))) {
                        return new Unavailable("Use of Unsafe was disabled by system property");
                    }
                    try {
                        Class<?> unsafeType = Class.forName("sun.misc.Unsafe");
                        Field theUnsafe = unsafeType.getDeclaredField("theUnsafe");
                        theUnsafe.setAccessible(true);
                        Object unsafe = theUnsafe.get(null);
                        try {
                            Method defineClass = unsafeType.getMethod("defineClass", String.class, byte[].class, Integer.TYPE, Integer.TYPE, ClassLoader.class, ProtectionDomain.class);
                            defineClass.setAccessible(true);
                            return new Enabled(unsafe, defineClass);
                        }
                        catch (Exception exception) {
                            try {
                                Field override;
                                try {
                                    override = AccessibleObject.class.getDeclaredField("override");
                                }
                                catch (NoSuchFieldException ignored) {
                                    override = new ByteBuddy().redefine(AccessibleObject.class).name("net.bytebuddy.mirror." + AccessibleObject.class.getSimpleName()).noNestMate().visit(new MemberRemoval().stripInvokables(ElementMatchers.any())).make().load(AccessibleObject.class.getClassLoader(), ClassLoadingStrategy.Default.WRAPPER.with(AccessibleObject.class.getProtectionDomain())).getLoaded().getDeclaredField("override");
                                }
                                long offset = (Long)unsafeType.getMethod("objectFieldOffset", Field.class).invoke(unsafe, override);
                                Method putBoolean = unsafeType.getMethod("putBoolean", Object.class, Long.TYPE, Boolean.TYPE);
                                Class<?> internalUnsafe = Class.forName("jdk.internal.misc.Unsafe");
                                Field theUnsafeInternal = internalUnsafe.getDeclaredField("theUnsafe");
                                putBoolean.invoke(unsafe, theUnsafeInternal, offset, true);
                                Method defineClassInternal = internalUnsafe.getMethod("defineClass", String.class, byte[].class, Integer.TYPE, Integer.TYPE, ClassLoader.class, ProtectionDomain.class);
                                putBoolean.invoke(unsafe, defineClassInternal, offset, true);
                                return new Enabled(theUnsafeInternal.get(null), defineClassInternal);
                            }
                            catch (Exception ignored) {
                                throw exception;
                            }
                        }
                    }
                    catch (Exception exception) {
                        return new Unavailable(exception.getMessage());
                    }
                }
            }

            public static interface Initializable {
                public boolean isAvailable();

                public Dispatcher initialize();
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @HashCodeAndEqualsPlugin.Enhance
    public static class UsingLookup
    extends AbstractBase {
        private static final MethodHandles METHOD_HANDLES;
        private static final MethodHandles.Lookup METHOD_HANDLES_LOOKUP;
        private static final int PACKAGE_LOOKUP = 8;
        private final Object lookup;
        private static final boolean ACCESS_CONTROLLER;

        protected UsingLookup(Object lookup) {
            this.lookup = lookup;
        }

        @AccessControllerPlugin.Enhance
        private static <T> T doPrivileged(PrivilegedAction<T> privilegedAction) {
            PrivilegedAction<T> action;
            if (ACCESS_CONTROLLER) {
                return AccessController.doPrivileged(privilegedAction);
            }
            return action.run();
        }

        public static UsingLookup of(Object lookup) {
            if (!UsingLookup.isAvailable()) {
                throw new IllegalStateException("The current VM does not support class definition via method handle lookups");
            }
            if (!JavaType.METHOD_HANDLES_LOOKUP.isInstance(lookup)) {
                throw new IllegalArgumentException("Not a method handle lookup: " + lookup);
            }
            if ((METHOD_HANDLES_LOOKUP.lookupModes(lookup) & 8) == 0) {
                throw new IllegalArgumentException("Lookup does not imply package-access: " + lookup);
            }
            return new UsingLookup(lookup);
        }

        public Class<?> lookupType() {
            return METHOD_HANDLES_LOOKUP.lookupClass(this.lookup);
        }

        public UsingLookup in(Class<?> type) {
            try {
                return new UsingLookup(METHOD_HANDLES.privateLookupIn(type, this.lookup));
            }
            catch (IllegalAccessException exception) {
                throw new IllegalStateException("Cannot access " + type.getName() + " from " + this.lookup, exception);
            }
        }

        @Override
        public boolean isAlive() {
            return UsingLookup.isAvailable();
        }

        @Override
        public Map<String, Class<?>> injectRaw(Map<? extends String, byte[]> types) {
            PackageDescription target = TypeDescription.ForLoadedType.of(this.lookupType()).getPackage();
            if (target == null) {
                throw new IllegalArgumentException("Cannot inject array or primitive type");
            }
            HashMap result = new HashMap();
            for (Map.Entry<? extends String, byte[]> entry : types.entrySet()) {
                int index = entry.getKey().lastIndexOf(46);
                if (!target.getName().equals(index == -1 ? "" : entry.getKey().substring(0, index))) {
                    throw new IllegalArgumentException(entry.getKey() + " must be defined in the same package as " + this.lookup);
                }
                try {
                    result.put(entry.getKey(), METHOD_HANDLES_LOOKUP.defineClass(this.lookup, entry.getValue()));
                }
                catch (Exception exception) {
                    throw new IllegalStateException(exception);
                }
            }
            return result;
        }

        public static boolean isAvailable() {
            return JavaType.MODULE.isAvailable();
        }

        /*
         * Enabled aggressive block sorting
         * Enabled unnecessary exception pruning
         * Enabled aggressive exception aggregation
         */
        static {
            try {
                Class.forName("java.security.AccessController", false, null);
                ACCESS_CONTROLLER = Boolean.parseBoolean(System.getProperty("net.bytebuddy.securitymanager", "true"));
            }
            catch (ClassNotFoundException classNotFoundException) {
                ACCESS_CONTROLLER = false;
            }
            catch (SecurityException securityException) {
                ACCESS_CONTROLLER = true;
            }
            METHOD_HANDLES = UsingLookup.doPrivileged(JavaDispatcher.of(MethodHandles.class));
            METHOD_HANDLES_LOOKUP = UsingLookup.doPrivileged(JavaDispatcher.of(MethodHandles.Lookup.class));
        }

        public boolean equals(@MaybeNull Object object) {
            if (this == object) {
                return true;
            }
            if (object == null) {
                return false;
            }
            if (this.getClass() != object.getClass()) {
                return false;
            }
            return this.lookup.equals(((UsingLookup)object).lookup);
        }

        public int hashCode() {
            return this.getClass().hashCode() * 31 + this.lookup.hashCode();
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        @JavaDispatcher.Proxied(value="java.lang.invoke.MethodHandles")
        protected static interface MethodHandles {
            @JavaDispatcher.IsStatic
            @JavaDispatcher.Proxied(value="privateLookupIn")
            public Object privateLookupIn(Class<?> var1, @JavaDispatcher.Proxied(value="java.lang.invoke.MethodHandles$Lookup") Object var2) throws IllegalAccessException;

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            @JavaDispatcher.Proxied(value="java.lang.invoke.MethodHandles$Lookup")
            public static interface Lookup {
                @JavaDispatcher.Proxied(value="lookupClass")
                public Class<?> lookupClass(Object var1);

                @JavaDispatcher.Proxied(value="lookupModes")
                public int lookupModes(Object var1);

                @JavaDispatcher.Proxied(value="defineClass")
                public Class<?> defineClass(Object var1, byte[] var2) throws IllegalAccessException;
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @HashCodeAndEqualsPlugin.Enhance
    public static class UsingReflection
    extends AbstractBase {
        private static final Dispatcher.Initializable DISPATCHER;
        private static final System SYSTEM;
        private static final Method CHECK_PERMISSION;
        private final ClassLoader classLoader;
        @MaybeNull
        @HashCodeAndEqualsPlugin.ValueHandling(value=HashCodeAndEqualsPlugin.ValueHandling.Sort.REVERSE_NULLABILITY)
        private final ProtectionDomain protectionDomain;
        private final PackageDefinitionStrategy packageDefinitionStrategy;
        private final boolean forbidExisting;
        private static final boolean ACCESS_CONTROLLER;

        public UsingReflection(ClassLoader classLoader) {
            this(classLoader, ClassLoadingStrategy.NO_PROTECTION_DOMAIN);
        }

        public UsingReflection(ClassLoader classLoader, @MaybeNull ProtectionDomain protectionDomain) {
            this(classLoader, protectionDomain, PackageDefinitionStrategy.Trivial.INSTANCE, false);
        }

        public UsingReflection(ClassLoader classLoader, @MaybeNull ProtectionDomain protectionDomain, PackageDefinitionStrategy packageDefinitionStrategy, boolean forbidExisting) {
            if (classLoader == null) {
                throw new IllegalArgumentException("Cannot inject classes into the bootstrap class loader");
            }
            this.classLoader = classLoader;
            this.protectionDomain = protectionDomain;
            this.packageDefinitionStrategy = packageDefinitionStrategy;
            this.forbidExisting = forbidExisting;
        }

        @AccessControllerPlugin.Enhance
        private static <T> T doPrivileged(PrivilegedAction<T> privilegedAction) {
            PrivilegedAction<T> action;
            if (ACCESS_CONTROLLER) {
                return AccessController.doPrivileged(privilegedAction);
            }
            return action.run();
        }

        @Override
        public boolean isAlive() {
            return UsingReflection.isAvailable();
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         * Unable to fully structure code
         */
        @Override
        public Map<String, Class<?>> injectRaw(Map<? extends String, byte[]> types) {
            dispatcher = UsingReflection.DISPATCHER.initialize();
            result = new HashMap<String, Class<?>>();
            for (Map.Entry<? extends String, byte[]> entry : types.entrySet()) {
                var6_6 = dispatcher.getClassLoadingLock(this.classLoader, entry.getKey());
                synchronized (var6_6) {
                    type = dispatcher.findClass(this.classLoader, entry.getKey());
                    if (type == null) {
                        packageIndex = entry.getKey().lastIndexOf(46);
                        if (packageIndex != -1 && (definition = this.packageDefinitionStrategy.define(this.classLoader, packageName = entry.getKey().substring(0, packageIndex), entry.getKey())).isDefined()) {
                            definedPackage = dispatcher.getDefinedPackage(this.classLoader, packageName);
                            if (definedPackage == null) {
                                try {
                                    dispatcher.definePackage(this.classLoader, packageName, definition.getSpecificationTitle(), definition.getSpecificationVersion(), definition.getSpecificationVendor(), definition.getImplementationTitle(), definition.getImplementationVersion(), definition.getImplementationVendor(), definition.getSealBase());
                                }
                                catch (IllegalStateException exception) {
                                    definedPackage = dispatcher.getPackage(this.classLoader, packageName);
                                    if (definedPackage == null) {
                                        throw exception;
                                    }
                                    if (definition.isCompatibleTo(definedPackage)) ** GOTO lbl25
                                    throw new SecurityException("Sealing violation for package " + packageName + " (getPackage fallback)");
                                }
                            } else if (!definition.isCompatibleTo(definedPackage)) {
                                throw new SecurityException("Sealing violation for package " + packageName);
                            }
                        }
lbl25:
                        // 6 sources

                        type = dispatcher.defineClass(this.classLoader, entry.getKey(), entry.getValue(), this.protectionDomain);
                    } else if (this.forbidExisting) {
                        throw new IllegalStateException("Cannot inject already loaded type: " + type);
                    }
                    result.put(entry.getKey(), type);
                }
            }
            return result;
        }

        public static boolean isAvailable() {
            return DISPATCHER.isAvailable();
        }

        public static ClassInjector ofSystemClassLoader() {
            return new UsingReflection(ClassLoader.getSystemClassLoader());
        }

        /*
         * Enabled aggressive block sorting
         * Enabled unnecessary exception pruning
         * Enabled aggressive exception aggregation
         */
        static {
            try {
                Class.forName("java.security.AccessController", false, null);
                ACCESS_CONTROLLER = Boolean.parseBoolean(java.lang.System.getProperty("net.bytebuddy.securitymanager", "true"));
            }
            catch (ClassNotFoundException classNotFoundException) {
                ACCESS_CONTROLLER = false;
            }
            catch (SecurityException securityException) {
                ACCESS_CONTROLLER = true;
            }
            DISPATCHER = UsingReflection.doPrivileged(Dispatcher.CreationAction.INSTANCE);
            SYSTEM = UsingReflection.doPrivileged(JavaDispatcher.of(System.class));
            CHECK_PERMISSION = UsingReflection.doPrivileged(new GetMethodAction("java.lang.SecurityManager", "checkPermission", Permission.class));
        }

        public boolean equals(@MaybeNull Object object) {
            block12: {
                block11: {
                    ProtectionDomain protectionDomain;
                    block10: {
                        ProtectionDomain protectionDomain2;
                        if (this == object) {
                            return true;
                        }
                        if (object == null) {
                            return false;
                        }
                        if (this.getClass() != object.getClass()) {
                            return false;
                        }
                        if (this.forbidExisting != ((UsingReflection)object).forbidExisting) {
                            return false;
                        }
                        if (!this.classLoader.equals(((UsingReflection)object).classLoader)) {
                            return false;
                        }
                        ProtectionDomain protectionDomain3 = ((UsingReflection)object).protectionDomain;
                        protectionDomain = protectionDomain2 = this.protectionDomain;
                        if (protectionDomain3 == null) break block10;
                        if (protectionDomain == null) break block11;
                        if (!protectionDomain2.equals(protectionDomain3)) {
                            return false;
                        }
                        break block12;
                    }
                    if (protectionDomain == null) break block12;
                }
                return false;
            }
            return this.packageDefinitionStrategy.equals(((UsingReflection)object).packageDefinitionStrategy);
        }

        public int hashCode() {
            int n = (this.getClass().hashCode() * 31 + this.classLoader.hashCode()) * 31;
            ProtectionDomain protectionDomain = this.protectionDomain;
            if (protectionDomain != null) {
                n = n + protectionDomain.hashCode();
            }
            return (n * 31 + this.packageDefinitionStrategy.hashCode()) * 31 + this.forbidExisting;
        }

        @JavaDispatcher.Proxied(value="java.lang.System")
        protected static interface System {
            @MaybeNull
            @JavaDispatcher.IsStatic
            @JavaDispatcher.Defaults
            @JavaDispatcher.Proxied(value="getSecurityManager")
            public Object getSecurityManager();
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        protected static interface Dispatcher {
            @AlwaysNull
            public static final Class<?> UNDEFINED = null;

            public Object getClassLoadingLock(ClassLoader var1, String var2);

            @MaybeNull
            public Class<?> findClass(ClassLoader var1, String var2);

            public Class<?> defineClass(ClassLoader var1, String var2, byte[] var3, @MaybeNull ProtectionDomain var4);

            @MaybeNull
            public Package getDefinedPackage(ClassLoader var1, String var2);

            @MaybeNull
            public Package getPackage(ClassLoader var1, String var2);

            public Package definePackage(ClassLoader var1, String var2, @MaybeNull String var3, @MaybeNull String var4, @MaybeNull String var5, @MaybeNull String var6, @MaybeNull String var7, @MaybeNull String var8, @MaybeNull URL var9);

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            @HashCodeAndEqualsPlugin.Enhance
            public static class Unavailable
            implements Dispatcher {
                private final String message;

                protected Unavailable(String message) {
                    this.message = message;
                }

                @Override
                public Object getClassLoadingLock(ClassLoader classLoader, String name) {
                    return classLoader;
                }

                @Override
                public Class<?> findClass(ClassLoader classLoader, String name) {
                    try {
                        return classLoader.loadClass(name);
                    }
                    catch (ClassNotFoundException ignored) {
                        return UNDEFINED;
                    }
                }

                @Override
                public Class<?> defineClass(ClassLoader classLoader, String name, byte[] binaryRepresentation, @MaybeNull ProtectionDomain protectionDomain) {
                    throw new UnsupportedOperationException("Cannot define class using reflection: " + this.message);
                }

                @Override
                public Package getDefinedPackage(ClassLoader classLoader, String name) {
                    throw new UnsupportedOperationException("Cannot get defined package using reflection: " + this.message);
                }

                @Override
                public Package getPackage(ClassLoader classLoader, String name) {
                    throw new UnsupportedOperationException("Cannot get package using reflection: " + this.message);
                }

                @Override
                public Package definePackage(ClassLoader classLoader, String name, @MaybeNull String specificationTitle, @MaybeNull String specificationVersion, @MaybeNull String specificationVendor, @MaybeNull String implementationTitle, @MaybeNull String implementationVersion, @MaybeNull String implementationVendor, @MaybeNull URL sealBase) {
                    throw new UnsupportedOperationException("Cannot define package using injection: " + this.message);
                }

                public boolean equals(@MaybeNull Object object) {
                    if (this == object) {
                        return true;
                    }
                    if (object == null) {
                        return false;
                    }
                    if (this.getClass() != object.getClass()) {
                        return false;
                    }
                    return this.message.equals(((Unavailable)object).message);
                }

                public int hashCode() {
                    return this.getClass().hashCode() * 31 + this.message.hashCode();
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            public static abstract class UsingUnsafeOverride
            implements Dispatcher,
            Initializable {
                protected final Method findLoadedClass;
                protected final Method defineClass;
                @MaybeNull
                protected final Method getDefinedPackage;
                protected final Method getPackage;
                protected final Method definePackage;

                protected UsingUnsafeOverride(Method findLoadedClass, Method defineClass, @MaybeNull Method getDefinedPackage, Method getPackage, Method definePackage) {
                    this.findLoadedClass = findLoadedClass;
                    this.defineClass = defineClass;
                    this.getDefinedPackage = getDefinedPackage;
                    this.getPackage = getPackage;
                    this.definePackage = definePackage;
                }

                @SuppressFBWarnings(value={"DP_DO_INSIDE_DO_PRIVILEGED"}, justification="Assuring privilege is explicit user responsibility.")
                protected static Initializable make() throws Exception {
                    Method getDefinedPackage;
                    Field override;
                    if (Boolean.parseBoolean(java.lang.System.getProperty("net.bytebuddy.safe", Boolean.toString(GraalImageCode.getCurrent().isDefined())))) {
                        return new Initializable.Unavailable("Use of Unsafe was disabled by system property");
                    }
                    Class<?> unsafeType = Class.forName("sun.misc.Unsafe");
                    Field theUnsafe = unsafeType.getDeclaredField("theUnsafe");
                    theUnsafe.setAccessible(true);
                    Object unsafe = theUnsafe.get(null);
                    try {
                        override = AccessibleObject.class.getDeclaredField("override");
                    }
                    catch (NoSuchFieldException ignored) {
                        override = new ByteBuddy().redefine(AccessibleObject.class).name("net.bytebuddy.mirror." + AccessibleObject.class.getSimpleName()).noNestMate().visit(new MemberRemoval().stripInvokables(ElementMatchers.any())).make().load(AccessibleObject.class.getClassLoader(), ClassLoadingStrategy.Default.WRAPPER.with(AccessibleObject.class.getProtectionDomain())).getLoaded().getDeclaredField("override");
                    }
                    long offset = (Long)unsafeType.getMethod("objectFieldOffset", Field.class).invoke(unsafe, override);
                    Method putBoolean = unsafeType.getMethod("putBoolean", Object.class, Long.TYPE, Boolean.TYPE);
                    if (JavaModule.isSupported()) {
                        try {
                            getDefinedPackage = ClassLoader.class.getMethod("getDefinedPackage", String.class);
                        }
                        catch (NoSuchMethodException ignored) {
                            getDefinedPackage = null;
                        }
                    } else {
                        getDefinedPackage = null;
                    }
                    Method getPackage = ClassLoader.class.getDeclaredMethod("getPackage", String.class);
                    putBoolean.invoke(unsafe, getPackage, offset, true);
                    Method findLoadedClass = ClassLoader.class.getDeclaredMethod("findLoadedClass", String.class);
                    Method defineClass = ClassLoader.class.getDeclaredMethod("defineClass", String.class, byte[].class, Integer.TYPE, Integer.TYPE, ProtectionDomain.class);
                    Method definePackage = ClassLoader.class.getDeclaredMethod("definePackage", String.class, String.class, String.class, String.class, String.class, String.class, String.class, URL.class);
                    putBoolean.invoke(unsafe, defineClass, offset, true);
                    putBoolean.invoke(unsafe, findLoadedClass, offset, true);
                    putBoolean.invoke(unsafe, definePackage, offset, true);
                    try {
                        Method getClassLoadingLock = ClassLoader.class.getDeclaredMethod("getClassLoadingLock", String.class);
                        putBoolean.invoke(unsafe, getClassLoadingLock, offset, true);
                        return new ForJava7CapableVm(findLoadedClass, defineClass, getDefinedPackage, getPackage, definePackage, getClassLoadingLock);
                    }
                    catch (NoSuchMethodException ignored) {
                        return new ForLegacyVm(findLoadedClass, defineClass, getDefinedPackage, getPackage, definePackage);
                    }
                }

                @Override
                public boolean isAvailable() {
                    return true;
                }

                @Override
                public Dispatcher initialize() {
                    Object securityManager = SYSTEM.getSecurityManager();
                    if (securityManager != null) {
                        try {
                            CHECK_PERMISSION.invoke(securityManager, SUPPRESS_ACCESS_CHECKS);
                        }
                        catch (InvocationTargetException exception) {
                            return new Unavailable(exception.getTargetException().getMessage());
                        }
                        catch (Exception exception) {
                            return new Unavailable(exception.getMessage());
                        }
                    }
                    return this;
                }

                @Override
                public Class<?> findClass(ClassLoader classLoader, String name) {
                    try {
                        return (Class)this.findLoadedClass.invoke((Object)classLoader, name);
                    }
                    catch (IllegalAccessException exception) {
                        throw new IllegalStateException(exception);
                    }
                    catch (InvocationTargetException exception) {
                        throw new IllegalStateException(exception.getTargetException());
                    }
                }

                @Override
                public Class<?> defineClass(ClassLoader classLoader, String name, byte[] binaryRepresentation, @MaybeNull ProtectionDomain protectionDomain) {
                    try {
                        return (Class)this.defineClass.invoke((Object)classLoader, name, binaryRepresentation, 0, binaryRepresentation.length, protectionDomain);
                    }
                    catch (IllegalAccessException exception) {
                        throw new IllegalStateException(exception);
                    }
                    catch (InvocationTargetException exception) {
                        throw new IllegalStateException(exception.getTargetException());
                    }
                }

                @Override
                @MaybeNull
                public Package getDefinedPackage(ClassLoader classLoader, String name) {
                    if (this.getDefinedPackage == null) {
                        return this.getPackage(classLoader, name);
                    }
                    try {
                        return (Package)this.getDefinedPackage.invoke((Object)classLoader, name);
                    }
                    catch (IllegalAccessException exception) {
                        throw new IllegalStateException(exception);
                    }
                    catch (InvocationTargetException exception) {
                        throw new IllegalStateException(exception.getTargetException());
                    }
                }

                @Override
                public Package getPackage(ClassLoader classLoader, String name) {
                    try {
                        return (Package)this.getPackage.invoke((Object)classLoader, name);
                    }
                    catch (IllegalAccessException exception) {
                        throw new IllegalStateException(exception);
                    }
                    catch (InvocationTargetException exception) {
                        throw new IllegalStateException(exception.getTargetException());
                    }
                }

                @Override
                public Package definePackage(ClassLoader classLoader, String name, @MaybeNull String specificationTitle, @MaybeNull String specificationVersion, @MaybeNull String specificationVendor, @MaybeNull String implementationTitle, @MaybeNull String implementationVersion, @MaybeNull String implementationVendor, @MaybeNull URL sealBase) {
                    try {
                        return (Package)this.definePackage.invoke((Object)classLoader, name, specificationTitle, specificationVersion, specificationVendor, implementationTitle, implementationVersion, implementationVendor, sealBase);
                    }
                    catch (IllegalAccessException exception) {
                        throw new IllegalStateException(exception);
                    }
                    catch (InvocationTargetException exception) {
                        throw new IllegalStateException(exception.getTargetException());
                    }
                }

                protected static class ForLegacyVm
                extends UsingUnsafeOverride {
                    protected ForLegacyVm(Method findLoadedClass, Method defineClass, @MaybeNull Method getDefinedPackage, Method getPackage, Method definePackage) {
                        super(findLoadedClass, defineClass, getDefinedPackage, getPackage, definePackage);
                    }

                    public Object getClassLoadingLock(ClassLoader classLoader, String name) {
                        return classLoader;
                    }
                }

                @HashCodeAndEqualsPlugin.Enhance
                protected static class ForJava7CapableVm
                extends UsingUnsafeOverride {
                    private final Method getClassLoadingLock;

                    protected ForJava7CapableVm(Method findLoadedClass, Method defineClass, @MaybeNull Method getDefinedPackage, Method getPackage, Method definePackage, Method getClassLoadingLock) {
                        super(findLoadedClass, defineClass, getDefinedPackage, getPackage, definePackage);
                        this.getClassLoadingLock = getClassLoadingLock;
                    }

                    public Object getClassLoadingLock(ClassLoader classLoader, String name) {
                        try {
                            return this.getClassLoadingLock.invoke((Object)classLoader, name);
                        }
                        catch (IllegalAccessException exception) {
                            throw new IllegalStateException(exception);
                        }
                        catch (InvocationTargetException exception) {
                            throw new IllegalStateException(exception.getTargetException());
                        }
                    }

                    public boolean equals(@MaybeNull Object object) {
                        if (this == object) {
                            return true;
                        }
                        if (object == null) {
                            return false;
                        }
                        if (this.getClass() != object.getClass()) {
                            return false;
                        }
                        return this.getClassLoadingLock.equals(((ForJava7CapableVm)object).getClassLoadingLock);
                    }

                    public int hashCode() {
                        return this.getClass().hashCode() * 31 + this.getClassLoadingLock.hashCode();
                    }
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            @HashCodeAndEqualsPlugin.Enhance
            public static class UsingUnsafeInjection
            implements Dispatcher,
            Initializable {
                private final Object accessor;
                private final Method findLoadedClass;
                private final Method defineClass;
                @UnknownNull
                private final Method getDefinedPackage;
                private final Method getPackage;
                private final Method definePackage;
                private final Method getClassLoadingLock;

                protected UsingUnsafeInjection(Object accessor, Method findLoadedClass, Method defineClass, @MaybeNull Method getDefinedPackage, Method getPackage, Method definePackage, Method getClassLoadingLock) {
                    this.accessor = accessor;
                    this.findLoadedClass = findLoadedClass;
                    this.defineClass = defineClass;
                    this.getDefinedPackage = getDefinedPackage;
                    this.getPackage = getPackage;
                    this.definePackage = definePackage;
                    this.getClassLoadingLock = getClassLoadingLock;
                }

                @SuppressFBWarnings(value={"DP_DO_INSIDE_DO_PRIVILEGED"}, justification="Assuring privilege is explicit user responsibility.")
                protected static Initializable make() throws Exception {
                    Method getDefinedPackage;
                    if (Boolean.parseBoolean(java.lang.System.getProperty("net.bytebuddy.safe", Boolean.toString(GraalImageCode.getCurrent().isDefined())))) {
                        return new Initializable.Unavailable("Use of Unsafe was disabled by system property");
                    }
                    Class<?> unsafe = Class.forName("sun.misc.Unsafe");
                    Field theUnsafe = unsafe.getDeclaredField("theUnsafe");
                    theUnsafe.setAccessible(true);
                    Object unsafeInstance = theUnsafe.get(null);
                    if (JavaModule.isSupported()) {
                        try {
                            getDefinedPackage = ClassLoader.class.getDeclaredMethod("getDefinedPackage", String.class);
                        }
                        catch (NoSuchMethodException ignored) {
                            getDefinedPackage = null;
                        }
                    } else {
                        getDefinedPackage = null;
                    }
                    DynamicType.Builder.MethodDefinition.ReceiverTypeDefinition builder = new ByteBuddy().with(TypeValidation.DISABLED).subclass(Object.class, (ConstructorStrategy)ConstructorStrategy.Default.NO_CONSTRUCTORS).name(ClassLoader.class.getName() + "$ByteBuddyAccessor$V1").defineMethod("findLoadedClass", (Type)((Object)Class.class), Visibility.PUBLIC).withParameters(new Type[]{ClassLoader.class, String.class}).intercept(MethodCall.invoke(ClassLoader.class.getDeclaredMethod("findLoadedClass", String.class)).onArgument(0).withArgument(1)).defineMethod("defineClass", (Type)((Object)Class.class), Visibility.PUBLIC).withParameters(new Type[]{ClassLoader.class, String.class, byte[].class, Integer.TYPE, Integer.TYPE, ProtectionDomain.class}).intercept(MethodCall.invoke(ClassLoader.class.getDeclaredMethod("defineClass", String.class, byte[].class, Integer.TYPE, Integer.TYPE, ProtectionDomain.class)).onArgument(0).withArgument(1, 2, 3, 4, 5)).defineMethod("getPackage", (Type)((Object)Package.class), Visibility.PUBLIC).withParameters(new Type[]{ClassLoader.class, String.class}).intercept(MethodCall.invoke(ClassLoader.class.getDeclaredMethod("getPackage", String.class)).onArgument(0).withArgument(1)).defineMethod("definePackage", (Type)((Object)Package.class), Visibility.PUBLIC).withParameters(new Type[]{ClassLoader.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, URL.class}).intercept(MethodCall.invoke(ClassLoader.class.getDeclaredMethod("definePackage", String.class, String.class, String.class, String.class, String.class, String.class, String.class, URL.class)).onArgument(0).withArgument(1, 2, 3, 4, 5, 6, 7, 8));
                    if (getDefinedPackage != null) {
                        builder = builder.defineMethod("getDefinedPackage", (Type)((Object)Package.class), Visibility.PUBLIC).withParameters(new Type[]{ClassLoader.class, String.class}).intercept(MethodCall.invoke(getDefinedPackage).onArgument(0).withArgument(1));
                    }
                    try {
                        builder = builder.defineMethod("getClassLoadingLock", (Type)((Object)Object.class), Visibility.PUBLIC).withParameters(new Type[]{ClassLoader.class, String.class}).intercept(MethodCall.invoke(ClassLoader.class.getDeclaredMethod("getClassLoadingLock", String.class)).onArgument(0).withArgument(1));
                    }
                    catch (NoSuchMethodException ignored) {
                        builder = builder.defineMethod("getClassLoadingLock", (Type)((Object)Object.class), Visibility.PUBLIC).withParameters(new Type[]{ClassLoader.class, String.class}).intercept(FixedValue.argument(0));
                    }
                    Class type = builder.make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, new ClassLoadingStrategy.ForUnsafeInjection()).getLoaded();
                    return new UsingUnsafeInjection(unsafe.getMethod("allocateInstance", Class.class).invoke(unsafeInstance, type), type.getMethod("findLoadedClass", ClassLoader.class, String.class), type.getMethod("defineClass", ClassLoader.class, String.class, byte[].class, Integer.TYPE, Integer.TYPE, ProtectionDomain.class), getDefinedPackage != null ? type.getMethod("getDefinedPackage", ClassLoader.class, String.class) : null, type.getMethod("getPackage", ClassLoader.class, String.class), type.getMethod("definePackage", ClassLoader.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, URL.class), type.getMethod("getClassLoadingLock", ClassLoader.class, String.class));
                }

                @Override
                public boolean isAvailable() {
                    return true;
                }

                @Override
                public Dispatcher initialize() {
                    Object securityManager = SYSTEM.getSecurityManager();
                    if (securityManager != null) {
                        try {
                            CHECK_PERMISSION.invoke(securityManager, SUPPRESS_ACCESS_CHECKS);
                        }
                        catch (InvocationTargetException exception) {
                            return new Unavailable(exception.getTargetException().getMessage());
                        }
                        catch (Exception exception) {
                            return new Unavailable(exception.getMessage());
                        }
                    }
                    return this;
                }

                @Override
                public Object getClassLoadingLock(ClassLoader classLoader, String name) {
                    try {
                        return this.getClassLoadingLock.invoke(this.accessor, classLoader, name);
                    }
                    catch (IllegalAccessException exception) {
                        throw new IllegalStateException(exception);
                    }
                    catch (InvocationTargetException exception) {
                        throw new IllegalStateException(exception.getTargetException());
                    }
                }

                @Override
                public Class<?> findClass(ClassLoader classLoader, String name) {
                    try {
                        return (Class)this.findLoadedClass.invoke(this.accessor, classLoader, name);
                    }
                    catch (IllegalAccessException exception) {
                        throw new IllegalStateException(exception);
                    }
                    catch (InvocationTargetException exception) {
                        throw new IllegalStateException(exception.getTargetException());
                    }
                }

                @Override
                public Class<?> defineClass(ClassLoader classLoader, String name, byte[] binaryRepresentation, @MaybeNull ProtectionDomain protectionDomain) {
                    try {
                        return (Class)this.defineClass.invoke(this.accessor, classLoader, name, binaryRepresentation, 0, binaryRepresentation.length, protectionDomain);
                    }
                    catch (IllegalAccessException exception) {
                        throw new IllegalStateException(exception);
                    }
                    catch (InvocationTargetException exception) {
                        throw new IllegalStateException(exception.getTargetException());
                    }
                }

                @Override
                @MaybeNull
                public Package getDefinedPackage(ClassLoader classLoader, String name) {
                    if (this.getDefinedPackage == null) {
                        return this.getPackage(classLoader, name);
                    }
                    try {
                        return (Package)this.getDefinedPackage.invoke(this.accessor, classLoader, name);
                    }
                    catch (IllegalAccessException exception) {
                        throw new IllegalStateException(exception);
                    }
                    catch (InvocationTargetException exception) {
                        throw new IllegalStateException(exception.getTargetException());
                    }
                }

                @Override
                public Package getPackage(ClassLoader classLoader, String name) {
                    try {
                        return (Package)this.getPackage.invoke(this.accessor, classLoader, name);
                    }
                    catch (IllegalAccessException exception) {
                        throw new IllegalStateException(exception);
                    }
                    catch (InvocationTargetException exception) {
                        throw new IllegalStateException(exception.getTargetException());
                    }
                }

                @Override
                public Package definePackage(ClassLoader classLoader, String name, @MaybeNull String specificationTitle, @MaybeNull String specificationVersion, @MaybeNull String specificationVendor, @MaybeNull String implementationTitle, @MaybeNull String implementationVersion, @MaybeNull String implementationVendor, @MaybeNull URL sealBase) {
                    try {
                        return (Package)this.definePackage.invoke(this.accessor, classLoader, name, specificationTitle, specificationVersion, specificationVendor, implementationTitle, implementationVersion, implementationVendor, sealBase);
                    }
                    catch (IllegalAccessException exception) {
                        throw new IllegalStateException(exception);
                    }
                    catch (InvocationTargetException exception) {
                        throw new IllegalStateException(exception.getTargetException());
                    }
                }

                public boolean equals(@MaybeNull Object object) {
                    if (this == object) {
                        return true;
                    }
                    if (object == null) {
                        return false;
                    }
                    if (this.getClass() != object.getClass()) {
                        return false;
                    }
                    if (!this.accessor.equals(((UsingUnsafeInjection)object).accessor)) {
                        return false;
                    }
                    if (!this.findLoadedClass.equals(((UsingUnsafeInjection)object).findLoadedClass)) {
                        return false;
                    }
                    if (!this.defineClass.equals(((UsingUnsafeInjection)object).defineClass)) {
                        return false;
                    }
                    if (!this.getDefinedPackage.equals(((UsingUnsafeInjection)object).getDefinedPackage)) {
                        return false;
                    }
                    if (!this.getPackage.equals(((UsingUnsafeInjection)object).getPackage)) {
                        return false;
                    }
                    if (!this.definePackage.equals(((UsingUnsafeInjection)object).definePackage)) {
                        return false;
                    }
                    return this.getClassLoadingLock.equals(((UsingUnsafeInjection)object).getClassLoadingLock);
                }

                public int hashCode() {
                    return ((((((this.getClass().hashCode() * 31 + this.accessor.hashCode()) * 31 + this.findLoadedClass.hashCode()) * 31 + this.defineClass.hashCode()) * 31 + this.getDefinedPackage.hashCode()) * 31 + this.getPackage.hashCode()) * 31 + this.definePackage.hashCode()) * 31 + this.getClassLoadingLock.hashCode();
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            @HashCodeAndEqualsPlugin.Enhance
            public static abstract class Direct
            implements Dispatcher,
            Initializable {
                protected final Method findLoadedClass;
                protected final Method defineClass;
                @UnknownNull
                protected final Method getDefinedPackage;
                protected final Method getPackage;
                protected final Method definePackage;

                protected Direct(Method findLoadedClass, Method defineClass, @MaybeNull Method getDefinedPackage, Method getPackage, Method definePackage) {
                    this.findLoadedClass = findLoadedClass;
                    this.defineClass = defineClass;
                    this.getDefinedPackage = getDefinedPackage;
                    this.getPackage = getPackage;
                    this.definePackage = definePackage;
                }

                @SuppressFBWarnings(value={"DP_DO_INSIDE_DO_PRIVILEGED"}, justification="Assuring privilege is explicit user responsibility.")
                protected static Initializable make() throws Exception {
                    Method getDefinedPackage;
                    if (JavaModule.isSupported()) {
                        try {
                            getDefinedPackage = ClassLoader.class.getMethod("getDefinedPackage", String.class);
                        }
                        catch (NoSuchMethodException ignored) {
                            getDefinedPackage = null;
                        }
                    } else {
                        getDefinedPackage = null;
                    }
                    Method getPackage = ClassLoader.class.getDeclaredMethod("getPackage", String.class);
                    getPackage.setAccessible(true);
                    Method findLoadedClass = ClassLoader.class.getDeclaredMethod("findLoadedClass", String.class);
                    findLoadedClass.setAccessible(true);
                    Method defineClass = ClassLoader.class.getDeclaredMethod("defineClass", String.class, byte[].class, Integer.TYPE, Integer.TYPE, ProtectionDomain.class);
                    defineClass.setAccessible(true);
                    Method definePackage = ClassLoader.class.getDeclaredMethod("definePackage", String.class, String.class, String.class, String.class, String.class, String.class, String.class, URL.class);
                    definePackage.setAccessible(true);
                    try {
                        Method getClassLoadingLock = ClassLoader.class.getDeclaredMethod("getClassLoadingLock", String.class);
                        getClassLoadingLock.setAccessible(true);
                        return new ForJava7CapableVm(findLoadedClass, defineClass, getDefinedPackage, getPackage, definePackage, getClassLoadingLock);
                    }
                    catch (NoSuchMethodException ignored) {
                        return new ForLegacyVm(findLoadedClass, defineClass, getDefinedPackage, getPackage, definePackage);
                    }
                }

                @Override
                public boolean isAvailable() {
                    return true;
                }

                @Override
                public Dispatcher initialize() {
                    Object securityManager = SYSTEM.getSecurityManager();
                    if (securityManager != null) {
                        try {
                            CHECK_PERMISSION.invoke(securityManager, SUPPRESS_ACCESS_CHECKS);
                        }
                        catch (InvocationTargetException exception) {
                            return new Unavailable(exception.getTargetException().getMessage());
                        }
                        catch (Exception exception) {
                            return new Unavailable(exception.getMessage());
                        }
                    }
                    return this;
                }

                @Override
                public Class<?> findClass(ClassLoader classLoader, String name) {
                    try {
                        return (Class)this.findLoadedClass.invoke((Object)classLoader, name);
                    }
                    catch (IllegalAccessException exception) {
                        throw new IllegalStateException(exception);
                    }
                    catch (InvocationTargetException exception) {
                        throw new IllegalStateException(exception.getTargetException());
                    }
                }

                @Override
                public Class<?> defineClass(ClassLoader classLoader, String name, byte[] binaryRepresentation, @MaybeNull ProtectionDomain protectionDomain) {
                    try {
                        return (Class)this.defineClass.invoke((Object)classLoader, name, binaryRepresentation, 0, binaryRepresentation.length, protectionDomain);
                    }
                    catch (IllegalAccessException exception) {
                        throw new IllegalStateException(exception);
                    }
                    catch (InvocationTargetException exception) {
                        throw new IllegalStateException(exception.getTargetException());
                    }
                }

                @Override
                @MaybeNull
                public Package getDefinedPackage(ClassLoader classLoader, String name) {
                    if (this.getDefinedPackage == null) {
                        return this.getPackage(classLoader, name);
                    }
                    try {
                        return (Package)this.getDefinedPackage.invoke((Object)classLoader, name);
                    }
                    catch (IllegalAccessException exception) {
                        throw new IllegalStateException(exception);
                    }
                    catch (InvocationTargetException exception) {
                        throw new IllegalStateException(exception.getTargetException());
                    }
                }

                @Override
                public Package getPackage(ClassLoader classLoader, String name) {
                    try {
                        return (Package)this.getPackage.invoke((Object)classLoader, name);
                    }
                    catch (IllegalAccessException exception) {
                        throw new IllegalStateException(exception);
                    }
                    catch (InvocationTargetException exception) {
                        throw new IllegalStateException(exception.getTargetException());
                    }
                }

                @Override
                public Package definePackage(ClassLoader classLoader, String name, @MaybeNull String specificationTitle, @MaybeNull String specificationVersion, @MaybeNull String specificationVendor, @MaybeNull String implementationTitle, @MaybeNull String implementationVersion, @MaybeNull String implementationVendor, @MaybeNull URL sealBase) {
                    try {
                        return (Package)this.definePackage.invoke((Object)classLoader, name, specificationTitle, specificationVersion, specificationVendor, implementationTitle, implementationVersion, implementationVendor, sealBase);
                    }
                    catch (IllegalAccessException exception) {
                        throw new IllegalStateException(exception);
                    }
                    catch (InvocationTargetException exception) {
                        throw new IllegalStateException(exception.getTargetException());
                    }
                }

                public boolean equals(@MaybeNull Object object) {
                    if (this == object) {
                        return true;
                    }
                    if (object == null) {
                        return false;
                    }
                    if (this.getClass() != object.getClass()) {
                        return false;
                    }
                    if (!this.findLoadedClass.equals(((Direct)object).findLoadedClass)) {
                        return false;
                    }
                    if (!this.defineClass.equals(((Direct)object).defineClass)) {
                        return false;
                    }
                    if (!this.getDefinedPackage.equals(((Direct)object).getDefinedPackage)) {
                        return false;
                    }
                    if (!this.getPackage.equals(((Direct)object).getPackage)) {
                        return false;
                    }
                    return this.definePackage.equals(((Direct)object).definePackage);
                }

                public int hashCode() {
                    return ((((this.getClass().hashCode() * 31 + this.findLoadedClass.hashCode()) * 31 + this.defineClass.hashCode()) * 31 + this.getDefinedPackage.hashCode()) * 31 + this.getPackage.hashCode()) * 31 + this.definePackage.hashCode();
                }

                protected static class ForLegacyVm
                extends Direct {
                    protected ForLegacyVm(Method findLoadedClass, Method defineClass, @MaybeNull Method getDefinedPackage, Method getPackage, Method definePackage) {
                        super(findLoadedClass, defineClass, getDefinedPackage, getPackage, definePackage);
                    }

                    public Object getClassLoadingLock(ClassLoader classLoader, String name) {
                        return classLoader;
                    }
                }

                @HashCodeAndEqualsPlugin.Enhance
                protected static class ForJava7CapableVm
                extends Direct {
                    private final Method getClassLoadingLock;

                    protected ForJava7CapableVm(Method findLoadedClass, Method defineClass, @MaybeNull Method getDefinedPackage, Method getPackage, Method definePackage, Method getClassLoadingLock) {
                        super(findLoadedClass, defineClass, getDefinedPackage, getPackage, definePackage);
                        this.getClassLoadingLock = getClassLoadingLock;
                    }

                    public Object getClassLoadingLock(ClassLoader classLoader, String name) {
                        try {
                            return this.getClassLoadingLock.invoke((Object)classLoader, name);
                        }
                        catch (IllegalAccessException exception) {
                            throw new IllegalStateException(exception);
                        }
                        catch (InvocationTargetException exception) {
                            throw new IllegalStateException(exception.getTargetException());
                        }
                    }

                    public boolean equals(@MaybeNull Object object) {
                        if (!super.equals(object)) {
                            return false;
                        }
                        if (this == object) {
                            return true;
                        }
                        if (object == null) {
                            return false;
                        }
                        if (this.getClass() != object.getClass()) {
                            return false;
                        }
                        return this.getClassLoadingLock.equals(((ForJava7CapableVm)object).getClassLoadingLock);
                    }

                    public int hashCode() {
                        return super.hashCode() * 31 + this.getClassLoadingLock.hashCode();
                    }
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            public static enum CreationAction implements PrivilegedAction<Initializable>
            {
                INSTANCE;


                @Override
                @SuppressFBWarnings(value={"REC_CATCH_EXCEPTION"}, justification="Exception should not be rethrown but trigger a fallback.")
                public Initializable run() {
                    try {
                        if (JavaModule.isSupported()) {
                            return UsingUnsafe.isAvailable() ? UsingUnsafeInjection.make() : UsingUnsafeOverride.make();
                        }
                        return Direct.make();
                    }
                    catch (InvocationTargetException exception) {
                        return new Initializable.Unavailable(exception.getTargetException().getMessage());
                    }
                    catch (Exception exception) {
                        return new Initializable.Unavailable(exception.getMessage());
                    }
                }
            }

            public static interface Initializable {
                public boolean isAvailable();

                public Dispatcher initialize();

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                @HashCodeAndEqualsPlugin.Enhance
                public static class Unavailable
                implements Dispatcher,
                Initializable {
                    private final String message;

                    protected Unavailable(String message) {
                        this.message = message;
                    }

                    @Override
                    public boolean isAvailable() {
                        return false;
                    }

                    @Override
                    public Dispatcher initialize() {
                        return this;
                    }

                    @Override
                    public Object getClassLoadingLock(ClassLoader classLoader, String name) {
                        return classLoader;
                    }

                    @Override
                    public Class<?> findClass(ClassLoader classLoader, String name) {
                        try {
                            return classLoader.loadClass(name);
                        }
                        catch (ClassNotFoundException ignored) {
                            return UNDEFINED;
                        }
                    }

                    @Override
                    public Class<?> defineClass(ClassLoader classLoader, String name, byte[] binaryRepresentation, @MaybeNull ProtectionDomain protectionDomain) {
                        throw new UnsupportedOperationException("Cannot define class using reflection: " + this.message);
                    }

                    @Override
                    public Package getDefinedPackage(ClassLoader classLoader, String name) {
                        throw new UnsupportedOperationException("Cannot get defined package using reflection: " + this.message);
                    }

                    @Override
                    public Package getPackage(ClassLoader classLoader, String name) {
                        throw new UnsupportedOperationException("Cannot get package using reflection: " + this.message);
                    }

                    @Override
                    public Package definePackage(ClassLoader classLoader, String name, @MaybeNull String specificationTitle, @MaybeNull String specificationVersion, @MaybeNull String specificationVendor, @MaybeNull String implementationTitle, @MaybeNull String implementationVersion, @MaybeNull String implementationVendor, @MaybeNull URL sealBase) {
                        throw new UnsupportedOperationException("Cannot define package using injection: " + this.message);
                    }

                    public boolean equals(@MaybeNull Object object) {
                        if (this == object) {
                            return true;
                        }
                        if (object == null) {
                            return false;
                        }
                        if (this.getClass() != object.getClass()) {
                            return false;
                        }
                        return this.message.equals(((Unavailable)object).message);
                    }

                    public int hashCode() {
                        return this.getClass().hashCode() * 31 + this.message.hashCode();
                    }
                }
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static abstract class AbstractBase
    implements ClassInjector {
        @Override
        public Map<TypeDescription, Class<?>> inject(Map<? extends TypeDescription, byte[]> types) {
            LinkedHashMap<String, byte[]> binaryRepresentations = new LinkedHashMap<String, byte[]>();
            for (Map.Entry<? extends TypeDescription, byte[]> entry : types.entrySet()) {
                binaryRepresentations.put(entry.getKey().getName(), entry.getValue());
            }
            Map<String, Class<?>> loadedTypes = this.injectRaw(binaryRepresentations);
            LinkedHashMap result = new LinkedHashMap();
            for (TypeDescription typeDescription : types.keySet()) {
                result.put(typeDescription, loadedTypes.get(typeDescription.getName()));
            }
            return result;
        }
    }
}

