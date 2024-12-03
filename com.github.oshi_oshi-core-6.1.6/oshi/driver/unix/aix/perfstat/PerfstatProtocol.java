/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jna.platform.unix.aix.Perfstat
 *  com.sun.jna.platform.unix.aix.Perfstat$perfstat_id_t
 *  com.sun.jna.platform.unix.aix.Perfstat$perfstat_protocol_t
 */
package oshi.driver.unix.aix.perfstat;

import com.sun.jna.platform.unix.aix.Perfstat;
import oshi.annotation.concurrent.ThreadSafe;

@ThreadSafe
public final class PerfstatProtocol {
    private static final Perfstat PERF = Perfstat.INSTANCE;

    private PerfstatProtocol() {
    }

    public static Perfstat.perfstat_protocol_t[] queryProtocols() {
        Perfstat.perfstat_protocol_t[] statp;
        Perfstat.perfstat_id_t firstprotocol;
        int ret;
        Perfstat.perfstat_protocol_t protocol = new Perfstat.perfstat_protocol_t();
        int total = PERF.perfstat_protocol(null, null, protocol.size(), 0);
        if (total > 0 && (ret = PERF.perfstat_protocol(firstprotocol = new Perfstat.perfstat_id_t(), statp = (Perfstat.perfstat_protocol_t[])protocol.toArray(total), protocol.size(), total)) > 0) {
            return statp;
        }
        return new Perfstat.perfstat_protocol_t[0];
    }
}

