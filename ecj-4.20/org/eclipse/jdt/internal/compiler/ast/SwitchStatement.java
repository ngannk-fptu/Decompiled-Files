/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.ast;

import java.util.Arrays;
import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.ast.Block;
import org.eclipse.jdt.internal.compiler.ast.BreakStatement;
import org.eclipse.jdt.internal.compiler.ast.CaseStatement;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.Statement;
import org.eclipse.jdt.internal.compiler.ast.SwitchExpression;
import org.eclipse.jdt.internal.compiler.ast.ThrowStatement;
import org.eclipse.jdt.internal.compiler.ast.YieldStatement;
import org.eclipse.jdt.internal.compiler.codegen.BranchLabel;
import org.eclipse.jdt.internal.compiler.codegen.CaseLabel;
import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
import org.eclipse.jdt.internal.compiler.flow.FlowContext;
import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
import org.eclipse.jdt.internal.compiler.flow.SwitchFlowContext;
import org.eclipse.jdt.internal.compiler.flow.UnconditionalFlowInfo;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.SyntheticMethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;

public class SwitchStatement
extends Expression {
    public Expression expression;
    public Statement[] statements;
    public BlockScope scope;
    public int explicitDeclarations;
    public BranchLabel breakLabel;
    public CaseStatement[] cases;
    public CaseStatement defaultCase;
    public int blockStart;
    public int caseCount;
    int[] constants;
    int[] constMapping;
    String[] stringConstants;
    public boolean switchLabeledRules = false;
    public int nConstants;
    public static final int CASE = 0;
    public static final int FALLTHROUGH = 1;
    public static final int ESCAPING = 2;
    public static final int BREAKING = 3;
    private static final char[] SecretStringVariableName = " switchDispatchString".toCharArray();
    public SyntheticMethodBinding synthetic;
    int preSwitchInitStateIndex = -1;
    int mergedInitStateIndex = -1;
    CaseStatement[] duplicateCaseStatements = null;
    int duplicateCaseStatementsCounter = 0;
    private LocalVariableBinding dispatchStringCopy = null;

    protected int getFallThroughState(Statement stmt, BlockScope blockScope) {
        if (this.switchLabeledRules) {
            if (stmt instanceof Expression && ((Expression)stmt).isTrulyExpression() || stmt instanceof ThrowStatement) {
                return 3;
            }
            if (!stmt.canCompleteNormally()) {
                return 3;
            }
            if (stmt instanceof Block) {
                int l;
                Block block = (Block)stmt;
                BreakStatement breakStatement = new BreakStatement(null, block.sourceEnd - 1, block.sourceEnd);
                breakStatement.isSynthetic = true;
                int n = l = block.statements == null ? 0 : block.statements.length;
                if (l == 0) {
                    block.statements = new Statement[]{breakStatement};
                    block.scope = this.scope;
                } else {
                    Statement[] newArray = new Statement[l + 1];
                    System.arraycopy(block.statements, 0, newArray, 0, l);
                    newArray[l] = breakStatement;
                    block.statements = newArray;
                }
                return 3;
            }
        }
        return 1;
    }

    protected void completeNormallyCheck(BlockScope blockScope) {
    }

    protected boolean needToCheckFlowInAbsenceOfDefaultBranch() {
        return true;
    }

    @Override
    public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo) {
        try {
            TypeBinding resolvedTypeBinding;
            flowInfo = this.expression.analyseCode(currentScope, flowContext, flowInfo);
            if ((this.expression.implicitConversion & 0x400) != 0 || this.expression.resolvedType != null && (this.expression.resolvedType.id == 11 || this.expression.resolvedType.isEnum())) {
                this.expression.checkNPE(currentScope, flowContext, flowInfo, 1);
            }
            this.breakLabel = new BranchLabel();
            SwitchFlowContext switchContext = new SwitchFlowContext(flowContext, this, this.breakLabel, true, true);
            switchContext.isExpression = this instanceof SwitchExpression;
            FlowInfo caseInits = FlowInfo.DEAD_END;
            this.preSwitchInitStateIndex = currentScope.methodScope().recordInitializationStates(flowInfo);
            int caseIndex = 0;
            if (this.statements != null) {
                int initialComplaintLevel;
                int complaintLevel = initialComplaintLevel = (flowInfo.reachMode() & 3) != 0 ? 1 : 0;
                int fallThroughState = 0;
                int i = 0;
                int max = this.statements.length;
                while (i < max) {
                    Statement statement = this.statements[i];
                    if (caseIndex < this.caseCount && statement == this.cases[caseIndex]) {
                        this.scope.enclosingCase = this.cases[caseIndex];
                        ++caseIndex;
                        if (fallThroughState == 1 && (statement.bits & 0x20000000) == 0) {
                            this.scope.problemReporter().possibleFallThroughCase(this.scope.enclosingCase);
                        }
                        caseInits = ((FlowInfo)caseInits).mergedWith(flowInfo.unconditionalInits());
                        complaintLevel = initialComplaintLevel;
                        fallThroughState = 0;
                    } else if (statement == this.defaultCase) {
                        this.scope.enclosingCase = this.defaultCase;
                        if (fallThroughState == 1 && (statement.bits & 0x20000000) == 0) {
                            this.scope.problemReporter().possibleFallThroughCase(this.scope.enclosingCase);
                        }
                        caseInits = ((FlowInfo)caseInits).mergedWith(flowInfo.unconditionalInits());
                        complaintLevel = initialComplaintLevel;
                        fallThroughState = 0;
                    } else {
                        if (!(this instanceof SwitchExpression) && currentScope.compilerOptions().complianceLevel >= 0x3A0000L && statement instanceof YieldStatement && ((YieldStatement)statement).isImplicit) {
                            YieldStatement y = (YieldStatement)statement;
                            Expression e = ((YieldStatement)statement).expression;
                            if (!y.expression.statementExpression()) {
                                this.scope.problemReporter().invalidExpressionAsStatement(e);
                            }
                        }
                        fallThroughState = this.getFallThroughState(statement, currentScope);
                    }
                    complaintLevel = statement.complainIfUnreachable(caseInits, this.scope, complaintLevel, true);
                    if (complaintLevel < 2) {
                        if ((caseInits = statement.analyseCode(this.scope, switchContext, caseInits)) == FlowInfo.DEAD_END) {
                            fallThroughState = 2;
                        }
                        switchContext.expireNullCheckedFieldInfo();
                    }
                    ++i;
                }
                this.completeNormallyCheck(currentScope);
            }
            if ((resolvedTypeBinding = this.expression.resolvedType).isEnum()) {
                SourceTypeBinding sourceTypeBinding = currentScope.classScope().referenceContext.binding;
                this.synthetic = sourceTypeBinding.addSyntheticMethodForSwitchEnum(resolvedTypeBinding, this);
            }
            if (this.defaultCase == null && this.needToCheckFlowInAbsenceOfDefaultBranch()) {
                flowInfo.addPotentialInitializationsFrom(((FlowInfo)caseInits).mergedWith(switchContext.initsOnBreak));
                this.mergedInitStateIndex = currentScope.methodScope().recordInitializationStates(flowInfo);
                FlowInfo flowInfo2 = flowInfo;
                return flowInfo2;
            }
            UnconditionalFlowInfo mergedInfo = ((FlowInfo)caseInits).mergedWith(switchContext.initsOnBreak);
            this.mergedInitStateIndex = currentScope.methodScope().recordInitializationStates(mergedInfo);
            UnconditionalFlowInfo unconditionalFlowInfo = mergedInfo;
            return unconditionalFlowInfo;
        }
        finally {
            if (this.scope != null) {
                this.scope.enclosingCase = null;
            }
        }
    }

    public void generateCodeForStringSwitch(BlockScope currentScope, CodeStream codeStream) {
        try {
            BranchLabel[] sourceCaseLabels;
            int max;
            int i;
            int constSize;
            if ((this.bits & Integer.MIN_VALUE) == 0) {
                return;
            }
            int pc = codeStream.position;
            boolean hasCases = this.caseCount != 0;
            int n = constSize = hasCases ? this.stringConstants.length : 0;
            if (currentScope.compilerOptions().complianceLevel >= 0x380000L) {
                i = 0;
                max = this.caseCount;
                while (i < max) {
                    int l = this.cases[i].constantExpressions.length;
                    this.cases[i].targetLabels = new BranchLabel[l];
                    ++i;
                }
                sourceCaseLabels = new BranchLabel[this.nConstants];
                int j = 0;
                int i2 = 0;
                int max2 = this.caseCount;
                while (i2 < max2) {
                    CaseStatement stmt = this.cases[i2];
                    int k = 0;
                    int l = stmt.constantExpressions.length;
                    while (k < l) {
                        stmt.targetLabels[k] = sourceCaseLabels[j] = new BranchLabel(codeStream);
                        sourceCaseLabels[j++].tagBits |= 2;
                        ++k;
                    }
                    ++i2;
                }
            } else {
                sourceCaseLabels = new BranchLabel[this.caseCount];
                i = 0;
                max = this.caseCount;
                while (i < max) {
                    this.cases[i].targetLabel = sourceCaseLabels[i] = new BranchLabel(codeStream);
                    sourceCaseLabels[i].tagBits |= 2;
                    ++i;
                }
            }
            class StringSwitchCase
            implements Comparable {
                int hashCode;
                String string;
                BranchLabel label;

                public StringSwitchCase(int hashCode, String string, BranchLabel label) {
                    this.hashCode = hashCode;
                    this.string = string;
                    this.label = label;
                }

                public int compareTo(Object o) {
                    StringSwitchCase that = (StringSwitchCase)o;
                    if (this.hashCode == that.hashCode) {
                        return 0;
                    }
                    if (this.hashCode > that.hashCode) {
                        return 1;
                    }
                    return -1;
                }

                public String toString() {
                    return "StringSwitchCase :\ncase " + this.hashCode + ":(" + this.string + ")\n";
                }
            }
            Object[] stringCases = new StringSwitchCase[constSize];
            CaseLabel[] hashCodeCaseLabels = new CaseLabel[constSize];
            this.constants = new int[constSize];
            int i3 = 0;
            while (i3 < constSize) {
                stringCases[i3] = new StringSwitchCase(this.stringConstants[i3].hashCode(), this.stringConstants[i3], sourceCaseLabels[this.constMapping[i3]]);
                hashCodeCaseLabels[i3] = new CaseLabel(codeStream);
                hashCodeCaseLabels[i3].tagBits |= 2;
                ++i3;
            }
            Arrays.sort(stringCases);
            int uniqHashCount = 0;
            int lastHashCode = 0;
            int i4 = 0;
            int length = constSize;
            while (i4 < length) {
                int hashCode = ((StringSwitchCase)stringCases[i4]).hashCode;
                if (i4 == 0 || hashCode != lastHashCode) {
                    int n2 = uniqHashCount++;
                    int n3 = hashCode;
                    this.constants[n2] = n3;
                    lastHashCode = n3;
                }
                ++i4;
            }
            if (uniqHashCount != constSize) {
                this.constants = new int[uniqHashCount];
                System.arraycopy(this.constants, 0, this.constants, 0, uniqHashCount);
                CaseLabel[] caseLabelArray = hashCodeCaseLabels;
                hashCodeCaseLabels = new CaseLabel[uniqHashCount];
                System.arraycopy(caseLabelArray, 0, hashCodeCaseLabels, 0, uniqHashCount);
            }
            int[] sortedIndexes = new int[uniqHashCount];
            int i5 = 0;
            while (i5 < uniqHashCount) {
                sortedIndexes[i5] = i5;
                ++i5;
            }
            CaseLabel defaultCaseLabel = new CaseLabel(codeStream);
            defaultCaseLabel.tagBits |= 2;
            this.breakLabel.initialize(codeStream);
            BranchLabel defaultBranchLabel = new BranchLabel(codeStream);
            if (hasCases) {
                defaultBranchLabel.tagBits |= 2;
            }
            if (this.defaultCase != null) {
                this.defaultCase.targetLabel = defaultBranchLabel;
            }
            this.expression.generateCode(currentScope, codeStream, true);
            codeStream.store(this.dispatchStringCopy, true);
            codeStream.addVariable(this.dispatchStringCopy);
            codeStream.invokeStringHashCode();
            if (hasCases) {
                codeStream.lookupswitch(defaultCaseLabel, this.constants, sortedIndexes, hashCodeCaseLabels);
                int i6 = 0;
                int j = 0;
                int max3 = constSize;
                while (i6 < max3) {
                    int hashCode = ((StringSwitchCase)stringCases[i6]).hashCode;
                    if (i6 == 0 || hashCode != lastHashCode) {
                        lastHashCode = hashCode;
                        if (i6 != 0) {
                            codeStream.goto_(defaultBranchLabel);
                        }
                        hashCodeCaseLabels[j++].place();
                    }
                    codeStream.load(this.dispatchStringCopy);
                    codeStream.ldc(((StringSwitchCase)stringCases[i6]).string);
                    codeStream.invokeStringEquals();
                    codeStream.ifne(((StringSwitchCase)stringCases[i6]).label);
                    ++i6;
                }
                codeStream.goto_(defaultBranchLabel);
            } else {
                codeStream.pop();
            }
            int caseIndex = 0;
            if (this.statements != null) {
                int i7 = 0;
                int maxCases = this.statements.length;
                while (i7 < maxCases) {
                    Statement statement = this.statements[i7];
                    if (caseIndex < this.caseCount && statement == this.cases[caseIndex]) {
                        this.scope.enclosingCase = this.cases[caseIndex];
                        if (this.preSwitchInitStateIndex != -1) {
                            codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.preSwitchInitStateIndex);
                        }
                        ++caseIndex;
                    } else if (statement == this.defaultCase) {
                        defaultCaseLabel.place();
                        this.scope.enclosingCase = this.defaultCase;
                        if (this.preSwitchInitStateIndex != -1) {
                            codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.preSwitchInitStateIndex);
                        }
                    }
                    this.statementGenerateCode(currentScope, codeStream, statement);
                    ++i7;
                }
            }
            if (this.mergedInitStateIndex != -1) {
                codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.mergedInitStateIndex);
                codeStream.addDefinitelyAssignedVariables(currentScope, this.mergedInitStateIndex);
            }
            codeStream.removeVariable(this.dispatchStringCopy);
            if (this.scope != currentScope) {
                codeStream.exitUserScope(this.scope);
            }
            this.breakLabel.place();
            if (this.defaultCase == null) {
                codeStream.recordPositionsFrom(codeStream.position, this.sourceEnd, true);
                defaultCaseLabel.place();
                defaultBranchLabel.place();
            }
            if (this.expectedType() != null) {
                TypeBinding expectedType = this.expectedType().erasure();
                boolean optimizedGoto = codeStream.lastAbruptCompletion == -1;
                codeStream.recordExpressionType(expectedType, optimizedGoto ? 0 : 1, optimizedGoto);
            }
            codeStream.recordPositionsFrom(pc, this.sourceStart);
        }
        finally {
            if (this.scope != null) {
                this.scope.enclosingCase = null;
            }
        }
    }

    @Override
    public void generateCode(BlockScope currentScope, CodeStream codeStream) {
        if (this.expression.resolvedType.id == 11) {
            this.generateCodeForStringSwitch(currentScope, codeStream);
            return;
        }
        try {
            boolean isEnumSwitchWithoutDefaultCase;
            boolean hasCases;
            CaseLabel[] caseLabels;
            int max;
            int i;
            if ((this.bits & Integer.MIN_VALUE) == 0) {
                return;
            }
            int pc = codeStream.position;
            this.breakLabel.initialize(codeStream);
            int constantCount = this.constants == null ? 0 : this.constants.length;
            int nCaseLabels = 0;
            if (currentScope.compilerOptions().complianceLevel >= 0x380000L) {
                i = 0;
                max = this.caseCount;
                while (i < max) {
                    int l = this.cases[i].constantExpressions.length;
                    nCaseLabels += l;
                    this.cases[i].targetLabels = new BranchLabel[l];
                    ++i;
                }
                caseLabels = new CaseLabel[nCaseLabels];
                int j = 0;
                int i2 = 0;
                int max2 = this.caseCount;
                while (i2 < max2) {
                    CaseStatement stmt = this.cases[i2];
                    int k = 0;
                    int l = stmt.constantExpressions.length;
                    while (k < l) {
                        caseLabels[j] = new CaseLabel(codeStream);
                        stmt.targetLabels[k] = caseLabels[j];
                        caseLabels[j++].tagBits |= 2;
                        ++k;
                    }
                    ++i2;
                }
            } else {
                caseLabels = new CaseLabel[this.caseCount];
                i = 0;
                max = this.caseCount;
                while (i < max) {
                    caseLabels[i] = new CaseLabel(codeStream);
                    this.cases[i].targetLabel = caseLabels[i];
                    caseLabels[i].tagBits |= 2;
                    ++i;
                }
            }
            CaseLabel defaultLabel = new CaseLabel(codeStream);
            boolean bl = hasCases = this.caseCount != 0;
            if (hasCases) {
                defaultLabel.tagBits |= 2;
            }
            if (this.defaultCase != null) {
                this.defaultCase.targetLabel = defaultLabel;
            }
            TypeBinding resolvedType1 = this.expression.resolvedType;
            boolean valueRequired = false;
            if (resolvedType1.isEnum()) {
                codeStream.invoke((byte)-72, this.synthetic, null);
                this.expression.generateCode(currentScope, codeStream, true);
                codeStream.invokeEnumOrdinal(resolvedType1.constantPoolName());
                codeStream.iaload();
                if (!hasCases) {
                    codeStream.pop();
                }
                valueRequired = hasCases;
            } else {
                valueRequired = this.expression.constant == Constant.NotAConstant || hasCases;
                this.expression.generateCode(currentScope, codeStream, valueRequired);
            }
            if (hasCases) {
                int[] sortedIndexes = new int[constantCount];
                int i3 = 0;
                while (i3 < constantCount) {
                    sortedIndexes[i3] = i3;
                    ++i3;
                }
                int[] localKeysCopy = new int[constantCount];
                System.arraycopy(this.constants, 0, localKeysCopy, 0, constantCount);
                CodeStream.sort(localKeysCopy, 0, constantCount - 1, sortedIndexes);
                int max3 = localKeysCopy[constantCount - 1];
                int min = localKeysCopy[0];
                if ((long)((double)constantCount * 2.5) > (long)max3 - (long)min) {
                    if (max3 > 0x7FFF0000 && currentScope.compilerOptions().complianceLevel < 0x300000L) {
                        codeStream.lookupswitch(defaultLabel, this.constants, sortedIndexes, caseLabels);
                    } else {
                        codeStream.tableswitch(defaultLabel, min, max3, this.constants, sortedIndexes, this.constMapping, caseLabels);
                    }
                } else {
                    codeStream.lookupswitch(defaultLabel, this.constants, sortedIndexes, caseLabels);
                }
                codeStream.recordPositionsFrom(codeStream.position, this.expression.sourceEnd);
            } else if (valueRequired) {
                codeStream.pop();
            }
            int caseIndex = 0;
            if (this.statements != null) {
                int i4 = 0;
                int maxCases = this.statements.length;
                while (i4 < maxCases) {
                    Statement statement = this.statements[i4];
                    if (caseIndex < constantCount && statement == this.cases[caseIndex]) {
                        this.scope.enclosingCase = this.cases[caseIndex];
                        if (this.preSwitchInitStateIndex != -1) {
                            codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.preSwitchInitStateIndex);
                        }
                        ++caseIndex;
                    } else if (statement == this.defaultCase) {
                        this.scope.enclosingCase = this.defaultCase;
                        if (this.preSwitchInitStateIndex != -1) {
                            codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.preSwitchInitStateIndex);
                        }
                    }
                    this.statementGenerateCode(currentScope, codeStream, statement);
                    ++i4;
                }
            }
            boolean enumInSwitchExpression = resolvedType1.isEnum() && this instanceof SwitchExpression;
            boolean bl2 = isEnumSwitchWithoutDefaultCase = this.defaultCase == null && enumInSwitchExpression;
            if (isEnumSwitchWithoutDefaultCase) {
                if (this.preSwitchInitStateIndex != -1) {
                    codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.preSwitchInitStateIndex);
                }
                defaultLabel.place();
                codeStream.newJavaLangIncompatibleClassChangeError();
                codeStream.dup();
                codeStream.invokeJavaLangIncompatibleClassChangeErrorDefaultConstructor();
                codeStream.athrow();
            }
            if (this.mergedInitStateIndex != -1) {
                codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.mergedInitStateIndex);
                codeStream.addDefinitelyAssignedVariables(currentScope, this.mergedInitStateIndex);
            }
            if (this.scope != currentScope) {
                codeStream.exitUserScope(this.scope);
            }
            this.breakLabel.place();
            if (this.defaultCase == null && !enumInSwitchExpression) {
                codeStream.recordPositionsFrom(codeStream.position, this.sourceEnd, true);
                defaultLabel.place();
            }
            if (this instanceof SwitchExpression) {
                TypeBinding switchResolveType = this.resolvedType;
                if (this.expectedType() != null) {
                    switchResolveType = this.expectedType().erasure();
                }
                boolean optimizedGoto = codeStream.lastAbruptCompletion == -1;
                codeStream.recordExpressionType(switchResolveType, optimizedGoto ? 0 : 1, optimizedGoto || isEnumSwitchWithoutDefaultCase);
            }
            codeStream.recordPositionsFrom(pc, this.sourceStart);
        }
        finally {
            if (this.scope != null) {
                this.scope.enclosingCase = null;
            }
        }
    }

    protected void statementGenerateCode(BlockScope currentScope, CodeStream codeStream, Statement statement) {
        statement.generateCode(this.scope, codeStream);
    }

    @Override
    public void generateCode(BlockScope currentScope, CodeStream codeStream, boolean valueRequired) {
        this.generateCode(currentScope, codeStream);
    }

    @Override
    public StringBuffer printStatement(int indent, StringBuffer output) {
        SwitchStatement.printIndent(indent, output).append("switch (");
        this.expression.printExpression(0, output).append(") {");
        if (this.statements != null) {
            int i = 0;
            while (i < this.statements.length) {
                output.append('\n');
                if (this.statements[i] instanceof CaseStatement) {
                    this.statements[i].printStatement(indent, output);
                } else {
                    this.statements[i].printStatement(indent + 2, output);
                }
                ++i;
            }
        }
        output.append("\n");
        return SwitchStatement.printIndent(indent, output).append('}');
    }

    private int getNConstants() {
        int n = 0;
        int i = 0;
        int l = this.statements.length;
        while (i < l) {
            Statement statement = this.statements[i];
            if (statement instanceof CaseStatement) {
                CaseStatement caseStmt = (CaseStatement)statement;
                n += caseStmt.constantExpressions != null ? caseStmt.constantExpressions.length : (caseStmt.constantExpression != null ? 1 : 0);
            }
            ++i;
        }
        return n;
    }

    protected void addSecretTryResultVariable() {
    }

    /*
     * Unable to fully structure code
     */
    @Override
    public void resolve(BlockScope upperScope) {
        block41: {
            try {
                block43: {
                    block45: {
                        block44: {
                            isEnumSwitch = false;
                            isStringSwitch = false;
                            expressionType = this.expression.resolveType(upperScope);
                            compilerOptions = upperScope.compilerOptions();
                            if (expressionType == null) break block43;
                            this.expression.computeConversion(upperScope, expressionType, expressionType);
                            if (expressionType.isValidBinding()) break block44;
                            expressionType = null;
                            break block43;
                        }
                        if (!expressionType.isBaseType()) break block45;
                        if (!this.expression.isConstantValueOfTypeAssignableToType(expressionType, TypeBinding.INT) && !expressionType.isCompatibleWith(TypeBinding.INT)) ** GOTO lbl-1000
                        break block43;
                    }
                    if (expressionType.isEnum()) {
                        isEnumSwitch = true;
                        if (compilerOptions.complianceLevel < 0x310000L) {
                            upperScope.problemReporter().incorrectSwitchType(this.expression, expressionType);
                        }
                    } else if (upperScope.isBoxingCompatibleWith(expressionType, TypeBinding.INT)) {
                        this.expression.computeConversion(upperScope, TypeBinding.INT, expressionType);
                    } else if (compilerOptions.complianceLevel >= 0x330000L && expressionType.id == 11) {
                        isStringSwitch = true;
                    } else lbl-1000:
                    // 2 sources

                    {
                        upperScope.problemReporter().incorrectSwitchType(this.expression, expressionType);
                        expressionType = null;
                    }
                }
                if (isStringSwitch) {
                    this.dispatchStringCopy = new LocalVariableBinding(SwitchStatement.SecretStringVariableName, (TypeBinding)upperScope.getJavaLangString(), 0, false);
                    upperScope.addLocalVariable(this.dispatchStringCopy);
                    this.dispatchStringCopy.setConstant(Constant.NotAConstant);
                    this.dispatchStringCopy.useFlag = 1;
                }
                if (this.statements != null) {
                    this.scope = new BlockScope(upperScope);
                    length = this.statements.length;
                    this.cases = new CaseStatement[length];
                    this.nConstants = this.getNConstants();
                    if (!isStringSwitch) {
                        this.constants = new int[this.nConstants];
                        this.constMapping = new int[this.nConstants];
                    } else {
                        this.stringConstants = new String[this.nConstants];
                        this.constMapping = new int[this.nConstants];
                    }
                    counter = 0;
                    caseCounter = 0;
                    i = 0;
                    while (i < length) {
                        caseIndex = new int[this.nConstants];
                        statement = this.statements[i];
                        if (!(statement instanceof CaseStatement)) {
                            statement.resolve(this.scope);
                        } else {
                            constantsList = statement.resolveCase(this.scope, expressionType, this);
                            if (constantsList != Constant.NotAConstantList) {
                                var16_18 = constantsList;
                                var15_17 = constantsList.length;
                                var14_16 = 0;
                                while (var14_16 < var15_17) {
                                    con = var16_18[var14_16];
                                    if (con != Constant.NotAConstant) {
                                        if (!isStringSwitch) {
                                            key = con.intValue();
                                            j = 0;
                                            while (j < counter) {
                                                if (this.constants[j] == key) {
                                                    this.reportDuplicateCase((CaseStatement)statement, this.cases[caseIndex[j]], length);
                                                }
                                                ++j;
                                            }
                                            this.constants[counter] = key;
                                        } else {
                                            key = con.stringValue();
                                            j = 0;
                                            while (j < counter) {
                                                if (this.stringConstants[j].equals(key)) {
                                                    this.reportDuplicateCase((CaseStatement)statement, this.cases[caseIndex[j]], length);
                                                }
                                                ++j;
                                            }
                                            this.stringConstants[counter] = key;
                                        }
                                        this.constMapping[counter] = counter;
                                        caseIndex[counter] = caseCounter;
                                        ++counter;
                                    }
                                    ++var14_16;
                                }
                            }
                            ++caseCounter;
                        }
                        ++i;
                    }
                    if (length != counter) {
                        if (!isStringSwitch) {
                            this.constants = new int[counter];
                            System.arraycopy(this.constants, 0, this.constants, 0, counter);
                        } else {
                            this.stringConstants = new String[counter];
                            System.arraycopy(this.stringConstants, 0, this.stringConstants, 0, counter);
                        }
                        this.constMapping = new int[counter];
                        System.arraycopy(this.constMapping, 0, this.constMapping, 0, counter);
                    }
                } else if ((this.bits & 8) != 0) {
                    upperScope.problemReporter().undocumentedEmptyBlock(this.blockStart, this.sourceEnd);
                }
                this.reportMixingCaseTypes();
                if (this.defaultCase == null) {
                    if (this.ignoreMissingDefaultCase(compilerOptions, isEnumSwitch)) {
                        if (isEnumSwitch) {
                            upperScope.methodScope().hasMissingSwitchDefault = true;
                        }
                    } else {
                        upperScope.problemReporter().missingDefaultCase(this, isEnumSwitch, expressionType);
                    }
                }
                if (!isEnumSwitch || compilerOptions.complianceLevel < 0x310000L || this.defaultCase != null && !compilerOptions.reportMissingEnumCaseDespiteDefault) break block41;
                v0 = constantCount = this.constants == null ? 0 : this.constants.length;
                if (constantCount < this.caseCount || constantCount == ((ReferenceBinding)expressionType).enumConstantCount()) break block41;
                enumFields = ((ReferenceBinding)expressionType.erasure()).fields();
                i = 0;
                max = enumFields.length;
                while (i < max) {
                    block42: {
                        enumConstant = enumFields[i];
                        if ((enumConstant.modifiers & 16384) != 0) {
                            j = 0;
                            while (j < constantCount) {
                                if (enumConstant.id + 1 != this.constants[j]) {
                                    ++j;
                                    continue;
                                }
                                break block42;
                            }
                            v1 = suppress = this.defaultCase != null && (this.defaultCase.bits & 0x40000000) != 0;
                            if (!suppress) {
                                this.reportMissingEnumConstantCase(upperScope, enumConstant);
                            }
                        }
                    }
                    ++i;
                }
            }
            finally {
                if (this.scope != null) {
                    this.scope.enclosingCase = null;
                }
            }
        }
    }

    protected void reportMissingEnumConstantCase(BlockScope upperScope, FieldBinding enumConstant) {
        upperScope.problemReporter().missingEnumConstantCase(this, enumConstant);
    }

    protected boolean ignoreMissingDefaultCase(CompilerOptions compilerOptions, boolean isEnumSwitch) {
        return compilerOptions.getSeverity(0x40008000) == 256;
    }

    @Override
    public boolean isTrulyExpression() {
        return false;
    }

    private void reportMixingCaseTypes() {
        if (this.caseCount == 0) {
            this.switchLabeledRules = this.defaultCase != null ? this.defaultCase.isExpr : this.switchLabeledRules;
            return;
        }
        boolean isExpr = this.switchLabeledRules = this.cases[0].isExpr;
        int i = 1;
        int l = this.caseCount;
        while (i < l) {
            if (this.cases[i].isExpr != isExpr) {
                this.scope.problemReporter().switchExpressionMixedCase(this.cases[i]);
                return;
            }
            ++i;
        }
        if (this.defaultCase != null && this.defaultCase.isExpr != isExpr) {
            this.scope.problemReporter().switchExpressionMixedCase(this.defaultCase);
        }
    }

    private void reportDuplicateCase(CaseStatement duplicate, CaseStatement original, int length) {
        if (this.duplicateCaseStatements == null) {
            this.scope.problemReporter().duplicateCase(original);
            if (duplicate != original) {
                this.scope.problemReporter().duplicateCase(duplicate);
            }
            this.duplicateCaseStatements = new CaseStatement[length];
            this.duplicateCaseStatements[this.duplicateCaseStatementsCounter++] = original;
            if (duplicate != original) {
                this.duplicateCaseStatements[this.duplicateCaseStatementsCounter++] = duplicate;
            }
        } else {
            boolean found = false;
            int k = 2;
            while (k < this.duplicateCaseStatementsCounter) {
                if (this.duplicateCaseStatements[k] == duplicate) {
                    found = true;
                    break;
                }
                ++k;
            }
            if (!found) {
                this.scope.problemReporter().duplicateCase(duplicate);
                this.duplicateCaseStatements[this.duplicateCaseStatementsCounter++] = duplicate;
            }
        }
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

    @Override
    public void branchChainTo(BranchLabel label) {
        if (this.breakLabel.forwardReferenceCount() > 0) {
            label.becomeDelegateFor(this.breakLabel);
        }
    }

    @Override
    public boolean doesNotCompleteNormally() {
        if (this.statements == null || this.statements.length == 0) {
            return false;
        }
        int i = 0;
        int length = this.statements.length;
        while (i < length) {
            if (this.statements[i].breaksOut(null)) {
                return false;
            }
            ++i;
        }
        return this.statements[this.statements.length - 1].doesNotCompleteNormally();
    }

    @Override
    public boolean completesByContinue() {
        if (this.statements == null || this.statements.length == 0) {
            return false;
        }
        int i = 0;
        int length = this.statements.length;
        while (i < length) {
            if (this.statements[i].completesByContinue()) {
                return true;
            }
            ++i;
        }
        return false;
    }

    @Override
    public boolean canCompleteNormally() {
        if (this.statements == null || this.statements.length == 0) {
            return true;
        }
        if (!this.switchLabeledRules) {
            if (this.statements[this.statements.length - 1].canCompleteNormally()) {
                return true;
            }
            if (this.defaultCase == null) {
                return true;
            }
            int i = 0;
            int length = this.statements.length;
            while (i < length) {
                if (this.statements[i].breaksOut(null)) {
                    return true;
                }
                ++i;
            }
        } else {
            Statement[] statementArray = this.statements;
            int n = this.statements.length;
            int n2 = 0;
            while (n2 < n) {
                Statement stmt = statementArray[n2];
                if (!(stmt instanceof CaseStatement)) {
                    if (this.defaultCase == null) {
                        return true;
                    }
                    if (stmt instanceof Expression) {
                        return true;
                    }
                    if (stmt.canCompleteNormally()) {
                        return true;
                    }
                    if (stmt instanceof YieldStatement && ((YieldStatement)stmt).isImplicit) {
                        return true;
                    }
                    if (stmt instanceof Block) {
                        Block block = (Block)stmt;
                        if (block.canCompleteNormally()) {
                            return true;
                        }
                        if (block.breaksOut(null)) {
                            return true;
                        }
                    }
                }
                ++n2;
            }
        }
        return false;
    }

    @Override
    public boolean continueCompletes() {
        if (this.statements == null || this.statements.length == 0) {
            return false;
        }
        int i = 0;
        int length = this.statements.length;
        while (i < length) {
            if (this.statements[i].continueCompletes()) {
                return true;
            }
            ++i;
        }
        return false;
    }

    @Override
    public StringBuffer printExpression(int indent, StringBuffer output) {
        return this.printStatement(indent, output);
    }
}

