/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.DataHandler
 *  javax.jws.WebParam$Mode
 *  javax.xml.bind.JAXBException
 *  javax.xml.bind.attachment.AttachmentUnmarshaller
 *  javax.xml.soap.SOAPException
 *  javax.xml.soap.SOAPFault
 *  javax.xml.ws.WebServiceException
 *  javax.xml.ws.soap.SOAPFaultException
 */
package com.sun.xml.ws.server.sei;

import com.sun.xml.ws.api.SOAPVersion;
import com.sun.xml.ws.api.message.Attachment;
import com.sun.xml.ws.api.message.AttachmentSet;
import com.sun.xml.ws.api.message.Message;
import com.sun.xml.ws.api.model.ParameterBinding;
import com.sun.xml.ws.api.streaming.XMLStreamReaderFactory;
import com.sun.xml.ws.encoding.DataHandlerDataSource;
import com.sun.xml.ws.encoding.StringDataContentHandler;
import com.sun.xml.ws.message.AttachmentUnmarshallerImpl;
import com.sun.xml.ws.model.ParameterImpl;
import com.sun.xml.ws.model.WrapperParameter;
import com.sun.xml.ws.resources.ServerMessages;
import com.sun.xml.ws.server.sei.EndpointValueSetter;
import com.sun.xml.ws.spi.db.DatabindingException;
import com.sun.xml.ws.spi.db.PropertyAccessor;
import com.sun.xml.ws.spi.db.RepeatedElementBridge;
import com.sun.xml.ws.spi.db.WrapperComposite;
import com.sun.xml.ws.spi.db.XMLBridge;
import com.sun.xml.ws.streaming.XMLStreamReaderUtil;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.activation.DataHandler;
import javax.imageio.ImageIO;
import javax.jws.WebParam;
import javax.xml.bind.JAXBException;
import javax.xml.bind.attachment.AttachmentUnmarshaller;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFault;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPFaultException;

public abstract class EndpointArgumentsBuilder {
    public static final EndpointArgumentsBuilder NONE = new None();
    private static final Map<Class, Object> primitiveUninitializedValues;
    protected QName wrapperName;
    protected Map<QName, WrappedPartBuilder> wrappedParts = null;

    public abstract void readRequest(Message var1, Object[] var2) throws JAXBException, XMLStreamException;

    public static Object getVMUninitializedValue(Type type) {
        return primitiveUninitializedValues.get(type);
    }

    protected void readWrappedRequest(Message msg, Object[] args) throws JAXBException, XMLStreamException {
        if (!msg.hasPayload()) {
            throw new WebServiceException("No payload. Expecting payload with " + this.wrapperName + " element");
        }
        XMLStreamReader reader = msg.readPayload();
        XMLStreamReaderUtil.verifyTag(reader, this.wrapperName);
        reader.nextTag();
        while (reader.getEventType() == 1) {
            QName name = reader.getName();
            WrappedPartBuilder part = this.wrappedParts.get(name);
            if (part == null) {
                XMLStreamReaderUtil.skipElement(reader);
                reader.nextTag();
            } else {
                part.readRequest(args, reader, msg.getAttachments());
            }
            XMLStreamReaderUtil.toNextTag(reader, name);
        }
        reader.close();
        XMLStreamReaderFactory.recycle(reader);
    }

