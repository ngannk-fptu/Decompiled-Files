/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy.build;

import net.bytebuddy.asm.MemberAttributeExtension;
import net.bytebuddy.build.HashCodeAndEqualsPlugin;
import net.bytebuddy.build.Plugin;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.attribute.AnnotationValueFilter;
import net.bytebuddy.implementation.attribute.MethodAttributeAppender;
import net.bytebuddy.jar.asm.AnnotationVisitor;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.jar.asm.Type;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.dispatcher.JavaDispatcher;
import net.bytebuddy.utility.nullability.MaybeNull;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@HashCodeAndEqualsPlugin.Enhance
public class DispatcherAnnotationPlugin
extends Plugin.ForElementMatcher
implements MethodAttributeAppender.Factory,
MethodAttributeAppender {
    public DispatcherAnnotationPlugin() {
        super(ElementMatchers.isAnnotatedWith(JavaDispatcher.Proxied.class));
    }

    @Override
    public DynamicType.Builder<?> apply(DynamicType.Builder<?> builder, TypeDescription typeDescription, ClassFileLocator classFileLocator) {
        return builder.visit(new MemberAttributeExtension.ForMethod().attribute(this).on(ElementMatchers.not(ElementMatchers.isAnnotatedWith(JavaDispatcher.Proxied.class)).and(ElementMatchers.isAbstract())));
    }

    @Override
    public void close() {
    }

    @Override
    public MethodAttributeAppender make(TypeDescription typeDescription) {
        return this;
    }

    @Override
    public void apply(MethodVisitor methodVisitor, MethodDescription methodDescription, AnnotationValueFilter annotationValueFilter) {
        AnnotationVisitor visitor = methodVisitor.visitAnnotation(Type.getDescriptor(JavaDispatcher.Proxied.class), true);
        if (visitor != null) {
            visitor.visit("value", methodDescription.getName());
            visitor.visitEnd();
        }
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
}

