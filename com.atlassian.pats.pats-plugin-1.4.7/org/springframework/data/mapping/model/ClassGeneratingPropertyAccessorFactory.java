/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.asm.ClassWriter
 *  org.springframework.asm.Label
 *  org.springframework.asm.MethodVisitor
 *  org.springframework.asm.Type
 *  org.springframework.cglib.core.ReflectUtils
 *  org.springframework.core.KotlinDetector
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ClassUtils
 *  org.springframework.util.ReflectionUtils
 */
package org.springframework.data.mapping.model;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.asm.ClassWriter;
import org.springframework.asm.Label;
import org.springframework.asm.MethodVisitor;
import org.springframework.asm.Type;
import org.springframework.cglib.core.ReflectUtils;
import org.springframework.core.KotlinDetector;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.data.mapping.PersistentPropertyAccessor;
import org.springframework.data.mapping.model.BytecodeUtil;
import org.springframework.data.mapping.model.KotlinCopyMethod;
import org.springframework.data.mapping.model.PersistentPropertyAccessorFactory;
import org.springframework.data.util.Optionals;
import org.springframework.data.util.TypeInformation;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

public class ClassGeneratingPropertyAccessorFactory
implements PersistentPropertyAccessorFactory {
    private final ThreadLocal<Object[]> argumentCache = ThreadLocal.withInitial(() -> new Object[1]);
    private volatile Map<PersistentEntity<?, ?>, Constructor<?>> constructorMap = new HashMap(32);
    private volatile Map<TypeInformation<?>, Class<PersistentPropertyAccessor<?>>> propertyAccessorClasses = new HashMap(32);

    @Override
    public <T> PersistentPropertyAccessor<T> getPropertyAccessor(PersistentEntity<?, ?> entity, T bean) {
        Object constructorMap;
        Constructor<?> constructor = this.constructorMap.get(entity);
        if (constructor == null) {
            Class<PersistentPropertyAccessor<?>> accessorClass = this.potentiallyCreateAndRegisterPersistentPropertyAccessorClass(entity);
            constructor = accessorClass.getConstructors()[0];
            constructorMap = new HashMap(this.constructorMap);
            constructorMap.put(entity, constructor);
            this.constructorMap = constructorMap;
        }
        Object[] args = this.argumentCache.get();
        args[0] = bean;
        try {
            constructorMap = (PersistentPropertyAccessor)constructor.newInstance(args);
            return constructorMap;
        }
        catch (Exception e) {
            throw new IllegalArgumentException(String.format("Cannot create persistent property accessor for %s", entity), e);
        }
        finally {
            args[0] = null;
        }
    }

    @Override
    public boolean isSupported(PersistentEntity<?, ?> entity) {
        Assert.notNull(entity, (String)"PersistentEntity must not be null!");
        return ClassGeneratingPropertyAccessorFactory.isClassLoaderDefineClassAvailable(entity) && ClassGeneratingPropertyAccessorFactory.isTypeInjectable(entity) && this.hasUniquePropertyHashCodes(entity);
    }

    private static boolean isClassLoaderDefineClassAvailable(PersistentEntity<?, ?> entity) {
        try {
            return ReflectionUtils.findMethod(entity.getType().getClassLoader().getClass(), (String)"defineClass", (Class[])new Class[]{String.class, byte[].class, Integer.TYPE, Integer.TYPE, ProtectionDomain.class}) != null;
        }
        catch (Exception e) {
            return false;
        }
    }

    private static boolean isTypeInjectable(PersistentEntity<?, ?> entity) {
        Class<?> type = entity.getType();
        return type.getClassLoader() != null && (type.getPackage() == null || !type.getPackage().getName().startsWith("java")) && ClassUtils.isPresent((String)PersistentPropertyAccessor.class.getName(), (ClassLoader)type.getClassLoader()) && ClassUtils.isPresent((String)Assert.class.getName(), (ClassLoader)type.getClassLoader());
    }

    private boolean hasUniquePropertyHashCodes(PersistentEntity<?, ?> entity) {
        HashSet hashCodes = new HashSet();
        AtomicInteger propertyCount = new AtomicInteger();
        entity.doWithProperties(property -> {
            hashCodes.add(property.getName().hashCode());
            propertyCount.incrementAndGet();
        });
        entity.doWithAssociations(association -> {
            if (association.getInverse() != null) {
                hashCodes.add(association.getInverse().getName().hashCode());
                propertyCount.incrementAndGet();
            }
        });
        return hashCodes.size() == propertyCount.get();
    }

    private synchronized Class<PersistentPropertyAccessor<?>> potentiallyCreateAndRegisterPersistentPropertyAccessorClass(PersistentEntity<?, ?> entity) {
        Map<TypeInformation<?>, Class<PersistentPropertyAccessor<?>>> map = this.propertyAccessorClasses;
        Class<PersistentPropertyAccessor<?>> propertyAccessorClass = map.get(entity.getTypeInformation());
        if (propertyAccessorClass != null) {
            return propertyAccessorClass;
        }
        propertyAccessorClass = this.createAccessorClass(entity);
        map = new HashMap(map);
        map.put(entity.getTypeInformation(), propertyAccessorClass);
        this.propertyAccessorClasses = map;
        return propertyAccessorClass;
    }

    private Class<PersistentPropertyAccessor<?>> createAccessorClass(PersistentEntity<?, ?> entity) {
        try {
            return PropertyAccessorClassGenerator.generateCustomAccessorClass(entity);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void visitInvokeMethodSingleArg(MethodVisitor mv, Method method) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        Class<?> parameterType = parameterTypes[0];
        Class<?> declaringClass = method.getDeclaringClass();
        boolean interfaceDefinition = declaringClass.isInterface();
        mv.visitTypeInsn(192, Type.getInternalName(BytecodeUtil.autoboxType(parameterType)));
        BytecodeUtil.autoboxIfNeeded(BytecodeUtil.autoboxType(parameterType), parameterType, mv);
        int invokeOpCode = ClassGeneratingPropertyAccessorFactory.getInvokeOp(method, interfaceDefinition);
        mv.visitMethodInsn(invokeOpCode, Type.getInternalName(method.getDeclaringClass()), method.getName(), String.format("(%s)%s", BytecodeUtil.signatureTypeName(parameterType), BytecodeUtil.signatureTypeName(method.getReturnType())), interfaceDefinition);
    }

    private static int getInvokeOp(Method method, boolean interfaceDefinition) {
        int invokeOpCode;
        int n = invokeOpCode = Modifier.isStatic(method.getModifiers()) ? 184 : 182;
        if (interfaceDefinition) {
            invokeOpCode = 185;
        }
        return invokeOpCode;
    }

    private static Map<String, PropertyStackAddress> createPropertyStackMap(List<PersistentProperty<?>> persistentProperties) {
        HashMap<String, PropertyStackAddress> stackmap = new HashMap<String, PropertyStackAddress>();
        for (PersistentProperty<?> property : persistentProperties) {
            stackmap.put(property.getName(), new PropertyStackAddress(new Label(), property.getName().hashCode()));
        }
        return stackmap;
    }

    static boolean supportsMutation(PersistentProperty<?> property) {
        if (property.isImmutable()) {
            if (property.getWither() != null) {
                return true;
            }
            if (ClassGeneratingPropertyAccessorFactory.hasKotlinCopyMethod(property)) {
                return true;
            }
        }
        return property.usePropertyAccess() && property.getSetter() != null || property.getField() != null && !Modifier.isFinal(property.getField().getModifiers());
    }

    private static boolean hasKotlinCopyMethod(PersistentProperty<?> property) {
        Class<?> type = property.getOwner().getType();
        if (BytecodeUtil.isAccessible(type) && KotlinDetector.isKotlinType(type)) {
            return KotlinCopyMethod.findCopyMethod(type).filter(it -> it.supportsProperty(property)).isPresent();
        }
        return false;
    }

    static class PropertyStackAddress
    implements Comparable<PropertyStackAddress> {
        private final Label label;
        private final int hash;

        public PropertyStackAddress(Label label, int hash) {
            this.label = label;
            this.hash = hash;
        }

        @Override
        public int compareTo(PropertyStackAddress o) {
            return Integer.compare(this.hash, o.hash);
        }
    }

    static class PropertyAccessorClassGenerator {
        private static final String INIT = "<init>";
        private static final String CLINIT = "<clinit>";
        private static final String TAG = "_Accessor_";
        private static final String JAVA_LANG_OBJECT = "java/lang/Object";
        private static final String JAVA_LANG_STRING = "java/lang/String";
        private static final String JAVA_LANG_REFLECT_METHOD = "java/lang/reflect/Method";
        private static final String JAVA_LANG_INVOKE_METHOD_HANDLE = "java/lang/invoke/MethodHandle";
        private static final String JAVA_LANG_CLASS = "java/lang/Class";
        private static final String BEAN_FIELD = "bean";
        private static final String THIS_REF = "this";
        private static final String PERSISTENT_PROPERTY = "org/springframework/data/mapping/PersistentProperty";
        private static final String SET_ACCESSIBLE = "setAccessible";
        private static final String JAVA_LANG_REFLECT_FIELD = "java/lang/reflect/Field";
        private static final String JAVA_LANG_INVOKE_METHOD_HANDLES = "java/lang/invoke/MethodHandles";
        private static final String JAVA_LANG_INVOKE_METHOD_HANDLES_LOOKUP = "java/lang/invoke/MethodHandles$Lookup";
        private static final String JAVA_LANG_UNSUPPORTED_OPERATION_EXCEPTION = "java/lang/UnsupportedOperationException";
        private static final String[] IMPLEMENTED_INTERFACES = new String[]{Type.getInternalName(PersistentPropertyAccessor.class)};

        PropertyAccessorClassGenerator() {
        }

        static Class<?> generateCustomAccessorClass(PersistentEntity<?, ?> entity) {
            Class<?> type;
            ClassLoader classLoader;
            String className = PropertyAccessorClassGenerator.generateClassName(entity);
            if (ClassUtils.isPresent((String)className, (ClassLoader)(classLoader = (type = entity.getType()).getClassLoader()))) {
                try {
                    return ClassUtils.forName((String)className, (ClassLoader)classLoader);
                }
                catch (Exception o_O) {
                    throw new IllegalStateException(o_O);
                }
            }
            byte[] bytecode = PropertyAccessorClassGenerator.generateBytecode(className.replace('.', '/'), entity);
            try {
                return ReflectUtils.defineClass((String)className, (byte[])bytecode, (ClassLoader)classLoader, (ProtectionDomain)type.getProtectionDomain(), type);
            }
            catch (Exception o_O) {
                throw new IllegalStateException(o_O);
            }
        }

        static byte[] generateBytecode(String internalClassName, PersistentEntity<?, ?> entity) {
            ClassWriter cw = new ClassWriter(1);
            cw.visit(50, 33, internalClassName, null, JAVA_LANG_OBJECT, IMPLEMENTED_INTERFACES);
            List<PersistentProperty<?>> persistentProperties = PropertyAccessorClassGenerator.getPersistentProperties(entity);
            PropertyAccessorClassGenerator.visitFields(entity, persistentProperties, cw);
            PropertyAccessorClassGenerator.visitDefaultConstructor(entity, internalClassName, cw);
            PropertyAccessorClassGenerator.visitStaticInitializer(entity, persistentProperties, internalClassName, cw);
            PropertyAccessorClassGenerator.visitBeanGetter(entity, internalClassName, cw);
            PropertyAccessorClassGenerator.visitSetProperty(entity, persistentProperties, internalClassName, cw);
            PropertyAccessorClassGenerator.visitGetProperty(entity, persistentProperties, internalClassName, cw);
            cw.visitEnd();
            return cw.toByteArray();
        }

        private static List<PersistentProperty<?>> getPersistentProperties(PersistentEntity<?, ?> entity) {
            ArrayList persistentProperties = new ArrayList();
            entity.doWithAssociations(association -> {
                if (association.getInverse() != null) {
                    persistentProperties.add((PersistentProperty<?>)association.getInverse());
                }
            });
            entity.doWithProperties(property -> persistentProperties.add(property));
            return persistentProperties;
        }

        private static void visitFields(PersistentEntity<?, ?> entity, List<PersistentProperty<?>> persistentProperties, ClassWriter cw) {
            cw.visitInnerClass(JAVA_LANG_INVOKE_METHOD_HANDLES_LOOKUP, JAVA_LANG_INVOKE_METHOD_HANDLES, "Lookup", 26);
            cw.visitField(2, BEAN_FIELD, PropertyAccessorClassGenerator.getAccessibleTypeReferenceName(entity), null, null).visitEnd();
            for (PersistentProperty<?> property : persistentProperties) {
                if (property.isImmutable()) {
                    if (PropertyAccessorClassGenerator.generateMethodHandle(entity, property.getWither())) {
                        cw.visitField(26, PropertyAccessorClassGenerator.witherName(property), BytecodeUtil.referenceName(JAVA_LANG_INVOKE_METHOD_HANDLE), null, null).visitEnd();
                    }
                } else if (PropertyAccessorClassGenerator.generateMethodHandle(entity, property.getSetter())) {
                    cw.visitField(26, PropertyAccessorClassGenerator.setterName(property), BytecodeUtil.referenceName(JAVA_LANG_INVOKE_METHOD_HANDLE), null, null).visitEnd();
                }
                if (PropertyAccessorClassGenerator.generateMethodHandle(entity, property.getGetter())) {
                    cw.visitField(26, PropertyAccessorClassGenerator.getterName(property), BytecodeUtil.referenceName(JAVA_LANG_INVOKE_METHOD_HANDLE), null, null).visitEnd();
                }
                if (!PropertyAccessorClassGenerator.generateSetterMethodHandle(entity, property.getField())) continue;
                if (!property.isImmutable()) {
                    cw.visitField(26, PropertyAccessorClassGenerator.fieldSetterName(property), BytecodeUtil.referenceName(JAVA_LANG_INVOKE_METHOD_HANDLE), null, null).visitEnd();
                }
                cw.visitField(26, PropertyAccessorClassGenerator.fieldGetterName(property), BytecodeUtil.referenceName(JAVA_LANG_INVOKE_METHOD_HANDLE), null, null).visitEnd();
            }
        }

        private static void visitDefaultConstructor(PersistentEntity<?, ?> entity, String internalClassName, ClassWriter cw) {
            MethodVisitor mv = cw.visitMethod(1, INIT, String.format("(%s)V", PropertyAccessorClassGenerator.getAccessibleTypeReferenceName(entity)), null, null);
            mv.visitCode();
            Label l0 = new Label();
            mv.visitLabel(l0);
            mv.visitVarInsn(25, 0);
            mv.visitMethodInsn(183, JAVA_LANG_OBJECT, INIT, "()V", false);
            mv.visitVarInsn(25, 1);
            mv.visitLdcInsn((Object)"Bean must not be null!");
            mv.visitMethodInsn(184, "org/springframework/util/Assert", "notNull", String.format("(%s%s)V", BytecodeUtil.referenceName(JAVA_LANG_OBJECT), BytecodeUtil.referenceName(JAVA_LANG_STRING)), false);
            mv.visitVarInsn(25, 0);
            mv.visitVarInsn(25, 1);
            mv.visitFieldInsn(181, internalClassName, BEAN_FIELD, PropertyAccessorClassGenerator.getAccessibleTypeReferenceName(entity));
            mv.visitInsn(177);
            Label l3 = new Label();
            mv.visitLabel(l3);
            mv.visitLocalVariable(THIS_REF, BytecodeUtil.referenceName(internalClassName), null, l0, l3, 0);
            mv.visitLocalVariable(BEAN_FIELD, PropertyAccessorClassGenerator.getAccessibleTypeReferenceName(entity), null, l0, l3, 1);
            mv.visitMaxs(2, 2);
        }

        private static void visitStaticInitializer(PersistentEntity<?, ?> entity, List<PersistentProperty<?>> persistentProperties, String internalClassName, ClassWriter cw) {
            MethodVisitor mv = cw.visitMethod(8, CLINIT, "()V", null, null);
            mv.visitCode();
            Label l0 = new Label();
            Label l1 = new Label();
            mv.visitLabel(l0);
            mv.visitMethodInsn(184, JAVA_LANG_INVOKE_METHOD_HANDLES, "lookup", String.format("()%s", BytecodeUtil.referenceName(JAVA_LANG_INVOKE_METHOD_HANDLES_LOOKUP)), false);
            mv.visitVarInsn(58, 0);
            List<Class<?>> entityClasses = PropertyAccessorClassGenerator.getPropertyDeclaratingClasses(persistentProperties);
            for (Class<?> clazz : entityClasses) {
                mv.visitLdcInsn((Object)clazz.getName());
                mv.visitMethodInsn(184, JAVA_LANG_CLASS, "forName", String.format("(%s)%s", BytecodeUtil.referenceName(JAVA_LANG_STRING), BytecodeUtil.referenceName(JAVA_LANG_CLASS)), false);
                mv.visitVarInsn(58, PropertyAccessorClassGenerator.classVariableIndex5(entityClasses, clazz));
            }
            for (PersistentProperty persistentProperty : persistentProperties) {
                if (persistentProperty.usePropertyAccess()) {
                    if (PropertyAccessorClassGenerator.generateMethodHandle(entity, persistentProperty.getGetter())) {
                        PropertyAccessorClassGenerator.visitPropertyGetterInitializer(persistentProperty, mv, entityClasses, internalClassName);
                    }
                    if (PropertyAccessorClassGenerator.generateMethodHandle(entity, persistentProperty.getSetter())) {
                        PropertyAccessorClassGenerator.visitPropertySetterInitializer(persistentProperty.getSetter(), persistentProperty, mv, entityClasses, internalClassName, PropertyAccessorClassGenerator::setterName, 2);
                    }
                }
                if (persistentProperty.isImmutable() && PropertyAccessorClassGenerator.generateMethodHandle(entity, persistentProperty.getWither())) {
                    PropertyAccessorClassGenerator.visitPropertySetterInitializer(persistentProperty.getWither(), persistentProperty, mv, entityClasses, internalClassName, PropertyAccessorClassGenerator::witherName, 4);
                }
                if (!PropertyAccessorClassGenerator.generateSetterMethodHandle(entity, persistentProperty.getField())) continue;
                PropertyAccessorClassGenerator.visitFieldGetterSetterInitializer(persistentProperty, mv, entityClasses, internalClassName);
            }
            mv.visitLabel(l1);
            mv.visitInsn(177);
            mv.visitLocalVariable("lookup", BytecodeUtil.referenceName(JAVA_LANG_INVOKE_METHOD_HANDLES_LOOKUP), null, l0, l1, 0);
            mv.visitLocalVariable("field", BytecodeUtil.referenceName(JAVA_LANG_REFLECT_FIELD), null, l0, l1, 1);
            mv.visitLocalVariable("setter", BytecodeUtil.referenceName(JAVA_LANG_REFLECT_METHOD), null, l0, l1, 2);
            mv.visitLocalVariable("getter", BytecodeUtil.referenceName(JAVA_LANG_REFLECT_METHOD), null, l0, l1, 3);
            mv.visitLocalVariable("wither", BytecodeUtil.referenceName(JAVA_LANG_REFLECT_METHOD), null, l0, l1, 4);
            for (Class clazz : entityClasses) {
                int index = PropertyAccessorClassGenerator.classVariableIndex5(entityClasses, clazz);
                mv.visitLocalVariable(String.format("class_%d", index), BytecodeUtil.referenceName(JAVA_LANG_CLASS), null, l0, l1, index);
            }
            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }

        private static List<Class<?>> getPropertyDeclaratingClasses(List<PersistentProperty<?>> persistentProperties) {
            return persistentProperties.stream().flatMap(property -> Optionals.toStream(Optional.ofNullable(property.getField()), Optional.ofNullable(property.getGetter()), Optional.ofNullable(property.getSetter())).map(it -> ((Member)((Object)it)).getDeclaringClass())).collect(Collectors.collectingAndThen(Collectors.toSet(), it -> new ArrayList(it)));
        }

        private static void visitPropertyGetterInitializer(PersistentProperty<?> property, MethodVisitor mv, List<Class<?>> entityClasses, String internalClassName) {
            Method getter = property.getGetter();
            if (getter != null) {
                mv.visitVarInsn(25, PropertyAccessorClassGenerator.classVariableIndex5(entityClasses, getter.getDeclaringClass()));
                mv.visitLdcInsn((Object)getter.getName());
                mv.visitInsn(3);
                mv.visitTypeInsn(189, JAVA_LANG_CLASS);
                mv.visitMethodInsn(182, JAVA_LANG_CLASS, "getDeclaredMethod", String.format("(%s[%s)%s", BytecodeUtil.referenceName(JAVA_LANG_STRING), BytecodeUtil.referenceName(JAVA_LANG_CLASS), BytecodeUtil.referenceName(JAVA_LANG_REFLECT_METHOD)), false);
                mv.visitVarInsn(58, 3);
                mv.visitVarInsn(25, 3);
                mv.visitInsn(4);
                mv.visitMethodInsn(182, JAVA_LANG_REFLECT_METHOD, SET_ACCESSIBLE, "(Z)V", false);
                mv.visitVarInsn(25, 0);
                mv.visitVarInsn(25, 3);
                mv.visitMethodInsn(182, JAVA_LANG_INVOKE_METHOD_HANDLES_LOOKUP, "unreflect", String.format("(%s)%s", BytecodeUtil.referenceName(JAVA_LANG_REFLECT_METHOD), BytecodeUtil.referenceName(JAVA_LANG_INVOKE_METHOD_HANDLE)), false);
            }
            if (getter == null) {
                mv.visitInsn(1);
            }
            mv.visitFieldInsn(179, internalClassName, PropertyAccessorClassGenerator.getterName(property), BytecodeUtil.referenceName(JAVA_LANG_INVOKE_METHOD_HANDLE));
        }

        private static void visitPropertySetterInitializer(@Nullable Method method, PersistentProperty<?> property, MethodVisitor mv, List<Class<?>> entityClasses, String internalClassName, Function<PersistentProperty<?>, String> setterNameFunction, int localVariableIndex) {
            if (method != null) {
                mv.visitVarInsn(25, PropertyAccessorClassGenerator.classVariableIndex5(entityClasses, method.getDeclaringClass()));
                mv.visitLdcInsn((Object)method.getName());
                mv.visitInsn(4);
                mv.visitTypeInsn(189, JAVA_LANG_CLASS);
                mv.visitInsn(89);
                mv.visitInsn(3);
                Class<?> parameterType = method.getParameterTypes()[0];
                if (parameterType.isPrimitive()) {
                    mv.visitFieldInsn(178, Type.getInternalName(BytecodeUtil.autoboxType(method.getParameterTypes()[0])), "TYPE", BytecodeUtil.referenceName(JAVA_LANG_CLASS));
                } else {
                    mv.visitLdcInsn((Object)Type.getType((String)BytecodeUtil.referenceName(parameterType)));
                }
                mv.visitInsn(83);
                mv.visitMethodInsn(182, JAVA_LANG_CLASS, "getDeclaredMethod", String.format("(%s[%s)%s", BytecodeUtil.referenceName(JAVA_LANG_STRING), BytecodeUtil.referenceName(JAVA_LANG_CLASS), BytecodeUtil.referenceName(JAVA_LANG_REFLECT_METHOD)), false);
                mv.visitVarInsn(58, localVariableIndex);
                mv.visitVarInsn(25, localVariableIndex);
                mv.visitInsn(4);
                mv.visitMethodInsn(182, JAVA_LANG_REFLECT_METHOD, SET_ACCESSIBLE, "(Z)V", false);
                mv.visitVarInsn(25, 0);
                mv.visitVarInsn(25, localVariableIndex);
                mv.visitMethodInsn(182, JAVA_LANG_INVOKE_METHOD_HANDLES_LOOKUP, "unreflect", String.format("(%s)%s", BytecodeUtil.referenceName(JAVA_LANG_REFLECT_METHOD), BytecodeUtil.referenceName(JAVA_LANG_INVOKE_METHOD_HANDLE)), false);
            }
            if (method == null) {
                mv.visitInsn(1);
            }
            mv.visitFieldInsn(179, internalClassName, setterNameFunction.apply(property), BytecodeUtil.referenceName(JAVA_LANG_INVOKE_METHOD_HANDLE));
        }

        private static void visitFieldGetterSetterInitializer(PersistentProperty<?> property, MethodVisitor mv, List<Class<?>> entityClasses, String internalClassName) {
            Field field = property.getField();
            if (field != null) {
                mv.visitVarInsn(25, PropertyAccessorClassGenerator.classVariableIndex5(entityClasses, field.getDeclaringClass()));
                mv.visitLdcInsn((Object)field.getName());
                mv.visitMethodInsn(182, JAVA_LANG_CLASS, "getDeclaredField", String.format("(%s)%s", BytecodeUtil.referenceName(JAVA_LANG_STRING), BytecodeUtil.referenceName(JAVA_LANG_REFLECT_FIELD)), false);
                mv.visitVarInsn(58, 1);
                mv.visitVarInsn(25, 1);
                mv.visitInsn(4);
                mv.visitMethodInsn(182, JAVA_LANG_REFLECT_FIELD, SET_ACCESSIBLE, "(Z)V", false);
                mv.visitVarInsn(25, 0);
                mv.visitVarInsn(25, 1);
                mv.visitMethodInsn(182, JAVA_LANG_INVOKE_METHOD_HANDLES_LOOKUP, "unreflectGetter", String.format("(%s)%s", BytecodeUtil.referenceName(JAVA_LANG_REFLECT_FIELD), BytecodeUtil.referenceName(JAVA_LANG_INVOKE_METHOD_HANDLE)), false);
                mv.visitFieldInsn(179, internalClassName, PropertyAccessorClassGenerator.fieldGetterName(property), BytecodeUtil.referenceName(JAVA_LANG_INVOKE_METHOD_HANDLE));
                if (!property.isImmutable()) {
                    mv.visitVarInsn(25, 0);
                    mv.visitVarInsn(25, 1);
                    mv.visitMethodInsn(182, JAVA_LANG_INVOKE_METHOD_HANDLES_LOOKUP, "unreflectSetter", String.format("(%s)%s", BytecodeUtil.referenceName(JAVA_LANG_REFLECT_FIELD), BytecodeUtil.referenceName(JAVA_LANG_INVOKE_METHOD_HANDLE)), false);
                    mv.visitFieldInsn(179, internalClassName, PropertyAccessorClassGenerator.fieldSetterName(property), BytecodeUtil.referenceName(JAVA_LANG_INVOKE_METHOD_HANDLE));
                }
            }
        }

        private static void visitBeanGetter(PersistentEntity<?, ?> entity, String internalClassName, ClassWriter cw) {
            MethodVisitor mv = cw.visitMethod(1, "getBean", String.format("()%s", BytecodeUtil.referenceName(JAVA_LANG_OBJECT)), null, null);
            mv.visitCode();
            Label l0 = new Label();
            mv.visitLabel(l0);
            mv.visitVarInsn(25, 0);
            mv.visitFieldInsn(180, internalClassName, BEAN_FIELD, PropertyAccessorClassGenerator.getAccessibleTypeReferenceName(entity));
            mv.visitInsn(176);
            Label l1 = new Label();
            mv.visitLabel(l1);
            mv.visitLocalVariable(THIS_REF, BytecodeUtil.referenceName(internalClassName), null, l0, l1, 0);
            mv.visitMaxs(1, 1);
            mv.visitEnd();
        }

        private static void visitGetProperty(PersistentEntity<?, ?> entity, List<PersistentProperty<?>> persistentProperties, String internalClassName, ClassWriter cw) {
            MethodVisitor mv = cw.visitMethod(1, "getProperty", "(Lorg/springframework/data/mapping/PersistentProperty;)Ljava/lang/Object;", "(Lorg/springframework/data/mapping/PersistentProperty<*>;)Ljava/lang/Object;", null);
            mv.visitCode();
            Label l0 = new Label();
            Label l1 = new Label();
            mv.visitLabel(l0);
            PropertyAccessorClassGenerator.visitAssertNotNull(mv);
            mv.visitVarInsn(25, 0);
            mv.visitFieldInsn(180, internalClassName, BEAN_FIELD, PropertyAccessorClassGenerator.getAccessibleTypeReferenceName(entity));
            mv.visitVarInsn(58, 2);
            PropertyAccessorClassGenerator.visitGetPropertySwitch(entity, persistentProperties, internalClassName, mv);
            mv.visitLabel(l1);
            PropertyAccessorClassGenerator.visitThrowUnsupportedOperationException(mv, "No accessor to get property %s!");
            mv.visitLocalVariable(THIS_REF, BytecodeUtil.referenceName(internalClassName), null, l0, l1, 0);
            mv.visitLocalVariable("property", BytecodeUtil.referenceName(PERSISTENT_PROPERTY), "Lorg/springframework/data/mapping/PersistentProperty<*>;", l0, l1, 1);
            mv.visitLocalVariable(BEAN_FIELD, PropertyAccessorClassGenerator.getAccessibleTypeReferenceName(entity), null, l0, l1, 2);
            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }

        private static void visitGetPropertySwitch(PersistentEntity<?, ?> entity, List<PersistentProperty<?>> persistentProperties, String internalClassName, MethodVisitor mv) {
            Map propertyStackMap = ClassGeneratingPropertyAccessorFactory.createPropertyStackMap(persistentProperties);
            int[] hashes = new int[propertyStackMap.size()];
            Label[] switchJumpLabels = new Label[propertyStackMap.size()];
            ArrayList stackmap = new ArrayList(propertyStackMap.values());
            Collections.sort(stackmap);
            for (int i = 0; i < stackmap.size(); ++i) {
                PropertyStackAddress propertyStackAddress = (PropertyStackAddress)stackmap.get(i);
                hashes[i] = propertyStackAddress.hash;
                switchJumpLabels[i] = propertyStackAddress.label;
            }
            Label dfltLabel = new Label();
            mv.visitVarInsn(25, 1);
            mv.visitMethodInsn(185, PERSISTENT_PROPERTY, "getName", String.format("()%s", BytecodeUtil.referenceName(JAVA_LANG_STRING)), true);
            mv.visitMethodInsn(182, JAVA_LANG_STRING, "hashCode", "()I", false);
            mv.visitLookupSwitchInsn(dfltLabel, hashes, switchJumpLabels);
            for (PersistentProperty<?> property : persistentProperties) {
                mv.visitLabel(((PropertyStackAddress)propertyStackMap.get(property.getName())).label);
                mv.visitFrame(3, 0, null, 0, null);
                if (property.getGetter() != null || property.getField() != null) {
                    PropertyAccessorClassGenerator.visitGetProperty0(entity, property, mv, internalClassName);
                    continue;
                }
                mv.visitJumpInsn(167, dfltLabel);
            }
            mv.visitLabel(dfltLabel);
            mv.visitFrame(3, 0, null, 0, null);
        }

        private static void visitGetProperty0(PersistentEntity<?, ?> entity, PersistentProperty<?> property, MethodVisitor mv, String internalClassName) {
            Method getter = property.getGetter();
            if (property.usePropertyAccess() && getter != null) {
                if (PropertyAccessorClassGenerator.generateMethodHandle(entity, getter)) {
                    mv.visitFieldInsn(178, internalClassName, PropertyAccessorClassGenerator.getterName(property), BytecodeUtil.referenceName(JAVA_LANG_INVOKE_METHOD_HANDLE));
                    mv.visitVarInsn(25, 2);
                    mv.visitMethodInsn(182, JAVA_LANG_INVOKE_METHOD_HANDLE, "invoke", String.format("(%s)%s", BytecodeUtil.referenceName(JAVA_LANG_OBJECT), BytecodeUtil.referenceName(JAVA_LANG_OBJECT)), false);
                } else {
                    mv.visitVarInsn(25, 2);
                    int invokeOpCode = 182;
                    Class<?> declaringClass = getter.getDeclaringClass();
                    boolean interfaceDefinition = declaringClass.isInterface();
                    if (interfaceDefinition) {
                        invokeOpCode = 185;
                    }
                    mv.visitMethodInsn(invokeOpCode, Type.getInternalName(declaringClass), getter.getName(), String.format("()%s", BytecodeUtil.signatureTypeName(getter.getReturnType())), interfaceDefinition);
                    BytecodeUtil.autoboxIfNeeded(getter.getReturnType(), BytecodeUtil.autoboxType(getter.getReturnType()), mv);
                }
            } else {
                Field field = property.getRequiredField();
                if (PropertyAccessorClassGenerator.generateMethodHandle(entity, field)) {
                    mv.visitFieldInsn(178, internalClassName, PropertyAccessorClassGenerator.fieldGetterName(property), BytecodeUtil.referenceName(JAVA_LANG_INVOKE_METHOD_HANDLE));
                    mv.visitVarInsn(25, 2);
                    mv.visitMethodInsn(182, JAVA_LANG_INVOKE_METHOD_HANDLE, "invoke", String.format("(%s)%s", BytecodeUtil.referenceName(JAVA_LANG_OBJECT), BytecodeUtil.referenceName(JAVA_LANG_OBJECT)), false);
                } else {
                    mv.visitVarInsn(25, 2);
                    mv.visitFieldInsn(180, Type.getInternalName(field.getDeclaringClass()), field.getName(), BytecodeUtil.signatureTypeName(field.getType()));
                    BytecodeUtil.autoboxIfNeeded(field.getType(), BytecodeUtil.autoboxType(field.getType()), mv);
                }
            }
            mv.visitInsn(176);
        }

        private static void visitSetProperty(PersistentEntity<?, ?> entity, List<PersistentProperty<?>> persistentProperties, String internalClassName, ClassWriter cw) {
            MethodVisitor mv = cw.visitMethod(1, "setProperty", "(Lorg/springframework/data/mapping/PersistentProperty;Ljava/lang/Object;)V", "(Lorg/springframework/data/mapping/PersistentProperty<*>;Ljava/lang/Object;)V", null);
            mv.visitCode();
            Label l0 = new Label();
            mv.visitLabel(l0);
            PropertyAccessorClassGenerator.visitAssertNotNull(mv);
            mv.visitVarInsn(25, 0);
            mv.visitFieldInsn(180, internalClassName, BEAN_FIELD, PropertyAccessorClassGenerator.getAccessibleTypeReferenceName(entity));
            mv.visitVarInsn(58, 3);
            PropertyAccessorClassGenerator.visitSetPropertySwitch(entity, persistentProperties, internalClassName, mv);
            Label l1 = new Label();
            mv.visitLabel(l1);
            PropertyAccessorClassGenerator.visitThrowUnsupportedOperationException(mv, "No accessor to set property %s!");
            mv.visitLocalVariable(THIS_REF, BytecodeUtil.referenceName(internalClassName), null, l0, l1, 0);
            mv.visitLocalVariable("property", "Lorg/springframework/data/mapping/PersistentProperty;", "Lorg/springframework/data/mapping/PersistentProperty<*>;", l0, l1, 1);
            mv.visitLocalVariable("value", BytecodeUtil.referenceName(JAVA_LANG_OBJECT), null, l0, l1, 2);
            mv.visitLocalVariable(BEAN_FIELD, PropertyAccessorClassGenerator.getAccessibleTypeReferenceName(entity), null, l0, l1, 3);
            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }

        private static void visitSetPropertySwitch(PersistentEntity<?, ?> entity, List<PersistentProperty<?>> persistentProperties, String internalClassName, MethodVisitor mv) {
            Map propertyStackMap = ClassGeneratingPropertyAccessorFactory.createPropertyStackMap(persistentProperties);
            int[] hashes = new int[propertyStackMap.size()];
            Label[] switchJumpLabels = new Label[propertyStackMap.size()];
            ArrayList stackmap = new ArrayList(propertyStackMap.values());
            Collections.sort(stackmap);
            for (int i = 0; i < stackmap.size(); ++i) {
                PropertyStackAddress propertyStackAddress = (PropertyStackAddress)stackmap.get(i);
                hashes[i] = propertyStackAddress.hash;
                switchJumpLabels[i] = propertyStackAddress.label;
            }
            Label dfltLabel = new Label();
            mv.visitVarInsn(25, 1);
            mv.visitMethodInsn(185, PERSISTENT_PROPERTY, "getName", String.format("()%s", BytecodeUtil.referenceName(JAVA_LANG_STRING)), true);
            mv.visitMethodInsn(182, JAVA_LANG_STRING, "hashCode", "()I", false);
            mv.visitLookupSwitchInsn(dfltLabel, hashes, switchJumpLabels);
            for (PersistentProperty<?> property : persistentProperties) {
                mv.visitLabel(((PropertyStackAddress)propertyStackMap.get(property.getName())).label);
                mv.visitFrame(3, 0, null, 0, null);
                if (ClassGeneratingPropertyAccessorFactory.supportsMutation(property)) {
                    PropertyAccessorClassGenerator.visitSetProperty0(entity, property, mv, internalClassName);
                    continue;
                }
                mv.visitJumpInsn(167, dfltLabel);
            }
            mv.visitLabel(dfltLabel);
            mv.visitFrame(3, 0, null, 0, null);
        }

        private static void visitSetProperty0(PersistentEntity<?, ?> entity, PersistentProperty<?> property, MethodVisitor mv, String internalClassName) {
            Method setter = property.getSetter();
            Method wither = property.getWither();
            if (property.isImmutable()) {
                if (wither != null) {
                    PropertyAccessorClassGenerator.visitWithProperty(entity, property, mv, internalClassName, wither);
                }
                if (ClassGeneratingPropertyAccessorFactory.hasKotlinCopyMethod(property)) {
                    PropertyAccessorClassGenerator.visitKotlinCopy(entity, property, mv, internalClassName);
                }
            } else if (property.usePropertyAccess() && setter != null) {
                PropertyAccessorClassGenerator.visitSetProperty(entity, property, mv, internalClassName, setter);
            } else {
                PropertyAccessorClassGenerator.visitSetField(entity, property, mv, internalClassName);
            }
            mv.visitInsn(177);
        }

        private static void visitWithProperty(PersistentEntity<?, ?> entity, PersistentProperty<?> property, MethodVisitor mv, String internalClassName, Method wither) {
            if (PropertyAccessorClassGenerator.generateMethodHandle(entity, wither)) {
                mv.visitVarInsn(25, 0);
                mv.visitFieldInsn(178, internalClassName, PropertyAccessorClassGenerator.witherName(property), BytecodeUtil.referenceName(JAVA_LANG_INVOKE_METHOD_HANDLE));
                mv.visitVarInsn(25, 3);
                mv.visitVarInsn(25, 2);
                mv.visitMethodInsn(182, JAVA_LANG_INVOKE_METHOD_HANDLE, "invoke", String.format("(%s%s)%s", BytecodeUtil.referenceName(JAVA_LANG_OBJECT), BytecodeUtil.referenceName(JAVA_LANG_OBJECT), PropertyAccessorClassGenerator.getAccessibleTypeReferenceName(entity)), false);
                if (PropertyAccessorClassGenerator.isAccessible(entity)) {
                    mv.visitTypeInsn(192, Type.getInternalName(entity.getType()));
                }
            } else {
                mv.visitVarInsn(25, 0);
                mv.visitVarInsn(25, 3);
                mv.visitVarInsn(25, 2);
                ClassGeneratingPropertyAccessorFactory.visitInvokeMethodSingleArg(mv, wither);
            }
            mv.visitFieldInsn(181, internalClassName, BEAN_FIELD, PropertyAccessorClassGenerator.getAccessibleTypeReferenceName(entity));
        }

        private static void visitKotlinCopy(PersistentEntity<?, ?> entity, PersistentProperty<?> property, MethodVisitor mv, String internalClassName) {
            KotlinCopyMethod kotlinCopyMethod = KotlinCopyMethod.findCopyMethod(entity.getType()).orElseThrow(() -> new IllegalStateException(String.format("No usable .copy(\u2026) method found in entity %s", entity.getType().getName())));
            mv.visitVarInsn(25, 0);
            if (kotlinCopyMethod.shouldUsePublicCopyMethod(entity)) {
                mv.visitVarInsn(25, 3);
                mv.visitVarInsn(25, 2);
                ClassGeneratingPropertyAccessorFactory.visitInvokeMethodSingleArg(mv, kotlinCopyMethod.getPublicCopyMethod());
            } else {
                Method copy = kotlinCopyMethod.getSyntheticCopyMethod();
                Class<?>[] parameterTypes = copy.getParameterTypes();
                mv.visitVarInsn(25, 3);
                KotlinCopyMethod.KotlinCopyByProperty copyByProperty = kotlinCopyMethod.forProperty(property).orElseThrow(() -> new IllegalStateException(String.format("No usable .copy(\u2026) method found for property %s", property)));
                for (int i = 1; i < kotlinCopyMethod.getParameterCount(); ++i) {
                    if (copyByProperty.getParameterPosition() == i) {
                        mv.visitVarInsn(25, 2);
                        mv.visitTypeInsn(192, Type.getInternalName(BytecodeUtil.autoboxType(parameterTypes[i])));
                        BytecodeUtil.autoboxIfNeeded(BytecodeUtil.autoboxType(parameterTypes[i]), parameterTypes[i], mv);
                        continue;
                    }
                    BytecodeUtil.visitDefaultValue(parameterTypes[i], mv);
                }
                copyByProperty.getDefaultMask().forEach(arg_0 -> ((MethodVisitor)mv).visitLdcInsn(arg_0));
                mv.visitInsn(1);
                int invokeOpCode = ClassGeneratingPropertyAccessorFactory.getInvokeOp(copy, false);
                mv.visitMethodInsn(invokeOpCode, Type.getInternalName(copy.getDeclaringClass()), copy.getName(), PropertyAccessorClassGenerator.getArgumentSignature(copy), false);
            }
            mv.visitFieldInsn(181, internalClassName, BEAN_FIELD, PropertyAccessorClassGenerator.getAccessibleTypeReferenceName(entity));
        }

        private static void visitSetProperty(PersistentEntity<?, ?> entity, PersistentProperty<?> property, MethodVisitor mv, String internalClassName, Method setter) {
            if (PropertyAccessorClassGenerator.generateMethodHandle(entity, setter)) {
                mv.visitFieldInsn(178, internalClassName, PropertyAccessorClassGenerator.setterName(property), BytecodeUtil.referenceName(JAVA_LANG_INVOKE_METHOD_HANDLE));
                mv.visitVarInsn(25, 3);
                mv.visitVarInsn(25, 2);
                mv.visitMethodInsn(182, JAVA_LANG_INVOKE_METHOD_HANDLE, "invoke", String.format("(%s%s)V", BytecodeUtil.referenceName(JAVA_LANG_OBJECT), BytecodeUtil.referenceName(JAVA_LANG_OBJECT)), false);
            } else {
                mv.visitVarInsn(25, 3);
                mv.visitVarInsn(25, 2);
                ClassGeneratingPropertyAccessorFactory.visitInvokeMethodSingleArg(mv, setter);
            }
        }

        private static void visitSetField(PersistentEntity<?, ?> entity, PersistentProperty<?> property, MethodVisitor mv, String internalClassName) {
            Field field = property.getField();
            if (field != null) {
                if (PropertyAccessorClassGenerator.generateSetterMethodHandle(entity, field)) {
                    mv.visitFieldInsn(178, internalClassName, PropertyAccessorClassGenerator.fieldSetterName(property), BytecodeUtil.referenceName(JAVA_LANG_INVOKE_METHOD_HANDLE));
                    mv.visitVarInsn(25, 3);
                    mv.visitVarInsn(25, 2);
                    mv.visitMethodInsn(182, JAVA_LANG_INVOKE_METHOD_HANDLE, "invoke", String.format("(%s%s)V", BytecodeUtil.referenceName(JAVA_LANG_OBJECT), BytecodeUtil.referenceName(JAVA_LANG_OBJECT)), false);
                } else {
                    mv.visitVarInsn(25, 3);
                    mv.visitVarInsn(25, 2);
                    Class<?> fieldType = field.getType();
                    mv.visitTypeInsn(192, Type.getInternalName(BytecodeUtil.autoboxType(fieldType)));
                    BytecodeUtil.autoboxIfNeeded(BytecodeUtil.autoboxType(fieldType), fieldType, mv);
                    mv.visitFieldInsn(181, Type.getInternalName(field.getDeclaringClass()), field.getName(), BytecodeUtil.signatureTypeName(fieldType));
                }
            }
        }

        private static String getArgumentSignature(Method method) {
            StringBuilder result = new StringBuilder("(");
            ArrayList<String> argumentTypes = new ArrayList<String>();
            for (Class<?> parameterType : method.getParameterTypes()) {
                result.append("%s");
                argumentTypes.add(BytecodeUtil.signatureTypeName(parameterType));
            }
            result.append(")%s");
            argumentTypes.add(BytecodeUtil.signatureTypeName(method.getReturnType()));
            return String.format(result.toString(), argumentTypes.toArray());
        }

        private static void visitAssertNotNull(MethodVisitor mv) {
            mv.visitVarInsn(25, 1);
            mv.visitLdcInsn((Object)"Property must not be null!");
            mv.visitMethodInsn(184, "org/springframework/util/Assert", "notNull", String.format("(%s%s)V", BytecodeUtil.referenceName(JAVA_LANG_OBJECT), BytecodeUtil.referenceName(JAVA_LANG_STRING)), false);
        }

        private static void visitThrowUnsupportedOperationException(MethodVisitor mv, String message) {
            mv.visitTypeInsn(187, JAVA_LANG_UNSUPPORTED_OPERATION_EXCEPTION);
            mv.visitInsn(89);
            mv.visitLdcInsn((Object)message);
            mv.visitInsn(4);
            mv.visitTypeInsn(189, JAVA_LANG_OBJECT);
            mv.visitInsn(89);
            mv.visitInsn(3);
            mv.visitVarInsn(25, 1);
            mv.visitInsn(83);
            mv.visitMethodInsn(184, JAVA_LANG_STRING, "format", "(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;", false);
            mv.visitMethodInsn(183, JAVA_LANG_UNSUPPORTED_OPERATION_EXCEPTION, INIT, "(Ljava/lang/String;)V", false);
            mv.visitInsn(191);
        }

        private static String fieldSetterName(PersistentProperty<?> property) {
            return String.format("$%s_fieldSetter", property.getName());
        }

        private static String fieldGetterName(PersistentProperty<?> property) {
            return String.format("$%s_fieldGetter", property.getName());
        }

        private static String setterName(PersistentProperty<?> property) {
            return String.format("$%s_setter", property.getName());
        }

        private static String witherName(PersistentProperty<?> property) {
            return String.format("$%s_wither", property.getName());
        }

        private static String getterName(PersistentProperty<?> property) {
            return String.format("$%s_getter", property.getName());
        }

        private static boolean isAccessible(PersistentEntity<?, ?> entity) {
            return BytecodeUtil.isAccessible(entity.getType());
        }

        private static String getAccessibleTypeReferenceName(PersistentEntity<?, ?> entity) {
            if (PropertyAccessorClassGenerator.isAccessible(entity)) {
                return BytecodeUtil.referenceName(entity.getType());
            }
            return BytecodeUtil.referenceName(JAVA_LANG_OBJECT);
        }

        private static boolean generateSetterMethodHandle(PersistentEntity<?, ?> entity, @Nullable Field field) {
            if (field == null) {
                return false;
            }
            return PropertyAccessorClassGenerator.generateMethodHandle(entity, field);
        }

        private static boolean generateMethodHandle(PersistentEntity<?, ?> entity, @Nullable Member member) {
            if (member == null) {
                return false;
            }
            if (PropertyAccessorClassGenerator.isAccessible(entity)) {
                Package declaringPackage;
                if ((Modifier.isProtected(member.getModifiers()) || BytecodeUtil.isDefault(member.getModifiers())) && (declaringPackage = member.getDeclaringClass().getPackage()) != null && !declaringPackage.equals(entity.getType().getPackage())) {
                    return true;
                }
                if (BytecodeUtil.isAccessible(member.getDeclaringClass()) && BytecodeUtil.isAccessible(member.getModifiers())) {
                    return false;
                }
            }
            return true;
        }

        private static int classVariableIndex5(List<Class<?>> list, Class<?> item) {
            return 5 + list.indexOf(item);
        }

        private static String generateClassName(PersistentEntity<?, ?> entity) {
            return entity.getType().getName() + TAG + Integer.toString(entity.hashCode(), 36);
        }
    }
}

