/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.DataHandler
 *  javax.xml.bind.annotation.adapters.XmlAdapter
 *  javax.xml.bind.attachment.AttachmentMarshaller
 *  javax.xml.bind.attachment.AttachmentUnmarshaller
 */
package com.sun.xml.bind.v2.runtime;

import com.sun.xml.bind.v2.runtime.XMLSerializer;
import com.sun.xml.bind.v2.runtime.unmarshaller.UnmarshallingContext;
import javax.activation.DataHandler;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.attachment.AttachmentMarshaller;
import javax.xml.bind.attachment.AttachmentUnmarshaller;

public final class SwaRefAdapter
extends XmlAdapter<String, DataHandler> {
    public DataHandler unmarshal(String cid) {
        AttachmentUnmarshaller au = UnmarshallingContext.getInstance().parent.getAttachmentUnmarshaller();
        return au.getAttachmentAsDataHandler(cid);
    }

    public String marshal(DataHandler data) {
        if (data == null) {
            return null;
        }
        AttachmentMarshaller am = XMLSerializer.getInstance().attachmentMarshaller;
        return am.addSwaRefAttachment(data);
    }
}

