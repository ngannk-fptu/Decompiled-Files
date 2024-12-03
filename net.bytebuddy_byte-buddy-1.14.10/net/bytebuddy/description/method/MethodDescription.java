/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 *  javax.annotation.Nonnull
 */
package net.bytebuddy.description.method;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.GenericSignatureFormatError;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nonnull;
import net.bytebuddy.build.AccessControllerPlugin;
import net.bytebuddy.build.CachedReturnPlugin;
import net.bytebuddy.description.ByteCodeElement;
import net.bytebuddy.description.DeclaredByType;
import net.bytebuddy.description.ModifierReviewable;
import net.bytebuddy.description.TypeVariableSource;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.annotation.AnnotationList;
import net.bytebuddy.description.annotation.AnnotationValue;
import net.bytebuddy.description.enumeration.EnumerationDescription;
import net.bytebuddy.description.method.ParameterDescription;
import net.bytebuddy.description.method.ParameterList;
import net.bytebuddy.description.modifier.ModifierContributor;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.description.type.TypeDefinition;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.description.type.TypeList;
import net.bytebuddy.description.type.TypeVariableToken;
import net.bytebuddy.jar.asm.Type;
import net.bytebuddy.jar.asm.signature.SignatureWriter;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.JavaType;
import net.bytebuddy.utility.dispatcher.JavaDispatcher;
import net.bytebuddy.utility.nullability.AlwaysNull;
import net.bytebuddy.utility.nullability.MaybeNull;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface MethodDescription
extends TypeVariableSource,
ModifierReviewable.ForMethodDescription,
DeclaredByType.WithMandatoryDeclaration,
ByteCodeElement.Member,
ByteCodeElement.TypeDependant<InDefinedShape, Token> {
    public static final String CONSTRUCTOR_INTERNAL_NAME = "<init>";
    public static final String TYPE_INITIALIZER_INTERNAL_NAME = "<clinit>";
    public static final int TYPE_INITIALIZER_MODIFIER = 8;
    @AlwaysNull
    public static final InDefinedShape UNDEFINED = null;

    @Override
    @Nonnull
    public TypeDefinition getDeclaringType();

    public TypeDescription.Generic getReturnType();

    public ParameterList<?> getParameters();

    public TypeList.Generic getExceptionTypes();

    public int getActualModifiers();

    public int getActualModifiers(boolean var1);

    public int getActualModifiers(boolean var1, Visibility var2);

    public boolean isConstructor();

    public boolean isMethod();

    public boolean isTypeInitializer();

    public boolean represents(Method var1);

    public boolean represents(Constructor<?> var1);

    public boolean isVirtual();

    public int getStackSize();

    public boolean isDefaultMethod();

    public boolean isSpecializableFor(TypeDescription var1);

    @MaybeNull
    public AnnotationValue<?, ?> getDefaultValue();

    @MaybeNull
    public <T> T getDefaultValue(Class<T> var1);

    public boolean isInvokableOn(TypeDescription var1);

    public boolean isInvokeBootstrap();

    public boolean isInvokeBootstrap(List<? extends TypeDefinition> var1);

    public boolean isConstantBootstrap();

    public boolean isConstantBootstrap(List<? extends TypeDefinition> var1);

    public boolean isDefaultValue();

    public boolean isDefaultValue(AnnotationValue<?, ?> var1);

    @MaybeNull
    public TypeDescription.Generic getReceiverType();

    public SignatureToken asSignatureToken();

    public TypeToken asTypeToken();

    public boolean isBridgeCompatible(TypeToken var1);

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class TypeToken {
        private final TypeDescription returnType;
        private final List<? extends TypeDescription> parameterTypes;
        private transient /* synthetic */ int hashCode;

        public TypeToken(TypeDescription returnType, List<? extends TypeDescription> parameterTypes) {
            this.returnType = returnType;
            this.parameterTypes = parameterTypes;
        }

        public TypeDescription getReturnType() {
            return this.returnType;
        }

        public List<TypeDescription> getParameterTypes() {
            return this.parameterTypes;
        }

        @CachedReturnPlugin.Enhance(value="hashCode")
        public int hashCode() {
            int n;
            int n2;
            int n3 = this.hashCode;
            if (n3 != 0) {
                n2 = 0;
            } else {
                TypeToken typeToken = this;
                int result = typeToken.returnType.hashCode();
                n2 = n = (result = 31 * result + typeToken.parameterTypes.hashCode());
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
            if (!(other instanceof TypeToken)) {
                return false;
            }
            TypeToken typeToken = (TypeToken)other;
            return this.returnType.equals(typeToken.returnType) && this.parameterTypes.equals(typeToken.parameterTypes);
        }

        public String toString() {
            StringBuilder stringBuilder = new StringBuilder().append('(');
            for (TypeDescription typeDescription : this.parameterTypes) {
                stringBuilder.append(typeDescription.getDescriptor());
            }
            return stringBuilder.append(')').append(this.returnType.getDescriptor()).toString();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class SignatureToken {
        private final String name;
        private final TypeDescription returnType;
        private final List<? extends TypeDescription> parameterTypes;
        private transient /* synthetic */ int hashCode;

        public SignatureToken(String name, TypeDescription returnType, TypeDescription ... parameterType) {
            this(name, returnType, Arrays.asList(parameterType));
        }

        public SignatureToken(String name, TypeDescription returnType, List<? extends TypeDescription> parameterTypes) {
            this.name = name;
            this.returnType = returnType;
            this.parameterTypes = parameterTypes;
        }

        public String getName() {
            return this.name;
        }

        public TypeDescription getReturnType() {
            return this.returnType;
        }

        public List<TypeDescription> getParameterTypes() {
            return this.parameterTypes;
        }

        public TypeToken asTypeToken() {
            return new TypeToken(this.returnType, this.parameterTypes);
        }

        public String getDescriptor() {
            StringBuilder stringBuilder = new StringBuilder().append('(');
            for (TypeDescription typeDescription : this.parameterTypes) {
                stringBuilder.append(typeDescription.getDescriptor());
            }
            return stringBuilder.append(')').append(this.returnType.getDescriptor()).toString();
        }

        @CachedReturnPlugin.Enhance(value="hashCode")
        public int hashCode() {
            int n;
            int n2;
            int n3 = this.hashCode;
            if (n3 != 0) {
                n2 = 0;
            } else {
                SignatureToken signatureToken = this;
                int result = signatureToken.name.hashCode();
                result = 31 * result + signatureToken.returnType.hashCode();
                n2 = n = (result = 31 * result + signatureToken.parameterTypes.hashCode());
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
            if (!(other instanceof SignatureToken)) {
                return false;
            }
            SignatureToken signatureToken = (SignatureToken)other;
            return this.name.equals(signatureToken.name) && this.returnType.equals(signatureToken.returnType) && this.parameterTypes.equals(signatureToken.parameterTypes);
        }

        public String toString() {
            StringBuilder stringBuilder = new StringBuilder().append(this.returnType).append(' ').append(this.name).append('(');
            boolean first = true;
            for (TypeDescription typeDescription : this.parameterTypes) {
                if (first) {
                    first = false;
                } else {
                    stringBuilder.append(',');
                }
                stringBuilder.append(typeDescription);
            }
            return stringBuilder.append(')').toString();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class Token
    implements ByteCodeElement.Token<Token> {
        private final String name;
        private final int modifiers;
        private final List<? extends TypeVariableToken> typeVariableTokens;
        private final TypeDescription.Generic returnType;
        private final List<? extends ParameterDescription.Token> parameterTokens;
        private final List<? extends TypeDescription.Generic> exceptionTypes;
        private final List<? extends AnnotationDescription> annotations;
        @MaybeNull
        private final AnnotationValue<?, ?> defaultValue;
        @MaybeNull
        private final TypeDescription.Generic receiverType;
        private transient /* synthetic */ int hashCode;

        public Token(int modifiers) {
            this(MethodDescription.CONSTRUCTOR_INTERNAL_NAME, modifiers, TypeDescription.Generic.OfNonGenericType.ForLoadedType.of(Void.TYPE));
        }

        public Token(String name, int modifiers, TypeDescription.Generic returnType) {
            this(name, modifiers, returnType, Collections.emptyList());
        }

        public Token(String name, int modifiers, TypeDescription.Generic returnType, List<? extends TypeDescription.Generic> parameterTypes) {
            this(name, modifiers, Collections.emptyList(), returnType, new ParameterDescription.Token.TypeList(parameterTypes), Collections.emptyList(), Collections.emptyList(), AnnotationValue.UNDEFINED, TypeDescription.Generic.UNDEFINED);
        }

        public Token(String name, int modifiers, List<? extends TypeVariableToken> typeVariableTokens, TypeDescription.Generic returnType, List<? extends ParameterDescription.Token> parameterTokens, List<? extends TypeDescription.Generic> exceptionTypes, List<? extends AnnotationDescription> annotations, @MaybeNull AnnotationValue<?, ?> defaultValue, @MaybeNull TypeDescription.Generic receiverType) {
            this.name = name;
            this.modifiers = modifiers;
            this.typeVariableTokens = typeVariableTokens;
            this.returnType = returnType;
            this.parameterTokens = parameterTokens;
            this.exceptionTypes = exceptionTypes;
            this.annotations = annotations;
            this.defaultValue = defaultValue;
            this.receiverType = receiverType;
        }

        public String getName() {
            return this.name;
        }

        public int getModifiers() {
            return this.modifiers;
        }

        public ByteCodeElement.Token.TokenList<TypeVariableToken> getTypeVariableTokens() {
            return new ByteCodeElement.Token.TokenList<TypeVariableToken>(this.typeVariableTokens);
        }

        public TypeDescription.Generic getReturnType() {
            return this.returnType;
        }

        public ByteCodeElement.Token.TokenList<ParameterDescription.Token> getParameterTokens() {
            return new ByteCodeElement.Token.TokenList<ParameterDescription.Token>(this.parameterTokens);
        }

        public TypeList.Generic getExceptionTypes() {
            return new TypeList.Generic.Explicit(this.exceptionTypes);
        }

        public AnnotationList getAnnotations() {
            return new AnnotationList.Explicit(this.annotations);
        }

        @MaybeNull
        public AnnotationValue<?, ?> getDefaultValue() {
            return this.defaultValue;
        }

        @MaybeNull
        public TypeDescription.Generic getReceiverType() {
            return this.receiverType;
        }

        @Override
        public Token accept(TypeDescription.Generic.Visitor<? extends TypeDescription.Generic> visitor) {
            return new Token(this.name, this.modifiers, this.getTypeVariableTokens().accept(visitor), this.returnType.accept(visitor), this.getParameterTokens().accept(visitor), this.getExceptionTypes().accept(visitor), this.annotations, this.defaultValue, this.receiverType == null ? TypeDescription.Generic.UNDEFINED : this.receiverType.accept(visitor));
        }

        public SignatureToken asSignatureToken(TypeDescription declaringType) {
            TypeDescription.Generic.Visitor.Reducing visitor = new TypeDescription.Generic.Visitor.Reducing(declaringType, this.typeVariableTokens);
            ArrayList<TypeDescription> parameters = new ArrayList<TypeDescription>(this.parameterTokens.size());
            for (ParameterDescription.Token token : this.parameterTokens) {
                parameters.add(token.getType().accept(visitor));
            }
            return new SignatureToken(this.name, this.returnType.accept(visitor), parameters);
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
                int result = token.name.hashCode();
                result = 31 * result + token.modifiers;
                result = 31 * result + token.typeVariableTokens.hashCode();
                result = 31 * result + token.returnType.hashCode();
                result = 31 * result + token.parameterTokens.hashCode();
                result = 31 * result + token.exceptionTypes.hashCode();
                result = 31 * result + token.annotations.hashCode();
                result = 31 * result + (token.defaultValue != null ? token.defaultValue.hashCode() : 0);
                n2 = n = (result = 31 * result + (token.receiverType != null ? token.receiverType.hashCode() : 0));
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
            if (other == null || this.getClass() != other.getClass()) {
                return false;
            }
            Token token = (Token)other;
            return this.modifiers == token.modifiers && this.name.equals(token.name) && this.typeVariableTokens.equals(token.typeVariableTokens) && this.returnType.equals(token.returnType) && this.parameterTokens.equals(token.parameterTokens) && this.exceptionTypes.equals(token.exceptionTypes) && this.annotations.equals(token.annotations) && (this.defaultValue != null ? this.defaultValue.equals(token.defaultValue) : token.defaultValue == null) && (this.receiverType != null ? this.receiverType.equals(token.receiverType) : token.receiverType == null);
        }

        public String toString() {
            return "MethodDescription.Token{name='" + this.name + '\'' + ", modifiers=" + this.modifiers + ", typeVariableTokens=" + this.typeVariableTokens + ", returnType=" + this.returnType + ", parameterTokens=" + this.parameterTokens + ", exceptionTypes=" + this.exceptionTypes + ", annotations=" + this.annotations + ", defaultValue=" + this.defaultValue + ", receiverType=" + this.receiverType + '}';
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class TypeSubstituting
    extends AbstractBase
    implements InGenericShape {
        private final TypeDescription.Generic declaringType;
        private final MethodDescription methodDescription;
        private final TypeDescription.Generic.Visitor<? extends TypeDescription.Generic> visitor;

        public TypeSubstituting(TypeDescription.Generic declaringType, MethodDescription methodDescription, TypeDescription.Generic.Visitor<? extends TypeDescription.Generic> visitor) {
            this.declaringType = declaringType;
            this.methodDescription = methodDescription;
            this.visitor = visitor;
        }

        @Override
        public TypeDescription.Generic getReturnType() {
            return this.methodDescription.getReturnType().accept(this.visitor);
        }

        @Override
        public TypeList.Generic getTypeVariables() {
            return (TypeList.Generic)this.methodDescription.getTypeVariables().accept(this.visitor).filter(ElementMatchers.ofSort(TypeDefinition.Sort.VARIABLE));
        }

        @Override
        public ParameterList<ParameterDescription.InGenericShape> getParameters() {
            return new ParameterList.TypeSubstituting(this, this.methodDescription.getParameters(), this.visitor);
        }

        @Override
        public TypeList.Generic getExceptionTypes() {
            return new TypeList.Generic.ForDetachedTypes(this.methodDescription.getExceptionTypes(), this.visitor);
        }

        @Override
        @MaybeNull
        public AnnotationValue<?, ?> getDefaultValue() {
            return this.methodDescription.getDefaultValue();
        }

        @Override
        public TypeDescription.Generic getReceiverType() {
            TypeDescription.Generic receiverType = this.methodDescription.getReceiverType();
            return receiverType == null ? TypeDescription.Generic.UNDEFINED : receiverType.accept(this.visitor);
        }

        @Override
        public AnnotationList getDeclaredAnnotations() {
            return this.methodDescription.getDeclaredAnnotations();
        }

        @Override
        @Nonnull
        public TypeDescription.Generic getDeclaringType() {
            return this.declaringType;
        }

        @Override
        public int getModifiers() {
            return this.methodDescription.getModifiers();
        }

        @Override
        public String getInternalName() {
            return this.methodDescription.getInternalName();
        }

        @Override
        public InDefinedShape asDefined() {
            return (InDefinedShape)this.methodDescription.asDefined();
        }

        @Override
        public boolean isConstructor() {
            return this.methodDescription.isConstructor();
        }

        @Override
        public boolean isMethod() {
            return this.methodDescription.isMethod();
        }

        @Override
        public boolean isTypeInitializer() {
            return this.methodDescription.isTypeInitializer();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class Latent
    extends InDefinedShape.AbstractBase {
        private final TypeDescription declaringType;
        private final String internalName;
        private final int modifiers;
        private final List<? extends TypeVariableToken> typeVariables;
        private final TypeDescription.Generic returnType;
        private final List<? extends ParameterDescription.Token> parameterTokens;
        private final List<? extends TypeDescription.Generic> exceptionTypes;
        private final List<? extends AnnotationDescription> declaredAnnotations;
        @MaybeNull
        private final AnnotationValue<?, ?> defaultValue;
        @MaybeNull
        private final TypeDescription.Generic receiverType;

        public Latent(TypeDescription declaringType, Token token) {
            this(declaringType, token.getName(), token.getModifiers(), token.getTypeVariableTokens(), token.getReturnType(), token.getParameterTokens(), token.getExceptionTypes(), token.getAnnotations(), token.getDefaultValue(), token.getReceiverType());
        }

        public Latent(TypeDescription declaringType, String internalName, int modifiers, List<? extends TypeVariableToken> typeVariables, TypeDescription.Generic returnType, List<? extends ParameterDescription.Token> parameterTokens, List<? extends TypeDescription.Generic> exceptionTypes, List<? extends AnnotationDescription> declaredAnnotations, @MaybeNull AnnotationValue<?, ?> defaultValue, @MaybeNull TypeDescription.Generic receiverType) {
            this.declaringType = declaringType;
            this.internalName = internalName;
            this.modifiers = modifiers;
            this.typeVariables = typeVariables;
            this.returnType = returnType;
            this.parameterTokens = parameterTokens;
            this.exceptionTypes = exceptionTypes;
            this.declaredAnnotations = declaredAnnotations;
            this.defaultValue = defaultValue;
            this.receiverType = receiverType;
        }

        @Override
        public TypeList.Generic getTypeVariables() {
            return TypeList.Generic.ForDetachedTypes.attachVariables(this, this.typeVariables);
        }

        @Override
        public TypeDescription.Generic getReturnType() {
            return this.returnType.accept(TypeDescription.Generic.Visitor.Substitutor.ForAttachment.of(this));
        }

        @Override
        public ParameterList<ParameterDescription.InDefinedShape> getParameters() {
            return new ParameterList.ForTokens(this, this.parameterTokens);
        }

        @Override
        public TypeList.Generic getExceptionTypes() {
            return TypeList.Generic.ForDetachedTypes.attach(this, this.exceptionTypes);
        }

        @Override
        public AnnotationList getDeclaredAnnotations() {
            return new AnnotationList.Explicit(this.declaredAnnotations);
        }

        @Override
        public String getInternalName() {
            return this.internalName;
        }

        @Override
        @Nonnull
        public TypeDescription getDeclaringType() {
            return this.declaringType;
        }

        @Override
        public int getModifiers() {
            return this.modifiers;
        }

        @Override
        @MaybeNull
        public AnnotationValue<?, ?> getDefaultValue() {
            return this.defaultValue;
        }

        @Override
        @MaybeNull
        public TypeDescription.Generic getReceiverType() {
            return this.receiverType == null ? super.getReceiverType() : this.receiverType.accept(TypeDescription.Generic.Visitor.Substitutor.ForAttachment.of(this));
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static class TypeInitializer
        extends InDefinedShape.AbstractBase {
            private final TypeDescription typeDescription;

            public TypeInitializer(TypeDescription typeDescription) {
                this.typeDescription = typeDescription;
            }

            @Override
            public TypeDescription.Generic getReturnType() {
                return TypeDescription.Generic.OfNonGenericType.ForLoadedType.of(Void.TYPE);
            }

            @Override
            public ParameterList<ParameterDescription.InDefinedShape> getParameters() {
                return new ParameterList.Empty<ParameterDescription.InDefinedShape>();
            }

            @Override
            public TypeList.Generic getExceptionTypes() {
                return new TypeList.Generic.Empty();
            }

            @Override
            @AlwaysNull
            public AnnotationValue<?, ?> getDefaultValue() {
                return AnnotationValue.UNDEFINED;
            }

            @Override
            public TypeList.Generic getTypeVariables() {
                return new TypeList.Generic.Empty();
            }

            @Override
            public AnnotationList getDeclaredAnnotations() {
                return new AnnotationList.Empty();
            }

            @Override
            @Nonnull
            public TypeDescription getDeclaringType() {
                return this.typeDescription;
            }

            @Override
            public int getModifiers() {
                return 8;
            }

            @Override
            public String getInternalName() {
                return MethodDescription.TYPE_INITIALIZER_INTERNAL_NAME;
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class ForLoadedMethod
    extends InDefinedShape.AbstractBase.ForLoadedExecutable<Method>
    implements ParameterDescription.ForLoadedParameter.ParameterAnnotationSource {
        private transient /* synthetic */ ParameterList parameters;
        private transient /* synthetic */ AnnotationList declaredAnnotations;
        private transient /* synthetic */ Annotation[][] parameterAnnotations;

        public ForLoadedMethod(Method method) {
            super(method);
        }

        @Override
        @Nonnull
        public TypeDescription getDeclaringType() {
            return TypeDescription.ForLoadedType.of(((Method)this.executable).getDeclaringClass());
        }

        @Override
        public TypeDescription.Generic getReturnType() {
            if (TypeDescription.AbstractBase.RAW_TYPES) {
                return TypeDescription.Generic.OfNonGenericType.ForLoadedType.of(((Method)this.executable).getReturnType());
            }
            return new TypeDescription.Generic.LazyProjection.ForLoadedReturnType((Method)this.executable);
        }

        @Override
        @CachedReturnPlugin.Enhance(value="parameters")
        public ParameterList<ParameterDescription.InDefinedShape> getParameters() {
            Object object;
            Object object2;
            ParameterList parameterList = this.parameters;
            if (parameterList != null) {
                object2 = null;
            } else {
                object = this;
                object2 = object = ParameterList.ForLoadedExecutable.of((Method)((ForLoadedMethod)object).executable, (ParameterDescription.ForLoadedParameter.ParameterAnnotationSource)object);
            }
            if (object == null) {
                object = this.parameters;
            } else {
                this.parameters = object;
            }
            return object;
        }

        @Override
        public TypeList.Generic getExceptionTypes() {
            if (TypeDescription.AbstractBase.RAW_TYPES) {
                return new TypeList.Generic.ForLoadedTypes(((Method)this.executable).getExceptionTypes());
            }
            return new TypeList.Generic.OfMethodExceptionTypes((Method)this.executable);
        }

        @Override
        public boolean isConstructor() {
            return false;
        }

        @Override
        public boolean isTypeInitializer() {
            return false;
        }

        @Override
        public boolean isBridge() {
            return ((Method)this.executable).isBridge();
        }

        @Override
        public boolean represents(Method method) {
            return ((Method)this.executable).equals(method) || this.equals(new ForLoadedMethod(method));
        }

        @Override
        public boolean represents(Constructor<?> constructor) {
            return false;
        }

        @Override
        public String getName() {
            return ((Method)this.executable).getName();
        }

        @Override
        public int getModifiers() {
            return ((Method)this.executable).getModifiers();
        }

        @Override
        public boolean isSynthetic() {
            return ((Method)this.executable).isSynthetic();
        }

        @Override
        public String getInternalName() {
            return ((Method)this.executable).getName();
        }

        @Override
        public String getDescriptor() {
            return Type.getMethodDescriptor((Method)this.executable);
        }

        public Method getLoadedMethod() {
            return (Method)this.executable;
        }

        @Override
        @CachedReturnPlugin.Enhance(value="declaredAnnotations")
        public AnnotationList getDeclaredAnnotations() {
            Object object;
            Object object2;
            AnnotationList annotationList = this.declaredAnnotations;
            if (annotationList != null) {
                object2 = null;
            } else {
                object = this;
                object2 = object = new AnnotationList.ForLoadedAnnotations(((Method)((ForLoadedMethod)object).executable).getDeclaredAnnotations());
            }
            if (object == null) {
                object = this.declaredAnnotations;
            } else {
                this.declaredAnnotations = object;
            }
            return object;
        }

        @Override
        @MaybeNull
        public AnnotationValue<?, ?> getDefaultValue() {
            Object value = ((Method)this.executable).getDefaultValue();
            return value == null ? AnnotationValue.UNDEFINED : AnnotationDescription.ForLoadedAnnotation.asValue(value, ((Method)this.executable).getReturnType());
        }

        @Override
        public TypeList.Generic getTypeVariables() {
            if (TypeDescription.AbstractBase.RAW_TYPES) {
                return new TypeList.Generic.Empty();
            }
            return TypeList.Generic.ForLoadedTypes.OfTypeVariables.of((GenericDeclaration)this.executable);
        }

        @Override
        @CachedReturnPlugin.Enhance(value="parameterAnnotations")
        public Annotation[][] getParameterAnnotations() {
            Annotation[][] annotationArray;
            Annotation[][] annotationArray2;
            Annotation[][] annotationArray3 = this.parameterAnnotations;
            if (annotationArray3 != null) {
                annotationArray2 = null;
            } else {
                annotationArray = this;
                annotationArray2 = annotationArray = ((Method)annotationArray.executable).getParameterAnnotations();
            }
            if (annotationArray == null) {
                annotationArray = this.parameterAnnotations;
            } else {
                this.parameterAnnotations = annotationArray;
            }
            return annotationArray;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class ForLoadedConstructor
    extends InDefinedShape.AbstractBase.ForLoadedExecutable<Constructor<?>>
    implements ParameterDescription.ForLoadedParameter.ParameterAnnotationSource {
        private transient /* synthetic */ ParameterList parameters;
        private transient /* synthetic */ AnnotationList declaredAnnotations;
        private transient /* synthetic */ Annotation[][] parameterAnnotations;

        public ForLoadedConstructor(Constructor<?> constructor) {
            super(constructor);
        }

        @Override
        @Nonnull
        public TypeDescription getDeclaringType() {
            return TypeDescription.ForLoadedType.of(((Constructor)this.executable).getDeclaringClass());
        }

        @Override
        public TypeDescription.Generic getReturnType() {
            return TypeDescription.Generic.OfNonGenericType.ForLoadedType.of(Void.TYPE);
        }

        @Override
        @CachedReturnPlugin.Enhance(value="parameters")
        public ParameterList<ParameterDescription.InDefinedShape> getParameters() {
            Object object;
            Object object2;
            ParameterList parameterList = this.parameters;
            if (parameterList != null) {
                object2 = null;
            } else {
                object = this;
                object2 = object = ParameterList.ForLoadedExecutable.of((Constructor)((ForLoadedConstructor)object).executable, (ParameterDescription.ForLoadedParameter.ParameterAnnotationSource)object);
            }
            if (object == null) {
                object = this.parameters;
            } else {
                this.parameters = object;
            }
            return object;
        }

        @Override
        public TypeList.Generic getExceptionTypes() {
            return new TypeList.Generic.OfConstructorExceptionTypes((Constructor)this.executable);
        }

        @Override
        public boolean isConstructor() {
            return true;
        }

        @Override
        public boolean isTypeInitializer() {
            return false;
        }

        @Override
        public boolean represents(Method method) {
            return false;
        }

        @Override
        public boolean represents(Constructor<?> constructor) {
            return ((Constructor)this.executable).equals(constructor) || this.equals(new ForLoadedConstructor(constructor));
        }

        @Override
        public String getName() {
            return ((Constructor)this.executable).getName();
        }

        @Override
        public int getModifiers() {
            return ((Constructor)this.executable).getModifiers();
        }

        @Override
        public boolean isSynthetic() {
            return ((Constructor)this.executable).isSynthetic();
        }

        @Override
        public String getInternalName() {
            return MethodDescription.CONSTRUCTOR_INTERNAL_NAME;
        }

        @Override
        public String getDescriptor() {
            return Type.getConstructorDescriptor((Constructor)this.executable);
        }

        @Override
        @AlwaysNull
        public AnnotationValue<?, ?> getDefaultValue() {
            return AnnotationValue.UNDEFINED;
        }

        @Override
        @CachedReturnPlugin.Enhance(value="declaredAnnotations")
        public AnnotationList getDeclaredAnnotations() {
            Object object;
            Object object2;
            AnnotationList annotationList = this.declaredAnnotations;
            if (annotationList != null) {
                object2 = null;
            } else {
                object = this;
                object2 = object = new AnnotationList.ForLoadedAnnotations(((Constructor)((ForLoadedConstructor)object).executable).getDeclaredAnnotations());
            }
            if (object == null) {
                object = this.declaredAnnotations;
            } else {
                this.declaredAnnotations = object;
            }
            return object;
        }

        @Override
        public TypeList.Generic getTypeVariables() {
            return TypeList.Generic.ForLoadedTypes.OfTypeVariables.of((GenericDeclaration)this.executable);
        }

        @Override
        @CachedReturnPlugin.Enhance(value="parameterAnnotations")
        public Annotation[][] getParameterAnnotations() {
            Annotation[][] annotationArray;
            Annotation[][] annotationArray2;
            Annotation[][] annotationArray3 = this.parameterAnnotations;
            if (annotationArray3 != null) {
                annotationArray2 = null;
            } else {
                annotationArray = this;
                annotationArray2 = annotationArray = ((Constructor)annotationArray.executable).getParameterAnnotations();
            }
            if (annotationArray == null) {
                annotationArray = this.parameterAnnotations;
            } else {
                this.parameterAnnotations = annotationArray;
            }
            return annotationArray;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static abstract class AbstractBase
    extends TypeVariableSource.AbstractBase
    implements MethodDescription {
        private static final int SOURCE_MODIFIERS = 1343;
        private transient /* synthetic */ int hashCode;

        @Override
        public int getStackSize() {
            return this.getParameters().asTypeList().getStackSize() + (this.isStatic() ? 0 : 1);
        }

        @Override
        public boolean isMethod() {
            return !this.isConstructor() && !this.isTypeInitializer();
        }

        @Override
        public boolean isConstructor() {
            return MethodDescription.CONSTRUCTOR_INTERNAL_NAME.equals(this.getInternalName());
        }

        @Override
        public boolean isTypeInitializer() {
            return MethodDescription.TYPE_INITIALIZER_INTERNAL_NAME.equals(this.getInternalName());
        }

        @Override
        public boolean represents(Method method) {
            return this.equals(new ForLoadedMethod(method));
        }

        @Override
        public boolean represents(Constructor<?> constructor) {
            return this.equals(new ForLoadedConstructor(constructor));
        }

        @Override
        public String getName() {
            return this.isMethod() ? this.getInternalName() : this.getDeclaringType().asErasure().getName();
        }

        @Override
        public String getActualName() {
            return this.isMethod() ? this.getName() : "";
        }

        @Override
        public String getDescriptor() {
            StringBuilder descriptor = new StringBuilder().append('(');
            for (TypeDescription parameterType : this.getParameters().asTypeList().asErasures()) {
                descriptor.append(parameterType.getDescriptor());
            }
            return descriptor.append(')').append(this.getReturnType().asErasure().getDescriptor()).toString();
        }

        @Override
        @MaybeNull
        public String getGenericSignature() {
            try {
                SignatureWriter signatureWriter = new SignatureWriter();
                boolean generic = false;
                for (TypeDescription.Generic typeVariable : this.getTypeVariables()) {
                    signatureWriter.visitFormalTypeParameter(typeVariable.getSymbol());
                    boolean classBound = true;
                    for (TypeDescription.Generic upperBound : typeVariable.getUpperBounds()) {
                        upperBound.accept(new TypeDescription.Generic.Visitor.ForSignatureVisitor(classBound ? signatureWriter.visitClassBound() : signatureWriter.visitInterfaceBound()));
                        classBound = false;
                    }
                    generic = true;
                }
                for (TypeDescription.Generic parameterType : this.getParameters().asTypeList()) {
                    parameterType.accept(new TypeDescription.Generic.Visitor.ForSignatureVisitor(signatureWriter.visitParameterType()));
                    generic = generic || !parameterType.getSort().isNonGeneric();
                }
                TypeDescription.Generic returnType = this.getReturnType();
                returnType.accept(new TypeDescription.Generic.Visitor.ForSignatureVisitor(signatureWriter.visitReturnType()));
                generic = generic || !returnType.getSort().isNonGeneric();
                TypeList.Generic exceptionTypes = this.getExceptionTypes();
                if (!((TypeList.Generic)exceptionTypes.filter(ElementMatchers.not(ElementMatchers.ofSort(TypeDefinition.Sort.NON_GENERIC)))).isEmpty()) {
                    for (TypeDescription.Generic exceptionType : exceptionTypes) {
                        exceptionType.accept(new TypeDescription.Generic.Visitor.ForSignatureVisitor(signatureWriter.visitExceptionType()));
                        generic = generic || !exceptionType.getSort().isNonGeneric();
                    }
                }
                return generic ? signatureWriter.toString() : NON_GENERIC_SIGNATURE;
            }
            catch (GenericSignatureFormatError ignored) {
                return NON_GENERIC_SIGNATURE;
            }
        }

        @Override
        public int getActualModifiers() {
            return this.getModifiers() | (this.getDeclaredAnnotations().isAnnotationPresent(Deprecated.class) ? 131072 : 0);
        }

        @Override
        public int getActualModifiers(boolean manifest) {
            return manifest ? this.getActualModifiers() & 0xFFFFFAFF : this.getActualModifiers() & 0xFFFFFEFF | 0x400;
        }

        @Override
        public int getActualModifiers(boolean manifest, Visibility visibility) {
            return ModifierContributor.Resolver.of(Collections.singleton(this.getVisibility().expandTo(visibility))).resolve(this.getActualModifiers(manifest));
        }

        @Override
        public boolean isVisibleTo(TypeDescription typeDescription) {
            return (this.isVirtual() || this.getDeclaringType().asErasure().isVisibleTo(typeDescription)) && (this.isPublic() || typeDescription.equals(this.getDeclaringType().asErasure()) || this.isProtected() && this.getDeclaringType().asErasure().isAssignableFrom(typeDescription) || !this.isPrivate() && typeDescription.isSamePackage(this.getDeclaringType().asErasure()) || this.isPrivate() && typeDescription.isNestMateOf(this.getDeclaringType().asErasure()));
        }

        @Override
        public boolean isAccessibleTo(TypeDescription typeDescription) {
            return (this.isVirtual() || this.getDeclaringType().asErasure().isVisibleTo(typeDescription)) && (this.isPublic() || typeDescription.equals(this.getDeclaringType().asErasure()) || !this.isPrivate() && typeDescription.isSamePackage(this.getDeclaringType().asErasure())) || this.isPrivate() && typeDescription.isNestMateOf(this.getDeclaringType().asErasure());
        }

        @Override
        public boolean isVirtual() {
            return !this.isConstructor() && !this.isPrivate() && !this.isStatic() && !this.isTypeInitializer();
        }

        @Override
        public boolean isDefaultMethod() {
            return !this.isAbstract() && !this.isBridge() && this.getDeclaringType().isInterface();
        }

        @Override
        public boolean isSpecializableFor(TypeDescription targetType) {
            if (this.isStatic()) {
                return false;
            }
            if (this.isPrivate() || this.isConstructor()) {
                return this.getDeclaringType().equals(targetType);
            }
            return !this.isAbstract() && this.getDeclaringType().asErasure().isAssignableFrom(targetType);
        }

        @Override
        @MaybeNull
        public <T> T getDefaultValue(Class<T> type) {
            return type.cast(this.getDefaultValue());
        }

        @Override
        public boolean isInvokableOn(TypeDescription typeDescription) {
            return !this.isStatic() && !this.isTypeInitializer() && this.isVisibleTo(typeDescription) && (this.isVirtual() ? this.getDeclaringType().asErasure().isAssignableFrom(typeDescription) : this.getDeclaringType().asErasure().equals(typeDescription));
        }

        @SuppressFBWarnings(value={"NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE"}, justification="Assuming component type for array type.")
        private boolean isBootstrap(TypeDescription bootstrapped) {
            TypeList parameterTypes = this.getParameters().asTypeList().asErasures();
            switch (parameterTypes.size()) {
                case 0: {
                    return false;
                }
                case 1: {
                    return ((TypeDescription)parameterTypes.getOnly()).represents((java.lang.reflect.Type)((Object)Object[].class));
                }
                case 2: {
                    return JavaType.METHOD_HANDLES_LOOKUP.getTypeStub().isAssignableTo((TypeDescription)parameterTypes.get(0)) && ((TypeDescription)parameterTypes.get(1)).represents((java.lang.reflect.Type)((Object)Object[].class));
                }
                case 3: {
                    return JavaType.METHOD_HANDLES_LOOKUP.getTypeStub().isAssignableTo((TypeDescription)parameterTypes.get(0)) && (((TypeDescription)parameterTypes.get(1)).represents((java.lang.reflect.Type)((Object)Object.class)) || ((TypeDescription)parameterTypes.get(1)).represents((java.lang.reflect.Type)((Object)String.class))) && (((TypeDescription)parameterTypes.get(2)).isArray() && ((TypeDescription)parameterTypes.get(2)).getComponentType().isAssignableFrom(bootstrapped) || ((TypeDescription)parameterTypes.get(2)).isAssignableFrom(bootstrapped));
                }
            }
            return JavaType.METHOD_HANDLES_LOOKUP.getTypeStub().isAssignableTo((TypeDescription)parameterTypes.get(0)) && (((TypeDescription)parameterTypes.get(1)).represents((java.lang.reflect.Type)((Object)Object.class)) || ((TypeDescription)parameterTypes.get(1)).represents((java.lang.reflect.Type)((Object)String.class))) && ((TypeDescription)parameterTypes.get(2)).isAssignableFrom(bootstrapped);
        }

        @SuppressFBWarnings(value={"NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE"}, justification="Assuming component type for array type.")
        private boolean isBootstrapping(List<? extends TypeDefinition> arguments) {
            TypeList targets = this.getParameters().asTypeList().asErasures();
            if (targets.size() < 4) {
                if (arguments.isEmpty()) {
                    return true;
                }
                if (((TypeDescription)targets.get(targets.size() - 1)).isArray()) {
                    for (TypeDefinition typeDefinition : arguments) {
                        if (typeDefinition.asErasure().isAssignableTo(((TypeDescription)targets.get(targets.size() - 1)).getComponentType())) continue;
                        return false;
                    }
                    return true;
                }
                return false;
            }
            Iterator iterator = ((TypeList)targets.subList(3, targets.size())).iterator();
            for (TypeDefinition typeDefinition : arguments) {
                if (!iterator.hasNext()) {
                    return false;
                }
                TypeDescription target = (TypeDescription)iterator.next();
                if (!iterator.hasNext() && target.isArray()) {
                    return true;
                }
                if (typeDefinition.asErasure().isAssignableTo(target)) continue;
                return false;
            }
            if (iterator.hasNext()) {
                return ((TypeDescription)iterator.next()).isArray() && !iterator.hasNext();
            }
            return true;
        }

        @Override
        public boolean isInvokeBootstrap() {
            TypeDescription returnType = this.getReturnType().asErasure();
            if (this.isMethod() && (!this.isStatic() || !JavaType.CALL_SITE.getTypeStub().isAssignableFrom(returnType) && !JavaType.CALL_SITE.getTypeStub().isAssignableTo(returnType)) || this.isConstructor() && !JavaType.CALL_SITE.getTypeStub().isAssignableFrom(this.getDeclaringType().asErasure())) {
                return false;
            }
            return this.isBootstrap(JavaType.METHOD_TYPE.getTypeStub());
        }

        @Override
        public boolean isInvokeBootstrap(List<? extends TypeDefinition> arguments) {
            return this.isInvokeBootstrap() && this.isBootstrapping(arguments);
        }

        @Override
        public boolean isConstantBootstrap() {
            return this.isBootstrap(TypeDescription.ForLoadedType.of(Class.class));
        }

        @Override
        public boolean isConstantBootstrap(List<? extends TypeDefinition> arguments) {
            return this.isConstantBootstrap() && this.isBootstrapping(arguments);
        }

        @Override
        public boolean isDefaultValue() {
            return !this.isConstructor() && !this.isStatic() && this.getReturnType().asErasure().isAnnotationReturnType() && this.getParameters().isEmpty();
        }

        @Override
        @SuppressFBWarnings(value={"NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE"}, justification="Assuming component type for array type.")
        public boolean isDefaultValue(AnnotationValue<?, ?> annotationValue) {
            if (!this.isDefaultValue()) {
                return false;
            }
            TypeDescription returnType = this.getReturnType().asErasure();
            Object value = annotationValue.resolve();
            return returnType.represents(Boolean.TYPE) && value instanceof Boolean || returnType.represents(Byte.TYPE) && value instanceof Byte || returnType.represents(Character.TYPE) && value instanceof Character || returnType.represents(Short.TYPE) && value instanceof Short || returnType.represents(Integer.TYPE) && value instanceof Integer || returnType.represents(Long.TYPE) && value instanceof Long || returnType.represents(Float.TYPE) && value instanceof Float || returnType.represents(Double.TYPE) && value instanceof Double || returnType.represents((java.lang.reflect.Type)((Object)String.class)) && value instanceof String || returnType.isAssignableTo(Enum.class) && value instanceof EnumerationDescription && AbstractBase.isEnumerationType(returnType, (EnumerationDescription)value) || returnType.isAssignableTo(Annotation.class) && value instanceof AnnotationDescription && AbstractBase.isAnnotationType(returnType, (AnnotationDescription)value) || returnType.represents((java.lang.reflect.Type)((Object)Class.class)) && value instanceof TypeDescription || returnType.represents((java.lang.reflect.Type)((Object)boolean[].class)) && value instanceof boolean[] || returnType.represents((java.lang.reflect.Type)((Object)byte[].class)) && value instanceof byte[] || returnType.represents((java.lang.reflect.Type)((Object)char[].class)) && value instanceof char[] || returnType.represents((java.lang.reflect.Type)((Object)short[].class)) && value instanceof short[] || returnType.represents((java.lang.reflect.Type)((Object)int[].class)) && value instanceof int[] || returnType.represents((java.lang.reflect.Type)((Object)long[].class)) && value instanceof long[] || returnType.represents((java.lang.reflect.Type)((Object)float[].class)) && value instanceof float[] || returnType.represents((java.lang.reflect.Type)((Object)double[].class)) && value instanceof double[] || returnType.represents((java.lang.reflect.Type)((Object)String[].class)) && value instanceof String[] || returnType.isAssignableTo(Enum[].class) && value instanceof EnumerationDescription[] && AbstractBase.isEnumerationType(returnType.getComponentType(), (EnumerationDescription[])value) || returnType.isAssignableTo(Annotation[].class) && value instanceof AnnotationDescription[] && AbstractBase.isAnnotationType(returnType.getComponentType(), (AnnotationDescription[])value) || returnType.represents((java.lang.reflect.Type)((Object)Class[].class)) && value instanceof TypeDescription[];
        }

        private static boolean isEnumerationType(TypeDescription enumerationType, EnumerationDescription ... enumerationDescription) {
            for (EnumerationDescription anEnumerationDescription : enumerationDescription) {
                if (anEnumerationDescription.getEnumerationType().equals(enumerationType)) continue;
                return false;
            }
            return true;
        }

        private static boolean isAnnotationType(TypeDescription annotationType, AnnotationDescription ... annotationDescription) {
            for (AnnotationDescription anAnnotationDescription : annotationDescription) {
                if (anAnnotationDescription.getAnnotationType().equals(annotationType)) continue;
                return false;
            }
            return true;
        }

        @Override
        @MaybeNull
        public TypeVariableSource getEnclosingSource() {
            return this.isStatic() ? TypeVariableSource.UNDEFINED : this.getDeclaringType().asErasure();
        }

        @Override
        public boolean isInferrable() {
            return true;
        }

        @Override
        public <T> T accept(TypeVariableSource.Visitor<T> visitor) {
            return visitor.onMethod((InDefinedShape)this.asDefined());
        }

        @Override
        public boolean isGenerified() {
            return !this.getTypeVariables().isEmpty();
        }

        @Override
        public Token asToken(ElementMatcher<? super TypeDescription> matcher) {
            TypeDescription.Generic receiverType = this.getReceiverType();
            return new Token(this.getInternalName(), this.getModifiers(), this.getTypeVariables().asTokenList(matcher), this.getReturnType().accept(new TypeDescription.Generic.Visitor.Substitutor.ForDetachment(matcher)), this.getParameters().asTokenList(matcher), this.getExceptionTypes().accept(new TypeDescription.Generic.Visitor.Substitutor.ForDetachment(matcher)), this.getDeclaredAnnotations(), this.getDefaultValue(), receiverType == null ? TypeDescription.Generic.UNDEFINED : receiverType.accept(new TypeDescription.Generic.Visitor.Substitutor.ForDetachment(matcher)));
        }

        @Override
        public SignatureToken asSignatureToken() {
            return new SignatureToken(this.getInternalName(), this.getReturnType().asErasure(), this.getParameters().asTypeList().asErasures());
        }

        @Override
        public TypeToken asTypeToken() {
            return new TypeToken(this.getReturnType().asErasure(), this.getParameters().asTypeList().asErasures());
        }

        @Override
        public boolean isBridgeCompatible(TypeToken typeToken) {
            TypeDescription bridgeReturnType;
            TypeList types = this.getParameters().asTypeList().asErasures();
            List<TypeDescription> bridgeTypes = typeToken.getParameterTypes();
            if (types.size() != bridgeTypes.size()) {
                return false;
            }
            for (int index = 0; index < types.size(); ++index) {
                if (((TypeDescription)types.get(index)).equals(bridgeTypes.get(index)) || !((TypeDescription)types.get(index)).isPrimitive() && !bridgeTypes.get(index).isPrimitive()) continue;
                return false;
            }
            TypeDescription returnType = this.getReturnType().asErasure();
            return returnType.equals(bridgeReturnType = typeToken.getReturnType()) || !returnType.isPrimitive() && !bridgeReturnType.isPrimitive();
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
                int hashCode = 17 + abstractBase.getDeclaringType().hashCode();
                hashCode = 31 * hashCode + abstractBase.getInternalName().hashCode();
                hashCode = 31 * hashCode + abstractBase.getReturnType().asErasure().hashCode();
                n2 = n = 31 * hashCode + abstractBase.getParameters().asTypeList().asErasures().hashCode();
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
            if (!(other instanceof MethodDescription)) {
                return false;
            }
            MethodDescription methodDescription = (MethodDescription)other;
            return this.getInternalName().equals(methodDescription.getInternalName()) && this.getDeclaringType().equals(methodDescription.getDeclaringType()) && this.getReturnType().asErasure().equals(methodDescription.getReturnType().asErasure()) && this.getParameters().asTypeList().asErasures().equals(methodDescription.getParameters().asTypeList().asErasures());
        }

        @Override
        public String toGenericString() {
            StringBuilder stringBuilder = new StringBuilder();
            int modifiers = this.getModifiers() & 0x53F;
            if (modifiers != 0) {
                stringBuilder.append(Modifier.toString(modifiers)).append(' ');
            }
            if (this.isMethod()) {
                stringBuilder.append(this.getReturnType().getActualName()).append(' ');
                stringBuilder.append(this.getDeclaringType().asErasure().getActualName()).append('.');
            }
            stringBuilder.append(this.getName()).append('(');
            boolean first = true;
            for (TypeDescription.Generic typeDescription : this.getParameters().asTypeList()) {
                if (!first) {
                    stringBuilder.append(',');
                } else {
                    first = false;
                }
                stringBuilder.append(typeDescription.getActualName());
            }
            stringBuilder.append(')');
            TypeList.Generic exceptionTypes = this.getExceptionTypes();
            if (!exceptionTypes.isEmpty()) {
                stringBuilder.append(" throws ");
                first = true;
                for (TypeDescription.Generic typeDescription : exceptionTypes) {
                    if (!first) {
                        stringBuilder.append(',');
                    } else {
                        first = false;
                    }
                    stringBuilder.append(typeDescription.getActualName());
                }
            }
            return stringBuilder.toString();
        }

        public String toString() {
            StringBuilder stringBuilder = new StringBuilder();
            int modifiers = this.getModifiers() & 0x53F;
            if (modifiers != 0) {
                stringBuilder.append(Modifier.toString(modifiers)).append(' ');
            }
            if (this.isMethod()) {
                stringBuilder.append(this.getReturnType().asErasure().getActualName()).append(' ');
                stringBuilder.append(this.getDeclaringType().asErasure().getActualName()).append('.');
            }
            stringBuilder.append(this.getName()).append('(');
            boolean first = true;
            for (TypeDescription typeDescription : this.getParameters().asTypeList().asErasures()) {
                if (!first) {
                    stringBuilder.append(',');
                } else {
                    first = false;
                }
                stringBuilder.append(typeDescription.getActualName());
            }
            stringBuilder.append(')');
            TypeList exceptionTypes = this.getExceptionTypes().asErasures();
            if (!exceptionTypes.isEmpty()) {
                stringBuilder.append(" throws ");
                first = true;
                for (TypeDescription typeDescription : exceptionTypes) {
                    if (!first) {
                        stringBuilder.append(',');
                    } else {
                        first = false;
                    }
                    stringBuilder.append(typeDescription.getActualName());
                }
            }
            return stringBuilder.toString();
        }

        @Override
        protected String toSafeString() {
            StringBuilder stringBuilder = new StringBuilder();
            int modifiers = this.getModifiers() & 0x53F;
            if (modifiers != 0) {
                stringBuilder.append(Modifier.toString(modifiers)).append(' ');
            }
            if (this.isMethod()) {
                stringBuilder.append(this.getReturnType().asErasure().getActualName()).append(' ');
                stringBuilder.append(this.getDeclaringType().asErasure().getActualName()).append('.');
            }
            return stringBuilder.append(this.getName()).append("(?)").toString();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static interface InDefinedShape
    extends MethodDescription {
        @Override
        @Nonnull
        public TypeDescription getDeclaringType();

        public ParameterList<ParameterDescription.InDefinedShape> getParameters();

        public static abstract class AbstractBase
        extends net.bytebuddy.description.method.MethodDescription$AbstractBase
        implements InDefinedShape {
            public InDefinedShape asDefined() {
                return this;
            }

            @MaybeNull
            public TypeDescription.Generic getReceiverType() {
                if (this.isStatic()) {
                    return TypeDescription.Generic.UNDEFINED;
                }
                if (this.isConstructor()) {
                    TypeDefinition declaringType = this.getDeclaringType();
                    TypeDescription enclosingDeclaringType = this.getDeclaringType().getEnclosingType();
                    if (enclosingDeclaringType == null) {
                        return TypeDescription.Generic.OfParameterizedType.ForGenerifiedErasure.of((TypeDescription)declaringType);
                    }
                    return declaringType.isStatic() ? enclosingDeclaringType.asGenericType() : TypeDescription.Generic.OfParameterizedType.ForGenerifiedErasure.of(enclosingDeclaringType);
                }
                return TypeDescription.Generic.OfParameterizedType.ForGenerifiedErasure.of((TypeDescription)this.getDeclaringType());
            }

            @JavaDispatcher.Proxied(value="java.lang.reflect.Executable")
            protected static interface Executable {
                @MaybeNull
                @JavaDispatcher.Defaults
                @JavaDispatcher.Proxied(value="getAnnotatedReceiverType")
                public AnnotatedElement getAnnotatedReceiverType(Object var1);
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            protected static abstract class ForLoadedExecutable<T extends AnnotatedElement>
            extends AbstractBase {
                protected static final Executable EXECUTABLE;
                protected final T executable;
                private static final boolean ACCESS_CONTROLLER;

                protected ForLoadedExecutable(T executable) {
                    this.executable = executable;
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
                public TypeDescription.Generic getReceiverType() {
                    AnnotatedElement element = EXECUTABLE.getAnnotatedReceiverType(this.executable);
                    return element == null ? super.getReceiverType() : TypeDefinition.Sort.describeAnnotated(element);
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
                    EXECUTABLE = ForLoadedExecutable.doPrivileged(JavaDispatcher.of(Executable.class));
                }
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static interface InGenericShape
    extends MethodDescription {
        @Override
        @Nonnull
        public TypeDescription.Generic getDeclaringType();

        public ParameterList<ParameterDescription.InGenericShape> getParameters();
    }
}

