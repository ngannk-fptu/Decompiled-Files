/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package net.bytebuddy.description.type;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.GenericSignatureFormatError;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import net.bytebuddy.build.AccessControllerPlugin;
import net.bytebuddy.build.CachedReturnPlugin;
import net.bytebuddy.description.ByteCodeElement;
import net.bytebuddy.description.DeclaredByType;
import net.bytebuddy.description.NamedElement;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.annotation.AnnotationList;
import net.bytebuddy.description.annotation.AnnotationSource;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.MethodList;
import net.bytebuddy.description.type.TypeDefinition;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.jar.asm.signature.SignatureWriter;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.dispatcher.JavaDispatcher;
import net.bytebuddy.utility.nullability.MaybeNull;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface RecordComponentDescription
extends DeclaredByType.WithMandatoryDeclaration,
NamedElement.WithDescriptor,
AnnotationSource,
ByteCodeElement.TypeDependant<InDefinedShape, Token> {
    public TypeDescription.Generic getType();

    public MethodDescription getAccessor();

    @Override
    public Token asToken(ElementMatcher<? super TypeDescription> var1);

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class Token
    implements ByteCodeElement.Token<Token> {
        private final String name;
        private final TypeDescription.Generic type;
        private final List<? extends AnnotationDescription> annotations;
        private transient /* synthetic */ int hashCode;

        public Token(String name, TypeDescription.Generic type) {
            this(name, type, Collections.emptyList());
        }

        public Token(String name, TypeDescription.Generic type, List<? extends AnnotationDescription> annotations) {
            this.name = name;
            this.type = type;
            this.annotations = annotations;
        }

        public String getName() {
            return this.name;
        }

        public TypeDescription.Generic getType() {
            return this.type;
        }

        public AnnotationList getAnnotations() {
            return new AnnotationList.Explicit(this.annotations);
        }

        @Override
        public Token accept(TypeDescription.Generic.Visitor<? extends TypeDescription.Generic> visitor) {
            return new Token(this.name, this.type.accept(visitor), this.annotations);
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
            return this.name.equals(token.name) && this.type.equals(token.type) && this.annotations.equals(token.annotations);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class TypeSubstituting
    extends AbstractBase
    implements InGenericShape {
        private final TypeDescription.Generic declaringType;
        private final RecordComponentDescription recordComponentDescription;
        private final TypeDescription.Generic.Visitor<? extends TypeDescription.Generic> visitor;

        public TypeSubstituting(TypeDescription.Generic declaringType, RecordComponentDescription recordComponentDescription, TypeDescription.Generic.Visitor<? extends TypeDescription.Generic> visitor) {
            this.declaringType = declaringType;
            this.recordComponentDescription = recordComponentDescription;
            this.visitor = visitor;
        }

        @Override
        public MethodDescription.InGenericShape getAccessor() {
            return (MethodDescription.InGenericShape)((MethodList)this.declaringType.getDeclaredMethods().filter(ElementMatchers.named(this.getActualName()))).getOnly();
        }

        @Override
        public TypeDescription.Generic getType() {
            return this.recordComponentDescription.getType().accept(this.visitor);
        }

        @Override
        public InDefinedShape asDefined() {
            return (InDefinedShape)this.recordComponentDescription.asDefined();
        }

        @Override
        @Nonnull
        public TypeDefinition getDeclaringType() {
            return this.declaringType;
        }

        @Override
        public String getActualName() {
            return this.recordComponentDescription.getActualName();
        }

        @Override
        public AnnotationList getDeclaredAnnotations() {
            return this.recordComponentDescription.getDeclaredAnnotations();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class Latent
    extends InDefinedShape.AbstractBase {
        private final TypeDescription declaringType;
        private final String name;
        private final TypeDescription.Generic type;
        private final List<? extends AnnotationDescription> annotations;

        public Latent(TypeDescription declaringType, Token token) {
            this(declaringType, token.getName(), token.getType(), token.getAnnotations());
        }

        public Latent(TypeDescription declaringType, String name, TypeDescription.Generic type, List<? extends AnnotationDescription> annotations) {
            this.declaringType = declaringType;
            this.name = name;
            this.type = type;
            this.annotations = annotations;
        }

        @Override
        public TypeDescription.Generic getType() {
            return this.type.accept(TypeDescription.Generic.Visitor.Substitutor.ForAttachment.of(this));
        }

        @Override
        @Nonnull
        public TypeDescription getDeclaringType() {
            return this.declaringType;
        }

        @Override
        public String getActualName() {
            return this.name;
        }

        @Override
        public AnnotationList getDeclaredAnnotations() {
            return new AnnotationList.Explicit(this.annotations);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class ForLoadedRecordComponent
    extends InDefinedShape.AbstractBase {
        protected static final RecordComponent RECORD_COMPONENT;
        private final AnnotatedElement recordComponent;
        private static final boolean ACCESS_CONTROLLER;

        protected ForLoadedRecordComponent(AnnotatedElement recordComponent) {
            this.recordComponent = recordComponent;
        }

        @AccessControllerPlugin.Enhance
        private static <T> T doPrivileged(PrivilegedAction<T> privilegedAction) {
            PrivilegedAction<T> action;
            if (ACCESS_CONTROLLER) {
                return AccessController.doPrivileged(privilegedAction);
            }
            return action.run();
        }

        public static RecordComponentDescription of(Object recordComponent) {
            if (!RECORD_COMPONENT.isInstance(recordComponent)) {
                throw new IllegalArgumentException("Not a record component: " + recordComponent);
            }
            return new ForLoadedRecordComponent((AnnotatedElement)recordComponent);
        }

        @Override
        public TypeDescription.Generic getType() {
            return new TypeDescription.Generic.LazyProjection.OfRecordComponent(this.recordComponent);
        }

        @Override
        public MethodDescription.InDefinedShape getAccessor() {
            return new MethodDescription.ForLoadedMethod(RECORD_COMPONENT.getAccessor(this.recordComponent));
        }

        @Override
        @Nonnull
        public TypeDescription getDeclaringType() {
            return TypeDescription.ForLoadedType.of(RECORD_COMPONENT.getDeclaringRecord(this.recordComponent));
        }

        @Override
        public String getActualName() {
            return RECORD_COMPONENT.getName(this.recordComponent);
        }

        @Override
        @MaybeNull
        public String getGenericSignature() {
            return RECORD_COMPONENT.getGenericSignature(this.recordComponent);
        }

        @Override
        public AnnotationList getDeclaredAnnotations() {
            return new AnnotationList.ForLoadedAnnotations(this.recordComponent.getDeclaredAnnotations());
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
            RECORD_COMPONENT = ForLoadedRecordComponent.doPrivileged(JavaDispatcher.of(RecordComponent.class));
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        @JavaDispatcher.Proxied(value="java.lang.reflect.RecordComponent")
        protected static interface RecordComponent {
            @JavaDispatcher.Instance
            @JavaDispatcher.Proxied(value="isInstance")
            public boolean isInstance(Object var1);

            @JavaDispatcher.Proxied(value="getName")
            public String getName(Object var1);

            @JavaDispatcher.Proxied(value="getDeclaringRecord")
            public Class<?> getDeclaringRecord(Object var1);

            @JavaDispatcher.Proxied(value="getAccessor")
            public Method getAccessor(Object var1);

            @JavaDispatcher.Proxied(value="getType")
            public Class<?> getType(Object var1);

            @JavaDispatcher.Proxied(value="getGenericType")
            public Type getGenericType(Object var1);

            @MaybeNull
            @JavaDispatcher.Proxied(value="getGenericSignature")
            public String getGenericSignature(Object var1);

            @JavaDispatcher.Proxied(value="getAnnotatedType")
            public AnnotatedElement getAnnotatedType(Object var1);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static abstract class AbstractBase
    implements RecordComponentDescription {
        @Override
        public Token asToken(ElementMatcher<? super TypeDescription> matcher) {
            return new Token(this.getActualName(), this.getType().accept(new TypeDescription.Generic.Visitor.Substitutor.ForDetachment(matcher)), this.getDeclaredAnnotations());
        }

        @Override
        public String getDescriptor() {
            return this.getType().asErasure().getDescriptor();
        }

        @Override
        @MaybeNull
        public String getGenericSignature() {
            TypeDescription.Generic recordComponentType = this.getType();
            try {
                return recordComponentType.getSort().isNonGeneric() ? NON_GENERIC_SIGNATURE : recordComponentType.accept(new TypeDescription.Generic.Visitor.ForSignatureVisitor(new SignatureWriter())).toString();
            }
            catch (GenericSignatureFormatError ignored) {
                return NON_GENERIC_SIGNATURE;
            }
        }

        public int hashCode() {
            return this.getActualName().hashCode();
        }

        public boolean equals(@MaybeNull Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof RecordComponentDescription)) {
                return false;
            }
            RecordComponentDescription recordComponentDescription = (RecordComponentDescription)other;
            return this.getActualName().equals(recordComponentDescription.getActualName());
        }

        public String toString() {
            return this.getType().getTypeName() + " " + this.getActualName();
        }
    }

    public static interface InDefinedShape
    extends RecordComponentDescription {
        public MethodDescription.InDefinedShape getAccessor();

        @Nonnull
        public TypeDescription getDeclaringType();

        public static abstract class AbstractBase
        extends net.bytebuddy.description.type.RecordComponentDescription$AbstractBase
        implements InDefinedShape {
            public MethodDescription.InDefinedShape getAccessor() {
                return (MethodDescription.InDefinedShape)((MethodList)this.getDeclaringType().getDeclaredMethods().filter(ElementMatchers.named(this.getActualName()))).getOnly();
            }

            public InDefinedShape asDefined() {
                return this;
            }
        }
    }

    public static interface InGenericShape
    extends RecordComponentDescription {
        public MethodDescription.InGenericShape getAccessor();
    }
}

