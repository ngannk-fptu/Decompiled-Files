/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.classfile;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;
import org.apache.bcel.classfile.AccessFlags;
import org.apache.bcel.classfile.AnnotationEntry;
import org.apache.bcel.classfile.Attribute;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.Node;
import org.apache.bcel.classfile.Signature;

public abstract class FieldOrMethod
extends AccessFlags
implements Cloneable,
Node {
    @Deprecated
    protected int name_index;
    @Deprecated
    protected int signature_index;
    @Deprecated
    protected Attribute[] attributes;
    @Deprecated
    protected int attributes_count;
    private AnnotationEntry[] annotationEntries;
    @Deprecated
    protected ConstantPool constant_pool;
    private String signatureAttributeString;
    private boolean searchedForSignatureAttribute;

    FieldOrMethod() {
    }

    protected FieldOrMethod(DataInput file, ConstantPool constantPool) throws IOException {
        this(file.readUnsignedShort(), file.readUnsignedShort(), file.readUnsignedShort(), null, constantPool);
        int attributesCount = file.readUnsignedShort();
        this.attributes = new Attribute[attributesCount];
        for (int i = 0; i < attributesCount; ++i) {
            this.attributes[i] = Attribute.readAttribute(file, constantPool);
        }
        this.attributes_count = attributesCount;
    }

    @Deprecated
    protected FieldOrMethod(DataInputStream file, ConstantPool constantPool) throws IOException {
        this((DataInput)file, constantPool);
    }

    protected FieldOrMethod(FieldOrMethod c) {
        this(c.getAccessFlags(), c.getNameIndex(), c.getSignatureIndex(), c.getAttributes(), c.getConstantPool());
    }

    protected FieldOrMethod(int accessFlags, int nameIndex, int signatureIndex, Attribute[] attributes, ConstantPool constantPool) {
        super(accessFlags);
        this.name_index = nameIndex;
        this.signature_index = signatureIndex;
        this.constant_pool = constantPool;
        this.setAttributes(attributes);
    }

    protected FieldOrMethod copy_(ConstantPool constantPool) {
        try {
            FieldOrMethod c = (FieldOrMethod)this.clone();
            c.constant_pool = constantPool;
            c.attributes = new Attribute[this.attributes.length];
            c.attributes_count = this.attributes_count;
            Arrays.setAll(c.attributes, i -> this.attributes[i].copy(constantPool));
            return c;
        }
        catch (CloneNotSupportedException e) {
            throw new IllegalStateException(e);
        }
    }

    public final void dump(DataOutputStream file) throws IOException {
        file.writeShort(super.getAccessFlags());
        file.writeShort(this.name_index);
        file.writeShort(this.signature_index);
        file.writeShort(this.attributes_count);
        if (this.attributes != null) {
            for (Attribute attribute : this.attributes) {
                attribute.dump(file);
            }
        }
    }

    public AnnotationEntry[] getAnnotationEntries() {
        if (this.annotationEntries == null) {
            this.annotationEntries = AnnotationEntry.createAnnotationEntries(this.getAttributes());
        }
        return this.annotationEntries;
    }

    public final Attribute[] getAttributes() {
        return this.attributes;
    }

    public final ConstantPool getConstantPool() {
        return this.constant_pool;
    }

    public final String getGenericSignature() {
        if (!this.searchedForSignatureAttribute) {
            boolean found = false;
            for (int i = 0; !found && i < this.attributes.length; ++i) {
                if (!(this.attributes[i] instanceof Signature)) continue;
                this.signatureAttributeString = ((Signature)this.attributes[i]).getSignature();
                found = true;
            }
            this.searchedForSignatureAttribute = true;
        }
        return this.signatureAttributeString;
    }

    public final String getName() {
        return this.constant_pool.getConstantUtf8(this.name_index).getBytes();
    }

    public final int getNameIndex() {
        return this.name_index;
    }

    public final String getSignature() {
        return this.constant_pool.getConstantUtf8(this.signature_index).getBytes();
    }

    public final int getSignatureIndex() {
        return this.signature_index;
    }

    public final void setAttributes(Attribute[] attributes) {
        this.attributes = attributes;
        this.attributes_count = attributes != null ? attributes.length : 0;
    }

    public final void setConstantPool(ConstantPool constantPool) {
        this.constant_pool = constantPool;
    }

    public final void setNameIndex(int nameIndex) {
        this.name_index = nameIndex;
    }

    public final void setSignatureIndex(int signatureIndex) {
        this.signature_index = signatureIndex;
    }
}

