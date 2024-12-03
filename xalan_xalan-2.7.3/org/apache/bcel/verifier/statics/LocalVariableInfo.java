/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.verifier.statics;

import java.util.Hashtable;
import org.apache.bcel.generic.Type;
import org.apache.bcel.verifier.exc.LocalVariableInfoInconsistentException;

public class LocalVariableInfo {
    private final Hashtable<String, Type> types = new Hashtable();
    private final Hashtable<String, String> names = new Hashtable();

    private void add(int offset, String name, Type t) throws LocalVariableInfoInconsistentException {
        if (this.getName(offset) != null && !this.getName(offset).equals(name)) {
            throw new LocalVariableInfoInconsistentException("At bytecode offset '" + offset + "' a local variable has two different names: '" + this.getName(offset) + "' and '" + name + "'.");
        }
        if (this.getType(offset) != null && !this.getType(offset).equals(t)) {
            throw new LocalVariableInfoInconsistentException("At bytecode offset '" + offset + "' a local variable has two different types: '" + this.getType(offset) + "' and '" + t + "'.");
        }
        this.setName(offset, name);
        this.setType(offset, t);
    }

    public void add(String name, int startPc, int length, Type type) throws LocalVariableInfoInconsistentException {
        for (int i = startPc; i <= startPc + length; ++i) {
            this.add(i, name, type);
        }
    }

    public String getName(int offset) {
        return this.names.get(Integer.toString(offset));
    }

    public Type getType(int offset) {
        return this.types.get(Integer.toString(offset));
    }

    private void setName(int offset, String name) {
        this.names.put(Integer.toString(offset), name);
    }

    private void setType(int offset, Type t) {
        this.types.put(Integer.toString(offset), t);
    }
}

