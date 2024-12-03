/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.generic;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.bcel.classfile.AnnotationEntry;
import org.apache.bcel.classfile.Annotations;
import org.apache.bcel.classfile.Attribute;
import org.apache.bcel.classfile.ConstantUtf8;
import org.apache.bcel.classfile.ElementValuePair;
import org.apache.bcel.classfile.ParameterAnnotations;
import org.apache.bcel.classfile.RuntimeInvisibleAnnotations;
import org.apache.bcel.classfile.RuntimeInvisibleParameterAnnotations;
import org.apache.bcel.classfile.RuntimeVisibleAnnotations;
import org.apache.bcel.classfile.RuntimeVisibleParameterAnnotations;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.ElementValueGen;
import org.apache.bcel.generic.ElementValuePairGen;
import org.apache.bcel.generic.ObjectType;

public class AnnotationEntryGen {
    static final AnnotationEntryGen[] EMPTY_ARRAY = new AnnotationEntryGen[0];
    private int typeIndex;
    private List<ElementValuePairGen> evs;
    private final ConstantPoolGen cpool;
    private boolean isRuntimeVisible;

    static Attribute[] getAnnotationAttributes(ConstantPoolGen cp, AnnotationEntryGen[] annotationEntryGens) {
        if (annotationEntryGens.length == 0) {
            return Attribute.EMPTY_ARRAY;
        }
        try {
            int countVisible = 0;
            int countInvisible = 0;
            for (AnnotationEntryGen annotationEntryGen : annotationEntryGens) {
                if (annotationEntryGen.isRuntimeVisible()) {
                    ++countVisible;
                    continue;
                }
                ++countInvisible;
            }
            ByteArrayOutputStream rvaBytes = new ByteArrayOutputStream();
            ByteArrayOutputStream riaBytes = new ByteArrayOutputStream();
            Throwable throwable = null;
            try (DataOutputStream rvaDos = new DataOutputStream(rvaBytes);
                 DataOutputStream riaDos22 = new DataOutputStream(riaBytes);){
                rvaDos.writeShort(countVisible);
                riaDos22.writeShort(countInvisible);
                for (AnnotationEntryGen a3 : annotationEntryGens) {
                    if (a3.isRuntimeVisible()) {
                        a3.dump(rvaDos);
                        continue;
                    }
                    a3.dump(riaDos22);
                }
            }
            catch (Throwable riaDos22) {
                Throwable throwable2 = riaDos22;
                throw riaDos22;
            }
            byte[] rvaData = rvaBytes.toByteArray();
            byte[] byArray = riaBytes.toByteArray();
            int rvaIndex = -1;
            int riaIndex = -1;
            if (rvaData.length > 2) {
                rvaIndex = cp.addUtf8("RuntimeVisibleAnnotations");
            }
            if (byArray.length > 2) {
                riaIndex = cp.addUtf8("RuntimeInvisibleAnnotations");
            }
            ArrayList<Annotations> newAttributes = new ArrayList<Annotations>();
            if (rvaData.length > 2) {
                newAttributes.add(new RuntimeVisibleAnnotations(rvaIndex, rvaData.length, new DataInputStream(new ByteArrayInputStream(rvaData)), cp.getConstantPool()));
            }
            if (byArray.length > 2) {
                newAttributes.add(new RuntimeInvisibleAnnotations(riaIndex, byArray.length, new DataInputStream(new ByteArrayInputStream(byArray)), cp.getConstantPool()));
            }
            return newAttributes.toArray(Attribute.EMPTY_ARRAY);
        }
        catch (IOException e) {
            System.err.println("IOException whilst processing annotations");
            e.printStackTrace();
            return null;
        }
    }

    static Attribute[] getParameterAnnotationAttributes(ConstantPoolGen cp, List<AnnotationEntryGen>[] vec) {
        int[] visCount = new int[vec.length];
        int totalVisCount = 0;
        int[] invisCount = new int[vec.length];
        int totalInvisCount = 0;
        try {
            int n;
            for (int i = 0; i < vec.length; ++i) {
                if (vec[i] == null) continue;
                for (AnnotationEntryGen annotationEntryGen : vec[i]) {
                    if (annotationEntryGen.isRuntimeVisible()) {
                        int n2 = i;
                        visCount[n2] = visCount[n2] + 1;
                        ++totalVisCount;
                        continue;
                    }
                    int n3 = i;
                    invisCount[n3] = invisCount[n3] + 1;
                    ++totalInvisCount;
                }
            }
            ByteArrayOutputStream rvaBytes = new ByteArrayOutputStream();
            Throwable throwable = null;
            try (DataOutputStream rvaDos = new DataOutputStream(rvaBytes);){
                rvaDos.writeByte(vec.length);
                for (int i = 0; i < vec.length; ++i) {
                    rvaDos.writeShort(visCount[i]);
                    if (visCount[i] <= 0) continue;
                    for (AnnotationEntryGen annotationEntryGen : vec[i]) {
                        if (!annotationEntryGen.isRuntimeVisible()) continue;
                        annotationEntryGen.dump(rvaDos);
                    }
                }
            }
            catch (Throwable i) {
                Throwable throwable2 = i;
                throw i;
            }
            ByteArrayOutputStream riaBytes = new ByteArrayOutputStream();
            try (DataOutputStream dataOutputStream = new DataOutputStream(riaBytes);){
                dataOutputStream.writeByte(vec.length);
                for (int i = 0; i < vec.length; ++i) {
                    dataOutputStream.writeShort(invisCount[i]);
                    if (invisCount[i] <= 0) continue;
                    for (AnnotationEntryGen element4 : vec[i]) {
                        if (element4.isRuntimeVisible()) continue;
                        element4.dump(dataOutputStream);
                    }
                }
            }
            byte[] byArray = rvaBytes.toByteArray();
            byte[] riaData = riaBytes.toByteArray();
            int rvaIndex = -1;
            int n4 = -1;
            if (totalVisCount > 0) {
                rvaIndex = cp.addUtf8("RuntimeVisibleParameterAnnotations");
            }
            if (totalInvisCount > 0) {
                n = cp.addUtf8("RuntimeInvisibleParameterAnnotations");
            }
            ArrayList<ParameterAnnotations> newAttributes = new ArrayList<ParameterAnnotations>();
            if (totalVisCount > 0) {
                newAttributes.add(new RuntimeVisibleParameterAnnotations(rvaIndex, byArray.length, new DataInputStream(new ByteArrayInputStream(byArray)), cp.getConstantPool()));
            }
            if (totalInvisCount > 0) {
                newAttributes.add(new RuntimeInvisibleParameterAnnotations(n, riaData.length, new DataInputStream(new ByteArrayInputStream(riaData)), cp.getConstantPool()));
            }
            return newAttributes.toArray(Attribute.EMPTY_ARRAY);
        }
        catch (IOException e) {
            System.err.println("IOException whilst processing parameter annotations");
            e.printStackTrace();
            return null;
        }
    }

