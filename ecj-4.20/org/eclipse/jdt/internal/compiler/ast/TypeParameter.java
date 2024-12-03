/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.ast;

import java.util.List;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.AbstractVariableDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.codegen.AnnotationContext;
import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
import org.eclipse.jdt.internal.compiler.lookup.AnnotationBinding;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodScope;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;
import org.eclipse.jdt.internal.compiler.lookup.TypeVariableBinding;

public class TypeParameter
extends AbstractVariableDeclaration {
    public TypeVariableBinding binding;
    public TypeReference[] bounds;

    @Override
    public int getKind() {
        return 6;
    }

    public void checkBounds(Scope scope) {
        if (this.type != null) {
            this.type.checkBounds(scope);
        }
        if (this.bounds != null) {
            int i = 0;
            int length = this.bounds.length;
            while (i < length) {
                this.bounds[i].checkBounds(scope);
                ++i;
            }
        }
    }

    public void getAllAnnotationContexts(int targetType, int typeParameterIndex, List<AnnotationContext> allAnnotationContexts) {
        TypeReference.AnnotationCollector collector = new TypeReference.AnnotationCollector(this, targetType, typeParameterIndex, allAnnotationContexts);
        if (this.annotations != null) {
            int annotationsLength = this.annotations.length;
            int i = 0;
            while (i < annotationsLength) {
                this.annotations[i].traverse((ASTVisitor)collector, (BlockScope)null);
                ++i;
            }
        }
        switch (collector.targetType) {
            case 0: {
                collector.targetType = 17;
                break;
            }
            case 1: {
                collector.targetType = 18;
            }
        }
        int boundIndex = 0;
        if (this.type != null) {
            if (this.type.resolvedType.isInterface()) {
                boundIndex = 1;
            }
            if ((this.type.bits & 0x100000) != 0) {
                collector.info2 = boundIndex;
                this.type.traverse((ASTVisitor)collector, (BlockScope)null);
            }
        }
        if (this.bounds != null) {
            int boundsLength = this.bounds.length;
            int i = 0;
            while (i < boundsLength) {
                TypeReference bound = this.bounds[i];
                if ((bound.bits & 0x100000) != 0) {
                    collector.info2 = ++boundIndex;
                    bound.traverse((ASTVisitor)collector, (BlockScope)null);
                }
                ++i;
            }
        }
    }

    private void internalResolve(Scope scope, boolean staticContext) {
        Binding existingType;
        if (this.binding != null && (existingType = scope.parent.getBinding(this.name, 4, this, false)) != null && this.binding != existingType && existingType.isValidBinding() && (existingType.kind() != 4100 || !staticContext)) {
            scope.problemReporter().typeHiding(this, existingType);
        }
        if (this.annotations != null || scope.environment().usesNullTypeAnnotations()) {
            this.resolveAnnotations(scope);
        }
        if (CharOperation.equals(this.name, TypeConstants.VAR)) {
            if (scope.compilerOptions().sourceLevel < 0x360000L) {
                scope.problemReporter().varIsReservedTypeNameInFuture(this);
            } else {
                scope.problemReporter().varIsNotAllowedHere(this);
            }
        }
        scope.problemReporter().validateRestrictedKeywords(this.name, this);
    }

    @Override
    public void resolve(BlockScope scope) {
        this.internalResolve(scope, scope.methodScope().isStatic);
    }

    public void resolve(ClassScope scope) {
        this.internalResolve(scope, scope.enclosingSourceType().isStatic());
    }

    public void resolveAnnotations(Scope scope) {
        BlockScope resolutionScope = Scope.typeAnnotationsResolutionScope(scope);
        if (resolutionScope != null) {
            AnnotationBinding[] annotationBindings = TypeParameter.resolveAnnotations(resolutionScope, this.annotations, this.binding, false);
            LookupEnvironment environment = scope.environment();
            boolean isAnnotationBasedNullAnalysisEnabled = environment.globalOptions.isAnnotationBasedNullAnalysisEnabled;
            if (annotationBindings != null && annotationBindings.length > 0) {
                this.binding.setTypeAnnotations(annotationBindings, isAnnotationBasedNullAnalysisEnabled);
                scope.referenceCompilationUnit().compilationResult.hasAnnotations = true;
            }
            if (isAnnotationBasedNullAnalysisEnabled && this.binding != null && this.binding.isValidBinding()) {
                if (!this.binding.hasNullTypeAnnotations() && scope.hasDefaultNullnessFor(128, this.sourceStart())) {
                    AnnotationBinding[] annots = new AnnotationBinding[]{environment.getNonNullAnnotation()};
                    TypeVariableBinding previousBinding = this.binding;
                    this.binding = (TypeVariableBinding)environment.createAnnotatedType((TypeBinding)this.binding, annots);
                    if (scope instanceof MethodScope) {
                        MethodBinding methodBinding;
                        MethodScope methodScope = (MethodScope)scope;
                        if (methodScope.referenceContext instanceof AbstractMethodDeclaration && (methodBinding = ((AbstractMethodDeclaration)methodScope.referenceContext).binding) != null) {
                            methodBinding.updateTypeVariableBinding(previousBinding, this.binding);
                        }
                    }
                }
                this.binding.evaluateNullAnnotations(scope, this);
            }
        }
    }

    @Override
    public StringBuffer printStatement(int indent, StringBuffer output) {
        if (this.annotations != null) {
            TypeParameter.printAnnotations(this.annotations, output);
            output.append(' ');
        }
        output.append(this.name);
        if (this.type != null) {
            output.append(" extends ");
            this.type.print(0, output);
        }
        if (this.bounds != null) {
            int i = 0;
            while (i < this.bounds.length) {
                output.append(" & ");
                this.bounds[i].print(0, output);
                ++i;
            }
        }
        return output;
    }

    @Override
    public void generateCode(BlockScope currentScope, CodeStream codeStream) {
    }

    @Override
    public void traverse(ASTVisitor visitor, BlockScope scope) {
        if (visitor.visit(this, scope)) {
            int i;
            if (this.annotations != null) {
                int annotationsLength = this.annotations.length;
                i = 0;
                while (i < annotationsLength) {
                    this.annotations[i].traverse(visitor, scope);
                    ++i;
                }
            }
            if (this.type != null) {
                this.type.traverse(visitor, scope);
            }
            if (this.bounds != null) {
                int boundsLength = this.bounds.length;
                i = 0;
                while (i < boundsLength) {
                    this.bounds[i].traverse(visitor, scope);
                    ++i;
                }
            }
        }
        visitor.endVisit(this, scope);
    }

    public void traverse(ASTVisitor visitor, ClassScope scope) {
        if (visitor.visit(this, scope)) {
            int i;
            if (this.annotations != null) {
                int annotationsLength = this.annotations.length;
                i = 0;
                while (i < annotationsLength) {
                    this.annotations[i].traverse(visitor, scope);
                    ++i;
                }
            }
            if (this.type != null) {
                this.type.traverse(visitor, scope);
            }
            if (this.bounds != null) {
                int boundsLength = this.bounds.length;
                i = 0;
                while (i < boundsLength) {
                    this.bounds[i].traverse(visitor, scope);
                    ++i;
                }
            }
        }
        visitor.endVisit(this, scope);
    }
}

