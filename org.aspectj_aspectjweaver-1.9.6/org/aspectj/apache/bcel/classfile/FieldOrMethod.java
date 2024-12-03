/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.apache.bcel.classfile;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import org.aspectj.apache.bcel.classfile.Attribute;
import org.aspectj.apache.bcel.classfile.AttributeUtils;
import org.aspectj.apache.bcel.classfile.ConstantPool;
import org.aspectj.apache.bcel.classfile.ConstantUtf8;
import org.aspectj.apache.bcel.classfile.Modifiers;
import org.aspectj.apache.bcel.classfile.Node;
import org.aspectj.apache.bcel.classfile.Signature;
import org.aspectj.apache.bcel.classfile.annotation.AnnotationGen;
import org.aspectj.apache.bcel.classfile.annotation.RuntimeAnnos;

public abstract class FieldOrMethod
extends Modifiers
implements Node {
    protected int nameIndex;
    protected int signatureIndex;
    protected Attribute[] attributes;
    protected ConstantPool cpool;
    private String name;
    private String signature;
    private AnnotationGen[] annotations;
    private String signatureAttributeString = null;
    private boolean searchedForSignatureAttribute = false;

    protected FieldOrMethod() {
    }

    protected FieldOrMethod(FieldOrMethod c) {
        this(c.getModifiers(), c.getNameIndex(), c.getSignatureIndex(), c.getAttributes(), c.getConstantPool());
    }

    protected FieldOrMethod(DataInputStream file, ConstantPool cpool) throws IOException {
        this(file.readUnsignedShort(), file.readUnsignedShort(), file.readUnsignedShort(), null, cpool);
        this.attributes = AttributeUtils.readAttributes(file, cpool);
    }

    protected FieldOrMethod(int accessFlags, int nameIndex, int signatureIndex, Attribute[] attributes, ConstantPool cpool) {
        this.modifiers = accessFlags;
        this.nameIndex = nameIndex;
        this.signatureIndex = signatureIndex;
        this.cpool = cpool;
        this.attributes = attributes;
    }

    public void setAttributes(Attribute[] attributes) {
        this.attributes = attributes;
    }

    public final void dump(DataOutputStream file) throws IOException {
        file.writeShort(this.modifiers);
        file.writeShort(this.nameIndex);
        file.writeShort(this.signatureIndex);
        AttributeUtils.writeAttributes(this.attributes, file);
    }

    public final Attribute[] getAttributes() {
        return this.attributes;
    }

    public final ConstantPool getConstantPool() {
        return this.cpool;
    }

    public final int getNameIndex() {
        return this.nameIndex;
    }

    public final int getSignatureIndex() {
        return this.signatureIndex;
    }

    public final String getName() {
        if (this.name == null) {
            ConstantUtf8 c = (ConstantUtf8)this.cpool.getConstant(this.nameIndex, (byte)1);
            this.name = c.getValue();
        }
        return this.name;
    }

    public final String getSignature() {
        if (this.signature == null) {
            ConstantUtf8 c = (ConstantUtf8)this.cpool.getConstant(this.signatureIndex, (byte)1);
            this.signature = c.getValue();
        }
        return this.signature;
    }

    public final String getDeclaredSignature() {
        if (this.getGenericSignature() != null) {
            return this.getGenericSignature();
        }
        return this.getSignature();
    }

    public AnnotationGen[] getAnnotations() {
        if (this.annotations == null) {
            ArrayList<AnnotationGen> accumulatedAnnotations = new ArrayList<AnnotationGen>();
            for (int i = 0; i < this.attributes.length; ++i) {
                Attribute attribute = this.attributes[i];
                if (!(attribute instanceof RuntimeAnnos)) continue;
                RuntimeAnnos runtimeAnnotations = (RuntimeAnnos)attribute;
                accumulatedAnnotations.addAll(runtimeAnnotations.getAnnotations());
            }
            this.annotations = accumulatedAnnotations.size() == 0 ? AnnotationGen.NO_ANNOTATIONS : accumulatedAnnotations.toArray(new AnnotationGen[0]);
        }
        return this.annotations;
    }

    public final String getGenericSignature() {
        if (!this.searchedForSignatureAttribute) {
            Signature sig = AttributeUtils.getSignatureAttribute(this.attributes);
            this.signatureAttributeString = sig == null ? null : sig.getSignature();
            this.searchedForSignatureAttribute = true;
        }
        return this.signatureAttributeString;
    }
}

