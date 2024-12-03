/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.DataHandler
 *  javax.activation.DataSource
 *  org.jvnet.mimepull.Header
 *  org.jvnet.mimepull.MIMEPart
 */
package com.sun.xml.messaging.saaj.packaging.mime.internet;

import com.sun.xml.messaging.saaj.packaging.mime.MessagingException;
import com.sun.xml.messaging.saaj.packaging.mime.internet.ContentDisposition;
import com.sun.xml.messaging.saaj.packaging.mime.internet.ContentType;
import com.sun.xml.messaging.saaj.packaging.mime.internet.HeaderTokenizer;
import com.sun.xml.messaging.saaj.packaging.mime.internet.InternetHeaders;
import com.sun.xml.messaging.saaj.packaging.mime.internet.MimeMultipart;
import com.sun.xml.messaging.saaj.packaging.mime.internet.MimePartDataSource;
import com.sun.xml.messaging.saaj.packaging.mime.internet.MimeUtility;
import com.sun.xml.messaging.saaj.packaging.mime.internet.ParseException;
import com.sun.xml.messaging.saaj.packaging.mime.internet.SharedInputStream;
import com.sun.xml.messaging.saaj.packaging.mime.internet.hdr;
import com.sun.xml.messaging.saaj.packaging.mime.util.OutputUtil;
import com.sun.xml.messaging.saaj.util.ByteOutputStream;
import com.sun.xml.messaging.saaj.util.FinalArrayList;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import org.jvnet.mimepull.Header;
import org.jvnet.mimepull.MIMEPart;

public final class MimeBodyPart {
    public static final String ATTACHMENT = "attachment";
    public static final String INLINE = "inline";
    private static boolean setDefaultTextCharset = true;
    private DataHandler dh;
    private byte[] content;
    private int contentLength;
    private int start = 0;
    private InputStream contentStream;
    private final InternetHeaders headers;
    private MimeMultipart parent;
    private MIMEPart mimePart;

    public MimeBodyPart() {
        this.headers = new InternetHeaders();
    }

    public MimeBodyPart(InputStream is) throws MessagingException {
        if (!(is instanceof ByteArrayInputStream || is instanceof BufferedInputStream || is instanceof SharedInputStream)) {
            is = new BufferedInputStream(is);
        }
        this.headers = new InternetHeaders(is);
        if (is instanceof SharedInputStream) {
            SharedInputStream sis = (SharedInputStream)((Object)is);
            this.contentStream = sis.newStream(sis.getPosition(), -1L);
        } else {
            try (ByteOutputStream bos = null;){
                bos = new ByteOutputStream();
                bos.write(is);
                this.content = bos.getBytes();
                this.contentLength = bos.getCount();
            }
        }
    }

    public MimeBodyPart(InternetHeaders headers, byte[] content, int len) {
        this.headers = headers;
        this.content = content;
        this.contentLength = len;
    }

    public MimeBodyPart(InternetHeaders headers, byte[] content, int start, int len) {
        this.headers = headers;
        this.content = content;
        this.start = start;
        this.contentLength = len;
    }

    public MimeBodyPart(MIMEPart part) {
        this.mimePart = part;
        this.headers = new InternetHeaders();
        List hdrs = this.mimePart.getAllHeaders();
        for (Header hd : hdrs) {
            this.headers.addHeader(hd.getName(), hd.getValue());
        }
    }

    public MimeMultipart getParent() {
        return this.parent;
    }

    public void setParent(MimeMultipart parent) {
        this.parent = parent;
    }

