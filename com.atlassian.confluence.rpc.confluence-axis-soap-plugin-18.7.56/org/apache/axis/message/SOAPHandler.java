/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.message;

import java.io.CharArrayWriter;
import javax.xml.soap.SOAPException;
import org.apache.axis.AxisFault;
import org.apache.axis.Constants;
import org.apache.axis.encoding.DeserializationContext;
import org.apache.axis.encoding.TypeMappingRegistry;
import org.apache.axis.message.MessageElement;
import org.apache.axis.soap.SOAPConstants;
import org.apache.axis.utils.Messages;
import org.apache.axis.utils.StringUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class SOAPHandler
extends DefaultHandler {
    public MessageElement myElement = null;
    private MessageElement[] myElements;
    private int myIndex = 0;
    private CharArrayWriter val;

    public SOAPHandler() {
    }

    public SOAPHandler(MessageElement[] elements, int index) {
        this.myElements = elements;
        this.myIndex = index;
    }

    public void startElement(String namespace, String localName, String prefix, Attributes attributes, DeserializationContext context) throws SAXException {
        TypeMappingRegistry tmr;
        String encodingStyle;
        SOAPConstants soapConstants = context.getSOAPConstants();
        if (!(soapConstants != SOAPConstants.SOAP12_CONSTANTS || (encodingStyle = attributes.getValue("http://www.w3.org/2003/05/soap-envelope", "encodingStyle")) == null || encodingStyle.equals("") || encodingStyle.equals("http://www.w3.org/2003/05/soap-envelope/encoding/none") || Constants.isSOAP_ENC(encodingStyle) || (tmr = context.getTypeMappingRegistry()).getTypeMapping(encodingStyle) != tmr.getDefaultTypeMapping())) {
            AxisFault fault = new AxisFault(Constants.FAULT_SOAP12_DATAENCODINGUNKNOWN, null, Messages.getMessage("invalidEncodingStyle"), null, null, null);
            throw new SAXException(fault);
        }
        if (!context.isDoneParsing() && !context.isProcessingRef()) {
            if (this.myElement == null) {
                try {
                    this.myElement = this.makeNewElement(namespace, localName, prefix, attributes, context);
                }
                catch (AxisFault axisFault) {
                    throw new SAXException(axisFault);
                }
            }
            context.pushNewElement(this.myElement);
        }
    }

    public MessageElement makeNewElement(String namespace, String localName, String prefix, Attributes attributes, DeserializationContext context) throws AxisFault {
        return new MessageElement(namespace, localName, prefix, attributes, context);
    }

    public void endElement(String namespace, String localName, DeserializationContext context) throws SAXException {
        if (this.myElement != null) {
            this.addTextNode();
            if (this.myElements != null) {
                this.myElements[this.myIndex] = this.myElement;
            }
            this.myElement.setEndIndex(context.getCurrentRecordPos());
        }
    }

    public SOAPHandler onStartChild(String namespace, String localName, String prefix, Attributes attributes, DeserializationContext context) throws SAXException {
        this.addTextNode();
        SOAPHandler handler = new SOAPHandler();
        return handler;
    }

    private void addTextNode() throws SAXException {
        if (this.myElement != null && this.val != null && this.val.size() > 0) {
            String s = StringUtils.strip(this.val.toString());
            this.val.reset();
            if (s.length() > 0) {
                try {
                    this.myElement.addTextNode(s);
                }
                catch (SOAPException e) {
                    throw new SAXException(e);
                }
            }
        }
    }

    public void onEndChild(String namespace, String localName, DeserializationContext context) throws SAXException {
    }

    public void characters(char[] chars, int start, int end) throws SAXException {
        if (this.val == null) {
            this.val = new CharArrayWriter();
        }
        this.val.write(chars, start, end);
    }
}

