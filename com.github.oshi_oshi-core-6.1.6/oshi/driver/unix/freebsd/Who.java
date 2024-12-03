/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jna.Native
 */
package oshi.driver.unix.freebsd;

import com.sun.jna.Native;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.jna.platform.unix.FreeBsdLibc;
import oshi.software.os.OSSession;

@ThreadSafe
public final class Who {
    private static final FreeBsdLibc LIBC = FreeBsdLibc.INSTANCE;

    private Who() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static synchronized List<OSSession> queryUtxent() {
        ArrayList<OSSession> whoList = new ArrayList<OSSession>();
        LIBC.setutxent();
        try {
            FreeBsdLibc.FreeBsdUtmpx ut;
            while ((ut = LIBC.getutxent()) != null) {
                if (ut.ut_type != 7 && ut.ut_type != 6) continue;
                String user = Native.toString((byte[])ut.ut_user, (Charset)StandardCharsets.US_ASCII);
                String device = Native.toString((byte[])ut.ut_line, (Charset)StandardCharsets.US_ASCII);
                String host = Native.toString((byte[])ut.ut_host, (Charset)StandardCharsets.US_ASCII);
                long loginTime = ut.ut_tv.tv_sec * 1000L + ut.ut_tv.tv_usec / 1000L;
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

