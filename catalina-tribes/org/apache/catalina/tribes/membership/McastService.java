/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 */
package org.apache.catalina.tribes.membership;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.Properties;
import javax.management.ObjectName;
import org.apache.catalina.tribes.ChannelException;
import org.apache.catalina.tribes.ChannelMessage;
import org.apache.catalina.tribes.Member;
import org.apache.catalina.tribes.MembershipProvider;
import org.apache.catalina.tribes.MessageListener;
import org.apache.catalina.tribes.io.ChannelData;
import org.apache.catalina.tribes.io.XByteBuffer;
import org.apache.catalina.tribes.jmx.JmxRegistry;
import org.apache.catalina.tribes.membership.McastServiceImpl;
import org.apache.catalina.tribes.membership.McastServiceMBean;
import org.apache.catalina.tribes.membership.MemberImpl;
import org.apache.catalina.tribes.membership.MembershipServiceBase;
import org.apache.catalina.tribes.util.Arrays;
import org.apache.catalina.tribes.util.StringManager;
import org.apache.catalina.tribes.util.UUIDGenerator;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

public class McastService
extends MembershipServiceBase
implements MessageListener,
McastServiceMBean {
    private static final Log log = LogFactory.getLog(McastService.class);
    protected static final StringManager sm = StringManager.getManager("org.apache.catalina.tribes.membership");
    protected McastServiceImpl impl;
    protected MessageListener msglistener;
    protected MemberImpl localMember;
    private int mcastSoTimeout;
    private int mcastTTL;
    protected byte[] payload;
    protected byte[] domain;
    private ObjectName oname = null;

    public McastService() {
        this.setDefaults(this.properties);
    }

    @Override
    public void setProperties(Properties properties) {
        this.hasProperty(properties, "mcastPort");
        this.hasProperty(properties, "mcastAddress");
        this.hasProperty(properties, "memberDropTime");
        this.hasProperty(properties, "mcastFrequency");
        this.hasProperty(properties, "tcpListenPort");
        this.hasProperty(properties, "tcpListenHost");
        this.setDefaults(properties);
        this.properties = properties;
    }

    @Override
    public String getLocalMemberName() {
        return this.localMember.toString();
    }

    @Override
    public Member getLocalMember(boolean alive) {
        if (alive && this.localMember != null && this.impl != null) {
            this.localMember.setMemberAliveTime(System.currentTimeMillis() - this.impl.getServiceStartTime());
        }
        return this.localMember;
    }

    @Override
    public void setLocalMemberProperties(String listenHost, int listenPort, int securePort, int udpPort) {
        this.properties.setProperty("tcpListenHost", listenHost);
        this.properties.setProperty("tcpListenPort", String.valueOf(listenPort));
        this.properties.setProperty("udpListenPort", String.valueOf(udpPort));
        this.properties.setProperty("tcpSecurePort", String.valueOf(securePort));
        try {
            if (this.localMember != null) {
                this.localMember.setHostname(listenHost);
                this.localMember.setPort(listenPort);
            } else {
                this.localMember = new MemberImpl(listenHost, listenPort, 0L);
                this.localMember.setUniqueId(UUIDGenerator.randomUUID(true));
                this.localMember.setPayload(this.getPayload());
                this.localMember.setDomain(this.getDomain());
                this.localMember.setLocal(true);
            }
            this.localMember.setSecurePort(securePort);
            this.localMember.setUdpPort(udpPort);
            this.localMember.getData(true, true);
        }
        catch (IOException x) {
            throw new IllegalArgumentException(x);
        }
    }

    public void setAddress(String addr) {
        this.properties.setProperty("mcastAddress", addr);
    }

    @Override
    public String getAddress() {
        return this.properties.getProperty("mcastAddress");
    }

    public void setMcastBindAddress(String bindaddr) {
        this.setBind(bindaddr);
    }

    public void setBind(String bindaddr) {
        this.properties.setProperty("mcastBindAddress", bindaddr);
    }

    @Override
    public String getBind() {
        return this.properties.getProperty("mcastBindAddress");
    }

    public void setPort(int port) {
        this.properties.setProperty("mcastPort", String.valueOf(port));
    }

    public void setRecoveryCounter(int recoveryCounter) {
        this.properties.setProperty("recoveryCounter", String.valueOf(recoveryCounter));
    }

    @Override
    public int getRecoveryCounter() {
        String p = this.properties.getProperty("recoveryCounter");
        if (p != null) {
            return Integer.parseInt(p);
        }
        return -1;
    }

    public void setRecoveryEnabled(boolean recoveryEnabled) {
        this.properties.setProperty("recoveryEnabled", String.valueOf(recoveryEnabled));
    }

    @Override
    public boolean getRecoveryEnabled() {
        String p = this.properties.getProperty("recoveryEnabled");
        if (p != null) {
            return Boolean.parseBoolean(p);
        }
        return false;
    }

    public void setRecoverySleepTime(long recoverySleepTime) {
        this.properties.setProperty("recoverySleepTime", String.valueOf(recoverySleepTime));
    }

    @Override
    public long getRecoverySleepTime() {
        String p = this.properties.getProperty("recoverySleepTime");
        if (p != null) {
            return Long.parseLong(p);
        }
        return -1L;
    }

    public void setLocalLoopbackDisabled(boolean localLoopbackDisabled) {
        this.properties.setProperty("localLoopbackDisabled", String.valueOf(localLoopbackDisabled));
    }

    @Override
    public boolean getLocalLoopbackDisabled() {
        String p = this.properties.getProperty("localLoopbackDisabled");
        if (p != null) {
            return Boolean.parseBoolean(p);
        }
        return false;
    }

    @Override
    public int getPort() {
        String p = this.properties.getProperty("mcastPort");
        return Integer.parseInt(p);
    }

    public void setFrequency(long time) {
        this.properties.setProperty("mcastFrequency", String.valueOf(time));
    }

    @Override
    public long getFrequency() {
        String p = this.properties.getProperty("mcastFrequency");
        return Long.parseLong(p);
    }

    public void setMcastDropTime(long time) {
        this.setDropTime(time);
    }

    public void setDropTime(long time) {
        this.properties.setProperty("memberDropTime", String.valueOf(time));
    }

    @Override
    public long getDropTime() {
        String p = this.properties.getProperty("memberDropTime");
        return Long.parseLong(p);
    }

    protected void hasProperty(Properties properties, String name) {
        if (properties.getProperty(name) == null) {
            throw new IllegalArgumentException(sm.getString("mcastService.missing.property", name));
        }
    }

    @Override
    public void start(int level) throws Exception {
        this.hasProperty(this.properties, "mcastPort");
        this.hasProperty(this.properties, "mcastAddress");
        this.hasProperty(this.properties, "memberDropTime");
        this.hasProperty(this.properties, "mcastFrequency");
        this.hasProperty(this.properties, "tcpListenPort");
        this.hasProperty(this.properties, "tcpListenHost");
        this.hasProperty(this.properties, "tcpSecurePort");
        this.hasProperty(this.properties, "udpListenPort");
        if (this.impl != null) {
            this.impl.start(level);
            return;
        }
        String host = this.getProperties().getProperty("tcpListenHost");
        int port = Integer.parseInt(this.getProperties().getProperty("tcpListenPort"));
        int securePort = Integer.parseInt(this.getProperties().getProperty("tcpSecurePort"));
        int udpPort = Integer.parseInt(this.getProperties().getProperty("udpListenPort"));
        if (this.localMember == null) {
            this.localMember = new MemberImpl(host, port, 100L);
            this.localMember.setUniqueId(UUIDGenerator.randomUUID(true));
            this.localMember.setLocal(true);
        } else {
            this.localMember.setHostname(host);
            this.localMember.setPort(port);
            this.localMember.setMemberAliveTime(100L);
        }
        this.localMember.setSecurePort(securePort);
        this.localMember.setUdpPort(udpPort);
        if (this.payload != null) {
            this.localMember.setPayload(this.payload);
        }
        if (this.domain != null) {
            this.localMember.setDomain(this.domain);
        }
        this.localMember.setServiceStartTime(System.currentTimeMillis());
        InetAddress bind = null;
        if (this.properties.getProperty("mcastBindAddress") != null) {
            bind = InetAddress.getByName(this.properties.getProperty("mcastBindAddress"));
        }
        int ttl = -1;
        int soTimeout = -1;
        if (this.properties.getProperty("mcastTTL") != null) {
            try {
                ttl = Integer.parseInt(this.properties.getProperty("mcastTTL"));
            }
            catch (Exception x) {
                log.error((Object)sm.getString("McastService.parseTTL", this.properties.getProperty("mcastTTL")), (Throwable)x);
            }
        }
        if (this.properties.getProperty("mcastSoTimeout") != null) {
            try {
                soTimeout = Integer.parseInt(this.properties.getProperty("mcastSoTimeout"));
            }
            catch (Exception x) {
                log.error((Object)sm.getString("McastService.parseSoTimeout", this.properties.getProperty("mcastSoTimeout")), (Throwable)x);
            }
        }
        this.impl = new McastServiceImpl(this.localMember, Long.parseLong(this.properties.getProperty("mcastFrequency")), Long.parseLong(this.properties.getProperty("memberDropTime")), Integer.parseInt(this.properties.getProperty("mcastPort")), bind, InetAddress.getByName(this.properties.getProperty("mcastAddress")), ttl, soTimeout, this, this, Boolean.parseBoolean(this.properties.getProperty("localLoopbackDisabled")));
        this.impl.setMembershipService(this);
        String value = this.properties.getProperty("recoveryEnabled");
        boolean recEnabled = Boolean.parseBoolean(value);
        this.impl.setRecoveryEnabled(recEnabled);
        int recCnt = Integer.parseInt(this.properties.getProperty("recoveryCounter"));
        this.impl.setRecoveryCounter(recCnt);
        long recSlpTime = Long.parseLong(this.properties.getProperty("recoverySleepTime"));
        this.impl.setRecoverySleepTime(recSlpTime);
        this.impl.setChannel(this.channel);
        this.impl.start(level);
        JmxRegistry jmxRegistry = JmxRegistry.getRegistry(this.channel);
        if (jmxRegistry != null) {
            this.oname = jmxRegistry.registerJmx(",component=Membership", this);
        }
    }

    @Override
    public void stop(int svc) {
        try {
            if (this.impl != null && this.impl.stop(svc)) {
                if (this.oname != null) {
                    JmxRegistry.getRegistry(this.channel).unregisterJmx(this.oname);
                    this.oname = null;
                }
                this.impl.setChannel(null);
                this.impl = null;
                this.channel = null;
            }
        }
        catch (Exception x) {
            log.error((Object)sm.getString("McastService.stopFail", svc), (Throwable)x);
        }
    }

    public void setMessageListener(MessageListener listener) {
        this.msglistener = listener;
    }

    public void removeMessageListener() {
        this.msglistener = null;
    }

    @Override
    public void messageReceived(ChannelMessage msg) {
        if (this.msglistener != null && this.msglistener.accept(msg)) {
            this.msglistener.messageReceived(msg);
        }
    }

    @Override
    public boolean accept(ChannelMessage msg) {
        return true;
    }

    @Override
    public void broadcast(ChannelMessage message) throws ChannelException {
        if (this.impl == null || (this.impl.startLevel & 8) != 8) {
            throw new ChannelException(sm.getString("mcastService.noStart"));
        }
        byte[] data = XByteBuffer.createDataPackage((ChannelData)message);
        if (data.length > 65535) {
            throw new ChannelException(sm.getString("mcastService.exceed.maxPacketSize", Integer.toString(data.length), Integer.toString(65535)));
        }
        DatagramPacket packet = new DatagramPacket(data, 0, data.length);
        try {
            this.impl.send(false, packet);
        }
        catch (Exception x) {
            throw new ChannelException(x);
        }
    }

    @Override
    public int getSoTimeout() {
        return this.mcastSoTimeout;
    }

    public void setSoTimeout(int mcastSoTimeout) {
        this.mcastSoTimeout = mcastSoTimeout;
        this.properties.setProperty("mcastSoTimeout", String.valueOf(mcastSoTimeout));
    }

    @Override
    public int getTtl() {
        return this.mcastTTL;
    }

    public byte[] getPayload() {
        return this.payload;
    }

    @Override
    public byte[] getDomain() {
        return this.domain;
    }

    public void setTtl(int mcastTTL) {
        this.mcastTTL = mcastTTL;
        this.properties.setProperty("mcastTTL", String.valueOf(mcastTTL));
    }

    @Override
    public void setPayload(byte[] payload) {
        this.payload = payload;
        if (this.localMember != null) {
            this.localMember.setPayload(payload);
            try {
                if (this.impl != null) {
                    this.impl.send(false);
                }
            }
            catch (Exception x) {
                log.error((Object)sm.getString("McastService.payload"), (Throwable)x);
            }
        }
    }

    @Override
    public void setDomain(byte[] domain) {
        this.domain = domain;
        if (this.localMember != null) {
            this.localMember.setDomain(domain);
            try {
                if (this.impl != null) {
                    this.impl.send(false);
                }
            }
            catch (Exception x) {
                log.error((Object)sm.getString("McastService.domain"), (Throwable)x);
            }
        }
    }

    public void setDomain(String domain) {
        if (domain == null) {
            return;
        }
        if (domain.startsWith("{")) {
            this.setDomain(Arrays.fromString(domain));
        } else {
            this.setDomain(Arrays.convert(domain));
        }
    }

    @Override
    public MembershipProvider getMembershipProvider() {
        return this.impl;
    }

    protected void setDefaults(Properties properties) {
        if (properties.getProperty("mcastPort") == null) {
            properties.setProperty("mcastPort", "45564");
        }
        if (properties.getProperty("mcastAddress") == null) {
            properties.setProperty("mcastAddress", "228.0.0.4");
        }
        if (properties.getProperty("memberDropTime") == null) {
            properties.setProperty("memberDropTime", "3000");
        }
        if (properties.getProperty("mcastFrequency") == null) {
            properties.setProperty("mcastFrequency", "500");
        }
        if (properties.getProperty("recoveryCounter") == null) {
            properties.setProperty("recoveryCounter", "10");
        }
        if (properties.getProperty("recoveryEnabled") == null) {
            properties.setProperty("recoveryEnabled", "true");
        }
        if (properties.getProperty("recoverySleepTime") == null) {
            properties.setProperty("recoverySleepTime", "5000");
        }
        if (properties.getProperty("localLoopbackDisabled") == null) {
            properties.setProperty("localLoopbackDisabled", "false");
        }
    }

    public static void main(String[] args) throws Exception {
        McastService service = new McastService();
        Properties p = new Properties();
        p.setProperty("mcastPort", "5555");
        p.setProperty("mcastAddress", "224.10.10.10");
        p.setProperty("mcastClusterDomain", "catalina");
        p.setProperty("bindAddress", "localhost");
        p.setProperty("memberDropTime", "3000");
        p.setProperty("mcastFrequency", "500");
        p.setProperty("tcpListenPort", "4000");
        p.setProperty("tcpListenHost", "127.0.0.1");
        p.setProperty("tcpSecurePort", "4100");
        p.setProperty("udpListenPort", "4200");
        service.setProperties(p);
        service.start();
        Thread.sleep(3600000L);
    }
}

