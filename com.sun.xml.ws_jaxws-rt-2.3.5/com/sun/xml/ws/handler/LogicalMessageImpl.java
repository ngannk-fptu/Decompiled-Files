/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.JAXBContext
 *  javax.xml.bind.JAXBException
 *  javax.xml.bind.Unmarshaller
 *  javax.xml.bind.util.JAXBSource
 *  javax.xml.ws.LogicalMessage
 *  javax.xml.ws.WebServiceException
 */
package com.sun.xml.ws.handler;

import com.sun.xml.ws.api.WSBinding;
import com.sun.xml.ws.api.message.AttachmentSet;
import com.sun.xml.ws.api.message.Message;
import com.sun.xml.ws.api.message.MessageHeaders;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.message.DOMMessage;
import com.sun.xml.ws.message.EmptyMessageImpl;
import com.sun.xml.ws.message.jaxb.JAXBMessage;
import com.sun.xml.ws.message.source.PayloadSourceMessage;
import com.sun.xml.ws.spi.db.BindingContext;
import com.sun.xml.ws.spi.db.BindingContextFactory;
import com.sun.xml.ws.util.xml.XmlUtil;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.util.JAXBSource;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.ws.LogicalMessage;
import javax.xml.ws.WebServiceException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

class LogicalMessageImpl
implements LogicalMessage {
    private Packet packet;
    protected BindingContext defaultJaxbContext;
    private ImmutableLM lm = null;

    public LogicalMessageImpl(BindingContext defaultJaxbContext, Packet packet) {
        this.packet = packet;
        this.defaultJaxbContext = defaultJaxbContext;
    }

    public Source getPayload() {
        if (this.lm == null) {
            Source payload = this.packet.getMessage().copy().readPayloadAsSource();
            if (payload instanceof DOMSource) {
                this.lm = this.createLogicalMessageImpl(payload);
            }
            return payload;
        }
        return this.lm.getPayload();
    }

    public void setPayload(Source payload) {
        this.lm = this.createLogicalMessageImpl(payload);
    }

    private ImmutableLM createLogicalMessageImpl(Source payload) {
        this.lm = payload == null ? new EmptyLogicalMessageImpl() : (payload instanceof DOMSource ? new DOMLogicalMessageImpl((DOMSource)payload) : new SourceLogicalMessageImpl(payload));
        return this.lm;
    }

    public Object getPayload(BindingContext context) {
        Object o;
        if (context == null) {
            context = this.defaultJaxbContext;
        }
        if (context == null) {
            throw new WebServiceException("JAXBContext parameter cannot be null");
        }
        if (this.lm == null) {
            try {
                o = this.packet.getMessage().copy().readPayloadAsJAXB(context.createUnmarshaller());
            }
            catch (JAXBException e) {
                throw new WebServiceException((Throwable)e);
            }
        } else {
            o = this.lm.getPayload(context);
            this.lm = new JAXBLogicalMessageImpl(context.getJAXBContext(), o);
        }
        return o;
    }

    public Object getPayload(JAXBContext context) {
        Object o;
        if (context == null) {
            return this.getPayload(this.defaultJaxbContext);
        }
        if (context == null) {
            throw new WebServiceException("JAXBContext parameter cannot be null");
        }
        if (this.lm == null) {
            try {
                o = this.packet.getMessage().copy().readPayloadAsJAXB(context.createUnmarshaller());
            }
            catch (JAXBException e) {
                throw new WebServiceException((Throwable)e);
            }
        } else {
            o = this.lm.getPayload(context);
            this.lm = new JAXBLogicalMessageImpl(context, o);
        }
        return o;
    }

    public void setPayload(Object payload, BindingContext context) {
        if (context == null) {
            context = this.defaultJaxbContext;
        }
        this.lm = payload == null ? new EmptyLogicalMessageImpl() : new JAXBLogicalMessageImpl(context.getJAXBContext(), payload);
    }

    public void setPayload(Object payload, JAXBContext context) {
        if (context == null) {
            this.setPayload(payload, this.defaultJaxbContext);
        }
        this.lm = payload == null ? new EmptyLogicalMessageImpl() : new JAXBLogicalMessageImpl(context, payload);
    }

    public boolean isPayloadModifed() {
        return this.lm != null;
    }

    public Message getMessage(MessageHeaders headers, AttachmentSet attachments, WSBinding binding) {
        assert (this.isPayloadModifed());
        if (this.isPayloadModifed()) {
            return this.lm.getMessage(headers, attachments, binding);
        }
        return this.packet.getMessage();
    }

    private class SourceLogicalMessageImpl
    extends ImmutableLM {
        private Source payloadSrc;

        public SourceLogicalMessageImpl(Source source) {
            this.payloadSrc = source;
        }

        @Override
        public Source getPayload() {
            assert (!(this.payloadSrc instanceof DOMSource));
            try {
                Transformer transformer = XmlUtil.newTransformer();
                DOMResult domResult = new DOMResult();
                transformer.transform(this.payloadSrc, domResult);
                DOMSource dom = new DOMSource(domResult.getNode());
                LogicalMessageImpl.this.lm = new DOMLogicalMessageImpl(dom);
                this.payloadSrc = null;
                return dom;
            }
            catch (TransformerException te) {
                throw new WebServiceException((Throwable)te);
            }
        }

        @Override
        public Object getPayload(JAXBContext context) {
            try {
                Source payloadSrc = this.getPayload();
                if (payloadSrc == null) {
                    return null;
                }
                Unmarshaller unmarshaller = context.createUnmarshaller();
                return unmarshaller.unmarshal(payloadSrc);
            }
            catch (JAXBException e) {
                throw new WebServiceException((Throwable)e);
            }
        }

        @Override
        public Object getPayload(BindingContext context) {
            try {
                Source payloadSrc = this.getPayload();
                if (payloadSrc == null) {
                    return null;
                }
                Unmarshaller unmarshaller = context.createUnmarshaller();
                return unmarshaller.unmarshal(payloadSrc);
            }
            catch (JAXBException e) {
                throw new WebServiceException((Throwable)e);
            }
        }

        @Override
        public Message getMessage(MessageHeaders headers, AttachmentSet attachments, WSBinding binding) {
            assert (this.payloadSrc != null);
            return new PayloadSourceMessage(headers, this.payloadSrc, attachments, binding.getSOAPVersion());
        }
    }

    private class JAXBLogicalMessageImpl
    extends ImmutableLM {
        private JAXBContext ctxt;
        private Object o;

        public JAXBLogicalMessageImpl(JAXBContext ctxt, Object o) {
            this.ctxt = ctxt;
            this.o = o;
        }

        @Override
        public Source getPayload() {
            JAXBContext context = this.ctxt;
            if (context == null) {
                context = LogicalMessageImpl.this.defaultJaxbContext.getJAXBContext();
            }
            try {
                return new JAXBSource(context, this.o);
            }
            catch (JAXBException e) {
                throw new WebServiceException((Throwable)e);
            }
        }

        @Override
        public Object getPayload(JAXBContext context) {
            try {
                Source payloadSrc = this.getPayload();
                if (payloadSrc == null) {
                    return null;
                }
                Unmarshaller unmarshaller = context.createUnmarshaller();
                return unmarshaller.unmarshal(payloadSrc);
            }
            catch (JAXBException e) {
                throw new WebServiceException((Throwable)e);
            }
        }

        @Override
        public Object getPayload(BindingContext context) {
            try {
                Source payloadSrc = this.getPayload();
                if (payloadSrc == null) {
                    return null;
                }
                Unmarshaller unmarshaller = context.createUnmarshaller();
                return unmarshaller.unmarshal(payloadSrc);
            }
            catch (JAXBException e) {
                throw new WebServiceException((Throwable)e);
            }
        }

        @Override
        public Message getMessage(MessageHeaders headers, AttachmentSet attachments, WSBinding binding) {
            return JAXBMessage.create(BindingContextFactory.create(this.ctxt), this.o, binding.getSOAPVersion(), headers, attachments);
        }
    }

    private class EmptyLogicalMessageImpl
    extends ImmutableLM {
        @Override
        public Source getPayload() {
            return null;
        }

        @Override
        public Object getPayload(JAXBContext context) {
            return null;
        }

        @Override
        public Object getPayload(BindingContext context) {
            return null;
        }

        @Override
        public Message getMessage(MessageHeaders headers, AttachmentSet attachments, WSBinding binding) {
            return new EmptyMessageImpl(headers, attachments, binding.getSOAPVersion());
        }
    }

    private class DOMLogicalMessageImpl
    extends SourceLogicalMessageImpl {
        private DOMSource dom;

        public DOMLogicalMessageImpl(DOMSource dom) {
            super(dom);
            this.dom = dom;
        }

        @Override
        public Source getPayload() {
            return this.dom;
        }

        @Override
        public Message getMessage(MessageHeaders headers, AttachmentSet attachments, WSBinding binding) {
            Node n = this.dom.getNode();
            if (n.getNodeType() == 9) {
                n = ((Document)n).getDocumentElement();
            }
            return new DOMMessage(binding.getSOAPVersion(), headers, (Element)n, attachments);
        }
    }

    private abstract class ImmutableLM {
        private ImmutableLM() {
        }

        public abstract Source getPayload();

        public abstract Object getPayload(BindingContext var1);

        public abstract Object getPayload(JAXBContext var1);

        public abstract Message getMessage(MessageHeaders var1, AttachmentSet var2, WSBinding var3);
    }
}

