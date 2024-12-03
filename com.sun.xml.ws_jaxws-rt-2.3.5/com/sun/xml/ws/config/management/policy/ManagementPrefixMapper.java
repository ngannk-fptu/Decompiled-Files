/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.xml.ws.policy.spi.PrefixMapper
 */
package com.sun.xml.ws.config.management.policy;

import com.sun.xml.ws.policy.spi.PrefixMapper;
import java.util.HashMap;
import java.util.Map;

public class ManagementPrefixMapper
implements PrefixMapper {
    private static final Map<String, String> prefixMap = new HashMap<String, String>();

    public Map<String, String> getPrefixMap() {
        return prefixMap;
    }

    static {
        prefixMap.put("http://java.sun.com/xml/ns/metro/management", "sunman");
    }
}

