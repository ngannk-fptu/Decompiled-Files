/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.apache.bcel.generic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.aspectj.apache.bcel.classfile.Attribute;
import org.aspectj.apache.bcel.classfile.ConstantPool;
import org.aspectj.apache.bcel.classfile.Modifiers;
import org.aspectj.apache.bcel.classfile.Utility;
import org.aspectj.apache.bcel.classfile.annotation.AnnotationGen;
import org.aspectj.apache.bcel.classfile.annotation.RuntimeAnnos;
import org.aspectj.apache.bcel.generic.Type;

public abstract class FieldGenOrMethodGen
extends Modifiers {
    protected String name;
    protected Type type;
    protected ConstantPool cp;
    private ArrayList<Attribute> attributeList = new ArrayList();
    protected ArrayList<AnnotationGen> annotationList = new ArrayList();

    protected FieldGenOrMethodGen() {
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Type getType() {
        return this.type;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ConstantPool getConstantPool() {
        return this.cp;
    }

    public void setConstantPool(ConstantPool cp) {
        this.cp = cp;
    }

    public void addAttribute(Attribute a) {
        this.attributeList.add(a);
    }

    public void removeAttribute(Attribute a) {
        this.attributeList.remove(a);
    }

    public void removeAttributes() {
        this.attributeList.clear();
    }

    public List<AnnotationGen> getAnnotations() {
        return this.annotationList;
    }

    public void addAnnotation(AnnotationGen ag) {
        this.annotationList.add(ag);
    }

    public void removeAnnotation(AnnotationGen ag) {
        this.annotationList.remove(ag);
    }

    public void removeAnnotations() {
        this.annotationList.clear();
    }

    public List<Attribute> getAttributes() {
        return this.attributeList;
    }

    public Attribute[] getAttributesImmutable() {
        Attribute[] attributes = new Attribute[this.attributeList.size()];
        this.attributeList.toArray(attributes);
        return attributes;
    }

    protected void addAnnotationsAsAttribute(ConstantPool cp) {
        Collection<RuntimeAnnos> attrs = Utility.getAnnotationAttributes(cp, this.annotationList);
        if (attrs != null) {
            for (Attribute attribute : attrs) {
                this.addAttribute(attribute);
            }
        }
    }

    public abstract String getSignature();
}

