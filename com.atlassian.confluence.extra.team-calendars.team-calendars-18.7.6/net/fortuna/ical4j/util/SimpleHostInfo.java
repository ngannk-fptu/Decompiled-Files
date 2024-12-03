/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.util;

import net.fortuna.ical4j.util.HostInfo;

public class SimpleHostInfo
implements HostInfo {
    private final String hostName;

    public SimpleHostInfo(String hostName) {
        this.hostName = hostName;
    }

    @Override
    public String getHostName() {
        return this.hostName;
    }
}

