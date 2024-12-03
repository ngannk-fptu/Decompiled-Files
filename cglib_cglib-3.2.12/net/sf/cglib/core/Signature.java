/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.objectweb.asm.Type
 */
package net.sf.cglib.core;

import org.objectweb.asm.Type;

public class Signature {
    private String name;
    private String desc;

    public Signature(String name, String desc) {
        if (name.indexOf(40) >= 0) {
            throw new IllegalArgumentException("Name '" + name + "' is invalid");
        }
        this.name = name;
        this.desc = desc;
    }

    public Signature(String name, Type returnType, Type[] argumentTypes) {
        this(name, Type.getMethodDescriptor((Type)returnType, (Type[])argumentTypes));
    }

    public String getName() {
        return this.name;
    }

    public String getDescriptor() {
        return this.desc;
    }

    public Type getReturnType() {
        return Type.getReturnType((String)this.desc);
    }

    public Type[] getArgumentTypes() {
        return Type.getArgumentTypes((String)this.desc);
    }

    public String toString() {
        return this.name + this.desc;
    }

    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof Signature)) {
            return false;
        }
        Signature other = (Signature)o;
        return this.name.equals(other.name) && this.desc.equals(other.desc);
    }

    public int hashCode() {
        return this.name.hashCode() ^ this.desc.hashCode();
    }
}

