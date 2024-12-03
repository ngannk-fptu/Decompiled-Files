/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.meta.When
 */
package net.bytebuddy.asm;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.meta.When;
import net.bytebuddy.asm.AsmVisitorWrapper;
import net.bytebuddy.build.HashCodeAndEqualsPlugin;
import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.field.FieldList;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.MethodList;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.jar.asm.ClassVisitor;
import net.bytebuddy.jar.asm.FieldVisitor;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.pool.TypePool;
import net.bytebuddy.utility.CompoundList;
import net.bytebuddy.utility.OpenedClassReader;
import net.bytebuddy.utility.nullability.AlwaysNull;
import net.bytebuddy.utility.nullability.MaybeNull;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@HashCodeAndEqualsPlugin.Enhance
public class MemberRemoval
extends AsmVisitorWrapper.AbstractBase {
    private final ElementMatcher.Junction<FieldDescription.InDefinedShape> fieldMatcher;
    private final ElementMatcher.Junction<MethodDescription> methodMatcher;

    public MemberRemoval() {
        this(ElementMatchers.none(), ElementMatchers.none());
    }

    protected MemberRemoval(ElementMatcher.Junction<FieldDescription.InDefinedShape> fieldMatcher, ElementMatcher.Junction<MethodDescription> methodMatcher) {
        this.fieldMatcher = fieldMatcher;
        this.methodMatcher = methodMatcher;
    }

    public MemberRemoval stripFields(ElementMatcher<? super FieldDescription.InDefinedShape> matcher) {
        return new MemberRemoval(this.fieldMatcher.or(matcher), this.methodMatcher);
    }

    public MemberRemoval stripMethods(ElementMatcher<? super MethodDescription> matcher) {
        return this.stripInvokables(ElementMatchers.isMethod().and(matcher));
    }

    public MemberRemoval stripConstructors(ElementMatcher<? super MethodDescription> matcher) {
        return this.stripInvokables(ElementMatchers.isConstructor().and(matcher));
    }

    public MemberRemoval stripInvokables(ElementMatcher<? super MethodDescription> matcher) {
        return new MemberRemoval(this.fieldMatcher, this.methodMatcher.or(matcher));
    }

    @Override
    public ClassVisitor wrap(TypeDescription instrumentedType, ClassVisitor classVisitor, Implementation.Context implementationContext, TypePool typePool, FieldList<FieldDescription.InDefinedShape> fields, MethodList<?> methods, int writerFlags, int readerFlags) {
        HashMap<String, FieldDescription.InDefinedShape> mappedFields = new HashMap<String, FieldDescription.InDefinedShape>();
        for (FieldDescription.InDefinedShape fieldDescription : fields) {
            mappedFields.put(fieldDescription.getInternalName() + fieldDescription.getDescriptor(), fieldDescription);
        }
        HashMap<String, MethodDescription> mappedMethods = new HashMap<String, MethodDescription>();
        for (MethodDescription methodDescription : CompoundList.of(methods, new MethodDescription.Latent.TypeInitializer(instrumentedType))) {
            mappedMethods.put(methodDescription.getInternalName() + methodDescription.getDescriptor(), methodDescription);
        }
        return new MemberRemovingClassVisitor(classVisitor, this.fieldMatcher, this.methodMatcher, mappedFields, mappedMethods);
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
        if (!this.fieldMatcher.equals(((MemberRemoval)object).fieldMatcher)) {
            return false;
        }
        return this.methodMatcher.equals(((MemberRemoval)object).methodMatcher);
    }

    public int hashCode() {
        return (this.getClass().hashCode() * 31 + this.fieldMatcher.hashCode()) * 31 + this.methodMatcher.hashCode();
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    protected static class MemberRemovingClassVisitor
    extends ClassVisitor {
        @Nonnull(when=When.NEVER)
        private static final FieldVisitor REMOVE_FIELD = null;
        @AlwaysNull
        private static final MethodVisitor REMOVE_METHOD = null;
        private final ElementMatcher.Junction<FieldDescription.InDefinedShape> fieldMatcher;
        private final ElementMatcher.Junction<MethodDescription> methodMatcher;
        private final Map<String, FieldDescription.InDefinedShape> fields;
        private final Map<String, MethodDescription> methods;

        protected MemberRemovingClassVisitor(ClassVisitor classVisitor, ElementMatcher.Junction<FieldDescription.InDefinedShape> fieldMatcher, ElementMatcher.Junction<MethodDescription> methodMatcher, Map<String, FieldDescription.InDefinedShape> fields, Map<String, MethodDescription> methods) {
            super(OpenedClassReader.ASM_API, classVisitor);
            this.fieldMatcher = fieldMatcher;
            this.methodMatcher = methodMatcher;
            this.fields = fields;
            this.methods = methods;
        }

        @Override
        @MaybeNull
        public FieldVisitor visitField(int modifiers, String internalName, String descriptor, @MaybeNull String signature, @MaybeNull Object value) {
            FieldDescription.InDefinedShape fieldDescription = this.fields.get(internalName + descriptor);
            return fieldDescription != null && this.fieldMatcher.matches(fieldDescription) ? REMOVE_FIELD : super.visitField(modifiers, internalName, descriptor, signature, value);
        }

        @Override
        @MaybeNull
        public MethodVisitor visitMethod(int modifiers, String internalName, String descriptor, @MaybeNull String signature, @MaybeNull String[] exception) {
            MethodDescription methodDescription = this.methods.get(internalName + descriptor);
            return methodDescription != null && this.methodMatcher.matches(methodDescription) ? REMOVE_METHOD : super.visitMethod(modifiers, internalName, descriptor, signature, exception);
        }
    }
}

