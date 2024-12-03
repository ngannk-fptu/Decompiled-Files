/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.ast.Assignment;
import org.eclipse.jdt.internal.compiler.ast.BinaryExpression;
import org.eclipse.jdt.internal.compiler.ast.CastExpression;
import org.eclipse.jdt.internal.compiler.ast.CompoundAssignment;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.FieldReference;
import org.eclipse.jdt.internal.compiler.ast.IntLiteral;
import org.eclipse.jdt.internal.compiler.ast.NameReference;
import org.eclipse.jdt.internal.compiler.ast.OperatorIds;
import org.eclipse.jdt.internal.compiler.ast.QualifiedThisReference;
import org.eclipse.jdt.internal.compiler.ast.Reference;
import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
import org.eclipse.jdt.internal.compiler.flow.FlowContext;
import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
import org.eclipse.jdt.internal.compiler.lookup.InvocationSite;
import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodScope;
import org.eclipse.jdt.internal.compiler.lookup.MissingTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.ProblemFieldBinding;
import org.eclipse.jdt.internal.compiler.lookup.ProblemReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.VariableBinding;
import org.eclipse.jdt.internal.compiler.problem.AbortMethod;

public class SingleNameReference
extends NameReference
implements OperatorIds {
    public static final int READ = 0;
    public static final int WRITE = 1;
    public char[] token;
    public MethodBinding[] syntheticAccessors;
    public TypeBinding genericCast;
    public boolean isLabel;

    public SingleNameReference(char[] source, long pos) {
        this.token = source;
        this.sourceStart = (int)(pos >>> 32);
        this.sourceEnd = (int)pos;
    }

    @Override
    public FlowInfo analyseAssignment(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo, Assignment assignment, boolean isCompound) {
        LocalVariableBinding localBinding;
        FieldBinding fieldBinding;
        boolean isReachable;
        boolean bl = isReachable = (flowInfo.tagBits & 3) == 0;
        if (isCompound) {
            switch (this.bits & 7) {
                case 1: {
                    FlowInfo fieldInits;
                    fieldBinding = (FieldBinding)this.binding;
                    if (fieldBinding.isBlankFinal() && currentScope.needBlankFinalFieldInitializationCheck(fieldBinding) && !(fieldInits = flowContext.getInitsForFinalBlankInitializationCheck(fieldBinding.declaringClass.original(), flowInfo)).isDefinitelyAssigned(fieldBinding)) {
                        currentScope.problemReporter().uninitializedBlankFinalField(fieldBinding, this);
                    }
                    this.manageSyntheticAccessIfNecessary(currentScope, flowInfo, true);
                    break;
                }
                case 2: {
                    localBinding = (LocalVariableBinding)this.binding;
                    if (!flowInfo.isDefinitelyAssigned(localBinding)) {
                        currentScope.problemReporter().uninitializedLocalVariable(localBinding, this, currentScope);
                    }
                    if (localBinding.useFlag == 1) break;
                    if (isReachable && (this.implicitConversion & 0x400) != 0) {
                        localBinding.useFlag = 1;
                        break;
                    }
                    if (localBinding.useFlag > 0) break;
                    --localBinding.useFlag;
                }
            }
        }
        if (assignment.expression != null) {
            flowInfo = assignment.expression.analyseCode(currentScope, flowContext, flowInfo).unconditionalInits();
        }
        switch (this.bits & 7) {
            case 1: {
                this.manageSyntheticAccessIfNecessary(currentScope, flowInfo, false);
                fieldBinding = (FieldBinding)this.binding;
                if (fieldBinding.isFinal()) {
                    if (!isCompound && fieldBinding.isBlankFinal() && currentScope.allowBlankFinalFieldAssignment(fieldBinding)) {
                        if (flowInfo.isPotentiallyAssigned(fieldBinding)) {
                            currentScope.problemReporter().duplicateInitializationOfBlankFinalField(fieldBinding, this);
                        } else {
                            flowContext.recordSettingFinal(fieldBinding, this, flowInfo);
                        }
                        flowInfo.markAsDefinitelyAssigned(fieldBinding);
                        break;
                    }
                    currentScope.problemReporter().cannotAssignToFinalField(fieldBinding, this);
                    break;
                }
                if (isCompound || !fieldBinding.isNonNull() && !fieldBinding.type.isTypeVariable() || !TypeBinding.equalsEquals(fieldBinding.declaringClass, currentScope.enclosingReceiverType())) break;
                flowInfo.markAsDefinitelyAssigned(fieldBinding);
                break;
            }
            case 2: {
                localBinding = (LocalVariableBinding)this.binding;
                boolean isFinal = localBinding.isFinal();
                this.bits = !flowInfo.isDefinitelyAssigned(localBinding) ? (this.bits |= 8) : (this.bits &= 0xFFFFFFF7);
                if (flowInfo.isPotentiallyAssigned(localBinding) || (this.bits & 0x80000) != 0) {
                    localBinding.tagBits &= 0xFFFFFFFFFFFFF7FFL;
                    if (!isFinal && (this.bits & 0x80000) != 0) {
                        currentScope.problemReporter().cannotReferToNonEffectivelyFinalOuterLocal(localBinding, this);
                    }
                }
                if (!isFinal && (localBinding.tagBits & 0x800L) != 0L && (localBinding.tagBits & 0x400L) == 0L) {
                    flowContext.recordSettingFinal(localBinding, this, flowInfo);
                } else if (isFinal) {
                    if ((this.bits & 0x1FE0) == 0) {
                        if (isReachable && isCompound || !localBinding.isBlankFinal()) {
                            currentScope.problemReporter().cannotAssignToFinalLocal(localBinding, this);
                        } else if (flowInfo.isPotentiallyAssigned(localBinding)) {
                            currentScope.problemReporter().duplicateInitializationOfFinalLocal(localBinding, this);
                        } else if ((this.bits & 0x80000) != 0) {
                            currentScope.problemReporter().cannotAssignToFinalOuterLocal(localBinding, this);
                        } else {
                            flowContext.recordSettingFinal(localBinding, this, flowInfo);
                        }
                    } else {
                        currentScope.problemReporter().cannotAssignToFinalOuterLocal(localBinding, this);
                    }
                } else if ((localBinding.tagBits & 0x400L) != 0L) {
                    currentScope.problemReporter().parameterAssignment(localBinding, this);
                }
                flowInfo.markAsDefinitelyAssigned(localBinding);
            }
        }
        this.manageEnclosingInstanceAccessIfNecessary(currentScope, flowInfo);
        return flowInfo;
    }

    @Override
    public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo) {
        return this.analyseCode(currentScope, flowContext, flowInfo, true);
    }

    @Override
    public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo, boolean valueRequired) {
        switch (this.bits & 7) {
            case 1: {
                FlowInfo fieldInits;
                FieldBinding fieldBinding;
                if (valueRequired || currentScope.compilerOptions().complianceLevel >= 0x300000L) {
                    this.manageSyntheticAccessIfNecessary(currentScope, flowInfo, true);
                }
                if (!(fieldBinding = (FieldBinding)this.binding).isBlankFinal() || !currentScope.needBlankFinalFieldInitializationCheck(fieldBinding) || (fieldInits = flowContext.getInitsForFinalBlankInitializationCheck(fieldBinding.declaringClass.original(), flowInfo)).isDefinitelyAssigned(fieldBinding)) break;
                currentScope.problemReporter().uninitializedBlankFinalField(fieldBinding, this);
                break;
            }
            case 2: {
                LocalVariableBinding localBinding = (LocalVariableBinding)this.binding;
                if (!flowInfo.isDefinitelyAssigned(localBinding)) {
                    currentScope.problemReporter().uninitializedLocalVariable(localBinding, this, currentScope);
                }
                if ((flowInfo.tagBits & 3) == 0) {
                    localBinding.useFlag = 1;
                    break;
                }
                if (localBinding.useFlag != 0) break;
                localBinding.useFlag = 2;
            }
        }
        if (valueRequired) {
            this.manageEnclosingInstanceAccessIfNecessary(currentScope, flowInfo);
        }
        return flowInfo;
    }

    public TypeBinding checkFieldAccess(BlockScope scope) {
        FieldBinding fieldBinding = (FieldBinding)this.binding;
        this.constant = fieldBinding.constant(scope);
        this.bits &= 0xFFFFFFF8;
        this.bits |= 1;
        MethodScope methodScope = scope.methodScope();
        if (fieldBinding.isStatic()) {
            ReferenceBinding declaringClass = fieldBinding.declaringClass;
            if (declaringClass.isEnum() && scope.kind != 5) {
                SourceTypeBinding sourceType = scope.enclosingSourceType();
                if (this.constant == Constant.NotAConstant && !methodScope.isStatic && (TypeBinding.equalsEquals(sourceType, declaringClass) || TypeBinding.equalsEquals(sourceType.superclass, declaringClass)) && methodScope.isInsideInitializerOrConstructor()) {
                    scope.problemReporter().enumStaticFieldUsedDuringInitialization(fieldBinding, this);
                }
            }
        } else {
            if (scope.compilerOptions().getSeverity(0x400000) != 256) {
                scope.problemReporter().unqualifiedFieldAccess(this, fieldBinding);
            }
            if (methodScope.isStatic) {
                scope.problemReporter().staticFieldAccessToNonStaticVariable(this, fieldBinding);
                return fieldBinding.type;
            }
            scope.tagAsAccessingEnclosingInstanceStateOf(fieldBinding.declaringClass, false);
        }
        if (this.isFieldUseDeprecated(fieldBinding, scope, this.bits)) {
            scope.problemReporter().deprecatedField(fieldBinding, this);
        }
        if ((this.bits & 0x2000) == 0 && TypeBinding.equalsEquals(methodScope.enclosingSourceType(), fieldBinding.original().declaringClass) && methodScope.lastVisibleFieldID >= 0 && fieldBinding.id >= methodScope.lastVisibleFieldID && (!fieldBinding.isStatic() || methodScope.isStatic)) {
            scope.problemReporter().forwardReference(this, 0, fieldBinding);
            this.bits |= 0x20000000;
        }
        return fieldBinding.type;
    }

    @Override
    public boolean checkNPE(BlockScope scope, FlowContext flowContext, FlowInfo flowInfo, int ttlForFieldCheck) {
        if (!super.checkNPE(scope, flowContext, flowInfo, ttlForFieldCheck)) {
            CompilerOptions compilerOptions = scope.compilerOptions();
            if (compilerOptions.isAnnotationBasedNullAnalysisEnabled && this.binding instanceof FieldBinding) {
                return this.checkNullableFieldDereference(scope, (FieldBinding)this.binding, ((long)this.sourceStart << 32) + (long)this.sourceEnd, flowContext, ttlForFieldCheck);
            }
        }
        return false;
    }

    @Override
    public void computeConversion(Scope scope, TypeBinding runtimeTimeType, TypeBinding compileTimeType) {
        if (runtimeTimeType == null || compileTimeType == null) {
            return;
        }
        if (this.binding != null && this.binding.isValidBinding()) {
            TypeBinding originalType = null;
            if ((this.bits & 1) != 0) {
                FieldBinding field = (FieldBinding)this.binding;
                FieldBinding originalBinding = field.original();
                originalType = originalBinding.type;
            } else if ((this.bits & 2) != 0) {
                LocalVariableBinding local = (LocalVariableBinding)this.binding;
                originalType = local.type;
            }
            if (originalType != null && originalType.leafComponentType().isTypeVariable()) {
                ReferenceBinding referenceCast;
                TypeBinding targetType = !compileTimeType.isBaseType() && runtimeTimeType.isBaseType() ? compileTimeType : runtimeTimeType;
                this.genericCast = originalType.genericCast(scope.boxing(targetType));
                if (this.genericCast instanceof ReferenceBinding && !(referenceCast = (ReferenceBinding)this.genericCast).canBeSeenBy(scope)) {
                    scope.problemReporter().invalidType(this, new ProblemReferenceBinding(CharOperation.splitOn('.', referenceCast.shortReadableName()), referenceCast, 2));
                }
            }
        }
        super.computeConversion(scope, runtimeTimeType, compileTimeType);
    }

    @Override
    public void generateAssignment(BlockScope currentScope, CodeStream codeStream, Assignment assignment, boolean valueRequired) {
        if (assignment.expression.isCompactableOperation()) {
            SingleNameReference variableReference;
            BinaryExpression operation = (BinaryExpression)assignment.expression;
            int operator = (operation.bits & 0x3F00) >> 8;
            if (operation.left instanceof SingleNameReference) {
                variableReference = (SingleNameReference)operation.left;
                if (variableReference.binding == this.binding) {
                    variableReference.generateCompoundAssignment(currentScope, codeStream, this.syntheticAccessors == null ? null : this.syntheticAccessors[1], operation.right, operator, operation.implicitConversion, valueRequired);
                    if (valueRequired) {
                        codeStream.generateImplicitConversion(assignment.implicitConversion);
                    }
                    return;
                }
            }
            if (operation.right instanceof SingleNameReference && (operator == 14 || operator == 15)) {
                variableReference = (SingleNameReference)operation.right;
                if (variableReference.binding == this.binding && operation.left.constant != Constant.NotAConstant && (operation.left.implicitConversion & 0xFF) >> 4 != 11 && (operation.right.implicitConversion & 0xFF) >> 4 != 11) {
                    variableReference.generateCompoundAssignment(currentScope, codeStream, this.syntheticAccessors == null ? null : this.syntheticAccessors[1], operation.left, operator, operation.implicitConversion, valueRequired);
                    if (valueRequired) {
                        codeStream.generateImplicitConversion(assignment.implicitConversion);
                    }
                    return;
                }
            }
        }
        switch (this.bits & 7) {
            case 1: {
                int pc = codeStream.position;
                FieldBinding codegenBinding = ((FieldBinding)this.binding).original();
                if (!codegenBinding.isStatic()) {
                    if ((this.bits & 0x1FE0) != 0) {
                        ReferenceBinding targetType = currentScope.enclosingSourceType().enclosingTypeAt((this.bits & 0x1FE0) >> 5);
                        Object[] emulationPath = currentScope.getEmulationPath(targetType, true, false);
                        codeStream.generateOuterAccess(emulationPath, this, targetType, currentScope);
                    } else {
                        this.generateReceiver(codeStream);
                    }
                }
                codeStream.recordPositionsFrom(pc, this.sourceStart);
                assignment.expression.generateCode(currentScope, codeStream, true);
                this.fieldStore(currentScope, codeStream, codegenBinding, this.syntheticAccessors == null ? null : this.syntheticAccessors[1], this.actualReceiverType, true, valueRequired);
                if (valueRequired) {
                    codeStream.generateImplicitConversion(assignment.implicitConversion);
                }
                return;
            }
            case 2: {
                LocalVariableBinding localBinding = (LocalVariableBinding)this.binding;
                if (localBinding.resolvedPosition == -1) {
                    if (assignment.expression.constant != Constant.NotAConstant) {
                        if (valueRequired) {
                            codeStream.generateConstant(assignment.expression.constant, assignment.implicitConversion);
                        }
                    } else {
                        assignment.expression.generateCode(currentScope, codeStream, true);
                        if (valueRequired) {
                            codeStream.generateImplicitConversion(assignment.implicitConversion);
                        } else {
                            switch (localBinding.type.id) {
                                case 7: 
                                case 8: {
                                    codeStream.pop2();
                                    break;
                                }
                                default: {
                                    codeStream.pop();
                                }
                            }
                        }
                    }
                    return;
                }
                assignment.expression.generateCode(currentScope, codeStream, true);
                if (localBinding.type.isArrayType() && assignment.expression instanceof CastExpression && ((CastExpression)assignment.expression).innermostCastedExpression().resolvedType == TypeBinding.NULL) {
                    codeStream.checkcast(localBinding.type);
                }
                codeStream.store(localBinding, valueRequired);
                if ((this.bits & 0x10) != 0) {
                    localBinding.recordInitializationStartPC(codeStream.position);
                }
                if ((this.bits & 8) != 0) {
                    localBinding.recordInitializationStartPC(codeStream.position);
                }
                if (!valueRequired) break;
                codeStream.generateImplicitConversion(assignment.implicitConversion);
            }
        }
    }

    @Override
    public void generateCode(BlockScope currentScope, CodeStream codeStream, boolean valueRequired) {
        int pc = codeStream.position;
        if (this.constant != Constant.NotAConstant) {
            if (valueRequired) {
                codeStream.generateConstant(this.constant, this.implicitConversion);
            }
            codeStream.recordPositionsFrom(pc, this.sourceStart);
            return;
        }
        switch (this.bits & 7) {
            case 1: {
                FieldBinding codegenField = ((FieldBinding)this.binding).original();
                Constant fieldConstant = codegenField.constant();
                if (fieldConstant != Constant.NotAConstant) {
                    if (valueRequired) {
                        codeStream.generateConstant(fieldConstant, this.implicitConversion);
                    }
                    codeStream.recordPositionsFrom(pc, this.sourceStart);
                    return;
                }
                if (codegenField.isStatic()) {
                    if (!valueRequired && TypeBinding.equalsEquals(((FieldBinding)this.binding).original().declaringClass, this.actualReceiverType.erasure()) && (this.implicitConversion & 0x400) == 0 && this.genericCast == null) {
                        codeStream.recordPositionsFrom(pc, this.sourceStart);
                        return;
                    }
                    if (this.syntheticAccessors == null || this.syntheticAccessors[0] == null) {
                        TypeBinding constantPoolDeclaringClass = CodeStream.getConstantPoolDeclaringClass((Scope)currentScope, codegenField, this.actualReceiverType, true);
                        codeStream.fieldAccess((byte)-78, codegenField, constantPoolDeclaringClass);
                        break;
                    }
                    codeStream.invoke((byte)-72, this.syntheticAccessors[0], null);
                    break;
                }
                if (!valueRequired && (this.implicitConversion & 0x400) == 0 && this.genericCast == null) {
                    codeStream.recordPositionsFrom(pc, this.sourceStart);
                    return;
                }
                if ((this.bits & 0x1FE0) != 0) {
                    ReferenceBinding targetType = currentScope.enclosingSourceType().enclosingTypeAt((this.bits & 0x1FE0) >> 5);
                    Object[] emulationPath = currentScope.getEmulationPath(targetType, true, false);
                    codeStream.generateOuterAccess(emulationPath, this, targetType, currentScope);
                } else {
                    this.generateReceiver(codeStream);
                }
                if (this.syntheticAccessors == null || this.syntheticAccessors[0] == null) {
                    TypeBinding constantPoolDeclaringClass = CodeStream.getConstantPoolDeclaringClass((Scope)currentScope, codegenField, this.actualReceiverType, true);
                    codeStream.fieldAccess((byte)-76, codegenField, constantPoolDeclaringClass);
                    break;
                }
                codeStream.invoke((byte)-72, this.syntheticAccessors[0], null);
                break;
            }
            case 2: {
                LocalVariableBinding localBinding = (LocalVariableBinding)this.binding;
                if (localBinding.resolvedPosition == -1) {
                    if (valueRequired) {
                        localBinding.useFlag = 1;
                        throw new AbortMethod(CodeStream.RESTART_CODE_GEN_FOR_UNUSED_LOCALS_MODE, null);
                    }
                    codeStream.recordPositionsFrom(pc, this.sourceStart);
                    return;
                }
                if (!valueRequired && (this.implicitConversion & 0x400) == 0) {
                    codeStream.recordPositionsFrom(pc, this.sourceStart);
                    return;
                }
                if ((this.bits & 0x80000) != 0) {
                    this.checkEffectiveFinality(localBinding, currentScope);
                    Object[] path = currentScope.getEmulationPath(localBinding);
                    codeStream.generateOuterAccess(path, this, localBinding, currentScope);
                    break;
                }
                codeStream.load(localBinding);
                break;
            }
            default: {
                codeStream.recordPositionsFrom(pc, this.sourceStart);
                return;
            }
        }
        if (this.genericCast != null) {
            codeStream.checkcast(this.genericCast);
        }
        if (valueRequired) {
            codeStream.generateImplicitConversion(this.implicitConversion);
        } else {
            boolean isUnboxing;
            boolean bl = isUnboxing = (this.implicitConversion & 0x400) != 0;
            if (isUnboxing) {
                codeStream.generateImplicitConversion(this.implicitConversion);
            }
            switch (isUnboxing ? this.postConversionType((Scope)currentScope).id : this.resolvedType.id) {
                case 7: 
                case 8: {
                    codeStream.pop2();
                    break;
                }
                default: {
                    codeStream.pop();
                }
            }
        }
        codeStream.recordPositionsFrom(pc, this.sourceStart);
    }

    @Override
    public void generateCompoundAssignment(BlockScope currentScope, CodeStream codeStream, Expression expression, int operator, int assignmentImplicitConversion, boolean valueRequired) {
        switch (this.bits & 7) {
            case 2: {
                LocalVariableBinding localBinding = (LocalVariableBinding)this.binding;
                Reference.reportOnlyUselesslyReadLocal(currentScope, localBinding, valueRequired);
                break;
            }
            case 1: {
                this.reportOnlyUselesslyReadPrivateField(currentScope, (FieldBinding)this.binding, valueRequired);
            }
        }
        this.generateCompoundAssignment(currentScope, codeStream, this.syntheticAccessors == null ? null : this.syntheticAccessors[1], expression, operator, assignmentImplicitConversion, valueRequired);
    }

    /*
     * Enabled aggressive block sorting
     */
    public void generateCompoundAssignment(BlockScope currentScope, CodeStream codeStream, MethodBinding writeAccessor, Expression expression, int operator, int assignmentImplicitConversion, boolean valueRequired) {
        switch (this.bits & 7) {
            case 1: {
                TypeBinding constantPoolDeclaringClass;
                FieldBinding codegenField = ((FieldBinding)this.binding).original();
                if (codegenField.isStatic()) {
                    if (this.syntheticAccessors == null || this.syntheticAccessors[0] == null) {
                        constantPoolDeclaringClass = CodeStream.getConstantPoolDeclaringClass((Scope)currentScope, codegenField, this.actualReceiverType, true);
                        codeStream.fieldAccess((byte)-78, codegenField, constantPoolDeclaringClass);
                        break;
                    }
                    codeStream.invoke((byte)-72, this.syntheticAccessors[0], null);
                    break;
                }
                if ((this.bits & 0x1FE0) != 0) {
                    ReferenceBinding targetType = currentScope.enclosingSourceType().enclosingTypeAt((this.bits & 0x1FE0) >> 5);
                    Object[] emulationPath = currentScope.getEmulationPath(targetType, true, false);
                    codeStream.generateOuterAccess(emulationPath, this, targetType, currentScope);
                } else {
                    codeStream.aload_0();
                }
                codeStream.dup();
                if (this.syntheticAccessors == null || this.syntheticAccessors[0] == null) {
                    constantPoolDeclaringClass = CodeStream.getConstantPoolDeclaringClass((Scope)currentScope, codegenField, this.actualReceiverType, true);
                    codeStream.fieldAccess((byte)-76, codegenField, constantPoolDeclaringClass);
                    break;
                }
                codeStream.invoke((byte)-72, this.syntheticAccessors[0], null);
                break;
            }
            case 2: {
                Constant assignConstant;
                LocalVariableBinding localBinding = (LocalVariableBinding)this.binding;
                switch (localBinding.type.id) {
                    case 11: {
                        codeStream.generateStringConcatenationAppend(currentScope, this, expression);
                        if (valueRequired) {
                            codeStream.dup();
                        }
                        codeStream.store(localBinding, false);
                        return;
                    }
                    case 10: {
                        assignConstant = expression.constant;
                        if (localBinding.resolvedPosition == -1) {
                            if (valueRequired) {
                                localBinding.useFlag = 1;
                                throw new AbortMethod(CodeStream.RESTART_CODE_GEN_FOR_UNUSED_LOCALS_MODE, null);
                            }
                            if (assignConstant != Constant.NotAConstant) return;
                            expression.generateCode(currentScope, codeStream, false);
                            return;
                        }
                        if (assignConstant == Constant.NotAConstant || assignConstant.typeID() == 9 || assignConstant.typeID() == 8) break;
                        switch (operator) {
                            case 14: {
                                int increment = assignConstant.intValue();
                                if (increment != (short)increment) break;
                                codeStream.iinc(localBinding.resolvedPosition, increment);
                                if (!valueRequired) return;
                                codeStream.load(localBinding);
                                return;
                            }
                            case 13: {
                                int increment = -assignConstant.intValue();
                                if (increment != (short)increment) break;
                                codeStream.iinc(localBinding.resolvedPosition, increment);
                                if (!valueRequired) return;
                                codeStream.load(localBinding);
                                return;
                            }
                        }
                        break;
                    }
                }
                if (localBinding.resolvedPosition == -1) {
                    assignConstant = expression.constant;
                    if (valueRequired) {
                        localBinding.useFlag = 1;
                        throw new AbortMethod(CodeStream.RESTART_CODE_GEN_FOR_UNUSED_LOCALS_MODE, null);
                    }
                    if (assignConstant != Constant.NotAConstant) return;
                    expression.generateCode(currentScope, codeStream, false);
                    return;
                }
                codeStream.load(localBinding);
                break;
            }
        }
        int operationTypeID = (this.implicitConversion & 0xFF) >> 4;
        switch (operationTypeID) {
            case 0: 
            case 1: 
            case 11: {
                codeStream.generateStringConcatenationAppend(currentScope, null, expression);
                break;
            }
            default: {
                if (this.genericCast != null) {
                    codeStream.checkcast(this.genericCast);
                }
                codeStream.generateImplicitConversion(this.implicitConversion);
                if (expression == IntLiteral.One) {
                    codeStream.generateConstant(expression.constant, this.implicitConversion);
                } else {
                    expression.generateCode(currentScope, codeStream, true);
                }
                codeStream.sendOperator(operator, operationTypeID);
                codeStream.generateImplicitConversion(assignmentImplicitConversion);
            }
        }
        switch (this.bits & 7) {
            case 1: {
                FieldBinding codegenField = ((FieldBinding)this.binding).original();
                this.fieldStore(currentScope, codeStream, codegenField, writeAccessor, this.actualReceiverType, true, valueRequired);
                return;
            }
            case 2: {
                LocalVariableBinding localBinding = (LocalVariableBinding)this.binding;
                if (valueRequired) {
                    switch (localBinding.type.id) {
                        case 7: 
                        case 8: {
                            codeStream.dup2();
                            break;
                        }
                        default: {
                            codeStream.dup();
                        }
                    }
                }
                codeStream.store(localBinding, false);
                return;
            }
        }
    }

    @Override
    public void generatePostIncrement(BlockScope currentScope, CodeStream codeStream, CompoundAssignment postIncrement, boolean valueRequired) {
        switch (this.bits & 7) {
            case 1: {
                TypeBinding operandType;
                TypeBinding constantPoolDeclaringClass;
                FieldBinding fieldBinding = (FieldBinding)this.binding;
                this.reportOnlyUselesslyReadPrivateField(currentScope, fieldBinding, valueRequired);
                FieldBinding codegenField = fieldBinding.original();
                if (codegenField.isStatic()) {
                    if (this.syntheticAccessors == null || this.syntheticAccessors[0] == null) {
                        constantPoolDeclaringClass = CodeStream.getConstantPoolDeclaringClass((Scope)currentScope, codegenField, this.actualReceiverType, true);
                        codeStream.fieldAccess((byte)-78, codegenField, constantPoolDeclaringClass);
                    } else {
                        codeStream.invoke((byte)-72, this.syntheticAccessors[0], null);
                    }
                } else {
                    if ((this.bits & 0x1FE0) != 0) {
                        ReferenceBinding targetType = currentScope.enclosingSourceType().enclosingTypeAt((this.bits & 0x1FE0) >> 5);
                        Object[] emulationPath = currentScope.getEmulationPath(targetType, true, false);
                        codeStream.generateOuterAccess(emulationPath, this, targetType, currentScope);
                    } else {
                        codeStream.aload_0();
                    }
                    codeStream.dup();
                    if (this.syntheticAccessors == null || this.syntheticAccessors[0] == null) {
                        constantPoolDeclaringClass = CodeStream.getConstantPoolDeclaringClass((Scope)currentScope, codegenField, this.actualReceiverType, true);
                        codeStream.fieldAccess((byte)-76, codegenField, constantPoolDeclaringClass);
                    } else {
                        codeStream.invoke((byte)-72, this.syntheticAccessors[0], null);
                    }
                }
                if (this.genericCast != null) {
                    codeStream.checkcast(this.genericCast);
                    operandType = this.genericCast;
                } else {
                    operandType = codegenField.type;
                }
                if (valueRequired) {
                    if (codegenField.isStatic()) {
                        switch (operandType.id) {
                            case 7: 
                            case 8: {
                                codeStream.dup2();
                                break;
                            }
                            default: {
                                codeStream.dup();
                                break;
                            }
                        }
                    } else {
                        switch (operandType.id) {
                            case 7: 
                            case 8: {
                                codeStream.dup2_x1();
                                break;
                            }
                            default: {
                                codeStream.dup_x1();
                            }
                        }
                    }
                }
                codeStream.generateImplicitConversion(this.implicitConversion);
                codeStream.generateConstant(postIncrement.expression.constant, this.implicitConversion);
                codeStream.sendOperator(postIncrement.operator, this.implicitConversion & 0xF);
                codeStream.generateImplicitConversion(postIncrement.preAssignImplicitConversion);
                this.fieldStore(currentScope, codeStream, codegenField, this.syntheticAccessors == null ? null : this.syntheticAccessors[1], this.actualReceiverType, true, false);
                return;
            }
            case 2: {
                LocalVariableBinding localBinding = (LocalVariableBinding)this.binding;
                Reference.reportOnlyUselesslyReadLocal(currentScope, localBinding, valueRequired);
                if (localBinding.resolvedPosition == -1) {
                    if (valueRequired) {
                        localBinding.useFlag = 1;
                        throw new AbortMethod(CodeStream.RESTART_CODE_GEN_FOR_UNUSED_LOCALS_MODE, null);
                    }
                    return;
                }
                if (TypeBinding.equalsEquals(localBinding.type, TypeBinding.INT)) {
                    if (valueRequired) {
                        codeStream.load(localBinding);
                    }
                    if (postIncrement.operator == 14) {
                        codeStream.iinc(localBinding.resolvedPosition, 1);
                        break;
                    }
                    codeStream.iinc(localBinding.resolvedPosition, -1);
                    break;
                }
                codeStream.load(localBinding);
                if (valueRequired) {
                    switch (localBinding.type.id) {
                        case 7: 
                        case 8: {
                            codeStream.dup2();
                            break;
                        }
                        default: {
                            codeStream.dup();
                        }
                    }
                }
                codeStream.generateImplicitConversion(this.implicitConversion);
                codeStream.generateConstant(postIncrement.expression.constant, this.implicitConversion);
                codeStream.sendOperator(postIncrement.operator, this.implicitConversion & 0xF);
                codeStream.generateImplicitConversion(postIncrement.preAssignImplicitConversion);
                codeStream.store(localBinding, false);
            }
        }
    }

    public void generateReceiver(CodeStream codeStream) {
        codeStream.aload_0();
    }

    @Override
    public TypeBinding[] genericTypeArguments() {
        return null;
    }

    @Override
    public boolean isEquivalent(Reference reference) {
        char[] otherToken = null;
        if (reference instanceof SingleNameReference) {
            otherToken = ((SingleNameReference)reference).token;
        } else if (reference instanceof FieldReference) {
            FieldReference fr = (FieldReference)reference;
            if (fr.receiver.isThis() && !(fr.receiver instanceof QualifiedThisReference)) {
                otherToken = fr.token;
            }
        }
        return otherToken != null && CharOperation.equals(this.token, otherToken);
    }

    @Override
    public LocalVariableBinding localVariableBinding() {
        switch (this.bits & 7) {
            case 1: {
                break;
            }
            case 2: {
                return (LocalVariableBinding)this.binding;
            }
        }
        return null;
    }

    @Override
    public VariableBinding nullAnnotatedVariableBinding(boolean supportTypeAnnotations) {
        switch (this.bits & 7) {
            case 1: 
            case 2: {
                if (!supportTypeAnnotations && (((VariableBinding)this.binding).tagBits & 0x180000000000000L) == 0L) break;
                return (VariableBinding)this.binding;
            }
        }
        return null;
    }

    @Override
    public int nullStatus(FlowInfo flowInfo, FlowContext flowContext) {
        if ((this.implicitConversion & 0x200) != 0) {
            return 4;
        }
        LocalVariableBinding local = this.localVariableBinding();
        if (local != null) {
            return flowInfo.nullStatus(local);
        }
        return super.nullStatus(flowInfo, flowContext);
    }

    public void manageEnclosingInstanceAccessIfNecessary(BlockScope currentScope, FlowInfo flowInfo) {
        LocalVariableBinding localVariableBinding;
        if ((this.bits & 0x1FE0) == 0 && (this.bits & 0x80000) == 0 || this.constant != Constant.NotAConstant) {
            return;
        }
        if ((this.bits & 7) == 2 && (localVariableBinding = (LocalVariableBinding)this.binding) != null) {
            if (localVariableBinding.isUninitializedIn(currentScope)) {
                return;
            }
            if ((localVariableBinding.tagBits & 0x800L) == 0L) {
                return;
            }
            switch (localVariableBinding.useFlag) {
                case 1: 
                case 2: {
                    currentScope.emulateOuterAccess(localVariableBinding);
                }
            }
        }
    }

    public void manageSyntheticAccessIfNecessary(BlockScope currentScope, FlowInfo flowInfo, boolean isReadAccess) {
        if ((flowInfo.tagBits & 1) != 0) {
            return;
        }
        if (this.constant != Constant.NotAConstant) {
            return;
        }
        if ((this.bits & 1) != 0) {
            FieldBinding fieldBinding = (FieldBinding)this.binding;
            FieldBinding codegenField = fieldBinding.original();
            if ((this.bits & 0x1FE0) != 0 && (codegenField.isPrivate() && !currentScope.enclosingSourceType().isNestmateOf(codegenField.declaringClass) || codegenField.isProtected() && codegenField.declaringClass.getPackage() != currentScope.enclosingSourceType().getPackage())) {
                if (this.syntheticAccessors == null) {
                    this.syntheticAccessors = new MethodBinding[2];
                }
                this.syntheticAccessors[isReadAccess ? 0 : 1] = ((SourceTypeBinding)currentScope.enclosingSourceType().enclosingTypeAt((this.bits & 0x1FE0) >> 5)).addSyntheticMethod(codegenField, isReadAccess, false);
                currentScope.problemReporter().needToEmulateFieldAccess(codegenField, this, isReadAccess);
                return;
            }
        }
    }

    @Override
    public TypeBinding postConversionType(Scope scope) {
        TypeBinding convertedType = this.resolvedType;
        if (this.genericCast != null) {
            convertedType = this.genericCast;
        }
        int runtimeType = (this.implicitConversion & 0xFF) >> 4;
        switch (runtimeType) {
            case 5: {
                convertedType = TypeBinding.BOOLEAN;
                break;
            }
            case 3: {
                convertedType = TypeBinding.BYTE;
                break;
            }
            case 4: {
                convertedType = TypeBinding.SHORT;
                break;
            }
            case 2: {
                convertedType = TypeBinding.CHAR;
                break;
            }
            case 10: {
                convertedType = TypeBinding.INT;
                break;
            }
            case 9: {
                convertedType = TypeBinding.FLOAT;
                break;
            }
            case 7: {
                convertedType = TypeBinding.LONG;
                break;
            }
            case 8: {
                convertedType = TypeBinding.DOUBLE;
            }
        }
        if ((this.implicitConversion & 0x200) != 0) {
            convertedType = scope.environment().computeBoxingType(convertedType);
        }
        return convertedType;
    }

    @Override
    public StringBuffer printExpression(int indent, StringBuffer output) {
        return output.append(this.token);
    }

    public TypeBinding reportError(BlockScope scope) {
        this.constant = Constant.NotAConstant;
        if (this.binding instanceof ProblemFieldBinding) {
            scope.problemReporter().invalidField(this, (FieldBinding)this.binding);
        } else if (this.binding instanceof ProblemReferenceBinding || this.binding instanceof MissingTypeBinding) {
            scope.problemReporter().invalidType(this, (TypeBinding)this.binding);
        } else {
            scope.problemReporter().unresolvableReference(this, this.binding);
        }
        return null;
    }

    @Override
    public TypeBinding resolveType(BlockScope scope) {
        if (this.actualReceiverType != null) {
            this.binding = scope.getField(this.actualReceiverType, this.token, this);
        } else {
            this.actualReceiverType = scope.enclosingSourceType();
            this.binding = scope.getBinding(this.token, this.bits & 7, (InvocationSite)this, true);
        }
        if (this.binding.isValidBinding()) {
            switch (this.bits & 7) {
                case 3: 
                case 7: {
                    if (this.binding instanceof VariableBinding) {
                        TypeBinding variableType;
                        VariableBinding variable = (VariableBinding)this.binding;
                        if (this.binding instanceof LocalVariableBinding) {
                            this.bits &= 0xFFFFFFF8;
                            this.bits |= 2;
                            ((LocalVariableBinding)this.binding).markReferenced();
                            if (!variable.isFinal() && (this.bits & 0x80000) != 0 && scope.compilerOptions().sourceLevel < 0x340000L) {
                                scope.problemReporter().cannotReferToNonFinalOuterLocal((LocalVariableBinding)variable, this);
                            }
                            this.checkLocalStaticClassVariables(scope, variable);
                            variableType = variable.type;
                            this.constant = (this.bits & 0x2000) == 0 ? variable.constant(scope) : Constant.NotAConstant;
                        } else {
                            variableType = this.checkFieldAccess(scope);
                        }
                        if (variableType != null) {
                            variableType = (this.bits & 0x2000) == 0 ? variableType.capture(scope, this.sourceStart, this.sourceEnd) : variableType;
                            this.resolvedType = variableType;
                            if ((variableType.tagBits & 0x80L) != 0L) {
                                if ((this.bits & 2) == 0) {
                                    scope.problemReporter().invalidType(this, variableType);
                                }
                                return null;
                            }
                        }
                        return variableType;
                    }
                    this.bits &= 0xFFFFFFF8;
                    this.bits |= 4;
                }
                case 4: {
                    this.constant = Constant.NotAConstant;
                    TypeBinding type = (TypeBinding)this.binding;
                    if (this.isTypeUseDeprecated(type, scope)) {
                        scope.problemReporter().deprecatedType(type, this);
                    }
                    this.resolvedType = type = scope.environment().convertToRawType(type, false);
                    return this.resolvedType;
                }
            }
        }
        this.resolvedType = this.reportError(scope);
        return this.resolvedType;
    }

    private void checkLocalStaticClassVariables(BlockScope scope, VariableBinding variable) {
        if (this.actualReceiverType.isStatic() && this.actualReceiverType.isLocalType() && (variable.modifiers & 8) == 0 && (this.bits & 0x80000) != 0) {
            ClassScope currentClassScope;
            BlockScope declaringScope = ((LocalVariableBinding)this.binding).declaringScope;
            MethodScope declaringMethodScope = declaringScope instanceof MethodScope ? (MethodScope)declaringScope : declaringScope.enclosingMethodScope();
            MethodScope currentMethodScope = scope instanceof MethodScope ? (MethodScope)scope : scope.enclosingMethodScope();
            ClassScope declaringClassScope = declaringMethodScope != null ? declaringMethodScope.classScope() : null;
            ClassScope classScope = currentClassScope = currentMethodScope != null ? currentMethodScope.classScope() : null;
            if (declaringClassScope != currentClassScope) {
                scope.problemReporter().recordStaticReferenceToOuterLocalVariable((LocalVariableBinding)variable, this);
            }
        }
    }

    @Override
    public void traverse(ASTVisitor visitor, BlockScope scope) {
        visitor.visit(this, scope);
        visitor.endVisit(this, scope);
    }

    @Override
    public void traverse(ASTVisitor visitor, ClassScope scope) {
        visitor.visit(this, scope);
        visitor.endVisit(this, scope);
    }

    @Override
    public String unboundReferenceErrorName() {
        return new String(this.token);
    }

    @Override
    public char[][] getName() {
        return new char[][]{this.token};
    }
}

