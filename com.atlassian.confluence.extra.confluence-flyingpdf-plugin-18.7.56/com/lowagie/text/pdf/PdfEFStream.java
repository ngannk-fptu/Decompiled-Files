/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.pdf.OutputStreamCounter;
import com.lowagie.text.pdf.OutputStreamEncryption;
import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfEncryption;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNull;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfStream;
import com.lowagie.text.pdf.PdfWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

public class PdfEFStream
extends PdfStream {
    public PdfEFStream(InputStream in, PdfWriter writer) {
        super(in, writer);
    }

    public PdfEFStream(byte[] fileStore) {
        super(fileStore);
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
        if (crypto != null && crypto.isEmbeddedFilesOnly()) {
            filter = new PdfArray();
            PdfArray decodeparms = new PdfArray();
            PdfDictionary crypt = new PdfDictionary();
            crypt.put(PdfName.NAME, PdfName.STDCF);
            ((PdfArray)filter).add(PdfName.CRYPT);
            decodeparms.add(crypt);
            if (this.compressed) {
                ((PdfArray)filter).add(PdfName.FLATEDECODE);
                decodeparms.add(new PdfNull());
            }
            this.put(PdfName.FILTER, filter);
            this.put(PdfName.DECODEPARMS, decodeparms);
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
            if (crypto != null) {
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
        } else if (crypto == null) {
            if (this.streamBytes != null) {
                this.streamBytes.writeTo(os);
            } else {
                os.write(this.bytes);
            }
        } else {
            byte[] b = this.streamBytes != null ? crypto.encryptByteArray(this.streamBytes.toByteArray()) : crypto.encryptByteArray(this.bytes);
            os.write(b);
        }
        os.write(ENDSTREAM);
    }
}

