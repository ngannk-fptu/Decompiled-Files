/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jna.platform.win32.Advapi32Util
 *  com.sun.jna.platform.win32.WinReg
 *  com.sun.jna.platform.win32.WinReg$HKEY
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package oshi.driver.windows.perfmon;

import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.WinReg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.GlobalConfig;
import oshi.util.Util;

@ThreadSafe
public final class PerfmonDisabled {
    private static final Logger LOG = LoggerFactory.getLogger(PerfmonDisabled.class);
    static final boolean PERF_OS_DISABLED = PerfmonDisabled.isDisabled("oshi.os.windows.perfos.disabled", "PerfOS");
    static final boolean PERF_PROC_DISABLED = PerfmonDisabled.isDisabled("oshi.os.windows.perfproc.disabled", "PerfProc");
    static final boolean PERF_DISK_DISABLED = PerfmonDisabled.isDisabled("oshi.os.windows.perfdisk.disabled", "PerfDisk");

    private PerfmonDisabled() {
        throw new AssertionError();
    }

    private static boolean isDisabled(String config, String service) {
        String perfDisabled = GlobalConfig.get(config);
        if (Util.isBlank(perfDisabled)) {
            String value;
            String key = String.format("SYSTEM\\CurrentControlSet\\Services\\%s\\Performance", service);
            if (Advapi32Util.registryValueExists((WinReg.HKEY)WinReg.HKEY_LOCAL_MACHINE, (String)key, (String)(value = "Disable Performance Counters")) && Advapi32Util.registryGetIntValue((WinReg.HKEY)WinReg.HKEY_LOCAL_MACHINE, (String)key, (String)value) > 0) {
                LOG.warn("{} counters are disabled and won't return data: {}\\\\{}\\\\{} > 0.", new Object[]{service, "HKEY_LOCAL_MACHINE", key, value});
                return true;
            }
            return false;
        }
        return Boolean.parseBoolean(perfDisabled);
    }
}

