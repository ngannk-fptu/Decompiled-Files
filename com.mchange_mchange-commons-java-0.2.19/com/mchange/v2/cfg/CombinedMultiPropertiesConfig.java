/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.cfg;

import com.mchange.v2.cfg.BasicMultiPropertiesConfig;
import com.mchange.v2.cfg.MultiPropertiesConfig;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

class CombinedMultiPropertiesConfig
extends MultiPropertiesConfig {
    MultiPropertiesConfig[] configs;
    String[] resourcePaths;
    List parseMessages;

    CombinedMultiPropertiesConfig(MultiPropertiesConfig[] multiPropertiesConfigArray) {
        int n;
        this.configs = multiPropertiesConfigArray;
        LinkedList<String> linkedList = new LinkedList<String>();
        for (int i = multiPropertiesConfigArray.length - 1; i >= 0; --i) {
            String[] stringArray = multiPropertiesConfigArray[i].getPropertiesResourcePaths();
            for (n = stringArray.length - 1; n >= 0; --n) {
                String string = stringArray[n];
                if (linkedList.contains(string)) continue;
                linkedList.add(0, string);
            }
        }
        this.resourcePaths = linkedList.toArray(new String[linkedList.size()]);
        LinkedList linkedList2 = new LinkedList();
        n = multiPropertiesConfigArray.length;
        for (int i = 0; i < n; ++i) {
            linkedList2.addAll(multiPropertiesConfigArray[i].getDelayedLogItems());
        }
        this.parseMessages = Collections.unmodifiableList(linkedList2);
    }

    private Map getPropsByResourcePaths() {
        HashMap<String, Properties> hashMap = new HashMap<String, Properties>();
        for (String string : this.resourcePaths) {
            hashMap.put(string, this.getPropertiesByResourcePath(string));
        }
        return Collections.unmodifiableMap(hashMap);
    }

    public BasicMultiPropertiesConfig toBasic() {
        String[] stringArray = this.getPropertiesResourcePaths();
        Map map = this.getPropsByResourcePaths();
        List list = this.getDelayedLogItems();
        return new BasicMultiPropertiesConfig(stringArray, map, list);
    }

    @Override
    public String[] getPropertiesResourcePaths() {
        return (String[])this.resourcePaths.clone();
    }

    @Override
    public Properties getPropertiesByResourcePath(String string) {
        Properties properties = new Properties();
        for (MultiPropertiesConfig multiPropertiesConfig : this.configs) {
            Properties properties2 = multiPropertiesConfig.getPropertiesByResourcePath(string);
            if (properties2 == null) continue;
            properties.putAll((Map<?, ?>)properties2);
        }
        return properties.size() > 0 ? properties : null;
    }

    @Override
    public Properties getPropertiesByPrefix(String string) {
        LinkedList<Map.Entry<Object, Object>> linkedList = new LinkedList<Map.Entry<Object, Object>>();
        for (int i = this.configs.length - 1; i >= 0; --i) {
            MultiPropertiesConfig multiPropertiesConfig = this.configs[i];
            Properties object = multiPropertiesConfig.getPropertiesByPrefix(string);
            if (object == null) continue;
            linkedList.addAll(0, object.entrySet());
        }
        if (linkedList.size() == 0) {
            return null;
        }
        Properties properties = new Properties();
        for (Map.Entry entry : linkedList) {
            properties.put(entry.getKey(), entry.getValue());
        }
        return properties;
    }

    @Override
    public String getProperty(String string) {
        for (int i = this.configs.length - 1; i >= 0; --i) {
            MultiPropertiesConfig multiPropertiesConfig = this.configs[i];
            String string2 = multiPropertiesConfig.getProperty(string);
            if (string2 == null) continue;
            return string2;
        }
        return null;
    }

    @Override
    public List getDelayedLogItems() {
        return this.parseMessages;
    }
}

