/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.generic;

import org.apache.bcel.Repository;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Utility;
import org.apache.bcel.generic.ReferenceType;

public class ObjectType
extends ReferenceType {
    private final String className;

    public static ObjectType getInstance(String className) {
        return new ObjectType(className);
    }

    public ObjectType(String className) {
        super((byte)14, "L" + Utility.packageToPath(className) + ";");
        this.className = Utility.pathToPackage(className);
    }

    public boolean accessibleTo(ObjectType accessor) throws ClassNotFoundException {
        JavaClass jc = Repository.lookupClass(this.className);
        if (jc.isPublic()) {
            return true;
        }
        JavaClass acc = Repository.lookupClass(accessor.className);
        return acc.getPackageName().equals(jc.getPackageName());
    }

    @Override
    public boolean equals(Object type) {
        return type instanceof ObjectType && ((ObjectType)type).className.equals(this.className);
    }

    @Override
    public String getClassName() {
        return this.className;
    }

    @Override
    public int hashCode() {
        return this.className.hashCode();
    }

    @Deprecated
    public boolean referencesClass() {
        try {
            JavaClass jc = Repository.lookupClass(this.className);
            return jc.isClass();
        }
        catch (ClassNotFoundException e) {
            return false;
        }
    }

    public boolean referencesClassExact() throws ClassNotFoundException {
        JavaClass jc = Repository.lookupClass(this.className);
        return jc.isClass();
    }

    @Deprecated
    public boolean referencesInterface() {
        try {
            JavaClass jc = Repository.lookupClass(this.className);
            return !jc.isClass();
        }
        catch (ClassNotFoundException e) {
            return false;
        }
    }

    public boolean referencesInterfaceExact() throws ClassNotFoundException {
        JavaClass jc = Repository.lookupClass(this.className);
        return !jc.isClass();
    }

    public boolean subclassOf(ObjectType superclass) throws ClassNotFoundException {
        if (this.referencesInterfaceExact() || superclass.referencesInterfaceExact()) {
            return false;
        }
        return Repository.instanceOf(this.className, superclass.className);
    }
}

