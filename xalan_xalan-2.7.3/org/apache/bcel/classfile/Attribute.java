/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.classfile;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.bcel.Const;
import org.apache.bcel.classfile.AnnotationDefault;
import org.apache.bcel.classfile.AttributeReader;
import org.apache.bcel.classfile.BootstrapMethods;
import org.apache.bcel.classfile.Code;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.ConstantValue;
import org.apache.bcel.classfile.Deprecated;
import org.apache.bcel.classfile.EnclosingMethod;
import org.apache.bcel.classfile.ExceptionTable;
import org.apache.bcel.classfile.InnerClasses;
import org.apache.bcel.classfile.LineNumberTable;
import org.apache.bcel.classfile.LocalVariableTable;
import org.apache.bcel.classfile.LocalVariableTypeTable;
import org.apache.bcel.classfile.MethodParameters;
import org.apache.bcel.classfile.Module;
import org.apache.bcel.classfile.ModuleMainClass;
import org.apache.bcel.classfile.ModulePackages;
import org.apache.bcel.classfile.NestHost;
import org.apache.bcel.classfile.NestMembers;
import org.apache.bcel.classfile.Node;
import org.apache.bcel.classfile.PMGClass;
import org.apache.bcel.classfile.RuntimeInvisibleAnnotations;
import org.apache.bcel.classfile.RuntimeInvisibleParameterAnnotations;
import org.apache.bcel.classfile.RuntimeVisibleAnnotations;
import org.apache.bcel.classfile.RuntimeVisibleParameterAnnotations;
import org.apache.bcel.classfile.Signature;
import org.apache.bcel.classfile.SourceFile;
import org.apache.bcel.classfile.StackMap;
import org.apache.bcel.classfile.Synthetic;
import org.apache.bcel.classfile.Unknown;
import org.apache.bcel.classfile.UnknownAttributeReader;
import org.apache.bcel.classfile.Visitor;
import org.apache.bcel.util.Args;

