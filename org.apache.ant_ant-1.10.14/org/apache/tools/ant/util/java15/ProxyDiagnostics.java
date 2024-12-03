/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.util.java15;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import org.apache.tools.ant.BuildException;

public class ProxyDiagnostics {
    private URI destURI;
    public static final String DEFAULT_DESTINATION = "https://ant.apache.org/";

    public ProxyDiagnostics(String destination) {
        try {
            this.destURI = new URI(destination);
        }
        catch (URISyntaxException e) {
            throw new BuildException(e);
        }
    }

    public ProxyDiagnostics() {
        this(DEFAULT_DESTINATION);
    }

    public String toString() {
        ProxySelector selector = ProxySelector.getDefault();
        StringBuilder result = new StringBuilder();
        for (Proxy proxy : selector.select(this.destURI)) {
            SocketAddress address = proxy.address();
            if (address == null) {
                result.append("Direct connection\n");
                continue;
            }
            result.append(proxy);
            if (address instanceof InetSocketAddress) {
                InetSocketAddress ina = (InetSocketAddress)address;
                result.append(' ');
                result.append(ina.getHostName());
                result.append(':');
                result.append(ina.getPort());
                if (ina.isUnresolved()) {
                    result.append(" [unresolved]");
                } else {
                    InetAddress addr = ina.getAddress();
                    result.append(" [");
                    result.append(addr.getHostAddress());
                    result.append(']');
                }
            }
            result.append('\n');
        }
        return result.toString();
    }
}

