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
public final class Win32Bios {
    private static final String WIN32_BIOS_WHERE_PRIMARY_BIOS_TRUE = "Win32_BIOS where PrimaryBIOS=true";

    private Win32Bios() {
    }

    public static WbemcliUtil.WmiResult<BiosSerialProperty> querySerialNumber() {
        WbemcliUtil.WmiQuery serialNumQuery = new WbemcliUtil.WmiQuery(WIN32_BIOS_WHERE_PRIMARY_BIOS_TRUE, BiosSerialProperty.class);
        return Objects.requireNonNull(WmiQueryHandler.createInstance()).queryWMI(serialNumQuery);
    }

    public static WbemcliUtil.WmiResult<BiosProperty> queryBiosInfo() {
        WbemcliUtil.WmiQuery biosQuery = new WbemcliUtil.WmiQuery(WIN32_BIOS_WHERE_PRIMARY_BIOS_TRUE, BiosProperty.class);
        return Objects.requireNonNull(WmiQueryHandler.createInstance()).queryWMI(biosQuery);
    }

    public static enum BiosSerialProperty {
        SERIALNUMBER;

    }

    public static enum BiosProperty {
        MANUFACTURER,
        NAME,
        DESCRIPTION,
        VERSION,
        RELEASEDATE;

    }
}

