/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.osgi.framework.Bundle
 *  org.osgi.framework.Version
 *  org.springframework.util.Assert
 *  org.springframework.util.ObjectUtils
 *  org.springframework.util.StringUtils
 */
package org.eclipse.gemini.blueprint.extender.support.internal;

import java.util.Dictionary;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.Bundle;
import org.osgi.framework.Version;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

public abstract class ConfigUtils {
    private static final Log log = LogFactory.getLog(ConfigUtils.class);
    public static final String EXTENDER_VERSION = "SpringExtender-Version";
    private static final String LEFT_CLOSED_INTERVAL = "[";
    private static final String LEFT_OPEN_INTERVAL = "(";
    private static final String RIGHT_CLOSED_INTERVAL = "]";
    private static final String RIGHT_OPEN_INTERVAL = ")";
    private static final String COMMA = ",";
    public static final String CONFIG_WILDCARD = "*";
    public static final String SPRING_CONTEXT_HEADER = "Spring-Context";
    public static final String DIRECTIVE_PUBLISH_CONTEXT = "publish-context";
    public static final String DIRECTIVE_TIMEOUT = "timeout";
    public static final String DIRECTIVE_TIMEOUT_VALUE_NONE = "none";
    public static final String DIRECTIVE_CREATE_ASYNCHRONOUSLY = "create-asynchronously";
    public static final String DIRECTIVE_WAIT_FOR_DEPS = "wait-for-dependencies";
    public static final boolean DIRECTIVE_WAIT_FOR_DEPS_DEFAULT = true;
    public static final String EQUALS = ":=";
    public static final String DIRECTIVE_SEPARATOR = ";";
    public static final String CONTEXT_LOCATION_SEPARATOR = ",";
    public static final boolean DIRECTIVE_PUBLISH_CONTEXT_DEFAULT = true;
    public static final boolean DIRECTIVE_CREATE_ASYNCHRONOUSLY_DEFAULT = true;
    public static final long DIRECTIVE_TIMEOUT_DEFAULT = 300L;
    public static final long DIRECTIVE_NO_TIMEOUT = -2L;

    public static boolean matchExtenderVersionRange(Bundle bundle, String header, Version versionToMatch) {
        int commaNr;
        Assert.notNull((Object)bundle);
        String range = (String)bundle.getHeaders().get(header);
        boolean trace = log.isTraceEnabled();
        if (!StringUtils.hasText((String)range)) {
            return true;
        }
        if (trace) {
            log.trace((Object)("discovered " + header + " header w/ value=" + range));
        }
        if ((commaNr = StringUtils.countOccurrencesOf((String)(range = StringUtils.trimWhitespace((String)range)), (String)",")) == 0) {
            Version version = Version.parseVersion((String)range);
            return versionToMatch.equals((Object)version);
        }
        if (commaNr == 1) {
            if (!range.startsWith(LEFT_CLOSED_INTERVAL) && !range.startsWith(LEFT_OPEN_INTERVAL) || !range.endsWith(RIGHT_CLOSED_INTERVAL) && !range.endsWith(RIGHT_OPEN_INTERVAL)) {
                throw new IllegalArgumentException("range [" + range + "] is invalid");
            }
            boolean equalMin = range.startsWith(LEFT_CLOSED_INTERVAL);
            boolean equalMax = range.endsWith(RIGHT_CLOSED_INTERVAL);
            range = range.substring(1, range.length() - 1);
            Object[] pieces = StringUtils.split((String)range, (String)",");
            if (trace) {
                log.trace((Object)("discovered low/high versions : " + ObjectUtils.nullSafeToString((Object[])pieces)));
            }
            Version minVer = Version.parseVersion((String)pieces[0]);
            Version maxVer = Version.parseVersion((String)pieces[1]);
            if (trace) {
                log.trace((Object)("comparing version " + versionToMatch + " w/ min=" + minVer + " and max=" + maxVer));
            }
            boolean result = true;
            int compareMin = versionToMatch.compareTo(minVer);
            result = equalMin ? result && compareMin >= 0 : result && compareMin > 0;
            int compareMax = versionToMatch.compareTo(maxVer);
            result = equalMax ? result && compareMax <= 0 : result && compareMax < 0;
            return result;
        }
        throw new IllegalArgumentException("range [" + range + "] is invalid");
    }

