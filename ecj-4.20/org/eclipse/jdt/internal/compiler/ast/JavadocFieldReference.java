/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.ast.FieldReference;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.ProblemFieldBinding;
import org.eclipse.jdt.internal.compiler.lookup.ProblemMethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.ProblemReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;

public class JavadocFieldReference
extends FieldReference {
    public int tagSourceStart;
    public int tagSourceEnd;
    public int tagValue;
    public MethodBinding methodBinding;

    public JavadocFieldReference(char[] source, long pos) {
        super(source, pos);
        this.bits |= 0x8000;
    }

    protected TypeBinding internalResolveType(Scope scope) {
        FieldBinding fieldBinding;
        this.constant = Constant.NotAConstant;
        this.actualReceiverType = this.receiver == null ? scope.enclosingReceiverType() : (scope.kind == 3 ? this.receiver.resolveType((ClassScope)scope) : this.receiver.resolveType((BlockScope)scope));
        if (this.actualReceiverType == null) {
            return null;
        }
        Binding binding = fieldBinding = this.receiver != null && this.receiver.isThis() ? scope.classScope().getBinding(this.token, this.bits & 7, this, true) : scope.getField(this.actualReceiverType, this.token, this);
        if (!fieldBinding.isValidBinding()) {
            switch (fieldBinding.problemId()) {
                case 5: 
                case 6: 
                case 7: {
                    FieldBinding closestMatch = ((ProblemFieldBinding)fieldBinding).closestMatch;
                    if (closestMatch == null) break;
                    fieldBinding = closestMatch;
                }
            }
        }
        if (!fieldBinding.isValidBinding() || !(fieldBinding instanceof FieldBinding)) {
            if (this.receiver.resolvedType instanceof ProblemReferenceBinding) {
                return null;
            }
            if (this.actualReceiverType instanceof ReferenceBinding) {
                ReferenceBinding refBinding = (ReferenceBinding)this.actualReceiverType;
                char[] selector = this.token;
                MethodBinding possibleMethod = null;
                if (CharOperation.equals(this.actualReceiverType.sourceName(), selector)) {
                    possibleMethod = scope.getConstructor(refBinding, Binding.NO_TYPES, this);
                } else {
                    MethodBinding methodBinding = possibleMethod = this.receiver.isThis() ? scope.getImplicitMethod(selector, Binding.NO_TYPES, this) : scope.getMethod(refBinding, selector, Binding.NO_TYPES, this);
                }
                if (possibleMethod.isValidBinding()) {
                    this.methodBinding = possibleMethod;
                } else {
                    ProblemMethodBinding problemMethodBinding = (ProblemMethodBinding)possibleMethod;
                    if (problemMethodBinding.closestMatch == null) {
                        if (fieldBinding.isValidBinding()) {
                            fieldBinding = new ProblemFieldBinding(refBinding, ((Binding)fieldBinding).readableName(), 1);
                        }
                        scope.problemReporter().javadocInvalidField(this, fieldBinding, this.actualReceiverType, scope.getDeclarationModifiers());
                    } else {
                        this.methodBinding = problemMethodBinding.closestMatch;
                    }
                }
            }
            return null;
        }
        this.binding = fieldBinding;
        if (this.isFieldUseDeprecated(this.binding, scope, this.bits)) {
            scope.problemReporter().javadocDeprecatedField(this.binding, this, scope.getDeclarationModifiers());
        }
        this.resolvedType = this.binding.type;
        return this.resolvedType;
    }

    @Override
    public boolean isSuperAccess() {
        return (this.bits & 0x4000) != 0;
    }

    @Override
    public StringBuffer printExpression(int indent, StringBuffer output) {
        if (this.receiver != null) {
            this.receiver.printExpression(0, output);
        }
        output.append('#').append(this.token);
        return output;
    }

    @Override
    public TypeBinding resolveType(BlockScope scope) {
        return this.internalResolveType(scope);
    }

    @Override
    public TypeBinding resolveType(ClassScope scope) {
        return this.internalResolveType(scope);
    }

    @Override
    public void traverse(ASTVisitor visitor, BlockScope scope) {
        if (visitor.visit(this, scope) && this.receiver != null) {
            this.receiver.traverse(visitor, scope);
        }
        visitor.endVisit(this, scope);
    }

    @Override
    public void traverse(ASTVisitor visitor, ClassScope scope) {
        if (visitor.visit(this, scope) && this.receiver != null) {
            this.receiver.traverse(visitor, scope);
        }
        visitor.endVisit(this, scope);
    }
}

