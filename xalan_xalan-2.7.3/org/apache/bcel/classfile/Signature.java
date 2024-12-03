/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.classfile;

import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import org.apache.bcel.classfile.Attribute;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.Utility;
import org.apache.bcel.classfile.Visitor;
import org.apache.bcel.util.Args;

public final class Signature
extends Attribute {
    private int signatureIndex;

    private static boolean identStart(int ch) {
        return ch == 84 || ch == 76;
    }

    public static boolean isActualParameterList(String s) {
        return s.startsWith("L") && s.endsWith(">;");
    }

    public static boolean isFormalParameterList(String s) {
        return s.startsWith("<") && s.indexOf(58) > 0;
    }

    private static void matchGJIdent(MyByteArrayInputStream in, StringBuilder buf) {
        Signature.matchIdent(in, buf);
        int ch = in.read();
        if (ch == 60 || ch == 40) {
            buf.append((char)ch);
            Signature.matchGJIdent(in, buf);
            while ((ch = in.read()) != 62 && ch != 41) {
                if (ch == -1) {
                    throw new IllegalArgumentException("Illegal signature: " + in.getData() + " reaching EOF");
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
        } else if (ch == 41) {
            in.unread();
        } else if (ch != 59) {
            throw new IllegalArgumentException("Illegal signature: " + in.getData() + " read " + (char)ch);
        }
    }

    private static void matchIdent(MyByteArrayInputStream in, StringBuilder buf) {
        int ch = in.read();
        if (ch == -1) {
            throw new IllegalArgumentException("Illegal signature: " + in.getData() + " no ident, reaching EOF");
        }
        if (!Signature.identStart(ch)) {
            StringBuilder buf2 = new StringBuilder();
            int count = 1;
            while (Character.isJavaIdentifierPart((char)ch)) {
                buf2.append((char)ch);
                ++count;
                ch = in.read();
            }
            if (ch == 58) {
                int skipExpected = "Ljava/lang/Object".length();
                long skipActual = in.skip(skipExpected);
                if (skipActual != (long)skipExpected) {
                    throw new IllegalStateException(String.format("Unexpected skip: expected=%,d, actual=%,d", skipExpected, skipActual));
                }
                buf.append((CharSequence)buf2);
                ch = in.read();
                in.unread();
            } else {
                for (int i = 0; i < count; ++i) {
                    in.unread();
                }
            }
            return;
        }
        StringBuilder buf2 = new StringBuilder();
        ch = in.read();
        do {
            buf2.append((char)ch);
        } while ((ch = in.read()) != -1 && (Character.isJavaIdentifierPart((char)ch) || ch == 47));
        buf.append(Utility.pathToPackage(buf2.toString()));
        if (ch != -1) {
            in.unread();
        }
    }

    public static String translate(String s) {
        StringBuilder buf = new StringBuilder();
        Signature.matchGJIdent(new MyByteArrayInputStream(s), buf);
        return buf.toString();
    }

    Signature(int nameIndex, int length, DataInput input, ConstantPool constantPool) throws IOException {
        this(nameIndex, length, input.readUnsignedShort(), constantPool);
    }

    public Signature(int nameIndex, int length, int signatureIndex, ConstantPool constantPool) {
        super((byte)10, nameIndex, Args.require(length, 2, "Signature length attribute"), constantPool);
        this.signatureIndex = signatureIndex;
        Objects.requireNonNull(constantPool.getConstantUtf8(signatureIndex), "constantPool.getConstantUtf8(signatureIndex)");
    }

    public Signature(Signature c) {
        this(c.getNameIndex(), c.getLength(), c.getSignatureIndex(), c.getConstantPool());
    }

    @Override
    public void accept(Visitor v) {
        v.visitSignature(this);
    }

    @Override
    public Attribute copy(ConstantPool constantPool) {
        return (Attribute)this.clone();
    }

    @Override
    public void dump(DataOutputStream file) throws IOException {
        super.dump(file);
        file.writeShort(this.signatureIndex);
    }

    public String getSignature() {
        return super.getConstantPool().getConstantUtf8(this.signatureIndex).getBytes();
    }

    public int getSignatureIndex() {
        return this.signatureIndex;
    }

    public void setSignatureIndex(int signatureIndex) {
        this.signatureIndex = signatureIndex;
    }

    @Override
    public String toString() {
        return "Signature: " + this.getSignature();
    }

    private static final class MyByteArrayInputStream
    extends ByteArrayInputStream {
        MyByteArrayInputStream(String data) {
            super(data.getBytes(StandardCharsets.UTF_8));
        }

        String getData() {
            return new String(this.buf, StandardCharsets.UTF_8);
        }

        void unread() {
            if (this.pos > 0) {
                --this.pos;
            }
        }
    }
}

