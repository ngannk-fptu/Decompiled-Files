/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime;

import groovy.lang.Closure;
import groovy.lang.GeneratedGroovyProxy;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;
import groovy.lang.GroovyRuntimeException;
import groovy.transform.Trait;
import groovyjarjarasm.asm.ClassVisitor;
import groovyjarjarasm.asm.ClassWriter;
import groovyjarjarasm.asm.Label;
import groovyjarjarasm.asm.MethodVisitor;
import groovyjarjarasm.asm.Opcodes;
import groovyjarjarasm.asm.Type;
import java.lang.reflect.Constructor;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.classgen.asm.BytecodeHelper;
import org.codehaus.groovy.control.CompilationUnit;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.ErrorCollector;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;
import org.codehaus.groovy.tools.GroovyClass;
import org.codehaus.groovy.transform.trait.Traits;

public class ProxyGeneratorAdapter
extends ClassVisitor
implements Opcodes {
    private static final Map<String, Boolean> EMPTY_DELEGATECLOSURE_MAP = Collections.emptyMap();
    private static final Set<String> EMPTY_STRING_SET = Collections.emptySet();
    private static final String CLOSURES_MAP_FIELD = "$closures$delegate$map";
    private static final String DELEGATE_OBJECT_FIELD = "$delegate";
    private static List<Method> OBJECT_METHODS = ProxyGeneratorAdapter.getInheritedMethods(Object.class, new ArrayList<Method>());
    private static List<Method> GROOVYOBJECT_METHODS = ProxyGeneratorAdapter.getInheritedMethods(GroovyObject.class, new ArrayList<Method>());
    private static final AtomicLong pxyCounter = new AtomicLong();
    private static final Set<String> GROOVYOBJECT_METHOD_NAMESS;
    private static final Object[] EMPTY_ARGS;
    private final Class superClass;
    private final Class delegateClass;
    private final InnerLoader loader;
    private final String proxyName;
    private final LinkedHashSet<Class> classList;
    private final Map<String, Boolean> delegatedClosures;
    private final boolean emptyBody;
    private final boolean hasWildcard;
    private final boolean generateDelegateField;
    private final Set<String> objectDelegateMethods;
    private final Set<Object> visitedMethods;
    private final Class cachedClass;
    private final Constructor cachedNoArgConstructor;

    public ProxyGeneratorAdapter(Map<Object, Object> closureMap, Class superClass, Class[] interfaces, ClassLoader proxyLoader, boolean emptyBody, Class delegateClass) {
        super(262144, new ClassWriter(0));
        Constructor constructor;
        Class[] classArray;
        this.loader = proxyLoader != null ? ProxyGeneratorAdapter.createInnerLoader(proxyLoader) : this.findClassLoader(superClass);
        this.visitedMethods = new LinkedHashSet<Object>();
        this.delegatedClosures = closureMap.isEmpty() ? EMPTY_DELEGATECLOSURE_MAP : new HashMap();
        boolean wildcard = false;
        for (Map.Entry<Object, Object> entry : closureMap.entrySet()) {
            String name = entry.getKey().toString();
            if ("*".equals(name)) {
                wildcard = true;
            }
            this.delegatedClosures.put(name, Boolean.FALSE);
        }
        this.hasWildcard = wildcard;
        Class fixedSuperClass = this.adjustSuperClass(superClass, interfaces);
        this.generateDelegateField = delegateClass != null;
        this.objectDelegateMethods = this.generateDelegateField ? ProxyGeneratorAdapter.createDelegateMethodList(fixedSuperClass, delegateClass, interfaces) : EMPTY_STRING_SET;
        this.delegateClass = delegateClass;
        this.superClass = fixedSuperClass;
        this.classList = new LinkedHashSet();
        this.classList.add(superClass);
        if (this.generateDelegateField) {
            this.classList.add(delegateClass);
            Collections.addAll(this.classList, delegateClass.getInterfaces());
        }
        if (interfaces != null) {
            Collections.addAll(this.classList, interfaces);
        }
        this.proxyName = this.proxyName();
        this.emptyBody = emptyBody;
        ClassWriter writer = (ClassWriter)this.cv;
        this.visit(49, 1, this.proxyName, null, null, null);
        byte[] b = writer.toByteArray();
        this.cachedClass = this.loader.defineClass(this.proxyName.replace('/', '.'), b);
        if (this.generateDelegateField) {
            Class[] classArray2 = new Class[2];
            classArray2[0] = Map.class;
            classArray = classArray2;
            classArray2[1] = delegateClass;
        } else {
            Class[] classArray3 = new Class[1];
            classArray = classArray3;
            classArray3[0] = Map.class;
        }
        Class[] args = classArray;
        try {
            constructor = this.cachedClass.getConstructor(args);
        }
        catch (NoSuchMethodException e) {
            constructor = null;
        }
        this.cachedNoArgConstructor = constructor;
    }

    private Class adjustSuperClass(Class superClass, Class[] interfaces) {
        boolean isSuperClassAnInterface = superClass.isInterface();
        if (!isSuperClassAnInterface) {
            return superClass;
        }
        Class<Object> result = Object.class;
        LinkedHashSet<ClassNode> traits = new LinkedHashSet<ClassNode>();
        ProxyGeneratorAdapter.collectTraits(superClass, traits);
        if (interfaces != null) {
            for (Class anInterface : interfaces) {
                ProxyGeneratorAdapter.collectTraits(anInterface, traits);
            }
        }
        if (!traits.isEmpty()) {
            String name = superClass.getName() + "$TraitAdapter";
            ClassNode cn = new ClassNode(name, 1025, ClassHelper.OBJECT_TYPE, traits.toArray(new ClassNode[traits.size()]), null);
            CompilationUnit cu = new CompilationUnit(this.loader);
            CompilerConfiguration config = new CompilerConfiguration();
            SourceUnit su = new SourceUnit(name + "wrapper", "", config, (GroovyClassLoader)this.loader, new ErrorCollector(config));
            cu.addSource(su);
            cu.compile(3);
            su.getAST().addClass(cn);
            cu.compile(7);
            List classes = cu.getClasses();
            for (GroovyClass groovyClass : classes) {
                if (!groovyClass.getName().equals(name)) continue;
                return this.loader.defineClass(name, groovyClass.getBytes());
            }
        }
        return result;
    }

    private static void collectTraits(Class clazz, Set<ClassNode> traits) {
        Trait annotation = clazz.getAnnotation(Trait.class);
        if (annotation != null) {
            ClassNode trait = ClassHelper.make(clazz);
            traits.add(trait.getPlainNodeReference());
            LinkedHashSet<ClassNode> selfTypes = new LinkedHashSet<ClassNode>();
            Traits.collectSelfTypes(trait, selfTypes, true, true);
            for (ClassNode selfType : selfTypes) {
                if (!Traits.isTrait(selfType)) continue;
                traits.add(selfType.getPlainNodeReference());
            }
        }
    }

    private static InnerLoader createInnerLoader(final ClassLoader parent) {
        return AccessController.doPrivileged(new PrivilegedAction<InnerLoader>(){

            @Override
            public InnerLoader run() {
                return new InnerLoader(parent);
            }
        });
    }

    private InnerLoader findClassLoader(Class clazz) {
        ClassLoader cl = clazz.getClassLoader();
        if (cl == null) {
            cl = this.getClass().getClassLoader();
        }
        return ProxyGeneratorAdapter.createInnerLoader(cl);
    }

    private static Set<String> createDelegateMethodList(Class superClass, Class delegateClass, Class[] interfaces) {
        HashSet<String> selectedMethods = new HashSet<String>();
        ArrayList<Method> interfaceMethods = new ArrayList<Method>();
        ArrayList<Method> superClassMethods = new ArrayList<Method>();
        Collections.addAll(superClassMethods, superClass.getDeclaredMethods());
        if (interfaces != null) {
            for (Class thisInterface : interfaces) {
                ProxyGeneratorAdapter.getInheritedMethods(thisInterface, interfaceMethods);
            }
            for (Method method : interfaceMethods) {
                if (ProxyGeneratorAdapter.containsEquivalentMethod(superClassMethods, method)) continue;
                selectedMethods.add(method.getName() + Type.getMethodDescriptor(method));
            }
        }
        List<Method> additionalMethods = ProxyGeneratorAdapter.getInheritedMethods(delegateClass, new ArrayList<Method>());
        for (Method method : additionalMethods) {
            if (method.getName().indexOf(36) != -1 || ProxyGeneratorAdapter.containsEquivalentMethod(interfaceMethods, method) || ProxyGeneratorAdapter.containsEquivalentMethod(OBJECT_METHODS, method) || ProxyGeneratorAdapter.containsEquivalentMethod(GROOVYOBJECT_METHODS, method)) continue;
            selectedMethods.add(method.getName() + Type.getMethodDescriptor(method));
        }
        return selectedMethods;
    }

    private static List<Method> getInheritedMethods(Class baseClass, List<Method> methods) {
        Collections.addAll(methods, baseClass.getMethods());
        for (Class currentClass = baseClass; currentClass != null; currentClass = currentClass.getSuperclass()) {
            Method[] protectedMethods;
            for (Method method : protectedMethods = currentClass.getDeclaredMethods()) {
                if (method.getName().indexOf(36) != -1 || !Modifier.isProtected(method.getModifiers()) || ProxyGeneratorAdapter.containsEquivalentMethod(methods, method)) continue;
                methods.add(method);
            }
        }
        return methods;
    }

    private static boolean containsEquivalentMethod(Collection<Method> publicAndProtectedMethods, Method candidate) {
        for (Method method : publicAndProtectedMethods) {
            if (!candidate.getName().equals(method.getName()) || !candidate.getReturnType().equals(method.getReturnType()) || !ProxyGeneratorAdapter.hasMatchingParameterTypes(candidate, method)) continue;
            return true;
        }
        return false;
    }

    private static boolean hasMatchingParameterTypes(Method method, Method candidate) {
        Class<?>[] methodParamTypes;
        Class<?>[] candidateParamTypes = candidate.getParameterTypes();
        if (candidateParamTypes.length != (methodParamTypes = method.getParameterTypes()).length) {
            return false;
        }
        for (int i = 0; i < methodParamTypes.length; ++i) {
            if (candidateParamTypes[i].equals(methodParamTypes[i])) continue;
            return false;
        }
        return true;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        boolean addGroovyObjectSupport;
        LinkedHashSet<String> interfacesSet = new LinkedHashSet<String>();
        if (interfaces != null) {
            Collections.addAll(interfacesSet, interfaces);
        }
        for (Class extraInterface : this.classList) {
            if (!extraInterface.isInterface()) continue;
            interfacesSet.add(BytecodeHelper.getClassInternalName(extraInterface));
        }
        boolean bl = addGroovyObjectSupport = !GroovyObject.class.isAssignableFrom(this.superClass);
        if (addGroovyObjectSupport) {
            interfacesSet.add("groovy/lang/GroovyObject");
        }
        if (this.generateDelegateField) {
            this.classList.add(GeneratedGroovyProxy.class);
            interfacesSet.add("groovy/lang/GeneratedGroovyProxy");
        }
        super.visit(49, 1, this.proxyName, signature, BytecodeHelper.getClassInternalName(this.superClass), interfacesSet.toArray(new String[interfacesSet.size()]));
        this.visitMethod(1, "<init>", "()V", null, null);
        this.addDelegateFields();
        if (addGroovyObjectSupport) {
            this.createGroovyObjectSupport();
        }
        for (Class clazz : this.classList) {
            this.visitClass(clazz);
        }
    }

    private void visitClass(Class clazz) {
        Method[] methods;
        for (Method method : methods = clazz.getDeclaredMethods()) {
            Class<?>[] classArray = method.getExceptionTypes();
            String[] exceptions = new String[classArray.length];
            for (int i = 0; i < exceptions.length; ++i) {
                exceptions[i] = BytecodeHelper.getClassInternalName(classArray[i]);
            }
            this.visitMethod(method.getModifiers(), method.getName(), BytecodeHelper.getMethodDescriptor(method.getReturnType(), method.getParameterTypes()), null, exceptions);
        }
        Constructor<?>[] constructors = clazz.getDeclaredConstructors();
        for (Constructor<?> constructor : constructors) {
            Class<?>[] exceptionTypes = constructor.getExceptionTypes();
            String[] exceptions = new String[exceptionTypes.length];
            for (int i = 0; i < exceptions.length; ++i) {
                exceptions[i] = BytecodeHelper.getClassInternalName(exceptionTypes[i]);
            }
            this.visitMethod(constructor.getModifiers(), "<init>", BytecodeHelper.getMethodDescriptor(Void.TYPE, constructor.getParameterTypes()), null, exceptions);
        }
        for (GenericDeclaration genericDeclaration : clazz.getInterfaces()) {
            this.visitClass((Class)genericDeclaration);
        }
        Class superclass = clazz.getSuperclass();
        if (superclass != null) {
            this.visitClass(superclass);
        }
        for (Map.Entry<String, Boolean> entry : this.delegatedClosures.entrySet()) {
            String name;
            Boolean bl = entry.getValue();
            if (bl.booleanValue() || "*".equals(name = entry.getKey())) continue;
            this.visitMethod(1, name, "([Ljava/lang/Object;)Ljava/lang/Object;", null, null);
        }
    }

    private void createGroovyObjectSupport() {
        this.visitField(130, "metaClass", "Lgroovy/lang/MetaClass;", null, null);
        MethodVisitor mv = super.visitMethod(1, "getMetaClass", "()Lgroovy/lang/MetaClass;", null, null);
        mv.visitCode();
        Label l0 = new Label();
        mv.visitLabel(l0);
        mv.visitVarInsn(25, 0);
        mv.visitFieldInsn(180, this.proxyName, "metaClass", "Lgroovy/lang/MetaClass;");
        Label l1 = new Label();
        mv.visitJumpInsn(199, l1);
        Label l2 = new Label();
        mv.visitLabel(l2);
        mv.visitVarInsn(25, 0);
        mv.visitVarInsn(25, 0);
        mv.visitMethodInsn(182, "java/lang/Object", "getClass", "()Ljava/lang/Class;", false);
        mv.visitMethodInsn(184, "org/codehaus/groovy/runtime/InvokerHelper", "getMetaClass", "(Ljava/lang/Class;)Lgroovy/lang/MetaClass;", false);
        mv.visitFieldInsn(181, this.proxyName, "metaClass", "Lgroovy/lang/MetaClass;");
        mv.visitLabel(l1);
        mv.visitVarInsn(25, 0);
        mv.visitFieldInsn(180, this.proxyName, "metaClass", "Lgroovy/lang/MetaClass;");
        mv.visitInsn(176);
        mv.visitMaxs(2, 1);
        mv.visitEnd();
        mv = super.visitMethod(1, "getProperty", "(Ljava/lang/String;)Ljava/lang/Object;", null, null);
        mv.visitCode();
        mv.visitIntInsn(25, 0);
        mv.visitMethodInsn(185, "groovy/lang/GroovyObject", "getMetaClass", "()Lgroovy/lang/MetaClass;", true);
        mv.visitIntInsn(25, 0);
        mv.visitVarInsn(25, 1);
        mv.visitMethodInsn(185, "groovy/lang/MetaClass", "getProperty", "(Ljava/lang/Object;Ljava/lang/String;)Ljava/lang/Object;", true);
        mv.visitInsn(176);
        mv.visitMaxs(3, 2);
        mv.visitEnd();
        mv = super.visitMethod(1, "setProperty", "(Ljava/lang/String;Ljava/lang/Object;)V", null, null);
        mv.visitCode();
        l0 = new Label();
        mv.visitLabel(l0);
        mv.visitVarInsn(25, 0);
        mv.visitMethodInsn(182, this.proxyName, "getMetaClass", "()Lgroovy/lang/MetaClass;", false);
        mv.visitVarInsn(25, 0);
        mv.visitVarInsn(25, 1);
        mv.visitVarInsn(25, 2);
        mv.visitMethodInsn(185, "groovy/lang/MetaClass", "setProperty", "(Ljava/lang/Object;Ljava/lang/String;Ljava/lang/Object;)V", true);
        l1 = new Label();
        mv.visitLabel(l1);
        mv.visitInsn(177);
        l2 = new Label();
        mv.visitLabel(l2);
        mv.visitMaxs(4, 3);
        mv.visitEnd();
        mv = super.visitMethod(1, "invokeMethod", "(Ljava/lang/String;Ljava/lang/Object;)Ljava/lang/Object;", null, null);
        l0 = new Label();
        mv.visitLabel(l0);
        mv.visitVarInsn(25, 0);
        mv.visitMethodInsn(182, this.proxyName, "getMetaClass", "()Lgroovy/lang/MetaClass;", false);
        mv.visitVarInsn(25, 0);
        mv.visitVarInsn(25, 1);
        mv.visitVarInsn(25, 2);
        mv.visitMethodInsn(185, "groovy/lang/MetaClass", "invokeMethod", "(Ljava/lang/Object;Ljava/lang/String;Ljava/lang/Object;)Ljava/lang/Object;", true);
        mv.visitInsn(176);
        l1 = new Label();
        mv.visitLabel(l1);
        mv.visitMaxs(4, 3);
        mv.visitEnd();
        mv = super.visitMethod(1, "setMetaClass", "(Lgroovy/lang/MetaClass;)V", null, null);
        mv.visitCode();
        l0 = new Label();
        mv.visitLabel(l0);
        mv.visitVarInsn(25, 0);
        mv.visitVarInsn(25, 1);
        mv.visitFieldInsn(181, this.proxyName, "metaClass", "Lgroovy/lang/MetaClass;");
        l1 = new Label();
        mv.visitLabel(l1);
        mv.visitInsn(177);
        l2 = new Label();
        mv.visitLabel(l2);
        mv.visitMaxs(2, 2);
        mv.visitEnd();
    }

    private void addDelegateFields() {
        this.visitField(18, CLOSURES_MAP_FIELD, "Ljava/util/Map;", null, null);
        if (this.generateDelegateField) {
            this.visitField(18, DELEGATE_OBJECT_FIELD, BytecodeHelper.getTypeDescription(this.delegateClass), null, null);
        }
    }

    private String proxyName() {
        int index;
        String name;
        String string = name = this.delegateClass != null ? this.delegateClass.getName() : this.superClass.getName();
        if (name.startsWith("[") && name.endsWith(";")) {
            name = name.substring(1, name.length() - 1) + "_array";
        }
        if ((index = name.lastIndexOf(46)) == -1) {
            return name + pxyCounter.incrementAndGet() + "_groovyProxy";
        }
        return name.substring(index + 1, name.length()) + pxyCounter.incrementAndGet() + "_groovyProxy";
    }

    private static boolean isImplemented(Class clazz, String name, String desc) {
        Method[] methods;
        for (Method method : methods = clazz.getDeclaredMethods()) {
            if (!method.getName().equals(name) || !desc.equals(Type.getMethodDescriptor(method))) continue;
            return !Modifier.isAbstract(method.getModifiers());
        }
        Class parent = clazz.getSuperclass();
        return parent != null && ProxyGeneratorAdapter.isImplemented(parent, name, desc);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        List<String> key = Arrays.asList(name, desc);
        if (this.visitedMethods.contains(key)) {
            return null;
        }
        if (Modifier.isPrivate(access) || Modifier.isNative(access) || (access & 0x1000) != 0) {
            return null;
        }
        int accessFlags = access;
        this.visitedMethods.add(key);
        if ((this.objectDelegateMethods.contains(name + desc) || this.delegatedClosures.containsKey(name) || !"<init>".equals(name) && this.hasWildcard) && !Modifier.isStatic(access) && !Modifier.isFinal(access)) {
            if (!GROOVYOBJECT_METHOD_NAMESS.contains(name)) {
                if (Modifier.isAbstract(access)) {
                    accessFlags -= 1024;
                }
                if (this.delegatedClosures.containsKey(name) || !"<init>".equals(name) && this.hasWildcard) {
                    this.delegatedClosures.put(name, Boolean.TRUE);
                    return this.makeDelegateToClosureCall(name, desc, signature, exceptions, accessFlags);
                }
                if (this.generateDelegateField && this.objectDelegateMethods.contains(name + desc)) {
                    return this.makeDelegateCall(name, desc, signature, exceptions, accessFlags);
                }
                this.delegatedClosures.put(name, Boolean.TRUE);
                return this.makeDelegateToClosureCall(name, desc, signature, exceptions, accessFlags);
            }
        } else {
            if ("getProxyTarget".equals(name) && "()Ljava/lang/Object;".equals(desc)) {
                return this.createGetProxyTargetMethod(access, name, desc, signature, exceptions);
            }
            if ("<init>".equals(name) && (Modifier.isPublic(access) || Modifier.isProtected(access))) {
                return this.createConstructor(access, name, desc, signature, exceptions);
            }
            if (Modifier.isAbstract(access) && !GROOVYOBJECT_METHOD_NAMESS.contains(name)) {
                if (ProxyGeneratorAdapter.isImplemented(this.superClass, name, desc)) {
                    return null;
                }
                MethodVisitor mv = super.visitMethod(accessFlags -= 1024, name, desc, signature, exceptions);
                mv.visitCode();
                Type[] args = Type.getArgumentTypes(desc);
                if (this.emptyBody) {
                    Type returnType = Type.getReturnType(desc);
                    if (returnType == Type.VOID_TYPE) {
                        mv.visitInsn(177);
                    } else {
                        int loadIns = ProxyGeneratorAdapter.getLoadInsn(returnType);
                        switch (loadIns) {
                            case 21: {
                                mv.visitInsn(3);
                                break;
                            }
                            case 22: {
                                mv.visitInsn(9);
                                break;
                            }
                            case 23: {
                                mv.visitInsn(11);
                                break;
                            }
                            case 24: {
                                mv.visitInsn(14);
                                break;
                            }
                            default: {
                                mv.visitInsn(1);
                            }
                        }
                        mv.visitInsn(ProxyGeneratorAdapter.getReturnInsn(returnType));
                        mv.visitMaxs(2, ProxyGeneratorAdapter.registerLen(args) + 1);
                    }
                } else {
                    mv.visitTypeInsn(187, "java/lang/UnsupportedOperationException");
                    mv.visitInsn(89);
                    mv.visitMethodInsn(183, "java/lang/UnsupportedOperationException", "<init>", "()V", false);
                    mv.visitInsn(191);
                    mv.visitMaxs(2, ProxyGeneratorAdapter.registerLen(args) + 1);
                }
                mv.visitEnd();
            }
        }
        return null;
    }

    private MethodVisitor createGetProxyTargetMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(17, name, desc, signature, exceptions);
        mv.visitCode();
        mv.visitIntInsn(25, 0);
        mv.visitFieldInsn(180, this.proxyName, DELEGATE_OBJECT_FIELD, BytecodeHelper.getTypeDescription(this.delegateClass));
        mv.visitInsn(176);
        mv.visitMaxs(1, 1);
        mv.visitEnd();
        return null;
    }

    private static int registerLen(Type[] args) {
        int i = 0;
        for (Type arg : args) {
            i += ProxyGeneratorAdapter.registerLen(arg);
        }
        return i;
    }

    private static int registerLen(Type arg) {
        return arg == Type.DOUBLE_TYPE || arg == Type.LONG_TYPE ? 2 : 1;
    }

    private MethodVisitor createConstructor(int access, String name, String desc, String signature, String[] exceptions) {
        Type[] args = Type.getArgumentTypes(desc);
        StringBuilder newDesc = new StringBuilder("(");
        for (Type arg : args) {
            newDesc.append(arg.getDescriptor());
        }
        newDesc.append("Ljava/util/Map;");
        if (this.generateDelegateField) {
            newDesc.append(BytecodeHelper.getTypeDescription(this.delegateClass));
        }
        newDesc.append(")V");
        MethodVisitor mv = super.visitMethod(access, name, newDesc.toString(), signature, exceptions);
        mv.visitCode();
        this.initializeDelegateClosure(mv, args);
        if (this.generateDelegateField) {
            this.initializeDelegateObject(mv, args);
        }
        mv.visitVarInsn(25, 0);
        int idx = 1;
        for (Type arg : args) {
            if (ProxyGeneratorAdapter.isPrimitive(arg)) {
                mv.visitIntInsn(ProxyGeneratorAdapter.getLoadInsn(arg), idx);
            } else {
                mv.visitVarInsn(25, idx);
            }
            idx += ProxyGeneratorAdapter.registerLen(arg);
        }
        mv.visitMethodInsn(183, BytecodeHelper.getClassInternalName(this.superClass), "<init>", desc, false);
        mv.visitInsn(177);
        int max = idx + 1 + (this.generateDelegateField ? 1 : 0);
        mv.visitMaxs(max, max);
        mv.visitEnd();
        return null;
    }

    private void initializeDelegateClosure(MethodVisitor mv, Type[] args) {
        int idx = 1 + ProxyGeneratorAdapter.getTypeArgsRegisterLength(args);
        mv.visitIntInsn(25, 0);
        mv.visitIntInsn(25, idx);
        mv.visitFieldInsn(181, this.proxyName, CLOSURES_MAP_FIELD, "Ljava/util/Map;");
    }

    private void initializeDelegateObject(MethodVisitor mv, Type[] args) {
        int idx = 2 + ProxyGeneratorAdapter.getTypeArgsRegisterLength(args);
        mv.visitIntInsn(25, 0);
        mv.visitIntInsn(25, idx);
        mv.visitFieldInsn(181, this.proxyName, DELEGATE_OBJECT_FIELD, BytecodeHelper.getTypeDescription(this.delegateClass));
    }

    private static int getTypeArgsRegisterLength(Type[] args) {
        int length = 0;
        for (Type type : args) {
            length += ProxyGeneratorAdapter.registerLen(type);
        }
        return length;
    }

    protected MethodVisitor makeDelegateCall(String name, String desc, String signature, String[] exceptions, int accessFlags) {
        MethodVisitor mv = super.visitMethod(accessFlags, name, desc, signature, exceptions);
        mv.visitVarInsn(25, 0);
        mv.visitFieldInsn(180, this.proxyName, DELEGATE_OBJECT_FIELD, BytecodeHelper.getTypeDescription(this.delegateClass));
        mv.visitLdcInsn(name);
        Type[] args = Type.getArgumentTypes(desc);
        BytecodeHelper.pushConstant(mv, args.length);
        mv.visitTypeInsn(189, "java/lang/Object");
        int size = 6;
        int idx = 1;
        for (int i = 0; i < args.length; ++i) {
            Type arg = args[i];
            mv.visitInsn(89);
            BytecodeHelper.pushConstant(mv, i);
            if (ProxyGeneratorAdapter.isPrimitive(arg)) {
                mv.visitIntInsn(ProxyGeneratorAdapter.getLoadInsn(arg), idx);
                String wrappedType = ProxyGeneratorAdapter.getWrappedClassDescriptor(arg);
                mv.visitMethodInsn(184, wrappedType, "valueOf", "(" + arg.getDescriptor() + ")L" + wrappedType + ";", false);
            } else {
                mv.visitVarInsn(25, idx);
            }
            size = Math.max(size, 5 + ProxyGeneratorAdapter.registerLen(arg));
            idx += ProxyGeneratorAdapter.registerLen(arg);
            mv.visitInsn(83);
        }
        mv.visitMethodInsn(184, "org/codehaus/groovy/runtime/InvokerHelper", "invokeMethod", "(Ljava/lang/Object;Ljava/lang/String;Ljava/lang/Object;)Ljava/lang/Object;", false);
        ProxyGeneratorAdapter.unwrapResult(mv, desc);
        mv.visitMaxs(size, ProxyGeneratorAdapter.registerLen(args) + 1);
        return mv;
    }

    protected MethodVisitor makeDelegateToClosureCall(String name, String desc, String signature, String[] exceptions, int accessFlags) {
        MethodVisitor mv = super.visitMethod(accessFlags, name, desc, signature, exceptions);
        mv.visitCode();
        int stackSize = 0;
        Type[] args = Type.getArgumentTypes(desc);
        int arrayStore = args.length + 1;
        BytecodeHelper.pushConstant(mv, args.length);
        mv.visitTypeInsn(189, "java/lang/Object");
        stackSize = 1;
        int idx = 1;
        for (int i = 0; i < args.length; ++i) {
            Type arg = args[i];
            mv.visitInsn(89);
            BytecodeHelper.pushConstant(mv, i);
            stackSize = 3;
            if (ProxyGeneratorAdapter.isPrimitive(arg)) {
                mv.visitIntInsn(ProxyGeneratorAdapter.getLoadInsn(arg), idx);
                String wrappedType = ProxyGeneratorAdapter.getWrappedClassDescriptor(arg);
                mv.visitMethodInsn(184, wrappedType, "valueOf", "(" + arg.getDescriptor() + ")L" + wrappedType + ";", false);
            } else {
                mv.visitVarInsn(25, idx);
            }
            idx += ProxyGeneratorAdapter.registerLen(arg);
            stackSize = Math.max(4, 3 + ProxyGeneratorAdapter.registerLen(arg));
            mv.visitInsn(83);
        }
        mv.visitVarInsn(58, arrayStore);
        int arrayIndex = arrayStore++;
        mv.visitVarInsn(25, 0);
        mv.visitFieldInsn(180, this.proxyName, CLOSURES_MAP_FIELD, "Ljava/util/Map;");
        mv.visitLdcInsn(name);
        mv.visitMethodInsn(185, "java/util/Map", "get", "(Ljava/lang/Object;)Ljava/lang/Object;", true);
        mv.visitVarInsn(58, arrayStore);
        Label notNull = new Label();
        mv.visitIntInsn(25, arrayStore);
        mv.visitJumpInsn(199, notNull);
        mv.visitVarInsn(25, 0);
        mv.visitFieldInsn(180, this.proxyName, CLOSURES_MAP_FIELD, "Ljava/util/Map;");
        mv.visitLdcInsn("*");
        mv.visitMethodInsn(185, "java/util/Map", "get", "(Ljava/lang/Object;)Ljava/lang/Object;", true);
        mv.visitVarInsn(58, arrayStore);
        mv.visitLabel(notNull);
        mv.visitVarInsn(25, arrayStore);
        mv.visitMethodInsn(184, BytecodeHelper.getClassInternalName(this.getClass()), "ensureClosure", "(Ljava/lang/Object;)Lgroovy/lang/Closure;", false);
        mv.visitVarInsn(25, arrayIndex);
        mv.visitMethodInsn(182, "groovy/lang/Closure", "call", "([Ljava/lang/Object;)Ljava/lang/Object;", false);
        ProxyGeneratorAdapter.unwrapResult(mv, desc);
        mv.visitMaxs(++stackSize, arrayStore + 1);
        mv.visitEnd();
        return null;
    }

    private static void unwrapResult(MethodVisitor mv, String desc) {
        Type returnType = Type.getReturnType(desc);
        if (returnType == Type.VOID_TYPE) {
            mv.visitInsn(87);
            mv.visitInsn(177);
        } else {
            if (ProxyGeneratorAdapter.isPrimitive(returnType)) {
                BytecodeHelper.unbox(mv, ClassHelper.make(returnType.getClassName()));
            } else {
                mv.visitTypeInsn(192, returnType.getInternalName());
            }
            mv.visitInsn(ProxyGeneratorAdapter.getReturnInsn(returnType));
        }
    }

    public GroovyObject proxy(Map<Object, Object> map, Object ... constructorArgs) {
        if (constructorArgs == null && this.cachedNoArgConstructor != null) {
            try {
                return (GroovyObject)this.cachedNoArgConstructor.newInstance(map);
            }
            catch (InstantiationException e) {
                throw new GroovyRuntimeException(e);
            }
            catch (IllegalAccessException e) {
                throw new GroovyRuntimeException(e);
            }
            catch (InvocationTargetException e) {
                throw new GroovyRuntimeException(e);
            }
        }
        if (constructorArgs == null) {
            constructorArgs = EMPTY_ARGS;
        }
        Object[] values = new Object[constructorArgs.length + 1];
        System.arraycopy(constructorArgs, 0, values, 0, constructorArgs.length);
        values[values.length - 1] = map;
        return (GroovyObject)DefaultGroovyMethods.newInstance(this.cachedClass, values);
    }

    public GroovyObject delegatingProxy(Object delegate, Map<Object, Object> map, Object ... constructorArgs) {
        if (constructorArgs == null && this.cachedNoArgConstructor != null) {
            try {
                return (GroovyObject)this.cachedNoArgConstructor.newInstance(map, delegate);
            }
            catch (InstantiationException e) {
                throw new GroovyRuntimeException(e);
            }
            catch (IllegalAccessException e) {
                throw new GroovyRuntimeException(e);
            }
            catch (InvocationTargetException e) {
                throw new GroovyRuntimeException(e);
            }
        }
        if (constructorArgs == null) {
            constructorArgs = EMPTY_ARGS;
        }
        Object[] values = new Object[constructorArgs.length + 2];
        System.arraycopy(constructorArgs, 0, values, 0, constructorArgs.length);
        values[values.length - 2] = map;
        values[values.length - 1] = delegate;
        return (GroovyObject)DefaultGroovyMethods.newInstance(this.cachedClass, values);
    }

    public static Closure ensureClosure(Object o) {
        if (o == null) {
            throw new UnsupportedOperationException();
        }
        if (o instanceof Closure) {
            return (Closure)o;
        }
        return new ReturnValueWrappingClosure<Object>(o);
    }

    private static int getLoadInsn(Type type) {
        if (type == Type.BOOLEAN_TYPE) {
            return 21;
        }
        if (type == Type.BYTE_TYPE) {
            return 21;
        }
        if (type == Type.CHAR_TYPE) {
            return 21;
        }
        if (type == Type.DOUBLE_TYPE) {
            return 24;
        }
        if (type == Type.FLOAT_TYPE) {
            return 23;
        }
        if (type == Type.INT_TYPE) {
            return 21;
        }
        if (type == Type.LONG_TYPE) {
            return 22;
        }
        if (type == Type.SHORT_TYPE) {
            return 21;
        }
        return 25;
    }

    private static int getReturnInsn(Type type) {
        if (type == Type.BOOLEAN_TYPE) {
            return 172;
        }
        if (type == Type.BYTE_TYPE) {
            return 172;
        }
        if (type == Type.CHAR_TYPE) {
            return 172;
        }
        if (type == Type.DOUBLE_TYPE) {
            return 175;
        }
        if (type == Type.FLOAT_TYPE) {
            return 174;
        }
        if (type == Type.INT_TYPE) {
            return 172;
        }
        if (type == Type.LONG_TYPE) {
            return 173;
        }
        if (type == Type.SHORT_TYPE) {
            return 172;
        }
        return 176;
    }

    private static boolean isPrimitive(Type arg) {
        return arg == Type.BOOLEAN_TYPE || arg == Type.BYTE_TYPE || arg == Type.CHAR_TYPE || arg == Type.DOUBLE_TYPE || arg == Type.FLOAT_TYPE || arg == Type.INT_TYPE || arg == Type.LONG_TYPE || arg == Type.SHORT_TYPE;
    }

    private static String getWrappedClassDescriptor(Type type) {
        if (type == Type.BOOLEAN_TYPE) {
            return "java/lang/Boolean";
        }
        if (type == Type.BYTE_TYPE) {
            return "java/lang/Byte";
        }
        if (type == Type.CHAR_TYPE) {
            return "java/lang/Character";
        }
        if (type == Type.DOUBLE_TYPE) {
            return "java/lang/Double";
        }
        if (type == Type.FLOAT_TYPE) {
            return "java/lang/Float";
        }
        if (type == Type.INT_TYPE) {
            return "java/lang/Integer";
        }
        if (type == Type.LONG_TYPE) {
            return "java/lang/Long";
        }
        if (type == Type.SHORT_TYPE) {
            return "java/lang/Short";
        }
        throw new IllegalArgumentException("Unexpected type class [" + type + "]");
    }

    static {
        EMPTY_ARGS = new Object[0];
        ArrayList<String> names = new ArrayList<String>();
        for (Method method : GroovyObject.class.getMethods()) {
            names.add(method.getName());
        }
        GROOVYOBJECT_METHOD_NAMESS = new HashSet<String>(names);
    }

    private static class ReturnValueWrappingClosure<V>
    extends Closure<V> {
        private final V value;

        public ReturnValueWrappingClosure(V returnValue) {
            super(null);
            this.value = returnValue;
        }

        @Override
        public V call(Object ... args) {
            return this.value;
        }
    }

    private static class InnerLoader
    extends GroovyClassLoader {
        protected InnerLoader(ClassLoader parent) {
            super(parent);
        }

        @Override
        public Class defineClass(String name, byte[] data) {
            return super.defineClass(name, data, 0, data.length);
        }
    }
}

