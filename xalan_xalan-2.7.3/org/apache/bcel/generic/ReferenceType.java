/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.generic;

import org.apache.bcel.Const;
import org.apache.bcel.Repository;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.generic.ArrayType;
import org.apache.bcel.generic.BasicType;
import org.apache.bcel.generic.ObjectType;
import org.apache.bcel.generic.Type;

public abstract class ReferenceType
extends Type {
    ReferenceType() {
        super((byte)14, "<null object>");
    }

    protected ReferenceType(byte t, String s) {
        super(t, s);
    }

    @Deprecated
    public ReferenceType firstCommonSuperclass(ReferenceType t) throws ClassNotFoundException {
        if (this.equals(Type.NULL)) {
            return t;
        }
        if (t.equals(Type.NULL) || this.equals(t)) {
            return this;
        }
        if (this instanceof ArrayType || t instanceof ArrayType) {
            return Type.OBJECT;
        }
        return this.getFirstCommonSuperclassInternal(t);
    }

    public ReferenceType getFirstCommonSuperclass(ReferenceType t) throws ClassNotFoundException {
        if (this.equals(Type.NULL)) {
            return t;
        }
        if (t.equals(Type.NULL) || this.equals(t)) {
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
        return this.getFirstCommonSuperclassInternal(t);
    }

    private ReferenceType getFirstCommonSuperclassInternal(ReferenceType t) throws ClassNotFoundException {
        if (this instanceof ObjectType && ((ObjectType)this).referencesInterfaceExact() || t instanceof ObjectType && ((ObjectType)t).referencesInterfaceExact()) {
            return Type.OBJECT;
        }
        ObjectType thiz = (ObjectType)this;
        ObjectType other = (ObjectType)t;
        JavaClass[] thizSups = Repository.getSuperClasses(thiz.getClassName());
        JavaClass[] otherSups = Repository.getSuperClasses(other.getClassName());
        if (thizSups == null || otherSups == null) {
            return null;
        }
        JavaClass[] thisSups = new JavaClass[thizSups.length + 1];
        JavaClass[] tSups = new JavaClass[otherSups.length + 1];
        System.arraycopy(thizSups, 0, thisSups, 1, thizSups.length);
        System.arraycopy(otherSups, 0, tSups, 1, otherSups.length);
        thisSups[0] = Repository.lookupClass(thiz.getClassName());
        tSups[0] = Repository.lookupClass(other.getClassName());
        for (JavaClass tSup : tSups) {
            for (JavaClass thisSup : thisSups) {
                if (!thisSup.equals(tSup)) continue;
                return ObjectType.getInstance(thisSup.getClassName());
            }
        }
        return null;
    }

    public boolean isAssignmentCompatibleWith(Type t) throws ClassNotFoundException {
        if (!(t instanceof ReferenceType)) {
            return false;
        }
        ReferenceType T = (ReferenceType)t;
        if (this.equals(Type.NULL)) {
            return true;
        }
        if (this instanceof ObjectType && ((ObjectType)this).referencesClassExact()) {
            if (T instanceof ObjectType && ((ObjectType)T).referencesClassExact() && (this.equals(T) || Repository.instanceOf(((ObjectType)this).getClassName(), ((ObjectType)T).getClassName()))) {
                return true;
            }
            if (T instanceof ObjectType && ((ObjectType)T).referencesInterfaceExact() && Repository.implementationOf(((ObjectType)this).getClassName(), ((ObjectType)T).getClassName())) {
                return true;
            }
        }
        if (this instanceof ObjectType && ((ObjectType)this).referencesInterfaceExact()) {
            if (T instanceof ObjectType && ((ObjectType)T).referencesClassExact() && T.equals(Type.OBJECT)) {
                return true;
            }
            if (T instanceof ObjectType && ((ObjectType)T).referencesInterfaceExact() && (this.equals(T) || Repository.implementationOf(((ObjectType)this).getClassName(), ((ObjectType)T).getClassName()))) {
                return true;
            }
        }
        if (this instanceof ArrayType) {
            if (T instanceof ObjectType && ((ObjectType)T).referencesClassExact() && T.equals(Type.OBJECT)) {
                return true;
            }
            if (T instanceof ArrayType) {
                Type sc = ((ArrayType)this).getElementType();
                Type tc = ((ArrayType)T).getElementType();
                if (sc instanceof BasicType && tc instanceof BasicType && sc.equals(tc)) {
                    return true;
                }
                if (tc instanceof ReferenceType && sc instanceof ReferenceType && ((ReferenceType)sc).isAssignmentCompatibleWith(tc)) {
                    return true;
                }
            }
            if (T instanceof ObjectType && ((ObjectType)T).referencesInterfaceExact()) {
                for (String element : Const.getInterfacesImplementedByArrays()) {
                    if (!T.equals(ObjectType.getInstance(element))) continue;
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isCastableTo(Type t) throws ClassNotFoundException {
        if (this.equals(Type.NULL)) {
            return t instanceof ReferenceType;
        }
        return this.isAssignmentCompatibleWith(t);
    }
}

