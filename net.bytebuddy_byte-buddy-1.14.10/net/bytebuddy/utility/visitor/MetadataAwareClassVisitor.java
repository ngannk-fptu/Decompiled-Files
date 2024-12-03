/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy.utility.visitor;

import net.bytebuddy.jar.asm.AnnotationVisitor;
import net.bytebuddy.jar.asm.Attribute;
import net.bytebuddy.jar.asm.ClassVisitor;
import net.bytebuddy.jar.asm.FieldVisitor;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.jar.asm.RecordComponentVisitor;
import net.bytebuddy.jar.asm.TypePath;
import net.bytebuddy.utility.nullability.MaybeNull;

public abstract class MetadataAwareClassVisitor
extends ClassVisitor {
    private boolean triggerNestHost = true;
    private boolean triggerOuterClass = true;
    private boolean triggerAttributes = true;

    protected MetadataAwareClassVisitor(int api, ClassVisitor classVisitor) {
        super(api, classVisitor);
    }

    protected void onNestHost() {
    }

    protected void onOuterType() {
    }

    protected void onAfterAttributes() {
    }

    private void considerTriggerNestHost() {
        if (this.triggerNestHost) {
            this.triggerNestHost = false;
            this.onNestHost();
        }
    }

    private void considerTriggerOuterClass() {
        if (this.triggerOuterClass) {
            this.triggerOuterClass = false;
            this.onOuterType();
        }
    }

    private void considerTriggerAfterAttributes() {
        if (this.triggerAttributes) {
            this.triggerAttributes = false;
            this.onAfterAttributes();
        }
    }

    public final void visitNestHost(String nestHost) {
        this.triggerNestHost = false;
        this.onVisitNestHost(nestHost);
    }

    protected void onVisitNestHost(String nestHost) {
        super.visitNestHost(nestHost);
    }

    public final void visitOuterClass(String owner, @MaybeNull String name, @MaybeNull String descriptor) {
        this.considerTriggerNestHost();
        this.triggerOuterClass = false;
        this.onVisitOuterClass(owner, name, descriptor);
    }

    protected void onVisitOuterClass(String owner, @MaybeNull String name, @MaybeNull String descriptor) {
        super.visitOuterClass(owner, name, descriptor);
    }

    public final void visitPermittedSubclass(String permittedSubclass) {
        this.considerTriggerNestHost();
        this.considerTriggerOuterClass();
        this.considerTriggerAfterAttributes();
        this.onVisitPermittedSubclass(permittedSubclass);
    }

    protected void onVisitPermittedSubclass(String permittedSubclass) {
        super.visitPermittedSubclass(permittedSubclass);
    }

    @MaybeNull
    public RecordComponentVisitor visitRecordComponent(String name, String descriptor, @MaybeNull String signature) {
        this.considerTriggerNestHost();
        this.considerTriggerOuterClass();
        this.considerTriggerAfterAttributes();
        return this.onVisitRecordComponent(name, descriptor, signature);
    }

    @MaybeNull
    protected RecordComponentVisitor onVisitRecordComponent(String name, String descriptor, @MaybeNull String signature) {
        return super.visitRecordComponent(name, descriptor, signature);
    }

    @MaybeNull
    public final AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        this.considerTriggerNestHost();
        this.considerTriggerOuterClass();
        return this.onVisitAnnotation(descriptor, visible);
    }

    @MaybeNull
    protected AnnotationVisitor onVisitAnnotation(String descriptor, boolean visible) {
        return super.visitAnnotation(descriptor, visible);
    }

    @MaybeNull
    public final AnnotationVisitor visitTypeAnnotation(int typeReference, TypePath typePath, String descriptor, boolean visible) {
        this.considerTriggerNestHost();
        this.considerTriggerOuterClass();
        return this.onVisitTypeAnnotation(typeReference, typePath, descriptor, visible);
    }

    @MaybeNull
    protected AnnotationVisitor onVisitTypeAnnotation(int typeReference, TypePath typePath, String descriptor, boolean visible) {
        return super.visitTypeAnnotation(typeReference, typePath, descriptor, visible);
    }

    public final void visitAttribute(Attribute attribute) {
        this.considerTriggerNestHost();
        this.considerTriggerOuterClass();
        this.onVisitAttribute(attribute);
    }

    protected void onVisitAttribute(Attribute attribute) {
        super.visitAttribute(attribute);
    }

    public final void visitNestMember(String nestMember) {
        this.considerTriggerNestHost();
        this.considerTriggerOuterClass();
        this.considerTriggerAfterAttributes();
        this.onVisitNestMember(nestMember);
    }

    protected void onVisitNestMember(String nestMember) {
        super.visitNestMember(nestMember);
    }

    public final void visitInnerClass(String name, @MaybeNull String outerName, @MaybeNull String innerName, int modifiers) {
        this.considerTriggerNestHost();
        this.considerTriggerOuterClass();
        this.considerTriggerAfterAttributes();
        this.onVisitInnerClass(name, outerName, innerName, modifiers);
    }

    protected void onVisitInnerClass(String internalName, @MaybeNull String outerName, @MaybeNull String innerName, int modifiers) {
        super.visitInnerClass(internalName, outerName, innerName, modifiers);
    }

    @MaybeNull
    public final FieldVisitor visitField(int modifiers, String internalName, String descriptor, @MaybeNull String signature, @MaybeNull Object value) {
        this.considerTriggerNestHost();
        this.considerTriggerOuterClass();
        this.considerTriggerAfterAttributes();
        return this.onVisitField(modifiers, internalName, descriptor, signature, value);
    }

    @MaybeNull
    protected FieldVisitor onVisitField(int modifiers, String internalName, String descriptor, @MaybeNull String signature, @MaybeNull Object value) {
        return super.visitField(modifiers, internalName, descriptor, signature, value);
    }

    @MaybeNull
    public final MethodVisitor visitMethod(int modifiers, String internalName, String descriptor, @MaybeNull String signature, @MaybeNull String[] exception) {
        this.considerTriggerNestHost();
        this.considerTriggerOuterClass();
        this.considerTriggerAfterAttributes();
        return this.onVisitMethod(modifiers, internalName, descriptor, signature, exception);
    }

    @MaybeNull
    protected MethodVisitor onVisitMethod(int modifiers, String internalName, String descriptor, @MaybeNull String signature, @MaybeNull String[] exception) {
        return super.visitMethod(modifiers, internalName, descriptor, signature, exception);
    }

    public final void visitEnd() {
        this.considerTriggerNestHost();
        this.considerTriggerOuterClass();
        this.considerTriggerAfterAttributes();
        this.onVisitEnd();
    }

    protected void onVisitEnd() {
        super.visitEnd();
    }
}

