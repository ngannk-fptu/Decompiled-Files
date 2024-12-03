/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy.asm;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.bytebuddy.asm.AsmVisitorWrapper;
import net.bytebuddy.build.HashCodeAndEqualsPlugin;
import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.field.FieldList;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.MethodList;
import net.bytebuddy.description.modifier.ModifierContributor;
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
import net.bytebuddy.utility.nullability.MaybeNull;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@HashCodeAndEqualsPlugin.Enhance
public class ModifierAdjustment
extends AsmVisitorWrapper.AbstractBase {
    private final List<Adjustment<TypeDescription>> typeAdjustments;
    private final List<Adjustment<FieldDescription.InDefinedShape>> fieldAdjustments;
    private final List<Adjustment<MethodDescription>> methodAdjustments;

    public ModifierAdjustment() {
        this(Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
    }

    protected ModifierAdjustment(List<Adjustment<TypeDescription>> typeAdjustments, List<Adjustment<FieldDescription.InDefinedShape>> fieldAdjustments, List<Adjustment<MethodDescription>> methodAdjustments) {
        this.typeAdjustments = typeAdjustments;
        this.fieldAdjustments = fieldAdjustments;
        this.methodAdjustments = methodAdjustments;
    }

    public ModifierAdjustment withTypeModifiers(ModifierContributor.ForType ... modifierContributor) {
        return this.withTypeModifiers(Arrays.asList(modifierContributor));
    }

    public ModifierAdjustment withTypeModifiers(List<? extends ModifierContributor.ForType> modifierContributors) {
        return this.withTypeModifiers(ElementMatchers.any(), modifierContributors);
    }

    public ModifierAdjustment withTypeModifiers(ElementMatcher<? super TypeDescription> matcher, ModifierContributor.ForType ... modifierContributor) {
        return this.withTypeModifiers(matcher, Arrays.asList(modifierContributor));
    }

    public ModifierAdjustment withTypeModifiers(ElementMatcher<? super TypeDescription> matcher, List<? extends ModifierContributor.ForType> modifierContributors) {
        return new ModifierAdjustment(CompoundList.of(new Adjustment<TypeDescription>(matcher, ModifierContributor.Resolver.of(modifierContributors)), this.typeAdjustments), this.fieldAdjustments, this.methodAdjustments);
    }

    public ModifierAdjustment withFieldModifiers(ModifierContributor.ForField ... modifierContributor) {
        return this.withFieldModifiers(Arrays.asList(modifierContributor));
    }

    public ModifierAdjustment withFieldModifiers(List<? extends ModifierContributor.ForField> modifierContributors) {
        return this.withFieldModifiers(ElementMatchers.any(), modifierContributors);
    }

    public ModifierAdjustment withFieldModifiers(ElementMatcher<? super FieldDescription.InDefinedShape> matcher, ModifierContributor.ForField ... modifierContributor) {
        return this.withFieldModifiers(matcher, Arrays.asList(modifierContributor));
    }

    public ModifierAdjustment withFieldModifiers(ElementMatcher<? super FieldDescription.InDefinedShape> matcher, List<? extends ModifierContributor.ForField> modifierContributors) {
        return new ModifierAdjustment(this.typeAdjustments, CompoundList.of(new Adjustment<FieldDescription.InDefinedShape>(matcher, ModifierContributor.Resolver.of(modifierContributors)), this.fieldAdjustments), this.methodAdjustments);
    }

    public ModifierAdjustment withMethodModifiers(ModifierContributor.ForMethod ... modifierContributor) {
        return this.withMethodModifiers(Arrays.asList(modifierContributor));
    }

    public ModifierAdjustment withMethodModifiers(List<? extends ModifierContributor.ForMethod> modifierContributors) {
        return this.withMethodModifiers(ElementMatchers.any(), modifierContributors);
    }

    public ModifierAdjustment withMethodModifiers(ElementMatcher<? super MethodDescription> matcher, ModifierContributor.ForMethod ... modifierContributor) {
        return this.withMethodModifiers(matcher, Arrays.asList(modifierContributor));
    }

    public ModifierAdjustment withMethodModifiers(ElementMatcher<? super MethodDescription> matcher, List<? extends ModifierContributor.ForMethod> modifierContributors) {
        return this.withInvokableModifiers(ElementMatchers.isMethod().and(matcher), modifierContributors);
    }

    public ModifierAdjustment withConstructorModifiers(ModifierContributor.ForMethod ... modifierContributor) {
        return this.withConstructorModifiers(Arrays.asList(modifierContributor));
    }

    public ModifierAdjustment withConstructorModifiers(List<? extends ModifierContributor.ForMethod> modifierContributors) {
        return this.withConstructorModifiers(ElementMatchers.any(), modifierContributors);
    }

    public ModifierAdjustment withConstructorModifiers(ElementMatcher<? super MethodDescription> matcher, ModifierContributor.ForMethod ... modifierContributor) {
        return this.withConstructorModifiers(matcher, Arrays.asList(modifierContributor));
    }

    public ModifierAdjustment withConstructorModifiers(ElementMatcher<? super MethodDescription> matcher, List<? extends ModifierContributor.ForMethod> modifierContributors) {
        return this.withInvokableModifiers(ElementMatchers.isConstructor().and(matcher), modifierContributors);
    }

    public ModifierAdjustment withInvokableModifiers(ModifierContributor.ForMethod ... modifierContributor) {
        return this.withInvokableModifiers(Arrays.asList(modifierContributor));
    }

    public ModifierAdjustment withInvokableModifiers(List<? extends ModifierContributor.ForMethod> modifierContributors) {
        return this.withInvokableModifiers(ElementMatchers.any(), modifierContributors);
    }

    public ModifierAdjustment withInvokableModifiers(ElementMatcher<? super MethodDescription> matcher, ModifierContributor.ForMethod ... modifierContributor) {
        return this.withInvokableModifiers(matcher, Arrays.asList(modifierContributor));
    }

    public ModifierAdjustment withInvokableModifiers(ElementMatcher<? super MethodDescription> matcher, List<? extends ModifierContributor.ForMethod> modifierContributors) {
        return new ModifierAdjustment(this.typeAdjustments, this.fieldAdjustments, CompoundList.of(new Adjustment<MethodDescription>(matcher, ModifierContributor.Resolver.of(modifierContributors)), this.methodAdjustments));
    }

    @Override
    public ModifierAdjustingClassVisitor wrap(TypeDescription instrumentedType, ClassVisitor classVisitor, Implementation.Context implementationContext, TypePool typePool, FieldList<FieldDescription.InDefinedShape> fields, MethodList<?> methods, int writerFlags, int readerFlags) {
        HashMap<String, FieldDescription.InDefinedShape> mappedFields = new HashMap<String, FieldDescription.InDefinedShape>();
        for (FieldDescription.InDefinedShape fieldDescription : fields) {
            mappedFields.put(fieldDescription.getInternalName() + fieldDescription.getDescriptor(), fieldDescription);
        }
        HashMap<String, MethodDescription> mappedMethods = new HashMap<String, MethodDescription>();
        for (MethodDescription methodDescription : CompoundList.of(methods, new MethodDescription.Latent.TypeInitializer(instrumentedType))) {
            mappedMethods.put(methodDescription.getInternalName() + methodDescription.getDescriptor(), methodDescription);
        }
        return new ModifierAdjustingClassVisitor(classVisitor, this.typeAdjustments, this.fieldAdjustments, this.methodAdjustments, instrumentedType, mappedFields, mappedMethods);
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
        if (!((Object)this.typeAdjustments).equals(((ModifierAdjustment)object).typeAdjustments)) {
            return false;
        }
        if (!((Object)this.fieldAdjustments).equals(((ModifierAdjustment)object).fieldAdjustments)) {
            return false;
        }
        return ((Object)this.methodAdjustments).equals(((ModifierAdjustment)object).methodAdjustments);
    }

    public int hashCode() {
        return ((this.getClass().hashCode() * 31 + ((Object)this.typeAdjustments).hashCode()) * 31 + ((Object)this.fieldAdjustments).hashCode()) * 31 + ((Object)this.methodAdjustments).hashCode();
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    protected static class ModifierAdjustingClassVisitor
    extends ClassVisitor {
        private final List<Adjustment<TypeDescription>> typeAdjustments;
        private final List<Adjustment<FieldDescription.InDefinedShape>> fieldAdjustments;
        private final List<Adjustment<MethodDescription>> methodAdjustments;
        private final TypeDescription instrumentedType;
        private final Map<String, FieldDescription.InDefinedShape> fields;
        private final Map<String, MethodDescription> methods;

        protected ModifierAdjustingClassVisitor(ClassVisitor classVisitor, List<Adjustment<TypeDescription>> typeAdjustments, List<Adjustment<FieldDescription.InDefinedShape>> fieldAdjustments, List<Adjustment<MethodDescription>> methodAdjustments, TypeDescription instrumentedType, Map<String, FieldDescription.InDefinedShape> fields, Map<String, MethodDescription> methods) {
            super(OpenedClassReader.ASM_API, classVisitor);
            this.typeAdjustments = typeAdjustments;
            this.fieldAdjustments = fieldAdjustments;
            this.methodAdjustments = methodAdjustments;
            this.instrumentedType = instrumentedType;
            this.fields = fields;
            this.methods = methods;
        }

        @Override
        public void visit(int version, int modifiers, String internalName, @MaybeNull String signature, @MaybeNull String superClassName, @MaybeNull String[] interfaceName) {
            for (Adjustment<TypeDescription> adjustment : this.typeAdjustments) {
                if (!adjustment.matches(this.instrumentedType)) continue;
                modifiers = adjustment.resolve(modifiers);
                break;
            }
            super.visit(version, modifiers, internalName, signature, superClassName, interfaceName);
        }

        @Override
        public void visitInnerClass(String internalName, @MaybeNull String outerName, @MaybeNull String innerName, int modifiers) {
            if (this.instrumentedType.getInternalName().equals(internalName)) {
                for (Adjustment<TypeDescription> adjustment : this.typeAdjustments) {
                    if (!adjustment.matches(this.instrumentedType)) continue;
                    modifiers = adjustment.resolve(modifiers);
                    break;
                }
            }
            super.visitInnerClass(internalName, outerName, innerName, modifiers);
        }

        @Override
        @MaybeNull
        public FieldVisitor visitField(int modifiers, String internalName, String descriptor, @MaybeNull String signature, @MaybeNull Object value) {
            FieldDescription.InDefinedShape fieldDescription = this.fields.get(internalName + descriptor);
            if (fieldDescription != null) {
                for (Adjustment<FieldDescription.InDefinedShape> adjustment : this.fieldAdjustments) {
                    if (!adjustment.matches(fieldDescription)) continue;
                    modifiers = adjustment.resolve(modifiers);
                    break;
                }
            }
            return super.visitField(modifiers, internalName, descriptor, signature, value);
        }

        @Override
        @MaybeNull
        public MethodVisitor visitMethod(int modifiers, String internalName, String descriptor, @MaybeNull String signature, @MaybeNull String[] exception) {
            MethodDescription methodDescription = this.methods.get(internalName + descriptor);
            if (methodDescription != null) {
                for (Adjustment<MethodDescription> adjustment : this.methodAdjustments) {
                    if (!adjustment.matches(methodDescription)) continue;
                    modifiers = adjustment.resolve(modifiers);
                    break;
                }
            }
            return super.visitMethod(modifiers, internalName, descriptor, signature, exception);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @HashCodeAndEqualsPlugin.Enhance
    protected static class Adjustment<T>
    implements ElementMatcher<T> {
        private final ElementMatcher<? super T> matcher;
        private final ModifierContributor.Resolver<?> resolver;

        protected Adjustment(ElementMatcher<? super T> matcher, ModifierContributor.Resolver<?> resolver) {
            this.matcher = matcher;
            this.resolver = resolver;
        }

        @Override
        public boolean matches(@MaybeNull T target) {
            return this.matcher.matches(target);
        }

        protected int resolve(int modifiers) {
            return this.resolver.resolve(modifiers);
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
            if (!this.matcher.equals(((Adjustment)object).matcher)) {
                return false;
            }
            return this.resolver.equals(((Adjustment)object).resolver);
        }

        public int hashCode() {
            return (this.getClass().hashCode() * 31 + this.matcher.hashCode()) * 31 + this.resolver.hashCode();
        }
    }
}

