/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 */
package org.apache.catalina.tribes.membership.cloud;

import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.security.AccessController;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.apache.catalina.tribes.ChannelListener;
import org.apache.catalina.tribes.Heartbeat;
import org.apache.catalina.tribes.Member;
import org.apache.catalina.tribes.membership.Membership;
import org.apache.catalina.tribes.membership.MembershipProviderBase;
import org.apache.catalina.tribes.membership.cloud.CloudMembershipService;
import org.apache.catalina.tribes.membership.cloud.StreamProvider;
import org.apache.catalina.tribes.util.StringManager;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

public abstract class CloudMembershipProvider
extends MembershipProviderBase
implements Heartbeat,
ChannelListener {
    private static final Log log = LogFactory.getLog(CloudMembershipProvider.class);
    protected static final StringManager sm = StringManager.getManager(CloudMembershipProvider.class);
    protected static final String CUSTOM_ENV_PREFIX = "OPENSHIFT_KUBE_PING_";
    protected String url;
    protected StreamProvider streamProvider;
    protected int connectionTimeout;
    protected int readTimeout;
    protected Instant startTime;
    protected MessageDigest md5;
    protected Map<String, String> headers = new HashMap<String, String>();
    protected String localIp;
    protected int port;
    protected long expirationTime = 5000L;

    public CloudMembershipProvider() {
        try {
            this.md5 = MessageDigest.getInstance("md5");
        }
        catch (NoSuchAlgorithmException noSuchAlgorithmException) {
            // empty catch block
        }
    }

    protected static String getEnv(String ... keys) {
        String key;
        String val = null;
        String[] stringArray = keys;
        int n = stringArray.length;
        for (int i = 0; i < n && (val = AccessController.doPrivileged(() -> CloudMembershipProvider.lambda$getEnv$0(key = stringArray[i]))) == null; ++i) {
        }
        return val;
    }

    protected String getNamespace() {
        String namespace = CloudMembershipProvider.getEnv("OPENSHIFT_KUBE_PING_NAMESPACE", "KUBERNETES_NAMESPACE");
        if (namespace == null || namespace.length() == 0) {
            log.warn((Object)sm.getString("kubernetesMembershipProvider.noNamespace"));
            namespace = "tomcat";
        }
        return namespace;
    }

    @Override
    public void init(Properties properties) throws IOException {
        this.startTime = Instant.now();
        CloudMembershipService service = (CloudMembershipService)this.service;
        this.connectionTimeout = service.getConnectTimeout();
        this.readTimeout = service.getReadTimeout();
        this.expirationTime = service.getExpirationTime();
        this.localIp = InetAddress.getLocalHost().getHostAddress();
        this.port = Integer.parseInt(properties.getProperty("tcpListenPort"));
    }

    @Override
    public void start(int level) throws Exception {
        if (this.membership == null) {
            this.membership = new Membership(this.service.getLocalMember(true));
        }
        this.service.getChannel().addChannelListener(this);
    }

    @Override
    public boolean stop(int level) throws Exception {
        return true;
    }

    @Override
    public void heartbeat() {
        Member[] expired;
        Member[] announcedMembers;
        for (Member member : announcedMembers = this.fetchMembers()) {
            this.updateMember(member, true);
        }
        for (Member member : expired = this.membership.expire(this.expirationTime)) {
            this.updateMember(member, false);
        }
    }

    protected abstract Member[] fetchMembers();

    protected void updateMember(Member member, boolean add) {
        if (add && !this.membership.memberAlive(member)) {
            return;
        }
        if (log.isDebugEnabled()) {
            String message = add ? "Member added: " + member : "Member disappeared: " + member;
            log.debug((Object)message);
        }
        Runnable r = () -> {
            Thread currentThread = Thread.currentThread();
            String name = currentThread.getName();
            try {
                String threadName = add ? "CloudMembership-memberAdded" : "CloudMembership-memberDisappeared";
                currentThread.setName(threadName);
                if (add) {
                    this.membershipListener.memberAdded(member);
                } else {
                    this.membershipListener.memberDisappeared(member);
                }
            }
            finally {
                currentThread.setName(name);
            }
        };
        this.executor.execute(r);
    }

    @Override
    public void messageReceived(Serializable msg, Member sender) {
    }

    @Override
    public boolean accept(Serializable msg, Member sender) {
        return false;
    }

    private static /* synthetic */ String lambda$getEnv$0(String key) {
        return System.getenv(key);
    }
}

