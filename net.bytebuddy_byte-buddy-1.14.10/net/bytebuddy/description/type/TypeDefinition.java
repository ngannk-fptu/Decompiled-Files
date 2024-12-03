/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy.description.type;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Iterator;
import java.util.NoSuchElementException;
import net.bytebuddy.build.AccessControllerPlugin;
import net.bytebuddy.description.ModifierReviewable;
import net.bytebuddy.description.NamedElement;
import net.bytebuddy.description.field.FieldList;
import net.bytebuddy.description.method.MethodList;
import net.bytebuddy.description.type.RecordComponentList;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.description.type.TypeList;
import net.bytebuddy.implementation.bytecode.StackSize;
import net.bytebuddy.utility.dispatcher.JavaDispatcher;
import net.bytebuddy.utility.nullability.MaybeNull;
import net.bytebuddy.utility.nullability.UnknownNull;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface TypeDefinition
extends NamedElement,
ModifierReviewable.ForTypeDefinition,
Iterable<TypeDefinition> {
    public static final String RAW_TYPES_PROPERTY = "net.bytebuddy.raw";

    public TypeDescription.Generic asGenericType();

    public TypeDescription asErasure();

    @MaybeNull
    public TypeDescription.Generic getSuperClass();

    public TypeList.Generic getInterfaces();

    public FieldList<?> getDeclaredFields();

    public MethodList<?> getDeclaredMethods();

    @MaybeNull
    public TypeDefinition getComponentType();

    public RecordComponentList<?> getRecordComponents();

    public Sort getSort();

    public String getTypeName();

    public StackSize getStackSize();

    public boolean isArray();

    public boolean isRecord();

    public boolean isPrimitive();

    public boolean represents(Type var1);

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class SuperClassIterator
    implements Iterator<TypeDefinition> {
        @UnknownNull
        private TypeDefinition nextClass;

        public SuperClassIterator(TypeDefinition initialType) {
            this.nextClass = initialType;
        }

        @Override
        public boolean hasNext() {
            return this.nextClass != null;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public TypeDefinition next() {
            if (!this.hasNext()) {
                throw new NoSuchElementException("End of type hierarchy");
            }
            try {
                TypeDefinition typeDefinition = this.nextClass;
                Object var3_2 = null;
                this.nextClass = this.nextClass.getSuperClass();
                return typeDefinition;
            }
            catch (Throwable throwable) {
                Object var3_3 = null;
                this.nextClass = this.nextClass.getSuperClass();
                throw throwable;
            }
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("remove");
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static final class Sort
    extends Enum<Sort> {
        public static final /* enum */ Sort NON_GENERIC;
        public static final /* enum */ Sort GENERIC_ARRAY;
        public static final /* enum */ Sort PARAMETERIZED;
        public static final /* enum */ Sort WILDCARD;
        public static final /* enum */ Sort VARIABLE;
        public static final /* enum */ Sort VARIABLE_SYMBOLIC;
        private static final AnnotatedType ANNOTATED_TYPE;
        private static final /* synthetic */ Sort[] $VALUES;
        private static final boolean ACCESS_CONTROLLER;

        public static Sort[] values() {
            return (Sort[])$VALUES.clone();
        }

        public static Sort valueOf(String name) {
            return Enum.valueOf(Sort.class, name);
        }

        @AccessControllerPlugin.Enhance
        private static <T> T doPrivileged(PrivilegedAction<T> privilegedAction) {
            PrivilegedAction<T> action;
            if (ACCESS_CONTROLLER) {
                return AccessController.doPrivileged(privilegedAction);
            }
            return action.run();
        }

        public static TypeDescription.Generic describe(Type type) {
            return Sort.describe(type, TypeDescription.Generic.AnnotationReader.NoOp.INSTANCE);
        }

        public static TypeDescription.Generic describeAnnotated(AnnotatedElement annotatedType) {
            if (!ANNOTATED_TYPE.isInstance(annotatedType)) {
                throw new IllegalArgumentException("Not an instance of AnnotatedType: " + annotatedType);
            }
            return Sort.describe(ANNOTATED_TYPE.getType(annotatedType), new TypeDescription.Generic.AnnotationReader.Delegator.Simple(annotatedType));
        }

        protected static TypeDescription.Generic describe(Type type, TypeDescription.Generic.AnnotationReader annotationReader) {
            if (type instanceof Class) {
                return new TypeDescription.Generic.OfNonGenericType.ForLoadedType((Class)type, annotationReader);
            }
            if (type instanceof GenericArrayType) {
                return new TypeDescription.Generic.OfGenericArray.ForLoadedType((GenericArrayType)type, annotationReader);
            }
            if (type instanceof ParameterizedType) {
                return new TypeDescription.Generic.OfParameterizedType.ForLoadedType((ParameterizedType)type, annotationReader);
            }
            if (type instanceof TypeVariable) {
                return new TypeDescription.Generic.OfTypeVariable.ForLoadedType((TypeVariable)type, annotationReader);
            }
            if (type instanceof WildcardType) {
                return new TypeDescription.Generic.OfWildcardType.ForLoadedType((WildcardType)type, annotationReader);
            }
            throw new IllegalArgumentException("Unknown type: " + type);
        }

        public boolean isNonGeneric() {
            return this == NON_GENERIC;
        }

        public boolean isParameterized() {
            return this == PARAMETERIZED;
        }

        public boolean isGenericArray() {
            return this == GENERIC_ARRAY;
        }

        public boolean isWildcard() {
            return this == WILDCARD;
        }

        public boolean isTypeVariable() {
            return this == VARIABLE || this == VARIABLE_SYMBOLIC;
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
            NON_GENERIC = new Sort();
            GENERIC_ARRAY = new Sort();
            PARAMETERIZED = new Sort();
            WILDCARD = new Sort();
            VARIABLE = new Sort();
            VARIABLE_SYMBOLIC = new Sort();
            $VALUES = new Sort[]{NON_GENERIC, GENERIC_ARRAY, PARAMETERIZED, WILDCARD, VARIABLE, VARIABLE_SYMBOLIC};
            ANNOTATED_TYPE = Sort.doPrivileged(JavaDispatcher.of(AnnotatedType.class));
        }

        @JavaDispatcher.Proxied(value="java.lang.reflect.AnnotatedType")
        protected static interface AnnotatedType {
            @JavaDispatcher.Instance
            @JavaDispatcher.Proxied(value="isInstance")
            public boolean isInstance(AnnotatedElement var1);

            @JavaDispatcher.Proxied(value="getType")
            public Type getType(AnnotatedElement var1);
        }
    }
}

