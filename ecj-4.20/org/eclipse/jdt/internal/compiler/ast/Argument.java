/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.CompactConstructorDeclaration;
import org.eclipse.jdt.internal.compiler.ast.LocalDeclaration;
import org.eclipse.jdt.internal.compiler.ast.RecordComponent;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.CatchParameterBinding;
import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
import org.eclipse.jdt.internal.compiler.lookup.InvocationSite;
import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodScope;
import org.eclipse.jdt.internal.compiler.lookup.RecordComponentBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;

public class Argument
extends LocalDeclaration {
    private static final char[] SET = "set".toCharArray();

    public Argument(char[] name, long posNom, TypeReference tr, int modifiers) {
        super(name, (int)(posNom >>> 32), (int)posNom);
        this.declarationSourceEnd = (int)posNom;
        this.modifiers = modifiers;
        this.type = tr;
        if (tr != null) {
            this.bits |= tr.bits & 0x100000;
        }
        this.bits |= 0x40000004;
    }

    public Argument(char[] name, long posNom, TypeReference tr, int modifiers, boolean typeElided) {
        super(name, (int)(posNom >>> 32), (int)posNom);
        this.declarationSourceEnd = (int)posNom;
        this.modifiers = modifiers;
        this.type = tr;
        if (tr != null) {
            this.bits |= tr.bits & 0x100000;
        }
        this.bits |= 0x40000006;
    }

    @Override
    public boolean isRecoveredFromLoneIdentifier() {
        return false;
    }

    public TypeBinding createBinding(MethodScope scope, TypeBinding typeBinding) {
        MethodBinding methodBinding;
        AbstractMethodDeclaration methodDecl;
        if (this.binding == null) {
            this.binding = new LocalVariableBinding((LocalDeclaration)this, typeBinding, this.modifiers, scope);
        } else if (!this.binding.type.isValidBinding() && (methodDecl = scope.referenceMethod()) != null && (methodBinding = methodDecl.binding) != null) {
            methodBinding.tagBits |= 0x200L;
        }
        if ((this.binding.tagBits & 0x200000000L) == 0L) {
            Annotation[] annots = this.annotations;
            long sourceLevel = scope.compilerOptions().sourceLevel;
            if (sourceLevel >= 0x3A0000L && annots == null) {
                annots = this.getCorrespondingRecordComponentAnnotationsIfApplicable(scope.referenceMethod());
                annots = ASTNode.copyRecordComponentAnnotations(scope, this.binding, annots);
            }
            if (annots != null) {
                Argument.resolveAnnotations(scope, annots, this.binding, true);
            }
            if (sourceLevel >= 0x340000L) {
                Annotation.isTypeUseCompatible(this.type, scope, annots);
                scope.validateNullAnnotation(this.binding.tagBits, this.type, annots);
            }
        }
        this.binding.declaration = this;
        return this.binding.type;
    }

    private Annotation[] getCorrespondingRecordComponentAnnotationsIfApplicable(AbstractMethodDeclaration methodDecl) {
        if (methodDecl != null && methodDecl.isConstructor() && (methodDecl.bits & 0x200) != 0 && (methodDecl.bits & 0x400) != 0) {
            ReferenceBinding referenceBinding;
            MethodBinding methodBinding = methodDecl.binding;
            ReferenceBinding referenceBinding2 = referenceBinding = methodBinding == null ? null : methodBinding.declaringClass;
            if (referenceBinding instanceof SourceTypeBinding) {
                SourceTypeBinding sourceTypeBinding = (SourceTypeBinding)referenceBinding;
                assert (sourceTypeBinding.isRecord());
                sourceTypeBinding.components();
                RecordComponentBinding recordComponentBinding = sourceTypeBinding.getRecordComponent(this.name);
                if (recordComponentBinding != null) {
                    RecordComponent recordComponent = recordComponentBinding.sourceRecordComponent();
                    return recordComponent.annotations;
                }
            }
        }
        return null;
    }

    public TypeBinding bind(MethodScope scope, TypeBinding typeBinding, boolean used) {
        TypeBinding newTypeBinding = this.createBinding(scope, typeBinding);
        Binding existingVariable = scope.getBinding(this.name, 3, (InvocationSite)this, false);
        if (existingVariable != null && existingVariable.isValidBinding()) {
            boolean localExists = existingVariable instanceof LocalVariableBinding;
            if (localExists && this.hiddenVariableDepth == 0) {
                if ((this.bits & 0x200000) != 0 && scope.isLambdaSubscope()) {
                    scope.problemReporter().lambdaRedeclaresArgument(this);
                } else if (!(scope.referenceContext instanceof CompactConstructorDeclaration)) {
                    scope.problemReporter().redefineArgument(this);
                }
            } else {
                boolean isSpecialArgument = false;
                if (existingVariable instanceof FieldBinding) {
                    AbstractMethodDeclaration methodDecl;
                    if (scope.isInsideConstructor()) {
                        isSpecialArgument = true;
                    } else if (!((FieldBinding)existingVariable).isRecordComponent() && (methodDecl = scope.referenceMethod()) != null && CharOperation.prefixEquals(SET, methodDecl.selector)) {
                        isSpecialArgument = true;
                    }
                }
                scope.problemReporter().localVariableHiding(this, existingVariable, isSpecialArgument);
            }
        }
        scope.addLocalVariable(this.binding);
        this.binding.useFlag = used ? 1 : 0;
        return newTypeBinding;
    }

    @Override
    public int getKind() {
        return (this.bits & 4) != 0 ? 5 : 4;
    }

    @Override
    public boolean isArgument() {
        return true;
    }

    public boolean isVarArgs() {
        return this.type != null && (this.type.bits & 0x4000) != 0;
    }

    public boolean hasElidedType() {
        return (this.bits & 2) != 0;
    }

    public boolean hasNullTypeAnnotation(TypeReference.AnnotationPosition position) {
        return TypeReference.containsNullAnnotation(this.annotations) || this.type != null && this.type.hasNullTypeAnnotation(position);
    }

    @Override
    public StringBuffer print(int indent, StringBuffer output) {
        Argument.printIndent(indent, output);
        Argument.printModifiers(this.modifiers, output);
        if (this.annotations != null) {
            Argument.printAnnotations(this.annotations, output);
            output.append(' ');
        }
        if (this.type == null) {
            output.append("<no type> ");
        } else {
            this.type.print(0, output).append(' ');
        }
        return output.append(this.name);
    }

    @Override
    public StringBuffer printStatement(int indent, StringBuffer output) {
        return this.print(indent, output).append(';');
    }

    public TypeBinding resolveForCatch(BlockScope scope) {
        boolean hasError;
        TypeBinding exceptionType = this.type.resolveType(scope, true);
        if (exceptionType == null) {
            hasError = true;
        } else {
            hasError = false;
            switch (exceptionType.kind()) {
                case 260: {
                    if (!exceptionType.isBoundParameterizedType()) break;
                    hasError = true;
                    scope.problemReporter().invalidParameterizedExceptionType(exceptionType, this);
                    break;
                }
                case 4100: {
                    scope.problemReporter().invalidTypeVariableAsException(exceptionType, this);
                    hasError = true;
                }
            }
            if (exceptionType.findSuperTypeOriginatingFrom(21, true) == null && exceptionType.isValidBinding()) {
                scope.problemReporter().cannotThrowType(this.type, exceptionType);
                hasError = true;
            }
        }
        Binding existingVariable = scope.getBinding(this.name, 3, (InvocationSite)this, false);
        if (existingVariable != null && existingVariable.isValidBinding()) {
            if (existingVariable instanceof LocalVariableBinding && this.hiddenVariableDepth == 0) {
                scope.problemReporter().redefineArgument(this);
            } else {
                scope.problemReporter().localVariableHiding(this, existingVariable, false);
            }
        }
        if ((this.type.bits & 0x20000000) != 0) {
            this.binding = new CatchParameterBinding((LocalDeclaration)this, exceptionType, this.modifiers | 0x10, false);
            this.binding.tagBits |= 0x1000L;
        } else {
            this.binding = new CatchParameterBinding((LocalDeclaration)this, exceptionType, this.modifiers, false);
        }
        Argument.resolveAnnotations(scope, this.annotations, this.binding, true);
        Annotation.isTypeUseCompatible(this.type, scope, this.annotations);
        if (scope.compilerOptions().isAnnotationBasedNullAnalysisEnabled && (this.type.hasNullTypeAnnotation(TypeReference.AnnotationPosition.ANY) || TypeReference.containsNullAnnotation(this.annotations))) {
            scope.problemReporter().nullAnnotationUnsupportedLocation(this.type);
        }
        scope.addLocalVariable(this.binding);
        this.binding.setConstant(Constant.NotAConstant);
        if (hasError) {
            return null;
        }
        return exceptionType;
    }

    @Override
    public void traverse(ASTVisitor visitor, BlockScope scope) {
        if (visitor.visit(this, scope)) {
            if (this.annotations != null) {
                int annotationsLength = this.annotations.length;
                int i = 0;
                while (i < annotationsLength) {
                    this.annotations[i].traverse(visitor, scope);
                    ++i;
                }
            }
            if (this.type != null) {
                this.type.traverse(visitor, scope);
            }
        }
        visitor.endVisit(this, scope);
    }

    public void traverse(ASTVisitor visitor, ClassScope scope) {
        if (visitor.visit(this, scope)) {
            if (this.annotations != null) {
                int annotationsLength = this.annotations.length;
                int i = 0;
                while (i < annotationsLength) {
                    this.annotations[i].traverse(visitor, scope);
                    ++i;
                }
            }
            if (this.type != null) {
                this.type.traverse(visitor, scope);
            }
        }
        visitor.endVisit(this, scope);
    }
}

