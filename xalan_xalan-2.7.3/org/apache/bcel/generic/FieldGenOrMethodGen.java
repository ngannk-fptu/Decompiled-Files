/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.generic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.bcel.classfile.AccessFlags;
import org.apache.bcel.classfile.Attribute;
import org.apache.bcel.generic.AnnotationEntryGen;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.NamedAndTyped;
import org.apache.bcel.generic.Type;

public abstract class FieldGenOrMethodGen
extends AccessFlags
implements NamedAndTyped,
Cloneable {
    @Deprecated
    protected String name;
    @Deprecated
    protected Type type;
    @Deprecated
    protected ConstantPoolGen cp;
    private final List<Attribute> attributeList = new ArrayList<Attribute>();
    private final List<AnnotationEntryGen> annotationList = new ArrayList<AnnotationEntryGen>();

    protected FieldGenOrMethodGen() {
    }

    protected FieldGenOrMethodGen(int accessFlags) {
        super(accessFlags);
    }

    protected void addAll(Attribute[] attrs) {
        Collections.addAll(this.attributeList, attrs);
    }

    public void addAnnotationEntry(AnnotationEntryGen ag) {
        this.annotationList.add(ag);
    }

    public void addAttribute(Attribute a) {
        this.attributeList.add(a);
    }

    public Object clone() {
        try {
            return super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new Error("Clone Not Supported");
        }
    }

    public AnnotationEntryGen[] getAnnotationEntries() {
        return this.annotationList.toArray(AnnotationEntryGen.EMPTY_ARRAY);
    }

    public Attribute[] getAttributes() {
        return this.attributeList.toArray(Attribute.EMPTY_ARRAY);
    }

    public ConstantPoolGen getConstantPool() {
        return this.cp;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public abstract String getSignature();

    @Override
    public Type getType() {
        return this.type;
    }

    public void removeAnnotationEntries() {
        this.annotationList.clear();
    }

    public void removeAnnotationEntry(AnnotationEntryGen ag) {
        this.annotationList.remove(ag);
    }

    public void removeAttribute(Attribute a) {
        this.attributeList.remove(a);
    }

    public void removeAttributes() {
        this.attributeList.clear();
    }

    public void setConstantPool(ConstantPoolGen cp) {
        this.cp = cp;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void setType(Type type) {
        if (type.getType() == 16) {
            throw new IllegalArgumentException("Type can not be " + type);
        }
        this.type = type;
    }
}

