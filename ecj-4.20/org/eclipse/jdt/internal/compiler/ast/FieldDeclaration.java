/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.ast;

import java.util.List;
import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.ast.AbstractVariableDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.ArrayInitializer;
import org.eclipse.jdt.internal.compiler.ast.CastExpression;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.ExpressionContext;
import org.eclipse.jdt.internal.compiler.ast.Javadoc;
import org.eclipse.jdt.internal.compiler.ast.NullAnnotationMatching;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.codegen.AnnotationContext;
import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
import org.eclipse.jdt.internal.compiler.flow.FlowContext;
import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.lookup.ArrayBinding;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodScope;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
import org.eclipse.jdt.internal.compiler.util.Util;

public class FieldDeclaration
extends AbstractVariableDeclaration {
    public FieldBinding binding;
    public Javadoc javadoc;
    public int endPart1Position;
    public int endPart2Position;
    public boolean isARecordComponent;

    public FieldDeclaration() {
    }

    public FieldDeclaration(char[] name, int sourceStart, int sourceEnd) {
        this.name = name;
        this.sourceStart = sourceStart;
        this.sourceEnd = sourceEnd;
    }

    public FlowInfo analyseCode(MethodScope initializationScope, FlowContext flowContext, FlowInfo flowInfo) {
        if (this.binding != null && !this.binding.isUsed() && this.binding.isOrEnclosedByPrivateType() && !initializationScope.referenceCompilationUnit().compilationResult.hasSyntaxError && !this.isARecordComponent) {
            initializationScope.problemReporter().unusedPrivateField(this);
        }
        if (this.binding != null && this.binding.isValidBinding() && this.binding.isStatic() && this.binding.constant(initializationScope) == Constant.NotAConstant && this.binding.declaringClass.isNestedType() && !this.binding.declaringClass.isStatic() && initializationScope.compilerOptions().sourceLevel < 0x3C0000L) {
            initializationScope.problemReporter().unexpectedStaticModifierForField((SourceTypeBinding)this.binding.declaringClass, this);
        }
        if (this.initialization != null) {
            flowInfo = this.initialization.analyseCode(initializationScope, flowContext, flowInfo).unconditionalInits();
            flowInfo.markAsDefinitelyAssigned(this.binding);
        }
        if (this.initialization != null && this.binding != null) {
            CompilerOptions options = initializationScope.compilerOptions();
            if (options.isAnnotationBasedNullAnalysisEnabled && (this.binding.isNonNull() || options.sourceLevel >= 0x340000L)) {
                int nullStatus = this.initialization.nullStatus(flowInfo, flowContext);
                NullAnnotationMatching.checkAssignment(initializationScope, flowContext, this.binding, flowInfo, nullStatus, this.initialization, this.initialization.resolvedType);
            }
            this.initialization.checkNPEbyUnboxing(initializationScope, flowContext, flowInfo);
        }
        return flowInfo;
    }

    @Override
    public void generateCode(BlockScope currentScope, CodeStream codeStream) {
        boolean isStatic;
        if ((this.bits & Integer.MIN_VALUE) == 0) {
            return;
        }
        int pc = codeStream.position;
        if (!(this.initialization == null || (isStatic = this.binding.isStatic()) && this.binding.constant() != Constant.NotAConstant)) {
            if (!isStatic) {
                codeStream.aload_0();
            }
            this.initialization.generateCode(currentScope, codeStream, true);
            if (isStatic) {
                codeStream.fieldAccess((byte)-77, this.binding, null);
            } else {
                codeStream.fieldAccess((byte)-75, this.binding, null);
            }
        }
        codeStream.recordPositionsFrom(pc, this.sourceStart);
    }

    public void getAllAnnotationContexts(int targetType, List<AnnotationContext> allAnnotationContexts) {
        TypeReference.AnnotationCollector collector = new TypeReference.AnnotationCollector(this.type, targetType, allAnnotationContexts);
        int i = 0;
        int max = this.annotations.length;
        while (i < max) {
            Annotation annotation = this.annotations[i];
            annotation.traverse((ASTVisitor)collector, (BlockScope)null);
            ++i;
        }
    }

    @Override
    public int getKind() {
        return this.type == null ? 3 : 1;
    }

    public boolean isStatic() {
        if (this.binding != null) {
            return this.binding.isStatic();
        }
        return (this.modifiers & 8) != 0;
    }

    public boolean isFinal() {
        if (this.binding != null) {
            return this.binding.isFinal();
        }
        return (this.modifiers & 0x10) != 0;
    }

    @Override
    public StringBuffer print(int indent, StringBuffer output) {
        if (this.isARecordComponent) {
            output.append("/* Implicit */");
        }
        return super.print(indent, output);
    }

    @Override
    public StringBuffer printStatement(int indent, StringBuffer output) {
        if (this.javadoc != null) {
            this.javadoc.print(indent, output);
        }
        return super.printStatement(indent, output);
    }

    public void resolve(MethodScope initializationScope) {
        if ((this.bits & 0x10) != 0) {
            return;
        }
        if (this.binding == null || !this.binding.isValidBinding()) {
            return;
        }
        this.bits |= 0x10;
        ClassScope classScope = initializationScope.enclosingClassScope();
        if (classScope != null) {
            FieldBinding existingVariable;
            SourceTypeBinding declaringType = classScope.enclosingSourceType();
            if (declaringType.superclass != null && (existingVariable = classScope.findField(declaringType.superclass, this.name, this, false, true)) != null && existingVariable.isValidBinding() && existingVariable.original() != this.binding && existingVariable.canBeSeenBy(declaringType, this, initializationScope)) {
                initializationScope.problemReporter().fieldHiding(this, existingVariable);
            } else {
                FieldBinding existingField;
                Binding existingVariable2;
                Scope outerScope = classScope.parent;
                if (outerScope.kind != 4 && (existingVariable2 = outerScope.getBinding(this.name, 3, this, false)) != null && existingVariable2.isValidBinding() && existingVariable2 != this.binding && (!(existingVariable2 instanceof FieldBinding) || (existingField = (FieldBinding)existingVariable2).original() != this.binding && (existingField.isStatic() || !declaringType.isStatic()))) {
                    initializationScope.problemReporter().fieldHiding(this, existingVariable2);
                }
            }
        }
        if (this.type != null) {
            this.type.resolvedType = this.binding.type;
        }
        FieldBinding previousField = initializationScope.initializedField;
        int previousFieldID = initializationScope.lastVisibleFieldID;
        try {
            initializationScope.initializedField = this.binding;
            initializationScope.lastVisibleFieldID = this.binding.id;
            FieldDeclaration.resolveAnnotations((BlockScope)initializationScope, this.annotations, this.binding);
            if (this.annotations != null) {
                int i = 0;
                int max = this.annotations.length;
                while (i < max) {
                    TypeBinding resolvedAnnotationType = this.annotations[i].resolvedType;
                    if (resolvedAnnotationType != null && (resolvedAnnotationType.getAnnotationTagBits() & 0x20000000000000L) != 0L) {
                        this.bits |= 0x100000;
                        break;
                    }
                    ++i;
                }
            }
            if ((this.binding.getAnnotationTagBits() & 0x400000000000L) == 0L && (this.binding.modifiers & 0x100000) != 0 && initializationScope.compilerOptions().sourceLevel >= 0x310000L) {
                initializationScope.problemReporter().missingDeprecatedAnnotationForField(this);
            }
            if (this.initialization == null) {
                this.binding.setConstant(Constant.NotAConstant);
            } else {
                this.binding.setConstant(Constant.NotAConstant);
                TypeBinding fieldType = this.binding.type;
                this.initialization.setExpressionContext(ExpressionContext.ASSIGNMENT_CONTEXT);
                this.initialization.setExpectedType(fieldType);
                if (this.initialization instanceof ArrayInitializer) {
                    TypeBinding initializationType = this.initialization.resolveTypeExpecting(initializationScope, fieldType);
                    if (initializationType != null) {
                        ((ArrayInitializer)this.initialization).binding = (ArrayBinding)initializationType;
                        this.initialization.computeConversion(initializationScope, fieldType, initializationType);
                    }
                } else {
                    TypeBinding initializationType = this.initialization.resolveType(initializationScope);
                    if (initializationType != null) {
                        if (TypeBinding.notEquals(fieldType, initializationType)) {
                            initializationScope.compilationUnitScope().recordTypeConversion(fieldType, initializationType);
                        }
                        if (this.initialization.isConstantValueOfTypeAssignableToType(initializationType, fieldType) || initializationType.isCompatibleWith(fieldType, classScope)) {
                            this.initialization.computeConversion(initializationScope, fieldType, initializationType);
                            if (initializationType.needsUncheckedConversion(fieldType)) {
                                initializationScope.problemReporter().unsafeTypeConversion(this.initialization, initializationType, fieldType);
                            }
                            if (this.initialization instanceof CastExpression && (this.initialization.bits & 0x4000) == 0) {
                                CastExpression.checkNeedForAssignedCast(initializationScope, fieldType, (CastExpression)this.initialization);
                            }
                        } else if (this.isBoxingCompatible(initializationType, fieldType, this.initialization, initializationScope)) {
                            this.initialization.computeConversion(initializationScope, fieldType, initializationType);
                            if (this.initialization instanceof CastExpression && (this.initialization.bits & 0x4000) == 0) {
                                CastExpression.checkNeedForAssignedCast(initializationScope, fieldType, (CastExpression)this.initialization);
                            }
                        } else if (((fieldType.tagBits | initializationType.tagBits) & 0x80L) == 0L) {
                            initializationScope.problemReporter().typeMismatchError(initializationType, fieldType, this.initialization, null);
                        }
                        if (this.binding.isFinal()) {
                            this.binding.setConstant(this.initialization.constant.castTo((this.binding.type.id << 4) + this.initialization.constant.typeID()));
                        }
                    } else {
                        this.binding.setConstant(Constant.NotAConstant);
                    }
                }
                if (this.binding == Expression.getDirectBinding(this.initialization)) {
                    initializationScope.problemReporter().assignmentHasNoEffect(this, this.name);
                }
            }
        }
        finally {
            initializationScope.initializedField = previousField;
            initializationScope.lastVisibleFieldID = previousFieldID;
            if (this.binding.constant(initializationScope) == null) {
                this.binding.setConstant(Constant.NotAConstant);
            }
        }
    }

    public void resolveJavadoc(MethodScope initializationScope) {
        if (this.javadoc != null) {
            FieldBinding previousField = initializationScope.initializedField;
            int previousFieldID = initializationScope.lastVisibleFieldID;
            try {
                initializationScope.initializedField = this.binding;
                if (this.binding != null) {
                    initializationScope.lastVisibleFieldID = this.binding.id;
                }
                this.javadoc.resolve(initializationScope);
            }
            finally {
                initializationScope.initializedField = previousField;
                initializationScope.lastVisibleFieldID = previousFieldID;
            }
        } else if (this.binding != null && this.binding.declaringClass != null && !this.binding.declaringClass.isLocalType()) {
            int javadocVisibility = this.binding.modifiers & 7;
            ProblemReporter reporter = initializationScope.problemReporter();
            int severity = reporter.computeSeverity(-1610612250);
            if (severity != 256) {
                ClassScope classScope = initializationScope.enclosingClassScope();
                if (classScope != null) {
                    javadocVisibility = Util.computeOuterMostVisibility(classScope.referenceType(), javadocVisibility);
                }
                int javadocModifiers = this.binding.modifiers & 0xFFFFFFF8 | javadocVisibility;
                reporter.javadocMissing(this.sourceStart, this.sourceEnd, severity, javadocModifiers);
            }
        }
    }

    public void traverse(ASTVisitor visitor, MethodScope scope) {
        if (visitor.visit(this, scope)) {
            if (this.javadoc != null) {
                this.javadoc.traverse(visitor, scope);
            }
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
            if (this.initialization != null) {
                this.initialization.traverse(visitor, scope);
            }
        }
        visitor.endVisit(this, scope);
    }
}

