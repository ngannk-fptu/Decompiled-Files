/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

public class HostInfo
extends Task {
    private static final String DEF_REM_ADDR6 = "::";
    private static final String DEF_REM_ADDR4 = "0.0.0.0";
    private static final String DEF_LOCAL_ADDR6 = "::1";
    private static final String DEF_LOCAL_ADDR4 = "127.0.0.1";
    private static final String DEF_LOCAL_NAME = "localhost";
    private static final String DEF_DOMAIN = "localdomain";
    private static final String DOMAIN = "DOMAIN";
    private static final String NAME = "NAME";
    private static final String ADDR4 = "ADDR4";
    private static final String ADDR6 = "ADDR6";
    private String prefix = "";
    private String host;
    private InetAddress nameAddr;
    private InetAddress best6;
    private InetAddress best4;
    private List<InetAddress> inetAddrs;

    public void setPrefix(String aPrefix) {
        this.prefix = aPrefix;
        if (!this.prefix.endsWith(".")) {
            this.prefix = this.prefix + ".";
        }
    }

    public void setHost(String aHost) {
        this.host = aHost;
    }

    @Override
    public void execute() throws BuildException {
        if (this.host == null || this.host.isEmpty()) {
            this.executeLocal();
        } else {
            this.executeRemote();
        }
    }

    private void executeLocal() {
        try {
            this.inetAddrs = new LinkedList<InetAddress>();
            Collections.list(NetworkInterface.getNetworkInterfaces()).forEach(netInterface -> this.inetAddrs.addAll(Collections.list(netInterface.getInetAddresses())));
            this.selectAddresses();
            if (this.nameAddr != null && this.hasHostName(this.nameAddr)) {
                this.setDomainAndName(this.nameAddr.getCanonicalHostName());
            } else {
                this.setProperty(DOMAIN, DEF_DOMAIN);
                this.setProperty(NAME, DEF_LOCAL_NAME);
            }
            if (this.best4 != null) {
                this.setProperty(ADDR4, this.best4.getHostAddress());
            } else {
                this.setProperty(ADDR4, DEF_LOCAL_ADDR4);
            }
            if (this.best6 != null) {
                this.setProperty(ADDR6, this.best6.getHostAddress());
            } else {
                this.setProperty(ADDR6, DEF_LOCAL_ADDR6);
            }
        }
        catch (Exception e) {
            this.log("Error retrieving local host information", e, 1);
            this.setProperty(DOMAIN, DEF_DOMAIN);
            this.setProperty(NAME, DEF_LOCAL_NAME);
            this.setProperty(ADDR4, DEF_LOCAL_ADDR4);
            this.setProperty(ADDR6, DEF_LOCAL_ADDR6);
        }
    }

    private boolean hasHostName(InetAddress addr) {
        return !addr.getHostAddress().equals(addr.getCanonicalHostName());
    }

    private void selectAddresses() {
        for (InetAddress current : this.inetAddrs) {
            if (current.isMulticastAddress()) continue;
            if (current instanceof Inet4Address) {
                this.best4 = this.selectBestAddress(this.best4, current);
                continue;
            }
            if (!(current instanceof Inet6Address)) continue;
            this.best6 = this.selectBestAddress(this.best6, current);
        }
        this.nameAddr = this.selectBestAddress(this.best4, this.best6);
    }

    private InetAddress selectBestAddress(InetAddress bestSoFar, InetAddress current) {
        InetAddress best = bestSoFar;
        if (best == null) {
            best = current;
        } else if (current != null && !current.isLoopbackAddress()) {
            if (current.isLinkLocalAddress()) {
                if (best.isLoopbackAddress()) {
                    best = current;
                }
            } else if (current.isSiteLocalAddress()) {
                if (best.isLoopbackAddress() || best.isLinkLocalAddress() || best.isSiteLocalAddress() && !this.hasHostName(best)) {
                    best = current;
                }
            } else if (best.isLoopbackAddress() || best.isLinkLocalAddress() || best.isSiteLocalAddress() || !this.hasHostName(best)) {
                best = current;
            }
        }
        return best;
    }

    private void executeRemote() {
        try {
            this.inetAddrs = Arrays.asList(InetAddress.getAllByName(this.host));
            this.selectAddresses();
            if (this.nameAddr != null && this.hasHostName(this.nameAddr)) {
                this.setDomainAndName(this.nameAddr.getCanonicalHostName());
            } else {
                this.setDomainAndName(this.host);
            }
            if (this.best4 != null) {
                this.setProperty(ADDR4, this.best4.getHostAddress());
            } else {
                this.setProperty(ADDR4, DEF_REM_ADDR4);
            }
            if (this.best6 != null) {
                this.setProperty(ADDR6, this.best6.getHostAddress());
            } else {
                this.setProperty(ADDR6, DEF_REM_ADDR6);
            }
        }
        catch (Exception e) {
            this.log("Error retrieving remote host information for host:" + this.host + ".", e, 1);
            this.setDomainAndName(this.host);
            this.setProperty(ADDR4, DEF_REM_ADDR4);
            this.setProperty(ADDR6, DEF_REM_ADDR6);
        }
    }

    private void setDomainAndName(String fqdn) {
        int idx = fqdn.indexOf(46);
        if (idx > 0) {
            this.setProperty(NAME, fqdn.substring(0, idx));
            this.setProperty(DOMAIN, fqdn.substring(idx + 1));
        } else {
            this.setProperty(NAME, fqdn);
            this.setProperty(DOMAIN, DEF_DOMAIN);
        }
    }

    private void setProperty(String name, String value) {
        this.getProject().setNewProperty(this.prefix + name, value);
    }
}

