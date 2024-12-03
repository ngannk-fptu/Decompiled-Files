/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.logging.Logger
 *  javax.activation.DataHandler
 *  javax.xml.bind.attachment.AttachmentMarshaller
 *  javax.xml.ws.WebServiceException
 */
package com.sun.xml.ws.message.jaxb;

import com.sun.istack.logging.Logger;
import com.sun.xml.ws.api.message.AttachmentSet;
import com.sun.xml.ws.message.DataHandlerAttachment;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.UUID;
import java.util.logging.Level;
import javax.activation.DataHandler;
import javax.xml.bind.attachment.AttachmentMarshaller;
import javax.xml.ws.WebServiceException;

final class AttachmentMarshallerImpl
extends AttachmentMarshaller {
    private static final Logger LOGGER = Logger.getLogger(AttachmentMarshallerImpl.class);
    private AttachmentSet attachments;

    public AttachmentMarshallerImpl(AttachmentSet attachemnts) {
        this.attachments = attachemnts;
    }

    void cleanup() {
        this.attachments = null;
    }

    public String addMtomAttachment(DataHandler data, String elementNamespace, String elementLocalName) {
        throw new IllegalStateException();
    }

    public String addMtomAttachment(byte[] data, int offset, int length, String mimeType, String elementNamespace, String elementLocalName) {
        throw new IllegalStateException();
    }

    public String addSwaRefAttachment(DataHandler data) {
        String cid = this.encodeCid(null);
        DataHandlerAttachment att = new DataHandlerAttachment(cid, data);
        this.attachments.add(att);
        cid = "cid:" + cid;
        return cid;
    }

    private String encodeCid(String ns) {
        String cid = "example.jaxws.sun.com";
        String name = UUID.randomUUID() + "@";
        if (ns != null && ns.length() > 0) {
            try {
                URI uri = new URI(ns);
                cid = uri.toURL().getHost();
            }
            catch (URISyntaxException e) {
                if (LOGGER.isLoggable(Level.INFO)) {
                    LOGGER.log(Level.INFO, null, (Throwable)e);
                }
                return null;
            }
            catch (MalformedURLException e) {
                try {
                    cid = URLEncoder.encode(ns, "UTF-8");
                }
                catch (UnsupportedEncodingException e1) {
                    throw new WebServiceException((Throwable)e);
                }
            }
        }
        return name + cid;
    }
}

