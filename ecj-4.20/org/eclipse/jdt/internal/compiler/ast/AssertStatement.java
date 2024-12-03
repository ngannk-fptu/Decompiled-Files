/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Clinit;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.Statement;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.codegen.BranchLabel;
import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
import org.eclipse.jdt.internal.compiler.flow.FlowContext;
import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
import org.eclipse.jdt.internal.compiler.flow.UnconditionalFlowInfo;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;

public class AssertStatement
extends Statement {
    public Expression assertExpression;
    public Expression exceptionArgument;
    int preAssertInitStateIndex = -1;
    private FieldBinding assertionSyntheticFieldBinding;

    public AssertStatement(Expression exceptionArgument, Expression assertExpression, int startPosition) {
        this.assertExpression = assertExpression;
        this.exceptionArgument = exceptionArgument;
        this.sourceStart = startPosition;
        this.sourceEnd = exceptionArgument.sourceEnd;
    }

    public AssertStatement(Expression assertExpression, int startPosition) {
        this.assertExpression = assertExpression;
        this.sourceStart = startPosition;
        this.sourceEnd = assertExpression.sourceEnd;
    }

    @Override
    public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo) {
        this.preAssertInitStateIndex = currentScope.methodScope().recordInitializationStates(flowInfo);
        Constant cst = this.assertExpression.optimizedBooleanConstant();
        this.assertExpression.checkNPEbyUnboxing(currentScope, flowContext, flowInfo);
        boolean isOptimizedTrueAssertion = cst != Constant.NotAConstant && cst.booleanValue();
        boolean isOptimizedFalseAssertion = cst != Constant.NotAConstant && !cst.booleanValue();
        flowContext.tagBits |= 0x1000;
        FlowInfo conditionFlowInfo = this.assertExpression.analyseCode(currentScope, flowContext, flowInfo.copy());
        flowContext.extendTimeToLiveForNullCheckedField(1);
        flowContext.tagBits &= 0xFFFFEFFF;
        UnconditionalFlowInfo assertWhenTrueInfo = conditionFlowInfo.initsWhenTrue().unconditionalInits();
        FlowInfo assertInfo = conditionFlowInfo.initsWhenFalse();
        if (isOptimizedTrueAssertion) {
            assertInfo.setReachMode(1);
        }
        if (this.exceptionArgument != null) {
            FlowInfo exceptionInfo = this.exceptionArgument.analyseCode(currentScope, flowContext, assertInfo.copy());
            if (isOptimizedTrueAssertion) {
                currentScope.problemReporter().fakeReachable(this.exceptionArgument);
            } else {
                flowContext.checkExceptionHandlers(currentScope.getJavaLangAssertionError(), (ASTNode)this, exceptionInfo, currentScope);
            }
        }
        if (!isOptimizedTrueAssertion) {
            this.manageSyntheticAccessIfNecessary(currentScope, flowInfo);
        }
        flowContext.recordAbruptExit();
        if (isOptimizedFalseAssertion) {
            return flowInfo;
        }
        CompilerOptions compilerOptions = currentScope.compilerOptions();
        if (!compilerOptions.includeNullInfoFromAsserts) {
            return flowInfo.nullInfoLessUnconditionalCopy().mergedWith(assertInfo.nullInfoLessUnconditionalCopy()).addNullInfoFrom(flowInfo);
        }
        return flowInfo.mergedWith(assertInfo.nullInfoLessUnconditionalCopy()).addInitializationsFrom(assertWhenTrueInfo.discardInitializationInfo());
    }

    @Override
    public void generateCode(BlockScope currentScope, CodeStream codeStream) {
        if ((this.bits & Integer.MIN_VALUE) == 0) {
            return;
        }
        int pc = codeStream.position;
        if (this.assertionSyntheticFieldBinding != null) {
            BranchLabel assertionActivationLabel = new BranchLabel(codeStream);
            codeStream.fieldAccess((byte)-78, this.assertionSyntheticFieldBinding, null);
            codeStream.ifne(assertionActivationLabel);
            BranchLabel falseLabel = new BranchLabel(codeStream);
            this.assertExpression.generateOptimizedBoolean(currentScope, codeStream, falseLabel, null, true);
            codeStream.newJavaLangAssertionError();
            codeStream.dup();
            if (this.exceptionArgument != null) {
                this.exceptionArgument.generateCode(currentScope, codeStream, true);
                codeStream.invokeJavaLangAssertionErrorConstructor(this.exceptionArgument.implicitConversion & 0xF);
            } else {
                codeStream.invokeJavaLangAssertionErrorDefaultConstructor();
            }
            codeStream.athrow();
            if (this.preAssertInitStateIndex != -1) {
                codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.preAssertInitStateIndex);
            }
            falseLabel.place();
            assertionActivationLabel.place();
        } else if (this.preAssertInitStateIndex != -1) {
            codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.preAssertInitStateIndex);
        }
        codeStream.recordPositionsFrom(pc, this.sourceStart);
    }

    @Override
    public void resolve(BlockScope scope) {
        TypeBinding exceptionArgumentType;
        this.assertExpression.resolveTypeExpecting(scope, TypeBinding.BOOLEAN);
        if (this.exceptionArgument != null && (exceptionArgumentType = this.exceptionArgument.resolveType(scope)) != null) {
            int id = exceptionArgumentType.id;
            switch (id) {
                case 6: {
                    scope.problemReporter().illegalVoidExpression(this.exceptionArgument);
                }
                default: {
                    id = 1;
                }
                case 2: 
                case 3: 
                case 4: 
                case 5: 
                case 7: 
                case 8: 
                case 9: 
                case 10: 
                case 11: 
            }
            this.exceptionArgument.implicitConversion = (id << 4) + id;
        }
    }

    @Override
    public void traverse(ASTVisitor visitor, BlockScope scope) {
        if (visitor.visit(this, scope)) {
            this.assertExpression.traverse(visitor, scope);
            if (this.exceptionArgument != null) {
                this.exceptionArgument.traverse(visitor, scope);
            }
        }
        visitor.endVisit(this, scope);
    }

    public void manageSyntheticAccessIfNecessary(BlockScope currentScope, FlowInfo flowInfo) {
        if ((flowInfo.tagBits & 1) == 0) {
            SourceTypeBinding outerMostClass = currentScope.enclosingSourceType();
            while (outerMostClass.isLocalType()) {
                ReferenceBinding enclosing = outerMostClass.enclosingType();
                if (enclosing == null || enclosing.isInterface()) break;
                outerMostClass = (SourceTypeBinding)enclosing;
            }
            this.assertionSyntheticFieldBinding = outerMostClass.addSyntheticFieldForAssert(currentScope);
            TypeDeclaration typeDeclaration = outerMostClass.scope.referenceType();
            AbstractMethodDeclaration[] methods = typeDeclaration.methods;
            int i = 0;
            int max = methods.length;
            while (i < max) {
                AbstractMethodDeclaration method = methods[i];
                if (method.isClinit()) {
                    ((Clinit)method).setAssertionSupport(this.assertionSyntheticFieldBinding, currentScope.compilerOptions().sourceLevel < 0x310000L);
                    break;
                }
                ++i;
            }
        }
    }

    @Override
    public StringBuffer printStatement(int tab, StringBuffer output) {
        AssertStatement.printIndent(tab, output);
        output.append("assert ");
        this.assertExpression.printExpression(0, output);
        if (this.exceptionArgument != null) {
            output.append(": ");
            this.exceptionArgument.printExpression(0, output);
        }
        return output.append(';');
    }
}