    public static String getSpringContextHeader(Dictionary headers) {
        Object header = null;
        if (headers != null) {
            header = headers.get(SPRING_CONTEXT_HEADER);
        }
        return header != null ? header.toString().trim() : null;
    }

    public static String getDirectiveValue(String header, String directive) {
        Assert.notNull((Object)header, (String)"not-null header required");
        Assert.notNull((Object)directive, (String)"not-null directive required");
        String[] directives = StringUtils.tokenizeToStringArray((String)header, (String)DIRECTIVE_SEPARATOR);
        for (int i = 0; i < directives.length; ++i) {
            String[] splittedDirective = StringUtils.delimitedListToStringArray((String)directives[i].trim(), (String)EQUALS);
            if (splittedDirective.length != 2 || !splittedDirective[0].equals(directive)) continue;
            return splittedDirective[1];
        }
        return null;
    }

    private static String getDirectiveValue(Dictionary headers, String directiveName) {
        String directive;
        String header = ConfigUtils.getSpringContextHeader(headers);
        if (header != null && (directive = ConfigUtils.getDirectiveValue(header, directiveName)) != null) {
            return directive;
        }
        return null;
    }

    public static boolean isDirectiveDefined(Dictionary headers, String directiveName) {
        String header = ConfigUtils.getSpringContextHeader(headers);
        if (header != null) {
            String directive = ConfigUtils.getDirectiveValue(header, directiveName);
            return directive != null;
        }
        return false;
    }

    public static boolean getPublishContext(Dictionary headers) {
        String value = ConfigUtils.getDirectiveValue(headers, DIRECTIVE_PUBLISH_CONTEXT);
        return value != null ? Boolean.valueOf(value) : true;
    }

    public static boolean getCreateAsync(Dictionary headers) {
        String value = ConfigUtils.getDirectiveValue(headers, DIRECTIVE_CREATE_ASYNCHRONOUSLY);
        return value != null ? Boolean.valueOf(value) : true;
    }

    public static long getTimeOut(Dictionary headers) {
        String value = ConfigUtils.getDirectiveValue(headers, DIRECTIVE_TIMEOUT);
        if (value != null) {
            if (DIRECTIVE_TIMEOUT_VALUE_NONE.equalsIgnoreCase(value)) {
                return -2L;
            }
            return Long.valueOf(value);
        }
        return 300L;
    }

    public static boolean getWaitForDependencies(Dictionary headers) {
        String value = ConfigUtils.getDirectiveValue(headers, DIRECTIVE_WAIT_FOR_DEPS);
        return value != null ? Boolean.valueOf(value) : true;
    }

    public static String[] getHeaderLocations(Dictionary headers) {
        return ConfigUtils.getLocationsFromHeader(ConfigUtils.getSpringContextHeader(headers), "osgibundle:/META-INF/spring/*.xml");
    }

    public static String[] getLocationsFromHeader(String header, String defaultValue) {
        String[] ctxEntries;
        if (StringUtils.hasText((String)header) && ';' != header.charAt(0)) {
            String locations = StringUtils.tokenizeToStringArray((String)header, (String)DIRECTIVE_SEPARATOR)[0];
            ctxEntries = StringUtils.tokenizeToStringArray((String)locations, (String)",");
            for (int i = 0; i < ctxEntries.length; ++i) {
                if (!CONFIG_WILDCARD.equals(ctxEntries[i])) continue;
                ctxEntries[i] = defaultValue;
            }
        } else {
            ctxEntries = new String[]{};
        }
        return ctxEntries;
    }
}

