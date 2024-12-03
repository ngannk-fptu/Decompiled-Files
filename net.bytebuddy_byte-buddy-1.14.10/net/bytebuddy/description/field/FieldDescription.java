/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 *  javax.annotation.Nonnull
 */
package net.bytebuddy.description.field;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.lang.reflect.Field;
import java.lang.reflect.GenericSignatureFormatError;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import net.bytebuddy.build.CachedReturnPlugin;
import net.bytebuddy.description.ByteCodeElement;
import net.bytebuddy.description.DeclaredByType;
import net.bytebuddy.description.ModifierReviewable;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.annotation.AnnotationList;
import net.bytebuddy.description.type.TypeDefinition;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.description.type.TypeVariableToken;
import net.bytebuddy.jar.asm.signature.SignatureWriter;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.utility.nullability.AlwaysNull;
import net.bytebuddy.utility.nullability.MaybeNull;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface FieldDescription
extends ModifierReviewable.ForFieldDescription,
DeclaredByType.WithMandatoryDeclaration,
ByteCodeElement.Member,
ByteCodeElement.TypeDependant<InDefinedShape, Token> {
    @AlwaysNull
    public static final Object NO_DEFAULT_VALUE = null;

    @Override
    @Nonnull
    public TypeDefinition getDeclaringType();

    public TypeDescription.Generic getType();

    public int getActualModifiers();

    public SignatureToken asSignatureToken();

    public static class SignatureToken {
        private final String name;
        private final TypeDescription type;
        private transient /* synthetic */ int hashCode;

        public SignatureToken(String name, TypeDescription type) {
            this.name = name;
            this.type = type;
        }

        public String getName() {
            return this.name;
        }

        public TypeDescription getType() {
            return this.type;
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
                n2 = n = (result = 31 * result + signatureToken.type.hashCode());
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
            return this.name.equals(signatureToken.name) && this.type.equals(signatureToken.type);
        }

        public String toString() {
            return this.type + " " + this.name;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class Token
    implements ByteCodeElement.Token<Token> {
        private final String name;
        private final int modifiers;
        private final TypeDescription.Generic type;
        private final List<? extends AnnotationDescription> annotations;
        private transient /* synthetic */ int hashCode;

        public Token(String name, int modifiers, TypeDescription.Generic type) {
            this(name, modifiers, type, Collections.emptyList());
        }

        public Token(String name, int modifiers, TypeDescription.Generic type, List<? extends AnnotationDescription> annotations) {
            this.name = name;
            this.modifiers = modifiers;
            this.type = type;
            this.annotations = annotations;
        }

        public String getName() {
            return this.name;
        }

        public TypeDescription.Generic getType() {
            return this.type;
        }

        public int getModifiers() {
            return this.modifiers;
        }

        public AnnotationList getAnnotations() {
            return new AnnotationList.Explicit(this.annotations);
        }

        @Override
        public Token accept(TypeDescription.Generic.Visitor<? extends TypeDescription.Generic> visitor) {
            return new Token(this.name, this.modifiers, this.type.accept(visitor), this.annotations);
        }

        public SignatureToken asSignatureToken(TypeDescription declaringType) {
            return new SignatureToken(this.name, this.type.accept(new TypeDescription.Generic.Visitor.Reducing(declaringType, new TypeVariableToken[0])));
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
                result = 31 * result + token.type.hashCode();
                n2 = n = (result = 31 * result + token.annotations.hashCode());
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
            return this.modifiers == token.modifiers && this.name.equals(token.name) && this.type.equals(token.type) && this.annotations.equals(token.annotations);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class TypeSubstituting
    extends AbstractBase
    implements InGenericShape {
        private final TypeDescription.Generic declaringType;
        private final FieldDescription fieldDescription;
        private final TypeDescription.Generic.Visitor<? extends TypeDescription.Generic> visitor;

        public TypeSubstituting(TypeDescription.Generic declaringType, FieldDescription fieldDescription, TypeDescription.Generic.Visitor<? extends TypeDescription.Generic> visitor) {
            this.declaringType = declaringType;
            this.fieldDescription = fieldDescription;
            this.visitor = visitor;
        }

        @Override
        public TypeDescription.Generic getType() {
            return this.fieldDescription.getType().accept(this.visitor);
        }

        @Override
        public AnnotationList getDeclaredAnnotations() {
            return this.fieldDescription.getDeclaredAnnotations();
        }

        @Override
        @Nonnull
        public TypeDescription.Generic getDeclaringType() {
            return this.declaringType;
        }

        @Override
        public int getModifiers() {
            return this.fieldDescription.getModifiers();
        }

        @Override
        public String getName() {
            return this.fieldDescription.getName();
        }

        @Override
        public InDefinedShape asDefined() {
            return (InDefinedShape)this.fieldDescription.asDefined();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class Latent
    extends InDefinedShape.AbstractBase {
        private final TypeDescription declaringType;
        private final String name;
        private final int modifiers;
        private final TypeDescription.Generic fieldType;
        private final List<? extends AnnotationDescription> declaredAnnotations;

        public Latent(TypeDescription declaringType, Token token) {
            this(declaringType, token.getName(), token.getModifiers(), token.getType(), token.getAnnotations());
        }

        public Latent(TypeDescription declaringType, String name, int modifiers, TypeDescription.Generic fieldType, List<? extends AnnotationDescription> declaredAnnotations) {
            this.declaringType = declaringType;
            this.name = name;
            this.modifiers = modifiers;
            this.fieldType = fieldType;
            this.declaredAnnotations = declaredAnnotations;
        }

        @Override
        public TypeDescription.Generic getType() {
            return this.fieldType.accept(TypeDescription.Generic.Visitor.Substitutor.ForAttachment.of(this));
        }

        @Override
        public AnnotationList getDeclaredAnnotations() {
            return new AnnotationList.Explicit(this.declaredAnnotations);
        }

        @Override
        public String getName() {
            return this.name;
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
    }

    public static class ForLoadedField
    extends InDefinedShape.AbstractBase {
        private final Field field;
        private transient /* synthetic */ AnnotationList declaredAnnotations;

        public ForLoadedField(Field field) {
            this.field = field;
        }

        public TypeDescription.Generic getType() {
            if (TypeDescription.AbstractBase.RAW_TYPES) {
                return TypeDescription.Generic.OfNonGenericType.ForLoadedType.of(this.field.getType());
            }
            return new TypeDescription.Generic.LazyProjection.ForLoadedFieldType(this.field);
        }

        @CachedReturnPlugin.Enhance(value="declaredAnnotations")
        public AnnotationList getDeclaredAnnotations() {
            Object object;
            Object object2;
            AnnotationList annotationList = this.declaredAnnotations;
            if (annotationList != null) {
                object2 = null;
            } else {
                object = this;
                object2 = object = new AnnotationList.ForLoadedAnnotations(((ForLoadedField)object).field.getDeclaredAnnotations());
            }
            if (object == null) {
                object = this.declaredAnnotations;
            } else {
                this.declaredAnnotations = object;
            }
            return object;
        }

        public String getName() {
            return this.field.getName();
        }

        @Nonnull
        public TypeDescription getDeclaringType() {
            return TypeDescription.ForLoadedType.of(this.field.getDeclaringClass());
        }

        public int getModifiers() {
            return this.field.getModifiers();
        }

        public boolean isSynthetic() {
            return this.field.isSynthetic();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static abstract class AbstractBase
    extends ModifierReviewable.AbstractBase
    implements FieldDescription {
        private transient /* synthetic */ int hashCode;

        @Override
        public String getInternalName() {
            return this.getName();
        }

        @Override
        public String getActualName() {
            return this.getName();
        }

        @Override
        public String getDescriptor() {
            return this.getType().asErasure().getDescriptor();
        }

        @Override
        @MaybeNull
        public String getGenericSignature() {
            TypeDescription.Generic fieldType = this.getType();
            try {
                return fieldType.getSort().isNonGeneric() ? NON_GENERIC_SIGNATURE : fieldType.accept(new TypeDescription.Generic.Visitor.ForSignatureVisitor(new SignatureWriter())).toString();
            }
            catch (GenericSignatureFormatError ignored) {
                return NON_GENERIC_SIGNATURE;
            }
        }

        @Override
        @SuppressFBWarnings(value={"NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE"}, justification="Assuming declaring type for type member.")
        public boolean isVisibleTo(TypeDescription typeDescription) {
            return this.getDeclaringType().asErasure().isVisibleTo(typeDescription) && (this.isPublic() || typeDescription.equals(this.getDeclaringType().asErasure()) || this.isProtected() && this.getDeclaringType().asErasure().isAssignableFrom(typeDescription) || !this.isPrivate() && typeDescription.isSamePackage(this.getDeclaringType().asErasure()) || this.isPrivate() && typeDescription.isNestMateOf(this.getDeclaringType().asErasure()));
        }

        @Override
        @SuppressFBWarnings(value={"NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE"}, justification="Assuming declaring type for type member.")
        public boolean isAccessibleTo(TypeDescription typeDescription) {
            return this.isPublic() || typeDescription.equals(this.getDeclaringType().asErasure()) || !this.isPrivate() && typeDescription.isSamePackage(this.getDeclaringType().asErasure()) || this.isPrivate() && typeDescription.isNestMateOf(this.getDeclaringType().asErasure());
        }

        @Override
        public int getActualModifiers() {
            return this.getModifiers() | (this.getDeclaredAnnotations().isAnnotationPresent(Deprecated.class) ? 131072 : 0);
        }

        @Override
        public Token asToken(ElementMatcher<? super TypeDescription> matcher) {
            return new Token(this.getName(), this.getModifiers(), this.getType().accept(new TypeDescription.Generic.Visitor.Substitutor.ForDetachment(matcher)), this.getDeclaredAnnotations());
        }

        @Override
        public SignatureToken asSignatureToken() {
            return new SignatureToken(this.getInternalName(), this.getType().asErasure());
        }

        @CachedReturnPlugin.Enhance(value="hashCode")
        @SuppressFBWarnings(value={"NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE"}, justification="Assuming declaring type for type member.")
        public int hashCode() {
            int n;
            int n2;
            int n3 = this.hashCode;
            if (n3 != 0) {
                n2 = 0;
            } else {
                AbstractBase abstractBase = this;
                n2 = n = abstractBase.getDeclaringType().hashCode() + 31 * (17 + abstractBase.getName().hashCode());
            }
            if (n == 0) {
                n = this.hashCode;
            } else {
                this.hashCode = n;
            }
            return n;
        }

        @SuppressFBWarnings(value={"NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE"}, justification="Assuming declaring type for type member.")
        public boolean equals(@MaybeNull Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof FieldDescription)) {
                return false;
            }
            FieldDescription fieldDescription = (FieldDescription)other;
            return this.getName().equals(fieldDescription.getName()) && this.getDeclaringType().equals(fieldDescription.getDeclaringType());
        }

        @Override
        @SuppressFBWarnings(value={"NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE"}, justification="Assuming declaring type for type member.")
        public String toGenericString() {
            StringBuilder stringBuilder = new StringBuilder();
            if (this.getModifiers() != 0) {
                stringBuilder.append(Modifier.toString(this.getModifiers())).append(' ');
            }
            stringBuilder.append(this.getType().getActualName()).append(' ');
            stringBuilder.append(this.getDeclaringType().asErasure().getActualName()).append('.');
            return stringBuilder.append(this.getName()).toString();
        }

        @SuppressFBWarnings(value={"NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE"}, justification="Assuming declaring type for type member.")
        public String toString() {
            StringBuilder stringBuilder = new StringBuilder();
            if (this.getModifiers() != 0) {
                stringBuilder.append(Modifier.toString(this.getModifiers())).append(' ');
            }
            stringBuilder.append(this.getType().asErasure().getActualName()).append(' ');
            stringBuilder.append(this.getDeclaringType().asErasure().getActualName()).append('.');
            return stringBuilder.append(this.getName()).toString();
        }
    }

    public static interface InDefinedShape
    extends FieldDescription {
        @Nonnull
        public TypeDescription getDeclaringType();

        public static abstract class AbstractBase
        extends net.bytebuddy.description.field.FieldDescription$AbstractBase
        implements InDefinedShape {
            public InDefinedShape asDefined() {
                return this;
            }
        }
    }

    public static interface InGenericShape
    extends FieldDescription {
        @Nonnull
        public TypeDescription.Generic getDeclaringType();
    }
}

