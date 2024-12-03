/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.plugin.util;

import java.math.BigInteger;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;

public class VersionStringComparator
implements Comparator<String> {
    public static final String DELIMITER_PATTERN = "[\\.-]";
    public static final String COMPONENT_PATTERN = "[\\d\\w]+";
    public static final String VALID_VERSION_PATTERN = "[\\d\\w]+(?:[\\.-][\\d\\w]+)*";
    private static final Pattern START_WITH_INT_PATTERN = Pattern.compile("(^\\d+)");
    public static final Pattern SNAPSHOT_PATTERN = Pattern.compile(".*-SNAPSHOT$");

    public static boolean isValidVersionString(String version) {
        return version != null && version.matches(VALID_VERSION_PATTERN);
    }

    public static boolean isSnapshotVersion(String version) {
        return version != null && SNAPSHOT_PATTERN.matcher(version).matches();
    }

    @Override
    public int compare(String version1, String version2) {
        String thisVersion = "0";
        if (StringUtils.isNotEmpty((CharSequence)version1)) {
            thisVersion = version1.replaceAll(" ", "");
        }
        String compareVersion = "0";
        if (StringUtils.isNotEmpty((CharSequence)version2)) {
            compareVersion = version2.replaceAll(" ", "");
        }
        if (!thisVersion.matches(VALID_VERSION_PATTERN) || !compareVersion.matches(VALID_VERSION_PATTERN)) {
            throw new IllegalArgumentException("Version number '" + thisVersion + "' cannot be compared to '" + compareVersion + "'");
        }
        String[] v1 = thisVersion.split(DELIMITER_PATTERN);
        String[] v2 = compareVersion.split(DELIMITER_PATTERN);
        VersionStringComponentComparator componentComparator = new VersionStringComponentComparator();
        for (int i = 0; i < (v1.length > v2.length ? v1.length : v2.length); ++i) {
            String component2;
            String component1 = i >= v1.length ? "0" : v1[i];
            String string = component2 = i >= v2.length ? "0" : v2[i];
            if (componentComparator.compare(component1, component2) == 0) continue;
            return componentComparator.compare(component1, component2);
        }
        return 0;
    }

    private class VersionStringComponentComparator
    implements Comparator<String> {
        public static final int FIRST_GREATER = 1;
        public static final int SECOND_GREATER = -1;

        private VersionStringComponentComparator() {
        }

        @Override
        public int compare(String component1, String component2) {
            if (component1.equalsIgnoreCase(component2)) {
                return 0;
            }
            if (this.isInteger(component1) && this.isInteger(component2)) {
                return new BigInteger(component1).compareTo(new BigInteger(component2));
            }
            BigInteger comp1BigIntPart = this.getStartingInteger(component1);
            BigInteger comp2BigIntPart = this.getStartingInteger(component2);
            if (comp1BigIntPart != null && comp2BigIntPart != null) {
                if (comp1BigIntPart.compareTo(comp2BigIntPart) > 0) {
                    return 1;
                }
                if (comp2BigIntPart.compareTo(comp1BigIntPart) > 0) {
                    return -1;
                }
            }
            if (this.isInteger(component1)) {
                return 1;
            }
            if (this.isInteger(component2)) {
                return -1;
            }
            return component1.compareToIgnoreCase(component2);
        }

        private boolean isInteger(String string) {
            return string.matches("\\d+");
        }

        private BigInteger getStartingInteger(String string) {
            Matcher matcher = START_WITH_INT_PATTERN.matcher(string);
            if (matcher.find()) {
                return new BigInteger(matcher.group(1));
            }
            return null;
        }
    }
}

