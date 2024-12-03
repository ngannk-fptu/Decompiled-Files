/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.cfg;

import com.mchange.v2.cfg.BasicMultiPropertiesConfig;
import com.mchange.v2.cfg.CombinedMultiPropertiesConfig;
import com.mchange.v2.cfg.DelayedLogItem;
import com.mchange.v2.cfg.MultiPropertiesConfig;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;

final class ConfigUtils {
    private static final String[] DFLT_VM_RSRC_PATHFILES = new String[]{"/com/mchange/v2/cfg/vmConfigResourcePaths.txt", "/mchange-config-resource-paths.txt"};
    private static final String[] HARDCODED_DFLT_RSRC_PATHS = new String[]{"/mchange-commons.properties", "hocon:/reference,/application,/", "/"};
    static final String[] NO_PATHS = new String[0];
    static MultiPropertiesConfig vmConfig = null;

    static MultiPropertiesConfig read(String[] stringArray, List list) {
        return new BasicMultiPropertiesConfig(stringArray, list);
    }

    public static MultiPropertiesConfig read(String[] stringArray) {
        return new BasicMultiPropertiesConfig(stringArray);
    }

    public static MultiPropertiesConfig combine(MultiPropertiesConfig[] multiPropertiesConfigArray) {
        return new CombinedMultiPropertiesConfig(multiPropertiesConfigArray).toBasic();
    }

    public static MultiPropertiesConfig readVmConfig(String[] stringArray, String[] stringArray2) {
        return ConfigUtils.readVmConfig(stringArray, stringArray2, null);
    }

    static List vmCondensedPaths(String[] stringArray, String[] stringArray2, List list) {
        List list2 = ConfigUtils.condensePaths(new String[][]{stringArray, ConfigUtils.vmResourcePaths(list), stringArray2});
        return ConfigUtils.ensureHoconInterresolvability(list2);
    }

    static String stringFromPathsList(List list) {
        StringBuffer stringBuffer = new StringBuffer(2048);
        int n = list.size();
        for (int i = 0; i < n; ++i) {
            if (i != 0) {
                stringBuffer.append(", ");
            }
            stringBuffer.append(list.get(i));
        }
        return stringBuffer.toString();
    }

    public static MultiPropertiesConfig readVmConfig(String[] stringArray, String[] stringArray2, List list) {
        stringArray = stringArray == null ? NO_PATHS : stringArray;
        stringArray2 = stringArray2 == null ? NO_PATHS : stringArray2;
        List list2 = ConfigUtils.vmCondensedPaths(stringArray, stringArray2, list);
        if (list != null) {
            list.add(new DelayedLogItem(DelayedLogItem.Level.FINER, "Reading VM config for path list " + ConfigUtils.stringFromPathsList(list2)));
        }
        return ConfigUtils.read(list2.toArray(new String[list2.size()]), list);
    }

