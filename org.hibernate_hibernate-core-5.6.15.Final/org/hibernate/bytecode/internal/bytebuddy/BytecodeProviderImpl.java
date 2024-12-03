/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.bytebuddy.ClassFileVersion
 *  net.bytebuddy.NamingStrategy
 *  net.bytebuddy.NamingStrategy$SuffixingRandom
 *  net.bytebuddy.NamingStrategy$SuffixingRandom$BaseNameResolver
 *  net.bytebuddy.NamingStrategy$SuffixingRandom$BaseNameResolver$ForFixedValue
 *  net.bytebuddy.description.method.MethodDescription
 *  net.bytebuddy.description.type.TypeDefinition
 *  net.bytebuddy.description.type.TypeDescription$ForLoadedType
 *  net.bytebuddy.description.type.TypeDescription$Generic
 *  net.bytebuddy.description.type.TypeDescription$Generic$OfNonGenericType$ForLoadedType
 *  net.bytebuddy.implementation.Implementation
 *  net.bytebuddy.implementation.Implementation$Context
 *  net.bytebuddy.implementation.Implementation$Simple
 *  net.bytebuddy.implementation.MethodCall
 *  net.bytebuddy.implementation.bytecode.ByteCodeAppender
 *  net.bytebuddy.implementation.bytecode.ByteCodeAppender$Size
 *  net.bytebuddy.implementation.bytecode.assign.Assigner
 *  net.bytebuddy.implementation.bytecode.assign.Assigner$Typing
 *  net.bytebuddy.implementation.bytecode.assign.primitive.PrimitiveBoxingDelegate
 *  net.bytebuddy.implementation.bytecode.assign.primitive.PrimitiveUnboxingDelegate
 *  net.bytebuddy.implementation.bytecode.assign.reference.ReferenceTypeAwareAssigner
 *  net.bytebuddy.jar.asm.MethodVisitor
 *  net.bytebuddy.jar.asm.Type
 *  net.bytebuddy.matcher.ElementMatcher
 *  net.bytebuddy.matcher.ElementMatcher$Junction
 *  net.bytebuddy.matcher.ElementMatchers
 */
package org.hibernate.bytecode.internal.bytebuddy;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.concurrent.Callable;
import net.bytebuddy.ClassFileVersion;
import net.bytebuddy.NamingStrategy;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDefinition;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.MethodCall;
import net.bytebuddy.implementation.bytecode.ByteCodeAppender;
import net.bytebuddy.implementation.bytecode.assign.Assigner;
import net.bytebuddy.implementation.bytecode.assign.primitive.PrimitiveBoxingDelegate;
import net.bytebuddy.implementation.bytecode.assign.primitive.PrimitiveUnboxingDelegate;
import net.bytebuddy.implementation.bytecode.assign.reference.ReferenceTypeAwareAssigner;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.jar.asm.Type;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;
import org.hibernate.HibernateException;
import org.hibernate.bytecode.enhance.internal.bytebuddy.EnhancerImpl;
import org.hibernate.bytecode.enhance.spi.EnhancementContext;
import org.hibernate.bytecode.enhance.spi.Enhancer;
import org.hibernate.bytecode.internal.bytebuddy.BulkAccessorException;
import org.hibernate.bytecode.internal.bytebuddy.ByteBuddyState;
import org.hibernate.bytecode.internal.bytebuddy.ProxyFactoryFactoryImpl;
import org.hibernate.bytecode.internal.bytebuddy.ReflectionOptimizerImpl;
import org.hibernate.bytecode.spi.BytecodeProvider;
import org.hibernate.bytecode.spi.ProxyFactoryFactory;
import org.hibernate.bytecode.spi.ReflectionOptimizer;
import org.hibernate.proxy.pojo.bytebuddy.ByteBuddyProxyHelper;

