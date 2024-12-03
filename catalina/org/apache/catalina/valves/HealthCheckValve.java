/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletException
 *  org.apache.tomcat.util.buf.MessageBytes
 */
package org.apache.catalina.valves;

import java.io.IOException;
import javax.servlet.ServletException;
import org.apache.catalina.Container;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.util.LifecycleBase;
import org.apache.catalina.valves.ValveBase;
import org.apache.tomcat.util.buf.MessageBytes;

public class HealthCheckValve
extends ValveBase {
    private static final String UP = "{\n  \"status\": \"UP\",\n  \"checks\": []\n}";
    private static final String DOWN = "{\n  \"status\": \"DOWN\",\n  \"checks\": []\n}";
    private String path = "/health";
    protected boolean context = false;
    protected boolean checkContainersAvailable = true;

    public HealthCheckValve() {
        super(true);
    }

    public final String getPath() {
        return this.path;
    }

    public final void setPath(String path) {
        this.path = path;
    }

    public boolean getCheckContainersAvailable() {
        return this.checkContainersAvailable;
    }

    public void setCheckContainersAvailable(boolean checkContainersAvailable) {
        this.checkContainersAvailable = checkContainersAvailable;
    }

    @Override
    protected synchronized void startInternal() throws LifecycleException {
        super.startInternal();
        this.context = this.getContainer() instanceof Context;
    }

    @Override
    public void invoke(Request request, Response response) throws IOException, ServletException {
        MessageBytes urlMB;
        MessageBytes messageBytes = urlMB = this.context ? request.getRequestPathMB() : request.getDecodedRequestURIMB();
        if (urlMB.equals(this.path)) {
            response.setContentType("application/json");
            if (!this.checkContainersAvailable || this.isAvailable(this.getContainer())) {
                response.getOutputStream().print(UP);
            } else {
                response.setStatus(503);
                response.getOutputStream().print(DOWN);
            }
        } else {
            this.getNext().invoke(request, response);
        }
    }

    protected boolean isAvailable(Container container) {
        for (Container child : container.findChildren()) {
            if (this.isAvailable(child)) continue;
            return false;
        }
        if (container instanceof LifecycleBase) {
            return ((LifecycleBase)((Object)container)).getState().isAvailable();
        }
        return true;
    }
}

