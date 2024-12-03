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
public final class Win32Fan {
    private static final String WIN32_FAN = "Win32_Fan";

    private Win32Fan() {
    }

    public static WbemcliUtil.WmiResult<SpeedProperty> querySpeed() {
        WbemcliUtil.WmiQuery fanQuery = new WbemcliUtil.WmiQuery(WIN32_FAN, SpeedProperty.class);
        return Objects.requireNonNull(WmiQueryHandler.createInstance()).queryWMI(fanQuery);
    }

    public static enum SpeedProperty {
        DESIREDSPEED;

    }
}

