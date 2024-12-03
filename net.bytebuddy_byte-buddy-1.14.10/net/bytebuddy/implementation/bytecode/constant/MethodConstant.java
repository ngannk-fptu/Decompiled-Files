/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package net.bytebuddy.implementation.bytecode.constant;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.List;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.MethodList;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.auxiliary.PrivilegedMemberLookupAction;
import net.bytebuddy.implementation.bytecode.Duplication;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.implementation.bytecode.TypeCreation;
import net.bytebuddy.implementation.bytecode.assign.TypeCasting;
import net.bytebuddy.implementation.bytecode.collection.ArrayFactory;
import net.bytebuddy.implementation.bytecode.constant.ClassConstant;
import net.bytebuddy.implementation.bytecode.constant.TextConstant;
import net.bytebuddy.implementation.bytecode.member.FieldAccess;
import net.bytebuddy.implementation.bytecode.member.MethodInvocation;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.nullability.MaybeNull;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class MethodConstant
extends StackManipulation.AbstractBase {
    @MaybeNull
    protected static final MethodDescription.InDefinedShape DO_PRIVILEGED = MethodConstant.doPrivileged();
    protected final MethodDescription.InDefinedShape methodDescription;

    @MaybeNull
    @SuppressFBWarnings(value={"REC_CATCH_EXCEPTION"}, justification="Exception should not be rethrown but trigger a fallback.")
    private static MethodDescription.InDefinedShape doPrivileged() {
        MethodDescription.ForLoadedMethod doPrivileged;
        try {
            doPrivileged = new MethodDescription.ForLoadedMethod(Class.forName("java.security.AccessController").getMethod("doPrivileged", PrivilegedExceptionAction.class));
            try {
                if (!Boolean.parseBoolean(System.getProperty("net.bytebuddy.securitymanager", "true"))) {
                    doPrivileged = null;
                }
            }
            catch (SecurityException securityException) {}
        }
        catch (Exception ignored) {
            doPrivileged = null;
        }
        return doPrivileged;
    }

    protected MethodConstant(MethodDescription.InDefinedShape methodDescription) {
        this.methodDescription = methodDescription;
    }

    public static CanCache of(MethodDescription.InDefinedShape methodDescription) {
        if (methodDescription.isTypeInitializer()) {
            return CanCacheIllegal.INSTANCE;
        }
        if (methodDescription.isConstructor()) {
            return new ForConstructor(methodDescription);
        }
        return new ForMethod(methodDescription);
    }

    public static CanCache ofPrivileged(MethodDescription.InDefinedShape methodDescription) {
        if (DO_PRIVILEGED == null) {
            return MethodConstant.of(methodDescription);
        }
        if (methodDescription.isTypeInitializer()) {
            return CanCacheIllegal.INSTANCE;
        }
        if (methodDescription.isConstructor()) {
            return new ForConstructor(methodDescription).withPrivilegedLookup();
        }
        return new ForMethod(methodDescription).withPrivilegedLookup();
    }

    protected static List<StackManipulation> typeConstantsFor(List<TypeDescription> parameterTypes) {
        ArrayList<StackManipulation> typeConstants = new ArrayList<StackManipulation>(parameterTypes.size());
        for (TypeDescription parameterType : parameterTypes) {
            typeConstants.add(ClassConstant.of(parameterType));
        }
        return typeConstants;
    }

    @Override
    public StackManipulation.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext) {
        return new StackManipulation.Compound(ClassConstant.of(this.methodDescription.getDeclaringType()), this.methodName(), ArrayFactory.forType(TypeDescription.Generic.OfNonGenericType.ForLoadedType.of(Class.class)).withValues(MethodConstant.typeConstantsFor(this.methodDescription.getParameters().asTypeList().asErasures())), MethodInvocation.invoke(this.accessorMethod())).apply(methodVisitor, implementationContext);
    }

    protected CanCache withPrivilegedLookup() {
        return new PrivilegedLookup(this.methodDescription, this.methodName());
    }

    protected abstract StackManipulation methodName();

    protected abstract MethodDescription.InDefinedShape accessorMethod();

    public int hashCode() {
        return this.methodDescription.hashCode();
    }

    public boolean equals(@MaybeNull Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || this.getClass() != other.getClass()) {
            return false;
        }
        MethodConstant methodConstant = (MethodConstant)other;
        return this.methodDescription.equals(methodConstant.methodDescription);
    }

    protected static class CachedConstructor
    implements StackManipulation {
        private static final TypeDescription CONSTRUCTOR_TYPE = TypeDescription.ForLoadedType.of(Constructor.class);
        private final StackManipulation constructorConstant;

        protected CachedConstructor(StackManipulation constructorConstant) {
            this.constructorConstant = constructorConstant;
        }

        public boolean isValid() {
            return this.constructorConstant.isValid();
        }

        public StackManipulation.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext) {
            return FieldAccess.forField(implementationContext.cache(this.constructorConstant, CONSTRUCTOR_TYPE)).read().apply(methodVisitor, implementationContext);
        }

        public int hashCode() {
            return this.constructorConstant.hashCode();
        }

        public boolean equals(@MaybeNull Object other) {
            if (this == other) {
                return true;
            }
            if (other == null || this.getClass() != other.getClass()) {
                return false;
            }
            CachedConstructor cachedConstructor = (CachedConstructor)other;
            return this.constructorConstant.equals(cachedConstructor.constructorConstant);
        }
    }

    protected static class CachedMethod
    implements StackManipulation {
        private static final TypeDescription METHOD_TYPE = TypeDescription.ForLoadedType.of(Method.class);
        private final StackManipulation methodConstant;

        protected CachedMethod(StackManipulation methodConstant) {
            this.methodConstant = methodConstant;
        }

        public boolean isValid() {
            return this.methodConstant.isValid();
        }

        public StackManipulation.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext) {
            return FieldAccess.forField(implementationContext.cache(this.methodConstant, METHOD_TYPE)).read().apply(methodVisitor, implementationContext);
        }

        public int hashCode() {
            return this.methodConstant.hashCode();
        }

        public boolean equals(@MaybeNull Object other) {
            if (this == other) {
                return true;
            }
            if (other == null || this.getClass() != other.getClass()) {
                return false;
            }
            CachedMethod cachedMethod = (CachedMethod)other;
            return this.methodConstant.equals(cachedMethod.methodConstant);
        }
    }

    protected static class PrivilegedLookup
    implements StackManipulation,
    CanCache {
        private final MethodDescription.InDefinedShape methodDescription;
        private final StackManipulation methodName;

        protected PrivilegedLookup(MethodDescription.InDefinedShape methodDescription, StackManipulation methodName) {
            this.methodDescription = methodDescription;
            this.methodName = methodName;
        }

        public boolean isValid() {
            return this.methodName.isValid();
        }

        public StackManipulation.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext) {
            if (DO_PRIVILEGED == null) {
                throw new IllegalStateException("Privileged method invocation is not supported on the current VM");
            }
            TypeDescription auxiliaryType = implementationContext.register(PrivilegedMemberLookupAction.of(this.methodDescription));
            return new StackManipulation.Compound(TypeCreation.of(auxiliaryType), Duplication.SINGLE, ClassConstant.of(this.methodDescription.getDeclaringType()), this.methodName, ArrayFactory.forType(TypeDescription.Generic.OfNonGenericType.ForLoadedType.of(Class.class)).withValues(MethodConstant.typeConstantsFor(this.methodDescription.getParameters().asTypeList().asErasures())), MethodInvocation.invoke((MethodDescription.InDefinedShape)((MethodList)auxiliaryType.getDeclaredMethods().filter(ElementMatchers.isConstructor())).getOnly()), MethodInvocation.invoke(DO_PRIVILEGED), TypeCasting.to(TypeDescription.ForLoadedType.of(this.methodDescription.isConstructor() ? Constructor.class : Method.class))).apply(methodVisitor, implementationContext);
        }

        public StackManipulation cached() {
            return this.methodDescription.isConstructor() ? new CachedConstructor(this) : new CachedMethod(this);
        }

        public int hashCode() {
            return this.methodDescription.hashCode();
        }

        public boolean equals(@MaybeNull Object other) {
            if (this == other) {
                return true;
            }
            if (other == null || this.getClass() != other.getClass()) {
                return false;
            }
            PrivilegedLookup privilegedLookup = (PrivilegedLookup)other;
            return this.methodDescription.equals(privilegedLookup.methodDescription);
        }
    }

    protected static class ForConstructor
    extends MethodConstant
    implements CanCache {
        private static final MethodDescription.InDefinedShape GET_CONSTRUCTOR;
        private static final MethodDescription.InDefinedShape GET_DECLARED_CONSTRUCTOR;

        protected ForConstructor(MethodDescription.InDefinedShape methodDescription) {
            super(methodDescription);
        }

        protected StackManipulation methodName() {
            return StackManipulation.Trivial.INSTANCE;
        }

        protected MethodDescription.InDefinedShape accessorMethod() {
            return this.methodDescription.isPublic() ? GET_CONSTRUCTOR : GET_DECLARED_CONSTRUCTOR;
        }

        public StackManipulation cached() {
            return new CachedConstructor(this);
        }

        static {
            try {
                GET_CONSTRUCTOR = new MethodDescription.ForLoadedMethod(Class.class.getMethod("getConstructor", Class[].class));
                GET_DECLARED_CONSTRUCTOR = new MethodDescription.ForLoadedMethod(Class.class.getMethod("getDeclaredConstructor", Class[].class));
            }
            catch (NoSuchMethodException exception) {
                throw new IllegalStateException("Could not locate Class::getDeclaredConstructor", exception);
            }
        }
    }

    protected static class ForMethod
    extends MethodConstant
    implements CanCache {
        private static final MethodDescription.InDefinedShape GET_METHOD;
        private static final MethodDescription.InDefinedShape GET_DECLARED_METHOD;

        protected ForMethod(MethodDescription.InDefinedShape methodDescription) {
            super(methodDescription);
        }

        protected StackManipulation methodName() {
            return new TextConstant(this.methodDescription.getInternalName());
        }

        protected MethodDescription.InDefinedShape accessorMethod() {
            return this.methodDescription.isPublic() ? GET_METHOD : GET_DECLARED_METHOD;
        }

        public StackManipulation cached() {
            return new CachedMethod(this);
        }

        static {
            try {
                GET_METHOD = new MethodDescription.ForLoadedMethod(Class.class.getMethod("getMethod", String.class, Class[].class));
                GET_DECLARED_METHOD = new MethodDescription.ForLoadedMethod(Class.class.getMethod("getDeclaredMethod", String.class, Class[].class));
            }
            catch (NoSuchMethodException exception) {
                throw new IllegalStateException("Could not locate method lookup", exception);
            }
        }
    }

    public static interface CanCache
    extends StackManipulation {
        public StackManipulation cached();
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    protected static enum CanCacheIllegal implements CanCache
    {
        INSTANCE;


        @Override
        public StackManipulation cached() {
            return StackManipulation.Illegal.INSTANCE;
        }

        @Override
        public boolean isValid() {
            return false;
        }

        @Override
        public StackManipulation.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext) {
            return StackManipulation.Illegal.INSTANCE.apply(methodVisitor, implementationContext);
        }
    }
}

