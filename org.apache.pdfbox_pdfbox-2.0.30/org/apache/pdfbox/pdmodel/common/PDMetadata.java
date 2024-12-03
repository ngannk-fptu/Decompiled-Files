/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.common;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.common.PDStream;

public class PDMetadata
extends PDStream {
    public PDMetadata(PDDocument document) {
        super(document);
        this.getCOSObject().setName(COSName.TYPE, "Metadata");
        this.getCOSObject().setName(COSName.SUBTYPE, "XML");
    }

    public PDMetadata(PDDocument doc, InputStream str) throws IOException {
        super(doc, str);
        this.getCOSObject().setName(COSName.TYPE, "Metadata");
        this.getCOSObject().setName(COSName.SUBTYPE, "XML");
    }

    public PDMetadata(COSStream str) {
        super(str);
    }

    public InputStream exportXMPMetadata() throws IOException {
        return this.createInputStream();
    }

    public void importXMPMetadata(byte[] xmp) throws IOException {
        OutputStream os = this.createOutputStream();
        os.write(xmp);
        os.close();
    }
}

