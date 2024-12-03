/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.cfg;

import com.mchange.v2.cfg.BasicPropertiesConfigSource;
import com.mchange.v2.cfg.DelayedLogItem;
import com.mchange.v2.cfg.MultiPropertiesConfig;
import com.mchange.v2.cfg.PropertiesConfigSource;
import com.mchange.v3.hocon.HoconPropertiesConfigSource;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

final class BasicMultiPropertiesConfig
extends MultiPropertiesConfig {
    private static final String HOCON_CFG_CNAME = "com.typesafe.config.Config";
    private static final int HOCON_PFX_LEN = 6;
    static final BasicMultiPropertiesConfig EMPTY = new BasicMultiPropertiesConfig();
    String[] rps;
    Map propsByResourcePaths;
    Map propsByPrefixes;
    List parseMessages;
    Properties propsByKey;

    static boolean isHoconPath(String string) {
        return string.length() > 6 && string.substring(0, 6).toLowerCase().equals("hocon:");
    }

    private static PropertiesConfigSource configSource(String string) throws Exception {
        boolean bl = BasicMultiPropertiesConfig.isHoconPath(string);
        if (!bl && !string.startsWith("/")) {
            throw new IllegalArgumentException(String.format("Resource identifier '%s' is neither an absolute resource path nor a HOCON path. (Resource paths should be specified beginning with '/' or 'hocon:/')", string));
        }
        if (bl) {
            try {
                Class.forName(HOCON_CFG_CNAME);
                return new HoconPropertiesConfigSource();
            }
            catch (ClassNotFoundException classNotFoundException) {
                String string2;
                int n = string.lastIndexOf(35);
                String string3 = string2 = n > 0 ? string.substring(6, n) : string.substring(6);
                if (BasicMultiPropertiesConfig.class.getResource(string2) == null) {
                    throw new FileNotFoundException(String.format("HOCON lib (typesafe-config) is not available. Also, no resource available at '%s' for HOCON identifier '%s'.", string2, string));
                }
                throw new Exception(String.format("Could not decode HOCON resource '%s', even though the resource exists, because HOCON lib (typesafe-config) is not available.", string), classNotFoundException);
            }
        }
        if ("/".equals(string)) {
            return new SystemPropertiesConfigSource();
        }
        return new BasicPropertiesConfigSource();
    }

    public BasicMultiPropertiesConfig(String[] stringArray) {
        this(stringArray, null);
    }

    BasicMultiPropertiesConfig(String[] stringArray, List list) {
        this.firstInit(stringArray, list);
        this.finishInit(list);
    }

    public BasicMultiPropertiesConfig(String string, Properties properties) {
        this(new String[]{string}, BasicMultiPropertiesConfig.resourcePathToPropertiesMap(string, properties), Collections.emptyList());
    }

    private static Map resourcePathToPropertiesMap(String string, Properties properties) {
        HashMap<String, Properties> hashMap = new HashMap<String, Properties>();
        hashMap.put(string, properties);
        return hashMap;
    }

    BasicMultiPropertiesConfig(String[] stringArray, Map map, List list) {
        this.rps = stringArray;
        this.propsByResourcePaths = map;
        ArrayList arrayList = new ArrayList();
        arrayList.addAll(list);
        this.finishInit(arrayList);
        this.parseMessages = arrayList;
    }

    private BasicMultiPropertiesConfig() {
        this.rps = new String[0];
        Map map = Collections.emptyMap();
        Map map2 = Collections.emptyMap();
        List list = Collections.emptyList();
        Properties properties = new Properties();
    }

    private void firstInit(String[] stringArray, List arrayList) {
        boolean bl = false;
        if (arrayList == null) {
            arrayList = new ArrayList<DelayedLogItem>();
            bl = true;
        }
        HashMap<String, Properties> hashMap = new HashMap<String, Properties>();
        ArrayList<String> arrayList2 = new ArrayList<String>();
        for (String string : stringArray) {
            try {
                PropertiesConfigSource propertiesConfigSource = BasicMultiPropertiesConfig.configSource(string);
                PropertiesConfigSource.Parse parse = propertiesConfigSource.propertiesFromSource(string);
                hashMap.put(string, parse.getProperties());
                arrayList2.add(string);
                arrayList.addAll(parse.getDelayedLogItems());
            }
            catch (FileNotFoundException fileNotFoundException) {
                arrayList.add(new DelayedLogItem(DelayedLogItem.Level.FINE, String.format("The configuration file for resource identifier '%s' could not be found. Skipping.", string)));
            }
            catch (Exception exception) {
                arrayList.add(new DelayedLogItem(DelayedLogItem.Level.WARNING, String.format("An Exception occurred while trying to read configuration data at resource identifier '%s'.", string), exception));
            }
        }
        this.rps = arrayList2.toArray(new String[arrayList2.size()]);
        this.propsByResourcePaths = Collections.unmodifiableMap(hashMap);
        this.parseMessages = Collections.unmodifiableList(arrayList);
        if (bl) {
            BasicMultiPropertiesConfig.dumpToSysErr(arrayList);
        }
    }

    private void finishInit(List arrayList) {
        boolean bl = false;
        if (arrayList == null) {
            arrayList = new ArrayList();
            bl = true;
        }
        this.propsByPrefixes = Collections.unmodifiableMap(BasicMultiPropertiesConfig.extractPrefixMapFromRsrcPathMap(this.rps, this.propsByResourcePaths, arrayList));
        this.propsByKey = BasicMultiPropertiesConfig.extractPropsByKey(this.rps, this.propsByResourcePaths, arrayList);
        if (bl) {
            BasicMultiPropertiesConfig.dumpToSysErr(arrayList);
        }
    }

    @Override
    public List getDelayedLogItems() {
        return this.parseMessages;
    }

    private static void dumpToSysErr(List list) {
        for (Object e : list) {
            System.err.println(e);
        }
    }

    private static String extractPrefix(String string) {
        int n = string.lastIndexOf(46);
        if (n < 0) {
            if ("".equals(string)) {
                return null;
            }
            return "";
        }
        return string.substring(0, n);
    }

    private static Properties findProps(String string, Map map) {
        Properties properties = (Properties)map.get(string);
        return properties;
    }

    private static Properties extractPropsByKey(String[] stringArray, Map map, List list) {
        Properties properties = new Properties();
        for (String string : stringArray) {
            Properties properties2 = BasicMultiPropertiesConfig.findProps(string, map);
            if (properties2 == null) {
                list.add(new DelayedLogItem(DelayedLogItem.Level.WARNING, BasicMultiPropertiesConfig.class.getName() + ".extractPropsByKey(): Could not find loaded properties for resource path: " + string));
                continue;
            }
            for (Object object : properties2.keySet()) {
                String string2;
                Object object2;
                if (!(object instanceof String)) {
                    object2 = BasicMultiPropertiesConfig.class.getName() + ": Properties object found at resource path " + ("/".equals(string) ? "[system properties]" : "'" + string + "'") + "' contains a key that is not a String: " + object + "; Skipping...";
                    list.add(new DelayedLogItem(DelayedLogItem.Level.WARNING, (String)object2));
                    continue;
                }
                object2 = properties2.get(object);
                if (object2 != null && !(object2 instanceof String)) {
                    string2 = BasicMultiPropertiesConfig.class.getName() + ": Properties object found at resource path " + ("/".equals(string) ? "[system properties]" : "'" + string + "'") + " contains a value that is not a String: " + object2 + "; Skipping...";
                    list.add(new DelayedLogItem(DelayedLogItem.Level.WARNING, string2));
                    continue;
                }
                string2 = (String)object;
                String string3 = (String)object2;
                properties.put(string2, string3);
            }
        }
        return properties;
    }

    private static Map extractPrefixMapFromRsrcPathMap(String[] stringArray, Map map, List list) {
        HashMap<String, Properties> hashMap = new HashMap<String, Properties>();
        for (String string : stringArray) {
            Properties properties = BasicMultiPropertiesConfig.findProps(string, map);
            if (properties == null) {
                String string2 = BasicMultiPropertiesConfig.class.getName() + ".extractPrefixMapFromRsrcPathMap(): Could not find loaded properties for resource path: " + string;
                list.add(new DelayedLogItem(DelayedLogItem.Level.WARNING, string2));
                continue;
            }
            for (Object object : properties.keySet()) {
                String string3;
                if (!(object instanceof String)) {
                    string3 = BasicMultiPropertiesConfig.class.getName() + ": Properties object found at resource path " + ("/".equals(string) ? "[system properties]" : "'" + string + "'") + "' contains a key that is not a String: " + object + "; Skipping...";
                    list.add(new DelayedLogItem(DelayedLogItem.Level.WARNING, string3));
                    continue;
                }
                string3 = (String)object;
                String string4 = BasicMultiPropertiesConfig.extractPrefix(string3);
                while (string4 != null) {
                    Properties properties2 = (Properties)hashMap.get(string4);
                    if (properties2 == null) {
                        properties2 = new Properties();
                        hashMap.put(string4, properties2);
                    }
                    properties2.put(string3, properties.get(string3));
                    string4 = BasicMultiPropertiesConfig.extractPrefix(string4);
                }
            }
        }
        return hashMap;
    }

    @Override
    public String[] getPropertiesResourcePaths() {
        return (String[])this.rps.clone();
    }

    @Override
    public Properties getPropertiesByResourcePath(String string) {
        Properties properties = (Properties)this.propsByResourcePaths.get(string);
        return properties == null ? new Properties() : properties;
    }

    @Override
    public Properties getPropertiesByPrefix(String string) {
        Properties properties = (Properties)this.propsByPrefixes.get(string);
        return properties == null ? new Properties() : properties;
    }

    @Override
    public String getProperty(String string) {
        return this.propsByKey.getProperty(string);
    }

    public String dump() {
        return String.format("[ propertiesByResourcePaths -> %s, propertiesByPrefixes -> %s ]", this.propsByResourcePaths, this.propsByPrefixes);
    }

    public String toString() {
        return super.toString() + " " + this.dump();
    }

    static final class SystemPropertiesConfigSource
    implements PropertiesConfigSource {
        SystemPropertiesConfigSource() {
        }

        @Override
        public PropertiesConfigSource.Parse propertiesFromSource(String string) throws FileNotFoundException, Exception {
            if ("/".equals(string)) {
                return new PropertiesConfigSource.Parse((Properties)System.getProperties().clone(), Collections.<DelayedLogItem>emptyList());
            }
            throw new Exception(String.format("Unexpected identifier for System properties: '%s'", string));
        }
    }
}

