/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc.dns;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DNSRecordSRV
implements Comparable<DNSRecordSRV> {
    private static final Pattern PATTERN = Pattern.compile("^([0-9]+) ([0-9]+) ([0-9]+) (.+)$");
    private final int priority;
    private final int weight;
    private final int port;
    private final String serverName;

    public static DNSRecordSRV parseFromDNSRecord(String record) {
        Matcher m = PATTERN.matcher(record);
        if (!m.matches()) {
            throw new IllegalArgumentException("record '" + record + "' cannot be matched as a valid DNS SRV Record");
        }
        try {
            int priority = Integer.parseInt(m.group(1));
            int weight = Integer.parseInt(m.group(2));
            int port = Integer.parseInt(m.group(3));
            String serverName = m.group(4);
            if (serverName.endsWith(".")) {
                serverName = serverName.substring(0, serverName.length() - 1);
            }
            return new DNSRecordSRV(priority, weight, port, serverName);
        }
        catch (IllegalArgumentException err) {
            throw err;
        }
        catch (Exception err) {
            throw new IllegalArgumentException("Failed to parse DNS SRV record '" + record + "'", err);
        }
    }

    public String toString() {
        return String.format("DNS.SRV[pri=%d w=%d port=%d h='%s']", this.priority, this.weight, this.port, this.serverName);
    }

    public DNSRecordSRV(int priority, int weight, int port, String serverName) {
        if (priority < 0) {
            throw new IllegalArgumentException("priority must be >= 0, but was: " + priority);
        }
        this.priority = priority;
        if (weight < 0) {
            throw new IllegalArgumentException("weight must be >= 0, but was: " + weight);
        }
        this.weight = weight;
        if (port < 0 || port > 65535) {
            throw new IllegalArgumentException("port must be between 0 and 65535, but was: " + port);
        }
        this.port = port;
        if (serverName == null || serverName.trim().isEmpty()) {
            throw new IllegalArgumentException("hostname is not supposed to be null or empty in a SRV Record");
        }
        this.serverName = serverName;
    }

    public int hashCode() {
        return this.serverName.hashCode();
    }

    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof DNSRecordSRV)) {
            return false;
        }
        DNSRecordSRV r = (DNSRecordSRV)other;
        return this.port == r.port && this.weight == r.weight && this.priority == r.priority && this.serverName.equals(r.serverName);
    }

    @Override
    public int compareTo(DNSRecordSRV o) {
        if (o == null) {
            return 1;
        }
        int p = Integer.compare(this.priority, o.priority);
        if (p != 0) {
            return p;
        }
        p = Integer.compare(this.weight, o.weight);
        if (p != 0) {
            return p;
        }
        p = Integer.compare(this.port, o.port);
        if (p != 0) {
            return p;
        }
        return this.serverName.compareTo(o.serverName);
    }

    public int getPriority() {
        return this.priority;
    }

    public int getWeight() {
        return this.weight;
    }

    public int getPort() {
        return this.port;
    }

    public String getServerName() {
        return this.serverName;
    }
}

