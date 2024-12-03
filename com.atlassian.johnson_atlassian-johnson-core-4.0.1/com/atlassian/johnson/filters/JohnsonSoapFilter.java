/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.johnson.filters;

import com.atlassian.johnson.JohnsonEventContainer;
import com.atlassian.johnson.filters.AbstractJohnsonFilter;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JohnsonSoapFilter
extends AbstractJohnsonFilter {
    public static final Logger LOG = LoggerFactory.getLogger(JohnsonSoapFilter.class);

    protected String buildSoapFault(String errorMessage) {
        return "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"\n                  xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n    <soapenv:Body>\n        <soapenv:Fault>\n            <faultcode>soapenv:Server</faultcode>\n            <faultstring>" + errorMessage + "            </faultstring>\n        </soapenv:Fault>\n    </soapenv:Body>\n</soapenv:Envelope>";
    }

    @Override
    protected void handleError(JohnsonEventContainer appEventContainer, HttpServletRequest servletRequest, HttpServletResponse servletResponse) throws IOException {
        LOG.info("The application is unavailable, or there are errors.  Returning a SOAP fault with the event message.");
        servletResponse.setContentType("text/xml;charset=utf-8");
        String message = this.getStringForEvents(appEventContainer.getEvents());
        servletResponse.setStatus(503);
        servletResponse.getWriter().write(this.buildSoapFault(message));
    }

    @Override
    protected void handleNotSetup(HttpServletRequest servletRequest, HttpServletResponse servletResponse) throws IOException {
        LOG.info("The application is not setup.  Returning a SOAP fault with a 'not setup' message.");
        servletResponse.setContentType("text/xml;charset=utf-8");
        String message = "The application has not yet been setup.";
        servletResponse.setStatus(503);
        servletResponse.getWriter().write(this.buildSoapFault(message));
    }
}

