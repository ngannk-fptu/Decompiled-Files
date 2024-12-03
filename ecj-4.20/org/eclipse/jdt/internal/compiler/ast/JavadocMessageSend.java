/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.JavadocArgumentExpression;
import org.eclipse.jdt.internal.compiler.ast.MessageSend;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.ProblemMethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.ProblemReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;

public class JavadocMessageSend
extends MessageSend {
    public int tagSourceStart;
    public int tagSourceEnd;
    public int tagValue;

    public JavadocMessageSend(char[] name, long pos) {
        this.selector = name;
        this.nameSourcePosition = pos;
        this.sourceStart = (int)(this.nameSourcePosition >>> 32);
        this.sourceEnd = (int)this.nameSourcePosition;
        this.bits |= 0x8000;
    }

    public JavadocMessageSend(char[] name, long pos, JavadocArgumentExpression[] arguments) {
        this(name, pos);
        this.arguments = arguments;
    }

    private TypeBinding internalResolveType(Scope scope) {
        this.constant = Constant.NotAConstant;
        this.actualReceiverType = this.receiver == null ? scope.enclosingReceiverType() : (scope.kind == 3 ? this.receiver.resolveType((ClassScope)scope) : this.receiver.resolveType((BlockScope)scope));
        boolean hasArgsTypeVar = false;
        if (this.arguments != null) {
            this.argumentsHaveErrors = false;
            int length = this.arguments.length;
            this.argumentTypes = new TypeBinding[length];
            int i = 0;
            while (i < length) {
                Expression argument = this.arguments[i];
                this.argumentTypes[i] = scope.kind == 3 ? argument.resolveType((ClassScope)scope) : argument.resolveType((BlockScope)scope);
                if (this.argumentTypes[i] == null) {
                    this.argumentsHaveErrors = true;
                } else if (!hasArgsTypeVar) {
                    hasArgsTypeVar = this.argumentTypes[i].isTypeVariable();
                }
                ++i;
            }
            if (this.argumentsHaveErrors) {
                return null;
            }
        }
        if (this.actualReceiverType == null) {
            return null;
        }
        this.actualReceiverType = scope.environment().convertToRawType(this.receiver.resolvedType, true);
        ReferenceBinding enclosingType = scope.enclosingReceiverType();
        if (enclosingType != null && enclosingType.isCompatibleWith(this.actualReceiverType)) {
            this.bits |= 0x4000;
        }
        if (this.actualReceiverType.isBaseType()) {
            scope.problemReporter().javadocErrorNoMethodFor(this, this.actualReceiverType, this.argumentTypes, scope.getDeclarationModifiers());
            return null;
        }
        this.binding = scope.getMethod(this.actualReceiverType, this.selector, this.argumentTypes, this);
        if (!this.binding.isValidBinding()) {
            TypeBinding enclosingTypeBinding = this.actualReceiverType;
            MethodBinding methodBinding = this.binding;
            while (!methodBinding.isValidBinding() && (enclosingTypeBinding.isMemberType() || enclosingTypeBinding.isLocalType())) {
                enclosingTypeBinding = enclosingTypeBinding.enclosingType();
                methodBinding = scope.getMethod(enclosingTypeBinding, this.selector, this.argumentTypes, this);
            }
            if (methodBinding.isValidBinding()) {
                this.binding = methodBinding;
            } else {
                enclosingTypeBinding = this.actualReceiverType;
                MethodBinding contructorBinding = this.binding;
                if (!contructorBinding.isValidBinding() && CharOperation.equals(this.selector, enclosingTypeBinding.shortReadableName())) {
                    contructorBinding = scope.getConstructor((ReferenceBinding)enclosingTypeBinding, this.argumentTypes, this);
                }
                while (!contructorBinding.isValidBinding() && (enclosingTypeBinding.isMemberType() || enclosingTypeBinding.isLocalType())) {
                    if (!CharOperation.equals(this.selector, (enclosingTypeBinding = enclosingTypeBinding.enclosingType()).shortReadableName())) continue;
                    contructorBinding = scope.getConstructor((ReferenceBinding)enclosingTypeBinding, this.argumentTypes, this);
                }
                if (contructorBinding.isValidBinding()) {
                    this.binding = contructorBinding;
                }
            }
        }
        if (!this.binding.isValidBinding()) {
            switch (this.binding.problemId()) {
                case 3: 
                case 5: 
                case 6: 
                case 7: {
                    MethodBinding closestMatch = ((ProblemMethodBinding)this.binding).closestMatch;
                    if (closestMatch == null) break;
                    this.binding = closestMatch;
                }
            }
        }
        if (!this.binding.isValidBinding()) {
            MethodBinding closestMatch;
            if (this.receiver.resolvedType instanceof ProblemReferenceBinding) {
                return null;
            }
            if (this.binding.declaringClass == null) {
                if (this.actualReceiverType instanceof ReferenceBinding) {
                    this.binding.declaringClass = (ReferenceBinding)this.actualReceiverType;
                } else {
                    scope.problemReporter().javadocErrorNoMethodFor(this, this.actualReceiverType, this.argumentTypes, scope.getDeclarationModifiers());
                    return null;
                }
            }
            scope.problemReporter().javadocInvalidMethod(this, this.binding, scope.getDeclarationModifiers());
            if (this.binding instanceof ProblemMethodBinding && (closestMatch = ((ProblemMethodBinding)this.binding).closestMatch) != null) {
                this.binding = closestMatch;
            }
            this.resolvedType = this.binding == null ? null : this.binding.returnType;
            return this.resolvedType;
        }
        if (hasArgsTypeVar) {
            ProblemMethodBinding problem = new ProblemMethodBinding(this.binding, this.selector, this.argumentTypes, 1);
            scope.problemReporter().javadocInvalidMethod(this, problem, scope.getDeclarationModifiers());
        } else if (this.binding.isVarargs()) {
            int length = this.argumentTypes.length;
            if (this.binding.parameters.length != length || !this.argumentTypes[length - 1].isArrayType()) {
                ProblemMethodBinding problem = new ProblemMethodBinding(this.binding, this.selector, this.argumentTypes, 1);
                scope.problemReporter().javadocInvalidMethod(this, problem, scope.getDeclarationModifiers());
            }
        } else {
            int length = this.argumentTypes.length;
            int i = 0;
            while (i < length) {
                if (TypeBinding.notEquals(this.binding.parameters[i].erasure(), this.argumentTypes[i].erasure())) {
                    ProblemMethodBinding problem = new ProblemMethodBinding(this.binding, this.selector, this.argumentTypes, 1);
                    scope.problemReporter().javadocInvalidMethod(this, problem, scope.getDeclarationModifiers());
                    break;
                }
                ++i;
            }
        }
        if (this.isMethodUseDeprecated(this.binding, scope, true, this)) {
            scope.problemReporter().javadocDeprecatedMethod(this.binding, this, scope.getDeclarationModifiers());
        }
        this.resolvedType = this.binding.returnType;
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
        output.append('#').append(this.selector).append('(');
        if (this.arguments != null) {
            int i = 0;
            while (i < this.arguments.length) {
                if (i > 0) {
                    output.append(", ");
                }
                this.arguments[i].printExpression(0, output);
                ++i;
            }
        }
        return output.append(')');
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
    public void traverse(ASTVisitor visitor, BlockScope blockScope) {
        if (visitor.visit(this, blockScope)) {
            if (this.receiver != null) {
                this.receiver.traverse(visitor, blockScope);
            }
            if (this.arguments != null) {
                int argumentsLength = this.arguments.length;
                int i = 0;
                while (i < argumentsLength) {
                    this.arguments[i].traverse(visitor, blockScope);
                    ++i;
                }
            }
        }
        visitor.endVisit(this, blockScope);
    }

    @Override
    public void traverse(ASTVisitor visitor, ClassScope scope) {
        if (visitor.visit(this, scope)) {
            if (this.receiver != null) {
                this.receiver.traverse(visitor, scope);
            }
            if (this.arguments != null) {
                int argumentsLength = this.arguments.length;
                int i = 0;
                while (i < argumentsLength) {
                    this.arguments[i].traverse(visitor, scope);
                    ++i;
                }
            }
        }
        visitor.endVisit(this, scope);
    }
}

