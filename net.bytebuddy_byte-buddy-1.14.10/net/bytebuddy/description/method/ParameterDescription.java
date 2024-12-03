/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package net.bytebuddy.description.method;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.AbstractList;
import java.util.Collections;
import java.util.List;
import net.bytebuddy.build.AccessControllerPlugin;
import net.bytebuddy.build.CachedReturnPlugin;
import net.bytebuddy.build.HashCodeAndEqualsPlugin;
import net.bytebuddy.description.ByteCodeElement;
import net.bytebuddy.description.ModifierReviewable;
import net.bytebuddy.description.NamedElement;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.annotation.AnnotationList;
import net.bytebuddy.description.annotation.AnnotationSource;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.ParameterList;
import net.bytebuddy.description.type.TypeDefinition;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.description.type.TypeList;
import net.bytebuddy.implementation.bytecode.StackSize;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.utility.dispatcher.JavaDispatcher;
import net.bytebuddy.utility.nullability.AlwaysNull;
import net.bytebuddy.utility.nullability.MaybeNull;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface ParameterDescription
extends AnnotationSource,
NamedElement.WithRuntimeName,
NamedElement.WithOptionalName,
ModifierReviewable.ForParameterDescription,
ByteCodeElement.TypeDependant<InDefinedShape, Token> {
    public static final String NAME_PREFIX = "arg";

    public TypeDescription.Generic getType();

    public MethodDescription getDeclaringMethod();

    public int getIndex();

    public boolean hasModifiers();

    public int getOffset();

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class Token
    implements ByteCodeElement.Token<Token> {
        @AlwaysNull
        public static final String NO_NAME = null;
        @AlwaysNull
        public static final Integer NO_MODIFIERS = null;
        private final TypeDescription.Generic type;
        private final List<? extends AnnotationDescription> annotations;
        @MaybeNull
        private final String name;
        @MaybeNull
        private final Integer modifiers;
        private transient /* synthetic */ int hashCode;

        public Token(TypeDescription.Generic type) {
            this(type, Collections.emptyList());
        }

        public Token(TypeDescription.Generic type, List<? extends AnnotationDescription> annotations) {
            this(type, annotations, NO_NAME, NO_MODIFIERS);
        }

        public Token(TypeDescription.Generic type, @MaybeNull String name, @MaybeNull Integer modifiers) {
            this(type, Collections.emptyList(), name, modifiers);
        }

        public Token(TypeDescription.Generic type, List<? extends AnnotationDescription> annotations, @MaybeNull String name, @MaybeNull Integer modifiers) {
            this.type = type;
            this.annotations = annotations;
            this.name = name;
            this.modifiers = modifiers;
        }

        public TypeDescription.Generic getType() {
            return this.type;
        }

        public AnnotationList getAnnotations() {
            return new AnnotationList.Explicit(this.annotations);
        }

        @MaybeNull
        public String getName() {
            return this.name;
        }

        @MaybeNull
        public Integer getModifiers() {
            return this.modifiers;
        }

        @Override
        public Token accept(TypeDescription.Generic.Visitor<? extends TypeDescription.Generic> visitor) {
            return new Token(this.type.accept(visitor), this.annotations, this.name, this.modifiers);
        }

        @CachedReturnPlugin.Enhance(value="hashCode")
        public int hashCode() {
            int n;
            int n2;
            int n3 = this.hashCode;
            if (n3 != 0) {
                n2 = 0;
            } else {
                Token token = this;
                int result = token.type.hashCode();
                result = 31 * result + token.annotations.hashCode();
                result = 31 * result + (token.name != null ? token.name.hashCode() : 0);
                n2 = n = (result = 31 * result + (token.modifiers != null ? token.modifiers.hashCode() : 0));
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
            if (!(other instanceof Token)) {
                return false;
            }
            Token token = (Token)other;
            return this.type.equals(token.type) && this.annotations.equals(token.annotations) && (this.name != null ? this.name.equals(token.name) : token.name == null) && (this.modifiers != null ? this.modifiers.equals(token.modifiers) : token.modifiers == null);
        }

        public String toString() {
            return "ParameterDescription.Token{type=" + this.type + ", annotations=" + this.annotations + ", name='" + this.name + '\'' + ", modifiers=" + this.modifiers + '}';
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static class TypeList
        extends AbstractList<Token> {
            private final List<? extends TypeDefinition> typeDescriptions;

            public TypeList(List<? extends TypeDefinition> typeDescriptions) {
                this.typeDescriptions = typeDescriptions;
            }

            @Override
            public Token get(int index) {
                return new Token(this.typeDescriptions.get(index).asGenericType());
            }

            @Override
            public int size() {
                return this.typeDescriptions.size();
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class TypeSubstituting
    extends AbstractBase
    implements InGenericShape {
        private final MethodDescription.InGenericShape declaringMethod;
        private final ParameterDescription parameterDescription;
        private final TypeDescription.Generic.Visitor<? extends TypeDescription.Generic> visitor;

        public TypeSubstituting(MethodDescription.InGenericShape declaringMethod, ParameterDescription parameterDescription, TypeDescription.Generic.Visitor<? extends TypeDescription.Generic> visitor) {
            this.declaringMethod = declaringMethod;
            this.parameterDescription = parameterDescription;
            this.visitor = visitor;
        }

        @Override
        public TypeDescription.Generic getType() {
            return this.parameterDescription.getType().accept(this.visitor);
        }

        @Override
        public MethodDescription.InGenericShape getDeclaringMethod() {
            return this.declaringMethod;
        }

        @Override
        public int getIndex() {
            return this.parameterDescription.getIndex();
        }

        @Override
        public boolean isNamed() {
            return this.parameterDescription.isNamed();
        }

        @Override
        public boolean hasModifiers() {
            return this.parameterDescription.hasModifiers();
        }

        @Override
        public int getOffset() {
            return this.parameterDescription.getOffset();
        }

        @Override
        public String getName() {
            return this.parameterDescription.getName();
        }

        @Override
        public int getModifiers() {
            return this.parameterDescription.getModifiers();
        }

        @Override
        public AnnotationList getDeclaredAnnotations() {
            return this.parameterDescription.getDeclaredAnnotations();
        }

        @Override
        public InDefinedShape asDefined() {
            return (InDefinedShape)this.parameterDescription.asDefined();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class Latent
    extends InDefinedShape.AbstractBase {
        private final MethodDescription.InDefinedShape declaringMethod;
        private final TypeDescription.Generic parameterType;
        private final List<? extends AnnotationDescription> declaredAnnotations;
        @MaybeNull
        private final String name;
        @MaybeNull
        private final Integer modifiers;
        private final int index;
        private final int offset;

        public Latent(MethodDescription.InDefinedShape declaringMethod, Token token, int index, int offset) {
            this(declaringMethod, token.getType(), token.getAnnotations(), token.getName(), token.getModifiers(), index, offset);
        }

        public Latent(MethodDescription.InDefinedShape declaringMethod, TypeDescription.Generic parameterType, int index, int offset) {
            this(declaringMethod, parameterType, Collections.emptyList(), Token.NO_NAME, Token.NO_MODIFIERS, index, offset);
        }

        public Latent(MethodDescription.InDefinedShape declaringMethod, TypeDescription.Generic parameterType, List<? extends AnnotationDescription> declaredAnnotations, @MaybeNull String name, @MaybeNull Integer modifiers, int index, int offset) {
            this.declaringMethod = declaringMethod;
            this.parameterType = parameterType;
            this.declaredAnnotations = declaredAnnotations;
            this.name = name;
            this.modifiers = modifiers;
            this.index = index;
            this.offset = offset;
        }

        @Override
        public TypeDescription.Generic getType() {
            return this.parameterType.accept(TypeDescription.Generic.Visitor.Substitutor.ForAttachment.of(this));
        }

        @Override
        public MethodDescription.InDefinedShape getDeclaringMethod() {
            return this.declaringMethod;
        }

        @Override
        public int getIndex() {
            return this.index;
        }

        @Override
        public int getOffset() {
            return this.offset;
        }

        @Override
        public boolean isNamed() {
            return this.name != null;
        }

        @Override
        public boolean hasModifiers() {
            return this.modifiers != null;
        }

        @Override
        public String getName() {
            return this.name == null ? super.getName() : this.name;
        }

        @Override
        public int getModifiers() {
            return this.modifiers == null ? super.getModifiers() : this.modifiers.intValue();
        }

        @Override
        public AnnotationList getDeclaredAnnotations() {
            return new AnnotationList.Explicit(this.declaredAnnotations);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static abstract class ForLoadedParameter<T extends AccessibleObject>
    extends InDefinedShape.AbstractBase {
        private static final Parameter PARAMETER;
        protected final T executable;
        protected final int index;
        protected final ParameterAnnotationSource parameterAnnotationSource;
        private static final boolean ACCESS_CONTROLLER;

        protected ForLoadedParameter(T executable, int index, ParameterAnnotationSource parameterAnnotationSource) {
            this.executable = executable;
            this.index = index;
            this.parameterAnnotationSource = parameterAnnotationSource;
        }

        @AccessControllerPlugin.Enhance
        private static <T> T doPrivileged(PrivilegedAction<T> privilegedAction) {
            PrivilegedAction<T> action;
            if (ACCESS_CONTROLLER) {
                return AccessController.doPrivileged(privilegedAction);
            }
            return action.run();
        }

        @Override
        public String getName() {
            return PARAMETER.getName(ParameterList.ForLoadedExecutable.EXECUTABLE.getParameters(this.executable)[this.index]);
        }

        @Override
        public int getIndex() {
            return this.index;
        }

        @Override
        public boolean isNamed() {
            return PARAMETER.isNamePresent(ParameterList.ForLoadedExecutable.EXECUTABLE.getParameters(this.executable)[this.index]);
        }

        @Override
        public int getModifiers() {
            return PARAMETER.getModifiers(ParameterList.ForLoadedExecutable.EXECUTABLE.getParameters(this.executable)[this.index]);
        }

        @Override
        public boolean hasModifiers() {
            return this.isNamed() || this.getModifiers() != 0;
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
            PARAMETER = ForLoadedParameter.doPrivileged(JavaDispatcher.of(Parameter.class));
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        protected static class OfLegacyVmConstructor
        extends InDefinedShape.AbstractBase {
            private final Constructor<?> constructor;
            private final int index;
            private final Class<?>[] parameterType;
            private final ParameterAnnotationSource parameterAnnotationSource;

            protected OfLegacyVmConstructor(Constructor<?> constructor, int index, Class<?>[] parameterType, ParameterAnnotationSource parameterAnnotationSource) {
                this.constructor = constructor;
                this.index = index;
                this.parameterType = parameterType;
                this.parameterAnnotationSource = parameterAnnotationSource;
            }

            @Override
            public TypeDescription.Generic getType() {
                if (TypeDescription.AbstractBase.RAW_TYPES) {
                    return TypeDescription.Generic.OfNonGenericType.ForLoadedType.of(this.parameterType[this.index]);
                }
                return new TypeDescription.Generic.LazyProjection.OfConstructorParameter(this.constructor, this.index, this.parameterType);
            }

            @Override
            public MethodDescription.InDefinedShape getDeclaringMethod() {
                return new MethodDescription.ForLoadedConstructor(this.constructor);
            }

            @Override
            public int getIndex() {
                return this.index;
            }

            @Override
            public boolean isNamed() {
                return false;
            }

            @Override
            public boolean hasModifiers() {
                return false;
            }

            @Override
            public AnnotationList getDeclaredAnnotations() {
                MethodDescription.InDefinedShape declaringMethod = this.getDeclaringMethod();
                Annotation[][] parameterAnnotation = this.parameterAnnotationSource.getParameterAnnotations();
                if (parameterAnnotation.length != declaringMethod.getParameters().size() && declaringMethod.getDeclaringType().isInnerClass()) {
                    return (AnnotationList)((Object)(this.index == 0 ? new AnnotationList.Empty() : new AnnotationList.ForLoadedAnnotations(parameterAnnotation[this.index - 1])));
                }
                return new AnnotationList.ForLoadedAnnotations(parameterAnnotation[this.index]);
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        protected static class OfLegacyVmMethod
        extends InDefinedShape.AbstractBase {
            private final Method method;
            private final int index;
            private final Class<?>[] parameterType;
            private final ParameterAnnotationSource parameterAnnotationSource;

            protected OfLegacyVmMethod(Method method, int index, Class<?>[] parameterType, ParameterAnnotationSource parameterAnnotationSource) {
                this.method = method;
                this.index = index;
                this.parameterType = parameterType;
                this.parameterAnnotationSource = parameterAnnotationSource;
            }

            @Override
            public TypeDescription.Generic getType() {
                if (TypeDescription.AbstractBase.RAW_TYPES) {
                    return TypeDescription.Generic.OfNonGenericType.ForLoadedType.of(this.parameterType[this.index]);
                }
                return new TypeDescription.Generic.LazyProjection.OfMethodParameter(this.method, this.index, this.parameterType);
            }

            @Override
            public MethodDescription.InDefinedShape getDeclaringMethod() {
                return new MethodDescription.ForLoadedMethod(this.method);
            }

            @Override
            public int getIndex() {
                return this.index;
            }

            @Override
            public boolean isNamed() {
                return false;
            }

            @Override
            public boolean hasModifiers() {
                return false;
            }

            @Override
            public AnnotationList getDeclaredAnnotations() {
                return new AnnotationList.ForLoadedAnnotations(this.parameterAnnotationSource.getParameterAnnotations()[this.index]);
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        protected static class OfConstructor
        extends ForLoadedParameter<Constructor<?>> {
            protected OfConstructor(Constructor<?> constructor, int index, ParameterAnnotationSource parameterAnnotationSource) {
                super(constructor, index, parameterAnnotationSource);
            }

            @Override
            @SuppressFBWarnings(value={"BC_UNCONFIRMED_CAST"}, justification="The implicit field type casting is not understood by Findbugs.")
            public MethodDescription.InDefinedShape getDeclaringMethod() {
                return new MethodDescription.ForLoadedConstructor((Constructor)this.executable);
            }

            @Override
            @SuppressFBWarnings(value={"BC_UNCONFIRMED_CAST"}, justification="The implicit field type casting is not understood by Findbugs.")
            public TypeDescription.Generic getType() {
                if (TypeDescription.AbstractBase.RAW_TYPES) {
                    return TypeDescription.Generic.OfNonGenericType.ForLoadedType.of(((Constructor)this.executable).getParameterTypes()[this.index]);
                }
                return new TypeDescription.Generic.LazyProjection.OfConstructorParameter((Constructor)this.executable, this.index, ((Constructor)this.executable).getParameterTypes());
            }

            @Override
            @SuppressFBWarnings(value={"BC_UNCONFIRMED_CAST"}, justification="The implicit field type casting is not understood by Findbugs")
            public AnnotationList getDeclaredAnnotations() {
                MethodDescription.InDefinedShape declaringMethod;
                Annotation[][] annotation = this.parameterAnnotationSource.getParameterAnnotations();
                if (annotation.length != (declaringMethod = this.getDeclaringMethod()).getParameters().size() && declaringMethod.getDeclaringType().isInnerClass()) {
                    return (AnnotationList)((Object)(this.index == 0 ? new AnnotationList.Empty() : new AnnotationList.ForLoadedAnnotations(annotation[this.index - 1])));
                }
                return new AnnotationList.ForLoadedAnnotations(annotation[this.index]);
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        protected static class OfMethod
        extends ForLoadedParameter<Method> {
            protected OfMethod(Method method, int index, ParameterAnnotationSource parameterAnnotationSource) {
                super(method, index, parameterAnnotationSource);
            }

            @Override
            @SuppressFBWarnings(value={"BC_UNCONFIRMED_CAST"}, justification="The implicit field type casting is not understood by Findbugs.")
            public MethodDescription.InDefinedShape getDeclaringMethod() {
                return new MethodDescription.ForLoadedMethod((Method)this.executable);
            }

            @Override
            @SuppressFBWarnings(value={"BC_UNCONFIRMED_CAST"}, justification="The implicit field type casting is not understood by Findbugs.")
            public TypeDescription.Generic getType() {
                if (TypeDescription.AbstractBase.RAW_TYPES) {
                    return TypeDescription.Generic.OfNonGenericType.ForLoadedType.of(((Method)this.executable).getParameterTypes()[this.index]);
                }
                return new TypeDescription.Generic.LazyProjection.OfMethodParameter((Method)this.executable, this.index, ((Method)this.executable).getParameterTypes());
            }

            @Override
            @SuppressFBWarnings(value={"BC_UNCONFIRMED_CAST"}, justification="The implicit field type casting is not understood by Findbugs.")
            public AnnotationList getDeclaredAnnotations() {
                return new AnnotationList.ForLoadedAnnotations(this.parameterAnnotationSource.getParameterAnnotations()[this.index]);
            }
        }

        @JavaDispatcher.Proxied(value="java.lang.reflect.Parameter")
        protected static interface Parameter {
            @JavaDispatcher.Proxied(value="getModifiers")
            public int getModifiers(Object var1);

            @JavaDispatcher.Proxied(value="isNamePresent")
            public boolean isNamePresent(Object var1);

            @JavaDispatcher.Proxied(value="getName")
            public String getName(Object var1);
        }

        public static interface ParameterAnnotationSource {
            public Annotation[][] getParameterAnnotations();

            @HashCodeAndEqualsPlugin.Enhance
            public static class ForLoadedMethod
            implements ParameterAnnotationSource {
                private final Method method;

                public ForLoadedMethod(Method method) {
                    this.method = method;
                }

                public Annotation[][] getParameterAnnotations() {
                    return this.method.getParameterAnnotations();
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
                    return this.method.equals(((ForLoadedMethod)object).method);
                }

                public int hashCode() {
                    return this.getClass().hashCode() * 31 + this.method.hashCode();
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            @HashCodeAndEqualsPlugin.Enhance
            public static class ForLoadedConstructor
            implements ParameterAnnotationSource {
                private final Constructor<?> constructor;

                public ForLoadedConstructor(Constructor<?> constructor) {
                    this.constructor = constructor;
                }

                @Override
                public Annotation[][] getParameterAnnotations() {
                    return this.constructor.getParameterAnnotations();
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
                    return this.constructor.equals(((ForLoadedConstructor)object).constructor);
                }

                public int hashCode() {
                    return this.getClass().hashCode() * 31 + this.constructor.hashCode();
                }
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static abstract class AbstractBase
    extends ModifierReviewable.AbstractBase
    implements ParameterDescription {
        private transient /* synthetic */ int offset;
        private transient /* synthetic */ int hashCode;

        @Override
        public String getName() {
            return ParameterDescription.NAME_PREFIX.concat(String.valueOf(this.getIndex()));
        }

        @Override
        public String getInternalName() {
            return this.getName();
        }

        @Override
        public String getActualName() {
            return this.isNamed() ? this.getName() : "";
        }

        @Override
        public int getModifiers() {
            return 0;
        }

        @Override
        @CachedReturnPlugin.Enhance(value="offset")
        public int getOffset() {
            int n;
            int n2;
            int n3 = this.offset;
            if (n3 != 0) {
                n2 = 0;
            } else {
                AbstractBase abstractBase = this;
                TypeList parameterType = abstractBase.getDeclaringMethod().getParameters().asTypeList().asErasures();
                int offset = abstractBase.getDeclaringMethod().isStatic() ? StackSize.ZERO.getSize() : StackSize.SINGLE.getSize();
                for (int i = 0; i < abstractBase.getIndex(); ++i) {
                    offset += ((TypeDescription)parameterType.get(i)).getStackSize().getSize();
                }
                n2 = n = offset;
            }
            if (n == 0) {
                n = this.offset;
            } else {
                this.offset = n;
            }
            return n;
        }

        @Override
        public Token asToken(ElementMatcher<? super TypeDescription> matcher) {
            return new Token(this.getType().accept(new TypeDescription.Generic.Visitor.Substitutor.ForDetachment(matcher)), this.getDeclaredAnnotations(), this.isNamed() ? this.getName() : Token.NO_NAME, this.hasModifiers() ? Integer.valueOf(this.getModifiers()) : Token.NO_MODIFIERS);
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
                n2 = n = abstractBase.getDeclaringMethod().hashCode() ^ abstractBase.getIndex();
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
            if (!(other instanceof ParameterDescription)) {
                return false;
            }
            ParameterDescription parameterDescription = (ParameterDescription)other;
            return this.getDeclaringMethod().equals(parameterDescription.getDeclaringMethod()) && this.getIndex() == parameterDescription.getIndex();
        }

        public String toString() {
            StringBuilder stringBuilder = new StringBuilder(Modifier.toString(this.getModifiers()));
            if (this.getModifiers() != 0) {
                stringBuilder.append(' ');
            }
            stringBuilder.append(this.isVarArgs() ? this.getType().asErasure().getName().replaceFirst("\\[]$", "...") : this.getType().asErasure().getName());
            return stringBuilder.append(' ').append(this.getName()).toString();
        }
    }

    public static interface InDefinedShape
    extends ParameterDescription {
        public MethodDescription.InDefinedShape getDeclaringMethod();

        public static abstract class AbstractBase
        extends net.bytebuddy.description.method.ParameterDescription$AbstractBase
        implements InDefinedShape {
            public InDefinedShape asDefined() {
                return this;
            }
        }
    }

    public static interface InGenericShape
    extends ParameterDescription {
        public MethodDescription.InGenericShape getDeclaringMethod();
    }
}

