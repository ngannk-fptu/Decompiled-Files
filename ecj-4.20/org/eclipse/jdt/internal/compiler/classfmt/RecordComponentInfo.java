/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.classfmt;

import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.classfmt.AnnotationInfo;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileStruct;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFormatException;
import org.eclipse.jdt.internal.compiler.classfmt.ComponentInfoWithAnnotation;
import org.eclipse.jdt.internal.compiler.classfmt.ComponentInfoWithTypeAnnotation;
import org.eclipse.jdt.internal.compiler.classfmt.TypeAnnotationInfo;
import org.eclipse.jdt.internal.compiler.codegen.AttributeNamesConstants;
import org.eclipse.jdt.internal.compiler.env.IBinaryAnnotation;
import org.eclipse.jdt.internal.compiler.env.IBinaryTypeAnnotation;
import org.eclipse.jdt.internal.compiler.env.IRecordComponent;
import org.eclipse.jdt.internal.compiler.impl.Constant;

public class RecordComponentInfo
extends ClassFileStruct
implements IRecordComponent,
Comparable {
    protected int attributeBytes;
    protected char[] descriptor;
    protected char[] name;
    protected char[] signature;
    protected int signatureUtf8Offset = -1;
    protected long tagBits;
    protected long version;

    public static RecordComponentInfo createComponent(byte[] classFileBytes, int[] offsets, int offset, long version) {
        RecordComponentInfo componentInfo = new RecordComponentInfo(classFileBytes, offsets, offset, version);
        int attributesCount = componentInfo.u2At(4);
        int readOffset = 6;
        ClassFileStruct[] annotations = null;
        ClassFileStruct[] typeAnnotations = null;
        int i = 0;
        while (i < attributesCount) {
            int utf8Offset = componentInfo.constantPoolOffsets[componentInfo.u2At(readOffset)] - componentInfo.structOffset;
            char[] attributeName = componentInfo.utf8At(utf8Offset + 3, componentInfo.u2At(utf8Offset + 1));
            if (attributeName.length > 0) {
                switch (attributeName[0]) {
                    case 'S': {
                        if (!CharOperation.equals(AttributeNamesConstants.SignatureName, attributeName)) break;
                        componentInfo.signatureUtf8Offset = componentInfo.constantPoolOffsets[componentInfo.u2At(readOffset + 6)] - componentInfo.structOffset;
                        break;
                    }
                    case 'R': {
                        ClassFileStruct[] combined;
                        int length;
                        AnnotationInfo[] decodedAnnotations = null;
                        TypeAnnotationInfo[] decodedTypeAnnotations = null;
                        if (CharOperation.equals(attributeName, AttributeNamesConstants.RuntimeVisibleAnnotationsName)) {
                            decodedAnnotations = componentInfo.decodeAnnotations(readOffset, true);
                        } else if (CharOperation.equals(attributeName, AttributeNamesConstants.RuntimeInvisibleAnnotationsName)) {
                            decodedAnnotations = componentInfo.decodeAnnotations(readOffset, false);
                        } else if (CharOperation.equals(attributeName, AttributeNamesConstants.RuntimeVisibleTypeAnnotationsName)) {
                            decodedTypeAnnotations = componentInfo.decodeTypeAnnotations(readOffset, true);
                        } else if (CharOperation.equals(attributeName, AttributeNamesConstants.RuntimeInvisibleTypeAnnotationsName)) {
                            decodedTypeAnnotations = componentInfo.decodeTypeAnnotations(readOffset, false);
                        }
                        if (decodedAnnotations != null) {
                            if (annotations == null) {
                                annotations = decodedAnnotations;
                                break;
                            }
                            length = annotations.length;
                            combined = new AnnotationInfo[length + decodedAnnotations.length];
                            System.arraycopy(annotations, 0, combined, 0, length);
                            System.arraycopy(decodedAnnotations, 0, combined, length, decodedAnnotations.length);
                            annotations = combined;
                            break;
                        }
                        if (decodedTypeAnnotations == null) break;
                        if (typeAnnotations == null) {
                            typeAnnotations = decodedTypeAnnotations;
                            break;
                        }
                        length = typeAnnotations.length;
                        combined = new TypeAnnotationInfo[length + decodedTypeAnnotations.length];
                        System.arraycopy(typeAnnotations, 0, combined, 0, length);
                        System.arraycopy(decodedTypeAnnotations, 0, combined, length, decodedTypeAnnotations.length);
                        typeAnnotations = combined;
                    }
                }
            }
            readOffset = (int)((long)readOffset + (6L + componentInfo.u4At(readOffset + 2)));
            ++i;
        }
        componentInfo.attributeBytes = readOffset;
        if (typeAnnotations != null) {
            return new ComponentInfoWithTypeAnnotation(componentInfo, (AnnotationInfo[])annotations, (TypeAnnotationInfo[])typeAnnotations);
        }
        if (annotations != null) {
            return new ComponentInfoWithAnnotation(componentInfo, (AnnotationInfo[])annotations);
        }
        return componentInfo;
    }

    protected RecordComponentInfo(byte[] classFileBytes, int[] offsets, int offset, long version) {
        super(classFileBytes, offsets, offset);
        this.version = version;
    }

    private AnnotationInfo[] decodeAnnotations(int offset, boolean runtimeVisible) {
        block7: {
            int numberOfAnnotations = this.u2At(offset + 6);
            if (numberOfAnnotations <= 0) break block7;
            int readOffset = offset + 8;
            AnnotationInfo[] newInfos = null;
            int newInfoCount = 0;
            int i = 0;
            while (i < numberOfAnnotations) {
                block9: {
                    AnnotationInfo newInfo;
                    block8: {
                        newInfo = new AnnotationInfo(this.reference, this.constantPoolOffsets, readOffset + this.structOffset, runtimeVisible, false);
                        readOffset += newInfo.readOffset;
                        long standardTagBits = newInfo.standardAnnotationTagBits;
                        if (standardTagBits == 0L) break block8;
                        this.tagBits |= standardTagBits;
                        if (this.version < 0x350000L || (standardTagBits & 0x400000000000L) == 0L) break block9;
                    }
                    if (newInfos == null) {
                        newInfos = new AnnotationInfo[numberOfAnnotations - i];
                    }
                    newInfos[newInfoCount++] = newInfo;
                }
                ++i;
            }
            if (newInfos != null) {
                if (newInfoCount != newInfos.length) {
                    AnnotationInfo[] annotationInfoArray = newInfos;
                    newInfos = new AnnotationInfo[newInfoCount];
                    System.arraycopy(annotationInfoArray, 0, newInfos, 0, newInfoCount);
                }
                return newInfos;
            }
        }
        return null;
    }

    TypeAnnotationInfo[] decodeTypeAnnotations(int offset, boolean runtimeVisible) {
        int numberOfAnnotations = this.u2At(offset + 6);
        if (numberOfAnnotations > 0) {
            int readOffset = offset + 8;
            TypeAnnotationInfo[] typeAnnos = new TypeAnnotationInfo[numberOfAnnotations];
            int i = 0;
            while (i < numberOfAnnotations) {
                TypeAnnotationInfo newInfo = new TypeAnnotationInfo(this.reference, this.constantPoolOffsets, readOffset + this.structOffset, runtimeVisible, false);
                readOffset += newInfo.readOffset;
                typeAnnos[i] = newInfo;
                ++i;
            }
            return typeAnnos;
        }
        return null;
    }

    public int compareTo(Object o) {
        return new String(this.getName()).compareTo(new String(((RecordComponentInfo)o).getName()));
    }

    public boolean equals(Object o) {
        if (!(o instanceof RecordComponentInfo)) {
            return false;
        }
        return CharOperation.equals(this.getName(), ((RecordComponentInfo)o).getName());
    }

    public int hashCode() {
        return CharOperation.hashCode(this.getName());
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
    public char[] getName() {
        if (this.name == null) {
            int utf8Offset = this.constantPoolOffsets[this.u2At(0)] - this.structOffset;
            this.name = this.utf8At(utf8Offset + 3, this.u2At(utf8Offset + 1));
        }
        return this.name;
    }

    @Override
    public long getTagBits() {
        return this.tagBits;
    }

    @Override
    public char[] getTypeName() {
        if (this.descriptor == null) {
            int utf8Offset = this.constantPoolOffsets[this.u2At(2)] - this.structOffset;
            this.descriptor = this.utf8At(utf8Offset + 3, this.u2At(utf8Offset + 1));
        }
        return this.descriptor;
    }

    @Override
    public IBinaryAnnotation[] getAnnotations() {
        return null;
    }

    @Override
    public IBinaryTypeAnnotation[] getTypeAnnotations() {
        return null;
    }

    protected void initialize() {
        this.getName();
        this.getTypeName();
        this.getGenericSignature();
        this.reset();
    }

    public int sizeInBytes() {
        return this.attributeBytes;
    }

    public void throwFormatException() throws ClassFormatException {
        throw new ClassFormatException(29);
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer(this.getClass().getName());
        this.toStringContent(buffer);
        return buffer.toString();
    }

    protected void toStringContent(StringBuffer buffer) {
        buffer.append('{').append(this.getTypeName()).append(' ').append(this.getName()).append(' ').append('}').toString();
    }

    @Override
    public Constant getConstant() {
        return null;
    }

    @Override
    public int getModifiers() {
        return 0;
    }
}

