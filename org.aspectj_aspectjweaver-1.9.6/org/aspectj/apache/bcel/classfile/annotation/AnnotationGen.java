/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.apache.bcel.classfile.annotation;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.aspectj.apache.bcel.classfile.ConstantPool;
import org.aspectj.apache.bcel.classfile.ConstantUtf8;
import org.aspectj.apache.bcel.classfile.Utility;
import org.aspectj.apache.bcel.classfile.annotation.ElementValue;
import org.aspectj.apache.bcel.classfile.annotation.NameValuePair;
import org.aspectj.apache.bcel.generic.ObjectType;

public class AnnotationGen {
    public static final AnnotationGen[] NO_ANNOTATIONS = new AnnotationGen[0];
    private int typeIndex;
    private List<NameValuePair> pairs = Collections.emptyList();
    private ConstantPool cpool;
    private boolean isRuntimeVisible = false;

    public AnnotationGen(AnnotationGen a, ConstantPool cpool, boolean copyPoolEntries) {
        this.cpool = cpool;
        this.typeIndex = copyPoolEntries ? cpool.addUtf8(a.getTypeSignature()) : a.getTypeIndex();
        this.isRuntimeVisible = a.isRuntimeVisible();
        this.pairs = this.copyValues(a.getValues(), cpool, copyPoolEntries);
    }

    private List<NameValuePair> copyValues(List<NameValuePair> in, ConstantPool cpool, boolean copyPoolEntries) {
        ArrayList<NameValuePair> out = new ArrayList<NameValuePair>();
        for (NameValuePair nvp : in) {
            out.add(new NameValuePair(nvp, cpool, copyPoolEntries));
        }
        return out;
    }

    private AnnotationGen(ConstantPool cpool) {
        this.cpool = cpool;
    }

    public AnnotationGen(ObjectType type, List<NameValuePair> pairs, boolean runtimeVisible, ConstantPool cpool) {
        this.cpool = cpool;
        if (type != null) {
            this.typeIndex = cpool.addUtf8(type.getSignature());
        }
        this.pairs = pairs;
        this.isRuntimeVisible = runtimeVisible;
    }

    public static AnnotationGen read(DataInputStream dis, ConstantPool cpool, boolean b) throws IOException {
        AnnotationGen a = new AnnotationGen(cpool);
        a.typeIndex = dis.readUnsignedShort();
        int elemValuePairCount = dis.readUnsignedShort();
        for (int i = 0; i < elemValuePairCount; ++i) {
            int nidx = dis.readUnsignedShort();
            a.addElementNameValuePair(new NameValuePair(nidx, ElementValue.readElementValue(dis, cpool), cpool));
        }
        a.isRuntimeVisible(b);
        return a;
    }

    public void dump(DataOutputStream dos) throws IOException {
        dos.writeShort(this.typeIndex);
        dos.writeShort(this.pairs.size());
        for (int i = 0; i < this.pairs.size(); ++i) {
            NameValuePair envp = this.pairs.get(i);
            envp.dump(dos);
        }
    }

    public void addElementNameValuePair(NameValuePair evp) {
        if (this.pairs == Collections.EMPTY_LIST) {
            this.pairs = new ArrayList<NameValuePair>();
        }
        this.pairs.add(evp);
    }

    public int getTypeIndex() {
        return this.typeIndex;
    }

    public String getTypeSignature() {
        ConstantUtf8 utf8 = (ConstantUtf8)this.cpool.getConstant(this.typeIndex);
        return utf8.getValue();
    }

    public String getTypeName() {
        return Utility.signatureToString(this.getTypeSignature());
    }

    public List<NameValuePair> getValues() {
        return this.pairs;
    }

    public String toString() {
        StringBuffer s = new StringBuffer();
        s.append("AnnotationGen:[" + this.getTypeName() + " #" + this.pairs.size() + " {");
        for (int i = 0; i < this.pairs.size(); ++i) {
            s.append(this.pairs.get(i));
            if (i + 1 >= this.pairs.size()) continue;
            s.append(",");
        }
        s.append("}]");
        return s.toString();
    }

    public String toShortString() {
        StringBuffer s = new StringBuffer();
        s.append("@").append(this.getTypeName());
        if (this.pairs.size() != 0) {
            s.append("(");
            for (int i = 0; i < this.pairs.size(); ++i) {
                s.append(this.pairs.get(i));
                if (i + 1 >= this.pairs.size()) continue;
                s.append(",");
            }
            s.append(")");
        }
        return s.toString();
    }

    private void isRuntimeVisible(boolean b) {
        this.isRuntimeVisible = b;
    }

    public boolean isRuntimeVisible() {
        return this.isRuntimeVisible;
    }

    public boolean hasNameValuePair(String name, String value) {
        for (NameValuePair pair : this.pairs) {
            if (!pair.getNameString().equals(name) || !pair.getValue().stringifyValue().equals(value)) continue;
            return true;
        }
        return false;
    }

    public boolean hasNamedValue(String name) {
        for (NameValuePair pair : this.pairs) {
            if (!pair.getNameString().equals(name)) continue;
            return true;
        }
        return false;
    }
}