    public static AnnotationEntryGen read(DataInput dis, ConstantPoolGen cpool, boolean b) throws IOException {
        AnnotationEntryGen a = new AnnotationEntryGen(cpool);
        a.typeIndex = dis.readUnsignedShort();
        int elemValuePairCount = dis.readUnsignedShort();
        for (int i = 0; i < elemValuePairCount; ++i) {
            int nidx = dis.readUnsignedShort();
            a.addElementNameValuePair(new ElementValuePairGen(nidx, ElementValueGen.readElementValue(dis, cpool), cpool));
        }
        a.isRuntimeVisible(b);
        return a;
    }

    public AnnotationEntryGen(AnnotationEntry a, ConstantPoolGen cpool, boolean copyPoolEntries) {
        this.cpool = cpool;
        this.typeIndex = copyPoolEntries ? cpool.addUtf8(a.getAnnotationType()) : a.getAnnotationTypeIndex();
        this.isRuntimeVisible = a.isRuntimeVisible();
        this.evs = this.copyValues(a.getElementValuePairs(), cpool, copyPoolEntries);
    }

    private AnnotationEntryGen(ConstantPoolGen cpool) {
        this.cpool = cpool;
    }

    public AnnotationEntryGen(ObjectType type, List<ElementValuePairGen> elements, boolean vis, ConstantPoolGen cpool) {
        this.cpool = cpool;
        this.typeIndex = cpool.addUtf8(type.getSignature());
        this.evs = elements;
        this.isRuntimeVisible = vis;
    }

    public void addElementNameValuePair(ElementValuePairGen evp) {
        if (this.evs == null) {
            this.evs = new ArrayList<ElementValuePairGen>();
        }
        this.evs.add(evp);
    }

    private List<ElementValuePairGen> copyValues(ElementValuePair[] in, ConstantPoolGen cpool, boolean copyPoolEntries) {
        ArrayList<ElementValuePairGen> out = new ArrayList<ElementValuePairGen>();
        for (ElementValuePair nvp : in) {
            out.add(new ElementValuePairGen(nvp, cpool, copyPoolEntries));
        }
        return out;
    }

    public void dump(DataOutputStream dos) throws IOException {
        dos.writeShort(this.typeIndex);
        dos.writeShort(this.evs.size());
        for (ElementValuePairGen envp : this.evs) {
            envp.dump(dos);
        }
    }

    public AnnotationEntry getAnnotation() {
        AnnotationEntry a = new AnnotationEntry(this.typeIndex, this.cpool.getConstantPool(), this.isRuntimeVisible);
        for (ElementValuePairGen element : this.evs) {
            a.addElementNameValuePair(element.getElementNameValuePair());
        }
        return a;
    }

    public int getTypeIndex() {
        return this.typeIndex;
    }

    public final String getTypeName() {
        return this.getTypeSignature();
    }

    public final String getTypeSignature() {
        ConstantUtf8 utf8 = (ConstantUtf8)this.cpool.getConstant(this.typeIndex);
        return utf8.getBytes();
    }

    public List<ElementValuePairGen> getValues() {
        return this.evs;
    }

    public boolean isRuntimeVisible() {
        return this.isRuntimeVisible;
    }

    private void isRuntimeVisible(boolean b) {
        this.isRuntimeVisible = b;
    }

    public String toShortString() {
        StringBuilder s = new StringBuilder();
        s.append("@").append(this.getTypeName()).append("(");
        for (int i = 0; i < this.evs.size(); ++i) {
            s.append(this.evs.get(i));
            if (i + 1 >= this.evs.size()) continue;
            s.append(",");
        }
        s.append(")");
        return s.toString();
    }

    public String toString() {
        StringBuilder s = new StringBuilder(32);
        s.append("AnnotationGen:[").append(this.getTypeName()).append(" #").append(this.evs.size()).append(" {");
        for (int i = 0; i < this.evs.size(); ++i) {
            s.append(this.evs.get(i));
            if (i + 1 >= this.evs.size()) continue;
            s.append(",");
        }
        s.append("}]");
        return s.toString();
    }
}

