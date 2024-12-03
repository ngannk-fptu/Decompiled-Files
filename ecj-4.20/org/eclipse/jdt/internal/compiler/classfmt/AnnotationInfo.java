/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.classfmt;

import java.util.Arrays;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.classfmt.BinaryTypeFormatter;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileStruct;
import org.eclipse.jdt.internal.compiler.classfmt.ElementValuePairInfo;
import org.eclipse.jdt.internal.compiler.codegen.ConstantPool;
import org.eclipse.jdt.internal.compiler.env.ClassSignature;
import org.eclipse.jdt.internal.compiler.env.EnumConstantSignature;
import org.eclipse.jdt.internal.compiler.env.IBinaryAnnotation;
import org.eclipse.jdt.internal.compiler.env.IBinaryElementValuePair;
import org.eclipse.jdt.internal.compiler.impl.BooleanConstant;
import org.eclipse.jdt.internal.compiler.impl.ByteConstant;
import org.eclipse.jdt.internal.compiler.impl.CharConstant;
import org.eclipse.jdt.internal.compiler.impl.DoubleConstant;
import org.eclipse.jdt.internal.compiler.impl.FloatConstant;
import org.eclipse.jdt.internal.compiler.impl.IntConstant;
import org.eclipse.jdt.internal.compiler.impl.LongConstant;
import org.eclipse.jdt.internal.compiler.impl.ShortConstant;
import org.eclipse.jdt.internal.compiler.impl.StringConstant;
import org.eclipse.jdt.internal.compiler.util.Util;

