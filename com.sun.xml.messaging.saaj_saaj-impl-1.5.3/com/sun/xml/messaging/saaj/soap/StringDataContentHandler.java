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
import com.sun.xml.messaging.saaj.packaging.mime.internet.MimeUtility;
import java.awt.datatransfer.DataFlavor;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import javax.activation.ActivationDataFlavor;
import javax.activation.DataContentHandler;
import javax.activation.DataSource;

public class StringDataContentHandler
implements DataContentHandler {
    private static ActivationDataFlavor myDF = new ActivationDataFlavor(String.class, "text/plain", "Text String");

    protected ActivationDataFlavor getDF() {
        return myDF;
    }

    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[]{this.getDF()};
    }

    public Object getTransferData(DataFlavor df, DataSource ds) throws IOException {
        if (this.getDF().equals(df)) {
            return this.getContent(ds);
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Object getContent(DataSource ds) throws IOException {
        String enc = null;
        InputStreamReader is = null;
        try {
            enc = this.getCharset(ds.getContentType());
            is = new InputStreamReader(ds.getInputStream(), enc);
        }
        catch (IllegalArgumentException iex) {
            throw new UnsupportedEncodingException(enc);
        }
        try {
            int count;
            int pos = 0;
            char[] buf = new char[1024];
            while ((count = is.read(buf, pos, buf.length - pos)) != -1) {
                if ((pos += count) < buf.length) continue;
                int size = buf.length;
                size = size < 262144 ? (size += size) : (size += 262144);
                char[] tbuf = new char[size];
                System.arraycopy(buf, 0, tbuf, 0, pos);
                buf = tbuf;
            }
            String string = new String(buf, 0, pos);
            return string;
        }
        finally {
            try {
                is.close();
            }
            catch (IOException iOException) {}
        }
    }

    public void writeTo(Object obj, String type, OutputStream os) throws IOException {
        if (!(obj instanceof String)) {
            throw new IOException("\"" + this.getDF().getMimeType() + "\" DataContentHandler requires String object, was given object of type " + obj.getClass().toString());
        }
        String enc = null;
        OutputStreamWriter osw = null;
        try {
            enc = this.getCharset(type);
            osw = new OutputStreamWriter(os, enc);
        }
        catch (IllegalArgumentException iex) {
            throw new UnsupportedEncodingException(enc);
        }
        String s = (String)obj;
        osw.write(s, 0, s.length());
        osw.flush();
    }

    private String getCharset(String type) {
        try {
            ContentType ct = new ContentType(type);
            String charset = ct.getParameter("charset");
            if (charset == null) {
                charset = "us-ascii";
            }
            return MimeUtility.javaCharset(charset);
        }
        catch (Exception ex) {
            return null;
        }
    }
}

