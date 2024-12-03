/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.ast.Assignment;
import org.eclipse.jdt.internal.compiler.ast.CastExpression;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.OperatorExpression;
import org.eclipse.jdt.internal.compiler.ast.OperatorIds;
import org.eclipse.jdt.internal.compiler.ast.Reference;
import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
import org.eclipse.jdt.internal.compiler.flow.FlowContext;
import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;

public class CompoundAssignment
extends Assignment
implements OperatorIds {
    public int operator;
    public int preAssignImplicitConversion;

    public CompoundAssignment(Expression lhs, Expression expression, int operator, int sourceEnd) {
        super(lhs, expression, sourceEnd);
        lhs.bits &= 0xFFFFDFFF;
        lhs.bits |= 0x10000;
        this.operator = operator;
    }

    @Override
    public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo) {
        LocalVariableBinding local;
        if (this.resolvedType.id != 11) {
            this.lhs.checkNPE(currentScope, flowContext, flowInfo);
            flowContext.recordAbruptExit();
        }
        this.expression.checkNPEbyUnboxing(currentScope, flowContext, flowInfo);
        flowInfo = ((Reference)this.lhs).analyseAssignment(currentScope, flowContext, flowInfo, this, true).unconditionalInits();
        if (this.resolvedType.id == 11 && (local = this.lhs.localVariableBinding()) != null) {
            flowInfo.markAsDefinitelyNonNull(local);
            flowContext.markFinallyNullStatus(local, 4);
        }
        return flowInfo;
    }

    public boolean checkCastCompatibility() {
        return true;
    }

    @Override
    public void generateCode(BlockScope currentScope, CodeStream codeStream, boolean valueRequired) {
        int pc = codeStream.position;
        ((Reference)this.lhs).generateCompoundAssignment(currentScope, codeStream, this.expression, this.operator, this.preAssignImplicitConversion, valueRequired);
        if (valueRequired) {
            codeStream.generateImplicitConversion(this.implicitConversion);
        }
        codeStream.recordPositionsFrom(pc, this.sourceStart);
    }

    @Override
    public int nullStatus(FlowInfo flowInfo, FlowContext flowContext) {
        return 4;
    }

    public String operatorToString() {
        switch (this.operator) {
            case 14: {
                return "+=";
            }
            case 13: {
                return "-=";
            }
            case 15: {
                return "*=";
            }
            case 9: {
                return "/=";
            }
            case 2: {
                return "&=";
            }
            case 3: {
                return "|=";
            }
            case 8: {
                return "^=";
            }
            case 16: {
                return "%=";
            }
            case 10: {
                return "<<=";
            }
            case 17: {
                return ">>=";
            }
            case 19: {
                return ">>>=";
            }
        }
        return "unknown operator";
    }

    @Override
    public StringBuffer printExpressionNoParenthesis(int indent, StringBuffer output) {
        this.lhs.printExpression(indent, output).append(' ').append(this.operatorToString()).append(' ');
        return this.expression.printExpression(0, output);
    }

    @Override
    public TypeBinding resolveType(BlockScope scope) {
        int result;
        this.constant = Constant.NotAConstant;
        if (!(this.lhs instanceof Reference) || this.lhs.isThis()) {
            scope.problemReporter().expressionShouldBeAVariable(this.lhs);
            return null;
        }
        boolean expressionIsCast = this.expression instanceof CastExpression;
        if (expressionIsCast) {
            this.expression.bits |= 0x20;
        }
        TypeBinding originalLhsType = this.lhs.resolveType(scope);
        TypeBinding originalExpressionType = this.expression.resolveType(scope);
        if (originalLhsType == null || originalExpressionType == null) {
            return null;
        }
        LookupEnvironment env = scope.environment();
        TypeBinding lhsType = originalLhsType;
        TypeBinding expressionType = originalExpressionType;
        boolean use15specifics = scope.compilerOptions().sourceLevel >= 0x310000L;
        boolean unboxedLhs = false;
        if (use15specifics) {
            TypeBinding unboxedType;
            if (!lhsType.isBaseType() && expressionType.id != 11 && expressionType.id != 12 && TypeBinding.notEquals(unboxedType = env.computeBoxingType(lhsType), lhsType)) {
                lhsType = unboxedType;
                unboxedLhs = true;
            }
            if (!expressionType.isBaseType() && lhsType.id != 11 && lhsType.id != 12) {
                expressionType = env.computeBoxingType(expressionType);
            }
        }
        if (this.restrainUsageToNumericTypes() && !lhsType.isNumericType()) {
            scope.problemReporter().operatorOnlyValidOnNumericType(this, lhsType, expressionType);
            return null;
        }
        int lhsID = lhsType.id;
        int expressionID = expressionType.id;
        if (lhsID > 15 || expressionID > 15) {
            if (lhsID != 11) {
                scope.problemReporter().invalidOperator(this, lhsType, expressionType);
                return null;
            }
            expressionID = 1;
        }
        if ((result = OperatorExpression.OperatorSignatures[this.operator][(lhsID << 4) + expressionID]) == 0) {
            scope.problemReporter().invalidOperator(this, lhsType, expressionType);
            return null;
        }
        if (this.operator == 14) {
            if (lhsID == 1 && scope.compilerOptions().complianceLevel < 0x330000L) {
                scope.problemReporter().invalidOperator(this, lhsType, expressionType);
                return null;
            }
            if ((lhsType.isNumericType() || lhsID == 5) && !expressionType.isNumericType()) {
                scope.problemReporter().invalidOperator(this, lhsType, expressionType);
                return null;
            }
        }
        TypeBinding resultType = TypeBinding.wellKnownType(scope, result & 0xF);
        if (this.checkCastCompatibility() && originalLhsType.id != 11 && resultType.id != 11 && !this.checkCastTypesCompatibility(scope, originalLhsType, resultType, null, true)) {
            scope.problemReporter().invalidOperator(this, originalLhsType, expressionType);
            return null;
        }
        this.lhs.computeConversion(scope, TypeBinding.wellKnownType(scope, result >>> 16 & 0xF), originalLhsType);
        this.expression.computeConversion(scope, TypeBinding.wellKnownType(scope, result >>> 8 & 0xF), originalExpressionType);
        this.preAssignImplicitConversion = (unboxedLhs ? 512 : 0) | lhsID << 4 | result & 0xF;
        if (unboxedLhs) {
            scope.problemReporter().autoboxing(this, lhsType, originalLhsType);
        }
        if (expressionIsCast) {
            CastExpression.checkNeedForArgumentCasts(scope, this.operator, result, this.lhs, originalLhsType.id, false, this.expression, originalExpressionType.id, true);
        }
        this.resolvedType = originalLhsType;
        return this.resolvedType;
    }

    public boolean restrainUsageToNumericTypes() {
        return false;
    }

    @Override
    public void traverse(ASTVisitor visitor, BlockScope scope) {
        if (visitor.visit(this, scope)) {
            this.lhs.traverse(visitor, scope);
            this.expression.traverse(visitor, scope);
        }
        visitor.endVisit(this, scope);
    }
}

