/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jna.platform.win32.COM.WbemcliUtil$WmiQuery
 *  com.sun.jna.platform.win32.COM.WbemcliUtil$WmiResult
 */
package oshi.driver.windows.wmi;

import com.sun.jna.platform.win32.COM.WbemcliUtil;
import java.util.Objects;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.platform.windows.WmiQueryHandler;

@ThreadSafe
public final class Win32OperatingSystem {
    private static final String WIN32_OPERATING_SYSTEM = "Win32_OperatingSystem";

    private Win32OperatingSystem() {
    }

    public static WbemcliUtil.WmiResult<OSVersionProperty> queryOsVersion() {
        WbemcliUtil.WmiQuery osVersionQuery = new WbemcliUtil.WmiQuery(WIN32_OPERATING_SYSTEM, OSVersionProperty.class);
        return Objects.requireNonNull(WmiQueryHandler.createInstance()).queryWMI(osVersionQuery);
    }

    public static enum OSVersionProperty {
        VERSION,
        PRODUCTTYPE,
        BUILDNUMBER,
        CSDVERSION,
        SUITEMASK;

    }
}

