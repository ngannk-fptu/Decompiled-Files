/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.ws.Service
 *  javax.xml.ws.WebEndpoint
 *  javax.xml.ws.WebServiceClient
 *  javax.xml.ws.WebServiceFeature
 */
package org.oasis_open.docs.ws_calendar.ns.soap;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;
import org.oasis_open.docs.ws_calendar.ns.soap.CalWsServicePortType;

@WebServiceClient(name="CalWsService", targetNamespace="http://docs.oasis-open.org/ws-calendar/ns/soap", wsdlLocation="file:/Users/mike/bedework/quickstart-dev/bw-xml/target/checkout/src/main/wsdls/calws-soap/wssvc.wsdl")
public class CalWsService
extends Service {
    private static final URL CALWSSERVICE_WSDL_LOCATION;
    private static final Logger logger;

    public CalWsService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public CalWsService() {
        super(CALWSSERVICE_WSDL_LOCATION, new QName("http://docs.oasis-open.org/ws-calendar/ns/soap", "CalWsService"));
    }

    @WebEndpoint(name="CalWsPort")
    public CalWsServicePortType getCalWsPort() {
        return (CalWsServicePortType)super.getPort(new QName("http://docs.oasis-open.org/ws-calendar/ns/soap", "CalWsPort"), CalWsServicePortType.class);
    }

    @WebEndpoint(name="CalWsPort")
    public CalWsServicePortType getCalWsPort(WebServiceFeature ... features) {
        return (CalWsServicePortType)super.getPort(new QName("http://docs.oasis-open.org/ws-calendar/ns/soap", "CalWsPort"), CalWsServicePortType.class, features);
    }

    static {
        logger = Logger.getLogger(CalWsService.class.getName());
        URL url = null;
        try {
            URL baseUrl = CalWsService.class.getResource(".");
            url = new URL(baseUrl, "file:/Users/mike/bedework/quickstart-dev/bw-xml/target/checkout/src/main/wsdls/calws-soap/wssvc.wsdl");
        }
        catch (MalformedURLException e) {
            logger.warning("Failed to create URL for the wsdl Location: 'file:/Users/mike/bedework/quickstart-dev/bw-xml/target/checkout/src/main/wsdls/calws-soap/wssvc.wsdl', retrying as a local file");
            logger.warning(e.getMessage());
        }
        CALWSSERVICE_WSDL_LOCATION = url;
    }
}

