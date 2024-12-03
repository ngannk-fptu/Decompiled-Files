/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.ast.Assignment;
import org.eclipse.jdt.internal.compiler.ast.BinaryExpression;
import org.eclipse.jdt.internal.compiler.ast.CastExpression;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.NullLiteral;
import org.eclipse.jdt.internal.compiler.ast.Reference;
import org.eclipse.jdt.internal.compiler.ast.SingleNameReference;
import org.eclipse.jdt.internal.compiler.codegen.BranchLabel;
import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
import org.eclipse.jdt.internal.compiler.flow.FlowContext;
import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
import org.eclipse.jdt.internal.compiler.flow.UnconditionalFlowInfo;
import org.eclipse.jdt.internal.compiler.impl.BooleanConstant;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;

public class EqualExpression
extends BinaryExpression {
    public EqualExpression(Expression left, Expression right, int operator) {
        super(left, right, operator);
    }

    private void checkNullComparison(BlockScope scope, FlowContext flowContext, FlowInfo flowInfo, FlowInfo initsWhenTrue, FlowInfo initsWhenFalse) {
        FieldBinding field;
        LocalVariableBinding local;
        boolean checkEquality;
        int rightStatus = this.right.nullStatus(flowInfo, flowContext);
        int leftStatus = this.left.nullStatus(flowInfo, flowContext);
        boolean leftNonNullChecked = false;
        boolean rightNonNullChecked = false;
        boolean bl = checkEquality = (this.bits & 0x3F00) >> 8 == 18;
        if ((flowContext.tagBits & 0xF000) == 0) {
            if (leftStatus == 4 && rightStatus == 2) {
                leftNonNullChecked = scope.problemReporter().expressionNonNullComparison(this.left, checkEquality);
            } else if (leftStatus == 2 && rightStatus == 4) {
                rightNonNullChecked = scope.problemReporter().expressionNonNullComparison(this.right, checkEquality);
            }
        }
        boolean contextualCheckEquality = checkEquality ^ (flowContext.tagBits & 4) != 0;
        if (!leftNonNullChecked) {
            local = this.left.localVariableBinding();
            if (local != null) {
                if ((local.type.tagBits & 2L) == 0L) {
                    this.checkVariableComparison(scope, flowContext, flowInfo, initsWhenTrue, initsWhenFalse, local, rightStatus, this.left);
                }
            } else if (this.left instanceof Reference && (!contextualCheckEquality && rightStatus == 2 || contextualCheckEquality && rightStatus == 4) && scope.compilerOptions().enableSyntacticNullAnalysisForFields && (field = ((Reference)this.left).lastFieldBinding()) != null && (field.type.tagBits & 2L) == 0L) {
                flowContext.recordNullCheckedFieldReference((Reference)this.left, 1);
            }
        }
        if (!rightNonNullChecked) {
            local = this.right.localVariableBinding();
            if (local != null) {
                if ((local.type.tagBits & 2L) == 0L) {
                    this.checkVariableComparison(scope, flowContext, flowInfo, initsWhenTrue, initsWhenFalse, local, leftStatus, this.right);
                }
            } else if (this.right instanceof Reference && (!contextualCheckEquality && leftStatus == 2 || contextualCheckEquality && leftStatus == 4) && scope.compilerOptions().enableSyntacticNullAnalysisForFields && (field = ((Reference)this.right).lastFieldBinding()) != null && (field.type.tagBits & 2L) == 0L) {
                flowContext.recordNullCheckedFieldReference((Reference)this.right, 1);
            }
        }
        if (leftNonNullChecked || rightNonNullChecked) {
            if (checkEquality) {
                initsWhenTrue.setReachMode(2);
            } else {
                initsWhenFalse.setReachMode(2);
            }
        }
    }

    private void checkVariableComparison(BlockScope scope, FlowContext flowContext, FlowInfo flowInfo, FlowInfo initsWhenTrue, FlowInfo initsWhenFalse, LocalVariableBinding local, int nullStatus, Expression reference) {
        switch (nullStatus) {
            case 2: {
                if ((this.bits & 0x3F00) >> 8 == 18) {
                    flowContext.recordUsingNullReference(scope, local, reference, 256, flowInfo);
                    initsWhenTrue.markAsComparedEqualToNull(local);
                    initsWhenFalse.markAsComparedEqualToNonNull(local);
                    break;
                }
                flowContext.recordUsingNullReference(scope, local, reference, 512, flowInfo);
                initsWhenTrue.markAsComparedEqualToNonNull(local);
                initsWhenFalse.markAsComparedEqualToNull(local);
                break;
            }
            case 4: {
                if ((this.bits & 0x3F00) >> 8 == 18) {
                    flowContext.recordUsingNullReference(scope, local, reference, 513, flowInfo);
                    initsWhenTrue.markAsComparedEqualToNonNull(local);
                    break;
                }
                flowContext.recordUsingNullReference(scope, local, reference, 257, flowInfo);
            }
        }
    }

    private void analyzeLocalVariable(Expression exp, FlowInfo flowInfo) {
        if (exp instanceof SingleNameReference && (exp.bits & 2) != 0) {
            LocalVariableBinding localBinding = (LocalVariableBinding)((SingleNameReference)exp).binding;
            if ((flowInfo.tagBits & 3) == 0) {
                localBinding.useFlag = 1;
            } else if (localBinding.useFlag == 0) {
                localBinding.useFlag = 2;
            }
        }
    }

    @Override
    public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo) {
        FlowInfo result;
        if ((this.bits & 0x3F00) >> 8 == 18) {
            if (this.left.constant != Constant.NotAConstant && this.left.constant.typeID() == 5) {
                if (this.left.constant.booleanValue()) {
                    result = this.right.analyseCode(currentScope, flowContext, flowInfo);
                } else {
                    result = this.right.analyseCode(currentScope, flowContext, flowInfo).asNegatedCondition();
                    this.analyzeLocalVariable(this.left, flowInfo);
                }
            } else if (this.right.constant != Constant.NotAConstant && this.right.constant.typeID() == 5) {
                if (this.right.constant.booleanValue()) {
                    result = this.left.analyseCode(currentScope, flowContext, flowInfo);
                } else {
                    result = this.left.analyseCode(currentScope, flowContext, flowInfo).asNegatedCondition();
                    this.analyzeLocalVariable(this.right, flowInfo);
                }
            } else {
                result = this.right.analyseCode(currentScope, flowContext, this.left.analyseCode(currentScope, flowContext, flowInfo).unconditionalInits()).unconditionalInits();
            }
        } else if (this.left.constant != Constant.NotAConstant && this.left.constant.typeID() == 5) {
            if (!this.left.constant.booleanValue()) {
                result = this.right.analyseCode(currentScope, flowContext, flowInfo);
                this.analyzeLocalVariable(this.left, flowInfo);
            } else {
                result = this.right.analyseCode(currentScope, flowContext, flowInfo).asNegatedCondition();
            }
        } else if (this.right.constant != Constant.NotAConstant && this.right.constant.typeID() == 5) {
            if (!this.right.constant.booleanValue()) {
                result = this.left.analyseCode(currentScope, flowContext, flowInfo);
                this.analyzeLocalVariable(this.right, flowInfo);
            } else {
                result = this.left.analyseCode(currentScope, flowContext, flowInfo).asNegatedCondition();
            }
        } else {
            result = this.right.analyseCode(currentScope, flowContext, this.left.analyseCode(currentScope, flowContext, flowInfo).unconditionalInits()).unconditionalInits();
        }
        if (result instanceof UnconditionalFlowInfo && (result.tagBits & 3) == 0) {
            result = FlowInfo.conditional(result.copy(), result.copy());
        }
        this.checkNullComparison(currentScope, flowContext, result, result.initsWhenTrue(), result.initsWhenFalse());
        return result;
    }

    public final void computeConstant(TypeBinding leftType, TypeBinding rightType) {
        if (this.left.constant != Constant.NotAConstant && this.right.constant != Constant.NotAConstant) {
            this.constant = Constant.computeConstantOperationEQUAL_EQUAL(this.left.constant, leftType.id, this.right.constant, rightType.id);
            if ((this.bits & 0x3F00) >> 8 == 29) {
                this.constant = BooleanConstant.fromValue(!this.constant.booleanValue());
            }
        } else {
            this.constant = Constant.NotAConstant;
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
        if ((this.left.implicitConversion & 0xF) == 5) {
            this.generateBooleanEqual(currentScope, codeStream, valueRequired);
        } else {
            this.generateNonBooleanEqual(currentScope, codeStream, valueRequired);
        }
        if (valueRequired) {
            codeStream.generateImplicitConversion(this.implicitConversion);
        }
        codeStream.recordPositionsFrom(pc, this.sourceStart);
    }

    @Override
    public void generateOptimizedBoolean(BlockScope currentScope, CodeStream codeStream, BranchLabel trueLabel, BranchLabel falseLabel, boolean valueRequired) {
        if (this.constant != Constant.NotAConstant) {
            super.generateOptimizedBoolean(currentScope, codeStream, trueLabel, falseLabel, valueRequired);
            return;
        }
        if ((this.bits & 0x3F00) >> 8 == 18) {
            if ((this.left.implicitConversion & 0xF) == 5) {
                this.generateOptimizedBooleanEqual(currentScope, codeStream, trueLabel, falseLabel, valueRequired);
            } else {
                this.generateOptimizedNonBooleanEqual(currentScope, codeStream, trueLabel, falseLabel, valueRequired);
            }
        } else if ((this.left.implicitConversion & 0xF) == 5) {
            this.generateOptimizedBooleanEqual(currentScope, codeStream, falseLabel, trueLabel, valueRequired);
        } else {
            this.generateOptimizedNonBooleanEqual(currentScope, codeStream, falseLabel, trueLabel, valueRequired);
        }
    }

    public void generateBooleanEqual(BlockScope currentScope, CodeStream codeStream, boolean valueRequired) {
        boolean isEqualOperator = (this.bits & 0x3F00) >> 8 == 18;
        Constant cst = this.left.optimizedBooleanConstant();
        if (cst != Constant.NotAConstant) {
            Constant rightCst = this.right.optimizedBooleanConstant();
            if (rightCst != Constant.NotAConstant) {
                this.left.generateCode(currentScope, codeStream, false);
                this.right.generateCode(currentScope, codeStream, false);
                if (valueRequired) {
                    boolean leftBool = cst.booleanValue();
                    boolean rightBool = rightCst.booleanValue();
                    if (isEqualOperator) {
                        if (leftBool == rightBool) {
                            codeStream.iconst_1();
                        } else {
                            codeStream.iconst_0();
                        }
                    } else if (leftBool != rightBool) {
                        codeStream.iconst_1();
                    } else {
                        codeStream.iconst_0();
                    }
                }
            } else if (cst.booleanValue() == isEqualOperator) {
                this.left.generateCode(currentScope, codeStream, false);
                this.right.generateCode(currentScope, codeStream, valueRequired);
            } else if (valueRequired) {
                BranchLabel falseLabel = new BranchLabel(codeStream);
                this.left.generateCode(currentScope, codeStream, false);
                this.right.generateOptimizedBoolean(currentScope, codeStream, null, falseLabel, valueRequired);
                codeStream.iconst_0();
                if ((this.bits & 0x10) != 0) {
                    codeStream.generateImplicitConversion(this.implicitConversion);
                    codeStream.generateReturnBytecode(this);
                    falseLabel.place();
                    codeStream.iconst_1();
                } else {
                    BranchLabel endLabel = new BranchLabel(codeStream);
                    codeStream.goto_(endLabel);
                    codeStream.decrStackSize(1);
                    falseLabel.place();
                    codeStream.iconst_1();
                    endLabel.place();
                }
            } else {
                this.left.generateCode(currentScope, codeStream, false);
                this.right.generateCode(currentScope, codeStream, false);
            }
            return;
        }
        cst = this.right.optimizedBooleanConstant();
        if (cst != Constant.NotAConstant) {
            if (cst.booleanValue() == isEqualOperator) {
                this.left.generateCode(currentScope, codeStream, valueRequired);
                this.right.generateCode(currentScope, codeStream, false);
            } else if (valueRequired) {
                BranchLabel falseLabel = new BranchLabel(codeStream);
                this.left.generateOptimizedBoolean(currentScope, codeStream, null, falseLabel, valueRequired);
                this.right.generateCode(currentScope, codeStream, false);
                codeStream.iconst_0();
                if ((this.bits & 0x10) != 0) {
                    codeStream.generateImplicitConversion(this.implicitConversion);
                    codeStream.generateReturnBytecode(this);
                    falseLabel.place();
                    codeStream.iconst_1();
                } else {
                    BranchLabel endLabel = new BranchLabel(codeStream);
                    codeStream.goto_(endLabel);
                    codeStream.decrStackSize(1);
                    falseLabel.place();
                    codeStream.iconst_1();
                    endLabel.place();
                }
            } else {
                this.left.generateCode(currentScope, codeStream, false);
                this.right.generateCode(currentScope, codeStream, false);
            }
            return;
        }
        this.left.generateCode(currentScope, codeStream, valueRequired);
        this.right.generateCode(currentScope, codeStream, valueRequired);
        if (valueRequired) {
            if (isEqualOperator) {
                BranchLabel falseLabel = new BranchLabel(codeStream);
                codeStream.if_icmpne(falseLabel);
                codeStream.iconst_1();
                if ((this.bits & 0x10) != 0) {
                    codeStream.generateImplicitConversion(this.implicitConversion);
                    codeStream.generateReturnBytecode(this);
                    falseLabel.place();
                    codeStream.iconst_0();
                } else {
                    BranchLabel endLabel = new BranchLabel(codeStream);
                    codeStream.goto_(endLabel);
                    codeStream.decrStackSize(1);
                    falseLabel.place();
                    codeStream.iconst_0();
                    endLabel.place();
                }
            } else {
                codeStream.ixor();
            }
        }
    }

    public void generateOptimizedBooleanEqual(BlockScope currentScope, CodeStream codeStream, BranchLabel trueLabel, BranchLabel falseLabel, boolean valueRequired) {
        if (this.left.constant != Constant.NotAConstant) {
            boolean inline = this.left.constant.booleanValue();
            this.right.generateOptimizedBoolean(currentScope, codeStream, inline ? trueLabel : falseLabel, inline ? falseLabel : trueLabel, valueRequired);
            return;
        }
        if (this.right.constant != Constant.NotAConstant) {
            boolean inline = this.right.constant.booleanValue();
            this.left.generateOptimizedBoolean(currentScope, codeStream, inline ? trueLabel : falseLabel, inline ? falseLabel : trueLabel, valueRequired);
            return;
        }
        this.left.generateCode(currentScope, codeStream, valueRequired);
        this.right.generateCode(currentScope, codeStream, valueRequired);
        int pc = codeStream.position;
        if (valueRequired) {
            if (falseLabel == null) {
                if (trueLabel != null) {
                    codeStream.if_icmpeq(trueLabel);
                }
            } else if (trueLabel == null) {
                codeStream.if_icmpne(falseLabel);
            }
        }
        codeStream.recordPositionsFrom(pc, this.sourceEnd);
    }

    public void generateNonBooleanEqual(BlockScope currentScope, CodeStream codeStream, boolean valueRequired) {
        BranchLabel falseLabel;
        boolean isEqualOperator;
        boolean bl = isEqualOperator = (this.bits & 0x3F00) >> 8 == 18;
        if ((this.left.implicitConversion & 0xFF) >> 4 == 10) {
            Constant cst = this.left.constant;
            if (cst != Constant.NotAConstant && cst.intValue() == 0) {
                this.right.generateCode(currentScope, codeStream, valueRequired);
                if (valueRequired) {
                    BranchLabel falseLabel2 = new BranchLabel(codeStream);
                    if (isEqualOperator) {
                        codeStream.ifne(falseLabel2);
                    } else {
                        codeStream.ifeq(falseLabel2);
                    }
                    codeStream.iconst_1();
                    if ((this.bits & 0x10) != 0) {
                        codeStream.generateImplicitConversion(this.implicitConversion);
                        codeStream.generateReturnBytecode(this);
                        falseLabel2.place();
                        codeStream.iconst_0();
                    } else {
                        BranchLabel endLabel = new BranchLabel(codeStream);
                        codeStream.goto_(endLabel);
                        codeStream.decrStackSize(1);
                        falseLabel2.place();
                        codeStream.iconst_0();
                        endLabel.place();
                    }
                }
                return;
            }
            cst = this.right.constant;
            if (cst != Constant.NotAConstant && cst.intValue() == 0) {
                this.left.generateCode(currentScope, codeStream, valueRequired);
                if (valueRequired) {
                    BranchLabel falseLabel3 = new BranchLabel(codeStream);
                    if (isEqualOperator) {
                        codeStream.ifne(falseLabel3);
                    } else {
                        codeStream.ifeq(falseLabel3);
                    }
                    codeStream.iconst_1();
                    if ((this.bits & 0x10) != 0) {
                        codeStream.generateImplicitConversion(this.implicitConversion);
                        codeStream.generateReturnBytecode(this);
                        falseLabel3.place();
                        codeStream.iconst_0();
                    } else {
                        BranchLabel endLabel = new BranchLabel(codeStream);
                        codeStream.goto_(endLabel);
                        codeStream.decrStackSize(1);
                        falseLabel3.place();
                        codeStream.iconst_0();
                        endLabel.place();
                    }
                }
                return;
            }
        }
        if (this.right instanceof NullLiteral) {
            if (this.left instanceof NullLiteral) {
                if (valueRequired) {
                    if (isEqualOperator) {
                        codeStream.iconst_1();
                    } else {
                        codeStream.iconst_0();
                    }
                }
            } else {
                this.left.generateCode(currentScope, codeStream, valueRequired);
                if (valueRequired) {
                    falseLabel = new BranchLabel(codeStream);
                    if (isEqualOperator) {
                        codeStream.ifnonnull(falseLabel);
                    } else {
                        codeStream.ifnull(falseLabel);
                    }
                    codeStream.iconst_1();
                    if ((this.bits & 0x10) != 0) {
                        codeStream.generateImplicitConversion(this.implicitConversion);
                        codeStream.generateReturnBytecode(this);
                        falseLabel.place();
                        codeStream.iconst_0();
                    } else {
                        BranchLabel endLabel = new BranchLabel(codeStream);
                        codeStream.goto_(endLabel);
                        codeStream.decrStackSize(1);
                        falseLabel.place();
                        codeStream.iconst_0();
                        endLabel.place();
                    }
                }
            }
            return;
        }
        if (this.left instanceof NullLiteral) {
            this.right.generateCode(currentScope, codeStream, valueRequired);
            if (valueRequired) {
                falseLabel = new BranchLabel(codeStream);
                if (isEqualOperator) {
                    codeStream.ifnonnull(falseLabel);
                } else {
                    codeStream.ifnull(falseLabel);
                }
                codeStream.iconst_1();
                if ((this.bits & 0x10) != 0) {
                    codeStream.generateImplicitConversion(this.implicitConversion);
                    codeStream.generateReturnBytecode(this);
                    falseLabel.place();
                    codeStream.iconst_0();
                } else {
                    BranchLabel endLabel = new BranchLabel(codeStream);
                    codeStream.goto_(endLabel);
                    codeStream.decrStackSize(1);
                    falseLabel.place();
                    codeStream.iconst_0();
                    endLabel.place();
                }
            }
            return;
        }
        this.left.generateCode(currentScope, codeStream, valueRequired);
        this.right.generateCode(currentScope, codeStream, valueRequired);
        if (valueRequired) {
            falseLabel = new BranchLabel(codeStream);
            if (isEqualOperator) {
                switch ((this.left.implicitConversion & 0xFF) >> 4) {
                    case 10: {
                        codeStream.if_icmpne(falseLabel);
                        break;
                    }
                    case 9: {
                        codeStream.fcmpl();
                        codeStream.ifne(falseLabel);
                        break;
                    }
                    case 7: {
                        codeStream.lcmp();
                        codeStream.ifne(falseLabel);
                        break;
                    }
                    case 8: {
                        codeStream.dcmpl();
                        codeStream.ifne(falseLabel);
                        break;
                    }
                    default: {
                        codeStream.if_acmpne(falseLabel);
                        break;
                    }
                }
            } else {
                switch ((this.left.implicitConversion & 0xFF) >> 4) {
                    case 10: {
                        codeStream.if_icmpeq(falseLabel);
                        break;
                    }
                    case 9: {
                        codeStream.fcmpl();
                        codeStream.ifeq(falseLabel);
                        break;
                    }
                    case 7: {
                        codeStream.lcmp();
                        codeStream.ifeq(falseLabel);
                        break;
                    }
                    case 8: {
                        codeStream.dcmpl();
                        codeStream.ifeq(falseLabel);
                        break;
                    }
                    default: {
                        codeStream.if_acmpeq(falseLabel);
                    }
                }
            }
            codeStream.iconst_1();
            if ((this.bits & 0x10) != 0) {
                codeStream.generateImplicitConversion(this.implicitConversion);
                codeStream.generateReturnBytecode(this);
                falseLabel.place();
                codeStream.iconst_0();
            } else {
                BranchLabel endLabel = new BranchLabel(codeStream);
                codeStream.goto_(endLabel);
                codeStream.decrStackSize(1);
                falseLabel.place();
                codeStream.iconst_0();
                endLabel.place();
            }
        }
    }

    public void generateOptimizedNonBooleanEqual(BlockScope currentScope, CodeStream codeStream, BranchLabel trueLabel, BranchLabel falseLabel, boolean valueRequired) {
        int pc = codeStream.position;
        Constant inline = this.right.constant;
        if (inline != Constant.NotAConstant && (this.left.implicitConversion & 0xFF) >> 4 == 10 && inline.intValue() == 0) {
            this.left.generateCode(currentScope, codeStream, valueRequired);
            if (valueRequired) {
                if (falseLabel == null) {
                    if (trueLabel != null) {
                        codeStream.ifeq(trueLabel);
                    }
                } else if (trueLabel == null) {
                    codeStream.ifne(falseLabel);
                }
            }
            codeStream.recordPositionsFrom(pc, this.sourceStart);
            return;
        }
        inline = this.left.constant;
        if (inline != Constant.NotAConstant && (this.left.implicitConversion & 0xFF) >> 4 == 10 && inline.intValue() == 0) {
            this.right.generateCode(currentScope, codeStream, valueRequired);
            if (valueRequired) {
                if (falseLabel == null) {
                    if (trueLabel != null) {
                        codeStream.ifeq(trueLabel);
                    }
                } else if (trueLabel == null) {
                    codeStream.ifne(falseLabel);
                }
            }
            codeStream.recordPositionsFrom(pc, this.sourceStart);
            return;
        }
        if (this.right instanceof NullLiteral) {
            if (this.left instanceof NullLiteral) {
                if (valueRequired && falseLabel == null && trueLabel != null) {
                    codeStream.goto_(trueLabel);
                }
            } else {
                this.left.generateCode(currentScope, codeStream, valueRequired);
                if (valueRequired) {
                    if (falseLabel == null) {
                        if (trueLabel != null) {
                            codeStream.ifnull(trueLabel);
                        }
                    } else if (trueLabel == null) {
                        codeStream.ifnonnull(falseLabel);
                    }
                }
            }
            codeStream.recordPositionsFrom(pc, this.sourceStart);
            return;
        }
        if (this.left instanceof NullLiteral) {
            this.right.generateCode(currentScope, codeStream, valueRequired);
            if (valueRequired) {
                if (falseLabel == null) {
                    if (trueLabel != null) {
                        codeStream.ifnull(trueLabel);
                    }
                } else if (trueLabel == null) {
                    codeStream.ifnonnull(falseLabel);
                }
            }
            codeStream.recordPositionsFrom(pc, this.sourceStart);
            return;
        }
        this.left.generateCode(currentScope, codeStream, valueRequired);
        this.right.generateCode(currentScope, codeStream, valueRequired);
        if (valueRequired) {
            if (falseLabel == null) {
                if (trueLabel != null) {
                    switch ((this.left.implicitConversion & 0xFF) >> 4) {
                        case 10: {
                            codeStream.if_icmpeq(trueLabel);
                            break;
                        }
                        case 9: {
                            codeStream.fcmpl();
                            codeStream.ifeq(trueLabel);
                            break;
                        }
                        case 7: {
                            codeStream.lcmp();
                            codeStream.ifeq(trueLabel);
                            break;
                        }
                        case 8: {
                            codeStream.dcmpl();
                            codeStream.ifeq(trueLabel);
                            break;
                        }
                        default: {
                            codeStream.if_acmpeq(trueLabel);
                            break;
                        }
                    }
                }
            } else if (trueLabel == null) {
                switch ((this.left.implicitConversion & 0xFF) >> 4) {
                    case 10: {
                        codeStream.if_icmpne(falseLabel);
                        break;
                    }
                    case 9: {
                        codeStream.fcmpl();
                        codeStream.ifne(falseLabel);
                        break;
                    }
                    case 7: {
                        codeStream.lcmp();
                        codeStream.ifne(falseLabel);
                        break;
                    }
                    case 8: {
                        codeStream.dcmpl();
                        codeStream.ifne(falseLabel);
                        break;
                    }
                    default: {
                        codeStream.if_acmpne(falseLabel);
                    }
                }
            }
        }
        codeStream.recordPositionsFrom(pc, this.sourceStart);
    }

    @Override
    public boolean isCompactableOperation() {
        return false;
    }

    @Override
    protected Constant optimizedNullComparisonConstant() {
        int operator = (this.bits & 0x3F00) >> 8;
        if (operator == 18) {
            if (this.left instanceof NullLiteral && this.right instanceof NullLiteral) {
                return BooleanConstant.fromValue(true);
            }
        } else if (operator == 29 && this.left instanceof NullLiteral && this.right instanceof NullLiteral) {
            return BooleanConstant.fromValue(false);
        }
        return Constant.NotAConstant;
    }

    @Override
    public TypeBinding resolveType(BlockScope scope) {
        boolean leftIsCast = this.left instanceof CastExpression;
        if (leftIsCast) {
            this.left.bits |= 0x20;
        }
        TypeBinding originalLeftType = this.left.resolveType(scope);
        boolean rightIsCast = this.right instanceof CastExpression;
        if (rightIsCast) {
            this.right.bits |= 0x20;
        }
        TypeBinding originalRightType = this.right.resolveType(scope);
        if (originalLeftType == null || originalRightType == null) {
            this.constant = Constant.NotAConstant;
            return null;
        }
        CompilerOptions compilerOptions = scope.compilerOptions();
        if (compilerOptions.complainOnUninternedIdentityComparison && originalRightType.hasTypeBit(16) && originalLeftType.hasTypeBit(16)) {
            scope.problemReporter().uninternedIdentityComparison(this, originalLeftType, originalRightType, scope.referenceCompilationUnit());
        }
        boolean use15specifics = compilerOptions.sourceLevel >= 0x310000L;
        TypeBinding leftType = originalLeftType;
        TypeBinding rightType = originalRightType;
        if (use15specifics) {
            if (leftType != TypeBinding.NULL && leftType.isBaseType()) {
                if (!rightType.isBaseType()) {
                    rightType = scope.environment().computeBoxingType(rightType);
                }
            } else if (rightType != TypeBinding.NULL && rightType.isBaseType()) {
                leftType = scope.environment().computeBoxingType(leftType);
            }
        }
        if (leftType.isBaseType() && rightType.isBaseType()) {
            int operator;
            int leftTypeID = leftType.id;
            int rightTypeID = rightType.id;
            int operatorSignature = OperatorSignatures[18][(leftTypeID << 4) + rightTypeID];
            this.left.computeConversion(scope, TypeBinding.wellKnownType(scope, operatorSignature >>> 16 & 0xF), originalLeftType);
            this.right.computeConversion(scope, TypeBinding.wellKnownType(scope, operatorSignature >>> 8 & 0xF), originalRightType);
            this.bits |= operatorSignature & 0xF;
            if ((operatorSignature & 0xF) == 0) {
                this.constant = Constant.NotAConstant;
                scope.problemReporter().invalidOperator(this, leftType, rightType);
                return null;
            }
            if (leftIsCast || rightIsCast) {
                CastExpression.checkNeedForArgumentCasts(scope, 18, operatorSignature, this.left, leftType.id, leftIsCast, this.right, rightType.id, rightIsCast);
            }
            this.computeConstant(leftType, rightType);
            Binding leftDirect = Expression.getDirectBinding(this.left);
            if (leftDirect != null && leftDirect == Expression.getDirectBinding(this.right)) {
                if (leftTypeID != 8 && leftTypeID != 9 && !(this.right instanceof Assignment)) {
                    scope.problemReporter().comparingIdenticalExpressions(this);
                }
            } else if (this.constant != Constant.NotAConstant && ((operator = (this.bits & 0x3F00) >> 8) == 18 && this.constant == BooleanConstant.fromValue(true) || operator == 29 && this.constant == BooleanConstant.fromValue(false))) {
                scope.problemReporter().comparingIdenticalExpressions(this);
            }
            this.resolvedType = TypeBinding.BOOLEAN;
            return this.resolvedType;
        }
        if (!(leftType.isBaseType() && leftType != TypeBinding.NULL || rightType.isBaseType() && rightType != TypeBinding.NULL || !this.checkCastTypesCompatibility(scope, leftType, rightType, null, true) && !this.checkCastTypesCompatibility(scope, rightType, leftType, null, true))) {
            Binding leftDirect;
            boolean unnecessaryRightCast;
            if (rightType.id == 11 && leftType.id == 11) {
                this.computeConstant(leftType, rightType);
            } else {
                this.constant = Constant.NotAConstant;
            }
            ReferenceBinding objectType = scope.getJavaLangObject();
            this.left.computeConversion(scope, objectType, leftType);
            this.right.computeConversion(scope, objectType, rightType);
            boolean unnecessaryLeftCast = (this.left.bits & 0x4000) != 0;
            boolean bl = unnecessaryRightCast = (this.right.bits & 0x4000) != 0;
            if (unnecessaryLeftCast || unnecessaryRightCast) {
                TypeBinding alternateRightType;
                TypeBinding alternateLeftType = unnecessaryLeftCast ? ((CastExpression)this.left).expression.resolvedType : leftType;
                TypeBinding typeBinding = alternateRightType = unnecessaryRightCast ? ((CastExpression)this.right).expression.resolvedType : rightType;
                if (!this.isCastNeeded(alternateLeftType, alternateRightType) && (this.checkCastTypesCompatibility(scope, alternateLeftType, alternateRightType, null, false) || this.checkCastTypesCompatibility(scope, alternateRightType, alternateLeftType, null, false))) {
                    if (unnecessaryLeftCast) {
                        scope.problemReporter().unnecessaryCast((CastExpression)this.left);
                    }
                    if (unnecessaryRightCast) {
                        scope.problemReporter().unnecessaryCast((CastExpression)this.right);
                    }
                }
            }
            if ((leftDirect = Expression.getDirectBinding(this.left)) != null && leftDirect == Expression.getDirectBinding(this.right) && !(this.right instanceof Assignment)) {
                scope.problemReporter().comparingIdenticalExpressions(this);
            }
            this.resolvedType = TypeBinding.BOOLEAN;
            return this.resolvedType;
        }
        this.constant = Constant.NotAConstant;
        scope.problemReporter().notCompatibleTypesError(this, leftType, rightType);
        return null;
    }

    private boolean isCastNeeded(TypeBinding leftType, TypeBinding rightType) {
        if (leftType.isParameterizedType()) {
            return rightType.isBaseType();
        }
        if (rightType.isParameterizedType()) {
            return leftType.isBaseType();
        }
        return false;
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

