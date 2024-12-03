/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package net.bytebuddy.utility.dispatcher;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.security.AccessController;
import java.security.Permission;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import net.bytebuddy.ClassFileVersion;
import net.bytebuddy.build.AccessControllerPlugin;
import net.bytebuddy.build.HashCodeAndEqualsPlugin;
import net.bytebuddy.jar.asm.ClassWriter;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.jar.asm.Type;
import net.bytebuddy.utility.GraalImageCode;
import net.bytebuddy.utility.Invoker;
import net.bytebuddy.utility.MethodComparator;
import net.bytebuddy.utility.nullability.MaybeNull;
import net.bytebuddy.utility.privilege.GetSystemPropertyAction;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@HashCodeAndEqualsPlugin.Enhance
public class JavaDispatcher<T>
implements PrivilegedAction<T> {
    public static final String GENERATE_PROPERTY = "net.bytebuddy.generate";
    private static final boolean GENERATE;
    private static final DynamicClassLoader.Resolver RESOLVER;
    private static final Invoker INVOKER;
    private final Class<T> proxy;
    @MaybeNull
    @HashCodeAndEqualsPlugin.ValueHandling(value=HashCodeAndEqualsPlugin.ValueHandling.Sort.REVERSE_NULLABILITY)
    private final ClassLoader classLoader;
    private final boolean generate;
    private static final boolean ACCESS_CONTROLLER;

    protected JavaDispatcher(Class<T> proxy, @MaybeNull ClassLoader classLoader, boolean generate) {
        this.proxy = proxy;
        this.classLoader = classLoader;
        this.generate = generate;
    }

    @AccessControllerPlugin.Enhance
    private static <T> T doPrivileged(PrivilegedAction<T> privilegedAction) {
        PrivilegedAction<T> action;
        if (ACCESS_CONTROLLER) {
            return AccessController.doPrivileged(privilegedAction);
        }
        return action.run();
    }

    public static <T> PrivilegedAction<T> of(Class<T> type) {
        return JavaDispatcher.of(type, null);
    }

    protected static <T> PrivilegedAction<T> of(Class<T> type, @MaybeNull ClassLoader classLoader) {
        return JavaDispatcher.of(type, classLoader, GENERATE);
    }

    protected static <T> PrivilegedAction<T> of(Class<T> type, @MaybeNull ClassLoader classLoader, boolean generate) {
        if (!type.isInterface()) {
            throw new IllegalArgumentException("Expected an interface instead of " + type);
        }
        if (!type.isAnnotationPresent(Proxied.class)) {
            throw new IllegalArgumentException("Expected " + type.getName() + " to be annotated with " + Proxied.class.getName());
        }
        if (type.getAnnotation(Proxied.class).value().startsWith("java.security.")) {
            throw new IllegalArgumentException("Classes related to Java security cannot be proxied: " + type.getName());
        }
        return new JavaDispatcher<T>(type, classLoader, generate);
    }

    @Override
    public T run() {
        Class<?> target;
        try {
            Object securityManager = System.class.getMethod("getSecurityManager", new Class[0]).invoke(null, new Object[0]);
            if (securityManager != null) {
                Class.forName("java.lang.SecurityManager").getMethod("checkPermission", Permission.class).invoke(securityManager, new RuntimePermission("net.bytebuddy.createJavaDispatcher"));
            }
        }
        catch (NoSuchMethodException securityManager) {
        }
        catch (ClassNotFoundException securityManager) {
        }
        catch (InvocationTargetException exception) {
            Throwable cause = exception.getTargetException();
            if (cause instanceof RuntimeException) {
                throw (RuntimeException)cause;
            }
            throw new IllegalStateException("Failed to assert access rights using security manager", cause);
        }
        catch (IllegalAccessException exception) {
            throw new IllegalStateException("Failed to access security manager", exception);
        }
        LinkedHashMap<Method, Dispatcher> dispatchers = this.generate ? new LinkedHashMap() : new HashMap();
        boolean defaults = this.proxy.isAnnotationPresent(Defaults.class);
        String name = this.proxy.getAnnotation(Proxied.class).value();
        try {
            target = Class.forName(name, false, this.classLoader);
        }
        catch (ClassNotFoundException exception) {
            for (Method method : this.generate ? GraalImageCode.getCurrent().sorted(this.proxy.getMethods(), MethodComparator.INSTANCE) : this.proxy.getMethods()) {
                if (method.getDeclaringClass() == Object.class) continue;
                if (method.isAnnotationPresent(Instance.class)) {
                    if (method.getParameterTypes().length != 1 || method.getParameterTypes()[0].isPrimitive() || method.getParameterTypes()[0].isArray()) {
                        throw new IllegalStateException("Instance check requires a single regular-typed argument: " + method);
                    }
                    if (method.getReturnType() != Boolean.TYPE) {
                        throw new IllegalStateException("Instance check requires a boolean return type: " + method);
                    }
                    dispatchers.put(method, Dispatcher.ForDefaultValue.BOOLEAN);
                    continue;
                }
                dispatchers.put(method, defaults || method.isAnnotationPresent(Defaults.class) ? Dispatcher.ForDefaultValue.of(method.getReturnType()) : new Dispatcher.ForUnresolvedMethod("Type not available on current VM: " + exception.getMessage()));
            }
            if (this.generate) {
                return (T)DynamicClassLoader.proxy(this.proxy, dispatchers);
            }
            return (T)Proxy.newProxyInstance(this.proxy.getClassLoader(), new Class[]{this.proxy}, (InvocationHandler)new ProxiedInvocationHandler(name, dispatchers));
        }
        boolean generate = this.generate;
        for (Method method : generate ? GraalImageCode.getCurrent().sorted(this.proxy.getMethods(), MethodComparator.INSTANCE) : this.proxy.getMethods()) {
            if (method.getDeclaringClass() == Object.class) continue;
            if (method.isAnnotationPresent(Instance.class)) {
                if (method.getParameterTypes().length != 1 || !method.getParameterTypes()[0].isAssignableFrom(target)) {
                    throw new IllegalStateException("Instance check requires a single regular-typed argument: " + method);
                }
                if (method.getReturnType() != Boolean.TYPE) {
                    throw new IllegalStateException("Instance check requires a boolean return type: " + method);
                }
                dispatchers.put(method, new Dispatcher.ForInstanceCheck(target));
                continue;
            }
            if (method.isAnnotationPresent(Container.class)) {
                if (method.getParameterTypes().length != 1 || method.getParameterTypes()[0] != Integer.TYPE) {
                    throw new IllegalStateException("Container creation requires a single int-typed argument: " + method);
                }
                if (!method.getReturnType().isArray() || !method.getReturnType().getComponentType().isAssignableFrom(target)) {
                    throw new IllegalStateException("Container creation requires an assignable array as return value: " + method);
                }
                dispatchers.put(method, new Dispatcher.ForContainerCreation(target));
                continue;
            }
            if (target.getName().equals("java.lang.invoke.MethodHandles") && method.getName().equals("lookup")) {
                throw new UnsupportedOperationException("Cannot resolve Byte Buddy lookup via dispatcher");
            }
            try {
                int offset;
                Class<?>[] parameterType = method.getParameterTypes();
                if (method.isAnnotationPresent(IsStatic.class) || method.isAnnotationPresent(IsConstructor.class)) {
                    offset = 0;
                } else {
                    offset = 1;
                    if (parameterType.length == 0) {
                        throw new IllegalStateException("Expected self type: " + method);
                    }
                    if (!parameterType[0].isAssignableFrom(target)) {
                        throw new IllegalStateException("Cannot assign self type: " + target + " on " + method);
                    }
                    Class[] adjusted = new Class[parameterType.length - 1];
                    System.arraycopy(parameterType, 1, adjusted, 0, adjusted.length);
                    parameterType = adjusted;
                }
                Annotation[][] parameterAnnotation = method.getParameterAnnotations();
                block13: for (int index = 0; index < parameterType.length; ++index) {
                    for (Annotation annotation : parameterAnnotation[index + offset]) {
                        if (!(annotation instanceof Proxied)) continue;
                        int arity = 0;
                        while (parameterType[index].isArray()) {
                            ++arity;
                            parameterType[index] = parameterType[index].getComponentType();
                        }
                        if (arity > 0) {
                            if (parameterType[index].isPrimitive()) {
                                throw new IllegalStateException("Primitive values are not supposed to be proxied: " + index + " of " + method);
                            }
                            if (!parameterType[index].isAssignableFrom(Class.forName(((Proxied)annotation).value(), false, this.classLoader))) {
                                throw new IllegalStateException("Cannot resolve to component type: " + ((Proxied)annotation).value() + " at " + index + " of " + method);
                            }
                            StringBuilder stringBuilder = new StringBuilder();
                            while (arity-- > 0) {
                                stringBuilder.append('[');
                            }
                            parameterType[index] = Class.forName(stringBuilder.append('L').append(((Proxied)annotation).value()).append(';').toString(), false, this.classLoader);
                            continue block13;
                        }
                        Class<?> resolved = Class.forName(((Proxied)annotation).value(), false, this.classLoader);
                        if (!parameterType[index].isAssignableFrom(resolved)) {
                            throw new IllegalStateException("Cannot resolve to type: " + resolved.getName() + " at " + index + " of " + method);
                        }
                        parameterType[index] = resolved;
                        continue block13;
                    }
                }
                if (method.isAnnotationPresent(IsConstructor.class)) {
                    Constructor<?> resolved = target.getConstructor(parameterType);
                    if (!method.getReturnType().isAssignableFrom(target)) {
                        throw new IllegalStateException("Cannot assign " + resolved.getDeclaringClass().getName() + " to " + method);
                    }
                    if ((resolved.getModifiers() & 1) == 0 || (target.getModifiers() & 1) == 0) {
                        resolved.setAccessible(true);
                        generate = false;
                    }
                    dispatchers.put(method, new Dispatcher.ForConstructor(resolved));
                    continue;
                }
                Proxied proxied = method.getAnnotation(Proxied.class);
                Method resolved = target.getMethod(proxied == null ? method.getName() : proxied.value(), parameterType);
                if (!method.getReturnType().isAssignableFrom(resolved.getReturnType())) {
                    throw new IllegalStateException("Cannot assign " + resolved.getReturnType().getName() + " to " + method);
                }
                block17: for (Class<?> type : resolved.getExceptionTypes()) {
                    if (RuntimeException.class.isAssignableFrom(type) || Error.class.isAssignableFrom(type)) continue;
                    for (Class<?> exception : method.getExceptionTypes()) {
                        if (exception.isAssignableFrom(type)) continue block17;
                    }
                    throw new IllegalStateException("Resolved method for " + method + " throws undeclared checked exception " + type.getName());
                }
                if ((resolved.getModifiers() & 1) == 0 || (resolved.getDeclaringClass().getModifiers() & 1) == 0) {
                    resolved.setAccessible(true);
                    generate = false;
                }
                if (Modifier.isStatic(resolved.getModifiers())) {
                    if (!method.isAnnotationPresent(IsStatic.class)) {
                        throw new IllegalStateException("Resolved method for " + method + " was expected to be static: " + resolved);
                    }
                    dispatchers.put(method, new Dispatcher.ForStaticMethod(resolved));
                    continue;
                }
                if (method.isAnnotationPresent(IsStatic.class)) {
                    throw new IllegalStateException("Resolved method for " + method + " was expected to be virtual: " + resolved);
                }
                dispatchers.put(method, new Dispatcher.ForNonStaticMethod(resolved));
            }
            catch (ClassNotFoundException exception) {
                dispatchers.put(method, defaults || method.isAnnotationPresent(Defaults.class) ? Dispatcher.ForDefaultValue.of(method.getReturnType()) : new Dispatcher.ForUnresolvedMethod("Class not available on current VM: " + exception.getMessage()));
            }
            catch (NoSuchMethodException exception) {
                dispatchers.put(method, defaults || method.isAnnotationPresent(Defaults.class) ? Dispatcher.ForDefaultValue.of(method.getReturnType()) : new Dispatcher.ForUnresolvedMethod("Method not available on current VM: " + exception.getMessage()));
            }
            catch (Throwable throwable) {
                dispatchers.put(method, new Dispatcher.ForUnresolvedMethod("Unexpected error: " + throwable.getMessage()));
            }
        }
        if (generate) {
            return (T)DynamicClassLoader.proxy(this.proxy, dispatchers);
        }
        return (T)Proxy.newProxyInstance(this.proxy.getClassLoader(), new Class[]{this.proxy}, (InvocationHandler)new ProxiedInvocationHandler(target.getName(), dispatchers));
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
        GENERATE = Boolean.parseBoolean(JavaDispatcher.doPrivileged(new GetSystemPropertyAction(GENERATE_PROPERTY)));
        RESOLVER = JavaDispatcher.doPrivileged(DynamicClassLoader.Resolver.CreationAction.INSTANCE);
        INVOKER = JavaDispatcher.doPrivileged(new InvokerCreationAction());
    }

    public boolean equals(@MaybeNull Object object) {
        block12: {
            block11: {
                ClassLoader classLoader;
                block10: {
                    ClassLoader classLoader2;
                    if (this == object) {
                        return true;
                    }
                    if (object == null) {
                        return false;
                    }
                    if (this.getClass() != object.getClass()) {
                        return false;
                    }
                    if (this.generate != ((JavaDispatcher)object).generate) {
                        return false;
                    }
                    if (!this.proxy.equals(((JavaDispatcher)object).proxy)) {
                        return false;
                    }
                    ClassLoader classLoader3 = ((JavaDispatcher)object).classLoader;
                    classLoader = classLoader2 = this.classLoader;
                    if (classLoader3 == null) break block10;
                    if (classLoader == null) break block11;
                    if (!classLoader2.equals(classLoader3)) {
                        return false;
                    }
                    break block12;
                }
                if (classLoader == null) break block12;
            }
            return false;
        }
        return true;
    }

    public int hashCode() {
        int n = (this.getClass().hashCode() * 31 + this.proxy.hashCode()) * 31;
        ClassLoader classLoader = this.classLoader;
        if (classLoader != null) {
            n = n + classLoader.hashCode();
        }
        return n * 31 + this.generate;
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    protected static class DynamicClassLoader
    extends ClassLoader {
        @MaybeNull
        private static final String DUMP_FOLDER;
        private static final Class<?>[] NO_PARAMETER;
        private static final Object[] NO_ARGUMENT;

        protected DynamicClassLoader(Class<?> target) {
            super(target.getClassLoader());
            RESOLVER.accept(this, target);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @SuppressFBWarnings(value={"REC_CATCH_EXCEPTION", "DP_CREATE_CLASSLOADER_INSIDE_DO_PRIVILEGED"}, justification="Expected internal invocation.")
        protected static Object proxy(Class<?> proxy, Map<Method, Dispatcher> dispatchers) {
            ClassWriter classWriter = new ClassWriter(0);
            classWriter.visit(ClassFileVersion.ofThisVm(ClassFileVersion.JAVA_V5).getMinorMajorVersion(), 1, Type.getInternalName(proxy) + "$Proxy", null, Type.getInternalName(Object.class), new String[]{Type.getInternalName(proxy)});
            for (Map.Entry<Method, Dispatcher> entry : dispatchers.entrySet()) {
                Class<?>[] exceptionType = entry.getKey().getExceptionTypes();
                String[] exceptionTypeName = new String[exceptionType.length];
                for (int index = 0; index < exceptionType.length; ++index) {
                    exceptionTypeName[index] = Type.getInternalName(exceptionType[index]);
                }
                MethodVisitor methodVisitor = classWriter.visitMethod(1, entry.getKey().getName(), Type.getMethodDescriptor(entry.getKey()), null, exceptionTypeName);
                methodVisitor.visitCode();
                int offset = (entry.getKey().getModifiers() & 8) == 0 ? 1 : 0;
                for (Class<?> type : entry.getKey().getParameterTypes()) {
                    offset += Type.getType(type).getSize();
                }
                methodVisitor.visitMaxs(entry.getValue().apply(methodVisitor, entry.getKey()), offset);
                methodVisitor.visitEnd();
            }
            MethodVisitor methodVisitor = classWriter.visitMethod(1, "<init>", Type.getMethodDescriptor(Type.VOID_TYPE, new Type[0]), null, null);
            methodVisitor.visitCode();
            methodVisitor.visitVarInsn(25, 0);
            methodVisitor.visitMethodInsn(183, Type.getInternalName(Object.class), "<init>", Type.getMethodDescriptor(Type.VOID_TYPE, new Type[0]), false);
            methodVisitor.visitInsn(177);
            methodVisitor.visitMaxs(1, 1);
            methodVisitor.visitEnd();
            classWriter.visitEnd();
            byte[] binaryRepresentation = classWriter.toByteArray();
            if (DUMP_FOLDER != null) {
                try {
                    FileOutputStream outputStream = new FileOutputStream(new File(DUMP_FOLDER, proxy.getName() + "$Proxy.class"));
                    try {
                        ((OutputStream)outputStream).write(binaryRepresentation);
                        Object var14_16 = null;
                    }
                    catch (Throwable throwable) {
                        Object var14_17 = null;
                        ((OutputStream)outputStream).close();
                        throw throwable;
                    }
                    ((OutputStream)outputStream).close();
                    {
                    }
                }
                catch (Throwable outputStream) {
                    // empty catch block
                }
            }
            try {
                return new DynamicClassLoader(proxy).defineClass(proxy.getName() + "$Proxy", binaryRepresentation, 0, binaryRepresentation.length, JavaDispatcher.class.getProtectionDomain()).getConstructor(NO_PARAMETER).newInstance(NO_ARGUMENT);
            }
            catch (Exception exception) {
                throw new IllegalStateException("Failed to create proxy for " + proxy.getName(), exception);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @SuppressFBWarnings(value={"REC_CATCH_EXCEPTION", "DP_CREATE_CLASSLOADER_INSIDE_DO_PRIVILEGED"}, justification="Expected internal invocation.")
        protected static Invoker invoker() {
            byte[] binaryRepresentation;
            block11: {
                ClassWriter classWriter = new ClassWriter(0);
                classWriter.visit(ClassFileVersion.ofThisVm(ClassFileVersion.JAVA_V5).getMinorMajorVersion(), 1, Type.getInternalName(Invoker.class) + "$Dispatcher", null, Type.getInternalName(Object.class), new String[]{Type.getInternalName(Invoker.class)});
                for (Method method : GraalImageCode.getCurrent().sorted(Invoker.class.getMethods(), MethodComparator.INSTANCE)) {
                    Class<?>[] exceptionType = method.getExceptionTypes();
                    String[] exceptionTypeName = new String[exceptionType.length];
                    for (int index = 0; index < exceptionType.length; ++index) {
                        exceptionTypeName[index] = Type.getInternalName(exceptionType[index]);
                    }
                    MethodVisitor methodVisitor = classWriter.visitMethod(1, method.getName(), Type.getMethodDescriptor(method), null, exceptionTypeName);
                    methodVisitor.visitCode();
                    int offset = 1;
                    Type[] parameter = new Type[method.getParameterTypes().length - 1];
                    for (int index = 0; index < method.getParameterTypes().length; ++index) {
                        Type type = Type.getType(method.getParameterTypes()[index]);
                        if (index > 0) {
                            parameter[index - 1] = type;
                        }
                        methodVisitor.visitVarInsn(type.getOpcode(21), offset);
                        offset += type.getSize();
                    }
                    methodVisitor.visitMethodInsn(182, Type.getInternalName(method.getParameterTypes()[0]), method.getName(), Type.getMethodDescriptor(Type.getReturnType(method), parameter), false);
                    methodVisitor.visitInsn(Type.getReturnType(method).getOpcode(172));
                    methodVisitor.visitMaxs(Math.max(offset - 1, Type.getReturnType(method).getSize()), offset);
                    methodVisitor.visitEnd();
                }
                MethodVisitor methodVisitor = classWriter.visitMethod(1, "<init>", Type.getMethodDescriptor(Type.VOID_TYPE, new Type[0]), null, null);
                methodVisitor.visitCode();
                methodVisitor.visitVarInsn(25, 0);
                methodVisitor.visitMethodInsn(183, Type.getInternalName(Object.class), "<init>", Type.getMethodDescriptor(Type.VOID_TYPE, new Type[0]), false);
                methodVisitor.visitInsn(177);
                methodVisitor.visitMaxs(1, 1);
                methodVisitor.visitEnd();
                classWriter.visitEnd();
                binaryRepresentation = classWriter.toByteArray();
                try {
                    String dumpFolder = System.getProperty("net.bytebuddy.dump");
                    if (dumpFolder == null) break block11;
                    FileOutputStream outputStream = new FileOutputStream(new File(dumpFolder, Invoker.class.getName() + "$Dispatcher.class"));
                    try {
                        ((OutputStream)outputStream).write(binaryRepresentation);
                        Object var13_18 = null;
                    }
                    catch (Throwable throwable) {
                        Object var13_19 = null;
                        ((OutputStream)outputStream).close();
                        throw throwable;
                    }
                    ((OutputStream)outputStream).close();
                    {
                    }
                }
                catch (Throwable dumpFolder) {
                    // empty catch block
                }
            }
            try {
                return (Invoker)new DynamicClassLoader(Invoker.class).defineClass(Invoker.class.getName() + "$Dispatcher", binaryRepresentation, 0, binaryRepresentation.length, JavaDispatcher.class.getProtectionDomain()).getConstructor(NO_PARAMETER).newInstance(NO_ARGUMENT);
            }
            catch (UnsupportedOperationException ignored) {
                return new DirectInvoker();
            }
            catch (Exception exception) {
                throw new IllegalStateException("Failed to create invoker for " + Invoker.class.getName(), exception);
            }
        }

        static {
            String dumpFolder;
            NO_PARAMETER = new Class[0];
            NO_ARGUMENT = new Object[0];
            try {
                dumpFolder = (String)JavaDispatcher.doPrivileged(new GetSystemPropertyAction("net.bytebuddy.dump"));
            }
            catch (Throwable ignored) {
                dumpFolder = null;
            }
            DUMP_FOLDER = dumpFolder;
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        protected static interface Resolver {
            public void accept(@MaybeNull ClassLoader var1, Class<?> var2);

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            @HashCodeAndEqualsPlugin.Enhance
            public static class ForModuleSystem
            implements Resolver {
                private final Method getModule;
                private final Method isExported;
                private final Method addExports;
                private final Method getUnnamedModule;

                protected ForModuleSystem(Method getModule, Method isExported, Method addExports, Method getUnnamedModule) {
                    this.getModule = getModule;
                    this.isExported = isExported;
                    this.addExports = addExports;
                    this.getUnnamedModule = getUnnamedModule;
                }

                @Override
                @SuppressFBWarnings(value={"REC_CATCH_EXCEPTION"}, justification="Exception should always be wrapped for clarity.")
                public void accept(@MaybeNull ClassLoader classLoader, Class<?> target) {
                    Package location = target.getPackage();
                    if (location != null) {
                        try {
                            Object module = this.getModule.invoke(target, new Object[0]);
                            if (!((Boolean)this.isExported.invoke(module, location.getName())).booleanValue()) {
                                this.addExports.invoke(module, location.getName(), this.getUnnamedModule.invoke((Object)classLoader, new Object[0]));
                            }
                        }
                        catch (Exception exception) {
                            throw new IllegalStateException("Failed to adjust module graph for dispatcher", exception);
                        }
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
                    if (!this.getModule.equals(((ForModuleSystem)object).getModule)) {
                        return false;
                    }
                    if (!this.isExported.equals(((ForModuleSystem)object).isExported)) {
                        return false;
                    }
                    if (!this.addExports.equals(((ForModuleSystem)object).addExports)) {
                        return false;
                    }
                    return this.getUnnamedModule.equals(((ForModuleSystem)object).getUnnamedModule);
                }

                public int hashCode() {
                    return (((this.getClass().hashCode() * 31 + this.getModule.hashCode()) * 31 + this.isExported.hashCode()) * 31 + this.addExports.hashCode()) * 31 + this.getUnnamedModule.hashCode();
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            public static enum NoOp implements Resolver
            {
                INSTANCE;


                @Override
                public void accept(@MaybeNull ClassLoader classLoader, Class<?> target) {
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            public static enum CreationAction implements PrivilegedAction<Resolver>
            {
                INSTANCE;


                @Override
                @SuppressFBWarnings(value={"REC_CATCH_EXCEPTION"}, justification="Exception should not be rethrown but trigger a fallback.")
                public Resolver run() {
                    try {
                        Class<?> module = Class.forName("java.lang.Module", false, null);
                        return new ForModuleSystem(Class.class.getMethod("getModule", new Class[0]), module.getMethod("isExported", String.class), module.getMethod("addExports", String.class, module), ClassLoader.class.getMethod("getUnnamedModule", new Class[0]));
                    }
                    catch (Exception ignored) {
                        return NoOp.INSTANCE;
                    }
                }
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @HashCodeAndEqualsPlugin.Enhance
    protected static class ProxiedInvocationHandler
    implements InvocationHandler {
        private static final Object[] NO_ARGUMENTS = new Object[0];
        private final String name;
        private final Map<Method, Dispatcher> targets;

        protected ProxiedInvocationHandler(String name, Map<Method, Dispatcher> targets) {
            this.name = name;
            this.targets = targets;
        }

        @Override
        @MaybeNull
        public Object invoke(Object proxy, Method method, @MaybeNull Object[] argument) throws Throwable {
            if (method.getDeclaringClass() == Object.class) {
                if (method.getName().equals("hashCode")) {
                    return ((Object)this).hashCode();
                }
                if (method.getName().equals("equals")) {
                    return argument[0] != null && Proxy.isProxyClass(argument[0].getClass()) && Proxy.getInvocationHandler(argument[0]).equals(this);
                }
                if (method.getName().equals("toString")) {
                    return "Call proxy for " + this.name;
                }
                throw new IllegalStateException("Unexpected object method: " + method);
            }
            Dispatcher dispatcher = this.targets.get(method);
            try {
                try {
                    if (dispatcher == null) {
                        throw new IllegalStateException("No proxy target found for " + method);
                    }
                    return dispatcher.invoke(argument == null ? NO_ARGUMENTS : argument);
                }
                catch (InvocationTargetException exception) {
                    throw exception.getTargetException();
                }
            }
            catch (RuntimeException exception) {
                throw exception;
            }
            catch (Error error) {
                throw error;
            }
            catch (Throwable throwable) {
                for (Class<?> type : method.getExceptionTypes()) {
                    if (!type.isInstance(throwable)) continue;
                    throw throwable;
                }
                throw new IllegalStateException("Failed to invoke proxy for " + method, throwable);
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
            if (!this.name.equals(((ProxiedInvocationHandler)object).name)) {
                return false;
            }
            return ((Object)this.targets).equals(((ProxiedInvocationHandler)object).targets);
        }

        public int hashCode() {
            return (this.getClass().hashCode() * 31 + this.name.hashCode()) * 31 + ((Object)this.targets).hashCode();
        }
    }

    protected static interface Dispatcher {
        @MaybeNull
        public Object invoke(Object[] var1) throws Throwable;

        public int apply(MethodVisitor var1, Method var2);

        @HashCodeAndEqualsPlugin.Enhance
        public static class ForUnresolvedMethod
        implements Dispatcher {
            private final String message;

            protected ForUnresolvedMethod(String message) {
                this.message = message;
            }

            public Object invoke(Object[] argument) throws Throwable {
                throw new IllegalStateException("Could not invoke proxy: " + this.message);
            }

            public int apply(MethodVisitor methodVisitor, Method method) {
                methodVisitor.visitTypeInsn(187, Type.getInternalName(IllegalStateException.class));
                methodVisitor.visitInsn(89);
                methodVisitor.visitLdcInsn(this.message);
                methodVisitor.visitMethodInsn(183, Type.getInternalName(IllegalStateException.class), "<init>", Type.getMethodDescriptor(Type.VOID_TYPE, Type.getType(String.class)), false);
                methodVisitor.visitInsn(191);
                return 3;
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
                return this.message.equals(((ForUnresolvedMethod)object).message);
            }

            public int hashCode() {
                return this.getClass().hashCode() * 31 + this.message.hashCode();
            }
        }

        @HashCodeAndEqualsPlugin.Enhance
        public static class ForNonStaticMethod
        implements Dispatcher {
            private static final Object[] NO_ARGUMENTS = new Object[0];
            private final Method method;

            protected ForNonStaticMethod(Method method) {
                this.method = method;
            }

            public Object invoke(Object[] argument) throws Throwable {
                Object[] reduced;
                if (argument.length == 1) {
                    reduced = NO_ARGUMENTS;
                } else {
                    reduced = new Object[argument.length - 1];
                    System.arraycopy(argument, 1, reduced, 0, reduced.length);
                }
                return INVOKER.invoke(this.method, argument[0], reduced);
            }

            public int apply(MethodVisitor methodVisitor, Method method) {
                Class<?>[] source = method.getParameterTypes();
                Class<?>[] target = this.method.getParameterTypes();
                int offset = 1;
                for (int index = 0; index < source.length; ++index) {
                    Type type = Type.getType(source[index]);
                    methodVisitor.visitVarInsn(type.getOpcode(21), offset);
                    if (source[index] != (index == 0 ? this.method.getDeclaringClass() : target[index - 1])) {
                        methodVisitor.visitTypeInsn(192, Type.getInternalName(index == 0 ? this.method.getDeclaringClass() : target[index - 1]));
                    }
                    offset += type.getSize();
                }
                methodVisitor.visitMethodInsn(this.method.getDeclaringClass().isInterface() ? 185 : 182, Type.getInternalName(this.method.getDeclaringClass()), this.method.getName(), Type.getMethodDescriptor(this.method), this.method.getDeclaringClass().isInterface());
                methodVisitor.visitInsn(Type.getReturnType(this.method).getOpcode(172));
                return Math.max(offset - 1, Type.getReturnType(this.method).getSize());
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
                return this.method.equals(((ForNonStaticMethod)object).method);
            }

            public int hashCode() {
                return this.getClass().hashCode() * 31 + this.method.hashCode();
            }
        }

        @HashCodeAndEqualsPlugin.Enhance
        public static class ForStaticMethod
        implements Dispatcher {
            private final Method method;

            protected ForStaticMethod(Method method) {
                this.method = method;
            }

            @MaybeNull
            public Object invoke(Object[] argument) throws Throwable {
                return INVOKER.invoke(this.method, null, argument);
            }

            public int apply(MethodVisitor methodVisitor, Method method) {
                Class<?>[] source = method.getParameterTypes();
                Class<?>[] target = this.method.getParameterTypes();
                int offset = 1;
                for (int index = 0; index < source.length; ++index) {
                    Type type = Type.getType(source[index]);
                    methodVisitor.visitVarInsn(type.getOpcode(21), offset);
                    if (source[index] != target[index]) {
                        methodVisitor.visitTypeInsn(192, Type.getInternalName(target[index]));
                    }
                    offset += type.getSize();
                }
                methodVisitor.visitMethodInsn(184, Type.getInternalName(this.method.getDeclaringClass()), this.method.getName(), Type.getMethodDescriptor(this.method), this.method.getDeclaringClass().isInterface());
                methodVisitor.visitInsn(Type.getReturnType(this.method).getOpcode(172));
                return Math.max(offset - 1, Type.getReturnType(this.method).getSize());
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
                return this.method.equals(((ForStaticMethod)object).method);
            }

            public int hashCode() {
                return this.getClass().hashCode() * 31 + this.method.hashCode();
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        @HashCodeAndEqualsPlugin.Enhance
        public static class ForConstructor
        implements Dispatcher {
            private final Constructor<?> constructor;

            protected ForConstructor(Constructor<?> constructor) {
                this.constructor = constructor;
            }

            @Override
            public Object invoke(Object[] argument) throws Throwable {
                return INVOKER.newInstance(this.constructor, argument);
            }

            @Override
            public int apply(MethodVisitor methodVisitor, Method method) {
                Class<?>[] source = method.getParameterTypes();
                Class<?>[] target = this.constructor.getParameterTypes();
                methodVisitor.visitTypeInsn(187, Type.getInternalName(this.constructor.getDeclaringClass()));
                methodVisitor.visitInsn(89);
                int offset = 1;
                for (int index = 0; index < source.length; ++index) {
                    Type type = Type.getType(source[index]);
                    methodVisitor.visitVarInsn(type.getOpcode(21), offset);
                    if (source[index] != target[index]) {
                        methodVisitor.visitTypeInsn(192, Type.getInternalName(target[index]));
                    }
                    offset += type.getSize();
                }
                methodVisitor.visitMethodInsn(183, Type.getInternalName(this.constructor.getDeclaringClass()), "<init>", Type.getConstructorDescriptor(this.constructor), false);
                methodVisitor.visitInsn(176);
                return offset + 1;
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
                return this.constructor.equals(((ForConstructor)object).constructor);
            }

            public int hashCode() {
                return this.getClass().hashCode() * 31 + this.constructor.hashCode();
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static enum ForDefaultValue implements Dispatcher
        {
            VOID(null, 0, 177, 0),
            BOOLEAN(false, 3, 172, 1),
            BOOLEAN_REVERSE(true, 4, 172, 1),
            BYTE((byte)0, 3, 172, 1),
            SHORT((short)0, 3, 172, 1),
            CHARACTER(Character.valueOf('\u0000'), 3, 172, 1),
            INTEGER(0, 3, 172, 1),
            LONG(0L, 9, 173, 2),
            FLOAT(Float.valueOf(0.0f), 11, 174, 1),
            DOUBLE(0.0, 14, 175, 2),
            REFERENCE(null, 1, 176, 1);

            @MaybeNull
            private final Object value;
            private final int load;
            private final int returned;
            private final int size;

            private ForDefaultValue(Object value, int load, int returned, int size) {
                this.value = value;
                this.load = load;
                this.returned = returned;
                this.size = size;
            }

            protected static Dispatcher of(Class<?> type) {
                if (type == Void.TYPE) {
                    return VOID;
                }
                if (type == Boolean.TYPE) {
                    return BOOLEAN;
                }
                if (type == Byte.TYPE) {
                    return BYTE;
                }
                if (type == Short.TYPE) {
                    return SHORT;
                }
                if (type == Character.TYPE) {
                    return CHARACTER;
                }
                if (type == Integer.TYPE) {
                    return INTEGER;
                }
                if (type == Long.TYPE) {
                    return LONG;
                }
                if (type == Float.TYPE) {
                    return FLOAT;
                }
                if (type == Double.TYPE) {
                    return DOUBLE;
                }
                if (type.isArray()) {
                    if (type.getComponentType() == Boolean.TYPE) {
                        return OfPrimitiveArray.BOOLEAN;
                    }
                    if (type.getComponentType() == Byte.TYPE) {
                        return OfPrimitiveArray.BYTE;
                    }
                    if (type.getComponentType() == Short.TYPE) {
                        return OfPrimitiveArray.SHORT;
                    }
                    if (type.getComponentType() == Character.TYPE) {
                        return OfPrimitiveArray.CHARACTER;
                    }
                    if (type.getComponentType() == Integer.TYPE) {
                        return OfPrimitiveArray.INTEGER;
                    }
                    if (type.getComponentType() == Long.TYPE) {
                        return OfPrimitiveArray.LONG;
                    }
                    if (type.getComponentType() == Float.TYPE) {
                        return OfPrimitiveArray.FLOAT;
                    }
                    if (type.getComponentType() == Double.TYPE) {
                        return OfPrimitiveArray.DOUBLE;
                    }
                    return OfNonPrimitiveArray.of(type.getComponentType());
                }
                return REFERENCE;
            }

            @Override
            @MaybeNull
            public Object invoke(Object[] argument) {
                return this.value;
            }

            @Override
            public int apply(MethodVisitor methodVisitor, Method method) {
                if (this.load != 0) {
                    methodVisitor.visitInsn(this.load);
                }
                methodVisitor.visitInsn(this.returned);
                return this.size;
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            @HashCodeAndEqualsPlugin.Enhance
            protected static class OfNonPrimitiveArray
            implements Dispatcher {
                @HashCodeAndEqualsPlugin.ValueHandling(value=HashCodeAndEqualsPlugin.ValueHandling.Sort.IGNORE)
                private final Object value;
                private final Class<?> componentType;

                protected OfNonPrimitiveArray(Object value, Class<?> componentType) {
                    this.value = value;
                    this.componentType = componentType;
                }

                protected static Dispatcher of(Class<?> componentType) {
                    return new OfNonPrimitiveArray(Array.newInstance(componentType, 0), componentType);
                }

                @Override
                public Object invoke(Object[] argument) {
                    return this.value;
                }

                @Override
                public int apply(MethodVisitor methodVisitor, Method method) {
                    methodVisitor.visitInsn(3);
                    methodVisitor.visitTypeInsn(189, Type.getInternalName(this.componentType));
                    methodVisitor.visitInsn(176);
                    return 1;
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
                    return this.componentType.equals(((OfNonPrimitiveArray)object).componentType);
                }

                public int hashCode() {
                    return this.getClass().hashCode() * 31 + this.componentType.hashCode();
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            protected static enum OfPrimitiveArray implements Dispatcher
            {
                BOOLEAN(new boolean[0], 4),
                BYTE(new byte[0], 8),
                SHORT(new short[0], 9),
                CHARACTER(new char[0], 5),
                INTEGER(new int[0], 10),
                LONG(new long[0], 11),
                FLOAT(new float[0], 6),
                DOUBLE(new double[0], 7);

                private final Object value;
                private final int operand;

                private OfPrimitiveArray(Object value, int operand) {
                    this.value = value;
                    this.operand = operand;
                }

                @Override
                public Object invoke(Object[] argument) {
                    return this.value;
                }

                @Override
                public int apply(MethodVisitor methodVisitor, Method method) {
                    methodVisitor.visitInsn(3);
                    methodVisitor.visitIntInsn(188, this.operand);
                    methodVisitor.visitInsn(176);
                    return 1;
                }
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        @HashCodeAndEqualsPlugin.Enhance
        public static class ForContainerCreation
        implements Dispatcher {
            private final Class<?> target;

            protected ForContainerCreation(Class<?> target) {
                this.target = target;
            }

            @Override
            public Object invoke(Object[] argument) {
                return Array.newInstance(this.target, (int)((Integer)argument[0]));
            }

            @Override
            public int apply(MethodVisitor methodVisitor, Method method) {
                methodVisitor.visitVarInsn(21, 1);
                methodVisitor.visitTypeInsn(189, Type.getInternalName(this.target));
                methodVisitor.visitInsn(176);
                return 1;
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
                return this.target.equals(((ForContainerCreation)object).target);
            }

            public int hashCode() {
                return this.getClass().hashCode() * 31 + this.target.hashCode();
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        @HashCodeAndEqualsPlugin.Enhance
        public static class ForInstanceCheck
        implements Dispatcher {
            private final Class<?> target;

            protected ForInstanceCheck(Class<?> target) {
                this.target = target;
            }

            @Override
            public Object invoke(Object[] argument) {
                return this.target.isInstance(argument[0]);
            }

            @Override
            public int apply(MethodVisitor methodVisitor, Method method) {
                methodVisitor.visitVarInsn(25, 1);
                methodVisitor.visitTypeInsn(193, Type.getInternalName(this.target));
                methodVisitor.visitInsn(172);
                return 1;
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
                return this.target.equals(((ForInstanceCheck)object).target);
            }

            public int hashCode() {
                return this.getClass().hashCode() * 31 + this.target.hashCode();
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @HashCodeAndEqualsPlugin.Enhance
    private static class DirectInvoker
    implements Invoker {
        private DirectInvoker() {
        }

        @Override
        public Object newInstance(Constructor<?> constructor, Object[] argument) throws InstantiationException, IllegalAccessException, InvocationTargetException {
            return constructor.newInstance(argument);
        }

        @Override
        public Object invoke(Method method, @MaybeNull Object instance, @MaybeNull Object[] argument) throws IllegalAccessException, InvocationTargetException {
            return method.invoke(instance, argument);
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
    @HashCodeAndEqualsPlugin.Enhance
    private static class InvokerCreationAction
    implements PrivilegedAction<Invoker> {
        private InvokerCreationAction() {
        }

        @Override
        public Invoker run() {
            return DynamicClassLoader.invoker();
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

    @Documented
    @Target(value={ElementType.TYPE, ElementType.METHOD})
    @Retention(value=RetentionPolicy.RUNTIME)
    public static @interface Defaults {
    }

    @Documented
    @Target(value={ElementType.METHOD})
    @Retention(value=RetentionPolicy.RUNTIME)
    public static @interface Container {
    }

    @Documented
    @Target(value={ElementType.METHOD})
    @Retention(value=RetentionPolicy.RUNTIME)
    public static @interface Instance {
    }

    @Documented
    @Target(value={ElementType.METHOD})
    @Retention(value=RetentionPolicy.RUNTIME)
    public static @interface IsConstructor {
    }

    @Documented
    @Target(value={ElementType.METHOD})
    @Retention(value=RetentionPolicy.RUNTIME)
    public static @interface IsStatic {
    }

    @Documented
    @Target(value={ElementType.TYPE, ElementType.METHOD, ElementType.PARAMETER})
    @Retention(value=RetentionPolicy.RUNTIME)
    public static @interface Proxied {
        public String value();
    }
}

