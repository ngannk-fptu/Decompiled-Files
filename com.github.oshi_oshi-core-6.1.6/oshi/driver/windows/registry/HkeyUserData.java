/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jna.platform.win32.Advapi32Util
 *  com.sun.jna.platform.win32.Advapi32Util$Account
 *  com.sun.jna.platform.win32.Advapi32Util$InfoKey
 *  com.sun.jna.platform.win32.Win32Exception
 *  com.sun.jna.platform.win32.WinReg
 *  com.sun.jna.platform.win32.WinReg$HKEY
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package oshi.driver.windows.registry;

import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.Win32Exception;
import com.sun.jna.platform.win32.WinReg;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.software.os.OSSession;

@ThreadSafe
public final class HkeyUserData {
    private static final String PATH_DELIMITER = "\\";
    private static final String DEFAULT_DEVICE = "Console";
    private static final String VOLATILE_ENV_SUBKEY = "Volatile Environment";
    private static final String CLIENTNAME = "CLIENTNAME";
    private static final String SESSIONNAME = "SESSIONNAME";
    private static final Logger LOG = LoggerFactory.getLogger(HkeyUserData.class);

    private HkeyUserData() {
    }

    public static List<OSSession> queryUserSessions() {
        ArrayList<OSSession> sessions = new ArrayList<OSSession>();
        for (String sidKey : Advapi32Util.registryGetKeys((WinReg.HKEY)WinReg.HKEY_USERS)) {
            if (sidKey.startsWith(".") || sidKey.endsWith("_Classes")) continue;
            try {
                Advapi32Util.Account a = Advapi32Util.getAccountBySid((String)sidKey);
                String name = a.name;
                String device = DEFAULT_DEVICE;
                String host = a.domain;
                long loginTime = 0L;
                String keyPath = sidKey + PATH_DELIMITER + VOLATILE_ENV_SUBKEY;
                if (Advapi32Util.registryKeyExists((WinReg.HKEY)WinReg.HKEY_USERS, (String)keyPath)) {
                    WinReg.HKEY hKey = Advapi32Util.registryGetKey((WinReg.HKEY)WinReg.HKEY_USERS, (String)keyPath, (int)131097).getValue();
                    Advapi32Util.InfoKey info = Advapi32Util.registryQueryInfoKey((WinReg.HKEY)hKey, (int)0);
                    loginTime = info.lpftLastWriteTime.toTime();
                    for (String subKey : Advapi32Util.registryGetKeys((WinReg.HKEY)hKey)) {
                        String client;
                        String session;
                        String subKeyPath = keyPath + PATH_DELIMITER + subKey;
                        if (Advapi32Util.registryValueExists((WinReg.HKEY)WinReg.HKEY_USERS, (String)subKeyPath, (String)SESSIONNAME) && !(session = Advapi32Util.registryGetStringValue((WinReg.HKEY)WinReg.HKEY_USERS, (String)subKeyPath, (String)SESSIONNAME)).isEmpty()) {
                            device = session;
                        }
                        if (!Advapi32Util.registryValueExists((WinReg.HKEY)WinReg.HKEY_USERS, (String)subKeyPath, (String)CLIENTNAME) || (client = Advapi32Util.registryGetStringValue((WinReg.HKEY)WinReg.HKEY_USERS, (String)subKeyPath, (String)CLIENTNAME)).isEmpty() || DEFAULT_DEVICE.equals(client)) continue;
                        host = client;
                    }
                    Advapi32Util.registryCloseKey((WinReg.HKEY)hKey);
                }
                sessions.add(new OSSession(name, device, loginTime, host));
            }
            catch (Win32Exception ex) {
                LOG.warn("Error querying SID {} from registry: {}", (Object)sidKey, (Object)ex.getMessage());
            }
        }
        return sessions;
    }
}

