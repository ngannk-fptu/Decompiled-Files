/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.asm.ClassWriter
 *  org.springframework.asm.MethodVisitor
 *  org.springframework.asm.Type
 *  org.springframework.beans.BeanInstantiationException
 *  org.springframework.cglib.core.ReflectUtils
 *  org.springframework.core.NativeDetector
 *  org.springframework.lang.Nullable
 *  org.springframework.util.ClassUtils
 */
package org.springframework.data.mapping.model;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.security.ProtectionDomain;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.asm.ClassWriter;
import org.springframework.asm.MethodVisitor;
import org.springframework.asm.Type;
import org.springframework.beans.BeanInstantiationException;
import org.springframework.cglib.core.ReflectUtils;
import org.springframework.core.NativeDetector;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.data.mapping.PreferredConstructor;
import org.springframework.data.mapping.model.EntityInstantiator;
import org.springframework.data.mapping.model.MappingInstantiationException;
import org.springframework.data.mapping.model.ParameterValueProvider;
import org.springframework.data.mapping.model.ReflectionEntityInstantiator;
import org.springframework.data.util.TypeInformation;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;

class ClassGeneratingEntityInstantiator
implements EntityInstantiator {
    private static final Log LOGGER = LogFactory.getLog(ClassGeneratingEntityInstantiator.class);
    private static final Object[] EMPTY_ARGS = new Object[0];
    private final ObjectInstantiatorClassGenerator generator;
    private volatile Map<TypeInformation<?>, EntityInstantiator> entityInstantiators = new HashMap(32);

    public ClassGeneratingEntityInstantiator() {
        this.generator = new ObjectInstantiatorClassGenerator();
    }

    @Override
    public <T, E extends PersistentEntity<? extends T, P>, P extends PersistentProperty<P>> T createInstance(E entity, ParameterValueProvider<P> provider) {
        EntityInstantiator instantiator = this.entityInstantiators.get(entity.getTypeInformation());
        if (instantiator == null) {
            instantiator = this.potentiallyCreateAndRegisterEntityInstantiator(entity);
        }
        return instantiator.createInstance(entity, provider);
    }

    private synchronized EntityInstantiator potentiallyCreateAndRegisterEntityInstantiator(PersistentEntity<?, ?> entity) {
        Map<TypeInformation<?>, EntityInstantiator> map = this.entityInstantiators;
        EntityInstantiator instantiator = map.get(entity.getTypeInformation());
        if (instantiator != null) {
            return instantiator;
        }
        instantiator = this.createEntityInstantiator(entity);
        map = new HashMap(map);
        map.put(entity.getTypeInformation(), instantiator);
        this.entityInstantiators = map;
        return instantiator;
    }

    private EntityInstantiator createEntityInstantiator(PersistentEntity<?, ?> entity) {
        if (this.shouldUseReflectionEntityInstantiator(entity)) {
            return ReflectionEntityInstantiator.INSTANCE;
        }
        if (Modifier.isAbstract(entity.getType().getModifiers())) {
            return MappingInstantiationExceptionEntityInstantiator.create(entity.getType());
        }
        try {
            return this.doCreateEntityInstantiator(entity);
        }
        catch (Throwable ex) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug((Object)String.format("Cannot create entity instantiator for %s. Falling back to ReflectionEntityInstantiator.", entity.getName()), ex);
            }
            return ReflectionEntityInstantiator.INSTANCE;
        }
    }

    protected EntityInstantiator doCreateEntityInstantiator(PersistentEntity<?, ?> entity) {
        return new EntityInstantiatorAdapter(this.createObjectInstantiator(entity, entity.getPersistenceConstructor()));
    }

    boolean shouldUseReflectionEntityInstantiator(PersistentEntity<?, ?> entity) {
        if (NativeDetector.inNativeImage()) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug((Object)String.format("graalvm.nativeimage - fall back to reflection for %s.", entity.getName()));
            }
            return true;
        }
        Class<?> type = entity.getType();
        if (type.isInterface() || type.isArray() || Modifier.isPrivate(type.getModifiers()) || type.isMemberClass() && !Modifier.isStatic(type.getModifiers()) || ClassUtils.isCglibProxyClass(type)) {
            return true;
        }
        PreferredConstructor<?, ?> persistenceConstructor = entity.getPersistenceConstructor();
        if (persistenceConstructor == null || Modifier.isPrivate(persistenceConstructor.getConstructor().getModifiers())) {
            return true;
        }
        return !ClassUtils.isPresent((String)ObjectInstantiator.class.getName(), (ClassLoader)type.getClassLoader());
    }

    static Object[] allocateArguments(int argumentCount) {
        return argumentCount == 0 ? EMPTY_ARGS : new Object[argumentCount];
    }

    ObjectInstantiator createObjectInstantiator(PersistentEntity<?, ?> entity, @Nullable PreferredConstructor<?, ?> constructor) {
        try {
            return (ObjectInstantiator)this.generator.generateCustomInstantiatorClass(entity, constructor).newInstance();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static <P extends PersistentProperty<P>, T> Object[] extractInvocationArguments(@Nullable PreferredConstructor<? extends T, P> constructor, ParameterValueProvider<P> provider) {
        if (constructor == null || !constructor.hasParameters()) {
            return ClassGeneratingEntityInstantiator.allocateArguments(0);
        }
        Object[] params = ClassGeneratingEntityInstantiator.allocateArguments(constructor.getConstructor().getParameterCount());
        int index = 0;
        for (PreferredConstructor.Parameter<Object, P> parameter : constructor.getParameters()) {
            params[index++] = provider.getParameterValue(parameter);
        }
        return params;
    }

    static class ObjectInstantiatorClassGenerator {
        private static final String INIT = "<init>";
        private static final String TAG = "_Instantiator_";
        private static final String JAVA_LANG_OBJECT = "java/lang/Object";
        private static final String CREATE_METHOD_NAME = "newInstance";
        private static final String[] IMPLEMENTED_INTERFACES = new String[]{Type.getInternalName(ObjectInstantiator.class)};

        ObjectInstantiatorClassGenerator() {
        }

        public Class<?> generateCustomInstantiatorClass(PersistentEntity<?, ?> entity, @Nullable PreferredConstructor<?, ?> constructor) {
            Class<?> type;
            ClassLoader classLoader;
            String className = this.generateClassName(entity);
            if (ClassUtils.isPresent((String)className, (ClassLoader)(classLoader = (type = entity.getType()).getClassLoader()))) {
                try {
                    return ClassUtils.forName((String)className, (ClassLoader)classLoader);
                }
                catch (Exception o_O) {
                    throw new IllegalStateException(o_O);
                }
            }
            byte[] bytecode = this.generateBytecode(className, entity, constructor);
            try {
                return ReflectUtils.defineClass((String)className, (byte[])bytecode, (ClassLoader)classLoader, (ProtectionDomain)type.getProtectionDomain(), type);
            }
            catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }

        private String generateClassName(PersistentEntity<?, ?> entity) {
            return entity.getType().getName() + TAG + Integer.toString(entity.hashCode(), 36);
        }

        public byte[] generateBytecode(String internalClassName, PersistentEntity<?, ?> entity, @Nullable PreferredConstructor<?, ?> constructor) {
            ClassWriter cw = new ClassWriter(1);
            cw.visit(50, 33, internalClassName.replace('.', '/'), null, JAVA_LANG_OBJECT, IMPLEMENTED_INTERFACES);
            this.visitDefaultConstructor(cw);
            this.visitCreateMethod(cw, entity, constructor);
            cw.visitEnd();
            return cw.toByteArray();
        }

        private void visitDefaultConstructor(ClassWriter cw) {
            MethodVisitor mv = cw.visitMethod(1, INIT, "()V", null, null);
            mv.visitCode();
            mv.visitVarInsn(25, 0);
            mv.visitMethodInsn(183, JAVA_LANG_OBJECT, INIT, "()V", false);
            mv.visitInsn(177);
            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }

        private void visitCreateMethod(ClassWriter cw, PersistentEntity<?, ?> entity, @Nullable PreferredConstructor<?, ?> constructor) {
            String entityTypeResourcePath = Type.getInternalName(entity.getType());
            MethodVisitor mv = cw.visitMethod(129, CREATE_METHOD_NAME, "([Ljava/lang/Object;)Ljava/lang/Object;", null, null);
            mv.visitCode();
            mv.visitTypeInsn(187, entityTypeResourcePath);
            mv.visitInsn(89);
            if (constructor != null) {
                Constructor<?> ctor = constructor.getConstructor();
                Class<?>[] parameterTypes = ctor.getParameterTypes();
                List<PreferredConstructor.Parameter<Object, ?>> parameters = constructor.getParameters();
                for (int i = 0; i < parameterTypes.length; ++i) {
                    mv.visitVarInsn(25, 1);
                    ObjectInstantiatorClassGenerator.visitArrayIndex(mv, i);
                    mv.visitInsn(50);
                    if (parameterTypes[i].isPrimitive()) {
                        mv.visitInsn(89);
                        String parameterName = parameters.size() > i ? parameters.get(i).getName() : null;
                        ObjectInstantiatorClassGenerator.insertAssertNotNull(mv, parameterName == null ? String.format("at index %d", i) : parameterName);
                        ObjectInstantiatorClassGenerator.insertUnboxInsns(mv, Type.getType(parameterTypes[i]).toString().charAt(0), "");
                        continue;
                    }
                    mv.visitTypeInsn(192, Type.getInternalName(parameterTypes[i]));
                }
                mv.visitMethodInsn(183, entityTypeResourcePath, INIT, Type.getConstructorDescriptor(ctor), false);
                mv.visitInsn(176);
                mv.visitMaxs(0, 0);
                mv.visitEnd();
            }
        }

        private static void visitArrayIndex(MethodVisitor mv, int idx) {
            if (idx >= 0 && idx < 6) {
                mv.visitInsn(3 + idx);
                return;
            }
            mv.visitLdcInsn((Object)idx);
        }

        private static void insertAssertNotNull(MethodVisitor mv, String parameterName) {
            mv.visitLdcInsn((Object)String.format("Parameter %s must not be null!", parameterName));
            mv.visitMethodInsn(184, "org/springframework/util/Assert", "notNull", String.format("(%s%s)V", String.format("L%s;", JAVA_LANG_OBJECT), "Ljava/lang/String;"), false);
        }

        private static void insertUnboxInsns(MethodVisitor mv, char ch, String stackDescriptor) {
            switch (ch) {
                case 'Z': {
                    if (!stackDescriptor.equals("Ljava/lang/Boolean")) {
                        mv.visitTypeInsn(192, "java/lang/Boolean");
                    }
                    mv.visitMethodInsn(182, "java/lang/Boolean", "booleanValue", "()Z", false);
                    break;
                }
                case 'B': {
                    if (!stackDescriptor.equals("Ljava/lang/Byte")) {
                        mv.visitTypeInsn(192, "java/lang/Byte");
                    }
                    mv.visitMethodInsn(182, "java/lang/Byte", "byteValue", "()B", false);
                    break;
                }
                case 'C': {
                    if (!stackDescriptor.equals("Ljava/lang/Character")) {
                        mv.visitTypeInsn(192, "java/lang/Character");
                    }
                    mv.visitMethodInsn(182, "java/lang/Character", "charValue", "()C", false);
                    break;
                }
                case 'D': {
                    if (!stackDescriptor.equals("Ljava/lang/Double")) {
                        mv.visitTypeInsn(192, "java/lang/Double");
                    }
                    mv.visitMethodInsn(182, "java/lang/Double", "doubleValue", "()D", false);
                    break;
                }
                case 'F': {
                    if (!stackDescriptor.equals("Ljava/lang/Float")) {
                        mv.visitTypeInsn(192, "java/lang/Float");
                    }
                    mv.visitMethodInsn(182, "java/lang/Float", "floatValue", "()F", false);
                    break;
                }
                case 'I': {
                    if (!stackDescriptor.equals("Ljava/lang/Integer")) {
                        mv.visitTypeInsn(192, "java/lang/Integer");
                    }
                    mv.visitMethodInsn(182, "java/lang/Integer", "intValue", "()I", false);
                    break;
                }
                case 'J': {
                    if (!stackDescriptor.equals("Ljava/lang/Long")) {
                        mv.visitTypeInsn(192, "java/lang/Long");
                    }
                    mv.visitMethodInsn(182, "java/lang/Long", "longValue", "()J", false);
                    break;
                }
                case 'S': {
                    if (!stackDescriptor.equals("Ljava/lang/Short")) {
                        mv.visitTypeInsn(192, "java/lang/Short");
                    }
                    mv.visitMethodInsn(182, "java/lang/Short", "shortValue", "()S", false);
                    break;
                }
                default: {
                    throw new IllegalArgumentException("Unboxing should not be attempted for descriptor '" + ch + "'");
                }
            }
        }
    }

    static class MappingInstantiationExceptionEntityInstantiator
    implements EntityInstantiator {
        private final Class<?> typeToCreate;

        private MappingInstantiationExceptionEntityInstantiator(Class<?> typeToCreate) {
            this.typeToCreate = typeToCreate;
        }

        public static EntityInstantiator create(Class<?> typeToCreate) {
            return new MappingInstantiationExceptionEntityInstantiator(typeToCreate);
        }

        @Override
        public <T, E extends PersistentEntity<? extends T, P>, P extends PersistentProperty<P>> T createInstance(E entity, ParameterValueProvider<P> provider) {
            Object[] params = ClassGeneratingEntityInstantiator.extractInvocationArguments(entity.getPersistenceConstructor(), provider);
            throw new MappingInstantiationException(entity, Arrays.asList(params), (Exception)((Object)new BeanInstantiationException(this.typeToCreate, "Class is abstract")));
        }
    }

    public static interface ObjectInstantiator {
        public Object newInstance(Object ... var1);
    }

    private static class EntityInstantiatorAdapter
    implements EntityInstantiator {
        private final ObjectInstantiator instantiator;

        public EntityInstantiatorAdapter(ObjectInstantiator instantiator) {
            this.instantiator = instantiator;
        }

        @Override
        public <T, E extends PersistentEntity<? extends T, P>, P extends PersistentProperty<P>> T createInstance(E entity, ParameterValueProvider<P> provider) {
            Object[] params = ClassGeneratingEntityInstantiator.extractInvocationArguments(entity.getPersistenceConstructor(), provider);
            try {
                return (T)this.instantiator.newInstance(params);
            }
            catch (Exception e) {
                throw new MappingInstantiationException(entity, Arrays.asList(params), e);
            }
        }
    }
}

