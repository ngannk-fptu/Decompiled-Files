/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.ActivationDataFlavor
 *  javax.activation.DataContentHandler
 *  javax.activation.DataSource
 *  javax.mail.MessagingException
 *  javax.mail.Multipart
 *  javax.mail.Part
 *  javax.mail.internet.ContentType
 *  javax.mail.internet.MimeBodyPart
 *  javax.mail.internet.MimeMultipart
 */
package org.bouncycastle.mail.smime.handlers;

import java.awt.datatransfer.DataFlavor;
import java.io.BufferedInputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import javax.activation.ActivationDataFlavor;
import javax.activation.DataContentHandler;
import javax.activation.DataSource;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.internet.ContentType;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import org.bouncycastle.mail.smime.SMIMEStreamingProcessor;
import org.bouncycastle.mail.smime.SMIMEUtil;

public class multipart_signed
implements DataContentHandler {
    private static final ActivationDataFlavor ADF = new ActivationDataFlavor(MimeMultipart.class, "multipart/signed", "Multipart Signed");
    private static final DataFlavor[] DFS = new DataFlavor[]{ADF};

    public Object getContent(DataSource ds) throws IOException {
        try {
            return new MimeMultipart(ds);
        }
        catch (MessagingException ex) {
            return null;
        }
    }

    public Object getTransferData(DataFlavor df, DataSource ds) throws IOException {
        if (ADF.equals(df)) {
            return this.getContent(ds);
        }
        return null;
    }

    public DataFlavor[] getTransferDataFlavors() {
        return DFS;
    }

    public void writeTo(Object obj, String _mimeType, OutputStream os) throws IOException {
        if (obj instanceof MimeMultipart) {
            try {
                this.outputBodyPart(os, obj);
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

    private void outputBodyPart(OutputStream out, Object bodyPart) throws MessagingException, IOException {
        Object content;
        if (bodyPart instanceof Multipart) {
            Multipart mp = (Multipart)bodyPart;
            ContentType contentType = new ContentType(mp.getContentType());
            String boundary = "--" + contentType.getParameter("boundary");
            LineOutputStream lOut = new LineOutputStream(out);
            for (int i = 0; i < mp.getCount(); ++i) {
                lOut.writeln(boundary);
                this.outputBodyPart(out, mp.getBodyPart(i));
                lOut.writeln();
            }
            lOut.writeln(boundary + "--");
            return;
        }
        MimeBodyPart mimePart = (MimeBodyPart)bodyPart;
        if (SMIMEUtil.isMultipartContent((Part)mimePart) && (content = mimePart.getContent()) instanceof Multipart) {
            Multipart mp = (Multipart)content;
            ContentType contentType = new ContentType(mp.getContentType());
            String boundary = "--" + contentType.getParameter("boundary");
            LineOutputStream lOut = new LineOutputStream(out);
            Enumeration headers = mimePart.getAllHeaderLines();
            while (headers.hasMoreElements()) {
                lOut.writeln((String)headers.nextElement());
            }
            lOut.writeln();
            multipart_signed.outputPreamble(lOut, mimePart, boundary);
            this.outputBodyPart(out, mp);
            return;
        }
        mimePart.writeTo(out);
    }

    static void outputPreamble(LineOutputStream lOut, MimeBodyPart part, String boundary) throws MessagingException, IOException {
        String line;
        InputStream in;
        try {
            in = part.getRawInputStream();
        }
        catch (MessagingException e) {
            return;
        }
        while ((line = multipart_signed.readLine(in)) != null && !line.equals(boundary)) {
            lOut.writeln(line);
        }
        in.close();
        if (line == null) {
            throw new MessagingException("no boundary found");
        }
    }

    private static String readLine(InputStream in) throws IOException {
        int ch;
        StringBuffer b = new StringBuffer();
        while ((ch = in.read()) >= 0 && ch != 10) {
            if (ch == 13) continue;
            b.append((char)ch);
        }
        if (ch < 0) {
            return null;
        }
        return b.toString();
    }

    private static class LineOutputStream
    extends FilterOutputStream {
        private static byte[] newline = new byte[2];

        public LineOutputStream(OutputStream outputstream) {
            super(outputstream);
        }

        public void writeln(String s) throws MessagingException {
            try {
                byte[] abyte0 = LineOutputStream.getBytes(s);
                this.out.write(abyte0);
                this.out.write(newline);
            }
            catch (Exception exception) {
                throw new MessagingException("IOException", exception);
            }
        }

        public void writeln() throws MessagingException {
            try {
                this.out.write(newline);
            }
            catch (Exception exception) {
                throw new MessagingException("IOException", exception);
            }
        }

        private static byte[] getBytes(String s) {
            char[] ac = s.toCharArray();
            int i = ac.length;
            byte[] abyte0 = new byte[i];
            int j = 0;
            while (j < i) {
                abyte0[j] = (byte)ac[j++];
            }
            return abyte0;
        }

        static {
            LineOutputStream.newline[0] = 13;
            LineOutputStream.newline[1] = 10;
        }
    }
}

