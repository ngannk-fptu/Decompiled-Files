/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jna.Platform
 */
package oshi.util;

import com.sun.jna.Platform;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.ExecutingCommand;
import oshi.util.FileUtil;
import oshi.util.Memoizer;

@ThreadSafe
public final class UserGroupInfo {
    private static final Supplier<Map<String, String>> USERS_ID_MAP = Memoizer.memoize(UserGroupInfo::getUserMap, TimeUnit.MINUTES.toNanos(1L));
    private static final Supplier<Map<String, String>> GROUPS_ID_MAP = Memoizer.memoize(UserGroupInfo::getGroupMap, TimeUnit.MINUTES.toNanos(1L));

    private UserGroupInfo() {
    }

    public static String getUser(String userId) {
        return USERS_ID_MAP.get().getOrDefault(userId, "unknown");
    }

    public static String getGroupName(String groupId) {
        return GROUPS_ID_MAP.get().getOrDefault(groupId, "unknown");
    }

    private static Map<String, String> getUserMap() {
        HashMap<String, String> userMap = new HashMap<String, String>();
        List<String> passwd = Platform.isAIX() ? FileUtil.readFile("/etc/passwd") : ExecutingCommand.runNative("getent passwd");
        for (String entry : passwd) {
            String[] split = entry.split(":");
            if (split.length <= 2) continue;
            String userName = split[0];
            String uid = split[2];
            userMap.putIfAbsent(uid, userName);
        }
        return userMap;
    }

    private static Map<String, String> getGroupMap() {
        HashMap<String, String> groupMap = new HashMap<String, String>();
        List<String> group = Platform.isAIX() ? FileUtil.readFile("/etc/group") : ExecutingCommand.runNative("getent group");
        for (String entry : group) {
            String[] split = entry.split(":");
            if (split.length <= 2) continue;
            String groupName = split[0];
            String gid = split[2];
            groupMap.putIfAbsent(gid, groupName);
        }
        return groupMap;
    }
}

