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
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import org.apache.catalina.tribes.Member;
import org.apache.catalina.tribes.membership.MemberImpl;
import org.apache.catalina.tribes.membership.cloud.CloudMembershipProvider;
import org.apache.catalina.tribes.membership.cloud.CloudMembershipService;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

public class DNSMembershipProvider
extends CloudMembershipProvider {
    private static final Log log = LogFactory.getLog(DNSMembershipProvider.class);
    private String dnsServiceName;

    @Override
    public void start(int level) throws Exception {
        if ((level & 4) == 0) {
            return;
        }
        super.start(level);
        this.dnsServiceName = DNSMembershipProvider.getEnv("DNS_MEMBERSHIP_SERVICE_NAME");
        if (this.dnsServiceName == null) {
            this.dnsServiceName = this.getNamespace();
        }
        if (log.isDebugEnabled()) {
            log.debug((Object)String.format("Namespace [%s] set; clustering enabled", this.dnsServiceName));
        }
        this.dnsServiceName = URLEncoder.encode(this.dnsServiceName, "UTF-8");
        this.heartbeat();
    }

    @Override
    public boolean stop(int level) throws Exception {
        return super.stop(level);
    }

    @Override
    protected Member[] fetchMembers() {
        ArrayList<MemberImpl> members = new ArrayList<MemberImpl>();
        InetAddress[] inetAddresses = null;
        try {
            inetAddresses = InetAddress.getAllByName(this.dnsServiceName);
        }
        catch (UnknownHostException exception) {
            log.warn((Object)sm.getString("dnsMembershipProvider.dnsError", this.dnsServiceName), (Throwable)exception);
        }
        if (inetAddresses != null) {
            for (InetAddress inetAddress : inetAddresses) {
                String ip = inetAddress.getHostAddress();
                byte[] id = this.md5.digest(ip.getBytes());
                if (ip.equals(this.localIp)) {
                    Member localMember = this.service.getLocalMember(false);
                    if (localMember.getUniqueId() != CloudMembershipService.INITIAL_ID || !(localMember instanceof MemberImpl)) continue;
                    ((MemberImpl)localMember).setUniqueId(id);
                    continue;
                }
                long aliveTime = -1L;
                MemberImpl member = null;
                try {
                    member = new MemberImpl(ip, this.port, aliveTime);
                }
                catch (IOException e) {
                    log.error((Object)sm.getString("kubernetesMembershipProvider.memberError"), (Throwable)e);
                    continue;
                }
                member.setUniqueId(id);
                members.add(member);
            }
        }
        return members.toArray(new Member[0]);
    }

    @Override
    public boolean accept(Serializable msg, Member sender) {
        boolean found = false;
        Member[] members = this.membership.getMembers();
        if (members != null) {
            for (Member member : members) {
                if (!Arrays.equals(sender.getHost(), member.getHost()) || sender.getPort() != member.getPort()) continue;
                found = true;
                break;
            }
        }
        if (!found) {
            MemberImpl member = new MemberImpl();
            member.setHost(sender.getHost());
            member.setPort(sender.getPort());
            byte[] host = sender.getHost();
            int i = 0;
            StringBuilder buf = new StringBuilder();
            buf.append(host[i++] & 0xFF);
            while (i < host.length) {
                buf.append('.').append(host[i] & 0xFF);
                ++i;
            }
            byte[] id = this.md5.digest(buf.toString().getBytes());
            member.setUniqueId(id);
            member.setMemberAliveTime(-1L);
            this.updateMember(member, true);
        }
        return false;
    }
}

