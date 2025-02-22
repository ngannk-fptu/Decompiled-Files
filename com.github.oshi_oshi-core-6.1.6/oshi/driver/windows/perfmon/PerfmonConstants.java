/*
 * Decompiled with CFR 0.152.
 */
package oshi.driver.windows.perfmon;

import oshi.annotation.concurrent.ThreadSafe;

@ThreadSafe
public final class PerfmonConstants {
    static final String MEMORY = "Memory";
    static final String WIN32_PERF_RAW_DATA_PERF_OS_MEMORY = "Win32_PerfRawData_PerfOS_Memory";
    static final String PAGING_FILE = "Paging File";
    static final String WIN32_PERF_RAW_DATA_PERF_OS_PAGING_FILE = "Win32_PerfRawData_PerfOS_PagingFile";
    static final String PHYSICAL_DISK = "PhysicalDisk";
    static final String WIN32_PERF_RAW_DATA_PERF_DISK_PHYSICAL_DISK_WHERE_NAME_NOT_TOTAL = "Win32_PerfRawData_PerfDisk_PhysicalDisk WHERE Name!=\"_Total\"";
    static final String PROCESS = "Process";
    static final String WIN32_PERFPROC_PROCESS = "Win32_PerfRawData_PerfProc_Process";
    static final String WIN32_PERFPROC_PROCESS_WHERE_NOT_NAME_LIKE_TOTAL = "Win32_PerfRawData_PerfProc_Process WHERE NOT Name LIKE \"%_Total\"";
    static final String THREAD = "Thread";
    static final String WIN32_PERF_RAW_DATA_PERF_PROC_THREAD_WHERE_NOT_NAME_LIKE_TOTAL = "Win32_PerfRawData_PerfProc_Thread WHERE NOT Name LIKE \"%_Total\"";
    static final String PROCESSOR = "Processor";
    static final String WIN32_PERF_RAW_DATA_PERF_OS_PROCESSOR_WHERE_NAME_NOT_TOTAL = "Win32_PerfRawData_PerfOS_Processor WHERE Name!=\"_Total\"";
    static final String WIN32_PERF_RAW_DATA_PERF_OS_PROCESSOR_WHERE_NAME_TOTAL = "Win32_PerfRawData_PerfOS_Processor WHERE Name=\"_Total\"";
    static final String PROCESSOR_INFORMATION = "Processor Information";
    static final String WIN32_PERF_RAW_DATA_COUNTERS_PROCESSOR_INFORMATION_WHERE_NOT_NAME_LIKE_TOTAL = "Win32_PerfRawData_Counters_ProcessorInformation WHERE NOT Name LIKE \"%_Total\"";
    static final String SYSTEM = "System";
    static final String WIN32_PERF_RAW_DATA_PERF_OS_SYSTEM = "Win32_PerfRawData_PerfOS_System";

    private PerfmonConstants() {
        throw new AssertionError();
    }
}

