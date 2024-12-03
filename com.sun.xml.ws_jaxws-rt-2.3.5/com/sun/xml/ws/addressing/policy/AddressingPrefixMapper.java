/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.xml.ws.policy.spi.PrefixMapper
 */
package com.sun.xml.ws.addressing.policy;

import com.sun.xml.ws.api.addressing.AddressingVersion;
import com.sun.xml.ws.policy.spi.PrefixMapper;
import java.util.HashMap;
import java.util.Map;

public class AddressingPrefixMapper
implements PrefixMapper {
    private static final Map<String, String> prefixMap = new HashMap<String, String>();

    public Map<String, String> getPrefixMap() {
        return prefixMap;
    }

    static {
        prefixMap.put(AddressingVersion.MEMBER.policyNsUri, "wsap");
        prefixMap.put(AddressingVersion.MEMBER.nsUri, "wsa");
        prefixMap.put("http://www.w3.org/2007/05/addressing/metadata", "wsam");
    }
}

