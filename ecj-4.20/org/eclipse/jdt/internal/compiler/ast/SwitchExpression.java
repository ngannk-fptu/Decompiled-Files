/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.ast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.ast.Assignment;
import org.eclipse.jdt.internal.compiler.ast.Block;
import org.eclipse.jdt.internal.compiler.ast.BreakStatement;
import org.eclipse.jdt.internal.compiler.ast.CaseStatement;
import org.eclipse.jdt.internal.compiler.ast.CastExpression;
import org.eclipse.jdt.internal.compiler.ast.ContinueStatement;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.ExpressionContext;
import org.eclipse.jdt.internal.compiler.ast.IPolyExpression;
import org.eclipse.jdt.internal.compiler.ast.LabeledStatement;
import org.eclipse.jdt.internal.compiler.ast.LambdaExpression;
import org.eclipse.jdt.internal.compiler.ast.LocalDeclaration;
import org.eclipse.jdt.internal.compiler.ast.MessageSend;
import org.eclipse.jdt.internal.compiler.ast.NullAnnotationMatching;
import org.eclipse.jdt.internal.compiler.ast.ReturnStatement;
import org.eclipse.jdt.internal.compiler.ast.Statement;
import org.eclipse.jdt.internal.compiler.ast.SwitchStatement;
import org.eclipse.jdt.internal.compiler.ast.ThrowStatement;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
import org.eclipse.jdt.internal.compiler.flow.FlowContext;
import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.lookup.BaseTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.PolyTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;

