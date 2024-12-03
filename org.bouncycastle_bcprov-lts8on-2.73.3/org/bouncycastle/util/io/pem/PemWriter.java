/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.util.io.pem;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import org.bouncycastle.util.Strings;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.io.pem.PemHeader;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemObjectGenerator;

public class PemWriter
extends BufferedWriter {
    private static final int LINE_LENGTH = 64;
    private final int nlLength;
    private char[] buf = new char[64];

    public PemWriter(Writer out) {
        super(out);
        String nl = Strings.lineSeparator();
        this.nlLength = nl != null ? nl.length() : 2;
    }

    public int getOutputSize(PemObject obj) {
        int size = 2 * (obj.getType().length() + 10 + this.nlLength) + 6 + 4;
        if (!obj.getHeaders().isEmpty()) {
            for (PemHeader hdr : obj.getHeaders()) {
                size += hdr.getName().length() + ": ".length() + hdr.getValue().length() + this.nlLength;
            }
            size += this.nlLength;
        }
        int dataLen = (obj.getContent().length + 2) / 3 * 4;
        return size += dataLen + (dataLen + 64 - 1) / 64 * this.nlLength;
    }

    public void writeObject(PemObjectGenerator objGen) throws IOException {
        PemObject obj = objGen.generate();
        this.writePreEncapsulationBoundary(obj.getType());
        if (!obj.getHeaders().isEmpty()) {
            for (PemHeader hdr : obj.getHeaders()) {
                this.write(hdr.getName());
                this.write(": ");
                this.write(hdr.getValue());
                this.newLine();
            }
            this.newLine();
        }
        this.writeEncoded(obj.getContent());
        this.writePostEncapsulationBoundary(obj.getType());
    }

    private void writeEncoded(byte[] bytes) throws IOException {
        bytes = Base64.encode(bytes);
        for (int i = 0; i < bytes.length; i += this.buf.length) {
            int index;
            for (index = 0; index != this.buf.length && i + index < bytes.length; ++index) {
                this.buf[index] = (char)bytes[i + index];
            }
            this.write(this.buf, 0, index);
            this.newLine();
        }
    }

    private void writePreEncapsulationBoundary(String type) throws IOException {
        this.write("-----BEGIN " + type + "-----");
        this.newLine();
    }

    private void writePostEncapsulationBoundary(String type) throws IOException {
        this.write("-----END " + type + "-----");
        this.newLine();
    }
}

