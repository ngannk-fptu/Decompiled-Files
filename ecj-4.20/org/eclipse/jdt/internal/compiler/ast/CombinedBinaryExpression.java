/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.ast.BinaryExpression;
import org.eclipse.jdt.internal.compiler.ast.CastExpression;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
import org.eclipse.jdt.internal.compiler.flow.FlowContext;
import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;

public class CombinedBinaryExpression
extends BinaryExpression {
    public int arity;
    public int arityMax;
    public static final int ARITY_MAX_MAX = 160;
    public static final int ARITY_MAX_MIN = 20;
    public static int defaultArityMaxStartingValue = 20;
    public BinaryExpression[] referencesTable;

    public CombinedBinaryExpression(Expression left, Expression right, int operator, int arity) {
        super(left, right, operator);
        this.initArity(left, arity);
    }

    public CombinedBinaryExpression(CombinedBinaryExpression expression) {
        super(expression);
        this.initArity(expression.left, expression.arity);
    }

    @Override
    public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo) {
        if (this.referencesTable == null) {
            return super.analyseCode(currentScope, flowContext, flowInfo);
        }
        try {
            BinaryExpression cursor = this.referencesTable[0];
            flowInfo = cursor.left.analyseCode(currentScope, flowContext, flowInfo).unconditionalInits();
            if (cursor.resolvedType.id != 11) {
                cursor.left.checkNPE(currentScope, flowContext, flowInfo);
            }
            int i = 0;
            int end = this.arity;
            while (i < end) {
                cursor = this.referencesTable[i];
                flowInfo = cursor.right.analyseCode(currentScope, flowContext, flowInfo).unconditionalInits();
                if (cursor.resolvedType.id != 11) {
                    cursor.right.checkNPE(currentScope, flowContext, flowInfo);
                }
                ++i;
            }
            flowInfo = this.right.analyseCode(currentScope, flowContext, flowInfo).unconditionalInits();
            if (this.resolvedType.id != 11) {
                this.right.checkNPE(currentScope, flowContext, flowInfo);
            }
            FlowInfo flowInfo2 = flowInfo;
            return flowInfo2;
        }
        finally {
            flowContext.recordAbruptExit();
        }
    }

    @Override
    public void generateOptimizedStringConcatenation(BlockScope blockScope, CodeStream codeStream, int typeID) {
        if (this.referencesTable == null) {
            super.generateOptimizedStringConcatenation(blockScope, codeStream, typeID);
        } else if ((this.bits & 0x3F00) >> 8 == 14 && (this.bits & 0xF) == 11) {
            if (this.constant != Constant.NotAConstant) {
                codeStream.generateConstant(this.constant, this.implicitConversion);
                codeStream.invokeStringConcatenationAppendForType(this.implicitConversion & 0xF);
            } else {
                BinaryExpression cursor = this.referencesTable[0];
                int restart = 0;
                int pc = codeStream.position;
                restart = this.arity - 1;
                while (restart >= 0) {
                    cursor = this.referencesTable[restart];
                    if (cursor.constant != Constant.NotAConstant) {
                        codeStream.generateConstant(cursor.constant, cursor.implicitConversion);
                        codeStream.invokeStringConcatenationAppendForType(cursor.implicitConversion & 0xF);
                        break;
                    }
                    --restart;
                }
                if (++restart == 0) {
                    cursor.left.generateOptimizedStringConcatenation(blockScope, codeStream, cursor.left.implicitConversion & 0xF);
                }
                int i = restart;
                while (i < this.arity) {
                    cursor = this.referencesTable[i];
                    codeStream.recordPositionsFrom(pc, cursor.left.sourceStart);
                    int pcAux = codeStream.position;
                    cursor.right.generateOptimizedStringConcatenation(blockScope, codeStream, cursor.right.implicitConversion & 0xF);
                    codeStream.recordPositionsFrom(pcAux, cursor.right.sourceStart);
                    ++i;
                }
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
        if (this.referencesTable == null) {
            super.generateOptimizedStringConcatenationCreation(blockScope, codeStream, typeID);
        } else if ((this.bits & 0x3F00) >> 8 == 14 && (this.bits & 0xF) == 11 && this.constant == Constant.NotAConstant) {
            int pc = codeStream.position;
            BinaryExpression cursor = this.referencesTable[this.arity - 1];
            int restart = 0;
            restart = this.arity - 1;
            while (restart >= 0) {
                cursor = this.referencesTable[restart];
                if ((cursor.bits & 0x3F00) >> 8 == 14 && (cursor.bits & 0xF) == 11) {
                    if (cursor.constant != Constant.NotAConstant) {
                        codeStream.newStringContatenation();
                        codeStream.dup();
                        codeStream.ldc(cursor.constant.stringValue());
                        codeStream.invokeStringConcatenationStringConstructor();
                        break;
                    }
                } else {
                    cursor.generateOptimizedStringConcatenationCreation(blockScope, codeStream, cursor.implicitConversion & 0xF);
                    break;
                }
                --restart;
            }
            if (++restart == 0) {
                cursor.left.generateOptimizedStringConcatenationCreation(blockScope, codeStream, cursor.left.implicitConversion & 0xF);
            }
            int i = restart;
            while (i < this.arity) {
                cursor = this.referencesTable[i];
                codeStream.recordPositionsFrom(pc, cursor.left.sourceStart);
                int pcAux = codeStream.position;
                cursor.right.generateOptimizedStringConcatenation(blockScope, codeStream, cursor.right.implicitConversion & 0xF);
                codeStream.recordPositionsFrom(pcAux, cursor.right.sourceStart);
                ++i;
            }
            codeStream.recordPositionsFrom(pc, this.left.sourceStart);
            pc = codeStream.position;
            this.right.generateOptimizedStringConcatenation(blockScope, codeStream, this.right.implicitConversion & 0xF);
            codeStream.recordPositionsFrom(pc, this.right.sourceStart);
        } else {
            super.generateOptimizedStringConcatenationCreation(blockScope, codeStream, typeID);
        }
    }

    private void initArity(Expression expression, int value) {
        this.arity = value;
        if (value > 1) {
            this.referencesTable = new BinaryExpression[value];
            this.referencesTable[value - 1] = (BinaryExpression)expression;
            int i = value - 1;
            while (i > 0) {
                this.referencesTable[i - 1] = (BinaryExpression)this.referencesTable[i].left;
                --i;
            }
        } else {
            this.arityMax = defaultArityMaxStartingValue;
        }
    }

    @Override
    public StringBuffer printExpressionNoParenthesis(int indent, StringBuffer output) {
        if (this.referencesTable == null) {
            return super.printExpressionNoParenthesis(indent, output);
        }
        String operatorString = this.operatorToString();
        int i = this.arity - 1;
        while (i >= 0) {
            output.append('(');
            --i;
        }
        output = this.referencesTable[0].left.printExpression(indent, output);
        i = 0;
        int end = this.arity;
        while (i < end) {
            output.append(' ').append(operatorString).append(' ');
            output = this.referencesTable[i].right.printExpression(0, output);
            output.append(')');
            ++i;
        }
        output.append(' ').append(operatorString).append(' ');
        return this.right.printExpression(0, output);
    }

    @Override
    public TypeBinding resolveType(BlockScope scope) {
        if (this.referencesTable == null) {
            return super.resolveType(scope);
        }
        BinaryExpression cursor = this.referencesTable[0];
        if (cursor.left instanceof CastExpression) {
            cursor.left.bits |= 0x20;
        }
        cursor.left.resolveType(scope);
        int i = 0;
        int end = this.arity;
        while (i < end) {
            this.referencesTable[i].nonRecursiveResolveTypeUpwards(scope);
            ++i;
        }
        this.nonRecursiveResolveTypeUpwards(scope);
        return this.resolvedType;
    }

    @Override
    public void traverse(ASTVisitor visitor, BlockScope scope) {
        if (this.referencesTable == null) {
            super.traverse(visitor, scope);
        } else {
            if (visitor.visit(this, scope)) {
                int restart = this.arity - 1;
                while (restart >= 0) {
                    if (!visitor.visit(this.referencesTable[restart], scope)) {
                        visitor.endVisit(this.referencesTable[restart], scope);
                        break;
                    }
                    --restart;
                }
                if (++restart == 0) {
                    this.referencesTable[0].left.traverse(visitor, scope);
                }
                int i = restart;
                int end = this.arity;
                while (i < end) {
                    this.referencesTable[i].right.traverse(visitor, scope);
                    visitor.endVisit(this.referencesTable[i], scope);
                    ++i;
                }
                this.right.traverse(visitor, scope);
            }
            visitor.endVisit(this, scope);
        }
    }

    public void tuneArityMax() {
        if (this.arityMax < 160) {
            this.arityMax *= 2;
        }
    }
}

