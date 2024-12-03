/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.DataHandler
 *  javax.xml.ws.WebServiceException
 */
package com.sun.xml.ws.server.sei;

import com.sun.xml.ws.api.message.Headers;
import com.sun.xml.ws.api.message.Message;
import com.sun.xml.ws.message.ByteArrayAttachment;
import com.sun.xml.ws.message.DataHandlerAttachment;
import com.sun.xml.ws.message.JAXBAttachment;
import com.sun.xml.ws.model.ParameterImpl;
import com.sun.xml.ws.server.sei.ValueGetter;
import com.sun.xml.ws.spi.db.XMLBridge;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.UUID;
import javax.activation.DataHandler;
import javax.xml.transform.Source;
import javax.xml.ws.WebServiceException;

public abstract class MessageFiller {
    protected final int methodPos;

    protected MessageFiller(int methodPos) {
        this.methodPos = methodPos;
    }

    public abstract void fillIn(Object[] var1, Object var2, Message var3);

    private static boolean isXMLMimeType(String mimeType) {
        return mimeType.equals("text/xml") || mimeType.equals("application/xml");
    }

    public static final class Header
    extends MessageFiller {
        private final XMLBridge bridge;
        private final ValueGetter getter;

        public Header(int methodPos, XMLBridge bridge, ValueGetter getter) {
            super(methodPos);
            this.bridge = bridge;
            this.getter = getter;
        }

        @Override
        public void fillIn(Object[] methodArgs, Object returnValue, Message msg) {
            Object value = this.methodPos == -1 ? returnValue : this.getter.get(methodArgs[this.methodPos]);
            msg.getHeaders().add(Headers.create(this.bridge, value));
        }
    }

    private static class JAXBFiller
    extends AttachmentFiller {
        protected JAXBFiller(ParameterImpl param, ValueGetter getter) {
            super(param, getter);
        }

        @Override
        public void fillIn(Object[] methodArgs, Object returnValue, Message msg) {
            String contentId = this.getContentId();
            Object obj = this.methodPos == -1 ? returnValue : this.getter.get(methodArgs[this.methodPos]);
            JAXBAttachment att = new JAXBAttachment(contentId, obj, this.param.getXMLBridge(), this.mimeType);
            msg.getAttachments().add(att);
        }
    }

    private static class DataHandlerFiller
    extends AttachmentFiller {
        protected DataHandlerFiller(ParameterImpl param, ValueGetter getter) {
            super(param, getter);
        }

        @Override
        public void fillIn(Object[] methodArgs, Object returnValue, Message msg) {
            String contentId = this.getContentId();
            Object obj = this.methodPos == -1 ? returnValue : this.getter.get(methodArgs[this.methodPos]);
            DataHandler dh = obj instanceof DataHandler ? (DataHandler)obj : new DataHandler(obj, this.mimeType);
            DataHandlerAttachment att = new DataHandlerAttachment(contentId, dh);
            msg.getAttachments().add(att);
        }
    }

    private static class ByteArrayFiller
    extends AttachmentFiller {
        protected ByteArrayFiller(ParameterImpl param, ValueGetter getter) {
            super(param, getter);
        }

        @Override
        public void fillIn(Object[] methodArgs, Object returnValue, Message msg) {
            Object obj;
            String contentId = this.getContentId();
            Object object = obj = this.methodPos == -1 ? returnValue : this.getter.get(methodArgs[this.methodPos]);
            if (obj != null) {
                ByteArrayAttachment att = new ByteArrayAttachment(contentId, (byte[])obj, this.mimeType);
                msg.getAttachments().add(att);
            }
        }
    }

    public static abstract class AttachmentFiller
    extends MessageFiller {
        protected final ParameterImpl param;
        protected final ValueGetter getter;
        protected final String mimeType;
        private final String contentIdPart;

        protected AttachmentFiller(ParameterImpl param, ValueGetter getter) {
            super(param.getIndex());
            this.param = param;
            this.getter = getter;
            this.mimeType = param.getBinding().getMimeType();
            try {
                this.contentIdPart = URLEncoder.encode(param.getPartName(), "UTF-8") + '=';
            }
            catch (UnsupportedEncodingException e) {
                throw new WebServiceException((Throwable)e);
            }
        }

        public static MessageFiller createAttachmentFiller(ParameterImpl param, ValueGetter getter) {
            Class type = (Class)param.getTypeInfo().type;
            if (DataHandler.class.isAssignableFrom(type) || Source.class.isAssignableFrom(type)) {
                return new DataHandlerFiller(param, getter);
            }
            if (byte[].class == type) {
                return new ByteArrayFiller(param, getter);
            }
            if (MessageFiller.isXMLMimeType(param.getBinding().getMimeType())) {
                return new JAXBFiller(param, getter);
            }
            return new DataHandlerFiller(param, getter);
        }

        String getContentId() {
            return this.contentIdPart + UUID.randomUUID() + "@jaxws.sun.com";
        }
    }
}

