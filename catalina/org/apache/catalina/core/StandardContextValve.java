/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletException
 *  org.apache.coyote.ContinueResponseTiming
 *  org.apache.tomcat.util.buf.MessageBytes
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.catalina.core;

import java.io.IOException;
import javax.servlet.ServletException;
import org.apache.catalina.Wrapper;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.valves.ValveBase;
import org.apache.coyote.ContinueResponseTiming;
import org.apache.tomcat.util.buf.MessageBytes;
import org.apache.tomcat.util.res.StringManager;

final class StandardContextValve
extends ValveBase {
    private static final StringManager sm = StringManager.getManager(StandardContextValve.class);

    StandardContextValve() {
        super(true);
    }

    @Override
    public void invoke(Request request, Response response) throws IOException, ServletException {
        MessageBytes requestPathMB = request.getRequestPathMB();
        if (requestPathMB.startsWithIgnoreCase("/META-INF/", 0) || requestPathMB.equalsIgnoreCase("/META-INF") || requestPathMB.startsWithIgnoreCase("/WEB-INF/", 0) || requestPathMB.equalsIgnoreCase("/WEB-INF")) {
            response.sendError(404);
            return;
        }
        Wrapper wrapper = request.getWrapper();
        if (wrapper == null || wrapper.isUnavailable()) {
            response.sendError(404);
            return;
        }
        try {
            response.sendAcknowledgement(ContinueResponseTiming.IMMEDIATELY);
        }
        catch (IOException ioe) {
            this.container.getLogger().error((Object)sm.getString("standardContextValve.acknowledgeException"), (Throwable)ioe);
            request.setAttribute("javax.servlet.error.exception", ioe);
            response.sendError(500);
            return;
        }
        if (request.isAsyncSupported()) {
            request.setAsyncSupported(wrapper.getPipeline().isAsyncSupported());
        }
        wrapper.getPipeline().getFirst().invoke(request, response);
    }
}

