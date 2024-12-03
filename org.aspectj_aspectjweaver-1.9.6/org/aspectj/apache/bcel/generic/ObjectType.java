/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.apache.bcel.generic;

import org.aspectj.apache.bcel.Repository;
import org.aspectj.apache.bcel.classfile.JavaClass;
import org.aspectj.apache.bcel.generic.ReferenceType;

public class ObjectType
extends ReferenceType {
    private String classname;

    public ObjectType(String class_name) {
        super((byte)14, ObjectType.toSignature(class_name));
        this.classname = class_name;
    }

    public ObjectType(String classname, String signature) {
        super((byte)14, signature);
        this.classname = classname;
    }

    private static String toSignature(String classname) {
        StringBuffer sig = new StringBuffer();
        sig.append("L").append(classname.replace('.', '/'));
        sig.append(";");
        return sig.toString();
    }

    public String getClassName() {
        return this.classname;
    }

    public int hashCode() {
        return this.classname.hashCode();
    }

    public boolean equals(Object type) {
        return type instanceof ObjectType ? ((ObjectType)type).classname.equals(this.classname) : false;
    }

    public boolean referencesClass() {
        JavaClass jc = Repository.lookupClass(this.classname);
        if (jc == null) {
            return false;
        }
        return jc.isClass();
    }

    public boolean referencesInterface() {
        JavaClass jc = Repository.lookupClass(this.classname);
        if (jc == null) {
            return false;
        }
        return !jc.isClass();
    }

    public boolean subclassOf(ObjectType superclass) {
        if (this.referencesInterface() || superclass.referencesInterface()) {
            return false;
        }
        return Repository.instanceOf(this.classname, superclass.classname);
    }

    public boolean accessibleTo(ObjectType accessor) {
        JavaClass jc = Repository.lookupClass(this.classname);
        if (jc.isPublic()) {
            return true;
        }
        JavaClass acc = Repository.lookupClass(accessor.classname);
        return acc.getPackageName().equals(jc.getPackageName());
    }
}

