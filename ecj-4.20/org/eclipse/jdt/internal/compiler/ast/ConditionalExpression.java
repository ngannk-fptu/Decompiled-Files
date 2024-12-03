/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.ast.CastExpression;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.ExpressionContext;
import org.eclipse.jdt.internal.compiler.ast.IPolyExpression;
import org.eclipse.jdt.internal.compiler.ast.LocalDeclaration;
import org.eclipse.jdt.internal.compiler.ast.NullAnnotationMatching;
import org.eclipse.jdt.internal.compiler.ast.OperatorExpression;
import org.eclipse.jdt.internal.compiler.codegen.BranchLabel;
import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
import org.eclipse.jdt.internal.compiler.flow.FlowContext;
import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
import org.eclipse.jdt.internal.compiler.flow.UnconditionalFlowInfo;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.lookup.BaseTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.PolyTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;

public class ConditionalExpression
extends OperatorExpression
implements IPolyExpression {
    public Expression condition;
    public Expression valueIfTrue;
    public Expression valueIfFalse;
    public Constant optimizedBooleanConstant;
    public Constant optimizedIfTrueConstant;
    public Constant optimizedIfFalseConstant;
    int trueInitStateIndex = -1;
    int falseInitStateIndex = -1;
    int mergedInitStateIndex = -1;
    private int nullStatus = 1;
    int ifFalseNullStatus;
    int ifTrueNullStatus;
    private TypeBinding expectedType;
    private ExpressionContext expressionContext = ExpressionContext.VANILLA_CONTEXT;
    private boolean isPolyExpression = false;
    private TypeBinding originalValueIfTrueType;
    private TypeBinding originalValueIfFalseType;
    private boolean use18specifics;

    public ConditionalExpression(Expression condition, Expression valueIfTrue, Expression valueIfFalse) {
        this.condition = condition;
        this.valueIfTrue = valueIfTrue;
        this.valueIfFalse = valueIfFalse;
        this.sourceStart = condition.sourceStart;
        this.sourceEnd = valueIfFalse.sourceEnd;
    }

    @Override
    public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo) {
        FlowInfo mergedInfo;
        int initialComplaintLevel = (flowInfo.reachMode() & 3) != 0 ? 1 : 0;
        Constant cst = this.condition.optimizedBooleanConstant();
        boolean isConditionOptimizedTrue = cst != Constant.NotAConstant && cst.booleanValue();
        boolean isConditionOptimizedFalse = cst != Constant.NotAConstant && !cst.booleanValue();
        int mode = flowInfo.reachMode();
        flowInfo = this.condition.analyseCode(currentScope, flowContext, flowInfo, cst == Constant.NotAConstant);
        ++flowContext.conditionalLevel;
        FlowInfo trueFlowInfo = flowInfo.initsWhenTrue().copy();
        CompilerOptions compilerOptions = currentScope.compilerOptions();
        if (isConditionOptimizedFalse) {
            if ((mode & 3) == 0) {
                trueFlowInfo.setReachMode(1);
            }
            if (!ConditionalExpression.isKnowDeadCodePattern(this.condition) || compilerOptions.reportDeadCodeInTrivialIfStatement) {
                this.valueIfTrue.complainIfUnreachable(trueFlowInfo, currentScope, initialComplaintLevel, false);
            }
        }
        this.trueInitStateIndex = currentScope.methodScope().recordInitializationStates(trueFlowInfo);
        this.condition.updateFlowOnBooleanResult(trueFlowInfo, true);
        trueFlowInfo = this.valueIfTrue.analyseCode(currentScope, flowContext, trueFlowInfo);
        this.valueIfTrue.checkNPEbyUnboxing(currentScope, flowContext, trueFlowInfo);
        this.ifTrueNullStatus = -1;
        if (compilerOptions.enableSyntacticNullAnalysisForFields) {
            this.ifTrueNullStatus = this.valueIfTrue.nullStatus(trueFlowInfo, flowContext);
            flowContext.expireNullCheckedFieldInfo();
        }
        FlowInfo falseFlowInfo = flowInfo.initsWhenFalse().copy();
        if (isConditionOptimizedTrue) {
            if ((mode & 3) == 0) {
                falseFlowInfo.setReachMode(1);
            }
            if (!ConditionalExpression.isKnowDeadCodePattern(this.condition) || compilerOptions.reportDeadCodeInTrivialIfStatement) {
                this.valueIfFalse.complainIfUnreachable(falseFlowInfo, currentScope, initialComplaintLevel, true);
            }
        }
        this.falseInitStateIndex = currentScope.methodScope().recordInitializationStates(falseFlowInfo);
        this.condition.updateFlowOnBooleanResult(falseFlowInfo, false);
        falseFlowInfo = this.valueIfFalse.analyseCode(currentScope, flowContext, falseFlowInfo);
        this.valueIfFalse.checkNPEbyUnboxing(currentScope, flowContext, falseFlowInfo);
        --flowContext.conditionalLevel;
        if (isConditionOptimizedTrue) {
            mergedInfo = trueFlowInfo.addPotentialInitializationsFrom(falseFlowInfo);
            this.nullStatus = this.ifTrueNullStatus != -1 ? this.ifTrueNullStatus : this.valueIfTrue.nullStatus(trueFlowInfo, flowContext);
        } else if (isConditionOptimizedFalse) {
            mergedInfo = falseFlowInfo.addPotentialInitializationsFrom(trueFlowInfo);
            this.nullStatus = this.valueIfFalse.nullStatus(falseFlowInfo, flowContext);
        } else {
            this.computeNullStatus(trueFlowInfo, falseFlowInfo, flowContext);
            cst = this.optimizedIfTrueConstant;
            boolean isValueIfTrueOptimizedTrue = cst != null && cst != Constant.NotAConstant && cst.booleanValue();
            boolean isValueIfTrueOptimizedFalse = cst != null && cst != Constant.NotAConstant && !cst.booleanValue();
            cst = this.optimizedIfFalseConstant;
            boolean isValueIfFalseOptimizedTrue = cst != null && cst != Constant.NotAConstant && cst.booleanValue();
            boolean isValueIfFalseOptimizedFalse = cst != null && cst != Constant.NotAConstant && !cst.booleanValue();
            UnconditionalFlowInfo trueFlowTowardsTrue = trueFlowInfo.initsWhenTrue().unconditionalCopy();
            UnconditionalFlowInfo falseFlowTowardsTrue = falseFlowInfo.initsWhenTrue().unconditionalCopy();
            UnconditionalFlowInfo trueFlowTowardsFalse = trueFlowInfo.initsWhenFalse().unconditionalInits();
            UnconditionalFlowInfo falseFlowTowardsFalse = falseFlowInfo.initsWhenFalse().unconditionalInits();
            if (isValueIfTrueOptimizedFalse) {
                trueFlowTowardsTrue.setReachMode(1);
            }
            if (isValueIfFalseOptimizedFalse) {
                falseFlowTowardsTrue.setReachMode(1);
            }
            if (isValueIfTrueOptimizedTrue) {
                trueFlowTowardsFalse.setReachMode(1);
            }
            if (isValueIfFalseOptimizedTrue) {
                falseFlowTowardsFalse.setReachMode(1);
            }
            mergedInfo = FlowInfo.conditional(trueFlowTowardsTrue.mergedWith(falseFlowTowardsTrue), trueFlowTowardsFalse.mergedWith(falseFlowTowardsFalse));
        }
        this.mergedInitStateIndex = currentScope.methodScope().recordInitializationStates(mergedInfo);
        mergedInfo.setReachMode(mode);
        return mergedInfo;
    }

    @Override
    public boolean checkNPE(BlockScope scope, FlowContext flowContext, FlowInfo flowInfo, int ttlForFieldCheck) {
        if ((this.nullStatus & 2) != 0) {
            scope.problemReporter().expressionNullReference(this);
        } else if ((this.nullStatus & 0x10) != 0) {
            scope.problemReporter().expressionPotentialNullReference(this);
        }
        return true;
    }

    private void computeNullStatus(FlowInfo trueBranchInfo, FlowInfo falseBranchInfo, FlowContext flowContext) {
        if (this.ifTrueNullStatus == -1) {
            this.ifTrueNullStatus = this.valueIfTrue.nullStatus(trueBranchInfo, flowContext);
        }
        this.ifFalseNullStatus = this.valueIfFalse.nullStatus(falseBranchInfo, flowContext);
        if (this.ifTrueNullStatus == this.ifFalseNullStatus) {
            this.nullStatus = this.ifTrueNullStatus;
            return;
        }
        if (trueBranchInfo.reachMode() != 0) {
            this.nullStatus = this.ifFalseNullStatus;
            return;
        }
        if (falseBranchInfo.reachMode() != 0) {
            this.nullStatus = this.ifTrueNullStatus;
            return;
        }
        int combinedStatus = this.ifTrueNullStatus | this.ifFalseNullStatus;
        int status = Expression.computeNullStatus(0, combinedStatus);
        if (status > 0) {
            this.nullStatus = status;
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
        Constant cst = this.condition.optimizedBooleanConstant();
        if (cst == Constant.NotAConstant) {
            cst = this.condition.optimizedNullComparisonConstant();
        }
        boolean needTruePart = cst == Constant.NotAConstant || cst.booleanValue();
        boolean needFalsePart = cst == Constant.NotAConstant || !cst.booleanValue();
        BranchLabel endifLabel = new BranchLabel(codeStream);
        BranchLabel falseLabel = new BranchLabel(codeStream);
        falseLabel.tagBits |= 2;
        this.condition.generateOptimizedBoolean(currentScope, codeStream, null, falseLabel, cst == Constant.NotAConstant);
        if (this.trueInitStateIndex != -1) {
            codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.trueInitStateIndex);
            codeStream.addDefinitelyAssignedVariables(currentScope, this.trueInitStateIndex);
        }
        if (needTruePart) {
            this.valueIfTrue.generateCode(currentScope, codeStream, valueRequired);
            if (needFalsePart) {
                int position = codeStream.position;
                codeStream.goto_(endifLabel);
                codeStream.recordPositionsFrom(position, this.valueIfTrue.sourceEnd);
                if (valueRequired) {
                    switch (this.resolvedType.id) {
                        case 7: 
                        case 8: {
                            codeStream.decrStackSize(2);
                            break;
                        }
                        default: {
                            codeStream.decrStackSize(1);
                        }
                    }
                }
            }
        }
        if (needFalsePart) {
            if (this.falseInitStateIndex != -1) {
                codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.falseInitStateIndex);
                codeStream.addDefinitelyAssignedVariables(currentScope, this.falseInitStateIndex);
            }
            if (falseLabel.forwardReferenceCount() > 0) {
                falseLabel.place();
            }
            this.valueIfFalse.generateCode(currentScope, codeStream, valueRequired);
            if (valueRequired) {
                codeStream.recordExpressionType(this.resolvedType);
            }
            if (needTruePart) {
                endifLabel.place();
            }
        }
        if (this.mergedInitStateIndex != -1) {
            codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.mergedInitStateIndex);
        }
        if (valueRequired) {
            codeStream.generateImplicitConversion(this.implicitConversion);
        }
        codeStream.recordPositionsFrom(pc, this.sourceStart);
    }

    @Override
    public void generateOptimizedBoolean(BlockScope currentScope, CodeStream codeStream, BranchLabel trueLabel, BranchLabel falseLabel, boolean valueRequired) {
        BranchLabel internalFalseLabel;
        BranchLabel endifLabel;
        boolean needFalsePart;
        int pc;
        block8: {
            block10: {
                boolean isValueIfTrueOptimizedFalse;
                Constant cst;
                block9: {
                    boolean isValueIfTrueOptimizedTrue;
                    pc = codeStream.position;
                    if (this.constant != Constant.NotAConstant && this.constant.typeID() == 5 || (this.valueIfTrue.implicitConversion & 0xFF) >> 4 != 5 || (this.valueIfFalse.implicitConversion & 0xFF) >> 4 != 5) {
                        super.generateOptimizedBoolean(currentScope, codeStream, trueLabel, falseLabel, valueRequired);
                        return;
                    }
                    cst = this.condition.constant;
                    Constant condCst = this.condition.optimizedBooleanConstant();
                    boolean needTruePart = !(cst != Constant.NotAConstant && !cst.booleanValue() || condCst != Constant.NotAConstant && !condCst.booleanValue());
                    needFalsePart = !(cst != Constant.NotAConstant && cst.booleanValue() || condCst != Constant.NotAConstant && condCst.booleanValue());
                    endifLabel = new BranchLabel(codeStream);
                    boolean needConditionValue = cst == Constant.NotAConstant && condCst == Constant.NotAConstant;
                    internalFalseLabel = new BranchLabel(codeStream);
                    this.condition.generateOptimizedBoolean(currentScope, codeStream, null, internalFalseLabel, needConditionValue);
                    if (this.trueInitStateIndex != -1) {
                        codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.trueInitStateIndex);
                        codeStream.addDefinitelyAssignedVariables(currentScope, this.trueInitStateIndex);
                    }
                    if (!needTruePart) break block8;
                    this.valueIfTrue.generateOptimizedBoolean(currentScope, codeStream, trueLabel, falseLabel, valueRequired);
                    if (!needFalsePart) break block8;
                    if (falseLabel != null) break block9;
                    if (trueLabel == null) break block10;
                    cst = this.optimizedIfTrueConstant;
                    boolean bl = isValueIfTrueOptimizedTrue = cst != null && cst != Constant.NotAConstant && cst.booleanValue();
                    if (!isValueIfTrueOptimizedTrue) break block10;
                    break block8;
                }
                if (trueLabel != null) break block10;
                cst = this.optimizedIfTrueConstant;
                boolean bl = isValueIfTrueOptimizedFalse = cst != null && cst != Constant.NotAConstant && !cst.booleanValue();
                if (isValueIfTrueOptimizedFalse) break block8;
            }
            int position = codeStream.position;
            codeStream.goto_(endifLabel);
            codeStream.recordPositionsFrom(position, this.valueIfTrue.sourceEnd);
        }
        if (needFalsePart) {
            internalFalseLabel.place();
            if (this.falseInitStateIndex != -1) {
                codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.falseInitStateIndex);
                codeStream.addDefinitelyAssignedVariables(currentScope, this.falseInitStateIndex);
            }
            this.valueIfFalse.generateOptimizedBoolean(currentScope, codeStream, trueLabel, falseLabel, valueRequired);
            endifLabel.place();
        }
        if (this.mergedInitStateIndex != -1) {
            codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.mergedInitStateIndex);
        }
        codeStream.recordPositionsFrom(pc, this.sourceEnd);
    }

    @Override
    public int nullStatus(FlowInfo flowInfo, FlowContext flowContext) {
        if ((this.implicitConversion & 0x200) != 0) {
            return 4;
        }
        return this.nullStatus;
    }

    @Override
    public Constant optimizedBooleanConstant() {
        return this.optimizedBooleanConstant == null ? this.constant : this.optimizedBooleanConstant;
    }

    @Override
    public StringBuffer printExpressionNoParenthesis(int indent, StringBuffer output) {
        this.condition.printExpression(indent, output).append(" ? ");
        this.valueIfTrue.printExpression(0, output).append(" : ");
        return this.valueIfFalse.printExpression(0, output);
    }

    @Override
    public void addPatternVariables(BlockScope scope, CodeStream codeStream) {
        this.condition.addPatternVariables(scope, codeStream);
        this.valueIfTrue.addPatternVariables(scope, codeStream);
        this.valueIfFalse.addPatternVariables(scope, codeStream);
    }

    @Override
    public void collectPatternVariablesToScope(LocalVariableBinding[] variables, BlockScope scope) {
        this.valueIfFalse.collectPatternVariablesToScope(null, scope);
        this.valueIfTrue.collectPatternVariablesToScope(null, scope);
        if (this.valueIfFalse.containsPatternVariable() && this.valueIfTrue.containsPatternVariable()) {
            LocalVariableBinding localVariableBinding2;
            int n;
            int n2;
            LocalVariableBinding[] localVariableBindingArray;
            char[] name;
            LocalVariableBinding localVariableBinding;
            int n3;
            int n4;
            LocalVariableBinding[] localVariableBindingArray2;
            LocalVariableBinding[] first = this.valueIfTrue.patternVarsWhenTrue;
            LocalVariableBinding[] second = this.valueIfFalse.patternVarsWhenTrue;
            if (first != null && second != null) {
                localVariableBindingArray2 = first;
                n4 = first.length;
                n3 = 0;
                while (n3 < n4) {
                    localVariableBinding = localVariableBindingArray2[n3];
                    name = localVariableBinding.name;
                    localVariableBindingArray = second;
                    n2 = second.length;
                    n = 0;
                    while (n < n2) {
                        localVariableBinding2 = localVariableBindingArray[n];
                        if (CharOperation.equals(name, localVariableBinding2.name)) {
                            scope.problemReporter().illegalRedeclarationOfPatternVar(localVariableBinding2, localVariableBinding2.declaration);
                        }
                        ++n;
                    }
                    ++n3;
                }
            }
            first = this.valueIfTrue.patternVarsWhenFalse;
            second = this.valueIfFalse.patternVarsWhenFalse;
            if (first != null && second != null) {
                localVariableBindingArray2 = first;
                n4 = first.length;
                n3 = 0;
                while (n3 < n4) {
                    localVariableBinding = localVariableBindingArray2[n3];
                    name = localVariableBinding.name;
                    localVariableBindingArray = second;
                    n2 = second.length;
                    n = 0;
                    while (n < n2) {
                        localVariableBinding2 = localVariableBindingArray[n];
                        if (CharOperation.equals(name, localVariableBinding2.name)) {
                            scope.problemReporter().illegalRedeclarationOfPatternVar(localVariableBinding2, localVariableBinding2.declaration);
                        }
                        ++n;
                    }
                    ++n3;
                }
            }
        }
        if (!this.condition.containsPatternVariable()) {
            return;
        }
        if (this.condition.getPatternVariableIntroduced() != null) {
            char[] name = this.condition.getPatternVariableIntroduced().name;
            LocalDeclaration localVar = this.valueIfTrue.getPatternVariableIntroduced();
            if (localVar != null && CharOperation.equals(name, localVar.name)) {
                scope.problemReporter().illegalRedeclarationOfPatternVar(localVar.binding, localVar);
                return;
            }
            localVar = this.valueIfFalse.getPatternVariableIntroduced();
            if (localVar != null && CharOperation.equals(name, localVar.name)) {
                scope.problemReporter().illegalRedeclarationOfPatternVar(localVar.binding, localVar);
                return;
            }
        }
        this.condition.collectPatternVariablesToScope(this.patternVarsWhenTrue, scope);
        variables = this.condition.getPatternVariablesWhenTrue();
        this.valueIfTrue.addPatternVariablesWhenTrue(variables);
        this.valueIfFalse.addPatternVariablesWhenFalse(variables);
        this.valueIfTrue.collectPatternVariablesToScope(variables, scope);
        variables = this.condition.getPatternVariablesWhenFalse();
        this.valueIfTrue.addPatternVariablesWhenFalse(variables);
        this.valueIfFalse.addPatternVariablesWhenTrue(variables);
        this.valueIfFalse.collectPatternVariablesToScope(variables, scope);
    }

    @Override
    public TypeBinding resolveType(BlockScope scope) {
        Constant falseConstant;
        Constant trueConstant;
        Constant condConstant;
        LookupEnvironment env = scope.environment();
        long sourceLevel = scope.compilerOptions().sourceLevel;
        boolean use15specifics = sourceLevel >= 0x310000L;
        boolean bl = this.use18specifics = sourceLevel >= 0x340000L;
        if (this.use18specifics && (this.expressionContext == ExpressionContext.ASSIGNMENT_CONTEXT || this.expressionContext == ExpressionContext.INVOCATION_CONTEXT)) {
            this.valueIfTrue.setExpressionContext(this.expressionContext);
            this.valueIfTrue.setExpectedType(this.expectedType);
            this.valueIfFalse.setExpressionContext(this.expressionContext);
            this.valueIfFalse.setExpectedType(this.expectedType);
        }
        this.collectPatternVariablesToScope(null, scope);
        if (this.constant != Constant.NotAConstant) {
            this.constant = Constant.NotAConstant;
            TypeBinding conditionType = this.condition.resolveTypeExpecting(scope, TypeBinding.BOOLEAN);
            this.condition.computeConversion(scope, TypeBinding.BOOLEAN, conditionType);
            if (this.valueIfTrue instanceof CastExpression) {
                this.valueIfTrue.bits |= 0x20;
            }
            this.originalValueIfTrueType = this.valueIfTrue.resolveType(scope);
            if (this.valueIfFalse instanceof CastExpression) {
                this.valueIfFalse.bits |= 0x20;
            }
            this.originalValueIfFalseType = this.valueIfFalse.resolveType(scope);
            if (conditionType == null || this.originalValueIfTrueType == null || this.originalValueIfFalseType == null) {
                return null;
            }
        } else {
            if (this.originalValueIfTrueType.kind() == 65540) {
                this.originalValueIfTrueType = this.valueIfTrue.resolveType(scope);
            }
            if (this.originalValueIfFalseType.kind() == 65540) {
                this.originalValueIfFalseType = this.valueIfFalse.resolveType(scope);
            }
            if (this.originalValueIfTrueType == null || !this.originalValueIfTrueType.isValidBinding()) {
                this.resolvedType = null;
                return null;
            }
            if (this.originalValueIfFalseType == null || !this.originalValueIfFalseType.isValidBinding()) {
                this.resolvedType = null;
                return null;
            }
        }
        if ((condConstant = this.condition.constant) != Constant.NotAConstant && (trueConstant = this.valueIfTrue.constant) != Constant.NotAConstant && (falseConstant = this.valueIfFalse.constant) != Constant.NotAConstant) {
            Constant constant = this.constant = condConstant.booleanValue() ? trueConstant : falseConstant;
        }
        if (this.isPolyExpression()) {
            if (this.expectedType == null || !this.expectedType.isProperType(true)) {
                this.constant = Constant.NotAConstant;
                return new PolyTypeBinding(this);
            }
            this.resolvedType = this.computeConversions(scope, this.expectedType) ? this.expectedType : null;
            return this.resolvedType;
        }
        TypeBinding valueIfTrueType = this.originalValueIfTrueType;
        TypeBinding valueIfFalseType = this.originalValueIfFalseType;
        if (use15specifics && TypeBinding.notEquals(valueIfTrueType, valueIfFalseType)) {
            TypeBinding unboxedIfTrueType;
            if (valueIfTrueType.isBaseType()) {
                if (valueIfFalseType.isBaseType()) {
                    if (valueIfTrueType == TypeBinding.NULL) {
                        valueIfFalseType = env.computeBoxingType(valueIfFalseType);
                    } else if (valueIfFalseType == TypeBinding.NULL) {
                        valueIfTrueType = env.computeBoxingType(valueIfTrueType);
                    }
                } else {
                    TypeBinding unboxedIfFalseType;
                    TypeBinding typeBinding = unboxedIfFalseType = valueIfFalseType.isBaseType() ? valueIfFalseType : env.computeBoxingType(valueIfFalseType);
                    if (valueIfTrueType.isNumericType() && unboxedIfFalseType.isNumericType()) {
                        valueIfFalseType = unboxedIfFalseType;
                    } else if (valueIfTrueType != TypeBinding.NULL) {
                        valueIfFalseType = env.computeBoxingType(valueIfFalseType);
                    }
                }
            } else if (valueIfFalseType.isBaseType()) {
                TypeBinding typeBinding = unboxedIfTrueType = valueIfTrueType.isBaseType() ? valueIfTrueType : env.computeBoxingType(valueIfTrueType);
                if (unboxedIfTrueType.isNumericType() && valueIfFalseType.isNumericType()) {
                    valueIfTrueType = unboxedIfTrueType;
                } else if (valueIfFalseType != TypeBinding.NULL) {
                    valueIfTrueType = env.computeBoxingType(valueIfTrueType);
                }
            } else {
                unboxedIfTrueType = env.computeBoxingType(valueIfTrueType);
                TypeBinding unboxedIfFalseType = env.computeBoxingType(valueIfFalseType);
                if (unboxedIfTrueType.isNumericType() && unboxedIfFalseType.isNumericType()) {
                    valueIfTrueType = unboxedIfTrueType;
                    valueIfFalseType = unboxedIfFalseType;
                }
            }
        }
        if (TypeBinding.equalsEquals(valueIfTrueType, valueIfFalseType)) {
            this.valueIfTrue.computeConversion(scope, valueIfTrueType, this.originalValueIfTrueType);
            this.valueIfFalse.computeConversion(scope, valueIfFalseType, this.originalValueIfFalseType);
            if (TypeBinding.equalsEquals(valueIfTrueType, TypeBinding.BOOLEAN)) {
                this.optimizedIfTrueConstant = this.valueIfTrue.optimizedBooleanConstant();
                this.optimizedIfFalseConstant = this.valueIfFalse.optimizedBooleanConstant();
                if (this.optimizedIfTrueConstant != Constant.NotAConstant && this.optimizedIfFalseConstant != Constant.NotAConstant && this.optimizedIfTrueConstant.booleanValue() == this.optimizedIfFalseConstant.booleanValue()) {
                    this.optimizedBooleanConstant = this.optimizedIfTrueConstant;
                } else {
                    condConstant = this.condition.optimizedBooleanConstant();
                    if (condConstant != Constant.NotAConstant) {
                        this.optimizedBooleanConstant = condConstant.booleanValue() ? this.optimizedIfTrueConstant : this.optimizedIfFalseConstant;
                    }
                }
            }
            this.resolvedType = NullAnnotationMatching.moreDangerousType(valueIfTrueType, valueIfFalseType);
            return this.resolvedType;
        }
        if (valueIfTrueType.isNumericType() && valueIfFalseType.isNumericType()) {
            if (TypeBinding.equalsEquals(valueIfTrueType, TypeBinding.BYTE) && TypeBinding.equalsEquals(valueIfFalseType, TypeBinding.SHORT) || TypeBinding.equalsEquals(valueIfTrueType, TypeBinding.SHORT) && TypeBinding.equalsEquals(valueIfFalseType, TypeBinding.BYTE)) {
                this.valueIfTrue.computeConversion(scope, TypeBinding.SHORT, this.originalValueIfTrueType);
                this.valueIfFalse.computeConversion(scope, TypeBinding.SHORT, this.originalValueIfFalseType);
                this.resolvedType = TypeBinding.SHORT;
                return this.resolvedType;
            }
            if ((TypeBinding.equalsEquals(valueIfTrueType, TypeBinding.BYTE) || TypeBinding.equalsEquals(valueIfTrueType, TypeBinding.SHORT) || TypeBinding.equalsEquals(valueIfTrueType, TypeBinding.CHAR)) && TypeBinding.equalsEquals(valueIfFalseType, TypeBinding.INT) && this.valueIfFalse.isConstantValueOfTypeAssignableToType(valueIfFalseType, valueIfTrueType)) {
                this.valueIfTrue.computeConversion(scope, valueIfTrueType, this.originalValueIfTrueType);
                this.valueIfFalse.computeConversion(scope, valueIfTrueType, this.originalValueIfFalseType);
                this.resolvedType = valueIfTrueType;
                return this.resolvedType;
            }
            if ((TypeBinding.equalsEquals(valueIfFalseType, TypeBinding.BYTE) || TypeBinding.equalsEquals(valueIfFalseType, TypeBinding.SHORT) || TypeBinding.equalsEquals(valueIfFalseType, TypeBinding.CHAR)) && TypeBinding.equalsEquals(valueIfTrueType, TypeBinding.INT) && this.valueIfTrue.isConstantValueOfTypeAssignableToType(valueIfTrueType, valueIfFalseType)) {
                this.valueIfTrue.computeConversion(scope, valueIfFalseType, this.originalValueIfTrueType);
                this.valueIfFalse.computeConversion(scope, valueIfFalseType, this.originalValueIfFalseType);
                this.resolvedType = valueIfFalseType;
                return this.resolvedType;
            }
            if (BaseTypeBinding.isNarrowing(valueIfTrueType.id, 10) && BaseTypeBinding.isNarrowing(valueIfFalseType.id, 10)) {
                this.valueIfTrue.computeConversion(scope, TypeBinding.INT, this.originalValueIfTrueType);
                this.valueIfFalse.computeConversion(scope, TypeBinding.INT, this.originalValueIfFalseType);
                this.resolvedType = TypeBinding.INT;
                return this.resolvedType;
            }
            if (BaseTypeBinding.isNarrowing(valueIfTrueType.id, 7) && BaseTypeBinding.isNarrowing(valueIfFalseType.id, 7)) {
                this.valueIfTrue.computeConversion(scope, TypeBinding.LONG, this.originalValueIfTrueType);
                this.valueIfFalse.computeConversion(scope, TypeBinding.LONG, this.originalValueIfFalseType);
                this.resolvedType = TypeBinding.LONG;
                return this.resolvedType;
            }
            if (BaseTypeBinding.isNarrowing(valueIfTrueType.id, 9) && BaseTypeBinding.isNarrowing(valueIfFalseType.id, 9)) {
                this.valueIfTrue.computeConversion(scope, TypeBinding.FLOAT, this.originalValueIfTrueType);
                this.valueIfFalse.computeConversion(scope, TypeBinding.FLOAT, this.originalValueIfFalseType);
                this.resolvedType = TypeBinding.FLOAT;
                return this.resolvedType;
            }
            this.valueIfTrue.computeConversion(scope, TypeBinding.DOUBLE, this.originalValueIfTrueType);
            this.valueIfFalse.computeConversion(scope, TypeBinding.DOUBLE, this.originalValueIfFalseType);
            this.resolvedType = TypeBinding.DOUBLE;
            return this.resolvedType;
        }
        if (valueIfTrueType.isBaseType() && valueIfTrueType != TypeBinding.NULL) {
            if (use15specifics) {
                valueIfTrueType = env.computeBoxingType(valueIfTrueType);
            } else {
                scope.problemReporter().conditionalArgumentsIncompatibleTypes(this, valueIfTrueType, valueIfFalseType);
                return null;
            }
        }
        if (valueIfFalseType.isBaseType() && valueIfFalseType != TypeBinding.NULL) {
            if (use15specifics) {
                valueIfFalseType = env.computeBoxingType(valueIfFalseType);
            } else {
                scope.problemReporter().conditionalArgumentsIncompatibleTypes(this, valueIfTrueType, valueIfFalseType);
                return null;
            }
        }
        if (use15specifics) {
            TypeBinding commonType = null;
            commonType = valueIfTrueType == TypeBinding.NULL ? valueIfFalseType : (valueIfFalseType == TypeBinding.NULL ? valueIfTrueType : scope.lowerUpperBound(new TypeBinding[]{valueIfTrueType, valueIfFalseType}));
            if (commonType != null) {
                this.valueIfTrue.computeConversion(scope, commonType, this.originalValueIfTrueType);
                this.valueIfFalse.computeConversion(scope, commonType, this.originalValueIfFalseType);
                this.resolvedType = commonType.capture(scope, this.sourceStart, this.sourceEnd);
                return this.resolvedType;
            }
        } else {
            if (valueIfFalseType.isCompatibleWith(valueIfTrueType)) {
                this.valueIfTrue.computeConversion(scope, valueIfTrueType, this.originalValueIfTrueType);
                this.valueIfFalse.computeConversion(scope, valueIfTrueType, this.originalValueIfFalseType);
                this.resolvedType = valueIfTrueType;
                return this.resolvedType;
            }
            if (valueIfTrueType.isCompatibleWith(valueIfFalseType)) {
                this.valueIfTrue.computeConversion(scope, valueIfFalseType, this.originalValueIfTrueType);
                this.valueIfFalse.computeConversion(scope, valueIfFalseType, this.originalValueIfFalseType);
                this.resolvedType = valueIfFalseType;
                return this.resolvedType;
            }
        }
        scope.problemReporter().conditionalArgumentsIncompatibleTypes(this, valueIfTrueType, valueIfFalseType);
        return null;
    }

    protected boolean computeConversions(BlockScope scope, TypeBinding targetType) {
        boolean ok = true;
        if (this.originalValueIfTrueType != null && this.originalValueIfTrueType.isValidBinding()) {
            if (this.valueIfTrue.isConstantValueOfTypeAssignableToType(this.originalValueIfTrueType, targetType) || this.originalValueIfTrueType.isCompatibleWith(targetType)) {
                this.valueIfTrue.computeConversion(scope, targetType, this.originalValueIfTrueType);
                if (this.originalValueIfTrueType.needsUncheckedConversion(targetType)) {
                    scope.problemReporter().unsafeTypeConversion(this.valueIfTrue, this.originalValueIfTrueType, targetType);
                }
                if (this.valueIfTrue instanceof CastExpression && (this.valueIfTrue.bits & 0x4020) == 0) {
                    CastExpression.checkNeedForAssignedCast(scope, targetType, (CastExpression)this.valueIfTrue);
                }
            } else if (this.isBoxingCompatible(this.originalValueIfTrueType, targetType, this.valueIfTrue, scope)) {
                this.valueIfTrue.computeConversion(scope, targetType, this.originalValueIfTrueType);
                if (this.valueIfTrue instanceof CastExpression && (this.valueIfTrue.bits & 0x4020) == 0) {
                    CastExpression.checkNeedForAssignedCast(scope, targetType, (CastExpression)this.valueIfTrue);
                }
            } else {
                scope.problemReporter().typeMismatchError(this.originalValueIfTrueType, targetType, this.valueIfTrue, null);
                ok = false;
            }
        }
        if (this.originalValueIfFalseType != null && this.originalValueIfFalseType.isValidBinding()) {
            if (this.valueIfFalse.isConstantValueOfTypeAssignableToType(this.originalValueIfFalseType, targetType) || this.originalValueIfFalseType.isCompatibleWith(targetType)) {
                this.valueIfFalse.computeConversion(scope, targetType, this.originalValueIfFalseType);
                if (this.originalValueIfFalseType.needsUncheckedConversion(targetType)) {
                    scope.problemReporter().unsafeTypeConversion(this.valueIfFalse, this.originalValueIfFalseType, targetType);
                }
                if (this.valueIfFalse instanceof CastExpression && (this.valueIfFalse.bits & 0x4020) == 0) {
                    CastExpression.checkNeedForAssignedCast(scope, targetType, (CastExpression)this.valueIfFalse);
                }
            } else if (this.isBoxingCompatible(this.originalValueIfFalseType, targetType, this.valueIfFalse, scope)) {
                this.valueIfFalse.computeConversion(scope, targetType, this.originalValueIfFalseType);
                if (this.valueIfFalse instanceof CastExpression && (this.valueIfFalse.bits & 0x4020) == 0) {
                    CastExpression.checkNeedForAssignedCast(scope, targetType, (CastExpression)this.valueIfFalse);
                }
            } else {
                scope.problemReporter().typeMismatchError(this.originalValueIfFalseType, targetType, this.valueIfFalse, null);
                ok = false;
            }
        }
        return ok;
    }

    @Override
    public void setExpectedType(TypeBinding expectedType) {
        this.expectedType = expectedType;
    }

    @Override
    public void setExpressionContext(ExpressionContext context) {
        this.expressionContext = context;
    }

    @Override
    public ExpressionContext getExpressionContext() {
        return this.expressionContext;
    }

    @Override
    public Expression[] getPolyExpressions() {
        Expression[] truePolys = this.valueIfTrue.getPolyExpressions();
        Expression[] falsePolys = this.valueIfFalse.getPolyExpressions();
        if (truePolys.length == 0) {
            return falsePolys;
        }
        if (falsePolys.length == 0) {
            return truePolys;
        }
        Expression[] allPolys = new Expression[truePolys.length + falsePolys.length];
        System.arraycopy(truePolys, 0, allPolys, 0, truePolys.length);
        System.arraycopy(falsePolys, 0, allPolys, truePolys.length, falsePolys.length);
        return allPolys;
    }

    @Override
    public boolean isPertinentToApplicability(TypeBinding targetType, MethodBinding method) {
        return this.valueIfTrue.isPertinentToApplicability(targetType, method) && this.valueIfFalse.isPertinentToApplicability(targetType, method);
    }

    @Override
    public boolean isPotentiallyCompatibleWith(TypeBinding targetType, Scope scope) {
        return this.valueIfTrue.isPotentiallyCompatibleWith(targetType, scope) && this.valueIfFalse.isPotentiallyCompatibleWith(targetType, scope);
    }

    @Override
    public boolean isFunctionalType() {
        return this.valueIfTrue.isFunctionalType() || this.valueIfFalse.isFunctionalType();
    }

    @Override
    public boolean isPolyExpression() throws UnsupportedOperationException {
        if (!this.use18specifics) {
            return false;
        }
        if (this.isPolyExpression) {
            return true;
        }
        if (this.expressionContext != ExpressionContext.ASSIGNMENT_CONTEXT && this.expressionContext != ExpressionContext.INVOCATION_CONTEXT) {
            return false;
        }
        if (this.originalValueIfTrueType == null || this.originalValueIfFalseType == null) {
            return false;
        }
        if (this.valueIfTrue.isPolyExpression() || this.valueIfFalse.isPolyExpression()) {
            return true;
        }
        if ((this.originalValueIfTrueType.isBaseType() || this.originalValueIfTrueType.id >= 26 && this.originalValueIfTrueType.id <= 33) && (this.originalValueIfFalseType.isBaseType() || this.originalValueIfFalseType.id >= 26 && this.originalValueIfFalseType.id <= 33)) {
            return false;
        }
        this.isPolyExpression = true;
        return true;
    }

    @Override
    public boolean isCompatibleWith(TypeBinding left, Scope scope) {
        return this.isPolyExpression() ? this.valueIfTrue.isCompatibleWith(left, scope) && this.valueIfFalse.isCompatibleWith(left, scope) : super.isCompatibleWith(left, scope);
    }

    @Override
    public boolean isBoxingCompatibleWith(TypeBinding targetType, Scope scope) {
        return this.isPolyExpression() ? (this.valueIfTrue.isCompatibleWith(targetType, scope) || this.valueIfTrue.isBoxingCompatibleWith(targetType, scope)) && (this.valueIfFalse.isCompatibleWith(targetType, scope) || this.valueIfFalse.isBoxingCompatibleWith(targetType, scope)) : super.isBoxingCompatibleWith(targetType, scope);
    }

    @Override
    public boolean sIsMoreSpecific(TypeBinding s, TypeBinding t, Scope scope) {
        if (super.sIsMoreSpecific(s, t, scope)) {
            return true;
        }
        return this.isPolyExpression() ? this.valueIfTrue.sIsMoreSpecific(s, t, scope) && this.valueIfFalse.sIsMoreSpecific(s, t, scope) : false;
    }

    @Override
    public void traverse(ASTVisitor visitor, BlockScope scope) {
        if (visitor.visit(this, scope)) {
            this.condition.traverse(visitor, scope);
            this.valueIfTrue.traverse(visitor, scope);
            this.valueIfFalse.traverse(visitor, scope);
        }
        visitor.endVisit(this, scope);
    }
}

