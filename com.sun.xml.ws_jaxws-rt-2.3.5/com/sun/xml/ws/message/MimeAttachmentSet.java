/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.Nullable
 *  javax.xml.ws.WebServiceException
 */
package com.sun.xml.ws.message;

import com.sun.istack.Nullable;
import com.sun.xml.ws.api.message.Attachment;
import com.sun.xml.ws.api.message.AttachmentSet;
import com.sun.xml.ws.encoding.MimeMultipartParser;
import com.sun.xml.ws.resources.EncodingMessages;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.xml.ws.WebServiceException;

public final class MimeAttachmentSet
implements AttachmentSet {
    private final MimeMultipartParser mpp;
    private Map<String, Attachment> atts = new HashMap<String, Attachment>();

    public MimeAttachmentSet(MimeMultipartParser mpp) {
        this.mpp = mpp;
    }

    @Override
    @Nullable
    public Attachment get(String contentId) {
        Attachment att = this.atts.get(contentId);
        if (att != null) {
            return att;
        }
        try {
            att = this.mpp.getAttachmentPart(contentId);
            if (att != null) {
                this.atts.put(contentId, att);
            }
        }
        catch (IOException e) {
            throw new WebServiceException(EncodingMessages.NO_SUCH_CONTENT_ID(contentId), (Throwable)e);
        }
        return att;
    }

    @Override
    public boolean isEmpty() {
        return this.atts.size() <= 0 && this.mpp.getAttachmentParts().isEmpty();
    }

    @Override
    public void add(Attachment att) {
        this.atts.put(att.getContentId(), att);
    }

    @Override
    public Iterator<Attachment> iterator() {
        Map<String, Attachment> attachments = this.mpp.getAttachmentParts();
        for (Map.Entry<String, Attachment> att : attachments.entrySet()) {
            if (this.atts.get(att.getKey()) != null) continue;
            this.atts.put(att.getKey(), att.getValue());
        }
        return this.atts.values().iterator();
    }
}

