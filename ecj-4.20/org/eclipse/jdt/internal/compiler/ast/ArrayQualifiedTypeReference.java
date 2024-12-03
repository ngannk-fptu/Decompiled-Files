/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.ArrayTypeReference;
import org.eclipse.jdt.internal.compiler.ast.QualifiedTypeReference;
import org.eclipse.jdt.internal.compiler.lookup.ArrayBinding;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.problem.AbortCompilation;

public class ArrayQualifiedTypeReference
extends QualifiedTypeReference {
    int dimensions;
    private Annotation[][] annotationsOnDimensions;
    public int extendedDimensions;

    public ArrayQualifiedTypeReference(char[][] sources, int dim, long[] poss) {
        super(sources, poss);
        this.dimensions = dim;
        this.annotationsOnDimensions = null;
    }

    public ArrayQualifiedTypeReference(char[][] sources, int dim, Annotation[][] annotationsOnDimensions, long[] poss) {
        this(sources, dim, poss);
        this.annotationsOnDimensions = annotationsOnDimensions;
        if (annotationsOnDimensions != null) {
            this.bits |= 0x100000;
        }
    }

    @Override
    public int dimensions() {
        return this.dimensions;
    }

    @Override
    public int extraDimensions() {
        return this.extendedDimensions;
    }

    @Override
    public Annotation[][] getAnnotationsOnDimensions(boolean useSourceOrder) {
        if (useSourceOrder || this.annotationsOnDimensions == null || this.annotationsOnDimensions.length == 0 || this.extendedDimensions == 0 || this.extendedDimensions == this.dimensions) {
            return this.annotationsOnDimensions;
        }
        Annotation[][] externalAnnotations = new Annotation[this.dimensions][];
        int baseDimensions = this.dimensions - this.extendedDimensions;
        System.arraycopy(this.annotationsOnDimensions, baseDimensions, externalAnnotations, 0, this.extendedDimensions);
        System.arraycopy(this.annotationsOnDimensions, 0, externalAnnotations, this.extendedDimensions, baseDimensions);
        return externalAnnotations;
    }

    @Override
    public void setAnnotationsOnDimensions(Annotation[][] annotationsOnDimensions) {
        this.annotationsOnDimensions = annotationsOnDimensions;
    }

    @Override
    public char[][] getParameterizedTypeName() {
        int dim = this.dimensions;
        char[] dimChars = new char[dim * 2];
        int i = 0;
        while (i < dim) {
            int index = i * 2;
            dimChars[index] = 91;
            dimChars[index + 1] = 93;
            ++i;
        }
        int length = this.tokens.length;
        char[][] qParamName = new char[length][];
        System.arraycopy(this.tokens, 0, qParamName, 0, length - 1);
        qParamName[length - 1] = CharOperation.concat(this.tokens[length - 1], dimChars);
        return qParamName;
    }

    @Override
    protected TypeBinding getTypeBinding(Scope scope) {
        if (this.resolvedType != null) {
            return this.resolvedType;
        }
        if (this.dimensions > 255) {
            scope.problemReporter().tooManyDimensions(this);
        }
        LookupEnvironment env = scope.environment();
        try {
            env.missingClassFileLocation = this;
            TypeBinding leafComponentType = super.getTypeBinding(scope);
            if (leafComponentType != null) {
                this.resolvedType = scope.createArrayType(leafComponentType, this.dimensions);
                ArrayBinding arrayBinding = this.resolvedType;
                return arrayBinding;
            }
            return null;
        }
        catch (AbortCompilation e) {
            e.updateContext(this, scope.referenceCompilationUnit().compilationResult);
            throw e;
        }
        finally {
            env.missingClassFileLocation = null;
        }
    }

    @Override
    protected TypeBinding internalResolveType(Scope scope, int location) {
        TypeBinding internalResolveType = super.internalResolveType(scope, location);
        internalResolveType = ArrayTypeReference.maybeMarkArrayContentsNonNull(scope, internalResolveType, this.sourceStart, this.dimensions, null);
        return internalResolveType;
    }

    @Override
    public StringBuffer printExpression(int indent, StringBuffer output) {
        super.printExpression(indent, output);
        if ((this.bits & 0x4000) != 0) {
            int i = 0;
            while (i < this.dimensions - 1) {
                if (this.annotationsOnDimensions != null && this.annotationsOnDimensions[i] != null) {
                    output.append(' ');
                    ArrayQualifiedTypeReference.printAnnotations(this.annotationsOnDimensions[i], output);
                    output.append(' ');
                }
                output.append("[]");
                ++i;
            }
            if (this.annotationsOnDimensions != null && this.annotationsOnDimensions[this.dimensions - 1] != null) {
                output.append(' ');
                ArrayQualifiedTypeReference.printAnnotations(this.annotationsOnDimensions[this.dimensions - 1], output);
                output.append(' ');
            }
            output.append("...");
        } else {
            int i = 0;
            while (i < this.dimensions) {
                if (this.annotationsOnDimensions != null && this.annotationsOnDimensions[i] != null) {
                    output.append(" ");
                    ArrayQualifiedTypeReference.printAnnotations(this.annotationsOnDimensions[i], output);
                    output.append(" ");
                }
                output.append("[]");
                ++i;
            }
        }
        return output;
    }

    @Override
    public void traverse(ASTVisitor visitor, BlockScope scope) {
        if (visitor.visit(this, scope)) {
            int j;
            if (this.annotations != null) {
                int annotationsLevels = this.annotations.length;
                int i = 0;
                while (i < annotationsLevels) {
                    int annotationsLength = this.annotations[i] == null ? 0 : this.annotations[i].length;
                    j = 0;
                    while (j < annotationsLength) {
                        this.annotations[i][j].traverse(visitor, scope);
                        ++j;
                    }
                    ++i;
                }
            }
            if (this.annotationsOnDimensions != null) {
                int i = 0;
                int max = this.annotationsOnDimensions.length;
                while (i < max) {
                    Annotation[] annotations2 = this.annotationsOnDimensions[i];
                    j = 0;
                    int max2 = annotations2 == null ? 0 : annotations2.length;
                    while (j < max2) {
                        Annotation annotation = annotations2[j];
                        annotation.traverse(visitor, scope);
                        ++j;
                    }
                    ++i;
                }
            }
        }
        visitor.endVisit(this, scope);
    }

    @Override
    public void traverse(ASTVisitor visitor, ClassScope scope) {
        if (visitor.visit(this, scope)) {
            int j;
            if (this.annotations != null) {
                int annotationsLevels = this.annotations.length;
                int i = 0;
                while (i < annotationsLevels) {
                    int annotationsLength = this.annotations[i] == null ? 0 : this.annotations[i].length;
                    j = 0;
                    while (j < annotationsLength) {
                        this.annotations[i][j].traverse(visitor, scope);
                        ++j;
                    }
                    ++i;
                }
            }
            if (this.annotationsOnDimensions != null) {
                int i = 0;
                int max = this.annotationsOnDimensions.length;
                while (i < max) {
                    Annotation[] annotations2 = this.annotationsOnDimensions[i];
                    j = 0;
                    int max2 = annotations2 == null ? 0 : annotations2.length;
                    while (j < max2) {
                        Annotation annotation = annotations2[j];
                        annotation.traverse(visitor, scope);
                        ++j;
                    }
                    ++i;
                }
            }
        }
        visitor.endVisit(this, scope);
    }
}

