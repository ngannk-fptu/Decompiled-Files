/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 */
package org.apache.catalina.tribes.membership.cloud;

import java.io.IOException;
import javax.management.ObjectName;
import org.apache.catalina.tribes.Member;
import org.apache.catalina.tribes.MembershipProvider;
import org.apache.catalina.tribes.jmx.JmxRegistry;
import org.apache.catalina.tribes.membership.MemberImpl;
import org.apache.catalina.tribes.membership.MembershipServiceBase;
import org.apache.catalina.tribes.membership.cloud.CloudMembershipServiceMBean;
import org.apache.catalina.tribes.util.StringManager;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

public class CloudMembershipService
extends MembershipServiceBase
implements CloudMembershipServiceMBean {
    private static final Log log = LogFactory.getLog(CloudMembershipService.class);
    protected static final StringManager sm = StringManager.getManager(CloudMembershipService.class);
    public static final String MEMBERSHIP_PROVIDER_CLASS_NAME = "membershipProviderClassName";
    private static final String KUBE = "kubernetes";
    private static final String DNS = "dns";
    private static final String KUBE_PROVIDER_CLASS = "org.apache.catalina.tribes.membership.cloud.KubernetesMembershipProvider";
    private static final String DNS_PROVIDER_CLASS = "org.apache.catalina.tribes.membership.cloud.DNSMembershipProvider";
    protected static final byte[] INITIAL_ID = new byte[16];
    private MembershipProvider membershipProvider;
    private MemberImpl localMember;
    private byte[] payload;
    private byte[] domain;
    private ObjectName oname = null;

    public Object getProperty(String name) {
        return this.properties.getProperty(name);
    }

    public boolean setProperty(String name, String value) {
        return this.properties.setProperty(name, value) == null;
    }

    public String getMembershipProviderClassName() {
        return this.properties.getProperty(MEMBERSHIP_PROVIDER_CLASS_NAME);
    }

    public void setMembershipProviderClassName(String membershipProviderClassName) {
        this.properties.setProperty(MEMBERSHIP_PROVIDER_CLASS_NAME, membershipProviderClassName);
    }

    @Override
    public void start(int level) throws Exception {
        if ((level & 4) == 0) {
            return;
        }
        this.createOrUpdateLocalMember();
        this.localMember.setServiceStartTime(System.currentTimeMillis());
        this.localMember.setMemberAliveTime(100L);
        this.localMember.setPayload(this.payload);
        this.localMember.setDomain(this.domain);
        if (this.membershipProvider == null) {
            String provider = this.getMembershipProviderClassName();
            if (provider == null || KUBE.equals(provider)) {
                provider = KUBE_PROVIDER_CLASS;
            } else if (DNS.equals(provider)) {
                provider = DNS_PROVIDER_CLASS;
            }
            if (log.isDebugEnabled()) {
                log.debug((Object)("Using membershipProvider: " + provider));
            }
            this.membershipProvider = (MembershipProvider)Class.forName(provider).getConstructor(new Class[0]).newInstance(new Object[0]);
            this.membershipProvider.setMembershipListener(this);
            this.membershipProvider.setMembershipService(this);
            this.membershipProvider.init(this.properties);
        }
        this.membershipProvider.start(level);
        JmxRegistry jmxRegistry = JmxRegistry.getRegistry(this.channel);
        if (jmxRegistry != null) {
            this.oname = jmxRegistry.registerJmx(",component=Membership", this);
        }
    }

    @Override
    public void stop(int level) {
        try {
            if (this.membershipProvider != null && this.membershipProvider.stop(level)) {
                if (this.oname != null) {
                    JmxRegistry.getRegistry(this.channel).unregisterJmx(this.oname);
                    this.oname = null;
                }
                this.membershipProvider = null;
                this.channel = null;
            }
        }
        catch (Exception e) {
            log.error((Object)sm.getString("cloudMembershipService.stopFail", level), (Throwable)e);
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
        if (log.isDebugEnabled()) {
            log.debug((Object)String.format("setLocalMemberProperties(%s, %d, %d, %d)", listenHost, listenPort, securePort, udpPort));
        }
        this.properties.setProperty("tcpListenHost", listenHost);
        this.properties.setProperty("tcpListenPort", String.valueOf(listenPort));
        this.properties.setProperty("udpListenPort", String.valueOf(udpPort));
        this.properties.setProperty("tcpSecurePort", String.valueOf(securePort));
        try {
            this.createOrUpdateLocalMember();
            this.localMember.setPayload(this.payload);
            this.localMember.setDomain(this.domain);
            this.localMember.getData(true, true);
        }
        catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private void createOrUpdateLocalMember() throws IOException {
        String host = this.properties.getProperty("tcpListenHost");
        int port = Integer.parseInt(this.properties.getProperty("tcpListenPort"));
        int securePort = Integer.parseInt(this.properties.getProperty("tcpSecurePort"));
        int udpPort = Integer.parseInt(this.properties.getProperty("udpListenPort"));
        if (this.localMember == null) {
            this.localMember = new MemberImpl();
            this.localMember.setUniqueId(INITIAL_ID);
            this.localMember.setLocal(true);
        }
        this.localMember.setHostname(host);
        this.localMember.setPort(port);
        this.localMember.setSecurePort(securePort);
        this.localMember.setUdpPort(udpPort);
        this.localMember.getData(true, true);
    }

    @Override
    public void setPayload(byte[] payload) {
        this.payload = payload;
        if (this.localMember != null) {
            this.localMember.setPayload(payload);
        }
    }

    @Override
    public void setDomain(byte[] domain) {
        this.domain = domain;
        if (this.localMember != null) {
            this.localMember.setDomain(domain);
        }
    }

    @Override
    public MembershipProvider getMembershipProvider() {
        return this.membershipProvider;
    }

    public void setMembershipProvider(MembershipProvider memberProvider) {
        this.membershipProvider = memberProvider;
    }

    @Override
    public int getConnectTimeout() {
        return Integer.parseInt(this.properties.getProperty("connectTimeout", "1000"));
    }

    public void setConnectTimeout(int connectTimeout) {
        this.properties.setProperty("connectTimeout", String.valueOf(connectTimeout));
    }

    @Override
    public int getReadTimeout() {
        return Integer.parseInt(this.properties.getProperty("readTimeout", "1000"));
    }

    public void setReadTimeout(int readTimeout) {
        this.properties.setProperty("readTimeout", String.valueOf(readTimeout));
    }

    @Override
    public long getExpirationTime() {
        return Long.parseLong(this.properties.getProperty("expirationTime", "5000"));
    }

    public void setExpirationTime(long expirationTime) {
        this.properties.setProperty("expirationTime", String.valueOf(expirationTime));
    }
}

