/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.DocWriter;
import com.lowagie.text.Document;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.error_messages.MessageLocalization;
import com.lowagie.text.pdf.OutputStreamCounter;
import com.lowagie.text.pdf.OutputStreamEncryption;
import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfEncryption;
import com.lowagie.text.pdf.PdfIndirectReference;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

public class PdfStream
extends PdfDictionary {
    public static final int DEFAULT_COMPRESSION = -1;
    public static final int NO_COMPRESSION = 0;
    public static final int BEST_SPEED = 1;
    public static final int BEST_COMPRESSION = 9;
    protected boolean compressed = false;
    protected int compressionLevel = 0;
    protected ByteArrayOutputStream streamBytes = null;
    protected InputStream inputStream;
    protected PdfIndirectReference ref;
    protected long inputStreamLength = -1L;
    protected PdfWriter writer;
    protected long rawLength;
    static final byte[] STARTSTREAM = DocWriter.getISOBytes("stream\n");
    static final byte[] ENDSTREAM = DocWriter.getISOBytes("\nendstream");
    static final int SIZESTREAM = STARTSTREAM.length + ENDSTREAM.length;

    public PdfStream(byte[] bytes) {
        this.type = 7;
        this.bytes = bytes;
        this.rawLength = bytes.length;
        this.put(PdfName.LENGTH, new PdfNumber(bytes.length));
    }

    public PdfStream(InputStream inputStream, PdfWriter writer) {
        this.type = 7;
        this.inputStream = inputStream;
        this.writer = writer;
        this.ref = writer.getPdfIndirectReference();
        this.put(PdfName.LENGTH, this.ref);
    }

    protected PdfStream() {
        this.type = 7;
    }

    public void writeLength() throws IOException {
        if (this.inputStream == null) {
            throw new UnsupportedOperationException(MessageLocalization.getComposedMessage("writelength.can.only.be.called.in.a.contructed.pdfstream.inputstream.pdfwriter"));
        }
        if (this.inputStreamLength == -1L) {
            throw new IOException(MessageLocalization.getComposedMessage("writelength.can.only.be.called.after.output.of.the.stream.body"));
        }
        this.writer.addToBody((PdfObject)new PdfNumber(this.inputStreamLength), this.ref, false);
    }

    public long getRawLength() {
        return this.rawLength;
    }

    public void flateCompress() {
        this.flateCompress(-1);
    }

    public void flateCompress(int compressionLevel) {
        if (!Document.compress) {
            return;
        }
        if (this.compressed) {
            return;
        }
        this.compressionLevel = compressionLevel;
        if (this.inputStream != null) {
            this.compressed = true;
            return;
        }
        PdfObject filter = PdfReader.getPdfObject(this.get(PdfName.FILTER));
        if (filter != null) {
            if (filter.isName()) {
                if (PdfName.FLATEDECODE.equals(filter)) {
                    return;
                }
            } else if (filter.isArray()) {
                if (((PdfArray)filter).contains(PdfName.FLATEDECODE)) {
                    return;
                }
            } else {
                throw new RuntimeException(MessageLocalization.getComposedMessage("stream.could.not.be.compressed.filter.is.not.a.name.or.array"));
            }
        }
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            Deflater deflater = new Deflater(compressionLevel);
            DeflaterOutputStream zip = new DeflaterOutputStream((OutputStream)stream, deflater);
            if (this.streamBytes != null) {
                this.streamBytes.writeTo(zip);
            } else {
                zip.write(this.bytes);
            }
            zip.close();
            deflater.end();
            this.streamBytes = stream;
            this.bytes = null;
            this.put(PdfName.LENGTH, new PdfNumber(this.streamBytes.size()));
            if (filter == null) {
                this.put(PdfName.FILTER, PdfName.FLATEDECODE);
            } else {
                PdfArray filters = new PdfArray(filter);
                filters.add(PdfName.FLATEDECODE);
                this.put(PdfName.FILTER, filters);
            }
            this.compressed = true;
        }
        catch (IOException ioe) {
            throw new ExceptionConverter(ioe);
        }
    }

    protected void superToPdf(PdfWriter writer, OutputStream os) throws IOException {
        super.toPdf(writer, os);
    }

    @Override
    public void toPdf(PdfWriter writer, OutputStream os) throws IOException {
        PdfObject filter;
        if (this.inputStream != null && this.compressed) {
            this.put(PdfName.FILTER, PdfName.FLATEDECODE);
        }
        PdfEncryption crypto = null;
        if (writer != null) {
            crypto = writer.getEncryption();
        }
        if (crypto != null && (filter = this.get(PdfName.FILTER)) != null) {
            PdfArray a;
            if (PdfName.CRYPT.equals(filter)) {
                crypto = null;
            } else if (filter.isArray() && !(a = (PdfArray)filter).isEmpty() && PdfName.CRYPT.equals(a.getPdfObject(0))) {
                crypto = null;
            }
        }
        PdfObject nn = this.get(PdfName.LENGTH);
        if (crypto != null && nn != null && nn.isNumber()) {
            int sz = ((PdfNumber)nn).intValue();
            this.put(PdfName.LENGTH, new PdfNumber(crypto.calculateStreamSize(sz)));
            this.superToPdf(writer, os);
            this.put(PdfName.LENGTH, nn);
        } else {
            this.superToPdf(writer, os);
        }
        os.write(STARTSTREAM);
        if (this.inputStream != null) {
            int n;
            this.rawLength = 0L;
            DeflaterOutputStream def = null;
            OutputStreamCounter osc = new OutputStreamCounter(os);
            OutputStreamEncryption ose = null;
            OutputStream fout = osc;
            if (crypto != null && !crypto.isEmbeddedFilesOnly()) {
                ose = crypto.getEncryptionStream(fout);
                fout = ose;
            }
            Deflater deflater = null;
            if (this.compressed) {
                deflater = new Deflater(this.compressionLevel);
                def = new DeflaterOutputStream(fout, deflater, 32768);
                fout = def;
            }
            byte[] buf = new byte[4192];
            while ((n = this.inputStream.read(buf)) > 0) {
                ((OutputStream)fout).write(buf, 0, n);
                this.rawLength += (long)n;
            }
            if (def != null) {
                def.finish();
                deflater.end();
            }
            if (ose != null) {
                ose.finish();
            }
            this.inputStreamLength = osc.getCounter();
        } else if (crypto != null && !crypto.isEmbeddedFilesOnly()) {
            byte[] b = this.streamBytes != null ? crypto.encryptByteArray(this.streamBytes.toByteArray()) : crypto.encryptByteArray(this.bytes);
            os.write(b);
        } else if (this.streamBytes != null) {
            this.streamBytes.writeTo(os);
        } else {
            os.write(this.bytes);
        }
        os.write(ENDSTREAM);
    }

    public void writeContent(OutputStream os) throws IOException {
        if (this.streamBytes != null) {
            this.streamBytes.writeTo(os);
        } else if (this.bytes != null) {
            os.write(this.bytes);
        }
    }

    @Override
    public String toString() {
        if (this.get(PdfName.TYPE) == null) {
            return "Stream";
        }
        return "Stream of type: " + this.get(PdfName.TYPE);
    }
}