public class SwitchExpression
extends SwitchStatement
implements IPolyExpression {
    TypeBinding expectedType;
    private ExpressionContext expressionContext = ExpressionContext.VANILLA_CONTEXT;
    private boolean isPolyExpression = false;
    private TypeBinding[] originalValueResultExpressionTypes;
    private TypeBinding[] finalValueResultExpressionTypes;
    private int nullStatus = 1;
    public List<Expression> resultExpressions;
    public boolean resolveAll;
    List<Integer> resultExpressionNullStatus;
    LocalVariableBinding hiddenYield;
    int hiddenYieldResolvedPosition = -1;
    public boolean containsTry = false;
    private static Map<TypeBinding, TypeBinding[]> type_map;
    static final char[] SECRET_YIELD_VALUE_NAME;
    int yieldResolvedPosition = -1;
    List<LocalVariableBinding> typesOnStack;

    static {
        SECRET_YIELD_VALUE_NAME = " yieldValue".toCharArray();
        type_map = new HashMap<TypeBinding, TypeBinding[]>();
        type_map.put(TypeBinding.CHAR, new TypeBinding[]{TypeBinding.CHAR, TypeBinding.INT});
        type_map.put(TypeBinding.SHORT, new TypeBinding[]{TypeBinding.SHORT, TypeBinding.BYTE, TypeBinding.INT});
        type_map.put(TypeBinding.BYTE, new TypeBinding[]{TypeBinding.BYTE, TypeBinding.INT});
    }

    @Override
    public void setExpressionContext(ExpressionContext context) {
        this.expressionContext = context;
    }

    @Override
    public void setExpectedType(TypeBinding expectedType) {
        this.expectedType = expectedType;
    }

    @Override
    public ExpressionContext getExpressionContext() {
        return this.expressionContext;
    }

    @Override
    protected boolean ignoreMissingDefaultCase(CompilerOptions compilerOptions, boolean isEnumSwitch) {
        return isEnumSwitch;
    }

    @Override
    protected void reportMissingEnumConstantCase(BlockScope upperScope, FieldBinding enumConstant) {
        upperScope.problemReporter().missingEnumConstantCase(this, enumConstant);
    }

    @Override
    protected int getFallThroughState(Statement stmt, BlockScope blockScope) {
        Block block;
        if (stmt instanceof Expression && ((Expression)stmt).isTrulyExpression() || stmt instanceof ThrowStatement) {
            return 3;
        }
        if (this.switchLabeledRules && stmt instanceof Block && !(block = (Block)stmt).canCompleteNormally()) {
            return 3;
        }
        return 1;
    }

    @Override
    public boolean checkNPE(BlockScope skope, FlowContext flowContext, FlowInfo flowInfo, int ttlForFieldCheck) {
        if ((this.nullStatus & 2) != 0) {
            skope.problemReporter().expressionNullReference(this);
        } else if ((this.nullStatus & 0x10) != 0) {
            skope.problemReporter().expressionPotentialNullReference(this);
        }
        return true;
    }

    private void computeNullStatus(FlowInfo flowInfo, FlowContext flowContext) {
        int status;
        boolean precomputed;
        boolean bl = precomputed = this.resultExpressionNullStatus.size() > 0;
        if (!precomputed) {
            this.resultExpressionNullStatus.add(this.resultExpressions.get(0).nullStatus(flowInfo, flowContext));
        }
        int combinedStatus = status = this.resultExpressions.get(0).nullStatus(flowInfo, flowContext);
        boolean identicalStatus = true;
        int i = 1;
        int l = this.resultExpressions.size();
        while (i < l) {
            int tmp;
            if (!precomputed) {
                this.resultExpressionNullStatus.add(this.resultExpressions.get(i).nullStatus(flowInfo, flowContext));
            }
            identicalStatus &= status == (tmp = this.resultExpressions.get(i).nullStatus(flowInfo, flowContext));
            combinedStatus |= tmp;
            ++i;
        }
        if (identicalStatus) {
            this.nullStatus = status;
            return;
        }
        status = Expression.computeNullStatus(0, combinedStatus);
        if (status > 0) {
            this.nullStatus = status;
        }
    }

    @Override
    protected void completeNormallyCheck(BlockScope blockScope) {
        int sz;
        int n = sz = this.statements != null ? this.statements.length : 0;
        if (sz == 0) {
            return;
        }
        if (this.switchLabeledRules) {
            Statement[] statementArray = this.statements;
            int n2 = this.statements.length;
            int n3 = 0;
            while (n3 < n2) {
                Statement stmt = statementArray[n3];
                if (stmt instanceof Block && stmt.canCompleteNormally()) {
                    blockScope.problemReporter().switchExpressionLastStatementCompletesNormally(stmt);
                }
                ++n3;
            }
            return;
        }
        Statement lastNonCaseStmt = null;
        Statement firstTrailingCaseStmt = null;
        int i = sz - 1;
        while (i >= 0) {
            Statement stmt = this.statements[sz - 1];
            if (!(stmt instanceof CaseStatement)) {
                lastNonCaseStmt = stmt;
                break;
            }
            firstTrailingCaseStmt = stmt;
            --i;
        }
        if (lastNonCaseStmt != null) {
            if (lastNonCaseStmt.canCompleteNormally()) {
                blockScope.problemReporter().switchExpressionLastStatementCompletesNormally(lastNonCaseStmt);
            } else if (lastNonCaseStmt instanceof ContinueStatement || lastNonCaseStmt instanceof ReturnStatement) {
                blockScope.problemReporter().switchExpressionIllegalLastStatement(lastNonCaseStmt);
            }
        }
        if (firstTrailingCaseStmt != null) {
            blockScope.problemReporter().switchExpressionTrailingSwitchLabels(firstTrailingCaseStmt);
        }
    }

    @Override
    protected boolean needToCheckFlowInAbsenceOfDefaultBranch() {
        return !this.switchLabeledRules;
    }

    @Override
    public Expression[] getPolyExpressions() {
        ArrayList<Expression> polys = new ArrayList<Expression>();
        for (Expression e : this.resultExpressions) {
            Expression[] ea = e.getPolyExpressions();
            if (ea == null || ea.length == 0) continue;
            polys.addAll(Arrays.asList(ea));
        }
        return polys.toArray(new Expression[0]);
    }

    @Override
    public boolean isPertinentToApplicability(TypeBinding targetType, MethodBinding method) {
        for (Expression e : this.resultExpressions) {
            if (e.isPertinentToApplicability(targetType, method)) continue;
            return false;
        }
        return true;
    }

    @Override
    public boolean isPotentiallyCompatibleWith(TypeBinding targetType, Scope scope1) {
        for (Expression e : this.resultExpressions) {
            if (e.isPotentiallyCompatibleWith(targetType, scope1)) continue;
            return false;
        }
        return true;
    }

    @Override
    public boolean isFunctionalType() {
        for (Expression e : this.resultExpressions) {
            if (!e.isFunctionalType()) continue;
            return true;
        }
        return false;
    }

    @Override
    public int nullStatus(FlowInfo flowInfo, FlowContext flowContext) {
        if ((this.implicitConversion & 0x200) != 0) {
            return 4;
        }
        return this.nullStatus;
    }

    @Override
    protected void statementGenerateCode(BlockScope currentScope, CodeStream codeStream, Statement statement) {
        if (!(statement instanceof Expression) || !((Expression)statement).isTrulyExpression() || statement instanceof Assignment || statement instanceof MessageSend || statement instanceof SwitchStatement && !(statement instanceof SwitchExpression)) {
            super.statementGenerateCode(currentScope, codeStream, statement);
            return;
        }
        Expression expression1 = (Expression)statement;
        expression1.generateCode(currentScope, codeStream, true);
    }

    private TypeBinding createType(int typeId) {
        TypeBinding type = TypeBinding.wellKnownType(this.scope, typeId);
        return type != null ? type : this.scope.getJavaLangObject();
    }

    private LocalVariableBinding addTypeStackVariable(CodeStream codeStream, TypeBinding type, int typeId, int index, int resolvedPosition) {
        char[] name = CharOperation.concat(SECRET_YIELD_VALUE_NAME, String.valueOf(index).toCharArray());
        type = type != null ? type : this.createType(typeId);
        LocalVariableBinding lvb = new LocalVariableBinding(name, type, 0, false);
        lvb.setConstant(Constant.NotAConstant);
        lvb.useFlag = 1;
        lvb.resolvedPosition = resolvedPosition;
        this.scope.addLocalVariable(lvb);
        lvb.declaration = new LocalDeclaration(name, 0, 0);
        return lvb;
    }

    private int getNextOffset(LocalVariableBinding local) {
        int delta = TypeBinding.equalsEquals(local.type, TypeBinding.LONG) || TypeBinding.equalsEquals(local.type, TypeBinding.DOUBLE) ? 2 : 1;
        return local.resolvedPosition + delta;
    }

    private void processTypesBindingsOnStack(CodeStream codeStream) {
        int count = 0;
        int nextResolvedPosition = this.scope.offset;
        if (!codeStream.switchSaveTypeBindings.empty()) {
            this.typesOnStack = new ArrayList<LocalVariableBinding>();
            int index = 0;
            Stack<TypeBinding> typeStack = new Stack<TypeBinding>();
            int sz = codeStream.switchSaveTypeBindings.size();
            int i = codeStream.lastSwitchCumulativeSyntheticVars;
            while (i < sz) {
                typeStack.add((TypeBinding)codeStream.switchSaveTypeBindings.get(i));
                ++i;
            }
            while (!typeStack.empty()) {
                TypeBinding type = (TypeBinding)typeStack.pop();
                LocalVariableBinding lvb = this.addTypeStackVariable(codeStream, type, 0, index++, nextResolvedPosition);
                nextResolvedPosition = this.getNextOffset(lvb);
                this.typesOnStack.add(lvb);
                codeStream.store(lvb, false);
                codeStream.addVariable(lvb);
                ++count;
            }
        }
        this.yieldResolvedPosition = nextResolvedPosition;
        int n = TypeBinding.equalsEquals(this.resolvedType, TypeBinding.LONG) || TypeBinding.equalsEquals(this.resolvedType, TypeBinding.DOUBLE) ? 2 : 1;
        codeStream.lastSwitchCumulativeSyntheticVars += count + 1;
        int delta = (nextResolvedPosition += n) - this.scope.offset;
        this.scope.adjustLocalVariablePositions(delta, false);
    }

    public void loadStoredTypesAndKeep(CodeStream codeStream) {
        List<LocalVariableBinding> tos = this.typesOnStack;
        int sz = tos != null ? tos.size() : 0;
        codeStream.clearTypeBindingStack();
        int index = sz - 1;
        while (index >= 0) {
            LocalVariableBinding lvb = tos.get(index--);
            codeStream.load(lvb);
        }
    }

    private void removeStoredTypes(CodeStream codeStream) {
        List<LocalVariableBinding> tos = this.typesOnStack;
        int sz = tos != null ? tos.size() : 0;
        int index = sz - 1;
        while (index >= 0) {
            LocalVariableBinding lvb = tos.get(index--);
            codeStream.removeVariable(lvb);
        }
    }

    @Override
    public void generateCode(BlockScope currentScope, CodeStream codeStream, boolean valueRequired) {
        int tmp = 0;
        if (this.containsTry) {
            tmp = codeStream.lastSwitchCumulativeSyntheticVars;
            this.processTypesBindingsOnStack(codeStream);
        }
        super.generateCode(currentScope, codeStream);
        if (this.containsTry) {
            this.removeStoredTypes(codeStream);
            codeStream.lastSwitchCumulativeSyntheticVars = tmp;
        }
        if (!valueRequired) {
            switch (this.postConversionType((Scope)currentScope).id) {
                case 7: 
                case 8: {
                    codeStream.pop2();
                    break;
                }
                case 6: {
                    break;
                }
                default: {
                    codeStream.pop();
                    break;
                }
            }
        } else if (!this.isPolyExpression()) {
            codeStream.generateImplicitConversion(this.implicitConversion);
        }
    }

    protected boolean computeConversions(BlockScope blockScope, TypeBinding targetType) {
        boolean ok = true;
        int i = 0;
        int l = this.resultExpressions.size();
        while (i < l) {
            ok &= this.computeConversionsResultExpressions(blockScope, targetType, this.originalValueResultExpressionTypes[i], this.resultExpressions.get(i));
            ++i;
        }
        return ok;
    }

    private boolean computeConversionsResultExpressions(BlockScope blockScope, TypeBinding targetType, TypeBinding resultExpressionType, Expression resultExpression) {
        if (resultExpressionType != null && resultExpressionType.isValidBinding()) {
            if (resultExpression.isConstantValueOfTypeAssignableToType(resultExpressionType, targetType) || resultExpressionType.isCompatibleWith(targetType)) {
                resultExpression.computeConversion(blockScope, targetType, resultExpressionType);
                if (resultExpressionType.needsUncheckedConversion(targetType)) {
                    blockScope.problemReporter().unsafeTypeConversion(resultExpression, resultExpressionType, targetType);
                }
                if (resultExpression instanceof CastExpression && (resultExpression.bits & 0x4020) == 0) {
                    CastExpression.checkNeedForAssignedCast(blockScope, targetType, (CastExpression)resultExpression);
                }
            } else if (this.isBoxingCompatible(resultExpressionType, targetType, resultExpression, blockScope)) {
                resultExpression.computeConversion(blockScope, targetType, resultExpressionType);
                if (resultExpression instanceof CastExpression && (resultExpression.bits & 0x4020) == 0) {
                    CastExpression.checkNeedForAssignedCast(blockScope, targetType, (CastExpression)resultExpression);
                }
            } else {
                blockScope.problemReporter().typeMismatchError(resultExpressionType, targetType, resultExpression, null);
                return false;
            }
        }
        return true;
    }

    @Override
    public TypeBinding resolveType(BlockScope upperScope) {
        return this.resolveTypeInternal(upperScope);
    }

    public TypeBinding resolveTypeInternal(BlockScope upperScope) {
        try {
            int resultExpressionsCount;
            if (this.constant != Constant.NotAConstant) {
                this.constant = Constant.NotAConstant;
                if (this.expressionContext == ExpressionContext.ASSIGNMENT_CONTEXT || this.expressionContext == ExpressionContext.INVOCATION_CONTEXT) {
                    for (Expression e : this.resultExpressions) {
                        e.setExpressionContext(this.expressionContext);
                        e.setExpectedType(this.expectedType);
                    }
                }
                this.resolve(upperScope);
                if (this.statements == null || this.statements.length == 0) {
                    upperScope.problemReporter().switchExpressionEmptySwitchBlock(this);
                    return null;
                }
                int n = resultExpressionsCount = this.resultExpressions != null ? this.resultExpressions.size() : 0;
                if (resultExpressionsCount == 0) {
                    upperScope.problemReporter().switchExpressionNoResultExpressions(this);
                    return null;
                }
                this.traverse((ASTVisitor)new OOBLFlagger(this), upperScope);
                if (this.originalValueResultExpressionTypes == null) {
                    this.originalValueResultExpressionTypes = new TypeBinding[resultExpressionsCount];
                    this.finalValueResultExpressionTypes = new TypeBinding[resultExpressionsCount];
                    int i = 0;
                    while (i < resultExpressionsCount) {
                        this.finalValueResultExpressionTypes[i] = this.originalValueResultExpressionTypes[i] = this.resultExpressions.get((int)i).resolvedType;
                        ++i;
                    }
                }
                if (this.isPolyExpression()) {
                    if (this.expectedType == null || !this.expectedType.isProperType(true)) {
                        PolyTypeBinding polyTypeBinding = new PolyTypeBinding(this);
                        return polyTypeBinding;
                    }
                    this.resolvedType = this.computeConversions(this.scope, this.expectedType) ? this.expectedType : null;
                    TypeBinding typeBinding = this.resolvedType;
                    return typeBinding;
                }
            } else {
                int n = resultExpressionsCount = this.resultExpressions != null ? this.resultExpressions.size() : 0;
                if (resultExpressionsCount == 0) {
                    this.resolvedType = null;
                    TypeBinding typeBinding = null;
                    return typeBinding;
                }
                int i = 0;
                while (i < resultExpressionsCount) {
                    Expression resultExpr = this.resultExpressions.get(i);
                    if (resultExpr.resolvedType == null || resultExpr.resolvedType.kind() == 65540) {
                        this.finalValueResultExpressionTypes[i] = this.originalValueResultExpressionTypes[i] = resultExpr.resolveTypeExpecting(upperScope, this.expectedType);
                    }
                    if (!(this.resolveAll || resultExpr.resolvedType != null && resultExpr.resolvedType.isValidBinding())) {
                        this.resolvedType = null;
                        TypeBinding typeBinding = null;
                        return typeBinding;
                    }
                    ++i;
                }
                TypeBinding typeBinding = this.resolvedType = this.computeConversions(this.scope, this.expectedType) ? this.expectedType : null;
            }
            if (resultExpressionsCount == 1) {
                TypeBinding typeBinding = this.resolvedType = this.originalValueResultExpressionTypes[0];
                return typeBinding;
            }
            boolean typeUniformAcrossAllArms = true;
            TypeBinding tmp = this.originalValueResultExpressionTypes[0];
            int i = 1;
            int l = this.originalValueResultExpressionTypes.length;
            while (i < l) {
                TypeBinding originalType = this.originalValueResultExpressionTypes[i];
                if (originalType != null && TypeBinding.notEquals(tmp, originalType)) {
                    typeUniformAcrossAllArms = false;
                    break;
                }
                ++i;
            }
            if (typeUniformAcrossAllArms) {
                tmp = this.originalValueResultExpressionTypes[0];
                i = 1;
                while (i < resultExpressionsCount) {
                    if (this.originalValueResultExpressionTypes[i] != null) {
                        tmp = NullAnnotationMatching.moreDangerousType(tmp, this.originalValueResultExpressionTypes[i]);
                    }
                    ++i;
                }
                TypeBinding typeBinding = this.resolvedType = tmp;
                return typeBinding;
            }
            boolean typeBbolean = true;
            TypeBinding[] typeBindingArray = this.originalValueResultExpressionTypes;
            int n = this.originalValueResultExpressionTypes.length;
            int originalType = 0;
            while (originalType < n) {
                TypeBinding t = typeBindingArray[originalType];
                if (t != null) {
                    typeBbolean &= t.id == 5 || t.id == 33;
                }
                ++originalType;
            }
            LookupEnvironment env = this.scope.environment();
            if (typeBbolean) {
                int i2 = 0;
                while (i2 < resultExpressionsCount) {
                    if (this.originalValueResultExpressionTypes[i2] != null && this.originalValueResultExpressionTypes[i2].id != 5) {
                        this.finalValueResultExpressionTypes[i2] = env.computeBoxingType(this.originalValueResultExpressionTypes[i2]);
                        this.resultExpressions.get(i2).computeConversion(this.scope, this.finalValueResultExpressionTypes[i2], this.originalValueResultExpressionTypes[i2]);
                    }
                    ++i2;
                }
                this.resolvedType = TypeBinding.BOOLEAN;
                BaseTypeBinding baseTypeBinding = this.resolvedType;
                return baseTypeBinding;
            }
            boolean typeNumeric = true;
            TypeBinding resultNumeric = null;
            HashSet<TypeBinding> typeSet = new HashSet<TypeBinding>();
            int i3 = 0;
            while (i3 < resultExpressionsCount) {
                TypeBinding originalType2 = this.originalValueResultExpressionTypes[i3];
                if (originalType2 != null) {
                    TypeBinding typeBinding = tmp = originalType2.isNumericType() ? originalType2 : env.computeBoxingType(originalType2);
                    if (!tmp.isNumericType()) {
                        typeNumeric = false;
                        break;
                    }
                    typeSet.add(TypeBinding.wellKnownType(this.scope, tmp.id));
                }
                ++i3;
            }
            if (typeNumeric) {
                TypeBinding[] dfl;
                TypeBinding[] typeBindingArray2 = dfl = new TypeBinding[]{TypeBinding.DOUBLE, TypeBinding.FLOAT, TypeBinding.LONG};
                int n2 = dfl.length;
                int n3 = 0;
                while (n3 < n2) {
                    TypeBinding binding = typeBindingArray2[n3];
                    if (typeSet.contains(binding)) {
                        resultNumeric = binding;
                        break;
                    }
                    ++n3;
                }
                resultNumeric = resultNumeric != null ? resultNumeric : this.check_nonconstant_int();
                resultNumeric = resultNumeric != null ? resultNumeric : this.getResultNumeric(typeSet);
                typeSet = null;
                int i4 = 0;
                while (i4 < resultExpressionsCount) {
                    this.resultExpressions.get(i4).computeConversion(this.scope, resultNumeric, this.originalValueResultExpressionTypes[i4]);
                    this.finalValueResultExpressionTypes[i4] = resultNumeric;
                    ++i4;
                }
                TypeBinding typeBinding = this.resolvedType = resultNumeric;
                return typeBinding;
            }
            i3 = 0;
            while (i3 < resultExpressionsCount) {
                TypeBinding finalType = this.finalValueResultExpressionTypes[i3];
                if (finalType != null && finalType.isBaseType()) {
                    this.finalValueResultExpressionTypes[i3] = env.computeBoxingType(finalType);
                }
                ++i3;
            }
            TypeBinding commonType = this.scope.lowerUpperBound(this.finalValueResultExpressionTypes);
            if (commonType != null) {
                int i5 = 0;
                int l2 = this.resultExpressions.size();
                while (i5 < l2) {
                    if (this.originalValueResultExpressionTypes[i5] != null) {
                        this.resultExpressions.get(i5).computeConversion(this.scope, commonType, this.originalValueResultExpressionTypes[i5]);
                        this.finalValueResultExpressionTypes[i5] = commonType;
                    }
                    ++i5;
                }
                TypeBinding typeBinding = this.resolvedType = commonType.capture(this.scope, this.sourceStart, this.sourceEnd);
                return typeBinding;
            }
            this.scope.problemReporter().switchExpressionIncompatibleResultExpressions(this);
            return null;
        }
        finally {
            if (this.scope != null) {
                this.scope.enclosingCase = null;
            }
        }
    }

    private TypeBinding check_nonconstant_int() {
        int i = 0;
        int l = this.resultExpressions.size();
        while (i < l) {
            Expression e = this.resultExpressions.get(i);
            TypeBinding type = this.originalValueResultExpressionTypes[i];
            if (type != null && type.id == 10 && e.constant == Constant.NotAConstant) {
                return TypeBinding.INT;
            }
            ++i;
        }
        return null;
    }

    private boolean areAllIntegerResultExpressionsConvertibleToTargetType(TypeBinding targetType) {
        int i = 0;
        int l = this.resultExpressions.size();
        while (i < l) {
            Expression e = this.resultExpressions.get(i);
            TypeBinding t = this.originalValueResultExpressionTypes[i];
            if (TypeBinding.equalsEquals(t, TypeBinding.INT) && !e.isConstantValueOfTypeAssignableToType(t, targetType)) {
                return false;
            }
            ++i;
        }
        return true;
    }

    @Override
    public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo) {
        flowInfo = super.analyseCode(currentScope, flowContext, flowInfo);
        this.resultExpressionNullStatus = new ArrayList<Integer>(0);
        CompilerOptions compilerOptions = currentScope.compilerOptions();
        if (compilerOptions.enableSyntacticNullAnalysisForFields) {
            for (Expression re : this.resultExpressions) {
                this.resultExpressionNullStatus.add(re.nullStatus(flowInfo, flowContext));
                flowContext.expireNullCheckedFieldInfo();
            }
        }
        this.computeNullStatus(flowInfo, flowContext);
        return flowInfo;
    }

    @Override
    protected void addSecretTryResultVariable() {
        if (this.containsTry) {
            this.hiddenYield = new LocalVariableBinding(SECRET_YIELD_VALUE_NAME, null, 0, false);
            this.hiddenYield.setConstant(Constant.NotAConstant);
            this.hiddenYield.useFlag = 1;
            this.scope.addLocalVariable(this.hiddenYield);
            this.hiddenYield.declaration = new LocalDeclaration(SECRET_YIELD_VALUE_NAME, 0, 0);
        }
    }

    private TypeBinding check_csb(Set<TypeBinding> typeSet, TypeBinding candidate) {
        if (!typeSet.contains(candidate)) {
            return null;
        }
        TypeBinding[] allowedTypes = type_map.get(candidate);
        Set allowedSet = Arrays.stream(allowedTypes).collect(Collectors.toSet());
        if (!allowedSet.containsAll(typeSet)) {
            return null;
        }
        return this.areAllIntegerResultExpressionsConvertibleToTargetType(candidate) ? candidate : null;
    }

    private TypeBinding getResultNumeric(Set<TypeBinding> typeSet) {
        TypeBinding[] csb;
        TypeBinding[] typeBindingArray = csb = new TypeBinding[]{TypeBinding.SHORT, TypeBinding.BYTE, TypeBinding.CHAR};
        int n = csb.length;
        int n2 = 0;
        while (n2 < n) {
            TypeBinding c = typeBindingArray[n2];
            TypeBinding result = this.check_csb(typeSet, c);
            if (result != null) {
                return result;
            }
            ++n2;
        }
        return TypeBinding.INT;
    }

    @Override
    public boolean isPolyExpression() {
        if (this.isPolyExpression) {
            return true;
        }
        this.isPolyExpression = this.expressionContext == ExpressionContext.ASSIGNMENT_CONTEXT || this.expressionContext == ExpressionContext.INVOCATION_CONTEXT;
        return this.isPolyExpression;
    }

    @Override
    public boolean isTrulyExpression() {
        return true;
    }

    @Override
    public boolean isCompatibleWith(TypeBinding left, Scope skope) {
        if (!this.isPolyExpression()) {
            return super.isCompatibleWith(left, skope);
        }
        for (Expression e : this.resultExpressions) {
            if (e.isCompatibleWith(left, skope)) continue;
            return false;
        }
        return true;
    }

    @Override
    public boolean isBoxingCompatibleWith(TypeBinding targetType, Scope skope) {
        if (!this.isPolyExpression()) {
            return super.isBoxingCompatibleWith(targetType, skope);
        }
        for (Expression e : this.resultExpressions) {
            if (e.isCompatibleWith(targetType, skope) || e.isBoxingCompatibleWith(targetType, skope)) continue;
            return false;
        }
        return true;
    }

    @Override
    public boolean sIsMoreSpecific(TypeBinding s, TypeBinding t, Scope skope) {
        if (super.sIsMoreSpecific(s, t, skope)) {
            return true;
        }
        if (!this.isPolyExpression()) {
            return false;
        }
        for (Expression e : this.resultExpressions) {
            if (e.sIsMoreSpecific(s, t, skope)) continue;
            return false;
        }
        return true;
    }

    @Override
    public TypeBinding expectedType() {
        return this.expectedType;
    }

    @Override
    public void traverse(ASTVisitor visitor, BlockScope blockScope) {
        if (visitor.visit(this, blockScope)) {
            this.expression.traverse(visitor, blockScope);
            if (this.statements != null) {
                int statementsLength = this.statements.length;
                int i = 0;
                while (i < statementsLength) {
                    this.statements[i].traverse(visitor, this.scope);
                    ++i;
                }
            }
        }
        visitor.endVisit(this, blockScope);
    }

    static class OOBLFlagger
    extends ASTVisitor {
        Set<String> labelDecls = new HashSet<String>();
        Set<BreakStatement> referencedBreakLabels = new HashSet<BreakStatement>();
        Set<ContinueStatement> referencedContinueLabels = new HashSet<ContinueStatement>();

        public OOBLFlagger(SwitchExpression se) {
        }

        @Override
        public boolean visit(SwitchExpression switchExpression, BlockScope blockScope) {
            return true;
        }

        private void checkForOutofBoundLabels(BlockScope blockScope) {
            try {
                for (BreakStatement bs : this.referencedBreakLabels) {
                    if (bs.label == null || bs.label.length == 0 || this.labelDecls.contains(new String(bs.label))) continue;
                    blockScope.problemReporter().switchExpressionsBreakOutOfSwitchExpression(bs);
                }
                for (ContinueStatement cs : this.referencedContinueLabels) {
                    if (cs.label == null || cs.label.length == 0 || this.labelDecls.contains(new String(cs.label))) continue;
                    blockScope.problemReporter().switchExpressionsContinueOutOfSwitchExpression(cs);
                }
            }
            catch (EmptyStackException emptyStackException) {}
        }

        @Override
        public void endVisit(SwitchExpression switchExpression, BlockScope blockScope) {
            this.checkForOutofBoundLabels(blockScope);
        }

        @Override
        public boolean visit(BreakStatement breakStatement, BlockScope blockScope) {
            if (breakStatement.label != null && breakStatement.label.length != 0) {
                this.referencedBreakLabels.add(breakStatement);
            }
            return true;
        }

        @Override
        public boolean visit(ContinueStatement continueStatement, BlockScope blockScope) {
            if (continueStatement.label != null && continueStatement.label.length != 0) {
                this.referencedContinueLabels.add(continueStatement);
            }
            return true;
        }

        @Override
        public boolean visit(LambdaExpression lambdaExpression, BlockScope blockScope) {
            return false;
        }

        @Override
        public boolean visit(LabeledStatement stmt, BlockScope blockScope) {
            if (stmt.label != null && stmt.label.length != 0) {
                this.labelDecls.add(new String(stmt.label));
            }
            return true;
        }

        @Override
        public boolean visit(ReturnStatement stmt, BlockScope blockScope) {
            blockScope.problemReporter().switchExpressionsReturnWithinSwitchExpression(stmt);
            return false;
        }

        @Override
        public boolean visit(TypeDeclaration stmt, BlockScope blockScope) {
            return false;
        }
    }
}

