/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.ast;

import java.util.function.Consumer;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.SingleTypeReference;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.lookup.AnnotationBinding;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;

public class ArrayTypeReference
extends SingleTypeReference {
    public int dimensions;
    private Annotation[][] annotationsOnDimensions;
    public int originalSourceEnd;
    public int extendedDimensions;
    public TypeBinding leafComponentTypeWithoutDefaultNullness;

    public ArrayTypeReference(char[] source, int dimensions, long pos) {
        super(source, pos);
        this.originalSourceEnd = this.sourceEnd;
        this.dimensions = dimensions;
        this.annotationsOnDimensions = null;
    }

    public ArrayTypeReference(char[] source, int dimensions, Annotation[][] annotationsOnDimensions, long pos) {
        this(source, dimensions, pos);
        if (annotationsOnDimensions != null) {
            this.bits |= 0x100000;
        }
        this.annotationsOnDimensions = annotationsOnDimensions;
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
        return new char[][]{CharOperation.concat(this.token, dimChars)};
    }

    @Override
    protected TypeBinding getTypeBinding(Scope scope) {
        if (this.resolvedType != null) {
            return this.resolvedType;
        }
        if (this.dimensions > 255) {
            scope.problemReporter().tooManyDimensions(this);
        }
        TypeBinding leafComponentType = scope.getType(this.token);
        return scope.createArrayType(leafComponentType, this.dimensions);
    }

    @Override
    public StringBuffer printExpression(int indent, StringBuffer output) {
        super.printExpression(indent, output);
        if ((this.bits & 0x4000) != 0) {
            int i = 0;
            while (i < this.dimensions - 1) {
                if (this.annotationsOnDimensions != null && this.annotationsOnDimensions[i] != null) {
                    output.append(' ');
                    ArrayTypeReference.printAnnotations(this.annotationsOnDimensions[i], output);
                    output.append(' ');
                }
                output.append("[]");
                ++i;
            }
            if (this.annotationsOnDimensions != null && this.annotationsOnDimensions[this.dimensions - 1] != null) {
                output.append(' ');
                ArrayTypeReference.printAnnotations(this.annotationsOnDimensions[this.dimensions - 1], output);
                output.append(' ');
            }
            output.append("...");
        } else {
            int i = 0;
            while (i < this.dimensions) {
                if (this.annotationsOnDimensions != null && this.annotationsOnDimensions[i] != null) {
                    output.append(" ");
                    ArrayTypeReference.printAnnotations(this.annotationsOnDimensions[i], output);
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
            if (this.annotations != null) {
                Annotation[] typeAnnotations = this.annotations[0];
                int i = 0;
                int length = typeAnnotations == null ? 0 : typeAnnotations.length;
                while (i < length) {
                    typeAnnotations[i].traverse(visitor, scope);
                    ++i;
                }
            }
            if (this.annotationsOnDimensions != null) {
                int i = 0;
                int max = this.annotationsOnDimensions.length;
                while (i < max) {
                    Annotation[] annotations2 = this.annotationsOnDimensions[i];
                    if (annotations2 != null) {
                        int j = 0;
                        int max2 = annotations2.length;
                        while (j < max2) {
                            Annotation annotation = annotations2[j];
                            annotation.traverse(visitor, scope);
                            ++j;
                        }
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
            if (this.annotations != null) {
                Annotation[] typeAnnotations = this.annotations[0];
                int i = 0;
                int length = typeAnnotations == null ? 0 : typeAnnotations.length;
                while (i < length) {
                    typeAnnotations[i].traverse(visitor, scope);
                    ++i;
                }
            }
            if (this.annotationsOnDimensions != null) {
                int i = 0;
                int max = this.annotationsOnDimensions.length;
                while (i < max) {
                    Annotation[] annotations2 = this.annotationsOnDimensions[i];
                    if (annotations2 != null) {
                        int j = 0;
                        int max2 = annotations2.length;
                        while (j < max2) {
                            Annotation annotation = annotations2[j];
                            annotation.traverse(visitor, scope);
                            ++j;
                        }
                    }
                    ++i;
                }
            }
        }
        visitor.endVisit(this, scope);
    }

    @Override
    protected TypeBinding internalResolveType(Scope scope, int location) {
        TypeBinding internalResolveType = super.internalResolveType(scope, location);
        internalResolveType = ArrayTypeReference.maybeMarkArrayContentsNonNull(scope, internalResolveType, this.sourceStart, this.dimensions, leafType -> {
            TypeBinding typeBinding = this.leafComponentTypeWithoutDefaultNullness = leafType;
        });
        return internalResolveType;
    }

    static TypeBinding maybeMarkArrayContentsNonNull(Scope scope, TypeBinding typeBinding, int sourceStart, int dimensions, Consumer<TypeBinding> leafConsumer) {
        LookupEnvironment environment = scope.environment();
        if (environment.usesNullTypeAnnotations() && scope.hasDefaultNullnessFor(512, sourceStart)) {
            AnnotationBinding nonNullAnnotation = environment.getNonNullAnnotation();
            typeBinding = ArrayTypeReference.addNonNullToDimensions(scope, typeBinding, nonNullAnnotation, dimensions);
            TypeBinding leafComponentType = typeBinding.leafComponentType();
            if ((leafComponentType.tagBits & 0x180000000000000L) == 0L && leafComponentType.acceptsNonNullDefault()) {
                if (leafConsumer != null) {
                    leafConsumer.accept(leafComponentType);
                }
                TypeBinding nonNullLeafComponentType = scope.environment().createAnnotatedType(leafComponentType, new AnnotationBinding[]{nonNullAnnotation});
                typeBinding = scope.createArrayType(nonNullLeafComponentType, typeBinding.dimensions(), typeBinding.getTypeAnnotations());
            }
        }
        return typeBinding;
    }

    static TypeBinding addNonNullToDimensions(Scope scope, TypeBinding typeBinding, AnnotationBinding nonNullAnnotation, int dimensions2) {
        AnnotationBinding[][] newAnnots = new AnnotationBinding[dimensions2][];
        AnnotationBinding[] oldAnnots = typeBinding.getTypeAnnotations();
        if (oldAnnots == null) {
            int i = 1;
            while (i < dimensions2) {
                newAnnots[i] = new AnnotationBinding[]{nonNullAnnotation};
                ++i;
            }
        } else {
            int j = 0;
            int i = 0;
            while (i < dimensions2) {
                if (j >= oldAnnots.length || oldAnnots[j] == null) {
                    if (i != 0) {
                        newAnnots[i] = new AnnotationBinding[]{nonNullAnnotation};
                    }
                    ++j;
                } else {
                    AnnotationBinding[] annotationsForDimension;
                    int k = j;
                    boolean seen = false;
                    while (oldAnnots[k] != null) {
                        seen |= oldAnnots[k].getAnnotationType().hasNullBit(96);
                        ++k;
                    }
                    if (seen || i == 0) {
                        if (k > j) {
                            annotationsForDimension = new AnnotationBinding[k - j];
                            System.arraycopy(oldAnnots, j, annotationsForDimension, 0, k - j);
                            newAnnots[i] = annotationsForDimension;
                        }
                    } else {
                        annotationsForDimension = new AnnotationBinding[k - j + 1];
                        annotationsForDimension[0] = nonNullAnnotation;
                        System.arraycopy(oldAnnots, j, annotationsForDimension, 1, k - j);
                        newAnnots[i] = annotationsForDimension;
                    }
                    j = k + 1;
                }
                ++i;
            }
        }
        return scope.environment().createAnnotatedType(typeBinding, newAnnots);
    }

    @Override
    public boolean hasNullTypeAnnotation(TypeReference.AnnotationPosition position) {
        switch (position) {
            case LEAF_TYPE: {
                return super.hasNullTypeAnnotation(position);
            }
            case MAIN_TYPE: {
                if (this.annotationsOnDimensions != null && this.annotationsOnDimensions.length > 0) {
                    Annotation[] innerAnnotations = this.annotationsOnDimensions[0];
                    return ArrayTypeReference.containsNullAnnotation(innerAnnotations);
                }
                return super.hasNullTypeAnnotation(position);
            }
            case ANY: {
                if (super.hasNullTypeAnnotation(position)) {
                    return true;
                }
                if (this.resolvedType != null && !this.resolvedType.hasNullTypeAnnotations()) {
                    return false;
                }
                if (this.annotationsOnDimensions == null) break;
                int i = 0;
                while (i < this.annotationsOnDimensions.length) {
                    Annotation[] innerAnnotations = this.annotationsOnDimensions[i];
                    if (ArrayTypeReference.containsNullAnnotation(innerAnnotations)) {
                        return true;
                    }
                    ++i;
                }
                break;
            }
        }
        return false;
    }
}

