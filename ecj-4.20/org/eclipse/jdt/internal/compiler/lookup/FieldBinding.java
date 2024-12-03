/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.lookup;

import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.IErrorHandlingPolicy;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.lookup.AnnotationBinding;
import org.eclipse.jdt.internal.compiler.lookup.ArrayBinding;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.internal.compiler.lookup.InvocationSite;
import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
import org.eclipse.jdt.internal.compiler.lookup.MethodScope;
import org.eclipse.jdt.internal.compiler.lookup.PackageBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.VariableBinding;
import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;

public class FieldBinding
extends VariableBinding {
    public ReferenceBinding declaringClass;
    public int compoundUseFlag = 0;

    protected FieldBinding() {
        super(null, null, 0, null);
    }

    public FieldBinding(char[] name, TypeBinding type, int modifiers, ReferenceBinding declaringClass, Constant constant) {
        super(name, type, modifiers, constant);
        this.declaringClass = declaringClass;
    }

    public FieldBinding(FieldBinding initialFieldBinding, ReferenceBinding declaringClass) {
        super(initialFieldBinding.name, initialFieldBinding.type, initialFieldBinding.modifiers, initialFieldBinding.constant());
        this.declaringClass = declaringClass;
        this.id = initialFieldBinding.id;
        this.setAnnotations(initialFieldBinding.getAnnotations(), false);
    }

    public FieldBinding(FieldDeclaration field, TypeBinding type, int modifiers, ReferenceBinding declaringClass) {
        this(field.name, type, modifiers, declaringClass, null);
        field.binding = this;
    }

    public final boolean canBeSeenBy(PackageBinding invocationPackage) {
        if (this.isPublic()) {
            return true;
        }
        if (this.isPrivate()) {
            return false;
        }
        return invocationPackage == this.declaringClass.getPackage();
    }

    public final boolean canBeSeenBy(TypeBinding receiverType, InvocationSite invocationSite, Scope scope) {
        if (this.isPublic()) {
            return true;
        }
        SourceTypeBinding invocationType = scope.enclosingSourceType();
        if (TypeBinding.equalsEquals(invocationType, this.declaringClass) && TypeBinding.equalsEquals(invocationType, receiverType)) {
            return true;
        }
        if (invocationType == null) {
            return !this.isPrivate() && scope.getCurrentPackage() == this.declaringClass.fPackage;
        }
        if (this.isProtected()) {
            if (TypeBinding.equalsEquals(invocationType, this.declaringClass)) {
                return true;
            }
            if (invocationType.fPackage == this.declaringClass.fPackage) {
                return true;
            }
            ReferenceBinding currentType = invocationType;
            int depth = 0;
            ReferenceBinding receiverErasure = (ReferenceBinding)receiverType.erasure();
            ReferenceBinding declaringErasure = (ReferenceBinding)this.declaringClass.erasure();
            do {
                if (currentType.findSuperTypeOriginatingFrom(declaringErasure) != null) {
                    if (invocationSite.isSuperAccess()) {
                        return true;
                    }
                    if (receiverType instanceof ArrayBinding) {
                        return false;
                    }
                    if (this.isStatic()) {
                        if (depth > 0) {
                            invocationSite.setDepth(depth);
                        }
                        return true;
                    }
                    if (TypeBinding.equalsEquals(currentType, receiverErasure) || receiverErasure.findSuperTypeOriginatingFrom(currentType) != null) {
                        if (depth > 0) {
                            invocationSite.setDepth(depth);
                        }
                        return true;
                    }
                }
                ++depth;
            } while ((currentType = currentType.enclosingType()) != null);
            return false;
        }
        if (this.isPrivate()) {
            if (!(!TypeBinding.notEquals(receiverType, this.declaringClass) || scope.compilerOptions().complianceLevel <= 0x320000L && receiverType.isTypeVariable() && ((TypeVariableBinding)receiverType).isErasureBoundTo(this.declaringClass.erasure()))) {
                return false;
            }
            if (TypeBinding.notEquals(invocationType, this.declaringClass)) {
                ReferenceBinding outerInvocationType = invocationType;
                ReferenceBinding temp = outerInvocationType.enclosingType();
                while (temp != null) {
                    outerInvocationType = temp;
                    temp = temp.enclosingType();
                }
                ReferenceBinding outerDeclaringClass = (ReferenceBinding)this.declaringClass.erasure();
                temp = outerDeclaringClass.enclosingType();
                while (temp != null) {
                    outerDeclaringClass = temp;
                    temp = temp.enclosingType();
                }
                if (TypeBinding.notEquals(outerInvocationType, outerDeclaringClass)) {
                    return false;
                }
            }
            return true;
        }
        PackageBinding declaringPackage = this.declaringClass.fPackage;
        if (invocationType.fPackage != declaringPackage) {
            return false;
        }
        if (receiverType instanceof ArrayBinding) {
            return false;
        }
        TypeBinding originalDeclaringClass = this.declaringClass.original();
        ReferenceBinding currentType = (ReferenceBinding)receiverType;
        do {
            if (currentType.isCapture() ? TypeBinding.equalsEquals(originalDeclaringClass, currentType.erasure().original()) : TypeBinding.equalsEquals(originalDeclaringClass, currentType.original())) {
                return true;
            }
            PackageBinding currentPackage = currentType.fPackage;
            if (currentPackage == null || currentPackage == declaringPackage) continue;
            return false;
        } while ((currentType = currentType.superclass()) != null);
        return false;
    }

    @Override
    public char[] computeUniqueKey(boolean isLeaf) {
        char[] cArray;
        char[] declaringKey = this.declaringClass == null ? CharOperation.NO_CHAR : this.declaringClass.computeUniqueKey(false);
        int declaringLength = declaringKey.length;
        int nameLength = this.name.length;
        if (this.type == null) {
            char[] cArray2 = new char[1];
            cArray = cArray2;
            cArray2[0] = 86;
        } else {
            cArray = this.type.computeUniqueKey(false);
        }
        char[] returnTypeKey = cArray;
        int returnTypeLength = returnTypeKey.length;
        char[] uniqueKey = new char[declaringLength + 1 + nameLength + 1 + returnTypeLength];
        int index = 0;
        System.arraycopy(declaringKey, 0, uniqueKey, index, declaringLength);
        index += declaringLength;
        uniqueKey[index++] = 46;
        System.arraycopy(this.name, 0, uniqueKey, index, nameLength);
        index += nameLength;
        uniqueKey[index++] = 41;
        System.arraycopy(returnTypeKey, 0, uniqueKey, index, returnTypeLength);
        return uniqueKey;
    }

    @Override
    public Constant constant() {
        Constant fieldConstant = this.constant;
        if (fieldConstant == null) {
            if (this.isFinal()) {
                FieldBinding originalField = this.original();
                if (originalField.declaringClass instanceof SourceTypeBinding) {
                    SourceTypeBinding sourceType = (SourceTypeBinding)originalField.declaringClass;
                    if (sourceType.scope != null) {
                        TypeDeclaration typeDecl = sourceType.scope.referenceContext;
                        FieldDeclaration fieldDecl = typeDecl.declarationOf(originalField);
                        MethodScope initScope = originalField.isStatic() ? typeDecl.staticInitializerScope : typeDecl.initializerScope;
                        boolean old = initScope.insideTypeAnnotation;
                        try {
                            initScope.insideTypeAnnotation = false;
                            fieldDecl.resolve(initScope);
                        }
                        finally {
                            initScope.insideTypeAnnotation = old;
                        }
                        fieldConstant = originalField.constant == null ? Constant.NotAConstant : originalField.constant;
                    } else {
                        fieldConstant = Constant.NotAConstant;
                    }
                } else {
                    fieldConstant = Constant.NotAConstant;
                }
            } else {
                fieldConstant = Constant.NotAConstant;
            }
            this.constant = fieldConstant;
        }
        return fieldConstant;
    }

    @Override
    public Constant constant(Scope scope) {
        if (this.constant != null) {
            return this.constant;
        }
        ProblemReporter problemReporter = scope.problemReporter();
        IErrorHandlingPolicy suspendedPolicy = problemReporter.suspendTempErrorHandlingPolicy();
        try {
            Constant constant = this.constant();
            return constant;
        }
        finally {
            problemReporter.resumeTempErrorHandlingPolicy(suspendedPolicy);
        }
    }

    public void fillInDefaultNonNullness(FieldDeclaration sourceField, Scope scope) {
        if (this.type == null || this.type.isBaseType()) {
            return;
        }
        LookupEnvironment environment = scope.environment();
        if (environment.usesNullTypeAnnotations()) {
            if (!this.type.acceptsNonNullDefault()) {
                return;
            }
            if ((this.type.tagBits & 0x180000000000000L) == 0L) {
                this.type = environment.createAnnotatedType(this.type, new AnnotationBinding[]{environment.getNonNullAnnotation()});
            } else if ((this.type.tagBits & 0x100000000000000L) != 0L) {
                scope.problemReporter().nullAnnotationIsRedundant(sourceField);
            }
        } else if ((this.tagBits & 0x180000000000000L) == 0L) {
            this.tagBits |= 0x100000000000000L;
        } else if ((this.tagBits & 0x100000000000000L) != 0L) {
            scope.problemReporter().nullAnnotationIsRedundant(sourceField);
        }
    }

    public char[] genericSignature() {
        if ((this.modifiers & 0x40000000) == 0) {
            return null;
        }
        return this.type.genericTypeSignature();
    }

    public final int getAccessFlags() {
        return this.modifiers & 0xFFFF;
    }

    @Override
    public AnnotationBinding[] getAnnotations() {
        FieldBinding originalField = this.original();
        ReferenceBinding declaringClassBinding = originalField.declaringClass;
        if (declaringClassBinding == null) {
            return Binding.NO_ANNOTATIONS;
        }
        return declaringClassBinding.retrieveAnnotations(originalField);
    }

    @Override
    public long getAnnotationTagBits() {
        FieldBinding originalField = this.original();
        if ((originalField.tagBits & 0x200000000L) == 0L && originalField.declaringClass instanceof SourceTypeBinding) {
            ClassScope scope = ((SourceTypeBinding)originalField.declaringClass).scope;
            if (scope == null) {
                this.tagBits |= 0x600000000L;
                return 0L;
            }
            TypeDeclaration typeDecl = scope.referenceContext;
            FieldDeclaration fieldDecl = typeDecl.declarationOf(originalField);
            if (fieldDecl != null) {
                MethodScope initializationScope = this.isStatic() ? typeDecl.staticInitializerScope : typeDecl.initializerScope;
                FieldBinding previousField = initializationScope.initializedField;
                int previousFieldID = initializationScope.lastVisibleFieldID;
                try {
                    initializationScope.initializedField = originalField;
                    initializationScope.lastVisibleFieldID = originalField.id;
                    ASTNode.resolveAnnotations((BlockScope)initializationScope, fieldDecl.annotations, originalField);
                }
                finally {
                    initializationScope.initializedField = previousField;
                    initializationScope.lastVisibleFieldID = previousFieldID;
                }
            }
        }
        return originalField.tagBits;
    }

    public final boolean isDefault() {
        return !this.isPublic() && !this.isProtected() && !this.isPrivate();
    }

    public final boolean isDeprecated() {
        return (this.modifiers & 0x100000) != 0;
    }

    public final boolean isPrivate() {
        return (this.modifiers & 2) != 0;
    }

    public final boolean isOrEnclosedByPrivateType() {
        if ((this.modifiers & 2) != 0) {
            return true;
        }
        return this.declaringClass != null && this.declaringClass.isOrEnclosedByPrivateType();
    }

    public final boolean isProtected() {
        return (this.modifiers & 4) != 0;
    }

    public final boolean isPublic() {
        return (this.modifiers & 1) != 0;
    }

    public final boolean isStatic() {
        return (this.modifiers & 8) != 0;
    }

    public final boolean isSynthetic() {
        return (this.modifiers & 0x1000) != 0;
    }

    public final boolean isTransient() {
        return (this.modifiers & 0x80) != 0;
    }

    public final boolean isUsed() {
        return (this.modifiers & 0x8000000) != 0 || this.compoundUseFlag > 0;
    }

    public final boolean isUsedOnlyInCompound() {
        return (this.modifiers & 0x8000000) == 0 && this.compoundUseFlag > 0;
    }

    public final boolean isViewedAsDeprecated() {
        return (this.modifiers & 0x300000) != 0;
    }

    @Override
    public final boolean isVolatile() {
        return (this.modifiers & 0x40) != 0;
    }

    @Override
    public final int kind() {
        return 1;
    }

    public boolean isRecordComponent() {
        return this.declaringClass != null && this.declaringClass.isRecord() && !this.isStatic() && (this.modifiers & 0x1000000) != 0;
    }

    public FieldBinding original() {
        return this;
    }

    @Override
    public void setAnnotations(AnnotationBinding[] annotations, boolean forceStore) {
        this.declaringClass.storeAnnotations(this, annotations, forceStore);
    }

    public FieldDeclaration sourceField() {
        SourceTypeBinding sourceType;
        try {
            sourceType = (SourceTypeBinding)this.declaringClass;
        }
        catch (ClassCastException classCastException) {
            return null;
        }
        FieldDeclaration[] fields = sourceType.scope.referenceContext.fields;
        if (fields != null) {
            int i = fields.length;
            while (--i >= 0) {
                if (this != fields[i].binding) continue;
                return fields[i];
            }
        }
        return null;
    }
}

