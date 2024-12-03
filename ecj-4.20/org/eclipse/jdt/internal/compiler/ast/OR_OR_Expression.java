/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.ast.Assignment;
import org.eclipse.jdt.internal.compiler.ast.BinaryExpression;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.codegen.BranchLabel;
import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
import org.eclipse.jdt.internal.compiler.flow.FlowContext;
import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;

public class OR_OR_Expression
extends BinaryExpression {
    int rightInitStateIndex = -1;
    int mergedInitStateIndex = -1;

    public OR_OR_Expression(Expression left, Expression right, int operator) {
        super(left, right, operator);
    }

    @Override
    public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo) {
        boolean isLeftOptimizedFalse;
        Constant cst = this.left.optimizedBooleanConstant();
        boolean isLeftOptimizedTrue = cst != Constant.NotAConstant && cst.booleanValue();
        boolean bl = isLeftOptimizedFalse = cst != Constant.NotAConstant && !cst.booleanValue();
        if (isLeftOptimizedFalse) {
            FlowInfo mergedInfo = this.left.analyseCode(currentScope, flowContext, flowInfo).unconditionalInits();
            flowContext.expireNullCheckedFieldInfo();
            mergedInfo = this.right.analyseCode(currentScope, flowContext, mergedInfo);
            flowContext.expireNullCheckedFieldInfo();
            this.mergedInitStateIndex = currentScope.methodScope().recordInitializationStates(mergedInfo);
            return mergedInfo;
        }
        FlowInfo leftInfo = this.left.analyseCode(currentScope, flowContext, flowInfo);
        if ((flowContext.tagBits & 4) == 0) {
            flowContext.expireNullCheckedFieldInfo();
        }
        FlowInfo rightInfo = leftInfo.initsWhenFalse().unconditionalCopy();
        this.rightInitStateIndex = currentScope.methodScope().recordInitializationStates(rightInfo);
        int previousMode = rightInfo.reachMode();
        if (isLeftOptimizedTrue && (rightInfo.reachMode() & 3) == 0) {
            currentScope.problemReporter().fakeReachable(this.right);
            rightInfo.setReachMode(1);
        }
        this.left.updateFlowOnBooleanResult(rightInfo, false);
        rightInfo = this.right.analyseCode(currentScope, flowContext, rightInfo);
        if ((flowContext.tagBits & 4) == 0) {
            flowContext.expireNullCheckedFieldInfo();
        }
        this.left.checkNPEbyUnboxing(currentScope, flowContext, flowInfo);
        this.right.checkNPEbyUnboxing(currentScope, flowContext, leftInfo.initsWhenFalse());
        FlowInfo leftInfoWhenTrueForMerging = leftInfo.initsWhenTrue().unconditionalCopy().addPotentialInitializationsFrom(rightInfo.unconditionalInitsWithoutSideEffect());
        FlowInfo mergedInfo = FlowInfo.conditional(leftInfoWhenTrueForMerging.unconditionalInits().mergedWith(rightInfo.safeInitsWhenTrue().setReachMode(previousMode).unconditionalInits()), rightInfo.initsWhenFalse());
        this.mergedInitStateIndex = currentScope.methodScope().recordInitializationStates(mergedInfo);
        return mergedInfo;
    }

    @Override
    public void generateCode(BlockScope currentScope, CodeStream codeStream, boolean valueRequired) {
        boolean rightIsTrue;
        boolean rightIsConst;
        boolean leftIsTrue;
        boolean leftIsConst;
        BranchLabel trueLabel;
        block23: {
            block22: {
                block21: {
                    int pc = codeStream.position;
                    if (this.constant != Constant.NotAConstant) {
                        if (valueRequired) {
                            codeStream.generateConstant(this.constant, this.implicitConversion);
                        }
                        codeStream.recordPositionsFrom(pc, this.sourceStart);
                        return;
                    }
                    Constant cst = this.right.constant;
                    if (cst != Constant.NotAConstant) {
                        if (cst.booleanValue()) {
                            this.left.generateCode(currentScope, codeStream, false);
                            if (valueRequired) {
                                codeStream.iconst_1();
                            }
                        } else {
                            this.left.generateCode(currentScope, codeStream, valueRequired);
                        }
                        if (this.mergedInitStateIndex != -1) {
                            codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.mergedInitStateIndex);
                        }
                        codeStream.generateImplicitConversion(this.implicitConversion);
                        codeStream.recordPositionsFrom(pc, this.sourceStart);
                        return;
                    }
                    trueLabel = new BranchLabel(codeStream);
                    cst = this.left.optimizedBooleanConstant();
                    leftIsConst = cst != Constant.NotAConstant;
                    leftIsTrue = leftIsConst && cst.booleanValue();
                    cst = this.right.optimizedBooleanConstant();
                    rightIsConst = cst != Constant.NotAConstant;
                    boolean bl = rightIsTrue = rightIsConst && cst.booleanValue();
                    if (!leftIsConst) break block21;
                    this.left.generateCode(currentScope, codeStream, false);
                    if (!leftIsTrue) break block22;
                    break block23;
                }
                this.left.generateOptimizedBoolean(currentScope, codeStream, trueLabel, null, true);
            }
            if (this.rightInitStateIndex != -1) {
                codeStream.addDefinitelyAssignedVariables(currentScope, this.rightInitStateIndex);
            }
            if (rightIsConst) {
                this.right.generateCode(currentScope, codeStream, false);
            } else {
                this.right.generateOptimizedBoolean(currentScope, codeStream, trueLabel, null, valueRequired);
            }
        }
        if (this.mergedInitStateIndex != -1) {
            codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.mergedInitStateIndex);
        }
        if (valueRequired) {
            if (leftIsConst && leftIsTrue) {
                codeStream.iconst_1();
                codeStream.recordPositionsFrom(codeStream.position, this.left.sourceEnd);
            } else {
                if (rightIsConst && rightIsTrue) {
                    codeStream.iconst_1();
                    codeStream.recordPositionsFrom(codeStream.position, this.left.sourceEnd);
                } else {
                    codeStream.iconst_0();
                }
                if (trueLabel.forwardReferenceCount() > 0) {
                    if ((this.bits & 0x10) != 0) {
                        codeStream.generateImplicitConversion(this.implicitConversion);
                        codeStream.generateReturnBytecode(this);
                        trueLabel.place();
                        codeStream.iconst_1();
                    } else {
                        BranchLabel endLabel = new BranchLabel(codeStream);
                        codeStream.goto_(endLabel);
                        codeStream.decrStackSize(1);
                        trueLabel.place();
                        codeStream.iconst_1();
                        endLabel.place();
                    }
                } else {
                    trueLabel.place();
                }
            }
            codeStream.generateImplicitConversion(this.implicitConversion);
            codeStream.recordPositionsFrom(codeStream.position, this.sourceEnd);
        } else {
            trueLabel.place();
        }
    }

    @Override
    public void generateOptimizedBoolean(BlockScope currentScope, CodeStream codeStream, BranchLabel trueLabel, BranchLabel falseLabel, boolean valueRequired) {
        boolean rightIsTrue;
        if (this.constant != Constant.NotAConstant) {
            super.generateOptimizedBoolean(currentScope, codeStream, trueLabel, falseLabel, valueRequired);
            return;
        }
        Constant cst = this.right.constant;
        if (cst != Constant.NotAConstant && !cst.booleanValue()) {
            int pc = codeStream.position;
            this.left.generateOptimizedBoolean(currentScope, codeStream, trueLabel, falseLabel, valueRequired);
            if (this.mergedInitStateIndex != -1) {
                codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.mergedInitStateIndex);
            }
            codeStream.recordPositionsFrom(pc, this.sourceStart);
            return;
        }
        cst = this.left.optimizedBooleanConstant();
        boolean leftIsConst = cst != Constant.NotAConstant;
        boolean leftIsTrue = leftIsConst && cst.booleanValue();
        cst = this.right.optimizedBooleanConstant();
        boolean rightIsConst = cst != Constant.NotAConstant;
        boolean bl = rightIsTrue = rightIsConst && cst.booleanValue();
        if (falseLabel == null) {
            if (trueLabel != null) {
                this.left.generateOptimizedBoolean(currentScope, codeStream, trueLabel, null, !leftIsConst);
                if (leftIsTrue) {
                    if (valueRequired) {
                        codeStream.goto_(trueLabel);
                    }
                    codeStream.recordPositionsFrom(codeStream.position, this.left.sourceEnd);
                } else {
                    if (this.rightInitStateIndex != -1) {
                        codeStream.addDefinitelyAssignedVariables(currentScope, this.rightInitStateIndex);
                    }
                    this.right.generateOptimizedBoolean(currentScope, codeStream, trueLabel, null, valueRequired && !rightIsConst);
                    if (valueRequired && rightIsTrue) {
                        codeStream.goto_(trueLabel);
                        codeStream.recordPositionsFrom(codeStream.position, this.sourceEnd);
                    }
                }
            }
        } else if (trueLabel == null) {
            BranchLabel internalTrueLabel = new BranchLabel(codeStream);
            this.left.generateOptimizedBoolean(currentScope, codeStream, internalTrueLabel, null, !leftIsConst);
            if (leftIsTrue) {
                internalTrueLabel.place();
            } else {
                if (this.rightInitStateIndex != -1) {
                    codeStream.addDefinitelyAssignedVariables(currentScope, this.rightInitStateIndex);
                }
                this.right.generateOptimizedBoolean(currentScope, codeStream, null, falseLabel, valueRequired && !rightIsConst);
                int pc = codeStream.position;
                if (valueRequired && rightIsConst && !rightIsTrue) {
                    codeStream.goto_(falseLabel);
                    codeStream.recordPositionsFrom(pc, this.sourceEnd);
                }
                internalTrueLabel.place();
            }
        }
        if (this.mergedInitStateIndex != -1) {
            codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.mergedInitStateIndex);
        }
    }

    @Override
    public void collectPatternVariablesToScope(LocalVariableBinding[] variables, BlockScope scope) {
        LocalVariableBinding[] temp = variables;
        this.left.collectPatternVariablesToScope(variables, scope);
        variables = this.left.getPatternVariablesWhenFalse();
        this.addPatternVariablesWhenFalse(variables);
        int length = (variables == null ? 0 : variables.length) + (temp == null ? 0 : temp.length);
        LocalVariableBinding[] newArray = new LocalVariableBinding[length];
        if (variables != null) {
            System.arraycopy(variables, 0, newArray, 0, variables.length);
        }
        if (temp != null) {
            System.arraycopy(temp, 0, newArray, variables == null ? 0 : variables.length, temp.length);
        }
        this.right.collectPatternVariablesToScope(newArray, scope);
        variables = this.right.getPatternVariablesWhenFalse();
        this.addPatternVariablesWhenFalse(variables);
        variables = this.left.getPatternVariablesWhenTrue();
        this.right.addPatternVariablesWhenFalse(variables);
        variables = this.left.getPatternVariablesWhenFalse();
        this.right.addPatternVariablesWhenTrue(variables);
    }

    @Override
    public boolean isCompactableOperation() {
        return false;
    }

    @Override
    public TypeBinding resolveType(BlockScope scope) {
        TypeBinding result = super.resolveType(scope);
        Binding leftDirect = Expression.getDirectBinding(this.left);
        if (leftDirect != null && leftDirect == Expression.getDirectBinding(this.right) && !(this.right instanceof Assignment)) {
            scope.problemReporter().comparingIdenticalExpressions(this);
        }
        return result;
    }

    @Override
    public void traverse(ASTVisitor visitor, BlockScope scope) {
        if (visitor.visit(this, scope)) {
            this.left.traverse(visitor, scope);
            this.right.traverse(visitor, scope);
        }
        visitor.endVisit(this, scope);
    }
}

