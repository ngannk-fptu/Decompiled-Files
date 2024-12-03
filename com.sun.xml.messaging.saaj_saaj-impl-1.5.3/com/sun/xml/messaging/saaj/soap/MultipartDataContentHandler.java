/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.ActivationDataFlavor
 *  javax.activation.DataContentHandler
 *  javax.activation.DataSource
 */
package com.sun.xml.messaging.saaj.soap;

import com.sun.xml.messaging.saaj.packaging.mime.internet.ContentType;
import com.sun.xml.messaging.saaj.packaging.mime.internet.MimeMultipart;
import com.sun.xml.messaging.saaj.util.ByteOutputStream;
import java.awt.datatransfer.DataFlavor;
import java.io.IOException;
import java.io.OutputStream;
import javax.activation.ActivationDataFlavor;
import javax.activation.DataContentHandler;
import javax.activation.DataSource;

public class MultipartDataContentHandler
implements DataContentHandler {
    private ActivationDataFlavor myDF = new ActivationDataFlavor(MimeMultipart.class, "multipart/mixed", "Multipart");

    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[]{this.myDF};
    }

    public Object getTransferData(DataFlavor df, DataSource ds) {
        if (this.myDF.equals(df)) {
            return this.getContent(ds);
        }
        return null;
    }

    public Object getContent(DataSource ds) {
        try {
            return new MimeMultipart(ds, new ContentType(ds.getContentType()));
        }
        catch (Exception e) {
            return null;
        }
    }

    public void writeTo(Object obj, String mimeType, OutputStream os) throws IOException {
        if (obj instanceof MimeMultipart) {
            try {
                ByteOutputStream baos = null;
                if (!(os instanceof ByteOutputStream)) {
                    throw new IOException("Input Stream expected to be a com.sun.xml.messaging.saaj.util.ByteOutputStream, but found " + os.getClass().getName());
                }
                baos = (ByteOutputStream)os;
                ((MimeMultipart)obj).writeTo(baos);
            }
            catch (Exception e) {
                throw new IOException(e.toString());
            }
        }
    }
}