public class BytecodeProviderImpl
implements BytecodeProvider {
    private static final String INSTANTIATOR_PROXY_NAMING_SUFFIX = "HibernateInstantiator";
    private static final String OPTIMIZER_PROXY_NAMING_SUFFIX = "HibernateAccessOptimizer";
    private static final ElementMatcher.Junction newInstanceMethodName = ElementMatchers.named((String)"newInstance");
    private static final ElementMatcher.Junction getPropertyValuesMethodName = ElementMatchers.named((String)"getPropertyValues");
    private static final ElementMatcher.Junction setPropertyValuesMethodName = ElementMatchers.named((String)"setPropertyValues");
    private static final ElementMatcher.Junction getPropertyNamesMethodName = ElementMatchers.named((String)"getPropertyNames");
    private final ByteBuddyState byteBuddyState;
    private final ByteBuddyProxyHelper byteBuddyProxyHelper;

    public BytecodeProviderImpl() {
        this(ClassFileVersion.ofThisVm((ClassFileVersion)ClassFileVersion.JAVA_V8));
    }

    public BytecodeProviderImpl(ClassFileVersion targetCompatibleJVM) {
        this.byteBuddyState = new ByteBuddyState(targetCompatibleJVM);
        this.byteBuddyProxyHelper = new ByteBuddyProxyHelper(this.byteBuddyState);
    }

    @Override
    public ProxyFactoryFactory getProxyFactoryFactory() {
        return new ProxyFactoryFactoryImpl(this.byteBuddyState, this.byteBuddyProxyHelper);
    }

    @Override
    public ReflectionOptimizer getReflectionOptimizer(Class clazz, String[] getterNames, String[] setterNames, Class[] types) {
        Class<?> fastClass;
        if (!clazz.isInterface() && !Modifier.isAbstract(clazz.getModifiers())) {
            Constructor<?> constructor = BytecodeProviderImpl.findConstructor(clazz);
            fastClass = this.byteBuddyState.load(clazz, byteBuddy -> byteBuddy.with((NamingStrategy)new NamingStrategy.SuffixingRandom(INSTANTIATOR_PROXY_NAMING_SUFFIX, (NamingStrategy.SuffixingRandom.BaseNameResolver)new NamingStrategy.SuffixingRandom.BaseNameResolver.ForFixedValue(clazz.getName()))).subclass(ReflectionOptimizer.InstantiationOptimizer.class).method((ElementMatcher)newInstanceMethodName).intercept((Implementation)MethodCall.construct((Constructor)constructor)));
        } else {
            fastClass = null;
        }
        Method[] getters = new Method[getterNames.length];
        Method[] setters = new Method[setterNames.length];
        BytecodeProviderImpl.findAccessors(clazz, getterNames, setterNames, types, getters, setters);
        Class<?> bulkAccessor = this.byteBuddyState.load(clazz, byteBuddy -> byteBuddy.with((NamingStrategy)new NamingStrategy.SuffixingRandom(OPTIMIZER_PROXY_NAMING_SUFFIX, (NamingStrategy.SuffixingRandom.BaseNameResolver)new NamingStrategy.SuffixingRandom.BaseNameResolver.ForFixedValue(clazz.getName()))).subclass(ReflectionOptimizer.AccessOptimizer.class).method((ElementMatcher)getPropertyValuesMethodName).intercept((Implementation)new Implementation.Simple(new ByteCodeAppender[]{new GetPropertyValues(clazz, getters)})).method((ElementMatcher)setPropertyValuesMethodName).intercept((Implementation)new Implementation.Simple(new ByteCodeAppender[]{new SetPropertyValues(clazz, setters)})).method((ElementMatcher)getPropertyNamesMethodName).intercept((Implementation)MethodCall.call((Callable)new CloningPropertyCall(getterNames))));
        try {
            return new ReflectionOptimizerImpl(fastClass != null ? (ReflectionOptimizer.InstantiationOptimizer)fastClass.newInstance() : null, (ReflectionOptimizer.AccessOptimizer)bulkAccessor.newInstance());
        }
        catch (Exception exception) {
            throw new HibernateException(exception);
        }
    }

    public ByteBuddyProxyHelper getByteBuddyProxyHelper() {
        return this.byteBuddyProxyHelper;
    }

    private static void findAccessors(Class clazz, String[] getterNames, String[] setterNames, Class[] types, Method[] getters, Method[] setters) {
        int length = types.length;
        if (setterNames.length != length || getterNames.length != length) {
            throw new BulkAccessorException("bad number of accessors");
        }
        Class[] getParam = new Class[]{};
        Class[] setParam = new Class[1];
        for (int i = 0; i < length; ++i) {
            if (getterNames[i] != null) {
                Method getter = BytecodeProviderImpl.findAccessor(clazz, getterNames[i], getParam, i);
                if (getter.getReturnType() != types[i]) {
                    throw new BulkAccessorException("wrong return type: " + getterNames[i], i);
                }
                getters[i] = getter;
            }
            if (setterNames[i] == null) continue;
            setParam[0] = types[i];
            setters[i] = BytecodeProviderImpl.findAccessor(clazz, setterNames[i], setParam, i);
        }
    }

    private static Method findAccessor(Class clazz, String name, Class[] params, int index) throws BulkAccessorException {
        try {
            Method method = clazz.getDeclaredMethod(name, params);
            if (Modifier.isPrivate(method.getModifiers())) {
                throw new BulkAccessorException("private property", index);
            }
            return method;
        }
        catch (NoSuchMethodException e) {
            throw new BulkAccessorException("cannot find an accessor", index);
        }
    }

    private static Constructor<?> findConstructor(Class clazz) {
        try {
            return clazz.getDeclaredConstructor(new Class[0]);
        }
        catch (NoSuchMethodException e) {
            throw new HibernateException(e);
        }
    }

    @Override
    public Enhancer getEnhancer(EnhancementContext enhancementContext) {
        return new EnhancerImpl(enhancementContext, this.byteBuddyState);
    }

    @Override
    public void resetCaches() {
        this.byteBuddyState.clearState();
    }

    public static class CloningPropertyCall
    implements Callable<String[]> {
        private final String[] propertyNames;

        private CloningPropertyCall(String[] propertyNames) {
            this.propertyNames = propertyNames;
        }

        @Override
        public String[] call() {
            return (String[])this.propertyNames.clone();
        }
    }

    private static class SetPropertyValues
    implements ByteCodeAppender {
        private final Class clazz;
        private final Method[] setters;

        public SetPropertyValues(Class clazz, Method[] setters) {
            this.clazz = clazz;
            this.setters = setters;
        }

        public ByteCodeAppender.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext, MethodDescription instrumentedMethod) {
            int index = 0;
            for (Method setter : this.setters) {
                methodVisitor.visitVarInsn(25, 1);
                methodVisitor.visitTypeInsn(192, Type.getInternalName((Class)this.clazz));
                methodVisitor.visitVarInsn(25, 2);
                methodVisitor.visitLdcInsn((Object)index++);
                methodVisitor.visitInsn(50);
                if (setter.getParameterTypes()[0].isPrimitive()) {
                    PrimitiveUnboxingDelegate.forReferenceType((TypeDefinition)TypeDescription.Generic.OBJECT).assignUnboxedTo((TypeDescription.Generic)new TypeDescription.Generic.OfNonGenericType.ForLoadedType(setter.getParameterTypes()[0]), (Assigner)ReferenceTypeAwareAssigner.INSTANCE, Assigner.Typing.DYNAMIC).apply(methodVisitor, implementationContext);
                } else {
                    methodVisitor.visitTypeInsn(192, Type.getInternalName(setter.getParameterTypes()[0]));
                }
                methodVisitor.visitMethodInsn(182, Type.getInternalName((Class)this.clazz), setter.getName(), Type.getMethodDescriptor((Method)setter), false);
            }
            methodVisitor.visitInsn(177);
            return new ByteCodeAppender.Size(4, instrumentedMethod.getStackSize());
        }
    }

    private static class GetPropertyValues
    implements ByteCodeAppender {
        private final Class clazz;
        private final Method[] getters;

        public GetPropertyValues(Class clazz, Method[] getters) {
            this.clazz = clazz;
            this.getters = getters;
        }

        public ByteCodeAppender.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext, MethodDescription instrumentedMethod) {
            methodVisitor.visitLdcInsn((Object)this.getters.length);
            methodVisitor.visitTypeInsn(189, Type.getInternalName(Object.class));
            int index = 0;
            for (Method getter : this.getters) {
                methodVisitor.visitInsn(89);
                methodVisitor.visitLdcInsn((Object)index++);
                methodVisitor.visitVarInsn(25, 1);
                methodVisitor.visitTypeInsn(192, Type.getInternalName((Class)this.clazz));
                methodVisitor.visitMethodInsn(182, Type.getInternalName((Class)this.clazz), getter.getName(), Type.getMethodDescriptor((Method)getter), false);
                if (getter.getReturnType().isPrimitive()) {
                    PrimitiveBoxingDelegate.forPrimitive((TypeDefinition)new TypeDescription.ForLoadedType(getter.getReturnType())).assignBoxedTo(TypeDescription.Generic.OBJECT, (Assigner)ReferenceTypeAwareAssigner.INSTANCE, Assigner.Typing.STATIC).apply(methodVisitor, implementationContext);
                }
                methodVisitor.visitInsn(83);
            }
            methodVisitor.visitInsn(176);
            return new ByteCodeAppender.Size(6, instrumentedMethod.getStackSize());
        }
    }
}

