/*
 * Decompiled with CFR 0.152.
 */
package org.apache.felix.framework;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.osgi.framework.Version;

public class VersionConverter {
    private static final Pattern FUZZY_VERSION = Pattern.compile("(\\d+)(\\.(\\d+)(\\.(\\d+))?)?([^a-zA-Z0-9](.*))?", 32);

    public static Version toOsgiVersion(String value) throws IllegalArgumentException {
        return new Version(VersionConverter.cleanupVersion(value));
    }

    private static String cleanupVersion(String version) {
        StringBuilder result = new StringBuilder();
        Matcher m = FUZZY_VERSION.matcher(version);
        if (m.matches()) {
            String major = m.group(1);
            String minor = m.group(3);
            String micro = m.group(5);
            String qualifier = m.group(7);
            if (major != null) {
                result.append(major);
                if (minor != null) {
                    result.append(".");
                    result.append(minor);
                    if (micro != null) {
                        result.append(".");
                        result.append(micro);
                        if (qualifier != null && !qualifier.isEmpty()) {
                            result.append(".");
                            VersionConverter.cleanupModifier(result, qualifier);
                        }
                    } else if (qualifier != null && !qualifier.isEmpty()) {
                        result.append(".0.");
                        VersionConverter.cleanupModifier(result, qualifier);
                    } else {
                        result.append(".0");
                    }
                } else if (qualifier != null && !qualifier.isEmpty()) {
                    result.append(".0.0.");
                    VersionConverter.cleanupModifier(result, qualifier);
                } else {
                    result.append(".0.0");
                }
            }
        } else {
            result.append("0.0.0.");
            VersionConverter.cleanupModifier(result, version);
        }
        return result.toString();
    }

    private static void cleanupModifier(StringBuilder result, String modifier) {
        for (int i = 0; i < modifier.length(); ++i) {
            char c = modifier.charAt(i);
            if (c >= '0' && c <= '9' || c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z' || c == '_' || c == '-') {
                result.append(c);
                continue;
            }
            result.append('_');
        }
    }
}

