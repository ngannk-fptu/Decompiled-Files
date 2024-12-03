/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.ast;

import java.util.ArrayList;
import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.NameReference;
import org.eclipse.jdt.internal.compiler.ast.QualifiedNameReference;
import org.eclipse.jdt.internal.compiler.ast.SingleNameReference;
import org.eclipse.jdt.internal.compiler.ast.Statement;
import org.eclipse.jdt.internal.compiler.ast.SwitchStatement;
import org.eclipse.jdt.internal.compiler.codegen.BranchLabel;
import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
import org.eclipse.jdt.internal.compiler.flow.FlowContext;
import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.impl.IntConstant;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;

public class CaseStatement
extends Statement {
    public Expression constantExpression;
    public BranchLabel targetLabel;
    public Expression[] constantExpressions;
    public BranchLabel[] targetLabels;
    public boolean isExpr = false;

    public CaseStatement(Expression constantExpression, int sourceEnd, int sourceStart) {
        this.constantExpression = constantExpression;
        this.sourceEnd = sourceEnd;
        this.sourceStart = sourceStart;
    }

    @Override
    public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo) {
        if (this.constantExpressions != null && this.constantExpressions.length > 1) {
            Expression[] expressionArray = this.constantExpressions;
            int n = this.constantExpressions.length;
            int n2 = 0;
            while (n2 < n) {
                Expression e = expressionArray[n2];
                this.analyseConstantExpression(currentScope, flowContext, flowInfo, e);
                ++n2;
            }
        } else if (this.constantExpression != null) {
            this.analyseConstantExpression(currentScope, flowContext, flowInfo, this.constantExpression);
        }
        return flowInfo;
    }

    private void analyseConstantExpression(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo, Expression e) {
        if (e.constant == Constant.NotAConstant && !e.resolvedType.isEnum()) {
            currentScope.problemReporter().caseExpressionMustBeConstant(e);
        }
        e.analyseCode(currentScope, flowContext, flowInfo);
    }

    @Override
    public StringBuffer printStatement(int tab, StringBuffer output) {
        CaseStatement.printIndent(tab, output);
        if (this.constantExpression == null) {
            output.append("default ");
            output.append(this.isExpr ? "->" : ":");
        } else {
            output.append("case ");
            if (this.constantExpressions != null && this.constantExpressions.length > 0) {
                int i = 0;
                int l = this.constantExpressions.length;
                while (i < l) {
                    this.constantExpressions[i].printExpression(0, output);
                    if (i < l - 1) {
                        output.append(',');
                    }
                    ++i;
                }
            } else {
                this.constantExpression.printExpression(0, output);
            }
            output.append(this.isExpr ? " ->" : " :");
        }
        return output;
    }

    @Override
    public void generateCode(BlockScope currentScope, CodeStream codeStream) {
        if ((this.bits & Integer.MIN_VALUE) == 0) {
            return;
        }
        int pc = codeStream.position;
        if (this.targetLabels != null) {
            int i = 0;
            int l = this.targetLabels.length;
            while (i < l) {
                this.targetLabels[i].place();
                ++i;
            }
        } else {
            this.targetLabel.place();
        }
        codeStream.recordPositionsFrom(pc, this.sourceStart);
    }

    @Override
    public void resolve(BlockScope scope) {
    }

    @Override
    public Constant[] resolveCase(BlockScope scope, TypeBinding switchExpressionType, SwitchStatement switchStatement) {
        TypeBinding caseType;
        scope.enclosingCase = this;
        if (this.constantExpression == null) {
            if (switchStatement.defaultCase != null) {
                scope.problemReporter().duplicateDefaultCase(this);
            }
            switchStatement.defaultCase = this;
            return Constant.NotAConstantList;
        }
        switchStatement.cases[switchStatement.caseCount++] = this;
        if (switchExpressionType != null && switchExpressionType.isEnum() && this.constantExpression instanceof SingleNameReference) {
            ((SingleNameReference)this.constantExpression).setActualReceiverType((ReferenceBinding)switchExpressionType);
        }
        if ((caseType = this.constantExpression.resolveType(scope)) == null || switchExpressionType == null) {
            return Constant.NotAConstantList;
        }
        if (this.constantExpressions != null && this.constantExpressions.length > 1) {
            ArrayList<Constant> cases = new ArrayList<Constant>();
            Expression[] expressionArray = this.constantExpressions;
            int n = this.constantExpressions.length;
            int n2 = 0;
            while (n2 < n) {
                Constant con;
                Expression e = expressionArray[n2];
                if (e != this.constantExpression) {
                    if (switchExpressionType.isEnum() && e instanceof SingleNameReference) {
                        ((SingleNameReference)e).setActualReceiverType((ReferenceBinding)switchExpressionType);
                    }
                    e.resolveType(scope);
                }
                if ((con = this.resolveConstantExpression(scope, caseType, switchExpressionType, switchStatement, e)) != Constant.NotAConstant) {
                    cases.add(con);
                }
                ++n2;
            }
            if (cases.size() > 0) {
                return cases.toArray(new Constant[cases.size()]);
            }
        } else {
            return new Constant[]{this.resolveConstantExpression(scope, caseType, switchExpressionType, switchStatement, this.constantExpression)};
        }
        return Constant.NotAConstantList;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public Constant resolveConstantExpression(BlockScope scope, TypeBinding caseType, TypeBinding switchExpressionType, SwitchStatement switchStatement, Expression expression) {
        if (expression.isConstantValueOfTypeAssignableToType(caseType, switchExpressionType) || caseType.isCompatibleWith(switchExpressionType)) {
            if (!caseType.isEnum()) return expression.constant;
            if ((expression.bits & 0x1FE00000) >> 21 != 0) {
                scope.problemReporter().enumConstantsCannotBeSurroundedByParenthesis(expression);
            }
            if (expression instanceof NameReference && (expression.bits & 7) == 1) {
                NameReference reference = (NameReference)expression;
                FieldBinding field = reference.fieldBinding();
                if ((field.modifiers & 0x4000) == 0) {
                    scope.problemReporter().enumSwitchCannotTargetField(reference, field);
                    return IntConstant.fromValue(field.original().id + 1);
                } else {
                    if (!(reference instanceof QualifiedNameReference)) return IntConstant.fromValue(field.original().id + 1);
                    scope.problemReporter().cannotUseQualifiedEnumConstantInCaseLabel(reference, field);
                }
                return IntConstant.fromValue(field.original().id + 1);
            }
        } else if (this.isBoxingCompatible(caseType, switchExpressionType, expression, scope)) {
            return expression.constant;
        }
        scope.problemReporter().typeMismatchError(caseType, switchExpressionType, this.constantExpression, (ASTNode)switchStatement.expression);
        return Constant.NotAConstant;
    }

    @Override
    public void traverse(ASTVisitor visitor, BlockScope blockScope) {
        if (visitor.visit(this, blockScope)) {
            if (this.constantExpressions != null && this.constantExpressions.length > 1) {
                Expression[] expressionArray = this.constantExpressions;
                int n = this.constantExpressions.length;
                int n2 = 0;
                while (n2 < n) {
                    Expression e = expressionArray[n2];
                    e.traverse(visitor, blockScope);
                    ++n2;
                }
            } else if (this.constantExpression != null) {
                this.constantExpression.traverse(visitor, blockScope);
            }
        }
        visitor.endVisit(this, blockScope);
    }
}

