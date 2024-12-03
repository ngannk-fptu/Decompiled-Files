/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.mail.BodyPart
 *  javax.mail.MessagingException
 *  javax.mail.Multipart
 *  javax.mail.Part
 *  javax.mail.internet.ContentType
 *  javax.mail.internet.MimeBodyPart
 *  javax.mail.internet.MimeMultipart
 *  org.bouncycastle.asn1.cms.IssuerAndSerialNumber
 *  org.bouncycastle.cert.jcajce.JcaX509CertificateHolder
 *  org.bouncycastle.cms.CMSTypedStream
 *  org.bouncycastle.util.Strings
 */
package org.bouncycastle.mail.smime;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.internet.ContentType;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import org.bouncycastle.asn1.cms.IssuerAndSerialNumber;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.cms.CMSTypedStream;
import org.bouncycastle.mail.smime.SMIMEException;
import org.bouncycastle.mail.smime.util.CRLFOutputStream;
import org.bouncycastle.mail.smime.util.FileBackedMimeBodyPart;
import org.bouncycastle.util.Strings;

public class SMIMEUtil {
    private static final String MULTIPART = "multipart";
    private static final int BUF_SIZE = 32760;

    public static boolean isMultipartContent(Part part) throws MessagingException {
        String partType = Strings.toLowerCase((String)part.getContentType());
        return partType.startsWith(MULTIPART);
    }

    static boolean isCanonicalisationRequired(MimeBodyPart bodyPart, String defaultContentTransferEncoding) throws MessagingException {
        String[] cte = bodyPart.getHeader("Content-Transfer-Encoding");
        String contentTransferEncoding = cte == null ? defaultContentTransferEncoding : cte[0];
        return !contentTransferEncoding.equalsIgnoreCase("binary");
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
        while ((line = SMIMEUtil.readLine(in)) != null && !line.equals(boundary)) {
            lOut.writeln(line);
        }
        in.close();
        if (line == null) {
            throw new MessagingException("no boundary found");
        }
    }

    static void outputPostamble(LineOutputStream lOut, MimeBodyPart part, int count, String boundary) throws MessagingException, IOException {
        String line;
        InputStream in;
        try {
            in = part.getRawInputStream();
        }
        catch (MessagingException e) {
            return;
        }
        int boundaries = count + 1;
        while (!((line = SMIMEUtil.readLine(in)) == null || line.startsWith(boundary) && --boundaries == 0)) {
        }
        while ((line = SMIMEUtil.readLine(in)) != null) {
            lOut.writeln(line);
        }
        in.close();
        if (boundaries != 0) {
            throw new MessagingException("all boundaries not found for: " + boundary);
        }
    }

    static void outputPostamble(LineOutputStream lOut, BodyPart parent, String parentBoundary, BodyPart part) throws MessagingException, IOException {
        String line;
        InputStream in;
        try {
            in = ((MimeBodyPart)parent).getRawInputStream();
        }
        catch (MessagingException e) {
            return;
        }
        MimeMultipart multipart = (MimeMultipart)part.getContent();
        ContentType contentType = new ContentType(multipart.getContentType());
        String boundary = "--" + contentType.getParameter("boundary");
        int count = multipart.getCount() + 1;
        while (count != 0 && (line = SMIMEUtil.readLine(in)) != null) {
            if (!line.startsWith(boundary)) continue;
            --count;
        }
        while ((line = SMIMEUtil.readLine(in)) != null && !line.startsWith(parentBoundary)) {
            lOut.writeln(line);
        }
        in.close();
    }

    private static String readLine(InputStream in) throws IOException {
        int ch;
        StringBuffer b = new StringBuffer();
        while ((ch = in.read()) >= 0 && ch != 10) {
            if (ch == 13) continue;
            b.append((char)ch);
        }
        if (ch < 0 && b.length() == 0) {
            return null;
        }
        return b.toString();
    }