public class AnnotationInfo
extends ClassFileStruct
implements IBinaryAnnotation {
    private char[] typename;
    private volatile ElementValuePairInfo[] pairs;
    long standardAnnotationTagBits = 0L;
    int readOffset = 0;
    static Object[] EmptyValueArray = new Object[0];
    public RuntimeException exceptionDuringDecode;

    AnnotationInfo(byte[] classFileBytes, int[] contantPoolOffsets, int offset) {
        super(classFileBytes, contantPoolOffsets, offset);
    }

    AnnotationInfo(byte[] classFileBytes, int[] contantPoolOffsets, int offset, boolean runtimeVisible, boolean populate) {
        this(classFileBytes, contantPoolOffsets, offset);
        if (populate) {
            this.decodeAnnotation();
        } else {
            this.readOffset = this.scanAnnotation(0, runtimeVisible, true);
        }
    }

    /*
     * Handled impossible loop by duplicating code
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private void decodeAnnotation() {
        StringBuilder newMessage;
        RuntimeException any2;
        block5: {
            int offset;
            block4: {
                this.readOffset = 0;
                int utf8Offset = this.constantPoolOffsets[this.u2At(0)] - this.structOffset;
                this.typename = this.utf8At(utf8Offset + 3, this.u2At(utf8Offset + 1));
                int numberOfPairs = this.u2At(2);
                this.readOffset += 4;
                ElementValuePairInfo[] decodedPairs = numberOfPairs == 0 ? ElementValuePairInfo.NoMembers : new ElementValuePairInfo[numberOfPairs];
                int i = 0;
                try {
                    while (i < numberOfPairs) {
                        utf8Offset = this.constantPoolOffsets[this.u2At(this.readOffset)] - this.structOffset;
                        char[] membername = this.utf8At(utf8Offset + 3, this.u2At(utf8Offset + 1));
                        this.readOffset += 2;
                        Object value = this.decodeDefaultValue();
                        decodedPairs[i++] = new ElementValuePairInfo(membername, value);
                    }
                    this.pairs = decodedPairs;
                    return;
                }
                catch (RuntimeException any2) {
                    this.sanitizePairs(decodedPairs);
                    newMessage = new StringBuilder(any2.getMessage());
                    newMessage.append(" while decoding pair #").append(i).append(" of annotation @").append(this.typename);
                    newMessage.append(", bytes at structOffset ").append(this.structOffset).append(":");
                    offset = this.structOffset;
                    if (!true) break block4;
                    if (offset > this.structOffset + this.readOffset) throw new IllegalStateException(newMessage.toString(), any2);
                    if (offset >= this.reference.length) break block5;
                }
            }
            do {
                newMessage.append(' ').append(Integer.toHexString(this.reference[offset++] & 0xFF));
                if (offset > this.structOffset + this.readOffset) throw new IllegalStateException(newMessage.toString(), any2);
            } while (offset < this.reference.length);
        }
        throw new IllegalStateException(newMessage.toString(), any2);
    }

    private void sanitizePairs(ElementValuePairInfo[] oldPairs) {
        if (oldPairs != null) {
            ElementValuePairInfo[] newPairs = new ElementValuePairInfo[oldPairs.length];
            int count = 0;
            int i = 0;
            while (i < oldPairs.length) {
                ElementValuePairInfo evpInfo = oldPairs[i];
                if (evpInfo != null) {
                    newPairs[count++] = evpInfo;
                }
                ++i;
            }
            this.pairs = count < oldPairs.length ? Arrays.copyOf(newPairs, count) : newPairs;
        } else {
            this.pairs = ElementValuePairInfo.NoMembers;
        }
    }

    Object decodeDefaultValue() {
        Object value = null;
        int tag = this.u1At(this.readOffset);
        ++this.readOffset;
        int constValueOffset = -1;
        switch (tag) {
            case 90: {
                constValueOffset = this.constantPoolOffsets[this.u2At(this.readOffset)] - this.structOffset;
                value = BooleanConstant.fromValue(this.i4At(constValueOffset + 1) == 1);
                this.readOffset += 2;
                break;
            }
            case 73: {
                constValueOffset = this.constantPoolOffsets[this.u2At(this.readOffset)] - this.structOffset;
                value = IntConstant.fromValue(this.i4At(constValueOffset + 1));
                this.readOffset += 2;
                break;
            }
            case 67: {
                constValueOffset = this.constantPoolOffsets[this.u2At(this.readOffset)] - this.structOffset;
                value = CharConstant.fromValue((char)this.i4At(constValueOffset + 1));
                this.readOffset += 2;
                break;
            }
            case 66: {
                constValueOffset = this.constantPoolOffsets[this.u2At(this.readOffset)] - this.structOffset;
                value = ByteConstant.fromValue((byte)this.i4At(constValueOffset + 1));
                this.readOffset += 2;
                break;
            }
            case 83: {
                constValueOffset = this.constantPoolOffsets[this.u2At(this.readOffset)] - this.structOffset;
                value = ShortConstant.fromValue((short)this.i4At(constValueOffset + 1));
                this.readOffset += 2;
                break;
            }
            case 68: {
                constValueOffset = this.constantPoolOffsets[this.u2At(this.readOffset)] - this.structOffset;
                value = DoubleConstant.fromValue(this.doubleAt(constValueOffset + 1));
                this.readOffset += 2;
                break;
            }
            case 70: {
                constValueOffset = this.constantPoolOffsets[this.u2At(this.readOffset)] - this.structOffset;
                value = FloatConstant.fromValue(this.floatAt(constValueOffset + 1));
                this.readOffset += 2;
                break;
            }
            case 74: {
                constValueOffset = this.constantPoolOffsets[this.u2At(this.readOffset)] - this.structOffset;
                value = LongConstant.fromValue(this.i8At(constValueOffset + 1));
                this.readOffset += 2;
                break;
            }
            case 115: {
                constValueOffset = this.constantPoolOffsets[this.u2At(this.readOffset)] - this.structOffset;
                value = StringConstant.fromValue(String.valueOf(this.utf8At(constValueOffset + 3, this.u2At(constValueOffset + 1))));
                this.readOffset += 2;
                break;
            }
            case 101: {
                constValueOffset = this.constantPoolOffsets[this.u2At(this.readOffset)] - this.structOffset;
                char[] typeName = this.utf8At(constValueOffset + 3, this.u2At(constValueOffset + 1));
                this.readOffset += 2;
                constValueOffset = this.constantPoolOffsets[this.u2At(this.readOffset)] - this.structOffset;
                char[] constName = this.utf8At(constValueOffset + 3, this.u2At(constValueOffset + 1));
                this.readOffset += 2;
                value = new EnumConstantSignature(typeName, constName);
                break;
            }
            case 99: {
                constValueOffset = this.constantPoolOffsets[this.u2At(this.readOffset)] - this.structOffset;
                char[] className = this.utf8At(constValueOffset + 3, this.u2At(constValueOffset + 1));
                value = new ClassSignature(className);
                this.readOffset += 2;
                break;
            }
            case 64: {
                value = new AnnotationInfo(this.reference, this.constantPoolOffsets, this.readOffset + this.structOffset, false, true);
                this.readOffset += ((AnnotationInfo)value).readOffset;
                break;
            }
            case 91: {
                int numberOfValues = this.u2At(this.readOffset);
                this.readOffset += 2;
                if (numberOfValues == 0) {
                    value = EmptyValueArray;
                    break;
                }
                Object[] arrayElements = new Object[numberOfValues];
                value = arrayElements;
                int i = 0;
                while (i < numberOfValues) {
                    arrayElements[i] = this.decodeDefaultValue();
                    ++i;
                }
                break;
            }
            default: {
                String tagDisplay = tag == 0 ? "0x00" : String.valueOf((char)tag) + " (" + Integer.toHexString(tag & 0xFF) + ')';
                throw new IllegalStateException("Unrecognized tag " + tagDisplay);
            }
        }
        return value;
    }

    @Override
    public IBinaryElementValuePair[] getElementValuePairs() {
        if (this.pairs == null) {
            this.lazyInitialize();
        }
        return this.pairs;
    }

    @Override
    public char[] getTypeName() {
        return this.typename;
    }

    @Override
    public boolean isDeprecatedAnnotation() {
        return (this.standardAnnotationTagBits & 0x4000400000000000L) != 0L;
    }

    void initialize() {
        if (this.pairs == null) {
            this.decodeAnnotation();
        }
    }

    synchronized void lazyInitialize() {
        if (this.pairs == null) {
            this.decodeAnnotation();
        }
    }

    private int readRetentionPolicy(int offset) {
        int currentOffset = offset;
        int tag = this.u1At(currentOffset);
        ++currentOffset;
        switch (tag) {
            case 101: {
                int utf8Offset = this.constantPoolOffsets[this.u2At(currentOffset)] - this.structOffset;
                char[] typeName = this.utf8At(utf8Offset + 3, this.u2At(utf8Offset + 1));
                currentOffset += 2;
                if (typeName.length == 38 && CharOperation.equals(typeName, ConstantPool.JAVA_LANG_ANNOTATION_RETENTIONPOLICY)) {
                    utf8Offset = this.constantPoolOffsets[this.u2At(currentOffset)] - this.structOffset;
                    char[] constName = this.utf8At(utf8Offset + 3, this.u2At(utf8Offset + 1));
                    this.standardAnnotationTagBits |= Annotation.getRetentionPolicy(constName);
                }
                currentOffset += 2;
                break;
            }
            case 66: 
            case 67: 
            case 68: 
            case 70: 
            case 73: 
            case 74: 
            case 83: 
            case 90: 
            case 99: 
            case 115: {
                currentOffset += 2;
                break;
            }
            case 64: {
                currentOffset = this.scanAnnotation(currentOffset, false, false);
                break;
            }
            case 91: {
                int numberOfValues = this.u2At(currentOffset);
                currentOffset += 2;
                int i = 0;
                while (i < numberOfValues) {
                    currentOffset = this.scanElementValue(currentOffset);
                    ++i;
                }
                break;
            }
            default: {
                throw new IllegalStateException();
            }
        }
        return currentOffset;
    }

    private int readTargetValue(int offset) {
        int currentOffset = offset;
        int tag = this.u1At(currentOffset);
        ++currentOffset;
        switch (tag) {
            case 101: {
                int utf8Offset = this.constantPoolOffsets[this.u2At(currentOffset)] - this.structOffset;
                char[] typeName = this.utf8At(utf8Offset + 3, this.u2At(utf8Offset + 1));
                currentOffset += 2;
                if (typeName.length == 34 && CharOperation.equals(typeName, ConstantPool.JAVA_LANG_ANNOTATION_ELEMENTTYPE)) {
                    utf8Offset = this.constantPoolOffsets[this.u2At(currentOffset)] - this.structOffset;
                    char[] constName = this.utf8At(utf8Offset + 3, this.u2At(utf8Offset + 1));
                    this.standardAnnotationTagBits |= Annotation.getTargetElementType(constName);
                }
                currentOffset += 2;
                break;
            }
            case 66: 
            case 67: 
            case 68: 
            case 70: 
            case 73: 
            case 74: 
            case 83: 
            case 90: 
            case 99: 
            case 115: {
                currentOffset += 2;
                break;
            }
            case 64: {
                currentOffset = this.scanAnnotation(currentOffset, false, false);
                break;
            }
            case 91: {
                int numberOfValues = this.u2At(currentOffset);
                currentOffset += 2;
                if (numberOfValues == 0) {
                    this.standardAnnotationTagBits |= 0x800000000L;
                    break;
                }
                int i = 0;
                while (i < numberOfValues) {
                    currentOffset = this.readTargetValue(currentOffset);
                    ++i;
                }
                break;
            }
            default: {
                throw new IllegalStateException();
            }
        }
        return currentOffset;
    }

    private int scanAnnotation(int offset, boolean expectRuntimeVisibleAnno, boolean toplevel) {
        int currentOffset = offset;
        int utf8Offset = this.constantPoolOffsets[this.u2At(offset)] - this.structOffset;
        char[] typeName = this.utf8At(utf8Offset + 3, this.u2At(utf8Offset + 1));
        if (toplevel) {
            this.typename = typeName;
        }
        int numberOfPairs = this.u2At(offset + 2);
        currentOffset += 4;
        if (expectRuntimeVisibleAnno && toplevel) {
            switch (typeName.length) {
                case 22: {
                    if (!CharOperation.equals(typeName, ConstantPool.JAVA_LANG_DEPRECATED)) break;
                    this.standardAnnotationTagBits |= 0x400000000000L;
                    break;
                }
                case 23: {
                    if (!CharOperation.equals(typeName, ConstantPool.JAVA_LANG_SAFEVARARGS)) break;
                    this.standardAnnotationTagBits |= 0x8000000000000L;
                    return currentOffset;
                }
                case 29: {
                    if (!CharOperation.equals(typeName, ConstantPool.JAVA_LANG_ANNOTATION_TARGET)) break;
                    return this.readTargetValue(currentOffset += 2);
                }
                case 32: {
                    if (CharOperation.equals(typeName, ConstantPool.JAVA_LANG_ANNOTATION_RETENTION)) {
                        return this.readRetentionPolicy(currentOffset += 2);
                    }
                    if (!CharOperation.equals(typeName, ConstantPool.JAVA_LANG_ANNOTATION_INHERITED)) break;
                    this.standardAnnotationTagBits |= 0x1000000000000L;
                    return currentOffset;
                }
                case 33: {
                    if (!CharOperation.equals(typeName, ConstantPool.JAVA_LANG_ANNOTATION_DOCUMENTED)) break;
                    this.standardAnnotationTagBits |= 0x800000000000L;
                    return currentOffset;
                }
                case 52: {
                    if (!CharOperation.equals(typeName, ConstantPool.JAVA_LANG_INVOKE_METHODHANDLE_POLYMORPHICSIGNATURE)) break;
                    this.standardAnnotationTagBits |= 0x10000000000000L;
                    return currentOffset;
                }
            }
        }
        int i = 0;
        while (i < numberOfPairs) {
            currentOffset += 2;
            currentOffset = this.scanElementValue(currentOffset);
            ++i;
        }
        return currentOffset;
    }

    private int scanElementValue(int offset) {
        int currentOffset = offset;
        int tag = this.u1At(currentOffset);
        ++currentOffset;
        switch (tag) {
            case 90: {
                int constantOffset;
                if ((this.standardAnnotationTagBits & 0x400000000000L) != 0L && this.i4At(constantOffset = this.constantPoolOffsets[this.u2At(currentOffset)] - this.structOffset + 1) == 1) {
                    this.standardAnnotationTagBits |= 0x4000000000000000L;
                }
                currentOffset += 2;
                break;
            }
            case 66: 
            case 67: 
            case 68: 
            case 70: 
            case 73: 
            case 74: 
            case 83: 
            case 99: 
            case 115: {
                currentOffset += 2;
                break;
            }
            case 101: {
                currentOffset += 4;
                break;
            }
            case 64: {
                currentOffset = this.scanAnnotation(currentOffset, false, false);
                break;
            }
            case 91: {
                int numberOfValues = this.u2At(currentOffset);
                currentOffset += 2;
                int i = 0;
                while (i < numberOfValues) {
                    currentOffset = this.scanElementValue(currentOffset);
                    ++i;
                }
                break;
            }
            default: {
                throw new IllegalStateException();
            }
        }
        return currentOffset;
    }

    public String toString() {
        return BinaryTypeFormatter.annotationToString(this);
    }

    public int hashCode() {
        int result = 1;
        result = 31 * result + Util.hashCode(this.pairs);
        result = 31 * result + CharOperation.hashCode(this.typename);
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        AnnotationInfo other = (AnnotationInfo)obj;
        if (!Arrays.equals(this.pairs, other.pairs)) {
            return false;
        }
        return Arrays.equals(this.typename, other.typename);
    }
}

