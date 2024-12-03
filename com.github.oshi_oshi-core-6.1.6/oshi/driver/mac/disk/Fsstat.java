/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jna.Native
 *  com.sun.jna.platform.mac.SystemB
 *  com.sun.jna.platform.mac.SystemB$Statfs
 */
package oshi.driver.mac.disk;

import com.sun.jna.Native;
import com.sun.jna.platform.mac.SystemB;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import oshi.annotation.concurrent.ThreadSafe;

@ThreadSafe
public final class Fsstat {
    private Fsstat() {
    }

    public static Map<String, String> queryPartitionToMountMap() {
        SystemB.Statfs[] fs;
        HashMap<String, String> mountPointMap = new HashMap<String, String>();
        int numfs = Fsstat.queryFsstat(null, 0, 0);
        for (SystemB.Statfs f : fs = Fsstat.getFileSystems(numfs)) {
            String mntFrom = Native.toString((byte[])f.f_mntfromname, (Charset)StandardCharsets.UTF_8);
            mountPointMap.put(mntFrom.replace("/dev/", ""), Native.toString((byte[])f.f_mntonname, (Charset)StandardCharsets.UTF_8));
        }
        return mountPointMap;
    }

    private static SystemB.Statfs[] getFileSystems(int numfs) {
        SystemB.Statfs[] fs = new SystemB.Statfs[numfs];
        Fsstat.queryFsstat(fs, numfs * new SystemB.Statfs().size(), 16);
        return fs;
    }

    private static int queryFsstat(SystemB.Statfs[] buf, int bufsize, int flags) {
        return SystemB.INSTANCE.getfsstat64(buf, bufsize, flags);
    }
}

