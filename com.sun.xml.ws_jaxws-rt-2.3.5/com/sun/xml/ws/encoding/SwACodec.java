/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.encoding;

import com.sun.xml.ws.api.SOAPVersion;
import com.sun.xml.ws.api.WSFeatureList;
import com.sun.xml.ws.api.message.Attachment;
import com.sun.xml.ws.api.message.AttachmentSet;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.pipe.Codec;
import com.sun.xml.ws.api.pipe.ContentType;
import com.sun.xml.ws.encoding.MimeCodec;
import com.sun.xml.ws.encoding.MimeMultipartParser;
import com.sun.xml.ws.encoding.RootOnlyCodec;
import com.sun.xml.ws.message.MimeAttachmentSet;
import java.io.IOException;
import java.nio.channels.WritableByteChannel;
import java.util.Map;

public final class SwACodec
extends MimeCodec {
    public SwACodec(SOAPVersion version, WSFeatureList f, Codec rootCodec) {
        super(version, f);
        this.mimeRootCodec = rootCodec;
    }

    private SwACodec(SwACodec that) {
        super(that);
        this.mimeRootCodec = that.mimeRootCodec.copy();
    }

    @Override
    protected void decode(MimeMultipartParser mpp, Packet packet) throws IOException {
        Attachment root = mpp.getRootPart();
        Codec rootCodec = this.getMimeRootCodec(packet);
        if (rootCodec instanceof RootOnlyCodec) {
            ((RootOnlyCodec)rootCodec).decode(root.asInputStream(), root.getContentType(), packet, (AttachmentSet)new MimeAttachmentSet(mpp));
        } else {
            rootCodec.decode(root.asInputStream(), root.getContentType(), packet);
            Map<String, Attachment> atts = mpp.getAttachmentParts();
            for (Map.Entry<String, Attachment> att : atts.entrySet()) {
                packet.getMessage().getAttachments().add(att.getValue());
            }
        }
    }

    @Override
    public ContentType encode(Packet packet, WritableByteChannel buffer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SwACodec copy() {
        return new SwACodec(this);
    }
}

