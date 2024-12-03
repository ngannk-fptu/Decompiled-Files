/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.classfmt;

import java.util.Arrays;
import org.eclipse.jdt.internal.compiler.classfmt.AnnotationInfo;
import org.eclipse.jdt.internal.compiler.classfmt.BinaryTypeFormatter;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileStruct;
import org.eclipse.jdt.internal.compiler.env.IBinaryAnnotation;
import org.eclipse.jdt.internal.compiler.env.IBinaryTypeAnnotation;

public class TypeAnnotationInfo
extends ClassFileStruct
implements IBinaryTypeAnnotation {
    private AnnotationInfo annotation;
    private int targetType = 0;
    private int info;
    private int info2;
    private int[] typePath;
    int readOffset = 0;

    TypeAnnotationInfo(byte[] classFileBytes, int[] contantPoolOffsets, int offset) {
        super(classFileBytes, contantPoolOffsets, offset);
    }

    TypeAnnotationInfo(byte[] classFileBytes, int[] contantPoolOffsets, int offset, boolean runtimeVisible, boolean populate) {
        this(classFileBytes, contantPoolOffsets, offset);
        this.readOffset = 0;
        this.targetType = this.u1At(0);
        switch (this.targetType) {
            case 0: 
            case 1: {
                this.info = this.u1At(1);
                this.readOffset += 2;
                break;
            }
            case 16: {
                this.info = this.u2At(1);
                this.readOffset += 3;
                break;
            }
            case 17: 
            case 18: {
                this.info = this.u1At(1);
                this.info2 = this.u1At(2);
                this.readOffset += 3;
                break;
            }
            case 19: 
            case 20: 
            case 21: {
                ++this.readOffset;
                break;
            }
            case 22: {
                this.info = this.u1At(1);
                this.readOffset += 2;
                break;
            }
            case 23: {
                this.info = this.u2At(1);
                this.readOffset += 3;
                break;
            }
            default: {
                throw new IllegalStateException("Target type not handled " + this.targetType);
            }
        }
        int typePathLength = this.u1At(this.readOffset);
        ++this.readOffset;
        if (typePathLength == 0) {
            this.typePath = NO_TYPE_PATH;
        } else {
            this.typePath = new int[typePathLength * 2];
            int index = 0;
            int i = 0;
            while (i < typePathLength) {
                this.typePath[index++] = this.u1At(this.readOffset++);
                this.typePath[index++] = this.u1At(this.readOffset++);
                ++i;
            }
        }
        this.annotation = new AnnotationInfo(classFileBytes, this.constantPoolOffsets, this.structOffset + this.readOffset, runtimeVisible, populate);
        this.readOffset += this.annotation.readOffset;
    }

    @Override
    public IBinaryAnnotation getAnnotation() {
        return this.annotation;
    }

    protected void initialize() {
        this.annotation.initialize();
    }

    @Override
    protected void reset() {
        this.annotation.reset();
        super.reset();
    }

    public String toString() {
        return BinaryTypeFormatter.annotationToString(this);
    }

    @Override
    public int getTargetType() {
        return this.targetType;
    }

    @Override
    public int getSupertypeIndex() {
        return this.info;
    }

    @Override
    public int getTypeParameterIndex() {
        return this.info;
    }

    @Override
    public int getBoundIndex() {
        return this.info2;
    }

    @Override
    public int getMethodFormalParameterIndex() {
        return this.info;
    }

    @Override
    public int getThrowsTypeIndex() {
        return this.info;
    }

    @Override
    public int[] getTypePath() {
        return this.typePath;
    }

    public int hashCode() {
        int result = 1;
        result = 31 * result + this.targetType;
        result = 31 * result + this.info;
        result = 31 * result + this.info2;
        if (this.typePath != null) {
            int i = 0;
            int max = this.typePath.length;
            while (i < max) {
                result = 31 * result + this.typePath[i];
                ++i;
            }
        }
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
        TypeAnnotationInfo other = (TypeAnnotationInfo)obj;
        if (this.targetType != other.targetType) {
            return false;
        }
        if (this.info != other.info) {
            return false;
        }
        if (this.info2 != other.info2) {
            return false;
        }
        if (!Arrays.equals(this.typePath, other.typePath)) {
            return false;
        }
        return this.annotation.equals(other.annotation);
    }
}

