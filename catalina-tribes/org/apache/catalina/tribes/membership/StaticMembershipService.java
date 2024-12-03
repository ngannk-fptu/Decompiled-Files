/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 */
package org.apache.catalina.tribes.membership;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;
import javax.management.ObjectName;
import org.apache.catalina.tribes.Member;
import org.apache.catalina.tribes.MembershipProvider;
import org.apache.catalina.tribes.jmx.JmxRegistry;
import org.apache.catalina.tribes.membership.MembershipServiceBase;
import org.apache.catalina.tribes.membership.StaticMember;
import org.apache.catalina.tribes.membership.StaticMembershipProvider;
import org.apache.catalina.tribes.membership.StaticMembershipServiceMBean;
import org.apache.catalina.tribes.util.StringManager;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

public class StaticMembershipService
extends MembershipServiceBase
implements StaticMembershipServiceMBean {
    private static final Log log = LogFactory.getLog(StaticMembershipService.class);
    protected static final StringManager sm = StringManager.getManager("org.apache.catalina.tribes.membership");
    protected final ArrayList<StaticMember> staticMembers = new ArrayList();
    private StaticMember localMember;
    private StaticMembershipProvider provider;
    private ObjectName oname = null;

    public StaticMembershipService() {
        this.setDefaults(this.properties);
    }

    @Override
    public void start(int level) throws Exception {
        if (this.provider != null) {
            this.provider.start(level);
            return;
        }
        this.localMember.setServiceStartTime(System.currentTimeMillis());
        this.localMember.setMemberAliveTime(100L);
        if (this.provider == null) {
            this.provider = this.buildMembershipProvider();
        }
        this.provider.start(level);
        JmxRegistry jmxRegistry = JmxRegistry.getRegistry(this.channel);
        if (jmxRegistry != null) {
            this.oname = jmxRegistry.registerJmx(",component=Membership", this);
        }
    }

    protected StaticMembershipProvider buildMembershipProvider() throws Exception {
        StaticMembershipProvider provider = new StaticMembershipProvider();
        provider.setChannel(this.channel);
        provider.setMembershipListener(this);
        provider.setMembershipService(this);
        provider.setStaticMembers(this.staticMembers);
        this.properties.setProperty("membershipName", this.getMembershipName());
        provider.init(this.properties);
        return provider;
    }

    @Override
    public void stop(int level) {
        try {
            if (this.provider != null && this.provider.stop(level)) {
                if (this.oname != null) {
                    JmxRegistry.getRegistry(this.channel).unregisterJmx(this.oname);
                    this.oname = null;
                }
                this.provider = null;
                this.channel = null;
            }
        }
        catch (Exception e) {
            log.error((Object)sm.getString("staticMembershipService.stopFail", level), (Throwable)e);
        }
    }

    @Override
    public Member getLocalMember(boolean incAliveTime) {
        if (incAliveTime && this.localMember != null) {
            this.localMember.setMemberAliveTime(System.currentTimeMillis() - this.localMember.getServiceStartTime());
        }
        return this.localMember;
    }

    @Override
    public void setLocalMemberProperties(String listenHost, int listenPort, int securePort, int udpPort) {
        this.properties.setProperty("tcpListenHost", listenHost);
        this.properties.setProperty("tcpListenPort", String.valueOf(listenPort));
        try {
            this.findLocalMember();
            this.localMember.setHostname(listenHost);
            this.localMember.setPort(listenPort);
            this.localMember.setSecurePort(securePort);
            this.localMember.setUdpPort(udpPort);
            this.localMember.getData(true, true);
        }
        catch (IOException x) {
            throw new IllegalArgumentException(x);
        }
    }

    @Override
    public void setPayload(byte[] payload) {
    }

    @Override
    public void setDomain(byte[] domain) {
    }

    @Override
    public MembershipProvider getMembershipProvider() {
        return this.provider;
    }

    public ArrayList<StaticMember> getStaticMembers() {
        return this.staticMembers;
    }

    public void addStaticMember(StaticMember member) {
        this.staticMembers.add(member);
    }

    public void removeStaticMember(StaticMember member) {
        this.staticMembers.remove(member);
    }

    public void setLocalMember(StaticMember member) {
        this.localMember = member;
        this.localMember.setLocal(true);
    }

    @Override
    public long getExpirationTime() {
        String expirationTime = this.properties.getProperty("expirationTime");
        return Long.parseLong(expirationTime);
    }

    public void setExpirationTime(long expirationTime) {
        this.properties.setProperty("expirationTime", String.valueOf(expirationTime));
    }

    @Override
    public int getConnectTimeout() {
        String connectTimeout = this.properties.getProperty("connectTimeout");
        return Integer.parseInt(connectTimeout);
    }

    public void setConnectTimeout(int connectTimeout) {
        this.properties.setProperty("connectTimeout", String.valueOf(connectTimeout));
    }

    @Override
    public long getRpcTimeout() {
        String rpcTimeout = this.properties.getProperty("rpcTimeout");
        return Long.parseLong(rpcTimeout);
    }

    public void setRpcTimeout(long rpcTimeout) {
        this.properties.setProperty("rpcTimeout", String.valueOf(rpcTimeout));
    }

    @Override
    public boolean getUseThread() {
        String useThread = this.properties.getProperty("useThread");
        return Boolean.parseBoolean(useThread);
    }

    public void setUseThread(boolean useThread) {
        this.properties.setProperty("useThread", String.valueOf(useThread));
    }

    @Override
    public long getPingInterval() {
        String pingInterval = this.properties.getProperty("pingInterval");
        return Long.parseLong(pingInterval);
    }

    public void setPingInterval(long pingInterval) {
        this.properties.setProperty("pingInterval", String.valueOf(pingInterval));
    }

    @Override
    public void setProperties(Properties properties) {
        this.setDefaults(properties);
        this.properties = properties;
    }

    protected void setDefaults(Properties properties) {
        if (properties.getProperty("expirationTime") == null) {
            properties.setProperty("expirationTime", "5000");
        }
        if (properties.getProperty("connectTimeout") == null) {
            properties.setProperty("connectTimeout", "500");
        }
        if (properties.getProperty("rpcTimeout") == null) {
            properties.setProperty("rpcTimeout", "3000");
        }
        if (properties.getProperty("useThread") == null) {
            properties.setProperty("useThread", "false");
        }
        if (properties.getProperty("pingInterval") == null) {
            properties.setProperty("pingInterval", "1000");
        }
    }

    private String getMembershipName() {
        return this.channel.getName() + "-StaticMembership";
    }

    private void findLocalMember() throws IOException {
        if (this.localMember != null) {
            return;
        }
        String listenHost = this.properties.getProperty("tcpListenHost");
        String listenPort = this.properties.getProperty("tcpListenPort");
        for (StaticMember staticMember : this.staticMembers) {
            if (!Arrays.equals(InetAddress.getByName(listenHost).getAddress(), staticMember.getHost()) || Integer.parseInt(listenPort) != staticMember.getPort()) continue;
            this.localMember = staticMember;
            break;
        }
        if (this.localMember == null) {
            throw new IllegalStateException(sm.getString("staticMembershipService.noLocalMember"));
        }
        this.staticMembers.remove(this.localMember);
    }
}

