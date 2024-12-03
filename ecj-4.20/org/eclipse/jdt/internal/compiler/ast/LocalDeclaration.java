/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.ast;

import java.util.HashSet;
import java.util.List;
import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.ast.AbstractVariableDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.ArrayInitializer;
import org.eclipse.jdt.internal.compiler.ast.ArrayQualifiedTypeReference;
import org.eclipse.jdt.internal.compiler.ast.CastExpression;
import org.eclipse.jdt.internal.compiler.ast.ConditionalExpression;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.ExpressionContext;
import org.eclipse.jdt.internal.compiler.ast.FakedTrackingVariable;
import org.eclipse.jdt.internal.compiler.ast.FunctionalExpression;
import org.eclipse.jdt.internal.compiler.ast.Invocation;
import org.eclipse.jdt.internal.compiler.ast.NullAnnotationMatching;
import org.eclipse.jdt.internal.compiler.ast.QualifiedTypeReference;
import org.eclipse.jdt.internal.compiler.ast.ReferenceExpression;
import org.eclipse.jdt.internal.compiler.ast.SingleTypeReference;
import org.eclipse.jdt.internal.compiler.ast.SwitchExpression;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.codegen.AnnotationContext;
import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
import org.eclipse.jdt.internal.compiler.flow.FlowContext;
import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
import org.eclipse.jdt.internal.compiler.flow.UnconditionalFlowInfo;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.lookup.ArrayBinding;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.InvocationSite;
import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBindingVisitor;
import org.eclipse.jdt.internal.compiler.lookup.TypeVariableBinding;
import org.eclipse.jdt.internal.compiler.parser.RecoveryScanner;

