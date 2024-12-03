/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.encoding;

import com.sun.xml.ws.api.SOAPVersion;
import com.sun.xml.ws.api.WSFeatureList;
import com.sun.xml.ws.api.message.Attachment;
import com.sun.xml.ws.api.message.AttachmentEx;
import com.sun.xml.ws.api.message.Message;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.pipe.Codec;
import com.sun.xml.ws.api.pipe.ContentType;
import com.sun.xml.ws.developer.StreamingAttachmentFeature;
import com.sun.xml.ws.encoding.ContentTypeImpl;
import com.sun.xml.ws.encoding.MimeMultipartParser;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.ReadableByteChannel;
import java.util.Iterator;
import java.util.UUID;

abstract class MimeCodec
implements Codec {
    public static final String MULTIPART_RELATED_MIME_TYPE = "multipart/related";
    protected Codec mimeRootCodec;
    protected final SOAPVersion version;
    protected final WSFeatureList features;

    protected MimeCodec(SOAPVersion version, WSFeatureList f) {
        this.version = version;
        this.features = f;
    }

    @Override
    public String getMimeType() {
        return MULTIPART_RELATED_MIME_TYPE;
    }

    protected Codec getMimeRootCodec(Packet packet) {
        return this.mimeRootCodec;
    }

    @Override
    public ContentType encode(Packet packet, OutputStream out) throws IOException {
        Message msg = packet.getMessage();
        if (msg == null) {
            return null;
        }
        ContentTypeImpl ctImpl = (ContentTypeImpl)this.getStaticContentType(packet);
        String boundary = ctImpl.getBoundary();
        String rootId = ctImpl.getRootId();
        boolean hasAttachments = boundary != null;
        Codec rootCodec = this.getMimeRootCodec(packet);
        if (hasAttachments) {
            String ctStr;
            MimeCodec.writeln("--" + boundary, out);
            ContentType ct = rootCodec.getStaticContentType(packet);
            String string = ctStr = ct != null ? ct.getContentType() : rootCodec.getMimeType();
            if (rootId != null) {
                MimeCodec.writeln("Content-ID: " + rootId, out);
            }
            MimeCodec.writeln("Content-Type: " + ctStr, out);
            MimeCodec.writeln(out);
        }
        ContentType primaryCt = rootCodec.encode(packet, out);
        if (hasAttachments) {
            MimeCodec.writeln(out);
            for (Attachment att : msg.getAttachments()) {
                MimeCodec.writeln("--" + boundary, out);
                String cid = att.getContentId();
                if (cid != null && cid.length() > 0 && cid.charAt(0) != '<') {
                    cid = '<' + cid + '>';
                }
                MimeCodec.writeln("Content-Id:" + cid, out);
                MimeCodec.writeln("Content-Type: " + att.getContentType(), out);
                this.writeCustomMimeHeaders(att, out);
                MimeCodec.writeln("Content-Transfer-Encoding: binary", out);
                MimeCodec.writeln(out);
                att.writeTo(out);
                MimeCodec.writeln(out);
            }
            MimeCodec.writeAsAscii("--" + boundary, out);
            MimeCodec.writeAsAscii("--", out);
        }
        return hasAttachments ? ctImpl : primaryCt;
    }

    private void writeCustomMimeHeaders(Attachment att, OutputStream out) throws IOException {
        if (att instanceof AttachmentEx) {
            Iterator<AttachmentEx.MimeHeader> allMimeHeaders = ((AttachmentEx)att).getMimeHeaders();
            while (allMimeHeaders.hasNext()) {
                AttachmentEx.MimeHeader mh = allMimeHeaders.next();
                String name = mh.getName();
                if ("Content-Type".equalsIgnoreCase(name) || "Content-Id".equalsIgnoreCase(name)) continue;
                MimeCodec.writeln(name + ": " + mh.getValue(), out);
            }
        }
    }

    @Override
    public ContentType getStaticContentType(Packet packet) {
        ContentType ct = (ContentType)packet.getInternalContentType();
        if (ct != null) {
            return ct;
        }
        Message msg = packet.getMessage();
        boolean hasAttachments = !msg.getAttachments().isEmpty();
        Codec rootCodec = this.getMimeRootCodec(packet);
        if (hasAttachments) {
            String boundary = "uuid:" + UUID.randomUUID().toString();
            String boundaryParameter = "boundary=\"" + boundary + "\"";
            String messageContentType = "multipart/related; type=\"" + rootCodec.getMimeType() + "\"; " + boundaryParameter;
            ContentTypeImpl impl = new ContentTypeImpl(messageContentType, packet.soapAction, null);
            impl.setBoundary(boundary);
            impl.setBoundaryParameter(boundaryParameter);
            packet.setContentType(impl);
            return impl;
        }
        ct = rootCodec.getStaticContentType(packet);
        packet.setContentType(ct);
        return ct;
    }

    protected MimeCodec(MimeCodec that) {
        this.version = that.version;
        this.features = that.features;
    }

    @Override
    public void decode(InputStream in, String contentType, Packet packet) throws IOException {
        MimeMultipartParser parser = new MimeMultipartParser(in, contentType, this.features.get(StreamingAttachmentFeature.class));
        this.decode(parser, packet);
    }

    @Override
    public void decode(ReadableByteChannel in, String contentType, Packet packet) {
        throw new UnsupportedOperationException();
    }

    protected abstract void decode(MimeMultipartParser var1, Packet var2) throws IOException;

    @Override
    public abstract MimeCodec copy();

    public static void writeln(String s, OutputStream out) throws IOException {
        MimeCodec.writeAsAscii(s, out);
        MimeCodec.writeln(out);
    }

    public static void writeAsAscii(String s, OutputStream out) throws IOException {
        int len = s.length();
        for (int i = 0; i < len; ++i) {
            out.write((byte)s.charAt(i));
        }
    }

    public static void writeln(OutputStream out) throws IOException {
        out.write(13);
        out.write(10);
    }
}

