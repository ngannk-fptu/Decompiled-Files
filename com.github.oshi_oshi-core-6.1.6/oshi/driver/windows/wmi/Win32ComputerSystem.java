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
public final class Win32ComputerSystem {
    private static final String WIN32_COMPUTER_SYSTEM = "Win32_ComputerSystem";

    private Win32ComputerSystem() {
    }

    public static WbemcliUtil.WmiResult<ComputerSystemProperty> queryComputerSystem() {
        WbemcliUtil.WmiQuery computerSystemQuery = new WbemcliUtil.WmiQuery(WIN32_COMPUTER_SYSTEM, ComputerSystemProperty.class);
        return Objects.requireNonNull(WmiQueryHandler.createInstance()).queryWMI(computerSystemQuery);
    }

    public static enum ComputerSystemProperty {
        MANUFACTURER,
        MODEL;

    }
}