public class LocalDeclaration
extends AbstractVariableDeclaration {
    public LocalVariableBinding binding;

    public LocalDeclaration(char[] name, int sourceStart, int sourceEnd) {
        this.name = name;
        this.sourceStart = sourceStart;
        this.sourceEnd = sourceEnd;
        this.declarationEnd = sourceEnd;
    }

    @Override
    public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo) {
        boolean shouldAnalyseResource;
        if ((flowInfo.tagBits & 1) == 0) {
            this.bits |= 0x40000000;
        }
        if (this.initialization == null) {
            return flowInfo;
        }
        this.initialization.checkNPEbyUnboxing(currentScope, flowContext, flowInfo);
        UnconditionalFlowInfo preInitInfo = null;
        boolean bl = shouldAnalyseResource = this.binding != null && flowInfo.reachMode() == 0 && currentScope.compilerOptions().analyseResourceLeaks && FakedTrackingVariable.isAnyCloseable(this.initialization.resolvedType);
        if (shouldAnalyseResource) {
            preInitInfo = flowInfo.unconditionalCopy();
            FakedTrackingVariable.preConnectTrackerAcrossAssignment(this, this.binding, this.initialization, flowInfo);
        }
        flowInfo = this.initialization.analyseCode(currentScope, flowContext, flowInfo).unconditionalInits();
        if (shouldAnalyseResource) {
            FakedTrackingVariable.handleResourceAssignment(currentScope, preInitInfo, flowInfo, flowContext, this, this.initialization, this.binding);
        } else {
            FakedTrackingVariable.cleanUpAfterAssignment(currentScope, 2, this.initialization);
        }
        int nullStatus = this.initialization.nullStatus(flowInfo, flowContext);
        this.bits = !flowInfo.isDefinitelyAssigned(this.binding) ? (this.bits |= 8) : (this.bits &= 0xFFFFFFF7);
        flowInfo.markAsDefinitelyAssigned(this.binding);
        if (currentScope.compilerOptions().isAnnotationBasedNullAnalysisEnabled) {
            nullStatus = NullAnnotationMatching.checkAssignment(currentScope, flowContext, this.binding, flowInfo, nullStatus, this.initialization, this.initialization.resolvedType);
        }
        if ((this.binding.type.tagBits & 2L) == 0L) {
            flowInfo.markNullStatus(this.binding, nullStatus);
        }
        return flowInfo;
    }

    public void checkModifiers() {
        if ((this.modifiers & 0xFFFF & 0xFFFFFFEF) != 0) {
            this.modifiers = this.modifiers & 0xFFBFFFFF | 0x800000;
        }
    }

    @Override
    public void generateCode(BlockScope currentScope, CodeStream codeStream) {
        if (this.binding.resolvedPosition != -1) {
            codeStream.addVisibleLocalVariable(this.binding);
        }
        if ((this.bits & Integer.MIN_VALUE) == 0) {
            return;
        }
        int pc = codeStream.position;
        if (this.initialization != null) {
            if (this.binding.resolvedPosition < 0) {
                if (this.initialization.constant == Constant.NotAConstant) {
                    this.initialization.generateCode(currentScope, codeStream, false);
                }
            } else {
                this.initialization.generateCode(currentScope, codeStream, true);
                if (this.binding.type.isArrayType() && this.initialization instanceof CastExpression && ((CastExpression)this.initialization).innermostCastedExpression().resolvedType == TypeBinding.NULL) {
                    codeStream.checkcast(this.binding.type);
                }
                codeStream.store(this.binding, false);
                if ((this.bits & 8) != 0) {
                    this.binding.recordInitializationStartPC(codeStream.position);
                }
            }
        }
        codeStream.recordPositionsFrom(pc, this.sourceStart);
    }

    @Override
    public int getKind() {
        return 4;
    }

    public void getAllAnnotationContexts(int targetType, LocalVariableBinding localVariable, List<AnnotationContext> allAnnotationContexts) {
        TypeReference.AnnotationCollector collector = new TypeReference.AnnotationCollector(this, targetType, localVariable, allAnnotationContexts);
        this.traverseWithoutInitializer(collector, null);
    }

    public void getAllAnnotationContexts(int targetType, int parameterIndex, List<AnnotationContext> allAnnotationContexts) {
        TypeReference.AnnotationCollector collector = new TypeReference.AnnotationCollector(this, targetType, parameterIndex, allAnnotationContexts);
        this.traverse(collector, null);
    }

    public boolean isArgument() {
        return false;
    }

    public boolean isReceiver() {
        return false;
    }

    public TypeBinding patchType(TypeBinding newType) {
        TypeBinding[] mentionedTypeVariables = this.findCapturedTypeVariables(newType);
        if (mentionedTypeVariables != null && mentionedTypeVariables.length > 0) {
            newType = newType.upwardsProjection(this.binding.declaringScope, mentionedTypeVariables);
        }
        this.type.resolvedType = newType;
        if (this.binding != null) {
            this.binding.type = newType;
            this.binding.markInitialized();
        }
        return this.type.resolvedType;
    }

    private TypeVariableBinding[] findCapturedTypeVariables(TypeBinding typeBinding) {
        final HashSet mentioned = new HashSet();
        TypeBindingVisitor.visit(new TypeBindingVisitor(){

            @Override
            public boolean visit(TypeVariableBinding typeVariable) {
                if (typeVariable.isCapture()) {
                    mentioned.add(typeVariable);
                }
                return super.visit(typeVariable);
            }
        }, typeBinding);
        if (mentioned.isEmpty()) {
            return null;
        }
        return mentioned.toArray(new TypeVariableBinding[mentioned.size()]);
    }

    private static Expression findPolyExpression(Expression e) {
        if (e instanceof FunctionalExpression) {
            return e;
        }
        if (e instanceof ConditionalExpression) {
            ConditionalExpression ce = (ConditionalExpression)e;
            Expression candidate = LocalDeclaration.findPolyExpression(ce.valueIfTrue);
            if (candidate == null) {
                candidate = LocalDeclaration.findPolyExpression(ce.valueIfFalse);
            }
            if (candidate != null) {
                return candidate;
            }
        }
        if (e instanceof SwitchExpression) {
            SwitchExpression se = (SwitchExpression)e;
            for (Expression re : se.resultExpressions) {
                Expression candidate = LocalDeclaration.findPolyExpression(re);
                if (candidate == null) continue;
                return candidate;
            }
        }
        return null;
    }

    @Override
    public void resolve(BlockScope scope) {
        this.resolve(scope, false);
    }

    public void resolve(final BlockScope scope, boolean isPatternVariable) {
        Binding existingVariable;
        LocalDeclaration.handleNonNullByDefault(scope, this.annotations, this);
        TypeBinding variableType = null;
        boolean variableTypeInferenceError = false;
        boolean isTypeNameVar = this.isTypeNameVar(scope);
        if (isTypeNameVar) {
            if ((this.bits & 0x10) == 0) {
                if (this.initialization != null) {
                    variableType = this.checkInferredLocalVariableInitializer(scope);
                    variableTypeInferenceError = variableType != null;
                } else {
                    scope.problemReporter().varLocalWithoutInitizalier(this);
                    variableType = scope.getJavaLangObject();
                    variableTypeInferenceError = true;
                }
            }
        } else {
            variableType = this.type.resolveType(scope, true);
        }
        this.bits |= this.type.bits & 0x100000;
        this.checkModifiers();
        if (variableType != null) {
            if (variableType == TypeBinding.VOID) {
                scope.problemReporter().variableTypeCannotBeVoid(this);
                return;
            }
            if (variableType.isArrayType() && ((ArrayBinding)variableType).leafComponentType == TypeBinding.VOID) {
                scope.problemReporter().variableTypeCannotBeVoidArray(this);
                return;
            }
        }
        if ((existingVariable = scope.getBinding(this.name, 3, (InvocationSite)this, false)) != null && existingVariable.isValidBinding()) {
            boolean localExists = existingVariable instanceof LocalVariableBinding;
            if (localExists && (this.bits & 0x200000) != 0 && scope.isLambdaSubscope() && this.hiddenVariableDepth == 0) {
                scope.problemReporter().lambdaRedeclaresLocal(this);
            } else if (localExists && this.hiddenVariableDepth == 0) {
                scope.problemReporter().redefineLocal(this);
            } else {
                scope.problemReporter().localVariableHiding(this, existingVariable, false);
            }
        }
        if ((this.modifiers & 0x10) != 0 && this.initialization == null) {
            this.modifiers |= 0x4000000;
        }
        this.binding = isTypeNameVar ? new LocalVariableBinding(this, variableType != null ? variableType : scope.getJavaLangObject(), this.modifiers, false){
            private boolean isInitialized;
            {
                super($anonymous0, $anonymous1, $anonymous2, $anonymous3);
                this.isInitialized = false;
            }

            @Override
            public void markReferenced() {
                if (!this.isInitialized) {
                    scope.problemReporter().varLocalReferencesItself(LocalDeclaration.this);
                    this.type = null;
                    this.isInitialized = true;
                }
            }

            @Override
            public void markInitialized() {
                this.isInitialized = true;
            }
        } : new LocalVariableBinding(this, variableType, this.modifiers, false);
        scope.addLocalVariable(this.binding);
        this.binding.setConstant(Constant.NotAConstant);
        if (variableType == null && this.initialization != null) {
            if (this.initialization instanceof CastExpression) {
                ((CastExpression)this.initialization).setVarTypeDeclaration(true);
            }
            this.initialization.resolveType(scope);
            if (isTypeNameVar && this.initialization.resolvedType != null) {
                if (TypeBinding.equalsEquals(TypeBinding.NULL, this.initialization.resolvedType)) {
                    scope.problemReporter().varLocalInitializedToNull(this);
                    variableTypeInferenceError = true;
                } else if (TypeBinding.equalsEquals(TypeBinding.VOID, this.initialization.resolvedType)) {
                    scope.problemReporter().varLocalInitializedToVoid(this);
                    variableTypeInferenceError = true;
                }
                variableType = this.patchType(this.initialization.resolvedType);
            } else {
                variableTypeInferenceError = true;
            }
        }
        this.binding.markInitialized();
        if (variableTypeInferenceError) {
            return;
        }
        boolean resolveAnnotationsEarly = false;
        if (scope.environment().usesNullTypeAnnotations() && !isTypeNameVar && variableType != null && variableType.isValidBinding()) {
            boolean bl = resolveAnnotationsEarly = this.initialization instanceof Invocation || this.initialization instanceof ConditionalExpression || this.initialization instanceof SwitchExpression || this.initialization instanceof ArrayInitializer;
        }
        if (resolveAnnotationsEarly) {
            LocalDeclaration.resolveAnnotations(scope, this.annotations, this.binding, true);
            variableType = this.type.resolvedType;
        }
        if (this.initialization != null) {
            if (this.initialization instanceof ArrayInitializer) {
                TypeBinding initializationType = this.initialization.resolveTypeExpecting(scope, variableType);
                if (initializationType != null) {
                    ((ArrayInitializer)this.initialization).binding = (ArrayBinding)initializationType;
                    this.initialization.computeConversion(scope, variableType, initializationType);
                }
            } else {
                TypeBinding initializationType;
                this.initialization.setExpressionContext(isTypeNameVar ? ExpressionContext.VANILLA_CONTEXT : ExpressionContext.ASSIGNMENT_CONTEXT);
                this.initialization.setExpectedType(variableType);
                TypeBinding typeBinding = initializationType = this.initialization.resolvedType != null ? this.initialization.resolvedType : this.initialization.resolveType(scope);
                if (initializationType != null) {
                    if (TypeBinding.notEquals(variableType, initializationType)) {
                        scope.compilationUnitScope().recordTypeConversion(variableType, initializationType);
                    }
                    if (this.initialization.isConstantValueOfTypeAssignableToType(initializationType, variableType) || initializationType.isCompatibleWith(variableType, scope)) {
                        this.initialization.computeConversion(scope, variableType, initializationType);
                        if (initializationType.needsUncheckedConversion(variableType)) {
                            scope.problemReporter().unsafeTypeConversion(this.initialization, initializationType, variableType);
                        }
                        if (this.initialization instanceof CastExpression && (this.initialization.bits & 0x4000) == 0) {
                            CastExpression.checkNeedForAssignedCast(scope, variableType, (CastExpression)this.initialization);
                        }
                    } else if (this.isBoxingCompatible(initializationType, variableType, this.initialization, scope)) {
                        this.initialization.computeConversion(scope, variableType, initializationType);
                        if (this.initialization instanceof CastExpression && (this.initialization.bits & 0x4000) == 0) {
                            CastExpression.checkNeedForAssignedCast(scope, variableType, (CastExpression)this.initialization);
                        }
                    } else if ((variableType.tagBits & 0x80L) == 0L) {
                        scope.problemReporter().typeMismatchError(initializationType, variableType, this.initialization, null);
                    }
                }
            }
            if (this.binding == Expression.getDirectBinding(this.initialization)) {
                scope.problemReporter().assignmentHasNoEffect(this, this.name);
            }
            this.binding.setConstant(this.binding.isFinal() ? this.initialization.constant.castTo((variableType.id << 4) + this.initialization.constant.typeID()) : Constant.NotAConstant);
        }
        if (!resolveAnnotationsEarly) {
            LocalDeclaration.resolveAnnotations(scope, this.annotations, this.binding, true);
        }
        Annotation.isTypeUseCompatible(this.type, scope, this.annotations);
        this.validateNullAnnotations(scope);
    }

    void validateNullAnnotations(BlockScope scope) {
        if (!scope.validateNullAnnotation(this.binding.tagBits, this.type, this.annotations)) {
            this.binding.tagBits &= 0xFE7FFFFFFFFFFFFFL;
        }
    }

    private TypeBinding checkInferredLocalVariableInitializer(BlockScope scope) {
        TypeBinding errorType = null;
        if (this.initialization instanceof ArrayInitializer) {
            scope.problemReporter().varLocalCannotBeArrayInitalizers(this);
            errorType = scope.createArrayType(scope.getJavaLangObject(), 1);
        } else {
            Expression polyExpression = LocalDeclaration.findPolyExpression(this.initialization);
            if (polyExpression instanceof ReferenceExpression) {
                scope.problemReporter().varLocalCannotBeMethodReference(this);
                errorType = TypeBinding.NULL;
            } else if (polyExpression != null) {
                scope.problemReporter().varLocalCannotBeLambda(this);
                errorType = TypeBinding.NULL;
            }
        }
        if (this.type.dimensions() > 0 || this.type.extraDimensions() > 0) {
            scope.problemReporter().varLocalCannotBeArray(this);
            errorType = scope.createArrayType(scope.getJavaLangObject(), 1);
        }
        if ((this.bits & 0x400000) != 0) {
            scope.problemReporter().varLocalMultipleDeclarators(this);
            errorType = this.initialization.resolveType(scope);
        }
        return errorType;
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
            this.type.traverse(visitor, scope);
            if (this.initialization != null) {
                this.initialization.traverse(visitor, scope);
            }
        }
        visitor.endVisit(this, scope);
    }

    private void traverseWithoutInitializer(ASTVisitor visitor, BlockScope scope) {
        if (visitor.visit(this, scope)) {
            if (this.annotations != null) {
                int annotationsLength = this.annotations.length;
                int i = 0;
                while (i < annotationsLength) {
                    this.annotations[i].traverse(visitor, scope);
                    ++i;
                }
            }
            this.type.traverse(visitor, scope);
        }
        visitor.endVisit(this, scope);
    }

    public boolean isRecoveredFromLoneIdentifier() {
        return this.name == RecoveryScanner.FAKE_IDENTIFIER && (this.type instanceof SingleTypeReference || this.type instanceof QualifiedTypeReference && !(this.type instanceof ArrayQualifiedTypeReference)) && this.initialization == null && !this.type.isBaseTypeReference();
    }

    public boolean isTypeNameVar(Scope scope) {
        return this.type != null && this.type.isTypeNameVar(scope);
    }
}

