/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.apache.bcel.classfile;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.aspectj.apache.bcel.Constants;
import org.aspectj.apache.bcel.classfile.ClassFormatException;
import org.aspectj.apache.bcel.classfile.ClassVisitor;
import org.aspectj.apache.bcel.classfile.Constant;
import org.aspectj.apache.bcel.classfile.ConstantCP;
import org.aspectj.apache.bcel.classfile.ConstantClass;
import org.aspectj.apache.bcel.classfile.ConstantDouble;
import org.aspectj.apache.bcel.classfile.ConstantFieldref;
import org.aspectj.apache.bcel.classfile.ConstantFloat;
import org.aspectj.apache.bcel.classfile.ConstantInteger;
import org.aspectj.apache.bcel.classfile.ConstantInterfaceMethodref;
import org.aspectj.apache.bcel.classfile.ConstantInvokeDynamic;
import org.aspectj.apache.bcel.classfile.ConstantLong;
import org.aspectj.apache.bcel.classfile.ConstantMethodHandle;
import org.aspectj.apache.bcel.classfile.ConstantMethodType;
import org.aspectj.apache.bcel.classfile.ConstantMethodref;
import org.aspectj.apache.bcel.classfile.ConstantModule;
import org.aspectj.apache.bcel.classfile.ConstantNameAndType;
import org.aspectj.apache.bcel.classfile.ConstantPackage;
import org.aspectj.apache.bcel.classfile.ConstantString;
import org.aspectj.apache.bcel.classfile.ConstantUtf8;
import org.aspectj.apache.bcel.classfile.Node;
import org.aspectj.apache.bcel.classfile.SimpleConstant;
import org.aspectj.apache.bcel.classfile.Utility;
import org.aspectj.apache.bcel.generic.ArrayType;
import org.aspectj.apache.bcel.generic.ObjectType;

