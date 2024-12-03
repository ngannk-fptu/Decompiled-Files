/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.classfile;

import java.io.DataInput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.stream.Stream;
import org.apache.bcel.classfile.Attribute;
import org.apache.bcel.classfile.BootstrapMethod;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.Visitor;

public class BootstrapMethods
extends Attribute
implements Iterable<BootstrapMethod> {
    private BootstrapMethod[] bootstrapMethods;

    public BootstrapMethods(BootstrapMethods c) {
        this(c.getNameIndex(), c.getLength(), c.getBootstrapMethods(), c.getConstantPool());
    }

    public BootstrapMethods(int nameIndex, int length, BootstrapMethod[] bootstrapMethods, ConstantPool constantPool) {
        super((byte)20, nameIndex, length, constantPool);
        this.bootstrapMethods = bootstrapMethods;
    }

    BootstrapMethods(int nameIndex, int length, DataInput input, ConstantPool constantPool) throws IOException {
        this(nameIndex, length, (BootstrapMethod[])null, constantPool);
        int numBootstrapMethods = input.readUnsignedShort();
        this.bootstrapMethods = new BootstrapMethod[numBootstrapMethods];
        for (int i = 0; i < numBootstrapMethods; ++i) {
            this.bootstrapMethods[i] = new BootstrapMethod(input);
        }
    }

    @Override
    public void accept(Visitor v) {
        v.visitBootstrapMethods(this);
    }

    @Override
    public BootstrapMethods copy(ConstantPool constantPool) {
        BootstrapMethods c = (BootstrapMethods)this.clone();
        c.bootstrapMethods = new BootstrapMethod[this.bootstrapMethods.length];
        for (int i = 0; i < this.bootstrapMethods.length; ++i) {
            c.bootstrapMethods[i] = this.bootstrapMethods[i].copy();
        }
        c.setConstantPool(constantPool);
        return c;
    }

    @Override
    public final void dump(DataOutputStream file) throws IOException {
        super.dump(file);
        file.writeShort(this.bootstrapMethods.length);
        for (BootstrapMethod bootstrapMethod : this.bootstrapMethods) {
            bootstrapMethod.dump(file);
        }
    }

    public final BootstrapMethod[] getBootstrapMethods() {
        return this.bootstrapMethods;
    }

    @Override
    public Iterator<BootstrapMethod> iterator() {
        return Stream.of(this.bootstrapMethods).iterator();
    }

    public final void setBootstrapMethods(BootstrapMethod[] bootstrapMethods) {
        this.bootstrapMethods = bootstrapMethods;
    }

    @Override
    public final String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("BootstrapMethods(");
        buf.append(this.bootstrapMethods.length);
        buf.append("):");
        for (int i = 0; i < this.bootstrapMethods.length; ++i) {
            buf.append("\n");
            int start = buf.length();
            buf.append("  ").append(i).append(": ");
            int indentCount = buf.length() - start;
            String[] lines = this.bootstrapMethods[i].toString(super.getConstantPool()).split("\\r?\\n");
            buf.append(lines[0]);
            for (int j = 1; j < lines.length; ++j) {
                buf.append("\n").append("          ", 0, indentCount).append(lines[j]);
            }
        }
        return buf.toString();
    }
}

