/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jna.Native
 *  com.sun.jna.Pointer
 *  com.sun.jna.platform.unix.solaris.Kstat2$Kstat2Handle
 *  com.sun.jna.platform.unix.solaris.Kstat2$Kstat2Map
 *  com.sun.jna.platform.unix.solaris.Kstat2$Kstat2MatcherList
 *  com.sun.jna.platform.unix.solaris.Kstat2StatusException
 *  com.sun.jna.platform.unix.solaris.LibKstat
 *  com.sun.jna.platform.unix.solaris.LibKstat$Kstat
 *  com.sun.jna.platform.unix.solaris.LibKstat$KstatCtl
 *  com.sun.jna.platform.unix.solaris.LibKstat$KstatNamed
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package oshi.util.platform.unix.solaris;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.unix.solaris.Kstat2;
import com.sun.jna.platform.unix.solaris.Kstat2StatusException;
import com.sun.jna.platform.unix.solaris.LibKstat;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.annotation.concurrent.GuardedBy;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.software.os.unix.solaris.SolarisOperatingSystem;
import oshi.util.FormatUtil;
import oshi.util.Util;

@ThreadSafe
public final class KstatUtil {
    private static final Logger LOG = LoggerFactory.getLogger(KstatUtil.class);
    private static final Lock CHAIN = new ReentrantLock();
    @GuardedBy(value="CHAIN")
    private static LibKstat.KstatCtl kstatCtl = null;

    private KstatUtil() {
    }

    public static synchronized KstatChain openChain() {
        CHAIN.lock();
        if (kstatCtl == null) {
            kstatCtl = LibKstat.INSTANCE.kstat_open();
        }
        return new KstatChain(kstatCtl);
    }

    public static String dataLookupString(LibKstat.Kstat ksp, String name) {
        if (ksp.ks_type != 1 && ksp.ks_type != 4) {
            throw new IllegalArgumentException("Not a kstat_named or kstat_timer kstat.");
        }
        Pointer p = LibKstat.INSTANCE.kstat_data_lookup(ksp, name);
        if (p == null) {
            LOG.debug("Failed to lookup kstat value for key {}", (Object)name);
            return "";
        }
        LibKstat.KstatNamed data = new LibKstat.KstatNamed(p);
        switch (data.data_type) {
            case 0: {
                return Native.toString((byte[])data.value.charc, (Charset)StandardCharsets.UTF_8);
            }
            case 1: {
                return Integer.toString(data.value.i32);
            }
            case 2: {
                return FormatUtil.toUnsignedString(data.value.ui32);
            }
            case 3: {
                return Long.toString(data.value.i64);
            }
            case 4: {
                return FormatUtil.toUnsignedString(data.value.ui64);
            }
            case 9: {
                return data.value.str.addr.getString(0L);
            }
        }
        LOG.error("Unimplemented kstat data type {}", (Object)data.data_type);
        return "";
    }

