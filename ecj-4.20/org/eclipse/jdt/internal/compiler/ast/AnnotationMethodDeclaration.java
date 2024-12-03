/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.ClassFile;
import org.eclipse.jdt.internal.compiler.CompilationResult;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.MemberValuePair;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeParameter;
import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.internal.compiler.lookup.ElementValuePair;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.parser.Parser;

public class AnnotationMethodDeclaration
extends MethodDeclaration {
    public Expression defaultValue;
    public int extendedDimensions;

    public AnnotationMethodDeclaration(CompilationResult compilationResult) {
        super(compilationResult);
    }

    @Override
    public void generateCode(ClassFile classFile) {
        classFile.generateMethodInfoHeader(this.binding);
        int methodAttributeOffset = classFile.contentsOffset;
        int attributeNumber = classFile.generateMethodInfoAttributes(this.binding, this);
        classFile.completeMethodInfo(this.binding, methodAttributeOffset, attributeNumber);
    }

    @Override
    public boolean isAnnotationMethod() {
        return true;
    }

    @Override
    public boolean isMethod() {
        return false;
    }

    @Override
    public void parseStatements(Parser parser, CompilationUnitDeclaration unit) {
    }

    @Override
    public StringBuffer print(int tab, StringBuffer output) {
        int i;
        TypeParameter[] typeParams;
        AnnotationMethodDeclaration.printIndent(tab, output);
        AnnotationMethodDeclaration.printModifiers(this.modifiers, output);
        if (this.annotations != null) {
            AnnotationMethodDeclaration.printAnnotations(this.annotations, output);
            output.append(' ');
        }
        if ((typeParams = this.typeParameters()) != null) {
            output.append('<');
            int max = typeParams.length - 1;
            int j = 0;
            while (j < max) {
                typeParams[j].print(0, output);
                output.append(", ");
                ++j;
            }
            typeParams[max].print(0, output);
            output.append('>');
        }
        this.printReturnType(0, output).append(this.selector).append('(');
        if (this.arguments != null) {
            i = 0;
            while (i < this.arguments.length) {
                if (i > 0) {
                    output.append(", ");
                }
                this.arguments[i].print(0, output);
                ++i;
            }
        }
        output.append(')');
        if (this.thrownExceptions != null) {
            output.append(" throws ");
            i = 0;
            while (i < this.thrownExceptions.length) {
                if (i > 0) {
                    output.append(", ");
                }
                this.thrownExceptions[i].print(0, output);
                ++i;
            }
        }
        if (this.defaultValue != null) {
            output.append(" default ");
            this.defaultValue.print(0, output);
        }
        this.printBody(tab + 1, output);
        return output;
    }

    @Override
    public void resolveStatements() {
        block10: {
            TypeBinding returnTypeBinding;
            block12: {
                block11: {
                    super.resolveStatements();
                    if (this.arguments != null || this.receiver != null) {
                        this.scope.problemReporter().annotationMembersCannotHaveParameters(this);
                    }
                    if (this.typeParameters != null) {
                        this.scope.problemReporter().annotationMembersCannotHaveTypeParameters(this);
                    }
                    if (this.extendedDimensions != 0) {
                        this.scope.problemReporter().illegalExtendedDimensions(this);
                    }
                    if (this.binding == null) {
                        return;
                    }
                    returnTypeBinding = this.binding.returnType;
                    if (returnTypeBinding == null) break block10;
                    TypeBinding leafReturnType = returnTypeBinding.leafComponentType();
                    if (returnTypeBinding.dimensions() > 1) break block11;
                    switch (leafReturnType.erasure().id) {
                        case 2: 
                        case 3: 
                        case 4: 
                        case 5: 
                        case 7: 
                        case 8: 
                        case 9: 
                        case 10: 
                        case 11: 
                        case 16: {
                            break block12;
                        }
                        default: {
                            if (leafReturnType.isEnum() || leafReturnType.isAnnotationType()) break block12;
                        }
                    }
                }
                this.scope.problemReporter().invalidAnnotationMemberType(this);
            }
            if (this.defaultValue != null) {
                MemberValuePair pair = new MemberValuePair(this.selector, this.sourceStart, this.sourceEnd, this.defaultValue);
                pair.binding = this.binding;
                if (pair.value.resolvedType == null) {
                    pair.resolveTypeExpecting(this.scope, returnTypeBinding);
                }
                this.binding.setDefaultValue(ElementValuePair.getValue(this.defaultValue));
            } else {
                this.binding.setDefaultValue(null);
            }
        }
    }

    @Override
    public void traverse(ASTVisitor visitor, ClassScope classScope) {
        if (visitor.visit(this, classScope)) {
            if (this.annotations != null) {
                int annotationsLength = this.annotations.length;
                int i = 0;
                while (i < annotationsLength) {
                    this.annotations[i].traverse(visitor, this.scope);
                    ++i;
                }
            }
            if (this.returnType != null) {
                this.returnType.traverse(visitor, this.scope);
            }
            if (this.defaultValue != null) {
                this.defaultValue.traverse(visitor, this.scope);
            }
        }
        visitor.endVisit(this, classScope);
    }
}

