/*
 * Decompiled with CFR 0.152.
 */
package oshi.driver.windows.perfmon;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.driver.windows.perfmon.PerfmonDisabled;
import oshi.util.platform.windows.PerfCounterWildcardQuery;
import oshi.util.tuples.Pair;

@ThreadSafe
public final class ThreadInformation {
    private ThreadInformation() {
    }

    public static Pair<List<String>, Map<ThreadPerformanceProperty, List<Long>>> queryThreadCounters() {
        if (PerfmonDisabled.PERF_PROC_DISABLED) {
            return new Pair<List<String>, Map<ThreadPerformanceProperty, List<Long>>>(Collections.emptyList(), Collections.emptyMap());
        }
        return PerfCounterWildcardQuery.queryInstancesAndValues(ThreadPerformanceProperty.class, "Thread", "Win32_PerfRawData_PerfProc_Thread WHERE NOT Name LIKE \"%_Total\"");
    }

    public static enum ThreadPerformanceProperty implements PerfCounterWildcardQuery.PdhCounterWildcardProperty
    {
        NAME("^*_Total"),
        PERCENTUSERTIME("% User Time"),
        PERCENTPRIVILEGEDTIME("% Privileged Time"),
        ELAPSEDTIME("Elapsed Time"),
        PRIORITYCURRENT("Priority Current"),
        STARTADDRESS("Start Address"),
        THREADSTATE("Thread State"),
        THREADWAITREASON("Thread Wait Reason"),
        IDPROCESS("ID Process"),
        IDTHREAD("ID Thread"),
        CONTEXTSWITCHESPERSEC("Context Switches/sec");

        private final String counter;

        private ThreadPerformanceProperty(String counter) {
            this.counter = counter;
        }

        @Override
        public String getCounter() {
            return this.counter;
        }
    }
}