    private static List condensePaths(String[][] stringArray) {
        HashSet<String> hashSet = new HashSet<String>();
        ArrayList<String> arrayList = new ArrayList<String>();
        int n = stringArray.length;
        while (--n >= 0) {
            int n2 = stringArray[n].length;
            while (--n2 >= 0) {
                String string = stringArray[n][n2];
                if (hashSet.contains(string)) continue;
                hashSet.add(string);
                arrayList.add(string);
            }
        }
        Collections.reverse(arrayList);
        return arrayList;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static List readResourcePathsFromResourcePathsTextFile(String string, List list) {
        ArrayList<String> arrayList = new ArrayList<String>();
        BufferedReader bufferedReader = null;
        try {
            InputStream inputStream = MultiPropertiesConfig.class.getResourceAsStream(string);
            if (inputStream != null) {
                String string2;
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "8859_1"));
                while ((string2 = bufferedReader.readLine()) != null) {
                    if ("".equals(string2 = string2.trim()) || string2.startsWith("#")) continue;
                    arrayList.add(string2);
                }
                if (list != null) {
                    list.add(new DelayedLogItem(DelayedLogItem.Level.FINEST, String.format("Added paths from resource path text file at '%s'", string)));
                }
            } else if (list != null) {
                list.add(new DelayedLogItem(DelayedLogItem.Level.FINEST, String.format("Could not find resource path text file for path '%s'. Skipping.", string)));
            }
        }
        catch (IOException iOException) {
            iOException.printStackTrace();
        }
        finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            }
            catch (IOException iOException) {
                iOException.printStackTrace();
            }
        }
        return arrayList;
    }

    private static List readResourcePathsFromResourcePathsTextFiles(String[] stringArray, List list) {
        ArrayList arrayList = new ArrayList();
        int n = stringArray.length;
        for (int i = 0; i < n; ++i) {
            arrayList.addAll(ConfigUtils.readResourcePathsFromResourcePathsTextFile(stringArray[i], list));
        }
        return arrayList;
    }

    private static String[] vmResourcePaths(List list) {
        List list2 = ConfigUtils.vmResourcePathList(list);
        return list2.toArray(new String[list2.size()]);
    }

    private static List vmResourcePathList(List list) {
        List<String> list2 = ConfigUtils.readResourcePathsFromResourcePathsTextFiles(DFLT_VM_RSRC_PATHFILES, list);
        List<String> list3 = list2.size() > 0 ? list2 : Arrays.asList(HARDCODED_DFLT_RSRC_PATHS);
        return list3;
    }

    public static synchronized MultiPropertiesConfig readVmConfig() {
        return ConfigUtils.readVmConfig(null);
    }

    public static synchronized MultiPropertiesConfig readVmConfig(List list) {
        if (vmConfig == null) {
            List list2 = ConfigUtils.vmResourcePathList(list);
            vmConfig = new BasicMultiPropertiesConfig(list2.toArray(new String[list2.size()]));
        }
        return vmConfig;
    }

    public static synchronized boolean foundVmConfig() {
        return vmConfig != null;
    }

    public static void dumpByPrefix(MultiPropertiesConfig multiPropertiesConfig, String string) {
        Properties properties = multiPropertiesConfig.getPropertiesByPrefix(string);
        TreeMap<Object, Object> treeMap = new TreeMap<Object, Object>();
        treeMap.putAll(properties);
        for (Map.Entry entry : treeMap.entrySet()) {
            System.err.println(entry.getKey() + " --> " + entry.getValue());
        }
    }

    private static void putToSet(Map<String, Set<String>> map, String string, String string2) {
        Set<String> set = map.get(string);
        if (set == null) {
            set = new HashSet<String>();
            map.put(string, set);
        }
        set.add(string2);
    }

    private static String makeHoconPathFromElements(List<String> list) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("hocon:");
        boolean bl = true;
        for (String string : list) {
            if (bl) {
                bl = false;
            } else {
                stringBuilder.append(",");
            }
            stringBuilder.append(string);
        }
        return stringBuilder.toString();
    }

    private static String normalizeHoconPathElement(String string) {
        return string.indexOf(":") < 0 && string.charAt(0) != '/' ? '/' + string : string;
    }

    private static List<String> ensureHoconInterresolvability(List<String> list) {
        Object object;
        HashMap hashMap = new HashMap();
        HashMap<String, Set<String>> hashMap2 = new HashMap<String, Set<String>>();
        ArrayList<String> arrayList = new ArrayList<String>();
        for (String string : list) {
            if (!string.toLowerCase().startsWith("hocon:")) continue;
            object = string.substring("hocon:".length()).split("\\s*,\\s*");
            int n = ((String[])object).length;
            for (int i = 0; i < n; ++i) {
                object[i] = ConfigUtils.normalizeHoconPathElement((String)object[i]);
            }
            hashMap.put(string, Arrays.asList(object));
            for (Object object2 : object) {
                ConfigUtils.putToSet(hashMap2, (String)object2, string);
                if (((String)object2).indexOf(46) >= 0 || "/".equals(object2)) continue;
                ConfigUtils.putToSet(hashMap2, (String)object2 + ".conf", string);
                ConfigUtils.putToSet(hashMap2, (String)object2 + ".properties", string);
                ConfigUtils.putToSet(hashMap2, (String)object2 + ".json", string);
            }
        }
        for (String string : list) {
            if (string.toLowerCase().startsWith("hocon:")) {
                object = (List)hashMap.get(string);
                HashSet hashSet = new HashSet();
                Object object3 = object.iterator();
                while (object3.hasNext()) {
                    String string2 = (String)object3.next();
                    if ("/".equals(string2)) continue;
                    hashSet.addAll((Collection)hashMap2.get(string2));
                }
                object3 = new ArrayList();
                for (String string2 : list) {
                    if (!string.toLowerCase().startsWith("hocon:") || string2 == string || !hashSet.contains(string2)) continue;
                    object3.addAll((Collection)hashMap.get(string2));
                }
                object3.addAll((Collection)hashMap.get(string));
                arrayList.add(ConfigUtils.makeHoconPathFromElements((List<String>)object3));
                continue;
            }
            arrayList.add(string);
        }
        return arrayList;
    }

    private ConfigUtils() {
    }
}

