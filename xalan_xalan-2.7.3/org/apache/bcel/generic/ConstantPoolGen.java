/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.generic;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.apache.bcel.classfile.Constant;
import org.apache.bcel.classfile.ConstantCP;
import org.apache.bcel.classfile.ConstantClass;
import org.apache.bcel.classfile.ConstantDouble;
import org.apache.bcel.classfile.ConstantDynamic;
import org.apache.bcel.classfile.ConstantFieldref;
import org.apache.bcel.classfile.ConstantFloat;
import org.apache.bcel.classfile.ConstantInteger;
import org.apache.bcel.classfile.ConstantInterfaceMethodref;
import org.apache.bcel.classfile.ConstantInvokeDynamic;
import org.apache.bcel.classfile.ConstantLong;
import org.apache.bcel.classfile.ConstantMethodref;
import org.apache.bcel.classfile.ConstantNameAndType;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.ConstantString;
import org.apache.bcel.classfile.ConstantUtf8;
import org.apache.bcel.classfile.Utility;
import org.apache.bcel.generic.ArrayType;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.ObjectType;

public class ConstantPoolGen {
    private static final int DEFAULT_BUFFER_SIZE = 256;
    private static final String METHODREF_DELIM = ":";
    private static final String IMETHODREF_DELIM = "#";
    private static final String FIELDREF_DELIM = "&";
    private static final String NAT_DELIM = "%";
    @Deprecated
    protected int size;
    @Deprecated
    protected Constant[] constants;
    @Deprecated
    protected int index = 1;
    private final Map<String, Integer> stringTable = new HashMap<String, Integer>();
    private final Map<String, Integer> classTable = new HashMap<String, Integer>();
    private final Map<String, Integer> utf8Table = new HashMap<String, Integer>();
    private final Map<String, Integer> natTable = new HashMap<String, Integer>();
    private final Map<String, Integer> cpTable = new HashMap<String, Integer>();

    public ConstantPoolGen() {
        this.size = 256;
        this.constants = new Constant[this.size];
    }

    public ConstantPoolGen(Constant[] cs) {
        StringBuilder sb = new StringBuilder(256);
        this.size = Math.min(Math.max(256, cs.length + 64), 65536);
        this.constants = new Constant[this.size];
        System.arraycopy(cs, 0, this.constants, 0, cs.length);
        if (cs.length > 0) {
            this.index = cs.length;
        }
        for (int i = 1; i < this.index; ++i) {
            ConstantUtf8 u8;
            String className;
            String key;
            ConstantUtf8 u82;
            Constant s;
            Constant c = this.constants[i];
            if (c instanceof ConstantString) {
                s = (ConstantString)c;
                u82 = (ConstantUtf8)this.constants[((ConstantString)s).getStringIndex()];
                key = u82.getBytes();
                if (this.stringTable.containsKey(key)) continue;
                this.stringTable.put(key, i);
                continue;
            }
            if (c instanceof ConstantClass) {
                s = (ConstantClass)c;
                u82 = (ConstantUtf8)this.constants[((ConstantClass)s).getNameIndex()];
                key = u82.getBytes();
                if (this.classTable.containsKey(key)) continue;
                this.classTable.put(key, i);
                continue;
            }
            if (c instanceof ConstantNameAndType) {
                ConstantNameAndType n = (ConstantNameAndType)c;
                ConstantUtf8 u8NameIdx = (ConstantUtf8)this.constants[n.getNameIndex()];
                ConstantUtf8 u8SigIdx = (ConstantUtf8)this.constants[n.getSignatureIndex()];
                sb.append(u8NameIdx.getBytes());
                sb.append(NAT_DELIM);
                sb.append(u8SigIdx.getBytes());
                String key2 = sb.toString();
                sb.delete(0, sb.length());
                if (this.natTable.containsKey(key2)) continue;
                this.natTable.put(key2, i);
                continue;
            }
            if (c instanceof ConstantUtf8) {
                ConstantUtf8 u = (ConstantUtf8)c;
                String key3 = u.getBytes();
                if (this.utf8Table.containsKey(key3)) continue;
                this.utf8Table.put(key3, i);
                continue;
            }
            if (!(c instanceof ConstantCP)) continue;
            ConstantCP m = (ConstantCP)c;
            if (c instanceof ConstantInvokeDynamic) {
                className = Integer.toString(((ConstantInvokeDynamic)m).getBootstrapMethodAttrIndex());
            } else if (c instanceof ConstantDynamic) {
                className = Integer.toString(((ConstantDynamic)m).getBootstrapMethodAttrIndex());
            } else {
                ConstantClass clazz = (ConstantClass)this.constants[m.getClassIndex()];
                u8 = (ConstantUtf8)this.constants[clazz.getNameIndex()];
                className = Utility.pathToPackage(u8.getBytes());
            }
            ConstantNameAndType n = (ConstantNameAndType)this.constants[m.getNameAndTypeIndex()];
            u8 = (ConstantUtf8)this.constants[n.getNameIndex()];
            String methodName = u8.getBytes();
            u8 = (ConstantUtf8)this.constants[n.getSignatureIndex()];
            String signature = u8.getBytes();
            String delim = METHODREF_DELIM;
            if (c instanceof ConstantInterfaceMethodref) {
                delim = IMETHODREF_DELIM;
            } else if (c instanceof ConstantFieldref) {
                delim = FIELDREF_DELIM;
            }
            sb.append(className);
            sb.append(delim);
            sb.append(methodName);
            sb.append(delim);
            sb.append(signature);
            String key4 = sb.toString();
            sb.delete(0, sb.length());
            if (this.cpTable.containsKey(key4)) continue;
            this.cpTable.put(key4, i);
        }
    }

