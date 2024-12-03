/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.ast.Assignment;
import org.eclipse.jdt.internal.compiler.ast.CastExpression;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.LocalDeclaration;
import org.eclipse.jdt.internal.compiler.ast.NullAnnotationMatching;
import org.eclipse.jdt.internal.compiler.ast.OperatorExpression;
import org.eclipse.jdt.internal.compiler.ast.Reference;
import org.eclipse.jdt.internal.compiler.ast.SingleNameReference;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.codegen.BranchLabel;
import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
import org.eclipse.jdt.internal.compiler.flow.FlowContext;
import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;

public class InstanceOfExpression
extends OperatorExpression {
    public Expression expression;
    public TypeReference type;
    public LocalDeclaration elementVariable;
    static final char[] SECRET_INSTANCEOF_PATTERN_EXPRESSION_VALUE = " instanceOfPatternExpressionValue".toCharArray();
    public LocalVariableBinding secretInstanceOfPatternExpressionValue = null;

    public InstanceOfExpression(Expression expression, TypeReference type) {
        this.expression = expression;
        this.type = type;
        type.bits |= 0x40000000;
        this.bits |= 0x1F00;
        this.sourceStart = expression.sourceStart;
        this.sourceEnd = type.sourceEnd;
    }

    public InstanceOfExpression(Expression expression, LocalDeclaration local) {
        this.expression = expression;
        this.elementVariable = local;
        this.type = this.elementVariable.type;
        this.bits |= 0x1F00;
        this.elementVariable.sourceStart = local.sourceStart;
        this.elementVariable.sourceEnd = local.sourceEnd;
        this.sourceStart = expression.sourceStart;
        this.sourceEnd = local.declarationSourceEnd;
    }

    @Override
    public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo) {
        FieldBinding field;
        LocalVariableBinding local = this.expression.localVariableBinding();
        FlowInfo initsWhenTrue = null;
        if (local != null && (local.type.tagBits & 2L) == 0L) {
            flowInfo = this.expression.analyseCode(currentScope, flowContext, flowInfo).unconditionalInits();
            initsWhenTrue = flowInfo.copy();
            initsWhenTrue.markAsComparedEqualToNonNull(local);
            flowContext.recordUsingNullReference(currentScope, local, this.expression, 1025, flowInfo);
            flowInfo = FlowInfo.conditional(initsWhenTrue.copy(), flowInfo.copy());
        } else if (this.expression instanceof Reference && currentScope.compilerOptions().enableSyntacticNullAnalysisForFields && (field = ((Reference)this.expression).lastFieldBinding()) != null && (field.type.tagBits & 2L) == 0L) {
            flowContext.recordNullCheckedFieldReference((Reference)this.expression, 1);
        }
        if (initsWhenTrue == null) {
            flowInfo = this.expression.analyseCode(currentScope, flowContext, flowInfo).unconditionalInits();
            if (this.elementVariable != null) {
                initsWhenTrue = flowInfo.copy();
            }
        }
        if (this.elementVariable != null) {
            initsWhenTrue.markAsDefinitelyAssigned(this.elementVariable.binding);
        }
        return initsWhenTrue == null ? flowInfo : FlowInfo.conditional(initsWhenTrue, flowInfo.copy());
    }

    @Override
    public void generateCode(BlockScope currentScope, CodeStream codeStream, boolean valueRequired) {
        if (this.elementVariable != null && this.elementVariable.binding != null) {
            this.elementVariable.binding.modifiers &= 0xEFFFFFFF;
        }
        this.addPatternVariables(currentScope, codeStream);
        int pc = codeStream.position;
        if (this.elementVariable != null) {
            this.addAssignment(currentScope, codeStream, this.secretInstanceOfPatternExpressionValue);
            codeStream.load(this.secretInstanceOfPatternExpressionValue);
        } else {
            this.expression.generateCode(currentScope, codeStream, true);
        }
        codeStream.instance_of(this.type, this.type.resolvedType);
        if (this.elementVariable != null) {
            BranchLabel actionLabel = new BranchLabel(codeStream);
            codeStream.dup();
            codeStream.ifeq(actionLabel);
            codeStream.load(this.secretInstanceOfPatternExpressionValue);
            codeStream.removeVariable(this.secretInstanceOfPatternExpressionValue);
            codeStream.checkcast(this.type, this.type.resolvedType, codeStream.position);
            this.elementVariable.binding.recordInitializationStartPC(codeStream.position);
            codeStream.store(this.elementVariable.binding, false);
            codeStream.removeVariable(this.elementVariable.binding);
            codeStream.recordPositionsFrom(codeStream.position, this.sourceEnd);
            actionLabel.place();
        }
        if (valueRequired) {
            codeStream.generateImplicitConversion(this.implicitConversion);
        } else {
            codeStream.pop();
        }
        codeStream.recordPositionsFrom(pc, this.sourceStart);
    }

    @Override
    public void generateOptimizedBoolean(BlockScope currentScope, CodeStream codeStream, BranchLabel trueLabel, BranchLabel falseLabel, boolean valueRequired) {
        if (this.elementVariable == null) {
            super.generateOptimizedBoolean(currentScope, codeStream, trueLabel, falseLabel, valueRequired);
            return;
        }
        Constant cst = this.optimizedBooleanConstant();
        this.addPatternVariables(currentScope, codeStream);
        int pc = codeStream.position;
        this.addAssignment(currentScope, codeStream, this.secretInstanceOfPatternExpressionValue);
        codeStream.load(this.secretInstanceOfPatternExpressionValue);
        BranchLabel nextSibling = falseLabel != null ? falseLabel : new BranchLabel(codeStream);
        codeStream.instance_of(this.type, this.type.resolvedType);
        if (this.elementVariable != null) {
            codeStream.ifeq(nextSibling);
            codeStream.load(this.secretInstanceOfPatternExpressionValue);
            codeStream.checkcast(this.type, this.type.resolvedType, codeStream.position);
            codeStream.dup();
            codeStream.store(this.elementVariable.binding, false);
            codeStream.load(this.secretInstanceOfPatternExpressionValue);
            codeStream.removeVariable(this.secretInstanceOfPatternExpressionValue);
            codeStream.checkcast(this.type, this.type.resolvedType, codeStream.position);
        }
        if (valueRequired && cst == Constant.NotAConstant) {
            codeStream.generateImplicitConversion(this.implicitConversion);
        } else {
            codeStream.pop();
        }
        codeStream.recordPositionsFrom(pc, this.sourceStart);
        if (cst != Constant.NotAConstant && cst.typeID() == 5) {
            pc = codeStream.position;
            if (cst.booleanValue()) {
                if (valueRequired && falseLabel == null && trueLabel != null) {
                    codeStream.goto_(trueLabel);
                }
            } else if (valueRequired && falseLabel != null && trueLabel == null) {
                codeStream.goto_(falseLabel);
            }
            codeStream.recordPositionsFrom(pc, this.sourceStart);
        } else {
            int position = codeStream.position;
            if (valueRequired) {
                if (falseLabel == null) {
                    if (trueLabel != null) {
                        codeStream.if_acmpeq(trueLabel);
                    }
                } else if (trueLabel == null) {
                    codeStream.if_acmpne(falseLabel);
                }
            }
            codeStream.recordPositionsFrom(position, this.sourceEnd);
        }
        if (nextSibling != falseLabel) {
            nextSibling.place();
        }
    }

    private void addAssignment(BlockScope currentScope, CodeStream codeStream, LocalVariableBinding local) {
        assert (local != null);
        SingleNameReference lhs = new SingleNameReference(local.name, 0L);
        lhs.binding = local;
        lhs.bits &= 0xFFFFFFF8;
        lhs.bits |= 2;
        lhs.bits |= 0x10;
        ((LocalVariableBinding)lhs.binding).markReferenced();
        Assignment assignment = new Assignment(lhs, this.expression, 0);
        assignment.generateCode(currentScope, codeStream);
        codeStream.addVariable(this.secretInstanceOfPatternExpressionValue);
    }

    @Override
    public StringBuffer printExpressionNoParenthesis(int indent, StringBuffer output) {
        this.expression.printExpression(indent, output).append(" instanceof ");
        return this.elementVariable == null ? this.type.print(0, output) : this.elementVariable.printAsExpression(0, output);
    }

    @Override
    public void addPatternVariables(BlockScope currentScope, CodeStream codeStream) {
        if (this.elementVariable != null) {
            codeStream.addVisibleLocalVariable(this.elementVariable.binding);
        }
    }

    public boolean resolvePatternVariable(BlockScope scope) {
        if (this.elementVariable == null) {
            return false;
        }
        if (this.elementVariable.binding == null) {
            this.elementVariable.modifiers |= 0x10000000;
            this.elementVariable.resolve(scope, true);
            this.elementVariable.modifiers &= 0xFBFFFFFF;
            this.elementVariable.binding.modifiers |= 0x10000000;
            this.elementVariable.binding.useFlag = 1;
            this.type = this.elementVariable.type;
        }
        return true;
    }

    @Override
    public void collectPatternVariablesToScope(LocalVariableBinding[] variables, BlockScope scope) {
        this.expression.collectPatternVariablesToScope(this.patternVarsWhenTrue, scope);
        if (this.elementVariable != null) {
            if (this.elementVariable.binding == null) {
                this.resolvePatternVariable(scope);
                if (variables != null) {
                    LocalVariableBinding[] localVariableBindingArray = variables;
                    int n = variables.length;
                    int n2 = 0;
                    while (n2 < n) {
                        LocalVariableBinding variable = localVariableBindingArray[n2];
                        if (CharOperation.equals(this.elementVariable.name, variable.name)) {
                            scope.problemReporter().redefineLocal(this.elementVariable);
                        }
                        ++n2;
                    }
                }
            }
            if (this.patternVarsWhenTrue == null) {
                this.patternVarsWhenTrue = new LocalVariableBinding[1];
                this.patternVarsWhenTrue[0] = this.elementVariable.binding;
            } else {
                this.addPatternVariablesWhenTrue(new LocalVariableBinding[]{this.elementVariable.binding});
            }
        }
    }

    @Override
    public void addPatternVariablesWhenTrue(LocalVariableBinding[] vars) {
        if (this.patternVarsWhenTrue == null) {
            this.getPatternVariablesWhenTrue();
        }
        if (vars == null || vars.length == 0) {
            return;
        }
        if (this.patternVarsWhenTrue == null) {
            this.patternVarsWhenTrue = vars;
        } else {
            int oldSize = this.patternVarsWhenTrue.length;
            int newLength = oldSize + vars.length;
            this.patternVarsWhenTrue = new LocalVariableBinding[newLength];
            System.arraycopy(this.patternVarsWhenTrue, 0, this.patternVarsWhenTrue, 0, oldSize);
            System.arraycopy(vars, 0, this.patternVarsWhenTrue, oldSize, vars.length);
        }
    }

    @Override
    public boolean containsPatternVariable() {
        return this.elementVariable != null;
    }

    @Override
    protected LocalDeclaration getPatternVariableIntroduced() {
        return this.elementVariable;
    }

    private void addSecretInstanceOfPatternExpressionValue(BlockScope scope1) {
        LocalVariableBinding local = new LocalVariableBinding(SECRET_INSTANCEOF_PATTERN_EXPRESSION_VALUE, TypeBinding.wellKnownType(scope1, 1), 0, false);
        local.setConstant(Constant.NotAConstant);
        local.useFlag = 1;
        local.declaration = new LocalDeclaration(SECRET_INSTANCEOF_PATTERN_EXPRESSION_VALUE, 0, 0);
        scope1.addLocalVariable(local);
        this.secretInstanceOfPatternExpressionValue = local;
    }

    @Override
    public TypeBinding resolveType(BlockScope scope) {
        TypeBinding expressionType;
        this.constant = Constant.NotAConstant;
        if (this.elementVariable != null) {
            this.addSecretInstanceOfPatternExpressionValue(scope);
        }
        this.resolvePatternVariable(scope);
        TypeBinding checkedType = this.type.resolveType(scope, true);
        if (this.expression instanceof CastExpression) {
            ((CastExpression)this.expression).setInstanceofType(checkedType);
        }
        if ((expressionType = this.expression.resolveType(scope)) != null && checkedType != null && this.type.hasNullTypeAnnotation(TypeReference.AnnotationPosition.ANY) && (!expressionType.isCompatibleWith(checkedType) || NullAnnotationMatching.analyse(checkedType, expressionType, -1).isAnyMismatch())) {
            scope.problemReporter().nullAnnotationUnsupportedLocation(this.type);
        }
        if (expressionType == null || checkedType == null) {
            return null;
        }
        if (this.secretInstanceOfPatternExpressionValue != null && expressionType != TypeBinding.NULL) {
            this.secretInstanceOfPatternExpressionValue.type = expressionType;
        }
        if (!checkedType.isReifiable()) {
            boolean isLegal;
            CompilerOptions options = scope.compilerOptions();
            if (options.complianceLevel < 0x3C0000L) {
                scope.problemReporter().illegalInstanceOfGenericType(checkedType, this);
            } else if (!(expressionType == TypeBinding.NULL || (isLegal = this.checkCastTypesCompatibility(scope, checkedType, expressionType, this.expression, true)) && (this.bits & 0x80) == 0)) {
                scope.problemReporter().unsafeCastInInstanceof(this.expression, checkedType, expressionType);
            }
        } else if (checkedType.isValidBinding() && (expressionType != TypeBinding.NULL && expressionType.isBaseType() || checkedType.isBaseType() || !this.checkCastTypesCompatibility(scope, checkedType, expressionType, null, true))) {
            scope.problemReporter().notCompatibleTypesError(this, expressionType, checkedType);
        }
        if (this.secretInstanceOfPatternExpressionValue != null && expressionType.isSubtypeOf(checkedType, false)) {
            scope.problemReporter().patternCannotBeSubtypeOfExpression(this.elementVariable.binding, this);
        }
        this.resolvedType = TypeBinding.BOOLEAN;
        return this.resolvedType;
    }

    @Override
    public boolean checkUnsafeCast(Scope scope, TypeBinding castType, TypeBinding expressionType, TypeBinding match, boolean isNarrowing) {
        if (!castType.isReifiable()) {
            return CastExpression.checkUnsafeCast(this, scope, castType, expressionType, match, isNarrowing);
        }
        return super.checkUnsafeCast(scope, castType, expressionType, match, isNarrowing);
    }

    @Override
    public void tagAsUnnecessaryCast(Scope scope, TypeBinding castType) {
        if (this.expression.resolvedType != TypeBinding.NULL) {
            scope.problemReporter().unnecessaryInstanceof(this, castType);
        }
    }

    @Override
    public void traverse(ASTVisitor visitor, BlockScope scope) {
        if (visitor.visit(this, scope)) {
            this.expression.traverse(visitor, scope);
            if (this.elementVariable != null) {
                this.elementVariable.traverse(visitor, scope);
            } else {
                this.type.traverse(visitor, scope);
            }
        }
        visitor.endVisit(this, scope);
    }
}