    public static long dataLookupLong(LibKstat.Kstat ksp, String name) {
        if (ksp.ks_type != 1 && ksp.ks_type != 4) {
            throw new IllegalArgumentException("Not a kstat_named or kstat_timer kstat.");
        }
        Pointer p = LibKstat.INSTANCE.kstat_data_lookup(ksp, name);
        if (p == null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Failed lo lookup kstat value on {}:{}:{} for key {}", new Object[]{Native.toString((byte[])ksp.ks_module, (Charset)StandardCharsets.US_ASCII), ksp.ks_instance, Native.toString((byte[])ksp.ks_name, (Charset)StandardCharsets.US_ASCII), name});
            }
            return 0L;
        }
        LibKstat.KstatNamed data = new LibKstat.KstatNamed(p);
        switch (data.data_type) {
            case 1: {
                return data.value.i32;
            }
            case 2: {
                return FormatUtil.getUnsignedInt(data.value.ui32);
            }
            case 3: {
                return data.value.i64;
            }
            case 4: {
                return data.value.ui64;
            }
        }
        LOG.error("Unimplemented or non-numeric kstat data type {}", (Object)data.data_type);
        return 0L;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static Object[] queryKstat2(String mapStr, String ... names) {
        if (!SolarisOperatingSystem.HAS_KSTAT2) {
            throw new UnsupportedOperationException("Kstat2 requires Solaris 11.4+. Use SolarisOperatingSystem#HAS_KSTAT2 to test this.");
        }
        Object[] result = new Object[names.length];
        Kstat2.Kstat2MatcherList matchers = new Kstat2.Kstat2MatcherList();
        CHAIN.lock();
        try {
            matchers.addMatcher(0, mapStr);
            try (Kstat2.Kstat2Handle handle = new Kstat2.Kstat2Handle();){
                Kstat2.Kstat2Map map = handle.lookupMap(mapStr);
                for (int i = 0; i < names.length; ++i) {
                    result[i] = map.getValue(names[i]);
                }
            }
        }
        catch (Kstat2StatusException e) {
            LOG.debug("Failed to get stats on {} for names {}: {}", new Object[]{mapStr, Arrays.toString(names), e.getMessage()});
        }
        finally {
            CHAIN.unlock();
            matchers.free();
        }
        return result;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static List<Object[]> queryKstat2List(String beforeStr, String afterStr, String ... names) {
        if (!SolarisOperatingSystem.HAS_KSTAT2) {
            throw new UnsupportedOperationException("Kstat2 requires Solaris 11.4+. Use SolarisOperatingSystem#HAS_KSTAT2 to test this.");
        }
        ArrayList<Object[]> results = new ArrayList<Object[]>();
        int s = 0;
        Kstat2.Kstat2MatcherList matchers = new Kstat2.Kstat2MatcherList();
        CHAIN.lock();
        try {
            matchers.addMatcher(1, beforeStr + "*" + afterStr);
            try (Kstat2.Kstat2Handle handle = new Kstat2.Kstat2Handle();){
                for (s = 0; s < Integer.MAX_VALUE; ++s) {
                    Object[] result = new Object[names.length];
                    Kstat2.Kstat2Map map = handle.lookupMap(beforeStr + s + afterStr);
                    for (int i = 0; i < names.length; ++i) {
                        result[i] = map.getValue(names[i]);
                    }
                    results.add(result);
                }
            }
        }
        catch (Kstat2StatusException e) {
            LOG.debug("Failed to get stats on {}{}{} for names {}: {}", new Object[]{beforeStr, s, afterStr, Arrays.toString(names), e.getMessage()});
        }
        finally {
            CHAIN.unlock();
            matchers.free();
        }
        return results;
    }

    public static final class KstatChain
    implements AutoCloseable {
        private final LibKstat.KstatCtl localCtlRef;

        private KstatChain(LibKstat.KstatCtl ctl) {
            this.localCtlRef = ctl;
            this.update();
        }

        @GuardedBy(value="CHAIN")
        public boolean read(LibKstat.Kstat ksp) {
            int retry = 0;
            while (0 > LibKstat.INSTANCE.kstat_read(this.localCtlRef, ksp, null)) {
                if (11 != Native.getLastError() || 5 <= ++retry) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Failed to read kstat {}:{}:{}", new Object[]{Native.toString((byte[])ksp.ks_module, (Charset)StandardCharsets.US_ASCII), ksp.ks_instance, Native.toString((byte[])ksp.ks_name, (Charset)StandardCharsets.US_ASCII)});
                    }
                    return false;
                }
                Util.sleep(8 << retry);
            }
            return true;
        }

        @GuardedBy(value="CHAIN")
        public LibKstat.Kstat lookup(String module, int instance, String name) {
            return LibKstat.INSTANCE.kstat_lookup(this.localCtlRef, module, instance, name);
        }

        @GuardedBy(value="CHAIN")
        public List<LibKstat.Kstat> lookupAll(String module, int instance, String name) {
            ArrayList<LibKstat.Kstat> kstats = new ArrayList<LibKstat.Kstat>();
            for (LibKstat.Kstat ksp = LibKstat.INSTANCE.kstat_lookup(this.localCtlRef, module, instance, name); ksp != null; ksp = ksp.next()) {
                if (module != null && !module.equals(Native.toString((byte[])ksp.ks_module, (Charset)StandardCharsets.US_ASCII)) || instance >= 0 && instance != ksp.ks_instance || name != null && !name.equals(Native.toString((byte[])ksp.ks_name, (Charset)StandardCharsets.US_ASCII))) continue;
                kstats.add(ksp);
            }
            return kstats;
        }

        @GuardedBy(value="CHAIN")
        public int update() {
            return LibKstat.INSTANCE.kstat_chain_update(this.localCtlRef);
        }

        @Override
        public void close() {
            CHAIN.unlock();
        }
    }
}

