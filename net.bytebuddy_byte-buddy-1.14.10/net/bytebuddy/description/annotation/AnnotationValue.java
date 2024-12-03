/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package net.bytebuddy.description.annotation;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.lang.annotation.Annotation;
import java.lang.annotation.AnnotationTypeMismatchException;
import java.lang.annotation.IncompleteAnnotationException;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.bytebuddy.ClassFileVersion;
import net.bytebuddy.build.CachedReturnPlugin;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.enumeration.EnumerationDescription;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDefinition;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.utility.nullability.AlwaysNull;
import net.bytebuddy.utility.nullability.MaybeNull;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface AnnotationValue<T, S> {
    @AlwaysNull
    public static final AnnotationValue<?, ?> UNDEFINED = null;

    public State getState();

    public Sort getSort();

    public AnnotationValue<T, S> filter(MethodDescription.InDefinedShape var1);

    public AnnotationValue<T, S> filter(MethodDescription.InDefinedShape var1, TypeDefinition var2);

    public T resolve();

    public <W> W resolve(Class<? extends W> var1);

    public Loaded<S> load(@MaybeNull ClassLoader var1);

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class ForIncompatibleType<U, V>
    extends AbstractBase<U, V> {
        private final TypeDescription typeDescription;

        public ForIncompatibleType(TypeDescription typeDescription) {
            this.typeDescription = typeDescription;
        }

        @Override
        public State getState() {
            return State.UNRESOLVED;
        }

        @Override
        public Sort getSort() {
            return Sort.NONE;
        }

        @Override
        public AnnotationValue<U, V> filter(MethodDescription.InDefinedShape property, TypeDefinition typeDefinition) {
            return this;
        }

        @Override
        public U resolve() {
            throw new IllegalStateException("Property is defined with an incompatible runtime type: " + this.typeDescription);
        }

        @Override
        public net.bytebuddy.description.annotation.AnnotationValue$Loaded<V> load(@MaybeNull ClassLoader classLoader) {
            try {
                return new Loaded(Class.forName(this.typeDescription.getName(), false, classLoader));
            }
            catch (ClassNotFoundException exception) {
                return new ForMissingType.Loaded(this.typeDescription.getName(), exception);
            }
        }

        public String toString() {
            return "/* Warning type incompatibility! \"" + this.typeDescription.getName() + "\" */";
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static class Loaded<W>
        extends Loaded.AbstractBase.ForUnresolvedProperty<W> {
            private final Class<?> type;

            public Loaded(Class<?> type) {
                this.type = type;
            }

            @Override
            public W resolve() {
                throw new IncompatibleClassChangeError(this.type.toString());
            }

            public String toString() {
                return "/* Warning type incompatibility! \"" + this.type.getName() + "\" */";
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class ForMissingValue<U, V>
    extends AbstractBase<U, V> {
        private final TypeDescription typeDescription;
        private final String property;

        public ForMissingValue(TypeDescription typeDescription, String property) {
            this.typeDescription = typeDescription;
            this.property = property;
        }

        @Override
        public State getState() {
            return State.UNDEFINED;
        }

        @Override
        public Sort getSort() {
            return Sort.NONE;
        }

        @Override
        public AnnotationValue<U, V> filter(MethodDescription.InDefinedShape property, TypeDefinition typeDefinition) {
            return this;
        }

        @Override
        public net.bytebuddy.description.annotation.AnnotationValue$Loaded<V> load(@MaybeNull ClassLoader classLoader) {
            try {
                Class<?> type = Class.forName(this.typeDescription.getName(), false, classLoader);
                return type.isAnnotation() ? new Loaded(type, this.property) : new ForIncompatibleType.Loaded(type);
            }
            catch (ClassNotFoundException exception) {
                return new ForMissingType.Loaded(this.typeDescription.getName(), exception);
            }
        }

        @Override
        public U resolve() {
            throw new IllegalStateException(this.typeDescription + " does not define " + this.property);
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static class Loaded<W>
        extends Loaded.AbstractBase<W> {
            private final Class<? extends Annotation> type;
            private final String property;

            public Loaded(Class<? extends Annotation> type, String property) {
                this.type = type;
                this.property = property;
            }

            @Override
            public State getState() {
                return State.UNDEFINED;
            }

            @Override
            public W resolve() {
                throw new IncompleteAnnotationException(this.type, this.property);
            }

            @Override
            public boolean represents(Object value) {
                return false;
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class ForMismatchedType<U, V>
    extends AbstractBase<U, V> {
        private final MethodDescription.InDefinedShape property;
        private final String value;

        public ForMismatchedType(MethodDescription.InDefinedShape property, String value) {
            this.property = property;
            this.value = value;
        }

        @Override
        public State getState() {
            return State.UNRESOLVED;
        }

        @Override
        public Sort getSort() {
            return Sort.NONE;
        }

        @Override
        public AnnotationValue<U, V> filter(MethodDescription.InDefinedShape property, TypeDefinition typeDefinition) {
            return new ForMismatchedType<U, V>(property, this.value);
        }

        @Override
        public U resolve() {
            throw new IllegalStateException(this.value + " cannot be used as value for " + this.property);
        }

        @Override
        public net.bytebuddy.description.annotation.AnnotationValue$Loaded<V> load(@MaybeNull ClassLoader classLoader) {
            try {
                Class<?> type = Class.forName(this.property.getDeclaringType().getName(), false, classLoader);
                try {
                    return new Loaded(type.getMethod(this.property.getName(), new Class[0]), this.value);
                }
                catch (NoSuchMethodException exception) {
                    return new ForIncompatibleType.Loaded(type);
                }
            }
            catch (ClassNotFoundException exception) {
                return new ForMissingType.Loaded(this.property.getDeclaringType().getName(), exception);
            }
        }

        public String toString() {
            return "/* Warning type mismatch! \"" + this.value + "\" */";
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static class Loaded<W>
        extends Loaded.AbstractBase.ForUnresolvedProperty<W> {
            private final Method property;
            private final String value;

            public Loaded(Method property, String value) {
                this.property = property;
                this.value = value;
            }

            @Override
            public W resolve() {
                throw new AnnotationTypeMismatchException(this.property, this.value);
            }

            public String toString() {
                return "/* Warning type mismatch! \"" + this.value + "\" */";
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class ForMissingType<U, V>
    extends AbstractBase<U, V> {
        private final String typeName;

        public ForMissingType(String typeName) {
            this.typeName = typeName;
        }

        @Override
        public State getState() {
            return State.UNRESOLVED;
        }

        @Override
        public Sort getSort() {
            return Sort.NONE;
        }

        @Override
        public AnnotationValue<U, V> filter(MethodDescription.InDefinedShape property, TypeDefinition typeDefinition) {
            return this;
        }

        @Override
        public U resolve() {
            throw new IllegalStateException("Type not found: " + this.typeName);
        }

        @Override
        public net.bytebuddy.description.annotation.AnnotationValue$Loaded<V> load(@MaybeNull ClassLoader classLoader) {
            return new Loaded(this.typeName, new ClassNotFoundException(this.typeName));
        }

        public String toString() {
            return this.typeName + ".class /* Warning: type not present! */";
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static class Loaded<U>
        extends Loaded.AbstractBase.ForUnresolvedProperty<U> {
            private final String typeName;
            private final ClassNotFoundException exception;

            public Loaded(String typeName, ClassNotFoundException exception) {
                this.typeName = typeName;
                this.exception = exception;
            }

            @Override
            public U resolve() {
                throw new TypeNotPresentException(this.typeName, this.exception);
            }

            public String toString() {
                return this.typeName + ".class /* Warning: type not present! */";
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class ForDescriptionArray<U, V>
    extends AbstractBase<U, V> {
        private final Class<?> unloadedComponentType;
        private final TypeDescription componentType;
        private final List<? extends AnnotationValue<?, ?>> values;
        private transient /* synthetic */ int hashCode;

        public ForDescriptionArray(Class<?> unloadedComponentType, TypeDescription componentType, List<? extends AnnotationValue<?, ?>> values) {
            this.unloadedComponentType = unloadedComponentType;
            this.componentType = componentType;
            this.values = values;
        }

        public static <W extends Enum<W>> AnnotationValue<EnumerationDescription[], W[]> of(TypeDescription enumerationType, EnumerationDescription[] enumerationDescription) {
            ArrayList values = new ArrayList(enumerationDescription.length);
            for (EnumerationDescription value : enumerationDescription) {
                if (!value.getEnumerationType().equals(enumerationType)) {
                    throw new IllegalArgumentException(value + " is not of " + enumerationType);
                }
                values.add(ForEnumerationDescription.of(value));
            }
            return new ForDescriptionArray<EnumerationDescription[], W[]>(EnumerationDescription.class, enumerationType, values);
        }

        public static <W extends Annotation> AnnotationValue<AnnotationDescription[], W[]> of(TypeDescription annotationType, AnnotationDescription[] annotationDescription) {
            ArrayList values = new ArrayList(annotationDescription.length);
            for (AnnotationDescription value : annotationDescription) {
                if (!value.getAnnotationType().equals(annotationType)) {
                    throw new IllegalArgumentException(value + " is not of " + annotationType);
                }
                values.add(new ForAnnotationDescription(value));
            }
            return new ForDescriptionArray<AnnotationDescription[], W[]>(AnnotationDescription.class, annotationType, values);
        }

        public static AnnotationValue<TypeDescription[], Class<?>[]> of(TypeDescription[] typeDescription) {
            ArrayList values = new ArrayList(typeDescription.length);
            for (TypeDescription value : typeDescription) {
                values.add(ForTypeDescription.of(value));
            }
            return new ForDescriptionArray<TypeDescription[], Class<?>[]>(TypeDescription.class, TypeDescription.ForLoadedType.of(Class.class), values);
        }

        @Override
        public State getState() {
            return State.RESOLVED;
        }

        @Override
        public Sort getSort() {
            return Sort.ARRAY;
        }

        @Override
        @SuppressFBWarnings(value={"NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE"}, justification="Assuming component type for array type.")
        public AnnotationValue<U, V> filter(MethodDescription.InDefinedShape property, TypeDefinition typeDefinition) {
            if (typeDefinition.isArray() && typeDefinition.getComponentType().asErasure().equals(this.componentType)) {
                for (AnnotationValue<?, ?> value : this.values) {
                    if ((value = value.filter(property, typeDefinition.getComponentType())).getState() == State.RESOLVED) continue;
                    return value;
                }
                return this;
            }
            return new ForMismatchedType(property, RenderingDispatcher.CURRENT.toArrayErrorString(Sort.of(this.componentType)));
        }

        @Override
        public U resolve() {
            Object resolved = Array.newInstance(this.unloadedComponentType, this.values.size());
            int index = 0;
            for (AnnotationValue<?, ?> value : this.values) {
                Array.set(resolved, index++, value.resolve());
            }
            return (U)resolved;
        }

        @Override
        public net.bytebuddy.description.annotation.AnnotationValue$Loaded<V> load(@MaybeNull ClassLoader classLoader) {
            ArrayList values = new ArrayList(this.values.size());
            for (AnnotationValue<?, ?> value : this.values) {
                values.add(value.load(classLoader));
            }
            try {
                return new Loaded(Class.forName(this.componentType.getName(), false, classLoader), values);
            }
            catch (ClassNotFoundException exception) {
                return new ForMissingType.Loaded(this.componentType.getName(), exception);
            }
        }

        @CachedReturnPlugin.Enhance(value="hashCode")
        public int hashCode() {
            int n;
            int n2;
            int n3 = this.hashCode;
            if (n3 != 0) {
                n2 = 0;
            } else {
                ForDescriptionArray forDescriptionArray = this;
                int result = 1;
                for (AnnotationValue<?, ?> value : forDescriptionArray.values) {
                    result = 31 * result + value.hashCode();
                }
                n2 = n = result;
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
            if (!(other instanceof AnnotationValue)) {
                return false;
            }
            AnnotationValue annotationValue = (AnnotationValue)other;
            Object value = annotationValue.resolve();
            if (!value.getClass().isArray()) {
                return false;
            }
            if (this.values.size() != Array.getLength(value)) {
                return false;
            }
            Iterator<AnnotationValue<?, ?>> iterator = this.values.iterator();
            for (int index = 0; index < this.values.size(); ++index) {
                AnnotationValue<?, ?> self = iterator.next();
                if (self.resolve().equals(Array.get(value, index))) continue;
                return false;
            }
            return true;
        }

        public String toString() {
            return RenderingDispatcher.CURRENT.toSourceString(this.values);
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        protected static class Loaded<W>
        extends Loaded.AbstractBase<W> {
            private final Class<W> componentType;
            private final List<net.bytebuddy.description.annotation.AnnotationValue$Loaded<?>> values;
            private transient /* synthetic */ int hashCode;

            protected Loaded(Class<W> componentType, List<net.bytebuddy.description.annotation.AnnotationValue$Loaded<?>> values) {
                this.componentType = componentType;
                this.values = values;
            }

            @Override
            public State getState() {
                for (net.bytebuddy.description.annotation.AnnotationValue$Loaded<?> value : this.values) {
                    if (value.getState().isResolved()) continue;
                    return State.UNRESOLVED;
                }
                return State.RESOLVED;
            }

            @Override
            public W resolve() {
                Object array = Array.newInstance(this.componentType, this.values.size());
                int index = 0;
                for (net.bytebuddy.description.annotation.AnnotationValue$Loaded<?> annotationValue : this.values) {
                    Array.set(array, index++, annotationValue.resolve());
                }
                return (W)array;
            }

            @Override
            public boolean represents(Object value) {
                if (!(value instanceof Object[])) {
                    return false;
                }
                if (value.getClass().getComponentType() != this.componentType) {
                    return false;
                }
                Object[] array = (Object[])value;
                if (this.values.size() != array.length) {
                    return false;
                }
                Iterator<net.bytebuddy.description.annotation.AnnotationValue$Loaded<?>> iterator = this.values.iterator();
                for (Object aValue : array) {
                    net.bytebuddy.description.annotation.AnnotationValue$Loaded<?> self = iterator.next();
                    if (self.represents(aValue)) continue;
                    return false;
                }
                return true;
            }

            @CachedReturnPlugin.Enhance(value="hashCode")
            public int hashCode() {
                int n;
                int n2;
                int n3 = this.hashCode;
                if (n3 != 0) {
                    n2 = 0;
                } else {
                    Loaded loaded = this;
                    int result = 1;
                    for (net.bytebuddy.description.annotation.AnnotationValue$Loaded<?> value : loaded.values) {
                        result = 31 * result + value.hashCode();
                    }
                    n2 = n = result;
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
                if (!(other instanceof net.bytebuddy.description.annotation.AnnotationValue$Loaded)) {
                    return false;
                }
                net.bytebuddy.description.annotation.AnnotationValue$Loaded annotationValue = (net.bytebuddy.description.annotation.AnnotationValue$Loaded)other;
                if (!annotationValue.getState().isResolved()) {
                    return false;
                }
                Object value = annotationValue.resolve();
                if (!(value instanceof Object[])) {
                    return false;
                }
                Object[] arrayValue = (Object[])value;
                if (this.values.size() != arrayValue.length) {
                    return false;
                }
                Iterator<net.bytebuddy.description.annotation.AnnotationValue$Loaded<?>> iterator = this.values.iterator();
                for (Object aValue : arrayValue) {
                    net.bytebuddy.description.annotation.AnnotationValue$Loaded<?> self = iterator.next();
                    if (self.getState().isResolved() && self.resolve().equals(aValue)) continue;
                    return false;
                }
                return true;
            }

            public String toString() {
                return RenderingDispatcher.CURRENT.toSourceString(this.values);
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class ForTypeDescription<U extends Class<U>>
    extends AbstractBase<TypeDescription, U> {
        private static final boolean NO_INITIALIZATION = false;
        private static final Map<TypeDescription, Class<?>> PRIMITIVE_TYPES = new HashMap();
        private final TypeDescription typeDescription;

        public ForTypeDescription(TypeDescription typeDescription) {
            this.typeDescription = typeDescription;
        }

        public static <V extends Class<V>> AnnotationValue<TypeDescription, V> of(TypeDescription typeDescription) {
            return new ForTypeDescription(typeDescription);
        }

        @Override
        public State getState() {
            return State.RESOLVED;
        }

        @Override
        public Sort getSort() {
            return Sort.TYPE;
        }

        @Override
        public AnnotationValue<TypeDescription, U> filter(MethodDescription.InDefinedShape property, TypeDefinition typeDefinition) {
            return typeDefinition.asErasure().represents((Type)((Object)Class.class)) ? this : new ForMismatchedType(property, property.getReturnType().isArray() ? RenderingDispatcher.CURRENT.toArrayErrorString(Sort.TYPE) : Class.class.getName() + '[' + this.typeDescription.getName() + ']');
        }

        @Override
        public TypeDescription resolve() {
            return this.typeDescription;
        }

        @Override
        public net.bytebuddy.description.annotation.AnnotationValue$Loaded<U> load(@MaybeNull ClassLoader classLoader) {
            try {
                return new Loaded(this.typeDescription.isPrimitive() ? PRIMITIVE_TYPES.get(this.typeDescription) : Class.forName(this.typeDescription.getName(), false, classLoader));
            }
            catch (ClassNotFoundException exception) {
                return new ForMissingType.Loaded(this.typeDescription.getName(), exception);
            }
        }

        public int hashCode() {
            return this.typeDescription.hashCode();
        }

        public boolean equals(@MaybeNull Object other) {
            return this == other || other instanceof AnnotationValue && this.typeDescription.equals(((AnnotationValue)other).resolve());
        }

        public String toString() {
            return RenderingDispatcher.CURRENT.toSourceString(this.typeDescription);
        }

        static {
            for (Class type : new Class[]{Boolean.TYPE, Byte.TYPE, Short.TYPE, Character.TYPE, Integer.TYPE, Long.TYPE, Float.TYPE, Double.TYPE, Void.TYPE}) {
                PRIMITIVE_TYPES.put(TypeDescription.ForLoadedType.of(type), type);
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        protected static class Loaded<U extends Class<U>>
        extends Loaded.AbstractBase<U> {
            private final U type;

            public Loaded(U type) {
                this.type = type;
            }

            @Override
            public State getState() {
                return State.RESOLVED;
            }

            @Override
            public U resolve() {
                return this.type;
            }

            @Override
            public boolean represents(Object value) {
                return this.type.equals(value);
            }

            public int hashCode() {
                return this.type.hashCode();
            }

            public boolean equals(@MaybeNull Object other) {
                if (this == other) {
                    return true;
                }
                if (!(other instanceof net.bytebuddy.description.annotation.AnnotationValue$Loaded)) {
                    return false;
                }
                net.bytebuddy.description.annotation.AnnotationValue$Loaded annotationValue = (net.bytebuddy.description.annotation.AnnotationValue$Loaded)other;
                return annotationValue.getState().isResolved() && this.type.equals(annotationValue.resolve());
            }

            public String toString() {
                return RenderingDispatcher.CURRENT.toSourceString(TypeDescription.ForLoadedType.of(this.type));
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class ForEnumerationDescription<U extends Enum<U>>
    extends AbstractBase<EnumerationDescription, U> {
        private final EnumerationDescription enumerationDescription;

        public ForEnumerationDescription(EnumerationDescription enumerationDescription) {
            this.enumerationDescription = enumerationDescription;
        }

        public static <V extends Enum<V>> AnnotationValue<EnumerationDescription, V> of(EnumerationDescription value) {
            return new ForEnumerationDescription(value);
        }

        @Override
        public EnumerationDescription resolve() {
            return this.enumerationDescription;
        }

        @Override
        public State getState() {
            return State.RESOLVED;
        }

        @Override
        public Sort getSort() {
            return Sort.ENUMERATION;
        }

        @Override
        public AnnotationValue<EnumerationDescription, U> filter(MethodDescription.InDefinedShape property, TypeDefinition typeDefinition) {
            return typeDefinition.asErasure().equals(this.enumerationDescription.getEnumerationType()) ? this : new ForMismatchedType(property, property.getReturnType().isArray() ? RenderingDispatcher.CURRENT.toArrayErrorString(Sort.ENUMERATION) : this.enumerationDescription.getEnumerationType().getName() + '.' + this.enumerationDescription.getValue());
        }

        @Override
        public net.bytebuddy.description.annotation.AnnotationValue$Loaded<U> load(@MaybeNull ClassLoader classLoader) {
            try {
                return new Loaded(this.enumerationDescription.load(Class.forName(this.enumerationDescription.getEnumerationType().getName(), false, classLoader)));
            }
            catch (ClassNotFoundException exception) {
                return new ForMissingType.Loaded(this.enumerationDescription.getEnumerationType().getName(), exception);
            }
        }

        public int hashCode() {
            return this.enumerationDescription.hashCode();
        }

        public boolean equals(@MaybeNull Object other) {
            return this == other || other instanceof AnnotationValue && this.enumerationDescription.equals(((AnnotationValue)other).resolve());
        }

        public String toString() {
            return this.enumerationDescription.toString();
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static class WithUnknownConstant<U extends Enum<U>>
        extends AbstractBase<EnumerationDescription, U> {
            private final TypeDescription typeDescription;
            private final String value;

            public WithUnknownConstant(TypeDescription typeDescription, String value) {
                this.typeDescription = typeDescription;
                this.value = value;
            }

            @Override
            public State getState() {
                return State.UNRESOLVED;
            }

            @Override
            public Sort getSort() {
                return Sort.NONE;
            }

            @Override
            public AnnotationValue<EnumerationDescription, U> filter(MethodDescription.InDefinedShape property, TypeDefinition typeDefinition) {
                return this;
            }

            @Override
            public EnumerationDescription resolve() {
                throw new IllegalStateException(this.typeDescription + " does not declare enumeration constant " + this.value);
            }

            @Override
            public net.bytebuddy.description.annotation.AnnotationValue$Loaded<U> load(@MaybeNull ClassLoader classLoader) {
                try {
                    return new Loaded(Class.forName(this.typeDescription.getName(), false, classLoader), this.value);
                }
                catch (ClassNotFoundException exception) {
                    return new ForMissingType.Loaded(this.typeDescription.getName(), exception);
                }
            }

            public String toString() {
                return this.value + " /* Warning: constant not present! */";
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            public static class Loaded
            extends Loaded.AbstractBase.ForUnresolvedProperty<Enum<?>> {
                private final Class<? extends Enum<?>> enumType;
                private final String value;

                public Loaded(Class<? extends Enum<?>> enumType, String value) {
                    this.enumType = enumType;
                    this.value = value;
                }

                @Override
                public Enum<?> resolve() {
                    throw new EnumConstantNotPresentException(this.enumType, this.value);
                }

                public String toString() {
                    return this.value + " /* Warning: constant not present! */";
                }
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static class Loaded<V extends Enum<V>>
        extends Loaded.AbstractBase<V> {
            private final V enumeration;

            public Loaded(V enumeration) {
                this.enumeration = enumeration;
            }

            @Override
            public State getState() {
                return State.RESOLVED;
            }

            @Override
            public V resolve() {
                return this.enumeration;
            }

            @Override
            public boolean represents(Object value) {
                return ((Enum)this.enumeration).equals(value);
            }

            public int hashCode() {
                return ((Enum)this.enumeration).hashCode();
            }

            public boolean equals(@MaybeNull Object other) {
                if (this == other) {
                    return true;
                }
                if (!(other instanceof net.bytebuddy.description.annotation.AnnotationValue$Loaded)) {
                    return false;
                }
                net.bytebuddy.description.annotation.AnnotationValue$Loaded annotationValue = (net.bytebuddy.description.annotation.AnnotationValue$Loaded)other;
                return annotationValue.getState().isResolved() && ((Enum)this.enumeration).equals(annotationValue.resolve());
            }

            public String toString() {
                return ((Enum)this.enumeration).toString();
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            public static class WithIncompatibleRuntimeType
            extends Loaded.AbstractBase<Enum<?>> {
                private final Class<?> type;

                public WithIncompatibleRuntimeType(Class<?> type) {
                    this.type = type;
                }

                @Override
                public State getState() {
                    return State.UNRESOLVED;
                }

                @Override
                public Enum<?> resolve() {
                    throw new IncompatibleClassChangeError("Not an enumeration type: " + this.type.getName());
                }

                @Override
                public boolean represents(Object value) {
                    return false;
                }
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class ForAnnotationDescription<U extends Annotation>
    extends AbstractBase<AnnotationDescription, U> {
        private final AnnotationDescription annotationDescription;

        public ForAnnotationDescription(AnnotationDescription annotationDescription) {
            this.annotationDescription = annotationDescription;
        }

        public static <V extends Annotation> AnnotationValue<AnnotationDescription, V> of(TypeDescription annotationType, Map<String, ? extends AnnotationValue<?, ?>> annotationValues) {
            return new ForAnnotationDescription(new AnnotationDescription.Latent(annotationType, annotationValues));
        }

        @Override
        public State getState() {
            return State.RESOLVED;
        }

        @Override
        public Sort getSort() {
            return Sort.ANNOTATION;
        }

        @Override
        public AnnotationValue<AnnotationDescription, U> filter(MethodDescription.InDefinedShape property, TypeDefinition typeDefinition) {
            return typeDefinition.asErasure().equals(this.annotationDescription.getAnnotationType()) ? this : new ForMismatchedType(property, property.getReturnType().isArray() ? RenderingDispatcher.CURRENT.toArrayErrorString(Sort.ANNOTATION) : this.annotationDescription.toString());
        }

        @Override
        public AnnotationDescription resolve() {
            return this.annotationDescription;
        }

        @Override
        public net.bytebuddy.description.annotation.AnnotationValue$Loaded<U> load(@MaybeNull ClassLoader classLoader) {
            try {
                return new Loaded(this.annotationDescription.prepare(Class.forName(this.annotationDescription.getAnnotationType().getName(), false, classLoader)).load());
            }
            catch (ClassNotFoundException exception) {
                return new ForMissingType.Loaded(this.annotationDescription.getAnnotationType().getName(), exception);
            }
        }

        public int hashCode() {
            return this.annotationDescription.hashCode();
        }

        public boolean equals(@MaybeNull Object other) {
            return this == other || other instanceof AnnotationValue && this.annotationDescription.equals(((AnnotationValue)other).resolve());
        }

        public String toString() {
            return this.annotationDescription.toString();
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static class Loaded<V extends Annotation>
        extends Loaded.AbstractBase<V> {
            private final V annotation;

            public Loaded(V annotation) {
                this.annotation = annotation;
            }

            @Override
            public State getState() {
                return State.RESOLVED;
            }

            @Override
            public V resolve() {
                return this.annotation;
            }

            @Override
            public boolean represents(Object value) {
                return this.annotation.equals(value);
            }

            public int hashCode() {
                return this.annotation.hashCode();
            }

            public boolean equals(@MaybeNull Object other) {
                if (this == other) {
                    return true;
                }
                if (!(other instanceof net.bytebuddy.description.annotation.AnnotationValue$Loaded)) {
                    return false;
                }
                net.bytebuddy.description.annotation.AnnotationValue$Loaded annotationValue = (net.bytebuddy.description.annotation.AnnotationValue$Loaded)other;
                return annotationValue.getState().isResolved() && this.annotation.equals(annotationValue.resolve());
            }

            public String toString() {
                return this.annotation.toString();
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class ForConstant<U>
    extends AbstractBase<U, U> {
        private final U value;
        private final PropertyDelegate propertyDelegate;
        private transient /* synthetic */ int hashCode;

        protected ForConstant(U value, PropertyDelegate propertyDelegate) {
            this.value = value;
            this.propertyDelegate = propertyDelegate;
        }

        public static AnnotationValue<Boolean, Boolean> of(boolean value) {
            return new ForConstant<Boolean>(value, PropertyDelegate.ForNonArrayType.BOOLEAN);
        }

        public static AnnotationValue<Byte, Byte> of(byte value) {
            return new ForConstant<Byte>(value, PropertyDelegate.ForNonArrayType.BYTE);
        }

        public static AnnotationValue<Short, Short> of(short value) {
            return new ForConstant<Short>(value, PropertyDelegate.ForNonArrayType.SHORT);
        }

        public static AnnotationValue<Character, Character> of(char value) {
            return new ForConstant<Character>(Character.valueOf(value), PropertyDelegate.ForNonArrayType.CHARACTER);
        }

        public static AnnotationValue<Integer, Integer> of(int value) {
            return new ForConstant<Integer>(value, PropertyDelegate.ForNonArrayType.INTEGER);
        }

        public static AnnotationValue<Long, Long> of(long value) {
            return new ForConstant<Long>(value, PropertyDelegate.ForNonArrayType.LONG);
        }

        public static AnnotationValue<Float, Float> of(float value) {
            return new ForConstant<Float>(Float.valueOf(value), PropertyDelegate.ForNonArrayType.FLOAT);
        }

        public static AnnotationValue<Double, Double> of(double value) {
            return new ForConstant<Double>(value, PropertyDelegate.ForNonArrayType.DOUBLE);
        }

        public static AnnotationValue<String, String> of(String value) {
            return new ForConstant<String>(value, PropertyDelegate.ForNonArrayType.STRING);
        }

        public static AnnotationValue<boolean[], boolean[]> of(boolean ... value) {
            return new ForConstant<boolean[]>(value, PropertyDelegate.ForArrayType.BOOLEAN);
        }

        public static AnnotationValue<byte[], byte[]> of(byte ... value) {
            return new ForConstant<byte[]>(value, PropertyDelegate.ForArrayType.BYTE);
        }

        public static AnnotationValue<short[], short[]> of(short ... value) {
            return new ForConstant<short[]>(value, PropertyDelegate.ForArrayType.SHORT);
        }

        public static AnnotationValue<char[], char[]> of(char ... value) {
            return new ForConstant<char[]>(value, PropertyDelegate.ForArrayType.CHARACTER);
        }

        public static AnnotationValue<int[], int[]> of(int ... value) {
            return new ForConstant<int[]>(value, PropertyDelegate.ForArrayType.INTEGER);
        }

        public static AnnotationValue<long[], long[]> of(long ... value) {
            return new ForConstant<long[]>(value, PropertyDelegate.ForArrayType.LONG);
        }

        public static AnnotationValue<float[], float[]> of(float ... value) {
            return new ForConstant<float[]>(value, PropertyDelegate.ForArrayType.FLOAT);
        }

        public static AnnotationValue<double[], double[]> of(double ... value) {
            return new ForConstant<double[]>(value, PropertyDelegate.ForArrayType.DOUBLE);
        }

        public static AnnotationValue<String[], String[]> of(String ... value) {
            return new ForConstant<String[]>(value, PropertyDelegate.ForArrayType.STRING);
        }

        public static AnnotationValue<?, ?> of(Object value) {
            if (value instanceof Boolean) {
                return ForConstant.of((boolean)((Boolean)value));
            }
            if (value instanceof Byte) {
                return ForConstant.of((byte)((Byte)value));
            }
            if (value instanceof Short) {
                return ForConstant.of((short)((Short)value));
            }
            if (value instanceof Character) {
                return ForConstant.of(((Character)value).charValue());
            }
            if (value instanceof Integer) {
                return ForConstant.of((int)((Integer)value));
            }
            if (value instanceof Long) {
                return ForConstant.of((long)((Long)value));
            }
            if (value instanceof Float) {
                return ForConstant.of(((Float)value).floatValue());
            }
            if (value instanceof Double) {
                return ForConstant.of((double)((Double)value));
            }
            if (value instanceof String) {
                return ForConstant.of((String)value);
            }
            if (value instanceof boolean[]) {
                return ForConstant.of((boolean[])value);
            }
            if (value instanceof byte[]) {
                return ForConstant.of((byte[])value);
            }
            if (value instanceof short[]) {
                return ForConstant.of((short[])value);
            }
            if (value instanceof char[]) {
                return ForConstant.of((char[])value);
            }
            if (value instanceof int[]) {
                return ForConstant.of((int[])value);
            }
            if (value instanceof long[]) {
                return ForConstant.of((long[])value);
            }
            if (value instanceof float[]) {
                return ForConstant.of((float[])value);
            }
            if (value instanceof double[]) {
                return ForConstant.of((double[])value);
            }
            if (value instanceof String[]) {
                return ForConstant.of((String[])value);
            }
            throw new IllegalArgumentException("Not a constant annotation value: " + value);
        }

        @Override
        public State getState() {
            return State.RESOLVED;
        }

        @Override
        public Sort getSort() {
            return Sort.of(TypeDescription.ForLoadedType.of(this.value.getClass()).asUnboxed());
        }

        @Override
        public AnnotationValue<U, U> filter(MethodDescription.InDefinedShape property, TypeDefinition typeDefinition) {
            if (typeDefinition.asErasure().asBoxed().represents(this.value.getClass())) {
                return this;
            }
            if (this.value.getClass().isArray()) {
                return new ForMismatchedType(property, RenderingDispatcher.CURRENT.toArrayErrorString(Sort.of(TypeDescription.ForLoadedType.of(this.value.getClass().getComponentType()))));
            }
            if (this.value instanceof Enum) {
                return new ForMismatchedType(property, this.value.getClass().getName() + '.' + ((Enum)this.value).name());
            }
            return new ForMismatchedType(property, RenderingDispatcher.CURRENT.toTypeErrorString(this.value.getClass()) + '[' + this.value + ']');
        }

        @Override
        public U resolve() {
            return this.value;
        }

        @Override
        public net.bytebuddy.description.annotation.AnnotationValue$Loaded<U> load(@MaybeNull ClassLoader classLoader) {
            return new Loaded<U>(this.value, this.propertyDelegate);
        }

        @CachedReturnPlugin.Enhance(value="hashCode")
        public int hashCode() {
            int n;
            int n2;
            int n3 = this.hashCode;
            if (n3 != 0) {
                n2 = 0;
            } else {
                ForConstant forConstant = this;
                n2 = n = forConstant.propertyDelegate.hashCode(forConstant.value);
            }
            if (n == 0) {
                n = this.hashCode;
            } else {
                this.hashCode = n;
            }
            return n;
        }

        public boolean equals(@MaybeNull Object other) {
            return this == other || other instanceof AnnotationValue && this.propertyDelegate.equals(this.value, ((AnnotationValue)other).resolve());
        }

        public String toString() {
            return this.propertyDelegate.toString(this.value);
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        protected static class Loaded<V>
        extends Loaded.AbstractBase<V> {
            private final V value;
            private final PropertyDelegate propertyDelegate;
            private transient /* synthetic */ int hashCode;

            protected Loaded(V value, PropertyDelegate propertyDelegate) {
                this.value = value;
                this.propertyDelegate = propertyDelegate;
            }

            @Override
            public State getState() {
                return State.RESOLVED;
            }

            @Override
            public V resolve() {
                return this.propertyDelegate.copy(this.value);
            }

            @Override
            public boolean represents(Object value) {
                return this.propertyDelegate.equals(this.value, value);
            }

            @CachedReturnPlugin.Enhance(value="hashCode")
            public int hashCode() {
                int n;
                int n2;
                int n3 = this.hashCode;
                if (n3 != 0) {
                    n2 = 0;
                } else {
                    Loaded loaded = this;
                    n2 = n = loaded.propertyDelegate.hashCode(loaded.value);
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
                if (!(other instanceof net.bytebuddy.description.annotation.AnnotationValue$Loaded)) {
                    return false;
                }
                net.bytebuddy.description.annotation.AnnotationValue$Loaded annotationValue = (net.bytebuddy.description.annotation.AnnotationValue$Loaded)other;
                return annotationValue.getState().isResolved() && this.propertyDelegate.equals(this.value, annotationValue.resolve());
            }

            public String toString() {
                return this.propertyDelegate.toString(this.value);
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        protected static interface PropertyDelegate {
            public <S> S copy(S var1);

            public int hashCode(Object var1);

            public boolean equals(Object var1, Object var2);

            public String toString(Object var1);

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            public static enum ForArrayType implements PropertyDelegate
            {
                BOOLEAN{

                    protected Object doCopy(Object value) {
                        return ((boolean[])value).clone();
                    }

                    public int hashCode(Object value) {
                        return Arrays.hashCode((boolean[])value);
                    }

                    public boolean equals(Object self, Object other) {
                        return other instanceof boolean[] && Arrays.equals((boolean[])self, (boolean[])other);
                    }

                    protected String toString(Object array, int index) {
                        return ForNonArrayType.BOOLEAN.toString(Array.getBoolean(array, index));
                    }
                }
                ,
                BYTE{

                    protected Object doCopy(Object value) {
                        return ((byte[])value).clone();
                    }

                    public int hashCode(Object value) {
                        return Arrays.hashCode((byte[])value);
                    }

                    public boolean equals(Object self, Object other) {
                        return other instanceof byte[] && Arrays.equals((byte[])self, (byte[])other);
                    }

                    protected String toString(Object array, int index) {
                        return ForNonArrayType.BYTE.toString(Array.getByte(array, index));
                    }
                }
                ,
                SHORT{

                    protected Object doCopy(Object value) {
                        return ((short[])value).clone();
                    }

                    public int hashCode(Object value) {
                        return Arrays.hashCode((short[])value);
                    }

                    public boolean equals(Object self, Object other) {
                        return other instanceof short[] && Arrays.equals((short[])self, (short[])other);
                    }

                    protected String toString(Object array, int index) {
                        return ForNonArrayType.SHORT.toString(Array.getShort(array, index));
                    }
                }
                ,
                CHARACTER{

                    protected Object doCopy(Object value) {
                        return ((char[])value).clone();
                    }

                    public int hashCode(Object value) {
                        return Arrays.hashCode((char[])value);
                    }

                    public boolean equals(Object self, Object other) {
                        return other instanceof char[] && Arrays.equals((char[])self, (char[])other);
                    }

                    protected String toString(Object array, int index) {
                        return ForNonArrayType.CHARACTER.toString(Character.valueOf(Array.getChar(array, index)));
                    }
                }
                ,
                INTEGER{

                    protected Object doCopy(Object value) {
                        return ((int[])value).clone();
                    }

                    public int hashCode(Object value) {
                        return Arrays.hashCode((int[])value);
                    }

                    public boolean equals(Object self, Object other) {
                        return other instanceof int[] && Arrays.equals((int[])self, (int[])other);
                    }

                    protected String toString(Object array, int index) {
                        return ForNonArrayType.INTEGER.toString(Array.getInt(array, index));
                    }
                }
                ,
                LONG{

                    protected Object doCopy(Object value) {
                        return ((long[])value).clone();
                    }

                    public int hashCode(Object value) {
                        return Arrays.hashCode((long[])value);
                    }

                    public boolean equals(Object self, Object other) {
                        return other instanceof long[] && Arrays.equals((long[])self, (long[])other);
                    }

                    protected String toString(Object array, int index) {
                        return ForNonArrayType.LONG.toString(Array.getLong(array, index));
                    }
                }
                ,
                FLOAT{

                    protected Object doCopy(Object value) {
                        return ((float[])value).clone();
                    }

                    public int hashCode(Object value) {
                        return Arrays.hashCode((float[])value);
                    }

                    public boolean equals(Object self, Object other) {
                        return other instanceof float[] && Arrays.equals((float[])self, (float[])other);
                    }

                    protected String toString(Object array, int index) {
                        return ForNonArrayType.FLOAT.toString(Float.valueOf(Array.getFloat(array, index)));
                    }
                }
                ,
                DOUBLE{

                    protected Object doCopy(Object value) {
                        return ((double[])value).clone();
                    }

                    public int hashCode(Object value) {
                        return Arrays.hashCode((double[])value);
                    }

                    public boolean equals(Object self, Object other) {
                        return other instanceof double[] && Arrays.equals((double[])self, (double[])other);
                    }

                    protected String toString(Object array, int index) {
                        return ForNonArrayType.DOUBLE.toString(Array.getDouble(array, index));
                    }
                }
                ,
                STRING{

                    protected Object doCopy(Object value) {
                        return ((String[])value).clone();
                    }

                    public int hashCode(Object value) {
                        return Arrays.hashCode((String[])value);
                    }

                    public boolean equals(Object self, Object other) {
                        return other instanceof String[] && Arrays.equals((String[])self, (String[])other);
                    }

                    protected String toString(Object array, int index) {
                        return ForNonArrayType.STRING.toString(Array.get(array, index));
                    }
                };


                @Override
                public <S> S copy(S value) {
                    return (S)this.doCopy(value);
                }

                protected abstract Object doCopy(Object var1);

                @Override
                public String toString(Object value) {
                    ArrayList<String> elements = new ArrayList<String>(Array.getLength(value));
                    for (int index = 0; index < Array.getLength(value); ++index) {
                        elements.add(this.toString(value, index));
                    }
                    return RenderingDispatcher.CURRENT.toSourceString(elements);
                }

                protected abstract String toString(Object var1, int var2);
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            public static enum ForNonArrayType implements PropertyDelegate
            {
                BOOLEAN{

                    public String toString(Object value) {
                        return RenderingDispatcher.CURRENT.toSourceString((Boolean)value);
                    }
                }
                ,
                BYTE{

                    public String toString(Object value) {
                        return RenderingDispatcher.CURRENT.toSourceString((Byte)value);
                    }
                }
                ,
                SHORT{

                    public String toString(Object value) {
                        return RenderingDispatcher.CURRENT.toSourceString((Short)value);
                    }
                }
                ,
                CHARACTER{

                    public String toString(Object value) {
                        return RenderingDispatcher.CURRENT.toSourceString(((Character)value).charValue());
                    }
                }
                ,
                INTEGER{

                    public String toString(Object value) {
                        return RenderingDispatcher.CURRENT.toSourceString((Integer)value);
                    }
                }
                ,
                LONG{

                    public String toString(Object value) {
                        return RenderingDispatcher.CURRENT.toSourceString((Long)value);
                    }
                }
                ,
                FLOAT{

                    public String toString(Object value) {
                        return RenderingDispatcher.CURRENT.toSourceString(((Float)value).floatValue());
                    }
                }
                ,
                DOUBLE{

                    public String toString(Object value) {
                        return RenderingDispatcher.CURRENT.toSourceString((Double)value);
                    }
                }
                ,
                STRING{

                    public String toString(Object value) {
                        return RenderingDispatcher.CURRENT.toSourceString((String)value);
                    }
                };


                @Override
                public <S> S copy(S value) {
                    return value;
                }

                @Override
                public int hashCode(Object value) {
                    return value.hashCode();
                }

                @Override
                public boolean equals(Object self, Object other) {
                    return self.equals(other);
                }
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static abstract class AbstractBase<U, V>
    implements AnnotationValue<U, V> {
        @Override
        public <W> W resolve(Class<? extends W> type) {
            return type.cast(this.resolve());
        }

        @Override
        public AnnotationValue<U, V> filter(MethodDescription.InDefinedShape property) {
            return this.filter(property, property.getReturnType());
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum Sort {
        BOOLEAN(90),
        BYTE(66),
        SHORT(83),
        CHARACTER(67),
        INTEGER(73),
        LONG(74),
        FLOAT(70),
        DOUBLE(68),
        STRING(115),
        TYPE(99),
        ENUMERATION(101),
        ANNOTATION(64),
        ARRAY(91),
        NONE(0);

        private final int tag;

        private Sort(int tag) {
            this.tag = tag;
        }

        public static Sort of(TypeDefinition typeDefinition) {
            if (typeDefinition.represents(Boolean.TYPE)) {
                return BOOLEAN;
            }
            if (typeDefinition.represents(Byte.TYPE)) {
                return BYTE;
            }
            if (typeDefinition.represents(Short.TYPE)) {
                return SHORT;
            }
            if (typeDefinition.represents(Character.TYPE)) {
                return CHARACTER;
            }
            if (typeDefinition.represents(Integer.TYPE)) {
                return INTEGER;
            }
            if (typeDefinition.represents(Long.TYPE)) {
                return LONG;
            }
            if (typeDefinition.represents(Float.TYPE)) {
                return FLOAT;
            }
            if (typeDefinition.represents(Double.TYPE)) {
                return DOUBLE;
            }
            if (typeDefinition.represents((Type)((Object)String.class))) {
                return STRING;
            }
            if (typeDefinition.represents((Type)((Object)Class.class))) {
                return TYPE;
            }
            if (typeDefinition.isEnum()) {
                return ENUMERATION;
            }
            if (typeDefinition.isAnnotation()) {
                return ANNOTATION;
            }
            if (typeDefinition.isArray()) {
                return ARRAY;
            }
            return NONE;
        }

        protected int getTag() {
            return this.tag;
        }

        public boolean isDefined() {
            return this != NONE;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum State {
        UNDEFINED,
        UNRESOLVED,
        RESOLVED;


        public boolean isDefined() {
            return this != UNDEFINED;
        }

        public boolean isResolved() {
            return this == RESOLVED;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static interface Loaded<U> {
        public State getState();

        public U resolve();

        public <V> V resolve(Class<? extends V> var1);

        public boolean represents(Object var1);

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static abstract class AbstractBase<W>
        implements Loaded<W> {
            @Override
            public <X> X resolve(Class<? extends X> type) {
                return type.cast(this.resolve());
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            public static abstract class ForUnresolvedProperty<Z>
            extends AbstractBase<Z> {
                @Override
                public State getState() {
                    return State.UNRESOLVED;
                }

                @Override
                public boolean represents(Object value) {
                    return false;
                }
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum RenderingDispatcher {
        LEGACY_VM('[', ']', true){

            public String toSourceString(char value) {
                return Character.toString(value);
            }

            public String toSourceString(long value) {
                return Long.toString(value);
            }

            public String toSourceString(float value) {
                return Float.toString(value);
            }

            public String toSourceString(double value) {
                return Double.toString(value);
            }

            public String toSourceString(String value) {
                return value;
            }

            public String toSourceString(TypeDescription value) {
                return value.toString();
            }
        }
        ,
        JAVA_9_CAPABLE_VM('{', '}', true){

            public String toSourceString(char value) {
                StringBuilder stringBuilder = new StringBuilder().append('\'');
                if (value == '\'') {
                    stringBuilder.append("\\'");
                } else {
                    stringBuilder.append(value);
                }
                return stringBuilder.append('\'').toString();
            }

            public String toSourceString(long value) {
                return Math.abs(value) <= Integer.MAX_VALUE ? String.valueOf(value) : value + "L";
            }

            public String toSourceString(float value) {
                return Math.abs(value) <= Float.MAX_VALUE ? value + "f" : (Float.isInfinite(value) ? (value < 0.0f ? "-1.0f/0.0f" : "1.0f/0.0f") : "0.0f/0.0f");
            }

            public String toSourceString(double value) {
                return Math.abs(value) <= Double.MAX_VALUE ? Double.toString(value) : (Double.isInfinite(value) ? (value < 0.0 ? "-1.0/0.0" : "1.0/0.0") : "0.0/0.0");
            }

            public String toSourceString(String value) {
                return "\"" + (value.indexOf(34) == -1 ? value : value.replace("\"", "\\\"")) + "\"";
            }

            public String toSourceString(TypeDescription value) {
                return value.getActualName() + ".class";
            }
        }
        ,
        JAVA_14_CAPABLE_VM('{', '}', true){

            public String toSourceString(byte value) {
                return "(byte)0x" + Integer.toHexString(value & 0xFF);
            }

            public String toSourceString(char value) {
                StringBuilder stringBuilder = new StringBuilder().append('\'');
                if (value == '\'') {
                    stringBuilder.append("\\'");
                } else {
                    stringBuilder.append(value);
                }
                return stringBuilder.append('\'').toString();
            }

            public String toSourceString(long value) {
                return value + "L";
            }

            public String toSourceString(float value) {
                return Math.abs(value) <= Float.MAX_VALUE ? value + "f" : (Float.isInfinite(value) ? (value < 0.0f ? "-1.0f/0.0f" : "1.0f/0.0f") : "0.0f/0.0f");
            }

            public String toSourceString(double value) {
                return Math.abs(value) <= Double.MAX_VALUE ? Double.toString(value) : (Double.isInfinite(value) ? (value < 0.0 ? "-1.0/0.0" : "1.0/0.0") : "0.0/0.0");
            }

            public String toSourceString(String value) {
                return "\"" + (value.indexOf(34) == -1 ? value : value.replace("\"", "\\\"")) + "\"";
            }

            public String toSourceString(TypeDescription value) {
                return value.getActualName() + ".class";
            }
        }
        ,
        JAVA_17_CAPABLE_VM('{', '}', false){

            @Override
            public String toSourceString(byte value) {
                return "(byte)0x" + Integer.toHexString(value & 0xFF);
            }

            @Override
            public String toSourceString(char value) {
                StringBuilder stringBuilder = new StringBuilder().append('\'');
                if (value == '\'') {
                    stringBuilder.append("\\'");
                } else {
                    stringBuilder.append(value);
                }
                return stringBuilder.append('\'').toString();
            }

            @Override
            public String toSourceString(long value) {
                return value + "L";
            }

            @Override
            public String toSourceString(float value) {
                return Math.abs(value) <= Float.MAX_VALUE ? value + "f" : (Float.isInfinite(value) ? (value < 0.0f ? "-1.0f/0.0f" : "1.0f/0.0f") : "0.0f/0.0f");
            }

            @Override
            public String toSourceString(double value) {
                return Math.abs(value) <= Double.MAX_VALUE ? Double.toString(value) : (Double.isInfinite(value) ? (value < 0.0 ? "-1.0/0.0" : "1.0/0.0") : "0.0/0.0");
            }

            @Override
            public String toSourceString(String value) {
                return "\"" + (value.indexOf(34) == -1 ? value : value.replace("\"", "\\\"")) + "\"";
            }

            @Override
            public String toSourceString(TypeDescription value) {
                return value.getActualName() + ".class";
            }

            @Override
            public String toTypeErrorString(Class<?> type) {
                return type.getName();
            }
        }
        ,
        JAVA_19_CAPABLE_VM('{', '}', ClassFileVersion.ofThisVm(ClassFileVersion.JAVA_V5).isLessThan(ClassFileVersion.JAVA_V17)){

            @Override
            public String toSourceString(byte value) {
                return "(byte)0x" + Integer.toHexString(value & 0xFF);
            }

            @Override
            public String toSourceString(char value) {
                StringBuilder stringBuilder = new StringBuilder().append('\'');
                if (value == '\'') {
                    stringBuilder.append("\\'");
                } else {
                    stringBuilder.append(value);
                }
                return stringBuilder.append('\'').toString();
            }

            @Override
            public String toSourceString(long value) {
                return value + "L";
            }

            @Override
            public String toSourceString(float value) {
                return Math.abs(value) <= Float.MAX_VALUE ? value + "f" : (Float.isInfinite(value) ? (value < 0.0f ? "-1.0f/0.0f" : "1.0f/0.0f") : "0.0f/0.0f");
            }

            @Override
            public String toSourceString(double value) {
                return Math.abs(value) <= Double.MAX_VALUE ? Double.toString(value) : (Double.isInfinite(value) ? (value < 0.0 ? "-1.0/0.0" : "1.0/0.0") : "0.0/0.0");
            }

            @Override
            public String toSourceString(String value) {
                return "\"" + (value.indexOf(34) == -1 ? value : value.replace("\"", "\\\"")) + "\"";
            }

            @Override
            public String toSourceString(TypeDescription value) {
                return value.getCanonicalName() + ".class";
            }

            @Override
            public String toTypeErrorString(Class<?> type) {
                return type.getName();
            }
        };

        private static final String ARRAY_PREFIX = "Array with component tag: ";
        public static final RenderingDispatcher CURRENT;
        private final char openingBrace;
        private final char closingBrace;
        private final boolean componentAsInteger;

        private RenderingDispatcher(char openingBrace, char closingBrace, boolean componentAsInteger) {
            this.openingBrace = openingBrace;
            this.closingBrace = closingBrace;
            this.componentAsInteger = componentAsInteger;
        }

        public String toSourceString(boolean value) {
            return Boolean.toString(value);
        }

        public String toSourceString(byte value) {
            return Byte.toString(value);
        }

        public String toSourceString(short value) {
            return Short.toString(value);
        }

        public abstract String toSourceString(char var1);

        public String toSourceString(int value) {
            return Integer.toString(value);
        }

        public abstract String toSourceString(long var1);

        public abstract String toSourceString(float var1);

        public abstract String toSourceString(double var1);

        public abstract String toSourceString(String var1);

        public abstract String toSourceString(TypeDescription var1);

        public String toSourceString(List<?> values) {
            StringBuilder stringBuilder = new StringBuilder().append(this.openingBrace);
            boolean first = true;
            for (Object value : values) {
                if (first) {
                    first = false;
                } else {
                    stringBuilder.append(", ");
                }
                stringBuilder.append(value);
            }
            return stringBuilder.append(this.closingBrace).toString();
        }

        public String toArrayErrorString(Sort sort) {
            return ARRAY_PREFIX + (this.componentAsInteger || !sort.isDefined() ? Integer.toString(sort.getTag()) : Character.toString((char)sort.getTag()));
        }

        public String toTypeErrorString(Class<?> type) {
            return type.toString();
        }

        static {
            ClassFileVersion classFileVersion = ClassFileVersion.ofThisVm(ClassFileVersion.JAVA_V5);
            CURRENT = classFileVersion.isAtLeast(ClassFileVersion.JAVA_V19) ? JAVA_19_CAPABLE_VM : (classFileVersion.isAtLeast(ClassFileVersion.JAVA_V17) ? JAVA_17_CAPABLE_VM : (classFileVersion.isAtLeast(ClassFileVersion.JAVA_V14) ? JAVA_14_CAPABLE_VM : (classFileVersion.isAtLeast(ClassFileVersion.JAVA_V9) ? JAVA_9_CAPABLE_VM : LEGACY_VM)));
        }
    }
}