public class ConstantPool
implements Node {
    private Constant[] pool;
    private int poolSize;
    private Map<String, Integer> utf8Cache = new HashMap<String, Integer>();
    private Map<String, Integer> methodCache = new HashMap<String, Integer>();
    private Map<String, Integer> fieldCache = new HashMap<String, Integer>();

    public int getSize() {
        return this.poolSize;
    }

    public ConstantPool() {
        this.pool = new Constant[10];
        this.poolSize = 0;
    }

    public ConstantPool(Constant[] constants) {
        this.pool = constants;
        this.poolSize = constants == null ? 0 : constants.length;
    }

    ConstantPool(DataInputStream file) throws IOException {
        this.poolSize = file.readUnsignedShort();
        this.pool = new Constant[this.poolSize];
        for (int i = 1; i < this.poolSize; ++i) {
            this.pool[i] = Constant.readConstant(file);
            byte tag = this.pool[i].getTag();
            if (tag != 6 && tag != 5) continue;
            ++i;
        }
    }

    public Constant getConstant(int index, byte tag) {
        Constant c = this.getConstant(index);
        if (c.tag == tag) {
            return c;
        }
        throw new ClassFormatException("Expected class '" + Constants.CONSTANT_NAMES[tag] + "' at index " + index + " and found " + c);
    }

    public Constant getConstant(int index) {
        try {
            return this.pool[index];
        }
        catch (ArrayIndexOutOfBoundsException aioobe) {
            throw new ClassFormatException("Index " + index + " into constant pool (size:" + this.poolSize + ") is invalid");
        }
    }

    public ConstantPool copy() {
        Constant[] newConstants = new Constant[this.poolSize];
        for (int i = 1; i < this.poolSize; ++i) {
            if (this.pool[i] == null) continue;
            newConstants[i] = this.pool[i].copy();
        }
        return new ConstantPool(newConstants);
    }

    public String getConstantString(int index, byte tag) throws ClassFormatException {
        int i;
        Constant c = this.getConstant(index, tag);
        switch (tag) {
            case 7: {
                i = ((ConstantClass)c).getNameIndex();
                break;
            }
            case 8: {
                i = ((ConstantString)c).getStringIndex();
                break;
            }
            default: {
                throw new RuntimeException("getConstantString called with illegal tag " + tag);
            }
        }
        c = this.getConstant(i, (byte)1);
        return ((ConstantUtf8)c).getValue();
    }

    public String constantToString(Constant c) {
        String str;
        switch (c.tag) {
            case 7: {
                int i = ((ConstantClass)c).getNameIndex();
                c = this.getConstant(i, (byte)1);
                str = Utility.compactClassName(((ConstantUtf8)c).getValue(), false);
                break;
            }
            case 8: {
                int i = ((ConstantString)c).getStringIndex();
                c = this.getConstant(i, (byte)1);
                str = "\"" + ConstantPool.escape(((ConstantUtf8)c).getValue()) + "\"";
                break;
            }
            case 1: 
            case 3: 
            case 4: 
            case 5: 
            case 6: {
                str = ((SimpleConstant)((Object)c)).getStringValue();
                break;
            }
            case 12: {
                str = this.constantToString(((ConstantNameAndType)c).getNameIndex(), (byte)1) + " " + this.constantToString(((ConstantNameAndType)c).getSignatureIndex(), (byte)1);
                break;
            }
            case 9: 
            case 10: 
            case 11: {
                str = this.constantToString(((ConstantCP)c).getClassIndex(), (byte)7) + "." + this.constantToString(((ConstantCP)c).getNameAndTypeIndex(), (byte)12);
                break;
            }
            case 18: {
                ConstantInvokeDynamic cID = (ConstantInvokeDynamic)c;
                return "#" + cID.getBootstrapMethodAttrIndex() + "." + this.constantToString(cID.getNameAndTypeIndex(), (byte)12);
            }
            case 15: {
                ConstantMethodHandle cMH = (ConstantMethodHandle)c;
                return cMH.getReferenceKind() + ":" + this.constantToString(cMH.getReferenceIndex(), (byte)10);
            }
            case 16: {
                ConstantMethodType cMT = (ConstantMethodType)c;
                return this.constantToString(cMT.getDescriptorIndex(), (byte)1);
            }
            case 19: {
                ConstantModule cM = (ConstantModule)c;
                return "Module:" + this.constantToString(cM.getNameIndex(), (byte)1);
            }
            case 20: {
                ConstantPackage cP = (ConstantPackage)c;
                return "Package:" + this.constantToString(cP.getNameIndex(), (byte)1);
            }
            default: {
                throw new RuntimeException("Unknown constant type " + c.tag);
            }
        }
        return str;
    }

    private static final String escape(String str) {
        int len = str.length();
        StringBuffer buf = new StringBuffer(len + 5);
        char[] ch = str.toCharArray();
        block7: for (int i = 0; i < len; ++i) {
            switch (ch[i]) {
                case '\n': {
                    buf.append("\\n");
                    continue block7;
                }
                case '\r': {
                    buf.append("\\r");
                    continue block7;
                }
                case '\t': {
                    buf.append("\\t");
                    continue block7;
                }
                case '\b': {
                    buf.append("\\b");
                    continue block7;
                }
                case '\"': {
                    buf.append("\\\"");
                    continue block7;
                }
                default: {
                    buf.append(ch[i]);
                }
            }
        }
        return buf.toString();
    }

    public String constantToString(int index, byte tag) {
        Constant c = this.getConstant(index, tag);
        return this.constantToString(c);
    }

    public String constantToString(int index) {
        return this.constantToString(this.getConstant(index));
    }

    @Override
    public void accept(ClassVisitor v) {
        v.visitConstantPool(this);
    }

    public Constant[] getConstantPool() {
        return this.pool;
    }

    public void dump(DataOutputStream file) throws IOException {
        file.writeShort(this.poolSize);
        for (int i = 1; i < this.poolSize; ++i) {
            if (this.pool[i] == null) continue;
            this.pool[i].dump(file);
        }
    }

    public ConstantUtf8 getConstantUtf8(int index) {
        Constant c = this.getConstant(index);
        assert (c != null);
        assert (c.tag == 1);
        return (ConstantUtf8)c;
    }

    public ConstantModule getConstantModule(int index) {
        Constant c = this.getConstant(index);
        assert (c != null);
        assert (c.tag == 19);
        return (ConstantModule)c;
    }

    public ConstantPackage getConstantPackage(int index) {
        Constant c = this.getConstant(index);
        assert (c != null);
        assert (c.tag == 20);
        return (ConstantPackage)c;
    }

    public String getConstantString_CONSTANTClass(int index) {
        ConstantClass c = (ConstantClass)this.getConstant(index, (byte)7);
        index = c.getNameIndex();
        return ((ConstantUtf8)this.getConstant(index, (byte)1)).getValue();
    }

    public int getLength() {
        return this.poolSize;
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();
        for (int i = 1; i < this.poolSize; ++i) {
            buf.append(i + ")" + this.pool[i] + "\n");
        }
        return buf.toString();
    }

    public int lookupInteger(int n) {
        for (int i = 1; i < this.poolSize; ++i) {
            ConstantInteger c;
            if (!(this.pool[i] instanceof ConstantInteger) || (c = (ConstantInteger)this.pool[i]).getValue() != n) continue;
            return i;
        }
        return -1;
    }

    public int lookupUtf8(String string) {
        Integer pos = this.utf8Cache.get(string);
        if (pos != null) {
            return pos;
        }
        for (int i = 1; i < this.poolSize; ++i) {
            Constant c = this.pool[i];
            if (c == null || c.tag != 1 || !((ConstantUtf8)c).getValue().equals(string)) continue;
            this.utf8Cache.put(string, i);
            return i;
        }
        return -1;
    }

    public int lookupClass(String classname) {
        for (int i = 1; i < this.poolSize; ++i) {
            int cIndex;
            String cName;
            Constant c = this.pool[i];
            if (c == null || c.tag != 7 || !(cName = ((ConstantUtf8)this.pool[cIndex = ((ConstantClass)c).getNameIndex()]).getValue()).equals(classname)) continue;
            return i;
        }
        return -1;
    }

    public int addUtf8(String n) {
        int ret = this.lookupUtf8(n);
        if (ret != -1) {
            return ret;
        }
        this.adjustSize();
        ret = this.poolSize;
        this.pool[this.poolSize++] = new ConstantUtf8(n);
        return ret;
    }

    public int addInteger(int n) {
        int ret = this.lookupInteger(n);
        if (ret != -1) {
            return ret;
        }
        this.adjustSize();
        ret = this.poolSize;
        this.pool[this.poolSize++] = new ConstantInteger(n);
        return ret;
    }

    public int addArrayClass(ArrayType type) {
        return this.addClass(type.getSignature());
    }

    public int addClass(ObjectType type) {
        return this.addClass(type.getClassName());
    }

    public int addClass(String classname) {
        String toAdd = classname.replace('.', '/');
        int ret = this.lookupClass(toAdd);
        if (ret != -1) {
            return ret;
        }
        this.adjustSize();
        ConstantClass c = new ConstantClass(this.addUtf8(toAdd));
        ret = this.poolSize;
        this.pool[this.poolSize++] = c;
        return ret;
    }

    private void adjustSize() {
        if (this.poolSize + 3 >= this.pool.length) {
            Constant[] cs = this.pool;
            this.pool = new Constant[cs.length + 8];
            System.arraycopy(cs, 0, this.pool, 0, cs.length);
        }
        if (this.poolSize == 0) {
            this.poolSize = 1;
        }
    }

    public int addFieldref(String class_name, String field_name, String signature) {
        int ret = this.lookupFieldref(class_name, field_name, signature);
        if (ret != -1) {
            return ret;
        }
        this.adjustSize();
        int class_index = this.addClass(class_name);
        int name_and_type_index = this.addNameAndType(field_name, signature);
        ret = this.poolSize;
        this.pool[this.poolSize++] = new ConstantFieldref(class_index, name_and_type_index);
        return ret;
    }

    public int lookupFieldref(String searchClassname, String searchFieldname, String searchSignature) {
        String k = new StringBuffer().append(searchClassname = searchClassname.replace('.', '/')).append(searchFieldname).append(searchSignature).toString();
        Integer pos = this.fieldCache.get(k);
        if (pos != null) {
            return pos;
        }
        for (int i = 1; i < this.poolSize; ++i) {
            String typeSignature;
            String name;
            Constant c = this.pool[i];
            if (c == null || c.tag != 9) continue;
            ConstantFieldref cfr = (ConstantFieldref)c;
            ConstantNameAndType cnat = (ConstantNameAndType)this.pool[cfr.getNameAndTypeIndex()];
            int cIndex = cfr.getClassIndex();
            ConstantClass cc = (ConstantClass)this.pool[cIndex];
            String cName = ((ConstantUtf8)this.pool[cc.getNameIndex()]).getValue();
            if (!cName.equals(searchClassname) || !(name = ((ConstantUtf8)this.pool[cnat.getNameIndex()]).getValue()).equals(searchFieldname) || !(typeSignature = ((ConstantUtf8)this.pool[cnat.getSignatureIndex()]).getValue()).equals(searchSignature)) continue;
            this.fieldCache.put(k, new Integer(i));
            return i;
        }
        return -1;
    }

    public int addNameAndType(String name, String signature) {
        int ret = this.lookupNameAndType(name, signature);
        if (ret != -1) {
            return ret;
        }
        this.adjustSize();
        int name_index = this.addUtf8(name);
        int signature_index = this.addUtf8(signature);
        ret = this.poolSize;
        this.pool[this.poolSize++] = new ConstantNameAndType(name_index, signature_index);
        return ret;
    }

    public int lookupNameAndType(String searchName, String searchTypeSignature) {
        for (int i = 1; i < this.poolSize; ++i) {
            String typeSignature;
            ConstantNameAndType cnat;
            String name;
            Constant c = this.pool[i];
            if (c == null || c.tag != 12 || !(name = ((ConstantUtf8)this.pool[(cnat = (ConstantNameAndType)c).getNameIndex()]).getValue()).equals(searchName) || !(typeSignature = ((ConstantUtf8)this.pool[cnat.getSignatureIndex()]).getValue()).equals(searchTypeSignature)) continue;
            return i;
        }
        return -1;
    }

    public int addFloat(float f) {
        int ret = this.lookupFloat(f);
        if (ret != -1) {
            return ret;
        }
        this.adjustSize();
        ret = this.poolSize;
        this.pool[this.poolSize++] = new ConstantFloat(f);
        return ret;
    }

    public int lookupFloat(float f) {
        int bits = Float.floatToIntBits(f);
        for (int i = 1; i < this.poolSize; ++i) {
            ConstantFloat cf;
            Constant c = this.pool[i];
            if (c == null || c.tag != 4 || Float.floatToIntBits((cf = (ConstantFloat)c).getValue().floatValue()) != bits) continue;
            return i;
        }
        return -1;
    }

    public int addDouble(double d) {
        int ret = this.lookupDouble(d);
        if (ret != -1) {
            return ret;
        }
        this.adjustSize();
        ret = this.poolSize;
        this.pool[this.poolSize] = new ConstantDouble(d);
        this.poolSize += 2;
        return ret;
    }

    public int lookupDouble(double d) {
        long bits = Double.doubleToLongBits(d);
        for (int i = 1; i < this.poolSize; ++i) {
            ConstantDouble cf;
            Constant c = this.pool[i];
            if (c == null || c.tag != 6 || Double.doubleToLongBits((cf = (ConstantDouble)c).getValue()) != bits) continue;
            return i;
        }
        return -1;
    }

    public int addLong(long l) {
        int ret = this.lookupLong(l);
        if (ret != -1) {
            return ret;
        }
        this.adjustSize();
        ret = this.poolSize;
        this.pool[this.poolSize] = new ConstantLong(l);
        this.poolSize += 2;
        return ret;
    }

    public int lookupString(String s) {
        for (int i = 1; i < this.poolSize; ++i) {
            ConstantString cs;
            ConstantUtf8 cu8;
            Constant c = this.pool[i];
            if (c == null || c.tag != 8 || !(cu8 = (ConstantUtf8)this.pool[(cs = (ConstantString)c).getStringIndex()]).getValue().equals(s)) continue;
            return i;
        }
        return -1;
    }

    public int addString(String str) {
        int ret = this.lookupString(str);
        if (ret != -1) {
            return ret;
        }
        int utf8 = this.addUtf8(str);
        this.adjustSize();
        ConstantString s = new ConstantString(utf8);
        ret = this.poolSize;
        this.pool[this.poolSize++] = s;
        return ret;
    }

    public int lookupLong(long l) {
        for (int i = 1; i < this.poolSize; ++i) {
            ConstantLong cf;
            Constant c = this.pool[i];
            if (c == null || c.tag != 5 || (cf = (ConstantLong)c).getValue() != l) continue;
            return i;
        }
        return -1;
    }

    public int addConstant(Constant c, ConstantPool cp) {
        Constant[] constants = cp.getConstantPool();
        switch (c.getTag()) {
            case 8: {
                ConstantString s = (ConstantString)c;
                ConstantUtf8 u8 = (ConstantUtf8)constants[s.getStringIndex()];
                return this.addString(u8.getValue());
            }
            case 7: {
                ConstantClass s = (ConstantClass)c;
                ConstantUtf8 u8 = (ConstantUtf8)constants[s.getNameIndex()];
                return this.addClass(u8.getValue());
            }
            case 12: {
                ConstantNameAndType n = (ConstantNameAndType)c;
                ConstantUtf8 u8 = (ConstantUtf8)constants[n.getNameIndex()];
                ConstantUtf8 u8_2 = (ConstantUtf8)constants[n.getSignatureIndex()];
                return this.addNameAndType(u8.getValue(), u8_2.getValue());
            }
            case 18: {
                ConstantInvokeDynamic cid = (ConstantInvokeDynamic)c;
                int index1 = cid.getBootstrapMethodAttrIndex();
                ConstantNameAndType cnat = (ConstantNameAndType)constants[cid.getNameAndTypeIndex()];
                ConstantUtf8 name = (ConstantUtf8)constants[cnat.getNameIndex()];
                ConstantUtf8 signature = (ConstantUtf8)constants[cnat.getSignatureIndex()];
                int index2 = this.addNameAndType(name.getValue(), signature.getValue());
                return this.addInvokeDynamic(index1, index2);
            }
            case 15: {
                ConstantMethodHandle cmh = (ConstantMethodHandle)c;
                return this.addMethodHandle(cmh.getReferenceKind(), this.addConstant(constants[cmh.getReferenceIndex()], cp));
            }
            case 1: {
                return this.addUtf8(((ConstantUtf8)c).getValue());
            }
            case 6: {
                return this.addDouble(((ConstantDouble)c).getValue());
            }
            case 4: {
                return this.addFloat(((ConstantFloat)c).getValue().floatValue());
            }
            case 5: {
                return this.addLong(((ConstantLong)c).getValue());
            }
            case 3: {
                return this.addInteger(((ConstantInteger)c).getValue());
            }
            case 16: {
                ConstantMethodType cmt = (ConstantMethodType)c;
                return this.addMethodType(this.addConstant(constants[cmt.getDescriptorIndex()], cp));
            }
            case 9: 
            case 10: 
            case 11: {
                ConstantCP m = (ConstantCP)c;
                ConstantClass clazz = (ConstantClass)constants[m.getClassIndex()];
                ConstantNameAndType n = (ConstantNameAndType)constants[m.getNameAndTypeIndex()];
                ConstantUtf8 u8 = (ConstantUtf8)constants[clazz.getNameIndex()];
                String class_name = u8.getValue().replace('/', '.');
                u8 = (ConstantUtf8)constants[n.getNameIndex()];
                String name = u8.getValue();
                u8 = (ConstantUtf8)constants[n.getSignatureIndex()];
                String signature = u8.getValue();
                switch (c.getTag()) {
                    case 11: {
                        return this.addInterfaceMethodref(class_name, name, signature);
                    }
                    case 10: {
                        return this.addMethodref(class_name, name, signature);
                    }
                    case 9: {
                        return this.addFieldref(class_name, name, signature);
                    }
                }
                throw new RuntimeException("Unknown constant type " + c);
            }
        }
        throw new RuntimeException("Unknown constant type " + c);
    }

    public int addMethodHandle(byte referenceKind, int referenceIndex) {
        this.adjustSize();
        int ret = this.poolSize;
        this.pool[this.poolSize++] = new ConstantMethodHandle(referenceKind, referenceIndex);
        return ret;
    }

    public int addMethodType(int descriptorIndex) {
        this.adjustSize();
        int ret = this.poolSize;
        this.pool[this.poolSize++] = new ConstantMethodType(descriptorIndex);
        return ret;
    }

    public int addMethodref(String class_name, String method_name, String signature) {
        int ret = this.lookupMethodref(class_name, method_name, signature);
        if (ret != -1) {
            return ret;
        }
        this.adjustSize();
        int name_and_type_index = this.addNameAndType(method_name, signature);
        int class_index = this.addClass(class_name);
        ret = this.poolSize;
        this.pool[this.poolSize++] = new ConstantMethodref(class_index, name_and_type_index);
        return ret;
    }

    public int addInvokeDynamic(int bootstrapMethodIndex, int constantNameAndTypeIndex) {
        this.adjustSize();
        int ret = this.poolSize;
        this.pool[this.poolSize++] = new ConstantInvokeDynamic(bootstrapMethodIndex, constantNameAndTypeIndex);
        return ret;
    }

    public int addInterfaceMethodref(String class_name, String method_name, String signature) {
        int ret = this.lookupInterfaceMethodref(class_name, method_name, signature);
        if (ret != -1) {
            return ret;
        }
        this.adjustSize();
        int class_index = this.addClass(class_name);
        int name_and_type_index = this.addNameAndType(method_name, signature);
        ret = this.poolSize;
        this.pool[this.poolSize++] = new ConstantInterfaceMethodref(class_index, name_and_type_index);
        return ret;
    }

    public int lookupInterfaceMethodref(String searchClassname, String searchMethodName, String searchSignature) {
        searchClassname = searchClassname.replace('.', '/');
        for (int i = 1; i < this.poolSize; ++i) {
            String typeSignature;
            ConstantNameAndType cnat;
            String name;
            ConstantInterfaceMethodref cfr;
            ConstantClass cc;
            String cName;
            Constant c = this.pool[i];
            if (c == null || c.tag != 11 || !(cName = ((ConstantUtf8)this.pool[(cc = (ConstantClass)this.pool[(cfr = (ConstantInterfaceMethodref)c).getClassIndex()]).getNameIndex()]).getValue()).equals(searchClassname) || !(name = ((ConstantUtf8)this.pool[(cnat = (ConstantNameAndType)this.pool[cfr.getNameAndTypeIndex()]).getNameIndex()]).getValue()).equals(searchMethodName) || !(typeSignature = ((ConstantUtf8)this.pool[cnat.getSignatureIndex()]).getValue()).equals(searchSignature)) continue;
            return i;
        }
        return -1;
    }

    public int lookupMethodref(String searchClassname, String searchMethodName, String searchSignature) {
        String key = new StringBuffer().append(searchClassname).append(searchMethodName).append(searchSignature).toString();
        Integer cached = this.methodCache.get(key);
        if (cached != null) {
            return cached;
        }
        searchClassname = searchClassname.replace('.', '/');
        for (int i = 1; i < this.poolSize; ++i) {
            String typeSignature;
            String name;
            Constant c = this.pool[i];
            if (c == null || c.tag != 10) continue;
            ConstantMethodref cfr = (ConstantMethodref)c;
            ConstantNameAndType cnat = (ConstantNameAndType)this.pool[cfr.getNameAndTypeIndex()];
            int cIndex = cfr.getClassIndex();
            ConstantClass cc = (ConstantClass)this.pool[cIndex];
            String cName = ((ConstantUtf8)this.pool[cc.getNameIndex()]).getValue();
            if (!cName.equals(searchClassname) || !(name = ((ConstantUtf8)this.pool[cnat.getNameIndex()]).getValue()).equals(searchMethodName) || !(typeSignature = ((ConstantUtf8)this.pool[cnat.getSignatureIndex()]).getValue()).equals(searchSignature)) continue;
            this.methodCache.put(key, new Integer(i));
            return i;
        }
        return -1;
    }

    public ConstantPool getFinalConstantPool() {
        Constant[] cs = new Constant[this.poolSize];
        System.arraycopy(this.pool, 0, cs, 0, this.poolSize);
        return new ConstantPool(cs);
    }

    public String getModuleName(int moduleIndex) {
        return this.getConstantModule(moduleIndex).getModuleName(this);
    }

    public String getPackageName(int packageIndex) {
        return this.getConstantPackage(packageIndex).getPackageName(this);
    }
}

