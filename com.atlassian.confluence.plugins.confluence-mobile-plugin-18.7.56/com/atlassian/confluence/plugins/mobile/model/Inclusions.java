/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.plugins.mobile.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class Inclusions {
    private List<String> inclusions;

    public Inclusions(String value) {
        this.inclusions = StringUtils.isBlank((CharSequence)value) ? new ArrayList() : Arrays.asList(value.split(","));
    }

    public boolean isInclude(String value) {
        return this.inclusions.contains(value);
    }

    public boolean isOnlyInclude(String value) {
        return this.isInclude(value) && this.inclusions.size() == 1;
    }

    public boolean isEmpty() {
        return this.inclusions.isEmpty();
    }
}