    public int getSize() {
        if (this.mimePart != null) {
            try {
                return this.mimePart.read().available();
            }
            catch (IOException ex) {
                return -1;
            }
        }
        if (this.content != null) {
            return this.contentLength;
        }
        if (this.contentStream != null) {
            try {
                int size = this.contentStream.available();
                if (size > 0) {
                    return size;
                }
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }
        return -1;
    }

    public int getLineCount() {
        return -1;
    }

    public String getContentType() {
        if (this.mimePart != null) {
            return this.mimePart.getContentType();
        }
        String s = this.getHeader("Content-Type", null);
        if (s == null) {
            s = "text/plain";
        }
        return s;
    }

    public boolean isMimeType(String mimeType) {
        boolean result;
        try {
            ContentType ct = new ContentType(this.getContentType());
            result = ct.match(mimeType);
        }
        catch (ParseException ex) {
            result = this.getContentType().equalsIgnoreCase(mimeType);
        }
        return result;
    }

    public String getDisposition() throws MessagingException {
        String s = this.getHeader("Content-Disposition", null);
        if (s == null) {
            return null;
        }
        ContentDisposition cd = new ContentDisposition(s);
        return cd.getDisposition();
    }

    public void setDisposition(String disposition) throws MessagingException {
        if (disposition == null) {
            this.removeHeader("Content-Disposition");
        } else {
            String s = this.getHeader("Content-Disposition", null);
            if (s != null) {
                ContentDisposition cd = new ContentDisposition(s);
                cd.setDisposition(disposition);
                disposition = cd.toString();
            }
            this.setHeader("Content-Disposition", disposition);
        }
    }

    public String getEncoding() throws MessagingException {
        HeaderTokenizer.Token tk;
        int tkType;
        String s = this.getHeader("Content-Transfer-Encoding", null);
        if (s == null) {
            return null;
        }
        if ((s = s.trim()).equalsIgnoreCase("7bit") || s.equalsIgnoreCase("8bit") || s.equalsIgnoreCase("quoted-printable") || s.equalsIgnoreCase("base64")) {
            return s;
        }
        HeaderTokenizer h = new HeaderTokenizer(s, "()<>@,;:\\\"\t []/?=");
        while ((tkType = (tk = h.next()).getType()) != -4) {
            if (tkType != -1) continue;
            return tk.getValue();
        }
        return s;
    }

    public String getContentID() {
        return this.getHeader("Content-ID", null);
    }

    public void setContentID(String cid) {
        if (cid == null) {
            this.removeHeader("Content-ID");
        } else {
            this.setHeader("Content-ID", cid);
        }
    }

    public String getContentMD5() {
        return this.getHeader("Content-MD5", null);
    }

    public void setContentMD5(String md5) {
        this.setHeader("Content-MD5", md5);
    }

    public String[] getContentLanguage() throws MessagingException {
        HeaderTokenizer.Token tk;
        int tkType;
        String s = this.getHeader("Content-Language", null);
        if (s == null) {
            return null;
        }
        HeaderTokenizer h = new HeaderTokenizer(s, "()<>@,;:\\\"\t []/?=");
        FinalArrayList<String> v = new FinalArrayList<String>();
        while ((tkType = (tk = h.next()).getType()) != -4) {
            if (tkType != -1) continue;
            v.add(tk.getValue());
        }
        if (v.size() == 0) {
            return null;
        }
        return v.toArray(new String[v.size()]);
    }

    public void setContentLanguage(String[] languages) {
        StringBuilder sb = new StringBuilder(languages[0]);
        for (int i = 1; i < languages.length; ++i) {
            sb.append(',').append(languages[i]);
        }
        this.setHeader("Content-Language", sb.toString());
    }

    public String getDescription() {
        String rawvalue = this.getHeader("Content-Description", null);
        if (rawvalue == null) {
            return null;
        }
        try {
            return MimeUtility.decodeText(MimeUtility.unfold(rawvalue));
        }
        catch (UnsupportedEncodingException ex) {
            return rawvalue;
        }
    }

    public void setDescription(String description) throws MessagingException {
        this.setDescription(description, null);
    }

    public void setDescription(String description, String charset) throws MessagingException {
        if (description == null) {
            this.removeHeader("Content-Description");
            return;
        }
        try {
            this.setHeader("Content-Description", MimeUtility.fold(21, MimeUtility.encodeText(description, charset, null)));
        }
        catch (UnsupportedEncodingException uex) {
            throw new MessagingException("Encoding error", uex);
        }
    }

    public String getFileName() throws MessagingException {
        String filename = null;
        String s = this.getHeader("Content-Disposition", null);
        if (s != null) {
            ContentDisposition cd = new ContentDisposition(s);
            filename = cd.getParameter("filename");
        }
        if (filename == null && (s = this.getHeader("Content-Type", null)) != null) {
            try {
                ContentType ct = new ContentType(s);
                filename = ct.getParameter("name");
            }
            catch (ParseException parseException) {
                // empty catch block
            }
        }
        return filename;
    }

    public void setFileName(String filename) throws MessagingException {
        String s = this.getHeader("Content-Disposition", null);
        ContentDisposition cd = new ContentDisposition(s == null ? ATTACHMENT : s);
        cd.setParameter("filename", filename);
        this.setHeader("Content-Disposition", cd.toString());
        s = this.getHeader("Content-Type", null);
        if (s != null) {
            try {
                ContentType cType = new ContentType(s);
                cType.setParameter("name", filename);
                this.setHeader("Content-Type", cType.toString());
            }
            catch (ParseException parseException) {
                // empty catch block
            }
        }
    }

    public InputStream getInputStream() throws IOException {
        return this.getDataHandler().getInputStream();
    }

    InputStream getContentStream() throws MessagingException {
        if (this.mimePart != null) {
            return this.mimePart.read();
        }
        if (this.contentStream != null) {
            return ((SharedInputStream)((Object)this.contentStream)).newStream(0L, -1L);
        }
        if (this.content != null) {
            return new ByteArrayInputStream(this.content, this.start, this.contentLength);
        }
        throw new MessagingException("No content");
    }

    public InputStream getRawInputStream() throws MessagingException {
        return this.getContentStream();
    }

    public DataHandler getDataHandler() {
        if (this.mimePart != null) {
            return new DataHandler(new DataSource(){

                public InputStream getInputStream() throws IOException {
                    return MimeBodyPart.this.mimePart.read();
                }

                public OutputStream getOutputStream() throws IOException {
                    throw new UnsupportedOperationException("getOutputStream cannot be supported : You have enabled LazyAttachments Option");
                }

                public String getContentType() {
                    return MimeBodyPart.this.mimePart.getContentType();
                }

                public String getName() {
                    return "MIMEPart Wrapped DataSource";
                }
            });
        }
        if (this.dh == null) {
            this.dh = new DataHandler((DataSource)new MimePartDataSource(this));
        }
        return this.dh;
    }

    public Object getContent() throws IOException {
        return this.getDataHandler().getContent();
    }

    public void setDataHandler(DataHandler dh) {
        if (this.mimePart != null) {
            this.mimePart = null;
        }
        this.dh = dh;
        this.content = null;
        this.contentStream = null;
        this.removeHeader("Content-Type");
        this.removeHeader("Content-Transfer-Encoding");
    }

    public void setContent(Object o, String type) {
        if (this.mimePart != null) {
            this.mimePart = null;
        }
        if (o instanceof MimeMultipart) {
            this.setContent((MimeMultipart)o);
        } else {
            this.setDataHandler(new DataHandler(o, type));
        }
    }

    public void setText(String text) {
        this.setText(text, null);
    }

    public void setText(String text, String charset) {
        if (charset == null) {
            charset = MimeUtility.checkAscii(text) != 1 ? MimeUtility.getDefaultMIMECharset() : "us-ascii";
        }
        this.setContent(text, "text/plain; charset=" + MimeUtility.quote(charset, "()<>@,;:\\\"\t []/?="));
    }

    public void setContent(MimeMultipart mp) {
        if (this.mimePart != null) {
            this.mimePart = null;
        }
        this.setDataHandler(new DataHandler((Object)mp, mp.getContentType().toString()));
        mp.setParent(this);
    }

    public void writeTo(OutputStream os) throws IOException, MessagingException {
        List<String> hdrLines = this.headers.getAllHeaderLines();
        int sz = hdrLines.size();
        for (int i = 0; i < sz; ++i) {
            OutputUtil.writeln(hdrLines.get(i), os);
        }
        OutputUtil.writeln(os);
        if (this.contentStream != null) {
            ((SharedInputStream)((Object)this.contentStream)).writeTo(0L, -1L, os);
        } else if (this.content != null) {
            os.write(this.content, this.start, this.contentLength);
        } else if (this.dh != null) {
            OutputStream wos = MimeUtility.encode(os, this.getEncoding());
            this.getDataHandler().writeTo(wos);
            if (os != wos) {
                wos.flush();
            }
        } else if (this.mimePart != null) {
            OutputStream wos = MimeUtility.encode(os, this.getEncoding());
            this.getDataHandler().writeTo(wos);
            if (os != wos) {
                wos.flush();
            }
        } else {
            throw new MessagingException("no content");
        }
    }

    public String[] getHeader(String name) {
        return this.headers.getHeader(name);
    }

    public String getHeader(String name, String delimiter) {
        return this.headers.getHeader(name, delimiter);
    }

    public void setHeader(String name, String value) {
        this.headers.setHeader(name, value);
    }

    public void addHeader(String name, String value) {
        this.headers.addHeader(name, value);
    }

    public void removeHeader(String name) {
        this.headers.removeHeader(name);
    }

    public FinalArrayList<hdr> getAllHeaders() {
        return this.headers.getAllHeaders();
    }

    public void addHeaderLine(String line) {
        this.headers.addHeaderLine(line);
    }

    protected void updateHeaders() throws MessagingException {
        DataHandler dh = this.getDataHandler();
        try {
            String type = dh.getContentType();
            boolean composite = false;
            boolean needCTHeader = this.getHeader("Content-Type") == null;
            ContentType cType = new ContentType(type);
            if (cType.match("multipart/*")) {
                composite = true;
                Object o = dh.getContent();
                ((MimeMultipart)o).updateHeaders();
            } else if (cType.match("message/rfc822")) {
                composite = true;
            }
            if (!composite) {
                if (this.getHeader("Content-Transfer-Encoding") == null) {
                    this.setEncoding(MimeUtility.getEncoding(dh));
                }
                if (needCTHeader && setDefaultTextCharset && cType.match("text/*") && cType.getParameter("charset") == null) {
                    String enc = this.getEncoding();
                    String charset = enc != null && enc.equalsIgnoreCase("7bit") ? "us-ascii" : MimeUtility.getDefaultMIMECharset();
                    cType.setParameter("charset", charset);
                    type = cType.toString();
                }
            }
            if (needCTHeader) {
                ContentDisposition cd;
                String filename;
                String s = this.getHeader("Content-Disposition", null);
                if (s != null && (filename = (cd = new ContentDisposition(s)).getParameter("filename")) != null) {
                    cType.setParameter("name", filename);
                    type = cType.toString();
                }
                this.setHeader("Content-Type", type);
            }
        }
        catch (IOException ex) {
            throw new MessagingException("IOException updating headers", ex);
        }
    }

    private void setEncoding(String encoding) {
        this.setHeader("Content-Transfer-Encoding", encoding);
    }

    static {
        try {
            String s = System.getProperty("mail.mime.setdefaulttextcharset");
            setDefaultTextCharset = s == null || !s.equalsIgnoreCase("false");
        }
        catch (SecurityException securityException) {
            // empty catch block
        }
    }
}

