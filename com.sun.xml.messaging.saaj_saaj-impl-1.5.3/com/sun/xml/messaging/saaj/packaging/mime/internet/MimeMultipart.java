/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.DataSource
 */
package com.sun.xml.messaging.saaj.packaging.mime.internet;

import com.sun.xml.messaging.saaj.packaging.mime.MessagingException;
import com.sun.xml.messaging.saaj.packaging.mime.MultipartDataSource;
import com.sun.xml.messaging.saaj.packaging.mime.internet.ContentType;
import com.sun.xml.messaging.saaj.packaging.mime.internet.InternetHeaders;
import com.sun.xml.messaging.saaj.packaging.mime.internet.MimeBodyPart;
import com.sun.xml.messaging.saaj.packaging.mime.internet.SharedInputStream;
import com.sun.xml.messaging.saaj.packaging.mime.internet.UniqueValue;
import com.sun.xml.messaging.saaj.packaging.mime.util.ASCIIUtility;
import com.sun.xml.messaging.saaj.packaging.mime.util.LineInputStream;
import com.sun.xml.messaging.saaj.packaging.mime.util.OutputUtil;
import com.sun.xml.messaging.saaj.util.ByteOutputStream;
import com.sun.xml.messaging.saaj.util.FinalArrayList;
import com.sun.xml.messaging.saaj.util.SAAJUtil;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.activation.DataSource;

public class MimeMultipart {
    protected DataSource ds = null;
    protected boolean parsed = true;
    protected FinalArrayList<MimeBodyPart> parts = new FinalArrayList();
    protected ContentType contentType;
    protected MimeBodyPart parent;
    protected static final boolean ignoreMissingEndBoundary = SAAJUtil.getSystemBoolean("saaj.mime.multipart.ignoremissingendboundary");

    public MimeMultipart() {
        this("mixed");
    }

    public MimeMultipart(String subtype) {
        String boundary = UniqueValue.getUniqueBoundaryValue();
        this.contentType = new ContentType("multipart", subtype, null);
        this.contentType.setParameter("boundary", boundary);
    }

    public MimeMultipart(DataSource ds, ContentType ct) throws MessagingException {
        this.parsed = false;
        this.ds = ds;
        this.contentType = ct == null ? new ContentType(ds.getContentType()) : ct;
    }

    public void setSubType(String subtype) {
        this.contentType.setSubType(subtype);
    }

    public int getCount() throws MessagingException {
        this.parse();
        if (this.parts == null) {
            return 0;
        }
        return this.parts.size();
    }

    public MimeBodyPart getBodyPart(int index) throws MessagingException {
        this.parse();
        if (this.parts == null) {
            throw new IndexOutOfBoundsException("No such BodyPart");
        }
        return (MimeBodyPart)this.parts.get(index);
    }

    public MimeBodyPart getBodyPart(String CID) throws MessagingException {
        this.parse();
        int count = this.getCount();
        for (int i = 0; i < count; ++i) {
            String sNoAngle;
            MimeBodyPart part = this.getBodyPart(i);
            String s = part.getContentID();
            String string = sNoAngle = s != null ? s.replaceFirst("^<", "").replaceFirst(">$", "") : null;
            if (s == null || !s.equals(CID) && !CID.equals(sNoAngle)) continue;
            return part;
        }
        return null;
    }

    protected void updateHeaders() throws MessagingException {
        for (int i = 0; i < this.parts.size(); ++i) {
            ((MimeBodyPart)this.parts.get(i)).updateHeaders();
        }
    }

    public void writeTo(OutputStream os) throws IOException, MessagingException {
        this.parse();
        String boundary = "--" + this.contentType.getParameter("boundary");
        for (int i = 0; i < this.parts.size(); ++i) {
            OutputUtil.writeln(boundary, os);
            this.getBodyPart(i).writeTo(os);
            OutputUtil.writeln(os);
        }
        OutputUtil.writeAsAscii(boundary, os);
        OutputUtil.writeAsAscii("--", os);
        os.flush();
    }

