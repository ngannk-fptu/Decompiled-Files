/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.plugin.webresource;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;

public class ResourceUtils {
    public static final String STATIC_HASH = "_statichash";
    public static final String WRM_INTEGRITY = "_wrm-integrity";
    private static final List<String> HTTP_ATTRIBUTE_AS_PARAMS = Arrays.asList("_statichash", "_wrm-integrity");

    private ResourceUtils() {
    }

    public static String getType(@Nonnull String path) {
        int index = path.lastIndexOf(46);
        if (index > -1 && index < path.length()) {
            return path.substring(index + 1).toLowerCase();
        }
        return "";
    }

    public static String getBasename(@Nonnull String path) {
        int index = path.lastIndexOf(46);
        if (index > -1 && index < path.length()) {
            return path.substring(0, index);
        }
        return path;
    }

    public static Map<String, String> getQueryParameters(HttpServletRequest request) {
        TreeMap<String, String> result = new TreeMap<String, String>();
        request.getParameterMap().forEach((key, values) -> {
            if (values != null && ((String[])values).length > 0) {
                result.put((String)key, values[0]);
            }
        });
        HTTP_ATTRIBUTE_AS_PARAMS.stream().filter(attr -> request.getAttribute(attr) != null).forEach(attr -> result.put((String)attr, request.getAttribute(attr).toString()));
        return result;
    }

    public static boolean canRequestedResourcesContentBeAssumedConstant(Map<String, String> params) {
        boolean nocache = "false".equals(params.get("cache"));
        boolean nohash = !params.containsKey(STATIC_HASH);
        return !nohash && !nocache;
    }

    public static boolean shouldValidateRequest(Map<String, String> params) {
        String val = params.get(WRM_INTEGRITY);
        if (StringUtils.isNotEmpty((CharSequence)val)) {
            return !"no-validate".equals(val);
        }
        return true;
    }
}