    public static final String getWSDLPartName(Attachment att) {
        String cId = att.getContentId();
        int index = cId.lastIndexOf(64, cId.length());
        if (index == -1) {
            return null;
        }
        String localPart = cId.substring(0, index);
        if ((index = localPart.lastIndexOf(61, localPart.length())) == -1) {
            return null;
        }
        try {
            return URLDecoder.decode(localPart.substring(0, index), "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            throw new WebServiceException((Throwable)e);
        }
    }

    private static boolean isXMLMimeType(String mimeType) {
        return mimeType.equals("text/xml") || mimeType.equals("application/xml");
    }

    static {
        Map<Class, Object> m = primitiveUninitializedValues = new HashMap<Class, Object>();
        m.put(Integer.TYPE, 0);
        m.put(Character.TYPE, Character.valueOf('\u0000'));
        m.put(Byte.TYPE, (byte)0);
        m.put(Short.TYPE, (short)0);
        m.put(Long.TYPE, 0L);
        m.put(Float.TYPE, Float.valueOf(0.0f));
        m.put(Double.TYPE, 0.0);
    }

    public static final class RpcLit
    extends EndpointArgumentsBuilder {
        public RpcLit(WrapperParameter wp) {
            assert (wp.getTypeInfo().type == WrapperComposite.class);
            this.wrapperName = wp.getName();
            this.wrappedParts = new HashMap();
            List<ParameterImpl> children = wp.getWrapperChildren();
            for (ParameterImpl p : children) {
                this.wrappedParts.put(p.getName(), new WrappedPartBuilder(p.getXMLBridge(), EndpointValueSetter.get(p)));
                assert (p.getBinding() == ParameterBinding.BODY);
            }
        }

        @Override
        public void readRequest(Message msg, Object[] args) throws JAXBException, XMLStreamException {
            this.readWrappedRequest(msg, args);
        }
    }

    public static final class DocLit
    extends EndpointArgumentsBuilder {
        private final PartBuilder[] parts;
        private final XMLBridge wrapper;
        private boolean dynamicWrapper;

        public DocLit(WrapperParameter wp, WebParam.Mode skipMode) {
            this.wrapperName = wp.getName();
            this.wrapper = wp.getXMLBridge();
            Class wrapperType = (Class)this.wrapper.getTypeInfo().type;
            this.dynamicWrapper = WrapperComposite.class.equals((Object)wrapperType);
            ArrayList<PartBuilder> parts = new ArrayList<PartBuilder>();
            List<ParameterImpl> children = wp.getWrapperChildren();
            for (ParameterImpl p : children) {
                if (p.getMode() == skipMode) continue;
                QName name = p.getName();
                try {
                    if (this.dynamicWrapper) {
                        XMLBridge xmlBridge;
                        if (this.wrappedParts == null) {
                            this.wrappedParts = new HashMap();
                        }
                        if ((xmlBridge = p.getInlinedRepeatedElementBridge()) == null) {
                            xmlBridge = p.getXMLBridge();
                        }
                        this.wrappedParts.put(p.getName(), new WrappedPartBuilder(xmlBridge, EndpointValueSetter.get(p)));
                        continue;
                    }
                    parts.add(new PartBuilder(wp.getOwner().getBindingContext().getElementPropertyAccessor(wrapperType, name.getNamespaceURI(), p.getName().getLocalPart()), EndpointValueSetter.get(p)));
                    assert (p.getBinding() == ParameterBinding.BODY);
                }
                catch (JAXBException e) {
                    throw new WebServiceException(wrapperType + " do not have a property of the name " + name, (Throwable)e);
                }
            }
            this.parts = parts.toArray(new PartBuilder[parts.size()]);
        }

        @Override
        public void readRequest(Message msg, Object[] args) throws JAXBException, XMLStreamException {
            if (this.dynamicWrapper) {
                this.readWrappedRequest(msg, args);
            } else if (this.parts.length > 0) {
                if (!msg.hasPayload()) {
                    throw new WebServiceException("No payload. Expecting payload with " + this.wrapperName + " element");
                }
                XMLStreamReader reader = msg.readPayload();
                XMLStreamReaderUtil.verifyTag(reader, this.wrapperName);
                Object wrapperBean = this.wrapper.unmarshal(reader, (AttachmentUnmarshaller)(msg.getAttachments() != null ? new AttachmentUnmarshallerImpl(msg.getAttachments()) : null));
                try {
                    for (PartBuilder part : this.parts) {
                        part.readRequest(args, wrapperBean);
                    }
                }
                catch (DatabindingException e) {
                    throw new WebServiceException((Throwable)e);
                }
                reader.close();
                XMLStreamReaderFactory.recycle(reader);
            } else {
                msg.consume();
            }
        }

        static final class PartBuilder {
            private final PropertyAccessor accessor;
            private final EndpointValueSetter setter;

            public PartBuilder(PropertyAccessor accessor, EndpointValueSetter setter) {
                this.accessor = accessor;
                this.setter = setter;
                assert (accessor != null && setter != null);
            }

            final void readRequest(Object[] args, Object wrapperBean) {
                Object obj = this.accessor.get(wrapperBean);
                this.setter.put(obj, args);
            }
        }
    }

    public static final class Body
    extends EndpointArgumentsBuilder {
        private final XMLBridge<?> bridge;
        private final EndpointValueSetter setter;

        public Body(XMLBridge<?> bridge, EndpointValueSetter setter) {
            this.bridge = bridge;
            this.setter = setter;
        }

        @Override
        public void readRequest(Message msg, Object[] args) throws JAXBException {
            this.setter.put(msg.readPayloadAsJAXB(this.bridge), args);
        }
    }

    public static final class Header
    extends EndpointArgumentsBuilder {
        private final XMLBridge<?> bridge;
        private final EndpointValueSetter setter;
        private final QName headerName;
        private final SOAPVersion soapVersion;

        public Header(SOAPVersion soapVersion, QName name, XMLBridge<?> bridge, EndpointValueSetter setter) {
            this.soapVersion = soapVersion;
            this.headerName = name;
            this.bridge = bridge;
            this.setter = setter;
        }

        public Header(SOAPVersion soapVersion, ParameterImpl param, EndpointValueSetter setter) {
            this(soapVersion, param.getTypeInfo().tagName, param.getXMLBridge(), setter);
            assert (param.getOutBinding() == ParameterBinding.HEADER);
        }

        private SOAPFaultException createDuplicateHeaderException() {
            try {
                SOAPFault fault = this.soapVersion.getSOAPFactory().createFault();
                fault.setFaultCode(this.soapVersion.faultCodeClient);
                fault.setFaultString(ServerMessages.DUPLICATE_PORT_KNOWN_HEADER(this.headerName));
                return new SOAPFaultException(fault);
            }
            catch (SOAPException e) {
                throw new WebServiceException((Throwable)e);
            }
        }

        @Override
        public void readRequest(Message msg, Object[] args) throws JAXBException {
            com.sun.xml.ws.api.message.Header header = null;
            Iterator<com.sun.xml.ws.api.message.Header> it = msg.getHeaders().getHeaders(this.headerName, true);
            if (it.hasNext()) {
                header = it.next();
                if (it.hasNext()) {
                    throw this.createDuplicateHeaderException();
                }
            }
            if (header != null) {
                this.setter.put(header.readAsJAXB(this.bridge), args);
            }
        }
    }

    private static final class StringBuilder
    extends AttachmentBuilder {
        StringBuilder(ParameterImpl param, EndpointValueSetter setter) {
            super(param, setter);
        }

        @Override
        void mapAttachment(Attachment att, Object[] args) {
            att.getContentType();
            StringDataContentHandler sdh = new StringDataContentHandler();
            try {
                String str = (String)sdh.getContent(new DataHandlerDataSource(att.asDataHandler()));
                this.setter.put(str, args);
            }
            catch (Exception e) {
                throw new WebServiceException((Throwable)e);
            }
        }
    }

    private static final class JAXBBuilder
    extends AttachmentBuilder {
        JAXBBuilder(ParameterImpl param, EndpointValueSetter setter) {
            super(param, setter);
        }

        @Override
        void mapAttachment(Attachment att, Object[] args) throws JAXBException {
            Object obj = this.param.getXMLBridge().unmarshal(att.asInputStream());
            this.setter.put(obj, args);
        }
    }

    private static final class InputStreamBuilder
    extends AttachmentBuilder {
        InputStreamBuilder(ParameterImpl param, EndpointValueSetter setter) {
            super(param, setter);
        }

        @Override
        void mapAttachment(Attachment att, Object[] args) {
            this.setter.put(att.asInputStream(), args);
        }
    }

    private static final class ImageBuilder
    extends AttachmentBuilder {
        ImageBuilder(ParameterImpl param, EndpointValueSetter setter) {
            super(param, setter);
        }

        @Override
        void mapAttachment(Attachment att, Object[] args) {
            BufferedImage image;
            InputStream is = null;
            try {
                is = att.asInputStream();
                image = ImageIO.read(is);
            }
            catch (IOException ioe) {
                throw new WebServiceException((Throwable)ioe);
            }
            finally {
                if (is != null) {
                    try {
                        is.close();
                    }
                    catch (IOException ioe) {
                        throw new WebServiceException((Throwable)ioe);
                    }
                }
            }
            this.setter.put(image, args);
        }
    }

    private static final class SourceBuilder
    extends AttachmentBuilder {
        SourceBuilder(ParameterImpl param, EndpointValueSetter setter) {
            super(param, setter);
        }

        @Override
        void mapAttachment(Attachment att, Object[] args) {
            this.setter.put(att.asSource(), args);
        }
    }

    private static final class ByteArrayBuilder
    extends AttachmentBuilder {
        ByteArrayBuilder(ParameterImpl param, EndpointValueSetter setter) {
            super(param, setter);
        }

        @Override
        void mapAttachment(Attachment att, Object[] args) {
            this.setter.put(att.asByteArray(), args);
        }
    }

    private static final class DataHandlerBuilder
    extends AttachmentBuilder {
        DataHandlerBuilder(ParameterImpl param, EndpointValueSetter setter) {
            super(param, setter);
        }

        @Override
        void mapAttachment(Attachment att, Object[] args) {
            this.setter.put(att.asDataHandler(), args);
        }
    }

    public static abstract class AttachmentBuilder
    extends EndpointArgumentsBuilder {
        protected final EndpointValueSetter setter;
        protected final ParameterImpl param;
        protected final String pname;
        protected final String pname1;

        AttachmentBuilder(ParameterImpl param, EndpointValueSetter setter) {
            this.setter = setter;
            this.param = param;
            this.pname = param.getPartName();
            this.pname1 = "<" + this.pname;
        }

        public static EndpointArgumentsBuilder createAttachmentBuilder(ParameterImpl param, EndpointValueSetter setter) {
            Class type = (Class)param.getTypeInfo().type;
            if (DataHandler.class.isAssignableFrom(type)) {
                return new DataHandlerBuilder(param, setter);
            }
            if (byte[].class == type) {
                return new ByteArrayBuilder(param, setter);
            }
            if (Source.class.isAssignableFrom(type)) {
                return new SourceBuilder(param, setter);
            }
            if (Image.class.isAssignableFrom(type)) {
                return new ImageBuilder(param, setter);
            }
            if (InputStream.class == type) {
                return new InputStreamBuilder(param, setter);
            }
            if (EndpointArgumentsBuilder.isXMLMimeType(param.getBinding().getMimeType())) {
                return new JAXBBuilder(param, setter);
            }
            if (String.class.isAssignableFrom(type)) {
                return new StringBuilder(param, setter);
            }
            throw new UnsupportedOperationException("Unknown Type=" + type + " Attachment is not mapped.");
        }

        @Override
        public void readRequest(Message msg, Object[] args) throws JAXBException, XMLStreamException {
            boolean foundAttachment = false;
            for (Attachment att : msg.getAttachments()) {
                String part = AttachmentBuilder.getWSDLPartName(att);
                if (part == null || !part.equals(this.pname) && !part.equals(this.pname1)) continue;
                foundAttachment = true;
                this.mapAttachment(att, args);
                break;
            }
            if (!foundAttachment) {
                throw new WebServiceException("Missing Attachment for " + this.pname);
            }
        }

        abstract void mapAttachment(Attachment var1, Object[] var2) throws JAXBException;
    }

    public static final class Composite
    extends EndpointArgumentsBuilder {
        private final EndpointArgumentsBuilder[] builders;

        public Composite(EndpointArgumentsBuilder ... builders) {
            this.builders = builders;
        }

        public Composite(Collection<? extends EndpointArgumentsBuilder> builders) {
            this(builders.toArray(new EndpointArgumentsBuilder[builders.size()]));
        }

        @Override
        public void readRequest(Message msg, Object[] args) throws JAXBException, XMLStreamException {
            for (EndpointArgumentsBuilder builder : this.builders) {
                builder.readRequest(msg, args);
            }
        }
    }

    public static final class NullSetter
    extends EndpointArgumentsBuilder {
        private final EndpointValueSetter setter;
        private final Object nullValue;

        public NullSetter(EndpointValueSetter setter, Object nullValue) {
            assert (setter != null);
            this.nullValue = nullValue;
            this.setter = setter;
        }

        @Override
        public void readRequest(Message msg, Object[] args) {
            this.setter.put(this.nullValue, args);
        }
    }

    static final class WrappedPartBuilder {
        private final XMLBridge bridge;
        private final EndpointValueSetter setter;

        public WrappedPartBuilder(XMLBridge bridge, EndpointValueSetter setter) {
            this.bridge = bridge;
            this.setter = setter;
        }

        void readRequest(Object[] args, XMLStreamReader r, AttachmentSet att) throws JAXBException {
            AttachmentUnmarshallerImpl au;
            Object obj = null;
            AttachmentUnmarshallerImpl attachmentUnmarshallerImpl = au = att != null ? new AttachmentUnmarshallerImpl(att) : null;
            if (this.bridge instanceof RepeatedElementBridge) {
                RepeatedElementBridge rbridge = (RepeatedElementBridge)this.bridge;
                ArrayList list = new ArrayList();
                QName name = r.getName();
                while (r.getEventType() == 1 && name.equals(r.getName())) {
                    list.add(rbridge.unmarshal(r, (AttachmentUnmarshaller)au));
                    XMLStreamReaderUtil.toNextTag(r, name);
                }
                obj = rbridge.collectionHandler().convert(list);
            } else {
                obj = this.bridge.unmarshal(r, (AttachmentUnmarshaller)au);
            }
            this.setter.put(obj, args);
        }
    }

    static final class None
    extends EndpointArgumentsBuilder {
        private None() {
        }

        @Override
        public void readRequest(Message msg, Object[] args) {
            msg.consume();
        }
    }
}

