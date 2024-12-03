/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jna.Native
 */
package oshi.driver.linux;

import com.sun.jna.Native;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.jna.platform.linux.LinuxLibc;
import oshi.software.os.OSSession;
import oshi.util.ParseUtil;

@ThreadSafe
public final class Who {
    private static final LinuxLibc LIBC = LinuxLibc.INSTANCE;

    private Who() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static synchronized List<OSSession> queryUtxent() {
        ArrayList<OSSession> whoList = new ArrayList<OSSession>();
        LIBC.setutxent();
        try {
            LinuxLibc.LinuxUtmpx ut;
            while ((ut = LIBC.getutxent()) != null) {
                if (ut.ut_type != 7 && ut.ut_type != 6) continue;
                String user = Native.toString((byte[])ut.ut_user, (Charset)Charset.defaultCharset());
                String device = Native.toString((byte[])ut.ut_line, (Charset)Charset.defaultCharset());
                String host = ParseUtil.parseUtAddrV6toIP(ut.ut_addr_v6);
                long loginTime = (long)ut.ut_tv.tv_sec * 1000L + (long)ut.ut_tv.tv_usec / 1000L;
                if (user.isEmpty() || device.isEmpty() || loginTime < 0L || loginTime > System.currentTimeMillis()) {
                    List<OSSession> list = oshi.driver.unix.Who.queryWho();
                    return list;
                }
                whoList.add(new OSSession(user, device, loginTime, host));
            }
        }
        finally {
            LIBC.endutxent();
        }
        return whoList;
    }
}

