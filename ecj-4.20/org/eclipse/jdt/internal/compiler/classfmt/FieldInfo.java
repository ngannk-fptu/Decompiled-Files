/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.classfmt;

import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.classfmt.AnnotationInfo;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileStruct;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFormatException;
import org.eclipse.jdt.internal.compiler.classfmt.FieldInfoWithAnnotation;
import org.eclipse.jdt.internal.compiler.classfmt.FieldInfoWithTypeAnnotation;
import org.eclipse.jdt.internal.compiler.classfmt.TypeAnnotationInfo;
import org.eclipse.jdt.internal.compiler.codegen.AttributeNamesConstants;
import org.eclipse.jdt.internal.compiler.env.IBinaryAnnotation;
import org.eclipse.jdt.internal.compiler.env.IBinaryField;
import org.eclipse.jdt.internal.compiler.env.IBinaryTypeAnnotation;
import org.eclipse.jdt.internal.compiler.impl.BooleanConstant;
import org.eclipse.jdt.internal.compiler.impl.ByteConstant;
import org.eclipse.jdt.internal.compiler.impl.CharConstant;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.impl.DoubleConstant;
import org.eclipse.jdt.internal.compiler.impl.FloatConstant;
import org.eclipse.jdt.internal.compiler.impl.IntConstant;
import org.eclipse.jdt.internal.compiler.impl.LongConstant;
import org.eclipse.jdt.internal.compiler.impl.ShortConstant;
import org.eclipse.jdt.internal.compiler.impl.StringConstant;
import org.eclipse.jdt.internal.compiler.util.Util;

