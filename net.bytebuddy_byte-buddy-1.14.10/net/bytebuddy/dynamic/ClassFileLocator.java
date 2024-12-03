/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package net.bytebuddy.dynamic;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.jar.JarFile;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.build.AccessControllerPlugin;
import net.bytebuddy.build.HashCodeAndEqualsPlugin;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.JavaModule;
import net.bytebuddy.utility.JavaType;
import net.bytebuddy.utility.StreamDrainer;
import net.bytebuddy.utility.dispatcher.JavaDispatcher;
import net.bytebuddy.utility.nullability.AlwaysNull;
import net.bytebuddy.utility.nullability.MaybeNull;

public interface ClassFileLocator
extends Closeable {
    public static final String CLASS_FILE_EXTENSION = ".class";

    public Resolution locate(String var1) throws IOException;

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @HashCodeAndEqualsPlugin.Enhance
    public static class Compound
    implements ClassFileLocator,
    Closeable {
        private final List<ClassFileLocator> classFileLocators = new ArrayList<ClassFileLocator>();

        public Compound(ClassFileLocator ... classFileLocator) {
            this(Arrays.asList(classFileLocator));
        }

        public Compound(List<? extends ClassFileLocator> classFileLocators) {
            for (ClassFileLocator classFileLocator : classFileLocators) {
                if (classFileLocator instanceof Compound) {
                    this.classFileLocators.addAll(((Compound)classFileLocator).classFileLocators);
                    continue;
                }
                if (classFileLocator instanceof NoOp) continue;
                this.classFileLocators.add(classFileLocator);
            }
        }

        @Override
        public Resolution locate(String name) throws IOException {
            for (ClassFileLocator classFileLocator : this.classFileLocators) {
                Resolution resolution = classFileLocator.locate(name);
                if (!resolution.isResolved()) continue;
                return resolution;
            }
            return new Resolution.Illegal(name);
        }

        @Override
        public void close() throws IOException {
            for (ClassFileLocator classFileLocator : this.classFileLocators) {
                classFileLocator.close();
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
            return ((Object)this.classFileLocators).equals(((Compound)object).classFileLocators);
        }

        public int hashCode() {
            return this.getClass().hashCode() * 31 + ((Object)this.classFileLocators).hashCode();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @HashCodeAndEqualsPlugin.Enhance
    public static class Filtering
    implements ClassFileLocator {
        private final ElementMatcher<? super String> matcher;
        private final ClassFileLocator delegate;

        public Filtering(ElementMatcher<? super String> matcher, ClassFileLocator delegate) {
            this.matcher = matcher;
            this.delegate = delegate;
        }

        @Override
        public Resolution locate(String name) throws IOException {
            return this.matcher.matches(name) ? this.delegate.locate(name) : new Resolution.Illegal(name);
        }

        @Override
        public void close() throws IOException {
            this.delegate.close();
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
            if (!this.matcher.equals(((Filtering)object).matcher)) {
                return false;
            }
            return this.delegate.equals(((Filtering)object).delegate);
        }

        public int hashCode() {
            return (this.getClass().hashCode() * 31 + this.matcher.hashCode()) * 31 + this.delegate.hashCode();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @HashCodeAndEqualsPlugin.Enhance
    public static class PackageDiscriminating
    implements ClassFileLocator {
        private final Map<String, ClassFileLocator> classFileLocators;

        public PackageDiscriminating(Map<String, ClassFileLocator> classFileLocators) {
            this.classFileLocators = classFileLocators;
        }

        @Override
        public Resolution locate(String name) throws IOException {
            int packageIndex = name.lastIndexOf(46);
            ClassFileLocator classFileLocator = this.classFileLocators.get(packageIndex == -1 ? "" : name.substring(0, packageIndex));
            return classFileLocator == null ? new Resolution.Illegal(name) : classFileLocator.locate(name);
        }

        @Override
        public void close() throws IOException {
            for (ClassFileLocator classFileLocator : this.classFileLocators.values()) {
                classFileLocator.close();
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
            return ((Object)this.classFileLocators).equals(((PackageDiscriminating)object).classFileLocators);
        }

        public int hashCode() {
            return this.getClass().hashCode() * 31 + ((Object)this.classFileLocators).hashCode();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @HashCodeAndEqualsPlugin.Enhance
    public static class ForInstrumentation
    implements ClassFileLocator {
        private static final Dispatcher DISPATCHER;
        private final Instrumentation instrumentation;
        private final ClassLoadingDelegate classLoadingDelegate;
        private static final boolean ACCESS_CONTROLLER;

        public ForInstrumentation(Instrumentation instrumentation, @MaybeNull ClassLoader classLoader) {
            this(instrumentation, ClassLoadingDelegate.Default.of(classLoader));
        }

        @AccessControllerPlugin.Enhance
        private static <T> T doPrivileged(PrivilegedAction<T> privilegedAction) {
            PrivilegedAction<T> action;
            if (ACCESS_CONTROLLER) {
                return AccessController.doPrivileged(privilegedAction);
            }
            return action.run();
        }

        public ForInstrumentation(Instrumentation instrumentation, ClassLoadingDelegate classLoadingDelegate) {
            if (!DISPATCHER.isRetransformClassesSupported(instrumentation)) {
                throw new IllegalArgumentException(instrumentation + " does not support retransformation");
            }
            this.instrumentation = instrumentation;
            this.classLoadingDelegate = classLoadingDelegate;
        }

        private static Instrumentation resolveByteBuddyAgentInstrumentation() {
            try {
                Class<?> installer = ClassLoader.getSystemClassLoader().loadClass("net.bytebuddy.agent.Installer");
                JavaModule source = JavaModule.ofType(AgentBuilder.class);
                JavaModule target = JavaModule.ofType(installer);
                if (source != null && !source.canRead(target)) {
                    Class<?> module = Class.forName("java.lang.Module");
                    module.getMethod("addReads", module).invoke(source.unwrap(), target.unwrap());
                }
                return (Instrumentation)installer.getMethod("getInstrumentation", new Class[0]).invoke(null, new Object[0]);
            }
            catch (RuntimeException exception) {
                throw exception;
            }
            catch (Exception exception) {
                throw new IllegalStateException("The Byte Buddy agent is not installed or not accessible", exception);
            }
        }

        public static ClassFileLocator fromInstalledAgent(@MaybeNull ClassLoader classLoader) {
            return new ForInstrumentation(ForInstrumentation.resolveByteBuddyAgentInstrumentation(), classLoader);
        }

        public static ClassFileLocator of(Instrumentation instrumentation, Class<?> type) {
            return new ForInstrumentation(instrumentation, ClassLoadingDelegate.Explicit.of(type));
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         * Enabled aggressive block sorting
         * Enabled unnecessary exception pruning
         * Enabled aggressive exception aggregation
         */
        @Override
        public Resolution locate(String name) {
            try {
                ExtractionClassFileTransformer classFileTransformer = new ExtractionClassFileTransformer(this.classLoadingDelegate.getClassLoader(), name);
                DISPATCHER.addTransformer(this.instrumentation, classFileTransformer, true);
                try {
                    DISPATCHER.retransformClasses(this.instrumentation, new Class[]{this.classLoadingDelegate.locate(name)});
                    byte[] binaryRepresentation = classFileTransformer.getBinaryRepresentation();
                    Resolution resolution = binaryRepresentation == null ? new Resolution.Illegal(name) : new Resolution.Explicit(binaryRepresentation);
                    Object var6_7 = null;
                    this.instrumentation.removeTransformer(classFileTransformer);
                    return resolution;
                }
                catch (Throwable throwable) {
                    Object var6_8 = null;
                    this.instrumentation.removeTransformer(classFileTransformer);
                    throw throwable;
                }
            }
            catch (RuntimeException exception) {
                throw exception;
            }
            catch (Exception ignored) {
                return new Resolution.Illegal(name);
            }
        }

        @Override
        public void close() {
        }

        static /* synthetic */ Object access$000(PrivilegedAction x0) {
            return ForInstrumentation.doPrivileged(x0);
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
            DISPATCHER = ForInstrumentation.doPrivileged(JavaDispatcher.of(Dispatcher.class));
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
            if (!this.instrumentation.equals(((ForInstrumentation)object).instrumentation)) {
                return false;
            }
            return this.classLoadingDelegate.equals(((ForInstrumentation)object).classLoadingDelegate);
        }

        public int hashCode() {
            return (this.getClass().hashCode() * 31 + this.instrumentation.hashCode()) * 31 + this.classLoadingDelegate.hashCode();
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        protected static class ExtractionClassFileTransformer
        implements ClassFileTransformer {
            @AlwaysNull
            private static final byte[] DO_NOT_TRANSFORM = null;
            @MaybeNull
            private final ClassLoader classLoader;
            private final String typeName;
            @MaybeNull
            @SuppressFBWarnings(value={"VO_VOLATILE_REFERENCE_TO_ARRAY"}, justification="The array is not to be modified by contract")
            private volatile byte[] binaryRepresentation;

            protected ExtractionClassFileTransformer(@MaybeNull ClassLoader classLoader, String typeName) {
                this.classLoader = classLoader;
                this.typeName = typeName;
            }

            @Override
            @MaybeNull
            @SuppressFBWarnings(value={"EI_EXPOSE_REP", "EI_EXPOSE_REP2"}, justification="The array is not modified by class contract.")
            public byte[] transform(@MaybeNull ClassLoader classLoader, @MaybeNull String internalName, @MaybeNull Class<?> redefinedType, ProtectionDomain protectionDomain, byte[] binaryRepresentation) {
                if (internalName != null && ElementMatchers.isChildOf(this.classLoader).matches(classLoader) && this.typeName.equals(internalName.replace('/', '.'))) {
                    this.binaryRepresentation = (byte[])binaryRepresentation.clone();
                }
                return DO_NOT_TRANSFORM;
            }

            @MaybeNull
            @SuppressFBWarnings(value={"EI_EXPOSE_REP"}, justification="The array is not to be modified by contract")
            protected byte[] getBinaryRepresentation() {
                return this.binaryRepresentation;
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static interface ClassLoadingDelegate {
            public Class<?> locate(String var1) throws ClassNotFoundException;

            @MaybeNull
            public ClassLoader getClassLoader();

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            @HashCodeAndEqualsPlugin.Enhance
            public static class Explicit
            implements ClassLoadingDelegate {
                private final ClassLoadingDelegate fallbackDelegate;
                private final Map<String, Class<?>> types;

                public Explicit(@MaybeNull ClassLoader classLoader, Collection<? extends Class<?>> types) {
                    this(Default.of(classLoader), types);
                }

                public Explicit(ClassLoadingDelegate fallbackDelegate, Collection<? extends Class<?>> types) {
                    this.fallbackDelegate = fallbackDelegate;
                    this.types = new HashMap();
                    for (Class<?> type : types) {
                        this.types.put(TypeDescription.ForLoadedType.getName(type), type);
                    }
                }

                public static ClassLoadingDelegate of(Class<?> type) {
                    return new Explicit(type.getClassLoader(), Collections.singleton(type));
                }

                @Override
                public Class<?> locate(String name) throws ClassNotFoundException {
                    Class<?> type = this.types.get(name);
                    return type == null ? this.fallbackDelegate.locate(name) : type;
                }

                @Override
                @MaybeNull
                public ClassLoader getClassLoader() {
                    return this.fallbackDelegate.getClassLoader();
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
                    if (!this.fallbackDelegate.equals(((Explicit)object).fallbackDelegate)) {
                        return false;
                    }
                    return ((Object)this.types).equals(((Explicit)object).types);
                }

                public int hashCode() {
                    return (this.getClass().hashCode() * 31 + this.fallbackDelegate.hashCode()) * 31 + ((Object)this.types).hashCode();
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            public static class ForDelegatingClassLoader
            extends Default {
                private static final String DELEGATING_CLASS_LOADER_NAME = "sun.reflect.DelegatingClassLoader";
                private static final int ONLY = 0;
                private static final Dispatcher.Initializable DISPATCHER;
                private static final boolean ACCESS_CONTROLLER;

                protected ForDelegatingClassLoader(ClassLoader classLoader) {
                    super(classLoader);
                }

                @AccessControllerPlugin.Enhance
                private static <T> T doPrivileged(PrivilegedAction<T> privilegedAction) {
                    PrivilegedAction<T> action;
                    if (ACCESS_CONTROLLER) {
                        return AccessController.doPrivileged(privilegedAction);
                    }
                    return action.run();
                }

                protected static boolean isDelegating(@MaybeNull ClassLoader classLoader) {
                    return classLoader != null && classLoader.getClass().getName().equals(DELEGATING_CLASS_LOADER_NAME);
                }

                @Override
                public Class<?> locate(String name) throws ClassNotFoundException {
                    Vector<Class<?>> classes;
                    try {
                        classes = DISPATCHER.initialize().extract(this.classLoader);
                    }
                    catch (RuntimeException ignored) {
                        return super.locate(name);
                    }
                    if (classes.size() != 1) {
                        return super.locate(name);
                    }
                    Class<?> type = classes.get(0);
                    return TypeDescription.ForLoadedType.getName(type).equals(name) ? type : super.locate(name);
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
                    DISPATCHER = ForDelegatingClassLoader.doPrivileged(Dispatcher.CreationAction.INSTANCE);
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                protected static interface Dispatcher {
                    public Vector<Class<?>> extract(ClassLoader var1);

                    @HashCodeAndEqualsPlugin.Enhance
                    public static class Unresolved
                    implements Initializable {
                        private final String message;

                        public Unresolved(String message) {
                            this.message = message;
                        }

                        public Dispatcher initialize() {
                            throw new UnsupportedOperationException("Could not locate classes vector: " + this.message);
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
                            return this.message.equals(((Unresolved)object).message);
                        }

                        public int hashCode() {
                            return this.getClass().hashCode() * 31 + this.message.hashCode();
                        }
                    }

                    /*
                     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                     */
                    @HashCodeAndEqualsPlugin.Enhance
                    public static class Resolved
                    implements Dispatcher,
                    Initializable,
                    PrivilegedAction<Dispatcher> {
                        private final Field field;
                        private static final boolean ACCESS_CONTROLLER;

                        public Resolved(Field field) {
                            this.field = field;
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
                        public Dispatcher initialize() {
                            return Resolved.doPrivileged(this);
                        }

                        @Override
                        public Vector<Class<?>> extract(ClassLoader classLoader) {
                            try {
                                return (Vector)this.field.get(classLoader);
                            }
                            catch (IllegalAccessException exception) {
                                throw new IllegalStateException("Cannot access field", exception);
                            }
                        }

                        @Override
                        public Dispatcher run() {
                            this.field.setAccessible(true);
                            return this;
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
                            return this.field.equals(((Resolved)object).field);
                        }

                        public int hashCode() {
                            return this.getClass().hashCode() * 31 + this.field.hashCode();
                        }

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
                        }
                    }

                    /*
                     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                     */
                    public static enum CreationAction implements PrivilegedAction<Initializable>
                    {
                        INSTANCE;


                        @Override
                        public Initializable run() {
                            try {
                                return new Resolved(ClassLoader.class.getDeclaredField("classes"));
                            }
                            catch (Exception exception) {
                                return new Unresolved(exception.getMessage());
                            }
                        }
                    }

                    public static interface Initializable {
                        public Dispatcher initialize();
                    }
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            @HashCodeAndEqualsPlugin.Enhance
            public static class Default
            implements ClassLoadingDelegate {
                private static final ClassLoader BOOT_LOADER_PROXY = (ClassLoader)ForInstrumentation.access$000(BootLoaderProxyCreationAction.INSTANCE);
                protected final ClassLoader classLoader;

                protected Default(ClassLoader classLoader) {
                    this.classLoader = classLoader;
                }

                public static ClassLoadingDelegate of(@MaybeNull ClassLoader classLoader) {
                    return ForDelegatingClassLoader.isDelegating(classLoader) ? new ForDelegatingClassLoader(classLoader) : new Default(classLoader == null ? BOOT_LOADER_PROXY : classLoader);
                }

                @Override
                public Class<?> locate(String name) throws ClassNotFoundException {
                    return this.classLoader.loadClass(name);
                }

                @Override
                @MaybeNull
                public ClassLoader getClassLoader() {
                    return this.classLoader == BOOT_LOADER_PROXY ? ClassLoadingStrategy.BOOTSTRAP_LOADER : this.classLoader;
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
                    return this.classLoader.equals(((Default)object).classLoader);
                }

                public int hashCode() {
                    return this.getClass().hashCode() * 31 + this.classLoader.hashCode();
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                protected static enum BootLoaderProxyCreationAction implements PrivilegedAction<ClassLoader>
                {
                    INSTANCE;


                    @Override
                    public ClassLoader run() {
                        return new URLClassLoader(new URL[0], ClassLoadingStrategy.BOOTSTRAP_LOADER);
                    }
                }
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        @JavaDispatcher.Proxied(value="java.lang.instrument.Instrumentation")
        protected static interface Dispatcher {
            @JavaDispatcher.Proxied(value="isRetransformClassesSupported")
            public boolean isRetransformClassesSupported(Instrumentation var1);

            @JavaDispatcher.Proxied(value="addTransformer")
            public void addTransformer(Instrumentation var1, ClassFileTransformer var2, boolean var3);

            @JavaDispatcher.Proxied(value="retransformClasses")
            public void retransformClasses(Instrumentation var1, Class<?>[] var2) throws UnmodifiableClassException;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @HashCodeAndEqualsPlugin.Enhance
    public static class ForUrl
    implements ClassFileLocator {
        private final ClassLoader classLoader;
        private static final boolean ACCESS_CONTROLLER;

        public ForUrl(URL ... url) {
            this.classLoader = ForUrl.doPrivileged(new ClassLoaderCreationAction(url));
        }

        public ForUrl(Collection<? extends URL> urls) {
            this(urls.toArray(new URL[0]));
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
        public Resolution locate(String name) throws IOException {
            return ForClassLoader.locate(this.classLoader, name);
        }

        @Override
        public void close() throws IOException {
            if (this.classLoader instanceof Closeable) {
                ((Closeable)((Object)this.classLoader)).close();
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
            return this.classLoader.equals(((ForUrl)object).classLoader);
        }

        public int hashCode() {
            return this.getClass().hashCode() * 31 + this.classLoader.hashCode();
        }

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
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        @HashCodeAndEqualsPlugin.Enhance
        protected static class ClassLoaderCreationAction
        implements PrivilegedAction<ClassLoader> {
            private final URL[] url;

            protected ClassLoaderCreationAction(URL[] url) {
                this.url = url;
            }

            @Override
            public ClassLoader run() {
                return new URLClassLoader(this.url, ClassLoadingStrategy.BOOTSTRAP_LOADER);
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
                return Arrays.equals(this.url, ((ClassLoaderCreationAction)object).url);
            }

            public int hashCode() {
                return this.getClass().hashCode() * 31 + Arrays.hashCode(this.url);
            }
        }
    }

    @HashCodeAndEqualsPlugin.Enhance
    public static class ForFolder
    implements ClassFileLocator {
        private final File folder;

        public ForFolder(File folder) {
            this.folder = folder;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public Resolution locate(String name) throws IOException {
            File file = new File(this.folder, name.replace('.', File.separatorChar) + ClassFileLocator.CLASS_FILE_EXTENSION);
            if (file.exists()) {
                Resolution.Explicit explicit;
                FileInputStream inputStream = new FileInputStream(file);
                try {
                    explicit = new Resolution.Explicit(StreamDrainer.DEFAULT.drain(inputStream));
                    Object var6_5 = null;
                }
                catch (Throwable throwable) {
                    Object var6_6 = null;
                    ((InputStream)inputStream).close();
                    throw throwable;
                }
                ((InputStream)inputStream).close();
                return explicit;
            }
            return new Resolution.Illegal(name);
        }

        public void close() {
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
            return this.folder.equals(((ForFolder)object).folder);
        }

        public int hashCode() {
            return this.getClass().hashCode() * 31 + this.folder.hashCode();
        }
    }

    @HashCodeAndEqualsPlugin.Enhance
    public static class ForModuleFile
    implements ClassFileLocator {
        private static final String JMOD_FILE_EXTENSION = ".jmod";
        private static final List<String> BOOT_LOCATIONS = Arrays.asList("jmods", "../jmods", "modules");
        private final ZipFile zipFile;

        public ForModuleFile(ZipFile zipFile) {
            this.zipFile = zipFile;
        }

        public static ClassFileLocator ofBootPath() throws IOException {
            String javaHome = System.getProperty("java.home").replace('\\', '/');
            File bootPath = null;
            for (String location : BOOT_LOCATIONS) {
                File candidate = new File(javaHome, location);
                if (!candidate.isDirectory()) continue;
                bootPath = candidate;
                break;
            }
            if (bootPath == null) {
                throw new IllegalStateException("Boot modules do not exist in " + javaHome + " for any of " + BOOT_LOCATIONS);
            }
            return ForModuleFile.ofBootPath(bootPath);
        }

        public static ClassFileLocator ofBootPath(File bootPath) throws IOException {
            File[] module = bootPath.listFiles();
            if (module == null) {
                return NoOp.INSTANCE;
            }
            ArrayList<ClassFileLocator> classFileLocators = new ArrayList<ClassFileLocator>(module.length);
            for (File aModule : module) {
                if (aModule.isFile()) {
                    classFileLocators.add(ForModuleFile.of(aModule));
                    continue;
                }
                if (!aModule.isDirectory()) continue;
                classFileLocators.add(new ForFolder(aModule));
            }
            return new Compound(classFileLocators);
        }

        public static ClassFileLocator ofModulePath() throws IOException {
            String modulePath = System.getProperty("jdk.module.path");
            return modulePath == null ? NoOp.INSTANCE : ForModuleFile.ofModulePath(modulePath);
        }

        public static ClassFileLocator ofModulePath(String modulePath) throws IOException {
            return ForModuleFile.ofModulePath(modulePath, System.getProperty("user.dir"));
        }

        public static ClassFileLocator ofModulePath(String modulePath, String baseFolder) throws IOException {
            ArrayList<ClassFileLocator> classFileLocators = new ArrayList<ClassFileLocator>();
            for (String element : Pattern.compile(System.getProperty("path.separator"), 16).split(modulePath)) {
                File file = new File(baseFolder, element);
                if (file.isDirectory()) {
                    File[] module = file.listFiles();
                    if (module == null) continue;
                    for (File aModule : module) {
                        if (aModule.isDirectory()) {
                            classFileLocators.add(new ForFolder(aModule));
                            continue;
                        }
                        if (!aModule.isFile()) continue;
                        classFileLocators.add(aModule.getName().endsWith(JMOD_FILE_EXTENSION) ? ForModuleFile.of(aModule) : ForJarFile.of(aModule));
                    }
                    continue;
                }
                if (!file.isFile()) continue;
                classFileLocators.add(file.getName().endsWith(JMOD_FILE_EXTENSION) ? ForModuleFile.of(file) : ForJarFile.of(file));
            }
            return new Compound(classFileLocators);
        }

        public static ClassFileLocator of(File file) throws IOException {
            return new ForModuleFile(new ZipFile(file));
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public Resolution locate(String name) throws IOException {
            Resolution.Explicit explicit;
            ZipEntry zipEntry = this.zipFile.getEntry("classes/" + name.replace('.', '/') + ClassFileLocator.CLASS_FILE_EXTENSION);
            if (zipEntry == null) {
                return new Resolution.Illegal(name);
            }
            InputStream inputStream = this.zipFile.getInputStream(zipEntry);
            try {
                explicit = new Resolution.Explicit(StreamDrainer.DEFAULT.drain(inputStream));
                Object var6_5 = null;
            }
            catch (Throwable throwable) {
                Object var6_6 = null;
                inputStream.close();
                throw throwable;
            }
            inputStream.close();
            return explicit;
        }

        public void close() throws IOException {
            this.zipFile.close();
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
            return this.zipFile.equals(((ForModuleFile)object).zipFile);
        }

        public int hashCode() {
            return this.getClass().hashCode() * 31 + this.zipFile.hashCode();
        }
    }

    @HashCodeAndEqualsPlugin.Enhance
    public static class ForJarFile
    implements ClassFileLocator {
        private static final List<String> RUNTIME_LOCATIONS = Arrays.asList("lib/rt.jar", "../lib/rt.jar", "../Classes/classes.jar");
        private final JarFile jarFile;

        public ForJarFile(JarFile jarFile) {
            this.jarFile = jarFile;
        }

        public static ClassFileLocator of(File file) throws IOException {
            return new ForJarFile(new JarFile(file));
        }

        public static ClassFileLocator ofClassPath() throws IOException {
            return ForJarFile.ofClassPath(System.getProperty("java.class.path"));
        }

        public static ClassFileLocator ofClassPath(String classPath) throws IOException {
            ArrayList<ClassFileLocator> classFileLocators = new ArrayList<ClassFileLocator>();
            for (String element : Pattern.compile(System.getProperty("path.separator"), 16).split(classPath)) {
                File file = new File(element);
                if (file.isDirectory()) {
                    classFileLocators.add(new ForFolder(file));
                    continue;
                }
                if (!file.isFile()) continue;
                classFileLocators.add(ForJarFile.of(file));
            }
            return new Compound(classFileLocators);
        }

        public static ClassFileLocator ofRuntimeJar() throws IOException {
            String javaHome = System.getProperty("java.home").replace('\\', '/');
            File runtimeJar = null;
            for (String location : RUNTIME_LOCATIONS) {
                File candidate = new File(javaHome, location);
                if (!candidate.isFile()) continue;
                runtimeJar = candidate;
                break;
            }
            if (runtimeJar == null) {
                throw new IllegalStateException("Runtime jar does not exist in " + javaHome + " for any of " + RUNTIME_LOCATIONS);
            }
            return ForJarFile.of(runtimeJar);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public Resolution locate(String name) throws IOException {
            Resolution.Explicit explicit;
            ZipEntry zipEntry = this.jarFile.getEntry(name.replace('.', '/') + ClassFileLocator.CLASS_FILE_EXTENSION);
            if (zipEntry == null) {
                return new Resolution.Illegal(name);
            }
            InputStream inputStream = this.jarFile.getInputStream(zipEntry);
            try {
                explicit = new Resolution.Explicit(StreamDrainer.DEFAULT.drain(inputStream));
                Object var6_5 = null;
            }
            catch (Throwable throwable) {
                Object var6_6 = null;
                inputStream.close();
                throw throwable;
            }
            inputStream.close();
            return explicit;
        }

        public void close() throws IOException {
            this.jarFile.close();
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
            return this.jarFile.equals(((ForJarFile)object).jarFile);
        }

        public int hashCode() {
            return this.getClass().hashCode() * 31 + this.jarFile.hashCode();
        }
    }

    @HashCodeAndEqualsPlugin.Enhance
    public static class ForModule
    implements ClassFileLocator {
        private static final Object[] NO_ARGUMENT = new Object[0];
        private final JavaModule module;

        protected ForModule(JavaModule module) {
            this.module = module;
        }

        @SuppressFBWarnings(value={"REC_CATCH_EXCEPTION"}, justification="Exception should always be wrapped for clarity")
        public static ClassFileLocator ofBootLayer() {
            try {
                HashMap<String, ClassFileLocator> bootModules = new HashMap<String, ClassFileLocator>();
                Class<?> layerType = Class.forName("java.lang.ModuleLayer");
                Method getPackages = JavaType.MODULE.load().getMethod("getPackages", new Class[0]);
                for (Object rawModule : (Set)layerType.getMethod("modules", new Class[0]).invoke(layerType.getMethod("boot", new Class[0]).invoke(null, new Object[0]), new Object[0])) {
                    ClassFileLocator classFileLocator = ForModule.of(JavaModule.of(rawModule));
                    for (Object packageName : (Set)getPackages.invoke(rawModule, NO_ARGUMENT)) {
                        bootModules.put((String)packageName, classFileLocator);
                    }
                }
                return new PackageDiscriminating(bootModules);
            }
            catch (Exception exception) {
                throw new IllegalStateException("Cannot process boot layer", exception);
            }
        }

        public static ClassFileLocator of(JavaModule module) {
            return module.isNamed() ? new ForModule(module) : ForClassLoader.of(module.getClassLoader());
        }

        public Resolution locate(String name) throws IOException {
            return ForModule.locate(this.module, name);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        protected static Resolution locate(JavaModule module, String typeName) throws IOException {
            InputStream inputStream = module.getResourceAsStream(typeName.replace('.', '/') + ClassFileLocator.CLASS_FILE_EXTENSION);
            if (inputStream != null) {
                Resolution.Explicit explicit;
                try {
                    explicit = new Resolution.Explicit(StreamDrainer.DEFAULT.drain(inputStream));
                    Object var5_4 = null;
                }
                catch (Throwable throwable) {
                    Object var5_5 = null;
                    inputStream.close();
                    throw throwable;
                }
                inputStream.close();
                return explicit;
            }
            return new Resolution.Illegal(typeName);
        }

        public void close() {
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
            return this.module.equals(((ForModule)object).module);
        }

        public int hashCode() {
            return this.getClass().hashCode() * 31 + this.module.hashCode();
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static class WeaklyReferenced
        extends WeakReference<Object>
        implements ClassFileLocator {
            private final int hashCode;

            protected WeaklyReferenced(Object module) {
                super(module);
                this.hashCode = System.identityHashCode(module);
            }

            public static ClassFileLocator of(JavaModule module) {
                if (module.isNamed()) {
                    return module.getClassLoader() == null || module.getClassLoader() == ClassLoader.getSystemClassLoader() || module.getClassLoader() == ClassLoader.getSystemClassLoader().getParent() ? new ForModule(module) : new WeaklyReferenced(module.unwrap());
                }
                return ForClassLoader.WeaklyReferenced.of(module.getClassLoader());
            }

            @Override
            public Resolution locate(String name) throws IOException {
                Object module = this.get();
                return module == null ? new Resolution.Illegal(name) : ForModule.locate(JavaModule.of(module), name);
            }

            @Override
            public void close() {
            }

            public int hashCode() {
                return this.hashCode;
            }

            public boolean equals(@MaybeNull Object other) {
                if (this == other) {
                    return true;
                }
                if (other == null || this.getClass() != other.getClass()) {
                    return false;
                }
                WeaklyReferenced weaklyReferenced = (WeaklyReferenced)other;
                Object module = weaklyReferenced.get();
                return module != null && this.get() == module;
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @HashCodeAndEqualsPlugin.Enhance
    public static class ForClassLoader
    implements ClassFileLocator {
        private static final ClassLoader BOOT_LOADER_PROXY;
        private final ClassLoader classLoader;
        private static final boolean ACCESS_CONTROLLER;

        protected ForClassLoader(ClassLoader classLoader) {
            this.classLoader = classLoader;
        }

        @AccessControllerPlugin.Enhance
        private static <T> T doPrivileged(PrivilegedAction<T> privilegedAction) {
            PrivilegedAction<T> action;
            if (ACCESS_CONTROLLER) {
                return AccessController.doPrivileged(privilegedAction);
            }
            return action.run();
        }

        public static ClassFileLocator ofSystemLoader() {
            return new ForClassLoader(ClassLoader.getSystemClassLoader());
        }

        public static ClassFileLocator ofPlatformLoader() {
            return ForClassLoader.of(ClassLoader.getSystemClassLoader().getParent());
        }

        public static ClassFileLocator ofBootLoader() {
            return new ForClassLoader(BOOT_LOADER_PROXY);
        }

        public static ClassFileLocator of(@MaybeNull ClassLoader classLoader) {
            return new ForClassLoader(classLoader == null ? BOOT_LOADER_PROXY : classLoader);
        }

        public static byte[] read(Class<?> type) {
            try {
                ClassLoader classLoader = type.getClassLoader();
                return ForClassLoader.locate(classLoader == null ? BOOT_LOADER_PROXY : classLoader, TypeDescription.ForLoadedType.getName(type)).resolve();
            }
            catch (IOException exception) {
                throw new IllegalStateException("Cannot read class file for " + type, exception);
            }
        }

        public static Map<Class<?>, byte[]> read(Class<?> ... type) {
            return ForClassLoader.read(Arrays.asList(type));
        }

        public static Map<Class<?>, byte[]> read(Collection<? extends Class<?>> types) {
            HashMap result = new HashMap();
            for (Class<?> type : types) {
                result.put(type, ForClassLoader.read(type));
            }
            return result;
        }

        public static Map<String, byte[]> readToNames(Class<?> ... type) {
            return ForClassLoader.readToNames(Arrays.asList(type));
        }

        public static Map<String, byte[]> readToNames(Collection<? extends Class<?>> types) {
            HashMap<String, byte[]> result = new HashMap<String, byte[]>();
            for (Class<?> type : types) {
                result.put(type.getName(), ForClassLoader.read(type));
            }
            return result;
        }

        @Override
        public Resolution locate(String name) throws IOException {
            return ForClassLoader.locate(this.classLoader, name);
        }

        @Override
        public void close() {
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        protected static Resolution locate(ClassLoader classLoader, String name) throws IOException {
            InputStream inputStream = classLoader.getResourceAsStream(name.replace('.', '/') + ClassFileLocator.CLASS_FILE_EXTENSION);
            if (inputStream != null) {
                Resolution.Explicit explicit;
                try {
                    explicit = new Resolution.Explicit(StreamDrainer.DEFAULT.drain(inputStream));
                    Object var5_4 = null;
                }
                catch (Throwable throwable) {
                    Object var5_5 = null;
                    inputStream.close();
                    throw throwable;
                }
                inputStream.close();
                return explicit;
            }
            return new Resolution.Illegal(name);
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
            BOOT_LOADER_PROXY = ForClassLoader.doPrivileged(BootLoaderProxyCreationAction.INSTANCE);
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
            return this.classLoader.equals(((ForClassLoader)object).classLoader);
        }

        public int hashCode() {
            return this.getClass().hashCode() * 31 + this.classLoader.hashCode();
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static class WeaklyReferenced
        extends WeakReference<ClassLoader>
        implements ClassFileLocator {
            private final int hashCode;

            protected WeaklyReferenced(ClassLoader classLoader) {
                super(classLoader);
                this.hashCode = System.identityHashCode(classLoader);
            }

            public static ClassFileLocator of(@MaybeNull ClassLoader classLoader) {
                return classLoader == null || classLoader == ClassLoader.getSystemClassLoader() || classLoader == ClassLoader.getSystemClassLoader().getParent() ? ForClassLoader.of(classLoader) : new WeaklyReferenced(classLoader);
            }

            @Override
            public Resolution locate(String name) throws IOException {
                ClassLoader classLoader = (ClassLoader)this.get();
                return classLoader == null ? new Resolution.Illegal(name) : ForClassLoader.locate(classLoader, name);
            }

            @Override
            public void close() {
            }

            public int hashCode() {
                return this.hashCode;
            }

            public boolean equals(@MaybeNull Object other) {
                if (this == other) {
                    return true;
                }
                if (other == null || this.getClass() != other.getClass()) {
                    return false;
                }
                WeaklyReferenced weaklyReferenced = (WeaklyReferenced)other;
                ClassLoader classLoader = (ClassLoader)weaklyReferenced.get();
                return classLoader != null && this.get() == classLoader;
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        protected static enum BootLoaderProxyCreationAction implements PrivilegedAction<ClassLoader>
        {
            INSTANCE;


            @Override
            public ClassLoader run() {
                return new URLClassLoader(new URL[0], ClassLoadingStrategy.BOOTSTRAP_LOADER);
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @HashCodeAndEqualsPlugin.Enhance
    public static class Simple
    implements ClassFileLocator {
        private final Map<String, byte[]> classFiles;

        public Simple(Map<String, byte[]> classFiles) {
            this.classFiles = classFiles;
        }

        public static ClassFileLocator of(String typeName, byte[] binaryRepresentation) {
            return new Simple(Collections.singletonMap(typeName, binaryRepresentation));
        }

        public static ClassFileLocator of(DynamicType dynamicType) {
            return Simple.of(dynamicType.getAllTypes());
        }

        public static ClassFileLocator of(Map<TypeDescription, byte[]> binaryRepresentations) {
            HashMap<String, byte[]> classFiles = new HashMap<String, byte[]>();
            for (Map.Entry<TypeDescription, byte[]> entry : binaryRepresentations.entrySet()) {
                classFiles.put(entry.getKey().getName(), entry.getValue());
            }
            return new Simple(classFiles);
        }

        public static ClassFileLocator ofResources(Map<String, byte[]> binaryRepresentations) {
            HashMap<String, byte[]> classFiles = new HashMap<String, byte[]>();
            for (Map.Entry<String, byte[]> entry : binaryRepresentations.entrySet()) {
                if (!entry.getKey().endsWith(ClassFileLocator.CLASS_FILE_EXTENSION)) continue;
                classFiles.put(entry.getKey().substring(0, entry.getKey().length() - ClassFileLocator.CLASS_FILE_EXTENSION.length()).replace('/', '.'), entry.getValue());
            }
            return new Simple(classFiles);
        }

        @Override
        public Resolution locate(String name) {
            byte[] binaryRepresentation = this.classFiles.get(name);
            return binaryRepresentation == null ? new Resolution.Illegal(name) : new Resolution.Explicit(binaryRepresentation);
        }

        @Override
        public void close() {
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
            return ((Object)this.classFiles).equals(((Simple)object).classFiles);
        }

        public int hashCode() {
            return this.getClass().hashCode() * 31 + ((Object)this.classFiles).hashCode();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum NoOp implements ClassFileLocator
    {
        INSTANCE;


        @Override
        public Resolution locate(String name) {
            return new Resolution.Illegal(name);
        }

        @Override
        public void close() {
        }
    }

    public static interface Resolution {
        public boolean isResolved();

        public byte[] resolve();

        @HashCodeAndEqualsPlugin.Enhance
        public static class Explicit
        implements Resolution {
            private final byte[] binaryRepresentation;

            @SuppressFBWarnings(value={"EI_EXPOSE_REP2"}, justification="The array is not modified by class contract.")
            public Explicit(byte[] binaryRepresentation) {
                this.binaryRepresentation = binaryRepresentation;
            }

            public boolean isResolved() {
                return true;
            }

            @SuppressFBWarnings(value={"EI_EXPOSE_REP"}, justification="The array is not modified by class contract.")
            public byte[] resolve() {
                return this.binaryRepresentation;
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
                return Arrays.equals(this.binaryRepresentation, ((Explicit)object).binaryRepresentation);
            }

            public int hashCode() {
                return this.getClass().hashCode() * 31 + Arrays.hashCode(this.binaryRepresentation);
            }
        }

        @HashCodeAndEqualsPlugin.Enhance
        public static class Illegal
        implements Resolution {
            private final String typeName;

            public Illegal(String typeName) {
                this.typeName = typeName;
            }

            public boolean isResolved() {
                return false;
            }

            public byte[] resolve() {
                throw new IllegalStateException("Could not locate class file for " + this.typeName);
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
                return this.typeName.equals(((Illegal)object).typeName);
            }

            public int hashCode() {
                return this.getClass().hashCode() * 31 + this.typeName.hashCode();
            }
        }
    }
}

