/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 *  javax.annotation.Nonnull
 */
package net.bytebuddy.dynamic;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nonnull;
import net.bytebuddy.build.HashCodeAndEqualsPlugin;
import net.bytebuddy.description.annotation.AnnotationList;
import net.bytebuddy.description.annotation.AnnotationValue;
import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.ParameterDescription;
import net.bytebuddy.description.method.ParameterList;
import net.bytebuddy.description.modifier.ModifierContributor;
import net.bytebuddy.description.type.TypeDefinition;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.description.type.TypeList;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.nullability.MaybeNull;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface Transformer<T> {
    public T transform(TypeDescription var1, T var2);

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @HashCodeAndEqualsPlugin.Enhance
    public static class Compound<S>
    implements Transformer<S> {
        private final List<Transformer<S>> transformers = new ArrayList<Transformer<S>>();

        public Compound(Transformer<S> ... transformer) {
            this(Arrays.asList(transformer));
        }

        public Compound(List<? extends Transformer<S>> transformers) {
            for (Transformer<S> transformer : transformers) {
                if (transformer instanceof Compound) {
                    this.transformers.addAll(((Compound)transformer).transformers);
                    continue;
                }
                if (transformer instanceof NoOp) continue;
                this.transformers.add(transformer);
            }
        }

        @Override
        public S transform(TypeDescription instrumentedType, S target) {
            for (Transformer<S> transformer : this.transformers) {
                target = transformer.transform(instrumentedType, target);
            }
            return target;
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
            return ((Object)this.transformers).equals(((Compound)object).transformers);
        }

        public int hashCode() {
            return this.getClass().hashCode() * 31 + ((Object)this.transformers).hashCode();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @HashCodeAndEqualsPlugin.Enhance
    public static class ForMethod
    implements Transformer<MethodDescription> {
        private final Transformer<MethodDescription.Token> transformer;

        public ForMethod(Transformer<MethodDescription.Token> transformer) {
            this.transformer = transformer;
        }

        public static Transformer<MethodDescription> withModifiers(ModifierContributor.ForMethod ... modifierContributor) {
            return ForMethod.withModifiers(Arrays.asList(modifierContributor));
        }

        public static Transformer<MethodDescription> withModifiers(List<? extends ModifierContributor.ForMethod> modifierContributors) {
            return new ForMethod(new MethodModifierTransformer(ModifierContributor.Resolver.of(modifierContributors)));
        }

        @Override
        public MethodDescription transform(TypeDescription instrumentedType, MethodDescription methodDescription) {
            return new TransformedMethod(instrumentedType, methodDescription.getDeclaringType(), this.transformer.transform(instrumentedType, (MethodDescription.Token)methodDescription.asToken(ElementMatchers.none())), (MethodDescription.InDefinedShape)methodDescription.asDefined());
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
            return this.transformer.equals(((ForMethod)object).transformer);
        }

        public int hashCode() {
            return this.getClass().hashCode() * 31 + this.transformer.hashCode();
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        protected static class TransformedMethod
        extends MethodDescription.AbstractBase {
            private final TypeDescription instrumentedType;
            private final TypeDefinition declaringType;
            private final MethodDescription.Token token;
            private final MethodDescription.InDefinedShape methodDescription;

            protected TransformedMethod(TypeDescription instrumentedType, TypeDefinition declaringType, MethodDescription.Token token, MethodDescription.InDefinedShape methodDescription) {
                this.instrumentedType = instrumentedType;
                this.declaringType = declaringType;
                this.token = token;
                this.methodDescription = methodDescription;
            }

            @Override
            public TypeList.Generic getTypeVariables() {
                return new TypeList.Generic.ForDetachedTypes.OfTypeVariables(this, this.token.getTypeVariableTokens(), new AttachmentVisitor());
            }

            @Override
            public TypeDescription.Generic getReturnType() {
                return this.token.getReturnType().accept(new AttachmentVisitor());
            }

            @Override
            public ParameterList<?> getParameters() {
                return new TransformedParameterList();
            }

            @Override
            public TypeList.Generic getExceptionTypes() {
                return new TypeList.Generic.ForDetachedTypes(this.token.getExceptionTypes(), new AttachmentVisitor());
            }

            @Override
            public AnnotationList getDeclaredAnnotations() {
                return this.token.getAnnotations();
            }

            @Override
            public String getInternalName() {
                return this.token.getName();
            }

            @Override
            @Nonnull
            public TypeDefinition getDeclaringType() {
                return this.declaringType;
            }

            @Override
            public int getModifiers() {
                return this.token.getModifiers();
            }

            @Override
            @MaybeNull
            public AnnotationValue<?, ?> getDefaultValue() {
                return this.token.getDefaultValue();
            }

            @Override
            public MethodDescription.InDefinedShape asDefined() {
                return this.methodDescription;
            }

            @Override
            public TypeDescription.Generic getReceiverType() {
                TypeDescription.Generic receiverType = this.token.getReceiverType();
                return receiverType == null ? TypeDescription.Generic.UNDEFINED : receiverType.accept(new AttachmentVisitor());
            }

            @HashCodeAndEqualsPlugin.Enhance(includeSyntheticFields=true)
            protected class AttachmentVisitor
            extends TypeDescription.Generic.Visitor.Substitutor.WithoutTypeSubstitution {
                protected AttachmentVisitor() {
                }

                public TypeDescription.Generic onTypeVariable(TypeDescription.Generic typeVariable) {
                    TypeList.Generic candidates = (TypeList.Generic)TransformedMethod.this.getTypeVariables().filter(ElementMatchers.named(typeVariable.getSymbol()));
                    return new TypeDescription.Generic.OfTypeVariable.WithAnnotationOverlay(candidates.isEmpty() ? TransformedMethod.this.instrumentedType.findExpectedVariable(typeVariable.getSymbol()) : (TypeDescription.Generic)candidates.getOnly(), typeVariable);
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
                    return TransformedMethod.this.equals(((AttachmentVisitor)object).TransformedMethod.this);
                }

                public int hashCode() {
                    return this.getClass().hashCode() * 31 + TransformedMethod.this.hashCode();
                }
            }

            protected class TransformedParameter
            extends ParameterDescription.AbstractBase {
                private final int index;
                private final ParameterDescription.Token parameterToken;

                protected TransformedParameter(int index, ParameterDescription.Token parameterToken) {
                    this.index = index;
                    this.parameterToken = parameterToken;
                }

                public TypeDescription.Generic getType() {
                    return this.parameterToken.getType().accept(new AttachmentVisitor());
                }

                public MethodDescription getDeclaringMethod() {
                    return TransformedMethod.this;
                }

                public int getIndex() {
                    return this.index;
                }

                public boolean isNamed() {
                    return this.parameterToken.getName() != null;
                }

                public boolean hasModifiers() {
                    return this.parameterToken.getModifiers() != null;
                }

                public String getName() {
                    String name = this.parameterToken.getName();
                    return name == null ? super.getName() : name;
                }

                public int getModifiers() {
                    Integer modifiers = this.parameterToken.getModifiers();
                    return modifiers == null ? super.getModifiers() : modifiers.intValue();
                }

                public AnnotationList getDeclaredAnnotations() {
                    return this.parameterToken.getAnnotations();
                }

                public ParameterDescription.InDefinedShape asDefined() {
                    return (ParameterDescription.InDefinedShape)TransformedMethod.this.methodDescription.getParameters().get(this.index);
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            protected class TransformedParameterList
            extends ParameterList.AbstractBase<ParameterDescription> {
                protected TransformedParameterList() {
                }

                @Override
                public ParameterDescription get(int index) {
                    return new TransformedParameter(index, (ParameterDescription.Token)TransformedMethod.this.token.getParameterTokens().get(index));
                }

                @Override
                public int size() {
                    return TransformedMethod.this.token.getParameterTokens().size();
                }
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        @HashCodeAndEqualsPlugin.Enhance
        protected static class MethodModifierTransformer
        implements Transformer<MethodDescription.Token> {
            private final ModifierContributor.Resolver<ModifierContributor.ForMethod> resolver;

            protected MethodModifierTransformer(ModifierContributor.Resolver<ModifierContributor.ForMethod> resolver) {
                this.resolver = resolver;
            }

            @Override
            public MethodDescription.Token transform(TypeDescription instrumentedType, MethodDescription.Token target) {
                return new MethodDescription.Token(target.getName(), this.resolver.resolve(target.getModifiers()), target.getTypeVariableTokens(), target.getReturnType(), target.getParameterTokens(), target.getExceptionTypes(), target.getAnnotations(), target.getDefaultValue(), target.getReceiverType());
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
                return this.resolver.equals(((MethodModifierTransformer)object).resolver);
            }

            public int hashCode() {
                return this.getClass().hashCode() * 31 + this.resolver.hashCode();
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @HashCodeAndEqualsPlugin.Enhance
    public static class ForField
    implements Transformer<FieldDescription> {
        private final Transformer<FieldDescription.Token> transformer;

        public ForField(Transformer<FieldDescription.Token> transformer) {
            this.transformer = transformer;
        }

        public static Transformer<FieldDescription> withModifiers(ModifierContributor.ForField ... modifierContributor) {
            return ForField.withModifiers(Arrays.asList(modifierContributor));
        }

        public static Transformer<FieldDescription> withModifiers(List<? extends ModifierContributor.ForField> modifierContributors) {
            return new ForField(new FieldModifierTransformer(ModifierContributor.Resolver.of(modifierContributors)));
        }

        @Override
        @SuppressFBWarnings(value={"NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE"}, justification="Assuming declaring type for type member.")
        public FieldDescription transform(TypeDescription instrumentedType, FieldDescription fieldDescription) {
            return new TransformedField(instrumentedType, fieldDescription.getDeclaringType(), this.transformer.transform(instrumentedType, (FieldDescription.Token)fieldDescription.asToken(ElementMatchers.none())), (FieldDescription.InDefinedShape)fieldDescription.asDefined());
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
            return this.transformer.equals(((ForField)object).transformer);
        }

        public int hashCode() {
            return this.getClass().hashCode() * 31 + this.transformer.hashCode();
        }

        protected static class TransformedField
        extends FieldDescription.AbstractBase {
            private final TypeDescription instrumentedType;
            private final TypeDefinition declaringType;
            private final FieldDescription.Token token;
            private final FieldDescription.InDefinedShape fieldDescription;

            protected TransformedField(TypeDescription instrumentedType, TypeDefinition declaringType, FieldDescription.Token token, FieldDescription.InDefinedShape fieldDescription) {
                this.instrumentedType = instrumentedType;
                this.declaringType = declaringType;
                this.token = token;
                this.fieldDescription = fieldDescription;
            }

            public TypeDescription.Generic getType() {
                return this.token.getType().accept(TypeDescription.Generic.Visitor.Substitutor.ForAttachment.of(this.instrumentedType));
            }

            public AnnotationList getDeclaredAnnotations() {
                return this.token.getAnnotations();
            }

            @Nonnull
            public TypeDefinition getDeclaringType() {
                return this.declaringType;
            }

            public int getModifiers() {
                return this.token.getModifiers();
            }

            public FieldDescription.InDefinedShape asDefined() {
                return this.fieldDescription;
            }

            public String getName() {
                return this.token.getName();
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        @HashCodeAndEqualsPlugin.Enhance
        protected static class FieldModifierTransformer
        implements Transformer<FieldDescription.Token> {
            private final ModifierContributor.Resolver<ModifierContributor.ForField> resolver;

            protected FieldModifierTransformer(ModifierContributor.Resolver<ModifierContributor.ForField> resolver) {
                this.resolver = resolver;
            }

            @Override
            public FieldDescription.Token transform(TypeDescription instrumentedType, FieldDescription.Token target) {
                return new FieldDescription.Token(target.getName(), this.resolver.resolve(target.getModifiers()), target.getType(), target.getAnnotations());
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
                return this.resolver.equals(((FieldModifierTransformer)object).resolver);
            }

            public int hashCode() {
                return this.getClass().hashCode() * 31 + this.resolver.hashCode();
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum NoOp implements Transformer<Object>
    {
        INSTANCE;


        public static <T> Transformer<T> make() {
            return INSTANCE;
        }

        @Override
        public Object transform(TypeDescription instrumentedType, Object target) {
            return target;
        }
    }
}

