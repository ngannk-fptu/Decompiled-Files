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

public class JohnsonXmlRpcFilter
extends AbstractJohnsonFilter {
    public static final int FAULT_CODE = 0;
    private static final Logger LOG = LoggerFactory.getLogger(JohnsonXmlRpcFilter.class);

    protected String buildXmlRpcErrorMessage(String error, int faultCode) {
        return "<?xml version=\"1.0\"?>\n<methodResponse>\n    <fault>\n        <value>\n            <struct>\n                <member>\n                    <name>faultString</name>\n                    <value>" + error + "</value>\n                </member>\n                <member>\n                    <name>faultCode</name>\n                    <value>\n                        <int>" + faultCode + "</int>\n                    </value>\n                </member>\n            </struct>\n        </value>\n    </fault>\n</methodResponse>";
    }

    @Override
    protected void handleError(JohnsonEventContainer appEventContainer, HttpServletRequest servletRequest, HttpServletResponse servletResponse) throws IOException {
        LOG.info("The application is unavailable, or there are errors.  Returning a SOAP fault with the event message.");
        servletResponse.setContentType("text/xml;charset=utf-8");
        String message = this.getStringForEvents(appEventContainer.getEvents());
        servletResponse.setStatus(503);
        servletResponse.getWriter().write(this.buildXmlRpcErrorMessage(message, 0));
    }

    @Override
    protected void handleNotSetup(HttpServletRequest servletRequest, HttpServletResponse servletResponse) throws IOException {
        LOG.info("The application is not setup.  Returning a SOAP fault with a 'not setup' message.");
        servletResponse.setContentType("text/xml;charset=utf-8");
        String message = "The application has not yet been setup.";
        servletResponse.setStatus(503);
        servletResponse.getWriter().write(this.buildXmlRpcErrorMessage(message, 0));
    }
}

