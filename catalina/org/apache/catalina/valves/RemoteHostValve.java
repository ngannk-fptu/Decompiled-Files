/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletException
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 */
package org.apache.catalina.valves;

import java.io.IOException;
import javax.servlet.ServletException;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.valves.RequestFilterValve;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

public final class RemoteHostValve
extends RequestFilterValve {
    private static final Log log = LogFactory.getLog(RemoteHostValve.class);

    @Override
    public void invoke(Request request, Response response) throws IOException, ServletException {
        String property = this.getAddConnectorPort() ? request.getRequest().getRemoteHost() + ";" + request.getConnector().getPortWithOffset() : request.getRequest().getRemoteHost();
        this.process(property, request, response);
    }

    @Override
    protected Log getLog() {
        return log;
    }
}

