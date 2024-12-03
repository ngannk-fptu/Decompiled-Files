/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jna.Memory
 *  com.sun.jna.Native
 *  com.sun.jna.Pointer
 *  com.sun.jna.Structure
 *  com.sun.jna.platform.unix.LibCAPI$size_t
 *  com.sun.jna.platform.unix.LibCAPI$size_t$ByReference
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package oshi.util.platform.unix.openbsd;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.platform.unix.LibCAPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.jna.platform.unix.OpenBsdLibc;
import oshi.util.ExecutingCommand;
import oshi.util.ParseUtil;

@ThreadSafe
public final class OpenBsdSysctlUtil {
    private static final String SYSCTL_N = "sysctl -n ";
    private static final Logger LOG = LoggerFactory.getLogger(OpenBsdSysctlUtil.class);
    private static final String SYSCTL_FAIL = "Failed sysctl call: {}, Error code: {}";

    private OpenBsdSysctlUtil() {
    }

    public static int sysctl(int[] name, int def) {
        LibCAPI.size_t.ByReference size = new LibCAPI.size_t.ByReference(new LibCAPI.size_t((long)OpenBsdLibc.INT_SIZE));
        Memory p = new Memory(size.longValue());
        if (0 != OpenBsdLibc.INSTANCE.sysctl(name, name.length, (Pointer)p, size, null, LibCAPI.size_t.ZERO)) {
            LOG.warn(SYSCTL_FAIL, (Object)name, (Object)Native.getLastError());
            return def;
        }
        return p.getInt(0L);
    }

    public static long sysctl(int[] name, long def) {
        LibCAPI.size_t.ByReference size = new LibCAPI.size_t.ByReference(new LibCAPI.size_t((long)OpenBsdLibc.UINT64_SIZE));
        Memory p = new Memory(size.longValue());
        if (0 != OpenBsdLibc.INSTANCE.sysctl(name, name.length, (Pointer)p, size, null, LibCAPI.size_t.ZERO)) {
            LOG.warn(SYSCTL_FAIL, (Object)name, (Object)Native.getLastError());
            return def;
        }
        return p.getLong(0L);
    }

    public static String sysctl(int[] name, String def) {
        LibCAPI.size_t.ByReference size = new LibCAPI.size_t.ByReference();
        if (0 != OpenBsdLibc.INSTANCE.sysctl(name, name.length, null, size, null, LibCAPI.size_t.ZERO)) {
            LOG.warn(SYSCTL_FAIL, (Object)name, (Object)Native.getLastError());
            return def;
        }
        Memory p = new Memory(size.longValue() + 1L);
        if (0 != OpenBsdLibc.INSTANCE.sysctl(name, name.length, (Pointer)p, size, null, LibCAPI.size_t.ZERO)) {
            LOG.warn(SYSCTL_FAIL, (Object)name, (Object)Native.getLastError());
            return def;
        }
        return p.getString(0L);
    }

    public static boolean sysctl(int[] name, Structure struct) {
        if (0 != OpenBsdLibc.INSTANCE.sysctl(name, name.length, struct.getPointer(), new LibCAPI.size_t.ByReference(new LibCAPI.size_t((long)struct.size())), null, LibCAPI.size_t.ZERO)) {
            LOG.error(SYSCTL_FAIL, (Object)name, (Object)Native.getLastError());
            return false;
        }
        struct.read();
        return true;
    }

    public static Memory sysctl(int[] name) {
        LibCAPI.size_t.ByReference size = new LibCAPI.size_t.ByReference();
        if (0 != OpenBsdLibc.INSTANCE.sysctl(name, name.length, null, size, null, LibCAPI.size_t.ZERO)) {
            LOG.error(SYSCTL_FAIL, (Object)name, (Object)Native.getLastError());
            return null;
        }
        Memory m = new Memory(size.longValue());
        if (0 != OpenBsdLibc.INSTANCE.sysctl(name, name.length, (Pointer)m, size, null, LibCAPI.size_t.ZERO)) {
            LOG.error(SYSCTL_FAIL, (Object)name, (Object)Native.getLastError());
            return null;
        }
        return m;
    }

    public static int sysctl(String name, int def) {
        return ParseUtil.parseIntOrDefault(ExecutingCommand.getFirstAnswer(SYSCTL_N + name), def);
    }

    public static long sysctl(String name, long def) {
        return ParseUtil.parseLongOrDefault(ExecutingCommand.getFirstAnswer(SYSCTL_N + name), def);
    }

    public static String sysctl(String name, String def) {
        String v = ExecutingCommand.getFirstAnswer(SYSCTL_N + name);
        if (null == v || v.isEmpty()) {
            return def;
        }
        return v;
    }
}

