/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.pdf.ByteBuffer;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfEncodings;
import com.lowagie.text.pdf.PdfEncryption;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfWriter;
import java.io.IOException;
import java.io.OutputStream;

public class PdfString
extends PdfObject {
    protected String value = "";
    protected String originalValue = null;
    protected String encoding = "PDF";
    protected int objNum = 0;
    protected int objGen = 0;
    protected boolean hexWriting = false;

    public PdfString() {
        super(3);
    }

    public PdfString(String value) {
        super(3);
        this.value = value;
    }

    public PdfString(String value, String encoding) {
        super(3);
        this.value = value;
        this.encoding = encoding;
    }

    public PdfString(byte[] bytes) {
        super(3);
        this.value = PdfEncodings.convertToString(bytes, null);
        this.encoding = "";
    }

    @Override
    public void toPdf(PdfWriter writer, OutputStream os) throws IOException {
        byte[] b = this.getBytes();
        PdfEncryption crypto = null;
        if (writer != null) {
            crypto = writer.getEncryption();
        }
        if (crypto != null && !crypto.isEmbeddedFilesOnly()) {
            b = crypto.encryptByteArray(b);
        }
        if (this.hexWriting) {
            ByteBuffer buf = new ByteBuffer();
            buf.append('<');
            int len = b.length;
            for (byte b1 : b) {
                buf.appendHex(b1);
            }
            buf.append('>');
            os.write(buf.toByteArray());
        } else {
            os.write(PdfContentByte.escapeString(b));
        }
    }

    @Override
    public String toString() {
        return this.value;
    }

    @Override
    public byte[] getBytes() {
        if (this.bytes == null) {
            this.bytes = this.encoding != null && this.encoding.equals("UnicodeBig") && PdfEncodings.isPdfDocEncoding(this.value) ? PdfEncodings.convertToBytes(this.value, "PDF") : PdfEncodings.convertToBytes(this.value, this.encoding);
        }
        return this.bytes;
    }

    public String toUnicodeString() {
        if (this.encoding != null && this.encoding.length() != 0) {
            return this.value;
        }
        this.getBytes();
        if (this.bytes.length >= 2 && this.bytes[0] == -2 && this.bytes[1] == -1) {
            return PdfEncodings.convertToString(this.bytes, "UnicodeBig");
        }
        return PdfEncodings.convertToString(this.bytes, "PDF");
    }

    public String getEncoding() {
        return this.encoding;
    }

    void setObjNum(int objNum, int objGen) {
        this.objNum = objNum;
        this.objGen = objGen;
    }

    void decrypt(PdfReader reader) {
        PdfEncryption decrypt = reader.getDecrypt();
        if (decrypt != null) {
            this.originalValue = this.value;
            decrypt.setHashKey(this.objNum, this.objGen);
            this.bytes = PdfEncodings.convertToBytes(this.value, null);
            this.bytes = decrypt.decryptByteArray(this.bytes);
            this.value = PdfEncodings.convertToString(this.bytes, null);
        }
    }

    public byte[] getOriginalBytes() {
        if (this.originalValue == null) {
            return this.getBytes();
        }
        return PdfEncodings.convertToBytes(this.originalValue, null);
    }

    public char[] getOriginalChars() {
        char[] chars;
        if (this.encoding == null || this.encoding.length() == 0) {
            byte[] bytes = this.getOriginalBytes();
            chars = new char[bytes.length];
            for (int i = 0; i < bytes.length; ++i) {
                chars[i] = (char)(bytes[i] & 0xFF);
            }
        } else {
            chars = new char[]{};
        }
        return chars;
    }

    public PdfString setHexWriting(boolean hexWriting) {
        this.hexWriting = hexWriting;
        return this;
    }

    public boolean isHexWriting() {
        return this.hexWriting;
    }
}

