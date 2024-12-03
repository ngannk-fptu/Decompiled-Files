/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jna.platform.win32.BaseTSD$DWORD_PTR
 *  com.sun.jna.platform.win32.Pdh
 *  com.sun.jna.platform.win32.Pdh$PDH_RAW_COUNTER
 *  com.sun.jna.platform.win32.VersionHelpers
 *  com.sun.jna.platform.win32.WinDef$DWORD
 *  com.sun.jna.platform.win32.WinDef$DWORDByReference
 *  com.sun.jna.platform.win32.WinDef$LONGLONGByReference
 *  com.sun.jna.platform.win32.WinNT$HANDLEByReference
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package oshi.util.platform.windows;

import com.sun.jna.platform.win32.BaseTSD;
import com.sun.jna.platform.win32.Pdh;
import com.sun.jna.platform.win32.VersionHelpers;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.annotation.concurrent.Immutable;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.FormatUtil;
import oshi.util.ParseUtil;
import oshi.util.Util;

@ThreadSafe
public final class PerfDataUtil {
    private static final Logger LOG = LoggerFactory.getLogger(PerfDataUtil.class);
    private static final BaseTSD.DWORD_PTR PZERO = new BaseTSD.DWORD_PTR(0L);
    private static final WinDef.DWORDByReference PDH_FMT_RAW = new WinDef.DWORDByReference(new WinDef.DWORD(16L));
    private static final Pdh PDH = Pdh.INSTANCE;
    private static final boolean IS_VISTA_OR_GREATER = VersionHelpers.IsWindowsVistaOrGreater();

    private PerfDataUtil() {
    }

    public static PerfCounter createCounter(String object, String instance, String counter) {
        return new PerfCounter(object, instance, counter);
    }

    public static long updateQueryTimestamp(WinNT.HANDLEByReference query) {
        WinDef.LONGLONGByReference pllTimeStamp = new WinDef.LONGLONGByReference();
        int ret = IS_VISTA_OR_GREATER ? PDH.PdhCollectQueryDataWithTime(query.getValue(), pllTimeStamp) : PDH.PdhCollectQueryData(query.getValue());
        int retries = 0;
        while (ret == -2147481643 && retries++ < 3) {
            Util.sleep(1 << retries);
            ret = IS_VISTA_OR_GREATER ? PDH.PdhCollectQueryDataWithTime(query.getValue(), pllTimeStamp) : PDH.PdhCollectQueryData(query.getValue());
        }
        if (ret != 0) {
            if (LOG.isWarnEnabled()) {
                LOG.warn("Failed to update counter. Error code: {}", (Object)String.format(FormatUtil.formatError(ret), new Object[0]));
            }
            return 0L;
        }
        return IS_VISTA_OR_GREATER ? ParseUtil.filetimeToUtcMs(pllTimeStamp.getValue().longValue(), true) : System.currentTimeMillis();
    }

    public static boolean openQuery(WinNT.HANDLEByReference q) {
        int ret = PDH.PdhOpenQuery(null, PZERO, q);
        if (ret != 0) {
            if (LOG.isErrorEnabled()) {
                LOG.error("Failed to open PDH Query. Error code: {}", (Object)String.format(FormatUtil.formatError(ret), new Object[0]));
            }
            return false;
        }
        return true;
    }

    public static boolean closeQuery(WinNT.HANDLEByReference q) {
        return 0 == PDH.PdhCloseQuery(q.getValue());
    }

    public static long queryCounter(WinNT.HANDLEByReference counter) {
        Pdh.PDH_RAW_COUNTER counterValue = new Pdh.PDH_RAW_COUNTER();
        int ret = PDH.PdhGetRawCounterValue(counter.getValue(), PDH_FMT_RAW, counterValue);
        if (ret != 0) {
            if (LOG.isWarnEnabled()) {
                LOG.warn("Failed to get counter. Error code: {}", (Object)String.format(FormatUtil.formatError(ret), new Object[0]));
            }
            return ret;
        }
        return counterValue.FirstValue;
    }

    public static long querySecondCounter(WinNT.HANDLEByReference counter) {
        Pdh.PDH_RAW_COUNTER counterValue = new Pdh.PDH_RAW_COUNTER();
        int ret = PDH.PdhGetRawCounterValue(counter.getValue(), PDH_FMT_RAW, counterValue);
        if (ret != 0) {
            if (LOG.isWarnEnabled()) {
                LOG.warn("Failed to get counter. Error code: {}", (Object)String.format(FormatUtil.formatError(ret), new Object[0]));
            }
            return ret;
        }
        return counterValue.SecondValue;
    }

    public static boolean addCounter(WinNT.HANDLEByReference query, String path, WinNT.HANDLEByReference p) {
        int ret;
        int n = ret = IS_VISTA_OR_GREATER ? PDH.PdhAddEnglishCounter(query.getValue(), path, PZERO, p) : PDH.PdhAddCounter(query.getValue(), path, PZERO, p);
        if (ret != 0) {
            if (LOG.isWarnEnabled()) {
                LOG.warn("Failed to add PDH Counter: {}, Error code: {}", (Object)path, (Object)String.format(FormatUtil.formatError(ret), new Object[0]));
            }
            return false;
        }
        return true;
    }

    public static boolean removeCounter(WinNT.HANDLEByReference p) {
        return 0 == PDH.PdhRemoveCounter(p.getValue());
    }

    @Immutable
    public static class PerfCounter {
        private final String object;
        private final String instance;
        private final String counter;
        private final boolean baseCounter;

        public PerfCounter(String objectName, String instanceName, String counterName) {
            this.object = objectName;
            this.instance = instanceName;
            int baseIdx = counterName.indexOf("_Base");
            if (baseIdx > 0) {
                this.counter = counterName.substring(0, baseIdx);
                this.baseCounter = true;
            } else {
                this.counter = counterName;
                this.baseCounter = false;
            }
        }

        public String getObject() {
            return this.object;
        }

        public String getInstance() {
            return this.instance;
        }

        public String getCounter() {
            return this.counter;
        }

        public boolean isBaseCounter() {
            return this.baseCounter;
        }

        public String getCounterPath() {
            StringBuilder sb = new StringBuilder();
            sb.append('\\').append(this.object);
            if (this.instance != null) {
                sb.append('(').append(this.instance).append(')');
            }
            sb.append('\\').append(this.counter);
            return sb.toString();
        }
    }
}

