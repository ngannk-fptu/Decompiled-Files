/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.classfmt;

import org.eclipse.jdt.internal.compiler.classfmt.ClassFileStruct;
import org.eclipse.jdt.internal.compiler.env.IBinaryNestedType;

public class InnerClassInfo
extends ClassFileStruct
implements IBinaryNestedType {
    int innerClassNameIndex = this.u2At(0);
    int outerClassNameIndex = this.u2At(2);
    int innerNameIndex = this.u2At(4);
    private char[] innerClassName;
    private char[] outerClassName;
    private char[] innerName;
    private int accessFlags = -1;
    private boolean readInnerClassName;
    private boolean readOuterClassName;
    private boolean readInnerName;

    public InnerClassInfo(byte[] classFileBytes, int[] offsets, int offset) {
        super(classFileBytes, offsets, offset);
    }

    @Override
    public char[] getEnclosingTypeName() {
        if (!this.readOuterClassName) {
            if (this.outerClassNameIndex != 0) {
                int utf8Offset = this.constantPoolOffsets[this.u2At(this.constantPoolOffsets[this.outerClassNameIndex] - this.structOffset + 1)] - this.structOffset;
                this.outerClassName = this.utf8At(utf8Offset + 3, this.u2At(utf8Offset + 1));
            }
            this.readOuterClassName = true;
        }
        return this.outerClassName;
    }

    @Override
    public int getModifiers() {
        if (this.accessFlags == -1) {
            this.accessFlags = this.u2At(6);
        }
        return this.accessFlags;
    }

    @Override
    public char[] getName() {
        if (!this.readInnerClassName) {
            if (this.innerClassNameIndex != 0) {
                int classOffset = this.constantPoolOffsets[this.innerClassNameIndex] - this.structOffset;
                int utf8Offset = this.constantPoolOffsets[this.u2At(classOffset + 1)] - this.structOffset;
                this.innerClassName = this.utf8At(utf8Offset + 3, this.u2At(utf8Offset + 1));
            }
            this.readInnerClassName = true;
        }
        return this.innerClassName;
    }

    public char[] getSourceName() {
        if (!this.readInnerName) {
            if (this.innerNameIndex != 0) {
                int utf8Offset = this.constantPoolOffsets[this.innerNameIndex] - this.structOffset;
                this.innerName = this.utf8At(utf8Offset + 3, this.u2At(utf8Offset + 1));
            }
            this.readInnerName = true;
        }
        return this.innerName;
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        if (this.getName() != null) {
            buffer.append(this.getName());
        }
        buffer.append("\n");
        if (this.getEnclosingTypeName() != null) {
            buffer.append(this.getEnclosingTypeName());
        }
        buffer.append("\n");
        if (this.getSourceName() != null) {
            buffer.append(this.getSourceName());
        }
        return buffer.toString();
    }

    void initialize() {
        this.getModifiers();
        this.getName();
        this.getSourceName();
        this.getEnclosingTypeName();
        this.reset();
    }
}

