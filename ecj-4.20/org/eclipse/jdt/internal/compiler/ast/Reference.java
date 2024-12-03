/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Argument;
import org.eclipse.jdt.internal.compiler.ast.Assignment;
import org.eclipse.jdt.internal.compiler.ast.CompoundAssignment;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
import org.eclipse.jdt.internal.compiler.flow.FlowContext;
import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodScope;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;

public abstract class Reference
extends Expression {
    public abstract FlowInfo analyseAssignment(BlockScope var1, FlowContext var2, FlowInfo var3, Assignment var4, boolean var5);

    @Override
    public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo) {
        return flowInfo;
    }

    @Override
    public boolean checkNPE(BlockScope scope, FlowContext flowContext, FlowInfo flowInfo, int ttlForFieldCheck) {
        if (flowContext.isNullcheckedFieldAccess(this)) {
            return true;
        }
        return super.checkNPE(scope, flowContext, flowInfo, ttlForFieldCheck);
    }

    protected boolean checkNullableFieldDereference(Scope scope, FieldBinding field, long sourcePosition, FlowContext flowContext, int ttlForFieldCheck) {
        if (field != null) {
            if (ttlForFieldCheck > 0 && scope.compilerOptions().enableSyntacticNullAnalysisForFields) {
                flowContext.recordNullCheckedFieldReference(this, ttlForFieldCheck);
            }
            if ((field.type.tagBits & 0x80000000000000L) != 0L) {
                scope.problemReporter().dereferencingNullableExpression(sourcePosition, scope.environment());
                return true;
            }
            if (field.type.isFreeTypeVariable()) {
                scope.problemReporter().fieldFreeTypeVariableReference(field, sourcePosition);
                return true;
            }
            if ((field.tagBits & 0x80000000000000L) != 0L) {
                scope.problemReporter().nullableFieldDereference(field, sourcePosition);
                return true;
            }
        }
        return false;
    }

    public FieldBinding fieldBinding() {
        return null;
    }

    public void fieldStore(Scope currentScope, CodeStream codeStream, FieldBinding fieldBinding, MethodBinding syntheticWriteAccessor, TypeBinding receiverType, boolean isImplicitThisReceiver, boolean valueRequired) {
        int pc = codeStream.position;
        if (fieldBinding.isStatic()) {
            if (valueRequired) {
                switch (fieldBinding.type.id) {
                    case 7: 
                    case 8: {
                        codeStream.dup2();
                        break;
                    }
                    default: {
                        codeStream.dup();
                    }
                }
            }
            if (syntheticWriteAccessor == null) {
                TypeBinding constantPoolDeclaringClass = CodeStream.getConstantPoolDeclaringClass(currentScope, fieldBinding, receiverType, isImplicitThisReceiver);
                codeStream.fieldAccess((byte)-77, fieldBinding, constantPoolDeclaringClass);
            } else {
                codeStream.invoke((byte)-72, syntheticWriteAccessor, null);
            }
        } else {
            if (valueRequired) {
                switch (fieldBinding.type.id) {
                    case 7: 
                    case 8: {
                        codeStream.dup2_x1();
                        break;
                    }
                    default: {
                        codeStream.dup_x1();
                    }
                }
            }
            if (syntheticWriteAccessor == null) {
                TypeBinding constantPoolDeclaringClass = CodeStream.getConstantPoolDeclaringClass(currentScope, fieldBinding, receiverType, isImplicitThisReceiver);
                codeStream.fieldAccess((byte)-75, fieldBinding, constantPoolDeclaringClass);
            } else {
                codeStream.invoke((byte)-72, syntheticWriteAccessor, null);
            }
        }
        codeStream.recordPositionsFrom(pc, this.sourceStart);
    }

    public abstract void generateAssignment(BlockScope var1, CodeStream var2, Assignment var3, boolean var4);

    public abstract void generateCompoundAssignment(BlockScope var1, CodeStream var2, Expression var3, int var4, int var5, boolean var6);

    public abstract void generatePostIncrement(BlockScope var1, CodeStream var2, CompoundAssignment var3, boolean var4);

    public boolean isEquivalent(Reference reference) {
        return false;
    }

    public FieldBinding lastFieldBinding() {
        return null;
    }

    @Override
    public int nullStatus(FlowInfo flowInfo, FlowContext flowContext) {
        if ((this.implicitConversion & 0x200) != 0) {
            return 4;
        }
        FieldBinding fieldBinding = this.lastFieldBinding();
        if (fieldBinding != null) {
            if (fieldBinding.isFinal() && fieldBinding.constant() != Constant.NotAConstant) {
                return 4;
            }
            if (fieldBinding.isNonNull() || flowContext.isNullcheckedFieldAccess(this)) {
                return 4;
            }
            if (fieldBinding.isNullable()) {
                return 16;
            }
            if (fieldBinding.type.isFreeTypeVariable()) {
                return 48;
            }
        }
        if (this.resolvedType != null) {
            return FlowInfo.tagBitsToNullStatus(this.resolvedType.tagBits);
        }
        return 1;
    }

    void reportOnlyUselesslyReadPrivateField(BlockScope currentScope, FieldBinding fieldBinding, boolean valueRequired) {
        if (valueRequired) {
            fieldBinding.compoundUseFlag = 0;
            fieldBinding.modifiers |= 0x8000000;
        } else if (fieldBinding.isUsedOnlyInCompound()) {
            --fieldBinding.compoundUseFlag;
            if (fieldBinding.compoundUseFlag == 0 && fieldBinding.isOrEnclosedByPrivateType() && (this.implicitConversion & 0x400) == 0) {
                currentScope.problemReporter().unusedPrivateField(fieldBinding.sourceField());
            }
        }
    }

    static void reportOnlyUselesslyReadLocal(BlockScope currentScope, LocalVariableBinding localBinding, boolean valueRequired) {
        if (localBinding.declaration == null) {
            return;
        }
        if ((localBinding.declaration.bits & 0x40000000) == 0) {
            return;
        }
        if (localBinding.useFlag >= 1) {
            return;
        }
        if (valueRequired) {
            localBinding.useFlag = 1;
            return;
        }
        ++localBinding.useFlag;
        if (localBinding.useFlag != 0) {
            return;
        }
        if (localBinding.declaration instanceof Argument) {
            MethodScope methodScope = currentScope.methodScope();
            if (methodScope != null && !methodScope.isLambdaScope()) {
                boolean shouldReport;
                MethodBinding method = ((AbstractMethodDeclaration)methodScope.referenceContext()).binding;
                boolean bl = shouldReport = !method.isMain();
                if (method.isImplementing()) {
                    shouldReport &= currentScope.compilerOptions().reportUnusedParameterWhenImplementingAbstract;
                } else if (method.isOverriding()) {
                    shouldReport &= currentScope.compilerOptions().reportUnusedParameterWhenOverridingConcrete;
                }
                if (shouldReport) {
                    currentScope.problemReporter().unusedArgument(localBinding.declaration);
                }
            }
        } else {
            currentScope.problemReporter().unusedLocalVariable(localBinding.declaration);
        }
    }
}

