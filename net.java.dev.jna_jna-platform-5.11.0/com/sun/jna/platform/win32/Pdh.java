/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jna.Native
 *  com.sun.jna.Pointer
 *  com.sun.jna.Structure
 *  com.sun.jna.Structure$FieldOrder
 *  com.sun.jna.win32.StdCallLibrary
 *  com.sun.jna.win32.W32APIOptions
 */
package com.sun.jna.platform.win32;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.BaseTSD;
import com.sun.jna.platform.win32.WinBase;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;
import java.util.Map;

public interface Pdh
extends StdCallLibrary {
    public static final Pdh INSTANCE = (Pdh)Native.load((String)"Pdh", Pdh.class, (Map)W32APIOptions.DEFAULT_OPTIONS);
    public static final int PDH_MAX_COUNTER_NAME = 1024;
    public static final int PDH_MAX_INSTANCE_NAME = 1024;
    public static final int PDH_MAX_COUNTER_PATH = 2048;
    public static final int PDH_MAX_DATASOURCE_PATH = 1024;
    public static final int PDH_MORE_DATA = -2147481646;
    public static final int PDH_INSUFFICIENT_BUFFER = -1073738814;
    public static final int PDH_INVALID_ARGUMENT = -1073738819;
    public static final int PDH_MEMORY_ALLOCATION_FAILURE = -1073738821;
    public static final int PDH_CSTATUS_NO_MACHINE = -2147481648;
    public static final int PDH_CSTATUS_NO_OBJECT = -1073738824;
    public static final int PDH_CVERSION_WIN40 = 1024;
    public static final int PDH_CVERSION_WIN50 = 1280;
    public static final int PDH_VERSION = 1283;
    public static final int PDH_PATH_WBEM_RESULT = 1;
    public static final int PDH_PATH_WBEM_INPUT = 2;
    public static final int PDH_FMT_RAW = 16;
    public static final int PDH_FMT_ANSI = 32;
    public static final int PDH_FMT_UNICODE = 64;
    public static final int PDH_FMT_LONG = 256;
    public static final int PDH_FMT_DOUBLE = 512;
    public static final int PDH_FMT_LARGE = 1024;
    public static final int PDH_FMT_NOSCALE = 4096;
    public static final int PDH_FMT_1000 = 8192;
    public static final int PDH_FMT_NODATA = 16384;
    public static final int PDH_FMT_NOCAP100 = 32768;
    public static final int PERF_DETAIL_COSTLY = 65536;
    public static final int PERF_DETAIL_STANDARD = 65535;

    public int PdhConnectMachine(String var1);

    public int PdhGetDllVersion(WinDef.DWORDByReference var1);

    public int PdhOpenQuery(String var1, BaseTSD.DWORD_PTR var2, WinNT.HANDLEByReference var3);

    public int PdhCloseQuery(WinNT.HANDLE var1);

    public int PdhMakeCounterPath(PDH_COUNTER_PATH_ELEMENTS var1, char[] var2, WinDef.DWORDByReference var3, int var4);

    public int PdhAddCounter(WinNT.HANDLE var1, String var2, BaseTSD.DWORD_PTR var3, WinNT.HANDLEByReference var4);

    public int PdhAddEnglishCounter(WinNT.HANDLE var1, String var2, BaseTSD.DWORD_PTR var3, WinNT.HANDLEByReference var4);

    public int PdhRemoveCounter(WinNT.HANDLE var1);

    public int PdhGetRawCounterValue(WinNT.HANDLE var1, WinDef.DWORDByReference var2, PDH_RAW_COUNTER var3);

    public int PdhValidatePath(String var1);

    public int PdhCollectQueryData(WinNT.HANDLE var1);

    public int PdhCollectQueryDataEx(WinNT.HANDLE var1, int var2, WinNT.HANDLE var3);

    public int PdhCollectQueryDataWithTime(WinNT.HANDLE var1, WinDef.LONGLONGByReference var2);

    public int PdhSetQueryTimeRange(WinNT.HANDLE var1, PDH_TIME_INFO var2);

    public int PdhEnumObjectItems(String var1, String var2, String var3, Pointer var4, WinDef.DWORDByReference var5, Pointer var6, WinDef.DWORDByReference var7, int var8, int var9);

    public int PdhLookupPerfIndexByName(String var1, String var2, WinDef.DWORDByReference var3);

    public int PdhLookupPerfNameByIndex(String var1, int var2, Pointer var3, WinDef.DWORDByReference var4);

    @Structure.FieldOrder(value={"StartTime", "EndTime", "SampleCount"})
    public static class PDH_TIME_INFO
    extends Structure {
        public long StartTime;
        public long EndTime;
        public int SampleCount;
    }

    @Structure.FieldOrder(value={"CStatus", "TimeStamp", "FirstValue", "SecondValue", "MultiCount"})
    public static class PDH_RAW_COUNTER
    extends Structure {
        public int CStatus;
        public WinBase.FILETIME TimeStamp = new WinBase.FILETIME();
        public long FirstValue;
        public long SecondValue;
        public int MultiCount;
    }

    @Structure.FieldOrder(value={"szMachineName", "szObjectName", "szInstanceName", "szParentInstance", "dwInstanceIndex", "szCounterName"})
    public static class PDH_COUNTER_PATH_ELEMENTS
    extends Structure {
        public String szMachineName;
        public String szObjectName;
        public String szInstanceName;
        public String szParentInstance;
        public int dwInstanceIndex;
        public String szCounterName;
    }
}

