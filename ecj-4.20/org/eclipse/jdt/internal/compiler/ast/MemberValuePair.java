/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.ArrayAllocationExpression;
import org.eclipse.jdt.internal.compiler.ast.ArrayInitializer;
import org.eclipse.jdt.internal.compiler.ast.ClassLiteralAccess;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.NameReference;
import org.eclipse.jdt.internal.compiler.ast.NullLiteral;
import org.eclipse.jdt.internal.compiler.ast.SingleNameReference;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.internal.compiler.lookup.ElementValuePair;
import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;

public class MemberValuePair
extends ASTNode {
    public char[] name;
    public Expression value;
    public MethodBinding binding;
    public ElementValuePair compilerElementPair = null;

    public MemberValuePair(char[] token, int sourceStart, int sourceEnd, Expression value) {
        this.name = token;
        this.sourceStart = sourceStart;
        this.sourceEnd = sourceEnd;
        this.value = value;
        if (value instanceof ArrayInitializer) {
            value.bits |= 1;
        }
    }

    @Override
    public StringBuffer print(int indent, StringBuffer output) {
        output.append(this.name).append(" = ");
        this.value.print(0, output);
        return output;
    }

    public void resolveTypeExpecting(final BlockScope scope, final TypeBinding requiredType) {
        boolean[] shouldExit;
        TypeBinding valueType;
        if (this.compilerElementPair != null) {
            return;
        }
        if (this.value == null) {
            this.compilerElementPair = new ElementValuePair(this.name, this.value, this.binding);
            return;
        }
        if (requiredType == null) {
            if (this.value instanceof ArrayInitializer) {
                this.value.resolveTypeExpecting(scope, null);
            } else {
                this.value.resolveType(scope);
            }
            this.compilerElementPair = new ElementValuePair(this.name, this.value, this.binding);
            return;
        }
        this.value.setExpectedType(requiredType);
        if (this.value instanceof ArrayInitializer) {
            ArrayInitializer initializer = (ArrayInitializer)this.value;
            valueType = initializer.resolveTypeExpecting(scope, this.binding.returnType);
        } else if (this.value instanceof ArrayAllocationExpression) {
            scope.problemReporter().annotationValueMustBeArrayInitializer(this.binding.declaringClass, this.name, this.value);
            this.value.resolveType(scope);
            valueType = null;
        } else {
            valueType = this.value.resolveType(scope);
            ASTVisitor visitor = new ASTVisitor(){

                @Override
                public boolean visit(SingleNameReference reference, BlockScope scop) {
                    if (reference.binding instanceof LocalVariableBinding) {
                        ((LocalVariableBinding)reference.binding).useFlag = 1;
                    }
                    return true;
                }
            };
            this.value.traverse(visitor, scope);
        }
        this.compilerElementPair = new ElementValuePair(this.name, this.value, this.binding);
        if (valueType == null) {
            return;
        }
        final TypeBinding leafType = requiredType.leafComponentType();
        Runnable check = new Runnable(shouldExit = new boolean[1]){
            private final /* synthetic */ boolean[] val$shouldExit;
            {
                this.val$shouldExit = blArray;
            }

            @Override
            public void run() {
                if (!MemberValuePair.this.value.isConstantValueOfTypeAssignableToType(valueType, requiredType) && !valueType.isCompatibleWith(requiredType)) {
                    if (!requiredType.isArrayType() || requiredType.dimensions() != 1 || !MemberValuePair.this.value.isConstantValueOfTypeAssignableToType(valueType, leafType) && !valueType.isCompatibleWith(leafType)) {
                        if (leafType.isAnnotationType() && !valueType.isAnnotationType()) {
                            scope.problemReporter().annotationValueMustBeAnnotation(MemberValuePair.this.binding.declaringClass, MemberValuePair.this.name, MemberValuePair.this.value, leafType);
                        } else {
                            scope.problemReporter().typeMismatchError(valueType, requiredType, MemberValuePair.this.value, null);
                        }
                        this.val$shouldExit[0] = true;
                    }
                } else {
                    scope.compilationUnitScope().recordTypeConversion(requiredType.leafComponentType(), valueType.leafComponentType());
                    MemberValuePair.this.value.computeConversion(scope, requiredType, valueType);
                }
            }
        };
        if (!scope.deferCheck(check)) {
            check.run();
            if (shouldExit[0]) {
                return;
            }
        }
        switch (leafType.erasure().id) {
            case 2: 
            case 3: 
            case 4: 
            case 5: 
            case 7: 
            case 8: 
            case 9: 
            case 10: 
            case 11: {
                if (this.value instanceof ArrayInitializer) {
                    ArrayInitializer initializer = (ArrayInitializer)this.value;
                    Expression[] expressions = initializer.expressions;
                    if (expressions == null) break;
                    int i = 0;
                    int max = expressions.length;
                    while (i < max) {
                        Expression expression = expressions[i];
                        if (expression.resolvedType != null && expression.constant == Constant.NotAConstant) {
                            scope.problemReporter().annotationValueMustBeConstant(this.binding.declaringClass, this.name, expressions[i], false);
                        }
                        ++i;
                    }
                    break;
                }
                if (this.value.constant != Constant.NotAConstant) break;
                if (valueType.isArrayType()) {
                    scope.problemReporter().annotationValueMustBeArrayInitializer(this.binding.declaringClass, this.name, this.value);
                    break;
                }
                scope.problemReporter().annotationValueMustBeConstant(this.binding.declaringClass, this.name, this.value, false);
                break;
            }
            case 16: {
                if (this.value instanceof ArrayInitializer) {
                    ArrayInitializer initializer = (ArrayInitializer)this.value;
                    Expression[] expressions = initializer.expressions;
                    if (expressions == null) break;
                    int i = 0;
                    int max = expressions.length;
                    while (i < max) {
                        Expression currentExpression = expressions[i];
                        if (!(currentExpression instanceof ClassLiteralAccess)) {
                            scope.problemReporter().annotationValueMustBeClassLiteral(this.binding.declaringClass, this.name, currentExpression);
                        }
                        ++i;
                    }
                    break;
                }
                if (this.value instanceof ClassLiteralAccess) break;
                scope.problemReporter().annotationValueMustBeClassLiteral(this.binding.declaringClass, this.name, this.value);
                break;
            }
            default: {
                if (leafType.isEnum()) {
                    if (this.value instanceof NullLiteral) {
                        scope.problemReporter().annotationValueMustBeConstant(this.binding.declaringClass, this.name, this.value, true);
                        break;
                    }
                    if (this.value instanceof ArrayInitializer) {
                        ArrayInitializer initializer = (ArrayInitializer)this.value;
                        Expression[] expressions = initializer.expressions;
                        if (expressions == null) break;
                        int i = 0;
                        int max = expressions.length;
                        while (i < max) {
                            Expression currentExpression = expressions[i];
                            if (currentExpression instanceof NullLiteral) {
                                scope.problemReporter().annotationValueMustBeConstant(this.binding.declaringClass, this.name, currentExpression, true);
                            } else if (currentExpression instanceof NameReference) {
                                NameReference nameReference = (NameReference)currentExpression;
                                Binding nameReferenceBinding = nameReference.binding;
                                if (nameReferenceBinding.kind() == 1) {
                                    FieldBinding fieldBinding = (FieldBinding)nameReferenceBinding;
                                    if (!fieldBinding.declaringClass.isEnum()) {
                                        scope.problemReporter().annotationValueMustBeConstant(this.binding.declaringClass, this.name, currentExpression, true);
                                    }
                                }
                            }
                            ++i;
                        }
                        break;
                    }
                    if (this.value instanceof NameReference) {
                        NameReference nameReference = (NameReference)this.value;
                        Binding nameReferenceBinding = nameReference.binding;
                        if (nameReferenceBinding.kind() != 1) break;
                        FieldBinding fieldBinding = (FieldBinding)nameReferenceBinding;
                        if (fieldBinding.declaringClass.isEnum()) break;
                        if (!fieldBinding.type.isArrayType()) {
                            scope.problemReporter().annotationValueMustBeConstant(this.binding.declaringClass, this.name, this.value, true);
                            break;
                        }
                        scope.problemReporter().annotationValueMustBeArrayInitializer(this.binding.declaringClass, this.name, this.value);
                        break;
                    }
                    scope.problemReporter().annotationValueMustBeConstant(this.binding.declaringClass, this.name, this.value, true);
                    break;
                }
                if (!leafType.isAnnotationType()) break;
                if (!valueType.leafComponentType().isAnnotationType()) {
                    scope.problemReporter().annotationValueMustBeAnnotation(this.binding.declaringClass, this.name, this.value, leafType);
                    break;
                }
                if (this.value instanceof ArrayInitializer) {
                    ArrayInitializer initializer = (ArrayInitializer)this.value;
                    Expression[] expressions = initializer.expressions;
                    if (expressions == null) break;
                    int i = 0;
                    int max = expressions.length;
                    while (i < max) {
                        Expression currentExpression = expressions[i];
                        if (currentExpression instanceof NullLiteral || !(currentExpression instanceof Annotation)) {
                            scope.problemReporter().annotationValueMustBeAnnotation(this.binding.declaringClass, this.name, currentExpression, leafType);
                        }
                        ++i;
                    }
                    break;
                }
                if (this.value instanceof Annotation) break;
                scope.problemReporter().annotationValueMustBeAnnotation(this.binding.declaringClass, this.name, this.value, leafType);
            }
        }
    }

    @Override
    public void traverse(ASTVisitor visitor, BlockScope scope) {
        if (visitor.visit(this, scope) && this.value != null) {
            this.value.traverse(visitor, scope);
        }
        visitor.endVisit(this, scope);
    }

    public void traverse(ASTVisitor visitor, ClassScope scope) {
        if (visitor.visit(this, scope) && this.value != null) {
            this.value.traverse(visitor, scope);
        }
        visitor.endVisit(this, scope);
    }
}

