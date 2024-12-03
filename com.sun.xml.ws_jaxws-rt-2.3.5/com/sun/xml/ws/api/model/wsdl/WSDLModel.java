/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  com.sun.xml.ws.policy.PolicyMap
 */
package com.sun.xml.ws.api.model.wsdl;

import com.sun.istack.NotNull;
import com.sun.xml.ws.api.model.wsdl.WSDLBoundPortType;
import com.sun.xml.ws.api.model.wsdl.WSDLExtensible;
import com.sun.xml.ws.api.model.wsdl.WSDLMessage;
import com.sun.xml.ws.api.model.wsdl.WSDLPortType;
import com.sun.xml.ws.api.model.wsdl.WSDLService;
import com.sun.xml.ws.api.policy.PolicyResolver;
import com.sun.xml.ws.api.policy.PolicyResolverFactory;
import com.sun.xml.ws.api.server.Container;
import com.sun.xml.ws.api.wsdl.parser.WSDLParserExtension;
import com.sun.xml.ws.api.wsdl.parser.XMLEntityResolver;
import com.sun.xml.ws.policy.PolicyMap;
import com.sun.xml.ws.wsdl.parser.RuntimeWSDLParser;
import java.io.IOException;
import java.util.Map;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;

public interface WSDLModel
extends WSDLExtensible {
    public WSDLPortType getPortType(@NotNull QName var1);

    public WSDLBoundPortType getBinding(@NotNull QName var1);

    public WSDLBoundPortType getBinding(@NotNull QName var1, @NotNull QName var2);

    public WSDLService getService(@NotNull QName var1);

    @NotNull
    public Map<QName, ? extends WSDLPortType> getPortTypes();

    @NotNull
    public Map<QName, ? extends WSDLBoundPortType> getBindings();

    @NotNull
    public Map<QName, ? extends WSDLService> getServices();

    public QName getFirstServiceName();

    public WSDLMessage getMessage(QName var1);

    @NotNull
    public Map<QName, ? extends WSDLMessage> getMessages();

    public PolicyMap getPolicyMap();

    public static class WSDLParser {
        @NotNull
        public static WSDLModel parse(XMLEntityResolver.Parser wsdlEntityParser, XMLEntityResolver resolver, boolean isClientSide, WSDLParserExtension ... extensions) throws IOException, XMLStreamException, SAXException {
            return WSDLParser.parse(wsdlEntityParser, resolver, isClientSide, Container.NONE, extensions);
        }

        @NotNull
        public static WSDLModel parse(XMLEntityResolver.Parser wsdlEntityParser, XMLEntityResolver resolver, boolean isClientSide, @NotNull Container container, WSDLParserExtension ... extensions) throws IOException, XMLStreamException, SAXException {
            return WSDLParser.parse(wsdlEntityParser, resolver, isClientSide, container, PolicyResolverFactory.create(), extensions);
        }

        @NotNull
        public static WSDLModel parse(XMLEntityResolver.Parser wsdlEntityParser, XMLEntityResolver resolver, boolean isClientSide, @NotNull Container container, PolicyResolver policyResolver, WSDLParserExtension ... extensions) throws IOException, XMLStreamException, SAXException {
            return RuntimeWSDLParser.parse(wsdlEntityParser, resolver, isClientSide, container, policyResolver, extensions);
        }
    }
}

