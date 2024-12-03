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
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.servlet.ServletException;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.util.NetMask;
import org.apache.catalina.valves.RequestFilterValve;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

public final class RemoteCIDRValve
extends RequestFilterValve {
    private static final Log log = LogFactory.getLog(RemoteCIDRValve.class);
    private final List<NetMask> allow = new ArrayList<NetMask>();
    private final List<NetMask> deny = new ArrayList<NetMask>();

    @Override
    public String getAllow() {
        return this.allow.toString().replace("[", "").replace("]", "");
    }

    @Override
    public void setAllow(String input) {
        List<String> messages = this.fillFromInput(input, this.allow);
        if (messages.isEmpty()) {
            return;
        }
        this.allowValid = false;
        for (String message : messages) {
            log.error((Object)message);
        }
        throw new IllegalArgumentException(sm.getString("remoteCidrValve.invalid", new Object[]{"allow"}));
    }

    @Override
    public String getDeny() {
        return this.deny.toString().replace("[", "").replace("]", "");
    }

    @Override
    public void setDeny(String input) {
        List<String> messages = this.fillFromInput(input, this.deny);
        if (messages.isEmpty()) {
            return;
        }
        this.denyValid = false;
        for (String message : messages) {
            log.error((Object)message);
        }
        throw new IllegalArgumentException(sm.getString("remoteCidrValve.invalid", new Object[]{"deny"}));
    }

    @Override
    public void invoke(Request request, Response response) throws IOException, ServletException {
        String property = this.getUsePeerAddress() ? request.getPeerAddr() : request.getRequest().getRemoteAddr();
        if (this.getAddConnectorPort()) {
            property = property + ";" + request.getConnector().getPortWithOffset();
        }
        this.process(property, request, response);
    }

    @Override
    public boolean isAllowed(String property) {
        InetAddress addr;
        String nonPortPart;
        int port;
        int portIdx = property.indexOf(59);
        if (portIdx == -1) {
            if (this.getAddConnectorPort()) {
                log.error((Object)sm.getString("remoteCidrValve.noPort"));
                return false;
            }
            port = -1;
            nonPortPart = property;
        } else {
            if (!this.getAddConnectorPort()) {
                log.error((Object)sm.getString("remoteCidrValve.unexpectedPort"));
                return false;
            }
            nonPortPart = property.substring(0, portIdx);
            try {
                port = Integer.parseInt(property.substring(portIdx + 1));
            }
            catch (NumberFormatException e) {
                log.error((Object)sm.getString("remoteCidrValve.noPort"), (Throwable)e);
                return false;
            }
        }
        try {
            addr = InetAddress.getByName(nonPortPart);
        }
        catch (UnknownHostException e) {
            log.error((Object)sm.getString("remoteCidrValve.noRemoteIp"), (Throwable)e);
            return false;
        }
        for (NetMask nm : this.deny) {
            if (!(this.getAddConnectorPort() ? nm.matches(addr, port) : nm.matches(addr))) continue;
            return false;
        }
        for (NetMask nm : this.allow) {
            if (!(this.getAddConnectorPort() ? nm.matches(addr, port) : nm.matches(addr))) continue;
            return true;
        }
        return !this.deny.isEmpty() && this.allow.isEmpty();
    }

    @Override
    protected Log getLog() {
        return log;
    }

    private List<String> fillFromInput(String input, List<NetMask> target) {
        target.clear();
        if (input == null || input.isEmpty()) {
            return Collections.emptyList();
        }
        ArrayList<String> messages = new ArrayList<String>();
        for (String s : input.split("\\s*,\\s*")) {
            try {
                NetMask nm = new NetMask(s);
                target.add(nm);
            }
            catch (IllegalArgumentException e) {
                messages.add(s + ": " + e.getMessage());
            }
        }
        return Collections.unmodifiableList(messages);
    }
}

