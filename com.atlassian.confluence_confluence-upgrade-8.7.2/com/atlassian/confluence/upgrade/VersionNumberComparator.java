/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.upgrade;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;

public class VersionNumberComparator
implements Comparator<String>,
Serializable {
    public static final VersionNumberComparator INSTANCE = new VersionNumberComparator();

    @Override
    public int compare(String o1, String o2) {
        int i;
        if (Objects.equals(o1, o2)) {
            return 0;
        }
        if (o1 == null && o2 != null) {
            return 1;
        }
        if (o1 != null && o2 == null) {
            return -1;
        }
        Integer[] version1 = this.splitVersion(o1);
        Integer[] version2 = this.splitVersion(o2);
        int commonSize = Math.min(version1.length, version2.length);
        for (i = 0; i < commonSize; ++i) {
            int compare = version1[i].compareTo(version2[i]);
            if (compare == 0) continue;
            return compare;
        }
        if (version1.length > commonSize) {
            for (i = commonSize; i < version1.length; ++i) {
                if (version1[i] == 0) continue;
                return 1;
            }
        }
        if (version2.length > commonSize) {
            for (i = commonSize; i < version2.length; ++i) {
                if (version2[i] == 0) continue;
                return -1;
            }
        }
        return 0;
    }

    private Integer[] splitVersion(String versionString) {
        if (StringUtils.isBlank((CharSequence)versionString)) {
            return new Integer[0];
        }
        ArrayList<Integer> version = new ArrayList<Integer>();
        for (String part : versionString.split("\\.")) {
            String number = part.split("[^0-9]")[0];
            if (number.length() <= 0) continue;
            try {
                version.add(Integer.parseInt(number));
            }
            catch (NumberFormatException numberFormatException) {
                // empty catch block
            }
        }
        return version.toArray(new Integer[version.size()]);
    }
}

