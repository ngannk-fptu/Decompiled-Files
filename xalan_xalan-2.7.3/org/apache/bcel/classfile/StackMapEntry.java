/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.classfile;

import java.io.DataInput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;
import org.apache.bcel.classfile.ClassFormatException;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.Node;
import org.apache.bcel.classfile.StackMapType;
import org.apache.bcel.classfile.Visitor;

public final class StackMapEntry
implements Node,
Cloneable {
    static final StackMapEntry[] EMPTY_ARRAY = new StackMapEntry[0];
    private int frameType;
    private int byteCodeOffset;
    private StackMapType[] typesOfLocals;
    private StackMapType[] typesOfStackItems;
    private ConstantPool constantPool;

    StackMapEntry(DataInput dataInput, ConstantPool constantPool) throws IOException {
        this(dataInput.readByte() & 0xFF, -1, null, null, constantPool);
        if (this.frameType >= 0 && this.frameType <= 63) {
            this.byteCodeOffset = this.frameType - 0;
        } else if (this.frameType >= 64 && this.frameType <= 127) {
            this.byteCodeOffset = this.frameType - 64;
            this.typesOfStackItems = new StackMapType[]{new StackMapType(dataInput, constantPool)};
        } else if (this.frameType == 247) {
            this.byteCodeOffset = dataInput.readUnsignedShort();
            this.typesOfStackItems = new StackMapType[]{new StackMapType(dataInput, constantPool)};
        } else if (this.frameType >= 248 && this.frameType <= 250) {
            this.byteCodeOffset = dataInput.readUnsignedShort();
        } else if (this.frameType == 251) {
            this.byteCodeOffset = dataInput.readUnsignedShort();
        } else if (this.frameType >= 252 && this.frameType <= 254) {
            this.byteCodeOffset = dataInput.readUnsignedShort();
            int numberOfLocals = this.frameType - 251;
            this.typesOfLocals = new StackMapType[numberOfLocals];
            for (int i = 0; i < numberOfLocals; ++i) {
                this.typesOfLocals[i] = new StackMapType(dataInput, constantPool);
            }
        } else if (this.frameType == 255) {
            this.byteCodeOffset = dataInput.readUnsignedShort();
            int numberOfLocals = dataInput.readUnsignedShort();
            this.typesOfLocals = new StackMapType[numberOfLocals];
            for (int i = 0; i < numberOfLocals; ++i) {
                this.typesOfLocals[i] = new StackMapType(dataInput, constantPool);
            }
            int numberOfStackItems = dataInput.readUnsignedShort();
            this.typesOfStackItems = new StackMapType[numberOfStackItems];
            for (int i = 0; i < numberOfStackItems; ++i) {
                this.typesOfStackItems[i] = new StackMapType(dataInput, constantPool);
            }
        } else {
            throw new ClassFormatException("Invalid frame type found while parsing stack map table: " + this.frameType);
        }
    }

    @Deprecated
    public StackMapEntry(int byteCodeOffset, int numberOfLocals, StackMapType[] typesOfLocals, int numberOfStackItems, StackMapType[] typesOfStackItems, ConstantPool constantPool) {
        this.byteCodeOffset = byteCodeOffset;
        this.typesOfLocals = typesOfLocals != null ? typesOfLocals : StackMapType.EMPTY_ARRAY;
        this.typesOfStackItems = typesOfStackItems != null ? typesOfStackItems : StackMapType.EMPTY_ARRAY;
        this.constantPool = constantPool;
        if (numberOfLocals < 0) {
            throw new IllegalArgumentException("numberOfLocals < 0");
        }
        if (numberOfStackItems < 0) {
            throw new IllegalArgumentException("numberOfStackItems < 0");
        }
    }

    public StackMapEntry(int tag, int byteCodeOffset, StackMapType[] typesOfLocals, StackMapType[] typesOfStackItems, ConstantPool constantPool) {
        this.frameType = tag;
        this.byteCodeOffset = byteCodeOffset;
        this.typesOfLocals = typesOfLocals != null ? typesOfLocals : StackMapType.EMPTY_ARRAY;
        this.typesOfStackItems = typesOfStackItems != null ? typesOfStackItems : StackMapType.EMPTY_ARRAY;
        this.constantPool = constantPool;
    }

    @Override
    public void accept(Visitor v) {
        v.visitStackMapEntry(this);
    }

    public StackMapEntry copy() {
        StackMapEntry e;
        try {
            e = (StackMapEntry)this.clone();
        }
        catch (CloneNotSupportedException ex) {
            throw new Error("Clone Not Supported");
        }
        e.typesOfLocals = new StackMapType[this.typesOfLocals.length];
        Arrays.setAll(e.typesOfLocals, i -> this.typesOfLocals[i].copy());
        e.typesOfStackItems = new StackMapType[this.typesOfStackItems.length];
        Arrays.setAll(e.typesOfStackItems, i -> this.typesOfStackItems[i].copy());
        return e;
    }

    public void dump(DataOutputStream file) throws IOException {
        file.write(this.frameType);
        if (this.frameType >= 64 && this.frameType <= 127) {
            this.typesOfStackItems[0].dump(file);
        } else if (this.frameType == 247) {
            file.writeShort(this.byteCodeOffset);
            this.typesOfStackItems[0].dump(file);
        } else if (this.frameType >= 248 && this.frameType <= 250) {
            file.writeShort(this.byteCodeOffset);
        } else if (this.frameType == 251) {
            file.writeShort(this.byteCodeOffset);
        } else if (this.frameType >= 252 && this.frameType <= 254) {
            file.writeShort(this.byteCodeOffset);
            for (StackMapType type : this.typesOfLocals) {
                type.dump(file);
            }
        } else if (this.frameType == 255) {
            file.writeShort(this.byteCodeOffset);
            file.writeShort(this.typesOfLocals.length);
            for (StackMapType type : this.typesOfLocals) {
                type.dump(file);
            }
            file.writeShort(this.typesOfStackItems.length);
            for (StackMapType type : this.typesOfStackItems) {
                type.dump(file);
            }
        } else if (this.frameType < 0 || this.frameType > 63) {
            throw new ClassFormatException("Invalid Stack map table tag: " + this.frameType);
        }
    }

    public int getByteCodeOffset() {
        return this.byteCodeOffset;
    }

    public ConstantPool getConstantPool() {
        return this.constantPool;
    }

    public int getFrameType() {
        return this.frameType;
    }

    int getMapEntrySize() {
        if (this.frameType >= 0 && this.frameType <= 63) {
            return 1;
        }
        if (this.frameType >= 64 && this.frameType <= 127) {
            return 1 + (this.typesOfStackItems[0].hasIndex() ? 3 : 1);
        }
        if (this.frameType == 247) {
            return 3 + (this.typesOfStackItems[0].hasIndex() ? 3 : 1);
        }
        if (this.frameType >= 248 && this.frameType <= 250 || this.frameType == 251) {
            return 3;
        }
        if (this.frameType >= 252 && this.frameType <= 254) {
            int len = 3;
            for (StackMapType typesOfLocal : this.typesOfLocals) {
                len += typesOfLocal.hasIndex() ? 3 : 1;
            }
            return len;
        }
        if (this.frameType != 255) {
            throw new IllegalStateException("Invalid StackMap frameType: " + this.frameType);
        }
        int len = 7;
        for (StackMapType typesOfLocal : this.typesOfLocals) {
            len += typesOfLocal.hasIndex() ? 3 : 1;
        }
        for (StackMapType typesOfStackItem : this.typesOfStackItems) {
            len += typesOfStackItem.hasIndex() ? 3 : 1;
        }
        return len;
    }

    public int getNumberOfLocals() {
        return this.typesOfLocals.length;
    }

    public int getNumberOfStackItems() {
        return this.typesOfStackItems.length;
    }

    public StackMapType[] getTypesOfLocals() {
        return this.typesOfLocals;
    }

    public StackMapType[] getTypesOfStackItems() {
        return this.typesOfStackItems;
    }

    private boolean invalidFrameType(int f) {
        return !(f == 247 || f >= 248 && f <= 250 || f == 251 || f >= 252 && f <= 254 || f == 255);
    }

    public void setByteCodeOffset(int newOffset) {
        if (newOffset < 0 || newOffset > Short.MAX_VALUE) {
            throw new IllegalArgumentException("Invalid StackMap offset: " + newOffset);
        }
        if (this.frameType >= 0 && this.frameType <= 63) {
            this.frameType = newOffset > 63 ? 251 : newOffset;
        } else if (this.frameType >= 64 && this.frameType <= 127) {
            this.frameType = newOffset > 63 ? 247 : 64 + newOffset;
        } else if (this.invalidFrameType(this.frameType)) {
            throw new IllegalStateException("Invalid StackMap frameType: " + this.frameType);
        }
        this.byteCodeOffset = newOffset;
    }

    public void setConstantPool(ConstantPool constantPool) {
        this.constantPool = constantPool;
    }

    public void setFrameType(int ft) {
        if (ft >= 0 && ft <= 63) {
            this.byteCodeOffset = ft - 0;
        } else if (ft >= 64 && ft <= 127) {
            this.byteCodeOffset = ft - 64;
        } else if (this.invalidFrameType(ft)) {
            throw new IllegalArgumentException("Invalid StackMap frameType");
        }
        this.frameType = ft;
    }

    @Deprecated
    public void setNumberOfLocals(int n) {
    }

    @Deprecated
    public void setNumberOfStackItems(int n) {
    }

    public void setTypesOfLocals(StackMapType[] types) {
        this.typesOfLocals = types != null ? types : StackMapType.EMPTY_ARRAY;
    }

    public void setTypesOfStackItems(StackMapType[] types) {
        this.typesOfStackItems = types != null ? types : StackMapType.EMPTY_ARRAY;
    }

    public String toString() {
        int i;
        StringBuilder buf = new StringBuilder(64);
        buf.append("(");
        if (this.frameType >= 0 && this.frameType <= 63) {
            buf.append("SAME");
        } else if (this.frameType >= 64 && this.frameType <= 127) {
            buf.append("SAME_LOCALS_1_STACK");
        } else if (this.frameType == 247) {
            buf.append("SAME_LOCALS_1_STACK_EXTENDED");
        } else if (this.frameType >= 248 && this.frameType <= 250) {
            buf.append("CHOP ").append(String.valueOf(251 - this.frameType));
        } else if (this.frameType == 251) {
            buf.append("SAME_EXTENDED");
        } else if (this.frameType >= 252 && this.frameType <= 254) {
            buf.append("APPEND ").append(String.valueOf(this.frameType - 251));
        } else if (this.frameType == 255) {
            buf.append("FULL");
        } else {
            buf.append("UNKNOWN (").append(this.frameType).append(")");
        }
        buf.append(", offset delta=").append(this.byteCodeOffset);
        if (this.typesOfLocals.length > 0) {
            buf.append(", locals={");
            for (i = 0; i < this.typesOfLocals.length; ++i) {
                buf.append(this.typesOfLocals[i]);
                if (i >= this.typesOfLocals.length - 1) continue;
                buf.append(", ");
            }
            buf.append("}");
        }
        if (this.typesOfStackItems.length > 0) {
            buf.append(", stack items={");
            for (i = 0; i < this.typesOfStackItems.length; ++i) {
                buf.append(this.typesOfStackItems[i]);
                if (i >= this.typesOfStackItems.length - 1) continue;
                buf.append(", ");
            }
            buf.append("}");
        }
        buf.append(")");
        return buf.toString();
    }

    public void updateByteCodeOffset(int delta) {
        this.setByteCodeOffset(this.byteCodeOffset + delta);
    }
}

