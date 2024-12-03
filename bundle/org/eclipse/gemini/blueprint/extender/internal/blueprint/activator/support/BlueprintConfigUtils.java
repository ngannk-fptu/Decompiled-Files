/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.ObjectUtils
 *  org.springframework.util.StringUtils
 */
package org.eclipse.gemini.blueprint.extender.internal.blueprint.activator.support;

import java.util.ArrayList;
import java.util.Dictionary;
import org.eclipse.gemini.blueprint.extender.support.internal.ConfigUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

public class BlueprintConfigUtils {
    private static final String EQUALS = "=";
    private static final String SEMI_COLON = ";";
    private static final String COMMA = ",";
    public static final String BLUEPRINT_HEADER = "Bundle-Blueprint";
    public static final String BLUEPRINT_GRACE_PERIOD = "blueprint.graceperiod";
    public static final String BLUEPRINT_TIMEOUT = "blueprint.timeout";
    public static final String EXTENDER_VERSION = "BlueprintExtender-Version";

    public static String getBlueprintHeader(Dictionary headers) {
        Object header = null;
        if (headers != null) {
            header = headers.get(BLUEPRINT_HEADER);
        }
        return header != null ? header.toString().trim() : null;
    }

    public static String getSymNameHeader(Dictionary headers) {
        Object header = null;
        if (headers != null) {
            header = headers.get("Bundle-SymbolicName");
        }
        return header != null ? header.toString().trim() : null;
    }

    private static String getDirectiveValue(Dictionary headers, String directiveName) {
        String directive;
        String header = BlueprintConfigUtils.getBlueprintHeader(headers);
        if (header != null && (directive = ConfigUtils.getDirectiveValue(header, directiveName)) != null) {
            return directive;
        }
        return null;
    }

    private static String getBlueprintDirectiveValue(Dictionary headers, String directiveName) {
        String directive;
        String header = BlueprintConfigUtils.getSymNameHeader(headers);
        if (header != null && (directive = ConfigUtils.getDirectiveValue(header, directiveName)) != null) {
            return directive;
        }
        return null;
    }

    public static boolean hasTimeout(Dictionary headers) {
        String header = BlueprintConfigUtils.getSymNameHeader(headers);
        if (header != null) {
            return StringUtils.hasText((String)ConfigUtils.getDirectiveValue(header, BLUEPRINT_TIMEOUT));
        }
        return false;
    }

    public static long getTimeOut(Dictionary headers) {
        String value = BlueprintConfigUtils.getBlueprintDirectiveValue(headers, BLUEPRINT_TIMEOUT);
        if (value != null) {
            if ("none".equalsIgnoreCase(value)) {
                return -2L;
            }
            return Long.valueOf(value);
        }
        return 300000L;
    }

    public static boolean getWaitForDependencies(Dictionary headers) {
        String value = BlueprintConfigUtils.getBlueprintDirectiveValue(headers, BLUEPRINT_GRACE_PERIOD);
        return value != null ? Boolean.valueOf(value) : true;
    }

    public static boolean getPublishContext(Dictionary headers) {
        String value = BlueprintConfigUtils.getDirectiveValue(headers, "publish-context");
        return value != null ? Boolean.valueOf(value) : true;
    }

    public static boolean getCreateAsync(Dictionary headers) {
        String value = BlueprintConfigUtils.getDirectiveValue(headers, "create-asynchronously");
        return value != null ? Boolean.valueOf(value) : true;
    }

    public static String[] getBlueprintHeaderLocations(Dictionary headers) {
        String header = BlueprintConfigUtils.getBlueprintHeader(headers);
        if (header == null) {
            return null;
        }
        if (header.length() == 0) {
            return new String[0];
        }
        ArrayList<String> ctxEntries = new ArrayList<String>(4);
        if (StringUtils.hasText((String)header)) {
            String[] clauses;
            for (String clause : clauses = header.split(COMMA)) {
                Object[] directives = clause.split(SEMI_COLON);
                if (ObjectUtils.isEmpty((Object[])directives)) continue;
                for (Object directive : directives) {
                    if (((String)directive).contains(EQUALS)) continue;
                    ctxEntries.add(((String)directive).trim());
                }
            }
        }
        for (int i = 0; i < ctxEntries.size(); ++i) {
            String ctxEntry = (String)ctxEntries.get(i);
            if (!"*".equals(ctxEntry)) continue;
            ctxEntry = "osgibundle:/META-INF/spring/*.xml";
        }
        return ctxEntries.toArray(new String[ctxEntries.size()]);
    }
}

