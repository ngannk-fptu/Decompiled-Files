/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jna.platform.win32.Kernel32
 *  com.sun.jna.platform.win32.WinBase$SYSTEM_INFO
 *  com.sun.jna.platform.win32.WinNT$HANDLE
 *  com.sun.jna.ptr.IntByReference
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package oshi.software.os.windows;

import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinBase;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WindowsOSSystemInfo {
    private static final Logger LOG = LoggerFactory.getLogger(WindowsOSSystemInfo.class);
    private WinBase.SYSTEM_INFO systemInfo = null;

    public WindowsOSSystemInfo() {
        this.init();
    }

    public WindowsOSSystemInfo(WinBase.SYSTEM_INFO si) {
        this.systemInfo = si;
    }

    private void init() {
        WinBase.SYSTEM_INFO si = new WinBase.SYSTEM_INFO();
        Kernel32.INSTANCE.GetSystemInfo(si);
        try {
            IntByReference isWow64 = new IntByReference();
            WinNT.HANDLE hProcess = Kernel32.INSTANCE.GetCurrentProcess();
            if (Kernel32.INSTANCE.IsWow64Process(hProcess, isWow64) && isWow64.getValue() > 0) {
                Kernel32.INSTANCE.GetNativeSystemInfo(si);
            }
        }
        catch (UnsatisfiedLinkError e) {
            LOG.trace("No WOW64 support: {}", (Object)e.getMessage());
        }
        this.systemInfo = si;
        LOG.debug("Initialized OSNativeSystemInfo");
    }

    public int getNumberOfProcessors() {
        return this.systemInfo.dwNumberOfProcessors.intValue();
    }
}

