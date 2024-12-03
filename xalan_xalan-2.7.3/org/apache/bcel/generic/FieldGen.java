/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.generic;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import org.apache.bcel.classfile.AnnotationEntry;
import org.apache.bcel.classfile.Annotations;
import org.apache.bcel.classfile.Attribute;
import org.apache.bcel.classfile.ConstantObject;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.ConstantValue;
import org.apache.bcel.classfile.Field;
import org.apache.bcel.classfile.Utility;
import org.apache.bcel.generic.AnnotationEntryGen;
import org.apache.bcel.generic.ClassGenException;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.FieldGenOrMethodGen;
import org.apache.bcel.generic.FieldObserver;
import org.apache.bcel.generic.ObjectType;
import org.apache.bcel.generic.Type;
import org.apache.bcel.util.BCELComparator;

public class FieldGen
extends FieldGenOrMethodGen {
    private static BCELComparator bcelComparator = new BCELComparator(){

        @Override
        public boolean equals(Object o1, Object o2) {
            FieldGen THIS = (FieldGen)o1;
            FieldGen THAT = (FieldGen)o2;
            return Objects.equals(THIS.getName(), THAT.getName()) && Objects.equals(THIS.getSignature(), THAT.getSignature());
        }

        @Override
        public int hashCode(Object o) {
            FieldGen THIS = (FieldGen)o;
            return THIS.getSignature().hashCode() ^ THIS.getName().hashCode();
        }
    };
    private Object value;
    private List<FieldObserver> observers;

    public static BCELComparator getComparator() {
        return bcelComparator;
    }

    public static void setComparator(BCELComparator comparator) {
        bcelComparator = comparator;
    }

    public FieldGen(Field field, ConstantPoolGen cp) {
        this(field.getAccessFlags(), Type.getType(field.getSignature()), field.getName(), cp);
        Attribute[] attrs;
        for (Attribute attr : attrs = field.getAttributes()) {
            if (attr instanceof ConstantValue) {
                this.setValue(((ConstantValue)attr).getConstantValueIndex());
                continue;
            }
            if (attr instanceof Annotations) {
                Annotations runtimeAnnotations = (Annotations)attr;
                runtimeAnnotations.forEach(element -> this.addAnnotationEntry(new AnnotationEntryGen((AnnotationEntry)element, cp, false)));
                continue;
            }
            this.addAttribute(attr);
        }
    }

    public FieldGen(int accessFlags, Type type, String name, ConstantPoolGen cp) {
        super(accessFlags);
        this.setType(type);
        this.setName(name);
        this.setConstantPool(cp);
    }

    private void addAnnotationsAsAttribute(ConstantPoolGen cp) {
        Stream.of(AnnotationEntryGen.getAnnotationAttributes(cp, super.getAnnotationEntries())).forEach(this::addAttribute);
    }

    private int addConstant() {
        switch (super.getType().getType()) {
            case 4: 
            case 5: 
            case 8: 
            case 9: 
            case 10: {
                return super.getConstantPool().addInteger((Integer)this.value);
            }
            case 6: {
                return super.getConstantPool().addFloat(((Float)this.value).floatValue());
            }
            case 7: {
                return super.getConstantPool().addDouble((Double)this.value);
            }
            case 11: {
                return super.getConstantPool().addLong((Long)this.value);
            }
            case 14: {
                return super.getConstantPool().addString((String)this.value);
            }
        }
        throw new IllegalStateException("Unhandled : " + super.getType().getType());
    }

    public void addObserver(FieldObserver o) {
        if (this.observers == null) {
            this.observers = new ArrayList<FieldObserver>();
        }
        this.observers.add(o);
    }

    public void cancelInitValue() {
        this.value = null;
    }

    private void checkType(Type atype) {
        Type superType = super.getType();
        if (superType == null) {
            throw new ClassGenException("You haven't defined the type of the field yet");
        }
        if (!this.isFinal()) {
            throw new ClassGenException("Only final fields may have an initial value!");
        }
        if (!superType.equals(atype)) {
            throw new ClassGenException("Types are not compatible: " + superType + " vs. " + atype);
        }
    }

    public FieldGen copy(ConstantPoolGen cp) {
        FieldGen fg = (FieldGen)this.clone();
        fg.setConstantPool(cp);
        return fg;
    }

    public boolean equals(Object obj) {
        return bcelComparator.equals(this, obj);
    }

    public Field getField() {
        String signature = this.getSignature();
        int nameIndex = super.getConstantPool().addUtf8(super.getName());
        int signatureIndex = super.getConstantPool().addUtf8(signature);
        if (this.value != null) {
            this.checkType(super.getType());
            int index = this.addConstant();
            this.addAttribute(new ConstantValue(super.getConstantPool().addUtf8("ConstantValue"), 2, index, super.getConstantPool().getConstantPool()));
        }
        this.addAnnotationsAsAttribute(super.getConstantPool());
        return new Field(super.getAccessFlags(), nameIndex, signatureIndex, this.getAttributes(), super.getConstantPool().getConstantPool());
    }

    public String getInitValue() {
        if (this.value != null) {
            return this.value.toString();
        }
        return null;
    }

    @Override
    public String getSignature() {
        return super.getType().getSignature();
    }

    public int hashCode() {
        return bcelComparator.hashCode(this);
    }

    public void removeObserver(FieldObserver o) {
        if (this.observers != null) {
            this.observers.remove(o);
        }
    }

    public void setInitValue(boolean b) {
        this.checkType(Type.BOOLEAN);
        if (b) {
            this.value = 1;
        }
    }

    public void setInitValue(byte b) {
        this.checkType(Type.BYTE);
        if (b != 0) {
            this.value = (int)b;
        }
    }

    public void setInitValue(char c) {
        this.checkType(Type.CHAR);
        if (c != '\u0000') {
            this.value = (int)c;
        }
    }

    public void setInitValue(double d) {
        this.checkType(Type.DOUBLE);
        if (d != 0.0) {
            this.value = d;
        }
    }

    public void setInitValue(float f) {
        this.checkType(Type.FLOAT);
        if ((double)f != 0.0) {
            this.value = Float.valueOf(f);
        }
    }

    public void setInitValue(int i) {
        this.checkType(Type.INT);
        if (i != 0) {
            this.value = i;
        }
    }

    public void setInitValue(long l) {
        this.checkType(Type.LONG);
        if (l != 0L) {
            this.value = l;
        }
    }

    public void setInitValue(short s) {
        this.checkType(Type.SHORT);
        if (s != 0) {
            this.value = (int)s;
        }
    }

    public void setInitValue(String str) {
        this.checkType(ObjectType.getInstance("java.lang.String"));
        if (str != null) {
            this.value = str;
        }
    }

    private void setValue(int index) {
        ConstantPool cp = super.getConstantPool().getConstantPool();
        Object c = cp.getConstant(index);
        this.value = ((ConstantObject)c).getConstantValue(cp);
    }

    public final String toString() {
        String access = Utility.accessToString(super.getAccessFlags());
        access = access.isEmpty() ? "" : access + " ";
        String signature = super.getType().toString();
        String name = this.getName();
        StringBuilder buf = new StringBuilder(32);
        buf.append(access).append(signature).append(" ").append(name);
        String value = this.getInitValue();
        if (value != null) {
            buf.append(" = ").append(value);
        }
        return buf.toString();
    }

    public void update() {
        if (this.observers != null) {
            for (FieldObserver observer : this.observers) {
                observer.notify(this);
            }
        }
    }
}

