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
import org.aspectj.apache.bcel.classfile.ConstantPool;
import org.aspectj.apache.bcel.classfile.ConstantUtf8;

public final class Signature
extends Attribute {
    private int signature_index;

    public Signature(Signature c) {
        this(c.getNameIndex(), c.getLength(), c.getSignatureIndex(), c.getConstantPool());
    }

    Signature(int name_index, int length, DataInputStream file, ConstantPool constant_pool) throws IOException {
        this(name_index, length, file.readUnsignedShort(), constant_pool);
    }

    public Signature(int name_index, int length, int signature_index, ConstantPool constant_pool) {
        super((byte)10, name_index, length, constant_pool);
        this.signature_index = signature_index;
    }

    @Override
    public void accept(ClassVisitor v) {
        System.err.println("Visiting non-standard Signature object");
        v.visitSignature(this);
    }

    @Override
    public final void dump(DataOutputStream file) throws IOException {
        super.dump(file);
        file.writeShort(this.signature_index);
    }

    public final int getSignatureIndex() {
        return this.signature_index;
    }

    public final void setSignatureIndex(int signature_index) {
        this.signature_index = signature_index;
    }

    public final String getSignature() {
        ConstantUtf8 c = (ConstantUtf8)this.cpool.getConstant(this.signature_index, (byte)1);
        return c.getValue();
    }

    private static boolean identStart(int ch) {
        return ch == 84 || ch == 76;
    }

    private static final void matchIdent(MyByteArrayInputStream in, StringBuffer buf) {
        int ch = in.read();
        if (ch == -1) {
            throw new RuntimeException("Illegal signature: " + in.getData() + " no ident, reaching EOF");
        }
        if (!Signature.identStart(ch)) {
            StringBuffer buf2 = new StringBuffer();
            int count = 1;
            while (Character.isJavaIdentifierPart((char)ch)) {
                buf2.append((char)ch);
                ++count;
                ch = in.read();
            }
            if (ch == 58) {
                in.skip("Ljava/lang/Object".length());
                buf.append(buf2);
                ch = in.read();
                in.unread();
            } else {
                for (int i = 0; i < count; ++i) {
                    in.unread();
                }
            }
            return;
        }
        StringBuffer buf2 = new StringBuffer();
        ch = in.read();
        do {
            buf2.append((char)ch);
        } while ((ch = in.read()) != -1 && (Character.isJavaIdentifierPart((char)ch) || ch == 47));
        buf.append(buf2.toString().replace('/', '.'));
        if (ch != -1) {
            in.unread();
        }
    }

    private static final void matchGJIdent(MyByteArrayInputStream in, StringBuffer buf) {
        Signature.matchIdent(in, buf);
        int ch = in.read();
        if (ch == 60 || ch == 40) {
            buf.append((char)ch);
            Signature.matchGJIdent(in, buf);
            while ((ch = in.read()) != 62 && ch != 41) {
                if (ch == -1) {
                    throw new RuntimeException("Illegal signature: " + in.getData() + " reaching EOF");
                }
                buf.append(", ");
                in.unread();
                Signature.matchGJIdent(in, buf);
            }
            buf.append((char)ch);
        } else {
            in.unread();
        }
        ch = in.read();
        if (Signature.identStart(ch)) {
            in.unread();
            Signature.matchGJIdent(in, buf);
        } else {
            if (ch == 41) {
                in.unread();
                return;
            }
            if (ch != 59) {
                throw new RuntimeException("Illegal signature: " + in.getData() + " read " + (char)ch);
            }
        }
    }

    public static String translate(String s) {
        StringBuffer buf = new StringBuffer();
        Signature.matchGJIdent(new MyByteArrayInputStream(s), buf);
        return buf.toString();
    }

    public static final boolean isFormalParameterList(String s) {
        return s.startsWith("<") && s.indexOf(58) > 0;
    }

    public static final boolean isActualParameterList(String s) {
        return s.startsWith("L") && s.endsWith(">;");
    }

    @Override
    public final String toString() {
        String s = this.getSignature();
        return "Signature(" + s + ")";
    }

    private static final class MyByteArrayInputStream
    extends ByteArrayInputStream {
        MyByteArrayInputStream(String data) {
            super(data.getBytes());
        }

        final int mark() {
            return this.pos;
        }

        final String getData() {
            return new String(this.buf);
        }

        final void reset(int p) {
            this.pos = p;
        }

        final void unread() {
            if (this.pos > 0) {
                --this.pos;
            }
        }
    }
}

