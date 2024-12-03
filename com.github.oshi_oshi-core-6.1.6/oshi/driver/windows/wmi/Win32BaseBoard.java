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
public final class Win32BaseBoard {
    private static final String WIN32_BASEBOARD = "Win32_BaseBoard";

    private Win32BaseBoard() {
    }

    public static WbemcliUtil.WmiResult<BaseBoardProperty> queryBaseboardInfo() {
        WbemcliUtil.WmiQuery baseboardQuery = new WbemcliUtil.WmiQuery(WIN32_BASEBOARD, BaseBoardProperty.class);
        return Objects.requireNonNull(WmiQueryHandler.createInstance()).queryWMI(baseboardQuery);
    }

    public static enum BaseBoardProperty {
        MANUFACTURER,
        MODEL,
        VERSION,
        SERIALNUMBER;

    }
}

