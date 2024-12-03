/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy.dynamic.loading;

import java.io.File;
import java.io.IOException;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.build.AccessControllerPlugin;
import net.bytebuddy.build.HashCodeAndEqualsPlugin;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.dynamic.loading.ClassInjector;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.utility.JavaModule;
import net.bytebuddy.utility.dispatcher.JavaDispatcher;
import net.bytebuddy.utility.nullability.AlwaysNull;
import net.bytebuddy.utility.nullability.MaybeNull;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@HashCodeAndEqualsPlugin.Enhance
public class ClassReloadingStrategy
implements ClassLoadingStrategy<ClassLoader> {
    protected static final Dispatcher DISPATCHER;
    private final Instrumentation instrumentation;
    private final Strategy strategy;
    private final BootstrapInjection bootstrapInjection;
    private final Map<String, Class<?>> preregisteredTypes;
    private static final boolean ACCESS_CONTROLLER;

    public ClassReloadingStrategy(Instrumentation instrumentation, Strategy strategy) {
        this(instrumentation, strategy, BootstrapInjection.Disabled.INSTANCE, Collections.emptyMap());
    }

    protected ClassReloadingStrategy(Instrumentation instrumentation, Strategy strategy, BootstrapInjection bootstrapInjection, Map<String, Class<?>> preregisteredTypes) {
        this.instrumentation = instrumentation;
        this.strategy = strategy.validate(instrumentation);
        this.bootstrapInjection = bootstrapInjection;
        this.preregisteredTypes = preregisteredTypes;
    }

    @AccessControllerPlugin.Enhance
    private static <T> T doPrivileged(PrivilegedAction<T> privilegedAction) {
        PrivilegedAction<T> action;
        if (ACCESS_CONTROLLER) {
            return AccessController.doPrivileged(privilegedAction);
        }
        return action.run();
    }

    public static ClassReloadingStrategy of(Instrumentation instrumentation) {
        if (DISPATCHER.isRetransformClassesSupported(instrumentation)) {
            return new ClassReloadingStrategy(instrumentation, Strategy.RETRANSFORMATION);
        }
        if (instrumentation.isRedefineClassesSupported()) {
            return new ClassReloadingStrategy(instrumentation, Strategy.REDEFINITION);
        }
        throw new IllegalArgumentException("Instrumentation does not support reloading of classes: " + instrumentation);
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

    public static ClassReloadingStrategy fromInstalledAgent() {
        return ClassReloadingStrategy.of(ClassReloadingStrategy.resolveByteBuddyAgentInstrumentation());
    }

    public static ClassReloadingStrategy fromInstalledAgent(Strategy strategy) {
        return new ClassReloadingStrategy(ClassReloadingStrategy.resolveByteBuddyAgentInstrumentation(), strategy);
    }

    @Override
    public Map<TypeDescription, Class<?>> load(@MaybeNull ClassLoader classLoader, Map<TypeDescription, byte[]> types) {
        HashMap availableTypes = new HashMap(this.preregisteredTypes);
        for (Class type : this.instrumentation.getInitiatedClasses(classLoader)) {
            availableTypes.put(TypeDescription.ForLoadedType.getName(type), type);
        }
        ConcurrentHashMap classDefinitions = new ConcurrentHashMap();
        HashMap loadedClasses = new HashMap();
        LinkedHashMap unloadedClasses = new LinkedHashMap();
        for (Map.Entry entry : types.entrySet()) {
            Class type = (Class)availableTypes.get(((TypeDescription)entry.getKey()).getName());
            if (type != null) {
                classDefinitions.put(type, new ClassDefinition(type, (byte[])entry.getValue()));
                loadedClasses.put((TypeDescription)entry.getKey(), type);
                continue;
            }
            unloadedClasses.put(entry.getKey(), entry.getValue());
        }
        try {
            this.strategy.apply(this.instrumentation, classDefinitions);
            if (!unloadedClasses.isEmpty()) {
                loadedClasses.putAll((classLoader == null ? this.bootstrapInjection.make(this.instrumentation) : new ClassInjector.UsingReflection(classLoader)).inject(unloadedClasses));
            }
        }
        catch (ClassNotFoundException exception) {
            throw new IllegalArgumentException("Could not locate classes for redefinition", exception);
        }
        catch (UnmodifiableClassException exception) {
            throw new IllegalStateException("Cannot redefine specified class", exception);
        }
        return loadedClasses;
    }

    public ClassReloadingStrategy reset(Class<?> ... type) throws IOException {
        return type.length == 0 ? this : this.reset(ClassFileLocator.ForClassLoader.of(type[0].getClassLoader()), type);
    }

    public ClassReloadingStrategy reset(ClassFileLocator classFileLocator, Class<?> ... type) throws IOException {
        if (type.length > 0) {
            try {
                this.strategy.reset(this.instrumentation, classFileLocator, Arrays.asList(type));
            }
            catch (ClassNotFoundException exception) {
                throw new IllegalArgumentException("Cannot locate types " + Arrays.toString(type), exception);
            }
            catch (UnmodifiableClassException exception) {
                throw new IllegalStateException("Cannot reset types " + Arrays.toString(type), exception);
            }
        }
        return this;
    }

    public ClassReloadingStrategy enableBootstrapInjection(File folder) {
        return new ClassReloadingStrategy(this.instrumentation, this.strategy, new BootstrapInjection.Enabled(folder), this.preregisteredTypes);
    }

    public ClassReloadingStrategy preregistered(Class<?> ... type) {
        HashMap preregisteredTypes = new HashMap(this.preregisteredTypes);
        for (Class<?> aType : type) {
            preregisteredTypes.put(TypeDescription.ForLoadedType.getName(aType), aType);
        }
        return new ClassReloadingStrategy(this.instrumentation, this.strategy, this.bootstrapInjection, preregisteredTypes);
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
        DISPATCHER = ClassReloadingStrategy.doPrivileged(JavaDispatcher.of(Dispatcher.class));
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
        if (!this.strategy.equals((Object)((ClassReloadingStrategy)object).strategy)) {
            return false;
        }
        if (!this.instrumentation.equals(((ClassReloadingStrategy)object).instrumentation)) {
            return false;
        }
        if (!this.bootstrapInjection.equals(((ClassReloadingStrategy)object).bootstrapInjection)) {
            return false;
        }
        return ((Object)this.preregisteredTypes).equals(((ClassReloadingStrategy)object).preregisteredTypes);
    }

    public int hashCode() {
        return (((this.getClass().hashCode() * 31 + this.instrumentation.hashCode()) * 31 + this.strategy.hashCode()) * 31 + this.bootstrapInjection.hashCode()) * 31 + ((Object)this.preregisteredTypes).hashCode();
    }

    protected static interface BootstrapInjection {
        public ClassInjector make(Instrumentation var1);

        @HashCodeAndEqualsPlugin.Enhance
        public static class Enabled
        implements BootstrapInjection {
            private final File folder;

            protected Enabled(File folder) {
                this.folder = folder;
            }

            public ClassInjector make(Instrumentation instrumentation) {
                return ClassInjector.UsingInstrumentation.of(this.folder, ClassInjector.UsingInstrumentation.Target.BOOTSTRAP, instrumentation);
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
                return this.folder.equals(((Enabled)object).folder);
            }

            public int hashCode() {
                return this.getClass().hashCode() * 31 + this.folder.hashCode();
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static enum Disabled implements BootstrapInjection
        {
            INSTANCE;


            @Override
            public ClassInjector make(Instrumentation instrumentation) {
                throw new IllegalStateException("Bootstrap injection is not enabled");
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum Strategy {
        REDEFINITION(true){

            @Override
            protected void apply(Instrumentation instrumentation, Map<Class<?>, ClassDefinition> classDefinitions) throws UnmodifiableClassException, ClassNotFoundException {
                instrumentation.redefineClasses(classDefinitions.values().toArray(new ClassDefinition[0]));
            }

            @Override
            protected Strategy validate(Instrumentation instrumentation) {
                if (!instrumentation.isRedefineClassesSupported()) {
                    throw new IllegalArgumentException("Does not support redefinition: " + instrumentation);
                }
                return this;
            }

            @Override
            public void reset(Instrumentation instrumentation, ClassFileLocator classFileLocator, List<Class<?>> types) throws IOException, UnmodifiableClassException, ClassNotFoundException {
                HashMap classDefinitions = new HashMap(types.size());
                for (Class<?> type : types) {
                    classDefinitions.put(type, new ClassDefinition(type, classFileLocator.locate(TypeDescription.ForLoadedType.getName(type)).resolve()));
                }
                this.apply(instrumentation, classDefinitions);
            }
        }
        ,
        RETRANSFORMATION(false){

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            protected void apply(Instrumentation instrumentation, Map<Class<?>, ClassDefinition> classDefinitions) throws UnmodifiableClassException {
                ClassRedefinitionTransformer classRedefinitionTransformer = new ClassRedefinitionTransformer(classDefinitions);
                2 var4_4 = this;
                synchronized (var4_4) {
                    DISPATCHER.addTransformer(instrumentation, classRedefinitionTransformer, true);
                    try {
                        DISPATCHER.retransformClasses(instrumentation, classDefinitions.keySet().toArray(new Class[0]));
                        Object var6_5 = null;
                        instrumentation.removeTransformer(classRedefinitionTransformer);
                    }
                    catch (Throwable throwable) {
                        Object var6_6 = null;
                        instrumentation.removeTransformer(classRedefinitionTransformer);
                        throw throwable;
                    }
                }
                classRedefinitionTransformer.assertTransformation();
            }

            @Override
            protected Strategy validate(Instrumentation instrumentation) {
                if (!DISPATCHER.isRetransformClassesSupported(instrumentation)) {
                    throw new IllegalArgumentException("Does not support retransformation: " + instrumentation);
                }
                return this;
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            public void reset(Instrumentation instrumentation, ClassFileLocator classFileLocator, List<Class<?>> types) throws UnmodifiableClassException, ClassNotFoundException {
                for (Class<?> type : types) {
                    if (DISPATCHER.isModifiableClass(instrumentation, type)) continue;
                    throw new IllegalArgumentException("Cannot modify type: " + type);
                }
                DISPATCHER.addTransformer(instrumentation, ClassResettingTransformer.INSTANCE, true);
                try {
                    DISPATCHER.retransformClasses(instrumentation, types.toArray(new Class[0]));
                    Object var7_6 = null;
                    instrumentation.removeTransformer(ClassResettingTransformer.INSTANCE);
                }
                catch (Throwable throwable) {
                    Object var7_7 = null;
                    instrumentation.removeTransformer(ClassResettingTransformer.INSTANCE);
                    throw throwable;
                }
            }
        };

        @AlwaysNull
        private static final byte[] NO_REDEFINITION;
        private static final boolean REDEFINE_CLASSES = true;
        private final boolean redefinition;

        private Strategy(boolean redefinition) {
            this.redefinition = redefinition;
        }

        protected abstract void apply(Instrumentation var1, Map<Class<?>, ClassDefinition> var2) throws UnmodifiableClassException, ClassNotFoundException;

        protected abstract Strategy validate(Instrumentation var1);

        public boolean isRedefinition() {
            return this.redefinition;
        }

        public abstract void reset(Instrumentation var1, ClassFileLocator var2, List<Class<?>> var3) throws IOException, UnmodifiableClassException, ClassNotFoundException;

        static {
            NO_REDEFINITION = null;
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        protected static enum ClassResettingTransformer implements ClassFileTransformer
        {
            INSTANCE;


            @Override
            @MaybeNull
            public byte[] transform(@MaybeNull ClassLoader classLoader, @MaybeNull String internalTypeName, @MaybeNull Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
                return NO_REDEFINITION;
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        protected static class ClassRedefinitionTransformer
        implements ClassFileTransformer {
            private final Map<Class<?>, ClassDefinition> redefinedClasses;

            protected ClassRedefinitionTransformer(Map<Class<?>, ClassDefinition> redefinedClasses) {
                this.redefinedClasses = redefinedClasses;
            }

            @Override
            @MaybeNull
            public byte[] transform(@MaybeNull ClassLoader classLoader, @MaybeNull String internalTypeName, @MaybeNull Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
                if (internalTypeName == null) {
                    return NO_REDEFINITION;
                }
                ClassDefinition redefinedClass = this.redefinedClasses.remove(classBeingRedefined);
                return redefinedClass == null ? NO_REDEFINITION : redefinedClass.getDefinitionClassFile();
            }

            public void assertTransformation() {
                if (!this.redefinedClasses.isEmpty()) {
                    throw new IllegalStateException("Could not transform: " + this.redefinedClasses.keySet());
                }
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @JavaDispatcher.Proxied(value="java.lang.instrument.Instrumentation")
    protected static interface Dispatcher {
        @JavaDispatcher.Proxied(value="isModifiableClass")
        public boolean isModifiableClass(Instrumentation var1, Class<?> var2);

        @JavaDispatcher.Proxied(value="isRetransformClassesSupported")
        public boolean isRetransformClassesSupported(Instrumentation var1);

        @JavaDispatcher.Proxied(value="addTransformer")
        public void addTransformer(Instrumentation var1, ClassFileTransformer var2, boolean var3);

        @JavaDispatcher.Proxied(value="retransformClasses")
        public void retransformClasses(Instrumentation var1, Class<?>[] var2) throws UnmodifiableClassException;
    }
}