public abstract class Attribute
implements Cloneable,
Node {
    private static final boolean debug = Boolean.getBoolean(Attribute.class.getCanonicalName() + ".debug");
    private static final Map<String, Object> READERS = new HashMap<String, Object>();
    public static final Attribute[] EMPTY_ARRAY = new Attribute[0];
    @java.lang.Deprecated
    protected int name_index;
    @java.lang.Deprecated
    protected int length;
    @java.lang.Deprecated
    protected byte tag;
    @java.lang.Deprecated
    protected ConstantPool constant_pool;

    @java.lang.Deprecated
    public static void addAttributeReader(String name, AttributeReader attributeReader) {
        READERS.put(name, attributeReader);
    }

    public static void addAttributeReader(String name, UnknownAttributeReader unknownAttributeReader) {
        READERS.put(name, unknownAttributeReader);
    }

    protected static void println(String msg) {
        if (debug) {
            System.err.println(msg);
        }
    }

    public static Attribute readAttribute(DataInput dataInput, ConstantPool constantPool) throws IOException {
        int tag = -1;
        int nameIndex = dataInput.readUnsignedShort();
        String name = constantPool.getConstantUtf8(nameIndex).getBytes();
        int length = dataInput.readInt();
        for (int i = 0; i < 27; i = (int)((byte)(i + 1))) {
            if (!name.equals(Const.getAttributeName(i))) continue;
            tag = i;
            break;
        }
        switch (tag) {
            case -1: {
                Object r = READERS.get(name);
                if (r instanceof UnknownAttributeReader) {
                    return ((UnknownAttributeReader)r).createAttribute(nameIndex, length, dataInput, constantPool);
                }
                return new Unknown(nameIndex, length, dataInput, constantPool);
            }
            case 1: {
                return new ConstantValue(nameIndex, length, dataInput, constantPool);
            }
            case 0: {
                return new SourceFile(nameIndex, length, dataInput, constantPool);
            }
            case 2: {
                return new Code(nameIndex, length, dataInput, constantPool);
            }
            case 3: {
                return new ExceptionTable(nameIndex, length, dataInput, constantPool);
            }
            case 4: {
                return new LineNumberTable(nameIndex, length, dataInput, constantPool);
            }
            case 5: {
                return new LocalVariableTable(nameIndex, length, dataInput, constantPool);
            }
            case 6: {
                return new InnerClasses(nameIndex, length, dataInput, constantPool);
            }
            case 7: {
                return new Synthetic(nameIndex, length, dataInput, constantPool);
            }
            case 8: {
                return new Deprecated(nameIndex, length, dataInput, constantPool);
            }
            case 9: {
                return new PMGClass(nameIndex, length, dataInput, constantPool);
            }
            case 10: {
                return new Signature(nameIndex, length, dataInput, constantPool);
            }
            case 11: {
                Attribute.println("Warning: Obsolete StackMap attribute ignored.");
                return new Unknown(nameIndex, length, dataInput, constantPool);
            }
            case 12: {
                return new RuntimeVisibleAnnotations(nameIndex, length, dataInput, constantPool);
            }
            case 13: {
                return new RuntimeInvisibleAnnotations(nameIndex, length, dataInput, constantPool);
            }
            case 14: {
                return new RuntimeVisibleParameterAnnotations(nameIndex, length, dataInput, constantPool);
            }
            case 15: {
                return new RuntimeInvisibleParameterAnnotations(nameIndex, length, dataInput, constantPool);
            }
            case 16: {
                return new AnnotationDefault(nameIndex, length, dataInput, constantPool);
            }
            case 17: {
                return new LocalVariableTypeTable(nameIndex, length, dataInput, constantPool);
            }
            case 18: {
                return new EnclosingMethod(nameIndex, length, dataInput, constantPool);
            }
            case 19: {
                return new StackMap(nameIndex, length, dataInput, constantPool);
            }
            case 20: {
                return new BootstrapMethods(nameIndex, length, dataInput, constantPool);
            }
            case 21: {
                return new MethodParameters(nameIndex, length, dataInput, constantPool);
            }
            case 22: {
                return new Module(nameIndex, length, dataInput, constantPool);
            }
            case 23: {
                return new ModulePackages(nameIndex, length, dataInput, constantPool);
            }
            case 24: {
                return new ModuleMainClass(nameIndex, length, dataInput, constantPool);
            }
            case 25: {
                return new NestHost(nameIndex, length, dataInput, constantPool);
            }
            case 26: {
                return new NestMembers(nameIndex, length, dataInput, constantPool);
            }
        }
        throw new IllegalStateException("Unrecognized attribute type tag parsed: " + tag);
    }

    public static Attribute readAttribute(DataInputStream dataInputStream, ConstantPool constantPool) throws IOException {
        return Attribute.readAttribute((DataInput)dataInputStream, constantPool);
    }

    public static void removeAttributeReader(String name) {
        READERS.remove(name);
    }

    protected Attribute(byte tag, int nameIndex, int length, ConstantPool constantPool) {
        this.tag = tag;
        this.name_index = Args.requireU2(nameIndex, 0, constantPool.getLength(), this.getClass().getSimpleName() + " name index");
        this.length = Args.requireU4(length, this.getClass().getSimpleName() + " attribute length");
        this.constant_pool = constantPool;
    }

    @Override
    public abstract void accept(Visitor var1);

    public Object clone() {
        Attribute attr = null;
        try {
            attr = (Attribute)super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new Error("Clone Not Supported");
        }
        return attr;
    }

    public abstract Attribute copy(ConstantPool var1);

    public void dump(DataOutputStream file) throws IOException {
        file.writeShort(this.name_index);
        file.writeInt(this.length);
    }

    public final ConstantPool getConstantPool() {
        return this.constant_pool;
    }

    public final int getLength() {
        return this.length;
    }

    public String getName() {
        return this.constant_pool.getConstantUtf8(this.name_index).getBytes();
    }

    public final int getNameIndex() {
        return this.name_index;
    }

    public final byte getTag() {
        return this.tag;
    }

    public final void setConstantPool(ConstantPool constantPool) {
        this.constant_pool = constantPool;
    }

    public final void setLength(int length) {
        this.length = length;
    }

    public final void setNameIndex(int nameIndex) {
        this.name_index = nameIndex;
    }

    public String toString() {
        return Const.getAttributeName(this.tag);
    }
}

