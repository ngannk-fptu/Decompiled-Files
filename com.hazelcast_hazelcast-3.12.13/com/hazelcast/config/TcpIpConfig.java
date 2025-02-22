/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.util.Preconditions;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class TcpIpConfig {
    private static final int CONNECTION_TIMEOUT_SEC = 5;
    private int connectionTimeoutSeconds = 5;
    private boolean enabled;
    private List<String> members = new ArrayList<String>();
    private String requiredMember;

    public int getConnectionTimeoutSeconds() {
        return this.connectionTimeoutSeconds;
    }

    public TcpIpConfig setConnectionTimeoutSeconds(int connectionTimeoutSeconds) {
        if (connectionTimeoutSeconds < 0) {
            throw new IllegalArgumentException("connection timeout can't be smaller than 0");
        }
        this.connectionTimeoutSeconds = connectionTimeoutSeconds;
        return this;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public TcpIpConfig setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public List<String> getMembers() {
        if (this.members == null) {
            this.members = new ArrayList<String>();
        }
        return this.members;
    }

    public TcpIpConfig setMembers(List<String> members) {
        Preconditions.isNotNull(members, "members");
        this.members.clear();
        for (String member : members) {
            this.addMember(member);
        }
        return this;
    }

    public TcpIpConfig addMember(String member) {
        String memberText = Preconditions.checkHasText(member, "member must contain text");
        StringTokenizer tokenizer = new StringTokenizer(memberText, ",");
        while (tokenizer.hasMoreTokens()) {
            String s = tokenizer.nextToken();
            this.members.add(s.trim());
        }
        return this;
    }

    public TcpIpConfig clear() {
        this.members.clear();
        return this;
    }

    public String getRequiredMember() {
        return this.requiredMember;
    }

    public TcpIpConfig setRequiredMember(String requiredMember) {
        this.requiredMember = requiredMember;
        return this;
    }

    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || !(o instanceof TcpIpConfig)) {
            return false;
        }
        TcpIpConfig that = (TcpIpConfig)o;
        if (this.connectionTimeoutSeconds != that.connectionTimeoutSeconds) {
            return false;
        }
        if (this.enabled != that.enabled) {
            return false;
        }
        if (this.members != null ? !this.members.equals(that.members) : that.members != null) {
            return false;
        }
        return this.requiredMember != null ? this.requiredMember.equals(that.requiredMember) : that.requiredMember == null;
    }

    public final int hashCode() {
        int result = this.connectionTimeoutSeconds;
        result = 31 * result + (this.enabled ? 1 : 0);
        result = 31 * result + (this.members != null ? this.members.hashCode() : 0);
        result = 31 * result + (this.requiredMember != null ? this.requiredMember.hashCode() : 0);
        return result;
    }

    public String toString() {
        return "TcpIpConfig [enabled=" + this.enabled + ", connectionTimeoutSeconds=" + this.connectionTimeoutSeconds + ", members=" + this.members + ", requiredMember=" + this.requiredMember + "]";
    }
}

