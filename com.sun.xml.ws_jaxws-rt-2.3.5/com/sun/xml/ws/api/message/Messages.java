/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  com.sun.istack.Nullable
 *  com.sun.xml.stream.buffer.XMLStreamBuffer
 *  javax.xml.bind.JAXBContext
 *  javax.xml.bind.Marshaller
 *  javax.xml.soap.Detail
 *  javax.xml.soap.SOAPConstants
 *  javax.xml.soap.SOAPElement
 *  javax.xml.soap.SOAPException
 *  javax.xml.soap.SOAPFault
 *  javax.xml.soap.SOAPMessage
 *  javax.xml.ws.ProtocolException
 *  javax.xml.ws.WebServiceException
 */
package com.sun.xml.ws.api.message;

import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import com.sun.xml.stream.buffer.XMLStreamBuffer;
import com.sun.xml.ws.addressing.WsaTubeHelper;
import com.sun.xml.ws.addressing.model.MissingAddressingHeaderException;
import com.sun.xml.ws.api.SOAPVersion;
import com.sun.xml.ws.api.WSBinding;
import com.sun.xml.ws.api.addressing.AddressingVersion;
import com.sun.xml.ws.api.message.HeaderList;
import com.sun.xml.ws.api.message.Headers;
import com.sun.xml.ws.api.message.Message;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.message.saaj.SAAJFactory;
import com.sun.xml.ws.api.pipe.Codecs;
import com.sun.xml.ws.fault.SOAPFaultBuilder;
import com.sun.xml.ws.message.AttachmentSetImpl;
import com.sun.xml.ws.message.DOMMessage;
import com.sun.xml.ws.message.EmptyMessageImpl;
import com.sun.xml.ws.message.ProblemActionHeader;
import com.sun.xml.ws.message.jaxb.JAXBMessage;
import com.sun.xml.ws.message.source.PayloadSourceMessage;
import com.sun.xml.ws.message.source.ProtocolSourceMessage;
import com.sun.xml.ws.message.stream.PayloadStreamReaderMessage;
import com.sun.xml.ws.resources.AddressingMessages;
import com.sun.xml.ws.spi.db.BindingContextFactory;
import com.sun.xml.ws.streaming.XMLStreamReaderException;
import com.sun.xml.ws.streaming.XMLStreamReaderUtil;
import com.sun.xml.ws.util.DOMUtil;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import javax.xml.soap.Detail;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPMessage;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.ProtocolException;
import javax.xml.ws.WebServiceException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public abstract class Messages {
    private Messages() {
    }

    public static Message create(JAXBContext context, Object jaxbObject, SOAPVersion soapVersion) {
        return JAXBMessage.create(context, jaxbObject, soapVersion);
    }

    @Deprecated
    public static Message createRaw(JAXBContext context, Object jaxbObject, SOAPVersion soapVersion) {
        return JAXBMessage.createRaw(context, jaxbObject, soapVersion);
    }

    @Deprecated
    public static Message create(Marshaller marshaller, Object jaxbObject, SOAPVersion soapVersion) {
        return Messages.create(BindingContextFactory.getBindingContext(marshaller).getJAXBContext(), jaxbObject, soapVersion);
    }

    public static Message create(SOAPMessage saaj) {
        return SAAJFactory.create(saaj);
    }

    public static Message createUsingPayload(Source payload, SOAPVersion ver) {
        StreamSource ss;
        if (payload instanceof DOMSource ? ((DOMSource)payload).getNode() == null : (payload instanceof StreamSource ? (ss = (StreamSource)payload).getInputStream() == null && ss.getReader() == null && ss.getSystemId() == null : payload instanceof SAXSource && (ss = (SAXSource)payload).getInputSource() == null && ss.getXMLReader() == null)) {
            return new EmptyMessageImpl(ver);
        }
        return new PayloadSourceMessage(payload, ver);
    }

    public static Message createUsingPayload(XMLStreamReader payload, SOAPVersion ver) {
        return new PayloadStreamReaderMessage(payload, ver);
    }

    public static Message createUsingPayload(Element payload, SOAPVersion ver) {
        return new DOMMessage(ver, payload);
    }

    public static Message create(Element soapEnvelope) {
        Element body;
        SOAPVersion ver = SOAPVersion.fromNsUri(soapEnvelope.getNamespaceURI());
        Element header = DOMUtil.getFirstChild(soapEnvelope, ver.nsUri, "Header");
        HeaderList headers = null;
        if (header != null) {
            for (Node n = header.getFirstChild(); n != null; n = n.getNextSibling()) {
                if (n.getNodeType() != 1) continue;
                if (headers == null) {
                    headers = new HeaderList(ver);
                }
                headers.add(Headers.create((Element)n));
            }
        }
        if ((body = DOMUtil.getFirstChild(soapEnvelope, ver.nsUri, "Body")) == null) {
            throw new WebServiceException("Message doesn't have <S:Body> " + soapEnvelope);
        }
        Element payload = DOMUtil.getFirstChild(soapEnvelope, ver.nsUri, "Body");
        if (payload == null) {
            return new EmptyMessageImpl(headers, new AttachmentSetImpl(), ver);
        }
        return new DOMMessage(ver, headers, payload);
    }

    public static Message create(Source envelope, SOAPVersion soapVersion) {
        return new ProtocolSourceMessage(envelope, soapVersion);
    }

    public static Message createEmpty(SOAPVersion soapVersion) {
        return new EmptyMessageImpl(soapVersion);
    }

    @NotNull
    public static Message create(@NotNull XMLStreamReader reader) {
        if (reader.getEventType() != 1) {
            XMLStreamReaderUtil.nextElementContent(reader);
        }
        assert (reader.getEventType() == 1) : reader.getEventType();
        SOAPVersion ver = SOAPVersion.fromNsUri(reader.getNamespaceURI());
        return Codecs.createSOAPEnvelopeXmlCodec(ver).decode(reader);
    }

    @NotNull
    public static Message create(@NotNull XMLStreamBuffer xsb) {
        try {
            return Messages.create((XMLStreamReader)xsb.readAsXMLStreamReader());
        }
        catch (XMLStreamException e) {
            throw new XMLStreamReaderException(e);
        }
    }

    public static Message create(Throwable t, SOAPVersion soapVersion) {
        return SOAPFaultBuilder.createSOAPFaultMessage(soapVersion, null, t);
    }

    public static Message create(SOAPFault fault) {
        SOAPVersion ver = SOAPVersion.fromNsUri(fault.getNamespaceURI());
        return new DOMMessage(ver, (Element)fault);
    }

    public static Message createAddressingFaultMessage(WSBinding binding, QName missingHeader) {
        return Messages.createAddressingFaultMessage(binding, null, missingHeader);
    }

    public static Message createAddressingFaultMessage(WSBinding binding, Packet p, QName missingHeader) {
        AddressingVersion av = binding.getAddressingVersion();
        if (av == null) {
            throw new WebServiceException(AddressingMessages.ADDRESSING_SHOULD_BE_ENABLED());
        }
        WsaTubeHelper helper = av.getWsaHelper(null, null, binding);
        return Messages.create(helper.newMapRequiredFault(new MissingAddressingHeaderException(missingHeader, p)));
    }

    public static Message create(@NotNull String unsupportedAction, @NotNull AddressingVersion av, @NotNull SOAPVersion sv) {
        Message faultMessage;
        QName subcode = av.actionNotSupportedTag;
        String faultstring = String.format(av.actionNotSupportedText, unsupportedAction);
        try {
            SOAPFault fault;
            if (sv == SOAPVersion.SOAP_12) {
                fault = SOAPVersion.SOAP_12.getSOAPFactory().createFault();
                fault.setFaultCode(SOAPConstants.SOAP_SENDER_FAULT);
                fault.appendFaultSubcode(subcode);
                Detail detail = fault.addDetail();
                SOAPElement se = detail.addChildElement(av.problemActionTag);
                se = se.addChildElement(av.actionTag);
                se.addTextNode(unsupportedAction);
            } else {
                fault = SOAPVersion.SOAP_11.getSOAPFactory().createFault();
                fault.setFaultCode(subcode);
            }
            fault.setFaultString(faultstring);
            faultMessage = SOAPFaultBuilder.createSOAPFaultMessage(sv, fault);
            if (sv == SOAPVersion.SOAP_11) {
                faultMessage.getHeaders().add(new ProblemActionHeader(unsupportedAction, av));
            }
        }
        catch (SOAPException e) {
            throw new WebServiceException((Throwable)e);
        }
        return faultMessage;
    }

    @NotNull
    public static Message create(@NotNull SOAPVersion soapVersion, @NotNull ProtocolException pex, @Nullable QName faultcode) {
        return SOAPFaultBuilder.createSOAPFaultMessage(soapVersion, pex, faultcode);
    }
}

