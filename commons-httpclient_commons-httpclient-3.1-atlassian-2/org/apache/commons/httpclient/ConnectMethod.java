/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.commons.httpclient;

import java.io.IOException;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpConnection;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.Wire;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ConnectMethod
extends HttpMethodBase {
    public static final String NAME = "CONNECT";
    private final HostConfiguration targethost;
    private static final Log LOG = LogFactory.getLog(ConnectMethod.class);

    public ConnectMethod() {
        this.targethost = null;
    }

    public ConnectMethod(HttpMethod method) {
        this.targethost = null;
    }

    public ConnectMethod(HostConfiguration targethost) {
        if (targethost == null) {
            throw new IllegalArgumentException("Target host may not be null");
        }
        this.targethost = targethost;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getPath() {
        if (this.targethost != null) {
            StringBuffer buffer = new StringBuffer();
            buffer.append(this.targethost.getHost());
            int port = this.targethost.getPort();
            if (port == -1) {
                port = this.targethost.getProtocol().getDefaultPort();
            }
            buffer.append(':');
            buffer.append(port);
            return buffer.toString();
        }
        return "/";
    }

    @Override
    public URI getURI() throws URIException {
        String charset = this.getParams().getUriCharset();
        return new URI(this.getPath(), true, charset);
    }

    @Override
    protected void addCookieRequestHeader(HttpState state, HttpConnection conn) throws IOException, HttpException {
    }

    @Override
    protected void addRequestHeaders(HttpState state, HttpConnection conn) throws IOException, HttpException {
        LOG.trace((Object)"enter ConnectMethod.addRequestHeaders(HttpState, HttpConnection)");
        this.addUserAgentRequestHeader(state, conn);
        this.addHostRequestHeader(state, conn);
        this.addProxyConnectionHeader(state, conn);
    }

    @Override
    public int execute(HttpState state, HttpConnection conn) throws IOException, HttpException {
        LOG.trace((Object)"enter ConnectMethod.execute(HttpState, HttpConnection)");
        int code = super.execute(state, conn);
        if (LOG.isDebugEnabled()) {
            LOG.debug((Object)("CONNECT status code " + code));
        }
        return code;
    }

    @Override
    protected void writeRequestLine(HttpState state, HttpConnection conn) throws IOException, HttpException {
        StringBuffer buffer = new StringBuffer();
        buffer.append(this.getName());
        buffer.append(' ');
        if (this.targethost != null) {
            buffer.append(this.getPath());
        } else {
            int port = conn.getPort();
            if (port == -1) {
                port = conn.getProtocol().getDefaultPort();
            }
            buffer.append(conn.getHost());
            buffer.append(':');
            buffer.append(port);
        }
        buffer.append(" ");
        buffer.append(this.getEffectiveVersion());
        String line = buffer.toString();
        conn.printLine(line, this.getParams().getHttpElementCharset());
        if (Wire.HEADER_WIRE.enabled()) {
            Wire.HEADER_WIRE.output(line);
        }
    }

    @Override
    protected boolean shouldCloseConnection(HttpConnection conn) {
        if (this.getStatusCode() == 200) {
            Header connectionHeader = null;
            if (!conn.isTransparent()) {
                connectionHeader = this.getResponseHeader("proxy-connection");
            }
            if (connectionHeader == null) {
                connectionHeader = this.getResponseHeader("connection");
            }
            if (connectionHeader != null && connectionHeader.getValue().equalsIgnoreCase("close") && LOG.isWarnEnabled()) {
                LOG.warn((Object)("Invalid header encountered '" + connectionHeader.toExternalForm() + "' in response " + this.getStatusLine().toString()));
            }
            return false;
        }
        return super.shouldCloseConnection(conn);
    }
}

