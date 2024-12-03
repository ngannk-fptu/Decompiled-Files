/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina.tribes.membership;

import java.io.IOException;
import org.apache.catalina.tribes.membership.MemberImpl;
import org.apache.catalina.tribes.util.Arrays;

public class StaticMember
extends MemberImpl {
    public StaticMember() {
    }

    public StaticMember(String host, int port, long aliveTime) throws IOException {
        super(host, port, aliveTime);
    }

    public StaticMember(String host, int port, long aliveTime, byte[] payload) throws IOException {
        super(host, port, aliveTime, payload);
    }

    public void setHost(String host) {
        if (host == null) {
            return;
        }
        if (host.startsWith("{")) {
            this.setHost(Arrays.fromString(host));
        } else {
            try {
                this.setHostname(host);
            }
            catch (IOException x) {
                throw new RuntimeException(x);
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

    public void setUniqueId(String id) {
        byte[] uuid = Arrays.fromString(id);
        if (uuid == null || uuid.length != 16) {
            throw new RuntimeException(sm.getString("staticMember.invalid.uuidLength", id));
        }
        this.setUniqueId(uuid);
    }
}

