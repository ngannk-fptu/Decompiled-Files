/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.FilterChain
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 */
package org.apache.catalina.filters;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import org.apache.catalina.filters.FilterBase;
import org.apache.catalina.util.NetMask;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

public final class RemoteCIDRFilter
extends FilterBase {
    private static final String PLAIN_TEXT_MIME_TYPE = "text/plain";
    private final Log log = LogFactory.getLog(RemoteCIDRFilter.class);
    private final List<NetMask> allow = new ArrayList<NetMask>();
    private final List<NetMask> deny = new ArrayList<NetMask>();

    public String getAllow() {
        return this.allow.toString().replace("[", "").replace("]", "");
    }

    public void setAllow(String input) {
        List<String> messages = this.fillFromInput(input, this.allow);
        if (messages.isEmpty()) {
            return;
        }
        for (String message : messages) {
            this.log.error((Object)message);
        }
        throw new IllegalArgumentException(sm.getString("remoteCidrFilter.invalid", new Object[]{"allow"}));
    }

    public String getDeny() {
        return this.deny.toString().replace("[", "").replace("]", "");
    }

    public void setDeny(String input) {
        List<String> messages = this.fillFromInput(input, this.deny);
        if (messages.isEmpty()) {
            return;
        }
        for (String message : messages) {
            this.log.error((Object)message);
        }
        throw new IllegalArgumentException(sm.getString("remoteCidrFilter.invalid", new Object[]{"deny"}));
    }

    @Override
    protected boolean isConfigProblemFatal() {
        return true;
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (this.isAllowed(request.getRemoteAddr())) {
            chain.doFilter(request, response);
            return;
        }
        if (!(response instanceof HttpServletResponse)) {
            this.sendErrorWhenNotHttp(response);
            return;
        }
        ((HttpServletResponse)response).sendError(403);
    }

    @Override
    public Log getLogger() {
        return this.log;
    }

    private boolean isAllowed(String property) {
        InetAddress addr;
        try {
            addr = InetAddress.getByName(property);
        }
        catch (UnknownHostException e) {
            this.log.error((Object)sm.getString("remoteCidrFilter.noRemoteIp"), (Throwable)e);
            return false;
        }
        for (NetMask nm : this.deny) {
            if (!nm.matches(addr)) continue;
            return false;
        }
        for (NetMask nm : this.allow) {
            if (!nm.matches(addr)) continue;
            return true;
        }
        return !this.deny.isEmpty() && this.allow.isEmpty();
    }

    private void sendErrorWhenNotHttp(ServletResponse response) throws IOException {
        PrintWriter writer = response.getWriter();
        response.setContentType(PLAIN_TEXT_MIME_TYPE);
        writer.write(sm.getString("http.403"));
        writer.flush();
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

