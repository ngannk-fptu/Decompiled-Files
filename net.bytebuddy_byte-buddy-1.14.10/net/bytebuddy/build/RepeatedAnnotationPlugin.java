/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package net.bytebuddy.build;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import net.bytebuddy.build.HashCodeAndEqualsPlugin;
import net.bytebuddy.build.Plugin;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.MethodList;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.attribute.AnnotationValueFilter;
import net.bytebuddy.implementation.attribute.TypeAttributeAppender;
import net.bytebuddy.jar.asm.AnnotationVisitor;
import net.bytebuddy.jar.asm.ClassVisitor;
import net.bytebuddy.jar.asm.Type;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.nullability.MaybeNull;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@HashCodeAndEqualsPlugin.Enhance
public class RepeatedAnnotationPlugin
extends Plugin.ForElementMatcher {
    private static final MethodDescription.InDefinedShape VALUE = (MethodDescription.InDefinedShape)((MethodList)TypeDescription.ForLoadedType.of(Enhance.class).getDeclaredMethods().filter(ElementMatchers.named("value"))).getOnly();

    public RepeatedAnnotationPlugin() {
        super(ElementMatchers.isAnnotatedWith(Enhance.class));
    }

    @Override
    @SuppressFBWarnings(value={"NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE"}, justification="Assuming component type for array type.")
    public DynamicType.Builder<?> apply(DynamicType.Builder<?> builder, TypeDescription typeDescription, ClassFileLocator classFileLocator) {
        TypeDescription target = typeDescription.getDeclaredAnnotations().ofType(Enhance.class).getValue(VALUE).resolve(TypeDescription.class);
        if (!target.isAnnotation()) {
            throw new IllegalStateException("Expected " + target + " to be an annotation type");
        }
        if (target.getDeclaredMethods().size() != 1 || ((MethodList)target.getDeclaredMethods().filter(ElementMatchers.named("value"))).size() != 1 || !((MethodDescription.InDefinedShape)((MethodList)target.getDeclaredMethods().filter(ElementMatchers.named("value"))).getOnly()).getReturnType().isArray() || !((MethodDescription.InDefinedShape)((MethodList)target.getDeclaredMethods().filter(ElementMatchers.named("value"))).getOnly()).getReturnType().getComponentType().asErasure().equals(typeDescription)) {
            throw new IllegalStateException("Expected " + target + " to declare exactly one property named value of an array type");
        }
        return builder.attribute(new RepeatedAnnotationAppender(target));
    }

    @Override
    public void close() {
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
        return this.getClass() == object.getClass();
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @HashCodeAndEqualsPlugin.Enhance
    protected static class RepeatedAnnotationAppender
    implements TypeAttributeAppender {
        private final TypeDescription target;

        protected RepeatedAnnotationAppender(TypeDescription target) {
            this.target = target;
        }

        public void apply(ClassVisitor classVisitor, TypeDescription instrumentedType, AnnotationValueFilter annotationValueFilter) {
            AnnotationVisitor visitor = classVisitor.visitAnnotation("Ljava/lang/annotation/Repeatable;", true);
            if (visitor != null) {
                visitor.visit("value", Type.getType(this.target.getDescriptor()));
                visitor.visitEnd();
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
            return this.target.equals(((RepeatedAnnotationAppender)object).target);
        }

        public int hashCode() {
            return this.getClass().hashCode() * 31 + this.target.hashCode();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @Documented
    @Target(value={ElementType.ANNOTATION_TYPE})
    @Retention(value=RetentionPolicy.RUNTIME)
    public static @interface Enhance {
        public Class<? extends Annotation> value();
    }
}

