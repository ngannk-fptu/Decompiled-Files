/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.ast.Assignment;
import org.eclipse.jdt.internal.compiler.ast.CompoundAssignment;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.Reference;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
import org.eclipse.jdt.internal.compiler.flow.FlowContext;
import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodScope;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;

public class ThisReference
extends Reference {
    public static ThisReference implicitThis() {
        ThisReference implicitThis = new ThisReference(0, 0);
        implicitThis.bits |= 4;
        return implicitThis;
    }

    public ThisReference(int sourceStart, int sourceEnd) {
        this.sourceStart = sourceStart;
        this.sourceEnd = sourceEnd;
    }

    @Override
    public FlowInfo analyseAssignment(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo, Assignment assignment, boolean isCompound) {
        return flowInfo;
    }

    public boolean checkAccess(BlockScope scope, ReferenceBinding receiverType) {
        TypeDeclaration type;
        MethodScope methodScope = scope.methodScope();
        if (methodScope.isConstructorCall) {
            methodScope.problemReporter().fieldsOrThisBeforeConstructorInvocation(this);
            return false;
        }
        if (methodScope.isStatic) {
            methodScope.problemReporter().errorThisSuperInStatic(this);
            return false;
        }
        if (this.isUnqualifiedSuper() && (type = methodScope.referenceType()) != null && TypeDeclaration.kind(type.modifiers) == 2) {
            methodScope.problemReporter().errorNoSuperInInterface(this);
            return false;
        }
        if (receiverType != null) {
            scope.tagAsAccessingEnclosingInstanceStateOf(receiverType, false);
        }
        return true;
    }

    @Override
    public boolean checkNPE(BlockScope scope, FlowContext flowContext, FlowInfo flowInfo, int ttlForFieldCheck) {
        return true;
    }

    @Override
    public void generateAssignment(BlockScope currentScope, CodeStream codeStream, Assignment assignment, boolean valueRequired) {
    }

    @Override
    public void generateCode(BlockScope currentScope, CodeStream codeStream, boolean valueRequired) {
        int pc = codeStream.position;
        if (valueRequired) {
            codeStream.aload_0();
        }
        if ((this.bits & 4) == 0) {
            codeStream.recordPositionsFrom(pc, this.sourceStart);
        }
    }

    @Override
    public void generateCompoundAssignment(BlockScope currentScope, CodeStream codeStream, Expression expression, int operator, int assignmentImplicitConversion, boolean valueRequired) {
    }

    @Override
    public void generatePostIncrement(BlockScope currentScope, CodeStream codeStream, CompoundAssignment postIncrement, boolean valueRequired) {
    }

    @Override
    public boolean isImplicitThis() {
        return (this.bits & 4) != 0;
    }

    @Override
    public boolean isThis() {
        return true;
    }

    @Override
    public int nullStatus(FlowInfo flowInfo, FlowContext flowContext) {
        return 4;
    }

    @Override
    public StringBuffer printExpression(int indent, StringBuffer output) {
        if (this.isImplicitThis()) {
            return output;
        }
        return output.append("this");
    }

    @Override
    public TypeBinding resolveType(BlockScope scope) {
        MethodBinding method;
        this.constant = Constant.NotAConstant;
        ReferenceBinding enclosingReceiverType = scope.enclosingReceiverType();
        if (!this.isImplicitThis() && !this.checkAccess(scope, enclosingReceiverType)) {
            return null;
        }
        this.resolvedType = enclosingReceiverType;
        MethodScope methodScope = scope.namedMethodScope();
        if (methodScope != null && (method = methodScope.referenceMethodBinding()) != null && method.receiver != null && TypeBinding.equalsEquals(method.receiver, this.resolvedType)) {
            this.resolvedType = method.receiver;
        }
        return this.resolvedType;
    }

    @Override
    public void traverse(ASTVisitor visitor, BlockScope blockScope) {
        visitor.visit(this, blockScope);
        visitor.endVisit(this, blockScope);
    }

    @Override
    public void traverse(ASTVisitor visitor, ClassScope blockScope) {
        visitor.visit(this, blockScope);
        visitor.endVisit(this, blockScope);
    }
}

