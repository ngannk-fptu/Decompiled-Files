/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy.dynamic.scaffold;

import net.bytebuddy.build.HashCodeAndEqualsPlugin;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.scaffold.TypeWriter;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.attribute.AnnotationValueFilter;
import net.bytebuddy.implementation.bytecode.ByteCodeAppender;
import net.bytebuddy.jar.asm.ClassVisitor;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.utility.nullability.MaybeNull;

public interface TypeInitializer
extends ByteCodeAppender {
    public boolean isDefined();

    public TypeInitializer expandWith(ByteCodeAppender var1);

    public TypeWriter.MethodPool.Record wrap(TypeWriter.MethodPool.Record var1);

    @HashCodeAndEqualsPlugin.Enhance
    public static class Simple
    implements TypeInitializer {
        private final ByteCodeAppender byteCodeAppender;

        public Simple(ByteCodeAppender byteCodeAppender) {
            this.byteCodeAppender = byteCodeAppender;
        }

        public boolean isDefined() {
            return true;
        }

        public TypeInitializer expandWith(ByteCodeAppender byteCodeAppender) {
            return new Simple(new ByteCodeAppender.Compound(this.byteCodeAppender, byteCodeAppender));
        }

        public TypeWriter.MethodPool.Record wrap(TypeWriter.MethodPool.Record record) {
            return record.prepend(this.byteCodeAppender);
        }

        public ByteCodeAppender.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext, MethodDescription instrumentedMethod) {
            return this.byteCodeAppender.apply(methodVisitor, implementationContext, instrumentedMethod);
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
            return this.byteCodeAppender.equals(((Simple)object).byteCodeAppender);
        }

        public int hashCode() {
            return this.getClass().hashCode() * 31 + this.byteCodeAppender.hashCode();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum None implements TypeInitializer
    {
        INSTANCE;


        @Override
        public boolean isDefined() {
            return false;
        }

        @Override
        public TypeInitializer expandWith(ByteCodeAppender byteCodeAppenderFactory) {
            return new Simple(byteCodeAppenderFactory);
        }

        @Override
        public TypeWriter.MethodPool.Record wrap(TypeWriter.MethodPool.Record record) {
            return record;
        }

        @Override
        public ByteCodeAppender.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext, MethodDescription instrumentedMethod) {
            return ByteCodeAppender.Size.ZERO;
        }
    }

    public static interface Drain {
        public void apply(ClassVisitor var1, TypeInitializer var2, Implementation.Context var3);

        @HashCodeAndEqualsPlugin.Enhance
        public static class Default
        implements Drain {
            protected final TypeDescription instrumentedType;
            protected final TypeWriter.MethodPool methodPool;
            protected final AnnotationValueFilter.Factory annotationValueFilterFactory;

            public Default(TypeDescription instrumentedType, TypeWriter.MethodPool methodPool, AnnotationValueFilter.Factory annotationValueFilterFactory) {
                this.instrumentedType = instrumentedType;
                this.methodPool = methodPool;
                this.annotationValueFilterFactory = annotationValueFilterFactory;
            }

            public void apply(ClassVisitor classVisitor, TypeInitializer typeInitializer, Implementation.Context implementationContext) {
                typeInitializer.wrap(this.methodPool.target(new MethodDescription.Latent.TypeInitializer(this.instrumentedType))).apply(classVisitor, implementationContext, this.annotationValueFilterFactory);
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
                if (!this.instrumentedType.equals(((Default)object).instrumentedType)) {
                    return false;
                }
                if (!this.methodPool.equals(((Default)object).methodPool)) {
                    return false;
                }
                return this.annotationValueFilterFactory.equals(((Default)object).annotationValueFilterFactory);
            }

            public int hashCode() {
                return ((this.getClass().hashCode() * 31 + this.instrumentedType.hashCode()) * 31 + this.methodPool.hashCode()) * 31 + this.annotationValueFilterFactory.hashCode();
            }
        }
    }
}