    public ConstantPoolGen(ConstantPool cp) {
        this(cp.getConstantPool());
    }

    public int addArrayClass(ArrayType type) {
        return this.addClass_(type.getSignature());
    }

    public int addClass(ObjectType type) {
        return this.addClass(type.getClassName());
    }

    public int addClass(String str) {
        return this.addClass_(Utility.packageToPath(str));
    }

    private int addClass_(String clazz) {
        int cpRet = this.lookupClass(clazz);
        if (cpRet != -1) {
            return cpRet;
        }
        this.adjustSize();
        ConstantClass c = new ConstantClass(this.addUtf8(clazz));
        int ret = this.index;
        this.constants[this.index++] = c;
        return this.computeIfAbsent(this.classTable, clazz, ret);
    }

    public int addConstant(Constant constant, ConstantPoolGen cpGen) {
        Constant[] constants = cpGen.getConstantPool().getConstantPool();
        switch (constant.getTag()) {
            case 8: {
                ConstantString s = (ConstantString)constant;
                ConstantUtf8 u8 = (ConstantUtf8)constants[s.getStringIndex()];
                return this.addString(u8.getBytes());
            }
            case 7: {
                ConstantClass s = (ConstantClass)constant;
                ConstantUtf8 u8 = (ConstantUtf8)constants[s.getNameIndex()];
                return this.addClass(u8.getBytes());
            }
            case 12: {
                ConstantNameAndType n = (ConstantNameAndType)constant;
                ConstantUtf8 u8 = (ConstantUtf8)constants[n.getNameIndex()];
                ConstantUtf8 u8_2 = (ConstantUtf8)constants[n.getSignatureIndex()];
                return this.addNameAndType(u8.getBytes(), u8_2.getBytes());
            }
            case 1: {
                return this.addUtf8(((ConstantUtf8)constant).getBytes());
            }
            case 6: {
                return this.addDouble(((ConstantDouble)constant).getBytes());
            }
            case 4: {
                return this.addFloat(((ConstantFloat)constant).getBytes());
            }
            case 5: {
                return this.addLong(((ConstantLong)constant).getBytes());
            }
            case 3: {
                return this.addInteger(((ConstantInteger)constant).getBytes());
            }
            case 9: 
            case 10: 
            case 11: {
                ConstantCP m = (ConstantCP)constant;
                ConstantClass clazz = (ConstantClass)constants[m.getClassIndex()];
                ConstantNameAndType n = (ConstantNameAndType)constants[m.getNameAndTypeIndex()];
                ConstantUtf8 u8 = (ConstantUtf8)constants[clazz.getNameIndex()];
                String className = Utility.pathToPackage(u8.getBytes());
                u8 = (ConstantUtf8)constants[n.getNameIndex()];
                String name = u8.getBytes();
                u8 = (ConstantUtf8)constants[n.getSignatureIndex()];
                String signature = u8.getBytes();
                switch (constant.getTag()) {
                    case 11: {
                        return this.addInterfaceMethodref(className, name, signature);
                    }
                    case 10: {
                        return this.addMethodref(className, name, signature);
                    }
                    case 9: {
                        return this.addFieldref(className, name, signature);
                    }
                }
                throw new IllegalArgumentException("Unknown constant type " + constant);
            }
        }
        throw new IllegalArgumentException("Unknown constant type " + constant);
    }

