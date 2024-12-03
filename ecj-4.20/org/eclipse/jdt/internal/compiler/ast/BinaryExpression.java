/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.ast.CastExpression;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.OperatorExpression;
import org.eclipse.jdt.internal.compiler.codegen.BranchLabel;
import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
import org.eclipse.jdt.internal.compiler.flow.FlowContext;
import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
import org.eclipse.jdt.internal.compiler.flow.UnconditionalFlowInfo;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.lookup.ArrayBinding;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;

public class BinaryExpression
extends OperatorExpression {
    public Expression left;
    public Expression right;
    public Constant optimizedBooleanConstant;

    public BinaryExpression(Expression left, Expression right, int operator) {
        this.left = left;
        this.right = right;
        this.bits |= operator << 8;
        this.sourceStart = left.sourceStart;
        this.sourceEnd = right.sourceEnd;
    }

    public BinaryExpression(BinaryExpression expression) {
        this.left = expression.left;
        this.right = expression.right;
        this.bits = expression.bits;
        this.sourceStart = expression.sourceStart;
        this.sourceEnd = expression.sourceEnd;
    }

    @Override
    public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo) {
        try {
            if (this.resolvedType.id == 11) {
                UnconditionalFlowInfo unconditionalFlowInfo = this.right.analyseCode(currentScope, flowContext, this.left.analyseCode(currentScope, flowContext, flowInfo).unconditionalInits()).unconditionalInits();
                return unconditionalFlowInfo;
            }
            flowInfo = this.left.analyseCode(currentScope, flowContext, flowInfo).unconditionalInits();
            this.left.checkNPE(currentScope, flowContext, flowInfo);
            if ((this.bits & 0x3F00) >> 8 != 2) {
                flowContext.expireNullCheckedFieldInfo();
            }
            flowInfo = this.right.analyseCode(currentScope, flowContext, flowInfo).unconditionalInits();
            this.right.checkNPE(currentScope, flowContext, flowInfo);
            if ((this.bits & 0x3F00) >> 8 != 2) {
                flowContext.expireNullCheckedFieldInfo();
            }
            FlowInfo flowInfo2 = flowInfo;
            return flowInfo2;
        }
        finally {
            flowContext.recordAbruptExit();
        }
    }

    @Override
    protected void updateFlowOnBooleanResult(FlowInfo flowInfo, boolean result) {
        int operator = (this.bits & 0x3F00) >> 8;
        if (result ? operator == 0 : operator == 1) {
            this.left.updateFlowOnBooleanResult(flowInfo, result);
            this.right.updateFlowOnBooleanResult(flowInfo, result);
        }
    }

    public void computeConstant(BlockScope scope, int leftId, int rightId) {
        if (this.left.constant != Constant.NotAConstant && this.right.constant != Constant.NotAConstant) {
            try {
                this.constant = Constant.computeConstantOperation(this.left.constant, leftId, (this.bits & 0x3F00) >> 8, this.right.constant, rightId);
            }
            catch (ArithmeticException arithmeticException) {
                this.constant = Constant.NotAConstant;
            }
        } else {
            this.constant = Constant.NotAConstant;
            this.optimizedBooleanConstant(leftId, (this.bits & 0x3F00) >> 8, rightId);
        }
    }

    @Override
    public Constant optimizedBooleanConstant() {
        return this.optimizedBooleanConstant == null ? this.constant : this.optimizedBooleanConstant;
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
        switch ((this.bits & 0x3F00) >> 8) {
            case 14: {
                switch (this.bits & 0xF) {
                    case 11: {
                        codeStream.generateStringConcatenationAppend(currentScope, this.left, this.right);
                        if (valueRequired) break;
                        codeStream.pop();
                        break;
                    }
                    case 10: {
                        this.left.generateCode(currentScope, codeStream, valueRequired);
                        this.right.generateCode(currentScope, codeStream, valueRequired);
                        if (!valueRequired) break;
                        codeStream.iadd();
                        break;
                    }
                    case 7: {
                        this.left.generateCode(currentScope, codeStream, valueRequired);
                        this.right.generateCode(currentScope, codeStream, valueRequired);
                        if (!valueRequired) break;
                        codeStream.ladd();
                        break;
                    }
                    case 8: {
                        this.left.generateCode(currentScope, codeStream, valueRequired);
                        this.right.generateCode(currentScope, codeStream, valueRequired);
                        if (!valueRequired) break;
                        codeStream.dadd();
                        break;
                    }
                    case 9: {
                        this.left.generateCode(currentScope, codeStream, valueRequired);
                        this.right.generateCode(currentScope, codeStream, valueRequired);
                        if (!valueRequired) break;
                        codeStream.fadd();
                    }
                }
                break;
            }
            case 13: {
                switch (this.bits & 0xF) {
                    case 10: {
                        this.left.generateCode(currentScope, codeStream, valueRequired);
                        this.right.generateCode(currentScope, codeStream, valueRequired);
                        if (!valueRequired) break;
                        codeStream.isub();
                        break;
                    }
                    case 7: {
                        this.left.generateCode(currentScope, codeStream, valueRequired);
                        this.right.generateCode(currentScope, codeStream, valueRequired);
                        if (!valueRequired) break;
                        codeStream.lsub();
                        break;
                    }
                    case 8: {
                        this.left.generateCode(currentScope, codeStream, valueRequired);
                        this.right.generateCode(currentScope, codeStream, valueRequired);
                        if (!valueRequired) break;
                        codeStream.dsub();
                        break;
                    }
                    case 9: {
                        this.left.generateCode(currentScope, codeStream, valueRequired);
                        this.right.generateCode(currentScope, codeStream, valueRequired);
                        if (!valueRequired) break;
                        codeStream.fsub();
                    }
                }
                break;
            }
            case 15: {
                switch (this.bits & 0xF) {
                    case 10: {
                        this.left.generateCode(currentScope, codeStream, valueRequired);
                        this.right.generateCode(currentScope, codeStream, valueRequired);
                        if (!valueRequired) break;
                        codeStream.imul();
                        break;
                    }
                    case 7: {
                        this.left.generateCode(currentScope, codeStream, valueRequired);
                        this.right.generateCode(currentScope, codeStream, valueRequired);
                        if (!valueRequired) break;
                        codeStream.lmul();
                        break;
                    }
                    case 8: {
                        this.left.generateCode(currentScope, codeStream, valueRequired);
                        this.right.generateCode(currentScope, codeStream, valueRequired);
                        if (!valueRequired) break;
                        codeStream.dmul();
                        break;
                    }
                    case 9: {
                        this.left.generateCode(currentScope, codeStream, valueRequired);
                        this.right.generateCode(currentScope, codeStream, valueRequired);
                        if (!valueRequired) break;
                        codeStream.fmul();
                    }
                }
                break;
            }
            case 9: {
                switch (this.bits & 0xF) {
                    case 10: {
                        this.left.generateCode(currentScope, codeStream, true);
                        this.right.generateCode(currentScope, codeStream, true);
                        codeStream.idiv();
                        if (valueRequired) break;
                        codeStream.pop();
                        break;
                    }
                    case 7: {
                        this.left.generateCode(currentScope, codeStream, true);
                        this.right.generateCode(currentScope, codeStream, true);
                        codeStream.ldiv();
                        if (valueRequired) break;
                        codeStream.pop2();
                        break;
                    }
                    case 8: {
                        this.left.generateCode(currentScope, codeStream, valueRequired);
                        this.right.generateCode(currentScope, codeStream, valueRequired);
                        if (!valueRequired) break;
                        codeStream.ddiv();
                        break;
                    }
                    case 9: {
                        this.left.generateCode(currentScope, codeStream, valueRequired);
                        this.right.generateCode(currentScope, codeStream, valueRequired);
                        if (!valueRequired) break;
                        codeStream.fdiv();
                    }
                }
                break;
            }
            case 16: {
                switch (this.bits & 0xF) {
                    case 10: {
                        this.left.generateCode(currentScope, codeStream, true);
                        this.right.generateCode(currentScope, codeStream, true);
                        codeStream.irem();
                        if (valueRequired) break;
                        codeStream.pop();
                        break;
                    }
                    case 7: {
                        this.left.generateCode(currentScope, codeStream, true);
                        this.right.generateCode(currentScope, codeStream, true);
                        codeStream.lrem();
                        if (valueRequired) break;
                        codeStream.pop2();
                        break;
                    }
                    case 8: {
                        this.left.generateCode(currentScope, codeStream, valueRequired);
                        this.right.generateCode(currentScope, codeStream, valueRequired);
                        if (!valueRequired) break;
                        codeStream.drem();
                        break;
                    }
                    case 9: {
                        this.left.generateCode(currentScope, codeStream, valueRequired);
                        this.right.generateCode(currentScope, codeStream, valueRequired);
                        if (!valueRequired) break;
                        codeStream.frem();
                    }
                }
                break;
            }
            case 2: {
                switch (this.bits & 0xF) {
                    case 10: {
                        if (this.left.constant != Constant.NotAConstant && this.left.constant.typeID() == 10 && this.left.constant.intValue() == 0) {
                            this.right.generateCode(currentScope, codeStream, false);
                            if (!valueRequired) break;
                            codeStream.iconst_0();
                            break;
                        }
                        if (this.right.constant != Constant.NotAConstant && this.right.constant.typeID() == 10 && this.right.constant.intValue() == 0) {
                            this.left.generateCode(currentScope, codeStream, false);
                            if (!valueRequired) break;
                            codeStream.iconst_0();
                            break;
                        }
                        this.left.generateCode(currentScope, codeStream, valueRequired);
                        this.right.generateCode(currentScope, codeStream, valueRequired);
                        if (!valueRequired) break;
                        codeStream.iand();
                        break;
                    }
                    case 7: {
                        if (this.left.constant != Constant.NotAConstant && this.left.constant.typeID() == 7 && this.left.constant.longValue() == 0L) {
                            this.right.generateCode(currentScope, codeStream, false);
                            if (!valueRequired) break;
                            codeStream.lconst_0();
                            break;
                        }
                        if (this.right.constant != Constant.NotAConstant && this.right.constant.typeID() == 7 && this.right.constant.longValue() == 0L) {
                            this.left.generateCode(currentScope, codeStream, false);
                            if (!valueRequired) break;
                            codeStream.lconst_0();
                            break;
                        }
                        this.left.generateCode(currentScope, codeStream, valueRequired);
                        this.right.generateCode(currentScope, codeStream, valueRequired);
                        if (!valueRequired) break;
                        codeStream.land();
                        break;
                    }
                    case 5: {
                        this.generateLogicalAnd(currentScope, codeStream, valueRequired);
                    }
                }
                break;
            }
            case 3: {
                switch (this.bits & 0xF) {
                    case 10: {
                        if (this.left.constant != Constant.NotAConstant && this.left.constant.typeID() == 10 && this.left.constant.intValue() == 0) {
                            this.right.generateCode(currentScope, codeStream, valueRequired);
                            break;
                        }
                        if (this.right.constant != Constant.NotAConstant && this.right.constant.typeID() == 10 && this.right.constant.intValue() == 0) {
                            this.left.generateCode(currentScope, codeStream, valueRequired);
                            break;
                        }
                        this.left.generateCode(currentScope, codeStream, valueRequired);
                        this.right.generateCode(currentScope, codeStream, valueRequired);
                        if (!valueRequired) break;
                        codeStream.ior();
                        break;
                    }
                    case 7: {
                        if (this.left.constant != Constant.NotAConstant && this.left.constant.typeID() == 7 && this.left.constant.longValue() == 0L) {
                            this.right.generateCode(currentScope, codeStream, valueRequired);
                            break;
                        }
                        if (this.right.constant != Constant.NotAConstant && this.right.constant.typeID() == 7 && this.right.constant.longValue() == 0L) {
                            this.left.generateCode(currentScope, codeStream, valueRequired);
                            break;
                        }
                        this.left.generateCode(currentScope, codeStream, valueRequired);
                        this.right.generateCode(currentScope, codeStream, valueRequired);
                        if (!valueRequired) break;
                        codeStream.lor();
                        break;
                    }
                    case 5: {
                        this.generateLogicalOr(currentScope, codeStream, valueRequired);
                    }
                }
                break;
            }
            case 8: {
                switch (this.bits & 0xF) {
                    case 10: {
                        if (this.left.constant != Constant.NotAConstant && this.left.constant.typeID() == 10 && this.left.constant.intValue() == 0) {
                            this.right.generateCode(currentScope, codeStream, valueRequired);
                            break;
                        }
                        if (this.right.constant != Constant.NotAConstant && this.right.constant.typeID() == 10 && this.right.constant.intValue() == 0) {
                            this.left.generateCode(currentScope, codeStream, valueRequired);
                            break;
                        }
                        this.left.generateCode(currentScope, codeStream, valueRequired);
                        this.right.generateCode(currentScope, codeStream, valueRequired);
                        if (!valueRequired) break;
                        codeStream.ixor();
                        break;
                    }
                    case 7: {
                        if (this.left.constant != Constant.NotAConstant && this.left.constant.typeID() == 7 && this.left.constant.longValue() == 0L) {
                            this.right.generateCode(currentScope, codeStream, valueRequired);
                            break;
                        }
                        if (this.right.constant != Constant.NotAConstant && this.right.constant.typeID() == 7 && this.right.constant.longValue() == 0L) {
                            this.left.generateCode(currentScope, codeStream, valueRequired);
                            break;
                        }
                        this.left.generateCode(currentScope, codeStream, valueRequired);
                        this.right.generateCode(currentScope, codeStream, valueRequired);
                        if (!valueRequired) break;
                        codeStream.lxor();
                        break;
                    }
                    case 5: {
                        this.generateLogicalXor(currentScope, codeStream, valueRequired);
                    }
                }
                break;
            }
            case 10: {
                switch (this.bits & 0xF) {
                    case 10: {
                        this.left.generateCode(currentScope, codeStream, valueRequired);
                        this.right.generateCode(currentScope, codeStream, valueRequired);
                        if (!valueRequired) break;
                        codeStream.ishl();
                        break;
                    }
                    case 7: {
                        this.left.generateCode(currentScope, codeStream, valueRequired);
                        this.right.generateCode(currentScope, codeStream, valueRequired);
                        if (!valueRequired) break;
                        codeStream.lshl();
                    }
                }
                break;
            }
            case 17: {
                switch (this.bits & 0xF) {
                    case 10: {
                        this.left.generateCode(currentScope, codeStream, valueRequired);
                        this.right.generateCode(currentScope, codeStream, valueRequired);
                        if (!valueRequired) break;
                        codeStream.ishr();
                        break;
                    }
                    case 7: {
                        this.left.generateCode(currentScope, codeStream, valueRequired);
                        this.right.generateCode(currentScope, codeStream, valueRequired);
                        if (!valueRequired) break;
                        codeStream.lshr();
                    }
                }
                break;
            }
            case 19: {
                switch (this.bits & 0xF) {
                    case 10: {
                        this.left.generateCode(currentScope, codeStream, valueRequired);
                        this.right.generateCode(currentScope, codeStream, valueRequired);
                        if (!valueRequired) break;
                        codeStream.iushr();
                        break;
                    }
                    case 7: {
                        this.left.generateCode(currentScope, codeStream, valueRequired);
                        this.right.generateCode(currentScope, codeStream, valueRequired);
                        if (!valueRequired) break;
                        codeStream.lushr();
                    }
                }
                break;
            }
            case 6: {
                BranchLabel falseLabel = new BranchLabel(codeStream);
                this.generateOptimizedGreaterThan(currentScope, codeStream, null, falseLabel, valueRequired);
                if (!valueRequired) break;
                codeStream.iconst_1();
                if ((this.bits & 0x10) != 0) {
                    codeStream.generateImplicitConversion(this.implicitConversion);
                    codeStream.generateReturnBytecode(this);
                    falseLabel.place();
                    codeStream.iconst_0();
                    break;
                }
                BranchLabel endLabel = new BranchLabel(codeStream);
                codeStream.goto_(endLabel);
                codeStream.decrStackSize(1);
                falseLabel.place();
                codeStream.iconst_0();
                endLabel.place();
                break;
            }
            case 7: {
                BranchLabel falseLabel = new BranchLabel(codeStream);
                this.generateOptimizedGreaterThanOrEqual(currentScope, codeStream, null, falseLabel, valueRequired);
                if (!valueRequired) break;
                codeStream.iconst_1();
                if ((this.bits & 0x10) != 0) {
                    codeStream.generateImplicitConversion(this.implicitConversion);
                    codeStream.generateReturnBytecode(this);
                    falseLabel.place();
                    codeStream.iconst_0();
                    break;
                }
                BranchLabel endLabel = new BranchLabel(codeStream);
                codeStream.goto_(endLabel);
                codeStream.decrStackSize(1);
                falseLabel.place();
                codeStream.iconst_0();
                endLabel.place();
                break;
            }
            case 4: {
                BranchLabel falseLabel = new BranchLabel(codeStream);
                this.generateOptimizedLessThan(currentScope, codeStream, null, falseLabel, valueRequired);
                if (!valueRequired) break;
                codeStream.iconst_1();
                if ((this.bits & 0x10) != 0) {
                    codeStream.generateImplicitConversion(this.implicitConversion);
                    codeStream.generateReturnBytecode(this);
                    falseLabel.place();
                    codeStream.iconst_0();
                    break;
                }
                BranchLabel endLabel = new BranchLabel(codeStream);
                codeStream.goto_(endLabel);
                codeStream.decrStackSize(1);
                falseLabel.place();
                codeStream.iconst_0();
                endLabel.place();
                break;
            }
            case 5: {
                BranchLabel falseLabel = new BranchLabel(codeStream);
                this.generateOptimizedLessThanOrEqual(currentScope, codeStream, null, falseLabel, valueRequired);
                if (!valueRequired) break;
                codeStream.iconst_1();
                if ((this.bits & 0x10) != 0) {
                    codeStream.generateImplicitConversion(this.implicitConversion);
                    codeStream.generateReturnBytecode(this);
                    falseLabel.place();
                    codeStream.iconst_0();
                    break;
                }
                BranchLabel endLabel = new BranchLabel(codeStream);
                codeStream.goto_(endLabel);
                codeStream.decrStackSize(1);
                falseLabel.place();
                codeStream.iconst_0();
                endLabel.place();
            }
        }
        if (valueRequired) {
            codeStream.generateImplicitConversion(this.implicitConversion);
        }
        codeStream.recordPositionsFrom(pc, this.sourceStart);
    }

    @Override
    public void generateOptimizedBoolean(BlockScope currentScope, CodeStream codeStream, BranchLabel trueLabel, BranchLabel falseLabel, boolean valueRequired) {
        if (this.constant != Constant.NotAConstant && this.constant.typeID() == 5) {
            super.generateOptimizedBoolean(currentScope, codeStream, trueLabel, falseLabel, valueRequired);
            return;
        }
        switch ((this.bits & 0x3F00) >> 8) {
            case 4: {
                this.generateOptimizedLessThan(currentScope, codeStream, trueLabel, falseLabel, valueRequired);
                return;
            }
            case 5: {
                this.generateOptimizedLessThanOrEqual(currentScope, codeStream, trueLabel, falseLabel, valueRequired);
                return;
            }
            case 6: {
                this.generateOptimizedGreaterThan(currentScope, codeStream, trueLabel, falseLabel, valueRequired);
                return;
            }
            case 7: {
                this.generateOptimizedGreaterThanOrEqual(currentScope, codeStream, trueLabel, falseLabel, valueRequired);
                return;
            }
            case 2: {
                this.generateOptimizedLogicalAnd(currentScope, codeStream, trueLabel, falseLabel, valueRequired);
                return;
            }
            case 3: {
                this.generateOptimizedLogicalOr(currentScope, codeStream, trueLabel, falseLabel, valueRequired);
                return;
            }
            case 8: {
                this.generateOptimizedLogicalXor(currentScope, codeStream, trueLabel, falseLabel, valueRequired);
                return;
            }
        }
        super.generateOptimizedBoolean(currentScope, codeStream, trueLabel, falseLabel, valueRequired);
    }

    public void generateOptimizedGreaterThan(BlockScope currentScope, CodeStream codeStream, BranchLabel trueLabel, BranchLabel falseLabel, boolean valueRequired) {
        int promotedTypeID = (this.left.implicitConversion & 0xFF) >> 4;
        if (promotedTypeID == 10) {
            if (this.left.constant != Constant.NotAConstant && this.left.constant.intValue() == 0) {
                this.right.generateCode(currentScope, codeStream, valueRequired);
                if (valueRequired) {
                    if (falseLabel == null) {
                        if (trueLabel != null) {
                            codeStream.iflt(trueLabel);
                        }
                    } else if (trueLabel == null) {
                        codeStream.ifge(falseLabel);
                    }
                }
                codeStream.recordPositionsFrom(codeStream.position, this.sourceEnd);
                return;
            }
            if (this.right.constant != Constant.NotAConstant && this.right.constant.intValue() == 0) {
                this.left.generateCode(currentScope, codeStream, valueRequired);
                if (valueRequired) {
                    if (falseLabel == null) {
                        if (trueLabel != null) {
                            codeStream.ifgt(trueLabel);
                        }
                    } else if (trueLabel == null) {
                        codeStream.ifle(falseLabel);
                    }
                }
                codeStream.recordPositionsFrom(codeStream.position, this.sourceEnd);
                return;
            }
        }
        this.left.generateCode(currentScope, codeStream, valueRequired);
        this.right.generateCode(currentScope, codeStream, valueRequired);
        if (valueRequired) {
            if (falseLabel == null) {
                if (trueLabel != null) {
                    switch (promotedTypeID) {
                        case 10: {
                            codeStream.if_icmpgt(trueLabel);
                            break;
                        }
                        case 9: {
                            codeStream.fcmpl();
                            codeStream.ifgt(trueLabel);
                            break;
                        }
                        case 7: {
                            codeStream.lcmp();
                            codeStream.ifgt(trueLabel);
                            break;
                        }
                        case 8: {
                            codeStream.dcmpl();
                            codeStream.ifgt(trueLabel);
                        }
                    }
                    codeStream.recordPositionsFrom(codeStream.position, this.sourceEnd);
                    return;
                }
            } else if (trueLabel == null) {
                switch (promotedTypeID) {
                    case 10: {
                        codeStream.if_icmple(falseLabel);
                        break;
                    }
                    case 9: {
                        codeStream.fcmpl();
                        codeStream.ifle(falseLabel);
                        break;
                    }
                    case 7: {
                        codeStream.lcmp();
                        codeStream.ifle(falseLabel);
                        break;
                    }
                    case 8: {
                        codeStream.dcmpl();
                        codeStream.ifle(falseLabel);
                    }
                }
                codeStream.recordPositionsFrom(codeStream.position, this.sourceEnd);
                return;
            }
        }
    }

    public void generateOptimizedGreaterThanOrEqual(BlockScope currentScope, CodeStream codeStream, BranchLabel trueLabel, BranchLabel falseLabel, boolean valueRequired) {
        int promotedTypeID = (this.left.implicitConversion & 0xFF) >> 4;
        if (promotedTypeID == 10) {
            if (this.left.constant != Constant.NotAConstant && this.left.constant.intValue() == 0) {
                this.right.generateCode(currentScope, codeStream, valueRequired);
                if (valueRequired) {
                    if (falseLabel == null) {
                        if (trueLabel != null) {
                            codeStream.ifle(trueLabel);
                        }
                    } else if (trueLabel == null) {
                        codeStream.ifgt(falseLabel);
                    }
                }
                codeStream.recordPositionsFrom(codeStream.position, this.sourceEnd);
                return;
            }
            if (this.right.constant != Constant.NotAConstant && this.right.constant.intValue() == 0) {
                this.left.generateCode(currentScope, codeStream, valueRequired);
                if (valueRequired) {
                    if (falseLabel == null) {
                        if (trueLabel != null) {
                            codeStream.ifge(trueLabel);
                        }
                    } else if (trueLabel == null) {
                        codeStream.iflt(falseLabel);
                    }
                }
                codeStream.recordPositionsFrom(codeStream.position, this.sourceEnd);
                return;
            }
        }
        this.left.generateCode(currentScope, codeStream, valueRequired);
        this.right.generateCode(currentScope, codeStream, valueRequired);
        if (valueRequired) {
            if (falseLabel == null) {
                if (trueLabel != null) {
                    switch (promotedTypeID) {
                        case 10: {
                            codeStream.if_icmpge(trueLabel);
                            break;
                        }
                        case 9: {
                            codeStream.fcmpl();
                            codeStream.ifge(trueLabel);
                            break;
                        }
                        case 7: {
                            codeStream.lcmp();
                            codeStream.ifge(trueLabel);
                            break;
                        }
                        case 8: {
                            codeStream.dcmpl();
                            codeStream.ifge(trueLabel);
                        }
                    }
                    codeStream.recordPositionsFrom(codeStream.position, this.sourceEnd);
                    return;
                }
            } else if (trueLabel == null) {
                switch (promotedTypeID) {
                    case 10: {
                        codeStream.if_icmplt(falseLabel);
                        break;
                    }
                    case 9: {
                        codeStream.fcmpl();
                        codeStream.iflt(falseLabel);
                        break;
                    }
                    case 7: {
                        codeStream.lcmp();
                        codeStream.iflt(falseLabel);
                        break;
                    }
                    case 8: {
                        codeStream.dcmpl();
                        codeStream.iflt(falseLabel);
                    }
                }
                codeStream.recordPositionsFrom(codeStream.position, this.sourceEnd);
                return;
            }
        }
    }

    public void generateOptimizedLessThan(BlockScope currentScope, CodeStream codeStream, BranchLabel trueLabel, BranchLabel falseLabel, boolean valueRequired) {
        int promotedTypeID = (this.left.implicitConversion & 0xFF) >> 4;
        if (promotedTypeID == 10) {
            if (this.left.constant != Constant.NotAConstant && this.left.constant.intValue() == 0) {
                this.right.generateCode(currentScope, codeStream, valueRequired);
                if (valueRequired) {
                    if (falseLabel == null) {
                        if (trueLabel != null) {
                            codeStream.ifgt(trueLabel);
                        }
                    } else if (trueLabel == null) {
                        codeStream.ifle(falseLabel);
                    }
                }
                codeStream.recordPositionsFrom(codeStream.position, this.sourceEnd);
                return;
            }
            if (this.right.constant != Constant.NotAConstant && this.right.constant.intValue() == 0) {
                this.left.generateCode(currentScope, codeStream, valueRequired);
                if (valueRequired) {
                    if (falseLabel == null) {
                        if (trueLabel != null) {
                            codeStream.iflt(trueLabel);
                        }
                    } else if (trueLabel == null) {
                        codeStream.ifge(falseLabel);
                    }
                }
                codeStream.recordPositionsFrom(codeStream.position, this.sourceEnd);
                return;
            }
        }
        this.left.generateCode(currentScope, codeStream, valueRequired);
        this.right.generateCode(currentScope, codeStream, valueRequired);
        if (valueRequired) {
            if (falseLabel == null) {
                if (trueLabel != null) {
                    switch (promotedTypeID) {
                        case 10: {
                            codeStream.if_icmplt(trueLabel);
                            break;
                        }
                        case 9: {
                            codeStream.fcmpg();
                            codeStream.iflt(trueLabel);
                            break;
                        }
                        case 7: {
                            codeStream.lcmp();
                            codeStream.iflt(trueLabel);
                            break;
                        }
                        case 8: {
                            codeStream.dcmpg();
                            codeStream.iflt(trueLabel);
                        }
                    }
                    codeStream.recordPositionsFrom(codeStream.position, this.sourceEnd);
                    return;
                }
            } else if (trueLabel == null) {
                switch (promotedTypeID) {
                    case 10: {
                        codeStream.if_icmpge(falseLabel);
                        break;
                    }
                    case 9: {
                        codeStream.fcmpg();
                        codeStream.ifge(falseLabel);
                        break;
                    }
                    case 7: {
                        codeStream.lcmp();
                        codeStream.ifge(falseLabel);
                        break;
                    }
                    case 8: {
                        codeStream.dcmpg();
                        codeStream.ifge(falseLabel);
                    }
                }
                codeStream.recordPositionsFrom(codeStream.position, this.sourceEnd);
                return;
            }
        }
    }

    public void generateOptimizedLessThanOrEqual(BlockScope currentScope, CodeStream codeStream, BranchLabel trueLabel, BranchLabel falseLabel, boolean valueRequired) {
        int promotedTypeID = (this.left.implicitConversion & 0xFF) >> 4;
        if (promotedTypeID == 10) {
            if (this.left.constant != Constant.NotAConstant && this.left.constant.intValue() == 0) {
                this.right.generateCode(currentScope, codeStream, valueRequired);
                if (valueRequired) {
                    if (falseLabel == null) {
                        if (trueLabel != null) {
                            codeStream.ifge(trueLabel);
                        }
                    } else if (trueLabel == null) {
                        codeStream.iflt(falseLabel);
                    }
                }
                codeStream.recordPositionsFrom(codeStream.position, this.sourceEnd);
                return;
            }
            if (this.right.constant != Constant.NotAConstant && this.right.constant.intValue() == 0) {
                this.left.generateCode(currentScope, codeStream, valueRequired);
                if (valueRequired) {
                    if (falseLabel == null) {
                        if (trueLabel != null) {
                            codeStream.ifle(trueLabel);
                        }
                    } else if (trueLabel == null) {
                        codeStream.ifgt(falseLabel);
                    }
                }
                codeStream.recordPositionsFrom(codeStream.position, this.sourceEnd);
                return;
            }
        }
        this.left.generateCode(currentScope, codeStream, valueRequired);
        this.right.generateCode(currentScope, codeStream, valueRequired);
        if (valueRequired) {
            if (falseLabel == null) {
                if (trueLabel != null) {
                    switch (promotedTypeID) {
                        case 10: {
                            codeStream.if_icmple(trueLabel);
                            break;
                        }
                        case 9: {
                            codeStream.fcmpg();
                            codeStream.ifle(trueLabel);
                            break;
                        }
                        case 7: {
                            codeStream.lcmp();
                            codeStream.ifle(trueLabel);
                            break;
                        }
                        case 8: {
                            codeStream.dcmpg();
                            codeStream.ifle(trueLabel);
                        }
                    }
                    codeStream.recordPositionsFrom(codeStream.position, this.sourceEnd);
                    return;
                }
            } else if (trueLabel == null) {
                switch (promotedTypeID) {
                    case 10: {
                        codeStream.if_icmpgt(falseLabel);
                        break;
                    }
                    case 9: {
                        codeStream.fcmpg();
                        codeStream.ifgt(falseLabel);
                        break;
                    }
                    case 7: {
                        codeStream.lcmp();
                        codeStream.ifgt(falseLabel);
                        break;
                    }
                    case 8: {
                        codeStream.dcmpg();
                        codeStream.ifgt(falseLabel);
                    }
                }
                codeStream.recordPositionsFrom(codeStream.position, this.sourceEnd);
                return;
            }
        }
    }

    public void generateLogicalAnd(BlockScope currentScope, CodeStream codeStream, boolean valueRequired) {
        if ((this.left.implicitConversion & 0xF) == 5) {
            Constant condConst = this.left.optimizedBooleanConstant();
            if (condConst != Constant.NotAConstant) {
                if (condConst.booleanValue()) {
                    this.left.generateCode(currentScope, codeStream, false);
                    this.right.generateCode(currentScope, codeStream, valueRequired);
                } else {
                    this.left.generateCode(currentScope, codeStream, false);
                    this.right.generateCode(currentScope, codeStream, false);
                    if (valueRequired) {
                        codeStream.iconst_0();
                    }
                    codeStream.recordPositionsFrom(codeStream.position, this.sourceEnd);
                }
                return;
            }
            condConst = this.right.optimizedBooleanConstant();
            if (condConst != Constant.NotAConstant) {
                if (condConst.booleanValue()) {
                    this.left.generateCode(currentScope, codeStream, valueRequired);
                    this.right.generateCode(currentScope, codeStream, false);
                } else {
                    this.left.generateCode(currentScope, codeStream, false);
                    this.right.generateCode(currentScope, codeStream, false);
                    if (valueRequired) {
                        codeStream.iconst_0();
                    }
                    codeStream.recordPositionsFrom(codeStream.position, this.sourceEnd);
                }
                return;
            }
        }
        this.left.generateCode(currentScope, codeStream, valueRequired);
        this.right.generateCode(currentScope, codeStream, valueRequired);
        if (valueRequired) {
            codeStream.iand();
        }
        codeStream.recordPositionsFrom(codeStream.position, this.sourceEnd);
    }

    public void generateLogicalOr(BlockScope currentScope, CodeStream codeStream, boolean valueRequired) {
        if ((this.left.implicitConversion & 0xF) == 5) {
            Constant condConst = this.left.optimizedBooleanConstant();
            if (condConst != Constant.NotAConstant) {
                if (condConst.booleanValue()) {
                    this.left.generateCode(currentScope, codeStream, false);
                    this.right.generateCode(currentScope, codeStream, false);
                    if (valueRequired) {
                        codeStream.iconst_1();
                    }
                    codeStream.recordPositionsFrom(codeStream.position, this.sourceEnd);
                } else {
                    this.left.generateCode(currentScope, codeStream, false);
                    this.right.generateCode(currentScope, codeStream, valueRequired);
                }
                return;
            }
            condConst = this.right.optimizedBooleanConstant();
            if (condConst != Constant.NotAConstant) {
                if (condConst.booleanValue()) {
                    this.left.generateCode(currentScope, codeStream, false);
                    this.right.generateCode(currentScope, codeStream, false);
                    if (valueRequired) {
                        codeStream.iconst_1();
                    }
                    codeStream.recordPositionsFrom(codeStream.position, this.sourceEnd);
                } else {
                    this.left.generateCode(currentScope, codeStream, valueRequired);
                    this.right.generateCode(currentScope, codeStream, false);
                }
                return;
            }
        }
        this.left.generateCode(currentScope, codeStream, valueRequired);
        this.right.generateCode(currentScope, codeStream, valueRequired);
        if (valueRequired) {
            codeStream.ior();
        }
        codeStream.recordPositionsFrom(codeStream.position, this.sourceEnd);
    }

    public void generateLogicalXor(BlockScope currentScope, CodeStream codeStream, boolean valueRequired) {
        if ((this.left.implicitConversion & 0xF) == 5) {
            Constant condConst = this.left.optimizedBooleanConstant();
            if (condConst != Constant.NotAConstant) {
                if (condConst.booleanValue()) {
                    this.left.generateCode(currentScope, codeStream, false);
                    if (valueRequired) {
                        codeStream.iconst_1();
                    }
                    this.right.generateCode(currentScope, codeStream, valueRequired);
                    if (valueRequired) {
                        codeStream.ixor();
                        codeStream.recordPositionsFrom(codeStream.position, this.sourceEnd);
                    }
                } else {
                    this.left.generateCode(currentScope, codeStream, false);
                    this.right.generateCode(currentScope, codeStream, valueRequired);
                }
                return;
            }
            condConst = this.right.optimizedBooleanConstant();
            if (condConst != Constant.NotAConstant) {
                if (condConst.booleanValue()) {
                    this.left.generateCode(currentScope, codeStream, valueRequired);
                    this.right.generateCode(currentScope, codeStream, false);
                    if (valueRequired) {
                        codeStream.iconst_1();
                        codeStream.ixor();
                        codeStream.recordPositionsFrom(codeStream.position, this.sourceEnd);
                    }
                } else {
                    this.left.generateCode(currentScope, codeStream, valueRequired);
                    this.right.generateCode(currentScope, codeStream, false);
                }
                return;
            }
        }
        this.left.generateCode(currentScope, codeStream, valueRequired);
        this.right.generateCode(currentScope, codeStream, valueRequired);
        if (valueRequired) {
            codeStream.ixor();
        }
        codeStream.recordPositionsFrom(codeStream.position, this.sourceEnd);
    }

    public void generateOptimizedLogicalAnd(BlockScope currentScope, CodeStream codeStream, BranchLabel trueLabel, BranchLabel falseLabel, boolean valueRequired) {
        if ((this.left.implicitConversion & 0xF) == 5) {
            Constant condConst = this.left.optimizedBooleanConstant();
            if (condConst != Constant.NotAConstant) {
                if (condConst.booleanValue()) {
                    this.left.generateOptimizedBoolean(currentScope, codeStream, trueLabel, falseLabel, false);
                    this.right.generateOptimizedBoolean(currentScope, codeStream, trueLabel, falseLabel, valueRequired);
                } else {
                    this.left.generateOptimizedBoolean(currentScope, codeStream, trueLabel, falseLabel, false);
                    this.right.generateOptimizedBoolean(currentScope, codeStream, trueLabel, falseLabel, false);
                    if (valueRequired && falseLabel != null) {
                        codeStream.goto_(falseLabel);
                    }
                    codeStream.recordPositionsFrom(codeStream.position, this.sourceEnd);
                }
                return;
            }
            condConst = this.right.optimizedBooleanConstant();
            if (condConst != Constant.NotAConstant) {
                if (condConst.booleanValue()) {
                    this.left.generateOptimizedBoolean(currentScope, codeStream, trueLabel, falseLabel, valueRequired);
                    this.right.generateOptimizedBoolean(currentScope, codeStream, trueLabel, falseLabel, false);
                } else {
                    BranchLabel internalTrueLabel = new BranchLabel(codeStream);
                    this.left.generateOptimizedBoolean(currentScope, codeStream, internalTrueLabel, falseLabel, false);
                    internalTrueLabel.place();
                    this.right.generateOptimizedBoolean(currentScope, codeStream, trueLabel, falseLabel, false);
                    if (valueRequired && falseLabel != null) {
                        codeStream.goto_(falseLabel);
                    }
                    codeStream.recordPositionsFrom(codeStream.position, this.sourceEnd);
                }
                return;
            }
        }
        this.left.generateCode(currentScope, codeStream, valueRequired);
        this.right.generateCode(currentScope, codeStream, valueRequired);
        if (valueRequired) {
            codeStream.iand();
            if (falseLabel == null) {
                if (trueLabel != null) {
                    codeStream.ifne(trueLabel);
                }
            } else if (trueLabel == null) {
                codeStream.ifeq(falseLabel);
            }
        }
        codeStream.recordPositionsFrom(codeStream.position, this.sourceEnd);
    }

    public void generateOptimizedLogicalOr(BlockScope currentScope, CodeStream codeStream, BranchLabel trueLabel, BranchLabel falseLabel, boolean valueRequired) {
        if ((this.left.implicitConversion & 0xF) == 5) {
            Constant condConst = this.left.optimizedBooleanConstant();
            if (condConst != Constant.NotAConstant) {
                if (condConst.booleanValue()) {
                    this.left.generateOptimizedBoolean(currentScope, codeStream, trueLabel, falseLabel, false);
                    BranchLabel internalFalseLabel = new BranchLabel(codeStream);
                    this.right.generateOptimizedBoolean(currentScope, codeStream, trueLabel, internalFalseLabel, false);
                    internalFalseLabel.place();
                    if (valueRequired && trueLabel != null) {
                        codeStream.goto_(trueLabel);
                    }
                    codeStream.recordPositionsFrom(codeStream.position, this.sourceEnd);
                } else {
                    this.left.generateOptimizedBoolean(currentScope, codeStream, trueLabel, falseLabel, false);
                    this.right.generateOptimizedBoolean(currentScope, codeStream, trueLabel, falseLabel, valueRequired);
                }
                return;
            }
            condConst = this.right.optimizedBooleanConstant();
            if (condConst != Constant.NotAConstant) {
                if (condConst.booleanValue()) {
                    BranchLabel internalFalseLabel = new BranchLabel(codeStream);
                    this.left.generateOptimizedBoolean(currentScope, codeStream, trueLabel, internalFalseLabel, false);
                    internalFalseLabel.place();
                    this.right.generateOptimizedBoolean(currentScope, codeStream, trueLabel, falseLabel, false);
                    if (valueRequired && trueLabel != null) {
                        codeStream.goto_(trueLabel);
                    }
                    codeStream.recordPositionsFrom(codeStream.position, this.sourceEnd);
                } else {
                    this.left.generateOptimizedBoolean(currentScope, codeStream, trueLabel, falseLabel, valueRequired);
                    this.right.generateOptimizedBoolean(currentScope, codeStream, trueLabel, falseLabel, false);
                }
                return;
            }
        }
        this.left.generateCode(currentScope, codeStream, valueRequired);
        this.right.generateCode(currentScope, codeStream, valueRequired);
        if (valueRequired) {
            codeStream.ior();
            if (falseLabel == null) {
                if (trueLabel != null) {
                    codeStream.ifne(trueLabel);
                }
            } else if (trueLabel == null) {
                codeStream.ifeq(falseLabel);
            }
        }
        codeStream.recordPositionsFrom(codeStream.position, this.sourceEnd);
    }

    public void generateOptimizedLogicalXor(BlockScope currentScope, CodeStream codeStream, BranchLabel trueLabel, BranchLabel falseLabel, boolean valueRequired) {
        if ((this.left.implicitConversion & 0xF) == 5) {
            Constant condConst = this.left.optimizedBooleanConstant();
            if (condConst != Constant.NotAConstant) {
                if (condConst.booleanValue()) {
                    this.left.generateOptimizedBoolean(currentScope, codeStream, trueLabel, falseLabel, false);
                    this.right.generateOptimizedBoolean(currentScope, codeStream, falseLabel, trueLabel, valueRequired);
                } else {
                    this.left.generateOptimizedBoolean(currentScope, codeStream, trueLabel, falseLabel, false);
                    this.right.generateOptimizedBoolean(currentScope, codeStream, trueLabel, falseLabel, valueRequired);
                }
                return;
            }
            condConst = this.right.optimizedBooleanConstant();
            if (condConst != Constant.NotAConstant) {
                if (condConst.booleanValue()) {
                    this.left.generateOptimizedBoolean(currentScope, codeStream, falseLabel, trueLabel, valueRequired);
                    this.right.generateOptimizedBoolean(currentScope, codeStream, trueLabel, falseLabel, false);
                } else {
                    this.left.generateOptimizedBoolean(currentScope, codeStream, trueLabel, falseLabel, valueRequired);
                    this.right.generateOptimizedBoolean(currentScope, codeStream, trueLabel, falseLabel, false);
                }
                return;
            }
        }
        this.left.generateCode(currentScope, codeStream, valueRequired);
        this.right.generateCode(currentScope, codeStream, valueRequired);
        if (valueRequired) {
            codeStream.ixor();
            if (falseLabel == null) {
                if (trueLabel != null) {
                    codeStream.ifne(trueLabel);
                }
            } else if (trueLabel == null) {
                codeStream.ifeq(falseLabel);
            }
        }
        codeStream.recordPositionsFrom(codeStream.position, this.sourceEnd);
    }

    @Override
    public void generateOptimizedStringConcatenation(BlockScope blockScope, CodeStream codeStream, int typeID) {
        if ((this.bits & 0x3F00) >> 8 == 14 && (this.bits & 0xF) == 11) {
            if (this.constant != Constant.NotAConstant) {
                codeStream.generateConstant(this.constant, this.implicitConversion);
                codeStream.invokeStringConcatenationAppendForType(this.implicitConversion & 0xF);
            } else {
                int pc = codeStream.position;
                this.left.generateOptimizedStringConcatenation(blockScope, codeStream, this.left.implicitConversion & 0xF);
                codeStream.recordPositionsFrom(pc, this.left.sourceStart);
                pc = codeStream.position;
                this.right.generateOptimizedStringConcatenation(blockScope, codeStream, this.right.implicitConversion & 0xF);
                codeStream.recordPositionsFrom(pc, this.right.sourceStart);
            }
        } else {
            super.generateOptimizedStringConcatenation(blockScope, codeStream, typeID);
        }
    }

    @Override
    public void generateOptimizedStringConcatenationCreation(BlockScope blockScope, CodeStream codeStream, int typeID) {
        if ((this.bits & 0x3F00) >> 8 == 14 && (this.bits & 0xF) == 11) {
            if (this.constant != Constant.NotAConstant) {
                codeStream.newStringContatenation();
                codeStream.dup();
                codeStream.ldc(this.constant.stringValue());
                codeStream.invokeStringConcatenationStringConstructor();
            } else {
                int pc = codeStream.position;
                this.left.generateOptimizedStringConcatenationCreation(blockScope, codeStream, this.left.implicitConversion & 0xF);
                codeStream.recordPositionsFrom(pc, this.left.sourceStart);
                pc = codeStream.position;
                this.right.generateOptimizedStringConcatenation(blockScope, codeStream, this.right.implicitConversion & 0xF);
                codeStream.recordPositionsFrom(pc, this.right.sourceStart);
            }
        } else {
            super.generateOptimizedStringConcatenationCreation(blockScope, codeStream, typeID);
        }
    }

    @Override
    public boolean isCompactableOperation() {
        return true;
    }

    void nonRecursiveResolveTypeUpwards(BlockScope scope) {
        boolean use15specifics;
        TypeBinding leftType = this.left.resolvedType;
        boolean rightIsCast = this.right instanceof CastExpression;
        if (rightIsCast) {
            this.right.bits |= 0x20;
        }
        TypeBinding rightType = this.right.resolveType(scope);
        if (leftType == null || rightType == null) {
            this.constant = Constant.NotAConstant;
            return;
        }
        int leftTypeID = leftType.id;
        int rightTypeID = rightType.id;
        boolean bl = use15specifics = scope.compilerOptions().sourceLevel >= 0x310000L;
        if (use15specifics) {
            if (!leftType.isBaseType() && rightTypeID != 11 && rightTypeID != 12) {
                leftTypeID = scope.environment().computeBoxingType((TypeBinding)leftType).id;
            }
            if (!rightType.isBaseType() && leftTypeID != 11 && leftTypeID != 12) {
                rightTypeID = scope.environment().computeBoxingType((TypeBinding)rightType).id;
            }
        }
        if (leftTypeID > 15 || rightTypeID > 15) {
            if (leftTypeID == 11) {
                rightTypeID = 1;
            } else if (rightTypeID == 11) {
                leftTypeID = 1;
            } else {
                this.constant = Constant.NotAConstant;
                scope.problemReporter().invalidOperator(this, leftType, rightType);
                return;
            }
        }
        if ((this.bits & 0x3F00) >> 8 == 14) {
            if (leftTypeID == 11) {
                this.left.computeConversion(scope, leftType, leftType);
                if (rightType.isArrayType() && TypeBinding.equalsEquals(((ArrayBinding)rightType).elementsType(), TypeBinding.CHAR)) {
                    scope.problemReporter().signalNoImplicitStringConversionForCharArrayExpression(this.right);
                }
            }
            if (rightTypeID == 11) {
                this.right.computeConversion(scope, rightType, rightType);
                if (leftType.isArrayType() && TypeBinding.equalsEquals(((ArrayBinding)leftType).elementsType(), TypeBinding.CHAR)) {
                    scope.problemReporter().signalNoImplicitStringConversionForCharArrayExpression(this.left);
                }
            }
        }
        int operator = (this.bits & 0x3F00) >> 8;
        int operatorSignature = OperatorExpression.OperatorSignatures[operator][(leftTypeID << 4) + rightTypeID];
        this.left.computeConversion(scope, TypeBinding.wellKnownType(scope, operatorSignature >>> 16 & 0xF), leftType);
        this.right.computeConversion(scope, TypeBinding.wellKnownType(scope, operatorSignature >>> 8 & 0xF), rightType);
        this.bits |= operatorSignature & 0xF;
        switch (operatorSignature & 0xF) {
            case 5: {
                this.resolvedType = TypeBinding.BOOLEAN;
                break;
            }
            case 3: {
                this.resolvedType = TypeBinding.BYTE;
                break;
            }
            case 2: {
                this.resolvedType = TypeBinding.CHAR;
                break;
            }
            case 8: {
                this.resolvedType = TypeBinding.DOUBLE;
                break;
            }
            case 9: {
                this.resolvedType = TypeBinding.FLOAT;
                break;
            }
            case 10: {
                this.resolvedType = TypeBinding.INT;
                break;
            }
            case 7: {
                this.resolvedType = TypeBinding.LONG;
                break;
            }
            case 11: {
                this.resolvedType = scope.getJavaLangString();
                break;
            }
            default: {
                this.constant = Constant.NotAConstant;
                scope.problemReporter().invalidOperator(this, leftType, rightType);
                return;
            }
        }
        boolean leftIsCast = this.left instanceof CastExpression;
        if (leftIsCast || rightIsCast) {
            CastExpression.checkNeedForArgumentCasts(scope, operator, operatorSignature, this.left, leftTypeID, leftIsCast, this.right, rightTypeID, rightIsCast);
        }
        this.computeConstant(scope, leftTypeID, rightTypeID);
    }

    public void optimizedBooleanConstant(int leftId, int operator, int rightId) {
        switch (operator) {
            case 2: {
                if (leftId != 5 || rightId != 5) {
                    return;
                }
            }
            case 0: {
                Constant cst = this.left.optimizedBooleanConstant();
                if (cst != Constant.NotAConstant) {
                    if (!cst.booleanValue()) {
                        this.optimizedBooleanConstant = cst;
                        return;
                    }
                    cst = this.right.optimizedBooleanConstant();
                    if (cst != Constant.NotAConstant) {
                        this.optimizedBooleanConstant = cst;
                    }
                    return;
                }
                cst = this.right.optimizedBooleanConstant();
                if (cst != Constant.NotAConstant && !cst.booleanValue()) {
                    this.optimizedBooleanConstant = cst;
                }
                return;
            }
            case 3: {
                if (leftId != 5 || rightId != 5) {
                    return;
                }
            }
            case 1: {
                Constant cst = this.left.optimizedBooleanConstant();
                if (cst != Constant.NotAConstant) {
                    if (cst.booleanValue()) {
                        this.optimizedBooleanConstant = cst;
                        return;
                    }
                    cst = this.right.optimizedBooleanConstant();
                    if (cst != Constant.NotAConstant) {
                        this.optimizedBooleanConstant = cst;
                    }
                    return;
                }
                cst = this.right.optimizedBooleanConstant();
                if (cst == Constant.NotAConstant || !cst.booleanValue()) break;
                this.optimizedBooleanConstant = cst;
            }
        }
    }

    @Override
    public StringBuffer printExpressionNoParenthesis(int indent, StringBuffer output) {
        this.left.printExpression(indent, output).append(' ').append(this.operatorToString()).append(' ');
        return this.right.printExpression(0, output);
    }

    @Override
    public void addPatternVariables(BlockScope scope, CodeStream codeStream) {
        this.left.addPatternVariables(scope, codeStream);
        this.right.addPatternVariables(scope, codeStream);
    }

    @Override
    public boolean containsPatternVariable() {
        return this.left.containsPatternVariable() || this.right.containsPatternVariable();
    }

    @Override
    public TypeBinding resolveType(BlockScope scope) {
        boolean use15specifics;
        boolean leftIsCast;
        if (this.patternVarsWhenFalse == null && this.patternVarsWhenTrue == null && this.containsPatternVariable()) {
            this.collectPatternVariablesToScope(null, scope);
        }
        if (leftIsCast = this.left instanceof CastExpression) {
            this.left.bits |= 0x20;
        }
        TypeBinding leftType = this.left.resolveType(scope);
        boolean rightIsCast = this.right instanceof CastExpression;
        if (rightIsCast) {
            this.right.bits |= 0x20;
        }
        TypeBinding rightType = this.right.resolveType(scope);
        if (leftType == null || rightType == null) {
            this.constant = Constant.NotAConstant;
            return null;
        }
        int leftTypeID = leftType.id;
        int rightTypeID = rightType.id;
        boolean bl = use15specifics = scope.compilerOptions().sourceLevel >= 0x310000L;
        if (use15specifics) {
            if (!leftType.isBaseType() && rightTypeID != 11 && rightTypeID != 12) {
                leftTypeID = scope.environment().computeBoxingType((TypeBinding)leftType).id;
            }
            if (!rightType.isBaseType() && leftTypeID != 11 && leftTypeID != 12) {
                rightTypeID = scope.environment().computeBoxingType((TypeBinding)rightType).id;
            }
        }
        if (leftTypeID > 15 || rightTypeID > 15) {
            if (leftTypeID == 11) {
                rightTypeID = 1;
            } else if (rightTypeID == 11) {
                leftTypeID = 1;
            } else {
                this.constant = Constant.NotAConstant;
                scope.problemReporter().invalidOperator(this, leftType, rightType);
                return null;
            }
        }
        if ((this.bits & 0x3F00) >> 8 == 14) {
            if (leftTypeID == 11) {
                this.left.computeConversion(scope, leftType, leftType);
                if (rightType.isArrayType() && TypeBinding.equalsEquals(((ArrayBinding)rightType).elementsType(), TypeBinding.CHAR)) {
                    scope.problemReporter().signalNoImplicitStringConversionForCharArrayExpression(this.right);
                }
            }
            if (rightTypeID == 11) {
                this.right.computeConversion(scope, rightType, rightType);
                if (leftType.isArrayType() && TypeBinding.equalsEquals(((ArrayBinding)leftType).elementsType(), TypeBinding.CHAR)) {
                    scope.problemReporter().signalNoImplicitStringConversionForCharArrayExpression(this.left);
                }
            }
        }
        int operator = (this.bits & 0x3F00) >> 8;
        int operatorSignature = OperatorExpression.OperatorSignatures[operator][(leftTypeID << 4) + rightTypeID];
        this.left.computeConversion(scope, TypeBinding.wellKnownType(scope, operatorSignature >>> 16 & 0xF), leftType);
        this.right.computeConversion(scope, TypeBinding.wellKnownType(scope, operatorSignature >>> 8 & 0xF), rightType);
        this.bits |= operatorSignature & 0xF;
        switch (operatorSignature & 0xF) {
            case 5: {
                this.resolvedType = TypeBinding.BOOLEAN;
                break;
            }
            case 3: {
                this.resolvedType = TypeBinding.BYTE;
                break;
            }
            case 2: {
                this.resolvedType = TypeBinding.CHAR;
                break;
            }
            case 8: {
                this.resolvedType = TypeBinding.DOUBLE;
                break;
            }
            case 9: {
                this.resolvedType = TypeBinding.FLOAT;
                break;
            }
            case 10: {
                this.resolvedType = TypeBinding.INT;
                break;
            }
            case 7: {
                this.resolvedType = TypeBinding.LONG;
                break;
            }
            case 11: {
                this.resolvedType = scope.getJavaLangString();
                break;
            }
            default: {
                this.constant = Constant.NotAConstant;
                scope.problemReporter().invalidOperator(this, leftType, rightType);
                return null;
            }
        }
        if (leftIsCast || rightIsCast) {
            CastExpression.checkNeedForArgumentCasts(scope, operator, operatorSignature, this.left, leftTypeID, leftIsCast, this.right, rightTypeID, rightIsCast);
        }
        this.computeConstant(scope, leftTypeID, rightTypeID);
        return this.resolvedType;
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

