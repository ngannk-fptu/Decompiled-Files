/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.coyote.ActionCode
 *  org.apache.coyote.UpgradeProtocol
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.net.SSLHostConfig
 */
package org.apache.catalina.authenticator;

import java.io.IOException;
import java.security.Principal;
import java.security.cert.X509Certificate;
import javax.servlet.http.HttpServletResponse;
import org.apache.catalina.Container;
import org.apache.catalina.Context;
import org.apache.catalina.Engine;
import org.apache.catalina.Host;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.authenticator.AuthenticatorBase;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.connector.Request;
import org.apache.coyote.ActionCode;
import org.apache.coyote.UpgradeProtocol;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.net.SSLHostConfig;

public class SSLAuthenticator
extends AuthenticatorBase {
    private final Log log = LogFactory.getLog(SSLAuthenticator.class);

    @Override
    protected boolean doAuthenticate(Request request, HttpServletResponse response) throws IOException {
        X509Certificate[] certs;
        if (this.checkForCachedAuthentication(request, response, false)) {
            return true;
        }
        if (this.containerLog.isDebugEnabled()) {
            this.containerLog.debug((Object)" Looking up certificates");
        }
        if ((certs = this.getRequestCertificates(request)) == null || certs.length < 1) {
            if (this.containerLog.isDebugEnabled()) {
                this.containerLog.debug((Object)"  No certificates included with this request");
            }
            response.sendError(401, sm.getString("authenticator.certificates"));
            return false;
        }
        Principal principal = this.context.getRealm().authenticate(certs);
        if (principal == null) {
            if (this.containerLog.isDebugEnabled()) {
                this.containerLog.debug((Object)"  Realm.authenticate() returned false");
            }
            response.sendError(401, sm.getString("authenticator.unauthorized"));
            return false;
        }
        this.register(request, response, principal, "CLIENT_CERT", null, null);
        return true;
    }

    @Override
    protected String getAuthMethod() {
        return "CLIENT_CERT";
    }

    @Override
    protected boolean isPreemptiveAuthPossible(Request request) {
        X509Certificate[] certs = this.getRequestCertificates(request);
        return certs != null && certs.length > 0;
    }

    protected X509Certificate[] getRequestCertificates(Request request) throws IllegalStateException {
        X509Certificate[] certs = (X509Certificate[])request.getAttribute("javax.servlet.request.X509Certificate");
        if (certs == null || certs.length < 1) {
            try {
                request.getCoyoteRequest().action(ActionCode.REQ_SSL_CERTIFICATE, null);
                certs = (X509Certificate[])request.getAttribute("javax.servlet.request.X509Certificate");
            }
            catch (IllegalStateException illegalStateException) {
                // empty catch block
            }
        }
        return certs;
    }

    @Override
    protected synchronized void startInternal() throws LifecycleException {
        Connector[] connectors;
        super.startInternal();
        Container container = this.getContainer();
        if (!(container instanceof Context)) {
            return;
        }
        Context context = (Context)container;
        if (!((container = context.getParent()) instanceof Host)) {
            return;
        }
        Host host = (Host)container;
        if (!((container = host.getParent()) instanceof Engine)) {
            return;
        }
        Engine engine = (Engine)container;
        for (Connector connector : connectors = engine.getService().findConnectors()) {
            SSLHostConfig[] sslHostConfigs;
            UpgradeProtocol[] upgradeProtocols;
            for (UpgradeProtocol upgradeProtocol : upgradeProtocols = connector.findUpgradeProtocols()) {
                if (!"h2".equals(upgradeProtocol.getAlpnName())) continue;
                this.log.warn((Object)sm.getString("sslAuthenticatorValve.http2", new Object[]{context.getName(), host.getName(), connector}));
                break;
            }
            for (SSLHostConfig sslHostConfig : sslHostConfigs = connector.findSslHostConfigs()) {
                if (sslHostConfig.isTls13RenegotiationAvailable()) continue;
                String[] enabledProtocols = sslHostConfig.getEnabledProtocols();
                if (enabledProtocols == null) {
                    enabledProtocols = sslHostConfig.getProtocols().toArray(new String[0]);
                }
                for (String enbabledProtocol : enabledProtocols) {
                    if (!"TLSv1.3".equals(enbabledProtocol)) continue;
                    this.log.warn((Object)sm.getString("sslAuthenticatorValve.tls13", new Object[]{context.getName(), host.getName(), connector}));
                }
            }
        }
    }
}

