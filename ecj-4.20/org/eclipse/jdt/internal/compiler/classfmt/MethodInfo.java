/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.classfmt;

import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.classfmt.AnnotationInfo;
import org.eclipse.jdt.internal.compiler.classfmt.BinaryTypeFormatter;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileStruct;
import org.eclipse.jdt.internal.compiler.classfmt.JavaBinaryNames;
import org.eclipse.jdt.internal.compiler.classfmt.MethodInfoWithAnnotations;
import org.eclipse.jdt.internal.compiler.classfmt.MethodInfoWithParameterAnnotations;
import org.eclipse.jdt.internal.compiler.classfmt.MethodInfoWithTypeAnnotations;
import org.eclipse.jdt.internal.compiler.classfmt.TypeAnnotationInfo;
import org.eclipse.jdt.internal.compiler.codegen.AttributeNamesConstants;
import org.eclipse.jdt.internal.compiler.codegen.ConstantPool;
import org.eclipse.jdt.internal.compiler.env.IBinaryAnnotation;
import org.eclipse.jdt.internal.compiler.env.IBinaryMethod;
import org.eclipse.jdt.internal.compiler.env.IBinaryTypeAnnotation;

public class MethodInfo
extends ClassFileStruct
implements IBinaryMethod,
Comparable {
    private static final char[][] noException = CharOperation.NO_CHAR_CHAR;
    private static final char[][] noArgumentNames = CharOperation.NO_CHAR_CHAR;
    private static final char[] ARG = "arg".toCharArray();
    protected int accessFlags = -1;
    protected int attributeBytes;
    protected char[] descriptor;
    protected volatile char[][] exceptionNames;
    protected char[] name;
    protected char[] signature;
    protected int signatureUtf8Offset = -1;
    protected long tagBits;
    protected volatile char[][] argumentNames;
    protected long version;

    public static MethodInfo createMethod(byte[] classFileBytes, int[] offsets, int offset, long version) {
        MethodInfo methodInfo = new MethodInfo(classFileBytes, offsets, offset, version);
        int attributesCount = methodInfo.u2At(6);
        int readOffset = 8;
        ClassFileStruct[] annotations = null;
        AnnotationInfo[][] parameterAnnotations = null;
        ClassFileStruct[] typeAnnotations = null;
        int i = 0;
        while (i < attributesCount) {
            int utf8Offset = methodInfo.constantPoolOffsets[methodInfo.u2At(readOffset)] - methodInfo.structOffset;
            char[] attributeName = methodInfo.utf8At(utf8Offset + 3, methodInfo.u2At(utf8Offset + 1));
            if (attributeName.length > 0) {
                switch (attributeName[0]) {
                    case 'M': {
                        if (!CharOperation.equals(attributeName, AttributeNamesConstants.MethodParametersName)) break;
                        methodInfo.decodeMethodParameters(readOffset, methodInfo);
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
                        AnnotationInfo[][] paramAnnotations = null;
                        TypeAnnotationInfo[] methodTypeAnnotations = null;
                        if (CharOperation.equals(attributeName, AttributeNamesConstants.RuntimeVisibleAnnotationsName)) {
                            methodAnnotations = MethodInfo.decodeMethodAnnotations(readOffset, true, methodInfo);
                        } else if (CharOperation.equals(attributeName, AttributeNamesConstants.RuntimeInvisibleAnnotationsName)) {
                            methodAnnotations = MethodInfo.decodeMethodAnnotations(readOffset, false, methodInfo);
                        } else if (CharOperation.equals(attributeName, AttributeNamesConstants.RuntimeVisibleParameterAnnotationsName)) {
                            paramAnnotations = MethodInfo.decodeParamAnnotations(readOffset, true, methodInfo);
                        } else if (CharOperation.equals(attributeName, AttributeNamesConstants.RuntimeInvisibleParameterAnnotationsName)) {
                            paramAnnotations = MethodInfo.decodeParamAnnotations(readOffset, false, methodInfo);
                        } else if (CharOperation.equals(attributeName, AttributeNamesConstants.RuntimeVisibleTypeAnnotationsName)) {
                            methodTypeAnnotations = MethodInfo.decodeTypeAnnotations(readOffset, true, methodInfo);
                        } else if (CharOperation.equals(attributeName, AttributeNamesConstants.RuntimeInvisibleTypeAnnotationsName)) {
                            methodTypeAnnotations = MethodInfo.decodeTypeAnnotations(readOffset, false, methodInfo);
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
                        if (paramAnnotations != null) {
                            int numberOfParameters = paramAnnotations.length;
                            if (parameterAnnotations == null) {
                                parameterAnnotations = paramAnnotations;
                                break;
                            }
                            int p = 0;
                            while (p < numberOfParameters) {
                                int numberOfAnnotations;
                                int n = numberOfAnnotations = paramAnnotations[p] == null ? 0 : paramAnnotations[p].length;
                                if (numberOfAnnotations > 0) {
                                    if (parameterAnnotations[p] == null) {
                                        parameterAnnotations[p] = paramAnnotations[p];
                                    } else {
                                        int length2 = parameterAnnotations[p].length;
                                        AnnotationInfo[] newAnnotations2 = new AnnotationInfo[length2 + numberOfAnnotations];
                                        System.arraycopy(parameterAnnotations[p], 0, newAnnotations2, 0, length2);
                                        System.arraycopy(paramAnnotations[p], 0, newAnnotations2, length2, numberOfAnnotations);
                                        parameterAnnotations[p] = newAnnotations2;
                                    }
                                }
                                ++p;
                            }
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
        if (typeAnnotations != null) {
            return new MethodInfoWithTypeAnnotations(methodInfo, (AnnotationInfo[])annotations, parameterAnnotations, (TypeAnnotationInfo[])typeAnnotations);
        }
        if (parameterAnnotations != null) {
            return new MethodInfoWithParameterAnnotations(methodInfo, (AnnotationInfo[])annotations, parameterAnnotations);
        }
        if (annotations != null) {
            return new MethodInfoWithAnnotations(methodInfo, (AnnotationInfo[])annotations);
        }
        return methodInfo;
    }

    static AnnotationInfo[] decodeAnnotations(int offset, boolean runtimeVisible, int numberOfAnnotations, MethodInfo methodInfo) {
        AnnotationInfo[] result = new AnnotationInfo[numberOfAnnotations];
        int readOffset = offset;
        int i = 0;
        while (i < numberOfAnnotations) {
            result[i] = new AnnotationInfo(methodInfo.reference, methodInfo.constantPoolOffsets, readOffset + methodInfo.structOffset, runtimeVisible, false);
            readOffset += result[i].readOffset;
            ++i;
        }
        return result;
    }

    static AnnotationInfo[] decodeMethodAnnotations(int offset, boolean runtimeVisible, MethodInfo methodInfo) {
        int numberOfAnnotations = methodInfo.u2At(offset + 6);
        if (numberOfAnnotations > 0) {
            AnnotationInfo[] annos = MethodInfo.decodeAnnotations(offset + 8, runtimeVisible, numberOfAnnotations, methodInfo);
            if (runtimeVisible) {
                int numRetainedAnnotations = 0;
                int i = 0;
                while (i < numberOfAnnotations) {
                    long standardAnnoTagBits = annos[i].standardAnnotationTagBits;
                    methodInfo.tagBits |= standardAnnoTagBits;
                    if (standardAnnoTagBits != 0L && (methodInfo.version < 0x350000L || (standardAnnoTagBits & 0x400000000000L) == 0L)) {
                        annos[i] = null;
                    } else {
                        ++numRetainedAnnotations;
                    }
                    ++i;
                }
                if (numRetainedAnnotations != numberOfAnnotations) {
                    if (numRetainedAnnotations == 0) {
                        return null;
                    }
                    AnnotationInfo[] temp = new AnnotationInfo[numRetainedAnnotations];
                    int tmpIndex = 0;
                    int i2 = 0;
                    while (i2 < numberOfAnnotations) {
                        if (annos[i2] != null) {
                            temp[tmpIndex++] = annos[i2];
                        }
                        ++i2;
                    }
                    annos = temp;
                }
            }
            return annos;
        }
        return null;
    }

    static TypeAnnotationInfo[] decodeTypeAnnotations(int offset, boolean runtimeVisible, MethodInfo methodInfo) {
        int numberOfAnnotations = methodInfo.u2At(offset + 6);
        if (numberOfAnnotations > 0) {
            int readOffset = offset + 8;
            TypeAnnotationInfo[] typeAnnos = new TypeAnnotationInfo[numberOfAnnotations];
            int i = 0;
            while (i < numberOfAnnotations) {
                TypeAnnotationInfo newInfo = new TypeAnnotationInfo(methodInfo.reference, methodInfo.constantPoolOffsets, readOffset + methodInfo.structOffset, runtimeVisible, false);
                readOffset += newInfo.readOffset;
                typeAnnos[i] = newInfo;
                ++i;
            }
            return typeAnnos;
        }
        return null;
    }

    static AnnotationInfo[][] decodeParamAnnotations(int offset, boolean runtimeVisible, MethodInfo methodInfo) {
        AnnotationInfo[][] allParamAnnotations = null;
        int numberOfParameters = methodInfo.u1At(offset + 6);
        if (numberOfParameters > 0) {
            int readOffset = offset + 7;
            int i = 0;
            while (i < numberOfParameters) {
                int numberOfAnnotations = methodInfo.u2At(readOffset);
                readOffset += 2;
                if (numberOfAnnotations > 0) {
                    if (allParamAnnotations == null) {
                        allParamAnnotations = new AnnotationInfo[numberOfParameters][];
                    }
                    AnnotationInfo[] annos = MethodInfo.decodeAnnotations(readOffset, runtimeVisible, numberOfAnnotations, methodInfo);
                    allParamAnnotations[i] = annos;
                    int aIndex = 0;
                    while (aIndex < annos.length) {
                        readOffset += annos[aIndex].readOffset;
                        ++aIndex;
                    }
                }
                ++i;
            }
        }
        return allParamAnnotations;
    }

    protected MethodInfo(byte[] classFileBytes, int[] offsets, int offset, long version) {
        super(classFileBytes, offsets, offset);
        this.version = version;
    }

    public int compareTo(Object o) {
        MethodInfo otherMethod = (MethodInfo)o;
        int result = new String(this.getSelector()).compareTo(new String(otherMethod.getSelector()));
        if (result != 0) {
            return result;
        }
        return new String(this.getMethodDescriptor()).compareTo(new String(otherMethod.getMethodDescriptor()));
    }

    public boolean equals(Object o) {
        if (!(o instanceof MethodInfo)) {
            return false;
        }
        MethodInfo otherMethod = (MethodInfo)o;
        return CharOperation.equals(this.getSelector(), otherMethod.getSelector()) && CharOperation.equals(this.getMethodDescriptor(), otherMethod.getMethodDescriptor());
    }

    public int hashCode() {
        return CharOperation.hashCode(this.getSelector()) + CharOperation.hashCode(this.getMethodDescriptor());
    }

    @Override
    public IBinaryAnnotation[] getAnnotations() {
        return null;
    }

    @Override
    public char[][] getArgumentNames() {
        if (this.argumentNames == null) {
            this.readCodeAttribute();
        }
        return this.argumentNames;
    }

    @Override
    public Object getDefaultValue() {
        return null;
    }

    @Override
    public char[][] getExceptionTypeNames() {
        if (this.exceptionNames == null) {
            this.readExceptionAttributes();
        }
        return this.exceptionNames;
    }

    @Override
    public char[] getGenericSignature() {
        if (this.signatureUtf8Offset != -1) {
            if (this.signature == null) {
                this.signature = this.utf8At(this.signatureUtf8Offset + 3, this.u2At(this.signatureUtf8Offset + 1));
            }
            return this.signature;
        }
        return null;
    }

    @Override
    public char[] getMethodDescriptor() {
        if (this.descriptor == null) {
            int utf8Offset = this.constantPoolOffsets[this.u2At(4)] - this.structOffset;
            this.descriptor = this.utf8At(utf8Offset + 3, this.u2At(utf8Offset + 1));
        }
        return this.descriptor;
    }

    @Override
    public int getModifiers() {
        if (this.accessFlags == -1) {
            this.readModifierRelatedAttributes();
        }
        return this.accessFlags;
    }

    @Override
    public IBinaryAnnotation[] getParameterAnnotations(int index, char[] classFileName) {
        return null;
    }

    @Override
    public int getAnnotatedParametersCount() {
        return 0;
    }

    @Override
    public IBinaryTypeAnnotation[] getTypeAnnotations() {
        return null;
    }

    @Override
    public char[] getSelector() {
        if (this.name == null) {
            int utf8Offset = this.constantPoolOffsets[this.u2At(2)] - this.structOffset;
            this.name = this.utf8At(utf8Offset + 3, this.u2At(utf8Offset + 1));
        }
        return this.name;
    }

    @Override
    public long getTagBits() {
        return this.tagBits;
    }

    protected void initialize() {
        this.getModifiers();
        this.getSelector();
        this.getMethodDescriptor();
        this.getExceptionTypeNames();
        this.getGenericSignature();
        this.getArgumentNames();
        this.reset();
    }

    @Override
    public boolean isClinit() {
        return JavaBinaryNames.isClinit(this.getSelector());
    }

    @Override
    public boolean isConstructor() {
        return JavaBinaryNames.isConstructor(this.getSelector());
    }

    public boolean isSynthetic() {
        return (this.getModifiers() & 0x1000) != 0;
    }

    private synchronized void readExceptionAttributes() {
        int attributesCount = this.u2At(6);
        int readOffset = 8;
        Object names = null;
        int i = 0;
        while (i < attributesCount) {
            int utf8Offset = this.constantPoolOffsets[this.u2At(readOffset)] - this.structOffset;
            char[] attributeName = this.utf8At(utf8Offset + 3, this.u2At(utf8Offset + 1));
            if (CharOperation.equals(attributeName, AttributeNamesConstants.ExceptionsName)) {
                int entriesNumber = this.u2At(readOffset + 6);
                readOffset += 8;
                if (entriesNumber == 0) {
                    names = noException;
                } else {
                    names = new char[entriesNumber][];
                    int j = 0;
                    while (j < entriesNumber) {
                        utf8Offset = this.constantPoolOffsets[this.u2At(this.constantPoolOffsets[this.u2At(readOffset)] - this.structOffset + 1)] - this.structOffset;
                        names[j] = this.utf8At(utf8Offset + 3, this.u2At(utf8Offset + 1));
                        readOffset += 2;
                        ++j;
                    }
                }
            } else {
                readOffset = (int)((long)readOffset + (6L + this.u4At(readOffset + 2)));
            }
            ++i;
        }
        this.exceptionNames = names == null ? noException : names;
    }

    private synchronized void readModifierRelatedAttributes() {
        int flags = this.u2At(0);
        int attributesCount = this.u2At(6);
        int readOffset = 8;
        int i = 0;
        while (i < attributesCount) {
            int utf8Offset = this.constantPoolOffsets[this.u2At(readOffset)] - this.structOffset;
            char[] attributeName = this.utf8At(utf8Offset + 3, this.u2At(utf8Offset + 1));
            if (attributeName.length != 0) {
                switch (attributeName[0]) {
                    case 'D': {
                        if (!CharOperation.equals(attributeName, AttributeNamesConstants.DeprecatedName)) break;
                        flags |= 0x100000;
                        break;
                    }
                    case 'S': {
                        if (!CharOperation.equals(attributeName, AttributeNamesConstants.SyntheticName)) break;
                        flags |= 0x1000;
                        break;
                    }
                    case 'A': {
                        if (!CharOperation.equals(attributeName, AttributeNamesConstants.AnnotationDefaultName)) break;
                        flags |= 0x20000;
                        break;
                    }
                    case 'V': {
                        if (!CharOperation.equals(attributeName, AttributeNamesConstants.VarargsName)) break;
                        flags |= 0x80;
                    }
                }
            }
            readOffset = (int)((long)readOffset + (6L + this.u4At(readOffset + 2)));
            ++i;
        }
        this.accessFlags = flags;
    }

    public int sizeInBytes() {
        return this.attributeBytes;
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        this.toString(buffer);
        return buffer.toString();
    }

    void toString(StringBuffer buffer) {
        buffer.append(this.getClass().getName());
        this.toStringContent(buffer);
    }

    protected void toStringContent(StringBuffer buffer) {
        BinaryTypeFormatter.methodToStringContent(buffer, this);
    }

    private synchronized void readCodeAttribute() {
        int attributesCount = this.u2At(6);
        int readOffset = 8;
        if (attributesCount != 0) {
            int i = 0;
            while (i < attributesCount) {
                int utf8Offset = this.constantPoolOffsets[this.u2At(readOffset)] - this.structOffset;
                char[] attributeName = this.utf8At(utf8Offset + 3, this.u2At(utf8Offset + 1));
                if (CharOperation.equals(attributeName, AttributeNamesConstants.CodeName)) {
                    this.decodeCodeAttribute(readOffset);
                    if (this.argumentNames == null) {
                        this.argumentNames = noArgumentNames;
                    }
                    return;
                }
                readOffset = (int)((long)readOffset + (6L + this.u4At(readOffset + 2)));
                ++i;
            }
        }
        this.argumentNames = noArgumentNames;
    }

    private void decodeCodeAttribute(int offset) {
        int readOffset = offset + 10;
        int codeLength = (int)this.u4At(readOffset);
        int exceptionTableLength = this.u2At(readOffset += 4 + codeLength);
        readOffset += 2;
        if (exceptionTableLength != 0) {
            int i = 0;
            while (i < exceptionTableLength) {
                readOffset += 8;
                ++i;
            }
        }
        int attributesCount = this.u2At(readOffset);
        readOffset += 2;
        int i = 0;
        while (i < attributesCount) {
            int utf8Offset = this.constantPoolOffsets[this.u2At(readOffset)] - this.structOffset;
            char[] attributeName = this.utf8At(utf8Offset + 3, this.u2At(utf8Offset + 1));
            if (CharOperation.equals(attributeName, AttributeNamesConstants.LocalVariableTableName)) {
                this.decodeLocalVariableAttribute(readOffset, codeLength);
            }
            readOffset = (int)((long)readOffset + (6L + this.u4At(readOffset + 2)));
            ++i;
        }
    }

    private void decodeLocalVariableAttribute(int offset, int codeLength) {
        int readOffset = offset + 6;
        int length = this.u2At(readOffset);
        if (length != 0) {
            readOffset += 2;
            char[][] names = new char[length][];
            int argumentNamesIndex = 0;
            int i = 0;
            while (i < length) {
                int startPC = this.u2At(readOffset);
                if (startPC != 0) break;
                int nameIndex = this.u2At(4 + readOffset);
                int utf8Offset = this.constantPoolOffsets[nameIndex] - this.structOffset;
                char[] localVariableName = this.utf8At(utf8Offset + 3, this.u2At(utf8Offset + 1));
                if (!CharOperation.equals(localVariableName, ConstantPool.This)) {
                    names[argumentNamesIndex++] = localVariableName;
                }
                readOffset += 10;
                ++i;
            }
            if (argumentNamesIndex != names.length) {
                char[][] cArrayArray = names;
                names = new char[argumentNamesIndex][];
                System.arraycopy(cArrayArray, 0, names, 0, argumentNamesIndex);
            }
            this.argumentNames = names;
        }
    }

    private void decodeMethodParameters(int offset, MethodInfo methodInfo) {
        int readOffset = offset + 6;
        int length = this.u1At(readOffset);
        if (length != 0) {
            ++readOffset;
            char[][] names = new char[length][];
            int i = 0;
            while (i < length) {
                int nameIndex = this.u2At(readOffset);
                if (nameIndex != 0) {
                    int utf8Offset = this.constantPoolOffsets[nameIndex] - this.structOffset;
                    char[] parameterName = this.utf8At(utf8Offset + 3, this.u2At(utf8Offset + 1));
                    names[i] = parameterName;
                } else {
                    names[i] = CharOperation.concat(ARG, String.valueOf(i).toCharArray());
                }
                readOffset += 4;
                ++i;
            }
            this.argumentNames = names;
        }
    }
}

