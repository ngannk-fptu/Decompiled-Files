/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.ast;

import java.util.function.BooleanSupplier;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.BreakStatement;
import org.eclipse.jdt.internal.compiler.ast.ConditionalExpression;
import org.eclipse.jdt.internal.compiler.ast.ContinueStatement;
import org.eclipse.jdt.internal.compiler.ast.DoStatement;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.ExpressionContext;
import org.eclipse.jdt.internal.compiler.ast.ForStatement;
import org.eclipse.jdt.internal.compiler.ast.ForeachStatement;
import org.eclipse.jdt.internal.compiler.ast.Invocation;
import org.eclipse.jdt.internal.compiler.ast.LambdaExpression;
import org.eclipse.jdt.internal.compiler.ast.NullAnnotationMatching;
import org.eclipse.jdt.internal.compiler.ast.Reference;
import org.eclipse.jdt.internal.compiler.ast.SwitchExpression;
import org.eclipse.jdt.internal.compiler.ast.SwitchStatement;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.UnaryExpression;
import org.eclipse.jdt.internal.compiler.ast.WhileStatement;
import org.eclipse.jdt.internal.compiler.ast.YieldStatement;
import org.eclipse.jdt.internal.compiler.codegen.BranchLabel;
import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
import org.eclipse.jdt.internal.compiler.flow.FlowContext;
import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.lookup.AnnotationBinding;
import org.eclipse.jdt.internal.compiler.lookup.ArrayBinding;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;

