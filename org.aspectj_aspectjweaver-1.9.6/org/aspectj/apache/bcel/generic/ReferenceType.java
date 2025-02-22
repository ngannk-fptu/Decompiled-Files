/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.apache.bcel.generic;

import org.aspectj.apache.bcel.Constants;
import org.aspectj.apache.bcel.Repository;
import org.aspectj.apache.bcel.classfile.JavaClass;
import org.aspectj.apache.bcel.generic.ArrayType;
import org.aspectj.apache.bcel.generic.BasicType;
import org.aspectj.apache.bcel.generic.ObjectType;
import org.aspectj.apache.bcel.generic.Type;

public abstract class ReferenceType
extends Type {
    protected ReferenceType(byte t, String s) {
        super(t, s);
    }

    ReferenceType() {
        super((byte)14, "<null object>");
    }

    public boolean isCastableTo(Type t) {
        if (this.equals(Type.NULL)) {
            return true;
        }
        return this.isAssignmentCompatibleWith(t);
    }

    public boolean isAssignmentCompatibleWith(Type t) {
        if (!(t instanceof ReferenceType)) {
            return false;
        }
        ReferenceType T = (ReferenceType)t;
        if (this.equals(Type.NULL)) {
            return true;
        }
        if (this instanceof ObjectType && ((ObjectType)this).referencesClass()) {
            if (T instanceof ObjectType && ((ObjectType)T).referencesClass()) {
                if (this.equals(T)) {
                    return true;
                }
                if (Repository.instanceOf(((ObjectType)this).getClassName(), ((ObjectType)T).getClassName())) {
                    return true;
                }
            }
            if (T instanceof ObjectType && ((ObjectType)T).referencesInterface() && Repository.implementationOf(((ObjectType)this).getClassName(), ((ObjectType)T).getClassName())) {
                return true;
            }
        }
        if (this instanceof ObjectType && ((ObjectType)this).referencesInterface()) {
            if (T instanceof ObjectType && ((ObjectType)T).referencesClass() && T.equals(Type.OBJECT)) {
                return true;
            }
            if (T instanceof ObjectType && ((ObjectType)T).referencesInterface()) {
                if (this.equals(T)) {
                    return true;
                }
                if (Repository.implementationOf(((ObjectType)this).getClassName(), ((ObjectType)T).getClassName())) {
                    return true;
                }
            }
        }
        if (this instanceof ArrayType) {
            if (T instanceof ObjectType && ((ObjectType)T).referencesClass() && T.equals(Type.OBJECT)) {
                return true;
            }
            if (T instanceof ArrayType) {
                Type sc = ((ArrayType)this).getElementType();
                Type tc = ((ArrayType)this).getElementType();
                if (sc instanceof BasicType && tc instanceof BasicType && sc.equals(tc)) {
                    return true;
                }
                if (tc instanceof ReferenceType && sc instanceof ReferenceType && ((ReferenceType)sc).isAssignmentCompatibleWith(tc)) {
                    return true;
                }
            }
            if (T instanceof ObjectType && ((ObjectType)T).referencesInterface()) {
                for (int ii = 0; ii < Constants.INTERFACES_IMPLEMENTED_BY_ARRAYS.length; ++ii) {
                    if (!T.equals(new ObjectType(Constants.INTERFACES_IMPLEMENTED_BY_ARRAYS[ii]))) continue;
                    return true;
                }
            }
        }
        return false;
    }

    public ReferenceType getFirstCommonSuperclass(ReferenceType t) {
        if (this.equals(Type.NULL)) {
            return t;
        }
        if (t.equals(Type.NULL)) {
            return this;
        }
        if (this.equals(t)) {
            return this;
        }
        if (this instanceof ArrayType && t instanceof ArrayType) {
            ArrayType arrType1 = (ArrayType)this;
            ArrayType arrType2 = (ArrayType)t;
            if (arrType1.getDimensions() == arrType2.getDimensions() && arrType1.getBasicType() instanceof ObjectType && arrType2.getBasicType() instanceof ObjectType) {
                return new ArrayType(((ObjectType)arrType1.getBasicType()).getFirstCommonSuperclass((ObjectType)arrType2.getBasicType()), arrType1.getDimensions());
            }
        }
        if (this instanceof ArrayType || t instanceof ArrayType) {
            return Type.OBJECT;
        }
        if (this instanceof ObjectType && ((ObjectType)this).referencesInterface() || t instanceof ObjectType && ((ObjectType)t).referencesInterface()) {
            return Type.OBJECT;
        }
        ObjectType thiz = (ObjectType)this;
        ObjectType other = (ObjectType)t;
        JavaClass[] thiz_sups = Repository.lookupClass(thiz.getClassName()).getSuperClasses();
        JavaClass[] other_sups = Repository.lookupClass(other.getClassName()).getSuperClasses();
        if (thiz_sups == null || other_sups == null) {
            return null;
        }
        JavaClass[] this_sups = new JavaClass[thiz_sups.length + 1];
        JavaClass[] t_sups = new JavaClass[other_sups.length + 1];
        System.arraycopy(thiz_sups, 0, this_sups, 1, thiz_sups.length);
        System.arraycopy(other_sups, 0, t_sups, 1, other_sups.length);
        this_sups[0] = Repository.lookupClass(thiz.getClassName());
        t_sups[0] = Repository.lookupClass(other.getClassName());
        for (int i = 0; i < t_sups.length; ++i) {
            for (int j = 0; j < this_sups.length; ++j) {
                if (!this_sups[j].equals(t_sups[i])) continue;
                return new ObjectType(this_sups[j].getClassName());
            }
        }
        return null;
    }
}