    static void outputBodyPart(OutputStream out, boolean topLevel, BodyPart bodyPart, String defaultContentTransferEncoding) throws MessagingException, IOException {
        if (bodyPart instanceof MimeBodyPart) {
            int len;
            InputStream inRaw;
            MimeBodyPart mimePart = (MimeBodyPart)bodyPart;
            String[] cte = mimePart.getHeader("Content-Transfer-Encoding");
            if (SMIMEUtil.isMultipartContent((Part)mimePart)) {
                Object content = bodyPart.getContent();
                Object mp = content instanceof Multipart ? (Multipart)content : new MimeMultipart(bodyPart.getDataHandler().getDataSource());
                ContentType contentType = new ContentType(mp.getContentType());
                String boundary = "--" + contentType.getParameter("boundary");
                LineOutputStream lOut = new LineOutputStream(out);
                Enumeration headers = mimePart.getAllHeaderLines();
                while (headers.hasMoreElements()) {
                    String header = (String)headers.nextElement();
                    lOut.writeln(header);
                }
                lOut.writeln();
                SMIMEUtil.outputPreamble(lOut, mimePart, boundary);
                for (int i = 0; i < mp.getCount(); ++i) {
                    lOut.writeln(boundary);
                    BodyPart part = mp.getBodyPart(i);
                    SMIMEUtil.outputBodyPart(out, false, part, defaultContentTransferEncoding);
                    if (!SMIMEUtil.isMultipartContent((Part)part)) {
                        lOut.writeln();
                        continue;
                    }
                    SMIMEUtil.outputPostamble(lOut, (BodyPart)mimePart, boundary, part);
                }
                lOut.writeln(boundary + "--");
                if (topLevel) {
                    SMIMEUtil.outputPostamble(lOut, mimePart, mp.getCount(), boundary);
                }
                return;
            }
            String contentTransferEncoding = cte == null ? defaultContentTransferEncoding : cte[0];
            if (!contentTransferEncoding.equalsIgnoreCase("base64") && !contentTransferEncoding.equalsIgnoreCase("quoted-printable")) {
                if (!contentTransferEncoding.equalsIgnoreCase("binary")) {
                    out = new CRLFOutputStream(out);
                }
                bodyPart.writeTo(out);
                out.flush();
                return;
            }
            boolean base64 = contentTransferEncoding.equalsIgnoreCase("base64");
            try {
                inRaw = mimePart.getRawInputStream();
            }
            catch (MessagingException e) {
                out = new CRLFOutputStream(out);
                bodyPart.writeTo(out);
                out.flush();
                return;
            }
            LineOutputStream outLine = new LineOutputStream(out);
            Enumeration e = mimePart.getAllHeaderLines();
            while (e.hasMoreElements()) {
                String header = (String)e.nextElement();
                outLine.writeln(header);
            }
            outLine.writeln();
            outLine.flush();
            FilterOutputStream outCRLF = base64 ? new Base64CRLFOutputStream(out) : new CRLFOutputStream(out);
            byte[] buf = new byte[32760];
            while ((len = inRaw.read(buf, 0, buf.length)) > 0) {
                ((OutputStream)outCRLF).write(buf, 0, len);
            }
            inRaw.close();
            ((OutputStream)outCRLF).flush();
        } else {
            if (!defaultContentTransferEncoding.equalsIgnoreCase("binary")) {
                out = new CRLFOutputStream(out);
            }
            bodyPart.writeTo(out);
            out.flush();
        }
    }

    public static MimeBodyPart toMimeBodyPart(byte[] content) throws SMIMEException {
        return SMIMEUtil.toMimeBodyPart(new ByteArrayInputStream(content));
    }

    public static MimeBodyPart toMimeBodyPart(InputStream content) throws SMIMEException {
        try {
            return new MimeBodyPart(content);
        }
        catch (MessagingException e) {
            throw new SMIMEException("exception creating body part.", (Exception)((Object)e));
        }
    }

    static FileBackedMimeBodyPart toWriteOnceBodyPart(CMSTypedStream content) throws SMIMEException {
        try {
            return new WriteOnceFileBackedMimeBodyPart(content.getContentStream(), File.createTempFile("bcMail", ".mime"));
        }
        catch (IOException e) {
            throw new SMIMEException("IOException creating tmp file:" + e.getMessage(), e);
        }
        catch (MessagingException e) {
            throw new SMIMEException("can't create part: " + (Object)((Object)e), (Exception)((Object)e));
        }
    }

    public static FileBackedMimeBodyPart toMimeBodyPart(CMSTypedStream content) throws SMIMEException {
        try {
            return SMIMEUtil.toMimeBodyPart(content, File.createTempFile("bcMail", ".mime"));
        }
        catch (IOException e) {
            throw new SMIMEException("IOException creating tmp file:" + e.getMessage(), e);
        }
    }

    public static FileBackedMimeBodyPart toMimeBodyPart(CMSTypedStream content, File file) throws SMIMEException {
        try {
            return new FileBackedMimeBodyPart(content.getContentStream(), file);
        }
        catch (IOException e) {
            throw new SMIMEException("can't save content to file: " + e, e);
        }
        catch (MessagingException e) {
            throw new SMIMEException("can't create part: " + (Object)((Object)e), (Exception)((Object)e));
        }
    }

    public static IssuerAndSerialNumber createIssuerAndSerialNumberFor(X509Certificate cert) throws CertificateParsingException {
        try {
            return new IssuerAndSerialNumber(new JcaX509CertificateHolder(cert).getIssuer(), cert.getSerialNumber());
        }
        catch (Exception e) {
            throw new CertificateParsingException("exception extracting issuer and serial number: " + e);
        }
    }

    static class Base64CRLFOutputStream
    extends FilterOutputStream {
        protected int lastb = -1;
        protected static byte[] newline = new byte[2];
        private boolean isCrlfStream;

        public Base64CRLFOutputStream(OutputStream outputstream) {
            super(outputstream);
        }

        @Override
        public void write(int i) throws IOException {
            if (i == 13) {
                this.out.write(newline);
            } else if (i == 10) {
                if (this.lastb != 13) {
                    if (!this.isCrlfStream || this.lastb != 10) {
                        this.out.write(newline);
                    }
                } else {
                    this.isCrlfStream = true;
                }
            } else {
                this.out.write(i);
            }
            this.lastb = i;
        }

        @Override
        public void write(byte[] buf) throws IOException {
            this.write(buf, 0, buf.length);
        }

        @Override
        public void write(byte[] buf, int off, int len) throws IOException {
            for (int i = off; i != off + len; ++i) {
                this.write(buf[i]);
            }
        }

        public void writeln() throws IOException {
            this.out.write(newline);
        }

        static {
            Base64CRLFOutputStream.newline[0] = 13;
            Base64CRLFOutputStream.newline[1] = 10;
        }
    }

    static class LineOutputStream
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

    private static class WriteOnceFileBackedMimeBodyPart
    extends FileBackedMimeBodyPart {
        public WriteOnceFileBackedMimeBodyPart(InputStream content, File file) throws MessagingException, IOException {
            super(content, file);
        }

        @Override
        public void writeTo(OutputStream out) throws MessagingException, IOException {
            super.writeTo(out);
            this.dispose();
        }
    }
}

