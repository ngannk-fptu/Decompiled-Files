/*
 * Decompiled with CFR 0.152.
 */
package oshi.driver.unix.aix;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.hardware.HWPartition;
import oshi.util.ExecutingCommand;
import oshi.util.ParseUtil;
import oshi.util.tuples.Pair;

@ThreadSafe
public final class Lspv {
    private Lspv() {
    }

    public static List<HWPartition> queryLogicalVolumes(String device, Map<String, Pair<Integer, Integer>> majMinMap) {
        String stateMarker = "PV STATE:";
        String sizeMarker = "PP SIZE:";
        long ppSize = 0L;
        for (String s : ExecutingCommand.runNative("lspv -L " + device)) {
            if (s.startsWith(stateMarker)) {
                if (s.contains("active")) continue;
                return Collections.emptyList();
            }
            if (!s.contains(sizeMarker)) continue;
            ppSize = ParseUtil.getFirstIntValue(s);
        }
        if (ppSize == 0L) {
            return Collections.emptyList();
        }
        ppSize <<= 20;
        HashMap<String, String> mountMap = new HashMap<String, String>();
        HashMap<String, String> typeMap = new HashMap<String, String>();
        HashMap<String, Integer> ppMap = new HashMap<String, Integer>();
        for (String s : ExecutingCommand.runNative("lspv -p " + device)) {
            String[] split = ParseUtil.whitespaces.split(s.trim());
            if (split.length < 6 || !"used".equals(split[1])) continue;
            String name = split[split.length - 3];
            mountMap.put(name, split[split.length - 1]);
            typeMap.put(name, split[split.length - 2]);
            int ppCount = 1 + ParseUtil.getNthIntValue(split[0], 2) - ParseUtil.getNthIntValue(split[0], 1);
            ppMap.put(name, ppCount + ppMap.getOrDefault(name, 0));
        }
        ArrayList<HWPartition> partitions = new ArrayList<HWPartition>();
        for (Map.Entry entry : mountMap.entrySet()) {
            String mount = "N/A".equals(entry.getValue()) ? "" : (String)entry.getValue();
            String name = (String)entry.getKey();
            String type = (String)typeMap.get(name);
            long size = ppSize * (long)((Integer)ppMap.get(name)).intValue();
            Pair<Integer, Integer> majMin = majMinMap.get(name);
            int major = majMin == null ? ParseUtil.getFirstIntValue(name) : majMin.getA();
            int minor = majMin == null ? ParseUtil.getFirstIntValue(name) : majMin.getB();
            partitions.add(new HWPartition(name, name, type, "", size, major, minor, mount));
        }
        return partitions;
    }
}

