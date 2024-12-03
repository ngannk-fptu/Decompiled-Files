/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.apache.bcel.classfile;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import org.aspectj.apache.bcel.classfile.Attribute;
import org.aspectj.apache.bcel.classfile.ClassVisitor;
import org.aspectj.apache.bcel.classfile.ConstantMethodHandle;
import org.aspectj.apache.bcel.classfile.ConstantPool;

public final class BootstrapMethods
extends Attribute {
    private boolean isInPackedState = false;
    private byte[] data;
    private int numBootstrapMethods;
    private BootstrapMethod[] bootstrapMethods;

    public BootstrapMethods(BootstrapMethods c) {
        this(c.getNameIndex(), c.getLength(), c.getBootstrapMethods(), c.getConstantPool());
    }

    public BootstrapMethods(int nameIndex, int length, BootstrapMethod[] lineNumberTable, ConstantPool constantPool) {
        super((byte)19, nameIndex, length, constantPool);
        this.setBootstrapMethods(lineNumberTable);
        this.isInPackedState = false;
    }

    public final void setBootstrapMethods(BootstrapMethod[] bootstrapMethods) {
        this.data = null;
        this.isInPackedState = false;
        this.bootstrapMethods = bootstrapMethods;
        this.numBootstrapMethods = bootstrapMethods == null ? 0 : bootstrapMethods.length;
    }

    BootstrapMethods(int name_index, int length, DataInputStream file, ConstantPool constant_pool) throws IOException {
        this(name_index, length, (BootstrapMethod[])null, constant_pool);
        this.data = new byte[length];
        file.readFully(this.data);
        this.isInPackedState = true;
    }

    private void unpack() {
        if (this.isInPackedState) {
            try {
                ByteArrayInputStream bs = new ByteArrayInputStream(this.data);
                DataInputStream dis = new DataInputStream(bs);
                this.numBootstrapMethods = dis.readUnsignedShort();
                this.bootstrapMethods = new BootstrapMethod[this.numBootstrapMethods];
                for (int i = 0; i < this.numBootstrapMethods; ++i) {
                    this.bootstrapMethods[i] = new BootstrapMethod(dis);
                }
                dis.close();
                this.data = null;
            }
            catch (IOException e) {
                throw new RuntimeException("Unpacking of LineNumberTable attribute failed");
            }
            this.isInPackedState = false;
        }
    }

    @Override
    public void accept(ClassVisitor v) {
        this.unpack();
        v.visitBootstrapMethods(this);
    }

    @Override
    public final void dump(DataOutputStream file) throws IOException {
        super.dump(file);
        if (this.isInPackedState) {
            file.write(this.data);
        } else {
            int blen = this.bootstrapMethods.length;
            file.writeShort(blen);
            for (int i = 0; i < blen; ++i) {
                this.bootstrapMethods[i].dump(file);
            }
        }
    }

    public final BootstrapMethod[] getBootstrapMethods() {
        this.unpack();
        return this.bootstrapMethods;
    }

    @Override
    public final String toString() {
        this.unpack();
        StringBuffer buf = new StringBuffer();
        StringBuffer line = new StringBuffer();
        for (int i = 0; i < this.numBootstrapMethods; ++i) {
            BootstrapMethod bm = this.bootstrapMethods[i];
            line.append("BootstrapMethod[").append(i).append("]:");
            int ref = bm.getBootstrapMethodRef();
            ConstantMethodHandle mh = (ConstantMethodHandle)this.getConstantPool().getConstant(ref);
            line.append("#" + ref + ":");
            line.append(ConstantMethodHandle.kindToString(mh.getReferenceKind()));
            line.append(" ").append(this.getConstantPool().getConstant(mh.getReferenceIndex()));
            int[] args = bm.getBootstrapArguments();
            line.append(" argcount:").append(args == null ? 0 : args.length).append(" ");
            if (args != null) {
                for (int a = 0; a < args.length; ++a) {
                    line.append(args[a]).append("(").append(this.getConstantPool().getConstant(args[a])).append(") ");
                }
            }
            if (i < this.numBootstrapMethods - 1) {
                line.append(", ");
            }
            if (line.length() <= 72) continue;
            line.append('\n');
            buf.append(line);
            line.setLength(0);
        }
        buf.append(line);
        return buf.toString();
    }

    public final int getNumBootstrapMethods() {
        this.unpack();
        return this.bootstrapMethods.length;
    }

    public static class BootstrapMethod {
        private int bootstrapMethodRef;
        private int[] bootstrapArguments;

        BootstrapMethod(DataInputStream file) throws IOException {
            this(file.readUnsignedShort(), BootstrapMethod.readBootstrapArguments(file));
        }

        private static int[] readBootstrapArguments(DataInputStream dis) throws IOException {
            int numBootstrapMethods = dis.readUnsignedShort();
            int[] bootstrapArguments = new int[numBootstrapMethods];
            for (int i = 0; i < numBootstrapMethods; ++i) {
                bootstrapArguments[i] = dis.readUnsignedShort();
            }
            return bootstrapArguments;
        }

        public BootstrapMethod(int bootstrapMethodRef, int[] bootstrapArguments) {
            this.bootstrapMethodRef = bootstrapMethodRef;
            this.bootstrapArguments = bootstrapArguments;
        }

        public int getBootstrapMethodRef() {
            return this.bootstrapMethodRef;
        }

        public int[] getBootstrapArguments() {
            return this.bootstrapArguments;
        }

        public final void dump(DataOutputStream file) throws IOException {
            file.writeShort(this.bootstrapMethodRef);
            int len = this.bootstrapArguments.length;
            file.writeShort(len);
            for (int i = 0; i < len; ++i) {
                file.writeShort(this.bootstrapArguments[i]);
            }
        }

        public final int getLength() {
            return 4 + 2 * this.bootstrapArguments.length;
        }
    }
}

