/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.ast.CastExpression;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.LocalDeclaration;
import org.eclipse.jdt.internal.compiler.ast.OperatorExpression;
import org.eclipse.jdt.internal.compiler.codegen.BranchLabel;
import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
import org.eclipse.jdt.internal.compiler.flow.FlowContext;
import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
import org.eclipse.jdt.internal.compiler.impl.BooleanConstant;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;

public class UnaryExpression
extends OperatorExpression {
    public Expression expression;
    public Constant optimizedBooleanConstant;

    public UnaryExpression(Expression expression, int operator) {
        this.expression = expression;
        this.bits |= operator << 8;
    }

    @Override
    public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo) {
        if ((this.bits & 0x3F00) >> 8 == 11) {
            flowContext.tagBits ^= 4;
            flowInfo = this.expression.analyseCode(currentScope, flowContext, flowInfo).asNegatedCondition();
            flowContext.tagBits ^= 4;
        } else {
            flowInfo = this.expression.analyseCode(currentScope, flowContext, flowInfo);
        }
        this.expression.checkNPE(currentScope, flowContext, flowInfo);
        return flowInfo;
    }

    @Override
    protected void updateFlowOnBooleanResult(FlowInfo flowInfo, boolean result) {
        if ((this.bits & 0x3F00) >> 8 == 11) {
            this.expression.updateFlowOnBooleanResult(flowInfo, !result);
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
            case 11: {
                switch ((this.expression.implicitConversion & 0xFF) >> 4) {
                    case 5: {
                        BranchLabel falseLabel = new BranchLabel(codeStream);
                        this.expression.generateOptimizedBoolean(currentScope, codeStream, null, falseLabel, valueRequired);
                        if (valueRequired) {
                            codeStream.iconst_0();
                            if (falseLabel.forwardReferenceCount() <= 0) break;
                            BranchLabel endifLabel = new BranchLabel(codeStream);
                            codeStream.goto_(endifLabel);
                            codeStream.decrStackSize(1);
                            falseLabel.place();
                            codeStream.iconst_1();
                            endifLabel.place();
                            break;
                        }
                        falseLabel.place();
                    }
                }
                break;
            }
            case 12: {
                switch ((this.expression.implicitConversion & 0xFF) >> 4) {
                    case 10: {
                        this.expression.generateCode(currentScope, codeStream, valueRequired);
                        if (!valueRequired) break;
                        codeStream.iconst_m1();
                        codeStream.ixor();
                        break;
                    }
                    case 7: {
                        this.expression.generateCode(currentScope, codeStream, valueRequired);
                        if (!valueRequired) break;
                        codeStream.ldc2_w(-1L);
                        codeStream.lxor();
                    }
                }
                break;
            }
            case 13: {
                if (this.constant != Constant.NotAConstant) {
                    if (!valueRequired) break;
                    switch ((this.expression.implicitConversion & 0xFF) >> 4) {
                        case 10: {
                            codeStream.generateInlinedValue(this.constant.intValue() * -1);
                            break;
                        }
                        case 9: {
                            codeStream.generateInlinedValue(this.constant.floatValue() * -1.0f);
                            break;
                        }
                        case 7: {
                            codeStream.generateInlinedValue(this.constant.longValue() * -1L);
                            break;
                        }
                        case 8: {
                            codeStream.generateInlinedValue(this.constant.doubleValue() * -1.0);
                        }
                    }
                    break;
                }
                this.expression.generateCode(currentScope, codeStream, valueRequired);
                if (!valueRequired) break;
                switch ((this.expression.implicitConversion & 0xFF) >> 4) {
                    case 10: {
                        codeStream.ineg();
                        break;
                    }
                    case 9: {
                        codeStream.fneg();
                        break;
                    }
                    case 7: {
                        codeStream.lneg();
                        break;
                    }
                    case 8: {
                        codeStream.dneg();
                    }
                }
                break;
            }
            case 14: {
                this.expression.generateCode(currentScope, codeStream, valueRequired);
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
        if ((this.bits & 0x3F00) >> 8 == 11) {
            this.expression.generateOptimizedBoolean(currentScope, codeStream, falseLabel, trueLabel, valueRequired);
        } else {
            super.generateOptimizedBoolean(currentScope, codeStream, trueLabel, falseLabel, valueRequired);
        }
    }

    @Override
    public StringBuffer printExpressionNoParenthesis(int indent, StringBuffer output) {
        output.append(this.operatorToString()).append(' ');
        return this.expression.printExpression(0, output);
    }

    @Override
    public void collectPatternVariablesToScope(LocalVariableBinding[] variables, BlockScope scope) {
        this.expression.collectPatternVariablesToScope(variables, scope);
        if ((this.bits & 0x3F00) >> 8 == 11) {
            variables = this.expression.getPatternVariablesWhenTrue();
            if (variables != null) {
                this.addPatternVariablesWhenFalse(variables);
            }
            if ((variables = this.expression.getPatternVariablesWhenFalse()) != null) {
                this.addPatternVariablesWhenTrue(variables);
            }
        } else {
            variables = this.expression.getPatternVariablesWhenTrue();
            this.addPatternVariablesWhenTrue(variables);
            variables = this.expression.getPatternVariablesWhenFalse();
            this.addPatternVariablesWhenFalse(variables);
        }
    }

    @Override
    public TypeBinding resolveType(BlockScope scope) {
        int tableId;
        boolean use15specifics;
        TypeBinding expressionType;
        boolean expressionIsCast = this.expression instanceof CastExpression;
        if (expressionIsCast) {
            this.expression.bits |= 0x20;
        }
        if ((expressionType = this.expression.resolveType(scope)) == null) {
            this.constant = Constant.NotAConstant;
            return null;
        }
        int expressionTypeID = expressionType.id;
        boolean bl = use15specifics = scope.compilerOptions().sourceLevel >= 0x310000L;
        if (use15specifics && !expressionType.isBaseType()) {
            expressionTypeID = scope.environment().computeBoxingType((TypeBinding)expressionType).id;
        }
        if (expressionTypeID > 15) {
            this.constant = Constant.NotAConstant;
            scope.problemReporter().invalidOperator(this, expressionType);
            return null;
        }
        switch ((this.bits & 0x3F00) >> 8) {
            case 11: {
                tableId = 0;
                break;
            }
            case 12: {
                tableId = 10;
                break;
            }
            default: {
                tableId = 13;
            }
        }
        int operatorSignature = OperatorSignatures[tableId][(expressionTypeID << 4) + expressionTypeID];
        this.expression.computeConversion(scope, TypeBinding.wellKnownType(scope, operatorSignature >>> 16 & 0xF), expressionType);
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
            default: {
                this.constant = Constant.NotAConstant;
                if (expressionTypeID != 0) {
                    scope.problemReporter().invalidOperator(this, expressionType);
                }
                return null;
            }
        }
        if (this.expression.constant != Constant.NotAConstant) {
            this.constant = Constant.computeConstantOperation(this.expression.constant, expressionTypeID, (this.bits & 0x3F00) >> 8);
        } else {
            Constant cst;
            this.constant = Constant.NotAConstant;
            if ((this.bits & 0x3F00) >> 8 == 11 && (cst = this.expression.optimizedBooleanConstant()) != Constant.NotAConstant) {
                this.optimizedBooleanConstant = BooleanConstant.fromValue(!cst.booleanValue());
            }
        }
        if (expressionIsCast) {
            CastExpression.checkNeedForArgumentCast(scope, tableId, operatorSignature, this.expression, expressionTypeID);
        }
        return this.resolvedType;
    }

    @Override
    public boolean containsPatternVariable() {
        return this.expression.containsPatternVariable();
    }

    @Override
    protected LocalDeclaration getPatternVariableIntroduced() {
        return this.expression.getPatternVariableIntroduced();
    }

    @Override
    public void traverse(ASTVisitor visitor, BlockScope blockScope) {
        if (visitor.visit(this, blockScope)) {
            this.expression.traverse(visitor, blockScope);
        }
        visitor.endVisit(this, blockScope);
    }
}

