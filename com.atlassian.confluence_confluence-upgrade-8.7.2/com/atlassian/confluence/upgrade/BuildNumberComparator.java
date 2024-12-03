/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.upgrade;

import java.io.Serializable;
import java.util.Comparator;
import org.apache.commons.lang3.StringUtils;

public class BuildNumberComparator
implements Comparator<String>,
Serializable {
    @Override
    public int compare(String o1, String o2) {
        Integer number1 = this.getBuildNumber(o1);
        Integer number2 = this.getBuildNumber(o2);
        if (number1 == null && number2 == null) {
            return 0;
        }
        if (number1 == null) {
            return 1;
        }
        if (number2 == null) {
            return -1;
        }
        return number1.compareTo(number2);
    }

    private Integer getBuildNumber(String buildNumberString) {
        if (StringUtils.isBlank((CharSequence)buildNumberString)) {
            return null;
        }
        try {
            return Integer.valueOf(buildNumberString);
        }
        catch (NumberFormatException e) {
            return null;
        }
    }
}

