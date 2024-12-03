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
public final class Win32ComputerSystemProduct {
    private static final String WIN32_COMPUTER_SYSTEM_PRODUCT = "Win32_ComputerSystemProduct";

    private Win32ComputerSystemProduct() {
    }

    public static WbemcliUtil.WmiResult<ComputerSystemProductProperty> queryIdentifyingNumberUUID() {
        WbemcliUtil.WmiQuery identifyingNumberQuery = new WbemcliUtil.WmiQuery(WIN32_COMPUTER_SYSTEM_PRODUCT, ComputerSystemProductProperty.class);
        return Objects.requireNonNull(WmiQueryHandler.createInstance()).queryWMI(identifyingNumberQuery);
    }

    public static enum ComputerSystemProductProperty {
        IDENTIFYINGNUMBER,
        UUID;

    }
}

