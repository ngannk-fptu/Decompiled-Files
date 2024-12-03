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
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.IncompleteAnnotationException;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import net.bytebuddy.ClassFileVersion;
import net.bytebuddy.build.AccessControllerPlugin;
import net.bytebuddy.build.CachedReturnPlugin;
import net.bytebuddy.build.HashCodeAndEqualsPlugin;
import net.bytebuddy.description.annotation.AnnotationList;
import net.bytebuddy.description.annotation.AnnotationValue;
import net.bytebuddy.description.enumeration.EnumerationDescription;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.MethodList;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.description.type.TypeList;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.nullability.AlwaysNull;
import net.bytebuddy.utility.nullability.MaybeNull;
import net.bytebuddy.utility.privilege.SetAccessibleAction;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface AnnotationDescription {
    @AlwaysNull
    public static final Loadable<?> UNDEFINED = null;

    public AnnotationValue<?, ?> getValue(String var1);

    public AnnotationValue<?, ?> getValue(MethodDescription.InDefinedShape var1);

    public TypeDescription getAnnotationType();

    public <T extends Annotation> Loadable<T> prepare(Class<T> var1);

    public RetentionPolicy getRetention();

    public Set<ElementType> getElementTypes();

    public boolean isSupportedOn(ElementType var1);

    public boolean isSupportedOn(String var1);

    public boolean isInherited();

    public boolean isDocumented();

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @HashCodeAndEqualsPlugin.Enhance
    public static class Builder {
        private final TypeDescription annotationType;
        private final Map<String, AnnotationValue<?, ?>> annotationValues;

        protected Builder(TypeDescription annotationType, Map<String, AnnotationValue<?, ?>> annotationValues) {
            this.annotationType = annotationType;
            this.annotationValues = annotationValues;
        }

        public static Builder ofType(Class<? extends Annotation> annotationType) {
            return Builder.ofType(TypeDescription.ForLoadedType.of(annotationType));
        }

        public static Builder ofType(TypeDescription annotationType) {
            if (!annotationType.isAnnotation()) {
                throw new IllegalArgumentException("Not an annotation type: " + annotationType);
            }
            return new Builder(annotationType, Collections.<String, AnnotationValue<?, ?>>emptyMap());
        }

        public Builder define(String property, AnnotationValue<?, ?> value) {
            MethodList methodDescriptions = (MethodList)this.annotationType.getDeclaredMethods().filter(ElementMatchers.named(property));
            if (methodDescriptions.isEmpty()) {
                throw new IllegalArgumentException(this.annotationType + " does not define a property named " + property);
            }
            HashMap annotationValues = new HashMap(this.annotationValues);
            if (annotationValues.put(((MethodDescription.InDefinedShape)methodDescriptions.getOnly()).getName(), value) != null) {
                throw new IllegalArgumentException("Property already defined: " + property);
            }
            return new Builder(this.annotationType, annotationValues);
        }

        public Builder define(String property, Enum<?> value) {
            return this.define(property, new EnumerationDescription.ForLoadedEnumeration(value));
        }

        public Builder define(String property, TypeDescription enumerationType, String value) {
            return this.define(property, new EnumerationDescription.Latent(enumerationType, value));
        }

        public Builder define(String property, EnumerationDescription value) {
            return this.define(property, AnnotationValue.ForEnumerationDescription.of(value));
        }

        public Builder define(String property, Annotation annotation) {
            return this.define(property, new ForLoadedAnnotation<Annotation>(annotation));
        }

        public Builder define(String property, AnnotationDescription annotationDescription) {
            return this.define(property, new AnnotationValue.ForAnnotationDescription(annotationDescription));
        }

        public Builder define(String property, Class<?> type) {
            return this.define(property, TypeDescription.ForLoadedType.of(type));
        }

        public Builder define(String property, TypeDescription typeDescription) {
            return this.define(property, AnnotationValue.ForTypeDescription.of(typeDescription));
        }

        public <T extends Enum<?>> Builder defineEnumerationArray(String property, Class<T> enumerationType, T ... value) {
            EnumerationDescription[] enumerationDescription = new EnumerationDescription[value.length];
            int index = 0;
            for (T aValue : value) {
                enumerationDescription[index++] = new EnumerationDescription.ForLoadedEnumeration((Enum<?>)aValue);
            }
            return this.defineEnumerationArray(property, TypeDescription.ForLoadedType.of(enumerationType), enumerationDescription);
        }

        public Builder defineEnumerationArray(String property, TypeDescription enumerationType, String ... value) {
            if (!enumerationType.isEnum()) {
                throw new IllegalArgumentException("Not an enumeration type: " + enumerationType);
            }
            EnumerationDescription[] enumerationDescription = new EnumerationDescription[value.length];
            for (int i = 0; i < value.length; ++i) {
                enumerationDescription[i] = new EnumerationDescription.Latent(enumerationType, value[i]);
            }
            return this.defineEnumerationArray(property, enumerationType, enumerationDescription);
        }

        public Builder defineEnumerationArray(String property, TypeDescription enumerationType, EnumerationDescription ... value) {
            return this.define(property, AnnotationValue.ForDescriptionArray.of(enumerationType, value));
        }

        public <T extends Annotation> Builder defineAnnotationArray(String property, Class<T> annotationType, T ... annotation) {
            return this.defineAnnotationArray(property, TypeDescription.ForLoadedType.of(annotationType), new AnnotationList.ForLoadedAnnotations((Annotation[])annotation).toArray(new AnnotationDescription[0]));
        }

        public Builder defineAnnotationArray(String property, TypeDescription annotationType, AnnotationDescription ... annotationDescription) {
            return this.define(property, AnnotationValue.ForDescriptionArray.of(annotationType, annotationDescription));
        }

        public Builder defineTypeArray(String property, Class<?> ... type) {
            return this.defineTypeArray(property, new TypeList.ForLoadedTypes(type).toArray(new TypeDescription[0]));
        }

        public Builder defineTypeArray(String property, TypeDescription ... typeDescription) {
            return this.define(property, AnnotationValue.ForDescriptionArray.of(typeDescription));
        }

        public Builder define(String property, boolean value) {
            return this.define(property, AnnotationValue.ForConstant.of(value));
        }

        public Builder define(String property, byte value) {
            return this.define(property, AnnotationValue.ForConstant.of(value));
        }

        public Builder define(String property, char value) {
            return this.define(property, AnnotationValue.ForConstant.of(value));
        }

        public Builder define(String property, short value) {
            return this.define(property, AnnotationValue.ForConstant.of(value));
        }

        public Builder define(String property, int value) {
            return this.define(property, AnnotationValue.ForConstant.of(value));
        }

        public Builder define(String property, long value) {
            return this.define(property, AnnotationValue.ForConstant.of(value));
        }

        public Builder define(String property, float value) {
            return this.define(property, AnnotationValue.ForConstant.of(value));
        }

        public Builder define(String property, double value) {
            return this.define(property, AnnotationValue.ForConstant.of(value));
        }

        public Builder define(String property, String value) {
            return this.define(property, AnnotationValue.ForConstant.of(value));
        }

        public Builder defineArray(String property, boolean ... value) {
            return this.define(property, AnnotationValue.ForConstant.of(value));
        }

        public Builder defineArray(String property, byte ... value) {
            return this.define(property, AnnotationValue.ForConstant.of(value));
        }

        public Builder defineArray(String property, char ... value) {
            return this.define(property, AnnotationValue.ForConstant.of(value));
        }

        public Builder defineArray(String property, short ... value) {
            return this.define(property, AnnotationValue.ForConstant.of(value));
        }

        public Builder defineArray(String property, int ... value) {
            return this.define(property, AnnotationValue.ForConstant.of(value));
        }

        public Builder defineArray(String property, long ... value) {
            return this.define(property, AnnotationValue.ForConstant.of(value));
        }

        public Builder defineArray(String property, float ... value) {
            return this.define(property, AnnotationValue.ForConstant.of(value));
        }

        public Builder defineArray(String property, double ... value) {
            return this.define(property, AnnotationValue.ForConstant.of(value));
        }

        public Builder defineArray(String property, String ... value) {
            return this.define(property, AnnotationValue.ForConstant.of(value));
        }

        public AnnotationDescription build() {
            for (MethodDescription.InDefinedShape methodDescription : this.annotationType.getDeclaredMethods()) {
                AnnotationValue<?, ?> annotationValue = this.annotationValues.get(methodDescription.getName());
                if (annotationValue == null && methodDescription.getDefaultValue() == null) {
                    throw new IllegalStateException("No value or default value defined for " + methodDescription.getName());
                }
                if (annotationValue == null || annotationValue.filter(methodDescription).getState() == AnnotationValue.State.RESOLVED) continue;
                throw new IllegalStateException("Illegal annotation value for " + methodDescription + ": " + annotationValue);
            }
            return new Latent(this.annotationType, this.annotationValues);
        }

        public AnnotationDescription build(boolean validated) {
            return validated ? this.build() : new Latent(this.annotationType, this.annotationValues);
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
            if (!this.annotationType.equals(((Builder)object).annotationType)) {
                return false;
            }
            return ((Object)this.annotationValues).equals(((Builder)object).annotationValues);
        }

        public int hashCode() {
            return (this.getClass().hashCode() * 31 + this.annotationType.hashCode()) * 31 + ((Object)this.annotationValues).hashCode();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class Latent
    extends AbstractBase {
        private final TypeDescription annotationType;
        private final Map<String, ? extends AnnotationValue<?, ?>> annotationValues;

        protected Latent(TypeDescription annotationType, Map<String, ? extends AnnotationValue<?, ?>> annotationValues) {
            this.annotationType = annotationType;
            this.annotationValues = annotationValues;
        }

        @Override
        public AnnotationValue<?, ?> getValue(MethodDescription.InDefinedShape property) {
            if (!property.getDeclaringType().equals(this.annotationType)) {
                throw new IllegalArgumentException("Not a property of " + this.annotationType + ": " + property);
            }
            AnnotationValue<?, ?> value = this.annotationValues.get(property.getName());
            if (value != null) {
                return value.filter(property);
            }
            AnnotationValue.ForMissingValue defaultValue = property.getDefaultValue();
            return defaultValue == null ? new AnnotationValue.ForMissingValue(this.annotationType, property.getName()) : defaultValue;
        }

        @Override
        public TypeDescription getAnnotationType() {
            return this.annotationType;
        }

        public <T extends Annotation> Loadable<T> prepare(Class<T> annotationType) {
            if (!this.annotationType.represents(annotationType)) {
                throw new IllegalArgumentException(annotationType + " does not represent " + this.annotationType);
            }
            return new Loadable<T>(annotationType);
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        protected class Loadable<S extends Annotation>
        extends AbstractBase
        implements net.bytebuddy.description.annotation.AnnotationDescription$Loadable<S> {
            private final Class<S> annotationType;

            protected Loadable(Class<S> annotationType) {
                this.annotationType = annotationType;
            }

            @Override
            public S load() {
                return AnnotationInvocationHandler.of(this.annotationType.getClassLoader(), this.annotationType, Latent.this.annotationValues);
            }

            @Override
            public AnnotationValue<?, ?> getValue(MethodDescription.InDefinedShape property) {
                return Latent.this.getValue(property);
            }

            @Override
            public TypeDescription getAnnotationType() {
                return TypeDescription.ForLoadedType.of(this.annotationType);
            }

            @Override
            public <T extends Annotation> net.bytebuddy.description.annotation.AnnotationDescription$Loadable<T> prepare(Class<T> annotationType) {
                return Latent.this.prepare((Class)annotationType);
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class ForLoadedAnnotation<S extends Annotation>
    extends AbstractBase
    implements Loadable<S> {
        private static final Object[] NO_ARGUMENT;
        private final S annotation;
        private final Class<S> annotationType;
        private static final boolean ACCESS_CONTROLLER;

        protected ForLoadedAnnotation(S annotation) {
            this((Annotation)annotation, annotation.annotationType());
        }

        private ForLoadedAnnotation(S annotation, Class<S> annotationType) {
            this.annotation = annotation;
            this.annotationType = annotationType;
        }

        @AccessControllerPlugin.Enhance
        private static <T> T doPrivileged(PrivilegedAction<T> privilegedAction) {
            PrivilegedAction<T> action;
            if (ACCESS_CONTROLLER) {
                return AccessController.doPrivileged(privilegedAction);
            }
            return action.run();
        }

        public static <U extends Annotation> Loadable<U> of(U annotation) {
            return new ForLoadedAnnotation<U>(annotation);
        }

        @Override
        public S load() {
            return this.annotationType == this.annotation.annotationType() ? this.annotation : AnnotationInvocationHandler.of(this.annotationType.getClassLoader(), this.annotationType, ForLoadedAnnotation.asValue(this.annotation));
        }

        private static Map<String, AnnotationValue<?, ?>> asValue(Annotation annotation) {
            HashMap annotationValues = new HashMap();
            for (Method property : annotation.annotationType().getDeclaredMethods()) {
                try {
                    annotationValues.put(property.getName(), ForLoadedAnnotation.asValue(property.invoke((Object)annotation, NO_ARGUMENT), property.getReturnType()));
                }
                catch (InvocationTargetException exception) {
                    Throwable cause = exception.getTargetException();
                    if (cause instanceof TypeNotPresentException) {
                        annotationValues.put(property.getName(), new AnnotationValue.ForMissingType(((TypeNotPresentException)cause).typeName()));
                        continue;
                    }
                    if (cause instanceof EnumConstantNotPresentException) {
                        annotationValues.put(property.getName(), new AnnotationValue.ForEnumerationDescription.WithUnknownConstant(new TypeDescription.ForLoadedType(((EnumConstantNotPresentException)cause).enumType()), ((EnumConstantNotPresentException)cause).constantName()));
                        continue;
                    }
                    if (cause instanceof AnnotationTypeMismatchException) {
                        annotationValues.put(property.getName(), new AnnotationValue.ForMismatchedType(new MethodDescription.ForLoadedMethod(((AnnotationTypeMismatchException)cause).element()), ((AnnotationTypeMismatchException)cause).foundType()));
                        continue;
                    }
                    if (cause instanceof IncompleteAnnotationException) continue;
                    throw new IllegalStateException("Cannot read " + property, cause);
                }
                catch (IllegalAccessException exception) {
                    throw new IllegalStateException("Cannot access " + property, exception);
                }
            }
            return annotationValues;
        }

        public static AnnotationValue<?, ?> asValue(Object value, Class<?> type) {
            if (Enum.class.isAssignableFrom(type)) {
                return AnnotationValue.ForEnumerationDescription.of(new EnumerationDescription.ForLoadedEnumeration((Enum)value));
            }
            if (Enum[].class.isAssignableFrom(type)) {
                Enum[] element = (Enum[])value;
                EnumerationDescription[] enumerationDescription = new EnumerationDescription[element.length];
                int index = 0;
                for (Enum anElement : element) {
                    enumerationDescription[index++] = new EnumerationDescription.ForLoadedEnumeration(anElement);
                }
                return AnnotationValue.ForDescriptionArray.of(TypeDescription.ForLoadedType.of(type.getComponentType()), enumerationDescription);
            }
            if (Annotation.class.isAssignableFrom(type)) {
                return AnnotationValue.ForAnnotationDescription.of(TypeDescription.ForLoadedType.of(type), ForLoadedAnnotation.asValue((Annotation)value));
            }
            if (Annotation[].class.isAssignableFrom(type)) {
                Annotation[] element = (Annotation[])value;
                AnnotationDescription[] annotationDescription = new AnnotationDescription[element.length];
                int index = 0;
                for (Annotation anElement : element) {
                    annotationDescription[index++] = new Latent(TypeDescription.ForLoadedType.of(type.getComponentType()), ForLoadedAnnotation.asValue(anElement));
                }
                return AnnotationValue.ForDescriptionArray.of(TypeDescription.ForLoadedType.of(type.getComponentType()), annotationDescription);
            }
            if (Class.class.isAssignableFrom(type)) {
                return AnnotationValue.ForTypeDescription.of(TypeDescription.ForLoadedType.of((Class)value));
            }
            if (Class[].class.isAssignableFrom(type)) {
                Class[] element = (Class[])value;
                TypeDescription[] typeDescription = new TypeDescription[element.length];
                int index = 0;
                for (Class anElement : element) {
                    typeDescription[index++] = TypeDescription.ForLoadedType.of(anElement);
                }
                return AnnotationValue.ForDescriptionArray.of(typeDescription);
            }
            return AnnotationValue.ForConstant.of(value);
        }

        @Override
        @SuppressFBWarnings(value={"REC_CATCH_EXCEPTION"}, justification="Exception should always be wrapped for clarity.")
        public AnnotationValue<?, ?> getValue(MethodDescription.InDefinedShape property) {
            if (!property.getDeclaringType().represents(this.annotation.annotationType())) {
                throw new IllegalArgumentException(property + " does not represent " + this.annotation.annotationType());
            }
            try {
                Method method;
                boolean accessible = property.getDeclaringType().isPublic();
                Method method2 = method = property instanceof MethodDescription.ForLoadedMethod ? ((MethodDescription.ForLoadedMethod)property).getLoadedMethod() : null;
                if (method == null || method.getDeclaringClass() != this.annotation.annotationType() || !accessible && !method.isAccessible()) {
                    method = this.annotation.annotationType().getMethod(property.getName(), new Class[0]);
                    if (!accessible) {
                        ForLoadedAnnotation.doPrivileged(new SetAccessibleAction<Method>(method));
                    }
                }
                return ForLoadedAnnotation.asValue(method.invoke(this.annotation, NO_ARGUMENT), method.getReturnType()).filter(property);
            }
            catch (InvocationTargetException exception) {
                Throwable cause = exception.getTargetException();
                if (cause instanceof TypeNotPresentException) {
                    return new AnnotationValue.ForMissingType(((TypeNotPresentException)cause).typeName());
                }
                if (cause instanceof EnumConstantNotPresentException) {
                    return new AnnotationValue.ForEnumerationDescription.WithUnknownConstant(new TypeDescription.ForLoadedType(((EnumConstantNotPresentException)cause).enumType()), ((EnumConstantNotPresentException)cause).constantName());
                }
                if (cause instanceof AnnotationTypeMismatchException) {
                    return new AnnotationValue.ForMismatchedType(new MethodDescription.ForLoadedMethod(((AnnotationTypeMismatchException)cause).element()), ((AnnotationTypeMismatchException)cause).foundType());
                }
                if (cause instanceof IncompleteAnnotationException) {
                    return new AnnotationValue.ForMissingValue(new TypeDescription.ForLoadedType(((IncompleteAnnotationException)cause).annotationType()), ((IncompleteAnnotationException)cause).elementName());
                }
                throw new IllegalStateException("Error reading annotation property " + property, cause);
            }
            catch (Exception exception) {
                throw new IllegalStateException("Cannot access annotation property " + property, exception);
            }
        }

        @Override
        public <T extends Annotation> Loadable<T> prepare(Class<T> annotationType) {
            if (!this.annotation.annotationType().getName().equals(annotationType.getName())) {
                throw new IllegalArgumentException(annotationType + " does not represent " + this.annotation.annotationType());
            }
            return annotationType == this.annotation.annotationType() ? this : new ForLoadedAnnotation<S>(this.annotation, annotationType);
        }

        @Override
        public TypeDescription getAnnotationType() {
            return TypeDescription.ForLoadedType.of(this.annotation.annotationType());
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
            NO_ARGUMENT = new Object[0];
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static abstract class AbstractBase
    implements AnnotationDescription {
        private static final Set<ElementType> DEFAULT_TARGET = new HashSet<ElementType>();
        private static final MethodDescription.InDefinedShape RETENTION_VALUE;
        private static final MethodDescription.InDefinedShape TARGET_VALUE;
        private transient /* synthetic */ int hashCode;

        @Override
        public AnnotationValue<?, ?> getValue(String property) {
            MethodList candidates = (MethodList)this.getAnnotationType().getDeclaredMethods().filter(ElementMatchers.named(property).and(ElementMatchers.takesArguments(0)).and(ElementMatchers.isPublic()).and(ElementMatchers.not(ElementMatchers.isStatic())));
            if (candidates.size() == 1) {
                return this.getValue((MethodDescription.InDefinedShape)candidates.getOnly());
            }
            throw new IllegalArgumentException("Unknown property of " + this.getAnnotationType() + ": " + property);
        }

        @Override
        public RetentionPolicy getRetention() {
            Loadable<Retention> retention = this.getAnnotationType().getDeclaredAnnotations().ofType(Retention.class);
            return retention == null ? RetentionPolicy.CLASS : retention.getValue(RETENTION_VALUE).load(ClassLoadingStrategy.BOOTSTRAP_LOADER).resolve(RetentionPolicy.class);
        }

        @Override
        public Set<ElementType> getElementTypes() {
            Loadable<Target> target = this.getAnnotationType().getDeclaredAnnotations().ofType(Target.class);
            return target == null ? Collections.unmodifiableSet(DEFAULT_TARGET) : new HashSet<Object>(Arrays.asList((Object[])target.getValue(TARGET_VALUE).load(ClassLoadingStrategy.BOOTSTRAP_LOADER).resolve(ElementType[].class)));
        }

        @Override
        public boolean isSupportedOn(ElementType elementType) {
            return this.isSupportedOn(elementType.name());
        }

        @Override
        public boolean isSupportedOn(String elementType) {
            Loadable<Target> target = this.getAnnotationType().getDeclaredAnnotations().ofType(Target.class);
            if (target == null) {
                if (elementType.equals("TYPE_USE")) {
                    return true;
                }
                for (ElementType candidate : DEFAULT_TARGET) {
                    if (!candidate.name().equals(elementType)) continue;
                    return true;
                }
            } else {
                for (EnumerationDescription enumerationDescription : target.getValue(TARGET_VALUE).resolve(EnumerationDescription[].class)) {
                    if (!enumerationDescription.getValue().equals(elementType)) continue;
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean isInherited() {
            return this.getAnnotationType().getDeclaredAnnotations().isAnnotationPresent(Inherited.class);
        }

        @Override
        public boolean isDocumented() {
            return this.getAnnotationType().getDeclaredAnnotations().isAnnotationPresent(Documented.class);
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
                int hashCode = 0;
                for (MethodDescription.InDefinedShape methodDescription : abstractBase.getAnnotationType().getDeclaredMethods()) {
                    hashCode += 31 * abstractBase.getValue(methodDescription).hashCode();
                }
                n2 = n = hashCode;
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
            if (!(other instanceof AnnotationDescription)) {
                return false;
            }
            AnnotationDescription annotationDescription = (AnnotationDescription)other;
            TypeDescription annotationType = this.getAnnotationType();
            if (!annotationDescription.getAnnotationType().equals(annotationType)) {
                return false;
            }
            for (MethodDescription.InDefinedShape methodDescription : annotationType.getDeclaredMethods()) {
                if (this.getValue(methodDescription).equals(annotationDescription.getValue(methodDescription))) continue;
                return false;
            }
            return true;
        }

        public String toString() {
            TypeDescription annotationType = this.getAnnotationType();
            StringBuilder toString = new StringBuilder().append('@');
            RenderingDispatcher.CURRENT.appendType(toString, annotationType);
            toString.append('(');
            boolean firstMember = true;
            for (MethodDescription.InDefinedShape methodDescription : annotationType.getDeclaredMethods()) {
                AnnotationValue<?, ?> value = this.getValue(methodDescription);
                if (value.getState() == AnnotationValue.State.UNDEFINED) continue;
                if (firstMember) {
                    firstMember = false;
                } else {
                    toString.append(", ");
                }
                RenderingDispatcher.CURRENT.appendPrefix(toString, methodDescription.getName(), annotationType.getDeclaredMethods().size());
                toString.append(value);
            }
            return toString.append(')').toString();
        }

        static {
            for (ElementType elementType : ElementType.values()) {
                if (elementType.name().equals("TYPE_PARAMETER")) continue;
                DEFAULT_TARGET.add(elementType);
            }
            RETENTION_VALUE = (MethodDescription.InDefinedShape)((MethodList)TypeDescription.ForLoadedType.of(Retention.class).getDeclaredMethods().filter(ElementMatchers.named("value"))).getOnly();
            TARGET_VALUE = (MethodDescription.InDefinedShape)((MethodList)TypeDescription.ForLoadedType.of(Target.class).getDeclaredMethods().filter(ElementMatchers.named("value"))).getOnly();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class AnnotationInvocationHandler<T extends Annotation>
    implements InvocationHandler {
        private static final String HASH_CODE = "hashCode";
        private static final String EQUALS = "equals";
        private static final String TO_STRING = "toString";
        private static final String ANNOTATION_TYPE = "annotationType";
        private static final Object[] NO_ARGUMENT = new Object[0];
        private final Class<? extends Annotation> annotationType;
        private final LinkedHashMap<Method, AnnotationValue.Loaded<?>> values;
        private transient /* synthetic */ int hashCode;

        protected AnnotationInvocationHandler(Class<T> annotationType, LinkedHashMap<Method, AnnotationValue.Loaded<?>> values) {
            this.annotationType = annotationType;
            this.values = values;
        }

        public static <S extends Annotation> S of(@MaybeNull ClassLoader classLoader, Class<S> annotationType, Map<String, ? extends AnnotationValue<?, ?>> values) {
            LinkedHashMap loadedValues = new LinkedHashMap();
            for (Method method : annotationType.getDeclaredMethods()) {
                AnnotationValue<?, ?> annotationValue = values.get(method.getName());
                if (annotationValue == null) {
                    Object defaultValue = method.getDefaultValue();
                    loadedValues.put(method, (defaultValue == null ? new AnnotationValue.ForMissingValue(new TypeDescription.ForLoadedType(method.getDeclaringClass()), method.getName()) : ForLoadedAnnotation.asValue(defaultValue, method.getReturnType())).load(classLoader));
                    continue;
                }
                loadedValues.put(method, annotationValue.filter(new MethodDescription.ForLoadedMethod(method)).load(classLoader));
            }
            return (S)((Annotation)Proxy.newProxyInstance(classLoader, new Class[]{annotationType}, new AnnotationInvocationHandler<S>(annotationType, loadedValues)));
        }

        @Override
        public Object invoke(Object proxy, Method method, @MaybeNull Object[] argument) {
            if (method.getDeclaringClass() != this.annotationType) {
                if (method.getName().equals(HASH_CODE)) {
                    return this.hashCodeRepresentation();
                }
                if (method.getName().equals(EQUALS) && method.getParameterTypes().length == 1) {
                    return this.equalsRepresentation(proxy, argument[0]);
                }
                if (method.getName().equals(TO_STRING)) {
                    return this.toStringRepresentation();
                }
                if (method.getName().equals(ANNOTATION_TYPE)) {
                    return this.annotationType;
                }
                throw new IllegalStateException("Unexpected method: " + method);
            }
            return this.values.get(method).resolve();
        }

        protected String toStringRepresentation() {
            StringBuilder toString = new StringBuilder();
            toString.append('@');
            RenderingDispatcher.CURRENT.appendType(toString, TypeDescription.ForLoadedType.of(this.annotationType));
            toString.append('(');
            boolean firstMember = true;
            for (Map.Entry<Method, AnnotationValue.Loaded<?>> entry : this.values.entrySet()) {
                if (!entry.getValue().getState().isDefined()) continue;
                if (firstMember) {
                    firstMember = false;
                } else {
                    toString.append(", ");
                }
                RenderingDispatcher.CURRENT.appendPrefix(toString, entry.getKey().getName(), this.values.entrySet().size());
                toString.append(entry.getValue().toString());
            }
            toString.append(')');
            return toString.toString();
        }

        private int hashCodeRepresentation() {
            int hashCode = 0;
            for (Map.Entry<Method, AnnotationValue.Loaded<?>> entry : this.values.entrySet()) {
                if (!entry.getValue().getState().isDefined()) continue;
                hashCode += 127 * entry.getKey().getName().hashCode() ^ entry.getValue().hashCode();
            }
            return hashCode;
        }

        private boolean equalsRepresentation(Object self, Object other) {
            Object invocationHandler;
            if (self == other) {
                return true;
            }
            if (!this.annotationType.isInstance(other)) {
                return false;
            }
            if (Proxy.isProxyClass(other.getClass()) && (invocationHandler = Proxy.getInvocationHandler(other)) instanceof AnnotationInvocationHandler) {
                return invocationHandler.equals(this);
            }
            try {
                for (Map.Entry entry : this.values.entrySet()) {
                    try {
                        if (((AnnotationValue.Loaded)entry.getValue()).represents(((Method)entry.getKey()).invoke(other, NO_ARGUMENT))) continue;
                        return false;
                    }
                    catch (RuntimeException exception) {
                        return false;
                    }
                }
                return true;
            }
            catch (InvocationTargetException ignored) {
                return false;
            }
            catch (IllegalAccessException exception) {
                throw new IllegalStateException("Could not access annotation property", exception);
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
                AnnotationInvocationHandler annotationInvocationHandler = this;
                int result = annotationInvocationHandler.annotationType.hashCode();
                result = 31 * result + annotationInvocationHandler.values.hashCode();
                for (Map.Entry<Method, AnnotationValue.Loaded<?>> entry : annotationInvocationHandler.values.entrySet()) {
                    result = 31 * result + entry.getValue().hashCode();
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
            if (!(other instanceof AnnotationInvocationHandler)) {
                return false;
            }
            AnnotationInvocationHandler that = (AnnotationInvocationHandler)other;
            if (!this.annotationType.equals(that.annotationType)) {
                return false;
            }
            for (Map.Entry<Method, AnnotationValue.Loaded<?>> entry : this.values.entrySet()) {
                if (entry.getValue().equals(that.values.get(entry.getKey()))) continue;
                return false;
            }
            return true;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum RenderingDispatcher {
        LEGACY_VM,
        JAVA_14_CAPABLE_VM{

            public void appendPrefix(StringBuilder toString, String key, int count) {
                if (count > 1 || !key.equals("value")) {
                    super.appendPrefix(toString, key, count);
                }
            }
        }
        ,
        JAVA_19_CAPABLE_VM{

            public void appendPrefix(StringBuilder toString, String key, int count) {
                if (count > 1 || !key.equals("value")) {
                    super.appendPrefix(toString, key, count);
                }
            }

            public void appendType(StringBuilder toString, TypeDescription typeDescription) {
                toString.append(typeDescription.getCanonicalName());
            }
        };

        public static final RenderingDispatcher CURRENT;

        public void appendPrefix(StringBuilder toString, String key, int count) {
            toString.append(key).append('=');
        }

        public void appendType(StringBuilder toString, TypeDescription typeDescription) {
            toString.append(typeDescription.getName());
        }

        static {
            ClassFileVersion classFileVersion = ClassFileVersion.ofThisVm(ClassFileVersion.JAVA_V5);
            CURRENT = classFileVersion.isAtLeast(ClassFileVersion.JAVA_V19) ? JAVA_19_CAPABLE_VM : (classFileVersion.isAtLeast(ClassFileVersion.JAVA_V14) ? JAVA_14_CAPABLE_VM : LEGACY_VM);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static interface Loadable<S extends Annotation>
    extends AnnotationDescription {
        public S load();
    }
}

