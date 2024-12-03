/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.gadgets;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

public class BodyToMacroConverterImpl {
    public Map<String, String> convertToMap(String body) {
        body = body.replaceAll("[\\r\\n]", "");
        String[] strings = body.split("&");
        HashMap<String, String> map = new HashMap<String, String>();
        for (String string : strings) {
            String[] substrings = string.split("=");
            try {
                if (substrings.length != 2) continue;
                map.put(URLDecoder.decode(substrings[0].trim(), "UTF-8"), URLDecoder.decode(substrings[1].trim(), "UTF-8"));
            }
            catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }
        return map;
    }
}