    protected void parse() throws MessagingException {
        InputStream in;
        if (this.parsed) {
            return;
        }
        SharedInputStream sin = null;
        long start = 0L;
        long end = 0L;
        boolean foundClosingBoundary = false;
        try {
            in = this.ds.getInputStream();
            if (!(in instanceof ByteArrayInputStream || in instanceof BufferedInputStream || in instanceof SharedInputStream)) {
                in = new BufferedInputStream(in);
            }
        }
        catch (Exception ex) {
            throw new MessagingException("No inputstream from datasource");
        }
        if (in instanceof SharedInputStream) {
            sin = (SharedInputStream)((Object)in);
        }
        String boundary = "--" + this.contentType.getParameter("boundary");
        byte[] bndbytes = ASCIIUtility.getBytes(boundary);
        int bl = bndbytes.length;
        try (ByteOutputStream buf = null;){
            String line;
            LineInputStream lin = new LineInputStream(in);
            while ((line = lin.readLine()) != null) {
                char c;
                int i;
                for (i = line.length() - 1; i >= 0 && ((c = line.charAt(i)) == ' ' || c == '\t'); --i) {
                }
                if (!(line = line.substring(0, i + 1)).equals(boundary)) continue;
                break;
            }
            if (line == null) {
                throw new MessagingException("Missing start boundary");
            }
            boolean done = false;
            while (!done) {
                InternetHeaders headers = null;
                if (sin != null) {
                    start = sin.getPosition();
                    while ((line = lin.readLine()) != null && line.length() > 0) {
                    }
                    if (line == null) {
                        if (!ignoreMissingEndBoundary) {
                            throw new MessagingException("Missing End Boundary for Mime Package : EOF while skipping headers");
                        }
                        break;
                    }
                } else {
                    headers = this.createInternetHeaders(in);
                }
                if (!in.markSupported()) {
                    throw new MessagingException("Stream doesn't support mark");
                }
                buf = null;
                if (sin == null) {
                    buf = new ByteOutputStream();
                }
                boolean bol = true;
                int eol1 = -1;
                int eol2 = -1;
                while (true) {
                    int b;
                    if (bol) {
                        int i;
                        in.mark(bl + 4 + 1000);
                        for (i = 0; i < bl && in.read() == bndbytes[i]; ++i) {
                        }
                        if (i == bl) {
                            int b2 = in.read();
                            if (b2 == 45 && in.read() == 45) {
                                done = true;
                                foundClosingBoundary = true;
                                break;
                            }
                            while (b2 == 32 || b2 == 9) {
                                b2 = in.read();
                            }
                            if (b2 == 10) break;
                            if (b2 == 13) {
                                in.mark(1);
                                if (in.read() == 10) break;
                                in.reset();
                                break;
                            }
                        }
                        in.reset();
                        if (buf != null && eol1 != -1) {
                            buf.write(eol1);
                            if (eol2 != -1) {
                                buf.write(eol2);
                            }
                            eol2 = -1;
                            eol1 = -1;
                        }
                    }
                    if ((b = in.read()) < 0) {
                        done = true;
                        break;
                    }
                    if (b == 13 || b == 10) {
                        bol = true;
                        if (sin != null) {
                            end = sin.getPosition() - 1L;
                        }
                        eol1 = b;
                        if (b != 13) continue;
                        in.mark(1);
                        b = in.read();
                        if (b == 10) {
                            eol2 = b;
                            continue;
                        }
                        in.reset();
                        continue;
                    }
                    bol = false;
                    if (buf == null) continue;
                    buf.write(b);
                }
                MimeBodyPart part = sin != null ? this.createMimeBodyPart(sin.newStream(start, end)) : this.createMimeBodyPart(headers, buf.getBytes(), buf.getCount());
                this.addBodyPart(part);
            }
        }
        if (!ignoreMissingEndBoundary && !foundClosingBoundary && sin == null) {
            throw new MessagingException("Missing End Boundary for Mime Package : EOF while skipping headers");
        }
        this.parsed = true;
    }

    protected InternetHeaders createInternetHeaders(InputStream is) throws MessagingException {
        return new InternetHeaders(is);
    }

    protected MimeBodyPart createMimeBodyPart(InternetHeaders headers, byte[] content, int len) {
        return new MimeBodyPart(headers, content, len);
    }

    protected MimeBodyPart createMimeBodyPart(InputStream is) throws MessagingException {
        return new MimeBodyPart(is);
    }

    protected void setMultipartDataSource(MultipartDataSource mp) throws MessagingException {
        this.contentType = new ContentType(mp.getContentType());
        int count = mp.getCount();
        for (int i = 0; i < count; ++i) {
            this.addBodyPart(mp.getBodyPart(i));
        }
    }

    public ContentType getContentType() {
        return this.contentType;
    }

    public boolean removeBodyPart(MimeBodyPart part) throws MessagingException {
        if (this.parts == null) {
            throw new MessagingException("No such body part");
        }
        boolean ret = this.parts.remove(part);
        part.setParent(null);
        return ret;
    }

    public void removeBodyPart(int index) {
        if (this.parts == null) {
            throw new IndexOutOfBoundsException("No such BodyPart");
        }
        MimeBodyPart part = (MimeBodyPart)this.parts.get(index);
        this.parts.remove(index);
        part.setParent(null);
    }

    public synchronized void addBodyPart(MimeBodyPart part) {
        if (this.parts == null) {
            this.parts = new FinalArrayList();
        }
        this.parts.add(part);
        part.setParent(this);
    }

    public synchronized void addBodyPart(MimeBodyPart part, int index) {
        if (this.parts == null) {
            this.parts = new FinalArrayList();
        }
        this.parts.add(index, part);
        part.setParent(this);
    }

    MimeBodyPart getParent() {
        return this.parent;
    }

    void setParent(MimeBodyPart parent) {
        this.parent = parent;
    }
}

