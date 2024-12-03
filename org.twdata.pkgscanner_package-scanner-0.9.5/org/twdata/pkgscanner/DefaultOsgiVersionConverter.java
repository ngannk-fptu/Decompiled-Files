/*
 * Decompiled with CFR 0.152.
 */
package org.twdata.pkgscanner;

import java.util.ArrayList;
import java.util.regex.Pattern;
import org.twdata.pkgscanner.OsgiVersionConverter;

public class DefaultOsgiVersionConverter
implements OsgiVersionConverter {
    private static final Pattern OSGI_VERSION_PATTERN = Pattern.compile("[0-9]+\\.[0-9]+\\.[0-9]+(\\.[0-9A-Za-z_-]+)?");

    public String getVersion(String version) {
        int t;
        if (OSGI_VERSION_PATTERN.matcher(version).matches()) {
            return version;
        }
        String[] osgiComponents = new String[4];
        int c = 0;
        String[] tokens = this.splitOnDelimiters(version);
        for (t = 0; t < tokens.length && c < 4 && this.isNumericComponent(tokens[t]); ++c, ++t) {
            osgiComponents[c] = tokens[t];
        }
        while (t < tokens.length) {
            if (osgiComponents[3] == null) {
                osgiComponents[3] = "";
            }
            osgiComponents[3] = osgiComponents[3] + tokens[t];
            if (t < tokens.length - 1) {
                osgiComponents[3] = osgiComponents[3] + "_";
            }
            ++t;
        }
        return this.getVersion(osgiComponents[0], osgiComponents[1], osgiComponents[2], osgiComponents[3]);
    }

    private String[] splitOnDelimiters(String version) {
        ArrayList<String> result = new ArrayList<String>(10);
        int lastDelimiter = -1;
        for (int c = 0; c < version.length(); ++c) {
            if (!this.isDelimiter(version.charAt(c))) continue;
            result.add(version.substring(lastDelimiter + 1, c));
            lastDelimiter = c;
        }
        result.add(version.substring(lastDelimiter + 1));
        return result.toArray(new String[result.size()]);
    }

    private boolean isDelimiter(char c) {
        boolean notADelimiter = c >= '0' && c <= '9' || c >= 'A' && c <= 'Z' || c >= 'a' && c <= 'z';
        return !notADelimiter;
    }

    private boolean isNumericComponent(String s) {
        if (s.length() > 4) {
            return false;
        }
        for (int c = 0; c < s.length(); ++c) {
            if (s.charAt(c) >= '0' && s.charAt(c) <= '9') continue;
            return false;
        }
        return true;
    }

    private String getVersion(String major, String minor, String service, String qualifier) {
        StringBuffer sb = new StringBuffer();
        sb.append(DefaultOsgiVersionConverter.isBlank(major) ? "0" : major);
        sb.append('.');
        sb.append(DefaultOsgiVersionConverter.isBlank(minor) ? "0" : minor);
        sb.append('.');
        sb.append(DefaultOsgiVersionConverter.isBlank(service) ? "0" : service);
        if (!DefaultOsgiVersionConverter.isBlank(qualifier)) {
            sb.append('.');
            sb.append(qualifier);
        }
        return sb.toString();
    }

    private static boolean isBlank(String str) {
        return str == null || str.length() == 0;
    }
}

