/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.handlers;

import java.io.ByteArrayInputStream;
import org.apache.axis.AxisFault;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.handlers.BasicHandler;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.utils.Messages;
import org.apache.axis.utils.XMLUtils;
import org.apache.commons.logging.Log;
import org.w3c.dom.Document;

public class EchoHandler
extends BasicHandler {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$handlers$EchoHandler == null ? (class$org$apache$axis$handlers$EchoHandler = EchoHandler.class$("org.apache.axis.handlers.EchoHandler")) : class$org$apache$axis$handlers$EchoHandler).getName());
    public String wsdlStart1 = "<?xml version=\"1.0\" encoding=\"utf-8\" ?><definitions xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" \nxmlns:http=\"http://schemas.xmlsoap.org/wsdl/http/\" \nxmlns:mime=\"http://schemas.xmlsoap.org/wsdl/mime/\" \nxmlns:soap=\"http://schemas.xmlsoap.org/wsdl/soap/\" \nxmlns:soapenc=\"http://schemas.xmlsoap.org/soap/encoding/\" \nxmlns:s0=\"http://tempuri.org/EchoService\" \ntargetNamespace=\"http://tempuri.org/EchoService\" \nxmlns=\"http://schemas.xmlsoap.org/wsdl/\"><message name=\"request\"><part name=\"content\" type=\"xsd:anyType\" /></message><message name=\"response\"><part name=\"content\" element=\"xsd:anyType\" /></message><portType name=\"EchoSoap\"><operation name=\"doIt\"><input message=\"s0:request\" /> <output message=\"s0:response\" /> </operation></portType><binding name=\"EchoSoap\" type=\"s0:EchoSoap\"><soap:binding transport=\"http://schemas.xmlsoap.org/soap/http\" style=\"document\" /><operation name=\"doIt\"><soap:operation soapAction=\"http://tempuri.org/Echo\" style=\"document\" /><input><soap:body use=\"literal\" /></input><output><soap:body use=\"literal\" /></output></operation></binding><service name=\"Echo\"><port name=\"EchoSoap\" binding=\"s0:EchoSoap\"><soap:address location=\"http://";
    public String wsdlStart = "<?xml version=\"1.0\" encoding=\"UTF-8\"?> \n<wsdl:definitions targetNamespace=\"http://handlers.apache.org/EchoService\" \nxmlns=\"http://schemas.xmlsoap.org/wsdl/\" \nxmlns:apachesoap=\"http://xml.apache.org/xml-soap\"  \nxmlns:impl=\"http://handlers.apache.org/EchoService\"  \nxmlns:intf=\"http://handlers.apache.org/EchoService\"  \nxmlns:soapenc=\"http://schemas.xmlsoap.org/soap/encoding/\"  \nxmlns:wsdl=\"http://schemas.xmlsoap.org/wsdl/\"  \nxmlns:wsdlsoap=\"http://schemas.xmlsoap.org/wsdl/soap/\"  \nxmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"> \n<wsdl:types> \n<schema targetNamespace=\"http://handlers.apache.org/EchoService\" \nxmlns=\"http://www.w3.org/2001/XMLSchema\"> \n<xsd:import namespace=\"http://schemas.xmlsoap.org/soap/encoding/\"/> \n<xsd:complexType name=\"echoElements\"> \n <xsd:sequence> \n   <xsd:element name=\"content\" type=\"xsd:anyType\"/> \n </xsd:sequence>\n</xsd:complexType> \n<xsd:complexType name=\"echoElementsReturn\"> \n <xsd:sequence> \n   <xsd:element name=\"content\" type=\"xsd:anyType\"/> \n </xsd:sequence> \n</xsd:complexType> \n</schema> \n</wsdl:types> \n  <wsdl:message name=\"echoElementsResponse\"> \n    <wsdl:part type=\"impl:echoElementsReturn\" name=\"echoElementsReturn\"/> \n  </wsdl:message> \n  <wsdl:message name=\"echoElementsRequest\"> \n    <wsdl:part type=\"impl:echoElements\" name=\"part\"/> \n  </wsdl:message> \n  <wsdl:portType name=\"EchoService\"> \n    <wsdl:operation name=\"doIt\"> \n      <wsdl:input message=\"impl:echoElementsRequest\" name=\"echoElementsRequest\"/> \n      <wsdl:output message=\"impl:echoElementsResponse\" name=\"echoElementsResponse\"/> \n    </wsdl:operation> \n  </wsdl:portType> \n  <wsdl:binding name=\"EchoServiceSoapBinding\" type=\"impl:EchoService\"> \n    <wsdlsoap:binding style=\"document\" transport=\"http://schemas.xmlsoap.org/soap/http\"/> \n    <wsdl:operation name=\"doIt\"> \n      <wsdlsoap:operation soapAction=\"\"/> \n      <wsdl:input name=\"echoElementsRequest\"> \n        <wsdlsoap:body namespace=\"http://handlers.apache.org/EchoService\" use=\"literal\"/> \n      </wsdl:input> \n      <wsdl:output name=\"echoElementsResponse\"> \n        <wsdlsoap:body namespace=\"http://handlers.apache.org/EchoService\" use=\"literal\"/> \n      </wsdl:output> \n    </wsdl:operation> \n  </wsdl:binding> \n  <wsdl:service name=\"EchoService\"> \n    <wsdl:port binding=\"impl:EchoServiceSoapBinding\" name=\"EchoService\"> \n      <wsdlsoap:address location=\"";
    String wsdlEnd = " \"/></wsdl:port>\n</wsdl:service>\n</wsdl:definitions>\n";
    static /* synthetic */ Class class$org$apache$axis$handlers$EchoHandler;

    public void invoke(MessageContext msgContext) throws AxisFault {
        log.debug((Object)"Enter: EchoHandler::invoke");
        try {
            Message msg = msgContext.getRequestMessage();
            SOAPEnvelope env = msg.getSOAPEnvelope();
            msgContext.setResponseMessage(new Message(env));
        }
        catch (Exception e) {
            log.error((Object)Messages.getMessage("exception00"), (Throwable)e);
            throw AxisFault.makeFault(e);
        }
        log.debug((Object)"Exit: EchoHandler::invoke");
    }

    public void generateWSDL(MessageContext msgContext) throws AxisFault {
        try {
            String url = msgContext.getStrProp("transport.url");
            String wsdlString = this.wsdlStart + url + this.wsdlEnd;
            Document doc = XMLUtils.newDocument(new ByteArrayInputStream(wsdlString.getBytes("UTF-8")));
            msgContext.setProperty("WSDL", doc);
        }
        catch (Exception e) {
            throw AxisFault.makeFault(e);
        }
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

