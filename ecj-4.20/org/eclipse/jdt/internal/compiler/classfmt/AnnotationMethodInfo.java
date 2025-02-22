/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.classfmt;

import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.classfmt.AnnotationInfo;
import org.eclipse.jdt.internal.compiler.classfmt.AnnotationMethodInfoWithAnnotations;
import org.eclipse.jdt.internal.compiler.classfmt.AnnotationMethodInfoWithTypeAnnotations;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileStruct;
import org.eclipse.jdt.internal.compiler.classfmt.MethodInfo;
import org.eclipse.jdt.internal.compiler.classfmt.MethodInfoWithAnnotations;
import org.eclipse.jdt.internal.compiler.classfmt.MethodInfoWithTypeAnnotations;
import org.eclipse.jdt.internal.compiler.classfmt.TypeAnnotationInfo;
import org.eclipse.jdt.internal.compiler.codegen.AttributeNamesConstants;

public class AnnotationMethodInfo
extends MethodInfo {
    protected Object defaultValue = null;

    public static MethodInfo createAnnotationMethod(byte[] classFileBytes, int[] offsets, int offset, long version) {
        MethodInfo methodInfo = new MethodInfo(classFileBytes, offsets, offset, version);
        int attributesCount = methodInfo.u2At(6);
        int readOffset = 8;
        ClassFileStruct[] annotations = null;
        Object defaultValue = null;
        ClassFileStruct[] typeAnnotations = null;
        int i = 0;
        while (i < attributesCount) {
            int utf8Offset = methodInfo.constantPoolOffsets[methodInfo.u2At(readOffset)] - methodInfo.structOffset;
            char[] attributeName = methodInfo.utf8At(utf8Offset + 3, methodInfo.u2At(utf8Offset + 1));
            if (attributeName.length > 0) {
                switch (attributeName[0]) {
                    case 'A': {
                        if (!CharOperation.equals(attributeName, AttributeNamesConstants.AnnotationDefaultName)) break;
                        AnnotationInfo info = new AnnotationInfo(methodInfo.reference, methodInfo.constantPoolOffsets, readOffset + 6 + methodInfo.structOffset);
                        defaultValue = info.decodeDefaultValue();
                        break;
                    }
                    case 'S': {
                        if (!CharOperation.equals(AttributeNamesConstants.SignatureName, attributeName)) break;
                        methodInfo.signatureUtf8Offset = methodInfo.constantPoolOffsets[methodInfo.u2At(readOffset + 6)] - methodInfo.structOffset;
                        break;
                    }
                    case 'R': {
                        ClassFileStruct[] newAnnotations;
                        int length;
                        AnnotationInfo[] methodAnnotations = null;
                        TypeAnnotationInfo[] methodTypeAnnotations = null;
                        if (CharOperation.equals(attributeName, AttributeNamesConstants.RuntimeVisibleAnnotationsName)) {
                            methodAnnotations = AnnotationMethodInfo.decodeMethodAnnotations(readOffset, true, methodInfo);
                        } else if (CharOperation.equals(attributeName, AttributeNamesConstants.RuntimeInvisibleAnnotationsName)) {
                            methodAnnotations = AnnotationMethodInfo.decodeMethodAnnotations(readOffset, false, methodInfo);
                        } else if (CharOperation.equals(attributeName, AttributeNamesConstants.RuntimeVisibleTypeAnnotationsName)) {
                            methodTypeAnnotations = AnnotationMethodInfo.decodeTypeAnnotations(readOffset, true, methodInfo);
                        } else if (CharOperation.equals(attributeName, AttributeNamesConstants.RuntimeInvisibleTypeAnnotationsName)) {
                            methodTypeAnnotations = AnnotationMethodInfo.decodeTypeAnnotations(readOffset, false, methodInfo);
                        }
                        if (methodAnnotations != null) {
                            if (annotations == null) {
                                annotations = methodAnnotations;
                                break;
                            }
                            length = annotations.length;
                            newAnnotations = new AnnotationInfo[length + methodAnnotations.length];
                            System.arraycopy(annotations, 0, newAnnotations, 0, length);
                            System.arraycopy(methodAnnotations, 0, newAnnotations, length, methodAnnotations.length);
                            annotations = newAnnotations;
                            break;
                        }
                        if (methodTypeAnnotations == null) break;
                        if (typeAnnotations == null) {
                            typeAnnotations = methodTypeAnnotations;
                            break;
                        }
                        length = typeAnnotations.length;
                        newAnnotations = new TypeAnnotationInfo[length + methodTypeAnnotations.length];
                        System.arraycopy(typeAnnotations, 0, newAnnotations, 0, length);
                        System.arraycopy(methodTypeAnnotations, 0, newAnnotations, length, methodTypeAnnotations.length);
                        typeAnnotations = newAnnotations;
                    }
                }
            }
            readOffset = (int)((long)readOffset + (6L + methodInfo.u4At(readOffset + 2)));
            ++i;
        }
        methodInfo.attributeBytes = readOffset;
        if (defaultValue != null) {
            if (typeAnnotations != null) {
                return new AnnotationMethodInfoWithTypeAnnotations(methodInfo, defaultValue, (AnnotationInfo[])annotations, (TypeAnnotationInfo[])typeAnnotations);
            }
            if (annotations != null) {
                return new AnnotationMethodInfoWithAnnotations(methodInfo, defaultValue, (AnnotationInfo[])annotations);
            }
            return new AnnotationMethodInfo(methodInfo, defaultValue);
        }
        if (typeAnnotations != null) {
            return new MethodInfoWithTypeAnnotations(methodInfo, (AnnotationInfo[])annotations, null, (TypeAnnotationInfo[])typeAnnotations);
        }
        if (annotations != null) {
            return new MethodInfoWithAnnotations(methodInfo, (AnnotationInfo[])annotations);
        }
        return methodInfo;
    }

    AnnotationMethodInfo(MethodInfo methodInfo, Object defaultValue) {
        super(methodInfo.reference, methodInfo.constantPoolOffsets, methodInfo.structOffset, methodInfo.version);
        this.defaultValue = defaultValue;
        this.accessFlags = methodInfo.accessFlags;
        this.attributeBytes = methodInfo.attributeBytes;
        this.descriptor = methodInfo.descriptor;
        this.exceptionNames = methodInfo.exceptionNames;
        this.name = methodInfo.name;
        this.signature = methodInfo.signature;
        this.signatureUtf8Offset = methodInfo.signatureUtf8Offset;
        this.tagBits = methodInfo.tagBits;
    }

    @Override
    public Object getDefaultValue() {
        return this.defaultValue;
    }
}