    public int addDouble(double n) {
        int ret = this.lookupDouble(n);
        if (ret != -1) {
            return ret;
        }
        this.adjustSize();
        ret = this.index;
        this.constants[this.index] = new ConstantDouble(n);
        this.index += 2;
        return ret;
    }

    public int addFieldref(String className, String fieldName, String signature) {
        int cpRet = this.lookupFieldref(className, fieldName, signature);
        if (cpRet != -1) {
            return cpRet;
        }
        this.adjustSize();
        int classIndex = this.addClass(className);
        int nameAndTypeIndex = this.addNameAndType(fieldName, signature);
        int ret = this.index;
        this.constants[this.index++] = new ConstantFieldref(classIndex, nameAndTypeIndex);
        return this.computeIfAbsent(this.cpTable, className + FIELDREF_DELIM + fieldName + FIELDREF_DELIM + signature, ret);
    }

    public int addFloat(float n) {
        int ret = this.lookupFloat(n);
        if (ret != -1) {
            return ret;
        }
        this.adjustSize();
        ret = this.index;
        this.constants[this.index++] = new ConstantFloat(n);
        return ret;
    }

    public int addInteger(int n) {
        int ret = this.lookupInteger(n);
        if (ret != -1) {
            return ret;
        }
        this.adjustSize();
        ret = this.index;
        this.constants[this.index++] = new ConstantInteger(n);
        return ret;
    }

    public int addInterfaceMethodref(MethodGen method) {
        return this.addInterfaceMethodref(method.getClassName(), method.getName(), method.getSignature());
    }

    public int addInterfaceMethodref(String className, String methodName, String signature) {
        int cpRet = this.lookupInterfaceMethodref(className, methodName, signature);
        if (cpRet != -1) {
            return cpRet;
        }
        this.adjustSize();
        int classIndex = this.addClass(className);
        int nameAndTypeIndex = this.addNameAndType(methodName, signature);
        int ret = this.index;
        this.constants[this.index++] = new ConstantInterfaceMethodref(classIndex, nameAndTypeIndex);
        return this.computeIfAbsent(this.cpTable, className + IMETHODREF_DELIM + methodName + IMETHODREF_DELIM + signature, ret);
    }

    public int addLong(long n) {
        int ret = this.lookupLong(n);
        if (ret != -1) {
            return ret;
        }
        this.adjustSize();
        ret = this.index;
        this.constants[this.index] = new ConstantLong(n);
        this.index += 2;
        return ret;
    }

    public int addMethodref(MethodGen method) {
        return this.addMethodref(method.getClassName(), method.getName(), method.getSignature());
    }

    public int addMethodref(String className, String methodName, String signature) {
        int cpRet = this.lookupMethodref(className, methodName, signature);
        if (cpRet != -1) {
            return cpRet;
        }
        this.adjustSize();
        int nameAndTypeIndex = this.addNameAndType(methodName, signature);
        int classIndex = this.addClass(className);
        int ret = this.index;
        this.constants[this.index++] = new ConstantMethodref(classIndex, nameAndTypeIndex);
        return this.computeIfAbsent(this.cpTable, className + METHODREF_DELIM + methodName + METHODREF_DELIM + signature, ret);
    }

    public int addNameAndType(String name, String signature) {
        int ret = this.lookupNameAndType(name, signature);
        if (ret != -1) {
            return ret;
        }
        this.adjustSize();
        int nameIndex = this.addUtf8(name);
        int signatureIndex = this.addUtf8(signature);
        ret = this.index;
        this.constants[this.index++] = new ConstantNameAndType(nameIndex, signatureIndex);
        return this.computeIfAbsent(this.natTable, name + NAT_DELIM + signature, ret);
    }

    public int addString(String str) {
        int ret = this.lookupString(str);
        if (ret != -1) {
            return ret;
        }
        int utf8 = this.addUtf8(str);
        this.adjustSize();
        ConstantString s = new ConstantString(utf8);
        ret = this.index;
        this.constants[this.index++] = s;
        return this.computeIfAbsent(this.stringTable, str, ret);
    }

