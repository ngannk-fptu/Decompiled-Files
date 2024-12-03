/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.ast.ArrayAllocationExpression;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.ExpressionContext;
import org.eclipse.jdt.internal.compiler.ast.FakedTrackingVariable;
import org.eclipse.jdt.internal.compiler.ast.NullLiteral;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
import org.eclipse.jdt.internal.compiler.flow.FlowContext;
import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.lookup.ArrayBinding;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;

public class ArrayInitializer
extends Expression {
    public Expression[] expressions;
    public ArrayBinding binding;

    @Override
    public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo) {
        if (this.expressions != null) {
            CompilerOptions compilerOptions = currentScope.compilerOptions();
            boolean analyseResources = compilerOptions.analyseResourceLeaks;
            boolean evalNullTypeAnnotations = currentScope.environment().usesNullTypeAnnotations();
            int i = 0;
            int max = this.expressions.length;
            while (i < max) {
                flowInfo = this.expressions[i].analyseCode(currentScope, flowContext, flowInfo).unconditionalInits();
                if (analyseResources && FakedTrackingVariable.isAnyCloseable(this.expressions[i].resolvedType)) {
                    flowInfo = FakedTrackingVariable.markPassedToOutside(currentScope, this.expressions[i], flowInfo, flowContext, false);
                }
                if (evalNullTypeAnnotations) {
                    this.checkAgainstNullTypeAnnotation(currentScope, this.binding.elementsType(), this.expressions[i], flowContext, flowInfo);
                }
                ++i;
            }
        }
        return flowInfo;
    }

    @Override
    public void generateCode(BlockScope currentScope, CodeStream codeStream, boolean valueRequired) {
        this.generateCode(null, null, currentScope, codeStream, valueRequired);
    }

    public void generateCode(TypeReference typeReference, ArrayAllocationExpression allocationExpression, BlockScope currentScope, CodeStream codeStream, boolean valueRequired) {
        int pc;
        block13: {
            pc = codeStream.position;
            int expressionLength = this.expressions == null ? 0 : this.expressions.length;
            codeStream.generateInlinedValue(expressionLength);
            codeStream.newArray(typeReference, allocationExpression, this.binding);
            if (this.expressions == null) break block13;
            int elementsTypeID = this.binding.dimensions > 1 ? -1 : this.binding.leafComponentType.id;
            int i = 0;
            while (i < expressionLength) {
                block15: {
                    Expression expr;
                    block14: {
                        expr = this.expressions[i];
                        if (expr.constant == Constant.NotAConstant) break block14;
                        switch (elementsTypeID) {
                            case 2: 
                            case 3: 
                            case 4: 
                            case 7: 
                            case 10: {
                                if (expr.constant.longValue() != 0L) {
                                    codeStream.dup();
                                    codeStream.generateInlinedValue(i);
                                    expr.generateCode(currentScope, codeStream, true);
                                    codeStream.arrayAtPut(elementsTypeID, false);
                                    break;
                                }
                                break block15;
                            }
                            case 8: 
                            case 9: {
                                double constantValue = expr.constant.doubleValue();
                                if (constantValue == -0.0 || constantValue != 0.0) {
                                    codeStream.dup();
                                    codeStream.generateInlinedValue(i);
                                    expr.generateCode(currentScope, codeStream, true);
                                    codeStream.arrayAtPut(elementsTypeID, false);
                                    break;
                                }
                                break block15;
                            }
                            case 5: {
                                if (expr.constant.booleanValue()) {
                                    codeStream.dup();
                                    codeStream.generateInlinedValue(i);
                                    expr.generateCode(currentScope, codeStream, true);
                                    codeStream.arrayAtPut(elementsTypeID, false);
                                    break;
                                }
                                break block15;
                            }
                            default: {
                                if (!(expr instanceof NullLiteral)) {
                                    codeStream.dup();
                                    codeStream.generateInlinedValue(i);
                                    expr.generateCode(currentScope, codeStream, true);
                                    codeStream.arrayAtPut(elementsTypeID, false);
                                    break;
                                }
                                break block15;
                            }
                        }
                        break block15;
                    }
                    if (!(expr instanceof NullLiteral)) {
                        codeStream.dup();
                        codeStream.generateInlinedValue(i);
                        expr.generateCode(currentScope, codeStream, true);
                        codeStream.arrayAtPut(elementsTypeID, false);
                    }
                }
                ++i;
            }
        }
        if (valueRequired) {
            codeStream.generateImplicitConversion(this.implicitConversion);
        } else {
            codeStream.pop();
        }
        codeStream.recordPositionsFrom(pc, this.sourceStart);
    }

    @Override
    public StringBuffer printExpression(int indent, StringBuffer output) {
        output.append('{');
        if (this.expressions != null) {
            int j = 20;
            int i = 0;
            while (i < this.expressions.length) {
                if (i > 0) {
                    output.append(", ");
                }
                this.expressions[i].printExpression(0, output);
                if (--j == 0) {
                    output.append('\n');
                    ArrayInitializer.printIndent(indent + 1, output);
                    j = 20;
                }
                ++i;
            }
        }
        return output.append('}');
    }

    @Override
    public TypeBinding resolveTypeExpecting(BlockScope scope, TypeBinding expectedType) {
        this.constant = Constant.NotAConstant;
        if (expectedType instanceof ArrayBinding) {
            TypeBinding leafComponentType;
            if ((this.bits & 1) == 0 && !(leafComponentType = expectedType.leafComponentType()).isReifiable()) {
                scope.problemReporter().illegalGenericArray(leafComponentType, this);
            }
            this.binding = (ArrayBinding)expectedType;
            this.resolvedType = this.binding;
            if (this.expressions == null) {
                return this.binding;
            }
            TypeBinding elementType = this.binding.elementsType();
            int i = 0;
            int length = this.expressions.length;
            while (i < length) {
                TypeBinding expressionType;
                Expression expression = this.expressions[i];
                expression.setExpressionContext(ExpressionContext.ASSIGNMENT_CONTEXT);
                expression.setExpectedType(elementType);
                TypeBinding typeBinding = expressionType = expression instanceof ArrayInitializer ? expression.resolveTypeExpecting(scope, elementType) : expression.resolveType(scope);
                if (expressionType != null) {
                    if (TypeBinding.notEquals(elementType, expressionType)) {
                        scope.compilationUnitScope().recordTypeConversion(elementType, expressionType);
                    }
                    if (expression.isConstantValueOfTypeAssignableToType(expressionType, elementType) || expressionType.isCompatibleWith(elementType)) {
                        expression.computeConversion(scope, elementType, expressionType);
                    } else if (this.isBoxingCompatible(expressionType, elementType, expression, scope)) {
                        expression.computeConversion(scope, elementType, expressionType);
                    } else {
                        scope.problemReporter().typeMismatchError(expressionType, elementType, expression, null);
                    }
                }
                ++i;
            }
            return this.binding;
        }
        TypeBinding leafElementType = null;
        int dim = 1;
        if (this.expressions == null) {
            leafElementType = scope.getJavaLangObject();
        } else {
            Expression expression = this.expressions[0];
            while (expression != null && expression instanceof ArrayInitializer) {
                ++dim;
                Expression[] subExprs = ((ArrayInitializer)expression).expressions;
                if (subExprs == null) {
                    leafElementType = scope.getJavaLangObject();
                    expression = null;
                    break;
                }
                expression = ((ArrayInitializer)expression).expressions[0];
            }
            if (expression != null) {
                leafElementType = expression.resolveType(scope);
            }
            int i = 1;
            int length = this.expressions.length;
            while (i < length) {
                expression = this.expressions[i];
                if (expression != null) {
                    expression.resolveType(scope);
                }
                ++i;
            }
        }
        if (leafElementType != null) {
            this.resolvedType = scope.createArrayType(leafElementType, dim);
            if (expectedType != null) {
                scope.problemReporter().typeMismatchError(this.resolvedType, expectedType, this, null);
            }
        }
        return null;
    }

    @Override
    public void traverse(ASTVisitor visitor, BlockScope scope) {
        if (visitor.visit(this, scope) && this.expressions != null) {
            int expressionsLength = this.expressions.length;
            int i = 0;
            while (i < expressionsLength) {
                this.expressions[i].traverse(visitor, scope);
                ++i;
            }
        }
        visitor.endVisit(this, scope);
    }
}

