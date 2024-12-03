/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package net.bytebuddy.description.type;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.GenericSignatureFormatError;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.bytebuddy.ClassFileVersion;
import net.bytebuddy.build.AccessControllerPlugin;
import net.bytebuddy.build.CachedReturnPlugin;
import net.bytebuddy.build.HashCodeAndEqualsPlugin;
import net.bytebuddy.description.ByteCodeElement;
import net.bytebuddy.description.ModifierReviewable;
import net.bytebuddy.description.TypeVariableSource;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.annotation.AnnotationList;
import net.bytebuddy.description.annotation.AnnotationSource;
import net.bytebuddy.description.enumeration.EnumerationDescription;
import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.field.FieldList;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.MethodList;
import net.bytebuddy.description.method.ParameterDescription;
import net.bytebuddy.description.type.PackageDescription;
import net.bytebuddy.description.type.RecordComponentDescription;
import net.bytebuddy.description.type.RecordComponentList;
import net.bytebuddy.description.type.TypeDefinition;
import net.bytebuddy.description.type.TypeList;
import net.bytebuddy.description.type.TypeVariableToken;
import net.bytebuddy.dynamic.TargetType;
import net.bytebuddy.implementation.bytecode.StackSize;
import net.bytebuddy.jar.asm.Type;
import net.bytebuddy.jar.asm.signature.SignatureVisitor;
import net.bytebuddy.jar.asm.signature.SignatureWriter;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.CompoundList;
import net.bytebuddy.utility.FieldComparator;
import net.bytebuddy.utility.GraalImageCode;
import net.bytebuddy.utility.JavaType;
import net.bytebuddy.utility.dispatcher.JavaDispatcher;
import net.bytebuddy.utility.nullability.AlwaysNull;
import net.bytebuddy.utility.nullability.MaybeNull;
import net.bytebuddy.utility.privilege.GetSystemPropertyAction;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface TypeDescription
extends TypeDefinition,
ByteCodeElement,
TypeVariableSource {
    @Deprecated
    public static final TypeDescription OBJECT = LazyProxy.of(Object.class);
    @Deprecated
    public static final TypeDescription STRING = LazyProxy.of(String.class);
    @Deprecated
    public static final TypeDescription CLASS = LazyProxy.of(Class.class);
    @Deprecated
    public static final TypeDescription THROWABLE = LazyProxy.of(Throwable.class);
    @Deprecated
    public static final TypeDescription VOID = LazyProxy.of(Void.TYPE);
    public static final TypeList.Generic ARRAY_INTERFACES = new TypeList.Generic.ForLoadedTypes(new java.lang.reflect.Type[]{Cloneable.class, Serializable.class});
    @AlwaysNull
    public static final TypeDescription UNDEFINED = null;

    public FieldList<FieldDescription.InDefinedShape> getDeclaredFields();

    public MethodList<MethodDescription.InDefinedShape> getDeclaredMethods();

    public RecordComponentList<RecordComponentDescription.InDefinedShape> getRecordComponents();

    public boolean isInstance(Object var1);

    public boolean isAssignableFrom(Class<?> var1);

    public boolean isAssignableFrom(TypeDescription var1);

    public boolean isAssignableTo(Class<?> var1);

    public boolean isAssignableTo(TypeDescription var1);

    public boolean isInHierarchyWith(Class<?> var1);

    public boolean isInHierarchyWith(TypeDescription var1);

    @Override
    @MaybeNull
    public TypeDescription getComponentType();

    @Override
    @MaybeNull
    public TypeDescription getDeclaringType();

    public TypeList getDeclaredTypes();

    @MaybeNull
    public MethodDescription.InDefinedShape getEnclosingMethod();

    @MaybeNull
    public TypeDescription getEnclosingType();

    public int getActualModifiers(boolean var1);

    public String getSimpleName();

    public String getLongSimpleName();

    @MaybeNull
    public String getCanonicalName();

    public boolean isAnonymousType();

    public boolean isLocalType();

    public boolean isMemberType();

    @MaybeNull
    public PackageDescription getPackage();

    public AnnotationList getInheritedAnnotations();

    public boolean isSamePackage(TypeDescription var1);

    public boolean isPrimitiveWrapper();

    public boolean isAnnotationReturnType();

    public boolean isAnnotationValue();

    public boolean isAnnotationValue(Object var1);

    public boolean isPackageType();

    public int getInnerClassCount();

    public boolean isInnerClass();

    public boolean isNestedClass();

    public TypeDescription asBoxed();

    public TypeDescription asUnboxed();

    @MaybeNull
    public Object getDefaultValue();

    public TypeDescription getNestHost();

    public TypeList getNestMembers();

    public boolean isNestHost();

    public boolean isNestMateOf(Class<?> var1);

    public boolean isNestMateOf(TypeDescription var1);

    public boolean isCompileTimeConstant();

    public TypeList getPermittedSubtypes();

    public boolean isSealed();

    @MaybeNull
    public ClassFileVersion getClassFileVersion();

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class SuperTypeLoading
    extends AbstractBase {
        private final TypeDescription delegate;
        @MaybeNull
        private final ClassLoader classLoader;
        private final ClassLoadingDelegate classLoadingDelegate;

        public SuperTypeLoading(TypeDescription delegate, @MaybeNull ClassLoader classLoader) {
            this(delegate, classLoader, ClassLoadingDelegate.Simple.INSTANCE);
        }

        public SuperTypeLoading(TypeDescription delegate, @MaybeNull ClassLoader classLoader, ClassLoadingDelegate classLoadingDelegate) {
            this.delegate = delegate;
            this.classLoader = classLoader;
            this.classLoadingDelegate = classLoadingDelegate;
        }

        @Override
        public AnnotationList getDeclaredAnnotations() {
            return this.delegate.getDeclaredAnnotations();
        }

        @Override
        public int getModifiers() {
            return this.delegate.getModifiers();
        }

        @Override
        public TypeList.Generic getTypeVariables() {
            return this.delegate.getTypeVariables();
        }

        @Override
        public String getDescriptor() {
            return this.delegate.getDescriptor();
        }

        @Override
        public String getName() {
            return this.delegate.getName();
        }

        @Override
        @MaybeNull
        public Generic getSuperClass() {
            Generic superClass = this.delegate.getSuperClass();
            return superClass == null ? Generic.UNDEFINED : new ClassLoadingTypeProjection(superClass, this.classLoader, this.classLoadingDelegate);
        }

        @Override
        public TypeList.Generic getInterfaces() {
            return new ClassLoadingTypeList(this.delegate.getInterfaces(), this.classLoader, this.classLoadingDelegate);
        }

        @Override
        public FieldList<FieldDescription.InDefinedShape> getDeclaredFields() {
            return this.delegate.getDeclaredFields();
        }

        @Override
        public MethodList<MethodDescription.InDefinedShape> getDeclaredMethods() {
            return this.delegate.getDeclaredMethods();
        }

        @Override
        public StackSize getStackSize() {
            return this.delegate.getStackSize();
        }

        @Override
        public boolean isArray() {
            return this.delegate.isArray();
        }

        @Override
        public boolean isPrimitive() {
            return this.delegate.isPrimitive();
        }

        @Override
        @MaybeNull
        public TypeDescription getComponentType() {
            return this.delegate.getComponentType();
        }

        @Override
        @MaybeNull
        public TypeDescription getDeclaringType() {
            return this.delegate.getDeclaringType();
        }

        @Override
        public TypeList getDeclaredTypes() {
            return this.delegate.getDeclaredTypes();
        }

        @Override
        @MaybeNull
        public MethodDescription.InDefinedShape getEnclosingMethod() {
            return this.delegate.getEnclosingMethod();
        }

        @Override
        @MaybeNull
        public TypeDescription getEnclosingType() {
            return this.delegate.getEnclosingType();
        }

        @Override
        public String getSimpleName() {
            return this.delegate.getSimpleName();
        }

        @Override
        @MaybeNull
        public String getCanonicalName() {
            return this.delegate.getCanonicalName();
        }

        @Override
        public boolean isAnonymousType() {
            return this.delegate.isAnonymousType();
        }

        @Override
        public boolean isLocalType() {
            return this.delegate.isLocalType();
        }

        @Override
        @MaybeNull
        public PackageDescription getPackage() {
            return this.delegate.getPackage();
        }

        @Override
        public TypeDescription getNestHost() {
            return this.delegate.getNestHost();
        }

        @Override
        public TypeList getNestMembers() {
            return this.delegate.getNestMembers();
        }

        @Override
        public RecordComponentList<RecordComponentDescription.InDefinedShape> getRecordComponents() {
            return this.delegate.getRecordComponents();
        }

        @Override
        public boolean isRecord() {
            return this.delegate.isRecord();
        }

        @Override
        public boolean isSealed() {
            return this.delegate.isSealed();
        }

        @Override
        public TypeList getPermittedSubtypes() {
            return this.delegate.getPermittedSubtypes();
        }

        @Override
        @MaybeNull
        public ClassFileVersion getClassFileVersion() {
            return this.delegate.getClassFileVersion();
        }

        protected static class ClassLoadingTypeList
        extends TypeList.Generic.AbstractBase {
            private final TypeList.Generic delegate;
            @MaybeNull
            private final ClassLoader classLoader;
            private final ClassLoadingDelegate classLoadingDelegate;

            protected ClassLoadingTypeList(TypeList.Generic delegate, @MaybeNull ClassLoader classLoader, ClassLoadingDelegate classLoadingDelegate) {
                this.delegate = delegate;
                this.classLoader = classLoader;
                this.classLoadingDelegate = classLoadingDelegate;
            }

            public Generic get(int index) {
                return new ClassLoadingTypeProjection((Generic)this.delegate.get(index), this.classLoader, this.classLoadingDelegate);
            }

            public int size() {
                return this.delegate.size();
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        protected static class ClassLoadingTypeProjection
        extends Generic.LazyProjection {
            private final Generic delegate;
            @MaybeNull
            private final ClassLoader classLoader;
            private final ClassLoadingDelegate classLoadingDelegate;
            private transient /* synthetic */ TypeDescription erasure;
            private transient /* synthetic */ Generic superClass;
            private transient /* synthetic */ TypeList.Generic interfaces;

            protected ClassLoadingTypeProjection(Generic delegate, @MaybeNull ClassLoader classLoader, ClassLoadingDelegate classLoadingDelegate) {
                this.delegate = delegate;
                this.classLoader = classLoader;
                this.classLoadingDelegate = classLoadingDelegate;
            }

            @Override
            public AnnotationList getDeclaredAnnotations() {
                return this.delegate.getDeclaredAnnotations();
            }

            @Override
            @CachedReturnPlugin.Enhance(value="erasure")
            public TypeDescription asErasure() {
                TypeDefinition typeDefinition;
                TypeDefinition typeDefinition2;
                TypeDescription typeDescription = this.erasure;
                if (typeDescription != null) {
                    typeDefinition2 = null;
                } else {
                    typeDefinition = this;
                    try {
                        typeDefinition2 = ForLoadedType.of(typeDefinition.classLoadingDelegate.load(typeDefinition.delegate.asErasure().getName(), typeDefinition.classLoader));
                    }
                    catch (ClassNotFoundException ignored) {
                        typeDefinition2 = typeDefinition = typeDefinition.delegate.asErasure();
                    }
                }
                if (typeDefinition == null) {
                    typeDefinition = this.erasure;
                } else {
                    this.erasure = typeDefinition;
                }
                return typeDefinition;
            }

            @Override
            protected Generic resolve() {
                return this.delegate;
            }

            @Override
            @MaybeNull
            @CachedReturnPlugin.Enhance(value="superClass")
            public Generic getSuperClass() {
                Generic generic;
                Generic generic2;
                Generic generic3 = this.superClass;
                if (generic3 != null) {
                    generic2 = null;
                } else {
                    generic = this;
                    Generic superClass = generic.delegate.getSuperClass();
                    if (superClass == null) {
                        generic2 = Generic.UNDEFINED;
                    } else {
                        try {
                            generic2 = new ClassLoadingTypeProjection(superClass, generic.classLoadingDelegate.load(generic.delegate.asErasure().getName(), generic.classLoader).getClassLoader(), generic.classLoadingDelegate);
                        }
                        catch (ClassNotFoundException ignored) {
                            generic2 = generic = superClass;
                        }
                    }
                }
                if (generic == null) {
                    generic = this.superClass;
                } else {
                    this.superClass = generic;
                }
                return generic;
            }

            @Override
            @CachedReturnPlugin.Enhance(value="interfaces")
            public TypeList.Generic getInterfaces() {
                Iterable<TypeDefinition> iterable;
                Iterable<Generic> iterable2;
                TypeList.Generic generic = this.interfaces;
                if (generic != null) {
                    iterable2 = null;
                } else {
                    iterable = this;
                    TypeList.Generic interfaces = iterable.delegate.getInterfaces();
                    try {
                        iterable2 = new ClassLoadingTypeList(interfaces, iterable.classLoadingDelegate.load(iterable.delegate.asErasure().getName(), iterable.classLoader).getClassLoader(), iterable.classLoadingDelegate);
                    }
                    catch (ClassNotFoundException ignored) {
                        iterable2 = iterable = interfaces;
                    }
                }
                if (iterable == null) {
                    iterable = this.interfaces;
                } else {
                    this.interfaces = iterable;
                }
                return iterable;
            }

            @Override
            public Iterator<TypeDefinition> iterator() {
                return new TypeDefinition.SuperClassIterator(this);
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static interface ClassLoadingDelegate {
            public Class<?> load(String var1, @MaybeNull ClassLoader var2) throws ClassNotFoundException;

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            public static enum Simple implements ClassLoadingDelegate
            {
                INSTANCE;


                @Override
                public Class<?> load(String name, @MaybeNull ClassLoader classLoader) throws ClassNotFoundException {
                    return Class.forName(name, false, classLoader);
                }
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class ForPackageDescription
    extends AbstractBase.OfSimpleType {
        private final PackageDescription packageDescription;

        public ForPackageDescription(PackageDescription packageDescription) {
            this.packageDescription = packageDescription;
        }

        @Override
        @MaybeNull
        public Generic getSuperClass() {
            return Generic.OfNonGenericType.ForLoadedType.of(Object.class);
        }

        @Override
        public TypeList.Generic getInterfaces() {
            return new TypeList.Generic.Empty();
        }

        @Override
        @MaybeNull
        public MethodDescription.InDefinedShape getEnclosingMethod() {
            return MethodDescription.UNDEFINED;
        }

        @Override
        @MaybeNull
        public TypeDescription getEnclosingType() {
            return UNDEFINED;
        }

        @Override
        public boolean isAnonymousType() {
            return false;
        }

        @Override
        public boolean isLocalType() {
            return false;
        }

        @Override
        public TypeList getDeclaredTypes() {
            return new TypeList.Empty();
        }

        @Override
        public FieldList<FieldDescription.InDefinedShape> getDeclaredFields() {
            return new FieldList.Empty<FieldDescription.InDefinedShape>();
        }

        @Override
        public MethodList<MethodDescription.InDefinedShape> getDeclaredMethods() {
            return new MethodList.Empty<MethodDescription.InDefinedShape>();
        }

        @Override
        public PackageDescription getPackage() {
            return this.packageDescription;
        }

        @Override
        public AnnotationList getDeclaredAnnotations() {
            return this.packageDescription.getDeclaredAnnotations();
        }

        @Override
        @MaybeNull
        public TypeDescription getDeclaringType() {
            return UNDEFINED;
        }

        @Override
        public TypeList.Generic getTypeVariables() {
            return new TypeList.Generic.Empty();
        }

        @Override
        public int getModifiers() {
            return 5632;
        }

        @Override
        public String getName() {
            return this.packageDescription.getName() + "." + "package-info";
        }

        @Override
        public TypeDescription getNestHost() {
            return this;
        }

        @Override
        public TypeList getNestMembers() {
            return new TypeList.Explicit(this);
        }

        @Override
        public RecordComponentList<RecordComponentDescription.InDefinedShape> getRecordComponents() {
            return new RecordComponentList.Empty<RecordComponentDescription.InDefinedShape>();
        }

        @Override
        public boolean isRecord() {
            return false;
        }

        @Override
        public TypeList getPermittedSubtypes() {
            return new TypeList.Empty();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class Latent
    extends AbstractBase.OfSimpleType {
        private final String name;
        private final int modifiers;
        @MaybeNull
        private final Generic superClass;
        private final List<? extends Generic> interfaces;

        public Latent(String name, int modifiers, @MaybeNull Generic superClass, Generic ... anInterface) {
            this(name, modifiers, superClass, Arrays.asList(anInterface));
        }

        public Latent(String name, int modifiers, @MaybeNull Generic superClass, List<? extends Generic> interfaces) {
            this.name = name;
            this.modifiers = modifiers;
            this.superClass = superClass;
            this.interfaces = interfaces;
        }

        @Override
        @MaybeNull
        public Generic getSuperClass() {
            return this.superClass;
        }

        @Override
        public TypeList.Generic getInterfaces() {
            return new TypeList.Generic.Explicit(this.interfaces);
        }

        @Override
        public MethodDescription.InDefinedShape getEnclosingMethod() {
            throw new IllegalStateException("Cannot resolve enclosing method of a latent type description: " + this);
        }

        @Override
        public TypeDescription getEnclosingType() {
            throw new IllegalStateException("Cannot resolve enclosing type of a latent type description: " + this);
        }

        @Override
        public TypeList getDeclaredTypes() {
            throw new IllegalStateException("Cannot resolve inner types of a latent type description: " + this);
        }

        @Override
        public boolean isAnonymousType() {
            throw new IllegalStateException("Cannot resolve anonymous type property of a latent type description: " + this);
        }

        @Override
        public boolean isLocalType() {
            throw new IllegalStateException("Cannot resolve local class property of a latent type description: " + this);
        }

        @Override
        public FieldList<FieldDescription.InDefinedShape> getDeclaredFields() {
            throw new IllegalStateException("Cannot resolve declared fields of a latent type description: " + this);
        }

        @Override
        public MethodList<MethodDescription.InDefinedShape> getDeclaredMethods() {
            throw new IllegalStateException("Cannot resolve declared methods of a latent type description: " + this);
        }

        @Override
        @MaybeNull
        public PackageDescription getPackage() {
            String name = this.getName();
            int index = name.lastIndexOf(46);
            return index == -1 ? PackageDescription.DEFAULT : new PackageDescription.Simple(name.substring(0, index));
        }

        @Override
        public AnnotationList getDeclaredAnnotations() {
            throw new IllegalStateException("Cannot resolve declared annotations of a latent type description: " + this);
        }

        @Override
        public TypeDescription getDeclaringType() {
            throw new IllegalStateException("Cannot resolve declared type of a latent type description: " + this);
        }

        @Override
        public int getModifiers() {
            return this.modifiers;
        }

        @Override
        public String getName() {
            return this.name;
        }

        @Override
        public TypeList.Generic getTypeVariables() {
            throw new IllegalStateException("Cannot resolve type variables of a latent type description: " + this);
        }

        @Override
        public TypeDescription getNestHost() {
            throw new IllegalStateException("Cannot resolve nest host of a latent type description: " + this);
        }

        @Override
        public TypeList getNestMembers() {
            throw new IllegalStateException("Cannot resolve nest mates of a latent type description: " + this);
        }

        @Override
        public RecordComponentList<RecordComponentDescription.InDefinedShape> getRecordComponents() {
            throw new IllegalStateException("Cannot resolve record components of a latent type description: " + this);
        }

        @Override
        public boolean isRecord() {
            throw new IllegalStateException("Cannot resolve record attribute of a latent type description: " + this);
        }

        @Override
        public TypeList getPermittedSubtypes() {
            throw new IllegalStateException("Cannot resolve permitted subclasses of a latent type description: " + this);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class ArrayProjection
    extends AbstractBase {
        private static final int ARRAY_IMPLIED = 1040;
        private static final int ARRAY_EXCLUDED = 8712;
        private final TypeDescription componentType;
        private final int arity;

        protected ArrayProjection(TypeDescription componentType, int arity) {
            this.componentType = componentType;
            this.arity = arity;
        }

        public static TypeDescription of(TypeDescription componentType) {
            return ArrayProjection.of(componentType, 1);
        }

        @SuppressFBWarnings(value={"NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE"}, justification="Assuming component type for array type.")
        public static TypeDescription of(TypeDescription componentType, int arity) {
            if (arity < 0) {
                throw new IllegalArgumentException("Arrays cannot have a negative arity");
            }
            while (componentType.isArray()) {
                componentType = componentType.getComponentType();
                ++arity;
            }
            return arity == 0 ? componentType : new ArrayProjection(componentType, arity);
        }

        @Override
        public boolean isArray() {
            return true;
        }

        @Override
        @MaybeNull
        public TypeDescription getComponentType() {
            return this.arity == 1 ? this.componentType : new ArrayProjection(this.componentType, this.arity - 1);
        }

        @Override
        public boolean isPrimitive() {
            return false;
        }

        @Override
        @MaybeNull
        public Generic getSuperClass() {
            return Generic.OfNonGenericType.ForLoadedType.of(Object.class);
        }

        @Override
        public TypeList.Generic getInterfaces() {
            return ARRAY_INTERFACES;
        }

        @Override
        @MaybeNull
        public MethodDescription.InDefinedShape getEnclosingMethod() {
            return MethodDescription.UNDEFINED;
        }

        @Override
        @MaybeNull
        public TypeDescription getEnclosingType() {
            return UNDEFINED;
        }

        @Override
        public TypeList getDeclaredTypes() {
            return new TypeList.Empty();
        }

        @Override
        public String getSimpleName() {
            StringBuilder stringBuilder = new StringBuilder(this.componentType.getSimpleName());
            for (int i = 0; i < this.arity; ++i) {
                stringBuilder.append("[]");
            }
            return stringBuilder.toString();
        }

        @Override
        @MaybeNull
        public String getCanonicalName() {
            String canonicalName = this.componentType.getCanonicalName();
            if (canonicalName == null) {
                return NO_NAME;
            }
            StringBuilder stringBuilder = new StringBuilder(canonicalName);
            for (int i = 0; i < this.arity; ++i) {
                stringBuilder.append("[]");
            }
            return stringBuilder.toString();
        }

        @Override
        public boolean isAnonymousType() {
            return false;
        }

        @Override
        public boolean isLocalType() {
            return false;
        }

        @Override
        public boolean isMemberType() {
            return false;
        }

        @Override
        public FieldList<FieldDescription.InDefinedShape> getDeclaredFields() {
            return new FieldList.Empty<FieldDescription.InDefinedShape>();
        }

        @Override
        public MethodList<MethodDescription.InDefinedShape> getDeclaredMethods() {
            return new MethodList.Empty<MethodDescription.InDefinedShape>();
        }

        @Override
        public StackSize getStackSize() {
            return StackSize.SINGLE;
        }

        @Override
        public AnnotationList getDeclaredAnnotations() {
            return new AnnotationList.Empty();
        }

        @Override
        public AnnotationList getInheritedAnnotations() {
            return new AnnotationList.Empty();
        }

        @Override
        @MaybeNull
        public PackageDescription getPackage() {
            return PackageDescription.UNDEFINED;
        }

        @Override
        public String getName() {
            int index;
            String descriptor = this.componentType.getDescriptor();
            StringBuilder stringBuilder = new StringBuilder(descriptor.length() + this.arity);
            for (index = 0; index < this.arity; ++index) {
                stringBuilder.append('[');
            }
            for (index = 0; index < descriptor.length(); ++index) {
                char character = descriptor.charAt(index);
                stringBuilder.append(character == '/' ? (char)'.' : (char)character);
            }
            return stringBuilder.toString();
        }

        @Override
        public String getDescriptor() {
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < this.arity; ++i) {
                stringBuilder.append('[');
            }
            return stringBuilder.append(this.componentType.getDescriptor()).toString();
        }

        @Override
        @AlwaysNull
        public TypeDescription getDeclaringType() {
            return UNDEFINED;
        }

        @Override
        @SuppressFBWarnings(value={"NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE"}, justification="Assuming component type for array type.")
        public int getModifiers() {
            return this.getComponentType().getModifiers() & 0xFFFFDDF7 | 0x410;
        }

        @Override
        public TypeList.Generic getTypeVariables() {
            return new TypeList.Generic.Empty();
        }

        @Override
        public TypeDescription getNestHost() {
            return this;
        }

        @Override
        public TypeList getNestMembers() {
            return new TypeList.Explicit(this);
        }

        @Override
        public RecordComponentList<RecordComponentDescription.InDefinedShape> getRecordComponents() {
            return new RecordComponentList.Empty<RecordComponentDescription.InDefinedShape>();
        }

        @Override
        public boolean isRecord() {
            return false;
        }

        @Override
        public TypeList getPermittedSubtypes() {
            return new TypeList.Empty();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @SuppressFBWarnings(value={"SE_TRANSIENT_FIELD_NOT_RESTORED"}, justification="Field is only used as a cache store and is implicitly recomputed")
    public static class ForLoadedType
    extends AbstractBase
    implements Serializable {
        private static final long serialVersionUID = 1L;
        private static final Dispatcher DISPATCHER;
        private static final Map<Class<?>, TypeDescription> TYPE_CACHE;
        private final Class<?> type;
        private transient /* synthetic */ FieldList declaredFields;
        private transient /* synthetic */ MethodList declaredMethods;
        private transient /* synthetic */ AnnotationList declaredAnnotations;
        private transient /* synthetic */ ClassFileVersion classFileVersion;
        private static final boolean ACCESS_CONTROLLER;

        public ForLoadedType(Class<?> type) {
            this.type = type;
        }

        @AccessControllerPlugin.Enhance
        private static <T> T doPrivileged(PrivilegedAction<T> privilegedAction) {
            PrivilegedAction<T> action;
            if (ACCESS_CONTROLLER) {
                return AccessController.doPrivileged(privilegedAction);
            }
            return action.run();
        }

        public static String getName(Class<?> type) {
            String name = type.getName();
            int anonymousLoaderIndex = name.indexOf(47);
            return anonymousLoaderIndex == -1 ? name : name.substring(0, anonymousLoaderIndex);
        }

        public static TypeDescription of(Class<?> type) {
            TypeDescription typeDescription = TYPE_CACHE.get(type);
            return typeDescription == null ? new ForLoadedType(type) : typeDescription;
        }

        @Override
        public boolean isAssignableFrom(Class<?> type) {
            return this.type.isAssignableFrom(type) || super.isAssignableFrom(type);
        }

        @Override
        public boolean isAssignableFrom(TypeDescription typeDescription) {
            return typeDescription instanceof ForLoadedType && this.type.isAssignableFrom(((ForLoadedType)typeDescription).type) || super.isAssignableFrom(typeDescription);
        }

        @Override
        public boolean isAssignableTo(Class<?> type) {
            return type.isAssignableFrom(this.type) || super.isAssignableTo(type);
        }

        @Override
        public boolean isAssignableTo(TypeDescription typeDescription) {
            return typeDescription instanceof ForLoadedType && ((ForLoadedType)typeDescription).type.isAssignableFrom(this.type) || super.isAssignableTo(typeDescription);
        }

        @Override
        public boolean isInHierarchyWith(Class<?> type) {
            return type.isAssignableFrom(this.type) || this.type.isAssignableFrom(type) || super.isInHierarchyWith(type);
        }

        @Override
        public boolean isInHierarchyWith(TypeDescription typeDescription) {
            return typeDescription instanceof ForLoadedType && (((ForLoadedType)typeDescription).type.isAssignableFrom(this.type) || this.type.isAssignableFrom(((ForLoadedType)typeDescription).type)) || super.isInHierarchyWith(typeDescription);
        }

        @Override
        public boolean represents(java.lang.reflect.Type type) {
            return type == this.type || super.represents(type);
        }

        @Override
        @MaybeNull
        public TypeDescription getComponentType() {
            Class<?> componentType = this.type.getComponentType();
            return componentType == null ? UNDEFINED : ForLoadedType.of(componentType);
        }

        @Override
        public boolean isArray() {
            return this.type.isArray();
        }

        @Override
        public boolean isPrimitive() {
            return this.type.isPrimitive();
        }

        @Override
        public boolean isAnnotation() {
            return this.type.isAnnotation();
        }

        @Override
        @MaybeNull
        public Generic getSuperClass() {
            if (RAW_TYPES) {
                return this.type.getSuperclass() == null ? Generic.UNDEFINED : Generic.OfNonGenericType.ForLoadedType.of(this.type.getSuperclass());
            }
            return Generic.LazyProjection.ForLoadedSuperClass.of(this.type);
        }

        @Override
        public TypeList.Generic getInterfaces() {
            if (RAW_TYPES) {
                return this.isArray() ? ARRAY_INTERFACES : new TypeList.Generic.ForLoadedTypes(this.type.getInterfaces());
            }
            return this.isArray() ? ARRAY_INTERFACES : new TypeList.Generic.OfLoadedInterfaceTypes(this.type);
        }

        @Override
        @MaybeNull
        public TypeDescription getDeclaringType() {
            Class<?> declaringType = this.type.getDeclaringClass();
            return declaringType == null ? UNDEFINED : ForLoadedType.of(declaringType);
        }

        @Override
        @MaybeNull
        public MethodDescription.InDefinedShape getEnclosingMethod() {
            Method enclosingMethod = this.type.getEnclosingMethod();
            Constructor<?> enclosingConstructor = this.type.getEnclosingConstructor();
            if (enclosingMethod != null) {
                return new MethodDescription.ForLoadedMethod(enclosingMethod);
            }
            if (enclosingConstructor != null) {
                return new MethodDescription.ForLoadedConstructor(enclosingConstructor);
            }
            return MethodDescription.UNDEFINED;
        }

        @Override
        public TypeDescription getEnclosingType() {
            Class<?> enclosingType = this.type.getEnclosingClass();
            return enclosingType == null ? UNDEFINED : ForLoadedType.of(enclosingType);
        }

        @Override
        public TypeList getDeclaredTypes() {
            return new TypeList.ForLoadedTypes(this.type.getDeclaredClasses());
        }

        @Override
        public String getSimpleName() {
            String simpleName = this.type.getSimpleName();
            int anonymousLoaderIndex = simpleName.indexOf(47);
            if (anonymousLoaderIndex == -1) {
                return simpleName;
            }
            StringBuilder normalized = new StringBuilder(simpleName.substring(0, anonymousLoaderIndex));
            Class<?> type = this.type;
            while (type.isArray()) {
                normalized.append("[]");
                type = type.getComponentType();
            }
            return normalized.toString();
        }

        @Override
        public boolean isAnonymousType() {
            return this.type.isAnonymousClass();
        }

        @Override
        public boolean isLocalType() {
            return this.type.isLocalClass();
        }

        @Override
        public boolean isMemberType() {
            return this.type.isMemberClass();
        }

        @Override
        @CachedReturnPlugin.Enhance(value="declaredFields")
        public FieldList<FieldDescription.InDefinedShape> getDeclaredFields() {
            Iterable<TypeDefinition> iterable;
            Iterable<FieldDescription.InDefinedShape> iterable2;
            FieldList fieldList = this.declaredFields;
            if (fieldList != null) {
                iterable2 = null;
            } else {
                iterable = this;
                iterable2 = iterable = new FieldList.ForLoadedFields(GraalImageCode.getCurrent().sorted(iterable.type.getDeclaredFields(), FieldComparator.INSTANCE));
            }
            if (iterable == null) {
                iterable = this.declaredFields;
            } else {
                this.declaredFields = iterable;
            }
            return iterable;
        }

        @Override
        @CachedReturnPlugin.Enhance(value="declaredMethods")
        public MethodList<MethodDescription.InDefinedShape> getDeclaredMethods() {
            Iterable<TypeDefinition> iterable;
            Iterable<MethodDescription.InDefinedShape> iterable2;
            MethodList methodList = this.declaredMethods;
            if (methodList != null) {
                iterable2 = null;
            } else {
                iterable = this;
                iterable2 = iterable = new MethodList.ForLoadedMethods(iterable.type);
            }
            if (iterable == null) {
                iterable = this.declaredMethods;
            } else {
                this.declaredMethods = iterable;
            }
            return iterable;
        }

        @Override
        @MaybeNull
        public PackageDescription getPackage() {
            if (this.type.isArray() || this.type.isPrimitive()) {
                return PackageDescription.UNDEFINED;
            }
            Package aPackage = this.type.getPackage();
            if (aPackage == null) {
                String name = this.type.getName();
                int index = name.lastIndexOf(46);
                return index == -1 ? PackageDescription.DEFAULT : new PackageDescription.Simple(name.substring(0, index));
            }
            return new PackageDescription.ForLoadedPackage(aPackage);
        }

        @Override
        public StackSize getStackSize() {
            return StackSize.of(this.type);
        }

        @Override
        public String getName() {
            return ForLoadedType.getName(this.type);
        }

        @Override
        @MaybeNull
        public String getCanonicalName() {
            String canonicalName = this.type.getCanonicalName();
            if (canonicalName == null) {
                return NO_NAME;
            }
            int anonymousLoaderIndex = canonicalName.indexOf(47);
            if (anonymousLoaderIndex == -1) {
                return canonicalName;
            }
            StringBuilder normalized = new StringBuilder(canonicalName.substring(0, anonymousLoaderIndex));
            Class<?> type = this.type;
            while (type.isArray()) {
                normalized.append("[]");
                type = type.getComponentType();
            }
            return normalized.toString();
        }

        @Override
        public String getDescriptor() {
            String name = this.type.getName();
            int anonymousLoaderIndex = name.indexOf(47);
            return anonymousLoaderIndex == -1 ? Type.getDescriptor(this.type) : "L" + name.substring(0, anonymousLoaderIndex).replace('.', '/') + ";";
        }

        @Override
        public int getModifiers() {
            return this.type.getModifiers();
        }

        @Override
        public TypeList.Generic getTypeVariables() {
            if (RAW_TYPES) {
                return new TypeList.Generic.Empty();
            }
            return TypeList.Generic.ForLoadedTypes.OfTypeVariables.of(this.type);
        }

        @Override
        @CachedReturnPlugin.Enhance(value="declaredAnnotations")
        public AnnotationList getDeclaredAnnotations() {
            Iterable<TypeDefinition> iterable;
            Iterable<AnnotationDescription> iterable2;
            AnnotationList annotationList = this.declaredAnnotations;
            if (annotationList != null) {
                iterable2 = null;
            } else {
                iterable = this;
                iterable2 = iterable = new AnnotationList.ForLoadedAnnotations(iterable.type.getDeclaredAnnotations());
            }
            if (iterable == null) {
                iterable = this.declaredAnnotations;
            } else {
                this.declaredAnnotations = iterable;
            }
            return iterable;
        }

        @Override
        public Generic asGenericType() {
            return Generic.OfNonGenericType.ForLoadedType.of(this.type);
        }

        @Override
        public TypeDescription getNestHost() {
            Class<?> host = DISPATCHER.getNestHost(this.type);
            return host == null ? this : ForLoadedType.of(host);
        }

        @Override
        public TypeList getNestMembers() {
            Class<?>[] classArray;
            Class<?>[] member = DISPATCHER.getNestMembers(this.type);
            if (member.length == 0) {
                Class[] classArray2 = new Class[1];
                classArray = classArray2;
                classArray2[0] = this.type;
            } else {
                classArray = member;
            }
            return new TypeList.ForLoadedTypes(classArray);
        }

        @Override
        public boolean isNestHost() {
            Class<?> host = DISPATCHER.getNestHost(this.type);
            return host == null || host == this.type;
        }

        @Override
        public boolean isNestMateOf(Class<?> type) {
            return DISPATCHER.isNestmateOf(this.type, type) || super.isNestMateOf(ForLoadedType.of(type));
        }

        @Override
        public boolean isNestMateOf(TypeDescription typeDescription) {
            return typeDescription instanceof ForLoadedType && DISPATCHER.isNestmateOf(this.type, ((ForLoadedType)typeDescription).type) || super.isNestMateOf(typeDescription);
        }

        @Override
        public RecordComponentList<RecordComponentDescription.InDefinedShape> getRecordComponents() {
            Object[] recordComponent = DISPATCHER.getRecordComponents(this.type);
            return (RecordComponentList)((Object)(recordComponent == null ? new RecordComponentList.Empty() : new RecordComponentList.ForLoadedRecordComponents(recordComponent)));
        }

        @Override
        public boolean isRecord() {
            return DISPATCHER.isRecord(this.type);
        }

        @Override
        public boolean isSealed() {
            return DISPATCHER.isSealed(this.type);
        }

        @Override
        public TypeList getPermittedSubtypes() {
            Class<?>[] permittedSubclass = DISPATCHER.getPermittedSubclasses(this.type);
            return (TypeList)((Object)(permittedSubclass == null ? new TypeList.Empty() : new TypeList.ForLoadedTypes(permittedSubclass)));
        }

        @Override
        @MaybeNull
        @CachedReturnPlugin.Enhance(value="classFileVersion")
        public ClassFileVersion getClassFileVersion() {
            Serializable serializable;
            Serializable serializable2;
            ClassFileVersion classFileVersion = this.classFileVersion;
            if (classFileVersion != null) {
                serializable2 = null;
            } else {
                serializable = this;
                try {
                    serializable2 = ClassFileVersion.of(serializable.type);
                }
                catch (Throwable ignored) {
                    serializable2 = serializable = null;
                }
            }
            if (serializable == null) {
                serializable = this.classFileVersion;
            } else {
                this.classFileVersion = serializable;
            }
            return serializable;
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
            DISPATCHER = ForLoadedType.doPrivileged(JavaDispatcher.of(Dispatcher.class));
            TYPE_CACHE = new HashMap();
            TYPE_CACHE.put(TargetType.class, new ForLoadedType(TargetType.class));
            TYPE_CACHE.put(Class.class, new ForLoadedType(Class.class));
            TYPE_CACHE.put(Throwable.class, new ForLoadedType(Throwable.class));
            TYPE_CACHE.put(Annotation.class, new ForLoadedType(Annotation.class));
            TYPE_CACHE.put(Object.class, new ForLoadedType(Object.class));
            TYPE_CACHE.put(String.class, new ForLoadedType(String.class));
            TYPE_CACHE.put(Boolean.class, new ForLoadedType(Boolean.class));
            TYPE_CACHE.put(Byte.class, new ForLoadedType(Byte.class));
            TYPE_CACHE.put(Short.class, new ForLoadedType(Short.class));
            TYPE_CACHE.put(Character.class, new ForLoadedType(Character.class));
            TYPE_CACHE.put(Integer.class, new ForLoadedType(Integer.class));
            TYPE_CACHE.put(Long.class, new ForLoadedType(Long.class));
            TYPE_CACHE.put(Float.class, new ForLoadedType(Float.class));
            TYPE_CACHE.put(Double.class, new ForLoadedType(Double.class));
            TYPE_CACHE.put(Void.TYPE, new ForLoadedType(Void.TYPE));
            TYPE_CACHE.put(Boolean.TYPE, new ForLoadedType(Boolean.TYPE));
            TYPE_CACHE.put(Byte.TYPE, new ForLoadedType(Byte.TYPE));
            TYPE_CACHE.put(Short.TYPE, new ForLoadedType(Short.TYPE));
            TYPE_CACHE.put(Character.TYPE, new ForLoadedType(Character.TYPE));
            TYPE_CACHE.put(Integer.TYPE, new ForLoadedType(Integer.TYPE));
            TYPE_CACHE.put(Long.TYPE, new ForLoadedType(Long.TYPE));
            TYPE_CACHE.put(Float.TYPE, new ForLoadedType(Float.TYPE));
            TYPE_CACHE.put(Double.TYPE, new ForLoadedType(Double.TYPE));
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        @JavaDispatcher.Defaults
        @JavaDispatcher.Proxied(value="java.lang.Class")
        protected static interface Dispatcher {
            @MaybeNull
            @JavaDispatcher.Proxied(value="getAnnotatedSuperclass")
            public AnnotatedElement getAnnotatedSuperclass(Class<?> var1);

            @JavaDispatcher.Proxied(value="getAnnotatedInterfaces")
            public AnnotatedElement[] getAnnotatedInterfaces(Class<?> var1);

            @MaybeNull
            @JavaDispatcher.Proxied(value="getNestHost")
            public Class<?> getNestHost(Class<?> var1);

            @JavaDispatcher.Proxied(value="getNestMembers")
            public Class<?>[] getNestMembers(Class<?> var1);

            @JavaDispatcher.Proxied(value="isNestmateOf")
            public boolean isNestmateOf(Class<?> var1, Class<?> var2);

            @JavaDispatcher.Proxied(value="isSealed")
            public boolean isSealed(Class<?> var1);

            @MaybeNull
            @JavaDispatcher.Proxied(value="getPermittedSubclasses")
            public Class<?>[] getPermittedSubclasses(Class<?> var1);

            @JavaDispatcher.Proxied(value="isRecord")
            public boolean isRecord(Class<?> var1);

            @MaybeNull
            @JavaDispatcher.Proxied(value="getRecordComponents")
            public Object[] getRecordComponents(Class<?> var1);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @HashCodeAndEqualsPlugin.Enhance
    public static class LazyProxy
    implements InvocationHandler {
        private final Class<?> type;

        protected LazyProxy(Class<?> type) {
            this.type = type;
        }

        protected static TypeDescription of(Class<?> type) {
            return (TypeDescription)Proxy.newProxyInstance(TypeDescription.class.getClassLoader(), new Class[]{TypeDescription.class}, (InvocationHandler)new LazyProxy(type));
        }

        @Override
        public Object invoke(Object proxy, Method method, @MaybeNull Object[] argument) throws Throwable {
            try {
                return method.invoke((Object)ForLoadedType.of(this.type), argument);
            }
            catch (InvocationTargetException exception) {
                throw exception.getTargetException();
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
            return this.type.equals(((LazyProxy)object).type);
        }

        public int hashCode() {
            return this.getClass().hashCode() * 31 + this.type.hashCode();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static abstract class AbstractBase
    extends TypeVariableSource.AbstractBase
    implements TypeDescription {
        public static final boolean RAW_TYPES;
        private transient /* synthetic */ int hashCode;
        private static final boolean ACCESS_CONTROLLER;

        @AccessControllerPlugin.Enhance
        private static <T> T doPrivileged(PrivilegedAction<T> privilegedAction) {
            PrivilegedAction<T> action;
            if (ACCESS_CONTROLLER) {
                return AccessController.doPrivileged(privilegedAction);
            }
            return action.run();
        }

        @SuppressFBWarnings(value={"NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE"}, justification="Assuming component type for array type.")
        private static boolean isAssignable(TypeDescription sourceType, TypeDescription targetType) {
            if (sourceType.equals(targetType)) {
                return true;
            }
            if (targetType.isArray()) {
                return sourceType.isArray() ? AbstractBase.isAssignable(sourceType.getComponentType(), targetType.getComponentType()) : sourceType.represents((java.lang.reflect.Type)((Object)Object.class)) || ARRAY_INTERFACES.contains(sourceType.asGenericType());
            }
            if (sourceType.represents((java.lang.reflect.Type)((Object)Object.class))) {
                return !targetType.isPrimitive();
            }
            Generic superClass = targetType.getSuperClass();
            if (superClass != null && sourceType.isAssignableFrom(superClass.asErasure())) {
                return true;
            }
            if (sourceType.isInterface()) {
                for (TypeDescription interfaceType : targetType.getInterfaces().asErasures()) {
                    if (!sourceType.isAssignableFrom(interfaceType)) continue;
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean isAssignableFrom(Class<?> type) {
            return this.isAssignableFrom(ForLoadedType.of(type));
        }

        @Override
        public boolean isAssignableFrom(TypeDescription typeDescription) {
            return AbstractBase.isAssignable(this, typeDescription);
        }

        @Override
        public boolean isAssignableTo(Class<?> type) {
            return this.isAssignableTo(ForLoadedType.of(type));
        }

        @Override
        public boolean isAssignableTo(TypeDescription typeDescription) {
            return AbstractBase.isAssignable(typeDescription, this);
        }

        @Override
        public boolean isInHierarchyWith(Class<?> type) {
            return this.isAssignableTo(type) || this.isAssignableFrom(type);
        }

        @Override
        public boolean isInHierarchyWith(TypeDescription typeDescription) {
            return this.isAssignableTo(typeDescription) || this.isAssignableFrom(typeDescription);
        }

        @Override
        public TypeDescription asErasure() {
            return this;
        }

        @Override
        public Generic asGenericType() {
            return new Generic.OfNonGenericType.ForErasure(this);
        }

        @Override
        public TypeDefinition.Sort getSort() {
            return TypeDefinition.Sort.NON_GENERIC;
        }

        @Override
        public boolean isInstance(Object value) {
            return this.isAssignableFrom(value.getClass());
        }

        @Override
        public boolean isAnnotationValue(Object value) {
            if (this.represents((java.lang.reflect.Type)((Object)Class.class)) && value instanceof TypeDescription || value instanceof AnnotationDescription && ((AnnotationDescription)value).getAnnotationType().equals(this) || value instanceof EnumerationDescription && ((EnumerationDescription)value).getEnumerationType().equals(this) || this.represents((java.lang.reflect.Type)((Object)String.class)) && value instanceof String || this.represents(Boolean.TYPE) && value instanceof Boolean || this.represents(Byte.TYPE) && value instanceof Byte || this.represents(Short.TYPE) && value instanceof Short || this.represents(Character.TYPE) && value instanceof Character || this.represents(Integer.TYPE) && value instanceof Integer || this.represents(Long.TYPE) && value instanceof Long || this.represents(Float.TYPE) && value instanceof Float || this.represents(Double.TYPE) && value instanceof Double || this.represents((java.lang.reflect.Type)((Object)String[].class)) && value instanceof String[] || this.represents((java.lang.reflect.Type)((Object)boolean[].class)) && value instanceof boolean[] || this.represents((java.lang.reflect.Type)((Object)byte[].class)) && value instanceof byte[] || this.represents((java.lang.reflect.Type)((Object)short[].class)) && value instanceof short[] || this.represents((java.lang.reflect.Type)((Object)char[].class)) && value instanceof char[] || this.represents((java.lang.reflect.Type)((Object)int[].class)) && value instanceof int[] || this.represents((java.lang.reflect.Type)((Object)long[].class)) && value instanceof long[] || this.represents((java.lang.reflect.Type)((Object)float[].class)) && value instanceof float[] || this.represents((java.lang.reflect.Type)((Object)double[].class)) && value instanceof double[] || this.represents((java.lang.reflect.Type)((Object)Class[].class)) && value instanceof TypeDescription[]) {
                return true;
            }
            if (this.isAssignableTo(Annotation[].class) && value instanceof AnnotationDescription[]) {
                for (AnnotationDescription annotationDescription : (AnnotationDescription[])value) {
                    if (annotationDescription.getAnnotationType().equals(this.getComponentType())) continue;
                    return false;
                }
                return true;
            }
            if (this.isAssignableTo(Enum[].class) && value instanceof EnumerationDescription[]) {
                for (EnumerationDescription enumerationDescription : (EnumerationDescription[])value) {
                    if (enumerationDescription.getEnumerationType().equals(this.getComponentType())) continue;
                    return false;
                }
                return true;
            }
            return false;
        }

        @Override
        public String getInternalName() {
            return this.getName().replace('.', '/');
        }

        @Override
        public int getActualModifiers(boolean superFlag) {
            int actualModifiers = this.getModifiers() | (this.getDeclaredAnnotations().isAnnotationPresent(Deprecated.class) ? 131072 : 0) | (this.isRecord() ? 65536 : 0) | (superFlag ? 32 : 0);
            if (this.isPrivate()) {
                return actualModifiers & 0xFFFFFFF5;
            }
            if (this.isProtected()) {
                return actualModifiers & 0xFFFFFFF3 | 1;
            }
            return actualModifiers & 0xFFFFFFF7;
        }

        @Override
        @MaybeNull
        public String getGenericSignature() {
            try {
                SignatureWriter signatureWriter = new SignatureWriter();
                boolean generic = false;
                for (Generic typeVariable : this.getTypeVariables()) {
                    signatureWriter.visitFormalTypeParameter(typeVariable.getSymbol());
                    Iterator iterator = typeVariable.getUpperBounds().iterator();
                    while (iterator.hasNext()) {
                        Generic upperBound;
                        upperBound.accept(new Generic.Visitor.ForSignatureVisitor((upperBound = (Generic)iterator.next()).asErasure().isInterface() ? signatureWriter.visitInterfaceBound() : signatureWriter.visitClassBound()));
                    }
                    generic = true;
                }
                Generic superClass = this.getSuperClass();
                if (superClass == null) {
                    superClass = Generic.OfNonGenericType.ForLoadedType.of(Object.class);
                }
                superClass.accept(new Generic.Visitor.ForSignatureVisitor(signatureWriter.visitSuperclass()));
                generic = generic || !superClass.getSort().isNonGeneric();
                for (Generic interfaceType : this.getInterfaces()) {
                    interfaceType.accept(new Generic.Visitor.ForSignatureVisitor(signatureWriter.visitInterface()));
                    generic = generic || !interfaceType.getSort().isNonGeneric();
                }
                return generic ? signatureWriter.toString() : NON_GENERIC_SIGNATURE;
            }
            catch (GenericSignatureFormatError ignored) {
                return NON_GENERIC_SIGNATURE;
            }
        }

        @Override
        public boolean isSamePackage(TypeDescription typeDescription) {
            PackageDescription thisPackage = this.getPackage();
            PackageDescription otherPackage = typeDescription.getPackage();
            return thisPackage == null || otherPackage == null ? thisPackage == otherPackage : thisPackage.equals(otherPackage);
        }

        @Override
        @SuppressFBWarnings(value={"NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE"}, justification="Assuming component type for array type.")
        public boolean isVisibleTo(TypeDescription typeDescription) {
            return this.isPrimitive() || (this.isArray() ? this.getComponentType().isVisibleTo(typeDescription) : this.isPublic() || this.isProtected() || this.isSamePackage(typeDescription));
        }

        @Override
        @SuppressFBWarnings(value={"NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE"}, justification="Assuming component type for array type.")
        public boolean isAccessibleTo(TypeDescription typeDescription) {
            return this.isPrimitive() || (this.isArray() ? this.getComponentType().isVisibleTo(typeDescription) : this.isPublic() || this.isSamePackage(typeDescription));
        }

        @Override
        public AnnotationList getInheritedAnnotations() {
            Generic superClass = this.getSuperClass();
            AnnotationList declaredAnnotations = this.getDeclaredAnnotations();
            if (superClass == null) {
                return declaredAnnotations;
            }
            HashSet<TypeDescription> annotationTypes = new HashSet<TypeDescription>();
            for (AnnotationDescription annotationDescription : declaredAnnotations) {
                annotationTypes.add(annotationDescription.getAnnotationType());
            }
            return new AnnotationList.Explicit(CompoundList.of(declaredAnnotations, superClass.asErasure().getInheritedAnnotations().inherited(annotationTypes)));
        }

        @Override
        @SuppressFBWarnings(value={"NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE"}, justification="Assuming component type for array type.")
        public String getActualName() {
            if (this.isArray()) {
                TypeDescription typeDescription = this;
                int dimensions = 0;
                do {
                    ++dimensions;
                } while ((typeDescription = typeDescription.getComponentType()).isArray());
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(typeDescription.getActualName());
                for (int i = 0; i < dimensions; ++i) {
                    stringBuilder.append("[]");
                }
                return stringBuilder.toString();
            }
            return this.getName();
        }

        @Override
        public String getLongSimpleName() {
            TypeDescription declaringType = this.getDeclaringType();
            return declaringType == null ? this.getSimpleName() : declaringType.getLongSimpleName() + "." + this.getSimpleName();
        }

        @Override
        public boolean isPrimitiveWrapper() {
            return this.represents((java.lang.reflect.Type)((Object)Boolean.class)) || this.represents((java.lang.reflect.Type)((Object)Byte.class)) || this.represents((java.lang.reflect.Type)((Object)Short.class)) || this.represents((java.lang.reflect.Type)((Object)Character.class)) || this.represents((java.lang.reflect.Type)((Object)Integer.class)) || this.represents((java.lang.reflect.Type)((Object)Long.class)) || this.represents((java.lang.reflect.Type)((Object)Float.class)) || this.represents((java.lang.reflect.Type)((Object)Double.class));
        }

        @Override
        @SuppressFBWarnings(value={"NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE"}, justification="Assuming component type for array type.")
        public boolean isAnnotationReturnType() {
            return this.isPrimitive() || this.represents((java.lang.reflect.Type)((Object)String.class)) || this.isAssignableTo(Enum.class) && !this.represents((java.lang.reflect.Type)((Object)Enum.class)) || this.isAssignableTo(Annotation.class) && !this.represents((java.lang.reflect.Type)((Object)Annotation.class)) || this.represents((java.lang.reflect.Type)((Object)Class.class)) || this.isArray() && !this.getComponentType().isArray() && this.getComponentType().isAnnotationReturnType();
        }

        @Override
        @SuppressFBWarnings(value={"NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE"}, justification="Assuming component type for array type.")
        public boolean isAnnotationValue() {
            return this.isPrimitive() || this.represents((java.lang.reflect.Type)((Object)String.class)) || this.isAssignableTo(TypeDescription.class) || this.isAssignableTo(AnnotationDescription.class) || this.isAssignableTo(EnumerationDescription.class) || this.isArray() && !this.getComponentType().isArray() && this.getComponentType().isAnnotationValue();
        }

        @Override
        @SuppressFBWarnings(value={"EC_UNRELATED_CLASS_AND_INTERFACE"}, justification="Fits equality contract for type definitions.")
        public boolean represents(java.lang.reflect.Type type) {
            return this.equals(TypeDefinition.Sort.describe(type));
        }

        @Override
        public String getTypeName() {
            return this.getName();
        }

        @Override
        @MaybeNull
        public TypeVariableSource getEnclosingSource() {
            MethodDescription.InDefinedShape enclosingMethod = this.getEnclosingMethod();
            return enclosingMethod == null ? (this.isStatic() ? TypeVariableSource.UNDEFINED : this.getEnclosingType()) : enclosingMethod;
        }

        @Override
        public boolean isInferrable() {
            return false;
        }

        @Override
        public <T> T accept(TypeVariableSource.Visitor<T> visitor) {
            return visitor.onType(this);
        }

        @Override
        public boolean isPackageType() {
            return this.getSimpleName().equals("package-info");
        }

        @Override
        public boolean isGenerified() {
            TypeDescription declaringType;
            if (!this.getTypeVariables().isEmpty()) {
                return true;
            }
            if (!this.isStatic() && (declaringType = this.getDeclaringType()) != null && declaringType.isGenerified()) {
                return true;
            }
            try {
                MethodDescription.InDefinedShape enclosingMethod = this.getEnclosingMethod();
                return enclosingMethod != null && enclosingMethod.isGenerified();
            }
            catch (Throwable ignored) {
                return false;
            }
        }

        @Override
        public int getInnerClassCount() {
            if (this.isStatic()) {
                return 0;
            }
            TypeDescription declaringType = this.getDeclaringType();
            return declaringType == null ? 0 : declaringType.getInnerClassCount() + 1;
        }

        @Override
        public boolean isInnerClass() {
            return !this.isStatic() && this.isNestedClass();
        }

        @Override
        public boolean isNestedClass() {
            return this.getDeclaringType() != null;
        }

        @Override
        public TypeDescription asBoxed() {
            if (this.represents(Boolean.TYPE)) {
                return ForLoadedType.of(Boolean.class);
            }
            if (this.represents(Byte.TYPE)) {
                return ForLoadedType.of(Byte.class);
            }
            if (this.represents(Short.TYPE)) {
                return ForLoadedType.of(Short.class);
            }
            if (this.represents(Character.TYPE)) {
                return ForLoadedType.of(Character.class);
            }
            if (this.represents(Integer.TYPE)) {
                return ForLoadedType.of(Integer.class);
            }
            if (this.represents(Long.TYPE)) {
                return ForLoadedType.of(Long.class);
            }
            if (this.represents(Float.TYPE)) {
                return ForLoadedType.of(Float.class);
            }
            if (this.represents(Double.TYPE)) {
                return ForLoadedType.of(Double.class);
            }
            return this;
        }

        @Override
        public TypeDescription asUnboxed() {
            if (this.represents((java.lang.reflect.Type)((Object)Boolean.class))) {
                return ForLoadedType.of(Boolean.TYPE);
            }
            if (this.represents((java.lang.reflect.Type)((Object)Byte.class))) {
                return ForLoadedType.of(Byte.TYPE);
            }
            if (this.represents((java.lang.reflect.Type)((Object)Short.class))) {
                return ForLoadedType.of(Short.TYPE);
            }
            if (this.represents((java.lang.reflect.Type)((Object)Character.class))) {
                return ForLoadedType.of(Character.TYPE);
            }
            if (this.represents((java.lang.reflect.Type)((Object)Integer.class))) {
                return ForLoadedType.of(Integer.TYPE);
            }
            if (this.represents((java.lang.reflect.Type)((Object)Long.class))) {
                return ForLoadedType.of(Long.TYPE);
            }
            if (this.represents((java.lang.reflect.Type)((Object)Float.class))) {
                return ForLoadedType.of(Float.TYPE);
            }
            if (this.represents((java.lang.reflect.Type)((Object)Double.class))) {
                return ForLoadedType.of(Double.TYPE);
            }
            return this;
        }

        @Override
        @MaybeNull
        public Object getDefaultValue() {
            if (this.represents(Boolean.TYPE)) {
                return false;
            }
            if (this.represents(Byte.TYPE)) {
                return (byte)0;
            }
            if (this.represents(Short.TYPE)) {
                return (short)0;
            }
            if (this.represents(Character.TYPE)) {
                return Character.valueOf('\u0000');
            }
            if (this.represents(Integer.TYPE)) {
                return 0;
            }
            if (this.represents(Long.TYPE)) {
                return 0L;
            }
            if (this.represents(Float.TYPE)) {
                return Float.valueOf(0.0f);
            }
            if (this.represents(Double.TYPE)) {
                return 0.0;
            }
            return null;
        }

        @Override
        public boolean isNestHost() {
            return this.equals(this.getNestHost());
        }

        @Override
        public boolean isNestMateOf(Class<?> type) {
            return this.isNestMateOf(ForLoadedType.of(type));
        }

        @Override
        public boolean isNestMateOf(TypeDescription typeDescription) {
            return this.getNestHost().equals(typeDescription.getNestHost());
        }

        @Override
        public boolean isMemberType() {
            return !this.isLocalType() && !this.isAnonymousType() && this.getDeclaringType() != null;
        }

        @Override
        public boolean isCompileTimeConstant() {
            return this.represents(Integer.TYPE) || this.represents(Long.TYPE) || this.represents(Float.TYPE) || this.represents(Double.TYPE) || this.represents((java.lang.reflect.Type)((Object)String.class)) || this.represents((java.lang.reflect.Type)((Object)Class.class)) || this.equals(JavaType.METHOD_TYPE.getTypeStub()) || this.equals(JavaType.METHOD_HANDLE.getTypeStub());
        }

        @Override
        public boolean isSealed() {
            return !this.isPrimitive() && !this.isArray() && !this.getPermittedSubtypes().isEmpty();
        }

        @Override
        @MaybeNull
        public ClassFileVersion getClassFileVersion() {
            return null;
        }

        @Override
        public Iterator<TypeDefinition> iterator() {
            return new TypeDefinition.SuperClassIterator(this);
        }

        @CachedReturnPlugin.Enhance(value="hashCode")
        public int hashCode() {
            int n;
            int n2;
            int n3 = this.hashCode;
            if (n3 != 0) {
                n2 = 0;
            } else {
                AbstractBase abstractBase = this;
                n2 = n = abstractBase.getName().hashCode();
            }
            if (n == 0) {
                n = this.hashCode;
            } else {
                this.hashCode = n;
            }
            return n;
        }

        public boolean equals(@MaybeNull Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof TypeDefinition)) {
                return false;
            }
            TypeDefinition typeDefinition = (TypeDefinition)other;
            return typeDefinition.getSort().isNonGeneric() && this.getName().equals(typeDefinition.asErasure().getName());
        }

        public String toString() {
            return (this.isPrimitive() ? "" : (this.isInterface() ? "interface" : "class") + " ") + this.getName();
        }

        @Override
        protected String toSafeString() {
            return this.toString();
        }

        /*
         * Enabled aggressive block sorting
         * Enabled unnecessary exception pruning
         * Enabled aggressive exception aggregation
         */
        static {
            boolean rawTypes;
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
            try {
                rawTypes = Boolean.parseBoolean(AbstractBase.doPrivileged(new GetSystemPropertyAction("net.bytebuddy.raw")));
            }
            catch (Exception ignored) {
                rawTypes = false;
            }
            RAW_TYPES = rawTypes;
        }

        public static abstract class OfSimpleType
        extends AbstractBase {
            public boolean isPrimitive() {
                return false;
            }

            public boolean isArray() {
                return false;
            }

            @MaybeNull
            public TypeDescription getComponentType() {
                return UNDEFINED;
            }

            public String getDescriptor() {
                return "L" + this.getInternalName() + ";";
            }

            @MaybeNull
            public String getCanonicalName() {
                if (this.isAnonymousType() || this.isLocalType()) {
                    return NO_NAME;
                }
                String internalName = this.getInternalName();
                TypeDescription enclosingType = this.getEnclosingType();
                if (enclosingType != null && internalName.startsWith(enclosingType.getInternalName() + "$")) {
                    return enclosingType.getCanonicalName() + "." + internalName.substring(enclosingType.getInternalName().length() + 1);
                }
                return this.getName();
            }

            public String getSimpleName() {
                int simpleNameIndex;
                String internalName = this.getInternalName();
                TypeDescription enclosingType = this.getEnclosingType();
                if (enclosingType != null && internalName.startsWith(enclosingType.getInternalName() + "$")) {
                    simpleNameIndex = enclosingType.getInternalName().length() + 1;
                } else {
                    simpleNameIndex = internalName.lastIndexOf(47);
                    if (simpleNameIndex == -1) {
                        return internalName;
                    }
                }
                while (simpleNameIndex < internalName.length() && !Character.isLetter(internalName.charAt(simpleNameIndex))) {
                    ++simpleNameIndex;
                }
                return internalName.substring(simpleNameIndex);
            }

            public StackSize getStackSize() {
                return StackSize.SINGLE;
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            public static abstract class WithDelegation
            extends OfSimpleType {
                protected abstract TypeDescription delegate();

                @Override
                public Generic getSuperClass() {
                    return this.delegate().getSuperClass();
                }

                @Override
                public TypeList.Generic getInterfaces() {
                    return this.delegate().getInterfaces();
                }

                @Override
                public FieldList<FieldDescription.InDefinedShape> getDeclaredFields() {
                    return this.delegate().getDeclaredFields();
                }

                @Override
                public MethodList<MethodDescription.InDefinedShape> getDeclaredMethods() {
                    return this.delegate().getDeclaredMethods();
                }

                @Override
                @MaybeNull
                public TypeDescription getDeclaringType() {
                    return this.delegate().getDeclaringType();
                }

                @Override
                @MaybeNull
                public MethodDescription.InDefinedShape getEnclosingMethod() {
                    return this.delegate().getEnclosingMethod();
                }

                @Override
                @MaybeNull
                public TypeDescription getEnclosingType() {
                    return this.delegate().getEnclosingType();
                }

                @Override
                public TypeList getDeclaredTypes() {
                    return this.delegate().getDeclaredTypes();
                }

                @Override
                public boolean isAnonymousType() {
                    return this.delegate().isAnonymousType();
                }

                @Override
                public boolean isLocalType() {
                    return this.delegate().isLocalType();
                }

                @Override
                @MaybeNull
                public PackageDescription getPackage() {
                    return this.delegate().getPackage();
                }

                @Override
                public AnnotationList getDeclaredAnnotations() {
                    return this.delegate().getDeclaredAnnotations();
                }

                @Override
                public TypeList.Generic getTypeVariables() {
                    return this.delegate().getTypeVariables();
                }

                @Override
                public int getModifiers() {
                    return this.delegate().getModifiers();
                }

                @Override
                @MaybeNull
                public String getGenericSignature() {
                    return this.delegate().getGenericSignature();
                }

                @Override
                public int getActualModifiers(boolean superFlag) {
                    return this.delegate().getActualModifiers(superFlag);
                }

                @Override
                public TypeDescription getNestHost() {
                    return this.delegate().getNestHost();
                }

                @Override
                public TypeList getNestMembers() {
                    return this.delegate().getNestMembers();
                }

                @Override
                public RecordComponentList<RecordComponentDescription.InDefinedShape> getRecordComponents() {
                    return this.delegate().getRecordComponents();
                }

                @Override
                public boolean isRecord() {
                    return this.delegate().isRecord();
                }

                @Override
                public boolean isSealed() {
                    return this.delegate().isSealed();
                }

                @Override
                public TypeList getPermittedSubtypes() {
                    return this.delegate().getPermittedSubtypes();
                }

                @Override
                @MaybeNull
                public ClassFileVersion getClassFileVersion() {
                    return this.delegate().getClassFileVersion();
                }
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static interface Generic
    extends TypeDefinition,
    AnnotationSource {
        @Deprecated
        public static final Generic OBJECT = LazyProxy.of(Object.class);
        @Deprecated
        public static final Generic CLASS = LazyProxy.of(Class.class);
        @Deprecated
        public static final Generic VOID = LazyProxy.of(Void.TYPE);
        @Deprecated
        public static final Generic ANNOTATION = LazyProxy.of(Annotation.class);
        @AlwaysNull
        public static final Generic UNDEFINED = null;

        public Generic asRawType();

        public TypeList.Generic getUpperBounds();

        public TypeList.Generic getLowerBounds();

        public TypeList.Generic getTypeArguments();

        @MaybeNull
        public Generic getOwnerType();

        @MaybeNull
        public Generic findBindingOf(Generic var1);

        public TypeVariableSource getTypeVariableSource();

        public String getSymbol();

        @Override
        @MaybeNull
        public Generic getComponentType();

        public FieldList<FieldDescription.InGenericShape> getDeclaredFields();

        public MethodList<MethodDescription.InGenericShape> getDeclaredMethods();

        public RecordComponentList<RecordComponentDescription.InGenericShape> getRecordComponents();

        public <T> T accept(Visitor<T> var1);

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        @HashCodeAndEqualsPlugin.Enhance
        public static abstract class Builder {
            @AlwaysNull
            private static final java.lang.reflect.Type UNDEFINED = null;
            protected final List<? extends AnnotationDescription> annotations;

            protected Builder(List<? extends AnnotationDescription> annotations) {
                this.annotations = annotations;
            }

            public static Builder of(java.lang.reflect.Type type) {
                return Builder.of(TypeDefinition.Sort.describe(type));
            }

            public static Builder of(Generic typeDescription) {
                return typeDescription.accept(Visitor.INSTANCE);
            }

            public static Builder rawType(Class<?> type) {
                return Builder.rawType(ForLoadedType.of(type));
            }

            public static Builder rawType(TypeDescription type) {
                return new OfNonGenericType(type);
            }

            public static Builder rawType(Class<?> type, @MaybeNull Generic ownerType) {
                return Builder.rawType(ForLoadedType.of(type), ownerType);
            }

            public static Builder rawType(TypeDescription type, @MaybeNull Generic ownerType) {
                TypeDescription declaringType = type.getDeclaringType();
                if (declaringType == null && ownerType != null) {
                    throw new IllegalArgumentException(type + " does not have a declaring type: " + ownerType);
                }
                if (!(declaringType == null || ownerType != null && declaringType.equals(ownerType.asErasure()))) {
                    throw new IllegalArgumentException(ownerType + " is not the declaring type of " + type);
                }
                return new OfNonGenericType(type, ownerType);
            }

            public static Generic unboundWildcard() {
                return Builder.unboundWildcard(Collections.emptySet());
            }

            public static Generic unboundWildcard(Annotation ... annotation) {
                return Builder.unboundWildcard(Arrays.asList(annotation));
            }

            public static Generic unboundWildcard(List<? extends Annotation> annotations) {
                return Builder.unboundWildcard(new AnnotationList.ForLoadedAnnotations(annotations));
            }

            public static Generic unboundWildcard(AnnotationDescription ... annotation) {
                return Builder.unboundWildcard(Arrays.asList(annotation));
            }

            public static Generic unboundWildcard(Collection<? extends AnnotationDescription> annotations) {
                return OfWildcardType.Latent.unbounded(new AnnotationSource.Explicit(new ArrayList<AnnotationDescription>(annotations)));
            }

            public static Builder typeVariable(String symbol) {
                return new OfTypeVariable(symbol);
            }

            public static Builder parameterizedType(Class<?> rawType, java.lang.reflect.Type ... parameter) {
                return Builder.parameterizedType(rawType, Arrays.asList(parameter));
            }

            public static Builder parameterizedType(Class<?> rawType, List<? extends java.lang.reflect.Type> parameters) {
                return Builder.parameterizedType(rawType, UNDEFINED, parameters);
            }

            public static Builder parameterizedType(Class<?> rawType, @MaybeNull java.lang.reflect.Type ownerType, List<? extends java.lang.reflect.Type> parameters) {
                return Builder.parameterizedType(ForLoadedType.of(rawType), ownerType == null ? null : TypeDefinition.Sort.describe(ownerType), new TypeList.Generic.ForLoadedTypes(parameters));
            }

            public static Builder parameterizedType(TypeDescription rawType, TypeDefinition ... parameter) {
                return Builder.parameterizedType(rawType, Arrays.asList(parameter));
            }

            public static Builder parameterizedType(TypeDescription rawType, Collection<? extends TypeDefinition> parameters) {
                return Builder.parameterizedType(rawType, UNDEFINED, parameters);
            }

            public static Builder parameterizedType(TypeDescription rawType, @MaybeNull Generic ownerType, Collection<? extends TypeDefinition> parameters) {
                TypeDescription declaringType = rawType.getDeclaringType();
                if (ownerType == null && declaringType != null && rawType.isStatic()) {
                    ownerType = declaringType.asGenericType();
                }
                if (!rawType.represents((java.lang.reflect.Type)((Object)TargetType.class))) {
                    if (!rawType.isGenerified()) {
                        throw new IllegalArgumentException(rawType + " is not a parameterized type");
                    }
                    if (ownerType == null && declaringType != null && !rawType.isStatic()) {
                        throw new IllegalArgumentException(rawType + " requires an owner type");
                    }
                    if (ownerType != null && !ownerType.asErasure().equals(declaringType)) {
                        throw new IllegalArgumentException(ownerType + " does not represent required owner for " + rawType);
                    }
                    if (ownerType != null && rawType.isStatic() ^ ownerType.getSort().isNonGeneric()) {
                        throw new IllegalArgumentException(ownerType + " does not define the correct parameters for owning " + rawType);
                    }
                    if (rawType.getTypeVariables().size() != parameters.size()) {
                        throw new IllegalArgumentException(parameters + " does not contain number of required parameters for " + rawType);
                    }
                }
                return new OfParameterizedType(rawType, ownerType, new TypeList.Generic.Explicit(new ArrayList<TypeDefinition>(parameters)));
            }

            public Generic asWildcardUpperBound() {
                return this.asWildcardUpperBound(Collections.emptySet());
            }

            public Generic asWildcardUpperBound(Annotation ... annotation) {
                return this.asWildcardUpperBound(Arrays.asList(annotation));
            }

            public Generic asWildcardUpperBound(List<? extends Annotation> annotations) {
                return this.asWildcardUpperBound(new AnnotationList.ForLoadedAnnotations(annotations));
            }

            public Generic asWildcardUpperBound(AnnotationDescription ... annotation) {
                return this.asWildcardUpperBound((Collection<? extends AnnotationDescription>)Arrays.asList(annotation));
            }

            public Generic asWildcardUpperBound(Collection<? extends AnnotationDescription> annotations) {
                return OfWildcardType.Latent.boundedAbove(this.build(), new AnnotationSource.Explicit(new ArrayList<AnnotationDescription>(annotations)));
            }

            public Generic asWildcardLowerBound() {
                return this.asWildcardLowerBound(Collections.emptySet());
            }

            public Generic asWildcardLowerBound(Annotation ... annotation) {
                return this.asWildcardLowerBound(Arrays.asList(annotation));
            }

            public Generic asWildcardLowerBound(List<? extends Annotation> annotations) {
                return this.asWildcardLowerBound(new AnnotationList.ForLoadedAnnotations(annotations));
            }

            public Generic asWildcardLowerBound(AnnotationDescription ... annotation) {
                return this.asWildcardLowerBound((Collection<? extends AnnotationDescription>)Arrays.asList(annotation));
            }

            public Generic asWildcardLowerBound(Collection<? extends AnnotationDescription> annotations) {
                return OfWildcardType.Latent.boundedBelow(this.build(), new AnnotationSource.Explicit(new ArrayList<AnnotationDescription>(annotations)));
            }

            public Builder asArray() {
                return this.asArray(1);
            }

            public Builder asArray(int arity) {
                if (arity < 1) {
                    throw new IllegalArgumentException("Cannot define an array of a non-positive arity: " + arity);
                }
                Generic typeDescription = this.build();
                while (--arity > 0) {
                    typeDescription = new OfGenericArray.Latent(typeDescription, AnnotationSource.Empty.INSTANCE);
                }
                return new OfGenericArrayType(typeDescription);
            }

            public Builder annotate(Annotation ... annotation) {
                return this.annotate(Arrays.asList(annotation));
            }

            public Builder annotate(List<? extends Annotation> annotations) {
                return this.annotate(new AnnotationList.ForLoadedAnnotations(annotations));
            }

            public Builder annotate(AnnotationDescription ... annotation) {
                return this.annotate((Collection<? extends AnnotationDescription>)Arrays.asList(annotation));
            }

            public Builder annotate(Collection<? extends AnnotationDescription> annotations) {
                return this.doAnnotate(new ArrayList<AnnotationDescription>(annotations));
            }

            protected abstract Builder doAnnotate(List<? extends AnnotationDescription> var1);

            public Generic build() {
                return this.doBuild();
            }

            public Generic build(Annotation ... annotation) {
                return this.build(Arrays.asList(annotation));
            }

            public Generic build(List<? extends Annotation> annotations) {
                return this.build(new AnnotationList.ForLoadedAnnotations(annotations));
            }

            public Generic build(AnnotationDescription ... annotation) {
                return this.build((Collection<? extends AnnotationDescription>)Arrays.asList(annotation));
            }

            public Generic build(Collection<? extends AnnotationDescription> annotations) {
                return this.doAnnotate(new ArrayList<AnnotationDescription>(annotations)).doBuild();
            }

            protected abstract Generic doBuild();

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
                return ((Object)this.annotations).equals(((Builder)object).annotations);
            }

            public int hashCode() {
                return this.getClass().hashCode() * 31 + ((Object)this.annotations).hashCode();
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            @HashCodeAndEqualsPlugin.Enhance
            protected static class OfTypeVariable
            extends Builder {
                private final String symbol;

                protected OfTypeVariable(String symbol) {
                    this(symbol, Collections.emptyList());
                }

                protected OfTypeVariable(String symbol, List<? extends AnnotationDescription> annotations) {
                    super(annotations);
                    this.symbol = symbol;
                }

                @Override
                protected Builder doAnnotate(List<? extends AnnotationDescription> annotations) {
                    return new OfTypeVariable(this.symbol, CompoundList.of(this.annotations, annotations));
                }

                @Override
                protected Generic doBuild() {
                    return new OfTypeVariable.Symbolic(this.symbol, new AnnotationSource.Explicit(this.annotations));
                }

                @Override
                public boolean equals(@MaybeNull Object object) {
                    if (!super.equals(object)) {
                        return false;
                    }
                    if (this == object) {
                        return true;
                    }
                    if (object == null) {
                        return false;
                    }
                    if (this.getClass() != object.getClass()) {
                        return false;
                    }
                    return this.symbol.equals(((OfTypeVariable)object).symbol);
                }

                @Override
                public int hashCode() {
                    return super.hashCode() * 31 + this.symbol.hashCode();
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            @HashCodeAndEqualsPlugin.Enhance
            protected static class OfGenericArrayType
            extends Builder {
                private final Generic componentType;

                protected OfGenericArrayType(Generic componentType) {
                    this(componentType, Collections.emptyList());
                }

                protected OfGenericArrayType(Generic componentType, List<? extends AnnotationDescription> annotations) {
                    super(annotations);
                    this.componentType = componentType;
                }

                @Override
                protected Builder doAnnotate(List<? extends AnnotationDescription> annotations) {
                    return new OfGenericArrayType(this.componentType, CompoundList.of(this.annotations, annotations));
                }

                @Override
                protected Generic doBuild() {
                    return new OfGenericArray.Latent(this.componentType, new AnnotationSource.Explicit(this.annotations));
                }

                @Override
                public boolean equals(@MaybeNull Object object) {
                    if (!super.equals(object)) {
                        return false;
                    }
                    if (this == object) {
                        return true;
                    }
                    if (object == null) {
                        return false;
                    }
                    if (this.getClass() != object.getClass()) {
                        return false;
                    }
                    return this.componentType.equals(((OfGenericArrayType)object).componentType);
                }

                @Override
                public int hashCode() {
                    return super.hashCode() * 31 + this.componentType.hashCode();
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            @HashCodeAndEqualsPlugin.Enhance
            protected static class OfParameterizedType
            extends Builder {
                private final TypeDescription rawType;
                @MaybeNull
                @HashCodeAndEqualsPlugin.ValueHandling(value=HashCodeAndEqualsPlugin.ValueHandling.Sort.REVERSE_NULLABILITY)
                private final Generic ownerType;
                private final List<? extends Generic> parameterTypes;

                protected OfParameterizedType(TypeDescription rawType, @MaybeNull Generic ownerType, List<? extends Generic> parameterTypes) {
                    this(rawType, ownerType, parameterTypes, Collections.emptyList());
                }

                protected OfParameterizedType(TypeDescription rawType, @MaybeNull Generic ownerType, List<? extends Generic> parameterTypes, List<? extends AnnotationDescription> annotations) {
                    super(annotations);
                    this.rawType = rawType;
                    this.ownerType = ownerType;
                    this.parameterTypes = parameterTypes;
                }

                @Override
                protected Builder doAnnotate(List<? extends AnnotationDescription> annotations) {
                    return new OfParameterizedType(this.rawType, this.ownerType, this.parameterTypes, CompoundList.of(this.annotations, annotations));
                }

                @Override
                protected Generic doBuild() {
                    return new OfParameterizedType.Latent(this.rawType, this.ownerType, this.parameterTypes, new AnnotationSource.Explicit(this.annotations));
                }

                @Override
                public boolean equals(@MaybeNull Object object) {
                    block12: {
                        block11: {
                            Generic generic;
                            block10: {
                                Generic generic2;
                                if (!super.equals(object)) {
                                    return false;
                                }
                                if (this == object) {
                                    return true;
                                }
                                if (object == null) {
                                    return false;
                                }
                                if (this.getClass() != object.getClass()) {
                                    return false;
                                }
                                if (!this.rawType.equals(((OfParameterizedType)object).rawType)) {
                                    return false;
                                }
                                Generic generic3 = ((OfParameterizedType)object).ownerType;
                                generic = generic2 = this.ownerType;
                                if (generic3 == null) break block10;
                                if (generic == null) break block11;
                                if (!generic2.equals(generic3)) {
                                    return false;
                                }
                                break block12;
                            }
                            if (generic == null) break block12;
                        }
                        return false;
                    }
                    return ((Object)this.parameterTypes).equals(((OfParameterizedType)object).parameterTypes);
                }

                @Override
                public int hashCode() {
                    int n = (super.hashCode() * 31 + this.rawType.hashCode()) * 31;
                    Generic generic = this.ownerType;
                    if (generic != null) {
                        n = n + generic.hashCode();
                    }
                    return n * 31 + ((Object)this.parameterTypes).hashCode();
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            @HashCodeAndEqualsPlugin.Enhance
            protected static class OfNonGenericType
            extends Builder {
                private final TypeDescription typeDescription;
                @MaybeNull
                @HashCodeAndEqualsPlugin.ValueHandling(value=HashCodeAndEqualsPlugin.ValueHandling.Sort.REVERSE_NULLABILITY)
                private final Generic ownerType;

                protected OfNonGenericType(TypeDescription typeDescription) {
                    this(typeDescription, typeDescription.getDeclaringType());
                }

                protected OfNonGenericType(TypeDescription typeDescription, @MaybeNull TypeDescription ownerType) {
                    this(typeDescription, ownerType == null ? Generic.UNDEFINED : ownerType.asGenericType());
                }

                protected OfNonGenericType(TypeDescription typeDescription, @MaybeNull Generic ownerType) {
                    this(typeDescription, ownerType, Collections.emptyList());
                }

                protected OfNonGenericType(TypeDescription typeDescription, @MaybeNull Generic ownerType, List<? extends AnnotationDescription> annotations) {
                    super(annotations);
                    this.ownerType = ownerType;
                    this.typeDescription = typeDescription;
                }

                @Override
                protected Builder doAnnotate(List<? extends AnnotationDescription> annotations) {
                    return new OfNonGenericType(this.typeDescription, this.ownerType, CompoundList.of(this.annotations, annotations));
                }

                @Override
                protected Generic doBuild() {
                    if (this.typeDescription.represents(Void.TYPE) && !this.annotations.isEmpty()) {
                        throw new IllegalArgumentException("The void non-type cannot be annotated");
                    }
                    return new OfNonGenericType.Latent(this.typeDescription, this.ownerType, (AnnotationSource)new AnnotationSource.Explicit(this.annotations));
                }

                @Override
                public boolean equals(@MaybeNull Object object) {
                    block12: {
                        block11: {
                            Generic generic;
                            block10: {
                                Generic generic2;
                                if (!super.equals(object)) {
                                    return false;
                                }
                                if (this == object) {
                                    return true;
                                }
                                if (object == null) {
                                    return false;
                                }
                                if (this.getClass() != object.getClass()) {
                                    return false;
                                }
                                if (!this.typeDescription.equals(((OfNonGenericType)object).typeDescription)) {
                                    return false;
                                }
                                Generic generic3 = ((OfNonGenericType)object).ownerType;
                                generic = generic2 = this.ownerType;
                                if (generic3 == null) break block10;
                                if (generic == null) break block11;
                                if (!generic2.equals(generic3)) {
                                    return false;
                                }
                                break block12;
                            }
                            if (generic == null) break block12;
                        }
                        return false;
                    }
                    return true;
                }

                @Override
                public int hashCode() {
                    int n = (super.hashCode() * 31 + this.typeDescription.hashCode()) * 31;
                    Generic generic = this.ownerType;
                    if (generic != null) {
                        n = n + generic.hashCode();
                    }
                    return n;
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            protected static enum Visitor implements net.bytebuddy.description.type.TypeDescription$Generic$Visitor<Builder>
            {
                INSTANCE;


                @Override
                @SuppressFBWarnings(value={"NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE"}, justification="Assuming component type for array type.")
                public Builder onGenericArray(Generic genericArray) {
                    return new OfGenericArrayType(genericArray.getComponentType(), genericArray.getDeclaredAnnotations());
                }

                @Override
                public Builder onWildcard(Generic wildcard) {
                    throw new IllegalArgumentException("Cannot resolve wildcard type " + wildcard + " to builder");
                }

                @Override
                public Builder onParameterizedType(Generic parameterizedType) {
                    return new OfParameterizedType(parameterizedType.asErasure(), parameterizedType.getOwnerType(), parameterizedType.getTypeArguments(), parameterizedType.getDeclaredAnnotations());
                }

                @Override
                public Builder onTypeVariable(Generic typeVariable) {
                    return new OfTypeVariable(typeVariable.getSymbol(), typeVariable.getDeclaredAnnotations());
                }

                @Override
                @SuppressFBWarnings(value={"NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE"}, justification="Assuming component type for array type.")
                public Builder onNonGenericType(Generic typeDescription) {
                    return typeDescription.isArray() ? typeDescription.getComponentType().accept(this).asArray().annotate(typeDescription.getDeclaredAnnotations()) : new OfNonGenericType(typeDescription.asErasure(), typeDescription.getOwnerType(), typeDescription.getDeclaredAnnotations());
                }
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static abstract class LazyProjection
        extends AbstractBase {
            private transient /* synthetic */ int hashCode;

            protected abstract Generic resolve();

            @Override
            public TypeDefinition.Sort getSort() {
                return this.resolve().getSort();
            }

            @Override
            public FieldList<FieldDescription.InGenericShape> getDeclaredFields() {
                return this.resolve().getDeclaredFields();
            }

            @Override
            public MethodList<MethodDescription.InGenericShape> getDeclaredMethods() {
                return this.resolve().getDeclaredMethods();
            }

            @Override
            public RecordComponentList<RecordComponentDescription.InGenericShape> getRecordComponents() {
                return this.resolve().getRecordComponents();
            }

            @Override
            public TypeList.Generic getUpperBounds() {
                return this.resolve().getUpperBounds();
            }

            @Override
            public TypeList.Generic getLowerBounds() {
                return this.resolve().getLowerBounds();
            }

            @Override
            @MaybeNull
            public Generic getComponentType() {
                return this.resolve().getComponentType();
            }

            @Override
            public TypeList.Generic getTypeArguments() {
                return this.resolve().getTypeArguments();
            }

            @Override
            @MaybeNull
            public Generic findBindingOf(Generic typeVariable) {
                return this.resolve().findBindingOf(typeVariable);
            }

            @Override
            public TypeVariableSource getTypeVariableSource() {
                return this.resolve().getTypeVariableSource();
            }

            @Override
            @MaybeNull
            public Generic getOwnerType() {
                return this.resolve().getOwnerType();
            }

            @Override
            public String getTypeName() {
                return this.resolve().getTypeName();
            }

            @Override
            public String getSymbol() {
                return this.resolve().getSymbol();
            }

            @Override
            public String getActualName() {
                return this.resolve().getActualName();
            }

            @Override
            public <T> T accept(Visitor<T> visitor) {
                return this.resolve().accept(visitor);
            }

            @Override
            public StackSize getStackSize() {
                return this.asErasure().getStackSize();
            }

            @Override
            public boolean isArray() {
                return this.asErasure().isArray();
            }

            @Override
            public boolean isPrimitive() {
                return this.asErasure().isPrimitive();
            }

            @Override
            public boolean isRecord() {
                return this.asErasure().isRecord();
            }

            @Override
            public boolean represents(java.lang.reflect.Type type) {
                return this.resolve().represents(type);
            }

            @CachedReturnPlugin.Enhance(value="hashCode")
            public int hashCode() {
                int n;
                int n2;
                int n3 = this.hashCode;
                if (n3 != 0) {
                    n2 = 0;
                } else {
                    LazyProjection lazyProjection = this;
                    n2 = n = lazyProjection.resolve().hashCode();
                }
                if (n == 0) {
                    n = this.hashCode;
                } else {
                    this.hashCode = n;
                }
                return n;
            }

            public boolean equals(@MaybeNull Object other) {
                return this == other || other instanceof TypeDefinition && this.resolve().equals(other);
            }

            public String toString() {
                return this.resolve().toString();
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            public static class WithResolvedErasure
            extends WithEagerNavigation {
                private final Generic delegate;
                private final Visitor<? extends Generic> visitor;
                private final AnnotationSource annotationSource;
                private transient /* synthetic */ Generic resolved;

                public WithResolvedErasure(Generic delegate, Visitor<? extends Generic> visitor) {
                    this(delegate, visitor, delegate);
                }

                public WithResolvedErasure(Generic delegate, Visitor<? extends Generic> visitor, AnnotationSource annotationSource) {
                    this.delegate = delegate;
                    this.visitor = visitor;
                    this.annotationSource = annotationSource;
                }

                @Override
                public AnnotationList getDeclaredAnnotations() {
                    return this.annotationSource.getDeclaredAnnotations();
                }

                @Override
                public TypeDescription asErasure() {
                    return this.delegate.asErasure();
                }

                @Override
                @CachedReturnPlugin.Enhance(value="resolved")
                protected Generic resolve() {
                    Generic generic;
                    Generic generic2;
                    Generic generic3 = this.resolved;
                    if (generic3 != null) {
                        generic2 = null;
                    } else {
                        generic = this;
                        generic2 = generic = generic.delegate.accept(generic.visitor);
                    }
                    if (generic == null) {
                        generic = this.resolved;
                    } else {
                        this.resolved = generic;
                    }
                    return generic;
                }
            }

            public static class OfRecordComponent
            extends WithEagerNavigation.OfAnnotatedElement {
                private final Object recordComponent;
                private transient /* synthetic */ Generic resolved;

                protected OfRecordComponent(Object recordComponent) {
                    this.recordComponent = recordComponent;
                }

                @CachedReturnPlugin.Enhance(value="resolved")
                protected Generic resolve() {
                    Generic generic;
                    Generic generic2;
                    Generic generic3 = this.resolved;
                    if (generic3 != null) {
                        generic2 = null;
                    } else {
                        generic = this;
                        generic2 = generic = TypeDefinition.Sort.describe(RecordComponentDescription.ForLoadedRecordComponent.RECORD_COMPONENT.getGenericType(generic.recordComponent), generic.getAnnotationReader());
                    }
                    if (generic == null) {
                        generic = this.resolved;
                    } else {
                        this.resolved = generic;
                    }
                    return generic;
                }

                public TypeDescription asErasure() {
                    return ForLoadedType.of(RecordComponentDescription.ForLoadedRecordComponent.RECORD_COMPONENT.getType(this.recordComponent));
                }

                protected AnnotationReader getAnnotationReader() {
                    return new AnnotationReader.Delegator.ForLoadedRecordComponent(this.recordComponent);
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            public static class OfMethodParameter
            extends WithEagerNavigation.OfAnnotatedElement {
                private final Method method;
                private final int index;
                private final Class<?>[] erasure;
                private transient /* synthetic */ Generic resolved;

                @SuppressFBWarnings(value={"EI_EXPOSE_REP2"}, justification="The array is not modified by class contract.")
                public OfMethodParameter(Method method, int index, Class<?>[] erasure) {
                    this.method = method;
                    this.index = index;
                    this.erasure = erasure;
                }

                @Override
                @CachedReturnPlugin.Enhance(value="resolved")
                protected Generic resolve() {
                    Generic generic;
                    Generic generic2;
                    Generic generic3 = this.resolved;
                    if (generic3 != null) {
                        generic2 = null;
                    } else {
                        generic = this;
                        java.lang.reflect.Type[] type = ((OfMethodParameter)generic).method.getGenericParameterTypes();
                        generic2 = generic = ((OfMethodParameter)generic).erasure.length == type.length ? TypeDefinition.Sort.describe(type[((OfMethodParameter)generic).index], ((OfMethodParameter)generic).getAnnotationReader()) : OfNonGenericType.ForLoadedType.of(((OfMethodParameter)generic).erasure[((OfMethodParameter)generic).index]);
                    }
                    if (generic == null) {
                        generic = this.resolved;
                    } else {
                        this.resolved = generic;
                    }
                    return generic;
                }

                @Override
                public TypeDescription asErasure() {
                    return ForLoadedType.of(this.erasure[this.index]);
                }

                @Override
                protected AnnotationReader getAnnotationReader() {
                    return new AnnotationReader.Delegator.ForLoadedExecutableParameterType(this.method, this.index);
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            public static class OfConstructorParameter
            extends WithEagerNavigation.OfAnnotatedElement {
                private final Constructor<?> constructor;
                private final int index;
                private final Class<?>[] erasure;
                private transient /* synthetic */ Generic delegate;

                @SuppressFBWarnings(value={"EI_EXPOSE_REP2"}, justification="The array is not modified by class contract.")
                public OfConstructorParameter(Constructor<?> constructor, int index, Class<?>[] erasure) {
                    this.constructor = constructor;
                    this.index = index;
                    this.erasure = erasure;
                }

                @Override
                @CachedReturnPlugin.Enhance(value="delegate")
                protected Generic resolve() {
                    Generic generic;
                    Generic generic2;
                    Generic generic3 = this.delegate;
                    if (generic3 != null) {
                        generic2 = null;
                    } else {
                        generic = this;
                        java.lang.reflect.Type[] type = ((OfConstructorParameter)generic).constructor.getGenericParameterTypes();
                        generic2 = generic = ((OfConstructorParameter)generic).erasure.length == type.length ? TypeDefinition.Sort.describe(type[((OfConstructorParameter)generic).index], ((OfConstructorParameter)generic).getAnnotationReader()) : OfNonGenericType.ForLoadedType.of(((OfConstructorParameter)generic).erasure[((OfConstructorParameter)generic).index]);
                    }
                    if (generic == null) {
                        generic = this.delegate;
                    } else {
                        this.delegate = generic;
                    }
                    return generic;
                }

                @Override
                public TypeDescription asErasure() {
                    return ForLoadedType.of(this.erasure[this.index]);
                }

                @Override
                protected AnnotationReader getAnnotationReader() {
                    return new AnnotationReader.Delegator.ForLoadedExecutableParameterType(this.constructor, this.index);
                }
            }

            public static class ForLoadedReturnType
            extends WithEagerNavigation.OfAnnotatedElement {
                private final Method method;
                private transient /* synthetic */ Generic resolved;

                public ForLoadedReturnType(Method method) {
                    this.method = method;
                }

                @CachedReturnPlugin.Enhance(value="resolved")
                protected Generic resolve() {
                    Generic generic;
                    Generic generic2;
                    Generic generic3 = this.resolved;
                    if (generic3 != null) {
                        generic2 = null;
                    } else {
                        generic = this;
                        generic2 = generic = TypeDefinition.Sort.describe(generic.method.getGenericReturnType(), generic.getAnnotationReader());
                    }
                    if (generic == null) {
                        generic = this.resolved;
                    } else {
                        this.resolved = generic;
                    }
                    return generic;
                }

                public TypeDescription asErasure() {
                    return ForLoadedType.of(this.method.getReturnType());
                }

                protected AnnotationReader getAnnotationReader() {
                    return new AnnotationReader.Delegator.ForLoadedMethodReturnType(this.method);
                }
            }

            public static class ForLoadedFieldType
            extends WithEagerNavigation.OfAnnotatedElement {
                private final Field field;
                private transient /* synthetic */ Generic resolved;

                public ForLoadedFieldType(Field field) {
                    this.field = field;
                }

                @CachedReturnPlugin.Enhance(value="resolved")
                protected Generic resolve() {
                    Generic generic;
                    Generic generic2;
                    Generic generic3 = this.resolved;
                    if (generic3 != null) {
                        generic2 = null;
                    } else {
                        generic = this;
                        generic2 = generic = TypeDefinition.Sort.describe(generic.field.getGenericType(), generic.getAnnotationReader());
                    }
                    if (generic == null) {
                        generic = this.resolved;
                    } else {
                        this.resolved = generic;
                    }
                    return generic;
                }

                public TypeDescription asErasure() {
                    return ForLoadedType.of(this.field.getType());
                }

                protected AnnotationReader getAnnotationReader() {
                    return new AnnotationReader.Delegator.ForLoadedField(this.field);
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            public static class ForLoadedSuperClass
            extends WithLazyNavigation.OfAnnotatedElement {
                private final Class<?> type;
                private transient /* synthetic */ Generic resolved;

                protected ForLoadedSuperClass(Class<?> type) {
                    this.type = type;
                }

                @MaybeNull
                public static Generic of(Class<?> type) {
                    return type.getSuperclass() == null ? UNDEFINED : new ForLoadedSuperClass(type);
                }

                @Override
                @CachedReturnPlugin.Enhance(value="resolved")
                protected Generic resolve() {
                    Generic generic;
                    Generic generic2;
                    Generic generic3 = this.resolved;
                    if (generic3 != null) {
                        generic2 = null;
                    } else {
                        generic = this;
                        generic2 = generic = TypeDefinition.Sort.describe(generic.type.getGenericSuperclass(), generic.getAnnotationReader());
                    }
                    if (generic == null) {
                        generic = this.resolved;
                    } else {
                        this.resolved = generic;
                    }
                    return generic;
                }

                @Override
                public TypeDescription asErasure() {
                    return ForLoadedType.of(this.type.getSuperclass());
                }

                @Override
                protected AnnotationReader getAnnotationReader() {
                    return new AnnotationReader.Delegator.ForLoadedSuperClass(this.type);
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            public static abstract class WithEagerNavigation
            extends LazyProjection {
                @Override
                @MaybeNull
                public Generic getSuperClass() {
                    return this.resolve().getSuperClass();
                }

                @Override
                public TypeList.Generic getInterfaces() {
                    return this.resolve().getInterfaces();
                }

                @Override
                public Iterator<TypeDefinition> iterator() {
                    return this.resolve().iterator();
                }

                protected static abstract class OfAnnotatedElement
                extends WithEagerNavigation {
                    protected OfAnnotatedElement() {
                    }

                    protected abstract AnnotationReader getAnnotationReader();

                    public AnnotationList getDeclaredAnnotations() {
                        return this.getAnnotationReader().asList();
                    }
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            public static abstract class WithLazyNavigation
            extends LazyProjection {
                @Override
                @MaybeNull
                public Generic getSuperClass() {
                    return LazySuperClass.of(this);
                }

                @Override
                public TypeList.Generic getInterfaces() {
                    return LazyInterfaceList.of(this);
                }

                @Override
                public Iterator<TypeDefinition> iterator() {
                    return new TypeDefinition.SuperClassIterator(this);
                }

                protected static abstract class OfAnnotatedElement
                extends WithLazyNavigation {
                    protected OfAnnotatedElement() {
                    }

                    protected abstract AnnotationReader getAnnotationReader();

                    public AnnotationList getDeclaredAnnotations() {
                        return this.getAnnotationReader().asList();
                    }
                }

                protected static class LazyInterfaceList
                extends TypeList.Generic.AbstractBase {
                    private final LazyProjection delegate;
                    private final TypeList.Generic rawInterfaces;

                    protected LazyInterfaceList(LazyProjection delegate, TypeList.Generic rawInterfaces) {
                        this.delegate = delegate;
                        this.rawInterfaces = rawInterfaces;
                    }

                    protected static TypeList.Generic of(LazyProjection delegate) {
                        return new LazyInterfaceList(delegate, delegate.asErasure().getInterfaces());
                    }

                    public Generic get(int index) {
                        return new LazyInterfaceType(this.delegate, index, (Generic)this.rawInterfaces.get(index));
                    }

                    public int size() {
                        return this.rawInterfaces.size();
                    }
                }

                protected static class LazyInterfaceType
                extends WithLazyNavigation {
                    private final LazyProjection delegate;
                    private final int index;
                    private final Generic rawInterface;
                    private transient /* synthetic */ Generic resolved;

                    protected LazyInterfaceType(LazyProjection delegate, int index, Generic rawInterface) {
                        this.delegate = delegate;
                        this.index = index;
                        this.rawInterface = rawInterface;
                    }

                    public AnnotationList getDeclaredAnnotations() {
                        return this.resolve().getDeclaredAnnotations();
                    }

                    public TypeDescription asErasure() {
                        return this.rawInterface.asErasure();
                    }

                    @CachedReturnPlugin.Enhance(value="resolved")
                    protected Generic resolve() {
                        Generic generic;
                        Generic generic2;
                        Generic generic3 = this.resolved;
                        if (generic3 != null) {
                            generic2 = null;
                        } else {
                            generic = this;
                            generic2 = generic = (Generic)generic.delegate.resolve().getInterfaces().get(generic.index);
                        }
                        if (generic == null) {
                            generic = this.resolved;
                        } else {
                            this.resolved = generic;
                        }
                        return generic;
                    }
                }

                protected static class LazySuperClass
                extends WithLazyNavigation {
                    private final LazyProjection delegate;
                    private transient /* synthetic */ Generic resolved;

                    protected LazySuperClass(LazyProjection delegate) {
                        this.delegate = delegate;
                    }

                    @MaybeNull
                    protected static Generic of(LazyProjection delegate) {
                        return delegate.asErasure().getSuperClass() == null ? UNDEFINED : new LazySuperClass(delegate);
                    }

                    public AnnotationList getDeclaredAnnotations() {
                        return this.resolve().getDeclaredAnnotations();
                    }

                    @SuppressFBWarnings(value={"NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE"}, justification="Assuming super class for given instance.")
                    public TypeDescription asErasure() {
                        return this.delegate.asErasure().getSuperClass().asErasure();
                    }

                    @CachedReturnPlugin.Enhance(value="resolved")
                    @SuppressFBWarnings(value={"NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE"}, justification="Assuming super class for given instance.")
                    protected Generic resolve() {
                        Generic generic;
                        Generic generic2;
                        Generic generic3 = this.resolved;
                        if (generic3 != null) {
                            generic2 = null;
                        } else {
                            generic = this;
                            generic2 = generic = generic.delegate.resolve().getSuperClass();
                        }
                        if (generic == null) {
                            generic = this.resolved;
                        } else {
                            this.resolved = generic;
                        }
                        return generic;
                    }
                }
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static abstract class OfTypeVariable
        extends AbstractBase {
            private transient /* synthetic */ int hashCode;

            @Override
            public TypeDefinition.Sort getSort() {
                return TypeDefinition.Sort.VARIABLE;
            }

            @Override
            public TypeDescription asErasure() {
                TypeList.Generic upperBounds = this.getUpperBounds();
                return upperBounds.isEmpty() ? net.bytebuddy.description.type.TypeDescription$ForLoadedType.of(Object.class) : ((Generic)upperBounds.get(0)).asErasure();
            }

            @Override
            @MaybeNull
            public Generic getSuperClass() {
                throw new IllegalStateException("A type variable does not imply a super type definition: " + this);
            }

            @Override
            public TypeList.Generic getInterfaces() {
                throw new IllegalStateException("A type variable does not imply an interface type definition: " + this);
            }

            @Override
            public FieldList<FieldDescription.InGenericShape> getDeclaredFields() {
                throw new IllegalStateException("A type variable does not imply field definitions: " + this);
            }

            @Override
            public MethodList<MethodDescription.InGenericShape> getDeclaredMethods() {
                throw new IllegalStateException("A type variable does not imply method definitions: " + this);
            }

            @Override
            public RecordComponentList<RecordComponentDescription.InGenericShape> getRecordComponents() {
                throw new IllegalStateException("A type variable does not imply record component definitions: " + this);
            }

            @Override
            public Generic getComponentType() {
                throw new IllegalStateException("A type variable does not imply a component type: " + this);
            }

            @Override
            public TypeList.Generic getTypeArguments() {
                throw new IllegalStateException("A type variable does not imply type arguments: " + this);
            }

            @Override
            public Generic findBindingOf(Generic typeVariable) {
                throw new IllegalStateException("A type variable does not imply type arguments: " + this);
            }

            @Override
            public TypeList.Generic getLowerBounds() {
                throw new IllegalStateException("A type variable does not imply lower bounds: " + this);
            }

            @Override
            public Generic getOwnerType() {
                throw new IllegalStateException("A type variable does not imply an owner type: " + this);
            }

            @Override
            public String getTypeName() {
                return this.toString();
            }

            @Override
            public String getActualName() {
                return this.getSymbol();
            }

            @Override
            public <T> T accept(Visitor<T> visitor) {
                return visitor.onTypeVariable(this);
            }

            @Override
            public StackSize getStackSize() {
                return StackSize.SINGLE;
            }

            @Override
            public boolean isArray() {
                return false;
            }

            @Override
            public boolean isPrimitive() {
                return false;
            }

            @Override
            public boolean isRecord() {
                return false;
            }

            @Override
            public boolean represents(java.lang.reflect.Type type) {
                return this.equals(TypeDefinition.Sort.describe(type));
            }

            @Override
            public Iterator<TypeDefinition> iterator() {
                throw new IllegalStateException("A type variable does not imply a super type definition: " + this);
            }

            @CachedReturnPlugin.Enhance(value="hashCode")
            public int hashCode() {
                int n;
                int n2;
                int n3 = this.hashCode;
                if (n3 != 0) {
                    n2 = 0;
                } else {
                    OfTypeVariable ofTypeVariable = this;
                    n2 = n = ofTypeVariable.getTypeVariableSource().hashCode() ^ ofTypeVariable.getSymbol().hashCode();
                }
                if (n == 0) {
                    n = this.hashCode;
                } else {
                    this.hashCode = n;
                }
                return n;
            }

            public boolean equals(@MaybeNull Object other) {
                if (this == other) {
                    return true;
                }
                if (!(other instanceof Generic)) {
                    return false;
                }
                Generic typeDescription = (Generic)other;
                return typeDescription.getSort().isTypeVariable() && this.getSymbol().equals(typeDescription.getSymbol()) && this.getTypeVariableSource().equals(typeDescription.getTypeVariableSource());
            }

            public String toString() {
                return this.getSymbol();
            }

            public static class WithAnnotationOverlay
            extends OfTypeVariable {
                private final Generic typeVariable;
                private final AnnotationSource annotationSource;

                public WithAnnotationOverlay(Generic typeVariable, AnnotationSource annotationSource) {
                    this.typeVariable = typeVariable;
                    this.annotationSource = annotationSource;
                }

                public AnnotationList getDeclaredAnnotations() {
                    return this.annotationSource.getDeclaredAnnotations();
                }

                public TypeList.Generic getUpperBounds() {
                    return this.typeVariable.getUpperBounds();
                }

                public TypeVariableSource getTypeVariableSource() {
                    return this.typeVariable.getTypeVariableSource();
                }

                public String getSymbol() {
                    return this.typeVariable.getSymbol();
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            public static class ForLoadedType
            extends OfTypeVariable {
                private final TypeVariable<?> typeVariable;
                private final AnnotationReader annotationReader;

                public ForLoadedType(TypeVariable<?> typeVariable) {
                    this(typeVariable, AnnotationReader.NoOp.INSTANCE);
                }

                protected ForLoadedType(TypeVariable<?> typeVariable, AnnotationReader annotationReader) {
                    this.typeVariable = typeVariable;
                    this.annotationReader = annotationReader;
                }

                @Override
                public TypeVariableSource getTypeVariableSource() {
                    Object genericDeclaration = this.typeVariable.getGenericDeclaration();
                    if (genericDeclaration instanceof Class) {
                        return net.bytebuddy.description.type.TypeDescription$ForLoadedType.of((Class)genericDeclaration);
                    }
                    if (genericDeclaration instanceof Method) {
                        return new MethodDescription.ForLoadedMethod((Method)genericDeclaration);
                    }
                    if (genericDeclaration instanceof Constructor) {
                        return new MethodDescription.ForLoadedConstructor((Constructor)genericDeclaration);
                    }
                    throw new IllegalStateException("Unknown declaration: " + genericDeclaration);
                }

                @Override
                public TypeList.Generic getUpperBounds() {
                    return new TypeVariableBoundList(this.typeVariable.getBounds(), this.annotationReader);
                }

                @Override
                public String getSymbol() {
                    return this.typeVariable.getName();
                }

                @Override
                public AnnotationList getDeclaredAnnotations() {
                    return this.annotationReader.asList();
                }

                @Override
                public boolean represents(java.lang.reflect.Type type) {
                    return this.typeVariable == type || super.represents(type);
                }

                protected static class TypeVariableBoundList
                extends TypeList.Generic.AbstractBase {
                    private final java.lang.reflect.Type[] bound;
                    private final AnnotationReader annotationReader;

                    protected TypeVariableBoundList(java.lang.reflect.Type[] bound, AnnotationReader annotationReader) {
                        this.bound = bound;
                        this.annotationReader = annotationReader;
                    }

                    public Generic get(int index) {
                        return TypeDefinition.Sort.describe(this.bound[index], this.annotationReader.ofTypeVariableBoundType(index));
                    }

                    public int size() {
                        return this.bound.length;
                    }
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            public static class Symbolic
            extends AbstractBase {
                private final String symbol;
                private final AnnotationSource annotationSource;

                public Symbolic(String symbol, AnnotationSource annotationSource) {
                    this.symbol = symbol;
                    this.annotationSource = annotationSource;
                }

                @Override
                public TypeDefinition.Sort getSort() {
                    return TypeDefinition.Sort.VARIABLE_SYMBOLIC;
                }

                @Override
                public String getSymbol() {
                    return this.symbol;
                }

                @Override
                public AnnotationList getDeclaredAnnotations() {
                    return this.annotationSource.getDeclaredAnnotations();
                }

                @Override
                public TypeDescription asErasure() {
                    throw new IllegalStateException("A symbolic type variable does not imply an erasure: " + this);
                }

                @Override
                public TypeList.Generic getUpperBounds() {
                    throw new IllegalStateException("A symbolic type variable does not imply an upper type bound: " + this);
                }

                @Override
                public TypeVariableSource getTypeVariableSource() {
                    throw new IllegalStateException("A symbolic type variable does not imply a variable source: " + this);
                }

                @Override
                @MaybeNull
                public Generic getSuperClass() {
                    throw new IllegalStateException("A symbolic type variable does not imply a super type definition: " + this);
                }

                @Override
                public TypeList.Generic getInterfaces() {
                    throw new IllegalStateException("A symbolic type variable does not imply an interface type definition: " + this);
                }

                @Override
                public FieldList<FieldDescription.InGenericShape> getDeclaredFields() {
                    throw new IllegalStateException("A symbolic type variable does not imply field definitions: " + this);
                }

                @Override
                public MethodList<MethodDescription.InGenericShape> getDeclaredMethods() {
                    throw new IllegalStateException("A symbolic type variable does not imply method definitions: " + this);
                }

                @Override
                public RecordComponentList<RecordComponentDescription.InGenericShape> getRecordComponents() {
                    throw new IllegalStateException("A symbolic type variable does not imply record component definitions: " + this);
                }

                @Override
                public Generic getComponentType() {
                    throw new IllegalStateException("A symbolic type variable does not imply a component type: " + this);
                }

                @Override
                public TypeList.Generic getTypeArguments() {
                    throw new IllegalStateException("A symbolic type variable does not imply type arguments: " + this);
                }

                @Override
                public Generic findBindingOf(Generic typeVariable) {
                    throw new IllegalStateException("A symbolic type variable does not imply type arguments: " + this);
                }

                @Override
                public TypeList.Generic getLowerBounds() {
                    throw new IllegalStateException("A symbolic type variable does not imply lower bounds: " + this);
                }

                @Override
                public Generic getOwnerType() {
                    throw new IllegalStateException("A symbolic type variable does not imply an owner type: " + this);
                }

                @Override
                public String getTypeName() {
                    return this.toString();
                }

                @Override
                public String getActualName() {
                    return this.getSymbol();
                }

                @Override
                public <T> T accept(Visitor<T> visitor) {
                    return visitor.onTypeVariable(this);
                }

                @Override
                public StackSize getStackSize() {
                    return StackSize.SINGLE;
                }

                @Override
                public boolean isArray() {
                    return false;
                }

                @Override
                public boolean isPrimitive() {
                    return false;
                }

                @Override
                public boolean isRecord() {
                    return false;
                }

                @Override
                public boolean represents(java.lang.reflect.Type type) {
                    if (type == null) {
                        throw new NullPointerException();
                    }
                    return false;
                }

                @Override
                public Iterator<TypeDefinition> iterator() {
                    throw new IllegalStateException("A symbolic type variable does not imply a super type definition: " + this);
                }

                public int hashCode() {
                    return this.symbol.hashCode();
                }

                public boolean equals(@MaybeNull Object other) {
                    if (this == other) {
                        return true;
                    }
                    if (!(other instanceof Generic)) {
                        return false;
                    }
                    Generic typeDescription = (Generic)other;
                    return typeDescription.getSort().isTypeVariable() && this.getSymbol().equals(typeDescription.getSymbol());
                }

                public String toString() {
                    return this.getSymbol();
                }
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static abstract class OfParameterizedType
        extends AbstractBase {
            private transient /* synthetic */ int hashCode;

            @Override
            public TypeDefinition.Sort getSort() {
                return TypeDefinition.Sort.PARAMETERIZED;
            }

            @Override
            @MaybeNull
            public Generic getSuperClass() {
                Generic superClass = this.asErasure().getSuperClass();
                return superClass == null ? UNDEFINED : new LazyProjection.WithResolvedErasure(superClass, new Visitor.Substitutor.ForTypeVariableBinding(this));
            }

            @Override
            public TypeList.Generic getInterfaces() {
                return new TypeList.Generic.ForDetachedTypes.WithResolvedErasure(this.asErasure().getInterfaces(), new Visitor.Substitutor.ForTypeVariableBinding(this));
            }

            @Override
            public FieldList<FieldDescription.InGenericShape> getDeclaredFields() {
                return new FieldList.TypeSubstituting(this, this.asErasure().getDeclaredFields(), new Visitor.Substitutor.ForTypeVariableBinding(this));
            }

            @Override
            public MethodList<MethodDescription.InGenericShape> getDeclaredMethods() {
                return new MethodList.TypeSubstituting(this, this.asErasure().getDeclaredMethods(), new Visitor.Substitutor.ForTypeVariableBinding(this));
            }

            @Override
            public RecordComponentList<RecordComponentDescription.InGenericShape> getRecordComponents() {
                return new RecordComponentList.TypeSubstituting(this, this.asErasure().getRecordComponents(), new Visitor.Substitutor.ForTypeVariableBinding(this));
            }

            @Override
            @MaybeNull
            public Generic findBindingOf(Generic typeVariable) {
                Generic typeDescription = this;
                do {
                    TypeList.Generic typeArguments = typeDescription.getTypeArguments();
                    TypeList.Generic typeVariables = typeDescription.asErasure().getTypeVariables();
                    for (int index = 0; index < Math.min(typeArguments.size(), typeVariables.size()); ++index) {
                        if (!typeVariable.equals(typeVariables.get(index))) continue;
                        return (Generic)typeArguments.get(index);
                    }
                } while ((typeDescription = typeDescription.getOwnerType()) != null && typeDescription.getSort().isParameterized());
                return UNDEFINED;
            }

            @Override
            public TypeList.Generic getUpperBounds() {
                throw new IllegalStateException("A parameterized type does not imply upper bounds: " + this);
            }

            @Override
            public TypeList.Generic getLowerBounds() {
                throw new IllegalStateException("A parameterized type does not imply lower bounds: " + this);
            }

            @Override
            public Generic getComponentType() {
                throw new IllegalStateException("A parameterized type does not imply a component type: " + this);
            }

            @Override
            public TypeVariableSource getTypeVariableSource() {
                throw new IllegalStateException("A parameterized type does not imply a type variable source: " + this);
            }

            @Override
            public String getSymbol() {
                throw new IllegalStateException("A parameterized type does not imply a symbol: " + this);
            }

            @Override
            public String getTypeName() {
                return this.toString();
            }

            @Override
            public String getActualName() {
                return this.toString();
            }

            @Override
            public boolean isPrimitive() {
                return false;
            }

            @Override
            public boolean isArray() {
                return false;
            }

            @Override
            public boolean isRecord() {
                return this.asErasure().isRecord();
            }

            @Override
            public boolean represents(java.lang.reflect.Type type) {
                return this.equals(TypeDefinition.Sort.describe(type));
            }

            @Override
            public Iterator<TypeDefinition> iterator() {
                return new TypeDefinition.SuperClassIterator(this);
            }

            @Override
            public <T> T accept(Visitor<T> visitor) {
                return visitor.onParameterizedType(this);
            }

            @Override
            public StackSize getStackSize() {
                return StackSize.SINGLE;
            }

            @CachedReturnPlugin.Enhance(value="hashCode")
            public int hashCode() {
                int n;
                int n2;
                int n3 = this.hashCode;
                if (n3 != 0) {
                    n2 = 0;
                } else {
                    OfParameterizedType ofParameterizedType = this;
                    int result = 1;
                    for (Generic typeArgument : ofParameterizedType.getTypeArguments()) {
                        result = 31 * result + typeArgument.hashCode();
                    }
                    Generic ownerType = ofParameterizedType.getOwnerType();
                    n2 = n = result ^ (ownerType == null ? ofParameterizedType.asErasure().hashCode() : ownerType.hashCode());
                }
                if (n == 0) {
                    n = this.hashCode;
                } else {
                    this.hashCode = n;
                }
                return n;
            }

            public boolean equals(@MaybeNull Object other) {
                if (this == other) {
                    return true;
                }
                if (!(other instanceof Generic)) {
                    return false;
                }
                Generic typeDescription = (Generic)other;
                if (!typeDescription.getSort().isParameterized()) {
                    return false;
                }
                Generic ownerType = this.getOwnerType();
                Generic otherOwnerType = typeDescription.getOwnerType();
                return !(!this.asErasure().equals(typeDescription.asErasure()) || ownerType == null && otherOwnerType != null || ownerType != null && !ownerType.equals(otherOwnerType) || !this.getTypeArguments().equals(typeDescription.getTypeArguments()));
            }

            public String toString() {
                StringBuilder stringBuilder = new StringBuilder();
                RenderingDelegate.CURRENT.apply(stringBuilder, this.asErasure(), this.getOwnerType());
                TypeList.Generic typeArguments = this.getTypeArguments();
                if (!typeArguments.isEmpty()) {
                    stringBuilder.append('<');
                    boolean multiple = false;
                    for (Generic typeArgument : typeArguments) {
                        if (multiple) {
                            stringBuilder.append(", ");
                        }
                        stringBuilder.append(typeArgument.getTypeName());
                        multiple = true;
                    }
                    stringBuilder.append('>');
                }
                return stringBuilder.toString();
            }

            public static class ForGenerifiedErasure
            extends OfParameterizedType {
                private final TypeDescription typeDescription;

                protected ForGenerifiedErasure(TypeDescription typeDescription) {
                    this.typeDescription = typeDescription;
                }

                public static Generic of(TypeDescription typeDescription) {
                    return typeDescription.isGenerified() ? new ForGenerifiedErasure(typeDescription) : new OfNonGenericType.ForErasure(typeDescription);
                }

                public TypeDescription asErasure() {
                    return this.typeDescription;
                }

                public TypeList.Generic getTypeArguments() {
                    return new TypeList.Generic.ForDetachedTypes(this.typeDescription.getTypeVariables(), Visitor.AnnotationStripper.INSTANCE);
                }

                @MaybeNull
                public Generic getOwnerType() {
                    TypeDescription declaringType = this.typeDescription.getDeclaringType();
                    return declaringType == null ? UNDEFINED : ForGenerifiedErasure.of(declaringType);
                }

                public AnnotationList getDeclaredAnnotations() {
                    return new AnnotationList.Empty();
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            public static class ForReifiedType
            extends OfParameterizedType {
                private final Generic parameterizedType;

                protected ForReifiedType(Generic parameterizedType) {
                    this.parameterizedType = parameterizedType;
                }

                @Override
                @MaybeNull
                public Generic getSuperClass() {
                    Generic superClass = super.getSuperClass();
                    return superClass == null ? UNDEFINED : new LazyProjection.WithResolvedErasure(superClass, Visitor.Reifying.INHERITING);
                }

                @Override
                public TypeList.Generic getInterfaces() {
                    return new TypeList.Generic.ForDetachedTypes.WithResolvedErasure(super.getInterfaces(), Visitor.Reifying.INHERITING);
                }

                @Override
                public FieldList<FieldDescription.InGenericShape> getDeclaredFields() {
                    return new FieldList.TypeSubstituting(this, super.getDeclaredFields(), Visitor.TypeErasing.INSTANCE);
                }

                @Override
                public MethodList<MethodDescription.InGenericShape> getDeclaredMethods() {
                    return new MethodList.TypeSubstituting(this, super.getDeclaredMethods(), Visitor.TypeErasing.INSTANCE);
                }

                @Override
                public TypeList.Generic getTypeArguments() {
                    return new TypeList.Generic.ForDetachedTypes(this.parameterizedType.getTypeArguments(), Visitor.TypeErasing.INSTANCE);
                }

                @Override
                @MaybeNull
                public Generic getOwnerType() {
                    Generic ownerType = this.parameterizedType.getOwnerType();
                    return ownerType == null ? UNDEFINED : ownerType.accept(Visitor.Reifying.INHERITING);
                }

                @Override
                public TypeDescription asErasure() {
                    return this.parameterizedType.asErasure();
                }

                @Override
                public AnnotationList getDeclaredAnnotations() {
                    return new AnnotationList.Empty();
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            public static class Latent
            extends OfParameterizedType {
                private final TypeDescription rawType;
                @MaybeNull
                private final Generic ownerType;
                private final List<? extends Generic> parameters;
                private final AnnotationSource annotationSource;

                public Latent(TypeDescription rawType, @MaybeNull Generic ownerType, List<? extends Generic> parameters, AnnotationSource annotationSource) {
                    this.rawType = rawType;
                    this.ownerType = ownerType;
                    this.parameters = parameters;
                    this.annotationSource = annotationSource;
                }

                @Override
                public TypeDescription asErasure() {
                    return this.rawType;
                }

                @Override
                @MaybeNull
                public Generic getOwnerType() {
                    return this.ownerType;
                }

                @Override
                public TypeList.Generic getTypeArguments() {
                    return new TypeList.Generic.Explicit(this.parameters);
                }

                @Override
                public AnnotationList getDeclaredAnnotations() {
                    return this.annotationSource.getDeclaredAnnotations();
                }
            }

            public static class ForLoadedType
            extends OfParameterizedType {
                private final ParameterizedType parameterizedType;
                private final AnnotationReader annotationReader;

                public ForLoadedType(ParameterizedType parameterizedType) {
                    this(parameterizedType, AnnotationReader.NoOp.INSTANCE);
                }

                protected ForLoadedType(ParameterizedType parameterizedType, AnnotationReader annotationReader) {
                    this.parameterizedType = parameterizedType;
                    this.annotationReader = annotationReader;
                }

                public TypeList.Generic getTypeArguments() {
                    return new ParameterArgumentTypeList(this.parameterizedType.getActualTypeArguments(), this.annotationReader);
                }

                @MaybeNull
                public Generic getOwnerType() {
                    java.lang.reflect.Type ownerType = this.parameterizedType.getOwnerType();
                    return ownerType == null ? UNDEFINED : TypeDefinition.Sort.describe(ownerType, this.annotationReader.ofOwnerType());
                }

                public TypeDescription asErasure() {
                    return net.bytebuddy.description.type.TypeDescription$ForLoadedType.of((Class)this.parameterizedType.getRawType());
                }

                public AnnotationList getDeclaredAnnotations() {
                    return this.annotationReader.asList();
                }

                public boolean represents(java.lang.reflect.Type type) {
                    return this.parameterizedType == type || super.represents(type);
                }

                protected static class ParameterArgumentTypeList
                extends TypeList.Generic.AbstractBase {
                    private final java.lang.reflect.Type[] argumentType;
                    private final AnnotationReader annotationReader;

                    protected ParameterArgumentTypeList(java.lang.reflect.Type[] argumentType, AnnotationReader annotationReader) {
                        this.argumentType = argumentType;
                        this.annotationReader = annotationReader;
                    }

                    public Generic get(int index) {
                        return TypeDefinition.Sort.describe(this.argumentType[index], this.annotationReader.ofTypeArgument(index));
                    }

                    public int size() {
                        return this.argumentType.length;
                    }
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            protected static enum RenderingDelegate {
                FOR_LEGACY_VM{

                    protected void apply(StringBuilder stringBuilder, TypeDescription erasure, @MaybeNull Generic ownerType) {
                        if (ownerType != null) {
                            stringBuilder.append(ownerType.getTypeName()).append('.').append(ownerType.getSort().isParameterized() ? erasure.getSimpleName() : erasure.getName());
                        } else {
                            stringBuilder.append(erasure.getName());
                        }
                    }
                }
                ,
                FOR_JAVA_8_CAPABLE_VM{

                    protected void apply(StringBuilder stringBuilder, TypeDescription erasure, @MaybeNull Generic ownerType) {
                        if (ownerType != null) {
                            stringBuilder.append(ownerType.getTypeName()).append('$');
                            if (ownerType.getSort().isParameterized()) {
                                stringBuilder.append(erasure.getName().replace(ownerType.asErasure().getName() + "$", ""));
                            } else {
                                stringBuilder.append(erasure.getSimpleName());
                            }
                        } else {
                            stringBuilder.append(erasure.getName());
                        }
                    }
                };

                protected static final RenderingDelegate CURRENT;

                protected abstract void apply(StringBuilder var1, TypeDescription var2, @MaybeNull Generic var3);

                static {
                    CURRENT = ClassFileVersion.ofThisVm(ClassFileVersion.JAVA_V5).isAtLeast(ClassFileVersion.JAVA_V8) ? FOR_JAVA_8_CAPABLE_VM : FOR_LEGACY_VM;
                }
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static abstract class OfWildcardType
        extends AbstractBase {
            public static final String SYMBOL = "?";
            private transient /* synthetic */ int hashCode;

            @Override
            public TypeDefinition.Sort getSort() {
                return TypeDefinition.Sort.WILDCARD;
            }

            @Override
            public TypeDescription asErasure() {
                throw new IllegalStateException("A wildcard does not represent an erasable type: " + this);
            }

            @Override
            @MaybeNull
            public Generic getSuperClass() {
                throw new IllegalStateException("A wildcard does not imply a super type definition: " + this);
            }

            @Override
            public TypeList.Generic getInterfaces() {
                throw new IllegalStateException("A wildcard does not imply an interface type definition: " + this);
            }

            @Override
            public FieldList<FieldDescription.InGenericShape> getDeclaredFields() {
                throw new IllegalStateException("A wildcard does not imply field definitions: " + this);
            }

            @Override
            public MethodList<MethodDescription.InGenericShape> getDeclaredMethods() {
                throw new IllegalStateException("A wildcard does not imply method definitions: " + this);
            }

            @Override
            public RecordComponentList<RecordComponentDescription.InGenericShape> getRecordComponents() {
                throw new IllegalStateException("A wildcard does not imply record component definitions: " + this);
            }

            @Override
            public Generic getComponentType() {
                throw new IllegalStateException("A wildcard does not imply a component type: " + this);
            }

            @Override
            public TypeVariableSource getTypeVariableSource() {
                throw new IllegalStateException("A wildcard does not imply a type variable source: " + this);
            }

            @Override
            public TypeList.Generic getTypeArguments() {
                throw new IllegalStateException("A wildcard does not imply type arguments: " + this);
            }

            @Override
            public Generic findBindingOf(Generic typeVariable) {
                throw new IllegalStateException("A wildcard does not imply type arguments: " + this);
            }

            @Override
            public Generic getOwnerType() {
                throw new IllegalStateException("A wildcard does not imply an owner type: " + this);
            }

            @Override
            public String getSymbol() {
                throw new IllegalStateException("A wildcard does not imply a symbol: " + this);
            }

            @Override
            public String getTypeName() {
                return this.toString();
            }

            @Override
            public String getActualName() {
                return this.toString();
            }

            @Override
            public boolean isPrimitive() {
                return false;
            }

            @Override
            public boolean isArray() {
                return false;
            }

            @Override
            public boolean isRecord() {
                return false;
            }

            @Override
            public boolean represents(java.lang.reflect.Type type) {
                return this.equals(TypeDefinition.Sort.describe(type));
            }

            @Override
            public Iterator<TypeDefinition> iterator() {
                throw new IllegalStateException("A wildcard does not imply a super type definition: " + this);
            }

            @Override
            public <T> T accept(Visitor<T> visitor) {
                return visitor.onWildcard(this);
            }

            @Override
            public StackSize getStackSize() {
                throw new IllegalStateException("A wildcard does not imply an operand stack size: " + this);
            }

            @CachedReturnPlugin.Enhance(value="hashCode")
            public int hashCode() {
                int n;
                int n2;
                int n3 = this.hashCode;
                if (n3 != 0) {
                    n2 = 0;
                } else {
                    OfWildcardType ofWildcardType = this;
                    int lowerHash = 1;
                    int upperHash = 1;
                    for (Generic lowerBound : ofWildcardType.getLowerBounds()) {
                        lowerHash = 31 * lowerHash + lowerBound.hashCode();
                    }
                    for (Generic upperBound : ofWildcardType.getUpperBounds()) {
                        upperHash = 31 * upperHash + upperBound.hashCode();
                    }
                    n2 = n = lowerHash ^ upperHash;
                }
                if (n == 0) {
                    n = this.hashCode;
                } else {
                    this.hashCode = n;
                }
                return n;
            }

            public boolean equals(@MaybeNull Object other) {
                if (this == other) {
                    return true;
                }
                if (!(other instanceof Generic)) {
                    return false;
                }
                Generic typeDescription = (Generic)other;
                return typeDescription.getSort().isWildcard() && this.getUpperBounds().equals(typeDescription.getUpperBounds()) && this.getLowerBounds().equals(typeDescription.getLowerBounds());
            }

            public String toString() {
                StringBuilder stringBuilder = new StringBuilder(SYMBOL);
                TypeList.Generic bounds = this.getLowerBounds();
                if (!bounds.isEmpty()) {
                    stringBuilder.append(" super ");
                } else {
                    bounds = this.getUpperBounds();
                    if (((Generic)bounds.getOnly()).equals(OfNonGenericType.ForLoadedType.of(Object.class))) {
                        return SYMBOL;
                    }
                    stringBuilder.append(" extends ");
                }
                return stringBuilder.append(((Generic)bounds.getOnly()).getTypeName()).toString();
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            public static class Latent
            extends OfWildcardType {
                private final List<? extends Generic> upperBounds;
                private final List<? extends Generic> lowerBounds;
                private final AnnotationSource annotationSource;

                protected Latent(List<? extends Generic> upperBounds, List<? extends Generic> lowerBounds, AnnotationSource annotationSource) {
                    this.upperBounds = upperBounds;
                    this.lowerBounds = lowerBounds;
                    this.annotationSource = annotationSource;
                }

                public static Generic unbounded(AnnotationSource annotationSource) {
                    return new Latent(Collections.singletonList(OfNonGenericType.ForLoadedType.of(Object.class)), Collections.emptyList(), annotationSource);
                }

                public static Generic boundedAbove(Generic upperBound, AnnotationSource annotationSource) {
                    return new Latent(Collections.singletonList(upperBound), Collections.emptyList(), annotationSource);
                }

                public static Generic boundedBelow(Generic lowerBound, AnnotationSource annotationSource) {
                    return new Latent(Collections.singletonList(OfNonGenericType.ForLoadedType.of(Object.class)), Collections.singletonList(lowerBound), annotationSource);
                }

                @Override
                public TypeList.Generic getUpperBounds() {
                    return new TypeList.Generic.Explicit(this.upperBounds);
                }

                @Override
                public TypeList.Generic getLowerBounds() {
                    return new TypeList.Generic.Explicit(this.lowerBounds);
                }

                @Override
                public AnnotationList getDeclaredAnnotations() {
                    return this.annotationSource.getDeclaredAnnotations();
                }
            }

            public static class ForLoadedType
            extends OfWildcardType {
                private final WildcardType wildcardType;
                private final AnnotationReader annotationReader;

                public ForLoadedType(WildcardType wildcardType) {
                    this(wildcardType, AnnotationReader.NoOp.INSTANCE);
                }

                protected ForLoadedType(WildcardType wildcardType, AnnotationReader annotationReader) {
                    this.wildcardType = wildcardType;
                    this.annotationReader = annotationReader;
                }

                public TypeList.Generic getUpperBounds() {
                    return new WildcardUpperBoundTypeList(this.wildcardType.getUpperBounds(), this.annotationReader);
                }

                public TypeList.Generic getLowerBounds() {
                    return new WildcardLowerBoundTypeList(this.wildcardType.getLowerBounds(), this.annotationReader);
                }

                public AnnotationList getDeclaredAnnotations() {
                    return this.annotationReader.asList();
                }

                public boolean represents(java.lang.reflect.Type type) {
                    return this.wildcardType == type || super.represents(type);
                }

                protected static class WildcardLowerBoundTypeList
                extends TypeList.Generic.AbstractBase {
                    private final java.lang.reflect.Type[] lowerBound;
                    private final AnnotationReader annotationReader;

                    protected WildcardLowerBoundTypeList(java.lang.reflect.Type[] lowerBound, AnnotationReader annotationReader) {
                        this.lowerBound = lowerBound;
                        this.annotationReader = annotationReader;
                    }

                    public Generic get(int index) {
                        return TypeDefinition.Sort.describe(this.lowerBound[index], this.annotationReader.ofWildcardLowerBoundType(index));
                    }

                    public int size() {
                        return this.lowerBound.length;
                    }
                }

                protected static class WildcardUpperBoundTypeList
                extends TypeList.Generic.AbstractBase {
                    private final java.lang.reflect.Type[] upperBound;
                    private final AnnotationReader annotationReader;

                    protected WildcardUpperBoundTypeList(java.lang.reflect.Type[] upperBound, AnnotationReader annotationReader) {
                        this.upperBound = upperBound;
                        this.annotationReader = annotationReader;
                    }

                    public Generic get(int index) {
                        return TypeDefinition.Sort.describe(this.upperBound[index], this.annotationReader.ofWildcardUpperBoundType(index));
                    }

                    public int size() {
                        return this.upperBound.length;
                    }
                }
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static abstract class OfGenericArray
        extends AbstractBase {
            private transient /* synthetic */ int hashCode;

            @Override
            @SuppressFBWarnings(value={"NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE"}, justification="Assuming component type for array type.")
            public TypeDefinition.Sort getSort() {
                return this.getComponentType().getSort().isNonGeneric() ? TypeDefinition.Sort.NON_GENERIC : TypeDefinition.Sort.GENERIC_ARRAY;
            }

            @Override
            @SuppressFBWarnings(value={"NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE"}, justification="Assuming component type for array type.")
            public TypeDescription asErasure() {
                return ArrayProjection.of(this.getComponentType().asErasure(), 1);
            }

            @Override
            @MaybeNull
            public Generic getSuperClass() {
                return OfNonGenericType.ForLoadedType.of(Object.class);
            }

            @Override
            public TypeList.Generic getInterfaces() {
                return ARRAY_INTERFACES;
            }

            @Override
            public FieldList<FieldDescription.InGenericShape> getDeclaredFields() {
                return new FieldList.Empty<FieldDescription.InGenericShape>();
            }

            @Override
            public MethodList<MethodDescription.InGenericShape> getDeclaredMethods() {
                return new MethodList.Empty<MethodDescription.InGenericShape>();
            }

            @Override
            public RecordComponentList<RecordComponentDescription.InGenericShape> getRecordComponents() {
                return new RecordComponentList.Empty<RecordComponentDescription.InGenericShape>();
            }

            @Override
            public TypeList.Generic getUpperBounds() {
                throw new IllegalStateException("A generic array type does not imply upper type bounds: " + this);
            }

            @Override
            public TypeList.Generic getLowerBounds() {
                throw new IllegalStateException("A generic array type does not imply lower type bounds: " + this);
            }

            @Override
            public TypeVariableSource getTypeVariableSource() {
                throw new IllegalStateException("A generic array type does not imply a type variable source: " + this);
            }

            @Override
            public TypeList.Generic getTypeArguments() {
                throw new IllegalStateException("A generic array type does not imply type arguments: " + this);
            }

            @Override
            public Generic findBindingOf(Generic typeVariable) {
                throw new IllegalStateException("A generic array type does not imply type arguments: " + this);
            }

            @Override
            @MaybeNull
            public Generic getOwnerType() {
                return UNDEFINED;
            }

            @Override
            public String getSymbol() {
                throw new IllegalStateException("A generic array type does not imply a symbol: " + this);
            }

            @Override
            public String getTypeName() {
                return this.getSort().isNonGeneric() ? this.asErasure().getTypeName() : this.toString();
            }

            @Override
            public String getActualName() {
                return this.getSort().isNonGeneric() ? this.asErasure().getActualName() : this.toString();
            }

            @Override
            public boolean isArray() {
                return true;
            }

            @Override
            public boolean isPrimitive() {
                return false;
            }

            @Override
            public boolean isRecord() {
                return false;
            }

            @Override
            public Iterator<TypeDefinition> iterator() {
                return new TypeDefinition.SuperClassIterator(this);
            }

            @Override
            public <T> T accept(Visitor<T> visitor) {
                return this.getSort().isNonGeneric() ? visitor.onNonGenericType(this) : visitor.onGenericArray(this);
            }

            @Override
            public StackSize getStackSize() {
                return StackSize.SINGLE;
            }

            @CachedReturnPlugin.Enhance(value="hashCode")
            @SuppressFBWarnings(value={"NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE"}, justification="Assuming component type for array type.")
            public int hashCode() {
                int n;
                int n2;
                int n3 = this.hashCode;
                if (n3 != 0) {
                    n2 = 0;
                } else {
                    OfGenericArray ofGenericArray = this;
                    n2 = n = ofGenericArray.getSort().isNonGeneric() ? ofGenericArray.asErasure().hashCode() : ofGenericArray.getComponentType().hashCode();
                }
                if (n == 0) {
                    n = this.hashCode;
                } else {
                    this.hashCode = n;
                }
                return n;
            }

            @SuppressFBWarnings(value={"EQ_CHECK_FOR_OPERAND_NOT_COMPATIBLE_WITH_THIS", "NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE"}, justification="Type check is performed by erasure implementation. Assuming component type for array type.")
            public boolean equals(@MaybeNull Object other) {
                if (this == other) {
                    return true;
                }
                if (this.getSort().isNonGeneric()) {
                    return this.asErasure().equals(other);
                }
                if (!(other instanceof Generic)) {
                    return false;
                }
                Generic typeDescription = (Generic)other;
                return typeDescription.getSort().isGenericArray() && this.getComponentType().equals(typeDescription.getComponentType());
            }

            @SuppressFBWarnings(value={"NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE"}, justification="Assuming component type for array type.")
            public String toString() {
                return this.getSort().isNonGeneric() ? this.asErasure().toString() : this.getComponentType().getTypeName() + "[]";
            }

            public static class Latent
            extends OfGenericArray {
                private final Generic componentType;
                private final AnnotationSource annotationSource;

                public Latent(Generic componentType, AnnotationSource annotationSource) {
                    this.componentType = componentType;
                    this.annotationSource = annotationSource;
                }

                public Generic getComponentType() {
                    return this.componentType;
                }

                public AnnotationList getDeclaredAnnotations() {
                    return this.annotationSource.getDeclaredAnnotations();
                }
            }

            public static class ForLoadedType
            extends OfGenericArray {
                private final GenericArrayType genericArrayType;
                private final AnnotationReader annotationReader;

                public ForLoadedType(GenericArrayType genericArrayType) {
                    this(genericArrayType, AnnotationReader.NoOp.INSTANCE);
                }

                protected ForLoadedType(GenericArrayType genericArrayType, AnnotationReader annotationReader) {
                    this.genericArrayType = genericArrayType;
                    this.annotationReader = annotationReader;
                }

                @MaybeNull
                public Generic getComponentType() {
                    return TypeDefinition.Sort.describe(this.genericArrayType.getGenericComponentType(), this.annotationReader.ofComponentType());
                }

                public AnnotationList getDeclaredAnnotations() {
                    return this.annotationReader.asList();
                }

                public boolean represents(java.lang.reflect.Type type) {
                    return this.genericArrayType == type || super.represents(type);
                }
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static abstract class OfNonGenericType
        extends AbstractBase {
            private transient /* synthetic */ int hashCode;

            @Override
            public TypeDefinition.Sort getSort() {
                return TypeDefinition.Sort.NON_GENERIC;
            }

            @Override
            @MaybeNull
            public Generic getSuperClass() {
                TypeDescription erasure = this.asErasure();
                Generic superClass = erasure.getSuperClass();
                if (net.bytebuddy.description.type.TypeDescription$AbstractBase.RAW_TYPES) {
                    return superClass;
                }
                return superClass == null ? UNDEFINED : new LazyProjection.WithResolvedErasure(superClass, new Visitor.ForRawType(erasure), AnnotationSource.Empty.INSTANCE);
            }

            @Override
            public TypeList.Generic getInterfaces() {
                TypeDescription erasure = this.asErasure();
                if (net.bytebuddy.description.type.TypeDescription$AbstractBase.RAW_TYPES) {
                    return erasure.getInterfaces();
                }
                return new TypeList.Generic.ForDetachedTypes.WithResolvedErasure(erasure.getInterfaces(), new Visitor.ForRawType(erasure));
            }

            @Override
            public FieldList<FieldDescription.InGenericShape> getDeclaredFields() {
                TypeDescription erasure = this.asErasure();
                return new FieldList.TypeSubstituting(this, erasure.getDeclaredFields(), (Visitor<? extends Generic>)(net.bytebuddy.description.type.TypeDescription$AbstractBase.RAW_TYPES ? Visitor.NoOp.INSTANCE : new Visitor.ForRawType(erasure)));
            }

            @Override
            public MethodList<MethodDescription.InGenericShape> getDeclaredMethods() {
                TypeDescription erasure = this.asErasure();
                return new MethodList.TypeSubstituting(this, erasure.getDeclaredMethods(), (Visitor<? extends Generic>)(net.bytebuddy.description.type.TypeDescription$AbstractBase.RAW_TYPES ? Visitor.NoOp.INSTANCE : new Visitor.ForRawType(erasure)));
            }

            @Override
            public RecordComponentList<RecordComponentDescription.InGenericShape> getRecordComponents() {
                TypeDescription erasure = this.asErasure();
                return new RecordComponentList.TypeSubstituting(this, erasure.getRecordComponents(), (Visitor<? extends Generic>)(net.bytebuddy.description.type.TypeDescription$AbstractBase.RAW_TYPES ? Visitor.NoOp.INSTANCE : new Visitor.ForRawType(erasure)));
            }

            @Override
            public TypeList.Generic getTypeArguments() {
                throw new IllegalStateException("A non-generic type does not imply type arguments: " + this);
            }

            @Override
            public Generic findBindingOf(Generic typeVariable) {
                throw new IllegalStateException("A non-generic type does not imply type arguments: " + this);
            }

            @Override
            public <T> T accept(Visitor<T> visitor) {
                return visitor.onNonGenericType(this);
            }

            @Override
            public String getTypeName() {
                return this.asErasure().getTypeName();
            }

            @Override
            public TypeList.Generic getUpperBounds() {
                throw new IllegalStateException("A non-generic type does not imply upper type bounds: " + this);
            }

            @Override
            public TypeList.Generic getLowerBounds() {
                throw new IllegalStateException("A non-generic type does not imply lower type bounds: " + this);
            }

            @Override
            public TypeVariableSource getTypeVariableSource() {
                throw new IllegalStateException("A non-generic type does not imply a type variable source: " + this);
            }

            @Override
            public String getSymbol() {
                throw new IllegalStateException("A non-generic type does not imply a symbol: " + this);
            }

            @Override
            public StackSize getStackSize() {
                return this.asErasure().getStackSize();
            }

            @Override
            public String getActualName() {
                return this.asErasure().getActualName();
            }

            @Override
            public boolean isArray() {
                return this.asErasure().isArray();
            }

            @Override
            public boolean isPrimitive() {
                return this.asErasure().isPrimitive();
            }

            @Override
            public boolean isRecord() {
                return this.asErasure().isRecord();
            }

            @Override
            public boolean represents(java.lang.reflect.Type type) {
                return this.asErasure().represents(type);
            }

            @Override
            public Iterator<TypeDefinition> iterator() {
                return new TypeDefinition.SuperClassIterator(this);
            }

            @CachedReturnPlugin.Enhance(value="hashCode")
            public int hashCode() {
                int n;
                int n2;
                int n3 = this.hashCode;
                if (n3 != 0) {
                    n2 = 0;
                } else {
                    OfNonGenericType ofNonGenericType = this;
                    n2 = n = ofNonGenericType.asErasure().hashCode();
                }
                if (n == 0) {
                    n = this.hashCode;
                } else {
                    this.hashCode = n;
                }
                return n;
            }

            @SuppressFBWarnings(value={"EQ_CHECK_FOR_OPERAND_NOT_COMPATIBLE_WITH_THIS"}, justification="Type check is performed by erasure implementation.")
            public boolean equals(@MaybeNull Object other) {
                return this == other || this.asErasure().equals(other);
            }

            public String toString() {
                return this.asErasure().toString();
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            public static class ForReifiedErasure
            extends OfNonGenericType {
                private final TypeDescription typeDescription;

                protected ForReifiedErasure(TypeDescription typeDescription) {
                    this.typeDescription = typeDescription;
                }

                protected static Generic of(TypeDescription typeDescription) {
                    return typeDescription.isGenerified() ? new ForReifiedErasure(typeDescription) : new ForErasure(typeDescription);
                }

                @Override
                @MaybeNull
                public Generic getSuperClass() {
                    Generic superClass = this.typeDescription.getSuperClass();
                    return superClass == null ? UNDEFINED : new LazyProjection.WithResolvedErasure(superClass, Visitor.Reifying.INHERITING);
                }

                @Override
                public TypeList.Generic getInterfaces() {
                    return new TypeList.Generic.ForDetachedTypes.WithResolvedErasure(this.typeDescription.getInterfaces(), Visitor.Reifying.INHERITING);
                }

                @Override
                public FieldList<FieldDescription.InGenericShape> getDeclaredFields() {
                    return new FieldList.TypeSubstituting(this, this.typeDescription.getDeclaredFields(), Visitor.TypeErasing.INSTANCE);
                }

                @Override
                public MethodList<MethodDescription.InGenericShape> getDeclaredMethods() {
                    return new MethodList.TypeSubstituting(this, this.typeDescription.getDeclaredMethods(), Visitor.TypeErasing.INSTANCE);
                }

                @Override
                public TypeDescription asErasure() {
                    return this.typeDescription;
                }

                @Override
                @MaybeNull
                public Generic getOwnerType() {
                    TypeDescription declaringType = this.typeDescription.getDeclaringType();
                    return declaringType == null ? UNDEFINED : ForReifiedErasure.of(declaringType);
                }

                @Override
                @MaybeNull
                public Generic getComponentType() {
                    TypeDescription componentType = this.typeDescription.getComponentType();
                    return componentType == null ? UNDEFINED : ForReifiedErasure.of(componentType);
                }

                @Override
                public AnnotationList getDeclaredAnnotations() {
                    return new AnnotationList.Empty();
                }
            }

            public static class Latent
            extends OfNonGenericType {
                private final TypeDescription typeDescription;
                @MaybeNull
                private final Generic declaringType;
                private final AnnotationSource annotationSource;

                public Latent(TypeDescription typeDescription, AnnotationSource annotationSource) {
                    this(typeDescription, typeDescription.getDeclaringType(), annotationSource);
                }

                private Latent(TypeDescription typeDescription, @MaybeNull TypeDescription declaringType, AnnotationSource annotationSource) {
                    this(typeDescription, declaringType == null ? UNDEFINED : declaringType.asGenericType(), annotationSource);
                }

                protected Latent(TypeDescription typeDescription, @MaybeNull Generic declaringType, AnnotationSource annotationSource) {
                    this.typeDescription = typeDescription;
                    this.declaringType = declaringType;
                    this.annotationSource = annotationSource;
                }

                @MaybeNull
                public Generic getOwnerType() {
                    return this.declaringType;
                }

                @MaybeNull
                public Generic getComponentType() {
                    TypeDescription componentType = this.typeDescription.getComponentType();
                    return componentType == null ? UNDEFINED : componentType.asGenericType();
                }

                public AnnotationList getDeclaredAnnotations() {
                    return this.annotationSource.getDeclaredAnnotations();
                }

                public TypeDescription asErasure() {
                    return this.typeDescription;
                }
            }

            public static class ForErasure
            extends OfNonGenericType {
                private final TypeDescription typeDescription;

                public ForErasure(TypeDescription typeDescription) {
                    this.typeDescription = typeDescription;
                }

                public TypeDescription asErasure() {
                    return this.typeDescription;
                }

                @MaybeNull
                public Generic getOwnerType() {
                    TypeDescription declaringType = this.typeDescription.getDeclaringType();
                    return declaringType == null ? UNDEFINED : declaringType.asGenericType();
                }

                @MaybeNull
                public Generic getComponentType() {
                    TypeDescription componentType = this.typeDescription.getComponentType();
                    return componentType == null ? UNDEFINED : componentType.asGenericType();
                }

                public AnnotationList getDeclaredAnnotations() {
                    return new AnnotationList.Empty();
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            public static class ForLoadedType
            extends OfNonGenericType {
                private static final Map<Class<?>, Generic> TYPE_CACHE = new HashMap();
                private final Class<?> type;
                private final AnnotationReader annotationReader;

                public ForLoadedType(Class<?> type) {
                    this(type, AnnotationReader.NoOp.INSTANCE);
                }

                protected ForLoadedType(Class<?> type, AnnotationReader annotationReader) {
                    this.type = type;
                    this.annotationReader = annotationReader;
                }

                public static Generic of(Class<?> type) {
                    Generic typeDescription = TYPE_CACHE.get(type);
                    return typeDescription == null ? new ForLoadedType(type) : typeDescription;
                }

                @Override
                public TypeDescription asErasure() {
                    return net.bytebuddy.description.type.TypeDescription$ForLoadedType.of(this.type);
                }

                @Override
                @MaybeNull
                public Generic getOwnerType() {
                    Class<?> declaringClass = this.type.getDeclaringClass();
                    return declaringClass == null ? UNDEFINED : new ForLoadedType(declaringClass, this.annotationReader.ofOuterClass());
                }

                @Override
                @MaybeNull
                public Generic getComponentType() {
                    Class<?> componentType = this.type.getComponentType();
                    return componentType == null ? UNDEFINED : new ForLoadedType(componentType, this.annotationReader.ofComponentType());
                }

                @Override
                public AnnotationList getDeclaredAnnotations() {
                    return this.annotationReader.asList();
                }

                @Override
                public boolean represents(java.lang.reflect.Type type) {
                    return this.type == type || super.represents(type);
                }

                static {
                    TYPE_CACHE.put(TargetType.class, new ForLoadedType(TargetType.class));
                    TYPE_CACHE.put(Class.class, new ForLoadedType(Class.class));
                    TYPE_CACHE.put(Throwable.class, new ForLoadedType(Throwable.class));
                    TYPE_CACHE.put(Annotation.class, new ForLoadedType(Annotation.class));
                    TYPE_CACHE.put(Object.class, new ForLoadedType(Object.class));
                    TYPE_CACHE.put(String.class, new ForLoadedType(String.class));
                    TYPE_CACHE.put(Boolean.class, new ForLoadedType(Boolean.class));
                    TYPE_CACHE.put(Byte.class, new ForLoadedType(Byte.class));
                    TYPE_CACHE.put(Short.class, new ForLoadedType(Short.class));
                    TYPE_CACHE.put(Character.class, new ForLoadedType(Character.class));
                    TYPE_CACHE.put(Integer.class, new ForLoadedType(Integer.class));
                    TYPE_CACHE.put(Long.class, new ForLoadedType(Long.class));
                    TYPE_CACHE.put(Float.class, new ForLoadedType(Float.class));
                    TYPE_CACHE.put(Double.class, new ForLoadedType(Double.class));
                    TYPE_CACHE.put(Void.TYPE, new ForLoadedType(Void.TYPE));
                    TYPE_CACHE.put(Boolean.TYPE, new ForLoadedType(Boolean.TYPE));
                    TYPE_CACHE.put(Byte.TYPE, new ForLoadedType(Byte.TYPE));
                    TYPE_CACHE.put(Short.TYPE, new ForLoadedType(Short.TYPE));
                    TYPE_CACHE.put(Character.TYPE, new ForLoadedType(Character.TYPE));
                    TYPE_CACHE.put(Integer.TYPE, new ForLoadedType(Integer.TYPE));
                    TYPE_CACHE.put(Long.TYPE, new ForLoadedType(Long.TYPE));
                    TYPE_CACHE.put(Float.TYPE, new ForLoadedType(Float.TYPE));
                    TYPE_CACHE.put(Double.TYPE, new ForLoadedType(Double.TYPE));
                }
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        @HashCodeAndEqualsPlugin.Enhance
        public static class LazyProxy
        implements InvocationHandler {
            private final Class<?> type;

            protected LazyProxy(Class<?> type) {
                this.type = type;
            }

            protected static Generic of(Class<?> type) {
                return (Generic)Proxy.newProxyInstance(Generic.class.getClassLoader(), new Class[]{Generic.class}, (InvocationHandler)new LazyProxy(type));
            }

            @Override
            public Object invoke(Object proxy, Method method, @MaybeNull Object[] argument) throws Throwable {
                try {
                    return method.invoke((Object)OfNonGenericType.ForLoadedType.of(this.type), argument);
                }
                catch (InvocationTargetException exception) {
                    throw exception.getTargetException();
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
                return this.type.equals(((LazyProxy)object).type);
            }

            public int hashCode() {
                return this.getClass().hashCode() * 31 + this.type.hashCode();
            }
        }

        public static abstract class AbstractBase
        extends ModifierReviewable.AbstractBase
        implements Generic {
            public int getModifiers() {
                return this.asErasure().getModifiers();
            }

            public Generic asGenericType() {
                return this;
            }

            public Generic asRawType() {
                return this.asErasure().asGenericType();
            }

            public boolean represents(java.lang.reflect.Type type) {
                return this.equals(TypeDefinition.Sort.describe(type));
            }
        }

        public static interface AnnotationReader {
            public AnnotatedElement resolve();

            public AnnotationList asList();

            public AnnotationReader ofWildcardUpperBoundType(int var1);

            public AnnotationReader ofWildcardLowerBoundType(int var1);

            public AnnotationReader ofTypeVariableBoundType(int var1);

            public AnnotationReader ofTypeArgument(int var1);

            public AnnotationReader ofOwnerType();

            public AnnotationReader ofOuterClass();

            public AnnotationReader ofComponentType();

            public static class ForOwnerType
            extends Delegator.Chained {
                private static final AnnotatedType ANNOTATED_TYPE = ForOwnerType.doPrivileged(JavaDispatcher.of(AnnotatedType.class));

                protected ForOwnerType(AnnotationReader annotationReader) {
                    super(annotationReader);
                }

                protected AnnotatedElement resolve(AnnotatedElement annotatedElement) {
                    try {
                        AnnotatedElement annotatedOwnerType = ANNOTATED_TYPE.getAnnotatedOwnerType(annotatedElement);
                        return annotatedOwnerType == null ? NoOp.INSTANCE : annotatedOwnerType;
                    }
                    catch (ClassCastException ignored) {
                        return NoOp.INSTANCE;
                    }
                }

                @JavaDispatcher.Proxied(value="java.lang.reflect.AnnotatedType")
                protected static interface AnnotatedType {
                    @MaybeNull
                    @JavaDispatcher.Defaults
                    @JavaDispatcher.Proxied(value="getAnnotatedOwnerType")
                    public AnnotatedElement getAnnotatedOwnerType(AnnotatedElement var1);
                }
            }

            public static class ForComponentType
            extends Delegator.Chained {
                private static final AnnotatedParameterizedType ANNOTATED_PARAMETERIZED_TYPE = ForComponentType.doPrivileged(JavaDispatcher.of(AnnotatedParameterizedType.class));

                protected ForComponentType(AnnotationReader annotationReader) {
                    super(annotationReader);
                }

                protected AnnotatedElement resolve(AnnotatedElement annotatedElement) {
                    if (!ANNOTATED_PARAMETERIZED_TYPE.isInstance(annotatedElement)) {
                        return NoOp.INSTANCE;
                    }
                    try {
                        return ANNOTATED_PARAMETERIZED_TYPE.getAnnotatedGenericComponentType(annotatedElement);
                    }
                    catch (ClassCastException ignored) {
                        return NoOp.INSTANCE;
                    }
                }

                @JavaDispatcher.Proxied(value="java.lang.reflect.AnnotatedArrayType")
                protected static interface AnnotatedParameterizedType {
                    @JavaDispatcher.Instance
                    @JavaDispatcher.Proxied(value="isInstance")
                    public boolean isInstance(AnnotatedElement var1);

                    @JavaDispatcher.Proxied(value="getAnnotatedGenericComponentType")
                    public AnnotatedElement getAnnotatedGenericComponentType(AnnotatedElement var1);
                }
            }

            @HashCodeAndEqualsPlugin.Enhance
            public static class ForTypeArgument
            extends Delegator.Chained {
                private static final AnnotatedParameterizedType ANNOTATED_PARAMETERIZED_TYPE = ForTypeArgument.doPrivileged(JavaDispatcher.of(AnnotatedParameterizedType.class));
                private final int index;

                protected ForTypeArgument(AnnotationReader annotationReader, int index) {
                    super(annotationReader);
                    this.index = index;
                }

                protected AnnotatedElement resolve(AnnotatedElement annotatedElement) {
                    if (!ANNOTATED_PARAMETERIZED_TYPE.isInstance(annotatedElement)) {
                        return NoOp.INSTANCE;
                    }
                    try {
                        return ANNOTATED_PARAMETERIZED_TYPE.getAnnotatedActualTypeArguments(annotatedElement)[this.index];
                    }
                    catch (ClassCastException ignored) {
                        return NoOp.INSTANCE;
                    }
                }

                public boolean equals(@MaybeNull Object object) {
                    if (!super.equals(object)) {
                        return false;
                    }
                    if (this == object) {
                        return true;
                    }
                    if (object == null) {
                        return false;
                    }
                    if (this.getClass() != object.getClass()) {
                        return false;
                    }
                    return this.index == ((ForTypeArgument)object).index;
                }

                public int hashCode() {
                    return super.hashCode() * 31 + this.index;
                }

                @JavaDispatcher.Proxied(value="java.lang.reflect.AnnotatedParameterizedType")
                protected static interface AnnotatedParameterizedType {
                    @JavaDispatcher.Instance
                    @JavaDispatcher.Proxied(value="isInstance")
                    public boolean isInstance(AnnotatedElement var1);

                    @JavaDispatcher.Proxied(value="getAnnotatedActualTypeArguments")
                    public AnnotatedElement[] getAnnotatedActualTypeArguments(AnnotatedElement var1);
                }
            }

            @HashCodeAndEqualsPlugin.Enhance
            public static class ForTypeVariableBoundType
            extends Delegator.Chained {
                private static final AnnotatedTypeVariable ANNOTATED_TYPE_VARIABLE = ForTypeVariableBoundType.doPrivileged(JavaDispatcher.of(AnnotatedTypeVariable.class));
                private final int index;

                protected ForTypeVariableBoundType(AnnotationReader annotationReader, int index) {
                    super(annotationReader);
                    this.index = index;
                }

                protected AnnotatedElement resolve(AnnotatedElement annotatedElement) {
                    if (!ANNOTATED_TYPE_VARIABLE.isInstance(annotatedElement)) {
                        return NoOp.INSTANCE;
                    }
                    try {
                        return ANNOTATED_TYPE_VARIABLE.getAnnotatedBounds(annotatedElement)[this.index];
                    }
                    catch (ClassCastException ignored) {
                        return NoOp.INSTANCE;
                    }
                }

                public boolean equals(@MaybeNull Object object) {
                    if (!super.equals(object)) {
                        return false;
                    }
                    if (this == object) {
                        return true;
                    }
                    if (object == null) {
                        return false;
                    }
                    if (this.getClass() != object.getClass()) {
                        return false;
                    }
                    return this.index == ((ForTypeVariableBoundType)object).index;
                }

                public int hashCode() {
                    return super.hashCode() * 31 + this.index;
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                @HashCodeAndEqualsPlugin.Enhance
                protected static class OfFormalTypeVariable
                extends Delegator {
                    private static final FormalTypeVariable TYPE_VARIABLE = OfFormalTypeVariable.doPrivileged(JavaDispatcher.of(FormalTypeVariable.class));
                    private final TypeVariable<?> typeVariable;
                    private final int index;

                    protected OfFormalTypeVariable(TypeVariable<?> typeVariable, int index) {
                        this.typeVariable = typeVariable;
                        this.index = index;
                    }

                    @Override
                    public AnnotatedElement resolve() {
                        try {
                            AnnotatedElement[] annotatedBound = TYPE_VARIABLE.getAnnotatedBounds(this.typeVariable);
                            return annotatedBound.length == 0 ? NoOp.INSTANCE : annotatedBound[this.index];
                        }
                        catch (ClassCastException ignored) {
                            return NoOp.INSTANCE;
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
                        if (this.index != ((OfFormalTypeVariable)object).index) {
                            return false;
                        }
                        return this.typeVariable.equals(((OfFormalTypeVariable)object).typeVariable);
                    }

                    public int hashCode() {
                        return (this.getClass().hashCode() * 31 + this.typeVariable.hashCode()) * 31 + this.index;
                    }

                    @JavaDispatcher.Proxied(value="java.lang.reflect.TypeVariable")
                    protected static interface FormalTypeVariable {
                        @JavaDispatcher.Defaults
                        @JavaDispatcher.Proxied(value="getAnnotatedBounds")
                        public AnnotatedElement[] getAnnotatedBounds(Object var1);
                    }
                }

                @JavaDispatcher.Proxied(value="java.lang.reflect.AnnotatedTypeVariable")
                protected static interface AnnotatedTypeVariable {
                    @JavaDispatcher.Instance
                    @JavaDispatcher.Proxied(value="isInstance")
                    public boolean isInstance(AnnotatedElement var1);

                    @JavaDispatcher.Proxied(value="getAnnotatedBounds")
                    public AnnotatedElement[] getAnnotatedBounds(AnnotatedElement var1);
                }
            }

            @HashCodeAndEqualsPlugin.Enhance
            public static class ForWildcardLowerBoundType
            extends Delegator.Chained {
                private static final AnnotatedWildcardType ANNOTATED_WILDCARD_TYPE = ForWildcardLowerBoundType.doPrivileged(JavaDispatcher.of(AnnotatedWildcardType.class));
                private final int index;

                protected ForWildcardLowerBoundType(AnnotationReader annotationReader, int index) {
                    super(annotationReader);
                    this.index = index;
                }

                protected AnnotatedElement resolve(AnnotatedElement annotatedElement) {
                    if (!ANNOTATED_WILDCARD_TYPE.isInstance(annotatedElement)) {
                        return NoOp.INSTANCE;
                    }
                    try {
                        return ANNOTATED_WILDCARD_TYPE.getAnnotatedLowerBounds(annotatedElement)[this.index];
                    }
                    catch (ClassCastException ignored) {
                        return NoOp.INSTANCE;
                    }
                }

                public boolean equals(@MaybeNull Object object) {
                    if (!super.equals(object)) {
                        return false;
                    }
                    if (this == object) {
                        return true;
                    }
                    if (object == null) {
                        return false;
                    }
                    if (this.getClass() != object.getClass()) {
                        return false;
                    }
                    return this.index == ((ForWildcardLowerBoundType)object).index;
                }

                public int hashCode() {
                    return super.hashCode() * 31 + this.index;
                }

                @JavaDispatcher.Proxied(value="java.lang.reflect.AnnotatedWildcardType")
                protected static interface AnnotatedWildcardType {
                    @JavaDispatcher.Instance
                    @JavaDispatcher.Proxied(value="isInstance")
                    public boolean isInstance(AnnotatedElement var1);

                    @JavaDispatcher.Proxied(value="getAnnotatedLowerBounds")
                    public AnnotatedElement[] getAnnotatedLowerBounds(AnnotatedElement var1);
                }
            }

            @HashCodeAndEqualsPlugin.Enhance
            public static class ForWildcardUpperBoundType
            extends Delegator.Chained {
                private static final AnnotatedWildcardType ANNOTATED_WILDCARD_TYPE = ForWildcardUpperBoundType.doPrivileged(JavaDispatcher.of(AnnotatedWildcardType.class));
                private final int index;

                protected ForWildcardUpperBoundType(AnnotationReader annotationReader, int index) {
                    super(annotationReader);
                    this.index = index;
                }

                protected AnnotatedElement resolve(AnnotatedElement annotatedElement) {
                    if (!ANNOTATED_WILDCARD_TYPE.isInstance(annotatedElement)) {
                        return NoOp.INSTANCE;
                    }
                    try {
                        AnnotatedElement[] annotatedUpperBound = ANNOTATED_WILDCARD_TYPE.getAnnotatedUpperBounds(annotatedElement);
                        return annotatedUpperBound.length == 0 ? NoOp.INSTANCE : annotatedUpperBound[this.index];
                    }
                    catch (ClassCastException ignored) {
                        return NoOp.INSTANCE;
                    }
                }

                public boolean equals(@MaybeNull Object object) {
                    if (!super.equals(object)) {
                        return false;
                    }
                    if (this == object) {
                        return true;
                    }
                    if (object == null) {
                        return false;
                    }
                    if (this.getClass() != object.getClass()) {
                        return false;
                    }
                    return this.index == ((ForWildcardUpperBoundType)object).index;
                }

                public int hashCode() {
                    return super.hashCode() * 31 + this.index;
                }

                @JavaDispatcher.Proxied(value="java.lang.reflect.AnnotatedWildcardType")
                protected static interface AnnotatedWildcardType {
                    @JavaDispatcher.Instance
                    @JavaDispatcher.Proxied(value="isInstance")
                    public boolean isInstance(AnnotatedElement var1);

                    @JavaDispatcher.Proxied(value="getAnnotatedUpperBounds")
                    public AnnotatedElement[] getAnnotatedUpperBounds(AnnotatedElement var1);
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            public static abstract class Delegator
            implements AnnotationReader {
                private static final boolean ACCESS_CONTROLLER;

                @AccessControllerPlugin.Enhance
                static <T> T doPrivileged(PrivilegedAction<T> privilegedAction) {
                    PrivilegedAction<T> action;
                    if (ACCESS_CONTROLLER) {
                        return AccessController.doPrivileged(privilegedAction);
                    }
                    return action.run();
                }

                @Override
                public AnnotationReader ofWildcardUpperBoundType(int index) {
                    return new ForWildcardUpperBoundType(this, index);
                }

                @Override
                public AnnotationReader ofWildcardLowerBoundType(int index) {
                    return new ForWildcardLowerBoundType(this, index);
                }

                @Override
                public AnnotationReader ofTypeVariableBoundType(int index) {
                    return new ForTypeVariableBoundType(this, index);
                }

                @Override
                public AnnotationReader ofTypeArgument(int index) {
                    return new ForTypeArgument(this, index);
                }

                @Override
                public AnnotationReader ofOwnerType() {
                    return new ForOwnerType(this);
                }

                @Override
                public AnnotationReader ofOuterClass() {
                    return new ForOwnerType(this);
                }

                @Override
                public AnnotationReader ofComponentType() {
                    return new ForComponentType(this);
                }

                @Override
                public AnnotationList asList() {
                    return new AnnotationList.ForLoadedAnnotations(this.resolve().getDeclaredAnnotations());
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

                public static class ForLoadedRecordComponent
                extends Delegator {
                    private final Object recordComponent;

                    public ForLoadedRecordComponent(Object recordComponent) {
                        this.recordComponent = recordComponent;
                    }

                    public AnnotatedElement resolve() {
                        return RecordComponentDescription.ForLoadedRecordComponent.RECORD_COMPONENT.getAnnotatedType(this.recordComponent);
                    }
                }

                @HashCodeAndEqualsPlugin.Enhance(includeSyntheticFields=true)
                public static class ForLoadedExecutableExceptionType
                extends Delegator {
                    protected static final Dispatcher DISPATCHER = ForLoadedExecutableExceptionType.doPrivileged(JavaDispatcher.of(Dispatcher.class));
                    private final AccessibleObject executable;
                    private final int index;

                    public ForLoadedExecutableExceptionType(AccessibleObject executable, int index) {
                        this.executable = executable;
                        this.index = index;
                    }

                    public AnnotatedElement resolve() {
                        AnnotatedElement[] element = DISPATCHER.getAnnotatedExceptionTypes(this.executable);
                        return element.length == 0 ? NoOp.INSTANCE : element[this.index];
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
                        if (this.index != ((ForLoadedExecutableExceptionType)object).index) {
                            return false;
                        }
                        return this.executable.equals(((ForLoadedExecutableExceptionType)object).executable);
                    }

                    public int hashCode() {
                        return (this.getClass().hashCode() * 31 + this.executable.hashCode()) * 31 + this.index;
                    }

                    @JavaDispatcher.Proxied(value="java.lang.reflect.Executable")
                    protected static interface Dispatcher {
                        @JavaDispatcher.Defaults
                        @JavaDispatcher.Proxied(value="getAnnotatedExceptionTypes")
                        public AnnotatedElement[] getAnnotatedExceptionTypes(Object var1);
                    }
                }

                @HashCodeAndEqualsPlugin.Enhance(includeSyntheticFields=true)
                public static class ForLoadedExecutableParameterType
                extends Delegator {
                    protected static final Dispatcher DISPATCHER = ForLoadedExecutableParameterType.doPrivileged(JavaDispatcher.of(Dispatcher.class));
                    private final AccessibleObject executable;
                    private final int index;

                    public ForLoadedExecutableParameterType(AccessibleObject executable, int index) {
                        this.executable = executable;
                        this.index = index;
                    }

                    public AnnotatedElement resolve() {
                        AnnotatedElement[] element = DISPATCHER.getAnnotatedParameterTypes(this.executable);
                        return element.length == 0 ? NoOp.INSTANCE : element[this.index];
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
                        if (this.index != ((ForLoadedExecutableParameterType)object).index) {
                            return false;
                        }
                        return this.executable.equals(((ForLoadedExecutableParameterType)object).executable);
                    }

                    public int hashCode() {
                        return (this.getClass().hashCode() * 31 + this.executable.hashCode()) * 31 + this.index;
                    }

                    @JavaDispatcher.Proxied(value="java.lang.reflect.Executable")
                    protected static interface Dispatcher {
                        @JavaDispatcher.Defaults
                        @JavaDispatcher.Proxied(value="getAnnotatedParameterTypes")
                        public AnnotatedElement[] getAnnotatedParameterTypes(Object var1);
                    }
                }

                @HashCodeAndEqualsPlugin.Enhance(includeSyntheticFields=true)
                public static class ForLoadedMethodReturnType
                extends Delegator {
                    protected static final Dispatcher DISPATCHER = ForLoadedMethodReturnType.doPrivileged(JavaDispatcher.of(Dispatcher.class));
                    private final Method method;

                    public ForLoadedMethodReturnType(Method method) {
                        this.method = method;
                    }

                    public AnnotatedElement resolve() {
                        AnnotatedElement element = DISPATCHER.getAnnotatedReturnType(this.method);
                        return element == null ? NoOp.INSTANCE : element;
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
                        return this.method.equals(((ForLoadedMethodReturnType)object).method);
                    }

                    public int hashCode() {
                        return this.getClass().hashCode() * 31 + this.method.hashCode();
                    }

                    @JavaDispatcher.Proxied(value="java.lang.reflect.Method")
                    protected static interface Dispatcher {
                        @MaybeNull
                        @JavaDispatcher.Defaults
                        @JavaDispatcher.Proxied(value="getAnnotatedReturnType")
                        public AnnotatedElement getAnnotatedReturnType(Method var1);
                    }
                }

                @HashCodeAndEqualsPlugin.Enhance(includeSyntheticFields=true)
                public static class ForLoadedField
                extends Delegator {
                    protected static final Dispatcher DISPATCHER = ForLoadedField.doPrivileged(JavaDispatcher.of(Dispatcher.class));
                    private final Field field;

                    public ForLoadedField(Field field) {
                        this.field = field;
                    }

                    public AnnotatedElement resolve() {
                        AnnotatedElement element = DISPATCHER.getAnnotatedType(this.field);
                        return element == null ? NoOp.INSTANCE : element;
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
                        return this.field.equals(((ForLoadedField)object).field);
                    }

                    public int hashCode() {
                        return this.getClass().hashCode() * 31 + this.field.hashCode();
                    }

                    @JavaDispatcher.Proxied(value="java.lang.reflect.Field")
                    protected static interface Dispatcher {
                        @MaybeNull
                        @JavaDispatcher.Defaults
                        @JavaDispatcher.Proxied(value="getAnnotatedType")
                        public AnnotatedElement getAnnotatedType(Field var1);
                    }
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                @HashCodeAndEqualsPlugin.Enhance(includeSyntheticFields=true)
                public static class ForLoadedInterface
                extends Delegator {
                    private final Class<?> type;
                    private final int index;

                    public ForLoadedInterface(Class<?> type, int index) {
                        this.type = type;
                        this.index = index;
                    }

                    @Override
                    public AnnotatedElement resolve() {
                        AnnotatedElement[] element = ForLoadedType.DISPATCHER.getAnnotatedInterfaces(this.type);
                        return element.length == 0 ? NoOp.INSTANCE : element[this.index];
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
                        if (this.index != ((ForLoadedInterface)object).index) {
                            return false;
                        }
                        return this.type.equals(((ForLoadedInterface)object).type);
                    }

                    public int hashCode() {
                        return (this.getClass().hashCode() * 31 + this.type.hashCode()) * 31 + this.index;
                    }
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                @HashCodeAndEqualsPlugin.Enhance(includeSyntheticFields=true)
                public static class ForLoadedSuperClass
                extends Delegator {
                    private final Class<?> type;

                    public ForLoadedSuperClass(Class<?> type) {
                        this.type = type;
                    }

                    @Override
                    public AnnotatedElement resolve() {
                        AnnotatedElement element = ForLoadedType.DISPATCHER.getAnnotatedSuperclass(this.type);
                        return element == null ? NoOp.INSTANCE : element;
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
                        return this.type.equals(((ForLoadedSuperClass)object).type);
                    }

                    public int hashCode() {
                        return this.getClass().hashCode() * 31 + this.type.hashCode();
                    }
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                @HashCodeAndEqualsPlugin.Enhance
                public static class ForLoadedTypeVariable
                extends Delegator {
                    private final TypeVariable<?> typeVariable;

                    public ForLoadedTypeVariable(TypeVariable<?> typeVariable) {
                        this.typeVariable = typeVariable;
                    }

                    @Override
                    @SuppressFBWarnings(value={"BC_VACUOUS_INSTANCEOF"}, justification="Cast is required for JVMs before Java 8.")
                    public AnnotatedElement resolve() {
                        return this.typeVariable instanceof AnnotatedElement ? this.typeVariable : NoOp.INSTANCE;
                    }

                    @Override
                    public AnnotationReader ofTypeVariableBoundType(int index) {
                        return new ForTypeVariableBoundType.OfFormalTypeVariable(this.typeVariable, index);
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
                        return this.typeVariable.equals(((ForLoadedTypeVariable)object).typeVariable);
                    }

                    public int hashCode() {
                        return this.getClass().hashCode() * 31 + this.typeVariable.hashCode();
                    }
                }

                @HashCodeAndEqualsPlugin.Enhance
                protected static abstract class Chained
                extends Delegator {
                    protected final AnnotationReader annotationReader;

                    protected Chained(AnnotationReader annotationReader) {
                        this.annotationReader = annotationReader;
                    }

                    public AnnotatedElement resolve() {
                        return this.resolve(this.annotationReader.resolve());
                    }

                    protected abstract AnnotatedElement resolve(AnnotatedElement var1);

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
                        return this.annotationReader.equals(((Chained)object).annotationReader);
                    }

                    public int hashCode() {
                        return this.getClass().hashCode() * 31 + this.annotationReader.hashCode();
                    }
                }

                @HashCodeAndEqualsPlugin.Enhance
                public static class Simple
                extends Delegator {
                    private final AnnotatedElement annotatedElement;

                    public Simple(AnnotatedElement annotatedElement) {
                        this.annotatedElement = annotatedElement;
                    }

                    public AnnotatedElement resolve() {
                        return this.annotatedElement;
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
                        return this.annotatedElement.equals(((Simple)object).annotatedElement);
                    }

                    public int hashCode() {
                        return this.getClass().hashCode() * 31 + this.annotatedElement.hashCode();
                    }
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            public static enum NoOp implements AnnotationReader,
            AnnotatedElement
            {
                INSTANCE;


                @Override
                public AnnotatedElement resolve() {
                    return this;
                }

                @Override
                public AnnotationList asList() {
                    return new AnnotationList.Empty();
                }

                @Override
                public AnnotationReader ofWildcardUpperBoundType(int index) {
                    return this;
                }

                @Override
                public AnnotationReader ofWildcardLowerBoundType(int index) {
                    return this;
                }

                @Override
                public AnnotationReader ofTypeVariableBoundType(int index) {
                    return this;
                }

                @Override
                public AnnotationReader ofTypeArgument(int index) {
                    return this;
                }

                @Override
                public AnnotationReader ofOwnerType() {
                    return this;
                }

                @Override
                public AnnotationReader ofOuterClass() {
                    return this;
                }

                @Override
                public AnnotationReader ofComponentType() {
                    return this;
                }

                @Override
                public boolean isAnnotationPresent(Class<? extends Annotation> annotationClass) {
                    throw new IllegalStateException("Cannot resolve annotations for no-op reader: " + this);
                }

                @Override
                public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
                    throw new IllegalStateException("Cannot resolve annotations for no-op reader: " + this);
                }

                @Override
                public Annotation[] getAnnotations() {
                    throw new IllegalStateException("Cannot resolve annotations for no-op reader: " + this);
                }

                @Override
                public Annotation[] getDeclaredAnnotations() {
                    return new Annotation[0];
                }
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static interface Visitor<T> {
            public T onGenericArray(Generic var1);

            public T onWildcard(Generic var1);

            public T onParameterizedType(Generic var1);

            public T onTypeVariable(Generic var1);

            public T onNonGenericType(Generic var1);

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            @HashCodeAndEqualsPlugin.Enhance
            public static class Reducing
            implements Visitor<TypeDescription> {
                private final TypeDescription declaringType;
                private final List<? extends TypeVariableToken> typeVariableTokens;

                public Reducing(TypeDescription declaringType, TypeVariableToken ... typeVariableToken) {
                    this(declaringType, Arrays.asList(typeVariableToken));
                }

                public Reducing(TypeDescription declaringType, List<? extends TypeVariableToken> typeVariableTokens) {
                    this.declaringType = declaringType;
                    this.typeVariableTokens = typeVariableTokens;
                }

                @Override
                @SuppressFBWarnings(value={"NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE"}, justification="Assuming component type for array type.")
                public TypeDescription onGenericArray(Generic genericArray) {
                    Generic targetType = genericArray;
                    int arity = 0;
                    do {
                        targetType = targetType.getComponentType();
                        ++arity;
                    } while (targetType.isArray());
                    if (targetType.getSort().isTypeVariable()) {
                        for (TypeVariableToken typeVariableToken : this.typeVariableTokens) {
                            if (!targetType.getSymbol().equals(typeVariableToken.getSymbol())) continue;
                            return ArrayProjection.of(((Generic)typeVariableToken.getBounds().get(0)).accept(this), arity);
                        }
                        return TargetType.resolve(ArrayProjection.of(this.declaringType.findExpectedVariable(targetType.getSymbol()).asErasure(), arity), this.declaringType);
                    }
                    return TargetType.resolve(genericArray.asErasure(), this.declaringType);
                }

                @Override
                public TypeDescription onWildcard(Generic wildcard) {
                    throw new IllegalStateException("A wildcard cannot be a top-level type: " + wildcard);
                }

                @Override
                public TypeDescription onParameterizedType(Generic parameterizedType) {
                    return TargetType.resolve(parameterizedType.asErasure(), this.declaringType);
                }

                @Override
                public TypeDescription onTypeVariable(Generic typeVariable) {
                    for (TypeVariableToken typeVariableToken : this.typeVariableTokens) {
                        if (!typeVariable.getSymbol().equals(typeVariableToken.getSymbol())) continue;
                        return ((Generic)typeVariableToken.getBounds().get(0)).accept(this);
                    }
                    return TargetType.resolve(this.declaringType.findExpectedVariable(typeVariable.getSymbol()).asErasure(), this.declaringType);
                }

                @Override
                public TypeDescription onNonGenericType(Generic typeDescription) {
                    return TargetType.resolve(typeDescription.asErasure(), this.declaringType);
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
                    if (!this.declaringType.equals(((Reducing)object).declaringType)) {
                        return false;
                    }
                    return ((Object)this.typeVariableTokens).equals(((Reducing)object).typeVariableTokens);
                }

                public int hashCode() {
                    return (this.getClass().hashCode() * 31 + this.declaringType.hashCode()) * 31 + ((Object)this.typeVariableTokens).hashCode();
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            public static class ForRawType
            implements Visitor<Generic> {
                private final TypeDescription declaringType;

                public ForRawType(TypeDescription declaringType) {
                    this.declaringType = declaringType;
                }

                @Override
                public Generic onGenericArray(Generic genericArray) {
                    return this.declaringType.isGenerified() ? new OfNonGenericType.Latent(genericArray.asErasure(), genericArray) : genericArray;
                }

                @Override
                public Generic onWildcard(Generic wildcard) {
                    throw new IllegalStateException("Did not expect wildcard on top-level: " + wildcard);
                }

                @Override
                public Generic onParameterizedType(Generic parameterizedType) {
                    return this.declaringType.isGenerified() ? new OfNonGenericType.Latent(parameterizedType.asErasure(), parameterizedType) : parameterizedType;
                }

                @Override
                public Generic onTypeVariable(Generic typeVariable) {
                    return this.declaringType.isGenerified() ? new OfNonGenericType.Latent(typeVariable.asErasure(), typeVariable) : typeVariable;
                }

                @Override
                public Generic onNonGenericType(Generic typeDescription) {
                    return typeDescription;
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            public static abstract class Substitutor
            implements Visitor<Generic> {
                @Override
                public Generic onParameterizedType(Generic parameterizedType) {
                    Generic ownerType = parameterizedType.getOwnerType();
                    ArrayList<Generic> typeArguments = new ArrayList<Generic>(parameterizedType.getTypeArguments().size());
                    for (Generic typeArgument : parameterizedType.getTypeArguments()) {
                        typeArguments.add(typeArgument.accept(this));
                    }
                    return new OfParameterizedType.Latent(parameterizedType.asRawType().accept(this).asErasure(), ownerType == null ? UNDEFINED : ownerType.accept(this), typeArguments, parameterizedType);
                }

                @Override
                @SuppressFBWarnings(value={"NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE"}, justification="Assuming component type for array type.")
                public Generic onGenericArray(Generic genericArray) {
                    return new OfGenericArray.Latent(genericArray.getComponentType().accept(this), genericArray);
                }

                @Override
                public Generic onWildcard(Generic wildcard) {
                    return new OfWildcardType.Latent(wildcard.getUpperBounds().accept(this), wildcard.getLowerBounds().accept(this), wildcard);
                }

                @Override
                @SuppressFBWarnings(value={"NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE"}, justification="Assuming component type for array type.")
                public Generic onNonGenericType(Generic typeDescription) {
                    return typeDescription.isArray() ? new OfGenericArray.Latent(typeDescription.getComponentType().accept(this), typeDescription) : this.onSimpleType(typeDescription);
                }

                protected abstract Generic onSimpleType(Generic var1);

                @HashCodeAndEqualsPlugin.Enhance
                public static class ForReplacement
                extends Substitutor {
                    private final TypeDescription typeDescription;

                    public ForReplacement(TypeDescription typeDescription) {
                        this.typeDescription = typeDescription;
                    }

                    public Generic onTypeVariable(Generic typeVariable) {
                        return typeVariable;
                    }

                    protected Generic onSimpleType(Generic typeDescription) {
                        return typeDescription.asErasure().equals(this.typeDescription) ? new OfNonGenericType.Latent(this.typeDescription, typeDescription) : typeDescription;
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
                        return this.typeDescription.equals(((ForReplacement)object).typeDescription);
                    }

                    public int hashCode() {
                        return this.getClass().hashCode() * 31 + this.typeDescription.hashCode();
                    }
                }

                @HashCodeAndEqualsPlugin.Enhance
                public static class ForTokenNormalization
                extends Substitutor {
                    private final TypeDescription typeDescription;

                    public ForTokenNormalization(TypeDescription typeDescription) {
                        this.typeDescription = typeDescription;
                    }

                    protected Generic onSimpleType(Generic typeDescription) {
                        return typeDescription.represents((java.lang.reflect.Type)((Object)TargetType.class)) ? new OfNonGenericType.Latent(this.typeDescription, typeDescription) : typeDescription;
                    }

                    public Generic onTypeVariable(Generic typeVariable) {
                        return new OfTypeVariable.Symbolic(typeVariable.getSymbol(), typeVariable);
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
                        return this.typeDescription.equals(((ForTokenNormalization)object).typeDescription);
                    }

                    public int hashCode() {
                        return this.getClass().hashCode() * 31 + this.typeDescription.hashCode();
                    }
                }

                @HashCodeAndEqualsPlugin.Enhance
                public static class ForTypeVariableBinding
                extends WithoutTypeSubstitution {
                    private final Generic parameterizedType;

                    protected ForTypeVariableBinding(Generic parameterizedType) {
                        this.parameterizedType = parameterizedType;
                    }

                    public Generic onTypeVariable(Generic typeVariable) {
                        return typeVariable.getTypeVariableSource().accept(new TypeVariableSubstitutor(typeVariable));
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
                        return this.parameterizedType.equals(((ForTypeVariableBinding)object).parameterizedType);
                    }

                    public int hashCode() {
                        return this.getClass().hashCode() * 31 + this.parameterizedType.hashCode();
                    }

                    protected class RetainedMethodTypeVariable
                    extends OfTypeVariable {
                        private final Generic typeVariable;

                        protected RetainedMethodTypeVariable(Generic typeVariable) {
                            this.typeVariable = typeVariable;
                        }

                        public TypeList.Generic getUpperBounds() {
                            return this.typeVariable.getUpperBounds().accept(ForTypeVariableBinding.this);
                        }

                        public TypeVariableSource getTypeVariableSource() {
                            return this.typeVariable.getTypeVariableSource();
                        }

                        public String getSymbol() {
                            return this.typeVariable.getSymbol();
                        }

                        public AnnotationList getDeclaredAnnotations() {
                            return this.typeVariable.getDeclaredAnnotations();
                        }
                    }

                    /*
                     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                     */
                    @HashCodeAndEqualsPlugin.Enhance(includeSyntheticFields=true)
                    protected class TypeVariableSubstitutor
                    implements TypeVariableSource.Visitor<Generic> {
                        private final Generic typeVariable;

                        protected TypeVariableSubstitutor(Generic typeVariable) {
                            this.typeVariable = typeVariable;
                        }

                        @Override
                        public Generic onType(TypeDescription typeDescription) {
                            Generic typeArgument = ForTypeVariableBinding.this.parameterizedType.findBindingOf(this.typeVariable);
                            return typeArgument == null ? this.typeVariable.asRawType() : typeArgument;
                        }

                        @Override
                        public Generic onMethod(MethodDescription.InDefinedShape methodDescription) {
                            return new RetainedMethodTypeVariable(this.typeVariable);
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
                            if (!this.typeVariable.equals(((TypeVariableSubstitutor)object).typeVariable)) {
                                return false;
                            }
                            return ForTypeVariableBinding.this.equals(((TypeVariableSubstitutor)object).ForTypeVariableBinding.this);
                        }

                        public int hashCode() {
                            return (this.getClass().hashCode() * 31 + this.typeVariable.hashCode()) * 31 + ForTypeVariableBinding.this.hashCode();
                        }
                    }
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                @HashCodeAndEqualsPlugin.Enhance
                public static class ForDetachment
                extends Substitutor {
                    private final ElementMatcher<? super TypeDescription> typeMatcher;

                    public ForDetachment(ElementMatcher<? super TypeDescription> typeMatcher) {
                        this.typeMatcher = typeMatcher;
                    }

                    public static Visitor<Generic> of(TypeDefinition typeDefinition) {
                        return new ForDetachment(ElementMatchers.is(typeDefinition));
                    }

                    @Override
                    public Generic onTypeVariable(Generic typeVariable) {
                        return new OfTypeVariable.Symbolic(typeVariable.getSymbol(), typeVariable);
                    }

                    @Override
                    protected Generic onSimpleType(Generic typeDescription) {
                        return this.typeMatcher.matches(typeDescription.asErasure()) ? new OfNonGenericType.Latent(TargetType.DESCRIPTION, typeDescription.getOwnerType(), (AnnotationSource)typeDescription) : typeDescription;
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
                        return this.typeMatcher.equals(((ForDetachment)object).typeMatcher);
                    }

                    public int hashCode() {
                        return this.getClass().hashCode() * 31 + this.typeMatcher.hashCode();
                    }
                }

                @HashCodeAndEqualsPlugin.Enhance
                public static class ForAttachment
                extends Substitutor {
                    private final TypeDescription declaringType;
                    private final TypeVariableSource typeVariableSource;

                    protected ForAttachment(TypeDefinition declaringType, TypeVariableSource typeVariableSource) {
                        this(declaringType.asErasure(), typeVariableSource);
                    }

                    protected ForAttachment(TypeDescription declaringType, TypeVariableSource typeVariableSource) {
                        this.declaringType = declaringType;
                        this.typeVariableSource = typeVariableSource;
                    }

                    public static ForAttachment of(TypeDescription typeDescription) {
                        return new ForAttachment(typeDescription, (TypeVariableSource)typeDescription);
                    }

                    @SuppressFBWarnings(value={"NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE"}, justification="Assuming declaring type for type member.")
                    public static ForAttachment of(FieldDescription fieldDescription) {
                        return new ForAttachment(fieldDescription.getDeclaringType(), (TypeVariableSource)fieldDescription.getDeclaringType().asErasure());
                    }

                    public static ForAttachment of(MethodDescription methodDescription) {
                        return new ForAttachment(methodDescription.getDeclaringType(), (TypeVariableSource)methodDescription);
                    }

                    public static ForAttachment of(ParameterDescription parameterDescription) {
                        return new ForAttachment(parameterDescription.getDeclaringMethod().getDeclaringType(), (TypeVariableSource)parameterDescription.getDeclaringMethod());
                    }

                    public static ForAttachment of(RecordComponentDescription recordComponentDescription) {
                        return new ForAttachment(recordComponentDescription.getDeclaringType(), (TypeVariableSource)recordComponentDescription.getDeclaringType().asErasure());
                    }

                    public Generic onTypeVariable(Generic typeVariable) {
                        return new OfTypeVariable.WithAnnotationOverlay(this.typeVariableSource.findExpectedVariable(typeVariable.getSymbol()), typeVariable);
                    }

                    protected Generic onSimpleType(Generic typeDescription) {
                        return typeDescription.represents((java.lang.reflect.Type)((Object)TargetType.class)) ? new OfNonGenericType.Latent(this.declaringType, typeDescription) : typeDescription;
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
                        if (!this.declaringType.equals(((ForAttachment)object).declaringType)) {
                            return false;
                        }
                        return this.typeVariableSource.equals(((ForAttachment)object).typeVariableSource);
                    }

                    public int hashCode() {
                        return (this.getClass().hashCode() * 31 + this.declaringType.hashCode()) * 31 + this.typeVariableSource.hashCode();
                    }
                }

                public static abstract class WithoutTypeSubstitution
                extends Substitutor {
                    public Generic onNonGenericType(Generic typeDescription) {
                        return typeDescription;
                    }

                    protected Generic onSimpleType(Generic typeDescription) {
                        return typeDescription;
                    }
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            @HashCodeAndEqualsPlugin.Enhance
            public static class ForSignatureVisitor
            implements Visitor<SignatureVisitor> {
                private static final int ONLY_CHARACTER = 0;
                protected final SignatureVisitor signatureVisitor;

                public ForSignatureVisitor(SignatureVisitor signatureVisitor) {
                    this.signatureVisitor = signatureVisitor;
                }

                @Override
                @SuppressFBWarnings(value={"NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE"}, justification="Assuming component type for array type.")
                public SignatureVisitor onGenericArray(Generic genericArray) {
                    genericArray.getComponentType().accept(new ForSignatureVisitor(this.signatureVisitor.visitArrayType()));
                    return this.signatureVisitor;
                }

                @Override
                public SignatureVisitor onWildcard(Generic wildcard) {
                    throw new IllegalStateException("Unexpected wildcard: " + wildcard);
                }

                @Override
                public SignatureVisitor onParameterizedType(Generic parameterizedType) {
                    this.onOwnableType(parameterizedType);
                    this.signatureVisitor.visitEnd();
                    return this.signatureVisitor;
                }

                private void onOwnableType(Generic ownableType) {
                    Generic ownerType = ownableType.getOwnerType();
                    if (ownerType != null && ownerType.getSort().isParameterized()) {
                        this.onOwnableType(ownerType);
                        this.signatureVisitor.visitInnerClassType(ownableType.asErasure().getSimpleName());
                    } else {
                        this.signatureVisitor.visitClassType(ownableType.asErasure().getInternalName());
                    }
                    for (Generic typeArgument : ownableType.getTypeArguments()) {
                        typeArgument.accept(new OfTypeArgument(this.signatureVisitor));
                    }
                }

                @Override
                public SignatureVisitor onTypeVariable(Generic typeVariable) {
                    this.signatureVisitor.visitTypeVariable(typeVariable.getSymbol());
                    return this.signatureVisitor;
                }

                @Override
                @SuppressFBWarnings(value={"NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE"}, justification="Assuming component type for array type.")
                public SignatureVisitor onNonGenericType(Generic typeDescription) {
                    if (typeDescription.isArray()) {
                        typeDescription.getComponentType().accept(new ForSignatureVisitor(this.signatureVisitor.visitArrayType()));
                    } else if (typeDescription.isPrimitive()) {
                        this.signatureVisitor.visitBaseType(typeDescription.asErasure().getDescriptor().charAt(0));
                    } else {
                        this.signatureVisitor.visitClassType(typeDescription.asErasure().getInternalName());
                        this.signatureVisitor.visitEnd();
                    }
                    return this.signatureVisitor;
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
                    return this.signatureVisitor.equals(((ForSignatureVisitor)object).signatureVisitor);
                }

                public int hashCode() {
                    return this.getClass().hashCode() * 31 + this.signatureVisitor.hashCode();
                }

                protected static class OfTypeArgument
                extends ForSignatureVisitor {
                    protected OfTypeArgument(SignatureVisitor signatureVisitor) {
                        super(signatureVisitor);
                    }

                    public SignatureVisitor onWildcard(Generic wildcard) {
                        TypeList.Generic upperBounds = wildcard.getUpperBounds();
                        TypeList.Generic lowerBounds = wildcard.getLowerBounds();
                        if (lowerBounds.isEmpty() && ((Generic)upperBounds.getOnly()).represents((java.lang.reflect.Type)((Object)Object.class))) {
                            this.signatureVisitor.visitTypeArgument();
                        } else if (!lowerBounds.isEmpty()) {
                            ((Generic)lowerBounds.getOnly()).accept(new ForSignatureVisitor(this.signatureVisitor.visitTypeArgument('-')));
                        } else {
                            ((Generic)upperBounds.getOnly()).accept(new ForSignatureVisitor(this.signatureVisitor.visitTypeArgument('+')));
                        }
                        return this.signatureVisitor;
                    }

                    public SignatureVisitor onGenericArray(Generic genericArray) {
                        genericArray.accept(new ForSignatureVisitor(this.signatureVisitor.visitTypeArgument('=')));
                        return this.signatureVisitor;
                    }

                    public SignatureVisitor onParameterizedType(Generic parameterizedType) {
                        parameterizedType.accept(new ForSignatureVisitor(this.signatureVisitor.visitTypeArgument('=')));
                        return this.signatureVisitor;
                    }

                    public SignatureVisitor onTypeVariable(Generic typeVariable) {
                        typeVariable.accept(new ForSignatureVisitor(this.signatureVisitor.visitTypeArgument('=')));
                        return this.signatureVisitor;
                    }

                    public SignatureVisitor onNonGenericType(Generic typeDescription) {
                        typeDescription.accept(new ForSignatureVisitor(this.signatureVisitor.visitTypeArgument('=')));
                        return this.signatureVisitor;
                    }
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            public static enum Reifying implements Visitor<Generic>
            {
                INITIATING{

                    public Generic onParameterizedType(Generic parameterizedType) {
                        return parameterizedType;
                    }
                }
                ,
                INHERITING{

                    public Generic onParameterizedType(Generic parameterizedType) {
                        return new OfParameterizedType.ForReifiedType(parameterizedType);
                    }
                };


                @Override
                public Generic onGenericArray(Generic genericArray) {
                    throw new IllegalArgumentException("Cannot reify a generic array: " + genericArray);
                }

                @Override
                public Generic onWildcard(Generic wildcard) {
                    throw new IllegalArgumentException("Cannot reify a wildcard: " + wildcard);
                }

                @Override
                public Generic onTypeVariable(Generic typeVariable) {
                    throw new IllegalArgumentException("Cannot reify a type variable: " + typeVariable);
                }

                @Override
                public Generic onNonGenericType(Generic typeDescription) {
                    TypeDescription erasure = typeDescription.asErasure();
                    return erasure.isGenerified() ? new OfNonGenericType.ForReifiedErasure(erasure) : typeDescription;
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            public static enum Validator implements Visitor<Boolean>
            {
                SUPER_CLASS(false, false, false, false){

                    public Boolean onNonGenericType(Generic typeDescription) {
                        return super.onNonGenericType(typeDescription) != false && !typeDescription.isInterface();
                    }

                    public Boolean onParameterizedType(Generic parameterizedType) {
                        return !parameterizedType.isInterface();
                    }
                }
                ,
                INTERFACE(false, false, false, false){

                    public Boolean onNonGenericType(Generic typeDescription) {
                        return super.onNonGenericType(typeDescription) != false && typeDescription.isInterface();
                    }

                    public Boolean onParameterizedType(Generic parameterizedType) {
                        return parameterizedType.isInterface();
                    }
                }
                ,
                TYPE_VARIABLE(false, false, true, false),
                FIELD(true, true, true, false),
                METHOD_RETURN(true, true, true, true),
                METHOD_PARAMETER(true, true, true, false),
                EXCEPTION(false, false, true, false){

                    public Boolean onParameterizedType(Generic parameterizedType) {
                        return false;
                    }

                    public Boolean onTypeVariable(Generic typeVariable) {
                        for (Generic bound : typeVariable.getUpperBounds()) {
                            if (!bound.accept(this).booleanValue()) continue;
                            return true;
                        }
                        return false;
                    }

                    public Boolean onNonGenericType(Generic typeDescription) {
                        return typeDescription.asErasure().isAssignableTo(Throwable.class);
                    }
                }
                ,
                RECEIVER(false, false, false, false);

                private final boolean acceptsArray;
                private final boolean acceptsPrimitive;
                private final boolean acceptsVariable;
                private final boolean acceptsVoid;

                private Validator(boolean acceptsArray, boolean acceptsPrimitive, boolean acceptsVariable, boolean acceptsVoid) {
                    this.acceptsArray = acceptsArray;
                    this.acceptsPrimitive = acceptsPrimitive;
                    this.acceptsVariable = acceptsVariable;
                    this.acceptsVoid = acceptsVoid;
                }

                @Override
                public Boolean onGenericArray(Generic genericArray) {
                    return this.acceptsArray;
                }

                @Override
                public Boolean onWildcard(Generic wildcard) {
                    return false;
                }

                @Override
                public Boolean onParameterizedType(Generic parameterizedType) {
                    return true;
                }

                @Override
                public Boolean onTypeVariable(Generic typeVariable) {
                    return this.acceptsVariable;
                }

                @Override
                public Boolean onNonGenericType(Generic typeDescription) {
                    return !(!this.acceptsArray && typeDescription.isArray() || !this.acceptsPrimitive && typeDescription.isPrimitive() || !this.acceptsVoid && typeDescription.represents(Void.TYPE));
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                public static enum ForTypeAnnotations implements Visitor<Boolean>
                {
                    INSTANCE;

                    private static final String TYPE_USE = "TYPE_USE";
                    private static final String TYPE_PARAMETER = "TYPE_PARAMETER";

                    public static boolean ofFormalTypeVariable(Generic typeVariable) {
                        HashSet<TypeDescription> annotationTypes = new HashSet<TypeDescription>();
                        for (AnnotationDescription annotationDescription : typeVariable.getDeclaredAnnotations()) {
                            if (annotationDescription.isSupportedOn(TYPE_PARAMETER) && annotationTypes.add(annotationDescription.getAnnotationType())) continue;
                            return false;
                        }
                        return true;
                    }

                    @Override
                    @SuppressFBWarnings(value={"NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE"}, justification="Assuming component type for array type.")
                    public Boolean onGenericArray(Generic genericArray) {
                        return this.isValid(genericArray) && genericArray.getComponentType().accept(this) != false;
                    }

                    @Override
                    public Boolean onWildcard(Generic wildcard) {
                        if (!this.isValid(wildcard)) {
                            return false;
                        }
                        TypeList.Generic lowerBounds = wildcard.getLowerBounds();
                        return ((Generic)(lowerBounds.isEmpty() ? wildcard.getUpperBounds() : lowerBounds).getOnly()).accept(this);
                    }

                    @Override
                    public Boolean onParameterizedType(Generic parameterizedType) {
                        if (!this.isValid(parameterizedType)) {
                            return false;
                        }
                        Generic ownerType = parameterizedType.getOwnerType();
                        if (ownerType != null && !ownerType.accept(this).booleanValue()) {
                            return false;
                        }
                        for (Generic typeArgument : parameterizedType.getTypeArguments()) {
                            if (typeArgument.accept(this).booleanValue()) continue;
                            return false;
                        }
                        return true;
                    }

                    @Override
                    public Boolean onTypeVariable(Generic typeVariable) {
                        return this.isValid(typeVariable);
                    }

                    @Override
                    @SuppressFBWarnings(value={"NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE"}, justification="Assuming component type for array type.")
                    public Boolean onNonGenericType(Generic typeDescription) {
                        return this.isValid(typeDescription) && (!typeDescription.isArray() || typeDescription.getComponentType().accept(this) != false);
                    }

                    private boolean isValid(Generic typeDescription) {
                        HashSet<TypeDescription> annotationTypes = new HashSet<TypeDescription>();
                        for (AnnotationDescription annotationDescription : typeDescription.getDeclaredAnnotations()) {
                            if (annotationDescription.isSupportedOn(TYPE_USE) && annotationTypes.add(annotationDescription.getAnnotationType())) continue;
                            return false;
                        }
                        return true;
                    }
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            public static enum Assigner implements Visitor<Dispatcher>
            {
                INSTANCE;


                @Override
                public Dispatcher onGenericArray(Generic genericArray) {
                    return new Dispatcher.ForGenericArray(genericArray);
                }

                @Override
                public Dispatcher onWildcard(Generic wildcard) {
                    throw new IllegalArgumentException("A wildcard is not a first level type: " + this);
                }

                @Override
                public Dispatcher onParameterizedType(Generic parameterizedType) {
                    return new Dispatcher.ForParameterizedType(parameterizedType);
                }

                @Override
                public Dispatcher onTypeVariable(Generic typeVariable) {
                    return new Dispatcher.ForTypeVariable(typeVariable);
                }

                @Override
                public Dispatcher onNonGenericType(Generic typeDescription) {
                    return new Dispatcher.ForNonGenericType(typeDescription.asErasure());
                }

                public static interface Dispatcher {
                    public boolean isAssignableFrom(Generic var1);

                    @HashCodeAndEqualsPlugin.Enhance
                    public static class ForGenericArray
                    extends AbstractBase {
                        private final Generic genericArray;

                        protected ForGenericArray(Generic genericArray) {
                            this.genericArray = genericArray;
                        }

                        @SuppressFBWarnings(value={"NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE"}, justification="Assuming component type for array type.")
                        public Boolean onGenericArray(Generic genericArray) {
                            return this.genericArray.getComponentType().accept(INSTANCE).isAssignableFrom(genericArray.getComponentType());
                        }

                        public Boolean onWildcard(Generic wildcard) {
                            throw new IllegalArgumentException("A wildcard is not a first-level type: " + wildcard);
                        }

                        public Boolean onParameterizedType(Generic parameterizedType) {
                            return false;
                        }

                        public Boolean onTypeVariable(Generic typeVariable) {
                            return false;
                        }

                        @SuppressFBWarnings(value={"NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE"}, justification="Assuming component type for array type.")
                        public Boolean onNonGenericType(Generic typeDescription) {
                            return typeDescription.isArray() && this.genericArray.getComponentType().accept(INSTANCE).isAssignableFrom(typeDescription.getComponentType());
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
                            return this.genericArray.equals(((ForGenericArray)object).genericArray);
                        }

                        public int hashCode() {
                            return this.getClass().hashCode() * 31 + this.genericArray.hashCode();
                        }
                    }

                    @HashCodeAndEqualsPlugin.Enhance
                    public static class ForParameterizedType
                    extends AbstractBase {
                        private final Generic parameterizedType;

                        protected ForParameterizedType(Generic parameterizedType) {
                            this.parameterizedType = parameterizedType;
                        }

                        public Boolean onGenericArray(Generic genericArray) {
                            return false;
                        }

                        public Boolean onWildcard(Generic wildcard) {
                            throw new IllegalArgumentException("A wildcard is not a first-level type: " + wildcard);
                        }

                        public Boolean onParameterizedType(Generic parameterizedType) {
                            if (this.parameterizedType.asErasure().equals(parameterizedType.asErasure())) {
                                Generic fromOwner = this.parameterizedType.getOwnerType();
                                Generic toOwner = parameterizedType.getOwnerType();
                                if (fromOwner != null && toOwner != null && !fromOwner.accept(INSTANCE).isAssignableFrom(toOwner)) {
                                    return false;
                                }
                                TypeList.Generic fromArguments = this.parameterizedType.getTypeArguments();
                                TypeList.Generic toArguments = parameterizedType.getTypeArguments();
                                if (fromArguments.size() == toArguments.size()) {
                                    for (int index = 0; index < fromArguments.size(); ++index) {
                                        if (((Generic)fromArguments.get(index)).accept(ParameterAssigner.INSTANCE).isAssignableFrom((Generic)toArguments.get(index))) continue;
                                        return false;
                                    }
                                    return true;
                                }
                                throw new IllegalArgumentException("Incompatible generic types: " + parameterizedType + " and " + this.parameterizedType);
                            }
                            Generic superClass = parameterizedType.getSuperClass();
                            if (superClass != null && this.isAssignableFrom(superClass)) {
                                return true;
                            }
                            for (Generic interfaceType : parameterizedType.getInterfaces()) {
                                if (!this.isAssignableFrom(interfaceType)) continue;
                                return true;
                            }
                            return false;
                        }

                        public Boolean onTypeVariable(Generic typeVariable) {
                            for (Generic upperBound : typeVariable.getUpperBounds()) {
                                if (!this.isAssignableFrom(upperBound)) continue;
                                return true;
                            }
                            return false;
                        }

                        public Boolean onNonGenericType(Generic typeDescription) {
                            if (this.parameterizedType.asErasure().equals(typeDescription.asErasure())) {
                                return true;
                            }
                            Generic superClass = typeDescription.getSuperClass();
                            if (superClass != null && this.isAssignableFrom(superClass)) {
                                return true;
                            }
                            for (Generic interfaceType : typeDescription.getInterfaces()) {
                                if (!this.isAssignableFrom(interfaceType)) continue;
                                return true;
                            }
                            return false;
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
                            return this.parameterizedType.equals(((ForParameterizedType)object).parameterizedType);
                        }

                        public int hashCode() {
                            return this.getClass().hashCode() * 31 + this.parameterizedType.hashCode();
                        }

                        /*
                         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                         */
                        protected static enum ParameterAssigner implements Visitor<Dispatcher>
                        {
                            INSTANCE;


                            @Override
                            public Dispatcher onGenericArray(Generic genericArray) {
                                return new InvariantBinding(genericArray);
                            }

                            @Override
                            public Dispatcher onWildcard(Generic wildcard) {
                                TypeList.Generic lowerBounds = wildcard.getLowerBounds();
                                return lowerBounds.isEmpty() ? new CovariantBinding((Generic)wildcard.getUpperBounds().getOnly()) : new ContravariantBinding((Generic)lowerBounds.getOnly());
                            }

                            @Override
                            public Dispatcher onParameterizedType(Generic parameterizedType) {
                                return new InvariantBinding(parameterizedType);
                            }

                            @Override
                            public Dispatcher onTypeVariable(Generic typeVariable) {
                                return new InvariantBinding(typeVariable);
                            }

                            @Override
                            public Dispatcher onNonGenericType(Generic typeDescription) {
                                return new InvariantBinding(typeDescription);
                            }

                            @HashCodeAndEqualsPlugin.Enhance
                            protected static class ContravariantBinding
                            implements Dispatcher {
                                private final Generic lowerBound;

                                protected ContravariantBinding(Generic lowerBound) {
                                    this.lowerBound = lowerBound;
                                }

                                public boolean isAssignableFrom(Generic typeDescription) {
                                    if (typeDescription.getSort().isWildcard()) {
                                        TypeList.Generic lowerBounds = typeDescription.getLowerBounds();
                                        return !lowerBounds.isEmpty() && ((Generic)lowerBounds.getOnly()).accept(Assigner.INSTANCE).isAssignableFrom(this.lowerBound);
                                    }
                                    return typeDescription.getSort().isWildcard() || typeDescription.accept(Assigner.INSTANCE).isAssignableFrom(this.lowerBound);
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
                                    return this.lowerBound.equals(((ContravariantBinding)object).lowerBound);
                                }

                                public int hashCode() {
                                    return this.getClass().hashCode() * 31 + this.lowerBound.hashCode();
                                }
                            }

                            @HashCodeAndEqualsPlugin.Enhance
                            protected static class CovariantBinding
                            implements Dispatcher {
                                private final Generic upperBound;

                                protected CovariantBinding(Generic upperBound) {
                                    this.upperBound = upperBound;
                                }

                                public boolean isAssignableFrom(Generic typeDescription) {
                                    if (typeDescription.getSort().isWildcard()) {
                                        return typeDescription.getLowerBounds().isEmpty() && this.upperBound.accept(Assigner.INSTANCE).isAssignableFrom((Generic)typeDescription.getUpperBounds().getOnly());
                                    }
                                    return this.upperBound.accept(Assigner.INSTANCE).isAssignableFrom(typeDescription);
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
                                    return this.upperBound.equals(((CovariantBinding)object).upperBound);
                                }

                                public int hashCode() {
                                    return this.getClass().hashCode() * 31 + this.upperBound.hashCode();
                                }
                            }

                            @HashCodeAndEqualsPlugin.Enhance
                            protected static class InvariantBinding
                            implements Dispatcher {
                                private final Generic typeDescription;

                                protected InvariantBinding(Generic typeDescription) {
                                    this.typeDescription = typeDescription;
                                }

                                public boolean isAssignableFrom(Generic typeDescription) {
                                    return typeDescription.equals(this.typeDescription);
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
                                    return this.typeDescription.equals(((InvariantBinding)object).typeDescription);
                                }

                                public int hashCode() {
                                    return this.getClass().hashCode() * 31 + this.typeDescription.hashCode();
                                }
                            }
                        }
                    }

                    @HashCodeAndEqualsPlugin.Enhance
                    public static class ForTypeVariable
                    extends AbstractBase {
                        private final Generic typeVariable;

                        protected ForTypeVariable(Generic typeVariable) {
                            this.typeVariable = typeVariable;
                        }

                        public Boolean onGenericArray(Generic genericArray) {
                            return false;
                        }

                        public Boolean onWildcard(Generic wildcard) {
                            throw new IllegalArgumentException("A wildcard is not a first-level type: " + wildcard);
                        }

                        public Boolean onParameterizedType(Generic parameterizedType) {
                            return false;
                        }

                        public Boolean onTypeVariable(Generic typeVariable) {
                            if (typeVariable.equals(this.typeVariable)) {
                                return true;
                            }
                            for (Generic upperBound : typeVariable.getUpperBounds()) {
                                if (!this.isAssignableFrom(upperBound)) continue;
                                return true;
                            }
                            return false;
                        }

                        public Boolean onNonGenericType(Generic typeDescription) {
                            return false;
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
                            return this.typeVariable.equals(((ForTypeVariable)object).typeVariable);
                        }

                        public int hashCode() {
                            return this.getClass().hashCode() * 31 + this.typeVariable.hashCode();
                        }
                    }

                    @HashCodeAndEqualsPlugin.Enhance
                    public static class ForNonGenericType
                    extends AbstractBase {
                        private final TypeDescription typeDescription;

                        protected ForNonGenericType(TypeDescription typeDescription) {
                            this.typeDescription = typeDescription;
                        }

                        @SuppressFBWarnings(value={"NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE"}, justification="Assuming component type for array type.")
                        public Boolean onGenericArray(Generic genericArray) {
                            return this.typeDescription.isArray() ? genericArray.getComponentType().accept(new ForNonGenericType(this.typeDescription.getComponentType())) : this.typeDescription.represents((java.lang.reflect.Type)((Object)Object.class)) || ARRAY_INTERFACES.contains(this.typeDescription.asGenericType());
                        }

                        public Boolean onWildcard(Generic wildcard) {
                            throw new IllegalArgumentException("A wildcard is not a first-level type: " + wildcard);
                        }

                        public Boolean onParameterizedType(Generic parameterizedType) {
                            if (this.typeDescription.equals(parameterizedType.asErasure())) {
                                return true;
                            }
                            Generic superClass = parameterizedType.getSuperClass();
                            if (superClass != null && this.isAssignableFrom(superClass)) {
                                return true;
                            }
                            for (Generic interfaceType : parameterizedType.getInterfaces()) {
                                if (!this.isAssignableFrom(interfaceType)) continue;
                                return true;
                            }
                            return this.typeDescription.represents((java.lang.reflect.Type)((Object)Object.class));
                        }

                        public Boolean onTypeVariable(Generic typeVariable) {
                            for (Generic upperBound : typeVariable.getUpperBounds()) {
                                if (!this.isAssignableFrom(upperBound)) continue;
                                return true;
                            }
                            return false;
                        }

                        public Boolean onNonGenericType(Generic typeDescription) {
                            return this.typeDescription.isAssignableFrom(typeDescription.asErasure());
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
                            return this.typeDescription.equals(((ForNonGenericType)object).typeDescription);
                        }

                        public int hashCode() {
                            return this.getClass().hashCode() * 31 + this.typeDescription.hashCode();
                        }
                    }

                    /*
                     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                     */
                    public static abstract class AbstractBase
                    implements Dispatcher,
                    Visitor<Boolean> {
                        @Override
                        public boolean isAssignableFrom(Generic typeDescription) {
                            return typeDescription.accept(this);
                        }
                    }
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            public static enum AnnotationStripper implements Visitor<Generic>
            {
                INSTANCE;


                @Override
                @SuppressFBWarnings(value={"NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE"}, justification="Assuming component type for array type.")
                public Generic onGenericArray(Generic genericArray) {
                    return new OfGenericArray.Latent(genericArray.getComponentType().accept(this), AnnotationSource.Empty.INSTANCE);
                }

                @Override
                public Generic onWildcard(Generic wildcard) {
                    return new OfWildcardType.Latent(wildcard.getUpperBounds().accept(this), wildcard.getLowerBounds().accept(this), AnnotationSource.Empty.INSTANCE);
                }

                @Override
                public Generic onParameterizedType(Generic parameterizedType) {
                    Generic ownerType = parameterizedType.getOwnerType();
                    return new OfParameterizedType.Latent(parameterizedType.asErasure(), ownerType == null ? UNDEFINED : ownerType.accept(this), parameterizedType.getTypeArguments().accept(this), AnnotationSource.Empty.INSTANCE);
                }

                @Override
                public Generic onTypeVariable(Generic typeVariable) {
                    return new NonAnnotatedTypeVariable(typeVariable);
                }

                @Override
                @SuppressFBWarnings(value={"NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE"}, justification="Assuming component type for array type.")
                public Generic onNonGenericType(Generic typeDescription) {
                    return typeDescription.isArray() ? new OfGenericArray.Latent(this.onNonGenericType(typeDescription.getComponentType()), AnnotationSource.Empty.INSTANCE) : new OfNonGenericType.Latent(typeDescription.asErasure(), AnnotationSource.Empty.INSTANCE);
                }

                protected static class NonAnnotatedTypeVariable
                extends OfTypeVariable {
                    private final Generic typeVariable;

                    protected NonAnnotatedTypeVariable(Generic typeVariable) {
                        this.typeVariable = typeVariable;
                    }

                    public TypeList.Generic getUpperBounds() {
                        return this.typeVariable.getUpperBounds();
                    }

                    public TypeVariableSource getTypeVariableSource() {
                        return this.typeVariable.getTypeVariableSource();
                    }

                    public String getSymbol() {
                        return this.typeVariable.getSymbol();
                    }

                    public AnnotationList getDeclaredAnnotations() {
                        return new AnnotationList.Empty();
                    }
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            public static enum TypeErasing implements Visitor<Generic>
            {
                INSTANCE;


                @Override
                public Generic onGenericArray(Generic genericArray) {
                    return genericArray.asRawType();
                }

                @Override
                public Generic onWildcard(Generic wildcard) {
                    throw new IllegalArgumentException("Cannot erase a wildcard type: " + wildcard);
                }

                @Override
                public Generic onParameterizedType(Generic parameterizedType) {
                    return parameterizedType.asRawType();
                }

                @Override
                public Generic onTypeVariable(Generic typeVariable) {
                    return typeVariable.asRawType();
                }

                @Override
                public Generic onNonGenericType(Generic typeDescription) {
                    return typeDescription.asRawType();
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            public static enum NoOp implements Visitor<Generic>
            {
                INSTANCE;


                @Override
                public Generic onGenericArray(Generic genericArray) {
                    return genericArray;
                }

                @Override
                public Generic onWildcard(Generic wildcard) {
                    return wildcard;
                }

                @Override
                public Generic onParameterizedType(Generic parameterizedType) {
                    return parameterizedType;
                }

                @Override
                public Generic onTypeVariable(Generic typeVariable) {
                    return typeVariable;
                }

                @Override
                public Generic onNonGenericType(Generic typeDescription) {
                    return typeDescription;
                }
            }
        }
    }
}

