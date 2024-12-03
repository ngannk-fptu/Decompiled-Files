/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.ast;

import java.util.List;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.CompilationResult;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.ast.FakedTrackingVariable;
import org.eclipse.jdt.internal.compiler.ast.RecordComponent;
import org.eclipse.jdt.internal.compiler.ast.Statement;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeParameter;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.codegen.AnnotationContext;
import org.eclipse.jdt.internal.compiler.flow.ExceptionHandlingFlowContext;
import org.eclipse.jdt.internal.compiler.flow.FlowContext;
import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.internal.compiler.lookup.LocalTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.MemberTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;
import org.eclipse.jdt.internal.compiler.lookup.TypeVariableBinding;
import org.eclipse.jdt.internal.compiler.parser.Parser;
import org.eclipse.jdt.internal.compiler.problem.AbortMethod;

public class MethodDeclaration
extends AbstractMethodDeclaration {
    public TypeReference returnType;
    public TypeParameter[] typeParameters;

    public MethodDeclaration(CompilationResult compilationResult) {
        super(compilationResult);
        this.bits |= 0x100;
    }

    public void analyseCode(ClassScope classScope, FlowContext flowContext, FlowInfo flowInfo) {
        if (this.ignoreFurtherInvestigation) {
            return;
        }
        try {
            if (this.binding == null) {
                return;
            }
            if (!this.binding.isUsed() && !this.binding.isAbstract() && (this.binding.isPrivate() || (this.binding.modifiers & 0x30000000) == 0 && this.binding.isOrEnclosedByPrivateType()) && !classScope.referenceCompilationUnit().compilationResult.hasSyntaxError) {
                this.scope.problemReporter().unusedPrivateMethod(this);
            }
            if (this.binding.declaringClass.isEnum() && (this.selector == TypeConstants.VALUES || this.selector == TypeConstants.VALUEOF)) {
                return;
            }
            if (this.binding.isAbstract() || this.binding.isNative()) {
                return;
            }
            if (this.typeParameters != null && !this.scope.referenceCompilationUnit().compilationResult.hasSyntaxError) {
                int i = 0;
                int length = this.typeParameters.length;
                while (i < length) {
                    TypeParameter typeParameter = this.typeParameters[i];
                    if ((typeParameter.binding.modifiers & 0x8000000) == 0) {
                        this.scope.problemReporter().unusedTypeParameter(typeParameter);
                    }
                    ++i;
                }
            }
            ExceptionHandlingFlowContext methodContext = new ExceptionHandlingFlowContext(flowContext, this, this.binding.thrownExceptions, null, this.scope, FlowInfo.DEAD_END);
            MethodDeclaration.analyseArguments(classScope.environment(), flowInfo, this.arguments, this.binding);
            if (this.binding.declaringClass instanceof MemberTypeBinding && !this.binding.declaringClass.isStatic()) {
                this.bits &= 0xFFFFFEFF;
            }
            if (this.statements != null) {
                CompilerOptions compilerOptions = this.scope.compilerOptions();
                boolean enableSyntacticNullAnalysisForFields = compilerOptions.enableSyntacticNullAnalysisForFields;
                int complaintLevel = (flowInfo.reachMode() & 3) == 0 ? 0 : 1;
                int i = 0;
                int count = this.statements.length;
                while (i < count) {
                    Statement stat = this.statements[i];
                    if ((complaintLevel = stat.complainIfUnreachable(flowInfo, this.scope, complaintLevel, true)) < 2) {
                        flowInfo = stat.analyseCode(this.scope, methodContext, flowInfo);
                    }
                    if (enableSyntacticNullAnalysisForFields) {
                        methodContext.expireNullCheckedFieldInfo();
                    }
                    if (compilerOptions.analyseResourceLeaks) {
                        FakedTrackingVariable.cleanUpUnassigned(this.scope, stat, flowInfo);
                    }
                    ++i;
                }
            } else {
                this.bits &= 0xFFFFFEFF;
            }
            TypeBinding returnTypeBinding = this.binding.returnType;
            if (returnTypeBinding == TypeBinding.VOID || this.isAbstract()) {
                if ((flowInfo.tagBits & 1) == 0) {
                    this.bits |= 0x40;
                }
            } else if (flowInfo != FlowInfo.DEAD_END) {
                this.scope.problemReporter().shouldReturn(returnTypeBinding, this);
            }
            methodContext.complainIfUnusedExceptionHandlers(this);
            this.scope.checkUnusedParameters(this.binding);
            if (!(this.binding.isStatic() || (this.bits & 0x100) == 0 || this.isDefaultMethod() || this.binding.isOverriding() || this.binding.isImplementing())) {
                if (this.binding.isPrivate() || this.binding.isFinal() || this.binding.declaringClass.isFinal()) {
                    this.scope.problemReporter().methodCanBeDeclaredStatic(this);
                } else {
                    this.scope.problemReporter().methodCanBePotentiallyDeclaredStatic(this);
                }
            }
            this.scope.checkUnclosedCloseables(flowInfo, null, null, null);
        }
        catch (AbortMethod abortMethod) {
            this.ignoreFurtherInvestigation = true;
        }
    }

    @Override
    public void getAllAnnotationContexts(int targetType, List allAnnotationContexts) {
        TypeReference.AnnotationCollector collector = new TypeReference.AnnotationCollector(this.returnType, targetType, (List<AnnotationContext>)allAnnotationContexts);
        int i = 0;
        int max = this.annotations.length;
        while (i < max) {
            Annotation annotation = this.annotations[i];
            annotation.traverse((ASTVisitor)collector, (BlockScope)null);
            ++i;
        }
    }

    public boolean hasNullTypeAnnotation(TypeReference.AnnotationPosition position) {
        return TypeReference.containsNullAnnotation(this.annotations) || this.returnType != null && this.returnType.hasNullTypeAnnotation(position);
    }

    @Override
    public boolean isDefaultMethod() {
        return (this.modifiers & 0x10000) != 0;
    }

    @Override
    public boolean isMethod() {
        return true;
    }

    @Override
    public RecordComponent getRecordComponent() {
        if (this.arguments != null && this.arguments.length != 0) {
            return null;
        }
        ClassScope skope = this.scope.classScope();
        TypeDeclaration typeDecl = skope.referenceContext;
        if (!typeDecl.isRecord()) {
            return null;
        }
        if (!skope.referenceContext.isRecord()) {
            return null;
        }
        RecordComponent[] recComps = typeDecl.recordComponents;
        if (recComps == null || recComps.length == 0) {
            return null;
        }
        RecordComponent[] recordComponentArray = recComps;
        int n = recComps.length;
        int n2 = 0;
        while (n2 < n) {
            RecordComponent recComp = recordComponentArray[n2];
            if (recComp != null && recComp.name != null && CharOperation.equals(this.selector, recComp.name)) {
                return recComp;
            }
            ++n2;
        }
        return null;
    }

    @Override
    public void parseStatements(Parser parser, CompilationUnitDeclaration unit) {
        parser.parse(this, unit);
        this.containsSwitchWithTry = parser.switchWithTry;
    }

    @Override
    public StringBuffer printReturnType(int indent, StringBuffer output) {
        if (this.returnType == null) {
            return output;
        }
        return this.returnType.printExpression(0, output).append(' ');
    }

    @Override
    public void resolveStatements() {
        int bindingModifiers;
        long complianceLevel;
        RecordComponent recordComponent;
        if (this.returnType != null && this.binding != null) {
            this.bits |= this.returnType.bits & 0x100000;
            this.returnType.resolvedType = this.binding.returnType;
        }
        if ((recordComponent = this.getRecordComponent()) != null) {
            if (this.returnType != null && TypeBinding.notEquals(this.returnType.resolvedType, recordComponent.type.resolvedType)) {
                this.scope.problemReporter().recordIllegalAccessorReturnType(this.returnType, recordComponent.type.resolvedType);
            }
            if (this.typeParameters != null) {
                this.scope.problemReporter().recordAccessorMethodShouldNotBeGeneric(this);
            }
            if (this.binding != null) {
                if ((this.binding.modifiers & 1) == 0) {
                    this.scope.problemReporter().recordAccessorMethodShouldBePublic(this);
                }
                if ((this.binding.modifiers & 8) != 0) {
                    this.scope.problemReporter().recordAccessorMethodShouldNotBeStatic(this);
                }
            }
            if (this.thrownExceptions != null) {
                this.scope.problemReporter().recordAccessorMethodHasThrowsClause(this);
            }
        }
        if (CharOperation.equals(this.scope.enclosingSourceType().sourceName, this.selector)) {
            this.scope.problemReporter().methodWithConstructorName(this);
        }
        boolean returnsUndeclTypeVar = false;
        if (this.returnType != null && this.returnType.resolvedType instanceof TypeVariableBinding) {
            returnsUndeclTypeVar = true;
        }
        if (this.typeParameters != null) {
            int i = 0;
            int length = this.typeParameters.length;
            while (i < length) {
                TypeParameter typeParameter = this.typeParameters[i];
                this.bits |= typeParameter.bits & 0x100000;
                if (returnsUndeclTypeVar && TypeBinding.equalsEquals(this.typeParameters[i].binding, this.returnType.resolvedType)) {
                    returnsUndeclTypeVar = false;
                }
                ++i;
            }
        }
        CompilerOptions compilerOptions = this.scope.compilerOptions();
        if (this.binding != null && recordComponent == null && (complianceLevel = compilerOptions.complianceLevel) >= 0x310000L) {
            boolean hasUnresolvedArguments;
            int bindingModifiers2 = this.binding.modifiers;
            boolean hasOverrideAnnotation = (this.binding.tagBits & 0x2000000000000L) != 0L;
            boolean bl = hasUnresolvedArguments = (this.binding.tagBits & 0x200L) != 0L;
            if (hasOverrideAnnotation && !hasUnresolvedArguments) {
                if ((bindingModifiers2 & 0x10000008) != 0x10000000 && (complianceLevel < 0x320000L || (bindingModifiers2 & 0x20000008) != 0x20000000)) {
                    this.scope.problemReporter().methodMustOverride(this, complianceLevel);
                }
            } else if (!this.binding.declaringClass.isInterface()) {
                if ((bindingModifiers2 & 0x10000008) == 0x10000000) {
                    this.scope.problemReporter().missingOverrideAnnotation(this);
                } else if (complianceLevel >= 0x320000L && compilerOptions.reportMissingOverrideAnnotationForInterfaceMethodImplementation && this.binding.isImplementing()) {
                    this.scope.problemReporter().missingOverrideAnnotationForInterfaceMethodImplementation(this);
                }
            } else if (complianceLevel >= 0x320000L && compilerOptions.reportMissingOverrideAnnotationForInterfaceMethodImplementation && ((bindingModifiers2 & 0x10000008) == 0x10000000 || this.binding.isImplementing())) {
                this.scope.problemReporter().missingOverrideAnnotationForInterfaceMethodImplementation(this);
            }
        }
        switch (TypeDeclaration.kind(this.scope.referenceType().modifiers)) {
            case 3: {
                if (this.selector == TypeConstants.VALUES || this.selector == TypeConstants.VALUEOF) break;
            }
            case 1: 
            case 5: {
                if ((this.modifiers & 0x1000000) != 0) {
                    if ((this.modifiers & 0x100) != 0 || (this.modifiers & 0x400) != 0) break;
                    this.scope.problemReporter().methodNeedBody(this);
                    break;
                }
                if ((this.modifiers & 0x100) != 0 || (this.modifiers & 0x400) != 0) {
                    this.scope.problemReporter().methodNeedingNoBody(this);
                    break;
                }
                if (this.binding != null && !this.binding.isStatic() && !(this.binding.declaringClass instanceof LocalTypeBinding) && !returnsUndeclTypeVar) break;
                this.bits &= 0xFFFFFEFF;
                break;
            }
            case 2: {
                boolean isPrivateMethod;
                if (compilerOptions.sourceLevel < 0x340000L || (this.modifiers & 0x1000400) != 0x1000000) break;
                boolean bl = isPrivateMethod = compilerOptions.sourceLevel >= 0x350000L && (this.modifiers & 2) != 0;
                if (!isPrivateMethod && (this.modifiers & 0x10008) == 0) break;
                this.scope.problemReporter().methodNeedBody(this);
            }
        }
        super.resolveStatements();
        if (compilerOptions.getSeverity(0x20100000) != 256 && this.binding != null && ((bindingModifiers = this.binding.modifiers) & 0x30000000) == 0x10000000 && (this.bits & 0x10) == 0) {
            this.scope.problemReporter().overridesMethodWithoutSuperInvocation(this.binding);
        }
    }

    @Override
    public void traverse(ASTVisitor visitor, ClassScope classScope) {
        if (visitor.visit(this, classScope)) {
            int i;
            if (this.javadoc != null) {
                this.javadoc.traverse(visitor, this.scope);
            }
            if (this.annotations != null) {
                int annotationsLength = this.annotations.length;
                i = 0;
                while (i < annotationsLength) {
                    this.annotations[i].traverse(visitor, this.scope);
                    ++i;
                }
            }
            if (this.typeParameters != null) {
                int typeParametersLength = this.typeParameters.length;
                i = 0;
                while (i < typeParametersLength) {
                    this.typeParameters[i].traverse(visitor, this.scope);
                    ++i;
                }
            }
            if (this.returnType != null) {
                this.returnType.traverse(visitor, this.scope);
            }
            if (this.arguments != null) {
                int argumentLength = this.arguments.length;
                i = 0;
                while (i < argumentLength) {
                    this.arguments[i].traverse(visitor, this.scope);
                    ++i;
                }
            }
            if (this.thrownExceptions != null) {
                int thrownExceptionsLength = this.thrownExceptions.length;
                i = 0;
                while (i < thrownExceptionsLength) {
                    this.thrownExceptions[i].traverse(visitor, this.scope);
                    ++i;
                }
            }
            if (this.statements != null) {
                int statementsLength = this.statements.length;
                i = 0;
                while (i < statementsLength) {
                    this.statements[i].traverse(visitor, this.scope);
                    ++i;
                }
            }
        }
        visitor.endVisit(this, classScope);
    }

    @Override
    public TypeParameter[] typeParameters() {
        return this.typeParameters;
    }
}

