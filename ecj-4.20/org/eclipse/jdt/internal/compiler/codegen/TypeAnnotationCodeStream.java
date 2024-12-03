/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.codegen;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.jdt.internal.compiler.ClassFile;
import org.eclipse.jdt.internal.compiler.ast.ArrayAllocationExpression;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.codegen.AnnotationContext;
import org.eclipse.jdt.internal.compiler.codegen.StackMapFrameCodeStream;
import org.eclipse.jdt.internal.compiler.lookup.ArrayBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;

public class TypeAnnotationCodeStream
extends StackMapFrameCodeStream {
    public List<AnnotationContext> allTypeAnnotationContexts;

    public TypeAnnotationCodeStream(ClassFile givenClassFile) {
        super(givenClassFile);
        this.generateAttributes |= 0x20;
        this.allTypeAnnotationContexts = new ArrayList<AnnotationContext>();
    }

    private void addAnnotationContext(TypeReference typeReference, int info, int targetType, ArrayAllocationExpression allocationExpression) {
        allocationExpression.getAllAnnotationContexts(targetType, info, this.allTypeAnnotationContexts);
    }

    private void addAnnotationContext(TypeReference typeReference, int info, int targetType) {
        typeReference.getAllAnnotationContexts(targetType, info, this.allTypeAnnotationContexts);
    }

    private void addAnnotationContext(TypeReference typeReference, int info, int typeIndex, int targetType) {
        typeReference.getAllAnnotationContexts(targetType, info, typeIndex, this.allTypeAnnotationContexts);
    }

    @Override
    public void instance_of(TypeReference typeReference, TypeBinding typeBinding) {
        if (typeReference != null && (typeReference.bits & 0x100000) != 0) {
            this.addAnnotationContext(typeReference, this.position, 67);
        }
        super.instance_of(typeReference, typeBinding);
    }

    @Override
    public void multianewarray(TypeReference typeReference, TypeBinding typeBinding, int dimensions, ArrayAllocationExpression allocationExpression) {
        if (typeReference != null && (typeReference.bits & 0x100000) != 0) {
            this.addAnnotationContext(typeReference, this.position, 68, allocationExpression);
        }
        super.multianewarray(typeReference, typeBinding, dimensions, allocationExpression);
    }

    @Override
    public void new_(TypeReference typeReference, TypeBinding typeBinding) {
        if (typeReference != null && (typeReference.bits & 0x100000) != 0) {
            this.addAnnotationContext(typeReference, this.position, 68);
        }
        super.new_(typeReference, typeBinding);
    }

    @Override
    public void newArray(TypeReference typeReference, ArrayAllocationExpression allocationExpression, ArrayBinding arrayBinding) {
        if (typeReference != null && (typeReference.bits & 0x100000) != 0) {
            this.addAnnotationContext(typeReference, this.position, 68, allocationExpression);
        }
        super.newArray(typeReference, allocationExpression, arrayBinding);
    }

    @Override
    public void checkcast(TypeReference typeReference, TypeBinding typeBinding, int currentPosition) {
        if (typeReference != null) {
            TypeReference[] typeReferences = typeReference.getTypeReferences();
            int i = typeReferences.length - 1;
            while (i >= 0) {
                typeReference = typeReferences[i];
                if (typeReference != null) {
                    if ((typeReference.bits & 0x100000) != 0) {
                        if (!typeReference.resolvedType.isBaseType()) {
                            this.addAnnotationContext(typeReference, this.position, i, 71);
                        } else {
                            this.addAnnotationContext(typeReference, currentPosition, i, 71);
                        }
                    }
                    if (!typeReference.resolvedType.isBaseType()) {
                        super.checkcast(typeReference, typeReference.resolvedType, currentPosition);
                    }
                }
                --i;
            }
        } else {
            super.checkcast(null, typeBinding, currentPosition);
        }
    }

    @Override
    public void invoke(byte opcode, MethodBinding methodBinding, TypeBinding declaringClass, TypeReference[] typeArguments) {
        if (typeArguments != null) {
            int targetType = methodBinding.isConstructor() ? 72 : 73;
            int i = 0;
            int max = typeArguments.length;
            while (i < max) {
                TypeReference typeArgument = typeArguments[i];
                if ((typeArgument.bits & 0x100000) != 0) {
                    this.addAnnotationContext(typeArgument, this.position, i, targetType);
                }
                ++i;
            }
        }
        super.invoke(opcode, methodBinding, declaringClass, typeArguments);
    }

    @Override
    public void invokeDynamic(int bootStrapIndex, int argsSize, int returnTypeSize, char[] selector, char[] signature, boolean isConstructorReference, TypeReference lhsTypeReference, TypeReference[] typeArguments, int typeId, TypeBinding type) {
        if (lhsTypeReference != null && (lhsTypeReference.bits & 0x100000) != 0) {
            if (isConstructorReference) {
                this.addAnnotationContext(lhsTypeReference, this.position, 0, 69);
            } else {
                this.addAnnotationContext(lhsTypeReference, this.position, 0, 70);
            }
        }
        if (typeArguments != null) {
            int targetType = isConstructorReference ? 74 : 75;
            int i = 0;
            int max = typeArguments.length;
            while (i < max) {
                TypeReference typeArgument = typeArguments[i];
                if ((typeArgument.bits & 0x100000) != 0) {
                    this.addAnnotationContext(typeArgument, this.position, i, targetType);
                }
                ++i;
            }
        }
        super.invokeDynamic(bootStrapIndex, argsSize, returnTypeSize, selector, signature, isConstructorReference, lhsTypeReference, typeArguments, typeId, type);
    }

    @Override
    public void reset(ClassFile givenClassFile) {
        super.reset(givenClassFile);
        this.allTypeAnnotationContexts = new ArrayList<AnnotationContext>();
    }

    @Override
    public void init(ClassFile targetClassFile) {
        super.init(targetClassFile);
        this.allTypeAnnotationContexts = new ArrayList<AnnotationContext>();
    }
}

