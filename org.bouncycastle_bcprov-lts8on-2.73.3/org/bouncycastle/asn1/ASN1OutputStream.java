/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1;

import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DEROutputStream;
import org.bouncycastle.asn1.DLOutputStream;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class ASN1OutputStream {
    private OutputStream os;

    public static ASN1OutputStream create(OutputStream out) {
        return new ASN1OutputStream(out);
    }

    public static ASN1OutputStream create(OutputStream out, String encoding) {
        if (encoding.equals("DER")) {
            return new DEROutputStream(out);
        }
        if (encoding.equals("DL")) {
            return new DLOutputStream(out);
        }
        return new ASN1OutputStream(out);
    }

    ASN1OutputStream(OutputStream os) {
        this.os = os;
    }

    public void close() throws IOException {
        this.os.close();
    }

    public void flush() throws IOException {
        this.os.flush();
    }

    public final void writeObject(ASN1Encodable encodable) throws IOException {
        if (null == encodable) {
            throw new IOException("null object detected");
        }
        this.writePrimitive(encodable.toASN1Primitive(), true);
        this.flushInternal();
    }

    public final void writeObject(ASN1Primitive primitive) throws IOException {
        if (null == primitive) {
            throw new IOException("null object detected");
        }
        this.writePrimitive(primitive, true);
        this.flushInternal();
    }

    void flushInternal() throws IOException {
    }

    DEROutputStream getDERSubStream() {
        return new DEROutputStream(this.os);
    }

    DLOutputStream getDLSubStream() {
        return new DLOutputStream(this.os);
    }

    final void writeDL(int length) throws IOException {
        if (length < 128) {
            this.write(length);
        } else {
            byte[] stack = new byte[5];
            int pos = stack.length;
            do {
                stack[--pos] = (byte)length;
            } while ((length >>>= 8) != 0);
            int count = stack.length - pos;
            stack[--pos] = (byte)(0x80 | count);
            this.write(stack, pos, count + 1);
        }
    }

    final void write(int b) throws IOException {
        this.os.write(b);
    }

    final void write(byte[] bytes, int off, int len) throws IOException {
        this.os.write(bytes, off, len);
    }

    void writeElements(ASN1Encodable[] elements) throws IOException {
        int count = elements.length;
        for (int i = 0; i < count; ++i) {
            elements[i].toASN1Primitive().encode(this, true);
        }
    }

    final void writeEncodingDL(boolean withID, int identifier, byte contents) throws IOException {
        this.writeIdentifier(withID, identifier);
        this.writeDL(1);
        this.write(contents);
    }

    final void writeEncodingDL(boolean withID, int identifier, byte[] contents) throws IOException {
        this.writeIdentifier(withID, identifier);
        this.writeDL(contents.length);
        this.write(contents, 0, contents.length);
    }

    final void writeEncodingDL(boolean withID, int identifier, byte[] contents, int contentsOff, int contentsLen) throws IOException {
        this.writeIdentifier(withID, identifier);
        this.writeDL(contentsLen);
        this.write(contents, contentsOff, contentsLen);
    }

    final void writeEncodingDL(boolean withID, int identifier, byte contentsPrefix, byte[] contents, int contentsOff, int contentsLen) throws IOException {
        this.writeIdentifier(withID, identifier);
        this.writeDL(1 + contentsLen);
        this.write(contentsPrefix);
        this.write(contents, contentsOff, contentsLen);
    }

    final void writeEncodingDL(boolean withID, int identifier, byte[] contents, int contentsOff, int contentsLen, byte contentsSuffix) throws IOException {
        this.writeIdentifier(withID, identifier);
        this.writeDL(contentsLen + 1);
        this.write(contents, contentsOff, contentsLen);
        this.write(contentsSuffix);
    }

    final void writeEncodingDL(boolean withID, int flags, int tag, byte[] contents) throws IOException {
        this.writeIdentifier(withID, flags, tag);
        this.writeDL(contents.length);
        this.write(contents, 0, contents.length);
    }

    final void writeEncodingIL(boolean withID, int identifier, ASN1Encodable[] elements) throws IOException {
        this.writeIdentifier(withID, identifier);
        this.write(128);
        this.writeElements(elements);
        this.write(0);
        this.write(0);
    }

    final void writeIdentifier(boolean withID, int identifier) throws IOException {
        if (withID) {
            this.write(identifier);
        }
    }

    final void writeIdentifier(boolean withID, int flags, int tag) throws IOException {
        if (withID) {
            if (tag < 31) {
                this.write(flags | tag);
            } else {
                byte[] stack = new byte[6];
                int pos = stack.length;
                stack[--pos] = (byte)(tag & 0x7F);
                while (tag > 127) {
                    stack[--pos] = (byte)((tag >>>= 7) & 0x7F | 0x80);
                }
                stack[--pos] = (byte)(flags | 0x1F);
                this.write(stack, pos, stack.length - pos);
            }
        }
    }

    void writePrimitive(ASN1Primitive primitive, boolean withID) throws IOException {
        primitive.encode(this, withID);
    }

    void writePrimitives(ASN1Primitive[] primitives) throws IOException {
        int count = primitives.length;
        for (int i = 0; i < count; ++i) {
            primitives[i].encode(this, true);
        }
    }

    static int getLengthOfDL(int dl) {
        if (dl < 128) {
            return 1;
        }
        int length = 2;
        while ((dl >>>= 8) != 0) {
            ++length;
        }
        return length;
    }

    static int getLengthOfEncodingDL(boolean withID, int contentsLength) {
        return (withID ? 1 : 0) + ASN1OutputStream.getLengthOfDL(contentsLength) + contentsLength;
    }

    static int getLengthOfIdentifier(int tag) {
        if (tag < 31) {
            return 1;
        }
        int length = 2;
        while ((tag >>>= 7) != 0) {
            ++length;
        }
        return length;
    }
}

