/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.lookup;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.CaseStatement;
import org.eclipse.jdt.internal.compiler.ast.FakedTrackingVariable;
import org.eclipse.jdt.internal.compiler.ast.LambdaExpression;
import org.eclipse.jdt.internal.compiler.ast.LocalDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ReturnStatement;
import org.eclipse.jdt.internal.compiler.ast.Statement;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
import org.eclipse.jdt.internal.compiler.flow.FlowContext;
import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.internal.compiler.lookup.CompilationUnitScope;
import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
import org.eclipse.jdt.internal.compiler.lookup.InvocationSite;
import org.eclipse.jdt.internal.compiler.lookup.LocalTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodScope;
import org.eclipse.jdt.internal.compiler.lookup.MethodVerifier;
import org.eclipse.jdt.internal.compiler.lookup.NestedTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.PackageBinding;
import org.eclipse.jdt.internal.compiler.lookup.ProblemBinding;
import org.eclipse.jdt.internal.compiler.lookup.ProblemFieldBinding;
import org.eclipse.jdt.internal.compiler.lookup.ProblemReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.SyntheticArgumentBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.VariableBinding;
import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;

public class BlockScope
extends Scope {
    public LocalVariableBinding[] locals;
    public int localIndex;
    public int startIndex;
    public int offset;
    public int maxOffset;
    public BlockScope[] shiftScopes;
    public Scope[] subscopes = new Scope[1];
    public int subscopeCount = 0;
    public CaseStatement enclosingCase;
    public static final VariableBinding[] EmulationPathToImplicitThis = new VariableBinding[0];
    public static final VariableBinding[] NoEnclosingInstanceInConstructorCall = new VariableBinding[0];
    public static final VariableBinding[] NoEnclosingInstanceInStaticContext = new VariableBinding[0];
    public boolean insideTypeAnnotation = false;
    public Statement blockStatement;
    private List trackingVariables;
    public FlowInfo finallyInfo;

    public BlockScope(BlockScope parent) {
        this(parent, true);
    }

    public BlockScope(BlockScope parent, boolean addToParentScope) {
        this(1, parent);
        this.locals = new LocalVariableBinding[5];
        if (addToParentScope) {
            parent.addSubscope(this);
        }
        this.startIndex = parent.localIndex;
    }

    public BlockScope(BlockScope parent, int variableCount) {
        this(1, parent);
        this.locals = new LocalVariableBinding[variableCount];
        parent.addSubscope(this);
        this.startIndex = parent.localIndex;
    }

    protected BlockScope(int kind, Scope parent) {
        super(kind, parent);
    }

    public final void addAnonymousType(TypeDeclaration anonymousType, ReferenceBinding superBinding) {
        ClassScope anonymousClassScope = new ClassScope(this, anonymousType);
        anonymousClassScope.buildAnonymousTypeBinding(this.enclosingSourceType(), superBinding);
        MethodScope methodScope = this.methodScope();
        while (methodScope != null && methodScope.referenceContext instanceof LambdaExpression) {
            LambdaExpression lambda = (LambdaExpression)methodScope.referenceContext;
            if (!lambda.scope.isStatic && !lambda.scope.isConstructorCall) {
                lambda.shouldCaptureInstance = true;
            }
            methodScope = methodScope.enclosingMethodScope();
        }
    }

    public final void addLocalType(TypeDeclaration localType) {
        ClassScope localTypeScope = new ClassScope(this, localType);
        this.addSubscope(localTypeScope);
        localTypeScope.buildLocalTypeBinding(this.enclosingSourceType());
        MethodScope methodScope = this.methodScope();
        while (methodScope != null && methodScope.referenceContext instanceof LambdaExpression) {
            LambdaExpression lambda = (LambdaExpression)methodScope.referenceContext;
            if (!lambda.scope.isStatic && !lambda.scope.isConstructorCall) {
                lambda.shouldCaptureInstance = true;
            }
            methodScope = methodScope.enclosingMethodScope();
        }
    }

    public final void addLocalVariable(LocalVariableBinding binding) {
        this.checkAndSetModifiersForVariable(binding);
        if (this.localIndex == this.locals.length) {
            this.locals = new LocalVariableBinding[this.localIndex * 2];
            System.arraycopy(this.locals, 0, this.locals, 0, this.localIndex);
        }
        this.locals[this.localIndex++] = binding;
        binding.declaringScope = this;
        binding.id = this.outerMostMethodScope().analysisIndex++;
    }

    public void addSubscope(Scope childScope) {
        if (this.subscopeCount == this.subscopes.length) {
            this.subscopes = new Scope[this.subscopeCount * 2];
            System.arraycopy(this.subscopes, 0, this.subscopes, 0, this.subscopeCount);
        }
        this.subscopes[this.subscopeCount++] = childScope;
    }

    public final boolean allowBlankFinalFieldAssignment(FieldBinding binding) {
        if (TypeBinding.notEquals(this.enclosingReceiverType(), binding.declaringClass)) {
            return false;
        }
        MethodScope methodScope = this.methodScope();
        if (methodScope.isStatic != binding.isStatic()) {
            return false;
        }
        if (methodScope.isLambdaScope()) {
            return false;
        }
        return methodScope.isInsideInitializer() || ((AbstractMethodDeclaration)methodScope.referenceContext).isInitializationMethod();
    }

    String basicToString(int tab) {
        String newLine = "\n";
        int i = tab;
        while (--i >= 0) {
            newLine = String.valueOf(newLine) + "\t";
        }
        String s = String.valueOf(newLine) + "--- Block Scope ---";
        newLine = String.valueOf(newLine) + "\t";
        s = String.valueOf(s) + newLine + "locals:";
        int i2 = 0;
        while (i2 < this.localIndex) {
            s = String.valueOf(s) + newLine + "\t" + this.locals[i2].toString();
            ++i2;
        }
        s = String.valueOf(s) + newLine + "startIndex = " + this.startIndex;
        return s;
    }

    private void checkAndSetModifiersForVariable(LocalVariableBinding varBinding) {
        int unexpectedModifiers;
        int realModifiers;
        int modifiers = varBinding.modifiers;
        if ((modifiers & 0x400000) != 0 && varBinding.declaration != null) {
            this.problemReporter().duplicateModifierForVariable(varBinding.declaration, this instanceof MethodScope);
        }
        if (((realModifiers = modifiers & 0xFFFF) & (unexpectedModifiers = -17)) != 0 && varBinding.declaration != null) {
            this.problemReporter().illegalModifierForVariable(varBinding.declaration, this instanceof MethodScope);
        }
        varBinding.modifiers = modifiers;
    }

    public void adjustLocalVariablePositions(int delta, boolean offsetAlreadyUpdated) {
        this.offset += offsetAlreadyUpdated ? 0 : delta;
        if (this.offset > this.maxOffset) {
            this.maxOffset = this.offset;
        }
        Scope[] scopeArray = this.subscopes;
        int n = this.subscopes.length;
        int n2 = 0;
        while (n2 < n) {
            Scope subScope = scopeArray[n2];
            if (subScope instanceof BlockScope) {
                ((BlockScope)subScope).adjustCurrentAndSubScopeLocalVariablePositions(delta);
            }
            ++n2;
        }
        Scope scope = this.parent;
        while (scope instanceof BlockScope) {
            BlockScope pBlock = (BlockScope)scope;
            int diff = this.maxOffset - pBlock.maxOffset;
            pBlock.maxOffset = pBlock.maxOffset + (diff > 0 ? diff : 0);
            if (scope instanceof MethodScope) break;
            scope = scope.parent;
        }
    }

    public void adjustCurrentAndSubScopeLocalVariablePositions(int delta) {
        this.offset += delta;
        if (this.offset > this.maxOffset) {
            this.maxOffset = this.offset;
        }
        Object[] objectArray = this.locals;
        int n = this.locals.length;
        int n2 = 0;
        while (n2 < n) {
            LocalVariableBinding lvb = objectArray[n2];
            if (lvb != null && lvb.resolvedPosition != -1) {
                lvb.resolvedPosition += delta;
            }
            ++n2;
        }
        objectArray = this.subscopes;
        n = this.subscopes.length;
        n2 = 0;
        while (n2 < n) {
            Object subScope = objectArray[n2];
            if (subScope instanceof BlockScope) {
                ((BlockScope)subScope).adjustCurrentAndSubScopeLocalVariablePositions(delta);
            }
            ++n2;
        }
    }

    void computeLocalVariablePositions(int ilocal, int initOffset, CodeStream codeStream) {
        this.offset = initOffset;
        this.maxOffset = initOffset;
        int maxLocals = this.localIndex;
        boolean hasMoreVariables = ilocal < maxLocals;
        int iscope = 0;
        int maxScopes = this.subscopeCount;
        boolean hasMoreScopes = maxScopes > 0;
        while (hasMoreVariables || hasMoreScopes) {
            boolean generateCurrentLocalVar;
            if (hasMoreScopes && (!hasMoreVariables || this.subscopes[iscope].startIndex() <= ilocal)) {
                if (this.subscopes[iscope] instanceof BlockScope) {
                    BlockScope subscope = (BlockScope)this.subscopes[iscope];
                    int subOffset = subscope.shiftScopes == null ? this.offset : subscope.maxShiftedOffset();
                    subscope.computeLocalVariablePositions(0, subOffset, codeStream);
                    if (subscope.maxOffset > this.maxOffset) {
                        this.maxOffset = subscope.maxOffset;
                    }
                }
                hasMoreScopes = ++iscope < maxScopes;
                continue;
            }
            LocalVariableBinding local = this.locals[ilocal];
            boolean bl = generateCurrentLocalVar = local.useFlag > 0 && local.constant() == Constant.NotAConstant;
            if (local.useFlag == 0 && local.declaration != null && (local.declaration.bits & 0x40000000) != 0) {
                if (local.isCatchParameter()) {
                    this.problemReporter().unusedExceptionParameter(local.declaration);
                } else {
                    this.problemReporter().unusedLocalVariable(local.declaration);
                }
            }
            if (!generateCurrentLocalVar && local.declaration != null && this.compilerOptions().preserveAllLocalVariables) {
                generateCurrentLocalVar = true;
                if (local.useFlag == 0) {
                    local.useFlag = 1;
                }
            }
            if (generateCurrentLocalVar) {
                if (local.declaration != null) {
                    codeStream.record(local);
                }
                local.resolvedPosition = this.offset++;
                if (TypeBinding.equalsEquals(local.type, TypeBinding.LONG) || TypeBinding.equalsEquals(local.type, TypeBinding.DOUBLE)) {
                    this.offset += 2;
                }
                if (this.offset > 65535) {
                    this.problemReporter().noMoreAvailableSpaceForLocal(local, local.declaration == null ? (ASTNode)((Object)this.methodScope().referenceContext) : local.declaration);
                }
            } else {
                local.resolvedPosition = -1;
            }
            boolean bl2 = hasMoreVariables = ++ilocal < maxLocals;
        }
        if (this.offset > this.maxOffset) {
            this.maxOffset = this.offset;
        }
    }

    public void emulateOuterAccess(LocalVariableBinding outerLocalVariable) {
        BlockScope outerVariableScope = outerLocalVariable.declaringScope;
        if (outerVariableScope == null) {
            return;
        }
        int depth = 0;
        Scope scope = this;
        while (outerVariableScope != scope) {
            switch (scope.kind) {
                case 3: {
                    ++depth;
                    break;
                }
                case 2: {
                    if (!scope.isLambdaScope()) break;
                    LambdaExpression lambdaExpression = (LambdaExpression)scope.referenceContext();
                    lambdaExpression.addSyntheticArgument(outerLocalVariable);
                }
            }
            scope = scope.parent;
        }
        if (depth == 0) {
            return;
        }
        MethodScope currentMethodScope = this.methodScope();
        if (outerVariableScope.methodScope() != currentMethodScope) {
            NestedTypeBinding currentType = (NestedTypeBinding)this.enclosingSourceType();
            if (!currentType.isLocalType()) {
                return;
            }
            if (!currentMethodScope.isInsideInitializerOrConstructor()) {
                currentType.addSyntheticArgumentAndField(outerLocalVariable);
            } else {
                currentType.addSyntheticArgument(outerLocalVariable);
            }
        }
    }

    public final ReferenceBinding findLocalType(char[] name) {
        long compliance = this.compilerOptions().complianceLevel;
        int i = this.subscopeCount - 1;
        while (i >= 0) {
            if (this.subscopes[i] instanceof ClassScope) {
                LocalTypeBinding sourceType = (LocalTypeBinding)((ClassScope)this.subscopes[i]).referenceContext.binding;
                if ((compliance < 0x300000L || sourceType.enclosingCase == null || this.isInsideCase(sourceType.enclosingCase)) && CharOperation.equals(sourceType.sourceName(), name)) {
                    return sourceType;
                }
            }
            --i;
        }
        return null;
    }

    public LocalDeclaration[] findLocalVariableDeclarations(int position) {
        int ilocal = 0;
        int maxLocals = this.localIndex;
        boolean hasMoreVariables = maxLocals > 0;
        LocalDeclaration[] localDeclarations = null;
        int declPtr = 0;
        int iscope = 0;
        int maxScopes = this.subscopeCount;
        boolean hasMoreScopes = maxScopes > 0;
        while (hasMoreVariables || hasMoreScopes) {
            LocalDeclaration localDecl;
            if (hasMoreScopes && (!hasMoreVariables || this.subscopes[iscope].startIndex() <= ilocal)) {
                Scope subscope = this.subscopes[iscope];
                if (subscope.kind == 1 && (localDeclarations = ((BlockScope)subscope).findLocalVariableDeclarations(position)) != null) {
                    return localDeclarations;
                }
                hasMoreScopes = ++iscope < maxScopes;
                continue;
            }
            LocalVariableBinding local = this.locals[ilocal];
            if (local != null && (local.modifiers & 0x10000000) == 0 && (localDecl = local.declaration) != null) {
                if (localDecl.declarationSourceStart <= position) {
                    if (position <= localDecl.declarationSourceEnd) {
                        if (localDeclarations == null) {
                            localDeclarations = new LocalDeclaration[maxLocals];
                        }
                        localDeclarations[declPtr++] = localDecl;
                    }
                } else {
                    return localDeclarations;
                }
            }
            boolean bl = hasMoreVariables = ++ilocal < maxLocals;
            if (hasMoreVariables || localDeclarations == null) continue;
            return localDeclarations;
        }
        return null;
    }

    private boolean isPatternVariableInScope(InvocationSite invocationSite, LocalVariableBinding variable) {
        LocalVariableBinding[] patternVariablesInScope = invocationSite.getPatternVariablesWhenTrue();
        if (patternVariablesInScope == null) {
            return false;
        }
        LocalVariableBinding[] localVariableBindingArray = patternVariablesInScope;
        int n = patternVariablesInScope.length;
        int n2 = 0;
        while (n2 < n) {
            LocalVariableBinding v = localVariableBindingArray[n2];
            if (v == variable) {
                return true;
            }
            ++n2;
        }
        return false;
    }

    @Override
    public LocalVariableBinding findVariable(char[] variableName, InvocationSite invocationSite) {
        char[] localName;
        LocalVariableBinding local;
        int varLength = variableName.length;
        int i = this.localIndex - 1;
        while (i >= 0) {
            local = this.locals[i];
            if ((local.modifiers & 0x10000000) == 0) {
                localName = local.name;
                if (local.name.length == varLength && CharOperation.equals(localName, variableName)) {
                    return local;
                }
            }
            --i;
        }
        i = this.localIndex - 1;
        while (i >= 0) {
            local = this.locals[i];
            if ((local.modifiers & 0x10000000) != 0) {
                localName = local.name;
                if (local.name.length == varLength && CharOperation.equals(localName, variableName) && this.isPatternVariableInScope(invocationSite, local)) {
                    return local;
                }
            }
            --i;
        }
        return null;
    }

    public Binding getBinding(char[][] compoundName, int mask, InvocationSite invocationSite, boolean needResolve) {
        ASTNode invocationNode;
        int currentIndex;
        int length;
        Binding binding;
        block22: {
            binding = this.getBinding(compoundName[0], mask | 4 | 0x10, invocationSite, needResolve);
            invocationSite.setFieldIndex(1);
            if (binding instanceof VariableBinding) {
                return binding;
            }
            CompilationUnitScope unitScope = this.compilationUnitScope();
            unitScope.recordQualifiedReference(compoundName);
            if (!binding.isValidBinding()) {
                return binding;
            }
            length = compoundName.length;
            currentIndex = 1;
            if (binding instanceof PackageBinding) {
                PackageBinding packageBinding = (PackageBinding)binding;
                while (currentIndex < length) {
                    unitScope.recordReference(packageBinding.compoundName, compoundName[currentIndex]);
                    binding = packageBinding.getTypeOrPackage(compoundName[currentIndex++], this.module(), currentIndex < length);
                    invocationSite.setFieldIndex(currentIndex);
                    if (binding == null) {
                        if (currentIndex == length) {
                            return new ProblemReferenceBinding(CharOperation.subarray(compoundName, 0, currentIndex), null, 1);
                        }
                        return new ProblemBinding(CharOperation.subarray(compoundName, 0, currentIndex), 1);
                    }
                    if (binding instanceof ReferenceBinding) {
                        if (!binding.isValidBinding()) {
                            return new ProblemReferenceBinding(CharOperation.subarray(compoundName, 0, currentIndex), (ReferenceBinding)((ReferenceBinding)binding).closestMatch(), binding.problemId());
                        }
                        if (!((ReferenceBinding)binding).canBeSeenBy(this)) {
                            return new ProblemReferenceBinding(CharOperation.subarray(compoundName, 0, currentIndex), (ReferenceBinding)binding, 2);
                        }
                        break block22;
                    }
                    packageBinding = (PackageBinding)binding;
                }
                return new ProblemReferenceBinding(CharOperation.subarray(compoundName, 0, currentIndex), null, 1);
            }
        }
        ReferenceBinding referenceBinding = (ReferenceBinding)binding;
        binding = this.environment().convertToRawType(referenceBinding, false);
        if (invocationSite instanceof ASTNode && (invocationNode = (ASTNode)((Object)invocationSite)).isTypeUseDeprecated(referenceBinding, this)) {
            this.problemReporter().deprecatedType(referenceBinding, invocationNode);
        }
        ProblemFieldBinding problemFieldBinding = null;
        while (currentIndex < length) {
            ASTNode invocationNode2;
            referenceBinding = (ReferenceBinding)binding;
            char[] nextName = compoundName[currentIndex++];
            invocationSite.setFieldIndex(currentIndex);
            invocationSite.setActualReceiverType(referenceBinding);
            if ((mask & 1) != 0 && (binding = this.findField(referenceBinding, nextName, invocationSite, true)) != null) {
                if (binding.isValidBinding()) break;
                problemFieldBinding = new ProblemFieldBinding(((ProblemFieldBinding)binding).closestMatch, ((ProblemFieldBinding)binding).declaringClass, CharOperation.concatWith(CharOperation.subarray(compoundName, 0, currentIndex), '.'), binding.problemId());
                if (binding.problemId() != 2) {
                    return problemFieldBinding;
                }
            }
            if ((binding = this.findMemberType(nextName, referenceBinding)) == null) {
                if (problemFieldBinding != null) {
                    return problemFieldBinding;
                }
                if ((mask & 1) != 0) {
                    return new ProblemFieldBinding(null, referenceBinding, nextName, 1);
                }
                if ((mask & 3) != 0) {
                    return new ProblemBinding(CharOperation.subarray(compoundName, 0, currentIndex), referenceBinding, 1);
                }
                return new ProblemReferenceBinding(CharOperation.subarray(compoundName, 0, currentIndex), referenceBinding, 1);
            }
            if (!binding.isValidBinding()) {
                if (problemFieldBinding != null) {
                    return problemFieldBinding;
                }
                return new ProblemReferenceBinding(CharOperation.subarray(compoundName, 0, currentIndex), (ReferenceBinding)((ReferenceBinding)binding).closestMatch(), binding.problemId());
            }
            if (!(invocationSite instanceof ASTNode) || !(invocationNode2 = (ASTNode)((Object)invocationSite)).isTypeUseDeprecated(referenceBinding = (ReferenceBinding)binding, this)) continue;
            this.problemReporter().deprecatedType(referenceBinding, invocationNode2);
        }
        if ((mask & 1) != 0 && binding instanceof FieldBinding) {
            FieldBinding field = (FieldBinding)binding;
            if (!field.isStatic()) {
                return new ProblemFieldBinding(field, field.declaringClass, CharOperation.concatWith(CharOperation.subarray(compoundName, 0, currentIndex), '.'), 7);
            }
            return binding;
        }
        if ((mask & 4) != 0 && binding instanceof ReferenceBinding) {
            return binding;
        }
        return new ProblemBinding(CharOperation.subarray(compoundName, 0, currentIndex), 1);
    }

    public final Binding getBinding(char[][] compoundName, InvocationSite invocationSite) {
        TypeBinding receiverType;
        Binding binding;
        int length;
        int currentIndex;
        block17: {
            block16: {
                currentIndex = 0;
                length = compoundName.length;
                if (!(binding = this.getBinding(compoundName[currentIndex++], 23, invocationSite, true)).isValidBinding()) {
                    return binding;
                }
                if (binding instanceof PackageBinding) {
                    while (currentIndex < length) {
                        PackageBinding packageBinding = (PackageBinding)binding;
                        if ((binding = packageBinding.getTypeOrPackage(compoundName[currentIndex++], this.module(), currentIndex < length)) == null) {
                            if (currentIndex == length) {
                                return new ProblemReferenceBinding(CharOperation.subarray(compoundName, 0, currentIndex), null, 1);
                            }
                            return new ProblemBinding(CharOperation.subarray(compoundName, 0, currentIndex), 1);
                        }
                        if (!(binding instanceof ReferenceBinding)) continue;
                        if (!binding.isValidBinding()) {
                            return new ProblemReferenceBinding(CharOperation.subarray(compoundName, 0, currentIndex), (ReferenceBinding)((ReferenceBinding)binding).closestMatch(), binding.problemId());
                        }
                        if (!((ReferenceBinding)binding).canBeSeenBy(this)) {
                            return new ProblemReferenceBinding(CharOperation.subarray(compoundName, 0, currentIndex), (ReferenceBinding)binding, 2);
                        }
                        break block16;
                    }
                    return binding;
                }
            }
            if (binding instanceof ReferenceBinding) {
                while (currentIndex < length) {
                    ReferenceBinding typeBinding = (ReferenceBinding)binding;
                    char[] nextName = compoundName[currentIndex++];
                    receiverType = typeBinding.capture(this, invocationSite.sourceStart(), invocationSite.sourceEnd());
                    binding = this.findField(receiverType, nextName, invocationSite, true);
                    if (binding != null) {
                        if (!binding.isValidBinding()) {
                            return new ProblemFieldBinding((FieldBinding)binding, ((FieldBinding)binding).declaringClass, CharOperation.concatWith(CharOperation.subarray(compoundName, 0, currentIndex), '.'), binding.problemId());
                        }
                        if (!((FieldBinding)binding).isStatic()) {
                            return new ProblemFieldBinding((FieldBinding)binding, ((FieldBinding)binding).declaringClass, CharOperation.concatWith(CharOperation.subarray(compoundName, 0, currentIndex), '.'), 7);
                        }
                        break block17;
                    }
                    binding = this.findMemberType(nextName, typeBinding);
                    if (binding == null) {
                        return new ProblemBinding(CharOperation.subarray(compoundName, 0, currentIndex), typeBinding, 1);
                    }
                    if (binding.isValidBinding()) continue;
                    return new ProblemReferenceBinding(CharOperation.subarray(compoundName, 0, currentIndex), (ReferenceBinding)((ReferenceBinding)binding).closestMatch(), binding.problemId());
                }
                return binding;
            }
        }
        VariableBinding variableBinding = (VariableBinding)binding;
        while (currentIndex < length) {
            TypeBinding typeBinding = variableBinding.type;
            if (typeBinding == null) {
                return new ProblemFieldBinding(null, null, CharOperation.concatWith(CharOperation.subarray(compoundName, 0, currentIndex), '.'), 1);
            }
            receiverType = typeBinding.capture(this, invocationSite.sourceStart(), invocationSite.sourceEnd());
            variableBinding = this.findField(receiverType, compoundName[currentIndex++], invocationSite, true);
            if (variableBinding == null) {
                return new ProblemFieldBinding(null, receiverType instanceof ReferenceBinding ? (ReferenceBinding)receiverType : null, CharOperation.concatWith(CharOperation.subarray(compoundName, 0, currentIndex), '.'), 1);
            }
            if (variableBinding.isValidBinding()) continue;
            return variableBinding;
        }
        return variableBinding;
    }

    public VariableBinding[] getEmulationPath(LocalVariableBinding outerLocalVariable) {
        FieldBinding syntheticField;
        SyntheticArgumentBinding syntheticArg;
        LambdaExpression lambda;
        SyntheticArgumentBinding syntheticArgument;
        MethodScope currentMethodScope = this.methodScope();
        SourceTypeBinding sourceType = currentMethodScope.enclosingSourceType();
        BlockScope variableScope = outerLocalVariable.declaringScope;
        if (variableScope == null || currentMethodScope == variableScope.methodScope()) {
            return new VariableBinding[]{outerLocalVariable};
        }
        if (currentMethodScope.isLambdaScope() && (syntheticArgument = (lambda = (LambdaExpression)currentMethodScope.referenceContext).getSyntheticArgument(outerLocalVariable)) != null) {
            return new VariableBinding[]{syntheticArgument};
        }
        if (currentMethodScope.isInsideInitializerOrConstructor() && sourceType.isNestedType() && (syntheticArg = ((NestedTypeBinding)sourceType).getSyntheticArgument(outerLocalVariable)) != null) {
            return new VariableBinding[]{syntheticArg};
        }
        if (!currentMethodScope.isStatic && (syntheticField = sourceType.getSyntheticField(outerLocalVariable)) != null) {
            return new VariableBinding[]{syntheticField};
        }
        return null;
    }

    public Object[] getEmulationPath(ReferenceBinding targetEnclosingType, boolean onlyExactMatch, boolean denyEnclosingArgInConstructorCall) {
        FieldBinding syntheticField;
        NestedTypeBinding nestedEnclosingType;
        SyntheticArgumentBinding enclosingArgument;
        ReferenceBinding enclosingType;
        SyntheticArgumentBinding syntheticArg;
        MethodScope currentMethodScope = this.methodScope();
        SourceTypeBinding sourceType = currentMethodScope.enclosingSourceType();
        if (!currentMethodScope.isStatic && !currentMethodScope.isConstructorCall && (TypeBinding.equalsEquals(sourceType, targetEnclosingType) || !onlyExactMatch && sourceType.findSuperTypeOriginatingFrom(targetEnclosingType) != null)) {
            return EmulationPathToImplicitThis;
        }
        if (!sourceType.isNestedType() || sourceType.isStatic()) {
            if (currentMethodScope.isConstructorCall) {
                return NoEnclosingInstanceInConstructorCall;
            }
            if (currentMethodScope.isStatic) {
                return NoEnclosingInstanceInStaticContext;
            }
            return null;
        }
        if (sourceType.isNestedType() && currentMethodScope.isInsideInitializer() && currentMethodScope.isStatic) {
            return NoEnclosingInstanceInStaticContext;
        }
        boolean insideConstructor = currentMethodScope.isInsideInitializerOrConstructor();
        if (insideConstructor && (syntheticArg = ((NestedTypeBinding)sourceType).getSyntheticArgument(targetEnclosingType, onlyExactMatch, currentMethodScope.isConstructorCall)) != null) {
            boolean isAnonymousAndHasEnclosing;
            boolean bl = isAnonymousAndHasEnclosing = sourceType.isAnonymousType() && sourceType.scope.referenceContext.allocation.enclosingInstance != null;
            if (denyEnclosingArgInConstructorCall && !isAnonymousAndHasEnclosing && (TypeBinding.equalsEquals(sourceType, targetEnclosingType) || !onlyExactMatch && sourceType.findSuperTypeOriginatingFrom(targetEnclosingType) != null)) {
                return NoEnclosingInstanceInConstructorCall;
            }
            return new Object[]{syntheticArg};
        }
        if (currentMethodScope.isStatic) {
            return NoEnclosingInstanceInStaticContext;
        }
        if (sourceType.isAnonymousType() && (enclosingType = sourceType.enclosingType()).isNestedType() && (enclosingArgument = (nestedEnclosingType = (NestedTypeBinding)enclosingType).getSyntheticArgument(nestedEnclosingType.enclosingType(), onlyExactMatch, currentMethodScope.isConstructorCall)) != null && (syntheticField = sourceType.getSyntheticField(enclosingArgument)) != null && (TypeBinding.equalsEquals(syntheticField.type, targetEnclosingType) || !onlyExactMatch && ((ReferenceBinding)syntheticField.type).findSuperTypeOriginatingFrom(targetEnclosingType) != null)) {
            return new Object[]{syntheticField};
        }
        FieldBinding syntheticField2 = sourceType.getSyntheticField(targetEnclosingType, onlyExactMatch);
        if (syntheticField2 != null) {
            if (currentMethodScope.isConstructorCall) {
                return NoEnclosingInstanceInConstructorCall;
            }
            return new Object[]{syntheticField2};
        }
        Object[] path = new Object[2];
        ReferenceBinding currentType = sourceType.enclosingType();
        if (insideConstructor) {
            path[0] = ((NestedTypeBinding)sourceType).getSyntheticArgument(currentType, onlyExactMatch, currentMethodScope.isConstructorCall);
        } else {
            if (currentMethodScope.isConstructorCall) {
                return NoEnclosingInstanceInConstructorCall;
            }
            path[0] = sourceType.getSyntheticField(currentType, onlyExactMatch);
        }
        if (path[0] != null) {
            ReferenceBinding currentEnclosingType;
            int count = 1;
            while ((currentEnclosingType = currentType.enclosingType()) != null) {
                if (TypeBinding.equalsEquals(currentType, targetEnclosingType) || !onlyExactMatch && currentType.findSuperTypeOriginatingFrom(targetEnclosingType) != null) break;
                if (currentMethodScope != null) {
                    if ((currentMethodScope = currentMethodScope.enclosingMethodScope()) != null && currentMethodScope.isConstructorCall) {
                        return NoEnclosingInstanceInConstructorCall;
                    }
                    if (currentMethodScope != null && currentMethodScope.isStatic) {
                        return NoEnclosingInstanceInStaticContext;
                    }
                }
                if ((syntheticField2 = ((NestedTypeBinding)currentType).getSyntheticField(currentEnclosingType, onlyExactMatch)) == null) break;
                if (count == path.length) {
                    Object[] objectArray = path;
                    path = new Object[count + 1];
                    System.arraycopy(objectArray, 0, path, 0, count);
                }
                path[count++] = ((SourceTypeBinding)syntheticField2.declaringClass).addSyntheticMethod(syntheticField2, true, false);
                currentType = currentEnclosingType;
            }
            if (TypeBinding.equalsEquals(currentType, targetEnclosingType) || !onlyExactMatch && currentType.findSuperTypeOriginatingFrom(targetEnclosingType) != null) {
                return path;
            }
        }
        return null;
    }

    public final boolean isDuplicateLocalVariable(char[] name) {
        BlockScope current = this;
        while (true) {
            int i = 0;
            while (i < this.localIndex) {
                if (CharOperation.equals(name, current.locals[i].name)) {
                    return true;
                }
                ++i;
            }
            if (current.kind != 1) {
                return false;
            }
            current = (BlockScope)current.parent;
        }
    }

    public int maxShiftedOffset() {
        int max = -1;
        if (this.shiftScopes != null) {
            int i = 0;
            int length = this.shiftScopes.length;
            while (i < length) {
                int subMaxOffset;
                if (this.shiftScopes[i] != null && (subMaxOffset = this.shiftScopes[i].maxOffset) > max) {
                    max = subMaxOffset;
                }
                ++i;
            }
        }
        return max;
    }

    public final boolean needBlankFinalFieldInitializationCheck(FieldBinding binding) {
        boolean isStatic = binding.isStatic();
        ReferenceBinding fieldDeclaringClass = binding.declaringClass;
        MethodScope methodScope = this.namedMethodScope();
        while (methodScope != null) {
            if (methodScope.isStatic != isStatic) {
                return false;
            }
            if (!methodScope.isInsideInitializer() && !((AbstractMethodDeclaration)methodScope.referenceContext).isInitializationMethod()) {
                return false;
            }
            ReferenceBinding enclosingType = methodScope.enclosingReceiverType();
            if (TypeBinding.equalsEquals(enclosingType, fieldDeclaringClass)) {
                return true;
            }
            if (!enclosingType.erasure().isAnonymousType()) {
                return false;
            }
            methodScope = methodScope.enclosingMethodScope().namedMethodScope();
        }
        return false;
    }

    @Override
    public ProblemReporter problemReporter() {
        return this.methodScope().problemReporter();
    }

    public void propagateInnerEmulation(ReferenceBinding targetType, boolean isEnclosingInstanceSupplied) {
        SyntheticArgumentBinding[] syntheticArguments = targetType.syntheticOuterLocalVariables();
        if (syntheticArguments != null) {
            int i = 0;
            int max = syntheticArguments.length;
            while (i < max) {
                SyntheticArgumentBinding syntheticArg = syntheticArguments[i];
                if (!isEnclosingInstanceSupplied || !TypeBinding.equalsEquals(syntheticArg.type, targetType.enclosingType())) {
                    this.emulateOuterAccess(syntheticArg.actualOuterLocalVariable);
                }
                ++i;
            }
        }
    }

    public TypeDeclaration referenceType() {
        return this.methodScope().referenceType();
    }

    public int scopeIndex() {
        if (this instanceof MethodScope) {
            return -1;
        }
        BlockScope parentScope = (BlockScope)this.parent;
        Scope[] parentSubscopes = parentScope.subscopes;
        int i = 0;
        int max = parentScope.subscopeCount;
        while (i < max) {
            if (parentSubscopes[i] == this) {
                return i;
            }
            ++i;
        }
        return -1;
    }

    @Override
    int startIndex() {
        return this.startIndex;
    }

    public String toString() {
        return this.toString(0);
    }

    public String toString(int tab) {
        String s = this.basicToString(tab);
        int i = 0;
        while (i < this.subscopeCount) {
            if (this.subscopes[i] instanceof BlockScope) {
                s = String.valueOf(s) + ((BlockScope)this.subscopes[i]).toString(tab + 1) + "\n";
            }
            ++i;
        }
        return s;
    }

    public int registerTrackingVariable(FakedTrackingVariable fakedTrackingVariable) {
        if (this.trackingVariables == null) {
            this.trackingVariables = new ArrayList(3);
        }
        this.trackingVariables.add(fakedTrackingVariable);
        MethodScope outerMethodScope = this.outerMostMethodScope();
        return outerMethodScope.analysisIndex++;
    }

    public void removeTrackingVar(FakedTrackingVariable trackingVariable) {
        if (trackingVariable.innerTracker != null) {
            trackingVariable.innerTracker.withdraw();
            trackingVariable.innerTracker = null;
        }
        if (this.trackingVariables != null && this.trackingVariables.remove(trackingVariable)) {
            return;
        }
        if (this.parent instanceof BlockScope) {
            ((BlockScope)this.parent).removeTrackingVar(trackingVariable);
        }
    }

    public void pruneWrapperTrackingVar(FakedTrackingVariable trackingVariable) {
        this.trackingVariables.remove(trackingVariable);
    }

    public boolean hasResourceTrackers() {
        return this.trackingVariables != null && !this.trackingVariables.isEmpty();
    }

    public void checkUnclosedCloseables(FlowInfo flowInfo, FlowContext flowContext, ASTNode location, BlockScope locationScope) {
        if (!this.compilerOptions().analyseResourceLeaks) {
            return;
        }
        if (this.trackingVariables == null) {
            if (location != null && this.parent instanceof BlockScope && !this.isLambdaScope()) {
                ((BlockScope)this.parent).checkUnclosedCloseables(flowInfo, flowContext, location, locationScope);
            }
            return;
        }
        if (location != null && flowInfo.reachMode() != 0) {
            return;
        }
        FakedTrackingVariable returnVar = location instanceof ReturnStatement ? FakedTrackingVariable.getCloseTrackingVariable(((ReturnStatement)location).expression, flowInfo, flowContext) : null;
        FakedTrackingVariable.IteratorForReporting iterator = new FakedTrackingVariable.IteratorForReporting(this.trackingVariables, this, location != null);
        while (iterator.hasNext()) {
            FakedTrackingVariable trackingVar = (FakedTrackingVariable)iterator.next();
            if (returnVar != null && trackingVar.isResourceBeingReturned(returnVar) || location != null && trackingVar.hasDefinitelyNoResource(flowInfo) || location != null && flowContext != null && flowContext.recordExitAgainstResource(this, flowInfo, trackingVar, location)) continue;
            int status = trackingVar.findMostSpecificStatus(flowInfo, this, locationScope);
            if (status == 2) {
                this.reportResourceLeak(trackingVar, location, status);
                continue;
            }
            if (location == null && trackingVar.reportRecordedErrors(this, status, flowInfo.reachMode() != 0)) continue;
            if (status == 16) {
                this.reportResourceLeak(trackingVar, location, status);
                continue;
            }
            if (status != 4 || this.environment().globalOptions.complianceLevel < 0x330000L) continue;
            trackingVar.reportExplicitClosing(this.problemReporter());
        }
        if (location == null) {
            int i = 0;
            while (i < this.localIndex) {
                this.locals[i].closeTracker = null;
                ++i;
            }
            this.trackingVariables = null;
        }
    }

    private void reportResourceLeak(FakedTrackingVariable trackingVar, ASTNode location, int nullStatus) {
        if (location != null) {
            trackingVar.recordErrorLocation(location, nullStatus);
        } else {
            trackingVar.reportError(this.problemReporter(), null, nullStatus);
        }
    }

    public void correlateTrackingVarsIfElse(FlowInfo thenFlowInfo, FlowInfo elseFlowInfo) {
        block6: {
            if (this.trackingVariables == null) break block6;
            int trackVarCount = this.trackingVariables.size();
            int i = 0;
            while (i < trackVarCount) {
                block9: {
                    FakedTrackingVariable trackingVar;
                    block11: {
                        block10: {
                            block7: {
                                int nullStatus;
                                boolean hasNullInfoInElse;
                                boolean hasNullInfoInThen;
                                block8: {
                                    trackingVar = (FakedTrackingVariable)this.trackingVariables.get(i);
                                    if (trackingVar.originalBinding != null) break block7;
                                    hasNullInfoInThen = thenFlowInfo.hasNullInfoFor(trackingVar.binding);
                                    hasNullInfoInElse = elseFlowInfo.hasNullInfoFor(trackingVar.binding);
                                    if (!hasNullInfoInThen || hasNullInfoInElse) break block8;
                                    nullStatus = thenFlowInfo.nullStatus(trackingVar.binding);
                                    elseFlowInfo.markNullStatus(trackingVar.binding, nullStatus);
                                    break block9;
                                }
                                if (hasNullInfoInThen || !hasNullInfoInElse) break block9;
                                nullStatus = elseFlowInfo.nullStatus(trackingVar.binding);
                                thenFlowInfo.markNullStatus(trackingVar.binding, nullStatus);
                                break block9;
                            }
                            if (!thenFlowInfo.isDefinitelyNonNull(trackingVar.binding) || !elseFlowInfo.isDefinitelyNull(trackingVar.originalBinding)) break block10;
                            elseFlowInfo.markAsDefinitelyNonNull(trackingVar.binding);
                            break block9;
                        }
                        if (!elseFlowInfo.isDefinitelyNonNull(trackingVar.binding) || !thenFlowInfo.isDefinitelyNull(trackingVar.originalBinding)) break block11;
                        thenFlowInfo.markAsDefinitelyNonNull(trackingVar.binding);
                        break block9;
                    }
                    if (thenFlowInfo == FlowInfo.DEAD_END || elseFlowInfo == FlowInfo.DEAD_END) break block9;
                    int j = i + 1;
                    while (j < trackVarCount) {
                        block12: {
                            int newStatus;
                            FakedTrackingVariable var2;
                            block14: {
                                boolean var2SeenInElse;
                                boolean var2SeenInThen;
                                boolean var1SeenInElse;
                                boolean var1SeenInThen;
                                block13: {
                                    var2 = (FakedTrackingVariable)this.trackingVariables.get(j);
                                    if (trackingVar.originalBinding != var2.originalBinding) break block12;
                                    var1SeenInThen = thenFlowInfo.hasNullInfoFor(trackingVar.binding);
                                    var1SeenInElse = elseFlowInfo.hasNullInfoFor(trackingVar.binding);
                                    var2SeenInThen = thenFlowInfo.hasNullInfoFor(var2.binding);
                                    var2SeenInElse = elseFlowInfo.hasNullInfoFor(var2.binding);
                                    if (var1SeenInThen || !var1SeenInElse || !var2SeenInThen || var2SeenInElse) break block13;
                                    newStatus = FlowInfo.mergeNullStatus(thenFlowInfo.nullStatus(var2.binding), elseFlowInfo.nullStatus(trackingVar.binding));
                                    break block14;
                                }
                                if (!var1SeenInThen || var1SeenInElse || var2SeenInThen || !var2SeenInElse) break block12;
                                newStatus = FlowInfo.mergeNullStatus(thenFlowInfo.nullStatus(trackingVar.binding), elseFlowInfo.nullStatus(var2.binding));
                            }
                            thenFlowInfo.markNullStatus(trackingVar.binding, newStatus);
                            elseFlowInfo.markNullStatus(trackingVar.binding, newStatus);
                            trackingVar.originalBinding.closeTracker = trackingVar;
                            thenFlowInfo.markNullStatus(var2.binding, 4);
                            elseFlowInfo.markNullStatus(var2.binding, 4);
                        }
                        ++j;
                    }
                }
                ++i;
            }
        }
        if (this.parent instanceof BlockScope) {
            ((BlockScope)this.parent).correlateTrackingVarsIfElse(thenFlowInfo, elseFlowInfo);
        }
    }

    public void checkAppropriateMethodAgainstSupers(char[] selector, MethodBinding compileTimeMethod, TypeBinding[] parameters, InvocationSite site) {
        ReferenceBinding[] superInterfaces;
        ReferenceBinding enclosingType = this.enclosingReceiverType();
        MethodBinding otherMethod = this.getMethod(enclosingType.superclass(), selector, parameters, site);
        if (this.checkAppropriate(compileTimeMethod, otherMethod, site) && (superInterfaces = enclosingType.superInterfaces()) != null) {
            int i = 0;
            while (i < superInterfaces.length) {
                otherMethod = this.getMethod(superInterfaces[i], selector, parameters, site);
                if (!this.checkAppropriate(compileTimeMethod, otherMethod, site)) break;
                ++i;
            }
        }
    }

    private boolean checkAppropriate(MethodBinding compileTimeDeclaration, MethodBinding otherMethod, InvocationSite location) {
        if (otherMethod == null || !otherMethod.isValidBinding() || otherMethod.original() == compileTimeDeclaration.original()) {
            return true;
        }
        if (MethodVerifier.doesMethodOverride(otherMethod, compileTimeDeclaration, this.environment())) {
            this.problemReporter().illegalSuperCallBypassingOverride(location, compileTimeDeclaration, otherMethod.declaringClass);
            return false;
        }
        return true;
    }
}

