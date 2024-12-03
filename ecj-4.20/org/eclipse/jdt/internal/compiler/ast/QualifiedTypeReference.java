/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.ArrayQualifiedTypeReference;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
import org.eclipse.jdt.internal.compiler.lookup.PackageBinding;
import org.eclipse.jdt.internal.compiler.lookup.ProblemReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.SplitPackageBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeVariableBinding;
import org.eclipse.jdt.internal.compiler.problem.AbortCompilation;

public class QualifiedTypeReference
extends TypeReference {
    public char[][] tokens;
    public long[] sourcePositions;

    public QualifiedTypeReference(char[][] sources, long[] poss) {
        this.tokens = sources;
        this.sourcePositions = poss;
        this.sourceStart = (int)(this.sourcePositions[0] >>> 32);
        this.sourceEnd = (int)(this.sourcePositions[this.sourcePositions.length - 1] & 0xFFFFFFFFL);
    }

    @Override
    public TypeReference augmentTypeWithAdditionalDimensions(int additionalDimensions, Annotation[][] additionalAnnotations, boolean isVarargs) {
        int totalDimensions = this.dimensions() + additionalDimensions;
        Annotation[][] allAnnotations = this.getMergedAnnotationsOnDimensions(additionalDimensions, additionalAnnotations);
        ArrayQualifiedTypeReference arrayQualifiedTypeReference = new ArrayQualifiedTypeReference(this.tokens, totalDimensions, allAnnotations, this.sourcePositions);
        arrayQualifiedTypeReference.annotations = this.annotations;
        arrayQualifiedTypeReference.bits |= this.bits & 0x100000;
        if (!isVarargs) {
            arrayQualifiedTypeReference.extendedDimensions = additionalDimensions;
        }
        return arrayQualifiedTypeReference;
    }

    protected TypeBinding findNextTypeBinding(int tokenIndex, Scope scope, PackageBinding packageBinding) {
        LookupEnvironment env = scope.environment();
        try {
            env.missingClassFileLocation = this;
            if (this.resolvedType == null) {
                this.resolvedType = scope.getType(this.tokens[tokenIndex], packageBinding);
            } else {
                this.resolvedType = scope.getMemberType(this.tokens[tokenIndex], (ReferenceBinding)this.resolvedType);
                if (!this.resolvedType.isValidBinding()) {
                    this.resolvedType = new ProblemReferenceBinding(CharOperation.subarray(this.tokens, 0, tokenIndex + 1), (ReferenceBinding)this.resolvedType.closestMatch(), this.resolvedType.problemId());
                }
            }
            TypeBinding typeBinding = this.resolvedType;
            return typeBinding;
        }
        catch (AbortCompilation e) {
            e.updateContext(this, scope.referenceCompilationUnit().compilationResult);
            throw e;
        }
        finally {
            env.missingClassFileLocation = null;
        }
    }

    @Override
    public char[] getLastToken() {
        return this.tokens[this.tokens.length - 1];
    }

    protected void rejectAnnotationsOnPackageQualifiers(Scope scope, PackageBinding packageBinding) {
        if (packageBinding == null || this.annotations == null) {
            return;
        }
        int i = packageBinding.compoundName.length;
        int j = 0;
        while (j < i) {
            Annotation[] qualifierAnnot = this.annotations[j];
            if (qualifierAnnot != null && qualifierAnnot.length > 0) {
                if (j == 0) {
                    int k = 0;
                    while (k < qualifierAnnot.length) {
                        scope.problemReporter().typeAnnotationAtQualifiedName(qualifierAnnot[k]);
                        ++k;
                    }
                } else {
                    scope.problemReporter().misplacedTypeAnnotations(qualifierAnnot[0], qualifierAnnot[qualifierAnnot.length - 1]);
                    this.annotations[j] = null;
                }
            }
            ++j;
        }
    }

    protected static void rejectAnnotationsOnStaticMemberQualififer(Scope scope, ReferenceBinding currentType, Annotation[] qualifierAnnot) {
        if (currentType.isMemberType() && currentType.isStatic() && qualifierAnnot != null && qualifierAnnot.length > 0) {
            scope.problemReporter().illegalTypeAnnotationsInStaticMemberAccess(qualifierAnnot[0], qualifierAnnot[qualifierAnnot.length - 1]);
        }
    }

    @Override
    protected TypeBinding getTypeBinding(Scope scope) {
        PackageBinding uniquePackage;
        int typeStart;
        if (this.resolvedType != null) {
            return this.resolvedType;
        }
        Binding binding = scope.getPackage(this.tokens);
        if (this.resolvedType != null) {
            return this.resolvedType;
        }
        if (binding != null && !binding.isValidBinding()) {
            if (binding instanceof ProblemReferenceBinding && binding.problemId() == 1) {
                ProblemReferenceBinding problemBinding = (ProblemReferenceBinding)binding;
                Binding pkg = scope.getTypeOrPackage(this.tokens);
                return new ProblemReferenceBinding(problemBinding.compoundName, pkg instanceof PackageBinding ? null : scope.environment().createMissingType(null, this.tokens), 1);
            }
            return (ReferenceBinding)binding;
        }
        PackageBinding packageBinding = binding == null ? null : (PackageBinding)binding;
        int n = typeStart = packageBinding == null ? 0 : packageBinding.compoundName.length;
        if (packageBinding != null && (uniquePackage = packageBinding.getVisibleFor(scope.module(), false)) instanceof SplitPackageBinding) {
            CompilerOptions compilerOptions = scope.compilerOptions();
            boolean inJdtDebugCompileMode = compilerOptions.enableJdtDebugCompileMode;
            if (!inJdtDebugCompileMode) {
                SplitPackageBinding splitPackage = (SplitPackageBinding)uniquePackage;
                scope.problemReporter().conflictingPackagesFromModules(splitPackage, scope.module(), this.sourceStart, (int)this.sourcePositions[typeStart - 1]);
                this.resolvedType = new ProblemReferenceBinding(this.tokens, null, 3);
                return null;
            }
        }
        this.rejectAnnotationsOnPackageQualifiers(scope, packageBinding);
        boolean isClassScope = scope.kind == 3;
        ReferenceBinding qualifiedType = null;
        int i = typeStart;
        int max = this.tokens.length;
        int last = max - 1;
        while (i < max) {
            this.findNextTypeBinding(i, scope, packageBinding);
            if (!this.resolvedType.isValidBinding()) {
                return this.resolvedType;
            }
            if (i == 0 && this.resolvedType.isTypeVariable() && ((TypeVariableBinding)this.resolvedType).firstBound == null) {
                scope.problemReporter().illegalAccessFromTypeVariable((TypeVariableBinding)this.resolvedType, this);
                return null;
            }
            if (i <= last && this.isTypeUseDeprecated(this.resolvedType, scope)) {
                this.reportDeprecatedType(this.resolvedType, scope, i);
            }
            if (isClassScope && ((ClassScope)scope).detectHierarchyCycle(this.resolvedType, this)) {
                return null;
            }
            ReferenceBinding currentType = (ReferenceBinding)this.resolvedType;
            if (qualifiedType != null) {
                boolean rawQualified;
                ReferenceBinding enclosingType;
                if (this.annotations != null) {
                    QualifiedTypeReference.rejectAnnotationsOnStaticMemberQualififer(scope, currentType, this.annotations[i - 1]);
                }
                if ((enclosingType = currentType.enclosingType()) != null && TypeBinding.notEquals(enclosingType.erasure(), qualifiedType.erasure())) {
                    qualifiedType = enclosingType;
                }
                qualifiedType = currentType.isGenericType() ? scope.environment().createRawType(currentType, qualifiedType) : (!currentType.hasEnclosingInstanceContext() ? currentType : ((rawQualified = qualifiedType.isRawType()) ? scope.environment().createRawType((ReferenceBinding)currentType.erasure(), qualifiedType) : (qualifiedType.isParameterizedType() && TypeBinding.equalsEquals(qualifiedType.erasure(), currentType.enclosingType().erasure()) ? scope.environment().createParameterizedType((ReferenceBinding)currentType.erasure(), null, qualifiedType) : currentType)));
            } else {
                qualifiedType = currentType.isGenericType() ? (ReferenceBinding)scope.environment().convertToRawType(currentType, false) : currentType;
            }
            this.recordResolution(scope.environment(), qualifiedType);
            ++i;
        }
        this.resolvedType = qualifiedType;
        return this.resolvedType;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void recordResolution(LookupEnvironment env, TypeBinding typeFound) {
        if (typeFound != null && typeFound.isValidBinding()) {
            LookupEnvironment lookupEnvironment = env.root;
            synchronized (lookupEnvironment) {
                int i = 0;
                while (i < env.root.resolutionListeners.length) {
                    env.root.resolutionListeners[i].recordResolution(this, typeFound);
                    ++i;
                }
            }
        }
    }

    @Override
    public char[][] getTypeName() {
        return this.tokens;
    }

    @Override
    public StringBuffer printExpression(int indent, StringBuffer output) {
        int i = 0;
        while (i < this.tokens.length) {
            if (i > 0) {
                output.append('.');
            }
            if (this.annotations != null && this.annotations[i] != null) {
                QualifiedTypeReference.printAnnotations(this.annotations[i], output);
                output.append(' ');
            }
            output.append(this.tokens[i]);
            ++i;
        }
        return output;
    }

    @Override
    public void traverse(ASTVisitor visitor, BlockScope scope) {
        if (visitor.visit(this, scope) && this.annotations != null) {
            int annotationsLevels = this.annotations.length;
            int i = 0;
            while (i < annotationsLevels) {
                int annotationsLength = this.annotations[i] == null ? 0 : this.annotations[i].length;
                int j = 0;
                while (j < annotationsLength) {
                    this.annotations[i][j].traverse(visitor, scope);
                    ++j;
                }
                ++i;
            }
        }
        visitor.endVisit(this, scope);
    }

    @Override
    public void traverse(ASTVisitor visitor, ClassScope scope) {
        if (visitor.visit(this, scope) && this.annotations != null) {
            int annotationsLevels = this.annotations.length;
            int i = 0;
            while (i < annotationsLevels) {
                int annotationsLength = this.annotations[i] == null ? 0 : this.annotations[i].length;
                int j = 0;
                while (j < annotationsLength) {
                    this.annotations[i][j].traverse(visitor, scope);
                    ++j;
                }
                ++i;
            }
        }
        visitor.endVisit(this, scope);
    }

    @Override
    public int getAnnotatableLevels() {
        return this.tokens.length;
    }
}