public abstract class Statement
extends ASTNode {
    public static final int NOT_COMPLAINED = 0;
    public static final int COMPLAINED_FAKE_REACHABLE = 1;
    public static final int COMPLAINED_UNREACHABLE = 2;
    LocalVariableBinding[] patternVarsWhenTrue = null;
    LocalVariableBinding[] patternVarsWhenFalse = null;

    protected static boolean isKnowDeadCodePattern(Expression expression) {
        if (expression instanceof UnaryExpression) {
            expression = ((UnaryExpression)expression).expression;
        }
        return expression instanceof Reference;
    }

    public abstract FlowInfo analyseCode(BlockScope var1, FlowContext var2, FlowInfo var3);

    public boolean doesNotCompleteNormally() {
        return false;
    }

    public boolean completesByContinue() {
        return false;
    }

    public boolean canCompleteNormally() {
        return true;
    }

    public boolean continueCompletes() {
        return false;
    }

    protected void analyseArguments(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo, MethodBinding methodBinding, Expression[] arguments) {
        block10: {
            int numParamsToCheck;
            boolean hasJDK15NullAnnotations;
            block11: {
                if (arguments == null) break block10;
                CompilerOptions compilerOptions = currentScope.compilerOptions();
                if (compilerOptions.sourceLevel >= 0x330000L && methodBinding.isPolymorphic()) {
                    return;
                }
                boolean considerTypeAnnotations = currentScope.environment().usesNullTypeAnnotations();
                hasJDK15NullAnnotations = methodBinding.parameterNonNullness != null;
                numParamsToCheck = methodBinding.parameters.length;
                int varArgPos = -1;
                TypeBinding varArgsType = null;
                boolean passThrough = false;
                if ((considerTypeAnnotations || hasJDK15NullAnnotations) && methodBinding.isVarargs()) {
                    TypeBinding lastType;
                    varArgPos = numParamsToCheck - 1;
                    varArgsType = methodBinding.parameters[varArgPos];
                    if (numParamsToCheck == arguments.length && ((lastType = arguments[varArgPos].resolvedType) == TypeBinding.NULL || varArgsType.dimensions() == lastType.dimensions() && lastType.isCompatibleWith(varArgsType))) {
                        passThrough = true;
                    }
                    if (!passThrough) {
                        --numParamsToCheck;
                    }
                }
                if (!considerTypeAnnotations) break block11;
                int i = 0;
                while (i < numParamsToCheck) {
                    TypeBinding expectedType = methodBinding.parameters[i];
                    Boolean specialCaseNonNullness = hasJDK15NullAnnotations ? methodBinding.parameterNonNullness[i] : null;
                    this.analyseOneArgument18(currentScope, flowContext, flowInfo, expectedType, arguments[i], specialCaseNonNullness, methodBinding.original().parameters[i]);
                    ++i;
                }
                if (passThrough || !(varArgsType instanceof ArrayBinding)) break block10;
                TypeBinding expectedType = ((ArrayBinding)varArgsType).elementsType();
                Boolean specialCaseNonNullness = hasJDK15NullAnnotations ? methodBinding.parameterNonNullness[varArgPos] : null;
                int i2 = numParamsToCheck;
                while (i2 < arguments.length) {
                    this.analyseOneArgument18(currentScope, flowContext, flowInfo, expectedType, arguments[i2], specialCaseNonNullness, methodBinding.original().parameters[varArgPos]);
                    ++i2;
                }
                break block10;
            }
            if (hasJDK15NullAnnotations) {
                int i = 0;
                while (i < numParamsToCheck) {
                    if (methodBinding.parameterNonNullness[i] == Boolean.TRUE) {
                        TypeBinding expectedType = methodBinding.parameters[i];
                        Expression argument = arguments[i];
                        int nullStatus = argument.nullStatus(flowInfo, flowContext);
                        if (nullStatus != 4) {
                            flowContext.recordNullityMismatch(currentScope, argument, argument.resolvedType, expectedType, flowInfo, nullStatus, null);
                        }
                    }
                    ++i;
                }
            }
        }
    }

    void analyseOneArgument18(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo, TypeBinding expectedType, Expression argument, Boolean expectedNonNullness, TypeBinding originalExpected) {
        if (argument instanceof ConditionalExpression && argument.isPolyExpression()) {
            ConditionalExpression ce = (ConditionalExpression)argument;
            ce.internalAnalyseOneArgument18(currentScope, flowContext, expectedType, ce.valueIfTrue, flowInfo, ce.ifTrueNullStatus, expectedNonNullness, originalExpected);
            ce.internalAnalyseOneArgument18(currentScope, flowContext, expectedType, ce.valueIfFalse, flowInfo, ce.ifFalseNullStatus, expectedNonNullness, originalExpected);
            return;
        }
        if (argument instanceof SwitchExpression && argument.isPolyExpression()) {
            SwitchExpression se = (SwitchExpression)argument;
            int i = 0;
            while (i < se.resultExpressions.size()) {
                se.internalAnalyseOneArgument18(currentScope, flowContext, expectedType, se.resultExpressions.get(i), flowInfo, se.resultExpressionNullStatus.get(i), expectedNonNullness, originalExpected);
                ++i;
            }
            return;
        }
        int nullStatus = argument.nullStatus(flowInfo, flowContext);
        this.internalAnalyseOneArgument18(currentScope, flowContext, expectedType, argument, flowInfo, nullStatus, expectedNonNullness, originalExpected);
    }

    void internalAnalyseOneArgument18(BlockScope currentScope, FlowContext flowContext, TypeBinding expectedType, Expression argument, FlowInfo flowInfo, int nullStatus, Boolean expectedNonNullness, TypeBinding originalExpected) {
        int statusFromAnnotatedNull = expectedNonNullness == Boolean.TRUE ? nullStatus : 0;
        NullAnnotationMatching annotationStatus = NullAnnotationMatching.analyse(expectedType, argument.resolvedType, nullStatus);
        if (!annotationStatus.isAnyMismatch() && statusFromAnnotatedNull != 0) {
            expectedType = originalExpected;
        }
        if (statusFromAnnotatedNull == 2) {
            currentScope.problemReporter().nullityMismatchingTypeAnnotation(argument, argument.resolvedType, expectedType, annotationStatus);
        } else if (annotationStatus.isAnyMismatch() || (statusFromAnnotatedNull & 0x10) != 0) {
            if (!expectedType.hasNullTypeAnnotations() && expectedNonNullness == Boolean.TRUE) {
                LookupEnvironment env = currentScope.environment();
                expectedType = env.createAnnotatedType(expectedType, new AnnotationBinding[]{env.getNonNullAnnotation()});
            }
            flowContext.recordNullityMismatch(currentScope, argument, argument.resolvedType, expectedType, flowInfo, nullStatus, annotationStatus);
        }
    }

    void checkAgainstNullAnnotation(BlockScope scope, FlowContext flowContext, FlowInfo flowInfo, Expression expr) {
        long tagBits;
        int nullStatus = expr.nullStatus(flowInfo, flowContext);
        MethodBinding methodBinding = null;
        boolean useTypeAnnotations = scope.environment().usesNullTypeAnnotations();
        try {
            methodBinding = scope.methodScope().referenceMethodBinding();
            tagBits = useTypeAnnotations ? methodBinding.returnType.tagBits : methodBinding.tagBits;
        }
        catch (NullPointerException nullPointerException) {
            return;
        }
        if (useTypeAnnotations) {
            this.checkAgainstNullTypeAnnotation(scope, methodBinding.returnType, expr, flowContext, flowInfo);
        } else if (nullStatus != 4 && (tagBits & 0x100000000000000L) != 0L) {
            flowContext.recordNullityMismatch(scope, expr, expr.resolvedType, methodBinding.returnType, flowInfo, nullStatus, null);
        }
    }

    protected void checkAgainstNullTypeAnnotation(BlockScope scope, TypeBinding requiredType, Expression expression, FlowContext flowContext, FlowInfo flowInfo) {
        if (expression instanceof ConditionalExpression && expression.isPolyExpression()) {
            ConditionalExpression ce = (ConditionalExpression)expression;
            this.internalCheckAgainstNullTypeAnnotation(scope, requiredType, ce.valueIfTrue, ce.ifTrueNullStatus, flowContext, flowInfo);
            this.internalCheckAgainstNullTypeAnnotation(scope, requiredType, ce.valueIfFalse, ce.ifFalseNullStatus, flowContext, flowInfo);
            return;
        }
        if (expression instanceof SwitchExpression && expression.isPolyExpression()) {
            SwitchExpression se = (SwitchExpression)expression;
            int i = 0;
            while (i < se.resultExpressions.size()) {
                this.internalCheckAgainstNullTypeAnnotation(scope, requiredType, se.resultExpressions.get(i), se.resultExpressionNullStatus.get(i), flowContext, flowInfo);
                ++i;
            }
            return;
        }
        int nullStatus = expression.nullStatus(flowInfo, flowContext);
        this.internalCheckAgainstNullTypeAnnotation(scope, requiredType, expression, nullStatus, flowContext, flowInfo);
    }

    private void internalCheckAgainstNullTypeAnnotation(BlockScope scope, TypeBinding requiredType, Expression expression, int nullStatus, FlowContext flowContext, FlowInfo flowInfo) {
        NullAnnotationMatching annotationStatus = NullAnnotationMatching.analyse(requiredType, expression.resolvedType, null, null, nullStatus, expression, NullAnnotationMatching.CheckMode.COMPATIBLE);
        if (annotationStatus.isDefiniteMismatch()) {
            scope.problemReporter().nullityMismatchingTypeAnnotation(expression, expression.resolvedType, requiredType, annotationStatus);
        } else {
            if (annotationStatus.wantToReport()) {
                annotationStatus.report(scope);
            }
            if (annotationStatus.isUnchecked()) {
                flowContext.recordNullityMismatch(scope, expression, expression.resolvedType, requiredType, flowInfo, nullStatus, annotationStatus);
            }
        }
    }

    public void branchChainTo(BranchLabel label) {
    }

    public boolean breaksOut(final char[] label) {
        return new ASTVisitor(){
            boolean breaksOut;

            @Override
            public boolean visit(TypeDeclaration type, BlockScope skope) {
                return label != null;
            }

            @Override
            public boolean visit(TypeDeclaration type, ClassScope skope) {
                return label != null;
            }

            @Override
            public boolean visit(LambdaExpression lambda, BlockScope skope) {
                return label != null;
            }

            @Override
            public boolean visit(WhileStatement whileStatement, BlockScope skope) {
                return label != null;
            }

            @Override
            public boolean visit(DoStatement doStatement, BlockScope skope) {
                return label != null;
            }

            @Override
            public boolean visit(ForeachStatement foreachStatement, BlockScope skope) {
                return label != null;
            }

            @Override
            public boolean visit(ForStatement forStatement, BlockScope skope) {
                return label != null;
            }

            @Override
            public boolean visit(SwitchStatement switchStatement, BlockScope skope) {
                return label != null;
            }

            @Override
            public boolean visit(BreakStatement breakStatement, BlockScope skope) {
                if (label == null || CharOperation.equals(label, breakStatement.label)) {
                    this.breaksOut = true;
                }
                return false;
            }

            @Override
            public boolean visit(YieldStatement yieldStatement, BlockScope skope) {
                return false;
            }

            public boolean breaksOut() {
                Statement.this.traverse(this, null);
                return this.breaksOut;
            }
        }.breaksOut();
    }

    public boolean continuesAtOuterLabel() {
        return new ASTVisitor(){
            boolean continuesToLabel;

            @Override
            public boolean visit(ContinueStatement continueStatement, BlockScope skope) {
                if (continueStatement.label != null) {
                    this.continuesToLabel = true;
                }
                return false;
            }

            public boolean continuesAtOuterLabel() {
                Statement.this.traverse(this, null);
                return this.continuesToLabel;
            }
        }.continuesAtOuterLabel();
    }

    public int complainIfUnreachable(FlowInfo flowInfo, BlockScope scope, int previousComplaintLevel, boolean endOfBlock) {
        if ((flowInfo.reachMode() & 3) != 0) {
            if ((flowInfo.reachMode() & 1) != 0) {
                this.bits &= Integer.MAX_VALUE;
            }
            if (flowInfo == FlowInfo.DEAD_END) {
                if (previousComplaintLevel < 2) {
                    if (!this.doNotReportUnreachable()) {
                        scope.problemReporter().unreachableCode(this);
                    }
                    if (endOfBlock) {
                        scope.checkUnclosedCloseables(flowInfo, null, null, null);
                    }
                }
                return 2;
            }
            if (previousComplaintLevel < 1) {
                scope.problemReporter().fakeReachable(this);
                if (endOfBlock) {
                    scope.checkUnclosedCloseables(flowInfo, null, null, null);
                }
            }
            return 1;
        }
        return previousComplaintLevel;
    }

    protected boolean doNotReportUnreachable() {
        return false;
    }

    public void generateArguments(MethodBinding binding, Expression[] arguments, BlockScope currentScope, CodeStream codeStream) {
        block10: {
            block9: {
                int argLength;
                if (!binding.isVarargs()) break block9;
                TypeBinding[] params = binding.parameters;
                int paramLength = params.length;
                int varArgIndex = paramLength - 1;
                int i = 0;
                while (i < varArgIndex) {
                    arguments[i].generateCode(currentScope, codeStream, true);
                    ++i;
                }
                ArrayBinding varArgsType = (ArrayBinding)params[varArgIndex];
                ArrayBinding codeGenVarArgsType = (ArrayBinding)binding.parameters[varArgIndex].erasure();
                int elementsTypeID = varArgsType.elementsType().id;
                int n = argLength = arguments == null ? 0 : arguments.length;
                if (argLength > paramLength) {
                    codeStream.generateInlinedValue(argLength - varArgIndex);
                    codeStream.newArray(codeGenVarArgsType);
                    int i2 = varArgIndex;
                    while (i2 < argLength) {
                        codeStream.dup();
                        codeStream.generateInlinedValue(i2 - varArgIndex);
                        arguments[i2].generateCode(currentScope, codeStream, true);
                        codeStream.arrayAtPut(elementsTypeID, false);
                        ++i2;
                    }
                } else if (argLength == paramLength) {
                    TypeBinding lastType = arguments[varArgIndex].resolvedType;
                    if (lastType == TypeBinding.NULL || varArgsType.dimensions() == lastType.dimensions() && lastType.isCompatibleWith(codeGenVarArgsType)) {
                        arguments[varArgIndex].generateCode(currentScope, codeStream, true);
                    } else {
                        codeStream.generateInlinedValue(1);
                        codeStream.newArray(codeGenVarArgsType);
                        codeStream.dup();
                        codeStream.generateInlinedValue(0);
                        arguments[varArgIndex].generateCode(currentScope, codeStream, true);
                        codeStream.arrayAtPut(elementsTypeID, false);
                    }
                } else {
                    codeStream.generateInlinedValue(0);
                    codeStream.newArray(codeGenVarArgsType);
                }
                break block10;
            }
            if (arguments == null) break block10;
            int i = 0;
            int max = arguments.length;
            while (i < max) {
                arguments[i].generateCode(currentScope, codeStream, true);
                ++i;
            }
        }
    }

    public abstract void generateCode(BlockScope var1, CodeStream var2);

    public boolean isBoxingCompatible(TypeBinding expressionType, TypeBinding targetType, Expression expression, Scope scope) {
        if (scope.isBoxingCompatibleWith(expressionType, targetType)) {
            return true;
        }
        return expressionType.isBaseType() && !targetType.isBaseType() && !targetType.isTypeVariable() && scope.compilerOptions().sourceLevel >= 0x310000L && (targetType.id == 26 || targetType.id == 27 || targetType.id == 28) && expression.isConstantValueOfTypeAssignableToType(expressionType, scope.environment().computeBoxingType(targetType));
    }

    public boolean isEmptyBlock() {
        return false;
    }

    public boolean isValidJavaStatement() {
        return true;
    }

    @Override
    public StringBuffer print(int indent, StringBuffer output) {
        return this.printStatement(indent, output);
    }

    public abstract StringBuffer printStatement(int var1, StringBuffer var2);

    public abstract void resolve(BlockScope var1);

    public LocalVariableBinding[] getPatternVariablesWhenTrue() {
        return this.patternVarsWhenTrue;
    }

    public LocalVariableBinding[] getPatternVariablesWhenFalse() {
        return this.patternVarsWhenFalse;
    }

    public void addPatternVariablesWhenTrue(LocalVariableBinding[] vars) {
        this.patternVarsWhenTrue = this.addPatternVariables(this.patternVarsWhenTrue, vars);
    }

    public void addPatternVariablesWhenFalse(LocalVariableBinding[] vars) {
        this.patternVarsWhenFalse = this.addPatternVariables(this.patternVarsWhenFalse, vars);
    }

    private LocalVariableBinding[] addPatternVariables(LocalVariableBinding[] current, LocalVariableBinding[] add) {
        if (add == null || add.length == 0) {
            return current;
        }
        if (current == null) {
            current = add;
        } else {
            LocalVariableBinding[] localVariableBindingArray = add;
            int n = add.length;
            int n2 = 0;
            while (n2 < n) {
                LocalVariableBinding local = localVariableBindingArray[n2];
                current = this.addPatternVariables(current, local);
                ++n2;
            }
        }
        return current;
    }

    private LocalVariableBinding[] addPatternVariables(LocalVariableBinding[] current, LocalVariableBinding add) {
        int oldSize = current.length;
        if (oldSize > 0 && current[oldSize - 1] == add) {
            return current;
        }
        int newLength = current.length + 1;
        LocalVariableBinding[] localVariableBindingArray = current;
        current = new LocalVariableBinding[newLength];
        System.arraycopy(localVariableBindingArray, 0, current, 0, oldSize);
        current[oldSize] = add;
        return current;
    }

    public void promotePatternVariablesIfApplicable(LocalVariableBinding[] patternVariablesInScope, BooleanSupplier condition) {
        if (patternVariablesInScope != null && condition.getAsBoolean()) {
            LocalVariableBinding[] localVariableBindingArray = patternVariablesInScope;
            int n = patternVariablesInScope.length;
            int n2 = 0;
            while (n2 < n) {
                LocalVariableBinding binding = localVariableBindingArray[n2];
                binding.modifiers &= 0xEFFFFFFF;
                ++n2;
            }
        }
    }

    public void resolveWithPatternVariablesInScope(LocalVariableBinding[] patternVariablesInScope, BlockScope scope) {
        if (patternVariablesInScope != null) {
            LocalVariableBinding binding;
            LocalVariableBinding[] localVariableBindingArray = patternVariablesInScope;
            int n = patternVariablesInScope.length;
            int n2 = 0;
            while (n2 < n) {
                binding = localVariableBindingArray[n2];
                binding.modifiers &= 0xEFFFFFFF;
                ++n2;
            }
            this.resolve(scope);
            localVariableBindingArray = patternVariablesInScope;
            n = patternVariablesInScope.length;
            n2 = 0;
            while (n2 < n) {
                binding = localVariableBindingArray[n2];
                binding.modifiers |= 0x10000000;
                ++n2;
            }
        } else {
            this.resolve(scope);
        }
    }

    public Constant[] resolveCase(BlockScope scope, TypeBinding testType, SwitchStatement switchStatement) {
        this.resolve(scope);
        return new Constant[]{Constant.NotAConstant};
    }

    public TypeBinding resolveExpressionType(BlockScope scope) {
        return null;
    }

    public boolean containsPatternVariable() {
        return false;
    }

    public TypeBinding invocationTargetType() {
        return null;
    }

    public TypeBinding expectedType() {
        return this.invocationTargetType();
    }

    public ExpressionContext getExpressionContext() {
        return ExpressionContext.VANILLA_CONTEXT;
    }

    protected MethodBinding findConstructorBinding(BlockScope scope, Invocation site, ReferenceBinding receiverType, TypeBinding[] argumentTypes) {
        MethodBinding ctorBinding = scope.getConstructor(receiverType, argumentTypes, site);
        return Statement.resolvePolyExpressionArguments(site, ctorBinding, argumentTypes, scope);
    }
}

