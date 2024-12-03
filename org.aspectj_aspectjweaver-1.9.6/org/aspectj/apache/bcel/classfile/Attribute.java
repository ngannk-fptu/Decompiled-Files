/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.apache.bcel.classfile;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import org.aspectj.apache.bcel.Constants;
import org.aspectj.apache.bcel.classfile.AnnotationDefault;
import org.aspectj.apache.bcel.classfile.BootstrapMethods;
import org.aspectj.apache.bcel.classfile.ClassVisitor;
import org.aspectj.apache.bcel.classfile.Code;
import org.aspectj.apache.bcel.classfile.ConstantPool;
import org.aspectj.apache.bcel.classfile.ConstantValue;
import org.aspectj.apache.bcel.classfile.Deprecated;
import org.aspectj.apache.bcel.classfile.EnclosingMethod;
import org.aspectj.apache.bcel.classfile.ExceptionTable;
import org.aspectj.apache.bcel.classfile.InnerClasses;
import org.aspectj.apache.bcel.classfile.LineNumberTable;
import org.aspectj.apache.bcel.classfile.LocalVariableTable;
import org.aspectj.apache.bcel.classfile.LocalVariableTypeTable;
import org.aspectj.apache.bcel.classfile.MethodParameters;
import org.aspectj.apache.bcel.classfile.Module;
import org.aspectj.apache.bcel.classfile.ModuleMainClass;
import org.aspectj.apache.bcel.classfile.ModulePackages;
import org.aspectj.apache.bcel.classfile.NestHost;
import org.aspectj.apache.bcel.classfile.NestMembers;
import org.aspectj.apache.bcel.classfile.Node;
import org.aspectj.apache.bcel.classfile.Signature;
import org.aspectj.apache.bcel.classfile.SourceFile;
import org.aspectj.apache.bcel.classfile.StackMap;
import org.aspectj.apache.bcel.classfile.Synthetic;
import org.aspectj.apache.bcel.classfile.Unknown;
import org.aspectj.apache.bcel.classfile.annotation.RuntimeInvisAnnos;
import org.aspectj.apache.bcel.classfile.annotation.RuntimeInvisParamAnnos;
import org.aspectj.apache.bcel.classfile.annotation.RuntimeInvisTypeAnnos;
import org.aspectj.apache.bcel.classfile.annotation.RuntimeVisAnnos;
import org.aspectj.apache.bcel.classfile.annotation.RuntimeVisParamAnnos;
import org.aspectj.apache.bcel.classfile.annotation.RuntimeVisTypeAnnos;

public abstract class Attribute
implements Cloneable,
Node,
Serializable {
    public static final Attribute[] NoAttributes = new Attribute[0];
    protected byte tag;
    protected int nameIndex;
    protected int length;
    protected ConstantPool cpool;

    protected Attribute(byte tag, int nameIndex, int length, ConstantPool cpool) {
        this.tag = tag;
        this.nameIndex = nameIndex;
        this.length = length;
        this.cpool = cpool;
    }

    public void dump(DataOutputStream file) throws IOException {
        file.writeShort(this.nameIndex);
        file.writeInt(this.length);
    }

    public static final Attribute readAttribute(DataInputStream file, ConstantPool cpool) throws IOException {
        int tag = -1;
        int idx = file.readUnsignedShort();
        String name = cpool.getConstantUtf8(idx).getValue();
        int len = file.readInt();
        for (int i = 0; i < 28; i = (int)((byte)(i + 1))) {
            if (!name.equals(Constants.ATTRIBUTE_NAMES[i])) continue;
            tag = i;
            break;
        }
        switch (tag) {
            case -1: {
                return new Unknown(idx, len, file, cpool);
            }
            case 1: {
                return new ConstantValue(idx, len, file, cpool);
            }
            case 0: {
                return new SourceFile(idx, len, file, cpool);
            }
            case 2: {
                return new Code(idx, len, file, cpool);
            }
            case 3: {
                return new ExceptionTable(idx, len, file, cpool);
            }
            case 4: {
                return new LineNumberTable(idx, len, file, cpool);
            }
            case 5: {
                return new LocalVariableTable(idx, len, file, cpool);
            }
            case 6: {
                return new InnerClasses(idx, len, file, cpool);
            }
            case 7: {
                return new Synthetic(idx, len, file, cpool);
            }
            case 8: {
                return new Deprecated(idx, len, file, cpool);
            }
            case 10: {
                return new Signature(idx, len, file, cpool);
            }
            case 11: {
                return new StackMap(idx, len, file, cpool);
            }
            case 12: {
                return new RuntimeVisAnnos(idx, len, file, cpool);
            }
            case 13: {
                return new RuntimeInvisAnnos(idx, len, file, cpool);
            }
            case 14: {
                return new RuntimeVisParamAnnos(idx, len, file, cpool);
            }
            case 15: {
                return new RuntimeInvisParamAnnos(idx, len, file, cpool);
            }
            case 18: {
                return new AnnotationDefault(idx, len, file, cpool);
            }
            case 16: {
                return new LocalVariableTypeTable(idx, len, file, cpool);
            }
            case 17: {
                return new EnclosingMethod(idx, len, file, cpool);
            }
            case 19: {
                return new BootstrapMethods(idx, len, file, cpool);
            }
            case 20: {
                return new RuntimeVisTypeAnnos(idx, len, file, cpool);
            }
            case 21: {
                return new RuntimeInvisTypeAnnos(idx, len, file, cpool);
            }
            case 22: {
                return new MethodParameters(idx, len, file, cpool);
            }
            case 23: {
                return new Module(idx, len, file, cpool);
            }
            case 24: {
                return new ModulePackages(idx, len, file, cpool);
            }
            case 25: {
                return new ModuleMainClass(idx, len, file, cpool);
            }
            case 26: {
                return new NestHost(idx, len, file, cpool);
            }
            case 27: {
                return new NestMembers(idx, len, file, cpool);
            }
        }
        throw new IllegalStateException();
    }

    public String getName() {
        return this.cpool.getConstantUtf8(this.nameIndex).getValue();
    }

    public final int getLength() {
        return this.length;
    }

    public final int getNameIndex() {
        return this.nameIndex;
    }

    public final byte getTag() {
        return this.tag;
    }

    public final ConstantPool getConstantPool() {
        return this.cpool;
    }

    public String toString() {
        return Constants.ATTRIBUTE_NAMES[this.tag];
    }

    @Override
    public abstract void accept(ClassVisitor var1);
}

