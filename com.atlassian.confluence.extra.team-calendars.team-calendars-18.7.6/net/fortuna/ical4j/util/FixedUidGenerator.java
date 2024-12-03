/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.util;

import java.net.SocketException;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.util.HostInfo;
import net.fortuna.ical4j.util.InetAddressHostInfo;
import net.fortuna.ical4j.util.UidGenerator;

public class FixedUidGenerator
implements UidGenerator {
    private final String pid;
    private final String hostName;
    private static long lastMillis;

    public FixedUidGenerator(String pid) throws SocketException {
        this(new InetAddressHostInfo(), pid);
    }

    public FixedUidGenerator(HostInfo hostInfo, String pid) {
        this.hostName = hostInfo == null ? null : hostInfo.getHostName();
        this.pid = pid;
    }

    @Override
    public Uid generateUid() {
        StringBuilder b = new StringBuilder();
        b.append(FixedUidGenerator.uniqueTimestamp());
        b.append('-');
        b.append(this.pid);
        if (this.hostName != null) {
            b.append('@');
            b.append(this.hostName);
        }
        return new Uid(b.toString());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static DateTime uniqueTimestamp() {
        Class<FixedUidGenerator> clazz = FixedUidGenerator.class;
        synchronized (FixedUidGenerator.class) {
            long currentMillis = System.currentTimeMillis();
            if (currentMillis < lastMillis) {
                currentMillis = lastMillis;
            }
            if (currentMillis - lastMillis < 1000L) {
                currentMillis += 1000L;
            }
            lastMillis = currentMillis;
            // ** MonitorExit[var2] (shouldn't be in output)
            DateTime timestamp = new DateTime(currentMillis);
            timestamp.setUtc(true);
            return timestamp;
        }
    }
}

