/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.ws.Service
 *  javax.xml.ws.WebEndpoint
 *  javax.xml.ws.WebServiceClient
 *  javax.xml.ws.WebServiceFeature
 */
package org.bedework.synch.wsmessages;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;
import org.bedework.synch.wsmessages.SynchRemoteServicePortType;

@WebServiceClient(name="SynchRemoteService", targetNamespace="http://www.bedework.org/synch/wsmessages", wsdlLocation="file:/Users/mike/bedework/quickstart-dev/bw-xml/target/checkout/src/main/wsdls/synchws/wssvc.wsdl")
public class SynchRemoteService
extends Service {
    private static final URL SYNCHREMOTESERVICE_WSDL_LOCATION;
    private static final Logger logger;

    public SynchRemoteService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public SynchRemoteService() {
        super(SYNCHREMOTESERVICE_WSDL_LOCATION, new QName("http://www.bedework.org/synch/wsmessages", "SynchRemoteService"));
    }

    @WebEndpoint(name="SynchRSPort")
    public SynchRemoteServicePortType getSynchRSPort() {
        return (SynchRemoteServicePortType)super.getPort(new QName("http://www.bedework.org/synch/wsmessages", "SynchRSPort"), SynchRemoteServicePortType.class);
    }

    @WebEndpoint(name="SynchRSPort")
    public SynchRemoteServicePortType getSynchRSPort(WebServiceFeature ... features) {
        return (SynchRemoteServicePortType)super.getPort(new QName("http://www.bedework.org/synch/wsmessages", "SynchRSPort"), SynchRemoteServicePortType.class, features);
    }

    static {
        logger = Logger.getLogger(SynchRemoteService.class.getName());
        URL url = null;
        try {
            URL baseUrl = SynchRemoteService.class.getResource(".");
            url = new URL(baseUrl, "file:/Users/mike/bedework/quickstart-dev/bw-xml/target/checkout/src/main/wsdls/synchws/wssvc.wsdl");
        }
        catch (MalformedURLException e) {
            logger.warning("Failed to create URL for the wsdl Location: 'file:/Users/mike/bedework/quickstart-dev/bw-xml/target/checkout/src/main/wsdls/synchws/wssvc.wsdl', retrying as a local file");
            logger.warning(e.getMessage());
        }
        SYNCHREMOTESERVICE_WSDL_LOCATION = url;
    }
}

