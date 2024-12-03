/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.ActivationDataFlavor
 *  javax.activation.DataContentHandler
 *  javax.activation.DataSource
 *  javax.mail.MessagingException
 *  javax.mail.internet.MimeBodyPart
 */
package org.bouncycastle.mail.smime.handlers;

import java.awt.datatransfer.DataFlavor;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.activation.ActivationDataFlavor;
import javax.activation.DataContentHandler;
import javax.activation.DataSource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import org.bouncycastle.mail.smime.SMIMEStreamingProcessor;

public class PKCS7ContentHandler
implements DataContentHandler {
    private final ActivationDataFlavor _adf;
    private final DataFlavor[] _dfs;

    PKCS7ContentHandler(ActivationDataFlavor adf, DataFlavor[] dfs) {
        this._adf = adf;
        this._dfs = dfs;
    }

    public Object getContent(DataSource ds) throws IOException {
        return ds.getInputStream();
    }

    public Object getTransferData(DataFlavor df, DataSource ds) throws IOException {
        if (this._adf.equals(df)) {
            return this.getContent(ds);
        }
        return null;
    }

    public DataFlavor[] getTransferDataFlavors() {
        return this._dfs;
    }

    public void writeTo(Object obj, String mimeType, OutputStream os) throws IOException {
        if (obj instanceof MimeBodyPart) {
            try {
                ((MimeBodyPart)obj).writeTo(os);
            }
            catch (MessagingException ex) {
                throw new IOException(ex.getMessage());
            }
        } else if (obj instanceof byte[]) {
            os.write((byte[])obj);
        } else if (obj instanceof InputStream) {
            int b;
            InputStream in = (InputStream)obj;
            if (!(in instanceof BufferedInputStream)) {
                in = new BufferedInputStream(in);
            }
            while ((b = in.read()) >= 0) {
                os.write(b);
            }
            in.close();
        } else if (obj instanceof SMIMEStreamingProcessor) {
            SMIMEStreamingProcessor processor = (SMIMEStreamingProcessor)obj;
            processor.write(os);
        } else {
            throw new IOException("unknown object in writeTo " + obj);
        }
    }
}

