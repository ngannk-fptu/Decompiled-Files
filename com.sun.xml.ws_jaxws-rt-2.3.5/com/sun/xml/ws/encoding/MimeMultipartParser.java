/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  com.sun.istack.Nullable
 *  javax.activation.DataHandler
 *  javax.xml.soap.SOAPException
 *  javax.xml.soap.SOAPMessage
 *  javax.xml.ws.WebServiceException
 *  org.jvnet.mimepull.Header
 *  org.jvnet.mimepull.MIMEMessage
 *  org.jvnet.mimepull.MIMEPart
 */
package com.sun.xml.ws.encoding;

import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import com.sun.xml.ws.api.message.Attachment;
import com.sun.xml.ws.api.message.AttachmentEx;
import com.sun.xml.ws.developer.StreamingAttachmentFeature;
import com.sun.xml.ws.developer.StreamingDataHandler;
import com.sun.xml.ws.encoding.ContentTypeImpl;
import com.sun.xml.ws.encoding.DataSourceStreamingDataHandler;
import com.sun.xml.ws.encoding.MIMEPartStreamingDataHandler;
import com.sun.xml.ws.util.ByteArrayBuffer;
import com.sun.xml.ws.util.ByteArrayDataSource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.activation.DataHandler;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.WebServiceException;
import org.jvnet.mimepull.Header;
import org.jvnet.mimepull.MIMEMessage;
import org.jvnet.mimepull.MIMEPart;

public final class MimeMultipartParser {
    private final String start;
    private final MIMEMessage message;
    private Attachment root;
    private ContentTypeImpl contentType;
    private final Map<String, Attachment> attachments = new HashMap<String, Attachment>();
    private boolean gotAll;

    public MimeMultipartParser(InputStream in, String cType, StreamingAttachmentFeature feature) {
        this.contentType = new ContentTypeImpl(cType);
        String boundary = this.contentType.getBoundary();
        if (boundary == null || boundary.equals("")) {
            throw new WebServiceException("MIME boundary parameter not found" + this.contentType);
        }
        this.message = feature != null ? new MIMEMessage(in, boundary, feature.getConfig()) : new MIMEMessage(in, boundary);
        String st = this.contentType.getRootId();
        if (st != null && st.length() > 2 && st.charAt(0) == '<' && st.charAt(st.length() - 1) == '>') {
            st = st.substring(1, st.length() - 1);
        }
        this.start = st;
    }

    @Nullable
    public Attachment getRootPart() {
        if (this.root == null) {
            this.root = new PartAttachment(this.start != null ? this.message.getPart(this.start) : this.message.getPart(0));
        }
        return this.root;
    }

    @NotNull
    public Map<String, Attachment> getAttachmentParts() {
        if (!this.gotAll) {
            MIMEPart rootPart = this.start != null ? this.message.getPart(this.start) : this.message.getPart(0);
            List parts = this.message.getAttachments();
            for (MIMEPart part : parts) {
                String cid;
                if (part == rootPart || this.attachments.containsKey(cid = part.getContentId())) continue;
                PartAttachment attach = new PartAttachment(part);
                this.attachments.put(attach.getContentId(), attach);
            }
            this.gotAll = true;
        }
        return this.attachments;
    }

    @Nullable
    public Attachment getAttachmentPart(String contentId) throws IOException {
        Attachment attach = this.attachments.get(contentId);
        if (attach == null) {
            MIMEPart part = this.message.getPart(contentId);
            attach = new PartAttachment(part);
            this.attachments.put(contentId, attach);
        }
        return attach;
    }

    public ContentTypeImpl getContentType() {
        return this.contentType;
    }

    static class PartAttachment
    implements AttachmentEx {
        final MIMEPart part;
        byte[] buf;
        private StreamingDataHandler streamingDataHandler;

        PartAttachment(MIMEPart part) {
            this.part = part;
        }

        @Override
        @NotNull
        public String getContentId() {
            return this.part.getContentId();
        }

        @Override
        @NotNull
        public String getContentType() {
            return this.part.getContentType();
        }

        @Override
        public byte[] asByteArray() {
            if (this.buf == null) {
                ByteArrayBuffer baf = new ByteArrayBuffer();
                try {
                    baf.write(this.part.readOnce());
                }
                catch (IOException ioe) {
                    throw new WebServiceException((Throwable)ioe);
                }
                finally {
                    if (baf != null) {
                        try {
                            baf.close();
                        }
                        catch (IOException ex) {
                            Logger.getLogger(MimeMultipartParser.class.getName()).log(Level.FINE, null, ex);
                        }
                    }
                }
                this.buf = baf.toByteArray();
            }
            return this.buf;
        }

        @Override
        public DataHandler asDataHandler() {
            if (this.streamingDataHandler == null) {
                this.streamingDataHandler = this.buf != null ? new DataSourceStreamingDataHandler(new ByteArrayDataSource(this.buf, this.getContentType())) : new MIMEPartStreamingDataHandler(this.part);
            }
            return this.streamingDataHandler;
        }

        @Override
        public Source asSource() {
            return this.buf != null ? new StreamSource(new ByteArrayInputStream(this.buf)) : new StreamSource(this.part.read());
        }

        @Override
        public InputStream asInputStream() {
            return this.buf != null ? new ByteArrayInputStream(this.buf) : this.part.read();
        }

        @Override
        public void writeTo(OutputStream os) throws IOException {
            if (this.buf != null) {
                os.write(this.buf);
            } else {
                int len;
                InputStream in = this.part.read();
                byte[] temp = new byte[8192];
                while ((len = in.read(temp)) != -1) {
                    os.write(temp, 0, len);
                }
                in.close();
            }
        }

        @Override
        public void writeTo(SOAPMessage saaj) throws SOAPException {
            saaj.createAttachmentPart().setDataHandler(this.asDataHandler());
        }

        @Override
        public Iterator<AttachmentEx.MimeHeader> getMimeHeaders() {
            final Iterator ih = this.part.getAllHeaders().iterator();
            return new Iterator<AttachmentEx.MimeHeader>(){

                @Override
                public boolean hasNext() {
                    return ih.hasNext();
                }

                @Override
                public AttachmentEx.MimeHeader next() {
                    final Header hdr = (Header)ih.next();
                    return new AttachmentEx.MimeHeader(){

                        @Override
                        public String getValue() {
                            return hdr.getValue();
                        }

                        @Override
                        public String getName() {
                            return hdr.getName();
                        }
                    };
                }

                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
        }
    }
}

