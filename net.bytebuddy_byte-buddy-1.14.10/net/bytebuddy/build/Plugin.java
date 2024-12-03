/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package net.bytebuddy.build;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.ClassFileVersion;
import net.bytebuddy.build.EntryPoint;
import net.bytebuddy.build.HashCodeAndEqualsPlugin;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.TypeResolutionStrategy;
import net.bytebuddy.dynamic.scaffold.inline.MethodNameTransformer;
import net.bytebuddy.implementation.LoadedTypeInitializer;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.pool.TypePool;
import net.bytebuddy.utility.CompoundList;
import net.bytebuddy.utility.FileSystem;
import net.bytebuddy.utility.nullability.AlwaysNull;
import net.bytebuddy.utility.nullability.MaybeNull;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface Plugin
extends ElementMatcher<TypeDescription>,
Closeable {
    public DynamicType.Builder<?> apply(DynamicType.Builder<?> var1, TypeDescription var2, ClassFileLocator var3);

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @HashCodeAndEqualsPlugin.Enhance
    public static abstract class ForElementMatcher
    implements Plugin {
        private final ElementMatcher<? super TypeDescription> matcher;

        protected ForElementMatcher(ElementMatcher<? super TypeDescription> matcher) {
            this.matcher = matcher;
        }

        @Override
        public boolean matches(@MaybeNull TypeDescription target) {
            return this.matcher.matches(target);
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
            return this.matcher.equals(((ForElementMatcher)object).matcher);
        }

        public int hashCode() {
            return this.getClass().hashCode() * 31 + this.matcher.hashCode();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @HashCodeAndEqualsPlugin.Enhance
    public static class NoOp
    implements Plugin,
    Factory {
        @Override
        public Plugin make() {
            return this;
        }

        @Override
        public boolean matches(@MaybeNull TypeDescription target) {
            return false;
        }

        @Override
        public DynamicType.Builder<?> apply(DynamicType.Builder<?> builder, TypeDescription typeDescription, ClassFileLocator classFileLocator) {
            throw new IllegalStateException("Cannot apply non-operational plugin");
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
            return this.getClass() == object.getClass();
        }

        public int hashCode() {
            return this.getClass().hashCode();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static interface Engine {
        public static final String CLASS_FILE_EXTENSION = ".class";
        public static final String MODULE_INFO = "module-info.class";
        public static final String PACKAGE_INFO = "package-info.class";
        public static final String PLUGIN_FILE = "META-INF/net.bytebuddy/build.plugins";

        public Engine with(ByteBuddy var1);

        public Engine with(TypeStrategy var1);

        public Engine with(PoolStrategy var1);

        public Engine with(ClassFileLocator var1);

        public Engine with(Listener var1);

        public Engine withoutErrorHandlers();

        public Engine withErrorHandlers(ErrorHandler ... var1);

        public Engine withErrorHandlers(List<? extends ErrorHandler> var1);

        public Engine withParallelTransformation(int var1);

        public Engine with(Dispatcher.Factory var1);

        public Engine ignore(ElementMatcher<? super TypeDescription> var1);

        public Summary apply(File var1, File var2, Factory ... var3) throws IOException;

        public Summary apply(File var1, File var2, List<? extends Factory> var3) throws IOException;

        public Summary apply(Source var1, Target var2, Factory ... var3) throws IOException;

        public Summary apply(Source var1, Target var2, List<? extends Factory> var3) throws IOException;

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        @HashCodeAndEqualsPlugin.Enhance
        public static class Default
        extends AbstractBase {
            private final ByteBuddy byteBuddy;
            private final TypeStrategy typeStrategy;
            private final PoolStrategy poolStrategy;
            private final ClassFileLocator classFileLocator;
            private final Listener listener;
            private final ErrorHandler errorHandler;
            private final Dispatcher.Factory dispatcherFactory;
            private final ElementMatcher.Junction<? super TypeDescription> ignoredTypeMatcher;

            public Default() {
                this(new ByteBuddy());
            }

            public Default(ByteBuddy byteBuddy) {
                this(byteBuddy, TypeStrategy.Default.REBASE);
            }

            protected Default(ByteBuddy byteBuddy, TypeStrategy typeStrategy) {
                this(byteBuddy, typeStrategy, PoolStrategy.Default.FAST, ClassFileLocator.NoOp.INSTANCE, Listener.NoOp.INSTANCE, new ErrorHandler.Compound(ErrorHandler.Failing.FAIL_FAST, ErrorHandler.Enforcing.ALL_TYPES_RESOLVED, ErrorHandler.Enforcing.NO_LIVE_INITIALIZERS), Dispatcher.ForSerialTransformation.Factory.INSTANCE, ElementMatchers.none());
            }

            protected Default(ByteBuddy byteBuddy, TypeStrategy typeStrategy, PoolStrategy poolStrategy, ClassFileLocator classFileLocator, Listener listener, ErrorHandler errorHandler, Dispatcher.Factory dispatcherFactory, ElementMatcher.Junction<? super TypeDescription> ignoredTypeMatcher) {
                this.byteBuddy = byteBuddy;
                this.typeStrategy = typeStrategy;
                this.poolStrategy = poolStrategy;
                this.classFileLocator = classFileLocator;
                this.listener = listener;
                this.errorHandler = errorHandler;
                this.dispatcherFactory = dispatcherFactory;
                this.ignoredTypeMatcher = ignoredTypeMatcher;
            }

            public static Engine of(EntryPoint entryPoint, ClassFileVersion classFileVersion, MethodNameTransformer methodNameTransformer) {
                return new Default(entryPoint.byteBuddy(classFileVersion), new TypeStrategy.ForEntryPoint(entryPoint, methodNameTransformer));
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            public static Set<String> scan(ClassLoader classLoader) throws IOException {
                HashSet<String> plugins = new HashSet<String>();
                Enumeration<URL> enumeration = classLoader.getResources(Engine.PLUGIN_FILE);
                while (enumeration.hasMoreElements()) {
                    Object var6_5;
                    BufferedReader reader = new BufferedReader(new InputStreamReader(enumeration.nextElement().openStream(), "UTF-8"));
                    try {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            plugins.add(line);
                        }
                        var6_5 = null;
                    }
                    catch (Throwable throwable) {
                        var6_5 = null;
                        reader.close();
                        throw throwable;
                    }
                    reader.close();
                    {
                    }
                }
                return plugins;
            }

            public static void main(String ... argument) throws ClassNotFoundException, IOException {
                if (argument.length < 2) {
                    throw new IllegalArgumentException("Expected arguments: <source> <target> [<plugin>, ...]");
                }
                ArrayList<Factory.UsingReflection> factories = new ArrayList<Factory.UsingReflection>(argument.length - 2);
                for (String plugin : Arrays.asList(argument).subList(2, argument.length)) {
                    factories.add(new Factory.UsingReflection(Class.forName(plugin)));
                }
                new Default().apply(new File(argument[0]), new File(argument[1]), factories);
            }

            @Override
            public Engine with(ByteBuddy byteBuddy) {
                return new Default(byteBuddy, this.typeStrategy, this.poolStrategy, this.classFileLocator, this.listener, this.errorHandler, this.dispatcherFactory, this.ignoredTypeMatcher);
            }

            @Override
            public Engine with(TypeStrategy typeStrategy) {
                return new Default(this.byteBuddy, typeStrategy, this.poolStrategy, this.classFileLocator, this.listener, this.errorHandler, this.dispatcherFactory, this.ignoredTypeMatcher);
            }

            @Override
            public Engine with(PoolStrategy poolStrategy) {
                return new Default(this.byteBuddy, this.typeStrategy, poolStrategy, this.classFileLocator, this.listener, this.errorHandler, this.dispatcherFactory, this.ignoredTypeMatcher);
            }

            @Override
            public Engine with(ClassFileLocator classFileLocator) {
                return new Default(this.byteBuddy, this.typeStrategy, this.poolStrategy, new ClassFileLocator.Compound(this.classFileLocator, classFileLocator), this.listener, this.errorHandler, this.dispatcherFactory, this.ignoredTypeMatcher);
            }

            @Override
            public Engine with(Listener listener) {
                return new Default(this.byteBuddy, this.typeStrategy, this.poolStrategy, this.classFileLocator, new Listener.Compound(this.listener, listener), this.errorHandler, this.dispatcherFactory, this.ignoredTypeMatcher);
            }

            @Override
            public Engine withoutErrorHandlers() {
                return new Default(this.byteBuddy, this.typeStrategy, this.poolStrategy, this.classFileLocator, this.listener, Listener.NoOp.INSTANCE, this.dispatcherFactory, this.ignoredTypeMatcher);
            }

            @Override
            public Engine withErrorHandlers(List<? extends ErrorHandler> errorHandlers) {
                return new Default(this.byteBuddy, this.typeStrategy, this.poolStrategy, this.classFileLocator, this.listener, new ErrorHandler.Compound(errorHandlers), this.dispatcherFactory, this.ignoredTypeMatcher);
            }

            @Override
            public Engine with(Dispatcher.Factory dispatcherFactory) {
                return new Default(this.byteBuddy, this.typeStrategy, this.poolStrategy, this.classFileLocator, this.listener, this.errorHandler, dispatcherFactory, this.ignoredTypeMatcher);
            }

            @Override
            public Engine ignore(ElementMatcher<? super TypeDescription> matcher) {
                return new Default(this.byteBuddy, this.typeStrategy, this.poolStrategy, this.classFileLocator, this.listener, this.errorHandler, this.dispatcherFactory, this.ignoredTypeMatcher.or(matcher));
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            public Summary apply(Source source, Target target, List<? extends Factory> factories) throws IOException {
                Listener.Compound listener = new Listener.Compound(this.listener, new Listener.ForErrorHandler(this.errorHandler));
                ArrayList<TypeDescription> transformed = new ArrayList<TypeDescription>();
                LinkedHashMap<TypeDescription, List<Throwable>> failed = new LinkedHashMap<TypeDescription, List<Throwable>>();
                ArrayList<String> unresolved = new ArrayList<String>();
                Throwable rethrown = null;
                ArrayList<Plugin> plugins = new ArrayList<Plugin>(factories.size());
                ArrayList<WithInitialization> initializers = new ArrayList<WithInitialization>();
                ArrayList<WithPreprocessor> preprocessors = new ArrayList<WithPreprocessor>();
                try {
                    for (Factory factory : factories) {
                        Plugin plugin = factory.make();
                        plugins.add(plugin);
                        if (plugin instanceof WithPreprocessor) {
                            preprocessors.add((WithPreprocessor)plugin);
                        }
                        if (!(plugin instanceof WithInitialization)) continue;
                        initializers.add((WithInitialization)plugin);
                    }
                    Source.Origin origin = source.read();
                    try {
                        ClassFileLocator.Compound compound = new ClassFileLocator.Compound(origin.getClassFileLocator(), this.classFileLocator);
                        TypePool typePool = this.poolStrategy.typePool(compound);
                        Manifest manifest = origin.getManifest();
                        listener.onManifest(manifest);
                        Target.Sink sink = target.write(manifest);
                        try {
                            for (WithInitialization initializer : initializers) {
                                sink.store(initializer.initialize(compound));
                            }
                            Dispatcher dispatcher = this.dispatcherFactory.make(sink, transformed, failed, unresolved);
                            try {
                                for (Source.Element element : origin) {
                                    if (Thread.interrupted()) {
                                        Thread.currentThread().interrupt();
                                        throw new IllegalStateException("Thread interrupted during plugin engine application");
                                    }
                                    String name = element.getName();
                                    while (name.startsWith("/")) {
                                        name = name.substring(1);
                                    }
                                    if (name.endsWith(Engine.CLASS_FILE_EXTENSION) && !name.endsWith(Engine.PACKAGE_INFO) && !name.equals(Engine.MODULE_INFO)) {
                                        dispatcher.accept(new Preprocessor(element, name.substring(0, name.length() - Engine.CLASS_FILE_EXTENSION.length()).replace('/', '.'), compound, typePool, listener, plugins, preprocessors), preprocessors.isEmpty());
                                        continue;
                                    }
                                    if (name.equals("META-INF/MANIFEST.MF")) continue;
                                    listener.onResource(name);
                                    sink.retain(element);
                                }
                                dispatcher.complete();
                                Object var22_22 = null;
                            }
                            catch (Throwable throwable) {
                                Object var22_23 = null;
                                dispatcher.close();
                                throw throwable;
                            }
                            dispatcher.close();
                            if (!failed.isEmpty()) {
                                listener.onError(failed);
                            }
                            Object var24_25 = null;
                        }
                        catch (Throwable throwable) {
                            Object var24_26 = null;
                            sink.close();
                            throw throwable;
                        }
                        sink.close();
                        Object var26_28 = null;
                    }
                    catch (Throwable throwable) {
                        Object var26_29 = null;
                        origin.close();
                        throw throwable;
                    }
                    origin.close();
                    Object var28_31 = null;
                }
                catch (Throwable throwable) {
                    Object var28_32 = null;
                    for (Plugin plugin : plugins) {
                        try {
                            plugin.close();
                        }
                        catch (Throwable throwable2) {
                            try {
                                listener.onError(plugin, throwable2);
                            }
                            catch (Throwable chained) {
                                rethrown = rethrown == null ? chained : rethrown;
                            }
                        }
                    }
                    throw throwable;
                }
                for (Plugin plugin : plugins) {
                    try {
                        plugin.close();
                    }
                    catch (Throwable throwable2) {
                        try {
                            listener.onError(plugin, throwable2);
                        }
                        catch (Throwable chained) {
                            rethrown = rethrown == null ? chained : rethrown;
                        }
                    }
                }
                if (rethrown == null) {
                    return new Summary(transformed, failed, unresolved);
                }
                if (rethrown instanceof IOException) {
                    throw (IOException)rethrown;
                }
                if (rethrown instanceof RuntimeException) {
                    throw (RuntimeException)rethrown;
                }
                throw new IllegalStateException(rethrown);
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
                if (!this.byteBuddy.equals(((Default)object).byteBuddy)) {
                    return false;
                }
                if (!this.typeStrategy.equals(((Default)object).typeStrategy)) {
                    return false;
                }
                if (!this.poolStrategy.equals(((Default)object).poolStrategy)) {
                    return false;
                }
                if (!this.classFileLocator.equals(((Default)object).classFileLocator)) {
                    return false;
                }
                if (!this.listener.equals(((Default)object).listener)) {
                    return false;
                }
                if (!this.errorHandler.equals(((Default)object).errorHandler)) {
                    return false;
                }
                if (!this.dispatcherFactory.equals(((Default)object).dispatcherFactory)) {
                    return false;
                }
                return this.ignoredTypeMatcher.equals(((Default)object).ignoredTypeMatcher);
            }

            public int hashCode() {
                return (((((((this.getClass().hashCode() * 31 + this.byteBuddy.hashCode()) * 31 + this.typeStrategy.hashCode()) * 31 + this.poolStrategy.hashCode()) * 31 + this.classFileLocator.hashCode()) * 31 + this.listener.hashCode()) * 31 + this.errorHandler.hashCode()) * 31 + this.dispatcherFactory.hashCode()) * 31 + this.ignoredTypeMatcher.hashCode();
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            private class Preprocessor
            implements Callable<Callable<? extends Dispatcher.Materializable>> {
                private final Source.Element element;
                private final String typeName;
                private final ClassFileLocator classFileLocator;
                private final TypePool typePool;
                private final Listener listener;
                private final List<Plugin> plugins;
                private final List<WithPreprocessor> preprocessors;

                private Preprocessor(Source.Element element, String typeName, ClassFileLocator classFileLocator, TypePool typePool, Listener listener, List<Plugin> plugins, List<WithPreprocessor> preprocessors) {
                    this.element = element;
                    this.typeName = typeName;
                    this.classFileLocator = classFileLocator;
                    this.typePool = typePool;
                    this.listener = listener;
                    this.plugins = plugins;
                    this.preprocessors = preprocessors;
                }

                @Override
                public Callable<Dispatcher.Materializable> call() throws Exception {
                    this.listener.onDiscovery(this.typeName);
                    TypePool.Resolution resolution = this.typePool.describe(this.typeName);
                    if (resolution.isResolved()) {
                        TypeDescription typeDescription = resolution.resolve();
                        try {
                            if (!Default.this.ignoredTypeMatcher.matches(typeDescription)) {
                                for (WithPreprocessor preprocessor : this.preprocessors) {
                                    preprocessor.onPreprocess(typeDescription, this.classFileLocator);
                                }
                                return new Resolved(typeDescription);
                            }
                            return new Ignored(typeDescription);
                        }
                        catch (Throwable throwable) {
                            this.listener.onComplete(typeDescription);
                            if (throwable instanceof Exception) {
                                throw (Exception)throwable;
                            }
                            if (throwable instanceof Error) {
                                throw (Error)throwable;
                            }
                            throw new IllegalStateException(throwable);
                        }
                    }
                    return new Unresolved();
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                private class Unresolved
                implements Callable<Dispatcher.Materializable> {
                    private Unresolved() {
                    }

                    @Override
                    public Dispatcher.Materializable call() {
                        Preprocessor.this.listener.onUnresolved(Preprocessor.this.typeName);
                        return new Dispatcher.Materializable.ForUnresolvedElement(Preprocessor.this.element, Preprocessor.this.typeName);
                    }
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                private class Ignored
                implements Callable<Dispatcher.Materializable> {
                    private final TypeDescription typeDescription;

                    private Ignored(TypeDescription typeDescription) {
                        this.typeDescription = typeDescription;
                    }

                    /*
                     * WARNING - Removed try catching itself - possible behaviour change.
                     */
                    @Override
                    public Dispatcher.Materializable call() {
                        try {
                            Preprocessor.this.listener.onIgnored(this.typeDescription, Preprocessor.this.plugins);
                            Object var2_1 = null;
                            Preprocessor.this.listener.onComplete(this.typeDescription);
                        }
                        catch (Throwable throwable) {
                            Object var2_2 = null;
                            Preprocessor.this.listener.onComplete(this.typeDescription);
                            throw throwable;
                        }
                        return new Dispatcher.Materializable.ForRetainedElement(Preprocessor.this.element);
                    }
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                private class Resolved
                implements Callable<Dispatcher.Materializable> {
                    private final TypeDescription typeDescription;

                    private Resolved(TypeDescription typeDescription) {
                        this.typeDescription = typeDescription;
                    }

                    /*
                     * WARNING - Removed try catching itself - possible behaviour change.
                     * Enabled aggressive block sorting
                     * Enabled unnecessary exception pruning
                     * Enabled aggressive exception aggregation
                     */
                    @Override
                    public Dispatcher.Materializable call() {
                        Object object;
                        block13: {
                            Object object2;
                            block12: {
                                ArrayList<Plugin> applied = new ArrayList<Plugin>();
                                ArrayList<Plugin> ignored = new ArrayList<Plugin>();
                                ArrayList<Throwable> errored = new ArrayList<Throwable>();
                                try {
                                    DynamicType.Builder<?> builder = Default.this.typeStrategy.builder(Default.this.byteBuddy, this.typeDescription, Preprocessor.this.classFileLocator);
                                    for (Plugin plugin : Preprocessor.this.plugins) {
                                        try {
                                            if (plugin.matches(this.typeDescription)) {
                                                builder = plugin.apply(builder, this.typeDescription, Preprocessor.this.classFileLocator);
                                                Preprocessor.this.listener.onTransformation(this.typeDescription, plugin);
                                                applied.add(plugin);
                                                continue;
                                            }
                                            Preprocessor.this.listener.onIgnored(this.typeDescription, plugin);
                                            ignored.add(plugin);
                                        }
                                        catch (Throwable throwable) {
                                            Preprocessor.this.listener.onError(this.typeDescription, plugin, throwable);
                                            errored.add(throwable);
                                        }
                                    }
                                    if (!errored.isEmpty()) {
                                        Preprocessor.this.listener.onError(this.typeDescription, errored);
                                        object = new Dispatcher.Materializable.ForFailedElement(Preprocessor.this.element, this.typeDescription, errored);
                                        Object var9_10 = null;
                                        Preprocessor.this.listener.onComplete(this.typeDescription);
                                        return object;
                                    }
                                    if (!applied.isEmpty()) {
                                        try {
                                            DynamicType.Unloaded<?> dynamicType = builder.make(TypeResolutionStrategy.Disabled.INSTANCE, Preprocessor.this.typePool);
                                            Preprocessor.this.listener.onTransformation(this.typeDescription, applied);
                                            for (Map.Entry<TypeDescription, LoadedTypeInitializer> entry : dynamicType.getLoadedTypeInitializers().entrySet()) {
                                                if (!entry.getValue().isAlive()) continue;
                                                Preprocessor.this.listener.onLiveInitializer(this.typeDescription, entry.getKey());
                                            }
                                            object2 = new Dispatcher.Materializable.ForTransformedElement(dynamicType);
                                            break block12;
                                        }
                                        catch (Throwable throwable) {
                                            errored.add(throwable);
                                            Preprocessor.this.listener.onError(this.typeDescription, errored);
                                            object2 = new Dispatcher.Materializable.ForFailedElement(Preprocessor.this.element, this.typeDescription, errored);
                                            Object var9_12 = null;
                                            Preprocessor.this.listener.onComplete(this.typeDescription);
                                            return object2;
                                        }
                                    }
                                    Preprocessor.this.listener.onIgnored(this.typeDescription, ignored);
                                    object = new Dispatcher.Materializable.ForRetainedElement(Preprocessor.this.element);
                                    break block13;
                                }
                                catch (Throwable throwable) {
                                    Object var9_14 = null;
                                    Preprocessor.this.listener.onComplete(this.typeDescription);
                                    throw throwable;
                                }
                            }
                            Object var9_11 = null;
                            Preprocessor.this.listener.onComplete(this.typeDescription);
                            return object2;
                        }
                        Object var9_13 = null;
                        Preprocessor.this.listener.onComplete(this.typeDescription);
                        return object;
                    }
                }
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static abstract class AbstractBase
        implements Engine {
            @Override
            public Engine withErrorHandlers(ErrorHandler ... errorHandler) {
                return this.withErrorHandlers(Arrays.asList(errorHandler));
            }

            @Override
            public Engine withParallelTransformation(int threads) {
                if (threads < 1) {
                    throw new IllegalArgumentException("Number of threads must be positive: " + threads);
                }
                return this.with(new Dispatcher.ForParallelTransformation.WithThrowawayExecutorService.Factory(threads));
            }

            @Override
            public Summary apply(File source, File target, Factory ... factory) throws IOException {
                return this.apply(source, target, Arrays.asList(factory));
            }

            @Override
            public Summary apply(File source, File target, List<? extends Factory> factories) throws IOException {
                return this.apply(source.isDirectory() ? new Source.ForFolder(source) : new Source.ForJarFile(source), target.isDirectory() ? new Target.ForFolder(target) : new Target.ForJarFile(target), factories);
            }

            @Override
            public Summary apply(Source source, Target target, Factory ... factory) throws IOException {
                return this.apply(source, target, Arrays.asList(factory));
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static class Summary {
            private final List<TypeDescription> transformed;
            private final Map<TypeDescription, List<Throwable>> failed;
            private final List<String> unresolved;

            public Summary(List<TypeDescription> transformed, Map<TypeDescription, List<Throwable>> failed, List<String> unresolved) {
                this.transformed = transformed;
                this.failed = failed;
                this.unresolved = unresolved;
            }

            public List<TypeDescription> getTransformed() {
                return this.transformed;
            }

            public Map<TypeDescription, List<Throwable>> getFailed() {
                return this.failed;
            }

            public List<String> getUnresolved() {
                return this.unresolved;
            }

            public int hashCode() {
                int result = this.transformed.hashCode();
                result = 31 * result + this.failed.hashCode();
                result = 31 * result + this.unresolved.hashCode();
                return result;
            }

            public boolean equals(@MaybeNull Object other) {
                if (this == other) {
                    return true;
                }
                if (other == null || this.getClass() != other.getClass()) {
                    return false;
                }
                Summary summary = (Summary)other;
                return this.transformed.equals(summary.transformed) && this.failed.equals(summary.failed) && this.unresolved.equals(summary.unresolved);
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static interface Dispatcher
        extends Closeable {
            public void accept(Callable<? extends Callable<? extends Materializable>> var1, boolean var2) throws IOException;

            public void complete() throws IOException;

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            public static class ForParallelTransformation
            implements Dispatcher {
                private final Target.Sink sink;
                private final List<TypeDescription> transformed;
                private final Map<TypeDescription, List<Throwable>> failed;
                private final List<String> unresolved;
                private final CompletionService<Callable<Materializable>> preprocessings;
                private final CompletionService<Materializable> materializers;
                private int deferred;
                private final Set<Future<?>> futures;

                protected ForParallelTransformation(Executor executor, Target.Sink sink, List<TypeDescription> transformed, Map<TypeDescription, List<Throwable>> failed, List<String> unresolved) {
                    this.sink = sink;
                    this.transformed = transformed;
                    this.failed = failed;
                    this.unresolved = unresolved;
                    this.preprocessings = new ExecutorCompletionService<Callable<Materializable>>(executor);
                    this.materializers = new ExecutorCompletionService<Materializable>(executor);
                    this.futures = new HashSet();
                }

                @Override
                public void accept(Callable<? extends Callable<? extends Materializable>> work, boolean eager) {
                    if (eager) {
                        this.futures.add(this.materializers.submit(new EagerWork(work)));
                    } else {
                        ++this.deferred;
                        this.futures.add(this.preprocessings.submit(work));
                    }
                }

                @Override
                public void complete() throws IOException {
                    try {
                        Future<Materializable> future;
                        ArrayList preprocessings = new ArrayList(this.deferred);
                        while (this.deferred-- > 0) {
                            future = this.preprocessings.take();
                            this.futures.remove(future);
                            preprocessings.add(future.get());
                        }
                        for (Callable preprocessing : preprocessings) {
                            this.futures.add(this.materializers.submit(preprocessing));
                        }
                        while (!this.futures.isEmpty()) {
                            future = this.materializers.take();
                            this.futures.remove(future);
                            future.get().materialize(this.sink, this.transformed, this.failed, this.unresolved);
                        }
                    }
                    catch (InterruptedException exception) {
                        Thread.currentThread().interrupt();
                        throw new IllegalStateException(exception);
                    }
                    catch (ExecutionException exception) {
                        Throwable cause = exception.getCause();
                        if (cause instanceof IOException) {
                            throw (IOException)cause;
                        }
                        if (cause instanceof RuntimeException) {
                            throw (RuntimeException)cause;
                        }
                        if (cause instanceof Error) {
                            throw (Error)cause;
                        }
                        throw new IllegalStateException(cause);
                    }
                }

                @Override
                public void close() {
                    for (Future<?> future : this.futures) {
                        future.cancel(true);
                    }
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                @HashCodeAndEqualsPlugin.Enhance
                protected static class EagerWork
                implements Callable<Materializable> {
                    private final Callable<? extends Callable<? extends Materializable>> work;

                    protected EagerWork(Callable<? extends Callable<? extends Materializable>> work) {
                        this.work = work;
                    }

                    @Override
                    public Materializable call() throws Exception {
                        return this.work.call().call();
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
                        return this.work.equals(((EagerWork)object).work);
                    }

                    public int hashCode() {
                        return this.getClass().hashCode() * 31 + this.work.hashCode();
                    }
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                @HashCodeAndEqualsPlugin.Enhance
                public static class Factory
                implements net.bytebuddy.build.Plugin$Engine$Dispatcher$Factory {
                    private final Executor executor;

                    public Factory(Executor executor) {
                        this.executor = executor;
                    }

                    @Override
                    public Dispatcher make(Target.Sink sink, List<TypeDescription> transformed, Map<TypeDescription, List<Throwable>> failed, List<String> unresolved) {
                        return new ForParallelTransformation(this.executor, sink, transformed, failed, unresolved);
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
                        return this.executor.equals(((Factory)object).executor);
                    }

                    public int hashCode() {
                        return this.getClass().hashCode() * 31 + this.executor.hashCode();
                    }
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                @HashCodeAndEqualsPlugin.Enhance
                public static class WithThrowawayExecutorService
                extends ForParallelTransformation {
                    private final ExecutorService executorService;

                    protected WithThrowawayExecutorService(ExecutorService executorService, Target.Sink sink, List<TypeDescription> transformed, Map<TypeDescription, List<Throwable>> failed, List<String> unresolved) {
                        super(executorService, sink, transformed, failed, unresolved);
                        this.executorService = executorService;
                    }

                    /*
                     * WARNING - Removed try catching itself - possible behaviour change.
                     */
                    @Override
                    public void close() {
                        try {
                            super.close();
                            Object var2_1 = null;
                            this.executorService.shutdown();
                        }
                        catch (Throwable throwable) {
                            Object var2_2 = null;
                            this.executorService.shutdown();
                            throw throwable;
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
                        return this.executorService.equals(((WithThrowawayExecutorService)object).executorService);
                    }

                    public int hashCode() {
                        return this.getClass().hashCode() * 31 + this.executorService.hashCode();
                    }

                    /*
                     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                     */
                    @HashCodeAndEqualsPlugin.Enhance
                    public static class Factory
                    implements net.bytebuddy.build.Plugin$Engine$Dispatcher$Factory {
                        private final int threads;

                        public Factory(int threads) {
                            this.threads = threads;
                        }

                        @Override
                        public Dispatcher make(Target.Sink sink, List<TypeDescription> transformed, Map<TypeDescription, List<Throwable>> failed, List<String> unresolved) {
                            return new WithThrowawayExecutorService(Executors.newFixedThreadPool(this.threads), sink, transformed, failed, unresolved);
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
                            return this.threads == ((Factory)object).threads;
                        }

                        public int hashCode() {
                            return this.getClass().hashCode() * 31 + this.threads;
                        }
                    }
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            public static class ForSerialTransformation
            implements Dispatcher {
                private final Target.Sink sink;
                private final List<TypeDescription> transformed;
                private final Map<TypeDescription, List<Throwable>> failed;
                private final List<String> unresolved;
                private final List<Callable<? extends Materializable>> preprocessings;

                protected ForSerialTransformation(Target.Sink sink, List<TypeDescription> transformed, Map<TypeDescription, List<Throwable>> failed, List<String> unresolved) {
                    this.sink = sink;
                    this.transformed = transformed;
                    this.failed = failed;
                    this.unresolved = unresolved;
                    this.preprocessings = new ArrayList<Callable<? extends Materializable>>();
                }

                @Override
                public void accept(Callable<? extends Callable<? extends Materializable>> work, boolean eager) throws IOException {
                    try {
                        Callable<? extends Materializable> preprocessed = work.call();
                        if (eager) {
                            preprocessed.call().materialize(this.sink, this.transformed, this.failed, this.unresolved);
                        } else {
                            this.preprocessings.add(preprocessed);
                        }
                    }
                    catch (Exception exception) {
                        if (exception instanceof IOException) {
                            throw (IOException)exception;
                        }
                        if (exception instanceof RuntimeException) {
                            throw (RuntimeException)exception;
                        }
                        throw new IllegalStateException(exception);
                    }
                }

                @Override
                public void complete() throws IOException {
                    for (Callable<? extends Materializable> preprocessing : this.preprocessings) {
                        if (Thread.interrupted()) {
                            Thread.currentThread().interrupt();
                            throw new IllegalStateException("Interrupted during plugin engine completion");
                        }
                        try {
                            preprocessing.call().materialize(this.sink, this.transformed, this.failed, this.unresolved);
                        }
                        catch (Exception exception) {
                            if (exception instanceof IOException) {
                                throw (IOException)exception;
                            }
                            if (exception instanceof RuntimeException) {
                                throw (RuntimeException)exception;
                            }
                            throw new IllegalStateException(exception);
                        }
                    }
                }

                @Override
                public void close() {
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                public static enum Factory implements net.bytebuddy.build.Plugin$Engine$Dispatcher$Factory
                {
                    INSTANCE;


                    @Override
                    public Dispatcher make(Target.Sink sink, List<TypeDescription> transformed, Map<TypeDescription, List<Throwable>> failed, List<String> unresolved) {
                        return new ForSerialTransformation(sink, transformed, failed, unresolved);
                    }
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            public static interface Factory {
                public Dispatcher make(Target.Sink var1, List<TypeDescription> var2, Map<TypeDescription, List<Throwable>> var3, List<String> var4);
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            public static interface Materializable {
                public void materialize(Target.Sink var1, List<TypeDescription> var2, Map<TypeDescription, List<Throwable>> var3, List<String> var4) throws IOException;

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                public static class ForUnresolvedElement
                implements Materializable {
                    private final Source.Element element;
                    private final String typeName;

                    protected ForUnresolvedElement(Source.Element element, String typeName) {
                        this.element = element;
                        this.typeName = typeName;
                    }

                    @Override
                    public void materialize(Target.Sink sink, List<TypeDescription> transformed, Map<TypeDescription, List<Throwable>> failed, List<String> unresolved) throws IOException {
                        sink.retain(this.element);
                        unresolved.add(this.typeName);
                    }
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                public static class ForFailedElement
                implements Materializable {
                    private final Source.Element element;
                    private final TypeDescription typeDescription;
                    private final List<Throwable> errored;

                    protected ForFailedElement(Source.Element element, TypeDescription typeDescription, List<Throwable> errored) {
                        this.element = element;
                        this.typeDescription = typeDescription;
                        this.errored = errored;
                    }

                    @Override
                    public void materialize(Target.Sink sink, List<TypeDescription> transformed, Map<TypeDescription, List<Throwable>> failed, List<String> unresolved) throws IOException {
                        sink.retain(this.element);
                        failed.put(this.typeDescription, this.errored);
                    }
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                public static class ForRetainedElement
                implements Materializable {
                    private final Source.Element element;

                    protected ForRetainedElement(Source.Element element) {
                        this.element = element;
                    }

                    @Override
                    public void materialize(Target.Sink sink, List<TypeDescription> transformed, Map<TypeDescription, List<Throwable>> failed, List<String> unresolved) throws IOException {
                        sink.retain(this.element);
                    }
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                public static class ForTransformedElement
                implements Materializable {
                    private final DynamicType dynamicType;

                    protected ForTransformedElement(DynamicType dynamicType) {
                        this.dynamicType = dynamicType;
                    }

                    @Override
                    public void materialize(Target.Sink sink, List<TypeDescription> transformed, Map<TypeDescription, List<Throwable>> failed, List<String> unresolved) throws IOException {
                        sink.store(this.dynamicType.getAllTypes());
                        transformed.add(this.dynamicType.getTypeDescription());
                    }
                }
            }
        }

        public static interface Target {
            public Sink write(@MaybeNull Manifest var1) throws IOException;

            @HashCodeAndEqualsPlugin.Enhance
            public static class ForJarFile
            implements Target {
                private final File file;

                public ForJarFile(File file) {
                    this.file = file;
                }

                public Sink write(@MaybeNull Manifest manifest) throws IOException {
                    return manifest == null ? new Sink.ForJarOutputStream(new JarOutputStream(new FileOutputStream(this.file))) : new Sink.ForJarOutputStream(new JarOutputStream((OutputStream)new FileOutputStream(this.file), manifest));
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
                    return this.file.equals(((ForJarFile)object).file);
                }

                public int hashCode() {
                    return this.getClass().hashCode() * 31 + this.file.hashCode();
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            @HashCodeAndEqualsPlugin.Enhance
            public static class ForFolder
            implements Target,
            Sink {
                private final File folder;

                public ForFolder(File folder) {
                    this.folder = folder;
                }

                /*
                 * WARNING - Removed try catching itself - possible behaviour change.
                 */
                @Override
                public Sink write(@MaybeNull Manifest manifest) throws IOException {
                    if (manifest != null) {
                        File target = new File(this.folder, "META-INF/MANIFEST.MF");
                        if (!target.getParentFile().isDirectory() && !target.getParentFile().mkdirs()) {
                            throw new IOException("Could not create directory: " + target.getParent());
                        }
                        FileOutputStream outputStream = new FileOutputStream(target);
                        try {
                            manifest.write(outputStream);
                            Object var5_4 = null;
                        }
                        catch (Throwable throwable) {
                            Object var5_5 = null;
                            ((OutputStream)outputStream).close();
                            throw throwable;
                        }
                        ((OutputStream)outputStream).close();
                        {
                        }
                    }
                    return this;
                }

                /*
                 * WARNING - Removed try catching itself - possible behaviour change.
                 */
                @Override
                public void store(Map<TypeDescription, byte[]> binaryRepresentations) throws IOException {
                    for (Map.Entry<TypeDescription, byte[]> entry : binaryRepresentations.entrySet()) {
                        Object var7_6;
                        File target = new File(this.folder, entry.getKey().getInternalName() + Engine.CLASS_FILE_EXTENSION);
                        if (!target.getParentFile().isDirectory() && !target.getParentFile().mkdirs()) {
                            throw new IOException("Could not create directory: " + target.getParent());
                        }
                        FileOutputStream outputStream = new FileOutputStream(target);
                        try {
                            ((OutputStream)outputStream).write(entry.getValue());
                            var7_6 = null;
                        }
                        catch (Throwable throwable) {
                            var7_6 = null;
                            ((OutputStream)outputStream).close();
                            throw throwable;
                        }
                        ((OutputStream)outputStream).close();
                        {
                        }
                    }
                }

                /*
                 * WARNING - Removed try catching itself - possible behaviour change.
                 */
                @Override
                public void retain(Source.Element element) throws IOException {
                    String name = element.getName();
                    if (!name.endsWith("/")) {
                        File target = new File(this.folder, name);
                        File resolved = element.resolveAs(File.class);
                        if (!target.getCanonicalPath().startsWith(this.folder.getCanonicalPath() + File.separatorChar)) {
                            throw new IllegalArgumentException(target + " is not a subdirectory of " + this.folder);
                        }
                        if (!target.getParentFile().isDirectory() && !target.getParentFile().mkdirs()) {
                            throw new IOException("Could not create directory: " + target.getParent());
                        }
                        if (resolved != null && !resolved.equals(target)) {
                            FileSystem.getInstance().copy(resolved, target);
                        } else if (!target.equals(resolved)) {
                            InputStream inputStream = element.getInputStream();
                            try {
                                FileOutputStream outputStream = new FileOutputStream(target);
                                try {
                                    int length;
                                    byte[] buffer = new byte[1024];
                                    while ((length = inputStream.read(buffer)) != -1) {
                                        ((OutputStream)outputStream).write(buffer, 0, length);
                                    }
                                    Object var10_9 = null;
                                }
                                catch (Throwable throwable) {
                                    Object var10_10 = null;
                                    ((OutputStream)outputStream).close();
                                    throw throwable;
                                }
                                ((OutputStream)outputStream).close();
                                Object var12_12 = null;
                            }
                            catch (Throwable throwable) {
                                Object var12_13 = null;
                                inputStream.close();
                                throw throwable;
                            }
                            inputStream.close();
                            {
                            }
                        }
                    }
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
                    return this.folder.equals(((ForFolder)object).folder);
                }

                public int hashCode() {
                    return this.getClass().hashCode() * 31 + this.folder.hashCode();
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            @HashCodeAndEqualsPlugin.Enhance
            public static class InMemory
            implements Target,
            Sink {
                private final Map<String, byte[]> storage;

                public InMemory() {
                    this(new HashMap<String, byte[]>());
                }

                public InMemory(Map<String, byte[]> storage) {
                    this.storage = storage;
                }

                /*
                 * WARNING - Removed try catching itself - possible behaviour change.
                 */
                @Override
                public Sink write(@MaybeNull Manifest manifest) throws IOException {
                    if (manifest != null) {
                        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                        try {
                            manifest.write(outputStream);
                            Object var4_3 = null;
                        }
                        catch (Throwable throwable) {
                            Object var4_4 = null;
                            outputStream.close();
                            throw throwable;
                        }
                        outputStream.close();
                        this.storage.put("META-INF/MANIFEST.MF", outputStream.toByteArray());
                    }
                    return this;
                }

                @Override
                public void store(Map<TypeDescription, byte[]> binaryRepresentations) {
                    for (Map.Entry<TypeDescription, byte[]> entry : binaryRepresentations.entrySet()) {
                        this.storage.put(entry.getKey().getInternalName() + Engine.CLASS_FILE_EXTENSION, entry.getValue());
                    }
                }

                /*
                 * WARNING - Removed try catching itself - possible behaviour change.
                 */
                @Override
                public void retain(Source.Element element) throws IOException {
                    String name = element.getName();
                    if (!name.endsWith("/")) {
                        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                        try {
                            InputStream inputStream = element.getInputStream();
                            try {
                                int length;
                                byte[] buffer = new byte[1024];
                                while ((length = inputStream.read(buffer)) != -1) {
                                    outputStream.write(buffer, 0, length);
                                }
                                Object var8_7 = null;
                            }
                            catch (Throwable throwable) {
                                Object var8_8 = null;
                                inputStream.close();
                                throw throwable;
                            }
                            inputStream.close();
                            Object var10_10 = null;
                        }
                        catch (Throwable throwable) {
                            Object var10_11 = null;
                            outputStream.close();
                            throw throwable;
                        }
                        outputStream.close();
                        this.storage.put(element.getName(), outputStream.toByteArray());
                    }
                }

                @Override
                public void close() {
                }

                public Map<String, byte[]> getStorage() {
                    return this.storage;
                }

                public Map<String, byte[]> toTypeMap() {
                    HashMap<String, byte[]> binaryRepresentations = new HashMap<String, byte[]>();
                    for (Map.Entry<String, byte[]> entry : this.storage.entrySet()) {
                        if (!entry.getKey().endsWith(Engine.CLASS_FILE_EXTENSION)) continue;
                        binaryRepresentations.put(entry.getKey().substring(0, entry.getKey().length() - Engine.CLASS_FILE_EXTENSION.length()).replace('/', '.'), entry.getValue());
                    }
                    return binaryRepresentations;
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
                    return ((Object)this.storage).equals(((InMemory)object).storage);
                }

                public int hashCode() {
                    return this.getClass().hashCode() * 31 + ((Object)this.storage).hashCode();
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            public static enum Discarding implements Target,
            Sink
            {
                INSTANCE;


                @Override
                public Sink write(@MaybeNull Manifest manifest) {
                    return this;
                }

                @Override
                public void store(Map<TypeDescription, byte[]> binaryRepresentations) {
                }

                @Override
                public void retain(Source.Element element) {
                }

                @Override
                public void close() {
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            public static interface Sink
            extends Closeable {
                public void store(Map<TypeDescription, byte[]> var1) throws IOException;

                public void retain(Source.Element var1) throws IOException;

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                public static class ForJarOutputStream
                implements Sink {
                    private final JarOutputStream outputStream;

                    public ForJarOutputStream(JarOutputStream outputStream) {
                        this.outputStream = outputStream;
                    }

                    @Override
                    public void store(Map<TypeDescription, byte[]> binaryRepresentations) throws IOException {
                        for (Map.Entry<TypeDescription, byte[]> entry : binaryRepresentations.entrySet()) {
                            this.outputStream.putNextEntry(new JarEntry(entry.getKey().getInternalName() + Engine.CLASS_FILE_EXTENSION));
                            this.outputStream.write(entry.getValue());
                            this.outputStream.closeEntry();
                        }
                    }

                    /*
                     * WARNING - Removed try catching itself - possible behaviour change.
                     */
                    @Override
                    public void retain(Source.Element element) throws IOException {
                        JarEntry entry = element.resolveAs(JarEntry.class);
                        this.outputStream.putNextEntry(entry == null ? new JarEntry(element.getName()) : entry);
                        InputStream inputStream = element.getInputStream();
                        try {
                            int length;
                            byte[] buffer = new byte[1024];
                            while ((length = inputStream.read(buffer)) != -1) {
                                this.outputStream.write(buffer, 0, length);
                            }
                            Object var7_6 = null;
                        }
                        catch (Throwable throwable) {
                            Object var7_7 = null;
                            inputStream.close();
                            throw throwable;
                        }
                        inputStream.close();
                        this.outputStream.closeEntry();
                    }

                    @Override
                    public void close() throws IOException {
                        this.outputStream.close();
                    }
                }
            }
        }

        public static interface Source {
            public Origin read() throws IOException;

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            @HashCodeAndEqualsPlugin.Enhance
            public static class Filtering
            implements Source {
                private final Source delegate;
                private final ElementMatcher<Element> matcher;
                private final boolean manifest;

                public Filtering(Source delegate, ElementMatcher<Element> matcher) {
                    this(delegate, matcher, true);
                }

                public Filtering(Source delegate, ElementMatcher<Element> matcher, boolean manifest) {
                    this.delegate = delegate;
                    this.matcher = matcher;
                    this.manifest = manifest;
                }

                @Override
                public Origin read() throws IOException {
                    return new Origin.Filtering(this.delegate.read(), this.matcher, this.manifest);
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
                    if (this.manifest != ((Filtering)object).manifest) {
                        return false;
                    }
                    if (!this.delegate.equals(((Filtering)object).delegate)) {
                        return false;
                    }
                    return this.matcher.equals(((Filtering)object).matcher);
                }

                public int hashCode() {
                    return ((this.getClass().hashCode() * 31 + this.delegate.hashCode()) * 31 + this.matcher.hashCode()) * 31 + this.manifest;
                }
            }

            @HashCodeAndEqualsPlugin.Enhance
            public static class ForJarFile
            implements Source {
                private final File file;

                public ForJarFile(File file) {
                    this.file = file;
                }

                public Origin read() throws IOException {
                    return new Origin.ForJarFile(new JarFile(this.file));
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
                    return this.file.equals(((ForJarFile)object).file);
                }

                public int hashCode() {
                    return this.getClass().hashCode() * 31 + this.file.hashCode();
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            @HashCodeAndEqualsPlugin.Enhance
            public static class ForFolder
            implements Source,
            Origin {
                private final File folder;

                public ForFolder(File folder) {
                    this.folder = folder;
                }

                @Override
                public Origin read() {
                    return this;
                }

                @Override
                public ClassFileLocator getClassFileLocator() {
                    return new ClassFileLocator.ForFolder(this.folder);
                }

                /*
                 * WARNING - Removed try catching itself - possible behaviour change.
                 */
                @Override
                @MaybeNull
                public Manifest getManifest() throws IOException {
                    File file = new File(this.folder, "META-INF/MANIFEST.MF");
                    if (file.exists()) {
                        Manifest manifest;
                        FileInputStream inputStream = new FileInputStream(file);
                        try {
                            manifest = new Manifest(inputStream);
                            Object var5_4 = null;
                        }
                        catch (Throwable throwable) {
                            Object var5_5 = null;
                            ((InputStream)inputStream).close();
                            throw throwable;
                        }
                        ((InputStream)inputStream).close();
                        return manifest;
                    }
                    return NO_MANIFEST;
                }

                @Override
                public Iterator<Element> iterator() {
                    return new FolderIterator(this.folder);
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
                    return this.folder.equals(((ForFolder)object).folder);
                }

                public int hashCode() {
                    return this.getClass().hashCode() * 31 + this.folder.hashCode();
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                protected class FolderIterator
                implements Iterator<Element> {
                    private final List<File> files;

                    protected FolderIterator(File folder) {
                        this.files = new ArrayList<File>(Collections.singleton(folder));
                        do {
                            File candidate;
                            File[] file;
                            if ((file = (candidate = this.files.remove(this.files.size() - 1)).listFiles()) == null) continue;
                            this.files.addAll(Arrays.asList(file));
                        } while (!this.files.isEmpty() && (this.files.get(this.files.size() - 1).isDirectory() || this.files.get(this.files.size() - 1).equals(new File(folder, "META-INF/MANIFEST.MF"))));
                    }

                    @Override
                    public boolean hasNext() {
                        return !this.files.isEmpty();
                    }

                    /*
                     * WARNING - Removed try catching itself - possible behaviour change.
                     */
                    @Override
                    @SuppressFBWarnings(value={"IT_NO_SUCH_ELEMENT"}, justification="Exception is thrown by invoking removeFirst on an empty list.")
                    public Element next() {
                        Element.ForFile forFile;
                        try {
                            forFile = new Element.ForFile(ForFolder.this.folder, this.files.remove(this.files.size() - 1));
                            Object var3_2 = null;
                        }
                        catch (Throwable throwable) {
                            Object var3_3 = null;
                            while (!this.files.isEmpty() && (this.files.get(this.files.size() - 1).isDirectory() || this.files.get(this.files.size() - 1).equals(new File(ForFolder.this.folder, "META-INF/MANIFEST.MF")))) {
                                File folder = this.files.remove(this.files.size() - 1);
                                File[] file = folder.listFiles();
                                if (file == null) continue;
                                this.files.addAll(Arrays.asList(file));
                            }
                            throw throwable;
                        }
                        while (!this.files.isEmpty() && (this.files.get(this.files.size() - 1).isDirectory() || this.files.get(this.files.size() - 1).equals(new File(ForFolder.this.folder, "META-INF/MANIFEST.MF")))) {
                            File folder = this.files.remove(this.files.size() - 1);
                            File[] file = folder.listFiles();
                            if (file == null) continue;
                            this.files.addAll(Arrays.asList(file));
                        }
                        return forFile;
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException("remove");
                    }
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            @HashCodeAndEqualsPlugin.Enhance
            public static class InMemory
            implements Source,
            Origin {
                private final Map<String, byte[]> storage;

                public InMemory(Map<String, byte[]> storage) {
                    this.storage = storage;
                }

                public static Source ofTypes(Class<?> ... type) {
                    return InMemory.ofTypes(Arrays.asList(type));
                }

                public static Source ofTypes(Collection<? extends Class<?>> types) {
                    HashMap<TypeDescription, byte[]> binaryRepresentations = new HashMap<TypeDescription, byte[]>();
                    for (Class<?> type : types) {
                        binaryRepresentations.put(TypeDescription.ForLoadedType.of(type), ClassFileLocator.ForClassLoader.read(type));
                    }
                    return InMemory.ofTypes(binaryRepresentations);
                }

                public static Source ofTypes(Map<TypeDescription, byte[]> binaryRepresentations) {
                    HashMap<String, byte[]> storage = new HashMap<String, byte[]>();
                    for (Map.Entry<TypeDescription, byte[]> entry : binaryRepresentations.entrySet()) {
                        storage.put(entry.getKey().getInternalName() + Engine.CLASS_FILE_EXTENSION, entry.getValue());
                    }
                    return new InMemory(storage);
                }

                @Override
                public Origin read() {
                    return this;
                }

                @Override
                public ClassFileLocator getClassFileLocator() {
                    return ClassFileLocator.Simple.ofResources(this.storage);
                }

                @Override
                @MaybeNull
                public Manifest getManifest() throws IOException {
                    byte[] binaryRepresentation = this.storage.get("META-INF/MANIFEST.MF");
                    if (binaryRepresentation == null) {
                        return NO_MANIFEST;
                    }
                    return new Manifest(new ByteArrayInputStream(binaryRepresentation));
                }

                @Override
                public Iterator<Element> iterator() {
                    return new MapEntryIterator(this.storage.entrySet().iterator());
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
                    return ((Object)this.storage).equals(((InMemory)object).storage);
                }

                public int hashCode() {
                    return this.getClass().hashCode() * 31 + ((Object)this.storage).hashCode();
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                protected static class MapEntryIterator
                implements Iterator<Element> {
                    private final Iterator<Map.Entry<String, byte[]>> iterator;

                    protected MapEntryIterator(Iterator<Map.Entry<String, byte[]>> iterator) {
                        this.iterator = iterator;
                    }

                    @Override
                    public boolean hasNext() {
                        return this.iterator.hasNext();
                    }

                    @Override
                    public Element next() {
                        Map.Entry<String, byte[]> entry = this.iterator.next();
                        return new Element.ForByteArray(entry.getKey(), entry.getValue());
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException("remove");
                    }
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            @HashCodeAndEqualsPlugin.Enhance
            public static class Compound
            implements Source {
                private final Collection<? extends Source> sources;

                public Compound(Collection<? extends Source> sources) {
                    this.sources = sources;
                }

                @Override
                public net.bytebuddy.build.Plugin$Engine$Source$Origin read() throws IOException {
                    if (this.sources.isEmpty()) {
                        return Empty.INSTANCE;
                    }
                    ArrayList<net.bytebuddy.build.Plugin$Engine$Source$Origin> origins = new ArrayList<net.bytebuddy.build.Plugin$Engine$Source$Origin>(this.sources.size());
                    try {
                        for (Source source : this.sources) {
                            origins.add(source.read());
                        }
                    }
                    catch (IOException exception) {
                        for (net.bytebuddy.build.Plugin$Engine$Source$Origin origin : origins) {
                            origin.close();
                        }
                        throw exception;
                    }
                    return new Origin(origins);
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
                    return ((Object)this.sources).equals(((Compound)object).sources);
                }

                public int hashCode() {
                    return this.getClass().hashCode() * 31 + ((Object)this.sources).hashCode();
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                @HashCodeAndEqualsPlugin.Enhance
                protected static class Origin
                implements net.bytebuddy.build.Plugin$Engine$Source$Origin {
                    private final List<net.bytebuddy.build.Plugin$Engine$Source$Origin> origins;

                    protected Origin(List<net.bytebuddy.build.Plugin$Engine$Source$Origin> origins) {
                        this.origins = origins;
                    }

                    @Override
                    public Manifest getManifest() throws IOException {
                        for (net.bytebuddy.build.Plugin$Engine$Source$Origin origin : this.origins) {
                            Manifest manifest = origin.getManifest();
                            if (manifest == null) continue;
                            return manifest;
                        }
                        return NO_MANIFEST;
                    }

                    @Override
                    public ClassFileLocator getClassFileLocator() {
                        ArrayList<ClassFileLocator> classFileLocators = new ArrayList<ClassFileLocator>(this.origins.size());
                        for (net.bytebuddy.build.Plugin$Engine$Source$Origin origin : this.origins) {
                            classFileLocators.add(origin.getClassFileLocator());
                        }
                        return new ClassFileLocator.Compound(classFileLocators);
                    }

                    @Override
                    public Iterator<Element> iterator() {
                        return new CompoundIterator(this.origins);
                    }

                    @Override
                    public void close() throws IOException {
                        for (net.bytebuddy.build.Plugin$Engine$Source$Origin origin : this.origins) {
                            origin.close();
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
                        return ((Object)this.origins).equals(((Origin)object).origins);
                    }

                    public int hashCode() {
                        return this.getClass().hashCode() * 31 + ((Object)this.origins).hashCode();
                    }

                    /*
                     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                     */
                    protected static class CompoundIterator
                    implements Iterator<Element> {
                        @MaybeNull
                        private Iterator<? extends Element> current;
                        private final List<? extends Iterable<? extends Element>> backlog;

                        protected CompoundIterator(List<? extends Iterable<? extends Element>> iterables) {
                            this.backlog = iterables;
                            this.forward();
                        }

                        @Override
                        public boolean hasNext() {
                            return this.current != null && this.current.hasNext();
                        }

                        @Override
                        public Element next() {
                            try {
                                if (this.current != null) {
                                    Element element = this.current.next();
                                    return element;
                                }
                                throw new NoSuchElementException();
                            }
                            finally {
                                this.forward();
                            }
                        }

                        private void forward() {
                            while (!(this.current != null && this.current.hasNext() || this.backlog.isEmpty())) {
                                this.current = this.backlog.remove(0).iterator();
                            }
                        }

                        @Override
                        public void remove() {
                            throw new UnsupportedOperationException("remove");
                        }
                    }
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            public static enum Empty implements Source,
            Origin
            {
                INSTANCE;


                @Override
                public Origin read() {
                    return this;
                }

                @Override
                public ClassFileLocator getClassFileLocator() {
                    return ClassFileLocator.NoOp.INSTANCE;
                }

                @Override
                @MaybeNull
                public Manifest getManifest() {
                    return NO_MANIFEST;
                }

                @Override
                public Iterator<Element> iterator() {
                    return Collections.emptySet().iterator();
                }

                @Override
                public void close() {
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            public static interface Element {
                public String getName();

                public InputStream getInputStream() throws IOException;

                @MaybeNull
                public <T> T resolveAs(Class<T> var1);

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                @HashCodeAndEqualsPlugin.Enhance
                public static class ForJarEntry
                implements Element {
                    private final JarFile file;
                    private final JarEntry entry;

                    public ForJarEntry(JarFile file, JarEntry entry) {
                        this.file = file;
                        this.entry = entry;
                    }

                    @Override
                    public String getName() {
                        return this.entry.getName();
                    }

                    @Override
                    public InputStream getInputStream() throws IOException {
                        return this.file.getInputStream(this.entry);
                    }

                    @Override
                    @MaybeNull
                    public <T> T resolveAs(Class<T> type) {
                        return (T)(JarEntry.class.isAssignableFrom(type) ? this.entry : null);
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
                        if (!this.file.equals(((ForJarEntry)object).file)) {
                            return false;
                        }
                        return this.entry.equals(((ForJarEntry)object).entry);
                    }

                    public int hashCode() {
                        return (this.getClass().hashCode() * 31 + this.file.hashCode()) * 31 + this.entry.hashCode();
                    }
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                @HashCodeAndEqualsPlugin.Enhance
                public static class ForFile
                implements Element {
                    private final File root;
                    private final File file;

                    public ForFile(File root, File file) {
                        this.root = root;
                        this.file = file;
                    }

                    @Override
                    public String getName() {
                        return this.root.getAbsoluteFile().toURI().relativize(this.file.getAbsoluteFile().toURI()).getPath();
                    }

                    @Override
                    public InputStream getInputStream() throws IOException {
                        return new FileInputStream(this.file);
                    }

                    @Override
                    @MaybeNull
                    public <T> T resolveAs(Class<T> type) {
                        return (T)(File.class.isAssignableFrom(type) ? this.file : null);
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
                        if (!this.root.equals(((ForFile)object).root)) {
                            return false;
                        }
                        return this.file.equals(((ForFile)object).file);
                    }

                    public int hashCode() {
                        return (this.getClass().hashCode() * 31 + this.root.hashCode()) * 31 + this.file.hashCode();
                    }
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                @HashCodeAndEqualsPlugin.Enhance
                @SuppressFBWarnings(value={"EI_EXPOSE_REP2"}, justification="The array is not modified by class contract.")
                public static class ForByteArray
                implements Element {
                    private final String name;
                    private final byte[] binaryRepresentation;

                    public ForByteArray(String name, byte[] binaryRepresentation) {
                        this.name = name;
                        this.binaryRepresentation = binaryRepresentation;
                    }

                    @Override
                    public String getName() {
                        return this.name;
                    }

                    @Override
                    public InputStream getInputStream() {
                        return new ByteArrayInputStream(this.binaryRepresentation);
                    }

                    @Override
                    @AlwaysNull
                    public <T> T resolveAs(Class<T> type) {
                        return null;
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
                        if (!this.name.equals(((ForByteArray)object).name)) {
                            return false;
                        }
                        return Arrays.equals(this.binaryRepresentation, ((ForByteArray)object).binaryRepresentation);
                    }

                    public int hashCode() {
                        return (this.getClass().hashCode() * 31 + this.name.hashCode()) * 31 + Arrays.hashCode(this.binaryRepresentation);
                    }
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            public static interface Origin
            extends Iterable<Element>,
            Closeable {
                @AlwaysNull
                public static final Manifest NO_MANIFEST = null;

                @MaybeNull
                public Manifest getManifest() throws IOException;

                public ClassFileLocator getClassFileLocator();

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                @HashCodeAndEqualsPlugin.Enhance
                public static class Filtering
                implements Origin {
                    private final Origin delegate;
                    private final ElementMatcher<Element> matcher;
                    private final boolean manifest;

                    public Filtering(Origin delegate, ElementMatcher<Element> matcher) {
                        this(delegate, matcher, true);
                    }

                    public Filtering(Origin delegate, ElementMatcher<Element> matcher, boolean manifest) {
                        this.delegate = delegate;
                        this.matcher = matcher;
                        this.manifest = manifest;
                    }

                    @Override
                    @MaybeNull
                    public Manifest getManifest() throws IOException {
                        return this.manifest ? this.delegate.getManifest() : NO_MANIFEST;
                    }

                    @Override
                    public ClassFileLocator getClassFileLocator() {
                        return this.delegate.getClassFileLocator();
                    }

                    @Override
                    public void close() throws IOException {
                        this.delegate.close();
                    }

                    @Override
                    public Iterator<Element> iterator() {
                        return new FilteringIterator(this.delegate.iterator(), this.matcher);
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
                        if (this.manifest != ((Filtering)object).manifest) {
                            return false;
                        }
                        if (!this.delegate.equals(((Filtering)object).delegate)) {
                            return false;
                        }
                        return this.matcher.equals(((Filtering)object).matcher);
                    }

                    public int hashCode() {
                        return ((this.getClass().hashCode() * 31 + this.delegate.hashCode()) * 31 + this.matcher.hashCode()) * 31 + this.manifest;
                    }

                    /*
                     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                     */
                    private static class FilteringIterator
                    implements Iterator<Element> {
                        private final Iterator<Element> iterator;
                        private final ElementMatcher<Element> matcher;
                        @MaybeNull
                        private Element current;

                        private FilteringIterator(Iterator<Element> iterator, ElementMatcher<Element> matcher) {
                            this.iterator = iterator;
                            this.matcher = matcher;
                            while (iterator.hasNext()) {
                                Element element = iterator.next();
                                if (!matcher.matches(element)) continue;
                                this.current = element;
                                break;
                            }
                        }

                        @Override
                        public boolean hasNext() {
                            return this.current != null;
                        }

                        @Override
                        public Element next() {
                            Element element;
                            if (this.current == null) {
                                throw new NoSuchElementException();
                            }
                            try {
                                element = this.current;
                                this.current = null;
                            }
                            catch (Throwable throwable) {
                                this.current = null;
                                while (this.iterator.hasNext()) {
                                    Element element2 = this.iterator.next();
                                    if (!this.matcher.matches(element2)) continue;
                                    this.current = element2;
                                    break;
                                }
                                throw throwable;
                            }
                            while (this.iterator.hasNext()) {
                                Element element3 = this.iterator.next();
                                if (!this.matcher.matches(element3)) continue;
                                this.current = element3;
                                break;
                            }
                            return element;
                        }

                        @Override
                        public void remove() {
                            this.iterator.remove();
                        }
                    }
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                public static class ForJarFile
                implements Origin {
                    private final JarFile file;

                    public ForJarFile(JarFile file) {
                        this.file = file;
                    }

                    @Override
                    @MaybeNull
                    public Manifest getManifest() throws IOException {
                        return this.file.getManifest();
                    }

                    @Override
                    public ClassFileLocator getClassFileLocator() {
                        return new ClassFileLocator.ForJarFile(this.file);
                    }

                    @Override
                    public void close() throws IOException {
                        this.file.close();
                    }

                    @Override
                    public Iterator<Element> iterator() {
                        return new JarFileIterator(this.file.entries());
                    }

                    /*
                     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                     */
                    protected class JarFileIterator
                    implements Iterator<Element> {
                        private final Enumeration<JarEntry> enumeration;

                        protected JarFileIterator(Enumeration<JarEntry> enumeration) {
                            this.enumeration = enumeration;
                        }

                        @Override
                        public boolean hasNext() {
                            return this.enumeration.hasMoreElements();
                        }

                        @Override
                        public Element next() {
                            return new Element.ForJarEntry(ForJarFile.this.file, this.enumeration.nextElement());
                        }

                        @Override
                        public void remove() {
                            throw new UnsupportedOperationException("remove");
                        }
                    }
                }
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static interface Listener
        extends ErrorHandler {
            public void onDiscovery(String var1);

            public void onTransformation(TypeDescription var1, Plugin var2);

            public void onTransformation(TypeDescription var1, List<Plugin> var2);

            public void onIgnored(TypeDescription var1, Plugin var2);

            public void onIgnored(TypeDescription var1, List<Plugin> var2);

            public void onComplete(TypeDescription var1);

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            @HashCodeAndEqualsPlugin.Enhance
            public static class Compound
            implements Listener {
                private final List<Listener> listeners = new ArrayList<Listener>();

                public Compound(Listener ... listener) {
                    this(Arrays.asList(listener));
                }

                public Compound(List<? extends Listener> listeners) {
                    for (Listener listener : listeners) {
                        if (listener instanceof Compound) {
                            this.listeners.addAll(((Compound)listener).listeners);
                            continue;
                        }
                        if (listener instanceof NoOp) continue;
                        this.listeners.add(listener);
                    }
                }

                @Override
                public void onDiscovery(String typeName) {
                    for (Listener listener : this.listeners) {
                        listener.onDiscovery(typeName);
                    }
                }

                @Override
                public void onTransformation(TypeDescription typeDescription, Plugin plugin) {
                    for (Listener listener : this.listeners) {
                        listener.onTransformation(typeDescription, plugin);
                    }
                }

                @Override
                public void onTransformation(TypeDescription typeDescription, List<Plugin> plugins) {
                    for (Listener listener : this.listeners) {
                        listener.onTransformation(typeDescription, plugins);
                    }
                }

                @Override
                public void onIgnored(TypeDescription typeDescription, Plugin plugin) {
                    for (Listener listener : this.listeners) {
                        listener.onIgnored(typeDescription, plugin);
                    }
                }

                @Override
                public void onIgnored(TypeDescription typeDescription, List<Plugin> plugins) {
                    for (Listener listener : this.listeners) {
                        listener.onIgnored(typeDescription, plugins);
                    }
                }

                @Override
                public void onError(TypeDescription typeDescription, Plugin plugin, Throwable throwable) {
                    for (Listener listener : this.listeners) {
                        listener.onError(typeDescription, plugin, throwable);
                    }
                }

                @Override
                public void onError(TypeDescription typeDescription, List<Throwable> throwables) {
                    for (Listener listener : this.listeners) {
                        listener.onError(typeDescription, throwables);
                    }
                }

                @Override
                public void onError(Map<TypeDescription, List<Throwable>> throwables) {
                    for (Listener listener : this.listeners) {
                        listener.onError(throwables);
                    }
                }

                @Override
                public void onError(Plugin plugin, Throwable throwable) {
                    for (Listener listener : this.listeners) {
                        listener.onError(plugin, throwable);
                    }
                }

                @Override
                public void onLiveInitializer(TypeDescription typeDescription, TypeDescription definingType) {
                    for (Listener listener : this.listeners) {
                        listener.onLiveInitializer(typeDescription, definingType);
                    }
                }

                @Override
                public void onComplete(TypeDescription typeDescription) {
                    for (Listener listener : this.listeners) {
                        listener.onComplete(typeDescription);
                    }
                }

                @Override
                public void onUnresolved(String typeName) {
                    for (Listener listener : this.listeners) {
                        listener.onUnresolved(typeName);
                    }
                }

                @Override
                public void onManifest(@MaybeNull Manifest manifest) {
                    for (Listener listener : this.listeners) {
                        listener.onManifest(manifest);
                    }
                }

                @Override
                public void onResource(String name) {
                    for (Listener listener : this.listeners) {
                        listener.onResource(name);
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
                    return ((Object)this.listeners).equals(((Compound)object).listeners);
                }

                public int hashCode() {
                    return this.getClass().hashCode() * 31 + ((Object)this.listeners).hashCode();
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            @HashCodeAndEqualsPlugin.Enhance
            public static class ForErrorHandler
            extends Adapter {
                private final ErrorHandler errorHandler;

                public ForErrorHandler(ErrorHandler errorHandler) {
                    this.errorHandler = errorHandler;
                }

                @Override
                public void onError(TypeDescription typeDescription, Plugin plugin, Throwable throwable) {
                    this.errorHandler.onError(typeDescription, plugin, throwable);
                }

                @Override
                public void onError(TypeDescription typeDescription, List<Throwable> throwables) {
                    this.errorHandler.onError(typeDescription, throwables);
                }

                @Override
                public void onError(Map<TypeDescription, List<Throwable>> throwables) {
                    this.errorHandler.onError(throwables);
                }

                @Override
                public void onError(Plugin plugin, Throwable throwable) {
                    this.errorHandler.onError(plugin, throwable);
                }

                @Override
                public void onLiveInitializer(TypeDescription typeDescription, TypeDescription definingType) {
                    this.errorHandler.onLiveInitializer(typeDescription, definingType);
                }

                @Override
                public void onUnresolved(String typeName) {
                    this.errorHandler.onUnresolved(typeName);
                }

                @Override
                public void onManifest(@MaybeNull Manifest manifest) {
                    this.errorHandler.onManifest(manifest);
                }

                @Override
                public void onResource(String name) {
                    this.errorHandler.onResource(name);
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
                    return this.errorHandler.equals(((ForErrorHandler)object).errorHandler);
                }

                public int hashCode() {
                    return this.getClass().hashCode() * 31 + this.errorHandler.hashCode();
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            @HashCodeAndEqualsPlugin.Enhance
            public static class WithErrorsOnly
            extends Adapter {
                private final Listener delegate;

                public WithErrorsOnly(Listener delegate) {
                    this.delegate = delegate;
                }

                @Override
                public void onError(TypeDescription typeDescription, Plugin plugin, Throwable throwable) {
                    this.delegate.onError(typeDescription, plugin, throwable);
                }

                @Override
                public void onError(TypeDescription typeDescription, List<Throwable> throwables) {
                    this.delegate.onError(typeDescription, throwables);
                }

                @Override
                public void onError(Map<TypeDescription, List<Throwable>> throwables) {
                    this.delegate.onError(throwables);
                }

                @Override
                public void onError(Plugin plugin, Throwable throwable) {
                    this.delegate.onError(plugin, throwable);
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
                    return this.delegate.equals(((WithErrorsOnly)object).delegate);
                }

                public int hashCode() {
                    return this.getClass().hashCode() * 31 + this.delegate.hashCode();
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            @HashCodeAndEqualsPlugin.Enhance
            public static class WithTransformationsOnly
            extends Adapter {
                private final Listener delegate;

                public WithTransformationsOnly(Listener delegate) {
                    this.delegate = delegate;
                }

                @Override
                public void onTransformation(TypeDescription typeDescription, Plugin plugin) {
                    this.delegate.onTransformation(typeDescription, plugin);
                }

                @Override
                public void onTransformation(TypeDescription typeDescription, List<Plugin> plugins) {
                    this.delegate.onTransformation(typeDescription, plugins);
                }

                @Override
                public void onError(TypeDescription typeDescription, Plugin plugin, Throwable throwable) {
                    this.delegate.onError(typeDescription, plugin, throwable);
                }

                @Override
                public void onError(TypeDescription typeDescription, List<Throwable> throwables) {
                    this.delegate.onError(typeDescription, throwables);
                }

                @Override
                public void onError(Map<TypeDescription, List<Throwable>> throwables) {
                    this.delegate.onError(throwables);
                }

                @Override
                public void onError(Plugin plugin, Throwable throwable) {
                    this.delegate.onError(plugin, throwable);
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
                    return this.delegate.equals(((WithTransformationsOnly)object).delegate);
                }

                public int hashCode() {
                    return this.getClass().hashCode() * 31 + this.delegate.hashCode();
                }
            }

            @HashCodeAndEqualsPlugin.Enhance
            public static class StreamWriting
            extends Adapter {
                protected static final String PREFIX = "[Byte Buddy]";
                private final PrintStream printStream;

                public StreamWriting(PrintStream printStream) {
                    this.printStream = printStream;
                }

                public static StreamWriting toSystemOut() {
                    return new StreamWriting(System.out);
                }

                public static StreamWriting toSystemError() {
                    return new StreamWriting(System.err);
                }

                public Listener withTransformationsOnly() {
                    return new WithTransformationsOnly(this);
                }

                public Listener withErrorsOnly() {
                    return new WithErrorsOnly(this);
                }

                public void onDiscovery(String typeName) {
                    this.printStream.printf("[Byte Buddy] DISCOVERY %s", typeName);
                }

                public void onTransformation(TypeDescription typeDescription, Plugin plugin) {
                    this.printStream.printf("[Byte Buddy] TRANSFORM %s for %s", typeDescription, plugin);
                }

                public void onIgnored(TypeDescription typeDescription, Plugin plugin) {
                    this.printStream.printf("[Byte Buddy] IGNORE %s for %s", typeDescription, plugin);
                }

                /*
                 * WARNING - Removed try catching itself - possible behaviour change.
                 */
                public void onError(TypeDescription typeDescription, Plugin plugin, Throwable throwable) {
                    PrintStream printStream = this.printStream;
                    synchronized (printStream) {
                        this.printStream.printf("[Byte Buddy] ERROR %s for %s", typeDescription, plugin);
                        throwable.printStackTrace(this.printStream);
                    }
                }

                /*
                 * WARNING - Removed try catching itself - possible behaviour change.
                 */
                public void onError(Plugin plugin, Throwable throwable) {
                    PrintStream printStream = this.printStream;
                    synchronized (printStream) {
                        this.printStream.printf("[Byte Buddy] ERROR %s", plugin);
                        throwable.printStackTrace(this.printStream);
                    }
                }

                public void onUnresolved(String typeName) {
                    this.printStream.printf("[Byte Buddy] UNRESOLVED %s", typeName);
                }

                public void onLiveInitializer(TypeDescription typeDescription, TypeDescription definingType) {
                    this.printStream.printf("[Byte Buddy] LIVE %s on %s", typeDescription, definingType);
                }

                public void onComplete(TypeDescription typeDescription) {
                    this.printStream.printf("[Byte Buddy] COMPLETE %s", typeDescription);
                }

                public void onManifest(@MaybeNull Manifest manifest) {
                    this.printStream.printf("[Byte Buddy] MANIFEST %b", manifest != null);
                }

                public void onResource(String name) {
                    this.printStream.printf("[Byte Buddy] RESOURCE %s", name);
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
                    return this.printStream.equals(((StreamWriting)object).printStream);
                }

                public int hashCode() {
                    return this.getClass().hashCode() * 31 + this.printStream.hashCode();
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            public static abstract class Adapter
            implements Listener {
                @Override
                public void onDiscovery(String typeName) {
                }

                @Override
                public void onTransformation(TypeDescription typeDescription, Plugin plugin) {
                }

                @Override
                public void onTransformation(TypeDescription typeDescription, List<Plugin> plugins) {
                }

                @Override
                public void onIgnored(TypeDescription typeDescription, Plugin plugin) {
                }

                @Override
                public void onIgnored(TypeDescription typeDescription, List<Plugin> plugins) {
                }

                @Override
                public void onError(TypeDescription typeDescription, Plugin plugin, Throwable throwable) {
                }

                @Override
                public void onError(TypeDescription typeDescription, List<Throwable> throwables) {
                }

                @Override
                public void onError(Map<TypeDescription, List<Throwable>> throwables) {
                }

                @Override
                public void onError(Plugin plugin, Throwable throwable) {
                }

                @Override
                public void onLiveInitializer(TypeDescription typeDescription, TypeDescription definingType) {
                }

                @Override
                public void onComplete(TypeDescription typeDescription) {
                }

                @Override
                public void onUnresolved(String typeName) {
                }

                @Override
                public void onManifest(@MaybeNull Manifest manifest) {
                }

                @Override
                public void onResource(String name) {
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            public static enum NoOp implements Listener
            {
                INSTANCE;


                @Override
                public void onDiscovery(String typeName) {
                }

                @Override
                public void onTransformation(TypeDescription typeDescription, Plugin plugin) {
                }

                @Override
                public void onTransformation(TypeDescription typeDescription, List<Plugin> plugins) {
                }

                @Override
                public void onIgnored(TypeDescription typeDescription, Plugin plugin) {
                }

                @Override
                public void onIgnored(TypeDescription typeDescription, List<Plugin> plugins) {
                }

                @Override
                public void onError(TypeDescription typeDescription, Plugin plugin, Throwable throwable) {
                }

                @Override
                public void onError(TypeDescription typeDescription, List<Throwable> throwables) {
                }

                @Override
                public void onError(Map<TypeDescription, List<Throwable>> throwables) {
                }

                @Override
                public void onError(Plugin plugin, Throwable throwable) {
                }

                @Override
                public void onLiveInitializer(TypeDescription typeDescription, TypeDescription definingType) {
                }

                @Override
                public void onComplete(TypeDescription typeDescription) {
                }

                @Override
                public void onUnresolved(String typeName) {
                }

                @Override
                public void onManifest(@MaybeNull Manifest manifest) {
                }

                @Override
                public void onResource(String name) {
                }
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static interface ErrorHandler {
            public void onError(TypeDescription var1, Plugin var2, Throwable var3);

            public void onError(TypeDescription var1, List<Throwable> var2);

            public void onError(Map<TypeDescription, List<Throwable>> var1);

            public void onError(Plugin var1, Throwable var2);

            public void onLiveInitializer(TypeDescription var1, TypeDescription var2);

            public void onUnresolved(String var1);

            public void onManifest(@MaybeNull Manifest var1);

            public void onResource(String var1);

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            public static class Compound
            implements ErrorHandler {
                private final List<ErrorHandler> errorHandlers = new ArrayList<ErrorHandler>();

                public Compound(ErrorHandler ... errorHandler) {
                    this(Arrays.asList(errorHandler));
                }

                public Compound(List<? extends ErrorHandler> errorHandlers) {
                    for (ErrorHandler errorHandler : errorHandlers) {
                        if (errorHandler instanceof Compound) {
                            this.errorHandlers.addAll(((Compound)errorHandler).errorHandlers);
                            continue;
                        }
                        if (errorHandler instanceof Listener.NoOp) continue;
                        this.errorHandlers.add(errorHandler);
                    }
                }

                @Override
                public void onError(TypeDescription typeDescription, Plugin plugin, Throwable throwable) {
                    for (ErrorHandler errorHandler : this.errorHandlers) {
                        errorHandler.onError(typeDescription, plugin, throwable);
                    }
                }

                @Override
                public void onError(TypeDescription typeDescription, List<Throwable> throwables) {
                    for (ErrorHandler errorHandler : this.errorHandlers) {
                        errorHandler.onError(typeDescription, throwables);
                    }
                }

                @Override
                public void onError(Map<TypeDescription, List<Throwable>> throwables) {
                    for (ErrorHandler errorHandler : this.errorHandlers) {
                        errorHandler.onError(throwables);
                    }
                }

                @Override
                public void onError(Plugin plugin, Throwable throwable) {
                    for (ErrorHandler errorHandler : this.errorHandlers) {
                        errorHandler.onError(plugin, throwable);
                    }
                }

                @Override
                public void onLiveInitializer(TypeDescription typeDescription, TypeDescription definingType) {
                    for (ErrorHandler errorHandler : this.errorHandlers) {
                        errorHandler.onLiveInitializer(typeDescription, definingType);
                    }
                }

                @Override
                public void onUnresolved(String typeName) {
                    for (ErrorHandler errorHandler : this.errorHandlers) {
                        errorHandler.onUnresolved(typeName);
                    }
                }

                @Override
                public void onManifest(@MaybeNull Manifest manifest) {
                    for (ErrorHandler errorHandler : this.errorHandlers) {
                        errorHandler.onManifest(manifest);
                    }
                }

                @Override
                public void onResource(String name) {
                    for (ErrorHandler errorHandler : this.errorHandlers) {
                        errorHandler.onResource(name);
                    }
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            public static enum Enforcing implements ErrorHandler
            {
                ALL_TYPES_RESOLVED{

                    public void onUnresolved(String typeName) {
                        throw new IllegalStateException("Failed to resolve type description for " + typeName);
                    }
                }
                ,
                NO_LIVE_INITIALIZERS{

                    public void onLiveInitializer(TypeDescription typeDescription, TypeDescription initializedType) {
                        throw new IllegalStateException("Failed to instrument " + typeDescription + " due to live initializer for " + initializedType);
                    }
                }
                ,
                CLASS_FILES_ONLY{

                    public void onResource(String name) {
                        throw new IllegalStateException("Discovered a resource when only class files were allowed: " + name);
                    }
                }
                ,
                MANIFEST_REQUIRED{

                    public void onManifest(@MaybeNull Manifest manifest) {
                        if (manifest == null) {
                            throw new IllegalStateException("Required a manifest but no manifest was found");
                        }
                    }
                };


                @Override
                public void onError(TypeDescription typeDescription, Plugin plugin, Throwable throwable) {
                }

                @Override
                public void onError(TypeDescription typeDescription, List<Throwable> throwables) {
                }

                @Override
                public void onError(Map<TypeDescription, List<Throwable>> throwables) {
                }

                @Override
                public void onError(Plugin plugin, Throwable throwable) {
                }

                @Override
                public void onLiveInitializer(TypeDescription typeDescription, TypeDescription definingType) {
                }

                @Override
                public void onUnresolved(String typeName) {
                }

                @Override
                public void onManifest(@MaybeNull Manifest manifest) {
                }

                @Override
                public void onResource(String name) {
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            public static enum Failing implements ErrorHandler
            {
                FAIL_FAST{

                    @Override
                    public void onError(TypeDescription typeDescription, Plugin plugin, Throwable throwable) {
                        throw new IllegalStateException("Failed to transform " + typeDescription + " using " + plugin, throwable);
                    }

                    @Override
                    public void onError(TypeDescription typeDescription, List<Throwable> throwables) {
                        throw new UnsupportedOperationException("onError");
                    }

                    @Override
                    public void onError(Map<TypeDescription, List<Throwable>> throwables) {
                        throw new UnsupportedOperationException("onError");
                    }
                }
                ,
                FAIL_AFTER_TYPE{

                    @Override
                    public void onError(TypeDescription typeDescription, Plugin plugin, Throwable throwable) {
                    }

                    @Override
                    public void onError(TypeDescription typeDescription, List<Throwable> throwables) {
                        throw new IllegalStateException("Failed to transform " + typeDescription + ": " + throwables);
                    }

                    @Override
                    public void onError(Map<TypeDescription, List<Throwable>> throwables) {
                        throw new UnsupportedOperationException("onError");
                    }
                }
                ,
                FAIL_LAST{

                    @Override
                    public void onError(TypeDescription typeDescription, Plugin plugin, Throwable throwable) {
                    }

                    @Override
                    public void onError(TypeDescription typeDescription, List<Throwable> throwables) {
                    }

                    @Override
                    public void onError(Map<TypeDescription, List<Throwable>> throwables) {
                        throw new IllegalStateException("Failed to transform at least one type: " + throwables);
                    }
                };


                @Override
                public void onError(Plugin plugin, Throwable throwable) {
                    throw new IllegalStateException("Failed to close plugin " + plugin, throwable);
                }

                @Override
                public void onLiveInitializer(TypeDescription typeDescription, TypeDescription definingType) {
                }

                @Override
                public void onUnresolved(String typeName) {
                }

                @Override
                public void onManifest(Manifest manifest) {
                }

                @Override
                public void onResource(String name) {
                }
            }
        }

        public static interface PoolStrategy {
            public TypePool typePool(ClassFileLocator var1);

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            public static enum Eager implements PoolStrategy
            {
                FAST(TypePool.Default.ReaderMode.FAST),
                EXTENDED(TypePool.Default.ReaderMode.EXTENDED);

                private final TypePool.Default.ReaderMode readerMode;

                private Eager(TypePool.Default.ReaderMode readerMode) {
                    this.readerMode = readerMode;
                }

                @Override
                public TypePool typePool(ClassFileLocator classFileLocator) {
                    return new TypePool.Default(new TypePool.CacheProvider.Simple(), classFileLocator, this.readerMode, TypePool.ClassLoading.ofPlatformLoader());
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            public static enum Default implements PoolStrategy
            {
                FAST(TypePool.Default.ReaderMode.FAST),
                EXTENDED(TypePool.Default.ReaderMode.EXTENDED);

                private final TypePool.Default.ReaderMode readerMode;

                private Default(TypePool.Default.ReaderMode readerMode) {
                    this.readerMode = readerMode;
                }

                @Override
                public TypePool typePool(ClassFileLocator classFileLocator) {
                    return new TypePool.Default.WithLazyResolution(new TypePool.CacheProvider.Simple(), classFileLocator, this.readerMode, TypePool.ClassLoading.ofPlatformLoader());
                }
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static interface TypeStrategy {
            public DynamicType.Builder<?> builder(ByteBuddy var1, TypeDescription var2, ClassFileLocator var3);

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            @HashCodeAndEqualsPlugin.Enhance
            public static class ForEntryPoint
            implements TypeStrategy {
                private final EntryPoint entryPoint;
                private final MethodNameTransformer methodNameTransformer;

                public ForEntryPoint(EntryPoint entryPoint, MethodNameTransformer methodNameTransformer) {
                    this.entryPoint = entryPoint;
                    this.methodNameTransformer = methodNameTransformer;
                }

                @Override
                public DynamicType.Builder<?> builder(ByteBuddy byteBuddy, TypeDescription typeDescription, ClassFileLocator classFileLocator) {
                    return this.entryPoint.transform(typeDescription, byteBuddy, classFileLocator, this.methodNameTransformer);
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
                    if (!this.entryPoint.equals(((ForEntryPoint)object).entryPoint)) {
                        return false;
                    }
                    return this.methodNameTransformer.equals(((ForEntryPoint)object).methodNameTransformer);
                }

                public int hashCode() {
                    return (this.getClass().hashCode() * 31 + this.entryPoint.hashCode()) * 31 + this.methodNameTransformer.hashCode();
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            public static enum Default implements TypeStrategy
            {
                REDEFINE{

                    @Override
                    public DynamicType.Builder<?> builder(ByteBuddy byteBuddy, TypeDescription typeDescription, ClassFileLocator classFileLocator) {
                        return byteBuddy.redefine(typeDescription, classFileLocator);
                    }
                }
                ,
                REBASE{

                    @Override
                    public DynamicType.Builder<?> builder(ByteBuddy byteBuddy, TypeDescription typeDescription, ClassFileLocator classFileLocator) {
                        return byteBuddy.rebase(typeDescription, classFileLocator);
                    }
                }
                ,
                DECORATE{

                    @Override
                    public DynamicType.Builder<?> builder(ByteBuddy byteBuddy, TypeDescription typeDescription, ClassFileLocator classFileLocator) {
                        return byteBuddy.decorate(typeDescription, classFileLocator);
                    }
                };

            }
        }
    }

    public static interface Factory {
        public Plugin make();

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        @HashCodeAndEqualsPlugin.Enhance
        public static class UsingReflection
        implements Factory {
            private final Class<? extends Plugin> type;
            private final List<ArgumentResolver> argumentResolvers;

            public UsingReflection(Class<? extends Plugin> type) {
                this(type, Collections.emptyList());
            }

            protected UsingReflection(Class<? extends Plugin> type, List<ArgumentResolver> argumentResolvers) {
                this.type = type;
                this.argumentResolvers = argumentResolvers;
            }

            public UsingReflection with(ArgumentResolver ... argumentResolver) {
                return this.with(Arrays.asList(argumentResolver));
            }

            public UsingReflection with(List<? extends ArgumentResolver> argumentResolvers) {
                return new UsingReflection(this.type, CompoundList.of(argumentResolvers, this.argumentResolvers));
            }

            @Override
            public Plugin make() {
                Instantiator instantiator = new Instantiator.Unresolved(this.type);
                block0: for (Constructor<?> constructor : this.type.getConstructors()) {
                    if (constructor.isSynthetic()) continue;
                    ArrayList<Object> arguments = new ArrayList<Object>(constructor.getParameterTypes().length);
                    int index = 0;
                    for (Class<?> type : constructor.getParameterTypes()) {
                        boolean resolved = false;
                        for (ArgumentResolver argumentResolver : this.argumentResolvers) {
                            ArgumentResolver.Resolution resolution = argumentResolver.resolve(index, type);
                            if (!resolution.isResolved()) continue;
                            arguments.add(resolution.getArgument());
                            resolved = true;
                            break;
                        }
                        if (!resolved) continue block0;
                        ++index;
                    }
                    instantiator = instantiator.replaceBy(new Instantiator.Resolved(constructor, arguments));
                }
                return instantiator.instantiate();
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
                if (!this.type.equals(((UsingReflection)object).type)) {
                    return false;
                }
                return ((Object)this.argumentResolvers).equals(((UsingReflection)object).argumentResolvers);
            }

            public int hashCode() {
                return (this.getClass().hashCode() * 31 + this.type.hashCode()) * 31 + ((Object)this.argumentResolvers).hashCode();
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            public static interface ArgumentResolver {
                public Resolution resolve(int var1, Class<?> var2);

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                @HashCodeAndEqualsPlugin.Enhance
                public static class ForIndex
                implements ArgumentResolver {
                    private static final Map<Class<?>, Class<?>> WRAPPER_TYPES = new HashMap();
                    private final int index;
                    @MaybeNull
                    @HashCodeAndEqualsPlugin.ValueHandling(value=HashCodeAndEqualsPlugin.ValueHandling.Sort.REVERSE_NULLABILITY)
                    private final Object value;

                    public ForIndex(int index, @MaybeNull Object value) {
                        this.index = index;
                        this.value = value;
                    }

                    @Override
                    public Resolution resolve(int index, Class<?> type) {
                        if (this.index != index) {
                            return Resolution.Unresolved.INSTANCE;
                        }
                        if (type.isPrimitive()) {
                            return WRAPPER_TYPES.get(type).isInstance(this.value) ? new Resolution.Resolved(this.value) : Resolution.Unresolved.INSTANCE;
                        }
                        return this.value == null || type.isInstance(this.value) ? new Resolution.Resolved(this.value) : Resolution.Unresolved.INSTANCE;
                    }

                    static {
                        WRAPPER_TYPES.put(Boolean.TYPE, Boolean.class);
                        WRAPPER_TYPES.put(Byte.TYPE, Byte.class);
                        WRAPPER_TYPES.put(Short.TYPE, Short.class);
                        WRAPPER_TYPES.put(Character.TYPE, Character.class);
                        WRAPPER_TYPES.put(Integer.TYPE, Integer.class);
                        WRAPPER_TYPES.put(Long.TYPE, Long.class);
                        WRAPPER_TYPES.put(Float.TYPE, Float.class);
                        WRAPPER_TYPES.put(Double.TYPE, Double.class);
                    }

                    public boolean equals(@MaybeNull Object object) {
                        block11: {
                            block10: {
                                Object object2;
                                block9: {
                                    Object object3;
                                    if (this == object) {
                                        return true;
                                    }
                                    if (object == null) {
                                        return false;
                                    }
                                    if (this.getClass() != object.getClass()) {
                                        return false;
                                    }
                                    if (this.index != ((ForIndex)object).index) {
                                        return false;
                                    }
                                    Object object4 = ((ForIndex)object).value;
                                    object2 = object3 = this.value;
                                    if (object4 == null) break block9;
                                    if (object2 == null) break block10;
                                    if (!object3.equals(object4)) {
                                        return false;
                                    }
                                    break block11;
                                }
                                if (object2 == null) break block11;
                            }
                            return false;
                        }
                        return true;
                    }

                    public int hashCode() {
                        int n = (this.getClass().hashCode() * 31 + this.index) * 31;
                        Object object = this.value;
                        if (object != null) {
                            n = n + object.hashCode();
                        }
                        return n;
                    }

                    /*
                     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                     */
                    @HashCodeAndEqualsPlugin.Enhance
                    public static class WithDynamicType
                    implements ArgumentResolver {
                        private final int index;
                        @MaybeNull
                        @HashCodeAndEqualsPlugin.ValueHandling(value=HashCodeAndEqualsPlugin.ValueHandling.Sort.REVERSE_NULLABILITY)
                        private final String value;

                        public WithDynamicType(int index, @MaybeNull String value) {
                            this.index = index;
                            this.value = value;
                        }

                        @Override
                        public Resolution resolve(int index, Class<?> type) {
                            if (this.index != index) {
                                return Resolution.Unresolved.INSTANCE;
                            }
                            if (type == Character.TYPE || type == Character.class) {
                                return this.value != null && this.value.length() == 1 ? new Resolution.Resolved(Character.valueOf(this.value.charAt(0))) : Resolution.Unresolved.INSTANCE;
                            }
                            if (type == String.class) {
                                return new Resolution.Resolved(this.value);
                            }
                            if (type.isPrimitive()) {
                                type = (Class)WRAPPER_TYPES.get(type);
                            }
                            try {
                                Method valueOf = type.getMethod("valueOf", String.class);
                                return Modifier.isStatic(valueOf.getModifiers()) && type.isAssignableFrom(valueOf.getReturnType()) ? new Resolution.Resolved(valueOf.invoke(null, this.value)) : Resolution.Unresolved.INSTANCE;
                            }
                            catch (IllegalAccessException exception) {
                                throw new IllegalStateException(exception);
                            }
                            catch (InvocationTargetException exception) {
                                throw new IllegalStateException(exception.getTargetException());
                            }
                            catch (NoSuchMethodException ignored) {
                                return Resolution.Unresolved.INSTANCE;
                            }
                        }

                        public boolean equals(@MaybeNull Object object) {
                            block11: {
                                block10: {
                                    String string;
                                    block9: {
                                        String string2;
                                        if (this == object) {
                                            return true;
                                        }
                                        if (object == null) {
                                            return false;
                                        }
                                        if (this.getClass() != object.getClass()) {
                                            return false;
                                        }
                                        if (this.index != ((WithDynamicType)object).index) {
                                            return false;
                                        }
                                        String string3 = ((WithDynamicType)object).value;
                                        string = string2 = this.value;
                                        if (string3 == null) break block9;
                                        if (string == null) break block10;
                                        if (!string2.equals(string3)) {
                                            return false;
                                        }
                                        break block11;
                                    }
                                    if (string == null) break block11;
                                }
                                return false;
                            }
                            return true;
                        }

                        public int hashCode() {
                            int n = (this.getClass().hashCode() * 31 + this.index) * 31;
                            String string = this.value;
                            if (string != null) {
                                n = n + string.hashCode();
                            }
                            return n;
                        }
                    }
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                @HashCodeAndEqualsPlugin.Enhance
                public static class ForType<T>
                implements ArgumentResolver {
                    private final Class<? extends T> type;
                    private final T value;

                    protected ForType(Class<? extends T> type, T value) {
                        this.type = type;
                        this.value = value;
                    }

                    public static <S> ArgumentResolver of(Class<? extends S> type, S value) {
                        return new ForType<S>(type, value);
                    }

                    @Override
                    public Resolution resolve(int index, Class<?> type) {
                        return type == this.type ? new Resolution.Resolved(this.value) : Resolution.Unresolved.INSTANCE;
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
                        if (!this.type.equals(((ForType)object).type)) {
                            return false;
                        }
                        return this.value.equals(((ForType)object).value);
                    }

                    public int hashCode() {
                        return (this.getClass().hashCode() * 31 + this.type.hashCode()) * 31 + this.value.hashCode();
                    }
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                public static enum NoOp implements ArgumentResolver
                {
                    INSTANCE;


                    @Override
                    public Resolution resolve(int index, Class<?> type) {
                        return Resolution.Unresolved.INSTANCE;
                    }
                }

                public static interface Resolution {
                    public boolean isResolved();

                    @MaybeNull
                    public Object getArgument();

                    @HashCodeAndEqualsPlugin.Enhance
                    public static class Resolved
                    implements Resolution {
                        @MaybeNull
                        @HashCodeAndEqualsPlugin.ValueHandling(value=HashCodeAndEqualsPlugin.ValueHandling.Sort.REVERSE_NULLABILITY)
                        private final Object argument;

                        public Resolved(@MaybeNull Object argument) {
                            this.argument = argument;
                        }

                        public boolean isResolved() {
                            return true;
                        }

                        @MaybeNull
                        public Object getArgument() {
                            return this.argument;
                        }

                        public boolean equals(@MaybeNull Object object) {
                            block10: {
                                block9: {
                                    Object object2;
                                    block8: {
                                        Object object3;
                                        if (this == object) {
                                            return true;
                                        }
                                        if (object == null) {
                                            return false;
                                        }
                                        if (this.getClass() != object.getClass()) {
                                            return false;
                                        }
                                        Object object4 = ((Resolved)object).argument;
                                        object2 = object3 = this.argument;
                                        if (object4 == null) break block8;
                                        if (object2 == null) break block9;
                                        if (!object3.equals(object4)) {
                                            return false;
                                        }
                                        break block10;
                                    }
                                    if (object2 == null) break block10;
                                }
                                return false;
                            }
                            return true;
                        }

                        public int hashCode() {
                            int n = this.getClass().hashCode() * 31;
                            Object object = this.argument;
                            if (object != null) {
                                n = n + object.hashCode();
                            }
                            return n;
                        }
                    }

                    /*
                     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                     */
                    public static enum Unresolved implements Resolution
                    {
                        INSTANCE;


                        @Override
                        public boolean isResolved() {
                            return false;
                        }

                        @Override
                        public Object getArgument() {
                            throw new IllegalStateException("Cannot get the argument for an unresolved parameter");
                        }
                    }
                }
            }

            @Documented
            @Target(value={ElementType.CONSTRUCTOR})
            @Retention(value=RetentionPolicy.RUNTIME)
            public static @interface Priority {
                public static final int DEFAULT = 0;

                public int value();
            }

            protected static interface Instantiator {
                public Instantiator replaceBy(Resolved var1);

                public Plugin instantiate();

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                @HashCodeAndEqualsPlugin.Enhance
                public static class Resolved
                implements Instantiator {
                    private final Constructor<? extends Plugin> constructor;
                    private final List<?> arguments;

                    protected Resolved(Constructor<? extends Plugin> constructor, List<?> arguments) {
                        this.constructor = constructor;
                        this.arguments = arguments;
                    }

                    protected Constructor<? extends Plugin> getConstructor() {
                        return this.constructor;
                    }

                    @Override
                    public Instantiator replaceBy(Resolved instantiator) {
                        int rightPriority;
                        Priority left = this.constructor.getAnnotation(Priority.class);
                        Priority right = instantiator.getConstructor().getAnnotation(Priority.class);
                        int leftPriority = left == null ? 0 : left.value();
                        int n = rightPriority = right == null ? 0 : right.value();
                        if (leftPriority > rightPriority) {
                            return this;
                        }
                        if (leftPriority < rightPriority) {
                            return instantiator;
                        }
                        if (this.constructor.getParameterTypes().length > instantiator.getConstructor().getParameterTypes().length) {
                            return this;
                        }
                        if (this.constructor.getParameterTypes().length < instantiator.getConstructor().getParameterTypes().length) {
                            return instantiator;
                        }
                        return new Ambiguous(this.constructor, instantiator.getConstructor(), leftPriority, this.constructor.getParameterTypes().length);
                    }

                    @Override
                    public Plugin instantiate() {
                        try {
                            return this.constructor.newInstance(this.arguments.toArray(new Object[0]));
                        }
                        catch (InstantiationException exception) {
                            throw new IllegalStateException("Failed to instantiate plugin via " + this.constructor, exception);
                        }
                        catch (IllegalAccessException exception) {
                            throw new IllegalStateException("Failed to access " + this.constructor, exception);
                        }
                        catch (InvocationTargetException exception) {
                            throw new IllegalStateException("Error during construction of" + this.constructor, exception.getTargetException());
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
                        if (!this.constructor.equals(((Resolved)object).constructor)) {
                            return false;
                        }
                        return ((Object)this.arguments).equals(((Resolved)object).arguments);
                    }

                    public int hashCode() {
                        return (this.getClass().hashCode() * 31 + this.constructor.hashCode()) * 31 + ((Object)this.arguments).hashCode();
                    }
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                @HashCodeAndEqualsPlugin.Enhance
                public static class Ambiguous
                implements Instantiator {
                    private final Constructor<?> left;
                    private final Constructor<?> right;
                    private final int priority;
                    private final int parameters;

                    protected Ambiguous(Constructor<?> left, Constructor<?> right, int priority, int parameters) {
                        this.left = left;
                        this.right = right;
                        this.priority = priority;
                        this.parameters = parameters;
                    }

                    @Override
                    public Instantiator replaceBy(Resolved instantiator) {
                        Priority priority = instantiator.getConstructor().getAnnotation(Priority.class);
                        if ((priority == null ? 0 : priority.value()) > this.priority) {
                            return instantiator;
                        }
                        if ((priority == null ? 0 : priority.value()) < this.priority) {
                            return this;
                        }
                        if (instantiator.getConstructor().getParameterTypes().length > this.parameters) {
                            return instantiator;
                        }
                        return this;
                    }

                    @Override
                    public Plugin instantiate() {
                        throw new IllegalStateException("Ambiguous constructors " + this.left + " and " + this.right);
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
                        if (this.priority != ((Ambiguous)object).priority) {
                            return false;
                        }
                        if (this.parameters != ((Ambiguous)object).parameters) {
                            return false;
                        }
                        if (!this.left.equals(((Ambiguous)object).left)) {
                            return false;
                        }
                        return this.right.equals(((Ambiguous)object).right);
                    }

                    public int hashCode() {
                        return (((this.getClass().hashCode() * 31 + this.left.hashCode()) * 31 + this.right.hashCode()) * 31 + this.priority) * 31 + this.parameters;
                    }
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                @HashCodeAndEqualsPlugin.Enhance
                public static class Unresolved
                implements Instantiator {
                    private final Class<? extends Plugin> type;

                    protected Unresolved(Class<? extends Plugin> type) {
                        this.type = type;
                    }

                    @Override
                    public Instantiator replaceBy(Resolved instantiator) {
                        return instantiator;
                    }

                    @Override
                    public Plugin instantiate() {
                        throw new IllegalStateException("No constructor resolvable for " + this.type);
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
                        return this.type.equals(((Unresolved)object).type);
                    }

                    public int hashCode() {
                        return this.getClass().hashCode() * 31 + this.type.hashCode();
                    }
                }
            }
        }

        @HashCodeAndEqualsPlugin.Enhance
        public static class Simple
        implements Factory {
            private final Plugin plugin;

            public Simple(Plugin plugin) {
                this.plugin = plugin;
            }

            public Plugin make() {
                return this.plugin;
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
                return this.plugin.equals(((Simple)object).plugin);
            }

            public int hashCode() {
                return this.getClass().hashCode() * 31 + this.plugin.hashCode();
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static interface WithInitialization
    extends Plugin {
        public Map<TypeDescription, byte[]> initialize(ClassFileLocator var1);
    }

    public static interface WithPreprocessor
    extends Plugin {
        public void onPreprocess(TypeDescription var1, ClassFileLocator var2);
    }
}

