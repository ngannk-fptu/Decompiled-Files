/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package net.bytebuddy.utility;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import net.bytebuddy.ClassFileVersion;
import net.bytebuddy.build.AccessControllerPlugin;
import net.bytebuddy.description.enumeration.EnumerationDescription;
import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.description.type.TypeList;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.implementation.bytecode.constant.ClassConstant;
import net.bytebuddy.implementation.bytecode.constant.DoubleConstant;
import net.bytebuddy.implementation.bytecode.constant.FloatConstant;
import net.bytebuddy.implementation.bytecode.constant.IntegerConstant;
import net.bytebuddy.implementation.bytecode.constant.JavaConstantValue;
import net.bytebuddy.implementation.bytecode.constant.LongConstant;
import net.bytebuddy.implementation.bytecode.constant.TextConstant;
import net.bytebuddy.jar.asm.Type;
import net.bytebuddy.pool.TypePool;
import net.bytebuddy.utility.CompoundList;
import net.bytebuddy.utility.ConstantValue;
import net.bytebuddy.utility.JavaType;
import net.bytebuddy.utility.dispatcher.JavaDispatcher;
import net.bytebuddy.utility.nullability.MaybeNull;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface JavaConstant
extends ConstantValue {
    public Object toDescription();

    public <T> T accept(Visitor<T> var1);

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class Dynamic
    implements JavaConstant {
        public static final String DEFAULT_NAME = "_";
        private final String name;
        private final TypeDescription typeDescription;
        private final MethodHandle bootstrap;
        private final List<JavaConstant> arguments;

        protected Dynamic(String name, TypeDescription typeDescription, MethodHandle bootstrap, List<JavaConstant> arguments) {
            this.name = name;
            this.typeDescription = typeDescription;
            this.bootstrap = bootstrap;
            this.arguments = arguments;
        }

        public static Dynamic ofNullConstant() {
            return new Dynamic(DEFAULT_NAME, TypeDescription.ForLoadedType.of(Object.class), new MethodHandle(MethodHandle.HandleType.INVOKE_STATIC, JavaType.CONSTANT_BOOTSTRAPS.getTypeStub(), "nullConstant", TypeDescription.ForLoadedType.of(Object.class), Arrays.asList(JavaType.METHOD_HANDLES_LOOKUP.getTypeStub(), TypeDescription.ForLoadedType.of(String.class), TypeDescription.ForLoadedType.of(Class.class))), Collections.<JavaConstant>emptyList());
        }

        public static JavaConstant ofPrimitiveType(Class<?> type) {
            return Dynamic.ofPrimitiveType(TypeDescription.ForLoadedType.of(type));
        }

        public static JavaConstant ofPrimitiveType(TypeDescription typeDescription) {
            if (!typeDescription.isPrimitive()) {
                throw new IllegalArgumentException("Not a primitive type: " + typeDescription);
            }
            return new Dynamic(typeDescription.getDescriptor(), TypeDescription.ForLoadedType.of(Class.class), new MethodHandle(MethodHandle.HandleType.INVOKE_STATIC, JavaType.CONSTANT_BOOTSTRAPS.getTypeStub(), "primitiveClass", TypeDescription.ForLoadedType.of(Class.class), Arrays.asList(JavaType.METHOD_HANDLES_LOOKUP.getTypeStub(), TypeDescription.ForLoadedType.of(String.class), TypeDescription.ForLoadedType.of(Class.class))), Collections.<JavaConstant>emptyList());
        }

        public static JavaConstant ofEnumeration(Enum<?> enumeration) {
            return Dynamic.ofEnumeration(new EnumerationDescription.ForLoadedEnumeration(enumeration));
        }

        public static JavaConstant ofEnumeration(EnumerationDescription enumerationDescription) {
            return new Dynamic(enumerationDescription.getValue(), enumerationDescription.getEnumerationType(), new MethodHandle(MethodHandle.HandleType.INVOKE_STATIC, JavaType.CONSTANT_BOOTSTRAPS.getTypeStub(), "enumConstant", TypeDescription.ForLoadedType.of(Enum.class), Arrays.asList(JavaType.METHOD_HANDLES_LOOKUP.getTypeStub(), TypeDescription.ForLoadedType.of(String.class), TypeDescription.ForLoadedType.of(Class.class))), Collections.<JavaConstant>emptyList());
        }

        public static Dynamic ofField(Field field) {
            return Dynamic.ofField(new FieldDescription.ForLoadedField(field));
        }

        public static Dynamic ofField(FieldDescription.InDefinedShape fieldDescription) {
            if (!fieldDescription.isStatic() || !fieldDescription.isFinal()) {
                throw new IllegalArgumentException("Field must be static and final: " + fieldDescription);
            }
            boolean selfDeclared = fieldDescription.getType().isPrimitive() ? fieldDescription.getType().asErasure().asBoxed().equals(fieldDescription.getType().asErasure()) : fieldDescription.getDeclaringType().equals(fieldDescription.getType().asErasure());
            return new Dynamic(fieldDescription.getInternalName(), fieldDescription.getType().asErasure(), new MethodHandle(MethodHandle.HandleType.INVOKE_STATIC, JavaType.CONSTANT_BOOTSTRAPS.getTypeStub(), "getStaticFinal", TypeDescription.ForLoadedType.of(Object.class), selfDeclared ? Arrays.asList(JavaType.METHOD_HANDLES_LOOKUP.getTypeStub(), TypeDescription.ForLoadedType.of(String.class), TypeDescription.ForLoadedType.of(Class.class)) : Arrays.asList(JavaType.METHOD_HANDLES_LOOKUP.getTypeStub(), TypeDescription.ForLoadedType.of(String.class), TypeDescription.ForLoadedType.of(Class.class), TypeDescription.ForLoadedType.of(Class.class))), selfDeclared ? Collections.emptyList() : Collections.singletonList(Simple.of(fieldDescription.getDeclaringType())));
        }

        public static Dynamic ofInvocation(Method method, Object ... constant) {
            return Dynamic.ofInvocation(method, Arrays.asList(constant));
        }

        public static Dynamic ofInvocation(Method method, List<?> constants) {
            return Dynamic.ofInvocation((MethodDescription.InDefinedShape)new MethodDescription.ForLoadedMethod(method), constants);
        }

        public static Dynamic ofInvocation(Constructor<?> constructor, Object ... constant) {
            return Dynamic.ofInvocation(constructor, Arrays.asList(constant));
        }

        public static Dynamic ofInvocation(Constructor<?> constructor, List<?> constants) {
            return Dynamic.ofInvocation((MethodDescription.InDefinedShape)new MethodDescription.ForLoadedConstructor(constructor), constants);
        }

        public static Dynamic ofInvocation(MethodDescription.InDefinedShape methodDescription, Object ... constant) {
            return Dynamic.ofInvocation(methodDescription, Arrays.asList(constant));
        }

        public static Dynamic ofInvocation(MethodDescription.InDefinedShape methodDescription, List<?> constants) {
            if (!methodDescription.isConstructor() && methodDescription.getReturnType().represents(Void.TYPE)) {
                throw new IllegalArgumentException("Bootstrap method is no constructor or non-void static factory: " + methodDescription);
            }
            if (methodDescription.isVarArgs() ? methodDescription.getParameters().size() + (methodDescription.isStatic() || methodDescription.isConstructor() ? 0 : 1) > constants.size() + 1 : methodDescription.getParameters().size() + (methodDescription.isStatic() || methodDescription.isConstructor() ? 0 : 1) != constants.size()) {
                throw new IllegalArgumentException("Cannot assign " + constants + " to " + methodDescription);
            }
            TypeList parameters = methodDescription.isStatic() || methodDescription.isConstructor() ? methodDescription.getParameters().asTypeList().asErasures() : CompoundList.of(methodDescription.getDeclaringType(), methodDescription.getParameters().asTypeList().asErasures());
            Iterator<Object> iterator = methodDescription.isVarArgs() ? CompoundList.of(parameters.subList(0, parameters.size() - 1), Collections.nCopies(constants.size() - parameters.size() + 1, ((TypeDescription)parameters.get(parameters.size() - 1)).getComponentType())).iterator() : parameters.iterator();
            ArrayList<JavaConstant> arguments = new ArrayList<JavaConstant>(constants.size() + 1);
            arguments.add(MethodHandle.of(methodDescription));
            for (Object constant : constants) {
                JavaConstant argument = Simple.wrap(constant);
                if (!argument.getTypeDescription().isAssignableTo((TypeDescription)iterator.next())) {
                    throw new IllegalArgumentException("Cannot assign " + constants + " to " + methodDescription);
                }
                arguments.add(argument);
            }
            return new Dynamic(DEFAULT_NAME, methodDescription.isConstructor() ? methodDescription.getDeclaringType() : methodDescription.getReturnType().asErasure(), new MethodHandle(MethodHandle.HandleType.INVOKE_STATIC, JavaType.CONSTANT_BOOTSTRAPS.getTypeStub(), "invoke", TypeDescription.ForLoadedType.of(Object.class), Arrays.asList(JavaType.METHOD_HANDLES_LOOKUP.getTypeStub(), TypeDescription.ForLoadedType.of(String.class), TypeDescription.ForLoadedType.of(Class.class), JavaType.METHOD_HANDLE.getTypeStub(), TypeDescription.ArrayProjection.of(TypeDescription.ForLoadedType.of(Object.class)))), arguments);
        }

        public static JavaConstant ofVarHandle(Field field) {
            return Dynamic.ofVarHandle(new FieldDescription.ForLoadedField(field));
        }

        public static JavaConstant ofVarHandle(FieldDescription.InDefinedShape fieldDescription) {
            return new Dynamic(fieldDescription.getInternalName(), JavaType.VAR_HANDLE.getTypeStub(), new MethodHandle(MethodHandle.HandleType.INVOKE_STATIC, JavaType.CONSTANT_BOOTSTRAPS.getTypeStub(), fieldDescription.isStatic() ? "staticFieldVarHandle" : "fieldVarHandle", JavaType.VAR_HANDLE.getTypeStub(), Arrays.asList(JavaType.METHOD_HANDLES_LOOKUP.getTypeStub(), TypeDescription.ForLoadedType.of(String.class), TypeDescription.ForLoadedType.of(Class.class), TypeDescription.ForLoadedType.of(Class.class), TypeDescription.ForLoadedType.of(Class.class))), Arrays.asList(Simple.of(fieldDescription.getDeclaringType()), Simple.of(fieldDescription.getType().asErasure())));
        }

        public static JavaConstant ofArrayVarHandle(Class<?> type) {
            return Dynamic.ofArrayVarHandle(TypeDescription.ForLoadedType.of(type));
        }

        public static JavaConstant ofArrayVarHandle(TypeDescription typeDescription) {
            if (!typeDescription.isArray()) {
                throw new IllegalArgumentException("Not an array type: " + typeDescription);
            }
            return new Dynamic(DEFAULT_NAME, JavaType.VAR_HANDLE.getTypeStub(), new MethodHandle(MethodHandle.HandleType.INVOKE_STATIC, JavaType.CONSTANT_BOOTSTRAPS.getTypeStub(), "arrayVarHandle", JavaType.VAR_HANDLE.getTypeStub(), Arrays.asList(JavaType.METHOD_HANDLES_LOOKUP.getTypeStub(), TypeDescription.ForLoadedType.of(String.class), TypeDescription.ForLoadedType.of(Class.class), TypeDescription.ForLoadedType.of(Class.class))), Collections.singletonList(Simple.of(typeDescription)));
        }

        public static Dynamic bootstrap(String name, Method method, Object ... constant) {
            return Dynamic.bootstrap(name, method, Arrays.asList(constant));
        }

        public static Dynamic bootstrap(String name, Method method, List<?> constants) {
            return Dynamic.bootstrap(name, (MethodDescription.InDefinedShape)new MethodDescription.ForLoadedMethod(method), constants);
        }

        public static Dynamic bootstrap(String name, Constructor<?> constructor, Object ... constant) {
            return Dynamic.bootstrap(name, constructor, Arrays.asList(constant));
        }

        public static Dynamic bootstrap(String name, Constructor<?> constructor, List<?> constants) {
            return Dynamic.bootstrap(name, (MethodDescription.InDefinedShape)new MethodDescription.ForLoadedConstructor(constructor), constants);
        }

        public static Dynamic bootstrap(String name, MethodDescription.InDefinedShape bootstrapMethod, Object ... constant) {
            return Dynamic.bootstrap(name, bootstrapMethod, Arrays.asList(constant));
        }

        public static Dynamic bootstrap(String name, MethodDescription.InDefinedShape bootstrap, List<?> arguments) {
            if (name.length() == 0 || name.contains(".")) {
                throw new IllegalArgumentException("Not a valid field name: " + name);
            }
            ArrayList<JavaConstant> constants = new ArrayList<JavaConstant>(arguments.size());
            for (Object argument : arguments) {
                constants.add(Simple.wrap(argument));
            }
            if (!bootstrap.isConstantBootstrap(TypeList.Explicit.of(constants))) {
                throw new IllegalArgumentException("Not a valid bootstrap method " + bootstrap + " for " + arguments);
            }
            return new Dynamic(name, bootstrap.isConstructor() ? bootstrap.getDeclaringType() : bootstrap.getReturnType().asErasure(), new MethodHandle(bootstrap.isConstructor() ? MethodHandle.HandleType.INVOKE_SPECIAL_CONSTRUCTOR : MethodHandle.HandleType.INVOKE_STATIC, bootstrap.getDeclaringType(), bootstrap.getInternalName(), bootstrap.getReturnType().asErasure(), bootstrap.getParameters().asTypeList().asErasures()), constants);
        }

        public String getName() {
            return this.name;
        }

        public MethodHandle getBootstrap() {
            return this.bootstrap;
        }

        public List<JavaConstant> getArguments() {
            return this.arguments;
        }

        public JavaConstant withType(Class<?> type) {
            return this.withType(TypeDescription.ForLoadedType.of(type));
        }

        public JavaConstant withType(TypeDescription typeDescription) {
            if (typeDescription.represents(Void.TYPE)) {
                throw new IllegalArgumentException("Constant value cannot represent void");
            }
            if (this.getBootstrap().getName().equals("<init>") ? !this.getTypeDescription().isAssignableTo(typeDescription) : !typeDescription.asBoxed().isInHierarchyWith(this.getTypeDescription().asBoxed())) {
                throw new IllegalArgumentException(typeDescription + " is not compatible with bootstrapped type " + this.getTypeDescription());
            }
            return new Dynamic(this.getName(), typeDescription, this.getBootstrap(), this.getArguments());
        }

        @Override
        public Object toDescription() {
            Object[] argument = Simple.CONSTANT_DESC.toArray(this.arguments.size());
            for (int index = 0; index < argument.length; ++index) {
                argument[index] = this.arguments.get(index).toDescription();
            }
            return Simple.DYNAMIC_CONSTANT_DESC.ofCanonical(Simple.METHOD_HANDLE_DESC.of(Simple.DIRECT_METHOD_HANDLE_DESC_KIND.valueOf(this.bootstrap.getHandleType().getIdentifier(), this.bootstrap.getOwnerType().isInterface()), Simple.CLASS_DESC.ofDescriptor(this.bootstrap.getOwnerType().getDescriptor()), this.bootstrap.getName(), this.bootstrap.getDescriptor()), this.getName(), Simple.CLASS_DESC.ofDescriptor(this.typeDescription.getDescriptor()), argument);
        }

        @Override
        public <T> T accept(Visitor<T> visitor) {
            return visitor.onDynamic(this);
        }

        @Override
        public TypeDescription getTypeDescription() {
            return this.typeDescription;
        }

        @Override
        public StackManipulation toStackManipulation() {
            return new JavaConstantValue(this);
        }

        public int hashCode() {
            int result = this.name.hashCode();
            result = 31 * result + this.typeDescription.hashCode();
            result = 31 * result + this.bootstrap.hashCode();
            result = 31 * result + this.arguments.hashCode();
            return result;
        }

        public boolean equals(@MaybeNull Object object) {
            if (this == object) {
                return true;
            }
            if (object == null || this.getClass() != object.getClass()) {
                return false;
            }
            Dynamic dynamic = (Dynamic)object;
            if (!this.name.equals(dynamic.name)) {
                return false;
            }
            if (!this.typeDescription.equals(dynamic.typeDescription)) {
                return false;
            }
            if (!this.bootstrap.equals(dynamic.bootstrap)) {
                return false;
            }
            return this.arguments.equals(dynamic.arguments);
        }

        public String toString() {
            StringBuilder stringBuilder = new StringBuilder().append(this.bootstrap.getOwnerType().getSimpleName()).append("::").append(this.bootstrap.getName()).append('(').append(this.name.equals(DEFAULT_NAME) ? "" : this.name).append('/');
            boolean first = true;
            for (JavaConstant constant : this.arguments) {
                if (first) {
                    first = false;
                } else {
                    stringBuilder.append(',');
                }
                stringBuilder.append(constant.toString());
            }
            return stringBuilder.append(')').append(this.typeDescription.getSimpleName()).toString();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class MethodHandle
    implements JavaConstant {
        protected static final MethodHandleInfo METHOD_HANDLE_INFO;
        protected static final MethodType METHOD_TYPE;
        protected static final MethodHandles METHOD_HANDLES;
        protected static final MethodHandles.Lookup METHOD_HANDLES_LOOKUP;
        private final HandleType handleType;
        private final TypeDescription ownerType;
        private final String name;
        private final TypeDescription returnType;
        private final List<? extends TypeDescription> parameterTypes;
        private static final boolean ACCESS_CONTROLLER;

        public MethodHandle(HandleType handleType, TypeDescription ownerType, String name, TypeDescription returnType, List<? extends TypeDescription> parameterTypes) {
            this.handleType = handleType;
            this.ownerType = ownerType;
            this.name = name;
            this.returnType = returnType;
            this.parameterTypes = parameterTypes;
        }

        @AccessControllerPlugin.Enhance
        private static <T> T doPrivileged(PrivilegedAction<T> privilegedAction) {
            PrivilegedAction<T> action;
            if (ACCESS_CONTROLLER) {
                return AccessController.doPrivileged(privilegedAction);
            }
            return action.run();
        }

        public static MethodHandle ofLoaded(Object methodHandle) {
            return MethodHandle.ofLoaded(methodHandle, METHOD_HANDLES.publicLookup());
        }

        public static MethodHandle ofLoaded(Object methodHandle, Object lookup) {
            if (!JavaType.METHOD_HANDLE.isInstance(methodHandle)) {
                throw new IllegalArgumentException("Expected method handle object: " + methodHandle);
            }
            if (!JavaType.METHOD_HANDLES_LOOKUP.isInstance(lookup)) {
                throw new IllegalArgumentException("Expected method handle lookup object: " + lookup);
            }
            Object methodHandleInfo = ClassFileVersion.ofThisVm(ClassFileVersion.JAVA_V8).isAtMost(ClassFileVersion.JAVA_V7) ? METHOD_HANDLE_INFO.revealDirect(methodHandle) : METHOD_HANDLES_LOOKUP.revealDirect(lookup, methodHandle);
            Object methodType = METHOD_HANDLE_INFO.getMethodType(methodHandleInfo);
            return new MethodHandle(HandleType.of(METHOD_HANDLE_INFO.getReferenceKind(methodHandleInfo)), TypeDescription.ForLoadedType.of(METHOD_HANDLE_INFO.getDeclaringClass(methodHandleInfo)), METHOD_HANDLE_INFO.getName(methodHandleInfo), TypeDescription.ForLoadedType.of(METHOD_TYPE.returnType(methodType)), new TypeList.ForLoadedTypes(METHOD_TYPE.parameterArray(methodType)));
        }

        public static MethodHandle of(Method method) {
            return MethodHandle.of(new MethodDescription.ForLoadedMethod(method));
        }

        public static MethodHandle of(Constructor<?> constructor) {
            return MethodHandle.of(new MethodDescription.ForLoadedConstructor(constructor));
        }

        public static MethodHandle of(MethodDescription.InDefinedShape methodDescription) {
            return new MethodHandle(HandleType.of(methodDescription), methodDescription.getDeclaringType().asErasure(), methodDescription.getInternalName(), methodDescription.getReturnType().asErasure(), methodDescription.getParameters().asTypeList().asErasures());
        }

        public static MethodHandle ofSpecial(Method method, Class<?> type) {
            return MethodHandle.ofSpecial(new MethodDescription.ForLoadedMethod(method), TypeDescription.ForLoadedType.of(type));
        }

        public static MethodHandle ofSpecial(MethodDescription.InDefinedShape methodDescription, TypeDescription typeDescription) {
            if (!methodDescription.isSpecializableFor(typeDescription)) {
                throw new IllegalArgumentException("Cannot specialize " + methodDescription + " for " + typeDescription);
            }
            return new MethodHandle(HandleType.ofSpecial(methodDescription), typeDescription, methodDescription.getInternalName(), methodDescription.getReturnType().asErasure(), methodDescription.getParameters().asTypeList().asErasures());
        }

        public static MethodHandle ofGetter(Field field) {
            return MethodHandle.ofGetter(new FieldDescription.ForLoadedField(field));
        }

        public static MethodHandle ofGetter(FieldDescription.InDefinedShape fieldDescription) {
            return new MethodHandle(HandleType.ofGetter(fieldDescription), fieldDescription.getDeclaringType().asErasure(), fieldDescription.getInternalName(), fieldDescription.getType().asErasure(), Collections.emptyList());
        }

        public static MethodHandle ofSetter(Field field) {
            return MethodHandle.ofSetter(new FieldDescription.ForLoadedField(field));
        }

        public static MethodHandle ofSetter(FieldDescription.InDefinedShape fieldDescription) {
            return new MethodHandle(HandleType.ofSetter(fieldDescription), fieldDescription.getDeclaringType().asErasure(), fieldDescription.getInternalName(), TypeDescription.ForLoadedType.of(Void.TYPE), Collections.singletonList(fieldDescription.getType().asErasure()));
        }

        public static Class<?> lookupType(Object callerClassLookup) {
            return METHOD_HANDLES_LOOKUP.lookupClass(callerClassLookup);
        }

        @Override
        public Object toDescription() {
            return Simple.METHOD_HANDLE_DESC.of(Simple.DIRECT_METHOD_HANDLE_DESC_KIND.valueOf(this.handleType.getIdentifier(), this.ownerType.isInterface()), Simple.CLASS_DESC.ofDescriptor(this.ownerType.getDescriptor()), this.name, this.getDescriptor());
        }

        @Override
        public <T> T accept(Visitor<T> visitor) {
            return visitor.onMethodHandle(this);
        }

        @Override
        public TypeDescription getTypeDescription() {
            return JavaType.METHOD_HANDLE.getTypeStub();
        }

        @Override
        public StackManipulation toStackManipulation() {
            return new JavaConstantValue(this);
        }

        public HandleType getHandleType() {
            return this.handleType;
        }

        public TypeDescription getOwnerType() {
            return this.ownerType;
        }

        public String getName() {
            return this.name;
        }

        public TypeDescription getReturnType() {
            return this.returnType;
        }

        public TypeList getParameterTypes() {
            return new TypeList.Explicit(this.parameterTypes);
        }

        public String getDescriptor() {
            switch (this.handleType) {
                case GET_FIELD: 
                case GET_STATIC_FIELD: {
                    return this.returnType.getDescriptor();
                }
                case PUT_FIELD: 
                case PUT_STATIC_FIELD: {
                    return this.parameterTypes.get(0).getDescriptor();
                }
            }
            StringBuilder stringBuilder = new StringBuilder().append('(');
            for (TypeDescription typeDescription : this.parameterTypes) {
                stringBuilder.append(typeDescription.getDescriptor());
            }
            return stringBuilder.append(')').append(this.returnType.getDescriptor()).toString();
        }

        public int hashCode() {
            int result = this.handleType.hashCode();
            result = 31 * result + this.ownerType.hashCode();
            result = 31 * result + this.name.hashCode();
            result = 31 * result + this.returnType.hashCode();
            result = 31 * result + this.parameterTypes.hashCode();
            return result;
        }

        public boolean equals(@MaybeNull Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof MethodHandle)) {
                return false;
            }
            MethodHandle methodHandle = (MethodHandle)other;
            return this.handleType == methodHandle.handleType && this.name.equals(methodHandle.name) && this.ownerType.equals(methodHandle.ownerType) && this.parameterTypes.equals(methodHandle.parameterTypes) && this.returnType.equals(methodHandle.returnType);
        }

        public String toString() {
            StringBuilder stringBuilder = new StringBuilder().append(this.handleType.name()).append(this.ownerType.isInterface() && !this.handleType.isField() && this.handleType != HandleType.INVOKE_INTERFACE ? "@interface" : "").append('/').append(this.ownerType.getSimpleName()).append("::").append(this.name).append('(');
            boolean first = true;
            for (TypeDescription typeDescription : this.parameterTypes) {
                if (first) {
                    first = false;
                } else {
                    stringBuilder.append(',');
                }
                stringBuilder.append(typeDescription.getSimpleName());
            }
            return stringBuilder.append(')').append(this.returnType.getSimpleName()).toString();
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
            METHOD_HANDLE_INFO = MethodHandle.doPrivileged(JavaDispatcher.of(MethodHandleInfo.class));
            METHOD_TYPE = MethodHandle.doPrivileged(JavaDispatcher.of(MethodType.class));
            METHOD_HANDLES = MethodHandle.doPrivileged(JavaDispatcher.of(MethodHandles.class));
            METHOD_HANDLES_LOOKUP = MethodHandle.doPrivileged(JavaDispatcher.of(MethodHandles.Lookup.class));
        }

        @JavaDispatcher.Proxied(value="java.lang.invoke.MethodHandles")
        protected static interface MethodHandles {
            @JavaDispatcher.IsStatic
            @JavaDispatcher.Proxied(value="publicLookup")
            public Object publicLookup();

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            @JavaDispatcher.Proxied(value="java.lang.invoke.MethodHandles$Lookup")
            public static interface Lookup {
                @JavaDispatcher.Proxied(value="lookupClass")
                public Class<?> lookupClass(Object var1);

                @JavaDispatcher.Proxied(value="revealDirect")
                public Object revealDirect(Object var1, @JavaDispatcher.Proxied(value="java.lang.invoke.MethodHandle") Object var2);
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        @JavaDispatcher.Proxied(value="java.lang.invoke.MethodType")
        protected static interface MethodType {
            @JavaDispatcher.Proxied(value="returnType")
            public Class<?> returnType(Object var1);

            @JavaDispatcher.Proxied(value="parameterArray")
            public Class<?>[] parameterArray(Object var1);
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        @JavaDispatcher.Proxied(value="java.lang.invoke.MethodHandleInfo")
        protected static interface MethodHandleInfo {
            @JavaDispatcher.Proxied(value="getName")
            public String getName(Object var1);

            @JavaDispatcher.Proxied(value="getDeclaringClass")
            public Class<?> getDeclaringClass(Object var1);

            @JavaDispatcher.Proxied(value="getReferenceKind")
            public int getReferenceKind(Object var1);

            @JavaDispatcher.Proxied(value="getMethodType")
            public Object getMethodType(Object var1);

            @JavaDispatcher.IsConstructor
            @JavaDispatcher.Proxied(value="revealDirect")
            public Object revealDirect(@JavaDispatcher.Proxied(value="java.lang.invoke.MethodHandle") Object var1);
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static enum HandleType {
            INVOKE_VIRTUAL(5, false),
            INVOKE_STATIC(6, false),
            INVOKE_SPECIAL(7, false),
            INVOKE_INTERFACE(9, false),
            INVOKE_SPECIAL_CONSTRUCTOR(8, false),
            PUT_FIELD(3, true),
            GET_FIELD(1, true),
            PUT_STATIC_FIELD(4, true),
            GET_STATIC_FIELD(2, true);

            private final int identifier;
            private final boolean field;

            private HandleType(int identifier, boolean field) {
                this.identifier = identifier;
                this.field = field;
            }

            protected static HandleType of(MethodDescription.InDefinedShape methodDescription) {
                if (methodDescription.isTypeInitializer()) {
                    throw new IllegalArgumentException("Cannot create handle of type initializer " + methodDescription);
                }
                if (methodDescription.isStatic()) {
                    return INVOKE_STATIC;
                }
                if (methodDescription.isConstructor()) {
                    return INVOKE_SPECIAL_CONSTRUCTOR;
                }
                if (methodDescription.isPrivate()) {
                    return INVOKE_SPECIAL;
                }
                if (methodDescription.getDeclaringType().isInterface()) {
                    return INVOKE_INTERFACE;
                }
                return INVOKE_VIRTUAL;
            }

            protected static HandleType of(int identifier) {
                for (HandleType handleType : HandleType.values()) {
                    if (handleType.getIdentifier() != identifier) continue;
                    return handleType;
                }
                throw new IllegalArgumentException("Unknown handle type: " + identifier);
            }

            protected static HandleType ofSpecial(MethodDescription.InDefinedShape methodDescription) {
                if (methodDescription.isStatic() || methodDescription.isAbstract()) {
                    throw new IllegalArgumentException("Cannot invoke " + methodDescription + " via invokespecial");
                }
                return methodDescription.isConstructor() ? INVOKE_SPECIAL_CONSTRUCTOR : INVOKE_SPECIAL;
            }

            protected static HandleType ofGetter(FieldDescription.InDefinedShape fieldDescription) {
                return fieldDescription.isStatic() ? GET_STATIC_FIELD : GET_FIELD;
            }

            protected static HandleType ofSetter(FieldDescription.InDefinedShape fieldDescription) {
                return fieldDescription.isStatic() ? PUT_STATIC_FIELD : PUT_FIELD;
            }

            public int getIdentifier() {
                return this.identifier;
            }

            public boolean isField() {
                return this.field;
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class MethodType
    implements JavaConstant {
        private static final Dispatcher DISPATCHER;
        private final TypeDescription returnType;
        private final List<? extends TypeDescription> parameterTypes;
        private static final boolean ACCESS_CONTROLLER;

        protected MethodType(TypeDescription returnType, List<? extends TypeDescription> parameterTypes) {
            this.returnType = returnType;
            this.parameterTypes = parameterTypes;
        }

        @AccessControllerPlugin.Enhance
        private static <T> T doPrivileged(PrivilegedAction<T> privilegedAction) {
            PrivilegedAction<T> action;
            if (ACCESS_CONTROLLER) {
                return AccessController.doPrivileged(privilegedAction);
            }
            return action.run();
        }

        public static MethodType ofLoaded(Object methodType) {
            if (!JavaType.METHOD_TYPE.isInstance(methodType)) {
                throw new IllegalArgumentException("Expected method type object: " + methodType);
            }
            return MethodType.of(DISPATCHER.returnType(methodType), DISPATCHER.parameterArray(methodType));
        }

        public static MethodType of(Class<?> returnType, Class<?> ... parameterType) {
            return MethodType.of(TypeDescription.ForLoadedType.of(returnType), new TypeList.ForLoadedTypes(parameterType));
        }

        public static MethodType of(TypeDescription returnType, TypeDescription ... parameterType) {
            return new MethodType(returnType, Arrays.asList(parameterType));
        }

        public static MethodType of(TypeDescription returnType, List<? extends TypeDescription> parameterTypes) {
            return new MethodType(returnType, parameterTypes);
        }

        public static MethodType of(Method method) {
            return MethodType.of(new MethodDescription.ForLoadedMethod(method));
        }

        public static MethodType of(Constructor<?> constructor) {
            return MethodType.of(new MethodDescription.ForLoadedConstructor(constructor));
        }

        public static MethodType of(MethodDescription methodDescription) {
            return new MethodType((methodDescription.isConstructor() ? methodDescription.getDeclaringType() : methodDescription.getReturnType()).asErasure(), (List<? extends TypeDescription>)(methodDescription.isStatic() || methodDescription.isConstructor() ? methodDescription.getParameters().asTypeList().asErasures() : CompoundList.of(methodDescription.getDeclaringType().asErasure(), methodDescription.getParameters().asTypeList().asErasures())));
        }

        public static MethodType ofSignature(Method method) {
            return MethodType.ofSignature(new MethodDescription.ForLoadedMethod(method));
        }

        public static MethodType ofSignature(Constructor<?> constructor) {
            return MethodType.ofSignature(new MethodDescription.ForLoadedConstructor(constructor));
        }

        public static MethodType ofSignature(MethodDescription methodDescription) {
            return new MethodType(methodDescription.getReturnType().asErasure(), methodDescription.getParameters().asTypeList().asErasures());
        }

        public static MethodType ofSetter(Field field) {
            return MethodType.ofSetter(new FieldDescription.ForLoadedField(field));
        }

        @SuppressFBWarnings(value={"NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE"}, justification="Assuming declaring type for type member.")
        public static MethodType ofSetter(FieldDescription fieldDescription) {
            return new MethodType(TypeDescription.ForLoadedType.of(Void.TYPE), fieldDescription.isStatic() ? Collections.singletonList(fieldDescription.getType().asErasure()) : Arrays.asList(fieldDescription.getDeclaringType().asErasure(), fieldDescription.getType().asErasure()));
        }

        public static MethodType ofGetter(Field field) {
            return MethodType.ofGetter(new FieldDescription.ForLoadedField(field));
        }

        @SuppressFBWarnings(value={"NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE"}, justification="Assuming declaring type for type member.")
        public static MethodType ofGetter(FieldDescription fieldDescription) {
            return new MethodType(fieldDescription.getType().asErasure(), fieldDescription.isStatic() ? Collections.emptyList() : Collections.singletonList(fieldDescription.getDeclaringType().asErasure()));
        }

        public static MethodType ofConstant(Object instance) {
            return MethodType.ofConstant(instance.getClass());
        }

        public static MethodType ofConstant(Class<?> type) {
            return MethodType.ofConstant(TypeDescription.ForLoadedType.of(type));
        }

        public static MethodType ofConstant(TypeDescription typeDescription) {
            return new MethodType(typeDescription, Collections.emptyList());
        }

        public TypeDescription getReturnType() {
            return this.returnType;
        }

        public TypeList getParameterTypes() {
            return new TypeList.Explicit(this.parameterTypes);
        }

        public String getDescriptor() {
            StringBuilder stringBuilder = new StringBuilder("(");
            for (TypeDescription typeDescription : this.parameterTypes) {
                stringBuilder.append(typeDescription.getDescriptor());
            }
            return stringBuilder.append(')').append(this.returnType.getDescriptor()).toString();
        }

        @Override
        public Object toDescription() {
            Object[] parameterType = Simple.CLASS_DESC.toArray(this.parameterTypes.size());
            for (int index = 0; index < this.parameterTypes.size(); ++index) {
                parameterType[index] = Simple.CLASS_DESC.ofDescriptor(this.parameterTypes.get(index).getDescriptor());
            }
            return Simple.METHOD_TYPE_DESC.of(Simple.CLASS_DESC.ofDescriptor(this.returnType.getDescriptor()), parameterType);
        }

        @Override
        public <T> T accept(Visitor<T> visitor) {
            return visitor.onMethodType(this);
        }

        @Override
        public TypeDescription getTypeDescription() {
            return JavaType.METHOD_TYPE.getTypeStub();
        }

        @Override
        public StackManipulation toStackManipulation() {
            return new JavaConstantValue(this);
        }

        public int hashCode() {
            int result = this.returnType.hashCode();
            result = 31 * result + this.parameterTypes.hashCode();
            return result;
        }

        public boolean equals(@MaybeNull Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof MethodType)) {
                return false;
            }
            MethodType methodType = (MethodType)other;
            return this.parameterTypes.equals(methodType.parameterTypes) && this.returnType.equals(methodType.returnType);
        }

        public String toString() {
            StringBuilder stringBuilder = new StringBuilder().append('(');
            boolean first = true;
            for (TypeDescription typeDescription : this.parameterTypes) {
                if (first) {
                    first = false;
                } else {
                    stringBuilder.append(',');
                }
                stringBuilder.append(typeDescription.getSimpleName());
            }
            return stringBuilder.append(')').append(this.returnType.getSimpleName()).toString();
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
            DISPATCHER = MethodType.doPrivileged(JavaDispatcher.of(Dispatcher.class));
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        @JavaDispatcher.Proxied(value="java.lang.invoke.MethodType")
        protected static interface Dispatcher {
            @JavaDispatcher.Proxied(value="returnType")
            public Class<?> returnType(Object var1);

            @JavaDispatcher.Proxied(value="parameterArray")
            public Class<?>[] parameterArray(Object var1);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static abstract class Simple<T>
    implements JavaConstant {
        protected static final Dispatcher CONSTANT_DESC;
        protected static final Dispatcher.OfClassDesc CLASS_DESC;
        protected static final Dispatcher.OfMethodTypeDesc METHOD_TYPE_DESC;
        protected static final Dispatcher.OfMethodHandleDesc METHOD_HANDLE_DESC;
        protected static final Dispatcher.OfDirectMethodHandleDesc DIRECT_METHOD_HANDLE_DESC;
        protected static final Dispatcher.OfDirectMethodHandleDesc.ForKind DIRECT_METHOD_HANDLE_DESC_KIND;
        protected static final Dispatcher.OfDynamicConstantDesc DYNAMIC_CONSTANT_DESC;
        protected final T value;
        private final TypeDescription typeDescription;
        private static final boolean ACCESS_CONTROLLER;

        protected Simple(T value, TypeDescription typeDescription) {
            this.value = value;
            this.typeDescription = typeDescription;
        }

        @AccessControllerPlugin.Enhance
        private static <T> T doPrivileged(PrivilegedAction<T> privilegedAction) {
            PrivilegedAction<T> action;
            if (ACCESS_CONTROLLER) {
                return AccessController.doPrivileged(privilegedAction);
            }
            return action.run();
        }

        public static JavaConstant ofLoaded(Object value) {
            JavaConstant constant = Simple.ofLoadedOrNull(value);
            if (constant == null) {
                throw new IllegalArgumentException("Not a constant: " + value);
            }
            return constant;
        }

        @MaybeNull
        protected static JavaConstant ofLoadedOrNull(Object value) {
            if (value instanceof Integer) {
                return new OfTrivialValue.ForInteger((Integer)value);
            }
            if (value instanceof Long) {
                return new OfTrivialValue.ForLong((Long)value);
            }
            if (value instanceof Float) {
                return new OfTrivialValue.ForFloat((Float)value);
            }
            if (value instanceof Double) {
                return new OfTrivialValue.ForDouble((Double)value);
            }
            if (value instanceof String) {
                return new OfTrivialValue.ForString((String)value);
            }
            if (value instanceof Class) {
                return Simple.of(TypeDescription.ForLoadedType.of((Class)value));
            }
            if (JavaType.METHOD_HANDLE.isInstance(value)) {
                return MethodHandle.ofLoaded(value);
            }
            if (JavaType.METHOD_TYPE.isInstance(value)) {
                return MethodType.ofLoaded(value);
            }
            return null;
        }

        public static JavaConstant ofDescription(Object value, @MaybeNull ClassLoader classLoader) {
            return Simple.ofDescription(value, ClassFileLocator.ForClassLoader.of(classLoader));
        }

        public static JavaConstant ofDescription(Object value, ClassFileLocator classFileLocator) {
            return Simple.ofDescription(value, TypePool.Default.WithLazyResolution.of(classFileLocator));
        }

        public static JavaConstant ofDescription(Object value, TypePool typePool) {
            if (value instanceof Integer) {
                return new OfTrivialValue.ForInteger((Integer)value);
            }
            if (value instanceof Long) {
                return new OfTrivialValue.ForLong((Long)value);
            }
            if (value instanceof Float) {
                return new OfTrivialValue.ForFloat((Float)value);
            }
            if (value instanceof Double) {
                return new OfTrivialValue.ForDouble((Double)value);
            }
            if (value instanceof String) {
                return new OfTrivialValue.ForString((String)value);
            }
            if (CLASS_DESC.isInstance(value)) {
                Type type = Type.getType(CLASS_DESC.descriptorString(value));
                return OfTypeDescription.of(typePool.describe(type.getSort() == 9 ? type.getInternalName().replace('/', '.') : type.getClassName()).resolve());
            }
            if (METHOD_TYPE_DESC.isInstance(value)) {
                Object[] parameterTypes = METHOD_TYPE_DESC.parameterArray(value);
                ArrayList<TypeDescription> typeDescriptions = new ArrayList<TypeDescription>(parameterTypes.length);
                for (Object parameterType : parameterTypes) {
                    Type type = Type.getType(CLASS_DESC.descriptorString(parameterType));
                    typeDescriptions.add(typePool.describe(type.getSort() == 9 ? type.getInternalName().replace('/', '.') : type.getClassName()).resolve());
                }
                Type type = Type.getType(CLASS_DESC.descriptorString(METHOD_TYPE_DESC.returnType(value)));
                return MethodType.of(typePool.describe(type.getSort() == 9 ? type.getInternalName().replace('/', '.') : type.getClassName()).resolve(), typeDescriptions);
            }
            if (DIRECT_METHOD_HANDLE_DESC.isInstance(value)) {
                Object[] parameterTypes = METHOD_TYPE_DESC.parameterArray(METHOD_HANDLE_DESC.invocationType(value));
                ArrayList<TypeDescription> typeDescriptions = new ArrayList<TypeDescription>(parameterTypes.length);
                for (Object parameterType : parameterTypes) {
                    Type type = Type.getType(CLASS_DESC.descriptorString(parameterType));
                    typeDescriptions.add(typePool.describe(type.getSort() == 9 ? type.getInternalName().replace('/', '.') : type.getClassName()).resolve());
                }
                Type type = Type.getType(CLASS_DESC.descriptorString(METHOD_TYPE_DESC.returnType(METHOD_HANDLE_DESC.invocationType(value))));
                return new MethodHandle(MethodHandle.HandleType.of(DIRECT_METHOD_HANDLE_DESC.refKind(value)), typePool.describe(Type.getType(CLASS_DESC.descriptorString(DIRECT_METHOD_HANDLE_DESC.owner(value))).getClassName()).resolve(), DIRECT_METHOD_HANDLE_DESC.methodName(value), DIRECT_METHOD_HANDLE_DESC.refKind(value) == 8 ? TypeDescription.ForLoadedType.of(Void.TYPE) : typePool.describe(type.getSort() == 9 ? type.getInternalName().replace('/', '.') : type.getClassName()).resolve(), typeDescriptions);
            }
            if (DYNAMIC_CONSTANT_DESC.isInstance(value)) {
                Type methodType = Type.getMethodType(DIRECT_METHOD_HANDLE_DESC.lookupDescriptor(DYNAMIC_CONSTANT_DESC.bootstrapMethod(value)));
                ArrayList<TypeDescription> parameterTypes = new ArrayList<TypeDescription>(methodType.getArgumentTypes().length);
                for (Type type : methodType.getArgumentTypes()) {
                    parameterTypes.add(typePool.describe(type.getSort() == 9 ? type.getInternalName().replace('/', '.') : type.getClassName()).resolve());
                }
                Object[] constant = DYNAMIC_CONSTANT_DESC.bootstrapArgs(value);
                ArrayList<JavaConstant> arguments = new ArrayList<JavaConstant>(constant.length);
                for (Object aConstant : constant) {
                    arguments.add(Simple.ofDescription(aConstant, typePool));
                }
                Type type = Type.getType(CLASS_DESC.descriptorString(DYNAMIC_CONSTANT_DESC.constantType(value)));
                return new Dynamic(DYNAMIC_CONSTANT_DESC.constantName(value), typePool.describe(type.getSort() == 9 ? type.getInternalName().replace('/', '.') : type.getClassName()).resolve(), new MethodHandle(MethodHandle.HandleType.of(DIRECT_METHOD_HANDLE_DESC.refKind(DYNAMIC_CONSTANT_DESC.bootstrapMethod(value))), typePool.describe(Type.getType(CLASS_DESC.descriptorString(DIRECT_METHOD_HANDLE_DESC.owner(DYNAMIC_CONSTANT_DESC.bootstrapMethod(value)))).getClassName()).resolve(), DIRECT_METHOD_HANDLE_DESC.methodName(DYNAMIC_CONSTANT_DESC.bootstrapMethod(value)), typePool.describe(methodType.getReturnType().getSort() == 9 ? methodType.getReturnType().getInternalName().replace('/', '.') : methodType.getReturnType().getClassName()).resolve(), parameterTypes), arguments);
            }
            throw new IllegalArgumentException("Not a resolvable constant description or not expressible as a constant pool value: " + value);
        }

        public static JavaConstant of(TypeDescription typeDescription) {
            if (typeDescription.isPrimitive()) {
                throw new IllegalArgumentException("A primitive type cannot be represented as a type constant: " + typeDescription);
            }
            return new OfTypeDescription(typeDescription);
        }

        public static JavaConstant wrap(Object value) {
            if (value instanceof JavaConstant) {
                return (JavaConstant)value;
            }
            if (value instanceof TypeDescription) {
                return Simple.of((TypeDescription)value);
            }
            return Simple.ofLoaded(value);
        }

        public static List<JavaConstant> wrap(List<?> values) {
            ArrayList<JavaConstant> constants = new ArrayList<JavaConstant>(values.size());
            for (Object value : values) {
                constants.add(Simple.wrap(value));
            }
            return constants;
        }

        public T getValue() {
            return this.value;
        }

        @Override
        public TypeDescription getTypeDescription() {
            return this.typeDescription;
        }

        public int hashCode() {
            return this.value.hashCode();
        }

        public boolean equals(@MaybeNull Object object) {
            if (this == object) {
                return true;
            }
            if (object == null || this.getClass() != object.getClass()) {
                return false;
            }
            return this.value.equals(((Simple)object).value);
        }

        public String toString() {
            return this.value.toString();
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
            CONSTANT_DESC = Simple.doPrivileged(JavaDispatcher.of(Dispatcher.class));
            CLASS_DESC = Simple.doPrivileged(JavaDispatcher.of(Dispatcher.OfClassDesc.class));
            METHOD_TYPE_DESC = Simple.doPrivileged(JavaDispatcher.of(Dispatcher.OfMethodTypeDesc.class));
            METHOD_HANDLE_DESC = Simple.doPrivileged(JavaDispatcher.of(Dispatcher.OfMethodHandleDesc.class));
            DIRECT_METHOD_HANDLE_DESC = Simple.doPrivileged(JavaDispatcher.of(Dispatcher.OfDirectMethodHandleDesc.class));
            DIRECT_METHOD_HANDLE_DESC_KIND = Simple.doPrivileged(JavaDispatcher.of(Dispatcher.OfDirectMethodHandleDesc.ForKind.class));
            DYNAMIC_CONSTANT_DESC = Simple.doPrivileged(JavaDispatcher.of(Dispatcher.OfDynamicConstantDesc.class));
        }

        @JavaDispatcher.Proxied(value="java.lang.constant.ConstantDesc")
        protected static interface Dispatcher {
            @JavaDispatcher.Instance
            @JavaDispatcher.Proxied(value="isInstance")
            public boolean isInstance(Object var1);

            @JavaDispatcher.Container
            @JavaDispatcher.Proxied(value="toArray")
            public Object[] toArray(int var1);

            @JavaDispatcher.Proxied(value="java.lang.constant.DynamicConstantDesc")
            public static interface OfDynamicConstantDesc
            extends Dispatcher {
                @JavaDispatcher.IsStatic
                @JavaDispatcher.Proxied(value="ofCanonical")
                public Object ofCanonical(@JavaDispatcher.Proxied(value="java.lang.constant.DirectMethodHandleDesc") Object var1, String var2, @JavaDispatcher.Proxied(value="java.lang.constant.ClassDesc") Object var3, @JavaDispatcher.Proxied(value="java.lang.constant.ConstantDesc") Object[] var4);

                @JavaDispatcher.Proxied(value="bootstrapArgs")
                public Object[] bootstrapArgs(Object var1);

                @JavaDispatcher.Proxied(value="constantName")
                public String constantName(Object var1);

                @JavaDispatcher.Proxied(value="constantType")
                public Object constantType(Object var1);

                @JavaDispatcher.Proxied(value="bootstrapMethod")
                public Object bootstrapMethod(Object var1);
            }

            @JavaDispatcher.Proxied(value="java.lang.constant.DirectMethodHandleDesc")
            public static interface OfDirectMethodHandleDesc
            extends Dispatcher {
                @JavaDispatcher.Proxied(value="refKind")
                public int refKind(Object var1);

                @JavaDispatcher.Proxied(value="methodName")
                public String methodName(Object var1);

                @JavaDispatcher.Proxied(value="owner")
                public Object owner(Object var1);

                @JavaDispatcher.Proxied(value="lookupDescriptor")
                public String lookupDescriptor(Object var1);

                @JavaDispatcher.Proxied(value="java.lang.constant.DirectMethodHandleDesc$Kind")
                public static interface ForKind {
                    @JavaDispatcher.IsStatic
                    @JavaDispatcher.Proxied(value="valueOf")
                    public Object valueOf(int var1, boolean var2);
                }
            }

            @JavaDispatcher.Proxied(value="java.lang.constant.MethodHandleDesc")
            public static interface OfMethodHandleDesc
            extends Dispatcher {
                @JavaDispatcher.IsStatic
                @JavaDispatcher.Proxied(value="of")
                public Object of(@JavaDispatcher.Proxied(value="java.lang.constant.DirectMethodHandleDesc$Kind") Object var1, @JavaDispatcher.Proxied(value="java.lang.constant.ClassDesc") Object var2, String var3, String var4);

                @JavaDispatcher.Proxied(value="invocationType")
                public Object invocationType(Object var1);
            }

            @JavaDispatcher.Proxied(value="java.lang.constant.MethodTypeDesc")
            public static interface OfMethodTypeDesc
            extends Dispatcher {
                @JavaDispatcher.IsStatic
                @JavaDispatcher.Proxied(value="of")
                public Object of(@JavaDispatcher.Proxied(value="java.lang.constant.ClassDesc") Object var1, @JavaDispatcher.Proxied(value="java.lang.constant.ClassDesc") Object[] var2);

                @JavaDispatcher.IsStatic
                @JavaDispatcher.Proxied(value="ofDescriptor")
                public Object ofDescriptor(String var1);

                @JavaDispatcher.Proxied(value="returnType")
                public Object returnType(Object var1);

                @JavaDispatcher.Proxied(value="parameterArray")
                public Object[] parameterArray(Object var1);
            }

            @JavaDispatcher.Proxied(value="java.lang.constant.ClassDesc")
            public static interface OfClassDesc
            extends Dispatcher {
                @JavaDispatcher.IsStatic
                @JavaDispatcher.Proxied(value="ofDescriptor")
                public Object ofDescriptor(String var1);

                @JavaDispatcher.Proxied(value="descriptorString")
                public String descriptorString(Object var1);
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        protected static class OfTypeDescription
        extends Simple<TypeDescription> {
            protected OfTypeDescription(TypeDescription value) {
                super(value, TypeDescription.ForLoadedType.of(Class.class));
            }

            @Override
            public Object toDescription() {
                return CLASS_DESC.ofDescriptor(((TypeDescription)this.value).getDescriptor());
            }

            @Override
            public StackManipulation toStackManipulation() {
                return ClassConstant.of((TypeDescription)this.value);
            }

            @Override
            public <T> T accept(Visitor<T> visitor) {
                return visitor.onType(this);
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        protected static abstract class OfTrivialValue<S>
        extends Simple<S> {
            protected OfTrivialValue(S value, TypeDescription typeDescription) {
                super(value, typeDescription);
            }

            @Override
            public Object toDescription() {
                return this.value;
            }

            @Override
            public <T> T accept(Visitor<T> visitor) {
                return visitor.onValue(this);
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            protected static class ForString
            extends OfTrivialValue<String> {
                public ForString(String value) {
                    super(value, TypeDescription.ForLoadedType.of(String.class));
                }

                @Override
                public StackManipulation toStackManipulation() {
                    return new TextConstant((String)this.value);
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            protected static class ForDouble
            extends OfTrivialValue<Double> {
                public ForDouble(Double value) {
                    super(value, TypeDescription.ForLoadedType.of(Double.TYPE));
                }

                @Override
                public StackManipulation toStackManipulation() {
                    return DoubleConstant.forValue((Double)this.value);
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            protected static class ForFloat
            extends OfTrivialValue<Float> {
                public ForFloat(Float value) {
                    super(value, TypeDescription.ForLoadedType.of(Float.TYPE));
                }

                @Override
                public StackManipulation toStackManipulation() {
                    return FloatConstant.forValue(((Float)this.value).floatValue());
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            protected static class ForLong
            extends OfTrivialValue<Long> {
                public ForLong(Long value) {
                    super(value, TypeDescription.ForLoadedType.of(Long.TYPE));
                }

                @Override
                public StackManipulation toStackManipulation() {
                    return LongConstant.forValue((Long)this.value);
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            protected static class ForInteger
            extends OfTrivialValue<Integer> {
                public ForInteger(Integer value) {
                    super(value, TypeDescription.ForLoadedType.of(Integer.TYPE));
                }

                @Override
                public StackManipulation toStackManipulation() {
                    return IntegerConstant.forValue((Integer)this.value);
                }
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static interface Visitor<T> {
        public T onValue(Simple<?> var1);

        public T onType(Simple<TypeDescription> var1);

        public T onMethodType(MethodType var1);

        public T onMethodHandle(MethodHandle var1);

        public T onDynamic(Dynamic var1);

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static enum NoOp implements Visitor<JavaConstant>
        {
            INSTANCE;


            @Override
            public JavaConstant onValue(Simple<?> constant) {
                return constant;
            }

            @Override
            public JavaConstant onType(Simple<TypeDescription> constant) {
                return constant;
            }

            @Override
            public JavaConstant onMethodType(MethodType constant) {
                return constant;
            }

            @Override
            public JavaConstant onMethodHandle(MethodHandle constant) {
                return constant;
            }

            @Override
            public JavaConstant onDynamic(Dynamic constant) {
                return constant;
            }
        }
    }
}

