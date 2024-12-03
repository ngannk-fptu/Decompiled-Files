/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.apache.bcel.generic;

import java.util.List;
import org.aspectj.apache.bcel.classfile.Attribute;
import org.aspectj.apache.bcel.classfile.Constant;
import org.aspectj.apache.bcel.classfile.ConstantDouble;
import org.aspectj.apache.bcel.classfile.ConstantFloat;
import org.aspectj.apache.bcel.classfile.ConstantInteger;
import org.aspectj.apache.bcel.classfile.ConstantLong;
import org.aspectj.apache.bcel.classfile.ConstantObject;
import org.aspectj.apache.bcel.classfile.ConstantPool;
import org.aspectj.apache.bcel.classfile.ConstantString;
import org.aspectj.apache.bcel.classfile.ConstantValue;
import org.aspectj.apache.bcel.classfile.Field;
import org.aspectj.apache.bcel.classfile.Utility;
import org.aspectj.apache.bcel.classfile.annotation.AnnotationGen;
import org.aspectj.apache.bcel.classfile.annotation.RuntimeAnnos;
import org.aspectj.apache.bcel.generic.ClassGenException;
import org.aspectj.apache.bcel.generic.FieldGenOrMethodGen;
import org.aspectj.apache.bcel.generic.Type;

public class FieldGen
extends FieldGenOrMethodGen {
    private Object value = null;

    public FieldGen(int modifiers, Type type, String name, ConstantPool cpool) {
        this.setModifiers(modifiers);
        this.setType(type);
        this.setName(name);
        this.setConstantPool(cpool);
    }

    public FieldGen(Field field, ConstantPool cp) {
        this(field.getModifiers(), Type.getType(field.getSignature()), field.getName(), cp);
        Attribute[] attrs = field.getAttributes();
        for (int i = 0; i < attrs.length; ++i) {
            if (attrs[i] instanceof ConstantValue) {
                this.setValue(((ConstantValue)attrs[i]).getConstantValueIndex());
                continue;
            }
            if (attrs[i] instanceof RuntimeAnnos) {
                RuntimeAnnos runtimeAnnotations = (RuntimeAnnos)attrs[i];
                List<AnnotationGen> l = runtimeAnnotations.getAnnotations();
                for (AnnotationGen element : l) {
                    this.addAnnotation(new AnnotationGen(element, cp, false));
                }
                continue;
            }
            this.addAttribute(attrs[i]);
        }
    }

    public void setValue(int index) {
        ConstantPool cp = this.cp;
        Constant c = cp.getConstant(index);
        this.value = c instanceof ConstantInteger ? Integer.valueOf(((ConstantInteger)c).getIntValue()) : (c instanceof ConstantFloat ? ((ConstantFloat)c).getValue() : (c instanceof ConstantDouble ? ((ConstantDouble)c).getValue() : (c instanceof ConstantLong ? ((ConstantLong)c).getValue() : (c instanceof ConstantString ? ((ConstantString)c).getString(cp) : ((ConstantObject)((Object)c)).getConstantValue(cp)))));
    }

    public void setValue(String constantString) {
        this.value = constantString;
    }

    public void wipeValue() {
        this.value = null;
    }

    private void checkType(Type atype) {
        if (this.type == null) {
            throw new ClassGenException("You haven't defined the type of the field yet");
        }
        if (!this.isFinal()) {
            throw new ClassGenException("Only final fields may have an initial value!");
        }
        if (!this.type.equals(atype)) {
            throw new ClassGenException("Types are not compatible: " + this.type + " vs. " + atype);
        }
    }

    public Field getField() {
        String signature = this.getSignature();
        int nameIndex = this.cp.addUtf8(this.name);
        int signatureIndex = this.cp.addUtf8(signature);
        if (this.value != null) {
            this.checkType(this.type);
            int index = this.addConstant();
            this.addAttribute(new ConstantValue(this.cp.addUtf8("ConstantValue"), 2, index, this.cp));
        }
        this.addAnnotationsAsAttribute(this.cp);
        return new Field(this.modifiers, nameIndex, signatureIndex, this.getAttributesImmutable(), this.cp);
    }

    private int addConstant() {
        switch (this.type.getType()) {
            case 4: 
            case 5: 
            case 8: 
            case 9: 
            case 10: {
                return this.cp.addInteger((Integer)this.value);
            }
            case 6: {
                return this.cp.addFloat(((Float)this.value).floatValue());
            }
            case 7: {
                return this.cp.addDouble((Double)this.value);
            }
            case 11: {
                return this.cp.addLong((Long)this.value);
            }
            case 14: {
                return this.cp.addString((String)this.value);
            }
        }
        throw new RuntimeException("Oops: Unhandled : " + this.type.getType());
    }

    @Override
    public String getSignature() {
        return this.type.getSignature();
    }

    public String getInitialValue() {
        return this.value == null ? null : this.value.toString();
    }

    public void setInitialStringValue(String value) {
        this.value = value;
    }

    public final String toString() {
        String access = Utility.accessToString(this.modifiers);
        access = access.equals("") ? "" : access + " ";
        String signature = this.type.toString();
        String name = this.getName();
        StringBuffer buf = new StringBuffer(access).append(signature).append(" ").append(name);
        String value = this.getInitialValue();
        if (value != null) {
            buf.append(" = ").append(value);
        }
        return buf.toString();
    }
}