    public int addUtf8(String n) {
        int ret = this.lookupUtf8(n);
        if (ret != -1) {
            return ret;
        }
        this.adjustSize();
        ret = this.index;
        this.constants[this.index++] = new ConstantUtf8(n);
        return this.computeIfAbsent(this.utf8Table, n, ret);
    }

    protected void adjustSize() {
        if (this.index + 3 >= 65536) {
            throw new IllegalStateException("The number of constants " + (this.index + 3) + " is over the size of the constant pool: " + 65535);
        }
        if (this.index + 3 >= this.size) {
            Constant[] cs = this.constants;
            this.size *= 2;
            this.size = Math.min(this.size, 65536);
            this.constants = new Constant[this.size];
            System.arraycopy(cs, 0, this.constants, 0, this.index);
        }
    }

    private int computeIfAbsent(Map<String, Integer> map, String key, int value) {
        return map.computeIfAbsent(key, k -> value);
    }

    public Constant getConstant(int i) {
        return this.constants[i];
    }

    public ConstantPool getConstantPool() {
        return new ConstantPool(this.constants);
    }

    public ConstantPool getFinalConstantPool() {
        return new ConstantPool(Arrays.copyOf(this.constants, this.index));
    }

    private int getIndex(Map<String, Integer> map, String key) {
        return this.toIndex(map.get(key));
    }

    public int getSize() {
        return this.index;
    }

    public int lookupClass(String str) {
        return this.getIndex(this.classTable, Utility.packageToPath(str));
    }

    public int lookupDouble(double n) {
        long bits = Double.doubleToLongBits(n);
        for (int i = 1; i < this.index; ++i) {
            ConstantDouble c;
            if (!(this.constants[i] instanceof ConstantDouble) || Double.doubleToLongBits((c = (ConstantDouble)this.constants[i]).getBytes()) != bits) continue;
            return i;
        }
        return -1;
    }

    public int lookupFieldref(String className, String fieldName, String signature) {
        return this.getIndex(this.cpTable, className + FIELDREF_DELIM + fieldName + FIELDREF_DELIM + signature);
    }

    public int lookupFloat(float n) {
        int bits = Float.floatToIntBits(n);
        for (int i = 1; i < this.index; ++i) {
            ConstantFloat c;
            if (!(this.constants[i] instanceof ConstantFloat) || Float.floatToIntBits((c = (ConstantFloat)this.constants[i]).getBytes()) != bits) continue;
            return i;
        }
        return -1;
    }

    public int lookupInteger(int n) {
        for (int i = 1; i < this.index; ++i) {
            ConstantInteger c;
            if (!(this.constants[i] instanceof ConstantInteger) || (c = (ConstantInteger)this.constants[i]).getBytes() != n) continue;
            return i;
        }
        return -1;
    }

    public int lookupInterfaceMethodref(MethodGen method) {
        return this.lookupInterfaceMethodref(method.getClassName(), method.getName(), method.getSignature());
    }

    public int lookupInterfaceMethodref(String className, String methodName, String signature) {
        return this.getIndex(this.cpTable, className + IMETHODREF_DELIM + methodName + IMETHODREF_DELIM + signature);
    }

    public int lookupLong(long n) {
        for (int i = 1; i < this.index; ++i) {
            ConstantLong c;
            if (!(this.constants[i] instanceof ConstantLong) || (c = (ConstantLong)this.constants[i]).getBytes() != n) continue;
            return i;
        }
        return -1;
    }

    public int lookupMethodref(MethodGen method) {
        return this.lookupMethodref(method.getClassName(), method.getName(), method.getSignature());
    }

    public int lookupMethodref(String className, String methodName, String signature) {
        return this.getIndex(this.cpTable, className + METHODREF_DELIM + methodName + METHODREF_DELIM + signature);
    }

    public int lookupNameAndType(String name, String signature) {
        return this.getIndex(this.natTable, name + NAT_DELIM + signature);
    }

    public int lookupString(String str) {
        return this.getIndex(this.stringTable, str);
    }

    public int lookupUtf8(String n) {
        return this.getIndex(this.utf8Table, n);
    }

    public void setConstant(int i, Constant c) {
        this.constants[i] = c;
    }

    private int toIndex(Integer index) {
        return index != null ? index : -1;
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();
        for (int i = 1; i < this.index; ++i) {
            buf.append(i).append(")").append(this.constants[i]).append("\n");
        }
        return buf.toString();
    }
}

