/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletException
 *  org.apache.juli.logging.Log
 */
package org.apache.catalina.valves;

import java.io.IOException;
import java.util.regex.Pattern;
import javax.servlet.ServletException;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.valves.ValveBase;
import org.apache.juli.logging.Log;

public abstract class RequestFilterValve
extends ValveBase {
    protected volatile Pattern allow = null;
    protected volatile String allowValue = null;
    protected volatile boolean allowValid = true;
    protected volatile Pattern deny = null;
    protected volatile String denyValue = null;
    protected volatile boolean denyValid = true;
    protected int denyStatus = 403;
    private boolean invalidAuthenticationWhenDeny = false;
    private volatile boolean addConnectorPort = false;
    private volatile boolean usePeerAddress = false;

    public RequestFilterValve() {
        super(true);
    }

    public String getAllow() {
        return this.allowValue;
    }

    public void setAllow(String allow) {
        if (allow == null || allow.length() == 0) {
            this.allow = null;
            this.allowValue = null;
            this.allowValid = true;
        } else {
            boolean success = false;
            try {
                this.allowValue = allow;
                this.allow = Pattern.compile(allow);
                success = true;
            }
            finally {
                this.allowValid = success;
            }
        }
    }

    public String getDeny() {
        return this.denyValue;
    }

    public void setDeny(String deny) {
        if (deny == null || deny.length() == 0) {
            this.deny = null;
            this.denyValue = null;
            this.denyValid = true;
        } else {
            boolean success = false;
            try {
                this.denyValue = deny;
                this.deny = Pattern.compile(deny);
                success = true;
            }
            finally {
                this.denyValid = success;
            }
        }
    }

    public final boolean isAllowValid() {
        return this.allowValid;
    }

    public final boolean isDenyValid() {
        return this.denyValid;
    }

    public int getDenyStatus() {
        return this.denyStatus;
    }

    public void setDenyStatus(int denyStatus) {
        this.denyStatus = denyStatus;
    }

    public boolean getInvalidAuthenticationWhenDeny() {
        return this.invalidAuthenticationWhenDeny;
    }

    public void setInvalidAuthenticationWhenDeny(boolean value) {
        this.invalidAuthenticationWhenDeny = value;
    }

    public boolean getAddConnectorPort() {
        return this.addConnectorPort;
    }

    public void setAddConnectorPort(boolean addConnectorPort) {
        this.addConnectorPort = addConnectorPort;
    }

    public boolean getUsePeerAddress() {
        return this.usePeerAddress;
    }

    public void setUsePeerAddress(boolean usePeerAddress) {
        this.usePeerAddress = usePeerAddress;
    }

    @Override
    public abstract void invoke(Request var1, Response var2) throws IOException, ServletException;

    @Override
    protected void initInternal() throws LifecycleException {
        super.initInternal();
        if (!this.allowValid || !this.denyValid) {
            throw new LifecycleException(sm.getString("requestFilterValve.configInvalid"));
        }
    }

    @Override
    protected synchronized void startInternal() throws LifecycleException {
        if (!this.allowValid || !this.denyValid) {
            throw new LifecycleException(sm.getString("requestFilterValve.configInvalid"));
        }
        super.startInternal();
    }

    protected void process(String property, Request request, Response response) throws IOException, ServletException {
        if (this.isAllowed(property)) {
            this.getNext().invoke(request, response);
            return;
        }
        if (this.getLog().isDebugEnabled()) {
            this.getLog().debug((Object)sm.getString("requestFilterValve.deny", new Object[]{request.getRequestURI(), property}));
        }
        this.denyRequest(request, response);
    }

    protected abstract Log getLog();

    protected void denyRequest(Request request, Response response) throws IOException, ServletException {
        Context context;
        if (this.invalidAuthenticationWhenDeny && (context = request.getContext()) != null && context.getPreemptiveAuthentication()) {
            if (request.getCoyoteRequest().getMimeHeaders().getValue("authorization") == null) {
                request.getCoyoteRequest().getMimeHeaders().addValue("authorization").setString("invalid");
            }
            this.getNext().invoke(request, response);
            return;
        }
        response.sendError(this.denyStatus);
    }

    public boolean isAllowed(String property) {
        Pattern deny = this.deny;
        Pattern allow = this.allow;
        if (deny != null && deny.matcher(property).matches()) {
            return false;
        }
        if (allow != null && allow.matcher(property).matches()) {
            return true;
        }
        return deny != null && allow == null;
    }
}