public class FieldInfo
extends ClassFileStruct
implements IBinaryField,
Comparable {
    protected int accessFlags = -1;
    protected int attributeBytes;
    protected Constant constant;
    protected char[] descriptor;
    protected char[] name;
    protected char[] signature;
    protected int signatureUtf8Offset = -1;
    protected long tagBits;
    protected Object wrappedConstantValue;
    protected long version;

    public static FieldInfo createField(byte[] classFileBytes, int[] offsets, int offset, long version) {
        FieldInfo fieldInfo = new FieldInfo(classFileBytes, offsets, offset, version);
        int attributesCount = fieldInfo.u2At(6);
        int readOffset = 8;
        ClassFileStruct[] annotations = null;
        ClassFileStruct[] typeAnnotations = null;
        int i = 0;
        while (i < attributesCount) {
            int utf8Offset = fieldInfo.constantPoolOffsets[fieldInfo.u2At(readOffset)] - fieldInfo.structOffset;
            char[] attributeName = fieldInfo.utf8At(utf8Offset + 3, fieldInfo.u2At(utf8Offset + 1));
            if (attributeName.length > 0) {
                switch (attributeName[0]) {
                    case 'S': {
                        if (!CharOperation.equals(AttributeNamesConstants.SignatureName, attributeName)) break;
                        fieldInfo.signatureUtf8Offset = fieldInfo.constantPoolOffsets[fieldInfo.u2At(readOffset + 6)] - fieldInfo.structOffset;
                        break;
                    }
                    case 'R': {
                        ClassFileStruct[] combined;
                        int length;
                        AnnotationInfo[] decodedAnnotations = null;
                        TypeAnnotationInfo[] decodedTypeAnnotations = null;
                        if (CharOperation.equals(attributeName, AttributeNamesConstants.RuntimeVisibleAnnotationsName)) {
                            decodedAnnotations = fieldInfo.decodeAnnotations(readOffset, true);
                        } else if (CharOperation.equals(attributeName, AttributeNamesConstants.RuntimeInvisibleAnnotationsName)) {
                            decodedAnnotations = fieldInfo.decodeAnnotations(readOffset, false);
                        } else if (CharOperation.equals(attributeName, AttributeNamesConstants.RuntimeVisibleTypeAnnotationsName)) {
                            decodedTypeAnnotations = fieldInfo.decodeTypeAnnotations(readOffset, true);
                        } else if (CharOperation.equals(attributeName, AttributeNamesConstants.RuntimeInvisibleTypeAnnotationsName)) {
                            decodedTypeAnnotations = fieldInfo.decodeTypeAnnotations(readOffset, false);
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
            readOffset = (int)((long)readOffset + (6L + fieldInfo.u4At(readOffset + 2)));
            ++i;
        }
        fieldInfo.attributeBytes = readOffset;
        if (typeAnnotations != null) {
            return new FieldInfoWithTypeAnnotation(fieldInfo, (AnnotationInfo[])annotations, (TypeAnnotationInfo[])typeAnnotations);
        }
        if (annotations != null) {
            return new FieldInfoWithAnnotation(fieldInfo, (AnnotationInfo[])annotations);
        }
        return fieldInfo;
    }

    protected FieldInfo(byte[] classFileBytes, int[] offsets, int offset, long version) {
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
        return new String(this.getName()).compareTo(new String(((FieldInfo)o).getName()));
    }

    public boolean equals(Object o) {
        if (!(o instanceof FieldInfo)) {
            return false;
        }
        return CharOperation.equals(this.getName(), ((FieldInfo)o).getName());
    }

    public int hashCode() {
        return CharOperation.hashCode(this.getName());
    }

    @Override
    public Constant getConstant() {
        if (this.constant == null) {
            this.readConstantAttribute();
        }
        return this.constant;
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
    public int getModifiers() {
        if (this.accessFlags == -1) {
            this.accessFlags = this.u2At(0);
            this.readModifierRelatedAttributes();
        }
        return this.accessFlags;
    }

    @Override
    public char[] getName() {
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

    @Override
    public char[] getTypeName() {
        if (this.descriptor == null) {
            int utf8Offset = this.constantPoolOffsets[this.u2At(4)] - this.structOffset;
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

    public Object getWrappedConstantValue() {
        if (this.wrappedConstantValue == null && this.hasConstant()) {
            Constant fieldConstant = this.getConstant();
            switch (fieldConstant.typeID()) {
                case 10: {
                    this.wrappedConstantValue = fieldConstant.intValue();
                    break;
                }
                case 3: {
                    this.wrappedConstantValue = fieldConstant.byteValue();
                    break;
                }
                case 4: {
                    this.wrappedConstantValue = fieldConstant.shortValue();
                    break;
                }
                case 2: {
                    this.wrappedConstantValue = Character.valueOf(fieldConstant.charValue());
                    break;
                }
                case 9: {
                    this.wrappedConstantValue = Float.valueOf(fieldConstant.floatValue());
                    break;
                }
                case 8: {
                    this.wrappedConstantValue = fieldConstant.doubleValue();
                    break;
                }
                case 5: {
                    this.wrappedConstantValue = Util.toBoolean(fieldConstant.booleanValue());
                    break;
                }
                case 7: {
                    this.wrappedConstantValue = fieldConstant.longValue();
                    break;
                }
                case 11: {
                    this.wrappedConstantValue = fieldConstant.stringValue();
                }
            }
        }
        return this.wrappedConstantValue;
    }

    public boolean hasConstant() {
        return this.getConstant() != Constant.NotAConstant;
    }

    protected void initialize() {
        this.getModifiers();
        this.getName();
        this.getConstant();
        this.getTypeName();
        this.getGenericSignature();
        this.reset();
    }

    public boolean isSynthetic() {
        return (this.getModifiers() & 0x1000) != 0;
    }

    private void readConstantAttribute() {
        int attributesCount = this.u2At(6);
        int readOffset = 8;
        boolean isConstant = false;
        int i = 0;
        while (i < attributesCount) {
            int utf8Offset = this.constantPoolOffsets[this.u2At(readOffset)] - this.structOffset;
            char[] attributeName = this.utf8At(utf8Offset + 3, this.u2At(utf8Offset + 1));
            if (CharOperation.equals(attributeName, AttributeNamesConstants.ConstantValueName)) {
                isConstant = true;
                int relativeOffset = this.constantPoolOffsets[this.u2At(readOffset + 6)] - this.structOffset;
                block0 : switch (this.u1At(relativeOffset)) {
                    case 3: {
                        char[] sign = this.getTypeName();
                        if (sign.length == 1) {
                            switch (sign[0]) {
                                case 'Z': {
                                    this.constant = BooleanConstant.fromValue(this.i4At(relativeOffset + 1) == 1);
                                    break block0;
                                }
                                case 'I': {
                                    this.constant = IntConstant.fromValue(this.i4At(relativeOffset + 1));
                                    break block0;
                                }
                                case 'C': {
                                    this.constant = CharConstant.fromValue((char)this.i4At(relativeOffset + 1));
                                    break block0;
                                }
                                case 'B': {
                                    this.constant = ByteConstant.fromValue((byte)this.i4At(relativeOffset + 1));
                                    break block0;
                                }
                                case 'S': {
                                    this.constant = ShortConstant.fromValue((short)this.i4At(relativeOffset + 1));
                                    break block0;
                                }
                            }
                            this.constant = Constant.NotAConstant;
                            break;
                        }
                        this.constant = Constant.NotAConstant;
                        break;
                    }
                    case 4: {
                        this.constant = FloatConstant.fromValue(this.floatAt(relativeOffset + 1));
                        break;
                    }
                    case 6: {
                        this.constant = DoubleConstant.fromValue(this.doubleAt(relativeOffset + 1));
                        break;
                    }
                    case 5: {
                        this.constant = LongConstant.fromValue(this.i8At(relativeOffset + 1));
                        break;
                    }
                    case 8: {
                        utf8Offset = this.constantPoolOffsets[this.u2At(relativeOffset + 1)] - this.structOffset;
                        this.constant = StringConstant.fromValue(String.valueOf(this.utf8At(utf8Offset + 3, this.u2At(utf8Offset + 1))));
                    }
                }
            }
            readOffset = (int)((long)readOffset + (6L + this.u4At(readOffset + 2)));
            ++i;
        }
        if (!isConstant) {
            this.constant = Constant.NotAConstant;
        }
    }

    private void readModifierRelatedAttributes() {
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
                        this.accessFlags |= 0x100000;
                        break;
                    }
                    case 'S': {
                        if (!CharOperation.equals(attributeName, AttributeNamesConstants.SyntheticName)) break;
                        this.accessFlags |= 0x1000;
                    }
                }
            }
            readOffset = (int)((long)readOffset + (6L + this.u4At(readOffset + 2)));
            ++i;
        }
    }

    public int sizeInBytes() {
        return this.attributeBytes;
    }

    public void throwFormatException() throws ClassFormatException {
        throw new ClassFormatException(17);
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer(this.getClass().getName());
        this.toStringContent(buffer);
        return buffer.toString();
    }

    protected void toStringContent(StringBuffer buffer) {
        int modifiers = this.getModifiers();
        buffer.append('{').append(String.valueOf((modifiers & 0x100000) != 0 ? "deprecated " : Util.EMPTY_STRING) + ((modifiers & 1) == 1 ? "public " : Util.EMPTY_STRING) + ((modifiers & 2) == 2 ? "private " : Util.EMPTY_STRING) + ((modifiers & 4) == 4 ? "protected " : Util.EMPTY_STRING) + ((modifiers & 8) == 8 ? "static " : Util.EMPTY_STRING) + ((modifiers & 0x10) == 16 ? "final " : Util.EMPTY_STRING) + ((modifiers & 0x40) == 64 ? "volatile " : Util.EMPTY_STRING) + ((modifiers & 0x80) == 128 ? "transient " : Util.EMPTY_STRING)).append(this.getTypeName()).append(' ').append(this.getName()).append(' ').append(this.getConstant()).append('}').toString();
    }
}

