/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.DataHandler
 *  javax.xml.bind.attachment.AttachmentUnmarshaller
 *  javax.xml.ws.WebServiceException
 */
package com.sun.xml.ws.message;

import com.sun.xml.ws.api.message.Attachment;
import com.sun.xml.ws.api.message.AttachmentSet;
import com.sun.xml.ws.resources.EncodingMessages;
import javax.activation.DataHandler;
import javax.xml.bind.attachment.AttachmentUnmarshaller;
import javax.xml.ws.WebServiceException;

public final class AttachmentUnmarshallerImpl
extends AttachmentUnmarshaller {
    private final AttachmentSet attachments;

    public AttachmentUnmarshallerImpl(AttachmentSet attachments) {
        this.attachments = attachments;
    }

    public DataHandler getAttachmentAsDataHandler(String cid) {
        Attachment a = this.attachments.get(this.stripScheme(cid));
        if (a == null) {
            throw new WebServiceException(EncodingMessages.NO_SUCH_CONTENT_ID(cid));
        }
        return a.asDataHandler();
    }

    public byte[] getAttachmentAsByteArray(String cid) {
        Attachment a = this.attachments.get(this.stripScheme(cid));
        if (a == null) {
            throw new WebServiceException(EncodingMessages.NO_SUCH_CONTENT_ID(cid));
        }
        return a.asByteArray();
    }

    private String stripScheme(String cid) {
        if (cid.startsWith("cid:")) {
            cid = cid.substring(4);
        }
        return cid;
    }
}

