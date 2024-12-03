/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletResponse
 */
package org.apache.axis.transport.http;

import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.servlet.http.HttpServletResponse;
import org.apache.axis.AxisFault;
import org.apache.axis.ConfigurationException;
import org.apache.axis.Constants;
import org.apache.axis.MessageContext;
import org.apache.axis.description.ServiceDesc;
import org.apache.axis.server.AxisServer;
import org.apache.axis.transport.http.AbstractQueryStringHandler;
import org.apache.axis.transport.http.HTTPConstants;
import org.apache.axis.utils.Messages;
import org.apache.axis.utils.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class QSWSDLHandler
extends AbstractQueryStringHandler {
    public void invoke(MessageContext msgContext) throws AxisFault {
        block7: {
            this.configureFromContext(msgContext);
            AxisServer engine = (AxisServer)msgContext.getProperty("transport.http.plugin.engine");
            PrintWriter writer = (PrintWriter)msgContext.getProperty("transport.http.plugin.writer");
            HttpServletResponse response = (HttpServletResponse)msgContext.getProperty(HTTPConstants.MC_HTTP_SERVLETRESPONSE);
            try {
                engine.generateWSDL(msgContext);
                Document wsdlDoc = (Document)msgContext.getProperty("WSDL");
                if (wsdlDoc != null) {
                    try {
                        this.updateSoapAddressLocationURLs(wsdlDoc, msgContext);
                    }
                    catch (RuntimeException re) {
                        this.log.warn((Object)"Failed to update soap:address location URL(s) in WSDL.", (Throwable)re);
                    }
                    response.setContentType("text/xml; charset=" + XMLUtils.getEncoding().toLowerCase());
                    this.reportWSDL(wsdlDoc, writer);
                    break block7;
                }
                if (this.log.isDebugEnabled()) {
                    this.log.debug((Object)"processWsdlRequest: failed to create WSDL");
                }
                this.reportNoWSDL(response, writer, "noWSDL02", null);
            }
            catch (AxisFault axisFault) {
                if (axisFault.getFaultCode().equals(Constants.QNAME_NO_SERVICE_FAULT_CODE)) {
                    this.processAxisFault(axisFault);
                    response.setStatus(404);
                    this.reportNoWSDL(response, writer, "noWSDL01", axisFault);
                }
                throw axisFault;
            }
        }
    }

    public void reportWSDL(Document doc, PrintWriter writer) {
        XMLUtils.PrettyDocumentToWriter(doc, writer);
    }

    public void reportNoWSDL(HttpServletResponse res, PrintWriter writer, String moreDetailCode, AxisFault axisFault) {
        res.setStatus(404);
        res.setContentType("text/html");
        writer.println("<h2>" + Messages.getMessage("error00") + "</h2>");
        writer.println("<p>" + Messages.getMessage("noWSDL00") + "</p>");
        if (moreDetailCode != null) {
            writer.println("<p>" + Messages.getMessage(moreDetailCode) + "</p>");
        }
        if (axisFault != null && this.isDevelopment()) {
            this.writeFault(writer, axisFault);
        }
    }

    protected void updateSoapAddressLocationURLs(Document wsdlDoc, MessageContext msgContext) throws AxisFault {
        Set deployedServiceNames;
        try {
            deployedServiceNames = this.getDeployedServiceNames(msgContext);
        }
        catch (ConfigurationException ce) {
            throw new AxisFault("Failed to determine deployed service names.", ce);
        }
        NodeList wsdlPorts = wsdlDoc.getDocumentElement().getElementsByTagNameNS("http://schemas.xmlsoap.org/wsdl/", "port");
        if (wsdlPorts != null) {
            String endpointURL = this.getEndpointURL(msgContext);
            String baseEndpointURL = endpointURL.substring(0, endpointURL.lastIndexOf("/") + 1);
            for (int i = 0; i < wsdlPorts.getLength(); ++i) {
                Element portElem = (Element)wsdlPorts.item(i);
                Node portNameAttrib = portElem.getAttributes().getNamedItem("name");
                if (portNameAttrib == null) continue;
                String portName = portNameAttrib.getNodeValue();
                NodeList soapAddresses = portElem.getElementsByTagNameNS("http://schemas.xmlsoap.org/wsdl/soap/", "address");
                if (soapAddresses == null || soapAddresses.getLength() == 0) {
                    soapAddresses = portElem.getElementsByTagNameNS("http://schemas.xmlsoap.org/wsdl/soap12/", "address");
                }
                if (soapAddresses == null) continue;
                for (int j = 0; j < soapAddresses.getLength(); ++j) {
                    Element addressElem = (Element)soapAddresses.item(j);
                    Node addressLocationAttrib = addressElem.getAttributes().getNamedItem("location");
                    if (addressLocationAttrib == null) continue;
                    String addressLocation = addressLocationAttrib.getNodeValue();
                    String addressServiceName = addressLocation.substring(addressLocation.lastIndexOf("/") + 1);
                    String newServiceName = this.getNewServiceName(deployedServiceNames, addressServiceName, portName);
                    if (newServiceName != null) {
                        String newAddressLocation = baseEndpointURL + newServiceName;
                        addressLocationAttrib.setNodeValue(newAddressLocation);
                        this.log.debug((Object)("Setting soap:address location values in WSDL for port " + portName + " to: " + newAddressLocation));
                        continue;
                    }
                    this.log.debug((Object)("For WSDL port: " + portName + ", unable to match port name or the last component of " + "the SOAP address url with a " + "service name deployed in server-config.wsdd.  Leaving SOAP address: " + addressLocation + " unmodified."));
                }
            }
        }
    }

    private String getNewServiceName(Set deployedServiceNames, String currentServiceEndpointName, String portName) {
        String endpointName = null;
        if (deployedServiceNames.contains(currentServiceEndpointName)) {
            endpointName = currentServiceEndpointName;
        } else if (deployedServiceNames.contains(portName)) {
            endpointName = portName;
        }
        return endpointName;
    }

    private Set getDeployedServiceNames(MessageContext msgContext) throws ConfigurationException {
        HashSet<String> serviceNames = new HashSet<String>();
        Iterator deployedServicesIter = msgContext.getAxisEngine().getConfig().getDeployedServices();
        while (deployedServicesIter.hasNext()) {
            ServiceDesc serviceDesc = (ServiceDesc)deployedServicesIter.next();
            serviceNames.add(serviceDesc.getName());
        }
        return serviceNames;
    }

    protected String getEndpointURL(MessageContext msgContext) throws AxisFault {
        String locationUrl = msgContext.getStrProp("axis.wsdlgen.serv.loc.url");
        if (locationUrl == null) {
            locationUrl = msgContext.getService().getInitializedServiceDesc(msgContext).getEndpointURL();
        }
        if (locationUrl == null) {
            locationUrl = msgContext.getStrProp("transport.url");
        }
        return locationUrl;
    }
}

